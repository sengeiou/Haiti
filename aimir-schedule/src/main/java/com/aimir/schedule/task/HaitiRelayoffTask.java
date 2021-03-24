package com.aimir.schedule.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.fep.logger.snowflake.SnowflakeGeneration;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.OperationLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.schedule.task.HaitiRelayoffTask.I210PLUS_RELAY_ACTION;
import com.aimir.util.DateTimeUtil;

@Service
public class HaitiRelayoffTask extends ScheduleTask {
	protected static Log log = LogFactory.getLog(HaitiRelayoffTask.class);
	
	private final int[] BREAK_REALY_OFF_FREEDAYS = new int[] {1, 7};
	private final int BREAK_RELAY_OFF_HOUR = 18;
	
	@Resource(name = "transactionManager")
	private HibernateTransactionManager txmanager;
	
	@Autowired
	private MeterDao meterDao;
	
    public static int totalExecuteCount = 0;
    public static int currentCount = 0;
	
	private boolean isNowRunning = false;
	
	enum I210PLUS_RELAY_ACTION {
		RELAY_OFF(1),
		RELAY_ON(15);
		
		int code;
		I210PLUS_RELAY_ACTION(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
	}
	
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-public.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        String mdevId = null;
        String dcuSysId = null;
        if(args.length >= 2 ) {
	        for(int i=0; i < args.length; i +=2 ) {
	        	String nextArg = args[i];
	        	
	        	if (nextArg.startsWith("-mdevId")) {
	        		mdevId = new String(args[i+1]);
	            }else if (nextArg.startsWith("-dcuSysId")) {
	            	dcuSysId = new String(args[i+1]);
	            }  
	        }
	        
	        log.info("mdevId : " + mdevId+", dcuSysId : " +dcuSysId);
        }
        
        HaitiRelayoffTask task = ctx.getBean(HaitiRelayoffTask.class);
        task.execute(ctx, mdevId, dcuSysId);
        System.exit(0);
	}

	@Override
	public void execute(JobExecutionContext context) { }
	
	private void execute(ApplicationContext ctx, String mdevId, String dcuSysId) {
		if(isNowRunning){
            log.info("########### EDH Realy off already running...");
            return;
        }
		
		if(checkFreedays()) {
			log.info("Can't relay off because it's the weekend.");
			return;
		}
		
		if(checkBreakRelayTime()) {
			log.info("Can't relay off because it's the after " + BREAK_RELAY_OFF_HOUR +" o'clock " );
			return;
		}
		
		if(checkHoliday()) {
			log.info("Can't relay off because it's the holiday. " );
			return;
		}
		
        isNowRunning = true;
        log.info("########### START Realy off Task ###############");
        
        Map<String, List<String>> targets = getTargetMeters(mdevId, dcuSysId);
        if(targets == null || targets.size() == 0) {
        	log.info("target contract empty!! sms task finish!!!");
        	return;
        }
        
        log.info("# total size : " + totalExecuteCount +", map Size : " + targets.size());
        
        int poolSize = Integer.parseInt(FMPProperty.getProperty("edh.sms.thread.pool.size", ""+6));
        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 10, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        
        for(String key : targets.keySet() ){
        	try {
        		executor.execute(new HaitiRelayoffTaskSubClz(targets.get(key)));
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
	
	//1=일요일, 2=월요일 ~ 7=토요일 
	private boolean checkFreedays() {
		Calendar calendar = Calendar.getInstance();
		int dayofWeek = calendar.get(Calendar.DAY_OF_WEEK);
		
		if(Arrays.stream(BREAK_REALY_OFF_FREEDAYS).boxed().collect(Collectors.toList()).contains(dayofWeek))
			return true;
				
		return false;
	}
	
	private boolean checkBreakRelayTime() {
		String strHH = DateTimeUtil.getDateString(new Date(), "HH");
		int hh = Integer.parseInt(strHH);
		
		if(hh >= BREAK_RELAY_OFF_HOUR) {
			return true;
		}
		
		return false;
	}
	
	private boolean checkHoliday() {
		return false;
	}
	
	private Map<String, List<String>> getTargetMeters(String mdevId, String dcuSysId) {
		TransactionStatus txstatus = null;
		Map<String, List<String>> targets = null;
		
		try {
			txstatus = txmanager.getTransaction(null);
			/*
			List<Map<String, Object>> queryResult = meterDao.getRelayOnOffMeters("relayoff", dcuSysId, mdevId);
			if(queryResult == null || queryResult.size() == 0)
				return null;

			totalExecuteCount = queryResult.size();
			
			targets = new HashMap<String, List<String>>();			
			for(Map<String, Object> map : queryResult) {
				String protocolType = String.valueOf(map.get("protocol_type"));
				String mdsId = String.valueOf(map.get("mds_id"));
				String mcuId = String.valueOf(map.get("mcu_id"));
				String contractId = String.valueOf(map.get("contract_id"));
				
				if(mcuId == null || mcuId.isEmpty()) { //GPRS
					List<String> l = new ArrayList<String>();
					l.add(mdsId);
					targets.put(mdsId, l);
					continue;
				}
				
				//RF, Zigbee, Subgiga
				List<String> meterList = null;
				if(targets.containsKey(mcuId)) 
					meterList = targets.get(mcuId);
				else 
					meterList = new ArrayList<String>();
				
				meterList.add(mdsId);
				targets.put(mcuId, meterList);
			}
			*/
			
			targets = new HashMap<String, List<String>>();
			
			List<String> l = new ArrayList<String>();
			l.add("810312254");
			
			targets.put("0", l);
			
			txmanager.commit(txstatus);
		}catch(Exception e) {
			log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
		}
		
		return targets;
	}
	
}



class HaitiRelayoffTaskSubClz implements Runnable {
	private final static Log log = LogFactory.getLog(HaitiRelayoffTaskSubClz.class);
	private final String[] EXCEPTION_MESSAGE = new String[] {"IF4ERR_BUSY", "IF4ERR_CANNOT_CONNECT", "Can't connect to DCU"};
	
	private HibernateTransactionManager txmanager;
	private ModemDao modemDao;
	private MeterDao meterDao;
	private CodeDao codeDao;
	private OperationLogDao operationLogDao;
	private CmdOperationUtil cmdOperationUtil;
	
	private Modem modem;
	private Meter meter;
	private MCU mcu;
	
	private List<String> meterList = null;
	
	HaitiRelayoffTaskSubClz(List<String> meterList) {
		txmanager = (HibernateTransactionManager)DataUtil.getBean("transactionManager");
		meterDao = DataUtil.getBean(MeterDao.class);
		modemDao = DataUtil.getBean(ModemDao.class);
		codeDao = DataUtil.getBean(CodeDao.class);
		operationLogDao = DataUtil.getBean(OperationLogDao.class);
		cmdOperationUtil = DataUtil.getBean(CmdOperationUtil.class);
		
		this.meterList = meterList;
	}
	
	@Override
	public void run() {
		for(int i=0; i<meterList.size(); i++) {
			String m = meterList.get(i);
			
			TransactionStatus txstatus = null;
			try {
				txstatus = txmanager.getTransaction(null);
				
				boolean isContinue = actionRelayOff(m);
				if(!isContinue) {
					synchronized (this) {
						HaitiRelayoffTask.currentCount = HaitiRelayoffTask.currentCount + (meterList.size() - 1);
						
			    		double rate = ((double) HaitiRelayoffTask.currentCount / (double) HaitiRelayoffTask.totalExecuteCount) * 100;
						log.info("PROCESS CURRNET ["+String.format("%.3f", rate)+"%], TOTAL COUNT ["+HaitiRelayoffTask.totalExecuteCount+"], CURRENT COUNT ["+HaitiRelayoffTask.currentCount+"]");
						return;
					}
				}
				
				Thread.sleep(3 * 1000);
				txmanager.commit(txstatus);
			}catch(Exception e) {
				log.error(e,e);
				
				if (txstatus != null) 
	    			txmanager.rollback(txstatus);
			}
		}
	}
	
	private boolean actionRelayOff(String mdsId) throws Exception {
		long start = System.currentTimeMillis();
		SnowflakeGeneration.getId();
		
		try {
			meter = meterDao.get(mdsId);
			
			if(meter == null) {
				log.info("meterId : " + mdsId +", meter is null! please check meter");
				return true;
			}
			
			if("I210+".equals(meter.getModel().getName())) {
				if(!actionOffByI210Plus()) {
					return false;
				}
			} 
		}catch(Exception e) {
			throw e;
		}
		
		synchronized (this) {
			HaitiRelayoffTask.currentCount++;
    		double rate = ((double) HaitiRelayoffTask.currentCount / (double) HaitiRelayoffTask.totalExecuteCount) * 100;

			long end = System.currentTimeMillis();
			long t = (end - start) / 1000;
			
			log.info("PROCESS CURRNET ["+String.format("%.3f", rate)+"%], TOTAL COUNT ["+HaitiRelayoffTask.totalExecuteCount+"], CURRENT COUNT ["+HaitiRelayoffTask.currentCount+"] "
					+ "time ["+t+"] ThreadName ["+Thread.currentThread().getName()+"] threadId ["+Thread.currentThread().getId()+"]");
		}
		
		SnowflakeGeneration.deleteId();
		return true;
	}
	
	/*
	 * 만약 DCU 접속에러가 떨어진다면 false을 리턴한다.
	 * return value : 
	 * 	true : meters 변수에 저장된 list의 다음 meter의 relay off을 진행한다.
	 *  false : meters 변수에 저장된 list에 대해서 더이상 relay off을 진행하지 않는다.
	 */
	private boolean actionOffByI210Plus() throws Exception {
		//only GE Vendor - I210+ 
		boolean retValue = true;
		
		modem = meter.getModem();
		if(modem == null) {
			log.info("modem is null. meter(" + meter.getMdsId()+") relay off fail. please check modem.");
			return true;
		}
		
		mcu = modem.getMcu();
		if(mcu == null) {
			log.info("mcu is null. meter(" + meter.getMdsId()+") relay off fail. please check mcu.");
			return true;
		}
		
		ResultStatus status = ResultStatus.FAIL;
		String errorReason = null;
		try {
			cmdOperationUtil.cmdSetEnergyLevel(mcu.getSysID(), modem.getDeviceSerial(), I210PLUS_RELAY_ACTION.RELAY_OFF.getCode());
			
			Thread.sleep(15 * 1000);
			
			int relayStatus = cmdOperationUtil.cmdGetEnergyLevel(mcu.getSysID(), modem.getDeviceSerial());
			if(relayStatus == I210PLUS_RELAY_ACTION.RELAY_OFF.getCode()) {
				status = ResultStatus.SUCCESS;
				Code code = codeDao.getCodeIdByCodeObject(MeterStatus.CutOff.getCode());
				
				if(code != null) {
					meter.setMeterStatus(code);
					modem.setLastLinkTime(DateTimeUtil.getDateString(new Date()));
					
					meterDao.update(meter);
					modemDao.update(modem);
				}
			} 
		}catch(Exception e) {
			errorReason = e.getMessage();
			boolean isStop = Arrays.stream(EXCEPTION_MESSAGE).anyMatch(errorReason::equals);
			log.info("errorReason : " + errorReason +", isStop : " +isStop);
			
			if(isStop) {
				retValue = false;
			}
		}

		saveOperationLog(meter.getSupplier(), meter.getMeterType(), meter.getMdsId(), 
				"balance-schedule", status, errorReason, I210PLUS_RELAY_ACTION.RELAY_OFF.name());
		
		return retValue;	
	}
	
	private void saveOperationLog(Supplier supplier, Code targetTypeCode, String targetName, String userId, ResultStatus status, String errorReason, String command) {
		Code operationCode = codeDao.getCodeIdByCodeObject("8.1.10"); // 8.1.10 Relay Off
		
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar today = Calendar.getInstance();
        String currDateTime = sdf.format(today.getTime());
        
		OperationLog opLog = new OperationLog();
		opLog.setOperatorType(1);//operator
		opLog.setOperationCommandCode(operationCode);
		opLog.setYyyymmdd(currDateTime.substring(0,8));
		opLog.setHhmmss(currDateTime.substring(8,14));
		opLog.setYyyymmddhhmmss(currDateTime);
		opLog.setDescription("");
		opLog.setErrorReason(errorReason);
		opLog.setResultSrc(status.name());
		opLog.setStatus(status.getCode());
		opLog.setTargetName(targetName);
		opLog.setTargetTypeCode(targetTypeCode);
		opLog.setUserId(userId);
		opLog.setSupplier(supplier);
		
		operationLogDao.add(opLog);
	}
	
}
