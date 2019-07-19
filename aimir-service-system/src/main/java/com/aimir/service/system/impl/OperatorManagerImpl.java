/**
 * OperatorManagerImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.service.system.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.LoginLogDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.RoleDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.membership.OperatorContractDao;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.LoginLog;
import com.aimir.model.system.Operator;
import com.aimir.model.system.OperatorContract;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.User;
import com.aimir.service.system.OperatorManager;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * OperatorManagerImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 6.   v1.0       김상연         주민등록번호 가입 여부 확인
 * 2011. 4. 12.  v1.1       김상연         Operator 등록 시 Operator 반환
 * 2011. 4. 13.  v1.2       김상연         HEMS 관련 정보 수정 관련 변경분
 * 2011. 4. 14.  v1.3       김상연         Operator 조회 (조건 : Operator)
 *
 */
@WebService(endpointInterface = "com.aimir.service.system.OperatorManager")
@Service(value = "operatorManager")
@Transactional
@RemotingDestination
public class OperatorManagerImpl implements OperatorManager {
    @Autowired
    OperatorDao dao;
    
    @Autowired
    LoginLogDao logDao;
    
    @Autowired
    RoleDao roleDao;
    
    @Autowired
    OperatorContractDao operatorContractDao;
    
    @Autowired
    DayEMDao dayEMDao;
    
    @Autowired
    TariffEMDao tariffEMDao;
    
    @Autowired
    ContractDao contractDao;

    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    CustomerDao customerDao;

    public void addOperator(Operator operator) {
        dao.add(operator);
    }

    public boolean checkOperator(String userId, String pw) {
        return false;
    }

    public void deleteOperator(Operator operator) {
        dao.delete(operator);
    }

    @SuppressWarnings("static-access")
	public Operator getOperator(Integer userId) {
    	Operator operator = dao.get(userId);
    	String lastPasswordChangeTime = null;
    	if(operator != null)
    	    lastPasswordChangeTime = operator.getLastPasswordChangeTime();
 
        Supplier supplierId = operator.getSupplier();
        TimeLocaleUtil.setSupplier(supplierId);
        
    	/* 2011. 04. 13 v1.2 HEMS 관련 정보 수정 관련 변경분 DELETE START 김상연 
    	// 로케일
    	Locale locale = new Locale(
    			operator.getSupplier().getLang().getCode_2letter(),
    			operator.getSupplier().getCountry().getCode_2letter());
    	
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
        
        // 타임 로케일 유틸
        TimeLocaleUtil tlu = new TimeLocaleUtil(
        		locale, 
        		dfs.getGroupingSeparator()+"", 
        		dfs.getDecimalSeparator()+"");
    	
        // 비밀번호 변경 시간에 로케일 적용
        lastPasswordChangeTime = tlu.getLocaleDate(
        		lastPasswordChangeTime, 
        		locale.getLanguage(), 
        		locale.getCountry());
        
    	// Transient 변수에 비밀번호 변경 시간 저장
        operator.setLastPasswordChangeTimeLocale(lastPasswordChangeTime);
           2011. 04. 13 v1.2 HEMS 관련 정보 수정 관련 변경분 DELETE END 김상연 */
 
    	Locale locale;
    	
    	 // Role의 isCustomerRole을 이용하여 role을 구분한다.
    	 // by 2012.10.11 prrain
    	if(operator != null) {
    	    Role role = roleDao.get(operator.getRoleId());
        	if (role.getCustomerRole()) {
        	
            	OperatorContract operatorContract = new OperatorContract();
            	operatorContract.setOperator(operator);
            	List<OperatorContract> operatorContracts = operatorContractDao.getOperatorContract(operatorContract);
            	
            	if (operatorContracts.size() > 0) {
            		
            		OperatorContract returnOperatorContract = operatorContracts.get(0);
            		Contract contract = returnOperatorContract.getContract();
            		Supplier supplier = contract.getSupplier();
            		
            		locale = new Locale(
            				supplier.getLang().getCode_2letter(),
            				supplier.getCountry().getCode_2letter());
            	} else {
            		locale = null;
            	}
        	} else {
            	locale = new Locale(
            			operator.getSupplier().getLang().getCode_2letter(),
            			operator.getSupplier().getCountry().getCode_2letter());
        	}  
        	
        	if (locale != null) {
        		
                DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
                
                // 타임 로케일 유틸
                TimeLocaleUtil tlu = new TimeLocaleUtil(
                		locale, 
                		dfs.getGroupingSeparator()+"", 
                		dfs.getDecimalSeparator()+"");
            	
                // 비밀번호 변경 시간에 로케일 적용
                lastPasswordChangeTime = tlu.getLocaleDate(
                		lastPasswordChangeTime, 
                		locale.getLanguage(), 
                		locale.getCountry());
                
            	// Transient 변수에 비밀번호 변경 시간 저장
                operator.setLastPasswordChangeTimeLocale(lastPasswordChangeTime);
        	}
        	/* 2011. 04. 13 v1.2 HEMS 관련 정보 수정 관련 변경분 ADD END 김상연 */
    	}
    	return operator;
    }
    
    @SuppressWarnings("static-access")
    public User getUser(Integer userId, String loginId) {
        
        //loginId는 유일하기 때문에 loginId로 분별
        User user = (User)dao.getOperatorByLoginId(loginId);
        
        if(user == null) {
            user = (User)customerDao.getCustomersByLoginId(loginId);
        }
        String lastPasswordChangeTime = null;
        if(user != null)
            lastPasswordChangeTime = user.getLastPasswordChangeTime();
        Locale locale;
        
        if(user != null) {
            Role role = roleDao.get(user.getRoleId());
            
            locale = new Locale(
                    user.getSupplier().getLang().getCode_2letter(),
                    user.getSupplier().getCountry().getCode_2letter());
              
            
            if (locale != null) {
                
                DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
                
                // 타임 로케일 유틸
                TimeLocaleUtil tlu = new TimeLocaleUtil(
                        locale, 
                        dfs.getGroupingSeparator()+"", 
                        dfs.getDecimalSeparator()+"");
                
                // 비밀번호 변경 시간에 로케일 적용
                lastPasswordChangeTime = tlu.getLocaleDate(
                        lastPasswordChangeTime, 
                        locale.getLanguage(), 
                        locale.getCountry());
                
                // Transient 변수에 비밀번호 변경 시간 저장
                user.setLastPasswordChangeTimeLocale(lastPasswordChangeTime);
            }
            /* 2011. 04. 13 v1.2 HEMS 관련 정보 수정 관련 변경분 ADD END 김상연 */
        }
        return user;
    }

    public List<Operator> getOperators() {
        return dao.getAll();
    }

    public void updateOperator(Operator operator) {
        dao.update(operator);
    }

    @SuppressWarnings("unused")
    public void updateOperatorInfo(Operator operator) {
    	
        Operator oldoperator = dao.get(operator.getId());
        dao.clear();
     	String oldPassword =  oldoperator.getPassword();
     
     	if (operator.getPassword().isEmpty()) {
     		operator.setPassword(oldPassword);
     	}
        // 비번 변경 시간을 저장한다.
        // 로케일
    	Locale locale = new Locale(
    			operator.getSupplier().getLang().getCode_2letter(),
    			operator.getSupplier().getCountry().getCode_2letter());
    	
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
        
        // 타임 로케일 유틸
        TimeLocaleUtil tlu = new TimeLocaleUtil(
        		locale, 
        		dfs.getGroupingSeparator()+"", 
        		dfs.getDecimalSeparator()+"");
    	
        dao.update(operator);
    }

    public Operator getOperatorByLoginId(String accountId){
    	return dao.getOperatorByLoginId(accountId);
    }

    @Deprecated
	public List<Object> getOperatorsByRole(Integer roleId) {
		
		List<Operator> operators = dao.getOperatorsByRole(roleId);
		
		List<Object> resultData = new ArrayList<Object>();
		
		for (int i = 0; i < operators.size(); i++) {
    		Map<String, Object> resultMap = new HashMap<String, Object>();
    		
    		resultMap.put("id", operators.get(i).getId());
    		resultMap.put("loginId",operators.get(i).getLoginId());
        	resultMap.put("name",operators.get(i).getName());
        	resultMap.put("telNo",operators.get(i).getTelNo());
        	resultMap.put("email",operators.get(i).getEmail());
        	resultMap.put("loginDenied",operators.get(i).getLoginDenied());
        	resultMap.put("location",operators.get(i).getLocation() == null ? "" : operators.get(i).getLocation().getName());
        	
        	resultData.add(resultMap);
		}
		
		return resultData;
	}

    /**
     * method name : getOperatorListByRole<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getOperatorListByRole(Map<String, Object> conditionMap) {
        List<Map<String, Object>> operatorList = dao.getOperatorListByRole(conditionMap, false);

        return operatorList;
    }

    /**
     * method name : getOperatorListByRoleTotalCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public Integer getOperatorListByRoleTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = dao.getOperatorListByRole(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

	public List<Operator> getOperatorsHaveNoRole(Integer supplierId) {
		return dao.getOperatorsHaveNoRole(supplierId);
	}

	public List<Operator> getOperatorsHaveNoSupplier() {
		return dao.getOperatorsHaveNoSupplier();
	}

	public boolean checkDuplicateLoginId(String loginId) {
		return dao.checkDuplicateLoginId(loginId);
	}

	public boolean checkPassword(String loginId, String password){
	    User loginUser = dao.getOperatorByLoginId(loginId);
	    if(loginUser == null)
	        loginUser = customerDao.getCustomersByLoginId(loginId);
		if (loginUser!=null && loginUser.getPassword().equals(password))
			return true;
		else return false;
	}

    public List<Object> getOperators(int page, int count, int roleId) {
    	List<Operator> operators = dao.getOperators(page, count, roleId);
    	
    	List<Object> resultData = new ArrayList<Object>();
    	
    	for (int i = 0; i < operators.size(); i++) {
    		Map<String, Object> resultMap = new HashMap<String, Object>();
    		
    		resultMap.put("id",operators.get(i).getId());
    		resultMap.put("loginId",operators.get(i).getLoginId());
        	resultMap.put("name",operators.get(i).getName());
        	resultMap.put("telNo",operators.get(i).getTelNo());
        	resultMap.put("email",operators.get(i).getEmail());
        	resultMap.put("loginDenied",operators.get(i).getLoginDenied());
        	resultMap.put("location",operators.get(i).getLocation() == null ? "" : operators.get(i).getLocation().getName());
        	
        	resultData.add(resultMap);
		}
    	
        return resultData;
    }

	public Map<String,String> getCount(int roleId) {
		Map<String,String> result = new HashMap<String,String>();        
        result.put("total", dao.count(roleId)+"");
		return result;
	}

	/* (non-Javadoc)
	 * 사용자의 Login 이력을 저장
	 * @see com.aimir.service.system.OperatorManager#addLoginLog(java.util.Map, com.aimir.model.system.Operator)
	 */
	public Long addLoginLog(Map<String, Object> condition, Operator operator) {
	    LoginLog log = new LoginLog();

	    log.setSessionId(StringUtil.nullToBlank(condition.get("sessionid")));   // 세션 아이디
	    log.setIpAddr(StringUtil.nullToBlank(condition.get("ipaddr")));         // IP 주소
	    log.setLoginDate(StringUtil.nullToBlank(condition.get("logindate")));   // 로그인 시간
	    log.setOperator(operator);
	    log.setLoginId(StringUtil.nullToBlank(condition.get("loginid")));   // 로그인 아이디
	    log.setStatus(StringUtil.nullToBlank(condition.get("status")));     // 상태

	    logDao.add(log);

	    return log.getId();
	}

	/* (non-Javadoc)
	 * 사용자의 Login 이력을 저장
	 * @see com.aimir.service.system.OperatorManager#addLoginLog(java.util.Map, com.aimir.model.system.Operator)
	 */
	public Long loginLogCustomer(Map<String, Object> condition, Customer customer) { 
	    LoginLog log = new LoginLog();
 
	    log.setSessionId(StringUtil.nullToBlank(condition.get("sessionid")));   // 세션 아이디
	    log.setIpAddr(StringUtil.nullToBlank(condition.get("ipaddr")));         // IP 주소
	    log.setLoginDate(StringUtil.nullToBlank(condition.get("logindate")));   // 로그인 시간
	    //log.setOperator(operator);
	    log.setLoginId(StringUtil.nullToBlank(condition.get("loginid")));   // 로그인 아이디
	    log.setStatus(StringUtil.nullToBlank(condition.get("status")));     // 상태

	    logDao.add(log);

	    return log.getId();
	}
	
	/* (non-Javadoc)
	 * 사용자의 Logout 이력을 저장
	 * @see com.aimir.service.system.OperatorManager#addLogoutLog(java.util.Map)
	 */
    public void addLogoutLog(Map<String, Object> condition) {
        // DB에 저장된 현재 사용자의 로그인이력 찾기
        Long genId = (Long) condition.get("generatedId");
        LoginLog log = logDao.get(genId);

        String currentTime = null;

        System.out.println("ID : " + log.getLoginId());
        System.out.println("LOGIN DATE : " + log.getLoginDate());

        try {
            currentTime = TimeUtil.getCurrentTime();
            System.out.println("LOGOUT TIME => " + currentTime);

            log.setLogoutDate(currentTime);
            log.setStatus(CommonConstants.LoginStatus.LOGOUT.toString());
            logDao.update(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see com.aimir.service.system.OperatorManager#getLoginLogGrid(java.util.Map)
     */
    @SuppressWarnings("unchecked")
    public List<Object> getLoginLogGrid(Map<String, Object> condition) {
        List<Object> result = new ArrayList<Object>();

        result = dao.getLoginLogGrid(condition);

        List<Object> data = (List<Object>) result.get(1);
        Map<String, Object> map = new HashMap<String, Object>();

        Supplier supplier = supplierDao.get((Integer) condition.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

        for (Object obj : data)
        {
        	
            map = (Map<String, Object>) obj;
            
            
            String strStatus = String.valueOf(map.get("status"));
            
            
            /**
             * LOGIN/LOGOUT/INVALID_PASSWORD => Login/Logout/Invalid password"로 변경처리
             */
            String strStatus3= this.changeUpperToLowerCase(strStatus);
            
            
            map.put("status",strStatus3);
            map.put("no", dfMd.format(map.get("no")));
            
            map.put("loginTime", TimeLocaleUtil.getLocaleDate((String) map.get("loginTime"), lang, country));
            map.put("logoutTime", TimeLocaleUtil.getLocaleDate((String) map.get("logoutTime"), lang, country));
        }

        return result;
    }
    
    
    /**
     * @desc  login_log grid all data fetch manager impl
     * @param condition
     * @return
     */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	public List<Object> getLoginLogGrid2(Map<String, Object> condition)
	{
		List result = new ArrayList();

		result = dao.getLoginLogGrid2(condition);

		//List<Object> data = (List<Object>) result.get(1);
		Map<String, Object> map = new HashMap<String, Object>();
		
		String supplierId = String.valueOf(condition.get("supplierId"));

		Supplier supplier = supplierDao.get( Integer.parseInt(supplierId));
		
		/*int intLOGINValue = LoginStatus.LOGIN.getCode();
		int intLOGOUTValue = LoginStatus.LOGOUT.getCode();
		int intINVALID_PASSWORDValue = LoginStatus.INVALID_PASSWORD.getCode();*/
		
		
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();

		
		for ( int i=0; i< result.size(); i++)
		{

			map = (Map<String, Object>) result.get(i);

			String strStatus = String.valueOf(map.get("status"));

			/**
			 * LOGIN/LOGOUT/INVALID_PASSWORD => Login/Logout/Invalid password"로
			 * 변경처리
			 */
			String strStatus3 = this.changeUpperToLowerCase(strStatus);

			map.put("status", strStatus3);

			map.put("loginTime", TimeLocaleUtil.getLocaleDate(	(String) map.get("loginTime"), lang, country));
			map.put("logoutTime", TimeLocaleUtil.getLocaleDate(	(String) map.get("logoutTime"), lang, country));
		}

		return result;
	}
    
    
    /**
     * @desc LOGIN/LOGOUT/INVALID_PASSWORD => Login/Logout/Invalid password"로 변경처리 method
     * 
     * @param strStatus
     * @return
     */
	public static String changeUpperToLowerCase(String strStatus)
	{
		String strStatus3 = "";

		if (strStatus.equals("INVALID_PASSWORD") || strStatus =="INVALID_PASSWORD")
		{
			strStatus3 = "Invalid password";
		} else
		{
			String strStatus1 = strStatus.substring(0, 1);
			String strStatus2 = strStatus.substring(1, strStatus.length());
			strStatus2 = strStatus2.toLowerCase();

			strStatus3 = strStatus1 + strStatus2;

		}

		return strStatus3;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.OperatorManager#checkPucNumber(java.lang.String)
	 */
	public boolean checkPucNumber(String pucNumber) {
		boolean status;
		Operator operator = dao.getOperatorByPucNumber(pucNumber);

		if (operator != null && operator.getOperatorStatus() > 0) {
			status = false;
		} else {
			status = true;
		}

		return status;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.OperatorManager#addOperatorReturnOperator(com.aimir.model.system.Operator)
	 */
	public Operator addOperatorReturnOperator(Operator operator) {
		return dao.add(operator);
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.OperatorManager#getOperatorByOperator(com.aimir.model.system.Operator)
	 */
	public List<Operator> getOperatorByOperator(Operator operator) {
		return dao.getOperatorByOperator(operator);
	}
	
	public List<Map<String,Object>> getLoginId() {
		return dao.getLoginId();
	}


}