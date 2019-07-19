package com.aimir.schedule.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.MeteringDataEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.impl.LpEMDaoImpl;
import com.aimir.dao.mvm.impl.MonthEMDaoImpl;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.LpEM;
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
public class ECGBillingMonthlyV3Task extends ScheduleTask {
	
	private static Log log = LogFactory.getLog(ECGBillingMonthlyV3Task.class);
    
	@Autowired
	LpEMDao lpEMDao;
	
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
	
	private String lastLpTime;
	
	double currentCredit = 0.0;
	
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-billingmonthlyv2.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        ECGBillingMonthlyV3Task task = ctx.getBean(ECGBillingMonthlyV3Task.class);
        try {
            task.execute(args[0], args[1], Integer.parseInt(args[2]));
        }
        catch (Exception e) {
            log.error(e, e);
        }
        System.exit(0);
    }
	
    @Transactional
    public void execute(String meterId, String yyyymm, int mon_cnt) throws Exception {
        log.info("###### Start Monthly Additional Billing MeterId[" + meterId + 
                "] YYYYMM[" + yyyymm + "] MON_CNT[" + mon_cnt + "] ######");
        
        Contract contract = getContract(meterId);
        // 월정산을 실행한 날짜시간
        String lastTokenDate = DateTimeUtil.getDateString(new Date());
        
        Operator operator = operatorDao.getOperatorByLoginId("admin");
        
        String tariffName = "";
        
        double lastMeteringValue = getLastMetering(meterId);
        log.info("LastMeteringValue[" + lastMeteringValue + "] LastLP_Time[" + lastLpTime + "]");
        
        if (lastMeteringValue == 0.0 || lastLpTime == null)
            return;
        
        Map<String, Double> yyyymmUsage = avgMonth(lastMeteringValue, yyyymm);
        
        String _yyyymm = null;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        cal.setTime(sdf.parse(yyyymm));
        currentCredit = contract.getCurrentCredit();
        
        // 시작녕월부터 정산하려는 개월수만큼만 정산한다.
        for (int j = 0; j < mon_cnt; j++) {
            _yyyymm = sdf.format(cal.getTime());
            try {
                calculateByContract(contract, _yyyymm+"31", tariffName,
                        _yyyymm, yyyymmUsage.get(_yyyymm), lastTokenDate, operator);
            } catch (Exception e){
                log.error("Contract ID: " + contract.getId() + " YYYYMM[" + _yyyymm + "]");
                log.error(e, e);
            }
            cal.add(Calendar.MONDAY, 1);
        }
        log.info("###### End Monthly Additional Billing ######");
    }
    
    /*
     * 월정산 시작 년월부터 마지막 LP의 년월까지의 일수를 계산하여 LP 전월까지의 월 평균 사용량을 계산한다.
     */
    private Map<String, Double> avgMonth(double lastMeteringValue, String startYyyymm)
            throws ParseException {
        // 마지막 LP의 일자를 가져온다.
        int days = 0;
        int sumDays = Integer.parseInt(lastLpTime.substring(6, 8));
        double avg = 0.0;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(startYyyymm));
        Map<String, Double> yyyymmUsage = new HashMap<String, Double>();
        
        int year = 0;
        int month = 1;
        String yearmonth;
        while (true) {
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            yearmonth = String.format("%04d%02d", year, month);

            // LP와 같은 년월이면 종료한다.
            if (yearmonth.compareTo(lastLpTime.substring(0,6)) >= 0)
                break;
            
            switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                days = 31;
                break;
            case 2:
                if (year % 4 == 0) days = 29;
                else days = 28;
                break;
            default : days = 30;
            }
            
            yyyymmUsage.put(sdf.format(cal.getTime()), (double)days);
            sumDays += days;
            cal.add(Calendar.MONTH, 1);
        }
        
        log.info("LastLPDays[" + sumDays + "]");
        if (sumDays != 0) avg = lastMeteringValue / (double)sumDays;
        
        String yyyymm = null;
        for (Iterator<String> i = yyyymmUsage.keySet().iterator(); i.hasNext(); ) {
            yyyymm = i.next();
            yyyymmUsage.put(yyyymm, avg * yyyymmUsage.get(yyyymm));
        }
        
        return yyyymmUsage;
    }
    
    // 시작월부터 lpTime의 월까지의 사용량을 구한다.
    private double getMonthSum(String meterId, String startYyyymm) {
        double total = 0.0;
        
        Set<Condition> conditions = new HashSet<Condition>();
        conditions.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
        conditions.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
        conditions.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
        conditions.add(new Condition("id.channel", new Object[]{1, 2}, null, Restriction.IN));
        conditions.add(new Condition("id.yyyymm", new Object[]{startYyyymm, lastLpTime.substring(0, 6)}, null, Restriction.BETWEEN));
        
        List<Projection> projections = new ArrayList<Projection>();
        projections.add(Projections.alias(Projections.sum("total"), "total"));
        List<Map<String, Object>> _total = ((MonthEMDaoImpl)monthEMDao).findByConditionsAndProjections(conditions, projections);
        
        if (_total != null && _total.size() == 1) {
            total = (Double)_total.get(0).get("total");
        }
        
        return total;
    }
    
     // 해당년월의 1, 2 채널의 월 합산을 가져온다.
    private double getMonthTotal(String meterId, String yyyymm) {
        double total = 0.0;
        
        Set<Condition> conditions = new HashSet<Condition>();
        conditions.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
        conditions.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
        conditions.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
        conditions.add(new Condition("id.channel", new Object[]{1, 2}, null, Restriction.IN));
        conditions.add(new Condition("id.yyyymm", new Object[]{yyyymm}, null, Restriction.EQ));
        
        List<Projection> projections = new ArrayList<Projection>();
        projections.add(Projections.alias(Projections.sum("total"), "total"));
        List<Map<String, Object>> _total = ((MonthEMDaoImpl)monthEMDao).findByConditionsAndProjections(conditions, projections);
        
        if (_total != null && _total.size() == 1) {
            if (_total.get(0) != null && _total.get(0).get("total") != null)
                total = (Double)_total.get(0).get("total");
        }
        
        return total;
    }
    
    // 마지막 누적값을 가져온다.
    private double getLastMetering(String meterId) {
        double lastMeteringValue = 0.0;
        
        Set<Condition> conditions = new HashSet<Condition>();
        conditions.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
        conditions.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
        conditions.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
        conditions.add(new Condition("id.channel", new Object[]{1}, null, Restriction.EQ));
        List<Projection> projections = new ArrayList<Projection>();
        projections.add(Projections.alias(Projections.max("id.yyyymmddhh"), "maxYyyymmddhh"));
        List<Map<String, Object>> maxyyyymmddhh = ((LpEMDaoImpl)lpEMDao).findByConditionsAndProjections(conditions, projections);
        
        if (maxyyyymmddhh != null && maxyyyymmddhh.size() == 1) {
            log.info(maxyyyymmddhh.get(0));
            lastLpTime = (String)maxyyyymmddhh.get(0).get("maxYyyymmddhh");
            if (lastLpTime == null)
                return 0.0;
            
            conditions = new HashSet<Condition>();
            conditions.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
            conditions.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
            conditions.add(new Condition("id.mdevId", new Object[]{meterId}, null, Restriction.EQ));
            conditions.add(new Condition("id.channel", new Object[]{1, 2}, null, Restriction.IN));
            conditions.add(new Condition("id.yyyymmddhh", new Object[]{lastLpTime}, null, Restriction.EQ));
            
            List<LpEM> lplist = lpEMDao.findByConditions(conditions);
            
            for (LpEM l : lplist) {
                lastMeteringValue += l.getValue();
            }
        }
        
        return lastMeteringValue;
    }
    
	@Override
	public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### ECGBillingMonthlyV3Task is already running...");
			return;
		}
		isNowRunning = true;
	}
	
	/*
	 * 모뎀이 GPRS(MMIU) 이면서 선불 고객
	 */
	private Contract getContract(String meterId) {
	    Set<Condition> condition = new HashSet<Condition>();
	    condition.add(new Condition("meter", new Object[]{"meter"}, null, Restriction.ALIAS));
	    condition.add(new Condition("meter.mdsId", new Object[]{meterId}, null, Restriction.EQ));
	    
	    List<Contract> contracts = contractDao.findByConditions(condition);
	    if (contracts != null && contracts.size() == 1)
	        return contracts.get(0);
	    else
	        return null; 
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
    
    private void calculateByContract(Contract contract, 
    		String tariffDay, 
    		String tariffName,
    		String yyyymm,
    		double totalUsage,
    		String lastTokenDate,
    		Operator operator) {
    	
    	Map<String ,Object> param = new HashMap<String, Object>();
    	
    	Customer customer = null;
    	Meter meter = null;

		customer = customerDao.get(contract.getCustomerId());
		meter = contract.getMeter();
		
		if ( customer == null || meter == null ) {
			return;
		}
		
		// 이전 월정산을 롤백한다.
        // rollbackMonthlyAdjustedLog(contract, lastTokenDate);
		
    	param.put("tariffIndex", contract.getTariffIndex());
    	param.put("searchDate", tariffDay);
    	
    	TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId());
		tariffName = tariffType.getName();
    	
    	List<TariffEM> tariffList = tariffEmDao.getApplyedTariff(param);
    	if ( tariffList == null || tariffList.size() < 1 ) {
    		return;
    	}
    	
    	Double totalAmount = 0d;
    	
    	// 해당월의 사용량을 평균 사룔양에서 제한다.
    	double monthTotal = getMonthTotal(meter.getMdsId(), yyyymm);
    	totalUsage -= monthTotal;
    	
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
        
    	paidAmount = 0.0;
    	Double additionalAmount = totalAmount - paidAmount;
    	
    	// subsidy는 모든 subsidy의 합으로 계산한다.
    	Double subsidy = govSubsidy + lifeLineSubsidy + newSubsidy;
    	Double levy = publicLevy + govLevy;
    	
    	// 2015.04.08 월정산중에 충전을 시도하면 충전금액이 반영되지 않을 수 있다.
    	// 최근 잔액을 가져오도록 한다.
    	// contract = contractDao.get(contract.getId());
    	
    	Double beforeCredit = currentCredit; // StringUtil.nullToDoubleZero(contract.getCurrentCredit());
    	currentCredit =  currentCredit - additionalAmount - serviceCharge - levy - vat + subsidy;    	
    	
    	log.debug("\n=== Contract Number: " + contract.getContractNumber() + " YYYYMM:" + yyyymm + " ==="
    	+ "\n Before Credit: " + beforeCredit // StringUtil.nullToDoubleZero(contract.getCurrentCredit())
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
        prepaymentLog.setLastLpTime(yyyymm);
    	prepaymentLogDao.add(prepaymentLog);
        log.info(prepaymentLog);

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
            for (PrepaymentLog logData : list) {
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
        } catch (Exception e) {
            log.error(e, e);
        }
    }
}
