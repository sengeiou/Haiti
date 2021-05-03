package com.aimir.schedule.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.fep.logger.snowflake.SnowflakeGeneration;
import com.aimir.fep.sms.edh.SendSMS_EDH_V2;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.util.DateTimeUtil;

@Service
public class HaitiSMSTask extends ScheduleTask {
	protected static Log log = LogFactory.getLog(HaitiSMSTask.class);
	
	@Resource(name = "transactionManager")
	HibernateTransactionManager txmanager;
	
	@Autowired
	ContractDao contractDao;
	
    public static int totalExecuteCount = 0;
    public static int currentCount = 0;
	
	private boolean isNowRunning = false;
	
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-public.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        SnowflakeGeneration.getInstance();
        
        String mdevId = null;
        String smsType = null;
        if(args.length >= 2 ) {
	        for(int i=0; i < args.length; i +=2 ) {
	        	String nextArg = args[i];
	        	
	        	if (nextArg.startsWith("-mdevId")) {
	        		mdevId = new String(args[i+1]);
	        	} else if (nextArg.startsWith("-smsType")) {
	        		smsType = new String(args[i+1]);
	            } 
	        }
	        
	        log.info("smsType : " + smsType+", mdevId : " +mdevId);
        }
        
        HaitiSMSTask task = ctx.getBean(HaitiSMSTask.class);
        task.execute(ctx, mdevId, smsType);
        System.exit(0);
	}
	
	private void execute(ApplicationContext ctx, String mdevId, String smsType) {
		if(isNowRunning){
            log.info("########### EDH SMS Task already running...");
            return;
        }
        isNowRunning = true;
        log.info("########### START EDH SMS Task ###############");
        
        List<Contract> targets = getTargetContract(mdevId);
        if(targets == null || targets.size() == 0) {
        	log.info("target contract empty!! sms task finish!!!");
        	return;
        }
        
        log.info("# target contract cnt : "+targets.size());
        totalExecuteCount = targets.size();
        
        int poolSize = Integer.parseInt(FMPProperty.getProperty("edh.sms.thread.pool.size", ""+6));
        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 10, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        
        for(Contract co : targets) {
        	try {
        		executor.execute(new SMSTaskSubClz(co.getId(), smsType));
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
        
        log.info("########### END EDH SMS Task ############");
        isNowRunning = false;        
	}
	
	private List<Contract> getTargetContract(String mdevId) {
		TransactionStatus txstatus = null;
		List<Contract> targetList = new ArrayList<Contract>();
		
		try {
			txstatus = txmanager.getTransaction(null);
			
			targetList = contractDao.getReqSendSMSList(mdevId);
			
			txmanager.commit(txstatus);
		}catch(Exception e) {
			log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
		}
		return targetList;
	}
	
	@Override
	public void execute(JobExecutionContext context) { }

}

class SMSTaskSubClz implements Runnable {
	
	private static Log log = LogFactory.getLog(SMSTaskSubClz.class);
	
	private HibernateTransactionManager txmanager;
			
	private Contract contract;
	private Meter meter;
	private Customer customer;
	
	private ContractDao contractDao;
	private MeterDao meterDao;
	private CustomerDao customerDao;
	private TariffTypeDao tariffTypeDao;
	private TariffEMDao tariffEMDao;
	
	private SEND_SMS_TYPE sendType = SEND_SMS_TYPE.WHOLESALE;
	
	private Integer contractId = null;
	private SendSMS_EDH_V2 sender = null;
	
	enum SEND_SMS_TYPE {
		DIRECT,
		WHOLESALE //default
	}
	
	SMSTaskSubClz(Integer contractId, String smsType) {
		txmanager = (HibernateTransactionManager)DataUtil.getBean("transactionManager");
		
		contractDao = DataUtil.getBean(ContractDao.class);
		meterDao = DataUtil.getBean(MeterDao.class);
		customerDao = DataUtil.getBean(CustomerDao.class);
		tariffTypeDao = DataUtil.getBean(TariffTypeDao.class);
		tariffEMDao = DataUtil.getBean(TariffEMDao.class);
		
		for(SEND_SMS_TYPE s : SEND_SMS_TYPE.values()) {
			if(s.name().equalsIgnoreCase(smsType)) {
				sendType = s;
				break;
			}
		}
		
		switch(sendType) {
		case DIRECT:
			sender = (SendSMS_EDH_V2) DataUtil.getBean("SMSDirectAtomparkFactory");
			break;
		case WHOLESALE:
			sender = (SendSMS_EDH_V2) DataUtil.getBean("SMSWholesaleAtomparkFactory");
			break;
		}
		
		this.contractId = contractId;
		SnowflakeGeneration.getInstance();
	}
		
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		SnowflakeGeneration.getId();
		
		synchronized (this) {
			HaitiSMSTask.currentCount++;
		}
		
		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);
			
			contract = contractDao.get(contractId);
			if(contract != null) {
				meter = contract.getMeter();
				customer = contract.getCustomer();
			}
			
			if(meter == null || customer == null || sender == null) {
				if(meter == null)
					log.error("contract id :" + contractId+" | meter is null");
				else if(customer == null)
					log.error("contract id :" + contractId+" | customer is null");
				else if(sender == null)
					log.error("sender is null | sendType : " + sendType.name());
				
				txmanager.commit(txstatus);
				return;
			}
		
			StringBuffer buffer = new StringBuffer();
			buffer.append("Nom Du Client : ").append(customer.getName()).append(",\n")
				.append("NIC Number : ").append(contract.getContractNumber()).append(",\n")
				.append("Courant Credit : ").append(contract.getCurrentCredit()).append("");
			
			if(sendType == SEND_SMS_TYPE.DIRECT) {
				buffer.append(" \n");
				buffer.append("* La charge mensuelle pour ").append(getFixedPrice()).append("HTG");
			}
			
			String mobileNumber = customer.getMobileNo() != null ? customer.getMobileNo() : customer.getMobileNumber();
						
			if(mobileNumber != null && !mobileNumber.isEmpty()) {
				log.info("mobileNumber : " + mobileNumber +", message : " + buffer.toString());
				sender.send(mobileNumber, buffer.toString());
			} else {
				log.error("contract id :" + contractId+" | mobileNumber is null");
			}
			
			txmanager.commit(txstatus);
		} catch(Exception e) {			
    		log.error(e, e);
    		
    		if (txstatus != null) 
    			txmanager.rollback(txstatus);
		}
		
		synchronized (this) {
    		double rate = ((double) HaitiSMSTask.currentCount / (double) HaitiSMSTask.totalExecuteCount) * 100;

			long end = System.currentTimeMillis();
			long t = (end - start) / 1000;
			
			log.info("PROCESS CURRNET ["+String.format("%.3f", rate)+"%], TOTAL COUNT ["+HaitiSMSTask.totalExecuteCount+"], CURRENT COUNT ["+HaitiSMSTask.currentCount+"] "
					+ "time ["+t+"] ThreadName ["+Thread.currentThread().getName()+"] threadId ["+Thread.currentThread().getId()+"]");
		}
		
		SnowflakeGeneration.deleteId();
	}
	
	private String getFixedPrice() {
		Integer tariffIndexId = contract.getTariffIndexId();
		if(tariffIndexId == null)
			return "0";
		
		TariffType tariffType = tariffTypeDao.get(tariffIndexId);
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tariffIndex", tariffType);
		param.put("searchDate", DateTimeUtil.getDateString(new Date(), "yyyyMMdd"));
		
		List<TariffEM> list = tariffEMDao.getApplyedTariff(param);
		if(list == null || list.size() == 0)
			return "0";
				
		double maxSC = 0d;
		for(TariffEM e : list) {
			double sc = e.getServiceCharge();
			if(maxSC < sc)
				maxSC = sc;
		}
		
		return String.valueOf((int)maxSC);
	}
	
	
}
