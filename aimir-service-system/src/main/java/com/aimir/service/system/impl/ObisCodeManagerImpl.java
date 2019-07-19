package com.aimir.service.system.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DLMSDataType;
import com.aimir.dao.system.ObisCodeDao;
import com.aimir.dao.system.RoleDao;
import com.aimir.model.system.OBISCode;
import com.aimir.service.system.ObisCodeManager;

@Service(value="ObisCodeManager")
public class ObisCodeManagerImpl implements ObisCodeManager {
	
	Log logger = LogFactory.getLog(ObisCodeManagerImpl.class);
	
    @Autowired
    ObisCodeDao obisCodeDao;
    
    @Autowired
    RoleDao roleDao;

    @Autowired
    HibernateTransactionManager transactionManager;
    
    public OBISCode getObisCode(Long id) {
        return obisCodeDao.get(id);
    }
    
    public void updateDescr(Map<String,Object> condition) throws Exception{
    	obisCodeDao.updateDescr(condition);
    }

    public List<Map<String,Object>> getObisCodeInfo(Map<String,Object> condition) {
        List<Map<String,Object>> obisCodeList = obisCodeDao.getObisCodeInfo(condition);
        int size = obisCodeList.size();
        
        for (int i = 0; i < size; i++) {
            Map<String,Object> map = obisCodeList.get(i);
            DLMSDataType dataType = (DLMSDataType)map.get("DATATYPE");
            map.put("DATATYPE", dataType != null ? dataType.getName() : dataType);
        }
        
        return obisCodeList;
    }

    //getObisCodeInfo와 동일. 조회조건만 다름.
    public List<Map<String,Object>> getObisCodeInfoByName(Map<String,Object> condition) {
        List<Map<String,Object>> obisCodeList = obisCodeDao.getObisCodeInfoByName(condition);
        int size = obisCodeList.size();

        for (int i = 0; i < size; i++) {
            Map<String,Object> map = obisCodeList.get(i);
            DLMSDataType dataType = (DLMSDataType)map.get("DATATYPE");
            map.put("DATATYPE", dataType != null ? dataType.getName() : dataType);
        }

        return obisCodeList;
    }
    
    public List<Map<String,Object>> getObisCodeWithEvent(Map<String,Object> condition) {
        return obisCodeDao.getObisCodeWithEvent(condition);
    }
    
    public List<Map<String,Object>> getObisCodeGroup(Map<String,Object> condition) {
        return obisCodeDao.getObisCodeGroup(condition);
    }
    
    /**
     * 
     * method name : getCheckDuplidate
     * method desc : ACTION의 경우 OBISCode, ClassId, AttributNo 가 중복되더라도 중복체크에 걸리지 않도록 한다.
     * ACTION의 경우 get/set과는 다른 기능.
     * 
     */
    public Integer getCheckDuplidate(Map<String,Object> condition) {
        List<Map<String,Object>> list = obisCodeDao.getObisCodeInfo(condition);
        String accessRight = (String)condition.get("accessRight");
        Integer cnt = 0;
        if(list != null) {
            for (int i = 0; i < list.size(); i++) {
                String dbAccessRight = (String) list.get(i).get("ACCESSRIGHT");
                if("ACTION".equals(accessRight)) {
                    if("ACTION".equals(dbAccessRight)) {
                        cnt++;
                    }
                } else {
                    if(!"ACTION".equals(dbAccessRight)) {
                        cnt++;
                    }
                }
            }
        }
        
        return cnt;
    }
    
    public void add(List<OBISCode> obisSaveList) {
        TransactionStatus txStatus = null;
        try {
            txStatus = transactionManager.getTransaction(null);
            int cnt= 0;
            for ( OBISCode obisCode : obisSaveList ) {
                    obisCodeDao.add(obisCode);
                    if ((++cnt)%20 == 0) {
                        obisCodeDao.flushAndClear();
                    }
            }
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            logger.error(e,e);
            transactionManager.rollback(txStatus);
        }
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void update(OBISCode updateObisCode) throws Exception {
        obisCodeDao.update(updateObisCode);
    }
    
    public void delete(long obisId) {
        obisCodeDao.deleteById(obisId);
    }
}
