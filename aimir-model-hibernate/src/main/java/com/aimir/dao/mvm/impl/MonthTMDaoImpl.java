package com.aimir.dao.mvm.impl;

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
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MonthTMDao;
import com.aimir.model.mvm.MonthTM;
import com.aimir.util.Condition;
import com.aimir.util.SearchCondition;

@Repository(value = "monthtmDao")
public class MonthTMDaoImpl extends
		AbstractHibernateGenericDao<MonthTM, Integer> implements MonthTMDao {

	private static Log logger = LogFactory.getLog(MonthTMDaoImpl.class);

	@Autowired
	protected MonthTMDaoImpl(SessionFactory sessionFactory) {
		super(MonthTM.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Object> getMonthTMsMaxMinAvg(Set<Condition> conditions,
			String div) {

		Criteria criteria = getSession().createCriteria(MonthTM.class);
		ProjectionList pjl = Projections.projectionList();
		if (conditions != null) {
			Iterator<Condition> it = conditions.iterator();
			while (it.hasNext()) {
				Condition condition = (Condition) it.next();
				Criterion addCriterion = SearchCondition
						.getCriterion(condition);

				if (addCriterion != null) {
					criteria.add(addCriterion);
				}
			}
		}

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

	public List<MonthTM> getMonthTMsByListCondition(Set<Condition> set) {

		return findByConditions(set);
	}

	public List<Object> getMonthTMsCountByListCondition(Set<Condition> set) {

		return findTotalCountByConditions(set);
	}

	/**
	 * BEMS 월별 온도 변화량 차트 최대/최소데이터 조회.
	 */
	@SuppressWarnings("unchecked")
    public List<Object> getConsumptionTmMonitoring(Map<String, Object> condition) {

		logger.info("BEMS 월별 온도 변화량  차트 최대/최소데이터 조회.\n==== conditions ====\n"
				+ condition);

		Integer locationId =((Number) condition.get("locationId")).intValue();
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		StringBuffer sb = new StringBuffer();

		sb.append("\n 		SELECT LL.PARENT_ID AS TM_PARENT_ID  ");
		sb.append("\n 		     , D.YYYYMM  ");
		sb.append("\n 		     , D.AVGVALUE AS TM_AVGVALUE  ");
		sb.append("\n 		     , D.MAXIMUMVALUE AS TM_MAXVALUE  ");
		sb.append("\n 		     , D.MINIMUMVALUE AS TM_MINVALUE  ");
		sb.append("\n 		FROM MONTH_TM D , (  ");
		sb.append("\n 			SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID FROM METER M , (   ");
		sb.append("\n 	 				SELECT ID , NAME , PARENT_ID FROM LOCATION WHERE ID = :locationId ");
		sb.append("\n 	 		)L WHERE M.LOCATION_ID = L.ID  ");
//		sb.append("\n 		   			 AND M.METERTYPE_ID = (  ");
//		sb.append("\n 		   					SELECT CC.id FROM code AS CC WHERE CC.parent_id = ( ");
//		sb.append("\n 		   									SELECT C.id FROM code AS C WHERE C.CODE = '1.3.1' "); // MeterType
//		sb.append("\n 		   					) AND CC.CODE = '1.3.1.2' "); // WaterMeter 온도코드로 수정할것
//		sb.append("\n 		   				) ");
		sb.append("\n 		)LL WHERE D.MDEV_ID = LL.MDS_ID  ");
		sb.append("\n 		 AND D.YYYYMM >= :startDate    ");
		sb.append("\n 		 AND D.YYYYMM <= :endDate  ");

		SQLQuery query = getSession().createSQLQuery(sb.toString());

		query.setInteger("locationId", locationId);
		query.setString("startDate", startDate);
		query.setString("endDate", endDate);

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@SuppressWarnings("unchecked")
    public List<Object> getUsageChartData(Set<Condition> conditions) {

		String startDate = "";
		String endDate = "";

		StringBuilder sb = new StringBuilder();
		StringBuilder querySb = new StringBuilder();

		Iterator<Condition> cIterator = conditions.iterator();
		int i = 0;

		while (cIterator.hasNext()) {
			String maxStr = "";
			String minStr = "";
			Condition con = cIterator.next();

			if ("date".equalsIgnoreCase(con.getField())) {
				maxStr = maxStr
						+ "(SELECT MAX(m.MAXIMUMVALUE) FROM MONTH_TM m WHERE m.yyyymm >= :"
						+ con.getOperator() + "_startDate";
				maxStr = maxStr + " AND m.yyyymm <= :" + con.getOperator()
						+ "_endDate ";
				minStr = minStr
						+ "(SELECT MIN(m.MINIMUMVALUE) FROM MONTH_TM m WHERE m.yyyymm >= :"
						+ con.getOperator() + "_startDate";
				minStr = minStr + " AND m.yyyymm <= :" + con.getOperator()
						+ "_endDate ";

			}

			if ("startEndDate".equalsIgnoreCase(con.getField())) {

				startDate = con.getValue()[0].toString();
				endDate = con.getValue()[1].toString();

			} else {

				maxStr = maxStr + " AND m.location_id=:location ";
				maxStr = maxStr + " ) " + "MAX_" + con.getOperator() + ",";
				minStr = minStr + " AND m.location_id=:location ";

				minStr = minStr + " ) " + "MIN_" + con.getOperator() + ",";

				maxStr = maxStr + " AND m.mds_id=:mds_id ";
				maxStr = maxStr + " ) " + "MAX_" + con.getOperator() + ",";
				minStr = minStr + " AND m.mds_id=:mds_id ";

				minStr = minStr + " ) " + "MIN_" + con.getOperator() + ",";

			}

			sb.append(maxStr + minStr);

			i++;

		}

		querySb.append("   SELECT ");
		querySb.append(sb.substring(0, sb.lastIndexOf(",")));

		querySb.append(" from MONTH_TM m where m.yyyymm >=:startDate ");
		querySb.append(" and m.yyyymm <=:endDate ");

		querySb.append(" and m.location_id =:location ");

		querySb.append(" and m.mds_id =:mds_id ");

		SQLQuery query = getSession().createSQLQuery(querySb.toString());

		query.setString("startDate", startDate);
		query.setString("endDate", endDate);

		Iterator<Condition> cqIterator = conditions.iterator();
		while (cqIterator.hasNext()) {
			Condition con = cqIterator.next();

			if ("date".equalsIgnoreCase(con.getField())) {
				query.setString(con.getOperator() + "_startDate", con
						.getValue()[0].toString());
				query.setString(con.getOperator() + "_endDate",
						con.getValue()[1].toString());
			}

			if ("location.id".equalsIgnoreCase(con.getField())) {
				query.setString("location", con.getField());

			}

			if ("meter.mdsId".equalsIgnoreCase(con.getField())) {
				query.setString("mds_id", con.getField());

			}
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

}