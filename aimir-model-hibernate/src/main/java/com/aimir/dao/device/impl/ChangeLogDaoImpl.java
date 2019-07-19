package com.aimir.dao.device.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.ChangeLogDao;
import com.aimir.model.device.ChangeLog;
import com.aimir.model.device.ChangeLogVO;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.SQLWrapper;

@Repository(value = "changelogDao")
public class ChangeLogDaoImpl extends AbstractHibernateGenericDao<ChangeLog, Long> implements ChangeLogDao {

	@Autowired
	protected ChangeLogDaoImpl(SessionFactory sessionFactory) {
		super(ChangeLog.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<ChangeLogVO> getChanageLogMiniChartData() {		
		
		StringBuffer sbQuery = new StringBuffer()
		 .append(" SELECT property, count(*) cnt                                                                                 \n")                                
		 .append("   FROM CHANGELOG                                                                                              \n")
		 .append("  WHERE TIMESTAMP(SUBSTR(CHANGEDATE, 1,4) CONCAT '-' CONCAT SUBSTR(CHANGEDATE, 5,2) CONCAT '-' CONCAT SUBSTR(CHANGEDATE, 5,2), \n")
		 .append("                  SUBSTR(CHANGETIME, 1,2) CONCAT '.' CONCAT SUBSTR(CHANGETIME, 3,2) CONCAT '.' CONCAT SUBSTR(CHANGETIME, 5,2)) \n")
		 .append("        > TIMESTAMP(:date, :time)                                                                              \n")
		 .append("  GROUP BY property                                                                                            \n")
		 .append("  ORDER BY cnt                                                                                                 \n");

		// 현재 시간 보다 한달 이전의 날짜 & 시간
		Map<String, String> dateMap = DateTimeUtil.calcDate(Calendar.MONTH, -1);
		
		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
		query.setString("date", dateMap.get("date"));
		query.setString("time", dateMap.get("time"));
		
		List result = query.list();
		
		List<ChangeLogVO> changeLogVos = new ArrayList<ChangeLogVO>();
		ChangeLogVO changeLogVO = null;

		for(int i = 0, size = result.size() ; i < size ; i++) {

			changeLogVO = new ChangeLogVO();
			
			Object[] resultData = (Object[])result.get(i);
			changeLogVO.setRank(Integer.toString(i + 1));
			changeLogVO.setProperty(resultData[0].toString());
			changeLogVO.setCnt(resultData[1].toString());
			
			changeLogVos.add(changeLogVO);
		}

		return changeLogVos;
	}
	
	@SuppressWarnings("unchecked")
	public List<ChangeLog> getChangeLogs(String[] array) {

//		arrayObj[0] = ("#operatorType").val();
//		arrayObj[1] = ("#operator").val();
//		arrayObj[2] = ("#operationCode").val();
//		arrayObj[3] = ("#targetCode").val();
//		arrayObj[4] = ("#target").val();
//		arrayObj[5] = ("#property").val();
//		arrayObj[6] = ("#startLogDate").val();
//		arrayObj[7] = ("#endLogDate").val();
		
//		String operatorType = array[0];
//		String operator = array[1];
//		String operationCode = array[2];
//		String targetCode = array[3];
//		String target = array[4];
//		String property = array[5];
//		String startLogDate = array[6];
//		String endLogDate = array[7];
						
		Criteria criteria = getSession().createCriteria(ChangeLog.class);		
		
//		if(operatorType != null && !"".equals(operatorType))
//			criteria.add(Restrictions.eq("id", operatorType));	
				
		int firstResult = Integer.parseInt(array[8]);
		int rowPerPage = Integer.parseInt(array[9]);

		criteria.setFirstResult(firstResult);		
		criteria.setMaxResults(rowPerPage);
		
		List<ChangeLog> changeLogs = criteria.list();

		return changeLogs; 
	}

	public Integer getChangeLogCount(String[] array) {

//		arrayObj[0] = ("#operatorType").val();
//		arrayObj[1] = ("#operator").val();
//		arrayObj[2] = ("#operationCode").val();
//		arrayObj[3] = ("#targetCode").val();
//		arrayObj[4] = ("#target").val();
//		arrayObj[5] = ("#property").val();
//		arrayObj[6] = ("#startLogDate").val();
//		arrayObj[7] = ("#endLogDate").val();
		
//		String operatorType = array[0];
//		String operator = array[1];
//		String operationCode = array[2];
//		String targetCode = array[3];
//		String target = array[4];
//		String property = array[5];
//		String startLogDate = array[6];
//		String endLogDate = array[7];

		Criteria criteria = getSession().createCriteria(ChangeLog.class);		
		criteria.setProjection(Projections.rowCount());

		return ((Number)criteria.uniqueResult()).intValue();
	}
}
