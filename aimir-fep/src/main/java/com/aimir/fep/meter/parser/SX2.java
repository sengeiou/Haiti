package com.aimir.fep.meter.parser;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.mvm.ChannelConfig;
import com.aimir.model.system.MeterConfig;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * SX2 계량기 파서  MultiChannelParser를 이용
 * Meter Data 파싱을 위해서 상속받음.
 * 
 * @author elevas
 * @since 2012.10.12
 */
public class SX2 extends MultiChannelParser {
    private static Log log = LogFactory.getLog(SX2.class);
    
    private Map<MeterDataTable, String> mdMap = new HashMap<MeterDataTable, String>();
    
    private List<EventLogData> eventLogDatas = new ArrayList<EventLogData>();
    
    public enum MeterDataTable {
        MeterIndentificationNumber(0x00, 7, "", ""),
        DateIssueOfSoftware(0x02, 6, "ddMMyy", ""),
        RateVoltage(0x05, 3, "V", ""),
        BasicCurrent(0x06, 2, "A", ""),
        MaximumCurrent(0x07, 3, "A", ""),
        RatedFrequency(0x08, 2, "Hz", ""),
        RelayContactStatus(0xF4, 2, "", ""),
        DateOfFutureFirmware(0xF5, 6, "ddMMyy", ""),
        DateOfDefaultFirmware(0xF6, 6, "ddMMyy", ""),
        ClearEnergyRegisterFlag(0xF7, 1, "", ""),
        RMSVoltage(0xD0, 5, "mV", ""),
        RMSNeutralCurrent(0xD1, 5, "mA", ""),
        RMSLineCurrent(0xD2, 5, "mA", ""),
        ForwardEnergyRegiser(0xD5, 9, "Wh", ""),
        ReverseEnergyRegister(0xD6, 9, "Wh", ""),
        PowerFactor(0xEC, 4, "", ""),
        ReverseCurrentLogPacket(0xD9, 0, "", "Reverse Current"),
        TerminalCoverLogPacket(0xDA, 0, "", "T-Cover Open"),
        EarthLoadLogPacket(0xDB, 0, "", "Earth Load"),
        BypassMainlineLogPacket(0xDC, 0, "", "Bypass Mainline"),
        OverloadCurrentLogPacket(0xDD, 0, "", "Overload Current"),
        FrontCoverLogPacket(0xE8, 0, "", "F-Cover Open"),
        TamperCodeDisplayStatus(0xE7, 6, "", ""),
        ClockSynchronization(0x34, 12, "yyMMddHHmmss", ""), 
        BatteryStatus(0x35, 2, "", "");         // 00:Low, 01:Normal
        
        private int id;
        private int size;
        private String unit;
        private String msg;
        
        MeterDataTable(int id, int size, String unit, String msg) {
            this.id = id;
            this.size = size;
            this.unit = unit;
            this.msg = msg;
        }
        
        public int getId() {
            return id;
        }
        
        public String getUnit() {
            return unit;
        }
        
        public String getMsg() {
            return msg;
        }
    }
    
    public void parse(byte[] data) throws Exception {
        super.parse(data);
        
        // 환산
        // Current 값 변환
        for (int i = 0; i < channelInfos.length; i++) {
            if (i == 0 || i == 1) channelInfos[i].setCurrentValue(channelInfos[i].getCurrentValue()/1000.0);
            else channelInfos[i].setCurrentValue(channelInfos[i].getCurrentValue());
        }
        
        this.meteringValue = channelInfos[0].getCurrentValue();
        
        // LP Data 환산
        for (int i = 0; i < lpData.length; i++) {
            for (int ch = 0; ch < lpData[i].getBasePulse().length; ch++) {
                
                // base value 환산
                if (ch == 0 || ch == 1) lpData[i].setBasePulse(ch, lpData[i].getBasePulse()[ch] / 1000.0);
                
                // lp 환산
                for (int j = 0; j < lpData[i].getLp()[ch].length; j++) {
                    if (lpData[i].getLp()[ch][j] == 65535) {
                        log.debug(lpData[i].getLp()[ch][j]);
                        continue;
                    }
                    
                    if (ch == 0 || ch == 1) lpData[i].setLp(ch, j, lpData[i].getLp()[ch][j] / 1000.0);
                }
            }
        }
        
        // SX2 Meter Data를 변환한다.
        MeterData[] mdList = getMeterDatas();
        
        int id = 0x00;
        for (MeterData md : mdList) {
            id = DataUtil.getIntToBytes(md.getId());
            for (MeterDataTable mdt : MeterDataTable.values()) {
                if (mdt.getId() == id) {
                    switch (mdt) {
                    case DateIssueOfSoftware :
                    case DateOfFutureFirmware :
                    case DateOfDefaultFirmware :
                        SimpleDateFormat sdf = new SimpleDateFormat(mdt.getUnit());
                        mdMap.put(mdt, DateTimeUtil.getDateString(sdf.parse(new String(md.getValue()))));
                        break;
                    case ClockSynchronization :
                        sdf = new SimpleDateFormat(mdt.getUnit());
                        byte[] bx = Hex.encode(new String(md.getValue()));
                        String dt = "";
                        for (int i = 0; i < bx.length; i++) {
                            dt += String.format("%02d", DataUtil.getIntToBytes(new byte[]{bx[i]}));
                        }
                        mdMap.put(mdt, DateTimeUtil.getDateString(sdf.parse(dt)));
                        break;
                    case ReverseCurrentLogPacket :
                    case TerminalCoverLogPacket :
                    case EarthLoadLogPacket :
                    case BypassMainlineLogPacket :
                    case OverloadCurrentLogPacket :
                    case FrontCoverLogPacket :
                        EventLogData el = new EventLogData();
                        el.setDate(this.getTimestamp().substring(0,8));
                        el.setTime(this.getTimestamp().substring(8));
                        el.setKind("STE");
                        el.setFlag(mdt.getId());
                        el.setMsg(mdt.getMsg());
                        eventLogDatas.add(el);
                        break;
                    default :
                        mdMap.put(mdt, new String(md.getValue()));
                    }
                }
            }
        }
    }
    
    public EventLogData[] getEventLogData() {
        return eventLogDatas.toArray(new EventLogData[0]);
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
        SimpleDateFormat datef8=null;
         
        if(meter!=null && meter.getSupplier()!=null){
            Supplier supplier = meter.getSupplier();
            if(supplier !=null){
                String lang = supplier.getLang().getCode_2letter();
                String country = supplier.getCountry().getCode_2letter();
                
                decimalf = TimeLocaleUtil.getDecimalFormat(supplier);
                datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
                datef8 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(8, lang, country));
            }
        }else{
            //locail 정보가 없을때는 기본 포멧을 사용한다.
            decimalf = new DecimalFormat();
            datef14 = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        }
        
        LinkedHashMap res = new LinkedHashMap();
        try {
            MeterConfig mc = (MeterConfig)meter.getModel().getDeviceConfig();
            ChannelConfig[] ccs = mc.getChannels().toArray(new ChannelConfig[0]);
            
            Arrays.sort(ccs, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    ChannelConfig c1 = (ChannelConfig)o1;
                    ChannelConfig c2 = (ChannelConfig)o2;
                    
                    return c1.getChannelIndex() - c2.getChannelIndex();
                }
                
            });
            
            res.put("Big Endian", meteringInfo.isBigEndian());
            res.put("Metering Time", datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meteringInfo.getTimestamp())));
            res.put("CH Count",  meteringInfo.getChCount());
            res.put("Period", meteringInfo.getPeriod());
            res.put("LP Data Count", meteringInfo.getLpCount());
            int interval = 60 / meteringInfo.getPeriod();
            
            res.put("Channel Info", "");
            for (int i = 0; i < channelInfos.length; i++) {
                res.put(i + ".Channel Type", "[" +channelInfos[i].getChannelType()+"]"+ccs[i].getChannel().getName()+"("+ccs[i].getChannel().getUnit()+")");
                res.put(i + ".Meter Constant",  channelInfos[i].getMeterConstant());
                res.put(i + ".Current Value",  decimalf.format(channelInfos[i].getCurrentValue()));
            }
            String lpTime = "";
            
            for (int i = 0; i < lpData.length; i++) {
                res.put(i+".BasePulse Time", datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(lpData[i].getLpDate()+"000000")));
                for (ChannelConfig cc : ccs) {
                    res.put(i+"."+cc.getChannel().getName()+"("+cc.getChannel().getUnit()+")",
                            decimalf.format(lpData[i].getBasePulse()[cc.getChannelIndex()-1]));
                }
                res.put(i+".LP Data", datef8.format(DateTimeUtil.getDateFromYYYYMMDD(lpData[i].getLpDate())));
                for (int j = 0, hour = 0, min = 0; j < lpData[i].getLp()[0].length; j++, min+=interval) {
                    if (min >= 60) {
                        hour++;
                        min = 0;
                    }
                    
                    if (hour == 24) break;
                    for(ChannelConfig cc : ccs) {
                        res.put(i+"."+"LP." + String.format("%02d%02d", hour, min) + "." + cc.getChannel().getName()+"("+cc.getChannel().getUnit()+")",
                                decimalf.format((lpData[i].getLp()[cc.getChannelIndex()-1][j]==65535? 0:lpData[i].getLp()[cc.getChannelIndex()-1][j])));
                    }
                }
            }
            
            if (mdMap.size() > 0)
                res.put("Meter Data", "===============================================");
            
            for (MeterDataTable mdt : mdMap.keySet().toArray(new MeterDataTable[0])) {
                switch (mdt) {
                case DateIssueOfSoftware :
                case DateOfFutureFirmware :
                case DateOfDefaultFirmware :
                    res.put(mdt.name(), datef8.format(DateTimeUtil.getDateFromYYYYMMDD(mdMap.get(mdt))));
                    break;
                case ClockSynchronization :
                    res.put(mdt.name(), datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(mdMap.get(mdt))));
                    break;
                default :
                    if (mdt.getUnit() != null && !"".equals(mdt.getUnit()))
                        res.put(mdt.name(), decimalf.format(Double.parseDouble(mdMap.get(mdt))) + " " + mdt.getUnit());
                    else
                        res.put(mdt.name(), mdMap.get(mdt));
                }
            }
            
            if (eventLogDatas.size() > 0)
                res.put("Meter Event", "===============================================");
            
            for (EventLogData el : getEventLogData()) {
                res.put(el.getMsg(), datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(el.getDate()+el.getTime())));
            }
        }
        catch (ParseException e) {
            log.warn(e);
        }
        
        return res;
    }
    
    public LinkedHashMap getRelayStatus() {
        LinkedHashMap map = new LinkedHashMap();
        String status = mdMap.get(MeterDataTable.RelayContactStatus);
        map.put("relay status", status.equals("00")? "Open":"Close");
        return map;
    }
}
