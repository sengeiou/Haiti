package com.aimir.fep.meter.saver;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.MeteringFlag;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.fep.meter.AbstractMDSaver;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.MeteringLP;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

@Service
@Transactional
public class OmniMDSaver extends AbstractMDSaver {
    @Override
    protected boolean save(IMeasurementData md) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    public String save(String meterNo, String dcuNo, String modemNo, String meteringTime,
            double meteringValue, int lpInterval, double[] lplist)
    throws Exception
    {
        log.info("meterNo[" + meterNo + "] dcuNo[" + dcuNo + "] modemNo[" + modemNo + "]"
                +"meteringTime[" + meteringTime + "] meteringValue[" + meteringValue + "]"
                +"lpInterval[" + lpInterval + "]");
        StringBuffer lpbuf = new StringBuffer();
        for (int i = 0; i < lplist.length; i++) {
            lpbuf.append(lplist[i]+",");
        }
        log.debug("LP[" + lpbuf.toString() + "]");
        
        try {
            Meter meter = meterDao.get(meterNo);
            meter.setLpInterval(lpInterval);
            
            saveMeteringData(MeteringType.Normal, meteringTime.substring(0, 8), meteringTime.substring(8)+"00",
                    meteringValue, meter, DeviceType.MCU, dcuNo, DeviceType.Meter, meterNo, null);
            
            int[] flaglist = new int[lplist.length];
            for (int i = 0 ; i < flaglist.length; i++) {
                // 검침실패
                if (lplist[i] == 65535) {
                    flaglist[i] = MeteringFlag.Fail.getFlag();
                    lplist[i] = 0.0;
                }
                else flaglist[i] = MeteringFlag.Correct.getFlag();
            }
            
            saveLPData(MeteringType.Manual, meteringTime.substring(0, 8), meteringTime.substring(8), 
                    new double[][]{lplist}, flaglist, meteringValue, meter, DeviceType.MCU, 
                    dcuNo, DeviceType.Meter, meterNo);
            
            meter.setLastReadDate(meteringTime+"00");
            if (meter.getModem() != null) {
                meter.getModem().setLastLinkTime(meter.getLastReadDate());
                modemDao.update(meter.getModem());
            }
            meterDao.update(meter);
            
            return "success";
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            return "fail : " + e.getMessage();
        }
    }
    
    /**
     * base 값을 가져오는 저장 로직
     * @param meterNo
     * @param dcuNo
     * @param modemNo
     * @param meteringTime
     * @param meteringValue
     * @param lpInterval
     * @param lplist
     * @return
     * @throws Exception
     */
    public String save1(String meterNo, String dcuNo, String modemNo,
            String meteringTime, double meteringValue, int lpInterval, double[] lplist)
    throws Exception
    {
        log.info("meterNo[" + meterNo + "] dcuNo[" + dcuNo + "] modemNo[" + modemNo + "]"
                +"meteringTime[" + meteringTime + "] meteringValue[" + meteringValue + "]"
                +"lpInterval[" + lpInterval + "]");
        
        try {
        	String systemTime = DateTimeUtil.getDateString(new Date());
        	
            Meter meter = meterDao.get(meterNo);
            meter.setLpInterval(lpInterval);
            
            saveMeteringData(MeteringType.Normal, meteringTime.substring(0, 8), meteringTime.substring(8)+"00",
                    meteringValue, meter, DeviceType.MCU, dcuNo, DeviceType.Meter, meterNo, null);
            
            // LP 데이타 저장
            int[] flaglist = null;
            double[] lp = null;
            HashSet<Condition> condition = null;
            MeteringLP mlp = null;
            int hh = Integer.parseInt(meteringTime.substring(8,10));
            int mm = Integer.parseInt(meteringTime.substring(10,12));
            int maxIdx = 0;
            double baseValue = 0.0;
            String strLpValue = null;
            int startIdx = 0;
            String lpdate = meteringTime.substring(0,8);
            List _lplist = null;
            
            log.debug("LP DATE[" + lpdate + "]");
            // lpDate의 lp를 조회해서 값이 있는 것까지의 주기를 구하고 base를 위해 value를 계산한다.
            condition = new HashSet<Condition>();
            condition.add(new Condition("id.mdevType", new Object[]{DeviceType.Meter}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevId", new Object[]{meterNo}, null, Restriction.EQ));
            // condition.add(new Condition("id.dst", new Object[]{dst}, null, Restriction.EQ));
            condition.add(new Condition("yyyymmdd", new Object[]{lpdate}, null, Restriction.EQ));
            condition.add(new Condition("id.channel", new Object[]{1}, null, Restriction.EQ));
            
            switch (MeterType.valueOf(meter.getMeterType().getName())) {
            case EnergyMeter :
            	_lplist = lpEMDao.getLpEMsByListCondition(condition); 
                break;
            case WaterMeter :
            	_lplist = lpWMDao.getLpWMsByListCondition(condition);
                break;
            case GasMeter :
            	_lplist = lpGMDao.getLpGMsByListCondition(condition);
                break;
            case HeatMeter :
            	_lplist = lpHMDao.getLpHMsByListCondition(condition);
            }

            // 값이 있는 것까지의 주기를 구하고 base를 계산한다.
            if (_lplist != null && _lplist.size() != 0) {
                // 저장된 최근 시간을 가져온다.
                for (int i = 0; i < _lplist.size(); i++) {
                    mlp = (MeteringLP)_lplist.get(i);
                    if (hh < Integer.parseInt(mlp.getHour()) ){
                        hh = Integer.parseInt(mlp.getHour());
                        maxIdx = i;
                    }
                }
                log.debug("MAX IDX[" + maxIdx + "]");
                mlp = (MeteringLP)_lplist.get(maxIdx);
                baseValue = mlp.getValue();
                
                for (; mm < 60; mm+=meter.getLpInterval()) {
                    strLpValue = BeanUtils.getProperty(mlp, "value_" + (mm<10? "0":"")+mm);
                    if (strLpValue == null)
                        break;
                    else {
                        baseValue += Double.parseDouble(strLpValue);
                    }
                }
            }
            else {
                baseValue = meteringValue;
            }
            
            if (mm == 60) {
                hh++;
                mm = 0;
            }
            
            log.debug("HH[" + hh + "] MM[" + mm + "]");
            
            startIdx = (hh-Integer.parseInt(meteringTime.substring(8,10))) * (60 / meter.getLpInterval()) + 
                        (mm / meter.getLpInterval());
            
            log.debug("START IDX[" + startIdx + "]");
            
            // 하루가 지나간 것으로 처리해야 됨.
            if (hh == 24) {
                hh = 0;
                // 다음날
                lpdate = DateTimeUtil.getPreDay(lpdate, -1);
            }
            
            flaglist = new int[lplist.length-startIdx];
            lp = new double[flaglist.length];
            
            for (int flagcnt=0; flagcnt < flaglist.length; flagcnt++) {
                lp[flagcnt] = lplist[startIdx+flagcnt];
                
                if (lp[flagcnt] > 65535)
                    flaglist[flagcnt] = MeteringFlag.Fail.getFlag();
                else
                    flaglist[flagcnt] = MeteringFlag.Correct.getFlag();
            }
            
            saveLPData(MeteringType.Normal, lpdate, (hh<10?"0":"")+ hh + (mm<10?"0":"")+mm,
                    new double[][]{lp}, flaglist, baseValue, meter,
                    DeviceType.MCU, dcuNo, DeviceType.Meter, meterNo);
            
            meter.setLastReadDate(meteringTime+"00");
            meter.getModem().setLastLinkTime(meter.getLastReadDate());
            meterDao.update(meter);
            modemDao.update(meter.getModem());
            
            return "success";
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            return "fail : " + e.getMessage();
        }
    }
}
