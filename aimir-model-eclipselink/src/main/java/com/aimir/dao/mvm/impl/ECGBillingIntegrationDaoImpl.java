package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.ECGBillingIntegrationDao;
import com.aimir.model.mvm.ECGBillingIntegration;
import com.aimir.util.Condition;

@Repository(value = "ecgBillingIntegrationDao")
public class ECGBillingIntegrationDaoImpl extends AbstractJpaDao<ECGBillingIntegration, Integer>
implements ECGBillingIntegrationDao {

    public ECGBillingIntegrationDaoImpl() {
		super(ECGBillingIntegration.class);
	}

    @Override
    public Class<ECGBillingIntegration> getPersistentClass() {
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
