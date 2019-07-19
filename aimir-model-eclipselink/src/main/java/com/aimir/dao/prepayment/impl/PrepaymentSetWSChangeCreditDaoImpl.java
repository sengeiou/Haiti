/**
 * PrepaymentSetWSChangeCreditDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.prepayment.PrepaymentSetWSChangeCreditDao;
import com.aimir.model.prepayment.PrepaymentSetWSChangeCredit;
import com.aimir.util.Condition;

/**
 * PrepaymentSetWSChangeCreditDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 27.  v1.0        문동규   Change Credit available 모델 DaoImpl
 *
 */
@Repository(value = "prepaymentSetWSChangeCreditDao")
public class PrepaymentSetWSChangeCreditDaoImpl 
			extends AbstractJpaDao<PrepaymentSetWSChangeCredit, Integer> 
			implements PrepaymentSetWSChangeCreditDao {

    public PrepaymentSetWSChangeCreditDaoImpl() {
		super(PrepaymentSetWSChangeCredit.class);
	}

    @Override
    public Class<PrepaymentSetWSChangeCredit> getPersistentClass() {
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
