package com.aimir.fep.meter.parser;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.parser.elsterA1140Table.A1140_BILLING_DATA;
import com.aimir.fep.meter.parser.elsterA1140Table.A1140_CB_BILLING_DATA;
import com.aimir.fep.meter.parser.elsterA1140Table.A1140_EVENT_LOG;
import com.aimir.fep.meter.parser.elsterA1140Table.A1140_LP_DATA;
import com.aimir.fep.meter.parser.elsterA1140Table.A1140_METER_INFO;
import com.aimir.fep.meter.parser.elsterA1140Table.A1140_MODEM_INFO;
import com.aimir.fep.meter.parser.elsterA1140Table.A1140_TEST_DATA;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;

/**
 * parsing ELSTER A1700 Meter Data
 *
 * @author EJ Choi
 */
public class ElsterA1140 extends MeterDataParser implements java.io.Serializable {
    
    private static final long serialVersionUID = 8714107014633414138L;

    private static Log log = LogFactory.getLog(ElsterA1140.class);
    
    public static final int LEN_HEADER   = 2;
    public static final String HEADER_MD = "MD";
    public static final String HEADER_MT = "MT";
    public static final String HEADER_BD = "BD";
    public static final String HEADER_LP = "LD";
    public static final String HEADER_EL = "EL";
    //jhkim 추가 currentBilling
    public static final String HEADER_CB = "CB";
    
    private byte[] rawData = null;
    private String meterId = null;
    private Double meteringValue = null;
    private int flag = 0;    
        
    private byte[] modem_info = null;
    private byte[] meter_info = null;
    private byte[] billing_data = null;
    private byte[] billing_data_cb = null;
    private byte[] lp_data = null;
    private byte[] event_log = null;
    
    private A1140_MODEM_INFO MODEM_INFO = null;
    private A1140_METER_INFO METER_INFO = null;
    private A1140_BILLING_DATA BILLING_DATA = null;
    private A1140_CB_BILLING_DATA BILLING_DATA_CB = null;
    private A1140_LP_DATA LP_DATA = null;
    private A1140_EVENT_LOG EVENT_LOG = null;
    
    /**
     * constructor
     */
    public ElsterA1140() { }

    public static void main(String args[]) throws Exception {
        ElsterA1140 elster = new ElsterA1140();
        A1140_TEST_DATA testData = new A1140_TEST_DATA();
        System.out.println(testData.toString());
        elster.parse(testData.getTestDataAll());
        elster.getInstrument();
    }

    /**
     * parseing Energy Meter Data of ELSTER A140 Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception {
        log.debug("[TOTAL] len=[" + data.length + "] data=[" + Hex.decode(data) + "]");
        this.rawData = data;
        
        int offset = 0;
        int totalLength = data.length;
        int dataLength = 0;
        
        while (offset < totalLength) {
            String header = new String(DataFormat.select(data, offset, LEN_HEADER));
            offset += LEN_HEADER;
            
            dataLength = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_HEADER));
            offset += LEN_HEADER;
            
            if (header != "" && header != null && dataLength > 0) {
                if (header.equals(HEADER_MD)) {
                    modem_info = DataFormat.select(data, offset, dataLength);
                    MODEM_INFO = new A1140_MODEM_INFO(modem_info);
                    
                    log.debug("MODEM_INFO[(length=" + dataLength + "), (data=" + Hex.decode(modem_info) + ")]");
                } else if (header.equals(HEADER_MT)) {
                    meter_info = DataFormat.select(data, offset, dataLength);
                    METER_INFO = new A1140_METER_INFO(meter_info);
                    
                    this.meterId = METER_INFO.getMeterSerial();
                    this.meterTime = METER_INFO.getMeterTime();
                    
                    log.debug("METER_INFO[(length=" + dataLength + "), (data=" + Hex.decode(meter_info) + ")]");
                } else if (header.equals(HEADER_BD)) {
                    billing_data = DataFormat.select(data, offset, dataLength);
                    BILLING_DATA = new A1140_BILLING_DATA(billing_data);
                    
                    log.debug("BILLING_DATA[(length=" + dataLength + "), (data=" + Hex.decode(billing_data) + ")]");
                } else if (header.equals(HEADER_CB)) {
                	billing_data_cb = DataFormat.select(data, offset, dataLength);
                    BILLING_DATA_CB = new A1140_CB_BILLING_DATA(billing_data_cb);
                    
                    log.debug("BILLING_DATA_CB [(length=" + dataLength + "), (data=" + Hex.decode(billing_data_cb) + ")]");
                } else if (header.equals(HEADER_LP)) {
                    lp_data = DataFormat.select(data, offset, dataLength);
                    LP_DATA = new A1140_LP_DATA(lp_data);

                    log.debug("LP_DATA[(length=" + dataLength + "), (data=" + Hex.decode(lp_data) + ")]");
                } else if (header.equals(HEADER_EL)) {
                    event_log = DataFormat.select(data, offset, dataLength);
                    EVENT_LOG = new A1140_EVENT_LOG(event_log);
                    
                    log.debug("EVENT_LOG[(length=" + dataLength + "), (data=" + Hex.decode(event_log) + ")]");
                } else {
                    log.debug("Wrong Format !!!");
                    break;
                }
            }
            offset += dataLength;
        }
        log.debug("Finished==============================================");
    }
    
    public LPData[] getLpData() throws Exception {
        LPData[] lpData = null;
        
        if (lp_data != null) {
            lpData = LP_DATA.getLpData();
        }

        return lpData;
    }
    
    public int getResolution() {
        int lpPeriod = 0;
        
        if (lp_data != null) {
            lpPeriod = LP_DATA.getLpPeriod();
        }
        
        return lpPeriod;
    }
    
    public List<EventLogData> getMeterEventLog() throws Exception {
        if (event_log != null) {
            return EVENT_LOG.getMeterEventLog();
        } else {
            return null;
        }
    }
    
    public List<PowerAlarmLogData> getPowerEventLog() throws Exception {
        if (event_log != null) {
            return EVENT_LOG.getPowerAlarmLog();
        } else {
            return null;
        }
    }
    
    public List<PowerAlarmLogData> getLpPowerEventLog() throws Exception {
        if (lp_data != null) {
            return LP_DATA.getLpPowerAlarmLog();
        } else {
            return null;
        }
    }
    
    public List<EventLogData> getLpMeterEventLog() throws Exception {
        if (lp_data != null) {
            return LP_DATA.getLpMeterEventLog();
        } else {
            return null;
        }
    }
    
    public Instrument[] getInstrument() throws Exception {
        Instrument[] insts = new Instrument[1];
        log.debug("Instrument ========================================================start2 "+METER_INFO);
        if (meter_info != null) {
            insts[0] = new Instrument();
            log.debug(" METER_INFO.getRMSCurrentA()) "+ METER_INFO.getRMSCurrentA());
            insts[0].setCURR_A(METER_INFO.getRMSCurrentA()==null ? 0.0 : METER_INFO.getRMSCurrentA());
            log.debug(" METER_INFO.getRMSCurrentB()) "+ METER_INFO.getRMSCurrentB());
            insts[0].setCURR_B(METER_INFO.getRMSCurrentB()==null ? 0.0 : METER_INFO.getRMSCurrentB());
            log.debug(" METER_INFO.getRMSCurrentC()) "+ METER_INFO.getRMSCurrentC());
            insts[0].setCURR_C(METER_INFO.getRMSCurrentC()==null ? 0.0 : METER_INFO.getRMSCurrentC());
            insts[0].setVOL_A(METER_INFO.getRMSVoltageA()==null ? 0.0 : METER_INFO.getRMSVoltageA());
            insts[0].setVOL_B(METER_INFO.getRMSVoltageB()==null ? 0.0 : METER_INFO.getRMSVoltageB());
            insts[0].setVOL_C(METER_INFO.getRMSVoltageC()==null ? 0.0 : METER_INFO.getRMSVoltageC());
            insts[0].setPF_TOTAL(METER_INFO.getPowerFactorTotal()==null ? 0.0 : METER_INFO.getPowerFactorTotal());
            insts[0].setPF_A(METER_INFO.getPowerFactorA()==null ? 0.0 : METER_INFO.getPowerFactorA());
            insts[0].setPF_B(METER_INFO.getPowerFactorB()==null ? 0.0 : METER_INFO.getPowerFactorB());
            insts[0].setPF_C(METER_INFO.getPowerFactorC()==null ? 0.0 : METER_INFO.getPowerFactorC());
            insts[0].setKW_A(METER_INFO.getActivePowerA()==null ? 0.0 : METER_INFO.getActivePowerA());
            insts[0].setKW_B(METER_INFO.getActivePowerB()==null ? 0.0 : METER_INFO.getActivePowerB());
            insts[0].setKW_C(METER_INFO.getActivePowerC()==null ? 0.0 : METER_INFO.getActivePowerC());            
            insts[0].setKVA_A(METER_INFO.getApparentPowerA()==null ? 0.0 : METER_INFO.getApparentPowerA());
            insts[0].setKVA_B(METER_INFO.getApparentPowerB()==null ? 0.0 : METER_INFO.getApparentPowerB());
            insts[0].setKVA_C(METER_INFO.getApparentPowerC()==null ? 0.0 : METER_INFO.getApparentPowerC());
            insts[0].setKVAR_A(METER_INFO.getReactivePowerA()==null ? 0.0 : METER_INFO.getReactivePowerA());
            insts[0].setKVAR_B(METER_INFO.getReactivePowerB()==null ? 0.0 : METER_INFO.getReactivePowerB());
            insts[0].setKVAR_C(METER_INFO.getReactivePowerC()==null ? 0.0 : METER_INFO.getReactivePowerC());
            insts[0].setLINE_FREQUENCY(METER_INFO.getFrequencyTotal()==null ? 0.0 : METER_INFO.getFrequencyTotal());
            insts[0].setCURR_ANGLE_A(METER_INFO.getAngleA()==null ? 0.0 : METER_INFO.getAngleA());
            insts[0].setCURR_ANGLE_B(METER_INFO.getAngleB()==null ? 0.0 : METER_INFO.getAngleB());
            insts[0].setCURR_ANGLE_C(METER_INFO.getAngleC()==null ? 0.0 : METER_INFO.getAngleC());
            
            return insts;
        } else {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public HashMap getModemData() throws Exception {
        HashMap<String, String> modemData = new HashMap<String, String>();
        
        if (meter_info != null) {
            modemData.put("protocolType", Protocol.GPRS.name());
            modemData.put("fwVersion"   , MODEM_INFO.getFwVerion());
            modemData.put("fwBuild"     , MODEM_INFO.getFwBuild());
            modemData.put("hwVersion"   , MODEM_INFO.getHwVersion());
//          modemData.put("moduleSerial", MODEM_INFO.getModuleSerial());
            modemData.put("simNumber"   , MODEM_INFO.getSimIMSI());
            modemData.put("rssi"        , MODEM_INFO.getRSSI()+"");
            modemData.put("ber"         , MODEM_INFO.getBER()+"");
            modemData.put("modemStatus" , MODEM_INFO.getModemStatus()+"");
        }
        return modemData;
    }
    
    public BillingData getBillingData() throws Exception {
        if (billing_data != null) {
            return BILLING_DATA.getBillingData();
        } else {
            return null;
        }
    }
    
    public BillingData getCurrentBillingData() throws Exception{
    	if(billing_data_cb != null){
    		return BILLING_DATA_CB.getBillingData();
    	} else{
    		return null;
    	}
    }

    @Override
    @SuppressWarnings("unchecked")
    public LinkedHashMap getData() {
        LinkedHashMap<String, String> dataMap = new LinkedHashMap<String, String>();
        
        BillingData billingData         = null;
        LPData[] lpData                 = null;
        List<EventLogData> eventLogList = null;
        
        return dataMap;
    }

    @Override
    public int getFlag() {
        return this.flag;
    }

    @Override
    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public int getLength() {
        return this.rawData.length;
    }

    @Override
    public Double getMeteringValue() {
        if (billing_data_cb != null) {
            try {
                 BillingData billingData = BILLING_DATA_CB.getBillingData();
                 this.meteringValue = billingData.getActiveEnergyRateTotal();//getActiveEnergyImportRateTotal();
//                this.meteringValue = METER_INFO.getActivePowerTotal();
            } catch (NumberFormatException e) {
                log.error(e);
            } catch (Exception e) {
                log.error(e);
            }
        }
        return this.meteringValue;
    }

    @Override
    public byte[] getRawData() {
        return this.rawData;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("Elster_A1140_DATA[\n");
        sb.append(MODEM_INFO.toString() + "\n");
        log.debug("MODEM_INFO is OK");
        sb.append(METER_INFO.toString() + "\n");
        log.debug("METER_INFO is OK");
        sb.append(BILLING_DATA.toString() + "\n");
        log.debug("BILLING_DATA is OK");
        sb.append(BILLING_DATA_CB.toString() + "\n");
        log.debug("BILLING_DATA_CB is OK");
        sb.append(LP_DATA.toString() + "\n");
        log.debug("LP_DATA is OK");
        sb.append(EVENT_LOG.toString() + "\n");
        log.debug("EVENT_LOG is OK") ;
        sb.append("]");
        
        return sb.toString();
    }
    
    public static String convertTimestamp(String title, byte[] data) {
        byte[] b = data;
        TimeZone tz =TimeZone.getDefault();
        
        DataFormat.convertEndian(b);
        String timestamp = DateTimeUtil.getDateString(DataFormat.getLongToBytes(b)*1000 - tz.getRawOffset()); //A1140미터는 UTC적용 시간을 넘겨주기때문에 기준시로 변경하여 시간값을 구한다.
        log.debug(title+"=[" + timestamp + "] RAW[" + Hex.decode(data) + "]");
        return timestamp;
    }
}
