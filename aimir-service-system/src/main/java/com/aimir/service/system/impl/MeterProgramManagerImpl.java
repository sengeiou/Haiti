/**
 * MeterProgramManagerImpl.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.service.system.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.MeterProgramKind;
import com.aimir.dao.system.MeterConfigDao;
import com.aimir.dao.system.MeterProgramDao;
import com.aimir.dao.system.MeterProgramLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.MeterConfig;
import com.aimir.model.system.MeterProgram;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.MeterProgramManager;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * MeterProgramManagerImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2012. 1. 30.  v1.0        문동규   TOU Profile Service Impl
 * 2012. 2. 24.  v1.0        문동규   Meter Program 으로 이름 변경
 *
 */
@WebService(endpointInterface = "com.aimir.service.system.MeterProgramManager")
@Service(value = "meterProgramManager")
public class MeterProgramManagerImpl implements MeterProgramManager {

    private static Log log = LogFactory.getLog(MeterProgramManagerImpl.class);

    @Autowired
    MeterProgramLogDao meterProgramLogDao;

    @Autowired
    MeterProgramDao meterProgramDao;
    
    @Autowired
    MeterConfigDao meterConfigDao;

    @Autowired
    SupplierDao supplierDao;

    /**
     * method name : getMeterProgramLogListTotalCount<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program Log 리스트의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Deprecated
    public Integer getMeterProgramLogListTotalCount(Map<String, Object> conditionMap) {
        Integer count = null;
        List<Map<String, Object>> result = meterProgramLogDao.getMeterProgramLogList(conditionMap, true);
        
        count = (Integer)result.get(0).get("total");
        
        return count;
    }

    /**
     * method name : getMeterProgramLogList<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program Log 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Deprecated
    public List<Map<String, Object>> getMeterProgramLogList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = meterProgramLogDao.getMeterProgramLogList(conditionMap, false);

        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        try {
            for (Map<String, Object> map : result) {
                map.put("lastModifiedDate", TimeLocaleUtil.getLocaleDate(DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)map.get("LAST_MODIFIED_DATE")), 14, lang, country));
                map.put("meterProgramId", map.get("METERPROGRAM_ID"));
                map.put("meterProgramKind", map.get("METERPROGRAM_KIND"));
                map.put("successCount", map.get("SUCCESS_COUNT"));
                map.put("failureCount", map.get("FAILURE_COUNT"));
                map.put("tryCount", map.get("TRY_COUNT"));
            }
        } catch(ParseException pe) {
            pe.printStackTrace();
        }
        return result;
    }

    /**
     * method name : getMeterProgramLogListTotalCountRenew<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program Log 리스트의 total count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getMeterProgramLogListTotalCountRenew(Map<String, Object> conditionMap) {
        Integer count = null;
        List<Map<String, Object>> result = meterProgramLogDao.getMeterProgramLogListRenew(conditionMap, true);

        count = (Integer)result.get(0).get("total");
        return count;
    }

    /**
     * method name : getMeterProgramLogListRenew<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program Log 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeterProgramLogListRenew(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = meterProgramLogDao.getMeterProgramLogListRenew(conditionMap, false);

        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

        for (Map<String, Object> map : result) {
            map.put("lastModifiedDate", TimeLocaleUtil.getLocaleDate((String)map.get("lastModifiedDate"), lang, country));
        	map.put("successCount", dfMd.format(map.get("successCount")));
        	map.put("failureCount", dfMd.format(map.get("failureCount")));
        	map.put("tryCount", dfMd.format(map.get("tryCount")));
        }

        return result;
    }

    /**
     * method name : getMeterProgramSettingsData<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Settings 값을 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public String getMeterProgramSettingsData(Map<String, Object> conditionMap) {
        String result = meterProgramDao.getMeterProgramSettingsData(conditionMap);
        return result;
    }

    /**
     * method name : saveMeterProgram<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program 정보를 저장한다.
     *
     * @param conditionMap
     * @throws Exception 
     */
    @Transactional
    public void saveMeterProgram(Map<String, Object> conditionMap) throws Exception {
    	
		if (!(conditionMap.containsKey("configId")
				&& conditionMap.containsKey("settings") && 
				conditionMap.containsKey("kind"))) {
			throw new Exception(String.format(
					"condition is not found - condition list[%s]", conditionMap
					.keySet()));
		}
    	
        Integer configId = (Integer)conditionMap.get("configId");
        String settings = (String)conditionMap.get("settings");
        String kind = (String)conditionMap.get("kind");
        
        MeterConfig mc = meterConfigDao.get(configId);
        
        if(mc==null){
        	log.error("can not found 'MeterConfig'");
        	throw new Exception("can not found 'MeterConfig'");
        }

        MeterProgram meterProgram = new MeterProgram();
        meterProgram.setLastModifiedDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        meterProgram.setMeterConfig(mc);
        meterProgram.setSettings(settings);

        MeterProgramKind meterProgramKind = null;
        if (!StringUtil.nullToBlank(kind).isEmpty()) {
            meterProgramKind = MeterProgramKind.valueOf(kind);
        }
        meterProgram.setKind(meterProgramKind);

        meterProgramDao.add(meterProgram);
    }
}