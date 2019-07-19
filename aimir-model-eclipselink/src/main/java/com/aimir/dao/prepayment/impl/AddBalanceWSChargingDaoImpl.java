/**
 * AddBalanceWSChargingDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.prepayment.AddBalanceWSChargingDao;
import com.aimir.model.prepayment.AddBalanceWSCharging;
import com.aimir.util.Condition;

/**
 * AddBalanceWSChargingDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 6. 28   v1.0        김상연   과금 충전 모델 DaoImpl
 * 2011. 8. 12   v1.0        문동규   클래스명 수정
 *
 */
@Repository(value = "addBalanceWSChargingDao")
public class AddBalanceWSChargingDaoImpl 
			extends AbstractJpaDao<AddBalanceWSCharging, Integer> 
			implements AddBalanceWSChargingDao {

    public AddBalanceWSChargingDaoImpl() {
		super(AddBalanceWSCharging.class);
	}

    @Override
    public Class<AddBalanceWSCharging> getPersistentClass() {
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
