package com.aimir.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.util.Condition;
import com.aimir.util.SearchCondition;

/**
 * @author Elevas
 * 
 * @param <T>
 *            Persistent Class
 * @param <ID>
 *            ID Class
 */
public abstract class AbstractJpaDao<T, ID extends Serializable>
        implements GenericDao<T, ID> {
    private static Log log = LogFactory.getLog(AbstractJpaDao.class);

    @PersistenceContext
    protected EntityManager em;

    private Class<T> entityClass;
    
    public AbstractJpaDao() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass()
                .getGenericSuperclass();
        this.entityClass = (Class<T>) genericSuperclass
                .getActualTypeArguments()[0];
    }

    public AbstractJpaDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public EntityManager getEntityManager() {
        return this.em;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public T findById(ID id, boolean lock) {
        if (id == null)
            throw new PersistenceException("Id may not be null");

        try {
            if (lock)
                return em.find(entityClass, id, LockModeType.PESSIMISTIC_WRITE);
            else
                return (T) em.find(entityClass, id);
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    public T findByCondition(String condition, Object value) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(getEntityClass());
            Path p1 = cq.from(getEntityClass());

            StringTokenizer st = new StringTokenizer(condition, ".");
            while (st.hasMoreTokens()) {
                p1 = p1.get(st.nextToken());
            }

            cq.where(cb.equal(p1, value));
            TypedQuery<T> q = em.createQuery(cq);
            return q.getSingleResult();

            // CriteriaBuilder cb = em.getCriteriaBuilder();
            // CriteriaQuery<T> cq = cb.createQuery(getEntityClass());
            // Root<T> root = cq.from(getEntityClass());
            // cq.where(cb.equal(root.get(condition), value));
            // TypedQuery<T> q = em.createQuery(cq);
            // return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> findByConditions(final Set<Condition> conditions) {
        List<T> results;
        //AdditionalCriteria property
        String emACmdevId = null;
        if(getEntityClass().getSimpleName().startsWith("Lp") || getEntityClass().getSimpleName().startsWith("MeteringData")) {
            for(Condition con : conditions) {
                 if( con.getField().equals("id.mdevId") ){
                    emACmdevId = con.getValue()[0].toString();
                }
             }
        }
        // set up the Criteria query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(getEntityClass());
        Root<T> root = cq.from(getEntityClass());

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (conditions != null) {
            Iterator<Condition> it = conditions.iterator();
            while (it.hasNext()) {
                Condition condition = it.next();
                Predicate addCriterion = SearchCondition.getCriterion(cb,
                        condition, root);

                if (addCriterion != null) {
                    predicates.add(addCriterion);
                }

                cq = SearchCondition.changeCriteria(cb, cq, condition, root);
            }
        }

        cq.where(predicates.toArray(new Predicate[] {}));
        TypedQuery<T> q = em.createQuery(cq);
        
        if(getEntityClass().getSimpleName().startsWith("Lp") || getEntityClass().getSimpleName().startsWith("MeteringData")) {
            if(emACmdevId != null) {
                q.setParameter("mdevId", emACmdevId);
            } else {
                q.setParameter("mdevId", "null");
            }
        }

        if (conditions != null) {
            Iterator<Condition> it = conditions.iterator();
            while (it.hasNext()) {
                Condition condition = it.next();
                q = SearchCondition.changeCriteria(q, condition);
            }
        }

        results = q.getResultList();
        return results;
    }

    @SuppressWarnings("unchecked")
    public List<Object> findTotalCountByConditions(Set<Condition> conditions) {
        // set up the Criteria query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(getEntityClass());
        Root<T> root = cq.from(getEntityClass());

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (conditions != null) {
            Iterator<Condition> it = conditions.iterator();
            while (it.hasNext()) {
                Condition condition = it.next();
                Predicate addCriterion = SearchCondition.getCriterion(cb,
                        condition, root);

                if (addCriterion != null) {
                    predicates.add(addCriterion);
                }

                cq = SearchCondition.changeCriteria(cb, cq, condition, root);
            }
        }

        cq.select((Selection<? extends T>) cb.count(root).alias("totalCount"));
        cq.where(predicates.toArray(new Predicate[] {}));

        TypedQuery<T> q = em.createQuery(cq);

        if (conditions != null) {
            Iterator<Condition> it = conditions.iterator();
            while (it.hasNext()) {
                Condition condition = it.next();
                q = SearchCondition.changeCriteria(q, condition);
            }
        }

        return (List<Object>) q.getResultList();
    }

    public List<T> getAll() {
        // set up the Criteria query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(getEntityClass());
        Root<T> root = cq.from(getEntityClass());
        CriteriaQuery<T> all = cq.select(root);
        TypedQuery<T> q = em.createQuery(all);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    protected List<T> findByCriteria(final Predicate... predicate) {
        List<T> results;
        // set up the Criteria query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(getEntityClass());
        Root<T> root = cq.from(getEntityClass());

        List<Predicate> predicates = new ArrayList<Predicate>();
        for (Predicate c : predicate) {
            predicates.add(c);
        }

        cq.where(predicates.toArray(new Predicate[] {}));

        TypedQuery<T> q = em.createQuery(cq);

        return q.getResultList();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public T saveOrUpdate(T entity) {
        try {
            em.persist(entity);
        } catch (EntityExistsException e) {
            em.merge(entity);
        }
        return entity;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public T saveOrUpdate_requires_new(T entity) {
        try {
            em.persist(entity);
        } catch (EntityExistsException e) {
            em.merge(entity);
        }
        return entity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public T add(T entity) {
        try {
            em.persist(entity);
        } catch (Exception e) {
            log.error(e, e);
        }

        return entity;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public T add_requires_new(T entity) {
        try {
            em.persist(entity);
        } catch (Exception e) {
            log.error(e, e);
        }

        return entity;
    }
    
	/* code s */
    public T codeAdd(T entity) {
        em.setFlushMode(FlushModeType.AUTO);
        add(entity);
        flushAndClear();
        return entity;
    }

    public T codeParentAdd(T entity) {
        em.setFlushMode(FlushModeType.AUTO);
        add(entity);
        return entity;
    }

    public T codeUpdate(T entity) {
        em.setFlushMode(FlushModeType.AUTO);
        update(entity);
        flushAndClear();
        return entity;
    }

    public void codeDelete(T entity) {
        em.setFlushMode(FlushModeType.AUTO);
        delete(entity);
        flushAndClear();
    }
    /* end */

    /* group s */
    public T groupAdd(T entity) {
        em.setFlushMode(FlushModeType.AUTO);
        add(entity);
        flushAndClear();
        return entity;
    }

    public T groupSaveOrUpdate(T entity) {
        em.setFlushMode(FlushModeType.AUTO);
        saveOrUpdate(entity);
        flushAndClear();
        return entity;
    }

    public T groupUpdate(T entity) {
        em.setFlushMode(FlushModeType.AUTO);
        update(entity);
        flushAndClear();
        return entity;
    }

    public void groupDelete(T entity) {
        em.setFlushMode(FlushModeType.AUTO);
        delete(entity);
        flushAndClear();
    }
    /* end */

    /* mcu s */
    public T mcuAdd(T entity) {
        em.setFlushMode(FlushModeType.AUTO);
        add(entity);
        flushAndClear();
        return entity;
    }
    /* end */

    /* meter s */
    public T meterAdd(T entity) {
        em.setFlushMode(FlushModeType.AUTO);
        add(entity);
        flushAndClear();
        return entity;
    }
    /* end */

    /* modem s */
    public T modemAdd(T entity) {
        em.setFlushMode(FlushModeType.AUTO);
        add(entity);
        flushAndClear();
        return entity;
    }
    /* end */

    @Transactional(propagation = Propagation.REQUIRED)
    public T update(T entity) {
        try {
            em.merge(entity);
        } catch (Exception e) {
            log.error(e, e);
        }
        return entity;
    }
    
    /*
     * SP-454 added 2017.01.23 for Lp update
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public T update(T entity, Properties addCriteria) {
        try {
            String key = null;
            for (Enumeration e = addCriteria.keys(); e.hasMoreElements(); ) {
                key = (String)e.nextElement();
                em.setProperty(key, addCriteria.get(key));
            }
            em.merge(entity);
        } catch (Exception e) {
            log.error(e, e);
        }
        return entity;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public T update_requires_new(T entity) {
        try {
            em.merge(entity);
        } catch (Exception e) {
            log.error(e, e);
        }
        return entity;
    }

    public void delete(T entity) {
        try {
            em.remove(entity);
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    public T get(ID id) {
        return (T) findById(id, false);
    }

    public boolean exists(ID id) {
        T entity;
        entity = (T) get(id);
        return (entity == null) ? false : true;
    }

    @SuppressWarnings("unchecked")
    public int deleteById(ID id) {
        Query query = em.createQuery("delete from "
                + entityClass.getSimpleName() + " where id = :id");
        int deletedCount = query.setParameter("id", id).executeUpdate();
        return deletedCount;
    }

    public int deleteAll() {
        Query query = em
                .createQuery("delete from " + entityClass.getSimpleName());
        return query.executeUpdate();
    }

    public int getRowCount() {
        Query query = em.createQuery(
                "select count(u) from " + entityClass.getSimpleName() + " u");
        Object result = query.getSingleResult();
        return (result == null) ? 0 : (Integer) result;
    }

    public void flush() {
        em.flush();
    }

    public void clear() {
        em.clear();
    }

    public void flushAndClear() {
        flush();
        clear();
    }

    public void evict(Object entity) {
        em.detach(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void merge(Object entity) {
    	try {
            em.merge(entity);
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    public void refresh(Object entity) {
        em.refresh(entity);
    }

    public void saveOrUpdateAll(Collection<?> entities) {
        for (T t : (T[]) entities.toArray()) {
            saveOrUpdate(t);
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
	public String callProcedure(Map<String, Object> parameter) {
    	
    	String procedureName = parameter.get("PROCEDURE_NAME").toString();
    	String param = parameter.get("THREAD_NUM").toString();
    	
    	if(procedureName == null || procedureName.isEmpty()) 
    		return "";
    	
    	StoredProcedureQuery spq  = em.createStoredProcedureQuery(procedureName);
    	spq.registerStoredProcedureParameter("THREAD_NUM", String.class, ParameterMode.IN);
    	spq.registerStoredProcedureParameter("P_RESULT", String.class, ParameterMode.OUT);
    	spq.setParameter("THREAD_NUM", param);
    	spq.execute();
    	
    	String result = (String) spq.getOutputParameterValue("P_RESULT");
    	    	
    	return result;
	}

	public List<T> findByExample(T exampleInstance, String[] excludeProperty) {
        // TODO Auto-generated method stub
        return null;
    }
}
