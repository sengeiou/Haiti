/**
 * BillingMonthGMDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.BillingMonthGMDao;
import com.aimir.model.mvm.BillingMonthGM;
import com.aimir.model.system.Contract;
import com.aimir.util.Condition;

/**
 * BillingMonthGMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 17.   v1.0       김상연         BillingMonthGM 조회 - 조건 (BillingMonthGM)
 * 2011. 5. 17.   v1.1       김상연         계약 회사 평균 조회
 * 2011. 6. 09.   v1.2       김상연         해당 계약 최고 사용 금액 조회
 * 2011. 6. 27.   v1.3       김상연        사용 평균 비용 조회
 *
 */
@Repository(value = "billingmonthgmDao")
public class BillingMonthGMDaoImpl extends AbstractJpaDao<BillingMonthGM, Integer> implements BillingMonthGMDao {

	@SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(BillingMonthGMDaoImpl.class);
    
	public BillingMonthGMDaoImpl() {
		super(BillingMonthGM.class);
	}

    @Override
    public List<BillingMonthGM> getBillingMonthGMs(
            BillingMonthGM billingMonthGM, String startDay, String finishDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getAverageUsage(BillingMonthGM billingMonthGM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getMaxBill(Contract contract, String yyyymmdd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getAverageBill(Contract contract, String yyyymmdd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getBillingMonthGMsAvg(BillingMonthGM billingMonthGM,
            String fromDay, String toDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<BillingMonthGM> getPersistentClass() {
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