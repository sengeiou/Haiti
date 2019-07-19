package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MeterEventDao;
import com.aimir.model.device.MeterEvent;
import com.aimir.util.Condition;

@Repository(value = "metereventDao")
public class MeterEventDaoImpl extends AbstractJpaDao<MeterEvent, Long> implements MeterEventDao {

    Log log = LogFactory.getLog(MeterEventDaoImpl.class);
    
    public MeterEventDaoImpl() {
        super(MeterEvent.class);
    }

    @Override
    public Class<MeterEvent> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEventNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEventIdsByNames(
            String[] meterEventNames) {
        // TODO Auto-generated method stub
        return null;
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

        Query query = getEntityManager().createQuery(sb.toString());
        query.setParameter("modelName", modelName);
        query.setParameter("meterEventNames", meterEventNames);

        return query.getResultList();

    }
    
    public MeterEvent getMeterEventByCondition(Map<String,Object> conditionMap) {
    	return null;
    }
}
