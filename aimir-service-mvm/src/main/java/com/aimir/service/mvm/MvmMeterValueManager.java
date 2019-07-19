package com.aimir.service.mvm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.aimir.service.mvm.bean.ChannelInfo;
import com.aimir.service.mvm.bean.CustomerInfo;
import com.aimir.service.mvm.impl.MvmDetailViewManagerImpl.WeeklyData;

@WebService(name="MvmService", targetNamespace="http://aimir.com/services")
public interface MvmMeterValueManager {
	
	@WebMethod
	@WebResult(name="ChannelInfo")
	public List<ChannelInfo> getChannelInfo(String mdsId, String type);
	
	@WebMethod
	@WebResult(name="ChannelInfoAll")
	public List<ChannelInfo> getChannelInfoAll(String mdsId, String type);
	
	@WebMethod
	@WebResult(name="getMeteringValueDetailLpData")
    public List<Map<String, Object>> getMeteringValueDetailLpData(Map<String, Object> conditionMap);
	
    /**
     * method name : getMeteringDataDetailRatelyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 Rate 별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="getMeteringValueDetailRatelyChartData")
    public Map<String, Object> getMeteringValueDetailRatelyChartData(Map<String, Object> conditionMap);
	
    /**
     * method name : getMeteringValueDetailIntervalChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 Interval 별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="getMeteringValueDetailIntervalChartData")
    public Map<String, Object> getMeteringValueDetailIntervalChartData(Map<String, Object> conditionMap);
	

    /**
     * method name : getMeteringValueDetailHourlyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 시간별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="getMeteringValueDetailHourlyChartData")
    public Map<String, Object> getMeteringValueDetailHourlyChartData(Map<String, Object> conditionMap);
	
    /**
     * method name : getMeteringValueDetailDailyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 일별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="getMeteringValueDetailDailyChartData")
    public Map<String, Object> getMeteringValueDetailDailyChartData(Map<String, Object> conditionMap);
	
    /**
     * method name : getMeteringValueDetailWeeklyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 주별 검침 chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="getMeteringValueDetailWeeklyChartData")
    public Map<String, Object> getMeteringValueDetailWeeklyChartData(Map<String, Object> conditionMap);
	
    /**
     * method name : getMeteringValueDetailMonthlyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 월별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="getMeteringValueDetailMonthlyChartData")
    public Map<String, Object> getMeteringValueDetailMonthlyChartData(Map<String, Object> conditionMap);
	
    /**
     * method name : getMeteringValueDetailWeekDailyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 요일별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="getMeteringValueDetailWeekDailyChartData")
    public Map<String, Object> getMeteringValueDetailWeekDailyChartData(Map<String, Object> conditionMap);
	

    /**
     * method name : getMeteringValueDetailSeasonalChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 계절별 chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="getMeteringValueDetailSeasonalChartData")
    public Map<String, Object> getMeteringValueDetailSeasonalChartData(Map<String, Object> conditionMap);
	
    /**
     * method name : getMeteringValueDetailRatelyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 Rate 별 지침값을 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="MeteringValueDetailRatelyDataList")
    public List<Map<String, Object>> getMeteringValueDetailRatelyData(Map<String, Object> conditionMap);

	@WebMethod(operationName ="MeteringValueDetailHourlyDataList")
	@WebResult(name="MeteringValueDetailHourlyDataList")
    public List<Map<String, Object>> getMeteringValueDetailHourlyData(Map<String, Object> conditionMap);
	
	@WebMethod(operationName ="MeteringValueDetailHourlyDataByInterval")
	@WebResult(name="MeteringValueDetailHourlyDataList")
    public List<Map<String, Object>> getMeteringValueDetailHourlyData(Map<String, Object> conditionMap, boolean isLpInterval);
	
	@WebMethod
	@WebResult(name="getMeteringValueDetailDailyDataList")
    public List<Map<String, Object>> getMeteringValueDetailDailyData(Map<String, Object> conditionMap);
	
	@WebMethod
	@WebResult(name="getMeteringValueDetailWeeklyDataList")
    public List<Map<String, Object>> getMeteringValueDetailWeeklyData(Map<String, Object> conditionMap);
	
    /**
     * method name : getMeteringValueDetailMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 월별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="getMeteringValueDetailMonthlyDataList")
    public List<Map<String, Object>> getMeteringValueDetailMonthlyData(Map<String, Object> conditionMap);
	

    /**
     * method name : getMeteringValueDetailWeekDailyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 요일별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="getMeteringValueDetailWeekDailyDataList")
    public List<Map<String, Object>> getMeteringValueDetailWeekDailyData(Map<String, Object> conditionMap);
	

    /**
     * method name : getMeteringValueDetailSeasonalData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 계절별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
	@WebMethod
	@WebResult(name="MeteringValueDetailSeasonalDataList")
    public List<Map<String, Object>> getMeteringValueDetailSeasonalData(Map<String, Object> conditionMap);
	
    
    /**
     * method name : getMeteringValueDataDailyData<b/>
     * method Desc : Metering Data 맥스가젯에서 일별 지침값을 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> contractNumber : Contract.contractNumber - contract number
     * <li> customerName : Customer.name - customer name
     * <li> meteringSF : String - Metering 성공여부
     * <li> searchDateType : String - 조회일자타입
     * <li> searchStartDate : String - 조회시작일자(yyyyMMdd)
     * <li> searchEndDate : String - 조회종료일자(yyyyMMdd)
     * <li> locationId : Location.id - location id
     * <li> tariffType : TariffType.id - tariff Type id
     * <li> mcuId : MCU.sysID - MCU id
     * <li> contractGroup : GroupMember.groupId - contract group id
     * <li> sicId : Contract.sic.id - sic id
     * <li> meterType : ChangeMeterTypeName.code - meter type
     * <li> tlbType : MeterType.lpClassName - lp class name
     * </ul>
     * 
     * @return List of Map {
     *                      num : Integer - row number
     *                      contractNumber : Contract.contractNumber - contract number
     *                      customerName : Customer.name - customer name
     *                      meteringTime : String - 미터링시간(yyyyMMdd 형식의 locale format)
     *                      meterNo : Meter.mdsId - meter no
     *                      modemId : Modem.deviceSerial - modem id
     *                      value : Lp.value - metering data
     *                      prevValue : Lp.value - 이전일자 metering data
     *                     }
     */
    public List<Map<String, Object>> getMeteringValueDataDailyData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringValueDataDailyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 일별 검침데이터의 total count 를 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> contractNumber : Contract.contractNumber - contract number
     * <li> customerName : Customer.name - customer name
     * <li> meteringSF : String - Metering 성공여부
     * <li> searchDateType : String - 조회일자타입
     * <li> searchStartDate : String - 조회시작일자(yyyyMMdd)
     * <li> searchEndDate : String - 조회종료일자(yyyyMMdd)
     * <li> locationId : Location.id - location id
     * <li> tariffType : TariffType.id - tariff Type id
     * <li> mcuId : MCU.sysID - MCU id
     * <li> contractGroup : GroupMember.groupId - contract group id
     * <li> sicId : Contract.sic.id - sic id
     * <li> meterType : ChangeMeterTypeName.code - meter type
     * <li> tlbType : MeterType.lpClassName - lp class name
     * </ul>
     * 
     * @return Integer - total count
     */
    public Integer getMeteringValueDataDailyDataTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringValueDataWeeklyData<b/>
     * method Desc : Metering Data 맥스가젯에서 주별 지침값을 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> contractNumber : Contract.contractNumber - contract number
     * <li> customerName : Customer.name - customer name
     * <li> meteringSF : String - Metering 성공여부
     * <li> searchDateType : String - 조회일자타입
     * <li> searchStartDate : String - 조회시작일자(yyyyMMdd)
     * <li> searchEndDate : String - 조회종료일자(yyyyMMdd)
     * <li> searchWeek : String - 조회할 주
     * <li> locationId : Location.id - location id
     * <li> tariffType : TariffType.id - tariff Type id
     * <li> mcuId : MCU.sysID - MCU id
     * <li> contractGroup : GroupMember.groupId - contract group id
     * <li> sicId : Contract.sic.id - sic id
     * <li> meterType : ChangeMeterTypeName.code - meter type
     * <li> tlbType : MeterType.lpClassName - lp class name
     * </ul>
     * 
     * @return List of Map {
     *                      num : Integer - row number
     *                      contractNumber : Contract.contractNumber - contract number
     *                      customerName : Customer.name - customer name
     *                      meteringTime : String - 미터링시간(조회한 주. ex.1 Week)
     *                      meterNo : Meter.mdsId - meter no
     *                      modemId : Modem.deviceSerial - modem id
     *                      value : Double - 조회하는 주의 metering data sum
     *                      prevValue : Double - 이전 주의 metering data sum
     *                     }
     */
    public List<Map<String, Object>> getMeteringValueDataWeeklyData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringValueDataWeeklyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 주별 지침값의 total count 를 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> contractNumber : Contract.contractNumber - contract number
     * <li> customerName : Customer.name - customer name
     * <li> meteringSF : String - Metering 성공여부
     * <li> searchDateType : String - 조회일자타입
     * <li> searchStartDate : String - 조회시작일자(yyyyMMdd)
     * <li> searchEndDate : String - 조회종료일자(yyyyMMdd)
     * <li> searchWeek : String - 조회할 주
     * <li> locationId : Location.id - location id
     * <li> tariffType : TariffType.id - tariff Type id
     * <li> mcuId : MCU.sysID - MCU id
     * <li> contractGroup : GroupMember.groupId - contract group id
     * <li> sicId : Contract.sic.id - sic id
     * <li> meterType : ChangeMeterTypeName.code - meter type
     * <li> tlbType : MeterType.lpClassName - lp class name
     * </ul>
     * 
     * @return Integer - total count
     */
    public Integer getMeteringValueDataWeeklyDataTotalCount(Map<String, Object> conditionMap);

    
    /**
     * method name : getMeteringValueDataWeekDailyData<b/>
     * method Desc : Metering Data 맥스가젯에서 요일별 지침값을 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> contractNumber : Contract.contractNumber - contract number
     * <li> customerName : Customer.name - customer name
     * <li> meteringSF : String - Metering 성공여부
     * <li> searchDateType : String - 조회일자타입
     * <li> searchStartDate : String - 조회시작일자(yyyyMMdd)
     * <li> searchEndDate : String - 조회종료일자(yyyyMMdd)
     * <li> locationId : Location.id - location id
     * <li> tariffType : TariffType.id - tariff Type id
     * <li> mcuId : MCU.sysID - MCU id
     * <li> contractGroup : GroupMember.groupId - contract group id
     * <li> sicId : Contract.sic.id - sic id
     * <li> meterType : ChangeMeterTypeName.code - meter type
     * <li> tlbType : MeterType.lpClassName - lp class name
     * </ul>
     * 
     * @return List of Map {
     *                      num : Integer - row number
     *                      contractNumber : Contract.contractNumber - contract number
     *                      customerName : Customer.name - customer name
     *                      meteringTime : String - 미터링시간(yyyyMMddEEE 형식의 locale format)
     *                      meterNo : Meter.mdsId - meter no
     *                      modemId : Modem.deviceSerial - modem id
     *                      value : Lp.value - metering data
     *                      prevValue : Lp.value - 이전일자 metering data
     *                     }
     */
    public List<Map<String, Object>> getMeteringValueDataWeekDailyData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringValueDataWeekDailyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 요일별 지침값의 total count 를 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> contractNumber : Contract.contractNumber - contract number
     * <li> customerName : Customer.name - customer name
     * <li> meteringSF : String - Metering 성공여부
     * <li> searchDateType : String - 조회일자타입
     * <li> searchStartDate : String - 조회시작일자(yyyyMMdd)
     * <li> searchEndDate : String - 조회종료일자(yyyyMMdd)
     * <li> locationId : Location.id - location id
     * <li> tariffType : TariffType.id - tariff Type id
     * <li> mcuId : MCU.sysID - MCU id
     * <li> contractGroup : GroupMember.groupId - contract group id
     * <li> sicId : Contract.sic.id - sic id
     * <li> meterType : ChangeMeterTypeName.code - meter type
     * <li> tlbType : MeterType.lpClassName - lp class name
     * </ul>
     * 
     * @return Integer - total count
     */
    public Integer getMeteringValueDataWeekDailyDataTotalCount(Map<String, Object> conditionMap);

    
    /**
     * method name : getMeteringValueDataSeasonalData<b/>
     * method Desc : Metering Data 맥스가젯에서 계절별 지침값을 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> contractNumber : Contract.contractNumber - contract number
     * <li> customerName : Customer.name - customer name
     * <li> meteringSF : String - Metering 성공여부
     * <li> searchDateType : String - 조회일자타입
     * <li> searchStartDate : String - 조회시작일자(yyyyMMdd)
     * <li> searchEndDate : String - 조회종료일자(yyyyMMdd)
     * <li> locationId : Location.id - location id
     * <li> tariffType : TariffType.id - tariff Type id
     * <li> mcuId : MCU.sysID - MCU id
     * <li> contractGroup : GroupMember.groupId - contract group id
     * <li> sicId : Contract.sic.id - sic id
     * <li> meterType : ChangeMeterTypeName.code - meter type
     * </ul>
     * 
     * @return List of Map {
     *                      num : Integer - row number
     *                      contractNumber : Contract.contractNumber - contract number
     *                      customerName : Customer.name - customer name
     *                      meteringTime : String - 미터링시간(계절명)
     *                      meterNo : Meter.mdsId - meter no
     *                      modemId : Modem.deviceSerial - modem id
     *                      value : Double - 해당연도의 metering data sum
     *                      prevValue : Double - 이전연도 metering data sum
     *                     }
     */
    public List<Map<String, Object>> getMeteringValueDataSeasonalData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringValueDataSeasonalDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 계절별 지침값의 total count 를 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> contractNumber : Contract.contractNumber - contract number
     * <li> customerName : Customer.name - customer name
     * <li> meteringSF : String - Metering 성공여부
     * <li> searchDateType : String - 조회일자타입
     * <li> searchStartDate : String - 조회시작일자(yyyyMMdd)
     * <li> searchEndDate : String - 조회종료일자(yyyyMMdd)
     * <li> locationId : Location.id - location id
     * <li> tariffType : TariffType.id - tariff Type id
     * <li> mcuId : MCU.sysID - MCU id
     * <li> contractGroup : GroupMember.groupId - contract group id
     * <li> sicId : Contract.sic.id - sic id
     * <li> meterType : ChangeMeterTypeName.code - meter type
     * </ul>
     * 
     * @return Integer - total count
     */
    public Integer getMeteringValueDataSeasonalDataTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringValueDataYearlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 연간 지침값을 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> contractNumber : Contract.contractNumber - contract number
     * <li> customerName : Customer.name - customer name
     * <li> meteringSF : String - Metering 성공여부
     * <li> searchDateType : String - 조회일자타입
     * <li> searchStartDate : String - 조회시작일자(yyyyMMdd)
     * <li> searchEndDate : String - 조회종료일자(yyyyMMdd)
     * <li> locationId : Location.id - location id
     * <li> tariffType : TariffType.id - tariff Type id
     * <li> mcuId : MCU.sysID - MCU id
     * <li> contractGroup : GroupMember.groupId - contract group id
     * <li> sicId : Contract.sic.id - sic id
     * <li> meterType : ChangeMeterTypeName.code - meter type
     * </ul>
     * 
     * @return List of Map {
     *                      num : Integer - row number
     *                      contractNumber : Contract.contractNumber - contract number
     *                      customerName : Customer.name - customer name
     *                      meteringTime : String - 미터링시간(yyyy)
     *                      meterNo : Meter.mdsId - meter no
     *                      modemId : Modem.deviceSerial - modem id
     *                      value : Double - 해당연도의 metering data sum
     *                      prevValue : Double - 이전연도 metering data sum
     *                     }
     */
    public List<Map<String, Object>> getMeteringValueDataYearlyData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringValueDataYearlyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 연간 지침값의 total count 를 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> contractNumber : Contract.contractNumber - contract number
     * <li> customerName : Customer.name - customer name
     * <li> meteringSF : String - Metering 성공여부
     * <li> searchDateType : String - 조회일자타입
     * <li> searchStartDate : String - 조회시작일자(yyyyMMdd)
     * <li> searchEndDate : String - 조회종료일자(yyyyMMdd)
     * <li> locationId : Location.id - location id
     * <li> tariffType : TariffType.id - tariff Type id
     * <li> mcuId : MCU.sysID - MCU id
     * <li> contractGroup : GroupMember.groupId - contract group id
     * <li> sicId : Contract.sic.id - sic id
     * <li> meterType : ChangeMeterTypeName.code - meter type
     * </ul>
     * 
     * @return Integer - total count
     */
    public Integer getMeteringValueDataYearlyDataTotalCount(Map<String, Object> conditionMap);

}