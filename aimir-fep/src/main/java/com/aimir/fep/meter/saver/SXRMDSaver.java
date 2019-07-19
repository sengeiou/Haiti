package com.aimir.fep.meter.saver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.RDataParser;
import com.aimir.fep.meter.parser.rdata.BPList;
import com.aimir.fep.meter.parser.rdata.LPList;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.MeteringLP;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

@Service
public class SXRMDSaver extends AbstractMDSaver {

    @Override
    protected boolean save(IMeasurementData md) throws Exception {
        RDataParser parser = (RDataParser)md.getMeterDataParser();
        
        // BP Count 만큼 마지막 검침값을 저장한다.
        // TODO lastValue를 계산하기 위해서 meterconfiguration의 sigExp를 가져와서 계산해야 되나
        // Wh, mA 단위로 오기 때문에 1000, 100으로 나눈다.
        String garbageDate = null;
        Map<String, Double> baseMap = new HashMap<String, Double>();
        for (BPList bplist : parser.getBpLists().toArray(new BPList[0])) {
            saveMeteringData(MeteringType.Normal, bplist.getLastTime().substring(0, 8),
                    bplist.getLastTime().substring(8)+"00", bplist.getLastValues().get(0).doubleValue()*0.001,
                    parser.getMeter(), parser.getDeviceType(), parser.getDeviceId(),
                    parser.getMDevType(), parser.getMDevId(), parser.getMeterTime());
            
            // base time이 0000이 아닌데 lp time과 오차범위 안이면 lp time 시간 기준 base value로 사용한다.
            // 오차를 벗어나면 쓰레기 값으로 간주한다.
            log.debug("BAET_TIME[" + bplist.getBaseTime() + "]");
            if (!bplist.getBaseTime().substring(8).equals("0000")) {
                // base time과 lp time을 비교하여 주기내의 오차면 base value를 사용한다.
                Calendar _basetime = Calendar.getInstance();
                _basetime.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(bplist.getBaseTime()+"00"));
                Calendar _lptime = Calendar.getInstance();
                _lptime.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(parser.getLpLists().get(0).getLpTime()+ "00"));
                log.debug("LP_TIME[" + parser.getLpLists().get(0).getLpTime() + "]");
                Calendar _lptime_min_lpinterval = (Calendar)_lptime.clone();
                _lptime_min_lpinterval.add(Calendar.MINUTE, -parser.getMeter().getLpInterval());
                
                if (_basetime.compareTo(_lptime_min_lpinterval) < 0 || _basetime.compareTo(_lptime) >= 0) {
                    garbageDate = bplist.getBaseTime().substring(0, 8);
                    log.debug("GARBAGE_DATE[" + garbageDate + "]");
                }
                else {
                    baseMap.put(bplist.getBaseTime().substring(0,8), bplist.getLastValues().get(0).doubleValue() * 0.001);
                    if (log.isDebugEnabled()) {
                        log.debug("BASE_DATE[" + bplist.getBaseTime().substring(0, 8) + 
                                "] BASE_VALUE[" + bplist.getLastValues().get(0).doubleValue() * 0.001 + "]");
                    }
                }
            }
            else {
                baseMap.put(bplist.getBaseTime().substring(0,8), bplist.getLastValues().get(0).doubleValue() * 0.001);
                if (log.isDebugEnabled()) {
                    log.debug("BASE_DATE[" + bplist.getBaseTime().substring(0, 8) + 
                            "] BASE_VALUE[" + bplist.getLastValues().get(0).doubleValue() * 0.001 + "]");
                }
            }
        }
        
        // LP 데이타 저장
        HashSet<Condition> condition = null;
        MeteringLP mlp = null;
        String lpdate = null;
        List lplist = null;
        double baseValue = 0.0;
        
        // LP list를 정리한다. garbageDate의 lp는 없앤다.
        List<LPList> newLplist = new ArrayList<LPList>();
        // LP 리스트이 첫번째 time을 가져온다.
        lpdate = parser.getLpLists().get(0).getLpTime();
        for (LPList _lplist : parser.getLpLists().toArray(new LPList[0])) {
            if (garbageDate == null || "".equals(garbageDate) || !_lplist.getLpTime().substring(0,8).equals(garbageDate)) {
                newLplist.add(_lplist);
            }
        }
        
        // newLplist의 lpTime이 시간순서대로 되어 있는지 체크하여 누락된 것이 있으면 실패처리
        lpdate = newLplist.get(0).getLpTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(lpdate+"00"));
        for (int i = 0; i < newLplist.size(); i++) {
            log.debug("SEQ_LP_TIME[" + lpdate + "] LP_TIME[" + newLplist.get(i).getLpTime() + "]");
            if (!lpdate.equals(newLplist.get(i).getLpTime())) {
                LPList _lplist = new LPList();
                _lplist.setLpTime(lpdate);
                newLplist.add(i, _lplist);
            }
            // LP 주기를 더한다.
            cal.add(Calendar.MINUTE, parser.getMeter().getLpInterval());
            lpdate = DateTimeUtil.getDateString(cal.getTime()).substring(0,12);
        }
        
        // BASE TIME의 HHMM이 0000이고 LP TIME이 0000아니면 DB에서 BASE VALUE룰 구한다.
        String bptime = parser.getBpLists().get(0).getBaseTime();
        lpdate = newLplist.get(0).getLpTime();
        String lptime = lpdate.substring(8,12);
        baseValue = baseMap.get(lpdate.substring(0,8));
        if (bptime.substring(8, 12).equals("0000") && !lpdate.substring(8, 12).equals("0000")) {
            // 분이 00이면 전시간 값을 가져온다.
            if (lpdate.substring(10,12).equals("00")) {
                lpdate = DateTimeUtil.getPreHour(lpdate, 1);
            }
            log.info("LP DATE[" + lpdate + "]");
            // lpDate의 lp를 조회해서 값이 있는 것까지의 주기를 구하고 base를 위해 value를 계산한다.
            condition = new HashSet<Condition>();
            condition.add(new Condition("id.mdevType", new Object[]{parser.getMDevType()}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevId", new Object[]{parser.getMDevId()}, null, Restriction.EQ));
            // condition.add(new Condition("id.dst", new Object[]{dst}, null, Restriction.EQ));
            condition.add(new Condition("id.yyyymmddhh", new Object[]{lpdate.substring(0,10)}, null, Restriction.EQ));
            condition.add(new Condition("id.channel", new Object[]{1}, null, Restriction.EQ));
            
            switch (MeterType.valueOf(parser.getMeter().getMeterType().getName())) {
            case EnergyMeter :
                lplist = lpEMDao.getLpEMsByListCondition(condition); 
                break;
            case WaterMeter :
                lplist = lpWMDao.getLpWMsByListCondition(condition);
                break;
            case GasMeter :
                lplist = lpGMDao.getLpGMsByListCondition(condition);
                break;
            case HeatMeter :
                lplist = lpHMDao.getLpHMsByListCondition(condition);
            }
            
            // 갸져온 LP 리스트에서 base value를 계산한다. 리스트에는 한개의 LP가 있어야 한다.
            // 만약 없으면 baseMap의 값을 그대로 사용한다. 위험하지만 어쩔 수 없음.
            if (lplist.size() > 0) {
                mlp = (MeteringLP)lplist.get(0);
                baseValue = mlp.getValue();
                String strLp = null;
                int hh = Integer.parseInt(mlp.getHour());
                // 00 ~ 60분까지 lp값을 더하다가 LPTIME과 시분이 같게 되면 종료한다.
                for (int mm = 0; mm < 60; mm += parser.getMeter().getLpInterval()) {
                    if (mm == 60) {
                        hh++;
                        mm = 0;
                    }
                    
                    if (lptime.equals((hh<10?"0":"")+hh+(mm<10?"0":"")+mm))
                        break;
                            
                    strLp = BeanUtils.getProperty(mlp, "value_"+ (mm<10? "0":"")+mm);
                    if (strLp != null && !"".equals(strLp)) {
                        baseValue += Double.parseDouble(strLp);
                    }
                }
            }
        }
        // 위의 로직에 의해서 basetime은 lpdate와 같게 된다.
        log.info("BASETIME[" + lpdate + "] BASEVALUE[" + baseValue + "]");
        
        // flaglist를 생성한다.
        int[] flaglist = new int[newLplist.size()];
        for (int i = 0; i < flaglist.length; i++) {
            if (newLplist.get(i).getLpValues() == null || newLplist.get(i).getLpValues().size() == 0)
                flaglist[i] = MeteringFlag.Fail.getFlag();
            else
                flaglist[i] = MeteringFlag.Correct.getFlag();
        }
        
        // 채널 배열을 생성한다.
        double[][] chlp = new double[parser.getChannelCount()][newLplist.size()];
        LPList _lplist = null;
        for (int lpcnt=0; lpcnt < newLplist.size(); lpcnt++) {
            _lplist = newLplist.get(lpcnt);
            if (_lplist.getLpValues() != null) {
                for (int chcnt = 0; chcnt < _lplist.getLpValues().size(); chcnt++) {
                    switch (chcnt) {
                    case 0 :
                    case 1 :
                    case 4 :
                        chlp[chcnt][lpcnt] = _lplist.getLpValues().get(chcnt);
                        if (chlp[chcnt][lpcnt] == 65535) {
                            chlp[chcnt][lpcnt] = 0;
                            flaglist[lpcnt] = MeteringFlag.Fail.getFlag();
                        }
                        else {
                            chlp[chcnt][lpcnt] *= 0.001;
                        }
                        break;
                    case 2 :
                    case 3 :
                    case 5 :
                        chlp[chcnt][lpcnt] = _lplist.getLpValues().get(chcnt);
                        if (chlp[chcnt][lpcnt] == 65535) {
                            chlp[chcnt][lpcnt] = 0;
                            flaglist[lpcnt] = MeteringFlag.Fail.getFlag();
                        }
                        else {
                            chlp[chcnt][lpcnt] *= 0.01;
                        }
                        break;
                    }
                        
                }
            }
        }
        
        saveLPData(MeteringType.Normal, lpdate.substring(0,8), lpdate.substring(8, 12),
                chlp, flaglist, baseValue, parser.getMeter(),
                parser.getDeviceType(), parser.getDeviceId(), parser.getMDevType(), parser.getMDevId());
        
        // TODO 로그 처리 로직을 추가해야 한다.
        return true;
    }
    
    @Override
    public String relayValveStatus(String mcuId, String meterId) {
        String rtnStr = null;
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            //SX2 미터 Relay Status
            Integer energyLevel = (int)commandGw.cmdGetEnergyLevel(meter.getMcu().getSysID(), meter.getModem().getDeviceSerial());
    
            final String RELAYON = "Normal";
            final String RELAYOFF = "RelayOff";
            
            String meterStatus=null;
            //SX2 미터는 1,15 의 Level 만 지원한다. 1=Relay On, 15=Relay Off
            switch (energyLevel) {
            case 1:
                meterStatus = RELAYON;
                break;
            case 15:
                meterStatus = RELAYOFF;
                break;
            default:
                break;
            }
            
            if(meterStatus!=null){
                Code code = CommonConstants.getMeterStatusByName(meterStatus);
                meter.setMeterStatus(code);
                rtnStr=String.format("Status : %s", code.getName());
                
                Contract contract = meter.getContract();
                if (meterStatus.equals(RELAYON)) {
                    Code pauseCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.NORMAL.getCode());
                    contract.setStatus(pauseCode);
                    contractDao.update(contract);
                }
                else if (meterStatus.equals(RELAYOFF)) {
                    Code pauseCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.PAUSE.getCode());
                    contract.setStatus(pauseCode);
                    contractDao.update(contract);
                }
            }else{
                rtnStr=String.format("Fail, energy level : %d", energyLevel);
            }
        }
        catch (Exception e) {
            rtnStr = "failReason : " + e.getMessage();
        }
        
        return MapToJSON(new String[]{rtnStr});
    }

    @Override
    public String relayValveOn(String mcuId, String meterId) {
        String rtnStr = null;
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            //1=RelayOn, 15=RelayOff
            final String SET_ENERGYLEVEL = "1";
            
            //SX2 미터 Relay command
            commandGw.cmdSetEnergyLevel(meter.getMcu().getSysID(), meter.getModem().getDeviceSerial(), SET_ENERGYLEVEL);
            
            meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));
            meterDao.update(meter);
            
            Contract contract = meter.getContract();
            if(contract != null) {
                Code pauseCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.NORMAL.getCode());
                contract.setStatus(pauseCode);
                contractDao.update(contract);
            }
            rtnStr=String.format("Status : %s", meter.getMeterStatus().getName());
        }
        catch (Exception e) {
            rtnStr = "failReason : " + e.getMessage();
        }
        
        return MapToJSON(new String[]{rtnStr});
    }
    
    @Override
    public String relayValveOff(String mcuId, String meterId) {
        String rtnStr = null;
        try {
            Meter meter = meterDao.get(meterId);
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            
            //1=RelayOn, 15=RelayOff
            final String SET_ENERGYLEVEL = "15";
            
            //SX2 미터 Relay command
            commandGw.cmdSetEnergyLevel(meter.getMcu().getSysID(), meter.getModem().getDeviceSerial(), SET_ENERGYLEVEL);
            
            meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()));
            meterDao.update(meter);
            
            Contract contract = meter.getContract();
            if(contract != null) {
                Code pauseCode = codeDao.getCodeIdByCodeObject(CommonConstants.ContractStatus.PAUSE.getCode());
                contract.setStatus(pauseCode);
                contractDao.update(contract);
            }
            rtnStr=String.format("Status : %s", meter.getMeterStatus().getName());
        }
        catch (Exception e) {
            rtnStr = "failReason : " + e.getMessage();
        }
        
        return MapToJSON(new String[]{rtnStr});
    }
    
    @Override
    public String syncTime(String mcuId, String meterId) {
        Meter meter = meterDao.get(meterId);
        String[] result = null;
        
        try {
            CommandGW commandGw = DataUtil.getBean(CommandGW.class);
            byte[] bx = commandGw.cmdMeterTimeSync(mcuId,meter.getMdsId());
            
            saveMeterTimeSyncLog(meter, result[0], result[1], 1);
        }
        catch (Exception e) {
            result = new String[]{e.getMessage()};
        }
        
        return MapToJSON(result);
    }
}
