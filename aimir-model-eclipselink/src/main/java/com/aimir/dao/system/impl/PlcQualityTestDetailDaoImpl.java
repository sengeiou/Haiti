package com.aimir.dao.system.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.PlcQualityTestDetailDao;

import com.aimir.model.system.PlcQualityTestDetail;
import com.aimir.model.system.Supplier;
import com.aimir.util.Condition;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Deprecated
@Repository("PlcQualityTestDetailDao")
public class PlcQualityTestDetailDaoImpl extends AbstractJpaDao<PlcQualityTestDetail, Integer> implements PlcQualityTestDetailDao {

	public PlcQualityTestDetailDaoImpl() {
	    super(PlcQualityTestDetail.class);
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
        Query query = getEntityManager().createQuery(listSb.toString());
        if(zigId != null) {
        	query.setParameter("zigId", zigId);
        }
        if(!startDate.isEmpty() && !endDate.isEmpty()) {
        	query.setParameter("startDate", startDate+"000000");
        	query.setParameter("endDate", endDate+"235959");
        }
        if("success".equals(testResult)) {
        	query.setParameter("testResult", true);
        } else if("fail".equals(testResult)){
        	query.setParameter("testResult", false);
        }

    	//Total Count
        countSb.append("\n SELECT COUNT(*) ");
        countSb.append(conditionSb);
        if("latest".equals(searchType)) {
        	countSb.append("\nAND pd.testStartDate = (SELECT MAX(pd.testStartDate)");
        	countSb.append(conditionSb + ")");
        }

        Query countQuery = getEntityManager().createQuery(countSb.toString());
        if(zigId != null) {
        	countQuery.setParameter("zigId", zigId);
        }
    	
        if(!startDate.isEmpty() && !endDate.isEmpty()) {
        	countQuery.setParameter("startDate", startDate+"000000");
        	countQuery.setParameter("endDate", endDate+"235959");
        }
        
        if("success".equals(testResult)) {
        	countQuery.setParameter("testResult", true);
        } else if("fail".equals(testResult)){
        	countQuery.setParameter("testResult", false);
        }

        Number totalCount = (Number)countQuery.getSingleResult();
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
            List<Map<String,Object>> dbList = null; // query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
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
        	List<Map<String,Object>> dbList = null; // query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
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
			Query maxQuery = getEntityManager().createQuery(sbMax.toString());
			maxQuery.setParameter("zigId", zigId);
			
			maxData = null; // maxQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
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

		Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("zigId", zigId);
		if("latest".equals(searchType) && maxData != null && maxData.size() > 0) {
			query.setParameter("testStartDate", String.valueOf(maxData.get(0).get("testStartDate")));
		}
		if(!startDate.isEmpty() && !endDate.isEmpty()) {
			query.setParameter("startDate", startDate+"000000");
			query.setParameter("endDate", endDate+"235959");
		}
		
        if("success".equals(testResult)) {
        	query.setParameter("testResult", true);
        } else if("fail".equals(testResult)){
        	query.setParameter("testResult", false);
        }
		
		returnData = null; // query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		return returnData; 
	}
	
	public List<Map<String,Object>> checkResult(String zigName) {
		List<Map<String, Object>> returnData = new ArrayList<Map<String,Object>>();
		StringBuilder sbMax = new StringBuilder();
		sbMax.append("\nSELECT pd.zigId as zigId, MAX(pd.testStartDate) as testStartDate ");
		sbMax.append("\nFROM PlcQualityTestDetail  pd, PlcQualityTest p");
		sbMax.append("\nWHERE p.id=pd.zigId AND p.zigName = :zigName ");
		sbMax.append("\nGROUP BY pd.zigId ");
		
		Query maxQuery = getEntityManager().createQuery(sbMax.toString());
		maxQuery.setParameter("zigName", zigName);
		
		List<Map<String,Object>> maxData = null; // maxQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		StringBuilder sb = new StringBuilder();
		sb.append("\nSELECT pd.zigId as zigId, p.zigName as zigName, pd.testStartDate as testStartDate, count(pd.zigId) as nullCnt");
		sb.append("\nFROM  PlcQualityTest p, PlcQualityTestDetail pd");
		sb.append("\nWHERE  p.id=pd.zigId AND p.zigName = :zigName");
		if(maxData != null && maxData.size() > 0) {
			sb.append("\nAND pd.testStartDate = :testStartDate");
		}
		sb.append("\n AND pd.testResult is null");
		sb.append("\nGROUP BY pd.zigId, p.zigName, pd.testStartDate ");

		Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("zigName", zigName);
		if(maxData.size() > 0) {
			query.setParameter("testStartDate", String.valueOf(maxData.get(0).get("testStartDate")));
		}
		
		returnData = null; // query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		return returnData; 
	}
	
	public int changeNullResult(Integer zigId, String testStartDate) {
		String completeDate = null;
		int returnCnt = 0;
		try {
			completeDate = TimeUtil.getCurrentTime();
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE PlcQualityTestDetail pd SET pd.testResult = :testResult , pd.failReason = :failReason, pd.completeDate = :completeDate WHERE pd.zigId=:zigId and pd.testResult is null and pd.testStartDate = :testStartDate");
	        Query query = getEntityManager().createQuery(sb.toString());
	        query.setParameter("testResult", false);
	        query.setParameter("completeDate", completeDate);
	        query.setParameter("failReason", "No Communication");
	        query.setParameter("zigId", zigId);
	        query.setParameter("testStartDate", testStartDate);
	        returnCnt = query.executeUpdate();
		} catch (ParseException e) {
			// logger.error(e,e);
		}
		return returnCnt;
	}

    @Override
    public Class<PlcQualityTestDetail> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

}