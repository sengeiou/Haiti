package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.GasMeter;
import com.aimir.model.device.HeatMeter;
import com.aimir.model.device.Meter;
import com.aimir.model.device.VolumeCorrector;
import com.aimir.model.device.WaterMeter;


/**
 * MeterMdisManager.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 11. 22. v1.0        문동규   MDIS 관련 method 를 기존 Manager(MeterManager) 에서 분리
 * </pre>
 */
public interface MeterMdisManager {
	
	public Meter getMeter(Integer meterId);

	/**
	 * method name : getEnergyMeter<b/>
	 * method Desc :
	 *
	 * @param meterId Meter.id
	 * @return com.aimir.model.device.EnergyMeter
	 */
	public EnergyMeter getEnergyMeter(Integer meterId);

	public Meter getMeter(String meterSerial);
	public void insertMeter(Meter meter);
	public void insertMeter(Map<String, Object> condition);
	public Map<String, Object> updateMeter(Meter meter);
	public Map<String, Object> updateMeterLoc(Meter meter);
	public Map<String, Object> updateMeterAddress(Meter meter);
	
	// MeterMiniGadget 
	public List<Object> getMiniChart(Map<String, Object> condition);
	
	// MeterMaxGadget
	public List<Object> getMeterSearchChart(Map<String, Object> condition);	
	public List<Object> getMeterSearchGrid(Map<String, Object> condition);
	
	public List<Object> getMeterLogChart(Map<String, Object> condition);
	public List<Object> getMeterLogGrid(Map<String, Object> condition);
	
	
	public Map<String, Object> getMeterSearchCondition();
	
	// ModemMax
	public List<Object> getMeterListByModem(Map<String, Object> condition);
	public List<Object> getMeterListByNotModem(Map<String, Object> condition);
	public Boolean unsetModemId(Map<String, Object> condition);
	public Boolean setModemId(Map<String, Object> condition);
	
	
	
	// 미터 등록
    public Map<String, Object> insertEnergyMeter(EnergyMeter energyMeter);
    public Map<String, Object> insertWaterMeter(WaterMeter waterMeter);
    public Map<String, Object> insertGasMeter(GasMeter gasMeter);
    public Map<String, Object> insertHeatMeter(HeatMeter heatMeter);
    public Map<String, Object> insertVolumeCorrector(VolumeCorrector volumeCorrector);
    
	// 미터 변경        
    public Map<String, Object> updateEnergyMeter(EnergyMeter energyMeter);
    public Map<String, Object> updateWaterMeter(WaterMeter waterMeter);
    public Map<String, Object> updateGasMeter(GasMeter gasMeter);
    public Map<String, Object> updateHeatMeter(HeatMeter heatMeter);
    public Map<String, Object> updateVolumeCorrector(VolumeCorrector volumeCorrector);
    
    // 미터 유형별 조회
    public Object getMeterByType(Map<String, Object> condition);
    
    // 미터 삭제
    public Map<String, Object> deleteMeter(Meter meter);
    
    
    public List<Object> getMeteringDataByMeterChart(Map<String, Object> condition);
    public List<Object> getMeteringDataByMeterGrid(Map<String, Object> condition);
    
    // 고객-계약
    public List<Object> getMeterListForContract(Map<String, Object> condition);
    
    /**
     * method name : getMeterSearchChartMdis<b/>
     * method Desc : MDIS - Meter Management 화면에서 chart 데이터를 조회한다.
     *
     * @param condition
     * @return
     */
    public List<Object> getMeterSearchChartMdis(Map<String, Object> condition);

    /**
     * method name : getMeterSearchGridMdis<b/>
     * method Desc : MDIS - Meter Management 화면에서 미터기 정보 리스트를 조회한다.
     *
     * @param condition
     * @return
     */
    public List<Object> getMeterSearchGridMdis(Map<String, Object> condition);

    /**
     * method name : getMeterDetailInfo<b/>
     * method Desc : MDIS - Meter Management 맥스가젯의 Detail Information 탭에서 데이터를 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> meterId : Meter.id
     * <li> supplierId : Supplier.id
     * </ul>
     * 
     * @return List of Map {prepaymentThreshold : Contract.prepaymentThreshold
     *                      qualitySide : MdisMeter.qualitySide
     *                      qualityActivePower : if qualitySide is Main then MdisMeter.qualityActivePowerA 
     *                                           else if qualitySide is Neutral then MdisMeter.qualityActivePowerB
     *                      qualityReactivePower : if qualitySide is Main then MdisMeter.qualityReactivePowerA 
     *                                             else if qualitySide is Neutral then MdisMeter.qualityReactivePowerB
     *                      qualityVol : if qualitySide is Main then MdisMeter.qualityVolA 
     *                                   else if qualitySide is Neutral then MdisMeter.qualityVolB
     *                      qualityCurrent : if qualitySide is Main then MdisMeter.qualityCurrentA 
     *                                       else if qualitySide is Neutral then MdisMeter.qualityCurrentB
     *                      qualityKva : if qualitySide is Main then MdisMeter.qualityKvaA 
     *                                   else if qualitySide is Neutral then MdisMeter.qualityKvaB
     *                      qualityPf : if qualitySide is Main then MdisMeter.qualityPfA 
     *                                  else if qualitySide is Neutral then MdisMeter.qualityPfB
     *                      qualityFrequencyA : MdisMeter.qualityFrequencyA
     *                      lp1Timing : MdisMeter.lp1Timing
     *                      lp2Pattern : MdisMeter.lp2Pattern
     *                      lp2Timing : MdisMeter.lp2Timing
     *                      prepaidAlertLevel1 : MdisMeter.prepaidAlertLevel1
     *                      prepaidAlertLevel2 : MdisMeter.prepaidAlertLevel2
     *                      prepaidAlertLevel3 : MdisMeter.prepaidAlertLevel3
     *                      prepaidAlertStart : MdisMeter.prepaidAlertStart
     *                      prepaidAlertOff : MdisMeter.prepaidAlertOff
     *                      meterDirection : MdisMeter.meterDirection
     *                      meterTime : MdisMeter.meterTime (yyyyMMddHHmmss)
     *                      lcdDispContent : MdisMeter.lcdDispContent
     *                      meterKind : MdisMeter.meterKind
     *                      cpuResetRam : MdisMeter.cpuResetRam
     *                      cpuResetRom : MdisMeter.cpuResetRom
     *                      wdtResetRam : MdisMeter.wdtResetRam
     *                      wdtResetRom : MdisMeter.wdtResetRom
     *                      tampBypass : MdisMeter.tampBypass
     *                      tampEarthLd : MdisMeter.tampEarthLd
     *                      tampReverse : MdisMeter.tampReverse
     *                      tampCoverOp : MdisMeter.tampCoverOp
     *                      tampFrontOp : MdisMeter.tampFrontOp
     *                     }
     */
    public Map<String, Object> getMeterDetailInfo(Map<String, Object> conditionMap);

    /**
     * method name : getMeterMdisExportExcelData<b/>
     * method Desc : MDIS - Meter Management 화면에서 Excel file 에 출력할 data 를 조회한다.
     *
     * @param condition
     * @return
     */
    public List<List<Object>> getMeterMdisExportExcelData(Map<String, Object> conditionMap);
}