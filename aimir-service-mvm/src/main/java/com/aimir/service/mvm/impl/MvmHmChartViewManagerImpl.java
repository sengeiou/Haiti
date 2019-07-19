package com.aimir.service.mvm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.dao.mvm.DayHMDao;
import com.aimir.dao.mvm.LpHMDao;
import com.aimir.dao.mvm.MonthHMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.model.mvm.DayHM;
import com.aimir.model.mvm.LpHM;
import com.aimir.model.mvm.MonthHM;
import com.aimir.model.mvm.Season;
import com.aimir.util.Condition;
import com.aimir.util.SearchCalendarUtil;
import com.aimir.util.Condition.Restriction;

@Service(value = "MvmHmChartViewManagerImpl")
public class MvmHmChartViewManagerImpl {
	@Autowired
	LpHMDao lpHMDao;

	@Autowired
	DayHMDao dayHMDao;

	@Autowired
	MonthHMDao monthHMDao;

	@Autowired
	SeasonDao seasonDao;
	

	/**
	 * @Method Name : getHMSearchDataHour
	 * @Date        : 2010. 4. 15.
	 * @Method 설명    : 시간별 차트 조회
	 * @param set
	 * @param custList
	 * @return
	 */
	public HashMap<String, Object> getHMSearchDataHour(Set<Condition> set, Integer[] custList) {
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		
		Double[] avgValue = new Double[custList.length];
		Double[] maxValue = new Double[custList.length];
		Double[] minValue = new Double[custList.length];
		Double[] sumValue = new Double[custList.length];
		
		if(custList.length >0 && custList != null) {
			for(int idx=0;idx < custList.length;idx++) {
			
				Condition cdt = new Condition("contract.id", new Object[] { custList[idx] }, null,Restriction.EQ);// 
				set.add(cdt);
				
				avgValue[idx] = (Double) lpHMDao.getLpHMsMaxMinSumAvg(set, "avg").get(0);
				maxValue[idx] = (Double) lpHMDao.getLpHMsMaxMinSumAvg(set, "max").get(0);
				minValue[idx] = (Double) lpHMDao.getLpHMsMaxMinSumAvg(set, "min").get(0);
				sumValue[idx] = (Double) lpHMDao.getLpHMsMaxMinSumAvg(set, "sum").get(0);
				
				set.remove(cdt);
			}
		
			Condition cdt = new Condition("contract.id", custList, null,Restriction.IN);//
			set.add(cdt);
			
			List<LpHM> dataList = lpHMDao.getLpHMsByListCondition(set);	
			
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
	 * @Method Name : getHMSearchDataDay
	 * @Date        : 2010. 4. 15.
	 * @Method 설명    : 일자/기간별차트 조회
	 * @param set	 : 조회조건
	 * @param custList : 고객정보
	 * @return
	 */
	public HashMap<String, Object>  getHMSearchDataDay(Set<Condition> set, Integer[] custList) {
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		
		Double[] avgValue = new Double[custList.length];
		Double[] maxValue = new Double[custList.length];
		Double[] minValue = new Double[custList.length];
		Double[] sumValue = new Double[custList.length];
		
		if(custList.length >0 && custList != null) {
			for(int idx=0;idx < custList.length;idx++) {
			
			Condition cdt = new Condition("contract.id", new Object[] { custList[idx] }, null,Restriction.EQ);// 
			set.add(cdt);
			
			avgValue[idx] = (Double) dayHMDao.getDayHMsMaxMinAvgSum(set, "avg").get(0);
			maxValue[idx] = (Double) dayHMDao.getDayHMsMaxMinAvgSum(set, "max").get(0);
			minValue[idx] = (Double) dayHMDao.getDayHMsMaxMinAvgSum(set, "min").get(0);
			sumValue[idx] = (Double) dayHMDao.getDayHMsMaxMinAvgSum(set, "sum").get(0);
			
			set.remove(cdt);
		}
		
		Condition cdt = new Condition("contract.id", custList, null,Restriction.IN);//
		set.add(cdt);
		List<DayHM> dataList = dayHMDao.getDayHMsByListCondition(set);
		
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
	 * @Method Name : getHMSearchDataMonth
	 * @Date        : 2010. 4. 15.
	 * @Method 설명    : 월별조회
	 * @param set
	 * @param custList
	 * @return
	 */
	public HashMap<String, Object>  getHMSearchDataMonth(Set<Condition> set, Integer[] custList) {
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		
		Double[] avgValue = new Double[custList.length];
		Double[] maxValue = new Double[custList.length];
		Double[] minValue = new Double[custList.length];
		Double[] sumValue = new Double[custList.length];
		
		if(custList.length >0 && custList != null) {
			for(int idx=0;idx < custList.length;idx++) {
			
			Condition cdt = new Condition("contract.id", new Object[] { custList[idx] }, null,Restriction.EQ);// 
			set.add(cdt);
			
			avgValue[idx] = (Double) monthHMDao.getMonthHMsMaxMinAvgSum(set, "avg").get(0);
			maxValue[idx] = (Double) monthHMDao.getMonthHMsMaxMinAvgSum(set, "max").get(0);
			minValue[idx] = (Double) monthHMDao.getMonthHMsMaxMinAvgSum(set, "min").get(0);
			sumValue[idx] = (Double) monthHMDao.getMonthHMsMaxMinAvgSum(set, "sum").get(0);
			
			set.remove(cdt);
		}
		
		Condition cdt = new Condition("contract.id", custList, null,Restriction.IN);//
		set.add(cdt);
		List<MonthHM> dataList = monthHMDao.getMonthHMsByListCondition(set);
		
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
	 * @Method Name : getHMSearchDataDayWeek
	 * @Date        : 2010. 4. 15.
	 * @Method 설명    : 요일별조회
	 * @param set
	 * @param custList
	 * @return
	 */
	public HashMap<String, Object>  getHMSearchDataDayWeek(Set<Condition> set, Integer[] custList) {
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		
		Double[] avgValue = new Double[custList.length];
		Double[] maxValue = new Double[custList.length];
		Double[] minValue = new Double[custList.length];
		Double[] sumValue = new Double[custList.length];
		
		if(custList.length >0 && custList != null) {
			for(int idx=0;idx < custList.length;idx++) {
			
			Condition cdt = new Condition("contract.id", new Object[] { custList[idx] }, null,Restriction.EQ);// 
			set.add(cdt);
			
			avgValue[idx] =  (Double) dayHMDao.getDayHMsMaxMinAvgSum(set, "avg").get(0);
			maxValue[idx] = (Double) dayHMDao.getDayHMsMaxMinAvgSum(set, "max").get(0);
			minValue[idx] = (Double) dayHMDao.getDayHMsMaxMinAvgSum(set, "min").get(0);
			sumValue[idx] = (Double) dayHMDao.getDayHMsMaxMinAvgSum(set, "sum").get(0);
			
			set.remove(cdt);
		}
		
		Condition cdt = new Condition("contract.id", custList, null,Restriction.IN);//
		set.add(cdt);
		List<DayHM> dataList = dayHMDao.getDayHMsByListCondition(set);
		
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
	 * @Method Name : getHMSearchDataSeason
	 * @Date        : 2010. 4. 14.
	 * @Method 설명    : 계절별조회
	 * @param set
	 * @param custList 
	 * @param year
	 * @return : 계절별데이터, 계절명
	 */
	public HashMap<String, Object>  getHMSearchDataSeason(Set<Condition> set,Integer[] custList, String year) {
		
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
			
				List<Object> dataList  = dayHMDao.getDayHMsSumList(set);
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
	 * @Method Name : getHMSearchDataWeek
	 * @Date        : 2010. 4. 15.
	 * @Method 설명    : 주별조회
	 * @param set
	 * @param custList
	 * @param yyMM
	 * @return
	 */
	public  HashMap<String, Object> getHMSearchDataWeek(Set<Condition> set, Integer[] custList, String yyMM) {
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
			
				List<Object> dataList  = dayHMDao.getDayHMsSumList(set);
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
    		Iterator it = seasonList.iterator();
    		
    		while (it.hasNext()) {
    			Season retSeason = (Season) it.next();
    			String[] searchDate = new String[2];
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
