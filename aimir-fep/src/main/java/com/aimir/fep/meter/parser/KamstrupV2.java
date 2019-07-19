package com.aimir.fep.meter.parser;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.MeteringFail;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing Kamstrup 162 Meter Data
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class KamstrupV2 extends MeterDataParser implements java.io.Serializable
{
    private static final long serialVersionUID = 1974450760100885924L;

    private static Log log = LogFactory.getLog(KamstrupV2.class);

    private byte[] PERIOD = new byte[1];
    private byte[] CM = new byte[1];
    private byte[] TF = new byte[3];
    private byte[][] CF;
    private byte[] Last = new byte[2];
    private byte[] CNT = new byte[2];

    private byte[] rawData = null;
    private Double meteringValue = null;
    private int flag = 0;
    private String meterId = null;
    private int period = 0;
    private int errorCode = 0;

    private Kamstrup kamstrupMeta = null;

    private ModemLPData[] lpData = null;
    private EventLogData[] eventlogdata = null;

    public KamstrupV2() {
    }

    /**
     * constructor
     */
    public KamstrupV2(String meterId)
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

    public Kamstrup getKamstrupMeta()
    {
        return kamstrupMeta;
    }

    public void setKamstrupMeta(Kamstrup kamstrupMeta)
    {
        this.kamstrupMeta = kamstrupMeta;
    }

    public void parse(byte[] data) throws Exception
    {
        parseModem(data);
    }
    
    /**
     * parse meter mesurement data
     * @param data
     */
    public void parseModem(byte[] data) throws Exception
    {
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
        Map<String, ModemLPData> _lpMap = new HashMap<String, ModemLPData>(); // ModemLPData[] _lpData = new ModemLPData[cnt];
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
            lpTime = Kamstrup.formatValue(tfUnit, LT, 0);
            _lpData.setLpDate(lpTime[0]); // lpTime[1]은 단위
            // 채널 개수만큼 LP를 가져온다.
            baseValueList = new double[ch_cnt];
            for (int ch = 0; ch < ch_cnt; ch++) {
                LP = new byte[DataUtil.getIntToByte(CF[ch][1])]; // CF의 두번째 바이트 길이로 설정
                System.arraycopy(data, pos, LP, 0, LP.length);
                pos += LP.length;
                
                log.debug(Hex.decode(LP));
                values = Kamstrup.formatValue(CF[ch][0], LP, Kamstrup.makeSiEx(CF[ch][2]));
                baseValueList[ch] = Double.parseDouble(values[0]);
                log.debug("Base Value["+ values[0] + "] Unit[" + values[1] + "]");
            }
            _lpData.setBasePulse(baseValueList);
            // log.debug(_lpData.toString());
            _lpMap.put(_lpData.getLpDate(), _lpData);
        }
        log.debug("Response LP size[" + cnt + "] Real LP size[" + _lpMap.size() + "]");
        
        int lpInterval = 60 / period;
        
        // KMP 미터 정보를 파싱한다.
        byte[] km = new byte[data.length - pos];
        System.arraycopy(data, pos, km, 0, km.length);
        kamstrupMeta = new Kamstrup();
        kamstrupMeta.setMeter(meter);
        kamstrupMeta.parse(km);
        this.meterTime = kamstrupMeta.getMeterTime();
        
        // 현재검침값
        this.meteringValue = kamstrupMeta.getMeteringValue();

        //kmp lp를 우선 처리
        if(kamstrupMeta.getLpMap() != null && kamstrupMeta.getLpMap().size() > 1){
        	log.info("KMP LP DATA SIZE=["+_lpMap.size()+"]");
        	_lpMap = kamstrupMeta.getLpMap();
        }
        
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
                    // meteringValue보다 누적값이 더 크면 입력한다.
                    if (this.meteringValue < slp2.getBasePulse()[0]) {
                        this.meteringValue = slp2.getBasePulse()[0];
                    }
                    
                    for (int ch = 0; ch < lpData[i].getBasePulse().length; ch++) {
                        // log.debug("i:"+i+", ch:"+ch+", s:"+s);
                        lpData[i].getLp()[ch][s] = slp2.getBasePulse()[ch] - slp1.getBasePulse()[ch];
                    }
                }
                // log.debug(lpData[i].toString());
            }
        }
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

        sb.append("Kamstrup162 DATA[");
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
    @SuppressWarnings("unchecked")
    @Override
    public LinkedHashMap getData()
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
        
        LinkedHashMap res = new LinkedHashMap(16,0.75f,false);
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
            res.putAll(kamstrupMeta.getData(meter));
        }
        catch (Exception e) {
            log.warn(e);
        }
        
        return res;
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
}
