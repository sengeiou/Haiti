package com.aimir.dao.system.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffType;

@Repository(value = "tarifftypeDao")
	public class TariffTypeDaoImpl extends AbstractHibernateGenericDao<TariffType, Integer> implements TariffTypeDao {
			
    Log logger = LogFactory.getLog(TariffTypeDaoImpl.class);
	    
    @Autowired
    protected TariffTypeDaoImpl(SessionFactory sessionFactory) {
        super(TariffType.class);
        super.setSessionFactory(sessionFactory);
    }
 
	@SuppressWarnings("unchecked")
	public List<TariffType> getTariffTypeBySupplier(String serviceType, Integer supplierId){

		List<TariffType> result = null;
		
    	if(supplierId==null||supplierId==0){
    		Query query = getSession().createQuery("FROM TariffType WHERE serviceTypeCode.code = :serviceType ORDER BY name ASC");
    		query.setString("serviceType", serviceType);
    		result = query.list();
    		
    		/*String query = "FROM TariffType WHERE serviceTypeCode.code = ?  ORDER BY name ASC";
			result = (List<TariffType>)getHibernateTemplate().find(query, new Object[]{serviceType});*/
		}
    	else{
    		Query query = getSession().createQuery("FROM TariffType WHERE serviceTypeCode.code = :serviceType AND supplier.id = :supplierId ORDER BY name ASC");
    		query.setString("serviceType", serviceType);
    		query.setInteger("supplierId", supplierId);
    		result = query.list();
    		
    		/*String query = "FROM TariffType WHERE serviceTypeCode.code = ? AND supplier.id = ? ORDER BY name ASC";
    		result = (List<TariffType>)getHibernateTemplate().find(query, new Object[]{serviceType, supplierId});*/
    	}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<TariffType> getTariffTypeList(Integer supplierId, Integer serviceType) {
    	if(supplierId==null||supplierId==0){
    		Query query = getSession().createQuery("FROM TariffType WHERE serviceTypeCode.id = :serviceType  ORDER BY name ASC");
    		query.setInteger("serviceType", serviceType);
    		
    		return query.list();
			// return (List<TariffType>)getHibernateTemplate().find("FROM TariffType WHERE serviceTypeCode.id = ? ORDER BY name ASC", serviceType);
    	}
    	else{
    		Query query = getSession().createQuery("FROM TariffType WHERE supplier.id = :supplierId and serviceTypeCode.id = :serviceType ORDER BY name ASC");
    		query.setInteger("supplierId", supplierId);
    		query.setInteger("serviceType", serviceType);
    		
    		return query.list();
    		
    		// return (List<TariffType>)getHibernateTemplate().find("FROM TariffType WHERE supplier.id = ? and serviceTypeCode.id = ? ORDER BY name ASC", supplierId, serviceType);
    	}
	}
	
	@SuppressWarnings("unchecked")
	public List<TariffType> getTariffTypeByName(String name) {
		String str = "FROM TariffType WHERE name = :name ORDER BY name ASC";
		Query query = getSession().createQuery(str);
		query.setString("name", name);
		return query.list();
	}
	
	public int updateData(TariffType tariffType) {
		Query query = null;
		StringBuffer sb = new StringBuffer();
		int result = 0;
		Integer id = tariffType.getId();
		
		try {
			sb.append("\nUPDATE TariffType tt SET ");
			if(tariffType.getName() != null) {
				sb.append("\ntt.name = :name,");
			}
			sb.append("\ntt.id=:id");
			sb.append("\nWHERE tt.id=:id ");
			
			query = getSession().createQuery(sb.toString());
			if(tariffType.getName() != null) {
				query.setString("name", tariffType.getName());
			}
			query.setInteger("id", id);
			
			result = query.executeUpdate();
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e,e);
		}
		return result;

	}
	
	public Integer getLastCode() {
		Query query = null;
		StringBuffer sb = new StringBuffer();
		Integer code = null;
		try {
			sb.append("\nSELECT MAX(tt.code)");
			sb.append("\nFROM TariffType tt");
			
			query = getSession().createQuery(sb.toString());
			code = (Integer)query.uniqueResult();
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e,e);
		}
		return code;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public TariffType addTariffType(String tariffTypeName, Code serviceType, Supplier supplier) {
		TariffType tariffType = null;
		
		Criteria criteria = getSession().createCriteria(TariffType.class);
		criteria.add(Restrictions.eq("name", tariffTypeName));
		if ( supplier != null ) {
			criteria.add(Restrictions.eq("supplier", supplier));
		}
		if ( serviceType != null ) {
			criteria.add(Restrictions.eq("serviceTypeCode", serviceType));
		}
		List<TariffType> list = criteria.list();
		
		if ( list != null && list.size() > 0 ) {
			tariffType = list.get(0);
		} else {
			tariffType = new TariffType();
			tariffType.setCode(getLastCode() + 1);
			tariffType.setName(tariffTypeName);
			tariffType.setSupplier(supplier);
			tariffType.setServiceTypeCode(serviceType);
			add(tariffType);
		}
		
		return tariffType;
	}
}
