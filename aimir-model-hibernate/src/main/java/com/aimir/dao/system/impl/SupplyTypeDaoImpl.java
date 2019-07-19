package com.aimir.dao.system.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.SupplyTypeDao;
import com.aimir.model.system.SupplyType;

@Repository(value="supplytypeDao")
public class SupplyTypeDaoImpl extends AbstractHibernateGenericDao<SupplyType, Integer> implements SupplyTypeDao {

	@Autowired
	protected SupplyTypeDaoImpl(SessionFactory sessionFactory) {
		super(SupplyType.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<SupplyType> getSupplyTypeBySupplierId(Integer supplierId) {
		Criteria criteria = getSession().createCriteria(SupplyType.class);
		criteria.add(Restrictions.eq("supplier.id", supplierId));
		
		return criteria.list();
	}

	// 공급타입의 중복을 체크한다.
	public boolean checkSupplyType(Integer supplierId, Integer typeId) {
		Criteria criteria = getSession().createCriteria(SupplyType.class);
		criteria.add(Restrictions.eq("supplier.id", supplierId));
		criteria.add(Restrictions.eq("typeCode.id", typeId));
		
		if (criteria.list().size() > 0) {
			return false;
		} else {
			return true;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<SupplyType> getSupplyTypeList(Integer supplierId) {
    	if(supplierId==null||supplierId==0){
    		Query query = getSession().createQuery("SELECT s.typeCode from SupplyType s");
    		return query.list();
    		// return (List<SupplyType>)getHibernateTemplate().find("SELECT s.typeCode from SupplyType s");
    	}
    	else{
    		Query query = getSession().createQuery("SELECT s.typeCode from SupplyType s WHERE supplier_id = :supplierId");
    		query.setInteger("supplierId", supplierId);
    		
    		return query.list();
    		// return (List<SupplyType>)getHibernateTemplate().find("SELECT s.typeCode from SupplyType s WHERE supplier_id = ?", supplierId);
    	}
	}
	
	@SuppressWarnings("unchecked")
	public List<SupplyType> getSupplyTypeBySupplierIdTypeId(Integer supplierId, Integer typeId) {
		Criteria criteria = getSession().createCriteria(SupplyType.class);
		criteria.add(Restrictions.eq("supplier.id", supplierId));
		criteria.add(Restrictions.eq("typeCode.id", typeId));
		
		return criteria.list();
	}

}
