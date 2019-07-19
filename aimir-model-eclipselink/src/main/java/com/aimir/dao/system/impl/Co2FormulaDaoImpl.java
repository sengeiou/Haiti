package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.Co2FormulaDao;
import com.aimir.model.system.Co2Formula;
import com.aimir.util.Condition;

@Repository(value="co2formulaDao")
public class Co2FormulaDaoImpl extends AbstractJpaDao<Co2Formula, Integer> implements Co2FormulaDao {

	public Co2FormulaDaoImpl() {
		super(Co2Formula.class);
	}

	public Co2Formula getCo2FormulaBySupplyType(Integer supplyTypeCodeId) {
	    String sql = "select c from Co2Formula c where c.supplyTypeCode.id = :supplyTypeCodeId";
	    Query query = em.createQuery(sql,  Co2Formula.class);
	    query.setParameter("supplyTypeCodeId", supplyTypeCodeId);
	    return (Co2Formula)query.getSingleResult();
	}
	
	/**
	 * 다른 테이블 조인 예)
	 *  DetachedCriteria criteria = DetachedCriteria.forClass(UserLog.class);
	 *  criteria.createAlias("user", "user");
	 *  criteria.setFetchMode("user", FetchMode.JOIN);
	 *  criteria.add(Restrictions.eq("user.userId", userId));        
	 *  List<UserLog> list = getHibernateTemplate().findByCriteria(criteria, 0, 1);
	 */
    public Co2Formula getCo2FormulaBySupplyType(String supplyTypeCode) {
	    String sql = "select c from Co2Formula c where c.supplyTypeCode.code = :supplyTypeCode";
	    Query query = em.createQuery(sql,  Co2Formula.class);
	    query.setParameter("supplyTypeCode", supplyTypeCode);
	    return (Co2Formula)query.getSingleResult();
    }

    @Override
    public Class<Co2Formula> getPersistentClass() {
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
