package com.aimir.schedule.job;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.command.ws.client.CommandWS;
import com.aimir.fep.modem.ModemCommandData;
import com.aimir.fep.modem.ModemROM;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Modem;
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.device.ZRU;
import com.aimir.schedule.command.CmdManager;
import com.aimir.schedule.util.ScheduleProperty;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

public class SensorJoinDefaultSetJob extends AimirJob
{
    private static Log log = LogFactory.getLog(SensorJoinDefaultSetJob.class);

    private static final String description = "scheduler.job.SensorJoinDefaultSetJob";
    private static final String[] paramList = {};
    private static final String[] paramListDescription = { };
    private static final boolean[] paramListRequired = {};
    private static final String[] paramListDefault = {};

    private static boolean isRun = false;

    @Autowired
    ModemDao modemDao;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException
    {
        String jobName = context.getJobDetail().getDescription();
        log.info("Executing job: " + jobName + " executing at " + new Date());
        
        JobDataMap data = context.getMergedJobDataMap();
        
        try{
            if(!isRun)
            {
                isRun = true;

                Set<Condition> condition = new HashSet<Condition>();
                condition.add(new Condition("modemType", new Object[]{ModemType.ZRU,ModemType.ZEUPLS}, null, Restriction.IN));
                condition.add(new Condition("needJoinSet", new Object[]{"0"}, null, Restriction.NOT));
                List<Modem> list = modemDao.findByConditions(condition);
                CommandWS gw = null; // cm.getCommandGW();
                for(int i=0;list!=null && i<list.size();i++)
                {
                    Modem modem = (Modem) list.get(i);
                    
                    try{
                        gw = CmdManager.getCommandWS(modem.getMcu().getProtocolType().getName());
                        setModemConfig(gw, modem.getMcu().getSysID(), modem.getModemType(),modem);
                        modemDao.update(modem);
                    }catch(Exception e)
                    {
                    	log.error(e,e);
                    }
                }  

                isRun = false;
            }else
            {
                log.debug("Already "+jobName+" Prcessing..");
            }
        }catch (Exception e)
        {
            isRun = false;
            log.error("Executing job: " + jobName + " occured exception at " + new Date() + " " + e.getMessage());
            throw new JobExecutionException(e.getMessage());
        }
        
        log.info("Executing job: " + jobName + " ending at " + new Date());

    }

    private void setModemConfig(CommandWS gw, String mcuId, ModemType modemType, Modem modem)
    throws Exception
    {

        log.debug("MCUID:"+mcuId+" modemType:"+modemType.name()+" modem:"+modem.getDeviceSerial() + " setModemConfig...");

        if (mcuId == null || "".equals(mcuId))
            return;
        
        int alarmFlag = Integer.parseInt(ScheduleProperty.getProperty("aimir.join.zeupls.defAlarmflag"));
        int lpPeriod = Integer.parseInt(ScheduleProperty.getProperty("aimir.join.zeupls.defLPPeriod"));
        int testFlag = Integer.parseInt(ScheduleProperty.getProperty("aimir.join.zru.defTestflag"));
        
        if(modemType.equals(ModemType.ZRU))
        {
            byte[] val = new byte[] { DataUtil.getByteToInt(testFlag) };
            gw.cmdSetModemROM(mcuId, modem.getDeviceSerial(), ModemROM.OFFSET_TEST_FLAG, val);

            ((ZRU)modem).setTestFlag(testFlag != 0 ? Boolean.TRUE : Boolean.FALSE);
            
        }else if(modemType.equals(ModemType.ZEUPLS))
        {
            ModemCommandData data = new ModemCommandData();
            data.setCmdType(ModemCommandData.CMD_TYPE_LP_PERIOD);
            byte[] val = new byte[] { DataUtil.getByteToInt(lpPeriod) };
            data.setData(val);
            if (data.getCmdType()!=(byte)0x99 && lpPeriod!=0)  // only setting when lpPeriod is not 0 in aimir.properties.
            {
                log.debug("cmdType[" + Hex.decode(new byte[]{data.getCmdType()}) + "] DataStream["
                          + Hex.decode(data.getData()) + "]");
                gw.cmdCommandModem(mcuId, modem.getDeviceSerial(), data.getCmdType(), data.getData());
                ((ZEUPLS)modem).setLpPeriod(lpPeriod);
                data.setCmdType(data.CMD_TYPE_NONE);
            }
            data.setCmdType(ModemCommandData.CMD_TYPE_ALARM_FLAG);
            val = new byte[] { DataUtil.getByteToInt(alarmFlag) };
            data.setData(val);
            if (data.getCmdType()!=(byte)0x99)
            {
                log.debug("cmdType[" + Hex.decode(new byte[]{data.getCmdType()}) + "] DataStream["
                          + Hex.decode(data.getData()) + "]");
                gw.cmdCommandModem(mcuId, modem.getDeviceSerial(), data.getCmdType(), data.getData());
                ((ZEUPLS)modem).setAlarmFlag(alarmFlag);
            }
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

    public String[] getParamListDefault()
    {
        return paramListDefault;
    }

    public String[] getParamListDescription()
    {
        return paramListDescription;
    }

    public boolean[] getParamListRequired()
    {
        return paramListRequired;
    }
}
