/**
 * PrepaymentSetWSChangeParamDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.prepayment.PrepaymentSetWSChangeParamDao;
import com.aimir.model.prepayment.PrepaymentSetWSChangeParam;
import com.aimir.util.Condition;

/**
 * PrepaymentSetWSChangeParamDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 27.  v1.0        문동규   Change Emergency Credit parameter 모델 DaoImpl
 *
 */
@Repository(value = "prepaymentSetWSChangeParamDao")
public class PrepaymentSetWSChangeParamDaoImpl 
			extends AbstractJpaDao<PrepaymentSetWSChangeParam, Integer> 
			implements PrepaymentSetWSChangeParamDao {

    public PrepaymentSetWSChangeParamDaoImpl() {
		super(PrepaymentSetWSChangeParam.class);
	}

    @Override
    public Class<PrepaymentSetWSChangeParam> getPersistentClass() {
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
