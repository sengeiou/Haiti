package com.aimir.fep.protocol.fmp.parser.alarm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.Hex;
import com.aimir.notification.VarBinds;

public class Summit3208GLD implements AlarmParser
{
    private Log log = LogFactory.getLog(Summit3208GLD.class);

    private int alarmVendor = 1;    
    private String alarmMessage = null;    

    public Summit3208GLD()
    {
    }

    public void parse(byte[] data) throws Exception
    {
        alarmMessage = Hex.decode(data);
    }

    public void getVarBinds(VarBinds vbs)
    {
        vbs.put("alarmVendor",Integer.toString(alarmVendor));
        vbs.put("alarmMesssage",alarmMessage);
    }

    public int getAlarmVendor() { return this.alarmVendor; }

    public String getAlarmMessage() { return this.alarmMessage; }
    public void setAlarmMessage(String data) { this.alarmMessage = data; }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Summit3208GLD Alarm::{")
            .append("alarmVendor=["+alarmVendor+"],")
            .append("alarmMessage=["+alarmMessage+"]")
            .append("}\n");
        return sb.toString();
    }
}
