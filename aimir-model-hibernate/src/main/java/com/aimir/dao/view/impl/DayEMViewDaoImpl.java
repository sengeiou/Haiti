package com.aimir.dao.view.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.ContractStatus;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.view.DayEMViewDao;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.view.DayEMView;
import com.aimir.model.view.DayGMView;
import com.aimir.util.Condition;
import com.aimir.util.StringUtil;

@Repository(value="dayemDaoView")
public class DayEMViewDaoImpl extends AbstractHibernateGenericDao<DayEMView, Integer> implements DayEMViewDao {
	private static Log log = LogFactory.getLog(DayEMViewDaoImpl.class);

    @Autowired
    protected DayEMViewDaoImpl(SessionFactory sessionFactory) {
        super(DayEMView.class);
        super.setSessionFactory(sessionFactory);
    }

    
    @Override
	public List<DayEMView> getDayEMs(DayEMView dayEMView) {
        Criteria criteria = getSession().createCriteria(DayEMView.class);
        
        if (dayEMView != null) {
            
            if (dayEMView.getContract() != null) {
                
                if (dayEMView.getContract().getId() != null) {
                    
                    criteria.add(Restrictions.eq("contract.id", dayEMView.getContract().getId()));
                } 
            }
            
            if (dayEMView.id.getChannel() != null) {
                
                criteria.add(Restrictions.eq("channel", dayEMView.id.getChannel()));
            }
            
            if (dayEMView.id.getYyyymmdd() != null) {
                
                
                criteria.add(Restrictions.eq("yyyymmdd", dayEMView.id.getYyyymmdd()));
            }
            
            if (dayEMView.id.getMdevType() != null) {
                
                criteria.add(Restrictions.eq("mdevType", dayEMView.id.getMdevType()));
            }
        }

        return criteria.list();
	}

	@Override
	public List<Object> getConsumptionEmCo2MonitoringSumMinMaxLocationId(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public List<DayEMView> getDayEMsByListCondition(Set<Condition> list) {
		 return findByConditions(list);
	}

	@Override
	public List<DayEMView> getDayCustomerBillingGridData(Map<String, Object> conditionMap) {
        String startDate = (String) conditionMap.get("startDate");
        String endDate = (String) conditionMap.get("endDate");
        String searchDateType = (String) conditionMap.get("searchDateType");
        String locationCondition = (String) conditionMap.get("locationCondition");
        String tariffIndex = (String) conditionMap.get("tariffIndex");
        String customerName = (String) conditionMap.get("customerName");
        String contractNo = (String) conditionMap.get("contractNo");
        String meterName = (String) conditionMap.get("meterName");
        int supplierId = Integer.parseInt(conditionMap.get("supplierId").toString());
        
        int page = Integer.parseInt((String) conditionMap.get("page"));
        int pageSize = Integer.parseInt((String) conditionMap.get("pageSize"));

        StringBuilder sb = new StringBuilder();
        sb.append("from DayEMView d ");
        sb.append("WHERE d.id.channel = :channel ");
        sb.append("AND   d.id.dst = 0 ");
        sb.append("AND   d.contract.serviceTypeCode.code = :serviceTypeCode ");
        sb.append("AND   d.contract.status.code = :status ");
        sb.append("AND   d.contract.customer.supplier.id = :supplierId      ");	

        if ("1".equals(searchDateType)) { // 일별
            sb.append("AND   d.id.yyyymmdd = :startDate ");
        } else if ("3".equals(searchDateType)) { // 월별
            sb.append("AND   d.id.yyyymmdd between :startDate and :endDate ");
        }
        if (!"".equals(locationCondition)) {
            sb.append("AND   d.location.id IN (").append(locationCondition).append(") ");
        }
        if (!"".equals(tariffIndex)) {
            sb.append("AND   d.contract.tariffIndex = :tariffIndex ");
        }
        if (!"".equals(customerName)) {
            sb.append("AND   UPPER(d.contract.customer.name) LIKE UPPER(:customerName) ");
        }
        if (!"".equals(contractNo)) {
            sb.append("AND   d.contract.contractNumber LIKE :contractNo ");
        }
        if (!"".equals(meterName)) {
            sb.append("AND   d.meter.mdsId LIKE :meterName ");
        }

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("channel", DefaultChannel.Usage.getCode());
        query.setString("serviceTypeCode", MeterType.EnergyMeter.getServiceType());
        query.setString("status", ContractStatus.NORMAL.getCode());
        query.setInteger("supplierId", supplierId);
        
        if ("1".equals(searchDateType)) {
            query.setString("startDate", startDate);
        } else if ("3".equals(searchDateType)) {
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
	public List<DayEMView> getMeteringFailureMeteringData(Map<String, Object> params) {
        String meterId = StringUtil.nullToBlank(params.get("meterId"));
        String startDate = StringUtil
                .nullToBlank(params.get("searchStartDate"));
        String endDate = StringUtil.nullToBlank(params.get("searchEndDate"));

        Query query = getSession().createQuery("FROM DayEMView d "
                                + "\n WHERE d.meter.id = ? "
                                + "\n AND d.id.channel = 1 "
                                + "\n AND d.id.dst = 0 "
                                + "\n AND d.id.yyyymmdd between ? and ?");
        query.setInteger(1, Integer.parseInt(meterId));
        query.setString(2,  startDate);
        query.setString(3,  endDate);
        return query.list();
	}

}
