package com.aimir.fep.trap.actions.PH;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//import java.util.HashMap; // INSERT 2018/04/18 #SP-929
//import java.util.Map; // INSERT 2018/04/18 #SP-929

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MbusSlaveIOModuleDao; // INSERT 2018/04/18 #SP-929
import com.aimir.dao.device.MeterAttrDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.mvm.MeteringDataDao;
import com.aimir.dao.mvm.MeteringDataNMDao; // INSERT 2018/04/18 #SP-929
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.Calculate; // INSERT 2018/05/02 #SP-929
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MbusSlaveIOModule; // INSERT 2018/04/18 #SP-929
import com.aimir.model.device.Meter;
import com.aimir.model.device.MeterAttr;
import com.aimir.model.device.Modem;
import com.aimir.model.mvm.MeteringData; // INSERT 2018/04/18 #SP-929
import com.aimir.model.mvm.MeteringDataNM; // INSERT 2018/04/18 #SP-929
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;

/**
 * Event ID : EV_PH_220.2.0 (Alarm Meter Event)
 *
 * @author 
 * @version $Rev: 1 $, $Date: 2016-05-13 10:00:00 +0900 $,
 */
@Component
public class EV_PH_220_2_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_PH_220_2_0_Action.class);

    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    SupplierDao supplierDao;

    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    EventAlertDao eaDao;

    // -> INSERT START 2018/04/18 #SP-929
    @Autowired
    protected MeteringDataDao meteringDataDao;
    
    @Autowired
    protected MeteringDataNMDao meteringDataNMDao;
    
    @Autowired
    protected MbusSlaveIOModuleDao mbusSlaveIOModuleDao;
    // <- INSERT END   2018/04/18 #SP-929
    
    @Autowired
    MeterAttrDao meterAttrDao;
   /**
     * execute event action
     *
     * @param trap - FMP Trap(Alarm Meter Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_PH_220_2_0_Action : EventName[evtAlarmMeter] "
                +" EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"] TargetClass[" + event.getActivatorType() + "]");

        TransactionStatus txstatus = null;
        try { 
            txstatus = txmanager.getTransaction(null);
            

            log.debug("EV_PH_220_2_0_Action : event[" + event.toString() + "]");

            String ipAddr = trap.getIpAddr();
            String meterId = event.getEventAttrValue("meterId");
            
            /*
             * meterAlarmData is one or more. it has to be taken as hex.
             */
            List<byte[]> alarmDataList = new ArrayList<byte[]>();
            String alarmDataHex = null;
            for (int i = 0; ; i++) {
                if (i > 0)
                    alarmDataHex = event.getEventAttrValue("streamEntry."+i+".hex");
                else
                    alarmDataHex = event.getEventAttrValue("streamEntry.hex");
                
                if (alarmDataHex == null || "".equals(alarmDataHex))
                    break;
                else
                    alarmDataList.add(Hex.encode(alarmDataHex));
            }
            
            log.debug("METER_ID[" + meterId + "]");
            Meter meter = null;
            Modem modem = null;
            if (meterId == null || "".equals(meterId)) {
                modem = modemDao.get(trap.getSourceId());
                if (modem != null && modem.getMeter().size() > 0) {
                    for (Meter m : modem.getMeter()) {
                        if (m.getModemPort() == null || m.getModemPort() == 0) {
                            meter = m;
                            break;
                        }
                    }
                }
            }
            else {
                meter = meterDao.get(meterId);
            }
            
            if (meter != null)
            {
	            event.setActivatorId(meterId);
	            event.setActivatorType(meter.getMeterType().getName());
	            event.setSupplier(meter.getSupplier());
	            event.setLocation(meter.getLocation());
            }
            else {
                if (modem != null) {
                    event.setActivatorId(trap.getSourceId());
                    event.setActivatorType(trap.getSourceType());
                    event.setSupplier(modem.getSupplier());
                    event.setLocation(modem.getLocation());
                }
            }
            
            doAlarm(alarmDataList, meter, event);
            /*
            byte[]  _timestamp = Hex.encode(event.getEventAttrValue("meterAlarmReceivedTime"));
            String meterAlarmReceivedTime = String.format("%4d%02d%02d%02d%02d%02d", 
                    DataUtil.getIntTo2Byte(new byte[]{_timestamp[0], _timestamp[1]}),
                    DataUtil.getIntToByte(_timestamp[2]),
                    DataUtil.getIntToByte(_timestamp[3]),
                    DataUtil.getIntToByte(_timestamp[4]),
                    DataUtil.getIntToByte(_timestamp[5]),
                    DataUtil.getIntToByte(_timestamp[6]));
            log.debug("METER_ALARM_RECEICED_TIME[" + meterAlarmReceivedTime + "]");
            */
            
            // byte[] _meterAlarmData =  Hex.encode(event.getEventAttrValue("meterAlarmData"));    
            
            
//
//            byte[] _alarmDateTime = new byte[14];
//            System.arraycopy(_meterAlarmData, 1+4, _alarmDateTime, 0, 14);
//            String alarmDateTime = new String(_alarmDateTime); //???????????
//            log.debug("METER_ALARM_DATA_DATE_TIME[" + alarmDateTime + "]");
//            
//            byte[] value = new byte[4];
//            System.arraycopy(_meterAlarmData, 1+4+14+1+1+8 + 1, value, 0, 4);
//            
//            String group = null;
//            String alarm = null;
//            if ( (alarm = getAlarmByte0(value[0])) != null ){
//            	group = "Other Alarms";
//            }
//            else if ( (alarm = getAlarmByte1(value[1])) != null ){ 
//            	group = "Critical Alarms";
//            }
//            else if ( (alarm = getAlarmByte2(value[2])) != null ){
//            	group = "M-Bus Alarms";
//            }
//            Meter meter = meterDao.get(meterId);
//            if (meter != null)
//            {
//	            event.setActivatorId(meterId);
//	            event.setActivatorType(meter.getMeterType().getName());
//	            event.setSupplier(meter.getSupplier());
//	            event.setLocation(meter.getLocation());
//            }
//	        log.debug("ALARM_GROUP[" + group + "] ALARM[" + alarm + "]");
            
        }
        catch(Exception ex) {
            log.error(ex,ex);
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }

        log.debug("Meter Event Action Compelte");
    }
    
    public void doAlarm(List<byte[]> alarmDataList, Meter meter, EventAlertLog event) 
    throws Exception
    {
        for(byte[] _meterAlarmData : alarmDataList) {
            doAlarm(_meterAlarmData, meter, event);
        }
    }
    
    public void doAlarm(String modemId, String _meterAlarmData) 
    throws Exception
    {
        Modem modem = modemDao.get(modemId);
        Meter meter = null;
        for (Meter m : modem.getMeter()) {
            if (m.getModemPort() == null || m.getModemPort() == 0) {
                meter = m;
                break;
            }
        }
        doAlarm(Hex.encode(_meterAlarmData), meter, null);
    }

 	// INSERT START SP-929
    public void saveMeteringDataNM(MeteringType meteringType, String meteringDateTime,
            byte digital, long canalog, long vanalog, Meter meter, DeviceType deviceType,
            String deviceId, DeviceType mdevType, String mdevId, String meterTime) throws Exception
    {
        log.debug("MeteringType[" + meteringType + "] MeteringDateTime[" + meteringDateTime +
                "] DIGITAL[" + Byte.toString(digital) +
                "] CURRENT_ANALOG[" + Long.toString(canalog) +
                "] VOLTAGE_ANALOG[" + Long.toString(vanalog) +
                "] DeviceType[" + deviceType + "] DeviceId[" + deviceId +
                "] MDevType[" + mdevType + "] MDevId[" + mdevId + "] MeterTime[" + meterTime + "]");
        
        Calculate root = null;
        String  calcCAnalog  = FMPProperty.getProperty("mbus.current.analog.calculate.string");
        String  calcVAnalog  = FMPProperty.getProperty("mbus.voltage.analog.calculate.string");
        String  scaleCAnalog = FMPProperty.getProperty("mbus.current.analog.scale.string");
        String  scaleVAnalog = FMPProperty.getProperty("mbus.voltage.analog.scale.string");
        if(calcCAnalog == null)
        	calcCAnalog = "x";
        if(calcVAnalog == null)
        	calcVAnalog = "x";
        if(scaleCAnalog == null)
        	scaleCAnalog = "0";
        if(scaleVAnalog == null)
        	scaleVAnalog = "0";
        
        Double  ch1 = 0.0; // Digital data 
        Double  ch2 = 0.0; // Analog data(Current)
        Double  ch3 = 0.0; // Convert Analog data(Current) 
        Double  ch4 = 0.0; // Analog data(Voltage)
        Double  ch5 = 0.0; // Convert Analog data(Voltage)
        int     iScaleC = Integer.valueOf(scaleCAnalog);
        int     iScaleV = Integer.valueOf(scaleVAnalog);
        BigDecimal dcValueC = new BigDecimal( new BigInteger(String.valueOf(canalog)), -iScaleC);
        BigDecimal dcValueV = new BigDecimal( new BigInteger(String.valueOf(vanalog)), -iScaleV);
        
        // Digital data
        ch1 = Double.valueOf(String.valueOf(DataUtil.getIntToByte(digital)));
       
        // Calculate: Analog data(Current)
        //Calculate.ValidateBracketBalance(Long.toString(canalog) + "*" + scaleCAnalog);
        //root = new Calculate(Long.toString(canalog) + "*" + scaleCAnalog);
        //root.parse();
        //root.calculate();
        //log.debug("EXPR[Analog data(Current)]: " + Long.toString(canalog) + "*" + scaleCAnalog + "=" + root.expression);
        //ch2 = Double.valueOf(root.expression);
        ch2 = dcValueC.doubleValue();
        log.debug("EXPR[Analog data(Current)]: value=" + ch2.toString() + " scale=" + scaleCAnalog);
        
        // Calculate: Convert Analog data(Current)
        calcCAnalog = calcCAnalog.replace(" ", "");
        calcCAnalog = calcCAnalog.replace("x", ch2.toString());
        Calculate.ValidateBracketBalance(calcCAnalog);
        root = new Calculate(calcCAnalog);
        root.parse();
        root.calculate();
        log.debug("EXPR[Convert Analog data(Current)]: " + calcCAnalog + "=" + root.expression);
        ch3 = Double.valueOf(root.expression);

        // Calculate: Analog data(Voltage)
        //Calculate.ValidateBracketBalance(Long.toString(vanalog) + "*" + scaleVAnalog);
        //root = new Calculate(Long.toString(vanalog) + "*" + scaleVAnalog);
        //root.parse();
        //root.calculate();
        //log.debug("EXPR[Analog data(Voltage)]: " + Long.toString(vanalog) + "*" + scaleVAnalog + "=" + root.expression);
        //ch4 = Double.valueOf(root.expression);
        ch4 = dcValueV.doubleValue();
        log.debug("EXPR[Analog data(Voltage)]: value=" + ch4.toString() + " scale=" + scaleVAnalog );
        
        // Calculate: Convert Analog data(Voltage)
        calcVAnalog = calcVAnalog.replace(" ", "");
        calcVAnalog = calcVAnalog.replace("x", ch4.toString());
        Calculate.ValidateBracketBalance(calcVAnalog);
        root = new Calculate(calcVAnalog);
        root.parse();
        root.calculate();
        log.debug("EXPR[Convert Analog data(Current)]: " + calcVAnalog + "=" + root.expression);
        ch5 = Double.valueOf(root.expression);
        
    	double meteringValue = 0;
        //MeteringData mdata = null;
        MeteringDataNM mdata = null;
        //switch (meterType) {
        //case EnergyMeter :
            mdata = new MeteringDataNM();
        //    break;
        //}

        mdata.setDeviceId(deviceId);
        mdata.setDeviceType(deviceType.name());
        mdata.setMDevId(mdevId);
        mdata.setMDevType(mdevType.name());
        mdata.setMeteringType(meteringType.getType());
        mdata.setCh1(ch1);
        mdata.setCh2(ch2);
        mdata.setCh3(ch3);
        mdata.setCh4(ch4);
        mdata.setCh5(ch5);

        // TODO timezoneId
        mdata.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        String dsttime = DateTimeUtil.getDST(null, meteringDateTime);
        mdata.setYyyymmdd(dsttime.substring(0, 8));
        mdata.setHhmmss(dsttime.substring(8, 14));
        mdata.setYyyymmddhhmmss(dsttime);
        mdata.setDst(DateTimeUtil.inDST(null, mdata.getYyyymmddhhmmss()));

        if (meter!=null && meter.getContract() != null) {
            mdata.setContract(meter.getContract());
        }

        switch (mdevType) {
        case Meter :
            mdata.setMeter(meter);
            mdata.setLocation(meter.getLocation());
            if(meter!=null && meter.getSupplier() != null){
                mdata.setSupplier(meter.getSupplier());
            }
            if (meter.getModem() != null)
                mdata.setModem(meter.getModem());
            break;
        case Modem :
            Modem modem = modemDao.get(mdevId);
            mdata.setModem(modem);
            mdata.setLocation(modem.getLocation());
            if(modem!=null && modem.getSupplier() != null){
                mdata.setSupplier(modem.getSupplier());
            }
            break;
        case EndDevice :
        }
        
        // MBUS SLAVE IO MODULE data
        boolean bNewdata = false;
        MbusSlaveIOModule mbdata = mbusSlaveIOModuleDao.get(meter.getMdsId());
        if( mbdata == null ){
        	bNewdata = true;
        	mbdata = new MbusSlaveIOModule();
            mbdata.setMeter(meter);
            mbdata.setMeterId(meter.getId());
            mbdata.setMdsId(meter.getMdsId());
            mbdata.setInstallDate(meteringDateTime);
        }
        
        // Cvm mini transistor output 1
        if( (digital & 0x01) == 0x01 ) mbdata.setDegital1(true);
        else                           mbdata.setDegital1(false);
        // Cvm mini transistor output 2
        if( (digital & 0x02) == 0x02 ) mbdata.setDegital2(true);
        else                           mbdata.setDegital2(false);
        // Switch sensor
        if( (digital & 0x04) == 0x04 ) mbdata.setDegital3(true);
        else                           mbdata.setDegital3(false);
        // Rgu 10 
        if( (digital & 0x08) == 0x08 ) mbdata.setDegital4(true);
        else                           mbdata.setDegital4(false);
        // Mechanical Switch sensor
        if( (digital & 0x10) == 0x10 ) mbdata.setDegital5(true);
        else                           mbdata.setDegital5(false);
        // Magnetic Switch sensor 
        if( (digital & 0x20) == 0x20 ) mbdata.setDegital6(true);
        else                           mbdata.setDegital6(false);
        // not support 
        if( (digital & 0x40) == 0x40 ) mbdata.setDegital7(true);
        else                           mbdata.setDegital7(false);
        // not support 
        if( (digital & 0x80) == 0x80 ) mbdata.setDegital8(true);
        else                           mbdata.setDegital8(false);
        mbdata.setDegitalCurrent(DataUtil.getIntToByte(digital));
        mbdata.setAnalogCurrent(ch2); // Current sample analog value, Value calculated by Scale
        mbdata.setAnalogCurrentCnv(ch3); // Converted Current sample analog value
        mbdata.setAnalogVoltage(ch4); // Voltage sample analog value, Value calculated by Scale
        mbdata.setAnalogVoltageCnv(ch5);// Converted Voltage sample analog value
        //mbdata.setScaleCurrent(Integer.valueOf(scaleCAnalog)); // Scale(Current sample analog)
        //mbdata.setScaleVoltage(Integer.valueOf(scaleVAnalog)); // Scale(Voltage sample analog)
        mbdata.setLastUpdateTime(meteringDateTime);

//        // 미터와 모뎀 최종 통신 시간과 값을 갱신한다.
//        Code normalStatus = CommonConstants.getMeterStatusByName(MeterStatus.Normal.name());
//        log.debug("MDevId[" + mdevId + "] METER_STATUS[" + (meter.getMeterStatus() == null ? "NULL" : meter.getMeterStatus()) + "]");
//        if (meter.getMeterStatus() == null || 
//                (meter.getMeterStatus() != null && 
//                !meter.getMeterStatus().getName().equals("CutOff") && 
//                !meter.getMeterStatus().getName().equals("Delete"))){
//            meter.setMeterStatus(normalStatus);
//            log.debug("MDevId[" + mdevId + "] METER_CHANGED_STATUS[" + meter.getMeterStatus() + "]");
//        }
//        if (meterTime != null && !"".equals(meterTime)) {
//            try {
//                long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meteringDateTime).getTime() - 
//                        DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
//                meter.setTimeDiff(diff / 1000);
//            }
//            catch (ParseException e) {
//                log.warn("MDevId[" + mdevId + "] Check MeterTime[" + meterTime + "] and MeteringTime[" + meteringDateTime + "]");
//            }
//        }
        
        // 수검침과 같이 모뎀과 관련이 없는 경우는 예외로 처리한다.
        //   if (meter.getModem() != null) {
        //       meter.getModem().setLastLinkTime(dsttime);
        //   }
        // }
    
        try {
            if( bNewdata == true ){
                log.debug("MBUSSLAVEIOMODULE:INSERT[" + meter.getMdsId() + "]");
                //mbusSlaveIOModuleDao.saveOrUpdate_requires_new(mbdata);
                mbusSlaveIOModuleDao.add(mbdata);
            }
            else{
                log.debug("MBUSSLAVEIOMODULE:UPDATE[" + meter.getMdsId() + "]");
            	//mbusSlaveIOModuleDao.update_requires_new(mbdata);
            	mbusSlaveIOModuleDao.update(mbdata);
            }
            
            //meteringDataNMDao.saveOrUpdate_requires_new(mdata);
            meteringDataNMDao.saveOrUpdate(mdata);

//            //meterDao.update_requires_new(meter);
//            meterDao.update(meter);
        }
        catch (Exception e) {
            log.warn(e);
        }
    }
    
    public void doAlarm2( Meter meter, byte[] value, String etime ) 
    throws Exception
    {
        String eventClassName = "Power Alarm";
        String message = "Power Restore";
        String activatorId = null;
        boolean lineAlarm = false;
        
        // Clock invalid
        if ((value[3] & 0x01) != 0x00) {
            eventClassName = "Meter Alarm";
            message = "Clock Invalid";
            log.debug(message);
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}}, 
                    new EventAlertLog());
        }
        // Replace battery
        if ((value[3] & 0x02) != 0x00) {
            eventClassName = "Meter Alarm";
            message = "Replace Battery";
            log.debug(message);
                
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}}, 
                    new EventAlertLog());
        }
        // Power Up
        if ((value[3] & 0x04) != 0x00) {
            eventClassName = "Power Alarm";
            message = "Power Restore";
            log.debug(message);
            activatorId = meter.getMdsId();
            
            EventAlertLog event = new EventAlertLog();
            event.setStatus(EventStatus.Cleared);
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    event);
                
            try {
                if (meter.getMeterStatus().getName().equals("PowerDown")) {
                    meter.setMeterStatus(CommonConstants.getMeterStatusByName("Normal"));
                    meterDao.update(meter);
                }
            }
            catch (Exception e) {
                log.error(e, e);
            }
        }

        message = "";
        
        // l1 missing
        if ((value[3] & 0x08) != 0x00) {
            lineAlarm = true;
            message = "L1";
        }
        // l2 missing
        if ((value[3] & 0x10) != 0x00) {
            lineAlarm = true;
            if (message != null && !"".equals(message))
                message += ",";
            message += "L2";
        }
        // l3 missing
        if ((value[3] & 0x20) != 0x00) {
            lineAlarm = true;
            if (message != null && !"".equals(message))
                message += ",";
            message += "L3";
        }
        
        if (lineAlarm) {
            eventClassName = "Power Alarm";
            message += " Missing";
            if (meter != null) {
                activatorId = meter.getMdsId();
                
                log.debug(message);
                
                EventUtil.sendEvent(eventClassName,
                        TargetClass.EnergyMeter,
                        activatorId,
                        etime,
                        new String[][] {{"message", message}}, 
                        new EventAlertLog());
            }
        }
        // Program memory error
        if ((value[2] & 0x01) != 0x00) {
            eventClassName = "Malfunction Warning";
            message = "Program Memory Error";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    new EventAlertLog());
        }
        // RAM error
        if ((value[2] & 0x02) != 0x00) {
            eventClassName = "Malfunction Warning";
            message = "RAM Error";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    new EventAlertLog());
        }
        // NV memory error
        if ((value[2] & 0x04) != 0x00) {
            eventClassName = "Malfunction Warning";
            message = "NV Memory Error";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    new EventAlertLog());
        }
        // Measurement System Error
        if ((value[2] & 0x08) != 0x00) {
            eventClassName = "Meter Alarm";
            message = "Measurement System Error";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    new EventAlertLog());
        }
        // Watchdong error
        if ((value[2] & 0x10) != 0x00) {
            eventClassName = "Meter Alarm";
            message = "Watchdog Error";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    new EventAlertLog());
        }
        // cover alarm
        if ((value[2] & 0x20) != 0x00) {
            eventClassName = "Cover Alarm";
            message = "Cover Open";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}, {"caseState", "Status : Open"}},
                    new EventAlertLog());
        }
        // strong magnet field detected
        if ((value[2] & 0x40) != 0x00) {
            eventClassName = "Meter Alarm";
            message = "Strong magnet field detected";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    new EventAlertLog());
        }
        // cover alarm
        if ((value[2] & 0x80) != 0x00) {
            eventClassName = "Cover Alarm";
            message = "Cover Close";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventAlertLog event = new EventAlertLog();
            event.setStatus(EventStatus.Cleared);
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}, {"caseState", "Status : Close"}},
                    event);
        }
        // communication error m-bus
        if ((value[1] & 0x01) != 0x00) {
            eventClassName = "Communication Alarm";
            message = "M-Bus Channel 1";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    new EventAlertLog());
        }
        // communication error m-bus
        if ((value[1] & 0x02) != 0x00) {
            eventClassName = "Communication Alarm";
            message = "M-Bus Channel 2";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    new EventAlertLog());
        }
        // communication error m-bus
        if ((value[1] & 0x04) != 0x00) {
            eventClassName = "Communication Alarm";
            message = "M-Bus Channel 3";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    new EventAlertLog());
        }
        // communication error m-bus
        if ((value[1] & 0x08) != 0x00) {
            eventClassName = "Communication Alarm";
            message = "M-Bus Channel 4";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    new EventAlertLog());
        }
        // new m-bus device discovered channel 1
        if ((value[0] & 0x01) != 0x00) {
            eventClassName = "Meter Alarm";
            message = "New M-Bus Channel 1";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    new EventAlertLog());
        }
        // new m-bus device discovered channel 2
        if ((value[0] & 0x02) != 0x00) {
            eventClassName = "Meter Alarm";
            message = "New M-Bus Channel 2";
            log.debug(message);
            
            activatorId = meter.getMdsId();

            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    new EventAlertLog());
        }
        // new m-bus device discovered channel 3
        if ((value[0] & 0x04) != 0x00) {
            eventClassName = "Meter Alarm";
            message = "New M-Bus Channel 3";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    new EventAlertLog());
        }
        // new m-bus device discovered channel 4
        if ((value[0] & 0x08) != 0x00) {
            eventClassName = "Meter Alarm";
            message = "New M-Bus Channel 4";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    new EventAlertLog());
        }
        // modem communication ok
        if ((value[0] & 0x10) != 0x00) {
            eventClassName = "Meter Alarm";
            message = "Modem Communication OK";
            log.debug(message);
            
            activatorId = meter.getMdsId();
            
            EventUtil.sendEvent(eventClassName,
                    TargetClass.EnergyMeter,
                    activatorId,
                    etime,
                    new String[][] {{"message", message}},
                    new EventAlertLog());
        }
    }
 	// INSERT END SP-929
    
    public void doAlarm(byte[] _meterAlarmData, Meter meter, EventAlertLog _event) 
    throws Exception
    {
        if (meter == null) {
            log.warn("Meter is NULL!!");
            return;
        }
        
        byte[] bx;
        int pos = 0;
            
        /*
        MeterEvent meterPayload = new MeterEvent();
        meterPayload.decode(_meterAlarmData);
        
        MeterEventFrame[] meterEventFrame = meterPayload.getMeterEventFrame();
        for ( int i=0; i<meterPayload.getCount(); i++ )
        {
            String alarmDateTime = meterEventFrame[i].getTime();
            String alarmValue = meterEventFrame[i].getValue();
            log.debug("ALARMDATATIME[" + alarmDateTime + "],VALUE[" +alarmValue+"]");
            byte[] _alarmValue = DataUtil.get4ByteToInt(Integer.parseInt(alarmValue));
            check1stByte(_alarmValue[0], meter, event);
            check2stByte(_alarmValue[1], meter, event);
       }
       */
       // OF
       bx = new byte[1];
       System.arraycopy(_meterAlarmData, pos, bx, 0, bx.length);
       pos += bx.length;
       
       // 40 00 00 00
       bx = new byte[4];
       System.arraycopy(_meterAlarmData, pos, bx, 0, bx.length);
       pos += bx.length;
       
       // 2 바이트 사용할 필요 없음.
       bx = new byte[14];
       System.arraycopy(_meterAlarmData, pos, bx, 0, bx.length);
       pos += bx.length;
       // 6th : week. skip
       String etime = String.format("%4d%02d%02d%02d%02d%02d", 
               DataUtil.getIntTo2Byte(new byte[]{bx[2], bx[3]}),
               DataUtil.getIntToByte(bx[4]), DataUtil.getIntToByte(bx[5]),
               DataUtil.getIntToByte(bx[7]), DataUtil.getIntToByte(bx[8]),
               DataUtil.getIntToByte(bx[9]));
       log.debug("EventTime[" + etime + "]");
       
       //structure
       bx = new byte[1];
       System.arraycopy(_meterAlarmData, pos, bx, 0, bx.length);
       pos += bx.length;
       
       bx = new byte[1];
       System.arraycopy(_meterAlarmData, pos, bx, 0, bx.length);
       pos += bx.length;
       // length - needless
       int length = DataUtil.getIntToBytes(bx);
       
       if (_meterAlarmData.length <= 21) {
           log.warn("AlarmData length is short [" + Hex.decode(_meterAlarmData) + "]");
           _event.setEventAlert(eaDao.findByCondition("name", "Meter Alarm"));
           _event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", Hex.decode(_meterAlarmData)));
           return;
       }
       
       // -> INSERT START 2018/04/13 #SP-929
	   boolean useMBusDebug = Boolean.parseBoolean(FMPProperty.getProperty("mbus.debug.eventalertlog.used"));
	   bx = new byte[1];
       System.arraycopy(_meterAlarmData, pos, bx, 0, bx.length);
       int kind = DataUtil.getIntToBytes(bx);
       if( kind == 0x02 ) { // structure
    	   String obisDigital = null;
    	   String obisCurrentA = null;
    	   String obisVoltageA = null;
    	   byte   digital = 0x00;
    	   long   canalog = 0;
    	   long   vanalog = 0;
    	   
           for( int i=0; i < length; i++ ) {
               byte[] obisCode = new byte[8];
               byte[] value = new byte[5];
               
               //String eventClassName = "M-BUS Meter Event";
               String eventClassName = "Meter Alarm";
               String message = "";
               String activatorId = null;
               
               //structure
               bx = new byte[1];
               System.arraycopy(_meterAlarmData, pos, bx, 0, bx.length);
               pos += bx.length;
               
               // length - needless
               bx = new byte[1];
               System.arraycopy(_meterAlarmData, pos, bx, 0, bx.length);
               pos += bx.length;
               
               // OBIS(6)+2
               System.arraycopy(_meterAlarmData, pos, obisCode, 0, obisCode.length);
               pos += obisCode.length;
               
               if(Hex.decode(obisCode).equals("0906" + OBIS.MBUS_DIGITAL_DATA.getCode())){
                  obisDigital = OBIS.MBUS_DIGITAL_DATA.getCode();
               //if(Hex.decode(obisCode).equals("0906" + "00015E1F06FF")){
               //    obisDigital = "00015E1F06FF";
                   System.arraycopy(_meterAlarmData, pos, value, 0, 2);
                   pos += 2;
                   digital = value[1];
                   byte[] valuetmp = new byte[1];
                   System.arraycopy(value, 1, valuetmp, 0, 1);
                   log.debug("VALUE[" + Hex.decode(valuetmp) + "(" + Byte.toString(digital) + ")]" );

                   boolean lineAlarm = false;
                   //eventClassName = "M-BUS Meter Event";
                   message = "MBUS_DIGITAL_DATA[" + Hex.decode(valuetmp) + "]";
                   log.debug(message);
                   activatorId = meter.getMdsId();
                   if( useMBusDebug ) {
	                   EventUtil.sendEvent(eventClassName,
	                           TargetClass.EnergyMeter,
	                           activatorId,
	                           etime,
	                           new String[][] {{"message", message}}, 
	                           new EventAlertLog());
                   }
               }
               else if(Hex.decode(obisCode).equals("0906" + OBIS.MBUS_CURRENT_ANALOG_VALUE.getCode())){
                   obisCurrentA = OBIS.MBUS_CURRENT_ANALOG_VALUE.getCode();
               //else if(Hex.decode(obisCode).equals("0906" + "0100010900FF")){
               //    obisCurrentA = "0100010900FF";
                   System.arraycopy(_meterAlarmData, pos, value, 0, value.length);
                   pos += value.length;
                   canalog = ((long)value[1] << 24 & 0xFF000000) + 
                		     ((long)value[2] << 16 & 0x00FF0000) + 
                		     ((long)value[3] <<  8 & 0x0000FF00) + 
                		     ((long)value[4] & 0x000000FF);
                   byte[] valuetmp = new byte[4];
                   System.arraycopy(value, 1, valuetmp, 0, 4);
                   log.debug("VALUE[" + Hex.decode(valuetmp) + "(" + Long.toString(canalog) + ")]" );
                   
                   boolean lineAlarm = false;
                   //eventClassName = "M-BUS Meter Event";
                   message = "MBUS_CURRENT_ANALOG_VALUE[" + Hex.decode(valuetmp) + "]";
                   log.debug(message);
                   activatorId = meter.getMdsId();
                   if( useMBusDebug ) {
	                   EventUtil.sendEvent(eventClassName,
	                           TargetClass.EnergyMeter,
	                           activatorId,
	                           etime,
	                           new String[][] {{"message", message}}, 
	                           new EventAlertLog());
                   }
               }
               else if(Hex.decode(obisCode).equals("0906" + OBIS.MBUS_VOLTAGE_ANALOG_VALUE.getCode())){
                    obisVoltageA = OBIS.MBUS_VOLTAGE_ANALOG_VALUE.getCode();
               //else if(Hex.decode(obisCode).equals("0906" + "0100010A00FF")){
               //    obisVoltageA = "0100010A00FF";
                   System.arraycopy(_meterAlarmData, pos, value, 0, value.length);
                   pos += value.length;
                   vanalog = ((long)value[1] << 24 & 0xFF000000) + 
              		         ((long)value[2] << 16 & 0x00FF0000) + 
              		         ((long)value[3] <<  8 & 0x0000FF00) + 
              		         ((long)value[4] & 0x000000FF);
                   byte[] valuetmp = new byte[4];
                   System.arraycopy(value, 1, valuetmp, 0, 4);
                  log.debug("VALUE[" + Hex.decode(valuetmp) + "(" + Long.toString(vanalog) + ")]" );
                   
                   boolean lineAlarm = false;
                   //eventClassName = "M-BUS Meter Event";
                   message = "MBUS_VOLTAGE_ANALOG_VALUE[" + Hex.decode(valuetmp) + "]";
                   log.debug(message);
                   activatorId = meter.getMdsId();
                   if( useMBusDebug ) {
	                   EventUtil.sendEvent(eventClassName,
	                           TargetClass.EnergyMeter,
	                           activatorId,
	                           etime,
	                           new String[][] {{"message", message}}, 
	                           new EventAlertLog());
                   }
               }
               else if( Hex.decode(obisCode).equals("09060000616200FF") ) {
                   bx = new byte[1]; //  SP-987  type 06:double-long-unsigned
                   byte[] val = new byte[4];
                   System.arraycopy(_meterAlarmData, pos, bx, 0, bx.length);
                   pos += bx.length;
            	   // Alarm Event Value 
                   System.arraycopy(_meterAlarmData, pos, val, 0, val.length);
                   pos += val.length;
                   log.debug("VALUE[" + Hex.decode(val) + "]");
                   doMeterEventAlarm( meter, val, etime ); // SP-987
               }
           }
           /* >>> Close */
           if( (obisDigital != null) || (obisCurrentA != null) || (obisVoltageA != null) ) {
        	   // Save to METERINGDATA_NM
        	   MeteringData mdata = null;
        	   
        	   mdata = new MeteringDataNM();
        	   mdata.setDeviceId(meter.getMdsId());
        	   DeviceType deviceType = DeviceType.Modem;
        	   String deviceId = meter.getModem().getDeviceSerial();
        	   DeviceType mdevType = DeviceType.Meter;
        	   String mdevId = meter.getMdsId();
        	   //String meterTime = etime;
        	   //String yyyymmdd = etime.substring(0, 8);
        	   //String hhmmss = etime.substring(8, 14);
        	   double meteringValue = 0;
        	   
               //saveMeteringDataNM( MeteringType.Normal, yyyymmdd, hhmmss, meteringValue, meter,
               //        deviceType, deviceId, mdevType, mdevId, meterTime);
               saveMeteringDataNM( MeteringType.Normal, etime, digital, canalog, vanalog, meter,
                       deviceType, deviceId, mdevType, mdevId, null);
               
          }
          /* Close <<< */
       }
       else {
       // <- INSERT END   2018/04/13 #SP-929
       
       byte[] obisCode = new byte[8];
       byte[] value = new byte[4]; // SP-987 byte[5]->byte[4]
       
       String eventClassName = "Power Alarm";
       String message = "Power Restore";
       String activatorId = null;
       
       System.arraycopy(_meterAlarmData, pos, obisCode, 0, obisCode.length);
       pos += obisCode.length;
       log.debug("OBIS[" + Hex.decode(obisCode) + "]");
       if (Hex.decode(obisCode).equals("09060000616200FF")) { // 09:OctetString,06:size,0000616200FF:OBIS
           bx = new byte[1]; //  SP-987  type 06:double-long-unsigned
           System.arraycopy(_meterAlarmData, pos, bx, 0, bx.length);
           pos += bx.length;
           
           System.arraycopy(_meterAlarmData, pos, value, 0, value.length);
           pos += value.length;
           log.debug("VALUE[" + Hex.decode(value) + "]");
           
           doMeterEventAlarm(meter, value, etime);
       }
       } // <- INSERT 2018/04/11 #SP-929
    }
    
    void check1stByte(byte value, Meter meter, EventAlertLog event ){
    	try { 
	    	int iVal = (int)value & 0xff;
	    	// POWER UP
	    	if ( (iVal & 4) != 0 )
	    	{
	    	    event.setStatus(EventStatus.Cleared);
	    		EventUtil.sendEvent("Power Alarm",
	    				TargetClass.valueOf(meter.getMeterType().getName()), 
	    				meter.getMdsId(),
	    				new String[][]{{"message","Power Restore"}}, event);
	    		log.debug("sendEvent [Meter Power Alarm],PARAM[message:Power Restore]");
	    	}
	    	// L1 connected incorrectly
	    	if ( (iVal & 8) != 0 )
	    	{
	    		EventUtil.sendEvent("Power Alarm",
	    				TargetClass.valueOf(meter.getMeterType().getName()), 
	    				meter.getMdsId(),
	    				new String[][]{{"message","Meter L1 connected incorrectly"}}, event);
	    		log.debug("sendEvent [Meter Power Alarm],PARAM[message:L1 connected incorrectly]");
	    	}
	    	// L2 connected incorrectly
	    	if ( (iVal & 16) != 0 )
	    	{
	    		EventUtil.sendEvent("Power Alarm",
	    				TargetClass.valueOf(meter.getMeterType().getName()), 
	    				meter.getMdsId(),
	    				new String[][]{{"message","Meter L2 connected incorrectly"}}, event);
	    		log.debug("sendEvent [Meter Power Alarm],PARAM[message:L2 connected incorrectly]");
	    	}
	    	// L3 connected incorrectly
	    	if ( (iVal & 32) != 0 )
	    	{
	    		EventUtil.sendEvent("Power Alarm",
	    				TargetClass.valueOf(meter.getMeterType().getName()), 
	    				meter.getMdsId(),
	    				new String[][]{{"message","Meter L3 connected incorrectly"}}, event);
	    		log.debug("sendEvent [Meter Power Alarm],PARAM[message:L3 connected incorrectly]");
	    	}
        }
        catch(Exception ex) {
            log.error(ex,ex);
        }
   }
    
    void check2stByte(byte value, Meter meter, EventAlertLog event ){
    	try { 
	    	int iVal = (int)value & 0xff;
	    	// Fraud attempt
	    	if ( (iVal & 32) != 0 )
	    	{
	    		EventUtil.sendEvent("Cover Alarm",
	    				TargetClass.valueOf(meter.getMeterType().getName()), 
	    				meter.getMdsId(),
	    				new String[][]{{"message","Meter Fraud attempt"}}, event);
	    		log.debug("sendEvent [Meter Cover Alarm],PARAM[message:Fraud attempt]");
	    	}
        }
        catch(Exception ex) {
            log.error(ex,ex);
        }
   }


    /** 
     * SP-987
     * @param meter
     * @param value
     * @param etime
     * @throws Exception
     */
    public void doMeterEventAlarm(Meter meter, byte[] value, String etime ) 
    throws Exception
    {
        byte[] preValue;
        MeterAttr attr = getMeterAttr(meter);
        // get pre Aram Value from METER_ATTR
        if ( attr.getAlarmValue() == null 
                || attr.getAlarmValue().length() < 8) {// alarmValue was set by EV_PH_240_2
            preValue = Hex.encode("00000000");
        }
        else {
            preValue = Hex.encode(attr.getAlarmValue());
        }
        // get bits only changed 0 -> 1 
        boolean checkPreValue = Boolean.parseBoolean(FMPProperty.getProperty("event.meteralarm.check.prevalue", "false"));
        if ( checkPreValue ) {
        	byte[] eventValue = getNewAlarmValue(preValue, value);
        	 doAlarm2(meter,eventValue,etime);
        }
        else {
        	doAlarm2(meter,value,etime);
        }
        
//        setValueToMeterAttr(attr, value);
        setValueToMeterAttr(meter, value);
    }
    
    
    /**
     * SP-987
     * @param preValue
     * @param curValue
     * @return
     */
    byte[] getNewAlarmValue(byte[] preValue, byte[] curValue)
    {
        byte[] ret = new byte[4];
        for ( int i = 0; i < 4; i++) {
            byte pre = preValue[i];
            byte cur = curValue[i];
            byte ev =  0x00;
            
            for ( int j = 0; j < 8; j++ ) {
                int mask = 0x01 << j ; 
                if ( ( (pre & mask) == 0x00 ) && ((cur & mask) != 0x00) ) {
                    ev |= mask;
                }
            }
            ret[i] = ev;
        }
        log.debug("preValue["+ Hex.decode(preValue)+"] currentValue[" + Hex.decode(curValue) + "] => newAlarmValue["+ Hex.decode(ret)+"]");
        return ret;
    }
    
    /**
     * SP-987
     * @param meter
     * @return
     */
    MeterAttr getMeterAttr(Meter meter) {
        MeterAttr meterAttr = meterAttrDao.getByMeterId(meter.getId());
        if ( meterAttr == null ) {
            meterAttr = new MeterAttr();
            meterAttr.setMeterId(meter.getId());
        }
        return meterAttr;
    }
    
    /**
     * SP-987
     * Save value to METER_ATTR.ALARM_VALUE,ALARM_DATE
     * @param meterAttr
     * @param value
     */
//    void setValueToMeterAttr(MeterAttr meterAttr, byte[] value) {
	void setValueToMeterAttr(Meter meter, byte[] value) {
		log.debug("setValueToMeterAttr:" + meter.getMdsId());
		try {
			String date = DateTimeUtil.getDateString(new Date());
			MeterAttr meterAttr = meterAttrDao.getByMeterId(meter.getId());
			if ( meterAttr == null ) {
				meterAttr = new MeterAttr();
				meterAttr.setMeterId(meter.getId());
				meterAttr.setAlarmValue(Hex.decode(value));
				meterAttr.setAlarmDate(date);
				meterAttrDao.add_requires_new(meterAttr);
			}
			else {
				meterAttr.setAlarmValue(Hex.decode(value));
				meterAttr.setAlarmDate(date);
				meterAttrDao.update_requires_new(meterAttr);
			}

		} catch (Exception e) {
			log.error(e, e);
		}	  
	}

//    String getAlarmByte0(byte value){
//	    String error = null;
//	    int iVal = (int)value & 0xff;
//	    switch (iVal){
//		    case 0x01: //Bit1
//		    	error = "Clock invalid";
//		    	break;
//		    case 0x02: //Bit2
//		    	error = "Replace battery";
//				break;
//		    case 0x04: //Bit3
//		    	error = "Power Up";
//		    	break;
//		    case 0x08: //Bit4
//		    	error = "L1 connected incorrectly";
//		    	break;
//		    case 0x10: //Bit5
//		    	error = "L2 connected incorrectly";
//		    	break;
//		    case 0x20: //Bit6
//		    	error = "L3 connected incorrectly";
//		    	break;
//		    case 0x40: //Bit7
//		    case 0x80: //Bit8	
//		    	// not used
//		    	break;
//	    }
//	    return error;
//    }
//    String getAlarmByte1(byte value){
//	    String error = null;
//	    int iVal = (int)value & 0xff;
//	    switch (iVal){
//		    case 0x01: //Bit1
//		    	error = "Program memory error";
//		    	break;
//		    case 0x02: //Bit2
//		    	error = "RAM Error";
//				break;
//		    case 0x04: //Bit3
//		    	error = "NV memory Error";
//		    	break;
//		    case 0x08: //Bit4
//		    	error = "Measurement System Error";
//		    	break; 	
//		    case 0x10: //Bit5
//		    	error = "Watchdog error";
//		    	break;
//		    case 0x20: //Bit6
//		    	error = "Fraud attempt";
//		    	break;
//		    case 0x40: //Bit7
//		    case 0x80: //Bit8	
//		    	// not used
//		    	break;
//	    }
//	    return error;
//    }
//    
//    
//    String getAlarmByte2(byte value){
//	    String error = null;
//	    int iVal = (int)value & 0xff;
//	    switch (iVal){
//		    case 0x01: //Bit1
//		    	error = "Communication error M-Bus";
//		    	break;
//		    case 0x02: //Bit2
//		    	error = "New M-Bus device discovered";
//				break;
//		    case 0x04: //Bit3	
//		    case 0x08: //Bit4
//		    case 0x10: //Bit5
//		    case 0x20: //Bit6
//		    case 0x40: //Bit7
//		    case 0x80: //Bit8	
//		    	break;
//	    }
//	    return error;
//    }
}
