/**
 * BillingMonthWMDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.BillingMonthWMDao;
import com.aimir.model.mvm.BillingMonthWM;
import com.aimir.model.system.Contract;
import com.aimir.util.Condition;

/**
 * BillingMonthWMDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 17.   v1.0       김상연         BillingMonthWM 조회 - 조건 (BillingMonthWM)
 * 2011. 5. 17.   v1.1       김상연         계약 회사 평균 조회
 * 2011. 6. 09.   v1.2       김상연         해당 계약 최고 사용 금액 조회
 * 2011. 6. 27.   v1.3       김상연        사용 평균 비용 조회
 *
 */
@Repository(value = "billingmonthwmDao")
public class BillingMonthWMDaoImpl extends AbstractJpaDao<BillingMonthWM, Integer> implements BillingMonthWMDao {

	@SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(BillingMonthWMDaoImpl.class);
    
	public BillingMonthWMDaoImpl() {
		super(BillingMonthWM.class);
	}

    @Override
    public Double getAverageUsage(BillingMonthWM billingMonthWM) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<BillingMonthWM> getBillingMonthWMs(
            BillingMonthWM billingMonthWM, String startDay, String finishDay) {
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
    public List<Object> getBillingMonthWMsAvg(BillingMonthWM billingMonthWM,
            String fromDay, String toDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<BillingMonthWM> getPersistentClass() {
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