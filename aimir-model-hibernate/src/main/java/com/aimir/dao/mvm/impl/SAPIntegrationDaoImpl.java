package com.aimir.dao.mvm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.SAPIntegrationDao;
import com.aimir.model.mvm.SAPIntegrationLog;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;

@Repository(value = "SAPIntegrationDao")
public class SAPIntegrationDaoImpl extends AbstractHibernateGenericDao<SAPIntegrationLog, Integer> implements SAPIntegrationDao {

	@Autowired
	protected SAPIntegrationDaoImpl(SessionFactory sessionFactory) {
		super(SAPIntegrationLog.class);
		super.setSessionFactory(sessionFactory);
	}
	
	public List<SAPIntegrationLog> getOrgerList(Map<String, Object> condition) {
		
		String deadLine		 	= StringUtil.nullToBlank(condition.get("deadLine"));
		String resultState		= StringUtil.nullToBlank(condition.get("resultState"));
		
		Query query = null;
		StringBuffer sb = new StringBuffer();
		sb.append("   FROM SAPIntegrationLog s 			");
		sb.append("\n WHERE  s.deadline >= :deadLine 	");
		sb.append("\n AND  s.resultState IS NULL 		");
		
		query = getSession().createQuery(sb.toString()).setParameter("deadLine", deadLine);
        
		List<SAPIntegrationLog> list = query.list();
		return list;
	}	
	
	public List<Object> getOutBoundGridData(Map<String, Object> condition) {
		
		List<Object> result      = new ArrayList<Object>();
		List<Map<String, Object>> gridData      = new ArrayList<Map<String, Object>>();
		
		String startDate = StringUtil.nullToZero(condition.get("startDate"));
		String endDate = StringUtil.nullToZero(condition.get("endDate"));
		String limit = StringUtil.nullToBlank(condition.get("limit"));
		String page = StringUtil.nullToBlank(condition.get("page"));
		
		StringBuffer sb =  new StringBuffer();
		sb.append("\nSELECT 	sapLog.OUTBOUND_FILENAME,  " +
				"				MAX(sapLog.OUTBOUND_DATE) AS OUTBOUND_DATE, " +
				"				COUNT(sapLog.SERIAL_NUMBER2) AS OUTBOUND_METERCNT, " +
				"				SUM(OUTBOUND_TOTALCNT) AS OUTBOUND_TOTALCNT ");
		sb.append("\nFROM 		(SELECT 	OUTBOUND_FILENAME AS OUTBOUND_FILENAME, " +
				"							COUNT(OUTBOUND_FILENAME) AS OUTBOUND_TOTALCNT, " +
				"							MAX(YYYYMMDDHHMMSS) AS OUTBOUND_DATE, SERIAL_NUMBER2 ");
		sb.append("\n			FROM		SAPIntegrationLog");
		sb.append("\n			WHERE		OUTBOUND_FILENAME IS NOT NULL");
		sb.append("\n			AND			YYYYMMDDHHMMSS BETWEEN :startDate AND :endDate");
		sb.append("\n			GROUP BY 	OUTBOUND_FILENAME, SERIAL_NUMBER2) sapLog");
		sb.append("\nGROUP BY 	sapLog.OUTBOUND_FILENAME");
		sb.append("\nORDER BY 	OUTBOUND_DATE DESC");
		
		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
		query.setString("startDate", startDate);
		query.setString("endDate", endDate);
		
		
		// Paging
        int rowPerPage = Integer.parseInt(limit);
        int firstIdx  = Integer.parseInt(page) * rowPerPage;
        
        query.setFirstResult(firstIdx);
        query.setMaxResults(rowPerPage);
 
        gridData = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
       
        
        // 전체 건수
        StringBuffer sbCount = new StringBuffer();
        sbCount.append("\n SELECT COUNT(*) ");
        sbCount.append("\n FROM (  	SELECT 	sapLog.OUTBOUND_FILENAME,  " +
				"					MAX(sapLog.OUTBOUND_DATE) AS OUTBOUND_DATE, " +
				"					COUNT(sapLog.SERIAL_NUMBER2) AS OUTBOUND_METERCNT, " +
				"					SUM(OUTBOUND_TOTALCNT) AS OUTBOUND_TOTALCNT ");
        sbCount.append("\nFROM 		(SELECT 	OUTBOUND_FILENAME AS OUTBOUND_FILENAME, " +
				"								COUNT(OUTBOUND_FILENAME) AS OUTBOUND_TOTALCNT, " +
				"								MAX(YYYYMMDDHHMMSS) AS OUTBOUND_DATE, SERIAL_NUMBER2 ");
        sbCount.append("\n			FROM		SAPIntegrationLog");
        sbCount.append("\n			WHERE		OUTBOUND_FILENAME IS NOT NULL");
        sbCount.append("\n			AND			YYYYMMDDHHMMSS BETWEEN :startDate AND :endDate");
        sbCount.append("\n			GROUP BY 	OUTBOUND_FILENAME, SERIAL_NUMBER2) sapLog");
        sbCount.append("\nGROUP BY 	sapLog.OUTBOUND_FILENAME");
        sbCount.append("\n ) countTotal ");
        
        SQLQuery countQuery = getSession().createSQLQuery(new SQLWrapper().getQuery(sbCount.toString()));
        countQuery.setString("startDate", startDate);
        countQuery.setString("endDate", endDate);
        Number gridDataCount = (Number)countQuery.uniqueResult();
        
        for (int i = 0; i < gridData.size(); i++) {
        	gridData.get(i).put("NO", (i+1) + firstIdx);
		}
        //gridData
        result.add(gridData);
        //Grid's Total Count
        result.add(gridDataCount.intValue());
        
        
        
		return result;
	}

	public List<Object> getInBoundGridData(Map<String, Object> condition) {

		List<Object> result      = new ArrayList<Object>();
		List<Map<String, Object>> gridData      = new ArrayList<Map<String, Object>>();
		
		String outboundFileName = StringUtil.nullToZero(condition.get("outboundFileName"));
		String outboundDate = StringUtil.nullToZero(condition.get("outboundDate"));
		if(outboundDate.length() > 8 ) {
			outboundDate = StringUtil.nullToZero(condition.get("outboundDate")).substring(0,8);
		}
		
		StringBuffer sb =  new StringBuffer();
		sb.append("\nSELECT		sapLog.INBOUND_FILENAME, MAX(sapLog.INBOUND_DATE) AS INBOUND_DATE, " +
								"COUNT(*) AS INBOUND_METERCNT, " +
								"SUM(sapLog.INBOUND_TOTALCNT) AS  INBOUND_TOTALCNT");
		sb.append("\nFROM		(SELECT	INBOUND_FILENAME AS INBOUND_FILENAME, " +
				"						MAX(INBOUND_WRITEDATE) AS INBOUND_DATE, " +
				"						COUNT(INBOUND_FILENAME) AS INBOUND_TOTALCNT ");
		sb.append("\n			FROM	SAPIntegrationLog ");
		sb.append("\n			WHERE	INBOUND_FILENAME IS NOT NULL ");
		sb.append("\n 			AND		OUTBOUND_FILENAME = :outboundFileName ");
		sb.append("\n			AND		YYYYMMDDHHMMSS like :outboundDate ");
		sb.append("\n			GROUP BY 	INBOUND_FILENAME, SERIAL_NUMBER2) sapLog ");
		sb.append("\nGROUP BY 	sapLog.INBOUND_FILENAME");
		sb.append("\nORDER BY 	INBOUND_DATE DESC");

		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
		query.setString("outboundFileName", outboundFileName);
		query.setString("outboundDate", outboundDate+"%");
		
        gridData = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        for (int i = 0; i < gridData.size(); i++) {
        	gridData.get(i).put("NO", i+1);
		}
        
        result.add(gridData);
        
		return result;
	}
	

	public List<Object> getErrorLogGridData(Map<String, Object> condition) {
		List<Object> result      = new ArrayList<Object>();
		List<Map<String, Object>> gridData      = new ArrayList<Map<String, Object>>();
		
		String outboundFileName = StringUtil.nullToZero(condition.get("outboundFileName"));
		String outboundDate = StringUtil.nullToZero(condition.get("outboundDate"));
		if(outboundDate.length() > 8 ) {
			outboundDate = StringUtil.nullToZero(condition.get("outboundDate")).substring(0,8);
		}
		
		StringBuffer sb =  new StringBuffer();
		sb.append("\nSELECT		Serial_Number2 AS METER_SERIAL, " +
				"				ERROR_REASON AS ERROR_REASON");
		sb.append("\nFROM		SAPIntegrationLog s");
		sb.append("\nWHERE		INBOUND_FILENAME IS NULL");
		sb.append("\nAND		OUTBOUND_FILENAME = :outboundFileName");
		sb.append("\nAND		YYYYMMDDHHMMSS like :outboundDate");
		sb.append("\nGROUP BY 	Serial_Number2, ERROR_REASON");

		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
		query.setString("outboundFileName", outboundFileName);
		query.setString("outboundDate", outboundDate+"%");
		
        gridData = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        
        for (int i = 0; i < gridData.size(); i++) {
        	gridData.get(i).put("NO", i+1);
		}
        
        result.add(gridData);
        
        // 전체 건수
        StringBuffer sbCount = new StringBuffer();
        sbCount.append("\n SELECT COUNT(*) ");
        sbCount.append("\n FROM (  ");
        sbCount.append(sb);
        sbCount.append("\n ) countTotal ");
        
        SQLQuery countQuery = getSession().createSQLQuery(new SQLWrapper().getQuery(sbCount.toString()));
        countQuery.setString("outboundFileName", outboundFileName);
        countQuery.setString("outboundDate", outboundDate+"%");
		
        Number gridDataCount = (Number)countQuery.uniqueResult();
        
        result.add(gridDataCount.toString());
		
		return result;
	}


}
