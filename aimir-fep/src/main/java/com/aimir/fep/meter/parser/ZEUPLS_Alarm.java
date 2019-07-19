package com.aimir.fep.meter.parser;

import java.util.ArrayList;

import com.aimir.fep.util.FMPProperty;

/**
 * parsing Alarm Data included in NAM Protocol ZEUPLS Meter Data
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2006-06-12 15:59:15 +0900 $,
 */
public class ZEUPLS_Alarm implements java.io.Serializable 
{
	private static final long serialVersionUID = -5335826196483959911L;
	public byte[]  alarmRawData = null;
    public boolean isParamterChanged = false;
    public boolean isReset = false;
    public boolean isLowBattery = false;
    public boolean isCaseOpen = false;
    public boolean isWakeUpSW = false;
    public boolean isSubmergence = false;
    public boolean isMeterPort0 = false;
    public boolean isMeterPort1 = false;
    public boolean isAcPowerOff = false;
    public boolean isBatteryFault = false;
    public boolean isADConvertFail = false;
    public boolean isSleepMode = false;
    public boolean isRepeaterMode = false;
    public boolean isACPowerNode = false;
    public boolean isTodayLP = false;
    public boolean isTilt = false;
    public boolean isMagneticTamp = false;
    /*
    public boolean[] alarm = { isParamterChanged, isReset, isLowBattery,
                              isCaseOpen, isWakeUpSW, isSubmergence,
                              isMeterPort0, isMeterPort1, isAcPowerOff,
                              isBatteryFault, isADConvertFail,isTilt,isMagneticTamp };
    */
    public boolean[] alarm = null;
    public ArrayList<String> eventList = null; 

    public void parse(byte[] alarmData)
        throws Exception
    {
    	int type = Integer.parseInt(FMPProperty.getProperty("zeupls.alarm.type","1"));
        this.alarmRawData = alarmData;

        this.isParamterChanged = (alarmData[0] & 0x80) >> 7 == 1;
        this.isReset = (alarmData[0] & 0x40) >> 6 == 1;
        this.isLowBattery = (alarmData[0] & 0x20) >> 5 == 1;
        this.isWakeUpSW = (alarmData[0] & 0x08) >> 3 == 1;
        this.isMeterPort0 = (alarmData[0] & 0x02) >> 1 == 1;
        this.isMeterPort1 = (alarmData[0] & 0x01) >> 0 == 1;
        this.isAcPowerOff = (alarmData[1] & 0x80) >> 7 == 1;
        this.isBatteryFault = (alarmData[1] & 0x40) >> 6 == 1;
        this.isADConvertFail = (alarmData[1] & 0x20) >> 5 == 1;
        this.isSleepMode = (alarmData[1] & 0x10) >> 4 == 1;
        this.isRepeaterMode = (alarmData[1] & 0x08) >> 3 == 1;
        this.isACPowerNode = (alarmData[1] & 0x04) >> 2 == 1;
        this.isTodayLP = (alarmData[1] & 0x02) >> 1 == 1;
        switch(type)
        {
        	case 1:// Water(Default)
	            this.isCaseOpen = (alarmData[0] & 0x10) >> 4 == 1;
	            this.isSubmergence = (alarmData[0] & 0x04) >> 2 == 1;
	            break;
        	case 2:// GasNatural
	            this.isTilt = (alarmData[0] & 0x10) >> 4 == 1;
	            this.isMagneticTamp = (alarmData[0] & 0x04) >> 2 == 1;
	            break;
        	case 3:// SSE Gas
	            this.isCaseOpen = (alarmData[0] & 0x10) >> 4 == 1;
	            this.isMagneticTamp = (alarmData[0] & 0x04) >> 2 == 1;
        }
        alarm = new boolean[]{ this.isLowBattery,this.isTilt,this.isMagneticTamp,this.isCaseOpen };
        convertEvent();
    }
    private void convertEvent()
    {
        if(eventList==null)
            eventList = new ArrayList();
        else
            eventList.clear();

        for(int i=0;i<alarm.length;i++)
        {
            if(alarm[i])
            {
                eventList.add(getEventRule(i));
            }            
        }
        
    }

    public boolean isParamterChanged()
    {
        return this.isParamterChanged;
    } 

    public boolean isReset()
    {
        return this.isReset;
    } 

    public boolean isLowBattery()
    {
        return this.isLowBattery;
    } 

    public boolean isCaseOpen()
    {
        return this.isCaseOpen;
    } 

    public boolean isWakeUpSW()
    {
        return this.isWakeUpSW;
    } 

    public boolean isSubmergence()
    {
        return this.isSubmergence;
    } 

    public boolean isMeterPort0()
    {
        return this.isMeterPort0;
    } 

    public boolean isMeterPort1()
    {
        return this.isMeterPort1;
    } 

    public boolean isAcPowerOff()
    {
        return this.isAcPowerOff;
    }

    public boolean isBatteryFault()
    {
        return this.isBatteryFault;
    }

    public boolean isADConvertFail()
    {
        return this.isADConvertFail;
    }

    public boolean isSleepMode()
    {
        return this.isSleepMode;
    }

    public boolean isRepeaterMode()
    {
        return this.isRepeaterMode;
    }

    public boolean isACPowerNode()
    {
        return this.isACPowerNode;
    }

    public boolean isTodayLP()
    {
        return this.isTodayLP;
    }
    
    public boolean isTilt()
    {
        return this.isTilt;
    }

    public boolean isMagneticTamp()
    {
        return this.isMagneticTamp;
    }

    public String[] getEvent()
    {
        return (String[]) eventList.toArray(new String[0]);
    }

    private String getEventRule(int type)
    {
        switch(type){
            case 0:
                return "LowBattery";
            case 1:
                return "TiltAlarm";
            case 2:
                return "MagneticTamper";
            case 3:
            	return "CaseOpen";
            default:
                return null;
        }
    }
    public String toString()
    {
        StringBuffer info = new StringBuffer();
        info.append("isParamterChanged = ").append(isParamterChanged).append('\n');
        info.append("isReset = ").append(isReset).append('\n');
        info.append("isLowBattery = ").append(isLowBattery).append('\n');
        info.append("isCaseOpen = ").append(isCaseOpen).append('\n');
        info.append("isWakeUpSW = ").append(isWakeUpSW).append('\n');
        info.append("isSubmergence = ").append(isSubmergence).append('\n');
        info.append("isMeterPort0 = ").append(isMeterPort0).append('\n');
        info.append("isMeterPort1 = ").append(isMeterPort1).append('\n');
        info.append("isAcPowerOff = ").append(isAcPowerOff).append('\n');
        info.append("isBatteryFault = ").append(isBatteryFault).append('\n');
        info.append("isADConvertFail = ").append(isADConvertFail).append('\n');
        info.append("isSleepMode = ").append(isSleepMode).append('\n');
        info.append("isRepeaterMode = ").append(isRepeaterMode).append('\n');
        info.append("isACPowerNode = ").append(isACPowerNode).append('\n');
        info.append("isTodayLP = ").append(isTodayLP).append('\n');
        info.append("isTilt = ").append(isTilt).append('\n');
        info.append("isMagneticTamp = ").append(isMagneticTamp).append('\n');

        return info.toString();
    }
}
