package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Supplier;
import com.aimir.util.Condition;

@Repository(value="supplierDao")
public class SupplierDaoImpl extends AbstractJpaDao<Supplier, Integer> implements SupplierDao{
    
	public SupplierDaoImpl() {
		super(Supplier.class);
	}

	public Supplier getSupplierByName(String supplierName) {
	    String sql = "select s from Supplier s where s.name = :name";
	    Query query = em.createQuery(sql, Supplier.class);
	    query.setParameter("name", supplierName);
	    return (Supplier)query.getSingleResult();
	}

	public Supplier getSupplierById(int supplierId) {
	    String sql = "select s from Supplier s where s.id = :id";
	    Query query = em.createQuery(sql, Supplier.class);
	    query.setParameter("id", supplierId);
	    return (Supplier)query.getSingleResult();
	}
	
	public Integer count() {
	    String sql = "select count(s) from Supplier s";
	    Query query = em.createQuery(sql, Integer.class);
	    return (Integer)query.getSingleResult();
	}

    @Override
    public Class<Supplier> getPersistentClass() {
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
