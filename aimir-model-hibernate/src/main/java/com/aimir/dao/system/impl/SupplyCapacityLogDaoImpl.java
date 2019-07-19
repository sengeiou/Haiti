package com.aimir.dao.system.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.SupplyCapacityLogDao;
import com.aimir.model.system.SupplyCapacityLog;

	@Repository(value="supplycapacitylogDao")
	public class SupplyCapacityLogDaoImpl extends AbstractHibernateGenericDao<SupplyCapacityLog, Long> implements SupplyCapacityLogDao{

	
		@Autowired
		protected SupplyCapacityLogDaoImpl(SessionFactory sessionFactory) {
			super(SupplyCapacityLog.class);
			super.setSessionFactory(sessionFactory);
		}

		public void supplyCapacityLogDelete(int supplyTypeId) {
			StringBuffer sql = new StringBuffer();             
	        sql.append("DELETE SupplyCapacityLog WHERE supplyType_id = ? ");
	        
	        Query query = getSession().createQuery(sql.toString());
	        query.setParameter(1, supplyTypeId);
	        query.executeUpdate();
	        // this.getHibernateTemplate().bulkUpdate(query.toString(), supplyTypeId );	
			
		}

		@SuppressWarnings("unchecked")
		public List<SupplyCapacityLog> getSupplyCapacityLogs(int page, int count) {
			int pageSize = count;
			Criteria criteria = getSession().createCriteria(SupplyCapacityLog.class);
			criteria.addOrder(Order.desc("id")); // 나중에 입력된 최근 글부터 정렬
			criteria.setFirstResult((page - 1) * pageSize); // page는 1부터 2,3... 하지만
			// 이는 입력 되기를 0번 글부터 시작되고
			// pageSize단위로 곱해준다.
			criteria.setMaxResults(pageSize); // 한번에 불러올 리스트 크기를 정의
			return criteria.list();
		}
		
	}
