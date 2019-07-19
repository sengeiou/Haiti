package com.aimir.dao.system;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.constants.CommonConstants.PeakType;
import com.aimir.dao.GenericDao;
import com.aimir.model.system.TOURate;
import com.aimir.util.Condition;

public interface TOURateDao extends GenericDao<TOURate, Integer> {

	/**
	 * method name : getPeakTimeZone
	 * method Desc : 계약종별과 계절, 월에 따른 TOU Rate (Peak time) 정보 취득

	 * @param condition
	 * {@code}
	 * 		int tariffType  = Integer.parseInt(String.valueOf(condition.get("tariffType")));
	 * 		String dateType = (String)condition.get("dateType");
	 * 		int season   = Integer.parseInt(String.valueOf(condition.get("season")));
	 * 		String month    = condition.get("startDate").toString().substring(4, 6);
	 * 
	 * @return Map { TOURate.tariffType.id as tariffType, 
	 * 				 TOURate.peakType as peakType,   
	 * 				 TOURate.startTime as startTime,  
	 * 				 TOURate.endTime as endTime }
	 */
	public Map<Integer, Object> getPeakTimeZone(Map<String, Object> condition);
	
	/**
	 * method name : getTOURateByListCondition
	 * method Desc : 조회조건 Condition 객체에 담긴 조회조건으로 TOURate 목록을 리턴
	 * 
	 * @param set
	 * @return List of TOURate @see com.aimir.model.system.TOURate
	 */
	public List<TOURate> getTOURateByListCondition(Set<Condition> set);
	
	/**
	 * method name : getTOURateWithSeasonsBySyearNull
	 * method Desc : TOU Rate의 syear 정보가 없는 TOU RAte 정보중에서 조회조건을 만족하는 목록을 Season 정보와 같이 추출한다.
	 * 				이는 일반적인 년도별 계절에 상관없이 일반적인 계절 정보를 조건으로 하여 추출하기 위함
	 * 
	 * @param condition
	 * {@code}
	 * 		String stdDate	= (String)condition.get("stdDate"); //start date
	 * 		String endDate	= (String)condition.get("endDate"); //end date
	 * 		int tariffTypeId  = (Integer)condition.get("tariffTypeId");
	 * 
	 * @return List Of Object {AimirSeason.SMONTH,  
	 * 						   AimirSeason.EMONTH, 
	 * 						   TOURate.LOCAL_NAME, 
	 * 						   TOURate.START_TIME, 
	 * 						   TOURate.END_TIME}
	 */
	public List<Object> getTOURateWithSeasonsBySyearNull(Map<String,Object> condition);
	
	/**
	 * method name : getTOURateWithSeasonsBySyear
	 * method Desc : TOU Rate의 syear 정보가 있는 TOU RAte 정보중에서 조회조건을 만족하는 목록을 Season 정보와 같이 추출한다.
	 * 
	 * @param condition
	 * {@code}
	 * 		String year	  	= (String)condition.get("year");
	 * 		String stdDate	= (String)condition.get("stdDate");
	 * 		String endDate	= (String)condition.get("endDate");
	 * 		int tariffTypeId  = (Integer)condition.get("tariffTypeId");
	 * 
	 * @return List Of Object { AimirSeason.SYEAR,
	 * 							AimirSeason.SMONTH,
	 * 							AimirSeason.SDAY,  
	 * 							AimirSeason.EYEAR,
	 * 							AimirSeason.EMONTH,
	 * 							AimirSeason.EDAY, 
	 * 							TOURate.LOCAL_NAME, 
	 * 							TOURate.START_TIME, 
	 * 							TOURate.END_TIME}
	 */
	public List<Object> getTOURateWithSeasonsBySyear(Map<String,Object> condition);
	
	/**
	 * method name : getTOURate
	 * method Desc : 계약종별, Season, PeakType 조건에 해당하는 TOURate 정보 리턴
	 * 
	 * @param tariffTypeId TOURate.tariffType.id
	 * @param seasonId TOURate.season.id
	 * @param peakType TOURate.peakType
	 * @return  @see com.aimir.model.system.TOURate
	 */
	public TOURate getTOURate(Integer tariffTypeId, Integer seasonId, PeakType peakType) ;
	
	/**
	 *  method name : touDeleteByCondition
	 * @return
	 */
	public int touDeleteByCondition(Map<String, Object> condition);

}