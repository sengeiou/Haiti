package com.aimir.service.mvm.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffGMDao;
import com.aimir.dao.system.TariffWMCaliberDao;
import com.aimir.dao.system.TariffWMDao;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.CustomerUsageManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@Deprecated
@Service(value="customerUsageManager")
public class CustomerUsageManagerImpl implements CustomerUsageManager {
	
    private static Log logger = LogFactory.getLog(CustomerUsageManagerImpl.class);
    
	@Autowired
	MeteringDayDao meteringDayDao;
	@Autowired
	TariffEMDao tariffEMDao;
	@Autowired
	TariffGMDao tariffGMDao;
	@Autowired
	TariffWMDao tariffWMDao;
	@Autowired
	TariffWMCaliberDao tariffWMCaliberDao;
	@Autowired
	ContractDao contractDao;
	@Autowired
	CustomerDao customerDao;
	@Autowired
	OperatorDao operatorDao;
	@Autowired
	SupplierDao supplierDao;
	
//	시간별,일별,월별,연도별 사용량을 조회한다.
	@SuppressWarnings("unchecked")
	public List<Object> getCustomerUsageMiniChart(Map<String, Object> params){
		
		
		String sViewType 	= StringUtil.nullToBlank(params.get("sViewType"));		// 일/월/년 -> CommonConstants.DateType
		String iStand 		= StringUtil.nullToBlank(params.get("iStand"));		// 월별시 이전/다음시 기준월 + iStand
		String iMdev_type	= StringUtil.nullToBlank(params.get("iMdev_type"));	// 
		String METER_TYPE	= StringUtil.nullToBlank(params.get("METER_TYPE"));	// 미터유형(전기,가스,수도)
		
		String sStart	= "";
		String sEnd		= "";
		String sLastYear= "";
		
		if("".equals(iMdev_type)){
			iMdev_type	= CommonConstants.DeviceType.Meter.getCode().toString();
			params.put("iMdev_type", iMdev_type);
		}
		
		if("".equals(sViewType)){
			sViewType = "daily";
			params.put("sViewType", sViewType);
		}
		
		/*
		 * 검색조건 시작일, 종료일 세팅 start
		 */ 
		
		/*일별 - 금일 기준 일주일 자료*/
		if(CommonConstants.DateType.valueOf("DAILY").getCode().equals(sViewType)){
			sStart	= CalendarUtil.getDateWithoutFormat(CalendarUtil.getCurrentDate(), Calendar.DAY_OF_MONTH, -6);
			params.put("sStart", sStart);
		
			sEnd	= CalendarUtil.getCurrentDate();
			params.put("sEnd", sEnd);
			
		/*월별 - 기준월, 기준월 전달, 전년도 기준월*/ 
		}else if(CommonConstants.DateType.valueOf("MONTHLY").getCode().equals(sViewType)){
			if("".equals(iStand)){
				sStart	= CalendarUtil.getDateWithoutFormat(CalendarUtil.getCurrentDate(), Calendar.MONTH, -1).substring(0, 6);
				params.put("sStart", sStart);
				
				sEnd	= CalendarUtil.getCurrentDate().substring(0, 6);
				params.put("sEnd", sEnd);
			}else{
				sStart	= CalendarUtil.getDateWithoutFormat(CalendarUtil.getCurrentDate(), Calendar.MONTH, (-1 + Integer.parseInt(iStand))).substring(0, 6);
				params.put("sStart", sStart);
				
				sEnd	= CalendarUtil.getDateWithoutFormat(CalendarUtil.getCurrentDate(), Calendar.MONTH, (Integer.parseInt(iStand))).substring(0, 6);
				params.put("sEnd", sEnd);
			}
			
			sLastYear	= CalendarUtil.getDateWithoutFormat(sEnd + "01", Calendar.YEAR, -1).substring(0, 6);
			
			params.put("sLastYear", sLastYear);
			
		/*연도별 - 현재년도*/
		}else if(CommonConstants.DateType.valueOf("YEARLY").getCode().equals(sViewType)){
			sStart	= CalendarUtil.getCurrentDate().substring(0, 4) + "01";	// 올 1월 부터
//			sStart	= CalendarUtil.getDateWithoutFormat(CalendarUtil.getCurrentDate(), Calendar.YEAR, -1).substring(0, 6); // 작년 현재 월 부터
			params.put("sStart", sStart);
		
			sEnd	= CalendarUtil.getCurrentDate().substring(0, 4) + "12";	// 올 12월까지
//			sEnd	= CalendarUtil.getCurrentDate().substring(0, 6); 			// 현재 월까지
			params.put("sEnd", sEnd);
			
		/*시간별 - 전일 00시 ~ 24시*/
		}else if(CommonConstants.DateType.valueOf("HOURLY").getCode().equals(sViewType)){
			sStart	= CalendarUtil.getDateWithoutFormat(CalendarUtil.getCurrentDate(), Calendar.DATE, -1);
			params.put("sStart", sStart);
		
			sEnd	= CalendarUtil.getDateWithoutFormat(CalendarUtil.getCurrentDate(), Calendar.DATE, -1);
			params.put("sEnd", sEnd);
		}
		
		/*
		 * 검색조건 시작일, 종료일 세팅 end
		 */
		
		List<Object> result	= new ArrayList<Object>();
		List<Object> list 	= null; 
		
		/*고객 ID로 계약 리스트를 가져옴.*/
		//List<Contract> contractList = contractDao.getContractByCustomerId(Integer.parseInt(params.get("sUserId").toString()));
		
		/*고객 ID로 계약 리스트를 가져옴.*/
		Operator operator = operatorDao.get(Integer.parseInt(params.get("sUserId").toString()));
		List<Contract> contractList = new ArrayList<Contract>();
		Customer customer = customerDao.getCustomersByLoginId(operator.getLoginId());
		//logger.debug("CUSTOMER="+customer.getName());
		
		if(customer != null){
		
			Set<Contract> cont = customer.getContracts();
			
			if(cont.toArray().length != 0){
				Object[] array = cont.toArray();
				
				for(int i = 0; array != null && i < array.length; i++) {
					contractList.add((Contract)array[i]);
				}

			}
		}
		
		/*일별*/
		if(CommonConstants.DateType.valueOf("DAILY").getCode().equals(sViewType)){
			list = meteringDayDao.getCustomerUsageEmDaily(METER_TYPE, params);
			
			/*사용요금을 가져와 세팅함.*/
			setPrice(list, contractList, METER_TYPE);
			
			result.add(list);
		/*월별*/
		}else if(CommonConstants.DateType.valueOf("MONTHLY").getCode().equals(sViewType)){
			list = meteringDayDao.getCustomerUsageEmMonthly(METER_TYPE, params);
			
			/*사용요금을 가져와 세팅함.*/
			setPrice(list, contractList, METER_TYPE);
			
			/* start 전월, 현월, 전년동월 의 데이터가 하나라도 없으면 0으로 세팅하여 보여줌.(데이터가 없어도 무조건 보이게 처리)*/
			boolean s = false;
			boolean e = false;
			boolean l = false;
			boolean empty = false;
			
			if(list.size() != 3){
				for(Object map : list){
					if(!((HashMap)map).get("yyyymmdd").equals(sStart)){
						s = true;
						break;
					}
				}
				
				for(Object map : list){
					if(!((HashMap)map).get("yyyymmdd").equals(sEnd)){
						e = true;
						break;
					}
				}
				
				for(Object map : list){
					if(!((HashMap)map).get("yyyymmdd").equals(sLastYear)){
						l = true;
						break;
					}
				}
				
				if(list.size() == 0) empty = true; 
				
				if(e || empty){
					Map<String, Object> addMap = new HashMap<String, Object>();
					((HashMap)addMap).put("yyyymmdd", sEnd);
					((HashMap)addMap).put("usage", 0);
					((HashMap)addMap).put("price", "0");
					
					list.add(addMap);
				}
				
				if(s || empty){
					Map<String, Object> addMap = new HashMap<String, Object>();
					((HashMap)addMap).put("yyyymmdd", sStart);
					((HashMap)addMap).put("usage", 0);
					((HashMap)addMap).put("price", "0");
					
					list.add(addMap);
				}
				
				if(l || empty){
					Map<String, Object> addMap = new HashMap<String, Object>();
					((HashMap)addMap).put("yyyymmdd", sLastYear);
					((HashMap)addMap).put("usage", 0);
					((HashMap)addMap).put("price", "0");
					
					list.add(addMap);
				}
			}
			/* end 전월, 현월, 전년동월 의 데이터가 하나라도 없으면 0으로 세팅하여 보여줌.(데이터가 없어도 무조건 보이게 처리)*/
			
			result.add(list);	
		/*연도별*/
		}else if(CommonConstants.DateType.valueOf("YEARLY").getCode().equals(sViewType)){
			list = meteringDayDao.getCustomerUsageEmYearly(METER_TYPE, params);
			
			/*사용요금을 가져와 세팅함.*/
			setPrice(list, contractList, METER_TYPE);
			
			result.add(list);
		/*시간별*/
		}else if(CommonConstants.DateType.valueOf("HOURLY").getCode().equals(sViewType)){
			
			List<Object> tmpList      = new ArrayList<Object>();
			
			tmpList	= meteringDayDao.getCustomerUsageEmHourly(METER_TYPE, params);

			/*
			 * 그리드 및 챠트에 보여지기 위한 데이터 변환 start.
			 */
			int listSize	= tmpList.size();
			
			List<Object> tmpMap		= null;
			Map<String, String> tmpResult	= null;
			List<Object> tmpResult2	=  new ArrayList<Object>();
			
			for(int i=0; i<listSize; i++){
				tmpMap	= (List<Object>)tmpList.get(i);
				
				for(int j=0; j<tmpMap.size(); j++){
					
					tmpResult	= new HashMap<String, String>();				
					
					tmpResult.put("yyyymmdd", String.format("%02d", j));// + "~" + String.format("%02d", j+1) + "(h)");
			    	tmpResult.put("usage", tmpMap.get(j).toString());
			    	tmpResult.put("price", "-");
			    	
			    	tmpResult2.add(tmpResult);
				}
			}
			
			/*
			 * 그리드 및 챠트에 보여지기 위한 데이터 변환 end.
			 */
			
			result.add(tmpResult2);
			
		}else
		{
			logger.debug("#################    viewType is not setting !!!!  #########################"  );
		}
		
		/*JSP 문구 (화면에 일/월/년을) 표시하기 위해*/
		Map<String, String> tmpMap	= new HashMap<String, String>();
		tmpMap.put("sEnd", sEnd);
		result.add(tmpMap);
		
		logger.debug("@@@@@@@ = > " + result);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getCustomerUsageMiniChartbySearchDate(Map<String, Object> params){
		
		String iMdev_type	= StringUtil.nullToBlank(params.get("iMdev_type"));	// 
		String METER_TYPE	= StringUtil.nullToBlank(params.get("METER_TYPE"));	// 미터유형(전기,가스,수도)
		
		String searchType = StringUtil.nullToBlank(params.get("searchType"));
		String sStart	= StringUtil.nullToBlank(params.get("startDate"));
		String sEnd		= StringUtil.nullToBlank(params.get("endDate"));
		String supplierId = StringUtil.nullToBlank(params.get("supplierId"));
		String sLastYear= "";
		
		if("".equals(iMdev_type)){
			iMdev_type	= CommonConstants.DeviceType.Meter.getCode().toString();
			params.put("iMdev_type", iMdev_type);
		}
		
		if(CommonConstants.DateType.valueOf("MONTHLYPERIOD").getCode().equals(searchType) ||
				CommonConstants.DateType.valueOf("YEARLY").getCode().equals(searchType)) {
			params.put("sStart", sStart.substring(0, 6));
			params.put("sEnd", sEnd.substring(0, 6));
			
			sLastYear	= CalendarUtil.getDateWithoutFormat(sEnd + "01", Calendar.YEAR, -1).substring(0, 6);			
			params.put("sLastYear", sLastYear);
		} else {		
			params.put("sStart", sStart);
			params.put("sEnd", sEnd);
		}
		
		/*
		 * 검색조건 시작일, 종료일 세팅 end
		 */
		
		List<Object> result	= new ArrayList<Object>();
		List<Object> list 	= null; 
		
		/*고객 ID로 계약 리스트를 가져옴.*/
		//List<Contract> contractList = contractDao.getContractByCustomerId(Integer.parseInt(params.get("sUserId").toString()));
		
		/*고객 ID로 계약 리스트를 가져옴.*/
		Customer customer = customerDao.get(Integer.parseInt(params.get("sUserId").toString()));
		List<Contract> contractList = new ArrayList<Contract>();
		//logger.debug("CUSTOMER="+customer.getName());
		
		if(customer != null){
		
			Set<Contract> cont = customer.getContracts();
			
			if(cont.toArray().length != 0){
				Object[] array = cont.toArray();
				
				for(int i = 0; array != null && i < array.length; i++) {
					contractList.add((Contract)array[i]);
				}

			}
		}
		
		/*주기별*/
		if(CommonConstants.DateType.valueOf("PERIOD").getCode().equals(searchType)){
			list = meteringDayDao.getCustomerUsageEmDaily(METER_TYPE, params);
			
			/*사용요금을 가져와 세팅함.*/
//			setPrice(list, contractList, METER_TYPE);
			Calendar ti = Calendar.getInstance();
			ti.set(Calendar.YEAR, Integer.parseInt(sStart.substring(0, 4)));
			ti.set(Calendar.MONTH, Integer.parseInt(sStart.substring(4, 6)) - 1);
			ti.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sStart.substring(6)));

			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

			List<Object> resultList = new ArrayList<Object>();
			int cIndex = 0;
			while(true) {
//				logger.debug(df.format(ti.getTime()));
				String cTime = df.format(ti.getTime());
				if(list != null && list.size() > cIndex) {
					HashMap data = (HashMap) list.get(cIndex);
					String yyyymmdd = (String) data.get("yyyymmdd");
//					logger.debug("=" + yyyymmdd);
					if(cTime.equals(yyyymmdd)) {
						resultList.add(data);
						cIndex++;
					} else {
						HashMap<String, String> tempData = new HashMap<String, String>();
						tempData.put("yyyymmdd", cTime);
						tempData.put("usage", "0");
						tempData.put("price", "0");
						resultList.add(tempData);
					}
				} else {
					HashMap<String, String> tempData = new HashMap<String, String>();
					tempData.put("yyyymmdd", cTime);
					tempData.put("usage", "0");
					tempData.put("price", "0");
					resultList.add(tempData);
				}
			
				if(sEnd.equals(df.format(ti.getTime())))
					break;
				
				ti.add(Calendar.DAY_OF_YEAR, 1);
			}
			
		 Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		 DecimalFormat dfMd = DecimalUtil.getDecimalFormat(supplier.getMd());
			for(Object obj: resultList) {
				HashMap data = (HashMap) obj;
				String usage = StringUtil.nullToZero(data.get("usage"));
				data.put("yyyymmdd", TimeLocaleUtil.getLocaleDate(String.valueOf(data.get("yyyymmdd")) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
				data.put("usage", dfMd.format(Double.parseDouble(usage)).replaceAll(",", ""));
			}
			
			result.add(resultList);
		/*월별(주기)*/
		}else if(CommonConstants.DateType.valueOf("MONTHLYPERIOD").getCode().equals(searchType)){
			list = meteringDayDao.getCustomerUsageEmMonthly(METER_TYPE, params);
			
			/*사용요금을 가져와 세팅함.*/
//			setPrice(list, contractList, METER_TYPE);
			
			/* start 전월, 현월, 전년동월 의 데이터가 하나라도 없으면 0으로 세팅하여 보여줌.(데이터가 없어도 무조건 보이게 처리)*/
/*
			boolean s = false;
			boolean e = false;
			boolean l = false;
			boolean empty = false;			

			if(list.size() != 3){
				for(Object map : list){
					if(!((HashMap)map).get("yyyymmdd").equals(sStart)){
						s = true;
						break;
					}
				}
				
				for(Object map : list){
					if(!((HashMap)map).get("yyyymmdd").equals(sEnd)){
						e = true;
						break;
					}
				}
				
				for(Object map : list){
					if(!((HashMap)map).get("yyyymmdd").equals(sLastYear)){
						l = true;
						break;
					}
				}
				
				if(list.size() == 0) empty = true; 
				
				if(e || empty){
					Map<String, Object> addMap = new HashMap<String, Object>();
					((HashMap)addMap).put("yyyymmdd", sEnd);
					((HashMap)addMap).put("usage", 0);
					((HashMap)addMap).put("price", "0");
					
					list.add(addMap);
				}
				
				if(s || empty){
					Map<String, Object> addMap = new HashMap<String, Object>();
					((HashMap)addMap).put("yyyymmdd", sStart);
					((HashMap)addMap).put("usage", 0);
					((HashMap)addMap).put("price", "0");
					
					list.add(addMap);
				}
				
				if(l || empty){
					Map<String, Object> addMap = new HashMap<String, Object>();
					((HashMap)addMap).put("yyyymmdd", sLastYear);
					((HashMap)addMap).put("usage", 0);
					((HashMap)addMap).put("price", "0");
					
					list.add(addMap);
				}
			}
*/
			/* end 전월, 현월, 전년동월 의 데이터가 하나라도 없으면 0으로 세팅하여 보여줌.(데이터가 없어도 무조건 보이게 처리)*/
			
			int startYear = Integer.parseInt(sStart.substring(0, 4));
			int startMonth = Integer.parseInt(sStart.substring(4, 6));
			int endYear = Integer.parseInt(sEnd.substring(0, 4));
			int endMonth = Integer.parseInt(sEnd.substring(4, 6));			
			
			List<Object> resulltList = new ArrayList<Object>();
			for(Object obj: list) {
				HashMap data = (HashMap) obj;
				String yyyymmdd = (String) data.get("yyyymmdd");
				while(true) {
					if(yyyymmdd.equals(getYYYYMM(startYear, startMonth))) {
						resulltList.add(data);
						if(startMonth < 12) {
							startMonth++;
						} else {
							startYear++;
							startMonth = 1;
						}
						break;
					} else {
						HashMap<String, String> tempData = new HashMap<String, String>();
						tempData.put("yyyymmdd", getYYYYMM(startYear, startMonth));
						tempData.put("usage", "0");
						tempData.put("price", "0");
						resulltList.add(tempData);
						
						if(startMonth < 12) {
							startMonth++;
						} else {
							startYear++;
							startMonth = 1;
						}
					}
				}
			}
			
			if(startYear < endYear || (startYear == endYear && startMonth <= endMonth)) {
				if(startYear < endYear) {
					for(int i=startYear ; i <= endYear ; i++) {
						for(int j=startMonth ; j<= 12; j++) {
							HashMap<String, String> tempData = new HashMap<String, String>();
							tempData.put("yyyymmdd", getYYYYMM(i, j));
							tempData.put("usage", "0");
							tempData.put("price", "0");
							resulltList.add(tempData);
							
							if(startYear == endYear && startMonth == endMonth)
								break;
						}
					}
				} else {
					for(int i=startMonth ; i <= endMonth ; i++) {
						HashMap<String, String> tempData = new HashMap<String, String>();
						tempData.put("yyyymmdd", getYYYYMM(startYear, i));
						tempData.put("usage", "0");
						tempData.put("price", "0");
						resulltList.add(tempData);
					}
				}
			}
			
			Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		 	DecimalFormat dfMd = DecimalUtil.getDecimalFormat(supplier.getMd());
			for(Object obj: resulltList) {
				HashMap data = (HashMap) obj;
				String usage = StringUtil.nullToZero(data.get("usage"));
				data.put("yyyymmdd", String.valueOf(data.get("yyyymmdd")).substring(0, 4) + ". " + Integer.parseInt(String.valueOf(data.get("yyyymmdd")).substring(4)));
				data.put("usage", dfMd.format(Double.parseDouble(usage)).replaceAll(",", ""));
			}
			
			result.add(resulltList);	
		/*연도별*/
		}else if(CommonConstants.DateType.valueOf("YEARLY").getCode().equals(searchType)){
			list = meteringDayDao.getCustomerUsageEmYearly(METER_TYPE, params);
			
			/*사용요금을 가져와 세팅함.*/
//			setPrice(list, contractList, METER_TYPE);			
			List<Object> resulltList = new ArrayList<Object>();
			
			int sYear = Integer.parseInt(sEnd.substring(0, 4));
			int cIndex = 0;
			for(int i=1 ; i <= 12 ; i++) {
				if(list != null && list.size() > cIndex) {
					HashMap data = (HashMap) list.get(cIndex);
					String yyyymmdd = (String) data.get("yyyymmdd");
					if(yyyymmdd.equals(getYYYYMM(sYear, i))) {
						resulltList.add(data);
						cIndex++;
					} else {
						HashMap<String, String> tempData = new HashMap<String, String>();
						tempData.put("yyyymmdd", getYYYYMM(sYear, i));
						tempData.put("usage", "0");
						tempData.put("price", "0");
						resulltList.add(tempData);
					}										
				} else {
					HashMap<String, String> tempData = new HashMap<String, String>();
					tempData.put("yyyymmdd", getYYYYMM(sYear, i));
					tempData.put("usage", "0");
					tempData.put("price", "0");
					resulltList.add(tempData);
				}
				
			}
			
			Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		 	DecimalFormat dfMd = DecimalUtil.getDecimalFormat(supplier.getMd());
			for(Object obj: resulltList) {
				HashMap data = (HashMap) obj;
				String usage = StringUtil.nullToZero(data.get("usage"));
				data.put("yyyymmdd", String.valueOf(data.get("yyyymmdd")).substring(0, 4) + ". " + Integer.parseInt(String.valueOf(data.get("yyyymmdd")).substring(4)));
				data.put("usage", dfMd.format(Double.parseDouble(usage)).replaceAll(",", ""));
			}
			
			result.add(resulltList);
		/*일별*/
		}else if(CommonConstants.DateType.valueOf("DAILY").getCode().equals(searchType)){
			
			List<Object> tmpList      = new ArrayList<Object>();
			
			tmpList	= meteringDayDao.getCustomerUsageEmHourly(METER_TYPE, params);

			/*
			 * 그리드 및 챠트에 보여지기 위한 데이터 변환 start.
			 */
			int listSize	= tmpList.size();
			
			List<Object> tmpMap		= null;
			Map<String, String> tmpResult	= null;
			List<Object> tmpResult2	=  new ArrayList<Object>();
			
            if(!"".equals(supplierId)) {
                
                Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
                DecimalFormat dfMd = DecimalUtil.getDecimalFormat(supplier.getMd());
                
    			if(tmpList != null && listSize > 0) {
    				for(int i=0; i<listSize; i++){
    					tmpMap	= (List<Object>)tmpList.get(i);
    					
    					for(int j=0; j<tmpMap.size(); j++){
    					    
    						tmpResult	= new HashMap<String, String>();				
    						String usage = StringUtil.nullToZero(tmpMap.get(j));
    						tmpResult.put("yyyymmdd", String.format("%02d", j));// + "~" + String.format("%02d", j+1) + "(h)");
    				    	tmpResult.put("usage", dfMd.format(Double.parseDouble(usage)).replaceAll(",", ""));
    				    	tmpResult.put("price", "-");
    				    	
    				    	tmpResult2.add(tmpResult);
    					}
    				}
    			} else {
    				for(int i=0 ; i <= 24 ; i++) {
    				    
    					tmpResult	= new HashMap<String, String>();				
    					
    					tmpResult.put("yyyymmdd", String.format("%02d", i));// + "~" + String.format("%02d", j+1) + "(h)");
    			    	tmpResult.put("usage", dfMd.format(0.0));
    			    	tmpResult.put("price", "0");
    			    	
    			    	tmpResult2.add(tmpResult);
    				}
    			}
            }
			
			/*
			 * 그리드 및 챠트에 보여지기 위한 데이터 변환 end.
			 */
			
			result.add(tmpResult2);
			
		}else
		{
			logger.debug("#################    viewType is not setting !!!!  #########################"  );
		}
		
		/*JSP 문구 (화면에 일/월/년을) 표시하기 위해*/
		Map<String, String> tmpMap	= new HashMap<String, String>();
		tmpMap.put("sEnd", sEnd);
		result.add(tmpMap);
		
		logger.debug("@@@@@@@ = > " + result);
		
		return result;
	}
	
	/*사용요금을 조회한다.*/
	public List<Object> getCustomerUsageFee(Map<String, Object> params){
		
		String iMdev_type	= StringUtil.nullToBlank(params.get("iMdev_type"));
		
		String sStart	= "";
		String sEnd		= "";
		
		if("".equals(iMdev_type)){
			iMdev_type	= CommonConstants.DeviceType.Meter.name();
			params.put("iMdev_type", iMdev_type);
		}
		
		/*전월*/
		sStart	= CalendarUtil.getDateWithoutFormat(CalendarUtil.getCurrentDate(), Calendar.MONTH, -1).substring(0, 6);
		params.put("sStart", sStart);
		
		/*현월*/
		sEnd	= CalendarUtil.getCurrentDate().substring(0, 6);
		params.put("sEnd", sEnd);
		
		String sLastYear	= sEnd + "01";
		
		/*전년 동월*/
		params.put("sLastYear", CalendarUtil.getDateWithoutFormat(sLastYear, Calendar.YEAR, -1).substring(0, 6));
		
		List<Object> result	= new ArrayList<Object>();
		
		/*사용량을 가져온다.*/
		result.add(meteringDayDao.getCustomerUsageFee(params));
		/*화면에 날짜를 표시하기 위해.*/
		result.add(sEnd);
		
		/*고객 ID로 계약 리스트를 가져옴.*/
		Operator operator = operatorDao.get(Integer.parseInt(params.get("sUserId").toString()));
		List<Contract> contractList = new ArrayList<Contract>();
		Customer customer = customerDao.getCustomersByLoginId(operator.getLoginId());
		//logger.debug("CUSTOMER="+customer.getName());
		
		if(customer != null){
		
			Set<Contract> cont = customer.getContracts();
			
			if(cont.toArray().length != 0){
				Object[] array = cont.toArray();
				
				for(int i = 0; array != null && i < array.length; i++) {
					contractList.add((Contract)array[i]);
				}

			}
		}

		logger.debug("CONTRACTSIZE="+contractList.size());
		logger.debug("SUUSERID="+params.get("sUserId"));
		
		/*사용요금을 가져온다.*/
		result.add(getSumTariff(contractList, CommonConstants.DateType.DAILY.getCode(), CalendarUtil.getCurrentDate().substring(0, 6) + "01", CalendarUtil.getCurrentDate(), ""));
		
		return result;
	}
	
	/*요금표를 조회한다.*/
	public Map<String, Object> getCustomerTariff(Map<String, Object> params){
		
		String METER_TYPE	= StringUtil.nullToBlank(params.get("METER_TYPE"));	// 미터유형(전기,가스,수도)
		
		logger.debug("params userId 		: " + StringUtil.nullToBlank(params.get("sUserId")));
		logger.debug("params METER_TYPE 	: " + METER_TYPE);
		logger.debug("params yyyymmdd = " + StringUtil.nullToBlank(params.get("yyyymmdd")));
		logger.debug("params supplierId = " + StringUtil.nullToBlank(params.get("supplierId")));
		
		Map<String, Object> result = new HashMap<String, Object>();
		
    	if(METER_TYPE.equals(CommonConstants.SupplierType.Electricity.name())){
			result.put("tariffEm", tariffEMDao.getCustomerChargeMgmtList(params));
    	}
    	else if(METER_TYPE.equals(CommonConstants.SupplierType.Gas.name())){
			result.put("tariffGm", tariffGMDao.getCustomerChargeMgmtList(params));
    	}
    	else if(METER_TYPE.equals(CommonConstants.SupplierType.Water.name())){
			result.put("tariffWmCal", tariffWMCaliberDao.getChargeMgmtList(params)); // -- 공급자 ID 필요...
			result.put("tariffWm", tariffWMDao.getCustomerChargeMgmtList(params));
    	}else{
    		logger.debug("METER_TYPE이 잘못되었습니다.");
    	}
    	
		return result;
		
	}
	
	/*월, 사용량, 요금의 필드를 가져온 리스트(요금정보는 들어 있지 않다. 필드만 존재)에 요금필드에 요금을 세팅한다.*/
	@SuppressWarnings("unchecked")
	public void setPrice(List<Object> list, List<Contract> contractList, String METER_TYPE){
		
		for(Object map : list){
			
			String str = ((HashMap)map).get("yyyymmdd").toString();
			
			Map<String, Double> tariffMap = getSumTariff(contractList, CommonConstants.DateType.valueOf("MONTHLY").getCode(), str, str, METER_TYPE);
			
			if(CommonConstants.ChangeMeterTypeName.EM.getCode().equals(METER_TYPE) 
					|| CommonConstants.SupplierType.Electricity.name().equals(METER_TYPE)
					|| "EnergyMeter".equals(METER_TYPE)){
				((HashMap)map).put("price", tariffMap.get(CommonConstants.SupplierType.Electricity.name()));
			}else if(CommonConstants.ChangeMeterTypeName.GM.getCode().equals(METER_TYPE) 
					|| CommonConstants.SupplierType.Gas.name().equals(METER_TYPE)
					|| "GasMeter".equals(METER_TYPE)){
				((HashMap)map).put("price", tariffMap.get(CommonConstants.SupplierType.Gas.name()));
			}else if(CommonConstants.ChangeMeterTypeName.WM.getCode().equals(METER_TYPE) 
					|| CommonConstants.SupplierType.Water.name().equals(METER_TYPE)
					|| "WaterMeter".equals(METER_TYPE)){
				((HashMap)map).put("price", tariffMap.get(CommonConstants.SupplierType.Water.name()));
			}
		}
	}
	
	public List<Object> getCustomerCO2Daily(Map<String, Object> condition){
		return meteringDayDao.getCustomerCO2Daily(condition);
	}
	
	/*dateType, startDate, endDate에 따른 요금을 가져온다.*/
	public Map<String, Double> getSumTariff(List<Contract> contractList, String dateType, String startDate, String endDate, String serviceType){
		
		Map<String, Object> tariff_params	= new HashMap<String, Object>();
		
		Double emSum = 0.0;
		Double gmSum = 0.0;
		Double wmSum = 0.0;
		
		 /*서비스유형에 따른 요금 합계를 구한다.*/
		for(Contract contract : contractList){
			
			tariff_params.put("contract", contract);
			tariff_params.put("dateType", dateType);
			tariff_params.put("startDate", startDate);
			tariff_params.put("endDate", endDate);
			
			if(CommonConstants.SupplierType.Electricity.name().equals(contract.getServiceTypeCode().getName()) 
					|| CommonConstants.ChangeMeterTypeName.EM.getCode().equals(serviceType)
					|| "EnergyMeter".equals(serviceType)){
				emSum += tariffEMDao.getUsageChargeByContract(tariff_params)==null?0.0:tariffEMDao.getUsageChargeByContract(tariff_params);
			}else if(CommonConstants.SupplierType.Gas.name().equals(contract.getServiceTypeCode().getName()) 
					|| CommonConstants.ChangeMeterTypeName.GM.getCode().equals(serviceType)
					|| "GasMeter".equals(serviceType)){
				gmSum += tariffGMDao.getUsageChargeByContract(tariff_params)==null?0.0:tariffGMDao.getUsageChargeByContract(tariff_params);
			}else if(CommonConstants.SupplierType.Water.name().equals(contract.getServiceTypeCode().getName()) 
					|| CommonConstants.ChangeMeterTypeName.WM.getCode().equals(serviceType)
					|| "WaterMeter".equals(serviceType)){
				wmSum += tariffWMDao.getUsageChargeByContract(tariff_params)==null?0.0:tariffWMDao.getUsageChargeByContract(tariff_params);
			}
		}
		
		Map<String, Double> tariffMap = new HashMap<String, Double>();
		tariffMap.put(CommonConstants.SupplierType.Electricity.name(), emSum);
		tariffMap.put(CommonConstants.SupplierType.Gas.name(), gmSum);
		tariffMap.put(CommonConstants.SupplierType.Water.name(), wmSum);
		tariffMap.put("SUM", emSum + gmSum + wmSum);
		
		return tariffMap;		
	}
	
	public String getYYYYMM(int y, int m) {
		if(m < 10) {
			return y + "0" + m;
		} else {
			return "" + y + m;
		}
		
	}
}
