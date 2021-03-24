package com.aimir.dao.device;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.Meter;

public interface MeterDao extends GenericDao<Meter, Integer> {
    public List<Object> getMetersByMcuName(String name);
    
    public Meter get(String mdsId);
    
    public Meter getInstallProperty(String installProperty);
    
    /**
     * 검침실패한 미터 목록을 조회한다.
     * @param param
     * @return totalCount 전체건수,list 페이징데이터
     */
    public Map<String,Object> getMeteringFailureMeter(Map<String,Object> param);
    
	/**
	 * 설치일자,공급사 기준 전체 미터갯수를 조회한다.
	 * @param params
	 * @return
	 */
	public Integer getMeterCount(Map<String,Object> params);
	
	/**
	 * LP_XX(전기,가스,수도,열량) 에 검침된 데이터가 특정일자가 전체 누락된
	 * 미터의 갯수를 조회한다.
	 * (누락건이 없는 미터수 + 부분누락된 미터수)를 조회하여 전체 미터수에서 빼서 계산한다.
	 * @param params
	 * @return
	 */
	public Integer getAllMissingMeterCount(Map<String,Object> params);
	
	/**
	 * LP_XX(전기,가스,수도,열량) 에 검침된 데이터가 부분적(특정시간,시간의 특정주기)으로 누락된
	 * 미터의 갯수를 조회한다.
	 * 조회기간에 현재일자가 포함되어있을경우
	 * 1.조회시작일~조회종료일전일
	 * 2.조회종료일,00시~ 현재시간 전시간
	 * 3.조회종료일,현재시간
	 * 세가지 조건으로 각각의 쿼리문을 수행한다.
	 * @param params
	 * @return
	 */
	public Integer getPatialMissingMeterCount(Map<String,Object> params);
	
	/**
	 * 일별,시간별로 LP(전기,가스,수도,열량) 에서 누락된 미터갯수를 조회한다.
	 * 조회일자가 현재일자일경우
	 * 1.00시~ 현재시간 전시간
	 * 2.현재시간
	 * 두가지 조건으로 각각의 쿼리문을 수행한다.
	 * @param params
	 * @return
	 */
	public List<Object> getMissingMetersByHour(Map<String,Object> params);
    

	/**
	 * 일별로 LP(전기,가스,수도,열량) 에서 누락된 미터갯수를 조회한다.
	 * 부분누락,전체누락이 아닌 갯수를 조회하여 전체 미터갯수에서 뺀다음 누락건수를 계산한다.
	 * 
	 * 조회기간에 현재일자가 포함되어있을경우
	 * 1.조회시작일~조회종료일전일
	 * 2.조회종료일,00시~ 현재시간 전시간
	 * 3.조회종료일,현재시간
	 * 세가지 조건으로 각각의 쿼리문을 수행한다.
	 * @param params
	 * @return
	 */
	public List<Object> getMissingMetersByDay(Map<String,Object> params);
	
	/**
	 * 검침데이터(LP) 에 누락된 미터목록을 조회한다.
	 * @param params
	 * @return
	 */
	public List<Object> getMissingMeters(Map<String,Object> params);
	
	public List<Object> getMissingMetersForRecollectByHour(Map<String,Object> params);
	
	public List<Object> getMissingMeters2(Map<String,Object> params);
	
	public String getMissingMetersTotalCnt(Map<String,Object> params);
		

    // Mini Gadget
    public List<Object> getMiniChartMeterTypeByLocation(Map<String, Object> condition);   
    public List<Object> getMiniChartMeterTypeByCommStatus(Map<String, Object> condition);
                           
    public List<Object> getMiniChartLocationByMeterType(Map<String, Object> condition);				
    public List<Object> getMiniChartLocationByCommStatus(Map<String, Object> condition);				
                           
    public List<Object> getMiniChartCommStatusByMeterType(Map<String, Object> condition);
    
    public List<Object> getMiniChartCommStatusByMeterType(Map<String, Object> condition, String[] arrFmtmessagecommalert);
    
    public List<Object> getMiniChartCommStatusByLocation(Map<String, Object> condition);
    
    public List<Object> getMiniChartCommStatusByLocation(Map<String, Object> condition, String[] arrFmtmessagecommalert);
    
    
    

    // Max Gadget
    public List<Object> getMeterSearchChart(Map<String, Object> condition);    
    public List<Object> getMeterSearchGrid(Map<String, Object> condition);

    /**
     * Retrieve Simple Information Of Meters
     * @param condition same as getMeterSearchGrid
     * @return mdsId, deviceSerial, model (3 items)
     */
    public List<Object> getSimpleMeterSearchGrid(Map<String, Object> condition);
    
    public List<Object> getMeterLogChart(Map<String, Object> condition);    
    public List<Object> getMeterLogGrid(Map<String, Object> condition);
    
    public List<Object> getMeterCommLog(Map<String, Object> condition);
    public List<Object> getMeterOperationLog(Map<String, Object> condition);
    
    public Map<String, Object> getMeterSearchCondition();
    
    public List<Meter> getMeterWithGpio(HashMap<String, Object> condition);

    public List<Meter> getMeterWithoutGpio(HashMap<String, Object> condition);

    /**
     * method name : getMeterMapDataWithoutGpio
     * method Desc : Meter Management 맥스가젯의 위치정보 탭에서 맵정보를 조회한다.
     *
     * @param condition
     * @return
     */
    public List<Meter> getMeterMapDataWithoutGpio(HashMap<String, Object> condition);

    public List<Meter> getMeterHavingModem(Integer id);
    
    public List<Object> getMeteringDataByMeterChart(Map<String, Object> condition);
    public List<Object> getMeteringDataByMeterGrid(Map<String, Object> condition);
    
    // ModemMax Gadget
    public List<Object> getMeterListByModem(Map<String, Object> condition);
    public List<Object> getMeterListByNotModem(Map<String, Object> condition);    
    
 // VEE에서 사용
    public Integer getMeterVEEParamsCount(HashMap<String, Object> hm);
    
    
    // 고객-계약
    public List<Object> getMeterListForContract(Map<String, Object> condition);
    
    //// customerMax contract information ext-js
    public List<Object> getMeterListForContractExtJs(Map<String, Object> condition);

    @Deprecated
	public List<Object> getGroupMember(Map<String, Object> condition);

    /**
     * method name : getMemberSelectData
     * method Desc : Group Management 가젯에서 Member 로 등록 가능한 Meter 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap);

	/**
	 * 
	 * @param deviceSerial 모뎀 시리얼번호
	 * @param modemPort    미터와 매핑된 모뎀 포트번호 (디폴트 0)
	 * @return
	 */
	public Meter getMeterByModemDeviceSerial(String deviceSerial, int modemPort);
	
	public List<Object> getMeterSupplierList();
	public Integer getSLAMeterCount(String today, int supplierId);

    /**
     * method name : getEbsDtsTreeDtsMeterNodeData
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 Meter Node Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEbsDtsTreeDtsMeterNodeData(Map<String, Object> conditionMap);

    /**
     * method name : getEbsDtsTreeContractMeterNodeData
     * method Desc : 전기에 대한 Energy Balance Monitoring 맥스가젯의 DTS Tree 의 Contract Meter Node Data 를 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li>
     * <li>
     * <li>
     * <li>
     * <li>
     * <li>
     * <li>
     * <li>
     * </ul>
     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
     *         Integer meterId = (Integer)conditionMap.get("meterId");
     *         Integer phaseId = (Integer)conditionMap.get("phaseId");
     *         String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
     *         String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate")); 
     *
     * @return List of Map {MDS_ID : Meter.mdsId - dts meter mds id
     *                      CONTRACT_NUMBER : Contract.contractNumber - contract number
     *                      CONT_METER_ID : Meter.id - meter id
     *                      CONSUME_ENERGY_TOTAL : Double - consumption energy sum}
     * 
     */
    public List<Map<String, Object>> getEbsDtsTreeContractMeterNodeData(Map<String, Object> conditionMap);

    /**
     * method name : getEbsMeterList
     * method Desc : Energy Balance Monitoring 맥스가젯의 Meter List 를 조회한다.
     *
     * @param conditionMap
     * @param isTotal
     * @return
     */
    public List<Map<String, Object>> getEbsMeterList(Map<String, Object> conditionMap, boolean isTotal);
    
    /**
     * method name : getEbsContractList
     * method Desc : 전기에 대한 Energy Balance Monitoring 맥스가젯의 Contract Meter List 를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *      String customerId = StringUtil.nullToBlank(conditionMap.get("customerId"));
     *      String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
     *      String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
     *      String mdsId = StringUtil.nullToBlank(conditionMap.get("mdsId"));
     *      List%ltInteger&gt locationIdList = (List&ltInteger&gt)conditionMap.get("locationIdList");
     *      Integer supplierId = (Integer)conditionMap.get("supplierId");
     *      Integer contractGroupId = (Integer)conditionMap.get("contractGroupId");
     *      Integer page = (Integer)conditionMap.get("page");
     *      Integer limit = (Integer)conditionMap.get("limit");
     * 
     * @param isTotal total Count를 구해서 리턴할지 여부
     * @return if isTotal is true then return  map.put("total", count);
     */
    public List<Map<String, Object>> getEbsContractMeterList(Map<String, Object> conditionMap, boolean isTotal);

    /**
     * method name : getDeleteEbsContractMeterNodeListByMeter
     * method Desc : 전기에 대한 Energy Balance Monitoring 맥스가젯의 삭제하는 Meter Node 에 포함되어있는 Contract List 를 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id
     * <li> meterId : Meter.id
     * </ul>
     *
     * @return List of Meter @see com.aimir.model.device.Meter
     */
    public List<Meter> getDeleteEbsContractMeterNodeListByMeter(Map<String, Object> conditionMap);

    /**
     * method name : getEbsDtsChartConsumeData
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree Chart 의 당월/전월/전년도 Consume Energy Data 를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
     *         Integer dtsId = (Integer)conditionMap.get("dtsId");
     *         Integer meterId = (Integer)conditionMap.get("meterId");
     *         Integer phaseId = (Integer)conditionMap.get("phaseId");
     *         Integer contractId = (Integer)conditionMap.get("contractId");
     *         Integer depth = (Integer)conditionMap.get("depth");
     *         String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
     *         String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
     *   
     * @return List of Map {YYYYMMDD, ENERGY_SUM}
     */
    public List<Map<String, Object>> getEbsDtsChartConsumeData(Map<String, Object> conditionMap);

    /**
     * method name : getEbsDtsChartContractMeterIds
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree Chart 의 조건에 해당하는 Contract Meter ID 리스트를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
     *         Integer depth = (Integer)conditionMap.get("depth");
     *         Integer dtsId = (Integer)conditionMap.get("dtsId");
     *         Integer meterId = (Integer)conditionMap.get("meterId");
     *         Integer phaseId = (Integer)conditionMap.get("phaseId");
     *         Integer contractId = (Integer)conditionMap.get("contractId");
     *         String nodeId = StringUtil.nullToBlank(conditionMap.get("nodeId"));
     *         String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
     *         String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
     *         String searchPreStartDate = StringUtil.nullToBlank(conditionMap.get("searchPreStartDate"));
     * @return List of Map {CONTRACT_ID}
     */
    public List<Map<String, Object>> getEbsDtsChartContractMeterIds(Map<String, Object> conditionMap);

    /**
     * method name : getMeters
     * method Desc : mcu id와 short id를 이용하여 계량기를 조회한다.
     * 
     * @param mcuId
     * @param shortId
     * @return
     */
    public List<Meter> getMeters(String mcuId, Integer shortId);
    
    /**
     * 미터리스트를 조회한다.
     * Meter find 에 사용될 컨디션을 변수로 받게 되어 있다.
     * 
     * @param condition Map 형식의 컨디션 변수
     * @return
     */
    public List<Meter> getMeterList(Map<String, Object> condition);

    /**
     * method name : getMeterCountListPerLocation
     * method Desc :
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> meterType : String - meter type
     * <li> endDate : string - 조회종료일자 (yyyyMMdd)
     * </ul>
     * @return List of Map {
     *                      LOC_ID : Location.id - location id
     *                      METER_CNT : Integer - meter count
     *                     }
     */
    public List<Map<String, Object>> getMeterCountListPerLocation(Map<String, Object> conditionMap);
    
    /**
     * @MethodName getMcuIdFromMdsId
     * @Date 2013. 9. 11.
     * @param mdsId
     * @return MCU의 SYS_ID
     * @Modified
     * @Description 미터의 MDS_ID를 통하여 연결된 MCU의 SYS_ID를 가지고 온다. 
     */
    public String getMcuIdFromMdsId(String mdsId);
    
    /**
     * method name : getActiveMeterCount
     * method Desc : 활성화된 미터의 개수를 조회
     *
     * @param condition
     * @return
     */
    public Integer getActiveMeterCount(Map<String, String> condition);
    
    /**
     * method name : getMeterByModemId
     * method Desc : 
     *
     * @param modemId
     * @return
     */
    public List<Meter> getMeterByModemId(String modemId);
    
    public Integer getTotalMeterCount();
    
    public List<Map<String, Object>> getMeteringRate(String searchTime, String tableName);
    
    public List<Map<String, Object>> getMeteringRate_detail(String searchTime, String tableName, String tempTableName);

    public List<Map<String, Object>> getPilot2MeteringRate(String searchTime, String tableName);
    
    public List<Map<String, Object>> getPilot2MeteringRate_detail(String searchTime, String tableName, String tempTableName);
    
    public List<Map<String, Object>> getSmallScaleMeteringRate(String searchTime);
    
    public List<Map<String, Object>> getSmallScaleMeteringRate_detail(String searchTime);

    public List<Map<String, Object>> getSmallScaleSLAMeteringRate(String searchTime);
    
    public List<Map<String, Object>> getSmallScaleSLAMeteringRate_detail(String searchTime);

    public List<Map<String, Object>> getRollOutMeteringRate(String searchTime);
    
    public List<Map<String, Object>> get48HourNoMeteringRate(String searchTime);
    
    public List<Map<String, Object>> getHLSKeyErrorMeteringRate();
    
    public List<Map<String, Object>> getMeterNoResponseMeteringRate();
    
    public List<Map<String, Object>> getNoValueMeteringRate();
    
    public List<Map<String, Object>> getMeterWithMCU(Map<String, String> condition);
    
    public List<Map<String, Object>> getMeterMMIU(Map<String, String>condition);
    
    public List<Object> getMissingMetersForRecollect(Map<String, Object> params); // SP-784
    
    public List<Object> getProblematicMeters(Map<String, Object> params); // INSERT SP-818

    public List<String> getFirmwareVersionList(Map<String, Object> condition);
    
    public List<String> getDeviceList(Map<String, Object> condition);
    
    public List<String> getDeviceListMeter(Map<String, Object> condition);
    
    public List<String> getTargetList(Map<String, Object> condition);

    public List<String> getTargetListMeter(Map<String, Object> condition);
    
    public List<Object> getMeterListCloneonoff(Map<String, Object> condition);
    
    public  List<Object>  getMsaListByLocationName(String locationName); // SP-1050
    
    public void updateModemIdNull(int id);
    
    public List<Map<String, Object>> getValidMeterList(Map<String, Object> condition);	//sp-1028
    
    public List<Map<String, Object>> getParentDevice(Map<String, Object> condition);	//sp-1004

    public List<Object> getMissingMetersForRecollectSLA(Map<String, Object> params); // SP-1075
    
    /**
     * method name : getMissLpMeter<b/>
     * method Desc : LP 데이터가 누락된 Meter 조회
     *
     * @param condition
     * @return
     */
    public List<Map<String, Object>> getMissLpMeter(Map<String, Object> condition);
    
    
    public List<Map<String, Object>> getRelayOnOffMeters(String action, String dcuSysId, String meterId);
    
}
