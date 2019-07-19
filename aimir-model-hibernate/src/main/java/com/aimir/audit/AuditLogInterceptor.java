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
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants.AuditAction;
import com.aimir.model.BaseObject;
import com.aimir.model.system.AuditLog;
import com.aimir.model.system.Code;
import com.aimir.util.SessionContext;

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
    public boolean onSave(Object entity,Serializable id, Object[] state,String[] propertyNames,Type[] types)
    {
        try{
        	if(sessionContextFactory != null){
                SessionContext sessionContext = (SessionContext) sessionContextFactory.getObject();
                if(sessionContext != null && sessionContext.getUser() != null){
                	auditTargetName = sessionContext.getUser().getLoginId();
                }        		
        	}
        }catch(Exception e){
        	log.debug("Have no SessionContext (save) : " + e.getMessage());
        }

        if (entity instanceof IAuditable) {
            log.debug("ENTITY[" + entity.getClass().getName() + "] ID[" + id + "]");
            Session session = null;
            TransactionStatus txStatus = null;
            try {
                AuditLog auditLog = new AuditLog();
                auditLog.setAction(AuditAction.SAVED);
                auditLog.setCreatedDate(new Date());
                auditLog.setEntityId(id instanceof Long? (Long)id:new Long((Integer)id));
                auditLog.setEntityName(entity.getClass().getName());
                auditLog.setInstanceName(((IAuditable) entity).getInstanceName());

                if(!auditTargetName.equals("")){
                	auditLog.setLoginId(auditTargetName);
                }

                txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
                session = transactionManager.getSessionFactory().getCurrentSession();
                session.save(auditLog);
                session.flush();
                transactionManager.commit(txStatus);
            }
            catch (Exception e) {
                log.error(e,e);
                if (txStatus != null)
                    transactionManager.rollback(txStatus);
            }
            finally {
            }
        }
        return true;
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
    public boolean onFlushDirty(Object entity,Serializable id,
            Object[] currentState,Object[] previousState,
            String[] propertyNames,Type[] types)
            throws CallbackException
    {
        try{
        	if(sessionContextFactory != null){
            	Object obj = sessionContextFactory.getObject();
            	if(obj != null && (obj instanceof SessionContext)){
                    SessionContext sessionContext = (SessionContext) obj;
                    if(sessionContext != null && sessionContext.getUser() != null){
                    	auditTargetName = sessionContext.getUser().getLoginId();
                    }        		
            	}        		
        	}
        }catch(Exception e){
        	log.debug("Have no SessionContext (flush): " + e.getMessage());
        }

        if (entity instanceof IAuditable) {
            StringBuilder buf = new StringBuilder();
            buf.append("ENTITY[" + entity.getClass().getName() + "] ID[" + id + "]");
            Session session = null;
            TransactionStatus txStatus = null;
            try {
                txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
                session = transactionManager.getSessionFactory().getCurrentSession();

                if (previousState == null)
                    previousState = new Object[currentState.length];

                for (int i = 0; i < propertyNames.length; i++) {
                    /*
                     * 속성으로 관계된 클래스의 equals가 구현되지 않아서 다른 객체로 인식하여 변경 이력이 남는다.
                     * BaseObject을 상속받지 않은 객체와 Primitive type 속성만 비교하도록 한다.
                     */
                    if (currentState[i] instanceof Code || !(currentState[i] instanceof BaseObject)) {
                        if ( ((currentState[i] != null && previousState[i] == null) ||
                                (currentState[i] == null && previousState[i] != null)) ||
                                (currentState[i] != null && previousState[i] != null && !currentState[i].equals(previousState[i]))) {
                            if (currentState[i] != null && previousState[i] != null && currentState[i] instanceof Code) {
                                if (((Code)currentState[i]).getId().equals(((Code)previousState[i]).getId()))
                                    continue;
                            }

                            buf.append(" PROPERTY_NAME[" + propertyNames[i] +
                                    "] PREVIOUS_STATE[" + previousState[i] +
                                    "] CURRENT_STATE[" + currentState[i] +
                                    "] AUDIT_TARGET[" + auditTargetName + "]");

                            AuditLog auditLog = new AuditLog();
                            auditLog.setAction(AuditAction.UPDATED);
                            auditLog.setCreatedDate(new Date());
                            auditLog.setEntityId(id instanceof Long? (Long)id:new Long((Integer)id));
                            auditLog.setEntityName(entity.getClass().getName());
                            auditLog.setPropertyName(propertyNames[i]);
                            auditLog.setInstanceName(((IAuditable) entity).getInstanceName());

                            if(String.valueOf(previousState[i]).length() > 1024){
                                auditLog.setPreviousState(String.valueOf(previousState[i]).substring(0,1023));
                            }else{
                                auditLog.setPreviousState(String.valueOf(previousState[i]));
                            }

                            if(String.valueOf(currentState[i]).length() > 1024){
                                auditLog.setCurrentState(String.valueOf(currentState[i]).substring(0, 1023));
                            }else{
                                auditLog.setCurrentState(String.valueOf(currentState[i]));
                            }

                            if(!auditTargetName.equals("")){
                            	auditLog.setLoginId(auditTargetName);
                            }

                            session.save(auditLog);
                            session.flush();
                        }
                    }
                }
                transactionManager.commit(txStatus);
            }
            catch (Exception e) {
                log.error (e, e);
                if (txStatus != null)
                    transactionManager.rollback(txStatus);
            }
            finally {
            }
            log.debug(buf.toString());
        }

        return true;
    }

    /**
     * Called before an object is deleted. It is not recommended that the interceptor modify the state.
     */
    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        try{
        	if(sessionContextFactory != null){
                SessionContext sessionContext = (SessionContext) sessionContextFactory.getObject();
                if(sessionContext != null && sessionContext.getUser() != null){
                	auditTargetName = sessionContext.getUser().getLoginId();
                }        		
        	}
        }catch(Exception e){
        	log.debug("Have no SessionContext (delete) : " + e.getMessage());
        }

        if (entity instanceof IAuditable) {
            log.debug("ENTITY[" + entity.getClass().getName() + "] ID[" + id + "]");
            Session session = null;
            TransactionStatus txStatus = null;
            try {
                txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
                session = transactionManager.getSessionFactory().getCurrentSession();

                AuditLog auditLog = new AuditLog();
                auditLog.setAction(AuditAction.DELETED);
                auditLog.setCreatedDate(new Date());
                auditLog.setEntityId(id instanceof Long? (Long)id:new Long((Integer)id));
                auditLog.setEntityName(entity.getClass().getName());
                auditLog.setInstanceName(((IAuditable) entity).getInstanceName());

                if(!auditTargetName.equals("")){
                	auditLog.setLoginId(auditTargetName);
                }

                session.save(auditLog);
                session.flush();

                transactionManager.commit(txStatus);
            }
            catch (Exception e) {
                if (txStatus != null)
                    transactionManager.rollback(txStatus);
            }
            finally {
            }
        }
    }
}
