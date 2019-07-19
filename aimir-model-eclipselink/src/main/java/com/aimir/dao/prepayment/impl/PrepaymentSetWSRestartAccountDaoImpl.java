/**
 * PrepaymentSetWSRestartAccountDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.prepayment.PrepaymentSetWSRestartAccountDao;
import com.aimir.model.prepayment.PrepaymentSetWSRestartAccount;
import com.aimir.util.Condition;

/**
 * PrepaymentSetWSRestartAccountDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 27.  v1.0        문동규   Restart Accounting 모델 DaoImpl
 *
 */
@Repository(value = "prepaymentSetWSRestartAccountDao")
public class PrepaymentSetWSRestartAccountDaoImpl 
			extends AbstractJpaDao<PrepaymentSetWSRestartAccount, Integer> 
			implements PrepaymentSetWSRestartAccountDao {

    public PrepaymentSetWSRestartAccountDaoImpl() {
		super(PrepaymentSetWSRestartAccount.class);
	}

    @Override
    public Class<PrepaymentSetWSRestartAccount> getPersistentClass() {
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
