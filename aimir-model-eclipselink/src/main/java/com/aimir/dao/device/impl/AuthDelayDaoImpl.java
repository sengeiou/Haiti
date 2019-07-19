// INSERT SP-121
package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityExistsException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.AuthDelayDao;
import com.aimir.model.device.AuthDelay;
import com.aimir.util.Condition;


@Repository(value = "authdelayDao")
public class AuthDelayDaoImpl extends AbstractJpaDao<AuthDelay, Long> implements AuthDelayDao {

	public AuthDelayDaoImpl() {
		super(AuthDelay.class);
	}

	public AuthDelay getAuthDelay(String ipaddress) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("SELECT a.id, a.ipaddress, a.errorcnt, a.lastdate ");
		hqlBuf.append("FROM AuthDelay a ");
		hqlBuf.append("WHERE a.ipaddress = :ipaddress ");
		//hqlBuf.append("WHERE a.ipaddress = ? ");

		Query query = em.createQuery(hqlBuf.toString(), AuthDelay.class);

		query.setParameter("ipaddress", ipaddress);
		//query.setParameter(1, ipaddress );
		
		List result = query.getResultList();
		
		if (result == null || result.size() == 0) {
			return null;
		}
		else {
			Object[] resultData = (Object[]) result.get(0);
			AuthDelay authdelay = new AuthDelay();
			authdelay.setId(Long.parseLong(resultData[0].toString()));
			if (resultData[1] != null)authdelay.setIpAddress(resultData[1].toString());
			if (resultData[2] != null)authdelay.setErrorCnt(Integer.parseInt(resultData[2].toString()));
			if (resultData[3] != null)authdelay.setLastDate(resultData[3].toString());
			return authdelay;
		}
	}

	public AuthDelay getAuthDelay(String ipaddress, Integer limitcnt) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("SELECT a.id, a.ipaddress, a.errorcnt, a.lastdate ");
		hqlBuf.append("FROM AuthDelay a ");
		hqlBuf.append("WHERE a.ipaddress = :ipaddress ");
		hqlBuf.append("AND a.errorcnt >= :limitcnt ");
		//hqlBuf.append("WHERE a.ipaddress = ? ");
		//hqlBuf.append("AND a.errorcnt >= ? ");
		
		Query query = em.createQuery(hqlBuf.toString(), AuthDelay.class);

		query.setParameter("ipaddress", ipaddress);
		query.setParameter("limitcnt", limitcnt);
		//query.setParameter(1, ipaddress);
		//query.setParameter(2, limitcnt);
		
		List result = query.getResultList();
		
		if (result == null || result.size() == 0) {
			return null;
		}
		else {
			Object[] resultData = (Object[]) result.get(0);
			AuthDelay authdelay = new AuthDelay();
			authdelay.setId(Long.parseLong(resultData[0].toString()));
			if (resultData[1] != null)authdelay.setIpAddress(resultData[1].toString());
			if (resultData[2] != null)authdelay.setErrorCnt(Integer.parseInt(resultData[2].toString()));
			if (resultData[3] != null)authdelay.setLastDate(resultData[3].toString());
			return authdelay;
		}	
	}	

    @Override
    public AuthDelay saveOrUpdate(AuthDelay entity) {
    	if ( entity.getId() == null ){
    		try {
                em.persist(entity);
            } catch (EntityExistsException e) {
               throw e;
            }
    	} else {
    		try {
    			 em.merge(entity);
            } catch (EntityExistsException e) {
                throw e;
    		}
    	}
        return entity;
    }
	
    @Override
    public Class<AuthDelay> getPersistentClass() {
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
