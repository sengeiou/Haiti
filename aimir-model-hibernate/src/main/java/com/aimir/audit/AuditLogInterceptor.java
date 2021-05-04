package com.aimir.audit;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants.AuditAction;
import com.aimir.model.BaseObject;
import com.aimir.model.system.AuditLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.util.SessionContext;

import net.bytebuddy.matcher.IsNamedMatcher;

/**
 * Hibernate의 insert/update/delete 쿼리 실행 전에 이력을 남기기 위한 인터셉터 클래스이다.
 * com.aimir.audit.IAuditable 인터페이스를 구현한 클래스가 대상이되며 대상 클래스와 관계된 클래스는
 * 제외한다.
 *
 * @since 2011.09.20
 * @author elevas
 *
 */
@Service
public class AuditLogInterceptor extends EmptyInterceptor {
    private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	@Resource(name="sessionContextFactory")
	private ObjectFactory sessionContextFactory;

    private static Log log = LogFactory.getLog(AuditLogInterceptor.class);

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private HibernateTransactionManager transactionManager;

    public HibernateTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(HibernateTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    private String auditTargetName = "UNKNOWN";

    public AuditLogInterceptor() {
    	String temp = System.getProperty("aimir.auditTargetName");

        if(temp != null && !temp.equals("")){
        	auditTargetName = temp;
        	log.debug("AUDITLOG_INTERCEPTOR_TARGETNAME = " + auditTargetName);
        }
	}

	/**
     * Called before an object is saved. The interceptor may modify the state,
     * which will be used for the SQL INSERT and propagated to the persistent object.
     */
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
    {
    	if (entity instanceof Contract) {
    		Contract contract = (Contract) entity;
    		//log.debug("contract : " +contract.toString());
         }
    	
         return super.onSave(entity, id, state, propertyNames, types);
    }

    /**
     * Called when an object is detected to be dirty, during a flush.
     * The interceptor may modify the detected currentState, which will be
     * propagated to both the database and the persistent object.
     * Note that not all flushes end in actual synchronization with the database,
     * in which case the new currentState will be propagated to the object,
     * but not necessarily (immediately) to the database.
     * It is strongly recommended that the interceptor not modify the previousState.
     */
    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
            String[] propertyNames, Type[] types)
            throws CallbackException
    {
    	try {
    		if (entity instanceof Contract) {
        		log.debug("currentState cnt : " +currentState.length+", previousState : " + previousState.length +", propertyNames : " +propertyNames.length);
        		for(int i=0; i<propertyNames.length; i++) {
        			//log.debug("i : " +i +", propertyNames : " +propertyNames[i] +", currentState : " +currentState[i] +" , previousState : " + previousState[i]);
        			
        		}
        	}
    	}catch(Exception e) {
    		log.error(e, e);
    		return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    	}
    	
    	return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }
    /**
     * Called before an object is deleted. It is not recommended that the interceptor modify the state.
     */
}
