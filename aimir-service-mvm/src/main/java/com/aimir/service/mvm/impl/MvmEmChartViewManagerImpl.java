package com.aimir.service.mvm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.LpEM;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.mvm.Season;
import com.aimir.util.Condition;
import com.aimir.util.SearchCalendarUtil;
import com.aimir.util.Condition.Restriction;

@Service(value = "MvmEmChartViewManagerImpl")
public class MvmEmChartViewManagerImpl {
	@Autowired
	LpEMDao lpEMDao;

	@Autowired
	DayEMDao dayEMDao;

	@Autowired
	MonthEMDao monthEMDao;

	@Autowired
	SeasonDao seasonDao;
	
    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(MvmEmChartViewManagerImpl.class);

	/**
	 * @Method Name : getEMSearchDataHour
	 * @Date        : 2010. 4. 15.
	 * @Method 설명    : 시간별 차트 조회
	 * @param set
	 * @param custList
	 * @return
	 */
    @Deprecated
	public HashMap<String, Object> getEMSearchDataHour(Set<Condition> set, Integer[] custList) {
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		
		Double[] avgValue = new Double[custList.length];
		Double[] maxValue = new Double[custList.length];
		Double[] minValue = new Double[custList.length];
		Double[] sumValue = new Double[custList.length];
		
		if(custList.length >0 && custList != null) { 
			for(int idx=0;idx < custList.length;idx++) {
				
				Condition cdt = new Condition("contract.id", new Object[] { custList[idx] }, null,Restriction.EQ);// 
				set.add(cdt);
				
				avgValue[idx] = (Double) lpEMDao.getLpEMsMaxMinSumAvg(set, "avg").get(0);
				maxValue[idx] = (Double) lpEMDao.getLpEMsMaxMinSumAvg(set, "max").get(0);
				minValue[idx] = (Double) lpEMDao.getLpEMsMaxMinSumAvg(set, "min").get(0);
				sumValue[idx] = (Double) lpEMDao.getLpEMsMaxMinSumAvg(set, "sum").get(0);
				
				set.remove(cdt);
				
			}
		
			Condition cdt = new Condition("contract.id", custList, null,Restriction.IN);//
			set.add(cdt);
			
			
			
			List<LpEM> dataList = lpEMDao.getLpEMsByListCondition(set);
			
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
     * @Method Name : getEMSearchDataHour
     * @Date        : 2010. 4. 15.
     * @Method 설명    : 시간별 차트 조회
     * @param set
     * @param custList
     * @param meterList Meter No List
     * @return
     */
    public HashMap<String, Object> getEMSearchDataHourByMeter(Set<Condition> set, Integer[] custList, String[] meterList) {
        HashMap<String, Object> resultHm = new HashMap<String, Object>();
        
        Double[] avgValue = new Double[meterList.length];
        Double[] maxValue = new Double[meterList.length];
        Double[] minValue = new Double[meterList.length];
        Double[] sumValue = new Double[meterList.length];
        
        if (meterList.length > 0 && meterList != null) {
            int len = meterList.length;
            for (int idx = 0; idx < len; idx++) {
                
                Condition cdt = new Condition("id.mdevId", new Object[] { meterList[idx] }, null,Restriction.EQ);// 
                set.add(cdt);
                
                avgValue[idx] = (Double) lpEMDao.getLpEMsMaxMinSumAvg(set, "avg").get(0);
                maxValue[idx] = (Double) lpEMDao.getLpEMsMaxMinSumAvg(set, "max").get(0);
                minValue[idx] = (Double) lpEMDao.getLpEMsMaxMinSumAvg(set, "min").get(0);
                sumValue[idx] = (Double) lpEMDao.getLpEMsMaxMinSumAvg(set, "sum").get(0);
                
                set.remove(cdt);
                
            }

            Condition cdt = new Condition("id.mdevId", meterList, null, Restriction.IN);
            set.add(cdt);

            List<LpEM> dataList = lpEMDao.getLpEMsByListCondition(set);

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
	 * @Method Name : getEMSearchDataDay
	 * @Date        : 2010. 4. 15.
	 * @Method 설명    : 일자/기간별차트 조회
	 * @param set	 : 조회조건
	 * @param custList : 고객정보
	 * @return
	 */
	public HashMap<String, Object>  getEMSearchDataDay(Set<Condition> set, Integer[] custList) {
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		
		Double[] avgValue = new Double[custList.length];
		Double[] maxValue = new Double[custList.length];
		Double[] minValue = new Double[custList.length];
		Double[] sumValue = new Double[custList.length];
		
		if(custList.length >0 && custList != null) { 
			for(int idx=0;idx < custList.length;idx++) {
			
				Condition cdt = new Condition("contract.id", new Object[] { custList[idx] }, null,Restriction.EQ);// 
				set.add(cdt);
				
				avgValue[idx] = (Double) dayEMDao.getDayEMsMaxMinAvgSum(set, "avg").get(0);
				maxValue[idx] = (Double) dayEMDao.getDayEMsMaxMinAvgSum(set, "max").get(0);
				minValue[idx] = (Double) dayEMDao.getDayEMsMaxMinAvgSum(set, "min").get(0);
				sumValue[idx] = (Double) dayEMDao.getDayEMsMaxMinAvgSum(set, "sum").get(0);
				
				set.remove(cdt);
			}
		
			Condition cdt = new Condition("contract.id", custList, null,Restriction.IN);//
			set.add(cdt);
			List<DayEM> dataList = dayEMDao.getDayEMsByListCondition(set);
			
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
	 * @Method Name : getEmSearchDataMonth
	 * @Date        : 2010. 4. 15.
	 * @Method 설명    : 월별조회
	 * @param set
	 * @param custList
	 * @return
	 */
	public HashMap<String, Object>  getEMSearchDataMonth(Set<Condition> set, Integer[] custList) {
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		
		Double[] avgValue = new Double[custList.length];
		Double[] maxValue = new Double[custList.length];
		Double[] minValue = new Double[custList.length];
		Double[] sumValue = new Double[custList.length];
		
		if(custList.length >0 && custList != null) { 
			for(int idx=0;idx < custList.length;idx++) {
			
			Condition cdt = new Condition("contract.id", new Object[] { custList[idx] }, null,Restriction.EQ);// 
			set.add(cdt);
			
			avgValue[idx] = (Double) monthEMDao.getMonthEMsMaxMinAvgSum(set, "avg").get(0);
			maxValue[idx] = (Double) monthEMDao.getMonthEMsMaxMinAvgSum(set, "max").get(0);
			minValue[idx] = (Double) monthEMDao.getMonthEMsMaxMinAvgSum(set, "min").get(0);
			sumValue[idx] = (Double) monthEMDao.getMonthEMsMaxMinAvgSum(set, "sum").get(0);
			
			set.remove(cdt);
			
		}
		
		Condition cdt = new Condition("contract.id", custList, null,Restriction.IN);//
		set.add(cdt);
		List<MonthEM> dataList = monthEMDao.getMonthEMsByListCondition(set);
		
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
	 * @Method Name : getEMSearchDataDayWeek
	 * @Date        : 2010. 4. 15.
	 * @Method 설명    : 요일별조회
	 * @param set
	 * @param custList
	 * @return
	 */
	public HashMap<String, Object>  getEMSearchDataDayWeek(Set<Condition> set, Integer[] custList) {
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		
		Double[] avgValue = new Double[custList.length];
		Double[] maxValue = new Double[custList.length];
		Double[] minValue = new Double[custList.length];
		Double[] sumValue = new Double[custList.length];
		
		if(custList.length >0 && custList != null) { 
			for(int idx=0;idx < custList.length;idx++) {
			
			Condition cdt = new Condition("contract.id", new Object[] { custList[idx] }, null,Restriction.EQ);// 
			set.add(cdt);
			
			avgValue[idx] =  (Double) dayEMDao.getDayEMsMaxMinAvgSum(set, "avg").get(0);
			maxValue[idx] = (Double) dayEMDao.getDayEMsMaxMinAvgSum(set, "max").get(0);
			minValue[idx] = (Double) dayEMDao.getDayEMsMaxMinAvgSum(set, "min").get(0);
			sumValue[idx] = (Double) dayEMDao.getDayEMsMaxMinAvgSum(set, "sum").get(0);
			
			set.remove(cdt);
		}
		
		Condition cdt = new Condition("contract.id", custList, null,Restriction.IN);//
		set.add(cdt);
		List<DayEM> dataList = dayEMDao.getDayEMsByListCondition(set);
		
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
	 * @Method Name : getEMSearchDataSeason
	 * @Date        : 2010. 4. 14.
	 * @Method 설명    : 계절별조회
	 * @param set
	 * @param custList 
	 * @param year
	 * @return : 계절별데이터, 계절명
	 */
	public HashMap<String, Object>  getEMSearchDataSeason(Set<Condition> set, Integer[] custList, String year) {
		
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		
		Condition cdt = new Condition("contract.id", custList, null,Restriction.IN);//
		set.add(cdt);
		Condition cdt2 = new Condition("contract.id", null, null, Restriction.ORDERBY);
		set.add(cdt2);
		
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
			
				List<Object> dataList  = dayEMDao.getDayEMsSumList(set);
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
	 * @Method Name : getEMSearchDataWeek
	 * @Date        : 2010. 4. 15.
	 * @Method 설명    : 주별조회
	 * @param set
	 * @param custList
	 * @param yyMM
	 * @return
	 */
	public  HashMap<String, Object> getEMSearchDataWeek(Set<Condition> set, Integer[] custList, String yyMM) {
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
			
				List<Object> dataList  = dayEMDao.getDayEMsSumList(set);
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
    				retSeason.setEday("31");
    				result.add(retSeason);
    			}
    			else if("Summer".equals(retSeason.getName())) {
    				retSeason.setSyear(year);
    				retSeason.setSday("01");
    				retSeason.setEyear(year);
    				retSeason.setEday("31");
    				result.add(retSeason);
    			}
    			else if("Autumn".equals(retSeason.getName())) {
    				retSeason.setSyear(year);
    				retSeason.setSday("01");
    				retSeason.setEyear(year);
    				retSeason.setEday("31");
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
