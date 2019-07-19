package com.aimir.fep.protocol.fmp.processor;

import java.util.Iterator;

import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.fep.protocol.fmp.frame.service.AlarmData;
import com.aimir.fep.protocol.fmp.gateway.if9.IF9Gateway;
import com.aimir.fep.protocol.fmp.log.AlarmLogger;
import com.aimir.fep.protocol.fmp.parser.alarm.AlarmMessageFactory;
import com.aimir.fep.protocol.fmp.parser.alarm.AlarmParser;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.CommLog;
import com.aimir.notification.FMPTrap;
import com.aimir.notification.VarBinds;
import com.aimir.util.TimeUtil;

/**
 * Alarm Service Data Processor
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class AlarmProcessor extends Processor
{
    private String protocolVersion = 
        FMPProperty.getProperty("protocol.version","0100");
    
    @Autowired
    private AlarmLogger alarmLogger = null;
    private IF9Gateway if9gw = null;
    
    /**
     * constructor
     *
     * @throws Exception
     */
    public AlarmProcessor() throws Exception
    {
        //log.debug("new AlarmProcessor()");
        // alarmLogger = (AlarmLogger)ctx.getBean("alarmLogger");
        // alarmLogger.init();
        
        try {
            if9gw = IF9Gateway.getInstance();
        }catch(Exception ex) {
            log.error("can not instanciate IF9Gateway");
        }
    }
    
    /**
     * processing Alarm Service Data
     *
     * @param sdata <code>Object</code> ServiceData
     */
    public int processing(Object sdata) throws Exception
    {
        //log.debug("AlarmProcessor processing ");
        if(!(sdata instanceof AlarmData))
        {
            log.debug("AlarmProcessor sdata["+sdata
                    +"] is not AlarmData");
            return 0;
        }
        AlarmData ad = (AlarmData)sdata;

        boolean if9Result = if9gw.processing(ad);
        String if9TimeStamp = TimeUtil.getCurrentTime();

        MIBUtil mibUtil = MIBUtil.getInstance();
        FMPTrap trap = new FMPTrap();

        trap.setProtocolName("FMP");
        trap.setProtocolVersion(protocolVersion);
        trap.setMcuId(ad.getMcuId());
        String alarmCode = mibUtil.getOid("alarmMessage").toString();
        trap.setCode(alarmCode);
        trap.setSourceType(ad.getVendor().toString());
        trap.setSourceId(ad.getSrcId().toString());
        trap.setTimeStamp(ad.getMcuTimeStamp().toString());

        log.debug("vendor=["+ad.getVendor()+"]");
        VarBinds vb = new VarBinds();
        AlarmParser parser = AlarmMessageFactory.getInstance(
                ad.getVendor().getValue());
        if(parser == null)
        {
            log.error("Can not Found Alarm Message Parser"
                    +" Alarm["+ad.toString()+"]");
            return 0;
        }
        try {
            parser.parse(ad.getAlarmMessage().getValue());
            parser.getVarBinds(vb);
        }catch(Exception exx) { 
            log.error("AlarmProcessor.processing:: "
                    +"parser.parse failed message["
                    +ad.getAlarmMessage()+"]",exx);
        }
        log.debug("Alarm : " + parser.toString());
        trap.setVarBinds(vb);
        vb.put("idx",ad.getIdx().toString());
        vb.put("mcuTimeStamp",ad.getMcuTimeStamp().getValue());
        vb.put("sensorTimeStamp",ad.getSensorTimeStamp().getValue());
        vb.put("if9TimeStamp",if9TimeStamp);
        vb.put("if9Result",Boolean.toString(if9Result));

        StringBuffer sb = new StringBuffer();
        sb.append("FMPTrap - \n");
        sb.append("code = ").append(trap.getCode()).append('\n');
        sb.append("sourceType = ").append(trap.getSourceType()).append('\n');
        sb.append("sourceId = [").append(trap.getSourceId().trim()).append("]\n");
        sb.append("timeStamp = ").append(trap.getTimeStamp()).append('\n');
        vb = trap.getVarBinds();
        Iterator iter = vb.keySet().iterator();
        while(iter.hasNext())
        {
            String key =(String)iter.next(); 
            String val = (String)vb.get(key);
            sb.append("oid=").append(key)
                .append(", val=").append(val).append('\n');
        }

        log.debug(sb.toString());
        alarmLogger.sendLog(trap);
        
        TextMessage msg = new ActiveMQTextMessage();
        StringBuffer buf = new StringBuffer();
        buf.append(AlarmLogger.MSGPROP.Message.getName() + "=" + AlarmLogger.MESSAGE.Alarm.getName() + ",");
        buf.append(AlarmLogger.MSGPROP.AlarmType.getName() + "=" + AlarmLogger.ALARMTYPE.Smoke.getName() + ","); // or High Temperature
        buf.append(AlarmLogger.MSGPROP.Source.getName() + "=" + ad.getSrcId().toString() + ",");
        buf.append(AlarmLogger.MSGPROP.Timestamp.getName() + "=" + ad.getSensorTimeStamp().getValue() + ",");
        buf.append(AlarmLogger.MSGPROP.Temperature.getName() + "=" + "" + ",");
        buf.append(AlarmLogger.MSGPROP.BatteryLevel.getName() + "=" + "" + ",");
        buf.append(AlarmLogger.MSGPROP.Status.getName() + "=" + AlarmLogger.STATUS.ON.getName());
        
        msg.setIntProperty("content-length", buf.length());
        msg.setText(buf.toString());
        
        alarmLogger.sendAlarm(msg, null, false);
        
        return 1;
    }
    
    /**
     * processing Alarm Service Data
     *
     * @param sdata <code>Object</code> ServiceData
     */
    public void processing(Object sdata, CommLog commLog) throws Exception
    {
        //log.debug("AlarmProcessor processing ");
        if(!(sdata instanceof AlarmData))
        {
            log.debug("AlarmProcessor sdata["+sdata
                    +"] is not AlarmData");
            return;
        }
        AlarmData ad = (AlarmData)sdata;

        boolean if9Result = if9gw.processing(ad);
        String if9TimeStamp = TimeUtil.getCurrentTime();

        MIBUtil mibUtil = MIBUtil.getInstance();
        FMPTrap trap = new FMPTrap();

        trap.setProtocolName("FMP");
        trap.setProtocolVersion(protocolVersion);
        trap.setMcuId(ad.getMcuId());
        String alarmCode = mibUtil.getOid("alarmMessage").toString();
        trap.setCode(alarmCode);
        trap.setSourceType(ad.getVendor().toString());
        trap.setSourceId(ad.getSrcId().toString());
        trap.setTimeStamp(ad.getMcuTimeStamp().toString());

        log.debug("vendor=["+ad.getVendor()+"]");
        VarBinds vb = new VarBinds();
        AlarmParser parser = AlarmMessageFactory.getInstance(
                ad.getVendor().getValue());
        if(parser == null)
        {
            log.error("Can not Found Alarm Message Parser"
                    +" Alarm["+ad.toString()+"]");
            return;
        }
        try {
            parser.parse(ad.getAlarmMessage().getValue());
            parser.getVarBinds(vb);
        }catch(Exception exx) { 
            log.error("AlarmProcessor.processing:: "
                    +"parser.parse failed message["
                    +ad.getAlarmMessage()+"]",exx);
        }
        log.debug("Alarm : " + parser.toString());
        trap.setVarBinds(vb);
        vb.put("idx",ad.getIdx().toString());
        vb.put("mcuTimeStamp",ad.getMcuTimeStamp().getValue());
        vb.put("sensorTimeStamp",ad.getSensorTimeStamp().getValue());
        vb.put("if9TimeStamp",if9TimeStamp);
        vb.put("if9Result",Boolean.toString(if9Result));

        StringBuffer sb = new StringBuffer();
        sb.append("FMPTrap - \n");
        sb.append("code = ").append(trap.getCode()).append('\n');
        sb.append("sourceType = ").append(trap.getSourceType()).append('\n');
        sb.append("sourceId = [").append(trap.getSourceId().trim()).append("]\n");
        sb.append("timeStamp = ").append(trap.getTimeStamp()).append('\n');
        vb = trap.getVarBinds();
        Iterator iter = vb.keySet().iterator();
        while(iter.hasNext())
        {
            String key =(String)iter.next(); 
            String val = (String)vb.get(key);
            sb.append("oid=").append(key)
                .append(", val=").append(val).append('\n');
        }

        log.debug(sb.toString());
        alarmLogger.sendLog(trap);
        
        TextMessage msg = new ActiveMQTextMessage();
        StringBuffer buf = new StringBuffer();
        buf.append(AlarmLogger.MSGPROP.Message.getName() + "=" + AlarmLogger.MESSAGE.Alarm.getName() + ",");
        buf.append(AlarmLogger.MSGPROP.AlarmType.getName() + "=" + AlarmLogger.ALARMTYPE.Smoke.getName() + ","); // or High Temperature
        buf.append(AlarmLogger.MSGPROP.Source.getName() + "=" + ad.getSrcId().toString() + ",");
        buf.append(AlarmLogger.MSGPROP.Timestamp.getName() + "=" + ad.getSensorTimeStamp().getValue() + ",");
        buf.append(AlarmLogger.MSGPROP.Temperature.getName() + "=" + "" + ",");
        buf.append(AlarmLogger.MSGPROP.BatteryLevel.getName() + "=" + "" + ",");
        buf.append(AlarmLogger.MSGPROP.Status.getName() + "=" + AlarmLogger.STATUS.ON.getName());
        
        msg.setIntProperty("content-length", buf.length());
        msg.setText(buf.toString());
        
        alarmLogger.sendAlarm(msg, null, false);
    }

    @Override
    public void restore() throws Exception {
        alarmLogger.init();
        alarmLogger.resendLogger();
    }
}
