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

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.view.DayGMViewDao;
import com.aimir.model.mvm.DayGM;
import com.aimir.model.view.DayGMView;
import com.aimir.util.Condition;

@Repository(value="daygmDaoView")
public class DayGMViewDaoImpl extends AbstractHibernateGenericDao<DayGMView, Integer> implements DayGMViewDao {
	private static Log log = LogFactory.getLog(DayGMViewDaoImpl.class);

    @Autowired
    protected DayGMViewDaoImpl(SessionFactory sessionFactory) {
        super(DayGMView.class);
        super.setSessionFactory(sessionFactory);
    }

	@Override
	public List<DayGMView> getDayGMsByListCondition(Set<Condition> list) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DayGMView> getDayCustomerBillingGridData(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}
 
	@Override
	public DayGMView getDayGMbySupplierId(Map<String, Object> params) {
		String yyyymmdd = (String)params.get("yyyymmdd");  
		Integer channel = (Integer)params.get("channel");
		Integer dst = (Integer)params.get("dst");
		DeviceType mdevType = (DeviceType)params.get("mdevType");  
		String mdevId = (String)params.get("mdevId");
		int supplierId = Integer.parseInt(params.get("supplierId").toString());
		
		StringBuilder sb = new StringBuilder()
		.append("")
		.append("   FROM DayGMView d                   ")
		.append("  WHERE d.id.channel = :channel                            ")
		.append("    AND d.id.mdevId = :mdevId ")
		.append("    AND d.id.dst = :dst ")
		.append("    AND d.id.yyyymmdd = :yyyymmdd ")
		.append("    AND d.id.mdevType = :mdevType ")
		.append("    AND d.contract.customer.supplier.id = :supplierId                ");	
		
		Query query = getSession().createQuery(sb.toString());
		query.setInteger("channel", channel);
		query.setString("mdevId", mdevId);
		query.setInteger("dst", dst);
		query.setString("yyyymmdd", yyyymmdd);
		query.setString("mdevType", mdevType.toString());
		query.setInteger("supplierId", supplierId);
		
		List<DayGMView> list = query.list();
		
		return list != null && list.size()>0 ? list.get(0) : null;
	}
	
}
