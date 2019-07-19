package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

public interface SearchMeteringDataManager {
    
    public Map<String,String> getLocationList();    
    public Map<String,String>  getMeteringDataHourTotal(String[] value,String type, String supplierId);
    public Map<String,String>  getMeteringDataDayTotal(String[] value,String type, String supplierId);
    public Map<String,String>  getMeteringDataDayWeekTotal(String[] value,String type, String supplierId);
    public Map<String,String>  getMeteringDataWeekTotal(String[] values,String type, String supplierId);
    public Map<String,String>  getMeteringDataMonthTotal(String[] value,String type, String supplierId);
    public Map<String,String>  getMeteringDataSeasonTotal(String[] value,String type, String supplierId);
    public Map<String,String>  getMeteringDataYearTotal(String[] value,String type, String supplierId);
    public List<?> getMeteringDataHour(String[] value,String type, String supplierId);
    public List<?> getMeteringDataDay(String[] value,String type, String supplierId);
    public List<?> getMeteringDataDayWeek(String[] value,String type, String supplierId);
    public List<?> getMeteringDataWeek(String[] value,String type, String supplierId);
    public List<?> getMeteringDataMonth(String[] value,String type, String supplierId);
    public List<?> getMeteringDataSeason(String[] value,String type, String supplierId);
    public List<?> getMeteringDataYear(String[] value,String type, String supplierId);
    
    //Excel 검색
    public List<?> getMeteringDataHourExcel(String[] value,String type, String supplierId);
    public List<?> getMeteringDataDayExcel(String[] value,String type, String supplierId);
    public List<?> getMeteringDataDayWeekExcel(String[] value,String type, String supplierId);
    public List<?> getMeteringDataWeekExcel(String[] value,String type, String supplierId);
    public List<?> getMeteringDataMonthExcel(String[] value,String type, String supplierId);
    public List<?> getMeteringDataSeasonExcel(String[] value,String type, String supplierId);
    public List<?> getMeteringDataYearExcel(String[] value,String type, String supplierId);
    
    public Map<String,String> getTariffTypeList();

    /**
     * method name : getMeteringDataHourlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 시간별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> contractNumber : Contract.contractNumber - contract number
     * <li> customerName : Customer.name - customer name
     * <li> meteringSF : String - Metering 성공여부
     * <li> searchDateType : String - 조회일자타입
     * <li> searchStartDate : String - 조회시작일자(yyyyMMdd)
     * <li> searchStartHour : String - 조회시작시간(HH)
     * <li> searchEndDate : String - 조회종료일자(yyyyMMdd)
     * <li> searchEndHour : String - 조회종료시간(HH)
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
     *                      meteringTime : String - 미터링시간(yyyyMMddHH 형식의 locale format)
     *                      meterNo : Meter.mdsId - meter no
     *                      modemId : Modem.deviceSerial - modem id
     *                      value : Lp.value - metering data
     *                      prevValue : Lp.value - 이전일자 metering data
     *                     }
     */
    public List<Map<String, Object>> getMeteringDataHourlyData(Map<String, Object> conditionMap);
    /**
     * method name : getMeteringValueDataHourlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 시간별 지침값을 조회한다.
     *
     * @param conditionMap
     */
    public List<Map<String, Object>> getMeteringValueDataHourlyData(Map<String, Object> conditionMap);
    
    

    /**
     * method name : getMeteringDataHourlyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 시간별 검침데이터의 total count 를 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> contractNumber : Contract.contractNumber - contract number
     * <li> customerName : Customer.name - customer name
     * <li> meteringSF : String - Metering 성공여부
     * <li> searchDateType : String - 조회일자타입
     * <li> searchStartDate : String - 조회시작일자(yyyyMMdd)
     * <li> searchStartHour : String - 조회시작시간(HH)
     * <li> searchEndDate : String - 조회종료일자(yyyyMMdd)
     * <li> searchEndHour : String - 조회종료시간(HH)
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
    public Integer getMeteringDataHourlyDataTotalCount(Map<String, Object> conditionMap);
    /**
     * method name : getMeteringValueDataHourlyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 시간별 지침값의 total count 를 조회한다.
     *
     * @param conditionMap
     */
    public Integer getMeteringValueDataHourlyDataTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataDailyData<b/>
     * method Desc : Metering Data 맥스가젯에서 일별 검침데이터를 조회한다.
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
    public List<Map<String, Object>> getMeteringDataDailyData(Map<String, Object> conditionMap);
    // Improvement Version 16-10-03
    public List<Map<String, Object>> getMeteringDataDailyData2(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataDailyDataTotalCount<b/>
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
    public Integer getMeteringDataDailyDataTotalCount(Map<String, Object> conditionMap);
    // Improvement Version 16-10-03
    public Integer getMeteringDataDailyDataTotalCount2(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataWeeklyData<b/>
     * method Desc : Metering Data 맥스가젯에서 주별 검침데이터를 조회한다.
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
    public List<Map<String, Object>> getMeteringDataWeeklyData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataWeeklyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 주별 검침데이터의 total count 를 조회한다.
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
    public Integer getMeteringDataWeeklyDataTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataWeekDailyData<b/>
     * method Desc : Metering Data 맥스가젯에서 요일별 검침데이터를 조회한다.
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
    public List<Map<String, Object>> getMeteringDataWeekDailyData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataWeekDailyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 요일별 검침데이터의 total count 를 조회한다.
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
    public Integer getMeteringDataWeekDailyDataTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 월별 검침데이터를 조회한다.
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
     *                      meteringTime : String - 미터링시간(yyyyMM 형식의 locale format)
     *                      meterNo : Meter.mdsId - meter no
     *                      modemId : Modem.deviceSerial - modem id
     *                      value : Month.value - metering data
     *                      prevValue : Month.value - 이전일자 metering data
     *                     }
     */
    public List<Map<String, Object>> getMeteringDataMonthlyData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataMonthlyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 월별 검침데이터의 total count 를 조회한다.
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
    public Integer getMeteringDataMonthlyDataTotalCount(Map<String, Object> conditionMap);
    
    /**
     * method name : getMeteringValueMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 월별 지침값을 조회한다.
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
     *                      meteringTime : String - 미터링시간(yyyyMM 형식의 locale format)
     *                      meterNo : Meter.mdsId - meter no
     *                      modemId : Modem.deviceSerial - modem id
     *                      value : Month.value - metering data
     *                      prevValue : Month.value - 이전일자 metering data
     *                     }
     */
    public List<Map<String, Object>> getMeteringValueMonthlyData(Map<String, Object> conditionMap);
    
    /**
     * method name : getMeteringValueMonthlyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 월별 검침데이터의 total count 를 조회한다.
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
    public Integer getMeteringValueMonthlyDataTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataYearlyData<b/>
     * method Desc : Metering Data 맥스가젯에서 연간 검침데이터를 조회한다.
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
    public List<Map<String, Object>> getMeteringDataYearlyData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataYearlyDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 연간 검침데이터의 total count 를 조회한다.
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
    public Integer getMeteringDataYearlyDataTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataSeasonalData<b/>
     * method Desc : Metering Data 맥스가젯에서 계절별 검침데이터를 조회한다.
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
    public List<Map<String, Object>> getMeteringDataSeasonalData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataSeasonalDataTotalCount<b/>
     * method Desc : Metering Data 맥스가젯에서 계절별 검침데이터의 total count 를 조회한다.
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
    public Integer getMeteringDataSeasonalDataTotalCount(Map<String, Object> conditionMap);
    
    public List<Map<String, Object>> getRealTimeMeterValues(Map<String, Object> conditionMap);
    public Integer getRealTimeMeterValuesTotalCount(Map<String, Object> conditionMap);
}