package com.aimir.schedule.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.Operator;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

public class ECGBillingMonthlyTask extends ScheduleTask {
	
	private static Log log = LogFactory.getLog(ECGBillingMonthlyTask.class);
    
    @Resource(name="transactionManager")
    HibernateTransactionManager txManager;
    
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
	
	TransactionStatus txStatus = null;
	private boolean isNowRunning = false;
	
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-forcrontab.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        ECGBillingMonthlyTask task = ctx.getBean(ECGBillingMonthlyTask.class);
        task.execute(null);
        System.exit(0);
    }
	
	@Override
	public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### ECGBillingMonthlyTask is already running...");
			return;
		}
		isNowRunning = true;
		
		log.info("###### Start Monthly Additional Billing ######");
    	String now = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

		String yyyymm = "";
		String tariffName = "";
		
		try {
			yyyymm = DateTimeUtil.getPreDay(now, 28).substring(0, 6);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("\n now: " + now
				+ "\n yyyymm: " + yyyymm);
		
		// 만약 이전에 한번 월간 정산을 한번 했다면 그 이력에 대해서 rollback한다. 
		rollbackMonthlyAdjustedLog(now.substring(0,6));
		List<Contract> list = contractDao.getECGContract();
		
    	String lastTokenDate = now;
    	//lastTokenDate = lastTokenDate.substring(0, 6) + "01000000";
    	Operator operator = operatorDao.getOperatorByLoginId("admin");
    	
    	int index = 0;
    	int size = list.size();
    	
        for ( Contract contract : list  ) {
        	txStatus = null;
        	
        	try {
        		txStatus = txManager.getTransaction(null);
        		calculateByContract(contract, ++index, size, index, yyyymm+"31", tariffName, yyyymm, lastTokenDate, operator);
        		txManager.commit(txStatus);
        	} catch (Exception e){
        		txManager.rollback(txStatus);
        		log.error("Contract ID: " + contract.getId());
        		e.printStackTrace();
        	}
        	
        }
        log.info("###### End Monthly Additional Billing ######");
        isNowRunning = false;
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

		customer = customerDao.get(contract.getCustomerId());
		meter = contract.getMeter();
		
		if ( customer == null || meter == null ) {
			return;
		}
		
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
    	
    	totalUsage = StringUtil.nullToDoubleZero(prepaymentLogDao.getMonthlyUsageByContract(contract, yyyymm));
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

    }
    
    /**
     * @MethodName rollbackMonthlyAdjustedLog
     * @Date 2013. 11. 21.
     * @param yyyymm
     * @Modified
     * @Description 월간 정산으로 차감된 Contract의 선금을 복원하고, PrepaymentLog를 삭제한다. 
     */
    private void rollbackMonthlyAdjustedLog(String yyyymm) {
    	
    	try {
    		txStatus = txManager.getTransaction(null);
			List<PrepaymentLog> list = prepaymentLogDao.getMonthlyReceiptLog(yyyymm);
			
			log.info("\n\n Rollback previous Monthly transaction for this month \n" + 
			"Log Size: " + list.size() + "\n\n");
			int cnt= 0;
			for ( PrepaymentLog logData : list ) {
					Contract contract  = contractDao.get(logData.getContractId());
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
					
					if ((++cnt)%1000 == 0) {
                        contractDao.flushAndClear();
                        prepaymentLogDao.flushAndClear();
                    }
			}
			txManager.commit(txStatus);
		} catch (Exception e) {
			txManager.rollback(txStatus);
			e.printStackTrace();
		}
    	
    }
}
