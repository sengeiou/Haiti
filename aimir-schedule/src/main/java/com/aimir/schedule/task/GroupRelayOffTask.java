package com.aimir.schedule.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.OperationLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.GroupMember;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class GroupRelayOffTask extends GroupTask {
    protected static Log log = LogFactory.getLog(GroupRelayOffTask.class);
	
    @Resource(name="transactionManager")
    HibernateTransactionManager txManager;

	@Autowired
	MeterDao meterDao;
	
	@Autowired
	ModemDao modemDao;
	
	@Autowired
	MCUDao mcuDao;
	
	@Autowired
	OperationLogDao operationLogDao;
	
	@Autowired
	CmdOperationUtil cmdOperationUtil;
	
	@Autowired
	CodeDao codeDao;
	
    private boolean isNowRunning = false;
    
    final String WRONGGROUP = "wrongParam";
	
    @Override
    public void execute(JobExecutionContext context) {
        if(isNowRunning){
            log.info("########### GroupRelayOFFTask is already running...");
            return;
        }
        isNowRunning = true;
        
        log.info("##########GroupRelayOFFTask START #############");
        
        TransactionStatus txStatus = null;
        Map<String, List<Meter>> groupDCU = new HashMap<String, List<Meter>>();
    	List<Meter> meterList = null;
    	Meter meter = null;
        //그룹에 포함된 미터 목록
        for(int i=0;i<this.groupMembers.length;i++){
        	try {
        		txStatus = txManager.getTransaction(null);
	        	GroupMember groupMember = this.groupMembers[i];
	            String memberID = groupMember.getMember();
	            
	            meter = meterDao.get(memberID);
	        	
	            MCU mcu = new MCU();
	            if(meter != null && meter.getModem() != null && meter.getModem().getMcu() != null) {
	            	mcu = meter.getMcu();
	            } else {
	            	mcu.setSysID(WRONGGROUP);
	            	Modem modem = new Modem();
	            	modem.setMcu(mcu);
	            	meter = new Meter();
	            	meter.setMdsId(memberID);
	            	meter.setModem(modem);
	            }
	            
	            meterList = groupDCU.get(mcu.getSysID());
	            if(meterList == null) {
	            	meterList = new ArrayList<Meter>();
	            }
	            
	            meterList.add(meter);
	            groupDCU.put(mcu.getSysID(), meterList);
	    		txManager.commit(txStatus);
            } catch (Exception e) {
                log.error(e,e);
                txManager.commit(txStatus);
            }
        }
        
        String mcuId = null;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 3, TimeUnit.MINUTES, new LinkedBlockingQueue());
        for (Iterator<String> i = groupDCU.keySet().iterator(); i.hasNext();) {
            mcuId = i.next();
            try {
                executor.execute(new RelayOffThread(mcuId, groupDCU.get(mcuId), WRONGGROUP));
            }
            catch (Exception e) {
                log.error(e, e);
            }
        }
        try {
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
        }
        catch (Exception e) {}
        
        log.info("##########GroupRelayOFFTask END #############");
        
        isNowRunning = false;
        
    }
}
    
class RelayOffThread implements Runnable {
    private static Log log = LogFactory.getLog(BalanceCheckThread.class);
    
    HibernateTransactionManager txManager = (HibernateTransactionManager)DataUtil.getBean("transactionManager");
    
    private String mcuId;
    private List<Meter> meterList;
    String WRONGGROUP = null;
    Properties messageProp = null;
    
    RelayOffThread(String mcuId, List<Meter> meterList, String WRONGGROUP) {
        this.mcuId = mcuId;
        this.meterList = meterList;
        this.WRONGGROUP = WRONGGROUP;
    }
    
    @Override
    public void run() {
        log.info("########## start thread MCU[" + mcuId + "] Meter_size[" + meterList.size() + "] ##########");
        String mcuStatus = null;
        Boolean cannotConnectDCU = false;
        for (Meter meter : meterList) {
        	if(!cannotConnectDCU) {
	            mcuStatus = relayOffMethod(meter);
	            // 집중기 상태가 연결이 안되는 경우 종료한다.
	            if (mcuStatus != null && mcuStatus.equals("Can't connect to DCU")) {     
	            	cannotConnectDCU = true;
	                log.warn("[MCU:" + mcuId + " MdsId:" + meter.getMdsId() + "] break checking.");
	            }
        	} else {
                ResultStatus status = ResultStatus.FAIL;
                MCU mcu = meter.getMcu();
                Supplier supplier = meter.getSupplier();
                Integer meterTypeCodeId = meter.getMeterTypeCodeId();
                log.debug("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() + "] Relay Off Status [" + status + "], Can't conntent to DCU");
        		saveOperationLog(supplier, meterTypeCodeId, meter.getMdsId(), "groupRelayOff_Schedule", status.getCode(), "Can't connect to DCU");
        	}
        }
        log.info("########## End thread MCU[" + mcuId + "] Meter_size [" + meterList.size() + "] ##########");
    }
    
    public String relayOffMethod(Meter meter) {
    	log.info("Relay OFF START GroupMember[" + meter.getMdsId() + "]");
        ResultStatus status = ResultStatus.FAIL;
        TransactionStatus txStatus = null;
        MCU mcu = null;
        Supplier supplier = null;
        Integer meterTypeCodeId = null;
        String mcuStatus = null;

        try{
            txStatus = txManager.getTransaction(null);
        	supplier = meter.getSupplier();
        	mcu = meter.getMcu();
        	meterTypeCodeId = meter.getMeterTypeCodeId();
        	txManager.commit(txStatus);
            if(!WRONGGROUP.equals(mcu.getSysID())) {
            	CmdOperationUtil cmdOperationUtil = DataUtil.getBean(CmdOperationUtil.class);
                Map<String, Object> resultMap = cmdOperationUtil.relayValveOff(mcu.getSysID(), meter.getMdsId());
                Object[] values = resultMap.values().toArray(new Object[0]);
                
                JsonParser jparser = new JsonParser();
                JsonArray ja = null;
                for (Object o : values) {
                    ja = jparser.parse((String)o).getAsJsonArray();
                    for (int i = 0; i < ja.size(); i++) {
                        if (ja.get(i).getAsJsonObject().get("name").getAsString().equals("Result") 
                        		|| ja.get(i).getAsJsonObject().get("value").getAsString().equals("Relays disconnected by command") 
                        		|| ja.get(i).getAsJsonObject().get("value").getAsString().equals("SUCCESS : Send SMS Command(RelayOff)")) {
                            status = ResultStatus.SUCCESS;
                            break;
                        }
                        else if (ja.get(i).getAsJsonObject().get("value").getAsString().equals("Can't connect to DCU")) {
                            mcuStatus = "Can't connect to DCU";
                            break;
                        }
                        else {
                            log.warn("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() + "] RelayValveOff Fail - " + ja.get(i).getAsJsonObject().get("value").getAsString());
                        }
                    }
                    if (status == ResultStatus.SUCCESS) break;
                }
                
                log.debug("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() + "] Relay Off Status [" + status + "]");
            }

        } catch (Exception e) {
            log.error("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() + "] Relay Off Status [" + status + "]", e);       
            status = ResultStatus.FAIL;
            txManager.commit(txStatus);
        }
        
        try {
            txStatus = txManager.getTransaction(null);
            // Operation Log에 기록
            saveOperationLog(supplier, meterTypeCodeId, meter.getMdsId(), "groupRelayOff_Schedule", status.getCode(), mcuStatus != null ? mcuStatus : status.name());
            txManager.commit(txStatus);
            return mcuStatus;
        } catch(Exception e) {
            log.error("[DCU:" + mcu.getSysID() + " METER:" + meter.getMdsId() + "] Relay Off Status [" + status + "]", e);       
            txManager.commit(txStatus);
        }
        return null;
    }


    
    private void saveOperationLog(Supplier supplier, Integer targetTypeCodeId,
            String targetName, String userId, Integer status, String errorReason){
        TransactionStatus txStatus = null;
        try {
        	txStatus = txManager.getTransaction(null);
        	CodeDao codeDao = DataUtil.getBean(CodeDao.class);
        	OperationLogDao operationLogDao = DataUtil.getBean(OperationLogDao.class);
            Code operationCode = codeDao.getCodeIdByCodeObject("8.1.10");
            
            Code meterTypeCode = codeDao.findByCondition("id",targetTypeCodeId);
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar today = Calendar.getInstance();
            String currDateTime = sdf.format(today.getTime());
    
            OperationLog operationLog = new OperationLog();
    
            operationLog.setOperatorType(1);//operator
            operationLog.setOperationCommandCode(operationCode);
            operationLog.setYyyymmdd(currDateTime.substring(0,8));
            operationLog.setHhmmss(currDateTime.substring(8,14));
            operationLog.setYyyymmddhhmmss(currDateTime);
            operationLog.setDescription("");
            operationLog.setErrorReason(errorReason);
            operationLog.setResultSrc("");
            operationLog.setStatus(status);
            operationLog.setTargetName(targetName);
            operationLog.setTargetTypeCode(meterTypeCode);
            operationLog.setUserId(userId);
            operationLog.setSupplier(supplier);
            operationLogDao.add(operationLog);
            txManager.commit(txStatus);
        }
        catch (Exception e) {
            log.error(e, e);
            
            if (txStatus != null) {
                try {
                    txManager.rollback(txStatus);
                }
                catch (Exception te) {}
            }
        }
    }
}