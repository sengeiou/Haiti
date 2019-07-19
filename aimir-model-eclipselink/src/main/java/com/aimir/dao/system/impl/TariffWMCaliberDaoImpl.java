package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.TariffWMCaliberDao;
import com.aimir.model.system.TariffWMCaliber;
import com.aimir.util.Condition;

@Repository(value = "tariffwmcaliberDao")
public class TariffWMCaliberDaoImpl extends AbstractJpaDao<TariffWMCaliber, Integer> implements TariffWMCaliberDao {
			
	Log log = LogFactory.getLog(TariffWMCaliberDaoImpl.class);
	    
	public TariffWMCaliberDaoImpl() {
		super(TariffWMCaliber.class);
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

		Query query = em.createQuery(sb.toString(), TariffWMCaliber.class);
		query.setParameter("supplierId", supplierId);

		if( yyyymmdd.length() > 0){
			query.setParameter("yyyymmdd", yyyymmdd + "000000");
		}

		List<Map<String, Object>> returns = new ArrayList<Map<String, Object>>();
		List<TariffWMCaliber> result = query.getResultList();
		try {
    		for (TariffWMCaliber t : result) {
    		    Map<String, Object> r = new HashMap<String, Object>();
    		    BeanUtils.populate(t, r);
    		    returns.add(r);
    		}
		}
		catch (Exception e) {
		    log.warn(e, e);
		}
		return returns;

	}
	
	public int updateData(TariffWMCaliber tariff) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE TariffWMCaliber t ");
		sb.append("SET t.basicRate = :basicRate, ");
		sb.append("    t.basicRateHot = :basicRateHot ");
		sb.append("WHERE t.id = :id ");					
	
		Query query = em.createQuery(sb.toString());
		query.setParameter("basicRate", tariff.getBasicRate());
		query.setParameter("basicRateHot",  tariff.getBasicRateHot());
		query.setParameter("id", tariff.getId());
		
		return query.executeUpdate();
	}
	
	public TariffWMCaliber getTariffWMCaliberByCaliber(Map<String,Object> params){
		Double caliber = (Double)params.get("caliber");
		int supplierId = (Integer)params.get("supplierId");
		
		String sql = "select t from TariffWM where caliber = :caliber and supplier.id = :supplierId";
		
		Query query = em.createQuery(sql, TariffWMCaliber.class);
		query.setParameter("caliber",  caliber);
		query.setParameter("supplierId", supplierId);
		
		return (TariffWMCaliber)query.getSingleResult();
	}

    @Override
    public Class<TariffWMCaliber> getPersistentClass() {
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