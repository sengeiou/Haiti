package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Repository;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.BillingDayGMDao;
import com.aimir.dao.mvm.BillingMonthGMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.TariffGMDao;
import com.aimir.model.system.TariffGM;
import com.aimir.model.vo.TariffGMVO;
import com.aimir.util.Condition;

@Repository(value = "tariffgmDao")
public class TariffGMDaoImpl extends AbstractJpaDao<TariffGM, Integer> implements TariffGMDao {
			
	Log logger = LogFactory.getLog(TariffGMDaoImpl.class);
	
	@Autowired
	SeasonDao seasonDao;
	
	@Autowired
	DayGMDao dayGMDao;
	
	@Autowired
	MonthGMDao monthGMDao;
	
	@Autowired
	BillingDayGMDao billingDayGMDao;
	
	@Autowired
	BillingMonthGMDao billingMonthGMDao;
	
	@Autowired
	ContractDao contractDao;
	
    @Autowired
    JpaTransactionManager transactionManager;

	public TariffGMDaoImpl() {
		super(TariffGM.class);
	}

    @Override
    public List<Map<String, Object>> getChargeMgmtList(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TariffGMVO> getCustomerChargeMgmtList(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int updateData(TariffGM tariffGM) throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Double getUsageCharge(Map<String, Object> condition)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getUsageCharges(Map<String, Object> condition)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getUsageChargeByContract(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TariffGM getApplyedTariff(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<TariffGM> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getYyyymmddList(Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }
}
