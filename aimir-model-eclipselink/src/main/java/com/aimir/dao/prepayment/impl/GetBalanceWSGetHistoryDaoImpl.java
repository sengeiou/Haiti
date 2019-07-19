/**
 * GetBalanceWSGetHistoryDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.prepayment.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.prepayment.GetBalanceWSGetHistoryDao;
import com.aimir.model.prepayment.GetBalanceWSGetHistory;
import com.aimir.util.Condition;

/**
 * GetBalanceWSGetHistoryDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 27.  v1.0        문동규   잔액 충전 내역 조회 모델 DaoImpl
 *
 */
@Repository(value = "getBalanceWSGetHistoryDao")
public class GetBalanceWSGetHistoryDaoImpl 
			extends AbstractJpaDao<GetBalanceWSGetHistory, Integer> 
			implements GetBalanceWSGetHistoryDao {

    public GetBalanceWSGetHistoryDaoImpl() {
		super(GetBalanceWSGetHistory.class);
	}

    @Override
    public Class<GetBalanceWSGetHistory> getPersistentClass() {
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
