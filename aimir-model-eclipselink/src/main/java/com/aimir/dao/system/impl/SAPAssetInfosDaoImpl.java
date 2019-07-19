/**
 * ContractDao.java Copyright NuriTelecom Limited 2012
 */
package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.SAPAssetInfosDao;
import com.aimir.model.system.SAPAssetInfos;
import com.aimir.util.Condition;

/**
 * SAPAssetInfosDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2012. 6. 5.   v1.0       enj       SAP에서 전송받은 정보를 관리하는 DAO의 구현 클래스  
 *
 */
@Repository(value="sapAssetInfosDao")
public class SAPAssetInfosDaoImpl extends AbstractJpaDao<SAPAssetInfos, Integer> implements SAPAssetInfosDao{
    Log logger = LogFactory.getLog(SAPAssetInfosDaoImpl.class);
    
    public SAPAssetInfosDaoImpl() {
        super(SAPAssetInfos.class);
    }

    @Override
    public Class<SAPAssetInfos> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }
}
