package com.aimir.service.device.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterCodes;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.RegType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.EnergyMeterDao;
import com.aimir.dao.device.GasMeterDao;
import com.aimir.dao.device.HeatMeterDao;
import com.aimir.dao.device.InverterDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.SolarPowerMeterDao;
import com.aimir.dao.device.VolumeCorrectorDao;
import com.aimir.dao.device.WaterMeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.GasMeter;
import com.aimir.model.device.HeatMeter;
import com.aimir.model.device.Inverter;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.SolarPowerMeter;
import com.aimir.model.device.VolumeCorrector;
import com.aimir.model.device.WaterMeter;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.DeviceRegistrationManager;
import com.aimir.service.device.MeterManager;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@WebService(endpointInterface = "com.aimir.service.device.MeterManager")
@Service(value="meterManager")
@Transactional(readOnly=false)
public class MeterManagerImpl implements MeterManager {

    private static Log log = LogFactory.getLog(MeterManagerImpl.class);

    @Autowired
    MeterDao meterDao;

    @Autowired
    LocationDao locDao;

    @Autowired
    ModemDao modemDao;

    @Autowired
    EnergyMeterDao energyMeterDao;
    
    @Autowired
    InverterDao inverterDao;

    @Autowired
    WaterMeterDao waterMeterDao;

    @Autowired
    GasMeterDao gasMeterDao;

    @Autowired
    HeatMeterDao heatMeterDao;

    @Autowired
    VolumeCorrectorDao volumeCorrectorDao;

    @Autowired
    SolarPowerMeterDao solarPowerMeterDao;
  
    @Autowired
    DeviceRegistrationManager deviceRegistrationManager;
    
    @Autowired
    SupplierDao supplierDao;

    @Autowired
    public LocationDao locationDao;

    @Autowired
    CodeDao codeDao;

    @WebMethod(exclude=true)
    public Meter getMeter(Integer meterId){
        Meter rtnMeter = meterDao.get(meterId);

        if(rtnMeter != null && rtnMeter.getModem() == null)
            rtnMeter.setModem(null);

        return rtnMeter;
    }

    public Meter getMeter(String mdsId) {
        return meterDao.findByCondition("mdsId", mdsId);
    }


    public void insertMeter(Meter meter){
        meterDao.add(meter);
    }

    public void insertMeterByMap(Map<String, Object> condition){

        String meterType    = StringUtil.nullToBlank(condition.get("meterType"));
        String mdsId        = StringUtil.nullToBlank(condition.get("mdsId"));
        String modemSerial  = StringUtil.nullToBlank(condition.get("modemSerial"));

        String vendor       = StringUtil.nullToBlank(condition.get("vendor"));
        String model        = StringUtil.nullToBlank(condition.get("model"));
        String port         = StringUtil.nullToBlank(condition.get("port"));

        String loc          = StringUtil.nullToBlank(condition.get("loc"));
        //String locDetail    = StringUtil.nullToBlank(condition.get("locDetail"));

        String supplierId   = StringUtil.nullToBlank(condition.get("supplierId"));

        Meter meter               = new Meter();


        Modem modem               = new Modem();
        DeviceModel deviceModel   = new DeviceModel();
        DeviceVendor deviceVendor = new DeviceVendor();
        Supplier supplier         = new Supplier();
        Location location         = new Location();


        modem = modemDao.get(modemSerial);
        meter.setModem(modem);


        deviceVendor.setId(Integer.parseInt(vendor));
        deviceModel.setId(Integer.parseInt(model));
        deviceModel.setDeviceVendor(deviceVendor);
        supplier.setId(Integer.parseInt(supplierId));
        location.setId(Integer.parseInt(loc));



        Code meterTypeCode        = new Code();
        meterTypeCode.setId(Integer.parseInt(meterType));

        meter.setMeterType(meterTypeCode);



        meter.setMdsId(mdsId);


        meter.setModel(deviceModel);
        meter.setModemPort(Integer.parseInt(port));

        meter.setSupplier(supplier);
        
        String code = CommonConstants.getMeterStatusCode(MeterStatus.NewRegistered);
        meter.setMeterStatus(CommonConstants.getMeterStatus(code));
        meterDao.add(meter);

        EnergyMeter energyMeter = new EnergyMeter();
        energyMeter = (EnergyMeter) meter;
        energyMeterDao.add(energyMeter);


    }

    @SuppressWarnings("unused")
    public List<Object> getMiniChart(Map<String, Object> condition) {
        // 공급지역 검색 조건 시 - 현재 노드 포함 하위 노드 값 조회 및 설정
        if (!StringUtil.nullToBlank(condition.get("permitLocationId")).isEmpty()) {
            Integer locationId = Integer.valueOf((String) condition.get("permitLocationId"));

            if (locationId != null) {
                List<Integer> locationIdList = null;
                locationIdList = locationDao.getChildLocationId(locationId);
                locationIdList.add(locationId);
                condition.put("locationIdList", locationIdList);
            }
        }

        // 사용자가 선택한 1/2차 조건에 따라서 데이터 조회
        // meterType / loc / commStatus

        // ml / mc
        // lm / lc
        // cm / cl

        String meterChart = condition.get("meterChart").toString();

        List<Object> result = new ArrayList<Object>();

        String fmtmessagecommalert = StringUtil.nullToBlank(condition.get("fmtmessagecommalert"));

        String gridType = StringUtil.nullToBlank(condition.get("gridType"));

        String[] messageCommAlert = {};

        if (fmtmessagecommalert != "")
            messageCommAlert = fmtmessagecommalert.split(",");

        // meterType / commStatus
        if (meterChart.equals("mc")) {
            result = meterDao.getMiniChartMeterTypeByCommStatus(condition);
        }

        // commStatus / meterType
        if (meterChart.equals("cm")) {

            if (fmtmessagecommalert != "")
                result = meterDao.getMiniChartCommStatusByMeterType(condition, messageCommAlert);
            else
                result = meterDao.getMiniChartCommStatusByMeterType(condition);
        }

        return result;
    }

    public List<Object> getMeterSearchChart(Map<String, Object> condition) {

        // 공급지역 검색 조건 시 - 현재 노드 포함 하위 노드 값 조회 및 설정
        if (!StringUtil.nullToBlank(condition.get("sLocationId")).isEmpty()) {
            Integer sLocationId = Integer.valueOf((String) condition.get("sLocationId"));

            if (sLocationId != null) {
                List<Integer> locationIdList = null;
                locationIdList = locationDao.getChildLocationId(sLocationId);
                locationIdList.add(sLocationId);
                condition.put("locationIdList", locationIdList);
            }
        } else if (!StringUtil.nullToBlank(condition.get("sPermitLocationId")).isEmpty()) {
            Integer sLocationId = Integer.valueOf((String) condition.get("sPermitLocationId"));

            if (sLocationId != null) {
                List<Integer> locationIdList = null;
                locationIdList = locationDao.getChildLocationId(sLocationId);
                locationIdList.add(sLocationId);
                condition.put("locationIdList", locationIdList);
            }
        }

        List<Object> result = new ArrayList<Object>();
        result = meterDao.getMeterSearchChart(condition);
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Object> getMeterSearchGrid(Map<String, Object> condition){

        // 공급지역 검색 조건 시 - 현재 노드 포함 하위 노드 값 조회 및 설정
        if (!StringUtil.nullToBlank(condition.get("sLocationId")).isEmpty()) {
            Integer sLocationId = Integer.valueOf((String)condition.get("sLocationId"));

            if (sLocationId != null) {
                List<Integer> locationIdList = null;
                locationIdList = locationDao.getChildLocationId(sLocationId);
                locationIdList.add(sLocationId);
                condition.put("locationIdList", locationIdList);
            }
        } else if (!StringUtil.nullToBlank(condition.get("sPermitLocationId")).isEmpty()) {
            Integer sLocationId = Integer.valueOf((String)condition.get("sPermitLocationId"));

            if (sLocationId != null) {
                List<Integer> locationIdList = null;
                locationIdList = locationDao.getChildLocationId(sLocationId);
                locationIdList.add(sLocationId);
                condition.put("locationIdList", locationIdList);
            }
        }

        List<Object> result = new ArrayList<Object>();
        result = meterDao.getMeterSearchGrid(condition);

        List<Object> gridList = (List<Object>) result.get(1);

        String supplierId = StringUtil.nullToBlank( condition.get("supplierId"));
        if (supplierId.length() > 0) {
            Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));

            for (Object data : gridList) {
                Map<String, Object> mapData = (Map<String, Object>) data;

                mapData.put("lastCommDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mapData.get("lastCommDate")) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
                mapData.put("installDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mapData.get("installDate")) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Object> getSimpleMeterSearchGrid(Map<String, Object> condition){

        // 공급지역 검색 조건 시 - 현재 노드 포함 하위 노드 값 조회 및 설정
        if (!StringUtil.nullToBlank(condition.get("sLocationId")).isEmpty()) {
            Integer sLocationId = Integer.valueOf((String)condition.get("sLocationId"));

            if (sLocationId != null) {
                List<Integer> locationIdList = null;
                locationIdList = locationDao.getChildLocationId(sLocationId);
                locationIdList.add(sLocationId);
                condition.put("locationIdList", locationIdList);
            }
        } else if (!StringUtil.nullToBlank(condition.get("sPermitLocationId")).isEmpty()) {
            Integer sLocationId = Integer.valueOf((String)condition.get("sPermitLocationId"));

            if (sLocationId != null) {
                List<Integer> locationIdList = null;
                locationIdList = locationDao.getChildLocationId(sLocationId);
                locationIdList.add(sLocationId);
                condition.put("locationIdList", locationIdList);
            }
        }

        List<Object> result = new ArrayList<Object>();
        result = meterDao.getSimpleMeterSearchGrid(condition);

        /*List<Object> gridList = (List<Object>) result.get(1);
        String supplierId = StringUtil.nullToBlank( condition.get("supplierId"));
        if (supplierId.length() > 0) {
            Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));

            for (Object data : gridList) {
                Map<String, Object> mapData = (Map<String, Object>) data;

                mapData.put("lastCommDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mapData.get("lastCommDate")) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
                mapData.put("installDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mapData.get("installDate")) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            }
        }*/

        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Object> getMeterLogChart(Map<String, Object> condition){

        List<Object> result = new ArrayList<Object>();
        result = meterDao.getMeterLogChart(condition);

        Supplier supplier = supplierDao.get(Integer.parseInt(String.valueOf(condition.get("supplierId"))));
        List<Object> dataList = (List<Object>) result.get(0);
        for(Object obj: dataList) {
            HashMap chartDataMap = (HashMap) obj;
            String yyyyMMdd = String.valueOf(chartDataMap.get("xTag"));
            chartDataMap.put("xTag", TimeLocaleUtil.getLocaleDate(yyyyMMdd, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
        }

        return result;

    }


    public List<Object> getMeterLogGrid(Map<String, Object> condition){

        List<Object> result = new ArrayList<Object>();
        String logType          = StringUtil.nullToBlank(condition.get("logType"));

        if(logType.equals("commLog")){
            result = meterDao.getMeterCommLog(condition);
        }else if(logType.equals("updateLog")){
            // 없음
            result = meterDao.getMeterCommLog(condition);
        }else if(logType.equals("brokenLog")){
            // 없음
            result = meterDao.getMeterCommLog(condition);
        }else if(logType.equals("operationLog")){
            result = meterDao.getMeterOperationLog(condition);
        }

        return result;

    }

    // modemId를 통하여, Modem에 연결된 Meter목록을 조회
    public List<Object> getMeterListByModem(Map<String, Object> condition){
        List<Object> result = new ArrayList<Object>();
        result = meterDao.getMeterListByModem(condition);
        return result;
    }


    // Modem에 할당되지 않은 meter목록 조회
    public List<Object> getMeterListByNotModem(Map<String, Object> condition){
        List<Object> result = new ArrayList<Object>();
        result = meterDao.getMeterListByNotModem(condition);
        return result;
    }

    // Modem에 할당된 미터 삭제
    public Boolean unsetModemId(Map<String, Object> condition){

        Boolean result  = true;
        String[] mdsId  = (String[]) condition.get("mdsId");
        int mdsIdLen    = 0;

        if(mdsId != null)
            mdsIdLen = mdsId.length;

        for(int i=0 ; i < mdsIdLen ; i++){
            Meter oriMeter = meterDao.get(mdsId[i]);
            oriMeter.setModem(null);
            meterDao.flushAndClear();
        }

        return result;
    }

    // Modem에 미터 할당
    public Boolean setModemId(Map<String, Object> condition){

        Boolean result  = true;
        String[] mdsId  = (String[]) condition.get("mdsId");
        Integer modemId = (Integer)  condition.get("modemId");
        int mdsIdLen    = 0;

        if(modemId==null)
            return false;

        if(mdsId != null)
            mdsIdLen = mdsId.length;

        for(int i=0 ; i < mdsIdLen ; i++){
            Meter oriMeter = meterDao.get(mdsId[i]);
            Modem modem = modemDao.get(modemId);

            oriMeter.setModem(modem);

            meterDao.flushAndClear();
        }

        return result;
    }



    public Map<String, Object> getMeterSearchCondition(){
        Map<String, Object> result = new HashMap<String, Object>();
        result = meterDao.getMeterSearchCondition();
        return result;
    }

    // 미터 등록 -------------------------------------
    public Map<String, Object> insertEnergyMeter(EnergyMeter energyMeter) {

        Map<String, Object> result = new HashMap<String, Object>();
        ResultStatus insertResult = ResultStatus.SUCCESS;

        try {
            // Modem 객체 설정
            if (energyMeter.getModem().getDeviceSerial() == null)
                energyMeter.setModem(null);
            else
                energyMeter.setModem(modemDao.get(energyMeter.getModem().getDeviceSerial()));

            energyMeter.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
//            String code = CommonConstants.getMeterStatusCode(MeterStatus.NewRegistered);
//            energyMeter.setMeterStatus(CommonConstants.getMeterStatus(code));
            energyMeter.setMeterStatus(codeDao.getMeterStatusCodeByName(MeterStatus.NewRegistered.name()));
            energyMeterDao.add(energyMeter);
//            energyMeterDao.flush();
            result.put("id", energyMeter.getId());

        } catch(Exception e) {
            log.error(e, e);
            insertResult = ResultStatus.FAIL;
            result = null;
        } finally {
            Map<String, Object> logData = new HashMap<String, Object>();
            logData.put("deviceType",   TargetClass.EnergyMeter);
            logData.put("deviceName",   energyMeter.getMdsId());
            logData.put("deviceModel",  energyMeter.getModel());
            logData.put("resultStatus", insertResult);
            logData.put("regType",      RegType.Manual);
            logData.put("supplier",     energyMeter.getSupplier());
            deviceRegistrationManager.insertDeviceRegLog(logData);
        }

        return result;
    }


    public Map<String, Object> insertWaterMeter(WaterMeter waterMeter) {
        Map<String, Object> result = new HashMap<String, Object>();
        ResultStatus insertResult = ResultStatus.SUCCESS;

        try {
            if (waterMeter.getModem().getDeviceSerial() == null)
                waterMeter.setModem(null);
            else
                waterMeter.setModem(modemDao.get(waterMeter.getModem().getDeviceSerial()));

            waterMeter.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
//            String code = CommonConstants.getMeterStatusCode(MeterStatus.NewRegistered);
//            waterMeter.setMeterStatus(CommonConstants.getMeterStatus(code));
            waterMeter.setMeterStatus(codeDao.getMeterStatusCodeByName(MeterStatus.NewRegistered.name()));
            waterMeterDao.add(waterMeter);
//            waterMeterDao.flush();

            result.put("id", waterMeter.getId());
        } catch(Exception e) {
            log.error(e, e);
            insertResult = ResultStatus.FAIL;
        } finally {
            Map<String, Object> logData = new HashMap<String, Object>();
            logData.put("deviceType",   TargetClass.WaterMeter);
            logData.put("deviceName",   waterMeter.getMdsId());
            logData.put("deviceModel",  waterMeter.getModel());
            logData.put("resultStatus", insertResult);
            logData.put("regType",      RegType.Manual);
            logData.put("supplier",     waterMeter.getSupplier());
            deviceRegistrationManager.insertDeviceRegLog(logData);
        }

        return result;
    }

    public Map<String, Object> insertGasMeter(GasMeter gasMeter) {
        Map<String, Object> result = new HashMap<String, Object>();
        ResultStatus insertResult = ResultStatus.SUCCESS;

        try {
            if (gasMeter.getModem().getDeviceSerial() == null)
                gasMeter.setModem(null);
            else
                gasMeter.setModem(modemDao.get(gasMeter.getModem().getDeviceSerial()));

            gasMeter.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
//            String code = CommonConstants.getMeterStatusCode(MeterStatus.NewRegistered);
//            gasMeter.setMeterStatus(CommonConstants.getMeterStatus(code));
            gasMeter.setMeterStatus(codeDao.getMeterStatusCodeByName(MeterStatus.NewRegistered.name()));
            gasMeterDao.add(gasMeter);
//            gasMeterDao.flush();

            result.put("id", gasMeter.getId());
        } catch(Exception e) {
            log.error(e, e);
            insertResult = ResultStatus.FAIL;
        } finally {
            Map<String, Object> logData = new HashMap<String, Object>();
            logData.put("deviceType",   TargetClass.GasMeter);
            logData.put("deviceName",   gasMeter.getMdsId());
            logData.put("deviceModel",  gasMeter.getModel());
            logData.put("resultStatus", insertResult);
            logData.put("regType",      RegType.Manual);
            logData.put("supplier",     gasMeter.getSupplier());
            deviceRegistrationManager.insertDeviceRegLog(logData);
        }
        return result;
    }

    public Map<String, Object> insertHeatMeter(HeatMeter heatMeter) {
        Map<String, Object> result = new HashMap<String, Object>();
        ResultStatus insertResult = ResultStatus.SUCCESS;

        try {
            if (heatMeter.getModem().getDeviceSerial() == null)
                heatMeter.setModem(null);
            else
                heatMeter.setModem(modemDao.get(heatMeter.getModem().getDeviceSerial()));

            heatMeter.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
//            String code = CommonConstants.getMeterStatusCode(MeterStatus.NewRegistered);
//            heatMeter.setMeterStatus(CommonConstants.getMeterStatus(code));
            heatMeter.setMeterStatus(codeDao.getMeterStatusCodeByName(MeterStatus.NewRegistered.name()));
            heatMeterDao.add(heatMeter);
//            heatMeterDao.flush();

            result.put("id", heatMeter.getId());
        } catch(Exception e) {
            log.error(e, e);
            insertResult = ResultStatus.FAIL;
        } finally {
            Map<String, Object> logData = new HashMap<String, Object>();
            logData.put("deviceType",   TargetClass.HeatMeter);
            logData.put("deviceName",   heatMeter.getMdsId());
            logData.put("deviceModel",  heatMeter.getModel());
            logData.put("resultStatus", insertResult);
            logData.put("regType",      RegType.Manual);
            logData.put("supplier",     heatMeter.getSupplier());
            deviceRegistrationManager.insertDeviceRegLog(logData);
        }

        return result;
    }

    public Map<String, Object> insertVolumeCorrector(VolumeCorrector volumeCorrector) {
        Map<String, Object> result = new HashMap<String, Object>();
        ResultStatus insertResult = ResultStatus.SUCCESS;

        try {
            if (volumeCorrector.getModem().getDeviceSerial() == null)
                volumeCorrector.setModem(null);
            else
                volumeCorrector.setModem(modemDao.get(volumeCorrector.getModem().getDeviceSerial()));

            volumeCorrector.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
//            String code = CommonConstants.getMeterStatusCode(MeterStatus.NewRegistered);
//            volumeCorrector.setMeterStatus(CommonConstants.getMeterStatus(code));
            volumeCorrector.setMeterStatus(codeDao.getMeterStatusCodeByName(MeterStatus.NewRegistered.name()));
            volumeCorrectorDao.add(volumeCorrector);
//            volumeCorrectorDao.flush();

            result.put("id", volumeCorrector.getId());
        } catch(Exception e) {
            log.error(e, e);
            insertResult = ResultStatus.FAIL;
        } finally {
            Map<String, Object> logData = new HashMap<String, Object>();
            logData.put("deviceType",   TargetClass.VolumeCorrector);
            logData.put("deviceName",   volumeCorrector.getMdsId());
            logData.put("deviceModel",  volumeCorrector.getModel());
            logData.put("resultStatus", insertResult);
            logData.put("regType",      RegType.Manual);
            logData.put("supplier",     volumeCorrector.getSupplier());
            deviceRegistrationManager.insertDeviceRegLog(logData);
        }

        return result;
    }

    //태양광 발전 관련 미터(인버터)등록 시 사용.
    public Map<String, Object> insertSolarPowerMeter(SolarPowerMeter solarPowerMeter) {

        Map<String, Object> result = new HashMap<String, Object>();
        ResultStatus insertResult = ResultStatus.SUCCESS;

        try {
            if (solarPowerMeter.getModem().getDeviceSerial() == null)
                solarPowerMeter.setModem(null);
            else
                solarPowerMeter.setModem(modemDao.get(solarPowerMeter.getModem().getDeviceSerial()));

            solarPowerMeter.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
//            String code = CommonConstants.getMeterStatusCode(MeterStatus.NewRegistered);
//            solarPowerMeter.setMeterStatus(CommonConstants.getMeterStatus(code));
            solarPowerMeter.setMeterStatus(codeDao.getMeterStatusCodeByName(MeterStatus.NewRegistered.name()));
            solarPowerMeterDao.add(solarPowerMeter);
//            solarPowerMeterDao.flush();

            result.put("id", solarPowerMeter.getId());
        } catch(Exception e) {
            log.error(e, e);
            insertResult = ResultStatus.FAIL;
        } finally {
            Map<String, Object> logData = new HashMap<String, Object>();
            logData.put("deviceType",   TargetClass.SolarPowerMeter);
            logData.put("deviceName",   solarPowerMeter.getMdsId());
            logData.put("deviceModel",  solarPowerMeter.getModel());
            logData.put("resultStatus", insertResult);
            logData.put("regType",      RegType.Manual);
            logData.put("supplier",     solarPowerMeter.getSupplier());
            deviceRegistrationManager.insertDeviceRegLog(logData);
        }

        return result;
    }
    
    // 인버터 등록 -------------------------------------
    public Map<String, Object> insertInverter(Inverter inverter) {

        Map<String, Object> result = new HashMap<String, Object>();
        ResultStatus insertResult = ResultStatus.SUCCESS;

        try {
            // Modem 객체 설정
            if (inverter.getModem().getDeviceSerial() == null)
            	inverter.setModem(null);
            else
            	inverter.setModem(modemDao.get(inverter.getModem().getDeviceSerial()));

            inverter.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
//            String code = CommonConstants.getMeterStatusCode(MeterStatus.NewRegistered);
//            energyMeter.setMeterStatus(CommonConstants.getMeterStatus(code));
            inverter.setMeterStatus(codeDao.getMeterStatusCodeByName(MeterStatus.NewRegistered.name()));
            inverterDao.add(inverter);
//            energyMeterDao.flush();
            result.put("id", inverter.getId());

        } catch(Exception e) {
            log.error(e, e);
            insertResult = ResultStatus.FAIL;
            result = null;
        } finally {
            Map<String, Object> logData = new HashMap<String, Object>();
            logData.put("deviceType",   TargetClass.EnergyMeter);
            logData.put("deviceName",   inverter.getMdsId());
            logData.put("deviceModel",  inverter.getModel());
            logData.put("resultStatus", insertResult);
            logData.put("regType",      RegType.Manual);
            logData.put("supplier",     inverter.getSupplier());
            deviceRegistrationManager.insertDeviceRegLog(logData);
        }

        return result;
    }


    // 미터 변경 -------------------------------------
    public Map<String, Object> updateMeter(Meter meter){
        Meter oriMeter = meterDao.get(meter.getMdsId());
        Map<String, Object> result = new HashMap<String, Object>();
        
        // RF 단말과 좌표 연동하기 위해 추가됨. 2016.05.18
        if (oriMeter != null && meter.getId() == null) {
            log.info("ID[" + meter.getMdsId() + 
                    "] X[" + meter.getGpioX() + 
                    "] Y[" + meter.getGpioY() + 
                    "] Z[" + meter.getGpioZ() + "]");
            
            if (meter.getGpioX() != null) oriMeter.setGpioX(meter.getGpioX());
            if (meter.getGpioY() != null) oriMeter.setGpioY(meter.getGpioY());
            if (meter.getGpioZ() != null) oriMeter.setGpioZ(meter.getGpioZ());
            
            meterDao.update(oriMeter);
            
            result.put("id", oriMeter.getId());

            return result;
        }
        // Meter oriMeter = meterDao.get(meter.getId());

        oriMeter.setMdsId(meter.getMdsId());
        oriMeter.setModel(meter.getModel());
        oriMeter.setSwVersion(meter.getSwVersion());
        oriMeter.setHwVersion(meter.getHwVersion());
        oriMeter.setInstallDate(meter.getInstallDate());
        if(meter.getUsageThreshold() != null)
            oriMeter.setUsageThreshold(meter.getUsageThreshold());
        if(meter.getModemPort() != null)
        	oriMeter.setModemPort(meter.getModemPort());
        try{
            if(meter.getLocation() != null && meter.getLocation().getId() != null)
                oriMeter.setLocation(locDao.get(meter.getLocation().getId()));
        }catch(Exception e){
            e.printStackTrace();
        }

        // Relay Control 미터 상태를 갱신하기 위해서 추가 함 - by eunmiae
        if(meter.getMeterStatus() != null) {
            oriMeter.setMeterStatus(meter.getMeterStatus());
        }

        meterDao.update(oriMeter);
//        meterDao.flushAndClear();

        result.put("id", meter.getId());

        return result;
    }


    // 미터 변경 -------------------------------------
    public Map<String, Object> updateWaterMeterInfo(WaterMeter watermeter){
        Map<String, Object> result = new HashMap<String, Object>();

        WaterMeter oriMeter = waterMeterDao.get(watermeter.getId());

        oriMeter.setMdsId(watermeter.getMdsId());
        oriMeter.setModel(watermeter.getModel());
        oriMeter.setSwVersion(watermeter.getSwVersion());
        oriMeter.setHwVersion(watermeter.getHwVersion());
        oriMeter.setInstallDate(watermeter.getInstallDate());
        if(watermeter.getUsageThreshold() != null)
            oriMeter.setUsageThreshold(watermeter.getUsageThreshold());
        if(watermeter.getModemPort() != null)
        	oriMeter.setModemPort(watermeter.getModemPort());
        
        try{
            if(watermeter.getLocation() != null && watermeter.getLocation().getId() != null)
                oriMeter.setLocation(locDao.get(watermeter.getLocation().getId()));
        }catch(Exception e){
            e.printStackTrace();
        }

        // Relay Control 미터 상태를 갱신하기 위해서 추가 함 - by eunmiae
        if(watermeter.getValveSerial() != null) {
            oriMeter.setValveSerial(watermeter.getValveSerial());
        }

        waterMeterDao.update(oriMeter);
//        waterMeterDao.flushAndClear();

        result.put("id", watermeter.getId());

        return result;
    }


    // EnergyMeter 변경
    public Map<String, Object> updateEnergyMeter(EnergyMeter energyMeter) {
        Map<String, Object> result = new HashMap<String, Object>();

        EnergyMeter oriMeter = energyMeterDao.get(energyMeter.getId());

        // RF 단말과 좌표 연동하기 위해 추가됨. 2016.05.18
        if (oriMeter != null && energyMeter.getId() == null) {
            log.info("ID[" + energyMeter.getMdsId() + 
                    "] X[" + energyMeter.getGpioX() + 
                    "] Y[" + energyMeter.getGpioY() + 
                    "] Z[" + energyMeter.getGpioZ() + "]");
            
            if (energyMeter.getGpioX() != null) oriMeter.setGpioX(energyMeter.getGpioX());
            if (energyMeter.getGpioY() != null) oriMeter.setGpioY(energyMeter.getGpioY());
            if (energyMeter.getGpioZ() != null) oriMeter.setGpioZ(energyMeter.getGpioZ());
            
            meterDao.update(oriMeter);
            
            result.put("id", energyMeter.getId());

            return result;
        }
        
        oriMeter.setMeterError(energyMeter.getMeterError());
        oriMeter.setMeterCaution(energyMeter.getMeterCaution());
        
        oriMeter.setInstallProperty(energyMeter.getInstallProperty());
        oriMeter.setLpInterval(energyMeter.getLpInterval());
        oriMeter.setPulseConstant(energyMeter.getPulseConstant());
        oriMeter.setTransformerRatio(energyMeter.getTransformerRatio());
        oriMeter.setCt(energyMeter.getCt());     	


        if (energyMeter.getCustomer() != null && energyMeter.getCustomer().getCustomerNo() != null && !energyMeter.getCustomer().getCustomerNo().equals(""))
            oriMeter.getContract().getCustomer().setCustomerNo(energyMeter.getCustomer().getCustomerNo());  //고객번호
        if (energyMeter.getCustomer() != null && energyMeter.getCustomer().getName() != null && !energyMeter.getCustomer().getName().equals(""))
            oriMeter.getContract().getCustomer().setName(energyMeter.getCustomer().getName());              //고객명

        Code code = null;
        Code updateMeterStatus = energyMeter.getMeterStatus();
        Code oriMeterStatus = oriMeter.getMeterStatus();

        if (updateMeterStatus != null && updateMeterStatus.getId() != null) {
            code = codeDao.get(updateMeterStatus.getId());
        }
        
        if (oriMeterStatus != null) {
            if (code == null || !oriMeterStatus.getId().equals(code.getId())) {
                oriMeter.setMeterStatus(code);
            }
        } else if (code != null) {
            oriMeter.setMeterStatus(code);
        }

        if (code != null && code.getCode().equals(MeterCodes.DELETE_STATUS.getCode())) {
            if (oriMeterStatus == null || (oriMeterStatus != null && !oriMeterStatus.getId().equals(code.getId()))) {
                try {
                    // Meter 상태를 Delete 로 변경할때 Delete Date 를 입력한다.
                    oriMeter.setDeleteDate(TimeUtil.getCurrentTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        energyMeterDao.update(oriMeter);

        result.put("id", energyMeter.getId());

        return result;
    }

    // WaterMeter 변경
    public Map<String, Object> updateWaterMeter(WaterMeter waterMeter){
        Map<String, Object> result = new HashMap<String, Object>();

        WaterMeter oriWaterMeter = waterMeterDao.get(waterMeter.getId());

        // RF 단말과 좌표 연동하기 위해 추가됨. 2016.05.18
        if (oriWaterMeter != null && waterMeter.getId() == null) {
            log.info("ID[" + waterMeter.getMdsId() + 
                    "] X[" + waterMeter.getGpioX() + 
                    "] Y[" + waterMeter.getGpioY() + 
                    "] Z[" + waterMeter.getGpioZ() + "]");
            
            if (waterMeter.getGpioX() != null) oriWaterMeter.setGpioX(waterMeter.getGpioX());
            if (waterMeter.getGpioY() != null) oriWaterMeter.setGpioY(waterMeter.getGpioY());
            if (waterMeter.getGpioZ() != null) oriWaterMeter.setGpioZ(waterMeter.getGpioZ());
            
            meterDao.update(oriWaterMeter);
            
            result.put("id", oriWaterMeter.getId());

            return result;
        }
        
        if(waterMeter.getCustomer() != null){
        if(waterMeter.getCustomer().getCustomerNo() != null && !waterMeter.getCustomer().getCustomerNo().equals(""))
            oriWaterMeter.getContract().getCustomer().setCustomerNo(waterMeter.getCustomer().getCustomerNo());  //고객번호
        if(waterMeter.getCustomer().getName() != null && !waterMeter.getCustomer().getName().equals(""))
            oriWaterMeter.getContract().getCustomer().setName(waterMeter.getCustomer().getName());              //고객명
        }

        // Relay Control 미터 상태를 갱신하기 위해서 추가 함 - by eunmiae
//        if(waterMeter.getMeterStatus() != null) {
//            oriWaterMeter.setMeterStatus(waterMeter.getMeterStatus());
//        }

        Code code = null;
        Code updateMeterStatus = waterMeter.getMeterStatus();
        Code oriMeterStatus = oriWaterMeter.getMeterStatus();

        if (updateMeterStatus != null && updateMeterStatus.getId() != null) {
            code = codeDao.get(updateMeterStatus.getId());
        }

        if (oriMeterStatus != null) {
            if (code == null || !oriMeterStatus.getId().equals(code.getId())) {
                oriWaterMeter.setMeterStatus(code);
            }
        } else if (code != null) {
            oriWaterMeter.setMeterStatus(code);
        }

        if (code != null && code.getCode().equals(MeterCodes.DELETE_STATUS.getCode())) {
            if (oriMeterStatus == null || (oriMeterStatus != null && !oriMeterStatus.getId().equals(code.getId()))) {
                try {
                    // Meter 상태를 Delete 로 변경할때 Delete Date 를 입력한다.
                    oriWaterMeter.setDeleteDate(TimeUtil.getCurrentTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if(waterMeter.getAlarmStatus() != null) {
            oriWaterMeter.setAlarmStatus(waterMeter.getAlarmStatus());
        }
        
        oriWaterMeter.setMeterError(waterMeter.getMeterError());
        oriWaterMeter.setMeterCaution(waterMeter.getMeterCaution());
        
        oriWaterMeter.setInstallProperty(waterMeter.getInstallProperty());
        // 펄스상수
        oriWaterMeter.setPulseConstant(waterMeter.getPulseConstant());
        // 최대유속
        oriWaterMeter.setQMax(waterMeter.getQMax());
        //지상/지하
        oriWaterMeter.setUnderGround(waterMeter.getUnderGround());
        //밸브 번호
        oriWaterMeter.setValveSerial(waterMeter.getValveSerial());


        waterMeterDao.update(oriWaterMeter);
//        waterMeterDao.flush();
//        waterMeterDao.flushAndClear();

        result.put("id", waterMeter.getId());

        return result;
    }

    // GasMeter 변경
    public Map<String, Object> updateGasMeter(GasMeter gasMeter){
        Map<String, Object> result = new HashMap<String, Object>();

        GasMeter oriGasMeter = gasMeterDao.get(gasMeter.getId());

        // RF 단말과 좌표 연동하기 위해 추가됨. 2016.05.18
        if (oriGasMeter != null && gasMeter.getId() == null) {
            log.info("ID[" + gasMeter.getMdsId() + 
                    "] X[" + gasMeter.getGpioX() + 
                    "] Y[" + gasMeter.getGpioY() + 
                    "] Z[" + gasMeter.getGpioZ() + "]");
            
            if (gasMeter.getGpioX() != null) oriGasMeter.setGpioX(gasMeter.getGpioX());
            if (gasMeter.getGpioY() != null) oriGasMeter.setGpioY(gasMeter.getGpioY());
            if (gasMeter.getGpioZ() != null) oriGasMeter.setGpioZ(gasMeter.getGpioZ());
            
            meterDao.update(oriGasMeter);
            
            result.put("id", gasMeter.getId());

            return result;
        }
        
        if(gasMeter.getCustomer().getCustomerNo() != null && !gasMeter.getCustomer().getCustomerNo().equals(""))
            oriGasMeter.getContract().getCustomer().setCustomerNo(gasMeter.getCustomer().getCustomerNo());  //고객번호
        if(gasMeter.getCustomer().getName() != null && !gasMeter.getCustomer().getName().equals(""))
            oriGasMeter.getContract().getCustomer().setName(gasMeter.getCustomer().getName());              //고객명

        oriGasMeter.setMeterError(gasMeter.getMeterError());
        oriGasMeter.setMeterCaution(gasMeter.getMeterCaution());
        oriGasMeter.setInstallProperty(gasMeter.getInstallProperty());
        // 펄스상수
        oriGasMeter.setPulseConstant(gasMeter.getPulseConstant());
        // 소비지역 - 모름

//        if(gasMeter.getMeterStatus() != null) {
//            oriGasMeter.setMeterStatus(gasMeter.getMeterStatus());
//        }
        Code code = null;
        Code updateMeterStatus = gasMeter.getMeterStatus();
        Code oriMeterStatus = oriGasMeter.getMeterStatus();

        if(updateMeterStatus != null && updateMeterStatus.getId() != null) {
            code = codeDao.get(updateMeterStatus.getId());
        }

        if (oriMeterStatus != null) {
            if (code == null || !oriMeterStatus.getId().equals(code.getId())) {
                oriGasMeter.setMeterStatus(code);
            }
        } else if (code != null) {
            oriGasMeter.setMeterStatus(code);
        }

        if (code != null && code.getCode().equals(MeterCodes.DELETE_STATUS.getCode())) {
            if (oriMeterStatus == null || (oriMeterStatus != null && !oriMeterStatus.getId().equals(code.getId()))) {
                try {
                    // Meter 상태를 Delete 로 변경할때 Delete Date 를 입력한다.
                    oriGasMeter.setDeleteDate(TimeUtil.getCurrentTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if(gasMeter.getAlarmStatus() != null) {
            oriGasMeter.setAlarmStatus(gasMeter.getAlarmStatus());
        }

        gasMeterDao.update(oriGasMeter);
//        gasMeterDao.flushAndClear();

        result.put("id", gasMeter.getId());

        return result;
    }

    // HeatMeter 변경
    public Map<String, Object> updateHeatMeter(HeatMeter heatMeter){
        Map<String, Object> result = new HashMap<String, Object>();

        HeatMeter oriHeatMeter = heatMeterDao.get(heatMeter.getId());

        // RF 단말과 좌표 연동하기 위해 추가됨. 2016.05.18
        if (oriHeatMeter != null && heatMeter.getId() == null) {
            log.info("ID[" + heatMeter.getMdsId() + 
                    "] X[" + heatMeter.getGpioX() + 
                    "] Y[" + heatMeter.getGpioY() + 
                    "] Z[" + heatMeter.getGpioZ() + "]");
            
            if (heatMeter.getGpioX() != null) oriHeatMeter.setGpioX(heatMeter.getGpioX());
            if (heatMeter.getGpioY() != null) oriHeatMeter.setGpioY(heatMeter.getGpioY());
            if (heatMeter.getGpioZ() != null) oriHeatMeter.setGpioZ(heatMeter.getGpioZ());
            
            meterDao.update(oriHeatMeter);
            
            result.put("id", oriHeatMeter.getId());

            return result;
        }
            
        if(heatMeter.getCustomer().getCustomerNo() != null && !heatMeter.getCustomer().getCustomerNo().equals(""))
            oriHeatMeter.getContract().getCustomer().setCustomerNo(heatMeter.getCustomer().getCustomerNo());    //고객번호
        if(heatMeter.getCustomer().getName() != null && !heatMeter.getCustomer().getName().equals(""))
            oriHeatMeter.getContract().getCustomer().setName(heatMeter.getCustomer().getName());                //고객명
        
        oriHeatMeter.setMeterError(heatMeter.getMeterError());
        oriHeatMeter.setMeterCaution(heatMeter.getMeterCaution());
        
        oriHeatMeter.setInstallProperty(heatMeter.getInstallProperty());
        // 펄스상수
        oriHeatMeter.setPulseConstant(heatMeter.getPulseConstant());
        // 소비지역 - 모름
        
//        if(heatMeter.getMeterStatus() != null) {
//            oriHeatMeter.setMeterStatus(heatMeter.getMeterStatus());
//        }
        Code code = null;
        Code updateMeterStatus = heatMeter.getMeterStatus();
        Code oriMeterStatus = oriHeatMeter.getMeterStatus();

        if (updateMeterStatus != null && updateMeterStatus.getId() != null) {
            code = codeDao.get(updateMeterStatus.getId());
        }

        if (oriMeterStatus != null) {
            if (code == null || !oriMeterStatus.getId().equals(code.getId())) {
                oriHeatMeter.setMeterStatus(code);
            }
        } else if (code != null) {
            oriHeatMeter.setMeterStatus(code);
        }

        if (code != null && code.getCode().equals(MeterCodes.DELETE_STATUS.getCode())) {
            if (oriMeterStatus == null || (oriMeterStatus != null && !oriMeterStatus.getId().equals(code.getId()))) {
                try {
                    // Meter 상태를 Delete 로 변경할때 Delete Date 를 입력한다.
                    oriHeatMeter.setDeleteDate(TimeUtil.getCurrentTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        // 열량계타입
        oriHeatMeter.setHeatType(heatMeter.getHeatType());
        // 검침단위
        oriHeatMeter.setMeteringUnit(heatMeter.getMeteringUnit());
        // 단위 펄스당 유량
        oriHeatMeter.setFlowPerUnitPulse(heatMeter.getFlowPerUnitPulse());
        // 기계실 번호
        oriHeatMeter.setApparatusRoomNumber(heatMeter.getApparatusRoomNumber());

        heatMeterDao.update(oriHeatMeter);
//        heatMeterDao.flushAndClear();

        result.put("id", heatMeter.getId());

        return result;
    }

    // VolumeCorrector 변경
    public Map<String, Object> updateVolumeCorrector(VolumeCorrector volumeCorrector){
        Map<String, Object> result = new HashMap<String, Object>();

        VolumeCorrector oriVolume = volumeCorrectorDao.get(volumeCorrector.getId());
        
        // RF 단말과 좌표 연동하기 위해 추가됨. 2016.05.18
        if (oriVolume != null && volumeCorrector.getId() == null) {
            log.info("ID[" + volumeCorrector.getMdsId() + 
                    "] X[" + volumeCorrector.getGpioX() + 
                    "] Y[" + volumeCorrector.getGpioY() + 
                    "] Z[" + volumeCorrector.getGpioZ() + "]");
            
            if (volumeCorrector.getGpioX() != null) oriVolume.setGpioX(volumeCorrector.getGpioX());
            if (volumeCorrector.getGpioY() != null) oriVolume.setGpioY(volumeCorrector.getGpioY());
            if (volumeCorrector.getGpioZ() != null) oriVolume.setGpioZ(volumeCorrector.getGpioZ());
            
            meterDao.update(oriVolume);
            
            result.put("id", oriVolume.getId());

            return result;
        }
        
        oriVolume.setMeterError(volumeCorrector.getMeterError());
        oriVolume.setMeterCaution(volumeCorrector.getMeterCaution());
        oriVolume.setInstallProperty(volumeCorrector.getInstallProperty());
        // 모름

//        if(volumeCorrector.getMeterStatus() != null) {
//            oriHeat.setMeterStatus(volumeCorrector.getMeterStatus());
//        }
        Code code = null;
        Code updateMeterStatus = volumeCorrector.getMeterStatus();
        Code oriMeterStatus = oriVolume.getMeterStatus();

        if(updateMeterStatus != null && updateMeterStatus.getId() != null) {
            code = codeDao.get(updateMeterStatus.getId());
        }

        if (oriMeterStatus != null) {
            if (code == null || !oriMeterStatus.getId().equals(code.getId())) {
                oriVolume.setMeterStatus(code);
            }
        } else if (code != null) {
            oriVolume.setMeterStatus(code);
        }

        if (code != null && code.getCode().equals(MeterCodes.DELETE_STATUS.getCode())) {
            if (oriMeterStatus == null || (oriMeterStatus != null && !oriMeterStatus.getId().equals(code.getId()))) {
                try {
                    // Meter 상태를 Delete 로 변경할때 Delete Date 를 입력한다.
                    oriVolume.setDeleteDate(TimeUtil.getCurrentTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        volumeCorrectorDao.update(oriVolume);
//        volumeCorrectorDao.flushAndClear();

        result.put("id", volumeCorrector.getId());

        return result;
    }

    // SolarPowerMeter 변경
    public Map<String, Object> updateSolarPowerMeter(SolarPowerMeter solarPowerMeter){
        Map<String, Object> result = new HashMap<String, Object>();

        SolarPowerMeter oriMeter = solarPowerMeterDao.get(solarPowerMeter.getId());
        
        // RF 단말과 좌표 연동하기 위해 추가됨. 2016.05.18
        if (oriMeter != null && solarPowerMeter.getId() == null) {
            log.info("ID[" + solarPowerMeter.getMdsId() + 
                    "] X[" + solarPowerMeter.getGpioX() + 
                    "] Y[" + solarPowerMeter.getGpioY() + 
                    "] Z[" + solarPowerMeter.getGpioZ() + "]");
            
            if (solarPowerMeter.getGpioX() != null) oriMeter.setGpioX(solarPowerMeter.getGpioX());
            if (solarPowerMeter.getGpioY() != null) oriMeter.setGpioY(solarPowerMeter.getGpioY());
            if (solarPowerMeter.getGpioZ() != null) oriMeter.setGpioZ(solarPowerMeter.getGpioZ());
            
            meterDao.update(oriMeter);
            
            result.put("id", oriMeter.getId());

            return result;
        }
        
        oriMeter.setMeterError(solarPowerMeter.getMeterError());
        oriMeter.setMeterCaution(solarPowerMeter.getMeterCaution());
        
        oriMeter.setInstallProperty(solarPowerMeter.getInstallProperty());
        oriMeter.setLpInterval(solarPowerMeter.getLpInterval());
        oriMeter.setPulseConstant(solarPowerMeter.getPulseConstant());

        if(solarPowerMeter.getCustomer().getCustomerNo() != null && !solarPowerMeter.getCustomer().getCustomerNo().equals(""))
            oriMeter.getContract().getCustomer().setCustomerNo(solarPowerMeter.getCustomer().getCustomerNo());  //고객번호
        if(solarPowerMeter.getCustomer().getName() != null && !solarPowerMeter.getCustomer().getName().equals(""))
            oriMeter.getContract().getCustomer().setName(solarPowerMeter.getCustomer().getName());              //고객명

        // Relay Control 미터 상태를 갱신하기 위해서 추가 함 - by eunmiae
//        if(solarPowerMeter.getMeterStatus() != null) {
//             oriMeter.setMeterStatus(solarPowerMeter.getMeterStatus());
//        }
        Code code = null;
        Code updateMeterStatus = solarPowerMeter.getMeterStatus();
        Code oriMeterStatus = oriMeter.getMeterStatus();

        if(updateMeterStatus != null && updateMeterStatus.getId() != null) {
            code = codeDao.get(updateMeterStatus.getId());
        }

        if (oriMeterStatus != null) {
            if (code == null || !oriMeterStatus.getId().equals(code.getId())) {
                oriMeter.setMeterStatus(code);
            }
        } else if (code != null) {
            oriMeter.setMeterStatus(code);
        }

        if (code != null && code.getCode().equals(MeterCodes.DELETE_STATUS.getCode())) {
            if (oriMeterStatus == null || (oriMeterStatus != null && !oriMeterStatus.getId().equals(code.getId()))) {
                try {
                    // Meter 상태를 Delete 로 변경할때 Delete Date 를 입력한다.
                    oriMeter.setDeleteDate(TimeUtil.getCurrentTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        solarPowerMeterDao.update(oriMeter);
//        solarPowerMeterDao.flushAndClear();

        result.put("id", solarPowerMeter.getId());

        return result;
    }
    
    // Inverter 변경
    public Map<String, Object> updateInverter(Inverter inverter) {
        Map<String, Object> result = new HashMap<String, Object>();

        Inverter oriInverter = inverterDao.get(inverter.getId());

//        oriMeter.setInstallProperty(inverter.getInstallProperty());
//        oriMeter.setLpInterval(inverter.getLpInterval());
//        oriMeter.setPulseConstant(inverter.getPulseConstant());
//        oriMeter.setTransformerRatio(inverter.getTransformerRatio());
//        oriMeter.setCt(inverter.getCt());

        if (inverter.getCustomer() != null && inverter.getCustomer().getCustomerNo() != null && !inverter.getCustomer().getCustomerNo().equals(""))
            oriInverter.getContract().getCustomer().setCustomerNo(inverter.getCustomer().getCustomerNo());  //고객번호
        if (inverter.getCustomer() != null && inverter.getCustomer().getName() != null && !inverter.getCustomer().getName().equals(""))
            oriInverter.getContract().getCustomer().setName(inverter.getCustomer().getName());              //고객명

        Code code = null;
        Code updateMeterStatus = inverter.getMeterStatus();
        Code oriMeterStatus = oriInverter.getMeterStatus();

        if (updateMeterStatus != null && updateMeterStatus.getId() != null) {
            code = codeDao.get(updateMeterStatus.getId());
        }

        if (oriMeterStatus != null) {
            if (code == null || !oriMeterStatus.getId().equals(code.getId())) {
                oriInverter.setMeterStatus(code);
            }
        } else if (code != null) {
            oriInverter.setMeterStatus(code);
        }

        if (code != null && code.getCode().equals(MeterCodes.DELETE_STATUS.getCode())) {
            if (oriMeterStatus == null || (oriMeterStatus != null && !oriMeterStatus.getId().equals(code.getId()))) {
                try {
                    // Meter 상태를 Delete 로 변경할때 Delete Date 를 입력한다.
                    oriInverter.setDeleteDate(TimeUtil.getCurrentTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        inverterDao.update(oriInverter);

        result.put("id", inverter.getId());

        return result;
    }    

    /* (non-Javadoc)
     * @see com.aimir.service.device.MeterManager#deleteMeter(com.aimir.model.device.Meter)
     */
    public int deleteMeter(Meter meter){
        int resultId = meterDao.deleteById(meter.getId());
        meterDao.flushAndClear();

        return resultId;
    }


    public int deleteMeterStatus(Meter meter){
        
        Meter oriMeter = meterDao.get(meter.getId());
         
        int resultId = oriMeter.getId();
        String code = CommonConstants.getMeterStatusCode(MeterStatus.Delete);
        oriMeter.setMeterStatus(CommonConstants.getMeterStatus(code));
        meterDao.update(oriMeter);  
        meterDao.flushAndClear();
        
        return resultId;
    }


    // 미터 설치지역 정보 변경
    public Map<String, Object> updateMeterLoc(Meter meter){
        Map<String, Object> result = new HashMap<String, Object>();

        Meter oriMeter = meterDao.get(meter.getId());

        Double gpioX = meter.getGpioX();
        Double gpioY = meter.getGpioY();
        Double gpioZ = meter.getGpioZ();


        if(gpioX != null )
            oriMeter.setGpioX(gpioX);

        if(gpioY != null )
            oriMeter.setGpioY(gpioY);

        if(gpioZ != null )
            oriMeter.setGpioZ(gpioZ);


        meterDao.update(oriMeter);
        meterDao.flushAndClear();

        result.put("id", meter.getId());

        return result;
    }

    // 미터 설치지역 정보 변경
    public Map<String, Object> updateMeterAddress(Meter meter){
        Map<String, Object> result = new HashMap<String, Object>();

        Meter oriMeter = meterDao.get(meter.getId());

        String newAddr = meter.getAddress() ;

        if(newAddr != null )
            oriMeter.setAddress(newAddr);


        meterDao.update(oriMeter);
        meterDao.flushAndClear();

        result.put("id", meter.getId());

        return result;
    }


    // 미터 유형별로 조회
    public Object getMeterByType(Map<String, Object> condition){

        Integer meterId     = Integer.parseInt(condition.get("meterId").toString());
        String meterType    = StringUtil.nullToBlank(condition.get("meterType"));

        Object rtnMeter    = null;

        if(meterType.equals(MeterType.EnergyMeter.toString()))
            rtnMeter = energyMeterDao.get(meterId);
        if(meterType.equals(MeterType.WaterMeter.toString()))
            rtnMeter = waterMeterDao.get(meterId);
        if(meterType.equals(MeterType.GasMeter.toString()))
            rtnMeter = gasMeterDao.get(meterId);
        if(meterType.equals(MeterType.HeatMeter.toString()))
            rtnMeter = heatMeterDao.get(meterId);
        if(meterType.equals(MeterType.VolumeCorrector.toString()))
            rtnMeter = volumeCorrectorDao.get(meterId);
        if(meterType.equals(MeterType.SolarPowerMeter.toString()))
            rtnMeter = solarPowerMeterDao.get(meterId);
        if(meterType.equals(MeterType.Inverter.toString()))
            rtnMeter = inverterDao.get(meterId);
        return rtnMeter;

    }

    // 미터별 검침데이터 조회
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Object> getMeteringDataByMeterChart(Map<String, Object> condition){
        List<Object> result = new ArrayList<Object>();
        result = meterDao.getMeteringDataByMeterChart(condition);


        String searchDateType = String.valueOf(condition.get("searchDateType"));

        if (searchDateType.equals("0")) {   // 연별이 아니면
            Supplier supplier = supplierDao.get(Integer.parseInt(String.valueOf(condition.get("supplierId"))));
            for(Object obj: result) {
                HashMap chartDataMap = (HashMap) obj;
                String yyyyMMdd = String.valueOf(chartDataMap.get("meteringDate"));
                chartDataMap.put("meteringDate", TimeLocaleUtil.getLocaleDate(yyyyMMdd.substring(0, 8), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()) + " " + yyyyMMdd.substring(8, 10));
            }
        }else if (!searchDateType.equals("4")) {    // 연별이 아니면
            Supplier supplier = supplierDao.get(Integer.parseInt(String.valueOf(condition.get("supplierId"))));
            for(Object obj: result) {
                HashMap chartDataMap = (HashMap) obj;
                String yyyyMMdd = String.valueOf(chartDataMap.get("meteringDate"));
                chartDataMap.put("meteringDate", TimeLocaleUtil.getLocaleDate(yyyyMMdd, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            }
        } else {
            for(Object obj: result) {
                HashMap chartDataMap = (HashMap) obj;
                String yyyyMMdd = String.valueOf(chartDataMap.get("meteringDate"));
                chartDataMap.put("meteringDate", yyyyMMdd.substring(0, 4) + ". " + yyyyMMdd.substring(4));
            }
        }


        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Object> getMeteringDataByMeterGrid(Map<String, Object> condition){
        List<Object> result = new ArrayList<Object>();
        result = meterDao.getMeteringDataByMeterGrid(condition);

        DecimalFormat dfMd = DecimalUtil.getDecimalFormat(supplierDao.get(Integer.parseInt(condition.get("supplierId").toString())).getMd());

        for(Object obj: (List)result.get(1)) {

            Map<String, Object> data = (HashMap)obj;

            Object usage = data.get("usage");
            Object co2 = data.get("co2");

            if ( StringUtil.nullToBlank(usage).length() > 0 )
                try {
                    data.put("usage", dfMd.format(Double.parseDouble(usage.toString())));
                } catch (NumberFormatException e) {
                    data.put("usage", usage.toString());
                    
                }

            if ( StringUtil.nullToBlank(co2).length() > 0 )
                try {
                    data.put("co2", dfMd.format(Double.parseDouble(co2.toString())));
                } catch (NumberFormatException e) {
                    data.put("co2", co2.toString());
                    
                }
        }

        return result;
    }


    public List<Object> getMeterListForContract(Map<String, Object> condition){
        List<Object> result = new ArrayList<Object>();
        result = meterDao.getMeterListForContract(condition);
        return result;
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public List<Meter> getMeterList(Map<String, Object> condition) {
        return meterDao.getMeterList(condition);

    }
    public List<Object> getMeterListContractExtJs(Map<String, Object> condition){
        List<Object> result = new ArrayList<Object>();
        result = meterDao.getMeterListForContractExtJs(condition);

        return result;
    }

    @Override
    public List<Meter> getManualMeterList(Map<String, Object> condition) {
        condition.put("isManualMeter", "1");
        return meterDao.getMeterList(condition);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> getMeterListExcel(Map<String, Object> condition) {
        condition.put("excelList", "list");

        // 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
        List<Object> result = new ArrayList<Object>();
        result = (List<Object>) this.getMeterSearchGrid(condition).get(1);

        List<Object> resultList = new ArrayList<Object>();
        HashMap<String,Object> resultMap = null;
        if(result.size() > 0) {
            Map<String,Object> tmp = null;
            for(Object obj:result) {
                tmp = new HashMap<String,Object>();
                tmp = (Map<String,Object>)obj;

                resultMap = new HashMap<String,Object>();
                resultMap.put("no",               StringUtil.nullToBlank(tmp.get("no")));
                resultMap.put("meterMds",         StringUtil.nullToBlank(tmp.get("meterMds")));
                resultMap.put("meterType",        StringUtil.nullToBlank(tmp.get("meterType")));
                resultMap.put("mcuSysID",         StringUtil.nullToBlank(tmp.get("mcuSysID")));
                resultMap.put("vendorName",       StringUtil.nullToBlank(tmp.get("vendorName")));
                resultMap.put("modelName",        StringUtil.nullToBlank(tmp.get("modelName")));
                resultMap.put("modemModelName",   StringUtil.nullToBlank(tmp.get("modemModelName")));
                resultMap.put("contractNumber",   StringUtil.nullToBlank(tmp.get("contractNumber")));
                //resultMap.put("customer",       StringUtil.nullToBlank(tmp.get("customer")));
                resultMap.put("installDate",      StringUtil.nullToBlank(tmp.get("installDate")));
                resultMap.put("lastCommDate",     StringUtil.nullToBlank(tmp.get("lastCommDate")));
                resultMap.put("locName",          StringUtil.nullToBlank(tmp.get("locName")));
                resultMap.put("commStatus",       StringUtil.nullToBlank(tmp.get("commStatus")));
                resultMap.put("meterId",          StringUtil.nullToBlank(tmp.get("meterId")));
                resultMap.put("customerId",       StringUtil.nullToBlank(tmp.get("customerId")));
                resultMap.put("customerName",     StringUtil.nullToBlank(tmp.get("customerName")));
                resultMap.put("installId",        StringUtil.nullToBlank(tmp.get("installId")));
                resultMap.put("transformerRatio", StringUtil.nullToBlank(tmp.get("transformerRatio")));
                resultMap.put("ct",               StringUtil.nullToBlank(tmp.get("ct")));
                resultMap.put("installProperty",  StringUtil.nullToBlank(tmp.get("installProperty")));
                // SORIA - Customer Address를 사용
                resultMap.put("customerAddress",  StringUtil.nullToBlank(tmp.get("customerAddr")));
                resultMap.put("address",          StringUtil.nullToBlank(tmp.get("address")));
                resultMap.put("address1",         StringUtil.nullToBlank(tmp.get("address1")));
                resultMap.put("address2",         StringUtil.nullToBlank(tmp.get("address2")));
                resultMap.put("address3",         StringUtil.nullToBlank(tmp.get("address3")));
                resultMap.put("meterAddress",     StringUtil.nullToBlank(tmp.get("meterAddress")));
                resultMap.put("ver", 			  StringUtil.nullToBlank(tmp.get("ver")));
                resultMap.put("modemId",          StringUtil.nullToBlank(tmp.get("modemId")));
                resultMap.put("swVer",         	  StringUtil.nullToBlank(tmp.get("swVer")));
                resultMap.put("hwVer",            StringUtil.nullToBlank(tmp.get("hwVer")));
                resultMap.put("gs1",          	  StringUtil.nullToBlank(tmp.get("gs1")));
                resultMap.put("manufacturedDate", StringUtil.nullToBlank(tmp.get("manufacturedDate")));

                resultList.add(resultMap);
            }
        }

        return resultList;
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getMeterCommInfoListExcel(Map<String, Object> condition) {
		condition.put("isExcel", true);

		List<Object> result = new ArrayList<Object>();
		result = (List<Object>) this.getMeterSearchChart(condition).get(0);

		List<Object> resultList = new ArrayList<Object>();
		HashMap<String, Object> resultMap = null;
		if (result.size() > 0) {
			Map<String, Object> tmp = null;
			for (Object obj : result) {
				tmp = new HashMap<String, Object>();
				tmp = (Map<String, Object>) obj;

				resultMap = new HashMap<String, Object>();
				resultMap = new HashMap<String, Object>();
				resultMap.put("no", StringUtil.nullToBlank(tmp.get("no")));
				resultMap.put("mcuSysId", StringUtil.nullToBlank(tmp.get("mcuSysID")));
				resultMap.put("activity24", StringUtil.nullToBlank(tmp.get("value0")));
				resultMap.put("noActivity24", StringUtil.nullToBlank(tmp.get("value1")));
				resultMap.put("noActivity48", StringUtil.nullToBlank(tmp.get("value2")));
				resultMap.put("unknown", StringUtil.nullToBlank(tmp.get("value3")));
				resultMap.put("commError", StringUtil.nullToBlank(tmp.get("value4")));
				resultMap.put("securityError", StringUtil.nullToBlank(tmp.get("value5")));
				resultMap.put("powerDown", StringUtil.nullToBlank(tmp.get("value6")));

				resultList.add(resultMap);
			}
		}

		return resultList;
	}

    /**
     * 각종 미터들의 Condition Set 을 생성
     * @param parameters 검색 조건 Map
     * @return Hibernate Criteria에 사용되는 com.aimir.util.Condition 셋
     */
    private Set<Condition> makeConditionForMeter(Map<String, Object> parameters) {
        Set<Condition> conditions = new HashSet<Condition>();

        if(parameters.containsKey("searchDate")){
            String date = (String) parameters.get("searchDate");
            if(date.contains("@")) {
                String [] dates = date.split("@");
                if(dates.length == 2) {
                    String startDate = dates[0] + "000000";
                    String endDate = dates[1] + "235959";
                    conditions.add(
                        new Condition(
                            "lastReadDate",
                            new Object[]{startDate, endDate},
                            null, Restriction.BETWEEN
                        )
                    );
                }
            }
        }
        if(parameters.containsKey("meterType")) {
            conditions.add(
                new Condition("meterType.id", new Object[]{parameters.get("meterType")}, null, Restriction.EQ)
            );
        }

        if(parameters.containsKey("first")) {
            int first = (Integer) ((parameters.get("first") != null) ? parameters.get("first") : 0);
            conditions.add(
                new Condition("", new Object[]{first}, null, Restriction.FIRST)
            );
        }
        if(parameters.containsKey("max")) {
            int first = (Integer) ((parameters.get("max") != null) ? parameters.get("max") : 10);
            conditions.add(
                new Condition("", new Object[]{first}, null, Restriction.MAX)
            );
        }

        conditions.add(
            new Condition("lastReadDate", null, null, Restriction.ORDERBYDESC)
        );

        return conditions;
    }

    @Override
    @SuppressWarnings("unchecked")
    @WebMethod(exclude=true)
    public <T extends Meter> List<T> getSpecificMeterList(Map<String, Object> parameters, Class<T> type) {

        if(type == null || !Meter.class.isAssignableFrom(type)) {
            throw new IllegalStateException("invalid.meterType");
        }
        Code code = CommonConstants.getMeterTypeByName(type.getSimpleName());
        parameters.put("meterType", code.getId());

        Set<Condition> conditions = makeConditionForMeter(parameters);

        return (List<T>) meterDao.findByConditions(conditions);
    }

    @Override
    @WebMethod(exclude=true)
    public List<Object> getMiniChart(Map<String, Object> condition,
            String fmtmessagecommalert)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getMcuIdFromMdsId(String mdsId) {
        return meterDao.getMcuIdFromMdsId(mdsId);
    }

    @Override
    public int setLocation(String meterId, String address, double x, double y,
            double z) {
        try {
            Meter meter = meterDao.get(meterId);
            if (meter == null)
                return 1;
            
            if (address != null && !"".equals(address))
                meter.setAddress(address);
            
            meter.setGpioX(x);
            meter.setGpioY(y);
            meter.setGpioZ(z);
            meterDao.update(meter);
            
            return 0;
        }
        catch (Exception e) {
            log.error(e, e);
            return 2;
        }
    }
    
    @Override
	public Integer getTotalMeterCount() {
    	return meterDao.getTotalMeterCount();
    }
    
    public List<String> getFirmwareVersionList(Map<String, Object> condition){
    	List<String> versionList = meterDao.getFirmwareVersionList(condition);
    	return versionList;
    }

    public List<String> getDeviceList(Map<String, Object> condition){
    	List<String> deviceList = meterDao.getDeviceList(condition);
    	return deviceList;
    }
    
    public List<String> getTargetList(Map<String, Object> condition){
    	List<String> deviceList = meterDao.getTargetList(condition);
    	return deviceList;
    }
    
    public List<String> getDeviceListMeter(Map<String, Object> condition){
    	List<String> deviceList = meterDao.getDeviceListMeter(condition);
    	return deviceList;
    }
	
	public List<String> getTargetListMeter(Map<String, Object> condition){
	   	List<String> deviceList = meterDao.getTargetListMeter(condition);
	   	return deviceList;
	}
	
	 public List<Object> getMeterListCloneonoff(Map<String, Object> condition) {
	    	List<Object> deviceList = meterDao.getMeterListCloneonoff(condition);
	    	
	    	String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
	    	Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
	    	for (Object data : deviceList) 
			{
		    	Map<String, Object> mapData = (Map<String, Object>) data;
		    	mapData.put("LASTCOMMDATE", TimeLocaleUtil.getLocaleDate(
						StringUtil.nullToBlank(mapData.get("LASTCOMMDATE")),
						supplier.getLang().getCode_2letter(), supplier
								.getCountry().getCode_2letter()));
		    	
			}
	    	return deviceList;
	    }
	 
	 /* SP-1050
	 * @see com.aimir.service.device.MeterManager#getMsaListByLocationName(java.lang.String)
	 */
	public List<Object> getMsaListByLocationName(String locationName)
	 {
		 List<Object> msaList= meterDao.getMsaListByLocationName(locationName);
		 return msaList;
	 }

	@Override
	public List<Map<String, Object>> getParentDevice(Map<String, Object> condition) {
		List<Map<String, Object>> deviceList = meterDao.getParentDevice(condition);
    	return deviceList;
	}
}
