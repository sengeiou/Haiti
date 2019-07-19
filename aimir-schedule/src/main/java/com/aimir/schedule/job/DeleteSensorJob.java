package com.aimir.schedule.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.fep.command.ws.client.CommandWS;
import com.aimir.model.device.Modem;
import com.aimir.model.device.OperationLog;
import com.aimir.schedule.command.CmdManager;

public class DeleteSensorJob extends AimirJob
{
    private static Log log = LogFactory.getLog(DeleteSensorJob.class);
    
    private static final String description = "com.aimir.schedule.job.DeleteSensorJob";
    private static final String[] paramList = null;
    private static final String[] paramListDescription = null;
    private static final boolean[] paramListRequired = null;
    private static final String[] paramListDefault = null;

    private static boolean isRun = false;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    OperationLogDao operationLogDao;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException
    {
		
        OperationLog operationLog = null;

        
        try {
            if(!isRun)
            {
                isRun = true;

                Map<String, String> condition = new HashMap<String, String>();
                condition.put("operationCommandCode.id",codeDao.getCodeIdByCode(TargetClass.Modem.getCode())+""); //TODO define code delete sensor command
                condition.put("status",null);
                condition.put("targetTypeCode.id",codeDao.getCodeIdByCode(TargetClass.Modem.getCode())+"");
                List<OperationLog> list = operationLogDao.getGridData(condition);   
                //modem에서 집중기아이디가 바뀌는 시점에 operationLog에 명령을 내려줘야 한다. 
                //스케줄잡은 operationLog에 저장된 타입중 집중기에 지워야할 목록을 찾아 냄
                String mcuId = null;
                String modemId = null;
                int tryCnt = 0;
                String descr = null;
                int fromIndex = 0;
                int endIndex = 0;
                Modem sensor = null;
                
                for (int i = 0; i < list.size(); i++) {
                	operationLog = (OperationLog)list.get(i); 
                	ResultStatus resultStatus = ResultStatus.SUCCESS;
                    if (operationLog  != null) {
                        descr = operationLog.getDescription();
                        fromIndex = descr.indexOf("MCU[") + 4;
                        endIndex = descr.indexOf("]", fromIndex);
                        if (endIndex == -1)
                            continue;
                        
                        mcuId = descr.substring(fromIndex,endIndex);
                        
                        fromIndex = descr.indexOf("TRY[") + 4;
                        endIndex = descr.indexOf("]", fromIndex);
                        if (endIndex == -1)
                            continue;
                        
                        tryCnt = Integer.parseInt(descr.substring(fromIndex, endIndex))+1;
                        log.debug("MCU[" + mcuId + "] SENSOR[" +
                        		operationLog.getTargetName() + "] TRY[" + tryCnt + "] cmdDeleteSensor");
                        
                        try {
                            sensor = modemDao.get(modemId);
                            if (!mcuId.equals(sensor.getMcu().getSysID())) {
                            	deleteModem(mcuId, operationLog.getTargetName());
                            }

                        }
                        catch (Exception e) {                        	
                        	resultStatus = ResultStatus.FAIL;
                        	log.error(e,e);
                        }

                        operationLog.setStatus(resultStatus.getCode());
                    	operationLogDao.update(operationLog);
                    }
                }

                isRun = false;
            }else
            {
                log.debug("Already DeleteSensorJob Prcessing..");
            }
        }
        catch (Exception e)
        {
            isRun = false;
            throw new JobExecutionException(e);
        }
    } 
	
	
	public void deleteModem(String mcuId, String targetName) throws Exception {
        
        try{
            com.aimir.model.device.MCU mcu = mcuDao.get(mcuId);
        	CommandWS gw = CmdManager.getCommandWS(mcu.getProtocolType().getName());

            gw.cmdDeleteModem(mcuId, targetName);
        
        }catch(Exception e){
        	throw e;
        }
	}

    public String getDescription()
    {
        return description;
    }

    public String[] getParamList()
    {
        return paramList;
    }

    public String[] getParamListDescription()
    {
        return paramListDescription;
    }

    public String[] getParamListDefault()
    {
        return paramListDefault;
    }

    public boolean[] getParamListRequired()
    {
        return paramListRequired;
    }

}