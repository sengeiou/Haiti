package com.aimir.service.device.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.CircuitBreakerStatus;
import com.aimir.constants.CommonConstants.GetTamperingCmdResult;
import com.aimir.constants.CommonConstants.MdisLp2Pattern;
import com.aimir.constants.CommonConstants.MdisMeterDirection;
import com.aimir.constants.CommonConstants.MdisMeterKind;
import com.aimir.constants.CommonConstants.MdisQualitySide;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.RegType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.EnergyMeterDao;
import com.aimir.dao.device.GasMeterDao;
import com.aimir.dao.device.HeatMeterDao;
import com.aimir.dao.device.MdisMeterDao;
import com.aimir.dao.device.MeterMdisDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.VolumeCorrectorDao;
import com.aimir.dao.device.WaterMeterDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.PowerQualityDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.GasMeter;
import com.aimir.model.device.HeatMeter;
import com.aimir.model.device.MdisMeter;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.VolumeCorrector;
import com.aimir.model.device.WaterMeter;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.DeviceRegistrationManager;
import com.aimir.service.device.MeterMdisManager;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * MeterMdisManagerImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 11. 22. v1.0        문동규   MDIS 관련 method 를 기존 ManagerImpl(MeterManagerImpl) 에서 분리
 * </pre>
 */
@Service(value="meterMdisManager")
@Transactional(readOnly=false)
public class MeterMdisManagerImpl implements MeterMdisManager{
	
    @Autowired
    MeterMdisDao meterMdisDao;

    @Autowired
    LocationDao locDao;

    @Autowired
    ModemDao modemDao;

    @Autowired
    EnergyMeterDao energyMeterDao;

    @Autowired
    WaterMeterDao waterMeterDao;

    @Autowired
    GasMeterDao gasMeterDao;

    @Autowired
    HeatMeterDao heatMeterDao;

    @Autowired
    VolumeCorrectorDao volumeCorrectorDao;

    @Autowired
    DeviceRegistrationManager deviceRegistrationManager;

    @Autowired
    SupplierDao supplierDao;

    @Autowired
    public LocationDao locationDao;

    @Autowired
    LpEMDao lpEMDao;

    @Autowired
    PowerQualityDao powerQualityDao;

    @Autowired
    MdisMeterDao mdisMeterDao;
    
    @Autowired
    CodeDao codeDao;

	public Meter getMeter(Integer meterId){
		Meter rtnMeter = meterMdisDao.get(meterId);
		
		if(rtnMeter.getModem() == null)
			rtnMeter.setModem(null);
		
		return rtnMeter;
	}
	
	public Meter getMeter(String mdsId) {
	    return meterMdisDao.findByCondition("mdsId", mdsId);
	}

	public EnergyMeter getEnergyMeter(Integer meterId){
	    EnergyMeter rtnMeter = energyMeterDao.get(meterId);

	    if(rtnMeter.getModem() == null)
	        rtnMeter.setModem(null);

	    return rtnMeter;
	}

	public void insertMeter(Meter meter){
		meterMdisDao.add(meter);	
	}
	
	public void insertMeter(Map<String, Object> condition){
		
		String meterType    = StringUtil.nullToBlank(condition.get("meterType"));         
		String mdsId        = StringUtil.nullToBlank(condition.get("mdsId"));    
		String modemSerial	= StringUtil.nullToBlank(condition.get("modemSerial"));
		                         
		String vendor		= StringUtil.nullToBlank(condition.get("vendor"));
		String model        = StringUtil.nullToBlank(condition.get("model"));
		String port         = StringUtil.nullToBlank(condition.get("port"));
		                         
		String loc          = StringUtil.nullToBlank(condition.get("loc"));
		//String locDetail    = StringUtil.nullToBlank(condition.get("locDetail"));
		                         
		String supplierId   = StringUtil.nullToBlank(condition.get("supplierId"));
		
		Meter meter               = new Meter();
		
		Modem modem 	   		  = new Modem();
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
		
		Code meterTypeCode 		  = new Code();
		meterTypeCode.setId(Integer.parseInt(meterType));
		meter.setMeterType(meterTypeCode);
		meter.setMdsId(mdsId);
		meter.setModel(deviceModel);
		meter.setModemPort(Integer.parseInt(port));
		meter.setSupplier(supplier);
		
		meterMdisDao.add(meter);
		
		EnergyMeter energyMeter = new EnergyMeter();		
		energyMeter = (EnergyMeter) meter;
		energyMeterDao.add(energyMeter);
	}

	public List<Object> getMiniChart(Map<String, Object> condition) {
		
		// 사용자가 선택한 1/2차 조건에 따라서 데이터 조회
		// meterType / loc / commStatus
    	
    	// ml / mc
    	// lm / lc
    	// cm / cl
		
		String meterChart = condition.get("meterChart").toString();
    	
		List<Object> result                 = new ArrayList<Object>();
		
		// meterType / loc
		if(meterChart.equals("ml")){			
			result = meterMdisDao.getMiniChartMeterTypeByLocation(condition);			
		}
		
		// meterType / commStatus
		if(meterChart.equals("mc")){
			result = meterMdisDao.getMiniChartMeterTypeByCommStatus(condition);			
		}
		
		
		// loc / meterType
		if(meterChart.equals("lm")){			
			result = meterMdisDao.getMiniChartLocationByMeterType(condition);
		}
		
		// loc / commStatus
		if(meterChart.equals("lc")){			
			result = meterMdisDao.getMiniChartLocationByCommStatus(condition);
		}
		
		
		// commStatus / meterType 
		if(meterChart.equals("cm")){			
			result = meterMdisDao.getMiniChartCommStatusByMeterType(condition);
		}
				
		// commStatus / loc
		if(meterChart.equals("cl")){
			result = meterMdisDao.getMiniChartCommStatusByLocation(condition);			
		}
		
		return result;
	}
	
	public List<Object> getMeterSearchChart(Map<String, Object> condition){
		
		// 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
		if(condition.get("sLocationId") != null && !((String)condition.get("sLocationId")).trim().equals("")) {
			List<Integer> locations = locationDao.getLeafLocationId(Integer.parseInt((String)condition.get("sLocationId")), Integer.parseInt((String)condition.get("supplierId")));
			String sLocations = "";
			for(int i=0 ; i<locations.size() ; i++) {
				if(i == 0) {
					sLocations += locations.get(i);
				} else {
					sLocations += ", " + locations.get(i);
				}
			}
			
			condition.put("sLocationId", sLocations);
		}
		
		List<Object> result = new ArrayList<Object>();		
		result = meterMdisDao.getMeterSearchChart(condition);		
		return result;
		
	}	
	

	
	@SuppressWarnings("unchecked")
    public List<Object> getMeterSearchGrid(Map<String, Object> condition){
		
		// 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
		if(condition.get("sLocationId") != null && !((String)condition.get("sLocationId")).trim().equals("")) {
			List<Integer> locations = locationDao.getLeafLocationId(Integer.parseInt((String)condition.get("sLocationId")), Integer.parseInt((String)condition.get("supplierId")));
			String sLocations = "";
			for(int i=0 ; i<locations.size() ; i++) {
				if(i == 0) {
					sLocations += locations.get(i);
				} else {
					sLocations += ", " + locations.get(i);
				}
			}
			
			condition.put("sLocationId", sLocations);
		}
		
		List<Object> result = new ArrayList<Object>();
		result = meterMdisDao.getMeterSearchGrid(condition);
		
		List<Object> gridList = (List<Object>) result.get(1);
		
		String supplierId = StringUtil.nullToBlank( condition.get("supplierId"));
		if(supplierId.length() > 0) {
			Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
			
			for(Object data : gridList) {
				Map<String, Object> mapData = (Map<String, Object>) data;
				
				mapData.put("lastCommDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mapData.get("lastCommDate")) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
				mapData.put("installDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mapData.get("installDate")) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			}
		}
		
		return result;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getMeterLogChart(Map<String, Object> condition){
		
		List<Object> result = new ArrayList<Object>();		
		result = meterMdisDao.getMeterLogChart(condition);
		
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
    		result = meterMdisDao.getMeterCommLog(condition);
    	}else if(logType.equals("updateLog")){
    		// 없음
    		result = meterMdisDao.getMeterCommLog(condition);
    	}else if(logType.equals("brokenLog")){
    		// 없음
    		result = meterMdisDao.getMeterCommLog(condition);
    	}else if(logType.equals("operationLog")){
    		result = meterMdisDao.getMeterOperationLog(condition);
    	}
    	
		return result;
		
	}
	
	// modemId를 통하여, Modem에 연결된 Meter목록을 조회
	public List<Object> getMeterListByModem(Map<String, Object> condition){
		List<Object> result = new ArrayList<Object>();		
		result = meterMdisDao.getMeterListByModem(condition);    	
		return result;		
	}
	
	
	// Modem에 할당되지 않은 meter목록 조회
	public List<Object> getMeterListByNotModem(Map<String, Object> condition){
		List<Object> result = new ArrayList<Object>();		
		result = meterMdisDao.getMeterListByNotModem(condition);    	
		return result;
	}
	
	// Modem에 할당된 미터 삭제
	public Boolean unsetModemId(Map<String, Object> condition){
		
		Boolean result	= true;		
		String[] mdsId  = (String[]) condition.get("mdsId");		
		int mdsIdLen 	= 0;
		
		if(mdsId != null)
			mdsIdLen = mdsId.length;
		
		for(int i=0 ; i < mdsIdLen ; i++){			
			Meter oriMeter = meterMdisDao.get(mdsId[i]);
			oriMeter.setModem(null);
			meterMdisDao.flushAndClear();
		}
		
		return result;
	}
	
	// Modem에 미터 할당
	public Boolean setModemId(Map<String, Object> condition){
		
		Boolean result	= true;		
		String[] mdsId  = (String[]) condition.get("mdsId");
		Integer modemId = (Integer)  condition.get("modemId");
		int mdsIdLen 	= 0;
		
		if(modemId==null)
			return false;
		
		if(mdsId != null)
			mdsIdLen = mdsId.length;
		
		for(int i=0 ; i < mdsIdLen ; i++){			
			Meter oriMeter = meterMdisDao.get(mdsId[i]);			
			Modem modem = modemDao.get(modemId);
			
			oriMeter.setModem(modem);
			
			meterMdisDao.flushAndClear();
		}
		
		return result;
	}
	
	public Map<String, Object> getMeterSearchCondition(){
		Map<String, Object> result = new HashMap<String, Object>();		
		result = meterMdisDao.getMeterSearchCondition();		
		return result;
	}
	
	// 미터 등록 -------------------------------------
	public Map<String, Object> insertEnergyMeter(EnergyMeter energyMeter){
		
		Map<String, Object> result = new HashMap<String, Object>();
		ResultStatus insertResult = ResultStatus.SUCCESS;
		
		try{	
			// Modem 객체 설정
			if(energyMeter.getModem().getDeviceSerial() == null)
				energyMeter.setModem(null);
			else
				energyMeter.setModem(modemDao.get(energyMeter.getModem().getDeviceSerial()));
			
			energyMeter.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));				
			energyMeterDao.codeAdd(energyMeter);
			
			result.put("id", energyMeter.getId());
			
			
		}catch(Exception e){
			insertResult = ResultStatus.FAIL;
		}finally{
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", 	TargetClass.EnergyMeter);
			logData.put("deviceName",   energyMeter.getMdsId());
			logData.put("deviceModel",  energyMeter.getModel());
			logData.put("resultStatus", insertResult);
			logData.put("regType", 		RegType.Manual);
			logData.put("supplier", 	energyMeter.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}
		
		return result;
	}
	
	
    public Map<String, Object> insertWaterMeter(WaterMeter waterMeter){
    	Map<String, Object> result = new HashMap<String, Object>();
    	ResultStatus insertResult = ResultStatus.SUCCESS;

		try{	
	    	if(waterMeter.getModem().getDeviceSerial() == null)
	    		waterMeter.setModem(null);
			else
				waterMeter.setModem(modemDao.get(waterMeter.getModem().getDeviceSerial()));
	    	
	    	waterMeter.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
	    	waterMeterDao.add(waterMeter);
	    	
	    	result.put("id", waterMeter.getId());
		}catch(Exception e){
			insertResult = ResultStatus.FAIL;
		}finally{
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", 	TargetClass.WaterMeter);
			logData.put("deviceName",   waterMeter.getMdsId());
			logData.put("deviceModel",  waterMeter.getModel());
			logData.put("resultStatus", insertResult);
			logData.put("regType", 		RegType.Manual);
			logData.put("supplier", 	waterMeter.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);    
		}
		
    	return result;
    }
    
    public Map<String, Object> insertGasMeter(GasMeter gasMeter){
    	Map<String, Object> result = new HashMap<String, Object>();
    	ResultStatus insertResult = ResultStatus.SUCCESS;
    	
    	try{	
	    	if(gasMeter.getModem().getDeviceSerial() == null)
	    		gasMeter.setModem(null);
			else
				gasMeter.setModem(modemDao.get(gasMeter.getModem().getDeviceSerial()));
	    	
	    	gasMeter.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
	    	gasMeterDao.add(gasMeter);
	    	
	    	result.put("id", gasMeter.getId());
		}catch(Exception e){
			insertResult = ResultStatus.FAIL;
		}finally{
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", 	TargetClass.GasMeter);
			logData.put("deviceName",   gasMeter.getMdsId());
			logData.put("deviceModel",  gasMeter.getModel());
			logData.put("resultStatus", insertResult);
			logData.put("regType", 		RegType.Manual);
			logData.put("supplier", 	gasMeter.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}
    	return result;
    }
    public Map<String, Object> insertHeatMeter(HeatMeter heatMeter){
    	Map<String, Object> result = new HashMap<String, Object>();
    	ResultStatus insertResult = ResultStatus.SUCCESS;
    	
    	try{	
	    	if(heatMeter.getModem().getDeviceSerial() == null)
	    		heatMeter.setModem(null);
			else
				heatMeter.setModem(modemDao.get(heatMeter.getModem().getDeviceSerial()));
	    	
	    	heatMeter.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
	    	heatMeterDao.add(heatMeter);
	    	
	    	result.put("id", heatMeter.getId());
		}catch(Exception e){
			insertResult = ResultStatus.FAIL;
		}finally{
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", 	TargetClass.HeatMeter);
			logData.put("deviceName",   heatMeter.getMdsId());
			logData.put("deviceModel",  heatMeter.getModel());
			logData.put("resultStatus", insertResult);
			logData.put("regType", 		RegType.Manual);
			logData.put("supplier", 	heatMeter.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}	
	    	
    	return result;
    	
    }
    
    public Map<String, Object> insertVolumeCorrector(VolumeCorrector volumeCorrector){
    	Map<String, Object> result = new HashMap<String, Object>();
    	ResultStatus insertResult = ResultStatus.SUCCESS;
    	
    	try{	
	    	if(volumeCorrector.getModem().getDeviceSerial() == null)
	    		volumeCorrector.setModem(null);
			else
				volumeCorrector.setModem(modemDao.get(volumeCorrector.getModem().getDeviceSerial()));
	    	
	    	volumeCorrector.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));
	    	volumeCorrectorDao.add(volumeCorrector);
	    	
	    	result.put("id", volumeCorrector.getId());
		}catch(Exception e){
			insertResult = ResultStatus.FAIL;
		}finally{
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", 	TargetClass.VolumeCorrector);
			logData.put("deviceName",   volumeCorrector.getMdsId());
			logData.put("deviceModel",  volumeCorrector.getModel());
			logData.put("resultStatus", insertResult);
			logData.put("regType", 		RegType.Manual);
			logData.put("supplier", 	volumeCorrector.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}	    	
    	
    	return result;
    }
    
	// 미터 변경 -------------------------------------    
	public Map<String, Object> updateMeter(Meter meter){
		Map<String, Object> result = new HashMap<String, Object>();
			
		Meter oriMeter = meterMdisDao.get(meter.getId());
		
		oriMeter.setMdsId(meter.getMdsId());
		oriMeter.setModel(meter.getModel());		
		oriMeter.setSwVersion(meter.getSwVersion());
		oriMeter.setHwVersion(meter.getHwVersion());
		oriMeter.setInstallDate(meter.getInstallDate());
		
		if(meter.getLocation() != null && meter.getLocation().getId() != null)
			oriMeter.setLocation(locDao.get(meter.getLocation().getId()));
		
		meterMdisDao.update(oriMeter);
		meterMdisDao.flushAndClear();
		
		result.put("id", meter.getId());
		
		return result;
	}
    
	// EnergyMeter 변경
	public Map<String, Object> updateEnergyMeter(EnergyMeter energyMeter){
		Map<String, Object> result = new HashMap<String, Object>();
		
		EnergyMeter oriEnergyMeter = energyMeterDao.get(energyMeter.getId());

		if(energyMeter.getCustomer().getCustomerNo() != null && !energyMeter.getCustomer().getCustomerNo().equals(""))
			oriEnergyMeter.getContract().getCustomer().setCustomerNo(energyMeter.getCustomer().getCustomerNo());	//고객번호
		if(energyMeter.getCustomer().getName() != null && !energyMeter.getCustomer().getName().equals(""))
			oriEnergyMeter.getContract().getCustomer().setName(energyMeter.getCustomer().getName());				//고객명
		
		oriEnergyMeter.setLpInterval( energyMeter.getLpInterval() ); //부하 주기
		oriEnergyMeter.setPulseConstant( energyMeter.getPulseConstant() ); //에너지 상수
		if(energyMeter.getTransformerRatio() != null && energyMeter.getTransformerRatio() >=0)
			oriEnergyMeter.setTransformerRatio(energyMeter.getTransformerRatio());	//변성기 배수
		
		energyMeterDao.update(oriEnergyMeter);
		energyMeterDao.flushAndClear();
		
		result.put("id", energyMeter.getId());
		
		return result;
	}
	
	// WaterMeter 변경
	public Map<String, Object> updateWaterMeter(WaterMeter waterMeter){
		Map<String, Object> result = new HashMap<String, Object>();
		
		WaterMeter oriWaterMeter = waterMeterDao.get(waterMeter.getId());
		
		if(waterMeter.getCustomer().getCustomerNo() != null && !waterMeter.getCustomer().getCustomerNo().equals(""))
			oriWaterMeter.getContract().getCustomer().setCustomerNo(waterMeter.getCustomer().getCustomerNo());	//고객번호
		if(waterMeter.getCustomer().getName() != null && !waterMeter.getCustomer().getName().equals(""))
			oriWaterMeter.getContract().getCustomer().setName(waterMeter.getCustomer().getName());				//고객명
		
		// 펄스상수
		oriWaterMeter.setPulseConstant(waterMeter.getPulseConstant());
		// 최대유속
		oriWaterMeter.setQMax(waterMeter.getQMax());
		//지상/지하
		oriWaterMeter.setUnderGround(waterMeter.getUnderGround());
		
		
		waterMeterDao.update(oriWaterMeter);
		waterMeterDao.flushAndClear();
		
		result.put("id", waterMeter.getId());
		
		return result;
	}
	
	// GasMeter 변경
	public Map<String, Object> updateGasMeter(GasMeter gasMeter){
		Map<String, Object> result = new HashMap<String, Object>();
		
		GasMeter oriGasMeter = gasMeterDao.get(gasMeter.getId());		
		
		if(gasMeter.getCustomer().getCustomerNo() != null && !gasMeter.getCustomer().getCustomerNo().equals(""))
			oriGasMeter.getContract().getCustomer().setCustomerNo(gasMeter.getCustomer().getCustomerNo());	//고객번호
		if(gasMeter.getCustomer().getName() != null && !gasMeter.getCustomer().getName().equals(""))
			oriGasMeter.getContract().getCustomer().setName(gasMeter.getCustomer().getName());				//고객명
		
		// 펄스상수
		oriGasMeter.setPulseConstant(gasMeter.getPulseConstant());
		// 소비지역 - 모름
		
		gasMeterDao.update(oriGasMeter);
		gasMeterDao.flushAndClear();
		
		result.put("id", gasMeter.getId());
		
		return result;
	}

	// HeatMeter 변경
	public Map<String, Object> updateHeatMeter(HeatMeter heatMeter){
		Map<String, Object> result = new HashMap<String, Object>();
		
		HeatMeter oriHeatMeter = heatMeterDao.get(heatMeter.getId());
		
		if(heatMeter.getCustomer().getCustomerNo() != null && !heatMeter.getCustomer().getCustomerNo().equals(""))
			oriHeatMeter.getContract().getCustomer().setCustomerNo(heatMeter.getCustomer().getCustomerNo());	//고객번호
		if(heatMeter.getCustomer().getName() != null && !heatMeter.getCustomer().getName().equals(""))
			oriHeatMeter.getContract().getCustomer().setName(heatMeter.getCustomer().getName());				//고객명
		// 펄스상수
		oriHeatMeter.setPulseConstant(heatMeter.getPulseConstant());
		// 소비지역 - 모름

		// 열량계타입
		oriHeatMeter.setHeatType(heatMeter.getHeatType());
		// 검침단위
		oriHeatMeter.setMeteringUnit(heatMeter.getMeteringUnit());
		// 단위 펄스당 유량
		oriHeatMeter.setFlowPerUnitPulse(heatMeter.getFlowPerUnitPulse());
		// 기계실 번호
		oriHeatMeter.setApparatusRoomNumber(heatMeter.getApparatusRoomNumber());
		
		heatMeterDao.update(oriHeatMeter);
		heatMeterDao.flushAndClear();
		
		result.put("id", heatMeter.getId());
		
		return result;
	}

	// VolumeCorrector 변경
	public Map<String, Object> updateVolumeCorrector(VolumeCorrector volumeCorrector){
		Map<String, Object> result = new HashMap<String, Object>();
		
		VolumeCorrector oriHeat = volumeCorrectorDao.get(volumeCorrector.getId());
	
		// 모름
		
		volumeCorrectorDao.update(oriHeat);
		volumeCorrectorDao.flushAndClear();
		
		result.put("id", volumeCorrector.getId());
		
		return result;
	}
	
	// 미터 삭제
	public Map<String, Object> deleteMeter(Meter meter){
		Map<String, Object> result = new HashMap<String, Object>();
		
		meterMdisDao.deleteById(meter.getId());
		meterMdisDao.flushAndClear();
		
		return result;
	}
	
	// 미터 설치지역 정보 변경
	public Map<String, Object> updateMeterLoc(Meter meter){
		Map<String, Object> result = new HashMap<String, Object>();
			
		Meter oriMeter = meterMdisDao.get(meter.getId());
		
		Double gpioX = meter.getGpioX(); 
		Double gpioY = meter.getGpioY();
		Double gpioZ = meter.getGpioZ();
		
		if(gpioX != null )
			oriMeter.setGpioX(gpioX);
		
		if(gpioY != null )
			oriMeter.setGpioY(gpioY);

		if(gpioZ != null )
			oriMeter.setGpioZ(gpioZ);
			
		meterMdisDao.update(oriMeter);
		meterMdisDao.flushAndClear();
		
		result.put("id", meter.getId());
		
		return result;
	}

	// 미터 설치지역 정보 변경
	public Map<String, Object> updateMeterAddress(Meter meter){
		Map<String, Object> result = new HashMap<String, Object>();
			
		Meter oriMeter = meterMdisDao.get(meter.getId());
		
		String newAddr = meter.getAddress() ;
		
		if(newAddr != null )
			oriMeter.setAddress(newAddr);
				
		meterMdisDao.update(oriMeter);
		meterMdisDao.flushAndClear();
		
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
		
		return rtnMeter;
		
	}
	
	// 미터별 검침데이터 조회
	@SuppressWarnings("unchecked")
	public List<Object> getMeteringDataByMeterChart(Map<String, Object> condition){
		List<Object> result = new ArrayList<Object>();
		result = meterMdisDao.getMeteringDataByMeterChart(condition);
		
		String searchDateType = String.valueOf(condition.get("searchDateType"));
		
		if (searchDateType.equals("0")) {	// 연별이 아니면
			Supplier supplier = supplierDao.get(Integer.parseInt(String.valueOf(condition.get("supplierId"))));
			for(Object obj: result) {
				HashMap chartDataMap = (HashMap) obj;
				String yyyyMMdd = String.valueOf(chartDataMap.get("meteringDate"));
				chartDataMap.put("meteringDate", TimeLocaleUtil.getLocaleDate(yyyyMMdd.substring(0, 8), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()) + " " + yyyyMMdd.substring(8, 10));						
			}
		}else if (!searchDateType.equals("4")) {	// 연별이 아니면
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
	
	@SuppressWarnings("unchecked")
	public List<Object> getMeteringDataByMeterGrid(Map<String, Object> condition){
		List<Object> result = new ArrayList<Object>();
		result = meterMdisDao.getMeteringDataByMeterGrid(condition);
		
		/*
		List<Object> gridList = (List<Object>) result.get(1);
		String searchDateType = String.valueOf(condition.get("searchDateType"));
		if (!searchDateType.equals("4")) {	// 연별이 아니면
			Supplier supplier = supplierDao.get(Integer.parseInt(String.valueOf(condition.get("supplierId"))));
			for(Object obj: gridList) {
				HashMap chartDataMap = (HashMap) obj;
				String yyyyMMdd = String.valueOf(chartDataMap.get("meteringDate"));
				chartDataMap.put("meteringDate", TimeLocaleUtil.getLocaleDate(yyyyMMdd, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));						
			}
		} else {
			for(Object obj: gridList) {
				HashMap chartDataMap = (HashMap) obj;
				String yyyyMMdd = String.valueOf(chartDataMap.get("meteringDate"));
				chartDataMap.put("meteringDate", yyyyMMdd.substring(0, 4) + ". " + yyyyMMdd.substring(4));
			}
		}		
		*/

		DecimalFormat dfMd = DecimalUtil.getDecimalFormat(supplierDao.get(Integer.parseInt(condition.get("supplierId").toString())).getMd());
		
    	for(Object obj: (List)result.get(1)) {

    		Map<String, Object> data = (HashMap)obj;
    		
    		Object usage = data.get("usage");
    		Object co2 = data.get("co2");
    		
    		if (StringUtil.nullToBlank(usage).length() > 0 )
    			data.put("usage", dfMd.format(Double.parseDouble(usage.toString())));
    		
    		if (StringUtil.nullToBlank(co2).length() > 0 )
    			data.put("co2", dfMd.format(Double.parseDouble(co2.toString())));	    		
    	}
		
		return result;
	}
	
	
	public List<Object> getMeterListForContract(Map<String, Object> condition){
		List<Object> result = new ArrayList<Object>();
		result = meterMdisDao.getMeterListForContract(condition);
		return result;
	}

    /**
     * method name : getMeterSearchChartMdis<b/>
     * method Desc : MDIS - Meter Management 화면에서 chart 데이터를 조회한다.
     *
     * @param condition
     * @return
     */
    public List<Object> getMeterSearchChartMdis(Map<String, Object> condition){

        // 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
        if(condition.get("sLocationId") != null && !((String)condition.get("sLocationId")).trim().equals("")) {
            List<Integer> locations = locationDao.getLeafLocationId(Integer.parseInt((String)condition.get("sLocationId")), Integer.parseInt((String)condition.get("supplierId")));
            String sLocations = "";
            for(int i=0 ; i<locations.size() ; i++) {
                if(i == 0) {
                    sLocations += locations.get(i);
                } else {
                    sLocations += ", " + locations.get(i);
                }
            }

            condition.put("sLocationId", sLocations);
        }

        List<Object> result = new ArrayList<Object>();
        result = meterMdisDao.getMeterSearchChartMdis(condition);
        return result;
    }

    /**
     * method name : getMeterSearchGridMdis<b/>
     * method Desc : MDIS - Meter Management 화면에서 미터기 정보 리스트를 조회한다.
     *
     * @param condition
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object> getMeterSearchGridMdis(Map<String, Object> condition) {

        // 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
        if(condition.get("sLocationId") != null && !((String)condition.get("sLocationId")).trim().equals("")) {
            List<Integer> locations = locationDao.getLeafLocationId(Integer.parseInt((String)condition.get("sLocationId")), Integer.parseInt((String)condition.get("supplierId")));
            String sLocations = "";
            for(int i=0 ; i<locations.size() ; i++) {
                if(i == 0) {
                    sLocations += locations.get(i);
                } else {
                    sLocations += ", " + locations.get(i);
                }
            }

            condition.put("sLocationId", sLocations);
        }

        List<Object> result = new ArrayList<Object>();
        result = meterMdisDao.getMeterSearchGridMdis(condition);

        Number totalCount = (Number)(new Long((String) result.get(0)));
        List<Map<String, Object>> gridList = (List<Map<String, Object>>) result.get(1);
        String curPage = StringUtil.nullToBlank(condition.get("curPage"));
        int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
        int firstIdx = Integer.parseInt(curPage) * rowPerPage;
        int cnt = 0;
        int status = 0;
        String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
        Supplier supplier = null;
        String lang = null;
        String country = null;

        DecimalFormat dfMd = null;
        
        if (supplierId.length() > 0) {
            supplier = supplierDao.get(Integer.parseInt(supplierId));
            lang = supplier.getLang().getCode_2letter();
            country = supplier.getCountry().getCode_2letter();
            dfMd = DecimalUtil.getDecimalFormat(supplier.getMd());
        }

        for (Map<String, Object> map : gridList) {
            map.put("no", totalCount.intValue() -cnt - firstIdx);

            if (map.get("customer") != null) {
                map.put("customer", "Y");
            } else {
                map.put("customer", "N");
            }

            status = DecimalUtil.ConvertNumberToInteger(map.get("commStatus"));

            switch(status) {
                case 0:
                    map.put("commStatus", "fmtMessage00");
                    break;

                case 1:
                    map.put("commStatus", "fmtMessage24");
                    break;

                case 2:
                    map.put("commStatus", "fmtMessage48");
                    break;

                default:
                    map.put("commStatus", "");
                    break;
            }

            if (map.get("switchStatus") == null) {
                map.put("switchStatus", "");
            } else if (DecimalUtil.ConvertNumberToInteger(map.get("switchStatus")).equals(CircuitBreakerStatus.Activation.getCode())) {
                map.put("switchStatus", CircuitBreakerStatus.Activation.name());
            } else if (DecimalUtil.ConvertNumberToInteger(map.get("switchStatus")).equals(CircuitBreakerStatus.Deactivation.getCode())) {
                map.put("switchStatus", CircuitBreakerStatus.Deactivation.name());
            } else {
                map.put("switchStatus", "");
            }

            if (supplierId.length() > 0) {
                map.put("lastCommDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(map.get("lastCommDate")) , lang, country));
                map.put("installDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(map.get("installDate")) , lang, country));
                
                if (map.get("lastMeteringValue") != null) {
                    map.put("lastMeteringValue", dfMd.format(DecimalUtil.ConvertNumberToDouble(map.get("lastMeteringValue"))));
                }
            }

            cnt++;
        }

        return result;
    }

    /**
     * method name : getMeterDetailInfo<b/>
     * method Desc : MDIS - Meter Management 맥스가젯의 Detail Information 탭에서 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getMeterDetailInfo(Map<String, Object> conditionMap) {
        Map<String, Object> result = new HashMap<String, Object>();
        // Meter 조회
        Meter meter = meterMdisDao.get((Integer)conditionMap.get("meterId"));
        // MdisMeter 조회
        MdisMeter mdisMeter = mdisMeterDao.get((Integer)conditionMap.get("meterId"));

        Supplier supplier = supplierDao.get(Integer.parseInt(String.valueOf(conditionMap.get("supplierId"))));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        DecimalFormat tdf = new DecimalFormat("##,###");
        Integer prepaymentThreshold = null;

        if (mdisMeter == null) {
            return result;
        }

        Double qualityActivePower = null;
        Double qualityReactivePower = null;
        Double qualityVol = null;
        Double qualityCurrent = null;
        Double qualityKva = null;
        Double qualityPf = null;
        MdisQualitySide qualitySideConst = null;
        boolean hasQualitySide = false;     // qualitySide 가 있으면 true, 없으면 false

        if (mdisMeter.getQualitySide() != null) {
            for (MdisQualitySide obj : MdisQualitySide.values()) {
                if (mdisMeter.getQualitySide().equals(obj.getCode())) {
                    result.put("qualitySide", obj.getMessage());
                    qualitySideConst = obj;
                    break;
                }
            }

            if (qualitySideConst != null) {
                switch (qualitySideConst) {
                    case MAIN:        // Main
                        qualityActivePower = mdisMeter.getQualityActivePowerA();
                        qualityReactivePower = mdisMeter.getQualityReactivePowerA();
                        qualityVol = mdisMeter.getQualityVolA();
                        qualityCurrent = mdisMeter.getQualityCurrentA();
                        qualityKva = mdisMeter.getQualityKvaA();
                        qualityPf = mdisMeter.getQualityPfA();
                        break;
                    case NEUTRAL:   // Neutral
                        qualityActivePower = mdisMeter.getQualityActivePowerB();
                        qualityReactivePower = mdisMeter.getQualityReactivePowerB();
                        qualityVol = mdisMeter.getQualityVolB();
                        qualityCurrent = mdisMeter.getQualityCurrentB();
                        qualityKva = mdisMeter.getQualityKvaB();
                        qualityPf = mdisMeter.getQualityPfB();
                        break;
                }

                result.put("qualityActivePower", mdf.format(StringUtil.nullToDoubleZero(qualityActivePower)));
                result.put("qualityReactivePower", mdf.format(StringUtil.nullToDoubleZero(qualityReactivePower)));
                result.put("qualityVol", mdf.format(StringUtil.nullToDoubleZero(qualityVol)));
                result.put("qualityCurrent", mdf.format(StringUtil.nullToDoubleZero(qualityCurrent)));
                result.put("qualityKva", mdf.format(StringUtil.nullToDoubleZero(qualityKva)));
                result.put("qualityPf", mdf.format(StringUtil.nullToDoubleZero(qualityPf)));
                hasQualitySide = true;
            }
        }

        if (!hasQualitySide) {
            result.put("qualitySide", "");
            result.put("qualityActivePower", null);
            result.put("qualityReactivePower", null);
            result.put("qualityVol", null);
            result.put("qualityCurrent", null);
            result.put("qualityKva", null);
            result.put("qualityPf", null);
        }

        result.put("qualityFrequencyA", mdf.format(StringUtil.nullToDoubleZero(mdisMeter.getQualityFrequencyA())));
        result.put("lp1Timing", mdisMeter.getLp1Timing());

        String lp2Pattern = null;

        if (mdisMeter.getLp2Pattern() != null) {
            for (MdisLp2Pattern constant : MdisLp2Pattern.values()) {
                if (mdisMeter.getLp2Pattern().equals(constant.getCode())) {
                    lp2Pattern = constant.getMessage();
                    break;
                }
            }
        }

        result.put("lp2Pattern", lp2Pattern);
        result.put("lp2Timing", mdisMeter.getLp2Timing());

        String meterDirection = null;
        
        if (mdisMeter.getMeterDirection() != null) {
            for (MdisMeterDirection constant : MdisMeterDirection.values()) {
                if (mdisMeter.getMeterDirection().equals(constant.getCode())) {
                    meterDirection = constant.getMessage();
                    break;
                }
            }
        }

        result.put("meterDirection", meterDirection);

        String meterTime = null;
        if (mdisMeter.getMeterTime() != null) {
            try {
                Date date = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(mdisMeter.getMeterTime());
                meterTime = TimeLocaleUtil.getLocaleDate(date, 14, lang, country);
            } catch(ParseException pe) {
                pe.printStackTrace();
            }
        }
        result.put("meterTime", meterTime);

        String meterKind = null;
        boolean isPrepaid = false;

        if (mdisMeter.getMeterKind() != null) {
            for (MdisMeterKind constant : MdisMeterKind.values()) {
                if (constant.getCode().equals(mdisMeter.getMeterKind())) {
                    meterKind = constant.getMessage();
                    if (constant.equals(MdisMeterKind.PREPAID)) {
                        isPrepaid = true;
                    }
                    break;
                }
            }
        }
        result.put("meterKind", meterKind);
//        String lcdDispContent = null;
//        StringBuilder sbLcdDispContent = new StringBuilder();
        if (isPrepaid) {
            if (meter.getContract() != null) {
                prepaymentThreshold = meter.getContract().getPrepaymentThreshold();
            }

            if (prepaymentThreshold != null) {
                result.put("prepaymentThreshold", mdf.format(prepaymentThreshold));
            } else {
                result.put("prepaymentThreshold", "");
            }

            result.put("prepaidAlertLevel1", mdf.format(mdisMeter.getPrepaidAlertLevel1()));
            result.put("prepaidAlertLevel2", mdf.format(mdisMeter.getPrepaidAlertLevel2()));
            result.put("prepaidAlertLevel3", mdf.format(mdisMeter.getPrepaidAlertLevel3()));
            result.put("prepaidAlertStart", tdf.format(mdisMeter.getPrepaidAlertStart()));
            result.put("prepaidAlertOff", mdf.format(mdisMeter.getPrepaidAlertOff()));

//            lcdDispContent = mdisMeter.getLcdDispContentPre();
//            if (!StringUtil.nullToBlank(lcdDispContent).isEmpty()) {
//                int len = lcdDispContent.length();
//                int idx = 0;
//                for (MdisLcdDisplayContent dispCont : MdisLcdDisplayContent.values()) {
//                    if (lcdDispContent.substring(idx, (idx+1)).equals("1")) {
//                        sbLcdDispContent.append(dispCont.getMessage()).append(",");
//                    }
//                    
//                    if (len == (idx+1)) {
//                        break;
//                    } else {
//                        idx++;
//                    }
//                }
//                
//                int sblen = sbLcdDispContent.length();
//                if (sblen > 0) {
//                    sbLcdDispContent.delete((sblen-1), sblen);
//                }
//            }
        } else {
            result.put("prepaymentThreshold", "");
            result.put("prepaidAlertLevel1", "");
            result.put("prepaidAlertLevel2", "");
            result.put("prepaidAlertLevel3", "");
            result.put("prepaidAlertStart", "");
            result.put("prepaidAlertOff", "");
        }

        String lcdDispContent = mdisMeter.getLcdDispContent();
        if (!StringUtil.nullToBlank(lcdDispContent).isEmpty()) {
            lcdDispContent = lcdDispContent.replaceAll("\r\n", "\n").replaceAll("\n", "<br/>");
        }
        result.put("lcdDispContent", lcdDispContent);
//        result.put("lcdDispContent", mdisMeter.getLcdDispContent());

        result.put("cpuResetRam", mdisMeter.getCpuResetRam());
        result.put("cpuResetRom", mdisMeter.getCpuResetRom());
        result.put("wdtResetRam", mdisMeter.getWdtResetRam());
        result.put("wdtResetRom", mdisMeter.getWdtResetRom());

        String tampBypass = null;
        String tampEarthLd = null;
        String tampReverse = null;
        String tampCoverOp = null;
        String tampFrontOp = null;

        for (GetTamperingCmdResult constant : GetTamperingCmdResult.values()) {
            if (mdisMeter.getTampBypass() != null && constant.getCode().equals(mdisMeter.getTampBypass().toString())) {
                tampBypass = constant.getMessage();
            }
            if (mdisMeter.getTampEarthLd() != null && constant.getCode().equals(mdisMeter.getTampEarthLd().toString())) {
                tampEarthLd = constant.getMessage();
            }
            if (mdisMeter.getTampReverse() != null && constant.getCode().equals(mdisMeter.getTampReverse().toString())) {
                tampReverse = constant.getMessage();
            }
            if (mdisMeter.getTampCoverOp() != null && constant.getCode().equals(mdisMeter.getTampCoverOp().toString())) {
                tampCoverOp = constant.getMessage();
            }
            if (mdisMeter.getTampFrontOp() != null && constant.getCode().equals(mdisMeter.getTampFrontOp().toString())) {
                tampFrontOp = constant.getMessage();
            }
        }

        result.put("tampBypass", tampBypass);
        result.put("tampEarthLd", tampEarthLd);
        result.put("tampReverse", tampReverse);
        result.put("tampCoverOp", tampCoverOp);
        result.put("tampFrontOp", tampFrontOp);
        
        return result;
    }

    /**
     * method name : getMeterMdisExportExcelData<b/>
     * method Desc : MDIS - Meter Management 화면에서 Excel file 에 출력할 data 를 조회한다.
     *
     * @param condition
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<List<Object>> getMeterMdisExportExcelData(Map<String, Object> conditionMap) {
        List<List<Object>> result = new ArrayList<List<Object>>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Object> colList = null;
        Integer sLocationId = (Integer)conditionMap.get("sLocationId");
        
        if (sLocationId != null) {
//            Integer locationId = Integer.parseInt(sLocationId);
            List<Integer> locationIdList = null;
            locationIdList = locationDao.getChildLocationId(sLocationId);
            locationIdList.add(sLocationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        // 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
//        if(condition.get("sLocationId") != null && !((String)condition.get("sLocationId")).trim().equals("")) {
//            List<Integer> locations = locationDao.getLeafLocationId(Integer.parseInt((String)condition.get("sLocationId")), Integer.parseInt((String)condition.get("supplierId")));
//            String sLocations = "";
//            for(int i=0 ; i<locations.size() ; i++) {
//                if(i == 0) {
//                    sLocations += locations.get(i);
//                } else {
//                    sLocations += ", " + locations.get(i);
//                }
//            }
//
//            condition.put("sLocationId", sLocations);
//        }

        list = meterMdisDao.getMeterMdisExportExcelData(conditionMap);

//        Number totalCount = (Number)(new Long((String) result.get(0)));
//        List<Map<String, Object>> gridList = (List<Map<String, Object>>) result.get(1);
//        String curPage = StringUtil.nullToBlank(conditionMap.get("curPage"));
//        int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
//        int firstIdx = Integer.parseInt(curPage) * rowPerPage;
        int cnt = 1;
        int status = 0;
        Integer switchStatus = 0;
//        String supplierId = StringUtil.nullToBlank(conditionMap.get("supplierId"));
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Supplier supplier = null;
        String lang = null;
        String country = null;
        Map<String, String> commStatusMsg = (Map<String, String>)conditionMap.get("commStatusMsg");
        DecimalFormat dfMd = null;
        boolean hasStatus = false;
        boolean hasSwCode = false;
        boolean hasHwCode = false;

        if (supplierId != null) {
            supplier = supplierDao.get(supplierId);
            lang = supplier.getLang().getCode_2letter();
            country = supplier.getCountry().getCode_2letter();
            dfMd = DecimalUtil.getDecimalFormat(supplier.getMd());
        }

        List<Code> swVerList = codeDao.getChildCodes("1.3.6");
        List<Code> hwVerList = codeDao.getChildCodes("1.3.7");

        for (Map<String, Object> obj : list) {
            hasStatus = false;
            hasSwCode = false;
            hasHwCode = false;
            colList = new ArrayList<Object>();
            colList.add(cnt++);
            colList.add(obj.get("METER_MDS"));
            colList.add(obj.get("METER_TYPE"));
            colList.add(obj.get("MCU_SYS_ID"));
            colList.add(obj.get("VENDOR_NAME"));
            colList.add(obj.get("MODEL_NAME"));
            colList.add(obj.get("HAS_CUSTOMER"));
            
            if (supplierId != null && !StringUtil.nullToBlank(obj.get("INSTALL_DATE")).isEmpty()) {
                colList.add(TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(obj.get("INSTALL_DATE")), lang, country));
            } else {
                colList.add("");
            }

            if (supplierId != null && !StringUtil.nullToBlank(obj.get("LAST_COMM_DATE")).isEmpty()) {
                colList.add(TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(obj.get("LAST_COMM_DATE")), lang, country));
            } else {
                colList.add("");
            }

            colList.add(obj.get("LOC_NAME"));

            status = DecimalUtil.ConvertNumberToInteger(obj.get("COMM_STATUS"));

            switch(status) {
                case 0:
                    colList.add(commStatusMsg.get("fmtMessage00"));
                    break;
                case 1:
                    colList.add(commStatusMsg.get("fmtMessage24"));
                    break;
                case 2:
                    colList.add(commStatusMsg.get("fmtMessage48"));
                    break;
                default:
                    colList.add("");
                    break;
            }

            switchStatus = DecimalUtil.ConvertNumberToInteger(obj.get("SWITCH_STATUS"));

            if (switchStatus == null) {
                colList.add("");
            } else {
                for (CircuitBreakerStatus constant : CircuitBreakerStatus.values()) {
                    if (switchStatus.equals(constant.getCode())) {
                        colList.add(constant.name());
                        hasStatus = true;
                        break;
                    }
                }

                if (!hasStatus) {
                    colList.add("");
                }
            }

            colList.add(obj.get("COMMAND_STATUS"));
            colList.add(obj.get("PREPAID_DEPOSIT"));

            if (supplierId != null && obj.get("LAST_METERING_VALUE") != null) {
                colList.add(dfMd.format(DecimalUtil.ConvertNumberToDouble(obj.get("LAST_METERING_VALUE"))));
            } else {
                colList.add("");
            }

            for (Code code : swVerList) {
                if ((code.getId().toString()).equals((String)obj.get("SW_VERSION"))) {
                    colList.add(code.getName());
                    hasSwCode = true;
                    break;
                }
            }

            if (!hasSwCode) {
                colList.add(obj.get("SW_VERSION"));
            }

            for (Code code : hwVerList) {
                if ((code.getId().toString()).equals((String)obj.get("HW_VERSION"))) {
                    colList.add(code.getName());
                    hasHwCode = true;
                    break;
                }
            }

            if (!hasHwCode) {
                colList.add(obj.get("HW_VERSION"));
            }

            colList.add(obj.get("CUSTOMER_NO"));
            colList.add(obj.get("CUSTOMER_NAME"));
            colList.add(obj.get("SUPPLIER_NAME"));
            colList.add((obj.get("LP_INTERVAL") == null) ? 15 : obj.get("LP_INTERVAL"));
            colList.add(obj.get("PULSE_CONSTANT"));
            colList.add(obj.get("TRANSFORMER_RATIO"));
            result.add(colList);
        }

        return result;
    }
}