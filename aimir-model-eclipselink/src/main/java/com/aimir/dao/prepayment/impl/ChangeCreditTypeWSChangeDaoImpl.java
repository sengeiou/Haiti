/**
 * ChangeCreditTypeWSChangeDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.prepayment.ChangeCreditTypeWSChangeDao;
import com.aimir.model.prepayment.ChangeCreditTypeWSChange;
import com.aimir.util.Condition;

/**
 * ChangeCreditTypeWSChangeDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 21.  v1.0        문동규   고객의 과금방식 변경 모델 DaoImpl
 *
 */
@Repository(value = "changeCreditTypeWSChangeDao")
public class ChangeCreditTypeWSChangeDaoImpl 
			extends AbstractJpaDao<ChangeCreditTypeWSChange, Integer> 
			implements ChangeCreditTypeWSChangeDao {

    public ChangeCreditTypeWSChangeDaoImpl() {
		super(ChangeCreditTypeWSChange.class);
	}

    @Override
    public Class<ChangeCreditTypeWSChange> getPersistentClass() {
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
