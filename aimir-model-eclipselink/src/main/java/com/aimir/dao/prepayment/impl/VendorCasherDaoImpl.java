package com.aimir.dao.prepayment.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.CasherStatus;
import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.prepayment.VendorCasherDao;
import com.aimir.model.prepayment.VendorCasher;
import com.aimir.model.system.Operator;
import com.aimir.util.Condition;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@Repository("vendorCasherDao")
public class VendorCasherDaoImpl extends AbstractJpaDao<VendorCasher, Integer>
 implements VendorCasherDao{

	Log logger = LogFactory.getLog(VendorCasherDaoImpl.class);
	
	public VendorCasherDaoImpl() {
		super(VendorCasher.class);
	}

    @Autowired
    JpaTransactionManager transactionManager;
    
	@Override
	@SuppressWarnings("unchecked")
	public Boolean isVaildVendorCasher(Operator vendor, String casherId, String hashedPw) {
		Boolean result = false;
		String sql = "select v from VendorCasher v where vendor.loginId = :vendorLoginId and casherId = :casherId " +
		             " and password = :password and status = :status";
		Query query = em.createQuery(sql, VendorCasher.class);
		query.setParameter("vendorLoginId", vendor.getLoginId());
		query.setParameter("casherId", casherId);
		query.setParameter("password", hashedPw);
		query.setParameter("status", CasherStatus.WORK.getCode());
		List<VendorCasher> list = query.getResultList();
		
		if (list != null && list.size() > 0) {
			result = true;
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getCasherList(Map<String, Object> condition) {
		Map<String, Object> result = new HashMap<String, Object>();
		Operator vendor = null;
		
		if ( condition.get("vendor") != null ) {
			vendor = (Operator) condition.get("vendor");
		}
		
		String casherId = StringUtil.nullToBlank(condition.get("casherId"));
		String name = StringUtil.nullToBlank(condition.get("name"));
		Integer page = (Integer) condition.get("page");
		Integer limit = (Integer) condition.get("limit");
		String country = StringUtil.nullToBlank(condition.get("country"));
		String lang = StringUtil.nullToBlank(condition.get("lang"));
		
		String sql = "select v from VendorCasher v where status = :status";
		
		if ( vendor != null ) {
		    sql += " and vendor.loginId like :vendorLoginId";
		}
		
		if ( !casherId.equals("") ) {
		    sql += " and casherId like :casherId";
		}
		
		if ( !name.equals("") ) {
		    sql += " and name like :name";
		}
		sql += " order by id";
		
		Query query = em.createQuery(sql, VendorCasher.class);
		
		query.setParameter("status", CasherStatus.WORK.getCode());
		
		if (vendor != null) {
		    query.setParameter("vendorLoginId", "%" + vendor.getLoginId() + "%");
		}
		
		if ( !casherId.equals("")) {
		    query.setParameter("casherId", "%" + casherId + "%");
		}
		
		if (!name.equals("")) {
		    query.setParameter("name", "%" + name + "%");
		}
		
		result.put("count", query.getResultList().size());
		
		query.setFirstResult((page-1)*limit);
		query.setMaxResults(limit);
		List<VendorCasher> list = query.getResultList();
		
		for ( VendorCasher casher : list ) {
			String date = casher.getLastUpdateDate();
			date = TimeLocaleUtil.getLocaleDate(date, lang, country);
			casher.setLastUpdateDate(date);
		}
		
		result.put("list", list);
		return result;
	}
	
	public String deleteCasher(Map<String, Object> condition) {
		String result = "failed:";
		TransactionStatus txStatus = null;
		
		try {
			txStatus = transactionManager.getTransaction(null);
			
			Integer id = (Integer) condition.get("id");
			String date = StringUtil.nullToBlank(condition.get("date"));
			
			VendorCasher casher = get(id);
			casher.setLastUpdateDate(date);
			casher.setStatus(CasherStatus.QUIT.getCode());
			
			update(casher);
			result = "success";
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			result += "can't delete casher account";
			transactionManager.rollback(txStatus);
			e.printStackTrace();
		}
		return result;
	}
	
	public VendorCasher getByVendorCasherId(String loginId, Operator vendor) {
	    String sql = "select v from VendorCasher v where status = :status " +
	                 "and casherId = :casherId and vendor.loginId = :vendorLoginId";
	    Query query = em.createQuery(sql, VendorCasher.class);
	    query.setParameter("status", CasherStatus.WORK.getCode());
	    query.setParameter("casherId",  loginId);
	    query.setParameter("vendorLoginId",  vendor.getLoginId());
	    return (VendorCasher)query.getSingleResult();
	}
	
	public VendorCasher getByMacAddress(String mac, Operator vendor) {
	    String sql = "select v from VendorCasher v where status = :status " +
	                 "and macAddress like :macAddress and vendor.loginId = :vendorLoginId";
	    Query query = em.createQuery(sql, VendorCasher.class);
	    query.setParameter("status", CasherStatus.WORK.getCode());
	    query.setParameter("macAddress", "%"+mac+"%");
	    query.setParameter("vendorLoginId", vendor.getLoginId());
	    return (VendorCasher)query.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<VendorCasher> getByVendorOperator(Operator vendor) {
	    String sql = "select v from VendorCasher v where status = :status " +
	                 "and vendor.loginId = :vendorLoginId";
	    Query query = em.createQuery(sql, VendorCasher.class);
	    query.setParameter("status", CasherStatus.WORK.getCode());
	    query.setParameter("vendorLoginId", vendor.getLoginId());
	    return query.getResultList();
	}
	
	@Transactional
	public String changePassword(Map<String, Object> condition) {
		String result = "failed:";
		String id = StringUtil.nullToBlank(condition.get("casherId"));
		String pwd = StringUtil.nullToBlank(condition.get("password"));
		Operator vendor = (Operator) condition.get("vendor");
		
		String sql = "update VendorCasher set password = :password " +
		             "where status = :status and casherId = :casherId and vendor.loginId = :vendorLoginId";
		Query query = em.createQuery(sql, VendorCasher.class);
		query.setParameter("password", pwd);
		query.setParameter("status", CasherStatus.WORK.getCode());
		query.setParameter("casherId",  id);
		query.setParameter("vendorLoginId", vendor.getLoginId());
		int i = query.executeUpdate();
		if (i > 0)
		    result = "success";
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getMacAddressLIst( Operator vendor ) {
	    String sql = "select macAddress from VendorCasher where status = :status and vendor.loginId = :vendorLoginId";
	    Query query = em.createQuery(sql, String.class);
	    query.setParameter("status", CasherStatus.WORK.getCode());
	    query.setParameter("vendorLoginId",  vendor.getLoginId());
	    List<String> macs = query.getResultList();
	    
		List<String> list = new ArrayList<String>();
		for ( String mac : macs ) {
			if ( mac == null ) {
				continue;
			}
			
			list.addAll(Arrays.asList(mac.split(",")));			
		}
		return list;
	}
	
	@Transactional
	public String updateMacAddress( Map<String, Object> condition ) {
		String result = "failed:";
		String id = StringUtil.nullToBlank(condition.get("casherId"));
		String macAddress = StringUtil.nullToBlank(condition.get("mac"));
		Operator vendor = (Operator) condition.get("vendor");
		
		String sql = "update VendorCasher set macAddress = :macAddress where status = :status " +
		             "and vendor.loginId = :vendorLoginId and casherId = :casherId";
		Query query = em.createQuery(sql, VendorCasher.class);
		query.setParameter("macAddress", macAddress);
		query.setParameter("status", CasherStatus.WORK.getCode());
		query.setParameter("vendorLoginId",  vendor.getLoginId());
		query.setParameter("casherId", id);
		int i = query.executeUpdate();
		if (i > 0) {
		    result = "success";
		}
		return result;
	}

    @Override
    public Boolean isVaildCasherForSA(String casherId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<VendorCasher> getCasher(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<VendorCasher> getCasherByName(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<VendorCasher> getPersistentClass() {
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
