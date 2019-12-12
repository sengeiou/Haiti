package com.aimir.dao.view.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.ContractStatus;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.view.MonthEMViewDao;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.system.Contract;
import com.aimir.model.view.MonthEMView;
import com.aimir.util.Condition;
import com.aimir.util.SQLWrapper;

@Repository(value="monthemDaoView")
@Transactional
public class MonthEMViewDaoImpl extends AbstractHibernateGenericDao<MonthEMView, Integer> implements MonthEMViewDao {
	private static Log logger = LogFactory.getLog(MonthEMViewDaoImpl.class);

	@Autowired
	protected MonthEMViewDaoImpl(SessionFactory sessionFactory) {
		super(MonthEMView.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<MonthEMView> getMonthEMsByListCondition(Set<Condition> list) {
		return findByConditions(list);
	}
    
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<MonthEMView> getMonthEMsByCondition(Map<String, Object> condition) {
		DeviceType mdevType     = (DeviceType) condition.get("mdevType");
        String mdevId           = (String) condition.get("mdevId");
        List<Integer> channelList  = (List<Integer>) condition.get("channelList");
        String  yyyymm          = (String) condition.get("yyyymm");
        Integer  dst      = (Integer) condition.get("dst");
        
        Query query = null;
        List<MonthEMView> returnList = new ArrayList<MonthEMView>();
        try {
            
            StringBuffer sb = new StringBuffer();
            sb.append("\nSELECT     em.id.yyyymm as yyyymm, ");
            sb.append("\n           sum(em.value_01) as value_01, sum(em.value_02) as value_02, sum(em.value_03) as value_03, sum(em.value_04) as value_04, ");
            sb.append("\n           sum(em.value_05) as value_05, sum(em.value_06) as value_06, sum(em.value_07) as value_07, sum(em.value_08) as value_08, ");
            sb.append("\n           sum(em.value_09) as value_09, sum(em.value_10) as value_10, sum(em.value_11) as value_11, sum(em.value_12) as value_12, ");
            sb.append("\n           sum(em.value_13) as value_13, sum(em.value_14) as value_14, sum(em.value_15) as value_15, sum(em.value_16) as value_16, ");
            sb.append("\n           sum(em.value_17) as value_17, sum(em.value_18) as value_18, sum(em.value_19) as value_19, sum(em.value_20) as value_20, ");
            sb.append("\n           sum(em.value_21) as value_21, sum(em.value_22) as value_22, sum(em.value_23) as value_23, sum(em.value_24) as value_24, ");
            sb.append("\n           sum(em.value_25) as value_25, sum(em.value_26) as value_26, sum(em.value_27) as value_27, sum(em.value_28) as value_28, ");
            sb.append("\n           sum(em.value_29) as value_29, sum(em.value_30) as value_30, sum(em.value_31) as value_31, ");
            sb.append("\n           em.mdevId as mdevId,");
            sb.append("\n           em.mdevType as mdevType,");
            sb.append("\n           sum(em.baseValue) as baseValue,");
            sb.append("\n           sum(em.total_value) as total_value");
            sb.append("\nFROM       MonthEMView em");
            sb.append("\nWHERE      em.id.mdevType = :mdevType ");
            sb.append("\nAND        em.id.mdevId = :mdevId ");
            sb.append("\nAND        em.id.yyyymm = :yyyymm ");
            sb.append("\nAND        em.id.channel in (:channelList) ");
            sb.append("\nAND        em.id.dst = :dst ");
            sb.append("\nGROUP BY   em.id.mdevId, em.id.mdevType, em.id.yyyymm ");

            
            query = getSession().createQuery(sb.toString());
            query.setParameter("mdevType", mdevType);
            query.setParameter("mdevId", mdevId);
            query.setParameter("yyyymm", yyyymm);
            query.setParameterList("channelList", channelList);
            query.setParameter("dst", dst);

            returnList = query.setResultTransformer(Transformers.aliasToBean(MonthEMView.class)).list();
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e,e);
        }
        return returnList;
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<MonthEMView> getMonthEMs(MonthEMView monthEMView) {
		Criteria criteria = getSession().createCriteria(MonthEM.class);
		
		if (monthEMView != null) {
			
			if (monthEMView.getContract() != null) {
				
				if (monthEMView.getContract().getId() != null) {
					
					criteria.add(Restrictions.eq("contract.id", monthEMView.getContract().getId()));
				} 
			}
			
			if (monthEMView.id.getChannel() != null) {
				
				criteria.add(Restrictions.eq("id.channel", monthEMView.id.getChannel()));
			}
			
			if (monthEMView.id.getYyyymm() != null) {
				
				if ( 6 == monthEMView.id.getYyyymm().length() ) {
					
					criteria.add(Restrictions.eq("id.yyyymm", monthEMView.id.getYyyymm()));
				} else if ( 4 == monthEMView.id.getYyyymm().length() ) {

					criteria.add(Restrictions.like("id.yyyymm", monthEMView.id.getYyyymm() + "%"));
					criteria.addOrder( Order.asc("id.yyyymm") );
				}
			}
			
			if (monthEMView.getMDevType() != null) {
				
				criteria.add(Restrictions.eq("id.mdevType", monthEMView.getMDevType()));
				
				if ( monthEMView.getMDevType().name().equals(DeviceType.EndDevice.name()) ) {
					
					criteria.addOrder( Order.asc("enddevice.id") );
				} else if ( monthEMView.getMDevType().name().equals(DeviceType.Modem.name()) ) {
					
					criteria.addOrder( Order.asc("modem.id") );
				}
			}
			
			if (monthEMView.getEnddevice() != null) {
				
				if ( monthEMView.getEnddevice().getId() != null ) {
					
					criteria.add(Restrictions.eq("enddevice.id", monthEMView.getEnddevice().getId()));
				}
			}
		}

		return criteria.list();
	}

	@Override
	public List<MonthEMView> getMonthCustomerBillingGridData(Map<String, Object> conditionMap) {
		String startDate = ((String) conditionMap.get("startDate")).substring(0, 6);
        String endDate = ((String) conditionMap.get("endDate")).substring(0, 6);
        String searchDateType = ((String) conditionMap.get("searchDateType"));
        String locationCondition = ((String) conditionMap.get("locationCondition"));
        String tariffIndex = conditionMap.get("tariffIndex").toString();
        String customerName = ((String) conditionMap.get("customerName"));
        String contractNo = ((String) conditionMap.get("contractNo"));
        String meterName = ((String) conditionMap.get("meterName"));
        int supplierId = Integer.parseInt(conditionMap.get("supplierId").toString());
        
        int page = Integer.parseInt((String) conditionMap.get("page"));
        int pageSize = Integer.parseInt((String) conditionMap.get("pageSize"));

        StringBuilder sb = new StringBuilder();
        sb.append("FROM MonthEMView m ");
        sb.append("WHERE m.id.channel = :channel ");
        sb.append("AND   m.contract.serviceTypeCode.code = :serviceTypeCode ");
        sb.append("AND   m.contract.status.code = :status ");
        sb.append("AND   m.contract.customer.supplier.id = :supplierId      ");	

        if ("4".equals(searchDateType)) { // Monthly
            sb.append("AND   m.id.yyyymm = :startDate ");
        } else if ("7".equals(searchDateType)) { // Seasonal
            sb.append("AND   m.id.yyyymm >= :startDate ");
            sb.append("AND   m.id.yyyymm <= :endDate ");
        }
        if (!"".equals(locationCondition)) {
            sb.append("AND   m.location.id in (").append(locationCondition).append(") ");
        }
        if (!"".equals(tariffIndex)) {
            sb.append("AND   m.contract.tariffIndex = :tariffIndex ");
        }
        if (!"".equals(customerName)) {
            sb.append("AND   m.contract.customer.name LIKE :customerName ");
        }
        if (!"".equals(contractNo)) {
            sb.append("AND   m.contract.contractNumber LIKE :contractNo ");
        }
        if (!"".equals(meterName)) {
            sb.append("AND   m.meter.mdsId LIKE :meterName ");
        }

        Query query = getSession().createQuery(sb.toString());
//        query.setString("channel", DefaultChannel.Usage.getCode() + "");
        query.setInteger("channel", DefaultChannel.Usage.getCode());
        query.setString("serviceTypeCode", MeterType.EnergyMeter.getServiceType());
        query.setString("status", ContractStatus.NORMAL.getCode());
        query.setInteger("supplierId", supplierId);
        
        if ("4".equals(searchDateType)) {
            query.setString("startDate", startDate);
        } else if ("7".equals(searchDateType)) {
            query.setString("startDate", startDate);
            query.setString("endDate", endDate);
        }

        if (!"".equals(tariffIndex)) {
            query.setString("tariffIndex", tariffIndex);
        }
        if (!"".equals(customerName)) {
            query.setString("customerName", new StringBuilder().append('%').append(customerName).append('%').toString());
        }
        if (!"".equals(contractNo)) {
            query.setString("contractNo", new StringBuilder().append('%').append(contractNo).append('%').toString());
        }
        if (!"".equals(meterName)) {
            query.setString("meterName", new StringBuilder().append('%').append(meterName).append('%').toString());
        }

        int firstResult = page * pageSize;

        query.setFirstResult(firstResult);
        query.setMaxResults(pageSize);

        return query.list();
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<MonthEMView> getMonthlyUsageByContract(Contract contract, String yyyymm, String channels) {
		String SEPARATOR = ",";
		Query query = null;
		
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("\n FROM MonthEMView");
		queryStr.append("\n WHERE id.yyyymm = :yyyymm");
		queryStr.append("\n AND contract.id = :contractId");
		queryStr.append("\n AND id.channel in (:channels)");

		String[] cha = channels.split(SEPARATOR);
		List<Integer> list = new ArrayList<Integer>();
		
		for ( String ch : cha ) {
			list.add(Integer.parseInt(ch));
		}
		
		query = getSession().createQuery(queryStr.toString());
		query.setString("yyyymm", yyyymm);
		query.setInteger("contractId", contract.getId());
		query.setParameterList("channels", list.toArray());
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<MonthEMView> getMonthEMbySupplierId(Map<String, Object> params) {
		String yyyymm = (String)params.get("yyyymm");  
		Integer channel = (Integer)params.get("channel");
		String mdevId = (String)params.get("mdevId");
		DeviceType mdevType = (DeviceType)params.get("mdevType");  
		int supplierId = (Integer)params.get("supplierId");
		
		StringBuffer sb = new StringBuffer();

		sb.append("\n  ");
		sb.append("\n FROM MonthEMView m");
		sb.append("\n WHERE m.id.mdevId = :mdevId");
		sb.append("\n AND m.id.mdevType = :mdevType");
		sb.append("\n AND m.id.yyyymm = :yyyymm");
		sb.append("\n AND m.id.channel = :channel");
		sb.append("\n AND m.contract.customer.supplier.id = :supplierId");
		
		
		Query query = getSession().createQuery(new SQLWrapper().getQuery(sb.toString()));
		query.setString("mdevId", mdevId);
		query.setString("mdevType", mdevType.toString());
		query.setString("yyyymm", yyyymm);
		query.setInteger("channel", channel);
		query.setInteger("supplierId", supplierId);
		
		return query.list();
	}
}
