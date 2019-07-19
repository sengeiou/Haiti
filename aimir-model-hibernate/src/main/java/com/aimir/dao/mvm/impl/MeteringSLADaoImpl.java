package com.aimir.dao.mvm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeteringFailReason;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.mvm.MeteringSLADao;
import com.aimir.model.mvm.MeteringSLA;
import com.aimir.util.StringUtil;

@Repository(value = "meteringslaDao")
public class MeteringSLADaoImpl extends AbstractHibernateGenericDao<MeteringSLA, Integer> implements MeteringSLADao {
	
	private static Log logger = LogFactory.getLog(MeteringSLADaoImpl.class);
	    
	@Autowired
	protected MeteringSLADaoImpl(SessionFactory sessionFactory) {
		super(MeteringSLA.class);
		super.setSessionFactory(sessionFactory);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Object> getMeteringSLASummaryGrid(Map<String, Object> condition){	
		
		String searchStartDate 	= StringUtil.nullToBlank(condition.get("searchStartDate"));
		String searchEndDate 	= StringUtil.nullToBlank(condition.get("searchEndDate"));
		String supplierId 		= StringUtil.nullToBlank(condition.get("supplierId"));
		
		List<Object> result      	= new ArrayList<Object>();
		List<Object> gridData 		= new ArrayList<Object>();		
		StringBuffer sbQuery 		= new StringBuffer();
		
		sbQuery.append("   SELECT SUM(total_installed_meters)  AS totalInstalledMeters  \n")
			   .append("        , SUM(comm_permitted_meters)   AS commPermittedMeters  	\n")
			   .append("        , SUM(permitted_meters)        AS permittedMeters  		\n")
			   .append("     FROM METERING_SLA  						\n")
			   .append("    WHERE SUPPLIER_ID = " + supplierId + "		\n")
			   .append("      AND YYYYMMDD >= '"+ searchStartDate +"'   \n")
			   .append("      AND YYYYMMDD <= '"+ searchEndDate   +"'	\n");
			   
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		List queryResultList = query.list();
				
		if( queryResultList != null){
			
			Object[] resultData = (Object[]) queryResultList.get(0);
			
			double totalMeters = Double.parseDouble(resultData[0] == null?"0":resultData[0].toString());
			
			for(int i =0; i < 3 ; i++){
				
				double slaPercent  = 0;
				double slaCount  = Double.parseDouble(resultData[i] == null?"0":resultData[i].toString());
				
				if(totalMeters != 0)
					slaPercent  = Double.parseDouble(String.format("%.2f", (slaCount  / totalMeters) * 100));
				
				HashMap chartDataMap = new HashMap();
				
				chartDataMap = new HashMap();
				chartDataMap.put("label", 		"");
				chartDataMap.put("slaCount", 	slaCount);
				chartDataMap.put("slaPercent",  slaPercent + "%");
				
				gridData.add(chartDataMap);
			}
				
		}
	
    	result.add(gridData);
		return result;
	}

	
	@SuppressWarnings("unchecked")
	public List<Object> getMeteringSLAList(Map<String, Object> condition){	
		
		String searchStartDate 	= StringUtil.nullToBlank(condition.get("searchStartDate"));
		String searchEndDate 	= StringUtil.nullToBlank(condition.get("searchEndDate"));
		String supplierId 		= StringUtil.nullToBlank(condition.get("supplierId"));
		String page 			= StringUtil.nullToBlank(condition.get("page"));
		Integer rowPerPage 		= CommonConstants.Paging.ROWPERPAGE.getPageNum();
		List<Object> result      	= new ArrayList<Object>();
		List<Object> gridData 		= new ArrayList<Object>();		
		StringBuffer sbQuery 		= new StringBuffer();
		
		sbQuery.append("   SELECT yyyymmdd  				\n")
			   .append("        , comm_permitted_meters  	\n")
			   .append("        , delivered_meters  		\n")
			   .append("        , permitted_meters  		\n")
			   .append("        , sla_meters  				\n")
			   .append("        , success_rate  			\n")
			   .append("        , total_gathered_meters  	\n")
			   .append("        , total_installed_meters  	\n")
			   .append("     FROM METERING_SLA  						\n")
			   .append("    WHERE SUPPLIER_ID = " + supplierId +       "\n")
			   .append("      AND YYYYMMDD >= '"+ searchStartDate +"'   \n")
			   .append("      AND YYYYMMDD <= '"+ searchEndDate   +"'	\n")
		       .append("      ORDER BY YYYYMMDD	\n");
			   
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		
		if(page !=null && !"".equals(page)){
			int firstPage  = Integer.valueOf(page) * rowPerPage;
			query.setFirstResult(firstPage).setMaxResults(rowPerPage);
		}
		
		List queryResultList = query.list();

		int queryResultListLen = 0;
		
		if( queryResultList != null)
			queryResultListLen = queryResultList.size();
		
		
		for(int i=0 ; i < queryResultListLen ; i++){
			
			Object[] resultData = (Object[]) queryResultList.get(i);
			HashMap gridDataMap = new HashMap();
			
			gridDataMap.put("xTag", 					resultData[0].toString());
			gridDataMap.put("commPermittedMeters", 		resultData[1].toString());
			gridDataMap.put("deliveredMeters", 			resultData[2].toString());
			gridDataMap.put("permittedMeters", 			resultData[3].toString());
			gridDataMap.put("slaMeters", 				resultData[4].toString());
			gridDataMap.put("successRate", 				resultData[5].toString());
			gridDataMap.put("totalGatheredMeters", 		resultData[6].toString());
			gridDataMap.put("totalInstalledMeters", 	resultData[7].toString());  
			
			gridData.add(gridDataMap);
			
		}
		
		result.add(gridData);
		
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Object> getMeteringSLAMissingData(Map<String, Object> condition){	
		
		
		String searchStartDate 	= StringUtil.nullToBlank(condition.get("searchStartDate"));
		String searchEndDate 	= StringUtil.nullToBlank(condition.get("searchEndDate"));
		String page 	= StringUtil.nullToBlank(condition.get("page"));
		List<Object> result      	= new ArrayList<Object>();
		List<Object> gridData 		= new ArrayList<Object>();		
		StringBuffer sbQuery 		= new StringBuffer();
		StringBuffer countQuery 	= new StringBuffer();
		
		
		countQuery.append(" SELECT COUNT(*)						\n")
			      .append("   FROM (							\n")
			      .append("        SELECT MDEV_ID				\n")
			      .append("             , COUNT(*) AS missedDays	\n")
			      .append("          FROM METERING_FAIL			\n")
			      .append("         WHERE MDEV_TYPE = 2			\n")
			      .append("           AND YYYYMMDD >= '"+ searchStartDate +"'  \n")
			      .append("           AND YYYYMMDD <= '"+ searchEndDate   +"'	\n")			   
			      .append("         GROUP BY MDEV_ID			\n")
			      .append("        ) T1							\n");
		
		SQLQuery countQueryObj = getSession().createSQLQuery(countQuery.toString());
		int totalCount = Integer.parseInt(countQueryObj.uniqueResult().toString());
		
		
		sbQuery.append(" SELECT missedDays					\n")
			   .append("      , COUNT(*)					\n")
			   .append("   FROM (							\n")
			   .append("        SELECT MDEV_ID				\n")
			   .append("             , COUNT(*) AS missedDays	\n")
			   .append("          FROM METERING_FAIL		\n")
			   .append("         WHERE MDEV_TYPE = 2		\n")
			   .append("           AND YYYYMMDD >= '"+ searchStartDate +"'  \n")
			   .append("           AND YYYYMMDD <= '"+ searchEndDate   +"'	\n")			   
			   .append("         GROUP BY MDEV_ID			\n")
			   .append("        ) T1						\n")
			   .append("  GROUP BY missedDays   			\n")
			   .append("  ORDER BY missedDays   			\n");
			   
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		
		if(page !=null && !"".equals(page)){
			Integer rowPerPage 		= CommonConstants.Paging.ROWPERPAGE.getPageNum();
			int firstPage  = Integer.valueOf(page) * rowPerPage;
			query.setFirstResult(firstPage).setMaxResults(rowPerPage);
		}
		
		List queryResultList = query.list();
		
		int queryResultListLen = 0;
		
		if( queryResultList != null)
			queryResultListLen = queryResultList.size();		
		
		for(int i=0 ; i < queryResultListLen ; i++){
			
			Object[] resultData = (Object[]) queryResultList.get(i);
			HashMap gridDataMap = new HashMap();
			
			gridDataMap.put("missedDays",				resultData[0].toString());
			gridDataMap.put("count", 					resultData[1].toString());
			
			Double missedDaysPercnet  = Double.parseDouble(String.format("%.2f", ( Double.parseDouble(resultData[1].toString()) / totalCount) * 100));			
			gridDataMap.put("missedDaysPercnet", 			missedDaysPercnet.toString());
			
			gridData.add(gridDataMap);
		}
		
		HashMap gridDataMap = new HashMap();
		
		gridDataMap.put("missedDays",				"Total");
		gridDataMap.put("count", 					totalCount);
		
		gridDataMap.put("missedDaysPercnet", 		"100%");
		
		gridData.add(gridDataMap);
		result.add(gridData);
		result.add(totalCount);
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getMeteringSLAMissingDetailChart(Map<String, Object> condition){
		
		String searchStartDate 	= StringUtil.nullToBlank(condition.get("searchStartDate"));
		String searchEndDate 	= StringUtil.nullToBlank(condition.get("searchEndDate"));
		String missedDay 		= StringUtil.nullToBlank(condition.get("missedDay"));
		
		List<Object> result      	= new ArrayList<Object>();
		List<Object> gridData 		= new ArrayList<Object>();		
		StringBuffer sbQuery 		= new StringBuffer();
		
		sbQuery.append(" SELECT FAIL_REASON					\n")
		       .append("      , COUNT(*) as count			\n")
		       .append("   FROM METERING_FAIL				\n")
		       .append("  WHERE mdev_ID in (				\n")
		       .append("        SELECT MDEV_ID				\n")
		       .append("          FROM METERING_FAIL		\n")
		       .append("         WHERE MDEV_TYPE = 2		\n")
		       .append("           AND YYYYMMDD >= '"+ searchStartDate +"'  \n")
		       .append("           AND YYYYMMDD <= '"+ searchEndDate   +"'	\n")			   
		       .append("         GROUP BY MDEV_ID							\n")
		       .append("        HAVING COUNT(*) = "+ missedDay +" )			\n")
		       .append("  GROUP BY FAIL_REASON   			\n")
		       .append("  ORDER BY FAIL_REASON   			\n");
		
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		List queryResultList = query.list();
		
		int queryResultListLen = 0;
		
		if( queryResultList != null)
			queryResultListLen = queryResultList.size();
		
		
		for(int i=0 ; i < queryResultListLen ; i++){
			
			Object[] resultData = (Object[]) queryResultList.get(i);
			HashMap gridDataMap = new HashMap();
			
			for(MeteringFailReason  _failReson : MeteringFailReason.values())
				if(_failReson.getCode() == ((Number) resultData[0]).intValue())
					gridDataMap.put("label",		_failReson.name());
			
			gridDataMap.put("data", 		resultData[1].toString());
			gridDataMap.put("failReason", 	resultData[0].toString());
			
			gridData.add(gridDataMap);
			
		}
		
		result.add(gridData);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getMeteringSLAMissingDetailGrid(Map<String, Object> condition){	
		
		String searchStartDate 	= StringUtil.nullToBlank(condition.get("searchStartDate"));
		String searchEndDate 	= StringUtil.nullToBlank(condition.get("searchEndDate"));
		String missedDay 		= StringUtil.nullToBlank(condition.get("missedDay"));
		String missedReason 	= StringUtil.nullToBlank(condition.get("missedReason"));
//		String curPage 			= StringUtil.nullToBlank(condition.get("curPage"));
		
		
		List<Object> result      	= new ArrayList<Object>();
		List<Object> gridData 		= new ArrayList<Object>();		
		StringBuffer sbQuery 		= new StringBuffer();
		
		sbQuery.append(" SELECT distinct contract.contract_number		\n")
	           .append("      , meter.mds_id					\n")
	           .append("      , modem.device_serial				\n")
	           .append("      , meter.last_Read_Date			\n")
	           .append("   FROM METERING_FAIL mf				\n")
	           .append("   LEFT OUTER JOIN CONTRACT contract	\n")
	           .append("     ON (mf.contract_ID = contract.ID)	\n")
	           .append("   LEFT OUTER JOIN METER meter 			\n")
	           .append("     ON (mf.meter_ID = meter.ID)		\n")
	           .append("   LEFT OUTER JOIN MODEM modem 			\n")
	           .append("     ON (mf.modem_ID = modem.ID)		\n")
	           .append("  WHERE mdev_ID in (					\n")
	           .append("        SELECT MDEV_ID					\n")
	           .append("          FROM METERING_FAIL			\n")
	           .append("         WHERE MDEV_TYPE = 2			\n")
	           .append("           AND YYYYMMDD >= '"+ searchStartDate +"'  \n")
	           .append("           AND YYYYMMDD <= '"+ searchEndDate   +"'	\n")			   
	           .append("         GROUP BY MDEV_ID							\n")
	           .append("        HAVING COUNT(*) = "+ missedDay +" )			\n");
		
		if(missedReason.length() > 0)
			sbQuery.append("  AND FAIL_REASON = " + missedReason +"		\n");
		
		
		
		// 전체 건수
		StringBuffer countQuery = new StringBuffer();
		countQuery.append("\n SELECT COUNT(*) ");
		countQuery.append("\n FROM (  ");
		countQuery.append(sbQuery);
		countQuery.append("\n ) countTotal ");
		
		SQLQuery countQueryObj = getSession().createSQLQuery(countQuery.toString());
		                  
		Number totalCount = (Number)countQueryObj.uniqueResult();
		
		result.add(totalCount.toString());
		
		
			   
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		
		// Paging
		/*int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
		int firstIdx  = Integer.parseInt(curPage) * rowPerPage;
		
		query.setFirstResult(firstIdx);
		query.setMaxResults(rowPerPage);*/
		
		
		List queryResultList = query.list();
		
		int queryResultListLen = 0;
		
		if( queryResultList != null)
			queryResultListLen = queryResultList.size();
		
		
		for(int i=0 ; i < queryResultListLen ; i++){
			Object[] resultData = (Object[]) queryResultList.get(i);
			HashMap gridDataMap = new HashMap();
			
			gridDataMap.put("no", 						i+1);
			gridDataMap.put("contractNumber",			StringUtil.nullToBlank(resultData[0]));
			gridDataMap.put("mdsId",			 		StringUtil.nullToBlank(resultData[1]));
			gridDataMap.put("deviceSerial", 			StringUtil.nullToBlank(resultData[2]));
			gridDataMap.put("lastReadDate", 			StringUtil.nullToBlank(resultData[3]));
						
			gridData.add(gridDataMap);
			
		}
		
		result.add(gridData);
		
		return result;
	}	
	
	
	
	
	
}
