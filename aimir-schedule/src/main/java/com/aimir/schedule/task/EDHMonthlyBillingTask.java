package com.aimir.schedule.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.FIXED_VAR;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.FixedVariableDao;
import com.aimir.dao.system.MonthlyBillingLogDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.fep.logger.snowflake.SnowflakeGeneration;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Contract;
import com.aimir.model.system.FixedVariable;
import com.aimir.model.system.MonthlyBillingLog;
import com.aimir.model.system.TariffType;
import com.aimir.schedule.task.EDHMonthlyBillingTask.OPERATION;
import com.aimir.util.DateTimeUtil;

@Service
public class EDHMonthlyBillingTask extends ScheduleTask {
	private static Log log = LogFactory.getLog(EDHMonthlyBillingTask.class);
	
	@Resource(name = "transactionManager")
	private HibernateTransactionManager txmanager;
	
	@Autowired
	private ContractDao contractDao;
	
    public static int totalExecuteCount = 0;
    public static int currentCount = 0;
    
	private boolean isNowRunning = false;
	
	enum OPERATION {
		ADD,
		SUBTRACT,
		MULTIPLY,
		DIVIDE
	}
	
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-public.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        String mdevId = null;
        if(args.length >= 2 ) {
	        for(int i=0; i < args.length; i +=2 ) {
	        	String nextArg = args[i];
	        	
	        	if (nextArg.startsWith("-mdevId")) {
	        		mdevId = new String(args[i+1]);
	            }  
	        }
	        
	        log.info("mdevId : " + mdevId);
        }
        
        EDHMonthlyBillingTask task = ctx.getBean(EDHMonthlyBillingTask.class);
        task.execute(ctx, mdevId);
        System.exit(0);
	}
	
	private void execute(ApplicationContext ctx, String mdevId) {
		if(isNowRunning){
            log.info("########### EDH Monthly Billing already running...");
            return;
        }
		
		List<Contract> targets = getTargets(mdevId);
		if(targets == null || targets.size() == 0) {
			log.info("target is empty!");
			return;
		}
		
		isNowRunning = true;
	    log.info("########### START Monthly Billing Task ###############");
	
	    int poolSize = Integer.parseInt(FMPProperty.getProperty("edh.sms.thread.pool.size", ""+6));
        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 10, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        
        for(Contract co : targets) {
        	try {
        		executor.execute(new EDHMonthlyBillingTaskSubClz(co.getId()));
        	}catch(Exception e) {
        		log.error(e,e);
        	}
        }
        
	    try {
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
        }
        catch (Exception e) {}
        
        log.info("########### END Monthly Billing Task ############");
        isNowRunning = false;        
	}
	
	private List<Contract> getTargets(String mdevId) {
		TransactionStatus txstatus = null;
		List<Contract> queryResult = null;
		
		try {
			txstatus = txmanager.getTransaction(null);
			
			queryResult = contractDao.getMnthlyBillingContractList(mdevId);
			if(queryResult == null || queryResult.size() == 0)
				return null;
			
			totalExecuteCount = queryResult.size();
			log.info("relay off meter cnt : " + totalExecuteCount);
			
			txmanager.commit(txstatus);
		}catch(Exception e) {
			log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
		}
		
		return queryResult;
	}
	
	@Override
	public void execute(JobExecutionContext context) { }

}

class EDHMonthlyBillingTaskSubClz implements Runnable {
	private final static Log log = LogFactory.getLog(EDHMonthlyBillingTaskSubClz.class);
	
	private HibernateTransactionManager txmanager;
	private MeterDao meterDao;
	private CodeDao codeDao;
	private ContractDao contractDao;
	private MonthlyBillingLogDao monthlyBillingLogDao;
	private FixedVariableDao fixedVariableDao;
	private TariffTypeDao tariffTypeDao;
	private Contract contract;
	private Meter meter;
	
	private Integer contractId;
	
	EDHMonthlyBillingTaskSubClz(Integer contractId) {
		txmanager = (HibernateTransactionManager)DataUtil.getBean("transactionManager");
		meterDao = DataUtil.getBean(MeterDao.class);
		codeDao = DataUtil.getBean(CodeDao.class);
		contractDao = DataUtil.getBean(ContractDao.class);
		monthlyBillingLogDao = DataUtil.getBean(MonthlyBillingLogDao.class);
		fixedVariableDao = DataUtil.getBean(FixedVariableDao.class);
		tariffTypeDao = DataUtil.getBean(TariffTypeDao.class);
		
		this.contractId = contractId;
		SnowflakeGeneration.getInstance();
	}
	
	private void init() {
		contract = contractDao.get(contractId);
		log.debug("contractId : " + contractId);
		
		if(contract != null)
			meter = contract.getMeter();
	}
	
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		SnowflakeGeneration.getId();
		
		synchronized (this) {
			EDHMonthlyBillingTask.currentCount++;
		}
		
		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			init();
			checkMontylyBilling();
			
			txmanager.commit(txstatus);
		} catch(Exception e) {			
    		log.error(e, e);
    		
    		if (txstatus != null) 
    			txmanager.rollback(txstatus);
		}
		
		synchronized (this) {
    		double rate = ((double) EDHMonthlyBillingTask.currentCount / (double) EDHMonthlyBillingTask.totalExecuteCount) * 100;

			long end = System.currentTimeMillis();
			long t = (end - start) / 1000;
			
			log.info("PROCESS CURRNET ["+String.format("%.3f", rate)+"%], TOTAL COUNT ["+EDHMonthlyBillingTask.totalExecuteCount+"], CURRENT COUNT ["+EDHMonthlyBillingTask.currentCount+"] "
					+ "time ["+t+"] ThreadName ["+Thread.currentThread().getName()+"] threadId ["+Thread.currentThread().getId()+"]");
		}
		
		SnowflakeGeneration.deleteId();
	}
	
	/*
	 * 현재월의 빌링여부를 확인한다.
	 */
	private void checkMontylyBilling() throws Exception {
		if(contract == null) {
			log.info("contract is null! contractId : " + contractId);
			return;
		}
		
		List<MonthlyBillingLog> saveMonthlyBillingList = null;
		String curMonth = DateTimeUtil.getDateString(new Date(), "yyyyMM");
		
		//이번달 월정산 여부를 확인한다.
		log.info("curMonth : " + curMonth +", mdsId : " + meter.getMdsId() +", contractId : "+contractId);
		MonthlyBillingLog monthlyBillingLog = monthlyBillingLogDao.getLastMonthlyBillingLog(contractId, null, curMonth);
		if(monthlyBillingLog != null) {
			log.debug("yyyymm : " + monthlyBillingLog.getYyyymm() +", mdsId : " +monthlyBillingLog.getMdsId());
			return;
		}
		
		if(contract.getTariffIndexId() == null) {
			log.info("tariffTypeId is null!! please check tariffId, contractId : " + contract.getId());
		}
		
		FixedVariable fixedVariable = fixedVariableDao.getFixedVariableDao(FIXED_VAR.SERVICE_CHARGE.name(), contract.getTariffIndexId(), DateTimeUtil.getDateString(new Date()));
		if(fixedVariable == null) {
			log.info("fixedVariable is null!! check parameter!!, name : " + FIXED_VAR.SERVICE_CHARGE.name()+", tariffId : " + contract.getTariffIndexId()+", applyDate : "+DateTimeUtil.getDateString(new Date()));
		}
		
		double serviceCharge = Double.parseDouble(fixedVariable.getAmount());
		monthlyBillingLog = monthlyBillingLogDao.getLastMonthlyBillingLog(contractId, meter.getMdsId(), null);
		if(monthlyBillingLog == null) {
			//한번도 월정산이 되지 않은 미터
			saveMonthlyBillingList = getMonthBillingLogList(serviceCharge);
		} else {
			//한번이상 정산되었지만 이번달에 정산되지 않은 미터
			saveMonthlyBillingList = getMonthBillingLogList(serviceCharge, monthlyBillingLog); 
		}

		StringBuffer buffer = new StringBuffer();
		BigDecimal balance = getBigDecimal(contract.getCurrentCredit());
		buffer.append("before balance : ").append(balance).append(", ");
		
		log.debug("saveMonthlyBillingLog : " + saveMonthlyBillingList.size());
		
		if(saveMonthlyBillingList.size() > 0) {
			for(MonthlyBillingLog mb : saveMonthlyBillingList) {
				
				BigDecimal sCharge = getBigDecimal(mb.getServiceCharge());				
				BigDecimal afterBalance = getBigDecialCalculation(balance, sCharge, OPERATION.SUBTRACT);
				
				mb.setBeforeCredit(balance.doubleValue());
				mb.setCurrentCredit(afterBalance.doubleValue());

				balance = afterBalance;
				monthlyBillingLogDao.add(mb);
				log.debug(mb.toString());
			}
			
			contract.setCurrentCredit(balance.doubleValue());
			contractDao.update(contract);
			
			buffer.append("after balance : ").append(balance);
			log.info("balance changed || " + buffer.toString());
		}
	}
	
	private List<MonthlyBillingLog> getMonthBillingLogList(double monthlyTax) throws Exception {
		List<MonthlyBillingLog> resultList = new ArrayList<MonthlyBillingLog>();
		
		String fDate = contract.getContractDate() != null ? contract.getContractDate() : meter.getInstallDate();
		fDate = fDate.substring(0, 8) + "01000000";
		Calendar cal = DateTimeUtil.getCalendar(fDate);
		
		Calendar now = Calendar.getInstance();
		while(true) {
			if(now.before(cal)) {
				break;
			} 
			
			String yyyymm = DateTimeUtil.getDateString(cal.getTime(), "yyyyMM");
			MonthlyBillingLog monthlyLog = setMonthlyBillingLog(yyyymm, new BigDecimal(String.valueOf(monthlyTax)));
			resultList.add(monthlyLog);
			
			cal.add(Calendar.MONTH, 1);
		}
		
		return resultList;
	}
	
	/*
	 * 계약일 기준으로 계약일(day) * (월 기본료 / 계얄월 마지막일)으로 계산 - //2021.04.21 일에 상관없이 무조건 월 기본료 차감으로 변경 됨
	private List<MonthlyBillingLog> getMonthBillingLogList(double monthlyTax) throws Exception {
		MonthlyBillingLog monthlyBillingLog = null;
		List<MonthlyBillingLog> resultList = new ArrayList<MonthlyBillingLog>();
		
		String fDate = contract.getContractDate() != null ? contract.getContractDate() : meter.getInstallDate();
		int installDay = Integer.parseInt(fDate.substring(6, 8));
		
		Calendar cal = DateTimeUtil.getCalendar(fDate);
		int monthMaxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		double avgDay = monthlyTax / monthMaxDay;
		int usingDay = 	monthMaxDay - installDay;
		
		BigDecimal avgDaySC = getBigDecimal(avgDay * usingDay);
		log.info("fDate : " + fDate + ",serviceCharge : " + monthlyTax + ", installDay : " + installDay +", monthMaxDay : " + monthMaxDay+", avgDay : " + avgDay+", usingDay : " + usingDay+", avgDaySC : " +avgDaySC);
		
		String yyyymm = fDate.substring(0, 6);
		monthlyBillingLog = setMonthlyBillingLog(yyyymm, avgDaySC);
		resultList.add(monthlyBillingLog);
		
		Calendar opar = Calendar.getInstance();
		opar.setTime(DateTimeUtil.getDateFromYYYYMMDD(monthlyBillingLog.getYyyymm() + "01"));
		opar.add(Calendar.MONTH, 1);
		
		Calendar now = Calendar.getInstance();
		while(true) {
			if(now.before(opar)) {
				break;
			} 
			
			yyyymm = DateTimeUtil.getDateString(opar.getTime(), "yyyyMM");
			MonthlyBillingLog monthlyLog = setMonthlyBillingLog(yyyymm, new BigDecimal(String.valueOf(monthlyTax)));
			resultList.add(monthlyLog);
			opar.add(Calendar.MONTH, 1);
		}
		
		
		return resultList;
	}
	*/
	
	private List<MonthlyBillingLog> getMonthBillingLogList(double monthlyTax, MonthlyBillingLog lastMonthBilling) throws Exception {
		List<MonthlyBillingLog> resultList = new ArrayList<MonthlyBillingLog>();
		
		String fDate = lastMonthBilling.getYyyymm() + "01000000";
		Calendar cal = DateTimeUtil.getCalendar(fDate);
		cal.add(Calendar.MONTH, 1);
		
		Calendar now = Calendar.getInstance();
		while(true) {
			if(now.before(cal)) {
				break;
			} 
			
			String yyyymm = DateTimeUtil.getDateString(cal.getTime(), "yyyyMM");
			MonthlyBillingLog monthlyLog = setMonthlyBillingLog(yyyymm, new BigDecimal(String.valueOf(monthlyTax)));
			resultList.add(monthlyLog);
			
			cal.add(Calendar.MONTH, 1);
		}
		
		return resultList;
	}
	
	
	private BigDecimal getBigDecimal(Object val) {
		return getBigDecimal(val, RoundingMode.FLOOR, 2);
	}
	
	private BigDecimal getBigDecimal(Object val, RoundingMode roundingMode, int digit) {
		if(val == null)
			val = "0";
		
		return new BigDecimal(String.valueOf(val)).setScale(digit, roundingMode);
	}
	
	private BigDecimal getBigDecialCalculation(BigDecimal left, BigDecimal right, OPERATION op) {
		BigDecimal retVal = null;
		
		switch(op) {
		case ADD:
			retVal = left.add(right);
			break;
		case SUBTRACT:
			retVal = left.subtract(right);
			break;
		case DIVIDE:
			retVal = left.divide(right);
			break;
		case MULTIPLY:
			retVal = left.multiply(right);
			break;
		}
		
		return retVal;
	}
	
	private MonthlyBillingLog setMonthlyBillingLog(String yyyymm, BigDecimal sc) {
		MonthlyBillingLog m = new MonthlyBillingLog();
		
		TariffType tariffType = null;
		if(contract.getTariffIndexId() != null) {
			tariffType = tariffTypeDao.get(contract.getTariffIndexId());
			m.setTariffType(tariffType.getName());
		}
		
		m.setContractId(contract.getId());
		m.setYyyymm(yyyymm);
		m.setMdsId(meter.getMdsId());
		m.setServiceCharge(sc.doubleValue());
		m.setWriteDate(DateTimeUtil.getDateString(new Date()));
		
		return m;
	}
}
