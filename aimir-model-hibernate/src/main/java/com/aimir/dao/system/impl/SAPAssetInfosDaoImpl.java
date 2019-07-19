/**
 * ContractDao.java Copyright NuriTelecom Limited 2012
 */
package com.aimir.dao.system.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.SAPAssetInfosDao;
import com.aimir.model.system.SAPAssetInfos;

/**
 * SAPAssetInfosDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2012. 6. 5.   v1.0       enj       SAP에서 전송받은 정보를 관리하는 DAO의 구현 클래스  
 *
 */
@Repository(value="sapAssetInfosDao")
public class SAPAssetInfosDaoImpl extends AbstractHibernateGenericDao<SAPAssetInfos, Integer> implements SAPAssetInfosDao{
    Log logger = LogFactory.getLog(SAPAssetInfosDaoImpl.class);
    
    @Autowired
    protected SAPAssetInfosDaoImpl(SessionFactory sessionFactory) {
        super(SAPAssetInfos.class);
        super.setSessionFactory(sessionFactory);
    }
}
