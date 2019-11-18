package com.aimir.fep.meter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ChannelCalcMethod;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.constants.CommonConstants.IntegratedFlag;
import com.aimir.constants.CommonConstants.MeterEventKind;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.MeterVendor;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.PowerEventStatus;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.MeterEventDao;
import com.aimir.dao.device.MeterEventLogDao;
import com.aimir.dao.device.MeterTimeSyncLogDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.ModemPowerLogDao;
import com.aimir.dao.device.PowerAlarmLogDao;
import com.aimir.dao.device.SNRLogDao;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayHMDao;
import com.aimir.dao.mvm.DayHUMDao;
import com.aimir.dao.mvm.DaySPMDao;
import com.aimir.dao.mvm.DayTMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.LpGMDao;
import com.aimir.dao.mvm.LpHMDao;
import com.aimir.dao.mvm.LpSPMDao;
import com.aimir.dao.mvm.LpTMDao;
import com.aimir.dao.mvm.LpWMDao;
import com.aimir.dao.mvm.MeteringDataDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.dao.mvm.MonthHMDao;
import com.aimir.dao.mvm.MonthHUMDao;
import com.aimir.dao.mvm.MonthSPMDao;
import com.aimir.dao.mvm.MonthTMDao;
import com.aimir.dao.mvm.MonthWMDao;
import com.aimir.dao.mvm.PowerQualityDao;
import com.aimir.dao.mvm.PowerQualityStatusDao;
import com.aimir.dao.mvm.RealTimeBillingEMDao;
import com.aimir.dao.system.Co2FormulaDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.fep.logger.AimirThreadMapper;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EnvData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.data.MeterTimeSyncData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.data.PowerQualityMonitor;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.link.MeterEventLink;
import com.aimir.fep.meter.parser.BatteryLog;
import com.aimir.fep.meter.parser.ZEUPLS_Status;
import com.aimir.fep.protocol.fmp.log.ExternalTableLogger;
import com.aimir.fep.protocol.fmp.log.ProcedureRecoveryLogger;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Util;
import com.aimir.model.device.Meter;
import com.aimir.model.device.MeterEvent;
import com.aimir.model.device.MeterEventLog;
import com.aimir.model.device.MeterTimeSyncLog;
import com.aimir.model.device.Modem;
import com.aimir.model.device.ModemPowerLog;
import com.aimir.model.device.PowerAlarmLog;
import com.aimir.model.device.SNRLog;
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.mvm.ChannelConfig;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.DayGM;
import com.aimir.model.mvm.DayHM;
import com.aimir.model.mvm.DayPk;
import com.aimir.model.mvm.DaySPM;
import com.aimir.model.mvm.DayWM;
import com.aimir.model.mvm.LpEM;
import com.aimir.model.mvm.LpGM;
import com.aimir.model.mvm.LpHM;
import com.aimir.model.mvm.LpPk;
import com.aimir.model.mvm.LpSPM;
import com.aimir.model.mvm.LpTM;
import com.aimir.model.mvm.LpWM;
import com.aimir.model.mvm.MeteringData;
import com.aimir.model.mvm.MeteringDataEM;
import com.aimir.model.mvm.MeteringDataGM;
import com.aimir.model.mvm.MeteringDataHM;
import com.aimir.model.mvm.MeteringDataSPM;
import com.aimir.model.mvm.MeteringDataWM;
import com.aimir.model.mvm.MeteringDay;
import com.aimir.model.mvm.MeteringLP;
import com.aimir.model.mvm.PowerQuality;
import com.aimir.model.mvm.PowerQualityPk;
import com.aimir.model.mvm.PowerQualityStatus;
import com.aimir.model.mvm.RealTimeBillingEM;
import com.aimir.model.system.Co2Formula;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.MeterConfig;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * 검침데이타 저장
 *
 * @author 박종성 elevas@nuritelecom.com
 */
@Service
public abstract class AbstractMDSaver
{
    protected static Log log = LogFactory.getLog(AbstractMDSaver.class);

    @Resource(name="transactionManager")
    protected JpaTransactionManager txmanager;
    
    @Autowired
    protected MeterDao meterDao;

    @Autowired
    protected ModemDao modemDao;

    @Autowired
    protected LpEMDao lpEMDao;

    @Autowired
    protected LpGMDao lpGMDao;

    @Autowired
    protected LpWMDao lpWMDao;

    @Autowired
    protected LpHMDao lpHMDao;
    
    @Autowired
    protected LpSPMDao lpSPMDao;
    
    @Autowired
    protected LpTMDao lpTMDao;
    
    @Autowired
    protected DayEMDao dayEMDao;

    @Autowired
    protected DayGMDao dayGMDao;

    @Autowired
    protected DayWMDao dayWMDao;

    @Autowired
    protected DayHUMDao dayHUMDao;

    @Autowired
    protected DayTMDao dayTMDao;

    @Autowired
    protected DayHMDao dayHMDao;
    
    @Autowired
    protected DaySPMDao daySPMDao;

    @Autowired
    protected MonthEMDao monthEMDao;

    @Autowired
    protected MonthGMDao monthGMDao;

    @Autowired
    protected MonthWMDao monthWMDao;

    @Autowired
    protected MonthHMDao monthHMDao;

    @Autowired
    protected MonthHUMDao monthHUMDao;

    @Autowired
    protected MonthTMDao monthTMDao;
    
    @Autowired
    protected MonthSPMDao monthSPMDao;

    @Autowired
    protected MeteringDataDao meteringDataDao;

    @Autowired
    protected PowerQualityDao powerQualityDao;
    
    @Autowired
    protected PowerQualityStatusDao powerQualityStatusDao;

    @Autowired
    protected Co2FormulaDao co2FormulaDao;

    @Autowired
    protected ModemPowerLogDao modemPowerLogDao;

    @Autowired
    protected BillingDayEMDao billingDayEMDao;

    @Autowired
    protected BillingMonthEMDao billingMonthEMDao;

    @Autowired
    protected RealTimeBillingEMDao realTimeBillingEMDao;

    @Autowired
    protected MeterTimeSyncLogDao meterTimeSyncLogDao;

    @Autowired
    protected MeterEventDao meterEventDao;

    @Autowired
    protected MeterEventLogDao meterEventLogDao;

    //@Autowired
    protected MeterEventLink meterEventLink;

    @Autowired
    protected PowerAlarmLogDao powerAlarmLogDao;

    @Autowired
    protected CodeDao codeDao;
    
    @Autowired
    protected ContractDao contractDao;
    
    @Autowired
    protected SNRLogDao snrLogDao;
    
    @Autowired
    protected DeviceModelDao deviceModelDao;    
    
    final static DecimalFormat dformat = new DecimalFormat("#0.000000");
    
    private static Map<String, String> locMap;
    
    /**
     * 파서종류별로 MeterDataSaver 추상클래스를 상속받아 데이타 저장을 구현한다.
     * @param md 파싱된 검침프레임
     * @return 성공여부
     * @throws Exception
     */
    protected abstract boolean save(IMeasurementData md) throws Exception;

    protected void saveMeterEventLog(Meter meter, EventLogData[] eventLogDatas) throws Exception
    {
        log.debug("save MeterEventLogData["+meter.getMdsId()+"]");

        try {
            List<MeterEvent> list = null;
            LinkedHashSet<Condition> condition = null;
            MeterEventLog meterEventLog = null;
            String openTime = null;
            List<MeterEventLog> meterEventLogs = null;
            for(int i = 0; eventLogDatas != null && i < eventLogDatas.length; i++){
                if (eventLogDatas[i] == null)
                    continue;
                
                log.debug("eventLogData="+eventLogDatas[i].toString());
                // 미터 이벤트를 찾아서 없으면 경고를 보내고 다음 이벤트 진행
                condition = new LinkedHashSet<Condition>();
    
                condition.add(new Condition("value", new Object[]{String.valueOf(eventLogDatas[i].getFlag())}, null, Restriction.EQ));
                condition.add(new Condition("kind", new Object[]{MeterEventKind.valueOf(eventLogDatas[i].getKind())}, null, Restriction.EQ));
                // condition.add(new Condition("name", new Object[]{eventLogDatas[i].getMsg()}, null, Restriction.EQ));
                condition.add(new Condition("model", new Object[]{meter.getModel().getName()}, null, Restriction.EQ));
    
                list = meterEventDao.findByConditions(condition);
    
                if (list == null || list.size() < 1) {                   
                    log.warn("No maching meter event class!! eventcode=["+eventLogDatas[i].getFlag()+"]");
                    log.warn("value["+eventLogDatas[i].getFlag()+"] kind["+eventLogDatas[i].getKind()+"] model["+meter.getModel().getName()+"]");
                    continue;
                }
                MeterEvent meterEvent = list.get(0);
    
                // 미터 이벤트 로그를 찾는다.
                // 로그가 있으면 다음 이벤트 진행
                openTime = DateTimeUtil.getDST(null, eventLogDatas[i].getDate()+eventLogDatas[i].getTime());
                condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.openTime", new Object[]{openTime}, null, Restriction.EQ));
                condition.add(new Condition("id.activatorId", new Object[]{meter.getMdsId()}, null, Restriction.EQ));
                condition.add(new Condition("id.meterEventId", new Object[]{meterEvent.getId()}, null, Restriction.EQ));
                // condition.add(new Condition("yyyymmdd", new Object[] {openTime.substring(0, 8)}, null, Restriction.EQ));
    
                meterEventLogs = meterEventLogDao.findByConditions(condition);
    
                if (meterEventLogs != null && meterEventLogs.size() != 0)
                    continue;
    
                meterEventLog = new MeterEventLog();
    
                meterEventLog.setMeterEventId(meterEvent.getId());
                meterEventLog.setActivatorId(meter.getMdsId());
                meterEventLog.setActivatorType(meter.getMeterType().getName());
                meterEventLog.setMessage(eventLogDatas[i].getAppend());
                meterEventLog.setOpenTime(openTime);
                meterEventLog.setWriteTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                meterEventLog.setYyyymmdd(meterEventLog.getOpenTime().substring(0, 8));
                meterEventLog.setSupplier(meter.getSupplier());
    
                log.debug("meterEvent.getId() "+meterEvent.getId());
                log.debug("meter.getMdsId() "+meter.getMdsId());
                log.debug("meter.getMeterType().getName() "+meter.getMeterType().getName());
                log.debug("event OpenDate "+ eventLogDatas[i].getDate() + eventLogDatas[i].getTime());
                log.debug("eventLogDatas[i].getAppend() "+eventLogDatas[i].getAppend());
                log.debug("meterEvent.getId() "+meterEvent.getId());
    
                meterEventLogDao.add(meterEventLog);
                // 연동
                //meterEventLink.execute(meterEventLog);
            }
        }
        catch (Exception e) {
            log.warn(e);
        }
    }

    protected void savePowerAlarmLog(Meter meter, PowerAlarmLogData[] powerAlarmLogDatas) throws Exception {
        log.debug("save PowerAlarmLogData["+meter.getMdsId()+"]");

        List<MeterEvent> list              = null;
        LinkedHashSet<Condition> condition       = null;
        PowerAlarmLog powerAlarmLog        = null;
        List<PowerAlarmLog> powerAlarmLogs = null;

        

        for (int i = 0; powerAlarmLogDatas != null && i < powerAlarmLogDatas.length; i++) {
            
            String openTime  = null;
            String closeTime = null;
            
            // 미터 이벤트를 찾아서 없으면 경고를 보내고 다음 이벤트 진행
            condition = new LinkedHashSet<Condition>();
            log.debug("PowerAlarmLog   bbb b");
            log.debug("PowerAlarmLog   powerAlarmLogDatas[i].getFlag() "+powerAlarmLogDatas[i].getFlag());
            log.debug("PowerAlarmLog   powerAlarmLogDatas[i].getKind() "+powerAlarmLogDatas[i].getKind());
            log.debug("PowerAlarmLog   powerAlarmLogDatas[i].getMsg() "+powerAlarmLogDatas[i].getMsg());
            log.debug("PowerAlarmLog   meter.getModel().getName() "+meter.getModel().getName());

            condition.add(new Condition("value", new Object[]{powerAlarmLogDatas[i].getFlag()+""}, null, Restriction.EQ));
            condition.add(new Condition("kind", new Object[]{MeterEventKind.valueOf(powerAlarmLogDatas[i].getKind())}, null, Restriction.EQ));
            condition.add(new Condition("name", new Object[]{powerAlarmLogDatas[i].getMsg()}, null, Restriction.EQ));
            condition.add(new Condition("model", new Object[]{meter.getModel().getName()}, null, Restriction.EQ));

            list = meterEventDao.findByConditions(condition);

            if (list == null || list.size() < 1) {
                log.warn("No maching meter event class!! eventcode=["+powerAlarmLogDatas[i].getFlag()+"]");
                log.warn("value["+powerAlarmLogDatas[i].getFlag()+"] kind["+powerAlarmLogDatas[i].getKind()+"] name["+powerAlarmLogDatas[i].getMsg()+"] model["+meter.getModel().getName()+"]");
                continue;
            }
            MeterEvent meterEvent = list.get(0);
            log.debug("PowerAlarmLog   meterEvent Id= "+meterEvent.getId());
            log.debug("PowerAlarmLog   meterEvent getModel= "+meterEvent.getModel());
            log.debug("PowerAlarmLog   meterEvent getName= "+meterEvent.getName());

            log.debug("PowerAlarmLog  getDate = "+ powerAlarmLogDatas[i].getDate() );
            log.debug("PowerAlarmLog  getTime = "+ powerAlarmLogDatas[i].getTime() );
            openTime = DateTimeUtil.getDST(null, powerAlarmLogDatas[i].getDate()+powerAlarmLogDatas[i].getTime());

            if (powerAlarmLogDatas[i].getCloseDate() != null && powerAlarmLogDatas[i].getCloseTime() != null 
                    && powerAlarmLogDatas[i].getCloseDate().length() != 0 && powerAlarmLogDatas[i].getCloseTime().length() != 0 ) {
                closeTime = DateTimeUtil.getDST(null, powerAlarmLogDatas[i].getCloseDate()+powerAlarmLogDatas[i].getCloseTime());
            }

            // 이벤트 로그를 찾는다.
            // 로그가 있으면 다음 이벤트 진행
            condition = new LinkedHashSet<Condition>();
            
            log.debug("PowerAlarmLog  meter.getMdsId() = "+ meter.getMdsId() );
            log.debug("PowerAlarmLog  meter.getId() = "+ meter.getId() );
            condition.add(new Condition("meter.id", new Object[]{ meter.getId() }, null, Restriction.EQ));
            log.debug("PowerAlarmLog  openTime = "+ openTime );
            condition.add(new Condition("openTime", new Object[]{openTime}, null, Restriction.EQ));            
            log.debug("PowerAlarmLog  Message = " + powerAlarmLogDatas[i].getMsg());
            condition.add(new Condition("message", new Object[]{powerAlarmLogDatas[i].getMsg()}, null, Restriction.EQ));
            log.debug("PowerAlarmLog  condition ok");
            powerAlarmLogs = powerAlarmLogDao.findByConditions(condition);            

            if (powerAlarmLogs != null && powerAlarmLogs.size() != 0) {
                continue;
            } else {
                powerAlarmLog = new PowerAlarmLog();

                log.debug("setWriteTime "+DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                log.debug("powerAlarmLogDatas[i].getMsg() "+powerAlarmLogDatas[i].getMsg());
                powerAlarmLog.setMeter(meter);
                powerAlarmLog.setSupplier(meter.getSupplier());
                powerAlarmLog.setOpenTime(openTime);
                powerAlarmLog.setWriteTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                powerAlarmLog.setMessage(powerAlarmLogDatas[i].getMsg());

                if (closeTime != null) {
                    powerAlarmLog.setCloseTime(closeTime);
                    powerAlarmLog.setStatus(PowerEventStatus.Closed.name());
                    powerAlarmLog.setDuration(Util.getDurationSec(openTime, closeTime));

//                  long duration = powerAlarmLog.getDuration();
                    long duration = Util.getDuration(openTime, closeTime);

                    condition = new LinkedHashSet<Condition>();
                    if (duration == 0) {
                        condition.add(new Condition("name", new Object[]{"0"}, null, Restriction.EQ));
                    } else if (duration > 0 && duration <= 5) {
                        condition.add(new Condition("name", new Object[]{"5"}, null, Restriction.EQ));
                    } else if (duration > 15 && duration <= 30) {
                        condition.add(new Condition("name", new Object[]{"15"}, null, Restriction.EQ));
                    } else if (duration > 30 && duration <= 60) {
                        condition.add(new Condition("name", new Object[]{"30"}, null, Restriction.EQ));
                    } else if (duration > (1 * 60) && duration <= (6 * 60)) {
                        condition.add(new Condition("name", new Object[]{"1"}, null, Restriction.EQ));
                    } else if (duration > (6 * 60) && duration <= (12 * 60)) {
                        condition.add(new Condition("name", new Object[]{"6"}, null, Restriction.EQ));
                    } else if (duration > (12 * 60) && duration <= (24 * 60)) {
                        condition.add(new Condition("name", new Object[]{"12"}, null, Restriction.EQ));
                    } else if (duration > (24 * 60)) {
                        condition.add(new Condition("name", new Object[]{"24"}, null, Restriction.EQ));
                    }
                    
                    if(duration > 60*60){ //1hour over
                        condition.add(new Condition("code", new Object[]{"12.1.%"}, null, Restriction.LIKE));
                    }else {
                        condition.add(new Condition("code", new Object[]{"12.2.%"}, null, Restriction.LIKE));
                    }

                    List<Code> codes = codeDao.findByConditions(condition);
                    Code code = null;
                    if(codes.size() > 0){
                        code = codes.get(0);
                    }

                    powerAlarmLog.setTypeCode(code);

                } else {
                    condition = new LinkedHashSet<Condition>();
                    condition.add(new Condition("name", new Object[]{"0"}, null, Restriction.EQ));
                    List<Code> codes = codeDao.findByConditions(condition);
                    Code code = null;
                    if(codes.size() > 0){
                        code = codes.get(0);
                    }
                    powerAlarmLog.setTypeCode(code);
                    
                    powerAlarmLog.setStatus(PowerEventStatus.Open.name());
                }

                log.debug("powerAlarmLogDatas[i].getLineType() "+powerAlarmLogDatas[i].getLineType());
                if (powerAlarmLogDatas[i].getLineType() != null ) {
                    powerAlarmLog.setLineType(powerAlarmLogDatas[i].getLineType().name());
                }
                powerAlarmLog.setId(TimeUtil.getCurrentLongTime());
                powerAlarmLogDao.add(powerAlarmLog);
            }
        }
    }

    protected void saveBatteryStatus(ZEUPLS modem, ZEUPLS_Status zbStatus) throws Exception
    {
        //ZEUPLS_Status zbStatus = ((ZEUPLS)parser).getStatus();
        log.debug("ModemId["+modem.getDeviceSerial()+"] zbStatus["
                  +zbStatus.toString()+"]");

        modem.setBatteryVolt(zbStatus.getBatteryVolt());
        modem.setOperatingDay(zbStatus.getOperatingDay());
        //modem.setBatteryStatus(1);
        modem.setResetCount(zbStatus.getResetCount());
        modem.setResetReason(zbStatus.getResetReason());
        modem.setCommState(1);
        modem.setLQI(zbStatus.getLqi());
        modem.setActiveTime(zbStatus.getActiveTime());

    }

    protected void saveBatteryLog(Modem modem, BatteryLog[] batteryLogs) throws Exception
    {
        String yyyymmddhhmmss = null;
        for (int i = 0; batteryLogs!= null && i < batteryLogs.length; i++) {

            ModemPowerLog modemPowerLog = new ModemPowerLog();
            for (int j = 0 ; j < batteryLogs[i].getValues().length; j++) {
                // yyyymmddhhmmss = String.format("%s%02d0000", batteryLogs[i].getYyyymmdd(), batteryLogs[i].getHourCnt());
                yyyymmddhhmmss = String.format("%s%s0000", batteryLogs[i].getYyyymmdd(), batteryLogs[i].getValues()[j][0]);
                log.info("Modem[" + modem.getDeviceSerial() + "] YYYYMMDDHHMMSS[" + yyyymmddhhmmss + "]");
                yyyymmddhhmmss = DateTimeUtil.getDST(null, yyyymmddhhmmss);
                modemPowerLog.setYyyymmdd(yyyymmddhhmmss.substring(0, 8));
                modemPowerLog.setHhmmss(yyyymmddhhmmss.substring(8, 14));
                modemPowerLog.setSupplier(modem.getSupplier());

                modemPowerLog.setDeviceId(modem.getDeviceSerial());
                modemPowerLog.setDeviceType(modem.getModemType().name());
                modemPowerLog.setBatteryVolt((Double)batteryLogs[i].getValues()[j][1]);
                modemPowerLog.setVoltageCurrent((Double)batteryLogs[i].getValues()[j][2]);
                modemPowerLog.setVoltageOffset(((Double)batteryLogs[i].getValues()[j][3]).intValue());
                
                modemPowerLog.setSolarADV((Double)batteryLogs[i].getValues()[j][4]);
                modemPowerLog.setSolarCHGBV((Double)batteryLogs[i].getValues()[j][5]);
                modemPowerLog.setSolarBCDV((Double)batteryLogs[i].getValues()[j][6]);
                
                if(batteryLogs[i].getValues()[j][7] instanceof Integer)
                    modemPowerLog.setResetCount(new Long((Integer)batteryLogs[i].getValues()[j][7]));
                else {
                    modemPowerLog.setResetCount((Long)batteryLogs[i].getValues()[j][7]);
                }
                
            }

            modemPowerLogDao.saveOrUpdate(modemPowerLog);
        }

    }
    
    protected void savePowerQualityStatus (Meter meter, String time, PowerQualityMonitor pqm,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId) 
    throws Exception {
        
        PowerQualityStatus pqstatus = new PowerQualityStatus();
        String _time = DateTimeUtil.getDST(null, time+"00");
        pqstatus.setDeviceId(deviceId);
        pqstatus.setDeviceType(deviceType);
        pqstatus.setMDevId(mdevId);
        pqstatus.setMDevType(mdevType.name());
        pqstatus.setYyyymmdd(_time.substring(0, 8));
        pqstatus.setYyyymmddhhmmss(_time.substring(0, 14));
        pqstatus.setHhmmss(_time.substring(8, 14));
        pqstatus.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        
        
        pqstatus.setDistortion_a_cnt(pqm.getDISTORTION_A_CNT());
        pqstatus.setDistortion_a_dur(pqm.getDISTORTION_A_DUR());
        pqstatus.setDistortion_b_cnt(pqm.getDISTORTION_B_CNT());
        pqstatus.setDistortion_b_dur(pqm.getDISTORTION_B_DUR());
        pqstatus.setDistortion_c_cnt(pqm.getDISTORTION_C_CNT());
        pqstatus.setDistortion_c_dur(pqm.getDISTORTION_C_DUR());
        
        pqstatus.setDistortion_cnt(pqm.getDISTORTION_CNT());
        pqstatus.setDistortion_dur(pqm.getDISTORTION_DUR());
        pqstatus.setDistortion_ing(pqm.getDISTORTION_STAT() == 0 ? false : true);
        
        pqstatus.setHarmonic_cnt(pqm.getHARMONIC_CNT());
        pqstatus.setHarmonic_dur(pqm.getHARMONIC_DUR());
        pqstatus.setHarmonic_ing(pqm.getHARMONIC_STAT() == 0 ? false : true);
        
        pqstatus.setHigh_frequency_cnt(pqm.getHIGH_FREQUENCY_CNT());
        pqstatus.setHigh_frequency_dur(pqm.getHIGH_FREQUENCY_DUR());
        pqstatus.setHigh_frequency_ing(pqm.getHIGH_FREQUENCY_STAT() == 0 ? false : true);
        
        pqstatus.setHigh_neutral_curr_cnt(pqm.getHIGH_NEUTRAL_CURR_CNT());
        pqstatus.setHigh_neutral_curr_dur(pqm.getHIGH_NEUTRAL_CURR_DUR());   
        pqstatus.setHigh_neutral_curr_ing(pqm.getHIGH_NEUTRAL_CURR_STAT() == 0 ? false : true);
        
        pqstatus.setHigh_vol_cnt(pqm.getHIGH_VOL_CNT());
        pqstatus.setHigh_vol_dur(pqm.getHIGH_VOL_DUR());
        pqstatus.setHigh_vol_ing(pqm.getHIGH_VOL_STAT() == 0 ? false : true);       
        
        pqstatus.setImbalance_curr_cnt(pqm.getIMBALANCE_CURR_CNT());
        pqstatus.setImbalance_curr_dur(pqm.getIMBALANCE_CURR_DUR());
        pqstatus.setImbalance_curr_ing(pqm.getIMBALANCE_CURR_STAT() == 0 ? false : true);
        
        pqstatus.setImbalance_vol_cnt(pqm.getIMBALANCE_VOL_CNT());
        pqstatus.setImbalance_vol_dur(pqm.getIMBALANCE_VOL_DUR());
        pqstatus.setImbalance_vol_ing(pqm.getIMBALANCE_VOL_STAT() == 0 ? false : true);
        
        pqstatus.setLow_curr_cnt(pqm.getLOW_CURR_CNT());
        pqstatus.setLow_curr_dur(pqm.getLOW_CURR_DUR());
        pqstatus.setLow_curr_ing(pqm.getLOW_CURR_STAT() == 0 ? false : true);
        
        pqstatus.setLow_vol_cnt(pqm.getLOW_VOL_CNT());
        pqstatus.setLow_vol_dur(pqm.getLOW_VOL_DUR());
        pqstatus.setLow_vol_ing(pqm.getLOW_VOL_STAT() == 0 ? false : true);     
        
        pqstatus.setOver_curr_cnt(pqm.getOVER_CURR_CNT());
        pqstatus.setOver_curr_dur(pqm.getOVER_CURR_DUR());
        pqstatus.setOver_curr_ing(pqm.getOVER_CURR_STAT() == 0 ? false : true);
        
        pqstatus.setPfactor_cnt(pqm.getPFACTOR_CNT());
        pqstatus.setPfactor_dur(pqm.getPFACTOR_DUR());
        pqstatus.setPfactor_ing(pqm.getPFACTOR_STAT() == 0 ? false : true);
        
        pqstatus.setPolarity_cross_phase_cnt(pqm.getPOLARITY_CROSS_PHASE_CNT());
        pqstatus.setPolarity_cross_phase_dur(pqm.getPOLARITY_CROSS_PHASE_DUR());
        pqstatus.setPolarity_cross_phase_ing(pqm.getPOLARITY_CROSS_PHASE_STAT() == 0 ? false : true);
        
        pqstatus.setReverse_pwr_cnt(pqm.getREVERSE_PWR_CNT());
        pqstatus.setReverse_pwr_dur(pqm.getREVERSE_PWR_DUR());
        pqstatus.setReverse_pwr_ing(pqm.getREVERSE_PWR_STAT() == 0 ? false : true);
        
        pqstatus.setService_vol_cnt(pqm.getSERVICE_VOL_CNT());
        pqstatus.setService_vol_dur(pqm.getSERVICE_VOL_DUR());
        pqstatus.setService_vol_ing(pqm.getSERVICE_VOL_STAT() == 0 ? false : true);
        
        pqstatus.setTdd_cnt(pqm.getTDD_CNT());
        pqstatus.setTdd_dur(pqm.getTDD_DUR());
        pqstatus.setTdd_ing(pqm.getTDD_STAT() == 0 ? false : true);
        
        pqstatus.setThd_curr_cnt(pqm.getTHD_CURR_CNT());
        pqstatus.setThd_curr_dur(pqm.getTHD_CURR_DUR());
        pqstatus.setThd_curr_ing(pqm.getTHD_CURR_STAT() == 0 ? false : true);
        
        pqstatus.setThd_vol_cnt(pqm.getTHD_VOL_CNT());
        pqstatus.setThd_vol_dur(pqm.getTHD_VOL_DUR());
        pqstatus.setThd_vol_ing(pqm.getTHD_VOL_STAT() == 0 ? false : true);
        
        pqstatus.setVol_a_sag_cnt(pqm.getVOL_A_SAG_CNT());
        pqstatus.setVol_a_sag_dur(pqm.getVOL_A_SAG_DUR());
        pqstatus.setVol_a_swell_cnt(pqm.getVOL_A_SWELL_CNT());
        pqstatus.setVol_a_swell_dur(pqm.getVOL_A_SWELL_DUR());
        
        pqstatus.setVol_b_sag_cnt(pqm.getVOL_B_SAG_CNT());
        pqstatus.setVol_b_sag_dur(pqm.getVOL_B_SAG_DUR());
        pqstatus.setVol_b_swell_cnt(pqm.getVOL_B_SWELL_CNT());
        pqstatus.setVol_b_swell_dur(pqm.getVOL_B_SWELL_DUR());
        
        pqstatus.setVol_c_sag_cnt(pqm.getVOL_C_SAG_CNT());
        pqstatus.setVol_c_sag_dur(pqm.getVOL_C_SAG_DUR());
        pqstatus.setVol_c_swell_cnt(pqm.getVOL_C_SWELL_CNT());
        pqstatus.setVol_c_swell_dur(pqm.getVOL_C_SWELL_DUR());
        
        pqstatus.setVol_cut_cnt(pqm.getVOL_CUT_CNT());
        pqstatus.setVol_cut_dur(pqm.getVOL_CUT_DUR());
        pqstatus.setVol_cut_ing(pqm.getVOL_CUT_STAT() == 0 ? false : true);
        
        pqstatus.setVol_flicker_cnt(pqm.getVOL_FLICKER_CNT());
        pqstatus.setVol_flicker_dur(pqm.getVOL_FLICKER_DUR());
        pqstatus.setVol_flicker_ing(pqm.getVOL_FLICKER_STAT() == 0 ? false : true);     
        
        pqstatus.setVol_fluctuation_cnt(pqm.getVOL_FLUCTUATION_CNT());
        pqstatus.setVol_fluctuation_dur(pqm.getVOL_FLUCTUATION_DUR());
        pqstatus.setVol_fluctuation_ing(pqm.getVOL_FLICKER_STAT() == 0 ? false : true);
        
        pqstatus.setVol_sag_cnt(pqm.getVOL_SAG_CNT());
        pqstatus.setVol_sag_dur(pqm.getVOL_SAG_DUR());
        pqstatus.setVol_sag_ing(pqm.getVOL_SAG_STAT() == 0 ? false : true);
        
        pqstatus.setVol_swell_cnt(pqm.getVOL_SWELL_CNT());
        pqstatus.setVol_swell_dur(pqm.getVOL_SWELL_DUR());
        pqstatus.setVol_swell_ing(pqm.getVOL_SWELL_STAT() == 0 ? false : true);     
        
        switch (mdevType) {
        case Meter :
            pqstatus.setMeter(meter);
            pqstatus.setSupplier(meter.getSupplier());
            break;
        case Modem :
            Modem modem = modemDao.get(mdevId);
            pqstatus.setModem(modem);
            if(modem!=null && modem.getSupplier() != null){
                pqstatus.setSupplier(modem.getSupplier());
            }
            break;
        case EndDevice :
            // pw.setEnvdevice(enddevice);
        }
        powerQualityStatusDao.saveOrUpdate(pqstatus);
    }

    protected void savePowerQuality(Meter meter, String time, Instrument[] instrument,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    throws Exception
    {
        PowerQuality pw = null;
        String _time = "";
        int dst = 0;

        Set<Condition> cond = null;
        List<PowerQuality> pqlist = null;
        
        if(instrument == null || instrument.length < 1) {
        	log.debug("PowerQuality data zero.");
        	return;
        }
        
        _time = DateTimeUtil.getDST(null, instrument[0].getDatetime()+"00");
        dst = DateTimeUtil.inDST(null, _time);
        
        Arrays.sort(instrument);     
        LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
        condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
        condition.add(new Condition("id.dst", new Object[]{dst}, null, Restriction.EQ));
        condition.add(new Condition("id.yyyymmddhhmm", new Object[]{instrument[0].getDatetime().substring(0, 12),
        		instrument[instrument.length - 1].getDatetime().substring(0, 12)}, null, Restriction.BETWEEN));
        condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
        pqlist = powerQualityDao.findByConditions(condition);
        
        Map<PowerQualityPk, PowerQuality> map = new HashMap<PowerQualityPk, PowerQuality>();
        for(PowerQuality pq : pqlist) {
        	map.put(pq.id, pq);
        }
        
        for (Instrument ins : instrument) {
        	 _time = DateTimeUtil.getDST(null, ins.getDatetime()+"00");
             dst = DateTimeUtil.inDST(null, _time);
             
        	 pw = new PowerQuality();
        	 BeanUtils.copyProperties(pw, ins);
        	 
             pw.setDeviceId(deviceId);
             pw.setDeviceType(deviceType);
             pw.setDst(dst);
             pw.setLine_frequency(ins.getLINE_FREQUENCY());
             pw.setMDevId(mdevId);
             pw.setMDevType(mdevType.name());
             pw.setYyyymmdd(_time.substring(0, 8));
             pw.setHhmm(_time.substring(8,12));
             pw.setYyyymmddhhmm(_time.substring(0, 12));
             pw.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
             
             if(map.containsKey(pw.id)) {
            	 continue;
             }
             
             switch (mdevType) {
             case Meter :
                 pw.setMeter(meter);
                 pw.setSupplier(meter.getSupplier());
                 break;
             case Modem :
                 Modem modem = modemDao.get(mdevId);
                 pw.setModem(modem);
                 if(modem!=null && modem.getSupplier() != null){
                     pw.setSupplier(modem.getSupplier());
                 }
                 break;
             case EndDevice :
                 // pw.setEnvdevice(enddevice);
             }
             
             if (meter != null && meter.getContract() != null)
                 pw.setContract(meter.getContract());

            pw.setCurr_1st_harmonic_mag_a(ins.getCURR_1ST_HARMONIC_MAG_A());
            pw.setCurr_1st_harmonic_mag_b(ins.getCURR_1ST_HARMONIC_MAG_B());
            pw.setCurr_1st_harmonic_mag_c(ins.getCURR_1ST_HARMONIC_MAG_C());
            pw.setCurr_2nd_harmonic_mag_a(ins.getCURR_2ND_HARMONIC_MAG_A());
            pw.setCurr_2nd_harmonic_mag_b(ins.getCURR_2ND_HARMONIC_MAG_B());
            pw.setCurr_2nd_harmonic_mag_c(ins.getCURR_2ND_HARMONIC_MAG_C());
            pw.setCurr_a(ins.getCURR_A());
            pw.setCurr_angle_a(ins.getCURR_ANGLE_A());
            pw.setCurr_angle_b(ins.getCURR_ANGLE_B());
            pw.setCurr_angle_c(ins.getCURR_ANGLE_C());
            pw.setCurr_b(ins.getCURR_B());
            pw.setCurr_c(ins.getCURR_C());
            pw.setCurr_harmonic_a(ins.getCURR_HARMONIC_A());
            pw.setCurr_harmonic_b(ins.getCURR_HARMONIC_B());
            pw.setCurr_harmonic_c(ins.getCURR_HARMONIC_C());
            pw.setCurr_seq_n(ins.getCURR_SEQ_N());
            pw.setCurr_seq_p(ins.getCURR_SEQ_P());
            pw.setCurr_seq_z(ins.getCURR_SEQ_Z());
            pw.setCurr_thd_a(ins.getCURR_THD_A());
            pw.setCurr_thd_b(ins.getCURR_THD_B());
            pw.setCurr_thd_c(ins.getCURR_THD_C());
            pw.setDistortion_kva_a(ins.getDISTORTION_KVA_A());
            pw.setDistortion_kva_b(ins.getDISTORTION_KVA_B());
            pw.setDistortion_kva_c(ins.getDISTORTION_KVA_C());
            pw.setDistortion_pf_a(ins.getDISTORTION_PF_A());
            pw.setDistortion_pf_b(ins.getDISTORTION_PF_B());
            pw.setDistortion_pf_c(ins.getDISTORTION_PF_C());
            pw.setDistortion_pf_total(ins.getDISTORTION_PF_TOTAL());
            pw.setKva_a(ins.getKVA_A());
            pw.setKva_b(ins.getKVA_B());
            pw.setKva_c(ins.getKVA_C());
            pw.setKvar_a(ins.getKVAR_A());
            pw.setKvar_b(ins.getKVAR_B());
            pw.setKvar_c(ins.getKVAR_C());
            pw.setKw_a(ins.getKW_A());
            pw.setKw_b(ins.getKW_B());
            pw.setKw_c(ins.getKW_C());
            pw.setPf_a(ins.getPF_A());
            pw.setPf_b(ins.getPF_B());
            pw.setPf_c(ins.getPF_C());
            pw.setPf_total(ins.getPF_TOTAL());
            pw.setPh_curr_pqm_a(ins.getPH_CURR_PQM_A());
            pw.setPh_curr_pqm_b(ins.getPH_CURR_PQM_B());
            pw.setPh_curr_pqm_c(ins.getPH_CURR_PQM_C());
            pw.setPh_fund_curr_a(ins.getPH_FUND_CURR_A());
            pw.setPh_fund_curr_b(ins.getPH_FUND_CURR_B());
            pw.setPh_fund_curr_c(ins.getPH_CURR_PQM_C());
            pw.setPh_fund_vol_a(ins.getPH_FUND_VOL_A());
            pw.setPh_fund_vol_b(ins.getPH_FUND_VOL_B());
            pw.setPh_fund_vol_c(ins.getPH_FUND_VOL_C());
            pw.setPh_vol_pqm_a(ins.getPH_VOL_PQM_A());
            pw.setPh_vol_pqm_b(ins.getPH_VOL_PQM_B());
            pw.setPh_vol_pqm_c(ins.getPH_VOL_PQM_C());
            pw.setSystem_pf_angle(ins.getSYSTEM_PF_ANGLE());
            pw.setTdd_a(ins.getTDD_A());
            pw.setTdd_b(ins.getTDD_B());
            pw.setTdd_c(ins.getTDD_C());
            pw.setVol_1st_harmonic_mag_a(ins.getVOL_1ST_HARMONIC_MAG_A());
            pw.setVol_1st_harmonic_mag_b(ins.getVOL_1ST_HARMONIC_MAG_B());
            pw.setVol_1st_harmonic_mag_c(ins.getVOL_1ST_HARMONIC_MAG_C());
            pw.setVol_2nd_harmonic_a(ins.getVOL_2ND_HARMONIC_A());
            pw.setVol_2nd_harmonic_b(ins.getVOL_2ND_HARMONIC_B());
            pw.setVol_2nd_harmonic_c(ins.getVOL_2ND_HARMONIC_C());
            pw.setVol_2nd_harmonic_mag_a(ins.getVOL_2ND_HARMONIC_MAG_A());
            pw.setVol_2nd_harmonic_mag_b(ins.getVOL_2ND_HARMONIC_MAG_B());
            pw.setVol_2nd_harmonic_mag_c(ins.getVOL_2ND_HARMONIC_MAG_C());
            pw.setVol_a(ins.getVOL_A());
            pw.setVol_b(ins.getVOL_B());
            pw.setVol_c(ins.getVOL_C());
            pw.setVol_angle_a(ins.getVOL_ANGLE_A());
            pw.setVol_angle_b(ins.getVOL_ANGLE_B());
            pw.setVol_angle_c(ins.getVOL_ANGLE_C());
            pw.setVol_seq_n(ins.getVOL_SEQ_N());
            pw.setVol_seq_p(ins.getVOL_SEQ_P());
            pw.setVol_seq_z(ins.getVOL_SEQ_Z());
            pw.setVol_thd_a(ins.getVOL_THD_A());
            pw.setVol_thd_b(ins.getVOL_THD_B());
            pw.setVol_thd_c(ins.getVOL_THD_C());
            pw.setLine_AB(ins.getLine_AB());
            pw.setLine_BC(ins.getLine_BC());
            pw.setLine_CA(ins.getLine_CA());

            powerQualityDao.add(pw);
        }
    }
    
    /**
     * OPF-610 DB(LP) normalization
     * @param meteringType : normal, ondemand, recovery
     * @param lpDate:yyyymmdd
     * @param lpTime:hhmm
     * @param lplist
     * @param flaglist
     * @param baseValue
     * @param meter
     * @param deviceType: MCU(0), Modem(1), Meter(2), EndDevice(3);
     * @param deviceId: 통신 장비 아이디(modem인 경우 eui64, mcu인 경우 mcu id)
     * @param mdevType : 검침 장비 타입(modem, meter)
     * @param mdevId : 검침 장비 아이디
     * @throws Exception
     */
    public void saveLPDataP(MeteringType meteringType, String lpDate, String lpTime,
            double[][] lplist, int[] flaglist, double baseValue, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId, String meteringTime) throws Exception{
        saveLPData(meteringType, lpDate, lpTime, lplist, flaglist, baseValue, meter,
                deviceType, deviceId, mdevType, mdevId, meteringTime);
    }
 	
 	/*
 	 * OPF-610 DB(LP) normalization
 	 * 에러를 최소화 하기 위해 기존 함수 수정
	 */
    protected void saveLPData(MeteringType meteringType, String lpDate, String lpTime,
            double[][] lplist, int[] flaglist, double[] baseValue, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId, String meteringTime)
    throws Exception
    {
    	if(lplist != null && lplist.length > 0) {
    		LPData[] lpData = new LPData[lplist[0].length];

    		//for each LP cnt. 0번(첫번째) 채널의 LPvalue 개수를 기준으로 함.
    		for(int i=0; i<lplist[0].length; i++) {
    			Double[] lps = new Double[lplist.length];
    			//for each Channel.
    			for(int j=0; j<lplist.length; j++) {
    				lps[j] = lplist[j][i];
    			}
    			lpData[i] = new LPData((lpDate + lpTime), lps[0], lps[0]);
    			lpData[i].setCh(lps);
    			lpData[i].setFlag(flaglist[i]);
    		}
    		
    		saveLPUsingLpNormalization(meteringType, null, lpData, mdevId, deviceId, mdevType, meteringTime);
    	}
    }
    
 	/*
 	 * OPF-610 DB(LP) normalization
 	 * 에러를 최소화 하기 위해 기존 함수 수정
	 */
    protected void saveLPData(MeteringType meteringType, String lpDate, String lpTime,
            double[][] lplist, int[] flaglist, double baseValue, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId, String meteringTime)
    throws Exception 
    {
    	saveLPData(meteringType, lpDate, lpTime, lplist, flaglist, 
    			new double[]{baseValue}, meter, deviceType, deviceId, mdevType, mdevId, meteringTime);
    }
    
    
 	/*
 	 * OPF-610 DB(LP) normalization
	 * key값을 채널별로 설정하며 내부에는 채널벌 LP 데이터가 들어가도록 한다.
	 * 나머지 0,98,99,100 채널은 상위에서 담당한다.
	 */
	protected boolean saveLPUsingLpNormalization(MeteringType meteringType, IMeasurementData md, 
			LPData[] validlplist, String mdevId, String deviceId, DeviceType mdevType, String meteringTime) throws Exception {
        
		log.info("######### Save mdevId:"+mdevId+", lp length:"+validlplist.length+", deviceId:"+deviceId+", mdevType:"+mdevType);
		//LPData 개수를 기준으로 list을 작성하며, 채널은 내부의 리스트로 관리
		//lpData가 24개이고 채널이 6개라면, 24개의 리스트가 존재하며 각각에 6개의 리스트가 하나씩 존재 
		//List<List<MeteringLP>> meteringList = new ArrayList<List<MeteringLP>>();
		Map<Integer, LinkedList<MeteringLP>> lpMap = new HashMap<Integer, LinkedList<MeteringLP>>();
		
		Meter meter = meterDao.get(mdevId);
		Modem modem= meter.getModem();
		MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());
		Map<String, Double> valSum = new HashMap<String, Double>();
		
		for(LPData lp : validlplist) {
			int chLen = lp.getCh().length;
			LinkedList<MeteringLP> subList = null;
			for(int i=0; i<chLen; i++) {
				int ch = i + 1;
				String sumKey = meter.getMdsId()+"_"+ch+"_"+lp.getDatetime().substring(0, 8);
				String myKey = meter.getMdsId()+"_"+ch+"_"+lp.getDatetime();
				if(!valSum.containsKey(sumKey)) {
					valSum.put(sumKey, (double) lp.getBasePulse());
				}
				if(!valSum.containsKey(myKey)){
					valSum.put(myKey, lp.getCh()[i]);
					valSum.put(sumKey, valSum.get(sumKey)+lp.getCh()[i]);
				}else {
					continue;
				}
				
				if(lpMap.get(ch) == null) 
					subList = new LinkedList<MeteringLP>();
				else
					subList = lpMap.get(ch);
				
				String dsttime = lp.getDatetime();
				int dst = DateTimeUtil.inDST(null, dsttime);
				int interval = meter.getLpInterval();
				int minute = Integer.parseInt(lp.getDatetime().substring(10, 12));
				
				MeteringLP meteringLP = null;
		        switch (meterType) {
		        case EnergyMeter :
		        	meteringLP = new LpEM();
		            break;
		        case WaterMeter :
		        	meteringLP = new LpWM();
		            break;
		        case GasMeter :
		        	meteringLP = new LpGM();
		            break;
		        case HeatMeter :
		        	meteringLP = new LpHM();
		            break;
		        case SolarPowerMeter :
		        	meteringLP = new LpSPM();
		            break;
		        case Inverter :
		        	meteringLP = new LpEM();
		            break;            
		        }
				
				meteringLP.setChannel(i + 1);
				meteringLP.setDeviceId(meter.getMdsId());
				meteringLP.setMDevId(meter.getMdsId());
				meteringLP.setMDevType(DeviceType.Meter.name());
                meteringLP.setDeviceType(DeviceType.Meter);
				meteringLP.setMeteringType(meteringType);
				meteringLP.setValue(valSum.get(sumKey));
				meteringLP.setDst(dst);
				meteringLP.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
				meteringLP.setDate(lp.getDatetime());
				meteringLP.setLpFlag(lp.getFlag());
				meteringLP.setContract(meter.getContract());
				meteringLP.setLpStatus(lp.getStatus());
				meteringLP.setModemTime(meteringTime);
				
				if(modem != null) {
					if(modem.getModemType() == ModemType.MMIU ||
							modem.getModemType() == ModemType.SINK ||
							modem.getModemType() == ModemType.Converter_Ethernet ||
							modem.getModemType() == ModemType.Coordinator ||
							modem.getModemType() == ModemType.LTE ||
							modem.getModemType() == ModemType.ZEU_PDA) {
						meteringLP.setDeviceId(modem.getDeviceSerial());
						meteringLP.setDeviceType(DeviceType.Modem.name());
					} else if(modem.getModemType() == ModemType.SubGiga ||
							modem.getModemType() == ModemType.ZRU ||
							modem.getModemType() == ModemType.ZMU ||
							modem.getModemType() == ModemType.ZEU_PC ||
							modem.getModemType() == ModemType.ZEUPLS ||
							modem.getModemType() == ModemType.ZEU_EISS ||
							modem.getModemType() == ModemType.ZEU_PQ ||
							modem.getModemType() == ModemType.ZEU_IO ||
							modem.getModemType() == ModemType.IEIU ||
							modem.getModemType() == ModemType.ZEU_MBus ||
							modem.getModemType() == ModemType.IHD ||
							modem.getModemType() == ModemType.ACD ||
							modem.getModemType() == ModemType.HMU ||
							modem.getModemType() == ModemType.KPX ||
							modem.getModemType() == ModemType.KPX_NEW ||
							modem.getModemType() == ModemType.KPX_HD ||
							modem.getModemType() == ModemType.PLC_G3 ||
							modem.getModemType() == ModemType.PLC_PRIME ||
							modem.getModemType() == ModemType.Repeater ||
							modem.getModemType() == ModemType.PLC_HD ||
							modem.getModemType() == ModemType.ZigBee ) {
						meteringLP.setDeviceId(deviceId);
						meteringLP.setDeviceType(DeviceType.MCU.name());
					} else {
						log.info("[checking] meter["+meter.getMdsId()+"] modem["+modem.getDeviceSerial()+"] / req modem type check");
					}
				}
				
				if( (minute % interval) == 0) {
					meteringLP.setIntervalYN(1);
				} else {
					meteringLP.setIntervalYN(0);
				}
				
				switch(mdevType) {
				case Meter:
					meteringLP.setMeter(meter);
					if(meter.getModem() != null) {
						meteringLP.setModemSerial(modem.getDeviceSerial());
					}
					break;
				case Modem:
					if(modem != null) {
						meteringLP.setModemSerial(meter.getModem().getDeviceSerial());
					}
					break;
				case EndDevice:
					break;
				default:
					break;
				}
				
				subList.add(meteringLP);
		
				lpMap.put(ch, subList);
			}
		}
		
		try {
			saveLPDataUsingLPTime(meteringType, lpMap, meter, mdevType);
		}catch(Exception e) {
			log.error(e,e);
			log.error(e.getMessage());
		}
		
		return true;
	}	
	// INSERT END SP-501
    
    protected void saveMeteringDataWithMultiChannel(MeteringType meteringType, String meteringDate,
            String meteringTime, double meteringValue, Meter meter, DeviceType deviceType,
            String deviceId, DeviceType mdevType, String mdevId, String meterTime, Double[] channels) throws Exception
    {
        log.debug("MeteringType[" + meteringType + "] MeteringDate[" + meteringDate +
                "] MeteringTime[" + meteringTime + "] MeteringValue[" + meteringValue +
                "] DeviceType[" + deviceType + "] DeviceId[" + deviceId +
                "] MDevType[" + mdevType + "] MDevId[" + mdevId + "] MeterTime[" + meterTime + "]");

        //영속
        // meterDao.saveOrUpdate(meter);
        
        MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());

        MeteringData mdata = null;
        switch (meterType) {
        case EnergyMeter :
            mdata = new MeteringDataEM();
            break;
        case WaterMeter :
            mdata = new MeteringDataWM();
            break;
        case GasMeter :
            mdata = new MeteringDataGM();
            break;
        case HeatMeter :
            mdata = new MeteringDataHM();
            break;
        case SolarPowerMeter :
            mdata = new MeteringDataSPM();
            break;
        }

        mdata.setDeviceId(deviceId);
        mdata.setDeviceType(deviceType.name());
        mdata.setMDevId(mdevId);
        mdata.setMDevType(mdevType.name());
        mdata.setMeteringType(meteringType.getType());
        mdata.setValue(dformat(meteringValue));
  
        if(channels != null && channels.length > 0){
        	for(int i = 0; i < channels.length; i++){
        		String chN = "ch"+(i+1);
        		BeanUtils.copyProperty(mdata, chN, dformat(dformat(channels[i])));
        	}
        }

        // TODO timezoneId
        mdata.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        String dsttime = DateTimeUtil.getDST(null, meteringDate+meteringTime);
        mdata.setYyyymmdd(dsttime.substring(0, 8));
        mdata.setHhmmss(dsttime.substring(8, 14));
        mdata.setYyyymmddhhmmss(dsttime);
        mdata.setDst(DateTimeUtil.inDST(null, mdata.getYyyymmddhhmmss()));

        // 미터 계약관계가 없을 수도 있다.
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

        // 미터와 모뎀 최종 통신 시간과 값을 갱신한다.
        // if (meter.getLastMeteringValue() == null || meter.getLastMeteringValue() < meteringValue)
        //    meter.setLastMeteringValue(meteringValue);
        log.debug("MDevId[" + mdevId + "] DSTTime["+dsttime+"] LASTREADDate[" + meter.getLastReadDate()+"]");
        // if (meter.getLastReadDate() == null || dsttime.substring(0, 10).compareTo(meter.getLastReadDate().substring(0, 10)) >= 0) {
            if (meterTime != null && !"".equals(meterTime))
                meter.setLastReadDate(meterTime);
            else
                meter.setLastReadDate(dsttime);
            
            meter.setLastMeteringValue(meteringValue);
            Code normalStatus = CommonConstants.getMeterStatusByName(MeterStatus.Normal.name());
            log.debug("MDevId[" + mdevId + "] METER_STATUS[" + (meter.getMeterStatus() == null ? "NULL" : meter.getMeterStatus()) + "]");
            //log.debug("METER_OLD_STATUS[" + (normalStatus==null? "NULL":normalStatus.getName()) + "]");
            if (meter.getMeterStatus() == null || 
                    (meter.getMeterStatus() != null && 
                    !meter.getMeterStatus().getName().equals("CutOff") && 
                    !meter.getMeterStatus().getName().equals("Delete"))){
                meter.setMeterStatus(normalStatus);
                log.debug("MDevId[" + mdevId + "] METER_CHANGED_STATUS[" + meter.getMeterStatus() + "]");
            }

            
            if (meterTime != null && !"".equals(meterTime)) {
                try {
                    long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meteringDate+meteringTime).getTime() - 
                            DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
                    meter.setTimeDiff(diff / 1000);
                }
                catch (ParseException e) {
                    log.warn("MDevId[" + mdevId + "] Check MeterTime[" + meterTime + "] and MeteringTime[" + meteringDate + meteringTime + "]");
                }
            }
            //=> DELETE START 2017.02.28 SP-516
            //// 수검침과 같이 모뎀과 관련이 없는 경우는 예외로 처리한다.
            //if (meter.getModem() != null) {
            //    meter.getModem().setLastLinkTime(dsttime);
            //}
            //=> DELETE END   2017.02.28 SP-516
            
        try {
        	//insert or update
            meteringDataDao.update(mdata);
        }
        catch (Exception e) {
            log.error(e);
        }
        //=> INSERT START 2017.02.28 SP-516
        try {
        	if (meter.getModem() != null) {
        		if(dsttime != null && !"".equals(dsttime) && TimeLocaleUtil.isThisDateValid(dsttime, "yyyyMMddHHmmss")){
            		meter.getModem().setLastLinkTime(dsttime);
        		}else{
        			meter.getModem().setLastLinkTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        		}

        		modemDao.update(meter.getModem());
        	}
        }
        catch (Exception e) {
            log.warn(e);
        }
        try {
        	meterDao.update(meter);
        }
        catch (Exception e) {
        	log.warn(e);
        }
        //=> INSERT END   2017.02.28 SP-516
    }
    
    protected void saveMeteringData(MeteringType meteringType, String meteringDate,
            String meteringTime, double meteringValue, Meter meter, DeviceType deviceType,
            String deviceId, DeviceType mdevType, String mdevId, String meterTime) throws Exception
    {
        log.debug("MeteringType[" + meteringType + "] MeteringDate[" + meteringDate +
                "] MeteringTime[" + meteringTime + "] MeteringValue[" + meteringValue +
                "] DeviceType[" + deviceType + "] DeviceId[" + deviceId +
                "] MDevType[" + mdevType + "] MDevId[" + mdevId + "] MeterTime[" + meterTime + "]");

        //영속
        // meterDao.saveOrUpdate(meter);
        
        MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());

        MeteringData mdata = null;
        switch (meterType) {
        case EnergyMeter :
            mdata = new MeteringDataEM();
            break;
        case WaterMeter :
            mdata = new MeteringDataWM();
            break;
        case GasMeter :
            mdata = new MeteringDataGM();
            break;
        case HeatMeter :
            mdata = new MeteringDataHM();
            break;
        case SolarPowerMeter :
            mdata = new MeteringDataSPM();
            break;
        }

        mdata.setDeviceId(deviceId);
        mdata.setDeviceType(deviceType.name());
        mdata.setMDevId(mdevId);
        mdata.setMDevType(mdevType.name());
        mdata.setMeteringType(meteringType.getType());
        mdata.setValue(dformat(meteringValue));

        // TODO timezoneId
        mdata.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        String dsttime = DateTimeUtil.getDST(null, meteringDate+meteringTime);
        mdata.setYyyymmdd(dsttime.substring(0, 8));
        mdata.setHhmmss(dsttime.substring(8, 14));
        mdata.setYyyymmddhhmmss(dsttime);
        mdata.setDst(DateTimeUtil.inDST(null, mdata.getYyyymmddhhmmss()));

        // 미터 계약관계가 없을 수도 있다.
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

        // 미터와 모뎀 최종 통신 시간과 값을 갱신한다.
        // if (meter.getLastMeteringValue() == null || meter.getLastMeteringValue() < meteringValue)
        //    meter.setLastMeteringValue(meteringValue);
        log.debug("MDevId[" + mdevId + "] DSTTime["+dsttime+"] LASTREADDate[" + meter.getLastReadDate()+"]");
        // if (meter.getLastReadDate() == null || dsttime.substring(0, 10).compareTo(meter.getLastReadDate().substring(0, 10)) >= 0) {
        if (meterTime != null && !"".equals(meterTime))
            meter.setLastReadDate(meterTime);
        else
            meter.setLastReadDate(dsttime);
        
        meter.setLastMeteringValue(meteringValue);
        Code normalStatus = CommonConstants.getMeterStatusByName(MeterStatus.Normal.name());
        log.debug("MDevId[" + mdevId + "] METER_STATUS[" + (meter.getMeterStatus() == null ? "NULL" : meter.getMeterStatus()) + "]");
        //log.debug("METER_OLD_STATUS[" + (normalStatus==null? "NULL":normalStatus.getName()) + "]");
        if (meter.getMeterStatus() == null || 
                (meter.getMeterStatus() != null && 
                !meter.getMeterStatus().getName().equals("CutOff") && 
                !meter.getMeterStatus().getName().equals("Delete"))){
            meter.setMeterStatus(normalStatus);
            log.debug("MDevId[" + mdevId + "] METER_CHANGED_STATUS[" + meter.getMeterStatus() + "]");
        }

        
        if (meterTime != null && !"".equals(meterTime)) {
            try {
                long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meteringDate+meteringTime).getTime() - 
                        DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
                meter.setTimeDiff(diff / 1000);
            }
            catch (ParseException e) {
                log.warn("MDevId[" + mdevId + "] Check MeterTime[" + meterTime + "] and MeteringTime[" + meteringDate + meteringTime + "]");
            }
        }
        
        // 수검침과 같이 모뎀과 관련이 없는 경우는 예외로 처리한다.
        //   if (meter.getModem() != null) {
        //       meter.getModem().setLastLinkTime(dsttime);
        //   }
        // }
    
        try {
            meteringDataDao.update(mdata);
            meterDao.update(meter);
        }
        catch (Exception e) {
            log.warn(e);
        }
    }

    protected void saveMeteringDataSP(String meteringDate, String meteringTime, Meter meter) throws Exception
    {
        log.debug("saveMeteringData SP MeteringDate[" + meteringDate +
                "] MeteringTime[" + meteringTime + "]");
   
        String dsttime = DateTimeUtil.getDST(null, meteringDate+meteringTime);
        
        try {
        	if (meter.getModem() != null) {
        		
        		if(TimeLocaleUtil.isThisDateValid(dsttime, "yyyyMMddHHmmss")){
            		meter.getModem().setLastLinkTime(dsttime);
        		}else{
            		meter.getModem().setLastLinkTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        		}

        		modemDao.update_requires_new(meter.getModem());
        	}
        }
        catch (Exception e) {
            log.warn(e);
        }
        try {
        	meterDao.update_requires_new(meter);
        }
        catch (Exception e) {
        	log.warn(e);
        }
    }    
   
    private ChannelCalcMethod getChMethod(ChannelConfig[] ccs, int ch) {
        for (ChannelConfig cc : ccs) {
            if (cc.getChannelIndex() == ch) {
                if (cc.getChannel().getChMethod() != null)
                    return cc.getChannel().getChMethod();
                else return ChannelCalcMethod.MAX;
            }
        }
        
        return ChannelCalcMethod.MAX;
    }
    
    private void saveLPDataUsingLPTime(MeteringType meteringType, Map<Integer, LinkedList<MeteringLP>> lpMap, 
    		Meter meter, DeviceType mdevType) throws Exception {
    	log.info("Meter Serial:"+meter.getMdsId() +", lp map size:"+lpMap.size());
    	if (lpMap.size() == 0) {
             throw new Exception("LP size is 0!!!");
        }
    	
//    	boolean dayMonthSave = Boolean.parseBoolean(FMPProperty.getProperty("daymonth.save", "true"));
    	
    	MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());
    	
    	if(Boolean.valueOf(FMPProperty.getProperty("fep.lp.using.procedure", "true"))
    			&& (meterType == MeterType.EnergyMeter || meterType == MeterType.WaterMeter)) {
    		saveLPDataUsingLPTimeUsingProcedure(meteringType, lpMap, meter, mdevType);
    	} else {
    		saveLPDataUsingLPTimeUsingJPA(meteringType, lpMap, meter, mdevType);
    	}
    	
//    	if(dayMonthSave) {
    		//saveDayDataUsingLPTimeUsingJPA(meteringType, lpMap, meter, mdevType);
			//saveMonthDataUsingLPTimeUsingJPA(meteringType, lpMap, meter, mdevType);
//    	}
    }
    
    private void saveLPDataUsingLPTimeUsingProcedure(MeteringType meteringType, Map<Integer, LinkedList<MeteringLP>> lpMap, 
    		Meter meter, DeviceType mdevType) throws Exception {
    	
    	StringBuilder appendBuilder = null;    	 
    	MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());
    	MeterConfig meterConfig = (MeterConfig)meter.getModel().getDeviceConfig();
    	
    	makeChLPsUsingLPTime(lpMap, meter);
    	
    	LinkedList<MeteringLP> ch1List = lpMap.get(ElectricityChannel.Usage.getChannel());
    	
        log.debug("Using JPA - Procedure | mdevId["+meter.getMdsId()+"] channel["+lpMap.size()+"] channel[1] count["+lpMap.get(1).size()+"]");
        Iterator<Integer> channels = lpMap.keySet().iterator();

        String filePrefix = "LP_EM_EXT_";
        String procedureName = "LP_EXTERNAL_MERGE"; //수도도 정규화가 적용되면서, 타입에 따라 구분 가능한 변수 추가.
        if(meteringType != null && meterType.equals(meterType.EnergyMeter)) { //현재 EnergyMeter 만 표시, 만약 다른 Meter Type 필요하다면 하단에 처리 필요
        	appendBuilder = new StringBuilder();
            while(channels.hasNext()) {
            	Integer channel = channels.next();
            	LinkedList<MeteringLP> lpList = lpMap.get(channel);
            	
        		for(MeteringLP mLP : lpList) {
        			mLP.setModemSerial(meter.getModem().getDeviceSerial());
        			appendBuilder.append(mLP.getExternalTableValue());
            	}	
            }
            filePrefix = "LP_EM_EXT_";
            procedureName = "LP_EXTERNAL_MERGE";
        }else if(meteringType != null && meterType.equals(meterType.WaterMeter)) { //EnergyMeter와 동일하지만 나중을 위해서 분리
            appendBuilder = new StringBuilder();
            while(channels.hasNext()) {
                Integer channel = channels.next();
                LinkedList<MeteringLP> lpList = lpMap.get(channel);

                for(MeteringLP mLP : lpList) {
                    mLP.setModemSerial(meter.getModem().getDeviceSerial());
                    appendBuilder.append(mLP.getExternalTableValue());
                }
            }
            filePrefix = "LP_WM_EXT_";
            procedureName = "LP_EXTERNAL_MERGE_WM";
        }
        
    	if(appendBuilder != null) {
        	String mappingID = AimirThreadMapper.getInstance().getMapperId(Thread.currentThread().getId());
        	String filename = filePrefix + mappingID; //"LP_EM_EXT_xxx"
        	
        	ExternalTableLogger logger = new ExternalTableLogger();
        	logger.writeObject(filename, appendBuilder.toString());
        	
        	Map<String, Object> parameter = new HashMap<String, Object>();
        	parameter.put("PROCEDURE_NAME", procedureName); //"LP_EXTERNAL_MERGE"
        	
        	parameter.put("THREAD_NUM", mappingID);
        	String procedureReuslt = lpEMDao.callProcedure(parameter);        	
        	
        	log.info("mappingID ["+mappingID+"] filename["+filename+"] procedure["+parameter.get("PROCEDURE_NAME")+"] procedureReuslt ["+procedureReuslt+"]");
        	
        	if(procedureReuslt.contains("ERROR")) {
        		try {
        			ProcedureRecoveryLogger prLogger = new ProcedureRecoveryLogger();
        			prLogger.makeLPOfProcedureERR(appendBuilder.toString());
        		}catch(Exception e) {
        			log.error(e,e);
        		}
        	}
        	logger.deleteFile(filename);
        	AimirThreadMapper.getInstance().deleteMapperId(Thread.currentThread().getId());
        }
    }    
    
    private void saveLPDataUsingLPTimeUsingJPA(MeteringType meteringType, Map<Integer, LinkedList<MeteringLP>> lpMap, 
    		Meter meter, DeviceType mdevType) throws Exception {
    	Map<LpPk, MeteringLP> _chLPs = null;
    	Map<Integer, LinkedList<MeteringLP>> addMap = new HashMap<Integer, LinkedList<MeteringLP>>();
    	Map<Integer, LinkedList<MeteringLP>> updateMap = new HashMap<Integer, LinkedList<MeteringLP>>();
    	
    	MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());
    	MeterConfig meterConfig = (MeterConfig)meter.getModel().getDeviceConfig();
    	
    	makeChLPsUsingLPTime(lpMap, meter);
    	
    	LinkedList<MeteringLP> ch1List = lpMap.get(ElectricityChannel.Usage.getChannel());
    	
    	// 전 채널 검침데이타를 가져온다.
        LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
        condition.add(new Condition("id.yyyymmddhhmiss", new Object[]{ch1List.get(0).getYyyymmddhhmiss(),
        		ch1List.get(ch1List.size() - 1).getYyyymmddhhmiss()}, null, Restriction.BETWEEN));
        condition.add(new Condition("id.mdevId", new Object[]{meter.getMdsId()}, null, Restriction.EQ));
        condition.add(new Condition("id.dst", new Object[]{ch1List.get(0).getDst()}, null, Restriction.EQ));
        condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ)); //index order
        _chLPs = listLP(condition, meterType);
                
        //day, month는 View에서 자동계산하도록 수정됨
        
        log.debug("Using JPA - Persist() | mdevId["+meter.getMdsId()+"] channel["+lpMap.size()+"] channel[1] count["+lpMap.get(1).size()+"]");
        Iterator<Integer> channels = lpMap.keySet().iterator();
        while(channels.hasNext()) {
        	Integer channel = channels.next();
        	LinkedList<MeteringLP> lpList = lpMap.get(channel);
        	
        	for(MeteringLP mLP : lpList) {
        		MeteringLP _mLP = null;
        		mLP.setModemSerial(meter.getModem().getDeviceSerial());
        		if((_mLP = _chLPs.get(mLP.getId())) != null) {
        			//update source
        			// 2012.10.16 추가. Normal 인 경우만 업데이트하지 않는다.
        			// 2019.07.22 Skip 해야할 때를 정해서 아래 루틴에 적용 필요
                	if(meteringType.getType() == MeteringType.Normal.getType()) {
                		continue;
                	}
                	
                	LinkedList<MeteringLP> updateList = null;
        			
        			if(updateMap.get(channel) == null) {
        				updateList = new LinkedList<MeteringLP>();
        			} else {
        				updateList = updateMap.get(channel);
        			}
        			
        			updateList.add(mLP);
        			updateMap.put(channel, updateList);
        		} else{
        			//insert source
        			LinkedList<MeteringLP> addList = null;
        			
        			if(addMap.get(channel) == null) {
        				addList = new LinkedList<MeteringLP>();
        			} else {
        				addList = addMap.get(channel);
        			}
        			
        			addList.add(mLP);
        			addMap.put(channel, addList);
        		}
        	}
        }
        
        if(addMap != null && addMap.size() > 0) {
        	addMeteringDataUsingJPA(meter.getMdsId(), meterType, addMap);
        }
        
        if(updateMap != null && updateMap.size() > 0) {
        	updateMeteringDataUsingJPA(meter.getMdsId(), meterType, updateMap);
        }
    }
    
    private void saveDayDataUsingLPTimeUsingJPA(MeteringType meteringType, Map<Integer, LinkedList<MeteringLP>> lpMap, 
    		Meter meter, DeviceType mdevType) throws Exception {
    	Map<DayPk, MeteringDay> _chDays = null;
    	MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());
    	
    	String mdevId = lpMap.get(1).get(0).getMDevId();
    	
    	LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
    	condition.add(new Condition("id.yyyymmdd", new Object[]{lpMap.get(1).get(0).getYyyymmdd(),
    			lpMap.get(1).get(lpMap.get(1).size() - 1).getYyyymmdd()}, null, Restriction.BETWEEN));
        condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
        condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
        condition.add(new Condition("id.dst", new Object[]{lpMap.get(1).get(0).getDst()}, null, Restriction.EQ));
        
    	_chDays = listDay(condition, meterType, lpMap.size());
    	log.debug("MDevId[" + mdevId + "] lpMap[" + lpMap.size() + "] _chDays[" + _chDays.size() + "]");
    	
    	for(Integer ch : lpMap.keySet()) {
    		//20190404 정규화 이전에는 시간을 컬럼으로 구분하여 1 row안에 모든 시간별 LP가 존재
    		//정규화에는 시간별 row가 생성되기 때문에 LP을 시간별로 구분할 필요가 있음
    		Map<String, LinkedList<MeteringLP>> _hourLpMap = new HashMap<String, LinkedList<MeteringLP>>();
    		LinkedList<MeteringLP> lpData = lpMap.get(ch);
    		
    		ElectricityChannel electricityChannel = CommonConstants.getElectricityChannel(ch);
    		
    		for(MeteringLP _lp : lpData) {
    			String hh = _lp.getHour();
    			LinkedList<MeteringLP> list = null;
    			
    			if(!_hourLpMap.containsKey(hh)) {
    				list = new LinkedList<MeteringLP>();
    				list.add(_lp);
    			} else {
    				list = _hourLpMap.get(hh);
    				list.add(_lp);
    			}
    			_hourLpMap.put(hh, list);
    		}
    		
    		//시간별로 정의된 내용을 기반으로 dayEM 생성
    		for(String hh : _hourLpMap.keySet()) {
    			LinkedList<MeteringLP> hourList = _hourLpMap.get(hh);
    			MeteringLP firstLp = hourList.get(0);
    			MeteringLP lastLp = hourList.get(hourList.size() - 1);
    			
    			DayPk dayPk = new DayPk();
    			dayPk.setChannel(ch);
        		dayPk.setDst(firstLp.getDst());
        		dayPk.setMDevId(mdevId);
        		dayPk.setMDevType(mdevType.name());
        		dayPk.setYyyymmdd(firstLp.getYyyymmdd());
        		//dayPk.setHh(firstLp.getHour());
        		
        		MeteringDay _lpDay = _chDays.get(dayPk);
        		if(_lpDay == null) {
        			_lpDay = newMeteringDay(meteringType, meterType, meter, 
        					firstLp.getDeviceType(), firstLp.getDeviceId(), mdevType, mdevId);
        			_lpDay.setId(dayPk);
        			
        			if(ch == ElectricityChannel.Co2.getChannel() ||
        					ch == ElectricityChannel.PowerFactor.getChannel() ||
        					ch == ElectricityChannel.ValidationStatus.getChannel()) {
        				_lpDay.setBaseValue(0.0);    				
        			} else {
        				//이미 정렬된 상태이기 때문에 첫번째 값이 가장 00분에 가까운 시간의 값이다.
        				_lpDay.setBaseValue(firstLp.getValue());
        			}
        			
        			//정규화 이전 dayType 변수는 삭제
        		}
        		
        		if (_lpDay.getContract() == null && meter.getContract() != null) {
        			_lpDay.setContract(meter.getContract());
        		}
        		
        		switch(electricityChannel) {
        		case ValidationStatus:
        			for(int i=hourList.size() - 1; i <=0; i--) {
        				MeteringLP item = hourList.get(i);
        				if(item.getIntervalYN() == 1) {
        					double v = item.getValue();
        					//_lpDay.setValue(v);        					
        					break;
        				}
        			}
        			break;
        		case Integrated:
        			break;
        		case PowerFactor:
        			
        			break;
        		default:
        			
        			break;
        		}
        		
        		
    		}
    		
   		
    		
    	}
    	
    }
    
    private void saveMonthDataUsingLPTimeUsingJPA(MeteringType meteringType, Map<Integer, LinkedList<MeteringLP>> lpMap, 
    		Meter meter, DeviceType mdevType) throws Exception {
    	
    }
    
    private void makeChLPsUsingLPTime(Map<Integer, LinkedList<MeteringLP>> lpMap, Meter meter) throws Exception {
    	double lpThreshold = Double.parseDouble(FMPProperty.getProperty("lp.threshold", "300"));
    	MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());
    	int chLpCnt = 0;
    	
    	//channel info 
    	//Co2(0), Usage(1), Integrated(98), PowerFactor(99), ValidationStatus(100);
    	
        // 미터의 채널 정보를 가져온다.
        ChannelConfig[] channels = null;
        if (meter.getModel() != null && meter.getModel().getDeviceConfig() != null)
            channels = ((MeterConfig)meter.getModel().getDeviceConfig()).getChannels().toArray(new ChannelConfig[0]);
        else channels = new ChannelConfig[0];
        
        double[] sum = new double[lpMap.size()];
        sum[0] = lpMap.get(ElectricityChannel.Usage.getChannel()).get(0).getValue();
        for(int i=1; i<sum.length; i++) {
        	sum[i] = 0d;
        }
        
        Iterator<Integer> channelList = lpMap.keySet().iterator();
        while(channelList.hasNext()) {
        	int valueCnt = 0;
        	Integer ch = channelList.next();
        	LinkedList<MeteringLP> lpList = lpMap.get(ch);
        	chLpCnt = lpList.size();
        	
        	for(MeteringLP lp : lpList) {
        		double tmp = dformat(lp.getValue());
        		if (getChMethod(channels, lp.getChannel()) == ChannelCalcMethod.SUM 
    					&& (tmp < 0.0 || tmp > lpThreshold)
	                    && meter.getModel().getName().equals(MeterVendor.KAMSTRUP.getName())) {
    				try {
    					EventUtil.sendEvent("Meter Value Alarm",
                                TargetClass.valueOf(meter.getMeterType().getName()),
                                meter.getMdsId(),
                                new String[][] {{"message", "LP DateTime[" + lp.id.getYyyymmddhhmiss() + "] Value[" + tmp + "]"}}
                                );
    				}catch(Exception e) { }
        		} else {
        			 valueCnt++;
        		}
        		
        		switch (getChMethod(channels, lp.getChannel())) {
        		case SUM:
        			sum[ch - 1] += lp.getValue();
        			break;
        		case MAX:
        			sum[ch - 1] = lp.getValue();
        			break;
        		case AVG:
        			sum[ch - 1] += lp.getValue();
        			sum[ch - 1] /= valueCnt;
        			break;
        		}
        	}
        }
        
//        MeteringLP[] co = new MeteringLP[chLpCnt];
//        MeteringLP[] flag = new MeteringLP[chLpCnt];
        MeteringLP[] integratedFlag = new MeteringLP[chLpCnt];
        
//        Co2Formula co2f = co2FormulaDao.getCo2FormulaBySupplyType(meterType.getServiceType());
        LinkedList<MeteringLP> ch1List = lpMap.get(ElectricityChannel.Usage.getChannel());
        
        for(int i=0; i<chLpCnt; i++) {
            MeteringLP mLP = ch1List.get(i);
            
//            co[i] = newMeteringLP(meterType, mLP);
//            flag[i] = newMeteringLP(meterType, mLP);
            integratedFlag[i] = newMeteringLP(meterType, mLP);
            
//            co[i].setChannel(ElectricityChannel.Co2.getChannel());
//            flag[i].setChannel(ElectricityChannel.ValidationStatus.getChannel());
            integratedFlag[i].setChannel(ElectricityChannel.Integrated.getChannel());
            
//            co[i].setValue(dformat(mLP.getValue() * co2f.getCo2factor()));
//            flag[i].setValue(mLP.getLpFlag());
            integratedFlag[i].setValue((double)IntegratedFlag.NOTSENDED.getFlag());
        }
        
//        lpMap.put(ElectricityChannel.Co2.getChannel(), new LinkedList<>(Arrays.asList(co)));
//        lpMap.put(ElectricityChannel.ValidationStatus.getChannel(), new LinkedList<>(Arrays.asList(flag)));
        //OPF-713 || Unnecessary channel delete(integrated, power factor)
        //lpMap.put(ElectricityChannel.Integrated.getChannel(), new LinkedList<>(Arrays.asList(integratedFlag)));
    }
    
    public void addMeteringDataUsingJPA(String mdevId, MeterType meterType, 
    		Map<Integer,LinkedList<MeteringLP>> addMap) 
    throws Exception {
    	
    	Iterator<Integer> channels = addMap.keySet().iterator();
    	while(channels.hasNext()) {
    		Integer ch = channels.next();
    		LinkedList<MeteringLP> list = addMap.get(ch);
    		
    		if(list != null) {
        		for(MeteringLP lp : list) {
        			log.debug("[ADD] mdevId["+mdevId+"] ch["+ch+"] meterType["+meterType+"] yyymmddhhmiss["+lp.getYyyymmddhhmiss()+"] "
        					+ "value["+lp.getValue()+"] cvalue["+lp.getIntervalYN()+"] DST["+lp.getDst()+"] contractId["+lp.getContractId()+"]");
        			
        			switch(meterType) {
        			case EnergyMeter:
        				lpEMDao.add((LpEM)lp);
        			break;
        			case WaterMeter :
        				lpWMDao.add((LpWM)lp);
                    break;
                    case GasMeter :
                        lpGMDao.add((LpGM)lp);
                        break;
                    case HeatMeter :
                        lpHMDao.add((LpHM)lp);
                        break;
                    case SolarPowerMeter :
                        lpSPMDao.add((LpSPM)lp);
                        break;
                    case Inverter :
                    	lpEMDao.add((LpEM)lp);
                    break;
                    default:
                    break;
        			}
        		}
    		}
    	}
    }
    
    public void updateMeteringDataUsingJPA(String mdevId, MeterType meterType, 
    		Map<Integer,LinkedList<MeteringLP>> updateMap) 
    throws Exception {
    	Iterator<Integer> channels = updateMap.keySet().iterator();
    	
    	Properties props = new Properties();
        props.setProperty("mdevId", mdevId);
        
    	while(channels.hasNext()) {
    		Integer ch = channels.next();
    		LinkedList<MeteringLP> list = updateMap.get(ch);
    		
    		if(list != null) {
        		for(MeteringLP lp : list) {
        			log.debug("[UPDATE] mdevId["+mdevId+"] ch["+ch+"] meterType["+meterType+"] yyymmddhhmiss["+lp.getYyyymmddhhmiss()+"] value["+lp.getValue()+"] cvalue["+lp.getIntervalYN()+"]");
        			        			
        			switch(meterType) {
        			case EnergyMeter:
        				lpEMDao.update((LpEM)lp, props);
        			break;
        			case WaterMeter :
        				lpWMDao.update((LpWM)lp, props);
                    break;
                    case GasMeter :
                        lpGMDao.update((LpGM)lp, props);
                        break;
                    case HeatMeter :
                        lpHMDao.update((LpHM)lp, props);
                        break;
                    case SolarPowerMeter :
                        lpSPMDao.update((LpSPM)lp, props);
                        break;
                    case Inverter :
                    	lpEMDao.update((LpEM)lp, props);
                    break;
                    default:
                    break;
        			}
        		}
    		}
    	}
    }

    private MeteringLP newMeteringLP(MeterType meterType, MeteringLP lp) {
        MeteringLP _lp = null;
        switch (meterType) {
        case EnergyMeter :
            _lp = new LpEM();
            break;
        case WaterMeter :
            _lp = new LpWM();
            break;
        case GasMeter :
            _lp = new LpGM();
            break;
        case HeatMeter :
            _lp = new LpHM();
            break;
        case SolarPowerMeter :
            _lp = new LpSPM();
            break;
        case Inverter :
            _lp = new LpEM();
            break;            
        }

        _lp.setContract(lp.getContract());
        _lp.setDeviceId(lp.getDeviceId());
        _lp.setDeviceType(lp.getDeviceType().name());
        _lp.setDst(lp.getDst());
        _lp.setDate(lp.getYyyymmddhhmiss());
        _lp.setMDevId(lp.getMDevId());
        _lp.setMDevType(lp.getMDevType().name());
        _lp.setMeteringType(lp.getMeteringType());
        _lp.setIntervalYN(lp.getIntervalYN());
        _lp.setWriteDate(lp.getWriteDate());
        _lp.setModemTime(lp.getModemTime());
        
        switch (lp.getMDevType()) {
        case Meter :
            _lp.setMeter(lp.getMeter());
            break;
        case Modem :
            break;
        case EndDevice :
        	break;
        }

        return _lp;
    }

    private MeteringDay newMeteringDay(MeteringType meteringType, MeterType meterType,
            Meter meter, DeviceType deviceType, String deviceId, DeviceType mdevType,
            String mdevId) {
        MeteringDay day = null;
        switch (meterType) {
        case EnergyMeter :
            day = new DayEM();
            break;
        case WaterMeter :
            day = new DayWM();
            break;
        case GasMeter :
            day = new DayGM();
            break;
        case HeatMeter :
            day = new DayHM();
            break;
        case SolarPowerMeter :
            day = new DaySPM();
            break;
        case Inverter :
            day = new DayEM();
            break;            
        }

        day.setDeviceId(deviceId);
        day.setDeviceType(deviceType.name());
        day.setMDevId(mdevId);
        day.setMDevType(mdevType.name());
        day.setMeteringType(meteringType.getType());
        day.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

        if (meter.getContract() != null) {
            day.setContract(meter.getContract());
            // 2012.04.13 sic를 정보를 입력한다.
            //day.setSic(meter.getContract().getSic() != null ? meter.getContract().getSic().getCode() : null);
        }

        switch (mdevType) {
        case Meter :
            day.setMeter(meter);
            day.setSupplier(meter.getSupplier());
            if (meter.getModem() != null)
                day.setModem(meter.getModem());
            break;
        case Modem :
            day.setModem(meter.getModem());
            day.setSupplier(meter.getSupplier());
            break;
        case EndDevice :
        }
        return day;
    }
    
    /*
         정규화로 인하여 함수 재설정
    private MeteringDay newMeteringDay(MeteringType meteringType, MeterType meterType,
            Meter meter, DeviceType deviceType, String deviceId, DeviceType mdevType,
            String mdevId) {
        MeteringDay day = null;
        switch (meterType) {
        case EnergyMeter :
            day = new DayEM();
            break;
        case WaterMeter :
            day = new DayWM();
            break;
        case GasMeter :
            day = new DayGM();
            break;
        case HeatMeter :
            day = new DayHM();
            break;
        case SolarPowerMeter :
            day = new DaySPM();
            break;
        case Inverter :
            day = new DayEM();
            break;            
        }

        day.setDeviceId(deviceId);
        day.setDeviceType(deviceType.name());
        day.setMDevId(mdevId);
        day.setMDevType(mdevType.name());
        day.setMeteringType(meteringType.getType());
        day.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

        if (meter.getContract() != null) {
            day.setContract(meter.getContract());
            // 2012.04.13 sic를 정보를 입력한다.
            day.setSic(meter.getContract().getSic() != null ? meter.getContract().getSic().getCode() : null);
        }

        switch (mdevType) {
        case Meter :
            day.setMeter(meter);
            day.setLocation(meter.getLocation());
            day.setSupplier(meter.getSupplier());
            if (meter.getModem() != null)
                day.setModem(meter.getModem());
            break;
        case Modem :
            day.setModem(meter.getModem());
            day.setLocation(meter.getModem().getLocation());//TODO!!!!!!!! 로직 검증 필요 미터 객체가 없을때 모뎀정보를 가져오지 못함
            day.setSupplier(meter.getSupplier());
            break;
        case EndDevice :
        }
        return day;
    }
    */
    
    private Map<LpPk, MeteringLP> listLP(Set<Condition> condition, MeterType meterType)  {
    	Map<LpPk, MeteringLP> map = new HashMap<LpPk, MeteringLP>();
    	
    	List list = new ArrayList<MeteringLP>();
        switch (meterType) {
        case EnergyMeter :
            list = lpEMDao.findByConditions(condition);
            break;
        case WaterMeter :
            list = lpWMDao.findByConditions(condition);
            break;
        case GasMeter :
            list = lpGMDao.findByConditions(condition);
            break;
        case HeatMeter :
            list = lpHMDao.findByConditions(condition);
            break;
        case VolumeCorrector :
            break;
        case SolarPowerMeter :
            list = lpSPMDao.findByConditions(condition);
            break;
        case Inverter :
            list = lpEMDao.findByConditions(condition);
            break;            
        }
        
        if(list != null) {
	        for(int i=0; i<list.size(); i++) {
	        	MeteringLP mLp = (MeteringLP)list.get(i);
	        	map.put(mLp.getId(), mLp);
	        }
        }
     
        return map;
    }

    /**
     * Self Read Data(for use Daily Data)
     * @param tou_block
     * @param meter
     * @param deviceType
     * @param deviceId
     * @param mdevType
     * @param mdevId
     */
    protected void saveDayBilling(TOU_BLOCK[] tou_block, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    {
        log.debug("save DailyBillingData["+meter.getMdsId());
        
        try{  

            if(tou_block == null || tou_block.length ==0 || tou_block[0] == null){
                log.debug("MDevId[" + mdevId + "] save DailyBillingData TOU data empty!");
                return;
            }
            String billtime = tou_block[0].getResetTime();
            if(billtime != null && billtime.length() == 14){

                BillingDayEM bill = new BillingDayEM();

                LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
                condition.add(new Condition("id.hhmmss", new Object[]{billtime.substring(8,14)}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmdd", new Object[]{billtime.substring(0,8)}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
                
                List<Object> ret = billingDayEMDao.findTotalCountByConditions(condition);
                
                Long count = (Long) ret.get(0);

                // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 설정한다.
                if(count != null && count.longValue() > 0) {
                   return;
                } 

                /*
                // 기존에 Billing정보가 존재하는지 검색한다. ADD START 20120404 by eunmiae
                // 스케줄러에서 변경된 빌링 정보가 본 기능 실행후 NULL로 변경되는 오류 수정
                BillingDayEM bill = null;

                LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmdd", new Object[]{billtime.substring(0,8)}, null, Restriction.EQ));
                condition.add(new Condition("id.hhmmss", new Object[]{billtime.substring(8,14)}, null, Restriction.EQ));

                List<BillingDayEM> billsByPk = billingDayEMDao.findByConditions(condition);

                // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 설정한다.
                if(billsByPk != null && billsByPk.size() != 0) {
                    bill = billsByPk.get(0);
                } else {
                    bill = new BillingDayEM();
                }
                // 기존에 Billing정보가 존재하는지 검색한다. ADD END 20120404 by eunmiae
                 * 
                 */

                if(meter.getContract() != null){
                    bill.setContract(meter.getContract());
                }
                if(meter.getSupplier() != null){
                    bill.setSupplier(meter.getSupplier());
                }
                if(meter.getLocation() != null){
                    bill.setLocation(meter.getLocation());
                }
                //bill.setBill(bill);//TODO TARIFF SET
                bill.setMDevType(mdevType.name());
                bill.setMDevId(mdevId);
                bill.setMeter(meter);
                bill.setModem(meter.getModem());
                bill.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

                if(tou_block.length >= 1){
                    bill.setYyyymmdd(tou_block[0].getResetTime().substring(0,8));
                    bill.setHhmmss(tou_block[0].getResetTime().substring(8,14));
                    bill.setActiveEnergyRateTotal((Double)tou_block[0].getSummation(0));
                    bill.setActiveEnergyImportRateTotal((Double)tou_block[0].getSummation(0));
                    bill.setActivePowerMaxDemandRateTotal((Double)tou_block[0].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRateTotal((String)tou_block[0].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRateTotal((Double)tou_block[0].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRateTotal((String)tou_block[0].getEventTime(0));
                    bill.setCumulativeDemandRateTotal((Double)tou_block[0].getCumDemand(0));
                    bill.setReactiveEnergyRateTotal((Double)tou_block[0].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRateTotal((String)tou_block[0].getEventTime(1));
                    bill.setReactivePowerMaxDemandRateTotal((Double)tou_block[0].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRateTotal((Double)tou_block[0].getCumDemand(1));
                }
                if(tou_block.length >= 2){
                    bill.setActiveEnergyRate1((Double)tou_block[1].getSummation(0));
                    bill.setActiveEnergyImportRate1((Double)tou_block[1].getSummation(0));
                    bill.setActivePowerMaxDemandRate1((Double)tou_block[1].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate1((String)tou_block[1].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate1((Double)tou_block[1].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate1((String)tou_block[1].getEventTime(0));
                    bill.setCumulativeDemandRate1((Double)tou_block[1].getCumDemand(0));
                    bill.setReactiveEnergyRate1((Double)tou_block[1].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate1((String)tou_block[1].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate1((Double)tou_block[1].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate1((Double)tou_block[1].getCumDemand(1));
                }

                if(tou_block.length >= 3){
                    bill.setActiveEnergyRate2((Double)tou_block[2].getSummation(0));
                    bill.setActiveEnergyImportRate2((Double)tou_block[2].getSummation(0));
                    bill.setActivePowerMaxDemandRate2((Double)tou_block[2].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate2((String)tou_block[2].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate2((Double)tou_block[2].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate2((String)tou_block[2].getEventTime(0));
                    bill.setCumulativeDemandRate2((Double)tou_block[2].getCumDemand(0));
                    bill.setReactiveEnergyRate2((Double)tou_block[2].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate2((String)tou_block[2].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate2((Double)tou_block[2].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate2((Double)tou_block[2].getCumDemand(1));
                }

                if(tou_block.length >= 4){
                    bill.setActiveEnergyRate3((Double)tou_block[3].getSummation(0));
                    bill.setActiveEnergyImportRate3((Double)tou_block[3].getSummation(0));
                    bill.setActivePowerMaxDemandRate3((Double)tou_block[3].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate3((String)tou_block[3].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate3((Double)tou_block[3].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate3((String)tou_block[3].getEventTime(0));
                    bill.setCumulativeDemandRate3((Double)tou_block[3].getCumDemand(0));
                    bill.setReactiveEnergyRate3((Double)tou_block[3].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate3((String)tou_block[3].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate3((Double)tou_block[3].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate3((Double)tou_block[3].getCumDemand(1));
                }

                if(tou_block.length >= 5){
                    bill.setActiveEnergyRate4((Double)tou_block[4].getSummation(0));
                    bill.setActiveEnergyImportRate4((Double)tou_block[4].getSummation(0));
                    bill.setActivePowerMaxDemandRate4((Double)tou_block[4].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate4((String)tou_block[4].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate4((Double)tou_block[4].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate4((String)tou_block[4].getEventTime(0));
                    bill.setCumulativeDemandRate4((Double)tou_block[4].getCumDemand(0));
                    bill.setReactiveEnergyRate4((Double)tou_block[4].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate4((String)tou_block[4].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate4((Double)tou_block[4].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate4((Double)tou_block[4].getCumDemand(1));
                }

                billingDayEMDao.add(bill);
            }
        }catch(Exception e){
         log.error(e,e);
        }

    }

    protected void saveMonthlyBilling(TOU_BLOCK[] tou_block, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    {
        log.debug("save MonthlyBillingData["+meter.getMdsId());
        
        try{  

            if(tou_block == null || tou_block.length ==0 || tou_block[0] == null){
                log.debug("MDevId[" + mdevId + "] save MonthlyBillingData TOU data empty!");
                return;
            }
            String billtime = tou_block[0].getResetTime();
            if(billtime != null && billtime.length() == 14){

                log.debug("MDevId[" + mdevId + "] billtime ["+billtime+"]");
                
                
                BillingMonthEM bill = new BillingMonthEM();

                LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
                condition.add(new Condition("id.hhmmss", new Object[]{billtime.substring(8,14)}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmdd", new Object[]{billtime.substring(0,8)}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
                
                List<Object> ret = billingMonthEMDao.findTotalCountByConditions(condition);
                
                Long count = (Long) ret.get(0);

                // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 설정한다.
                if(count != null && count.longValue() > 0) {
                    return;
                }
                
                /*
//                BillingMonthEM bill = new BillingMonthEM();
                
                // 기존에 Billing정보가 존재하는지 검색한다. ADD START 20120404 by eunmiae
                // 스케줄러에서 변경된 빌링 정보가 본 기능 실행후 NULL로 변경되는 오류 수정
                BillingMonthEM bill = null;

                LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmdd", new Object[]{billtime.substring(0,8)}, null, Restriction.EQ));
                condition.add(new Condition("id.hhmmss", new Object[]{billtime.substring(8,14)}, null, Restriction.EQ));

                List<BillingMonthEM> billsByPk = billingMonthEMDao.findByConditions(condition);

                // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 갱신한다.
                if(billsByPk != null && billsByPk.size() != 0) {
                    bill = billsByPk.get(0);
                } else {
                    bill = new BillingMonthEM();
                }
                // 기존에 Billing정보가 존재하는지 검색한다. ADD END 20120404 by eunmiae
                 * 
                 */

                if(meter.getContract() != null){
                    bill.setContract(meter.getContract());
                }
                if(meter.getSupplier() != null){
                    bill.setSupplier(meter.getSupplier());
                }
                if(meter.getLocation() != null){
                    bill.setLocation(meter.getLocation());
                }
                //bill.setBill(bill);//TODO TARIFF SET
                bill.setMDevType(mdevType.name());
                bill.setMDevId(mdevId);
                bill.setMeter(meter);
                bill.setModem(meter.getModem());
                bill.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

                if(tou_block.length >= 1){
                    bill.setYyyymmdd(tou_block[0].getResetTime().substring(0,8));
                    bill.setHhmmss(tou_block[0].getResetTime().substring(8,14));
                    if(tou_block[0].getSummations()!=null && tou_block[0].getSummations().size()>0){
                        bill.setActiveEnergyRateTotal((Double)tou_block[0].getSummation(0));
                        bill.setActiveEnergyImportRateTotal((Double)tou_block[0].getSummation(0));
                        bill.setReactiveEnergyRateTotal((Double)tou_block[0].getSummation(1));
                    }
                    if(tou_block[0].getCurrDemand()!=null && tou_block[0].getCurrDemand().size()>0) {
                        bill.setActivePowerMaxDemandRateTotal((Double)tou_block[0].getCurrDemand(0));
                        bill.setActivePwrDmdMaxImportRateTotal((Double)tou_block[0].getCurrDemand(0));
                        bill.setReactivePowerMaxDemandRateTotal((Double)tou_block[0].getCurrDemand(1));
                    }
                    if(tou_block[0].getEventTime()!=null && tou_block[0].getCurrDemand().size()>0) {
                        bill.setActivePowerDemandMaxTimeRateTotal((String)tou_block[0].getEventTime(0));
                        bill.setActivePwrDmdMaxTimeImportRateTotal((String)tou_block[0].getEventTime(0));
                        bill.setReactivePowerDemandMaxTimeRateTotal((String)tou_block[0].getEventTime(1));
                    }
                    if(tou_block[0].getCumDemand()!=null && tou_block[0].getCumDemand().size()>0) {
                        bill.setCumulativeDemandRateTotal((Double)tou_block[0].getCumDemand(0));
                        bill.setCumulativeReactivePowerDemandRateTotal((Double)tou_block[0].getCumDemand(1));
                    }
                }
                if(tou_block.length >= 2){
                    bill.setActiveEnergyRate1((Double)tou_block[1].getSummation(0));
                    bill.setActiveEnergyImportRate1((Double)tou_block[1].getSummation(0));
                    bill.setActivePowerMaxDemandRate1((Double)tou_block[1].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate1((String)tou_block[1].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate1((Double)tou_block[1].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate1((String)tou_block[1].getEventTime(0));
                    bill.setCumulativeDemandRate1((Double)tou_block[1].getCumDemand(0));
                    bill.setReactiveEnergyRate1((Double)tou_block[1].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate1((String)tou_block[1].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate1((Double)tou_block[1].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate1((Double)tou_block[1].getCumDemand(1));
                }

                if(tou_block.length >= 3){
                    bill.setActiveEnergyRate2((Double)tou_block[2].getSummation(0));
                    bill.setActiveEnergyImportRate2((Double)tou_block[2].getSummation(0));
                    bill.setActivePowerMaxDemandRate2((Double)tou_block[2].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate2((String)tou_block[2].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate2((Double)tou_block[2].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate2((String)tou_block[2].getEventTime(0));
                    bill.setCumulativeDemandRate2((Double)tou_block[2].getCumDemand(0));
                    bill.setReactiveEnergyRate2((Double)tou_block[2].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate2((String)tou_block[2].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate2((Double)tou_block[2].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate2((Double)tou_block[2].getCumDemand(1));
                }

                if(tou_block.length >= 4){
                    bill.setActiveEnergyRate3((Double)tou_block[3].getSummation(0));
                    bill.setActiveEnergyImportRate3((Double)tou_block[3].getSummation(0));
                    bill.setActivePowerMaxDemandRate3((Double)tou_block[3].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate3((String)tou_block[3].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate3((Double)tou_block[3].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate3((String)tou_block[3].getEventTime(0));
                    bill.setCumulativeDemandRate3((Double)tou_block[3].getCumDemand(0));
                    bill.setReactiveEnergyRate3((Double)tou_block[3].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate3((String)tou_block[3].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate3((Double)tou_block[3].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate3((Double)tou_block[3].getCumDemand(1));
                }

                if(tou_block.length >= 5){
                    bill.setActiveEnergyRate4((Double)tou_block[4].getSummation(0));
                    bill.setActiveEnergyImportRate4((Double)tou_block[4].getSummation(0));
                    bill.setActivePowerMaxDemandRate4((Double)tou_block[4].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate4((String)tou_block[4].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate4((Double)tou_block[4].getCurrDemand(0));
                    bill.setActivePwrDmdMaxTimeImportRate4((String)tou_block[4].getEventTime(0));
                    bill.setCumulativeDemandRate4((Double)tou_block[4].getCumDemand(0));
                    bill.setReactiveEnergyRate4((Double)tou_block[4].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate4((String)tou_block[4].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate4((Double)tou_block[4].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate4((Double)tou_block[4].getCumDemand(1));
                }

                billingMonthEMDao.add(bill);
            }
        }catch(Exception e){
         log.error(e,e);
        }


    }

    protected void saveCurrentBilling(TOU_BLOCK[] tou_block, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    {
        log.debug("save RealTimeTOUData["+meter.getMdsId());

        try{
            if(tou_block == null || tou_block.length ==0 || tou_block[0] == null){
                log.debug("MDevId[" + mdevId + "] save RealTime TOU data empty!");
                return;
            }
            String billtime = tou_block[0].getResetTime();
            if(billtime != null && billtime.length() == 14){
                log.debug("MDevId[" + mdevId + "] billtime ["+billtime+"]");
                
//                RealTimeBillingEM bill = new RealTimeBillingEM();

                // 기존에 Billing정보가 존재하는지 검색한다. ADD START 20120404 by eunmiae
                // 스케줄러에서 변경된 빌링 정보가 본 기능 실행후 NULL로 변경되는 오류 수정
                RealTimeBillingEM bill = new RealTimeBillingEM();

                LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
                condition.add(new Condition("id.hhmmss", new Object[]{billtime.substring(8,14)}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmdd", new Object[]{billtime.substring(0,8)}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));

                List<RealTimeBillingEM> billsByPk = realTimeBillingEMDao.findByConditions(condition);

                // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 설정한다.
                if(billsByPk != null && billsByPk.size() != 0) {
                    bill = billsByPk.get(0);
                    return;
                } else {
                    bill = new RealTimeBillingEM();
                }
                // 기존에 Billing정보가 존재하는지 검색한다. ADD END 20120404 by eunmiae

                if(meter.getContract() != null){
                    bill.setContract(meter.getContract());
                }

                //bill.setBill(bill);//TODO TARIFF SET
                bill.setMDevType(mdevType.name());
                bill.setMDevId(mdevId);
                
                switch (mdevType) {
                case Meter :
                    bill.setMeter(meter);
                   if(meter.getSupplier() != null){
                       bill.setSupplier(meter.getSupplier());
                   }
                   if(meter.getLocation() != null){
                       bill.setLocation(meter.getLocation());
                   }
                    break;
                case Modem :
                    Modem modem = modemDao.get(mdevId);
                    bill.setModem(modem);
                    if(modem!=null && modem.getSupplier() != null){
                        bill.setSupplier(modem.getSupplier());
                    }
                    if(modem.getLocation() != null){
                        bill.setLocation(modem.getLocation());
                    }
                    break;
                case EndDevice :
                    // pw.setEnvdevice(enddevice);
                }

                bill.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

                if(tou_block.length >= 1){
                    if(tou_block[0].getResetTime()!=null) {
                        bill.setYyyymmdd(tou_block[0].getResetTime().substring(0,8));
                        bill.setHhmmss(tou_block[0].getResetTime().substring(8,14));
                    }
                    if(tou_block[0].getSummations()!=null && tou_block[0].getSummations().size()>0) {
                        bill.setActiveEnergyRateTotal((Double)tou_block[0].getSummation(0));
                        bill.setActiveEnergyImportRateTotal((Double)tou_block[0].getSummation(0));
                        bill.setReactiveEnergyRateTotal((Double)tou_block[0].getSummation(1));
                    }
                    if(tou_block[0].getCurrDemand()!=null && tou_block[0].getCurrDemand().size()>0) {
                        bill.setActivePowerMaxDemandRateTotal((Double)tou_block[0].getCurrDemand(0));
                        bill.setActivePwrDmdMaxImportRateTotal((Double)tou_block[0].getCurrDemand(0));
                        bill.setReactivePowerMaxDemandRateTotal((Double)tou_block[0].getCurrDemand(1));
                    }
                    if(tou_block[0].getEventTime()!=null && tou_block[0].getEventTime().size()>0) {
                        bill.setActivePowerDemandMaxTimeRateTotal((String)tou_block[0].getEventTime(0));
                        bill.setActivePwrDmdMaxTimeImportRateTotal((String)tou_block[0].getEventTime(0));
                        bill.setReactivePowerDemandMaxTimeRateTotal((String)tou_block[0].getEventTime(1));
                    }
                    if(tou_block[0].getCumDemand()!=null && tou_block[0].getCumDemand().size()>0) {
                        bill.setCumulativeDemandRateTotal((Double)tou_block[0].getCumDemand(0));
                        bill.setCumulativeReactivePowerDemandRateTotal((Double)tou_block[0].getCumDemand(1));
                    }
                }
                if(tou_block.length >= 2){
                    bill.setActiveEnergyRate1((Double)tou_block[1].getSummation(0));
                    bill.setActiveEnergyImportRate1((Double)tou_block[1].getSummation(0));
                    bill.setActivePowerMaxDemandRate1((Double)tou_block[1].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate1((String)tou_block[1].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate1((Double)tou_block[1].getSummation(0));
                    bill.setActivePwrDmdMaxTimeImportRate1((String)tou_block[1].getEventTime(0));
                    bill.setCumulativeDemandRate1((Double)tou_block[1].getCumDemand(0));
                    bill.setReactiveEnergyRate1((Double)tou_block[1].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate1((String)tou_block[1].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate1((Double)tou_block[1].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate1((Double)tou_block[1].getCumDemand(1));
                }

                if(tou_block.length >= 3){
                    bill.setActiveEnergyRate2((Double)tou_block[2].getSummation(0));
                    bill.setActiveEnergyImportRate2((Double)tou_block[2].getSummation(0));
                    bill.setActivePowerMaxDemandRate2((Double)tou_block[2].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate2((String)tou_block[2].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate2((Double)tou_block[2].getSummation(0));
                    bill.setActivePwrDmdMaxTimeImportRate2((String)tou_block[2].getEventTime(0));
                    bill.setCumulativeDemandRate2((Double)tou_block[2].getCumDemand(0));
                    bill.setReactiveEnergyRate2((Double)tou_block[2].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate2((String)tou_block[2].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate2((Double)tou_block[2].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate2((Double)tou_block[2].getCumDemand(1));
                }

                if(tou_block.length >= 4){
                    bill.setActiveEnergyRate3((Double)tou_block[3].getSummation(0));
                    bill.setActiveEnergyImportRate3((Double)tou_block[3].getSummation(0));
                    bill.setActivePowerMaxDemandRate3((Double)tou_block[3].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate3((String)tou_block[3].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate3((Double)tou_block[3].getSummation(0));
                    bill.setActivePwrDmdMaxTimeImportRate3((String)tou_block[3].getEventTime(0));
                    bill.setCumulativeDemandRate3((Double)tou_block[3].getCumDemand(0));
                    bill.setReactiveEnergyRate3((Double)tou_block[3].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate3((String)tou_block[3].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate3((Double)tou_block[3].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate3((Double)tou_block[3].getCumDemand(1));
                }

                if(tou_block.length >= 5){
                    bill.setActiveEnergyRate4((Double)tou_block[4].getSummation(0));
                    bill.setActiveEnergyImportRate4((Double)tou_block[4].getSummation(0));
                    bill.setActivePowerMaxDemandRate4((Double)tou_block[4].getCurrDemand(0));
                    bill.setActivePowerDemandMaxTimeRate4((String)tou_block[4].getEventTime(0));
                    bill.setActivePwrDmdMaxImportRate4((Double)tou_block[4].getSummation(0));
                    bill.setActivePwrDmdMaxTimeImportRate4((String)tou_block[4].getEventTime(0));
                    bill.setCumulativeDemandRate4((Double)tou_block[4].getCumDemand(0));
                    bill.setReactiveEnergyRate4((Double)tou_block[4].getSummation(1));
                    bill.setReactivePowerDemandMaxTimeRate4((String)tou_block[4].getEventTime(1));
                    bill.setReactivePowerMaxDemandRate4((Double)tou_block[4].getCurrDemand(1));
                    bill.setCumulativeReactivePowerDemandRate4((Double)tou_block[4].getCumDemand(1));
                }

                realTimeBillingEMDao.add(bill);
            }
        }catch(Exception e){
            log.error(e,e);
        }


    }

    protected void saveCurrentBilling(BillingData billData, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    {
         log.debug("save CurrentBillingData["+meter.getMdsId());
         
         try{  

             String yyyymmddOfBillData = "";
             String hhmmssOfBillData = "";
     
             if(billData == null){
                 log.debug("MDevId[" + mdevId + "] save MonthlyBillingData BillingData data empty!");
                 return;
             }

//              RealTimeBillingEM bill = new RealTimeBillingEM();

                // 기존에 Billing정보가 존재하는지 검색한다. ADD START 20120404 by eunmiae
                // 스케줄러에서 변경된 빌링 정보가 본 기능 실행후 NULL로 변경되는 오류 수정
                if(billData.getBillingTimestamp() != null){
                    yyyymmddOfBillData = billData.getBillingTimestamp().substring(0, 8);
                    hhmmssOfBillData = billData.getBillingTimestamp().substring(8, 14);  
                }else{
                    yyyymmddOfBillData = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").substring(0,8);
                    hhmmssOfBillData = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").substring(8,14);                
                }
             
                RealTimeBillingEM bill = new RealTimeBillingEM();
                
                // 데이터를 모두 복사(null포함)
                try {
                    BeanUtils.copyProperties(bill, billData);
                } catch (Exception e) {
                    log.warn(e);
                }
                
                /*
                LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
                condition.add(new Condition("id.yyyymmdd", new Object[]{yyyymmddOfBillData}, null, Restriction.EQ));
                condition.add(new Condition("id.hhmmss", new Object[]{hhmmssOfBillData}, null, Restriction.EQ));


                List<RealTimeBillingEM> billsByPk = realTimeBillingEMDao.findByConditions(condition);

                // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 설정한다.
                if(billsByPk != null && billsByPk.size() != 0) {
                    bill = billsByPk.get(0);
                } else {
                    bill = new RealTimeBillingEM();
                }
                // 기존에 Billing정보가 존재하는지 검색한다. ADD END 20120404 by eunmiae
                */
                 if(meter.getContract() != null){
                    bill.setContract(meter.getContract());
                 }

                 bill.setMDevType(mdevType.name());
                 bill.setMDevId(mdevId);
                 
                 switch (mdevType) {
                 case Meter :
                    bill.setMeter(meter);
                    if(meter.getSupplier() != null){
                        bill.setSupplier(meter.getSupplier());
                    }
                    if(meter.getLocation() != null){
                        bill.setLocation(meter.getLocation());
                    }
                     break;
                 case Modem :
                     Modem modem = modemDao.get(mdevId);
                     bill.setModem(modem);
                     if(modem!=null && modem.getSupplier() != null){
                        bill.setSupplier(modem.getSupplier());
                     }
                     if(modem.getLocation() != null){
                         bill.setLocation(modem.getLocation());
                     }
                     break;
                 case EndDevice :
                     // pw.setEnvdevice(enddevice);
                 }

                 bill.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                 bill.setYyyymmdd(yyyymmddOfBillData);
                 bill.setHhmmss(hhmmssOfBillData);

                 log.debug("MDevId[" + mdevId + "] bill.getMDevType() "+bill.getMDevType());//4개는 null이여서는 안되는 값이다.
                 log.debug("MDevId[" + mdevId + "] bill.getMDevId()   "+bill.getMDevId());
                 log.debug("MDevId[" + mdevId + "] bill.getYyyymmdd() "+bill.getYyyymmdd());
                 log.debug("MDevId[" + mdevId + "] bill.getHhmmss() "+bill.getHhmmss());
                 log.debug("MDevId[" + mdevId + "] bill.getActiveEnergyImportRate1() " + bill.getActiveEnergyImportRate1());
                 log.debug("MDevId[" + mdevId + "] billData.getActiveEnergyImportRate1() " + billData.getActiveEnergyImportRate1());

                 realTimeBillingEMDao.saveOrUpdate(bill);
//             }
         }catch(Exception e){
             log.warn(e,e);
         }
    }
    
    protected void saveDailyBilling(BillingData billData, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    {
        log.debug("save DailyBillingData["+meter.getMdsId());
         
        try {
            String hhmmssOfBillData = "";
            if(billData == null){
                log.debug("MDevId[" + mdevId + "] save DailyBillingData BillingData data empty!");
                return;
            }

            // 기존에 Billing정보가 존재하는지 검색한다. ADD START 20120404 by eunmiae
            // 스케줄러에서 변경된 빌링 정보가 본 기능 실행후 NULL로 변경되는 오류 수정
             
            if(billData.getBillingTimestamp().length() == 8){
                hhmmssOfBillData = "000000";
            }else if(billData.getBillingTimestamp().length() == 10){
                hhmmssOfBillData = billData.getBillingTimestamp().substring(8,10)+"0000";
            }else if(billData.getBillingTimestamp().length() == 12){
                hhmmssOfBillData = billData.getBillingTimestamp().substring(8,12)+"00";
            }else if(billData.getBillingTimestamp().length() == 14){
                hhmmssOfBillData = billData.getBillingTimestamp().substring(8,14);
            }

            BillingDayEM bill = new BillingDayEM();

            LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
            condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
            condition.add(new Condition("id.hhmmss", new Object[]{hhmmssOfBillData}, null, Restriction.EQ));
            condition.add(new Condition("id.yyyymmdd", new Object[]{billData.getBillingTimestamp().substring(0,8)}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));
            
            List<Object> ret = billingDayEMDao.findTotalCountByConditions(condition);
            
            Long count = (Long) ret.get(0);

            // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 설정한다.
            if(count != null && count.longValue() > 0) {
                return;
            } 
            // 기존에 Billing정보가 존재하는지 검색한다. ADD END 20120404 by eunmiae
             
            // 데이터를 모두 복사(null포함)
            try {
                BeanUtils.copyProperties(bill, billData);
            } catch (Exception e) {
                log.error(e);
            }
             
            if(meter.getContract() != null){
                bill.setContract(meter.getContract());
            }

            bill.setMDevType(mdevType.name());
            bill.setMDevId(mdevId);
             
            switch (mdevType) {
            case Meter :
                bill.setMeter(meter);
                if(meter.getSupplier() != null){
                    bill.setSupplier(meter.getSupplier());
                }
                if(meter.getLocation() != null){
                    bill.setLocation(meter.getLocation());
                }
                break;
            case Modem :
                Modem modem = modemDao.get(mdevId);
                bill.setModem(modem);
                if(modem!=null && modem.getSupplier() != null){
                   bill.setSupplier(modem.getSupplier());
                }
                if(modem.getLocation() != null){
                    bill.setLocation(modem.getLocation());
                }
                break;
            case EndDevice :
                 // pw.setEnvdevice(enddevice);
            }

            bill.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            bill.setYyyymmdd(billData.getBillingTimestamp().substring(0,8));
            bill.setHhmmss(hhmmssOfBillData);

            log.debug("bill.getMDevType() "+bill.getMDevType());
            log.debug("bill.getMDevId()   "+bill.getMDevId());
            log.debug("bill.getYyyymmdd() "+bill.getYyyymmdd());

            billingDayEMDao.add(bill);
        }
        catch(Exception e){
            log.warn(e,e);
        }
    }

    protected void saveMonthlyBilling(BillingData billData, Meter meter,
            DeviceType deviceType, String deviceId, DeviceType mdevType, String mdevId)
    {
        log.debug("save MonthlyBillingData["+meter.getMdsId());

        try{  
             
            String hhmmssOfBillData = "";
            if(billData == null){
                log.debug("MDevId[" + mdevId + "] save MonthlyBillingData BillingData data empty!");
                return;
            }

            // 기존에 Billing정보가 존재하는지 검색한다. ADD START 20120404 by eunmiae
            // 스케줄러에서 변경된 빌링 정보가 본 기능 실행후 NULL로 변경되는 오류 수정
            if(billData.getBillingTimestamp().length() == 8){
                hhmmssOfBillData= "000000";
            }else if(billData.getBillingTimestamp().length() == 10){
                hhmmssOfBillData= billData.getBillingTimestamp().substring(8,10)+"0000";
            }else if(billData.getBillingTimestamp().length() == 12){
                hhmmssOfBillData= billData.getBillingTimestamp().substring(8,12)+"00";
            }else if(billData.getBillingTimestamp().length() == 14){
                hhmmssOfBillData= billData.getBillingTimestamp().substring(8,14);
            }
             
            BillingMonthEM bill = new BillingMonthEM();

            LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
            condition.add(new Condition("id.mdevId", new Object[]{mdevId}, null, Restriction.EQ));
            condition.add(new Condition("id.hhmmss", new Object[]{hhmmssOfBillData}, null, Restriction.EQ));
            condition.add(new Condition("id.yyyymmdd", new Object[]{billData.getBillingTimestamp().substring(0,8)}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevType", new Object[]{mdevType}, null, Restriction.EQ));

            List<Object> ret = billingMonthEMDao.findTotalCountByConditions(condition);
            
            Long count = (Long) ret.get(0);

            // 기존에 데이터가 있을 경우는 기존 정보에 검침된 빌링 정보를 설정한다.
            if(count != null && count.longValue() > 0) {
                return;
            }

            // 데이터를 모두 복사(null포함)
            try {
                BeanUtils.copyProperties(bill, billData);
            } catch (Exception e) {
                log.warn(e);
            }
             
            // 기존에 Billing정보가 존재하는지 검색한다. ADD END 20120404 by eunmiae
            if(meter.getContract() != null){
                bill.setContract(meter.getContract());
            }

            bill.setMDevType(mdevType.name());
            bill.setMDevId(mdevId);
             
            switch (mdevType) {
            case Meter :
                bill.setMeter(meter);
                if(meter.getSupplier() != null){
                    bill.setSupplier(meter.getSupplier());
                }
                if(meter.getLocation() != null){
                    bill.setLocation(meter.getLocation());
                }
                break;
            case Modem :
                Modem modem = modemDao.get(mdevId);
                bill.setModem(modem);
                if(modem!=null && modem.getSupplier() != null){
                    bill.setSupplier(modem.getSupplier());
                }
                if(modem.getLocation() != null){
                    bill.setLocation(modem.getLocation());
                }
                break;
            case EndDevice :
                // pw.setEnvdevice(enddevice);
            }

            bill.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            bill.setYyyymmdd(billData.getBillingTimestamp().substring(0,8));
            bill.setHhmmss(hhmmssOfBillData);
             
             
            if(billData.getBillingTimestamp().length() == 8){
                bill.setHhmmss("000000");
            }else if(billData.getBillingTimestamp().length() == 10){
                bill.setHhmmss(billData.getBillingTimestamp().substring(8,10)+"0000");
            }else if(billData.getBillingTimestamp().length() == 12){
                bill.setHhmmss(billData.getBillingTimestamp().substring(8,12)+"00");
            }else if(billData.getBillingTimestamp().length() == 14){
                bill.setHhmmss(billData.getBillingTimestamp().substring(8,14));
            }

            log.debug("bill.getMDevType() "+bill.getMDevType());
            log.debug("bill.getMDevId()   "+bill.getMDevId());
            log.debug("bill.getYyyymmdd() "+bill.getYyyymmdd());
             
            billingMonthEMDao.add(bill);

        }
        catch(Exception e){
            log.warn(e,e);
        }
    }

    protected void saveMeterTimeSyncLog(Meter meter, String before, String after,
            int result) throws Exception {

        log.debug("save MeterTimeSyncLog["+meter.getMdsId()+"]");

        MeterTimeSyncData mtdata = new MeterTimeSyncData();
        mtdata.setAtime(after);
        mtdata.setBtime(before);
        mtdata.setContent("");
        mtdata.setCtime(after);
        mtdata.setUserID("system");
        long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(after).getTime() - 
                DateTimeUtil.getDateFromYYYYMMDDHHMMSS(before).getTime();
        mtdata.setTimediff(diff);
        mtdata.setResult(result);
        
        saveMeterTimeSyncLog(meter, mtdata);
    }
    
    protected void saveMeterTimeSyncLog(Meter meter, MeterTimeSyncData data) {

        log.debug("save MeterTimeSyncLog["+meter.getMdsId()+"]");
        try {
            if(data.getCtime() == null || "".equals(data.getCtime())){ //주키값이 없으면 insert 할수 없다.
                throw new Exception("Meter time is wrong. check meter time!!");
            }
        
            MeterTimeSyncLog mt = new MeterTimeSyncLog();
            mt.setAfterDate(data.getAtime());
            mt.setBeforeDate(data.getBtime());
            mt.setDescr(data.getContent());
            mt.setLocation(meter.getLocation());
            mt.setMeter(meter);
            mt.setMeterDate(data.getCtime());
            mt.setOperator(data.getUserID());
            mt.setOperatorType(OperatorType.SYSTEM.name());
    
            switch(data.getResult()){
                case 0 : mt.setResult(ResultStatus.SUCCESS.name());break;
                case 1 : mt.setResult(ResultStatus.FAIL.name());break;
                case 2 : mt.setResult(ResultStatus.INVALID_PARAMETER.name());break;
                case 3 : mt.setResult(ResultStatus.COMMUNICATION_FAIL.name());break;
            }
    
            mt.setSupplier(meter.getSupplier());
            mt.setTimeDiff(data.getTimediff());
            mt.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            mt.setId(TimeUtil.getCurrentLongTime());
            meterTimeSyncLogDao.add(mt);
        }
        catch (Exception e) {
            log.warn(e, e);
        }
    }
    
    /**
     * 집중기나 모뎀의 검침시간과 미터시간이 거의 일치해야 한다.
     * 만약 오차가 많이 발생하면 시간 동기화를 할 수 있도록 한다.
     * @param meter
     * @param meteringTime
     * @param meterTime
     * @throws Exception
     */
    protected void saveMeterTimeSyncLog(Meter meter, String meteringTime, String meterTime) {

        log.debug("MDevId[" + meter.getMdsId() + "] save MeterTimeSyncLog[MeteringTime=" + meteringTime + ", MeterTime=" + meterTime +"]");
        try {
            if (meteringTime != null && meterTime != null) {
                MeterTimeSyncLog mt = new MeterTimeSyncLog();
                mt.setLocation(meter.getLocation());
                mt.setMeter(meter);
                mt.setMeterDate(meterTime);
                mt.setAfterDate("");
                mt.setBeforeDate("");
                mt.setOperator("system");
                mt.setOperatorType(OperatorType.SYSTEM.name());
                mt.setSupplier(meter.getSupplier());
                long diff = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meteringTime).getTime() - 
                        DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterTime).getTime();
                mt.setTimeDiff(diff / 1000);
                mt.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                mt.setResult(ResultStatus.SUCCESS.name());
                mt.setId(TimeUtil.getCurrentLongTime());
                meterTimeSyncLogDao.add(mt);
            }
        }
        catch (Exception e) {
            log.warn(e, e);
        }
    }
    
    public static Double dformat(Double n){
        if(n==null)
            return 0d;
        return new Double(dformat.format(n));
    }
    
    public static int getDayType(String yyyymmdd) throws ParseException {
        // 공휴일 프로퍼티가 없으면 fmp.properties에 설정이 없는 것이므로 영업일로 처리한다.
        // 태국 TCIS 기능을 위해서만 처리하므로 다른 사이트는 적용하지 않아도 된다. 
        String holidaylist = FMPProperty.getProperty("sic.day.holiday.list");
        if (holidaylist == null)
            return 0;
        
        StringTokenizer st = new StringTokenizer(holidaylist, ",");
        while (st.hasMoreTokens()) {
            if (yyyymmdd.substring(4).equals(st.nextToken()))
                return Integer.parseInt(FMPProperty.getProperty("sic.day.holiday"));
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateTimeUtil.getDateFromYYYYMMDD(yyyymmdd));
        return Integer.parseInt(FMPProperty.getProperty("sic.day." + cal.get(Calendar.DAY_OF_WEEK)));
    }
    
    public String relayValveOn(String mcuId, String meterId) {
        return "Not supported";
    }

    public String relayValveOff(String mcuId, String meterId) {
        return "Not supported";
    }

    public String relayValveStatus(String mcuId, String meterId) {
        return "Not supported";
    }

    public String syncTime(String mcuId, String meterId) {
        return "Not supported";
    }

    public String relayValveActivate(String mcuId, String meterId) {
        return "Not supported";
    }

    public String relayValveDeactivate(String mcuId, String meterId) {
        return "Not supported";
    }
    
    public MeterData onDemandMeterBypass(String mcuId, String meterId,String modemId, String nOption, String fromDate, String toDate)
            throws Exception
    {
    	return null;
    }
    // -> UPDATE START 2016/09/20 SP-117
    // protected void updateMeterStatusNormal(Meter meter) {
    public void updateMeterStatusNormal(Meter meter) {
    // <- UPDATE END   2016/09/20 SP-117
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            
            meter = meterDao.get(meter.getMdsId());
            meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));
            // meterDao.update(meter);
            
            Contract contract = meter.getContract();
            if(contract != null && (contract.getStatus() == null 
                    || contract.getStatus().getCode().equals(CommonConstants.ContractStatus.PAUSE.getCode()))) {
                Code normalCode = CommonConstants.getContractStatus(CommonConstants.ContractStatus.NORMAL.getCode());
                // codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.NORMAL.getCode());
                contract.setStatus(normalCode);
                // contractDao.update(contract);
                // contractDao.updateStatus(contract.getId(), normalCode);
            }
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
        }
    }
 
    protected void updateMeterModel(Meter meter, String meterModel){
        List<DeviceModel> models = deviceModelDao.getDeviceModelByName(meter.getSupplierId(), meterModel);
        if(models != null && models.size() > 0 && models.get(0) != null){
            DeviceModel org = meter.getModel();
            if(org != null){
                if(!org.getId().equals(models.get(0).getId())){
                    meter.setModel(models.get(0));
                }
            }else{
                meter.setModel(models.get(0));
            }           
        }
    }
    
    // -> UPDATE START 2016/09/20 SP-117
    // protected void updateMeterStatusCutOff(Meter meter) {
    public void updateMeterStatusCutOff(Meter meter) {
    // <- UPDATE END   2016/09/20 SP-117
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            meter = meterDao.get(meter.getMdsId());
            meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()));
            // meterDao.update(meter);
            
            Contract contract = meter.getContract();
            if(contract != null && (contract.getStatus() == null
                    || contract.getStatus().getCode().equals(CommonConstants.ContractStatus.NORMAL.getCode()))) {
                Code pauseCode = CommonConstants.getContractStatus(CommonConstants.ContractStatus.PAUSE.getCode());
                // codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.PAUSE.getCode());
                contract.setStatus(pauseCode);
            }
            
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
        }
    }
 
    public String MapToJSON(String[] array) {
        StringBuffer rStr = new StringBuffer();
        rStr.append("[");
        for (int i = 0; array != null && i < array.length; i++) {
            rStr.append("{\"name\":\"");
            rStr.append("key[" + i + "]");
            rStr.append("\",\"value\":\"");
            rStr.append(array[i]);
            rStr.append("\"}");
            if (i < array.length - 1) {
                rStr.append(",");
            }
        }
        rStr.append("]");
        return rStr.toString();
    }

    @SuppressWarnings("unchecked")
    public String MapToJSON(Map map) {
        StringBuffer rStr = new StringBuffer();
        Iterator<String> keys = map.keySet().iterator();
        String keyVal = null;
        rStr.append("[");
        while (keys.hasNext()) {
            keyVal = (String) keys.next();
            rStr.append("{\"name\":\"");
            rStr.append(keyVal);
            rStr.append("\",\"value\":\"");
            rStr.append(map.get(keyVal));
            rStr.append("\"}");
            if (keys.hasNext()) {
                rStr.append(",");
            }
        }
        rStr.append("]");
        return rStr.toString();
    }
    
    public JsonElement StringToJsonArray(String str) {
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(str);
    }
    
    protected void saveSNRLog(String deviceId, String yyyymmdd, String hhmmss, Modem modem, Double val) 
    {
        try{
            SNRLog snrLog = new SNRLog();            
            snrLog.setDcuid(deviceId);
            snrLog.setDeviceId(modem.getDeviceSerial());
            snrLog.setDeviceType(modem.getModemType().name());
            snrLog.setYyyymmdd(yyyymmdd);
            snrLog.setHhmmss(hhmmss);
            snrLog.setSnr(val);
            snrLogDao.add(snrLog);
        }catch(Exception e){
            log.warn(e,e);
        }
    }
    
    protected void saveEnvData(Modem modem, List<EnvData> lpDatas) throws Exception
    {
        for (EnvData envData: lpDatas) {

            ZEUPLS envSensor = (ZEUPLS) modemDao.get(envData.getSensorId());
                
            try{
                if( envSensor == null){
                    envSensor = new ZEUPLS();
                    envSensor.setDeviceSerial(envData.getSensorId());
                    envSensor.setModemType(ModemType.ZEUPLS.name());
                    envSensor.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                    envSensor.setCommState(1);
                    envSensor.setLastLinkTime(modem.getInstallDate());
                    envSensor.setModem(modem);
                    List<DeviceModel> models = deviceModelDao.getDeviceModelByName(modem.getSupplierId(), "NZM-C001SR");
                    if (models.size() == 1)
                        envSensor.setModel(models.get(0));
                    modemDao.add_requires_new(envSensor);
                    //modemDao.flushAndClear();
                }else{
                    envSensor.setCommState(1);
                    envSensor.setLastLinkTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                    //envSensor.setModem(modem);
                    modemDao.update_requires_new(envSensor);
                    //modemDao.flushAndClear();
                }

            }catch(Exception e){
                log.warn(e,e);
            }
            envSensor = null;
            //com.aimir.model.device.EnvSensor dbenvSensor = (com.aimir.model.device.EnvSensor) modemDao.get(envData.getSensorId());
            
            for (int i = 0 ; i < envData.getChannelCnt(); i++) {
                LpTM lp = new LpTM();   
                String str_mm = "value_"+envData.getDatetime().substring(10,12);              
                BeanUtils.copyProperty(lp, str_mm, dformat(envData.getCh()[i]));
                lp.setChannel(i+1);
                lp.setDeviceId(envData.getSensorId());
                lp.setDeviceType(DeviceType.Modem.getCode());
                lp.setDst(0);
                lp.setYyyymmdd(envData.getDatetime().substring(0,8));
                lp.setYyyymmddhh(envData.getDatetime().substring(0,10));
                lp.setHour(envData.getDatetime().substring(8,10));
                lp.setMDevId(envData.getSensorId());
                lp.setMDevType(DeviceType.Modem.name());
                lp.setMeteringType(0);
                lp.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                //lp.setModem(dbenvSensor);
                
                log.debug("SENSORID["+envData.getSensorId()+"] YYYYMMDDHHMM["+envData.getDatetime().substring(0,12)+"] CHANNEL["+(i+1)+"] VALUE["+envData.getCh()[i]+"]");
                
                LinkedHashSet<Condition> condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.yyyymmddhhmiss", new Object[]{envData.getDatetime().substring(0,10) + "%"}, null, Restriction.LIKE));
                condition.add(new Condition("id.channel", new Object[]{i+1}, null, Restriction.EQ));
                condition.add(new Condition("id.dst", new Object[]{0}, null, Restriction.EQ));
                condition.add(new Condition("id.mdevId", new Object[]{envData.getSensorId()}, null, Restriction.EQ));
                // condition.add(new Condition("id.mdevType", new Object[]{DeviceType.Modem}, null, Restriction.EQ));                
                
                List<LpTM> dbList = lpTMDao.findByConditions(condition);
                
                try{

                    if(dbList != null && dbList.size() > 0){
                        LpTM updateLp = dbList.get(0);
                        updateLp.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                        BeanUtils.copyProperty(updateLp, str_mm, dformat(envData.getCh()[i]));
                        lpTMDao.update(updateLp);
                        lpTMDao.flushAndClear();
                    } else{
                        lpTMDao.add(lp);
                        lpTMDao.flushAndClear();
                    }
                }catch(Exception e){
                    log.warn(e,e);
                }
            }            
        }
    }

    private Map<DayPk, MeteringDay> listDay(Set<Condition> condition, MeterType meterType, int chcnt) throws Exception {
    	Map<DayPk, MeteringDay> result = new HashMap<DayPk, MeteringDay>();
    	List list = null;    	 
    	
    	switch (meterType) {
        case EnergyMeter :
            list = dayEMDao.findByConditions(condition);
            break;
        case WaterMeter :
            list = dayWMDao.findByConditions(condition);
            break;
        case GasMeter :
            list = dayGMDao.findByConditions(condition);
            break;
        case HeatMeter :
            list = dayHMDao.findByConditions(condition);
            break;
        case SolarPowerMeter :
            list = daySPMDao.findByConditions(condition);
            break;
        case Inverter :
            list = dayEMDao.findByConditions(condition);
            break;            
        }
    	
    	log.debug("LP Channel Count[" + chcnt + "] // Query Result Count["+list == null ? 0 : list.size()+"]");
    	
    	for(int i=0; i<list.size(); i++) {
    		MeteringDay meteringDay = (MeteringDay)list.get(i);
    		result.put(meteringDay.getId(), meteringDay);
    	}
    	
    	return result;
    }
}
