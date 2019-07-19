package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.model.device.EventAlert;
import com.aimir.util.CommonUtils2;

@Repository(value = "eventalertDao")
public class EventAlertDaoImpl extends AbstractHibernateGenericDao<EventAlert, Integer> implements EventAlertDao {

	@Autowired
	protected EventAlertDaoImpl(SessionFactory sessionFactory) {
		super(EventAlert.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<EventAlert> getEventAlertsByType(String eventAlertType) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT e ");
		hqlBuf.append(" FROM EventAlert e");
		hqlBuf.append(" WHERE e.eventAlertType = :eventAlertType"); 
		hqlBuf.append(" ORDER BY e.NAME"); 
		
		Query query = getSession().createQuery(hqlBuf.toString());

		query.setParameter("eventAlertType", CommonConstants.EventAlertType.valueOf(eventAlertType));
		
		List result = query.list();
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public List<EventAlert> getEventAlertList(Map<String, String> conditionMap){
        StringBuffer hqlBuf = new StringBuffer();       
        hqlBuf.append(" FROM EventAlert");       
        hqlBuf.append(" ORDER BY NAME");
        Query query = getSession().createQuery(hqlBuf.toString());
                
        // paging 로직 추가.
        query = CommonUtils2.addPagingForQuery(query, conditionMap);
        List result = query.list();
        
        return result;
    }

	public Integer getRowCountByQuery(){
        StringBuffer hqlBuf = new StringBuffer();
        hqlBuf.append(" SELECT COUNT(e.id) ");
        hqlBuf.append(" FROM EventAlert e");

        Query query = getSession().createQuery(hqlBuf.toString());

        List result = query.list();

        return Integer.parseInt(result.get(0).toString());
    }
}
