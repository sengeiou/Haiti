package com.aimir.dao.prepayment.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.CasherStatus;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.prepayment.VendorCasherDao;
import com.aimir.model.prepayment.VendorCasher;
import com.aimir.model.system.Operator;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@Repository("vendorCasherDao")
public class VendorCasherDaoImpl extends AbstractHibernateGenericDao<VendorCasher, Integer>
 implements VendorCasherDao{

	Log logger = LogFactory.getLog(VendorCasherDaoImpl.class);
	
	@Autowired
	protected VendorCasherDaoImpl(SessionFactory sessionFactory) {
		super(VendorCasher.class);
		super.setSessionFactory(sessionFactory);
	}

    @Autowired
    HibernateTransactionManager transactionManager;
    
	@Override
	@SuppressWarnings("unchecked")
	public Boolean isVaildVendorCasher(Operator vendor, String casherId, String hashedPw) {
		Boolean result = false;
		Criteria criteria = getSession().createCriteria(VendorCasher.class);
		
		criteria.add(Restrictions.eq("vendor", vendor));
		criteria.add(Restrictions.eq("casherId", casherId));
		criteria.add(Restrictions.eq("password", hashedPw));
		criteria.add(Restrictions.eq("status", CasherStatus.WORK.getCode()));
		List<VendorCasher> list = criteria.list();
		
		if (list != null && list.size() > 0) {
			result = true;
		}
		return result;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Boolean isVaildCasherForSA(String casherId) {
		Boolean result = false;
		Criteria criteria = getSession().createCriteria(VendorCasher.class);
		
		criteria.add(Restrictions.eq("casherId", casherId));
		criteria.add(Restrictions.eq("status", CasherStatus.WORK.getCode()));
		List<VendorCasher> list = criteria.list();
		
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
		
		Boolean allManager = (Boolean)condition.get("allManager");
		String casherId = StringUtil.nullToBlank(condition.get("casherId"));
		String name = StringUtil.nullToBlank(condition.get("name"));
		Integer page = (Integer) condition.get("page");
		Integer limit = (Integer) condition.get("limit");
		String country = StringUtil.nullToBlank(condition.get("country"));
		String lang = StringUtil.nullToBlank(condition.get("lang"));
		
		Criteria criteria = getSession().createCriteria(VendorCasher.class);
		
		criteria.add(Restrictions.eq("status", CasherStatus.WORK.getCode()));
		
		if(allManager) {
			criteria.add(Restrictions.eq("isManager", true));	
		}
		
		if ( vendor != null ) {
			criteria.add(Restrictions.eq("vendor", vendor));
		}
		
		if ( !casherId.equals("") ) {
			criteria.add(Restrictions.ilike("casherId", "%" + casherId + "%"));
		}
		
		if ( !name.equals("") ) {
			criteria.add(Restrictions.ilike("name", "%" + name + "%"));
		}
		
		result.put("count", criteria.list().size());
		
		criteria.addOrder(Order.asc("id"));		
		criteria.setFirstResult((page-1)*limit);
		criteria.setMaxResults(limit);
		List<VendorCasher> list = criteria.list();
		
		for ( VendorCasher casher : list ) {
			String date = casher.getLastUpdateDate();
			date = TimeLocaleUtil.getLocaleDate(date, lang, country);
			casher.setLastUpdateDate(date);
		}
		
		result.put("list", list);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<VendorCasher> getCasher(Map<String, Object> condition) {
		
		String casherId = StringUtil.nullToBlank(condition.get("casherId"));
		String password = StringUtil.nullToBlank(condition.get("password"));
		
		Criteria criteria = getSession().createCriteria(VendorCasher.class);
		
		criteria.add(Restrictions.eq("status", CasherStatus.WORK.getCode()));
		
		if ( !"".equals(casherId) ) {
			criteria.add(Restrictions.eq("casherId", casherId));
		}
		
		if ( !"".equals(password) ) {
			criteria.add(Restrictions.eq("password", password));
		}
		
		List<VendorCasher> list = criteria.list();
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<VendorCasher> getCasherByName(Map<String, Object> condition) {

		String casherId = StringUtil.nullToBlank(condition.get("casherId"));
		String vendorAccount = StringUtil.nullToBlank(condition.get("vendorAccount"));
		
		Criteria criteria = getSession().createCriteria(VendorCasher.class);
        criteria.createAlias("vendor", "v");		
        criteria.add(Restrictions.eq("v.loginId", vendorAccount));		
        
		criteria.add(Restrictions.eq("status", CasherStatus.WORK.getCode()));
		criteria.add(Restrictions.eq("casherId", casherId));

		
		List<VendorCasher> list = criteria.list();
		return list;
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
		Criteria criteria = getSession().createCriteria(VendorCasher.class);
		criteria.add(Restrictions.eq("status", CasherStatus.WORK.getCode()));
		criteria.add(Restrictions.eq("casherId", loginId));
		criteria.add(Restrictions.eq("vendor", vendor));
		return (VendorCasher) criteria.uniqueResult();
	}
	
	public VendorCasher getByMacAddress(String mac, Operator vendor) {
		Criteria criteria = getSession().createCriteria(VendorCasher.class);
		criteria.add(Restrictions.eq("status", CasherStatus.WORK.getCode()));
		criteria.add(Restrictions.eq("macAddress", mac));
		criteria.add(Restrictions.eq("vendor", vendor));
		return (VendorCasher) criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<VendorCasher> getByVendorOperator(Operator operator) {
		Criteria criteria = getSession().createCriteria(VendorCasher.class);
		criteria.add(Restrictions.eq("vendor", operator));
		criteria.add(Restrictions.eq("status", CasherStatus.WORK.getCode()));
		return criteria.list(); 
	}
	
	@Transactional
	public String changePassword(Map<String, Object> condition) {
		String result = "failed:";
		String id = StringUtil.nullToBlank(condition.get("casherId"));
		String pwd = StringUtil.nullToBlank(condition.get("password"));
		Operator vendor = (Operator) condition.get("vendor");
		
		try {
			Criteria criteria = getSession().createCriteria(VendorCasher.class);
			criteria.add(Restrictions.eq("status", CasherStatus.WORK.getCode()));
			criteria.add(Restrictions.eq("casherId", id));
			criteria.add(Restrictions.eq("vendor", vendor));
			VendorCasher casher = (VendorCasher) criteria.uniqueResult();
			casher.setPassword(pwd);
			casher.setIsFirst(false);
			update(casher);
			result = "success";
		}catch (Exception e){
			e.printStackTrace();
			result +="can't change password now";
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getMacAddressLIst( Operator vendor ) {
		List<String> list = new ArrayList<String>();
		Criteria criteria = getSession().createCriteria(VendorCasher.class);
		criteria.add(Restrictions.eq("status", CasherStatus.WORK.getCode()));
		criteria.add(Restrictions.eq("vendor", vendor));
		List<VendorCasher> casherList = criteria.list();
		
		for ( VendorCasher casher : casherList ) {
			if ( casher.getMacAddress() == null ) {
				continue;
			}
			list.addAll(removeTunelAdapter(casher.getMacAddress()));
		}
		return list;
	}
	
	@Transactional
	public String updateMacAddress( Map<String, Object> condition ) {
		String result = "failed:";
		String id = StringUtil.nullToBlank(condition.get("casherId"));
		String macAddress = StringUtil.nullToBlank(condition.get("mac"));
		Operator vendor = (Operator) condition.get("vendor");
		String macList = "";
		
		if(!macAddress.equals("")){		
			for(String mac : removeTunelAdapter(macAddress)){
				macList += mac + ",";
			}
			macList = macList.substring(0, macList.length() - 1);
		}
		
		try {
			Criteria criteria = getSession().createCriteria(VendorCasher.class);
			criteria.add(Restrictions.eq("status", CasherStatus.WORK.getCode()));
			criteria.add(Restrictions.eq("vendor", vendor));
			criteria.add(Restrictions.eq("casherId", id));
			VendorCasher casher = (VendorCasher) criteria.uniqueResult();
			
			casher.setMacAddress(macList);
			casher.setIsFirst(new Boolean(false));
			update(casher);
			result = "success";
		} catch ( Exception e) {
			e.printStackTrace();
			result +="can't update macaddress now";
		}		
		return result;
	}
	
	/*
	 * 터널 어댑터 삭제
	 */
	private List<String> removeTunelAdapter(String macAddressList){
		macAddressList.trim();
        List<String> removelist = new ArrayList<String>();
        removelist.add("00:00:00:00:00:00:00:E0");
        
        List<String> cleanMacList = new ArrayList<String>();
        cleanMacList.addAll(Arrays.asList(macAddressList.split(",")));
        cleanMacList.removeAll(removelist);
		
        return cleanMacList;
	}
}
