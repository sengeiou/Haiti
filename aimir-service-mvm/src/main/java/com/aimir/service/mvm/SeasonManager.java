package com.aimir.service.mvm;

import java.util.Map;

import com.aimir.model.mvm.Season;
import com.aimir.service.mvm.bean.SeasonData;

public interface SeasonManager {
	/**
	 * 입력받은 년도의 계절정보(시작일자,종료일자포함)를 조회한다.
	 * 년도가 입력되지 않았을경우 공통계절데이터(시작년도가 존재하지않는 계절데이터)를 조회한다.
	 * 현재일자를 기준으로 계산한다.
	 * 
	 * @param year
	 * @return
	 */
	public Map<String,SeasonData>  getSeasonData(String year);

    /**
     * 입력받은 년도의 계절정보(시작일자,종료일자포함)를 조회한다.
     * 년도가 입력되지 않았을경우 공통계절데이터(시작년도가 존재하지않는 계절데이터)를 조회한다.
     * parameter 로 받은 basicDate 를 기준으로 계산한다.
     * 
     * @param year
     * @param basicDate 기준일자
     * @return
     */
    public Map<String,SeasonData>  getSeasonData(String year, String basicDate);

    /**
     * method name : getSeasonPeriodByDate<b/>
     * method Desc : 입력받은 일자의 계절이름, 계절기간(시작일자,종료일자)을 조회한다.
     *
     * @param strDate
     * <ul>
     * <li> strDate : String - 조회일자 (yyyyMMdd)
     * </ul>
     * 
     * @return List of Map {
     *                      seasonName : 계절이름
     *                      startDate : String - 계절시작일자 (yyyyMMdd)
     *                      endDate : String - 계절종료일자 (yyyyMMdd)
     *                     }
     */
    public Map<String, String> getSeasonPeriodByDate(String strDate);

    /**
     * method name : getSeasonDataListByDates<b/>
     * method Desc :
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public Map<String, Object> getSeasonDataListByDates(String startDate, String endDate);

    /**
     * method name : getSeasonByDate<b/>
     * method Desc : 해당 일자가 포함된 Season 객체를 가져온다.
     *
     * @param strDate
     * @return
     */
    public Season getSeasonByDate(String strDate);
}