package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

public interface ComparisonChartManager {
//	public List<LoadDurationChartData> getLoadDurationChartData(String[] values, String type);
//	public HashMap<String, Object> getOverlayChartData(String[] values, String type);
//	public HashMap<String, Object> getInOffTimeChartData(String[] values, String type);
//	public HashMap<String, Object> getContractNumber(String[] values);

    /**
     * Method Name : getOverlayChartDailyData
     * Date : 2011. 7. 4
     * Method 설명 : Metering Data - Chart View - Overlay Chart (Daily)
     * 
     * @param condition
     * @return
     */
    public Map<String, Object> getOverlayChartDailyData(Map<String, Object> condition);

    /**
     * Method Name : getOverlayChartDailyWeekData
     * Date : 2011. 7. 6
     * Method 설명 : Metering Data - Chart View - Overlay Chart (DailyWeek)
     * 
     * @param condition
     * @return
     */
    public Map<String, Object> getOverlayChartDailyWeekData(Map<String, Object> condition);

    /**
     * Method Name : getOverlayChartWeeklyData
     * Date : 2011. 7. 7
     * Method 설명 : Metering Data - Chart View - Overlay Chart (Weekly)
     * 
     * @param condition
     * @return
     */
    public Map<String, Object> getOverlayChartWeeklyData(Map<String, Object> condition);

    /**
     * Method Name : getOverlayChartMonthlyData
     * Date : 2011. 7. 7
     * Method 설명 : Metering Data - Chart View - Overlay Chart (Monthly)
     * 
     * @param condition
     * @return
     */
    public Map<String, Object> getOverlayChartMonthlyData(Map<String, Object> condition);

    /**
     * Locale formatting 된 00시 ~ 23시 시간형식 리스트를 조회한다.
     * 
     * @param supplierId
     * @return
     */
    public List<String> getLocaleAllHours(String supplierId);

    /**
     * Locale formatting 된 요일 전체 리스트를 조회한다.
     * 
     * @param supplierId
     * @return
     */
    public List<String> getLocaleAllWeekDays(String supplierId);

}
