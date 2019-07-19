package com.aimir.schedule.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.mvm.impl.MeteringDataEMDaoImpl;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.MeteringDataEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.MeteringDataEM;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.Operator;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.Condition.Restriction;

/**
 * 전월 마지막 지침값을 가지고 월정산
 * GPRS 모뎀용 월정산으로 먼저 계산하고 추후에 모든 미터에 대해서 마지막 지침값을 이용하도록 변경할 수 있다.
 * @author elevas
 *
 */
@Transactional
@Service
public class ECGBillingMonthlyV2Task extends ScheduleTask {
	
	private static Log log = LogFactory.getLog(ECGBillingMonthlyV2Task.class);
    
    @Autowired
    DayEMDao dayEMDao;
    
    @Autowired
    MonthEMDao monthEMDao;
	
    @Autowired
    ContractDao contractDao;

    @Autowired
    TariffEMDao tariffEmDao;
    
    @Autowired
    PrepaymentLogDao prepaymentLogDao;
    
    @Autowired
    OperatorDao operatorDao;
    
    @Autowired
    CustomerDao customerDao;
    
    @Autowired
    TariffTypeDao tariffTypeDao;
    
    @Autowired
    MeteringDataEMDao meteringDataEMDao;
	
	TransactionStatus txStatus = null;
	private boolean isNowRunning = false;
	
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-billingmonthlyv2.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        ECGBillingMonthlyV2Task task = ctx.getBean(ECGBillingMonthlyV2Task.class);
        try {
            task.execute(args[0]);
        }
        catch (Exception e) {
            log.error(e, e);
        }
        System.exit(0);
    }
	
    @Transactional
    public void execute(String yyyymm) throws Exception {
        log.info("###### Start Monthly Additional Billing [" + yyyymm + "] ######");
        
        List<Contract> list = getContracts();
        // 월정산을 실행한 날짜시간
        String lastTokenDate = DateTimeUtil.getDateString(new Date());
        
        Operator operator = operatorDao.getOperatorByLoginId("admin");
        
        int index = 0;
        int size = list.size();
        
        String tariffName = "";
        
        for ( Contract contract : list  ) {
            try {
                calculateByContract(contract.getId(), ++index, size, index, 
                        yyyymm+"31", tariffName, yyyymm, lastTokenDate, operator);
            } catch (Exception e){
                log.error("Contract ID: " + contract.getId());
                log.error(e, e);
            }
            
        }
        log.info("###### End Monthly Additional Billing ######");
    }
    
	@Override
	public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### ECGBillingMonthlyV2Task is already running...");
			return;
		}
		isNowRunning = true;
		
    	String now = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

		String yyyymm = "";
		try {
			yyyymm = DateTimeUtil.getPreDay(now, 28).substring(0, 6);
			execute(yyyymm);
		} catch (Exception e) {
			log.error(e, e);
		}
		
        isNowRunning = false;
	}
	
	/*
	 * 모뎀이 GPRS(MMIU) 이면서 선불 고객
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	private List<Contract> getContracts() {
	    Set<Condition> condition = new HashSet<Condition>();
	    condition.add(new Condition("meter", new Object[]{"meter"}, null, Restriction.ALIAS));
	    condition.add(new Condition("meter.modem", new Object[]{"modem"}, null, Restriction.ALIAS));
	    condition.add(new Condition("modem.modemType", new Object[]{ModemType.MMIU}, null, Restriction.EQ));
	    condition.add(new Condition("tariffIndex", new Object[]{"tariffType"}, null, Restriction.ALIAS));
	    condition.add(new Condition("tariffType.name", new Object[]{"Residential", "Non Residential"}, null, Restriction.IN));
	    
	    return contractDao.findByConditions(condition);
	}
	
	public Double blockBill(String tariffName, List<TariffEM> tariffEMList, double usage) {
	    ECGBlockDailyEMBillingInfoSaveV2Task task = new ECGBlockDailyEMBillingInfoSaveV2Task();
	    return task.blockBill(tariffName, tariffEMList, usage);
	}
    
    private TariffEM getTariffByUsage(List<TariffEM> tariffList, Double usage) {
    	TariffEM result = null;
    	
    	for ( TariffEM tariff : tariffList ) {
    		Double min = tariff.getSupplySizeMin();
    		Double max = tariff.getSupplySizeMax();
    		
    		if ( min == null && max == null ) {
    			continue;
    		}
    		
    		if (( min == null && usage <= max )|| (min == 0 && usage <=max)) {
    			result =  tariff;
    		} else if ( max == null && usage > min ) {
    			result =  tariff;
    		} else if ( usage > min && usage <= max ) {
    			result =  tariff;
    		} 
    	}
    	return result;
    }
    
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    private void calculateByContract(Integer contractId, 
    		int index,
    		int size, 
    		int proccessRate, 
    		String tariffDay, 
    		String tariffName,
    		String yyyymm,
    		String lastTokenDate,
    		Operator operator) {
    	
    	log.info("###" + index +"/" + size +"###");
    	Map<String ,Object> param = new HashMap<String, Object>();
    	
    	Customer customer = null;
    	Meter meter = null;

    	Contract contract = contractDao.get(contractId);
    	
		customer = customerDao.get(contract.getCustomerId());
		meter = contract.getMeter();
		
		if ( customer == null || meter == null ) {
			return;
		}
		
		// 이전 월정산을 롤백한다.
        rollbackMonthlyAdjustedLog(contract, lastTokenDate);
		
    	param.put("tariffIndex", contract.getTariffIndex());
    	param.put("searchDate", tariffDay);
    	
    	TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId());
		tariffName = tariffType.getName();
    	
    	List<TariffEM> tariffList = tariffEmDao.getApplyedTariff(param);
    	if ( tariffList == null || tariffList.size() < 1 ) {
    		return;
    	}
    	
    	Double totalAmount = 0d;
    	Double totalUsage = 0d;
    	
    	totalUsage = StringUtil.nullToDoubleZero(getUsage(meter.getMdsId(), yyyymm));
    	TariffEM levyTariff = getTariffByUsage(tariffList, totalUsage);
    	if (levyTariff == null) {
    		log.info("skip \n" +
    				"totalUsage: " + totalUsage +
    				"tariffId: " + contract.getTariffIndexId());
    	}
    	Double govLevy = StringUtil.nullToDoubleZero(levyTariff.getTransmissionNetworkCharge()) * totalUsage;
    	Double publicLevy = StringUtil.nullToDoubleZero(levyTariff.getDistributionNetworkCharge()) * totalUsage;
    	totalAmount = blockBill(tariffName, tariffList, totalUsage);
    	Double serviceCharge = StringUtil.nullToDoubleZero(levyTariff.getServiceCharge());
    	Double vat = StringUtil.nullToDoubleZero(levyTariff.getEnergyDemandCharge()) * ( totalAmount + serviceCharge ); 
    	Double govSubsidy = StringUtil.nullToDoubleZero(levyTariff.getAdminCharge()) * totalUsage;
    	Double lifeLineSubsidy = StringUtil.nullToDoubleZero(levyTariff.getReactiveEnergyCharge());
    	
    	//사용량이 0이면 lifeLineSubsidy는 없다.
    	if("Residential".equals(contract.getTariffIndex().getName()) && totalUsage == 0) {
    	    lifeLineSubsidy = 0d;
    	}
    	
    	//사용량이 150이상이면 govSubsidy는 없다.
    	if("Residential".equals(contract.getTariffIndex().getName()) && totalUsage > 150) {
    	    govSubsidy = 0d;
    	}

    	Double paidAmount = prepaymentLogDao.getMonthlyPaidAmount(contract, yyyymm);
    	
    	ECGBlockDailyEMBillingInfoSaveV2Task task = new ECGBlockDailyEMBillingInfoSaveV2Task();
        double[] blockNewSubsidy = task.blockNewSubsidy(contract.getTariffIndex().getName(), tariffList, totalUsage);
    	
        /*
    	Double vatOnSubsidyRate = StringUtil.nullToDoubleZero(levyTariff.getEnergyDemandCharge()) 
    			* StringUtil.nullToDoubleZero(levyTariff.getRateRebalancingLevy());
    	vatOnSubsidyRate = Double.parseDouble(String.format("%.4f", vatOnSubsidyRate));
    	
    	Double vatOnSubsidy = vatOnSubsidyRate * totalUsage;
    	Double newSubsidyRate = (StringUtil.nullToDoubleZero(vatOnSubsidyRate) 
    			+ StringUtil.nullToDoubleZero(levyTariff.getRateRebalancingLevy()));
    	Double newSubsidy = newSubsidyRate * totalUsage;
    	*/
        double newSubsidy = blockNewSubsidy[0];
        double vatOnSubsidy = blockNewSubsidy[1];
    	
    	Double additionalAmount = totalAmount - paidAmount;
    	
    	// subsidy는 모든 subsidy의 합으로 계산한다.
    	Double subsidy = govSubsidy + lifeLineSubsidy + newSubsidy;
    	Double levy = publicLevy + govLevy;
    	
    	// 2015.04.08 월정산중에 충전을 시도하면 충전금액이 반영되지 않을 수 있다.
    	// 최근 잔액을 가져오도록 한다.
    	contract = contractDao.get(contract.getId());
    	
    	Double beforeCredit = StringUtil.nullToDoubleZero(contract.getCurrentCredit());
    	Double currentCredit =  beforeCredit - additionalAmount - serviceCharge - levy - vat + subsidy;    	
    	
    	log.debug("\n=== Contract Number: " + contract.getContractNumber() + " ==="
    	+ "\n Before Credit: " + StringUtil.nullToDoubleZero(contract.getCurrentCredit())
    	+ "\n After Credit: " + currentCredit
    	+ "\n Total Usage: " + totalUsage
    	+ "\n Total Amount: " + totalAmount
    	+ "\n Paid Amount: " + paidAmount
    	+ "\n Additional Amount: " + additionalAmount
    	+ "\n Service Charge Amount: " + serviceCharge
    	+ "\n Public Lavy: " + publicLevy
    	+ "\n Gov. Levy: " + govLevy
    	+ "\n VAT: " + vat
    	// + "\n vatOnSubsidyRate: " + vatOnSubsidyRate
    	+ "\n vatOnSubsidy: " + vatOnSubsidy
    	+ "\n LifeLine Subsidy: " + lifeLineSubsidy
    	+ "\n Subsidy: " + govSubsidy
    	// + "\n new Subsidy rate: " + newSubsidyRate
    	+ "\n new Subsidy: " + newSubsidy);
    	
    	contract.setCurrentCredit(currentCredit);
    	contractDao.update(contract);
    	
    	PrepaymentLog prepaymentLog = new PrepaymentLog();
    	prepaymentLog.setLastTokenDate(lastTokenDate);
    	prepaymentLog.setCustomer(customer);
    	prepaymentLog.setContract(contract);        	
    	prepaymentLog.setPreBalance(beforeCredit);
    	prepaymentLog.setBalance(currentCredit);
    	prepaymentLog.setMonthlyTotalAmount(totalAmount);
    	prepaymentLog.setMonthlyPaidAmount(paidAmount);
    	prepaymentLog.setMonthlyServiceCharge(serviceCharge);
    	prepaymentLog.setUsedConsumption(totalUsage);
    	prepaymentLog.setUsedCost(additionalAmount);
    	prepaymentLog.setPublicLevy(publicLevy);
    	prepaymentLog.setGovLevy(govLevy);
    	prepaymentLog.setVat(vat);
    	prepaymentLog.setVatOnSubsidy(vatOnSubsidy);
    	prepaymentLog.setLifeLineSubsidy(lifeLineSubsidy);
    	prepaymentLog.setSubsidy(govSubsidy);
    	prepaymentLog.setAdditionalSubsidy(newSubsidy);
    	prepaymentLog.setOperator(operator);
        prepaymentLog.setLocation(contract.getLocation());
        prepaymentLog.setTariffIndex(contract.getTariffIndex());
    	prepaymentLogDao.add(prepaymentLog);
        log.info(prepaymentLog);

    }
    
    /*
     * 해당월의 마지막 지침값과 전월 지침을 가지고 사용량을 구한다.
     * 마지막 지침값의 날짜가 월말이면 상관없지만 그렇지 않은 경우를 위해서 일수를 가지고 평균을 구한 후에
     * 월 사용량을 계산할 수 있어야 한다.
     */
    private double getUsage(String meterId, String yyyymm) {
        try {
            // 해당월의 마지막 지침값을 가져온다.
            MeteringDataEM thisYyyymm = getMeteringDataEM(meterId, yyyymm);
            if (thisYyyymm == null) return 0.0;
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar cal = Calendar.getInstance();
            MeteringDataEM prevYyyymm = null;
            // 전월 또는 전전월의 마지막 지침값을 가져온다.
            for (int i = 0; i < 2; i++) {
                cal.setTime(sdf.parse(yyyymm+"01000000"));
                cal.add(Calendar.MONTH, -1);
                yyyymm = sdf.format(cal.getTime()).substring(0, 6);
                prevYyyymm = getMeteringDataEM(meterId, yyyymm);
                if (prevYyyymm != null) {
                    return thisYyyymm.getValue() - prevYyyymm.getValue();
                }
            }
            
            return thisYyyymm.getValue();
        }
        catch (ParseException e) {
            log.error(e, e);
        }
        
        return 0.0;
    }
    
    /*
     * 해당월의 마지막 지침값을 조회한다.
     */
    private MeteringDataEM getMeteringDataEM(String meterId, String yyyymm) {
        log.info("meterId[" + meterId + "] yyyymm[" + yyyymm + "]");
        Set<Condition> conditions = new HashSet<Condition>();
        conditions.add(new Condition("id.yyyymmddhhmmss", new Object[]{yyyymm+"%"}, null, Restriction.LIKE));
        conditions.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
        conditions.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
        conditions.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
        List<Projection> projections = new ArrayList<Projection>();
        projections.add(Projections.alias(Projections.max("id.yyyymmddhhmmss"), "maxYyyymmddhhmmss"));
        List<Map<String, Object>> maxyyyymmddhhmmss = ((MeteringDataEMDaoImpl)meteringDataEMDao).findByConditionsAndProjections(conditions, projections);
        
        if (maxyyyymmddhhmmss != null && maxyyyymmddhhmmss.size() == 1) {
            log.info(maxyyyymmddhhmmss.get(0));
            String yyyymmddhhmmss = (String)maxyyyymmddhhmmss.get(0).get("maxYyyymmddhhmmss");
            if (yyyymmddhhmmss == null)
                return null;
            
            conditions = new HashSet<Condition>();
            conditions.add(new Condition("id.yyyymmddhhmmss", new Object[]{yyyymmddhhmmss}, null, Restriction.EQ));
            conditions.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
            conditions.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
            conditions.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
            
            return meteringDataEMDao.findByConditions(conditions).get(0);
        }
        
        return null;
    }
    
    /**
     * @MethodName rollbackMonthlyAdjustedLog
     * @Date 2013. 11. 21.
     * @param yyyymm
     * @Modified
     * @Description 월간 정산으로 차감된 Contract의 선금을 복원하고, PrepaymentLog를 삭제한다. 
     */
    @Transactional(propagation=Propagation.REQUIRED)
    private void rollbackMonthlyAdjustedLog(Contract contract, String lastTokenDate) {
    	
    	try {
    	    Set<Condition> condition = new HashSet<Condition>();
    	    condition.add(new Condition("lastTokenDate", new Object[]{lastTokenDate.substring(0, 6)+"%"}, null, Restriction.LIKE));
    	    condition.add(new Condition("contract", new Object[]{"c"}, null, Restriction.ALIAS));
    	    condition.add(new Condition("c.contractNumber", new Object[]{contract.getContractNumber()}, null, Restriction.EQ));
    	    condition.add(new Condition("monthlyPaidAmount", new Object[]{0.0}, null, Restriction.GE));
    	    condition.add(new Condition("monthlyTotalAmount", new Object[]{0.0}, null, Restriction.GE));
    	    condition.add(new Condition("monthlyServiceCharge", new Object[]{0.0}, null, Restriction.GE));
    	    
            List<PrepaymentLog> list = prepaymentLogDao.findByConditions(condition);
            if (list.size() == 1) {
                PrepaymentLog logData = list.get(0);
                log.info(logData);
                
                Double beforeBalance = StringUtil.nullToDoubleZero(logData.getPreBalance());
                Double afterBalance = StringUtil.nullToDoubleZero(logData.getBalance());
                Double chargedAmount = beforeBalance - afterBalance;
                Double preCredit = StringUtil.nullToDoubleZero(contract.getCurrentCredit());
                Double credit = preCredit + chargedAmount;
                contract.setCurrentCredit(credit);
                log.debug("\n==rollback contract==\n" +
                        "contractNumber: " + contract.getContractNumber() + "\n" +
                        "preCredit: " + preCredit + "\n" + 
                        "chargedAmount: " + chargedAmount + "\n" +  
                        "credit: " + credit + "\n" );
                contractDao.update(contract);
                prepaymentLogDao.delete(logData);
                // contractDao.flushAndClear();
                // prepaymentLogDao.flushAndClear();
            }
            else {
                log.warn("PrepaymentLog size[" + list.size() + "] check");
            }
        } catch (Exception e) {
            log.error(e, e);
        }
    }
}
