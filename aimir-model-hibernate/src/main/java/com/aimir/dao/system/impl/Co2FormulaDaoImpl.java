package com.aimir.dao.system.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.Co2FormulaDao;
import com.aimir.model.system.Co2Formula;

@Repository(value="co2formulaDao")
public class Co2FormulaDaoImpl extends AbstractHibernateGenericDao<Co2Formula, Integer> implements Co2FormulaDao {

	@Autowired
	protected Co2FormulaDaoImpl(SessionFactory sessionFactory) {
		super(Co2Formula.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public Co2Formula getCo2FormulaBySupplyType(Integer supplyTypeCodeId) {
		Criteria criteria = getSession().createCriteria(Co2Formula.class);
		criteria.add(Restrictions.eq("supplyTypeCode.id", supplyTypeCodeId));
		
		List<Co2Formula> list = criteria.list();
		Co2Formula co2formula = null;
		if (list.size() > 0) {
			co2formula = list.get(0);
		}
		return co2formula;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * 다른 테이블 조인 예)
	 *  DetachedCriteria criteria = DetachedCriteria.forClass(UserLog.class);
	 *  criteria.createAlias("user", "user");
	 *  criteria.setFetchMode("user", FetchMode.JOIN);
	 *  criteria.add(Restrictions.eq("user.userId", userId));        
	 *  List<UserLog> list = getHibernateTemplate().findByCriteria(criteria, 0, 1);
	 */
    public Co2Formula getCo2FormulaBySupplyType(String supplyTypeCode) {
        Criteria criteria = getSession().createCriteria(Co2Formula.class);        
        criteria.createAlias("supplyTypeCode", "sp");
        criteria.add(Restrictions.eq("sp.code", supplyTypeCode));
        
        List<Co2Formula> list = criteria.list();
        Co2Formula co2formula = null;
        if (list.size() > 0) {
            co2formula = list.get(0);
        }
        return co2formula;
    }

}
