package com.aimir.fep.meter.parser;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.aimir.fep.command.conf.KamstrupCIDMeta;
import com.aimir.fep.command.conf.KamstrupCIDMeta.CID;
import com.aimir.fep.command.conf.KamstrupCIDMeta.SUB_CID;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.MeteringFail;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing Kamstrup OmniPower Meter Data
 *
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2014-04-15 15:59:15 +0900 $,
 */
@Service
public class KamstrupOmniPower extends MeterDataParser implements java.io.Serializable
{
    private static final long serialVersionUID = 1974450760100885924L;

    private static Log log = LogFactory.getLog(KamstrupOmniPower.class);

    private byte[] PERIOD = new byte[1];
    private byte[] CM = new byte[1];
    private byte[] TF = new byte[3];
    private byte[][] CF;
    private byte[] Last = new byte[4];
    private byte[] CNT = new byte[2];

    private byte[] rawData = null;
    private Double meteringValue = null;
    private int flag = 0;
    private String meterId = null;
    private int period = 0;
    private int errorCode = 0;
    private int lpInterval = 0;

    private ModemLPData[] lpData = null;
    private EventLogData[] eventlogdata = null;
    
    private double activeEnergyA14 = 0; //1.8.0
    private double activeEnergyA23 = 0; //1.8.1
    private double activeEnergyR12 = 0; //1.8.2
    private double activeEnergyR34 = 0; //1.8.3
    private double activeEnergyA14Tariff1 = 0;
    private double activeEnergyA14Tariff2 = 0;
    private double activeEnergyA14Tariff3 = 0;
    private double voltageL1 = 0;
    private double voltageL2 = 0;
    private double voltageL3 = 0;
    private double currentL1 = 0;
    private double currentL2 = 0;
    private double currentL3 = 0;
    private double avgVoltageL1 = 0;
    private double avgVoltageL2 = 0;
    private double avgVoltageL3 = 0;
    private double avgCurrentL1 = 0;
    private double avgCurrentL2 = 0;
    private double avgCurrentL3 = 0;
    private String date = "";
    private String clock = "";
    private int hourCounter = 0; //96.8.0
    private int pulseInput = 0; //0.128.0
    private String rtc = "";
    private String rtcStatus = "";
    private String maxPowerP14RTC = "";
    private double maxPowerP14Tariff1 = 0; //1.6.0
    private String maxPowerP14Tariff1RTC = "";
    private double maxPowerP14Tariff2 = 0;
    private String maxPowerP14Tariff2RTC = "";
    private int connStatus = 0;
    private int connFeedback = 0;
    private String totMeterNumber = "";
    private String meterNumber1 = "";
    private String meterNumber2 = "";
    private String meterNumber3 = "";
    private String typeNumber = "";
    private int meterStatus = 0;
    private String swRevision = "";
    private int opMode = 0;

    private Map<String, String[]> valueMap = new LinkedHashMap<String, String[]>();
    private Map<String, ModemLPData> _lpMap = new HashMap<String, ModemLPData>();
    private List<BillingData> billList = new ArrayList<BillingData>();
    
    private final int UNIT_KAMDATETIME = 72;
    private final String LOG_LOAD_PROFILE = "Load profile logger";
    private final String LOG_DEBITING = "Debiting logger 1";
    
    public KamstrupOmniPower() {
    }

    /**
     * constructor
     */
    public KamstrupOmniPower(String meterId)
    {
        this.meterId = meterId;
    }

    /**
     * getRawData
     */
    public byte[] getRawData()
    {
        return rawData;
    }

    /**
     * get data length
     * @return length
     */
    public int getLength()
    {
        if(rawData == null)
            return 0;

        return rawData.length;
    }

    public Double getMeteringValue()
    {
        // TODO Auto-generated method stub
        return meteringValue;
    }

    public String getMeterId()
    {
        // TODO Auto-generated method stub
        return meterId;
    }

    public ModemLPData[] getLpData() {
        return lpData;
    }

    public EventLogData[] getEventLog(){
        return eventlogdata;
    }
    
    /**
     * get flag
     * @return flag measurement flag
     */
    public int getFlag()
    {
        return this.flag;
    }

    /**
     * set flag
     * @param flag measurement flag
     */
    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    public int getPeriod() {
        return period;
    }
    
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getLpInterval() {
        return lpInterval;
    }

    public void setLpInterval(int lpInterval) {
        this.lpInterval = lpInterval;
    }

    public EventLogData[] getEventlogdata() {
        return eventlogdata;
    }

    public void setEventlogdata(EventLogData[] eventlogdata) {
        this.eventlogdata = eventlogdata;
    }

    public double getActiveEnergyA14() {
        return activeEnergyA14;
    }

    public void setActiveEnergyA14(double activeEnergyA14) {
        this.activeEnergyA14 = activeEnergyA14;
    }

    public double getActiveEnergyA23() {
        return activeEnergyA23;
    }

    public void setActiveEnergyA23(double activeEnergyA23) {
        this.activeEnergyA23 = activeEnergyA23;
    }

    public double getActiveEnergyR12() {
        return activeEnergyR12;
    }

    public void setActiveEnergyR12(double activeEnergyR12) {
        this.activeEnergyR12 = activeEnergyR12;
    }

    public double getActiveEnergyR34() {
        return activeEnergyR34;
    }

    public void setActiveEnergyR34(double activeEnergyR34) {
        this.activeEnergyR34 = activeEnergyR34;
    }

    public double getActiveEnergyA14Tariff1() {
        return activeEnergyA14Tariff1;
    }

    public void setActiveEnergyA14Tariff1(double activeEnergyA14Tariff1) {
        this.activeEnergyA14Tariff1 = activeEnergyA14Tariff1;
    }

    public double getActiveEnergyA14Tariff2() {
        return activeEnergyA14Tariff2;
    }

    public void setActiveEnergyA14Tariff2(double activeEnergyA14Tariff2) {
        this.activeEnergyA14Tariff2 = activeEnergyA14Tariff2;
    }

    public double getActiveEnergyA14Tariff3() {
        return activeEnergyA14Tariff3;
    }

    public void setActiveEnergyA14Tariff3(double activeEnergyA14Tariff3) {
        this.activeEnergyA14Tariff3 = activeEnergyA14Tariff3;
    }

    public double getVoltageL1() {
        return voltageL1;
    }

    public void setVoltageL1(double voltageL1) {
        this.voltageL1 = voltageL1;
    }

    public double getVoltageL2() {
        return voltageL2;
    }

    public void setVoltageL2(double voltageL2) {
        this.voltageL2 = voltageL2;
    }

    public double getVoltageL3() {
        return voltageL3;
    }

    public void setVoltageL3(double voltageL3) {
        this.voltageL3 = voltageL3;
    }

    public double getCurrentL1() {
        return currentL1;
    }

    public void setCurrentL1(double currentL1) {
        this.currentL1 = currentL1;
    }

    public double getCurrentL2() {
        return currentL2;
    }

    public void setCurrentL2(double currentL2) {
        this.currentL2 = currentL2;
    }

    public double getCurrentL3() {
        return currentL3;
    }

    public void setCurrentL3(double currentL3) {
        this.currentL3 = currentL3;
    }

    public double getAvgVoltageL1() {
        return avgVoltageL1;
    }

    public void setAvgVoltageL1(double avgVoltageL1) {
        this.avgVoltageL1 = avgVoltageL1;
    }

    public double getAvgVoltageL2() {
        return avgVoltageL2;
    }

    public void setAvgVoltageL2(double avgVoltageL2) {
        this.avgVoltageL2 = avgVoltageL2;
    }

    public double getAvgVoltageL3() {
        return avgVoltageL3;
    }

    public void setAvgVoltageL3(double avgVoltageL3) {
        this.avgVoltageL3 = avgVoltageL3;
    }

    public double getAvgCurrentL1() {
        return avgCurrentL1;
    }

    public void setAvgCurrentL1(double avgCurrentL1) {
        this.avgCurrentL1 = avgCurrentL1;
    }

    public double getAvgCurrentL2() {
        return avgCurrentL2;
    }

    public void setAvgCurrentL2(double avgCurrentL2) {
        this.avgCurrentL2 = avgCurrentL2;
    }

    public double getAvgCurrentL3() {
        return avgCurrentL3;
    }

    public void setAvgCurrentL3(double avgCurrentL3) {
        this.avgCurrentL3 = avgCurrentL3;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getHourCounter() {
        return hourCounter;
    }

    public void setHourCounter(int hourCounter) {
        this.hourCounter = hourCounter;
    }

    public int getPulseInput() {
        return pulseInput;
    }

    public void setPulseInput(int pulseInput) {
        this.pulseInput = pulseInput;
    }

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    public String getRtcStatus() {
        return rtcStatus;
    }

    public void setRtcStatus(String rtcStatus) {
        this.rtcStatus = rtcStatus;
    }

    public String getMaxPowerP14RTC() {
        return maxPowerP14RTC;
    }

    public void setMaxPowerP14RTC(String maxPowerP14RTC) {
        this.maxPowerP14RTC = maxPowerP14RTC;
    }

    public double getMaxPowerP14Tariff1() {
        return maxPowerP14Tariff1;
    }

    public void setMaxPowerP14Tariff1(double maxPowerP14Tariff1) {
        this.maxPowerP14Tariff1 = maxPowerP14Tariff1;
    }

    public String getMaxPowerP14Tariff1RTC() {
        return maxPowerP14Tariff1RTC;
    }

    public void setMaxPowerP14Tariff1RTC(String maxPowerP14Tariff1RTC) {
        this.maxPowerP14Tariff1RTC = maxPowerP14Tariff1RTC;
    }

    public double getMaxPowerP14Tariff2() {
        return maxPowerP14Tariff2;
    }

    public void setMaxPowerP14Tariff2(double maxPowerP14Tariff2) {
        this.maxPowerP14Tariff2 = maxPowerP14Tariff2;
    }

    public String getMaxPowerP14Tariff2RTC() {
        return maxPowerP14Tariff2RTC;
    }

    public void setMaxPowerP14Tariff2RTC(String maxPowerP14Tariff2RTC) {
        this.maxPowerP14Tariff2RTC = maxPowerP14Tariff2RTC;
    }

    public int getConnStatus() {
        return connStatus;
    }

    public void setConnStatus(int connStatus) {
        this.connStatus = connStatus;
    }

    public int getConnFeedback() {
        return connFeedback;
    }

    public void setConnFeedback(int connFeedback) {
        this.connFeedback = connFeedback;
    }

    public String getTotMeterNumber() {
        return totMeterNumber;
    }

    public void setTotMeterNumber(String totMeterNumber) {
        this.totMeterNumber = totMeterNumber;
    }

    public String getMeterNumber1() {
        return meterNumber1;
    }

    public void setMeterNumber1(String meterNumber1) {
        this.meterNumber1 = meterNumber1;
    }

    public String getMeterNumber2() {
        return meterNumber2;
    }

    public void setMeterNumber2(String meterNumber2) {
        this.meterNumber2 = meterNumber2;
    }

    public String getMeterNumber3() {
        return meterNumber3;
    }

    public void setMeterNumber3(String meterNumber3) {
        this.meterNumber3 = meterNumber3;
    }

    public String getTypeNumber() {
        return typeNumber;
    }

    public void setTypeNumber(String typeNumber) {
        this.typeNumber = typeNumber;
    }

    public int getMeterStatus() {
        return meterStatus;
    }

    public void setMeterStatus(int meterStatus) {
        this.meterStatus = meterStatus;
    }

    public String getSwRevision() {
        return swRevision;
    }

    public void setSwRevision(String swRevision) {
        this.swRevision = swRevision;
    }

    public int getOpMode() {
        return opMode;
    }

    public void setOpMode(int opMode) {
        this.opMode = opMode;
    }

    public void setMeteringValue(Double meteringValue) {
        this.meteringValue = meteringValue;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public BillingData[] getBillData() {
        return this.billList.toArray(new BillingData[0]);
    }
    
    /**
     * parse meter mesurement data
     * @param data
     */
    public void parse(byte[] data) throws Exception
    {
        log.debug(Hex.decode(data));
        
        int pos = 0;

        System.arraycopy(data, pos, PERIOD, 0, PERIOD.length);
        pos += PERIOD.length;
        period = DataUtil.getIntToBytes(PERIOD);
        log.debug("PERIOD[" + period + "]");

        System.arraycopy(data, pos, CM, 0, CM.length);
        pos += CM.length;
        int channelMask = DataUtil.getIntToBytes(CM);
        log.debug("CM[" + channelMask + "]");

        // CM을 이용하여 채널 개수를 파악한다.
        boolean ch_a14 = (0xF8 & CM[0]) > 0;
        boolean ch_a23 = (0xF4 & CM[0]) > 0;
        boolean ch_r12 = (0xF2 & CM[0]) > 0;
        boolean ch_r34 = (0xF1 & CM[0]) > 0;
        
        int ch_cnt = 0;
        if (ch_a14) ch_cnt++;
        if (ch_a23) ch_cnt++;
        if (ch_r12) ch_cnt++;
        if (ch_r34) ch_cnt++;
        
        log.debug("ChannelCnt[" + ch_cnt + "]");
        
        System.arraycopy(data, pos, TF, 0, TF.length);
        pos += TF.length;
        log.debug("TF[" + Hex.decode(TF) + "]");
        
        CF = new byte[ch_cnt][3];
        
        for (int i = 0; i < ch_cnt; i++) {
            System.arraycopy(data, pos, CF[i], 0, CF[i].length);
            pos += CF[i].length;
            log.debug("CF"+ i + "[" + Hex.decode(CF[i]) + "]");
        }

        System.arraycopy(data, pos, Last, 0, Last.length);
        pos += Last.length;
        int last = DataUtil.getIntToBytes(Last);
        log.debug("Last[" + last + "]");

        System.arraycopy(data, pos, CNT, 0, CNT.length);
        pos += CNT.length;
        int cnt = DataUtil.getIntToBytes(CNT);
        log.debug("CNT[" + cnt + "]");

        // TF[1] : 길이
        int tfUnit = DataUtil.getIntToByte(TF[0]);
        int tfLen = DataUtil.getIntToByte(TF[1]);
        byte[] LT = new byte[tfLen];
        String[] lpTime = null;
        
        // LP Data를 가져온다.
        byte[] LP = null;
        double[] baseValueList;
        ModemLPData _lpData = null;
        String[] values = null;
        for (int i = 0; i < cnt; i++) {
            _lpData = new ModemLPData();
            
            // LT를 가져온다.
            System.arraycopy(data, pos, LT, 0, LT.length);
            pos += LT.length;
            // TF의 포맷을 이용한다.
            lpTime = formatValue(tfUnit, LT, 0);
            log.debug("LPTIME[" + lpTime[0] + "]");
            _lpData.setLpDate(lpTime[0]); // lpTime[1]은 단위
            // 채널 개수만큼 LP를 가져온다.
            baseValueList = new double[ch_cnt];
            for (int ch = 0; ch < ch_cnt; ch++) {
                LP = new byte[DataUtil.getIntToByte(CF[ch][1])]; // CF의 두번째 바이트 길이로 설정
                System.arraycopy(data, pos, LP, 0, LP.length);
                pos += LP.length;
                
                log.debug(Hex.decode(LP));
                values = formatValue(CF[ch][0], LP, KamstrupCIDMeta.makeSiEx(CF[ch][2]));
                baseValueList[ch] = Double.parseDouble(values[0]);
                log.debug("LP Value["+ values[0] + "] Unit[" + values[1] + "]");
            }
            _lpData.setBasePulse(baseValueList);
            // log.debug(_lpData.toString());
            _lpMap.put(_lpData.getLpDate(), _lpData);
        }
        log.debug("Response LP size[" + cnt + "] Real LP size[" + _lpMap.size() + "]");
        
        lpInterval = 60 / period;
        
        this.lpData = makeLPData(_lpMap);
        
        // KMP 미터 정보를 파싱한다.
        byte[] km = new byte[data.length - pos];
        System.arraycopy(data, pos, km, 0, km.length);
        parseRegisterValues(km);
    }

    private ModemLPData[] makeLPData(Map<String, ModemLPData> _lpMap) throws Exception
    {
        ModemLPData[] lpData = null;
        // lp를 생성해야 한다.
        Calendar cal = Calendar.getInstance();
        String lpdatetime = null;
        if (_lpMap.size() > 1) {
         // LP 가 연속이 아닐 수 있다.
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Map<Integer, List<ModemLPData>> lpMap = new HashMap<Integer, List<ModemLPData>>();
            ModemLPData[] _lpDatas = _lpMap.values().toArray(new ModemLPData[0]);
            Arrays.sort(_lpDatas, new Comparator<ModemLPData>() {

                @Override
                public int compare(ModemLPData m1, ModemLPData m2) {
                    return m1.getLpDate().compareTo(m2.getLpDate());
                }
                
            });
            cal.setTime(sdf.parse(_lpDatas[0].getLpDate()));
            List<ModemLPData> _lplist = new ArrayList<ModemLPData>();
            int lpmapidx = 0;
            for (int i = 0; i < _lpDatas.length; i++) {
                lpdatetime = sdf.format(cal.getTime());
                // 주기별로 더해진 LP시간과 실제 LP의 시간을 비교하여 같으면 리스트에 추가하고
                // 다르면 다른 그룹으로 처리한다.
                if (lpdatetime.equals(_lpDatas[i].getLpDate())) {
                    _lplist.add(_lpDatas[i]);
                }
                else {
                    log.info("TURNING DATE[" + lpdatetime + ", " + _lpDatas[i].getLpDate() + "]");
                    lpMap.put(lpmapidx++, _lplist);
                    log.info("LP MAP SIZE[" + lpMap.size() + "] LP SIZE[" + _lplist.size() + "]");
                    _lplist = new ArrayList<ModemLPData>();
                    _lplist.add(_lpDatas[i]);
                    cal.setTime(sdf.parse(_lpDatas[i].getLpDate()));
                }
                cal.add(Calendar.MINUTE, lpInterval);
            }
            lpMap.put(lpmapidx++, _lplist);
            log.info("LP MAP SIZE[" + lpMap.size() + "] LP SIZE[" + _lplist.size() + "]");
            
            // Map에 있는 연속된 LP 데이타를 이용하여 채널별 lp를 구한다.
            lpData = new ModemLPData[lpmapidx];
            ModemLPData slp1 = null;
            ModemLPData slp2 = null;
            for (int i = 0; i < lpmapidx; i++) {
                _lplist = lpMap.get(i);
                // 첫번째 데이타에 채널별 사용량을 넣는다.
                // _lplist의 사이즈가 1이면 스킵한다.
                if (_lplist.size() == 0 || _lplist.size() == 1) continue;
                lpData[i] = _lplist.get(1);
                lpData[i].setLp(new double[lpData[i].getBasePulse().length][_lplist.size()-1]);
                for (int s = 0; s < _lplist.size()-1; s++) {
                    slp1 = _lplist.get(s);
                    slp2 = _lplist.get(s+1);
                    
                    lpData[i].setBasePulse(slp1.getBasePulse());
                    for (int ch = 0; ch < lpData[i].getBasePulse().length; ch++) {
                        // log.debug("i:"+i+", ch:"+ch+", s:"+s);
                        lpData[i].getLp()[ch][s] = slp2.getBasePulse()[ch] - slp1.getBasePulse()[ch];
                    }
                }
                // log.debug(lpData[i].toString());
            }
        }
        
        return lpData;
    }
    
    public MeteringFail getMeteringFail() {

        MeteringFail meteringFail = null;
        if(this.errorCode > 0){
             meteringFail = new MeteringFail();
             meteringFail.setModemErrCode(this.errorCode);
             meteringFail.setModemErrCodeName(NURI_T002.getMODEM_ERROR_NAME(this.errorCode));
             return meteringFail;
        }else{
            return null;
        }
    }

    /**
     * get String
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("KamstrupOmniPower DATA[");
        sb.append("(meteringValue=").append(meteringValue).append("),");
        sb.append("(lp=(");
        for(int i = 0 ; i < 24 ; i++)
        {
            // sb.append("Hour["+i+"]=["+preDayLPValue[i]+"], ");
        }
        sb.append(")\n");
        sb.append("]\n");

        return sb.toString();
    }

    /**
     * get Data
     */
    @Override
    public LinkedHashMap<String, String> getData()
    {
        DecimalFormat decimalf=null;
        SimpleDateFormat datef14=null;
         
        if(meter!=null && meter.getSupplier()!=null){
            Supplier supplier = meter.getSupplier();
            if(supplier !=null){
                String lang = supplier.getLang().getCode_2letter();
                String country = supplier.getCountry().getCode_2letter();
                
                decimalf = TimeLocaleUtil.getDecimalFormat(supplier);
                datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
            }
        }else{
            //locail 정보가 없을때는 기본 포멧을 사용한다.
            decimalf = new DecimalFormat();
            datef14 = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        }
        
        LinkedHashMap<String, String> res = new LinkedHashMap<String, String>(16,0.75f,false);
        try {
            String meteringTime = null;
            if(super.meteringTime != null)
            	meteringTime = datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(super.meteringTime));
            //res.put("name","ZEUPLS");
            res.put("Metering Time", meteringTime);
            res.put("Metering Value(kWh)",""+decimalf.format(meteringValue));
            // res.put("Unit", unit);
            // res.put("Meter Constant", decimalf.format(meterConstant));
            // res.put("Current Pulse(kW)", decimalf.format(curPulse));
    
            int interval = 60 / period;
            String lpTime = "";
            Calendar cal = Calendar.getInstance();
            if (lpData != null) {
                for (int i = 0; i < lpData.length; i++) {
                    if (lpData[i] == null) continue;
                    
                    cal.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(lpData[i].getLpDate()));
                    res.put(i+") BasePulse Time", datef14.format(cal.getTime()));
                    for (int ch = 0; ch < lpData[i].getBasePulse().length; ch++) {
                        res.put(i+") BasePulse(kWh, kvarh) ch"+ch, decimalf.format(lpData[i].getBasePulse()[ch]));
                    }
                    res.put("", "");
                    res.put(i+") LP Time", "LP Value(kWh, kvarh)");
                    for (int j = 0; j < lpData[i].getLp()[0].length; cal.add(Calendar.MINUTE, interval), j++) {
                        lpTime = datef14.format(cal.getTime());
                        // if (DateTimeUtil.getDateString(cal.getTime()).compareTo(meteringTime) > 0) break;
                        for (int ch = 0; ch < lpData[i].getLp().length; ch++) {
                            res.put(i+") "+lpTime +" ch"+ch,
                                    decimalf.format((lpData[i].getLp()[ch][j]==65535? 0:lpData[i].getLp()[ch][j])));
                        }
                    }
                }
            }
            res.put("Kamstrup Meta Information", "");
            res.putAll(getData(meter));
        }
        catch (Exception e) {
            log.warn(e);
        }
        
        return res;
    }

    /**
     * parseing Energy Meter Data of Kamstrup Meter
     * @param data stream of result command
     */
    public void parseRegisterValues(byte[] data) throws Exception
    {
        int pos = 0;
        int len = 0;
        byte[] bx = null;
        while (pos < data.length) {
            len = DataUtil.getIntToByte(data[pos]);
            log.debug("KMP Register Len[" + len + "]");
            
            // 현재 위치에서 데이타를 가져올 수 없는 상황이 되면 종료
            if (len + pos > data.length) break;
            
            pos++;
            
            bx = new byte[len];
            System.arraycopy(data, pos, bx, 0, bx.length);
            pos += bx.length;
            
            parseKmpFrame(bx);
        }
    }
    
    private void parseKmpFrame(byte[] data) throws Exception
    {
        int pos = 0;
        int sof = (int)data[pos++];
        int addr = (int)data[pos++];
        // SOF,ADDR:1byte, CRC:2byte, EOF:1byte 제거한 길이만큼 가져온다.
        byte[] bx = new byte[data.length - 5];
        
        if (sof == 0x40 && addr == 0x3F) {
            System.arraycopy(data, pos, bx, 0, bx.length);
            parseOmnipowerData(bx);
        }
        else {
            log.warn("SOF[" + sof + "] ADDR[" + addr + "]");
        }
    }
    
    private ModemLPData makeLPData(RegisterValue[] regs) {
        ModemLPData lpData = new ModemLPData();
        lpData.setBasePulse(new double[regs.length-1]);
        
        for (int i = 0; i < regs.length; i++) {
            if (regs[i].getUnit() == UNIT_KAMDATETIME)
                lpData.setLpDate(formatValue(UNIT_KAMDATETIME, Hex.encode(regs[i].getValue()), 1)[0]);
            else {
                lpData.getBasePulse()[i-1] = Double.parseDouble(regs[i].getValue());
            }
        }
        
        return lpData;
    }
    
    private BillingData makeBillingData(RegisterValue[] regs) {
        BillingData bill = new BillingData();
        
        String strRid = null;
        for (int i = 0; i < regs.length; i++) {
            strRid = CID.LoggerReadOut.getRid(regs[i].getRid());
            
            if (regs[i].getUnit() == UNIT_KAMDATETIME)
                bill.setBillingTimestamp(formatValue(UNIT_KAMDATETIME, Hex.encode(regs[i].getValue()), 1)[0]);
            else if (strRid.equals("Active energy A14")){
                bill.setActiveEnergyImportRateTotal(Double.parseDouble(regs[i].getValue()));
            }
            else if (strRid.equals("Active energy A14 Tariff 1")) {
                bill.setActiveEnergyImportRate1(Double.parseDouble(regs[i].getValue()));
            }
            else if (strRid.equals("Active energy A14 Tariff 2")) {
                bill.setActiveEnergyImportRate2(Double.parseDouble(regs[i].getValue()));
            }
            else if (strRid.equals("Active energy A14 Tariff 3")) {
                bill.setActiveEnergyImportRate3(Double.parseDouble(regs[i].getValue()));
            }
            else if (strRid.equals("Max power P14 RTC")) {
                bill.setActivePowerDemandMaxTimeRateTotal(formatValue(UNIT_KAMDATETIME, Hex.encode(regs[i].getValue()), 1)[0]);
            }
            else if (strRid.equals("Max power P14")) {
                bill.setActivePowerMaxDemandRateTotal(Double.parseDouble(regs[i].getValue()));
            }
            else if (strRid.equals("Accumulated max power P14")) {
                bill.setCummActivePwrDmdMaxImportRateTotal(Double.parseDouble(regs[i].getValue()));
            }
        }
        
        return bill;
    }
    
    private void parseOmnipowerData(byte[] bx) throws Exception
    {
        log.debug(Hex.decode(bx));
        int pos = 0;
        
        byte cid = 0x00;
        byte[] rid = new byte[2];
        byte rUnit = 0x00;
        byte rNob = 0x00;
        byte rSiEx = 0x00;
        byte[] rvalue = null;
        
        while (pos < bx.length) {
            cid = bx[pos];
            
            CID ecid = KamstrupCIDMeta.getCid(cid);
            log.debug("CID[" + ecid + "]");
            if (ecid == CID.GetType) {
                makeValue(ecid, null, (byte)0x00, (byte)0x00, bx);
                String[] getType = (String[])ecid.getResponse(bx);
                log.info("MeterType[" + getType[0] + "] SW_Revision[" + getType[1] + "]");
                pos += 5;
            }
            else if (ecid == CID.GetRegister) {
                pos++;
                while (pos < bx.length) {
                    System.arraycopy(bx, pos, rid, 0, rid.length);
                    pos += rid.length;
                    
                    rUnit = bx[pos++];
                    rNob = bx[pos++];
                    rSiEx = bx[pos++];
    
                    rvalue = new byte[DataUtil.getIntToByte(rNob)];
                    System.arraycopy(bx, pos, rvalue, 0, rvalue.length);
                    pos += rvalue.length;
                    
                    makeValue(ecid, rid, rUnit, rSiEx, rvalue);
                }
            }
            else if (ecid == CID.LoggerReadOut) {
                SUB_CID subcid = null;
                String logType = "";
                int noReg = 0;
                byte[] LOG_ID = new byte[4];
                
                pos++;
                while(pos < bx.length) {
                    cid = bx[pos++];
                    
                    subcid = KamstrupCIDMeta.getSubCid(cid);
                    
                    logType = subcid.getLogType(bx[pos++]);
                    noReg = bx[pos++];
                    log.debug("Sub Command[" + subcid + "] LogType[" + logType + "] No Reg[" + noReg + "]");
                    
                    System.arraycopy(bx, pos, LOG_ID, 0, LOG_ID.length);
                    pos += LOG_ID.length;
                    log.debug("Log ID[" + DataUtil.getLongToBytes(LOG_ID) + "]");
                    
                    // register
                    if (noReg != 0) {
                        RegisterValue[] regValues = new RegisterValue[noReg];
                        String[] value = null;
                        for (int i = 0; i < noReg; i++) {
                            System.arraycopy(bx, pos, rid, 0, rid.length);
                            pos += rid.length;
                            
                            rUnit = bx[pos++];
                            rNob = bx[pos++];
                            rSiEx = bx[pos++];
            
                            rvalue = new byte[DataUtil.getIntToByte(rNob)];
                            System.arraycopy(bx, pos, rvalue, 0, rvalue.length);
                            pos += rvalue.length;
                            
                            // KamDateTime 아니면
                            if (rUnit != UNIT_KAMDATETIME) {
                                value = makeValue(rUnit, rSiEx, rvalue);
                            }
                            else {
                                value = new String[]{Hex.decode(rvalue)};
                            }
                            
                            regValues[i] = new RegisterValue(DataUtil.getIntTo2Byte(rid),
                                    rUnit, DataUtil.getIntToByte(rNob), rSiEx, value[0]);
    
                            valueMap.put(ecid+"."+logType+"."+
                            DataUtil.getLongToBytes(LOG_ID)+"."+
                                    CID.LoggerReadOut.getRid(DataUtil.getIntToBytes(rid)), value);
                            log.debug("BASE " + regValues[i].toString());
                        }
                        // Load Profile Logger를 저장한다.
                        if (logType.equals(LOG_LOAD_PROFILE)) {
                            ModemLPData _lpData = makeLPData(regValues);
                            _lpMap.put(_lpData.getLpDate(), _lpData);
                        }
                        else if (logType.equals(LOG_DEBITING)) {
                            billList.add(makeBillingData(regValues));
                        }
                        
                        // defualt rid를 찾아서 길이만큼 default interval를 건너뛴다.
                        System.arraycopy(bx, pos, rid, 0, rid.length);
                        pos += rid.length;
                        
                        int compLen = 0;
                        RegisterValue defaultInt = regValues[0];
                        
                        // rid가 0xFFFF 이면 default interval이 없다. 바로 log시작
                        if (!Hex.decode(rid).equals("FFFF")) {
                            int irid = DataUtil.getIntTo2Byte(rid);
                            
                            // default rid를 찾아서 데이타 길이만큼 스킵하고 비트 처리를 위한 초기화
                            for (int i = 0; i < regValues.length; i++) {
                                defaultInt = regValues[i];
                                if (defaultInt.getRid() == irid) {
                                    pos += defaultInt.getNob();
                                    break;
                                }
                            }
                        }
                        // comp 갯수는 register 사이즈의 % 2. 왜냐하면 
                        compLen = noReg % 2 == 0? noReg/2 : noReg/2+1;
                        log.debug("Default [" + defaultInt.toString() + "] Comp Val Len[" + compLen + "]");
                        
                        int sign = 1;
                        int compRegSize = 0;
                        // comp format을 담기 위한 변수. 4bit씩 처리해야 되므로 compLen의 두배로 초기화
                        int[] compFormat = new int[compLen*2];
                        
                        // new log id : 4bytes, info : 2bytes
                        while(pos + 6 < bx.length) {
                            for (int i = 0; i < compFormat.length; i+=2) {
                                // 첫 4비트를 오른쪽으로 시프트하여 정수를 만든다.
                                compFormat[i] = bx[pos] >> 4;
                                // 첫 4비트를 0으로 만든다.
                                compFormat[i+1] = (bx[pos++]&0x0F);
                            }
                            for (int i = 0; i < regValues.length; i++) {
                                // sign bit
                                sign *= (compFormat[i] & 0x08) != 0? -1:1;
                                // comp reg size
                                compRegSize = compFormat[i] & 0x07;
                                
                                // Reg 길이가 7이면 full value
                                if (compRegSize == 7) {
                                    rvalue = new byte[defaultInt.getNob()];
                                }
                                else {
                                    rvalue = new byte[compRegSize];
                                }
                                System.arraycopy(bx, pos, rvalue, 0, rvalue.length);
                                pos += rvalue.length;
                                
                                // KamDateTime 이 아니면
                                if (compRegSize == 7) {
                                    if (regValues[i].getUnit() != UNIT_KAMDATETIME) {
                                        regValues[i].setValue(makeValue(regValues[i].getUnit(), regValues[i].getSigEx(), rvalue)[0]);
                                    }
                                    else {
                                        regValues[i].setValue(Hex.decode(rvalue));
                                    }
                                }
                                else {
                                    if (regValues[i].getUnit() != UNIT_KAMDATETIME) {
                                        value = makeValue(regValues[i].getUnit(), regValues[i].getSigEx(), rvalue);
                                        regValues[i].setValue(Double.parseDouble(regValues[i].getValue()) + Double.parseDouble(value[0])+"");
                                        log.debug("Rvalue[" + Hex.decode(rvalue) + "] value[" + regValues[i].getValue() + "] unit[" + value[1] + "]");
                                        
                                        valueMap.put(ecid+"."+logType+"."+
                                                 DataUtil.getLongToBytes(LOG_ID)+"."+
                                                        CID.LoggerReadOut.getRid(regValues[i].getRid())+"." + pos, new String[]{regValues[i].getValue(), value[1]});
                                    }
                                    else {
                                        long kamdate = DataUtil.getLongToBytes(Hex.encode(regValues[i].getValue().substring(2)));
                                        if (logType.equals(LOG_LOAD_PROFILE) && rvalue.length == 0) {
                                            // rvalue 값이 없으면 주기를 넣는다.
                                            rvalue = DataUtil.get4ByteToInt(lpInterval * 60);
                                        }
                                        kamdate += (sign*DataUtil.getLongToBytes(rvalue));
                                        regValues[i].setValue(regValues[i].getValue().substring(0, 2) + 
                                                Hex.decode(DataUtil.get4ByteToInt(kamdate)));
                                        
                                        String[] fkamdate = formatValue(UNIT_KAMDATETIME, Hex.encode(regValues[i].getValue()), 0);
                                        log.debug("KamDateTime[" + fkamdate[0] + "," + fkamdate[1] + "] Rvalue[" + Hex.decode(rvalue) + "]");
                                        valueMap.put(ecid+"."+logType+"."+
                                                 DataUtil.getLongToBytes(LOG_ID)+"."+
                                                        CID.LoggerReadOut.getRid(regValues[i].getRid())+"." + pos, new String[]{regValues[i].getValue()});
                                    }
                                }
                                // log.debug(regValues[i].toString() + " Sign[" + sign + "] CompRegSize[" + compRegSize + "] Value[" + value[0] + "]");
                            }
                            
                            if (logType.equals(LOG_LOAD_PROFILE)) {
                                ModemLPData _lpData = makeLPData(regValues);
                                _lpMap.put(_lpData.getLpDate(), _lpData);
                            }
                            else if (logType.equals(LOG_DEBITING)) {
                                billList.add(makeBillingData(regValues));
                            }
                        }
                    }
                    
                    byte[] newLogId = new byte[4];
                    System.arraycopy(bx, pos, newLogId, 0, newLogId.length);
                    pos += newLogId.length;
                    log.debug("New log ID[" + DataUtil.getLongToBytes(newLogId) + "]");
                    
                    byte[] info = new byte[2];
                    System.arraycopy(bx, pos, info, 0, info.length);
                    pos += info.length;
                    
                }
                
                lpData = makeLPData(_lpMap);
            }
        }
        
    }
    
    public static String[] makeValue(byte bUnit, byte bSiEx, byte[] bValue) {
        double siEx = KamstrupCIDMeta.makeSiEx(bSiEx);//-1^SI*-1^SE*exponent

        return formatValue(DataUtil.getIntToByte(bUnit), bValue, siEx);//getUnit
    }
    
    private void makeValue(CID cid, byte[] bRid, byte bUnit, byte bSiEx, byte[] bValue)
    throws Exception {
        log.debug(Hex.decode(bValue));
        
        if (cid == CID.GetType) {
            String[] res = (String[])cid.getResponse(bValue);
            valueMap.put(cid.name(), res);
        }
        else if (cid == CID.GetRegister || cid == CID.LoggerReadOut) {
            String rid = cid.getRid(DataUtil.getIntTo2Byte(bRid));//getRid

            String[] value = makeValue(bUnit, bSiEx, bValue); //getUnit
            
            log.debug("[" + rid+"]: "+ value[0] + " " + value[1]);
            if (value[0] == null || "".equals(value[0]))
                value[0] = "0";
            
            valueMap.put(cid + "." + rid, value);
            
            if("Serial number".equals(rid)){
                meterId=value[0];
            }
            else if ("RTC".equals(rid)) {
                meterTime = value[0];
            }
            if("Active energy A14".equals(rid)){
                activeEnergyA14=Double.parseDouble(value[0]);
            }
            else if ("Active energy A23".equals(rid)) {
                activeEnergyA23=Double.parseDouble(value[0]);
            }
            else if ("Reactive energy R12".equals(rid)) {
                activeEnergyR12=Double.parseDouble(value[0]);
            }
            else if ("Reactive energy R34".equals(rid)) {
                activeEnergyR34=Double.parseDouble(value[0]);
            }
            else if("Active energy A14 Tariff 1".equals(rid)){
                activeEnergyA14Tariff1=Double.parseDouble(value[0]);
            }
            else if("Active energy A14 Tariff 2".equals(rid)){
                activeEnergyA14Tariff2=Double.parseDouble(value[0]);
            }
            else if("Active energy A14 Tariff 3".equals(rid)){
                activeEnergyA14Tariff3=Double.parseDouble(value[0]);
            }
            else if("Voltage L1".equals(rid)){
                voltageL1=Double.parseDouble(value[0]);
            }
            else if("Voltage L2".equals(rid)){
                voltageL2=Double.parseDouble(value[0]);
            }
            else if("Voltage L3".equals(rid)){
                voltageL3=Double.parseDouble(value[0]);
            }
            else if("Current L1".equals(rid)){
                currentL1=Double.parseDouble(value[0]);
            }
            else if("Current L2".equals(rid)){
                currentL2=Double.parseDouble(value[0]);
            }
            else if("Current L3".equals(rid)){
                currentL3=Double.parseDouble(value[0]);
            }
            else if("Hour counter".equals(rid)){
                hourCounter=Integer.parseInt(value[0]);
            }
            else if ("RTC status".equals(rid)) {
                rtcStatus = value[0];
            }
            else if("Pulse input".equals(rid)){
                pulseInput=Integer.parseInt(value[0]);
            }
            else if("Max power P14 RTC".equals(rid)) {
                maxPowerP14RTC = value[0];
            }
            else if("Max power P14 Tariff 1".equals(rid)) {
                maxPowerP14Tariff1=Double.parseDouble(value[0]);
            }
            else if ("Max power P14 Tariff 1 RTC".equals(rid)) {
                maxPowerP14Tariff1RTC = value[0];
            }
            else if("Max power P14 Tariff 2".equals(rid)){
                maxPowerP14Tariff2=Double.parseDouble(value[0]);
            }
            else if ("Max power P14 Tariff 2 RTC".equals(rid)) {
                maxPowerP14Tariff2RTC = value[0];
            }
            else if ("Connection status".equals(rid)) {
                connStatus = Integer.parseInt(value[0]);
            }
            else if ("Connection feedback".equals(rid)) {
                connFeedback = Integer.parseInt(value[0]);
            }
            else if ("Avg Voltage L1".equals(rid)) {
                avgVoltageL1 = Double.parseDouble(value[0]);
            }
            else if ("Avg Voltage L2".equals(rid)) {
                avgVoltageL2 = Double.parseDouble(value[0]);
            }
            else if ("Avg Voltage L3".equals(rid)) {
                avgVoltageL3 = Double.parseDouble(value[0]);
            }
            else if ("Avg Current L1".equals(rid)) {
                avgCurrentL1 = Double.parseDouble(value[0]);
            }
            else if ("Avg Current L2".equals(rid)) {
                avgCurrentL2 = Double.parseDouble(value[0]);
            }
            else if ("Avg Current L3".equals(rid)) {
                avgCurrentL3 = Double.parseDouble(value[0]);
            }
            else if ("Type number".equals(rid)) {
                typeNumber = value[0];
            }
            else if ("Meter status".equals(rid)) {
                meterStatus = Integer.parseInt(value[0]);
            }
            else if ("Operation mode".equals(rid)) {
                opMode = Integer.parseInt(value[0]);
            }
            else if ("SoftwareRevision".equals(rid)) {
                swRevision = value[0];
            }
        }
        this.meteringValue = new Double(activeEnergyA14);
    }
    
    public static String[] formatValue(int unit, byte[] bValue, double siEx ){
        int value = DataUtil.getIntToBytes(bValue);
        switch(unit){
        case 1:
            return new String[] {(value * siEx)+"", "Wh"};
        case 2:
            return new String[] {(value * siEx)+"", "kWh"};
        case 3:
            return new String[] {(value * siEx)+"", "MWh"};
        case 4:
            return new String[] {(value * siEx)+"", "GWh"};
        case 13:
            return new String[] {(value * siEx)+"", "varh"};
        case 14:
            return new String[] {(value * siEx)+"", "kvarh"};
        case 15:
            return new String[] {(value * siEx)+"", "Mvarh"};
        case 16:
            return new String[] {(value * siEx)+"", "Gvarh"};
        case 17:
            return new String[] {(value * siEx)+"", "VAh"};
        case 18:
            return new String[] {(value * siEx)+"", "kVAh"};
        case 19:
            return new String[] {(value * siEx)+"", "MVAh"};
        case 20:
            return new String[] {(value * siEx)+"", "GVAh"};
        case 21:
            return new String[] {(value * siEx)+"", "W"};
        case 22:
            return new String[] {(value * siEx)+"", "kW"};
        case 23:
            return new String[] {(value * siEx)+"", "MW"};
        case 24:
            return new String[] {(value * siEx)+"", "GW"};
        case 25:
            return new String[] {(value * siEx)+"", "var"};
        case 26:
            return new String[] {(value * siEx)+"", "kvar"};
        case 27:
            return new String[] {(value * siEx)+"", "Mvar"};
        case 28:
            return new String[] {(value * siEx)+"", "Gvar"};
        case 29:
            return new String[] {(value * siEx)+"", "VA"};
        case 30:
            return new String[] {(value * siEx)+"", "kVA"};
        case 31:
            return new String[] {(value * siEx)+"", "MVA"};
        case 32:
            return new String[] {(value * siEx)+"", "GVA"};
        case 33:
            return new String[] {(value * siEx)+"", "V"};
        case 34:
            return new String[] {(value * siEx)+"", "A"};
        case 35:
            return new String[] {(value * siEx)+"", "kV"};
        case 36:
            return new String[] {(value * siEx)+"", "kA"};
        case 37:
            return new String[] {(value * siEx)+"", "c"};
        case 38:
            return new String[] {(value * siEx)+"", "K"};
        case 39:
            return new String[] {(value * siEx)+"", "l"};
        case 40:
            return new String[] {(value * siEx)+"", "m3"};
        case 46:
            return new String[] {value+"", "h"};
        case 47:
            return new String[] {value+"", ""};
        case 48:
            return new String[] {"20" + value+"", ""};
        case 49:
            return new String[] {value+"", ""};
        case 50:
            return new String[] {value+"", ""};
        case 51:
            return new String[] {value+"", ""};
        case 63:
        case 53: // RTC
            String rtc = "20";
            int temp = DataUtil.getIntToByte(bValue[7]);
            rtc += String.format("%02d", temp);
            temp = DataUtil.getIntToByte(bValue[6]);
            rtc += String.format("%02d", temp);
            temp = DataUtil.getIntToByte(bValue[5]);
            rtc += String.format("%02d", temp);
            temp = DataUtil.getIntToByte(bValue[4]);
            rtc += String.format("%02d", temp);
            temp = DataUtil.getIntToByte(bValue[3]);
            rtc += String.format("%02d", temp);
            temp = DataUtil.getIntToByte(bValue[2]);
            rtc += String.format("%02d", temp);
            return new String[] {rtc+"", "RTC"};
        case 54:
            String ascii = new String(bValue);
            return new String[] {ascii, "ASCII coded data"};
        case 55:
            return new String[] {(value * siEx)+"", "m3 x 10"};
        case 56:
            return new String[] {(value * siEx)+"", "ton x 10"};
        case 57:
            return new String[] {(value * siEx)+"", "Gj x 10"};
        case 59:
            return new String[] {Hex.decode(bValue), "The value should be decoded bit vise"};
        case 60:
            return new String[] {value+"", "s"};
        case 61:
            return new String[] {value+"", "ms"};
        case 62:
            return new String[] {value+"", ""};
        case 67:
            return new String[] {value+"", "Hz"};
        case 68:
            return new String[] {value+"", ""};
        case 72:
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                Calendar cal = Calendar.getInstance();
                // KamDateTime은 2000년 1월 1일 00시 00분 00초부터 초 값으로 되어 있다.
                cal.setTime(sdf.parse("20000101000000"));
                long s = DataUtil.getLongToBytes(new byte[]{bValue[1], bValue[2], bValue[3], bValue[4]});
                cal.setTimeInMillis(cal.getTimeInMillis() + (s * 1000)); 
                return new String[] {sdf.format(cal.getTime()), ""};
            }
            catch (Exception e) {
                return new String[]{e.getMessage(), ""};
            }
        default:
            return new String[] {Hex.decode(bValue), ""};
        }

    }
    
    public LinkedHashMap<String, String> getData(Meter meter) {
        this.meter = meter;
        return getMetaData();
    }

    /**
     * get Data
     */
    @SuppressWarnings("unchecked")
    private LinkedHashMap<String, String> getMetaData()
    {
        //lplist = getLPData(bao.toByteArray(),period);
        LinkedHashMap<String, String> res = new LinkedHashMap<String, String>(16,0.75f,false);
        DecimalFormat decimalf=null;
        SimpleDateFormat datef14=null;
        SimpleDateFormat timef = null;
        SimpleDateFormat datef = null;
         
        if(meter!=null && meter.getSupplier()!=null){
            Supplier supplier = meter.getSupplier();
            if(supplier !=null){
                String lang = supplier.getLang().getCode_2letter();
                String country = supplier.getCountry().getCode_2letter();
                
                decimalf = TimeLocaleUtil.getDecimalFormat(supplier);
                datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
                timef = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(6, lang, country));
                datef = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(8, lang, country));
            }
        }else{
            //locail 정보가 없을때는 기본 포멧을 사용한다.
            decimalf = new DecimalFormat();
            datef14 = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
            datef = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT);
            timef = (SimpleDateFormat)DateFormat.getTimeInstance(DateFormat.MEDIUM);
        }

        String rid = null;
        String[] value = null;
        for (Iterator<String> i = valueMap.keySet().iterator(); i.hasNext(); ) {
            rid = i.next();
            value = valueMap.get(rid);
            if (rid.contains("RTC") && !rid.contains("RTC Status")) {
                try {
                    if (value[0].equals("20000000000000")) {
                        res.put(rid, "cannot format[" + value[0].substring(2)+"]");
                    }
                    else {
                        if (rid.contains("LoggerReadOut")) {
                            res.put(rid, datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(formatValue(UNIT_KAMDATETIME, Hex.encode(value[0]), 1)[0])));
                        }
                        else {
                            res.put(rid, datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(value[0])));
                        }
                    }
                } catch (ParseException e) {
                    log.warn(rid + " " + e);
                }
            }
            else if (rid.contains("Clock")) {
                try {
                    res.put(rid, timef.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS("00000000"+value[0])));
                } catch (ParseException e) {
                    log.warn(e);
                }
            }
            else if (rid.contains("Date")) {
                try {
                    res.put(rid, datef.format(DateTimeUtil.getDateFromYYYYMMDD(value[0])));
                } catch (ParseException e) {
                    log.warn(e);
                }
            }
            else {
                res.put(rid, value[0] + " " + value[1]);
            }
        }

        return res;
    }
    
    public Instrument[] getInstrument(){
        Instrument[] instruments = new Instrument[1];
        Instrument inst = new Instrument();
        inst.setVOL_A(voltageL1);
        inst.setVOL_B(voltageL2);
        inst.setVOL_C(voltageL3);
        inst.setCURR_A(currentL1);
        inst.setCURR_B(currentL2);
        inst.setCURR_C(currentL3);
        instruments[0] = inst;
        return instruments;
    }
    
    class RegisterValue {
        private int rid;
        private byte unit;
        private int nob;
        private byte sigEx;
        private String value;
        
        RegisterValue(int rid, byte unit, int nob, byte sigEx, String value) {
            this.rid = rid;
            this.unit = unit;
            this.nob = nob;
            this.sigEx = sigEx;
            this.value = value;
        }

        public int getRid() {
            return rid;
        }

        public void setRid(int rid) {
            this.rid = rid;
        }

        public byte getUnit() {
            return unit;
        }

        public void setUnit(byte unit) {
            this.unit = unit;
        }

        public int getNob() {
            return nob;
        }

        public void setNob(int nob) {
            this.nob = nob;
        }

        public byte getSigEx() {
            return sigEx;
        }

        public void setSigEx(byte sigEx) {
            this.sigEx = sigEx;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
        
        
        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("RID[" + CID.GetRegister.getRid(rid) + "]" +
            "Nob[" + nob + "]" +
                "Unit[" + CID.GetRegister.getUnit(unit) + "]" +
                    "Value[" + value + "]");
            if (unit == UNIT_KAMDATETIME) {
                buf.append("Convert[" + formatValue(unit, Hex.encode(value), 1)[0]+"]");
            }
            
            return buf.toString();
        }
    }
}
