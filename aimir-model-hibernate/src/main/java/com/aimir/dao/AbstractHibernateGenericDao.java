package com.aimir.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.SearchCondition;

/**
 * @author Toby Ilmin Lee
 * 
 * @param <T>
 *            Persistent Class
 * @param <ID>
 *            ID Class
 */
public abstract class AbstractHibernateGenericDao<T, ID extends Serializable> implements GenericDao<T, ID> { 

	private Class<T> persisentClass;
	
	private SessionFactory sessionFactory;
	
	protected SessionFactory getSessionFactory() {
	    return sessionFactory;
	}

	protected void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
	}
	
	protected AbstractHibernateGenericDao(Class<T> persistentClass) {
		this.persisentClass = persistentClass;
	}

	public Class<T> getPersistentClass() {
		return persisentClass;
	}

	protected Session getSession() {
	    return getSessionFactory().getCurrentSession();
	}
	
	public T findById(ID id, boolean lock) {
		T entity;
		if (lock)
			entity = (T) getSession().get(getPersistentClass(), id, LockMode.PESSIMISTIC_WRITE);
		else
			entity = (T) getSession().get(getPersistentClass(), id);

		return entity;
	}
	
	@SuppressWarnings("unchecked")
	public T findByCondition(final String condition, final Object value) {
	    Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.addOrder(Order.desc("id"));                
        criteria.add(Restrictions.eq(condition, value));
        List<T> list = criteria.list();
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        else {
            return null;
        }
	}

	@SuppressWarnings("unchecked")
	public List<T> findByConditions(final Set<Condition> conditions) {
	    Criteria criteria = getSession().createCriteria(getPersistentClass());
        
        if(conditions != null) {
            Iterator<Condition> it = conditions.iterator();
            while(it.hasNext()){
                Condition condition = it.next();
                Criterion addCriterion = SearchCondition.getCriterion(condition);
                
                if(addCriterion != null){
                    criteria.add(addCriterion);
                }
                
                if(condition.getRestriction() == Restriction.ORDERBY)
                    criteria.addOrder(Order.asc(condition.getField()));
                if(condition.getRestriction() == Restriction.ORDERBYDESC)
                    criteria.addOrder(Order.desc(condition.getField()));
                
                if(condition.getRestriction() == Restriction.ALIAS)                         
                    criteria.createAlias(condition.getField(),(String)condition.getValue()[0]);                     
                if(condition.getRestriction() == Restriction.FIRST)                        
                    criteria.setFirstResult((Integer) condition.getValue()[0]);
                if(condition.getRestriction() == Restriction.MAX)
                    criteria.setMaxResults((Integer) condition.getValue()[0]);
             
            }
        }
        return criteria.list();
	}
	
    @SuppressWarnings("unchecked")
    public List<Object> findTotalCountByConditions(final Set<Condition> conditions) {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        if(conditions != null) {                                
            Iterator it = conditions.iterator();
            while(it.hasNext()){
                Condition condition = (Condition)it.next();
                Criterion addCriterion = SearchCondition.getCriterion(condition);
                if(addCriterion != null){
                    criteria.add(addCriterion);
                }                       
	            if(condition.getRestriction() == Restriction.ALIAS) {			                
                    criteria.createAlias(
                    	condition.getField(),(String)condition.getValue()[0]
                    );
	            }
            }
        }
        criteria.setProjection(
    		Projections.projectionList().add(Projections.rowCount(),"totalCount")
    	);
        return criteria.list();
    }
    
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findByConditionsAndProjections(
		Set<Condition> conditions, List<Projection> projections) {
    	
		Criteria criteria = getSession().createCriteria(getPersistentClass());

		// Set Condition
		if(conditions != null && !conditions.isEmpty()) {
			for(Condition condition : conditions) {
				Criterion addCriterion = SearchCondition.getCriterion(condition);
				if (addCriterion != null) {
					criteria.add(addCriterion);
				}
				else {
					SearchCondition.changeCriteria(criteria, condition);
				}				
			}
		}
		
		// Set Projection
		if(projections != null && projections.size() > 0) {
			ProjectionList pList = Projections.projectionList();
			for (int i = 0; i < projections.size(); i++) {
				pList.add(projections.get(i));				
			}
			criteria.setProjection(pList);
		}
		return criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

    @SuppressWarnings("unchecked")
	public List<Object> getSumFieldByCondition(
			Set<Condition> conditions, String field, String ...groupBy) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());

		if (conditions != null && !conditions.isEmpty()) {
			for (Condition condition : conditions) {
				Criterion addCriterion = SearchCondition.getCriterion(condition);
				if (addCriterion != null) {
					criteria.add(addCriterion);
				}
			}
		}
		
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.sum(field));
		
		if(groupBy != null && groupBy.length > 0) {
			for (String string : groupBy) {
				pList.add(Projections.groupProperty(string));
			}
		}
		
		criteria.setProjection(pList);
		return criteria.list();
	}

	public List<T> getAll() {
		return findByCriteria();
	}

	@SuppressWarnings("unchecked")
	protected List<T> findByCriteria(final Criterion... criterion) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		for (Criterion c : criterion) {
			crit.add(c);
		}
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByExample(final T exampleInstance, final String[] excludeProperty) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		Example example = Example.create(exampleInstance);
		for (String exclude : excludeProperty) {
			example.excludeProperty(exclude);
		}
		crit.add(example);
		return crit.list();
	}

	public T saveOrUpdate(T entity) {
		getSession().saveOrUpdate(entity);
		return entity;
	}
	
	 @Transactional(propagation = Propagation.REQUIRES_NEW)
	public T saveOrUpdate_requires_new(T entity) {
        getSession().saveOrUpdate(entity);
        return entity;
    }

	public T add(T entity) {
		getSession().save(entity);
		return entity;
	}
	
	 @Transactional(propagation = Propagation.REQUIRES_NEW)
	public T add_requires_new(T entity) {
        getSession().save(entity);
        return entity;
    }
	
	/* code s */
	public T codeAdd(T entity) {
		getSession().setFlushMode(FlushMode.AUTO);
		getSession().save(entity);
		flushAndClear();
		return entity;
	}
	public T codeParentAdd(T entity) {
		getSession().setFlushMode(FlushMode.AUTO);
		getSession().save(entity);
		return entity;
	}	
	public T codeUpdate(T entity) {
		getSession().setFlushMode(FlushMode.AUTO);
		getSession().update(entity);
		flushAndClear();
		return entity;
	}	
	public void codeDelete(T entity) {
		getSession().setFlushMode(FlushMode.AUTO);
		getSession().delete(entity);
		flushAndClear();
	}
	/*end*/
	
	/* group s */
	public T groupAdd(T entity) {
		getSession().setFlushMode(FlushMode.AUTO);
		getSession().save(entity);
		flushAndClear();
		return entity;
	}
	public T groupSaveOrUpdate(T entity) {
		getSession().setFlushMode(FlushMode.AUTO);
		getSession().saveOrUpdate(entity);
		flushAndClear();
		return entity;
	}
	public T groupUpdate(T entity) {
		getSession().setFlushMode(FlushMode.AUTO);
		getSession().update(entity);
		flushAndClear();
		return entity;
	}	
	public void groupDelete(T entity) {
		getSession().setFlushMode(FlushMode.AUTO);
		getSession().delete(entity);
		flushAndClear();
	}
	/*end*/

	/* mcu s */
	public T mcuAdd(T entity) {
		getSession().setFlushMode(FlushMode.AUTO);
		getSession().save(entity);
		flushAndClear();
		return entity;
	}
	/*end*/

	/* meter s */
	public T meterAdd(T entity) {
		getSession().setFlushMode(FlushMode.AUTO);
		getSession().save(entity);
		flushAndClear();
		return entity;
	}
	/*end*/

	/* modem s */
	public T modemAdd(T entity) {
		getSession().setFlushMode(FlushMode.AUTO);
		getSession().save(entity);
		flushAndClear();
		return entity;
	}
	/*end*/
	
	public T update(T entity) {
		getSession().update(entity);
		return entity;
	}

    public T update(T entity, Properties addCriteria) {
        return update(entity);
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public T update_requires_new(T entity) {
        getSession().update(entity);
        return entity;
    }

	public void delete(T entity) {
		getSession().delete(entity);
	}

	public T get(ID id) {
		return (T) getSession().get(persisentClass, id);
	}
	
	public boolean exists(ID id){
		T entity;
		entity =  (T) getSession().get(persisentClass, id);
		return (entity == null)? false:true;
	}

	@SuppressWarnings("unchecked")
	public int deleteById(final ID id) {
		int affectedRecords = getSession().createQuery(
							"delete from " + persisentClass.getSimpleName() + " where id = ?")
							.setParameter(0, id)
							.executeUpdate();
		return affectedRecords;
	}

	public int deleteAll() {
		return 0; // getSession().bulkUpdate("delete from " + persisentClass.getSimpleName());
	}

	public int getRowCount() {
		Integer cnt =  (Integer)getSession().createQuery(
				"select count(u) from " + persisentClass.getSimpleName() + " u")
				.uniqueResult();

		return (cnt == null) ? 0 : cnt.intValue();
	}

	public void flush() {
		getSession().flush();
	}

	public void clear() {
		getSession().clear();
	}

	public void flushAndClear() {
		flush();
		clear();
	}

	public void evict(Object entity) {
		getSession().evict(entity);		
	}

	public void merge(Object entity) {
		getSession().merge(entity);
	}
	
	public void refresh(Object entity) {
		getSession().refresh(entity);
	}
	
	public void saveOrUpdateAll(Collection<?> entities){
	    for (Object t : entities) {
	        getSession().saveOrUpdate(t);
	    }
		// getSession().saveOrUpdateAll(entities);
	}

	@Override
	public String callProcedure(Map<String, Object> parameter) {
		// TODO Auto-generated method stub
		return null;
	}

}
