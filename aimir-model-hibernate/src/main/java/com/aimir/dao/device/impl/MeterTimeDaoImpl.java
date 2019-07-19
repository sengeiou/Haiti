package com.aimir.dao.device.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.MeterCodes;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MeterTimeDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Supplier;
import com.aimir.util.CommonUtils2;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;



@Repository(value = "meterTimeDao")
public class MeterTimeDaoImpl extends AbstractHibernateGenericDao<Meter, Integer> implements MeterTimeDao {

	Log logger = LogFactory.getLog(MeterTimeDaoImpl.class);

	@Autowired
	SupplierDao supplierDao;
	
	@Autowired
	protected MeterTimeDaoImpl(SessionFactory sessionFactory) {
		super(Meter.class);
		super.setSessionFactory(sessionFactory);
	}

	
/*    
	@SuppressWarnings("unchecked")
    public List<Object> getMeterTimeTimeDiffChart(Map<String, Object> condition){
		
		List<Object> chartData 	= new ArrayList<Object>();		
		StringBuffer sbQuery 	= new StringBuffer();
		
		//  1hour =  3600
		// 12hour = 43200
		// 24hour = 86400
		sbQuery.append(" SELECT SUM( CASE WHEN me.time_diff <  3600                           THEN 1 ELSE 0 END) AS normal  \n")
			   .append("      , SUM( CASE WHEN me.time_diff < 43200 AND me.time_diff >=  3600 THEN 1 ELSE 0 END) AS diff_1  \n")
			   .append("      , SUM( CASE WHEN me.time_diff < 86400 AND me.time_diff >= 43200 THEN 1 ELSE 0 END) AS diff_12 \n")
			   .append("      , SUM( CASE WHEN                          me.time_diff >= 86400 THEN 1 ELSE 0 END) AS diff_24 \n");
		
		// 조건문 적용
		sbQuery.append(setMeterTimeTimeDiffWhere(condition).toString());
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		List timeDiffList = query.list();
				
		if( timeDiffList != null){
			
			Object[] resultData = (Object[]) timeDiffList.get(0);
			
			for(int i = 0 ; i < 4 ; i++){
				
				HashMap chartDataMap = new HashMap();

				chartDataMap.put("label", "");
				chartDataMap.put("data", resultData[i]);
				
				chartData.add(chartDataMap);
			}
		}
		
		List<Object> result      = new ArrayList<Object>();
		
    	result.add(chartData);
    	
    	return result;
    }
*/	
	@SuppressWarnings("unchecked")
    public List<Object> getMeterTimeTimeDiffChart(Map<String, Object> condition){
		StringBuffer sbQuery 	= new StringBuffer();
		
		//  1hour =  3600
		// 12hour = 43200
		// 24hour = 86400
		sbQuery.append(" SELECT SUM( CASE WHEN me.time_diff <  3600 AND me.time_diff >  -3600  THEN 1 ELSE 0 END) AS NORMAL  \n")
			   .append("      , SUM( CASE WHEN (me.time_diff < 43200 AND me.time_diff >=  3600) OR (me.time_diff > -43200 AND me.time_diff <=  -3600) \n")
			   .append("      THEN 1 ELSE 0 END) AS DIFF_1  \n")
			   .append("      , SUM( CASE WHEN (me.time_diff < 86400 AND me.time_diff >= 43200) OR (me.time_diff > -86400 AND me.time_diff <= -43200) \n")
			   .append("      THEN 1 ELSE 0 END) AS DIFF_12 \n")
			   .append("      , SUM( CASE WHEN me.time_diff >= 86400 OR me.time_diff <= -86400 THEN 1 ELSE 0 END) AS DIFF_24 \n")
			   .append("      , SUM( CASE WHEN me.time_diff is null THEN 1 ELSE 0 END) AS DIFF_UNKNOWN \n");
		
		// 조건문 적용
		sbQuery.append(setMeterTimeTimeDiffWhere(condition).toString());
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

/*	
	@SuppressWarnings("unchecked")
    public List<Object> getMeterTimeTimeDiffComplianceChart(Map<String, Object> condition){
		
		

		List<Object> chartData 	= new ArrayList<Object>();		
		StringBuffer sbQuery 	= new StringBuffer();
		
		sbQuery.append(" SELECT SUM( CASE WHEN syncLog.id is not null  THEN 1 ELSE 0 END) AS compliance      \n")
			   .append("      , SUM( CASE WHEN syncLog.id is null  THEN 1 ELSE 0 END)     AS non_compliance  \n");

		// 조건문 적용		
		sbQuery.append(setMeterTimeTimeDiffWhere(condition).toString());
		
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		List timeDiffList = query.list();
				
		if( timeDiffList != null){
		
			
			Object[] resultData = (Object[]) timeDiffList.get(0);
			
			for(int i = 0 ; i < 2 ; i++){
				
				HashMap chartDataMap = new HashMap();

				chartDataMap.put("label", "");
				chartDataMap.put("data", resultData[i]);
				
				chartData.add(chartDataMap);
			}
		}
		
		List<Object> result      = new ArrayList<Object>();
		
    	result.add(chartData);
    	
    	return result;
    }
*/
	
	@SuppressWarnings("unchecked")
    public List<Object> getMeterTimeTimeDiffComplianceChart(Map<String, Object> condition){
		StringBuffer sbQuery 	= new StringBuffer();
		
		sbQuery.append(" SELECT SUM( CASE WHEN last_timesync_date is not null  THEN 1 ELSE 0 END) AS COMPLIANCE      \n")
			   .append("      , SUM( CASE WHEN last_timesync_date is null  THEN 1 ELSE 0 END)     AS NON_COMPLIANCE  \n");

		// 조건문 적용		
		sbQuery.append(setMeterTimeTimeDiffWhere(condition).toString());
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
	
	
	@SuppressWarnings("unchecked")
    public List<Object> getMeterTimeTimeDiffGrid(Map<String, Object> condition){
		
	
		
		int curPage 			= (Integer)condition.get("curPage");
		String supplierId		= condition.get("supplierId").toString();
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		
		List<Object> result      = new ArrayList<Object>();

		List<Object> gridData 	= new ArrayList<Object>();		
		StringBuffer sbQuery 	= new StringBuffer();
		
		sbQuery.append(" SELECT mcu.sys_id			AS sysId	\n")
			   .append("      , me.mds_id	 		AS mdsId	\n")
			   .append("      , customer.name	 	AS cusName	\n")
			   .append("      , contract.contract_number 				AS contractNumber	\n")
			   .append("      , loc.name		 	AS locName	\n")
			   .append("      , me.last_timesync_date	AS lastLinkTime \n")
			   .append("      , me.time_Diff		AS timeDiff	\n");
		
		// 조건문 적용		
		sbQuery.append(setMeterTimeTimeDiffWhere(condition).toString());
		
		
		// totalCount - 시작
		StringBuffer countQuery = new StringBuffer();
		countQuery.append("\n SELECT COUNT(mdsId) ");
		countQuery.append("\n FROM (  ");
		countQuery.append(sbQuery);
		countQuery.append("\n ) countTotal ");
		
		SQLQuery countQueryObj = getSession().createSQLQuery(countQuery.toString());
				                  
		Number totalCount = (Number)countQueryObj.uniqueResult();
		
		result.add(totalCount.toString());
		// totalCount - 종료
		
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		
		// Paging 설정
		int rowPerPage = (Integer)condition.get("pageSize");
		int firstIdx  = curPage * rowPerPage;		
		query.setFirstResult(firstIdx);
		query.setMaxResults(rowPerPage);		
		
		List timeDiffList = query.list();
		
		int timeDiffListLen = 0;
		if(timeDiffList != null)
			timeDiffListLen = timeDiffList.size();
		
		DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
				
		int count=1;
		for(int i=0 ; i < timeDiffListLen ; i++){
			
			Map<String, Object> chartDataMap = new HashMap<String, Object>();
			Object[] resultData = (Object[]) timeDiffList.get(i);
			
			chartDataMap.put("no",           	dfMd.format(CommonUtils2.makeIdxPerPage(String.valueOf(curPage), String.valueOf(rowPerPage), count)));
			chartDataMap.put("mcuSysId",     	resultData[0]);
			chartDataMap.put("meterMdsID",	 	resultData[1]);
			chartDataMap.put("customerName",	resultData[2]);
			chartDataMap.put("contractNumber", 	resultData[3]);
			chartDataMap.put("locName",			resultData[4]);
			chartDataMap.put("lastLinkTime",	resultData[5] ==null? "":TimeLocaleUtil.getLocaleDate(resultData[5].toString() , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter())); //resultData[5]
			chartDataMap.put("timeDiff",		resultData[6] == null ? null : dfMd.format(Long.parseLong(resultData[6].toString())));
			
			gridData.add(chartDataMap);
			count++;
		}
		
		
    	result.add(gridData);
    	
    	return result;
    }
	
	
	// timeDiff에서 사용하는 From / Where 조건 생성
	private StringBuffer setMeterTimeTimeDiffWhere(Map<String, Object> condition){
		
		String mcuSysId 		= StringUtil.nullToBlank(condition.get("mcuSysId"));
		String customerName 	= StringUtil.nullToBlank(condition.get("customerName"));
		String meterMdsId 		= StringUtil.nullToBlank(condition.get("meterMdsId"));
		String contractNumber 	= StringUtil.nullToBlank(condition.get("contractNumber"));

		String timeDiff 		= StringUtil.nullToBlank(condition.get("timeDiff"));
		String time 			= StringUtil.nullToBlank(condition.get("time"));
		String timeType 		= StringUtil.nullToBlank(condition.get("timeType"));

		String compliance 		= StringUtil.nullToBlank(condition.get("compliance"));
		String supplierId 		= StringUtil.nullToBlank(condition.get("supplierId"));

		
		String pre30Day         = DateTimeUtil.calcDate(Calendar.DATE , -30, "yyyyMMdd");
		Integer sec1Hour        = 60*60;
		Integer sec12Hour       = 60*60*12;
		Integer sec24Hour       = 60*60*24;
		
		StringBuffer sbQuery 	= new StringBuffer();
		
		sbQuery
			   .append("   FROM METER me								  \n")
			   .append("   LEFT OUTER JOIN MODEM modem					  \n")
			   .append("     ON (me.MODEM_ID = modem.ID)				  \n")
			   .append("   LEFT OUTER JOIN MCU mcu						  \n")
			   .append("     ON (modem.MCU_ID = mcu.ID)  				  \n")
			   .append("   LEFT OUTER JOIN (SELECT METER_ID as ID		  \n")
			   .append("    				  FROM METERTIMESYNC_LOG	  \n")
			   .append("    				 WHERE WRITE_DATE <= '"+ pre30Day +"'\n")
			   .append("                     GROUP BY METER_ID) syncLog	  \n")
			   .append("     ON (me.ID = syncLog.ID)					  \n")
			   .append("   LEFT OUTER JOIN CONTRACT contract			  \n")
			   .append("     ON (me.ID = contract.METER_ID)		  \n")
			   .append("   LEFT OUTER JOIN CUSTOMER customer			  \n")
			   .append("     ON (contract.customer_ID = customer.ID)	  \n")
			   .append("   LEFT OUTER JOIN LOCATION loc					  \n")
			   .append("     ON (me.LOCATION_ID = loc.ID)				  \n")
			   .append("  WHERE me.SUPPLIER_ID = " + supplierId +        "\n")
		       .append("  AND me.meter_status <> (select id from code where code= '" + MeterCodes.DELETE_STATUS.getCode()  + "') \n");
		
		if(!mcuSysId.equals(""))
			sbQuery.append("    AND mcu.SYS_ID = '" + mcuSysId +"'");
		
		if(!customerName.equals(""))
			sbQuery.append("    AND customer.NAME LIKE '%" + customerName + "%'");
		
		if(!meterMdsId.equals(""))
			sbQuery.append("    AND me.MDS_ID LIKE '%" + meterMdsId + "%'");
		
		if(!contractNumber.equals(""))
			sbQuery.append("    AND contract.CONTRACT_NUMBER = '" + contractNumber + "'");
		
		
		if(timeDiff.equals("1")) {	// 일치
			sbQuery.append("    AND (me.TIME_DIFF < "  + sec1Hour  );
			sbQuery.append("    AND me.TIME_DIFF > "  + -sec1Hour +")" );
		} else if(timeDiff.equals("2")){	// 1 ~ 12시간
			sbQuery.append("    AND   ((me.TIME_DIFF >= "  + sec1Hour  );
			sbQuery.append("    AND   me.TIME_DIFF < " + sec12Hour+")" );
			
			sbQuery.append("    OR ( me.TIME_DIFF <= "  + -sec1Hour  );
			sbQuery.append("    AND   me.TIME_DIFF > " + -sec12Hour +"))" );
		}else if(timeDiff.equals("3")){	// 12 ~ 24시간
			sbQuery.append("    AND ((me.TIME_DIFF >= "  + sec12Hour );
			sbQuery.append("    AND me.TIME_DIFF < " + sec24Hour +")" );
			
			sbQuery.append("    OR (me.TIME_DIFF <= "  + -sec12Hour );
			sbQuery.append("    AND me.TIME_DIFF > " + -sec24Hour +"))" );
		}else if(timeDiff.equals("4")) {	// 24시간이상
			sbQuery.append("    AND (me.TIME_DIFF > " + sec24Hour);
			sbQuery.append("    OR me.TIME_DIFF < " + -sec24Hour + ")");
		}else if(timeDiff.equals("5")) {   // 확인불가(Unknown)
			sbQuery.append("    AND me.TIME_DIFF is null");
		}else if(timeDiff.equals("6")) { //사용자정의
		    sbQuery.append("    AND me.TIME_DIFF >= " + Integer.parseInt(time));
		    sbQuery.append("    AND me.TIME_DIFF is not null");
		}
		/*else if(timeDiff.equals("5")){	// 사용자정의
			
			Integer secTimeDiff = 0;
			
			if(timeType.equals("sec"))
				secTimeDiff = 1;
			else if(timeType.equals("min"))
				secTimeDiff = 60;
			else if(timeType.equals("hour"))
				secTimeDiff = 60*60;
			
			if(time.equals(""))
				time = "0";
			
			sbQuery.append("    AND me.TIME_DIFF > " + Integer.parseInt(time) * secTimeDiff);
		}*/	
		
		
	if(!compliance.equals("")){
		if(compliance.equals("Y"))
			sbQuery.append("    AND last_timesync_date is not null   ");
		else if(compliance.equals("N"))
			sbQuery.append("    AND last_timesync_date is null   ");
	}
	
	
	
		return sbQuery;
	}
	
	

	@SuppressWarnings("unchecked")
	public List<Object> getMeterTimeSyncLogChart(Map<String, Object> condition){
		
		
		String method		= StringUtil.nullToBlank(condition.get("method"));

		String searchStartDate 	= StringUtil.nullToBlank(condition.get("searchStartDate"));
		String searchEndDate 	= StringUtil.nullToBlank(condition.get("searchEndDate"));
		
		List<Object> chartData 		= new ArrayList<Object>();		
		StringBuffer sbQuery 		= new StringBuffer();
		StringBuffer sbQueryDummy 	= new StringBuffer();
		
		
    	// 시작날짜 ~ 종료날짜 
    	SimpleDateFormat dateForm = new SimpleDateFormat("yyyyMMdd");
    	Date startDate = new Date();
    	Date endDate   = new Date();
    	
		try {
			startDate = dateForm.parse(searchStartDate);
			endDate   = dateForm.parse(searchEndDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
    	int diffDays = (int)( (endDate.getTime() - startDate.getTime()) /86400000);
    	
    	List<String> daysList = new ArrayList();

    	Calendar calendar = new GregorianCalendar();
 		calendar.setTime(startDate);
 		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    	 
 		daysList.add(searchStartDate);
 		
 		for(int i=0 ; i<diffDays; i++){
    		 calendar.add(Calendar.DATE, 1);
    		 daysList.add(formatter.format(calendar.getTime()));
    	 }
 		
 		int daysListLen = 0;		
		if(daysList != null)
			daysListLen = daysList.size();
		
		for(int i = 0 ; i < daysListLen ; i++)			
			sbQueryDummy.append("SELECT '" + daysList.get(i) + "'  AS write_date  , 0 AS result1, 0 AS result2, 0 AS result3, 0 AS result4 FROM METERTIMESYNC_LOG \n UNION ALL \n" );
		
	
		sbQuery.append("  SELECT SUBSTR(write_date, 1, 8) as write_date \n")
			   .append("       , SUM(result1) AS result1				\n")
			   .append("       , SUM(result2) AS result2				\n")
			   .append("       , SUM(result3) AS result3				\n")
			   .append("       , SUM(result4) AS reulst4				\n")
			   .append("    FROM (     									\n")
			   .append(sbQueryDummy.toString())
			   .append("  SELECT SUBSTR(syncLog.write_date, 1, 8) AS write_date		\n");
			   
				//  1hour =  3600
				// 12hour = 43200
				// 24hour = 86400
			   if(method.equals("Auto")){
				   sbQuery.append("       , SUM( CASE WHEN ((syncLog.time_diff < 3600)  AND (syncLog.time_diff > -3600))  THEN 1 ELSE 0 END) AS result1	\n")
					      .append("       , SUM( CASE WHEN (syncLog.time_diff < 43200 AND syncLog.time_diff >= 3600)  OR (syncLog.time_diff > -43200 AND syncLog.time_diff <= -3600)	\n")
					      .append("        	THEN 1 ELSE 0 END) AS result2 \n")
					      .append("       , SUM( CASE WHEN (syncLog.time_diff < 86400 AND syncLog.time_diff >= 43200) OR (syncLog.time_diff > -86400 AND syncLog.time_diff <= -43200)	\n")
					      .append("       THEN 1 ELSE 0 END) AS result3	\n")
					      .append("       , SUM( CASE WHEN  syncLog.time_diff >= 86400 OR syncLog.time_diff <= -86400 THEN 1 ELSE 0 END) AS result4	\n");
			   }else if(method.equals("Manual")){
			   sbQuery.append("       , SUM( CASE WHEN syncLog.result = 0 THEN 1 ELSE 0 END) AS result1	\n")
				      .append("       , SUM( CASE WHEN syncLog.result = 1 THEN 1 ELSE 0 END) AS result2 \n")
				      .append("       , SUM( CASE WHEN syncLog.result = 2 THEN 1 ELSE 0 END) AS result3	\n")
				      .append("       , SUM( CASE WHEN syncLog.result = 3 THEN 1 ELSE 0 END) AS result4	\n");
			   }
			   
	    // Where 조건
        sbQuery.append(setMeterTimeSyncLogWhere(condition).toString());
		
		sbQuery.append("   GROUP BY SUBSTR(syncLog.write_date, 1, 8)		\n")
			   .append("       ) rowData					\n")
			   .append("   GROUP BY SUBSTR(write_date, 1, 8)				\n")
			   .append("   ORDER BY SUBSTR(write_date, 1, 8)				\n");
			   
		
		SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sbQuery.toString()));
		List dataList = query.list();
		
		int dataListLen = 0;
		if( dataList != null){
			dataListLen = dataList.size();
			
			for (int i=0 ; i<dataListLen ;i++){

				Object[] resultData = (Object[]) dataList.get(i);
				
				HashMap chartDataMap = new HashMap();
	
				chartDataMap.put("xTag", 		resultData[0]);
				chartDataMap.put("result1", 	resultData[1]);
				chartDataMap.put("result2", 	resultData[2]);
				chartDataMap.put("result3",	 	resultData[3]);
				chartDataMap.put("result4", 	resultData[4]);
				
				chartData.add(chartDataMap);
			}
		}
		
		List<Object> result      = new ArrayList<Object>();
		
		result.add(chartData);
		
		return result;
		
	}
	
    

	@SuppressWarnings("unchecked")
	public List<Object> getMeterTimeSyncLogAutoChart(Map<String, Object> condition){

		List<Object> chartData 		= new ArrayList<Object>();		
		StringBuffer sbQuery 		= new StringBuffer();

	
		sbQuery.append("  SELECT mcu.sys_id as mcuID		\n")
			   .append("       , SUM( CASE WHEN syncLog.result = 0 THEN 1 ELSE 0 END) AS result1	\n")
			   .append("       , SUM( CASE WHEN syncLog.result = 1 THEN 1 ELSE 0 END) AS result2    \n")
			   .append("       , SUM( CASE WHEN syncLog.result = 2 THEN 1 ELSE 0 END) AS result3	\n")
			   .append("       , SUM( CASE WHEN syncLog.result = 3 THEN 1 ELSE 0 END) AS result4	\n")
			   .append("       , COUNT(mcu.sys_id) AS result5				\n");
		
	    // Where 조건
        sbQuery.append(setMeterTimeSyncLogWhere(condition).toString());

		sbQuery.append("     AND mcu.sys_id is not null \n")
		       .append("   GROUP BY mcu.sys_id			\n")
			   .append("   ORDER BY result5				\n");
			   
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		List dataList = query.list();
		
		int dataListLen = 0;
		if( dataList != null){
			dataListLen = dataList.size()>10?10:dataList.size();
			
			for (int i=0 ; i< dataListLen ;i++){

				Object[] resultData = (Object[]) dataList.get(i);
				
				HashMap chartDataMap = new HashMap();
	
				chartDataMap.put("xTag", 		resultData[0]);
				//chartDataMap.put("result1", 	resultData[1]);
				chartDataMap.put("result2", 	resultData[2]);
				chartDataMap.put("result3",	 	resultData[3]);
				chartDataMap.put("result4", 	resultData[4]);
				
				chartData.add(chartDataMap);
			}
		}
		
		List<Object> result      = new ArrayList<Object>();
		
		result.add(chartData);
		
		return result;
		
	}

/*	
	@SuppressWarnings("unchecked")
	public List<Object> getMeterTimeSyncLogManualChart(Map<String, Object> condition){
		
		List<Object> chartData 		= new ArrayList<Object>();		
		StringBuffer sbQuery 		= new StringBuffer();
	
		sbQuery.append("  SELECT SUM( CASE WHEN syncLog.result = 0 THEN 1 ELSE 0 END) AS result1	\n")
			   .append("       , SUM( CASE WHEN syncLog.result = 1 THEN 1 ELSE 0 END) AS result2 	\n")
			   .append("       , SUM( CASE WHEN syncLog.result = 2 THEN 1 ELSE 0 END) AS result3	\n")
			   .append("       , SUM( CASE WHEN syncLog.result = 3 THEN 1 ELSE 0 END) AS result4	\n");

	    // Where 조건
        sbQuery.append(setMeterTimeSyncLogWhere(condition).toString());
        
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		List dataList = query.list();
		
		
		if( dataList != null){
			
			Object[] resultData = (Object[]) dataList.get(0);
			
			for(int i = 0 ; i < 4 ; i++){
				
				HashMap chartDataMap = new HashMap();

				chartDataMap.put("label", "");
				chartDataMap.put("data", resultData[i]);
				
				chartData.add(chartDataMap);
			}
		}
		
		List<Object> result      = new ArrayList<Object>();
		
		result.add(chartData);
		
		return result;
		
	}
*/	
	
	@SuppressWarnings("unchecked")
	public List<Object> getMeterTimeSyncLogManualChart(Map<String, Object> condition){
		
		List<Object> chartData 		= new ArrayList<Object>();		
		StringBuffer sbQuery 		= new StringBuffer();
	
		sbQuery.append("  SELECT SUM( CASE WHEN syncLog.result = 0 THEN 1 ELSE 0 END) AS result1	\n")
			   .append("       , SUM( CASE WHEN syncLog.result = 1 THEN 1 ELSE 0 END) AS result2 	\n")
			   .append("       , SUM( CASE WHEN syncLog.result = 2 THEN 1 ELSE 0 END) AS result3	\n")
			   .append("       , SUM( CASE WHEN syncLog.result = 3 THEN 1 ELSE 0 END) AS result4	\n");

	    // Where 조건
        sbQuery.append(setMeterTimeSyncLogWhere(condition).toString());
        
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@SuppressWarnings("unchecked")
	public List<Object> getMeterTimeSyncLogGrid(Map<String, Object> condition){
		
		String curPage 			= StringUtil.nullToBlank(condition.get("curPage"));
		List<Object> result      = new ArrayList<Object>();
		List<Object> gridData 		= new ArrayList<Object>();		
		StringBuffer sbQuery 		= new StringBuffer();
	
		sbQuery.append("  SELECT syncLog.write_date       AS writeDate   	\n")
			   .append("	   , syncLog.operator_Type    AS method			\n")
			   .append("	   , mcu.sys_id               AS mcuSysID       \n")
			   .append("	   , me.mds_id                AS meterMdsId     \n")
			   .append("	   , syncLog.time_diff        AS timeDiff       \n")
			   .append("	   , syncLog.before_date      AS PreviousDate   \n")
			   .append("	   , syncLog.after_date       AS currentDate    \n")
			   .append("	   , syncLog.result           AS Status         \n")
			   .append("	   , syncLog.operator         AS operator       \n");

		// Where 조건
        sbQuery.append(setMeterTimeSyncLogWhere(condition).toString());
		
		// totalCount - 시작
		StringBuffer countQuery = new StringBuffer();
		countQuery.append("\n SELECT COUNT(writeDate) ");
		countQuery.append("\n FROM (  ");
		countQuery.append(sbQuery);
		countQuery.append("\n ) countTotal ");
		
		SQLQuery countQueryObj = getSession().createSQLQuery(countQuery.toString());
				                  
		Number totalCount = (Number)countQueryObj.uniqueResult();
		
		result.add(totalCount.toString());
		// totalCount - 종료
		
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		
		// Paging 설정
		int rowPerPage = Integer.parseInt((String)condition.get("pageSize"));
		int firstIdx  = Integer.parseInt(curPage) * rowPerPage;		
		query.setFirstResult(firstIdx);
		query.setMaxResults(rowPerPage);
		
		
		List dataList = query.list();
		
		int dataListLen = 0;
		if(dataList != null)
			dataListLen = dataList.size();
		int count=1;
		for(int i=0 ; i < dataListLen ; i++){
			
			HashMap chartDataMap = new HashMap();
			Object[] resultData = (Object[]) dataList.get(i);
			
			chartDataMap.put("no",           	Integer.toString(CommonUtils2.makeIdxPerPage(String.valueOf(curPage), String.valueOf(rowPerPage), count)));
			chartDataMap.put("writeDate",    	resultData[0]);
			
			if(resultData[1] != null)
				if(resultData[1].equals(0))
					chartDataMap.put("method",			"Auto");
				else if(resultData[1].equals(1))
					chartDataMap.put("method",			"Manual");
				else	
					chartDataMap.put("method",			"");
			
			chartDataMap.put("mcuSysID",    	resultData[2]);
			chartDataMap.put("meterMdsId",  	resultData[3]);
			chartDataMap.put("timeDiff",    	resultData[4]);
			chartDataMap.put("previousDate",	resultData[5]);
			chartDataMap.put("currentDate", 	resultData[6]);
			
			
			if(resultData[7] != null)
				if(resultData[7].equals(0))
					chartDataMap.put("status",			"Success");
				else if(resultData[7].equals(1))
					chartDataMap.put("status",			"Failed");
				else if(resultData[7].equals(2))
					chartDataMap.put("status",			"Invalid Argument");
				else if(resultData[7].equals(3))
					chartDataMap.put("status",			"Communication Failure");
				else
					chartDataMap.put("status",			"");
			
			chartDataMap.put("operator",    	resultData[8]);
					
			gridData.add(chartDataMap);
			count++;
		}
		
		result.add(gridData);
		
		return result;
		
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public List<Object> getMeterTimeThresholdGrid(Map<String, Object> condition){
		
		String mcuSysType 	= StringUtil.nullToBlank(condition.get("mcuSysType"));
		String mcuSysId 	= StringUtil.nullToBlank(condition.get("mcuSysId"));
		String mcuCommState = StringUtil.nullToBlank(condition.get("mcuCommState"));

		String locationId 	= StringUtil.nullToBlank(condition.get("locationId"));
		String time		 	= StringUtil.nullToBlank(condition.get("time"));
		String timeDiffType = StringUtil.nullToBlank(condition.get("timeDiffType"));
		String supplierId 	= StringUtil.nullToBlank(condition.get("supplierId"));
		String curPage 		= StringUtil.nullToBlank(condition.get("curPage"));
		
		String datePre24H 	= DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddhhmmss");
		String datePre48H 	= DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddhhmmss");

		List<Object> result      	= new ArrayList<Object>();
		List<Object> gridData 		= new ArrayList<Object>();		
		StringBuffer sbQuery 		= new StringBuffer();
	
		sbQuery.append("  SELECT mcu.SYS_ID AS sysID 				\n")
			   //.append("       , mcu.SYS_TYPE  						\n")
			   
			   .append("       , code.Name	 						\n")
			   .append("       , mcu.SYS_NAME  						\n")
			   .append("       , mcu.SYS_PHONE_NUMBER  				\n")
			   .append("       , mcu.IP_ADDR  						\n")
			   .append("       , mcuVar.varTimesyncThreshold  		\n")
			   .append("       , mcu.LAST_COMM_DATE  as lstCommDate	\n")
			   
			   .append("       , CASE WHEN 	   mcu.LAST_COMM_DATE <  :datePre24H THEN '0'	\n")
		       .append("              WHEN     mcu.LAST_COMM_DATE >= :datePre24H  			\n")
		       .append("                   AND mcu.LAST_COMM_DATE <  :datePre48H THEN '24'  \n")
		       .append("              WHEN     mcu.LAST_COMM_DATE >  :datePre48H THEN '48'	\n")
		       .append(" 		      ELSE '9'								     	\n")
	           .append("          END        as commStatus                     	    \n")
			   .append("       , mcu.ID 	 		 as mcuId	\n")
			   .append("   FROM MCU mcu  							\n")
			   .append("   LEFT OUTER JOIN MCUVAR mcuVar  			\n")
			   .append("     ON (    mcu.MCU_VAR_ID = mcuVar.ID  	\n")
			   .append("         AND mcuVar.varEnableAutoTimesync = 1)  \n")
			   .append("   LEFT OUTER JOIN CODE code	  			\n")
			   .append("     ON (    mcu.SYS_TYPE = code.ID	 ) 		\n")
			   .append("   WHERE mcu.supplier_id = " + supplierId + "	\n");		
		
		
		if(!mcuSysType.equals(""))
			sbQuery.append("    AND mcu.SYS_TYPE = " + mcuSysType );
		
		if(!mcuSysId.equals(""))
			sbQuery.append("    AND mcu.SYS_ID = '" + mcuSysId + "'");
		
		if(!mcuCommState.equals(""))
			if(mcuCommState.equals("0"))
				sbQuery.append("    AND mcu.LAST_COMM_DATE <  :datePre24H  \n");
			else if(mcuCommState.equals("24"))
				sbQuery.append("    AND mcu.LAST_COMM_DATE >= :datePre24H  \n")
					   .append("    AND mcu.LAST_COMM_DATE <  :datePre48H  \n");		       
			else if(mcuCommState.equals("48"))
				sbQuery.append("    AND mcu.LAST_COMM_DATE >  :datePre48H  \n");
		
		if(!locationId.equals(""))
			sbQuery.append("    AND mcu.LOCATION_ID = " + locationId);
		
		
		if(!time.equals("")){
			Integer secTimeDiff = 0;
			
			if(timeDiffType.equals("sec"))
				secTimeDiff = 1;
			else if(timeDiffType.equals("min"))
				secTimeDiff = 60;
			else if(timeDiffType.equals("hour"))
				secTimeDiff = 60*60;
			
			sbQuery.append("    AND mcuVar.varTimesyncThreshold > " + Integer.parseInt(time) * secTimeDiff);
		}
		
				
		
		// totalCount - 시작
		StringBuffer countQuery = new StringBuffer();
		countQuery.append("\n SELECT COUNT(*) ");
		countQuery.append("\n FROM (  ");
		countQuery.append(sbQuery);
		countQuery.append("\n ) countTotal ");
		
		SQLQuery countQueryObj = getSession().createSQLQuery(countQuery.toString());
		countQueryObj.setString("datePre24H", datePre24H);
		countQueryObj.setString("datePre48H", datePre48H);
				                  
		Number totalCount = (Number)countQueryObj.uniqueResult();
		
		result.add(totalCount.toString());
		// totalCount - 종료
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setString("datePre24H", datePre24H);
		query.setString("datePre48H", datePre48H);
		
		// Paging 설정
		int rowPerPage = Integer.parseInt((String)condition.get("pageSize"));
		int firstIdx  = Integer.parseInt(curPage) * rowPerPage;		
		query.setFirstResult(firstIdx);
		query.setMaxResults(rowPerPage);
		
		List dataList = query.list();
		
		int dataListLen = 0;
		if(dataList != null)
			dataListLen = dataList.size();
		
		int count=1;
		for(int i=0 ; i < dataListLen ; i++){
			
			HashMap chartDataMap = new HashMap();
			Object[] resultData = (Object[]) dataList.get(i);
			
			chartDataMap.put("check",       "0");
			chartDataMap.put("no",          Integer.toString(CommonUtils2.makeIdxPerPage(String.valueOf(curPage), String.valueOf(rowPerPage), count)));
			chartDataMap.put("sysId",    	resultData[0]);	
			chartDataMap.put("sysType",    	resultData[1]);
			chartDataMap.put("sysName",    	resultData[2]);
			chartDataMap.put("phone",	  	resultData[3]);
			chartDataMap.put("ipAddr",    	resultData[4]);
			chartDataMap.put("threshold",	resultData[5]);
			chartDataMap.put("lastComm", 	resultData[6]);
			chartDataMap.put("commState",   resultData[7]);
			chartDataMap.put("mcuId",   	resultData[8]);
			
					
			gridData.add(chartDataMap);
			count++;
		}
		
		
		result.add(gridData);
		
		return result;
		
	}	
	
	
	// timeDiff에서 사용하는 From / Where 조건 생성
	private StringBuffer setMeterTimeSyncLogWhere(Map<String, Object> condition){
		String mcuSysId 	= StringUtil.nullToBlank(condition.get("mcuSysId"));
		String meterMdsId 	= StringUtil.nullToBlank(condition.get("meterMdsId"));
		String operatorId 	= StringUtil.nullToBlank(condition.get("operatorId"));

		String method		= StringUtil.nullToBlank(condition.get("method"));
		String status		= StringUtil.nullToBlank(condition.get("status"));

		String timeDiff 	= StringUtil.nullToBlank(condition.get("timeDiff"));
		String time 		= StringUtil.nullToBlank(condition.get("time"));
		String timeType 	= StringUtil.nullToBlank(condition.get("timeType"));

		String searchStartDate 	= StringUtil.nullToBlank(condition.get("searchStartDate"));
		String searchEndDate 	= StringUtil.nullToBlank(condition.get("searchEndDate"));
		String supplierId 		= StringUtil.nullToBlank(condition.get("supplierId"));
		
		Integer sec1Hour        = 60*60;
		Integer sec12Hour       = 60*60*12;
		Integer sec24Hour       = 60*60*24;
		
		StringBuffer sbQuery 		= new StringBuffer();
			   
	    sbQuery.append("    FROM METERTIMESYNC_LOG syncLog				\n")
			   .append("    LEFT OUTER JOIN METER me  					\n")
			   .append("      ON (syncLog.METER_ID = me.ID)				\n")
			   .append("    LEFT OUTER JOIN MODEM modem					\n")
			   .append("      ON (me.MODEM_ID = modem.ID)				\n")
			   .append("    LEFT OUTER JOIN MCU mcu						\n")
			   .append("      ON (modem.MCU_ID = mcu.ID)  				\n")
			   .append("   WHERE syncLog.supplier_id = " + supplierId + "\n")
	    	   .append("   AND me.meter_status <> (select id from code where code= '" + MeterCodes.DELETE_STATUS.getCode()  + "') \n");
		
		
	    if(!mcuSysId.equals(""))
			sbQuery.append("    AND mcu.SYS_ID = '" + mcuSysId + "'");
		
		if(!meterMdsId.equals(""))
			sbQuery.append("    AND me.MDS_ID LIKE '%" + meterMdsId + "%'");

		if(!operatorId.equals(""))
			sbQuery.append("    AND syncLog.OPERATOR LIKE '%" + operatorId + "%'");

				

		if(method.equals("Auto"))
			sbQuery.append("    AND syncLog.OPERATOR_TYPE = 0 ");
		else if(method.equals("Manual"))
			sbQuery.append("    AND syncLog.OPERATOR_TYPE = 1 ");		
		
		if(!status.equals(""))
			if(status.equals("Success"))
				sbQuery.append("    AND syncLog.RESULT = 0 ");
			else if(status.equals("Fail"))	// 성공하지 못했다면, 실패
				sbQuery.append("    AND syncLog.RESULT in(1,2,3) ");
		
		
		if(timeDiff.equals("1")) {	// 일치
			sbQuery.append("    AND (syncLog.TIME_DIFF < "  + sec1Hour  );
			sbQuery.append("    AND syncLog.TIME_DIFF > "  + -sec1Hour +")" );
		}else if(timeDiff.equals("2")){	// 1 ~ 12시간
			sbQuery.append("    AND   ((syncLog.TIME_DIFF >= "  + sec1Hour  );
			sbQuery.append("    AND   syncLog.TIME_DIFF < " + sec12Hour+")" );
			
			sbQuery.append("    OR ( syncLog.TIME_DIFF <= "  + -sec1Hour  );
			sbQuery.append("    AND   syncLog.TIME_DIFF > " + -sec12Hour +"))" );
		}else if(timeDiff.equals("3")){	// 12 ~ 24시간
			sbQuery.append("    AND ((syncLog.TIME_DIFF >= "  + sec12Hour );
			sbQuery.append("    AND syncLog.TIME_DIFF < " + sec24Hour +")" );
			
			sbQuery.append("    OR (syncLog.TIME_DIFF <= "  + -sec12Hour );
			sbQuery.append("    AND syncLog.TIME_DIFF > " + -sec24Hour +"))" );
		}else if(timeDiff.equals("4")){	// 24시간이상
			sbQuery.append("    AND (syncLog.TIME_DIFF > " + sec24Hour);
			sbQuery.append("    OR syncLog.TIME_DIFF < " + -sec24Hour + ")");
		}else if(timeDiff.equals("5")){	// 확인불가(Unknown)
			sbQuery.append("    AND syncLog.TIME_DIFF is null ");
		}else if(timeDiff.equals("6")){   // 사용자정의
		    sbQuery.append("   AND syncLog.TIME_DIFF > " + Integer.parseInt(time));
            sbQuery.append("    AND syncLog.TIME_DIFF is not null ");
        }
		/*else if(timeDiff.equals("5")){	// 사용자정의
			
			Integer secTimeDiff = 0;
			
			if(timeType.equals("sec"))
				secTimeDiff = 1;
			else if(timeType.equals("min"))
				secTimeDiff = 60;
			else if(timeType.equals("hour"))
				secTimeDiff = 60*60;
			
			if(time.equals(""))
				time = "0";
			
			sbQuery.append("    AND syncLog.TIME_DIFF > " + Integer.parseInt(time) * secTimeDiff);
		}*/
		
		
		if(!searchStartDate.equals("")){
			sbQuery.append("    AND syncLog.write_date >= '" + searchStartDate +"000000'");
		}
		
		if(!searchEndDate.equals("")){
			sbQuery.append("    AND syncLog.write_date <= '" + searchEndDate +"595959'");
		}
		
		
		return sbQuery;
	}

}


