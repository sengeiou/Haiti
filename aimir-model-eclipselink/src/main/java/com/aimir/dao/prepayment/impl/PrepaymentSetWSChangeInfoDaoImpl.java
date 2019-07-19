/**
 * PrepaymentSetWSChangeInfoDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.prepayment.PrepaymentSetWSChangeInfoDao;
import com.aimir.model.prepayment.PrepaymentSetWSChangeInfo;
import com.aimir.util.Condition;

/**
 * PrepaymentSetWSChangeInfoDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 8. 4.   v1.0        문동규   Change Energy Utility Information 모델 DaoImpl
 *
 */
@Repository(value = "prepaymentSetWSChangeInfoDao")
public class PrepaymentSetWSChangeInfoDaoImpl 
			extends AbstractJpaDao<PrepaymentSetWSChangeInfo, Integer> 
			implements PrepaymentSetWSChangeInfoDao {

    public PrepaymentSetWSChangeInfoDaoImpl() {
		super(PrepaymentSetWSChangeInfo.class);
	}

    @Override
    public Class<PrepaymentSetWSChangeInfo> getPersistentClass() {
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
