package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.model.system.Customer;
import com.aimir.util.Condition;
import com.aimir.util.StringUtil;

@Repository(value = "customerDao")
public class CustomerDaoImpl extends AbstractHibernateGenericDao<Customer, Integer> implements CustomerDao {
		
    Log logger = LogFactory.getLog(CustomerDaoImpl.class);
    
    @Autowired
    protected CustomerDaoImpl(SessionFactory sessionFactory) {
        super(Customer.class);
        super.setSessionFactory(sessionFactory);
    }
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<Customer> getCustomersByName(String[] name) {			
	    Criteria criteria = getSession().createCriteria(Customer.class);
        criteria.add(Restrictions.in("name", name));
        return criteria.list();
	}		
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<Customer> getCustomersByCustomerNo(String[] customerNo) {            
        Criteria criteria = getSession().createCriteria(Customer.class);
        criteria.add(Restrictions.in("customerNo", customerNo));
        return criteria.list();
    }   
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public Customer getCustomersByLoginId(String loginId) {
        Criteria criteria = getSession().createCriteria(Customer.class);
        criteria.add(Restrictions.eq("loginId", loginId));
        List<Customer> list =  criteria.list();
    	
    	if(list !=null && list.size()>0)
    		return (Customer)list.get(0);
    	else return null; 
	}

	public int idOverlapCheck(String customerNo) {
		Query query = getSession().createQuery("SELECT COUNT(c.customerNo) FROM Customer c WHERE c.customerNo = " + customerNo + " ");
		return DataAccessUtils.intResult(query.list());		
	}
	
    public int loginIdOverlapCheck(String loginId, String customerNo) {
        if(customerNo == null) {
        	Query query = getSession().createQuery("SELECT COUNT(o.loginId) FROM Operator o WHERE o.loginId = '" + loginId + "' ");
            return DataAccessUtils.intResult(query.list());   
        } else {
        	Query query = getSession().createQuery("SELECT COUNT(c.loginId) FROM Customer c WHERE c.loginId = " + customerNo + " AND c.customerNo <> ? ");
            return DataAccessUtils.intResult(query.list());
        }
    }

	/**
	 * method name : checkCustomerNoLoginMapping
     * method Desc : 입력받은 CustomerNumber가 다른 customer의 Login아이디와 매핑되어있는지 체크한다.
     * 
     * @param customerNo
     * @return
     */
    public List<Map<String, String>> checkCustomerNoLoginMapping(String customerNo) {
    	StringBuilder sb = new StringBuilder();
		sb.append("SELECT c.loginId as LOGINID, c.name as NAME, c.email as EMAIL, c.telNo as TELNO FROM Customer c WHERE c.customerNo = :customerNo");

		Query query = getSession().createQuery(sb.toString());
		query.setString("customerNo",customerNo);

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
	
	public Integer getNextId() {
		String qu =
				"SELECT Max(id) FROM Customer";
		Query query = getSession().createQuery(qu);
		return (Integer) query.uniqueResult() + 1;
	}
	
	public List<Customer> customerSearchList(Set<Condition> set) {
	    return findByConditions(set);
	}

	public int customerSearchListCount(String customerNo, String name) {
		Query query = getSession().createQuery("SELECT COUNT(id) FROM Customer WHERE name like " + "%" + name + "%" + " and customerNo like " + "%" + customerNo + "%" + " ");
		return DataAccessUtils.intResult(query.list());		
	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public Integer getTotalCustomer(Map<String, Object> conditionMap) {
		String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
		String customerNo = (String)conditionMap.get("customerNo");
		String customerName = (String)conditionMap.get("customerName");
		String locationId = (String)conditionMap.get("location");
		String mdsId = (String)conditionMap.get("mdsId");
		String sicId = (String)conditionMap.get("customerType");
        String[] sicIds = sicId.split(",");
        List<Integer> sicIdList = new ArrayList<Integer>();
        
        for (String obj : sicIds) {
            if (!obj.equals("") && !obj.equals("null")) {
                sicIdList.add(Integer.valueOf(obj));
            }
        }

		String address = (String)conditionMap.get("address");
		String serviceType = (String)conditionMap.get("serviceType");
		String supplierId = (String)conditionMap.get("supplierId");
		List<Integer> locationIdList = (List<Integer>)conditionMap.get("locationIdList");

		StringBuilder sb = new StringBuilder()
		.append("SELECT COUNT(DISTINCT cust.id) ")
		.append("FROM CUSTOMER cust ")
		.append("     LEFT OUTER JOIN ") 
		.append("     CONTRACT cont ")
		.append("     ON  cust.ID = cont.CUSTOMER_ID ")
        .append("     AND cont.SUPPLIER_ID = :supplierId ")
		.append("     LEFT OUTER JOIN METER meter ON cont.METER_ID = meter.ID ")
		.append("     LEFT OUTER JOIN code cd1 ON cont.sic_id = cd1.id ")
		.append("WHERE 1=1 ")
		.append("AND cust.SUPPLIER_ID = :supplierId ");
		
		if(!contractNumber.isEmpty()) sb.append("AND cont.contract_number =:contractNumber");
		if(!"".equals(customerNo)) sb.append("AND cust.customerNo like :customerNo ");
		if(!"".equals(customerName)) sb.append("AND cust.NAME LIKE :customerName ");
        if(locationIdList != null) sb.append("AND cont.LOCATION_ID IN (:locationIdList) ");
		if(!"".equals(address)) sb.append("AND cust.ADDRESS like :address ");
		if(!"".equals(serviceType) && !serviceType.equals("null")) sb.append("AND cont.SERVICETYPE_ID = :serviceType ");
		if(!"".equals(mdsId)) sb.append("AND meter.MDS_ID like :mdsId ");
//			if(!"".equals(sicId) && !sicId.equals("null")) sb.append("AND cont.sic_id = :sicId ");
        if(sicIdList.size() > 0) sb.append("AND cont.sic_id IN (:sicIdList) ");

		SQLQuery query = getSession().createSQLQuery(sb.toString());	
		query.setInteger("supplierId", Integer.valueOf(supplierId));
		
		if(!contractNumber.isEmpty()) query.setString("contractNumber", contractNumber);
		if(!"".equals(customerNo)) query.setString("customerNo", "%"+customerNo+"%");
		if(!"".equals(customerName)) query.setString("customerName", "%"+customerName+"%");
		if(locationIdList != null) query.setParameterList("locationIdList", locationIdList);
		if(!"".equals(address)) query.setString("address", "%" + address + "%");
		if(!"".equals(serviceType) && !serviceType.equals("null")) query.setInteger("serviceType", Integer.parseInt(serviceType));
		if(!"".equals(mdsId)) query.setString("mdsId", "%"+mdsId+"%");
		//if(!"".equals(sicId) && !sicId.equals("null")) query.setInteger("sicId", Integer.parseInt(sicId));
		if(sicIdList.size() > 0) query.setParameterList("sicIdList", sicIdList);
		
		Number cnt = (Number)query.uniqueResult();
		
		return cnt.intValue();
	}

//		public void updateCustomer(Customer c) {
//			System.out.println("daoimpl : " + c);
//			StringBuffer sb = new StringBuffer();
//			sb.append("UPDATE Customer c ")
//			.append("SET c.customerNo = ? ")
//			.append(", c.name = ? ")
//			.append(", c.loginId = ? ")
//			//.append(", c.passwd = ? ")
//			.append(", c.mobileNo = ? ")
//			.append(", c.telephoneNo = ? ")
//			.append(", c.address = ? ")
//			.append(", c.address1 = ? ")
//			.append(", c.address2 = ? ")
//			.append(", c.address3 = ? ")
//			.append(", c.familyCnt = ? ")
//			.append(", c.co2MileId = ? ")
//			.append(", c.email = ? ")
//			.append(", c.smsYn = ? ")
//			.append(", c.emailYn = ? ")
//			.append(", c.demandResponse = ?")
//			//남아공 추가필드
//			.append(", c.identityOrCompanyRegNo = ? ")
//			.append(", c.initials = ? ")
//			.append(", c.vatNo = ? ")
//			.append(", c.workTelephone = ? ")
//			.append(", c.postalAddressLine1 = ? ")
//			.append(", c.postalAddressLine2 = ? ")
//			.append(", c.postalSuburb = ? ")
//			.append(", c.postalCode = ? ")
//			;
//			
//			sb.append("WHERE c.id = ? ");
//
//			//HQL문을 이용한 CUD를 할 경우에는 getHibernateTemplate().bulkUpdate() 메소드를 사용한다.		
//			//Object[] objects = new Object[]{}; 
//            this.getHibernateTemplate().bulkUpdate(sb.toString(), new Object[] {c.getCustomerNo(), c.getName(), c.getLoginId(), c.getMobileNo(), c.getTelephoneNo(),
//                c.getAddress(), c.getAddress1(), c.getAddress2(), c.getAddress3(), c.getFamilyCnt(), c.getCo2MileId(), c.getEmail(), c.getSmsYn(), c.getEmailYn(), c.getDemandResponse(), c.getIdentityOrCompanyRegNo(), c.getInitials(), c.getVatNo(), c.getWorkTelephone(), c.getPostalAddressLine1(), c.getPostalAddressLine2(), c.getPostalSuburb(), c.getPostalCode(), c.getId()} );
//		}
	

	/**
	 * method name : getDemandResponseCustomerList
	 * method Desc : DR고객 리스트 취득
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<Customer> getDemandResponseCustomerList() {            
        Criteria criteria = getSession().createCriteria(Customer.class);
        criteria.add(Restrictions.eq("demandResponse", true));
        // 서비스 타입 조건 추가여부 고려
        return criteria.list();
    }
	
	public Integer getCustomerCount(Map<String, String> condition) {
		Integer supplierId = Integer.parseInt(condition.get("supplierId"));
		
		Criteria criteria = getSession().createCriteria(Customer.class);
		
		criteria.setProjection(Projections.rowCount());
		
		if (supplierId != null)
            criteria.add(Restrictions.eq("supplier.id", supplierId));
		
		return ((Number)criteria.uniqueResult()).intValue();
	}
	
	
    /**
     * method name : getCustomerListByRole<b/>
     * method Desc : User Management 가젯에서 Operator(Customer) List 를 조회한다. 
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getCustomerListByRole(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
        Integer roleId = (Integer)conditionMap.get("roleId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT c.id AS id, ");
            sb.append("\n       c.loginId AS loginId, ");
            sb.append("\n       c.name AS name, ");
            sb.append("\n       c.telNo AS telNo, ");
            sb.append("\n       c.email AS email, ");
            sb.append("\n       c.loginDenied AS loginDenied, ");
            sb.append("\n       lo.name AS location ");
        }

        sb.append("\nFROM Customer c ");
        sb.append("\n     LEFT OUTER JOIN c.location lo ");
        sb.append("\nWHERE c.role.id = :roleId ");

        if (!isCount) {
            sb.append("\nORDER BY lower(c.loginId) ");
        }

        Query query = getSession().createQuery(sb.toString());

        query.setInteger("roleId", roleId);

        if (isCount) {
            Map<String, Object> map = new HashMap<String, Object>();
            Number count = (Number)query.uniqueResult();
            map.put("total", count.intValue());
            result = new ArrayList<Map<String, Object>>();
            result.add(map);
        } else {
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }

        return result;
    }
}
