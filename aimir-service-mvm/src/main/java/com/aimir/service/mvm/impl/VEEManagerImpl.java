package com.aimir.service.mvm.impl;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.EditItem;
import com.aimir.constants.CommonConstants.MeterCodes;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.VEEParam;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.LpGMDao;
import com.aimir.dao.mvm.LpWMDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.dao.mvm.MonthWMDao;
import com.aimir.dao.mvm.VEELogDao;
import com.aimir.dao.mvm.VEEParameterDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.DayGM;
import com.aimir.model.mvm.DayPk;
import com.aimir.model.mvm.DayWM;
import com.aimir.model.mvm.LpEM;
import com.aimir.model.mvm.LpGM;
import com.aimir.model.mvm.LpPk;
import com.aimir.model.mvm.LpWM;
import com.aimir.model.mvm.MeteringDay;
import com.aimir.model.mvm.MeteringLP;
import com.aimir.model.mvm.MeteringMonth;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.mvm.MonthGM;
import com.aimir.model.mvm.MonthPk;
import com.aimir.model.mvm.MonthWM;
import com.aimir.model.mvm.VEELog;
import com.aimir.model.mvm.VEEParameter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.VEEManager;
import com.aimir.service.mvm.bean.VEEHistoryData;
import com.aimir.service.mvm.bean.VEEMaxData;
import com.aimir.service.mvm.bean.VEEMaxDetailData;
import com.aimir.service.mvm.bean.VEEMiniData;
import com.aimir.service.mvm.bean.VEEParameterData;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Service(value="vEEManager")
@Transactional(propagation=Propagation.REQUIRED)
public class VEEManagerImpl implements VEEManager{
	
	
	@Autowired
	VEEParameterDao vEEParameterDao;
	
	@Autowired
	MeteringDayDao meteringDayDao;
	
	@Autowired
	VEELogDao vEELogDao;
	
	@Autowired
	MeterDao meterDao;
	
	@Autowired
	LpEMDao lpEMDao;
	
	@Autowired
	LpGMDao lpGMDao;
	
	@Autowired
	LpWMDao lpWMDao;
	
	@Autowired
	DayEMDao dayEMDao;
	
	@Autowired
	DayWMDao dayWMDao;
	
	@Autowired
	DayGMDao dayGMDao;
	
	@Autowired
	MonthEMDao monthEMDao;
	
	@Autowired
	MonthGMDao monthGMDao;
	
	@Autowired
	MonthWMDao monthWMDao;
	
	@Autowired
	OperatorDao operatorDao;
	
	@Autowired
	SupplierDao supplierDao;

    @Autowired
    ContractDao contractDao;
    
    @Autowired
    LocationDao locationDao;

	Log logger = LogFactory.getLog(MvmChartViewManagerImpl.class);
	
	// Table Item을 추출함
	public List<String> getTableItemList() {
		List<String> result = new ArrayList<String>();
		int objSize = CommonConstants.VEETableItem.values().length;
	    for(int i=0;i<objSize;i++) {
	    	
	    	if(CommonConstants.VEETableItem.Meter.toString().equals(CommonConstants.VEETableItem.values()[i].toString())) continue;
	    	if(CommonConstants.VEETableItem.EventAlertLog.toString().equals(CommonConstants.VEETableItem.values()[i].toString())) continue;
	    	result.add(CommonConstants.VEETableItem.values()[i].toString());
	    }
	    return result;
	}
	
	//VEERule의 select박스를 생성함
	public List<Map<String, Object>> getVEEEditItemList() {
			
	    List<Map<String, Object>> editItemList = new ArrayList<Map<String, Object>>();

		int objSize = CommonConstants.EditItem.values().length;
	    for(int i=0;i<objSize;i++) {
	        Map<String, Object> data = new HashMap<String, Object>();
	        data.put("code", CommonConstants.EditItem.values()[i].getCode());
	        data.put("name", CommonConstants.EditItem.values()[i].name());
	        editItemList.add(data);
	    }
	    return editItemList;
	}
		
	//VEERule의 select박스를 생성함
	public List<Map<String, Object>> getVEEParameterNameList() {
		List<Map<String, Object>> veeParamList = new ArrayList<Map<String, Object>>();
		int objSize = CommonConstants.VEEParam.values().length;
		for(int i=0;i<objSize;i++) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("code", i);
            data.put("name", CommonConstants.VEEParam.values()[i].name());
            veeParamList.add(data);
        }
	    return veeParamList;
	}
	
	public List<Map<String, String>> getVEEParamNames(String ruleType) {
		List<VEEParameter> parameters = 
				vEEParameterDao.getParameterList(ruleType);
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		
	    for ( VEEParameter param : parameters ) {
	    	Map<String, String> map = new HashMap<String, String>();
	    	map.put("name", param.getLocalName());
	    	map.put("value", param.getParameter().toString());
	    	result.add(map);
	    	
	    }
	    return result;		
	}
	
	//VEERule의 select박스를 생성함
	public List<String> getVEERuleList() {
		List<String> result = new ArrayList<String>();
		int objSize = CommonConstants.VEEType.values().length;
	    for(int i=0;i<objSize;i++) {
	    	result.add(CommonConstants.VEEType.values()[i].toString());
	    }
	    return result;
	}
	
	// MiniVEEHistory 데이터 조회
	public List<Object> getMiniVEEHistoryManager(String[] values) {
		List<Object> result = new ArrayList<Object>();
		HashMap<String,Object> conditions = makeMiniSearchCondition(values);
		List<Object> dataList = vEELogDao.getVeeLogByCountList(conditions);
		int dataSize = dataList.size();
		if(dataSize > 0) {
			Supplier supplier = supplierDao.get(Integer.parseInt(values[8]));
			DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
			Iterator<Object> it = dataList.iterator();
			while (it.hasNext()) {
				Object[] ojb = (Object[]) it.next();
				EditItem item = (EditItem)ojb[0];
				Long count = (Long)ojb[1];
				VEEMiniData vd = new VEEMiniData();
				vd.setItem(item.toString());
				vd.setCount(dfMd.format(count));
				result.add(vd);
			}
		}
		return result;
	}
	
	@SuppressWarnings("unused")
    public Map<String, String> getMaxVEEValidationCheckManagerTotal(String[] values){
		
		String meterType	= StringUtil.nullToBlank(values[0]);
		String startDate	= StringUtil.nullToBlank(values[3]);	//조회조건
		String endDate		= StringUtil.nullToBlank(values[4]);	//조회조건
		String contractNo	= StringUtil.nullToBlank(values[6]);	//조회조건
		String meterId		= StringUtil.nullToBlank(values[7]);	//조회조건
		String userId		= StringUtil.nullToBlank(values[8]);	//조회조건
		String veeParametersName	= StringUtil.nullToBlank(values[9]); //조회조건
		
		if("".equals(startDate)){
			startDate = CalendarUtil.getCurrentDate();
			endDate = CalendarUtil.getCurrentDate();
		}
		
		Map<String, String> result = new HashMap<String, String>();
        result.put("total", "0");
		
		List<HashMap<String, Object>> conList = (List<HashMap<String, Object>>)makeParametersConditions(values, veeParametersName);
		
		Iterator<HashMap<String, Object>> it = conList.iterator();
		
		while (it.hasNext()) {
			HashMap<String, Object> hm = (HashMap<String, Object>) it.next();
			String useYn = (String)hm.get("useYn");
			String item = (String)hm.get("item");
			
			String condition1 = StringUtil.nullToBlank(hm.get("condition1"));
			String conValue1 = StringUtil.nullToBlank(hm.get("conValue1"));
			String condition2 = StringUtil.nullToBlank(hm.get("condition2"));
			String conValue2 = StringUtil.nullToBlank(hm.get("conValue2"));
			String keyValue	= hm.get("keyValue").toString();

			//조회 조건
			hm.put("startDate", startDate);
			hm.put("endDate", endDate);
			hm.put("contractNo", contractNo);
			hm.put("meterId", meterId);
			hm.put("userId", userId);
			
			//임계치 사용여부
			if("true".equals(useYn)) {
				HashMap<String, Object> querys = makeThresholdPeriodAndThresholdItemQuery(hm, meterType);
				List<Map<String, Object>> compareList = meteringDayDao.getDayVEEList(querys.get("compareQry").toString());

				String 	className 	= "";
				String  yyyymmdd	= "";
				int	 	valueCnt1	= 0;
				int	 	valueCnt2	= 0;
				
				if(CommonConstants.VEETableItem.Day.toString().equals(item)) {//day일때

					className 	= CommonConstants.MeterType.valueOf(meterType).getDayClassName();
					yyyymmdd	= "yyyymmdd";
					valueCnt1	= 0; 
					valueCnt2	= 23; 
				}
				else if(CommonConstants.VEETableItem.LoadProfile.toString().equals(item)) {
					className 	= CommonConstants.MeterType.valueOf(meterType).getLpClassName();
					yyyymmdd	= "yyyymmddhh";
					valueCnt1	= 0;
					valueCnt2	= 59;
				}else if(CommonConstants.VEETableItem.Month.toString().equals(item)){
					className 	= CommonConstants.MeterType.valueOf(meterType).getMonthClassName();
					yyyymmdd	= "yyyymm";
					valueCnt1	= 1;
					valueCnt2	= 31;
				}

				// 조회 조건에 해당하는 자료들 중 임계치 설정에 해당하는 자료 가져오기.
				if(compareList.size() > 0){
					
					Map<String, Object> compareMap = compareList.get(0);
					
					StringBuffer filterSB = new StringBuffer();
					
					filterSB.append(" SELECT t.id.").append(yyyymmdd).append(" as yyyymmdd, t.id.channel as channel, m.mdsId as mdsId, c.id as contractId, t.id.mdevType as mdevType, t.id.mdevId as mdevId, t.id.dst as dst, c.contractNumber as contractNo, ");

					//value_00, value_01, value_02.....  
					getQuery(valueCnt1, valueCnt2, filterSB, className, yyyymmdd, startDate, endDate);
					
					if(!"".equals(StringUtil.nullToBlank(hm.get("contractNo")))){
						filterSB.append(" AND c.contractNumber = '").append(hm.get("contractNo")).append("' ");
					}
					
					if(!"".equals(StringUtil.nullToBlank(hm.get("meterId")))){
						filterSB.append(" AND m.mdsId = '").append(hm.get("meterId")).append("' ");
					}
					
					// 임계치값 검색(Out Of Threshold)
					if(keyValue.equals("OutOfThreshold")){
						filterSB.append(" AND (  t.value_00 > m.usageThreshold or  t.value_01 > m.usageThreshold or  " +
								"t.value_02 > m.usageThreshold or  t.value_03 > m.usageThreshold or  t.value_04 > m.usageThreshold or  " +
								"t.value_05 > m.usageThreshold or  t.value_06 > m.usageThreshold or  t.value_07 > m.usageThreshold or  " +
								"t.value_08 > m.usageThreshold or  t.value_09 > m.usageThreshold or  t.value_10 > m.usageThreshold or  " +
								"t.value_11 > m.usageThreshold or  t.value_12 > m.usageThreshold or  t.value_13 > m.usageThreshold or  " +
								"t.value_14 > m.usageThreshold or  t.value_15 > m.usageThreshold or  t.value_16 > m.usageThreshold or  " +
								"t.value_17 > m.usageThreshold or  t.value_18 > m.usageThreshold or  t.value_19 > m.usageThreshold or  " +
								"t.value_20 > m.usageThreshold or  t.value_21 > m.usageThreshold or  t.value_22 > m.usageThreshold or  " +
								"t.value_23  > m.usageThreshold ) ");
					}		
					//LoadProfile인 경우 total이 존재하지 않음으로 조건 추가  
					if(!CommonConstants.VEETableItem.LoadProfile.toString().equals(item)) {
					//VEE_PARAMETER의 THRESHOLD_XX 에 대한 조건 
						if(!"".equals( condition1) && !"".equals(conValue1) && ("".equals( condition2) || "".equals(conValue2))){
							filterSB.append(" AND t.total").append(" ").append(condition1).append(conValue1+" ");
						}else if(!"".equals( condition2) && !"".equals(conValue2) && ("".equals( condition1) || "".equals(conValue1))){
							filterSB.append(" AND t.total").append(" ").append(condition2).append(conValue2+" ");
						}else if(!"".equals( condition1) && !"".equals(conValue1) && !"".equals( condition2) || !"".equals(conValue2)){
							filterSB.append(" AND t.total ").append(condition1).append(conValue1+" ")
							.append(" AND t.total ").append(condition2).append(conValue2+" ");
						}else{
						
						}
					}
					
					List<Map<String, Object>> filterList = meteringDayDao.getDayVEEListTotal(filterSB.toString());
					
					result.put("total", filterList.size() + "");
					
				}
				
			}else {
				
				HashMap<String, Object> querys = makeConditionQuery(hm, meterType);
				List<Map<String, Object>> compareList = meteringDayDao.getDayVEEListTotal(querys.get("compareQry").toString());
				result.put("total", compareList.size() + "");
			}
		}

		return result;
	}
	
	@SuppressWarnings("unused")
    public List<VEEMaxData> getMaxVEEValidationCheckManager(String[] values, String startRow, String pageSize) {
		
		String meterType	= StringUtil.nullToBlank(values[0]);
		String startDate	= StringUtil.nullToBlank(values[3]);	//조회조건
		String endDate		= StringUtil.nullToBlank(values[4]);	//조회조건
		String contractNo	= StringUtil.nullToBlank(values[6]);	//조회조건
		String meterId		= StringUtil.nullToBlank(values[7]);	//조회조건
		String userId		= StringUtil.nullToBlank(values[8]);	//조회조건
		String veeParametersName	= StringUtil.nullToBlank(values[9]); //조회조건
		
		if ("".equals(startDate)) {
			startDate = CalendarUtil.getCurrentDate();
			endDate = CalendarUtil.getCurrentDate();
		}
		
		int istartRow = (int)(Double.parseDouble(startRow));
		int ipageSize = (int)(Double.parseDouble(pageSize));
		
		List<VEEMaxData> result = new ArrayList<VEEMaxData>();
		
		List<HashMap<String, Object>> conList = (List<HashMap<String, Object>>)makeParametersConditions(values, veeParametersName);
		
		Iterator<HashMap<String, Object>> it = conList.iterator();
		
		Supplier supplier = supplierDao.get(Integer.parseInt(StringUtil.nullToBlank(values[13])));
		
		int num = 0;
		CommonConstants.VEETableItem vee = null;
		
		while (it.hasNext()) {
			HashMap<String, Object> hm = (HashMap<String, Object>) it.next();
			String useYn = (String)hm.get("useYn");
			String item = (String)hm.get("item");
			vee = CommonConstants.VEETableItem.valueOf(item);
			
			String condition1 = StringUtil.nullToBlank(hm.get("condition1"));
			String conValue1 = StringUtil.nullToBlank(hm.get("conValue1"));
			String condition2 = StringUtil.nullToBlank(hm.get("condition2"));
			String conValue2 = StringUtil.nullToBlank(hm.get("conValue2"));
			String keyValue	= hm.get("keyValue").toString();
						
			//조회 조건
			hm.put("startDate", startDate);
			hm.put("endDate", endDate);
			hm.put("contractNo", contractNo);
			hm.put("meterId", meterId);
			hm.put("userId", userId);
			
			//임계치 사용여부
			if ("true".equals(useYn)) {
				HashMap<String, Object> querys = makeThresholdPeriodAndThresholdItemQuery(hm, meterType);
				List<Map<String, Object>> compareList = meteringDayDao.getDayVEEList(querys.get("compareQry").toString());

				String 	className 	= "";
				String  yyyymmdd	= "";
				int	 	valueCnt1	= 0;
				int	 	valueCnt2	= 0;
				
				if (CommonConstants.VEETableItem.Day.toString().equals(item)) {//day일때
					className 	= CommonConstants.MeterType.valueOf(meterType).getDayClassName();
					yyyymmdd	= "yyyymmdd";
					valueCnt1	= 0; 
					valueCnt2	= 23; 
				} else if (CommonConstants.VEETableItem.LoadProfile.toString().equals(item)) {
					className 	= CommonConstants.MeterType.valueOf(meterType).getLpClassName();
					yyyymmdd	= "yyyymmddhh";
					valueCnt1	= 0;
					valueCnt2	= 59;
				} else if (CommonConstants.VEETableItem.Month.toString().equals(item)) {
					className 	= CommonConstants.MeterType.valueOf(meterType).getMonthClassName();
					yyyymmdd	= "yyyymm";
					valueCnt1	= 1;
					valueCnt2	= 31;
				}
				
				// 조회 조건에 해당하는 자료들 중 임계치 설정에 해당하는 자료 가져오기.
				if (compareList.size() > 0) {

					Map<String, Object> compareMap = compareList.get(0);

					StringBuffer filterSB = new StringBuffer();

					filterSB.append(" SELECT t.id.").append(yyyymmdd).append(" as yyyymmdd, t.id.channel as channel, m.mdsId as mdsId, c.id as contractId, t.id.mdevType as mdevType, t.id.mdevId as mdevId, t.id.dst as dst, c.contractNumber as contractNo,");

					//value_00, value_01, value_02.....  
					getQuery(valueCnt1, valueCnt2, filterSB, className, yyyymmdd, startDate, endDate);

					if (!"".equals(StringUtil.nullToBlank(hm.get("contractNo")))) {
						filterSB.append(" AND c.contractNumber = '").append(hm.get("contractNo")).append("' ");
					}

					if (!"".equals(StringUtil.nullToBlank(hm.get("meterId")))) {
						filterSB.append(" AND m.mdsId = '").append(hm.get("meterId")).append("' ");
					}

					// 임계치값 검색(Out Of Threshold)
					if (keyValue.equals("OutOfThreshold")) {
						filterSB.append(" AND (  t.value_00 > m.usageThreshold or  t.value_01 > m.usageThreshold or  " +
								"t.value_02 > m.usageThreshold or  t.value_03 > m.usageThreshold or  t.value_04 > m.usageThreshold or  " +
								"t.value_05 > m.usageThreshold or  t.value_06 > m.usageThreshold or  t.value_07 > m.usageThreshold or  " +
								"t.value_08 > m.usageThreshold or  t.value_09 > m.usageThreshold or  t.value_10 > m.usageThreshold or  " +
								"t.value_11 > m.usageThreshold or  t.value_12 > m.usageThreshold or  t.value_13 > m.usageThreshold or  " +
								"t.value_14 > m.usageThreshold or  t.value_15 > m.usageThreshold or  t.value_16 > m.usageThreshold or  " +
								"t.value_17 > m.usageThreshold or  t.value_18 > m.usageThreshold or  t.value_19 > m.usageThreshold or  " +
								"t.value_20 > m.usageThreshold or  t.value_21 > m.usageThreshold or  t.value_22 > m.usageThreshold or  " +
								"t.value_23  > m.usageThreshold ) ");
					}

					if (!CommonConstants.VEETableItem.LoadProfile.toString().equals(item)) {
					//VEE_PARAMETER의 THRESHOLD_XX 에 대한 조건 
						if (!"".equals( condition1) && !"".equals(conValue1) && ("".equals( condition2) || "".equals(conValue2))) {
							filterSB.append(" AND t.total").append(" ").append(condition1).append(conValue1+" ");
						} else if (!"".equals( condition2) && !"".equals(conValue2) && ("".equals( condition1) || "".equals(conValue1))) {
							filterSB.append(" AND t.total").append(" ").append(condition2).append(conValue2+" ");
						} else if (!"".equals( condition1) && !"".equals(conValue1) && !"".equals( condition2) || !"".equals(conValue2)) {
							filterSB.append(" AND t.total ").append(condition1).append(conValue1+" ")
							        .append(" AND t.total ").append(condition2).append(conValue2+" ");
						} else {
		
						}
					}

				    filterSB.append(" ORDER BY t.id.").append(yyyymmdd).append(" DESC ");   // sorting

					List<Map<String, Object>> filterList = meteringDayDao.getDayVEEListPage(filterSB.toString(), istartRow * ipageSize, ipageSize);
					
					VEEMaxData vmd = null;
					
					for (int i = 0; i < filterList.size(); i++) {
						vmd = new VEEMaxData();
						
						Map<String, Object> rstMap = filterList.get(i);
						vmd.setCheckItem(keyValue);
						vmd.setContractId(StringUtil.nullToBlank(rstMap.get("contractId")));
						vmd.setContractNo(StringUtil.nullToBlank(rstMap.get("contractNo")));
						vmd.setDeviceId(StringUtil.nullToBlank(rstMap.get("mdsId")));
						vmd.setWriteTime(TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(rstMap.get("yyyymmdd")) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
						vmd.setChannel(StringUtil.nullToBlank(rstMap.get("channel")));
						vmd.setTable(item);
						
						vmd.setYyyymmdd(StringUtil.nullToBlank(rstMap.get("yyyymmdd")));
						vmd.setMdevType(StringUtil.nullToBlank(rstMap.get("mdevType")));
						vmd.setMdevId(StringUtil.nullToBlank(rstMap.get("mdevId")));
						vmd.setDst(StringUtil.nullToBlank(rstMap.get("dst")));
						vmd.setTotal(istartRow+i+1);
						result.add(vmd);
					}
				}
			} else {

				HashMap<String, Object> querys = makeConditionQuery(hm, meterType);
				StringBuilder sbCompareQry = new StringBuilder(querys.get("compareQry").toString());
				// sorting
				switch (vee) {
				    case Meter:
				        sbCompareQry.append(" ORDER BY m.lastReadDate DESC ");
				        break;
				    case EventAlertLog:
				        sbCompareQry.append(" ORDER BY openTime DESC ");
                        break;
				    case LoadProfile:
                        sbCompareQry.append(" ORDER BY t.id.yyyymmddhh DESC ");
                        break;
                    case Day:
                        sbCompareQry.append(" ORDER BY t.id.yyyymmdd DESC ");
                        break;
                    case Month:
                        sbCompareQry.append(" ORDER BY t.id.yyyymm DESC ");
                        break;
				}

				List<Map<String, Object>> compareList = meteringDayDao.getDayVEEListPage(sbCompareQry.toString(), istartRow * ipageSize, ipageSize);
				VEEMaxData vmd = null;
				
				if(CommonConstants.VEETableItem.Meter.toString().equals(item)) {
					for(int i=0; i<compareList.size(); i++){
						vmd = new VEEMaxData();
						
						Map<String, Object> rstMap = compareList.get(i);
						vmd.setCheckItem(keyValue);
						vmd.setContractId(StringUtil.nullToBlank(rstMap.get("contractId")));
						vmd.setContractNo(StringUtil.nullToBlank(rstMap.get("contractNo")));
						vmd.setDeviceId(StringUtil.nullToBlank(rstMap.get("mdsId")));
						vmd.setWriteTime(TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(rstMap.get("lastReadDate")) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
						vmd.setMeterId(StringUtil.nullToBlank(rstMap.get("meterId")));
						vmd.setTable(item);
						vmd.setTotal(istartRow+i+1);
						result.add(vmd);
					}
				}else if(CommonConstants.VEETableItem.EventAlertLog.toString().equals(item)) {
					for(int i=0; i<compareList.size(); i++){
						vmd = new VEEMaxData();
						
						Map<String, Object> rstMap = compareList.get(i);
						vmd.setCheckItem(keyValue);
						vmd.setDeviceId(StringUtil.nullToBlank(rstMap.get("activatorId")));
						vmd.setWriteTime(TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(rstMap.get("openTime")) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
						vmd.setEventAlertLogId(StringUtil.nullToBlank(rstMap.get("eventAlertLogId")));
						vmd.setTable(item);
						vmd.setTotal(istartRow+i+1);
						result.add(vmd);
					}
				}else{//meter, lp, month
					
					for(int i=0; i<compareList.size(); i++){
						vmd = new VEEMaxData();
						
						Map<String, Object> rstMap = compareList.get(i);
						vmd.setCheckItem(keyValue);
						vmd.setContractId(StringUtil.nullToBlank(rstMap.get("contractId")));
						vmd.setContractNo(StringUtil.nullToBlank(rstMap.get("contractNo")));
						vmd.setDeviceId(StringUtil.nullToBlank(rstMap.get("mdsId")));
						vmd.setWriteTime(TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(rstMap.get("yyyymmdd")) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
						vmd.setChannel(StringUtil.nullToBlank(rstMap.get("channel")));
						vmd.setTable(item);
						
						vmd.setYyyymmdd(StringUtil.nullToBlank(rstMap.get("yyyymmdd")));
						vmd.setMdevType(StringUtil.nullToBlank(rstMap.get("mdevType")));
						vmd.setMdevId(StringUtil.nullToBlank(rstMap.get("mdevId")));
						vmd.setDst(StringUtil.nullToBlank(rstMap.get("dst")));
						vmd.setTotal(istartRow+i+1);
						result.add(vmd);
					}
				}
				
			}
		}

		return result;

	}
	
	
	// VEE miniValidationCheckManager 조회
	// 기본 로직은 VEEParameger 정보를 조합해서 쿼리를 만든다.
	// 이때 임계치가 있는 데이터들은 먼져 임계치의 데이터에서 해당하는 자료의 contractId를 추출한다.
	// 추출된 임계치 데이터의 ContractID를 기준으로 다시 조회해서 count함
	// makeParametersConditions() 메소드는 화면에서 입수된 조회값과 VEEParameter에 있는 정보를 가지고 각각의 데이터를 hashmap에다 넣는다.
	// hashmap에 들어간 정보중에서 필요한 정보를 추출해서 makeThresholdPeriodAndThresholdItemQuery() -> 임계치 값을 추출하기 위한 sql생성을 하고
	// 이것을 가지고 각각의 정보를 select해서 list의 size를 구함
	// 현재 미완료 건은 임계치 값을 가지고 각각의 item별(meter, day, lp)의 데이터를 조회해서 화면에 카운트 해야함
	// meterType을 기반으로 필요한 테이블을 후출해야함
	// 현재 flex와 DB가 start되지 않아서 일단 코딩상으로 구현함
	// 구현상의 문제점.....
	// 임계치에서 실제 count되는 데이터가  key값이 되는 값으로 되고있지 않음
	// (현재 day_em의 경우 key는 날짜, 채널등임)
	// 따라서 성능상의 문제가 발생할 수 있음
	@SuppressWarnings("unused")
    public List<VEEMiniData> getMiniVEEValidationCheckManager(String[] values) {
		
		List<VEEMiniData> result = new ArrayList<VEEMiniData>();
		
		String meterType = values[0];
		String startDate = StringUtil.nullToBlank(values[3]);
		String endDate	 = StringUtil.nullToBlank(values[4]);
		
		// 여기도 값없으면 오늘날짜
		if("".equals(startDate)){
			startDate = CalendarUtil.getCurrentDate();
			endDate = CalendarUtil.getCurrentDate();
		}
		
		List<HashMap<String, Object>> conList = (List<HashMap<String, Object>>)makeParametersConditions(values, "");
		
		Iterator<HashMap<String, Object>> it = conList.iterator();
		
		Supplier supplier = supplierDao.get(Integer.parseInt(values[8]));
		DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

		while (it.hasNext()) {
			HashMap<String, Object> hm = (HashMap<String, Object>) it.next();
			
			String useYn = (String)hm.get("useYn");
			String item = (String)hm.get("item");
			String keyValue = ((VEEParam)hm.get("keyValue")).name();
			
			String condition1 = StringUtil.nullToBlank(hm.get("condition1"));
			String conValue1 = StringUtil.nullToBlank(hm.get("conValue1"));
			String condition2 = StringUtil.nullToBlank(hm.get("condition2"));
			String conValue2 = StringUtil.nullToBlank(hm.get("conValue2"));
				
			hm.put("startDate", startDate);
			hm.put("endDate", endDate);
			
			Integer dataCount 	= 0;//vee parameter에 의해 걸러진 데이터 갯수
			Integer allCount 	= 0;//전체 갯수
			
			//임계치 사용여부
			if("true".equals(useYn)) {
				HashMap<String, Object> querys = makeThresholdPeriodAndThresholdItemQuery(hm, meterType);
				
				List<Map<String, Object>> compareList = meteringDayDao.getDayVEEList(querys.get("compareQry").toString());
				List<Map<String, Object>> dataList = meteringDayDao.getDayVEEList(querys.get("dataQry").toString());
				
				String 	className 	= "";
				String  yyyymmdd	= "";
				int	 	valueCnt1	= 0;
				int	 	valueCnt2	= 0;
				
				allCount 	= dataList.size();
				
				if(CommonConstants.VEETableItem.Day.toString().equals(item)) {//day일때

					className 	= CommonConstants.MeterType.valueOf(meterType).getDayClassName();
					yyyymmdd	= "yyyymmdd";
					valueCnt1	= 0; 
					valueCnt2	= 23; 
			
				}
				else if(CommonConstants.VEETableItem.LoadProfile.toString().equals(item)) {
					className 	= CommonConstants.MeterType.valueOf(meterType).getLpClassName();
					yyyymmdd	= "yyyymmddhh";
					valueCnt1	= 0;
					valueCnt2	= 59;
				}else if(CommonConstants.VEETableItem.Month.toString().equals(item)){
					className 	= CommonConstants.MeterType.valueOf(meterType).getMonthClassName();
					yyyymmdd	= "yyyymm";
					valueCnt1	= 1;
					valueCnt2	= 31;
				}
				
				// 조회 조건에 해당하는 자료들 중 임계치 설정에 해당하는 자료 가져오기.
				if(compareList.size() > 0){

					Map<String, Object> compareMap = compareList.get(0);

					StringBuffer filterSB = new StringBuffer();

					filterSB.append(" SELECT t.id.").append(yyyymmdd).append(" as yyyymmdd, t.id.channel as channel, t.id.mdevType as mdevType, t.id.mdevId as mdevId, t.id.dst as dst, ");

					//value_00, value_01, value_02.....  
					getQuery(valueCnt1, valueCnt2, filterSB, className, yyyymmdd, startDate, endDate);

                    // 임계치값 검색(Out Of Threshold)
                    if (keyValue.equals("OutOfThreshold")) {
                        filterSB.append(" AND ( t.value_00 > m.usageThreshold or t.value_01 > m.usageThreshold or ")
                                .append("t.value_02 > m.usageThreshold or t.value_03 > m.usageThreshold or t.value_04 > m.usageThreshold or ")
                                .append("t.value_05 > m.usageThreshold or t.value_06 > m.usageThreshold or t.value_07 > m.usageThreshold or ")
                                .append("t.value_08 > m.usageThreshold or t.value_09 > m.usageThreshold or t.value_10 > m.usageThreshold or ")
                                .append("t.value_11 > m.usageThreshold or t.value_12 > m.usageThreshold or t.value_13 > m.usageThreshold or ")
                                .append("t.value_14 > m.usageThreshold or t.value_15 > m.usageThreshold or t.value_16 > m.usageThreshold or ")
                                .append("t.value_17 > m.usageThreshold or t.value_18 > m.usageThreshold or t.value_19 > m.usageThreshold or ")
                                .append("t.value_20 > m.usageThreshold or t.value_21 > m.usageThreshold or t.value_22 > m.usageThreshold or ")
                                .append("t.value_23 > m.usageThreshold ) ");
                    }

                    if (!CommonConstants.VEETableItem.LoadProfile.toString().equals(item)) {
                        // VEE_PARAMETER의 THRESHOLD_XX 에 대한 조건
                        if (!"".equals(condition1) && !"".equals(conValue1) && ("".equals(condition2) || "".equals(conValue2))) {
                            filterSB.append(" AND t.total").append(" ").append(condition1).append(conValue1 + " ");
                        } else if (!"".equals(condition2) && !"".equals(conValue2)
                                && ("".equals(condition1) || "".equals(conValue1))) {
                            filterSB.append(" AND t.total").append(" ").append(condition2).append(conValue2 + " ");
                        } else if (!"".equals(condition1) && !"".equals(conValue1) && !"".equals(condition2)
                                || !"".equals(conValue2)) {
                            filterSB.append(" AND t.total ").append(condition1).append(conValue1 + " ").append(" AND t.total ")
                                    .append(condition2).append(conValue2 + " ");
                        }
                    }

					List<Map<String, Object>> filterList = meteringDayDao.getDayVEEList(filterSB.toString());

					dataCount	= filterList.size(); 
				}else{
					dataCount	= 0; 
				}
				
			}else {
				
				HashMap<String, Object> querys = makeConditionQuery(hm, meterType);
				logger.debug(querys.get("compareQry").toString());
				logger.debug(querys.get("dataQry").toString());
				
				List<Map<String, Object>> compareList = meteringDayDao.getDayVEEList(querys.get("compareQry").toString());
				List<Map<String, Object>> dataList = meteringDayDao.getDayVEEList(querys.get("dataQry").toString());
				
				dataCount 	= compareList.size();
				allCount	= dataList.size();
			}
			
			VEEMiniData vd = new VEEMiniData();
			
			vd.setCount(dataCount.toString());
			vd.setAllCount(allCount);
			vd.setCnt_allCnt(dataCount + "/" + allCount);
			vd.setItem((String)hm.get("keyName"));
			
			boolean add	= true;
			
			//result에 같은 keyName이 존재한다면 합친다.
			for(int m=0; m<result.size(); m++){
				VEEMiniData vmd = result.get(m);
				
				if(vd.getItem().equals(vmd.getItem())){
					vmd.setCount(dfMd.format (Double.parseDouble(vd.getCount()) + Double.parseDouble(vmd.getCount())));
					vmd.setAllCount(vd.getAllCount() + vmd.getAllCount());
					vmd.setCnt_allCnt(dfMd.format(Double.parseDouble(vmd.getCount())) + "/" + dfMd.format(vmd.getAllCount()));
					
					add = false;
					break;
				}
			}
			
			if(add){
				result.add(vd);
			}
		}
		return result;
	}
	
    /*
     * condition 쿼리 생성하기
     */
    private HashMap<String, Object> makeConditionQuery(HashMap<String, Object> hm, String meterType) {

        String startDate        = StringUtil.nullToBlank(hm.get("startDate"));
        String endDate          = StringUtil.nullToBlank(hm.get("endDate"));
        String item             = StringUtil.nullToBlank(hm.get("item"));

        String condition        = StringUtil.nullToBlank(hm.get("condition"));

        String  className   = "";
        String  yyyymmdd    = "";
        int     valueCnt1   = 0;
        int     valueCnt2   = 0;

        StringBuffer curSB = new StringBuffer();    //조회조건(jsp날짜)에 해당하는 자료.
        StringBuffer conSB = new StringBuffer();    //condition 설정에 해당하는 자료.

        if (CommonConstants.VEETableItem.Meter.toString().equals(item)) {
//            curSB.append(" SELECT mdsId, lastReadDate, id as meterId, contract.id as contractId FROM ").append(item)
//                .append("\n WHERE meterType.name = '").append(meterType).append("' ")
//                .append("\n AND writeDate between '")
//                .append(startDate)
//                .append("' AND '")
//                .append(endDate).append("235959")
//                .append("'");
//
//            conSB.append(" SELECT mdsId, lastReadDate, id as meterId,  contract.id as contractId  FROM ").append(item)
//                .append("\n WHERE meterType.name = '").append(meterType).append("' ")
//                .append("\n AND writeDate between '")
//                .append(startDate)
//                .append("' AND '")
//                .append(endDate).append("235959")
//                .append("'");
//
//            if (!"".equals(condition)) {
//                conSB.append(" AND (").append(condition).append(")");
//            }
//
//            //jsp 조회 조건
//            if (!"".equals(StringUtil.nullToBlank(hm.get("contractNo")))) {
//                conSB.append(" AND contract.id = ").append(hm.get("contractNo")).append(" ");
//            }
//
//            if (!"".equals(StringUtil.nullToBlank(hm.get("meterId")))) {
//                conSB.append(" AND mdsId = '").append(hm.get("meterId")).append("' ");
//            }

            String deleteCode = MeterCodes.DELETE_STATUS.getCode();
            curSB.append("\nSELECT m.mdsId, m.lastReadDate, m.id AS meterId, c.id AS contractId, c.contractNumber AS contractNo ");
            curSB.append("\nFROM Contract c RIGHT OUTER JOIN c.meter m ");
            curSB.append("\n     LEFT OUTER JOIN m.meterStatus s ");
            curSB.append("\nWHERE m.meterType.name = '").append(meterType).append("' ");
            curSB.append("\nAND   m.writeDate between '").append(startDate).append("000000' AND '");
            curSB.append(endDate).append("235959' ");
            curSB.append("\nAND   (s.id IS NULL ");
            curSB.append("\n    OR s.code != '").append(deleteCode).append("' ");
            curSB.append("\n    OR (s.code = '").append(deleteCode).append("' ");
            curSB.append("\n    AND m.deleteDate > '").append(startDate).append("235959')) ");

            conSB.append("\nSELECT m.mdsId, m.lastReadDate, m.id AS meterId, c.id AS contractId, c.contractNumber AS contractNo ");
            conSB.append("\nFROM Contract c RIGHT OUTER JOIN c.meter m ");
            conSB.append("\n     LEFT OUTER JOIN m.meterStatus s ");
            conSB.append("\nWHERE m.meterType.name = '").append(meterType).append("' ");
            conSB.append("\nAND   m.writeDate BETWEEN '").append(startDate).append("000000' AND '");
            conSB.append(endDate).append("235959' ");
            conSB.append("\nAND   (s.id IS NULL ");
            conSB.append("\n    OR s.code != '").append(deleteCode).append("' ");
            conSB.append("\n    OR (s.code = '").append(deleteCode).append("' ");
            conSB.append("\n    AND m.deleteDate > '").append(startDate).append("235959')) ");

            // TODO - 보완예정
            if (!"".equals(condition)) {
                conSB.append("\nAND   (m.").append(condition).append(")");
            }

            // jsp 조회 조건
            if (!"".equals(StringUtil.nullToBlank(hm.get("contractNo")))) {
                conSB.append("\nAND   c.contractNumber = '").append(hm.get("contractNo")).append("' ");
            }

            if (!"".equals(StringUtil.nullToBlank(hm.get("meterId")))) {
                conSB.append("\nAND   m.mdsId = '").append(hm.get("meterId")).append("' ");
            }

        } else if (CommonConstants.VEETableItem.EventAlertLog.toString().equals(item)) {

            curSB.append(" SELECT id as eventAlertLogId, openTime, activatorId FROM ").append(item).append(" WHERE ")
                .append("\n writeTime between '")
                .append(startDate).append("000000' AND '")
                .append(endDate).append("235959' ");

            conSB.append(" SELECT id as eventAlertLogId, openTime, activatorId FROM ").append(item).append(" WHERE ")
                .append("\n writeTime between '")
                .append(startDate).append("000000' AND '")
                .append(endDate).append("235959' ");

            if (!"".equals(condition)) {
                conSB.append(" AND (").append(condition).append(")");
            }

            //jsp 조회 조건
            if (!"".equals(StringUtil.nullToBlank(hm.get("contractNo")))) {

                //계약 아이디로 Meter의 비지니스키를 찾아서 activatorId 에 해당하는 자료를 찾는다.
                Meter m = null;
                List<Object> cList = contractDao.getContractIdByContractNo(hm.get("contractNo").toString());

                if (cList != null && cList.size() > 0) {
                    Contract c = (Contract)cList.get(0);
                    if (c.getMeter() != null) {
                        m = meterDao.get(c.getMeterId());
                    }
                }

                if (m != null) {
                    conSB.append(" AND activatorId = '").append(m.getMdsId()).append("' ");
                    conSB.append(" AND activatorType = '").append(meterType).append("' ");
                } else {
                    conSB.append(" AND 1!=1");  // 일치하지 않으면 자료 조회 X.
                }
            }

            /*
             *  eventAlertLog의 activatorType가 미터이면
                activatorId가 미터의 비지니스키가 된다.
                activatorId와 meter의 mdsId가 일치하는 자료를 조회.
             */
            if (!"".equals(StringUtil.nullToBlank(hm.get("meterId")))) {
                conSB.append(" AND activatorId = '").append(hm.get("meterId")).append("' ");
                conSB.append(" AND activatorType = '").append(meterType).append("' ");
            }

        } else {
            if (CommonConstants.VEETableItem.Day.toString().equals(item)) {
                className   = CommonConstants.MeterType.valueOf(meterType).getDayClassName();
                yyyymmdd    = "yyyymmdd";
                valueCnt1   = 0;
                valueCnt2   = 23;

            } else if (CommonConstants.VEETableItem.Month.toString().equals(item)) {
                className   = CommonConstants.MeterType.valueOf(meterType).getMonthClassName();
                yyyymmdd    = "yyyymm";
                valueCnt1   = 1;
                valueCnt2   = 31;

                startDate   = startDate.substring(0, 6);
                endDate     = endDate.substring(0, 6);
            } else if (CommonConstants.VEETableItem.LoadProfile.toString().equals(item)) {
                className   = CommonConstants.MeterType.valueOf(meterType).getLpClassName();
                yyyymmdd    = "yyyymmddhh";
                valueCnt1   = 0;
                valueCnt2   = 59;

                startDate   = startDate + "00";
                endDate     = endDate + "23";
            }

            //조회조건(jsp날짜)에 데이터
            curSB.append(" SELECT t.id.").append(yyyymmdd).append(" as yyyymmdd, t.id.channel as channel, t.id.mdevType as mdevType, t.id.mdevId as mdevId, t.id.dst as dst, c.contractNumber as contractNo, ");

            getQuery(valueCnt1, valueCnt2, curSB, className, yyyymmdd, startDate, endDate);

            //condition 조건 데이터
            conSB.append(" SELECT t.id.").append(yyyymmdd).append(" as yyyymmdd, t.id.channel as channel, t.id.mdevType as mdevType, t.id.mdevId as mdevId, t.id.dst as dst, c.contractNumber as contractNo, ");

            getQuery(valueCnt1, valueCnt2, conSB, className, yyyymmdd, startDate, endDate);

            if (!"".equals(condition)) {
                conSB.append(" AND (").append(condition).append(")");
            }

            //jsp 조회 조건
            if (!"".equals(StringUtil.nullToBlank(hm.get("contractNo")))) {
                conSB.append(" AND c.contractNumber = '").append(hm.get("contractNo")).append("' ");
            }

            if (!"".equals(StringUtil.nullToBlank(hm.get("meterId")))) {
                conSB.append(" AND m.mdsId = '").append(hm.get("meterId")).append("' ");
            }

        }

        hm.put("dataQry", curSB.toString());
        hm.put("compareQry", conSB.toString());
        return hm;
    }

	/*
	 * 임계치 쿼리 생성하기
	 */
	@SuppressWarnings("unused")
    private HashMap<String, Object> makeThresholdPeriodAndThresholdItemQuery(HashMap<String, Object> hm, String meterType) {
		String thresholdPeriod 		=  StringUtil.nullToBlank(hm.get("thresholdPeriod"));
		String thresholdItem 		= StringUtil.nullToBlank(hm.get("thresholdItem"));
		
		String startDate 			= StringUtil.nullToBlank(hm.get("startDate"));
		String endDate 				= StringUtil.nullToBlank(hm.get("endDate"));
		String[] beforeDate 		= getSearchBeforeDate(startDate, endDate, thresholdPeriod);
		String beforeStdDate 		= beforeDate[0];
		String beforeEndDate 		= beforeDate[1];
		String item 				= StringUtil.nullToBlank(hm.get("item"));
		
		StringBuffer curSB = new StringBuffer();	//조회조건(jsp날짜)에 해당하는 자료.
		StringBuffer preSB = new StringBuffer();	//임계치 설정에 해당하는 자료.
		
		String 	className 	= "";
		String  yyyymmdd	= "";
		int	 	valueCnt1	= 0;
		int	 	valueCnt2	= 0;
		
		if(CommonConstants.VEETableItem.Day.toString().equals(item)){
			className 	= CommonConstants.MeterType.valueOf(meterType).getDayClassName();
			yyyymmdd	= "yyyymmdd";
			valueCnt1	= 0; 			
			valueCnt2	= 23; 			
			
		}else if(CommonConstants.VEETableItem.Month.toString().equals(item)){
			className 	= CommonConstants.MeterType.valueOf(meterType).getMonthClassName();
			yyyymmdd	= "yyyymm";
			valueCnt1	= 1; 		
			valueCnt2	= 31; 		
			
			startDate	= startDate.substring(0, 6);
			endDate		= endDate.substring(0, 6);
			beforeStdDate	= beforeStdDate.substring(0, 6);
			beforeEndDate	= beforeEndDate.substring(0, 6);
		}else if(CommonConstants.VEETableItem.LoadProfile.toString().equals(item)){
			className 	= CommonConstants.MeterType.valueOf(meterType).getLpClassName();
			yyyymmdd	= "yyyymmddhh";
			valueCnt1	= 0; 
			valueCnt2	= 59; 
			
			startDate	= startDate + "00";
			endDate		= endDate + "23";
			beforeStdDate	= beforeStdDate + "00";
			beforeEndDate	= beforeEndDate + "23";
		}
		
		//조회조건(jsp날짜)에 데이터
		curSB.append(" SELECT t.id.").append(yyyymmdd).append(" as yyyymmdd, t.id.channel as channel, t.id.mdevType as mdevType, t.id.mdevId as mdevId, t.id.dst as dst, ");

		getQuery(valueCnt1, valueCnt2, curSB, className, yyyymmdd, startDate, endDate);
		
		//임계치 쿼리				
		preSB.append(" SELECT ");
		
		getQuery(valueCnt1, valueCnt2, preSB, className, yyyymmdd, beforeStdDate, beforeEndDate);
		
		hm.put("dataQry", curSB.toString());
		hm.put("compareQry", preSB.toString());
		return hm;
	}
	
	// 조회 조건 생성을 위해 prameter 추출
	private List<HashMap<String, Object>> makeParametersConditions(String[] values, String value) {
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String,Object>>();
		String meterType = values[0];//meterType
		Code meterCode = CommonConstants.getMeterTypeByName(meterType);

		List<VEEParameter> dataList = new ArrayList<VEEParameter>();
		Set<Condition> set = new HashSet<Condition>();

		if (value.length() > 0) {
			set.add(new Condition("parameter",new Object[]{CommonConstants.VEEParam.valueOf(value)},null,Restriction.EQ));
		}

		dataList = vEEParameterDao.getVEEParameterByListCondition(set);

		if (dataList.size() > 0) {
			Iterator<VEEParameter> it = dataList.iterator();
			while (it.hasNext()) {
				VEEParameter vp = (VEEParameter) it.next();
				
				HashMap<String, Object> hm = new HashMap<String, Object>();
				
				hm.put("keyValue", vp.getParameter());//추출되는 값의 키
				hm.put("keyName", vp.getLocalName());//가젯에 표시될 항목명, 추출되는 값의 키
				hm.put("meterTypeCode", meterCode);// meter조회시 필수값
				
				hm.put("tableName", vp.getItem().name());// 테이블name
				hm.put("condition", vp.getCondition());// 테이블name
				hm.put("item", vp.getItem().toString());

				boolean useYn = vp.getUseThreshold();// 임계값 존재여부
				hm.put("useYn", useYn+"");

				if (useYn) {

					String thresholdPeriod = vp.getThresholdPeriod()==null?null:vp.getThresholdPeriod().name();
					String thresholdItem = vp.getThresholdItem()==null?null:vp.getThresholdItem().name();

					if (thresholdPeriod != null && thresholdPeriod.length() > 0 && thresholdItem != null && thresholdItem.length() > 0) {
						hm.put("thresholdPeriod", thresholdPeriod);
						hm.put("thresholdItem", thresholdItem);
					}

					Integer conValue1 = vp.getThreshold1();
					String condition1 = vp.getThresholdCondition1();

					if (conValue1 != null && condition1 != null && condition1.length() > 0) {
						hm.put("conValue1", conValue1);
						hm.put("condition1", condition1);
					}

					Integer conValue2 = vp.getThreshold2();
					String condition2 = vp.getThresholdCondition2();

					if (conValue2 != null && condition2 != null && condition2.length() > 0) {
						hm.put("conValue2", conValue2);
						hm.put("condition2", condition2);
					}
				}
				result.add(hm);
			}
		}

		return result;
	}

	public List<Object> getMaxVEEHistoryManager(String[] values, String page, String limit) {
		
		int supplierId = Integer.parseInt(values[13]);
		Supplier supplier = supplierDao.get(supplierId);
		
		List<Object> result = new ArrayList<Object>();
		HashMap<String,Object> conditions =makeMaxSearchCondition(values);

		int ipage = Integer.parseInt(page);
		int ipageSize = Integer.parseInt(limit);
		int istartRow = (ipage - 1) * ipageSize;
		
		List<Operator> operatorList = operatorDao.getAll();
		Map<String, String> opNameMap = new HashMap<String, String>();
		Map<String, String> opIdMap = new HashMap<String, String>();
		for(Operator op : operatorList) {
			opNameMap.put(String.valueOf(op.getId()), op.getLoginId());
			opIdMap.put(op.getLoginId(),String.valueOf(op.getId()));
		}
		
		Set<Condition> set = new HashSet<Condition>();
		
		if(StringUtil.nullToBlank(conditions.get("tableName")).length() > 0) {
			set.add(new Condition("tableName",new Object[]{conditions.get("tableName")},null,Restriction.EQ));
        }
		if(StringUtil.nullToBlank(conditions.get("contractNo")).length() > 0) {
			set.add(new Condition("contract",new Object[]{"cont"},null,Restriction.ALIAS));
        	set.add(new Condition("cont.contractNumber",new Object[]{conditions.get("contractNo")},null,Restriction.EQ));
        }
        if(StringUtil.nullToBlank(conditions.get("userId")).length() > 0 ) {
        	String strOperator = "-1";
        	if(StringUtil.nullToBlank(opIdMap.get(conditions.get("userId"))).length() > 0){
        		strOperator = opIdMap.get(conditions.get("userId"));
        	}
        	set.add(new Condition("operator",new Object[]{strOperator},null,Restriction.EQ));
        }
        
        if(StringUtil.nullToBlank(conditions.get("editItem")).length() > 0) {
        	set.add(new Condition("editItem",new Object[]{CommonConstants.EditItem.values()[new Integer(conditions.get("editItem").toString())]},null,Restriction.EQ));
        }

//        if(StringUtil.nullToBlank(startRow).length() > 0) {
        	set.add(new Condition("first",new Object[]{istartRow},null,Restriction.FIRST));
//        }

//        if(StringUtil.nullToBlank(pageSize).length() > 0) {
        	set.add(new Condition("max",new Object[]{ipageSize},null,Restriction.MAX));
//        }

        set.add(new Condition("writeDate",new Object[]{},null,Restriction.ORDERBYDESC));
		
		set.add(new Condition("yyyymmdd", new Object[] { conditions.get("startDate"), conditions.get("endDate") }, null, Restriction.BETWEEN));
		
		List<VEELog> dataList = vEELogDao.getVEELogByListCondition(set, istartRow, ipageSize);
		
		//DecimalFormat df = new DecimalFormat(veeDecimalFormat);
		
		int dataSize = dataList.size();
		//System.out.println("~~~~~~~~~~~~~~~~~~~~> size : " + dataSize);
		if(dataSize > 0) {

			DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

			Iterator<VEELog> it = dataList.iterator();
			while (it.hasNext()) {
				VEELog ojb = (VEELog) it.next();
				VEEHistoryData vhd = new VEEHistoryData();
				vhd.setCol1(TimeLocaleUtil.getLocaleDate(ojb.getWriteDate() , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));//시각
				vhd.setCol2((ojb.getContract()==null?"":ojb.getContract().getContractNumber()));//계약번호
				vhd.setCol3("");//미터번호 -- vee_log table의 meter_id column 삭제됨. 2010-08-12
				vhd.setCol4(ojb.getSupplier()==null?"":ojb.getSupplier().getAddress());//소비지역
				Integer idx = ojb.getEditItem().getCode();
				String item = CommonConstants.EditItem.values()[idx].toString();
				vhd.setCol5(item);//데이터유형
				

				if((ojb.getAttrName().startsWith("value_")|| 
						ojb.getAttrName().startsWith("Value_")) 
						&& (ojb.getTableName().startsWith("Lp") || ojb.getTableName().startsWith("LoadProfile") || ojb.getTableName().startsWith("LP") )){
					String attr = ojb.getAttrName().replaceAll("value_", "")+" [mm]";
					attr = ojb.getAttrName().replaceAll("value_", "")+" [mm]";
					vhd.setCol6(attr);//속성
				}
				else if(ojb.getAttrName().startsWith("value_") && ojb.getTableName().startsWith("Day")){
					String attr = ojb.getAttrName().replaceAll("value_", "")+" [hh]";
					vhd.setCol6(attr);//속성
				}
				else if(ojb.getAttrName().startsWith("value_") && ojb.getTableName().startsWith("Month")){
					String attr = ojb.getAttrName().replaceAll("value_", "")+" [dd]";
					vhd.setCol6(attr);//속성
				}
				else{
					vhd.setCol6(ojb.getAttrName());//속성
				}

				if(StringUtil.nullToBlank(ojb.getAfterValue()).length() > 0) {
					vhd.setCol7(mdf.format(new Double(ojb.getAfterValue())));//이후값
				} else {
					vhd.setCol7(ojb.getAfterValue());//이후값
				}
				if(StringUtil.nullToBlank(ojb.getBeforeValue()).length() > 0) {
					vhd.setCol8(mdf.format(new Double(ojb.getBeforeValue())));//이전값
				} else {
					vhd.setCol8(ojb.getBeforeValue());//이전값
				}
				vhd.setCol9(TimeLocaleUtil.getLocaleDate(ojb.getYyyymmdd() , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()) + " " +  Integer.parseInt(ojb.getHh()));//검침일자
				vhd.setCol10(opNameMap.get(ojb.getOperator()));//사용자
				vhd.setCol11(StringUtil.nullToBlank(ojb.getId()));//ID
				vhd.setCol12(StringUtil.nullToBlank(ojb.getContract()));//A.CONTRACT_ID
				vhd.setCol13(StringUtil.nullToBlank(ojb.getMDevType()));//A.MDEV_TYPE
				vhd.setCol14(ojb.getMDevId());//A.MDEV_ID
				vhd.setCol15(StringUtil.nullToBlank(ojb.getDst()));//A.DST
				vhd.setCol16(StringUtil.nullToBlank(ojb.getResult()));//A.RESULT
				vhd.setCol17(StringUtil.nullToBlank(ojb.getYyyymmdd()));//A.YYYYMMDD
				vhd.setCol18(StringUtil.nullToBlank(ojb.getOperatorType()));//A.OPERATOR_TYPE
				vhd.setCol19(ojb.getSupplier()==null?"":ojb.getSupplier().getId().toString());//D.ID ==> SUPPLIER_id
				vhd.setCol20(ojb.getLocation()==null?"":ojb.getLocation().getId().toString());//A.LOCATION_ID
				vhd.setCol21(StringUtil.nullToBlank(ojb.getChannel()));//A.CHANNEL
				vhd.setCol22(ojb.getTableName());//A.TABLE_NAME
				vhd.setCol23(StringUtil.nullToBlank(ojb.getYyyymmdd()) + String.format("%02d", Integer.parseInt(ojb.getHh())));
				vhd.setCol24(StringUtil.nullToBlank(String.format("%02d", Integer.parseInt(ojb.getHh()))));
				
				result.add(vhd);
			}
		}
		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public Map<String,String> getMaxVEEHistoryManagerTotal(String[] values) {
		
		Map result = new HashMap();
        result.put("total","0");
        
		HashMap<String,Object> conditions =makeMaxSearchCondition(values);
		
		//List<Object> dataList = vEELogDao.getVeeLogByDataList(conditions);
		
		
		List<Operator> operatorList = operatorDao.getAll();
		Map<String, String> opIdMap = new HashMap<String, String>();
		for(Operator op : operatorList) {
			opIdMap.put(op.getLoginId(),String.valueOf(op.getId()));
		}
		
		Set<Condition> set = new HashSet<Condition>();
		
		if(StringUtil.nullToBlank(conditions.get("tableName")).length() > 0) {
			set.add(new Condition("tableName",new Object[]{conditions.get("tableName")},null,Restriction.EQ));
        }
		if(StringUtil.nullToBlank(conditions.get("contractNo")).length() > 0) {
			set.add(new Condition("contract",new Object[]{"cont"},null,Restriction.ALIAS));
        	set.add(new Condition("cont.contractNumber",new Object[]{conditions.get("contractNo")},null,Restriction.EQ));
        }
        if(StringUtil.nullToBlank(conditions.get("userId")).length() > 0) {
        	String strOperator = "-1";
        	if(StringUtil.nullToBlank(opIdMap.get(conditions.get("userId"))).length() > 0){
        		strOperator = opIdMap.get(conditions.get("userId"));
        	}
        	set.add(new Condition("operator",new Object[]{strOperator},null,Restriction.EQ));
        }
        if(StringUtil.nullToBlank(conditions.get("editItem")).length() > 0) {
        	set.add(new Condition("editItem",new Object[]{CommonConstants.EditItem.values()[new Integer(conditions.get("editItem").toString())]},null,Restriction.EQ));
        }
		
		set.add(new Condition("yyyymmdd", new Object[] { conditions.get("startDate"), conditions.get("endDate") }, null, Restriction.BETWEEN));
		
		List<VEELog> dataList = vEELogDao.getVEELogByListCondition(set);
		
		result.put("total", dataList.size());
		
		return result;
	}
	
	// VEEParameters의 mini는 제외함
	public List<VEEParameterData> getMaxVEEParametersManager(String values) {
		List<VEEParameterData> result = new ArrayList<VEEParameterData>();
		List<Object> dataList = new ArrayList<Object>();
		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("ruleType", values);
		dataList = vEEParameterDao.getParameterDataList(hm);
		if(dataList.size() > 0) {
			Iterator<Object> it = dataList.iterator();
			while (it.hasNext()) {
				Object[] ojb = (Object[]) it.next();
				VEEParameterData veeData = new VEEParameterData();
				veeData.setChked(false);
				veeData.setLocalName((String)ojb[0]);
				veeData.setItem((String)ojb[1]);
				
				Short useThreshold = Short.valueOf(ojb[2].toString());
				if(useThreshold == 1) {
					veeData.setUseThreshold("Yes");
				}
				else {
					veeData.setUseThreshold("No");
				}
				veeData.setCondition((String)ojb[3]);
				
				result.add(veeData);
			}
		}
		
		return result;
	}
	
	
	// 미터타입과 테이블 타입을 가지고 실제 테이블을 조회함
	private String getTableName(String meterType, String tableType) {
		String result = "";
		if("Meter".equals(tableType)) {
			result =CommonConstants.MeterType.valueOf(meterType).getMeteringClassName();
		}
		else if("LoadProfile".equals(tableType)) {
			result =CommonConstants.MeterType.valueOf(meterType).getLpClassName();
		}
		else if("Day".equals(tableType)) {
			result =CommonConstants.MeterType.valueOf(meterType).getDayClassName();
		}
		else if("Month".equals(tableType)) {
			result =CommonConstants.MeterType.valueOf(meterType).getMonthClassName();
		}else if("EventAlertLog".equals(tableType)) {
			result = tableType;
		}
		return result;
	}
	
	// Mini가젯에서 사용되는 조회조건을 생성한다.
	// 공통 : 시작/종료일자
	// parameters :  meterType, tabType, dateType, startDate, EndDate, table_name
	private HashMap<String,Object> makeMiniSearchCondition(String[] values){
		HashMap<String,Object> result = new HashMap<String,Object>();
		String meterType = values[0];
		String dateTypeIdx = values[2];
		String startDate = StringUtil.nullToBlank(values[3]);
		String endDate = StringUtil.nullToBlank(values[4]);
		String tableType = values[5];
		String tableName = getTableName(meterType, tableType);

		if(DateType.DAILY.getCode().equals(dateTypeIdx)){
			if("".equals(startDate)){
				startDate = CalendarUtil.getCurrentDate();
				endDate = CalendarUtil.getCurrentDate();
			}
		}
		
		result.put("meterType", meterType);
		result.put("startDate", startDate);
		result.put("endDate", endDate);
		result.put("tableName", tableName);
		
		return result;
	}
	
	// Max가젯에서 사용되는 조회조건을 생성한다.
	// 공통 : 시작/종료일자
	// history (계약번호, 미터아이디, 사용자아이디, ?)
	// validate Check (?, 장비아이디, 계약번호)
	// parameters :  meterType, tabType, dateType, startDate, EndDate, table_name
	private HashMap<String,Object> makeMaxSearchCondition(String[] values){
		HashMap<String,Object> result = new HashMap<String,Object>();
		String dateTypeIdx = values[2];
		String startDate = values[3].substring(0, 8);
		String endDate = values[4].substring(0, 8);
		String meterType = values[0];
		String tableType = values[5];
		String tableName = getTableName(meterType, tableType); //values[5];
		String contractNo = values[6];
		String meterId = values[7];
		String userId = values[8];
		String editItem = values[14];
		
		
		String dateType = CommonConstants.DateType.values()[Integer.parseInt(dateTypeIdx)].toString();
		if("MONTHLY".equals(dateType)) {
			startDate = values[3].substring(0, 6)+"01";
			endDate = values[4].substring(0, 6)+"31";
		}
		else if("YEARLY".equals(dateType)) {
			startDate = values[3].substring(0, 4)+"0101";
			endDate = values[4].substring(0, 4)+"2131";
		}
		else {
			startDate = values[3].substring(0, 8);
			endDate = values[4].substring(0, 8);
		}
		result.put("meterType", meterType);
		result.put("startDate", startDate);
		result.put("endDate", endDate);
		result.put("tableName", tableName);
		result.put("contractNo", contractNo);
		result.put("mdsId", meterId);
		result.put("userId", userId);
		result.put("editItem", editItem);
		
		return result;
	}
	
	// 조회값을 각 sub 서비스로 넘기기 위한 HashMap
	public HashMap<String, Object> makeSearchHashMap(String[] values, String type){
		HashMap<String, Object> resultHm = new HashMap<String, Object>();
		//계약번호, 미터아이디, 사용자아이디
		return resultHm;
	}
	/*
	 * 전일, 전월, 전년도의 시작일과 종료일을 구함
	 * div(LastDay:전일, LastMonth:전월, LastYear:전년)
	 */
	public String[] getSearchBeforeDate(String sDate, String eDate, String div) {
		String startDate	= "";
		String endDate		= "";
		
		String[] result		= new String[2];
		
		if("LastDay".equals(div))  {
			startDate 	= CalendarUtil.getDateWithoutFormat(sDate, Calendar.DATE, -1);
			endDate 	= CalendarUtil.getDateWithoutFormat(sDate, Calendar.DATE, -1);
		}
		else if("LastMonth".equals(div))  {
			startDate 	= CalendarUtil.getDateMonth(CalendarUtil.getDateWithoutFormat(sDate, Calendar.MONTH, -1).substring(0,4), CalendarUtil.getDateWithoutFormat(sDate, Calendar.MONTH, -1).substring(4,6)).get("startDate");
			endDate 	= CalendarUtil.getDateMonth(CalendarUtil.getDateWithoutFormat(sDate, Calendar.MONTH, -1).substring(0,4), CalendarUtil.getDateWithoutFormat(sDate, Calendar.MONTH, -1).substring(4,6)).get("endDate");
		}
		else if("LastYear".equals(div))  {
			startDate 	= CalendarUtil.getDateWithoutFormat(sDate, Calendar.YEAR, -1).substring(0,4) + "0101";
			endDate 	= CalendarUtil.getDateWithoutFormat(sDate, Calendar.YEAR, -1).substring(0,4) + "1231";
		}else{
			startDate=sDate;
			endDate=eDate;
		}
		
		result[0] =startDate;
		result[1] =endDate;
		return result;
	}

	public void getQuery(int valueCnt1, int valueCnt2, StringBuffer sb, String className, String yyyymmdd, String startDate, String endDate){
		for(int j=valueCnt1; j<=valueCnt2; j++){
			sb.append(" t.value_").append(String.format("%02d", j));
			
			if(j < valueCnt2){
				sb.append(" , ");
			}
		}

		String deleteCode = MeterCodes.DELETE_STATUS.getCode();

        sb.append(" FROM ").append(className).append(" as t, ");
        sb.append(" Contract c RIGHT OUTER JOIN c.meter as m ");
        sb.append(" LEFT OUTER JOIN m.meterStatus s ");
        sb.append(" WHERE t.id.mdevId = m.mdsId ");
        sb.append(" AND t.id.channel = 1 ");
        sb.append(" AND t.id.").append(yyyymmdd).append(" between '");
        sb.append(startDate).append("' AND '");
        sb.append(endDate).append("' ");
        sb.append(" AND (s.id IS NULL ");
        sb.append("   OR s.code != '").append(deleteCode).append("' ");
        sb.append("   OR (s.code = '").append(deleteCode).append("' AND m.deleteDate > '");
        sb.append(startDate).append("235959')) ");
	}

	//lp 데이터를 가져온다.
	@SuppressWarnings("unused")
    public Map<String, Object>  getLpData(String meterType, String item, String yyyymmdd, String channel, String mdevType, String mdevId, String dst, String supplierId){
				
		String lpClassName = "";
		
		List<Object> lpList = new ArrayList<Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		List<LpEM> lpemList = null;
		
		int lpInterval = 60;
		
		if(mdevId == null || "".equals(mdevId)){
			resultMap.put("lpList", null);
			return resultMap;
		}
		
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		String mdFormat = supplier.getMd().getPattern().replace("#", "0");
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
		
		if(CommonConstants.VEETableItem.Day.toString().equals(item) || CommonConstants.VEETableItem.Month.toString().equals(item) || CommonConstants.VEETableItem.LoadProfile.toString().equals(item)){
			lpClassName 	= CommonConstants.MeterType.valueOf(meterType).getLpClassName();
			
			Set<Condition> set = new HashSet<Condition>();

			if(CommonConstants.VEETableItem.Month.toString().equals(item)){
				if(!"".equals(yyyymmdd)){
					set.add(new Condition("yyyymmdd",new Object[]{yyyymmdd},null,Restriction.LIKE));
				}
			}else if(CommonConstants.VEETableItem.Day.toString().equals(item)){
				if(!"".equals(yyyymmdd)){
					set.add(new Condition("yyyymmdd",new Object[]{yyyymmdd},null,Restriction.EQ));
				}
			}else if(CommonConstants.VEETableItem.LoadProfile.toString().equals(item)){
				if(!"".equals(yyyymmdd)){
					set.add(new Condition("id.yyyymmddhh",new Object[]{yyyymmdd},null,Restriction.EQ));
				}
			}
			
			
			if(!"".equals(channel)){
				set.add(new Condition("id.channel",new Object[]{Integer.parseInt(channel)},null,Restriction.EQ));
			}
			
			if(!"".equals(mdevType)){
				set.add(new Condition("id.mdevType",new Object[]{CommonConstants.DeviceType.valueOf(mdevType)},null,Restriction.EQ));
			}
			
			if(!"".equals(mdevId)){
				set.add(new Condition("id.mdevId",new Object[]{mdevId},null,Restriction.EQ));
			}
			
			if(!"".equals(dst)){
				set.add(new Condition("id.dst",new Object[]{Integer.parseInt(dst)},null,Restriction.EQ));
			}
			
			set.add(new Condition("hour", new Object[]{}, null, Restriction.ORDERBY));
			
			
			if(CommonConstants.MeterType.EnergyMeter.getLpClassName().equals(lpClassName)){
				List<LpEM> emList = lpEMDao.getLpEMsByListCondition(set);
				try {
					lpInterval = lpEMDao.getLpInterval(mdevId);
				} catch(Exception e) {
//					e.printStackTrace();
				}
				VEEMaxDetailData vmdd = null;
				
				for(int i=0; i<emList.size(); i++){
					vmdd = new VEEMaxDetailData();
					
					vmdd.setYyyymmddhh(TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(((LpEM)emList.get(i)).getYyyymmdd()) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()) + " " + StringUtil.nullToBlank(((LpEM)emList.get(i)).getHour()));
					vmdd.setChannel(StringUtil.nullToBlank(((LpEM)emList.get(i)).getChannel()));
					vmdd.setMdev_type(StringUtil.nullToBlank(((LpEM)emList.get(i)).getId().getMDevType()));
					vmdd.setMdev_id(StringUtil.nullToBlank(((LpEM)emList.get(i)).getId().getMDevId()));
					vmdd.setDst(StringUtil.nullToBlank(((LpEM)emList.get(i)).getId().getDst()));
					vmdd.setHh(StringUtil.nullToBlank(((LpEM)emList.get(i)).getHour()));
					vmdd.setYyyymmdd(StringUtil.nullToBlank(((LpEM)emList.get(i)).getYyyymmdd()));
						
					if(((LpEM)emList.get(i)).getValue_00() != null) vmdd.setValue_00(df.format(((LpEM)emList.get(i)).getValue_00()));
					else vmdd.setValue_00("");
					if(((LpEM)emList.get(i)).getValue_01() != null) vmdd.setValue_01(df.format(((LpEM)emList.get(i)).getValue_01()));
					else vmdd.setValue_01("");
					if(((LpEM)emList.get(i)).getValue_02() != null) vmdd.setValue_02(df.format(((LpEM)emList.get(i)).getValue_02()));
					else vmdd.setValue_02("");
					if(((LpEM)emList.get(i)).getValue_03() != null) vmdd.setValue_03(df.format(((LpEM)emList.get(i)).getValue_03()));
					else vmdd.setValue_03("");
					if(((LpEM)emList.get(i)).getValue_04() != null) vmdd.setValue_04(df.format(((LpEM)emList.get(i)).getValue_04()));
					else vmdd.setValue_04("");
					if(((LpEM)emList.get(i)).getValue_05() != null) vmdd.setValue_05(df.format(((LpEM)emList.get(i)).getValue_05()));
					else vmdd.setValue_05("");
					if(((LpEM)emList.get(i)).getValue_06() != null) vmdd.setValue_06(df.format(((LpEM)emList.get(i)).getValue_06()));
					else vmdd.setValue_06("");
					if(((LpEM)emList.get(i)).getValue_07() != null) vmdd.setValue_07(df.format(((LpEM)emList.get(i)).getValue_07()));
					else vmdd.setValue_07("");
					if(((LpEM)emList.get(i)).getValue_08() != null) vmdd.setValue_08(df.format(((LpEM)emList.get(i)).getValue_08()));
					else vmdd.setValue_08("");
					if(((LpEM)emList.get(i)).getValue_09() != null) vmdd.setValue_09(df.format(((LpEM)emList.get(i)).getValue_09()));
					else vmdd.setValue_09("");
										
					if(((LpEM)emList.get(i)).getValue_10() != null) vmdd.setValue_10(df.format(((LpEM)emList.get(i)).getValue_10()));
					else vmdd.setValue_10("");
					if(((LpEM)emList.get(i)).getValue_11() != null) vmdd.setValue_11(df.format(((LpEM)emList.get(i)).getValue_11()));
					else vmdd.setValue_11("");
					if(((LpEM)emList.get(i)).getValue_12() != null) vmdd.setValue_12(df.format(((LpEM)emList.get(i)).getValue_12()));
					else vmdd.setValue_12("");
					if(((LpEM)emList.get(i)).getValue_13() != null) vmdd.setValue_13(df.format(((LpEM)emList.get(i)).getValue_13()));
					else vmdd.setValue_13("");
					if(((LpEM)emList.get(i)).getValue_14() != null) vmdd.setValue_14(df.format(((LpEM)emList.get(i)).getValue_14()));
					else vmdd.setValue_14("");
					if(((LpEM)emList.get(i)).getValue_15() != null) vmdd.setValue_15(df.format(((LpEM)emList.get(i)).getValue_15()));
					else vmdd.setValue_15("");
					if(((LpEM)emList.get(i)).getValue_16() != null) vmdd.setValue_16(df.format(((LpEM)emList.get(i)).getValue_16()));
					else vmdd.setValue_16("");
					if(((LpEM)emList.get(i)).getValue_17() != null) vmdd.setValue_17(df.format(((LpEM)emList.get(i)).getValue_17()));
					else vmdd.setValue_17("");
					if(((LpEM)emList.get(i)).getValue_18() != null) vmdd.setValue_18(df.format(((LpEM)emList.get(i)).getValue_18()));
					else vmdd.setValue_18("");
					if(((LpEM)emList.get(i)).getValue_19() != null) vmdd.setValue_19(df.format(((LpEM)emList.get(i)).getValue_19()));
					else vmdd.setValue_19("");
					
					if(((LpEM)emList.get(i)).getValue_20() != null) vmdd.setValue_20(df.format(((LpEM)emList.get(i)).getValue_20()));
					else vmdd.setValue_20("");
					if(((LpEM)emList.get(i)).getValue_21() != null) vmdd.setValue_21(df.format(((LpEM)emList.get(i)).getValue_21()));
					else vmdd.setValue_21("");
					if(((LpEM)emList.get(i)).getValue_22() != null) vmdd.setValue_22(df.format(((LpEM)emList.get(i)).getValue_22()));
					else vmdd.setValue_22("");
					if(((LpEM)emList.get(i)).getValue_23() != null) vmdd.setValue_23(df.format(((LpEM)emList.get(i)).getValue_23()));
					else vmdd.setValue_23("");
					if(((LpEM)emList.get(i)).getValue_24() != null) vmdd.setValue_24(df.format(((LpEM)emList.get(i)).getValue_24()));
					else vmdd.setValue_24("");
					if(((LpEM)emList.get(i)).getValue_25() != null) vmdd.setValue_25(df.format(((LpEM)emList.get(i)).getValue_25()));
					else vmdd.setValue_25("");
					if(((LpEM)emList.get(i)).getValue_26() != null) vmdd.setValue_26(df.format(((LpEM)emList.get(i)).getValue_26()));
					else vmdd.setValue_26("");
					if(((LpEM)emList.get(i)).getValue_27() != null) vmdd.setValue_27(df.format(((LpEM)emList.get(i)).getValue_27()));
					else vmdd.setValue_27("");
					if(((LpEM)emList.get(i)).getValue_28() != null) vmdd.setValue_28(df.format(((LpEM)emList.get(i)).getValue_28()));
					else vmdd.setValue_28("");
					if(((LpEM)emList.get(i)).getValue_29() != null) vmdd.setValue_29(df.format(((LpEM)emList.get(i)).getValue_29()));
					else vmdd.setValue_29("");
					
					if(((LpEM)emList.get(i)).getValue_30() != null) vmdd.setValue_30(df.format(((LpEM)emList.get(i)).getValue_30()));
					else vmdd.setValue_30("");
					if(((LpEM)emList.get(i)).getValue_31() != null) vmdd.setValue_31(df.format(((LpEM)emList.get(i)).getValue_31()));
					else vmdd.setValue_31("");
					if(((LpEM)emList.get(i)).getValue_32() != null) vmdd.setValue_32(df.format(((LpEM)emList.get(i)).getValue_32()));
					else vmdd.setValue_32("");
					if(((LpEM)emList.get(i)).getValue_33() != null) vmdd.setValue_33(df.format(((LpEM)emList.get(i)).getValue_33()));
					else vmdd.setValue_33("");
					if(((LpEM)emList.get(i)).getValue_34() != null) vmdd.setValue_34(df.format(((LpEM)emList.get(i)).getValue_34()));
					else vmdd.setValue_34("");
					if(((LpEM)emList.get(i)).getValue_35() != null) vmdd.setValue_35(df.format(((LpEM)emList.get(i)).getValue_35()));
					else vmdd.setValue_35("");
					if(((LpEM)emList.get(i)).getValue_36() != null) vmdd.setValue_36(df.format(((LpEM)emList.get(i)).getValue_36()));
					else vmdd.setValue_36("");
					if(((LpEM)emList.get(i)).getValue_37() != null) vmdd.setValue_37(df.format(((LpEM)emList.get(i)).getValue_37()));
					else vmdd.setValue_37("");
					if(((LpEM)emList.get(i)).getValue_38() != null) vmdd.setValue_38(df.format(((LpEM)emList.get(i)).getValue_38()));
					else vmdd.setValue_38("");
					if(((LpEM)emList.get(i)).getValue_39() != null) vmdd.setValue_39(df.format(((LpEM)emList.get(i)).getValue_39()));
					else vmdd.setValue_39("");
					
					if(((LpEM)emList.get(i)).getValue_40() != null) vmdd.setValue_40(df.format(((LpEM)emList.get(i)).getValue_40()));
					else vmdd.setValue_40("");
					if(((LpEM)emList.get(i)).getValue_41() != null) vmdd.setValue_41(df.format(((LpEM)emList.get(i)).getValue_41()));
					else vmdd.setValue_41("");
					if(((LpEM)emList.get(i)).getValue_42() != null) vmdd.setValue_42(df.format(((LpEM)emList.get(i)).getValue_42()));
					else vmdd.setValue_42("");
					if(((LpEM)emList.get(i)).getValue_43() != null) vmdd.setValue_43(df.format(((LpEM)emList.get(i)).getValue_43()));
					else vmdd.setValue_43("");
					if(((LpEM)emList.get(i)).getValue_44() != null) vmdd.setValue_44(df.format(((LpEM)emList.get(i)).getValue_44()));
					else vmdd.setValue_44("");
					if(((LpEM)emList.get(i)).getValue_45() != null) vmdd.setValue_45(df.format(((LpEM)emList.get(i)).getValue_45()));
					else vmdd.setValue_45("");
					if(((LpEM)emList.get(i)).getValue_46() != null) vmdd.setValue_46(df.format(((LpEM)emList.get(i)).getValue_46()));
					else vmdd.setValue_46("");
					if(((LpEM)emList.get(i)).getValue_47() != null) vmdd.setValue_47(df.format(((LpEM)emList.get(i)).getValue_47()));
					else vmdd.setValue_47("");
					if(((LpEM)emList.get(i)).getValue_48() != null) vmdd.setValue_48(df.format(((LpEM)emList.get(i)).getValue_48()));
					else vmdd.setValue_48("");
					if(((LpEM)emList.get(i)).getValue_49() != null) vmdd.setValue_49(df.format(((LpEM)emList.get(i)).getValue_49()));
					else vmdd.setValue_49("");
					
					if(((LpEM)emList.get(i)).getValue_50() != null) vmdd.setValue_50(df.format(((LpEM)emList.get(i)).getValue_50()));
					else vmdd.setValue_50("");
					if(((LpEM)emList.get(i)).getValue_51() != null) vmdd.setValue_51(df.format(((LpEM)emList.get(i)).getValue_51()));
					else vmdd.setValue_51("");
					if(((LpEM)emList.get(i)).getValue_52() != null) vmdd.setValue_52(df.format(((LpEM)emList.get(i)).getValue_52()));
					else vmdd.setValue_52("");
					if(((LpEM)emList.get(i)).getValue_53() != null) vmdd.setValue_53(df.format(((LpEM)emList.get(i)).getValue_53()));
					else vmdd.setValue_53("");
					if(((LpEM)emList.get(i)).getValue_54() != null) vmdd.setValue_54(df.format(((LpEM)emList.get(i)).getValue_54()));
					else vmdd.setValue_54("");
					if(((LpEM)emList.get(i)).getValue_55() != null) vmdd.setValue_55(df.format(((LpEM)emList.get(i)).getValue_55()));
					else vmdd.setValue_55("");
					if(((LpEM)emList.get(i)).getValue_56() != null) vmdd.setValue_56(df.format(((LpEM)emList.get(i)).getValue_56()));
					else vmdd.setValue_56("");
					if(((LpEM)emList.get(i)).getValue_57() != null) vmdd.setValue_57(df.format(((LpEM)emList.get(i)).getValue_57()));
					else vmdd.setValue_57("");
					if(((LpEM)emList.get(i)).getValue_58() != null) vmdd.setValue_58(df.format(((LpEM)emList.get(i)).getValue_58()));
					else vmdd.setValue_58("");
					if(((LpEM)emList.get(i)).getValue_59() != null) vmdd.setValue_59(df.format(((LpEM)emList.get(i)).getValue_59()));
					else vmdd.setValue_59("");
					
					vmdd.setRealData("Y");
					
					lpList.add(vmdd);
				}
				
			}else if(CommonConstants.MeterType.GasMeter.getLpClassName().equals(lpClassName)){
				List<LpGM> gmList = lpGMDao.getLpGMsByListCondition(set);
				try {
					lpInterval = lpGMDao.getLpInterval(mdevId);
				} catch(Exception e) {
//					e.printStackTrace();
				}
				
				VEEMaxDetailData vmdd = null;
				for(int i=0; i<gmList.size(); i++){
					
					vmdd = new VEEMaxDetailData();
					
					vmdd.setYyyymmddhh(TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(((LpGM)gmList.get(i)).getYyyymmdd()) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()) + " " + StringUtil.nullToBlank(((LpGM)gmList.get(i)).getHour()));
					vmdd.setChannel(StringUtil.nullToBlank(((LpGM)gmList.get(i)).getChannel()));
					vmdd.setMdev_type(StringUtil.nullToBlank(((LpGM)gmList.get(i)).getId().getMDevType()));
					vmdd.setMdev_id(StringUtil.nullToBlank(((LpGM)gmList.get(i)).getId().getMDevId()));
					vmdd.setDst(StringUtil.nullToBlank(((LpGM)gmList.get(i)).getId().getDst()));
					vmdd.setHh(StringUtil.nullToBlank(((LpGM)gmList.get(i)).getHour()));
					vmdd.setYyyymmdd(StringUtil.nullToBlank(((LpGM)gmList.get(i)).getYyyymmdd()));
	
					if(((LpGM)gmList.get(i)).getValue_00() != null) vmdd.setValue_00(df.format(((LpGM)gmList.get(i)).getValue_00()));
					else vmdd.setValue_00("");
					if(((LpGM)gmList.get(i)).getValue_01() != null) vmdd.setValue_01(df.format(((LpGM)gmList.get(i)).getValue_01()));
					else vmdd.setValue_01("");
					if(((LpGM)gmList.get(i)).getValue_02() != null) vmdd.setValue_02(df.format(((LpGM)gmList.get(i)).getValue_02()));
					else vmdd.setValue_02("");
					if(((LpGM)gmList.get(i)).getValue_03() != null) vmdd.setValue_03(df.format(((LpGM)gmList.get(i)).getValue_03()));
					else vmdd.setValue_03("");
					if(((LpGM)gmList.get(i)).getValue_04() != null) vmdd.setValue_04(df.format(((LpGM)gmList.get(i)).getValue_04()));
					else vmdd.setValue_04("");
					if(((LpGM)gmList.get(i)).getValue_05() != null) vmdd.setValue_05(df.format(((LpGM)gmList.get(i)).getValue_05()));
					else vmdd.setValue_05("");
					if(((LpGM)gmList.get(i)).getValue_06() != null) vmdd.setValue_06(df.format(((LpGM)gmList.get(i)).getValue_06()));
					else vmdd.setValue_06("");
					if(((LpGM)gmList.get(i)).getValue_07() != null) vmdd.setValue_07(df.format(((LpGM)gmList.get(i)).getValue_07()));
					else vmdd.setValue_07("");
					if(((LpGM)gmList.get(i)).getValue_08() != null) vmdd.setValue_08(df.format(((LpGM)gmList.get(i)).getValue_08()));
					else vmdd.setValue_08("");
					if(((LpGM)gmList.get(i)).getValue_09() != null) vmdd.setValue_09(df.format(((LpGM)gmList.get(i)).getValue_09()));
					else vmdd.setValue_09("");
										
					if(((LpGM)gmList.get(i)).getValue_10() != null) vmdd.setValue_10(df.format(((LpGM)gmList.get(i)).getValue_10()));
					else vmdd.setValue_10("");
					if(((LpGM)gmList.get(i)).getValue_11() != null) vmdd.setValue_11(df.format(((LpGM)gmList.get(i)).getValue_11()));
					else vmdd.setValue_11("");
					if(((LpGM)gmList.get(i)).getValue_12() != null) vmdd.setValue_12(df.format(((LpGM)gmList.get(i)).getValue_12()));
					else vmdd.setValue_12("");
					if(((LpGM)gmList.get(i)).getValue_13() != null) vmdd.setValue_13(df.format(((LpGM)gmList.get(i)).getValue_13()));
					else vmdd.setValue_13("");
					if(((LpGM)gmList.get(i)).getValue_14() != null) vmdd.setValue_14(df.format(((LpGM)gmList.get(i)).getValue_14()));
					else vmdd.setValue_14("");
					if(((LpGM)gmList.get(i)).getValue_15() != null) vmdd.setValue_15(df.format(((LpGM)gmList.get(i)).getValue_15()));
					else vmdd.setValue_15("");
					if(((LpGM)gmList.get(i)).getValue_16() != null) vmdd.setValue_16(df.format(((LpGM)gmList.get(i)).getValue_16()));
					else vmdd.setValue_16("");
					if(((LpGM)gmList.get(i)).getValue_17() != null) vmdd.setValue_17(df.format(((LpGM)gmList.get(i)).getValue_17()));
					else vmdd.setValue_17("");
					if(((LpGM)gmList.get(i)).getValue_18() != null) vmdd.setValue_18(df.format(((LpGM)gmList.get(i)).getValue_18()));
					else vmdd.setValue_18("");
					if(((LpGM)gmList.get(i)).getValue_19() != null) vmdd.setValue_19(df.format(((LpGM)gmList.get(i)).getValue_19()));
					else vmdd.setValue_19("");
					
					if(((LpGM)gmList.get(i)).getValue_20() != null) vmdd.setValue_20(df.format(((LpGM)gmList.get(i)).getValue_20()));
					else vmdd.setValue_20("");
					if(((LpGM)gmList.get(i)).getValue_21() != null) vmdd.setValue_21(df.format(((LpGM)gmList.get(i)).getValue_21()));
					else vmdd.setValue_21("");
					if(((LpGM)gmList.get(i)).getValue_22() != null) vmdd.setValue_22(df.format(((LpGM)gmList.get(i)).getValue_22()));
					else vmdd.setValue_22("");
					if(((LpGM)gmList.get(i)).getValue_23() != null) vmdd.setValue_23(df.format(((LpGM)gmList.get(i)).getValue_23()));
					else vmdd.setValue_23("");
					if(((LpGM)gmList.get(i)).getValue_24() != null) vmdd.setValue_24(df.format(((LpGM)gmList.get(i)).getValue_24()));
					else vmdd.setValue_24("");
					if(((LpGM)gmList.get(i)).getValue_25() != null) vmdd.setValue_25(df.format(((LpGM)gmList.get(i)).getValue_25()));
					else vmdd.setValue_25("");
					if(((LpGM)gmList.get(i)).getValue_26() != null) vmdd.setValue_26(df.format(((LpGM)gmList.get(i)).getValue_26()));
					else vmdd.setValue_26("");
					if(((LpGM)gmList.get(i)).getValue_27() != null) vmdd.setValue_27(df.format(((LpGM)gmList.get(i)).getValue_27()));
					else vmdd.setValue_27("");
					if(((LpGM)gmList.get(i)).getValue_28() != null) vmdd.setValue_28(df.format(((LpGM)gmList.get(i)).getValue_28()));
					else vmdd.setValue_28("");
					if(((LpGM)gmList.get(i)).getValue_29() != null) vmdd.setValue_29(df.format(((LpGM)gmList.get(i)).getValue_29()));
					else vmdd.setValue_29("");
					
					if(((LpGM)gmList.get(i)).getValue_30() != null) vmdd.setValue_30(df.format(((LpGM)gmList.get(i)).getValue_30()));
					else vmdd.setValue_30("");
					if(((LpGM)gmList.get(i)).getValue_31() != null) vmdd.setValue_31(df.format(((LpGM)gmList.get(i)).getValue_31()));
					else vmdd.setValue_31("");
					if(((LpGM)gmList.get(i)).getValue_32() != null) vmdd.setValue_32(df.format(((LpGM)gmList.get(i)).getValue_32()));
					else vmdd.setValue_32("");
					if(((LpGM)gmList.get(i)).getValue_33() != null) vmdd.setValue_33(df.format(((LpGM)gmList.get(i)).getValue_33()));
					else vmdd.setValue_33("");
					if(((LpGM)gmList.get(i)).getValue_34() != null) vmdd.setValue_34(df.format(((LpGM)gmList.get(i)).getValue_34()));
					else vmdd.setValue_34("");
					if(((LpGM)gmList.get(i)).getValue_35() != null) vmdd.setValue_35(df.format(((LpGM)gmList.get(i)).getValue_35()));
					else vmdd.setValue_35("");
					if(((LpGM)gmList.get(i)).getValue_36() != null) vmdd.setValue_36(df.format(((LpGM)gmList.get(i)).getValue_36()));
					else vmdd.setValue_36("");
					if(((LpGM)gmList.get(i)).getValue_37() != null) vmdd.setValue_37(df.format(((LpGM)gmList.get(i)).getValue_37()));
					else vmdd.setValue_37("");
					if(((LpGM)gmList.get(i)).getValue_38() != null) vmdd.setValue_38(df.format(((LpGM)gmList.get(i)).getValue_38()));
					else vmdd.setValue_38("");
					if(((LpGM)gmList.get(i)).getValue_39() != null) vmdd.setValue_39(df.format(((LpGM)gmList.get(i)).getValue_39()));
					else vmdd.setValue_39("");
					
					if(((LpGM)gmList.get(i)).getValue_40() != null) vmdd.setValue_40(df.format(((LpGM)gmList.get(i)).getValue_40()));
					else vmdd.setValue_40("");
					if(((LpGM)gmList.get(i)).getValue_41() != null) vmdd.setValue_41(df.format(((LpGM)gmList.get(i)).getValue_41()));
					else vmdd.setValue_41("");
					if(((LpGM)gmList.get(i)).getValue_42() != null) vmdd.setValue_42(df.format(((LpGM)gmList.get(i)).getValue_42()));
					else vmdd.setValue_42("");
					if(((LpGM)gmList.get(i)).getValue_43() != null) vmdd.setValue_43(df.format(((LpGM)gmList.get(i)).getValue_43()));
					else vmdd.setValue_43("");
					if(((LpGM)gmList.get(i)).getValue_44() != null) vmdd.setValue_44(df.format(((LpGM)gmList.get(i)).getValue_44()));
					else vmdd.setValue_44("");
					if(((LpGM)gmList.get(i)).getValue_45() != null) vmdd.setValue_45(df.format(((LpGM)gmList.get(i)).getValue_45()));
					else vmdd.setValue_45("");
					if(((LpGM)gmList.get(i)).getValue_46() != null) vmdd.setValue_46(df.format(((LpGM)gmList.get(i)).getValue_46()));
					else vmdd.setValue_46("");
					if(((LpGM)gmList.get(i)).getValue_47() != null) vmdd.setValue_47(df.format(((LpGM)gmList.get(i)).getValue_47()));
					else vmdd.setValue_47("");
					if(((LpGM)gmList.get(i)).getValue_48() != null) vmdd.setValue_48(df.format(((LpGM)gmList.get(i)).getValue_48()));
					else vmdd.setValue_48("");
					if(((LpGM)gmList.get(i)).getValue_49() != null) vmdd.setValue_49(df.format(((LpGM)gmList.get(i)).getValue_49()));
					else vmdd.setValue_49("");
					
					if(((LpGM)gmList.get(i)).getValue_50() != null) vmdd.setValue_50(df.format(((LpGM)gmList.get(i)).getValue_50()));
					else vmdd.setValue_50("");
					if(((LpGM)gmList.get(i)).getValue_51() != null) vmdd.setValue_51(df.format(((LpGM)gmList.get(i)).getValue_51()));
					else vmdd.setValue_51("");
					if(((LpGM)gmList.get(i)).getValue_52() != null) vmdd.setValue_52(df.format(((LpGM)gmList.get(i)).getValue_52()));
					else vmdd.setValue_52("");
					if(((LpGM)gmList.get(i)).getValue_53() != null) vmdd.setValue_53(df.format(((LpGM)gmList.get(i)).getValue_53()));
					else vmdd.setValue_53("");
					if(((LpGM)gmList.get(i)).getValue_54() != null) vmdd.setValue_54(df.format(((LpGM)gmList.get(i)).getValue_54()));
					else vmdd.setValue_54("");
					if(((LpGM)gmList.get(i)).getValue_55() != null) vmdd.setValue_55(df.format(((LpGM)gmList.get(i)).getValue_55()));
					else vmdd.setValue_55("");
					if(((LpGM)gmList.get(i)).getValue_56() != null) vmdd.setValue_56(df.format(((LpGM)gmList.get(i)).getValue_56()));
					else vmdd.setValue_56("");
					if(((LpGM)gmList.get(i)).getValue_57() != null) vmdd.setValue_57(df.format(((LpGM)gmList.get(i)).getValue_57()));
					else vmdd.setValue_57("");
					if(((LpGM)gmList.get(i)).getValue_58() != null) vmdd.setValue_58(df.format(((LpGM)gmList.get(i)).getValue_58()));
					else vmdd.setValue_58("");
					if(((LpGM)gmList.get(i)).getValue_59() != null) vmdd.setValue_59(df.format(((LpGM)gmList.get(i)).getValue_59()));
					else vmdd.setValue_59("");
					
					vmdd.setRealData("Y");
					
					lpList.add(vmdd);
				}
				
			}else if(CommonConstants.MeterType.WaterMeter.getLpClassName().equals(lpClassName)){
				List<LpWM> wmList = lpWMDao.getLpWMsByListCondition(set);
				try {
					lpInterval = lpWMDao.getLpInterval(mdevId);
				} catch(Exception e) {
//					e.printStackTrace();
				}
				
				VEEMaxDetailData vmdd = null;
				for(int i=0; i<wmList.size(); i++){
					
					vmdd = new VEEMaxDetailData();
					
					vmdd.setYyyymmddhh(TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(((LpWM)wmList.get(i)).getYyyymmdd()) , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()) + " " + StringUtil.nullToBlank(((LpWM)wmList.get(i)).getHour()));
					vmdd.setChannel(StringUtil.nullToBlank(((LpWM)wmList.get(i)).getChannel()));
					vmdd.setMdev_type(StringUtil.nullToBlank(((LpWM)wmList.get(i)).getId().getMDevType()));
					vmdd.setMdev_id(StringUtil.nullToBlank(((LpWM)wmList.get(i)).getId().getMDevId()));
					vmdd.setDst(StringUtil.nullToBlank(((LpWM)wmList.get(i)).getId().getDst()));
					vmdd.setHh(StringUtil.nullToBlank(((LpWM)wmList.get(i)).getHour()));
					vmdd.setYyyymmdd(StringUtil.nullToBlank(((LpWM)wmList.get(i)).getYyyymmdd()));
					
					if(((LpWM)wmList.get(i)).getValue_00() != null) vmdd.setValue_00(df.format(((LpWM)wmList.get(i)).getValue_00()));
					else vmdd.setValue_00("");
					if(((LpWM)wmList.get(i)).getValue_01() != null) vmdd.setValue_01(df.format(((LpWM)wmList.get(i)).getValue_01()));
					else vmdd.setValue_01("");
					if(((LpWM)wmList.get(i)).getValue_02() != null) vmdd.setValue_02(df.format(((LpWM)wmList.get(i)).getValue_02()));
					else vmdd.setValue_02("");
					if(((LpWM)wmList.get(i)).getValue_03() != null) vmdd.setValue_03(df.format(((LpWM)wmList.get(i)).getValue_03()));
					else vmdd.setValue_03("");
					if(((LpWM)wmList.get(i)).getValue_04() != null) vmdd.setValue_04(df.format(((LpWM)wmList.get(i)).getValue_04()));
					else vmdd.setValue_04("");
					if(((LpWM)wmList.get(i)).getValue_05() != null) vmdd.setValue_05(df.format(((LpWM)wmList.get(i)).getValue_05()));
					else vmdd.setValue_05("");
					if(((LpWM)wmList.get(i)).getValue_06() != null) vmdd.setValue_06(df.format(((LpWM)wmList.get(i)).getValue_06()));
					else vmdd.setValue_06("");
					if(((LpWM)wmList.get(i)).getValue_07() != null) vmdd.setValue_07(df.format(((LpWM)wmList.get(i)).getValue_07()));
					else vmdd.setValue_07("");
					if(((LpWM)wmList.get(i)).getValue_08() != null) vmdd.setValue_08(df.format(((LpWM)wmList.get(i)).getValue_08()));
					else vmdd.setValue_08("");
					if(((LpWM)wmList.get(i)).getValue_09() != null) vmdd.setValue_09(df.format(((LpWM)wmList.get(i)).getValue_09()));
					else vmdd.setValue_09("");
										
					if(((LpWM)wmList.get(i)).getValue_10() != null) vmdd.setValue_10(df.format(((LpWM)wmList.get(i)).getValue_10()));
					else vmdd.setValue_10("");
					if(((LpWM)wmList.get(i)).getValue_11() != null) vmdd.setValue_11(df.format(((LpWM)wmList.get(i)).getValue_11()));
					else vmdd.setValue_11("");
					if(((LpWM)wmList.get(i)).getValue_12() != null) vmdd.setValue_12(df.format(((LpWM)wmList.get(i)).getValue_12()));
					else vmdd.setValue_12("");
					if(((LpWM)wmList.get(i)).getValue_13() != null) vmdd.setValue_13(df.format(((LpWM)wmList.get(i)).getValue_13()));
					else vmdd.setValue_13("");
					if(((LpWM)wmList.get(i)).getValue_14() != null) vmdd.setValue_14(df.format(((LpWM)wmList.get(i)).getValue_14()));
					else vmdd.setValue_14("");
					if(((LpWM)wmList.get(i)).getValue_15() != null) vmdd.setValue_15(df.format(((LpWM)wmList.get(i)).getValue_15()));
					else vmdd.setValue_15("");
					if(((LpWM)wmList.get(i)).getValue_16() != null) vmdd.setValue_16(df.format(((LpWM)wmList.get(i)).getValue_16()));
					else vmdd.setValue_16("");
					if(((LpWM)wmList.get(i)).getValue_17() != null) vmdd.setValue_17(df.format(((LpWM)wmList.get(i)).getValue_17()));
					else vmdd.setValue_17("");
					if(((LpWM)wmList.get(i)).getValue_18() != null) vmdd.setValue_18(df.format(((LpWM)wmList.get(i)).getValue_18()));
					else vmdd.setValue_18("");
					if(((LpWM)wmList.get(i)).getValue_19() != null) vmdd.setValue_19(df.format(((LpWM)wmList.get(i)).getValue_19()));
					else vmdd.setValue_19("");
					
					if(((LpWM)wmList.get(i)).getValue_20() != null) vmdd.setValue_20(df.format(((LpWM)wmList.get(i)).getValue_20()));
					else vmdd.setValue_20("");
					if(((LpWM)wmList.get(i)).getValue_21() != null) vmdd.setValue_21(df.format(((LpWM)wmList.get(i)).getValue_21()));
					else vmdd.setValue_21("");
					if(((LpWM)wmList.get(i)).getValue_22() != null) vmdd.setValue_22(df.format(((LpWM)wmList.get(i)).getValue_22()));
					else vmdd.setValue_22("");
					if(((LpWM)wmList.get(i)).getValue_23() != null) vmdd.setValue_23(df.format(((LpWM)wmList.get(i)).getValue_23()));
					else vmdd.setValue_23("");
					if(((LpWM)wmList.get(i)).getValue_24() != null) vmdd.setValue_24(df.format(((LpWM)wmList.get(i)).getValue_24()));
					else vmdd.setValue_24("");
					if(((LpWM)wmList.get(i)).getValue_25() != null) vmdd.setValue_25(df.format(((LpWM)wmList.get(i)).getValue_25()));
					else vmdd.setValue_25("");
					if(((LpWM)wmList.get(i)).getValue_26() != null) vmdd.setValue_26(df.format(((LpWM)wmList.get(i)).getValue_26()));
					else vmdd.setValue_26("");
					if(((LpWM)wmList.get(i)).getValue_27() != null) vmdd.setValue_27(df.format(((LpWM)wmList.get(i)).getValue_27()));
					else vmdd.setValue_27("");
					if(((LpWM)wmList.get(i)).getValue_28() != null) vmdd.setValue_28(df.format(((LpWM)wmList.get(i)).getValue_28()));
					else vmdd.setValue_28("");
					if(((LpWM)wmList.get(i)).getValue_29() != null) vmdd.setValue_29(df.format(((LpWM)wmList.get(i)).getValue_29()));
					else vmdd.setValue_29("");
					
					if(((LpWM)wmList.get(i)).getValue_30() != null) vmdd.setValue_30(df.format(((LpWM)wmList.get(i)).getValue_30()));
					else vmdd.setValue_30("");
					if(((LpWM)wmList.get(i)).getValue_31() != null) vmdd.setValue_31(df.format(((LpWM)wmList.get(i)).getValue_31()));
					else vmdd.setValue_31("");
					if(((LpWM)wmList.get(i)).getValue_32() != null) vmdd.setValue_32(df.format(((LpWM)wmList.get(i)).getValue_32()));
					else vmdd.setValue_32("");
					if(((LpWM)wmList.get(i)).getValue_33() != null) vmdd.setValue_33(df.format(((LpWM)wmList.get(i)).getValue_33()));
					else vmdd.setValue_33("");
					if(((LpWM)wmList.get(i)).getValue_34() != null) vmdd.setValue_34(df.format(((LpWM)wmList.get(i)).getValue_34()));
					else vmdd.setValue_34("");
					if(((LpWM)wmList.get(i)).getValue_35() != null) vmdd.setValue_35(df.format(((LpWM)wmList.get(i)).getValue_35()));
					else vmdd.setValue_35("");
					if(((LpWM)wmList.get(i)).getValue_36() != null) vmdd.setValue_36(df.format(((LpWM)wmList.get(i)).getValue_36()));
					else vmdd.setValue_36("");
					if(((LpWM)wmList.get(i)).getValue_37() != null) vmdd.setValue_37(df.format(((LpWM)wmList.get(i)).getValue_37()));
					else vmdd.setValue_37("");
					if(((LpWM)wmList.get(i)).getValue_38() != null) vmdd.setValue_38(df.format(((LpWM)wmList.get(i)).getValue_38()));
					else vmdd.setValue_38("");
					if(((LpWM)wmList.get(i)).getValue_39() != null) vmdd.setValue_39(df.format(((LpWM)wmList.get(i)).getValue_39()));
					else vmdd.setValue_39("");
					
					if(((LpWM)wmList.get(i)).getValue_40() != null) vmdd.setValue_40(df.format(((LpWM)wmList.get(i)).getValue_40()));
					else vmdd.setValue_40("");
					if(((LpWM)wmList.get(i)).getValue_41() != null) vmdd.setValue_41(df.format(((LpWM)wmList.get(i)).getValue_41()));
					else vmdd.setValue_41("");
					if(((LpWM)wmList.get(i)).getValue_42() != null) vmdd.setValue_42(df.format(((LpWM)wmList.get(i)).getValue_42()));
					else vmdd.setValue_42("");
					if(((LpWM)wmList.get(i)).getValue_43() != null) vmdd.setValue_43(df.format(((LpWM)wmList.get(i)).getValue_43()));
					else vmdd.setValue_43("");
					if(((LpWM)wmList.get(i)).getValue_44() != null) vmdd.setValue_44(df.format(((LpWM)wmList.get(i)).getValue_44()));
					else vmdd.setValue_44("");
					if(((LpWM)wmList.get(i)).getValue_45() != null) vmdd.setValue_45(df.format(((LpWM)wmList.get(i)).getValue_45()));
					else vmdd.setValue_45("");
					if(((LpWM)wmList.get(i)).getValue_46() != null) vmdd.setValue_46(df.format(((LpWM)wmList.get(i)).getValue_46()));
					else vmdd.setValue_46("");
					if(((LpWM)wmList.get(i)).getValue_47() != null) vmdd.setValue_47(df.format(((LpWM)wmList.get(i)).getValue_47()));
					else vmdd.setValue_47("");
					if(((LpWM)wmList.get(i)).getValue_48() != null) vmdd.setValue_48(df.format(((LpWM)wmList.get(i)).getValue_48()));
					else vmdd.setValue_48("");
					if(((LpWM)wmList.get(i)).getValue_49() != null) vmdd.setValue_49(df.format(((LpWM)wmList.get(i)).getValue_49()));
					else vmdd.setValue_49("");
					
					if(((LpWM)wmList.get(i)).getValue_50() != null) vmdd.setValue_50(df.format(((LpWM)wmList.get(i)).getValue_50()));
					else vmdd.setValue_50("");
					if(((LpWM)wmList.get(i)).getValue_51() != null) vmdd.setValue_51(df.format(((LpWM)wmList.get(i)).getValue_51()));
					else vmdd.setValue_51("");
					if(((LpWM)wmList.get(i)).getValue_52() != null) vmdd.setValue_52(df.format(((LpWM)wmList.get(i)).getValue_52()));
					else vmdd.setValue_52("");
					if(((LpWM)wmList.get(i)).getValue_53() != null) vmdd.setValue_53(df.format(((LpWM)wmList.get(i)).getValue_53()));
					else vmdd.setValue_53("");
					if(((LpWM)wmList.get(i)).getValue_54() != null) vmdd.setValue_54(df.format(((LpWM)wmList.get(i)).getValue_54()));
					else vmdd.setValue_54("");
					if(((LpWM)wmList.get(i)).getValue_55() != null) vmdd.setValue_55(df.format(((LpWM)wmList.get(i)).getValue_55()));
					else vmdd.setValue_55("");
					if(((LpWM)wmList.get(i)).getValue_56() != null) vmdd.setValue_56(df.format(((LpWM)wmList.get(i)).getValue_56()));
					else vmdd.setValue_56("");
					if(((LpWM)wmList.get(i)).getValue_57() != null) vmdd.setValue_57(df.format(((LpWM)wmList.get(i)).getValue_57()));
					else vmdd.setValue_57("");
					if(((LpWM)wmList.get(i)).getValue_58() != null) vmdd.setValue_58(df.format(((LpWM)wmList.get(i)).getValue_58()));
					else vmdd.setValue_58("");
					if(((LpWM)wmList.get(i)).getValue_59() != null) vmdd.setValue_59(df.format(((LpWM)wmList.get(i)).getValue_59()));
					else vmdd.setValue_59("");
					
					vmdd.setRealData("Y");
					
					lpList.add(vmdd);
				}
				
			}else{
				//System.out.println(" >> VEEManagerImpl.java : 문제발생!!");
			}
			 
		}else if(CommonConstants.VEETableItem.EventAlertLog.toString().equals(item)){
			lpClassName 	= CommonConstants.MeterType.valueOf(meterType).getLpClassName();
			
		}else if(CommonConstants.VEETableItem.Meter.toString().equals(item)){
			lpClassName 	= CommonConstants.MeterType.valueOf(meterType).getLpClassName();
		}
		
		boolean exist = false;
		
		for(int k=0; k<24; k++){	
			exist = false;
			
			for(int p=0; p<lpList.size(); p++){
				if(String.format("%02d", k).equals(((VEEMaxDetailData)(lpList.get(p))).getHh())){
					exist = true;
					break;
				}
			}
			
			if(!exist){
				VEEMaxDetailData vmdd = new VEEMaxDetailData();
				
				vmdd.setYyyymmdd(yyyymmdd);
				vmdd.setChannel(channel);
				vmdd.setMdev_id(mdevId);
				vmdd.setMdev_type(mdevType);
				vmdd.setDst(dst);
				vmdd.setHh(String.format("%02d", k));
				vmdd.setYyyymmddhh(TimeLocaleUtil.getLocaleDate(yyyymmdd, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter())+ " " + String.format("%02d", k));
				vmdd.setRealData("N");
				
				
				if(lpList.size() > 0){
					vmdd.setContract_id(((VEEMaxDetailData)(lpList.get(0))).getContract_id());
					vmdd.setDevice_id(((VEEMaxDetailData)(lpList.get(0))).getDevice_id());
					vmdd.setDevice_type(((VEEMaxDetailData)(lpList.get(0))).getDevice_type());
					vmdd.setEnddevice_id(((VEEMaxDetailData)(lpList.get(0))).getEnddevice_id());
					vmdd.setLocation_id(((VEEMaxDetailData)(lpList.get(0))).getLocation_id());
					vmdd.setMeter_id(((VEEMaxDetailData)(lpList.get(0))).getMeter_id());
					vmdd.setMeteringtype(((VEEMaxDetailData)(lpList.get(0))).getMeteringtype());
					vmdd.setModem_id(((VEEMaxDetailData)(lpList.get(0))).getModem_id());
				}
				
				lpList.add(k, vmdd);
			}
		}
		
//		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("lpList", lpList);
		resultMap.put("lpinterval", lpInterval);
		resultMap.put("mdFormat", mdFormat);
		return resultMap;
	}
	
	
	@SuppressWarnings("unused")
    public String estimationData(String meterType, String userId, String yyyymmddhh, String yyyymmdd, String hh, String channel, String mdevType, String mdevId, String dst){
		
		userId = String.valueOf(Math.round(Double.parseDouble(userId)));
		
		String lpClassName 	= CommonConstants.MeterType.valueOf(meterType).getLpClassName();
		String msg = "";
		String table = "";
		
		if(CommonConstants.MeterType.EnergyMeter.getLpClassName().equals(lpClassName)){
			
			table = CommonConstants.MeterType.EnergyMeter.getLpClassName();
			
			LpPk lppk = new LpPk();
			
			lppk.setYyyymmddhh(yyyymmddhh);
			lppk.setChannel(Integer.parseInt(channel));
			lppk.setMDevType(mdevType);
			lppk.setMDevId(mdevId);
			lppk.setDst(Integer.parseInt(dst));
			
			//yyyymm 의 날짜와 yyyymm-1 의 날짜의 데이터를 가져옴.
			String[] list_yymmdd = {(Integer.parseInt(yyyymmdd)-1)+"", yyyymmdd};
			Set<Condition> set = new HashSet<Condition>();
			
			set.add(new Condition("yyyymmdd",list_yymmdd,null,Restriction.IN));
			set.add(new Condition("id.channel",new Object[]{lppk.getChannel()},null,Restriction.EQ));
			set.add(new Condition("id.mdevType",new Object[]{lppk.getMDevType()},null,Restriction.EQ));
			set.add(new Condition("id.mdevId",new Object[]{lppk.getMDevId()},null,Restriction.EQ));
			set.add(new Condition("id.dst",new Object[]{lppk.getDst()},null,Restriction.EQ));
			
			//System.out.println("==> getLpEMsByListCondition~~~~~~~~~~~~~~~~~~~~~~~~");
			List<LpEM> lpemList = lpEMDao.getLpEMsByListCondition(set);
			
			//System.out.println("lpemList.size : " + lpemList.size());
			if(lpemList.size() == 0){
				return msg = "하루전의 데이터가 존재하지 않습니다.";
			}
			
			LpEM curLpEM = null;
			
			//yyyymmdd -1의 데이터
			List<LpEM> preList = new ArrayList<LpEM>();
			//yyyymmdd 의 데이터
			List<LpEM> nowList = new ArrayList<LpEM>();
			
			int yesterCnt = 0;
			//int 
			
			for(int k=0; k<lpemList.size(); k++){
				curLpEM = lpemList.get(k);
				
				if((Integer.parseInt(yyyymmdd)-1) == Integer.parseInt(curLpEM.getYyyymmdd())){
					preList.add(curLpEM);
					yesterCnt ++;
				}
				
				if((Integer.parseInt(yyyymmdd)) == Integer.parseInt(curLpEM.getYyyymmdd())){
					nowList.add(curLpEM);
				}
			}
			
			boolean insert = true;
			
			for(int m=0; m<60; m++){
				
				insert = true;
				
				for(int n=0; n<nowList.size(); n++){
					if(String.format("%02d", m).equals(nowList.get(n).getHour())){
						insert = false;
						break;
					}
				}
				
				if(insert){
					for(int k=0; k<preList.size(); k++){
						if(String.format("%02d", m).equals(preList.get(k).getHour())){
							
							LpEM tmp = preList.get(k);
							
							tmp.setYyyymmdd(yyyymmdd);
							tmp.setYyyymmddhh(yyyymmddhh);
							//tmp.setSupplier(supplier);
							lpEMDao.add(tmp);
						}
					}
				}
			}
			
			if(yesterCnt == 0){
				return msg = "하루전의 데이터가 존재하지 않습니다.";
			}
			
		}
		
		return "";
	}
	
	/*	1. lp_xx table update/insert
		2. day_xx table update
		3. month_xx table update
		4. vee_log insert
	*/
	@SuppressWarnings("unused")
    public String updateLpData(String meterType, String userId, String supplierId, String yyyymmddhh, String yyyymmdd, String hh, String channel, String mdevType, String mdevId, String dst, String[] params){
		
		userId = String.valueOf(Math.round(Double.parseDouble(userId)));
		

		if(params.length > 60){
		
		}
		
		String lpClassName 	= CommonConstants.MeterType.valueOf(meterType).getLpClassName();
		String msg = "";
		String table = "";
		
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		DecimalFormat df = new DecimalFormat(supplier.getMd().getPattern());
		
		if(params.length > 60 && "N".equals(StringUtil.nullToBlank(params[60]))){// 존재하지 않는 hh 인경우 데이터 인서트.
			
			MeteringLP currLp = null;
			
			if(CommonConstants.MeterType.EnergyMeter.getLpClassName().equals(lpClassName)){
				table = CommonConstants.MeterType.EnergyMeter.getLpClassName();				
				currLp = new LpEM();
			}else if(CommonConstants.MeterType.GasMeter.getLpClassName().equals(lpClassName)){
				table = CommonConstants.MeterType.GasMeter.getLpClassName();				
				currLp = new LpGM();				
			}else if(CommonConstants.MeterType.WaterMeter.getLpClassName().equals(lpClassName)){
				table = CommonConstants.MeterType.WaterMeter.getLpClassName();				
				currLp = new LpWM();				
			}
			
			LpPk lppk = new LpPk();
			
			lppk.setYyyymmddhh(yyyymmddhh);
			lppk.setChannel(Integer.parseInt(channel));
			lppk.setMDevType(mdevType);
			lppk.setMDevId(mdevId);
			lppk.setDst(Integer.parseInt(dst));
		
			
			Set<Condition> set = new HashSet<Condition>();
			set.add(new Condition("id.channel",new Object[]{lppk.getChannel()},null,Restriction.EQ));
			set.add(new Condition("id.mdevType",new Object[]{lppk.getMDevType()},null,Restriction.EQ));
			set.add(new Condition("id.mdevId",new Object[]{lppk.getMDevId()},null,Restriction.EQ));
			set.add(new Condition("id.dst",new Object[]{lppk.getDst()},null,Restriction.EQ));			

			currLp.setId(lppk);
			currLp.setYyyymmdd(yyyymmdd);
			currLp.setHour(hh);
			currLp.setWriteDate(CalendarUtil.getCurrentDate());
			currLp.setSupplier(supplier);
			
			for(int p=0; p<params.length; p++){
				if("null".equals(StringUtil.nullToBlank(params[p]))){
					params[p] = "";
				}
			}
			
			if(!StringUtil.nullToBlank(params[0]).equals("")) currLp.setValue_00(Double.parseDouble(StringUtil.nullToBlank(params[0])));
			if(!StringUtil.nullToBlank(params[1]).equals("")) currLp.setValue_01(Double.parseDouble(StringUtil.nullToBlank(params[1])));
			if(!StringUtil.nullToBlank(params[2]).equals("")) currLp.setValue_02(Double.parseDouble(StringUtil.nullToBlank(params[2])));
			if(!StringUtil.nullToBlank(params[3]).equals("")) currLp.setValue_03(Double.parseDouble(StringUtil.nullToBlank(params[3])));
			if(!StringUtil.nullToBlank(params[4]).equals("")) currLp.setValue_04(Double.parseDouble(StringUtil.nullToBlank(params[4])));
			if(!StringUtil.nullToBlank(params[5]).equals("")) currLp.setValue_05(Double.parseDouble(StringUtil.nullToBlank(params[5])));
			if(!StringUtil.nullToBlank(params[6]).equals("")) currLp.setValue_06(Double.parseDouble(StringUtil.nullToBlank(params[6])));
			if(!StringUtil.nullToBlank(params[7]).equals("")) currLp.setValue_07(Double.parseDouble(StringUtil.nullToBlank(params[7])));
			if(!StringUtil.nullToBlank(params[8]).equals("")) currLp.setValue_08(Double.parseDouble(StringUtil.nullToBlank(params[8])));
			if(!StringUtil.nullToBlank(params[9]).equals("")) currLp.setValue_09(Double.parseDouble(StringUtil.nullToBlank(params[9])));
			if(!StringUtil.nullToBlank(params[10]).equals("")) currLp.setValue_10(Double.parseDouble(StringUtil.nullToBlank(params[10])));
			
			if(!StringUtil.nullToBlank(params[11]).equals("")) currLp.setValue_11(Double.parseDouble(StringUtil.nullToBlank(params[11])));
			if(!StringUtil.nullToBlank(params[12]).equals("")) currLp.setValue_12(Double.parseDouble(StringUtil.nullToBlank(params[12])));
			if(!StringUtil.nullToBlank(params[13]).equals("")) currLp.setValue_13(Double.parseDouble(StringUtil.nullToBlank(params[13])));
			if(!StringUtil.nullToBlank(params[14]).equals("")) currLp.setValue_14(Double.parseDouble(StringUtil.nullToBlank(params[14])));
			if(!StringUtil.nullToBlank(params[15]).equals("")) currLp.setValue_15(Double.parseDouble(StringUtil.nullToBlank(params[15])));
			if(!StringUtil.nullToBlank(params[16]).equals("")) currLp.setValue_16(Double.parseDouble(StringUtil.nullToBlank(params[16])));
			if(!StringUtil.nullToBlank(params[17]).equals("")) currLp.setValue_17(Double.parseDouble(StringUtil.nullToBlank(params[17])));
			if(!StringUtil.nullToBlank(params[18]).equals("")) currLp.setValue_18(Double.parseDouble(StringUtil.nullToBlank(params[18])));
			if(!StringUtil.nullToBlank(params[19]).equals("")) currLp.setValue_19(Double.parseDouble(StringUtil.nullToBlank(params[19])));
			if(!StringUtil.nullToBlank(params[20]).equals("")) currLp.setValue_20(Double.parseDouble(StringUtil.nullToBlank(params[20])));
			
			if(!StringUtil.nullToBlank(params[21]).equals("")) currLp.setValue_21(Double.parseDouble(StringUtil.nullToBlank(params[21])));
			if(!StringUtil.nullToBlank(params[22]).equals("")) currLp.setValue_22(Double.parseDouble(StringUtil.nullToBlank(params[22])));
			if(!StringUtil.nullToBlank(params[23]).equals("")) currLp.setValue_23(Double.parseDouble(StringUtil.nullToBlank(params[23])));
			if(!StringUtil.nullToBlank(params[24]).equals("")) currLp.setValue_24(Double.parseDouble(StringUtil.nullToBlank(params[24])));
			if(!StringUtil.nullToBlank(params[25]).equals("")) currLp.setValue_25(Double.parseDouble(StringUtil.nullToBlank(params[25])));
			if(!StringUtil.nullToBlank(params[26]).equals("")) currLp.setValue_26(Double.parseDouble(StringUtil.nullToBlank(params[26])));
			if(!StringUtil.nullToBlank(params[27]).equals("")) currLp.setValue_27(Double.parseDouble(StringUtil.nullToBlank(params[27])));
			if(!StringUtil.nullToBlank(params[28]).equals("")) currLp.setValue_28(Double.parseDouble(StringUtil.nullToBlank(params[28])));
			if(!StringUtil.nullToBlank(params[29]).equals("")) currLp.setValue_29(Double.parseDouble(StringUtil.nullToBlank(params[29])));
			if(!StringUtil.nullToBlank(params[30]).equals("")) currLp.setValue_30(Double.parseDouble(StringUtil.nullToBlank(params[30])));
			
			if(!StringUtil.nullToBlank(params[31]).equals("")) currLp.setValue_31(Double.parseDouble(StringUtil.nullToBlank(params[31])));
			if(!StringUtil.nullToBlank(params[32]).equals("")) currLp.setValue_32(Double.parseDouble(StringUtil.nullToBlank(params[32])));
			if(!StringUtil.nullToBlank(params[33]).equals("")) currLp.setValue_33(Double.parseDouble(StringUtil.nullToBlank(params[33])));
			if(!StringUtil.nullToBlank(params[34]).equals("")) currLp.setValue_34(Double.parseDouble(StringUtil.nullToBlank(params[34])));
			if(!StringUtil.nullToBlank(params[35]).equals("")) currLp.setValue_35(Double.parseDouble(StringUtil.nullToBlank(params[35])));
			if(!StringUtil.nullToBlank(params[36]).equals("")) currLp.setValue_36(Double.parseDouble(StringUtil.nullToBlank(params[36])));
			if(!StringUtil.nullToBlank(params[37]).equals("")) currLp.setValue_37(Double.parseDouble(StringUtil.nullToBlank(params[37])));
			if(!StringUtil.nullToBlank(params[38]).equals("")) currLp.setValue_38(Double.parseDouble(StringUtil.nullToBlank(params[38])));
			if(!StringUtil.nullToBlank(params[39]).equals("")) currLp.setValue_39(Double.parseDouble(StringUtil.nullToBlank(params[39])));
			if(!StringUtil.nullToBlank(params[40]).equals("")) currLp.setValue_40(Double.parseDouble(StringUtil.nullToBlank(params[40])));
			
			if(!StringUtil.nullToBlank(params[41]).equals("")) currLp.setValue_41(Double.parseDouble(StringUtil.nullToBlank(params[41])));
			if(!StringUtil.nullToBlank(params[42]).equals("")) currLp.setValue_42(Double.parseDouble(StringUtil.nullToBlank(params[42])));
			if(!StringUtil.nullToBlank(params[43]).equals("")) currLp.setValue_43(Double.parseDouble(StringUtil.nullToBlank(params[43])));
			if(!StringUtil.nullToBlank(params[44]).equals("")) currLp.setValue_44(Double.parseDouble(StringUtil.nullToBlank(params[44])));
			if(!StringUtil.nullToBlank(params[45]).equals("")) currLp.setValue_45(Double.parseDouble(StringUtil.nullToBlank(params[45])));
			if(!StringUtil.nullToBlank(params[46]).equals("")) currLp.setValue_46(Double.parseDouble(StringUtil.nullToBlank(params[46])));
			if(!StringUtil.nullToBlank(params[47]).equals("")) currLp.setValue_47(Double.parseDouble(StringUtil.nullToBlank(params[47])));
			if(!StringUtil.nullToBlank(params[48]).equals("")) currLp.setValue_48(Double.parseDouble(StringUtil.nullToBlank(params[48])));
			if(!StringUtil.nullToBlank(params[49]).equals("")) currLp.setValue_49(Double.parseDouble(StringUtil.nullToBlank(params[49])));
			if(!StringUtil.nullToBlank(params[50]).equals("")) currLp.setValue_50(Double.parseDouble(StringUtil.nullToBlank(params[50])));
			
			if(!StringUtil.nullToBlank(params[51]).equals("")) currLp.setValue_51(Double.parseDouble(StringUtil.nullToBlank(params[51])));
			if(!StringUtil.nullToBlank(params[52]).equals("")) currLp.setValue_52(Double.parseDouble(StringUtil.nullToBlank(params[52])));
			if(!StringUtil.nullToBlank(params[53]).equals("")) currLp.setValue_53(Double.parseDouble(StringUtil.nullToBlank(params[53])));
			if(!StringUtil.nullToBlank(params[54]).equals("")) currLp.setValue_54(Double.parseDouble(StringUtil.nullToBlank(params[54])));
			if(!StringUtil.nullToBlank(params[55]).equals("")) currLp.setValue_55(Double.parseDouble(StringUtil.nullToBlank(params[55])));
			if(!StringUtil.nullToBlank(params[56]).equals("")) currLp.setValue_56(Double.parseDouble(StringUtil.nullToBlank(params[56])));
			if(!StringUtil.nullToBlank(params[57]).equals("")) currLp.setValue_57(Double.parseDouble(StringUtil.nullToBlank(params[57])));
			if(!StringUtil.nullToBlank(params[58]).equals("")) currLp.setValue_58(Double.parseDouble(StringUtil.nullToBlank(params[58])));
			if(!StringUtil.nullToBlank(params[59]).equals("")) currLp.setValue_59(Double.parseDouble(StringUtil.nullToBlank(params[59])));

			
			if(currLp != null) {
				if(CommonConstants.MeterType.EnergyMeter.getLpClassName().equals(lpClassName)){				
					lpEMDao.saveOrUpdate((LpEM)currLp);
					
					for(int j=0; j<60; j++){
						if(!StringUtil.nullToBlank(params[j]).equals("")) {
							insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_" + String.format("%02d", j), "", params[j], userId, lppk, EditItem.IndividualEdited.toString());
						}				
					}
					
					/* 2. day_xx update */
					Map<String, Object> renewDayEM = dayemUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, hh, userId, currLp, EditItem.IndividualEdited.toString());
					if("wrong data !! => [hh]".equals(renewDayEM.get("returnValue"))) {
						return "wrong data !! => [hh]";
					} else {
						msg = "";
					}
					
					/*3. month_xx table update start*/
					msg = monthemUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, hh, userId, (DayEM)renewDayEM.get("returnValue"), EditItem.IndividualEdited.toString());
					
					if(!"".equals(msg)) return msg;
					
				}else if(CommonConstants.MeterType.GasMeter.getLpClassName().equals(lpClassName)){
					lpGMDao.saveOrUpdate((LpGM)currLp);
					
					for(int j=0; j<60; j++){
						if(!StringUtil.nullToBlank(params[j]).equals("")) {
							insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_" + String.format("%02d", j), "", params[j], userId, lppk, EditItem.IndividualEdited.toString());
						}				
					}
					
					/* 2. day_xx update */
					Map<String, Object> renewDayGM = daygmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, hh, userId, currLp, EditItem.IndividualEdited.toString());
					if("wrong data !! => [hh]".equals(renewDayGM.get("returnValue"))) {
						return "wrong data !! => [hh]";
					} else {
						msg = "";
					}
					
					/*3. month_xx table update start*/
					msg = monthgmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, hh, userId, (DayGM)renewDayGM.get("returnValue"), EditItem.IndividualEdited.toString());
					
					if(!"".equals(msg)) return msg;					
				}else if(CommonConstants.MeterType.WaterMeter.getLpClassName().equals(lpClassName)){
					lpWMDao.saveOrUpdate((LpWM)currLp);
					
					for(int j=0; j<60; j++){
						if(!StringUtil.nullToBlank(params[j]).equals("")) {
							insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_" + String.format("%02d", j), "", params[j], userId, lppk, EditItem.IndividualEdited.toString());
						}				
					}
					
					/* 2. day_xx update */
					Map<String, Object> renewDayWM = daywmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, hh, userId, currLp, EditItem.IndividualEdited.toString());
					if("wrong data !! => [hh]".equals(renewDayWM.get("returnValue"))) {
						return "wrong data !! => [hh]";
					} else {
						msg = "";
					}
					
					/*3. month_xx table update start*/
					msg = monthwmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, hh, userId, (DayWM)renewDayWM.get("returnValue"), EditItem.IndividualEdited.toString());
					
					if(!"".equals(msg)) return msg;					
				}
			}
		}else{
						
			LpPk lppk = new LpPk();

			lppk.setYyyymmddhh(yyyymmddhh);
			lppk.setChannel(Integer.parseInt(channel));
			lppk.setMDevType(mdevType);
			lppk.setMDevId(mdevId);
			lppk.setDst(Integer.parseInt(dst));
			
			Set<Condition> set = new HashSet<Condition>();
			set.add(new Condition("id.yyyymmddhh",new Object[]{lppk.getYyyymmddhh()},null,Restriction.EQ));
			set.add(new Condition("id.channel",new Object[]{lppk.getChannel()},null,Restriction.EQ));
			set.add(new Condition("id.mdevType",new Object[]{lppk.getMDevType()},null,Restriction.EQ));
			set.add(new Condition("id.mdevId",new Object[]{lppk.getMDevId()},null,Restriction.EQ));
			set.add(new Condition("id.dst",new Object[]{lppk.getDst()},null,Restriction.EQ));
			
			MeteringLP currLp = null;
			List<LpEM>  currLp1= lpEMDao.getLpEMsByListCondition(set);
			if(CommonConstants.MeterType.EnergyMeter.getLpClassName().equals(lpClassName)) {				
				currLp = lpEMDao.getLpEMsByListCondition(set).get(0);
				table = CommonConstants.MeterType.EnergyMeter.getLpClassName();
			}else if(CommonConstants.MeterType.GasMeter.getLpClassName().equals(lpClassName)){
				currLp = lpGMDao.getLpGMsByListCondition(set).get(0);
				table = CommonConstants.MeterType.GasMeter.getLpClassName();
			}else if(CommonConstants.MeterType.WaterMeter.getLpClassName().equals(lpClassName)){
				currLp = lpWMDao.getLpWMsByListCondition(set).get(0);
				table = CommonConstants.MeterType.WaterMeter.getLpClassName();
			}
			
			/*1. lp_xx table update start*/
			
			
			//원래의 lp 데이터.
			//System.out.println("==> getLpEMsByListCondition~~~~~~~~~~~~~~~~~~~~~~~~");
			
			/* 
			 * 복원 클릭시.... (params[] => 
				paramArray[0]// 속성값
		        paramArray[1] =  이후값
		        paramArray[2] =  이전값
		        paramArray[3] =  테이블 클래스 명)
	        */
			if(params.length < 30){
				
				String _attrName 	= params[0];	//ex) value_00
				String _afterValue 	= StringUtil.nullToBlank(params[1]);
				String _beforeValue = StringUtil.nullToBlank(params[2]);
				String _tableName	= params[3];	//LpEM
				
				// _afterValue의 값이 적용된 값으로 세팅... : 복원 버튼 클릭시 적용 버튼을 클릭하여 수정되는 구문을 같이 쓰기 위해...
				params = new String[60];
				
				params[0] = StringUtil.nullToBlank(currLp.getValue_00());
				params[1] = StringUtil.nullToBlank(currLp.getValue_01());
				params[2] = StringUtil.nullToBlank(currLp.getValue_02());
				params[3] = StringUtil.nullToBlank(currLp.getValue_03());
				params[4] = StringUtil.nullToBlank(currLp.getValue_04());
				params[5] = StringUtil.nullToBlank(currLp.getValue_05());
				params[6] = StringUtil.nullToBlank(currLp.getValue_06());
				params[7] = StringUtil.nullToBlank(currLp.getValue_07());
				params[8] = StringUtil.nullToBlank(currLp.getValue_08());
				params[9] = StringUtil.nullToBlank(currLp.getValue_09());
				params[10] = StringUtil.nullToBlank(currLp.getValue_10());
				params[11] = StringUtil.nullToBlank(currLp.getValue_11());
				params[12] = StringUtil.nullToBlank(currLp.getValue_12());
				params[13] = StringUtil.nullToBlank(currLp.getValue_13());
				params[14] = StringUtil.nullToBlank(currLp.getValue_14());
				params[15] = StringUtil.nullToBlank(currLp.getValue_15());
				params[16] = StringUtil.nullToBlank(currLp.getValue_16());
				params[17] = StringUtil.nullToBlank(currLp.getValue_17());
				params[18] = StringUtil.nullToBlank(currLp.getValue_18());
				params[19] = StringUtil.nullToBlank(currLp.getValue_19());
				params[20] = StringUtil.nullToBlank(currLp.getValue_20());
				params[21] = StringUtil.nullToBlank(currLp.getValue_21());
				params[22] = StringUtil.nullToBlank(currLp.getValue_22());
				params[23] = StringUtil.nullToBlank(currLp.getValue_23());
				params[24] = StringUtil.nullToBlank(currLp.getValue_24());
				params[25] = StringUtil.nullToBlank(currLp.getValue_25());
				params[26] = StringUtil.nullToBlank(currLp.getValue_26());
				params[27] = StringUtil.nullToBlank(currLp.getValue_27());
				params[28] = StringUtil.nullToBlank(currLp.getValue_28());
				params[29] = StringUtil.nullToBlank(currLp.getValue_29());
				params[30] = StringUtil.nullToBlank(currLp.getValue_30());
				params[31] = StringUtil.nullToBlank(currLp.getValue_31());
				params[32] = StringUtil.nullToBlank(currLp.getValue_32());
				params[33] = StringUtil.nullToBlank(currLp.getValue_33());
				params[34] = StringUtil.nullToBlank(currLp.getValue_34());
				params[35] = StringUtil.nullToBlank(currLp.getValue_35());
				params[36] = StringUtil.nullToBlank(currLp.getValue_36());
				params[37] = StringUtil.nullToBlank(currLp.getValue_37());
				params[38] = StringUtil.nullToBlank(currLp.getValue_38());
				params[39] = StringUtil.nullToBlank(currLp.getValue_39());
				params[40] = StringUtil.nullToBlank(currLp.getValue_40());
				params[41] = StringUtil.nullToBlank(currLp.getValue_41());
				params[42] = StringUtil.nullToBlank(currLp.getValue_42());
				params[43] = StringUtil.nullToBlank(currLp.getValue_43());
				params[44] = StringUtil.nullToBlank(currLp.getValue_44());
				params[45] = StringUtil.nullToBlank(currLp.getValue_45());
				params[46] = StringUtil.nullToBlank(currLp.getValue_46());
				params[47] = StringUtil.nullToBlank(currLp.getValue_47());
				params[48] = StringUtil.nullToBlank(currLp.getValue_48());
				params[49] = StringUtil.nullToBlank(currLp.getValue_49());
				params[50] = StringUtil.nullToBlank(currLp.getValue_50());
				params[51] = StringUtil.nullToBlank(currLp.getValue_51());
				params[52] = StringUtil.nullToBlank(currLp.getValue_52());
				params[53] = StringUtil.nullToBlank(currLp.getValue_53());
				params[54] = StringUtil.nullToBlank(currLp.getValue_54());
				params[55] = StringUtil.nullToBlank(currLp.getValue_55());
				params[56] = StringUtil.nullToBlank(currLp.getValue_56());
				params[57] = StringUtil.nullToBlank(currLp.getValue_57());
				params[58] = StringUtil.nullToBlank(currLp.getValue_58());
				params[59] = StringUtil.nullToBlank(currLp.getValue_59());
				
				for(int p=0; p<params.length; p++){
					if(Integer.parseInt((_attrName.substring(6, 8))) == p){
						if(_beforeValue.trim().equals("")) {
							params[p] = "";
						} else {
							params[p] = df.format(Double.parseDouble(_beforeValue));
						}
					}
				}
			}
			
			if(!params[0].equals(currLp.getValue_00()==null?"":df.format(currLp.getValue_00()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "value_00", currLp.getValue_00()==null?"":currLp.getValue_00().toString(), params[0], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[1].equals(currLp.getValue_01()==null?"":df.format(currLp.getValue_01()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "value_01", currLp.getValue_01()==null?"":currLp.getValue_01().toString(), params[1], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[2].equals(currLp.getValue_02()==null?"":df.format(currLp.getValue_02()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "value_02", currLp.getValue_02()==null?"":currLp.getValue_02().toString(), params[2], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[3].equals(currLp.getValue_03()==null?"":df.format(currLp.getValue_03()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "value_03", currLp.getValue_03()==null?"":currLp.getValue_03().toString(), params[3], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[4].equals(currLp.getValue_04()==null?"":df.format(currLp.getValue_04()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "value_04", currLp.getValue_04()==null?"":currLp.getValue_04().toString(), params[4], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[5].equals(currLp.getValue_05()==null?"":df.format(currLp.getValue_05()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "value_05", currLp.getValue_05()==null?"":currLp.getValue_05().toString(), params[5], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[6].equals(currLp.getValue_06()==null?"":df.format(currLp.getValue_06()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "value_06", currLp.getValue_06()==null?"":currLp.getValue_06().toString(), params[6], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[7].equals(currLp.getValue_07()==null?"":df.format(currLp.getValue_07()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "value_07", currLp.getValue_07()==null?"":currLp.getValue_07().toString(), params[7], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[8].equals(currLp.getValue_08()==null?"":df.format(currLp.getValue_08()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "value_08", currLp.getValue_08()==null?"":currLp.getValue_08().toString(), params[8], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[9].equals(currLp.getValue_09()==null?"":df.format(currLp.getValue_09()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "value_09", currLp.getValue_09()==null?"":currLp.getValue_09().toString(), params[9], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[10].equals(currLp.getValue_10()==null?"":df.format(currLp.getValue_10()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "value_10", currLp.getValue_10()==null?"":currLp.getValue_10().toString(), params[10], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[11].equals(currLp.getValue_11()==null?"":df.format(currLp.getValue_11()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_11", currLp.getValue_11()==null?"":currLp.getValue_11().toString(), params[11], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[12].equals(currLp.getValue_12()==null?"":df.format(currLp.getValue_12()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_12", currLp.getValue_12()==null?"":currLp.getValue_12().toString(), params[12], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[13].equals(currLp.getValue_13()==null?"":df.format(currLp.getValue_13()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_13", currLp.getValue_13()==null?"":currLp.getValue_13().toString(), params[13], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[14].equals(currLp.getValue_14()==null?"":df.format(currLp.getValue_14()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_14", currLp.getValue_14()==null?"":currLp.getValue_14().toString(), params[14], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[15].equals(currLp.getValue_15()==null?"":df.format(currLp.getValue_15()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_15", currLp.getValue_15()==null?"":currLp.getValue_15().toString(), params[15], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[16].equals(currLp.getValue_16()==null?"":df.format(currLp.getValue_16()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_16", currLp.getValue_16()==null?"":currLp.getValue_16().toString(), params[16], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[17].equals(currLp.getValue_17()==null?"":df.format(currLp.getValue_17()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_17", currLp.getValue_17()==null?"":currLp.getValue_17().toString(), params[17], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[18].equals(currLp.getValue_18()==null?"":df.format(currLp.getValue_18()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_18", currLp.getValue_18()==null?"":currLp.getValue_18().toString(), params[18], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[19].equals(currLp.getValue_19()==null?"":df.format(currLp.getValue_19()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_19", currLp.getValue_19()==null?"":currLp.getValue_19().toString(), params[19], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[20].equals(currLp.getValue_20()==null?"":df.format(currLp.getValue_20()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_20", currLp.getValue_20()==null?"":currLp.getValue_20().toString(), params[20], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[21].equals(currLp.getValue_21()==null?"":df.format(currLp.getValue_21()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_21", currLp.getValue_21()==null?"":currLp.getValue_21().toString(), params[21], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[22].equals(currLp.getValue_22()==null?"":df.format(currLp.getValue_22()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_22", currLp.getValue_22()==null?"":currLp.getValue_22().toString(), params[22], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[23].equals(currLp.getValue_23()==null?"":df.format(currLp.getValue_23()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_23", currLp.getValue_23()==null?"":currLp.getValue_23().toString(), params[23], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[24].equals(currLp.getValue_24()==null?"":df.format(currLp.getValue_24()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_24", currLp.getValue_24()==null?"":currLp.getValue_24().toString(), params[24], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[25].equals(currLp.getValue_25()==null?"":df.format(currLp.getValue_25()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_25", currLp.getValue_25()==null?"":currLp.getValue_25().toString(), params[25], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[26].equals(currLp.getValue_26()==null?"":df.format(currLp.getValue_26()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_26", currLp.getValue_26()==null?"":currLp.getValue_26().toString(), params[26], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[27].equals(currLp.getValue_27()==null?"":df.format(currLp.getValue_27()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_27", currLp.getValue_27()==null?"":currLp.getValue_27().toString(), params[27], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[28].equals(currLp.getValue_28()==null?"":df.format(currLp.getValue_28()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_28", currLp.getValue_28()==null?"":currLp.getValue_28().toString(), params[28], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[29].equals(currLp.getValue_29()==null?"":df.format(currLp.getValue_29()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_29", currLp.getValue_29()==null?"":currLp.getValue_29().toString(), params[29], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[30].equals(currLp.getValue_30()==null?"":df.format(currLp.getValue_30()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_30", currLp.getValue_30()==null?"":currLp.getValue_30().toString(), params[30], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[31].equals(currLp.getValue_31()==null?"":df.format(currLp.getValue_31()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_31", currLp.getValue_31()==null?"":currLp.getValue_31().toString(), params[31], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[32].equals(currLp.getValue_32()==null?"":df.format(currLp.getValue_32()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_32", currLp.getValue_32()==null?"":currLp.getValue_32().toString(), params[32], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[33].equals(currLp.getValue_33()==null?"":df.format(currLp.getValue_33()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_33", currLp.getValue_33()==null?"":currLp.getValue_33().toString(), params[33], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[34].equals(currLp.getValue_34()==null?"":df.format(currLp.getValue_34()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_34", currLp.getValue_34()==null?"":currLp.getValue_34().toString(), params[34], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[35].equals(currLp.getValue_35()==null?"":df.format(currLp.getValue_35()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_35", currLp.getValue_35()==null?"":currLp.getValue_35().toString(), params[35], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[36].equals(currLp.getValue_36()==null?"":df.format(currLp.getValue_36()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_36", currLp.getValue_36()==null?"":currLp.getValue_36().toString(), params[36], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[37].equals(currLp.getValue_37()==null?"":df.format(currLp.getValue_37()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_37", currLp.getValue_37()==null?"":currLp.getValue_37().toString(), params[37], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[38].equals(currLp.getValue_38()==null?"":df.format(currLp.getValue_38()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_38", currLp.getValue_38()==null?"":currLp.getValue_38().toString(), params[38], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[39].equals(currLp.getValue_39()==null?"":df.format(currLp.getValue_39()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_39", currLp.getValue_39()==null?"":currLp.getValue_39().toString(), params[39], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[40].equals(currLp.getValue_40()==null?"":df.format(currLp.getValue_40()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_40", currLp.getValue_40()==null?"":currLp.getValue_40().toString(), params[40], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[41].equals(currLp.getValue_41()==null?"":df.format(currLp.getValue_41()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_41", currLp.getValue_41()==null?"":currLp.getValue_41().toString(), params[41], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[42].equals(currLp.getValue_42()==null?"":df.format(currLp.getValue_42()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_42", currLp.getValue_42()==null?"":currLp.getValue_42().toString(), params[42], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[43].equals(currLp.getValue_43()==null?"":df.format(currLp.getValue_43()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_43", currLp.getValue_43()==null?"":currLp.getValue_43().toString(), params[43], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[44].equals(currLp.getValue_44()==null?"":df.format(currLp.getValue_44()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_44", currLp.getValue_44()==null?"":currLp.getValue_44().toString(), params[44], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[45].equals(currLp.getValue_45()==null?"":df.format(currLp.getValue_45()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_45", currLp.getValue_45()==null?"":currLp.getValue_45().toString(), params[45], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[46].equals(currLp.getValue_46()==null?"":df.format(currLp.getValue_46()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_46", currLp.getValue_46()==null?"":currLp.getValue_46().toString(), params[46], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[47].equals(currLp.getValue_47()==null?"":df.format(currLp.getValue_47()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_47", currLp.getValue_47()==null?"":currLp.getValue_47().toString(), params[47], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[48].equals(currLp.getValue_48()==null?"":df.format(currLp.getValue_48()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_48", currLp.getValue_48()==null?"":currLp.getValue_48().toString(), params[48], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[49].equals(currLp.getValue_49()==null?"":df.format(currLp.getValue_49()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_49", currLp.getValue_49()==null?"":currLp.getValue_49().toString(), params[49], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[50].equals(currLp.getValue_50()==null?"":df.format(currLp.getValue_50()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_50", currLp.getValue_50()==null?"":currLp.getValue_50().toString(), params[50], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[51].equals(currLp.getValue_51()==null?"":df.format(currLp.getValue_51()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_51", currLp.getValue_51()==null?"":currLp.getValue_51().toString(), params[51], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[52].equals(currLp.getValue_52()==null?"":df.format(currLp.getValue_52()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_52", currLp.getValue_52()==null?"":currLp.getValue_52().toString(), params[52], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[53].equals(currLp.getValue_53()==null?"":df.format(currLp.getValue_53()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_53", currLp.getValue_53()==null?"":currLp.getValue_53().toString(), params[53], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[54].equals(currLp.getValue_54()==null?"":df.format(currLp.getValue_54()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_54", currLp.getValue_54()==null?"":currLp.getValue_54().toString(), params[54], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[55].equals(currLp.getValue_55()==null?"":df.format(currLp.getValue_55()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_55", currLp.getValue_55()==null?"":currLp.getValue_55().toString(), params[55], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[56].equals(currLp.getValue_56()==null?"":df.format(currLp.getValue_56()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_56", currLp.getValue_56()==null?"":currLp.getValue_56().toString(), params[56], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[57].equals(currLp.getValue_57()==null?"":df.format(currLp.getValue_57()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_57", currLp.getValue_57()==null?"":currLp.getValue_57().toString(), params[57], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[58].equals(currLp.getValue_58()==null?"":df.format(currLp.getValue_58()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_58", currLp.getValue_58()==null?"":currLp.getValue_58().toString(), params[58], userId, lppk, EditItem.IndividualEdited.toString());
			}
			if(!params[59].equals(currLp.getValue_59()==null?"":df.format(currLp.getValue_59()))){
				insertVEELog_Lp(currLp, yyyymmdd, hh, table, "Value_59", currLp.getValue_59()==null?"":currLp.getValue_59().toString(), params[59], userId, lppk, EditItem.IndividualEdited.toString());
			}
			
			
			if(!params[0].equals(StringUtil.nullToBlank(currLp.getValue_00()))) currLp.setValue_00(params[0].equals("")?null:Double.parseDouble(params[0]));
			if(!params[1].equals(StringUtil.nullToBlank(currLp.getValue_01()))) currLp.setValue_01(params[1].equals("")?null:Double.parseDouble(params[1]));
			if(!params[2].equals(StringUtil.nullToBlank(currLp.getValue_02()))) currLp.setValue_02(params[2].equals("")?null:Double.parseDouble(params[2]));
			if(!params[3].equals(StringUtil.nullToBlank(currLp.getValue_03()))) currLp.setValue_03(params[3].equals("")?null:Double.parseDouble(params[3]));
			if(!params[4].equals(StringUtil.nullToBlank(currLp.getValue_04()))) currLp.setValue_04(params[4].equals("")?null:Double.parseDouble(params[4]));
			if(!params[5].equals(StringUtil.nullToBlank(currLp.getValue_05()))) currLp.setValue_05(params[5].equals("")?null:Double.parseDouble(params[5]));
			if(!params[6].equals(StringUtil.nullToBlank(currLp.getValue_06()))) currLp.setValue_06(params[6].equals("")?null:Double.parseDouble(params[6]));
			if(!params[7].equals(StringUtil.nullToBlank(currLp.getValue_07()))) currLp.setValue_07(params[7].equals("")?null:Double.parseDouble(params[7]));
			if(!params[8].equals(StringUtil.nullToBlank(currLp.getValue_08()))) currLp.setValue_08(params[8].equals("")?null:Double.parseDouble(params[8]));
			if(!params[9].equals(StringUtil.nullToBlank(currLp.getValue_09()))) currLp.setValue_09(params[9].equals("")?null:Double.parseDouble(params[9]));
			if(!params[10].equals(StringUtil.nullToBlank(currLp.getValue_10()))) currLp.setValue_10(params[10].equals("")?null:Double.parseDouble(params[10]));
			
			if(!params[11].equals(StringUtil.nullToBlank(currLp.getValue_11()))) currLp.setValue_11(params[11].equals("")?null:Double.parseDouble(params[11]));
			if(!params[12].equals(StringUtil.nullToBlank(currLp.getValue_12()))) currLp.setValue_12(params[12].equals("")?null:Double.parseDouble(params[12]));
			if(!params[13].equals(StringUtil.nullToBlank(currLp.getValue_13()))) currLp.setValue_13(params[13].equals("")?null:Double.parseDouble(params[13]));
			if(!params[14].equals(StringUtil.nullToBlank(currLp.getValue_14()))) currLp.setValue_14(params[14].equals("")?null:Double.parseDouble(params[14]));
			if(!params[15].equals(StringUtil.nullToBlank(currLp.getValue_15()))) currLp.setValue_15(params[15].equals("")?null:Double.parseDouble(params[15]));
			if(!params[16].equals(StringUtil.nullToBlank(currLp.getValue_16()))) currLp.setValue_16(params[16].equals("")?null:Double.parseDouble(params[16]));
			if(!params[17].equals(StringUtil.nullToBlank(currLp.getValue_17()))) currLp.setValue_17(params[17].equals("")?null:Double.parseDouble(params[17]));
			if(!params[18].equals(StringUtil.nullToBlank(currLp.getValue_18()))) currLp.setValue_18(params[18].equals("")?null:Double.parseDouble(params[18]));
			if(!params[19].equals(StringUtil.nullToBlank(currLp.getValue_19()))) currLp.setValue_19(params[19].equals("")?null:Double.parseDouble(params[19]));
			if(!params[20].equals(StringUtil.nullToBlank(currLp.getValue_20()))) currLp.setValue_20(params[20].equals("")?null:Double.parseDouble(params[20]));
			
			if(!params[21].equals(StringUtil.nullToBlank(currLp.getValue_21()))) currLp.setValue_21(params[21].equals("")?null:Double.parseDouble(params[21]));
			if(!params[22].equals(StringUtil.nullToBlank(currLp.getValue_22()))) currLp.setValue_22(params[22].equals("")?null:Double.parseDouble(params[22]));
			if(!params[23].equals(StringUtil.nullToBlank(currLp.getValue_23()))) currLp.setValue_23(params[23].equals("")?null:Double.parseDouble(params[23]));
			if(!params[24].equals(StringUtil.nullToBlank(currLp.getValue_24()))) currLp.setValue_24(params[24].equals("")?null:Double.parseDouble(params[24]));
			if(!params[25].equals(StringUtil.nullToBlank(currLp.getValue_25()))) currLp.setValue_25(params[25].equals("")?null:Double.parseDouble(params[25]));
			if(!params[26].equals(StringUtil.nullToBlank(currLp.getValue_26()))) currLp.setValue_26(params[26].equals("")?null:Double.parseDouble(params[26]));
			if(!params[27].equals(StringUtil.nullToBlank(currLp.getValue_27()))) currLp.setValue_27(params[27].equals("")?null:Double.parseDouble(params[27]));
			if(!params[28].equals(StringUtil.nullToBlank(currLp.getValue_28()))) currLp.setValue_28(params[28].equals("")?null:Double.parseDouble(params[28]));
			if(!params[29].equals(StringUtil.nullToBlank(currLp.getValue_29()))) currLp.setValue_29(params[29].equals("")?null:Double.parseDouble(params[29]));
			if(!params[30].equals(StringUtil.nullToBlank(currLp.getValue_30()))) currLp.setValue_30(params[30].equals("")?null:Double.parseDouble(params[30]));
			
			if(!params[31].equals(StringUtil.nullToBlank(currLp.getValue_31()))) currLp.setValue_31(params[31].equals("")?null:Double.parseDouble(params[31]));
			if(!params[32].equals(StringUtil.nullToBlank(currLp.getValue_32()))) currLp.setValue_32(params[32].equals("")?null:Double.parseDouble(params[32]));
			if(!params[33].equals(StringUtil.nullToBlank(currLp.getValue_33()))) currLp.setValue_33(params[33].equals("")?null:Double.parseDouble(params[33]));
			if(!params[34].equals(StringUtil.nullToBlank(currLp.getValue_34()))) currLp.setValue_34(params[34].equals("")?null:Double.parseDouble(params[34]));
			if(!params[35].equals(StringUtil.nullToBlank(currLp.getValue_35()))) currLp.setValue_35(params[35].equals("")?null:Double.parseDouble(params[35]));
			if(!params[36].equals(StringUtil.nullToBlank(currLp.getValue_36()))) currLp.setValue_36(params[36].equals("")?null:Double.parseDouble(params[36]));
			if(!params[37].equals(StringUtil.nullToBlank(currLp.getValue_37()))) currLp.setValue_37(params[37].equals("")?null:Double.parseDouble(params[37]));
			if(!params[38].equals(StringUtil.nullToBlank(currLp.getValue_38()))) currLp.setValue_38(params[38].equals("")?null:Double.parseDouble(params[38]));
			if(!params[39].equals(StringUtil.nullToBlank(currLp.getValue_39()))) currLp.setValue_39(params[39].equals("")?null:Double.parseDouble(params[39]));
			if(!params[40].equals(StringUtil.nullToBlank(currLp.getValue_40()))) currLp.setValue_40(params[40].equals("")?null:Double.parseDouble(params[40]));
			
			if(!params[41].equals(StringUtil.nullToBlank(currLp.getValue_41()))) currLp.setValue_41(params[41].equals("")?null:Double.parseDouble(params[41]));
			if(!params[42].equals(StringUtil.nullToBlank(currLp.getValue_42()))) currLp.setValue_42(params[42].equals("")?null:Double.parseDouble(params[42]));
			if(!params[43].equals(StringUtil.nullToBlank(currLp.getValue_43()))) currLp.setValue_43(params[43].equals("")?null:Double.parseDouble(params[43]));
			if(!params[44].equals(StringUtil.nullToBlank(currLp.getValue_44()))) currLp.setValue_44(params[44].equals("")?null:Double.parseDouble(params[44]));
			if(!params[45].equals(StringUtil.nullToBlank(currLp.getValue_45()))) currLp.setValue_45(params[45].equals("")?null:Double.parseDouble(params[45]));
			if(!params[46].equals(StringUtil.nullToBlank(currLp.getValue_46()))) currLp.setValue_46(params[46].equals("")?null:Double.parseDouble(params[46]));
			if(!params[47].equals(StringUtil.nullToBlank(currLp.getValue_47()))) currLp.setValue_47(params[47].equals("")?null:Double.parseDouble(params[47]));
			if(!params[48].equals(StringUtil.nullToBlank(currLp.getValue_48()))) currLp.setValue_48(params[48].equals("")?null:Double.parseDouble(params[48]));
			if(!params[49].equals(StringUtil.nullToBlank(currLp.getValue_49()))) currLp.setValue_49(params[49].equals("")?null:Double.parseDouble(params[49]));
			if(!params[50].equals(StringUtil.nullToBlank(currLp.getValue_50()))) currLp.setValue_50(params[50].equals("")?null:Double.parseDouble(params[50]));
			
			if(!params[51].equals(StringUtil.nullToBlank(currLp.getValue_51()))) currLp.setValue_51(params[51].equals("")?null:Double.parseDouble(params[51]));
			if(!params[52].equals(StringUtil.nullToBlank(currLp.getValue_52()))) currLp.setValue_52(params[52].equals("")?null:Double.parseDouble(params[52]));
			if(!params[53].equals(StringUtil.nullToBlank(currLp.getValue_53()))) currLp.setValue_53(params[53].equals("")?null:Double.parseDouble(params[53]));
			if(!params[54].equals(StringUtil.nullToBlank(currLp.getValue_54()))) currLp.setValue_54(params[54].equals("")?null:Double.parseDouble(params[54]));
			if(!params[55].equals(StringUtil.nullToBlank(currLp.getValue_55()))) currLp.setValue_55(params[55].equals("")?null:Double.parseDouble(params[55]));
			if(!params[56].equals(StringUtil.nullToBlank(currLp.getValue_56()))) currLp.setValue_56(params[56].equals("")?null:Double.parseDouble(params[56]));
			if(!params[57].equals(StringUtil.nullToBlank(currLp.getValue_57()))) currLp.setValue_57(params[57].equals("")?null:Double.parseDouble(params[57]));
			if(!params[58].equals(StringUtil.nullToBlank(currLp.getValue_58()))) currLp.setValue_58(params[58].equals("")?null:Double.parseDouble(params[58]));
			if(!params[59].equals(StringUtil.nullToBlank(currLp.getValue_59()))) currLp.setValue_59(params[59].equals("")?null:Double.parseDouble(params[59]));
			
			if(CommonConstants.MeterType.EnergyMeter.getLpClassName().equals(lpClassName)) {				
				//System.out.println("==> curLpEMDao.updatecurLpEM~~~~~~~~~~~~");
				lpEMDao.update((LpEM)currLp);
				
				//System.out.println("==> lpEMDao.updateLpEM end");
				
				//Integer.parseInt(null);
				
				//System.out.println("==> lp_xx table update end");
				/*1. lp_xx table update end*/

				
				/* 2. day_xx update */
				Map<String, Object> renewDayEM = dayemUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, hh, userId, currLp, EditItem.IndividualEdited.toString());
				if("wrong data !! => [hh]".equals(renewDayEM.get("returnValue"))) {
					return "wrong data !! => [hh]";
				} else {
					msg = "";
				}
				
				/*3. month_xx table update start*/
				msg = monthemUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, hh, userId, (DayEM)renewDayEM.get("returnValue"), EditItem.IndividualEdited.toString());
				if(!"".equals(msg)) return msg;
			}else if(CommonConstants.MeterType.GasMeter.getLpClassName().equals(lpClassName)){
				//System.out.println("==> curLpGMDao.updatecurLpEM~~~~~~~~~~~~");
				lpGMDao.update((LpGM)currLp);
				
				//System.out.println("==> lpGMDao.updateLpGM end");
				
				//Integer.parseInt(null);
				
				//System.out.println("==> lp_xx table update end");
				/*1. lp_xx table update end*/

				
				/* 2. day_xx update */
				Map<String, Object> renewDayGM = daygmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, hh, userId, currLp, EditItem.IndividualEdited.toString());
				if("wrong data !! => [hh]".equals(renewDayGM.get("returnValue"))) {
					return "wrong data !! => [hh]";
				} else {
					msg = "";
				}
				
				/*3. month_xx table update start*/
				msg = monthgmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, hh, userId, (DayGM)renewDayGM.get("returnValue"), EditItem.IndividualEdited.toString());
				if(!"".equals(msg)) return msg;
			}else if(CommonConstants.MeterType.WaterMeter.getLpClassName().equals(lpClassName)){
				//System.out.println("==> curLpWMDao.updatecurLpEM~~~~~~~~~~~~");
				lpWMDao.update((LpWM)currLp);
				
				//System.out.println("==> lpWMDao.updateLpWM end");
				
				//Integer.parseInt(null);
				
				//System.out.println("==> lp_xx table update end");
				/*1. lp_xx table update end*/

				
				/* 2. day_xx update */
				Map<String, Object> renewDayWM = daywmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, hh, userId, currLp, EditItem.IndividualEdited.toString());
				if("wrong data !! => [hh]".equals(renewDayWM.get("returnValue"))) {
					return "wrong data !! => [hh]";
				} else {
					msg = "";
				}
				
				/*3. month_xx table update start*/
				msg = monthwmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, hh, userId, (DayWM)renewDayWM.get("returnValue"), EditItem.IndividualEdited.toString());
				if(!"".equals(msg)) return msg;				
			}
		}
		
		return msg;
		
	}
	
	@SuppressWarnings("unused")
    public Map<String, Object> dayemUpdate(String yyyymmdd, String channel, String mdevType, String mdevId, String dst, String[] params, String hh, String userId, MeteringLP lpData, String editItem){
		/*2. day_xx table update start*/
		//System.out.println("==> day_xx table update start");
		Map<String, Object> returnData = new HashMap<String, Object>();
		DayPk daypk = new DayPk();
		
		daypk.setYyyymmdd(yyyymmdd);
		daypk.setChannel(Integer.parseInt(channel));
		daypk.setMDevType(mdevType);
		daypk.setMDevId(mdevId);
		daypk.setDst(Integer.parseInt(dst));
		
		Set<Condition> setDay = new HashSet<Condition>();
		setDay.add(new Condition("id.yyyymmdd",new Object[]{daypk.getYyyymmdd()},null,Restriction.EQ));
		setDay.add(new Condition("id.channel",new Object[]{daypk.getChannel()},null,Restriction.EQ));
		setDay.add(new Condition("id.mdevType",new Object[]{daypk.getMDevType()},null,Restriction.EQ));
		setDay.add(new Condition("id.mdevId",new Object[]{daypk.getMDevId()},null,Restriction.EQ));
		setDay.add(new Condition("id.dst",new Object[]{daypk.getDst()},null,Restriction.EQ));
		//원래의 day 데이터.
		//System.out.println("==> getDayEMsByListCondition" );
		
		List<DayEM> dayemList = dayEMDao.getDayEMsByListCondition(setDay);
		DayEM curDayEM = null;
		if(dayemList.size()>0){
			curDayEM = dayEMDao.getDayEMsByListCondition(setDay).get(0);
		}else{
			curDayEM = new DayEM();
			
			curDayEM.setId(daypk);
			curDayEM.setChannel(lpData.getChannel());
			curDayEM.setContract(lpData.getContract());
			curDayEM.setDeviceId(lpData.getDeviceId());
			
			if(lpData.getDeviceType()!=null)
				curDayEM.setDeviceType(lpData.getDeviceType().toString());
			
			curDayEM.setDst(lpData.getDst());
			curDayEM.setEnddevice(lpData.getEnddevice());
			curDayEM.setLocation(lpData.getLocation());
			curDayEM.setMDevId(lpData.getMDevId());
			
			if(lpData.getMDevType()!=null)
				curDayEM.setMDevType(lpData.getMDevType().toString());
			
			curDayEM.setMeter(lpData.getMeter());
			curDayEM.setMeteringType(lpData.getMeteringType());
			curDayEM.setModem(lpData.getModem());
			curDayEM.setYyyymmdd(yyyymmdd);
			curDayEM.setSupplier(lpData.getSupplier());
		}
		
		int k=0;
		Double hap = 0.0;
		
		for(int m=0; m<60; m++){
			//System.out.println(" value_" + k + " : " + StringUtil.nullToBlank(params[m]));
			hap = hap + Double.parseDouble("".equals(StringUtil.nullToBlank(params[m]))?"0.0":params[m]);
			k++;
		}

		
		String originalDayEmValue = "";
		String dayAttrName = "";
		
		if("00".equals(hh)){
			originalDayEmValue = curDayEM.getValue_00()==null?"":curDayEM.getValue_00().toString();
			dayAttrName = "value_00";
		}else if("01".equals(hh)){
			originalDayEmValue = curDayEM.getValue_01()==null?"":curDayEM.getValue_01().toString();
			dayAttrName = "value_01";
		}else if("02".equals(hh)){
			originalDayEmValue = curDayEM.getValue_02()==null?"":curDayEM.getValue_02().toString();
			dayAttrName = "value_02";
		}else if("03".equals(hh)){
			originalDayEmValue = curDayEM.getValue_03()==null?"":curDayEM.getValue_03().toString();
			dayAttrName = "value_03";
		}else if("04".equals(hh)){
			originalDayEmValue = curDayEM.getValue_04()==null?"":curDayEM.getValue_04().toString();
			dayAttrName = "value_04";
		}else if("05".equals(hh)){
			originalDayEmValue = curDayEM.getValue_05()==null?"":curDayEM.getValue_05().toString();
			dayAttrName = "value_05";
		}else if("06".equals(hh)){
			originalDayEmValue = curDayEM.getValue_06()==null?"":curDayEM.getValue_06().toString();
			dayAttrName = "value_06";
		}else if("07".equals(hh)){
			originalDayEmValue = curDayEM.getValue_07()==null?"":curDayEM.getValue_07().toString();
			dayAttrName = "value_07";
		}else if("08".equals(hh)){
			originalDayEmValue = curDayEM.getValue_08()==null?"":curDayEM.getValue_08().toString();
			dayAttrName = "value_08";
		}else if("09".equals(hh)){
			originalDayEmValue = curDayEM.getValue_09()==null?"":curDayEM.getValue_09().toString();
			dayAttrName = "value_09";
		}else if("10".equals(hh)){
			originalDayEmValue = curDayEM.getValue_10()==null?"":curDayEM.getValue_10().toString();
			dayAttrName = "value_10";
		}else if("11".equals(hh)){
			originalDayEmValue = curDayEM.getValue_11()==null?"":curDayEM.getValue_11().toString();
			dayAttrName = "value_11";
		}else if("12".equals(hh)){
			originalDayEmValue = curDayEM.getValue_12()==null?"":curDayEM.getValue_12().toString();
			dayAttrName = "value_12";
		}else if("13".equals(hh)){
			originalDayEmValue = curDayEM.getValue_13()==null?"":curDayEM.getValue_13().toString();
			dayAttrName = "value_13";
		}else if("14".equals(hh)){
			originalDayEmValue = curDayEM.getValue_14()==null?"":curDayEM.getValue_14().toString();
			dayAttrName = "value_14";
		}else if("15".equals(hh)){
			originalDayEmValue = curDayEM.getValue_15()==null?"":curDayEM.getValue_15().toString();
			dayAttrName = "value_15";
		}else if("16".equals(hh)){
			originalDayEmValue = curDayEM.getValue_16()==null?"":curDayEM.getValue_16().toString();
			dayAttrName = "value_16";
		}else if("17".equals(hh)){
			originalDayEmValue = curDayEM.getValue_17()==null?"":curDayEM.getValue_17().toString();
			dayAttrName = "value_17";
		}else if("18".equals(hh)){
			originalDayEmValue = curDayEM.getValue_18()==null?"":curDayEM.getValue_18().toString();
			dayAttrName = "value_18";
		}else if("19".equals(hh)){
			originalDayEmValue = curDayEM.getValue_19()==null?"":curDayEM.getValue_19().toString();
			dayAttrName = "value_19";
		}else if("20".equals(hh)){
			originalDayEmValue = curDayEM.getValue_20()==null?"":curDayEM.getValue_20().toString();
			dayAttrName = "value_20";
		}else if("21".equals(hh)){
			originalDayEmValue = curDayEM.getValue_21()==null?"":curDayEM.getValue_21().toString();
			dayAttrName = "value_21";
		}else if("22".equals(hh)){
			originalDayEmValue = curDayEM.getValue_22()==null?"":curDayEM.getValue_22().toString();
			dayAttrName = "value_22";
		}else if("23".equals(hh)){
			originalDayEmValue = curDayEM.getValue_23()==null?"":curDayEM.getValue_23().toString();
			dayAttrName = "value_23";
		}else{
			//System.out.println("[VEEManageImpl.java] ~~> 잘못된 hh 값입니다.");
			returnData.put("returnValue", "wrong data !! => [hh]");
			return returnData;
		}
		
		insertVEELog_day(curDayEM, yyyymmdd, hh, CommonConstants.MeterType.EnergyMeter.getDayClassName(), dayAttrName, originalDayEmValue.toString(), hap.toString(), userId, editItem);
		
		
		if("00".equals(hh)){
			curDayEM.setValue_00(hap);
		}else if("01".equals(hh)){
			curDayEM.setValue_01(hap);
		}else if("02".equals(hh)){
			curDayEM.setValue_02(hap);
		}else if("03".equals(hh)){
			curDayEM.setValue_03(hap);
		}else if("04".equals(hh)){
			curDayEM.setValue_04(hap);
		}else if("05".equals(hh)){
			curDayEM.setValue_05(hap);
		}else if("06".equals(hh)){
			curDayEM.setValue_06(hap);
		}else if("07".equals(hh)){
			curDayEM.setValue_07(hap);
		}else if("08".equals(hh)){
			curDayEM.setValue_08(hap);
		}else if("09".equals(hh)){
			curDayEM.setValue_09(hap);
		}else if("10".equals(hh)){
			curDayEM.setValue_10(hap);
		}else if("11".equals(hh)){
			curDayEM.setValue_11(hap);
		}else if("12".equals(hh)){
			curDayEM.setValue_12(hap);
		}else if("13".equals(hh)){
			curDayEM.setValue_13(hap);
		}else if("14".equals(hh)){
			curDayEM.setValue_14(hap);
		}else if("15".equals(hh)){
			curDayEM.setValue_15(hap);
		}else if("16".equals(hh)){
			curDayEM.setValue_16(hap);
		}else if("17".equals(hh)){
			curDayEM.setValue_17(hap);
		}else if("18".equals(hh)){
			curDayEM.setValue_18(hap);
		}else if("19".equals(hh)){
			curDayEM.setValue_19(hap);
		}else if("20".equals(hh)){
			curDayEM.setValue_20(hap);
		}else if("21".equals(hh)){
			curDayEM.setValue_21(hap);
		}else if("22".equals(hh)){
			curDayEM.setValue_22(hap);
		}else if("23".equals(hh)){
			curDayEM.setValue_23(hap);
		}else{
			//System.out.println("[VEEManageImpl.java] ~~> 잘못된 hh 값입니다.");
			returnData.put("returnValue", "wrong data !! => [hh]");
			return returnData;
		}
		
		double curDayEMTotal = (curDayEM.getValue_00()==null?0.0:curDayEM.getValue_00()) +
								(curDayEM.getValue_01()==null?0.0:curDayEM.getValue_01()) +
								(curDayEM.getValue_02()==null?0.0:curDayEM.getValue_02()) + 
								(curDayEM.getValue_03()==null?0.0:curDayEM.getValue_03()) + 
								(curDayEM.getValue_04()==null?0.0:curDayEM.getValue_04()) + 
								(curDayEM.getValue_05()==null?0.0:curDayEM.getValue_05()) + 
								(curDayEM.getValue_06()==null?0.0:curDayEM.getValue_06()) + 
								(curDayEM.getValue_07()==null?0.0:curDayEM.getValue_07()) + 
								(curDayEM.getValue_08()==null?0.0:curDayEM.getValue_08()) + 
								(curDayEM.getValue_09()==null?0.0:curDayEM.getValue_09()) + 
								(curDayEM.getValue_10()==null?0.0:curDayEM.getValue_10()) +
								(curDayEM.getValue_11()==null?0.0:curDayEM.getValue_11()) +
								(curDayEM.getValue_12()==null?0.0:curDayEM.getValue_12()) + 
								(curDayEM.getValue_13()==null?0.0:curDayEM.getValue_13()) + 
								(curDayEM.getValue_14()==null?0.0:curDayEM.getValue_14()) + 
								(curDayEM.getValue_15()==null?0.0:curDayEM.getValue_15()) + 
								(curDayEM.getValue_16()==null?0.0:curDayEM.getValue_16()) + 
								(curDayEM.getValue_17()==null?0.0:curDayEM.getValue_17()) + 
								(curDayEM.getValue_18()==null?0.0:curDayEM.getValue_18()) + 
								(curDayEM.getValue_19()==null?0.0:curDayEM.getValue_19()) + 
								(curDayEM.getValue_20()==null?0.0:curDayEM.getValue_20()) +
								(curDayEM.getValue_21()==null?0.0:curDayEM.getValue_21()) +
								(curDayEM.getValue_22()==null?0.0:curDayEM.getValue_22()) + 
								(curDayEM.getValue_23()==null?0.0:curDayEM.getValue_23());
		
		curDayEM.setTotal(curDayEMTotal);
		
		//System.out.println("==> dayEMDao.update");
		
		dayEMDao.saveOrUpdate(curDayEM);
		/*2. day_xx table update end*/
		
		returnData.put("returnValue", curDayEM);
		return returnData;
	}
	
	@SuppressWarnings("unused")
    public Map<String, Object> daygmUpdate(String yyyymmdd, String channel, String mdevType, String mdevId, String dst, String[] params, String hh, String userId, MeteringLP lpData, String editItem){
		/*2. day_xx table update start*/
		//System.out.println("==> day_xx table update start");
		Map<String, Object> returnData = new HashMap<String, Object>();
		DayPk daypk = new DayPk();
		
		daypk.setYyyymmdd(yyyymmdd);
		daypk.setChannel(Integer.parseInt(channel));
		daypk.setMDevType(mdevType);
		daypk.setMDevId(mdevId);
		daypk.setDst(Integer.parseInt(dst));
		
		Set<Condition> setDay = new HashSet<Condition>();
		setDay.add(new Condition("id.yyyymmdd",new Object[]{daypk.getYyyymmdd()},null,Restriction.EQ));
		setDay.add(new Condition("id.channel",new Object[]{daypk.getChannel()},null,Restriction.EQ));
		setDay.add(new Condition("id.mdevType",new Object[]{daypk.getMDevType()},null,Restriction.EQ));
		setDay.add(new Condition("id.mdevId",new Object[]{daypk.getMDevId()},null,Restriction.EQ));
		setDay.add(new Condition("id.dst",new Object[]{daypk.getDst()},null,Restriction.EQ));
		//원래의 day 데이터.
		//System.out.println("==> getDayEMsByListCondition" );
		
		List<DayGM> dayemList = dayGMDao.getDayGMsByListCondition(setDay);
		DayGM curDayGM = null;
		if(dayemList.size()>0){
			curDayGM = dayGMDao.getDayGMsByListCondition(setDay).get(0);
		}else{
			curDayGM = new DayGM();
			
			curDayGM.setId(daypk);
			curDayGM.setChannel(lpData.getChannel());
			curDayGM.setContract(lpData.getContract());
			curDayGM.setDeviceId(lpData.getDeviceId());
			
			if(lpData.getDeviceType()!=null)
				curDayGM.setDeviceType(lpData.getDeviceType().toString());
			
			curDayGM.setDst(lpData.getDst());
			curDayGM.setEnddevice(lpData.getEnddevice());
			curDayGM.setLocation(lpData.getLocation());
			curDayGM.setMDevId(lpData.getMDevId());
			
			if(lpData.getMDevType()!=null)
				curDayGM.setMDevType(lpData.getMDevType().toString());
			
			curDayGM.setMeter(lpData.getMeter());
			curDayGM.setMeteringType(lpData.getMeteringType());
			curDayGM.setModem(lpData.getModem());
			curDayGM.setYyyymmdd(yyyymmdd);
		}
		
		int k=0;
		Double hap = 0.0;
		
		for(int m=0; m<60; m++){
			//System.out.println(" value_" + k + " : " + StringUtil.nullToBlank(params[m]));
			hap = hap + Double.parseDouble("".equals(StringUtil.nullToBlank(params[m]))?"0.0":params[m]);
			k++;
		}

		
		String originalDayGmValue = "";
		String dayAttrName = "";
		
		if("00".equals(hh)){
			originalDayGmValue = curDayGM.getValue_00()==null?"":curDayGM.getValue_00().toString();
			dayAttrName = "value_00";
		}else if("01".equals(hh)){
			originalDayGmValue = curDayGM.getValue_01()==null?"":curDayGM.getValue_01().toString();
			dayAttrName = "value_01";
		}else if("02".equals(hh)){
			originalDayGmValue = curDayGM.getValue_02()==null?"":curDayGM.getValue_02().toString();
			dayAttrName = "value_02";
		}else if("03".equals(hh)){
			originalDayGmValue = curDayGM.getValue_03()==null?"":curDayGM.getValue_03().toString();
			dayAttrName = "value_03";
		}else if("04".equals(hh)){
			originalDayGmValue = curDayGM.getValue_04()==null?"":curDayGM.getValue_04().toString();
			dayAttrName = "value_04";
		}else if("05".equals(hh)){
			originalDayGmValue = curDayGM.getValue_05()==null?"":curDayGM.getValue_05().toString();
			dayAttrName = "value_05";
		}else if("06".equals(hh)){
			originalDayGmValue = curDayGM.getValue_06()==null?"":curDayGM.getValue_06().toString();
			dayAttrName = "value_06";
		}else if("07".equals(hh)){
			originalDayGmValue = curDayGM.getValue_07()==null?"":curDayGM.getValue_07().toString();
			dayAttrName = "value_07";
		}else if("08".equals(hh)){
			originalDayGmValue = curDayGM.getValue_08()==null?"":curDayGM.getValue_08().toString();
			dayAttrName = "value_08";
		}else if("09".equals(hh)){
			originalDayGmValue = curDayGM.getValue_09()==null?"":curDayGM.getValue_09().toString();
			dayAttrName = "value_09";
		}else if("10".equals(hh)){
			originalDayGmValue = curDayGM.getValue_10()==null?"":curDayGM.getValue_10().toString();
			dayAttrName = "value_10";
		}else if("11".equals(hh)){
			originalDayGmValue = curDayGM.getValue_11()==null?"":curDayGM.getValue_11().toString();
			dayAttrName = "value_11";
		}else if("12".equals(hh)){
			originalDayGmValue = curDayGM.getValue_12()==null?"":curDayGM.getValue_12().toString();
			dayAttrName = "value_12";
		}else if("13".equals(hh)){
			originalDayGmValue = curDayGM.getValue_13()==null?"":curDayGM.getValue_13().toString();
			dayAttrName = "value_13";
		}else if("14".equals(hh)){
			originalDayGmValue = curDayGM.getValue_14()==null?"":curDayGM.getValue_14().toString();
			dayAttrName = "value_14";
		}else if("15".equals(hh)){
			originalDayGmValue = curDayGM.getValue_15()==null?"":curDayGM.getValue_15().toString();
			dayAttrName = "value_15";
		}else if("16".equals(hh)){
			originalDayGmValue = curDayGM.getValue_16()==null?"":curDayGM.getValue_16().toString();
			dayAttrName = "value_16";
		}else if("17".equals(hh)){
			originalDayGmValue = curDayGM.getValue_17()==null?"":curDayGM.getValue_17().toString();
			dayAttrName = "value_17";
		}else if("18".equals(hh)){
			originalDayGmValue = curDayGM.getValue_18()==null?"":curDayGM.getValue_18().toString();
			dayAttrName = "value_18";
		}else if("19".equals(hh)){
			originalDayGmValue = curDayGM.getValue_19()==null?"":curDayGM.getValue_19().toString();
			dayAttrName = "value_19";
		}else if("20".equals(hh)){
			originalDayGmValue = curDayGM.getValue_20()==null?"":curDayGM.getValue_20().toString();
			dayAttrName = "value_20";
		}else if("21".equals(hh)){
			originalDayGmValue = curDayGM.getValue_21()==null?"":curDayGM.getValue_21().toString();
			dayAttrName = "value_21";
		}else if("22".equals(hh)){
			originalDayGmValue = curDayGM.getValue_22()==null?"":curDayGM.getValue_22().toString();
			dayAttrName = "value_22";
		}else if("23".equals(hh)){
			originalDayGmValue = curDayGM.getValue_23()==null?"":curDayGM.getValue_23().toString();
			dayAttrName = "value_23";
		}else{
			//System.out.println("[VEEManageImpl.java] ~~> 잘못된 hh 값입니다.");
			returnData.put("returnValue", "wrong data !! => [hh]");
			return returnData;
		}
		
		insertVEELog_day(curDayGM, yyyymmdd, hh, CommonConstants.MeterType.GasMeter.getDayClassName(), dayAttrName, originalDayGmValue.toString(), hap.toString(), userId, editItem);
		
		
		if("00".equals(hh)){
			curDayGM.setValue_00(hap);
		}else if("01".equals(hh)){
			curDayGM.setValue_01(hap);
		}else if("02".equals(hh)){
			curDayGM.setValue_02(hap);
		}else if("03".equals(hh)){
			curDayGM.setValue_03(hap);
		}else if("04".equals(hh)){
			curDayGM.setValue_04(hap);
		}else if("05".equals(hh)){
			curDayGM.setValue_05(hap);
		}else if("06".equals(hh)){
			curDayGM.setValue_06(hap);
		}else if("07".equals(hh)){
			curDayGM.setValue_07(hap);
		}else if("08".equals(hh)){
			curDayGM.setValue_08(hap);
		}else if("09".equals(hh)){
			curDayGM.setValue_09(hap);
		}else if("10".equals(hh)){
			curDayGM.setValue_10(hap);
		}else if("11".equals(hh)){
			curDayGM.setValue_11(hap);
		}else if("12".equals(hh)){
			curDayGM.setValue_12(hap);
		}else if("13".equals(hh)){
			curDayGM.setValue_13(hap);
		}else if("14".equals(hh)){
			curDayGM.setValue_14(hap);
		}else if("15".equals(hh)){
			curDayGM.setValue_15(hap);
		}else if("16".equals(hh)){
			curDayGM.setValue_16(hap);
		}else if("17".equals(hh)){
			curDayGM.setValue_17(hap);
		}else if("18".equals(hh)){
			curDayGM.setValue_18(hap);
		}else if("19".equals(hh)){
			curDayGM.setValue_19(hap);
		}else if("20".equals(hh)){
			curDayGM.setValue_20(hap);
		}else if("21".equals(hh)){
			curDayGM.setValue_21(hap);
		}else if("22".equals(hh)){
			curDayGM.setValue_22(hap);
		}else if("23".equals(hh)){
			curDayGM.setValue_23(hap);
		}else{
			//System.out.println("[VEEManageImpl.java] ~~> 잘못된 hh 값입니다.");
			returnData.put("returnValue", "wrong data !! => [hh]");
			return returnData;
		}
		
		double curDayGMTotal = (curDayGM.getValue_00()==null?0.0:curDayGM.getValue_00()) +
								(curDayGM.getValue_01()==null?0.0:curDayGM.getValue_01()) +
								(curDayGM.getValue_02()==null?0.0:curDayGM.getValue_02()) + 
								(curDayGM.getValue_03()==null?0.0:curDayGM.getValue_03()) + 
								(curDayGM.getValue_04()==null?0.0:curDayGM.getValue_04()) + 
								(curDayGM.getValue_05()==null?0.0:curDayGM.getValue_05()) + 
								(curDayGM.getValue_06()==null?0.0:curDayGM.getValue_06()) + 
								(curDayGM.getValue_07()==null?0.0:curDayGM.getValue_07()) + 
								(curDayGM.getValue_08()==null?0.0:curDayGM.getValue_08()) + 
								(curDayGM.getValue_09()==null?0.0:curDayGM.getValue_09()) + 
								(curDayGM.getValue_10()==null?0.0:curDayGM.getValue_10()) +
								(curDayGM.getValue_11()==null?0.0:curDayGM.getValue_11()) +
								(curDayGM.getValue_12()==null?0.0:curDayGM.getValue_12()) + 
								(curDayGM.getValue_13()==null?0.0:curDayGM.getValue_13()) + 
								(curDayGM.getValue_14()==null?0.0:curDayGM.getValue_14()) + 
								(curDayGM.getValue_15()==null?0.0:curDayGM.getValue_15()) + 
								(curDayGM.getValue_16()==null?0.0:curDayGM.getValue_16()) + 
								(curDayGM.getValue_17()==null?0.0:curDayGM.getValue_17()) + 
								(curDayGM.getValue_18()==null?0.0:curDayGM.getValue_18()) + 
								(curDayGM.getValue_19()==null?0.0:curDayGM.getValue_19()) + 
								(curDayGM.getValue_20()==null?0.0:curDayGM.getValue_20()) +
								(curDayGM.getValue_21()==null?0.0:curDayGM.getValue_21()) +
								(curDayGM.getValue_22()==null?0.0:curDayGM.getValue_22()) + 
								(curDayGM.getValue_23()==null?0.0:curDayGM.getValue_23());
		
		curDayGM.setTotal(curDayGMTotal);
		
		//System.out.println("==> dayEMDao.update");
		
		dayGMDao.saveOrUpdate(curDayGM);
		/*2. day_xx table update end*/
		
		returnData.put("returnValue", curDayGM);
		return returnData;
	}
	
	@SuppressWarnings("unused")
    public Map<String, Object> daywmUpdate(String yyyymmdd, String channel, String mdevType, String mdevId, String dst, String[] params, String hh, String userId, MeteringLP lpData, String editItem){
		/*2. day_xx table update start*/
		//System.out.println("==> day_xx table update start");
		Map<String, Object> returnData = new HashMap<String, Object>();
		DayPk daypk = new DayPk();
		
		daypk.setYyyymmdd(yyyymmdd);
		daypk.setChannel(Integer.parseInt(channel));
		daypk.setMDevType(mdevType);
		daypk.setMDevId(mdevId);
		daypk.setDst(Integer.parseInt(dst));
		
		Set<Condition> setDay = new HashSet<Condition>();
		setDay.add(new Condition("id.yyyymmdd",new Object[]{daypk.getYyyymmdd()},null,Restriction.EQ));
		setDay.add(new Condition("id.channel",new Object[]{daypk.getChannel()},null,Restriction.EQ));
		setDay.add(new Condition("id.mdevType",new Object[]{daypk.getMDevType()},null,Restriction.EQ));
		setDay.add(new Condition("id.mdevId",new Object[]{daypk.getMDevId()},null,Restriction.EQ));
		setDay.add(new Condition("id.dst",new Object[]{daypk.getDst()},null,Restriction.EQ));
		//원래의 day 데이터.
		//System.out.println("==> getDayEMsByListCondition" );
		
		List<DayWM> dayemList = dayWMDao.getDayWMsByListCondition(setDay);
		DayWM curDayWM = null;
		if(dayemList.size()>0){
			curDayWM = dayWMDao.getDayWMsByListCondition(setDay).get(0);
		}else{
			curDayWM = new DayWM();
			
			curDayWM.setId(daypk);
			curDayWM.setChannel(lpData.getChannel());
			curDayWM.setContract(lpData.getContract());
			curDayWM.setDeviceId(lpData.getDeviceId());
			
			if(lpData.getDeviceType()!=null)
				curDayWM.setDeviceType(lpData.getDeviceType().toString());
			
			curDayWM.setDst(lpData.getDst());
			curDayWM.setEnddevice(lpData.getEnddevice());
			curDayWM.setLocation(lpData.getLocation());
			curDayWM.setMDevId(lpData.getMDevId());
			
			if(lpData.getMDevType()!=null)
				curDayWM.setMDevType(lpData.getMDevType().toString());
			
			curDayWM.setMeter(lpData.getMeter());
			curDayWM.setMeteringType(lpData.getMeteringType());
			curDayWM.setModem(lpData.getModem());
			curDayWM.setYyyymmdd(yyyymmdd);
			curDayWM.setSupplier(lpData.getSupplier());
		}
		
		int k=0;
		Double hap = 0.0;
		
		for(int m=0; m<60; m++){
			//System.out.println(" value_" + k + " : " + StringUtil.nullToBlank(params[m]));
			hap = hap + Double.parseDouble("".equals(StringUtil.nullToBlank(params[m]))?"0.0":params[m]);
			k++;
		}

		
		String originalDayWmValue = "";
		String dayAttrName = "";
		
		if("00".equals(hh)){
			originalDayWmValue = curDayWM.getValue_00()==null?"":curDayWM.getValue_00().toString();
			dayAttrName = "value_00";
		}else if("01".equals(hh)){
			originalDayWmValue = curDayWM.getValue_01()==null?"":curDayWM.getValue_01().toString();
			dayAttrName = "value_01";
		}else if("02".equals(hh)){
			originalDayWmValue = curDayWM.getValue_02()==null?"":curDayWM.getValue_02().toString();
			dayAttrName = "value_02";
		}else if("03".equals(hh)){
			originalDayWmValue = curDayWM.getValue_03()==null?"":curDayWM.getValue_03().toString();
			dayAttrName = "value_03";
		}else if("04".equals(hh)){
			originalDayWmValue = curDayWM.getValue_04()==null?"":curDayWM.getValue_04().toString();
			dayAttrName = "value_04";
		}else if("05".equals(hh)){
			originalDayWmValue = curDayWM.getValue_05()==null?"":curDayWM.getValue_05().toString();
			dayAttrName = "value_05";
		}else if("06".equals(hh)){
			originalDayWmValue = curDayWM.getValue_06()==null?"":curDayWM.getValue_06().toString();
			dayAttrName = "value_06";
		}else if("07".equals(hh)){
			originalDayWmValue = curDayWM.getValue_07()==null?"":curDayWM.getValue_07().toString();
			dayAttrName = "value_07";
		}else if("08".equals(hh)){
			originalDayWmValue = curDayWM.getValue_08()==null?"":curDayWM.getValue_08().toString();
			dayAttrName = "value_08";
		}else if("09".equals(hh)){
			originalDayWmValue = curDayWM.getValue_09()==null?"":curDayWM.getValue_09().toString();
			dayAttrName = "value_09";
		}else if("10".equals(hh)){
			originalDayWmValue = curDayWM.getValue_10()==null?"":curDayWM.getValue_10().toString();
			dayAttrName = "value_10";
		}else if("11".equals(hh)){
			originalDayWmValue = curDayWM.getValue_11()==null?"":curDayWM.getValue_11().toString();
			dayAttrName = "value_11";
		}else if("12".equals(hh)){
			originalDayWmValue = curDayWM.getValue_12()==null?"":curDayWM.getValue_12().toString();
			dayAttrName = "value_12";
		}else if("13".equals(hh)){
			originalDayWmValue = curDayWM.getValue_13()==null?"":curDayWM.getValue_13().toString();
			dayAttrName = "value_13";
		}else if("14".equals(hh)){
			originalDayWmValue = curDayWM.getValue_14()==null?"":curDayWM.getValue_14().toString();
			dayAttrName = "value_14";
		}else if("15".equals(hh)){
			originalDayWmValue = curDayWM.getValue_15()==null?"":curDayWM.getValue_15().toString();
			dayAttrName = "value_15";
		}else if("16".equals(hh)){
			originalDayWmValue = curDayWM.getValue_16()==null?"":curDayWM.getValue_16().toString();
			dayAttrName = "value_16";
		}else if("17".equals(hh)){
			originalDayWmValue = curDayWM.getValue_17()==null?"":curDayWM.getValue_17().toString();
			dayAttrName = "value_17";
		}else if("18".equals(hh)){
			originalDayWmValue = curDayWM.getValue_18()==null?"":curDayWM.getValue_18().toString();
			dayAttrName = "value_18";
		}else if("19".equals(hh)){
			originalDayWmValue = curDayWM.getValue_19()==null?"":curDayWM.getValue_19().toString();
			dayAttrName = "value_19";
		}else if("20".equals(hh)){
			originalDayWmValue = curDayWM.getValue_20()==null?"":curDayWM.getValue_20().toString();
			dayAttrName = "value_20";
		}else if("21".equals(hh)){
			originalDayWmValue = curDayWM.getValue_21()==null?"":curDayWM.getValue_21().toString();
			dayAttrName = "value_21";
		}else if("22".equals(hh)){
			originalDayWmValue = curDayWM.getValue_22()==null?"":curDayWM.getValue_22().toString();
			dayAttrName = "value_22";
		}else if("23".equals(hh)){
			originalDayWmValue = curDayWM.getValue_23()==null?"":curDayWM.getValue_23().toString();
			dayAttrName = "value_23";
		}else{
			//System.out.println("[VEEManageImpl.java] ~~> 잘못된 hh 값입니다.");
			returnData.put("returnValue", "wrong data !! => [hh]");
			return returnData;
		}
		
		insertVEELog_day(curDayWM, yyyymmdd, hh, CommonConstants.MeterType.GasMeter.getDayClassName(), dayAttrName, originalDayWmValue.toString(), hap.toString(), userId, editItem);
		
		
		if("00".equals(hh)){
			curDayWM.setValue_00(hap);
		}else if("01".equals(hh)){
			curDayWM.setValue_01(hap);
		}else if("02".equals(hh)){
			curDayWM.setValue_02(hap);
		}else if("03".equals(hh)){
			curDayWM.setValue_03(hap);
		}else if("04".equals(hh)){
			curDayWM.setValue_04(hap);
		}else if("05".equals(hh)){
			curDayWM.setValue_05(hap);
		}else if("06".equals(hh)){
			curDayWM.setValue_06(hap);
		}else if("07".equals(hh)){
			curDayWM.setValue_07(hap);
		}else if("08".equals(hh)){
			curDayWM.setValue_08(hap);
		}else if("09".equals(hh)){
			curDayWM.setValue_09(hap);
		}else if("10".equals(hh)){
			curDayWM.setValue_10(hap);
		}else if("11".equals(hh)){
			curDayWM.setValue_11(hap);
		}else if("12".equals(hh)){
			curDayWM.setValue_12(hap);
		}else if("13".equals(hh)){
			curDayWM.setValue_13(hap);
		}else if("14".equals(hh)){
			curDayWM.setValue_14(hap);
		}else if("15".equals(hh)){
			curDayWM.setValue_15(hap);
		}else if("16".equals(hh)){
			curDayWM.setValue_16(hap);
		}else if("17".equals(hh)){
			curDayWM.setValue_17(hap);
		}else if("18".equals(hh)){
			curDayWM.setValue_18(hap);
		}else if("19".equals(hh)){
			curDayWM.setValue_19(hap);
		}else if("20".equals(hh)){
			curDayWM.setValue_20(hap);
		}else if("21".equals(hh)){
			curDayWM.setValue_21(hap);
		}else if("22".equals(hh)){
			curDayWM.setValue_22(hap);
		}else if("23".equals(hh)){
			curDayWM.setValue_23(hap);
		}else{
			//System.out.println("[VEEManageImpl.java] ~~> 잘못된 hh 값입니다.");
			returnData.put("returnValue", "wrong data !! => [hh]");
			return returnData;
		}
		
		double curDayWMTotal = (curDayWM.getValue_00()==null?0.0:curDayWM.getValue_00()) +
								(curDayWM.getValue_01()==null?0.0:curDayWM.getValue_01()) +
								(curDayWM.getValue_02()==null?0.0:curDayWM.getValue_02()) + 
								(curDayWM.getValue_03()==null?0.0:curDayWM.getValue_03()) + 
								(curDayWM.getValue_04()==null?0.0:curDayWM.getValue_04()) + 
								(curDayWM.getValue_05()==null?0.0:curDayWM.getValue_05()) + 
								(curDayWM.getValue_06()==null?0.0:curDayWM.getValue_06()) + 
								(curDayWM.getValue_07()==null?0.0:curDayWM.getValue_07()) + 
								(curDayWM.getValue_08()==null?0.0:curDayWM.getValue_08()) + 
								(curDayWM.getValue_09()==null?0.0:curDayWM.getValue_09()) + 
								(curDayWM.getValue_10()==null?0.0:curDayWM.getValue_10()) +
								(curDayWM.getValue_11()==null?0.0:curDayWM.getValue_11()) +
								(curDayWM.getValue_12()==null?0.0:curDayWM.getValue_12()) + 
								(curDayWM.getValue_13()==null?0.0:curDayWM.getValue_13()) + 
								(curDayWM.getValue_14()==null?0.0:curDayWM.getValue_14()) + 
								(curDayWM.getValue_15()==null?0.0:curDayWM.getValue_15()) + 
								(curDayWM.getValue_16()==null?0.0:curDayWM.getValue_16()) + 
								(curDayWM.getValue_17()==null?0.0:curDayWM.getValue_17()) + 
								(curDayWM.getValue_18()==null?0.0:curDayWM.getValue_18()) + 
								(curDayWM.getValue_19()==null?0.0:curDayWM.getValue_19()) + 
								(curDayWM.getValue_20()==null?0.0:curDayWM.getValue_20()) +
								(curDayWM.getValue_21()==null?0.0:curDayWM.getValue_21()) +
								(curDayWM.getValue_22()==null?0.0:curDayWM.getValue_22()) + 
								(curDayWM.getValue_23()==null?0.0:curDayWM.getValue_23());
		
		curDayWM.setTotal(curDayWMTotal);
		
		//System.out.println("==> dayEMDao.update");
		
		dayWMDao.saveOrUpdate(curDayWM);
		/*2. day_xx table update end*/
		
		returnData.put("returnValue", curDayWM);
		return returnData;
	}
	
	
	public String monthemUpdate(String yyyymmdd, String channel, String mdevType, String mdevId, String dst, String[] params, String hh, String userId, DayEM currEM, String editItem){
		
		DayPk daypk = new DayPk();
		
		daypk.setYyyymmdd(yyyymmdd);
		daypk.setChannel(Integer.parseInt(channel));
		daypk.setMDevType(mdevType);
		daypk.setMDevId(mdevId);
		daypk.setDst(Integer.parseInt(dst));
		
		MonthPk monthpk = new MonthPk();
		
		monthpk.setYyyymm(yyyymmdd.substring(0, 6));
		monthpk.setChannel(Integer.parseInt(channel));
		monthpk.setMDevType(mdevType);
		monthpk.setMDevId(mdevId);
		monthpk.setDst(Integer.parseInt(dst));
		
		Set<Condition> setMonth = new HashSet<Condition>();
		setMonth.add(new Condition("id.yyyymm",new Object[]{monthpk.getYyyymm()},null,Restriction.EQ));
		setMonth.add(new Condition("id.channel",new Object[]{monthpk.getChannel()},null,Restriction.EQ));
		setMonth.add(new Condition("id.mdevType",new Object[]{monthpk.getMDevType()},null,Restriction.EQ));
		setMonth.add(new Condition("id.mdevId",new Object[]{monthpk.getMDevId()},null,Restriction.EQ));
		setMonth.add(new Condition("id.dst",new Object[]{monthpk.getDst()},null,Restriction.EQ));
		//원래의 day 데이터.
		
		//System.out.println("==> select curMonthEM start");
		MonthEM curMonthEM = null;
		
		List<MonthEM> monthemList = monthEMDao.getMonthEMsByListCondition(setMonth);
		if(monthemList.size() > 0){
			curMonthEM = monthemList.get(0);
		}else{
			curMonthEM = new MonthEM();
			curMonthEM.setId(monthpk);
			curMonthEM.setChannel(currEM.getChannel());
			curMonthEM.setContract(currEM.getContract());
			curMonthEM.setDeviceId(currEM.getDeviceId());
			
			if(currEM.getDeviceType()!=null)
				curMonthEM.setDeviceType(currEM.getDeviceType().toString());
			
			curMonthEM.setDst(currEM.getDst());
			curMonthEM.setEnddevice(currEM.getEnddevice());
			curMonthEM.setLocation(currEM.getLocation());
			curMonthEM.setMDevId(currEM.getMDevId());
			
			if(currEM.getMDevType()!=null)
				curMonthEM.setMDevType(currEM.getMDevType().toString());
			
			curMonthEM.setMeter(meterDao.get(currEM.getMeterId()));
			curMonthEM.setMeteringType(currEM.getMeteringType());
			curMonthEM.setModem(currEM.getModem());
			curMonthEM.setYyyymm(monthpk.getYyyymm());
			curMonthEM.setSupplier(currEM.getSupplier());
		}
		
		//System.out.println("==> select curMonthEM end");

		//System.out.println("==> select daySum start");
		String daySum = currEM.getTotal().toString();
		
		//System.out.println("==> select daySum end");
		String monthAttrName = "";
		String originalMonthEmValue = "";
		String day = yyyymmdd.substring(6,8);
		
		if("01".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_01()==null?"":curMonthEM.getValue_01().toString();
			monthAttrName = "value_01";
		}else if("02".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_02()==null?"":curMonthEM.getValue_02().toString();
			monthAttrName = "value_02";
		}else if("03".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_03()==null?"":curMonthEM.getValue_03().toString();
			monthAttrName = "value_03";
		}else if("04".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_04()==null?"":curMonthEM.getValue_04().toString();
			monthAttrName = "value_04";
		}else if("05".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_05()==null?"":curMonthEM.getValue_05().toString();
			monthAttrName = "value_05";
		}else if("06".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_06()==null?"":curMonthEM.getValue_06().toString();
			monthAttrName = "value_06";
		}else if("07".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_07()==null?"":curMonthEM.getValue_07().toString();
			monthAttrName = "value_07";
		}else if("08".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_08()==null?"":curMonthEM.getValue_08().toString();
			monthAttrName = "value_08";
		}else if("09".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_09()==null?"":curMonthEM.getValue_09().toString();
			monthAttrName = "value_09";
		}else if("10".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_10()==null?"":curMonthEM.getValue_10().toString();
			monthAttrName = "value_10";
		}else if("11".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_11()==null?"":curMonthEM.getValue_11().toString();
			monthAttrName = "value_11";
		}else if("12".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_12()==null?"":curMonthEM.getValue_12().toString();
			monthAttrName = "value_12";
		}else if("13".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_13()==null?"":curMonthEM.getValue_13().toString();
			monthAttrName = "value_13";
		}else if("14".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_14()==null?"":curMonthEM.getValue_14().toString();
			monthAttrName = "value_14";
		}else if("15".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_15()==null?"":curMonthEM.getValue_15().toString();
			monthAttrName = "value_15";
		}else if("16".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_16()==null?"":curMonthEM.getValue_16().toString();
			monthAttrName = "value_16";
		}else if("17".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_17()==null?"":curMonthEM.getValue_17().toString();
			monthAttrName = "value_17";
		}else if("18".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_18()==null?"":curMonthEM.getValue_18().toString();
			monthAttrName = "value_18";
		}else if("19".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_19()==null?"":curMonthEM.getValue_19().toString();
			monthAttrName = "value_19";
		}else if("20".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_20()==null?"":curMonthEM.getValue_20().toString();
			monthAttrName = "value_20";
		}else if("21".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_21()==null?"":curMonthEM.getValue_21().toString();
			monthAttrName = "value_21";
		}else if("22".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_22()==null?"":curMonthEM.getValue_22().toString();
			monthAttrName = "value_22";
		}else if("23".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_23()==null?"":curMonthEM.getValue_23().toString();
			monthAttrName = "value_23";
		}else if("24".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_24()==null?"":curMonthEM.getValue_24().toString();
			monthAttrName = "value_24";
		}else if("25".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_25()==null?"":curMonthEM.getValue_25().toString();
			monthAttrName = "value_25";
		}else if("26".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_26()==null?"":curMonthEM.getValue_26().toString();
			monthAttrName = "value_26";
		}else if("27".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_27()==null?"":curMonthEM.getValue_27().toString();
			monthAttrName = "value_27";
		}else if("28".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_28()==null?"":curMonthEM.getValue_28().toString();
			monthAttrName = "value_28";
		}else if("29".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_29()==null?"":curMonthEM.getValue_29().toString();
			monthAttrName = "value_29";
		}else if("30".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_30()==null?"":curMonthEM.getValue_30().toString();
			monthAttrName = "value_30";
		}else if("31".equals(day)){
			originalMonthEmValue = curMonthEM.getValue_31()==null?"":curMonthEM.getValue_31().toString();
			monthAttrName = "value_31";
		}else{
			//System.out.println("[VEEManageImpl.java] ~~> 잘못된 Double.parseDouble(daySum) 값입니다.");
			return "wrong data !! => [day]";
		}
		
		insertVEELog_month(curMonthEM, yyyymmdd, hh, CommonConstants.MeterType.EnergyMeter.getMonthClassName(), monthAttrName, originalMonthEmValue.toString(), daySum, userId, editItem);
		
		
		if("01".equals(day)){
			curMonthEM.setValue_01(Double.parseDouble(daySum));
		}else if("02".equals(day)){
			curMonthEM.setValue_02(Double.parseDouble(daySum));
		}else if("03".equals(day)){
			curMonthEM.setValue_03(Double.parseDouble(daySum));
		}else if("04".equals(day)){
			curMonthEM.setValue_04(Double.parseDouble(daySum));
		}else if("05".equals(day)){
			curMonthEM.setValue_05(Double.parseDouble(daySum));
		}else if("06".equals(day)){
			curMonthEM.setValue_06(Double.parseDouble(daySum));
		}else if("07".equals(day)){
			curMonthEM.setValue_07(Double.parseDouble(daySum));
		}else if("08".equals(day)){
			curMonthEM.setValue_08(Double.parseDouble(daySum));
		}else if("09".equals(day)){
			curMonthEM.setValue_09(Double.parseDouble(daySum));
		}else if("10".equals(day)){
			curMonthEM.setValue_10(Double.parseDouble(daySum));
		}else if("11".equals(day)){
			curMonthEM.setValue_11(Double.parseDouble(daySum));
		}else if("12".equals(day)){
			curMonthEM.setValue_12(Double.parseDouble(daySum));
		}else if("13".equals(day)){
			curMonthEM.setValue_13(Double.parseDouble(daySum));
		}else if("14".equals(day)){
			curMonthEM.setValue_14(Double.parseDouble(daySum));
		}else if("15".equals(day)){
			curMonthEM.setValue_15(Double.parseDouble(daySum));
		}else if("16".equals(day)){
			curMonthEM.setValue_16(Double.parseDouble(daySum));
		}else if("17".equals(day)){
			curMonthEM.setValue_17(Double.parseDouble(daySum));
		}else if("18".equals(day)){
			curMonthEM.setValue_18(Double.parseDouble(daySum));
		}else if("19".equals(day)){
			curMonthEM.setValue_19(Double.parseDouble(daySum));
		}else if("20".equals(day)){
			curMonthEM.setValue_20(Double.parseDouble(daySum));
		}else if("21".equals(day)){
			curMonthEM.setValue_21(Double.parseDouble(daySum));
		}else if("22".equals(day)){
			curMonthEM.setValue_22(Double.parseDouble(daySum));
		}else if("23".equals(day)){
			curMonthEM.setValue_23(Double.parseDouble(daySum));
		}else if("24".equals(day)){
			curMonthEM.setValue_24(Double.parseDouble(daySum));
		}else if("25".equals(day)){
			curMonthEM.setValue_25(Double.parseDouble(daySum));
		}else if("26".equals(day)){
			curMonthEM.setValue_26(Double.parseDouble(daySum));
		}else if("27".equals(day)){
			curMonthEM.setValue_27(Double.parseDouble(daySum));
		}else if("28".equals(day)){
			curMonthEM.setValue_28(Double.parseDouble(daySum));
		}else if("29".equals(day)){
			curMonthEM.setValue_29(Double.parseDouble(daySum));
		}else if("30".equals(day)){
			curMonthEM.setValue_30(Double.parseDouble(daySum));
		}else if("31".equals(day)){
			curMonthEM.setValue_31(Double.parseDouble(daySum));
		}else{
			//System.out.println("[VEEManageImpl.java] ~~> 잘못된 Double.parseDouble(daySum) 값입니다.");
			return "wrong data !! => [day]";
		}
		
		double curMonthEMTotal = 	(curMonthEM.getValue_01()==null?0.0:curMonthEM.getValue_01()) +
									(curMonthEM.getValue_02()==null?0.0:curMonthEM.getValue_02()) + 
									(curMonthEM.getValue_03()==null?0.0:curMonthEM.getValue_03()) + 
									(curMonthEM.getValue_04()==null?0.0:curMonthEM.getValue_04()) + 
									(curMonthEM.getValue_05()==null?0.0:curMonthEM.getValue_05()) + 
									(curMonthEM.getValue_06()==null?0.0:curMonthEM.getValue_06()) + 
									(curMonthEM.getValue_07()==null?0.0:curMonthEM.getValue_07()) + 
									(curMonthEM.getValue_08()==null?0.0:curMonthEM.getValue_08()) + 
									(curMonthEM.getValue_09()==null?0.0:curMonthEM.getValue_09()) + 
									(curMonthEM.getValue_10()==null?0.0:curMonthEM.getValue_10()) +
									(curMonthEM.getValue_11()==null?0.0:curMonthEM.getValue_11()) +
									(curMonthEM.getValue_12()==null?0.0:curMonthEM.getValue_12()) + 
									(curMonthEM.getValue_13()==null?0.0:curMonthEM.getValue_13()) + 
									(curMonthEM.getValue_14()==null?0.0:curMonthEM.getValue_14()) + 
									(curMonthEM.getValue_15()==null?0.0:curMonthEM.getValue_15()) + 
									(curMonthEM.getValue_16()==null?0.0:curMonthEM.getValue_16()) + 
									(curMonthEM.getValue_17()==null?0.0:curMonthEM.getValue_17()) + 
									(curMonthEM.getValue_18()==null?0.0:curMonthEM.getValue_18()) + 
									(curMonthEM.getValue_19()==null?0.0:curMonthEM.getValue_19()) + 
									(curMonthEM.getValue_20()==null?0.0:curMonthEM.getValue_20()) +
									(curMonthEM.getValue_21()==null?0.0:curMonthEM.getValue_21()) +
									(curMonthEM.getValue_22()==null?0.0:curMonthEM.getValue_22()) + 
									(curMonthEM.getValue_23()==null?0.0:curMonthEM.getValue_23()) +
									(curMonthEM.getValue_24()==null?0.0:curMonthEM.getValue_24()) + 
									(curMonthEM.getValue_25()==null?0.0:curMonthEM.getValue_25()) + 
									(curMonthEM.getValue_26()==null?0.0:curMonthEM.getValue_26()) + 
									(curMonthEM.getValue_27()==null?0.0:curMonthEM.getValue_27()) + 
									(curMonthEM.getValue_28()==null?0.0:curMonthEM.getValue_28()) + 
									(curMonthEM.getValue_29()==null?0.0:curMonthEM.getValue_29()) + 
									(curMonthEM.getValue_30()==null?0.0:curMonthEM.getValue_30()) +
									(curMonthEM.getValue_31()==null?0.0:curMonthEM.getValue_31()) ;
		
		curMonthEM.setTotal(curMonthEMTotal);
		
		monthEMDao.saveOrUpdate(curMonthEM);
		
		/*3. month_xx table update end*/
		return "";
	}
	
	public String monthgmUpdate(String yyyymmdd, String channel, String mdevType, String mdevId, String dst, String[] params, String hh, String userId, DayGM curGM, String editItem){
		
		DayPk daypk = new DayPk();
		
		daypk.setYyyymmdd(yyyymmdd);
		daypk.setChannel(Integer.parseInt(channel));
		daypk.setMDevType(mdevType);
		daypk.setMDevId(mdevId);
		daypk.setDst(Integer.parseInt(dst));
		
		MonthPk monthpk = new MonthPk();
		
		monthpk.setYyyymm(yyyymmdd.substring(0, 6));
		monthpk.setChannel(Integer.parseInt(channel));
		monthpk.setMDevType(mdevType);
		monthpk.setMDevId(mdevId);
		monthpk.setDst(Integer.parseInt(dst));
		
		Set<Condition> setMonth = new HashSet<Condition>();
		setMonth.add(new Condition("id.yyyymm",new Object[]{monthpk.getYyyymm()},null,Restriction.EQ));
		setMonth.add(new Condition("id.channel",new Object[]{monthpk.getChannel()},null,Restriction.EQ));
		setMonth.add(new Condition("id.mdevType",new Object[]{monthpk.getMDevType()},null,Restriction.EQ));
		setMonth.add(new Condition("id.mdevId",new Object[]{monthpk.getMDevId()},null,Restriction.EQ));
		setMonth.add(new Condition("id.dst",new Object[]{monthpk.getDst()},null,Restriction.EQ));
		//원래의 day 데이터.
		
		//System.out.println("==> select curMonthEM start");
		MonthGM curMonthGM = null;
		
		List<MonthGM> monthemList = monthGMDao.getMonthGMsByListCondition(setMonth);
		if(monthemList.size() > 0){
			curMonthGM = monthemList.get(0);
		}else{
			curMonthGM = new MonthGM();
			curMonthGM.setId(monthpk);
			curMonthGM.setChannel(curGM.getChannel());
			curMonthGM.setContract(curGM.getContract());
			curMonthGM.setDeviceId(curGM.getDeviceId());
			
			if(curGM.getDeviceType()!=null)
				curMonthGM.setDeviceType(curGM.getDeviceType().toString());
			
			curMonthGM.setDst(curGM.getDst());
			curMonthGM.setEnddevice(curGM.getEnddevice());
			curMonthGM.setLocation(curGM.getLocation());
			curMonthGM.setMDevId(curGM.getMDevId());
			
			if(curGM.getMDevType()!=null)
				curMonthGM.setMDevType(curGM.getMDevType().toString());
			
			curMonthGM.setMeter(curGM.getMeter());
			curMonthGM.setMeteringType(curGM.getMeteringType());
			curMonthGM.setModem(curGM.getModem());
			curMonthGM.setYyyymm(monthpk.getYyyymm());
			curMonthGM.setSupplier(curGM.getSupplier());
		}
		
		//System.out.println("==> select curMonthEM end");

		//System.out.println("==> select daySum start");
		String daySum = curGM.getTotal().toString();
		
		//System.out.println("==> select daySum end");
		String monthAttrName = "";
		String originalMonthGmValue = "";
		String day = yyyymmdd.substring(6,8);
		
		if("01".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_01()==null?"":curMonthGM.getValue_01().toString();
			monthAttrName = "value_01";
		}else if("02".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_02()==null?"":curMonthGM.getValue_02().toString();
			monthAttrName = "value_02";
		}else if("03".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_03()==null?"":curMonthGM.getValue_03().toString();
			monthAttrName = "value_03";
		}else if("04".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_04()==null?"":curMonthGM.getValue_04().toString();
			monthAttrName = "value_04";
		}else if("05".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_05()==null?"":curMonthGM.getValue_05().toString();
			monthAttrName = "value_05";
		}else if("06".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_06()==null?"":curMonthGM.getValue_06().toString();
			monthAttrName = "value_06";
		}else if("07".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_07()==null?"":curMonthGM.getValue_07().toString();
			monthAttrName = "value_07";
		}else if("08".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_08()==null?"":curMonthGM.getValue_08().toString();
			monthAttrName = "value_08";
		}else if("09".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_09()==null?"":curMonthGM.getValue_09().toString();
			monthAttrName = "value_09";
		}else if("10".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_10()==null?"":curMonthGM.getValue_10().toString();
			monthAttrName = "value_10";
		}else if("11".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_11()==null?"":curMonthGM.getValue_11().toString();
			monthAttrName = "value_11";
		}else if("12".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_12()==null?"":curMonthGM.getValue_12().toString();
			monthAttrName = "value_12";
		}else if("13".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_13()==null?"":curMonthGM.getValue_13().toString();
			monthAttrName = "value_13";
		}else if("14".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_14()==null?"":curMonthGM.getValue_14().toString();
			monthAttrName = "value_14";
		}else if("15".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_15()==null?"":curMonthGM.getValue_15().toString();
			monthAttrName = "value_15";
		}else if("16".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_16()==null?"":curMonthGM.getValue_16().toString();
			monthAttrName = "value_16";
		}else if("17".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_17()==null?"":curMonthGM.getValue_17().toString();
			monthAttrName = "value_17";
		}else if("18".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_18()==null?"":curMonthGM.getValue_18().toString();
			monthAttrName = "value_18";
		}else if("19".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_19()==null?"":curMonthGM.getValue_19().toString();
			monthAttrName = "value_19";
		}else if("20".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_20()==null?"":curMonthGM.getValue_20().toString();
			monthAttrName = "value_20";
		}else if("21".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_21()==null?"":curMonthGM.getValue_21().toString();
			monthAttrName = "value_21";
		}else if("22".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_22()==null?"":curMonthGM.getValue_22().toString();
			monthAttrName = "value_22";
		}else if("23".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_23()==null?"":curMonthGM.getValue_23().toString();
			monthAttrName = "value_23";
		}else if("24".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_24()==null?"":curMonthGM.getValue_24().toString();
			monthAttrName = "value_24";
		}else if("25".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_25()==null?"":curMonthGM.getValue_25().toString();
			monthAttrName = "value_25";
		}else if("26".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_26()==null?"":curMonthGM.getValue_26().toString();
			monthAttrName = "value_26";
		}else if("27".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_27()==null?"":curMonthGM.getValue_27().toString();
			monthAttrName = "value_27";
		}else if("28".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_28()==null?"":curMonthGM.getValue_28().toString();
			monthAttrName = "value_28";
		}else if("29".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_29()==null?"":curMonthGM.getValue_29().toString();
			monthAttrName = "value_29";
		}else if("30".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_30()==null?"":curMonthGM.getValue_30().toString();
			monthAttrName = "value_30";
		}else if("31".equals(day)){
			originalMonthGmValue = curMonthGM.getValue_31()==null?"":curMonthGM.getValue_31().toString();
			monthAttrName = "value_31";
		}else{
			//System.out.println("[VEEManageImpl.java] ~~> 잘못된 Double.parseDouble(daySum) 값입니다.");
			return "wrong data !! => [day]";
		}
		
		insertVEELog_month(curMonthGM, yyyymmdd, hh, CommonConstants.MeterType.GasMeter.getMonthClassName(), monthAttrName, originalMonthGmValue.toString(), daySum, userId, editItem);
		
		
		if("01".equals(day)){
			curMonthGM.setValue_01(Double.parseDouble(daySum));
		}else if("02".equals(day)){
			curMonthGM.setValue_02(Double.parseDouble(daySum));
		}else if("03".equals(day)){
			curMonthGM.setValue_03(Double.parseDouble(daySum));
		}else if("04".equals(day)){
			curMonthGM.setValue_04(Double.parseDouble(daySum));
		}else if("05".equals(day)){
			curMonthGM.setValue_05(Double.parseDouble(daySum));
		}else if("06".equals(day)){
			curMonthGM.setValue_06(Double.parseDouble(daySum));
		}else if("07".equals(day)){
			curMonthGM.setValue_07(Double.parseDouble(daySum));
		}else if("08".equals(day)){
			curMonthGM.setValue_08(Double.parseDouble(daySum));
		}else if("09".equals(day)){
			curMonthGM.setValue_09(Double.parseDouble(daySum));
		}else if("10".equals(day)){
			curMonthGM.setValue_10(Double.parseDouble(daySum));
		}else if("11".equals(day)){
			curMonthGM.setValue_11(Double.parseDouble(daySum));
		}else if("12".equals(day)){
			curMonthGM.setValue_12(Double.parseDouble(daySum));
		}else if("13".equals(day)){
			curMonthGM.setValue_13(Double.parseDouble(daySum));
		}else if("14".equals(day)){
			curMonthGM.setValue_14(Double.parseDouble(daySum));
		}else if("15".equals(day)){
			curMonthGM.setValue_15(Double.parseDouble(daySum));
		}else if("16".equals(day)){
			curMonthGM.setValue_16(Double.parseDouble(daySum));
		}else if("17".equals(day)){
			curMonthGM.setValue_17(Double.parseDouble(daySum));
		}else if("18".equals(day)){
			curMonthGM.setValue_18(Double.parseDouble(daySum));
		}else if("19".equals(day)){
			curMonthGM.setValue_19(Double.parseDouble(daySum));
		}else if("20".equals(day)){
			curMonthGM.setValue_20(Double.parseDouble(daySum));
		}else if("21".equals(day)){
			curMonthGM.setValue_21(Double.parseDouble(daySum));
		}else if("22".equals(day)){
			curMonthGM.setValue_22(Double.parseDouble(daySum));
		}else if("23".equals(day)){
			curMonthGM.setValue_23(Double.parseDouble(daySum));
		}else if("24".equals(day)){
			curMonthGM.setValue_24(Double.parseDouble(daySum));
		}else if("25".equals(day)){
			curMonthGM.setValue_25(Double.parseDouble(daySum));
		}else if("26".equals(day)){
			curMonthGM.setValue_26(Double.parseDouble(daySum));
		}else if("27".equals(day)){
			curMonthGM.setValue_27(Double.parseDouble(daySum));
		}else if("28".equals(day)){
			curMonthGM.setValue_28(Double.parseDouble(daySum));
		}else if("29".equals(day)){
			curMonthGM.setValue_29(Double.parseDouble(daySum));
		}else if("30".equals(day)){
			curMonthGM.setValue_30(Double.parseDouble(daySum));
		}else if("31".equals(day)){
			curMonthGM.setValue_31(Double.parseDouble(daySum));
		}else{
			//System.out.println("[VEEManageImpl.java] ~~> 잘못된 Double.parseDouble(daySum) 값입니다.");
			return "wrong data !! => [day]";
		}
		
		double curMonthGMTotal = 	(curMonthGM.getValue_01()==null?0.0:curMonthGM.getValue_01()) +
									(curMonthGM.getValue_02()==null?0.0:curMonthGM.getValue_02()) + 
									(curMonthGM.getValue_03()==null?0.0:curMonthGM.getValue_03()) + 
									(curMonthGM.getValue_04()==null?0.0:curMonthGM.getValue_04()) + 
									(curMonthGM.getValue_05()==null?0.0:curMonthGM.getValue_05()) + 
									(curMonthGM.getValue_06()==null?0.0:curMonthGM.getValue_06()) + 
									(curMonthGM.getValue_07()==null?0.0:curMonthGM.getValue_07()) + 
									(curMonthGM.getValue_08()==null?0.0:curMonthGM.getValue_08()) + 
									(curMonthGM.getValue_09()==null?0.0:curMonthGM.getValue_09()) + 
									(curMonthGM.getValue_10()==null?0.0:curMonthGM.getValue_10()) +
									(curMonthGM.getValue_11()==null?0.0:curMonthGM.getValue_11()) +
									(curMonthGM.getValue_12()==null?0.0:curMonthGM.getValue_12()) + 
									(curMonthGM.getValue_13()==null?0.0:curMonthGM.getValue_13()) + 
									(curMonthGM.getValue_14()==null?0.0:curMonthGM.getValue_14()) + 
									(curMonthGM.getValue_15()==null?0.0:curMonthGM.getValue_15()) + 
									(curMonthGM.getValue_16()==null?0.0:curMonthGM.getValue_16()) + 
									(curMonthGM.getValue_17()==null?0.0:curMonthGM.getValue_17()) + 
									(curMonthGM.getValue_18()==null?0.0:curMonthGM.getValue_18()) + 
									(curMonthGM.getValue_19()==null?0.0:curMonthGM.getValue_19()) + 
									(curMonthGM.getValue_20()==null?0.0:curMonthGM.getValue_20()) +
									(curMonthGM.getValue_21()==null?0.0:curMonthGM.getValue_21()) +
									(curMonthGM.getValue_22()==null?0.0:curMonthGM.getValue_22()) + 
									(curMonthGM.getValue_23()==null?0.0:curMonthGM.getValue_23()) +
									(curMonthGM.getValue_24()==null?0.0:curMonthGM.getValue_24()) + 
									(curMonthGM.getValue_25()==null?0.0:curMonthGM.getValue_25()) + 
									(curMonthGM.getValue_26()==null?0.0:curMonthGM.getValue_26()) + 
									(curMonthGM.getValue_27()==null?0.0:curMonthGM.getValue_27()) + 
									(curMonthGM.getValue_28()==null?0.0:curMonthGM.getValue_28()) + 
									(curMonthGM.getValue_29()==null?0.0:curMonthGM.getValue_29()) + 
									(curMonthGM.getValue_30()==null?0.0:curMonthGM.getValue_30()) +
									(curMonthGM.getValue_31()==null?0.0:curMonthGM.getValue_31()) ;
		
		curMonthGM.setTotal(curMonthGMTotal);
		
		monthGMDao.saveOrUpdate(curMonthGM);
		
		/*3. month_xx table update end*/
		return "";
	}
	
	public String monthwmUpdate(String yyyymmdd, String channel, String mdevType, String mdevId, String dst, String[] params, String hh, String userId, DayWM curWM, String editItem){
		
		DayPk daypk = new DayPk();
		
		daypk.setYyyymmdd(yyyymmdd);
		daypk.setChannel(Integer.parseInt(channel));
		daypk.setMDevType(mdevType);
		daypk.setMDevId(mdevId);
		daypk.setDst(Integer.parseInt(dst));
		
		MonthPk monthpk = new MonthPk();
		
		monthpk.setYyyymm(yyyymmdd.substring(0, 6));
		monthpk.setChannel(Integer.parseInt(channel));
		monthpk.setMDevType(mdevType);
		monthpk.setMDevId(mdevId);
		monthpk.setDst(Integer.parseInt(dst));
		
		Set<Condition> setMonth = new HashSet<Condition>();
		setMonth.add(new Condition("id.yyyymm",new Object[]{monthpk.getYyyymm()},null,Restriction.EQ));
		setMonth.add(new Condition("id.channel",new Object[]{monthpk.getChannel()},null,Restriction.EQ));
		setMonth.add(new Condition("id.mdevType",new Object[]{monthpk.getMDevType()},null,Restriction.EQ));
		setMonth.add(new Condition("id.mdevId",new Object[]{monthpk.getMDevId()},null,Restriction.EQ));
		setMonth.add(new Condition("id.dst",new Object[]{monthpk.getDst()},null,Restriction.EQ));
		//원래의 day 데이터.
		
		//System.out.println("==> select curMonthEM start");
		MonthWM curMonthWM = null;
		
		List<MonthWM> monthemList = monthWMDao.getMonthWMsByListCondition(setMonth);
		if(monthemList.size() > 0){
			curMonthWM = monthemList.get(0);
		}else{
			curMonthWM = new MonthWM();
			curMonthWM.setId(monthpk);
			curMonthWM.setChannel(curWM.getChannel());
			curMonthWM.setContract(curWM.getContract());
			curMonthWM.setDeviceId(curWM.getDeviceId());
			
			if(curWM.getDeviceType()!=null)
				curMonthWM.setDeviceType(curWM.getDeviceType().toString());
			
			curMonthWM.setDst(curWM.getDst());
			curMonthWM.setEnddevice(curWM.getEnddevice());
			curMonthWM.setLocation(curWM.getLocation());
			curMonthWM.setMDevId(curWM.getMDevId());
			
			if(curWM.getMDevType()!=null)
				curMonthWM.setMDevType(curWM.getMDevType().toString());
			
			curMonthWM.setMeter(curWM.getMeter());
			curMonthWM.setMeteringType(curWM.getMeteringType());
			curMonthWM.setModem(curWM.getModem());
			curMonthWM.setYyyymm(monthpk.getYyyymm());
			curMonthWM.setSupplier(curWM.getSupplier());
			
		}
		
		//System.out.println("==> select curMonthEM end");

		//System.out.println("==> select daySum start");
		String daySum = curWM.getTotal().toString();
		
		//System.out.println("==> select daySum end");
		String monthAttrName = "";
		String originalMonthWmValue = "";
		String day = yyyymmdd.substring(6,8);
		
		if("01".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_01()==null?"":curMonthWM.getValue_01().toString();
			monthAttrName = "value_01";
		}else if("02".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_02()==null?"":curMonthWM.getValue_02().toString();
			monthAttrName = "value_02";
		}else if("03".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_03()==null?"":curMonthWM.getValue_03().toString();
			monthAttrName = "value_03";
		}else if("04".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_04()==null?"":curMonthWM.getValue_04().toString();
			monthAttrName = "value_04";
		}else if("05".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_05()==null?"":curMonthWM.getValue_05().toString();
			monthAttrName = "value_05";
		}else if("06".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_06()==null?"":curMonthWM.getValue_06().toString();
			monthAttrName = "value_06";
		}else if("07".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_07()==null?"":curMonthWM.getValue_07().toString();
			monthAttrName = "value_07";
		}else if("08".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_08()==null?"":curMonthWM.getValue_08().toString();
			monthAttrName = "value_08";
		}else if("09".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_09()==null?"":curMonthWM.getValue_09().toString();
			monthAttrName = "value_09";
		}else if("10".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_10()==null?"":curMonthWM.getValue_10().toString();
			monthAttrName = "value_10";
		}else if("11".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_11()==null?"":curMonthWM.getValue_11().toString();
			monthAttrName = "value_11";
		}else if("12".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_12()==null?"":curMonthWM.getValue_12().toString();
			monthAttrName = "value_12";
		}else if("13".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_13()==null?"":curMonthWM.getValue_13().toString();
			monthAttrName = "value_13";
		}else if("14".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_14()==null?"":curMonthWM.getValue_14().toString();
			monthAttrName = "value_14";
		}else if("15".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_15()==null?"":curMonthWM.getValue_15().toString();
			monthAttrName = "value_15";
		}else if("16".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_16()==null?"":curMonthWM.getValue_16().toString();
			monthAttrName = "value_16";
		}else if("17".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_17()==null?"":curMonthWM.getValue_17().toString();
			monthAttrName = "value_17";
		}else if("18".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_18()==null?"":curMonthWM.getValue_18().toString();
			monthAttrName = "value_18";
		}else if("19".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_19()==null?"":curMonthWM.getValue_19().toString();
			monthAttrName = "value_19";
		}else if("20".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_20()==null?"":curMonthWM.getValue_20().toString();
			monthAttrName = "value_20";
		}else if("21".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_21()==null?"":curMonthWM.getValue_21().toString();
			monthAttrName = "value_21";
		}else if("22".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_22()==null?"":curMonthWM.getValue_22().toString();
			monthAttrName = "value_22";
		}else if("23".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_23()==null?"":curMonthWM.getValue_23().toString();
			monthAttrName = "value_23";
		}else if("24".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_24()==null?"":curMonthWM.getValue_24().toString();
			monthAttrName = "value_24";
		}else if("25".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_25()==null?"":curMonthWM.getValue_25().toString();
			monthAttrName = "value_25";
		}else if("26".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_26()==null?"":curMonthWM.getValue_26().toString();
			monthAttrName = "value_26";
		}else if("27".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_27()==null?"":curMonthWM.getValue_27().toString();
			monthAttrName = "value_27";
		}else if("28".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_28()==null?"":curMonthWM.getValue_28().toString();
			monthAttrName = "value_28";
		}else if("29".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_29()==null?"":curMonthWM.getValue_29().toString();
			monthAttrName = "value_29";
		}else if("30".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_30()==null?"":curMonthWM.getValue_30().toString();
			monthAttrName = "value_30";
		}else if("31".equals(day)){
			originalMonthWmValue = curMonthWM.getValue_31()==null?"":curMonthWM.getValue_31().toString();
			monthAttrName = "value_31";
		}else{
			//System.out.println("[VEEManageImpl.java] ~~> 잘못된 Double.parseDouble(daySum) 값입니다.");
			return "wrong data !! => [day]";
		}
		
		insertVEELog_month(curMonthWM, yyyymmdd, hh, CommonConstants.MeterType.GasMeter.getMonthClassName(), monthAttrName, originalMonthWmValue.toString(), daySum, userId, editItem);
		
		
		if("01".equals(day)){
			curMonthWM.setValue_01(Double.parseDouble(daySum));
		}else if("02".equals(day)){
			curMonthWM.setValue_02(Double.parseDouble(daySum));
		}else if("03".equals(day)){
			curMonthWM.setValue_03(Double.parseDouble(daySum));
		}else if("04".equals(day)){
			curMonthWM.setValue_04(Double.parseDouble(daySum));
		}else if("05".equals(day)){
			curMonthWM.setValue_05(Double.parseDouble(daySum));
		}else if("06".equals(day)){
			curMonthWM.setValue_06(Double.parseDouble(daySum));
		}else if("07".equals(day)){
			curMonthWM.setValue_07(Double.parseDouble(daySum));
		}else if("08".equals(day)){
			curMonthWM.setValue_08(Double.parseDouble(daySum));
		}else if("09".equals(day)){
			curMonthWM.setValue_09(Double.parseDouble(daySum));
		}else if("10".equals(day)){
			curMonthWM.setValue_10(Double.parseDouble(daySum));
		}else if("11".equals(day)){
			curMonthWM.setValue_11(Double.parseDouble(daySum));
		}else if("12".equals(day)){
			curMonthWM.setValue_12(Double.parseDouble(daySum));
		}else if("13".equals(day)){
			curMonthWM.setValue_13(Double.parseDouble(daySum));
		}else if("14".equals(day)){
			curMonthWM.setValue_14(Double.parseDouble(daySum));
		}else if("15".equals(day)){
			curMonthWM.setValue_15(Double.parseDouble(daySum));
		}else if("16".equals(day)){
			curMonthWM.setValue_16(Double.parseDouble(daySum));
		}else if("17".equals(day)){
			curMonthWM.setValue_17(Double.parseDouble(daySum));
		}else if("18".equals(day)){
			curMonthWM.setValue_18(Double.parseDouble(daySum));
		}else if("19".equals(day)){
			curMonthWM.setValue_19(Double.parseDouble(daySum));
		}else if("20".equals(day)){
			curMonthWM.setValue_20(Double.parseDouble(daySum));
		}else if("21".equals(day)){
			curMonthWM.setValue_21(Double.parseDouble(daySum));
		}else if("22".equals(day)){
			curMonthWM.setValue_22(Double.parseDouble(daySum));
		}else if("23".equals(day)){
			curMonthWM.setValue_23(Double.parseDouble(daySum));
		}else if("24".equals(day)){
			curMonthWM.setValue_24(Double.parseDouble(daySum));
		}else if("25".equals(day)){
			curMonthWM.setValue_25(Double.parseDouble(daySum));
		}else if("26".equals(day)){
			curMonthWM.setValue_26(Double.parseDouble(daySum));
		}else if("27".equals(day)){
			curMonthWM.setValue_27(Double.parseDouble(daySum));
		}else if("28".equals(day)){
			curMonthWM.setValue_28(Double.parseDouble(daySum));
		}else if("29".equals(day)){
			curMonthWM.setValue_29(Double.parseDouble(daySum));
		}else if("30".equals(day)){
			curMonthWM.setValue_30(Double.parseDouble(daySum));
		}else if("31".equals(day)){
			curMonthWM.setValue_31(Double.parseDouble(daySum));
		}else{
			//System.out.println("[VEEManageImpl.java] ~~> 잘못된 Double.parseDouble(daySum) 값입니다.");
			return "wrong data !! => [day]";
		}
		
		double curMonthWMTotal = 	(curMonthWM.getValue_01()==null?0.0:curMonthWM.getValue_01()) +
									(curMonthWM.getValue_02()==null?0.0:curMonthWM.getValue_02()) + 
									(curMonthWM.getValue_03()==null?0.0:curMonthWM.getValue_03()) + 
									(curMonthWM.getValue_04()==null?0.0:curMonthWM.getValue_04()) + 
									(curMonthWM.getValue_05()==null?0.0:curMonthWM.getValue_05()) + 
									(curMonthWM.getValue_06()==null?0.0:curMonthWM.getValue_06()) + 
									(curMonthWM.getValue_07()==null?0.0:curMonthWM.getValue_07()) + 
									(curMonthWM.getValue_08()==null?0.0:curMonthWM.getValue_08()) + 
									(curMonthWM.getValue_09()==null?0.0:curMonthWM.getValue_09()) + 
									(curMonthWM.getValue_10()==null?0.0:curMonthWM.getValue_10()) +
									(curMonthWM.getValue_11()==null?0.0:curMonthWM.getValue_11()) +
									(curMonthWM.getValue_12()==null?0.0:curMonthWM.getValue_12()) + 
									(curMonthWM.getValue_13()==null?0.0:curMonthWM.getValue_13()) + 
									(curMonthWM.getValue_14()==null?0.0:curMonthWM.getValue_14()) + 
									(curMonthWM.getValue_15()==null?0.0:curMonthWM.getValue_15()) + 
									(curMonthWM.getValue_16()==null?0.0:curMonthWM.getValue_16()) + 
									(curMonthWM.getValue_17()==null?0.0:curMonthWM.getValue_17()) + 
									(curMonthWM.getValue_18()==null?0.0:curMonthWM.getValue_18()) + 
									(curMonthWM.getValue_19()==null?0.0:curMonthWM.getValue_19()) + 
									(curMonthWM.getValue_20()==null?0.0:curMonthWM.getValue_20()) +
									(curMonthWM.getValue_21()==null?0.0:curMonthWM.getValue_21()) +
									(curMonthWM.getValue_22()==null?0.0:curMonthWM.getValue_22()) + 
									(curMonthWM.getValue_23()==null?0.0:curMonthWM.getValue_23()) +
									(curMonthWM.getValue_24()==null?0.0:curMonthWM.getValue_24()) + 
									(curMonthWM.getValue_25()==null?0.0:curMonthWM.getValue_25()) + 
									(curMonthWM.getValue_26()==null?0.0:curMonthWM.getValue_26()) + 
									(curMonthWM.getValue_27()==null?0.0:curMonthWM.getValue_27()) + 
									(curMonthWM.getValue_28()==null?0.0:curMonthWM.getValue_28()) + 
									(curMonthWM.getValue_29()==null?0.0:curMonthWM.getValue_29()) + 
									(curMonthWM.getValue_30()==null?0.0:curMonthWM.getValue_30()) +
									(curMonthWM.getValue_31()==null?0.0:curMonthWM.getValue_31()) ;
		
		curMonthWM.setTotal(curMonthWMTotal);
		
		monthWMDao.saveOrUpdate(curMonthWM);
		
		/*3. month_xx table update end*/
		return "";
	}
	
	public void insertVEELog_Lp(MeteringLP currLp, String yyyymmdd, String hh, String table, String attrName, String beforValue, String afterValue, String userId, LpPk lppk, String editItem){
		/*4. vee_log insert start*/
	
			VEELog veelog = new VEELog();
			if(currLp.getMeter() != null){
				Meter meter = meterDao.get(currLp.getMeterId());
				veelog.setSupplier(meter.getSupplier());
			}else{
				Operator op = operatorDao.get(new Integer(Integer.parseInt(userId)));
				veelog.setSupplier(op.getSupplier());
			}
			if(currLp.getContractId() != null) {
				veelog.setContract(contractDao.get(currLp.getContractId()));
			}
			veelog.setMDevId(currLp.getMDevId());
			if(currLp.getMDevType() != null) veelog.setMDevType(currLp.getMDevType().toString());
			veelog.setDst(currLp.getDst());
			if(currLp.getLocationId() != null) {
				veelog.setLocation(locationDao.get(currLp.getLocationId()));
			}
			veelog.setYyyymmdd(yyyymmdd); 
			veelog.setHh(hh);
			veelog.setWriteDate(TimeUtil.getCurrentTimeMilli().substring(0, 14));
			veelog.setTableName(table);
			veelog.setAttrName(attrName);
			veelog.setBeforeValue(beforValue);
			veelog.setAfterValue(afterValue);
			if(currLp.getId() != null)veelog.setChannel(currLp.getId().getChannel());
			
			//ResultStatus.SUCCESS
			veelog.setResult(ResultStatus.SUCCESS);
			
			veelog.setOperatorType(OperatorType.OPERATOR);
			
			veelog.setOperator(userId);
			veelog.setDescr("");
			veelog.setEditItem(editItem); //새로 등록한 경우 : UserDefinedEstimated(2),  기존 데이터 수정한 경우 : IndividualEdited(4)
			
			//System.out.println("=> insertVEELog_LpEM start");
			vEELogDao.add(veelog);
			//System.out.println("=> insertVEELog_LpEM end");
			/*4. vee_log insert end*/
	}
	
	public void insertVEELog_day(MeteringDay currDay, String yyyymmdd, String hh, String table, String attrName, String beforValue, String afterValue, String userId, String editItem){
		/*4. vee_log insert start*/
		VEELog veelog = new VEELog();
		
		if(currDay.getMeter() != null){ 
			Meter meter = meterDao.get(currDay.getMeterId());
			veelog.setSupplier(meter.getSupplier());
		}else{
			Operator op = operatorDao.get(Integer.valueOf(userId));
			veelog.setSupplier(op.getSupplier());
		}
		if(currDay.getContractId() != null) {
			veelog.setContract(contractDao.get(currDay.getContractId()));
		}
		veelog.setMDevId(currDay.getMDevId());
		if(currDay.getMDevType() != null) veelog.setMDevType(currDay.getMDevType().toString());
		veelog.setDst(currDay.getDst());
		if(currDay.getLocationId() != null) {
			veelog.setLocation(locationDao.get(currDay.getLocationId()));
		}
		veelog.setYyyymmdd(yyyymmdd); 
		veelog.setHh(hh);
		veelog.setWriteDate(TimeUtil.getCurrentTimeMilli().substring(0, 14));
		veelog.setTableName(table);
		veelog.setAttrName(attrName);
		veelog.setBeforeValue(beforValue);
		veelog.setAfterValue(afterValue);
		if(currDay.getId() != null) veelog.setChannel(currDay.getId().getChannel());
		
		//ResultStatus.SUCCESS
		veelog.setResult(ResultStatus.SUCCESS);
		
		veelog.setOperatorType(OperatorType.OPERATOR);
		
		veelog.setOperator(userId);
		veelog.setDescr("");
		veelog.setEditItem(editItem); //새로 등록한 경우 : UserDefinedEstimated(2),  기존 데이터 수정한 경우 : IndividualEdited(4)
		
		vEELogDao.codeParentAdd(veelog);
		/*4. vee_log insert end*/
	}
	
	public void insertVEELog_month(MeteringMonth curMonth, String yyyymmdd, String hh, String table, String attrName, String beforValue, String afterValue, String userId, String editItem){
/*4. vee_log insert start*/
		VEELog veelog = new VEELog();
		
		if(curMonth.getMeter() == null){
			Operator op = operatorDao.get(Integer.valueOf(userId));
			veelog.setSupplier(op.getSupplier());
		}else{
			Meter meter = meterDao.get(curMonth.getMeterId());
			veelog.setSupplier(meter.getSupplier());
		}
		if(curMonth.getContractId() != null) {
			veelog.setContract(contractDao.get(curMonth.getContractId()));
		}
		veelog.setMDevId(curMonth.getMDevId());
		if(curMonth.getMDevType() != null) veelog.setMDevType(curMonth.getMDevType().toString());
		veelog.setDst(curMonth.getDst());
		if(curMonth.getLocationId() != null) {
			veelog.setLocation(locationDao.get(curMonth.getLocationId()));
		}
		veelog.setYyyymmdd(yyyymmdd); 
		veelog.setHh(hh);
		veelog.setWriteDate(TimeUtil.getCurrentTimeMilli().substring(0, 14));
		veelog.setTableName(table);
		veelog.setAttrName(attrName);
		veelog.setBeforeValue(beforValue);
		veelog.setAfterValue(afterValue);
		if(curMonth.getId() != null) veelog.setChannel(curMonth.getId().getChannel());
		
		//ResultStatus.SUCCESS
		veelog.setResult(ResultStatus.SUCCESS);
		
		veelog.setOperatorType(OperatorType.OPERATOR);
		
		veelog.setOperator(userId);
		veelog.setDescr("");
		veelog.setEditItem(editItem); //새로 등록한 경우 : UserDefinedEstimated(2),  기존 데이터 수정한 경우 : IndividualEdited(4)
		
		vEELogDao.codeParentAdd(veelog);
		/*4. vee_log insert end*/
	}


	public Map<String, Object> getPreviewAutoEstimation(Map<String, Object> conditions) {
		
		String meterType = StringUtil.nullToBlank(conditions.get("meterType"));
		String item = StringUtil.nullToBlank(conditions.get("item"));
		String yyyymmdd = StringUtil.nullToBlank(conditions.get("yyyymmdd"));
		String channel = StringUtil.nullToBlank(conditions.get("channel"));
		String mdevType = StringUtil.nullToBlank(conditions.get("mdevType"));
		String mdevId = StringUtil.nullToBlank(conditions.get("mdevId"));
		String dst = StringUtil.nullToBlank(conditions.get("dst"));
		String supplierId = StringUtil.nullToBlank(conditions.get("supplierId"));
		String type1 = StringUtil.nullToBlank(conditions.get("type1"));
		String type2 = StringUtil.nullToBlank(conditions.get("type2"));
		
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		
		String lpClassName = "";
		int lpInterval = 60;
		
		List<VEEMaxDetailData> lpList = validateAutoEstimation(meterType, item, yyyymmdd, channel, mdevType, mdevId, dst, type1, supplierId);		
		if( lpList != null) {
			
			DecimalFormat df = new DecimalFormat(supplier.getMd().getPattern());
			
			lpClassName 	= CommonConstants.MeterType.valueOf(meterType).getLpClassName();
			
			if(CommonConstants.MeterType.EnergyMeter.getLpClassName().equals(lpClassName)){
				try {
					lpInterval = lpEMDao.getLpInterval(mdevId);
				} catch(Exception e) {}				
			} else if(CommonConstants.MeterType.GasMeter.getLpClassName().equals(lpClassName)){
				try {
					lpInterval = lpGMDao.getLpInterval(mdevId);
				} catch(Exception e) {}				
			} else if(CommonConstants.MeterType.WaterMeter.getLpClassName().equals(lpClassName)){
				try {
					lpInterval = lpWMDao.getLpInterval(mdevId);
				} catch(Exception e) {}				
			}
			
			if(type2.equals("avg")) { // 평균 값으로 적용
				
				double totalValue[] = new double[60];
				
				for(int i=0; i<lpList.size(); i++) {
					VEEMaxDetailData data = lpList.get(i);					
					for(int j=0; j<60 ; j++) {
						try {
							String value = String.valueOf(VEEMaxDetailData.class.getMethod("getValue_" + String.format("%02d", j)).invoke(data));
							if(value.length() > 0) {
								totalValue[j] += Double.parseDouble(value);
							}
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						}
					}
				}
				
				for(int i=0; i<lpList.size(); i++) {
					VEEMaxDetailData data = lpList.get(i);					
					
					data.setValue_00(df.format(totalValue[0] / 24));
					data.setValue_01(df.format(totalValue[1] / 24));
					data.setValue_02(df.format(totalValue[2] / 24));
					data.setValue_03(df.format(totalValue[3] / 24));
					data.setValue_04(df.format(totalValue[4] / 24));
					data.setValue_05(df.format(totalValue[5] / 24));
					data.setValue_06(df.format(totalValue[6] / 24));
					data.setValue_07(df.format(totalValue[7] / 24));
					data.setValue_08(df.format(totalValue[8] / 24));
					data.setValue_09(df.format(totalValue[9] / 24));
					
					data.setValue_10(df.format(totalValue[10] / 24));
					data.setValue_11(df.format(totalValue[11] / 24));
					data.setValue_12(df.format(totalValue[12] / 24));
					data.setValue_13(df.format(totalValue[13] / 24));
					data.setValue_14(df.format(totalValue[14] / 24));
					data.setValue_15(df.format(totalValue[15] / 24));
					data.setValue_16(df.format(totalValue[16] / 24));
					data.setValue_17(df.format(totalValue[17] / 24));
					data.setValue_18(df.format(totalValue[18] / 24));
					data.setValue_19(df.format(totalValue[19] / 24));
					
					data.setValue_20(df.format(totalValue[20] / 24));
					data.setValue_21(df.format(totalValue[21] / 24));
					data.setValue_22(df.format(totalValue[22] / 24));
					data.setValue_23(df.format(totalValue[23] / 24));
					data.setValue_24(df.format(totalValue[24] / 24));
					data.setValue_25(df.format(totalValue[25] / 24));
					data.setValue_26(df.format(totalValue[26] / 24));
					data.setValue_27(df.format(totalValue[27] / 24));
					data.setValue_28(df.format(totalValue[28] / 24));
					data.setValue_29(df.format(totalValue[29] / 24));
					
					data.setValue_30(df.format(totalValue[30] / 24));
					data.setValue_31(df.format(totalValue[31] / 24));
					data.setValue_32(df.format(totalValue[32] / 24));
					data.setValue_33(df.format(totalValue[33] / 24));
					data.setValue_34(df.format(totalValue[34] / 24));
					data.setValue_35(df.format(totalValue[35] / 24));
					data.setValue_36(df.format(totalValue[36] / 24));
					data.setValue_37(df.format(totalValue[37] / 24));
					data.setValue_38(df.format(totalValue[38] / 24));
					data.setValue_39(df.format(totalValue[39] / 24));
					
					data.setValue_40(df.format(totalValue[40] / 24));
					data.setValue_41(df.format(totalValue[41] / 24));
					data.setValue_42(df.format(totalValue[42] / 24));
					data.setValue_43(df.format(totalValue[43] / 24));
					data.setValue_44(df.format(totalValue[44] / 24));
					data.setValue_45(df.format(totalValue[45] / 24));
					data.setValue_46(df.format(totalValue[46] / 24));
					data.setValue_47(df.format(totalValue[47] / 24));
					data.setValue_48(df.format(totalValue[48] / 24));
					data.setValue_49(df.format(totalValue[49] / 24));
					
					data.setValue_50(df.format(totalValue[50] / 24));
					data.setValue_51(df.format(totalValue[51] / 24));
					data.setValue_52(df.format(totalValue[52] / 24));
					data.setValue_53(df.format(totalValue[53] / 24));
					data.setValue_54(df.format(totalValue[54] / 24));
					data.setValue_55(df.format(totalValue[55] / 24));
					data.setValue_56(df.format(totalValue[56] / 24));
					data.setValue_57(df.format(totalValue[57] / 24));
					data.setValue_58(df.format(totalValue[58] / 24));
					data.setValue_59(df.format(totalValue[59] / 24));
				}
			}
				
			Set<Condition> set = new HashSet<Condition>();
			
			if(!"".equals(yyyymmdd)) set.add(new Condition("yyyymmdd",new Object[]{yyyymmdd},null,Restriction.EQ));				
			if(!"".equals(channel)) set.add(new Condition("id.channel",new Object[]{Integer.parseInt(channel)},null,Restriction.EQ));
			if(!"".equals(mdevType)) set.add(new Condition("id.mdevType",new Object[]{CommonConstants.DeviceType.valueOf(mdevType)},null,Restriction.EQ));
			if(!"".equals(mdevId)) set.add(new Condition("id.mdevId",new Object[]{mdevId},null,Restriction.EQ));
			if(!"".equals(dst)) set.add(new Condition("id.dst",new Object[]{Integer.parseInt(dst)},null,Restriction.EQ));
			set.add(new Condition("hour", new Object[]{}, null, Restriction.ORDERBY));
			
			if(CommonConstants.MeterType.EnergyMeter.getLpClassName().equals(lpClassName)){
				List<LpEM> emList = lpEMDao.getLpEMsByListCondition(set);
				
				try {
					lpInterval = lpEMDao.getLpInterval(mdevId);
				} catch(Exception e) {}
				
				
				for(int i=0 ; i<24 ; i++) {
					VEEMaxDetailData lpData = lpList.get(i);
					
					lpData.setYyyymmdd(yyyymmdd);
					lpData.setChannel(channel);
					lpData.setMdev_id(mdevId);
					lpData.setMdev_type(mdevType);
					lpData.setDst(dst);
					lpData.setHh(String.format("%02d", i));
					lpData.setYyyymmddhh(TimeLocaleUtil.getLocaleDate(yyyymmdd, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter())+ " " + String.format("%02d", i));
					lpData.setRealData("N");
					lpData.setIsAutoEstimation("Y");
					
					boolean existRealData = false;
					int emptyCount = 0;
					for(int j=0; j<emList.size(); j++) {
						if(String.format("%02d", i).equals(emList.get(j).getHour())){
							LpEM lpReal = emList.get(j);
							try {
								for(int k=0 ; k < 60 && lpInterval > 0 ; ) {
									if(k % lpInterval == 0) {								
										if(LpEM.class.getMethod("getValue_" + String.format("%02d", k)).invoke(lpReal) != null) {
											existRealData = true;				
										}else{
											emptyCount++;
										}
										k = k + lpInterval;
									}
								}
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							
							if(existRealData) {
								lpData.setValue_00(lpReal.getValue_00()==null?lpData.getValue_00():df.format(lpReal.getValue_00()));
								lpData.setValue_01(lpReal.getValue_01()==null?lpData.getValue_01():df.format(lpReal.getValue_01()));
								lpData.setValue_02(lpReal.getValue_02()==null?lpData.getValue_02():df.format(lpReal.getValue_02()));
								lpData.setValue_03(lpReal.getValue_03()==null?lpData.getValue_03():df.format(lpReal.getValue_03()));
								lpData.setValue_04(lpReal.getValue_04()==null?lpData.getValue_04():df.format(lpReal.getValue_04()));
								lpData.setValue_05(lpReal.getValue_05()==null?lpData.getValue_05():df.format(lpReal.getValue_05()));
								lpData.setValue_06(lpReal.getValue_06()==null?lpData.getValue_06():df.format(lpReal.getValue_06()));
								lpData.setValue_07(lpReal.getValue_07()==null?lpData.getValue_07():df.format(lpReal.getValue_07()));
								lpData.setValue_08(lpReal.getValue_08()==null?lpData.getValue_08():df.format(lpReal.getValue_08()));
								lpData.setValue_09(lpReal.getValue_09()==null?lpData.getValue_09():df.format(lpReal.getValue_09()));
								
								lpData.setValue_10(lpReal.getValue_10()==null?lpData.getValue_10():df.format(lpReal.getValue_10()));
								lpData.setValue_11(lpReal.getValue_11()==null?lpData.getValue_11():df.format(lpReal.getValue_11()));
								lpData.setValue_12(lpReal.getValue_12()==null?lpData.getValue_12():df.format(lpReal.getValue_12()));
								lpData.setValue_13(lpReal.getValue_13()==null?lpData.getValue_13():df.format(lpReal.getValue_13()));
								lpData.setValue_14(lpReal.getValue_14()==null?lpData.getValue_14():df.format(lpReal.getValue_14()));
								lpData.setValue_15(lpReal.getValue_15()==null?lpData.getValue_15():df.format(lpReal.getValue_15()));
								lpData.setValue_16(lpReal.getValue_16()==null?lpData.getValue_16():df.format(lpReal.getValue_16()));
								lpData.setValue_17(lpReal.getValue_17()==null?lpData.getValue_17():df.format(lpReal.getValue_17()));
								lpData.setValue_18(lpReal.getValue_18()==null?lpData.getValue_18():df.format(lpReal.getValue_18()));
								lpData.setValue_19(lpReal.getValue_19()==null?lpData.getValue_19():df.format(lpReal.getValue_19()));
								
								lpData.setValue_20(lpReal.getValue_20()==null?lpData.getValue_20():df.format(lpReal.getValue_20()));
								lpData.setValue_21(lpReal.getValue_21()==null?lpData.getValue_21():df.format(lpReal.getValue_21()));
								lpData.setValue_22(lpReal.getValue_22()==null?lpData.getValue_22():df.format(lpReal.getValue_22()));
								lpData.setValue_23(lpReal.getValue_23()==null?lpData.getValue_23():df.format(lpReal.getValue_23()));
								lpData.setValue_24(lpReal.getValue_24()==null?lpData.getValue_24():df.format(lpReal.getValue_24()));
								lpData.setValue_25(lpReal.getValue_25()==null?lpData.getValue_25():df.format(lpReal.getValue_25()));
								lpData.setValue_26(lpReal.getValue_26()==null?lpData.getValue_26():df.format(lpReal.getValue_26()));
								lpData.setValue_27(lpReal.getValue_27()==null?lpData.getValue_27():df.format(lpReal.getValue_27()));
								lpData.setValue_28(lpReal.getValue_28()==null?lpData.getValue_28():df.format(lpReal.getValue_28()));
								lpData.setValue_29(lpReal.getValue_29()==null?lpData.getValue_29():df.format(lpReal.getValue_29()));
								
								lpData.setValue_30(lpReal.getValue_30()==null?lpData.getValue_30():df.format(lpReal.getValue_30()));
								lpData.setValue_31(lpReal.getValue_31()==null?lpData.getValue_31():df.format(lpReal.getValue_31()));
								lpData.setValue_32(lpReal.getValue_32()==null?lpData.getValue_32():df.format(lpReal.getValue_32()));
								lpData.setValue_33(lpReal.getValue_33()==null?lpData.getValue_33():df.format(lpReal.getValue_33()));
								lpData.setValue_34(lpReal.getValue_34()==null?lpData.getValue_34():df.format(lpReal.getValue_34()));
								lpData.setValue_35(lpReal.getValue_35()==null?lpData.getValue_35():df.format(lpReal.getValue_35()));
								lpData.setValue_36(lpReal.getValue_36()==null?lpData.getValue_36():df.format(lpReal.getValue_36()));
								lpData.setValue_37(lpReal.getValue_37()==null?lpData.getValue_37():df.format(lpReal.getValue_37()));
								lpData.setValue_38(lpReal.getValue_38()==null?lpData.getValue_38():df.format(lpReal.getValue_38()));
								lpData.setValue_39(lpReal.getValue_39()==null?lpData.getValue_38():df.format(lpReal.getValue_39()));
								
								lpData.setValue_40(lpReal.getValue_40()==null?lpData.getValue_40():df.format(lpReal.getValue_40()));
								lpData.setValue_41(lpReal.getValue_41()==null?lpData.getValue_41():df.format(lpReal.getValue_41()));
								lpData.setValue_42(lpReal.getValue_42()==null?lpData.getValue_42():df.format(lpReal.getValue_42()));
								lpData.setValue_43(lpReal.getValue_43()==null?lpData.getValue_43():df.format(lpReal.getValue_43()));
								lpData.setValue_44(lpReal.getValue_44()==null?lpData.getValue_44():df.format(lpReal.getValue_44()));
								lpData.setValue_45(lpReal.getValue_45()==null?lpData.getValue_45():df.format(lpReal.getValue_45()));
								lpData.setValue_46(lpReal.getValue_46()==null?lpData.getValue_46():df.format(lpReal.getValue_46()));
								lpData.setValue_47(lpReal.getValue_47()==null?lpData.getValue_47():df.format(lpReal.getValue_47()));
								lpData.setValue_48(lpReal.getValue_48()==null?lpData.getValue_48():df.format(lpReal.getValue_48()));
								lpData.setValue_49(lpReal.getValue_49()==null?lpData.getValue_49():df.format(lpReal.getValue_49()));
								
								lpData.setValue_50(lpReal.getValue_50()==null?lpData.getValue_50():df.format(lpReal.getValue_50()));
								lpData.setValue_51(lpReal.getValue_51()==null?lpData.getValue_51():df.format(lpReal.getValue_51()));
								lpData.setValue_52(lpReal.getValue_52()==null?lpData.getValue_52():df.format(lpReal.getValue_52()));
								lpData.setValue_53(lpReal.getValue_53()==null?lpData.getValue_53():df.format(lpReal.getValue_53()));
								lpData.setValue_54(lpReal.getValue_54()==null?lpData.getValue_54():df.format(lpReal.getValue_54()));
								lpData.setValue_55(lpReal.getValue_55()==null?lpData.getValue_55():df.format(lpReal.getValue_55()));
								lpData.setValue_56(lpReal.getValue_56()==null?lpData.getValue_56():df.format(lpReal.getValue_56()));
								lpData.setValue_57(lpReal.getValue_57()==null?lpData.getValue_57():df.format(lpReal.getValue_57()));
								lpData.setValue_58(lpReal.getValue_58()==null?lpData.getValue_58():df.format(lpReal.getValue_58()));
								lpData.setValue_59(lpReal.getValue_59()==null?lpData.getValue_59():df.format(lpReal.getValue_59()));
								
								lpData.setRealData("Y");
								if(emptyCount > 0){
									lpData.setRealData("N");
									lpData.setIsAutoEstimation("Y");
								}else{
									lpData.setRealData("Y");
									lpData.setIsAutoEstimation("N");
								}

								
								//break; // TODO FIX 다음 회차에도 중간중간 누락일 수가 있는데 그냥 빠져나가 버리기 땜시 문제가 있음
							}
						}
					}
				}
			} else if(CommonConstants.MeterType.GasMeter.getLpClassName().equals(lpClassName)){
				List<LpGM> gmList = lpGMDao.getLpGMsByListCondition(set);
				
				try {
					lpInterval = lpGMDao.getLpInterval(mdevId);
				} catch(Exception e) {}
				
				
				for(int i=0 ; i<24 ; i++) {
					VEEMaxDetailData lpData = lpList.get(i);
					
					lpData.setYyyymmdd(yyyymmdd);
					lpData.setChannel(channel);
					lpData.setMdev_id(mdevId);
					lpData.setMdev_type(mdevType);
					lpData.setDst(dst);
					lpData.setHh(String.format("%02d", i));
					lpData.setYyyymmddhh(TimeLocaleUtil.getLocaleDate(yyyymmdd, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter())+ " " + String.format("%02d", i));
					lpData.setRealData("N");
					lpData.setIsAutoEstimation("Y");
					
					boolean existRealData = false;
					for(int j=0; j<gmList.size(); j++) {
						if(String.format("%02d", i).equals(gmList.get(j).getHour())){
							LpGM lpReal = gmList.get(j);
							try {
								for(int k=0 ; k < 60 ; k++) {
									if(k % lpInterval == 0) {								
										if(LpGM.class.getMethod("getValue_" + String.format("%02d", k)).invoke(lpReal) != null) {
											existRealData = true;
											continue;
										}
									}
								}
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							
							if(existRealData) {
								lpData.setValue_00(lpReal.getValue_00()==null?"":df.format(lpReal.getValue_00()));
								lpData.setValue_01(lpReal.getValue_01()==null?"":df.format(lpReal.getValue_01()));
								lpData.setValue_02(lpReal.getValue_02()==null?"":df.format(lpReal.getValue_02()));
								lpData.setValue_03(lpReal.getValue_03()==null?"":df.format(lpReal.getValue_03()));
								lpData.setValue_04(lpReal.getValue_04()==null?"":df.format(lpReal.getValue_04()));
								lpData.setValue_05(lpReal.getValue_05()==null?"":df.format(lpReal.getValue_05()));
								lpData.setValue_06(lpReal.getValue_06()==null?"":df.format(lpReal.getValue_06()));
								lpData.setValue_07(lpReal.getValue_07()==null?"":df.format(lpReal.getValue_07()));
								lpData.setValue_08(lpReal.getValue_08()==null?"":df.format(lpReal.getValue_08()));
								lpData.setValue_09(lpReal.getValue_09()==null?"":df.format(lpReal.getValue_09()));
								
								lpData.setValue_10(lpReal.getValue_10()==null?"":df.format(lpReal.getValue_10()));
								lpData.setValue_11(lpReal.getValue_11()==null?"":df.format(lpReal.getValue_11()));
								lpData.setValue_12(lpReal.getValue_12()==null?"":df.format(lpReal.getValue_12()));
								lpData.setValue_13(lpReal.getValue_13()==null?"":df.format(lpReal.getValue_13()));
								lpData.setValue_14(lpReal.getValue_14()==null?"":df.format(lpReal.getValue_14()));
								lpData.setValue_15(lpReal.getValue_15()==null?"":df.format(lpReal.getValue_15()));
								lpData.setValue_16(lpReal.getValue_16()==null?"":df.format(lpReal.getValue_16()));
								lpData.setValue_17(lpReal.getValue_17()==null?"":df.format(lpReal.getValue_17()));
								lpData.setValue_18(lpReal.getValue_18()==null?"":df.format(lpReal.getValue_18()));
								lpData.setValue_19(lpReal.getValue_19()==null?"":df.format(lpReal.getValue_19()));
								
								lpData.setValue_20(lpReal.getValue_20()==null?"":df.format(lpReal.getValue_20()));
								lpData.setValue_21(lpReal.getValue_21()==null?"":df.format(lpReal.getValue_21()));
								lpData.setValue_22(lpReal.getValue_22()==null?"":df.format(lpReal.getValue_22()));
								lpData.setValue_23(lpReal.getValue_23()==null?"":df.format(lpReal.getValue_23()));
								lpData.setValue_24(lpReal.getValue_24()==null?"":df.format(lpReal.getValue_24()));
								lpData.setValue_25(lpReal.getValue_25()==null?"":df.format(lpReal.getValue_25()));
								lpData.setValue_26(lpReal.getValue_26()==null?"":df.format(lpReal.getValue_26()));
								lpData.setValue_27(lpReal.getValue_27()==null?"":df.format(lpReal.getValue_27()));
								lpData.setValue_28(lpReal.getValue_28()==null?"":df.format(lpReal.getValue_28()));
								lpData.setValue_29(lpReal.getValue_29()==null?"":df.format(lpReal.getValue_29()));
								
								lpData.setValue_30(lpReal.getValue_30()==null?"":df.format(lpReal.getValue_30()));
								lpData.setValue_31(lpReal.getValue_31()==null?"":df.format(lpReal.getValue_31()));
								lpData.setValue_32(lpReal.getValue_32()==null?"":df.format(lpReal.getValue_32()));
								lpData.setValue_33(lpReal.getValue_33()==null?"":df.format(lpReal.getValue_33()));
								lpData.setValue_34(lpReal.getValue_34()==null?"":df.format(lpReal.getValue_34()));
								lpData.setValue_35(lpReal.getValue_35()==null?"":df.format(lpReal.getValue_35()));
								lpData.setValue_36(lpReal.getValue_36()==null?"":df.format(lpReal.getValue_36()));
								lpData.setValue_37(lpReal.getValue_37()==null?"":df.format(lpReal.getValue_37()));
								lpData.setValue_38(lpReal.getValue_38()==null?"":df.format(lpReal.getValue_38()));
								lpData.setValue_39(lpReal.getValue_39()==null?"":df.format(lpReal.getValue_39()));
								
								lpData.setValue_40(lpReal.getValue_40()==null?"":df.format(lpReal.getValue_40()));
								lpData.setValue_41(lpReal.getValue_41()==null?"":df.format(lpReal.getValue_41()));
								lpData.setValue_42(lpReal.getValue_42()==null?"":df.format(lpReal.getValue_42()));
								lpData.setValue_43(lpReal.getValue_43()==null?"":df.format(lpReal.getValue_43()));
								lpData.setValue_44(lpReal.getValue_44()==null?"":df.format(lpReal.getValue_44()));
								lpData.setValue_45(lpReal.getValue_45()==null?"":df.format(lpReal.getValue_45()));
								lpData.setValue_46(lpReal.getValue_46()==null?"":df.format(lpReal.getValue_46()));
								lpData.setValue_47(lpReal.getValue_47()==null?"":df.format(lpReal.getValue_47()));
								lpData.setValue_48(lpReal.getValue_48()==null?"":df.format(lpReal.getValue_48()));
								lpData.setValue_49(lpReal.getValue_49()==null?"":df.format(lpReal.getValue_49()));
								
								lpData.setValue_50(lpReal.getValue_50()==null?"":df.format(lpReal.getValue_50()));
								lpData.setValue_51(lpReal.getValue_51()==null?"":df.format(lpReal.getValue_51()));
								lpData.setValue_52(lpReal.getValue_52()==null?"":df.format(lpReal.getValue_52()));
								lpData.setValue_53(lpReal.getValue_53()==null?"":df.format(lpReal.getValue_53()));
								lpData.setValue_54(lpReal.getValue_54()==null?"":df.format(lpReal.getValue_54()));
								lpData.setValue_55(lpReal.getValue_55()==null?"":df.format(lpReal.getValue_55()));
								lpData.setValue_56(lpReal.getValue_56()==null?"":df.format(lpReal.getValue_56()));
								lpData.setValue_57(lpReal.getValue_57()==null?"":df.format(lpReal.getValue_57()));
								lpData.setValue_58(lpReal.getValue_58()==null?"":df.format(lpReal.getValue_58()));
								lpData.setValue_59(lpReal.getValue_59()==null?"":df.format(lpReal.getValue_59()));
								
								lpData.setRealData("Y");
								lpData.setIsAutoEstimation("N");
								
								break;
							}
						}
					}
				}
			} else if(CommonConstants.MeterType.WaterMeter.getLpClassName().equals(lpClassName)){
				List<LpWM> wmList = lpWMDao.getLpWMsByListCondition(set);
				
				try {
					lpInterval = lpWMDao.getLpInterval(mdevId);
				} catch(Exception e) {}
				
				
				for(int i=0 ; i<24 ; i++) {
					VEEMaxDetailData lpData = lpList.get(i);
					
					lpData.setYyyymmdd(yyyymmdd);
					lpData.setChannel(channel);
					lpData.setMdev_id(mdevId);
					lpData.setMdev_type(mdevType);
					lpData.setDst(dst);
					lpData.setHh(String.format("%02d", i));
					lpData.setYyyymmddhh(TimeLocaleUtil.getLocaleDate(yyyymmdd, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter())+ " " + String.format("%02d", i));
					lpData.setRealData("N");
					lpData.setIsAutoEstimation("Y");
					
					boolean existRealData = false;
					for(int j=0; j<wmList.size(); j++) {
						if(String.format("%02d", i).equals(wmList.get(j).getHour())){
							LpWM lpReal = wmList.get(j);
							try {
								for(int k=0 ; k < 60 ; k++) {
									if(k % lpInterval == 0) {								
										if(LpWM.class.getMethod("getValue_" + String.format("%02d", k)).invoke(lpReal) != null) {
											existRealData = true;
											continue;
										}
									}
								}
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							
							if(existRealData) {
								lpData.setValue_00(StringUtil.nullToBlank(lpReal.getValue_00()));
								lpData.setValue_01(StringUtil.nullToBlank(lpReal.getValue_01()));
								lpData.setValue_02(StringUtil.nullToBlank(lpReal.getValue_02()));
								lpData.setValue_03(StringUtil.nullToBlank(lpReal.getValue_03()));
								lpData.setValue_04(StringUtil.nullToBlank(lpReal.getValue_04()));
								lpData.setValue_05(StringUtil.nullToBlank(lpReal.getValue_05()));
								lpData.setValue_06(StringUtil.nullToBlank(lpReal.getValue_06()));
								lpData.setValue_07(StringUtil.nullToBlank(lpReal.getValue_07()));
								lpData.setValue_08(StringUtil.nullToBlank(lpReal.getValue_08()));
								lpData.setValue_09(StringUtil.nullToBlank(lpReal.getValue_09()));
								
								lpData.setValue_10(StringUtil.nullToBlank(lpReal.getValue_10()));
								lpData.setValue_11(StringUtil.nullToBlank(lpReal.getValue_11()));
								lpData.setValue_12(StringUtil.nullToBlank(lpReal.getValue_12()));
								lpData.setValue_13(StringUtil.nullToBlank(lpReal.getValue_13()));
								lpData.setValue_14(StringUtil.nullToBlank(lpReal.getValue_14()));
								lpData.setValue_15(StringUtil.nullToBlank(lpReal.getValue_15()));
								lpData.setValue_16(StringUtil.nullToBlank(lpReal.getValue_16()));
								lpData.setValue_17(StringUtil.nullToBlank(lpReal.getValue_17()));
								lpData.setValue_18(StringUtil.nullToBlank(lpReal.getValue_18()));
								lpData.setValue_19(StringUtil.nullToBlank(lpReal.getValue_19()));
								
								lpData.setValue_20(StringUtil.nullToBlank(lpReal.getValue_20()));
								lpData.setValue_21(StringUtil.nullToBlank(lpReal.getValue_21()));
								lpData.setValue_22(StringUtil.nullToBlank(lpReal.getValue_22()));
								lpData.setValue_23(StringUtil.nullToBlank(lpReal.getValue_23()));
								lpData.setValue_24(StringUtil.nullToBlank(lpReal.getValue_24()));
								lpData.setValue_25(StringUtil.nullToBlank(lpReal.getValue_25()));
								lpData.setValue_26(StringUtil.nullToBlank(lpReal.getValue_26()));
								lpData.setValue_27(StringUtil.nullToBlank(lpReal.getValue_27()));
								lpData.setValue_28(StringUtil.nullToBlank(lpReal.getValue_28()));
								lpData.setValue_29(StringUtil.nullToBlank(lpReal.getValue_29()));
								
								lpData.setValue_30(StringUtil.nullToBlank(lpReal.getValue_30()));
								lpData.setValue_31(StringUtil.nullToBlank(lpReal.getValue_31()));
								lpData.setValue_32(StringUtil.nullToBlank(lpReal.getValue_32()));
								lpData.setValue_33(StringUtil.nullToBlank(lpReal.getValue_33()));
								lpData.setValue_34(StringUtil.nullToBlank(lpReal.getValue_34()));
								lpData.setValue_35(StringUtil.nullToBlank(lpReal.getValue_35()));
								lpData.setValue_36(StringUtil.nullToBlank(lpReal.getValue_36()));
								lpData.setValue_37(StringUtil.nullToBlank(lpReal.getValue_37()));
								lpData.setValue_38(StringUtil.nullToBlank(lpReal.getValue_38()));
								lpData.setValue_39(StringUtil.nullToBlank(lpReal.getValue_39()));
								
								lpData.setValue_40(StringUtil.nullToBlank(lpReal.getValue_40()));
								lpData.setValue_41(StringUtil.nullToBlank(lpReal.getValue_41()));
								lpData.setValue_42(StringUtil.nullToBlank(lpReal.getValue_42()));
								lpData.setValue_43(StringUtil.nullToBlank(lpReal.getValue_43()));
								lpData.setValue_44(StringUtil.nullToBlank(lpReal.getValue_44()));
								lpData.setValue_45(StringUtil.nullToBlank(lpReal.getValue_45()));
								lpData.setValue_46(StringUtil.nullToBlank(lpReal.getValue_46()));
								lpData.setValue_47(StringUtil.nullToBlank(lpReal.getValue_47()));
								lpData.setValue_48(StringUtil.nullToBlank(lpReal.getValue_48()));
								lpData.setValue_49(StringUtil.nullToBlank(lpReal.getValue_49()));
								
								lpData.setValue_50(StringUtil.nullToBlank(lpReal.getValue_50()));
								lpData.setValue_51(StringUtil.nullToBlank(lpReal.getValue_51()));
								lpData.setValue_52(StringUtil.nullToBlank(lpReal.getValue_52()));
								lpData.setValue_53(StringUtil.nullToBlank(lpReal.getValue_53()));
								lpData.setValue_54(StringUtil.nullToBlank(lpReal.getValue_54()));
								lpData.setValue_55(StringUtil.nullToBlank(lpReal.getValue_55()));
								lpData.setValue_56(StringUtil.nullToBlank(lpReal.getValue_56()));
								lpData.setValue_57(StringUtil.nullToBlank(lpReal.getValue_57()));
								lpData.setValue_58(StringUtil.nullToBlank(lpReal.getValue_58()));
								lpData.setValue_59(StringUtil.nullToBlank(lpReal.getValue_59()));
								
								lpData.setRealData("Y");
								lpData.setIsAutoEstimation("N");
								
								break;
							}
						}
					}
				}				
			}
		}

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("lpList", lpList);
		resultMap.put("lpInterval", lpInterval);
		
		return resultMap;
	}


	public Map<String, Object> updateAutoEstimation(Map<String, Object> conditions) {
		
		String meterType = StringUtil.nullToBlank(conditions.get("meterType"));
		String item = StringUtil.nullToBlank(conditions.get("item"));
		String yyyymmdd = StringUtil.nullToBlank(conditions.get("yyyymmdd"));
		String channel = StringUtil.nullToBlank(conditions.get("channel"));
		String mdevType = StringUtil.nullToBlank(conditions.get("mdevType"));
		String mdevId = StringUtil.nullToBlank(conditions.get("mdevId"));
		String dst = StringUtil.nullToBlank(conditions.get("dst"));
		String supplierId = StringUtil.nullToBlank(conditions.get("supplierId"));
		String type1 = StringUtil.nullToBlank(conditions.get("type1"));
		String type2 = StringUtil.nullToBlank(conditions.get("type2"));
		String userId = StringUtil.nullToBlank(conditions.get("userId"));
		
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		
		String lpClassName = "";
		int lpInterval = 60;
		
		List<VEEMaxDetailData> lpList = validateAutoEstimation(meterType, item, yyyymmdd, channel, mdevType, mdevId, dst, type1, supplierId);		
		if( lpList != null) {
			
			DecimalFormat df = new DecimalFormat(supplier.getMd().getPattern());
			
			lpClassName 	= CommonConstants.MeterType.valueOf(meterType).getLpClassName();
			
			if(CommonConstants.MeterType.EnergyMeter.getLpClassName().equals(lpClassName)){
				try {
					lpInterval = lpEMDao.getLpInterval(mdevId);
				} catch(Exception e) {}				
			} else if(CommonConstants.MeterType.GasMeter.getLpClassName().equals(lpClassName)){
				try {
					lpInterval = lpGMDao.getLpInterval(mdevId);
				} catch(Exception e) {}				
			} else if(CommonConstants.MeterType.WaterMeter.getLpClassName().equals(lpClassName)){
				try {
					lpInterval = lpWMDao.getLpInterval(mdevId);
				} catch(Exception e) {}				
			}
			
			if(type2.equals("avg")) { // 평균 값으로 적용
				
				double totalValue[] = new double[60];
				
				for(int i=0; i<lpList.size(); i++) {
					VEEMaxDetailData data = lpList.get(i);					
					for(int j=0; j<60 ; j++) {
						try {
							String value = String.valueOf(VEEMaxDetailData.class.getMethod("getValue_" + String.format("%02d", j)).invoke(data));
							if(value.length() > 0) {
								totalValue[j] += Double.parseDouble(value);
							}
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						}
					}
				}
				
				for(int i=0; i<lpList.size(); i++) {
					VEEMaxDetailData data = lpList.get(i);					
					
					data.setValue_00(df.format(totalValue[0] / 24));
					data.setValue_01(df.format(totalValue[1] / 24));
					data.setValue_02(df.format(totalValue[2] / 24));
					data.setValue_03(df.format(totalValue[3] / 24));
					data.setValue_04(df.format(totalValue[4] / 24));
					data.setValue_05(df.format(totalValue[5] / 24));
					data.setValue_06(df.format(totalValue[6] / 24));
					data.setValue_07(df.format(totalValue[7] / 24));
					data.setValue_08(df.format(totalValue[8] / 24));
					data.setValue_09(df.format(totalValue[9] / 24));
					
					data.setValue_10(df.format(totalValue[10] / 24));
					data.setValue_11(df.format(totalValue[11] / 24));
					data.setValue_12(df.format(totalValue[12] / 24));
					data.setValue_13(df.format(totalValue[13] / 24));
					data.setValue_14(df.format(totalValue[14] / 24));
					data.setValue_15(df.format(totalValue[15] / 24));
					data.setValue_16(df.format(totalValue[16] / 24));
					data.setValue_17(df.format(totalValue[17] / 24));
					data.setValue_18(df.format(totalValue[18] / 24));
					data.setValue_19(df.format(totalValue[19] / 24));
					
					data.setValue_20(df.format(totalValue[20] / 24));
					data.setValue_21(df.format(totalValue[21] / 24));
					data.setValue_22(df.format(totalValue[22] / 24));
					data.setValue_23(df.format(totalValue[23] / 24));
					data.setValue_24(df.format(totalValue[24] / 24));
					data.setValue_25(df.format(totalValue[25] / 24));
					data.setValue_26(df.format(totalValue[26] / 24));
					data.setValue_27(df.format(totalValue[27] / 24));
					data.setValue_28(df.format(totalValue[28] / 24));
					data.setValue_29(df.format(totalValue[29] / 24));
					
					data.setValue_30(df.format(totalValue[30] / 24));
					data.setValue_31(df.format(totalValue[31] / 24));
					data.setValue_32(df.format(totalValue[32] / 24));
					data.setValue_33(df.format(totalValue[33] / 24));
					data.setValue_34(df.format(totalValue[34] / 24));
					data.setValue_35(df.format(totalValue[35] / 24));
					data.setValue_36(df.format(totalValue[36] / 24));
					data.setValue_37(df.format(totalValue[37] / 24));
					data.setValue_38(df.format(totalValue[38] / 24));
					data.setValue_39(df.format(totalValue[39] / 24));
					
					data.setValue_40(df.format(totalValue[40] / 24));
					data.setValue_41(df.format(totalValue[41] / 24));
					data.setValue_42(df.format(totalValue[42] / 24));
					data.setValue_43(df.format(totalValue[43] / 24));
					data.setValue_44(df.format(totalValue[44] / 24));
					data.setValue_45(df.format(totalValue[45] / 24));
					data.setValue_46(df.format(totalValue[46] / 24));
					data.setValue_47(df.format(totalValue[47] / 24));
					data.setValue_48(df.format(totalValue[48] / 24));
					data.setValue_49(df.format(totalValue[49] / 24));
					
					data.setValue_50(df.format(totalValue[50] / 24));
					data.setValue_51(df.format(totalValue[51] / 24));
					data.setValue_52(df.format(totalValue[52] / 24));
					data.setValue_53(df.format(totalValue[53] / 24));
					data.setValue_54(df.format(totalValue[54] / 24));
					data.setValue_55(df.format(totalValue[55] / 24));
					data.setValue_56(df.format(totalValue[56] / 24));
					data.setValue_57(df.format(totalValue[57] / 24));
					data.setValue_58(df.format(totalValue[58] / 24));
					data.setValue_59(df.format(totalValue[59] / 24));
				}
			}
				
			Set<Condition> set = new HashSet<Condition>();
			
			if(!"".equals(yyyymmdd)) set.add(new Condition("yyyymmdd",new Object[]{yyyymmdd},null,Restriction.EQ));				
			if(!"".equals(channel)) set.add(new Condition("id.channel",new Object[]{Integer.parseInt(channel)},null,Restriction.EQ));
			if(!"".equals(mdevType)) set.add(new Condition("id.mdevType",new Object[]{CommonConstants.DeviceType.valueOf(mdevType)},null,Restriction.EQ));
			if(!"".equals(mdevId)) set.add(new Condition("id.mdevId",new Object[]{mdevId},null,Restriction.EQ));
			if(!"".equals(dst)) set.add(new Condition("id.dst",new Object[]{Integer.parseInt(dst)},null,Restriction.EQ));
			set.add(new Condition("hour", new Object[]{}, null, Restriction.ORDERBY));
			
			if(CommonConstants.MeterType.EnergyMeter.getLpClassName().equals(lpClassName)){
				List<LpEM> emList = lpEMDao.getLpEMsByListCondition(set);
				
				try {
					lpInterval = lpEMDao.getLpInterval(mdevId);
				} catch(Exception e) {}
				
				
				for(int i=0 ; i<24 ; i++) {
					VEEMaxDetailData lpData = lpList.get(i);
					
					lpData.setYyyymmdd(yyyymmdd);
					lpData.setChannel(channel);
					lpData.setMdev_id(mdevId);
					lpData.setMdev_type(mdevType);
					lpData.setDst(dst);
					lpData.setHh(String.format("%02d", i));
					lpData.setYyyymmddhh(TimeLocaleUtil.getLocaleDate(yyyymmdd, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter())+ " " + String.format("%02d", i));
					lpData.setRealData("N");
					lpData.setIsAutoEstimation("Y");
					
					boolean existRealData = false;
					boolean isNullData = true;
					for(int j=0; j<emList.size(); j++) {
						if(String.format("%02d", i).equals(emList.get(j).getHour())){
							LpEM lpReal = emList.get(j);
							existRealData = true;
							try {
								for(int k=0 ; k < 60 ; k++) {
									if(k % lpInterval == 0) {
										if(LpEM.class.getMethod("getValue_" + String.format("%02d", k)).invoke(lpReal) != null) {
											isNullData = false;
											break;
										}
									}
								}
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							
							if(existRealData) break;
						}
					}
					
					if(!existRealData) {
						String table = "";
						
						LpPk lppk = new LpPk();								
						lppk.setYyyymmddhh(lpData.getYyyymmdd() + lpData.getHh());
						lppk.setChannel(Integer.parseInt(lpData.getChannel()));
						lppk.setMDevType(lpData.getMdev_type());
						lppk.setMDevId(lpData.getMdev_id());
						lppk.setDst(Integer.parseInt(lpData.getDst()));
						
						MeteringLP currLp = null;
						
						table = CommonConstants.MeterType.EnergyMeter.getLpClassName();				
						currLp = new LpEM();
						
						currLp.setId(lppk);
						currLp.setYyyymmdd(lpData.getYyyymmdd());
						currLp.setHour(lpData.getHh());
						currLp.setWriteDate(CalendarUtil.getCurrentDate());
						currLp.setSupplier(supplier);
						
						String[] params = new String[60];
						
						for(int k=0; k<60 ; k++) {
							try {
								String value = String.valueOf(VEEMaxDetailData.class.getMethod("getValue_" + String.format("%02d", k)).invoke(lpData));
								params[k] = StringUtil.nullToBlank(value);									
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
									e.printStackTrace();
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							}
						}
						
						for(int k=0 ; k<60 ; k++) {
							if(k % lpInterval == 0 && !params[k].equals("")) {
								insertVEELog_Lp(currLp, yyyymmdd, lpData.getHh(), table, "Value_" + String.format("%02d", k), "", params[k], userId, lppk, EditItem.AutomaticEstimated.toString());
							}
						}
						
						if(!StringUtil.nullToBlank(params[0]).equals("")) currLp.setValue_00(Double.parseDouble(StringUtil.nullToBlank(params[0])));
						if(!StringUtil.nullToBlank(params[1]).equals("")) currLp.setValue_01(Double.parseDouble(StringUtil.nullToBlank(params[1])));
						if(!StringUtil.nullToBlank(params[2]).equals("")) currLp.setValue_02(Double.parseDouble(StringUtil.nullToBlank(params[2])));
						if(!StringUtil.nullToBlank(params[3]).equals("")) currLp.setValue_03(Double.parseDouble(StringUtil.nullToBlank(params[3])));
						if(!StringUtil.nullToBlank(params[4]).equals("")) currLp.setValue_04(Double.parseDouble(StringUtil.nullToBlank(params[4])));
						if(!StringUtil.nullToBlank(params[5]).equals("")) currLp.setValue_05(Double.parseDouble(StringUtil.nullToBlank(params[5])));
						if(!StringUtil.nullToBlank(params[6]).equals("")) currLp.setValue_06(Double.parseDouble(StringUtil.nullToBlank(params[6])));
						if(!StringUtil.nullToBlank(params[7]).equals("")) currLp.setValue_07(Double.parseDouble(StringUtil.nullToBlank(params[7])));
						if(!StringUtil.nullToBlank(params[8]).equals("")) currLp.setValue_08(Double.parseDouble(StringUtil.nullToBlank(params[8])));
						if(!StringUtil.nullToBlank(params[9]).equals("")) currLp.setValue_09(Double.parseDouble(StringUtil.nullToBlank(params[9])));
						if(!StringUtil.nullToBlank(params[10]).equals("")) currLp.setValue_10(Double.parseDouble(StringUtil.nullToBlank(params[10])));
						
						if(!StringUtil.nullToBlank(params[11]).equals("")) currLp.setValue_11(Double.parseDouble(StringUtil.nullToBlank(params[11])));
						if(!StringUtil.nullToBlank(params[12]).equals("")) currLp.setValue_12(Double.parseDouble(StringUtil.nullToBlank(params[12])));
						if(!StringUtil.nullToBlank(params[13]).equals("")) currLp.setValue_13(Double.parseDouble(StringUtil.nullToBlank(params[13])));
						if(!StringUtil.nullToBlank(params[14]).equals("")) currLp.setValue_14(Double.parseDouble(StringUtil.nullToBlank(params[14])));
						if(!StringUtil.nullToBlank(params[15]).equals("")) currLp.setValue_15(Double.parseDouble(StringUtil.nullToBlank(params[15])));
						if(!StringUtil.nullToBlank(params[16]).equals("")) currLp.setValue_16(Double.parseDouble(StringUtil.nullToBlank(params[16])));
						if(!StringUtil.nullToBlank(params[17]).equals("")) currLp.setValue_17(Double.parseDouble(StringUtil.nullToBlank(params[17])));
						if(!StringUtil.nullToBlank(params[18]).equals("")) currLp.setValue_18(Double.parseDouble(StringUtil.nullToBlank(params[18])));
						if(!StringUtil.nullToBlank(params[19]).equals("")) currLp.setValue_19(Double.parseDouble(StringUtil.nullToBlank(params[19])));
						if(!StringUtil.nullToBlank(params[20]).equals("")) currLp.setValue_20(Double.parseDouble(StringUtil.nullToBlank(params[20])));
						
						if(!StringUtil.nullToBlank(params[21]).equals("")) currLp.setValue_21(Double.parseDouble(StringUtil.nullToBlank(params[21])));
						if(!StringUtil.nullToBlank(params[22]).equals("")) currLp.setValue_22(Double.parseDouble(StringUtil.nullToBlank(params[22])));
						if(!StringUtil.nullToBlank(params[23]).equals("")) currLp.setValue_23(Double.parseDouble(StringUtil.nullToBlank(params[23])));
						if(!StringUtil.nullToBlank(params[24]).equals("")) currLp.setValue_24(Double.parseDouble(StringUtil.nullToBlank(params[24])));
						if(!StringUtil.nullToBlank(params[25]).equals("")) currLp.setValue_25(Double.parseDouble(StringUtil.nullToBlank(params[25])));
						if(!StringUtil.nullToBlank(params[26]).equals("")) currLp.setValue_26(Double.parseDouble(StringUtil.nullToBlank(params[26])));
						if(!StringUtil.nullToBlank(params[27]).equals("")) currLp.setValue_27(Double.parseDouble(StringUtil.nullToBlank(params[27])));
						if(!StringUtil.nullToBlank(params[28]).equals("")) currLp.setValue_28(Double.parseDouble(StringUtil.nullToBlank(params[28])));
						if(!StringUtil.nullToBlank(params[29]).equals("")) currLp.setValue_29(Double.parseDouble(StringUtil.nullToBlank(params[29])));
						if(!StringUtil.nullToBlank(params[30]).equals("")) currLp.setValue_30(Double.parseDouble(StringUtil.nullToBlank(params[30])));
						
						if(!StringUtil.nullToBlank(params[31]).equals("")) currLp.setValue_31(Double.parseDouble(StringUtil.nullToBlank(params[31])));
						if(!StringUtil.nullToBlank(params[32]).equals("")) currLp.setValue_32(Double.parseDouble(StringUtil.nullToBlank(params[32])));
						if(!StringUtil.nullToBlank(params[33]).equals("")) currLp.setValue_33(Double.parseDouble(StringUtil.nullToBlank(params[33])));
						if(!StringUtil.nullToBlank(params[34]).equals("")) currLp.setValue_34(Double.parseDouble(StringUtil.nullToBlank(params[34])));
						if(!StringUtil.nullToBlank(params[35]).equals("")) currLp.setValue_35(Double.parseDouble(StringUtil.nullToBlank(params[35])));
						if(!StringUtil.nullToBlank(params[36]).equals("")) currLp.setValue_36(Double.parseDouble(StringUtil.nullToBlank(params[36])));
						if(!StringUtil.nullToBlank(params[37]).equals("")) currLp.setValue_37(Double.parseDouble(StringUtil.nullToBlank(params[37])));
						if(!StringUtil.nullToBlank(params[38]).equals("")) currLp.setValue_38(Double.parseDouble(StringUtil.nullToBlank(params[38])));
						if(!StringUtil.nullToBlank(params[39]).equals("")) currLp.setValue_39(Double.parseDouble(StringUtil.nullToBlank(params[39])));
						if(!StringUtil.nullToBlank(params[40]).equals("")) currLp.setValue_40(Double.parseDouble(StringUtil.nullToBlank(params[40])));
						
						if(!StringUtil.nullToBlank(params[41]).equals("")) currLp.setValue_41(Double.parseDouble(StringUtil.nullToBlank(params[41])));
						if(!StringUtil.nullToBlank(params[42]).equals("")) currLp.setValue_42(Double.parseDouble(StringUtil.nullToBlank(params[42])));
						if(!StringUtil.nullToBlank(params[43]).equals("")) currLp.setValue_43(Double.parseDouble(StringUtil.nullToBlank(params[43])));
						if(!StringUtil.nullToBlank(params[44]).equals("")) currLp.setValue_44(Double.parseDouble(StringUtil.nullToBlank(params[44])));
						if(!StringUtil.nullToBlank(params[45]).equals("")) currLp.setValue_45(Double.parseDouble(StringUtil.nullToBlank(params[45])));
						if(!StringUtil.nullToBlank(params[46]).equals("")) currLp.setValue_46(Double.parseDouble(StringUtil.nullToBlank(params[46])));
						if(!StringUtil.nullToBlank(params[47]).equals("")) currLp.setValue_47(Double.parseDouble(StringUtil.nullToBlank(params[47])));
						if(!StringUtil.nullToBlank(params[48]).equals("")) currLp.setValue_48(Double.parseDouble(StringUtil.nullToBlank(params[48])));
						if(!StringUtil.nullToBlank(params[49]).equals("")) currLp.setValue_49(Double.parseDouble(StringUtil.nullToBlank(params[49])));
						if(!StringUtil.nullToBlank(params[50]).equals("")) currLp.setValue_50(Double.parseDouble(StringUtil.nullToBlank(params[50])));
						
						if(!StringUtil.nullToBlank(params[51]).equals("")) currLp.setValue_51(Double.parseDouble(StringUtil.nullToBlank(params[51])));
						if(!StringUtil.nullToBlank(params[52]).equals("")) currLp.setValue_52(Double.parseDouble(StringUtil.nullToBlank(params[52])));
						if(!StringUtil.nullToBlank(params[53]).equals("")) currLp.setValue_53(Double.parseDouble(StringUtil.nullToBlank(params[53])));
						if(!StringUtil.nullToBlank(params[54]).equals("")) currLp.setValue_54(Double.parseDouble(StringUtil.nullToBlank(params[54])));
						if(!StringUtil.nullToBlank(params[55]).equals("")) currLp.setValue_55(Double.parseDouble(StringUtil.nullToBlank(params[55])));
						if(!StringUtil.nullToBlank(params[56]).equals("")) currLp.setValue_56(Double.parseDouble(StringUtil.nullToBlank(params[56])));
						if(!StringUtil.nullToBlank(params[57]).equals("")) currLp.setValue_57(Double.parseDouble(StringUtil.nullToBlank(params[57])));
						if(!StringUtil.nullToBlank(params[58]).equals("")) currLp.setValue_58(Double.parseDouble(StringUtil.nullToBlank(params[58])));
						if(!StringUtil.nullToBlank(params[59]).equals("")) currLp.setValue_59(Double.parseDouble(StringUtil.nullToBlank(params[59])));
						
						lpEMDao.saveOrUpdate((LpEM)currLp);
						
						/* 2. day_xx update */
						Map<String, Object> renewDayEM = dayemUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, lpData.getHh(), userId, currLp, EditItem.AutomaticEstimated.toString());
						
						/*3. month_xx table update start*/
						monthemUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, lpData.getHh(), userId, (DayEM)renewDayEM.get("returnValue"), EditItem.AutomaticEstimated.toString());
						
					} else if(existRealData && isNullData) {
						String table = "";
						
						LpPk lppk = new LpPk();								
						lppk.setYyyymmddhh(lpData.getYyyymmdd() + lpData.getHh());
						lppk.setChannel(Integer.parseInt(lpData.getChannel()));
						lppk.setMDevType(lpData.getMdev_type());
						lppk.setMDevId(lpData.getMdev_id());
						lppk.setDst(Integer.parseInt(lpData.getDst()));
						
						Set<Condition> findSet = new HashSet<Condition>();
						findSet.add(new Condition("id.yyyymmddhh",new Object[]{lppk.getYyyymmddhh()},null,Restriction.EQ));
						findSet.add(new Condition("id.channel",new Object[]{lppk.getChannel()},null,Restriction.EQ));
						findSet.add(new Condition("id.mdevType",new Object[]{lppk.getMDevType()},null,Restriction.EQ));
						findSet.add(new Condition("id.mdevId",new Object[]{lppk.getMDevId()},null,Restriction.EQ));
						findSet.add(new Condition("id.dst",new Object[]{lppk.getDst()},null,Restriction.EQ));
						
						MeteringLP currLp = null;
						
						table = CommonConstants.MeterType.EnergyMeter.getLpClassName();				
						currLp = lpEMDao.getLpEMsByListCondition(findSet).get(0);
						
						String[] params = new String[60];
						
						for(int k=0; k<60 ; k++) {
							try {
								String value = String.valueOf(VEEMaxDetailData.class.getMethod("getValue_" + String.format("%02d", k)).invoke(lpData));
								params[k] = StringUtil.nullToBlank(value);									
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							}
						}
						
						for(int k=0 ; k<60 ; k++) {
							if(k % lpInterval == 0 && !params[k].equals("")) {
								insertVEELog_Lp(currLp, yyyymmdd, lpData.getHh(), table, "Value_" + String.format("%02d", k), "", params[k], userId, lppk, EditItem.AutomaticEstimated.toString());
							}
						}
						
						if(!StringUtil.nullToBlank(params[0]).equals("")) currLp.setValue_00(Double.parseDouble(StringUtil.nullToBlank(params[0])));
						if(!StringUtil.nullToBlank(params[1]).equals("")) currLp.setValue_01(Double.parseDouble(StringUtil.nullToBlank(params[1])));
						if(!StringUtil.nullToBlank(params[2]).equals("")) currLp.setValue_02(Double.parseDouble(StringUtil.nullToBlank(params[2])));
						if(!StringUtil.nullToBlank(params[3]).equals("")) currLp.setValue_03(Double.parseDouble(StringUtil.nullToBlank(params[3])));
						if(!StringUtil.nullToBlank(params[4]).equals("")) currLp.setValue_04(Double.parseDouble(StringUtil.nullToBlank(params[4])));
						if(!StringUtil.nullToBlank(params[5]).equals("")) currLp.setValue_05(Double.parseDouble(StringUtil.nullToBlank(params[5])));
						if(!StringUtil.nullToBlank(params[6]).equals("")) currLp.setValue_06(Double.parseDouble(StringUtil.nullToBlank(params[6])));
						if(!StringUtil.nullToBlank(params[7]).equals("")) currLp.setValue_07(Double.parseDouble(StringUtil.nullToBlank(params[7])));
						if(!StringUtil.nullToBlank(params[8]).equals("")) currLp.setValue_08(Double.parseDouble(StringUtil.nullToBlank(params[8])));
						if(!StringUtil.nullToBlank(params[9]).equals("")) currLp.setValue_09(Double.parseDouble(StringUtil.nullToBlank(params[9])));
						if(!StringUtil.nullToBlank(params[10]).equals("")) currLp.setValue_10(Double.parseDouble(StringUtil.nullToBlank(params[10])));
						
						if(!StringUtil.nullToBlank(params[11]).equals("")) currLp.setValue_11(Double.parseDouble(StringUtil.nullToBlank(params[11])));
						if(!StringUtil.nullToBlank(params[12]).equals("")) currLp.setValue_12(Double.parseDouble(StringUtil.nullToBlank(params[12])));
						if(!StringUtil.nullToBlank(params[13]).equals("")) currLp.setValue_13(Double.parseDouble(StringUtil.nullToBlank(params[13])));
						if(!StringUtil.nullToBlank(params[14]).equals("")) currLp.setValue_14(Double.parseDouble(StringUtil.nullToBlank(params[14])));
						if(!StringUtil.nullToBlank(params[15]).equals("")) currLp.setValue_15(Double.parseDouble(StringUtil.nullToBlank(params[15])));
						if(!StringUtil.nullToBlank(params[16]).equals("")) currLp.setValue_16(Double.parseDouble(StringUtil.nullToBlank(params[16])));
						if(!StringUtil.nullToBlank(params[17]).equals("")) currLp.setValue_17(Double.parseDouble(StringUtil.nullToBlank(params[17])));
						if(!StringUtil.nullToBlank(params[18]).equals("")) currLp.setValue_18(Double.parseDouble(StringUtil.nullToBlank(params[18])));
						if(!StringUtil.nullToBlank(params[19]).equals("")) currLp.setValue_19(Double.parseDouble(StringUtil.nullToBlank(params[19])));
						if(!StringUtil.nullToBlank(params[20]).equals("")) currLp.setValue_20(Double.parseDouble(StringUtil.nullToBlank(params[20])));
						
						if(!StringUtil.nullToBlank(params[21]).equals("")) currLp.setValue_21(Double.parseDouble(StringUtil.nullToBlank(params[21])));
						if(!StringUtil.nullToBlank(params[22]).equals("")) currLp.setValue_22(Double.parseDouble(StringUtil.nullToBlank(params[22])));
						if(!StringUtil.nullToBlank(params[23]).equals("")) currLp.setValue_23(Double.parseDouble(StringUtil.nullToBlank(params[23])));
						if(!StringUtil.nullToBlank(params[24]).equals("")) currLp.setValue_24(Double.parseDouble(StringUtil.nullToBlank(params[24])));
						if(!StringUtil.nullToBlank(params[25]).equals("")) currLp.setValue_25(Double.parseDouble(StringUtil.nullToBlank(params[25])));
						if(!StringUtil.nullToBlank(params[26]).equals("")) currLp.setValue_26(Double.parseDouble(StringUtil.nullToBlank(params[26])));
						if(!StringUtil.nullToBlank(params[27]).equals("")) currLp.setValue_27(Double.parseDouble(StringUtil.nullToBlank(params[27])));
						if(!StringUtil.nullToBlank(params[28]).equals("")) currLp.setValue_28(Double.parseDouble(StringUtil.nullToBlank(params[28])));
						if(!StringUtil.nullToBlank(params[29]).equals("")) currLp.setValue_29(Double.parseDouble(StringUtil.nullToBlank(params[29])));
						if(!StringUtil.nullToBlank(params[30]).equals("")) currLp.setValue_30(Double.parseDouble(StringUtil.nullToBlank(params[30])));
						
						if(!StringUtil.nullToBlank(params[31]).equals("")) currLp.setValue_31(Double.parseDouble(StringUtil.nullToBlank(params[31])));
						if(!StringUtil.nullToBlank(params[32]).equals("")) currLp.setValue_32(Double.parseDouble(StringUtil.nullToBlank(params[32])));
						if(!StringUtil.nullToBlank(params[33]).equals("")) currLp.setValue_33(Double.parseDouble(StringUtil.nullToBlank(params[33])));
						if(!StringUtil.nullToBlank(params[34]).equals("")) currLp.setValue_34(Double.parseDouble(StringUtil.nullToBlank(params[34])));
						if(!StringUtil.nullToBlank(params[35]).equals("")) currLp.setValue_35(Double.parseDouble(StringUtil.nullToBlank(params[35])));
						if(!StringUtil.nullToBlank(params[36]).equals("")) currLp.setValue_36(Double.parseDouble(StringUtil.nullToBlank(params[36])));
						if(!StringUtil.nullToBlank(params[37]).equals("")) currLp.setValue_37(Double.parseDouble(StringUtil.nullToBlank(params[37])));
						if(!StringUtil.nullToBlank(params[38]).equals("")) currLp.setValue_38(Double.parseDouble(StringUtil.nullToBlank(params[38])));
						if(!StringUtil.nullToBlank(params[39]).equals("")) currLp.setValue_39(Double.parseDouble(StringUtil.nullToBlank(params[39])));
						if(!StringUtil.nullToBlank(params[40]).equals("")) currLp.setValue_40(Double.parseDouble(StringUtil.nullToBlank(params[40])));
						
						if(!StringUtil.nullToBlank(params[41]).equals("")) currLp.setValue_41(Double.parseDouble(StringUtil.nullToBlank(params[41])));
						if(!StringUtil.nullToBlank(params[42]).equals("")) currLp.setValue_42(Double.parseDouble(StringUtil.nullToBlank(params[42])));
						if(!StringUtil.nullToBlank(params[43]).equals("")) currLp.setValue_43(Double.parseDouble(StringUtil.nullToBlank(params[43])));
						if(!StringUtil.nullToBlank(params[44]).equals("")) currLp.setValue_44(Double.parseDouble(StringUtil.nullToBlank(params[44])));
						if(!StringUtil.nullToBlank(params[45]).equals("")) currLp.setValue_45(Double.parseDouble(StringUtil.nullToBlank(params[45])));
						if(!StringUtil.nullToBlank(params[46]).equals("")) currLp.setValue_46(Double.parseDouble(StringUtil.nullToBlank(params[46])));
						if(!StringUtil.nullToBlank(params[47]).equals("")) currLp.setValue_47(Double.parseDouble(StringUtil.nullToBlank(params[47])));
						if(!StringUtil.nullToBlank(params[48]).equals("")) currLp.setValue_48(Double.parseDouble(StringUtil.nullToBlank(params[48])));
						if(!StringUtil.nullToBlank(params[49]).equals("")) currLp.setValue_49(Double.parseDouble(StringUtil.nullToBlank(params[49])));
						if(!StringUtil.nullToBlank(params[50]).equals("")) currLp.setValue_50(Double.parseDouble(StringUtil.nullToBlank(params[50])));
						
						if(!StringUtil.nullToBlank(params[51]).equals("")) currLp.setValue_51(Double.parseDouble(StringUtil.nullToBlank(params[51])));
						if(!StringUtil.nullToBlank(params[52]).equals("")) currLp.setValue_52(Double.parseDouble(StringUtil.nullToBlank(params[52])));
						if(!StringUtil.nullToBlank(params[53]).equals("")) currLp.setValue_53(Double.parseDouble(StringUtil.nullToBlank(params[53])));
						if(!StringUtil.nullToBlank(params[54]).equals("")) currLp.setValue_54(Double.parseDouble(StringUtil.nullToBlank(params[54])));
						if(!StringUtil.nullToBlank(params[55]).equals("")) currLp.setValue_55(Double.parseDouble(StringUtil.nullToBlank(params[55])));
						if(!StringUtil.nullToBlank(params[56]).equals("")) currLp.setValue_56(Double.parseDouble(StringUtil.nullToBlank(params[56])));
						if(!StringUtil.nullToBlank(params[57]).equals("")) currLp.setValue_57(Double.parseDouble(StringUtil.nullToBlank(params[57])));
						if(!StringUtil.nullToBlank(params[58]).equals("")) currLp.setValue_58(Double.parseDouble(StringUtil.nullToBlank(params[58])));
						if(!StringUtil.nullToBlank(params[59]).equals("")) currLp.setValue_59(Double.parseDouble(StringUtil.nullToBlank(params[59])));
						
						lpEMDao.update((LpEM)currLp);
						
						/* 2. day_xx update */
						Map<String, Object> renewDayEM = dayemUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, lpData.getHh(), userId, currLp, EditItem.AutomaticEstimated.toString());
						
						/*3. month_xx table update start*/
						monthemUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, lpData.getHh(), userId, (DayEM)renewDayEM.get("returnValue"), EditItem.AutomaticEstimated.toString());						
					}
				}
			} else if(CommonConstants.MeterType.GasMeter.getLpClassName().equals(lpClassName)){
				List<LpGM> gmList = lpGMDao.getLpGMsByListCondition(set);
				
				for(int i=0 ; i<24 ; i++) {
					VEEMaxDetailData lpData = lpList.get(i);
					
					lpData.setYyyymmdd(yyyymmdd);
					lpData.setChannel(channel);
					lpData.setMdev_id(mdevId);
					lpData.setMdev_type(mdevType);
					lpData.setDst(dst);
					lpData.setHh(String.format("%02d", i));
					lpData.setYyyymmddhh(TimeLocaleUtil.getLocaleDate(yyyymmdd, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter())+ " " + String.format("%02d", i));
					lpData.setRealData("N");
					lpData.setIsAutoEstimation("Y");
					
					boolean existRealData = false;
					boolean isNullData = true;
					for(int j=0; j<gmList.size(); j++) {
						if(String.format("%02d", i).equals(gmList.get(j).getHour())){
							LpGM lpReal = gmList.get(j);
							existRealData = true;
							try {
								for(int k=0 ; k < 60 ; k++) {
									if(k % lpInterval == 0) {
										if(LpGM.class.getMethod("getValue_" + String.format("%02d", k)).invoke(lpReal) != null) {
											isNullData = false;
											break;
										}
									}
								}
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							
							if(existRealData) break;
						}
					}
					
					if(!existRealData) {
						String table = "";
						
						LpPk lppk = new LpPk();								
						lppk.setYyyymmddhh(lpData.getYyyymmdd() + lpData.getHh());
						lppk.setChannel(Integer.parseInt(lpData.getChannel()));
						lppk.setMDevType(lpData.getMdev_type());
						lppk.setMDevId(lpData.getMdev_id());
						lppk.setDst(Integer.parseInt(lpData.getDst()));
						
						MeteringLP currLp = null;
						
						table = CommonConstants.MeterType.GasMeter.getLpClassName();				
						currLp = new LpGM();						
						currLp.setId(lppk);
						currLp.setYyyymmdd(lpData.getYyyymmdd());
						currLp.setHour(lpData.getHh());
						currLp.setWriteDate(CalendarUtil.getCurrentDate());
						currLp.setSupplier(supplier);
						
						String[] params = new String[60];
						
						for(int k=0; k<60 ; k++) {
							try {
								String value = String.valueOf(VEEMaxDetailData.class.getMethod("getValue_" + String.format("%02d", k)).invoke(lpData));
								params[k] = StringUtil.nullToBlank(value);									
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							}
						}
						
						for(int k=0 ; k<60 ; k++) {
							if(k % lpInterval == 0 && !params[k].equals("")) {
								insertVEELog_Lp(currLp, yyyymmdd, lpData.getHh(), table, "Value_" + String.format("%02d", k), "", params[k], userId, lppk, EditItem.AutomaticEstimated.toString());
							}
						}
						
						if(!StringUtil.nullToBlank(params[0]).equals("")) currLp.setValue_00(Double.parseDouble(StringUtil.nullToBlank(params[0])));
						if(!StringUtil.nullToBlank(params[1]).equals("")) currLp.setValue_01(Double.parseDouble(StringUtil.nullToBlank(params[1])));
						if(!StringUtil.nullToBlank(params[2]).equals("")) currLp.setValue_02(Double.parseDouble(StringUtil.nullToBlank(params[2])));
						if(!StringUtil.nullToBlank(params[3]).equals("")) currLp.setValue_03(Double.parseDouble(StringUtil.nullToBlank(params[3])));
						if(!StringUtil.nullToBlank(params[4]).equals("")) currLp.setValue_04(Double.parseDouble(StringUtil.nullToBlank(params[4])));
						if(!StringUtil.nullToBlank(params[5]).equals("")) currLp.setValue_05(Double.parseDouble(StringUtil.nullToBlank(params[5])));
						if(!StringUtil.nullToBlank(params[6]).equals("")) currLp.setValue_06(Double.parseDouble(StringUtil.nullToBlank(params[6])));
						if(!StringUtil.nullToBlank(params[7]).equals("")) currLp.setValue_07(Double.parseDouble(StringUtil.nullToBlank(params[7])));
						if(!StringUtil.nullToBlank(params[8]).equals("")) currLp.setValue_08(Double.parseDouble(StringUtil.nullToBlank(params[8])));
						if(!StringUtil.nullToBlank(params[9]).equals("")) currLp.setValue_09(Double.parseDouble(StringUtil.nullToBlank(params[9])));
						if(!StringUtil.nullToBlank(params[10]).equals("")) currLp.setValue_10(Double.parseDouble(StringUtil.nullToBlank(params[10])));
						
						if(!StringUtil.nullToBlank(params[11]).equals("")) currLp.setValue_11(Double.parseDouble(StringUtil.nullToBlank(params[11])));
						if(!StringUtil.nullToBlank(params[12]).equals("")) currLp.setValue_12(Double.parseDouble(StringUtil.nullToBlank(params[12])));
						if(!StringUtil.nullToBlank(params[13]).equals("")) currLp.setValue_13(Double.parseDouble(StringUtil.nullToBlank(params[13])));
						if(!StringUtil.nullToBlank(params[14]).equals("")) currLp.setValue_14(Double.parseDouble(StringUtil.nullToBlank(params[14])));
						if(!StringUtil.nullToBlank(params[15]).equals("")) currLp.setValue_15(Double.parseDouble(StringUtil.nullToBlank(params[15])));
						if(!StringUtil.nullToBlank(params[16]).equals("")) currLp.setValue_16(Double.parseDouble(StringUtil.nullToBlank(params[16])));
						if(!StringUtil.nullToBlank(params[17]).equals("")) currLp.setValue_17(Double.parseDouble(StringUtil.nullToBlank(params[17])));
						if(!StringUtil.nullToBlank(params[18]).equals("")) currLp.setValue_18(Double.parseDouble(StringUtil.nullToBlank(params[18])));
						if(!StringUtil.nullToBlank(params[19]).equals("")) currLp.setValue_19(Double.parseDouble(StringUtil.nullToBlank(params[19])));
						if(!StringUtil.nullToBlank(params[20]).equals("")) currLp.setValue_20(Double.parseDouble(StringUtil.nullToBlank(params[20])));
						
						if(!StringUtil.nullToBlank(params[21]).equals("")) currLp.setValue_21(Double.parseDouble(StringUtil.nullToBlank(params[21])));
						if(!StringUtil.nullToBlank(params[22]).equals("")) currLp.setValue_22(Double.parseDouble(StringUtil.nullToBlank(params[22])));
						if(!StringUtil.nullToBlank(params[23]).equals("")) currLp.setValue_23(Double.parseDouble(StringUtil.nullToBlank(params[23])));
						if(!StringUtil.nullToBlank(params[24]).equals("")) currLp.setValue_24(Double.parseDouble(StringUtil.nullToBlank(params[24])));
						if(!StringUtil.nullToBlank(params[25]).equals("")) currLp.setValue_25(Double.parseDouble(StringUtil.nullToBlank(params[25])));
						if(!StringUtil.nullToBlank(params[26]).equals("")) currLp.setValue_26(Double.parseDouble(StringUtil.nullToBlank(params[26])));
						if(!StringUtil.nullToBlank(params[27]).equals("")) currLp.setValue_27(Double.parseDouble(StringUtil.nullToBlank(params[27])));
						if(!StringUtil.nullToBlank(params[28]).equals("")) currLp.setValue_28(Double.parseDouble(StringUtil.nullToBlank(params[28])));
						if(!StringUtil.nullToBlank(params[29]).equals("")) currLp.setValue_29(Double.parseDouble(StringUtil.nullToBlank(params[29])));
						if(!StringUtil.nullToBlank(params[30]).equals("")) currLp.setValue_30(Double.parseDouble(StringUtil.nullToBlank(params[30])));
						
						if(!StringUtil.nullToBlank(params[31]).equals("")) currLp.setValue_31(Double.parseDouble(StringUtil.nullToBlank(params[31])));
						if(!StringUtil.nullToBlank(params[32]).equals("")) currLp.setValue_32(Double.parseDouble(StringUtil.nullToBlank(params[32])));
						if(!StringUtil.nullToBlank(params[33]).equals("")) currLp.setValue_33(Double.parseDouble(StringUtil.nullToBlank(params[33])));
						if(!StringUtil.nullToBlank(params[34]).equals("")) currLp.setValue_34(Double.parseDouble(StringUtil.nullToBlank(params[34])));
						if(!StringUtil.nullToBlank(params[35]).equals("")) currLp.setValue_35(Double.parseDouble(StringUtil.nullToBlank(params[35])));
						if(!StringUtil.nullToBlank(params[36]).equals("")) currLp.setValue_36(Double.parseDouble(StringUtil.nullToBlank(params[36])));
						if(!StringUtil.nullToBlank(params[37]).equals("")) currLp.setValue_37(Double.parseDouble(StringUtil.nullToBlank(params[37])));
						if(!StringUtil.nullToBlank(params[38]).equals("")) currLp.setValue_38(Double.parseDouble(StringUtil.nullToBlank(params[38])));
						if(!StringUtil.nullToBlank(params[39]).equals("")) currLp.setValue_39(Double.parseDouble(StringUtil.nullToBlank(params[39])));
						if(!StringUtil.nullToBlank(params[40]).equals("")) currLp.setValue_40(Double.parseDouble(StringUtil.nullToBlank(params[40])));
						
						if(!StringUtil.nullToBlank(params[41]).equals("")) currLp.setValue_41(Double.parseDouble(StringUtil.nullToBlank(params[41])));
						if(!StringUtil.nullToBlank(params[42]).equals("")) currLp.setValue_42(Double.parseDouble(StringUtil.nullToBlank(params[42])));
						if(!StringUtil.nullToBlank(params[43]).equals("")) currLp.setValue_43(Double.parseDouble(StringUtil.nullToBlank(params[43])));
						if(!StringUtil.nullToBlank(params[44]).equals("")) currLp.setValue_44(Double.parseDouble(StringUtil.nullToBlank(params[44])));
						if(!StringUtil.nullToBlank(params[45]).equals("")) currLp.setValue_45(Double.parseDouble(StringUtil.nullToBlank(params[45])));
						if(!StringUtil.nullToBlank(params[46]).equals("")) currLp.setValue_46(Double.parseDouble(StringUtil.nullToBlank(params[46])));
						if(!StringUtil.nullToBlank(params[47]).equals("")) currLp.setValue_47(Double.parseDouble(StringUtil.nullToBlank(params[47])));
						if(!StringUtil.nullToBlank(params[48]).equals("")) currLp.setValue_48(Double.parseDouble(StringUtil.nullToBlank(params[48])));
						if(!StringUtil.nullToBlank(params[49]).equals("")) currLp.setValue_49(Double.parseDouble(StringUtil.nullToBlank(params[49])));
						if(!StringUtil.nullToBlank(params[50]).equals("")) currLp.setValue_50(Double.parseDouble(StringUtil.nullToBlank(params[50])));
						
						if(!StringUtil.nullToBlank(params[51]).equals("")) currLp.setValue_51(Double.parseDouble(StringUtil.nullToBlank(params[51])));
						if(!StringUtil.nullToBlank(params[52]).equals("")) currLp.setValue_52(Double.parseDouble(StringUtil.nullToBlank(params[52])));
						if(!StringUtil.nullToBlank(params[53]).equals("")) currLp.setValue_53(Double.parseDouble(StringUtil.nullToBlank(params[53])));
						if(!StringUtil.nullToBlank(params[54]).equals("")) currLp.setValue_54(Double.parseDouble(StringUtil.nullToBlank(params[54])));
						if(!StringUtil.nullToBlank(params[55]).equals("")) currLp.setValue_55(Double.parseDouble(StringUtil.nullToBlank(params[55])));
						if(!StringUtil.nullToBlank(params[56]).equals("")) currLp.setValue_56(Double.parseDouble(StringUtil.nullToBlank(params[56])));
						if(!StringUtil.nullToBlank(params[57]).equals("")) currLp.setValue_57(Double.parseDouble(StringUtil.nullToBlank(params[57])));
						if(!StringUtil.nullToBlank(params[58]).equals("")) currLp.setValue_58(Double.parseDouble(StringUtil.nullToBlank(params[58])));
						if(!StringUtil.nullToBlank(params[59]).equals("")) currLp.setValue_59(Double.parseDouble(StringUtil.nullToBlank(params[59])));
						
						lpGMDao.saveOrUpdate((LpGM)currLp);
						
						/* 2. day_xx update */
						Map<String, Object> renewDayGM = daygmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, lpData.getHh(), userId, currLp, EditItem.AutomaticEstimated.toString());
						
						/*3. month_xx table update start*/
						monthgmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, lpData.getHh(), userId, (DayGM)renewDayGM.get("returnValue"), EditItem.AutomaticEstimated.toString());
						
					} else if(existRealData && isNullData) {
						String table = "";
						
						LpPk lppk = new LpPk();								
						lppk.setYyyymmddhh(lpData.getYyyymmdd() + lpData.getHh());
						lppk.setChannel(Integer.parseInt(lpData.getChannel()));
						lppk.setMDevType(lpData.getMdev_type());
						lppk.setMDevId(lpData.getMdev_id());
						lppk.setDst(Integer.parseInt(lpData.getDst()));
						
						Set<Condition> findSet = new HashSet<Condition>();
						findSet.add(new Condition("id.yyyymmddhh",new Object[]{lppk.getYyyymmddhh()},null,Restriction.EQ));
						findSet.add(new Condition("id.channel",new Object[]{lppk.getChannel()},null,Restriction.EQ));
						findSet.add(new Condition("id.mdevType",new Object[]{lppk.getMDevType()},null,Restriction.EQ));
						findSet.add(new Condition("id.mdevId",new Object[]{lppk.getMDevId()},null,Restriction.EQ));
						findSet.add(new Condition("id.dst",new Object[]{lppk.getDst()},null,Restriction.EQ));
						
						MeteringLP currLp = null;
						
						table = CommonConstants.MeterType.GasMeter.getLpClassName();				
						currLp = lpGMDao.getLpGMsByListCondition(findSet).get(0);
						
						String[] params = new String[60];
						
						for(int k=0; k<60 ; k++) {
							try {
								String value = String.valueOf(VEEMaxDetailData.class.getMethod("getValue_" + String.format("%02d", k)).invoke(lpData));
								params[k] = StringUtil.nullToBlank(value);									
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							}
						}
						
						for(int k=0 ; k<60 ; k++) {
							if(k % lpInterval == 0 && !params[k].equals("")) {
								insertVEELog_Lp(currLp, yyyymmdd, lpData.getHh(), table, "Value_" + String.format("%02d", k), "", params[k], userId, lppk, EditItem.AutomaticEstimated.toString());
							}
						}
						
						if(!StringUtil.nullToBlank(params[0]).equals("")) currLp.setValue_00(Double.parseDouble(StringUtil.nullToBlank(params[0])));
						if(!StringUtil.nullToBlank(params[1]).equals("")) currLp.setValue_01(Double.parseDouble(StringUtil.nullToBlank(params[1])));
						if(!StringUtil.nullToBlank(params[2]).equals("")) currLp.setValue_02(Double.parseDouble(StringUtil.nullToBlank(params[2])));
						if(!StringUtil.nullToBlank(params[3]).equals("")) currLp.setValue_03(Double.parseDouble(StringUtil.nullToBlank(params[3])));
						if(!StringUtil.nullToBlank(params[4]).equals("")) currLp.setValue_04(Double.parseDouble(StringUtil.nullToBlank(params[4])));
						if(!StringUtil.nullToBlank(params[5]).equals("")) currLp.setValue_05(Double.parseDouble(StringUtil.nullToBlank(params[5])));
						if(!StringUtil.nullToBlank(params[6]).equals("")) currLp.setValue_06(Double.parseDouble(StringUtil.nullToBlank(params[6])));
						if(!StringUtil.nullToBlank(params[7]).equals("")) currLp.setValue_07(Double.parseDouble(StringUtil.nullToBlank(params[7])));
						if(!StringUtil.nullToBlank(params[8]).equals("")) currLp.setValue_08(Double.parseDouble(StringUtil.nullToBlank(params[8])));
						if(!StringUtil.nullToBlank(params[9]).equals("")) currLp.setValue_09(Double.parseDouble(StringUtil.nullToBlank(params[9])));
						if(!StringUtil.nullToBlank(params[10]).equals("")) currLp.setValue_10(Double.parseDouble(StringUtil.nullToBlank(params[10])));
						
						if(!StringUtil.nullToBlank(params[11]).equals("")) currLp.setValue_11(Double.parseDouble(StringUtil.nullToBlank(params[11])));
						if(!StringUtil.nullToBlank(params[12]).equals("")) currLp.setValue_12(Double.parseDouble(StringUtil.nullToBlank(params[12])));
						if(!StringUtil.nullToBlank(params[13]).equals("")) currLp.setValue_13(Double.parseDouble(StringUtil.nullToBlank(params[13])));
						if(!StringUtil.nullToBlank(params[14]).equals("")) currLp.setValue_14(Double.parseDouble(StringUtil.nullToBlank(params[14])));
						if(!StringUtil.nullToBlank(params[15]).equals("")) currLp.setValue_15(Double.parseDouble(StringUtil.nullToBlank(params[15])));
						if(!StringUtil.nullToBlank(params[16]).equals("")) currLp.setValue_16(Double.parseDouble(StringUtil.nullToBlank(params[16])));
						if(!StringUtil.nullToBlank(params[17]).equals("")) currLp.setValue_17(Double.parseDouble(StringUtil.nullToBlank(params[17])));
						if(!StringUtil.nullToBlank(params[18]).equals("")) currLp.setValue_18(Double.parseDouble(StringUtil.nullToBlank(params[18])));
						if(!StringUtil.nullToBlank(params[19]).equals("")) currLp.setValue_19(Double.parseDouble(StringUtil.nullToBlank(params[19])));
						if(!StringUtil.nullToBlank(params[20]).equals("")) currLp.setValue_20(Double.parseDouble(StringUtil.nullToBlank(params[20])));
						
						if(!StringUtil.nullToBlank(params[21]).equals("")) currLp.setValue_21(Double.parseDouble(StringUtil.nullToBlank(params[21])));
						if(!StringUtil.nullToBlank(params[22]).equals("")) currLp.setValue_22(Double.parseDouble(StringUtil.nullToBlank(params[22])));
						if(!StringUtil.nullToBlank(params[23]).equals("")) currLp.setValue_23(Double.parseDouble(StringUtil.nullToBlank(params[23])));
						if(!StringUtil.nullToBlank(params[24]).equals("")) currLp.setValue_24(Double.parseDouble(StringUtil.nullToBlank(params[24])));
						if(!StringUtil.nullToBlank(params[25]).equals("")) currLp.setValue_25(Double.parseDouble(StringUtil.nullToBlank(params[25])));
						if(!StringUtil.nullToBlank(params[26]).equals("")) currLp.setValue_26(Double.parseDouble(StringUtil.nullToBlank(params[26])));
						if(!StringUtil.nullToBlank(params[27]).equals("")) currLp.setValue_27(Double.parseDouble(StringUtil.nullToBlank(params[27])));
						if(!StringUtil.nullToBlank(params[28]).equals("")) currLp.setValue_28(Double.parseDouble(StringUtil.nullToBlank(params[28])));
						if(!StringUtil.nullToBlank(params[29]).equals("")) currLp.setValue_29(Double.parseDouble(StringUtil.nullToBlank(params[29])));
						if(!StringUtil.nullToBlank(params[30]).equals("")) currLp.setValue_30(Double.parseDouble(StringUtil.nullToBlank(params[30])));
						
						if(!StringUtil.nullToBlank(params[31]).equals("")) currLp.setValue_31(Double.parseDouble(StringUtil.nullToBlank(params[31])));
						if(!StringUtil.nullToBlank(params[32]).equals("")) currLp.setValue_32(Double.parseDouble(StringUtil.nullToBlank(params[32])));
						if(!StringUtil.nullToBlank(params[33]).equals("")) currLp.setValue_33(Double.parseDouble(StringUtil.nullToBlank(params[33])));
						if(!StringUtil.nullToBlank(params[34]).equals("")) currLp.setValue_34(Double.parseDouble(StringUtil.nullToBlank(params[34])));
						if(!StringUtil.nullToBlank(params[35]).equals("")) currLp.setValue_35(Double.parseDouble(StringUtil.nullToBlank(params[35])));
						if(!StringUtil.nullToBlank(params[36]).equals("")) currLp.setValue_36(Double.parseDouble(StringUtil.nullToBlank(params[36])));
						if(!StringUtil.nullToBlank(params[37]).equals("")) currLp.setValue_37(Double.parseDouble(StringUtil.nullToBlank(params[37])));
						if(!StringUtil.nullToBlank(params[38]).equals("")) currLp.setValue_38(Double.parseDouble(StringUtil.nullToBlank(params[38])));
						if(!StringUtil.nullToBlank(params[39]).equals("")) currLp.setValue_39(Double.parseDouble(StringUtil.nullToBlank(params[39])));
						if(!StringUtil.nullToBlank(params[40]).equals("")) currLp.setValue_40(Double.parseDouble(StringUtil.nullToBlank(params[40])));
						
						if(!StringUtil.nullToBlank(params[41]).equals("")) currLp.setValue_41(Double.parseDouble(StringUtil.nullToBlank(params[41])));
						if(!StringUtil.nullToBlank(params[42]).equals("")) currLp.setValue_42(Double.parseDouble(StringUtil.nullToBlank(params[42])));
						if(!StringUtil.nullToBlank(params[43]).equals("")) currLp.setValue_43(Double.parseDouble(StringUtil.nullToBlank(params[43])));
						if(!StringUtil.nullToBlank(params[44]).equals("")) currLp.setValue_44(Double.parseDouble(StringUtil.nullToBlank(params[44])));
						if(!StringUtil.nullToBlank(params[45]).equals("")) currLp.setValue_45(Double.parseDouble(StringUtil.nullToBlank(params[45])));
						if(!StringUtil.nullToBlank(params[46]).equals("")) currLp.setValue_46(Double.parseDouble(StringUtil.nullToBlank(params[46])));
						if(!StringUtil.nullToBlank(params[47]).equals("")) currLp.setValue_47(Double.parseDouble(StringUtil.nullToBlank(params[47])));
						if(!StringUtil.nullToBlank(params[48]).equals("")) currLp.setValue_48(Double.parseDouble(StringUtil.nullToBlank(params[48])));
						if(!StringUtil.nullToBlank(params[49]).equals("")) currLp.setValue_49(Double.parseDouble(StringUtil.nullToBlank(params[49])));
						if(!StringUtil.nullToBlank(params[50]).equals("")) currLp.setValue_50(Double.parseDouble(StringUtil.nullToBlank(params[50])));
						
						if(!StringUtil.nullToBlank(params[51]).equals("")) currLp.setValue_51(Double.parseDouble(StringUtil.nullToBlank(params[51])));
						if(!StringUtil.nullToBlank(params[52]).equals("")) currLp.setValue_52(Double.parseDouble(StringUtil.nullToBlank(params[52])));
						if(!StringUtil.nullToBlank(params[53]).equals("")) currLp.setValue_53(Double.parseDouble(StringUtil.nullToBlank(params[53])));
						if(!StringUtil.nullToBlank(params[54]).equals("")) currLp.setValue_54(Double.parseDouble(StringUtil.nullToBlank(params[54])));
						if(!StringUtil.nullToBlank(params[55]).equals("")) currLp.setValue_55(Double.parseDouble(StringUtil.nullToBlank(params[55])));
						if(!StringUtil.nullToBlank(params[56]).equals("")) currLp.setValue_56(Double.parseDouble(StringUtil.nullToBlank(params[56])));
						if(!StringUtil.nullToBlank(params[57]).equals("")) currLp.setValue_57(Double.parseDouble(StringUtil.nullToBlank(params[57])));
						if(!StringUtil.nullToBlank(params[58]).equals("")) currLp.setValue_58(Double.parseDouble(StringUtil.nullToBlank(params[58])));
						if(!StringUtil.nullToBlank(params[59]).equals("")) currLp.setValue_59(Double.parseDouble(StringUtil.nullToBlank(params[59])));
						
						lpGMDao.update((LpGM)currLp);
						
						/* 2. day_xx update */
						Map<String, Object> renewDayGM = daygmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, lpData.getHh(), userId, currLp, EditItem.AutomaticEstimated.toString());
						
						/*3. month_xx table update start*/
						monthgmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, lpData.getHh(), userId, (DayGM)renewDayGM.get("returnValue"), EditItem.AutomaticEstimated.toString());						
					}
				}
			} else if(CommonConstants.MeterType.WaterMeter.getLpClassName().equals(lpClassName)){
				List<LpWM> wmList = lpWMDao.getLpWMsByListCondition(set);
			
				for(int i=0 ; i<24 ; i++) {
					VEEMaxDetailData lpData = lpList.get(i);
					
					lpData.setYyyymmdd(yyyymmdd);
					lpData.setChannel(channel);
					lpData.setMdev_id(mdevId);
					lpData.setMdev_type(mdevType);
					lpData.setDst(dst);
					lpData.setHh(String.format("%02d", i));
					lpData.setYyyymmddhh(TimeLocaleUtil.getLocaleDate(yyyymmdd, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter())+ " " + String.format("%02d", i));
					lpData.setRealData("N");
					lpData.setIsAutoEstimation("Y");
					
					boolean existRealData = false;
					boolean isNullData = true;
					for(int j=0; j<wmList.size(); j++) {
						if(String.format("%02d", i).equals(wmList.get(j).getHour())){
							LpWM lpReal = wmList.get(j);
							existRealData = true;
							try {
								for(int k=0 ; k < 60 ; k++) {
									if(k % lpInterval == 0) {
										if(LpWM.class.getMethod("getValue_" + String.format("%02d", k)).invoke(lpReal) != null) {
											isNullData = false;
											break;
										}
									}
								}
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							
							if(existRealData) break;
						}
					}
					
					if(!existRealData) {
						String table = "";
						
						LpPk lppk = new LpPk();								
						lppk.setYyyymmddhh(lpData.getYyyymmdd() + lpData.getHh());
						lppk.setChannel(Integer.parseInt(lpData.getChannel()));
						lppk.setMDevType(lpData.getMdev_type());
						lppk.setMDevId(lpData.getMdev_id());
						lppk.setDst(Integer.parseInt(lpData.getDst()));
						
						MeteringLP currLp = null;
						
						table = CommonConstants.MeterType.WaterMeter.getLpClassName();
						
						currLp = new LpWM();						
						currLp.setId(lppk);
						currLp.setYyyymmdd(lpData.getYyyymmdd());
						currLp.setHour(lpData.getHh());
						currLp.setWriteDate(CalendarUtil.getCurrentDate());
						currLp.setSupplier(supplier);
						
						String[] params = new String[60];
						
						for(int k=0; k<60 ; k++) {
							try {
								String value = String.valueOf(VEEMaxDetailData.class.getMethod("getValue_" + String.format("%02d", k)).invoke(lpData));
								params[k] = StringUtil.nullToBlank(value);									
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							}
						}
						
						for(int k=0 ; k<60 ; k++) {
							if(k % lpInterval == 0 && !params[k].equals("")) {
								insertVEELog_Lp(currLp, yyyymmdd, lpData.getHh(), table, "Value_" + String.format("%02d", k), "", params[k], userId, lppk, EditItem.AutomaticEstimated.toString());
							}
						}
						
						if(!StringUtil.nullToBlank(params[0]).equals("")) currLp.setValue_00(Double.parseDouble(StringUtil.nullToBlank(params[0])));
						if(!StringUtil.nullToBlank(params[1]).equals("")) currLp.setValue_01(Double.parseDouble(StringUtil.nullToBlank(params[1])));
						if(!StringUtil.nullToBlank(params[2]).equals("")) currLp.setValue_02(Double.parseDouble(StringUtil.nullToBlank(params[2])));
						if(!StringUtil.nullToBlank(params[3]).equals("")) currLp.setValue_03(Double.parseDouble(StringUtil.nullToBlank(params[3])));
						if(!StringUtil.nullToBlank(params[4]).equals("")) currLp.setValue_04(Double.parseDouble(StringUtil.nullToBlank(params[4])));
						if(!StringUtil.nullToBlank(params[5]).equals("")) currLp.setValue_05(Double.parseDouble(StringUtil.nullToBlank(params[5])));
						if(!StringUtil.nullToBlank(params[6]).equals("")) currLp.setValue_06(Double.parseDouble(StringUtil.nullToBlank(params[6])));
						if(!StringUtil.nullToBlank(params[7]).equals("")) currLp.setValue_07(Double.parseDouble(StringUtil.nullToBlank(params[7])));
						if(!StringUtil.nullToBlank(params[8]).equals("")) currLp.setValue_08(Double.parseDouble(StringUtil.nullToBlank(params[8])));
						if(!StringUtil.nullToBlank(params[9]).equals("")) currLp.setValue_09(Double.parseDouble(StringUtil.nullToBlank(params[9])));
						if(!StringUtil.nullToBlank(params[10]).equals("")) currLp.setValue_10(Double.parseDouble(StringUtil.nullToBlank(params[10])));
						
						if(!StringUtil.nullToBlank(params[11]).equals("")) currLp.setValue_11(Double.parseDouble(StringUtil.nullToBlank(params[11])));
						if(!StringUtil.nullToBlank(params[12]).equals("")) currLp.setValue_12(Double.parseDouble(StringUtil.nullToBlank(params[12])));
						if(!StringUtil.nullToBlank(params[13]).equals("")) currLp.setValue_13(Double.parseDouble(StringUtil.nullToBlank(params[13])));
						if(!StringUtil.nullToBlank(params[14]).equals("")) currLp.setValue_14(Double.parseDouble(StringUtil.nullToBlank(params[14])));
						if(!StringUtil.nullToBlank(params[15]).equals("")) currLp.setValue_15(Double.parseDouble(StringUtil.nullToBlank(params[15])));
						if(!StringUtil.nullToBlank(params[16]).equals("")) currLp.setValue_16(Double.parseDouble(StringUtil.nullToBlank(params[16])));
						if(!StringUtil.nullToBlank(params[17]).equals("")) currLp.setValue_17(Double.parseDouble(StringUtil.nullToBlank(params[17])));
						if(!StringUtil.nullToBlank(params[18]).equals("")) currLp.setValue_18(Double.parseDouble(StringUtil.nullToBlank(params[18])));
						if(!StringUtil.nullToBlank(params[19]).equals("")) currLp.setValue_19(Double.parseDouble(StringUtil.nullToBlank(params[19])));
						if(!StringUtil.nullToBlank(params[20]).equals("")) currLp.setValue_20(Double.parseDouble(StringUtil.nullToBlank(params[20])));
						
						if(!StringUtil.nullToBlank(params[21]).equals("")) currLp.setValue_21(Double.parseDouble(StringUtil.nullToBlank(params[21])));
						if(!StringUtil.nullToBlank(params[22]).equals("")) currLp.setValue_22(Double.parseDouble(StringUtil.nullToBlank(params[22])));
						if(!StringUtil.nullToBlank(params[23]).equals("")) currLp.setValue_23(Double.parseDouble(StringUtil.nullToBlank(params[23])));
						if(!StringUtil.nullToBlank(params[24]).equals("")) currLp.setValue_24(Double.parseDouble(StringUtil.nullToBlank(params[24])));
						if(!StringUtil.nullToBlank(params[25]).equals("")) currLp.setValue_25(Double.parseDouble(StringUtil.nullToBlank(params[25])));
						if(!StringUtil.nullToBlank(params[26]).equals("")) currLp.setValue_26(Double.parseDouble(StringUtil.nullToBlank(params[26])));
						if(!StringUtil.nullToBlank(params[27]).equals("")) currLp.setValue_27(Double.parseDouble(StringUtil.nullToBlank(params[27])));
						if(!StringUtil.nullToBlank(params[28]).equals("")) currLp.setValue_28(Double.parseDouble(StringUtil.nullToBlank(params[28])));
						if(!StringUtil.nullToBlank(params[29]).equals("")) currLp.setValue_29(Double.parseDouble(StringUtil.nullToBlank(params[29])));
						if(!StringUtil.nullToBlank(params[30]).equals("")) currLp.setValue_30(Double.parseDouble(StringUtil.nullToBlank(params[30])));
						
						if(!StringUtil.nullToBlank(params[31]).equals("")) currLp.setValue_31(Double.parseDouble(StringUtil.nullToBlank(params[31])));
						if(!StringUtil.nullToBlank(params[32]).equals("")) currLp.setValue_32(Double.parseDouble(StringUtil.nullToBlank(params[32])));
						if(!StringUtil.nullToBlank(params[33]).equals("")) currLp.setValue_33(Double.parseDouble(StringUtil.nullToBlank(params[33])));
						if(!StringUtil.nullToBlank(params[34]).equals("")) currLp.setValue_34(Double.parseDouble(StringUtil.nullToBlank(params[34])));
						if(!StringUtil.nullToBlank(params[35]).equals("")) currLp.setValue_35(Double.parseDouble(StringUtil.nullToBlank(params[35])));
						if(!StringUtil.nullToBlank(params[36]).equals("")) currLp.setValue_36(Double.parseDouble(StringUtil.nullToBlank(params[36])));
						if(!StringUtil.nullToBlank(params[37]).equals("")) currLp.setValue_37(Double.parseDouble(StringUtil.nullToBlank(params[37])));
						if(!StringUtil.nullToBlank(params[38]).equals("")) currLp.setValue_38(Double.parseDouble(StringUtil.nullToBlank(params[38])));
						if(!StringUtil.nullToBlank(params[39]).equals("")) currLp.setValue_39(Double.parseDouble(StringUtil.nullToBlank(params[39])));
						if(!StringUtil.nullToBlank(params[40]).equals("")) currLp.setValue_40(Double.parseDouble(StringUtil.nullToBlank(params[40])));
						
						if(!StringUtil.nullToBlank(params[41]).equals("")) currLp.setValue_41(Double.parseDouble(StringUtil.nullToBlank(params[41])));
						if(!StringUtil.nullToBlank(params[42]).equals("")) currLp.setValue_42(Double.parseDouble(StringUtil.nullToBlank(params[42])));
						if(!StringUtil.nullToBlank(params[43]).equals("")) currLp.setValue_43(Double.parseDouble(StringUtil.nullToBlank(params[43])));
						if(!StringUtil.nullToBlank(params[44]).equals("")) currLp.setValue_44(Double.parseDouble(StringUtil.nullToBlank(params[44])));
						if(!StringUtil.nullToBlank(params[45]).equals("")) currLp.setValue_45(Double.parseDouble(StringUtil.nullToBlank(params[45])));
						if(!StringUtil.nullToBlank(params[46]).equals("")) currLp.setValue_46(Double.parseDouble(StringUtil.nullToBlank(params[46])));
						if(!StringUtil.nullToBlank(params[47]).equals("")) currLp.setValue_47(Double.parseDouble(StringUtil.nullToBlank(params[47])));
						if(!StringUtil.nullToBlank(params[48]).equals("")) currLp.setValue_48(Double.parseDouble(StringUtil.nullToBlank(params[48])));
						if(!StringUtil.nullToBlank(params[49]).equals("")) currLp.setValue_49(Double.parseDouble(StringUtil.nullToBlank(params[49])));
						if(!StringUtil.nullToBlank(params[50]).equals("")) currLp.setValue_50(Double.parseDouble(StringUtil.nullToBlank(params[50])));
						
						if(!StringUtil.nullToBlank(params[51]).equals("")) currLp.setValue_51(Double.parseDouble(StringUtil.nullToBlank(params[51])));
						if(!StringUtil.nullToBlank(params[52]).equals("")) currLp.setValue_52(Double.parseDouble(StringUtil.nullToBlank(params[52])));
						if(!StringUtil.nullToBlank(params[53]).equals("")) currLp.setValue_53(Double.parseDouble(StringUtil.nullToBlank(params[53])));
						if(!StringUtil.nullToBlank(params[54]).equals("")) currLp.setValue_54(Double.parseDouble(StringUtil.nullToBlank(params[54])));
						if(!StringUtil.nullToBlank(params[55]).equals("")) currLp.setValue_55(Double.parseDouble(StringUtil.nullToBlank(params[55])));
						if(!StringUtil.nullToBlank(params[56]).equals("")) currLp.setValue_56(Double.parseDouble(StringUtil.nullToBlank(params[56])));
						if(!StringUtil.nullToBlank(params[57]).equals("")) currLp.setValue_57(Double.parseDouble(StringUtil.nullToBlank(params[57])));
						if(!StringUtil.nullToBlank(params[58]).equals("")) currLp.setValue_58(Double.parseDouble(StringUtil.nullToBlank(params[58])));
						if(!StringUtil.nullToBlank(params[59]).equals("")) currLp.setValue_59(Double.parseDouble(StringUtil.nullToBlank(params[59])));
						
						lpWMDao.saveOrUpdate((LpWM)currLp);
						
						/* 2. day_xx update */
						Map<String, Object> renewDayWM = daywmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, lpData.getHh(), userId, currLp, EditItem.AutomaticEstimated.toString());
						
						/*3. month_xx table update start*/
						monthwmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, lpData.getHh(), userId, (DayWM)renewDayWM.get("returnValue"), EditItem.AutomaticEstimated.toString());
						
					} else if(existRealData && isNullData) {
						String table = "";
						
						LpPk lppk = new LpPk();								
						lppk.setYyyymmddhh(lpData.getYyyymmdd() + lpData.getHh());
						lppk.setChannel(Integer.parseInt(lpData.getChannel()));
						lppk.setMDevType(lpData.getMdev_type());
						lppk.setMDevId(lpData.getMdev_id());
						lppk.setDst(Integer.parseInt(lpData.getDst()));
						
						Set<Condition> findSet = new HashSet<Condition>();
						findSet.add(new Condition("id.yyyymmddhh",new Object[]{lppk.getYyyymmddhh()},null,Restriction.EQ));
						findSet.add(new Condition("id.channel",new Object[]{lppk.getChannel()},null,Restriction.EQ));
						findSet.add(new Condition("id.mdevType",new Object[]{lppk.getMDevType()},null,Restriction.EQ));
						findSet.add(new Condition("id.mdevId",new Object[]{lppk.getMDevId()},null,Restriction.EQ));
						findSet.add(new Condition("id.dst",new Object[]{lppk.getDst()},null,Restriction.EQ));
						
						MeteringLP currLp = null;
						
						table = CommonConstants.MeterType.WaterMeter.getLpClassName();				
						currLp = lpWMDao.getLpWMsByListCondition(findSet).get(0);
						
						String[] params = new String[60];
						
						for(int k=0; k<60 ; k++) {
							try {
								String value = String.valueOf(VEEMaxDetailData.class.getMethod("getValue_" + String.format("%02d", k)).invoke(lpData));
								params[k] = StringUtil.nullToBlank(value);									
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							}
						}
						
						for(int k=0 ; k<60 ; k++) {
							if(k % lpInterval == 0 && !params[k].equals("")) {
								insertVEELog_Lp(currLp, yyyymmdd, lpData.getHh(), table, "Value_" + String.format("%02d", k), "", params[k], userId, lppk, EditItem.AutomaticEstimated.toString());
							}
						}
						
						if(!StringUtil.nullToBlank(params[0]).equals("")) currLp.setValue_00(Double.parseDouble(StringUtil.nullToBlank(params[0])));
						if(!StringUtil.nullToBlank(params[1]).equals("")) currLp.setValue_01(Double.parseDouble(StringUtil.nullToBlank(params[1])));
						if(!StringUtil.nullToBlank(params[2]).equals("")) currLp.setValue_02(Double.parseDouble(StringUtil.nullToBlank(params[2])));
						if(!StringUtil.nullToBlank(params[3]).equals("")) currLp.setValue_03(Double.parseDouble(StringUtil.nullToBlank(params[3])));
						if(!StringUtil.nullToBlank(params[4]).equals("")) currLp.setValue_04(Double.parseDouble(StringUtil.nullToBlank(params[4])));
						if(!StringUtil.nullToBlank(params[5]).equals("")) currLp.setValue_05(Double.parseDouble(StringUtil.nullToBlank(params[5])));
						if(!StringUtil.nullToBlank(params[6]).equals("")) currLp.setValue_06(Double.parseDouble(StringUtil.nullToBlank(params[6])));
						if(!StringUtil.nullToBlank(params[7]).equals("")) currLp.setValue_07(Double.parseDouble(StringUtil.nullToBlank(params[7])));
						if(!StringUtil.nullToBlank(params[8]).equals("")) currLp.setValue_08(Double.parseDouble(StringUtil.nullToBlank(params[8])));
						if(!StringUtil.nullToBlank(params[9]).equals("")) currLp.setValue_09(Double.parseDouble(StringUtil.nullToBlank(params[9])));
						if(!StringUtil.nullToBlank(params[10]).equals("")) currLp.setValue_10(Double.parseDouble(StringUtil.nullToBlank(params[10])));
						
						if(!StringUtil.nullToBlank(params[11]).equals("")) currLp.setValue_11(Double.parseDouble(StringUtil.nullToBlank(params[11])));
						if(!StringUtil.nullToBlank(params[12]).equals("")) currLp.setValue_12(Double.parseDouble(StringUtil.nullToBlank(params[12])));
						if(!StringUtil.nullToBlank(params[13]).equals("")) currLp.setValue_13(Double.parseDouble(StringUtil.nullToBlank(params[13])));
						if(!StringUtil.nullToBlank(params[14]).equals("")) currLp.setValue_14(Double.parseDouble(StringUtil.nullToBlank(params[14])));
						if(!StringUtil.nullToBlank(params[15]).equals("")) currLp.setValue_15(Double.parseDouble(StringUtil.nullToBlank(params[15])));
						if(!StringUtil.nullToBlank(params[16]).equals("")) currLp.setValue_16(Double.parseDouble(StringUtil.nullToBlank(params[16])));
						if(!StringUtil.nullToBlank(params[17]).equals("")) currLp.setValue_17(Double.parseDouble(StringUtil.nullToBlank(params[17])));
						if(!StringUtil.nullToBlank(params[18]).equals("")) currLp.setValue_18(Double.parseDouble(StringUtil.nullToBlank(params[18])));
						if(!StringUtil.nullToBlank(params[19]).equals("")) currLp.setValue_19(Double.parseDouble(StringUtil.nullToBlank(params[19])));
						if(!StringUtil.nullToBlank(params[20]).equals("")) currLp.setValue_20(Double.parseDouble(StringUtil.nullToBlank(params[20])));
						
						if(!StringUtil.nullToBlank(params[21]).equals("")) currLp.setValue_21(Double.parseDouble(StringUtil.nullToBlank(params[21])));
						if(!StringUtil.nullToBlank(params[22]).equals("")) currLp.setValue_22(Double.parseDouble(StringUtil.nullToBlank(params[22])));
						if(!StringUtil.nullToBlank(params[23]).equals("")) currLp.setValue_23(Double.parseDouble(StringUtil.nullToBlank(params[23])));
						if(!StringUtil.nullToBlank(params[24]).equals("")) currLp.setValue_24(Double.parseDouble(StringUtil.nullToBlank(params[24])));
						if(!StringUtil.nullToBlank(params[25]).equals("")) currLp.setValue_25(Double.parseDouble(StringUtil.nullToBlank(params[25])));
						if(!StringUtil.nullToBlank(params[26]).equals("")) currLp.setValue_26(Double.parseDouble(StringUtil.nullToBlank(params[26])));
						if(!StringUtil.nullToBlank(params[27]).equals("")) currLp.setValue_27(Double.parseDouble(StringUtil.nullToBlank(params[27])));
						if(!StringUtil.nullToBlank(params[28]).equals("")) currLp.setValue_28(Double.parseDouble(StringUtil.nullToBlank(params[28])));
						if(!StringUtil.nullToBlank(params[29]).equals("")) currLp.setValue_29(Double.parseDouble(StringUtil.nullToBlank(params[29])));
						if(!StringUtil.nullToBlank(params[30]).equals("")) currLp.setValue_30(Double.parseDouble(StringUtil.nullToBlank(params[30])));
						
						if(!StringUtil.nullToBlank(params[31]).equals("")) currLp.setValue_31(Double.parseDouble(StringUtil.nullToBlank(params[31])));
						if(!StringUtil.nullToBlank(params[32]).equals("")) currLp.setValue_32(Double.parseDouble(StringUtil.nullToBlank(params[32])));
						if(!StringUtil.nullToBlank(params[33]).equals("")) currLp.setValue_33(Double.parseDouble(StringUtil.nullToBlank(params[33])));
						if(!StringUtil.nullToBlank(params[34]).equals("")) currLp.setValue_34(Double.parseDouble(StringUtil.nullToBlank(params[34])));
						if(!StringUtil.nullToBlank(params[35]).equals("")) currLp.setValue_35(Double.parseDouble(StringUtil.nullToBlank(params[35])));
						if(!StringUtil.nullToBlank(params[36]).equals("")) currLp.setValue_36(Double.parseDouble(StringUtil.nullToBlank(params[36])));
						if(!StringUtil.nullToBlank(params[37]).equals("")) currLp.setValue_37(Double.parseDouble(StringUtil.nullToBlank(params[37])));
						if(!StringUtil.nullToBlank(params[38]).equals("")) currLp.setValue_38(Double.parseDouble(StringUtil.nullToBlank(params[38])));
						if(!StringUtil.nullToBlank(params[39]).equals("")) currLp.setValue_39(Double.parseDouble(StringUtil.nullToBlank(params[39])));
						if(!StringUtil.nullToBlank(params[40]).equals("")) currLp.setValue_40(Double.parseDouble(StringUtil.nullToBlank(params[40])));
						
						if(!StringUtil.nullToBlank(params[41]).equals("")) currLp.setValue_41(Double.parseDouble(StringUtil.nullToBlank(params[41])));
						if(!StringUtil.nullToBlank(params[42]).equals("")) currLp.setValue_42(Double.parseDouble(StringUtil.nullToBlank(params[42])));
						if(!StringUtil.nullToBlank(params[43]).equals("")) currLp.setValue_43(Double.parseDouble(StringUtil.nullToBlank(params[43])));
						if(!StringUtil.nullToBlank(params[44]).equals("")) currLp.setValue_44(Double.parseDouble(StringUtil.nullToBlank(params[44])));
						if(!StringUtil.nullToBlank(params[45]).equals("")) currLp.setValue_45(Double.parseDouble(StringUtil.nullToBlank(params[45])));
						if(!StringUtil.nullToBlank(params[46]).equals("")) currLp.setValue_46(Double.parseDouble(StringUtil.nullToBlank(params[46])));
						if(!StringUtil.nullToBlank(params[47]).equals("")) currLp.setValue_47(Double.parseDouble(StringUtil.nullToBlank(params[47])));
						if(!StringUtil.nullToBlank(params[48]).equals("")) currLp.setValue_48(Double.parseDouble(StringUtil.nullToBlank(params[48])));
						if(!StringUtil.nullToBlank(params[49]).equals("")) currLp.setValue_49(Double.parseDouble(StringUtil.nullToBlank(params[49])));
						if(!StringUtil.nullToBlank(params[50]).equals("")) currLp.setValue_50(Double.parseDouble(StringUtil.nullToBlank(params[50])));
						
						if(!StringUtil.nullToBlank(params[51]).equals("")) currLp.setValue_51(Double.parseDouble(StringUtil.nullToBlank(params[51])));
						if(!StringUtil.nullToBlank(params[52]).equals("")) currLp.setValue_52(Double.parseDouble(StringUtil.nullToBlank(params[52])));
						if(!StringUtil.nullToBlank(params[53]).equals("")) currLp.setValue_53(Double.parseDouble(StringUtil.nullToBlank(params[53])));
						if(!StringUtil.nullToBlank(params[54]).equals("")) currLp.setValue_54(Double.parseDouble(StringUtil.nullToBlank(params[54])));
						if(!StringUtil.nullToBlank(params[55]).equals("")) currLp.setValue_55(Double.parseDouble(StringUtil.nullToBlank(params[55])));
						if(!StringUtil.nullToBlank(params[56]).equals("")) currLp.setValue_56(Double.parseDouble(StringUtil.nullToBlank(params[56])));
						if(!StringUtil.nullToBlank(params[57]).equals("")) currLp.setValue_57(Double.parseDouble(StringUtil.nullToBlank(params[57])));
						if(!StringUtil.nullToBlank(params[58]).equals("")) currLp.setValue_58(Double.parseDouble(StringUtil.nullToBlank(params[58])));
						if(!StringUtil.nullToBlank(params[59]).equals("")) currLp.setValue_59(Double.parseDouble(StringUtil.nullToBlank(params[59])));
						
						lpWMDao.update((LpWM)currLp);
						
						/* 2. day_xx update */
						Map<String, Object> renewDayWM =daywmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, lpData.getHh(), userId, currLp, EditItem.AutomaticEstimated.toString());
						
						/*3. month_xx table update start*/
						monthwmUpdate(yyyymmdd, channel, mdevType, mdevId, dst, params, lpData.getHh(), userId, (DayWM)renewDayWM.get("returnValue"), EditItem.AutomaticEstimated.toString());						
					}
				}				
			}
		}

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("lpList", lpList);
		resultMap.put("lpInterval", lpInterval);
		
		return resultMap;
	}
	
	
	/*
	 * 해당 조건(전일, 전주, 전월, 현월 등)에 만족하는지 검증
	 * Interval에 따라 모든 값이 존재하는지 (null이 아닌지) 검증
	 * 모두 정상적으로 확인되면 true 반환
	 */
	@SuppressWarnings("unused")
    public List<VEEMaxDetailData> validateAutoEstimation(String meterType, String item, String yyyymmdd, String channel, String mdevType, String mdevId, String dst, String type, String supplierId) {
		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		Calendar ti = Calendar.getInstance();
		try {
			ti.setTime(dateformat.parse(yyyymmdd));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		
		DecimalFormat df = new DecimalFormat(supplier.getMd().getPattern());
		
		/*
		 * 기간 검증
		 */
		String preDate = "";
		String lastDate = "";
		 
		if(type.equals("0")) {	// Last Day
			ti.add(Calendar.DAY_OF_YEAR, -1);
			
			preDate = dateformat.format(ti.getTime());
		} else if(type.equals("1")) {	// Last Week
			ti.add(Calendar.WEEK_OF_YEAR, -1);
			
			preDate = dateformat.format(ti.getTime());
		} else if(type.equals("2")) {	// Last Month
			ti.add(Calendar.MONTH, -1);
			
			preDate = dateformat.format(ti.getTime());
			if(Integer.parseInt(yyyymmdd.substring(6)) != Integer.parseInt(preDate.substring(6))) {
				return null;	// 전월 동일인 날일 없음!!
			}
			
		} else if(type.equals("3")) {	// Current Month
			if(Integer.parseInt(yyyymmdd.substring(6)) <= 1) {
				return null;	// 해당 월 첫날임!!
			}
			preDate = yyyymmdd.substring(0, 6) + "01";
			
			ti.add(Calendar.DAY_OF_YEAR, -1);
			lastDate = dateformat.format(ti.getTime());
		}
		
		String lpClassName = "";
		int lpInterval = 60;
		
		List<VEEMaxDetailData> lpList = new ArrayList<VEEMaxDetailData>();
		
		if(CommonConstants.VEETableItem.Day.toString().equals(item) || CommonConstants.VEETableItem.Month.toString().equals(item) || CommonConstants.VEETableItem.LoadProfile.toString().equals(item)){
			lpClassName 	= CommonConstants.MeterType.valueOf(meterType).getLpClassName();
			
			Set<Condition> set = new HashSet<Condition>();
			
			if(!type.equals("3")) {
				set.add(new Condition("yyyymmdd",new Object[]{preDate},null,Restriction.EQ));
			} else {
				set.add(new Condition("yyyymmdd",new Object[]{preDate, lastDate},null,Restriction.BETWEEN));
			}
			
			if(!"".equals(channel)) set.add(new Condition("id.channel",new Object[]{Integer.parseInt(channel)},null,Restriction.EQ));
			if(!"".equals(mdevType)) set.add(new Condition("id.mdevType",new Object[]{CommonConstants.DeviceType.valueOf(mdevType)},null,Restriction.EQ));
			if(!"".equals(mdevId)) set.add(new Condition("id.mdevId",new Object[]{mdevId},null,Restriction.EQ));
			if(!"".equals(dst)) set.add(new Condition("id.dst",new Object[]{Integer.parseInt(dst)},null,Restriction.EQ));
			set.add(new Condition("hour", new Object[]{}, null, Restriction.ORDERBY));
			
			if(CommonConstants.MeterType.EnergyMeter.getLpClassName().equals(lpClassName)){
				List<LpEM> emList = lpEMDao.getLpEMsByListCondition(set);
				
				try {
					lpInterval = lpEMDao.getLpInterval(mdevId);
				} catch(Exception e) {}
				
				if(type.equals("3")) {
					if(emList.size() != ((Integer.parseInt(lastDate.substring(6)) - Integer.parseInt(preDate.substring(6)) + 1) * 24)) return null;
				} else {
					if(emList.size() != 24) return null;
				}
				
				for(int i=0; i < emList.size() ; i++) {
					LpEM lpEm = emList.get(i);
					try {
						for(int j=0 ; j < 60 ; j++) {
							if(j % lpInterval == 0) {								
								if(LpEM.class.getMethod("getValue_" + String.format("%02d", j)).invoke(lpEm) == null) {
									return null;
								}
							}
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
				String str = "aaa";
				if(type.equals("3")) {
					double duration = Integer.parseInt(lastDate.substring(6)) - Integer.parseInt(preDate.substring(6)) + 1;
					
					VEEMaxDetailData vmdd = null;
					for(int i=0; i < 24; i++){
						vmdd = new VEEMaxDetailData();
						lpList.add(vmdd);
					}
					
					for(int i=0; i<emList.size(); i++){
						vmdd = lpList.get(i % 24);
						
						vmdd.setValue_00(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_00())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_00()))));
						vmdd.setValue_01(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_01())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_01()))));
						vmdd.setValue_02(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_02())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_02()))));
						vmdd.setValue_03(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_03())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_03()))));
						vmdd.setValue_04(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_04())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_04()))));
						vmdd.setValue_05(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_05())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_05()))));
						vmdd.setValue_06(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_06())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_06()))));
						vmdd.setValue_07(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_07())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_07()))));
						vmdd.setValue_08(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_08())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_08()))));
						vmdd.setValue_09(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_09())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_09()))));
						
						vmdd.setValue_10(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_10())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_10()))));
						vmdd.setValue_11(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_11())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_11()))));
						vmdd.setValue_12(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_12())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_12()))));
						vmdd.setValue_13(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_13())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_13()))));
						vmdd.setValue_14(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_14())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_14()))));
						vmdd.setValue_15(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_15())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_15()))));
						vmdd.setValue_16(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_16())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_16()))));
						vmdd.setValue_17(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_17())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_17()))));
						vmdd.setValue_18(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_18())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_18()))));
						vmdd.setValue_19(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_19())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_19()))));
						
						vmdd.setValue_20(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_20())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_20()))));
						vmdd.setValue_21(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_21())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_21()))));
						vmdd.setValue_22(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_22())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_22()))));
						vmdd.setValue_23(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_23())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_23()))));
						vmdd.setValue_24(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_24())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_24()))));
						vmdd.setValue_25(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_25())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_25()))));
						vmdd.setValue_26(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_26())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_26()))));
						vmdd.setValue_27(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_27())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_27()))));
						vmdd.setValue_28(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_28())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_28()))));
						vmdd.setValue_29(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_29())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_29()))));
						
						vmdd.setValue_30(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_30())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_30()))));
						vmdd.setValue_31(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_31())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_31()))));
						vmdd.setValue_32(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_32())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_32()))));
						vmdd.setValue_33(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_33())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_33()))));
						vmdd.setValue_34(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_34())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_34()))));
						vmdd.setValue_35(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_35())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_35()))));
						vmdd.setValue_36(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_36())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_36()))));
						vmdd.setValue_37(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_37())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_37()))));
						vmdd.setValue_38(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_38())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_38()))));
						vmdd.setValue_39(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_39())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_39()))));
						
						vmdd.setValue_40(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_40())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_40()))));
						vmdd.setValue_41(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_41())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_41()))));
						vmdd.setValue_42(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_42())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_42()))));
						vmdd.setValue_43(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_43())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_43()))));
						vmdd.setValue_44(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_44())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_44()))));
						vmdd.setValue_45(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_45())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_45()))));
						vmdd.setValue_46(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_46())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_46()))));
						vmdd.setValue_47(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_47())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_47()))));
						vmdd.setValue_48(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_48())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_48()))));
						vmdd.setValue_49(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_49())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_49()))));
						
						vmdd.setValue_50(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_50())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_50()))));
						vmdd.setValue_51(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_51())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_51()))));
						vmdd.setValue_52(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_52())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_52()))));
						vmdd.setValue_53(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_53())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_53()))));
						vmdd.setValue_54(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_54())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_54()))));
						vmdd.setValue_55(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_55())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_55()))));
						vmdd.setValue_56(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_56())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_56()))));
						vmdd.setValue_57(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_57())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_57()))));
						vmdd.setValue_58(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_58())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_58()))));
						vmdd.setValue_59(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_59())) + Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_59()))));
					}
					
					for(int i=0; i < lpList.size() ; i++) {
						VEEMaxDetailData lpData = lpList.get(i);

						lpData.setValue_00(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_00())) / duration));
						lpData.setValue_01(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_01())) / duration));
						lpData.setValue_02(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_02())) / duration));
						lpData.setValue_03(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_03())) / duration));
						lpData.setValue_04(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_04())) / duration));
						lpData.setValue_05(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_05())) / duration));
						lpData.setValue_06(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_06())) / duration));
						lpData.setValue_07(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_07())) / duration));
						lpData.setValue_08(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_08())) / duration));
						lpData.setValue_09(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_09())) / duration));
						
						lpData.setValue_10(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_10())) / duration));
						lpData.setValue_11(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_11())) / duration));
						lpData.setValue_12(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_12())) / duration));
						lpData.setValue_13(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_13())) / duration));
						lpData.setValue_14(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_14())) / duration));
						lpData.setValue_15(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_15())) / duration));
						lpData.setValue_16(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_16())) / duration));
						lpData.setValue_17(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_17())) / duration));
						lpData.setValue_18(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_18())) / duration));
						lpData.setValue_19(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_19())) / duration));
						
						lpData.setValue_20(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_20())) / duration));
						lpData.setValue_21(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_21())) / duration));
						lpData.setValue_22(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_22())) / duration));
						lpData.setValue_23(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_23())) / duration));
						lpData.setValue_24(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_24())) / duration));
						lpData.setValue_25(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_25())) / duration));
						lpData.setValue_26(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_26())) / duration));
						lpData.setValue_27(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_27())) / duration));
						lpData.setValue_28(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_28())) / duration));
						lpData.setValue_29(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_29())) / duration));
						
						lpData.setValue_30(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_30())) / duration));
						lpData.setValue_31(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_31())) / duration));
						lpData.setValue_32(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_32())) / duration));
						lpData.setValue_33(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_33())) / duration));
						lpData.setValue_34(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_34())) / duration));
						lpData.setValue_35(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_35())) / duration));
						lpData.setValue_36(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_36())) / duration));
						lpData.setValue_37(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_37())) / duration));
						lpData.setValue_38(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_38())) / duration));
						lpData.setValue_39(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_39())) / duration));
						
						lpData.setValue_40(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_40())) / duration));
						lpData.setValue_41(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_41())) / duration));
						lpData.setValue_42(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_42())) / duration));
						lpData.setValue_43(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_43())) / duration));
						lpData.setValue_44(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_44())) / duration));
						lpData.setValue_45(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_45())) / duration));
						lpData.setValue_46(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_46())) / duration));
						lpData.setValue_47(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_47())) / duration));
						lpData.setValue_48(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_48())) / duration));
						lpData.setValue_49(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_49())) / duration));
						
						lpData.setValue_50(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_50())) / duration));
						lpData.setValue_51(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_51())) / duration));
						lpData.setValue_52(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_52())) / duration));
						lpData.setValue_53(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_53())) / duration));
						lpData.setValue_54(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_54())) / duration));
						lpData.setValue_55(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_55())) / duration));
						lpData.setValue_56(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_56())) / duration));
						lpData.setValue_57(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_57())) / duration));
						lpData.setValue_58(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_58())) / duration));
						lpData.setValue_59(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_59())) / duration));
					}
				} else {
					VEEMaxDetailData vmdd = null;
					
					for(int i=0; i<emList.size(); i++){
						vmdd = new VEEMaxDetailData();
						
						vmdd.setValue_00(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_00()))));
						vmdd.setValue_01(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_01()))));
						vmdd.setValue_02(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_02()))));
						vmdd.setValue_03(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_03()))));
						vmdd.setValue_04(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_04()))));
						vmdd.setValue_05(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_05()))));
						vmdd.setValue_06(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_06()))));
						vmdd.setValue_07(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_07()))));
						vmdd.setValue_08(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_08()))));
						vmdd.setValue_09(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_09()))));
						
						vmdd.setValue_10(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_10()))));
						vmdd.setValue_11(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_11()))));
						vmdd.setValue_12(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_12()))));
						vmdd.setValue_13(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_13()))));
						vmdd.setValue_14(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_14()))));
						vmdd.setValue_15(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_15()))));
						vmdd.setValue_16(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_16()))));
						vmdd.setValue_17(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_17()))));
						vmdd.setValue_18(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_18()))));
						vmdd.setValue_19(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_19()))));
						
						vmdd.setValue_20(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_20()))));
						vmdd.setValue_21(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_21()))));
						vmdd.setValue_22(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_22()))));
						vmdd.setValue_23(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_23()))));
						vmdd.setValue_24(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_24()))));
						vmdd.setValue_25(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_25()))));
						vmdd.setValue_26(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_26()))));
						vmdd.setValue_27(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_27()))));
						vmdd.setValue_28(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_28()))));
						vmdd.setValue_29(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_29()))));
						
						vmdd.setValue_30(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_30()))));
						vmdd.setValue_31(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_31()))));
						vmdd.setValue_32(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_32()))));
						vmdd.setValue_33(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_33()))));
						vmdd.setValue_34(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_34()))));
						vmdd.setValue_35(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_35()))));
						vmdd.setValue_36(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_36()))));
						vmdd.setValue_37(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_37()))));
						vmdd.setValue_38(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_38()))));
						vmdd.setValue_39(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_39()))));
						
						vmdd.setValue_40(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_40()))));
						vmdd.setValue_41(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_41()))));
						vmdd.setValue_42(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_42()))));
						vmdd.setValue_43(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_43()))));
						vmdd.setValue_44(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_44()))));
						vmdd.setValue_45(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_45()))));
						vmdd.setValue_46(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_46()))));
						vmdd.setValue_47(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_47()))));
						vmdd.setValue_48(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_48()))));
						vmdd.setValue_49(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_49()))));
						
						vmdd.setValue_50(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_50()))));
						vmdd.setValue_51(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_51()))));
						vmdd.setValue_52(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_52()))));
						vmdd.setValue_53(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_53()))));
						vmdd.setValue_54(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_54()))));
						vmdd.setValue_55(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_55()))));
						vmdd.setValue_56(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_56()))));
						vmdd.setValue_57(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_57()))));
						vmdd.setValue_58(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_58()))));
						vmdd.setValue_59(df.format(Double.parseDouble(StringUtil.nullToZero(((LpEM)emList.get(i)).getValue_59()))));
						
						lpList.add(vmdd);
					}
				}
				
			} else if(CommonConstants.MeterType.GasMeter.getLpClassName().equals(lpClassName)){
				List<LpGM> gmList = lpGMDao.getLpGMsByListCondition(set);

				try {
					lpInterval = lpGMDao.getLpInterval(mdevId);
				} catch(Exception e) {}
				
				if(type.equals("3")) {
					double duration = Integer.parseInt(lastDate.substring(6)) - Integer.parseInt(preDate.substring(6)) + 1;
					
					VEEMaxDetailData vmdd = null;
					for(int i=0; i < 24; i++){
						vmdd = new VEEMaxDetailData();
						lpList.add(vmdd);
					}
					
					for(int i=0; i<gmList.size(); i++){
						vmdd = lpList.get(i % 24);
						
						vmdd.setValue_00(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_00())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_00()))));
						vmdd.setValue_01(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_01())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_01()))));
						vmdd.setValue_02(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_02())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_02()))));
						vmdd.setValue_03(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_03())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_03()))));
						vmdd.setValue_04(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_04())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_04()))));
						vmdd.setValue_05(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_05())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_05()))));
						vmdd.setValue_06(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_06())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_06()))));
						vmdd.setValue_07(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_07())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_07()))));
						vmdd.setValue_08(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_08())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_08()))));
						vmdd.setValue_09(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_09())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_09()))));
						
						vmdd.setValue_10(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_10())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_10()))));
						vmdd.setValue_11(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_11())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_11()))));
						vmdd.setValue_12(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_12())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_12()))));
						vmdd.setValue_13(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_13())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_13()))));
						vmdd.setValue_14(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_14())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_14()))));
						vmdd.setValue_15(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_15())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_15()))));
						vmdd.setValue_16(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_16())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_16()))));
						vmdd.setValue_17(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_17())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_17()))));
						vmdd.setValue_18(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_18())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_18()))));
						vmdd.setValue_19(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_19())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_19()))));
						
						vmdd.setValue_20(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_20())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_20()))));
						vmdd.setValue_21(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_21())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_21()))));
						vmdd.setValue_22(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_22())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_22()))));
						vmdd.setValue_23(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_23())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_23()))));
						vmdd.setValue_24(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_24())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_24()))));
						vmdd.setValue_25(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_25())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_25()))));
						vmdd.setValue_26(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_26())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_26()))));
						vmdd.setValue_27(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_27())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_27()))));
						vmdd.setValue_28(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_28())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_28()))));
						vmdd.setValue_29(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_29())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_29()))));
						
						vmdd.setValue_30(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_30())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_30()))));
						vmdd.setValue_31(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_31())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_31()))));
						vmdd.setValue_32(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_32())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_32()))));
						vmdd.setValue_33(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_33())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_33()))));
						vmdd.setValue_34(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_34())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_34()))));
						vmdd.setValue_35(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_35())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_35()))));
						vmdd.setValue_36(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_36())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_36()))));
						vmdd.setValue_37(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_37())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_37()))));
						vmdd.setValue_38(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_38())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_38()))));
						vmdd.setValue_39(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_39())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_39()))));
						
						vmdd.setValue_40(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_40())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_40()))));
						vmdd.setValue_41(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_41())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_41()))));
						vmdd.setValue_42(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_42())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_42()))));
						vmdd.setValue_43(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_43())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_43()))));
						vmdd.setValue_44(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_44())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_44()))));
						vmdd.setValue_45(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_45())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_45()))));
						vmdd.setValue_46(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_46())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_46()))));
						vmdd.setValue_47(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_47())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_47()))));
						vmdd.setValue_48(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_48())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_48()))));
						vmdd.setValue_49(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_49())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_49()))));
						
						vmdd.setValue_50(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_50())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_50()))));
						vmdd.setValue_51(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_51())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_51()))));
						vmdd.setValue_52(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_52())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_52()))));
						vmdd.setValue_53(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_53())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_53()))));
						vmdd.setValue_54(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_54())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_54()))));
						vmdd.setValue_55(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_55())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_55()))));
						vmdd.setValue_56(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_56())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_56()))));
						vmdd.setValue_57(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_57())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_57()))));
						vmdd.setValue_58(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_58())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_58()))));
						vmdd.setValue_59(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_59())) + Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_59()))));
					}
					
					for(int i=0; i < lpList.size() ; i++) {
						VEEMaxDetailData lpData = lpList.get(i);

						lpData.setValue_00(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_00())) / duration));
						lpData.setValue_01(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_01())) / duration));
						lpData.setValue_02(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_02())) / duration));
						lpData.setValue_03(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_03())) / duration));
						lpData.setValue_04(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_04())) / duration));
						lpData.setValue_05(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_05())) / duration));
						lpData.setValue_06(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_06())) / duration));
						lpData.setValue_07(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_07())) / duration));
						lpData.setValue_08(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_08())) / duration));
						lpData.setValue_09(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_09())) / duration));
						
						lpData.setValue_10(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_10())) / duration));
						lpData.setValue_11(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_11())) / duration));
						lpData.setValue_12(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_12())) / duration));
						lpData.setValue_13(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_13())) / duration));
						lpData.setValue_14(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_14())) / duration));
						lpData.setValue_15(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_15())) / duration));
						lpData.setValue_16(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_16())) / duration));
						lpData.setValue_17(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_17())) / duration));
						lpData.setValue_18(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_18())) / duration));
						lpData.setValue_19(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_19())) / duration));
						
						lpData.setValue_20(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_20())) / duration));
						lpData.setValue_21(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_21())) / duration));
						lpData.setValue_22(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_22())) / duration));
						lpData.setValue_23(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_23())) / duration));
						lpData.setValue_24(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_24())) / duration));
						lpData.setValue_25(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_25())) / duration));
						lpData.setValue_26(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_26())) / duration));
						lpData.setValue_27(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_27())) / duration));
						lpData.setValue_28(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_28())) / duration));
						lpData.setValue_29(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_29())) / duration));
						
						lpData.setValue_30(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_30())) / duration));
						lpData.setValue_31(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_31())) / duration));
						lpData.setValue_32(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_32())) / duration));
						lpData.setValue_33(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_33())) / duration));
						lpData.setValue_34(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_34())) / duration));
						lpData.setValue_35(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_35())) / duration));
						lpData.setValue_36(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_36())) / duration));
						lpData.setValue_37(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_37())) / duration));
						lpData.setValue_38(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_38())) / duration));
						lpData.setValue_39(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_39())) / duration));
						
						lpData.setValue_40(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_40())) / duration));
						lpData.setValue_41(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_41())) / duration));
						lpData.setValue_42(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_42())) / duration));
						lpData.setValue_43(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_43())) / duration));
						lpData.setValue_44(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_44())) / duration));
						lpData.setValue_45(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_45())) / duration));
						lpData.setValue_46(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_46())) / duration));
						lpData.setValue_47(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_47())) / duration));
						lpData.setValue_48(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_48())) / duration));
						lpData.setValue_49(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_49())) / duration));
						
						lpData.setValue_50(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_50())) / duration));
						lpData.setValue_51(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_51())) / duration));
						lpData.setValue_52(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_52())) / duration));
						lpData.setValue_53(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_53())) / duration));
						lpData.setValue_54(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_54())) / duration));
						lpData.setValue_55(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_55())) / duration));
						lpData.setValue_56(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_56())) / duration));
						lpData.setValue_57(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_57())) / duration));
						lpData.setValue_58(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_58())) / duration));
						lpData.setValue_59(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_59())) / duration));
					}
				} else {
					VEEMaxDetailData vmdd = null;
					
					for(int i=0; i<gmList.size(); i++){
						vmdd = new VEEMaxDetailData();
						
						vmdd.setValue_00(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_00()))));
						vmdd.setValue_01(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_01()))));
						vmdd.setValue_02(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_02()))));
						vmdd.setValue_03(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_03()))));
						vmdd.setValue_04(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_04()))));
						vmdd.setValue_05(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_05()))));
						vmdd.setValue_06(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_06()))));
						vmdd.setValue_07(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_07()))));
						vmdd.setValue_08(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_08()))));
						vmdd.setValue_09(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_09()))));
						
						vmdd.setValue_10(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_10()))));
						vmdd.setValue_11(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_11()))));
						vmdd.setValue_12(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_12()))));
						vmdd.setValue_13(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_13()))));
						vmdd.setValue_14(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_14()))));
						vmdd.setValue_15(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_15()))));
						vmdd.setValue_16(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_16()))));
						vmdd.setValue_17(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_17()))));
						vmdd.setValue_18(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_18()))));
						vmdd.setValue_19(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_19()))));
						
						vmdd.setValue_20(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_20()))));
						vmdd.setValue_21(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_21()))));
						vmdd.setValue_22(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_22()))));
						vmdd.setValue_23(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_23()))));
						vmdd.setValue_24(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_24()))));
						vmdd.setValue_25(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_25()))));
						vmdd.setValue_26(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_26()))));
						vmdd.setValue_27(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_27()))));
						vmdd.setValue_28(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_28()))));
						vmdd.setValue_29(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_29()))));
						
						vmdd.setValue_30(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_30()))));
						vmdd.setValue_31(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_31()))));
						vmdd.setValue_32(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_32()))));
						vmdd.setValue_33(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_33()))));
						vmdd.setValue_34(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_34()))));
						vmdd.setValue_35(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_35()))));
						vmdd.setValue_36(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_36()))));
						vmdd.setValue_37(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_37()))));
						vmdd.setValue_38(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_38()))));
						vmdd.setValue_39(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_39()))));
						
						vmdd.setValue_40(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_40()))));
						vmdd.setValue_41(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_41()))));
						vmdd.setValue_42(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_42()))));
						vmdd.setValue_43(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_43()))));
						vmdd.setValue_44(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_44()))));
						vmdd.setValue_45(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_45()))));
						vmdd.setValue_46(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_46()))));
						vmdd.setValue_47(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_47()))));
						vmdd.setValue_48(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_48()))));
						vmdd.setValue_49(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_49()))));
						
						vmdd.setValue_50(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_50()))));
						vmdd.setValue_51(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_51()))));
						vmdd.setValue_52(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_52()))));
						vmdd.setValue_53(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_53()))));
						vmdd.setValue_54(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_54()))));
						vmdd.setValue_55(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_55()))));
						vmdd.setValue_56(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_56()))));
						vmdd.setValue_57(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_57()))));
						vmdd.setValue_58(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_58()))));
						vmdd.setValue_59(df.format(Double.parseDouble(StringUtil.nullToZero(((LpGM)gmList.get(i)).getValue_59()))));
						
						lpList.add(vmdd);
					}
				}
				
			} else if(CommonConstants.MeterType.WaterMeter.getLpClassName().equals(lpClassName)){
				List<LpWM> wmList = lpWMDao.getLpWMsByListCondition(set);
				
				try {
					lpInterval = lpWMDao.getLpInterval(mdevId);
				} catch(Exception e) {}
				
				if(type.equals("3")) {
					double duration = Integer.parseInt(lastDate.substring(6)) - Integer.parseInt(preDate.substring(6)) + 1;
					
					VEEMaxDetailData vmdd = null;
					for(int i=0; i < 24; i++){
						vmdd = new VEEMaxDetailData();
						lpList.add(vmdd);
					}
					
					for(int i=0; i<wmList.size(); i++){
						vmdd = lpList.get(i % 24);
						
						vmdd.setValue_00(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_00())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_00()))));
						vmdd.setValue_01(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_01())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_01()))));
						vmdd.setValue_02(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_02())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_02()))));
						vmdd.setValue_03(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_03())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_03()))));
						vmdd.setValue_04(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_04())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_04()))));
						vmdd.setValue_05(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_05())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_05()))));
						vmdd.setValue_06(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_06())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_06()))));
						vmdd.setValue_07(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_07())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_07()))));
						vmdd.setValue_08(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_08())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_08()))));
						vmdd.setValue_09(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_09())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_09()))));
						
						vmdd.setValue_10(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_10())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_10()))));
						vmdd.setValue_11(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_11())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_11()))));
						vmdd.setValue_12(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_12())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_12()))));
						vmdd.setValue_13(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_13())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_13()))));
						vmdd.setValue_14(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_14())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_14()))));
						vmdd.setValue_15(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_15())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_15()))));
						vmdd.setValue_16(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_16())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_16()))));
						vmdd.setValue_17(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_17())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_17()))));
						vmdd.setValue_18(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_18())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_18()))));
						vmdd.setValue_19(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_19())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_19()))));
						
						vmdd.setValue_20(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_20())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_20()))));
						vmdd.setValue_21(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_21())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_21()))));
						vmdd.setValue_22(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_22())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_22()))));
						vmdd.setValue_23(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_23())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_23()))));
						vmdd.setValue_24(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_24())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_24()))));
						vmdd.setValue_25(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_25())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_25()))));
						vmdd.setValue_26(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_26())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_26()))));
						vmdd.setValue_27(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_27())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_27()))));
						vmdd.setValue_28(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_28())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_28()))));
						vmdd.setValue_29(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_29())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_29()))));
						
						vmdd.setValue_30(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_30())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_30()))));
						vmdd.setValue_31(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_31())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_31()))));
						vmdd.setValue_32(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_32())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_32()))));
						vmdd.setValue_33(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_33())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_33()))));
						vmdd.setValue_34(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_34())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_34()))));
						vmdd.setValue_35(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_35())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_35()))));
						vmdd.setValue_36(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_36())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_36()))));
						vmdd.setValue_37(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_37())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_37()))));
						vmdd.setValue_38(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_38())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_38()))));
						vmdd.setValue_39(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_39())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_39()))));
						
						vmdd.setValue_40(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_40())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_40()))));
						vmdd.setValue_41(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_41())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_41()))));
						vmdd.setValue_42(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_42())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_42()))));
						vmdd.setValue_43(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_43())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_43()))));
						vmdd.setValue_44(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_44())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_44()))));
						vmdd.setValue_45(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_45())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_45()))));
						vmdd.setValue_46(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_46())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_46()))));
						vmdd.setValue_47(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_47())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_47()))));
						vmdd.setValue_48(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_48())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_48()))));
						vmdd.setValue_49(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_49())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_49()))));
						
						vmdd.setValue_50(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_50())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_50()))));
						vmdd.setValue_51(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_51())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_51()))));
						vmdd.setValue_52(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_52())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_52()))));
						vmdd.setValue_53(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_53())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_53()))));
						vmdd.setValue_54(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_54())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_54()))));
						vmdd.setValue_55(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_55())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_55()))));
						vmdd.setValue_56(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_56())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_56()))));
						vmdd.setValue_57(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_57())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_57()))));
						vmdd.setValue_58(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_58())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_58()))));
						vmdd.setValue_59(String.valueOf(Double.parseDouble(StringUtil.nullToZero(vmdd.getValue_59())) + Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_59()))));
					}
					
					for(int i=0; i < lpList.size() ; i++) {
						VEEMaxDetailData lpData = lpList.get(i);

						lpData.setValue_00(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_00())) / duration));
						lpData.setValue_01(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_01())) / duration));
						lpData.setValue_02(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_02())) / duration));
						lpData.setValue_03(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_03())) / duration));
						lpData.setValue_04(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_04())) / duration));
						lpData.setValue_05(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_05())) / duration));
						lpData.setValue_06(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_06())) / duration));
						lpData.setValue_07(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_07())) / duration));
						lpData.setValue_08(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_08())) / duration));
						lpData.setValue_09(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_09())) / duration));
						
						lpData.setValue_10(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_10())) / duration));
						lpData.setValue_11(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_11())) / duration));
						lpData.setValue_12(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_12())) / duration));
						lpData.setValue_13(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_13())) / duration));
						lpData.setValue_14(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_14())) / duration));
						lpData.setValue_15(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_15())) / duration));
						lpData.setValue_16(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_16())) / duration));
						lpData.setValue_17(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_17())) / duration));
						lpData.setValue_18(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_18())) / duration));
						lpData.setValue_19(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_19())) / duration));
						
						lpData.setValue_20(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_20())) / duration));
						lpData.setValue_21(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_21())) / duration));
						lpData.setValue_22(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_22())) / duration));
						lpData.setValue_23(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_23())) / duration));
						lpData.setValue_24(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_24())) / duration));
						lpData.setValue_25(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_25())) / duration));
						lpData.setValue_26(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_26())) / duration));
						lpData.setValue_27(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_27())) / duration));
						lpData.setValue_28(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_28())) / duration));
						lpData.setValue_29(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_29())) / duration));
						
						lpData.setValue_30(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_30())) / duration));
						lpData.setValue_31(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_31())) / duration));
						lpData.setValue_32(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_32())) / duration));
						lpData.setValue_33(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_33())) / duration));
						lpData.setValue_34(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_34())) / duration));
						lpData.setValue_35(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_35())) / duration));
						lpData.setValue_36(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_36())) / duration));
						lpData.setValue_37(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_37())) / duration));
						lpData.setValue_38(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_38())) / duration));
						lpData.setValue_39(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_39())) / duration));
						
						lpData.setValue_40(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_40())) / duration));
						lpData.setValue_41(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_41())) / duration));
						lpData.setValue_42(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_42())) / duration));
						lpData.setValue_43(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_43())) / duration));
						lpData.setValue_44(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_44())) / duration));
						lpData.setValue_45(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_45())) / duration));
						lpData.setValue_46(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_46())) / duration));
						lpData.setValue_47(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_47())) / duration));
						lpData.setValue_48(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_48())) / duration));
						lpData.setValue_49(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_49())) / duration));
						
						lpData.setValue_50(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_50())) / duration));
						lpData.setValue_51(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_51())) / duration));
						lpData.setValue_52(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_52())) / duration));
						lpData.setValue_53(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_53())) / duration));
						lpData.setValue_54(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_54())) / duration));
						lpData.setValue_55(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_55())) / duration));
						lpData.setValue_56(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_56())) / duration));
						lpData.setValue_57(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_57())) / duration));
						lpData.setValue_58(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_58())) / duration));
						lpData.setValue_59(df.format(Double.parseDouble(StringUtil.nullToZero(lpData.getValue_59())) / duration));
					}
				} else {
					VEEMaxDetailData vmdd = null;
					
					for(int i=0; i<wmList.size(); i++){
						vmdd = new VEEMaxDetailData();
						
						vmdd.setValue_00(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_00()))));
						vmdd.setValue_01(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_01()))));
						vmdd.setValue_02(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_02()))));
						vmdd.setValue_03(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_03()))));
						vmdd.setValue_04(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_04()))));
						vmdd.setValue_05(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_05()))));
						vmdd.setValue_06(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_06()))));
						vmdd.setValue_07(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_07()))));
						vmdd.setValue_08(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_08()))));
						vmdd.setValue_09(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_09()))));
						
						vmdd.setValue_10(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_10()))));
						vmdd.setValue_11(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_11()))));
						vmdd.setValue_12(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_12()))));
						vmdd.setValue_13(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_13()))));
						vmdd.setValue_14(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_14()))));
						vmdd.setValue_15(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_15()))));
						vmdd.setValue_16(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_16()))));
						vmdd.setValue_17(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_17()))));
						vmdd.setValue_18(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_18()))));
						vmdd.setValue_19(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_19()))));
						
						vmdd.setValue_20(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_20()))));
						vmdd.setValue_21(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_21()))));
						vmdd.setValue_22(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_22()))));
						vmdd.setValue_23(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_23()))));
						vmdd.setValue_24(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_24()))));
						vmdd.setValue_25(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_25()))));
						vmdd.setValue_26(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_26()))));
						vmdd.setValue_27(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_27()))));
						vmdd.setValue_28(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_28()))));
						vmdd.setValue_29(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_29()))));
						
						vmdd.setValue_30(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_30()))));
						vmdd.setValue_31(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_31()))));
						vmdd.setValue_32(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_32()))));
						vmdd.setValue_33(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_33()))));
						vmdd.setValue_34(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_34()))));
						vmdd.setValue_35(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_35()))));
						vmdd.setValue_36(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_36()))));
						vmdd.setValue_37(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_37()))));
						vmdd.setValue_38(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_38()))));
						vmdd.setValue_39(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_39()))));
						
						vmdd.setValue_40(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_40()))));
						vmdd.setValue_41(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_41()))));
						vmdd.setValue_42(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_42()))));
						vmdd.setValue_43(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_43()))));
						vmdd.setValue_44(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_44()))));
						vmdd.setValue_45(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_45()))));
						vmdd.setValue_46(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_46()))));
						vmdd.setValue_47(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_47()))));
						vmdd.setValue_48(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_48()))));
						vmdd.setValue_49(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_49()))));
						
						vmdd.setValue_50(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_50()))));
						vmdd.setValue_51(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_51()))));
						vmdd.setValue_52(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_52()))));
						vmdd.setValue_53(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_53()))));
						vmdd.setValue_54(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_54()))));
						vmdd.setValue_55(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_55()))));
						vmdd.setValue_56(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_56()))));
						vmdd.setValue_57(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_57()))));
						vmdd.setValue_58(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_58()))));
						vmdd.setValue_59(df.format(Double.parseDouble(StringUtil.nullToZero(((LpWM)wmList.get(i)).getValue_59()))));
						
						lpList.add(vmdd);
					}
				}				
			}
		}
		
		// 이곳까지 오면 Interval에 맞추어  모든 데이터가 존재하는 것임
		
		
		
		return lpList;
	}

}
