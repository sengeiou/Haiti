package com.aimir.dao.mvm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.mvm.BillingBlockTariffWrongDao;
import com.aimir.model.mvm.BillingBlockTariffWrong;
import com.aimir.util.Condition;

@Repository(value = "billingBlockTariffWrongDao")
public class BillingBlockTariffWrongDaoImpl extends AbstractJpaDao<BillingBlockTariffWrong, Integer> implements BillingBlockTariffWrongDao {
    @SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(BillingBlockTariffDaoImpl.class);
    
    public BillingBlockTariffWrongDaoImpl() {
		super(BillingBlockTariffWrong.class);
	}

	@Override
	public Class<BillingBlockTariffWrong> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> arg0, String arg1, String... arg2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public BillingBlockTariffWrong getBillingBlockTariffWrong(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int updateComplateBillingBlockWrong(String mdevId) {
		return 0;		
	}
	
	@Override
	public Integer udpateBillingFail() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
