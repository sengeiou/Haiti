package com.aimir.dao.mvm.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.dao.mvm.RealTimeBillingEMDao;
import com.aimir.dao.mvm.SAPDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.mvm.SAP;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

@Repository(value = "sapDao")
public class SAPDaoImpl extends AbstractHibernateGenericDao<SAP, Integer> implements SAPDao{
	@Autowired
	private BillingMonthEMDao billingMonthEMDao;
	
	@Autowired
	private RealTimeBillingEMDao realTimeBillEMDao;
	
	@Autowired
	private MeterDao meterDao;
	
	@Autowired
	protected SAPDaoImpl(SessionFactory sessionFactory) {
		super(SAP.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public List<BillingMonthEM> getBillingMonthEMs(Integer sapId) {
		SAP sap = this.get(sapId);
		Meter meter = sap.getMeter();
		Set<Condition> condition = new HashSet<Condition>();
		condition.add(new Condition("meter",new Object[]{meter},null,Restriction.EQ));
		return billingMonthEMDao.findByConditions(condition);
	}

//	@Override
//	public SAPWrap getSapWrap(Integer id) {
//		
//		SAP sap = this.get(id);
//		
//		SAPWrap sapWrap = new SAPWrap();
//		sapWrap.setSap(sap);
//		
//		//가장 최근것을 읽어온다.
//		
//		//조건 설정
//		Set<Condition> conditions = new HashSet<Condition>();
//		conditions.add(new Condition("meter",new Object[]{sap.getMeter()},null,Restriction.EQ));
//		Condition orderCondition = new Condition();
//		orderCondition.setField("writeDate");
//		orderCondition.setRestrict(Restriction.ORDERBYDESC);
//		conditions.add(orderCondition);
//		
//		//billing month em 조회
//		BillingMonthEM billing = null;
//		List<BillingMonthEM> bills = billingMonthEMDao.findByConditions(conditions);
//		if(bills==null || bills.size()==0){
//			return null;
//		}
//		billing = bills.get(0);
//		sapWrap.setBilling(billing);
//		
//		
//		//current billing 조회
//		RealTimeBillingEM currentBilling = null;
//		List<RealTimeBillingEM> currs = realTimeBillEMDao.findByConditions(conditions);
//		if(currs==null || currs.size()==0){
//			return null;
//		}
//		currentBilling = currs.get(0);
//		sapWrap.setCurrBilling(currentBilling);
//		
//		return sapWrap;
//	}

	@Override
	public SAP getSAP(String meterSerial) {
		Meter meter = meterDao.get(meterSerial);
		return this.findByCondition("meter", meter);
	}

}
