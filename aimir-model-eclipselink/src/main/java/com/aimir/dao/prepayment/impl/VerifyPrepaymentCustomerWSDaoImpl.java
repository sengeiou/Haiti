package com.aimir.dao.prepayment.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.prepayment.VerifyPrepaymentCustomerWSDao;
import com.aimir.model.prepayment.VerifyPrepaymentCustomerWS;
import com.aimir.util.Condition;

@Repository(value = "verifyPrepaymentCustomerWSDao")
public class VerifyPrepaymentCustomerWSDaoImpl 
				extends AbstractJpaDao<VerifyPrepaymentCustomerWS, Integer> 
				implements VerifyPrepaymentCustomerWSDao {

    public VerifyPrepaymentCustomerWSDaoImpl() {
		super(VerifyPrepaymentCustomerWS.class);
	}

    @Override
    public Class<VerifyPrepaymentCustomerWS> getPersistentClass() {
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
