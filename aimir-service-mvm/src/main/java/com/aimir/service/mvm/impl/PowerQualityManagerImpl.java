package com.aimir.service.mvm.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.TypeView;
import com.aimir.dao.mvm.PowerQualityDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.system.DecimalPattern;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.PowerQualityManager;
import com.aimir.util.CommonUtils2;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@Service(value="powerQualityManager")
public class PowerQualityManagerImpl implements PowerQualityManager{

    Log logger = LogFactory.getLog(PowerQualityManagerImpl.class);
    	
	@Autowired
	PowerQualityDao powerQualityDao;
	
	@Autowired
	SupplierDao supplierDao;

	/**
	 * PowerType 콤보 조회
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getTypeViewCombo(){

		TypeView[] types = TypeView.values();
		
		List<Object> resultList = new ArrayList<Object>();
		Map<String,Object> resultMap = null;
		
		for(TypeView type:types){
			resultMap = new HashMap();
			resultMap.put("id", type.getType());
			resultMap.put("name", type.name());
			resultList.add(resultMap);
		}

		return resultList;
	}

//	@SuppressWarnings("unchecked")
//	public Map<String, Object> getPowerQuality(Map<String, Object> condition) {
//		Integer nNormalCnt = 0;
//		Integer nVoltageUnbalanceCnt = 0;
//		Integer nReverseAngleUnbalanceCnt = 0;
//
//		condition.put("angle", 0);  // angle은 0로 고정됨
//
//		List<Map<String, Object>> _result = powerQualityDao.getCountForPQMini(condition);
//		nVoltageUnbalanceCnt      = _result !=null && _result.size()>0 ? Integer.parseInt( _result.get(0).get("CNT1").toString()) : 0;
//		nNormalCnt                = _result !=null && _result.size()>0 ? Integer.parseInt( _result.get(0).get("CNT2").toString()) : 0;
//		nReverseAngleUnbalanceCnt = _result !=null && _result.size()>0 ? Integer.parseInt( _result.get(0).get("CNT3").toString()) : 0;
//		nNormalCnt = nNormalCnt-(nVoltageUnbalanceCnt+nReverseAngleUnbalanceCnt);
//		
//		Map<String, Object> result = new HashMap<String, Object>();
//  		result.put("normal", nNormalCnt);
//		result.put("abnormal1", nVoltageUnbalanceCnt);
//		result.put("abnormal2", nReverseAngleUnbalanceCnt);
//		
//		return result;
//	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getPowerQuality(Map<String, Object> condition) {
		Integer nNormalCnt = 0;
		Integer nVoltageUnbalanceCnt = 0;
		Integer nReverseAngleUnbalanceCnt = 0;
		
		nVoltageUnbalanceCnt = powerQualityDao.getCount(condition);
		
		condition.put("deviation", 0);
		nNormalCnt = powerQualityDao.getCount(condition);
		
		condition.put("angle", 0);
		nReverseAngleUnbalanceCnt = powerQualityDao.getCount(condition);
		
		nNormalCnt = nNormalCnt-(nVoltageUnbalanceCnt+nReverseAngleUnbalanceCnt);
		
		Map<String, Object> result = new HashMap<String, Object>();
  		result.put("normal", nNormalCnt);
		result.put("abnormal1", nVoltageUnbalanceCnt);
		result.put("abnormal2", nReverseAngleUnbalanceCnt);
		
		return result;
	}

    @SuppressWarnings("unchecked")
	public Map<String, Object> getPowerQualityList(Map<String, Object> condition) {
    	
    	if(String.valueOf(condition.get("fromDate")).length() == 0 && String.valueOf(condition.get("toDate")).length() == 0) {
    		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    		condition.put("fromDate", df.format(new Date()));
    		condition.put("toDate", df.format(new Date()));
    		condition.put("dateType", "2");
    	}

		String strSupplierId = (String)condition.get("supplierId");
		Integer supplierId = 0;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
			
		}
		condition.put("supplierId", supplierId);
		
		String strDeviation = (String)condition.get("deviation");
		Integer deviation = 0;
		if(!"".equals(StringUtil.nullToBlank(strDeviation))){
			deviation = Integer.parseInt(strDeviation);
		}
		condition.put("deviation", deviation);
		
		int page=0;
		if(condition.containsKey("page")){
			Object oPage = condition.get("page");
			if(oPage instanceof Integer){
				page = (Integer)oPage;
			}else if(oPage instanceof String){
				page = Integer.parseInt((String)oPage);
			}
			condition.put("page", page);
		}
		int rowPerPage = 0;
		if(!condition.containsKey("pageSize")){
			rowPerPage = 1000000;
		}else{
			rowPerPage=(Integer)condition.get("pageSize");
		}
		condition.put("rowPerPage", rowPerPage);

		List<Object> voltageLevels = powerQualityDao.getVoltageLevels(condition);

		
		// 1page 최초 조회시에만 total 건수 구하기. 
		Integer total = null;
		
		total = powerQualityDao.getVoltageLevelsCount(condition); 


		Supplier supplier = supplierDao.get(supplierId);
		DecimalPattern  pattern = supplier.getMd();
		DecimalFormat decimalFormat = pattern==null ? new DecimalFormat("0.0") : DecimalUtil.getDecimalFormat(pattern);
		
		List<Map<String,Object>> gridlist = new ArrayList<Map<String,Object>>();
		for(Object obj:voltageLevels){
			Map<String, Object> row = (Map<String, Object>)obj;
	
			row.put("volA_avg", decimalFormat.format(nullToZero(row.get("volA_avg"))));
			row.put("volA_max", decimalFormat.format(nullToZero(row.get("volA_max"))));
			row.put("volA_min", decimalFormat.format(nullToZero(row.get("volA_min"))));
			
			row.put("volB_avg", decimalFormat.format(nullToZero(row.get("volB_avg"))));
			row.put("volB_max", decimalFormat.format(nullToZero(row.get("volB_max"))));
			row.put("volB_min", decimalFormat.format(nullToZero(row.get("volB_min"))));
			
			row.put("volC_avg", decimalFormat.format(nullToZero(row.get("volC_avg"))));
			row.put("volC_max", decimalFormat.format(nullToZero(row.get("volC_max"))));
			row.put("volC_min", decimalFormat.format(nullToZero(row.get("volC_min"))));
			
			row.put("vol_angleA_avg", decimalFormat.format(nullToZero(row.get("vol_angleA_avg"))));
			row.put("vol_angleA_max", decimalFormat.format(nullToZero(row.get("vol_angleA_max"))));
			row.put("vol_angleA_min", decimalFormat.format(nullToZero(row.get("vol_angleA_min"))));			
			
			row.put("vol_angleB_avg", decimalFormat.format(nullToZero(row.get("vol_angleB_avg"))));
			row.put("vol_angleB_max", decimalFormat.format(nullToZero(row.get("vol_angleB_max"))));
			row.put("vol_angleB_min", decimalFormat.format(nullToZero(row.get("vol_angleB_min"))));
			
			row.put("vol_angleC_avg", decimalFormat.format(nullToZero(row.get("vol_angleC_avg"))));
			row.put("vol_angleC_max", decimalFormat.format(nullToZero(row.get("vol_angleC_max"))));
			row.put("vol_angleC_min", decimalFormat.format(nullToZero(row.get("vol_angleC_min"))));
			
			// 날짜 formatting
			if(row.get("yyyymmdd")!=null)
				row.put("yyyymmdd", TimeLocaleUtil.getLocaleDate((String)row.get("yyyymmdd") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			gridlist.add(row);
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		if(total != null){
			result.put("total", total.toString());
		}
		result.put("grid", gridlist);

		return result;    
    }

    // INSERT START SP-204
    @SuppressWarnings("unchecked")
	public Map<String, Object> getPowerQualityListForSoria(Map<String, Object> condition) {
    	
    	if(String.valueOf(condition.get("fromDate")).length() == 0 && String.valueOf(condition.get("toDate")).length() == 0) {
    		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    		condition.put("fromDate", df.format(new Date()));
    		condition.put("toDate", df.format(new Date()));
    		condition.put("dateType", "2");
    	}

		String strSupplierId = (String)condition.get("supplierId");
		Integer supplierId = 0;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
			
		}
		condition.put("supplierId", supplierId);
		
		String strDeviation = (String)condition.get("deviation");
		Integer deviation = 0;
		if(!"".equals(StringUtil.nullToBlank(strDeviation))){
			deviation = Integer.parseInt(strDeviation);
		}
		condition.put("deviation", deviation);
		
		int page=0;
		if(condition.containsKey("page")){
			Object oPage = condition.get("page");
			if(oPage instanceof Integer){
				page = (Integer)oPage;
			}else if(oPage instanceof String){
				page = Integer.parseInt((String)oPage);
			}
			condition.put("page", page);
		}
		int rowPerPage = 0;
		if(!condition.containsKey("pageSize")){
			rowPerPage = 1000000;
		}else{
			rowPerPage=(Integer)condition.get("pageSize");
		}
		condition.put("rowPerPage", rowPerPage);

		List<Object> voltageLevels = powerQualityDao.getVoltageLevelsForSoria(condition);

		
		// 1page 최초 조회시에만 total 건수 구하기. 
		Integer total = null;
		
		total = powerQualityDao.getVoltageLevelsCount(condition); 


		Supplier supplier = supplierDao.get(supplierId);
		DecimalPattern  pattern = supplier.getMd();
		DecimalFormat decimalFormat = pattern==null ? new DecimalFormat("0.0") : DecimalUtil.getDecimalFormat(pattern);
		
		List<Map<String,Object>> gridlist = new ArrayList<Map<String,Object>>();
		for(Object obj:voltageLevels){
			Map<String, Object> row = (Map<String, Object>)obj;
	
			row.put("volL1_avg", decimalFormat.format(nullToZero(row.get("volL1_avg"))));
			row.put("volL1_max", decimalFormat.format(nullToZero(row.get("volL1_max"))));
			row.put("volL1_min", decimalFormat.format(nullToZero(row.get("volL1_min"))));
			
			row.put("volL2_avg", decimalFormat.format(nullToZero(row.get("volL2_avg"))));
			row.put("volL2_max", decimalFormat.format(nullToZero(row.get("volL2_max"))));
			row.put("volL2_min", decimalFormat.format(nullToZero(row.get("volL2_min"))));
			
			row.put("volL3_avg", decimalFormat.format(nullToZero(row.get("volL3_avg"))));
			row.put("volL3_max", decimalFormat.format(nullToZero(row.get("volL3_max"))));
			row.put("volL3_min", decimalFormat.format(nullToZero(row.get("volL3_min"))));
						
			// 날짜 formatting
			if(row.get("yyyymmdd")!=null)
				row.put("yyyymmdd", TimeLocaleUtil.getLocaleDate((String)row.get("yyyymmdd") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			gridlist.add(row);
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		if(total != null){
			result.put("total", total.toString());
		}
		result.put("grid", gridlist);

		return result;    
    }    
    // INSERT END SP-204
    
    @SuppressWarnings("unchecked")
	public Map<String, Object> getDailyPowerQualityData(Map<String, Object> condition) {
    	Integer supplierId = (Integer) ObjectUtils.defaultIfNull(condition.get("supplierId"), null);
    	Supplier supplier = supplierDao.get(supplierId);
		DecimalPattern  pattern = supplier.getMd();
		DecimalFormat decimalFormat = pattern==null ? new DecimalFormat("0.0") : DecimalUtil.getDecimalFormat(pattern);
		
    	Map<String, Object> gridData = powerQualityDao.getPowerQualityData(condition);
    	List<Map<String, Object>> data = (List<Map<String, Object>>) gridData.get("data");
    	String[] phaseArray = {"a", "b", "c"};
    	String[] prefixArray = {"vol_", "curr_", "vol_thd_", "pf_", "vol_angle_"};
    	
    	for ( Map<String, Object> rec : data ) {
    		String hhmm = StringUtil.nullToBlank(rec.get("hhmm"));
    		String hh = hhmm.substring(0, 2);
    		String mm = hhmm.substring(2);
    		hhmm = hh + ":" + mm;
    		rec.put("hhmm", hhmm);
    		
    		Double lineAB = StringUtil.nullToDoubleZero((Double) rec.get("line_ab"));
    		Double lincAC = StringUtil.nullToDoubleZero((Double) rec.get("line_ac"));
    		
    		rec.put("line_ab", decimalFormat.format(lineAB));
    		rec.put("line_ac", decimalFormat.format(lincAC));
    		
    		for ( String phase : phaseArray ) {
    			for ( String prefix : prefixArray ) {
    				String key = prefix + phase;
    				String value = decimalFormat.format((Double) ObjectUtils.defaultIfNull(rec.get(key), 0d));
    				rec.put(key, value);    				
    			}
    		}
    	}
    	
    	gridData.put("data", data);
    	return gridData;
    }
    
    /**
     * object의 값이 널인지 확인하고 널일경우 해당 Instance를 생성한다.
     * @param object
     * @return
     */
    private Double nullToZero(Object object){
		if (object == null)
			return 0d;

		return Double.parseDouble( object.toString() );
    }

	@SuppressWarnings("unchecked")
	public Map<String, Object> getPowerInstrumentList(Map<String, Object> condition) {
		if(String.valueOf(condition.get("fromDate")).length() == 0 && String.valueOf(condition.get("toDate")).length() == 0) {
    		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    		condition.put("fromDate", df.format(new Date()));
    		condition.put("toDate", df.format(new Date()));
    		condition.put("dateType", "2");
    	}
		
		int page = (Integer)condition.get("page");

    	String selectType  = (String)condition.get("selectType");
    	Integer nSelectType = 1;
    	if(!"".equals(StringUtil.nullToBlank(selectType))){
    		nSelectType = Integer.parseInt(selectType);
		}
    	condition.put("selectType", nSelectType);
    	
		String strSupplierId = (String)condition.get("supplierId");
		Integer supplierId = 0;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
			
		}
		condition.put("supplierId", supplierId);
		
		String strDeviation = (String)condition.get("deviation");
		Integer deviation = 0;
		if(!"".equals(StringUtil.nullToBlank(strDeviation))){
			deviation = Integer.parseInt(strDeviation);
		}
		condition.put("deviation", deviation);
		
		//날짜 형식 불러오기
		Supplier supplier = supplierDao.get(supplierId);
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
		condition.put("decimalFormat", df);
		condition.put("from3letter",supplier.getLang().getCode_3letter());
		condition.put("to3letter",supplier.getCountry().getCode_3letter());
		
		int rowPerPage = 0;
		if(!condition.containsKey("pageSize")){
			rowPerPage = 1000000;
		}else{
			rowPerPage=(Integer)condition.get("pageSize");
		}
		condition.put("pageSize", rowPerPage);
		
		List<Object> powerInstrumentList = powerQualityDao.getPowerInstrumentList(condition);
		
		
		// 1page 최초 조회시에만 total 건수 구하기. 
		Object total = null;
		
		total = powerQualityDao.getPowerInstrumentListCount(condition);//

		
		Supplier sp = supplierDao.get(supplierId);
		DecimalPattern  pattern = sp.getMd();
		DecimalFormat decimalFormat = pattern==null ? new DecimalFormat("0.0") : DecimalUtil.getDecimalFormat(pattern);
		DecimalFormat dfMd = DecimalUtil.getMDStyle(pattern);

		List<Map<String,Object>> gridlist = new ArrayList<Map<String,Object>>();
		int count=1;
		for(Object obj:powerInstrumentList){
			Map<String, Object> row = (Map<String, Object>)obj;
			row.put("no", dfMd.format(CommonUtils2.makeIdxPerPage(String.valueOf(page), String.valueOf(rowPerPage), count)));
			row.put("vol_a", decimalFormat.format(nullToZero(row.get("vol_a"))));
			row.put("vol_b", decimalFormat.format(nullToZero(row.get("vol_b"))));
			row.put("vol_c", decimalFormat.format(nullToZero(row.get("vol_c"))));
			
			row.put("curr_a", decimalFormat.format(nullToZero(row.get("curr_a"))));
			row.put("curr_b", decimalFormat.format(nullToZero(row.get("curr_b"))));
			row.put("curr_c", decimalFormat.format(nullToZero(row.get("curr_c"))));
			
			row.put("curr_angle_a", decimalFormat.format(nullToZero(row.get("curr_angle_a"))));
			row.put("curr_angle_b", decimalFormat.format(nullToZero(row.get("curr_angle_b"))));
			row.put("curr_angle_c", decimalFormat.format(nullToZero(row.get("curr_angle_c"))));
			
			row.put("vol_angle_a", decimalFormat.format(nullToZero(row.get("vol_angle_a"))));
			row.put("vol_angle_b", decimalFormat.format(nullToZero(row.get("vol_angle_b"))));
			row.put("vol_angle_c", decimalFormat.format(nullToZero(row.get("vol_angle_c"))));
			
			row.put("voltA", decimalFormat.format(nullToZero(row.get("voltA"))));
			row.put("voltB", decimalFormat.format(nullToZero(row.get("voltB"))));
			row.put("voltC", decimalFormat.format(nullToZero(row.get("voltC"))));
			
			row.put("currA", decimalFormat.format(nullToZero(row.get("currA"))));
			row.put("currB", decimalFormat.format(nullToZero(row.get("currB"))));
			row.put("currC", decimalFormat.format(nullToZero(row.get("currC"))));
			
			row.put("line_AB", decimalFormat.format(nullToZero(row.get("line_AB"))));
			row.put("line_CA", decimalFormat.format(nullToZero(row.get("line_CA"))));
			row.put("line_BC", decimalFormat.format(nullToZero(row.get("line_BC"))));

			// 날짜 formatting
			if(row.get("yyyymmdd")!=null)
				row.put("yyyymmdd", TimeLocaleUtil.getLocaleDate((String)row.get("yyyymmdd") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			
			gridlist.add(row);
			count++;
		}

		
		Map<String, Object> result = new HashMap<String, Object>();
		if(total != null){
			result.put("total", total.toString());
		}
		result.put("grid", gridlist);

		return result;  
    	
    }

	@SuppressWarnings("unchecked")
	public Map<String, Object> getPowerDetailList(Map<String, Object> condition){

		String strSupplierId = (String)condition.get("supplierId");
		Integer supplierId = 0;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
		}
		condition.put("supplierId", supplierId);

		String strDeviceType = (String)condition.get("deviceType");
		Integer deviceType = null;
		if(!("".equals(strDeviceType)) && strDeviceType != null) {
			deviceType = DeviceType.valueOf(strDeviceType).getCode();
		} else {
			deviceType = -1;
		}
		condition.put("deviceType", deviceType);
		
		String strDeviation = (String)condition.get("deviation");
		Integer deviation = 0;
		if(!"".equals(StringUtil.nullToBlank(strDeviation))){
			deviation = Integer.parseInt(strDeviation);
		}
		condition.put("deviation", deviation);
		
		String strTypeView = (String)condition.get("typeView");
		Integer typeView = 0;
		if(!"".equals(StringUtil.nullToBlank(strTypeView))){
			typeView = Integer.parseInt(strTypeView);
		}
		condition.put("typeView", typeView);
		
		//날짜 형식 불러오기
		Supplier supplier = supplierDao.get(supplierId);
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
		condition.put("decimalFormat", df);
		condition.put("from2letter",supplier.getLang().getCode_2letter());
		condition.put("to2letter",supplier.getCountry().getCode_2letter());
		
		List<Object> grid = powerQualityDao.getPowerDetailList(condition);
		
		// 1page 최초 조회시에만 total 건수 구하기.
		int page       = 0;
		if(condition.containsKey("page")){
			Object oPage = condition.get("page");
			if(oPage instanceof Integer){
				page = (Integer)oPage;
			}else if(oPage instanceof String){
				page = Integer.parseInt((String)oPage);
			}	
		}
		Object total = null;

		total = powerQualityDao.getPowerDetailListCount(condition);

		Map<String, Object> result = new HashMap<String, Object>();
		if(total != null){
			result.put("total", total.toString());
		}
		result.put("grid", grid);

		return result; 
	}
	
}
