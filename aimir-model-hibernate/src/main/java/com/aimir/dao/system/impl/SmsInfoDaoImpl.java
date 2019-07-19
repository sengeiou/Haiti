package com.aimir.dao.system.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.SmsInfoDao;
import com.aimir.model.system.SmsInfo;

@Repository(value="smsInfoDao")
public class SmsInfoDaoImpl extends AbstractHibernateGenericDao<SmsInfo, Integer> implements SmsInfoDao{

    Log logger = LogFactory.getLog(SmsInfoDaoImpl.class);

    @Autowired
    protected SmsInfoDaoImpl(SessionFactory sessionFactory) {
        super(SmsInfo.class);
        super.setSessionFactory(sessionFactory);
    }
    
    @Override
    public SmsInfo getSmsInfo(Integer contractId) {
    	SmsInfo smsInfo = null;
    	try{
	        StringBuffer sb = new StringBuffer();
	        sb.append("\nFROM SmsInfo ");
	        sb.append("\nWHERE contractId = :contractId ");
	        Query query = getSession().createQuery(sb.toString());
	        query.setInteger("contractId", contractId);
	       
	        List<SmsInfo> list = query.list();
	        
	        if(list != null && list.size() > 0) {
	        	smsInfo = list.get(0);
	        }
    	} catch(Exception e) {
    		logger.error(e,e);
    	}
        return smsInfo;
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public void updateSmsNumber(int contractId, String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE SmsInfo \n");
        sb.append("set smsNumber =:msg \n");
        sb.append("WHERE contractId = :id");
        Query query = getSession().createQuery(sb.toString());
        query.setString("msg", msg);
        query.setInteger("id", contractId);
        query.executeUpdate();
    }
    
    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public void updateSMSPriod(int contractId, String msg, String lastNotificationDate) {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE SmsInfo \n");
        sb.append("set smsNumber =:msg, \n");
        sb.append("    lastNotificationDate =:lastNotificationDate \n");
        sb.append("WHERE contractId = :id");
        Query query = getSession().createQuery(sb.toString());
        query.setString("msg", msg);
        query.setString("lastNotificationDate", lastNotificationDate);
        query.setInteger("id", contractId);
        query.executeUpdate();
    }
}