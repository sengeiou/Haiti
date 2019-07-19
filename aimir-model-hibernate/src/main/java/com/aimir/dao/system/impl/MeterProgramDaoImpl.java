package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.MeterProgramKind;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.MeterProgramDao;
import com.aimir.model.system.MeterProgram;

@Repository(value = "meterProgramDao")
public class MeterProgramDaoImpl extends
		AbstractHibernateGenericDao<MeterProgram, Integer> implements
		MeterProgramDao {

	@Autowired
	protected MeterProgramDaoImpl(SessionFactory sessionFactory) {
		super(MeterProgram.class);
		super.setSessionFactory(sessionFactory);
	}

	public MeterProgram getMeterConfigId(int meterconfig_id) {
		Criteria crit = getSession().createCriteria(MeterProgram.class).add(
				Restrictions.eq("meterConfigId", meterconfig_id)).addOrder(
				Order.desc("id"));

		List<MeterProgram> list = crit.list();

		if (list.size() > 0)
			return list.get(0);
		else
			return null;

	}

	@Override
	public MeterProgram getMeterConfigId(int meterconfig_id,
			MeterProgramKind kind) {
		Criteria crit = getSession().createCriteria(MeterProgram.class).
				add(Restrictions.eq("meterConfigId", meterconfig_id))
				.add(Restrictions.eq("kind", kind))
				.addOrder(Order.desc("id"));

		List<MeterProgram> list = crit.list();

		if (list.size() > 0)
			return list.get(0);
		else
			return null;
	}

    /**
     * method name : getMeterProgramSettingsData<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Settings 값을 조회한다.
     *
     * @param conditionMap
     * @param isCount total count 여부
     * @return
     */
    public String getMeterProgramSettingsData(Map<String, Object> conditionMap) {
        String result;
        Integer meterProgramId = (Integer)conditionMap.get("meterProgramId");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT mp.settings AS settings ");
        sb.append("\nFROM MeterProgram mp ");
        sb.append("\nWHERE mp.id = :meterProgramId ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("meterProgramId", meterProgramId);

        result = (String)query.uniqueResult();

        return result;
    }
}