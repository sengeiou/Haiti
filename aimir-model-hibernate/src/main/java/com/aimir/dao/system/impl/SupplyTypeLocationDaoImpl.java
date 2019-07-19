package com.aimir.dao.system.impl;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.SupplyTypeLocationDao;
import com.aimir.model.system.SupplyTypeLocation;

@Repository(value="supplytypelocationDao")
public class SupplyTypeLocationDaoImpl extends AbstractHibernateGenericDao<SupplyTypeLocation, Integer> implements SupplyTypeLocationDao{

	@Autowired
	protected SupplyTypeLocationDaoImpl(SessionFactory sessionFactory) {
		super(SupplyTypeLocation.class);
		super.setSessionFactory(sessionFactory);
	}

	// 공급타입의 중복을 체크한다.
	public boolean checkSupplyType(Integer typeId) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT st.id");
		hqlBuf.append(" FROM SupplyTypeLocation l JOIN l.supplyType st");
		hqlBuf.append("                           JOIN st.typeCode c");
		hqlBuf.append(" WHERE c.id = :typeId");
		Query query = getSession().createQuery(hqlBuf.toString());
		
		query.setParameter("typeId", typeId);
		
		if (query.list().size() > 0) {
			return false;
		} else {
			return true;
		}
	}

}
