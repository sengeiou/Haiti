package com.aimir.dao.system.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.PlcQualityTestDetailDao;
import com.aimir.model.system.PlcQualityTestDetail;
import com.aimir.model.system.Supplier;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Repository("PlcQualityTestDetailDao")
public class PlcQualityTestDetailDaoImpl extends AbstractHibernateGenericDao<PlcQualityTestDetail, Integer> implements PlcQualityTestDetailDao {

	private static Log logger = LogFactory.getLog(PlcQualityTestDetailDaoImpl.class);
	
	@Autowired
	protected PlcQualityTestDetailDaoImpl(SessionFactory sessionFactory) {
		super(PlcQualityTestDetail.class);
		super.setSessionFactory(sessionFactory);
	}
	
	public List<Object> getPlcQualityDetailResult(Map<String, Object> condition) {
		List<Object> returnData = new ArrayList<Object>();

		Integer zigId = (Integer)condition.get("zigId");
		String startDate = StringUtil.nullToBlank(condition.get("startDate"));
		String endDate = StringUtil.nullToBlank(condition.get("endDate"));
		String searchType = (String)condition.get("searchType");
		String testResult = (String)condition.get("testResult");
		String limit = (String)condition.get("limit");
		String curPage = (String)condition.get("curPage");
		Supplier supplier = (Supplier)condition.get("supplier");
		
        StringBuilder listSb = new StringBuilder();
        StringBuilder conditionSb = new StringBuilder();
        StringBuilder countSb = new StringBuilder();

        //Paging Data
        listSb.append("\nSELECT pd.testResult as testResult, ");
        listSb.append("\n		pd.meterSerial as meterSerial, ");
        listSb.append("\n		pd.modemSerial as modemSerial, ");
        listSb.append("\n		pd.hwVer as hwVer,");
        listSb.append("\n		pd.swVer as swVer,");
        listSb.append("\n		pd.swBuild as swBuild,");
        listSb.append("\n		pd.failReason as failReason,");
        listSb.append("\n		pd.completeDate as completeDate");
        conditionSb.append("\nFROM 	PlcQualityTestDetail pd, PlcQualityTest p");
        conditionSb.append("\nWHERE  p.id = pd.zigId");
        if(zigId != null) {
        	conditionSb.append("\nAND 	p.id = :zigId");
        }
        if("start".equals(searchType) && !startDate.isEmpty() && !endDate.isEmpty()) {
        	conditionSb.append("\nAND  pd.testStartDate BETWEEN :startDate AND :endDate");
        }

        if("complete".equals(searchType) && !startDate.isEmpty() && !endDate.isEmpty()) {
        	conditionSb.append("\nAND  pd.completeDate BETWEEN :startDate AND :endDate");
        }

        if("success".equals(testResult) || "fail".equals(testResult)) {
        	conditionSb.append("\nAND pd.testResult = :testResult");
        } else if ("unKnown".equals(testResult)){
        	conditionSb.append("\nAND pd.testResult is null");
        }
        
        listSb.append(conditionSb);
        if("latest".equals(searchType)) {
			listSb.append("\nAND pd.testStartDate  = (SELECT MAX(pd.testStartDate)");
        	listSb.append(conditionSb + ")");        
        }
        listSb.append("\nORDER BY pd.id");
        Query query = getSession().createQuery(listSb.toString());
        if(zigId != null) {
        	query.setInteger("zigId", zigId);
        }
        if(!startDate.isEmpty() && !endDate.isEmpty()) {
        	query.setString("startDate", startDate+"000000");
        	query.setString("endDate", endDate+"235959");
        }
        if("success".equals(testResult)) {
        	query.setBoolean("testResult", true);
        } else if("fail".equals(testResult)){
        	query.setBoolean("testResult", false);
        }

    	//Total Count
        countSb.append("\n SELECT COUNT(*) ");
        countSb.append(conditionSb);
        if("latest".equals(searchType)) {
        	countSb.append("\nAND pd.testStartDate = (SELECT MAX(pd.testStartDate)");
        	countSb.append(conditionSb + ")");
        }

        Query countQuery = getSession().createQuery(countSb.toString());
        if(zigId != null) {
        	countQuery.setInteger("zigId", zigId);
        }
    	
        if(!startDate.isEmpty() && !endDate.isEmpty()) {
        	countQuery.setString("startDate", startDate+"000000");
        	countQuery.setString("endDate", endDate+"235959");
        }
        
        if("success".equals(testResult)) {
        	countQuery.setBoolean("testResult", true);
        } else if("fail".equals(testResult)){
        	countQuery.setBoolean("testResult", false);
        }

        Number totalCount = (Number)countQuery.uniqueResult();
        returnData.add(totalCount);
        
        boolean isExcel = Boolean.parseBoolean(String.valueOf(condition.get("isExcel")));
        
        Map<String, Object> refindMap = null;
        if (!isExcel) {
            //Paging
            int rowPerPage = 10;
            if (!limit.isEmpty()) {
                rowPerPage = Integer.parseInt(limit);
            }

            int firstIdx  = Integer.parseInt(curPage) * rowPerPage;

            query.setFirstResult(firstIdx);
            query.setMaxResults(rowPerPage);
            List<Map<String,Object>> dbList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
            List<Map<String,Object>> tempList = new ArrayList<Map<String,Object>>();  
        	long idx = 0;
        	for (Map<String, Object> map : dbList) {
        		refindMap = new HashMap<String, Object>();
        		
                if (curPage != null && limit != null) {
                	refindMap.put("no", firstIdx + idx + 1);
                    idx++;
                }

                refindMap.put("testResult",  map.get("testResult"));
                refindMap.put("meterSerial", map.get("meterSerial"));
                refindMap.put("modemSerial", map.get("modemSerial"));
                refindMap.put("hwVer", map.get("hwVer"));
                refindMap.put("swVer", map.get("swVer"));
                refindMap.put("swBuild", map.get("swBuild"));
                refindMap.put("failReason", map.get("failReason"));
                refindMap.put("completeDate", TimeLocaleUtil.getLocaleDate(String.valueOf(map.get("completeDate")) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
                tempList.add(refindMap);
    		}
            returnData.add(tempList);
        	
        } else {
        	List<Map<String,Object>> dbList =query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
            returnData.add(dbList);	
        }

		return returnData;
	}
	
	public List<Map<String,Object>> getSummaryInfo(Map<String, Object> condition) {
		Integer zigId = (Integer)condition.get("zigId");
		String startDate = StringUtil.nullToBlank(condition.get("startDate"));
		String endDate = StringUtil.nullToBlank(condition.get("endDate"));
		String searchType = (String)condition.get("searchType");
		String testResult = (String)condition.get("testResult");
		List<Map<String,Object>> maxData = null;
		List<Map<String, Object>> returnData = new ArrayList<Map<String,Object>>();
		
		if("latest".equals(searchType)) {
			StringBuilder sbMax = new StringBuilder();
			sbMax.append("\nSELECT pd.zigId as zigId, MAX(pd.testStartDate) as testStartDate ");
			sbMax.append("\nFROM PlcQualityTestDetail  pd, PlcQualityTest p");
			sbMax.append("\nWHERE p.id=pd.zigId AND p.id = :zigId ");
			sbMax.append("\nGROUP BY pd.zigId ");
			if("success".equals(testResult)) {
				sbMax.append("\n");
			}
			Query maxQuery = getSession().createQuery(sbMax.toString());
			maxQuery.setInteger("zigId", zigId);
			
			maxData = maxQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("\nSELECT pd.zigId, pd.testResult as testResult, count(pd.zigId) as cnt");
		sb.append("\nFROM  PlcQualityTestDetail pd");
		sb.append("\nWHERE pd.zigId = :zigId");
		
		if("start".equals(searchType) && !startDate.isEmpty() && !endDate.isEmpty()) {
			sb.append("\nAND  pd.testStartDate BETWEEN :startDate AND :endDate");
        }

        if("complete".equals(searchType) && !startDate.isEmpty() && !endDate.isEmpty()) {
        	sb.append("\nAND  pd.completeDate BETWEEN :startDate AND :endDate");
        }

        if("success".equals(testResult) || "fail".equals(testResult)) {
        	sb.append("\nAND pd.testResult = :testResult");
        } else if ("unKnown".equals(testResult)){
        	sb.append("\nAND pd.testResult is null");
        }
        
		if("latest".equals(searchType)) {
			sb.append("\nAND pd.testStartDate=:testStartDate");
		}
		
		sb.append("\nGROUP BY pd.zigId, pd.testResult ");

		Query query = getSession().createQuery(sb.toString());
		query.setInteger("zigId", zigId);
		if("latest".equals(searchType) && maxData.size() > 0) {
			query.setString("testStartDate", String.valueOf(maxData.get(0).get("testStartDate")));
		}
		if(!startDate.isEmpty() && !endDate.isEmpty()) {
			query.setString("startDate", startDate+"000000");
			query.setString("endDate", endDate+"235959");
		}
		
        if("success".equals(testResult)) {
        	query.setBoolean("testResult", true);
        } else if("fail".equals(testResult)){
        	query.setBoolean("testResult", false);
        }
		
		returnData = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return returnData; 
	}
	
	public List<Map<String,Object>> checkResult(String zigName) {
		List<Map<String, Object>> returnData = new ArrayList<Map<String,Object>>();
		StringBuilder sbMax = new StringBuilder();
		sbMax.append("\nSELECT pd.zigId as zigId, MAX(pd.testStartDate) as testStartDate ");
		sbMax.append("\nFROM PlcQualityTestDetail  pd, PlcQualityTest p");
		sbMax.append("\nWHERE p.id=pd.zigId AND p.zigName = :zigName ");
		sbMax.append("\nGROUP BY pd.zigId ");
		
		Query maxQuery = getSession().createQuery(sbMax.toString());
		maxQuery.setString("zigName", zigName);
		
		List<Map<String,Object>> maxData = maxQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		StringBuilder sb = new StringBuilder();
		sb.append("\nSELECT pd.zigId as zigId, p.zigName as zigName, pd.testStartDate as testStartDate, count(pd.zigId) as nullCnt");
		sb.append("\nFROM  PlcQualityTest p, PlcQualityTestDetail pd");
		sb.append("\nWHERE  p.id=pd.zigId AND p.zigName = :zigName");
		if(maxData.size() > 0) {
			sb.append("\nAND pd.testStartDate = :testStartDate");
		}
		sb.append("\n AND pd.testResult is null");
		sb.append("\nGROUP BY pd.zigId, p.zigName, pd.testStartDate ");

		Query query = getSession().createQuery(sb.toString());
		query.setString("zigName", zigName);
		if(maxData.size() > 0) {
			query.setString("testStartDate", String.valueOf(maxData.get(0).get("testStartDate")));
		}
		
		returnData = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		return returnData; 
	}
	
	public int changeNullResult(Integer zigId, String testStartDate) {
		String completeDate = null;
		int returnCnt = 0;
		try {
			completeDate = TimeUtil.getCurrentTime();
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE PlcQualityTestDetail pd SET pd.testResult = :testResult , pd.failReason = :failReason, pd.completeDate = :completeDate WHERE pd.zigId=:zigId and pd.testResult is null and pd.testStartDate = :testStartDate");
	        Query query = getSession().createQuery(sb.toString());
	        query.setBoolean("testResult", false);
	        query.setString("completeDate", completeDate);
	        query.setString("failReason", "No Communication");
	        query.setInteger("zigId", zigId);
	        query.setString("testStartDate", testStartDate);
	        returnCnt = query.executeUpdate();
		} catch (ParseException e) {
			logger.error(e,e);
		}
		return returnCnt;
	}
    
}