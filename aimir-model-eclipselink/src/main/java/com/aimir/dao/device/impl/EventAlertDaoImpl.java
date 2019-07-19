package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.model.device.EventAlert;
import com.aimir.util.CommonUtils;
import com.aimir.util.Condition;

@Repository(value = "eventalertDao")
public class EventAlertDaoImpl extends AbstractJpaDao<EventAlert, Integer> implements EventAlertDao {

	public EventAlertDaoImpl() {
		super(EventAlert.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<EventAlert> getEventAlertsByType(String eventAlertType) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT e ");
		hqlBuf.append(" FROM EventAlert e");
		hqlBuf.append(" WHERE e.eventAlertType = :eventAlertType"); 
		hqlBuf.append(" ORDER BY e.NAME"); 
		
		Query query = em.createQuery(hqlBuf.toString(), EventAlert.class);

		query.setParameter("eventAlertType", CommonConstants.EventAlertType.valueOf(eventAlertType));
		
		List result = query.getResultList();
		
		return result;
	}

	@SuppressWarnings("unchecked")
    public List<EventAlert> getEventAlertList(Map<String, String> conditionMap){
        StringBuffer hqlBuf = new StringBuffer();       
        hqlBuf.append(" FROM EventAlert");       
        hqlBuf.append(" ORDER BY NAME");
        Query query = em.createQuery(hqlBuf.toString());
                
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EventAlert> criteria = cb.createQuery(EventAlert.class);
        Root<EventAlert> from = criteria.from(EventAlert.class);
        criteria.orderBy(cb.asc(from.get("name")));
        
        TypedQuery typedQuery = em.createQuery(criteria);
        // paging 로직 추가.
        query = CommonUtils.addPagingForQuery(query, conditionMap);
        List result = query.getResultList();
        
        return result;
    }

    public Integer getRowCountByQuery(){
        StringBuffer hqlBuf = new StringBuffer();
        hqlBuf.append(" SELECT COUNT(e.id) ");
        hqlBuf.append(" FROM EventAlert e");

        Query query = em.createQuery(hqlBuf.toString(), EventAlert.class);

        List result = query.getResultList();

        return Integer.parseInt(result.get(0).toString());
    }
	
    @Override
    public Class<EventAlert> getPersistentClass() {
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
