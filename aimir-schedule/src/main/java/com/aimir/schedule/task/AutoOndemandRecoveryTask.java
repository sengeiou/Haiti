package com.aimir.schedule.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List; 
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.util.CmdUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.DateTimeUtil;

public class AutoOndemandRecoveryTask extends ScheduleTask {

    private static Log log = LogFactory.getLog(AutoOndemandRecoveryTask.class);
    private boolean onRunning = false;
    
    @Resource(name="transactionManager")
    HibernateTransactionManager txmanager;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
	CmdOperationUtil cmdOperationUtil;
	
	@Autowired
    AsyncCommandLogDao asyncCommandLogDao;
	
	@Autowired
    AsyncCommandParamDao asyncCommandParamDao;
		
	private final int TODAY = 0;
	private final int TWODAYAGO = -1;
	
	@Override
	public void execute(JobExecutionContext context) {
	}
	
	public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext((String)"/spring-AutoOndemandRecoveryTask.xml");
        DataUtil.setApplicationContext(ctx);
        AutoOndemandRecoveryTask task = ctx.getBean(AutoOndemandRecoveryTask.class);
        log.info("======================== AutoOndemandRecoveryTask start. ========================");
        try {
			task.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
        log.info("========================= AutoOndemandRecoveryTask end. =========================");
        System.exit(0);
    }
	
	public void execute() {
		if(onRunning){
            log.info("########### Automatic Ondemand Recovery Task is already running...");
            return;
        }
    	
    	log.debug("### Automatic Ondemand Recovery Task - AutoOndemandRecoveryTask Class execute ");
    	onRunning = true;
		
		try {
			actionTask();
		} catch(Exception e) {
			e.printStackTrace();
		}
    	
    	onRunning = false;
	}
    
	private void actionTask() throws Exception {
		TransactionStatus txstatus = null;	
		List<String> mdsIdList = getMissLpMeter();
				
		try{
			txstatus = txmanager.getTransaction(null);
			if(mdsIdList == null || mdsIdList.size() == 0)  {
				log.debug("missLpMeter Empty");
				return;
			}
			log.debug("### meterList length["+mdsIdList.size()+"] ###");
			String fromDate = null;
			String toDate = null;
			for(int i=0; i<mdsIdList.size(); i++) {
				String mdsId = mdsIdList.get(i);		
				Meter meter = meterDao.get(mdsId);
				if(meter.getModemId() == null) continue;
				Modem modem = modemDao.get(meter.getModemId());
				String nOption = "";
				Map<?, ?> result = null;
				
				if(modem != null) {
//					if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) {
//						//if( (modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS) ) {
//							if (fromDate != null && toDate != null) {
//								if (type == null ||  "METER".equals(type) ) {
//									result = this.onDemandMeterBypassMBB(meter.getMdsId(), fromDate, toDate, loginId);
//									log.info("send sms command : onDemandMeterBypassMBB");
//								}
//								else if ( "MODEM".equals(type)){
//									result = this.romReadBypassMBB(meter.getMdsId(), fromDate, toDate, loginId);
//									log.info("send sms command : romReadBypassMBB");
//								}
//							} else {
//								result = this.onDemandMeterBypassMBB(meter.getMdsId(), fromDate, toDate, loginId);
//			       				log.info("send sms command : onDemandMeterBypassMBB");
//							}
//
//					} else {
						log.debug("### modemType["+modem.getModemType()+"] , meterId["+mdsId+"] ,modemId["+modem.getDeviceSerial()+"]### ");
						fromDate = getDateString(TWODAYAGO) + "000000";
						toDate = getDateString(TODAY) + "235959";
						try
						{
							if (fromDate != null && toDate != null) {
								result = cmdOperationUtil.doOnDemand(meter, 0, "admin", nOption, fromDate, toDate);
							} else {
								result = cmdOperationUtil.doOnDemand(meter, 0, "admin", nOption, "", "");
							}
						}catch(Exception e){}
						log.info("###Ondemand["+modem.getDeviceSerial()+"] Command end###");						
//					}
				}
			}
			txmanager.commit(txstatus);
		}catch  (Exception e) {
    		if (txstatus != null&& !txstatus.isCompleted())
    			txmanager.rollback(txstatus);
    	}
	}
	
	private List<String> getMissLpMeter() throws Exception{
		
		Map<String,Object> conditionMap = new HashMap<String,Object>();
		List<String> mdsIdList = null;
		
		conditionMap.put("startDate", getDateString(TWODAYAGO));
		conditionMap.put("lpChannel", 1);
		conditionMap.put("lpType", "Meter");
		conditionMap.put("codeName", "Normal");
		conditionMap.put("code", "1.3.3.1");
		
		List<Map<String, Object>> data = meterDao.getMissLpMeter(conditionMap);
		log.debug("AutoOndemandRecoveryTRask Meter Query Length ["+data.size()+"]");
		
		if(data != null && data.size() > 0) {
			mdsIdList = new ArrayList<String>();
					
			for(int i=0; i<data.size(); i++) {
				Map<String, Object> dataMap = data.get(i);
				
				String mdsId = (String)dataMap.get("MDS_ID");
				if(mdsId != null && !mdsId.isEmpty()) {
					mdsIdList.add(mdsId);
				}
			}
		}
		
		return mdsIdList;
	}
	
	private String getDateString(int num) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();            
        cal.add(Calendar.DAY_OF_MONTH, num);
        
        return DateTimeUtil.getDateString(cal.getTime()).substring(0, 8);
	}
}
