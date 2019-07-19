package com.aimir.dao.device.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.EventAlertLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.EventAlertLogVO;

import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

@SuppressWarnings("rawtypes")
@Repository(value = "eventalertlogDao")
public class EventAlertLogDaoImpl extends AbstractJpaDao<EventAlertLog, Long> implements EventAlertLogDao {

	Log log = LogFactory.getLog(EventAlertLogDaoImpl.class);
	
	public EventAlertLogDaoImpl() {
		super(EventAlertLog.class);
	}
	
	@Autowired CodeDao codeDao;

	public List<EventAlertLogVO> getEventAlertLogRealTime(Set<Condition> conditions) {
		Calendar calendar = Calendar.getInstance();
		
		String today = Integer.toString(calendar.get(Calendar.YEAR));
		
		int month = calendar.get(Calendar.MONTH) + 1;
		if (month < 10) {
			today += "0" + Integer.toString(month);
		} else {
			today += Integer.toString(month);
		}
		
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if (day < 10) {
			today += "0" + Integer.toString(day);
		} else {
			today += Integer.toString(day);
		}
		
		return getEventAlertLogRealTime(conditions, today, today);
		
	}
	
	@SuppressWarnings("unchecked")
	public List<EventAlertLog> getOpenEventAlertLog(String activatorType, String activatorId, Integer eventAlertId) {
	    String sql = "select max(e.openTime) from EventAlertLog e where e.openTime between :fromTime and :toTime and "
	    		   + "e.activatorType = :activatorType and " +
	                 "e.activatorId = :activatorId and e.status = :status and (e.closeTime is null or e.closeTime = :closeTime)" +
                     " and e.eventAlert.id = :eventAlertId";
	    Query query = em.createQuery(sql, String.class);
	    
	    query.setParameter("fromTime", DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd")+"000000"); //SP-1011
	    query.setParameter("toTime", DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
	    query.setParameter("activatorType", TargetClass.valueOf(activatorType));
	    query.setParameter("activatorId", activatorId);
	    query.setParameter("eventAlertId", eventAlertId);
	    query.setParameter("status", EventStatus.Open);
	    query.setParameter("closeTime", "");
	    
	    String openTime = (String)query.getSingleResult();
	    
	    sql = "select e from EventAlertLog e where e.activatorType = :activatorType and " +
                     "e.activatorId = :activatorId and e.status = :status and e.openTime = :openTime " +
	                 " and e.eventAlert.id = :eventAlertId";
	    query = em.createQuery(sql, EventAlertLog.class);
	    query.setParameter("activatorType", TargetClass.valueOf(activatorType));
        query.setParameter("activatorId", activatorId);
        query.setParameter("eventAlertId", eventAlertId);
        query.setParameter("status", EventStatus.Open);
        query.setParameter("openTime", openTime);
        
        return query.getResultList();
	}
	
	
	private List<EventAlertLogVO> getEventAlertLogRealTime(Set<Condition> conditions, String searchStartDate, String searchEndDate){
		
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT e.eventAlertType, ea.message, l.name, ea.activatorId, ea.activatorIp, ea.status,ea.activatorType,");
		hqlBuf.append("        ea.openTime, ea.closeTime, ea.duration");
		hqlBuf.append(" FROM EventAlertLog ea join ea.eventAlert e");
		hqlBuf.append("                       join ea.supplier s");
		hqlBuf.append("                       join ea.location l");
		hqlBuf.append(" WHERE ea.openTime >= :opentime1");
		hqlBuf.append("   AND ea.openTime <= :opentime2");
		if(conditions != null) {
			boolean whereFlag = false;
			boolean flag = false;
			Iterator it = conditions.iterator();
    		log.info("\n=================");
			while(it.hasNext()){
	            Condition condition = (Condition)it.next();
	            String strCondition = condition.getField();
	    		log.info("\n"+ strCondition);
	    		log.info("\n"+ condition.getValue()[0]);
	    		log.info("\n=================");
	            
	            if (condition.getRestriction() != Restriction.FIRST && condition.getRestriction() != Restriction.MAX) {
	            	if (whereFlag == false) {
	            		whereFlag = true;
	            		hqlBuf.append(" AND");
	            	} else {
	            		if (flag == true) {
	            			flag = false;
    						hqlBuf.append(" AND ");
	            		}
	            	}
	            }
	            
	            if (strCondition.equals("supplier.id")) {
					hqlBuf.append(" s.id = :supplierId");
					flag = true;
				} else if (strCondition.equals("eventAlertType")) {
					hqlBuf.append(" e.eventAlertType = :eventAlertType");
					flag = true;
				} else if (strCondition.equals("severity")) {
					hqlBuf.append(" ea.severity = :severity");
					flag = true;
				} else if (strCondition.equals("eventAlert.id")) {
					hqlBuf.append(" e.id = :eventAlert");
					flag = true;
				} else if (strCondition.equals("status")) {
					hqlBuf.append(" ea.status = :status");
					flag = true;
				} else if (strCondition.equals("activatorType")) {
					hqlBuf.append(" ea.activatorType = :activatorType");
					flag = true;
				} else if (strCondition.equals("activatorId")) {
					hqlBuf.append(" ea.activatorId = :activatorId");
					flag = true;
				} else if (strCondition.equals("location.id")) {
					hqlBuf.append(" l.id = :location");
					flag = true;
				}
			}
		}
		hqlBuf.append(" ORDER BY ea.openTime DESC");
		Query query = em.createQuery(hqlBuf.toString(), Object[].class);
		
		if(conditions != null) {
			
			Iterator it = conditions.iterator();
			while(it.hasNext()){
	            Condition condition = (Condition)it.next();
	            String strCondition = condition.getField();

	            if (strCondition.equals("supplier.id")) {
					query.setParameter("supplierId", condition.getValue()[0]);
				} else if (strCondition.equals("eventAlertType")) {
					query.setParameter("eventAlertType", condition.getValue()[0]);
				} else if (strCondition.equals("severity")) {
					query.setParameter("severity", condition.getValue()[0]);
				} else if (strCondition.equals("eventAlert.id")) {
					query.setParameter("eventAlert", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("status")) {
					query.setParameter("status", condition.getValue()[0]);
				} else if (strCondition.equals("activatorType")) {
					query.setParameter("activatorType", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("activatorId")) {
					query.setParameter("activatorId", (String) condition.getValue()[0]);
				} else if (strCondition.equals("location.id")) {
					query.setParameter("location", (Integer) condition.getValue()[0]);
				} else if (condition.getRestriction() == Restriction.FIRST) {
					query.setFirstResult((Integer) condition.getValue()[0]);
				} else if (condition.getRestriction() == Restriction.MAX) {
					query.setMaxResults((Integer) condition.getValue()[0]);
				}
			}
		}
		
		query.setParameter("opentime1", searchStartDate + "000000");
		query.setParameter("opentime2", searchEndDate + "235959");
		
		List result = query.getResultList();
		
		List<EventAlertLogVO> eventAlertLogVOs = new ArrayList<EventAlertLogVO>();
		EventAlertLogVO eventAlertLogVO = null;
		
		for (int i = 0; i < result.size(); i++) {
			eventAlertLogVO = new EventAlertLogVO();
			
			Object[] resultData = (Object[]) result.get(i);
			eventAlertLogVO.setRank(Integer.toString(i + 1));
			eventAlertLogVO.setType(resultData[0].toString().substring(0));
			if (resultData[1] != null) {
				eventAlertLogVO.setMessage(resultData[1].toString());
			}
			eventAlertLogVO.setLocation(resultData[2].toString());
			eventAlertLogVO.setActivatorId(resultData[3].toString());
			eventAlertLogVO.setActivatorIp(resultData[4].toString());
			eventAlertLogVO.setStatus(resultData[5].toString());
			eventAlertLogVO.setActivatorType(resultData[6].toString());
			eventAlertLogVO.setOpenTime(resultData[7].toString());
			if (resultData[8] != null) {
				eventAlertLogVO.setCloseTime(resultData[8].toString());
			}
			if (resultData[9] != null) {
				eventAlertLogVO.setDuration(resultData[9].toString());
			}
			eventAlertLogVOs.add(eventAlertLogVO);
		}
		return eventAlertLogVOs;
	}
	
	
	public List<EventAlertLogVO> getEventAlertLogHistory(Set<Condition> conditions) {

		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT e.eventAlertType, ea.message, l.name, ea.activatorId, ea.activatorIp, ea.status, ea.activatorType, ");
		hqlBuf.append("        ea.writeTime, ea.openTime, ea.closeTime, ea.duration, e.severity");
		hqlBuf.append(" FROM EventAlertLog ea join ea.eventAlert e");
		hqlBuf.append("                       left join ea.supplier s");
		hqlBuf.append("                       left join ea.location l");

		if(conditions != null) {
			boolean whereFlag = false;
			boolean flag = false;
			Iterator it = conditions.iterator();
			while(it.hasNext()){
	            Condition condition = (Condition)it.next();
	            String strCondition = condition.getField();
	            
	            if (condition.getRestriction() != Restriction.FIRST && condition.getRestriction() != Restriction.MAX) {
	            	if (whereFlag == false) {
	            		whereFlag = true;
	            		hqlBuf.append(" WHERE");
	            	} else {
	            		if (flag == true) {
	            			flag = false;
    						hqlBuf.append(" AND ");
	            		}
	            	}
	            }
	            
	            if (strCondition.equals("supplier.id")) {
					hqlBuf.append(" (s.id = :supplierId or s.id is null) ");
					flag = true;
				} else if (strCondition.equals("eventAlertType")) {
					hqlBuf.append(" e.eventAlertType = :eventAlertType");
					flag = true;
				} else if (strCondition.equals("severity")) {
					hqlBuf.append(" e.severity = :severity");
					flag = true;
				} else if (strCondition.equals("eventAlert.id")) {
					hqlBuf.append(" e.id = :eventAlert");
					flag = true;
				} else if (strCondition.equals("status")) {
					hqlBuf.append(" ea.status = :status");
					flag = true;
				} else if (strCondition.equals("activatorType")) {
					hqlBuf.append(" ac.id = :activatorType");
					flag = true;
				} else if (strCondition.equals("activatorId")) {
					hqlBuf.append(" ea.activatorId = :activatorId");
					flag = true;
				} else if (strCondition.equals("location.id")) {
					hqlBuf.append(" l.id = :location");
					flag = true;
				} else if (strCondition.equals("message")) {
					hqlBuf.append(" ea.message LIKE :message");
					flag = true;
				} else if (strCondition.equals("startDate")) {
					hqlBuf.append(" ea.openTime >= :startDate");
					flag = true;
				} else if (strCondition.equals("endDate")) {
					hqlBuf.append(" ea.openTime <= :endDate");
					flag = true;
				}
			}
		}
		hqlBuf.append(" ORDER BY ea.openTime DESC");

		Query query = em.createQuery(hqlBuf.toString(), Object[].class);
		
		if(conditions != null) {
			Iterator it = conditions.iterator();
			while(it.hasNext()){
	            Condition condition = (Condition)it.next();
	            String strCondition = condition.getField();

	            if (strCondition.equals("supplier.id")) {
					query.setParameter("supplierId", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("eventAlertType")) {
					query.setParameter("eventAlertType", CommonConstants.EventAlertType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("severity")) {
					query.setParameter("severity", CommonConstants.SeverityType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("eventAlert.id")) {
					query.setParameter("eventAlert", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("status")) {
					query.setParameter("status", CommonConstants.EventStatus.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("activatorType")) {
					query.setParameter("activatorType", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("activatorId")) {
					query.setParameter("activatorId", (String) condition.getValue()[0]);
				} else if (strCondition.equals("location.id")) {
					query.setParameter("location", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("message")) {
					query.setParameter("message", "%" + (String) condition.getValue()[0] +"%");
				} else if (condition.getRestriction() == Restriction.FIRST) {
					query.setFirstResult((Integer) condition.getValue()[0]);
				} else if (condition.getRestriction() == Restriction.MAX) {
					query.setMaxResults((Integer) condition.getValue()[0]);
				} else if (strCondition.equals("startDate")) {
					query.setParameter("startDate", condition.getValue()[0].toString() + "000000");
				} else if (strCondition.equals("endDate")) {
					query.setParameter("endDate", condition.getValue()[0].toString() + "235959");
				}
			}
		}
		
		List result = query.getResultList();
		
		List<EventAlertLogVO> eventAlertLogVOs = new ArrayList<EventAlertLogVO>();
		EventAlertLogVO eventAlertLogVO = null;

		for (int i = 0; i < result.size(); i++) {
			eventAlertLogVO = new EventAlertLogVO();
			
			Object[] resultData = (Object[]) result.get(i);
			eventAlertLogVO.setRank(Integer.toString(i + 1));
			eventAlertLogVO.setType(resultData[0].toString().substring(0));
			if (resultData[1] != null) {
				eventAlertLogVO.setMessage(resultData[1].toString());
			}
			eventAlertLogVO.setLocation(resultData[2] ==  null ? "" :resultData[2].toString() );			
			eventAlertLogVO.setActivatorId(resultData[3].toString());
			if(resultData[4] != null){
				eventAlertLogVO.setActivatorIp(resultData[4].toString());
			}

			eventAlertLogVO.setStatus(resultData[5].toString());
			eventAlertLogVO.setActivatorType(resultData[6].toString());
			eventAlertLogVO.setWriteTime(resultData[7].toString());
			eventAlertLogVO.setOpenTime(resultData[8].toString());
			if (resultData[9] != null) {
				eventAlertLogVO.setCloseTime(resultData[9].toString());
			}
			if (resultData[10] != null) {
				eventAlertLogVO.setDuration(resultData[10].toString());
			}
			if (resultData[11] != null) {
				eventAlertLogVO.setSeverity(resultData[11].toString());
			}
			eventAlertLogVOs.add(eventAlertLogVO);
		}

		return eventAlertLogVOs;
	}
	
	@SuppressWarnings("unchecked")
    public List<EventAlertLogVO> getEventAlertLogHistory(Set<Condition> conditions, List<Integer> locations) {

		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT e.eventAlertType, ea.message, l.name, ea.activatorId, ea.activatorIp, ea.status, ea.activatorType, ");
		hqlBuf.append("        ea.writeTime, ea.openTime, ea.closeTime, ea.duration, e.severity");
		hqlBuf.append(" FROM EventAlertLog ea join ea.eventAlert e");
//		hqlBuf.append("                       left join ea.supplier s");
		hqlBuf.append("                       join ea.supplier s");
		hqlBuf.append("                       left join ea.location l");

		if(conditions != null) {
			boolean whereFlag = false;
			boolean flag = false;
			Iterator it = conditions.iterator();
			while(it.hasNext()){
	            Condition condition = (Condition)it.next();
	            String strCondition = condition.getField();
	            
	            if (condition.getRestriction() != Restriction.FIRST && condition.getRestriction() != Restriction.MAX) {
	            	if (whereFlag == false) {
	            		whereFlag = true;
	            		hqlBuf.append(" WHERE");
	            	} else {
	            		if (flag == true) {
	            			flag = false;
    						hqlBuf.append(" AND ");
	            		}
	            	}
	            }
	            
	            if (strCondition.equals("supplier.id")) {
//					hqlBuf.append(" (s.id = :supplierId or s.id is null) ");
					hqlBuf.append(" s.id = :supplierId ");
					flag = true;
				} else if (strCondition.equals("eventAlertType")) {
					hqlBuf.append(" e.eventAlertType = :eventAlertType");
					flag = true;
				} else if (strCondition.equals("severity")) {
					hqlBuf.append(" e.severity = :severity");
					flag = true;
				} else if (strCondition.equals("eventAlert.id")) {
					hqlBuf.append(" e.id = :eventAlert");
					flag = true;
				} else if (strCondition.equals("status")) {
					hqlBuf.append(" ea.status = :status");
					flag = true;
				} else if (strCondition.equals("activatorType")) {
					hqlBuf.append(" ea.activatorType = :activatorType");
					flag = true;
				} else if (strCondition.equals("activatorId")) {
					hqlBuf.append(" ea.activatorId = :activatorId");
					flag = true;
				} else if (strCondition.equals("location.id")) {
					hqlBuf.append(" l.id IN (:location)");
					flag = true;
				} else if (strCondition.equals("message")) {
					hqlBuf.append(" ea.message LIKE :message");
					flag = true;
				} else if (strCondition.equals("startDate")) {
					hqlBuf.append(" ea.openTime >= :startDate");
					flag = true;
				} else if (strCondition.equals("endDate")) {
					hqlBuf.append(" ea.openTime <= :endDate");
					flag = true;
				}
			}
		}
		hqlBuf.append(" ORDER BY ea.openTime DESC");

		Query query = em.createQuery(hqlBuf.toString(), Object[].class);
		
		if(conditions != null) {
			Iterator it = conditions.iterator();
			while(it.hasNext()){
	            Condition condition = (Condition)it.next();
	            String strCondition = condition.getField();

	            if (strCondition.equals("supplier.id")) {
					query.setParameter("supplierId", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("eventAlertType")) {
					query.setParameter("eventAlertType", CommonConstants.EventAlertType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("severity")) {
					query.setParameter("severity", CommonConstants.SeverityType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("eventAlert.id")) {
					query.setParameter("eventAlert", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("status")) {
					query.setParameter("status", CommonConstants.EventStatus.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("activatorType")) {
					query.setParameter("activatorType", TargetClass.valueOf((String) condition.getValue()[0]));//jhkim
				} else if (strCondition.equals("activatorId")) {
					query.setParameter("activatorId", (String) condition.getValue()[0]);
				} else if (strCondition.equals("location.id")) {
					query.setParameter("location", locations);
				} else if (strCondition.equals("message")) {
					query.setParameter("message", "%" + (String) condition.getValue()[0] +"%");
				} else if (condition.getRestriction() == Restriction.FIRST) {
					query.setFirstResult((Integer) condition.getValue()[0]);
				} else if (condition.getRestriction() == Restriction.MAX) {
					query.setMaxResults((Integer) condition.getValue()[0]);
				} else if (strCondition.equals("startDate")) {
					query.setParameter("startDate", condition.getValue()[0].toString() + "000000");
				} else if (strCondition.equals("endDate")) {
					query.setParameter("endDate", condition.getValue()[0].toString() + "235959");
				}
			}
		}

		List<Object[]> result = query.getResultList();
		
		List<EventAlertLogVO> eventAlertLogVOs = new ArrayList<EventAlertLogVO>();
		EventAlertLogVO eventAlertLogVO = null;
		Object[] resultData = null;

		for (int i = 0; i < result.size(); i++) {
			eventAlertLogVO = new EventAlertLogVO();
			
			resultData = result.get(i);
			eventAlertLogVO.setRank(Integer.toString(i + 1));
			eventAlertLogVO.setType(resultData[0].toString().substring(0));
			if (resultData[1] != null) {
				eventAlertLogVO.setMessage(resultData[1].toString());
			}
			eventAlertLogVO.setLocation(resultData[2] ==  null ? "" :resultData[2].toString() );
			eventAlertLogVO.setActivatorId(resultData[3].toString());
			if(resultData[4] != null){
				eventAlertLogVO.setActivatorIp(resultData[4].toString());
			}

			eventAlertLogVO.setStatus(resultData[5].toString());
			eventAlertLogVO.setActivatorType(resultData[6].toString());
			eventAlertLogVO.setWriteTime(resultData[7].toString());
			eventAlertLogVO.setOpenTime(resultData[8].toString());
			if (resultData[9] != null) {
				eventAlertLogVO.setCloseTime(resultData[9].toString());
			}
			if (resultData[10] != null) {
				eventAlertLogVO.setDuration(resultData[10].toString());
			}
			if (resultData[11] != null) {
				eventAlertLogVO.setSeverity(resultData[11].toString());
			}
			eventAlertLogVOs.add(eventAlertLogVO);
			
		}

		return eventAlertLogVOs;
	}
	
	
	/**
	 * @desc 쿼리에 페이징 기능을 추가하는 method
	 * @param query
	 * @param conditionMap
	 * @return Query
	 */
	public static Query  addPagingForQuery(Query query, Map<String, String> conditionMap)
	{
		String strPage = conditionMap.get("page");
		String strPageSize = conditionMap.get("pageSize");

		int pageCommaIndex = strPage.indexOf(".");
		int pageSizeCommaIndex = strPageSize.indexOf(".");
		int page = -1;
		int pageSize = -1;
		
		if(pageCommaIndex > -1)
			page = Integer.parseInt(strPage.substring(0, pageCommaIndex));
		else
			page = Integer.parseInt(strPage);
		
		if(pageSizeCommaIndex > -1)
			pageSize = Integer.parseInt(strPageSize.substring(0, pageSizeCommaIndex));
		else
			pageSize = Integer.parseInt(strPageSize);

		int firstResult = page * pageSize;
		
		query.setFirstResult(firstResult);		
		query.setMaxResults(pageSize);
		
		return query;
		
	}

    @Override
    public List<EventAlertLogVO> getEventAlertLogRealTimeForMini(
            Set<Condition> conditions, String searchStartDate,
            String searchEndDate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EventAlertLogVO> getEventAlertLogHistoryExcel(
            Set<Condition> conditions, List<Integer> locations) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EventAlertLogVO> getEventAlertLogHistoryCount(
            Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EventAlertLogVO> getEventAlertLogHistoryCount(
            Set<Condition> conditions, List<Integer> locations) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEventAlertLogByActivatorType(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEventAlertLogByMessage(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEventAlertLogCount(Map<String, String> map) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List<Map<String, String>> getEventAlertLogs(Map<String, String> map) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EventAlertLogVO> getEventAlertLogHistory2(
            Set<Condition> conditions, List<Integer> locations,
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEventAlertLogHistoryTotalCnt(Set<Condition> conditions,
            List<Integer> locations) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEventAlertLogFromDB(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long findTotalByConditions(Set<Condition> conditions) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Map<String, Object>> getMcuEventAlertLogList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<EventAlertLog> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public List<Map<String, Object>> getMcuLogType(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// INSERT SP-818
	@Override
	public List<Map<String, Object>>  getProblematicMetersEvent(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}

}