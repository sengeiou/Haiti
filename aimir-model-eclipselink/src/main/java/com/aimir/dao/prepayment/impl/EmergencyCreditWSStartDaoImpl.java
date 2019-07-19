/**
 * EmergencyCreditWSStartDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.prepayment.EmergencyCreditWSStartDao;
import com.aimir.model.prepayment.EmergencyCreditWSStart;
import com.aimir.util.Condition;

/**
 * EmergencyCreditWSStartDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 25.  v1.0        문동규   Emergency Credit 모델 DaoImpl
 *
 */
@Repository(value = "emergencyCreditWSStartDao")
public class EmergencyCreditWSStartDaoImpl 
			extends AbstractJpaDao<EmergencyCreditWSStart, Integer> 
			implements EmergencyCreditWSStartDao {

    public EmergencyCreditWSStartDaoImpl() {
		super(EmergencyCreditWSStart.class);
	}

    @Override
    public Class<EmergencyCreditWSStart> getPersistentClass() {
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
