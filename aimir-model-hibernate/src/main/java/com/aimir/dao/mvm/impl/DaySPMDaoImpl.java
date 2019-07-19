package com.aimir.dao.mvm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.DaySPMDao;
import com.aimir.model.mvm.DaySPM;
import com.aimir.util.Condition;
import com.aimir.util.SearchCondition;
import com.aimir.util.Condition.Restriction;

/**
 * 태양열에너지 day 검침 Dao
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
@Repository(value = "daySPMDao")
public class DaySPMDaoImpl extends AbstractHibernateGenericDao<DaySPM, Integer> 
	implements DaySPMDao {
	
	@Autowired
	protected DaySPMDaoImpl(SessionFactory sessionFactory) {
		super(DaySPM.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@Override
	public long totalByConditions(Set<Condition> condition) {
		List<Object> ret = findTotalCountByConditions(condition);
		return (Long) ret.get(0);
	}

	@Override
    public List<Object> getConsumptionEmCo2ManualMonitoring(Map<String, Object> condition, DateType dateType) {
    	if(DateType.WEEKLY.equals(dateType)) {
    		return getConsumptionEmCo2WeekManualMonitoring(condition);
    	}
    	else {
    		return new ArrayList<Object>();
    	}
    }
	
	@Override
	public double getSumTotalUsageByCondition(Set<Condition> conditions) {
		conditions.add(
			new Condition("id.channel", new Object[]{ 1 }, null, Restriction.EQ)
		);
		List<Object> list = getSumFieldByCondition(conditions, "total");
		double sum = 0;
		if(list != null && !list.isEmpty()) {
			Object obj = list.get(0);
			sum = (obj != null) ? (Double)obj : 0;
		}
		return sum;
	}	
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Double> getSumUsageByCondition(Set<Condition> conditions) {
		
		Criteria criteria = getSession().createCriteria(getPersistentClass());
		conditions.add(
			new Condition("id.channel", new Object[]{ 1 }, null, Restriction.EQ)
		);
		if (conditions != null && !conditions.isEmpty()) {
			for (Condition condition : conditions) {
				Criterion addCriterion = SearchCondition.getCriterion(condition);
				if (addCriterion != null) {
					criteria.add(addCriterion);
				}
			}
		}
		
		ProjectionList pList = Projections.projectionList();
		String field = "value_";
		for(int i=0; i < 24; i++) {			
			pList.add(Projections.sum((i < 10) ? field + "0" + i : field + i).as("H" + i));
		}
		criteria.setProjection(pList);
		return (Map<String, Double>) criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult();
	}

	
	@SuppressWarnings("unchecked")
	private List<Object> getConsumptionEmCo2WeekManualMonitoring(Map<String, Object> condition) {
		
		Integer meterId = (Integer) condition.get("meterId");
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		StringBuffer sb = new StringBuffer();
		
		sb.append("\n    SELECT SPM.YYYYMMDD AS YYYYMMDD , SPM.TOTAL AS SPM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  ");
		sb.append("\n      FROM (      ");
		sb.append("\n			  SELECT D.YYYYMMDD , SUM(D.TOTAL) AS TOTAL ");
		sb.append("\n				FROM DAY_SPM D , (      ");
		sb.append("\n						SELECT M.MDS_ID , L.ID , L.NAME  ");
		sb.append("\n						  FROM METER M , LOCATION L  ");
		sb.append("\n						 WHERE M.LOCATION_ID = L.ID     ");
		if(meterId != null) {
			sb.append("\n						AND M.ID = :meterId ");
		}
		sb.append("\n					 ) LL  ");
		sb.append("\n				WHERE D.MDEV_ID = LL.MDS_ID  ");
		sb.append("\n				  AND D.CHANNEL = 1       ");
		if(startDate != null && endDate != null) {
			sb.append("\n    			  AND D.YYYYMMDD >= :startDate   ");
			sb.append("\n    			  AND D.YYYYMMDD <= :endDate     ");
		}	
		sb.append("\n				GROUP BY D.YYYYMMDD   ");
		sb.append("\n	   ) SPM ");
		sb.append("\n	   LEFT JOIN ");
		sb.append("\n	   ( ");
		sb.append("\n			   SELECT D2.YYYYMMDD , SUM(D2.TOTAL) AS TOTAL ");
		sb.append("\n				 FROM DAY_SPM D2 , ( ");
		sb.append("\n						SELECT M2.MDS_ID , L2.ID , L2.NAME  ");
		sb.append("\n						 FROM METER M2 , LOCATION L2  ");
		sb.append("\n						WHERE M2.LOCATION_ID = L2.ID     ");
		if(meterId != null) {
			sb.append("\n						AND M2.ID = :meterId ");
		}
		sb.append("\n					  ) LL2  ");
		sb.append("\n				WHERE D2.MDEV_ID = LL2.MDS_ID  ");
		sb.append("\n				  AND D2.CHANNEL = 0       ");
		if(startDate != null && endDate != null) {
			sb.append("\n  				  AND D2.YYYYMMDD >= :startDate   ");
			sb.append("\n  				  AND D2.YYYYMMDD <= :endDate     ");
		}	
		sb.append("\n				GROUP BY D2.YYYYMMDD    ");
		sb.append("\n         ) CO2    ");
		sb.append("\n    ON SPM.YYYYMMDD = CO2.YYYYMMDD ");
		sb.append("\n	ORDER BY SPM.YYYYMMDD ASC  ");
		
		
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		if(meterId != null) {
			query.setInteger("meterId", meterId);
		}
		
		if(startDate != null && endDate != null) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@Override
	public List<DaySPM> getDaySPMsByListCondition(Set<Condition> condition) {
		return findByConditions(condition);
	}

	@Override
	public List<Object> getDaySPMsCountByListCondition(Set<Condition> condition) {
		return findTotalCountByConditions(condition);
	}
}
