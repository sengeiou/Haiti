/**
 * GetBalanceWSGetInfoDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.prepayment.GetBalanceWSGetInfoDao;
import com.aimir.model.prepayment.GetBalanceWSGetInfo;
import com.aimir.util.Condition;

/**
 * GetBalanceWSGetInfoDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 22.  v1.0        문동규   현재 잔액 정보 요청 모델 DaoImpl
 *
 */
@Repository(value = "getBalanceWSGetInfoDao")
public class GetBalanceWSGetInfoDaoImpl 
			extends AbstractJpaDao<GetBalanceWSGetInfo, Integer> 
			implements GetBalanceWSGetInfoDao {

    public GetBalanceWSGetInfoDaoImpl() {
		super(GetBalanceWSGetInfo.class);
	}

    @Override
    public Class<GetBalanceWSGetInfo> getPersistentClass() {
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
