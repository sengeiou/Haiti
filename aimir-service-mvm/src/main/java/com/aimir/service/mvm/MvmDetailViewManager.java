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
public interface MvmDetailViewManager {

    @WebMethod
    @WebResult(name="CustomerInfo")
    public CustomerInfo getCustomerInfo(String mdsId, String supplierId);
    @WebMethod
    @WebResult(name="ChannelInfo")
    public List<ChannelInfo> getChannelInfo(String mdsId, String type);
    @WebMethod
    @WebResult(name="ChannelInfoAll")
    public List<ChannelInfo> getChannelInfoAll(String mdsId, String type);
    
    @WebMethod
    @WebResult(name="DetailHourData")
    public HashMap<String, Object> getDetailHourData(String[] values, String type, String supplierId);

    public HashMap<String, Object> getDetailHourData4fc(String[] values, String type, String supplierId);
    @WebMethod
    @WebResult(name="DetailHourlyAllData")
    public Map<String, Object> getDetailHourlyAllData(String[] values, String type, String supplierId);
    @WebMethod
    @WebResult(name="DetailHourlyAllChildrenData")
    public Map<String, Object> getDetailHourlyAllChildrenData(String[] values, String type, String supplierId, String searchDate);
    @WebMethod
    @WebResult(name="DetailDayData")
    public HashMap<String, Object> getDetailDayData(String[] values, String type, String supplierId);
    public HashMap<String, Object> getDetailDayData4fc(String[] values, String type, String supplierId);
    
    @WebMethod
    @WebResult(name="CalendarDetailMonthData")
    public HashMap<String, Object> getCalendarDetailMonthData(String[] values, String type);
    @WebMethod
    @WebResult(name="CalendarDetailMonthChart")
    public HashMap<String, Object> getCalendarDetailMonthChart(String[] values, String type);
    @WebMethod
    @WebResult(name="DetailDayWeekData")
    public HashMap<String, Object> getDetailDayWeekData(String[] values, String type, String supplierId);
    @WebMethod
    @WebResult(name="DetailMonthData")
    public HashMap<String, Object> getDetailMonthData(String[] values, String type, String supplierId);
    public HashMap<String, Object> getDetailMonthData4fc(String[] values, String type, String supplierId);
    @WebMethod
    @WebResult(name="DetailWeekData")
    public HashMap<String, Object> getDetailWeekData(String[] values, String type, String supplierId);
    @WebMethod
    @WebResult(name="DetailSeasonData")
    public HashMap<String, Object> getDetailSeasonData(String[] values, String type, String supplierId);

    /**
     * 1달 기간의 데이터를 주별로 나누고, 하나의 주는 또 일별로 나누어 데이터를 구한다.
     * <br>
     * kskim.
     * @param condition
     * @param type
     * @param supplierId
     * @return
     */
    @WebMethod
    @WebResult(name="WeeklyData")
    public WeeklyData[] getDetailWeeklyUnitData(String[] condition, String type, String supplierId);

    @WebMethod(operationName ="MeteringDataDetailHourlyDataList")
    @WebResult(name="MeteringDataDetailHourlyDataList")
    public List<Map<String, Object>> getMeteringDataDetailHourlyData(Map<String, Object> conditionMap);

    @WebMethod(operationName ="MeteringDataDetailHourlyDataByInterval")
    @WebResult(name="MeteringDataDetailHourlyDataList")
    public List<Map<String, Object>> getMeteringDataDetailHourlyData(Map<String, Object> conditionMap, boolean isLpInterval);

    @WebMethod
    @WebResult(name="MeteringDataDetailLpDataList")
    public List<Map<String, Object>> getMeteringDataDetailLpData(Map<String, Object> conditionMap);

    @WebMethod
    @WebResult(name="MeteringDataDetailDailyDataList")
    public List<Map<String, Object>> getMeteringDataDetailDailyData(Map<String, Object> conditionMap);

    @WebMethod
    @WebResult(name="MeteringDataDetailWeeklyDataList")
    public List<Map<String, Object>> getMeteringDataDetailWeeklyData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataDetailMonthlyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 월별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MeteringDataDetailMonthlyDataList")
    public List<Map<String, Object>> getMeteringDataDetailMonthlyData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataDetailWeekDailyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 요일별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MeteringDataDetailWeekDailyDataList")
    public List<Map<String, Object>> getMeteringDataDetailWeekDailyData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataDetailWeeklyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 주별 검침 chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MeteringDataDetailWeeklyChartData")
    public Map<String, Object> getMeteringDataDetailWeeklyChartData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataDetailWeekDailyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 요일별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MeteringDataDetailWeekDailyChartData")
    public Map<String, Object> getMeteringDataDetailWeekDailyChartData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataDetailSeasonalData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 계절별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MeteringDataDetailSeasonalDataList")
    public List<Map<String, Object>> getMeteringDataDetailSeasonalData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataDetailSeasonalChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 계절별 chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MeteringDataDetailSeasonalChartData")
    public Map<String, Object> getMeteringDataDetailSeasonalChartData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataDetailRatelyData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 Rate 별 검침데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MeteringDataDetailRatelyDataList")
    public List<Map<String, Object>> getMeteringDataDetailRatelyData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataDetailRatelyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 Rate 별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MeteringDataDetailRatelyChartData")
    public Map<String, Object> getMeteringDataDetailRatelyChartData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataDetailIntervalChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 Interval 별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MeteringDataDetailIntervalChartData")
    public Map<String, Object> getMeteringDataDetailIntervalChartData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataDetailDailyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 일별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MeteringDataDetailDailyChartData")
    public Map<String, Object> getMeteringDataDetailDailyChartData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataDetailMonthlyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 월별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MeteringDataDetailMonthlyChartData")
    public Map<String, Object> getMeteringDataDetailMonthlyChartData(Map<String, Object> conditionMap);

    /**
     * method name : getMeteringDataDetailHourlyChartData<b/>
     * method Desc : Metering Data 맥스가젯 상세화면에서 시간별 Chart 데이터를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="MeteringDataDetailHourlyChartData")
    public Map<String, Object> getMeteringDataDetailHourlyChartData(Map<String, Object> conditionMap);
    
    public List<Map<String, Object>> getMdsIdFromContract(Map<String, Object> conditionMap);
}