package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.model.system.DeviceModel;
import com.aimir.util.StringUtil;

@Repository(value="devicemodelDao")
public class DeviceModelDaoImpl extends AbstractHibernateGenericDao<DeviceModel, Integer> implements DeviceModelDao {

	@Autowired
	protected DeviceModelDaoImpl(SessionFactory sessionFactory) {
		super(DeviceModel.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<DeviceModel> getDeviceModels() {
		Query query = getSession().createQuery("from DeviceModel");
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<DeviceModel> getDeviceModels(Integer vendorId) {
		Query query = getSession().createQuery("from DeviceModel d where d.deviceVendor.id = " + vendorId + " order by upper(name) asc");
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<DeviceModel> getDeviceModels(String vendorName) {
		Query query = getSession().createQuery("from DeviceModel d where d.deviceVendor.name = :name ");
		query.setString("name", vendorName);
		return query.list();
		
	}

	@SuppressWarnings("unchecked")
	public List<DeviceModel> getDeviceModels(int vendorId, int deviceTypeId) {
		Query query = getSession().createQuery("from DeviceModel d where d.deviceVendor.id = :vendorId  and d.deviceType.id = :deviceTypeId ");
		query.setInteger("vendorId", vendorId);
		query.setInteger("deviceTypeId", deviceTypeId);
		return query.list();
		
	}
	
	@SuppressWarnings("unchecked")
	public List<DeviceModel> getDeviceModels(Map<String, Object> condition) {
		int vendorId	         = Integer.parseInt(condition.get("vendorId").toString());
		String subDeviceType	 = StringUtil.nullToBlank(condition.get("subDeviceType"));
		
		Query query = getSession().createQuery("from DeviceModel d where d.deviceVendor.id = :vendorId and d.deviceType.id in ( :deviceTypeId ) order by d.name asc");
		query.setInteger("vendorId", vendorId);
		query.setString("deviceTypeId", subDeviceType);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<DeviceModel> getDeviceModelByName(Integer supplierId, String name) {
		Query query = getSession().createQuery("select d from DeviceModel d where d.name = ?");
		query.setString(0, name);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<DeviceModel> getDeviceModelBySupplierId(Integer supplierId) {
		Query query = getSession().createQuery("select d from DeviceModel d inner join d.deviceVendor v with v.supplier.id = ?");
		query.setInteger(0, supplierId);
		
		return query.list();
	}
	
/*
	public List<DeviceModel> getDeviceModelsBySupplier(Integer supplier_id) {
		
		return getHibernateTemplate().find("from DeviceModel d where d.supplier.id = ? order by d.deviceType.name, d.deviceVendor.name", supplier_id);
	}
	
	public List<DeviceModel> getDevicesBySupplier(Supplier supplier) {
		
		return getHibernateTemplate().find("from DeviceModel d where d.supplier = ?", supplier);
	}

	public List<DeviceModel> getDevicesByType(Code deviceType) {
		return getHibernateTemplate().find("from DeviceModel d where d.deviceType = ?", deviceType);
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getDeviceTree(final Integer supplierId) {
		
		return (List<Object[]>)getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session s) throws HibernateException, SQLException {
				return s.createQuery("select m.deviceType.name, m.deviceType.id, " +
											"m.deviceVendor.name, m.deviceVendor.id, " +
											"m.name, m.id, m.supplier.id " +
									 "from DeviceModel m " +
									 "where m.supplier.id = :supplierId " +
									 "order by m.deviceType.id, m.deviceVendor.id ")
									 .setInteger("supplierId", supplierId)
									 .list();
			}
			
		});
	}
*/

	@SuppressWarnings("unchecked")
	public DeviceModel getDeviceModelByCode(Integer supplierId, Integer code, Integer deviceTypeCodeId) {
		
		Query query = getSession().createQuery("select d from DeviceModel d where d.code = ? and d.deviceType.id = ?");
		query.setInteger(0, code);
		query.setInteger(1, deviceTypeCodeId);
		DeviceModel deviceModel = new DeviceModel();
		if (query.list().size() > 0) {
			deviceModel = (DeviceModel)(query.list()).get(0);
		}
		return deviceModel;
	}
	
	@SuppressWarnings("unchecked")
	public List<DeviceModel> getDeviceModelByTypeId(Integer supplierId, Integer typeId) {
		Query query = getSession().createQuery("select d from DeviceModel d inner join d.deviceVendor v where d.deviceType.id = ? order by v.name asc");
		query.setInteger(0, typeId);
		return query.list();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<DeviceModel> getDeviceModelByTypeIdUnknown(final Integer supplierId) {
		Query query = getSession().createQuery("select d from DeviceModel d where d.deviceType.code not like '1%'");
		return query.list();
	}
	/*@SuppressWarnings("unchecked")
	public List<DeviceModel> getDeviceModelByTypeIdUnknown(final Integer supplierId) {
		return (List<DeviceModel>) getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session s) throws HibernateException, SQLException {
				List<DeviceModel> resultList = new ArrayList<DeviceModel>();
				StringBuffer sb = new StringBuffer();
				sb.append("select d.*,v.name from DeviceModel d where d.type_id is null");
				sb.append(" UNION ALL ");
				sb.append(" select d.* from DeviceModel d,DeviceVendor v,Code c");
				sb.append(" where d.deviceVendor_id = v.id and v.supplier_id = ?");
				sb.append(" and d.type_id=c.id and c.code not like '1%' ");
				List result = s.createSQLQuery(sb.toString()).setInteger(0, supplierId).list();
				
				Object[] resultData = null;		
					
				for(int i = 0, size = result.size() ; i < size ; i++) {
					DeviceModel devicemodel = new DeviceModel();
					resultData = (Object[])result.get(i);
					String name = resultData[3]+"";
					String id = resultData[0]+"";
					devicemodel.setName(name.trim());
					devicemodel.setId(Integer.parseInt(id));
					
					resultList.add(devicemodel);
				}
				 return resultList;
			}			
		});
	}*/

	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getMCUDeviceModel(String inCondition) {

//		return (List<DeviceModel>) getHibernateTemplate().execute(new HibernateCallback() {
//			public Object doInHibernate(Session s) throws HibernateException, SQLException {
//				return s.createQuery("select d from DeviceModel d where deviceType.id in " + inCondition).list();				
//			}
//		});
		
		StringBuffer sb = new StringBuffer()
		.append(" select d.id as id, d.name as modelName, v.name as vendorName from DeviceModel d inner join d.deviceVendor v ")
		.append("  where d.deviceType.id in " + inCondition)
		.append("  order by vendorName, modelName");
		
		Query query = getSession().createQuery(sb.toString());
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
}
