package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.GasMeter;
import com.aimir.model.device.HeatMeter;
import com.aimir.model.device.Inverter;
import com.aimir.model.device.Meter;
import com.aimir.model.device.SolarPowerMeter;
import com.aimir.model.device.VolumeCorrector;
import com.aimir.model.device.WaterMeter;

@WebService(name="MeterService", targetNamespace="http://aimir.com/services")
public interface MeterManager {
	
	public @WebResult(name="Meter") Meter getMeter(@WebParam(name="MeterIdInteger") Integer meterId);
    
	public @WebResult(name="Meter") Meter getMeter(@WebParam(name="MeterSerialString") String meterSerial);
    
	public void insertMeter(@WebParam(name="Meter") Meter meter);
	
	public void insertMeterByMap(Map<String, Object> condition);
	public Map<String, Object> updateMeter(Meter meter);
	public Map<String, Object> updateWaterMeterInfo(WaterMeter watermeter);
	public Map<String, Object> updateMeterLoc(Meter meter);
	public Map<String, Object> updateMeterAddress(Meter meter);
	
	// MeterMiniGadget
	public List<Object> getMiniChart(@WebParam(name="conditionMap") Map<String, Object> condition);
	
	public List<Object> getMiniChart(Map<String, Object> condition, String fmtmessagecommalert);
	
	// MeterMaxGadget
	public List<Object> getMeterSearchChart(Map<String, Object> condition);	
	public List<Object> getMeterSearchGrid(Map<String, Object> condition);
	public List<Object> getSimpleMeterSearchGrid(Map<String, Object> condition);
	
	public List<Object> getMeterLogChart(Map<String, Object> condition);
	public List<Object> getMeterLogGrid(Map<String, Object> condition);
	
	public Map<String, Object> getMeterSearchCondition();
	
	// ModemMax
	public List<Object> getMeterListByModem(Map<String, Object> condition);
	public List<Object> getMeterListByNotModem(Map<String, Object> condition);
	public Boolean unsetModemId(Map<String, Object> condition);
	public Boolean setModemId(Map<String, Object> condition);
	
	// 미터 등록
    public @WebResult(name="EnergyMeterMap") Map<String, Object> insertEnergyMeter(@WebParam(name="EnergyMeterInstance") EnergyMeter energyMeter);
	
    public @WebResult(name="WaterMeterMap") Map<String, Object> insertWaterMeter(@WebParam(name="WaterMeterInstance") WaterMeter waterMeter);
	
    public @WebResult(name="GasMeterMap") Map<String, Object> insertGasMeter(@WebParam(name="GasMeterInstance") GasMeter gasMeter);
	
    public @WebResult(name="HeatMeterMap") Map<String, Object> insertHeatMeter(@WebParam(name="HeatMeterInstance") HeatMeter heatMeter);
	
    public @WebResult(name="VolumeCorrectorMap") Map<String, Object> insertVolumeCorrector(@WebParam(name="VolumeCorrectorInstance") VolumeCorrector volumeCorrector);
	
    public @WebResult(name="SolarPowerMeterMap") Map<String, Object> insertSolarPowerMeter(@WebParam(name="SolarPowerMeterInstance") SolarPowerMeter solarPowerMeter);
	
    public @WebResult(name="InverterMap") Map<String, Object> insertInverter(@WebParam(name="InverterInstance") Inverter inverter);
    
	// 미터 변경        
    public @WebResult(name="EnergyMeterMap") Map<String, Object> updateEnergyMeter(@WebParam(name="EnergyMeterInstance") EnergyMeter energyMeter);
	
    public @WebResult(name="WaterMeterMap") Map<String, Object> updateWaterMeter(@WebParam(name="WaterMeterInstance") WaterMeter waterMeter);
	
    public @WebResult(name="GasMeterMap") Map<String, Object> updateGasMeter(@WebParam(name="GasMeterInstance") GasMeter gasMeter);
	
    public @WebResult(name="HeatMeterMap") Map<String, Object> updateHeatMeter(@WebParam(name="HeatMeterInstance") HeatMeter heatMeter);
	
    public @WebResult(name="VolumeCorrector") Map<String, Object> updateVolumeCorrector(@WebParam(name="VolumeCorrectorInstance") VolumeCorrector volumeCorrector);
	
    public @WebResult(name="SolarPowerMeterMap") Map<String, Object> updateSolarPowerMeter(@WebParam(name="SolarPowerMeterInstance") SolarPowerMeter solarPowerMeter);
	
    public @WebResult(name="InverterMap") Map<String, Object> updateInverter(@WebParam(name="InverterInstance") Inverter inverter);
    
    // 미터 유형별 조회
    public Object getMeterByType(Map<String, Object> condition);
    

    /**
     * method name : deleteMeter
     * method Desc : Delete meter information by meter id(pk)
     *
     * @param meter
     * @return
     */
    public int deleteMeter(Meter meter);
    public int deleteMeterStatus(Meter meter);
    
    public List<Object> getMeteringDataByMeterChart(Map<String, Object> condition);
    public List<Object> getMeteringDataByMeterGrid(Map<String, Object> condition);
    
    // 고객-계약
    public List<Object> getMeterListForContract(Map<String, Object> condition);
    // customerMax contract information ext-js
    public List<Object> getMeterListContractExtJs(Map<String, Object> condition);
    
    public List<Meter> getMeterList(Map<String, Object> condition);    
    /**
     * 미터 리스트를 조회한다.
     * Generics를 사용한 Type을 받는다.
     * 
     * @param condition 검색조건
     * @param type 미터 Domain Class 타입
     * @param meterType 미터타입
     * @return
     */
    public <T extends Meter> List<T> getSpecificMeterList(Map<String, Object> condition, Class<T> type);
    
	public List<Meter> getManualMeterList(Map<String, Object> condition);
	
	/** 
	 * 페이징처리 안된 미터 리스트를 구하는 메소드
	 * @param conditionMap
	 * @return Meter List
	 */
	public List<Object> getMeterListExcel(Map<String, Object> conditionMap);
	
	public List<Object> getMeterCommInfoListExcel(Map<String, Object> conditionMap);
	
    /**
     * @MethodName getMcuIdFromMdsId
     * @Date 2013. 9. 11.
     * @param mdsId
     * @return MCU의 SYS_ID
     * @Modified
     * @Description 미터의 MDS_ID를 통하여 연결된 MCU의 SYS_ID를 가지고 온다. 
     */	
	public String getMcuIdFromMdsId(String mdsId);

    public @WebResult(name=("result")) int setLocation(@WebParam(name="MeterSerial") String meterId,
            @WebParam(name="Address") String address, 
            @WebParam(name="GPS_X") double x, 
            @WebParam(name="GPS_Y") double y,
            @WebParam(name="GPS_Z") double z);

    public Integer getTotalMeterCount();
    
    public List<String> getFirmwareVersionList(Map<String, Object> condition);
	
	public List<String> getDeviceList(Map<String, Object> condition);
	
	public List<String> getDeviceListMeter(Map<String, Object> condition);
	
	public List<String> getTargetList(Map<String, Object> condition);

	public List<String> getTargetListMeter(Map<String, Object> condition);
	
	public List<Object> getMeterListCloneonoff(Map<String, Object> condition);
	
	 public List<Object> getMsaListByLocationName(String locationName); // SP-1050
	 
	 public List<Map<String, Object>> getParentDevice(Map<String, Object> condition);	//sp-1004
    
}
