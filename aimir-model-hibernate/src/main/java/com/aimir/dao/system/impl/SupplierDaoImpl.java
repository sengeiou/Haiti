package com.aimir.dao.system.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Supplier;

@Repository(value="supplierDao")
public class SupplierDaoImpl extends AbstractHibernateGenericDao<Supplier, Integer> implements SupplierDao{
    
	@Autowired
	protected SupplierDaoImpl(SessionFactory sessionFactory) {
		super(Supplier.class);
		super.setSessionFactory(sessionFactory);
	}


	@SuppressWarnings("unchecked")
	public Supplier getSupplierByName(String supplierName) {
		Query query = getSession().createQuery("FROM Supplier s WHERE name = :supplierName");
		query.setString("supplierName", supplierName);
		
		List<Supplier> sList = query.list();
		Supplier supplier = new Supplier();
		if (sList.size() > 0) {
			supplier = sList.get(0);
		}
		return supplier;
	}

	@SuppressWarnings("unchecked")
	public Supplier getSupplierById(int supplierId) {
		Query query = getSession().createQuery("FROM Supplier s WHERE id = :supplierId");
		query.setInteger("supplierId", supplierId);
		
		List<Supplier> sList = query.list();
		Supplier supplier = new Supplier();
		if (sList.size() > 0) {
			supplier = sList.get(0);
		}
		return supplier;
	}
	
	public Integer count() {
		Criteria criteria = getSession().createCriteria(Supplier.class);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}
}
