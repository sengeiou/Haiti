package com.aimir.schedule.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.MeterDao;
import com.aimir.fep.command.ws.client.CommandWS;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.protocol.mrp.command.frame.sms.RequestFrame;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.GroupMember;
import com.aimir.schedule.command.CmdManager;
import com.aimir.util.TimeUtil;

public class GroupOndemandMeterTask extends GroupTask {
	protected static Log log = LogFactory.getLog(GroupOndemandMeterTask.class);

	@Autowired
	private MeterDao meterDao;
	
	@Autowired
	private HibernateTransactionManager tx;
	
	ThreadPoolExecutor executor = null;// Executors.newFixedThreadPool(50);
	
	@Override
	public void execute(JobExecutionContext context) {
		log.debug("GroupOndemandMeterTask");
		
        try {
            failMembers = new ArrayList<String>();
            // Quartz 스케줄러의 정책에 의해서 강제로 종료되므로 CallerRunsPolicy 를 무조건 사용해야 함.  
            executor = new ThreadPoolExecutor(10, 50, 10, TimeUnit.SECONDS, new LinkedBlockingQueue(), new ThreadPoolExecutor.CallerRunsPolicy());
			//그룹에 포함된 미터 목록
        	for(int i=0;i<this.groupMembers.length;i++){
        		GroupMember groupMember = this.groupMembers[i];

        		String memberID = groupMember.getMember();
				executor.execute(new GroupOndemandMeterTaskThread(memberID, this, meterDao, tx));
			}
			
        	executor.shutdown();
        	while (!executor.awaitTermination(10, TimeUnit.SECONDS)) ;
        	
			if(isSuccess()){
				setSuccessResult();
			}else{
				setFailResult();
			}
		}
        catch (Exception e) {
			log.error("Group ondemand meter Error");
			log.error(e,e);
			setFailResult(e.getMessage());
		}
	}
	
	public void setGroupMember(GroupMember[] groupMember) {
	    this.groupMembers = groupMember;
	}
}


class GroupOndemandMeterTaskThread implements Runnable {
    private static Log log = LogFactory.getLog(GroupOndemandMeterTaskThread.class);
    
    private String meterId;
    private GroupOndemandMeterTask task;
    
    MeterDao meterDao;
    
    HibernateTransactionManager tx;
    
    GroupOndemandMeterTaskThread(String meterId, GroupOndemandMeterTask task,
            MeterDao meterDao, HibernateTransactionManager tx) {
        this.meterId = meterId;
        this.task = task;
        this.meterDao = meterDao;
        this.tx = tx;
    }

    @Override
    public void run() {
        TransactionStatus txStatus = null;
        try {
            txStatus = tx.getTransaction(null);
            
            Meter meter = meterDao.get(meterId);
            if(meter!=null){
                Modem modem = meter.getModem();

                Calendar c = Calendar.getInstance();
                
                Date toDate = c.getTime();
                
                Date fromDate = c.getTime();
                
                String dayPattern = "yyyyMMdd";
                
                SimpleDateFormat sf = new SimpleDateFormat(dayPattern);
                
                //주기적인 검침 이기때문에 날짜 조건을 오늘 날짜로 한다.
                String startDate =sf.format(fromDate);
                String endDate = sf.format(toDate);
                
                //시작 시간과 종료 시간을 설정.
                startDate = startDate + "000000";
                endDate = endDate + "235959";
    
                log.debug(String.format(
                                        "Modem[%s], Meter[%s], OnDemand fromDate[%s], toDate[%s]",
                                        modem.getDeviceSerial(), 
                                        meter.getMdsId(), 
                                        fromDate,
                                        toDate));
                
                // CmdOperationUtil cou = DataUtil.getBean(CmdOperationUtil.class);
                Map mapResult = doOnDemand(meter, 0, "admin", null, startDate, endDate);
                
                //result 확인
                if(mapResult.containsKey("result")){
                    String strResult = mapResult.get("result").toString();
                    
                    //실패 판단
                    if("Failure".equals(strResult)){
                        task.addFailMember(meterId);
                    }
                }
            }
            tx.commit(txStatus);
        }
        catch(Exception e){
            log.debug(e,e);
            task.addFailMember(meterId);
            
            if (txStatus != null) {
                tx.rollback(txStatus);
            }
        }
    }
    
    
    private Map doOnDemand(Meter meter, int serviceType, String operator,
            String nOption, String fromDate, String toDate) throws Exception {
        log.info("doOnDemand meter[" + meter.getMdsId() + "]");
        String sensorInstanceName = null;
        Map result = new HashMap();
        
        fromDate = (fromDate == null || "".equals(fromDate)) ? TimeUtil
                .getCurrentDay()
                : fromDate;
        toDate = (toDate == null || "".equals(toDate)) ? TimeUtil
                .getCurrentDay()
                : toDate;

        try {
            CommandWS gw = null;

            Modem modem = meter.getModem();
            String meterId = meter.getMdsId();
            String modemId = modem.getId().toString();

            String mcuId = null;
            if (modem.getMcu() != null)
                mcuId = modem.getMcu().getSysID();
            
            ModemType modemType = ModemType.Unknown;
            if (modem != null) {
                modemType = modem.getModemType();
            }

            MeterData emd = null;

            if (modemType == ModemType.MMIU || modemType == ModemType.IEIU || modemType== ModemType.Converter_Ethernet)
            {
                gw = CmdManager.getCommandWS(meter.getModem().getProtocolType());
                        
                if (Protocol.SMS.equals(meter.getModem().getProtocolType())) {
                	log.debug("SMS Protocol");
                    int seq = new Random().nextInt(100) & 0xFF;
            
                    List<String> list = new ArrayList<String>();
                    
                    list.add(RequestFrame.CMD_ONDEMAND);
                    list.add(String.valueOf(seq));
                    list.add(RequestFrame.BG);
                    list.add(fromDate);
                    list.add(toDate);

                    gw.cmdSendSMS(modem.getDeviceSerial(), list);

                } else {
                    emd = gw.cmdOnDemandMeter2(mcuId, meterId, meter.getModem().getDeviceSerial(), "0", fromDate, toDate);
                }
                //else {
                //    log.debug("fromDate:" + fromDate + " toDate:" + toDate);
                //    gw.cmdOnDemandMeter(meterId, fromDate, toDate);
                //}

            }
            // MBus
            else if (modemType == ModemType.ZEU_MBus) {
                gw = CmdManager.getCommandWS(getProtocolType(modem));
                
                String nPort = meter.getModemPort() + "";
                emd = gw.cmdOnDemandMBus(mcuId, meterId, modemId, nPort, nOption, fromDate, toDate);
            }
            else {
                modem = meter.getModem();

                modemId = modem.getDeviceSerial();
                
                gw = CmdManager.getCommandWS(getProtocolType(modem));
                // if (revision.compareTo("2688") >= 0 && isAsynch(modem)) {
                sensorInstanceName = modem.getDeviceSerial();
                emd = gw.cmdOnDemandMeter2(mcuId, meterId, modemId, nOption, fromDate, toDate);
            }

            result.put("result", "Success");

            if (sensorInstanceName != null) {
                modem.setCommState(1);
            }

        } catch (Exception ex) {
            
            log.error("Error Message:"+ex.getMessage());
            String errorMessage = "Meter Busy! - current meter reading";
            String exMessage = "";
            if(ex.getMessage().indexOf("com.aimir.fep.protocol.fmp.exception.FMPMcuException") >=0){
                int idx = ex.getMessage().indexOf(":");
                exMessage = ex.getMessage().substring(idx+1);//left trim delete
            }else if(ex.getMessage().indexOf("java.lang.Exception") >=0){
                int idx = ex.getMessage().indexOf(":");
                exMessage = ex.getMessage().substring(idx+1);//left trim delete
            }else{
                exMessage = ex.getMessage();
            }

            if(exMessage.indexOf("IF4ERR_RETURN_DATA_EMPTY") >= 0){
                errorMessage = "Read timeout!- network leave or device reset";
            }else if(exMessage.indexOf("IF4ERR_") >= 0){
                errorMessage = exMessage.substring(7);
            }else if(exMessage.indexOf("Interval Server") >= 0 || ex.getMessage().indexOf("Null") >= 0){
                errorMessage = "No Target Communication Information(Modem, DCU's IP Address or Port, Meter Model Information) !!";
            }else{
                errorMessage = exMessage;
            }
            result.put("meterValue", "");
            result.put("result", "Failure["+errorMessage+"]");
            result.put("detail", "<html></html>");
            log.error(ex, ex);
        }
        
        return result;
    }
    
    private String getProtocolType(Modem modem) {
        MCU mcu = modem.getMcu();
        
        if (mcu != null)
            return mcu.getProtocolType().getName();
        
        if (modem != null)
            return modem.getProtocolType().name();
        
        return Protocol.LAN.name();
    }
}
