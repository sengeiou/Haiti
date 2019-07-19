package com.aimir.dao.system.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.DeviceVendorDao;
import com.aimir.model.system.DeviceVendor;

@Repository(value = "devicevendorDao")
public class DeviceVendorDaoImpl extends AbstractHibernateGenericDao<DeviceVendor, Integer> implements DeviceVendorDao{

    @Autowired
    protected DeviceVendorDaoImpl(SessionFactory sessionFactory) {
        super(DeviceVendor.class);
        super.setSessionFactory(sessionFactory);
    }

    @SuppressWarnings("unchecked")
    public List<DeviceVendor> getDeviceVendorsBySupplierId(Integer supplierId) {
    	Query query = getSession().createQuery("from DeviceVendor order by upper(name) asc");
        return query.list();
        //return getHibernateTemplate().findByNamedParam("select v.* from DeviceModel as m inner join m.deviceType c right outer join m.deviceVendor as v with v.supplier.id = :supplierId", "supplierId", supplierId);
        
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getDeviceVendorsForTree(Integer supplierId) {
    	Query query = getSession().createQuery("select m.deviceType.name, m.deviceType.id, " +
                "v.name, v.id, " +
                "m.name, m.id " +
         "from DeviceVendor as v left outer join DeviceModel as m " +
         "where v.id = m.deviceVendor.id " +
         "order by m.deviceType.id, v.id ");
    	
    	return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<DeviceVendor> getDeviceVendorByName(final Integer supplierId, final String name) {
    	
    	Query query = getSession().createQuery("select v from DeviceVendor v " +
                " where v.name like ?");
    	query.setString(0, name+'%');
    	return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<DeviceVendor> getDeviceVendorByCode(final Integer supplierId, final Integer code) {
    	Query query = getSession().createQuery("from DeviceVendor v " +
                " where v.code = ?");
    	query.setInteger(0, code);
    	return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<DeviceVendor> getDeviceVendorsOrderByName() {
    	Query query = getSession().createQuery("from DeviceVendor order by upper(name) asc");
        return query.list();
        //return getHibernateTemplate().findByNamedParam("select v.* from DeviceModel as m inner join m.deviceType c right outer join m.deviceVendor as v with v.supplier.id = :supplierId", "supplierId", supplierId);
        
    }

}
