package com.aimir.dao.mvm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MonthSPMDao;
import com.aimir.model.mvm.MonthSPM;
import com.aimir.util.Condition;

/**
 * 태양열에너지 월별 검침 Dao
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
@Repository(value = "monthSPMDao")
public class MonthSPMDaoImpl extends AbstractHibernateGenericDao<MonthSPM, Integer> 
	implements MonthSPMDao {
	
	@Autowired
	protected MonthSPMDaoImpl(SessionFactory sessionFactory) {
		super(MonthSPM.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@Override
	public long totalByConditions(Set<Condition> condition) {
		List<Object> ret = findTotalCountByConditions(condition);
		return (Long) ret.get(0);
	}

	/**
     * 날자 타입 검색별로 수 검침결과를 반환
     * 
	 * @param condition
	 * @param dateType
	 * @return
     */
    @Override
    public List<Object> getConsumptionEmCo2ManualMonitoring(Map<String, Object> condition, DateType dateType) {
    	if(DateType.MONTHLY.equals(dateType)) {
    		return getConsumptionEmCo2MonthlyManualMonitoring(condition);
    	}
    	else if(DateType.SEASONAL.equals(dateType)){
    		return getConsumptionEmCo2SeasonalManualMonitoring(condition);
    	}
    	else {
    		return new ArrayList<Object>();
    	}
    }
    
    private List<Object> getConsumptionEmCo2SeasonalManualMonitoring(
			Map<String, Object> condition) {
		return getConsumptionEmCo2MonthlyManualMonitoring(condition);
	}

	@SuppressWarnings("unchecked")
	private List<Object> getConsumptionEmCo2MonthlyManualMonitoring(Map<String, Object> condition) {
		Integer meterId = (Integer) condition.get("meterId");
		String startDate = ObjectUtils.defaultIfNull(condition.get("startDate"), "").toString();
		if (startDate.length() > 6) startDate = startDate.substring(0, 6);
		String endDate = ObjectUtils.defaultIfNull(condition.get("endDate"), "").toString();
		if (endDate.length() > 6) endDate = endDate.substring(0, 6);

		StringBuffer sb = new StringBuffer();

		sb.append("\n SELECT SPM.YYYYMM AS YYYYMM , SPM.TOTAL AS SPM_TOTAL ,  CO2.TOTAL AS CO2_TOTAL  FROM (  ");
		sb.append("\n 	SELECT D.YYYYMM , SUM(D.TOTAL) AS TOTAL  ");
		sb.append("\n 	FROM MONTH_SPM D , ( ");
		sb.append("\n 		SELECT M.MDS_ID , L.ID , L.NAME , L.PARENT_ID ");
		sb.append("FROM METER M , LOCATION L ");
		sb.append("WHERE M.LOCATION_ID = L.ID ");
		if(meterId != null) {
			sb.append("AND M.ID = :meterId ");
		}
		sb.append("\n 	) LL WHERE D.MDEV_ID = LL.MDS_ID AND D.CHANNEL=1 ");
		if(!startDate.isEmpty() && !endDate.isEmpty()) {
			sb.append("\n    AND D.YYYYMM >= :startDate   ");
			sb.append("\n    AND D.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D.YYYYMM ");
		sb.append("\n ) SPM ");
		sb.append("\n LEFT JOIN ");
		sb.append("\n (  ");
		sb.append("\n 	SELECT D2.YYYYMM , SUM(D2.TOTAL) AS TOTAL ");
		sb.append("\n 	FROM MONTH_SPM D2 , ( ");
		sb.append("\n 		SELECT M2.MDS_ID , L2.ID , L2.NAME , L2.PARENT_ID ");
		sb.append("FROM METER M2 , LOCATION L2 ");
		sb.append("WHERE M2.LOCATION_ID = L2.ID ");
		if(meterId != null) {
			sb.append("AND M2.ID = :meterId ");
		}
		sb.append("\n 	) LL2 WHERE D2.MDEV_ID = LL2.MDS_ID AND D2.CHANNEL=0 ");
		if(!startDate.isEmpty() && !endDate.isEmpty()) {
			sb.append("\n    AND D2.YYYYMM >= :startDate   ");
			sb.append("\n    AND D2.YYYYMM <= :endDate     ");
		}
		sb.append("\n 	GROUP BY D2.YYYYMM ");
		sb.append("\n ) CO2 ");
		sb.append("\n ON SPM.YYYYMM = CO2.YYYYMM");

		SQLQuery query = getSession().createSQLQuery(sb.toString());
		if(meterId != null) {
			query.setInteger("meterId", meterId);
		}
		if(!startDate.isEmpty() && !endDate.isEmpty()) {
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@Override
	public List<MonthSPM> getMonthSPMsByListCondition(Set<Condition> conditions) {
		return findByConditions(conditions);
	}

	@Override
	public List<Object> getMonthSPMsCountByListCondition(Set<Condition> conditions) {
		return findTotalCountByConditions(conditions);
	}
}
