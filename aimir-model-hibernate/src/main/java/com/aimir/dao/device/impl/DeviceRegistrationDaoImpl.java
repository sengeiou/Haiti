package com.aimir.dao.device.impl;

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

import com.aimir.constants.CommonConstants.CustomerSearchType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.RegType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.DeviceRegistrationDao;
import com.aimir.model.device.DeviceRegLog;
import com.aimir.model.system.DeviceVendor;
import com.aimir.util.StringUtil;

@Repository(value = "deviceregistrationDao")
public class DeviceRegistrationDaoImpl extends AbstractHibernateGenericDao<DeviceRegLog, Integer> implements DeviceRegistrationDao {

    Log logger = LogFactory.getLog(DeviceRegistrationDaoImpl.class);
    
	@Autowired
	protected DeviceRegistrationDaoImpl(SessionFactory sessionFactory) {
		super(DeviceRegLog.class);
		super.setSessionFactory(sessionFactory);
	}

	// MCU 등록정보 조회
	@SuppressWarnings("unchecked")
	public List<Object> getMiniChart(Map<String, Object> condition){
    	
		List<Object> chartData 	 = new ArrayList<Object>();
    	List<Object> result      = new ArrayList<Object>();
    	
    	StringBuffer sbQuery 	 = new StringBuffer();
    	
    	String inCondition		 = StringUtil.nullToBlank(condition.get("inCondition"));
    	String supplierId		 = StringUtil.nullToBlank(condition.get("supplierId"));
    	
		// chartData
		sbQuery = new StringBuffer();
		
		sbQuery.append(" SELECT label 	                		\n")		
			   .append("      , value 	                		\n")
			   .append("      , maxDate                         \n")
		       .append("   FROM ( 								\n");
		       
		int i = 0;
		
		for(RegType _regType:RegType.values()){
			
			if( i > 0)
			sbQuery.append("      UNION ALL		        	                \n");
			
			sbQuery.append("     SELECT '"+ i +"'      AS label 		    \n")
			       .append("          , COUNT(*) AS value			        \n")
			       .append("          , MAX(create_date) AS maxDate	        \n")
			       .append("       FROM DEVICEREG_LOG 				        \n")
			       .append("      WHERE deviceType in ("+ inCondition + ")  \n")			   
			       .append("        AND reg_type = '"+ _regType +"'         \n")
		           .append("        AND SUPPLIER_ID = "+ supplierId +"    \n");
			       
			i++;
		}	   
			   
		sbQuery.append("  ) tt 							        \n")		
			   .append("  ORDER BY label				        \n");
		
		List dataList = null;	
		
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());		
		query = getSession().createSQLQuery(sbQuery.toString());
		
		dataList = query.list();
		
		
		
		for(i=0 ; i < 4 ; i++){	
			Object[] resultData = (Object[]) dataList.get(i);		
			HashMap chartDataMap = new HashMap();
			
			chartDataMap.put("label", 	resultData[0].toString());
			chartDataMap.put("data",  	resultData[1].toString());
			
			if(resultData[2] == null)
				chartDataMap.put("maxDate", "");	
			else
				chartDataMap.put("maxDate", resultData[2].toString());
			
			chartData.add(chartDataMap);
			
		}
    	result.add(chartData);
    	
    	return result;
	}	
	
	
	
	@SuppressWarnings("unchecked")
	public List<DeviceVendor> getVendorListBySubDeviceType(Map<String, Object> condition){	
		

    	List<Object> result      = new ArrayList<Object>();
    	
    	StringBuffer sbQuery 	 = new StringBuffer();
    	
    	String subDeviceType	 = StringUtil.nullToBlank(condition.get("subDeviceType"));
    	
		// chartData
		sbQuery = new StringBuffer();
		
		sbQuery.append(" SELECT deviceVendor.ID    AS ID                        \n")		
		       .append("      , deviceVendor.NAME  AS NAME                      \n")
		       .append("   FROM DEVICEVENDOR deviceVendor                    \n")
		       .append("   JOIN (SELECT DEVICEVENDOR_ID 						\n")
		       .append("           FROM DEVICEMODEL      						\n")
			   .append("          WHERE DEVICEVENDOR_ID is not null 			\n");
		       if(!"".equals(subDeviceType)) {
		    	   sbQuery.append("          AND TYPE_ID in ( "+ subDeviceType + ") 		\n");		    	   
		       }
		       
		       sbQuery.append("          GROUP BY DEVICEVENDOR_ID 						\n")
		       .append("        ) deviceModel                                   \n")
		       .append("     ON (deviceModel.DEVICEVENDOR_ID = deviceVendor.ID) \n")
		       .append("  GROUP BY deviceVendor.ID                              \n")
		       .append("         , deviceVendor.NAME                            \n")
		       .append("  ORDER BY deviceVendor.NAME  							\n");

		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query = getSession().createSQLQuery(sbQuery.toString());
		
		List dataList = query.list();
    	
    	List<DeviceVendor> vendorList = new ArrayList<DeviceVendor>();
    	
    	int vendorListLen = 0;
    	if(dataList != null)
    		vendorListLen  = dataList.size();
    	
    	for(int i = 0 ; i < vendorListLen ; i++){
    		
    		Object[] resultData = (Object[]) dataList.get(i);
    		
    		DeviceVendor tmpDeviceVendor = new DeviceVendor();
    		
    		tmpDeviceVendor.setId(Integer.parseInt(resultData[0].toString()));
    		tmpDeviceVendor.setName(resultData[1].toString());
    		
    		vendorList.add(tmpDeviceVendor);
    	}
    	
    	
    	return vendorList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getDeviceRegLog(Map<String, Object> condition){	
		
    	List<Object> result      = new ArrayList<Object>();
    	List<Object> gridData 	 = new ArrayList<Object>();
    	
    	StringBuffer sbQuery 	 = new StringBuffer();
    	
    	String deviceType    = StringUtil.nullToBlank(condition.get("deviceType"));
    	String subDeviceType = StringUtil.nullToBlank(condition.get("subDeviceType"));
    	String vendor        = StringUtil.nullToBlank(condition.get("vendor"));
    	String model         = StringUtil.nullToBlank(condition.get("model"));    	
    	String deviceID      = StringUtil.nullToBlank(condition.get("deviceID"));
    	String regType       = StringUtil.nullToBlank(condition.get("regType"));
    	String regResult     = StringUtil.nullToBlank(condition.get("regResult"));
    	String message       = StringUtil.nullToBlank(condition.get("message"));
    	String searchStartDate = StringUtil.nullToBlank(condition.get("searchStartDate"));
    	String searchEndDate = StringUtil.nullToBlank(condition.get("searchEndDate"));
    	String supplierId    = StringUtil.nullToBlank(condition.get("supplierId"));
    	
    	sbQuery.append(" SELECT regLog.DEVICETYPE 		AS type 		\n")
			   .append("      , regLog.DEVICETYPE 		AS deviceType 	\n")
			   .append("      , regLog.DEVICE_NAME 		AS deviceName 	\n")
			   .append("      , vendor.NAME            	AS vendorName 	\n")
			   .append("      , model.NAME             	AS modelName 	\n")
			   .append("      , regLog.REG_TYPE        	AS regType 		\n")
			   .append("      , regLog.RESULT          	AS result 		\n")
			   .append("      , regLog.MESSAGE         	AS message 		\n")
			   .append("      , regLog.CREATE_DATE         	AS installdate 		\n")
			   .append("   FROM DEVICEREG_LOG regLog 				\n")
			   .append("   LEFT OUTER JOIN DEVICEMODEL model 		\n")
		       .append("     ON regLog.DEVICEMODEL_ID = model.ID 		\n")
		       .append("   LEFT OUTER JOIN DEVICEVENDOR vendor 			\n")
		       .append("     ON model.DEVICEVENDOR_ID = vendor.ID 		\n")
		       .append("  WHERE 1=1								 		\n")
		       .append(" AND CREATE_DATE BETWEEN '"+searchStartDate+"000000' AND '"+searchEndDate+"235959'	\n");

		if(deviceType != null && deviceType.length() > 0){
		
			StringBuffer inCondition = new StringBuffer("''");
			
			// MCU
			if(TargetClass.DCU.toString().equals(deviceType)){
				inCondition.append(", '"+ TargetClass.DCU + "'"); 	
			}
			
			// MODEM
			if(deviceType.equals("Modem"))
				for(ModemType _modemType : ModemType.values())
						inCondition.append(", '"+ _modemType + "'");
						
						
			// METER
			if(deviceType.equals("Meter"))
				for(MeterType _meterType : MeterType.values())
					inCondition.append(", '"+ _meterType + "'");
				
			// Customer & Contract
				if(deviceType.equals("Customer")) {
					for(CustomerSearchType _customerSearchType : CustomerSearchType.values())
						inCondition.append(", '"+ _customerSearchType + "'");
				}

			
			sbQuery.append("     AND regLog.DEVICETYPE in ("+ inCondition.toString() +")");
		}
			
		if(subDeviceType != null && subDeviceType.length() > 0)
			sbQuery.append("     AND regLog.DEVICETYPE = '"+ subDeviceType +"'");
		
		if(vendor != null && vendor.length() > 0)
			sbQuery.append("     AND vendor.ID = "+ vendor);
		        
		if(model != null && model.length() > 0)
			sbQuery.append("     AND model.ID  = "+ model);		          
		
		if(deviceID != null && deviceID.length() > 0)
			sbQuery.append("     AND regLog.DEVICE_NAME  = '"+ deviceID +"'");
		
		if(regType != null && regType.length() > 0)
			sbQuery.append("     AND regLog.REG_TYPE = '"+ regType  +"'");
		
		if(regResult != null && regResult.length() > 0)
			if(regResult.equals("SUCCESS"))
				sbQuery.append("     AND regLog.RESULT = '"+ regResult  +"'");
			else
				sbQuery.append("     AND regLog.RESULT != 'SUCCESS' ");
		
		if(message != null && message.length() > 0)
			sbQuery.append("     AND regLog.MESSAGE LIKE '%"+ message +"%'");
		
		if(supplierId != null && supplierId.length() > 0)
			sbQuery.append("     AND regLog.SUPPLIER_ID  = "+ supplierId);	
		
		// No.을 위한 전체 건수[start]
				StringBuffer countQuery = new StringBuffer();
				countQuery.append("\n SELECT COUNT(countTotal.deviceName) ");
				countQuery.append("\n FROM (  ");
				countQuery.append(sbQuery);
				countQuery.append("\n ) countTotal ");
				
				SQLQuery countQueryObj = getSession().createSQLQuery(countQuery.toString());
				                  
				Number totalCount = (Number)countQueryObj.uniqueResult();
				
				result.add(totalCount.toString());
		// No.을 위한 전체 건수[end]
				
		sbQuery.append("     ORDER BY regLog.ID DESC");
		
		StringBuffer sbQueryData = new StringBuffer();
		sbQueryData.append(sbQuery);
		
		//logger.debug("query="+sbQueryData.toString());
		
		SQLQuery query = getSession().createSQLQuery(sbQueryData.toString());
		
		// Paging
//		int rowPerPage = CommonConstants.Paging.ROWPERPAGE_20.getPageNum();
//		int firstIdx   = Integer.parseInt(curPage) * rowPerPage;
		
//		query.setFirstResult(firstIdx);
//		query.setMaxResults(rowPerPage);
		
		List dateList = null;
		dateList = query.list();
		
		// 실제 데이터
		int dataListLen = 0;
		if(dateList != null)
			dataListLen= dateList.size();
		int count=1;
		for(int i=0 ; i < dataListLen ; i++){
			
			HashMap chartDataMap = new HashMap();
			Object[] resultData = (Object[]) dateList.get(i);
			
//			chartDataMap.put("no"			,Integer.toString(CommonUtils2.makeIdxPerPage(String.valueOf(curPage), String.valueOf(rowPerPage), count)));
			chartDataMap.put("no"			,i+1);
			
			String type = resultData[0].toString();
			
			//TODO 기존의 소스는 MCU의Type (indoor, outdoor, dcu) 까지 구분 추후 구현 요망.
			/*for(UsingMCUType _mcuType : UsingMCUType.values())
				if(_mcuType.toString().equals(type))
					chartDataMap.put("type" 	, "MCU");
			*/
			// MCU
			if(TargetClass.DCU.toString().equals(type)){
					chartDataMap.put("type" 	, "DCU");
				}
				
			// MODEM
			for(ModemType _modemType : ModemType.values())
				if(_modemType.toString().equals(type))
					chartDataMap.put("type" 	, "Modem");
						
						
			// METER
			for(MeterType _meterType : MeterType.values())
				if(_meterType.toString().equals(type))
					chartDataMap.put("type" 	, "Meter");
			
			// Customer
			for(CustomerSearchType _customerSearchType : CustomerSearchType.values())
				if(_customerSearchType.toString().equals(type))
					chartDataMap.put("type" 	, "Customer");
			
			chartDataMap.put("deviceType" 	, resultData[1]);
			chartDataMap.put("deviceName" 	, resultData[2]);
			chartDataMap.put("vendorName" 	, resultData[3]);
			chartDataMap.put("modelName" 	, resultData[4]);
			chartDataMap.put("regType" 		, resultData[5]);
			chartDataMap.put("result" 		, resultData[6]);
			chartDataMap.put("message" 		, resultData[7]);
//			String tempDateFormat = TimeUtil.getYYYYMMDD( resultData[8].toString());
	
			chartDataMap.put("installdate" 	, resultData[8]);
			
			gridData.add(chartDataMap);
			count++;
		}
	
		result.add(gridData);
		return result;
	}

	@Override
	public List<Object> getShipmentImportHistory(Map<String, Object> condition, boolean isTotal) {
		List<Object> result      = new ArrayList<Object>();
		List<Object> gridData 	 = new ArrayList<Object>();

		Integer supplierId = (Integer) condition.get("supplierId");
		String deviceType = StringUtil.nullToBlank(condition.get("targetType"));
		String detailType = StringUtil.nullToBlank(condition.get("detailType"));
		String fileName = StringUtil.nullToBlank(condition.get("fileName"));
		String startDate = StringUtil.nullToBlank(condition.get("startDate"));
		String endDate = StringUtil.nullToBlank(condition.get("endDate"));
		String regType = StringUtil.nullToBlank(condition.get("regType"));

		StringBuffer sqlBuf = new StringBuffer();

		if (isTotal) {
			sqlBuf.append("\nSELECT COUNT(*) AS cnt FROM ( ");
		}
		
		sqlBuf.append("SELECT        			                    														 \n");
		sqlBuf.append("				devicereg_log.DEVICETYPE,          	      												 \n");
		sqlBuf.append("				devicereg_log.SHIPMENT_FILE_NAME,    	      											 \n");
		sqlBuf.append("				devicereg_log.TOTAL_COUNT,    	  	    												 \n");
		sqlBuf.append("				devicereg_log.SUCCESS_COUNT,    	  	    											 \n");
		sqlBuf.append("				devicereg_log.FAIL_COUNT,	    	  	    											 \n");
		sqlBuf.append("				devicereg_log.CREATE_DATE		   	  	    											 \n");
		sqlBuf.append("FROM			DEVICEREG_LOG devicereg_log																 \n"); 
		sqlBuf.append("WHERE		devicereg_log.SUPPLIER_ID = :supplierId													 \n");
		sqlBuf.append("AND			devicereg_log.REG_TYPE = :regType								 						 \n");
		sqlBuf.append("AND			devicereg_log.DEVICETYPE = :detailType													 \n");
		sqlBuf.append("AND   	 	devicereg_log.CREATE_DATE BETWEEN '" + startDate + "000000' AND '" + endDate + "235959'	 \n");
		
		if (!fileName.equals("")) {
			sqlBuf.append("AND devicereg_log.SHIPMENT_FILE_NAME LIKE :fileName											 	 \n");
		}
		
		if(!isTotal) {
			sqlBuf.append("ORDER BY devicereg_log.CREATE_DATE	 															 \n");
		}
		
		if (isTotal) {
			sqlBuf.append("\n) totalCount");
		}
		
		SQLQuery query = getSession().createSQLQuery(sqlBuf.toString());
		query = getSession().createSQLQuery(sqlBuf.toString());
		query.setInteger("supplierId", supplierId);
		query.setString("regType", regType);
		query.setString("detailType", detailType);
		 
		if (!fileName.equals("")) {
			query.setString("fileName", "%" + fileName + "%");
		}

		if (isTotal) {
			Number totalCount = (Number) query.uniqueResult();
			result.add(totalCount.toString());

		} else {
			List dateList = null;
			dateList = query.list();

			int dataListLen = 0;
			if (dateList != null)
				dataListLen = dateList.size();
			int count = 1;
			for (int i = 0; i < dataListLen; i++) {
				
				HashMap map = new HashMap();
				Object[] resultData = (Object[]) dateList.get(i);
				
				map.put("num"			, i + 1);
				map.put("deviceType" 	, resultData[0]);
				map.put("fileName" 		, resultData[1]);
				map.put("totalCount" 	, resultData[2]);
				map.put("successCount" 	, resultData[3]);
				map.put("failCount" 	, resultData[4]);
				map.put("importDate" 	, resultData[5]);
				
				gridData.add(map);
				count++;
			}

			result.add(gridData);
		}

		return result;
	}
	
}

