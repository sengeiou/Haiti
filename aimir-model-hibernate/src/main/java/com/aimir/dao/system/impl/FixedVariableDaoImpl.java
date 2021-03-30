package com.aimir.dao.system.impl;

import javax.persistence.NoResultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.FixedVariableDao;
import com.aimir.model.mvm.Holidays;
import com.aimir.model.system.FixedVariable;

@Repository(value = "fixedVariableDao")
public class FixedVariableDaoImpl extends AbstractHibernateGenericDao<FixedVariable, Integer> implements FixedVariableDao {
	private Log logger = LogFactory.getLog(FixedVariableDaoImpl.class);
	
	@Autowired
	protected FixedVariableDaoImpl(SessionFactory sessionFactory) {
		super(FixedVariable.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public FixedVariable getFixedVariableDao(String name, Integer tariffId, String applydate) {
		if(name == null || applydate == null) {
			logger.info("name or applidate is null! name : " + name +", tariffId : " + tariffId+", applydate : " +applydate);
			return null;
		}
		
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("\n SELECT * FROM ( ");
			sb.append("\n 	SELECT f.* FROM FIXED_VARIABLE f");
			sb.append("\n 	WHERE");
			sb.append("\n 		1 = 1");
			
			if(name != null)		
				sb.append("\n 		AND f.NAME = :name");
			
			if(tariffId != null)		
				sb.append("\n 		AND f.TARIFFTYPE_ID = :tariffId");
			
			if(applydate != null)		
				sb.append("\n 		AND f.APPLYDATE <= :applydate");

			sb.append("\n 		ORDER BY f.APPLYDATE DESC ");
			sb.append("\n 		FETCH first 1 ROW only");
			sb.append("\n )");
			
			
			Query query = getSession().createNativeQuery(sb.toString(), FixedVariable.class);
			return (FixedVariable) query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}


}
