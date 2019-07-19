/**
 * SupplyControlWSRearmDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.prepayment.SupplyControlWSRearmDao;
import com.aimir.model.prepayment.SupplyControlWSRearm;
import com.aimir.util.Condition;

/**
 * SupplyControlWSRearmDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 20.  v1.0        문동규   공급 재개 모델 DaoImpl
 *
 */
@Repository(value = "supplyControlWSRearmDao")
public class SupplyControlWSRearmDaoImpl 
			extends AbstractJpaDao<SupplyControlWSRearm, Integer> 
			implements SupplyControlWSRearmDao {

    public SupplyControlWSRearmDaoImpl() {
		super(SupplyControlWSRearm.class);
	}

    @Override
    public Class<SupplyControlWSRearm> getPersistentClass() {
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
