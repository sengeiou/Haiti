package com.aimir.service.mvm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.LpWMDao;
import com.aimir.dao.mvm.MonthWMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.model.mvm.DayWM;
import com.aimir.model.mvm.LpWM;
import com.aimir.model.mvm.MonthWM;
import com.aimir.model.mvm.Season;
import com.aimir.util.Condition;
import com.aimir.util.SearchCalendarUtil;
import com.aimir.util.Condition.Restriction;

@Service(value = "MvmWmChartViewManagerImpl")
public class MvmWmChartViewManagerImpl {
	@Autowired
	LpWMDao lpWMDao;

	@Autowired
	DayWMDao dayWMDao;

	@Autowired
	MonthWMDao monthWMDao;

	@Autowired
	SeasonDao seasonDao;
	

	/**
	 * @Method Name : getWMSearchDataHour
	 * @Date        : 2010. 4. 15.
	 * @Method 설명    : 시간별 차트 조회
	 * @param set
	 * @param custList
	 * @return
	 */
	public HashMap<String, Object> getWMSearchDataHour(Set<Condition> set, Integer[] custList) {
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		
		Double[] avgValue = new Double[custList.length];
		Double[] maxValue = new Double[custList.length];
		Double[] minValue = new Double[custList.length];
		Double[] sumValue = new Double[custList.length];
		
		if(custList.length >0 && custList != null) {
			for(int idx=0;idx < custList.length;idx++) {
			
				Condition cdt = new Condition("contract.id", new Object[] { custList[idx] }, null,Restriction.EQ);// 
				set.add(cdt);
				
				avgValue[idx] = (Double) lpWMDao.getLpWMsMaxMinSumAvg(set, "avg").get(0);
				maxValue[idx] = (Double) lpWMDao.getLpWMsMaxMinSumAvg(set, "max").get(0);
				minValue[idx] = (Double) lpWMDao.getLpWMsMaxMinSumAvg(set, "min").get(0);
				sumValue[idx] = (Double) lpWMDao.getLpWMsMaxMinSumAvg(set, "sum").get(0);
			
			set.remove(cdt);
			}
		
			Condition cdt = new Condition("contract.id", custList, null,Restriction.IN);//
			set.add(cdt);
			
			List<LpWM> dataList = lpWMDao.getLpWMsByListCondition(set);	
			
			resultHm.put("arrAvgValue", avgValue);
			resultHm.put("arrMaxValue", maxValue);
			resultHm.put("arrMinValue", minValue);
			resultHm.put("arrSumValue", sumValue);
			resultHm.put("arrContId", custList);
			resultHm.put("dataList", dataList);
		}
		return resultHm;

	}
	
	

	/**
	 * @Method Name : getWMSearchDataDay
	 * @Date        : 2010. 4. 15.
	 * @Method 설명    : 일자/기간별차트 조회
	 * @param set	 : 조회조건
	 * @param custList : 고객정보
	 * @return
	 */
	public HashMap<String, Object>  getWMSearchDataDay(Set<Condition> set, Integer[]  custList) {
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		
		Double[] avgValue = new Double[custList.length];
		Double[] maxValue = new Double[custList.length];
		Double[] minValue = new Double[custList.length];
		Double[] sumValue = new Double[custList.length];
		
		if(custList.length >0 && custList != null) {
			for(int idx=0;idx < custList.length;idx++) {
			
			Condition cdt = new Condition("contract.id", new Object[] { custList[idx] }, null,Restriction.EQ);// 
			set.add(cdt);
			
			avgValue[idx] = (Double) dayWMDao.getDayWMsMaxMinAvgSum(set, "avg").get(0);
			maxValue[idx] = (Double) dayWMDao.getDayWMsMaxMinAvgSum(set, "max").get(0);
			minValue[idx] = (Double) dayWMDao.getDayWMsMaxMinAvgSum(set, "min").get(0);
			sumValue[idx] = (Double) dayWMDao.getDayWMsMaxMinAvgSum(set, "sum").get(0);
			
			set.remove(cdt);
		}
		
		Condition cdt = new Condition("contract.id", custList, null,Restriction.IN);//
		set.add(cdt);
		List<DayWM> dataList = dayWMDao.getDayWMsByListCondition(set);
		
		resultHm.put("arrAvgValue", avgValue);
		resultHm.put("arrMaxValue", maxValue);
		resultHm.put("arrMinValue", minValue);
		resultHm.put("arrSumValue", sumValue);
		resultHm.put("arrContId", custList);
		resultHm.put("dataList", dataList);
		}
		return resultHm;

	}
	
	/**
	 * @Method Name : getWMSearchDataMonth
	 * @Date        : 2010. 4. 15.
	 * @Method 설명    : 월별조회
	 * @param set
	 * @param custList
	 * @return
	 */
	public HashMap<String, Object>  getWMSearchDataMonth(Set<Condition> set, Integer[]  custList) {
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		
		
		Double[] avgValue = new Double[custList.length];
		Double[] maxValue = new Double[custList.length];
		Double[] minValue = new Double[custList.length];
		Double[] sumValue = new Double[custList.length];
		
		if(custList.length >0 && custList != null) {
			for(int idx=0;idx < custList.length;idx++) {
			
			
			Condition cdt = new Condition("contract.id", new Object[] { custList[idx] }, null,Restriction.EQ);// 
			set.add(cdt);
			
			avgValue[idx] = (Double) monthWMDao.getMonthWMsMaxMinAvgSum(set, "avg").get(0);
			maxValue[idx] = (Double) monthWMDao.getMonthWMsMaxMinAvgSum(set, "max").get(0);
			minValue[idx] = (Double) monthWMDao.getMonthWMsMaxMinAvgSum(set, "min").get(0);
			sumValue[idx] = (Double) monthWMDao.getMonthWMsMaxMinAvgSum(set, "sum").get(0);
			
			set.remove(cdt);
		}
		
		Condition cdt = new Condition("contract.id", custList, null,Restriction.IN);//
		set.add(cdt);
		List<MonthWM> dataList = monthWMDao.getMonthWMsByListCondition(set);
		
		resultHm.put("arrAvgValue", avgValue);
		resultHm.put("arrMaxValue", maxValue);
		resultHm.put("arrMinValue", minValue);
		resultHm.put("arrSumValue", sumValue);
		resultHm.put("arrContId", custList);
		resultHm.put("dataList", dataList);
		}
		return resultHm;

	}
	
	
	/**
	 * @Method Name : getWMSearchDataDayWeek
	 * @Date        : 2010. 4. 15.
	 * @Method 설명    : 요일별조회
	 * @param set
	 * @param custList
	 * @return
	 */
	public HashMap<String, Object>  getWMSearchDataDayWeek(Set<Condition> set, Integer[]  custList) {
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		
		Double[] avgValue = new Double[custList.length];
		Double[] maxValue = new Double[custList.length];
		Double[] minValue = new Double[custList.length];
		Double[] sumValue = new Double[custList.length];
		
		if(custList.length >0 && custList != null) {
			for(int idx=0;idx < custList.length;idx++) {
			
			Condition cdt = new Condition("contract.id", new Object[] { custList[idx] }, null,Restriction.EQ);// 
			set.add(cdt);
			
			avgValue[idx] =  (Double) dayWMDao.getDayWMsMaxMinAvgSum(set, "avg").get(0);
			maxValue[idx] = (Double) dayWMDao.getDayWMsMaxMinAvgSum(set, "max").get(0);
			minValue[idx] = (Double) dayWMDao.getDayWMsMaxMinAvgSum(set, "min").get(0);
			sumValue[idx] = (Double) dayWMDao.getDayWMsMaxMinAvgSum(set, "sum").get(0);
			
			set.remove(cdt);
		}
		
		Condition cdt = new Condition("contract.id", custList, null,Restriction.IN);//
		set.add(cdt);
		List<DayWM> dataList = dayWMDao.getDayWMsByListCondition(set);
		
		resultHm.put("arrAvgValue", avgValue);
		resultHm.put("arrMaxValue", maxValue);
		resultHm.put("arrMinValue", minValue);
		resultHm.put("arrSumValue", sumValue);
		resultHm.put("arrContId", custList);
		resultHm.put("dataList", dataList);
		}
		return resultHm;

	}
	
	/**
	 * @Method Name : getWMSearchDataSeason
	 * @Date        : 2010. 4. 14.
	 * @Method 설명    : 계절별조회
	 * @param set
	 * @param custList 
	 * @param year
	 * @return : 계절별데이터, 계절명
	 */
	public HashMap<String, Object>  getWMSearchDataSeason(Set<Condition> set,  Integer[]   custList, String year) {
		
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		
		Condition cdt = new Condition("contract.id", custList, null,Restriction.IN);//
		set.add(cdt);
		
		// season별 조회조건 가져오기
		List<Season> searchDataList =  getSeasonDate(year);
		
		ArrayList<String> arrFirstName = new ArrayList<String>();
		int rowNum =searchDataList.size();
		
		for (int idx = 0; idx < searchDataList.size(); idx++) {
			
			String beginDate = searchDataList.get(idx).getSyear() + searchDataList.get(idx).getSmonth()+ searchDataList.get(idx).getSday();
			
			String endDate = searchDataList.get(idx).getEyear() + searchDataList.get(idx).getEmonth()+ searchDataList.get(idx).getEday();
			arrFirstName.add(searchDataList.get(idx).getName());
			
			if ((beginDate != null && beginDate.length() != 0) && (endDate != null && endDate.length() != 0)) {
				Condition cdt3 = new Condition("id.yyyymmdd", new Object[] { beginDate, endDate }, null, Restriction.BETWEEN);
				set.add(cdt3);
			
				List<Object> dataList  = dayWMDao.getDayWMsSumList(set);
				resultHm.put("dataList"+idx, dataList);
				
				set.remove(cdt3);
			}
	
		}
		resultHm.put("firstColNm", arrFirstName);
		resultHm.put("rowNum", rowNum);
		resultHm.put("contractId", custList);
		
		return resultHm;

	}
	
	/**
	 * @Method Name : getWMSearchDataWeek
	 * @Date        : 2010. 4. 15.
	 * @Method 설명    : 주별조회
	 * @param set
	 * @param custList
	 * @param yyMM
	 * @return
	 */
	public  HashMap<String, Object> getWMSearchDataWeek(Set<Condition> set, Integer[]  custList, String yyMM) {
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		ArrayList<String> arrFirstName = new ArrayList<String>();
		int rowNum= 0;
		
		Condition cdt = new Condition("contract.id", custList, null,Restriction.IN);//
		set.add(cdt);
		SearchCalendarUtil sCaldUtil = new SearchCalendarUtil();
		List<String> DateList = sCaldUtil.getMonthToBeginDateEndDate(yyMM);
		
		// 조회일자 가져오기
		for (rowNum = 0; rowNum < DateList.size(); rowNum++) {
			String val = DateList.get(rowNum);
			String beginDate = val.substring(0, 8);
			String endDate = val.substring(8, 16);
			
			if ((beginDate != null && beginDate.length() != 0) && (endDate != null && endDate.length() != 0)) {
				Condition cdt1 = new Condition("id.yyyymmdd", new Object[] { beginDate, endDate }, null, Restriction.BETWEEN);
				set.add(cdt1);
			
				List<Object> dataList  = dayWMDao.getDayWMsSumList(set);
				resultHm.put("dataList"+rowNum, dataList);
				
				set.remove(cdt1);
				
				arrFirstName.add((rowNum+1)+"Week");
			}
			
		}
		
		resultHm.put("firstColNm", arrFirstName);
		resultHm.put("rowNum", rowNum);
		resultHm.put("contractId", custList);

		return resultHm;
	}
	
	/*
     * Season의 계절별 시작일, 종료일 가져오기
     */
    private List<Season> getSeasonDate (String year) {
    	
    	List<Season> result = new ArrayList<Season>();
    	
    	List<Season> searchSeasonList = seasonDao.getSeasonsBySyear(year);
    	if (searchSeasonList.size() > 0 && searchSeasonList != null) {
    		result = searchSeasonList;
    	}
    	else {
    		List<Season> seasonList = seasonDao.getSeasonsBySyearIsNull();
    		Iterator<Season> it = seasonList.iterator();
    		
    		while (it.hasNext()) {
    			Season retSeason = it.next();

    			if("Spring".equals(retSeason.getName())) {
    				retSeason.setSyear(year);
    				retSeason.setSday("01");
    				retSeason.setEyear(year);
    				retSeason.setEmonth("31");
    				result.add(retSeason);
    			}
    			else if("Summer".equals(retSeason.getName())) {
    				retSeason.setSyear(year);
    				retSeason.setSday("01");
    				retSeason.setEyear(year);
    				retSeason.setEmonth("31");
    				result.add(retSeason);
    			}
    			else if("Autumn".equals(retSeason.getName())) {
    				retSeason.setSyear(year);
    				retSeason.setSday("01");
    				retSeason.setEyear(year);
    				retSeason.setEmonth("31");
    				result.add(retSeason);
    			}
    			else { 
    				retSeason.setSyear(year);
    				retSeason.setSday("01");
    				retSeason.setEyear((Integer.parseInt(year)+1)+"");
    				retSeason.setEmonth("31");
    				result.add(retSeason);
    			}
    		}
    	}
    	
    	return result;
    }
}
