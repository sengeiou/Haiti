package com.aimir.dao.device.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.AsyncCommandParamPk;

@Repository(value = "asynccommandparamDao")
public class AsyncCommandParamDaoImpl 
extends AbstractHibernateGenericDao<AsyncCommandParam, AsyncCommandParamPk>
implements AsyncCommandParamDao {

	@Autowired
	protected AsyncCommandParamDaoImpl(SessionFactory sessionFactory) {
		super(AsyncCommandParam.class);
		super.setSessionFactory(sessionFactory);
	}
	
	public Integer getMaxNum(String mcuId, Long trId) {
		
        StringBuffer sb = new StringBuffer();
        
        sb.append("\n SELECT max(a.id.num)");
        sb.append("\n FROM AsyncCommandParam a");
        sb.append("\n WHERE a.id.mcuId=:mcuId");
        sb.append("\n AND a.id.trId=:trId");

        Query query = getSession().createQuery(sb.toString());
        query.setString("mcuId",mcuId);
        query.setLong("trId",trId);

        Number totalCount = (Number)query.uniqueResult();
        int returnData = 0;

        if(totalCount == null)
        	returnData = 0;
        else {
        	returnData = totalCount.intValue();
        }
        return returnData;

	}

	@Override
	public List<AsyncCommandParam> getCmdParams(String deviceSerial, long trId, String paramName) {
		Criteria criteria = getSession().createCriteria(AsyncCommandParam.class);
		criteria.add(Restrictions.eq("id.mcuId", deviceSerial));
		criteria.add(Restrictions.eq("id.trId", trId));
		
		if(paramName != null){
			criteria.add(Restrictions.eq("paramType", paramName));			
		}
		
		return criteria.list();
	}
	
    @SuppressWarnings("unchecked")
    @Override
    public List<AsyncCommandParam> getCmdParamsByTrnxId(String deviceSerial, String paramName) {
        StringBuilder sb = new StringBuilder();
        sb.append("from AsyncCommandParam acp ");
        sb.append(" where acp.id.mcuId=:mcuId ");
        sb.append(" and acp.id.trId = (select max(acpb.id.trId) from AsyncCommandParam acpb)");
        if(paramName != null && !paramName.equals("")){
            sb.append(" and acp.paramType = :paramType");           
        }

        Query query = getSessionFactory().getCurrentSession().createQuery(sb.toString());
        query.setString("mcuId", deviceSerial);
        if(paramName != null && !paramName.equals("")){
            query.setString("paramType", paramName);    
        }
        
        return query.list();
    }

}

