package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MeterEventDao;
import com.aimir.model.device.MeterEvent;
import com.aimir.util.StringUtil;

@Repository(value = "metereventDao")
public class MeterEventDaoImpl extends AbstractHibernateGenericDao<MeterEvent, Long> implements MeterEventDao {

    Log log = LogFactory.getLog(MeterEventDaoImpl.class);
    
    @Autowired
    protected MeterEventDaoImpl(SessionFactory sessionFactory) {
        super(MeterEvent.class);
        super.setSessionFactory(sessionFactory);
    }

    /**
     * 미터 이벤트 로그 - 이벤트 이름 리스트 조회
     *
     * @return
     */
    public List<Map<String, Object>> getEventNames() {
        Criteria crit = getSession().createCriteria(MeterEvent.class)
        .setProjection(Projections.groupProperty("name").as("eventName"))
        .addOrder(Order.asc("eventName"));

        return crit.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * 미터 이벤트 로그 - 이벤트 이름 리스트로 이벤트 아이디 리스트 조회
     *
     * @param meterEventNames
     * @return
     */
    public List<Map<String, Object>> getEventIdsByNames(String[] meterEventNames) {
        Criteria crit = getSession().createCriteria(MeterEvent.class)
        .setProjection(Projections.groupProperty("id").as("meterEventId"))
        .add(Restrictions.in("name", meterEventNames))
        .addOrder(Order.asc("meterEventId"));

        return crit.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    /**
     * 이벤트 이름과 모델명에 해당하는 아이디 조회
     * @param meterEventNames
     * @param modelName
     * @return MeterEvent.Id
     */
    public List<Object> getEventIdsByNames2(String meterEventNames, String modelName){
        StringBuilder sb = new StringBuilder();
        sb.append("\n SELECT id,name");
        sb.append("\n FROM MeterEvent");
        sb.append("\n WHERE model = :modelName");
        sb.append("\n AND UPPER(NAME) = :meterEventNames");

        Query query = getSession().createQuery(sb.toString());
        query.setString("modelName", modelName);
        query.setString("meterEventNames", meterEventNames);

        return query.list();

    }
    
    public MeterEvent getMeterEventByCondition(Map<String,Object> conditionMap) {
    	MeterEvent meterEvent = null;
    	String id = StringUtil.nullToBlank(conditionMap.get("id"));
    	String name = StringUtil.nullToBlank(conditionMap.get("name"));
    	
    	StringBuffer sb = new StringBuffer();
    	sb.append("\nFROM 	MeterEvent me ");
    	sb.append("\nWHERE 	me.id = :id");
    	if(!name.isEmpty()) {
    		sb.append("\nAND 	me.name = :name");
    	}
    	
    	Query query = getSession().createQuery(sb.toString());
    	query.setString("id", id);
    	if(!name.isEmpty()) {
    		query.setString("name", name);
    	}
    	
    	List<MeterEvent> list = query.list();
    	if(list.size() > 0) {
    		meterEvent = list.get(0);
    	}
    	
    	return meterEvent;
    }

}
