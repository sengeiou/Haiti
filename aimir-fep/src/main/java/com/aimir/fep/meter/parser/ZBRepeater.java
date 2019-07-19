package com.aimir.fep.meter.parser;

import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ModemPowerType;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * parsing ZEUPLS@ meter data
 *
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2008-10-08 09:59:15 +0900 $,
 */
public class ZBRepeater extends MeterDataParser implements ModemParser, java.io.Serializable 
{
	private static final long serialVersionUID = 1895527841210148348L;
	private static Log log = LogFactory.getLog(ZBRepeater.class);
    private final int DATACNT = 1;
    
    private byte[] INFOLENGTH = new byte[1];
    private byte[] TIMEZONE = new byte[2];
    private byte[] DST = new byte[2];
    private byte[] YEAR = new byte[2];
    private byte[] MONTH = new byte[1];
    private byte[] DAY = new byte[1];
    private byte[] HOUR = new byte[1];
    private byte[] MINUTE = new byte[1];
    private byte[] SECOND = new byte[1];
    
    private byte[] FWVERSION = new byte[1];
    private byte[] FWBUILD = new byte[1];
    private byte[] HWVERSION = new byte[1];
    private byte[] SWVERSION = new byte[1];
    
    private byte[] POWERTYPE = new byte[1];
    private byte[] OPERATINGDAY = new byte[2];
    private byte[] ACTIVEMINUTE = new byte[2];
    private byte[] RESETREASON = new byte[1];
    private byte[] PERMITMODE = new byte[1];
    private byte[] PERMITSTATE = new byte[1];
    
    private byte[] ALARMFLAG = new byte[1];
    private byte[] ALARMMASK = new byte[2];
    private byte[] TESTFLAG = new byte[1];
    private byte[] BATTERYVOLT = new byte[2];
    private byte[] BATTERYCURRENT = new byte[2];
    private byte[] VOLTOFFSET = new byte[1];
    private byte[] SOLARADV = new byte[2];
    private byte[] SOLARCHGBV = new byte[2];
    private byte[] SOLARBDCV = new byte[2];
    private byte[] LOGCOUNT = new byte[1];
    private byte[] HOURCOUNT = new byte[1];
    private byte[] HHMM = new byte[1];
    
    private String currentTime = null;//yyyymmddhhmmss
    
    private String fwVersion = null;
    private String fwBuild = null;
    private String swVersion = null;
    private String hwVersion = null;
    
    private int powerType = 0;
    private int operatingDay = 0;
    private int activeMinute = 0;
    private int resetReason = 0;
    private int permitMode = 0;
    private int permitState = 0;
    
    private int alarmFlag = 0;
    private int alarmMask = 0;
    private int testFlag = 0;
    private double batteryVolt = 0;
    private double batteryCurrent = 0;
    private double voltOffset = 0;
    private double solarADV = 0;
    private double solarChgBV = 0;
    private double solarBDCV = 0;
    private int logCnt = 0;
    
    private BatteryLog[] batteryLogs;
    /**
     * constructor
     */
    public ZBRepeater()
    {
    }

    public String getCurrentTime()
    {
        return this.currentTime;
    }

    /**
     * parse meter mesurement data
     * @param data 
     */
    public void parse(byte[] data) throws Exception
    {
        int pos = 0;
        
        System.arraycopy(data, pos, INFOLENGTH, 0, INFOLENGTH.length);
        pos += INFOLENGTH.length;
        
        System.arraycopy(data, pos, TIMEZONE, 0, TIMEZONE.length);//Word 2
        pos += TIMEZONE.length;
        int timeZone = DataUtil.getIntTo2Byte(TIMEZONE);
        log.debug("TIMEZONE[" + timeZone + "]");
        
        System.arraycopy(data, pos, DST,0, DST.length);//Word 2
        pos += DST.length;
        int dst = DataUtil.getIntTo2Byte(DST);
        log.debug("DST[" + dst + "]");
        
        System.arraycopy(data, pos, YEAR, 0, YEAR.length);//Word 2
        pos += YEAR.length;
        int currentYear = DataUtil.getIntTo2Byte(YEAR);
        log.debug("YEAR[" + currentYear + "]");
        
        System.arraycopy(data, pos, MONTH, 0, MONTH.length);//Byte 1
        pos += MONTH.length;
        int currentMonth = DataUtil.getIntToBytes(MONTH);
        log.debug("MONTH[" + currentMonth + "]");
        
        System.arraycopy(data, pos, DAY, 0, DAY.length);//Byte 1
        pos += DAY.length;
        int currentDay = DataUtil.getIntToBytes(DAY);
        log.debug("DAY[" + currentDay + "]");
        
        System.arraycopy(data, pos, HOUR, 0, HOUR.length);//Byte 1
        pos += HOUR.length;
        int currentHour = DataUtil.getIntToBytes(HOUR);
        log.debug("HOUR[" + currentHour + "]");
        
        System.arraycopy(data, pos, MINUTE, 0, MINUTE.length);//Byte 1
        pos += MINUTE.length;
        int currentMinute = DataUtil.getIntToBytes(MINUTE);
        log.debug("MINUTE[" + currentMinute + "]");
        
        System.arraycopy(data, pos, SECOND, 0, SECOND.length);//Byte 1
        pos += SECOND.length;
        int currentSecond = DataUtil.getIntToBytes(SECOND);
        log.debug("SECOND[" + currentSecond + "]");
        
        currentTime = Integer.toString(currentYear)
        + (currentMonth < 10? "0"+currentMonth:""+currentMonth)
        + (currentDay < 10? "0"+currentDay:""+currentDay)
        + (currentHour < 10? "0"+currentHour:""+currentHour)
        + (currentMinute < 10? "0"+currentMinute:""+currentMinute)
        + (currentSecond < 10? "0"+currentSecond:""+currentSecond);
        
        System.arraycopy(data, pos, FWVERSION, 0, FWVERSION.length);
        pos += FWVERSION.length;
        fwVersion = Hex.decode(FWVERSION);
        fwVersion = Double.parseDouble(fwVersion) / 10.0 + "";
        
        System.arraycopy(data, pos, FWBUILD, 0, FWBUILD.length);
        pos += FWBUILD.length;
        fwBuild = Hex.decode(FWBUILD);
        
        System.arraycopy(data, pos, HWVERSION, 0, HWVERSION.length);
        pos += HWVERSION.length;
        hwVersion = Hex.decode(HWVERSION);
        hwVersion = Double.parseDouble(hwVersion) / 10.0 + "";
        
        System.arraycopy(data, pos, SWVERSION, 0, SWVERSION.length);
        pos += SWVERSION.length;
        swVersion = Hex.decode(SWVERSION);
        swVersion = Double.parseDouble(swVersion) / 10.0 + "";
        
        System.arraycopy(data, pos, POWERTYPE, 0, POWERTYPE.length);
        pos += POWERTYPE.length;
        powerType = DataUtil.getIntToBytes(POWERTYPE);
        log.debug("POWERTYPE[" + powerType + "]");
        
        System.arraycopy(data, pos, OPERATINGDAY, 0, OPERATINGDAY.length);//Byte 2
        pos += OPERATINGDAY.length;
        operatingDay = DataUtil.getIntTo2Byte(OPERATINGDAY);
        log.debug("OPERATINGDAY[" + operatingDay + "]");
        
        System.arraycopy(data, pos, ACTIVEMINUTE, 0, ACTIVEMINUTE.length);//Byte 2
        pos += ACTIVEMINUTE.length;
        activeMinute = DataUtil.getIntTo2Byte(ACTIVEMINUTE);
        log.debug("ACTIVEMINUTE[" + activeMinute + "]");
        
        System.arraycopy(data, pos, RESETREASON, 0, RESETREASON.length);
        pos += RESETREASON.length;
        resetReason = DataUtil.getIntToBytes(RESETREASON);
        log.debug("RESETREASON[" + resetReason + "]");
        
        System.arraycopy(data, pos, PERMITMODE, 0, PERMITMODE.length);
        pos += PERMITMODE.length;
        permitMode = DataUtil.getIntToBytes(PERMITMODE);
        log.debug("PERMITMODE[" + permitMode + "]");
        
        System.arraycopy(data, pos, PERMITSTATE, 0, PERMITSTATE.length);
        pos += PERMITSTATE.length;
        permitState = DataUtil.getIntToBytes(PERMITSTATE);
        log.debug("PERMITSTATE[" + permitState + "]");
        
        System.arraycopy(data, pos, ALARMFLAG, 0, ALARMFLAG.length);
        pos += ALARMFLAG.length;
        alarmFlag = DataUtil.getIntToBytes(ALARMFLAG);
        log.debug("ALARMFLAG[" + alarmFlag + "]");
        
        System.arraycopy(data, pos, ALARMMASK, 0, ALARMMASK.length);
        pos += ALARMMASK.length;
        alarmMask = DataUtil.getIntTo2Byte(ALARMMASK);
        log.debug("ALRAMMASK[" + alarmMask + "]");
        
        System.arraycopy(data, pos, TESTFLAG, 0, TESTFLAG.length);
        pos += TESTFLAG.length;
        testFlag = DataUtil.getIntToBytes(TESTFLAG);
        log.debug("TESTFLAG[" + testFlag + "]");
        
        System.arraycopy(data, pos, BATTERYVOLT, 0, BATTERYVOLT.length);
        pos += BATTERYVOLT.length;
        
        System.arraycopy(data, pos, BATTERYCURRENT, 0, BATTERYCURRENT.length);
        pos += BATTERYCURRENT.length;
        
        System.arraycopy(data, pos, VOLTOFFSET, 0, VOLTOFFSET.length);
        pos += VOLTOFFSET.length;
        
        System.arraycopy(data, pos, SOLARADV, 0, SOLARADV.length);
        pos += SOLARADV.length;
        
        System.arraycopy(data, pos, SOLARCHGBV, 0, SOLARCHGBV.length);
        pos += SOLARCHGBV.length;
        
        System.arraycopy(data, pos, SOLARBDCV, 0, SOLARBDCV.length);
        pos += SOLARBDCV.length;
        
        ModemPowerType modemPowerType = 
            ModemPowerType.valueOf(CommonConstants.getModemPowerType(powerType+"").getName());
        if (modemPowerType == ModemPowerType.Battery) {
            batteryVolt = (double)DataUtil.getIntTo2Byte(BATTERYVOLT) / 10000.0;
            log.debug("BATTERYVOLT[" + batteryVolt + "]");
            
            batteryCurrent = (double)DataUtil.getIntTo2Byte(BATTERYCURRENT) / 10000.0;
            log.debug("BATTERYCURRENT[" + batteryCurrent + "]");
            
            voltOffset = DataUtil.getIntToBytes(VOLTOFFSET);
            log.debug("VOLTOFFSET[" + voltOffset + "]");
        }
        else if (modemPowerType == ModemPowerType.Solar) {
            solarADV = (double)DataUtil.getIntTo2Byte(SOLARADV) * 2.0 / 10000.0;
            log.debug("SOLARADV[" + solarADV + "]");
            
            solarChgBV = (double)DataUtil.getIntTo2Byte(SOLARCHGBV) / 10000.0;
            log.debug("SOLARCHGBV[" + solarChgBV + "]");
            
            solarBDCV = (double)DataUtil.getIntTo2Byte(SOLARBDCV) / 10000.0;
            log.debug("SOLARBDCV[" + solarBDCV + "]");
        }
        
        System.arraycopy(data, pos, LOGCOUNT, 0, LOGCOUNT.length);
        pos += LOGCOUNT.length;
        logCnt = DataUtil.getIntToBytes(LOGCOUNT);
        
        if (logCnt == 0) {
            batteryLogs = new BatteryLog[1];
            batteryLogs[0] = new BatteryLog();
            batteryLogs[0].setYyyymmdd(currentTime.substring(0, 8));
            Object[][] values = {{currentTime.substring(8, 12),
                batteryVolt, batteryCurrent, voltOffset, solarADV, solarChgBV, solarBDCV, 0L}};
            batteryLogs[0].setValues(values);
        }
        else {
            batteryLogs = new BatteryLog[logCnt];
            Object[][] values = null;
            int hhmm = 0;
            for (int i = 0; i < logCnt; i++) {
                batteryLogs[i] = new BatteryLog();
                
                System.arraycopy(data, pos, YEAR, 0, YEAR.length);
                pos += YEAR.length;
                System.arraycopy(data, pos, MONTH, 0, MONTH.length);
                pos += MONTH.length;
                System.arraycopy(data, pos, DAY, 0, DAY.length);
                pos += DAY.length;
                
                batteryLogs[i].setYyyymmdd(DataUtil.getIntTo2Byte(YEAR)
                        + "" + (DataUtil.getIntToBytes(MONTH) < 10? 
                                "0"+DataUtil.getIntToBytes(MONTH):DataUtil.getIntToBytes(MONTH))
                                + "" + (DataUtil.getIntToBytes(DAY) < 10? 
                                        "0"+DataUtil.getIntToBytes(DAY):DataUtil.getIntToBytes(DAY)));
                
                System.arraycopy(data, pos, HOURCOUNT, 0, HOURCOUNT.length);
                pos += HOURCOUNT.length;
                batteryLogs[i].setHourCnt(DataUtil.getIntToBytes(HOURCOUNT));
                
                values = new Object[batteryLogs[i].getHourCnt()][4];
                for (int j = 0; j < batteryLogs[i].getHourCnt(); j++) {
                    System.arraycopy(data, pos, HHMM, 0, HHMM.length);
                    pos += HHMM.length;
                    hhmm = DataUtil.getIntToBytes(HHMM);
                    System.arraycopy(data, pos, SOLARADV, 0, SOLARADV.length);
                    pos += SOLARADV.length;
                    System.arraycopy(data, pos, SOLARCHGBV, 0, SOLARCHGBV.length);
                    pos += SOLARCHGBV.length;
                    System.arraycopy(data, pos, SOLARBDCV, 0, SOLARBDCV.length);
                    pos += SOLARBDCV.length;
                    
                    values[j] = new Object[] {(hhmm / 4 < 10? "0"+hhmm/4:""+hhmm/4) + 
                            (hhmm%4==0? "00":hhmm%4*15),
                            0.0, 0.0, 0.0,
                            (double)DataUtil.getIntTo2Byte(SOLARADV)/10000.0 * 2,
                            (double)DataUtil.getIntTo2Byte(SOLARCHGBV)/10000.0,
                            (double)DataUtil.getIntTo2Byte(SOLARBDCV)/10000.0, 0L};
                    batteryLogs[i].setValues(values);
                }
            }
        }
    }    

    /**
     * get String
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append("ZBREPEATER DATA[");
        sb.append("(gmtTime=").append(currentTime).append("),");
        sb.append("(fwVersion=").append(fwVersion).append("),");
        sb.append("(fwBuild=").append(fwBuild).append("),");
        sb.append("(hwVersio=").append(hwVersion).append("),");
        sb.append("(swVersion=").append(swVersion).append("),");
        sb.append("(powerType=").append(CommonConstants.getModemPowerType(powerType+"").getDescr()).append("),");
        sb.append("(operatingDay=").append(operatingDay).append(')');
        sb.append("(activeMin=").append(activeMinute).append(')');
        sb.append("(resetReason=").append(resetReason).append(')');
        sb.append("(permitMode=").append(permitMode).append(')');
        sb.append("(permitState=").append(permitState).append(')');
        sb.append("(alarmFlag=").append(alarmFlag).append(')');
        sb.append("(alarmMask=").append(alarmMask).append(')');
        sb.append("(testFlag=").append(testFlag).append(')');
        sb.append("(batteryVolt=").append(batteryVolt).append(')');
        sb.append("(batteryCurrent=").append(batteryCurrent).append(')');
        sb.append("(voltOffset=").append(voltOffset).append(')');
        sb.append("(solarADV=").append(solarADV).append(')');
        sb.append("(solarChgBV=").append(solarChgBV).append(')');
        sb.append("(solarBDCV=").append(solarBDCV).append(')');
        sb.append("]\n");

        return sb.toString();
    }

    /**
     * get Data
     */
    @Override
    public LinkedHashMap<String, String> getData()
    {
        LinkedHashMap<String, String> res = new LinkedHashMap<String, String>(16,0.75f,false);
        
        //res.put("name","ZEUPLS2");
        res.put("Current Time", currentTime);
        res.put("FW Version", swVersion);
        res.put("FW Build", fwBuild);
        res.put("HW Version", hwVersion);
        res.put("SW Version", swVersion);
        res.put("Power Type", CommonConstants.getModemPowerType(powerType+"").getDescr());
        res.put("Operating Day", operatingDay+"");
        res.put("Active Minute", activeMinute+"");
        res.put("Reset Reason", resetReason+"");
        res.put("Permit Mode", permitMode+"");
        res.put("Permit State", permitState+"");
        res.put("Alarm Flag", alarmFlag+"");
        res.put("Alarm Mask", alarmMask+"");
        res.put("Test Flag", testFlag+"");
        res.put("Battery Volt", batteryVolt+"");
        res.put("Battery Current", batteryCurrent+"");
        res.put("Battery Offset", voltOffset+"");
        res.put("Solar AD V", solarADV+"");
        res.put("Solar CHG B V", solarChgBV+"");
        res.put("Solar B DC V", solarBDCV+"");
        
        return res;
    }

    public Double getMeteringValue()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getMeterId()
    {
        // TODO Auto-generated method stub
        return "";
    }
    
    public String getFwVersion()
    {
        return fwVersion;
    }
    
    public String getFwBuild()
    {
        return fwBuild;
    }
    
    public String getHwVersion()
    {
        return hwVersion;
    }
    
    public String getSwVersion()
    {
        return swVersion;
    }
    
    public int getAlarmFlag()
    {
        return alarmFlag;
    }

    public void setAlarmFlag(int alarmFlag)
    {
        this.alarmFlag = alarmFlag;
    }

    public int getPowerType()
    {
        return powerType;
    }

    public void setPowerType(int powerType)
    {
        this.powerType = powerType;
    }

    public int getOperatingDay()
    {
        return operatingDay;
    }

    public void setOperatingDay(int operatingDay)
    {
        this.operatingDay = operatingDay;
    }

    public int getActiveMinute()
    {
        return activeMinute;
    }

    public void setActiveMinute(int activeMinute)
    {
        this.activeMinute = activeMinute;
    }

    public int getResetReason()
    {
        return resetReason;
    }

    public void setResetReason(int resetReason)
    {
        this.resetReason = resetReason;
    }

    public int getPermitMode()
    {
        return permitMode;
    }

    public void setPermitMode(int permitMode)
    {
        this.permitMode = permitMode;
    }

    public int getPermitState()
    {
        return permitState;
    }

    public void setPermitState(int permitState)
    {
        this.permitState = permitState;
    }

    public int getAlarmMask()
    {
        return alarmMask;
    }

    public void setAlarmMask(int alarmMask)
    {
        this.alarmMask = alarmMask;
    }

    public int getTestFlag()
    {
        return testFlag;
    }

    public void setTestFlag(int testFlag)
    {
        this.testFlag = testFlag;
    }

    public double getBatteryVolt()
    {
        return batteryVolt;
    }

    public void setBatteryVolt(double batteryVolt)
    {
        this.batteryVolt = batteryVolt;
    }

    public double getBatteryCurrent()
    {
        return batteryCurrent;
    }

    public void setBatteryCurrent(double batteryCurrent)
    {
        this.batteryCurrent = batteryCurrent;
    }

    public double getVoltOffset()
    {
        return voltOffset;
    }

    public void setVoltOffset(double voltOffset)
    {
        this.voltOffset = voltOffset;
    }

    public double getSolarADV()
    {
        return solarADV;
    }

    public void setSolarADV(double solarADV)
    {
        this.solarADV = solarADV;
    }

    public double getSolarChgBV()
    {
        return solarChgBV;
    }

    public void setSolarChgBV(double solarChgBV)
    {
        this.solarChgBV = solarChgBV;
    }

    public double getSolarBDCV()
    {
        return solarBDCV;
    }

    public void setSolarBDCV(double solarBDCV)
    {
        this.solarBDCV = solarBDCV;
    }

    public void setCurrentTime(String currentTime)
    {
        this.currentTime = currentTime;
    }

    public void setFwVersion(String fwVersion)
    {
        this.fwVersion = fwVersion;
    }

    public void setFwBuild(String fwBuild)
    {
        this.fwBuild = fwBuild;
    }

    public void setSwVersion(String swVersion)
    {
        this.swVersion = swVersion;
    }

    public void setHwVersion(String hwVersion)
    {
        this.hwVersion = hwVersion;
    }

    public int getFlag()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getLength()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public byte[] getRawData()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setFlag(int flag)
    {
        // TODO Auto-generated method stub
        
    }

    public ModemLPData[] getLpData()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int getPeriod()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public BatteryLog[] getBatteryLogs()
    {
        return batteryLogs;
    }

    public void setBatteryLogs(BatteryLog[] batteryLogs)
    {
        this.batteryLogs = batteryLogs;
    }
}
