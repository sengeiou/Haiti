package com.aimir.service.system.impl;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.dao.system.AuditLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.AuditLogManager;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@WebService(endpointInterface = "com.aimir.service.system.AuditLogManager")
@Service(value = "auditLogManager")
public class AuditLogManagerImpl implements AuditLogManager {

    protected static Log log = LogFactory.getLog(AuditLogManagerImpl.class);

    @Autowired
    AuditLogDao auditLogDao; 

    @Autowired
    SupplierDao supplierDao; 

    /**
     * method name : getAuditLogRankingList
     * method Desc : ChangeLog 미니가젯의 AuditLog Ranking 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getAuditLogRankingList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = auditLogDao.getAuditLogRankingList(conditionMap, false);

        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");
        Integer supplierId = (Integer)conditionMap.get("supplierId");

        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
        
        String entityName = null;
        int endIndex = 0;
        int ranking = (page-1) * limit + 1;

        for (Map<String, Object> map : result) {
            map.put("ranking", dfMd.format(ranking++));
            if (!StringUtil.nullToBlank(map.get("entityName")).isEmpty()) {
                entityName = (String)map.get("entityName");
                endIndex = entityName.lastIndexOf(".");

                map.put("entityName", entityName.substring(endIndex+1));
            }
        }
        return result;
    }

    /**
     * method name : getAuditLogRankingListTotalCount
     * method Desc : ChangeLog 미니가젯의 AuditLog Ranking 리스트의 Total Count를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getAuditLogRankingListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = auditLogDao.getAuditLogRankingList(conditionMap, true);
//        return ((Long)result.get(0).get("total")).intValue();
        return (Integer)result.get(0).get("total");
    }

    /**
     * method name : getAuditLogList
     * method Desc : ChangeLog 맥스가젯의 AuditLog 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getAuditLogList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = auditLogDao.getAuditLogList(conditionMap, false);

        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
        
        String entityName = null;
        int endIndex = 0;
        int num = (page-1) * limit + 1;
        long longTime = 0l;

        for (Map<String, Object> map : result) {
            map.put("num", dfMd.format(num++));

            longTime = ((Date)map.get("createdDate")).getTime();
            map.put("createdDate", TimeLocaleUtil.getLocaleDate(TimeUtil.getDateUsingFormat(longTime, "yyyyMMddHHmmss"), lang, country));

            if (!StringUtil.nullToBlank(map.get("entityName")).isEmpty()) {
                entityName = (String)map.get("entityName");
                endIndex = entityName.lastIndexOf(".");

                map.put("entityName", entityName.substring(endIndex+1));
            }
        }
        return result;
    }

    /**
     * method name : getAuditLogListTotalCount
     * method Desc : ChangeLog 맥스가젯의 AuditLog 리스트의 Total Count를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getAuditLogListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = auditLogDao.getAuditLogList(conditionMap, true);
//        return ((Long)result.get(0).get("total")).intValue();
        return (Integer)result.get(0).get("total");
    }

}