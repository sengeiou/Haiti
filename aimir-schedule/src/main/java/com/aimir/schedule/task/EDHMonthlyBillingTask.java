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
import com.aimir.fep.logger.snowflake.SnowflakeGeneration;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Contract;
import com.aimir.model.system.FixedVariable;
import com.aimir.model.system.MonthlyBillingLog;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

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
            log.info("########### EDH Realy off already running...");
            return;
        }
		
		List<Contract> targets = getTargets(mdevId);
		if(targets == null || targets.size() == 0) {
			log.info("target is empty!");
			return;
		}
		
		isNowRunning = true;
	    log.info("########### START Realy off Task ###############");
	
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
        
        log.info("########### END Realy off Task ############");
        isNowRunning = false;        
	}
	
	private List<Contract> getTargets(String mdevId) {
		TransactionStatus txstatus = null;
		List<Contract> queryResult = null;
		
		try {
			txstatus = txmanager.getTransaction(null);
			
			queryResult = contractDao.getValidContractList(mdevId);
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
		
		this.contractId = contractId;
	}
	
	private void init() {
		contract = contractDao.get(contractId);

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
		
		//이번달 월정산 여부를 확인한다.
		MonthlyBillingLog monthlyBillingLog = monthlyBillingLogDao.getLastMonthlyBillingLog(contractId, meter.getMdsId());
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
		
		List<MonthlyBillingLog> saveMonthlyBillingList = new ArrayList<MonthlyBillingLog>();
		
		double serviceCharge = Double.parseDouble(fixedVariable.getAmount());
		//월 정산 기록이 없다면... 첫번째 월의 사용량을 일할계산한다.
		if(monthlyBillingLog == null) {
			String fDate = contract.getContractDate() != null ? contract.getContractDate() : meter.getInstallDate();
			int installDay = Integer.parseInt(fDate.substring(4, 6));
			
			Calendar cal = DateTimeUtil.getCalendar(fDate);
			int monthMaxDay = cal.getMaximum(Calendar.DAY_OF_MONTH);
			
			double avgDay = serviceCharge / monthMaxDay;
			int usingDay = 	monthMaxDay - installDay;
			
			BigDecimal avgDaySC = getBigDecimal(String.valueOf(avgDay * usingDay), RoundingMode.FLOOR, 2);
			log.info("serviceCharge : " + serviceCharge + ", installDay : " + installDay +", monthMaxDay : " + monthMaxDay+", avgDay : " + avgDay+", usingDay : " + usingDay+", avgDaySC : " +avgDaySC);
			
			String yyyymm = fDate.substring(0, 6);
			monthlyBillingLog = setMonthlyBillingLog(yyyymm, avgDaySC);
			saveMonthlyBillingList.add(monthlyBillingLog);
		}
		
		Calendar opar = Calendar.getInstance();
		opar.setTime(DateTimeUtil.getDateFromYYYYMMDD(monthlyBillingLog.getYyyymm() + "01"));
		opar.add(Calendar.MONTH, 1);
		
		Calendar now = Calendar.getInstance();
		while(true) {
			if(now.before(opar)) {
				break;
			} 
			
			String yyyymm = DateTimeUtil.getDateString(opar.getTime(), "yyyyMM");
			MonthlyBillingLog monthlyLog = setMonthlyBillingLog(yyyymm, new BigDecimal(String.valueOf(serviceCharge)));
			saveMonthlyBillingList.add(monthlyLog);
			opar.add(Calendar.MONTH, 1);
		}
	
		double balance = StringUtil.nullToDoubleZero(contract.getCurrentCredit());
		log.debug("before balance : " + balance);
		
		log.debug("saveMonthlyBillingLog : " + saveMonthlyBillingList.size());
		if(saveMonthlyBillingList.size() > 0) {
			for(MonthlyBillingLog mb : saveMonthlyBillingList) {
				double afterBalance = balance - mb.getServiceCharge();
				
				mb.setBeforeCredit(balance);
				mb.setCurrentCredit(afterBalance);

				balance = afterBalance;
				//monthlyBillingLogDao.add(mb);
				log.debug(mb.toString());
			}
			
			contract.setCurrentCredit(balance);
			log.debug("after balance : " + balance);
			//contractDao.update(contract);
		}
	}
	
	private BigDecimal getBigDecimal(String val, RoundingMode roundingMode, int digit) {
		return new BigDecimal(val).setScale(digit, roundingMode);
	}
	
	private MonthlyBillingLog setMonthlyBillingLog(String yyyymm, BigDecimal sc) {
		MonthlyBillingLog m = new MonthlyBillingLog();
		
		m.setContractId(contract.getId());
		m.setYyyymm(yyyymm);
		m.setMdsId(meter.getMdsId());
		m.setServiceCharge(sc.doubleValue());
		m.setWriteDate(DateTimeUtil.getDateString(new Date()));
		
		return m;
	}
}
