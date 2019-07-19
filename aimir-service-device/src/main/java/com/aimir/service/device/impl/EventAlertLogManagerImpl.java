package com.aimir.service.device.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.dao.device.EventAlertLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.ProfileDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.EventAlertLogVO;
import com.aimir.model.system.Profile;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.EventAlertLogManager;
import com.aimir.util.CommonUtils2;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;



@Service(value="eventAlertLogManager")
public class EventAlertLogManagerImpl implements EventAlertLogManager {

    private static Log logger = LogFactory.getLog(EventAlertLogManagerImpl.class);
    @Autowired
    EventAlertLogDao dao;

    @Autowired
    CodeDao codeDao;

    @Autowired
    ProfileDao profileDao;

    @Autowired
    LocationDao locationDao;

    @Autowired
    SupplierDao supplierDao;
    
    /**
	 * @desc extjs 
	 * @param conditionMap
	 * @return
	 */
	public static Map<String, String> getFirstPageForExtjsGrid(Map<String, String> conditionMap)
	{
		// 페이징 처리를 위한 부분 추가.extjs 는 0부터 시작
		int temppage = Integer.parseInt(conditionMap.get("page"));

		temppage = temppage - 1;

		conditionMap.put("page", Integer.toString(temppage));
		
		return conditionMap;
	}
	
	public static int makeIdxPerPage(String curPage, String pageSize, int idx1)
	{
		
		return (Integer.parseInt(curPage) * Integer.parseInt(pageSize)) + idx1;
		 
	}
	
	
	
	

	public void addEventAlertLog(EventAlertLog eventAlertLog) {
		dao.add(eventAlertLog);
	}

	/*
	 * 최대 5개 타입별 로그 반환
	 * @see com.aimir.service.device.EventAlertLogManager#getEventAlertLogByActivatorTypeForMini(java.lang.Integer)
	 */
	public List<Map<String, Object>> getEventAlertLogByActivatorTypeForMini(Map<String, Object> conditionMap) {
	    List<Map<String, Object>> result = dao.getEventAlertLogByActivatorType(conditionMap);
	    List<Map<String, Object>> eventAlertLogVOs = new ArrayList<Map<String, Object>>();

	    if (result.size() > 5) {
	        for (int i = 0; i < 5; i++) {
	            eventAlertLogVOs.add(result.get(i));
	        }
	    } else {
	        eventAlertLogVOs = result;
	    }

	    return eventAlertLogVOs;
	}

	/*
	 * 최대 5개 메세지별 로그 반환
	 * @see com.aimir.service.device.EventAlertLogManager#getEventAlertLogByMessageForMini(java.lang.Integer)
	 */
	public List<Map<String, Object>> getEventAlertLogByMessageForMini(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = dao.getEventAlertLogByMessage(conditionMap);
        List<Map<String, Object>> eventAlertLogVOs = new ArrayList<Map<String, Object>>();

        if (result.size() > 5) {
            for (int i = 0; i < 5; i++) {
                eventAlertLogVOs.add(result.get(i));
            }
        } else {
            eventAlertLogVOs = result;
        }

        return eventAlertLogVOs;
	}

	/*
	 * 타입별 로그 반환
	 * @see com.aimir.service.device.EventAlertLogManager#getEventAlertLogByActivatorType(java.lang.Integer)
	 */
	//public List<EventAlertLogSummaryVO> getEventAlertLogByActivatorType(Integer supplierId) {
	public List<Map<String, Object>> getEventAlertLogByActivatorType(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer locationId = (Integer)conditionMap.get("locationId");

        List<Integer> locationIdList = null;
        if (locationId != null) {
            locationIdList = locationDao.getLeafLocationId(locationId, supplierId);
            conditionMap.put("locationIdList", locationIdList);
        }

        List<Map<String, Object>> result = dao.getEventAlertLogByActivatorType(conditionMap);
        return result;
	}
	/*
	 * 메세지별 로그 반환
	 * @see com.aimir.service.device.EventAlertLogManager#getEventAlertLogByMessage(java.lang.Integer)
	 */
	public List<Map<String, Object>> getEventAlertLogByMessage(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer locationId = (Integer)conditionMap.get("locationId");

        List<Integer> locationIdList = null;
        if (locationId != null) {
            locationIdList = locationDao.getLeafLocationId(locationId, supplierId);
            conditionMap.put("locationIdList", locationIdList);
        }

        List<Map<String, Object>> result = dao.getEventAlertLogByMessage(conditionMap);
        return result;
	}
	
	
	/*
	 * 실시간 이력 반환 (맥스)
	 * @see com.aimir.service.device.EventAlertLogManager#getEventAlertLogRealTimeForMax(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	public List<EventAlertLogVO> getEventAlertLogRealTimeForMax(Integer userId, Integer supplierId, Integer first, Integer max) {
		Set<Condition> set = getConditionByProfile(userId, supplierId);
		
		Condition condition1 = new Condition("" ,new Object[]{first}, null, Restriction.FIRST);
		Condition condition2 = new Condition("" ,new Object[]{max}, null, Restriction.MAX);
		
		set.add(condition1);
		set.add(condition2);
		
		return dao.getEventAlertLogRealTime(set);
	}
	
	
	
	/*
	 * 최근 5개의 실시간 이력 반환 (미니)
	 * @see com.aimir.service.device.EventAlertLogManager#getEventAlertLogRealTimeForMini(java.lang.Integer, java.lang.Integer)
	 */
	public List<EventAlertLogVO> getEventAlertLogRealTimeForMini(Integer userId, Integer supplierId, String searchStartDate, String searchEndDate) {
		
		
		return dao.getEventAlertLogRealTimeForMini(getConditionByProfile(userId, supplierId), searchStartDate, searchEndDate);
		
	}
	/*
	 * 사용자의 설정에서 실시간 이력 조건 가져오기
	 */
	private Set<Condition> getConditionByProfile(Integer userId, Integer supplierId) {

		Set<Condition> set = new HashSet<Condition>();		
		Condition condition1 = new Condition("supplier.id",new Object[]{supplierId},null,Restriction.EQ);
		set.add(condition1);
		Profile profile = null;
	    List<Profile> list = profileDao.getProfileByUser(userId);
	    if(list != null && list.size() > 0){
	    	profile = list.get(0);	
	    	
			if (profile.getEventAlertType() != null) {
				Condition condition2 = new Condition("eventAlertType",new Object[]{profile.getEventAlertType()},null,Restriction.EQ);
				set.add(condition2);
			}
			
			if (profile.getSeverity() != null) {
				Condition condition3 = new Condition("severity",new Object[]{profile.getSeverity()},null,Restriction.EQ);
				set.add(condition3);
			}
			
			if (profile.getEventAlert() != null) {
				Condition condition4 = new Condition("eventAlert.id",new Object[]{profile.getEventAlert().getId()},null,Restriction.EQ);
				set.add(condition4);
			}
			
			if (profile.getStatus() != null) {
				Condition condition5 = new Condition("status",new Object[]{profile.getStatus()},null,Restriction.EQ);
				set.add(condition5);
			}
			
			if (profile.getActivatorType() != null) {
				Condition condition6 = new Condition("activatorType",new Object[]{profile.getActivatorType().name()},null,Restriction.EQ);
				set.add(condition6);
			}
			
			if (profile.getActivatorId() != null) {
				Condition condition6 = new Condition("activatorId",new Object[]{profile.getActivatorId()},null,Restriction.EQ);
				set.add(condition6);
			}
			
			if (profile.getLocation() != null) {
				Condition condition6 = new Condition("location.id",new Object[]{profile.getLocation().getId()},null,Restriction.EQ);
				set.add(condition6);
			}
	    }

		return set;
	}
	
	
	
	
	/*
	 * 이력 개수 가져오기
	 * @see com.aimir.service.device.EventAlertLogManager#getEventAlertLogHistoryTotal(java.lang.String[])
	 */
	public Map<String, Object> getEventAlertLogHistoryTotal(String[] values) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total", "0");
		Set<Condition> set = new HashSet<Condition>();
		
		String locationId = values[1].substring(values[1].indexOf(":") + 1).trim();
		
		List<Integer> locations = null;
		if(locationId.length() > 0) {
			locations = locationDao.getLeafLocationId(Integer.parseInt(locationId), Integer.parseInt(values[0].substring(values[0].indexOf(":") + 1).trim()));
		}
		
		for (String value : values) {
			if (value != null) {
				Condition con = getSearchLogCondition(value);
				
				if (con != null) {
					if (con.getValue() == null) {
						return result;
					}
					set.add(con);
				}
			}
		}
		result.put("total", dao.getEventAlertLogHistoryCount(set, locations).size());
		
		return result;
	}
	
	
	/**
	 * @desc : EventAlertLogHistoryTotal COUNT fetch
	 */
	@SuppressWarnings("unused")
	public String getEventAlertLogHistoryTotalCnt(String[] values)
	{
		
		
		Set<Condition> set = new HashSet<Condition>();

		String locationId = values[1].substring(values[1].indexOf(":") + 1).trim();

		List<Integer> locations = null;
		
		
		if (locationId.length() > 0)
		{
			locations = locationDao.getLeafLocationId(
					Integer.parseInt(locationId),
					Integer.parseInt(values[0].substring(	values[0].indexOf(":") + 1).trim()));
		}

		int firstRow = 0;

		for (String value : values)
		{
			if (value != null)
			{
				Condition con = getSearchLogCondition(value);

				if (con != null)
				{
					if (con.getField() != null && con.getValue() != null)
					{
						if (con.getRestriction() == Restriction.FIRST)
						{
							firstRow = Integer.parseInt(con.getValue()[0].toString());
						}
						set.add(con);
					}
				}
			}
		}
		

		// fetch eventAlertLogHistory TOTAL COUNT
		String eventAlertLogHistoryTotalCnt= dao.getEventAlertLogHistoryTotalCnt(set, locations);
		

		return eventAlertLogHistoryTotalCnt;
		
		
	}
	
	
	
	/*
	 * 실시간 개수 가져오기
	 * @see com.aimir.service.device.EventAlertLogManager#getEventAlertLogRealTimeTotal(java.lang.Integer, java.lang.Integer)
	 */
	public Map<String, Object> getEventAlertLogRealTimeTotal(Integer userId, Integer supplierId) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total", "0");
		
		Set<Condition> set = getConditionByProfile(userId, supplierId);
		
		result.put("total", dao.getEventAlertLogRealTime(set).size());
		
		return result;
	}
	/*
	 * 실시간 이력 조회
	 * @see com.aimir.service.device.EventAlertLogManager#getEventAlertLogRealTime(java.lang.String[])
	 */
	public List<EventAlertLogVO> getEventAlertLogRealTime(String[] values) {
		Set<Condition> set = new HashSet<Condition>();
		List<EventAlertLogVO> result = new ArrayList<EventAlertLogVO>();
		@SuppressWarnings("unused")
		int firstRow = 0;

		for (String value : values) {
			if (value != null) {
				Condition con = getSearchLogCondition(value);
				
				if (con != null) {
					if (con.getValue() == null) {
						return result;
					}
					if (con.getRestriction() == Restriction.FIRST) {
						firstRow = Integer.parseInt(con.getValue()[0].toString());
					}
					set.add(con);
				}
			}
		}
		
		return dao.getEventAlertLogRealTime(set);
	}
	
	
	
	
	/*
	 * event alert log 이력 조회
	 * @see com.aimir.service.device.EventAlertLogManager#getEventAlertLogHistory(java.lang.String[])
	 */
    public List<EventAlertLogVO> getEventAlertLogHistory(String[] values) {
        Set<Condition> set = new HashSet<Condition>();

        String locationId = values[1].substring(values[1].indexOf(":") + 1).trim();

        List<Integer> locations = null;

        if (locationId.length() > 0) {
            locations = locationDao.getLeafLocationId(Integer.parseInt(locationId),
                    Integer.parseInt(values[0].substring(values[0].indexOf(":") + 1).trim()));
        }

        @SuppressWarnings("unused")
        int firstRow = 0;

        for (String value : values) {
            if (value != null) {
                Condition con = getSearchLogCondition(value);

                if (con != null) {
                    if (con.getField() != null && con.getValue() != null) {
                        if (con.getRestriction() == Restriction.FIRST) {
                            firstRow = Integer.parseInt(con.getValue()[0].toString());
                        }
                        set.add(con);
                    }
                }
            }
        }

        List<EventAlertLogVO> eventAlertList = dao.getEventAlertLogHistory(set, locations);

        return eventAlertList;
    }

    @SuppressWarnings({ "static-access", "unused" })
    public List<EventAlertLogVO> getEventAlertLogHistory2(String[] values, Map<String, String> conditionMap) {
        Map<String, String> conditionMap2 = this.getFirstPageForExtjsGrid(conditionMap);

        Set<Condition> set = new HashSet<Condition>();

        String locationId = values[1].substring(values[1].indexOf(":") + 1).trim();

        List<Integer> locations = null;

        if (locationId.length() > 0) {
            locations = locationDao.getLeafLocationId(Integer.parseInt(locationId),
                    Integer.parseInt(values[0].substring(values[0].indexOf(":") + 1).trim()));
        }

        int firstRow = 0;

        for (String value : values) {
            if (value != null) {
                Condition con = getSearchLogCondition(value);

                if (con != null) {
                    if (con.getField() != null && con.getValue() != null) {
                        if (con.getRestriction() == Restriction.FIRST) {
                            firstRow = Integer.parseInt(con.getValue()[0].toString());
                        }
                        set.add(con);
                    }
                }
            }
        }

        // ## EventAlertLogHistory List fetch from model
        List<EventAlertLogVO> eventalertloghistorylist = dao.getEventAlertLogHistory2(set, locations, conditionMap2);

        int supplierId = 0;

        supplierId = CommonUtils2.checkSupplierId((conditionMap2.get("supplierId")));

        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
        int idx = 1;
        for (EventAlertLogVO EventAlertLogBean : eventalertloghistorylist) {

            // date value 형식에 맞게 변환.
            EventAlertLogBean.setOpenTime(TimeLocaleUtil.getLocaleDate(EventAlertLogBean.getOpenTime(), supplier.getLang()
                    .getCode_2letter(), supplier.getCountry().getCode_2letter()));
            EventAlertLogBean.setCloseTime(TimeLocaleUtil.getLocaleDate(EventAlertLogBean.getCloseTime(), supplier.getLang()
                    .getCode_2letter(), supplier.getCountry().getCode_2letter()));
            EventAlertLogBean.setWriteTime(TimeLocaleUtil.getLocaleDate(EventAlertLogBean.getWriteTime(), supplier.getLang()
                    .getCode_2letter(), supplier.getCountry().getCode_2letter()));

//            DecimalFormat dfCd = DecimalUtil.getDecimalFormat(supplierDao.get(supplierId).getCd());

            // 숫자 포멧팅 처리
            // String strSendBytes = dfCd.format(Double.parseDouble(EventAlertLogBean.getSendBytes().toString()));

            String curPage = conditionMap2.get("page");
            String pageSize = conditionMap2.get("pageSize");

            // 페이지에 따른 페이지 인덱스 설정.
            EventAlertLogBean.setIdx(dfMd.format(this.makeIdxPerPage(curPage, pageSize, idx)));
            idx++;
        }

        return eventalertloghistorylist;
    }

    private Set<Condition> setPagingVariables(Map<String, Integer> pagingVars) {
        Set<Condition> conditions = new HashSet<Condition>();
        if (pagingVars != null) {
            if (pagingVars.containsKey("first")) {
                int first = (pagingVars.get("first") != null) ? pagingVars.get("first") : 0;
                conditions.add(new Condition("", new Object[] { first }, null, Restriction.FIRST));
            } else {
                conditions.add(new Condition("", new Object[] { 0 }, null, Restriction.FIRST));
            }

            if (pagingVars.containsKey("max")) {
                int first = (pagingVars.get("max") != null) ? pagingVars.get("max") : 10;
                conditions.add(new Condition("", new Object[] { first }, null, Restriction.MAX));
            } else {
                conditions.add(new Condition("", new Object[] { 10 }, null, Restriction.MAX));
            }
        }
        return conditions;
    }
	
	/**
	 * Map parameter로 Condition 셋을 만든다.
	 * 
	 * @param parameters 파라미터 값
	 * @param pagingVars 페이징 값
	 * @return Condition 객체 
	 * 
	 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
	 */
    private Set<Condition> buildConditionFromMap(Map<String, String> parameters, Map<String, Integer> pagingVars) {

        Set<Condition> conditions = new HashSet<Condition>();
        int supplierId = Integer.parseInt(StringUtil.nullToZero(parameters.get("supplierId")));
        if (parameters.containsKey("locationId")) {
            int locationId = Integer.parseInt(parameters.get("locationId"));
            List<Integer> list = locationDao.getLeafLocationId(locationId, supplierId);
            conditions.add(new Condition("location.id", list.toArray(), null, Restriction.IN));
        }
        if (parameters.containsKey("supplierId")) {
            conditions.add(new Condition("supplier.id", new Object[] { supplierId }, null, Restriction.EQ));
        }
        if (parameters.containsKey("status")) {
            try {
                EventStatus status = EventStatus.valueOf(parameters.get("status"));
                conditions.add(new Condition("status", new Object[] { status }, null, Restriction.EQ));
            } catch (IllegalArgumentException ignore) {
            }
        }

        if (parameters.containsKey("searchDate")) {
            String date = parameters.get("searchDate");
            if (date.contains("@")) {
                String[] dates = date.split("@");
                if (dates.length == 2) {
                    String startDate = dates[0] + "000000";
                    String endDate = dates[1] + "235959";
                    conditions.add(new Condition("openTime", new Object[] { startDate, endDate }, null, Restriction.BETWEEN));
                }
            }
        }

        conditions.add(new Condition("openTime", null, null, Restriction.ORDERBYDESC));

        if (pagingVars != null) {
            conditions.addAll(setPagingVariables(pagingVars));
        }
        return conditions;
    }
	
	@Override
    public List<EventAlertLog> getEventAlertLogs(Map<String, String> parameters, Map<String, Integer> pagingVars) {
        List<EventAlertLog> res = dao.findByConditions(buildConditionFromMap(parameters, pagingVars));
        return res;
    }
	
	@Override
    public long getEventAlertLogCount(Map<String, String> parameters) {
        return dao.findTotalByConditions(buildConditionFromMap(parameters, null));
    }
	
	/*
	 * 엑셀 저장용 이력 조회
	 * @see com.aimir.service.device.EventAlertLogManager#getEventAlertLogHistoryExcel(java.lang.String[])
	 */
	public List<EventAlertLogVO> getEventAlertLogHistoryExcel(String[] values) {
		Set<Condition> set = new HashSet<Condition>();
		
		String locationId = values[1].substring(values[1].indexOf(":") + 1).trim();
		int supplierId= Integer.parseInt(values[0].substring(values[1].indexOf(":") + 1).trim());
		
	    Supplier supplier = supplierDao.get(supplierId);
	    
		List<Integer> locations = null;
		if(locationId.length() > 0) {
			locations = locationDao.getLeafLocationId(Integer.parseInt(locationId), Integer.parseInt(values[0].substring(values[0].indexOf(":") + 1).trim()));
		}
		
		@SuppressWarnings("unused")
		int firstRow = 0;

		for (String value : values) {
			if (value != null) {
				Condition con = getSearchLogCondition(value);
				
				if (con != null) {	
					if (con.getField() != null && con.getValue() != null) {						
						set.add(con);
					}
				}
			}
		}

		List<EventAlertLogVO> eventalertloghistorylist= dao.getEventAlertLogHistoryExcel(set, locations );
		
//		int idx = 1; 
		for (EventAlertLogVO EventAlertLogBean : eventalertloghistorylist){
			
			EventAlertLogBean.setWriteTime(TimeLocaleUtil.getLocaleDate(EventAlertLogBean.getWriteTime(), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			EventAlertLogBean.setOpenTime(TimeLocaleUtil.getLocaleDate(EventAlertLogBean.getOpenTime(),	supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			EventAlertLogBean.setCloseTime(TimeLocaleUtil.getLocaleDate(EventAlertLogBean.getCloseTime(), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
	
//			idx++;
		}
		
		return eventalertloghistorylist;
	}

	/*
	 * 검색 조건 변환 후 반환
	 */
	private Condition getSearchLogCondition(String conditions) {
		Condition condition = new Condition();
		String[] strCondition = conditions.split(":");
		String field = null;
		Object[] value = new Object[1];
		Restriction restrict = null;
		logger.debug("CONDITION=" +conditions);
		if (strCondition.length < 2 || strCondition[1].trim().equals("")) {
			return null;
		}
		
		if (strCondition[0].equals("supplier")) {
			restrict = Restriction.EQ;
			field = "supplier.id";
			value[0] = Integer.parseInt(strCondition[1]);
		} else if (strCondition[0].equals("eventAlertType")) {
			restrict = Restriction.EQ;
			field = "eventAlertType";
			value[0] = strCondition[1];
		} else if (strCondition[0].equals("severity")) {
			restrict = Restriction.EQ;
			field = "severity";
			value[0] = strCondition[1];
		} else if (strCondition[0].equals("eventAlertClass")) {
			restrict = Restriction.EQ;
			field = "eventAlert.id";
			value[0] = Integer.parseInt(strCondition[1]);
		} else if (strCondition[0].equals("status")) {
			restrict = Restriction.EQ;
			field = "status";
			value[0] = strCondition[1];
		} else if (strCondition[0].equals("activatorType")) {
			restrict = Restriction.EQ;
			field = "activatorType";
			value[0] = strCondition[1]; //Integer.parseInt(strCondition[1]); jhkim
		} else if (strCondition[0].equals("activatorId")) {
			restrict = Restriction.EQ;
			field = "activatorId";
			value[0] = strCondition[1];
		} else if (strCondition[0].equals("location") && Integer.parseInt(strCondition[1]) > 0) {
			restrict = Restriction.EQ;
			field = "location.id";
			value[0] = Integer.parseInt(strCondition[1]);
		} else if (strCondition[0].equals("message")) {
			restrict = Restriction.LIKE;
			field = "message";
			value[0] = strCondition[1];
		} else if (strCondition[0].equals("yyyymmddhhmmss")) {
			restrict = Restriction.BETWEEN;
			field = "yyyymmddhhmmss";
			value[0] = strCondition[1];
			value[1] = strCondition[2];
		} else if (strCondition[0].equals("first")) {
			restrict = Restriction.FIRST;
			field = "";
			value[0] = Integer.parseInt(strCondition[1]);
		} else if (strCondition[0].equals("max")) {
			restrict = Restriction.MAX;
			field = "";
			value[0] = Integer.parseInt(strCondition[1]);
		} else if (strCondition[0].equals("startDate")) {
			restrict = Restriction.GE;
			field = "startDate";
			value[0] = strCondition[1];
		} else if (strCondition[0].equals("endDate")) {
			restrict = Restriction.LE;
			field = "endDate";
			value[0] = strCondition[1];
		}
		
		condition.setRestrict(restrict);
		condition.setField(field);
		condition.setValue(value);
		
		return condition;
	}

    /**
     * method name : getEventAlertLogFromDB<b/>
     * method Desc : MDIS. EventAlertLog 가젯에서 RealTime 데이터를 DB에서 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEventAlertLogFromDB(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = dao.getEventAlertLogFromDB(conditionMap);

        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        if (result != null) {
            for (Map<String, Object> obj : result) {
                obj.put("openTime", TimeLocaleUtil.getLocaleDate((String)obj.get("openTime"), lang, country));
                obj.put("closeTime", TimeLocaleUtil.getLocaleDate((String)obj.get("closeTime"), lang, country));
            }
        }

        return result;
    }	
}