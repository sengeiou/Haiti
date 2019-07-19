package com.aimir.schedule.task;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

public class ECGBillingMonthlyNotCalculationTask extends ScheduleTask {
	
	private static Log log = LogFactory.getLog(ECGBillingMonthlyNotCalculationTask.class);
    
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
	MeterDao meterDao;
	
	@Autowired
	TariffTypeDao tariffTypeDao;
	
	@Autowired
	LocationDao locationDao;	
	
	
	TransactionStatus txStatus = null;
	
    enum KamstrupChannel {
        ActiveEnergyImp(1), ActiveEnergyExp(2), ReactiveEnergyImp(3), ReactiveEnergyExp(4);
        
        private Integer channel;
        
        KamstrupChannel(Integer channel) {
            this.channel = channel;
        }
        
        public Integer getChannel() {
            return this.channel;
        }
    }

	public void execute(JobExecutionContext context) {
		log.info("###### Start Monthly Additional Billing in ECGBillingMonthlyNotCalculationTask ######");
		String[] yyyymmArr = new String[3];
		//lastTokenDateArr 는 정산을 돌리는 현재날짜를 의미하고 7,8,9월달의 사용량을 정산하는 거기때문에 lastTokenDate를 한달씩 뒤인 8,9,10월 1일로 지정한다.
		String[] lastTokenDateArr = {"20140801000000","20140901000000","20141001000000"};
		String tariffName = "";
		
		try {
			for (int i = 0; i < lastTokenDateArr.length; i++) {
				yyyymmArr[i] = DateTimeUtil.getPreDay(lastTokenDateArr[i], 28).substring(0, 6);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        try{
	    	Map<String, Object> conditionMap = new HashMap<String, Object>();
	    	String[] modelName = {"K382M AB1"};
	        conditionMap.put("modelName", modelName);
			List<Contract> list = contractDao.getECGContractByNotCalculation(conditionMap);
			
	    	Operator operator = operatorDao.getOperatorByLoginId("admin");
	    	
	    	
	    	int size = list.size();
	    	for (int i = 0; i < lastTokenDateArr.length; i++) {
	    		log.info("\n monthlyDataSaveDate: " + lastTokenDateArr[i]
	    				+ "\n yyyymm: " + yyyymmArr[i]);
	    		int index = 0;
		        for ( Contract contract : list  ) {
		        	txStatus = null;
		        	
		        	try {
		        		txStatus = txManager.getTransaction(null);
		        		calculateByContract(contract, ++index, size, index, yyyymmArr[i]+"31", tariffName, yyyymmArr[i], lastTokenDateArr[i], operator);
		        		txManager.commit(txStatus);
		        	} catch (Exception e){
		        		txManager.rollback(txStatus);
		        		log.error("Contract ID: " + contract.getId());
		        		log.warn(e,e);
		        		e.printStackTrace();
		        	}
		        	
		        }
	    	}
        } catch(Exception e) {
            log.error(e,e);
        } 
        log.info("###### End Monthly Additional Billing in ECGBillingMonthlyNotCalculationTask ######");
        
	}
	
	public Double blockBill(String tariffName, List<TariffEM> tariffEMList, double usage) {
        double supplyMin = 0.0;
        double supplyMax = 0.0;
        //해당구간에서 사용한 사용량
        double diff = 0.0;
        //남은 사용량(사용량 - 계산완료된 사용량)
        double resultUsage = 0.0;
        double returnBill = 0.0;
        
        Collections.sort(tariffEMList, new Comparator<TariffEM>() {

            @Override
            public int compare(TariffEM t1, TariffEM t2) {
                return t1.getSupplySizeMin() < t2.getSupplySizeMin()? -1:1;
            }
        });
        
        if (tariffName.equals("Residential")) {
            // 0 ~ 50
            if (usage <= tariffEMList.get(1).getSupplySizeMin()) {
                returnBill = usage * tariffEMList.get(0).getActiveEnergyCharge();
            }
            // 50 ~
            else {
                // ~ 300
                if (usage <= tariffEMList.get(1).getSupplySizeMax()) {
                    returnBill = usage * tariffEMList.get(1).getActiveEnergyCharge();
                }
                // ~ 600
                else if (usage <= tariffEMList.get(2).getSupplySizeMax()) {
                    returnBill = tariffEMList.get(1).getSupplySizeMax() * tariffEMList.get(1).getActiveEnergyCharge();
                    returnBill += ((usage - tariffEMList.get(2).getSupplySizeMin()) * tariffEMList.get(2).getActiveEnergyCharge());
                }
                // 600 ~
                else {
                    returnBill = tariffEMList.get(1).getSupplySizeMax() * tariffEMList.get(1).getActiveEnergyCharge();
                    returnBill += (tariffEMList.get(2).getSupplySizeMax()-tariffEMList.get(2).getSupplySizeMin()) * 
                            tariffEMList.get(2).getActiveEnergyCharge();
                    returnBill += ((usage - tariffEMList.get(3).getSupplySizeMin()) * tariffEMList.get(3).getActiveEnergyCharge());
                }
            }
        }
        else if (tariffName.equals("Non Residential")) {
            // ~ 300
            if (usage <= tariffEMList.get(0).getSupplySizeMax()) {
                returnBill = usage * tariffEMList.get(0).getActiveEnergyCharge();
            }
            // ~ 600 
            else if (usage <= tariffEMList.get(1).getSupplySizeMax()) {
                returnBill = tariffEMList.get(0).getSupplySizeMax() * tariffEMList.get(0).getActiveEnergyCharge();
                returnBill += ((usage - tariffEMList.get(1).getSupplySizeMin()) * tariffEMList.get(1).getActiveEnergyCharge());
            }
            // 600 ~
            else {
                returnBill = tariffEMList.get(0).getSupplySizeMax() * tariffEMList.get(0).getActiveEnergyCharge();
                returnBill += (tariffEMList.get(1).getSupplySizeMax()-tariffEMList.get(1).getSupplySizeMin()) * tariffEMList.get(1).getActiveEnergyCharge();
                returnBill += ((usage - tariffEMList.get(2).getSupplySizeMin()) * tariffEMList.get(2).getActiveEnergyCharge());
            }
        }
        return returnBill;
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
    	
    	log.info("### date ["+yyyymm+"] "+ index +"/" + size +"###");
    	
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("contractId",contract.getId());
    	conditionMap.put("lastTokenDate",lastTokenDate.substring(0,6));
    	List<PrepaymentLog> monthlyPaidDataList = prepaymentLogDao.getMonthlyPaidDataCount(conditionMap);
    	//월정산사용량 데이터가 이미 존해하고 그 데이터의 사용량이 0보다 크면 이미 월정산이 실행된것으로 간주한다.
		for (int i = 0; i < monthlyPaidDataList.size(); i++) {
			if(!(monthlyPaidDataList.get(i).getUsedConsumption() <= 0)) {
				log.info("\n=== Contract Number: " + contract.getContractNumber() + " ===" +
						"\nmonthlyData is exist");
	    		return;
			}
		}

    	Map<String ,Object> param = new HashMap<String, Object>();
    	
    	Customer customer = null;
		customer = customerDao.get(contract.getCustomerId());
		
		if ( customer == null || contract.getMeter() == null ) {
			return;
		}
		
    	TariffType tariffType = tariffTypeDao.get(contract.getTariffIndexId());
		tariffName = tariffType.getName();
		
		param.put("tariffIndex", tariffType);
    	param.put("searchDate", tariffDay);
    	
    	List<TariffEM> tariffList = tariffEmDao.getApplyedTariff(param);
    	if ( tariffList == null || tariffList.size() < 1 ) {
    		return;
    	}
    	
    	Double totalAmount = 0d;
    	Double totalUsage = 0d;
    	
    	String serviceChannel = KamstrupChannel.ActiveEnergyImp.getChannel()+","+KamstrupChannel.ActiveEnergyExp.getChannel();
        
    	List<MonthEM> monthlyEMList = monthEMDao.getMonthlyUsageByContract(contract, yyyymm, serviceChannel);
    	
    	for ( MonthEM em : monthlyEMList ) {
    		totalUsage += StringUtil.nullToDoubleZero(em.getTotal());
    	}
    	
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
    	if("Residential".equals(tariffName) && totalUsage == 0) {
    	    lifeLineSubsidy = 0d;
    	}
    	
    	//사용량이 150이상이면 govSubsidy는 없다.
    	if("Residential".equals(tariffName) && totalUsage > 150) {
    	    govSubsidy = 0d;
    	}

    	Double paidAmount = prepaymentLogDao.getMonthlyPaidAmount(contract, yyyymm);
    	
    	Double vatOnSubsidyRate = StringUtil.nullToDoubleZero(levyTariff.getEnergyDemandCharge()) 
    			* StringUtil.nullToDoubleZero(levyTariff.getRateRebalancingLevy());
    	vatOnSubsidyRate = Double.parseDouble(String.format("%.4f", vatOnSubsidyRate));
    	
    	Double vatOnSubsidy = vatOnSubsidyRate * totalUsage;
    	Double newSubsidyRate = (StringUtil.nullToDoubleZero(vatOnSubsidyRate) 
    			+ StringUtil.nullToDoubleZero(levyTariff.getRateRebalancingLevy()));
    	Double newSubsidy = newSubsidyRate * totalUsage;
    	
    	Double additionalAmount = totalAmount - paidAmount;
    	
    	// subsidy는 모든 subsidy의 합으로 계산한다.
    	Double subsidy = govSubsidy + lifeLineSubsidy + newSubsidy;
    	Double levy = publicLevy + govLevy;
    	
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
    	+ "\n vatOnSubsidyRate: " + vatOnSubsidyRate
    	+ "\n vatOnSubsidy: " + vatOnSubsidy
    	+ "\n LifeLine Subsidy: " + lifeLineSubsidy
    	+ "\n Subsidy: " + govSubsidy
    	+ "\n new Subsidy rate: " + newSubsidyRate
    	+ "\n new Subsidy: " + newSubsidy);
    	
    	contract.setCurrentCredit(currentCredit);
    	contractDao.update(contract);
    	
    	Location location = contract.getLocation() == null ? null : locationDao.get(contract.getLocationId());
    	
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
        prepaymentLog.setLocation(location);
        prepaymentLog.setTariffIndex(tariffType);
    	prepaymentLogDao.add(prepaymentLog);    	

    }

}
