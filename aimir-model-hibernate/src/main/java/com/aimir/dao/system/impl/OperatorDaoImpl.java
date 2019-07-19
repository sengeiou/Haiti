/**
 * OperatorDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.LoginStatus;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.model.system.Operator;
import com.aimir.util.StringUtil;

/**
 * OperatorDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 6.   v1.0       김상연         주민등록번호 정보 추출
 * 2011. 4. 14.  v1.1       김상연         Operator 조회 (조건 : Operator)
 *
 */
@Repository(value = "operatorDao")
public class OperatorDaoImpl extends AbstractHibernateGenericDao<Operator, Integer> implements OperatorDao {

    Log logger = LogFactory.getLog(OperatorDaoImpl.class);
    
	@Autowired
	protected OperatorDaoImpl(SessionFactory sessionFactory) {
		super(Operator.class);
		super.setSessionFactory(sessionFactory);
	}

	public boolean checkOperator(int userId, String pw){
    	boolean result = false;

		Query query = getSession().createQuery("from Operator where loginId = :userId");
		query.setInteger("userId", userId);
    	
		Operator operator = (Operator)query.list();
    	// Operator operator = (Operator) getHibernateTemplate().find("from Operator where loginId = ?", userId);
        
        if (operator != null) {
        	if(operator.getPassword().equals(pw))
        		result = true;
        }
        
        return result;
	}
	
	@SuppressWarnings("unchecked")
	public Operator getOperatorByLoginId(String loginId) {
    	logger.debug("loginId : " + loginId);
    	
		Query query = getSession().createQuery("FROM Operator WHERE loginId =:loginId");
		query.setString("loginId", loginId);
    	
		List<Operator> list = query.list();
    	// List<Operator> list = (List<Operator>)getHibernateTemplate().find("FROM Operator WHERE loginId =?", loginId);
    	
    	if(list !=null && list.size()>0)
    		return (Operator)list.get(0);
    	else return null; 
	}
    
	@SuppressWarnings("unused")
	private boolean isEmpty(String val) {
		return (val == null || val.trim().length() == 0);
	}

    @Deprecated
	@SuppressWarnings("unchecked")
	public List<Operator> getOperatorsByRole(Integer roleId) {
		if(roleId==null) {
			return null;
		}
		//2011.05.25 jhkim 처음 로딩시 10개만 화면에 출력으로 변경
		Criteria criteria = getSession().createCriteria(Operator.class);
		criteria.add(Restrictions.eq("role.id", roleId));
		criteria.addOrder(Order.asc("loginId").ignoreCase()); // 나중에 입력된 최근 글부터 정렬
		criteria.setFirstResult(0); 
		criteria.setMaxResults(10); // 한번에 불러올 리스트 크기를 정의
		return criteria.list();	
		
//		DetachedCriteria criteria = DetachedCriteria.forClass(Operator.class);
//		criteria.add(Restrictions.eq("role.id", roleId));
//		criteria.addOrder(Order.asc("name"));		
//		return getHibernateTemplate().findByCriteria(criteria);
	}

    /**
     * method name : getOperatorListByRole<b/>
     * method Desc : User Management 가젯에서 Operator List 를 조회한다. 
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getOperatorListByRole(Map<String, Object> conditionMap, boolean isCount) {
        List<Map<String, Object>> result;
        Integer roleId = (Integer)conditionMap.get("roleId");
        Integer page = (Integer)conditionMap.get("page");
        Integer limit = (Integer)conditionMap.get("limit");

        StringBuilder sb = new StringBuilder();

        if (isCount) {
            sb.append("\nSELECT COUNT(*) AS cnt ");
        } else {
            sb.append("\nSELECT op.id AS id, ");
            sb.append("\n       op.loginId AS loginId, ");
            sb.append("\n       op.name AS name, ");
            sb.append("\n       op.telNo AS telNo, ");
            sb.append("\n       op.email AS email, ");
            sb.append("\n       op.loginDenied AS loginDenied, ");
            sb.append("\n       lo.name AS location ");
        }

        sb.append("\nFROM Operator op ");
        sb.append("\n     LEFT OUTER JOIN op.location lo ");
        sb.append("\nWHERE op.role.id = :roleId ");

        if (!isCount) {
            sb.append("\nORDER BY lower(op.loginId) ");
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

	@SuppressWarnings("unchecked")
	public List<Operator> getOperatorsHaveNoSupplier() {
		Query query = getSession().createQuery("FROM Operator WHERE supplier_id IS NULL");
		return query.list();
		// return (List<Operator>)getHibernateTemplate().find("FROM Operator WHERE supplier_id IS NULL");
	}
	
	@SuppressWarnings("unchecked")
	public List<Operator> getOperatorsHaveNoRole(Integer supplierId) {
		if(supplierId==null)
			return  null;
		
		Criteria criteria = getSession().createCriteria(Operator.class);
		criteria.add(Restrictions.eq("supplier.id", supplierId));
		criteria.add(Restrictions.isNull("role"));
		
		return criteria.list();
		
		/*DetachedCriteria criteria = DetachedCriteria.forClass(Operator.class);
		criteria.add(Restrictions.eq("supplier.id", supplierId));
		criteria.add(Restrictions.isNull("role"));
		
		return (List<Operator>)getHibernateTemplate().findByCriteria(criteria);*/
	}

	public boolean checkDuplicateLoginId(String loginId) {
		Criteria criteria = getSession().createCriteria(Operator.class);
		criteria.add(Restrictions.eq("loginId", loginId));
		
		if (criteria.list().size() > 0) {
			return false;
		} else {
			return true;
		}
	}
	
	@SuppressWarnings("unchecked")
	@SuppressAjWarnings("unchecked")
	public List<Operator> getVendorByLoginIdAndName(int page, int limit, String loginId, String name, Integer supplierId, String supplierName) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\nFROM Operator op ");
        sb.append("\nWHERE 1=1 ");
        
		Criteria criteria = getSession().createCriteria(Operator.class);
		if (loginId != null && !loginId.equals("")) {
			sb.append("\nAND op.loginId like '%"+loginId+"%' ");
		}
		if (name != null && !name.equals("")) {
			sb.append("\nAND op.loginId like '%"+name+"%' ");
		}
		if (supplierId != null) {
			sb.append("\nAND op.supplierId = "+supplierId);
		}
		sb.append("\nAND (op.role.name = 'vendor' or op.role.name like '%^_vendor' escape '^')");
		Query query = getSession().createQuery(sb.toString());
		query.setFirstResult((page - 1) * limit);
		query.setMaxResults(limit);
		return query.list();
	}
	
	@SuppressAjWarnings("unchecked")
	public int getVendorCountByLoginIdAndName(String loginId, String name, Integer supplierId, String supplierName) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\nFROM Operator op ");
        sb.append("\nWHERE 1=1 ");
        
		Criteria criteria = getSession().createCriteria(Operator.class);
		if (loginId != null && !loginId.equals("")) {
			sb.append("\nAND op.loginId like '%"+loginId+"%' ");
		}
		if (name != null && !name.equals("")) {
			sb.append("\nAND op.loginId like '%"+name+"%' ");
		}
		if (supplierId != null) {
			sb.append("\nAND op.supplierId = "+supplierId);
		}
		sb.append("\nAND (op.role.name = 'vendor' or op.role.name like '%^_vendor' escape '^')");
		Query query = getSession().createQuery(sb.toString());
		return query.list().size();
	}	
	
	public List<Map<String, Object>> getOperatorListByRoleType(Map<String, Object> params) {
        String roleType = StringUtil.nullToBlank(params.get("roleType"));
        Integer supplierId = (Integer) params.get("supplierId");
        StringBuilder sb = new StringBuilder();
        
        sb.append("\nSELECT op.loginId as loginId ");
        sb.append("\nFROM Operator op ");
        sb.append("\nWHERE op.role.name=:roleType ");
        sb.append("\nAND op.supplierId=:supplierId ");
        
        Query query = getSession().createQuery(sb.toString());
        
        query.setString("roleType", roleType);
        query.setInteger("supplierId", supplierId);
        
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();      
    }
	
	@SuppressWarnings("unchecked")
	public List<Operator> getOperators(int page, int count, int roleId) {
		int pageSize = count;
		Criteria criteria = getSession().createCriteria(Operator.class);
		criteria.add(Restrictions.eq("role.id", roleId));
		criteria.addOrder(Order.desc("id")); // 나중에 입력된 최근 글부터 정렬
		criteria.setFirstResult((page - 1) * pageSize); // page는 1부터 2,3... 하지만
		// 이는 입력 되기를 0번 글부터 시작되고
		// pageSize단위로 곱해준다.
		criteria.setMaxResults(pageSize); // 한번에 불러올 리스트 크기를 정의
		return criteria.list();
	}

	public Integer count(int roleId) {
		Criteria criteria = getSession().createCriteria(Operator.class);
		criteria.add(Restrictions.eq("role.id", roleId));
		criteria.setProjection(Projections.rowCount());
		Long count = ((Number) criteria.uniqueResult()).longValue();
		return count.intValue();
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.system.OperatorDao#getLoginLogGrid(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getLoginLogGrid(Map<String, Object> condition) {

		List<Object> gridData 	 = new ArrayList<Object>();
		List<Object> result		 = new ArrayList<Object>();
		StringBuilder sbQuery 	 = new StringBuilder();

		String roleId            = StringUtil.nullToBlank(condition.get("roleId"));
		String loginId           = StringUtil.nullToBlank(condition.get("loginId"));   
		String ipAddr            = StringUtil.nullToBlank(condition.get("ipAddr"));

		boolean login            = Boolean.parseBoolean(StringUtil.nullToBlank(condition.get("login")));		                                           
		boolean logOut           = Boolean.parseBoolean(StringUtil.nullToBlank(condition.get("logOut")));
		boolean loginFail        = Boolean.parseBoolean(StringUtil.nullToBlank(condition.get("loginFail")));

		String searchStartDate   = StringUtil.nullToBlank(condition.get("searchStartDate"));   
		String searchEndDate     = StringUtil.nullToBlank(condition.get("searchEndDate"));

		String curPage   		= StringUtil.nullToBlank(condition.get("curPage"));

		boolean isCustomerRole = condition.get("roleName").equals("customer") ? true : false;
		
		sbQuery.append(" SELECT loginLog.LOGIN_ID     as userId 		\n");
		
		if(isCustomerRole){
			sbQuery.append("  , cu.NAME              as userName 		\n");
			sbQuery.append("  , 'customer'           as userGroup 		\n");
		}else{
			sbQuery.append("  , opr.NAME              as userName 		\n");
			sbQuery.append("  , role.Name             as userGroup 		\n");				   
		}

		sbQuery.append("      , loginLog.IP_ADDR      as ipAddr 		\n");
		sbQuery.append("      , loginLog.LOGIN_DATE   as loginTime 		\n");
		sbQuery.append("      , loginLog.LOGOUT_DATE  as logouTime 		\n");
		sbQuery.append("      , loginLog.STATUS       as status 		\n");
		sbQuery.append("   FROM LOGIN_LOG loginLog 						\n");
		
		if(isCustomerRole){
			sbQuery.append("   JOIN CUSTOMER cu                         \n");
			sbQuery.append("     ON (loginLog.LOGIN_ID = cu.LOGINID)    \n");
		}else{
			sbQuery.append("   JOIN OPERATOR opr                        \n");
			sbQuery.append("     ON (loginLog.OPERATOR_ID = opr.ID)     \n");
			sbQuery.append("   JOIN ROLE role                           \n");
			sbQuery.append("     ON (opr.ROLE_ID = role.ID)			    \n");			
		}
		

		sbQuery.append("  WHERE 1=1            						    \n");

		//if(!roleId.equals(""))
		if(!roleId.equals("") && !isCustomerRole)
			sbQuery.append("     AND role.ID = "+ roleId );

		if(!loginId.equals(""))
			sbQuery.append("     AND loginLog.LOGIN_ID LIKE '%"+ loginId +"%'"); 

		if(!ipAddr.equals(""))
			sbQuery.append("     AND loginLog.IP_ADDR LIKE '%"+ ipAddr +"%'");

		if(login || logOut || loginFail){			
			sbQuery.append("     AND ( 1=0      ");

			if(login)
				sbQuery.append("     OR loginLog.STATUS = " + LoginStatus.LOGIN.getIntValue());

			if(logOut)
				sbQuery.append("     OR loginLog.STATUS = " + LoginStatus.LOGOUT.getIntValue());

			if(loginFail){
				sbQuery.append("     OR loginLog.STATUS = "+ LoginStatus.INVALID_ID.getIntValue());
				sbQuery.append("     OR loginLog.STATUS = "+ LoginStatus.INVALID_PASSWORD.getIntValue());
				sbQuery.append("     OR loginLog.STATUS = "+ LoginStatus.NOT_ALLOWED_IPADDR.getIntValue());
				sbQuery.append("     OR loginLog.STATUS = "+ LoginStatus.LOGIN_IS_DENIED.getIntValue()); 
			}

		    sbQuery.append("         )      ");
		}

		if(!searchStartDate.equals(""))
			sbQuery.append("     AND loginLog.LOGIN_DATE > '"+ searchStartDate + "000000'");
		if(!searchEndDate.equals(""))
			sbQuery.append("     AND loginLog.LOGIN_DATE < '"+ searchEndDate + "235959'");

		StringBuffer sbQueryData = new StringBuffer();
		sbQueryData.append(sbQuery);
		sbQueryData.append(" order by 5 DESC ");
		SQLQuery query = getSession().createSQLQuery(sbQueryData.toString());

		// Paging
		int rowPerPage = CommonConstants.Paging.ROWPERPAGE.getPageNum();
		int firstIdx  = Integer.parseInt(curPage) * rowPerPage;

		query.setFirstResult(firstIdx);
		query.setMaxResults(rowPerPage);

		List<Object> dateList = null;
		dateList = query.list();

		// 전체 건수
		StringBuffer countQuery = new StringBuffer();
		countQuery.append("\n SELECT COUNT(countTotal.userId) ");
		countQuery.append("\n FROM (  ");
		countQuery.append(sbQuery);
		countQuery.append("\n ) countTotal ");

		SQLQuery countQueryObj = getSession().createSQLQuery(countQuery.toString());
		Number totalCount = (Number)countQueryObj.uniqueResult();
		
		result.add(totalCount.toString());

		// 실제 데이터
		int dataListLen = 0;
		if(dateList != null)
			dataListLen= dateList.size();
		
		for(int i=0 ; i < dataListLen ; i++) {

			HashMap<String, Object> chartDataMap = new HashMap<String, Object>();
			Object[] resultData = (Object[]) dateList.get(i);

			chartDataMap.put("no",          firstIdx+i+1 );                       
			chartDataMap.put("userId",     	resultData[0]);                 
			chartDataMap.put("userName",    resultData[1]);
			chartDataMap.put("userGroup",   resultData[2]);
			chartDataMap.put("ipAddr",   	resultData[3]);
			chartDataMap.put("loginTime",   resultData[4]);
			chartDataMap.put("logoutTime",  resultData[5]);

			//Todo : 아래 이것들좀 어떻게... - 곽재원
			/*
			if(resultData[6].toString().equals(Integer.toString(LoginStatus.LOGIN.getIntValue())))
				chartDataMap.put("status",    	LoginStatus.LOGIN.name());
			else if( resultData[6].toString().equals(Integer.toString(LoginStatus.LOGOUT.getIntValue())))			
				chartDataMap.put("status",    	LoginStatus.LOGOUT.name());			
			else if( resultData[6].toString().equals(Integer.toString(LoginStatus.INVALID_ID.getIntValue())))			
				chartDataMap.put("status",    	LoginStatus.INVALID_ID.name());			
			else if( resultData[6].toString().equals(Integer.toString(LoginStatus.INVALID_PASSWORD.getIntValue())))			
				chartDataMap.put("status",    	LoginStatus.INVALID_PASSWORD.name());
			else if( resultData[6].toString().equals(Integer.toString(LoginStatus.NOT_ALLOWED_IPADDR.getIntValue())))			
				chartDataMap.put("status",    	LoginStatus.NOT_ALLOWED_IPADDR.name());
			*/

	    	logger.debug("loginstatus : " + resultData[6]);
		    for (LoginStatus _loginStatus : LoginStatus.values()) {
		        if (_loginStatus.getCode().equals(Integer.valueOf(resultData[6].toString()))) {
					chartDataMap.put("status", _loginStatus.name());
		        }
		    }
			gridData.add(chartDataMap);

		}

		result.add(gridData);

		return result;	
	}
	
	
	/**
	 * @Desc : all login log grid data fetch
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public List getLoginLogGrid2(Map<String, Object> condition) {

		List gridData 	 = new ArrayList();
		List<Object> result		 = new ArrayList<Object>();
		StringBuffer sbQuery 	 = new StringBuffer();

		String roleId            = StringUtil.nullToBlank(condition.get("roleId"));
		String loginId           = StringUtil.nullToBlank(condition.get("loginId"));   
		String ipAddr            = StringUtil.nullToBlank(condition.get("ipAddr"));
		
		String LoginStatusCheckedValue            =String.valueOf(condition.get("loginStatusCheckedValue"));
		
		String[] arrloginStatus= LoginStatusCheckedValue.split("@");
		
	
		
		for ( int i=0 ; i < arrloginStatus.length;i++)
		{
			if ( arrloginStatus[i].equals("loginLogLogin") )
				condition.put("login", true);
			else if ( arrloginStatus[i].equals("loginLogLogOut") )
				condition.put("logOut", true);
			else if ( arrloginStatus[i].equals("loginLogLoginFail") )
				condition.put("loginFail", true);
		}
		

		boolean login            = Boolean.parseBoolean(StringUtil.nullToBlank(condition.get("login")));		                                           
		boolean logOut           = Boolean.parseBoolean(StringUtil.nullToBlank(condition.get("logOut")));
		boolean loginFail        = Boolean.parseBoolean(StringUtil.nullToBlank(condition.get("loginFail")));
		
		//search_from=20120819@20120821
		//서치 date 설정 부분 
		
		String search_from =  (String) condition.get("search_from");
		
		String[] search_froms = search_from.split("@");
		
		String searchStartDate   = search_froms[0];   
		String searchEndDate     = search_froms[1];

		/*String searchStartDate   = StringUtil.nullToBlank(condition.get("searchStartDate"));   
		String searchEndDate     = StringUtil.nullToBlank(condition.get("searchEndDate"));*/

		
//		String curPage   		= StringUtil.nullToBlank(condition.get("curPage"));

		sbQuery.append(" SELECT loginLog.LOGIN_ID     as userId 		\n")
			   .append("      , opr.NAME              as userName 		\n")
			   .append("      , role.Name             as userGroup 		\n")
			   .append("      , loginLog.IP_ADDR      as ipAddr 		\n")
			   .append("      , loginLog.LOGIN_DATE   as loginTime 		\n")
			   .append("      , loginLog.LOGOUT_DATE  as logouTime 		\n")
			   .append("      , loginLog.STATUS       as status 		\n")
			   .append("   FROM LOGIN_LOG loginLog 						\n")
			   .append("   JOIN OPERATOR opr                            \n")
			   .append("     ON (loginLog.OPERATOR_ID = opr.ID)         \n")
			   .append("   JOIN ROLE role                               \n")
			   .append("     ON (opr.ROLE_ID = role.ID)			        \n")
			   .append("  WHERE 1=1            						    \n");

		if(!roleId.equals(""))
			sbQuery.append("     AND role.ID = "+ roleId );

		if(!loginId.equals(""))
			sbQuery.append("     AND loginLog.LOGIN_ID LIKE '%"+ loginId +"%'"); 

		if(!ipAddr.equals(""))
			sbQuery.append("     AND loginLog.IP_ADDR LIKE '%"+ ipAddr +"%'");

		if(login || logOut || loginFail){			
			sbQuery.append("     AND ( 1=0      ");

			if(login)
				sbQuery.append("     OR loginLog.STATUS = " + LoginStatus.LOGIN.getIntValue());

			if(logOut)
				sbQuery.append("     OR loginLog.STATUS = " + LoginStatus.LOGOUT.getIntValue());

			if(loginFail){
				sbQuery.append("     OR loginLog.STATUS = "+ LoginStatus.INVALID_ID.getIntValue());
				sbQuery.append("     OR loginLog.STATUS = "+ LoginStatus.INVALID_PASSWORD.getIntValue());
				sbQuery.append("     OR loginLog.STATUS = "+ LoginStatus.NOT_ALLOWED_IPADDR.getIntValue());
				sbQuery.append("     OR loginLog.STATUS = "+ LoginStatus.LOGIN_IS_DENIED.getIntValue());
			}

		    sbQuery.append("         )      ");
		}

		if(!searchStartDate.equals(""))
			sbQuery.append("     AND loginLog.LOGIN_DATE > '"+ searchStartDate + "000000'");
		if(!searchEndDate.equals(""))
			sbQuery.append("     AND loginLog.LOGIN_DATE < '"+ searchEndDate + "235959'");

		StringBuffer sbQueryData = new StringBuffer();
		sbQueryData.append(sbQuery);
		sbQueryData.append(" order by 5 DESC ");
		SQLQuery query = getSession().createSQLQuery(sbQueryData.toString());

		
		
		
		List<Object> dateList = null;
		dateList = query.list();

		// 전체 건수
		StringBuffer countQuery = new StringBuffer();
		countQuery.append("\n SELECT COUNT(countTotal.userId) ");
		countQuery.append("\n FROM (  ");
		countQuery.append(sbQuery);
		countQuery.append("\n ) countTotal ");

		SQLQuery countQueryObj = getSession().createSQLQuery(countQuery.toString());
		Number totalCount = (Number)countQueryObj.uniqueResult();
		
		result.add(totalCount.toString());

		// 실제 데이터
		int dataListLen = 0;
		if(dateList != null)
			dataListLen= dateList.size();
		
		for(int i=0 ; i < dataListLen ; i++) 
		{

			HashMap<String, Object> chartDataMap = new HashMap<String, Object>();
			Object[] resultData = (Object[]) dateList.get(i);

			chartDataMap.put("no",          i );                       
			chartDataMap.put("userId",     	resultData[0]);                 
			chartDataMap.put("userName",    resultData[1]);
			chartDataMap.put("userGroup",   resultData[2]);
			chartDataMap.put("ipAddr",   	resultData[3]);
			chartDataMap.put("loginTime",   resultData[4]);
			chartDataMap.put("logoutTime",  resultData[5]);

		
	    	logger.debug("loginstatus : " + resultData[6]);
		    for (LoginStatus _loginStatus : LoginStatus.values()) {
		        if (_loginStatus.getCode().equals(Integer.valueOf(resultData[6].toString()))) {
					chartDataMap.put("status", _loginStatus.name());
		        }
		    }
		    
		    
			gridData.add(chartDataMap);

		}

		

		return gridData;
		
		/*return result;	*/
	}
	
	/**
	 * 아이디로 검색하여 해당 Operation 객체 반환
	 */
	@SuppressWarnings("unchecked")
	public Operator getOperatorById(Integer operatorId) {
		Query query = getSession().createQuery("FROM Operator WHERE id = :operatorId");
		query.setInteger("operatorId", operatorId);
		List<Operator> list = query.list();
		
		// List<Operator> list = (List<Operator>)getHibernateTemplate().find("FROM Operator WHERE id = ?", operatorId);
    	if(list !=null && list.size()>0)
    		return (Operator)list.get(0);
    	else return null; 
	}

	/**
	 * 그룹 관리 중 멤버 리스트 조회
	 * 
	 * @param condition
	 * @return
	 */
    @SuppressWarnings("unchecked")
    @Deprecated
	public List<Object> getGroupMember(Map<String, Object> condition){
    	String member = StringUtil.nullToBlank(condition.get("member"));

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT t.id, t.name ")
		  .append("FROM OPERATOR t LEFT JOIN GROUP_MEMBER g ON t.name = g.member ")
		  .append("WHERE t.supplier_id = :supplierId ");
		if(!"".equals(member)){
			sb.append("AND t.name like '%").append((String)condition.get("member")).append("%'");
		}
		sb.append("AND t.name NOT IN ( ");
			sb.append("SELECT t.name ");
			sb.append("FROM OPERATOR t RIGHT JOIN GROUP_MEMBER g ON t.name = g.member ");
			sb.append("WHERE t.supplier_id = :supplierId ");
		sb.append(") ");
		sb.append(" ORDER BY t.name ASC");

		SQLQuery query = getSession().createSQLQuery(sb.toString());
		return query.setInteger("supplierId", Integer.parseInt((String)condition.get("supplierId")))
					.list();
    }

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : Group Management 가젯에서 Member 로 등록 가능한 Operator 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer groupId = (Integer)conditionMap.get("groupId");
        String memberName = StringUtil.nullToBlank(conditionMap.get("memberName"));
        
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT op.id AS value, ");
        sb.append("\n       op.name AS text, ");
        sb.append("\n       'Operator' AS type ");
        sb.append("\nFROM Operator op ");
        sb.append("\nWHERE op.supplier.id = :supplierId ");

        if (!memberName.isEmpty()) {
            sb.append("\nAND   op.name LIKE :memberName ");
        }

        sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    SELECT 'X' ");
        sb.append("\n    FROM GroupMember gm ");
        sb.append("\n    WHERE gm.member = op.name ");
        sb.append("\n    AND   gm.aimirGroup.id = :groupId ");
        sb.append("\n) ");
        sb.append("\nORDER BY op.name ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", supplierId);
        query.setInteger("groupId", groupId);
        if (!memberName.isEmpty()) {
            query.setString("memberName", "%" + memberName + "%");
        }

        return query.list();
    }

	/* (non-Javadoc)
	 * @see com.aimir.dao.system.OperatorDao#getOperatorByPucNumber(java.lang.String)
	 */
	public Operator getOperatorByPucNumber(String pucNumber) {
		return findByCondition("pucNumber", pucNumber);
	}

	/* (non-Javadoc)
	 * @see com.aimir.dao.system.OperatorDao#getOperatorByOperator(com.aimir.model.system.Operator)
	 */
	@SuppressWarnings("unchecked")
	public List<Operator> getOperatorByOperator(Operator operator) {

		Criteria criteria = getSession().createCriteria(Operator.class);
		
		if (operator != null) {
			if (operator.getId() != null) {
				
				criteria.add(Restrictions.eq("id", operator.getId()));
			}
			
			if (operator.getPucNumber() != null) {
				
				criteria.add(Restrictions.eq("pucNumber", operator.getPucNumber()));
			}
			
			if (operator.getName() != null) {
				
				criteria.add(Restrictions.eq("name", operator.getName()));
			}
			
			if (operator.getOperatorStatus() != null) {
				
				criteria.add(Restrictions.eq("operatorStatus", operator.getOperatorStatus()));
			}
			
			if (operator.getLoginId() != null) {
				
				criteria.add(Restrictions.eq("loginId", operator.getLoginId()));
			}
		}
		
		List<Operator> operators = criteria.list();
		
		return operators;
	}
	
	public int chargeDeposit(String vendorId, Double amount) {			
		StringBuilder sb = new StringBuilder();
		
		sb.append("update OPERATOR ");
		sb.append("set DEPOSIT = DEPOSIT + :amount ");
		sb.append("where LOGINID = :loginId");		
		Query query = getSession().createSQLQuery(sb.toString());
		query.setDouble("amount", amount);
		query.setString("loginId", vendorId);
		return query.executeUpdate();
	}
	
	
    /**
     * method name : getOperatorByName<b/>
     *
     * @param String name
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Operator> getOperatorByName(String name) {

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT o.id AS id, ");
        sb.append("\n       o.aliasName AS aliasName, ");
        sb.append("\nFROM Operator o ");
        sb.append("\nWHERE o.name = :name ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("name", name);
        
        return query.list();
    }
    
    public List<Map<String,Object>> getLoginId() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("\nSELECT o.id AS id,");
    	sb.append("\n       o.loginId AS loginId");
    	sb.append("\nFROM Operator o");
    	
    	Query query = getSession().createQuery(sb.toString());
    	return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

}