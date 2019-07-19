package com.aimir.fep.meter.parser;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.command.conf.KamstrupCIDMeta;
import com.aimir.fep.command.conf.KamstrupCIDMeta.CID;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing Kamstrup Meter Data
 *
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2008-06-30 12:32:15 +0900 $,
 */
public class Kamstrup extends MeterDataParser implements java.io.Serializable
{
	private static final long serialVersionUID = 5930336984016579743L;

	private static Log _log = LogFactory.getLog(Kamstrup.class);

    private byte[] rawData = null;
    private Double meteringValue = null;
    LPData[] lplist = null;
    private String meterId = null;
    private boolean meterStatus = true;
    private Hashtable<String, String> oidValues =  new Hashtable<String, String>();
    private double activeEnergyA14 = 0; //1.8.0
    private double activeEnergyA23 = 0;
    private double reactiveEnergyR12 = 0;
    private double reactiveEnergyR34 = 0;
    private double activeEnergyA14Tariff1 = 0; //1.8.1
    private double activeEnergyA14Tariff2 = 0; //1.8.2
    private double activeEnergyA14Tariff3 = 0; //1.8.3
    private double activeEnergyA14Tariff4 = 0; //1.8.4
    private double voltageL1 = 0;
    private double voltageL2 = 0;
    private double voltageL3 = 0;
    private double currentL1 = 0;
    private double currentL2 = 0;
    private double currentL3 = 0;
    private int hourCounter = 0; //96.8.0
    private int pulseInput = 0; //0.128.0
    private double maxPowerP14Tariff1 = 0; //1.6.0
    private String maxPowerP14Tariff1Date;
    private double maxPowerP14Tariff2 = 0;
    private String maxPowerP14Tariff2Date;
    
    private Map<String, String[]> valueMap = new LinkedHashMap<String, String[]>();
    private Map<String, ModemLPData> lpMap = new LinkedHashMap<String, ModemLPData>();
    
    private int lpInterval = 0;

    /**
     * constructor
     */
    public Kamstrup()
    {
    }

    /**
     * get Metering Value
     */
    public Double getMeteringValue()
    {
        return this.meteringValue;
    }

    public LPData[] getLPData(){
        return lplist;
    }

    public LPData[] getLPData(int resolution){
        _log.debug("LP raw Data: "+Hex.decode(rawData));
        int period = 60 / resolution;
        
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        //Cid
        bao.write(rawData, 0,rawData.length);
        
        byte[] year = new byte[2];
        byte[] month = new byte[1];
        byte[] day = new byte[1];
        byte[] basePulse = new byte[4];
        byte[] lp = new byte[2];

        List<LPData> list = new ArrayList<LPData>();
        LPData _lpData = null;
        int _year = 0;
        String _strYear="";
        int _month = 0;
        int _day = 0;

        int endIdx = Hex.decode(rawData).indexOf("03E9")/2;
        if(endIdx == -1)
            endIdx = rawData.length;
        //for (int pos = 0; pos < data.length; ) {
        for (int pos = 18; pos < endIdx; ) {
            System.arraycopy(rawData, pos, year, 0, year.length);
            pos += year.length;
            System.arraycopy(rawData, pos, month, 0, month.length);
            pos += month.length;
            System.arraycopy(rawData, pos, day, 0, day.length);
            pos += day.length;
            System.arraycopy(rawData, pos, basePulse, 0, basePulse.length);
            pos += basePulse.length;

            _year = DataUtil.getIntTo2Byte(year);
            if(_year<10){
                _strYear="000"+_year;
            }else if(_year<100){
                _strYear="00"+_year;
            }else if(_year<1000){
                _strYear="0"+_year;
            }else{
                _strYear=""+_year;
            }
            _month = DataUtil.getIntToBytes(month);
            _day = DataUtil.getIntToBytes(day);

            _lpData = new LPData();
            // _lpData.setPeriod(period);
            _lpData.setDatetime(_strYear + (_month < 10? "0"+_month:""+_month)
                                  + (_day < 10? "0"+_day:""+_day));
            _lpData.setBasePulse(DataUtil.getLongToBytes(basePulse));
            _lpData.setCh(new Double[24*period]);
            for (int j = 0; j < _lpData.getCh().length; j++) {
                lp = new byte[2];
                System.arraycopy(rawData, pos, lp, 0, lp.length);
                pos += lp.length;
                _lpData.getCh()[j] = (double)DataUtil.getIntTo2Byte(lp);
            }
            list.add(_lpData);
            _log.debug(_lpData.toString());
        }
        lplist = (LPData[])list.toArray(new LPData[list.size()]);
        return lplist;
    }

    /**
     * get meter id
     * @return meter id
     */
    public String getMeterId()
    {
        return meterId;
    }

    /**
     * get raw Data
     */
    public byte[] getRawData()
    {
        return this.rawData;
    }

    public int getLength()
    {
        return this.rawData.length;
    }

    public boolean getMeterStatus() {
        return meterStatus;
    }

    public ModemLPData[] getModemLPData() {
        TreeMap<String, ModemLPData> t = new TreeMap<>(lpMap);   // Sorted by key
        return t.values().toArray(new ModemLPData[0]);
    }
    
    /**
     * parseing Energy Meter Data of Kamstrup Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception
    {
        int period=0;
        int startIdx=0;
        this.rawData = data;
        _log.debug("Data : "+Hex.decode(data));
        int len = 0;
        byte sof = (byte)0x00;
        byte addr = (byte)0x00;
        byte cid = (byte)0x00;
        int[] nob;
        int logType = 0;
        int nor = 0;
        int logId = 0;
        int tempLogId = 0;
        
        if (data.length == 0)
            return;
        
        if(DataUtil.getIntTo2Byte(data[0], data[1])!=1001){
            period=DataUtil.getIntToByte(data[15]);
            if(period==0){
                startIdx=18;
            }else{
                startIdx = Hex.decode(data).indexOf("03E9")/2;
                if(startIdx == -1)
                    startIdx=26+2*(24*period);
            }
        }

        byte[] bx = null;
        byte[][] rid = null;
        byte[] unit = null;
        byte[] siEx = null;
        byte temp[] = null;
        for (int pos=0, offset=0; pos < data.length;offset=0) {
            len = DataUtil.getIntToByte(data[pos]);
            
            if (pos + len >= data.length) break;
            
            pos++;
            bx = new byte[len];
            System.arraycopy(data, pos, bx, 0, len);
            pos += len;
            sof = bx[offset++];
            addr = bx[offset++];
            cid = bx[offset++];
            
            log.debug("Header[" + Hex.decode(new byte[]{sof,addr,cid}) + "] SOF[" + sof + "] ADDR[" + addr + "] CID[" + cid + "] LEN[" + len + "]");
            CID ecid = KamstrupCIDMeta.getCid(cid);
            
            if (ecid == CID.GetType) {
                String[] getType = (String[])ecid.getResponse(new byte[]{0x00, bx[3], 0x00, bx[6], bx[5]});
                log.info("MeterType[" + getType[0] + "] SW_Revision[" + getType[1] + "]");
                // pos += 4;
            }
            else if (ecid == CID.GetLogTimePresent || ecid == CID.GetLogLastPresent 
                    || ecid == CID.GetLogIDPresent || ecid == CID.GetLogTimePast) {
                logType = DataUtil.getIntToByte(bx[offset++]);
                nor = DataUtil.getIntToByte(bx[offset++]);
                log.debug("LogType[" + logType + "] No. of Register[" + nor + "]");
                rid = new byte[nor][];
                unit = new byte[nor];
                siEx = new byte[nor];
                nob = new int[nor];
                
                for (int idx=1; nor > 0;idx++) {
                    if (logId == (tempLogId=DataUtil.getIntTo2Byte(new byte[]{bx[offset++], bx[offset++]}))) {
                        break;
                    }
                    else logId = tempLogId;
                    log.debug("LogId[" + logId + "]");
                    for (int i = 0; i < nor; i++) {
                        if (idx == 1) {
                            rid[i] = new byte[]{bx[offset], bx[offset+1]};
                            unit[i] = bx[offset+2];
                            nob[i] = DataUtil.getIntToByte(bx[offset+3]);
                            siEx[i] = bx[offset+4];
                            
                            temp = new byte[nob[i]];
                            System.arraycopy(bx, 5+offset, temp, 0, temp.length);
                            
                            offset = offset+5+nob[i];
                        }
                        else {
                            temp = new byte[nob[i]];
                            System.arraycopy(bx, offset, temp, 0, temp.length);
                            offset = offset+nob[i];
                        }
                        
                        meterTime = makeValue(cid, nor, logType, logId, idx, rid[i], unit[i], siEx[i], temp, meterTime);
                    }
                }
            }
            else if (cid == (byte)0xAA) {
                byte regSetup = bx[offset++];
                byte logInterval = bx[offset++];
                byte loggerSize = bx[offset++];
                log.debug("REGSETUP[" + Hex.decode(new byte[]{regSetup})+
                        "] LOGINTERVAL[" + Hex.decode(new byte[]{logInterval}) +
                        "] LOGGERSIZE[" + Hex.decode(new byte[]{loggerSize}) + "]");
                lpInterval = DataUtil.getIntToByte(logInterval);
            }
            else {
                nob = new int[1];
                String rtc = "";
                for (; offset < len-4; ) {
                    nob[0] = DataUtil.getIntToByte(bx[3+offset]);//getValueCount
                    temp = new byte[nob[0]];
                    System.arraycopy(bx, 5+offset, temp, 0, temp.length);
                    
                    rtc = makeValue(cid, nor, logType, logId, 0, new byte[]{bx[offset], bx[1+offset]},
                            bx[2+offset],
                            bx[4+offset],
                            temp,
                            rtc);
                    
                    offset = offset+5+nob[0];
                }
            }
        }
        _log.debug("Kamstrup Data Parse Finished :: DATA["+toString()+"]");
    }
    
    
    public static String[] makeValue(byte bUnit, byte bSiEx, byte[] bValue) {
        int signInt = (bSiEx & 128)/128;
        int signExp = (bSiEx & 64)/64;
        int exp = ((bSiEx&32) + (bSiEx&16) + (bSiEx&8) + (bSiEx&4) + (bSiEx&2) + (bSiEx&1));
        double siEx = Math.pow(-1, signInt)*Math.pow(10, Math.pow(-1, signExp)*exp);//-1^SI*-1^SE*exponent

        return formatValue(DataUtil.getIntToByte(bUnit), bValue, siEx);//getUnit
    }
    
    public static double makeSiEx(byte bSiEx) {
        int signInt = (bSiEx & 128)/128;
        int signExp = (bSiEx & 64)/64;
        int exp = ((bSiEx&32) + (bSiEx&16) + (bSiEx&8) + (bSiEx&4) + (bSiEx&2) + (bSiEx&1));
        double siEx = Math.pow(-1, signInt)*Math.pow(10, Math.pow(-1, signExp)*exp);//-1^SI*-1^SE*exponent
        return siEx;
    }
    
    private String makeValue(byte cid, int nor, int logType, int logId, int logIdx, byte[] bRid, byte bUnit, byte bSiEx, byte[] bValue, String rtc) {
        String rid = getRid(DataUtil.getIntTo2Byte(bRid));//getRid
        double siEx = makeSiEx(bSiEx);

        String[] value = makeValue(bUnit, bSiEx, bValue); //getUnit
        
        valueMap.put(Hex.decode(new byte[]{cid}) + "." + logId + "." + logIdx + ". " + rid, value);
        
        _log.debug("[" + logIdx + ". " + rid+"]: "+ value[0] + " " + value[1]);
        if (value[0] == null || "".equals(value[0]))
            value[0] = "0";
        
        if("Serial number".equals(rid)){
            meterId=value[0];
        }
        
        if (logIdx == 0 && rid.equals("RTC"))
            meterTime = value[0];
        
        if (cid == 0x10) {
            if("Active energy A14".equals(rid)){
                activeEnergyA14=Double.parseDouble(value[0]);
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
            else if("Active energy A14 Tariff 4".equals(rid)){
                activeEnergyA14Tariff4=Double.parseDouble(value[0]);
            }
            else if ("Active energy A23".equals(rid)) {
                activeEnergyA23 = Double.parseDouble(value[0]);
            }
            else if ("Reactive energy R12".equals(rid)) {
                reactiveEnergyR12 = Double.parseDouble(value[0]);
            }
            else if ("Reactive energy R34".equals(rid)) {
                reactiveEnergyR34 = Double.parseDouble(value[0]);
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
            else if("Pulse input".equals(rid)){
                pulseInput=Integer.parseInt(value[0]);
            }
            else if("Max power P14 Tariff 1".equals(rid)) {
                maxPowerP14Tariff1=Double.parseDouble(value[0]);
            }
            else if("Max power P14 Tariff 2".equals(rid)){
                maxPowerP14Tariff2=Double.parseDouble(value[0]);
            }
            else if("Max power P14 Tariff 1 RTC".equals(rid)) {
                maxPowerP14Tariff1Date=value[0].substring(0, 8);
            }
            else if("Max power P14 Tariff 2 RTC".equals(rid)) {
                maxPowerP14Tariff2Date=value[0].substring(0, 8);
            }
        }
        
        /*
        else if("Peak Power Date".equals(rid)){
            for(int i=0;i<(6-value[0].length());i++){
                peakPowerDate+="0";
            }
            peakPowerDate="20"+peakPowerDate+value[0];
            peakPowerDate=peakPowerDate.substring(0, 4)+":"+peakPowerDate.substring(4, 6)+":"+peakPowerDate.substring(6, 8);
        }
        */
        this.meteringValue = new Double(activeEnergyA14);
        
        // ModemLPData를 생성한다.
        // CID A2가 LP 채널 값이다. 변환하지 않는다.
        if ((cid == (byte)0xA0 || cid == (byte)0xA1 || cid == (byte)0xA2 || cid == (byte)0xA3) && logType == 6) {
            if ("RTC".equals(rid)) {
                rtc = value[0];
            }
            
            // cid+"."+logId 가 키
            ModemLPData lpData = null;
            log.debug("RTC[" + rtc + "]");
            if ((lpData = lpMap.get(rtc)) == null) {
                lpData = new ModemLPData();
                lpData.setBasePulse(new double[0]);
                lpData.setLpDate(rtc);
            }
            else {
                if (lpData.getBasePulse().length == 4) return rtc;
            }
            
            if ("Active energy A14".equals(rid) || "Active energy A23".equals(rid) ||
                    "Reactive energy R12".equals(rid) || "Reactive energy R34".equals(rid)) {
                // base pulse
                List<Double> bp = new ArrayList<Double>();
                for (int i = 0; i < lpData.getBasePulse().length; i++) {
                    bp.add(lpData.getBasePulse()[i]);
                }
                bp.add(Double.parseDouble(value[0]));
                
                lpData.setBasePulse(new double[bp.size()]);
                for (int i = 0; i < bp.size(); i++) {
                    lpData.getBasePulse()[i] = bp.get(i).doubleValue();
                }
            }
            log.debug("BASEVALUE SIZE[" + lpData.getBasePulse().length + "]");
            
            lpMap.put(rtc, lpData);
        }
        
        return rtc;
    }
    
    private void makeOidValues(String data) throws Exception
    {
        String[] lines = data.split("\n");

        String[] items;
        int idx;
        String oid;
        String value;
        for(int i = 0 ; i < lines.length ; i++)
        {
            idx = lines[i].indexOf("(");
            if(idx < 1)
                continue;
            oid = lines[i].substring(0,idx);
            value = lines[i].substring(idx+1);
            idx = value.indexOf(")");
            if(idx < 1)
                continue;
            value = value.substring(0,idx);

            oid = oid.trim();
            _log.debug("oid["+oid+"] value["+value+"]");
            oidValues.put(oid,value);
        }
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("Kamstrup Meter DATA[");
        sb.append("(meteringValue=").append(meteringValue).append("),");
        sb.append("(meterId=").append(meterId).append("),");
        sb.append("(activeEnergyA14=").append(activeEnergyA14).append("),");
        sb.append("(activeEnergyA23=").append(activeEnergyA23).append("),");
        sb.append("(reactiveEnergyR12=").append(reactiveEnergyR12).append("),");
        sb.append("(reactiveEnergyR34=").append(reactiveEnergyR34).append("),");
        sb.append("(activeEnergyA14Tariff1=").append(activeEnergyA14Tariff1).append("),");
        sb.append("(activeEnergyA14Tariff2=").append(activeEnergyA14Tariff2).append("),");
        sb.append("(activeEnergyA14Tariff3=").append(activeEnergyA14Tariff3).append("),");
        sb.append("(activeEnergyA14Tariff4=").append(activeEnergyA14Tariff4).append("),");
        sb.append("(voltageL1=").append(voltageL1).append("),");
        sb.append("(voltageL2=").append(voltageL2).append("),");
        sb.append("(voltageL3=").append(voltageL3).append("),");
        sb.append("(currentL1=").append(currentL1).append("),");
        sb.append("(currentL2=").append(currentL2).append("),");
        sb.append("(currentL3=").append(currentL3).append("),");
        sb.append("(hourCounter=").append(hourCounter).append("),");
        sb.append("(pulseInput=").append(pulseInput).append("),");
        sb.append("(maxPowerP14Tariff1=").append(maxPowerP14Tariff1).append(')');
        sb.append("(maxPowerP14Tariff1Date=").append(maxPowerP14Tariff1Date).append(')');
        sb.append("(maxPowerP14Tariff2=").append(maxPowerP14Tariff2).append(')');
        sb.append("(maxPowerP14Tariff2Date=").append(maxPowerP14Tariff2Date).append(')');
        sb.append("]\n");

        return sb.toString();
    }
    
    public LinkedHashMap getData(Meter meter) {
        this.meter = meter;
        return getData();
    }

    /**
     * get Data
     */
    @SuppressWarnings("unchecked")
    @Override
    public LinkedHashMap getData()
    {
        //lplist = getLPData(bao.toByteArray(),period);
        LinkedHashMap res = new LinkedHashMap(16,0.75f,false);
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
                        res.put(rid, datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(value[0])));
                    }
                } catch (ParseException e) {
                    log.warn(e);
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

        if(lplist != null && lplist.length > 0){
            int nbr_chn=1;
            ArrayList chartData0 = new ArrayList();//time chart
            ArrayList[] chartDatas = new ArrayList[nbr_chn]; //channel chart(ch1,ch2,...)
            for(int k = 0; k < nbr_chn ; k++){
                chartDatas[k] = new ArrayList();
            }
            ArrayList lpDataTime = new ArrayList();
            for(int i = 0; i < lplist.length; i++){
                String datetime = lplist[i].getDatetime();//yyyymmdd
                for(int j=0;j<lplist[i].getCh().length;j++){
                    String tempDateTime = datetime+(j<10? "0"+j : j)+"00";//yyyymmddhhmm
                    String val = "";
                    val=(lplist[i].getCh())[j]+"";
                    res.put("LP"+tempDateTime+"0000", val);

                    chartData0.add(tempDateTime.substring(6,8)
                                  +tempDateTime.substring(8,10)
                                  +tempDateTime.substring(10,12));
                    chartDatas[0].add(Double.parseDouble(val));
                    lpDataTime.add(tempDateTime+"00");
                }
            }
            //res.put("lplist", lplist);
            //res.put("chartData0", chartData0);
            //res.put("lpDataTime", lpDataTime);
            //res.put("chartDatas", chartDatas);
        }
        return res;
    }

    public int getFlag()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setFlag(int flag)
    {
        // TODO Auto-generated method stub

    }

    public String getRid(int rid){
        switch(rid){
        case 1:
            return "Active energy A14";
        case 2:
            return "Active energy A23";
        case 1031:
            return "Active energy A1234";
        case 3:
            return "Reactive energy R12";
        case 4:
            return "Reactive energy R34";
        case 5:
            return "Reactive energy R1";
        case 6:
            return "Reactive energy R4";
        case 13 :
            return "Active energy A14";
        case 14 :
            return "Active energy A23";
        case 15 :
            return "Reactive energy R12";
        case 16 :
            return "Reactive energy R34";
        case 19:
            return "Active energy A14 Tariff 1";
        case 23:
            return "Active energy A14 Tariff 2";
        case 27:
            return "Active energy A14 Tariff 3";
        case 31:
            return "Active energy A14 Tariff 4";
        case 1059:
            return "Active energy A14 Tariff 5";
        case 1060:
            return "Active energy A14 Tariff 6";
        case 1061:
            return "Active energy A14 Tariff 7";
        case 1062:
            return "Active energy A14 Tariff 8";
        case 20:
            return "Active energy A23 Tariff 1";
        case 24:
            return "Active energy A23 Tariff 2";
        case 28:
            return "Active energy A23 Tariff 3";
        case 32:
            return "Active energy A23 Tariff 4";
        case 1063:
            return "Active energy A23 Tariff 5";
        case 1064:
            return "Active energy A23 Tariff 6";
        case 1065:
            return "Active energy A23 Tariff 7";
        case 1066:
            return "Active energy A23 Tariff 8";
        case 21:
            return "Reactive energy R12 Tariff 1";
        case 25:
            return "Reactive energy R12 Tariff 2";
        case 29:
            return "Reactive energy R12 Tariff 3";
        case 33:
            return "Reactive energy R12 Tariff 4";
        case 1067:
            return "Reactive energy R12 Tariff 5";
        case 1068:
            return "Reactive energy R12 Tariff 6";
        case 1069:
            return "Reactive energy R12 Tariff 7";
        case 1070:
            return "Reactive energy R12 Tariff 8";
        case 22:
            return "Reactive energy R34 Tariff 1";
        case 26:
            return "Reactive energy R34 Tariff 2";
        case 30:
            return "Reactive energy R34 Tariff 3";
        case 34:
            return "Reactive energy R34 Tariff 4";
        case 1071:
            return "Reactive energy R34 Tariff 5";
        case 1072:
            return "Reactive energy R34 Tariff 6";
        case 1073:
            return "Reactive energy R34 Tariff 7";
        case 1074:
            return "Reactive energy R34 Tariff 8";
        case 17:
            return "Resetable counter A14";
        case 18:
            return "Resetable counter A23";
        case 39:
            return "Max power P14";
        case 40:
            return "Max power P23";
        case 41:
            return "Max power Q12";
        case 42:
            return "Max power Q34";
        case 1023:
            return "Actual power P14";
        case 1024:
            return "Actual power P23";
        case 1025:
            return "Actual power Q12";
        case 1026:
            return "Actual power Q34";
        case 43:
            return "Accumulated max power P14";
        case 44:
            return "Accumulated max power P23";
        case 45:
            return "Accumulated max power Q12";
        case 46:
            return "Accumulated max power Q34";
        case 47:
            return "Number of debiting periods";
        case 1049:
            return "Max power P14 RTC";
        case 58:
            return "Pulse input";
        case 1004:
            return "Hour counter";
        case 1002:
            return "Clock";
        case 1003:
            return "Date";
        case 1047:
            return "RTC";
        case 54:
            return "Configurations number 1";
        case 55:
            return "Configurations number 2";
        case 56:
            return "Configurations number 3";
        case 1029:
            return "Configurations number 4";
        case 1075:
            return "Configurations number 5";
        case 57:
            return "Special Data 1";
        case 1021:
            return "Special Data 2";
        case 1010:
            return "Total meter number";
        case 51:
            return "Meter number 1";
        case 52:
            return "Meter number 2";
        case 53:
            return "Meter number 3";
        case 50:
            return "Meter status";
        case 1001:
            return "Serial number";
        case 1058:
            return "Type number";
        case 2010:
            return "Active tariff";
        case 1033:
            return "Max power P14 Tariff 1";
        case 1050:
            return "Max power P14 Tariff 1 RTC";
        case 1036:
            return "Max power P14 Tariff 2";
        case 1051:
            return "Max power P14 Tariff 2 RTC";
        case 1038:
            return "Peak Power Date";
        case 1039:
            return "Power threshold value";
        case 1040:
            return "Power threshold counter";
        case 1045:
            return "RTC Status";
        case 1046:
            return "VCOPCO Status";
        case 1054:
            return "Voltage L1";
        case 1055:
            return "Voltage L2";
        case 1056:
            return "Voltage L3";
        case 1076:
            return "Current L1";
        case 1077:
            return "Current L2";
        case 1078:
            return "Current L3";
        case 1080:
            return "Actual power P14 L1";
        case 1081:
            return "Actual power P14 L2";
        case 1082:
            return "Actual power P14 L3";
        case 1083:
            return "ROM checksum";
        case 1005:
            return "Software revision";
        case 1084:
            return "Voltage extremity";
        case 1085:
            return "Voltage event";
        case 1086:
            return "Logger status";
        case 1087:
            return "Connection status";
        case 1088:
            return "Connection feedback";
        case 1102:
            return "Module port I/O configuration";
        case 1175 :
            return "Debitinglogger2 loginterval";
        case 1190 :
            return "P14 Maximum";
        case 1191 :
            return "P14 Minimum";
        case 1193 :
            return "LegalLoggerDepth";
        case 1194 :
            return "AnalysisLoggerDepth";
        case 1196 :
            return "P14 Maximum time";
        case 1197 :
            return "P14 Maximum Date";
        case 1198 :
            return "P14 Maximum RTC";
        case 1199 :
            return "P14 Minimum time";
        case 1200 :
            return "P14 Minimum Date";
        case 1201 :
            return "P14 Minimum RTC";
        case 1215 :
            return "Avg Voltage L1";
        case 1216 :
            return "Avg Voltage L2";
        case 1217 :
            return "Avg Voltage L3";
        case 1218 :
            return "Avg Current L1";
        case 1219 :
            return "Avg Current L2";
        case 1220 :
            return "Avg Current L3";
        case 1222 :
            return "LoadProfile Event Status";
        case 1224 :
            return "Logger status 2";
        // Additional registers for 351B
        case 7 :
            return "Seconday active energy A14";
        case 8 :
            return "Secondary active energy A23";
        case 9 : 
            return "Secondary reactive energy R12";
        case 10 :
            return "Secondary reactive energy R34";
        case 11 :
            return "Secondary reactive energy R1";
        case 12 :
            return "Secondary reacitve energy R4";
        case 1138 :
            return "Secondary active energy A14 Tariff 1";
        case 1139 :
            return "Secondary active energy A14 Tariff 2";
        case 1140 :
            return "Secondary active energy A14 Tariff 3";
        case 1141 :
            return "Secondary active energy A14 Tariff 4";
        case 1142 :
            return "Secondary active energy A14 Tariff 5";
        case 1143 :
            return "Secondary active energy A14 Tariff 6";
        case 1144 :
            return "Secondary active energy A14 Tariff 7";
        case 1145 :
            return "Secondary active energy A14 Tariff 8";
        case 1146 :
            return "Secondary active energy A23 Tariff 1";
        case 1147 :
            return "Secondary active energy A23 Tariff 2";
        case 1148 :
            return "Secondary active energy A23 Tariff 3";
        case 1149 :
            return "Secondary active energy A23 Tariff 4";
        case 1150 :
            return "Secondary active energy A23 Tariff 5";
        case 1151 :
            return "Secondary active energy A23 Tariff 6";
        case 1152 :
            return "Secondary active energy A23 Tariff 7";
        case 1153 :
            return "Secondary active energy A23 Tariff 8";
        case 1154 :
            return "Secondary reactive energy R12 Tariff 1";
        case 1155 :
            return "Secondary reactive energy R12 Tariff 2";
        case 1156 :
            return "Secondary reactive energy R12 Tariff 3";
        case 1157 :
            return "Secondary reactive energy R12 Tariff 4";
        case 1158 :
            return "Secondary reactive energy R12 Tariff 5";
        case 1159 :
            return "Secondary reactive energy R12 Tariff 6";
        case 1160 :
            return "Secondary reactive energy R12 Tariff 7";
        case 1161 :
            return "Secondary reactive energy R12 Tariff 8";
        case 1162 :
            return "Secondary reactive energy R34 Tariff 1";
        case 1163 :
            return "Secondary reactive energy R34 Tariff 2";
        case 1164 :
            return "Secondary reactive energy R34 Tariff 3";
        case 1165 :
            return "Secondary reactive energy R34 Tariff 4";
        case 1166 :
            return "Secondary reactive energy R34 Tariff 5";
        case 1167 :
            return "Secondary reactive energy R34 Tariff 6";
        case 1168 :
            return "Secondary reactive energy R34 Tariff 7";
        case 1169 :
            return "Secondary reactive energy R34 Tariff 8";
        case 1170 :
            return "Power factor L1";
        case 1171 :
            return "Power factor L2";
        case 1172 :
            return "Power factor L3";
        case 1173 :
            return "Total power factor";
        default:
            return "Unknown[" + rid + "]";
        }
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
            return new String[] {(value)+"", "h"};
        case 47:
            return new String[] {value+"", ""};
        case 48:
            return new String[] {"20" + value+"", ""};
        case 51:
            return new String[] {(value)+"", ""};
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
        default:
            return new String[] {"", ""};
        }

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
    
    public Map<String, String[]> getValue() {
        return this.valueMap;
    }
    
    public int getLpInterval() {
        return this.lpInterval;
    }

	public Map<String, ModemLPData> getLpMap() {
		return lpMap;
	}
    
}
