/**
 * CheckBalanceSettingWSModifyDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.prepayment.CheckBalanceSettingWSModifyDao;
import com.aimir.model.prepayment.CheckBalanceSettingWSModify;
import com.aimir.util.Condition;

/**
 * CheckBalanceSettingWSModifyDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 22.  v1.0        문동규   잔액 체크 주기 설정 모델 DaoImpl
 *
 */
@Repository(value = "checkBalanceSettingWSModifyDao")
public class CheckBalanceSettingWSModifyDaoImpl 
			extends AbstractJpaDao<CheckBalanceSettingWSModify, Integer> 
			implements CheckBalanceSettingWSModifyDao {

    public CheckBalanceSettingWSModifyDaoImpl() {
		super(CheckBalanceSettingWSModify.class);
	}

    @Override
    public Class<CheckBalanceSettingWSModify> getPersistentClass() {
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
