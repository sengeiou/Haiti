/**
 * SupplyControlWSInterruptDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.prepayment.SupplyControlWSInterruptDao;
import com.aimir.model.prepayment.SupplyControlWSInterrupt;
import com.aimir.util.Condition;

/**
 * SupplyControlWSInterruptDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 20.  v1.0        문동규   공급 차단 모델 DaoImpl
 *
 */
@Repository(value = "supplyControlWSInterruptDao")
public class SupplyControlWSInterruptDaoImpl 
			extends AbstractJpaDao<SupplyControlWSInterrupt, Integer> 
			implements SupplyControlWSInterruptDao {

    public SupplyControlWSInterruptDaoImpl() {
		super(SupplyControlWSInterrupt.class);
	}

    @Override
    public Class<SupplyControlWSInterrupt> getPersistentClass() {
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
