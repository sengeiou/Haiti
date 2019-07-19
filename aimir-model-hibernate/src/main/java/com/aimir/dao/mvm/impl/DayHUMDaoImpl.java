package com.aimir.dao.mvm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.DayHUMDao;
import com.aimir.model.mvm.DayHUM;
import com.aimir.util.Condition;
import com.aimir.util.SearchCondition;

@Repository(value = "dayhumDao")
@SuppressWarnings("unchecked")
public class DayHUMDaoImpl extends AbstractHibernateGenericDao<DayHUM, Integer>
		implements DayHUMDao {

	private static Log logger = LogFactory.getLog(DayHUMDaoImpl.class);

	@Autowired
	protected DayHUMDaoImpl(SessionFactory sessionFactory) {
		super(DayHUM.class);
		super.setSessionFactory(sessionFactory);
	}

	public List<DayHUM> getDayHUMsByMap(Map map) {
	    Criteria criteria = getSession().createCriteria(DayHUM.class);
		criteria.add(Restrictions.allEq(map));

		return criteria.list();
	}

	public List<DayHUM> getDayHUMsByList(List<Map> list) {
		Criteria criteria = getSession().createCriteria(DayHUM.class);
		for (Map map : list) {
			if (map.get("type").equals("eq")) {
				criteria.add(Restrictions.eq((String) map.get("key"), map
						.get("value")));
			} else if (map.get("type").equals("like")) {
				criteria.add(Restrictions.like((String) map.get("key"), map
						.get("value")));
			} else if (map.get("type").equals("in")) {
				criteria.add(Restrictions.in((String) map.get("key"),
						(Object[]) map.get("value")));
			} else if (map.get("type").equals("isNull")) {
				criteria.add(Restrictions.isNull((String) map.get("key")));
			} else if (map.get("type").equals("isNotNull")) {
				criteria.add(Restrictions.isNotNull((String) map.get("key")));
			}
		}
		return criteria.list();
	}

	public List<DayHUM> getDayHUMsByListCondition(Set<Condition> list) {

		return findByConditions(list);
	}

	public int getAvgGroupByListCondition(Set<Condition> conditions) {
		Criteria criteria = getSession().createCriteria(DayHUM.class);
		if (conditions != null) {
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
				Criterion addCriterion = SearchCondition
						.getCriterion(condition);

				if (addCriterion != null) {
					criteria.add(addCriterion);
				}
			}

		}
		criteria.setProjection(Projections.projectionList().add(
				Projections.avg("avgValue")).add(
				Projections.property("id.meter")).add(
				Projections.groupProperty("id.contract")).add(
				Projections.groupProperty("id.meter")));

		return criteria.list().size();
	}

	public List<Object> getDayHUMsCountByListCondition(Set<Condition> set) {

		return findTotalCountByConditions(set);
	}

	public List<Object> getDayHUMsMaxMinAvg(Set<Condition> conditions,
			String div) {

		Criteria criteria = getSession().createCriteria(DayHUM.class);

		if (conditions != null) {
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
				Criterion addCriterion = SearchCondition
						.getCriterion(condition);

				if (addCriterion != null) {
					criteria.add(addCriterion);
				}
			}
		}

		ProjectionList pjl = Projections.projectionList();

		if ("max".equals(div)) {
			pjl.add(Projections.max("maximumValue"));
		} else if ("min".equals(div)) {
			pjl.add(Projections.min("minimumValue"));
		} else if ("avg".equals(div)) {
			pjl.add(Projections.avg("avgValue"));
		}

		criteria.setProjection(pjl);
		return criteria.list();

	}

	public List<Object> getDayHUMsAvgList(Set<Condition> conditions) {

		Criteria criteria = getSession().createCriteria(DayHUM.class);

		if (conditions != null) {
			Iterator it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
				Criterion addCriterion = SearchCondition
						.getCriterion(condition);

				if (addCriterion != null) {
					criteria.add(addCriterion);
				}
			}
		}

		criteria.setProjection(Projections.projectionList().add(
				Projections.avg("avg")).add(
				Projections.groupProperty("location.id")));

		List<Object> result = new ArrayList<Object>();
		HashMap<Object, Object> hm = new HashMap<Object, Object>();
		Iterator it = criteria.list().iterator();
		int idx = 0;
		while (it.hasNext()) {
			Object[] objVal = (Object[]) it.next();
			hm.put(((Number) objVal[1]).intValue(), ((Number) objVal[0]).doubleValue());
			result.add(hm);
			idx++;
		}

		return result;

	}

	/*
	 * BEMS 특정일의 시간별 습도 변화량 차트 일 최대/일 최소/ 시간별 데이터 조회.
	 */
	public List<Object> getConsumptionHumMonitoring(
			Map<String, Object> condition) {

		logger.info("BEMS 특정일의 시간별 습도 변화량  차트 일 최대/일 최소/ 시간별 데이터 조회.\n==== conditions ====\n"	+ condition);

		Integer locationId = ((Number) condition.get("locationId")).intValue();
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		StringBuffer sb = new StringBuffer();

		sb.append("\n 		SELECT LL.PARENT_ID AS HUM_PARENT_ID  ");
		sb.append("\n 		     , D.YYYYMMDD  ");
		sb.append("\n 		     , D.AVGVALUE AS HUM_AVGVALUE  ");
		sb.append("\n 		     , D.MAXIMUMVALUE AS HUM_MAXVALUE  ");
		sb.append("\n 		     , D.MINIMUMVALUE AS HUM_MINVALUE  ");
		sb.append("\n 			 , D.VALUE_00 AS HUM_00 , D.VALUE_01 AS HUM_01 , D.VALUE_02 AS HUM_02 , D.VALUE_03 AS HUM_03 , D.VALUE_04 AS HUM_04  ");
		sb.append("\n 			 , D.VALUE_05 AS HUM_05 , D.VALUE_06 AS HUM_06 , D.VALUE_07 AS HUM_07 , D.VALUE_08 AS HUM_08 , D.VALUE_09 AS HUM_09  ");
		sb.append("\n 			 , D.VALUE_10 AS HUM_10 , D.VALUE_11 AS HUM_11 , D.VALUE_12 AS HUM_12 , D.VALUE_13 AS HUM_13 , D.VALUE_14 AS HUM_14  ");
		sb.append("\n 			 , D.VALUE_15 AS HUM_15 , D.VALUE_16 AS HUM_16 , D.VALUE_17 AS HUM_17 , D.VALUE_18 AS HUM_18 , D.VALUE_19 AS HUM_19  ");
		sb.append("\n 			 , D.VALUE_20 AS HUM_20 , D.VALUE_21 AS HUM_21 , D.VALUE_22 AS HUM_22 , D.VALUE_23 AS HUM_23 ");
		sb.append("\n 		FROM DAY_HUM D , (  ");
		sb.append("\n 			SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID FROM METER M , (   ");
		sb.append("\n 	 				SELECT ID , NAME , PARENT_ID FROM LOCATION WHERE ID = :locationId ");
		sb.append("\n 	 		) L WHERE M.LOCATION_ID = L.ID  ");
//		sb.append("\n 		   			 AND M.METERTYPE_ID = (  ");
//		sb.append("\n 		   					SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
//		sb.append("\n 		   									SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' "); // MeterType
//		sb.append("\n 		   					) AND CC.CODE = '1.3.1.2' "); // WaterMeter 습도 코드로 수정할것
//		sb.append("\n 		   				) ");
		sb.append("\n 		)LL WHERE D.MDEV_ID = LL.MDS_ID  ");
		sb.append("\n 		 AND D.YYYYMMDD >= :startDate    ");
		sb.append("\n 		 AND D.YYYYMMDD <= :endDate  ");

		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("locationId", locationId );
		query.setString("startDate", startDate);
		query.setString("endDate", endDate);

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}

}