package com.aimir.dao.device.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.OperationLog;
import com.aimir.model.device.OperationLogChartData;
import com.aimir.model.system.Supplier;
import com.aimir.util.DecimalUtil;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Repository(value = "operationlogDao")
public class OperationLogDaoImpl extends AbstractHibernateGenericDao<OperationLog, Long> implements OperationLogDao {

    private static Log logger = LogFactory.getLog(OperationLogDaoImpl.class);

	@Autowired
	SupplierDao supplierDao;
	
	@Autowired
	protected OperationLogDaoImpl(SessionFactory sessionFactory) {
		super(OperationLog.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("rawtypes")
    public List<OperationLogChartData> getOperationLogMiniChartData(Integer supplier) {

//		Map<String, String> dateMap = DateTimeUtil.calcDate(Calendar.MONTH, -1);
		String preMonth = null;
		try {
	        preMonth = TimeUtil.getPreMonth(TimeUtil.getCurrentDay(), 1).substring(0, 8);
		} catch(ParseException pe) {
		    pe.printStackTrace();
		}
		
		// FIXME 네이티브쿼리 -> HSQL 로 변경 필요
		StringBuffer sbQry = new StringBuffer()
		.append(" SELECT code.descr AS NAME, sum(comm.success) AS SUCCESS, sum(comm.fail) AS FAIL \n")
		.append("   FROM (SELECT operation_Command_Code, yyyymmdd,    \n")
//		.append("       CASE WHEN (lower(ERROR_Reason) like '%success%' ) THEN 1 ELSE 0 END success, \n")
//		.append("       CASE WHEN (lower(error_Reason) <> 'success' )OR error_Reason IS NULL THEN 1 ELSE 0 END fail \n")
		.append("                CASE WHEN status = 0 THEN 1 ELSE 0 END success,   \n")
		.append("                CASE WHEN status <> 0 OR status IS NULL THEN 1 ELSE 0 END fail \n")
		.append("           FROM operation_log                 \n")
		.append("           WHERE supplier_id = :supplierId) comm,                 \n")
		.append("         Code code                                   \n")
		.append("  WHERE comm.operation_Command_Code = code.id        \n")
		.append("    AND comm.yyyymmdd >= :yyyymmdd                   \n")
		.append("  GROUP BY code.descr                                \n");

		SQLQuery query = getSession().createSQLQuery(sbQry.toString());
		query.setInteger("supplierId", supplier);
		query.setString("yyyymmdd", preMonth);
		
		List result = query.list();	
		
		List<OperationLogChartData> chartDatas = new ArrayList<OperationLogChartData>();
		OperationLogChartData chartData = null;
		
		Object[] resultData = null;		
		String operationCodeName = null;
		String successCnt = null;
		String failCnt = null;
		String totalCnt = null;
		
		for(int i = 0, size = result.size() ; i < size ; i++) {

			resultData = (Object[])result.get(i);
			operationCodeName = (String)resultData[0];
			successCnt = resultData[1].toString();
			failCnt = resultData[2].toString();
			totalCnt = (Integer.parseInt(successCnt) + Integer.parseInt(failCnt)) + "";
			
			chartData = new OperationLogChartData();
			chartData.setOperation(operationCodeName);
			chartData.setSuccessCnt(successCnt);
			chartData.setFailCnt(failCnt);
			chartData.setCnt(totalCnt);
			
			chartDatas.add(chartData);					
		}
		
		return setProgressBarWidth(chartDatas, supplier);		
	}

	@SuppressWarnings("unchecked")
	private List<OperationLogChartData> setProgressBarWidth(List<OperationLogChartData> chartDatas, Integer supplierId) {
		
		List<OperationLogChartData> tempChartDatas = new ArrayList<OperationLogChartData>();
	/*	
		if(chartDatas == null || chartDatas.size() == 0) {
			OperationLogChartData chartData = new OperationLogChartData();	
			chartData.setCnt("0");
			chartData.setFailCnt("0");
			chartData.setSuccessCnt("0");
			chartData.setOperation("There is no data.");
			chartData.setWidth("0");
			
			tempChartDatas.add(chartData);
		}
		*/
		int maxWidth = 130;
		int width = -1;
		int maxCnt = -1;
		int currCnt = -1;
		
		// 최대 수 구하기 위한 포~
		for(OperationLogChartData chartData : chartDatas) {
			
			currCnt = Integer.parseInt(chartData.getCnt());
			
			if(maxCnt < currCnt)
				maxCnt = currCnt; 			
		}

		// 프로그레스바  width 구하기 위한 포~
		for(OperationLogChartData chartData : chartDatas) {
			
		    if (maxCnt == 0) {
		        width = 0;
		    } else {
	            width = (Integer.parseInt(chartData.getCnt()) * maxWidth) / maxCnt;
		    }
			chartData.setWidth(Integer.toString(width));
			tempChartDatas.add(chartData);			
		}
		
		// cnt 갯수 순으로 Collection 안의 정렬~
		Collections.sort(tempChartDatas, new Comparator() {

			public int compare(Object o1, Object o2) {
				
				int o1Cnt = Integer.parseInt(((OperationLogChartData)o1).getCnt());
				int o2Cnt = Integer.parseInt(((OperationLogChartData)o2).getCnt());

				if(o1Cnt > o2Cnt)
					return -1;
				else if(o1Cnt < o2Cnt)
					return 1;
				else
					return 0;
			}			
		});
		
		// 랭크 붙이기 
		List<OperationLogChartData> returnChartDatas = new ArrayList<OperationLogChartData>();
		int i = 1;
		
		Supplier supplier = supplierDao.get(supplierId);
		DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
		
		for(OperationLogChartData data : tempChartDatas) {
			//2011.5.17 jhkim data Rank : String -> int 
			data.setRank(dfMd.format(i));
			data.setCnt(dfMd.format(Integer.parseInt(data.getCnt())));
			data.setFailCnt(dfMd.format(Integer.parseInt(data.getFailCnt())));
			data.setSuccessCnt(dfMd.format(Integer.parseInt(data.getSuccessCnt())));
			
			i++;
			returnChartDatas.add(data);
		}
		
		
		return tempChartDatas;
	}

	@SuppressWarnings("unchecked")
	public List<OperationLogChartData> getAdvanceGridData(Map<String, String> conditionMap) {

		String operatorType = conditionMap.get("operatorType");
		String userId = conditionMap.get("userId");
		String targetType = conditionMap.get("targetType");
		String targetName = conditionMap.get("targetName");
		String status = conditionMap.get("status");
		String description = conditionMap.get("description");
		String period = conditionMap.get("period");
		String startDate = conditionMap.get("startDate");
		String endDate = conditionMap.get("endDate");
		Integer supplierId = Integer.parseInt(conditionMap.get("supplierId"));
		
		StringBuffer sbQry = new StringBuffer();
		sbQry.append(" SELECT code.descr AS NAME, code.id AS ID, sum(comm.success) AS SUCCESS, sum(comm.fail) AS FAIL \n");
		sbQry.append("   FROM (SELECT operation_Command_Code, yyyymmdd,                 \n");
//		sbQry.append("                CASE WHEN (lower(ERROR_Reason) LIKE '%success%' ) THEN 1 ELSE 0 END success,   \n");
//		sbQry.append("                CASE WHEN (lower(error_Reason) <> 'success' )OR error_Reason IS NULL THEN 1 ELSE 0 END fail \n");
		sbQry.append("                CASE WHEN status = 0 THEN 1 ELSE 0 END success,   \n");
		sbQry.append("                CASE WHEN status <> 0 OR status IS NULL THEN 1 ELSE 0 END fail \n");
		sbQry.append("           FROM operation_log                                     \n");
		sbQry.append("          WHERE id is not null                                    \n");
		sbQry.append("          AND supplier_id = :supplierId                          \n");
	
		if(!"".equals(operatorType)) {
			sbQry.append(" AND OPERATOR_TYPE = :operatorType \n");
		}		
		if(!"".equals(userId)) {
			sbQry.append(" AND USER_ID = :userId");
		}
		if(!"".equals(targetType)) {
			sbQry.append(" AND TARGET_TYPE_CODE = :targetType \n");
		}		
		if(!"".equals(targetName)) {
			sbQry.append(" AND TARGET_NAME = :targetName \n");
		}		
		if(status != null && !"".equals(status)) {
			if("0".equals(status)) {
				sbQry.append(" AND lower(ERROR_REASON) like '%success%' ");
			} else {
				sbQry.append(" AND (lower(ERROR_REASON) <> 'success' OR ERROR_REASON IS NULL) ");
			}
		}
//		if(!"".equals(description)) {
//			sbQry.append(" AND DESCRIPTION LIKE :description ");
//		}	
		
		//
		/*if(!"".equals(period)) {			
//			sbQry.append(" AND YYYYMMDD || HHMMSS > :startDate ");
//			sbQry.append(" AND YYYYMMDD || HHMMSS < :endDate ");
			if(DateType.HOURLY.getCode().equals(period)) {
				sbQry.append(" AND YYYYMMDDHHMMSS >= :startDate "); 			// || substr(HHMMSS, 1, 2) >= :startDate ");
				sbQry.append(" AND YYYYMMDDHHMMSS <= :endDate "); 				// || substr(HHMMSS, 1, 2) <= :endDate ");
			} else if(DateType.MONTHLYPERIOD.getCode().equals(period)) {	//jhkim 시작 끝 날짜 8자리로 변경
				sbQry.append(" AND YYYYMMDD >= :startDate ");
				sbQry.append(" AND YYYYMMDD >= :endDate ");
			} else {
				sbQry.append(" AND YYYYMMDD >= :startDate ");						
				sbQry.append(" AND YYYYMMDD <= :endDate ");			
			}	
		}*/
		//sp-1053 Using 'YYYYMMDDHHMMSS' column for search condition
		if(!"".equals(period)) {			
			sbQry.append(" AND YYYYMMDDHHMMSS >= :startDate "); 			// || substr(HHMMSS, 1, 2) >= :startDate ");
			sbQry.append(" AND YYYYMMDDHHMMSS <= :endDate "); 
		}

		
		
		sbQry.append("         ) comm, 					                                \n");
		sbQry.append("         Code code                                                \n");
		sbQry.append("  WHERE comm.operation_Command_Code = code.id                      \n");
		sbQry.append("  GROUP BY code.descr, code.id                                     \n");
		
		SQLQuery query = getSession().createSQLQuery(sbQry.toString());	
		query.setInteger("supplierId", supplierId);
		if(!"".equals(operatorType)) {
			query.setString("operatorType", operatorType);
		}		
		if(!"".equals(userId)) {
			query.setString("userId", userId);
		}
		if(!"".equals(targetType)) {
			query.setString("targetType", targetType);
		}		
		
		if(!"".equals(targetName)) {
			query.setString("targetName", targetName);
		}		
//		if(!"".equals(status)) {
//			query.setString("status", status);
//		}
//		if(!"".equals(description)) {
//			query.setString("description", description + "%");
//		}
		if(!"".equals(period)) {	
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}
		
		List result = query.list();	

		List<OperationLogChartData> chartDatas = new ArrayList<OperationLogChartData>();
		OperationLogChartData chartData = null;
		
		Object[] resultData = null;		
		String operationCommandName = null;
		String operationCommandId = null;
		String successCnt = null;
		String failCnt = null;
		String totalCnt = null;

		for(int i = 0, size = result.size() ; i < size ; i++) {

			resultData = (Object[])result.get(i);
			operationCommandName = (String)resultData[0];
			operationCommandId = resultData[1].toString(); 
			successCnt = resultData[2].toString();
			failCnt = resultData[3].toString();
			totalCnt = (Integer.parseInt(successCnt) + Integer.parseInt(failCnt)) + "";
			
			chartData = new OperationLogChartData();
			chartData.setOperation(operationCommandName);
			chartData.setOperationCommandId(operationCommandId);
			chartData.setSuccessCnt(successCnt);
			chartData.setFailCnt(failCnt);
			chartData.setCnt(totalCnt);
			
			chartDatas.add(chartData);					
		}

		return setProgressBarWidth(chartDatas, supplierId);		
	}
	
	@SuppressWarnings("unchecked")
	public List<OperationLogChartData> getColumnChartData(Map<String, String> conditionMap) {

		String operatorType = conditionMap.get("operatorType");
		String userId = conditionMap.get("userId");
		String targetType = conditionMap.get("targetType");
		String targetName = conditionMap.get("targetName");
		String status = conditionMap.get("status");
		String description = conditionMap.get("description");
		String operation = conditionMap.get("operation");
		String period = conditionMap.get("period");
		String startDate = conditionMap.get("startDate");
		String endDate = conditionMap.get("endDate");
		String supplierId = StringUtil.nullToBlank(conditionMap.get("supplierId"));
		
		String groupByCondition = makeGroupByCondition(period, startDate, endDate);
		
		// bizRule => status[0:성공, 1, 2, 3:실패]
		StringBuffer sbQry = new StringBuffer();
		 sbQry.append(" SELECT t1.date1, t1.OPERATOR_TYPE,\n");
		 sbQry.append("        SUM(CASE WHEN t1.status = 0 THEN t1.CNT ELSE 0 END) success, \n");
		 sbQry.append("        SUM(CASE WHEN t1.status <> 0 OR t1.status IS NULL THEN t1.CNT ELSE 0 END) fail    \n");    // status=null 일 경우 fail 에 추가
		 sbQry.append("   FROM (SELECT " + groupByCondition + " date1, OPERATOR_TYPE, COUNT(*) cnt ,       \n");
		 sbQry.append("     CASE WHEN (lower(ERROR_Reason) LIKE '%success%' ) THEN 0 ELSE 1 END STATUS       \n");
		 sbQry.append("           FROM OPERATION_LOG                                        \n");
		 sbQry.append("          WHERE id is not null                                       \n");
		
		 if(!supplierId.isEmpty()) {
			 sbQry.append(" AND supplier_Id = :supplierId");
		 }
		if(!"".equals(operatorType)) {
			sbQry.append(" AND OPERATOR_TYPE = :operatorType \n");
		}		
		if(!"".equals(userId)) {
			sbQry.append(" AND USER_ID = :userId");
		}
		if(!"".equals(targetType)) {
			sbQry.append(" AND TARGET_TYPE_CODE = :targetType \n");
		}		
		if(!"".equals(targetName)) {
			sbQry.append(" AND TARGET_NAME = :targetName \n");
		}		
		if(status != null && !"".equals(status)) {
			if("0".equals(status)) {
				sbQry.append(" AND lower(ERROR_REASON) like '%success%' ");
			} else {
				sbQry.append(" AND (lower(ERROR_REASON) <> 'success' OR ERROR_REASON IS NULL) ");
			}
		}
		
		if(!"".equals(operation)) {
			sbQry.append(" AND OPERATION_COMMAND_CODE = :operation");
		}	
		if(!"".equals(period)) {			
			//sp-1053 Using 'YYYYMMDDHHMMSS' column for search condition
			/*if(DateType.HOURLY.getCode().equals(period)) {
				sbQry.append(" AND YYYYMMDDHHMMSS >= :startDate "); 			// || substr(HHMMSS, 1, 2) >= :startDate ");
				sbQry.append(" AND YYYYMMDDHHMMSS <= :endDate "); 				// || substr(HHMMSS, 1, 2) <= :endDate ");
			}else {
				sbQry.append(" AND YYYYMMDD >= :startDate ");						
				sbQry.append(" AND YYYYMMDD <= :endDate ");			
			}*/
			sbQry.append(" AND YYYYMMDDHHMMSS >= :startDate ");
			sbQry.append(" AND YYYYMMDDHHMMSS <= :endDate ");
		}		

		sbQry.append("          GROUP BY " + groupByCondition + ", OPERATOR_TYPE, ERROR_REASON) t1  \n");
		sbQry.append(" GROUP BY t1.date1, t1.OPERATOR_TYPE                                     \n");
		sbQry.append(" ORDER BY t1.date1 desc                                     \n");
		   		
		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQry.toString()));
		if(!supplierId.isEmpty()) {
			query.setInteger("supplierId", Integer.parseInt(supplierId));
		}
		if(!"".equals(operatorType)) {
			query.setString("operatorType", operatorType);
		}		
		if(!"".equals(userId)) {
			query.setString("userId", userId);
		}
		if(!"".equals(targetType)) {
			query.setString("targetType", targetType);
		}				
		if(!"".equals(targetName)) {
			query.setString("targetName", targetName);
		}		
		if(!"".equals(conditionMap.get("operation"))) {
			query.setString("operation", operation);
		}
		if(!"".equals(period)) {	
			query.setString("startDate", startDate);
			query.setString("endDate", endDate);
		}	

		List result = query.list();	

		List<OperationLogChartData> chartDatas = new ArrayList<OperationLogChartData>();
		OperationLogChartData chartData = null;
		Object[] resultData = null;		
		String operatorTypeName = null;
		
		for(int i = 0, size = result.size() ; i < size ; i++) {
							
			resultData = (Object[])result.get(i);
			
			//bizRule => operatorType[0:System, 1:Operator]
			if("0".equals(resultData[1].toString())) {
				operatorTypeName = "System";
			} else {
				operatorTypeName = "Operator";
			}
			
			//jhkim
			Supplier supplier = null;
			String tempDate = null;
			if(!conditionMap.get("supplierId").equals("")) {
				supplier = supplierDao.get(Integer.parseInt(conditionMap.get("supplierId")));
				if(DateType.HOURLY.getCode().equals(period)) {//hourly search
					tempDate = TimeLocaleUtil.getLocaleDateHour(StringUtil.nullToBlank(resultData[0].toString()) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
				}else {
					tempDate = TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(resultData[0].toString()) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
				}
				
			} else {
				tempDate = resultData[0].toString();
			}
			
			
			
			
			chartData = new OperationLogChartData();
			chartData.setDateOperatorType(tempDate + "\n" + operatorTypeName);
			chartData.setDate(tempDate);
			chartData.setOperatorType(resultData[1].toString());
			chartData.setSuccessCnt(resultData[2].toString());
			chartData.setFailCnt(resultData[3].toString());
							
			chartDatas.add(chartData);
		}
		
		return chartDatas;	
	}
	
	private String makeGroupByCondition(String period, String startDate, String endDate) {

		String groupByCondition = "YYYYMMDD";

		if(DateType.HOURLY.getCode().equals(period)) {
//			groupByCondition = "YYYYMMDDHHMMSS ";
			groupByCondition = "substr(YYYYMMDDHHMMSS, 1, 10)"; // 시간별 검색일 경우는 시간까지만 그룹핑한다.(초단위까지 그룹핑 하지 않는다.)
		} else if(DateType.MONTHLYPERIOD.getCode().equals(period)) {
			groupByCondition = "YYYYMMDD ";
		}
		
		return groupByCondition;
	}

	@SuppressWarnings("unchecked")
	public List<OperationLog> getGridData(Map<String, String> conditionMap) {		
		
		String operatorType = conditionMap.get("operatorType");
		String userId = conditionMap.get("userId");
		String targetType = conditionMap.get("targetType");
		String targetName = conditionMap.get("targetName");
		String status = conditionMap.get("status");
		String description = conditionMap.get("description");
		String operation = conditionMap.get("operation");
		String status2 = conditionMap.get("status2");
		String period = conditionMap.get("period");
		String startDate = conditionMap.get("startDate");
		String endDate = conditionMap.get("endDate");
		String date = conditionMap.get("date");
		String supplierId = StringUtil.nullToBlank(conditionMap.get("supplierId"));
		Boolean isHourlySearch= DateType.HOURLY.getCode().equals(period);
		
		StringBuffer sbQry = new StringBuffer()
		.append("   FROM OperationLog log where log.id is not null \n");

		if(!supplierId.isEmpty()) {
			sbQry.append("   AND log.supplierId = :supplierId \n");
		}

		if(operatorType != null && !"".equals(operatorType)) {
			sbQry.append(" AND log.operatorType = :operatorType \n");
		}		
		if(userId != null && !"".equals(userId)) {
			sbQry.append(" AND log.userId = :userId");
		}
		if(targetType != null && !"".equals(targetType)) {
			sbQry.append(" AND log.targetTypeCode.id = :targetType \n");
		}		
		if(targetName != null && !"".equals(targetName)) {
			sbQry.append(" AND log.targetName = :targetName \n");
		}		

		if(status != null && !"".equals(status)) {
			if("0".equals(status)) {
				sbQry.append(" AND lower(ERROR_REASON) like '%success%' ");
			} else {
				sbQry.append(" AND (lower(ERROR_REASON) not like '%success%' OR ERROR_REASON IS NULL) ");
			}
		}

		if(operation != null && !"".equals(operation)) {
			sbQry.append(" AND OPERATION_COMMAND_CODE = :operation");
		}	

		//sp-1053 Using 'YYYYMMDDHHMMSS' column for search condition
		/*if(!"".equals(period)) {
			
			if("".equals(date)) {				

				if(isHourlySearch) {
					sbQry.append(" AND YYYYMMDDHHMMSS >= :startDate "); 			// || substr(HHMMSS, 1, 2) >= :startDate ");
					sbQry.append(" AND YYYYMMDDHHMMSS <= :endDate "); 				// || substr(HHMMSS, 1, 2) <= :endDate ");
				}else {
					sbQry.append(" AND YYYYMMDD >= :startDate ");						
					sbQry.append(" AND YYYYMMDD <= :endDate ");			
				}	
			} else {			
				if(isHourlySearch) {
					sbQry.append(" AND YYYYMMDDHHMMSS like :date ");					//YYYYMMDD || substr(HHMMSS, 1, 2) = :date ");
				}else {
					sbQry.append(" AND YYYYMMDD = :date ");
				}
			}
		}
		if(isHourlySearch){
			sbQry.append(" ORDER BY YYYYMMDDHHMMSS DESC");
		}else{
		    //sbQry.append(" ORDER BY YYYYMMDD DESC");
            // 2016-02-25 모든 DateType에 대하여 시간까지 고려하여 정렬하도록 함.
            sbQry.append(" ORDER BY YYYYMMDDHHMMSS DESC");
		}*/
		if(!"".equals(period)) {
			
			if("".equals(date)) {				

					sbQry.append(" AND YYYYMMDDHHMMSS >= :startDate "); 			// || substr(HHMMSS, 1, 2) >= :startDate ");
					sbQry.append(" AND YYYYMMDDHHMMSS <= :endDate "); 				// || substr(HHMMSS, 1, 2) <= :endDate ");
				
			} else {			
					sbQry.append(" AND YYYYMMDDHHMMSS like :date ");					//YYYYMMDD || substr(HHMMSS, 1, 2) = :date ");
			}
		}
		sbQry.append(" ORDER BY YYYYMMDDHHMMSS DESC");
		
		
		Query query = getSession().createQuery(sbQry.toString());
		if(!supplierId.isEmpty()) {
			query.setInteger("supplierId", Integer.parseInt(supplierId));
		}

		if(operatorType != null && !"".equals(operatorType)) {
			query.setString("operatorType", operatorType);
		}		
		if(userId != null && !"".equals(userId)) {
			query.setString("userId", userId);
		}
		if(targetType != null && !"".equals(targetType)) {
			query.setString("targetType", targetType);
		}		
		
		if(targetName != null && !"".equals(targetName)) {
			query.setString("targetName", targetName);
		}		

		if(operation != null && !"".equals(operation)) {
			query.setString("operation", operation);
		}	
		if(period != null && !"".equals(period)) {	
			
			if("".equals(date)) {				
				query.setString("startDate", startDate);
				query.setString("endDate", endDate);
			} else {	
				//sp-1053 Using 'YYYYMMDDHHMMSS' column for search condition
				if(isHourlySearch) {
					query.setString("date", date.substring(0,10)+"%");
				} else {
					query.setString("date", date);
				}
				
			}
		}		

		Boolean isExcel = Boolean.parseBoolean(conditionMap.get("isExcel"));
		String strPage = conditionMap.get("page");
		String strPageSize = conditionMap.get("pageSize");
		
		if(!isExcel) {
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
        }
		
		return query.list();	
	}
	
	public String getGridDataCount(Map<String, String> conditionMap) {
		return null;
	}
	
	public String getOperationLogMaxGridDataCount(Map<String, String> conditionMap) {

		String operatorType = conditionMap.get("operatorType");
		String userId = conditionMap.get("userId");
		String targetType = conditionMap.get("targetType");
		String targetName = conditionMap.get("targetName");
		String status = conditionMap.get("status");
		String description = conditionMap.get("description");
		String operation = conditionMap.get("operation");
		String status2 = conditionMap.get("status2");
		String period = conditionMap.get("period");
		String startDate = conditionMap.get("startDate");
		String endDate = conditionMap.get("endDate");
		String date = conditionMap.get("date");
		String supplierId = StringUtil.nullToBlank(conditionMap.get("supplierId"));
		
		StringBuffer sbQry = new StringBuffer()
		.append("select count(log) AS cnt FROM OperationLog log where log.id is not null \n");

		if(!supplierId.isEmpty()) {
			sbQry.append(" AND log.supplierId = :supplierId \n");
		}
		if(operatorType != null && !"".equals(operatorType)) {
			sbQry.append(" AND log.operatorType = :operatorType \n");
		}		
		if(userId != null && !"".equals(userId)) {
			sbQry.append(" AND log.userId = :userId");
		}
		if(targetType != null && !"".equals(targetType)) {
			sbQry.append(" AND log.targetTypeCode.id = :targetType \n");
		}		
		if(targetName != null && !"".equals(targetName)) {
			sbQry.append(" AND log.targetName = :targetName \n");
		}		
		if(status != null && !"".equals(status)) {
			if("0".equals(status)) {
				sbQry.append(" AND lower(ERROR_REASON) like '%success%' ");
			} else {
				sbQry.append(" AND (lower(ERROR_REASON) <> 'success' OR ERROR_REASON IS NULL) ");
			}
		}
		if(operation != null && !"".equals(operation)) {
			sbQry.append(" AND OPERATION_COMMAND_CODE = :operation");
		}	

		if(!"".equals(period)) {
			//sp-1053 Using 'YYYYMMDDHHMMSS' column for search condition
			/*if("".equals(date)) {				
				if(DateType.HOURLY.getCode().equals(period)) {
					sbQry.append(" AND YYYYMMDDHHMMSS >= :startDate ");
					sbQry.append(" AND YYYYMMDDHHMMSS <= :endDate ");
				}else {
					sbQry.append(" AND YYYYMMDD >= :startDate ");						
					sbQry.append(" AND YYYYMMDD <= :endDate ");			
				}	
			} else {			
				if(DateType.HOURLY.getCode().equals(period)) {
					sbQry.append(" AND YYYYMMDDHHMMSS = :date ");
				}else {
					sbQry.append(" AND YYYYMMDD = :date ");
				}
			}*/
			if("".equals(date)) {				
				sbQry.append(" AND YYYYMMDDHHMMSS >= :startDate ");
				sbQry.append(" AND YYYYMMDDHHMMSS <= :endDate ");
			} else {			
				sbQry.append(" AND YYYYMMDDHHMMSS = :date ");
			}
		}
		
		Query query = getSession().createQuery(sbQry.toString());	

		if(!supplierId.isEmpty()) {
			query.setInteger("supplierId", Integer.parseInt(supplierId));
		}
		if(operatorType != null && !"".equals(operatorType)) {
			query.setString("operatorType", operatorType);
		}		
		if(userId != null && !"".equals(userId)) {
			query.setString("userId", userId);
		}
		if(targetType != null && !"".equals(targetType)) {
			query.setString("targetType", targetType);
		}		
		
		if(targetName != null && !"".equals(targetName)) {
			query.setString("targetName", targetName);
		}		
		if(operation != null && !"".equals(operation)) {
			query.setString("operation", operation);
		}	
		if(period != null && !"".equals(period)) {	
			
			if("".equals(date)) {				
				query.setString("startDate", startDate);
				query.setString("endDate", endDate);
			} else {			
				query.setString("date", date);
			}
		}		
		
		@SuppressWarnings("unused")
		Object obj = query.uniqueResult();
		//return query.uniqueResult().toString();
		return obj.toString();
	}

    /**
     * method name : getMcuOperationLogList<b/>
     * method Desc : Concentrator Management 맥스가젯 History 탭에서 명령내역을 조회한다. 
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMcuOperationLogList(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        String targetName = StringUtil.nullToBlank(conditionMap.get("targetName"));
        String startDate = StringUtil.nullToBlank(conditionMap.get("startDate"));
        String endDate = StringUtil.nullToBlank(conditionMap.get("endDate"));
        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT op.yyyymmddhhmmss AS yyyymmddhhmmss, ");
            sb.append("\n       op.targetTypeCode.descr AS targetTypeCode, ");
            sb.append("\n       op.operatorType AS operatorType, ");
            sb.append("\n       op.userId AS userId, ");
            sb.append("\n       op.targetName AS targetName, ");
            sb.append("\n       op.description AS description ");
        }
        sb.append("\nFROM OperationLog op ");
        sb.append("\nWHERE op.targetName = :targetName ");
        sb.append("\nAND   op.yyyymmdd BETWEEN :startDate AND :endDate ");
        if (!isCount) {
            sb.append("\nORDER BY op.yyyymmddhhmmss DESC ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setString("targetName", targetName);
        query.setString("startDate", startDate);
        query.setString("endDate", endDate);

        if (isCount) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("total", ((Number)query.uniqueResult()).intValue());
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
}