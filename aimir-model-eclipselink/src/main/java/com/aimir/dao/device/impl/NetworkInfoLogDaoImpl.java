package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.NetworkInfoLogDao;
import com.aimir.model.device.NetworkInfoLog;
import com.aimir.model.device.NetworkInfoLogPk;
import com.aimir.util.Condition;

@Repository(value = "networkInfoLogDao")
public class NetworkInfoLogDaoImpl 
extends AbstractJpaDao<NetworkInfoLog, NetworkInfoLogPk>
implements NetworkInfoLogDao {

    public NetworkInfoLogDaoImpl() {
        super(NetworkInfoLog.class);
    }
    
    public NetworkInfoLog[] list(String command, String startDate, String endDate, int page, int limit)
    throws Exception
    {
        String sql = "From NetworkInfoLog n where n.id.dateTime between :startDate and :endDate ";
        if (command != null && !"All".equalsIgnoreCase(command))
            sql += " and n.id.command = :command" ;
        
        Query query = getEntityManager().createQuery(sql);
        query.setParameter("startDate", startDate+"000000");
        query.setParameter("endDate", endDate+"000000");
        
        if (command != null && !"All".equalsIgnoreCase(command))
            query.setParameter("command", command);
        
        query.setFirstResult(page * limit);
        query.setMaxResults(limit);
        
        return (NetworkInfoLog[])query.getResultList().toArray(new NetworkInfoLog[0]);
    }

    @Override
    public Class<NetworkInfoLog> getPersistentClass() {
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
