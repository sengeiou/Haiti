package com.aimir.dao.device.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.EventAlertType;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.SeverityType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.EventAlertLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.EventAlertLogVO;

import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.CommonUtils2;

@SuppressWarnings("rawtypes")
@Repository(value = "eventalertlogDao")
public class EventAlertLogDaoImpl extends AbstractHibernateGenericDao<EventAlertLog, Long> implements EventAlertLogDao {

	Log log = LogFactory.getLog(EventAlertLogDaoImpl.class);

	@Autowired
	protected EventAlertLogDaoImpl(SessionFactory sessionFactory) {
		super(EventAlertLog.class);
		super.setSessionFactory(sessionFactory);
	}

	@Autowired
	CodeDao codeDao;

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
		String maxOt = (String) getSession().createCriteria(EventAlertLog.class)
				.add(Restrictions.eq("activatorType", TargetClass.valueOf(activatorType)))
				.add(Restrictions.eq("activatorId", activatorId)).add(Restrictions.eq("status", EventStatus.Open))
				.add(Restrictions.or(Restrictions.isNull("closeTime"), Restrictions.eq("closeTime", "")))
				.setProjection(Projections.max("openTime")).createCriteria("eventAlert")
				.add(Restrictions.idEq(eventAlertId)).uniqueResult();

		return getSession().createCriteria(EventAlertLog.class)
				.add(Restrictions.eq("activatorType", TargetClass.valueOf(activatorType)))
				.add(Restrictions.eq("activatorId", activatorId)).add(Restrictions.eq("status", EventStatus.Open))
				.add(Restrictions.eq("openTime", maxOt))
				.add(Restrictions.or(Restrictions.isNull("closeTime"), Restrictions.eq("closeTime", "")))
				.createCriteria("eventAlert").add(Restrictions.idEq(eventAlertId)).list();
	}

	private List<EventAlertLogVO> getEventAlertLogRealTime(Set<Condition> conditions, String searchStartDate,
			String searchEndDate) {

		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(
				" SELECT e.eventAlertType, ea.message, l.name, ea.activatorId, ea.activatorIp, ea.status,ea.activatorType,");
		hqlBuf.append("        ea.openTime, ea.closeTime, ea.duration");
		hqlBuf.append(" FROM EventAlertLog ea join ea.eventAlert e");
		hqlBuf.append("                       join ea.supplier s");
		hqlBuf.append("                       join ea.location l");
		hqlBuf.append(" WHERE ea.openTime >= :opentime1");
		hqlBuf.append("   AND ea.openTime <= :opentime2");
		if (conditions != null) {
			boolean whereFlag = false;
			boolean flag = false;
			Iterator it = conditions.iterator();
			log.info("\n=================");
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
				String strCondition = condition.getField();
				log.info("\n" + strCondition);
				log.info("\n" + condition.getValue()[0]);
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
		Query query = getSession().createQuery(hqlBuf.toString());

		if (conditions != null) {

			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
				String strCondition = condition.getField();

				if (strCondition.equals("supplier.id")) {
					query.setParameter("supplierId", (Integer) condition.getValue()[0]);
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

		List result = query.list();

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
		hqlBuf.append(
				" SELECT e.eventAlertType, ea.message, l.name, ea.activatorId, ea.activatorIp, ea.status, ea.activatorType, ");
		hqlBuf.append("        ea.writeTime, ea.openTime, ea.closeTime, ea.duration, e.severity");
		hqlBuf.append(" FROM EventAlertLog ea join ea.eventAlert e");
		hqlBuf.append("                       left join ea.supplier s");
		hqlBuf.append("                       left join ea.location l");

		if (conditions != null) {
			boolean whereFlag = false;
			boolean flag = false;
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
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

		Query query = getSession().createQuery(hqlBuf.toString());

		if (conditions != null) {
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
				String strCondition = condition.getField();

				if (strCondition.equals("supplier.id")) {
					query.setParameter("supplierId", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("eventAlertType")) {
					query.setParameter("eventAlertType",
							CommonConstants.EventAlertType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("severity")) {
					query.setParameter("severity",
							CommonConstants.SeverityType.valueOf((String) condition.getValue()[0]));
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
					query.setParameter("message", "%" + (String) condition.getValue()[0] + "%");
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

		List result = query.list();

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
			eventAlertLogVO.setLocation(resultData[2] == null ? "" : resultData[2].toString());
			eventAlertLogVO.setActivatorId(resultData[3].toString());
			if (resultData[4] != null) {
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
		hqlBuf.append(
				" SELECT e.eventAlertType, ea.message, l.name, ea.activatorId, ea.activatorIp, ea.status, ea.activatorType, ");
		hqlBuf.append("        ea.writeTime, ea.openTime, ea.closeTime, ea.duration, e.severity");
		hqlBuf.append(" FROM EventAlertLog ea join ea.eventAlert e");
		// hqlBuf.append(" left join ea.supplier s");
		hqlBuf.append("                       join ea.supplier s");
		hqlBuf.append("                       left join ea.location l");

		if (conditions != null) {
			boolean whereFlag = false;
			boolean flag = false;
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
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
					// hqlBuf.append(" (s.id = :supplierId or s.id is null) ");
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

		Query query = getSession().createQuery(hqlBuf.toString());

		if (conditions != null) {
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
				String strCondition = condition.getField();

				if (strCondition.equals("supplier.id")) {
					query.setParameter("supplierId", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("eventAlertType")) {
					query.setParameter("eventAlertType",
							CommonConstants.EventAlertType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("severity")) {
					query.setParameter("severity",
							CommonConstants.SeverityType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("eventAlert.id")) {
					query.setParameter("eventAlert", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("status")) {
					query.setParameter("status", CommonConstants.EventStatus.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("activatorType")) {
					query.setParameter("activatorType", TargetClass.valueOf((String) condition.getValue()[0]));// jhkim
				} else if (strCondition.equals("activatorId")) {
					query.setParameter("activatorId", (String) condition.getValue()[0]);
				} else if (strCondition.equals("location.id")) {
					query.setParameterList("location", locations);
				} else if (strCondition.equals("message")) {
					query.setParameter("message", "%" + (String) condition.getValue()[0] + "%");
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

		List<Object[]> result = query.list();

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
			eventAlertLogVO.setLocation(resultData[2] == null ? "" : resultData[2].toString());
			eventAlertLogVO.setActivatorId(resultData[3].toString());
			if (resultData[4] != null) {
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
	public static Query addPagingForQuery(Query query, Map<String, String> conditionMap) {
		String strPage = conditionMap.get("page");
		String strPageSize = conditionMap.get("pageSize");

		int pageCommaIndex = strPage.indexOf(".");
		int pageSizeCommaIndex = strPageSize.indexOf(".");
		int page = -1;
		int pageSize = -1;

		if (pageCommaIndex > -1)
			page = Integer.parseInt(strPage.substring(0, pageCommaIndex));
		else
			page = Integer.parseInt(strPage);

		if (pageSizeCommaIndex > -1)
			pageSize = Integer.parseInt(strPageSize.substring(0, pageSizeCommaIndex));
		else
			pageSize = Integer.parseInt(strPageSize);

		int firstResult = page * pageSize;

		query.setFirstResult(firstResult);
		query.setMaxResults(pageSize);

		return query;

	}

	/**
	 * @dESC: EventAlertLogHistory FETCH DAO2 (페이징 추가)
	 */
	@SuppressWarnings({ "unchecked" })
	public List<EventAlertLogVO> getEventAlertLogHistory2(Set<Condition> conditions, List<Integer> locations,
			Map<String, String> conditionMap) {

		StringBuffer hqlBuf = new StringBuffer();
		// hqlBuf.append(" SELECT e.eventAlertType, ea.message, l.name,
		// ea.activatorId, ea.activatorIp, ea.status, ea.activatorType, ");
		// // EventAlert.name & id 추가
		// hqlBuf.append(" ea.writeTime, ea.openTime, ea.closeTime, ea.duration,
		// e.severity, e.id, e.name ");
		// hqlBuf.append(" FROM EventAlertLog ea join ea.eventAlert e");
		// // hqlBuf.append(" left join ea.supplier s");
		// hqlBuf.append(" join ea.supplier s");
		// hqlBuf.append(" left join ea.location l");

		hqlBuf.append("\nSELECT e.eventAlertType, ");
		hqlBuf.append("\n       ea.message, ");
		hqlBuf.append("\n       l.name, ");
		hqlBuf.append("\n       ea.activatorId, ");
		hqlBuf.append("\n       ea.activatorIp, ");
		hqlBuf.append("\n       ea.status, ");
		hqlBuf.append("\n       ea.activatorType, ");
		hqlBuf.append("\n       ea.writeTime, ");
		hqlBuf.append("\n       ea.openTime, ");
		hqlBuf.append("\n       ea.closeTime, ");
		hqlBuf.append("\n       ea.duration, ");
		hqlBuf.append("\n       e.severity, ");
		hqlBuf.append("\n       e.id,  ");
		hqlBuf.append("\n       e.name, ");
		hqlBuf.append("\n       ea.id AS eventLogId ");
		hqlBuf.append("\nFROM EventAlertLog ea ");
		hqlBuf.append("\n     JOIN ea.eventAlert e ");
		hqlBuf.append("\n     LEFT OUTER JOIN ea.location l ");

		if (conditions != null) {
			boolean whereFlag = false;
			boolean flag = false;
			Iterator it = conditions.iterator();

			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
				String strCondition = condition.getField();

				if (condition.getRestriction() != Restriction.FIRST && condition.getRestriction() != Restriction.MAX) {
					if (whereFlag == false) {
						whereFlag = true;
						hqlBuf.append("\nWHERE");
					} else {
						if (flag == true) {
							flag = false;
							hqlBuf.append("\n   AND ");
						}
					}
				}

				if (strCondition.equals("supplier.id")) {
					// hqlBuf.append(" (s.id = :supplierId or s.id is null) ");
					// hqlBuf.append(" s.id = :supplierId ");
					hqlBuf.append(" ea.supplier.id = :supplierId ");
					flag = true;
				} else if (strCondition.equals("eventAlertType")) {
					hqlBuf.append(" e.eventAlertType = :eventAlertType ");
					flag = true;
				} else if (strCondition.equals("severity")) {
					hqlBuf.append(" e.severity = :severity ");
					flag = true;
				} else if (strCondition.equals("eventAlert.id")) {
					hqlBuf.append(" e.id = :eventAlert ");
					flag = true;
				} else if (strCondition.equals("status")) {
					hqlBuf.append(" ea.status = :status ");
					flag = true;
				} else if (strCondition.equals("activatorType")) {
					hqlBuf.append(" ea.activatorType = :activatorType ");
					flag = true;
				} else if (strCondition.equals("activatorId")) {
					hqlBuf.append(" ea.activatorId = :activatorId ");
					flag = true;
				} else if (strCondition.equals("location.id")) {
					hqlBuf.append(" l.id IN (:location) ");
					flag = true;
				} else if (strCondition.equals("message")) {
					hqlBuf.append(" ea.message LIKE :message ");
					flag = true;
				} else if (strCondition.equals("startDate")) {
					hqlBuf.append(" ea.openTime >= :startDate ");
					flag = true;
				} else if (strCondition.equals("endDate")) {
					hqlBuf.append(" ea.openTime <= :endDate ");
					flag = true;
				}
			}
		}
		
		hqlBuf.append("\nORDER BY ea.id desc");

		Query query = getSession().createQuery(hqlBuf.toString());

		/**
		 * @desc query set Parameters..
		 */
		if (conditions != null) {
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
				String strCondition = condition.getField();

				if (strCondition.equals("supplier.id")) {
					int tempIntValue = (Integer) condition.getValue()[0];
					query.setParameter("supplierId", tempIntValue);
				} else if (strCondition.equals("eventAlertType")) {
					query.setParameter("eventAlertType",
							CommonConstants.EventAlertType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("severity")) {
					query.setParameter("severity",
							CommonConstants.SeverityType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("eventAlert.id")) {
					query.setParameter("eventAlert", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("status")) {
					query.setParameter("status", CommonConstants.EventStatus.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("activatorType")) {
					query.setParameter("activatorType", TargetClass.valueOf((String) condition.getValue()[0]));// jhkim
				} else if (strCondition.equals("activatorId")) {
					query.setParameter("activatorId", (String) condition.getValue()[0]);
				} else if (strCondition.equals("location.id")) {
					query.setParameterList("location", locations);
				} else if (strCondition.equals("message")) {
					query.setParameter("message", "%" + (String) condition.getValue()[0] + "%");
				} else if (condition.getRestriction() == Restriction.FIRST) {
					query.setFirstResult((Integer) condition.getValue()[0]);
				} else if (condition.getRestriction() == Restriction.MAX) {
					query.setMaxResults((Integer) condition.getValue()[0]);
				} else if (strCondition.equals("startDate")) {
					String tempstartdate = null;
					if (StringUtil.nullToBlank(conditionMap.get("period"))
							.equals(CommonConstants.DateType.PERIOD.getCode())) {
						tempstartdate = condition.getValue()[0].toString();
					} else {
						tempstartdate = condition.getValue()[0].toString() + "000000";
					}
					query.setParameter("startDate", tempstartdate);
				} else if (strCondition.equals("endDate")) {
					String tempEndDate = null;
					if (StringUtil.nullToBlank(conditionMap.get("period"))
							.equals(CommonConstants.DateType.PERIOD.getCode())) {
						tempEndDate = condition.getValue()[0].toString();
					} else {
						tempEndDate = condition.getValue()[0].toString() + "235959";
					}
					query.setParameter("endDate", tempEndDate);
				}
			}
		} // End of if (conditions != null)

		/**
		 * paging 로직 추가.
		 */
		query = CommonUtils2.addPagingForQuery(query, conditionMap);

		List<Object[]> result = query.list();

		List<EventAlertLogVO> eventAlertLogList = new ArrayList<EventAlertLogVO>();
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
			eventAlertLogVO.setLocation(resultData[2] == null ? "" : resultData[2].toString());
			eventAlertLogVO.setActivatorId(resultData[3].toString());
			if (resultData[4] != null) {
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

			// EventAlert.id
			if (resultData[12] != null) {
				eventAlertLogVO.setEventAlertId(resultData[12].toString());
			}
			// EventAlert.name
			if (resultData[13] != null) {
				eventAlertLogVO.setEventAlertName(resultData[13].toString());
			}

			if (resultData[14] != null) {
				eventAlertLogVO.setEventLogId(resultData[14].toString());
			}

			eventAlertLogList.add(eventAlertLogVO);
		}

		return eventAlertLogList;
	}

	/**
	 * @desc EventAlertLogHistory Total Cnt fetch DAO
	 */
	public String getEventAlertLogHistoryTotalCnt(Set<Condition> conditions, List<Integer> locations) {

		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT count(*)  ");
		hqlBuf.append(" FROM EventAlertLog ea JOIN ea.eventAlert e");
		hqlBuf.append("                       JOIN ea.supplier s");
		hqlBuf.append("                       LEFT JOIN ea.location l");

		if (conditions != null) {
			boolean whereFlag = false;
			boolean flag = false;
			Iterator it = conditions.iterator();

			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
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
					// hqlBuf.append(" (s.id = :supplierId or s.id is null) ");
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
		// hqlBuf.append(" ORDER BY ea.openTime DESC");

		Query query = getSession().createQuery(hqlBuf.toString());

		/**
		 * @desc query set Parameters..
		 */
		if (conditions != null) {
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
				String strCondition = condition.getField();

				if (strCondition.equals("supplier.id")) {
					int tempIntValue = (Integer) condition.getValue()[0];
					query.setParameter("supplierId", tempIntValue);
				} else if (strCondition.equals("eventAlertType")) {
					query.setParameter("eventAlertType", EventAlertType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("severity")) {
					query.setParameter("severity", SeverityType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("eventAlert.id")) {
					query.setParameter("eventAlert", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("status")) {
					query.setParameter("status", EventStatus.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("activatorType")) {
					query.setParameter("activatorType", TargetClass.valueOf((String) condition.getValue()[0]));// jhkim
				} else if (strCondition.equals("activatorId")) {
					query.setParameter("activatorId", (String) condition.getValue()[0]);
				} else if (strCondition.equals("location.id")) {
					query.setParameterList("location", locations);
				} else if (strCondition.equals("message")) {
					query.setParameter("message", "%" + (String) condition.getValue()[0] + "%");
				} else if (condition.getRestriction() == Restriction.FIRST) {
					query.setFirstResult((Integer) condition.getValue()[0]);
				} else if (condition.getRestriction() == Restriction.MAX) {
					query.setMaxResults((Integer) condition.getValue()[0]);
				} else if (strCondition.equals("startDate")) {
					String tempstartdate = condition.getValue()[0].toString() + "000000";
					query.setParameter("startDate", tempstartdate);
				} else if (strCondition.equals("endDate")) {
					String tempEndDate = condition.getValue()[0].toString() + "235959";
					query.setParameter("endDate", tempEndDate);
				}
			}
		} // End of if (conditions != null)

		Object object = query.uniqueResult();

		return object.toString();
	}

	public List<EventAlertLogVO> getEventAlertLogHistoryExcel(Set<Condition> conditions, List<Integer> locations) {

		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(
				" SELECT e.eventAlertType, ea.message, l.name, ea.activatorId, ea.activatorIp, ea.status, ea.activatorType, ");
		hqlBuf.append("        ea.writeTime, ea.openTime, ea.closeTime, ea.duration, e.severity");
		hqlBuf.append(" FROM EventAlertLog ea join ea.eventAlert e");
		// hqlBuf.append(" left join ea.supplier s");
		hqlBuf.append("                       join ea.supplier s");
		hqlBuf.append("                       left join ea.location l");

		if (conditions != null) {
			boolean whereFlag = false;
			boolean flag = false;
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
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

		Query query = getSession().createQuery(hqlBuf.toString());

		if (conditions != null) {
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
				String strCondition = condition.getField();

				if (strCondition.equals("supplier.id")) {
					query.setParameter("supplierId", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("eventAlertType")) {
					query.setParameter("eventAlertType",
							CommonConstants.EventAlertType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("severity")) {
					query.setParameter("severity",
							CommonConstants.SeverityType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("eventAlert.id")) {
					query.setParameter("eventAlert", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("status")) {
					query.setParameter("status", CommonConstants.EventStatus.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("activatorType")) {
					query.setParameter("activatorType", TargetClass.valueOf((String) condition.getValue()[0]));// jhkim
				} else if (strCondition.equals("activatorId")) {
					query.setParameter("activatorId", (String) condition.getValue()[0]);
				} else if (strCondition.equals("location.id")) {
					query.setParameterList("location", locations);
				} else if (strCondition.equals("message")) {
					query.setParameter("message", "%" + (String) condition.getValue()[0] + "%");
				} else if (strCondition.equals("startDate")) {
					query.setParameter("startDate", condition.getValue()[0].toString() + "000000");
				} else if (strCondition.equals("endDate")) {
					query.setParameter("endDate", condition.getValue()[0].toString() + "235959");
				}
			}
		}

		List result = query.list();

		List<EventAlertLogVO> eventAlertLogVOs = new ArrayList<EventAlertLogVO>();
		EventAlertLogVO eventAlertLogVO = null;

		for (int i = 0; i < result.size(); i++) {
			eventAlertLogVO = new EventAlertLogVO();

			Object[] resultData = (Object[]) result.get(i);
			eventAlertLogVO.setRank(Integer.toString(i + 1));
			eventAlertLogVO.setType(resultData[0].toString().substring(0));
			if (resultData[1] != null) {
				eventAlertLogVO.setMessage(resultData[1].toString());
			} else {
				eventAlertLogVO.setMessage("");
			}
			eventAlertLogVO.setLocation(resultData[2] == null ? "" : resultData[2].toString());
			eventAlertLogVO.setActivatorId(resultData[3].toString());
			if (resultData[4] != null) {
				eventAlertLogVO.setActivatorIp(resultData[4].toString());
			} else {
				eventAlertLogVO.setActivatorIp("");
			}

			eventAlertLogVO.setStatus(resultData[5].toString());
			eventAlertLogVO.setActivatorType(resultData[6].toString());
			eventAlertLogVO.setWriteTime(resultData[7].toString());
			eventAlertLogVO.setOpenTime(resultData[8].toString());
			if (resultData[9] != null) {
				eventAlertLogVO.setCloseTime(resultData[9].toString());
			} else {
				eventAlertLogVO.setCloseTime("");
			}
			if (resultData[10] != null) {
				eventAlertLogVO.setDuration(resultData[10].toString());
			} else {
				eventAlertLogVO.setDuration("");
			}
			if (resultData[11] != null) {
				eventAlertLogVO.setSeverity(resultData[11].toString());
			} else {
				eventAlertLogVO.setSeverity("");
			}
			eventAlertLogVOs.add(eventAlertLogVO);

		}

		return eventAlertLogVOs;
	}

	public List<EventAlertLogVO> getEventAlertLogHistoryCount(Set<Condition> conditions) {

		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(
				" SELECT e.eventAlertType, ea.message, l.name, ea.activatorId, ea.activatorIp, ea.status, ea.activatorType, ");
		hqlBuf.append("        ea.writeTime, ea.openTime, ea.closeTime, ea.duration, e.severity");
		hqlBuf.append(" FROM EventAlertLog ea join ea.eventAlert e");
		hqlBuf.append("                       left join ea.supplier s");
		hqlBuf.append("                       left join ea.location l");

		if (conditions != null) {
			boolean whereFlag = false;
			boolean flag = false;
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
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

		Query query = getSession().createQuery(hqlBuf.toString());

		if (conditions != null) {
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
				String strCondition = condition.getField();

				if (strCondition.equals("supplier.id")) {
					query.setParameter("supplierId", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("eventAlertType")) {
					query.setParameter("eventAlertType",
							CommonConstants.EventAlertType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("severity")) {
					query.setParameter("severity",
							CommonConstants.SeverityType.valueOf((String) condition.getValue()[0]));
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
					query.setParameter("message", "%" + (String) condition.getValue()[0] + "%");
				} else if (strCondition.equals("startDate")) {
					query.setParameter("startDate", condition.getValue()[0].toString() + "000000");
				} else if (strCondition.equals("endDate")) {
					query.setParameter("endDate", condition.getValue()[0].toString() + "235959");
				}
			}
		}

		List result = query.list();

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
			eventAlertLogVO.setLocation(resultData[2] == null ? "" : resultData[2].toString());
			eventAlertLogVO.setActivatorId(resultData[3].toString());
			if (resultData[4] != null) {
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

	public List<EventAlertLogVO> getEventAlertLogHistoryCount(Set<Condition> conditions, List<Integer> locations) {

		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(
				" SELECT e.eventAlertType, ea.message, l.name, ea.activatorId, ea.activatorIp, ea.status, ea.activatorType, ");
		hqlBuf.append("        ea.writeTime, ea.openTime, ea.closeTime, ea.duration, e.severity");
		hqlBuf.append(" FROM EventAlertLog ea join ea.eventAlert e");
		hqlBuf.append("                       left join ea.supplier s");
		hqlBuf.append("                       left join ea.location l");

		if (conditions != null) {
			boolean whereFlag = false;
			boolean flag = false;
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
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

		Query query = getSession().createQuery(hqlBuf.toString());

		if (conditions != null) {
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
				String strCondition = condition.getField();

				if (strCondition.equals("supplier.id")) {
					query.setParameter("supplierId", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("eventAlertType")) {
					query.setParameter("eventAlertType",
							CommonConstants.EventAlertType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("severity")) {
					query.setParameter("severity",
							CommonConstants.SeverityType.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("eventAlert.id")) {
					query.setParameter("eventAlert", (Integer) condition.getValue()[0]);
				} else if (strCondition.equals("status")) {
					query.setParameter("status", CommonConstants.EventStatus.valueOf((String) condition.getValue()[0]));
				} else if (strCondition.equals("activatorType")) {
					query.setParameter("activatorType", TargetClass.valueOf((String) condition.getValue()[0]));// jhkim
				} else if (strCondition.equals("activatorId")) {
					query.setParameter("activatorId", (String) condition.getValue()[0]);
				} else if (strCondition.equals("location.id")) {
					query.setParameterList("location", locations);
				} else if (strCondition.equals("message")) {
					query.setParameter("message", "%" + (String) condition.getValue()[0] + "%");
				} else if (strCondition.equals("startDate")) {
					query.setParameter("startDate", condition.getValue()[0].toString() + "000000");
				} else if (strCondition.equals("endDate")) {
					query.setParameter("endDate", condition.getValue()[0].toString() + "235959");
				}
			}
		}

		List result = query.list();

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
			eventAlertLogVO.setLocation(resultData[2] == null ? "" : resultData[2].toString());
			eventAlertLogVO.setActivatorId(resultData[3].toString());
			if (resultData[4] != null) {
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
	public List<Map<String, Object>> getEventAlertLogByActivatorType(Map<String, Object> conditionMap) {

		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
		String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
		String eventAlertType = StringUtil.nullToBlank(conditionMap.get("eventAlertType"));
		String severity = StringUtil.nullToBlank(conditionMap.get("severity"));
		Integer eventAlertClass = (Integer) conditionMap.get("eventAlertClass");
		String status = StringUtil.nullToBlank(conditionMap.get("status"));
		String activatorType = StringUtil.nullToBlank(conditionMap.get("activatorType"));
		String activatorId = StringUtil.nullToBlank(conditionMap.get("activatorId"));
		String message = StringUtil.nullToBlank(conditionMap.get("message"));
		List<Integer> locationIdList = (List<Integer>) conditionMap.get("locationIdList");

		if (!searchStartDate.isEmpty()) {
			searchStartDate = searchStartDate + "000000";
			searchEndDate = searchEndDate + "235959";
		} else {
			try {
				searchStartDate = TimeUtil.getCurrentDay() + "000000";
				searchEndDate = TimeUtil.getCurrentDay() + "235959";
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append("\nSELECT ea.activatorType AS type, ");
		sb.append("\n       COUNT(ea.id) AS value ");
		sb.append("\nFROM EventAlertLog ea ");
		sb.append("\n     JOIN ea.eventAlert e ");
		sb.append("\n     JOIN ea.supplier s ");

		if (locationIdList != null && locationIdList.size() > 0) {
			sb.append("\n     JOIN ea.location l ");
		}
		sb.append("\nWHERE s.id = :supplierId ");
		sb.append("\nAND   ea.openTime between :startDate AND :endDate ");

		if (!eventAlertType.isEmpty()) {
			sb.append("\nAND   e.eventAlertType = :eventAlertType ");
		}
		if (!severity.isEmpty()) {
			sb.append("\nAND   e.severity = :severity ");
		}
		if (eventAlertClass != null) {
			sb.append("\nAND   e.id = :eventAlert ");
		}
		if (!status.isEmpty()) {
			sb.append("\nAND   ea.status = :status ");
		}
		if (!activatorType.isEmpty()) {
			sb.append("\nAND   ea.activatorType = :activatorType ");
		}
		if (!activatorId.isEmpty()) {
			sb.append("\nAND   ea.activatorId = :activatorId ");
		}
		if (locationIdList != null && locationIdList.size() > 0) {
			sb.append("\nAND   l.id IN (:locationIdList) ");
		}
		if (!message.isEmpty()) {
			sb.append("\nAND   ea.message LIKE :message ");
		}

		sb.append("\nGROUP BY ea.activatorType");

		Query query = getSession().createQuery(sb.toString());

		query.setInteger("supplierId", supplierId);
		query.setString("startDate", searchStartDate);
		query.setString("endDate", searchEndDate);

		if (!eventAlertType.isEmpty()) {
			query.setParameter("eventAlertType", EventAlertType.valueOf(eventAlertType));
		}
		if (!severity.isEmpty()) {
			query.setParameter("severity", SeverityType.valueOf(severity));
		}
		if (eventAlertClass != null) {
			query.setInteger("eventAlert", eventAlertClass);
		}
		if (!status.isEmpty()) {
			query.setParameter("status", EventStatus.valueOf(status));
		}
		if (!activatorType.isEmpty()) {
			query.setParameter("activatorType", TargetClass.valueOf(activatorType));
		}
		if (!activatorId.isEmpty()) {
			query.setString("activatorId", activatorId);
		}
		if (locationIdList != null && locationIdList.size() > 0) {
			query.setParameterList("locationIdList", locationIdList);
		}
		if (!message.isEmpty()) {
			query.setString("message", "%" + message + "%");
		}

		List<Map<String, Object>> result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		// List<EventAlertLogSummaryVO> eventAlertLogSummaryVOs = new
		// ArrayList<EventAlertLogSummaryVO>();
		// EventAlertLogSummaryVO eventAlertLogSummaryVO = null;
		//
		// for (int i = 0; i < result.size(); i++) {
		// eventAlertLogSummaryVO = new EventAlertLogSummaryVO();
		//
		// Object[] resultData = (Object[]) result.get(i);
		// eventAlertLogSummaryVO.setType(resultData[0].toString());
		// eventAlertLogSummaryVO.setValue(Integer.parseInt(resultData[1].toString()));
		//
		// eventAlertLogSummaryVOs.add(eventAlertLogSummaryVO);
		// }
		// return eventAlertLogSummaryVOs;
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getEventAlertLogByMessage(Map<String, Object> conditionMap) {
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate"));
		String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate"));
		String eventAlertType = StringUtil.nullToBlank(conditionMap.get("eventAlertType"));
		String severity = StringUtil.nullToBlank(conditionMap.get("severity"));
		Integer eventAlertClass = (Integer) conditionMap.get("eventAlertClass");
		String status = StringUtil.nullToBlank(conditionMap.get("status"));
		String activatorType = StringUtil.nullToBlank(conditionMap.get("activatorType"));
		String activatorId = StringUtil.nullToBlank(conditionMap.get("activatorId"));
		String message = StringUtil.nullToBlank(conditionMap.get("message"));
		List<Integer> locationIdList = (List<Integer>) conditionMap.get("locationIdList");

		if (!searchStartDate.isEmpty()) {
			searchStartDate = searchStartDate + "000000";
			searchEndDate = searchEndDate + "235959";
		} else {
			try {
				searchStartDate = TimeUtil.getCurrentDay() + "000000";
				searchEndDate = TimeUtil.getCurrentDay() + "235959";
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append("\nSELECT e.name AS type, ");
		sb.append("\n       COUNT(ea.message) AS value ");
		sb.append("\nFROM EventAlertLog ea ");
		sb.append("\n     JOIN ea.eventAlert e ");
		sb.append("\n     JOIN ea.supplier s ");

		if (locationIdList != null && locationIdList.size() > 0) {
			sb.append("\n     JOIN ea.location l ");
		}
		sb.append("\nWHERE s.id = :supplierId ");
		sb.append("\nAND   ea.openTime between :startDate AND :endDate ");

		if (!eventAlertType.isEmpty()) {
			sb.append("\nAND   e.eventAlertType = :eventAlertType ");
		}
		if (!severity.isEmpty()) {
			sb.append("\nAND   e.severity = :severity ");
		}
		if (eventAlertClass != null) {
			sb.append("\nAND   e.id = :eventAlert ");
		}
		if (!status.isEmpty()) {
			sb.append("\nAND   ea.status = :status ");
		}
		if (!activatorType.isEmpty()) {
			sb.append("\nAND   ea.activatorType = :activatorType ");
		}
		if (!activatorId.isEmpty()) {
			sb.append("\nAND   ea.activatorId = :activatorId ");
		}
		if (locationIdList != null && locationIdList.size() > 0) {
			sb.append("\nAND   l.id IN (:locationIdList) ");
		}
		if (!message.isEmpty()) {
			sb.append("\nAND   ea.message LIKE :message ");
		}

		sb.append("\nGROUP BY e.name");

		Query query = getSession().createQuery(sb.toString());

		query.setInteger("supplierId", supplierId);
		query.setString("startDate", searchStartDate);
		query.setString("endDate", searchEndDate);

		if (!eventAlertType.isEmpty()) {
			query.setParameter("eventAlertType", EventAlertType.valueOf(eventAlertType));
		}
		if (!severity.isEmpty()) {
			query.setParameter("severity", SeverityType.valueOf(severity));
		}

		if (eventAlertClass != null) {
			query.setInteger("eventAlert", eventAlertClass);
		}
		if (!status.isEmpty()) {
			query.setParameter("status", EventStatus.valueOf(status));
		}
		if (!activatorType.isEmpty()) {
			query.setParameter("activatorType", TargetClass.valueOf(activatorType));
		}
		if (!activatorId.isEmpty()) {
			query.setString("activatorId", activatorId);
		}
		if (locationIdList != null && locationIdList.size() > 0) {
			query.setParameterList("locationIdList", locationIdList);
		}
		if (!message.isEmpty()) {
			query.setString("message", "%" + message + "%");
		}

		List<Map<String, Object>> result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		// List<EventAlertLogSummaryVO> eventAlertLogSummaryVOs = new
		// ArrayList<EventAlertLogSummaryVO>();
		// EventAlertLogSummaryVO eventAlertLogSummaryVO = null;
		// Object[] resultData = null;
		//
		// for (int i = 0; i < result.size(); i++) {
		// eventAlertLogSummaryVO = new EventAlertLogSummaryVO();
		//
		// resultData = result.get(i);
		// eventAlertLogSummaryVO.setType(resultData[0].toString());
		// eventAlertLogSummaryVO.setValue(Integer.parseInt(resultData[1].toString()));
		//
		// eventAlertLogSummaryVOs.add(eventAlertLogSummaryVO);
		// }
		// return eventAlertLogSummaryVOs;
		return result;
	}

	public List<EventAlertLogVO> getEventAlertLogRealTimeForMini(Set<Condition> conditions, String searchStartDate,
			String searchEndDate) {

		List<EventAlertLogVO> result = getEventAlertLogRealTime(conditions, searchStartDate, searchEndDate);
		List<EventAlertLogVO> eventAlertLogVOs = new ArrayList<EventAlertLogVO>();

		if (result.size() > 5) {
			for (int i = 0; i < 5; i++) {
				eventAlertLogVOs.add(result.get(i));
			}
		} else {
			eventAlertLogVOs = result;
		}

		return eventAlertLogVOs;
	}

	// public List<EventAlertLogSummaryVO>
	// getEventAlertLogByActivatorTypeForMini(String[] conditions) {
	// List<EventAlertLogSummaryVO> result =
	// getEventAlertLogByActivatorType(conditions);
	// List<EventAlertLogSummaryVO> eventAlertLogVOs = new
	// ArrayList<EventAlertLogSummaryVO>();
	//
	// if (result.size() > 5) {
	// for (int i = 0; i < 5; i++) {
	// eventAlertLogVOs.add(result.get(i));
	// }
	// } else {
	// eventAlertLogVOs = result;
	// }
	//
	// return eventAlertLogVOs;
	// }

	// public List<EventAlertLogSummaryVO>
	// getEventAlertLogByMessageForMini(String[] conditions) {
	// List<EventAlertLogSummaryVO> result =
	// getEventAlertLogByMessage(conditions);
	// List<EventAlertLogSummaryVO> eventAlertLogVOs = new
	// ArrayList<EventAlertLogSummaryVO>();
	//
	// if (result.size() > 5) {
	// for (int i = 0; i < 5; i++) {
	// eventAlertLogVOs.add(result.get(i));
	// }
	// } else {
	// eventAlertLogVOs = result;
	// }
	//
	// return eventAlertLogVOs;
	// }

	public String getEventAlertLogCount(Map<String, String> map) {

		String startDate = map.get("startDate");
		String endDate = map.get("endDate");
		String mcuId = map.get("mcuId");

		StringBuilder sb = new StringBuilder().append(" SELECT count(log) ").append("   FROM EventAlertLog log ")
				.append("  WHERE log.openTime >= :startDate ").append("    AND log.openTime <= :endDate   ")
				.append("    AND log.activatorType = :activatorType ")
				.append("    AND log.activatorId = :activatorId  ");

		Query query = getSession().createQuery(sb.toString());
		query.setString("startDate", startDate);
		query.setString("endDate", endDate);
		query.setString("activatorType", TargetClass.DCU.name());
		query.setString("activatorId", mcuId);

		return query.uniqueResult().toString();
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	public List<Map<String, String>> getEventAlertLogs(Map<String, String> map) {

		String startDate = map.get("startDate");
		String endDate = map.get("endDate");
		String mcuId = map.get("mcuId");
		int page = Integer.parseInt(map.get("page"));
		int pageSize = Integer.parseInt(map.get("pageSize"));
		int firstResult = page * pageSize;

		StringBuilder sb = new StringBuilder()
				.append(" SELECT log.severity as severity, log.message as message, log.location.name as locationName, ")
				.append("        log.openTime as openTime, log.closeTime as closeTime, log.duration as duration ")
				.append("   FROM EventAlertLog log ").append("  WHERE log.openTime >= :startDate ")
				.append("    AND log.openTime <= :endDate   ").append("    AND log.activatorType = :activatorType ")
				.append("    AND log.activatorId = :activatorId  ");

		Query query = getSession().createQuery(sb.toString());
		query.setString("startDate", startDate);
		query.setString("endDate", endDate);
		query.setString("activatorType", TargetClass.DCU.name());
		query.setString("activatorId", mcuId);

		query.setFirstResult(firstResult);
		query.setMaxResults(pageSize);

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	/**
	 * method name : getMcuEventAlertLogList<b/> method Desc : Concentrator
	 * Management 맥스가젯 History 탭에서 장애내역을 조회한다.
	 *
	 * @param conditionMap
	 * @param isCount
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMcuEventAlertLogList(Map<String, Object> conditionMap, boolean isCount) {
		List<Map<String, Object>> result;
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		Integer page = (Integer) conditionMap.get("page");
		Integer limit = (Integer) conditionMap.get("limit");
		String mcuId = StringUtil.nullToBlank(conditionMap.get("mcuId"));
		String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
		String endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
		StringBuilder sb = new StringBuilder();

		if (isCount) {
			sb.append("\nSELECT COUNT(*) AS cnt ");
		} else {
			sb.append("\nSELECT log.severity AS severity, ");
			sb.append("\n       log.message AS message, ");
			sb.append("\n       log.location.name AS locationName, ");
			sb.append("\n       log.openTime AS openTime, ");
			sb.append("\n       log.closeTime AS closeTime, ");
			sb.append("\n       log.duration AS duration ");
		}
		sb.append("\nFROM EventAlertLog log ");
		sb.append("\nWHERE log.supplierId = :supplierId ");
		sb.append("\nAND   log.activatorType = :activatorType ");
		sb.append("\nAND   log.activatorId = :activatorId ");
		sb.append("\nAND   log.openTime BETWEEN :startDate AND :endDate ");
		if (!isCount) {
			sb.append("\nORDER BY log.openTime DESC ");
		}

		Query query = getSession().createQuery(sb.toString());
		query.setInteger("supplierId", supplierId);
		query.setString("activatorType", TargetClass.DCU.name());
		query.setString("activatorId", mcuId);
		query.setString("startDate", startDate);
		query.setString("endDate", endDate);

		if (isCount) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("total", ((Number) query.uniqueResult()).intValue());
			result = new ArrayList<Map<String, Object>>();
			result.add(map);
		} else {
			if (page != null && limit != null) {
				query.setFirstResult((page - 1) * limit);
				query.setMaxResults(limit);
			}
			result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		}

		return result;
	}

	/**
	 * method name : getEventAlertLogFromDB<b/> method Desc : MDIS.
	 * EventAlertLog 가젯에서 RealTime 데이터를 조회한다.
	 *
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getEventAlertLogFromDB(Map<String, Object> conditionMap) {
		Integer supplierId = (Integer) conditionMap.get("supplierId");
		String searchStartDateTime = (String) conditionMap.get("searchStartDateTime");
		String searchEndDateTime = (String) conditionMap.get("searchEndDateTime");

		StringBuilder sb = new StringBuilder();

		sb.append("\nSELECT log.activatorId AS activatorId, ");
		sb.append("\n       log.activatorType AS activatorType, ");
		sb.append("\n       log.activatorIp AS activatorIp, ");
		sb.append("\n       log.status AS status, ");
		sb.append("\n       log.message AS eventMessage, ");
		sb.append("\n       loc.name AS location, ");
		sb.append("\n       log.openTime AS openTimeValue, ");
		sb.append("\n       log.openTime AS openTime, ");
		sb.append("\n       log.closeTime AS closeTime, ");
		sb.append("\n       log.duration AS duration ");
		sb.append("\nFROM EventAlertLog log ");
		sb.append("\n     LEFT OUTER JOIN ");
		sb.append("\n     log.location loc ");
		sb.append("\nWHERE log.supplier.id = :supplierId ");
		sb.append("\nAND   log.writeTime > :searchStartDateTime ");
		sb.append("\nAND   log.writeTime <= :searchEndDateTime ");
		sb.append("\nORDER BY openTime DESC ");

		Query query = getSession().createQuery(sb.toString());
		query.setInteger("supplierId", supplierId);
		query.setString("searchStartDateTime", searchStartDateTime);
		query.setString("searchEndDateTime", searchEndDateTime);

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@Override
	public long findTotalByConditions(Set<Condition> conditions) {
		List<Object> ret = findTotalCountByConditions(conditions);
		Long f = (Long) ret.get(0);
		return f;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMcuLogType(Map<String, Object> conditionMap) {
		String activator_type = (String) conditionMap.get("activator_type");
		String activatorId = (String) conditionMap.get("activatorId");
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("\nSELECT log.openTime ");
		sb.append("\nFROM EventAlertLog log");
		sb.append("\nWHERE activatorType = :activator_type ");
		sb.append("\nAND    activatorId = :activatorId ");
		sb.append("\nORDER BY log.openTime DESC ");
		
		Query query = getSession().createQuery(sb.toString());
		query.setString("activator_type", activator_type);
		query.setString("activatorId", activatorId);

		return query.list();
	}
	
	// INSERT START SP-818
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>>  getProblematicMetersEvent(Map<String, Object> conditionMap) {
        String supplierId = StringUtil.nullToBlank(conditionMap.get("supplierId"));
        String locationName = StringUtil.nullToBlank(conditionMap.get("locationName"));
		String searchStartDate = StringUtil.nullToBlank(conditionMap.get("searchStartDate")); //YYYYMMDD
		String searchEndDate = StringUtil.nullToBlank(conditionMap.get("searchEndDate")); //YYYYMMDD
		String message = StringUtil.nullToBlank(conditionMap.get("message"));

		if (!searchStartDate.isEmpty()) {
			searchStartDate = searchStartDate + "000000";
			searchEndDate = searchEndDate + "235959";
		} else {
			try {
				searchStartDate = TimeUtil.getCurrentDay() + "000000";
				searchEndDate = TimeUtil.getCurrentDay() + "235959";
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append("\nSELECT ev.activatorId, ");
		sb.append("\n       COUNT(ev.activatorId) AS idcount, ");
		sb.append("\n       MAX(ev.openTime) AS lastdate, ");
		sb.append("\n       ev.message ");
		sb.append("\nFROM EventAlertLog ev");
		sb.append("\n     LEFT OUTER JOIN ev.location lo ");
		sb.append("\nWHERE  ");
		sb.append("\n ev.openTime between :startDate AND :endDate ");

		if (!"".equals(supplierId)) {
        	sb.append("\n AND ev.supplier.id = :supplierId ");
        }
        if (!"".equals(locationName)) {
        	sb.append("\n AND lo.name = :locationName ");
        }
		if (!message.isEmpty()) {
			sb.append("\n AND   ev.message LIKE :message ");
		}
		
		sb.append("\nGROUP BY ev.activatorId, ev.message");

		Query query = getSession().createQuery(sb.toString());

		query.setString("startDate", searchStartDate);
		query.setString("endDate", searchEndDate);
        if (!"".equals(supplierId)) {
            query.setInteger("supplierId", Integer.parseInt(supplierId));
        }
        if (!"".equals(locationName)) {
        	query.setString("locationName", locationName);
        }		
		if (!message.isEmpty()) {
			query.setString("message", "%" + message + "%");
		}

	    Integer start = 0;	    
    	query.setFirstResult(start.intValue());
    	
        List<Object> result = query.list();
//    	System.out.print(query.toString());
//        System.out.print("result count = " + result.size());

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();        
        for (Object obj : result) {
            HashMap<String, Object> resultMap = new HashMap<String, Object>();
            Object[] objs = (Object[]) obj;
            resultMap.put("activatorId" , objs[0] == null ?  null : (String)objs[0]);
            resultMap.put("idCount", objs[1] == null ?  null : ((Number) objs[1]).intValue());
            resultMap.put("lastDate", objs[2] == null ?  null : (String) objs[2]);
            resultMap.put("message", objs[3] == null ?  null :(String) objs[3]);
            resultList.add(resultMap);
        }
        
        return resultList;
	}
	// INSERT END SP-818

}