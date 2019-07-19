/**
 * PrepaymentChangeTariffWSChangeTariffDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.prepayment.PrepaymentChangeTariffWSChangeTariffDao;
import com.aimir.model.prepayment.PrepaymentChangeTariffWSChangeTariff;
import com.aimir.util.Condition;

/**
 * PrepaymentChangeTariffWSChangeTariffDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 28.  v1.0        문동규   선불 요금 변경 모델 DaoImpl
 *
 */
@Repository(value = "prepaymentChangeTariffWSChangeTariffDao")
public class PrepaymentChangeTariffWSChangeTariffDaoImpl 
			extends AbstractJpaDao<PrepaymentChangeTariffWSChangeTariff, Integer> 
			implements PrepaymentChangeTariffWSChangeTariffDao {

    public PrepaymentChangeTariffWSChangeTariffDaoImpl() {
		super(PrepaymentChangeTariffWSChangeTariff.class);
	}

    @Override
    public Class<PrepaymentChangeTariffWSChangeTariff> getPersistentClass() {
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
