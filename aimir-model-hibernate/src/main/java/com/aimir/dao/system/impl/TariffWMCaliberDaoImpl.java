package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.TariffWMCaliberDao;
import com.aimir.model.system.TariffWMCaliber;

@Repository(value = "tariffwmcaliberDao")
public class TariffWMCaliberDaoImpl extends AbstractHibernateGenericDao<TariffWMCaliber, Integer> implements TariffWMCaliberDao {
			
	Log logger = LogFactory.getLog(TariffWMCaliberDaoImpl.class);
	    
	@Autowired
	protected TariffWMCaliberDaoImpl(SessionFactory sessionFactory) {
		super(TariffWMCaliber.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getChargeMgmtList(Map<String, Object> condition){
	
		int supplierId = (Integer)condition.get("supplierId");
		String yyyymmdd = (String)condition.get("yyyymmdd");

		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT	t.id as id, ");
		sb.append("\n       	t.caliber as caliber, ");
		sb.append("\n       	t.basicRate as basicRate, ");
		sb.append("\n       	t.basicRateHot as basicRateHot, ");
		sb.append("\n       	t.writeTime as yyyymmdd ");
		sb.append("\n FROM      TariffWMCaliber t");
		sb.append("\n WHERE     t.supplier.id = :supplierId ");
		if(yyyymmdd.length() > 0){
			sb.append("\n AND       t.writeTime = :yyyymmdd ");
		}
		sb.append("\n ORDER BY t.writeTime ");

		Query query = getSession().createQuery(sb.toString())
								  .setInteger("supplierId", supplierId);

		if( yyyymmdd.length() > 0){
			query.setString("yyyymmdd", yyyymmdd + "000000");
		}

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

	}
	
	public int updateData(TariffWMCaliber tariff) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE TariffWMCaliber t ");
		sb.append("SET t.basicRate = ?, ");
		sb.append("    t.basicRateHot = ? ");
		sb.append("WHERE t.id = ? ");					
	
		//HQL문을 이용한 CUD를 할 경우에는 getHibernateTemplate().bulkUpdate() 메소드를 사용한다.		
		Query query = getSession().createQuery(sb.toString());
		query.setParameter(1, tariff.getBasicRate());
		query.setParameter(2, tariff.getBasicRateHot());
		query.setParameter(3, tariff.getId());
		return query.executeUpdate();
		// return this.getHibernateTemplate().bulkUpdate(sb.toString(), new Object[] { tariff.getBasicRate(), tariff.getBasicRateHot(), tariff.getId() } );
	}
	
	@SuppressWarnings("unchecked")
	public TariffWMCaliber getTariffWMCaliberByCaliber(Map<String,Object> params){
		Double caliber = (Double)params.get("caliber");
		int supplierId = (Integer)params.get("supplierId");
		
		Criteria criteria = getSession().createCriteria(TariffWMCaliber.class);
		criteria.add(Restrictions.eq("caliber", caliber));
		criteria.add(Restrictions.eq("supplier.id", supplierId));
		
		List<TariffWMCaliber> tariffWMCaliberList = criteria.list();
			
		return tariffWMCaliberList!=null&&tariffWMCaliberList.size()>0?(TariffWMCaliber)tariffWMCaliberList.get(0):null;
	}
}