package com.aimir.dao.view.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.view.MonthGMViewDao;
import com.aimir.model.view.MonthGMView;
import com.aimir.util.Condition;
import com.aimir.util.SQLWrapper;

@Repository(value="monthgmDaoView")
@Transactional
public class MonthGMViewDaoImpl extends AbstractHibernateGenericDao<MonthGMView, Integer> implements MonthGMViewDao {
	private static Log log = LogFactory.getLog(MonthGMViewDaoImpl.class);

	@Autowired
	protected MonthGMViewDaoImpl(SessionFactory sessionFactory) {
		super(MonthGMView.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@Override
	public List<MonthGMView> getMonthGMsByListCondition(Set<Condition> list) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MonthGMView> getMonthCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}
    
	@Override
	@SuppressWarnings("unchecked")
	public MonthGMView getMonthGMbySupplierId(Map<String, Object> params) {
		String yyyymm = (String)params.get("yyyymm");  
		Integer channel = (Integer)params.get("channel");
		Integer dst = (Integer)params.get("dst");
		DeviceType mdevType = (DeviceType)params.get("mdevType");  
		String mdevId = (String)params.get("mdevId");
		int supplierId = (Integer)params.get("supplierId");
		
		StringBuffer sb = new StringBuffer();

		sb.append("\n  ");
		sb.append("\n FROM MonthGMView m");
		sb.append("\n WHERE m.id.mdevId = :mdevId");
		sb.append("\n AND m.id.mdevType = :mdevType");
		sb.append("\n AND m.id.dst = :dst");
		sb.append("\n AND m.id.yyyymm = :yyyymm");
		sb.append("\n AND m.id.channel = :channel");
		sb.append("\n AND m.contract.customer.supplier.id = :supplierId");
		
		
		Query query = getSession().createQuery(new SQLWrapper().getQuery(sb.toString()));
		query.setString("mdevId", mdevId);
		query.setString("mdevType", mdevType.toString());
		query.setInteger("dst", dst);
		query.setString("yyyymm", yyyymm);
		query.setInteger("channel", channel);
		query.setInteger("supplierId", supplierId);
		
		List<MonthGMView> list = query.list();
		
		return list != null && list.size() >0 ? list.get(0) : null;
	}

}
