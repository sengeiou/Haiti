package com.aimir.service.mvm.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.dao.mvm.SeasonDao;
import com.aimir.model.mvm.Season;
import com.aimir.service.mvm.SeasonManager;
import com.aimir.service.mvm.bean.SeasonData;
import com.aimir.util.CalendarUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;


@Service(value = "seasonManager")
public class SeasonManagerImpl implements SeasonManager{
	@Autowired
    SeasonDao seasonDao; 

    public Map<String, SeasonData> getSeasonData(String year) {
        String basicDate = null;
        try {
            basicDate = TimeUtil.getCurrentDay();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getSeasonData(year, basicDate);
    }

	public Map<String,SeasonData> getSeasonData(String year, String basicDate){

		// 특정년도 계절 데이터를 조회한다.
		List<Season> seasonList = null;
		if(year!=null&&!"".equals(year.trim())){
			seasonList = seasonDao.getSeasonsBySyear(year);
		}
		// 특정년도 계절데이터가 없거을경우 공용계절데이터(시작년도가 null)를 조회한다.
		if(seasonList==null||seasonList.size()<1){
			seasonList = seasonDao.getSeasonsBySyearIsNull();
		}

		Map<String,SeasonData> seasonDataMap = new HashMap<String,SeasonData>();

		if(seasonList==null||seasonList.size()<1)return seasonDataMap;

		int i=1;
		for(Season season:seasonList){
			String startMonth="";
			String startDay="";
			String endMonth="";
			String endDay="";
			SeasonData seasonData = new SeasonData();

			seasonData.setId(Integer.toString(i++));
			seasonData.setName(season.getName());

			// 시작일,종료일계산
			// 시작일,종료일데이터가있을경우 사용,없으면 해당월로 계산
			startMonth = season.getSmonth();
			if(Integer.parseInt(startMonth)<10){
				startMonth = "0"+Integer.toString(Integer.parseInt(startMonth));
			}

			startDay = season.getSday();
			if(startDay==null||"".equals(startDay.trim())){
				startDay = "01";
			}

			String endYear = season.getEyear();
			if(endYear == null){
			    // Winter 일 경우만 종료년도를 증가시킴.
				endYear = (season.getId().equals(new Integer(4))) ? Integer.toString(Integer.parseInt(year)+1) : Integer.toString(Integer.parseInt(year));
			}


			endMonth = season.getEmonth();
			if(Integer.parseInt(endMonth)<10){
				endMonth = "0"+Integer.toString(Integer.parseInt(endMonth));
			}

			endDay = season.getEday();
			if(endDay==null||"".equals(endDay.trim())){
				int lastDate = 31;
				int iyear = Integer.parseInt(year);
			    int imonth = Integer.parseInt(endMonth)-1;
			    int iday = Integer.parseInt("1");
			    Calendar cal = Calendar.getInstance();
			    cal.set(iyear, imonth, iday);

			    lastDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			    endDay = Integer.toString(lastDate);
			}

			if(Integer.parseInt(endDay)<10){
				endDay = "0"+Integer.toString(Integer.parseInt(endDay));
			}

			seasonData.setStartDate(year+startMonth+startDay);
			seasonData.setEndDate(endYear+endMonth+endDay);

			// 기준일자로 체크하기
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(new Date());
//			
//		    int iyear = cal.get(Calendar.YEAR);
//		    int imonth = cal.get(Calendar.MONTH)+1;
//		    int iday = cal.get(Calendar.DAY_OF_MONTH);
//		    
//		    StringBuffer sb = new StringBuffer();
//		    sb.append(Integer.toString(iyear));
//		    sb.append(imonth<10?"0"+Integer.toString(imonth):Integer.toString(imonth));
//		    sb.append(iday<10?"0"+Integer.toString(iday):Integer.toString(iday));
//		    
//		    Integer currDate = Integer.parseInt(sb.toString());
			Integer currDate = Integer.parseInt(basicDate);

		    //계절의 시작일이 오늘날짜와 같거나 이전일경우에만 조회
			if(Integer.parseInt(seasonData.getStartDate()) <= currDate){

				//계절의 종료일이 오늘날짜보다 클경우 종료일은 오늘날짜로 설정
				if(Integer.parseInt(seasonData.getEndDate()) > currDate){
					seasonData.setEndDate(Integer.toString(currDate));
				}
				seasonDataMap.put(season.getName(), seasonData);
			}
		}

		return seasonDataMap;
	}

    /**
     * method name : getSeasonPeriodByDate<b/>
     * method Desc :
     *
     * @param strDate
     * @return
     */
    public Map<String, String> getSeasonPeriodByDate(String strDate) {
        if (StringUtil.nullToBlank(strDate).isEmpty()) {
            return null;
        }
        // 특정년도 계절 데이터를 조회한다.
        List<Season> seasonList = null;
        String year = strDate.substring(0, 4);
        seasonList = seasonDao.getSeasonsBySyear(year);
        // 특정년도 계절데이터가 없을 경우 공용계절데이터(시작년도가 null)를 조회한다.
        if (seasonList == null || seasonList.size() < 1) {
            seasonList = seasonDao.getSeasonsBySyearIsNull();
        }

        Map<String, String> result = new HashMap<String, String>();

        if (seasonList == null || seasonList.size() < 1) return result;

        String startMonth = null;
        String startDay = null;
        String endMonth = null;
        String endDay = null;
        int lastDay = 0;
        int iyear = 0;
        int imonth = 0;
        int iday = 0;
        Calendar cal = null;

        for (Season season : seasonList) {
            startMonth = null;
            startDay = null;
            endMonth = null;
            endDay = null;

            // 시작일,종료일계산
            // 시작일,종료일데이터가있을경우 사용,없으면 해당월로 계산
            startMonth = StringUtil.frontAppendNStr('0', season.getSmonth(), 2);
            startDay = (StringUtil.nullToBlank(season.getSday()).isEmpty()) ? "01" : StringUtil.frontAppendNStr('0', season.getSday(), 2);

            String endYear = season.getEyear();
            if (endYear == null) {
                endYear = year;
            }

            endMonth = StringUtil.frontAppendNStr('0', season.getEmonth(), 2);

            if (StringUtil.nullToBlank(season.getEday()).isEmpty()) {
                iyear = Integer.parseInt(year);
                imonth = Integer.parseInt(endMonth)-1;
                iday = Integer.parseInt("1");
                cal = Calendar.getInstance();
                cal.set(iyear, imonth, iday);
                lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                endDay = StringUtil.frontAppendNStr('0', Integer.toString(lastDay), 2);
            } else {
                endDay = StringUtil.frontAppendNStr('0', season.getEday(), 2);
            }

            String startDate = year+startMonth+startDay;
            String endDate = endYear+endMonth+endDay;
            Boolean hasDay = false;

            try {
                if (startMonth.compareTo(endMonth) > 0) {   // 계절 시작월이 종료월보다 클 경우
                    String firstDate = year + "0101";
                    String lastDate = year + "1231";

                    if (TimeUtil.getDayDuration(firstDate, strDate) >= 0 && TimeUtil.getDayDuration(strDate, endDate) >= 0) {   // 전년도에 걸쳐있는 경우
                        result.put("seasonName", season.getName());
                        result.put("startDate", Integer.toString(Integer.parseInt(year)-1) + startDate.substring(4,8));
                        result.put("endDate", endDate);

                        if ((StringUtil.nullToBlank(season.getSday()).isEmpty() || season.getSday().equals("01")) && StringUtil.nullToBlank(season.getEday()).isEmpty()) {
                            hasDay = false;
                        } else {
                            hasDay = true;
                        }
                        result.put("hasDay", hasDay.toString());
                        break;
                    } else if (TimeUtil.getDayDuration(startDate, strDate) >= 0 && TimeUtil.getDayDuration(strDate, lastDate) >= 0) {   // 내년도에 걸쳐있는 경우
                        result.put("seasonName", season.getName());
                        result.put("startDate", startDate);
                        result.put("endDate", Integer.toString(Integer.parseInt(year)+1) + endDate.substring(4,8));

                        if ((StringUtil.nullToBlank(season.getSday()).isEmpty() || season.getSday().equals("01")) && StringUtil.nullToBlank(season.getEday()).isEmpty()) {
                            hasDay = false;
                        } else {
                            hasDay = true;
                        }
                        result.put("hasDay", hasDay.toString());
                        break;
                    }
                } else {
                    if (TimeUtil.getDayDuration(startDate, strDate) >= 0 && TimeUtil.getDayDuration(strDate, endDate) >= 0) {
                        result.put("seasonName", season.getName());
                        result.put("startDate", startDate);
                        result.put("endDate", endDate);

                        if ((StringUtil.nullToBlank(season.getSday()).isEmpty() || season.getSday().equals("01")) && StringUtil.nullToBlank(season.getEday()).isEmpty()) {
                            hasDay = false;
                        } else {
                            hasDay = true;
                        }
                        result.put("hasDay", hasDay.toString());
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * method name : getSeasonDataListByDates<b/>
     * method Desc :
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public Map<String, Object> getSeasonDataListByDates(String startDate, String endDate) {
//        List<Map<String, SeasonData>> seasonDataList = new ArrayList<Map<String, SeasonData>>();
        Map<String, Object> result = new HashMap<String, Object>();
        List<SeasonData> seasonDataList = new ArrayList<SeasonData>();
//        Map<String, SeasonData> seasonDataMap = new HashMap<String,SeasonData>();
        Set<String> seasonYearSet = new LinkedHashSet<String>();
        List<Season> seasonList = null;
//        String searchStartYear = startDate.substring(0, 4);
        String searchEndYear = endDate.substring(0, 4);
        String curDate = startDate;
        String curYear = null;
        boolean hasDay = false; // 계절 기간이 일단위로 구분될 경우 true

        for (int i = 0; i < 20; i++) {
            curYear = curDate.substring(0, 4);
            seasonYearSet.add(curYear);
            
            if (curYear.equals(searchEndYear)) {
                break;
            }
            curDate = CalendarUtil.getDate(curYear + "0101", Calendar.YEAR, 1);
        }

        String startMonth = null;
        String startDay = null;
        String endYear = null;
        String endMonth = null;
        String endDay = null;
        SeasonData seasonData = new SeasonData();
        int idx = 1;
        
        for (String year : seasonYearSet) {
            // 특정년도 계절 데이터를 조회한다.
            if (year != null && !year.isEmpty()) {
                seasonList = seasonDao.getSeasonsBySyear(year);
            }
            // 특정년도 계절데이터가 없을 경우 공용계절데이터(시작년도가 null)를 조회한다.
            if (seasonList == null || seasonList.size() < 1) {
                seasonList = seasonDao.getSeasonsBySyearIsNull();
            }

            if (seasonList == null || seasonList.size() < 1) {
//                return seasonDataMap;
                continue;
            }

//            seasonDataMap = new HashMap<String,SeasonData>();
            idx = 1;

            for (Season season : seasonList) {
                startMonth = null;
                startDay = null;
                endYear = null;
                endMonth = null;
                endDay = null;
                seasonData = new SeasonData();

                seasonData.setId(Integer.toString(idx++));
                seasonData.setName(season.getName());

                // 시작일,종료일계산
                // 시작일,종료일데이터가 있을경우 사용,없으면 해당월로 계산
                startMonth = StringUtil.frontAppendNStr('0', season.getSmonth(), 2);
//                if (Integer.parseInt(startMonth) < 10) {
//                    startMonth = "0" + Integer.toString(Integer.parseInt(startMonth));
//                }

//                startDay = season.getSday();
                if (season.getSday() == null || season.getSday().isEmpty()) {
                    startDay = "01";
                } else {
                    startDay = StringUtil.frontAppendNStr('0', season.getSday(), 2);
                    if (!startDay.equals("01")) {
                        hasDay = true;
                    }
                }

                if (season.getEyear() == null) {
                    // Winter 일 경우만 종료년도를 증가시킴.
                    endYear = (season.getId().equals(new Integer(4))) ? Integer.toString(Integer.parseInt(year)+1) : Integer.toString(Integer.parseInt(year));
                } else {
                    endYear = season.getEyear();
                }

                endMonth = StringUtil.frontAppendNStr('0', season.getEmonth(), 2);

                int lastDate = 31;
                int iyear = Integer.parseInt(year);
                int imonth = Integer.parseInt(endMonth)-1;
                int iday = Integer.parseInt("1");
                Calendar cal = Calendar.getInstance();
                cal.set(iyear, imonth, iday);
                lastDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

                if (season.getEday() == null || season.getEday().isEmpty()) {
                    endDay = StringUtil.frontAppendNStr('0', Integer.toString(lastDate), 2);
                } else {
                    endDay = StringUtil.frontAppendNStr('0', season.getEday(), 2);

                    if (!endDay.equals(StringUtil.frontAppendNStr('0', Integer.toString(lastDate), 2))) {
                        hasDay = true;
                    }
                }

                seasonData.setStartDate(year+startMonth+startDay);
                seasonData.setEndDate(endYear+endMonth+endDay);

//                if (searchStartYear.equals(searchEndYear)) {    // 조회시작년도와 조회종료년도가 동일할 경우
//                    // 조회시작일자 보다 크거나 같고 조회종료일자 보다 작거나 같은 계절만 조회
////                    if (seasonData.getStartDate().compareTo(startDate) >= 0 && seasonData.getEndDate().compareTo(endDate) <= 0) {
////                    if (seasonData.getStartDate().compareTo(startDate) <= 0 && seasonData.getEndDate().compareTo(endDate) >= 0) {
//                    
//                    
//                    
//                    
//                    
//                    if ((seasonData.getStartDate().compareTo(startDate) <= 0 && seasonData.getEndDate().compareTo(startDate) >= 0) ||
//                            (seasonData.getStartDate().compareTo(endDate) <= 0 && seasonData.getEndDate().compareTo(endDate) >= 0) ||
//                            (seasonData.getStartDate().compareTo(startDate) >= 0 && seasonData.getEndDate().compareTo(endDate) <= 0)) {
//                        seasonDataList.add(seasonData);
//                    }
//                    
//                } else {
//                    if (year.equals(startDate.substring(0, 4))) {   // 조회시작일자와 동일 년도일 경우
//                        if (seasonData.getStartDate().compareTo(startDate) >= 0) {  // 조회시작일자 보다 크거나 같은 계절만 조회
////                            seasonDataMap.put(season.getName(), seasonData);
//                            seasonDataList.add(seasonData);
//                        }
//                    } else if (year.equals(endDate.substring(0, 4))) {    // 조회종료일자와 동일 년도일 경우
//                        if (seasonData.getEndDate().compareTo(endDate) <= 0) {  // 조회종료일자 보다 작거나 같은 계절만 조회
////                            seasonDataMap.put(season.getName(), seasonData);
//                            seasonDataList.add(seasonData);
//                        }
//                    } else {
////                        seasonDataMap.put(season.getName(), seasonData);
//                        seasonDataList.add(seasonData);
//                    }
//                }
                
                if ((seasonData.getStartDate().compareTo(startDate) <= 0 && seasonData.getEndDate().compareTo(startDate) >= 0) ||
                        (seasonData.getStartDate().compareTo(endDate) <= 0 && seasonData.getEndDate().compareTo(endDate) >= 0) ||
                        (seasonData.getStartDate().compareTo(startDate) >= 0 && seasonData.getEndDate().compareTo(endDate) <= 0)) {
                    seasonDataList.add(seasonData);
                }

            }
            
//            seasonDataList.add(seasonDataMap);
        }
        
        result.put("hasDay", hasDay);
        result.put("seasonDataList", seasonDataList);
        return result;
    }

    /**
     * method name : getSeasonByDate<b/>
     * method Desc : 해당 일자가 포함된 Season 객체를 가져온다.
     *
     * @param strDate
     * @return
     */
    public Season getSeasonByDate(String strDate) {
        if (StringUtil.nullToBlank(strDate).isEmpty() || strDate.length() < 8) {
            return null;
        }
        // 특정년도 계절 데이터를 조회한다.
        List<Season> seasonList = null;
        String year = strDate.substring(0, 4);
        seasonList = seasonDao.getSeasonsBySyear(year);
        // 특정년도 계절데이터가 없을 경우 공용계절데이터(시작년도가 null)를 조회한다.
        if (seasonList == null || seasonList.size() < 1) {
            seasonList = seasonDao.getSeasonsBySyearIsNull();
        }

        if (seasonList == null || seasonList.size() < 1)
            return null;

        String startMonth = null;
        String startDay = null;
        String endMonth = null;
        String endDay = null;
        int lastDay = 0;
        int iyear = 0;
        int imonth = 0;
        int iday = 0;
        Calendar cal = null;

        for (Season season : seasonList) {
            startMonth = null;
            startDay = null;
            endMonth = null;
            endDay = null;

            // 시작일,종료일계산
            // 시작일,종료일데이터가있을경우 사용,없으면 해당월로 계산
            startMonth = StringUtil.frontAppendNStr('0', season.getSmonth(), 2);
            startDay = (StringUtil.nullToBlank(season.getSday()).isEmpty()) ? "01" : StringUtil.frontAppendNStr('0',
                    season.getSday(), 2);

            String endYear = season.getEyear();
            if (endYear == null) {
                endYear = year;
            }

            endMonth = StringUtil.frontAppendNStr('0', season.getEmonth(), 2);

            if (StringUtil.nullToBlank(season.getEday()).isEmpty()) {
                iyear = Integer.parseInt(year);
                imonth = Integer.parseInt(endMonth) - 1;
                iday = Integer.parseInt("1");
                cal = Calendar.getInstance();
                cal.set(iyear, imonth, iday);
                lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                endDay = StringUtil.frontAppendNStr('0', Integer.toString(lastDay), 2);
            } else {
                endDay = StringUtil.frontAppendNStr('0', season.getEday(), 2);
            }

            String startDate = year + startMonth + startDay;
            String endDate = endYear + endMonth + endDay;

            try {
                if (startMonth.compareTo(endMonth) > 0) { // 계절 시작월이 종료월보다 클 경우
                    String firstDate = year + "0101";
                    String lastDate = year + "1231";

                    if (TimeUtil.getDayDuration(firstDate, strDate) >= 0 && TimeUtil.getDayDuration(strDate, endDate) >= 0) { // 전년도에
                        return season;
                    } else if (TimeUtil.getDayDuration(startDate, strDate) >= 0
                            && TimeUtil.getDayDuration(strDate, lastDate) >= 0) { // 내년도에 걸쳐있는 경우
                        return season;
                    }
                } else {
                    if (TimeUtil.getDayDuration(startDate, strDate) >= 0 && TimeUtil.getDayDuration(strDate, endDate) >= 0) {
                        return season;
                    }
                }
            } catch(NumberFormatException e) {
                e.printStackTrace();
            } catch(ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}