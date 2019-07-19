package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplyTypeDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.system.Contract;
import com.aimir.model.system.TariffEM;
import com.aimir.model.vo.TariffEMVO;
import com.aimir.util.Condition;

@Repository(value = "tariffemDao")
@Transactional
public class TariffEMDaoImpl extends AbstractJpaDao<TariffEM, Integer> implements TariffEMDao {
			
	private static Log logger = LogFactory.getLog(TariffEMDaoImpl.class);
	    
	public TariffEMDaoImpl() {
		super(TariffEM.class);
	}
	
	@Autowired
	TOURateDao touRateDao;
	
	@Autowired
	SeasonDao seasonDao;
	
	@Autowired
	DayEMDao dayEMDao;
	
	@Autowired
	MonthEMDao monthEMDao;
	
	@Autowired
	BillingDayEMDao billingDayEMDao;
	
	@Autowired
	BillingMonthEMDao billingMonthEMDao;
	
	@Autowired
	PrepaymentLogDao prepaymentLogDao;
	
	@Autowired
	ContractDao contractDao;
	
	@Autowired
    SupplyTypeDao supplyTypeDao;
	
	@Autowired
	CodeDao codeDao;
	
	@Autowired
	TariffTypeDao tariffTypeDao;
	
    @Autowired
    JpaTransactionManager transactionManager;

    @Override
    public List<Map<String, Object>> getChargeMgmtList(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TariffEMVO> getCustomerChargeMgmtList(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int updateData(TariffEM tariffEM) throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int tariffDeleteByCondition(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Double getUsageChargeByContract(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getPrepaymentTariff(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TariffEM> getApplyedTariff(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TariffEM> getNewestTariff(Contract contract) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean isNewDate(String yyyymmdd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TariffEM> getNewestTariff(Contract contract, String yyyymmdd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean deleteYyyymmddTariff(String yyyymmdd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<TariffEM> getPersistentClass() {
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

    @Override
    public String getAppliedTariffDate(String date, Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getTariffSupplySizeComboData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

	
}