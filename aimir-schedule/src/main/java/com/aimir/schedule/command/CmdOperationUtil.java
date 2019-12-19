package com.aimir.schedule.command;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.FW_EQUIP;
import com.aimir.constants.CommonConstants.FW_OTA;
import com.aimir.constants.CommonConstants.FW_STATE;
import com.aimir.constants.CommonConstants.MeterProgramKind;
import com.aimir.constants.CommonConstants.MeterVendor;
import com.aimir.constants.CommonConstants.ModemCommandType;
import com.aimir.constants.CommonConstants.ModemIFType;
import com.aimir.constants.CommonConstants.ModemNetworkType;
import com.aimir.constants.CommonConstants.ModemPowerType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.OTAExecuteType;
import com.aimir.constants.CommonConstants.OTATargetType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.dao.device.ConverterDao;
import com.aimir.dao.device.IEIUDao;
import com.aimir.dao.device.MCUCodiDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.NetworkInfoLogDao;
import com.aimir.dao.device.ZBRepeaterDao;
import com.aimir.dao.device.ZEUMBusDao;
import com.aimir.dao.device.ZEUPLSDao;
import com.aimir.dao.device.ZMUDao;
import com.aimir.dao.device.ZRUDao;
import com.aimir.dao.system.MeterProgramDao;
import com.aimir.fep.command.conf.DefaultConf;
import com.aimir.fep.command.conf.KamstrupCIDMeta;
import com.aimir.fep.command.ws.client.CommandWS;
import com.aimir.fep.command.ws.client.Exception_Exception;
import com.aimir.fep.command.ws.client.IntArray;
import com.aimir.fep.command.ws.client.ResponseMap;
import com.aimir.fep.command.ws.client.StringArray;
import com.aimir.fep.mcu.data.McuPropertyResult;
import com.aimir.fep.mcu.data.McuScheduleResult;
import com.aimir.fep.mcu.data.ScheduleData;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.data.Response;
import com.aimir.fep.modem.AmrData;
import com.aimir.fep.modem.BatteryLog;
import com.aimir.fep.modem.EventLog;
import com.aimir.fep.modem.LPData;
import com.aimir.fep.modem.ModemCommandData;
import com.aimir.fep.modem.ModemNetwork;
import com.aimir.fep.modem.ModemNode;
import com.aimir.fep.modem.ModemROM;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.exception.FMPMcuException;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiBindingEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiDeviceEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiMemoryEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.codiNeighborEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.drLevelEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.endDeviceEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.idrEntry;
import com.aimir.fep.protocol.fmp.frame.service.entry.sensorInfoNewEntry;
import com.aimir.fep.protocol.mrp.command.frame.sms.RequestFrame;
import com.aimir.fep.protocol.nip.command.InitiateModuleUpgrade;
import com.aimir.fep.util.ByPassFrameUtil;
import com.aimir.fep.util.ByteArray;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FirmwareUtil;
import com.aimir.fep.util.GroupInfo;
import com.aimir.fep.util.GroupTypeInfo;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.Converter;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.IEIU;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;
import com.aimir.model.device.MCUCodiBinding;
import com.aimir.model.device.MCUCodiDevice;
import com.aimir.model.device.MCUCodiMemory;
import com.aimir.model.device.MCUCodiNeighbor;
import com.aimir.model.device.MCUVar;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.NetworkInfoLog;
import com.aimir.model.device.SubGiga;
import com.aimir.model.device.ZBRepeater;
import com.aimir.model.device.ZEUMBus;
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.device.ZMU;
import com.aimir.model.device.ZRU;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.MeterConfig;
import com.aimir.model.system.MeterProgram;
import com.aimir.schedule.adapter.ScheduleOperationUtil;
import com.aimir.schedule.util.ScheduleProperty;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Mask;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Unit Operation Utility Class
 *
 * @author D.J Park
 * @version $Rev: 1 $, $Date: 2005-12-07 15:59:15 +0900 $,
 */
@Service
@Scope(value="prototype")
public class CmdOperationUtil {
    private Log log = LogFactory.getLog(CmdOperationUtil.class);
    private String regexIPv4 = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";
    private String regexIPv6 = "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$";
    private String regexIPv4andIPv6 = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)|(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$";
	final int MSG_TIMEOUT = 30;
	final int TUNNEL_TIMEOUT = 0;
	
    @Autowired
    private MCUDao mcuDao;

    @Autowired
    private MeterDao meterDao;

    @Autowired
    private ModemDao modemDao;

    @Autowired
    private ZRUDao zruDao;

    @Autowired
    private ZEUPLSDao zeuplsDao;

    @Autowired
    private ZMUDao zmuDao;

    @Autowired
    private ZBRepeaterDao zbRepeaterDao;

    @Autowired
    private ZEUMBusDao zeuMBusDao;

    @Autowired
    private MCUCodiDao codiDao;

    @Autowired
    private MeterProgramDao MeterProgramDao;
    
    @Autowired
	private NetworkInfoLogDao nlogDao_HN;
    
    @Autowired
    private MMIUDao mmiuDao;
    
    @Autowired
    private IEIUDao ieiuDao;
    
    @Autowired
    private ConverterDao converterDao;
    
    @Resource(name="transactionManager")
    private HibernateTransactionManager txManager;
    
    private String viewCmdResult;
    
    private String getProtocolType(String mcuId, String meterId, String modemId) {
        TransactionStatus txStatus = null;
        try {
            txStatus = txManager.getTransaction(
                    new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
            MCU mcu = null;
            if (mcuId != null && !"".equals(mcuId))
                mcu = mcuDao.get(mcuId);
            
            if (mcu != null)
                return mcu.getProtocolType().getName();
            
            Meter meter = null;
            if (meterId != null && !"".equals(meterId)) {
                meter = meterDao.get(meterId);
                if (meter != null && meter.getModem() != null)
                    return meter.getModem().getProtocolType().name();
            }
            
            Modem modem = null;
            if (modemId != null && !"".equals(modem)) {
                modem = modemDao.get(modemId);
                
                if (modem != null)
                    return modem.getProtocolType().name();
            }
        }
        catch (Exception e) {
            log.error(e, e);
        }
        finally {
            if (txStatus != null)
                txManager.commit(txStatus);
        }
        
        return Protocol.LAN.name();
    }
    
    public Map<String, String> cmdRelaySwitchStatus(String mcuId,
            String meterId) throws Exception {
        log.info("cmdRelaySwitchStatus[" + mcuId + "," + meterId + "]");
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null));

            ResponseMap rmap = gw.getRelaySwitchStatus(mcuId, meterId);
            
            Map<String, String> res = new HashMap<String, String>();
            /*MeterData.Map.Entry entry = null;
            for (Object o : rmap.getResponse().getEntry().toArray()) {
                entry = (MeterData.Map.Entry)o;
                res.put((String)entry.getKey(), (String)entry.getValue());
            }*/
            for (ResponseMap.Response.Entry e : rmap.getResponse().getEntry().toArray(new ResponseMap.Response.Entry[0])) {
                res.put((String)e.getKey(), (String)e.getValue());
            }
            return res;
        } catch (Exception e) {
            throw e;
        }
    }

    public Map<String, String> cmdRelaySwitchAndActivate(String mcuId,
            String meterId, String cmdNum) throws Exception {
        log.info("Energy Meter's Relay Switch And Activate On/Off [" + mcuId
                + "," + meterId + "]");
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null));

            ResponseMap rmap = gw.cmdRelaySwitchAndActivate(mcuId, meterId, cmdNum);
            Map<String, String> res = new HashMap<String, String>();
            
            for (ResponseMap.Response.Entry e : rmap.getResponse().getEntry().toArray(new ResponseMap.Response.Entry[0])) {
                res.put((String)e.getKey(), (String)e.getValue());
            }
            return res;
        } catch (Exception e) {
            throw e;
        }
    }

    public String cmdAidonMccb(String mcuId, String meterId, String req)
            throws Exception {
        log.info("cmdAidonMccb[" + mcuId + "," + meterId + "," + req + "]");
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null));

            String ret = gw.cmdAidonMCCB(mcuId, meterId, req);
            return ret;
        } catch (Exception e) {
            throw e;
        }
    }

    public Object[] cmdKamstrupCID(String mcuId, String meterId,
            String[] req) throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("MCUID[" + mcuId + "] METERID[" + meterId + "] REQ[");
        for (int i = 0; req != null && i < req.length; i++) {
            buf.append(req[i] + ",");
        }
        buf.append("]");

        log.info(buf.toString());

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null));
            List<String> params = new ArrayList<String>();
            for (String r : req) {
                params.add(r);
            }
            Meter meter = meterDao.get(meterId);
            return KamstrupCIDMeta.getResult(gw.cmdKamstrupCID1(mcuId, meterId, params),
                    meter.getModel().getName());

        } catch (Exception e) {
            throw e;
        }
    }

    public Object[] cmdKamstrupCID(String mcuId, String meterId,
            String kind, String[] req) throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("MCUID[" + mcuId + "] METERID[" + meterId + "] KIND[" + kind
                + "] REQ[");
        for (int i = 0; req != null && i < req.length; i++) {
            buf.append(req[i] + ",");
        }
        buf.append("]");

        log.info(buf.toString());
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null));
            List<String> params = new ArrayList<String>();
            for (String r : req) {
                params.add(r);
            }
            Meter meter = meterDao.get(meterId);
            return KamstrupCIDMeta.getResult(gw.cmdKamstrupCID(mcuId, meterId, kind, params),
                    meter.getModel().getName());
        } catch (Exception e) {
            throw e;
        } 
    }

    
    public <K, V> void doMCUScanning(CommandWS gw, MCU mcu)
            throws Exception {
        log.info("doMCUScanning MCU[" + mcu.getSysID() + "]");

        String mcuId = mcu.getSysID();

        DefaultConf defaultConf = DefaultConf.getInstance();
        Hashtable props = defaultConf.getDefaultProperties("MCU");

        log.debug("props size=" + props.size());
        MIBUtil mibUtil = MIBUtil.getInstance();
        List<String> property = new ArrayList<String>();
        Iterator it = props.keySet().iterator();
        for (int i = 0; it.hasNext(); i++) {
            try {

                String key = (String) it.next();
                property.add((mibUtil.getOid(key)).getValue());
                log.debug("props[" + i + "] :" + key + " ,oid= " + property.get(i));
            } catch (Exception e) {
            }
        }
        ResponseMap res = null;
        try {
            if (gw == null) {
                gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            }

            res = gw.cmdStdGet1(mcuId, property);

        } catch (Exception e) {
            throw e;
        }

        String val;
        List<ResponseMap.Response.Entry> entries = res.getResponse().getEntry();
        
        for (ResponseMap.Response.Entry e : (ResponseMap.Response.Entry[])entries.toArray()) {
            if (((String)e.getKey()).contains("geographicCoordsX")) {
                val = (String) e.getValue();
                int idx = val.indexOf(",");
                if (idx > 0) {
                    mcu.setGpioX(Double.valueOf(val.substring(idx + 1)));
                    mcu.setGpioY(Double.valueOf(val.substring(0, idx - 1)));
                }
            }
        }
        // find Codi
        try {
            findCodi(mcuId);
        } catch (Exception e) {
            log.error(e, e);
        }

        // find Mobile Port Information
        try {
            findMobilePort(mcuId);
        } catch (Exception e) {
            log.error(e, e);
        }

        // find Lan Port Information
        try {
            findLanPort(mcuId);
        } catch (Exception e) {
            log.error(e, e);
        }

        mcuDao.update(mcu);
    }

    /**
     * 모뎀의 배터리 로그 정보 취득 (단 모뎀의 전원타입이 Battery이고 타입이 ZBRepeater, ZEUPLS인 경우 해당)
     * readcount - log count (max 50)
     *
     * @param mcuId
     * @param modemId
     * @return
     * @throws Exception
     */
    @SuppressWarnings( { "unchecked", "unused" })
    public Map getSensorBatteryLog(String mcuId, String modemId,
            int readcount, int serviceType, String operator) throws Exception {
        Map map = new HashMap();
        try {
            log.debug("getSensorBatteryLog,[" + mcuId + "],[" + modemId + "],["
                    + readcount + "]");
            ModemROM modemROM = null;

            MCU mcu = mcuDao.get(mcuId);
            String revision = mcu.getSysSwRevision();
            if (revision == null || "".equals(revision)) {
                throw new Exception("Check MCU[" + mcuId + "] revision!");
            }

            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            modemROM = gw.cmdGetModemROM1(mcuId, modemId, 
                    makeIntArray(new int[]{ModemROM.OFFSET_BATTERY_POINTER,1}));

            Modem sensor = modemDao.get(modemId);
            if (revision.compareTo("2688") >= 0) {
                if (isAsynch(sensor)) {
                    long trId = gw.cmdAsynchronousCall(
                                    mcuId,
                                    "eui64Entry",
                                    sensor.getModemType().name(),
                                    modemId,
                                    "cmdGetModemBattery",
                                    (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT
                                            .getCode()
                                            | (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE
                                                    .getCode(), 0, 0, 2,
                                    makeStringArray(new String[]{"sensorID", modemId}, 
                                            new String[]{"byteEntry", ""+readcount}),
                                    serviceType, operator);
                    map.put("result", "Success");
                    map.put("commandMethod", "AsynchronousCall");
                    map.put("trId", trId);
                } else {
                    BatteryLog batteryLog = gw.cmdGetModemBattery(mcuId, modemId, readcount);
                    map.put("result", "Success");
                    map.put("commandMethod", "GetSensorEvent");
                    map.put("eventLog", batteryLog);
                }
            } else {
                int maxlen = 60;
                int len = 5 * readcount;
                int pointer = modemROM.getPointer();

                int i = 0;
                int newpointer = ModemROM.OFFSET_BATTERY_LOGDATA; // +
                // ((pointer-(maxlen/5*i))%50)*5;
                log.debug("getSensorBatteryLog find pointer=[" + pointer
                        + "], address = [" + newpointer + "]");
                modemROM = gw.cmdGetModemROM1(mcuId, modemId, makeIntArray(new int[]{newpointer, 250*5}));
                BatteryLog batteryLog = new BatteryLog();
                BeanUtils.copyProperties(batteryLog, modemROM.getBatteryLog());
                map.put("result", "Success");
                map.put("commandMethod", "GetModemROM");
                map.put("batteryLog", batteryLog);
            }
        } catch (Exception e) {
            map.put("result", "Fail");
            map.put("errorLog", e.getMessage());
        }
        return map;
    }

    /**
     * 모뎀의 이벤트 로그 취득(모뎀타입이 리피터, ZEUPLS, ZRU, MBUS)인 경우 취득 가능 readcount - event
     * count (max 250)
     *
     * @param mcuId
     * @param modemId
     * @param count
     * @return
     * @throws Exception
     */
    public Map getSensorEventLog(MCU mcu, Modem modem, int readcount,
            int serviceType, String operator) throws Exception {
        Map map = new HashMap();

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcu.getSysID(), modem.getDeviceSerial(), null));

            List<EventLog> list = new ArrayList<EventLog>();
            log.debug("getSensorEventLog,[" + mcu.getSysID() + "],["
                    + modem.getDeviceSerial() + "],[" + readcount + "]");

            String revision = mcu.getSysSwRevision();
            if (revision == null || "".equals(revision)) {
                //throw new Exception("Check MCU[" + mcu.getSysID()
                //        + "] revision!");
                revision = "";
            }

            if (revision.compareTo("2688") >= 0) {
                if (isAsynch(modem)) {
                    long trId = gw.cmdAsynchronousCall(
                                    mcu.getSysID(),
                                    "eui64Entry",
                                    modem.getModemType().name(),
                                    modem.getDeviceSerial(),
                                    "cmdGetModemEvent",
                                    (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT
                                            .getCode()
                                            | (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE
                                                    .getCode(), 0, 0, 2,
                                    makeStringArray(new String[]{"sensorID", modem.getDeviceSerial()},
                                            new String[]{"byteEntry", ""+readcount}),
                                    serviceType, operator);
                    map.put("result", "Success");
                    map.put("commandMethod", "AsynchronousCall");
                    map.put("trId", trId);
                } else {
                    List<EventLog> ellist = gw.cmdGetModemEvent(mcu.getSysID(),
                            modem.getDeviceSerial(), readcount);
                    map.put("result", "Success");
                    map.put("commandMethod", "GetSensorEvent");
                    map.put("eventLog", ellist);
                }
            } 
            else if(modem.getAmiNetworkAddress() != null && !"".equals(modem.getAmiNetworkAddress())){
                List<EventLog> ellist = gw.cmdGetModemEvent(mcu.getSysID(),
                        modem.getDeviceSerial(), readcount);
                map.put("result", "Success");
                map.put("commandMethod", "GetSensorEvent");
                map.put("eventLog", ellist);  
            }
            else 
            {
                ModemROM modemROM = gw.cmdGetModemROM1(mcu.getSysID(),
                        modem.getDeviceSerial(), makeIntArray(new int[]{ModemROM.OFFSET_EVENT_POINTER, 1}));

                int pointer = modemROM.getPointer();

                int newpointer = ModemROM.OFFSET_EVENT_LOGDATA;// +
                // ((250+pointer)%250)*16;
                log.debug("getSensorEventLog find pointer=[" + pointer
                        + "], address = [" + newpointer + "]");
                modemROM = gw.cmdGetModemROM1(mcu.getSysID(), modem
                        .getDeviceSerial(), makeIntArray(new int[]{newpointer, 250*16}));
                for (EventLog el : modemROM.getEventLog()) {
                    list.add(el);
                }
                map.put("result", "Success");
                map.put("commandMethod", "GetModemROM");
                map.put("eventLog", list);
            }
        } catch (Exception e) {
            map.put("result", "Fail");
            map.put("errorLog", e.getMessage());
        }
        return map;
    }

    private List<IntArray> makeIntArray(int[]...param) {
        List<IntArray> list = new ArrayList<IntArray>();
        for (int i = 0 ; i < param.length; i++) {
            IntArray ia = new IntArray();
            for (int j = 0;  j < param[i].length; j++) {
                ia.getItem().add(param[i][j]);
            }
            list.add(ia);
        }
        return list;
    }
    
    private List<StringArray> makeStringArray(String[]...param) {
        List<StringArray> list = new ArrayList<StringArray>();
        for (int i = 0 ; i < param.length; i++) {
            StringArray ia = new StringArray();
            for (int j = 0;  j < param[i].length; j++) {
                ia.getItem().add(param[i][j]);
            }
            list.add(ia);
        }
        return list;
    }
    
    /**
     * 모뎀의 LP 로그 취득(모뎀타입이 리피터, ZEUPLS, ZRU, MBUS)인 경우 취득 가능 모뎀이 검침데이터를 저장하고
     * 있는경우에 해당 day lp days ex) day 1 ==> current day ex) day 2 ==> current day,
     * yesterday
     *
     * @param mcuId
     * @param modemId
     * @param day
     * @return
     * @throws Exception
     */

    public LPData[] getSensorLPLog(String mcuId, String modemId, int day)
            throws Exception {
        if (day > 40) {
            day = 40;// max 40 days
        }

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));

            log.debug("getSensorLPLog,[" + mcuId + "],[" + modemId + "],["
                    + day + "]");
            ModemROM modemROM = null;
            modemROM = gw.cmdGetModemROM1(mcuId, modemId, makeIntArray(new int[]{ModemROM.OFFSET_METER_LPPERIOD, 2}));
            int lpPeriod = modemROM.getLpPeriod();
            int lpPointer = modemROM.getPointer();
            ByteArray ba = new ByteArray();
            // TODO know calculate pointer position
            int pointer = ModemROM.OFFSET_METER_LPLOGDATA
                    + ((40 + lpPointer - day) % 40) * ((48 * lpPeriod) + 8);
            int len = (8+(48*lpPeriod))*(day+1);
            // int len = (8 + (48 * lpPeriod));
            log.debug("getSensorEventLog find lppointer=[" + lpPointer
                    + "], address = [" + pointer + "]");
            modemROM = gw.cmdGetModemROM1(mcuId, modemId, makeIntArray(new int[]{pointer, len}));
            modemROM.parseLP(lpPeriod);

            return modemROM.getLpData();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 모뎀의 롬 정보 읽기(펌웨어 버전,메모리정보,검침주기 등의 정보를 취득) 모뎀 타입이 지그비 타입인 경우 ZRU, 리피터,
     * ZEUPLS, ZRU, MBUS인 경우 취득 가능
     *
     * @param gw
     * @param sensor
     * @param mcuId
     * @param modemId
     * @param serviceType
     * @param operator
     * @return
     * @throws Exception
     */
    public Modem doGetModemROM(CommandWS gw, Modem sensor,
            String mcuId, String modemId, int serviceType, String operator)
            throws Exception {
        log.info("doGetModemROM sensor[" + sensor.getDeviceSerial()
                + "], MCU ID :" + mcuId);

        String fwVersion = sensor.getFwVer();
        String fwBuild = sensor.getFwRevision();
        ModemROM modemROM = new ModemROM(fwVersion, fwBuild);
        String revision = sensor.getMcu().getSysSwRevision();
        ZEUPLS zeupls = null;
        ZRU zru = null;
        ZBRepeater repeater = null;
        ZMU zmu = null;

        if (sensor instanceof ZEUPLS) {
            zeupls = (ZEUPLS) sensor;
        } else if (sensor instanceof ZRU) {
            zru = (ZRU) sensor;
        } else if (sensor instanceof ZBRepeater) {
            repeater = (ZBRepeater) sensor;
        } else if (sensor instanceof ZMU) {
            zmu = (ZMU) sensor;
        }

        try {
            if (gw == null) {
                gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));
            }

            log.debug("revision: " + (revision.compareTo("2688") >= 0));
            log.debug("isAsync:" + isAsynch(sensor));
            if (revision.compareTo("2688") >= 0 && isAsynch(sensor)) {
                log.debug("here~!");
                long trId = gw
                        .cmdAsynchronousCall(
                                mcuId,
                                "eui64Entry",
                                sensor.getModemType().name(),
                                modemId,
                                "cmdGetModemROM",
                                (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT
                                        .getCode()
                                        | (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE
                                                .getCode(),
                                0,
                                0,
                                2,
                                makeStringArray(new String[]{ "sensorID", modemId },
                                        new String[]{"wordEntry", "" + ModemROM.OFFSET_MANUAL_ENABLE },
                                        new String[]{"wordEntry","" + modemROM.getNetworkSize() },
                                        new String[]{"wordEntry","" + ModemROM.OFFSET_NODEKIND },
                                        new String[]{"wordEntry","" + modemROM.getNodeSize() },
                                        new String[]{"wordEntry",""+ modemROM.OFFSET_METER_SERIAL_NUMBER },
                                        new String[]{"wordEntry","" + modemROM.getAmrSize() },
                                        new String[]{"wordEntry",""+ modemROM.OFFSET_METER_LPPERIOD },
                                        new String[]{"wordEntry", "2" },
                                        new String[]{"wordEntry",""+ modemROM.OFFSET_NETWORK_TYPE },
                                        new String[]{ "wordEntry", "1" }), serviceType,
                                operator);
                // TODO IMPLEMENT
                // sensor.addProperty(new MOPROPERTY("commandMethod",
                // "AsynchronousCall"));
                // sensor.addProperty(new MOPROPERTY("trId", ""+trId));

            } else {
                modemROM = gw.cmdGetModemROM1(mcuId, modemId, 
                        makeIntArray(new int[]{ ModemROM.OFFSET_MANUAL_ENABLE, modemROM.getNetworkSize() },
                                new int[]{ ModemROM.OFFSET_NODEKIND, modemROM.getNodeSize() },
                                new int[]{ ModemROM.OFFSET_METER_SERIAL_NUMBER,modemROM.getAmrSize() },
                                new int[]{ ModemROM.OFFSET_METER_LPPERIOD, 2 }));

                ModemNetwork sn = modemROM.getModemNetwork();
                ModemNode sNode = modemROM.getModemNode();
                AmrData amrData = modemROM.getAmrData();

                // install date
                if (sensor.getInstallDate() == null
                        || "".equals(sensor.getInstallDate())) {
                    if (zeupls != null) {
                        zeupls.setInstallDate(DateTimeUtil
                                .getCurrentDateTimeByFormat(null));
                    } else if (zru != null) {
                        zru.setInstallDate(DateTimeUtil
                                .getCurrentDateTimeByFormat(null));
                    } else if (repeater != null) {
                        repeater.setInstallDate(DateTimeUtil
                                .getCurrentDateTimeByFormat(null));
                    } else if (zmu != null) {
                        zmu.setInstallDate(DateTimeUtil
                                .getCurrentDateTimeByFormat(null));
                    }
                }

                if (sn != null) {
                    if (zeupls != null) {
                        zeupls.setLinkKey(sn.getLinkKey());

                        zeupls.setNetworkKey(sn.getNetworkKey());
                        zeupls.setExtPanId(sn.getExtPanId());
                        // sensor.setProperty(new
                        // MOPROPERTY("txPower",sn.getTxPower()+""));
                        zeupls.setChannelId(sn.getChannel());
                        zeupls
                                .setManualEnable(sn.getManualEnable() == 255 ? false
                                        : true);
                        zeupls.setPanId(sn.getPanId());
                        zeupls
                                .setSecurityEnable(sn.getSecurityEnable() == 255 ? false
                                        : true);
                    } else if (zru != null) {
                        zru.setLinkKey(sn.getLinkKey());
                        zru.setNetworkKey(sn.getNetworkKey());
                        zru.setExtPanId(sn.getExtPanId());
                        zru.setChannelId(sn.getChannel());
                        zru.setManualEnable(sn.getManualEnable() == 255 ? false
                                : true);
                        zru.setPanId(sn.getPanId());
                        zru
                                .setSecurityEnable(sn.getSecurityEnable() == 255 ? false
                                        : true);
                    } else if (repeater != null) {
                        repeater.setLinkKey(sn.getLinkKey());
                        repeater.setNetworkKey(sn.getNetworkKey());
                        repeater.setExtPanId(sn.getExtPanId());
                        repeater.setChannelId(sn.getChannel());
                        repeater
                                .setManualEnable(sn.getManualEnable() == 255 ? false
                                        : true);
                        repeater.setPanId(sn.getPanId());
                        repeater
                                .setSecurityEnable(sn.getSecurityEnable() == 255 ? false
                                        : true);
                    } else if (zmu != null) {
                        zmu.setLinkKey(sn.getLinkKey());
                        zmu.setNetworkKey(sn.getNetworkKey());
                        zmu.setExtPanId(sn.getExtPanId());
                        zmu.setChannelId(sn.getChannel());
                        zmu.setManualEnable(sn.getManualEnable() == 255 ? false
                                : true);
                        zmu.setPanId(sn.getPanId());
                        zmu
                                .setSecurityEnable(sn.getSecurityEnable() == 255 ? false
                                        : true);
                    }

                }
                if (sNode != null) {

                    log.debug("protocolVerion[" + sNode.getProtocolVersion()
                            + "]");
                    if (zeupls != null) {
                        zeupls.setHwVer(sNode.getHardwareVersion());
                        zeupls.setNodeKind(sNode.getNodeKind());
                        zeupls.setProtocolVersion(sNode.getProtocolVersion());
                        zeupls.setResetCount(sNode.getResetCount());
                        zeupls.setLastResetCode(sNode.getResetReason());
                        zeupls.setSwVer(sNode.getSoftwareVersion());
                        zeupls.setFwVer(sNode.getFirmwareVersion());
                        zeupls.setFwRevision(sNode.getFirmwareBuild());
                        zeupls
                                .setZdzdIfVersion(sNode
                                        .getZdzdInterfaceVersion());
                    } else if (zru != null) {
                        zru.setHwVer(sNode.getHardwareVersion());
                        zru.setNodeKind(sNode.getNodeKind());
                        zru.setProtocolVersion(sNode.getProtocolVersion());
                        zru.setResetCount(sNode.getResetCount());
                        zru.setLastResetCode(sNode.getResetReason());
                        zru.setSwVer(sNode.getSoftwareVersion());
                        zru.setFwVer(sNode.getFirmwareVersion());
                        zru.setFwRevision(sNode.getFirmwareBuild());
                        zru.setZdzdIfVersion(sNode.getZdzdInterfaceVersion());
                    } else if (repeater != null) {
                        repeater.setHwVer(sNode.getHardwareVersion());
                        repeater.setNodeKind(sNode.getNodeKind());
                        repeater.setProtocolVersion(sNode.getProtocolVersion());
                        repeater.setResetCount(sNode.getResetCount());
                        repeater.setLastResetCode(sNode.getResetReason());
                        repeater.setSwVer(sNode.getSoftwareVersion());
                        repeater.setFwVer(sNode.getFirmwareVersion());
                        repeater.setFwRevision(sNode.getFirmwareBuild());
                        repeater.setZdzdIfVersion(sNode
                                .getZdzdInterfaceVersion());
                    } else if (zmu != null) {
                        zmu.setHwVer(sNode.getHardwareVersion());
                        zmu.setNodeKind(sNode.getNodeKind());
                        zmu.setProtocolVersion(sNode.getProtocolVersion());
                        zmu.setResetCount(sNode.getResetCount());
                        zmu.setLastResetCode(sNode.getResetReason());
                        zmu.setSwVer(sNode.getSoftwareVersion());
                        zmu.setFwVer(sNode.getFirmwareVersion());
                        zmu.setFwRevision(sNode.getFirmwareBuild());
                        zmu.setZdzdIfVersion(sNode.getZdzdInterfaceVersion());
                    }

                    if ((sensor.getModemType().equals(ModemType.ZEUPLS) || sensor
                            .getModemType().equals(ModemType.Repeater))
                            && fwVersion.compareTo("2.1") >= 0
                            && fwBuild.compareTo("18") >= 0) {
                        if (zeupls != null) {
                            zeupls.setSolarADV(sNode.getSolarADVolt());
                            zeupls.setSolarChgBV(sNode.getSolarChgBattVolt());
                            zeupls.setSolarBDCV(sNode.getSolarBDCVolt());
                        } else if (repeater != null) {
                            repeater.setSolarADV(sNode.getSolarADVolt());
                            repeater.setSolarChgBV(sNode.getSolarChgBattVolt());
                            repeater.setSolarBDCV(sNode.getSolarBDCVolt());
                        }

                    }

                } else {
                    log.debug("sNode is null!!");
                }
                if (amrData != null) {
                    // sensor.setProperty(new
                    // MOPROPERTY("vendor",amrData.getVendor()));

                    if (zeupls != null) {
                        zeupls.setTestFlag(Boolean.valueOf(amrData
                                .getTestFlag()
                                + ""));
                        zeupls.setFixedReset(amrData.getFixedReset() + "");
                    } else if (zru != null) {
                        zru.setTestFlag(Boolean.valueOf(amrData.getTestFlag()
                                + ""));
                        zru.setFixedReset(amrData.getFixedReset() + "");
                    } else if (repeater != null) {
                        repeater.setTestFlag(Boolean.valueOf(amrData
                                .getTestFlag()
                                + ""));
                        repeater.setFixedReset(amrData.getFixedReset() + "");
                    }

                    StringBuffer mask = new StringBuffer();
                    for (int i = 0; i < amrData.getMeteringDay().length; i++) {
                        mask.append("" + amrData.getMeteringDay()[i]);
                    }

                    if (zeupls != null) {
                        zeupls.setMeteringDay(mask.toString());
                    } else if (zru != null) {
                        zru.setMeteringDay(mask.toString());
                    } else if (repeater != null) {
                        repeater.setMeteringDay(mask.toString());
                    }
                    mask.setLength(0);
                    for (int i = 0; i < amrData.getMeteringHour().length; i++) {
                        mask.append("" + amrData.getMeteringHour()[i]);
                    }

                    if (zeupls != null) {
                        zeupls.setMeteringHour(mask.toString());
                        zeupls.setLpChoice(amrData.getLpChoice());
                    } else if (zru != null) {
                        zru.setMeteringHour(mask.toString());
                        zru.setLpChoice(amrData.getLpChoice());
                    } else if (repeater != null) {
                        repeater.setMeteringHour(mask.toString());
                        repeater.setLpChoice(amrData.getLpChoice());
                    }
                    if (fwVersion.compareTo("2.1") >= 0
                            && fwBuild.compareTo("18") >= 0) {

                        if (zeupls != null) {
                            zeupls.setAlarmFlag(amrData.getAlarmFlag());
                            zeupls.setPermitMode(amrData.getPermitMode());
                            zeupls.setPermitState(amrData.getPermitState());
                            zeupls.setAlarmMask(amrData.getAlarmMask());
                        }
                    }

                    String meterId = amrData.getMeterSerialNumber();
                    log.info("METERID[" + meterId + "]");
                    if (meterId != null && !"".equals(meterId)) {
                        Meter meter = meterDao.get(meterId);
                        meterDao.get(meterId);

                        if (meter != null) {
                            meter.setModem(sensor);
                        }
                    }
                }
            }

            modemDao.update(sensor);

            Modem retModem = null;
            if (zeupls != null) {
                retModem = zeupls;
            } else if (zru != null) {
                retModem = zru;
            } else if (repeater != null) {
                retModem = repeater;
            } else if (zmu != null) {
                retModem = zmu;
            }
            return retModem;
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }

    /**
     * 모뎀의 롬 정보 읽기(펌웨어 버전,메모리정보,검침주기 등의 정보를 취득) 모뎀 타입이 지그비 타입인 경우 ZRU 경우 취득 가능
     *
     * @param gw
     * @param sensor
     * @param mcuId
     * @param modemId
     * @param serviceType
     * @param operator
     * @return
     * @throws Exception
     */
    @Transactional(readOnly=false)
    public String doSensorScanning(String mcuId, String modemId,
            String modemType, int serviceType, String operator)
            throws Exception {
        log.info("doZRUScanning sensor mcuId [" + mcuId + "]");
        log.info("doZRUScanning sensor modemId[" + modemId + "]");
        log.info("doZRUScanning sensor modemType[" + modemType + "]");
        log.info("doZRUScanning sensor serviceType[" + serviceType + "]");
        log.info("doZRUScanning sensor operator[" + operator + "]");

        String ret = null;
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));
            log.info("doZRUScanning mcuId[" + mcuId + "]");
            Modem modem = modemDao.get(Integer.parseInt(modemId));
            if(modem != null && modem.getAmiNetworkAddress() != null && !"".equals(modem.getAmiNetworkAddress())){
                
                log.info("doGetModemCluster[" + modemId + "]");
                ret = gw.doGetModemCluster(mcuId, modemId, modemType,
                        serviceType, operator);
            }else{
                log.info("cmdGetModemInfoNew[" + modemId + "]");
                sensorInfoNewEntry sensorEntries = gw.cmdGetModemInfoNew(modem.getDeviceSerial());

                modem.setCommState(new BYTE(sensorEntries.getSensorState().getValue()).getValue());
                modem.setFwRevision(new WORD(sensorEntries.getSensorFwBuild().getValue()).getValue() + "");
                modem.setFwVer(new WORD(sensorEntries.getSensorFwVersion().getValue()).decodeVersion());
                modem.setHwVer(new WORD(sensorEntries.getSensorHwVersion().getValue()).decodeVersion());
                modem.setLastLinkTime(new TIMESTAMP(sensorEntries.getSensorLastConnect().getValue()).getValue());
                modem.setNodeKind(new OCTET(sensorEntries.getSensorModel().getValue()).toString());
                modemDao.update(modem);
                
                ret = "success";
            }

            return ret;
        } catch (Exception e) {
            throw e;
        }
    }
    
    public Map<String, Object> cmdGetModemInfoNew(String sensorId, String sysId) throws Exception {
        sensorInfoNewEntry sensorEntries;
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(sysId, null, null));
            sensorEntries = gw.cmdGetModemInfoNew(sensorId);
        } catch (Exception e) {
            throw e;
        }
        log.debug("update modem info [ modem=" + new HEX(sensorEntries.getSensorID().getValue()).toString()+"]");
        Map<String, Object> map = new HashMap<String, Object>(); 
        map.put("deviceSerial", new HEX(sensorEntries.getSensorID().getValue()).getValue());
        map.put("commState", new BYTE(sensorEntries.getSensorState().getValue()).getValue());
        map.put("fwVer", new WORD(sensorEntries.getSensorFwVersion().getValue()).decodeVersion());
        map.put("fwRevision", new WORD(sensorEntries.getSensorFwBuild().getValue()).getValue() + "");
        map.put("hwVer", new WORD(sensorEntries.getSensorHwVersion().getValue()).decodeVersion());
        map.put("lastLinkTime", new TIMESTAMP(sensorEntries.getSensorLastConnect().getValue()).getValue());
        map.put("nodeKind", new OCTET(sensorEntries.getSensorModel().getValue()).toString());
        map.put("mdsId", new OCTET(sensorEntries.getSensorSerial().getValue()).toString());
        map.put("sensorType", getSensorType(sensorEntries.getSensorModel().toString()));
        return map;
    }    

    /**
     * 집중기 정보 등록(서버에 등록되지 않은 집중기인 경우)
     *
     * @param mcu
     * @throws Exception
     */
    public void registerMCU(MCU mcu) throws Exception {
        log.info("registerMCU MCU[" + mcu.getSysID() + "]");
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcu.getSysID(), null, null));

            String mcuInstName = mcu.getSysID();
            String mcuId = mcu.getSysID();

            // Time Synchronization
            gw.cmdMcuSetTime(mcuId, TimeUtil.getCurrentTime());

            // Set Default Configuration
            Hashtable mo = setDefaultMcuConfig(mcu, mcuId, mcu.getMcuType()
                    .getName());

            DefaultConf defaultConf = DefaultConf.getInstance();
            Hashtable props = defaultConf.getDefaultProperties("MCU");

            log.debug("props size=" + props.size());
            MIBUtil mibUtil = MIBUtil.getInstance();
            List<String> property = new ArrayList<String>();
            Iterator it = props.keySet().iterator();
            for (int i = 0; it.hasNext(); i++) {
                try {

                    String key = (String) it.next();
                    property.add((mibUtil.getOid(key)).getValue());
                    log.debug("props[" + i + "] :" + key + " ,oid= "
                            + property.get(i));
                } catch (Exception e) {
                }
            }
            
            ResponseMap rmap = gw.cmdStdGet1(mcuId, property);

            String val;
            for (ResponseMap.Response.Entry e : rmap.getResponse().getEntry()) {
                if (((String)e.getKey()).contains("geographicCoordsX")) {
                    val = (String) e.getValue();
                    int idx = val.indexOf(",");
                    if (idx > 0) {
                        mcu.setGpioX(Double.valueOf(val.substring(idx + 1)));
                        mcu.setGpioY(Double.valueOf(val.substring(0, idx - 1)));
                    }
                }
            }

            // find Codi
            try {
                findCodi(mcuId);
            } catch (Exception e) {
                log.error(e, e);
            }

            // find Mobile Port Information
            try {
                findMobilePort(mcuId);
            } catch (Exception e) {
                log.error(e, e);
            }

            // find Lan Port Information
            try {
                findLanPort(mcuId);
            } catch (Exception e) {
                log.error(e, e);
            }

            mcu.setNetworkStatus(1);
            mcu.setInstallDate(TimeUtil.getCurrentTime());
            mcu.setLastCommDate(TimeUtil.getCurrentTime());

            mcuDao.update(mcu);

            try {
                gw.cmdMcuReset(mcuId);
            } catch (Exception e) {
                log.error(e, e);
            }
        } catch (Exception e) {
            throw e;
        }

    }

    // TODO IMPLEMENT
    /*
     * public void registerZRU(String mcuId, ZRU sensor, int serviceType,
     * String operator) throws Exception {
     * log.info("registerZRU MCU["+mcuId+"] SENSOR["
     * +sensor.getDeviceSerial()+"]"); CommandGWMBean gw = getCommandGW();
     * gw.cmdSetModem(mcuId,sensor); if(!sensor.isHasProperty("mcuId")) {
     * sensor.addProperty(new MOPROPERTY("mcuId",mcuId)); }
     * doZRUScanning(sensor, serviceType, operator); String curTime =
     * TimeUtil.getCurrentTime(); sensor.setProperty(new
     * MOPROPERTY("installDate",curTime)); sensor.setProperty(new
     * MOPROPERTY("lastLinkTime",curTime)); sensor.setProperty(new
     * MOPROPERTY("lastModifiedTime",curTime)); sensor.setProperty(new
     * MOPROPERTY("commState","1")); IUtil.getMIOLocal().SetInstance(sensor); }
     *
     * public void registerZMU(String mcuId, ZMU sensor, int serviceType,
     * String operator) throws Exception {
     * log.info("registerZMU MCU["+mcuId+"] SENSOR["
     * +sensor.getDeviceSerial()+"]"); CommandGWMBean gw = getCommandGW();
     * gw.cmdSetSensorMO(mcuId,sensor); if(!sensor.isHasProperty("mcuId")) {
     * sensor.addProperty(new MOPROPERTY("mcuId",mcuId)); }
     * doZMUScanning(sensor, serviceType, operator); String curTime =
     * TimeUtil.getCurrentTime(); sensor.setProperty(new
     * MOPROPERTY("installDate",curTime)); sensor.setProperty(new
     * MOPROPERTY("lastLinkTime",curTime)); sensor.setProperty(new
     * MOPROPERTY("lastModifiedTime",curTime)); sensor.setProperty(new
     * MOPROPERTY("commState","1")); IUtil.getMIOLocal().SetInstance(sensor); }
     *
     * public void registerZEUPLS(String mcuId, ZEUPLS sensor, int
     * serviceType, String operator) throws Exception {
     * log.info("registerZEUPLS MCU["
     * +mcuId+"] SENSOR["+sensor.getDeviceSerial()+"]"); CommandGWMBean gw =
     * getCommandGW(); gw.cmdSetSensorMO(mcuId,sensor);
     * if(!sensor.isHasProperty("mcuId")) { sensor.addProperty(new
     * MOPROPERTY("mcuId",mcuId)); } doZEUPLSScanning(sensor, serviceType,
     * operator); String curTime = TimeUtil.getCurrentTime();
     * sensor.setProperty(new MOPROPERTY("installDate",curTime));
     * sensor.setProperty(new MOPROPERTY("lastLinkTime",curTime));
     * sensor.setProperty(new MOPROPERTY("lastModifiedTime",curTime));
     * sensor.setProperty(new MOPROPERTY("commState","1"));
     * IUtil.getMIOLocal().SetInstance(sensor); }
     */
    public void registerMMIU(MMIU sensor) throws Exception {
        log.info("registerMMIU SENSOR[" + sensor.getDeviceSerial() + "]");
        String curTime = TimeUtil.getCurrentTime();

        if (modemDao.exists(sensor.getId())) {

            sensor.setLastLinkTime(curTime);
            sensor.setCommState(1);
            modemDao.update(sensor);
        } else {
            sensor.setInstallDate(curTime);
            sensor.setLastLinkTime(curTime);
            sensor.setCommState(1);
            modemDao.add(sensor);
        }
    }

    public void registerIEIU(IEIU sensor) throws Exception {
        log.info("registerIEIU SENSOR[" + sensor.getDeviceSerial() + "]");
        String curTime = TimeUtil.getCurrentTime();
        if (modemDao.exists(sensor.getId())) {

            sensor.setLastLinkTime(curTime);
            sensor.setCommState(1);
            modemDao.update(sensor);
        } else {
            sensor.setInstallDate(curTime);
            sensor.setLastLinkTime(curTime);
            sensor.setCommState(1);
            modemDao.add(sensor);
        }
    }

    @Transactional(readOnly=false)
    public Map getMCUStatus(String mcuId) throws Exception {
        log.info("getMCUStatus MCU["+mcuId+"] ");

        MCU mcu = mcuDao.get(mcuId);
        DefaultConf defaultConf = DefaultConf.getInstance();
        Hashtable props = null;
        if(mcu.getNameSpace() != null && !"".equals(mcu.getNameSpace())){
        	props = defaultConf.getDefaultProperties("MCUStatus-"+mcu.getNameSpace());
        }else{
        	props = defaultConf.getDefaultProperties("MCUStatus");
        }
        
        List<String> mibPropNames = new ArrayList<String>();
        Iterator it = props.keySet().iterator();
        for (int i = 0; it.hasNext(); i++) {
            try {
                String name = (String) it.next();
                mibPropNames.add(name);
                log.debug("props[" + i + "] :" + name);
            } catch (Exception e) {
            }
        }
        
        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcu.getSysID(), null, null));
        Map resProps = new Hashtable();
        try {
            ResponseMap rmap = gw.cmdStdGet1(mcuId, mibPropNames);
            for (ResponseMap.Response.Entry e : rmap.getResponse().getEntry()) {
                resProps.put(e.getKey(), e.getValue());
            }
            //mcu.setLowBatteryFlag(new Integer((String)resProps.get("gpioLowBattery")));
            mcu.setNetworkStatus(1);
        }
        catch(Exception ex) {
            log.error(ex,ex);
            mcu.setNetworkStatus(0);
        }
        
        mcuDao.update(mcu);
        
        return resProps; 
    }
     
    public Modem getSensorStatus(MCU mcu, Modem modem, int serviceType,
            String operator) throws Exception {
        log.info("getZRUStatus MCU[" + mcu.getSysID() + "] SENSOR["
                + modem.getDeviceSerial() + "]");

        String fwVersion = modem.getFwVer();
        String fwBuild = modem.getFwRevision();

        ZRU zru = null;
        ZBRepeater repeater = null;
        ZMU zmu = null;
        ZEUPLS zeupls = null;

        ModemROM modemROM = new ModemROM(fwVersion, fwBuild);
        /*
        if (modem instanceof ZRU) {
            zru = (ZRU) modem;
            modemROM = new ZRUROM(fwVersion, fwBuild);
        } else if (modem instanceof ZBRepeater) {
            repeater = (ZBRepeater) modem;
            modemROM = new ZEUPLSROM(fwVersion, fwBuild);
        } else if (modem instanceof ZMU) {
            zmu = (ZMU) modem;
            modemROM = new ZRUROM(fwVersion, fwBuild);
        } else if (modem instanceof ZEUPLS) {
            zeupls = (ZEUPLS) modem;
            modemROM = new ZEUPLSROM(fwVersion, fwBuild);
        }
        */
        
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcu.getSysID(), modem.getDeviceSerial(), null));

            String revision = mcu.getSysSwRevision();
            if (revision.compareTo("2688") >= 0 && isAsynch(modem)) {
                long trId = gw
                        .cmdAsynchronousCall(
                                mcu.getSysID(),
                                "eui64Entry",
                                "ZRU",
                                modem.getDeviceSerial(),
                                "cmdGetModemROM",
                                (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT
                                        .getCode()
                                        | (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE
                                                .getCode(),
                                0,
                                0,
                                2,
                                makeStringArray(new String[]{ "sensorID", modem.getDeviceSerial() },
                                        new String[] {"wordEntry", ""+ ModemROM.OFFSET_NODEKIND },
                                        new String[] { "wordEntry","" + modemROM.getNodeSize() }),
                                serviceType, operator);
                // inst.addProperty(new MOPROPERTY("commandMethod",
                // "AsynchronousCall"));//TODO FIND
                // inst.addProperty(new MOPROPERTY("trId", ""+trId));//TODO
                // FIND

            } else {
                modemROM = gw.cmdGetModemROM1(mcu.getSysID(), modem
                        .getDeviceSerial(),
                        makeIntArray(new int[] {ModemROM.OFFSET_NODEKIND, modemROM.getNodeSize()}));
                ModemNode sNode = modemROM.getModemNode();

                if (sNode != null) {
                    modem.setFwVer(sNode.getSoftwareVersion());
                    modem.setHwVer(sNode.getHardwareVersion());
                    modem.setNodeKind(sNode.getNodeKind());
                    modem.setProtocolVersion(sNode.getProtocolVersion());
                    modem.setResetCount(sNode.getResetCount());
                    modem.setLastResetCode(sNode.getResetReason());
                    modem.setCommState(1);
                    if (modem instanceof ZRU) {
                        zru.setFwVer(sNode.getSoftwareVersion());
                        zru.setHwVer(sNode.getHardwareVersion());
                        zru.setNodeKind(sNode.getNodeKind());
                        zru.setProtocolVersion(sNode.getProtocolVersion());
                        zru.setResetCount(sNode.getResetCount());
                        zru.setLastResetCode(sNode.getResetReason());
                        zru.setCommState(1);
                        return zru;
                    } else if (modem instanceof ZBRepeater) {

                        repeater.setFwVer(sNode.getSoftwareVersion());
                        repeater.setHwVer(sNode.getHardwareVersion());
                        repeater.setNodeKind(sNode.getNodeKind());
                        repeater.setProtocolVersion(sNode
                                .getProtocolVersion());
                        repeater.setResetCount(sNode.getResetCount());
                        repeater.setLastResetCode(sNode.getResetReason());
                        repeater.setCommState(1);
                        return repeater;
                    } else if (modem instanceof ZMU) {
                        zmu.setFwVer(sNode.getSoftwareVersion());
                        zmu.setHwVer(sNode.getHardwareVersion());
                        zmu.setNodeKind(sNode.getNodeKind());
                        zmu.setProtocolVersion(sNode.getProtocolVersion());
                        zmu.setResetCount(sNode.getResetCount());
                        zmu.setLastResetCode(sNode.getResetReason());
                        zmu.setCommState(1);
                        return zmu;
                    } else if (modem instanceof ZEUPLS) {
                        zeupls = (ZEUPLS) modem;
                        zeupls.setFwVer(sNode.getSoftwareVersion());
                        zeupls.setHwVer(sNode.getHardwareVersion());
                        zeupls.setNodeKind(sNode.getNodeKind());
                        zeupls.setProtocolVersion(sNode
                                .getProtocolVersion());
                        zeupls.setResetCount(sNode.getResetCount());
                        zeupls.setLastResetCode(sNode.getResetReason());
                        zeupls.setCommState(1);
                        return zeupls;
                    }
                }
            }
        } catch (Exception ex) {
            log.error(ex, ex);
            modem.setCommState(0);
            throw ex;
        }

        // zruDao.update(zru);
        return modem;
    }

    public ZRU getZRUStatus(String mcuId, String modemId,
            int serviceType, String operator) throws Exception {
        log.info("getZRUStatus MCU[" + mcuId + "] SENSOR[" + modemId + "]");

        if (mcuId == null || "".equals(mcuId)) {
            throw new Exception("MCU is not existed");
        }

        MCU mcu = mcuDao.get(mcuId);
        ZRU zru = (ZRU) modemDao.get(modemId);
        String fwVersion = zru.getFwVer();
        String fwBuild = zru.getFwRevision();
        ModemROM modemROM = new ModemROM(fwVersion, fwBuild);

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));

            String revision = mcu.getSysSwRevision();
            if (revision.compareTo("2688") >= 0 && isAsynch(zru)) {
                long trId = gw
                        .cmdAsynchronousCall(
                                mcuId,
                                "eui64Entry",
                                "ZRU",
                                modemId,
                                "cmdGetModemROM",
                                (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT
                                        .getCode()
                                        | (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE
                                                .getCode(),
                                0,
                                0,
                                2,
                                makeStringArray(new String[] {"sensorID", modemId },
                                        new String[] {"wordEntry", "" + ModemROM.OFFSET_NODEKIND },
                                        new String[] { "wordEntry", "" + modemROM.getNodeSize() }),
                                serviceType, operator);
                // inst.addProperty(new MOPROPERTY("commandMethod",
                // "AsynchronousCall"));//TODO FIND
                // inst.addProperty(new MOPROPERTY("trId", ""+trId));//TODO
                // FIND

            } else {
                modemROM = gw.cmdGetModemROM1(mcuId, modemId,
                        makeIntArray(new int[]{ ModemROM.OFFSET_NODEKIND, modemROM.getNodeSize() }));
                ModemNode sNode = modemROM.getModemNode();

                if (sNode != null) {
                    zru.setFwVer(sNode.getSoftwareVersion());
                    zru.setHwVer(sNode.getHardwareVersion());
                    zru.setNodeKind(sNode.getNodeKind());
                    zru.setProtocolVersion(sNode.getProtocolVersion());
                    zru.setResetCount(sNode.getResetCount());
                    zru.setLastResetCode(sNode.getResetReason());
                    zru.setCommState(1);
                }
            }
        } catch (Exception ex) {
            log.error(ex, ex);
            zru.setCommState(0);
            throw ex;
        }

        zruDao.update(zru);
        return zru;
    }

    public ZBRepeater getZBRepeaterStatus(String mcuId, String modemId,
            int serviceType, String operator) throws Exception {
        log.info("MCU[" + mcuId + "] SENSOR[" + modemId + "]");

        if (mcuId == null || "".equals(mcuId)) {
            throw new Exception("MCU is not existed");
        }

        MCU mcu = mcuDao.get(mcuId);
        ZBRepeater modem = (ZBRepeater) modemDao.get(modemId);
        String fwVersion = modem.getFwVer();
        String fwBuild = modem.getFwRevision();
        ModemROM modemROM = new ModemROM(fwVersion, fwBuild);

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));

            String revision = mcu.getSysSwRevision();

            if (revision.compareTo("2688") >= 0 && isAsynch(modem)) {
                long trId = gw
                        .cmdAsynchronousCall(
                                mcuId,
                                "eui64Entry",
                                "ZBRepeater",
                                modemId,
                                "cmdGetModemROM",
                                (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT
                                        .getCode()
                                        | (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE
                                                .getCode(),
                                0,
                                0,
                                1,
                                makeStringArray(new String[] { "sensorID", modemId },
                                        new String[] {"wordEntry",""+ ModemROM.OFFSET_NODEKIND },
                                        new String[] { "wordEntry","" + modemROM.getNodeSize() }),
                                serviceType, operator);
                // inst.addProperty(new MOPROPERTY("commandMethod",
                // "AsynchronousCall")); //TODO FIND
                // inst.addProperty(new MOPROPERTY("trId", ""+trId));//TODO
                // FIND
            } else {
                modemROM = gw.cmdGetModemROM1(mcuId, modemId,
                        makeIntArray(new int[] { ModemROM.OFFSET_NODEKIND, modemROM.getNodeSize() }));
                ModemNode sNode = modemROM.getModemNode();

                if (sNode != null) {
                    modem.setFwVer(sNode.getSoftwareVersion());
                    modem.setHwVer(sNode.getHardwareVersion());
                    modem.setNodeKind(sNode.getNodeKind());
                    modem.setProtocolVersion(sNode.getProtocolVersion());
                    modem.setResetCount(sNode.getResetCount());
                    modem.setLastResetCode(sNode.getResetReason());
                    modem.setLastLinkTime(DateTimeUtil
                            .getDateString(new Date()));
                    modem.setCommState(1);
                }
            }
        } catch (Exception ex) {
            log.error(ex, ex);
            modem.setCommState(0);
            throw ex;
        }
        zbRepeaterDao.update(modem);
        return modem;
    }

    public ZMU getZMUStatus(String mcuId, String modemId,
            int serviceType, String operator) throws Exception {
        log.info("getZMUStatus MCU[" + mcuId + "] SENSOR[" + modemId + "]");

        ZMU modem = new ZMU();

        if (mcuId == null || "".equals(mcuId)) {
            throw new Exception("MCU is not existed");
        }
        MCU mcu = mcuDao.get(mcuId);

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));

            String revision = mcu.getSysSwRevision();
            if (revision.compareTo("2688") >= 0 && isAsynch(modem)) {
                long trId = gw.cmdAsynchronousCall(mcuId, "eui64Entry", modem
                        .getModemType().name(), modemId, "cmdGetModemROM",
                        (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT.getCode()
                                | (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE
                                        .getCode(), 0, 0, 2, 
                                        makeStringArray(new String[] { "sensorID", modemId }, 
                                                new String[] { "wordEntry", "0" },
                                                new String[] { "wordEntry", "0" }),
                                                serviceType, operator);
                // inst.addProperty(new MOPROPERTY("commandMethod",
                // "AsynchronousCall")); //TODO FIND
                // inst.addProperty(new MOPROPERTY("trId", ""+trId));//TODO FIND
            } else {
                ModemROM modemROM = gw.cmdGetModemROM1(mcuId, modemId,
                        makeIntArray(new int[]{ 0, 0 }));
                ModemNetwork sn = modemROM.getModemNetwork();
                ModemNode sNode = modemROM.getModemNode();
                modem.setCommState(1);
            }
        } catch (Exception ex) {
            log.error(ex, ex);
            modem.setCommState(0);
            throw ex;
        }
        modemDao.update(modem);
        return modem;
    }

    public ZEUPLS getZEUPLSStatus(String mcuId, String modemId,
            int serviceType, String operator) throws Exception {
        log.info("MCU[" + mcuId + "] SENSOR[" + modemId + "]");

        if (mcuId == null || "".equals(mcuId)) {
            throw new Exception("MCU is not existed");
        }

        MCU mcu = mcuDao.get(mcuId);
        ZEUPLS modem = zeuplsDao.get(modemId);
        String fwVersion = modem.getFwVer();
        String fwBuild = modem.getFwRevision();
        ModemROM modemROM = new ModemROM(fwVersion, fwBuild);
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));

            String revision = mcu.getSysSwRevision();
            if (revision.compareTo("2688") >= 0 && isAsynch(modem)) {
                long trId = gw
                        .cmdAsynchronousCall(
                                mcuId,
                                "eui64Entry",
                                "ZEUPLS",
                                modemId,
                                "cmdGetModemROM",
                                (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT
                                        .getCode()
                                        | (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE
                                                .getCode(),
                                0,
                                0,
                                2,
                                makeStringArray(new String[]{ "sensorID", modemId },
                                        new String[] {"wordEntry",""+ ModemROM.OFFSET_NODEKIND },
                                        new String[] { "wordEntry","" + modemROM.getNodeSize() } ),
                                serviceType, operator);
                // inst.addProperty(new MOPROPERTY("commandMethod",
                // "AsynchronousCall")); //TODO FIND
                // inst.addProperty(new MOPROPERTY("trId", ""+trId));//TODO
                // FIND
            } else {
                modemROM = gw.cmdGetModemROM1(mcuId, modemId,
                        makeIntArray(new int[] { ModemROM.OFFSET_NODEKIND, modemROM.getNodeSize() }));
                ModemNode sNode = modemROM.getModemNode();

                if (sNode != null) {
                    modem.setFwVer(sNode.getSoftwareVersion());
                    modem.setHwVer(sNode.getHardwareVersion());
                    modem.setNodeKind(sNode.getNodeKind());
                    modem.setProtocolVersion(sNode.getProtocolVersion());
                    modem.setResetCount(sNode.getResetCount());
                    modem.setLastResetCode(sNode.getResetReason());
                    modem.setLastLinkTime(DateTimeUtil
                            .getDateString(new Date()));
                    modem.setCommState(1);
                }
            }
        } catch (Exception ex) {
            log.error(ex, ex);
            modem.setCommState(0);
            throw ex;
        }
        modemDao.update(modem);
        return modem;
    }

    /**
     * 집중기 시간 설정
     *
     * @param mcuId
     * @throws Exception
     */
    public void timeSynchronization(String mcuId) throws Exception {
        log.info("timeSynchronization MCU[" + mcuId + "]");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            String time = TimeUtil.getCurrentTime();
            gw.cmdMcuSetTime(mcuId, time);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 집중기 리셋
     *
     * @param mcuId
     * @throws Exception
     */
    public void mcuReset(String mcuId) throws Exception {
        log.info("mcuReset MCU[" + mcuId + "]");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdMcuReset(mcuId);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param mcuId
     * @param meterId
     * @param sensorId
     * @param nOption
     * @param fromDate
     * @param toDate
     * @throws Exception
     */
    public void cmdOnRecoveryDemandMeter(String mcuId,  String modemId, 
            String nOption, int offSet, int count) throws Exception {
        log.debug("cmdOnRecoveryDemandMeter("+mcuId+"," + modemId +"," 
            + nOption + "," + offSet + "," + count + ")");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));
            gw.cmdOnRecoveryDemandMeter(mcuId, modemId, nOption, offSet, count);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 집중기 시간을 특정 시간으로 설정
     *
     * @param mcuId
     * @param time
     * @throws Exception
     */
    public void cmdMcuSetTime(String mcuId, String time)
            throws Exception {
        log.info("cmdMcuSetTime MCU[" + mcuId + "], Time[" + time + "]");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdMcuSetTime(mcuId, time);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 집중기 정보 설정(검침스케줄,업로드 스케줄 등의 정보를 설정)
     *
     * @param mcu
     * @throws Exception
     */
    public void updateMcuConfiguration(MCU mcu) throws Exception {
        log.info("updateMcuConfiguration MCU[" + mcu.getSysID() + "]");
        ArrayList<String> props = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        String mcuId = mcu.getSysID();
        MCUVar mcuVar = mcu.getMcuVar();
        DefaultConf defaultConf = DefaultConf.getInstance();

        // Communication Log
        // props.add("commLogFlag");
        // values.add(defaultConf.getMcuPropertyName(mcu.get));
        // props.add("commLogSaveDay");
        // values.add(defaultConf.getMcuPropertyName(prop.getName()));

        // Operation Log
        // props.add("operLogFlag");
        // values.add(defaultConf.getMcuPropertyName(prop.getName()));
        // props.add("operLogSaveDay");
        // values.add(defaultConf.getMcuPropertyName(prop.getName()));

        // Temperature
        // props.add("readTempInterval");
        // values.add(defaultConf.getMcuPropertyName(prop.getName()));

        // Metering Schedule
        props.add("meterDayMask");
        values.add(mcuVar.getVarMeterDayMask() + "");

        props.add("meterHourMask");
        values.add(mcuVar.getVarMeterHourMask() + "");

        // Miss Metering Schedule
        props.add("missMeterDayMask");
        values.add(mcuVar.getVarRecoveryDayMask() + "");
        props.add("missMeterHourMask");
        values.add(mcuVar.getVarRecoveryHourMask() + "");
        props.add("failReadFlag");
        values.add(mcuVar.getVarEnableRecovery() + "");

        // Metering Data Upload
        props.add("meterUploadType");
        values.add(mcuVar.getVarMeterUploadCycleType() + "");
        props.add("meterUploadCycle");
        values.add(mcuVar.getVarMeterUploadCycle() + "");
        props.add("meterUploadHour");
        values.add(mcuVar.getVarMeterUploadStartHour() + "");
        props.add("meterUploadMinute");
        values.add(mcuVar.getVarMeterUploadStartMin() + "");

        // Retry Metering Data Upload
        props.add("meterUploadRetryCnt");
        values.add(mcuVar.getVarMeterUploadRetry() + "");

        // Mobile Comm Log
        props.add("mobileUsageFlag");
        values.add(mcuVar.getVarEnableCommLog() + "");
        // Time Sync
        props.add("autoTimeSyncFlag");
        values.add(mcuVar.getVarEnableMeterTimesync() + "");
        props.add("autoTimeSyncSec");
        values.add(mcuVar.getVarAutoTimesyncIntegereval() + "");

        if (props.size() < 1) {
            return;
        }
        
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));

            gw.cmdStdSet1(mcuId, props, values);

        } catch (Exception e) {
            throw e;
        }
    }

    public String getRemoteControlHTML(String cmd, EnergyMeter moInst) {
        StringBuffer html = new StringBuffer();
        html.append("<html>");
        html
                .append("<link href=/aimir-web/css/style.css rel=stylesheet type=text/css>");
        html
                .append("<table  border=1 cellpadding=0 cellspacing=0 bordercolor=#FFFFFF border=1 width=100%>");
        html.append("<tr>");
        html.append("<td align=center class=tdhead1>ID</td>");
        html.append("<td align=center class=tdblue>").append(moInst.getMdsId())
                .append("</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td align=center class=tdhead1>Result</td>");
        html.append("<td align=center class=tdblue>").append(
                moInst.getMeterStatus().getName()).append("</td>");
        html.append("</tr>");
        if (cmd.equals("remoteGetStatus")) {
            // TODO IMPLEMENT
            /*
             * if(moInst.getPropertyValueString("message")!=null){
             * html.append("<tr>");
             * html.append("<td align=center class=tdhead1>Status</td>");
             * html.append("<td align=center class=tdblue>").append(moInst.
             * getPropertyValueString("message")).append("</td>");
             * html.append("</tr>"); }else
             * if(moInst.getPropertyValueString("activateStatus")!=null){
             * html.append("<tr>");
             * html.append("<td align=center class=tdhead1>Status</td>");
             * html.append
             * ("<td align=center class=tdblue>Activate:").append(toGEStatus
             * (moInst.getPropertyValueString("activateStatus")));
             * html.append(" Swtich:"
             * ).append(toGEStatus(moInst.getPropertyValueString
             * ("switchStatus"))).append("</td>"); html.append("</tr>"); }else{
             * html.append("<tr>");
             * html.append("<td align=center class=tdhead1>Status</td>");
             * html.append("<td align=center class=tdblue></td>");
             * html.append("</tr>"); }
             */
        } else if (cmd.equals("remotePowerOff") || cmd.equals("remotePowerOn")) {
            // TODO IMPLEMENT
            /*
             * if(moInst.getPropertyValueString("message")!=null){
             * if(moInst.getPropertyValueString("message").equals("OK")){
             * html.append("<tr>");
             * html.append("<td align=center class=tdhead1>Status</td>");
             * html.append("<td align=center class=tdblue>").append(moInst.
             * getPropertyValueString
             * ("message")).append(" [Detail:Get Status]</td>");
             * html.append("</tr>"); }else{ html.append("<tr>");
             * html.append("<td align=center class=tdhead1>Status</td>");
             * html.append("<td align=center class=tdblue>").append(moInst.
             * getPropertyValueString("message")).append("</td>");
             * html.append("</tr>"); } }else
             * if(moInst.getPropertyValueString("activateStatus")!=null){
             * html.append("<tr>");
             * html.append("<td align=center class=tdhead1>Status</td>");
             * html.append
             * ("<td align=center class=tdblue>Activate:").append(toGEStatus
             * (moInst.getPropertyValueString("activateStatus")));
             * html.append(" Swtich:"
             * ).append(toGEStatus(moInst.getPropertyValueString
             * ("switchStatus"))).append("</td>"); html.append("</tr>"); }else{
             * html.append("<tr>");
             * html.append("<td align=center class=tdhead1>Status</td>");
             * html.append("<td align=center class=tdblue></td>");
             * html.append("</tr>"); }
             */
        }

        html.append("</table>");
        html.append("</html>");
        return html.toString();
    }

    private String toGEStatus(String str) {
        if (str.equals("0")) {
            return "Off";
        } else if (str.equals("1")) {
            return "On";
        } else {
            return "Off";
        }
    }

    public String getOndemandDetailHTML(MeterData.Map data) {
        StringBuffer html = new StringBuffer();
        html.append("<html><head>");
        html.append("<style>.am_button, .am_button a, .am_button button, .am_button input, .am_button div.divbutton {background-repeat:repeat-x !important;}</style></head>");
        html.append("<form name='f' method='post' style='display:none'>");
        html.append("<textarea name='excelData'></textarea></form>");
        html.append("<link href=/aimir-web/css/style.css rel=stylesheet type=text/css>");
        html.append("<ul><span style='float:right'><li><em class='am_button' style='margin-right: 50px; '><a href='javascript:openExcelReport2();'>excel</a></em></li></span></ul>");
        html.append("<table  border=1 cellpadding=0 cellspacing=0 bordercolor=#FFFFFF border=1 width=100% id=ondemandTable>");
        html.append("<caption style='text-align: center;font-size: 20px;'><b>Ondemand Result</b></caption>");
        html.append("<tr><td>&nbsp;</td><td>&nbsp;</td><tr>");
        List<MeterData.Map.Entry> iter = data.getEntry();
        MeterData.Map.Entry entry;
        Object value;
        Object key;

        boolean isMx2 = false;
        String volAng_a = null;
        String volAng_b = null;
        String volAng_c = null;

        String curAng_a = null;
        String curAng_b = null;
        String curAng_c = null;

        for (int cnt = 0; cnt < iter.size(); cnt++) {
            //if (cnt % 2 == 0) {
                html.append("<tr>");
            //}
            entry = iter.get(cnt);
            key = entry.getKey();
            if (!"Status".equals(key) && !"LpValue".equals(key) && !"rowMap".equals(key)) {

                // if (data.get(key) instanceof String) {
                try{
                    value = entry.getValue();
                }catch(Exception e){
                    value = "";
                }
                    /*
                     * if(value.matches("^[0-9]+\\.[0-9]+$")){
                     * html.append("<td align='center'class='tdhead1'>"
                     * +key+"</td>");
                     * html.append("<td align='center'class='tdblue'>"
                     * +df2.format(Double.valueOf(value))+"</td>"); } else{
                     * html.
                     * append("<td align='center'class='tdhead1'>"+key+"</td>");
                     * html
                     * .append("<td align='center'class='tdblue'>"+value+"</td>"
                     * ); }
                     */
                html.append("<td height='auto' width='50%' align=left style=\"word-break:break-all\"><b>").append(key).append("</b></td>");
                html.append("<td height='auto' align=left style=\"word-break:break-all\">").append(value).append("</td>");
                // }

                if ("Model".equals(key) && "Mitsubishi-MX2".equals((String)value)) {
                    isMx2 = true;
                } else if ("Voltage Angle(A)".equals(key)) {
                    volAng_a = (String)value;
                } else if ("Voltage Angle(B)".equals(key)) {
                    volAng_b = (String)value;
                } else if ("Voltage Angle(C)".equals(key)) {
                    volAng_c = (String)value;
                } else if ("Current Angle(A)".equals(key)) {
                    curAng_a = (String)value;
                } else if ("Current Angle(B)".equals(key)) {
                    curAng_b = (String)value;
                } else if ("Current Angle(C)".equals(key)) {
                    curAng_c = (String)value;
                }
            }
            //if (cnt % 2 == 1) {
            html.append("</tr>");
            //}
        }

        html.append("</table>");

        if (isMx2 && (!StringUtil.nullToBlank(volAng_a).isEmpty() || !StringUtil.nullToBlank(volAng_b).isEmpty() || !StringUtil.nullToBlank(volAng_c).isEmpty() 
                || !StringUtil.nullToBlank(curAng_a).isEmpty() || !StringUtil.nullToBlank(curAng_b).isEmpty() || !StringUtil.nullToBlank(curAng_c).isEmpty())) {
            html.append("<table id='phasorDiagramTbl' style='display:none; width:100%;'>");
            html.append("<tr>");
            html.append("<td height='auto' style='width:49%; vertical-align:middle; text-align:center;' id='phasorDiagram'></td>");
            html.append("<td height='auto' style='width:49%; vertical-align:middle;'><span id='angleGrid'></span></td>");
            html.append("</tr>");
            html.append("</table>");
            html.append("<input type='hidden' id='isMx2' value='true' style='display:none;'/>");
            html.append("<input type='hidden' id='volAng_a' value='").append(StringUtil.nullToBlank(volAng_a)).append("' style='display:none;'/>");
            html.append("<input type='hidden' id='volAng_b' value='").append(StringUtil.nullToBlank(volAng_b)).append("' style='display:none;'/>");
            html.append("<input type='hidden' id='volAng_c' value='").append(StringUtil.nullToBlank(volAng_c)).append("' style='display:none;'/>");
            html.append("<input type='hidden' id='curAng_a' value='").append(StringUtil.nullToBlank(curAng_a)).append("' style='display:none;'/>");
            html.append("<input type='hidden' id='curAng_b' value='").append(StringUtil.nullToBlank(curAng_b)).append("' style='display:none;'/>");
            html.append("<input type='hidden' id='curAng_c' value='").append(StringUtil.nullToBlank(curAng_c)).append("' style='display:none;'/>");
        }

        html.append("</html>");
        return html.toString();
    }

    public String getOndemandDetailHTML2(HashMap<String, String> data) {
        StringBuffer html = new StringBuffer();
        html.append("<html>");
        html
                .append("<link href=/aimir-web/css/style.css rel=stylesheet type=text/css>");
        html
                .append("<table  border=1 cellpadding=0 cellspacing=0 bordercolor=#FFFFFF border=1 width=100%>");
        Iterator<String> iter = data.keySet().iterator();
        String key;
        String value;
        while (iter.hasNext()) {
            key = iter.next();
            if("rowMap".equals(key)) {
                continue;
            }
            value = String.valueOf(data.get(key));
            if (key.startsWith("TITLE")) {
                html.append("<tr>");
                html
                        .append("<td align='center' style=\"word-break:break-all\" colspan=2>"
                                + value + "</td>");
                html.append("</tr>");
            } else {
                html.append("<tr>");
                if (!(key).equals("Status")) {
                    html
                            .append("<td align='left' style=\"word-break:break-all\" width='50%'>"
                                    + key
                                    + "</td><td align='right' style=\"word-break:break-all\" width='50%'>"
                                    + value + "</td>");
                } else {
                    continue;
                }
                html.append("</tr>");
            }
        }
        html.append("</table>");
        html.append("</html>");
        return html.toString();
    }

    /**
     * 미터 타입에 상관없이 미터 온디맨드 수행
     *
     * @param meter
     * @param serviceType
     * @param operator
     * @param nOption
     *            - 데이터 옵션(GE미터인 경우 옵션필요)
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    
    @SuppressWarnings("unchecked")
    public Map doOnDemand(Meter meter, int serviceType, String operator,
            String nOption, String fromDate, String toDate) throws Exception {
        log.info("doOnDemand meter[" + meter.getMdsId() + "]");
        String sensorInstanceName = null;
        Map result = new HashMap();
        DataConfiguration config = null;
        try {
            config = new DataConfiguration(new PropertiesConfiguration("command.properties"));
        } catch (ConfigurationException e) {
            try {
                config = new DataConfiguration(new PropertiesConfiguration("config/command.properties"));
            }
            catch (ConfigurationException ee) {
                log.error(e, e);
            }
        }
        
        fromDate = (fromDate == null || "".equals(fromDate)) ? TimeUtil.getCurrentDay() : fromDate;
        toDate = (toDate == null || "".equals(toDate)) ? TimeUtil.getCurrentDay() : toDate;

        try {
            CommandWS gw = null;

            Modem modem = meter.getModem();
            String meterId = meter.getMdsId();
            String modemId = modem.getId().toString();
            boolean isAsync = false;
            long trId = 0;

            String mcuId = null;
            if (modem.getMcu() != null)
                mcuId = modem.getMcu().getSysID();
            
            
            ModemType modemType = ModemType.Unknown;
            if (modem != null) {
                modemType = modem.getModemType();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            int days = (int) ((formatter.parse(toDate.substring(0, 8)).getTime() - formatter.parse(fromDate.substring(0, 8)).getTime())/1000/3600/24 + 1);
            
            int meterHandshakeTimeout = Integer.valueOf((String) config.getProperty("meter.timeout.handshaking"))*1000;
            int meterDayTimeout = Integer.valueOf((String) config.getProperty("meter.timeout.day"))*1000;
            int LPinterval = meter.getLpInterval();
            String timeout = String.valueOf(meterHandshakeTimeout + (meterDayTimeout * (60 / LPinterval) * days));

            MeterData emd = null;

//            if (modemType == ModemType.MMIU || modemType == ModemType.IEIU || modemType== ModemType.Converter)
          if (modemType == ModemType.MMIU || modemType == ModemType.IEIU || modemType== ModemType.Converter_Ethernet || modemType == ModemType.SubGiga){
                gw = CmdManager.getCommandWS(meter.getModem().getProtocolType(), timeout);
                //if (Protocol.SMS.equals(meter.getModem().getProtocolType())) {
                	String nameSpace = null;
        			if  ((modem.getModemType() == ModemType.MMIU)) { 
        				MMIU mmiuModem = mmiuDao.get(modem.getId());
        				nameSpace = mmiuModem.getNameSpace();
        			}
        			else if ((modem.getModemType() == ModemType.IEIU)) { 
        				IEIU ieiuModem = ieiuDao.get(modem.getId());
        				nameSpace = ieiuModem.getNameSpace();
        			}
        			else if ((modem.getModemType() == ModemType.Converter_Ethernet)) { 
        				Converter converterModem = converterDao.get(modem.getId());
        				nameSpace = converterModem.getNameSpace();
        			} else if ((modem.getModemType() == ModemType.SubGiga)) {
        				nameSpace = modem.getNameSpace();
        			}
            		if ( nameSpace != null && nameSpace.equals("SP")) {
                        emd = gw.cmdOnDemandMeterByPass(mcuId, meterId, meter.getModem().getDeviceSerial(), "", fromDate, toDate);	
            		}
            		else {
	                    String[] parameter = null;
	                    int seq = new Random().nextInt(100) & 0xFF;
	
	                    if (nOption.equals("F")) {
	                        parameter = new String[2];
	                        parameter[0] = seq + "";
	                        parameter[1] = nOption; // foreground
	                        List<String> params = new ArrayList<String>();
	                        params.add(seq+"");
	                        params.add(nOption);
	                        List<String> ret = gw.cmdOnDemandMeter3(mcuId, meterId,
	                                modemId, "CM01", params);
	                        if (ret != null && ret.size() >= 2) {
	                            log.debug("LpValue=" + ret.get(1));
	                            // map.put("meter Id",meterId);
	                            // map.put("modem Id",mcuId);
	                            // map.put("Metering Time", ret[0]);
	                            // map.put("Total Usage", ret[1]);
	                            // map.put("LpValue", new Double(ret[1]));
	                        }
	
	                    } else {
	                        parameter = new String[4];
	                        List<String> params = new ArrayList<String>();
	                        params.add(seq+"");
	                        params.add(nOption);
	                        params.add(fromDate);
	                        params.add(toDate);
	                        gw.cmdOnDemandMeterAsync(mcuId, meterId, modemId, "CM01", params);
	                    }
            		}
                //} else {
                //    emd = gw.cmdOnDemandMeter2(mcuId, meterId, meter.getModem().getDeviceSerial(), "0", fromDate, toDate);
                //}
                //else {
                //    log.debug("fromDate:" + fromDate + " toDate:" + toDate);
                //    gw.cmdOnDemandMeter(meterId, fromDate, toDate);
                //}

            }
            // MBus
            else if (modemType == ModemType.ZEU_MBus) {
                gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null), timeout);
                
                String nPort = meter.getModemPort() + "";
                emd = gw.cmdOnDemandMBus(mcuId, meterId, modemId, nPort, nOption, fromDate, toDate);
            }
            else {
                modem = meter.getModem();

                modemId = modem.getDeviceSerial();
                
                gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null), timeout);
                // if (revision.compareTo("2688") >= 0 && isAsynch(modem)) {
                if (isAsynch(modem)) {
                    isAsync = true;
                    int[] offsetCount = convertOffsetCount(meter, "", "");
                    List<StringArray> params = new ArrayList<StringArray>();
                    StringArray sa = new StringArray();
                    sa.getItem().add("sensorID");
                    sa.getItem().add(modemId);
                    params.add(sa);
                    sa = new StringArray();
                    sa.getItem().add("intEntry");
                    sa.getItem().add("0");
                    params.add(sa);
                    sa = new StringArray();
                    sa.getItem().add("intEntry");
                    sa.getItem().add(""+offsetCount[0]);
                    params.add(sa);
                    sa = new StringArray();
                    sa.getItem().add("intEntry");
                    sa.getItem().add(""+offsetCount[1]);
                    params.add(sa);
                    
                    trId = gw.cmdAsynchronousCall(
                                    mcuId,
                                    "eui64Entry",
                                    modem.getModemType().name(),
                                    modemId,
                                    "cmdOnDemandMeter",
                                    (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT
                                            .getCode()
                                            | (byte) TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE
                                                    .getCode(),
                                    0,
                                    0,
                                    2,
                                    params,
                                    serviceType, operator);
                } else {
                    sensorInstanceName = modem.getDeviceSerial();
                    emd = gw.cmdOnDemandMeter2(mcuId, meterId, modemId, nOption, fromDate, toDate);
                }
            }

            if (!isAsync) {
                if(emd != null && emd.getMap() != null){
                    String detailInfo = getOndemandDetailHTML(emd.getMap());
//                    log.info("detailInfo[" + detailInfo + "]");
                    result.put("detail", detailInfo);
                    result.put("rawMap", emd.getMap());
                    modem.setCommState(1);
                }
            } else {
                result.put("commandMethod", "AsynchronousCall");
                result.put("transactionId", "" + trId);
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
            result.put("rawMap", null);
            log.error(ex, ex);
        }
        
        return result;
    }

    /**
     * 온디맨드 요청시 시작일,종료일에 대한 날짜를 오프셋으로 변환하여 계산
     *
     * @param meter
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */

    @SuppressWarnings("unchecked")
    public Map cmdDistributionMMIU(Meter meter, String toFile)
            throws Exception {
        log.info("doOnDemand meter[" + meter.getMdsId() + "]");
        Map result = new HashMap();

        try {
            Modem modem = meter.getModem();
            String modemId = modem.getDeviceSerial();
            String meterId = meter.getMdsId();
            
            CommandWS gw = CmdManager.getCommandWS(modem.getProtocolType());

            String mcuId = "";

            List<String> params = new ArrayList<String>();
            int seq = new Random().nextInt(100) & 0xFF;
 
            params.add(seq+"");//sequence
            params.add("F");//type
            params.add("21");//port
            params.add("aimir");//id
            params.add("roqkf2010aimir");//pass
            params.add(toFile.substring(0, toFile.lastIndexOf('.'))); //file path 파일 확장자를 제거해 준다.
            gw.cmdOnDemandMeterAsync(mcuId, meterId, modemId,RequestFrame.CMD_DOTA,params);
            // modem=meter 조인해서..

            result.put("result", "Success");
        } catch (Exception ex) {
            result.put("err", "err");
            log.error(ex, ex);
        }
        return result;
    }

    public int[] convertOffsetCount(Meter meter, String fromDate,
            String toDate) throws Exception {
        int nOffset = 0;
        int nCount = 0;
        ModemType modemType = meter.getModem().getModemType();

        if (fromDate.length() > 0 && toDate.length() > 0) {
            if (modemType != ModemType.ZEUPLS
                    || (fromDate != null && fromDate.length() >= 8)) {// all
                // type
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                Calendar today = Calendar.getInstance();
                Calendar from = Calendar.getInstance();
                Calendar to = Calendar.getInstance();
                from.setTime(sdf.parse(fromDate.substring(0, 8)));
                to.setTime(sdf.parse(toDate.substring(0, 8)));

                if (today.before(to)) {
                    throw new Exception("request toDate[" + toDate
                            + "] must not be after today");
                }

                if (!today.after(from)) {
                    throw new Exception("request fromDate[" + fromDate
                            + "] must be before today");
                }

                while (today.after(from)) {
                    from.add(Calendar.DAY_OF_YEAR, 1);
                    nOffset++;
                }

                from.setTime(sdf.parse(fromDate));

                while (today.after(to)) {
                    to.add(Calendar.DAY_OF_YEAR, 1);
                    nCount++;
                }
                nCount = nOffset - nCount + 1;
            } else {// zeupls
                nOffset = Integer.parseInt(fromDate);
                nCount = Integer.parseInt(toDate);
            }
        } else {
            if (modemType != ModemType.ZEUPLS) {// all type
                nOffset = 1;
                nCount = 1;
            } else {
                nOffset = Integer.parseInt(DateTimeUtil
                        .getCurrentDateTimeByFormat("yyyyMMdd"));
                nCount = nOffset;
            }
        }

        DeviceModel model = meter.getModel();
        // TODO 벤더, 모델 처리 방안 필요함.
        if (model.getDeviceVendor().getName().contains("GE")
                && model.getName().contains("I210")) {
            int interval = meter.getLpInterval();
            if (interval == 0) {
                nOffset *= 48;
                nCount = 0;
            } else {
                nOffset *= (60 / interval * 24);
                nCount *= (60 / interval * 24);
            }
        }
        log.debug("nOffset[" + nOffset + "] nCount[" + nCount + "]");
        return new int[] { nOffset, nCount };
    }

    /*
     * public boolean getSavedMeteringDataInMCU(String mcuId, String
     * meterId, String fromTime, String toTime) { boolean result = false; try {
     * CommandGWMBean gw = getCommandGW(); EnergyMeterData[] emds =
     * gw.cmdGetMeter(mcuId, meterId,fromTime,toTime); if(emds.length < 1) {
     * return false; }
     *
     * EMUtil.getEMHM().saveGetMeterData(mcuId,meterId,emds);
     *
     * result = true; } catch(Exception ex) { log.error(ex,ex); }
     *
     * return result; }
     */

    /*
     * public boolean getSavedMeteringDataInMCU1(String mcuId, String
     * meterId, String fromTime, String toTime) { boolean result = false; try {
     * int retry = 10; int optiondefault = 0; int option2 = 16; int savePeriod =
     * 30; int savePerioddefault = 30; int savePeriod2 = 40; int option = 0;
     *
     * Calendar toCal = Calendar.getInstance(); Calendar tempCal =
     * Calendar.getInstance();
     *
     * Meter meter = meterDao.get(meterId);
     *
     * String metervendor = meter.getPropertyValueString("vendor");
     *
     * toCal.setTime(DateTimeUtil.getDateFromYYYYMMDD(toTime));
     * tempCal.setTime(DateTimeUtil.getDateFromYYYYMMDD(toTime));
     *
     * if(metervendor.equals("2")){ option = option2; savePeriod = savePeriod2;
     * }
     *
     * long diffDate = 0;
     *
     * diffDate= ((DateTimeUtil.getDateFromYYYYMMDD(toTime).getTime() -
     * DateTimeUtil.getDateFromYYYYMMDD(fromTime).getTime()) / 86400000)+1;
     *
     * while (diffDate - Long.valueOf(savePeriod) > 0) {
     * tempCal.add(Calendar.DAY_OF_YEAR, -Integer.valueOf(savePeriod));
     * cmdGetMeterSchedule(meterId, option,
     * DateTimeUtil.getDateString(tempCal.getTime()).substring(0, 8),
     * DateTimeUtil.getDateString(toCal.getTime()).substring(0, 8));
     * toCal.add(Calendar.DAY_OF_YEAR, -Integer.valueOf(savePeriod)); diffDate =
     * diffDate - Long.valueOf(savePeriod); }
     *
     * String stringresult = cmdGetMeterSchedule(meterId, option, fromTime,
     * DateTimeUtil.getDateString(tempCal.getTime()).substring(0, 8));
     *
     * if(stringresult.equals("Success")) result = true; else result = false; }
     * catch(Exception ex) { log.error(ex,ex); }
     *
     * return result; }
     */

    private String cmdGetMeterSchedule(String meterId, int option,
            String fromDate, String toDate) {

        try {
            log.debug("\n\nRun Requeset recollect LP by schedule, meterId=["
                    + meterId + "]");
            Meter meter = meterDao.get(meterId);
            
            CommandWS gw = CmdManager.getCommandWS(meter.getModem().getMcu().getProtocolType().getName());
            // MIUtil miu = MIUtil.getInstance();
            // UserData user = new UserData();
            // MOINSTANCE[] users = miu.getLocalInterface()
            // .EnumerateInstances(AimirModel.MI_PERSON_USER,
            // "id ='mtr_admin'", null, 0, true);
            // user.setInstanceName(users[0].getName());
            // user.setId(users[0].getPropertyValueString("id"));
            // user.setUserId(users[0].getPropertyValueString("userId"));
            // user.setUserName(users[0].getPropertyValueString("name"));
            // user.setServiceId(CUtil.parseInt(users[0].getPropertyValueString("serviceType")));

            Calendar cal = Calendar.getInstance();

            int vendor = meter.getModel().getDeviceVendor().getCode();
            MeterVendor meterVendor = CommonConstants.getMeterVendor(vendor);
            if (meterVendor == MeterVendor.LSIS) {
                toDate = DateTimeUtil.getDateString(cal.getTime()).substring(0,
                        8);
            }

            if (toDate == null || toDate.length() == 0)
                toDate = DateTimeUtil.getDateString(cal.getTime()).substring(0,
                        8);
            cal.add(Calendar.DATE, -1);
            if (fromDate == null || fromDate.length() == 0)
                fromDate = DateTimeUtil.getDateString(cal.getTime()).substring(
                        0, 8);
            int nOffset = 0;
            int nCount = 0;
            if (fromDate.length() > 0 && toDate.length() > 0) {
                Calendar today = Calendar.getInstance();
                Calendar from = Calendar.getInstance();
                Calendar to = Calendar.getInstance();
                from.setTime(DateTimeUtil.getDateFromYYYYMMDD(fromDate));
                to.setTime(DateTimeUtil.getDateFromYYYYMMDD(toDate));

                if (today.before(to))
                    throw new Exception("request toDate[" + toDate
                            + "] must not be after today");

                if (!today.after(from))
                    throw new Exception("request fromDate[" + fromDate
                            + "] must be before today");

                while (today.after(from)) {
                    from.add(Calendar.DAY_OF_YEAR, 1);
                    nOffset++;
                }

                from.setTime(DateTimeUtil.getDateFromYYYYMMDD(fromDate));

                while (today.after(to)) {
                    to.add(Calendar.DAY_OF_YEAR, 1);
                    nCount++;
                }
                nCount = nOffset - nCount + 1;
            }

            // target.setMeterInstance(meter.getName());
            if (meterVendor == MeterVendor.GE) {
                Integer interval = meter.getLpInterval();
                if (interval == null || 0 == interval) {
                    nOffset *= 48;
                    nCount = 0;
                } else {
                    nOffset *= (60 / interval * 24);
                    nCount *= (60 / interval * 24);
                }
            }

            Object o = null;
            String errmsg = "";

            String modemId = meter.getModem().getDeviceSerial();
            String mcuId = meter.getModem().getMcu().getSysID();
            log.debug("Param McuId[" + mcuId + "] meterId[" + meterId
                    + "] sensorid[" + modemId + "] fromDate[" + fromDate
                    + "] toDate[" + toDate + "] nOffset[" + nOffset
                    + "] nCount[" + nCount + "]");
            if (modemId == null || modemId.length() == 0) {
                throw new Exception("meterId=[" + meterId
                        + "] SensorId is not found");
            } else if (mcuId == null || mcuId.length() == 0) {
                throw new Exception("meterId=[" + meterId
                        + "] McuId is not found");
            } else {
                o = gw.cmdGetMeterSchedule(mcuId, modemId, option, nOffset,
                        nCount);
                if (o instanceof MeterData) {
                    // TODO IMPLEMENT
                    /*
                     * EMHistoryMgr hdm = EMUtil.getEMHM();
                     * hdm.saveGetMeterData(mcuId,meterId,new
                     * MeterData[]{(MeterData)o});
                     */
                }
            }
            errmsg = "Success";

        } catch (Exception e) {
            log.error(e);
            return "Fail";
        }
        return "Success";
    }

    /**
     * 집중기 상태 진단
     *
     * @param mcuId
     * @return
     * @throws Exception
     */
    public Hashtable getMCUDiagnosis(String mcuId)
            throws Exception {
        Hashtable props = new Hashtable();
        boolean exist = false;

        try {
            MCU mcu = mcuDao.get(mcuId);
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            ResponseMap rmap = gw.cmdMcuDiagnosis(mcuId); 

            for (ResponseMap.Response.Entry e : rmap.getResponse().getEntry()) {
                props.put(e.getKey(), (String)e.getValue());
                
                if (props.contains("mcuState")) {
                    props.get("mcuState");
                }
                if (props.contains("sinkState")) {
                    props.get("sinkState");
                }
                if (props.contains("powerState")) {
                    props.get("powerState");
                }
                if (props.contains("batteryState")) {
                    props.get("batteryState");
                }
                if (props.contains("temperatureState")) {
                    props.get("temperatureState");
                }
                if (props.contains("memoryState")) {
                    props.get("memoryState");
                }
                if (props.contains("flashState")) {
                    props.get("flashState");
                }
                if (props.contains("gsmState")) {
                    props.get("gsmState");
                }
                if (props.contains("ethernetState")) {
                    props.get("ethernetState");
                }
            }

            // mcu.setNetworkStatus(1);
        } catch (Exception ex) {
            log.error(ex, ex);
            throw ex;
            // mcu.setNetworkStatus(0);
        }

        /*
         * if(exist){ mcuDao.add(mcu); }else{ mcuDao.update(mcu); }
         */

        return props;
    }

    // TODO IMPLEMENT
    /*
     * public Object getRelayStatus(String mcuId, int groupId, int
     * memberId) throws Exception {
     * log.info("Energy Meter's Relay Switch Status Get ["+mcuId+"]");
     * CommandGWMBean gw = getCommandGW(); Object res = gw.getRelaySwitchStatus(
     * mcuId, groupId, memberId ); return res; }
     *
     * public Object getRelayStatus(String mcuId,String modemId) throws
     * Exception { log.info("Energy Meter's Relay Switch Status Get ["+mcuId+","
     * + modemId + "]"); CommandGWMBean gw = getCommandGW(); Object res =
     * gw.getRelaySwitchStatus( mcuId, modemId ); return res; }
     *
     *
     * public Object cmdRelaySwitchAndActivate(String mcuId, int groupId,
     * int memberId, String cmdNum) throws Exception {
     * log.info("Energy Meter's Relay Switch And Activate On/Off ["+mcuId+"]");
     * CommandGWMBean gw = getCommandGW(); Object res =
     * gw.cmdRelaySwitchAndActivate( mcuId, groupId, memberId, cmdNum ); return
     * res; }
     *
     * public Object cmdRelaySwitchAndActivate(String mcuId, String
     * meterId, String cmdNum) throws Exception {
     * log.info("Energy Meter's Relay Switch And Activate On/Off ["
     * +mcuId+","+meterId+"]"); CommandGWMBean gw = getCommandGW(); Object res =
     * gw.cmdRelaySwitchAndActivate( mcuId, meterId, cmdNum ); return res; }
     *
     *
     * public void cmdReadTableAll(String mcuId, int nOption, String
     * fromDate, String toDate) throws Exception {
     * log.info("Energy Meter's get all table["
     * +mcuId+","+nOption+","+fromDate+","+toDate+"]"); CommandGWMBean gw =
     * getCommandGW(); gw.cmdReadTableAll(mcuId, nOption, fromDate, toDate); }
     *
     * public void cmdReadTableAll(String mcuId, int nOption, int
     * nOffset, int nCount) throws Exception {
     * log.info("Energy Meter's get all table["
     * +mcuId+","+nOption+","+nOffset+","+nCount+"]"); CommandGWMBean gw =
     * getCommandGW(); gw.cmdReadTableAll(mcuId, nOption, nOffset, nCount); }
     *
     * public void cmdGetAllTable(String mcuId) throws Exception {
     * log.info("Energy Meter's get all table["+mcuId+"]"); CommandGWMBean gw =
     * getCommandGW(); gw.cmdGetAllTable(mcuId); }
     */

    public Object cmdGetMeterSchedule(String mcuId, String modemId,
            int nOption, int nOffset, int nCount) throws Exception {
        log.info("Meter's schedule[" + mcuId + "," + modemId + "," + nOption
                + "," + nOffset + "," + nCount + "]");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));
            Object res = gw.cmdGetMeterSchedule(mcuId, modemId, nOption,
                    nOffset, nCount);
            return res;
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * 집중기 DST 설정
     *
     * @param mcuId
     * @param fileName
     * @throws Exception
     */
    public void cmdMcuSetDST(String mcuId, String fileName)
            throws Exception {
        log.info("Set MCU DST File[" + mcuId + "]");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdMcuSetDST(mcuId, fileName);
        } catch (Exception e) {
            throw e;
        }
    }

    public long cmdMcuSetGMT(String mcuId) throws Exception {
        long result = 0;
        log.info("Set MCU GMT Time [" + mcuId + "]");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            result = gw.cmdMcuSetGMT(mcuId);
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    public void cmdMcuSetConfiguration(String mcuId) throws Exception {
        String fileName = "/tmp/config.tar.gz";
        log.info("Set MCU Configuration [" + mcuId + "]");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdPutFile(mcuId, fileName);
            gw.cmdSetConfiguration(mcuId);
        } catch (Exception e) {
            throw e;
        }
    }

    public Map<String, String> cmdMcuGetConfiguration(String mcuId)
            throws Exception {
        log.info("Get MCU Configuration [" + mcuId + "]");
        String ddir = ScheduleProperty.getProperty("command.download.dir");
        if (ddir == null || ddir.length() == 0) {
            ddir = "/home/aimir/mcu/download";
        }

        List<String> res = null;

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));

            res = gw.cmdGetConfiguration(mcuId);

            Map<String, String> map = new LinkedHashMap<String, String>();
            if (res == null || res.size() < 2) {
                throw new Exception("Get MCU Configuration data empty!");
            } else {
                String fileName = res.get(0);

                String len = res.get(1);
                gw.cmdGetFile(mcuId, fileName);
                map.put("File Name", ddir + "/" + mcuId.substring(9) + "."
                        + res.get(0).substring(5));
                map.put("File Size", len + "(bytes)");
            }

            return map;
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * 미터 시간 동기화 명령 - 집중기에 연결하여 수행
     *
     * @param mcuId
     * @param meterId
     * @return
     * @throws Exception
     */
    public String[] cmdMeterTimeSync(String mcuId, String meterId)
            throws Exception {
        List<String> result = null;
        log.info("Energy Meter's sync meter time[" + mcuId + "," + meterId
                + "]");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null));

            result = gw.cmdMeterTimeSync(mcuId, meterId);

        } catch (Exception e) {
            throw e;
        } 

        return result.toArray(new String[0]);
    }
    
    public Map<String, String> cmdMeterTimeSyncByGtype(String mcuId, String meterId) throws Exception {
        log.info("cmdMeterTimeSyncByGtype[" + mcuId + "," + meterId + "]");
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null));

            ResponseMap rmap = gw.cmdMeterTimeSyncByGtype(mcuId, meterId);
            
            Map<String, String> res = new HashMap<String, String>();

            for (ResponseMap.Response.Entry e : rmap.getResponse().getEntry().toArray(new ResponseMap.Response.Entry[0])) {
                res.put((String)e.getKey(), (String)e.getValue());
            }
            return res;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 그룹별 비동기 명령 수행
     *
     * @param mcuId
     * @param groupKey
     * @param command
     * @param option
     * @param day
     * @param nice
     * @param ntry
     * @return
     * @throws Exception
     */
    public long cmdGroupAsyncCall(String mcuId, int groupKey,
            String command, int option, int day, int nice, int ntry)
            throws Exception {

        List<SMIValue> param = new ArrayList<SMIValue>();
        log.info("cmdGroupAsyncCall [" + mcuId + "]," + groupKey + ",command="
                + command);
        if (command.equals("cmdGetSensorROM")) {
            param.add(DataUtil.getSMIValueByObject("wordEntry", String
                    .valueOf(ModemROM.OFFSET_NODEKIND)));
            param.add(DataUtil.getSMIValueByObject("wordEntry", 39 + ""));
        }
        /*
         * new int[][] {{ModemROM.OFFSET_NODEKIND, modemROM.getNodeSize()}}
         */
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            return gw.cmdGroupAsyncCall(mcuId, groupKey, command, option, day,
                    nice, ntry, param);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 그룹 추가 명령어
     * @param mcuId
     * @param groupName
     * @throws Exception
     * @return GroupKey
     */
    public int cmdGroupAdd(String mcuId, String groupName)
            throws Exception {

        log.info("cmdGroupAdd [" + mcuId + "]," + groupName);

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            return gw.cmdGroupAdd(mcuId, groupName);
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * Type을 지원하는 Group을 추가/갱신
     * 만약 해당 Type, Name의 Group이 없다면 신규 생성되고 있을 경우 Sensor ID가 추가
     * @param mcuId
     * @param groupType
     * @param groupName
     * @param modemId
     * @throws Exception
     */
    public void cmdUpdateGroup(String mcuId, String groupType, String groupName, String[] modemId)
            throws Exception {

        log.info("cmdGroupAdd [" + mcuId + "]," + groupName);

        try{
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            List<String> modemList = new ArrayList<String>();
            for (String s : modemId) {
                modemList.add(s);
            }
            gw.cmdUpdateGroup(mcuId, groupType, groupName, modemList);
        } catch (Exception e) {
            throw e;
        }
    }  
    
    /**
     * 지정된 ID를 Group정보에서 삭제
     * 만약 ID를 삭제 후 해당 Group의 Member가 더 이상 없을 경우 Group 정보도 삭제
     * @param mcuId
     * @param groupType
     * @param groupName
     * @param modemId
     * @throws Exception
     */
       public void cmdDeleteGroup(String mcuId, String groupType, String groupName, String[] modemId)
            throws Exception {

        log.info("cmdGroupAdd [" + mcuId + "]," + groupName);

        try{
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            List<String> modemList = new ArrayList<String>();
            for (String s : modemId) {
                modemList.add(s);
            }
            gw.cmdDeleteGroup(mcuId, groupType, groupName, modemList);
        } catch (Exception e) {
            throw e;
        }
    }
    
    public void cmdGroupAddMember(String mcuId, int groupKey,
            String modemId) throws Exception {

        log.info("cmdGroupAddMember [" + mcuId + "]," + groupKey);

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdGroupAddMember(mcuId, groupKey, modemId);
        } catch (Exception e) {
            throw e;
        }
    }

    public void cmdGroupDelete(String mcuId, int groupKey)
            throws Exception {

        log.info("cmdGroupDelete [" + mcuId + "]," + groupKey);

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdGroupDelete(mcuId, groupKey);
        } catch (Exception e) {
            throw e;
        }
    }

    public void cmdGroupDeleteMember(String mcuId, int groupKey,
            String modemId) throws Exception {

        log.info("cmdGroupDeleteMember [" + mcuId + "]," + groupKey);

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));
            gw.cmdGroupDeleteMember(mcuId, groupKey, modemId);
        } catch (Exception e) {
            throw e;
        }
    }

    public GroupInfo[] cmdGroupInfo(String mcuId) throws Exception {

        log.info("cmdGroupInfo [" + mcuId + "]");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            return gw.cmdGroupInfo(mcuId).toArray(new GroupInfo[0]);
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }

    public GroupInfo[] cmdGroupInfo(String mcuId, int groupKey)
            throws Exception {

        log.info("cmdGroupInfo [" + mcuId + "]," + groupKey);

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            return gw.cmdGroupInfo1(mcuId, groupKey).toArray(new GroupInfo[0]);
        } catch (Exception e) {
            throw e;
        }
    }

    public GroupInfo[] cmdGroupInfoByModem(String mcuId, String modemId)
            throws Exception {

        log.info("cmdGroupInfoBy Modem [" + mcuId + "]," + modemId);

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));
            return gw.cmdGroupInfo2(mcuId, modemId, true).toArray(new GroupInfo[0]);
        } catch (Exception e) {
            throw e;
        }
    }
    
    public List<GroupTypeInfo> cmdGetGroup(String mcuId, String groupType, String groupName, String modemId)
            throws Exception {

        log.info("cmdGetGroup Modem [" + mcuId + "]," + modemId);

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));
            return gw.cmdGetGroup(mcuId, groupType, groupName, modemId);
        } catch (Exception e) {
            throw e;
        }
    }

    public void cmdFirmwareUpdate(String mcuId) throws Exception {

        log.info("cmdFirmwareUpdate[" + mcuId + "]");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdFirmwareUpdate(mcuId);
        } catch (Exception e) {
            throw e;
        }
    }

    public void cmdInstallFile(String mcuId, String filename, int type,
            String reservationTime) throws Exception {

        log.info("cmdInstallFile[" + mcuId + "]");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdInstallFile1(mcuId, filename, type, reservationTime);
        } catch (Exception e) {
            throw e;
        }
    }

    public void cmdUpdateModemFirmware(String mcuId, String modemId,
            String fileName) throws Exception {

        log.info("cmdUpdateModemFirmware[" + mcuId + "]");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));
            gw.cmdUpdateModemFirmware(mcuId, modemId, fileName);
        } catch (Exception e) {
            throw e;
        }
    }

    @Deprecated
    public void cmdPackageDistribution(String mcuId, int equipType,
            String triggerId, String oldHwVersion, String oldSwVersion,
            String oldBuildNumber, String newHwVersion, String newSwVersion,
            String newBuildNumber, String binaryMD5, String binaryUrl,
            String diffMD5, String diffUrl, String[] equipList, int otaType,
            int modemType, String modemTypeStr, int dataType, int otaLevel,
            int otaRetry) throws Exception {

        log.info("cmdPackageDistribution[" + mcuId + "]");

        try {
            List<String> _equipList = new ArrayList<String>();
            for (String equip : equipList) {
                _equipList.add(equip);
            }
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdPackageDistribution(mcuId, equipType, triggerId,
                    oldHwVersion, oldSwVersion, oldBuildNumber, newHwVersion,
                    newSwVersion, newBuildNumber, binaryMD5, binaryUrl,
                    diffMD5, diffUrl, _equipList, otaType, modemType,
                    modemTypeStr, dataType, otaLevel, otaRetry);
        } catch (Exception e) {
            throw e;
        }
    }

    public void cmdDistribution(String mcuId, String triggerId,
            int equipKind, String model, int transferType, int otaStep,
            int multiWriteCount, int maxRetryCount, int otaThreadCount,
            int installType, int oldHwVersion, int oldFwVersion, int oldBuild,
            int newHwVersion, int newFwVersion, int newBuild, String binaryURL,
            String binaryMD5, String diffURL, String diffMD5, List equipIdList)
            throws Exception {

        log.info("cmdDistribution[" + mcuId + "]");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdDistribution(mcuId, triggerId, equipKind, model,
                    transferType, otaStep, multiWriteCount, maxRetryCount,
                    otaThreadCount, installType, oldHwVersion, oldFwVersion,
                    oldBuild, newHwVersion, newFwVersion, newBuild, binaryURL,
                    binaryMD5, diffURL, diffMD5, equipIdList);
        } catch (Exception e) {
            throw e;
        }
    }
    
    public List cmdDistributionState(String mcuId, String triggerId)
            throws Exception {

        log.info("cmdDistributionState [" + mcuId + "]," + triggerId);

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            return gw.cmdDistributionState(mcuId, triggerId);
        } catch (Exception e) {
            throw e;
        }
    }

    public void cmdDistributionCancel(String mcuId, String triggerId)
            throws Exception {

        log.info("cmdDistributionCancel [" + mcuId + "]," + triggerId);

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdDistributionCancel(mcuId, triggerId);
        } catch (Exception e) {
            throw e;
        }
    }

    public void findMobilePort(String mcuId) throws Exception {
        log.debug("update mobile port [ mcu=" + mcuId + "]");

        MIBUtil mibUtil = MIBUtil.getInstance();

        // TODO IMPLEMENT
        /*
         * // MOPROPERTY[] props = IUtil.getMOPropertyHasOid("MCUMobilePort");
         * MOPROPERTY[] props = new MOPROPERTY[8]; props[0] = new
         * MOPROPERTY("mobileID", "");
         * props[0].setObjectId(mibUtil.getOid("mobileID").getValue()); props[1]
         * = new MOPROPERTY("mobileIpaddr", "");
         * props[1].setObjectId(mibUtil.getOid("mobileIpaddr").getValue());
         * props[2] = new MOPROPERTY("mobileRxDbm", "");
         * props[2].setObjectId(mibUtil.getOid("mobileRxDbm").getValue());
         * props[3] = new MOPROPERTY("mobileTxDbm", "");
         * props[3].setObjectId(mibUtil.getOid("mobileTxDbm").getValue());
         * props[4] = new MOPROPERTY("mobilePacketLiveTime", "");
         * props[4].setObjectId
         * (mibUtil.getOid("mobilePacketLiveTime").getValue()); props[5] = new
         * MOPROPERTY("mobileSendBytes", "");
         * props[5].setObjectId(mibUtil.getOid("mobileSendBytes").getValue());
         * props[6] = new MOPROPERTY("mobileRecvBytes", "");
         * props[6].setObjectId(mibUtil.getOid("mobileRecvBytes").getValue());
         * props[7] = new MOPROPERTY("mobileLastConnectTime", "");
         * props[7].setObjectId
         * (mibUtil.getOid("mobileLastConnectTime").getValue());
         *
         * MOINSTANCE mport = IUtil.makeDummyMO("MCUMobilePort");
         * mport.addProperty(new MOPROPERTY("mcuId", new MOVALUE(mcuId)));
         * MOPROPERTY[] res = gw.cmdStdGet(mcuId,props); for (int i = 0; i <
         * res.length; i++) { if (res[i].getName().equals("mobileID")) {
         * mport.addProperty(new MOPROPERTY("mobileId", res[i].getValue())); }
         * else if (res[i].getName().equals("mobileIpaddr")) {
         * mport.addProperty(new MOPROPERTY("packetIpaddr", res[i].getValue()));
         * } else if (res[i].getName().equals("mobileRxDbm")) {
         * mport.addProperty(new MOPROPERTY("rcvDBM", res[i].getValue())); }
         * else if (res[i].getName().equals("mobileTxDbm")) {
         * mport.addProperty(new MOPROPERTY("sendDBM", res[i].getValue())); }
         * else if (res[i].getName().equals("mobilePacketLiveTime")) {
         * mport.addProperty(new MOPROPERTY("pktLiveTime", res[i].getValue()));
         * } else if (res[i].getName().equals("mobileSendBytes")) {
         * mport.addProperty(new MOPROPERTY("sendBytes", res[i].getValue())); }
         * else if (res[i].getName().equals("mobileRecvBytes")) {
         * mport.addProperty(new MOPROPERTY("rcvBytes", res[i].getValue())); }
         * else if (res[i].getName().equals("mobileLastConnectTime")) {
         * mport.addProperty(new MOPROPERTY("lastConnDate", res[i].getValue()));
         * } } mport.setProperty(new MOPROPERTY("id", mcuId + "_mport"));
         *
         * String mportInstanceName = IUtil.createInstance(mport, true);
         *
         * if (mcuInstanceName != null && mportInstanceName != null) { try {
         * IUtil.createAssociation("MCUHasMobilePort", mcuInstanceName,
         * mportInstanceName); } catch (MIException me) { if (me.getType() == 4)
         * { log.debug("alread association ["+mcuInstanceName+
         * ","+mportInstanceName+"]"); } else {
         * log.debug("create association failed : "+me); } } catch (Exception e)
         * { log.debug("create association failed : "+e); } }
         */
    }

    public void findLanPort(String mcuId) throws Exception {
        // TODO IMPLEMENT
        /*
         * log.debug("update lan port [ mcu="+mcuId+"]"); CommandGWMBean gw =
         * getCommandGW(); MIBUtil mibUtil = MIBUtil.getInstance();
         *
         * MOPROPERTY[] props = new MOPROPERTY[5]; props[0] = new
         * MOPROPERTY("ethName", "");
         * props[0].setObjectId(mibUtil.getOid("ethName").getValue()); props[1]
         * = new MOPROPERTY("ethPhyAddr", "");
         * props[1].setObjectId(mibUtil.getOid("ethPhyAddr").getValue());
         * props[2] = new MOPROPERTY("ethIpAddr", "");
         * props[2].setObjectId(mibUtil.getOid("ethIpAddr").getValue());
         * props[3] = new MOPROPERTY("ethSubnetMask", "");
         * props[3].setObjectId(mibUtil.getOid("ethSubnetMask").getValue());
         * props[4] = new MOPROPERTY("ethGateway", "");
         * props[4].setObjectId(mibUtil.getOid("ethGateway").getValue());
         *
         * MOINSTANCE lport = IUtil.makeDummyMO("MCULanPort");
         * lport.addProperty(new MOPROPERTY("mcuId", new MOVALUE(mcuId)));
         * MOPROPERTY[] res = gw.cmdStdGet(mcuId,props); for (int i = 0; i <
         * res.length; i++) { if (res[i].getName().equals("ethPhyAddr")){
         * log.debug("OID:"+res[i].getObjectId()); }
         *
         * if (res[i].getName().equals("ethPhyAddr")) { lport.addProperty(new
         * MOPROPERTY("physicalAddress", new
         * OCTET(res[i].getValue()).toHexString())); } else if
         * (res[i].getName().equals("ethIpAddr")) { lport.addProperty(new
         * MOPROPERTY("address", res[i].getValue())); } else if
         * (res[i].getName().equals("ethSubnetMask")) { lport.addProperty(new
         * MOPROPERTY("subnetMask", res[i].getValue())); } else if
         * (res[i].getName().equals("ethGateway")) { lport.addProperty(new
         * MOPROPERTY("defualtGw", res[i].getValue())); } }
         * lport.addProperty(new MOPROPERTY("id",mcuId+"_ppp"));
         *
         * String lportInstanceName = IUtil.createInstance(lport, true);
         *
         * if (mcuInstanceName != null && lportInstanceName != null) { try {
         * IUtil.createAssociation("MCUHasLANIF", mcuInstanceName,
         * lportInstanceName); } catch (MIException me) { if (me.getType() == 4)
         * { log.debug("already association ["+mcuInstanceName+
         * ","+lportInstanceName+"]"); } else {
         * log.debug("create association failed : "+me); } } catch (Exception e)
         * { log.debug("create association failed : "+e); } }
         */
    }

    public void findCodi(String mcuId) throws Exception {
        codiEntry[] ces = null;

        codiEntry[] codiEntry = null;
        try {
            MCU mcu = mcuDao.get(mcuId);
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            ces = gw.cmdGetCodiList(mcuId).toArray(new codiEntry[0]);

            for (int i = 0; i < ces.length; i++) {
                int codiIndex = ces[i].getCodiIndex().getValue();
                codiDeviceEntry cde = gw.cmdGetCodiDevice1(mcuId, codiIndex);
                codiBindingEntry cbe = gw.cmdGetCodiBinding1(mcuId, codiIndex);
                codiNeighborEntry cne = gw.cmdGetCodiNeighbor1(mcuId, codiIndex);
                codiMemoryEntry cme = gw.cmdGetCodiMemory1(mcuId, codiIndex);

                MCUCodiDevice cdevice = new MCUCodiDevice();
                cdevice.setCodiBaudRate(cde.getCodiBaudRate().getValue());
                cdevice.setCodiDataBit(cde.getCodiDataBit().getValue());
                cdevice.setCodiDevice(cde.getCodiDevice().toHexString());
                cdevice.setCodiID(cde.getCodiDevice().toHexString());
                cdevice.setCodiIndex(codiIndex);
                cdevice.setCodiParityBit(cde.getCodiParityBit().getValue());
                cdevice.setCodiRtsCts(cde.getCodiRtsCts().getValue());
                cdevice.setCodiStopBit(cde.getCodiStopBit().getValue());

                MCUCodiBinding cbind = new MCUCodiBinding();
                cbind.setCodiBindID(cbe.getCodiBindID().getValue());
                cbind.setCodiBindIndex(cbe.getCodiBindIndex().getValue());
                cbind.setCodiBindLocal(cbe.getCodiBindLocal().getValue());
                cbind.setCodiBindRemote(cbe.getCodiBindRemote().getValue());
                cbind.setCodiBindType(cbe.getCodiBindType().getValue());
                cbind.setCodiLastHeard(cbe.getCodiLastHeard().getValue());

                MCUCodiNeighbor cnb = new MCUCodiNeighbor();
                cnb.setCodiNeighborAge(cne.getCodiNeighborAge().getValue());
                cnb.setCodiNeighborId(cne.getCodiNeighborID().getValue());
                cnb.setCodiNeighborInCost(cne.getCodiNeighborInCost()
                        .getValue());
                cnb.setCodiNeighborIndex(cne.getCodiNeighborIndex().getValue());
                cnb.setCodiNeighborLqi(cne.getCodiNeighborLqi().getValue());
                cnb.setCodiNeighborOutCost(cne.getCodiNeighborOutCost()
                        .getValue());
                cnb.setCodiNeighborShortId(cne.getCodiNeighborShortID()
                        .getValue());

                MCUCodi codi = new MCUCodi();
                codi.setMcuCodiBinding(cbind);
                codi.setMcuCodiDevice(cdevice);
                codi.setMcuCodiMemory(new MCUCodiMemory());
                codi.setMcuCodiNeighbor(cnb);

                codiDao.saveOrUpdate(codi);
                mcu.setMcuCodi(codi);
                log.debug("update codi mo [ codi=" + ces[i].getCodiID()
                        + ", mcuid=" + mcuId + "]");
            }
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        }

    }

    public List<Map<String, Object>> findSensorInfo(MCU mcu) throws Exception {

        List<sensorInfoNewEntry> sensorEntries;
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcu.getSysID(), null, null));
            sensorEntries = gw.cmdGetModemAllNew(mcu.getSysID());
        } catch (Exception e) {
            throw e;
        }

        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        for (sensorInfoNewEntry entry : sensorEntries) {
            log.debug("update modem info [ modem=" + new HEX(entry.getSensorID().getValue()).toString()
                    + ", mcuid=" + mcu.getSysID() + "]");
            Map<String, Object> map = new HashMap<String, Object>(); 
            map.put("deviceSerial", new HEX(entry.getSensorID().getValue()).getValue());
            map.put("commState", new BYTE(entry.getSensorState().getValue()).getValue());
            map.put("fwVer", new WORD(entry.getSensorFwVersion().getValue()).decodeVersion());
            map.put("fwRevision", new WORD(entry.getSensorFwBuild().getValue()).getValue() + "");
            map.put("hwVer", new WORD(entry.getSensorHwVersion().getValue()).decodeVersion());
            map.put("lastLinkTime", new TIMESTAMP(entry.getSensorLastConnect().getValue()).getValue());
            map.put("nodeKind", new OCTET(entry.getSensorModel().getValue()).toString());
            map.put("sensorType", getSensorType(entry.getSensorModel().toString()));
            list.add(map);
        }
        return list;
    }
    
    public void findSensorNew(MCU mcu) throws Exception {

        List<sensorInfoNewEntry> sensorEntries;
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcu.getSysID(), null, null));
            sensorEntries = gw.cmdGetModemAllNew(mcu.getSysID());
        } catch (Exception e) {
            throw e;
        }

        for (sensorInfoNewEntry entry : sensorEntries) {
            log.debug("update modem info [ modem=" + new HEX(entry.getSensorID().getValue()).toString()
                    + ", mcuid=" + mcu.getSysID() + "]");

            updateSensorMO(entry, mcu);
        }

    }

    public void findSensorSPC(String sensorInstanceName,
            sensorInfoNewEntry sne) throws Exception {
        log.debug("update sensor spc mo [ sensor=" + sensorInstanceName + "]");
        Object spcInstanceName = createSensorSPCMO(sne);
        if (sensorInstanceName != null && spcInstanceName != null) {
            try {
                // IUtil.createAssociation("SerialConfOfZigBeeSensor",
                // sensorInstanceName, spcInstanceName);
            }

            catch (Exception e) {
                log.debug("create association failed : " + e);
            }
        }
    }

    /*
     * public String createCodiMO(codiEntry ce, codiDeviceEntry cde,
     * codiBindingEntry cbe, codiNeighborEntry cne, codiMemoryEntry cme, String
     * mcuId) throws Exception { MOINSTANCE codi =
     * CmdUtil.makeCodiMO(ce,cde,cbe,cne,cme, mcuId); return
     * IUtil.createInstance(codi, true); }
     */

    public String createCodiMO(codiEntry ce, codiDeviceEntry cde,
            codiBindingEntry cbe, codiNeighborEntry cne, codiMemoryEntry cme,
            String mcuInstName, String mcuId) throws Exception {
        String res = null;

        // TODO IMPLEMENT
        /*
         * MCU mcu = mcuDao.get( mcuId); MCUCodi codi = mcu.getMcuCodi();
         *
         * String codiId = ce.getCodiID().toString(); String codiIndex =
         * ce.getCodiIndex().toString();
         *
         * if (codi == null ) { log.error("no codi associated by "+mcuInstName);
         *
         * String[] tmpName =
         * IUtil.getMIOLocal().EnumerateInstanceNames("MCUCodi",
         * "id='"+codiId+"'"); if (tmpName != null && tmpName.length > 0) {
         * IUtil.getMIOLocal().DeleteInstance(tmpName[0], false); } res =
         * IUtil.getMIOLocal().CreateInstance(codi, false); } else { boolean
         * flag = true; String tmpId = null; String tmpIndex = null; for (int i
         * = 0; i < codies.length; i++) { tmpId =
         * codies[i].getPropertyValueString("id"); tmpIndex =
         * codies[i].getPropertyValueString("codiIndex");
         * log.debug("tmp codi "+tmpId+","+tmpIndex); if
         * (codiIndex.equals(tmpIndex)) { if (codiId.equals(tmpId)) {
         * codi.setName(codies[i].getName());
         * IUtil.getMIOLocal().SetInstance(codi); res = codies[i].getName(); }
         * else { IUtil.getMIOLocal().DeleteInstance(codies[i].getName(),
         * false); String[] tmpName =
         * IUtil.getMIOLocal().EnumerateInstanceNames("MCUCodi",
         * "id='"+codiId+"'"); if (tmpName != null && tmpName.length > 0) {
         * IUtil.getMIOLocal().DeleteInstance(tmpName[0], false); } res =
         * IUtil.getMIOLocal().CreateInstance(codi, false); } flag = false; } }
         *
         * if (flag) { String[] tmpName =
         * IUtil.getMIOLocal().EnumerateInstanceNames("MCUCodi",
         * "id='"+codiId+"'"); if (tmpName != null && tmpName.length > 0) {
         * IUtil.getMIOLocal().DeleteInstance(tmpName[0], false);
         *
         * } res = IUtil.getMIOLocal().CreateInstance(codi, false); } }
         */
        return res;
    }

    public Modem updateSensorMO(sensorInfoNewEntry sne, MCU mcu)
            throws Exception {
        ModemType sensorType = getSensorType(sne.getSensorModel().toString());

        if (sensorType.equals(ModemType.ZRU)) {
            ZRU zru = new ZRU();

            // sne.getSensorLastOTATime();
            // sne.getSensorOTAState();
            zru.setDeviceSerial(new HEX(sne.getSensorID().getValue()).getValue());
            zru.setMcu(mcu);
            zru.setCommState(new BYTE(sne.getSensorState().getValue()).getValue());
            zru.setFwVer(new WORD(sne.getSensorFwVersion().getValue()).decodeVersion());
            zru.setFwRevision(new WORD(sne.getSensorFwBuild().getValue()).getValue() + "");
            zru.setHwVer(new WORD(sne.getSensorHwVersion().getValue()).decodeVersion());
            zru.setLastLinkTime(new TIMESTAMP(sne.getSensorLastConnect().getValue()).getValue());
            zru.setNodeKind(new OCTET(sne.getSensorModel().getValue()).toString());

            zruDao.add(zru);
        } else if (sensorType.equals(ModemType.ZMU)) {

            ZMU zmu = new ZMU();
            zmu.setDeviceSerial(new HEX(sne.getSensorID().getValue()).getValue());
            zmu.setMcu(mcu);
            zmu.setCommState(new BYTE(sne.getSensorState().getValue()).getValue());
            zmu.setFwVer(new WORD(sne.getSensorFwVersion().getValue()).decodeVersion());
            zmu.setFwRevision(new WORD(sne.getSensorFwBuild().getValue()).getValue() + "");
            zmu.setHwVer(new WORD(sne.getSensorHwVersion().getValue()).decodeVersion());
            zmu.setLastLinkTime(new TIMESTAMP(sne.getSensorLastConnect().getValue()).getValue());
            zmu.setNodeKind(new OCTET(sne.getSensorModel().getValue()).toString());
            zmuDao.add(zmu);
        } else if (sensorType.equals(ModemType.ZEUPLS)) {
            ZEUPLS zeupls = new ZEUPLS();

            zeupls.setDeviceSerial(new HEX(sne.getSensorID().getValue()).getValue());
            zeupls.setMcu(mcu);
            zeupls.setCommState(new BYTE(sne.getSensorState().getValue()).getValue());
            zeupls.setFwVer(new WORD(sne.getSensorFwVersion().getValue()).decodeVersion());
            zeupls.setFwRevision(new WORD(sne.getSensorFwBuild().getValue()).getValue() + "");
            zeupls.setHwVer(new WORD(sne.getSensorHwVersion().getValue()).decodeVersion());
            zeupls.setLastLinkTime(new TIMESTAMP(sne.getSensorLastConnect().getValue()).getValue());
            zeupls.setNodeKind(new OCTET(sne.getSensorModel().getValue()).toString());
            zeuplsDao.add(zeupls);

        } else if (sensorType.equals(ModemType.Repeater)) {
            ZBRepeater zbRepeater = new ZBRepeater();
            zbRepeater.setDeviceSerial(new HEX(sne.getSensorID().getValue()).getValue());
            zbRepeater.setMcu(mcu);
            zbRepeater.setCommState(new BYTE(sne.getSensorState().getValue()).getValue());
            zbRepeater.setFwVer(new WORD(sne.getSensorFwVersion().getValue()).decodeVersion());
            zbRepeater.setFwRevision(new WORD(sne.getSensorFwBuild().getValue()).getValue() + "");
            zbRepeater.setHwVer(new WORD(sne.getSensorHwVersion().getValue()).decodeVersion());
            zbRepeater.setLastLinkTime(new TIMESTAMP(sne.getSensorLastConnect().getValue()).getValue());
            zbRepeater.setNodeKind(new OCTET(sne.getSensorModel().getValue()).toString());
            zbRepeaterDao.add(zbRepeater);
        } else if (sensorType.equals(ModemType.ZEU_MBus)) {
            ZEUMBus zeumbus = new ZEUMBus();
            zeumbus.setDeviceSerial(new HEX(sne.getSensorID().getValue()).getValue());
            zeumbus.setMcu(mcu);
            zeumbus.setCommState(new BYTE(sne.getSensorState().getValue()).getValue());
            zeumbus.setFwVer(new WORD(sne.getSensorFwVersion().getValue()).decodeVersion());
            zeumbus.setFwRevision(new WORD(sne.getSensorFwBuild().getValue()).getValue() + "");
            zeumbus.setHwVer(new WORD(sne.getSensorHwVersion().getValue()).decodeVersion());
            zeumbus.setLastLinkTime(new TIMESTAMP(sne.getSensorLastConnect().getValue()).getValue());
            zeumbus.setNodeKind(new OCTET(sne.getSensorModel().getValue()).toString());
            zeuMBusDao.add(zeumbus);
        } else {
            log.debug("Unkown Sensor Type or Not implemented Sensor Type");
            return null;
            // throw new
            // Exception("Unkown Sensor Type or Not implemented Sensor Type");
        }

        Modem instance = modemDao.get(new OCTET(sne.getSensorSerial().getValue()).toHexString());
        if (instance != null) {
            // IUtil.getMIOLocal().SetInstance(sensor);//TODO MODEM UPDATE
            // findSensorSPC(instanceName, sne);//TODO MODEMCONFG
        }
        return instance;
    }

    public Object createSensorSPCMO(sensorInfoNewEntry sne)
            throws Exception {
        ModemType sensorType = getSensorType(sne.getSensorModel().toString());

        if (sensorType.equals(ModemType.ZRU)) {
            // spc = CmdUtil.makeZRUSPCMO(sne); //TODO
        } else if (sensorType.equals(ModemType.ZMU)) {
            // spc = CmdUtil.makeZMUSPCMO(sne); //TODO
        } else {
            log.debug("Unkown Sensor Type or Not implemented Sensor Type");
        }
        return null;
        // return IUtil.createInstance(spc, true);
    }

    public List<Modem> getSensorMO(String mcuInstanceName)
            throws Exception {

        List list = null;
        Set<Condition> condition = new HashSet<Condition>();
        Condition con = new Condition("mcu.sysId",
                new Object[] { mcuInstanceName }, null, Restriction.EQ);
        condition.add(con);
        list = modemDao.findByConditions(condition);
        return list;
    }

    public Hashtable setDefaultMcuConfig(MCU mcu, String mcuId,
            String mcuType) throws Exception {

        Hashtable df = getDefaultMcuConfig("MCU", mcuType);
        // TODO IMPLEMENT
        /*
         * try{
         *
         * df.keySet().iterator(); gw.cmdStdSet(mcuId,String[] propNames,
         * String[] propValues); }catch(Exception e) { log.error(e); } for(int i
         * = 0 ; i < df.length ; i++) { mo.addProperty(df[i]); }
         *
         * MOPROPERTY p = mcu.getProperty("meterReadStart"); if (p == null) { p
         * = IUtil.getMoPropertyClone(mo.getClassName(), "meterReadStart"); }
         *
         * DefaultConf defaultConf = DefaultConf.getInstance();
         * p.setName(defaultConf.getMcuPropertyName(p.getName()));
         *
         * if (p.getValue() == null || p.getValue().equals("")) {
         * p.setValue(TimeUtil.getCurrentTime()); mo.addProperty(p); } try{
         * gw.cmdStdSet(mcuId,p); }catch(Exception e) { log.error(e); }
         */
        return df;
    }

    public Hashtable getDefaultMcuConfig(String className, String mcuType)
            throws Exception {
        DefaultConf dc = DefaultConf.getInstance();
        Hashtable props = dc.getDefaultProperties(className);
        return props;
    }

    /*
     * public void setInstallDate(MOINSTANCE m, String time) throws
     * Exception { try { MOINSTANCE mo = IUtil.makeDummyMO(m.getClassName());
     * mo.setName(m.getName()); mo.addProperty(new
     * MOPROPERTY("installDate",time)); mo.addProperty(new
     * MOPROPERTY("networkStatus","1")); IUtil.getMIOLocal().SetInstance(mo); }
     * catch (Exception e) {
     * log.error("update install date failed ["+m.getName()+"]");
     * log.error(e,e); } }
     */

    /*
     * public void setLastTimeSyncDate(MOINSTANCE m, String time) throws
     * Exception { try { MOINSTANCE mo = IUtil.makeDummyMO(m.getClassName());
     * mo.setName(m.getName()); mo.addProperty(new
     * MOPROPERTY("lastTimeSyncDate",time));
     * IUtil.getMIOLocal().SetInstance(mo); } catch (Exception e) {
     * log.error("update last time sync date failed ["+m.getName()+"]");
     * log.error(e,e); } }
     */

    /*
     * public void setInstallDate(MOINSTANCE[] m, String time) throws
     * Exception { for (int i = 0; i < m.length; i++) { setInstallDate(m[i],
     * time); } }
     */
    /*
     * public void setUnitInstallDate(Modem sensor, String time) { try {
     * MOINSTANCE[] units = IUtil.getMIOLocal().EnumerateAssociationInstances(
     * "AttachedSensor",sensor.getDeviceSerial(),"attachedSensor", "System");
     * for (int i = 0; i < units.length; i++) { setInstallDate(units[i], time);
     * }
     *
     * } catch (Exception e) {
     * log.error("update units install date failed ["+sensor
     * .getDeviceSerial()+"]"); log.error(e,e); } }
     */

    /*
     * public void setUnitInstallDate(MOINSTANCE[] sensor, String time) {
     * for (int i = 0; i < sensor.length; i++) { setUnitInstallDate(sensor[i],
     * time); } }
     */

    /*
     * public void updateMO(MOINSTANCE mo, boolean logFlag) {
     * updateMO(mo, "", logFlag); }
     */

    public String getBooleanString(String val) {
        String retval = "false";
        int i = 0;
        try {
            i = Integer.parseInt(val);
        } catch (Exception ex) {
        }
        if (i == 1) {
            retval = "true";
        }
        return retval;
    }

    /*
     * public void updateMO(MOINSTANCE mo, String oid, boolean logFlag) {
     * String instName = mo.getName(); if (instName == null) {
     * log.debug("mo instance ["+mo+"] does not have instance name"); return; }
     * MOINSTANCE tmpmo = null; try { tmpmo =
     * IUtil.getMIOLocal().GetInstance(instName, null); } catch (Exception e) {
     * log.error(e,e); }
     *
     * if (tmpmo == null) {
     * log.debug("mo instance ["+instName+"] is not exist in MI"); return; }
     *
     * MOPROPERTY mop = null; String mopName = null; String mopValue = null;
     * String tmpValue = null; Vector<MOPROPERTY> mops = mo.getProperties();
     * Vector<ChangeHistoryLogData> chlds = null; ChangeHistoryLogData chld =
     * null; if (logFlag) { chlds = new Vector<ChangeHistoryLogData>(); }
     *
     * String tmpId = tmpmo.getPropertyValueString("id"); String mopType = null;
     *
     * try { for (int i = 0; i < mops.size(); i++) { mop = mops.elementAt(i);
     * mopName = mop.getName(); mopValue = mop.getValue(); tmpValue =
     * tmpmo.getPropertyValueString(mopName); if(tmpmo.getProperty(mopName) !=
     * null) { mopType = tmpmo.getProperty(mopName).getType(); } if
     * (mopValue.equals(tmpValue)) { mo.removeProperty(mopName); } else {
     * //added by D.J Park in 2006.03.06 // check boolean value
     *
     * if(mopType != null && mopType.equals("java.lang.Boolean")) {
     * if(getBooleanString(mopValue).toLowerCase().equals(
     * tmpValue.toLowerCase())) { mo.removeProperty(mopName); continue; } }
     *
     * if (logFlag) { String time = TimeUtil.getCurrentTime(); chld = new
     * ChangeHistoryLogData(); chld.setChangeDate(TimeUtil.getCurrentDay());
     * chld.setChangeTime(time.substring(8)); chld.setServiceType(new
     * Integer(0)); chld.setOperator(tmpId);
     * chld.setOperatorInstanceName(instName);
     * chld.setTargetInstanceName(tmpmo.getName()); chld.setPropertyId(mopName);
     * chld.setPreviousVal(tmpValue); chld.setCurrentVal(mopValue);
     * chld.setInterface("5"); //modified by D.J Park 2006.02.17
     *
     * chld.setTargetType(IUtil.getSystemClassCode( tmpmo.getClassName()));
     * chld.setTargetId(tmpmo.getPropertyValueString("id"));
     * chld.setOperation(oid); chlds.add(chld); } } }
     *
     * IUtil.getMIOLocal().SetInstance(mo); } catch (Exception e) {
     * log.error(e,e); }
     *
     * if (logFlag) { for (int i = 0; i < chlds.size(); i++) { try {
     * DbUtil.saveCHLog(chlds.elementAt(i)); } catch (Exception e) {
     * log.error(e,e); } } } }
     */

    /*
     * public void insertUnRegisteredEquipment(MOINSTANCE inst) { try {
     * IUtil.createInstance(inst,true); }catch(Exception ex) { log.error(ex,ex);
     * } }
     */

    /*
     * public void removeEventAttributeAll(Event event) { EventAttr[]
     * attrs = event.getEventAttrs(); for(int i = 0 ; i < attrs.length ; i++) {
     * event.remove(attrs[i].getAttrName()); } }
     */

    @SuppressWarnings("unchecked")
    public String MapToJSON(Map map) throws Exception {
        StringBuffer rStr = new StringBuffer();
        Iterator<String> keys = map.keySet().iterator();
        String keyVal = null;
        rStr.append("[");
        while (keys.hasNext()) {
            keyVal = keys.next();
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

    public ModemType getSensorType(String meterModel) {
        ModemType modemType = ModemType.ZEUPLS;

        if (meterModel.contains("Kamstrup")
                || meterModel.contains("Aidon")
                || meterModel.contains("I210")
                || meterModel.contains("kV2c")
                || meterModel.contains("ADN")
                || meterModel.contains("K382")
                || meterModel.contains("K351")
                || meterModel.contains("K162")
                || meterModel.contains("K282")
                || meterModel.contains("SM")
                || meterModel.contains("Azos")
                || meterModel.contains("I210P")
                || meterModel.contains("LK")
                || meterModel.contains("OMNI")) {
            modemType = ModemType.ZRU;
        } else if (meterModel.equals("Elster M 140")
                || meterModel.equals("Elster V 220")
                || meterModel.equals("MD13")) {
            modemType = ModemType.ZEUPLS;
        } else if (meterModel.equals("REPEATER")) {
            modemType = ModemType.Repeater;
        }
        // Arm or 250 is ZEU_MBUS SensorType
        else if (meterModel.equals("NAMR-H101MG")
                || meterModel.equals("MSTR711")) {
            modemType = ModemType.ZEU_MBus;
        }
        return modemType;
    }

    /**
     * get Equip Version Information
     *
     * @param equipKind
     * @param mcuId
     */
    public int getEquipVersion(int equipKind, String triggerId, String mcuId) {
        log.debug("[getEquipVersion] EquipKind: " + equipKind + " ,triggerId:"  + triggerId + " ,mcuId:" + mcuId);
        int result = FW_STATE.Success.getState();
        try {
            // All
            if (equipKind == FW_EQUIP.All.getKind()) {
                try {
                    getMCUVersion(triggerId, mcuId);
                    getCodiVersion(triggerId, mcuId);
                    getModemVersion(triggerId, mcuId);
                } catch (Exception e) {
                    result = FW_STATE.Fail.getState();
                    log.error("Can Not Get Sensor Version Info :"+ e.getMessage(), e);
                }
            }
            // MCU
            else if (equipKind == FW_EQUIP.MCU.getKind()) {
                try {
                    getMCUVersion(triggerId, mcuId);
                } catch (Exception e) {
                    result = FW_STATE.Fail.getState();
                    log.error("Can Not Get MCU Version Info :" + e.getMessage(),e);
                }
            }
            // Codi
            else if (equipKind == FW_EQUIP.Coordinator.getKind()) {
                try {
                    getCodiVersion(triggerId, mcuId);
                } catch (Exception e) {
                    result = FW_STATE.Fail.getState();
                    log.error("Can Not Get Codi Version Info :" + e.getMessage(), e);
                }
            }
            // Modem or Repeater
            else if (equipKind == FW_EQUIP.Modem.getKind()) {
                try {
                    getModemVersion(triggerId, mcuId);
                } catch (Exception e) {
                    result = FW_STATE.Fail.getState();
                    log.error("Can Not Get Modem Version Info :" + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            result = FW_STATE.Fail.getState();
            log.error("Can Not Get Sensor Version Info :" + e.getMessage(), e);
        }
        return result;
    }

    /**
     * @param gw
     * @param triggerId
     * @param mcuId
     * @throws Exception
     */
    public void getModemVersion(String triggerId, String mcuId)
            throws Exception {
        log.debug("getModemVersion start...");

        List<sensorInfoNewEntry> sensorEntries = null;
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            sensorEntries = gw.cmdGetModemAllNew(mcuId);
        } catch (Exception e) {
            throw e;
        }

        String hwVersion = "";
        String fwVersion = "";
        String fwBuild = "";
        boolean isArm = false;
        MCU mcu = mcuDao.get(mcuId);
        if (mcu == null) {
            log.debug("MCU not exist! [" + mcuId + "]");
            return;
        }
        
        String modemId = null;
        int otaState = 0;
        for (sensorInfoNewEntry entry : sensorEntries) {
            // Check Sensor Type
            if (new OCTET(entry.getSensorModel().getValue()).toString().equals("MSTR711")) {
                isArm = true;
            } else {
                isArm = false;
            }

            // -------------------
            // Update OTA State
            // -------------------
            if (entry.getSensorID() != null && entry.getSensorOTAState() != null) {
                otaState = new BYTE(entry.getSensorOTAState().getValue()).getValue();
                modemId = new HEX(entry.getSensorID().getValue()).toString();
                log.debug("TriggerId[" + triggerId + "] mcuId[" + mcuId + 
                        "] modemId[" + modemId + "] otaState[" + otaState + "]");

                if ((otaState & FW_OTA.All.getStep()) == FW_OTA.All.getStep()) {
                    FirmwareUtil.updateOTAHistory(triggerId, modemId, FW_OTA.All,FW_STATE.Unknown, "");
                } else if ((otaState & FW_OTA.Scan.getStep()) == FW_OTA.Scan.getStep()) {
                    FirmwareUtil.updateOTAHistory(triggerId, modemId, FW_OTA.Scan,FW_STATE.Unknown, "");
                } else if ((otaState & FW_OTA.Install.getStep()) == FW_OTA.Install.getStep()) {
                    FirmwareUtil.updateOTAHistory(triggerId, modemId, FW_OTA.Install,FW_STATE.Unknown, "");
                } else if ((otaState & FW_OTA.Verify.getStep()) == FW_OTA.Verify.getStep()) {
                    FirmwareUtil.updateOTAHistory(triggerId, modemId, FW_OTA.Verify,FW_STATE.Unknown, "");
                } else if ((otaState & FW_OTA.DataSend.getStep()) == FW_OTA.DataSend.getStep()) {
                    FirmwareUtil.updateOTAHistory(triggerId, modemId, FW_OTA.DataSend,FW_STATE.Unknown, "");
                } else if ((otaState & FW_OTA.Check.getStep()) == FW_OTA.Check.getStep()) {
                    FirmwareUtil.updateOTAHistory(triggerId, modemId, FW_OTA.Check,FW_STATE.Unknown, "");
                } else if ((otaState & FW_OTA.Init.getStep()) == FW_OTA.Init.getStep()) {
                    FirmwareUtil.updateOTAHistory(triggerId, modemId, FW_OTA.Init,FW_STATE.Unknown, "");
                }
            }

            // -------------------
            // Update Version Info.
            // -------------------
            Modem dummySensor = new Modem();
            ModemType sensorType = getSensorType(new OCTET(entry.getSensorModel().getValue()).toString());
            log.debug("sensorType: " + sensorType);
            if (sensorType == ModemType.ZRU) {
                dummySensor.setModemType(ModemType.ZRU.name());
            } else if (sensorType == ModemType.ZEUPLS) {
                dummySensor.setModemType(ModemType.ZEUPLS.name());
            } else if (sensorType == ModemType.Repeater) {
                dummySensor.setModemType(ModemType.Repeater.name());
            } else if (sensorType == ModemType.ZEU_MBus) {
                dummySensor.setModemType(ModemType.ZEU_MBus.name());
            } else {
                log.debug("Unkown Sensor Type or Not implemented Sensor Type");
            }

            if (mcuId != null) {
                dummySensor.setMcu(mcu);
            }
            if (modemId != null) {
                dummySensor.setDeviceSerial(modemId);
            }

            // TODO 정리
            /*
             * if(sie[i].getSensorSerial()!=null){ Meter meter =
             * meterDao.get(sie[i].getSensorSerial().toString());
             * meter.setModem(modem) if(meter != null){
             * dummySensor.setMeter(meter); } }
             */
            if (entry.getSensorLastConnect() != null) {
                dummySensor.setLastLinkTime(new TIMESTAMP(entry.getSensorLastConnect().getValue()).toString());
            }
            if (entry.getSensorInstallDate() != null) {
                dummySensor.setInstallDate(new TIMESTAMP(entry.getSensorInstallDate().getValue()).toString());
            }
            if (entry.getSensorState() != null) {
                dummySensor.setCommState(new BYTE(entry.getSensorState().getValue()).getValue());
            }

            // ------------
            // Arm
            // ------------
            if (isArm) {
                if (entry.getSensorHwVersion() != null) {
                    hwVersion = new WORD(entry.getSensorHwVersion().getValue()).decodeVersion();
                    dummySensor.setHwVer(hwVersion);
                }
                if (entry.getSensorFwVersion() != null) {
                    fwVersion = new WORD(entry.getSensorFwVersion().getValue()).decodeVersion();
                    dummySensor.setFwVer(fwVersion);
                }
                if (entry.getSensorFwBuild() != null) {
                    fwBuild = String.format("%2d", new WORD(entry.getSensorFwBuild().getValue()).getValue());
                    dummySensor.setFwRevision(fwBuild);
                }
                log.debug("Arm - mcuId[" + mcuId + "] modemId["
                        + modemId + "] hwVersion[" + hwVersion
                        + "] fwVersion[" + fwVersion + "] fwBuild[" + fwBuild
                        + "]");
            }
            // ------------
            // Zigbee
            // ------------
            else {
                if (entry.getSensorHwVersion() != null) {
                    hwVersion = new WORD(entry.getSensorHwVersion().getValue()).decodeVersion();
                    dummySensor.setHwVer(hwVersion);
                }
                if (entry.getSensorFwVersion() != null) {
                    fwVersion = new WORD(entry.getSensorFwVersion().getValue()).decodeVersion();
                    dummySensor.setFwVer(fwVersion);
                }
                if (entry.getSensorFwBuild() != null) {
                    fwBuild = String.format("%2d", new WORD(entry.getSensorFwBuild().getValue()).getValue());
                    dummySensor.setFwRevision(fwBuild);
                }
                log.debug("Zigbee - mcuId[" + mcuId + "] modemId["
                        + modemId + "] hwVersion[" + hwVersion
                        + "] fwVersion[" + fwVersion + "] fwBuild[" + fwBuild
                        + "]");
            }

            Modem instance = modemDao.get(modemId);
            // DB Exist
            if (instance != null) {
                /*FirmwareUtil.checkFirmwareHistory(FW_EQUIP.Modem.getKind(),
                        triggerId, mcuId, sie[i].getSensorID().toString(),
                        hwVersion, fwVersion, fwBuild, isArm);*/
                /*
                 * 여기서 modem f/w 업데이트
                 *
                 * */

                log.debug("getModemVersion Exist Sensor - isArm["
                        + isArm
                        + "] mcuId["
                        + mcuId
                        + "] modemId["
                        + modemId
                        + "] hwVersion["
                        + hwVersion
                        + "] fwVersion["
                        + fwVersion
                        + "] fwBuild["
                        + fwBuild + "]");
                dummySensor.setId(instance.getId());
                modemDao.update(dummySensor);
            }
            // Not Exist
            else {
                modemDao.add(dummySensor);
                log.debug("Not Exist Sensor - mcuId["
                        + mcuId
                        + "] modemId["
                        + modemId
                        + "] hwVersion["
                        + hwVersion
                        + "] fwVersion["
                        + fwVersion
                        + "] fwBuild["
                        + fwBuild + "]");
            }
        }
    }

    /**
     * @param gw
     * @param triggerId
     * @param mcuId
     * @throws Exception
     */
    public void getMCUVersion(String triggerId, String mcuId)
            throws Exception {

        log.debug("getMCUVersion start");

        Hashtable mop = new Hashtable();
        codiEntry[] codiEntry = null;
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            ResponseMap rmap = gw.cmdStdGetChild(mcuId, "2.1.0"); 
            for (ResponseMap.Response.Entry e : rmap.getResponse().getEntry()) {
                mop.put(e.getKey(), e.getValue());
            }
        } catch (Exception e) {
            throw e;
        }

        MCU mcu = new MCU();
        String hwVersion = "";
        String fwVersion = "";
        String fwBuild = "";
        // TODO IMPLEMENT
        log.debug("mop.keySet()");
        Iterator it = mop.keySet().iterator();
        for (int i = 0; it.hasNext(); i++) {
            String key = (String) it.next();
            log.debug("mop.key = "+key );
            log.debug("String.valueOf(mop.get(key) = "+String.valueOf(mop.get(key)));
            if(key.equals("sysHwVersion")){
                  hwVersion=String.valueOf(mop.get(key));
            }
            if(key.equals("sysSwVersion")){
                fwVersion=String.valueOf(mop.get(key));
            }
            if(key.equals("sysSwRevision")){
                fwBuild=String.valueOf(mop.get(key));
            }
        }

        log.debug("hwVersion = "+hwVersion );
        log.debug("fwVersion = "+fwVersion );
        log.debug("fwBuild = "+fwBuild );
        log.debug("mcu.getSysID() = "+mcu.getSysID() );
        String mcuSysId = mcu.getSysID();

        /*
         * 여기서 FW 업그레이드 한다.
         * */
        StringBuffer sql = new StringBuffer();
        sql.append(" update mcu ");
        sql.append(" set sys_sw_revision = '"+hwVersion+"' , sys_sw_version =''"+fwVersion+"',  sys_sw_revision = ''"+fwBuild+"' ");
        sql.append(" where sys_id = '"+mcuSysId+"' ");
        mcuDao.updateFWByotaEvent(sql.toString());

    }

    /**
     * @param gw
     * @param triggerId
     * @param mcuId
     * @throws Exception
     */
    public void getCodiVersion(String triggerId, String mcuId)
            throws Exception {

        List<codiEntry> codiEntries;
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            codiEntries = gw.cmdGetCodiList(mcuId);
        } catch (Exception e) {
            throw e;
        }

        String hwVersion = "";
        String fwVersion = "";
        String fwBuild = "";
        String codiId = "";

        MCU mcu = mcuDao.get(mcuId);

        if (mcu == null) {
            log.error("MCU[" + mcuId
                    + "] is not have a codinator information in DB");
            return;
        }
        MCUCodi codi = mcu.getMcuCodi();

        if (codi == null) {
            log.error("MCU[" + mcuId
                    + "] is not have a codinator information in DB");
            return;
        }
        
        for (codiEntry _codiEntry : codiEntries) {
            codiId = new OCTET(_codiEntry.getCodiID().getValue()).toString();
            hwVersion = String.valueOf(_codiEntry.getCodiHwVer().getValue());
            codi.setCodiHwVer(hwVersion);
            fwVersion = String.valueOf(_codiEntry.getCodiFwVer().getValue());
            codi.setCodiFwVer(fwVersion);
            fwBuild = String.valueOf(_codiEntry.getCodiFwBuild().getValue());
            codi.setCodiFwBuild(fwBuild);
        }

        /*FirmwareUtil.checkFirmwareHistory(CommonConstants.FW_EQUIP.Coordinator
                .getKind(), triggerId + "", mcuId, codiId, hwVersion + "",
                fwVersion + "", fwBuild + "", false);*/
        log.debug("mcuId[" + mcuId + "] codiId[" + codi.getCodiShortID()
                + "] hwVersion[" + hwVersion + "] fwVersion[" + fwVersion
                + "] fwBuild[" + fwBuild + "]");
        codiDao.update(codi);
    }

    public void settingGetVersionSchedule(String equipKind,String triggerId, String mcuId) throws Exception {
        /* 이벤트마다 크론을 실행 하지 않기 위해.
        String equipKindStr = "";
        if (Integer.parseInt(equipKind) <= 3) {
            equipKindStr = CommonConstants.FW_EQUIP.valueOf(equipKind).name();

        } else {
            equipKindStr = "All";
        }
        Calendar cal = Calendar.getInstance();
        Date startTime = cal.getTime();
        String cronExp = "0 15 2 ? * *";
        if (equipKind.equals(CommonConstants.FW_EQUIP.MCU.getKind() + "")) {
            cal.add(Calendar.MINUTE, 5);
            cronExp = "0 0/5 * ? * *";
        } else if (equipKind.equals(CommonConstants.FW_EQUIP.Coordinator
                .getKind()
                + "")) {
            cal.add(Calendar.MINUTE, 5);
            cronExp = "0 0/5 * ? * *";
        } else if (equipKind.equals(CommonConstants.FW_EQUIP.Modem.getKind()
                + "")) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            cronExp = "0 0/5 * ? * *";
        } else {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            cronExp = "0 0/5 * ? * *";
        }
        startTime = cal.getTime();
        cal.add(Calendar.MINUTE, 5);
        Date endTime = cal.getTime();

        JobDetail job = null;
        JobDataMap jobMap = null;

        job = new JobDetail();
        job.setGroup("ScheduleDist");
        job.setName("Get Equip Version equipKind[" + equipKindStr + "] triggerId[" + triggerId + "] mcuId[" + mcuId + "]");
        job.setJobClass(Class.forName("com.aimir.schedule.job.ScheduleGetVersion"));
        job.setDescription("Get Equip Version Info");

        log.debug("Description: " + job.getDescription());
        job.setRequestsRecovery(true);
        jobMap = new JobDataMap();
        jobMap.put("equipKind", equipKind);
        jobMap.put("mcuId", mcuId);
        jobMap.put("triggerId", triggerId);
        jobMap.put("startTime", startTime);
        jobMap.put("endTime", endTime);
        jobMap.put("cronExp", cronExp);
        log.info(job.getName() + " cronExp: " + cronExp + " startTime: " + startTime + " endTime: " + endTime);
        JobUtil.addJobCron(job, jobMap, cronExp, startTime, endTime);*/

        //모뎀 스케줄러는 따로 만들고 바로 FW업데이트 한다.
        getEquipVersion(Integer.parseInt(equipKind), triggerId, mcuId);
    }

    public boolean isAsynch(Modem modem) throws Exception {
        if (modem.getModemType() == ModemType.ZEUPLS) {
            com.aimir.model.device.ZEUPLS zeupls = zeuplsDao.get(modem.getDeviceSerial());
            if (zeupls.getPowerType() == ModemPowerType.Battery
                    && zeupls.getNetworkType() == ModemNetworkType.FFD)
                return true;
        } else if (modem.getModemType() == ModemType.Repeater) {
            com.aimir.model.device.ZBRepeater repeater = zbRepeaterDao.get(modem.getDeviceSerial());
            if (repeater.getPowerType() == ModemPowerType.Battery
                    && repeater.getNetworkType() == ModemNetworkType.FFD)
                return true;
        }
        return false;
    }

    public Hashtable doMCUScanning(MCU mcu) throws Exception {

        DefaultConf defaultConf = DefaultConf.getInstance();
        Hashtable props = null;
        MIBUtil mibUtil = null;
        if(mcu.getNameSpace() != null && !"".equals(mcu.getNameSpace())){
        	props = defaultConf.getDefaultProperties("MCU-"+mcu.getNameSpace());
        	if(props == null){
        		props = defaultConf.getDefaultProperties("MCU");
                log.debug("props size=" + props.size());
                mibUtil = MIBUtil.getInstance(mcu.getNameSpace());
        	}
        }else{
        	props = defaultConf.getDefaultProperties("MCU");
            log.debug("props size=" + props.size());
            mibUtil = MIBUtil.getInstance();
        }

        List<String> property = new ArrayList<String>();
        Iterator it = props.keySet().iterator();
        for (int i = 0; it.hasNext(); i++) {
            try {

                String key = (String) it.next();
                // property[i] = (mibUtil.getOid(key)).getValue();
                property.add(key);
                log.debug("props[" + i + "] :" + key + " ,oid= " + property.get(i));
            } catch (Exception e) {
            }
        }

        Hashtable res = new Hashtable();
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcu.getSysID(), null, null));
            ResponseMap rmap = gw.cmdMcuScanning(mcu.getSysID(), property);
            List<ResponseMap.Response.Entry> entries = rmap.getResponse().getEntry();
            for (ResponseMap.Response.Entry e : entries.toArray(new ResponseMap.Response.Entry[0])) {
                res.put(e.getKey(), e.getValue());
            }
        } catch (Exception e) {
            throw e;
        }

        return res;
    }
    
    @Transactional(readOnly=false)
    public String doMCUPing(MCU mcu, int packetSize, int count) throws Exception {
    	//NetworkInfoLog nlog = new NetworkInfoLog();
        String cmdResult = "";
        String ipv4 = mcu.getIpAddr();
		String ipv6 = mcu.getIpv6Addr();
		
		// IPv4,IPv6 주소값 모두 null일 경우
		if (ipv4 == null && ipv6 == null) {
			return "NO-IP";
		}
		
		// FEP-SERVER Check
		try {
			CmdManager.getCommandWS(getProtocolType(mcu.getSysID(), null, null));
		} catch (Exception e) {
			return "FEP-DOWN";
		}
		
        CommandWS commandGW = CmdManager.getCommandWS(getProtocolType(mcu.getSysID(), null, null));
        String statusNetwork = "";
        String statusNetworkForIpv4 = "";
		String statusNetworkForIpv6 = "";
        
        if (ipv6 == null) { // CASE 1: IPv4 주소값만 들어있을 경우
        	cmdResult = cmdLinuxPing(commandGW, packetSize, count, ipv4); 
        	statusNetwork = saveNetworkInfoLog(packetSize, count, ipv4, cmdResult);
		} else if (ipv4 == null) { // CASE 2: IPv6 주소값만 들어있을 경우
			cmdResult = cmdLinuxPing(commandGW, packetSize, count, ipv6); 
			statusNetwork = saveNetworkInfoLog(packetSize, count, ipv6, cmdResult);
		} else if(ipv4 != null && ipv6 != null) { // CASE 3: IPv4,IPv6 주소값 모두 들어있을 경우
			String ipv4_pingResult = "";
			String ipv6_pingResult = "";
			
			// IPv4 Logic
			ipv4_pingResult = cmdLinuxPing(commandGW, packetSize, count, ipv4);
			statusNetworkForIpv4 = saveNetworkInfoLog(packetSize, count, ipv4, ipv4_pingResult);

			// IPv6 Logic
			ipv6_pingResult = cmdLinuxPing(commandGW, packetSize, count, ipv6); 
			statusNetworkForIpv6 = saveNetworkInfoLog(packetSize, count, ipv6, ipv6_pingResult);
        	
			cmdResult = "[IPv4]\n" + ipv4_pingResult + "\n\n" + "[IPv6]\n" + ipv6_pingResult;
		} else {
			return "FAIL";
		}
        
        if(statusNetwork == "FAIL" || statusNetworkForIpv4 == "FAIL" || statusNetworkForIpv6 == "FAIL") {
        	return "LOSS-ALL";
        }
		
		return cmdResult;
    }
    
    @Transactional(readOnly=false)
	public String doModemPing(Modem modem, int packetSize, int count) throws Exception {
		
    	//NetworkInfoLog nlog = new NetworkInfoLog();
        String cmdResult = "";
        String ipv4 = "";
		String ipv6 = "";
    	
		if (modem.getModemType() == ModemType.SubGiga) { // Modem Type이 SubGiga일 경우
			SubGiga subGigaModem = (SubGiga) modem;
			ipv4 = subGigaModem.getIpAddr();
			ipv6 = subGigaModem.getIpv6Address();
		} else if ((modem.getModemType() == ModemType.MMIU)) { // Modem Type이 MMIU일 경우
			MMIU mmiuModem = (MMIU) modem;
			ipv4 = mmiuModem.getIpAddr();
			ipv6 = mmiuModem.getIpv6Address();
			
			if (modem.getProtocolType() == Protocol.SMS) {
				// MMIU-SMS인데 IP주소가 없는 경우, 에러처리
				// MMIU-SMS인데 IP주소가 있는 경우, 해당 IP로 명령 실행
				if (ipv4 == null && ipv6 == null) {
					return "NOT-SUPPORT";
				}
			}
		}
		
		// IPv4,IPv6 주소값 모두 null일 경우
		if(ipv4 == null && ipv6 == null) { 
			return "NO-IP";	
		}
		
		// FEP-SERVER Check
		try {
			CmdManager.getCommandWS(getProtocolType(null, null, modem.getDeviceSerial()));
		} catch (Exception e) {
			return "FEP-DOWN";
		}
		
		CommandWS commandGW = CmdManager.getCommandWS(getProtocolType(null, null, modem.getDeviceSerial()));
		String statusNetwork = "";
        String statusNetworkForIpv4 = "";
		String statusNetworkForIpv6 = "";
		
		if (ipv6 == null) { // CASE 1: IPv4 주소값만 들어있을 경우
			cmdResult = cmdLinuxPing(commandGW, packetSize, count, ipv4); 
			statusNetwork = saveNetworkInfoLog(packetSize, count, ipv4, cmdResult);
		} else if (ipv4 == null) { // CASE 2: IPv6 주소값만 들어있을 경우
			cmdResult = cmdLinuxPing(commandGW, packetSize, count, ipv6); 
			statusNetwork = saveNetworkInfoLog(packetSize, count, ipv6, cmdResult);
		} else if(ipv4 != null && ipv6 != null) { // CASE 3: IPv4,IPv6 주소값 모두 들어있을 경우
			String ipv4_pingResult = "";
			String ipv6_pingResult = "";
			
			// IPv4 Logic
			ipv4_pingResult = cmdLinuxPing(commandGW, packetSize, count, ipv4);
			statusNetworkForIpv4 = saveNetworkInfoLog(packetSize, count, ipv4, ipv4_pingResult);

			// IPv6 Logic
			ipv6_pingResult = cmdLinuxPing(commandGW, packetSize, count, ipv6); 
			statusNetworkForIpv6 = saveNetworkInfoLog(packetSize, count, ipv6, ipv6_pingResult);
        	
			cmdResult = "[IPv4]\n" + ipv4_pingResult + "\n\n" + "[IPv6]\n" + ipv6_pingResult;
		} else {
			return "FAIL";
		}
		
		if(statusNetwork == "FAIL" || statusNetworkForIpv4 == "FAIL" || statusNetworkForIpv6 == "FAIL") {
        	return "LOSS-ALL";
        }
		
		return cmdResult;
	}
    
    public String saveNetworkInfoLog(int packetSize, int count,  String ipAddress, String cmdResult) throws Exception {
    	NetworkInfoLog nlog = new NetworkInfoLog();
    	
    	if(cmdResult.equals("FAIL")) {
    		return "FAIL";
    	}
    	
    	// 결과 분석 영역(S)
		double avgRtt = 0.0;
		double loss = 0.0;
		double avgTtl = 0.0;
		int timeOut_count = 0;
		int timeOutCheckCount = 0;
		int timeOutIndex = -1;
		double defaultNum = 0.0;
		String defaultText = "non";
		String timeOut = "Request timed out.";
		String lostAll = "100% loss";
		String lostAllLinux = "100% packet loss";
		String hostUnreachable = "unreachable";
		String hostError = "not find host";
		String hostErrorLinux = "unknown";
		
		// IP 유효 검사
		if (cmdResult.contains(hostError) || cmdResult.contains(hostUnreachable) || cmdResult.contains(hostErrorLinux)) {
			return "FAIL";
		}
		
		if (cmdResult.contains(lostAllLinux)) {
			loss = 100.0;
			avgTtl = defaultNum;
			avgRtt = defaultNum;
			packetSize = 0;
		} else {

			// 평균 RTT
			String[] splitDiagonal = cmdResult.split("/");
			String[] splitAvgRTT = splitDiagonal[4].split("=");
			String avg_Rtt = splitAvgRTT[0];
			avgRtt = Double.parseDouble(avg_Rtt);

			// 손실
			String[] splitComma = cmdResult.split(",");
			String str_loss = splitComma[2].replaceAll("[^0-9]", "");
			loss = Double.parseDouble(str_loss);

			// 평균 TTL
			String[] splitTtlPattern = cmdResult.split("ttl=");
			String[] splitTtl = null;
			String avg_Ttl = "";
			String str_Ttl = "";
			int int_Ttl = 0;
			int sum_Ttl = 0;

			Pattern pattern = Pattern.compile(timeOut);
			Matcher matcher = pattern.matcher(cmdResult);

			for (int i = 0; matcher.find(i); i = matcher.end()) {
				timeOut_count++;
			}

			for (int i = 1; i < splitTtlPattern.length; i++) {
				splitTtl = splitTtlPattern[i].split("=");
				str_Ttl = splitTtl[0].replaceAll("[^0-9]", "");
				int_Ttl = Integer.parseInt(str_Ttl);

				sum_Ttl += int_Ttl;
			}

			avgTtl = sum_Ttl / (count - timeOut_count);
		}
		// 결과 분석 영역(E)
		
		nlog.setTargetNode(defaultText);
		nlog.setCommand("ICMP");
		nlog.setDateTime(DateTimeUtil.getDateString(new Date()));
		nlog.setTemperature(defaultNum);
		nlog.setWeather(defaultText);
		nlog.setLoss(loss);
		nlog.setTtl(avgTtl);
		nlog.setRtt(avgRtt);
		nlog.setPacketSize(packetSize);
		nlog.setIpAddr(ipAddress);

		log.info(nlog.toJSONString());
		log.debug("IP[" + ipAddress + "], PACKET_SIZE[" + packetSize + "] COUNT[" + count + "] " + "LOSS[" + loss + "] AVG_TTL[" + avgTtl + "] AVG_RTT[" + avgRtt + "]");
		
		nlogDao_HN.add(nlog);
		
		if (nlog.getLoss() == 100) { 
			return "FAIL";
		}
		
		return cmdResult;
    }
    
	@Transactional(readOnly = false)
	public String doMcuCOAPPing(MCU mcu) throws Exception {
		String cmdResult = "";
		String ipv4 = mcu.getIpAddr();
		String ipv6 = mcu.getIpv6Addr();
		String type ="";
		// IPv4,IPv6 주소값 모두 null일 경우
		if(ipv4 == null && ipv6 == null) { 
			return "NO-IP";	
		}
		
		// FEP-SERVER Check
		try {
			CmdManager.getCommandWS(getProtocolType(mcu.getSysID(), null, null));
		} catch (Exception e) {
			return "FEP-DOWN";
		}
		
		CommandWS commandGW = CmdManager.getCommandWS(getProtocolType(mcu.getSysID(), null, null));
		
		if (ipv6 == null) { // CASE 1: IPv4 주소값만 들어있을 경우
			cmdResult = commandGW.coapPing(ipv4,"mcu",type, "", mcu.getSysSwVersion());
		} else if (ipv4 == null) { // CASE 2: IPv6 주소값만 들어있을 경우
			cmdResult = commandGW.coapPing(ipv6,"mcu",type, "", mcu.getSysSwVersion());
		} else if (ipv4 != null && ipv6 != null) { // CASE 3: IPv4,IPv6 주소값 모두 들어있을 경우
			String ipv4_pingResult = "";
			String ipv6_pingResult = "";
			
			ipv4_pingResult = commandGW.coapPing(ipv4,"mcu",type, "", mcu.getSysSwVersion());
			ipv6_pingResult = commandGW.coapPing(ipv6,"mcu",type, "", mcu.getSysSwVersion());
			
			cmdResult = "[IPv4]\n" + ipv4_pingResult + "\n\n" + "[IPv6]\n" + ipv6_pingResult;
		} else {
			return "FAIL";
		}

		return cmdResult;
	}
	
    public String doMCUTraceroute(MCU mcu, String hopCount) throws Exception {
    	//NetworkInfoLog nlog = new NetworkInfoLog();
        String cmdResult = "";
        String ipv4 = mcu.getIpAddr();
		String ipv6 = mcu.getIpv6Addr();
		
		// IPv4,IPv6 주소값 모두 null일 경우
		if (ipv4 == null && ipv6 == null) {
			return "This command is not supported. Because there is no ip address.";
		}
		
		try {
			// FEP-SERVER Check
			CmdManager.getCommandWS(getProtocolType(mcu.getSysID(), null, null));
		} catch (Exception e) {
			return "FEP-Server is down.";
		}
		
        CommandWS commandGW = CmdManager.getCommandWS(getProtocolType(mcu.getSysID(), null, null));
        
		if (ipv6 == null) { 
			// CASE 1: IPv4 주소값만 들어있을 경우
			cmdResult = cmdLinuxTraceroute(commandGW, ipv4, hopCount);
		} else if (ipv4 == null) { 
			// CASE 2: IPv6 주소값만 들어있을 경우
			cmdResult = cmdLinuxTraceroute(commandGW, ipv6, hopCount);
		} else if(ipv4 != null && ipv6 != null) { 
			// CASE 3: IPv4,IPv6 주소값 모두 들어있을 경우
			String ipv4_tracerouteResult = "";
			String ipv6_tracerouteResult = "";
			
			ipv4_tracerouteResult = cmdLinuxTraceroute(commandGW, ipv4, hopCount);
			ipv6_tracerouteResult = cmdLinuxTraceroute(commandGW, ipv6, hopCount); 
        	
			cmdResult = "[IPv4]\n" + ipv4_tracerouteResult + "\n\n" + "[IPv6]\n" + ipv6_tracerouteResult;
		} else {
			return "FAIL";
		}
		
		return cmdResult;
    }
    
//	public String cmdWindowsTraceroute(String ipAddress) {
//		Pattern pattern;
//		Runtime runTime = Runtime.getRuntime();
//		Process process;
//		String line = "";
//		String param = "";
//		String cmdResult = "";
//
//		pattern = Pattern.compile(regexIPv4andIPv6);
//        if(ipAddress == null || pattern.matcher(ipAddress).matches() == false){
//        	cmdResult = "Does not fit on the IPv4 & IPv6 type.";
//        } 
//		
//		// IPv4
//		pattern = Pattern.compile(regexIPv4);
//		if (pattern.matcher(ipAddress).matches() == true) {
//			param = "tracert " + ipAddress;
//		}
//
//		// IPv6
//		pattern = Pattern.compile(regexIPv6);
//		if (pattern.matcher(ipAddress).matches() == true) {
//			param = "tracert -6 " + ipAddress;
//		}
//
//		try {
//			process = runTime.exec("cmd /c chcp 437 & " + param);
//
//			InputStream inputStream = process.getInputStream();
//			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//			while ((line = bufferedReader.readLine()) != null) {
//				cmdResult += line + "\n";
//			}
//
//			inputStream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return cmdResult;
//	}
    
//	public String cmdWindowsPing(String packetSize, String count_ping, String ipAddress) {
//		Pattern pattern;
//		Runtime runTime = Runtime.getRuntime();
//		Process process;
//		String cmdResult = "";
//		String line = "";
//		String param = "";
//		
//		pattern = Pattern.compile(regexIPv4andIPv6);
//        if(ipAddress == null || pattern.matcher(ipAddress).matches() == false){
//        	cmdResult = "FAIL";
//        } else {
//        	// IPv4
//    		pattern = Pattern.compile(regexIPv4);
//    		if (pattern.matcher(ipAddress).matches() == true) {
//    			param = "ping -n " + count_ping + " -l " + packetSize + " " + ipAddress;
//    		}
//
//    		// IPv6
//    		pattern = Pattern.compile(regexIPv6);
//    		if (pattern.matcher(ipAddress).matches() == true) {
//    			param = "ping -6 -n " + count_ping + " -l " + packetSize + " " + ipAddress;
//    		}
//
//    		try {
//    			process = runTime.exec("cmd /c chcp 437 & " + param);
//
//    			InputStream inputStream = process.getInputStream();
//    			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//    			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//    			while ((line = bufferedReader.readLine()) != null) {
//    				cmdResult += line + "\n";
//    			}
//
//    			inputStream.close();
//    		} catch (IOException e) {
//    			e.printStackTrace();
//    		}
//        }
//        
//		log.debug(cmdResult);
//		return cmdResult;
//	}
    
	public String cmdLinuxPing(CommandWS commandGW, int packet_size, int count, String ipAddress) throws Exception_Exception {
    	Pattern pattern;
    	//CmdOperationUtil cmdOperationUtil = new CmdOperationUtil();
        List<String> commands = new ArrayList<String>();
        String packetSize = Integer.toString(packet_size);
        String count_ping = Integer.toString(count);
        
        
        pattern = Pattern.compile(regexIPv4andIPv6);
        if(ipAddress == null || pattern.matcher(ipAddress).matches() == false){
        	return "FAIL";
        	
        } else {
        	// IPv4
        	pattern = Pattern.compile(regexIPv4);
    		if(pattern.matcher(ipAddress).matches() == true){
    			commands.add("ping");
                commands.add("-c");
                commands.add(count_ping);
                commands.add("-s");
                commands.add(packetSize);
                commands.add(ipAddress);
    		}
    		
            // IPv6
    		pattern = Pattern.compile(regexIPv6);
    		if(pattern.matcher(ipAddress).matches() == true){
    			commands.add("ping6");
                commands.add("-c");
                commands.add(count_ping);
                commands.add("-s");
                commands.add(packetSize);
                commands.add(ipAddress);
    		}
        }
        
    	return commandGW.icmpPing(commands);
    }
    
	public String cmdLinuxTraceroute(CommandWS commandGW, String ipAddress, String hopCount) throws Exception_Exception {
		Pattern pattern;
		//CmdOperationUtil cmdOperationUtil = new CmdOperationUtil();
		List<String> commands = new ArrayList<String>();
		String cmdResult = "";

		pattern = Pattern.compile(regexIPv4andIPv6);
        if(ipAddress == null || pattern.matcher(ipAddress).matches() == false){
        	return "FAIL";
        } 
		
		// IPv4
		pattern = Pattern.compile(regexIPv4);
		if (pattern.matcher(ipAddress).matches() == true) {
			commands.add("traceroute");
			commands.add("-n");
			commands.add("-w 3");
			commands.add("-q 2");
			// set the maximum hop limit
			commands.add("-m" + hopCount);
			commands.add(ipAddress);
		}
		
		// IPv6
		pattern = Pattern.compile(regexIPv6);
		if (pattern.matcher(ipAddress).matches() == true) {
			commands.add("traceroute6");
			commands.add("-n");
			commands.add("-w 3");
			commands.add("-q 2");
			// set the maximum hop limit
			commands.add("-m " + hopCount);
			commands.add(ipAddress);
		}
		
		return commandGW.traceroute(commands);
	}
	
	@Transactional(readOnly = false)
	public String doModemCOAPPing(Modem modem, String ipv4, String ipv6, String type) throws Exception {
		String cmdResult = "";
		String modemType = "";
		String protocolType = "";
		
		// IPv4,IPv6 주소값 모두 null일 경우
		if(ipv4 == null && ipv6 == null) { 
			return "NO-IP";	
		}
		
		// FEP-SERVER Check
		try {
			CmdManager.getCommandWS(getProtocolType(null, null, modem.getDeviceSerial()));
		} catch (Exception e) {
			return "FEP-DOWN";
		}
		
		CommandWS commandGW = CmdManager.getCommandWS(getProtocolType(null, null, modem.getDeviceSerial()));
		
		if (ipv6 == null) { // CASE 1: IPv4 주소값만 들어있을 경우
			cmdResult = commandGW.coapPing(ipv4,"modem",type, modem.getProtocolType().name(), modem.getFwVer());
		} else if (ipv4 == null) { // CASE 2: IPv6 주소값만 들어있을 경우
			cmdResult = commandGW.coapPing(ipv6,"modem",type, modem.getProtocolType().name(), modem.getFwVer());
		} else if (ipv4 != null && ipv6 != null) { // CASE 3: IPv4,IPv6 주소값 모두 들어있을 경우
			String ipv4_pingResult = "";
			String ipv6_pingResult = "";
				
			ipv4_pingResult = commandGW.coapPing(ipv4,"modem",type, modem.getProtocolType().name(), modem.getFwVer());
			ipv6_pingResult = commandGW.coapPing(ipv6,"modem",type, modem.getProtocolType().name(), modem.getFwVer());
			
			cmdResult = "[IPv4]\n" + ipv4_pingResult + "\n\n" + "[IPv6]\n" + ipv6_pingResult;
		} else {
			return "FAIL";
		}

		return cmdResult;
	}
	
	/**
	 * GetInfo (COAP)
	 **/
	@Transactional(readOnly = false)
	public Map<String, String> coapGetInfo(Modem modem, String ipv4ForMBB, String ipv6ForMBB) throws Exception {
		//CmdOperationUtil cmdOperationUtil = new CmdOperationUtil();
		//CommandGW commandGW = new CommandGW();
		String modemIpv4 = "";
		String modemIpv6 ="";
		String type ="";
		log.debug("[Get Info]");
		if(modem instanceof SubGiga){
			SubGiga subGigaModem = (SubGiga)modem;
			modemIpv4 = subGigaModem.getIpAddr();
			modemIpv6 = subGigaModem.getIpv6Address();
			type = ModemIFType.RF.name();
			log.debug("[Get Info]: Modem Type : RF");
		}else if(modem instanceof MMIU){
			MMIU mmiuModem = (MMIU)modem;
			if(modem.getProtocolType() == Protocol.IP){
				modemIpv4 = mmiuModem.getIpAddr();
				modemIpv6 = mmiuModem.getIpv6Address();
				type = ModemIFType.Ethernet.name();
				log.debug("[Get Info]: Modem Type : Ethernet");
			}else if(modem.getProtocolType() == Protocol.SMS){
				modemIpv4 = ipv4ForMBB;
				modemIpv6 = ipv6ForMBB;
				type = ModemIFType.MBB.name();
				log.debug("[Get Info]: Modem Type : MBB");
			}else if(modem.getProtocolType() == Protocol.GPRS){
				modemIpv4 = mmiuModem.getIpAddr();
				modemIpv6 = mmiuModem.getIpv6Address();
				type = ModemIFType.MBB.name();
				log.debug("[Get Info]: Modem Type : MBB");
			}
		}
		
		Map cmdResult = new HashMap<String,String>();
		
		String ipv4 = modemIpv4 != null ? modemIpv4 :" ";
		String ipv6 = modemIpv6 != null ? modemIpv6 :" ";
		log.debug("[Get Info] pattern check");
		Pattern pattern;
		pattern = Pattern.compile(regexIPv4);
		if (pattern.matcher(ipv4).matches() != true) 
			ipv4 = null;
		pattern = Pattern.compile(regexIPv6);
		if (pattern.matcher(ipv6).matches() != true) 
			ipv6 = null;
		if((ipv4 != null)&&(ipv6 != null))
			ipv4 = null;
		log.debug("[Get Info] Modem IP: IPv4[" + ipv4 + "] / IPv6[" + ipv6 + "] Modem Type: " + type );
		
		
		//cmdResult =commandGW.modemCoAP(ipv4,ipv6,type);
		String modemId = modem.getDeviceSerial();
		CommandWS commandGW = CmdManager.getCommandWS(getProtocolType(null, null, modemId));
		ResponseMap obj=commandGW.coapGetInfo(ipv4, ipv6, type, modem.getProtocolType().name(), modem.getFwVer());
		for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
            log.debug("key["+e.getKey()+"], value["+ e.getValue()+"]");
            cmdResult.put(e.getKey().toString(), e.getValue().toString());
        }
		if(type.startsWith(ModemIFType.RF.name()))
			type = ModemIFType.RF.name();
		cmdResult.put("type", type);
		return cmdResult;
	}
	
	/**
	 * ModemReset (COAP)
	 **/
	@Transactional(readOnly = false)
	public Map<String, String> ModemReset(Modem modem, String ipv4ForMBB, String ipv6ForMBB) throws Exception {
		//CmdOperationUtil cmdOperationUtil = new CmdOperationUtil();
		//CommandGW commandGW = new CommandGW();
		String modemIpv4 = "";
		String modemIpv6 ="";
		String type ="";
		
		//SubGiga와 MMIU일때 타입나눠서 주소 받아오기
		if(modem instanceof SubGiga){
			SubGiga subGigaModem = (SubGiga)modem;
			modemIpv4 = subGigaModem.getIpAddr();
			modemIpv6 = subGigaModem.getIpv6Address();
			type = ModemIFType.RF.name();
		}else if(modem instanceof MMIU){
			MMIU mmiuModem = (MMIU)modem;
			if(modem.getProtocolType() == Protocol.IP){
				modemIpv4 = mmiuModem.getIpAddr();
				modemIpv6 = mmiuModem.getIpv6Address();
				type = ModemIFType.Ethernet.name();
			}else if(modem.getProtocolType() == Protocol.SMS){
				modemIpv4 = ipv4ForMBB;
				modemIpv6 = ipv6ForMBB;
				type = ModemIFType.MBB.name();
			}else if(modem.getProtocolType() == Protocol.GPRS){
				modemIpv4 = mmiuModem.getIpAddr();
				modemIpv6 = mmiuModem.getIpv6Address();
				type = ModemIFType.MBB.name();
			}
		}
		
		Map cmdResult = new HashMap<String,String>();
		String ipv4 = modemIpv4 != null ? modemIpv4 :" ";
		String ipv6 = modemIpv6 != null ? modemIpv6 :" ";
		Pattern pattern;
		pattern = Pattern.compile(regexIPv4);
		if (pattern.matcher(ipv4).matches() != true) 
			ipv4 = null;
		pattern = Pattern.compile(regexIPv6);
		if (pattern.matcher(ipv6).matches() != true) 
			ipv6 = null;
		if((ipv4 != null)&&(ipv6 != null))
			ipv4 = null;
		log.debug("[Reset Modem] Modem IP: " + ipv4 + " / " + ipv6 + " Modem Type: " + type );
		//cmdResult =commandGW.modemReset(ipv4,ipv6,type);
		String modemId = modem.getDeviceSerial();
		CommandWS commandGW = CmdManager.getCommandWS(getProtocolType(null, null, modemId));
		ResponseMap obj=commandGW.modemReset(ipv4,ipv6,type, modem.getProtocolType().name(), modem.getFwVer());
		for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
            log.debug("key["+e.getKey()+"], value["+ e.getValue()+"]");
            cmdResult.put(e.getKey().toString(), e.getValue().toString());
        }
		return cmdResult;
	}

	/**
	 * coapBrowser
	 **/
	@Transactional(readOnly = false)
	public Map<String, String> coapBrowser(Modem modem, String ipv4ForMBB, String ipv6ForMBB, String uri, String query, String config) throws Exception {
		//CmdOperationUtil cmdOperationUtil = new CmdOperationUtil();
		//CommandGW commandGW = new CommandGW();
		String modemIpv4 = "";
		String modemIpv6 ="";
		String type ="";
		
		//SubGiga와 MMIU일때 타입나눠서 주소 받아오기
		if(modem instanceof SubGiga){
			SubGiga subGigaModem = (SubGiga)modem;
			modemIpv4 = subGigaModem.getIpAddr();
			modemIpv6 = subGigaModem.getIpv6Address();
			type=subGigaModem.getFwVer(); 	// RF  pana적용 때문에 임시 수정
		}else if(modem instanceof MMIU){
			MMIU mmiuModem = (MMIU)modem;
			if(modem.getProtocolType() == Protocol.IP){
				modemIpv4 = mmiuModem.getIpAddr();
				modemIpv6 = mmiuModem.getIpv6Address();
				type = ModemIFType.Ethernet.name();
			}else if(modem.getProtocolType() == Protocol.SMS){
				modemIpv4 = ipv4ForMBB;
				modemIpv6 = ipv6ForMBB;
				type = ModemIFType.MBB.name();
			}else if(modem.getProtocolType() == Protocol.GPRS){
				modemIpv4 = mmiuModem.getIpAddr();
				modemIpv6 = mmiuModem.getIpv6Address();
				type = ModemIFType.MBB.name();
			}
		}
		
		Map cmdResult = new HashMap<String,String>();
		String ipv4 = modemIpv4 != null ? modemIpv4 :" ";
		String ipv6 = modemIpv6 != null ? modemIpv6 :" ";
		Pattern pattern;
		pattern = Pattern.compile(regexIPv4);
		if (pattern.matcher(ipv4).matches() != true) 
			ipv4 = null;
		pattern = Pattern.compile(regexIPv6);
		if (pattern.matcher(ipv6).matches() != true) 
			ipv6 = null;
		if((ipv4 != null)&&(ipv6 != null))
			ipv4 = null;
		//cmdResult =commandGW.modemReset(ipv4,ipv6,type);
		String modemId = modem.getDeviceSerial();
		CommandWS commandGW = CmdManager.getCommandWS(getProtocolType(null, null, modemId));
		ResponseMap obj=commandGW.coapBrowser(ipv4,ipv6,uri,query,config,type, modem.getProtocolType().name(), modem.getFwVer());
		for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
            log.debug("key["+e.getKey()+"], value["+ e.getValue()+"]");
            cmdResult.put(e.getKey().toString(), e.getValue().toString());
        }
		return cmdResult;
	}
	
	/**
	 * coapBrowser
	 **/
	@Transactional(readOnly = false)
	public Map<String, String> coapBrowserForDCU(MCU dcu, String uri, String query, String config) throws Exception {
		//CmdOperationUtil cmdOperationUtil = new CmdOperationUtil();
		Pattern pattern;
		Map cmdResult = new HashMap<String,String>();
		String ipv4 = "";
		String ipv6 = ""; 
		if(dcu.getIpAddr() != null)
			ipv4 = dcu.getIpAddr();
		if(dcu.getIpv6Addr() != null)
			ipv6 = dcu.getIpv6Addr();
		pattern = Pattern.compile(regexIPv4);
		if (pattern.matcher(ipv4).matches() != true) 
			ipv4 = null;
		pattern = Pattern.compile(regexIPv6);
		if (pattern.matcher(ipv6).matches() != true) 
			ipv6 = null;
		if((ipv4 != null)&&(ipv6 != null))
			ipv4 = null;
		log.debug("[Coap Browser] Modem IP: " + ipv4 + " / " + ipv6);
		CommandWS commandGW = CmdManager.getCommandWS(getProtocolType(dcu.getSysID(), null, null));
		ResponseMap obj=commandGW.coapBrowser(ipv4,ipv6,uri,query,config,"", "", dcu.getSysSwVersion());
		for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
            log.debug("key["+e.getKey()+"], value["+ e.getValue()+"]");
            cmdResult.put(e.getKey().toString(), e.getValue().toString());
        }
		return cmdResult;
	}
	
    public String doModemTraceroute(Modem modem, String hopCount) throws Exception {
    	String cmdResult = "";
        String ipv4 = "";
		String ipv6 = "";
    	
		if (modem.getModemType() == ModemType.SubGiga) { // Modem Type이 SubGiga일 경우
			SubGiga subGigaModem = (SubGiga) modem;
			ipv4 = subGigaModem.getIpAddr();
			ipv6 = subGigaModem.getIpv6Address();
		} else if ((modem.getModemType() == ModemType.MMIU)) { // Modem Type이 MMIU일 경우
			MMIU mmiuModem = (MMIU) modem;
			ipv4 = mmiuModem.getIpAddr();
			ipv6 = mmiuModem.getIpv6Address();
			
			if (modem.getProtocolType() == Protocol.SMS) {
				// MMIU-SMS인데 IP주소가 없는 경우, 에러처리
				// MMIU-SMS인데 IP주소가 있는 경우, 해당 IP로 명령 실행
				if (ipv4 == null && ipv6 == null) {
					return "Does not support the SMS service to traceroute.";
				}
			}
		}
		
		// IPv4,IPv6 주소값 모두 null일 경우
		if (ipv4 == null && ipv6 == null) {
			return "This command is not supported. Because there is no ip address.";
		}
		
		// FEP-SERVER Check
		try {
			CmdManager.getCommandWS(getProtocolType(modem.getDeviceSerial(), null, null));
		} catch (Exception e) {
			return "FEP-Server is down.";
		}
		
		CommandWS commandGW = CmdManager.getCommandWS(getProtocolType(null, null, modem.getDeviceSerial()));
    	
		if (ipv6 == null) { 
			// CASE 1: IPv4 주소값만 들어있을 경우
			cmdResult = cmdLinuxTraceroute(commandGW, ipv4, hopCount);
		} else if (ipv4 == null) {
			// CASE 2: IPv6 주소값만 들어있을 경우
			cmdResult = cmdLinuxTraceroute(commandGW, ipv6, hopCount);
		} else if(ipv4 != null && ipv6 != null) { 
			// CASE 3: IPv4,IPv6 주소값 모두 들어있을 경우
			String ipv4_tracerouteResult = "";
			String ipv6_tracerouteResult = "";
			
			ipv4_tracerouteResult = cmdLinuxTraceroute(commandGW, ipv4, hopCount);
			ipv6_tracerouteResult = cmdLinuxTraceroute(commandGW, ipv6, hopCount); 
        	
			cmdResult = "[IPv4]\n" + ipv4_tracerouteResult + "\n\n" + "[IPv6]\n" + ipv6_tracerouteResult;
		} else {
			return "FAIL";
		}
		
		return cmdResult;
    }
    
    public Hashtable doMCUSaveSchedule(MCU mcu, Hashtable props)
            throws Exception {

        DefaultConf defaultConf = DefaultConf.getInstance();
//        Hashtable props = defaultConf.getDefaultProperties("MCUScheduleVar");

        // MIBUtil mibUtil = MIBUtil.getInstance();
        List<String> property = new ArrayList<String>(props.size());
        List<String> value = new ArrayList<String>(props.size());
        Iterator it = props.keySet().iterator();
        for (int i = 0; it.hasNext(); i++) {
            try {

                String key = (String) it.next();
                // property[i] = (mibUtil.getOid(key)).getValue();
                property.add(key);
                value.add(BeanUtils.getProperty(props, key));

                log.debug("props[" + i + "] :" + key + " ,oid= " + property.get(i)
                        + ",value=" + value.get(i));
            } catch (Exception e) {
            }
        }

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcu.getSysID(), null, null));
            gw.cmdStdSet1(mcu.getSysID(), property, value);

        } catch (Exception e) {
            throw e;
        }

        return props;
    }
    
    public Map doMCUImportSchedule(MCU mcu) {
        
        log.info("doMCUImportSchedule MCU["+mcu.getSysID()+"] ");
        
        List<String> mibPropNames = new ArrayList<String>(); 
        
        mibPropNames.add("varMeterDayMask"); // 검침 일자 마스크 (0Bit = 1일, 30Bit=31일)
        mibPropNames.add("varMeterHourMask"); // 검침 시간 마스크 (0Bit = 0시, 22Bit=23시)
        mibPropNames.add("varMeterStartMin"); // GE 이벤트 READ 일자 마스크 (0Bit = 1일, 30Bit=31일
        mibPropNames.add("varMeteringPeriod"); // 정기 검침 시도 시간 (분)
        mibPropNames.add("varMeteringRetry"); // 정기 검침 재시도 횟수 (회)
        mibPropNames.add("varEnableReadMeterEvent"); // GE미터의 이벤트 Reading 사용 여부 (0:미사용, 1:사용)
        mibPropNames.add("varEventReadDayMask"); // GE 이벤트 READ 일자 마스크 (0Bit = 1일, 30Bit=31일)
        mibPropNames.add("varEventReadHourMask"); // GE 이벤트 READ 시간 마스크 (0Bit = 0시, 22Bit=23시)
        mibPropNames.add("varMeterTimesyncDayMask"); //미터 시간 동기화 일자 마스크 (0BIT=1일, ..)
                                                    //시작 분, 재시도, Period는 미터 상태 검사와 같은 시간에
                                                    //사용, 미터시간동기화나 상태 검사는 동시 처리가 가능
        mibPropNames.add("varMeterTimesyncHourMask"); // 미터 시간 동기화 시간 마스크 (0BIT=0시, ..)
        mibPropNames.add("varEnableAutoUpload"); // 검침 데이터 자동 전송 여부 (0:미사용, 1:사용)

        mibPropNames.add("varMeterUploadStartHour"); // 검침 데이터 전송 시작 시간(시) (0~23)
        mibPropNames.add("varMeterUploadStartMin"); // 검침 데이터 전송 시작 시간(분) (0~59)
        mibPropNames.add("varUploadTryTime"); // varMeterUploadTryTime
        mibPropNames.add("varMeterUploadRetry"); // 검침 데이터 전송 실패시 반복 횟수
        mibPropNames.add("varMeterUploadCycleType"); // 검침 데이터 전송 주기 형식
        mibPropNames.add("varMeterUploadCycle"); //검침 데이터 전송 주기 (2.2.45에 따름)
        
        mibPropNames.add("varEnableRecovery"); // 실패 검침 기능 사용 여부 (0:미사용, 1:사용)
        mibPropNames.add("varRecoveryDayMask"); // 실패 검침 일자 마스크 (0Bit = 1일, 30Bit=31일)
        mibPropNames.add("varRecoveryHourMask"); // 실패 검침 시간 마스크 (0Bit = 0시, 22Bit=23시)
        mibPropNames.add("varRecoveryStartMin"); // 실패 검침 시작 시간 (분)
        mibPropNames.add("varRecoveryPeriod"); // 실패 검침 시도 시간 (분)
        mibPropNames.add("varRecoveryRetry"); // 실패 검침 재시도 횟수 (회)
        
        String[] props = (String[]) mibPropNames.toArray(new String[0]);
        
        Map resProps = new Hashtable();
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcu.getSysID(), null, null));
            
            ResponseMap rmap = gw.cmdStdGet1(mcu.getSysID(), mibPropNames);
            for (ResponseMap.Response.Entry e : rmap.getResponse().getEntry()) {
                resProps.put(e.getKey(), e.getValue());
            }
        }
        catch(Exception ex) {
            log.error(ex,ex);
        }
        
        return resProps; 
    }

    public String cmdSensorLPLogRecovery(String mdsId, String dcuNo,
            String modemNo, double meteringValue, int lpInterval, int[] lplist)
            throws Exception {

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(dcuNo, mdsId, modemNo));

            List<Integer> _lplist = new ArrayList<Integer>();
            for (int lp : lplist) {
                _lplist.add(lp);
            }
            return gw.cmdSensorLPLogRecovery(mdsId, dcuNo, modemNo,
                    meteringValue, lpInterval, _lplist);
        } catch (Exception e) {
            throw e;
        }

    }

    public boolean doModemUpdate(Map<String, Object> param)
            throws Exception {
        boolean force = false;

        String mcuId = (String) param.get("mcuId");
        String modemId = (String) param.get("modemId");
        int revisionNumber = (Integer) param.get("revisionNumber");
        int fw = (Integer) param.get("fw");
        int buildNumber = (Integer) param.get("buildNumber");
        try {
            MCU mcu = mcuDao.get(mcuId);
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));

            if (param.containsKey("manualEnable")) {
                byte val = DataUtil.getByteToInt((Integer) param
                        .get("manualEnable"));
                gw.cmdSetModemROM(mcuId, modemId,
                        ModemROM.OFFSET_MANUAL_ENABLE, new byte[] { val });
            }

            if (param.containsKey("channelId")) {
                byte val = DataUtil.getByteToInt((Integer) param
                        .get("channelId"));
                gw.cmdSetModemROM(mcuId, modemId, ModemROM.OFFSET_CHANNEL,
                        new byte[] { val });
            }

            if (param.containsKey("panId")) {
                byte val = DataUtil.getByteToInt((Integer) param.get("panId"));
                gw.cmdSetModemROM(mcuId, modemId, ModemROM.OFFSET_PANID,
                        new byte[] { val });
            }

            if (param.containsKey("securityEnable")) {
                byte val = DataUtil.getByteToInt((Integer) param
                        .get("securityEnable"));
                gw.cmdSetModemROM(mcuId, modemId,
                        ModemROM.OFFSET_SECURITY_ENABLE, new byte[] { val });
            }

            if (param.containsKey("txPower")) {
                byte val = DataUtil
                        .getByteToInt((Integer) param.get("txPower"));
                gw.cmdSetModemROM(mcuId, modemId, ModemROM.OFFSET_TXPOWER,
                        new byte[] { val });
            }

            if (param.containsKey("linkKey")) {
                byte[] val = Hex.encode((String) param.get("linkKey"));
                gw
                        .cmdSetModemROM(mcuId, modemId,
                                ModemROM.OFFSET_LINK_KEY, val);
            }

            if (param.containsKey("networkKey")) {
                byte[] val = Hex.encode((String) param.get("networkKey"));
                gw.cmdSetModemROM(mcuId, modemId, ModemROM.OFFSET_NETWORK_KEY,
                        val);
            }

            if (param.containsKey("unitSerial")) {

                String val = (String) param.get("unitSerial");
                gw.cmdSetModemROM(mcuId, modemId,
                        ModemROM.OFFSET_METER_SERIAL_NUMBER, val.getBytes());
            }

            if (param.containsKey("vendor")) {

                String val = (String) param.get("vendor");
                gw.cmdSetModemROM(mcuId, modemId, ModemROM.OFFSET_VENDOR, val
                        .getBytes());
            }
            if (param.containsKey("customerName")) {

                String val = (String) param.get("customerName");
                gw.cmdSetModemROM(mcuId, modemId,
                        ModemROM.OFFSET_CUSTOMER_NAME, val.getBytes());
            }

            if (param.containsKey("consumptionLocation")) {

                String val = (String) param.get("consumptionLocation");
                gw.cmdSetModemROM(mcuId, modemId,
                        ModemROM.OFFSET_CONSUMPTION_LOCATION, val.getBytes());
            }

            if (param.containsKey("fixedReset")) {
                byte val = DataUtil.getByteToInt((Integer) param
                        .get("fixedReset"));
                gw.cmdSetModemROM(mcuId, modemId, ModemROM.OFFSET_FIXED_RESET,
                        new byte[] { val });
            }
            if (param.containsKey("testFlag")) {
                byte val = DataUtil.getByteToInt((Integer) param
                        .get("testFlag"));
                gw.cmdSetModemROM(mcuId, modemId, ModemROM.OFFSET_TEST_FLAG,
                        new byte[] { val });
            }

            if (param.containsKey("repeatingDay")) {

                Mask mask = new Mask(Integer.parseInt((String) param
                        .get("repeatingDay")));
                byte[] val = ModemROM.makeDayToByte(mask.getMaskBits());
                gw.cmdSetModemROM(mcuId, modemId,
                        ModemROM.OFFSET_REPEATING_DAY, val);
            }

            if (param.containsKey("repeatingHour")) {

                Mask mask = new Mask(Integer.parseInt((String) param
                        .get("repeatingHour")));
                byte[] val = ModemROM.makeDayToByte(mask.getMaskBits());
                gw.cmdSetModemROM(mcuId, modemId,
                        ModemROM.OFFSET_REPEATING_HOUR, val);
            }

            if (param.containsKey("repeatingSetupSec")) {

                byte[] val = DataUtil.get2ByteToInt((Integer) param
                        .get("repeatingSetupSec"));
                gw.cmdSetModemROM(mcuId, modemId,
                        ModemROM.OFFSET_REPEATING_SETUP_SEC, val);
            }


        } catch (Exception e) {
            throw e;
        }

        return true;
    }

    public boolean doModemScheduleUpdate(Map<String, Object> param)
            throws Exception {
        boolean force = false;

        String mcuId = (String) param.get("mcuId");
        String modemId = (String) param.get("modemId");
        int revisionNumber = (Integer) param.get("revisionNumber");
        int fw = (Integer) param.get("fw");
        int buildNumber = (Integer) param.get("buildNumber");
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));


            if (param.containsKey("lpChoice")) {
                byte[] val = new byte[] { DataUtil.getByteToInt((Integer) param
                        .get("lpChoice")) };
                // gw.cmdSetSensorROM(mcuId, sensorId,
                // SensorROM.OFFSET_LP_CHOICE, val);

                if (revisionNumber < 1703) {
                    gw.cmdCommandModem(mcuId, modemId,
                            ModemCommandData.CMD_TYPE_LP_CHOICE, val);
                } else {
                    gw.cmdCommandModem1(mcuId, modemId,
                            ModemCommandData.CMD_TYPE_LP_CHOICE, fw,
                            buildNumber, force, val);
                }
            }

            if (param.containsKey("meteringDay")) {

                byte[] val = ModemROM.makeDayToByte((String) param
                .get("meteringDay"));
                // gw.cmdSetSensorROM(mcuId, sensorId,
                // SensorROM.OFFSET_METERING_DAY, val);

                if (revisionNumber < 1703) {
                    gw.cmdCommandModem(mcuId, modemId,
                            ModemCommandData.CMD_TYPE_METERING_DAY, val);
                } else {
                    gw.cmdCommandModem1(mcuId, modemId,
                            ModemCommandData.CMD_TYPE_METERING_DAY, fw,
                            buildNumber, force, val);
                }
            }

            if (param.containsKey("meteringHour")) {

                byte[] val = ModemROM.makeHourToByte((String) param
                        .get("meteringHour"));
                // gw.cmdSetSensorROM(mcuId, sensorId,
                // SensorROM.OFFSET_METERING_HOUR, val);

                if (revisionNumber < 1703) {
                    gw.cmdCommandModem(mcuId, modemId,
                            ModemCommandData.CMD_TYPE_METERING_HOUR, val);
                } else {
                    gw.cmdCommandModem1(mcuId, modemId,
                            ModemCommandData.CMD_TYPE_METERING_HOUR, fw,
                            buildNumber, force, val);
                }
            }
        } catch (Exception e) {
            throw e;
        }

        return true;
    }

    public boolean doZEUPLSScheduleUpdate(String mcuId,
            int revisionNumber, ZEUPLS zeupls, String prop, Integer fw,
            Integer buildNumber) throws Exception {

        ModemCommandData data = new ModemCommandData();
        boolean force = false;
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, zeupls.getDeviceSerial()));
            if (prop.equals("meteringDay")) {
                data.setCmdType(ModemCommandData.CMD_TYPE_METERING_DAY);
                byte[] val = ModemROM.makeDayToByte(zeupls.getMeteringDay());
                val[val.length - 1] = (byte) (val[val.length - 1] | 0x01); // 0
                // bit(value
                // 1)
                data.setData(val);
            } else if (prop.equals("meteringHour")) {
                data.setCmdType(ModemCommandData.CMD_TYPE_METERING_HOUR);
                byte[] val = ModemROM.makeHourToByte(zeupls.getMeteringHour());
                data.setData(val);
            }

            else if (prop.equals("lpChoice")) {
                data.setCmdType(ModemCommandData.CMD_TYPE_LP_CHOICE);
                byte[] val = new byte[] { DataUtil.getByteToInt(zeupls
                        .getLpChoice()) };
                data.setData(val);
            } else if (prop.equals("lpPeriod")) {
                data.setCmdType(ModemCommandData.CMD_TYPE_LP_PERIOD);
                byte[] val = new byte[] { DataUtil.getByteToInt(zeupls
                        .getLpPeriod()) };
                data.setData(val);
                // MOINSTANCE meter = EMUtil.getMeterMOBySensorId(sensorId, 0);
                // if (meter != null)
                // IUtil.setPropertyValue(meter.getName(), "resolution",
                // 60/Integer.parseInt(element.getCurrentValue())+"");
            } else if (prop.equals("alarmFlag")) {
                data.setCmdType(ModemCommandData.CMD_TYPE_ALARM_FLAG);
                byte[] val = new byte[] { DataUtil.getByteToInt(zeupls
                        .getAlarmFlag()) };
                data.setData(val);
            }
            if (data.getCmdType() != (byte) 0x99) {
                Set<Meter> meterList = zeupls.getMeter();

                if (meterList == null || meterList.size() == 0) {
                    throw new Exception("Related Meter Dose Not Existed");
                }

                String meterType = "";
                for (Iterator iter = meterList.iterator(); iter.hasNext();) {
                    Meter meter = (Meter) iter.next();
                    meterType += meter.getMeterType();
                }

                log.debug("meterType=" + meterType + ",modemId="
                        + zeupls.getDeviceSerial());
                if (meterType.indexOf("VolumeCorrector") >= 0) {

                    if (data.getCmdType() == ModemCommandData.CMD_TYPE_METERING_HOUR) {
                        log.info("cmdType["
                                + Hex.decode(new byte[] { data.getCmdType() })
                                + "] DataStream[" + Hex.decode(data.getData())
                                + "]");
                        boolean isLinkSkip = true;
                        int cmd = 3;// update interval
                        int portNo = 0;// don't care any number

                        byte[] param = data.getData();
                        byte[] frame = ByPassFrameUtil.getVCByPassFrame(zeupls
                                .getDeviceSerial(), portNo, cmd, param);
                        gw.cmdBypassSensor1(mcuId, zeupls.getDeviceSerial(),
                                isLinkSkip, frame);
                    }
                } else {
                    log.info("cmdType["
                            + Hex.decode(new byte[] { data.getCmdType() })
                            + "] DataStream[" + Hex.decode(data.getData())
                            + "]");
                    if (revisionNumber < 1703) {
                        gw.cmdCommandModem(mcuId, zeupls.getDeviceSerial(),
                                data.getCmdType(), data.getData());
                    } else {
                        gw.cmdCommandModem1(mcuId, zeupls.getDeviceSerial(),
                                data.getCmdType(), fw, buildNumber, force, data
                                        .getData());
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }

        return true;
    }

    /**
     * @param mcuId
     * @param drLevelEntry
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdDRAgreement(String mcuId, drLevelEntry drLevelEntry) throws FMPMcuException,Exception {
        log.info("cmdDRAgreement [" + mcuId + "] drLevelEntry["+drLevelEntry+"]");
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdDRAgreement(mcuId, drLevelEntry);
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }

    /**
     * @param mcuId
     * @param deviceId
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdDRCancel(String mcuId, String deviceId) throws FMPMcuException,Exception {
        log.info("cmdDRCancel [" + mcuId + "] deviceId["+deviceId+"]");
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, deviceId, deviceId));
            gw.cmdDRCancel(mcuId, deviceId);
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }

    /**
     * @param mcuId
     * @param idrEntry
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdIDRStart(String mcuId, idrEntry idrEntry) throws FMPMcuException,Exception {
        log.info("cmdIDRStart [" + mcuId + "] idrEntry["+idrEntry+"]");
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdIDRStart(mcuId, idrEntry);
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }

    /**
     * @param mcuId
     * @param eventId
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdIDRCancel(String mcuId, String eventId) throws FMPMcuException,Exception {
        log.info("cmdIDRStart [" + mcuId + "] eventId["+eventId+"]");
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdIDRCancel(mcuId, eventId);
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }

    /**
     * @param mcuId
     * @param deviceId
     * @return
     * @throws FMPMcuException
     * @throws Exception
     */
    public endDeviceEntry cmdGetDRLevel(String mcuId, String deviceId) throws FMPMcuException,Exception {
        log.info("cmdGetDRLevel [" + mcuId + "] deviceId["+deviceId+"]");
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, deviceId, deviceId));
            return gw.cmdGetDRLevel(mcuId, deviceId);
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }

    /**
     * @param mcuId
     * @param endDeviceEntry
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdSetDRLevel(String mcuId, endDeviceEntry endDeviceEntry) throws FMPMcuException,Exception {
        log.info("cmdSetDRLevel [" + mcuId + "] endDeviceEntry["+endDeviceEntry+"]");
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdSetDRLevel(mcuId, endDeviceEntry);
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }

    /**
     * @param mcuId
     * @param serviceId
     * @param deviceId
     * @param eventId
     * @param drLevel
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdEndDeviceControl(String mcuId, String serviceId, String deviceId, String eventId, String drLevel) throws FMPMcuException, Exception {
        log.info("cmdEndDeviceControl [" + mcuId + "] serviceId["+serviceId+"] deviceId[deviceId] eventId[eventId] drLevel[drLevel]");
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, deviceId, deviceId));
            gw.cmdEndDeviceControl(mcuId, serviceId, deviceId, eventId, drLevel);
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }

    public void cmdDigitalInOut(String mcuId, String modemId, byte direction, byte mask, byte value) throws FMPMcuException,Exception {
        log.info("cmdDigitalInOut [" + mcuId + "] modemId["+modemId+"] direction["+direction+"] mask["+mask+"] value["+value+"]");
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));
            gw.cmdDigitalInOut(mcuId, modemId, direction, mask, value);
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }

    /**
     * @param mcuId
     * @param deviceId
     * @return endDeviceEntry
     * @throws FMPMcuException
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public endDeviceEntry cmdGetDRAsset(String mcuId, String deviceId) throws FMPMcuException, Exception {
        log.info("cmdGetDRAsset [" + mcuId + "] deviceId["+deviceId+"]");
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, deviceId, deviceId));
            return gw.cmdGetDRAsset(mcuId, deviceId);
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }
    
    
    /**
     * Broadcast start
     * @param mcuId
     * @param deviceId
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdBroadcast(String mcuId, String deviceId, String message) throws FMPMcuException,Exception {
        log.info("cmdBroadcast [" + mcuId + "] deviceId["+deviceId+"] message[" +message+ "]" );
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, deviceId, deviceId));
            gw.cmdBroadcast(mcuId, deviceId, message);
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }
    
    /**
     * @param modemId
     * @throws Exception 
     */
    public Map<String, Object> cmdGetMeterStatus(String modemId) throws Exception{
        log.info("cmdGetMeterStatus modemid : ["+modemId+"]");
        try {
            Modem modem = modemDao.get(modemId);
            CommandWS gw = CmdManager.getCommandWS(modem.getProtocolType());
            MeterData meterData = gw.cmdGetMeterInfoFromModem(modem.getMcu().getSysID(), modemId);
            
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("meterId", meterData.getMeterId());
            map.put("serviceType", meterData.getServiceType());
            map.put("meterTime", meterData.getTime());
            map.put("type", meterData.getType());
            map.put("vendor", meterData.getVendor());
            return map;
        }catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }
    
    /**
     * @param modemId
     * @throws Exception 
     */
    public Map<String, Object> cmdKDGetMeterStatus(String modemId) throws Exception{
        log.info("cmdKDGetMeterStatus modemid : ["+modemId+"]");
        try {
            Modem modem = modemDao.get(modemId);
            CommandWS gw = CmdManager.getCommandWS(modem.getProtocolType());
            ResponseMap obj = gw.cmdKDGetMeterStatus(modemId);
            
            Map<String, Object> map = new HashMap<String, Object>();
            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("key["+e.getKey()+"], value["+ e.getValue()+"]");
                if("meterStatus".equals(e.getKey().toString())) {
                    map.put(e.getKey().toString(), 2000+Integer.parseInt(e.getValue().toString()));
                } else {
                    map.put(e.getKey().toString(), e.getValue());
                }
            }

            return map;
        }catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }
    
    /**
     * 대상 Device 의 현재 Energy Level 값을 실시간으로 얻어온다.
     * @param mcuId
     * @param sensorId
     * @return
     * @throws Exception 
     */
    public Integer cmdGetEnergyLevel(String mcuId, String sensorId) throws Exception{
        log.info(String.format("cmdGetEnergyLevel MCU ID : [%s], SENSOR ID : [%s]",mcuId,sensorId));
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, sensorId));
            byte result = gw.cmdGetEnergyLevel(mcuId, sensorId);
            return (int)result;
        }catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }
    
    /**
     * 대상 Device 의 현재 Energy Level 값을 실시간으로 설정한다.
     * @param mcuId
     * @param sensorId
     * @param energyLevel
     * @throws Exception 
     */
    public void cmdSetEnergyLevel(String mcuId, String sensorId, Integer energyLevel) throws Exception{
        log.info(String.format("cmdSetEnergyLevel MCU ID : [%s], SENSOR ID : [%s]",mcuId,sensorId));
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, sensorId));
            gw.cmdSetEnergyLevel(mcuId, sensorId, energyLevel.toString());
        }catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }
    
    /**
     * 대상 Device 의 현재 Energy Level 값을 실시간으로 설정한다.
     * @param mcuId
     * @param sensorId
     * @param energyLevel
     * @param meterSerial
     * @throws Exception 
     */
    public void cmdSetEnergyLevel(String mcuId, String sensorId, Integer energyLevel,
            String meterSerial) throws Exception{
        log.info(String.format("cmdSetEnergyLevel MCU ID : [%s], SENSOR ID : [%s]",mcuId,sensorId));
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, sensorId));
            gw.cmdSetEnergyLevel1(mcuId, sensorId, energyLevel.toString(), meterSerial);
        }catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }
    
    /**
     * @param mcuId
     * @param modemId
     * @param valveStatus
     * @return
     * @throws Exception
     */
    public int cmdValveControl(String mcuId, String modemId, int valveStatus) throws Exception{
        log.info(String.format("cmdValveControl mcuid[%s], modemid[%s], valveStatus[%d]",mcuId,modemId,valveStatus));
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));
            return gw.cmdValveControl(mcuId, modemId, valveStatus);
        }catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }
    
    /**
     * @param mcuId
     * @param modemId
     * @param valveStatus
     * @return
     * @throws Exception
     */
    public Map<String, Object> cmdKDValveControl(String mcuId, String modemId, int valveStatus) throws Exception{
        log.info(String.format("cmdKDValveControl mcuid[%s], modemid[%s], valveStatus[%d]",mcuId,modemId,valveStatus));
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, modemId));
            gw.cmdKDValveControl(modemId, valveStatus);
            
            ResponseMap obj = gw.cmdKDGetMeterStatus(modemId);
            
            Map<String, Object> map = new HashMap<String, Object>();
            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("key["+e.getKey()+"], value["+ e.getValue()+"]");
                if("meterStatus".equals(e.getKey().toString())) {
                    map.put(e.getKey().toString(), 2000+Integer.parseInt(e.getValue().toString()));
                } else {
                    map.put(e.getKey().toString(), e.getValue());
                }
            }

            return map;
            
        }catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }
    
    
    public void cmdMeterProgram(String meterSerial, MeterProgramKind kind) throws Exception{
        CommandWS gw;
        try {
            Meter meter = meterDao.get(meterSerial);
            gw = CmdManager.getCommandWS(meter.getModem().getProtocolType());
            
            MeterConfig meterConfig=(MeterConfig) meter.getModel().getDeviceConfig();
            
            MeterProgram mp = MeterProgramDao.getMeterConfigId(meterConfig.getId(),kind);

            String settingStr = mp.getSettings();
        
            MeterProgram _mp = new MeterProgram();
            BeanUtils.copyProperties(_mp, mp);
            com.aimir.fep.command.ws.client.Response response = 
                       gw.cmdMeterProgram(meterSerial, settingStr, _mp);
            switch (response.getType()) {
            case OK:
                break;
            default:
                throw new Exception(response.getMessage());
            }
        } catch (Exception e) {
            log.error(e,e);
            throw new Exception(e);
        }
    }
    

    /**
     * Demand Reset
     * 
     * @param meterSerial
     * @throws FMPMcuException
     * @throws Exception
     */
    public void cmdDemandReset(Integer meterId) throws Exception {
        log.info("cmdDDemandReset [" + meterId + "]");
        try {
            Meter meter = meterDao.get(meterId);
            CommandWS gw = CmdManager.getCommandWS(meter.getModem().getProtocolType());
            com.aimir.fep.command.ws.client.Response response = 
                    gw.cmdDemandReset(meterId);

            switch (response.getType()) {
                case OK:
                    break;
                default:
                    throw new Exception(response.getMessage());
            }
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        }
    }

    public void cmdBypassTimeSync(String modemSerial, String loginId) throws Exception {
        log.info("cmdBypassTimeSync");

        Modem modem = modemDao.get(modemSerial);
        CommandWS gw = CmdManager.getCommandWS(modem.getProtocolType());

        Response response = gw.cmdBypassTimeSync(modemSerial, loginId);
        switch (response.getType()) {
        case OK:
            break;
        default:
            throw new Exception(response.getMessage());
        }
    }

    public void cmdSendSMS(String modemSerial, String ... params) throws Exception{
        log.info("cmdSms");

        try{
            Modem modem = modemDao.get(modemSerial);
            CommandWS gw = CmdManager.getCommandWS(modem.getProtocolType());
            List<String> list = new ArrayList<String>();
            for (String s : params) {
                list.add(s);
            }
            gw.cmdSendSMS(modemSerial, list);
        }
        catch(Exception e){
            log.error(e, e);
            throw e;
        }
    }
    
    /**
     * DOTA 명령을 SMS 메시지 로 보내는 기능.
     * @param modemSerial
     * @param filePath
     * @throws Exception 
     */
    public void cmdSmsFirmwareUpdate(String modemSerial, String filePath) throws Exception {
        log.info("cmdSmsFirmwareUpdate");

        Modem modem = modemDao.get(modemSerial);
        CommandWS gw = CmdManager.getCommandWS(modem.getProtocolType());

        com.aimir.fep.command.ws.client.Response response =
                gw.cmdSmsFirmwareUpdate(modemSerial, filePath);
        switch (response.getType()) {
        case OK:
            break;
        default:
            throw new Exception(response.getMessage());
        }
    }

    /**
     * Bypass mode 로  실행되는 Meter Program 
     * @param meterSerial
     * @param kind
     */
    public void cmdBypassMeterProgram(String meterSerial, MeterProgramKind kind) throws Exception {
        log.info("cmdBypassMeterProgram");
        
        Meter meter = meterDao.get(meterSerial);
        CommandWS gw = CmdManager.getCommandWS(meter.getModem().getProtocolType());

        com.aimir.fep.command.ws.client.Response response =
                gw.cmdBypassMeterProgram(meterSerial,kind);
        switch (response.getType()) {
        case OK:
            break;
        default:
            throw new Exception(response.getMessage());
        }
    }
    
    /**
     * 미터 펌웨어 롤백 기능(SX2미터).<br>
     * @param mcuId
     * @param sensorList
     * @throws Exception 
     */
    public void cmdMeterFactoryReset(String mcuId, String sensorId) throws Exception{
        log.info("cmdMergeAndInstall");
        
        try{
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, sensorId));
            gw.cmdMeterFactoryReset(mcuId, sensorId);
        } catch(Exception e){
            throw new Exception(e);
        }
    }
    
    
    /**
     * method name : cmdSetIHDTable<b/>
     * method Desc : 집중기에 IHD Install Key 등록 명령
     * 
     * @param mcuId
     * @param sensorId
     * @return
     * @throws Exception
     */
    public String cmdSetIHDTable(String mcuId, String sensorId)
            throws Exception {

        log.info("cmdSetIHDTable [" + mcuId + "]," + sensorId);

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, sensorId));
            return gw.cmdSetIHDTable(mcuId, sensorId);
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * method name : cmdDelIHDTable<b/>
     * method Desc : 집중기에 IHD Install Key 삭제 명령
     * 
     * @param mcuId
     * @param sensorId
     * @return
     * @throws Exception
     */
    public String cmdDelIHDTable(String mcuId, String sensorId)
    throws Exception {

        log.info("cmdDelIHDTable [" + mcuId + "]," + sensorId);
        
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, sensorId));
            return gw.cmdDelIHDTable(mcuId, sensorId);
        } catch (Exception e) {
            throw e;
        }
    }

    public void test() {
        try{
        Modem modem = modemDao.get("000D6F000030EF3B");
        CommandWS gw = CmdManager.getCommandWS(modem.getProtocolType());
        gw.cmdKDValveControl("000D6F000030EF3B", 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
        /*com.aimir.fep.command.ws.client.Response response =
                gw.cmdKDValveControl("000D6F000030EF3B", 0);
        switch (response.getType()) {
        case OK:
            break;
        default:
            throw new Exception(response.getMessage());
        }*/
    }
    
    /**
     * method name : cmdSendIHDData
     * method Desc : IHD로 데이터를 보낼때 사용하는 메소드
     * 
     * @param mcuId, sensorId, data
     * @return
     * @throws Exception
     */
    public void cmdSendIHDData(String mcuId, String sensorId, byte[] data) 
            throws Exception {

    	log.debug("[MCU:" + mcuId + " SENSOR_ID: " + sensorId + "] cmdSendIHDData [" + mcuId + "]," + sensorId);

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, sensorId));
            gw.cmdSendIHDData(mcuId, sensorId, data);
        } catch (Exception e) {
            throw e;
        }
    }
    
    public Map<String, Object> relayValveOn(String mcuId, String meterId) throws Exception
    {
        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null));
        ResponseMap obj = gw.relayValveOn(mcuId, meterId);
        
        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
            log.debug("[MCU:" + mcuId + " METER: " + meterId + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
            map.put(e.getKey().toString(), e.getValue());
        }

        return map;
    }
    
    public Map<String, Object> relayValveOff(String mcuId, String meterId) throws Exception
    {
        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null));
        ResponseMap obj = gw.relayValveOff(mcuId, meterId);
        
        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
        	log.debug("[MCU:" + mcuId + " METER: " + meterId + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
            map.put(e.getKey().toString(), e.getValue());
        }

        return map;
    }
    
    public Map<String, Object> relayValveStatus(String mcuId, String meterId) throws Exception
    {
        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null));
        ResponseMap obj = gw.relayValveStatus(mcuId, meterId);
        
        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
        	log.debug("[MCU:" + mcuId + " METER: " + meterId + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
            map.put(e.getKey().toString(), e.getValue());
        }

        return map;
    }
    
    public Map<String, Object> relayValveActivate(String mcuId, String meterId) throws Exception
    {
        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null));
        ResponseMap obj = gw.relayValveActivate(mcuId, meterId);
        
        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
        	log.debug("[MCU:" + mcuId + " METER: " + meterId + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
            map.put(e.getKey().toString(), e.getValue());
        }

        return map;
    }
    
    public Map<String, Object> syncTime(String mcuId, String meterId) throws Exception
    {
        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null));
        ResponseMap obj = gw.syncTime(mcuId, meterId);
        
        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
        	log.debug("[MCU:" + mcuId + " METER: " + meterId + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
            map.put(e.getKey().toString(), e.getValue());
        }

        return map;
    }
    
    
    /**
     * 집중기 리셋
     *
     * @param mcuId
     * @throws Exception
     */
    public void cmdAssembleTestStart(String mcuId) throws Exception {
        log.info("cmdAssembleTestStart MCU[" + mcuId + "]");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            gw.cmdAssembleTestStart(mcuId);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * NI : SORIA sub1Ghz 모뎀에 대한 baudrate 커맨드 전송
     * @param mdsId modem.id != deviceserial
     * @param requestType
     * @param rateValue
     * @return
     * @throws Exception
     */
    public Map<String, Object> cmdMeterBaudRate(String mdsId, String requestType, int rateValue) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mdsId, null, null));
            ResponseMap obj = gw.cmdMeterBaudRate(mdsId,requestType,rateValue);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("[DEVICE SERIAL:" + mdsId + "] REQUEST TYPE: " + requestType + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
        	log.error(e,e);
            map.put("rtnStr", e.getMessage());
            throw e;
        }

        return map;
    }
    
    /**
     * NI : Real Time Metering
     */
    public Map<String, Object> cmdRealTimeMetering(String mdsId, int interval, int duration) throws Exception {
    	log.debug("## NI command - Real Time Metering ["+mdsId+"],["+interval+"],["+duration+"]");
    	
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(null, mdsId, null));
            ResponseMap obj = gw.cmdRealTimeMetering(mdsId, interval, duration);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("[DEVICE SERIAL:" + mdsId + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
        	log.error(e,e);
            map.put("rtnStr", e.getMessage());
            throw e;
        }
        return map;
    }
    
    /**
     * NI : 모뎀의 SNMP TRAP의 on/off를 제어
     * @param mdsId   modem.id != deviceserial
     * @param requestType   SET/GET
     * @param trapStatus    enable(1)/disable(0)
     * @return
     * @throws Exception
     */
    public Map<String, Object> cmdModemSnmpTrap(String mdsId, String requestType, String trapStatus) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mdsId, null, null));
            ResponseMap obj = gw.cmdSnmpTrapOnOff(mdsId,requestType,trapStatus);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("[DEVICE SERIAL:" + mdsId + "] REQUEST TYPE: " + requestType + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
            log.error(e,e);
            map.put("rtnStr", e.getMessage());
            throw e;
        }

        return map;
    }

    /**
     * NI프로토콜을 통한 모뎀의 이벤트로그 수집 (get)
     * @param mdsId
     * @param logCount 요청할 로그의 개수 지정
     * @return
     * @throws Exception
     */
    // UPDATE START SP-681
//    public Map<String, Object> cmdModemEventLog(String mdsId, int logCount) throws Exception {
    public Map<String, Object> cmdModemEventLog(String modemId, int logCount) throws Exception {
    // UPDATE END SP-681
        log.info("cmdModemEventLog START || MODEM[" + modemId + "]");
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            // UPDATE START SP-681
//            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mdsId, null, null));
//            Modem modem = modemDao.get(Integer.parseInt(mdsId));
//            ResponseMap obj = gw.getModemEventLog(mdsId,logCount);
        	
            Modem modem = modemDao.get(Integer.parseInt(modemId));
            String protocolName = modem.getProtocolType() == null ? Protocol.LAN.name() : modem.getProtocolType().name();
            CommandWS gw = CmdManager.getCommandWS(protocolName);        	
        	
            ResponseMap obj = null;
			if (modem.getFwVer().compareTo("1.2") >= 0) {
				obj = gw.getModemEventLog(modemId,logCount, 0);
			} else {
				obj = gw.getModemEventLog(modemId,logCount, -1); // if offset < 0 then old version
			}
			// UPDATE END SP-681
            
            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
    			// UPDATE START SP-681
//                log.debug("[DEVICE SERIAL:" + mdsId + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
                log.debug("[MODEM ID:" + modemId + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
        		// UPDATE END SP-681
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("rtnStr", e.getMessage());
            throw e;
        }

        return map;
    }
    
    public Map<String, Object> 	setCloneOnOff(String modemId, int count) throws Exception {
        log.info("setCloneOnOff START || MODEM[" + modemId + "]");
        Map<String, Object> map = new HashMap<String, Object>();
        
        try {
			CommandWS gw = CmdManager.getCommandWS(getProtocolType(modemId, null, null));
			ResponseMap obj = gw.setCloneOnOff(modemId, count);
 
            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
				log.debug("[DEVICE SERIAL:" + modemId + "] key[" + e.getKey() + "], value[" + e.getValue() + "]");
                map.put(e.getKey().toString(), e.getValue());
            }
            
            map.put("rtnStr", "Get Response From CommandGW");
        } catch (Exception e) {
			log.error(e, e);
            map.put("rtnStr", e.getMessage());
        }

        return map;
    }
    
	// INSERT START SP-681
    public Map<String, Object> 	setCloneOnOffWithTarget(String modemId, String cloneCode, int count, String version, int euiCount, List<String> euiList) throws Exception {
        log.info("setCloneOnOffWithTarget START || MODEM[" + modemId + "]");
        Map<String, Object> map = new HashMap<String, Object>();
        
        try {
//			CommandWS gw = CmdManager.getCommandWS(getProtocolType(modemId, null, null));
            Modem modem = modemDao.get(Integer.parseInt(modemId));
            String protocolName = modem.getProtocolType() == null ? Protocol.LAN.name() : modem.getProtocolType().name();
            CommandWS gw = CmdManager.getCommandWS(protocolName);     
        	ResponseMap obj = gw.setCloneOnOffWithTarget(modemId, cloneCode, count, version, euiCount, euiList);
 
            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
				log.debug("[DEVICE SERIAL:" + modemId + "] key[" + e.getKey() + "], value[" + e.getValue() + "]");
                map.put(e.getKey().toString(), e.getValue());
            }
            
            map.put("rtnStr", "Get Response From CommandGW");
        } catch (Exception e) {
			log.error(e, e);
            map.put("rtnStr", e.getMessage());
        }

        return map;
    }    
	// INSERT END SP-681
    
    /**
     * 외주개발용 커맨드 전송
     * 프로토콜에 따라 작성된 문자열을 대상 집중기에 전송함
     * 집중기에서 해당 문자열 해석하여 노드 혹은 센서 탐색
     *
     * @param mcuId
     * @param generalStream 사전 정의된 프로토콜에 따라 작성된 문자열
     * @throws Exception
     */
    public void cmdExtCommand(String mcuId, byte[] generalStream) throws Exception {
        log.info("cmdExtCommand START || MCU[" + mcuId + "]");

        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            ResponseMap obj = gw.cmdExtCommand(mcuId,generalStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    
    /**
     * this code is not used anymore.
     * @deprecated
     */
 	public Map<String, Object> cmdMultiFirmwareOTA(OTATargetType otaTargetType, List<String> deviceList
		, Protocol modemProtocol, String takeOver, boolean useNullBypass, String firmwareId) throws Exception {
 		
	    log.info("[cmdMultiFirmwareOTA] OTATargetType=[" + otaTargetType + "] DeviceList=[" + deviceList + "], Protocol=[" + modemProtocol.name() 
	    + "], takeOver=[" + takeOver + "] UsingBypass=[" + useNullBypass + "], FirmwareId=[" + firmwareId + "]");
	             
	    CommandWS gw = CmdManager.getCommandWS(modemProtocol);
	    // UPDATE START SP-681
	    //ResponseMap response = gw.cmdMultiFirmwareOTA(otaTargetType.name(), deviceList, takeOver, useNullBypass, firmwareId);
	    ResponseMap response = gw.cmdMultiFirmwareOTA(otaTargetType.name(), deviceList, takeOver, useNullBypass, firmwareId, "", "", "");
	    // UPDATE END SP-681
	
	    Map<String, Object> map = new HashMap<String, Object>();
	    for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
	        map.put(e.getKey().toString(), e.getValue());
	    }        
    	return null;
 	}
 	
	public Map<String, Object> cmdMultiFirmwareOTAImprov(String locationId, OTATargetType otaTargetType, boolean isImmediately
			, String firmwareId, String issueDate, OTAExecuteType otaExecuteType
			, int otaRetryCount, int otaRetryCycle, boolean useAsyncChannel) throws Exception {
		return cmdMultiFirmwareOTAImprov(locationId, otaTargetType, isImmediately, firmwareId, issueDate, otaExecuteType, otaRetryCount, otaRetryCycle, useAsyncChannel, null);	
	}
	
	public Map<String, Object> cmdMultiFirmwareOTAImprov(String locationId, OTATargetType otaTargetType, boolean isImmediately
			, String firmwareId, String issueDate, OTAExecuteType otaExecuteType
			, int otaRetryCount, int otaRetryCycle, boolean useAsyncChannel, String toTargetProperty) throws Exception {

		log.info("[cmdMultiFirmwareOTAImprov] OTATargetType=[" + otaTargetType + "] isImmediately=[" + isImmediately + "]"
				+ ", firmwareId=[" + firmwareId + "] issueDate=[" + issueDate + "]"
				+ ", OTAExecuteType=[" + otaExecuteType + "], otaRetryCount=[" + otaRetryCount + "], otaRetryCycle=[" + otaRetryCycle + "], useAsyncChannel=["+ useAsyncChannel +"], toTargetProperty=[" + toTargetProperty + "]");

        // 스케줄러 등록
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("otaTargetType", otaTargetType);
        params.put("firmwareId", firmwareId);
        params.put("issueDate", issueDate);
        params.put("otaExecuteType", otaExecuteType);
        params.put("useAsyncChannel", useAsyncChannel);
        
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("jobName", "otaScheduleJob");
        conditionMap.put("jobClassName", "com.aimir.schedule.job.OTAScheduleJob");
        conditionMap.put("jobDescription", "this is job description !");
        conditionMap.put("triggerName", "OTAScheduleJobTrigger-" + issueDate + "-" + locationId);
        conditionMap.put("interval", Long.parseLong(String.valueOf(otaRetryCycle)));
        conditionMap.put("repeatCount", otaRetryCount);
        
        if(!isImmediately){
            conditionMap.put("startTime", DateTimeUtil.getDateFromYYYYMMDDHHMMSS(issueDate));
        }
        conditionMap.put("cron", false);
        conditionMap.put("fepProtocol", "IP");
        conditionMap.put("subJobData", params);
        
        if(toTargetProperty != null && !toTargetProperty.equals("")) {
            conditionMap.put("toTargetProperty", toTargetProperty);        	
        }
        
        log.debug("Scheduler Call => " + conditionMap.toString());
        boolean addResult = ScheduleOperationUtil.addJobTrigger2(conditionMap);
        log.debug("Scheduler Finished");
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("result", addResult);
        map.put("resultValue", "Execute OTA Schedule result = " + addResult);
        
        return map;        
	}
	
	
	public Map<String, Object> commandCloneOnOffStart(String locationId, OTATargetType otaTargetType, boolean isImmediately
			, String issueDate, OTAExecuteType otaExecuteType
			, int otaRetryCount, int otaRetryCycle, boolean propagation, ModemCommandType commandType, int cloningTime) throws Exception {

		log.info("[commandCloneOnOffStart] OTATargetType=[" + otaTargetType + "] isImmediately=[" + isImmediately + "]"
				+ ", issueDate=[" + issueDate + "]"
				+ ", OTAExecuteType=[" + otaExecuteType + "], otaRetryCount=[" + otaRetryCount + "], otaRetryCycle=[" + otaRetryCycle + "]"
				+ ", Propagation=[" + propagation + "], ModemCommandType=[" + commandType.name() + "], CloningTime=[" + cloningTime + "]");

        // 스케줄러 등록
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("otaTargetType", otaTargetType);
        params.put("issueDate", issueDate);
        params.put("otaExecuteType", otaExecuteType);
        
        params.put("propagation", propagation);
        params.put("modemCommandType", commandType);
        params.put("cloningTime", cloningTime);
        
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("jobName", "cloneOnOffScheduleJob");
        conditionMap.put("jobClassName", "com.aimir.schedule.job.CloneOnOffScheduleJob");
        conditionMap.put("jobDescription", "this is job description !");
        conditionMap.put("triggerName", "CloneOnOffScheduleJobTrigger-" + issueDate + "-" + locationId);
        conditionMap.put("interval", Long.parseLong(String.valueOf(otaRetryCycle)));
        conditionMap.put("repeatCount", otaRetryCount);
        
        if(!isImmediately){
            conditionMap.put("startTime", DateTimeUtil.getDateFromYYYYMMDDHHMMSS(issueDate));
        }
        conditionMap.put("cron", false);
        conditionMap.put("fepProtocol", "IP");
        conditionMap.put("subJobData", params);
        
        log.debug("Scheduler Call => " + conditionMap.toString());
        boolean addResult = ScheduleOperationUtil.addJobTrigger2(conditionMap);
        log.debug("Scheduler Finished");
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("result", addResult);
        map.put("resultValue", "Execute Clone On/Off Schedule result = " + addResult);
        
        return map;        
	}
	

	// INSERT START SP-681
	public Map<String, Object> cmdMultiFirmwareAdvancedOTA(OTATargetType otaTargetType, List<String> deviceList
			, Protocol modemProtocol, String takeOver, boolean useNullBypass, String firmwareId
			, String optVersion, String optModel, String optTime) throws Exception {
        log.info("[cmdMultiFirmwareOTA] OTATargetType=[" + otaTargetType + "] DeviceList=[" + deviceList + "], Protocol=[" + modemProtocol.name() 
        + "], takeOver=[" + takeOver + "] UsingBypass=[" + useNullBypass + "], FirmwareId=[" + firmwareId 
        + "], optVersion=[" + optVersion + "], optModel=[" + optModel + "], optTime=[" + optTime + "]");

        CommandWS gw = CmdManager.getCommandWS(modemProtocol);
        ResponseMap response = gw.cmdMultiFirmwareOTA(otaTargetType.name(), deviceList, takeOver, 
        												useNullBypass, firmwareId, 
        												optVersion, optModel, optTime);

        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
            map.put(e.getKey().toString(), e.getValue());
        }
        
        return map;
		
	}

	public Map<String, Object> cmdReqNodeUpgrade(String mcuId, int upgradeType, int control,
			String imageKey, String imageUrl, String upgradeCheckSum, 
			List<String> filterValue) throws Exception {
        log.info("[cmdReqNodeUpgrade] McuId=[" + mcuId + "] UpgradeType=[" + upgradeType  
        + "], Control=[" + control + "] ImageKey=[" + imageKey + "], ImageUrl=[" + imageUrl 
        + "], UpgradeCheckSum=[" + upgradeCheckSum + "]");

        
        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
        ResponseMap response = gw.cmdReqNodeUpgrade(mcuId, upgradeType, control, 
        		imageKey, imageUrl, upgradeCheckSum, filterValue);

        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
            map.put(e.getKey().toString(), e.getValue());
        }
        
        return map;
		
	}	
	
	public Map<String, Object> cmdReqImagePropagate(String mcuId, int upgradeType, int control,
			String imageKey, String imageUrl, String upgradeCheckSum, 
			String imageVersion, String targetModel , int cloneCount, List<String> filterValue) throws Exception {
        log.info("[cmdReqNodeUpgrade] McuId=[" + mcuId + "] UpgradeType=[" + upgradeType  
        + "], Control=[" + control + "] ImageKey=[" + imageKey + "], ImageUrl=[" + imageUrl 
        + "], UpgradeCheckSum=[" + upgradeCheckSum + "], ImageVersion=[" + imageVersion
        + "], TargetModel=[" + targetModel + "]"
        + "], CloneCount=[" + cloneCount + "]");

        
        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
        ResponseMap response = gw.cmdReqImagePropagate(mcuId, upgradeType, control, 
        		imageKey, imageUrl, upgradeCheckSum, imageVersion, targetModel, cloneCount, filterValue);

        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
            map.put(e.getKey().toString(), e.getValue());
        }
        
        return map;
		
	}		

	public Map<String, Object> cmdGetImagePropagateInfo(String mcuId, int upgradeType)
			throws Exception {
        log.info("[cmdGetImagePropagateInfo] McuId=[" + mcuId + "] UpgradeType=[" + upgradeType  
        + "],");

        
        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
        ResponseMap response = gw.cmdGetImagePropagateInfo(mcuId, upgradeType);

        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
            log.debug("key["+e.getKey()+"], value["+ e.getValue()+"]");
            map.put(e.getKey().toString(), e.getValue());
        }
        return map;
		
	}
		
	// INSERT END SP-681
	
    /**
     * 
     * Multi Device Meter F/W Version Check
     * @param meterId
     * @param modemProtocol
     * @return
     * @throws Exception
     */
	public Map<String, Object> cmdGetMeterFWVersion(String meterId, Protocol modemProtocol) throws Exception {
        log.info("[cmdGetMeterFWVersion] Meter mdsId=" + meterId + ", Protocol=" + modemProtocol.name());

        CommandWS gw = CmdManager.getCommandWS(modemProtocol);
        ResponseMap response = gw.cmdGetMeterFWVersion(meterId);

        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
            map.put(e.getKey().toString(), e.getValue());
        }

        return map;
        
        

        
        
        /*
         * 임시 테스트 코드
         */
        // 스케줄러 등록
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("meterId", meterId);
//        params.put("modemProtocol", modemProtocol);
//        
//        Map<String, Object> conditionMap = new HashMap<String, Object>();
//
//        conditionMap.put("jobName", "OTAScheduleJob");
//        conditionMap.put("jobClassName", "com.aimir.schedule.job.OTAScheduleJob");
//        conditionMap.put("jobDescription", "this is job description !");
////        conditionMap.put("jobGroup", "DCU_001");
////        conditionMap.put("groupType", "DCU");
//        conditionMap.put("triggerName", "OTA_DCU_001_trigger");
//        conditionMap.put("interval", 180000);
//        conditionMap.put("repeatCount", 3);
////        conditionMap.put("startTime", startTime);
////        conditionMap.put("endTime", endTime);
////        conditionMap.put("expression", expression);
//        conditionMap.put("cron", false);
// //       conditionMap.put("loginId", loginId);
//        conditionMap.put("params", params);
//        
//        log.debug("SC Call => " + conditionMap.toString());
//        boolean addResult = ScheduleOperationUtil.addJobTrigger(conditionMap);
//        log.debug("SC Finished");
//        
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("result", addResult);
//        map.put("resultValue", "Test Good? : " + addResult);
//        
//        return map;
		
	}
	
    /**
     * 
     * Meter key Check
     * @param meterId
     * @param modemProtocol
     * @return
     * @throws Exception
     */
	public Map<String, Object> cmdSORIAGetMeterKey(String meterId, Protocol modemProtocol) throws Exception {
        log.info("[cmdSORIAGetMeterKey] Meter mdsId=" + meterId + ", Protocol=" + modemProtocol.name());

        CommandWS gw = CmdManager.getCommandWS(modemProtocol);
        ResponseMap response = gw.cmdSORIAGetMeterKey(meterId);

        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
            map.put(e.getKey().toString(), e.getValue());
        }
        
        return map;
		
	}
	
    /**
     * 
     * Meter key Check
     * @param meterId
     * @param modemProtocol
     * @return
     * @throws Exception
     */
	public Map<String, Object> cmdSORIASetMeterSerial(String eui, Protocol modemProtocol) throws Exception {
        log.info("[cmdSORIASetMeterSerial] Modem EUI =" + eui + ", Protocol=" + modemProtocol.name());

        CommandWS gw = CmdManager.getCommandWS(modemProtocol);
        ResponseMap response = gw.cmdSORIASetMeterSerial(eui);

        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
            map.put(e.getKey().toString(), e.getValue());
        }
        
        return map;
		
	}

    public String cmdMCUGetLog(String mcuId, int count) throws Exception {
        
        log.info("cmdMCUGetLog MCU["+mcuId+"] count["+count+"]");
        String resProps = "";
	    try {
	        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
	        resProps = gw.cmdMcuGetLog(mcuId, count);
	    }
	    catch(Exception e) {
	        log.error(e,e);
	    	throw e;
	    }
	    
	    return resProps; 
    }
    
   public String cmdForceUpload(String mcuId, String serverName, int dataCount, String fromDate, String toDate) throws Exception {
        
        log.info("cmdForceUpload serverName["+serverName+"] dataCount["+dataCount+"] fromDate["+fromDate+"] toDate["+toDate+"]");
        String resProps = "";
	    try {
	        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
	        resProps = gw.cmdForceUpload(mcuId, serverName, dataCount, fromDate, toDate);
	    }
	    catch(Exception e) {
	        log.error(e,e);
	    	throw e;
	    }
	    
	    return resProps; 
    }
    
	public Map<String, Object> cmdMCUGetSchedule(String mcuId, String name) throws Exception {
        log.info("cmdMcuGetSchedule MCU["+mcuId+"] Name["+name+"]");

        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
        ResponseMap response = gw.cmdMcuGetSchedule(mcuId, name);

        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
            map.put(e.getKey().toString(), e.getValue());
        }
        
        return map;
		
	}

    public void cmdMcuSetSchedule(String mcuId, List<ArrayList<String>> scheduleList) throws Exception {
        log.info("cmdMcuSetSchedule MCU[" + mcuId + "]");        
        
        List<StringArray> saList = new ArrayList<StringArray>();
        
        for(ArrayList<String> schedule : scheduleList) {       
	        StringArray sa = new StringArray();
	        sa.getItem().add(schedule.get(0).toString());
	        sa.getItem().add(schedule.get(1).toString());
	        sa.getItem().add(schedule.get(2).toString());
	        sa.getItem().add(schedule.get(3).toString());
	        saList.add(sa);
        }
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            ResponseMap obj = gw.cmdMcuSetSchedule(mcuId, saList);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    // get mcu retry interval time (cmdGetProperty 109.1.1)
    public Map<String, Object> cmdMcuGetProperty(String mcuId, String name) throws Exception {
        log.info("cmdGetProperty MCU["+mcuId+"] Name["+name+"]");

        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
        ResponseMap response = gw.cmdMcuGetProperty(mcuId, name);

        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
            map.put(e.getKey().toString(), e.getValue());
        }
        
        return map;
		
	}

    // set mcu retry interval time (cmdSetProperty 109.1.2)
    public Map<String, Object> cmdMcuSetProperty(String mcuId, String[] key, String[] keyValue) throws Exception {
        log.info("cmdSetProperty MCU[" + mcuId + "]");

        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
        ResponseMap response = gw.cmdMcuSetProperty(mcuId, key, keyValue);

        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
            map.put(e.getKey().toString(), e.getValue());
        }

        return map;
    }
    
	public Map<String, Object> cmdMcuStdGet(String mcuId, String oid) throws Exception {
        log.info("cmdMcuStdGet MCU["+mcuId+"] OID["+oid+"]");
        
        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
        ResponseMap response = gw.cmdMcuStdGet(mcuId, oid);
        
        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
            map.put(e.getKey().toString(), e.getValue());
        }
        
        return map;
	}
	
	public Map<String, Object> cmdGetMcuNMSInformation(String mcuId) throws Exception {
        log.info("cmdGetMcuNMSInformation MCU["+mcuId+"]");
        
        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
        ResponseMap response = gw.cmdGetMcuNMSInformation(mcuId);
        
        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
            map.put(e.getKey().toString(), e.getValue());
        }
        
        log.debug("map : " + map);
        return map;
	}
    
    public List<Map<String, Object>> cmdMeterParamGet(String modemId, String param, Protocol modemProtocol) throws Exception {
    	log.info("cmdMeterParamGet MeterId[" + modemId + "]");        
    	List<Map<String, Object>> returnList = new LinkedList<Map<String, Object>>();
        CommandWS gw = CmdManager.getCommandWS(modemProtocol);
        ResponseMap response = gw.cmdMeterParamGet(modemId, param);
        
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
        	Map<String, Object> map = new LinkedHashMap<String,Object>();
        	map.put("paramType",e.getKey().toString());
            map.put("paramValue", e.getValue());
            returnList.add(map);
        }
        
        return returnList;
    }
    
    public List<Map<String, Object>> cmdMeterParamAct(String modemId, String param, Protocol modemProtocol) throws Exception {
    	log.info("cmdMeterParamAct MeterId[" + modemId + "]");        
    	List<Map<String, Object>> returnList = new LinkedList<Map<String, Object>>();
        CommandWS gw = CmdManager.getCommandWS(modemProtocol);
        ResponseMap response = gw.cmdMeterParamAct(modemId, param);
        
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
        	Map<String, Object> map = new LinkedHashMap<String,Object>();
        	map.put("paramType",e.getKey().toString());
            map.put("paramValue", e.getValue());
            returnList.add(map);
        }
        
        return returnList;
    }
    
    private JsonElement StringToJsonArray(String str) {
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(str);
    }
    
    public List<Map<String, Object>> cmdMeterParamSet(String modemId, String param, Protocol modemProtocol) throws Exception {
    	log.info("cmdMeterParamSet ModemId[" + modemId + "]");
    	List<Map<String, Object>> returnList = new LinkedList<Map<String, Object>>();

        CommandWS gw = CmdManager.getCommandWS(modemProtocol);
        ResponseMap response = gw.cmdMeterParamSet(modemId, param);
        
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
        	Map<String, Object> map = new LinkedHashMap<String,Object>();
        	map.put("paramType",e.getKey().toString());
            map.put("paramValue", e.getValue());
            returnList.add(map);
        }
        
        return returnList;
    }

    public String cmdSendEventByFep(long logId, String eventStatusName) throws Exception {
        log.info("cmdSendEventByFep START || EventLog[" + logId + "]");
        String obj = "FAIL";
        try {
            CommandWS gw = CmdManager.getCommandWS(Protocol.IP);
            obj = gw.cmdSendEventByFep(logId, eventStatusName);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return obj;
    }

    // INSET START SP-179
    @SuppressWarnings("unchecked")
    public Map cmdGetMeteringData(Meter meter, int serviceType, String operator,
            String nOption, String fromDate, String toDate, String[] modemArray) throws Exception {
        log.info("doOnDemand meter[" + meter.getMdsId() + "]");
        String sensorInstanceName = null;
        Map result = new HashMap();
        
        fromDate = (fromDate == null || "".equals(fromDate)) ? TimeUtil
                .getCurrentDay()
                : fromDate;
        toDate = (toDate == null || "".equals(toDate)) ? TimeUtil
                .getCurrentDay()
                : toDate;

        DataConfiguration config = null;
        try {
        	config = new DataConfiguration(new PropertiesConfiguration("command.properties"));
        } catch (ConfigurationException e) {
        	try {
        		config = new DataConfiguration(new PropertiesConfiguration("config/command.properties"));
            }
            catch (ConfigurationException ee) {
            	log.error(e, e);
            }
        }
        try {
            CommandWS gw = null;

            Modem modem = meter.getModem();
            String meterId = meter.getMdsId();
            String modemId = modem.getId().toString();
            boolean isAsync = false;
            long trId = 0;

            String mcuId = null;
            if (modem.getMcu() != null)
                mcuId = modem.getMcu().getSysID();
            
            
            ModemType modemType = ModemType.Unknown;
            if (modem != null) {
                modemType = modem.getModemType();
            }

            MeterData emd = null;

            modem = meter.getModem();

            modemId = modem.getDeviceSerial();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            int days = (int) ((formatter.parse(toDate.substring(0, 8)).getTime() - formatter.parse(fromDate.substring(0, 8)).getTime())/1000/3600/24 + 1);
            
            int dcuHandshakeTimeout = Integer.valueOf((String) config.getProperty("dcu.timeout.handshaking"))*1000;
            int dcuDayTimeout = Integer.valueOf((String) config.getProperty("dcu.timeout.day"))*1000;
            int LPinterval = meter.getLpInterval();
            
            String timeout = String.valueOf(dcuHandshakeTimeout + (dcuDayTimeout * (60 / LPinterval) * days));
            
            gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null), timeout);
            sensorInstanceName = modem.getDeviceSerial();
            emd = gw.cmdGetMeteringData(mcuId, meterId, modemId, nOption, fromDate, toDate, modemArray);

            if (!isAsync) {
                if(emd != null && emd.getMap() != null){
                    String detailInfo = getOndemandDetailHTML(emd.getMap());
//                    log.info("detailInfo[" + detailInfo + "]");
                    result.put("detail", detailInfo);
                    result.put("rawMap", emd.getMap());
                    modem.setCommState(1);
                }
            } else {
                result.put("commandMethod", "AsynchronousCall");
                result.put("transactionId", "" + trId);
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
            result.put("rawMap", null);
            log.error(ex, ex);
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public Map cmdGetMeteringDataToNeighborDCU(String mcuId, Meter meter, int serviceType, String operator,
            String nOption, String fromDate, String toDate, String[] modemArray) throws Exception {
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
            boolean isAsync = false;
            long trId = 0;
           
            
            ModemType modemType = ModemType.Unknown;
            if (modem != null) {
                modemType = modem.getModemType();
            }

            MeterData emd = null;

            modem = meter.getModem();

            modemId = modem.getDeviceSerial();
            
            gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null));
            sensorInstanceName = modem.getDeviceSerial();
            emd = gw.cmdGetMeteringDataToNeighborDCU(mcuId, meterId, modemId, nOption, fromDate, toDate, modemArray);

            if (!isAsync) {
                if(emd != null && emd.getMap() != null){
                    String detailInfo = getOndemandDetailHTML(emd.getMap());
                    log.info("detailInfo[" + detailInfo + "]");
                    result.put("detail", detailInfo);
                    result.put("rawMap", emd.getMap());
                    modem.setCommState(1);
                }
            } else {
                result.put("commandMethod", "AsynchronousCall");
                result.put("transactionId", "" + trId);
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
            result.put("rawMap", null);
            log.error(ex, ex);
        }
        
        return result;
    }

    @SuppressWarnings("unchecked")
    public Map cmdGetROMRead(Meter meter, int serviceType, String operator,
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
        DataConfiguration config = null;
        try {
        	config = new DataConfiguration(new PropertiesConfiguration("command.properties"));
        } catch (ConfigurationException e) {
        	try {
        		config = new DataConfiguration(new PropertiesConfiguration("config/command.properties"));
            }
            catch (ConfigurationException ee) {
                log.error(e, e);
            }
        }
        try {
            CommandWS gw = null;

            Modem modem = meter.getModem();
            String meterId = meter.getMdsId();
            String modemId = modem.getId().toString();
            boolean isAsync = false;
            long trId = 0;

            String mcuId = null;
            if (modem.getMcu() != null)
                mcuId = modem.getMcu().getSysID();
            
            
            ModemType modemType = ModemType.Unknown;
            if (modem != null) {
                modemType = modem.getModemType();
            }

            MeterData emd = null;

            modem = meter.getModem();

            modemId = modem.getDeviceSerial();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            int days = (int) ((formatter.parse(toDate.substring(0, 8)).getTime() - formatter.parse(fromDate.substring(0, 8)).getTime())/1000/3600/24 + 1);
            
            int modemHandshakeTimeout = Integer.valueOf((String) config.getProperty("modem.timeout.handshaking"))*1000;
            int modemDayTimeout = Integer.valueOf((String) config.getProperty("modem.timeout.day"))*1000;
            int LPinterval = meter.getLpInterval();

            String timeout = String.valueOf(modemHandshakeTimeout + (modemDayTimeout * (60 / LPinterval) * days));
            
            gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null), timeout);
            sensorInstanceName = modem.getDeviceSerial();
            emd = gw.cmdGetROMRead(mcuId, meterId, modemId, nOption, fromDate, toDate);

            if (!isAsync) {
                if(emd != null && emd.getMap() != null){
                    String detailInfo = getOndemandDetailHTML(emd.getMap());
//                    log.info("detailInfo[" + detailInfo + "]");
                    result.put("detail", detailInfo);
                    result.put("rawMap", emd.getMap());
                    modem.setCommState(1);
                }
            } else {
                result.put("commandMethod", "AsynchronousCall");
                result.put("transactionId", "" + trId);
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
            result.put("rawMap", null);
            log.error(ex, ex);
        }
        
        return result;
    }
    // INSET END SP-179

    
    /**
     * SORIA  Mobaile Interface APN
     * @param mdsId 
     * @param 
     * @return
     * @throws Exception
     */
    public Map<String, Object> cmdGetApn(String mdsId  ) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(null, null, mdsId));
            ResponseMap obj = gw.cmdGetApn(mdsId);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("[DEVICE SERIAL:" + mdsId + "] REQUEST TYPE: GET ] key["+e.getKey()+"], value["+ e.getValue()+"]");
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("rtnStr", e.getMessage());
            throw e;
        }

        return map;
    }
    
    public List<Map<String, Object>> cmdGetStandardEventLog(String modemId, String fromDate, String toDate, Protocol modemProtocol) throws Exception {
    	log.info("cmdGetStandardEventLog ModemId[" + modemId + "]");        
    	List<Map<String, Object>> returnList = new LinkedList<Map<String, Object>>();
        CommandWS gw = CmdManager.getCommandWS(modemProtocol);
        ResponseMap response = gw.cmdGetStandardEventLog(modemId, fromDate, toDate);
        
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
        	Map<String, Object> map = new LinkedHashMap<String,Object>();
        	map.put("paramType",e.getKey().toString());
            map.put("paramValue", e.getValue());
            returnList.add(map);
        }
        
        return returnList;
    }
    
    public List<Map<String, Object>> cmdGetTamperingLog(String modemId, String fromDate, String toDate, Protocol modemProtocol) throws Exception {
    	log.info("cmdGetStandardEventLog ModemId[" + modemId + "]");        
    	List<Map<String, Object>> returnList = new LinkedList<Map<String, Object>>();
        CommandWS gw = CmdManager.getCommandWS(modemProtocol);
        ResponseMap response = gw.cmdGetTamperingLog(modemId, fromDate, toDate);
        
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
        	Map<String, Object> map = new LinkedHashMap<String,Object>();
        	map.put("paramType",e.getKey().toString());
            map.put("paramValue", e.getValue());
            returnList.add(map);
        }
        
        return returnList;
    }
    
    public List<Map<String, Object>> cmdGetPowerFailureLog(String modemId, String fromDate, String toDate, Protocol modemProtocol) throws Exception {
    	log.info("cmdGetStandardEventLog ModemId[" + modemId + "]");        
    	List<Map<String, Object>> returnList = new LinkedList<Map<String, Object>>();
        CommandWS gw = CmdManager.getCommandWS(modemProtocol);
        ResponseMap response = gw.cmdGetPowerFailureLog(modemId, fromDate, toDate);
        
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
        	Map<String, Object> map = new LinkedHashMap<String,Object>();
        	map.put("paramType",e.getKey().toString());
            map.put("paramValue", e.getValue());
            returnList.add(map);
        }
        
        return returnList;
    }
    public List<Map<String, Object>> cmdGetControlLog(String modemId, String fromDate, String toDate, Protocol modemProtocol) throws Exception {
    	log.info("cmdGetStandardEventLog ModemId[" + modemId + "]");        
    	List<Map<String, Object>> returnList = new LinkedList<Map<String, Object>>();
        CommandWS gw = CmdManager.getCommandWS(modemProtocol);
        ResponseMap response = gw.cmdGetControlLog(modemId, fromDate, toDate);
        
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
        	Map<String, Object> map = new LinkedHashMap<String,Object>();
        	map.put("paramType",e.getKey().toString());
            map.put("paramValue", e.getValue());
            returnList.add(map);
        }
        
        return returnList;
    }
    public List<Map<String, Object>> cmdGetPQLog(String modemId, String fromDate, String toDate, Protocol modemProtocol) throws Exception {
    	log.info("cmdGetStandardEventLog ModemId[" + modemId + "]");        
    	List<Map<String, Object>> returnList = new LinkedList<Map<String, Object>>();
        CommandWS gw = CmdManager.getCommandWS(modemProtocol);
        ResponseMap response = gw.cmdGetPQLog(modemId, fromDate, toDate);
        
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
        	Map<String, Object> map = new LinkedHashMap<String,Object>();
        	map.put("paramType",e.getKey().toString());
            map.put("paramValue", e.getValue());
            returnList.add(map);
        }
        
        return returnList;
    }
    public List<Map<String, Object>> cmdGetFWUpgradeLog(String modemId, String fromDate, String toDate, Protocol modemProtocol) throws Exception {
    	log.info("cmdGetStandardEventLog ModemId[" + modemId + "]");        
    	List<Map<String, Object>> returnList = new LinkedList<Map<String, Object>>();
        CommandWS gw = CmdManager.getCommandWS(modemProtocol);
        ResponseMap response = gw.cmdGetFWUpgradeLog(modemId, fromDate, toDate);
        
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
        	Map<String, Object> map = new LinkedHashMap<String,Object>();
        	map.put("paramType",e.getKey().toString());
            map.put("paramValue", e.getValue());
            returnList.add(map);
        }
        
        return returnList;
    }


    /**
     * SORIA 모뎀에 대한 retrycount 커맨드 전송
     * @param modemId modem.id != deviceserial
     * @param requestType
     * @param retryValue
     * @return
     * @throws Exception
     */
    public Map<String, Object> cmdRetryCount(String modemId, String requestType, int retryValue) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(null, null, modemId));
            ResponseMap obj = gw.cmdRetryCount(modemId, requestType, retryValue);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("[DEVICE SERIAL:" + modemId + "] REQUEST TYPE: " + requestType + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("rtnStr", e.getMessage());
            throw e;
        }

        return map;
    }

    /**
     * SORIA 모뎀에 대한 metering interval 커맨드 전송
     * @param modemId modem.id != deviceserial
     * @param requestType
     * @param intervalValue
     * @return
     * @throws Exception
     */
    public Map<String, Object> cmdMeteringInterval(String modemId, String requestType, int intervalValue) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(null, null, modemId));
            ResponseMap obj = gw.cmdMeteringInterval(modemId, requestType, intervalValue);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("[DEVICE SERIAL:" + modemId + "] REQUEST TYPE: " + requestType + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("rtnStr", e.getMessage());
            throw e;
        }

        return map;
    }

    // INSERT START SP-193
    public String cmdSendEvent(String eventAlertName, String target, String activatorId, String[][] params) throws Exception {
    	log.info("cmdSendEvent [" + eventAlertName + "," + target + "," + activatorId + "]");
    	
    	List<StringArray> saList = new ArrayList<StringArray>();
    	
    	for(int i=0; i < params.length; i++ ) {
	        StringArray sa = new StringArray();
	        sa.getItem().add(params[i][0]);
	        sa.getItem().add(params[i][1]);
	        saList.add(sa);
        }
        String obj = "FAIL";
        try {
            CommandWS gw = CmdManager.getCommandWS(Protocol.IP);
            obj = gw.cmdSendEvent(eventAlertName, target, activatorId, saList);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return obj;
    }
    // INSERT END SP-193
    
    public void cmdSendEvent2(String eventAlertName, String activatorType, String activatorId, int supplierId) throws Exception {
    	log.info("eventAlertName : " + eventAlertName + ", activatorType : " + ", activatorId : " + ", supplierId : " + supplierId);
    	
    	try {
    		CommandWS gw = CmdManager.getCommandWS(Protocol.IP);
    		
			gw.cmdSendEvent2(eventAlertName, activatorType, activatorId, supplierId);
		} catch (Exception e) {
			log.error(e, e);
		}
    }

    /**
     * 
     * @param mdsId modem.id != deviceserial
     * @param requestType
     * @param rateValue
     * @return
     * @throws Exception
     */
    public Map<String, Object> cmdModemResetTime(String modemId, String requestType, int resetTime)  throws Exception  {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Modem modem = modemDao.get(Integer.parseInt(modemId));
            String protocolName = modem.getProtocolType() == null ? Protocol.LAN.name() : modem.getProtocolType().name();
            CommandWS gw = CmdManager.getCommandWS(protocolName);
            ResponseMap obj = gw.cmdModemResetTime(modemId,requestType,resetTime);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("[MODEM ID:" + modemId + "] REQUEST TYPE: " + requestType + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("rtnStr", e.getMessage());
            throw e;
        }

        return map;
    }
    
    /**
     * 
     * @param mdsId modem.id != deviceserial
     * @param requestType
     * @param rateValue
     * @return
     * @throws Exception
     */
    public Map<String, Object>  cmdModemMode(String modemId, String requestType, int mode ) throws Exception  {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Modem modem = modemDao.get(Integer.parseInt(modemId));
            String protocolName = modem.getProtocolType() == null ? Protocol.LAN.name() : modem.getProtocolType().name();
            CommandWS gw = CmdManager.getCommandWS(protocolName);
            ResponseMap obj = gw.cmdModemMode(modemId,requestType,mode);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("[MODEM ID:" + modemId + "] REQUEST TYPE: " + requestType + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("rtnStr", e.getMessage());
            throw e;
        }
        return map;
    }
    
    /**
     * 
     * @param mdsId modem.id != deviceserial
     * @param requestType
     * @param rateValue
     * @return
     * @throws Exception
     */
    public Map<String, Object>  cmdSnmpServerIpv6Port(String modemId, String requestType, int type , String ipAddress, String port) throws Exception  {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Modem modem = modemDao.get(Integer.parseInt(modemId));
            String protocolName = modem.getProtocolType() == null ? Protocol.LAN.name() : modem.getProtocolType().name();
            CommandWS gw = CmdManager.getCommandWS(protocolName);
            ResponseMap obj = gw.cmdSnmpServerIpv6Port(modemId,requestType,type,ipAddress,port);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("[MODEM ID:" + modemId + "] REQUEST TYPE: " + requestType + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("rtnStr", e.getMessage());
            throw e;
        }
        return map;
    }
    
    /**
     * 
     * @param mdsId modem.id != deviceserial
     * @param requestType
     * @param rateValue
     * @return
     * @throws Exception
     */
    public Map<String, Object>  cmdModemIpInformation(String modemId, String requestType, int targetType, int ipType , String ipAddress) throws Exception  {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Modem modem = modemDao.get(Integer.parseInt(modemId));
            String protocolName = modem.getProtocolType() == null ? Protocol.LAN.name() : modem.getProtocolType().name();
            CommandWS gw = CmdManager.getCommandWS(protocolName);
            ResponseMap obj = gw.cmdModemIpInformation(modemId,requestType,targetType, ipType,ipAddress);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("[MODEM ID:" + modemId + "] REQUEST TYPE: " + requestType + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("rtnStr", e.getMessage());
            throw e;
        }
        return map;
    }
    
    /**
     * 
     * @param mdsId modem.id != deviceserial
     * @param requestType
     * @param rateValue
     * @return
     * @throws Exception
     */
    public Map<String, Object>  cmdModemPortInformation(String modemId, String requestType, int targetType, String port) throws Exception  {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Modem modem = modemDao.get(Integer.parseInt(modemId));
            String protocolName = modem.getProtocolType() == null ? Protocol.LAN.name() : modem.getProtocolType().name();
            CommandWS gw = CmdManager.getCommandWS(protocolName);
            ResponseMap obj = gw.cmdModemPortInformation(modemId,requestType,targetType, port);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("[MODEM ID:" + modemId + "] REQUEST TYPE: " + requestType + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("rtnStr", e.getMessage());
            throw e;
        }
        return map;
    }
    
    /**
     * 
     * @param mdsId modem.id != deviceserial
     * @param requestType
     * @param rateValue
     * @return
     * @throws Exception
     */
    public Map<String, Object> cmdAlarmEventCommandOnOff(String modemId, String requestType, int count , String cmds) throws Exception  {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Modem modem = modemDao.get(Integer.parseInt(modemId));
            String protocolName = modem.getProtocolType() == null ? Protocol.LAN.name() : modem.getProtocolType().name();
            CommandWS gw = CmdManager.getCommandWS(protocolName);
            ResponseMap obj = gw.cmdAlarmEventCommandOnOff(modemId,requestType, count, cmds );

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("[MODEM ID:" + modemId + "] REQUEST TYPE: " + requestType + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("rtnStr", e.getMessage());
            throw e;
        }
        return map;
    }
    
    /**
     * 
     * @param mdsId modem.id != deviceserial
     * @param requestType
     * @param rateValue
     * @return
     * @throws Exception
     */
    public Map<String, Object> cmdTransmitFrequency(String modemId, String requestType, int second) throws Exception  {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Modem modem = modemDao.get(Integer.parseInt(modemId));
            String protocolName = modem.getProtocolType() == null ? Protocol.LAN.name() : modem.getProtocolType().name();
            CommandWS gw = CmdManager.getCommandWS(protocolName);
            ResponseMap obj = gw.cmdTransmitFrequency(modemId,requestType, second);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("[MODEM ID:" + modemId + "] REQUEST TYPE: " + requestType + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("rtnStr", e.getMessage());
            throw e;
        }
        return map;
    }
    
    public String sendSMS(Map<String, Object> condition, List<String> parameterList, String cmdMap) throws Exception {
    	String messageType = condition.get("messageType").toString();
    	String mobliePhNum = condition.get("mobliePhNum").toString();
    	String euiId = condition.get("euiId").toString();
    	String commandCode = condition.get("commandCode").toString();
    	String commandName = condition.get("commandName").toString();
    	List<String> paramList = parameterList;
    	String rtnString;
    	
    	log.debug("sendSMS ==> commandName : " + commandName + ", messageType : " + messageType + ", mobliePhNum : "
   				+ mobliePhNum + ", euiId : " + euiId + ", commandCode : " + commandCode + ", paramList : " + paramList + ", cmdMap : " + cmdMap);
    	
    	CommandWS commandGW = CmdManager.getCommandWS(getProtocolType(null, null, euiId));
    	rtnString = commandGW.sendSMS(commandName, messageType, mobliePhNum, euiId, commandCode, paramList, cmdMap);
    	
    	return rtnString;
    	
    }

    // INSET START SP-476
    @SuppressWarnings("unchecked")
    public Map cmdDmdNiGetRomRead(Meter meter, int serviceType, String operator,
            String fromDate, String toDate) throws Exception {
        log.info("cmdDmdNiGetRomRead meter[" + meter.getMdsId() + "]");
        String sensorInstanceName = null;
        Map result = new HashMap();
        
        fromDate = (fromDate == null || "".equals(fromDate)) ? TimeUtil
                .getCurrentDay()
                : fromDate;
        toDate = (toDate == null || "".equals(toDate)) ? TimeUtil
                .getCurrentDay()
                : toDate;
        
        DataConfiguration config = null;
        try {
            config = new DataConfiguration(new PropertiesConfiguration("command.properties"));
        } catch (ConfigurationException e) {
            try {
                config = new DataConfiguration(new PropertiesConfiguration("config/command.properties"));
            }
            catch (ConfigurationException ee) {
                log.error(e, e);
            }
        }

        try {
            CommandWS gw = null;

            Modem modem = meter.getModem();
            String meterId = meter.getMdsId();
            String modemId = modem.getId().toString();
            boolean isAsync = false;
            long trId = 0;

            String mcuId = null;
            if (modem.getMcu() != null)
                mcuId = modem.getMcu().getSysID();
            
            
            ModemType modemType = ModemType.Unknown;
            if (modem != null) {
                modemType = modem.getModemType();
            }

            MeterData emd = null;

            modem = meter.getModem();

            modemId = modem.getDeviceSerial();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            int days = (int) ((formatter.parse(toDate.substring(0, 8)).getTime() - formatter.parse(fromDate.substring(0, 8)).getTime())/1000/3600/24 + 1);
            
            int modemHandshakeTimeout = Integer.valueOf((String) config.getProperty("modem.timeout.handshaking"))*1000;
            int modemDayTimeout = Integer.valueOf((String) config.getProperty("modem.timeout.day"))*1000;
            int LPinterval = meter.getLpInterval();

            String timeout = String.valueOf(modemHandshakeTimeout + (modemDayTimeout * (60 / LPinterval) * days));
            
            gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null), timeout);
            sensorInstanceName = modem.getDeviceSerial();
            // UPDATE START SP-681
//            emd = gw.cmdDmdNiGetRomRead(mcuId, meterId, modemId, fromDate, toDate);
            
            if (modem.getFwVer().compareTo("1.2") >= 0) {
                emd = gw.cmdDmdNiGetRomRead(mcuId, meterId, modemId, fromDate, toDate, 3); // PollType(3) Timestamp             	
            } else {
            	emd = gw.cmdDmdNiGetRomRead(mcuId, meterId, modemId, fromDate, toDate, 2); // PollType(2) Offset and count
            }
            // UPDATE END SP-681

            
            if (!isAsync) {
                if(emd != null && emd.getMap() != null){
                    String detailInfo = getOndemandDetailHTML(emd.getMap());
//                    log.info("detailInfo[" + detailInfo + "]");
                    result.put("detail", detailInfo);
                    result.put("rawMap", emd.getMap());
                    modem.setCommState(1);
                }
            } else {
                result.put("commandMethod", "AsynchronousCall");
                result.put("transactionId", "" + trId);
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
            result.put("rawMap", null);
            log.error(ex, ex);
        }
        
        return result;
    }
    // INSET END SP-476    
    //INSERT START SP-681
    public Map<String, Object>  cmdExecDmdNiCommand(String modemId, String requestType, String attrId, String attrParam) throws Exception  {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Modem modem = modemDao.get(Integer.parseInt(modemId));
            String protocolName = modem.getProtocolType() == null ? Protocol.LAN.name() : modem.getProtocolType().name();
            CommandWS gw = CmdManager.getCommandWS(protocolName);
            ResponseMap obj = gw.cmdExecDmdNiCommand(modemId,requestType,attrId, attrParam);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("[MODEM ID:" + modemId + "] REQUEST TYPE: " + requestType + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("rtnStr", e.getMessage());
            throw e;
        }
        return map;
    }    
    // INSERT END SP-681
    
 	// INSERT START SP-575
    public Map<String, Object>  cmdGeneralNiCommand(String modemId, String requestType, String attrId, String attrParam) throws Exception  {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Modem modem = modemDao.get(Integer.parseInt(modemId));
            String protocolName = modem.getProtocolType() == null ? Protocol.LAN.name() : modem.getProtocolType().name();
            CommandWS gw = CmdManager.getCommandWS(protocolName);
            ResponseMap obj = gw.cmdGeneralNiCommand(modemId,requestType,attrId, attrParam);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
                log.debug("[MODEM ID:" + modemId + "] REQUEST TYPE: " + requestType + "] key["+e.getKey()+"], value["+ e.getValue()+"]");
                map.put(e.getKey().toString(), e.getValue());
            }
            map.put("rtnStr","Get Response From CommandGW");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("rtnStr", e.getMessage());
            throw e;
        }
        return map;
    }    
 	// INSERT END SP-575

    /**
     * SP-677
     * @param mcuId
     * @param modemPort
     * @param meterIdList
     * @param modemIdList
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map cmdDmdNiGetRomReadMulti(String mcuId, int modemPort, 
            List<String> meterIdList, List<String> modemIdList, String fromDate, String toDate) throws Exception {
    	StringBuffer sb = new StringBuffer();
    	for ( String mdsId : meterIdList){
    		sb.append(mdsId+",");
    	}

        log.debug("cmdDmdNiGetRomReadMulti meters[" + sb.toString() + "] from[" + fromDate +"] to[" + toDate +"]");
        String sensorInstanceName = null;
        Map result = new HashMap();
    	ArrayList<MeterData> mdList = new ArrayList<MeterData>();
    	ArrayList<String> successList = new ArrayList<String>();
    	ArrayList<String> errorList = new ArrayList<String>();
    	
        fromDate = (fromDate == null || "".equals(fromDate)) ? TimeUtil
                .getCurrentDay()
                : fromDate;
        toDate = (toDate == null || "".equals(toDate)) ? TimeUtil
                .getCurrentDay()
                : toDate;

        try {
            CommandWS gw = null;

            boolean isAsync = false;
            long trId = 0;

            Map<String,Object>retData = null;

            
            gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
 //           sensorInstanceName = modem.getDeviceSerial();
            ResponseMap obj = gw.cmdDmdNiGetRomReadMulti(mcuId, modemPort, meterIdList, modemIdList, fromDate, toDate);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
     			if ( "RESULT".equals(e.getKey().toString()) ){
     				result.put("result", "Success");
     				log.info("cmdDmdNiGetRomReadMulti() Success( but some meters perhaps failed) : meters[" + sb.toString() + "] from[" + fromDate +"] to[" + toDate +"]");
//     				if ( (boolean)e.getValue() == true ){
//     		            result.put("result", "Success");
//     			        log.info("cmdDmdNiGetRomReadMulti() Success( but some meters perhaps failed) : meters[" + sb.toString() + "] from[" + fromDate +"] to[" + toDate +"]");
//     				}
//     				else {
//     		            result.put("result", "Failure[No Meter Value]");
//    			        log.info("No Meter Value : meters[" + sb.toString() + "] from[" + fromDate +"] to[" + toDate +"]");	
//     				}
     			}
     			else {
     			  if (  Integer.parseInt((String)e.getValue()) == ErrorCode.IF4ERR_NOERROR	){
     				  successList.add(e.getKey().toString());
     			  }
     			  else {
     				 errorList.add(e.getKey().toString());
     			  }
     			}
            }
            result.put("successMeters", successList);
            result.put("errorMeters", errorList);
            StringBuffer sbs = new StringBuffer();
            for ( String mdsId : successList){
            	sbs.append(mdsId+",");
            }
            StringBuffer sbe = new StringBuffer();
            for ( String mdsId : errorList){
            	sbe.append(mdsId+",");
            }
            log.info("cmdDmdNiGetRomReadMulti() : Success Meters[" + sbs.toString() + "] Error Meters[" + sbe.toString() +"]");

        }catch (Exception ex) {
            log.error(ex, ex);
            
            String errorMessage = "";
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

            result.put("result", "Failure["+errorMessage+"]");;

        }
        
        return result;
    }
    
    public Map<String, Object> cmdMCUGetSchedule_(String mcuId, String name) throws Exception {
        log.info("cmdMcuGetSchedule MCU["+mcuId+"] Name["+name+"]");

        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
        ResponseMap response = gw.cmdMcuGetSchedule_(mcuId, name);

        Map<String, Object> map = new HashMap<String, Object>();
        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
            map.put(e.getKey().toString(), e.getValue());
        }
        
        return map;
		
	}

    public void cmdMcuSetSchedule_(String mcuId, List<ArrayList<String>> scheduleList) throws Exception {
        log.info("cmdMcuSetSchedule MCU[" + mcuId + "]");        
        
        List<StringArray> saList = new ArrayList<StringArray>();
        
        for(ArrayList<String> schedule : scheduleList) {       
	        StringArray sa = new StringArray();
	        sa.getItem().add(schedule.get(0).toString());
	        sa.getItem().add(schedule.get(1).toString());
	        sa.getItem().add(schedule.get(2).toString());
	        sa.getItem().add(schedule.get(3).toString());
	        saList.add(sa);
        }
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            ResponseMap obj = gw.cmdMcuSetSchedule_(mcuId, saList);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public void cmdMcuDeleteSchedule(String mcuId, String scheduleName) throws Exception {
        log.info("cmdMcuDeleteSchedule MCU[" + mcuId + "]");        
        
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            ResponseMap obj = gw.cmdMcuDeleteSchedule(mcuId, scheduleName);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public void cmdMcuExecuteSchedule(String mcuId, String scheduleName) throws Exception {
        log.info("cmdMcuExecuteSchedule MCU[" + mcuId + "]");        
        
        try {
            CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            ResponseMap obj = gw.cmdMcuExecuteSchedule(mcuId, scheduleName);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* SP-779 GetSchedule*/
	public List<Map<String,Object>> cmdMCUGroupGetSchedule(Integer groupId) throws Exception {
		log.info("cmdMCUGroupGetSchedule GroupId["+groupId+"]");
		
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		List<ScheduleData> response = new ArrayList<ScheduleData>();
		
		try {
			CommandWS gw = CmdManager.getCommandWS(getProtocolType(null,null,null));
			response = gw.cmdMcuGroupGetSchedule(groupId);
			
			if(response != null) {
				for(int i = 0; i<response.size(); i++) {
					Map<String, Object> entryResult = new HashMap<String, Object>();
					entryResult.put("sysId", response.get(i).getSysId());
					
					for(ScheduleData.Map.Entry e : response.get(i).getMap().getEntry()) {
						entryResult.put((String) e.getKey(), e.getValue());
					}
					
					log.info("entryResult => "+entryResult);
					result.add(entryResult);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}
	
	 /* SP-779 - SetSchedule */
	public List<Map<String,Object>> cmdMCUGroupSetSchedule(Integer groupId, List<ArrayList<String>> scheduleList) throws Exception {
		log.info("cmdMCUGroupGetSchedule GroupId["+groupId+"]");
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<McuScheduleResult> response = new ArrayList<McuScheduleResult>();
		List<StringArray> stringList = new ArrayList<StringArray>();
        
        for(ArrayList<String> schedule : scheduleList) {       
	        StringArray stringArray = new StringArray();
	        stringArray.getItem().add(schedule.get(0).toString());
	        stringArray.getItem().add(schedule.get(1).toString());
	        stringArray.getItem().add(schedule.get(2).toString());
	        stringArray.getItem().add(schedule.get(3).toString());
	        stringList.add(stringArray);
        }
        
		try {
			CommandWS gw = CmdManager.getCommandWS(getProtocolType(null,null,null));
			response = gw.cmdMcuGroupSetSchedule(groupId, stringList);
			
			if(response != null) {
				for(int i = 0; i < response.size(); i++) {
					resultMap = new HashMap<String, Object>();
					resultMap.put("mcuId", response.get(i).getSysId());
					resultMap.put("result", response.get(i).getResult());
					resultList.add(resultMap);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return resultList;
	}
	
	/* SP-779 - Set MCU Retry Interval Time (cmdSetProperty 109.1.2) -> MCU List로 변경 */
    public List<Map<String, Object>> cmdMcuGroupSetProperty(Integer groupId, String[] key, String[] keyValue) throws Exception {
        log.info("cmdMCUGroupSetProperty GroupId[" + groupId + "]");
        
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
		List<McuPropertyResult> response = new ArrayList<McuPropertyResult>();
        
		try {
			CommandWS gw = CmdManager.getCommandWS(getProtocolType(null, null, null));
			response = gw.cmdMcuGroupSetProperty(groupId, key, keyValue);
			
			if(response != null) {
				for(int i = 0; i < response.size(); i++) {
					resultMap = new HashMap<String, Object>();
					resultMap.put("mcuId", response.get(i).getSysId());
					resultMap.put("result", response.get(i).getResult());
					resultList.add(resultMap);
				}
			}
			
		}catch (Exception e) {
			log.error(e,e);
			throw e;
		}

        return resultList;
    }
    
	// INSERT START SP-802
	// Metering Data Request
    @SuppressWarnings("unchecked")
    public Map cmdMeteringDataRequest(String mcuId, List<String> modemIdList, String fromDate, String toDate) throws Exception {
    	StringBuffer sb = new StringBuffer();
    	for ( String id : modemIdList){
    		sb.append(id+",");
    	}

        log.debug("cmdMeteringDataRequest meters[" + sb.toString() + "] from[" + fromDate +"] to[" + toDate +"]");
        Map result = new HashMap();
    	ArrayList<MeterData> mdList = new ArrayList<MeterData>();
    	ArrayList<String> successList = new ArrayList<String>();
    	ArrayList<String> errorList = new ArrayList<String>();
    	
        fromDate = (fromDate == null || "".equals(fromDate)) ? TimeUtil
                .getCurrentDay()
                : fromDate;
        toDate = (toDate == null || "".equals(toDate)) ? TimeUtil
                .getCurrentDay()
                : toDate;

        try {
            CommandWS gw = null;

            boolean isAsync = false;
            long trId = 0;

            Map<String,Object>retData = null;

            
            gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
            ResponseMap obj = gw.cmdMeteringDataRequest(mcuId, modemIdList, fromDate, toDate);

            for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
     			if ( "RESULT".equals(e.getKey().toString()) ){
     				result.put("result", "Success");
     				log.info("cmdDmdNiGetRomReadMulti() Success( but some meters perhaps failed) : meters[" + sb.toString() + "] from[" + fromDate +"] to[" + toDate +"]");
     			}
     			else {
     			  if (  Integer.parseInt((String)e.getValue()) == ErrorCode.IF4ERR_NOERROR	){
     				  successList.add(e.getKey().toString());
     			  }
     			  else {
     				 errorList.add(e.getKey().toString());
     			  }
     			}
            }
            result.put("successMeters", successList);
            result.put("errorMeters", errorList);
            StringBuffer sbs = new StringBuffer();
            for ( String mdsId : successList){
            	sbs.append(mdsId+",");
            }
            StringBuffer sbe = new StringBuffer();
            for ( String mdsId : errorList){
            	sbe.append(mdsId+",");
            }
            log.info("cmdDmdNiGetRomReadMulti() : Success Meters[" + sbs.toString() + "] Error Meters[" + sbe.toString() +"]");

        }catch (Exception ex) {
            log.error(ex, ex);
            
            String errorMessage = "";
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

            result.put("result", "Failure["+errorMessage+"]");;

        }
        
        return result;
    }
    // INSERT END SP-802
    
	public Map<String, Object> cmdGetCoordinatorConfigure(String mcuId)
			throws Exception {
        log.info("[cmdGetCoordinatorConfigure] McuId=[" + mcuId + "] ");

        Map<String, Object> map = new HashMap<String, Object>();
        
        try{
	        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
	        ResponseMap response = gw.cmdGetCoordinatorConfigure(mcuId);
	
	        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
	            log.debug("key["+e.getKey()+"], value["+ e.getValue()+"]");
	            map.put(e.getKey().toString(), e.getValue());
	        }
	    } catch (Exception e) {
	        map.put("rtnStr", e.getMessage());
	        log.error(e, e);
	    }

        return map;
		
	}
	
	public Map<String, Object> cmdSetCoordinatorConfigure(String mcuId, int configurations, int modemMode, 
			int resetTime, int metringInterval, int transmitFrequency, int cloneTerminate)  throws Exception {
        log.info("[cmdSetCoordinatorConfigure] McuId=[" + mcuId + "] Configurations=[" + configurations  
        + "], ModemMode=[" + modemMode + "] ResetTime=[" + resetTime + "], MetringInterval=[" + metringInterval 
        + "], TransmitFrequency=[" + transmitFrequency + "], CloneTerminate=[" + cloneTerminate + "]");

        Map<String, Object> map = new HashMap<String, Object>();

        try{
	        CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, null, null));
	        ResponseMap response = gw.cmdSetCoordinatorConfigure(mcuId, configurations, modemMode, 
	        		resetTime, metringInterval, transmitFrequency, cloneTerminate);
	
	        for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
	            map.put(e.getKey().toString(), e.getValue());
	        }
	    } catch (Exception e) {
	        map.put("rtnStr", e.getMessage());
	        log.error(e, e);
	    }

        return map;
		
	}
	
	/**
	 * 
	 * OPF-982 
	 * @param target
	 * @param ftpUrl
	 * @param ftpPort
	 * @param ftpDirectory
	 * @param targetFile
	 * @param userName
	 * @param password
	 * @return
	 */
	public Map<String, Object> cmdFOTA(MMIU targetModem, String ftpUrl, String ftpPort,
			String ftpDirectory, String targetFile, String username, String password){
		log.info("[cmdFOTA] targetModem=["+targetModem+"] ftpUrl=["+ftpUrl+"] ftpPort=["+ftpPort+"] ftpDirectory=["+ftpDirectory+"] "
				+ "targetFile=["+targetFile+"] userName=["+username+"] password=["+password+"]");
		Map<String, Object> map = new HashMap<String, Object>();
        try {
			CommandWS gw = CmdManager.getCommandWS(targetModem.getProtocolType().name());
			ResponseMap response = gw.cmdFOTA(targetModem, ftpUrl, ftpPort, ftpDirectory, targetFile, username, password);
			for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
	            map.put(e.getKey().toString(), e.getValue());
	        }
			log.debug("map : " + map);
		} catch (Exception e) {
			log.error(e,e);
		}
		return map;
	}


	
	public Map<String, Object> cmdGetTariffForPKS(String mcuId, String meterId) throws Exception {
		log.debug("[MCU:" + mcuId + " METER: " + meterId + "],  MSG_TIMEOUT="+MSG_TIMEOUT+", TUNNEL_TIMEOUT=" + TUNNEL_TIMEOUT);

		CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null));
		ResponseMap rMap = null;
		log.info("\n------------------ New feature:'Tariff Get/Set' will be added. -------------\n");
		//rMap  = gw.cmdGetTariffForPKS(mcuId, meterId, MSG_TIMEOUT, TUNNEL_TIMEOUT);

		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", "Fail");
		for (ResponseMap.Response.Entry e : rMap.getResponse().getEntry()) {
			log.debug("ke = " + e.getKey() + ", value = " + e.getValue());
			if(((String)e.getKey()).equals("status")) {
				map.put("status", e.getValue());
			}else if(((String)e.getKey()).equals("rtnStr")) {
				map.put("detail", getTariffDetailHTML(e.getValue().toString(), "getTariff"));
			}
		}

		return map;
	}

	public Map<String, Object> cmdSetTariffForPKS(String mcuId, String meterId, String tariffTypeCode) throws Exception {
		return cmdSetTariffForPKS(mcuId, meterId, tariffTypeCode, null);
	}
	
	public Map<String, Object> cmdSetTariffForPKS(String mcuId, String meterId, String tariffTypeCode, Map<String, String> tariff) throws Exception {
		log.debug("[MCU:" + mcuId + " METER: " + meterId + " tariffTypeCode: " + tariffTypeCode + "], tariffSize:" +(tariff == null ? "Null~!" : tariff.size())+ ", MSG_TIMEOUT="+MSG_TIMEOUT+", TUNNEL_TIMEOUT=" + TUNNEL_TIMEOUT);

		
		//test code
//		if(tariff == null) {
//			tariff = new LinkedHashMap<String, String>();
//			tariff.put("aaa", "1234");
//			tariff.put("bbb", "5678");
//			tariff.put("ccc", "91011");
//		}
		
		String tariffString = null;
		if(tariff != null) {
			try {
				tariffString = StringUtil.objectToJsonString(tariff);	
				log.debug("Tariff Map to JsonString => " + tariffString);
			} catch (Exception e) {
				log.error("Tariff map convert error - " + e.getMessage(), e);
				throw e;
			}
		}
		
		CommandWS gw = CmdManager.getCommandWS(getProtocolType(mcuId, meterId, null));
		ResponseMap obj = null;
		log.info("\n------------------ New feature:'Tariff Get/Set' will be added. -------------\n");
		//obj = gw.cmdSetTariffForPKS(mcuId, meterId, tariffTypeCode, tariffString, MSG_TIMEOUT, TUNNEL_TIMEOUT);

		Map<String, Object> map = new HashMap<String, Object>();
		for (ResponseMap.Response.Entry e : obj.getResponse().getEntry()) {
			log.debug("[MCU:" + mcuId + " METER: " + meterId + "] key[" + e.getKey() + "], value[" + e.getValue() + "]");
			map.put(e.getKey().toString(), e.getValue());
			
			if(((String)e.getKey()).equals("status")) {
				map.put("status", e.getValue());
			}else if(((String)e.getKey()).equals("rtnStr")) {
				
				if(map.containsKey("status") && !map.get("status").equals("fail")) {
					map.put("detail", getTariffDetailHTML(e.getValue().toString(), "setTariff"));					
				}else {
					map.put("detail", e.getValue().toString());
				}
			}
		}

		return map;
	}
	
	public String getTariffDetailHTML(String jsonString, String title) {
		StringBuilder html = new StringBuilder();
		html.append("<html>");
		html.append("<form name='f' method='post' style='display:none'>");
		html.append("<textarea name='excelData'></textarea></form>");
		html.append("<link href=/aimir-web/css/style.css rel=stylesheet type=text/css>");
		html.append("<style>");
		html.append(".text {mso-number-format: \"\\@\";}");
		html.append("</style>");		
		html.append("<table  border=1 cellpadding=0 cellspacing=0 bordercolor=#FFFFFF border=1 width=100% id=ondemandTable>");
		if("getTariff".equals(title)) {
			html.append("<caption style='text-align: center;font-size: 20px;'><b>Get Tariff Result</b></caption>");
		}else if("setTariff".equals(title)) {
			html.append("<caption style='text-align: center;font-size: 20px;'><b>Set Tariff Result</b></caption>");
		}
		html.append("<tr><td>&nbsp;</td><td>&nbsp;</td><tr>");
		
		if(jsonString != null && !jsonString.isEmpty()) {
			try {
				Map<String, String> resultMap = StringUtil.jsonStringToMap(jsonString);
				Iterator<String> iter = resultMap.keySet().iterator();
				while(iter.hasNext()) {
					String key = iter.next();
					
					html.append("<tr>");
					html.append("<td height='auto' width='50%' align=left style=\"word-break:break-all\"><b>").append(key.trim()).append("</b></td>");
					html.append("<td height='auto' align=left class='text' style=\"word-break:break-all;mso-number-format:\"\\@\";\">").append(resultMap.get(key).trim()).append("</td>");
					html.append("</tr>");
				}
				
			} catch (Exception e) {
				log.error("Result Parsing error - " + e.getMessage(), e);
			}
		}
		
		html.append("</table>");
		html.append("</html>");
		
		return html.toString();
	}
	
}
