package com.aimir.dao.integration.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.integration.WSMeterConfigOBISDao;
import com.aimir.model.device.ThresholdWarning;
import com.aimir.model.integration.WSMeterConfigOBIS;


@Repository(value = "wsmeterconfigobisDao")
public class WSMeterConfigOBISDaoImpl extends AbstractHibernateGenericDao<WSMeterConfigOBIS, Long> implements WSMeterConfigOBISDao {

	private static Log log = LogFactory.getLog(WSMeterConfigOBIS.class);
	
	@Autowired
	protected WSMeterConfigOBISDaoImpl(SessionFactory sessionFactory) {
		super(WSMeterConfigOBIS.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@Override
	public WSMeterConfigOBIS get(String userId, String obisCode, String classId, String attributeNo) {
		
        StringBuffer query = new StringBuffer();

        query.append(" SELECT   o ");
        query.append(" FROM     WSMeterConfigOBIS o INNER JOIN o.meterConfUser WSMeterConfigUser ");
        query.append(" WHERE    WSMeterConfigUser.userId = :userid ");
        query.append(" AND      o.obisCode = :obiscode ");        	
        if (!obisCode.equals("USER")) {
            query.append(" AND      o.classId = :classid ");        	
            query.append(" AND      o.attributeNo = :attributeno ");        	
        }

        Query _query = getSession().createQuery(query.toString());
        _query.setParameter("userid",  userId);
        _query.setParameter("obiscode",  obisCode);
        if (!obisCode.equals("USER")) {
	        _query.setParameter("classid",  classId);
	        _query.setParameter("attributeno",  attributeNo);
        }
        
		if (_query.list().size() == 0) {
			return null;
		}           
        return (WSMeterConfigOBIS) _query.list().get(0);
        
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<WSMeterConfigOBIS> getMeterConfigOBISList(String userId) {

        StringBuffer query = new StringBuffer();

        query.append(" SELECT   o ");
        query.append(" FROM     WSMeterConfigOBIS o INNER JOIN o.meterConfUser WSMeterConfigUser ");
        query.append(" WHERE    WSMeterConfigUser.userId = :userid ");

        Query _query = getSession().createQuery(query.toString());
        _query.setParameter("userid",  userId);
        return _query.list();
		
	}
}