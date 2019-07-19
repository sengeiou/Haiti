/**
 * MembershipController.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.bo.system.membership;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncryptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.system.operator.LoginController;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Operator;
import com.aimir.model.system.OperatorContract;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.energySavingGoal.EnergySavingGoalManager;
import com.aimir.service.system.membership.OperatorContractManager;
import com.aimir.util.TimeUtil;

/**
 * MembershipController.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 11.   v1.0       김상연         HEMS 회원 가입 관련
 * 2011. 4. 13.   v1.1       김상연         HEMS 회원 정보 수정 관련
 * 2011. 4. 14.   v1.2       김상연         HEMS 회원 정보 조회 관련
 *
 */

@Controller
public class MembershipController {

	private final Log log = LogFactory.getLog(LoginController.class);

    AimirUser user;
    Map<String, Object> condition;
    
	@Autowired
    OperatorManager operatorManager;

    @Autowired
    SupplierManager supplierManager;
    
    @Autowired
    ContractManager contractManager;
    
    @Autowired
    OperatorContractManager operatorContractManager;
    
    @Autowired
    RoleManager roleManager;
    
    @Autowired
    EnergySavingGoalManager energySavingGoalManager;
    
    @Autowired
    HibernateTransactionManager transactionManager;

    /**
     * method name : checkPucNumber
     * method Desc : 주민등록번호 가입 여부 확인
     *
     * @param number
     * @return
     */
    @RequestMapping(value="/gadget/system/membership/checkPucNumber")
    public ModelAndView checkPucNumber(@RequestParam("number") String number) {
    	
    	String pucNumber = null;
    	boolean status = false;
    	
    	try {

    		pucNumber = ESAPI.encryptor().encrypt(number);
    		
			status = operatorManager.checkPucNumber(pucNumber);
    	} catch (EncryptionException e) {
    		
			e.printStackTrace();
		}
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	mav.addObject("status", status);
    	   	
    	return mav;
    }

	/**
	 * method name : checkDuplicateId
	 * method Desc : HEMS용 ID 체크
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/gadget/system/membership/checkId")
	public ModelAndView checkId(@RequestParam("id") String id) {
		
		ModelAndView mav = new ModelAndView("jsonView");		

		mav.addObject("status", operatorManager.checkDuplicateLoginId(id));
		
		return mav;
	}
	
	/**
	 * method name : getSupplierList
	 * method Desc : Supplier List 조회 (HEMS 회원가입용)
	 *
	 * @return
	 */
	@RequestMapping(value="/gadget/system/membership/getSupplierList")
	public ModelAndView getSupplierList() {

        ModelAndView mav = new ModelAndView("jsonView");
        
        mav.addObject("supplierList", supplierManager.getSuppliers());
        
        return mav;
	}
	
	/**
	 * method name : checkContract
	 * method Desc : Contract 유효성 체크
	 *
	 * @param supplierId
	 * @param contractNo
	 * @return
	 */
	@RequestMapping(value="/gadget/system/membership/checkContract")
	public ModelAndView checkContract(
			@RequestParam("supplier") int supplierId,
			@RequestParam("contractNo") String contractNo) {
		
		Contract contract = new Contract() ;
		
		Supplier supplier = new Supplier(supplierId);
		
		contract.setSupplier(supplier);
		contract.setContractNumber(contractNo);
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		mav.addObject("status", contractManager.checkContract(contract));
		
		return mav;
	}
	
	/**
	 * method name : insertMembership
	 * method Desc : 회원 등록
	 *
	 * @param supplierId
	 * @param contractNo
	 * @return
	 */
	@RequestMapping(value="/gadget/system/membership/insertMembership")
	public ModelAndView insertMembership(
			HttpServletRequest request,
			HttpServletResponse response) {
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
		
        String loginId = request.getParameter("id");
        String pw = request.getParameter("pw");
        String password = null;
        String name = request.getParameter("name");
        String number = request.getParameter("number");
        String pucNumber = null;
        String email1 = request.getParameter("email1");
        String email2 = request.getParameter("email2");
        String email = email1 + "@" + email2;
        String emailYn = request.getParameter("emailYn");
        int intEmailYn;
        String zipCode = request.getParameter("zipCode");
        String address1 = request.getParameter("address1");
        String address2 = request.getParameter("address2");
        String telNo = request.getParameter("telNo");
        String mobileNumber = request.getParameter("mobileNumber");
        String smsYn = request.getParameter("smsYn");
        int intSmsYn;
        String writeDate = null;
        String lastLine = request.getParameter("lastLine");
        int contractIndex;
        String arrayChecked[];
        String arraySupplier[];
        int arraySupplierId[];
        String arrayContractNo[];
        String arrayFriendlyName[];
        String strY = "Y";
        String strC = "C";
        String roleName = "customer";
        int int1 = 1;
        int int0 = 0;
        boolean status = false;
        
		OperatorContract operatorContract;
		Contract contract;
		Supplier supplier;
		Role role;
		
		List<Contract> contracts;
		
        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);

        try {
			
            txStatus = transactionManager.getTransaction(txDefine);
            
			password = instance.hashPassword(pw, loginId);
	        pucNumber = ESAPI.encryptor().encrypt(number);
			writeDate = TimeUtil.getCurrentTime();
	        contractIndex = Integer.parseInt(lastLine);
	        role = roleManager.getRoleByName(roleName);
	        intEmailYn = Integer.parseInt(emailYn);
	        intSmsYn = Integer.parseInt(smsYn);
	        
	        arrayChecked = new String[contractIndex - 1];
	        arraySupplier = new String[contractIndex - 1];
	        arraySupplierId = new int[contractIndex - 1];
	        arrayContractNo = new String[contractIndex - 1];
	        arrayFriendlyName = new String[contractIndex -1];
	        
	        
			for (int i = 1; i < contractIndex; i++) {
				
				arrayChecked[i - 1] = request.getParameter("checked" + i);
				arraySupplier[i - 1] = request.getParameter("supplier" + i);
				arrayContractNo[i - 1] = request.getParameter("contractNo" + i);
		        arraySupplierId[i - 1] = Integer.parseInt(arraySupplier[i - 1]);
		        arrayFriendlyName[i - 1] = request.getParameter("friendlyName" + i);
			}

			Operator operator = new Operator();
			
			operator.setAliasName(name);
			operator.setRole(role);
			operator.setShowDefaultDashboard(true);
			operator.setOperatorStatus(int1);
			operator.setLoginId(loginId);
			operator.setPassword(password);
			operator.setName(name);
			operator.setPucNumber(pucNumber);
			operator.setEmail(email);
			operator.setEmailYn(intEmailYn);
			operator.setZipCode(zipCode);
			operator.setAddress1(address1);
			operator.setAddress2(address2);
			operator.setTelNo(telNo);
			operator.setMobileNumber(mobileNumber);
			operator.setSmsYn(intSmsYn);
			operator.setWriteDate(writeDate);
			operator.setFailedLoginCount(int0);
			operator.setLoginDenied(false);
			operator.setSupplier(new Supplier(arraySupplierId[0]));

			operator = operatorManager.addOperatorReturnOperator(operator);
			
			for (int i = 0; i < contractIndex - 1; i++) {
				
				operatorContract = new OperatorContract();
				
				if (strY.equals(arrayChecked[i])) {
					
					operatorContract.setContractStatus(1);
				} else if (strC.equals(arrayChecked[i])) {
					
					operatorContract.setContractStatus(0);
				} else {
					continue;
				}

				operatorContract.setWriteDate(writeDate);

				contract = new Contract();
				supplier = new Supplier(arraySupplierId[i]);
				
				contract.setContractNumber(arrayContractNo[i]);
				contract.setSupplier(supplier);
				
				contracts = contractManager.getContractByContract(contract);
				
				for (Contract insertContract: contracts) {

					operatorContract.setContract(insertContract);
					operatorContract.setOperator(operator);
					operatorContract.setCustomerNumber(insertContract.getCustomer().getCustomerNo());
					operatorContract.setFriendlyName(arrayFriendlyName[i]);

					operatorContractManager.addOperatorContract(operatorContract);
				}
			}

			// 기본 설정 대시보드/가젯 정보 등록
			operatorContractManager.saveDashboardGadgetInfo(operator.getId());

			transactionManager.commit(txStatus);
            
			status = true;
		} catch (Exception e) {
			
        	transactionManager.rollback(txStatus);
            
            status = false;
            
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView("jsonView");
        
		mav.addObject("status", status);
		
		return mav;
	}

	/**
	 * method name : deleteMembership
	 * method Desc : HEMS 회원 탈퇴 로직
	 *
	 * @param operatorId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/gadget/system/membership/deleteMembership")
	public ModelAndView deleteMembership(
			@RequestParam("operatorId") int operatorId) {
		
		boolean status = false;
		
        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);

		try{
			
            txStatus = transactionManager.getTransaction(txDefine);
            
            Operator operator = operatorManager.getOperator(operatorId);
            
            int intDelete = 0;
            
            operator.setOperatorStatus(intDelete);
            
            operatorManager.updateOperator(operator);
            
        	HttpSession session = ESAPI.currentRequest().getSession();
        	
        	Long genId = (Long)session.getAttribute("generatedId");
        	log.debug("getUsernameParameterName " + ESAPI.securityConfiguration().getUsernameParameterName());
        	
        	if(genId != null){
        		
        		condition = new HashMap();
            	condition.put("generatedId", genId);
            	log.debug("==========Generated ID : " + genId);
            	
            	operatorManager.addLogoutLog(condition);
               	log.debug("===============LOGOUT WRITE===============");
        	}
            
            AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
            AimirUser user = (AimirUser)instance.getUserFromSession();

            // LOG OUT
            if(user != null){
            	user.logout();
            }
            
			transactionManager.commit(txStatus);
            
            status = true;
		} catch (Exception e) {
			
        	transactionManager.rollback(txStatus);
            
			status = false;
		}
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		mav.addObject("status", status);
		
		return mav;
	}
	
	/**
	 * method name : modifyMembership
	 * method Desc : HEMS 회원 정보 수정 로직
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/gadget/system/membership/modifyMembership")
	public ModelAndView modifyMembership(
			HttpServletRequest request,
			HttpServletResponse response) {
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
		
        String operatorId = request.getParameter("operatorId");
        int intId;
        String loginId;
        String pw = request.getParameter("pw");
        String password = null;
        String email1 = request.getParameter("email1");
        String email2 = request.getParameter("email2");
        String email = email1 + "@" + email2;
        String emailYn = request.getParameter("emailYn");
        int intEmailYn;
        String zipCode = request.getParameter("zipCode");
        String address1 = request.getParameter("address1");
        String address2 = request.getParameter("address2");
        String telNo = request.getParameter("telNo");
        String mobileNumber = request.getParameter("mobileNumber");
        String smsYn = request.getParameter("smsYn");
        int intSmsYn;
        String updateDate;
        String lastLine = request.getParameter("lastLine");
        int contractIndex;
        String arrayId[];
        String arrayChecked[];
        String arraySupplier[];
        int arraySupplierId[];
        String arrayContractNo[];
        String arrayFriendlyName[];
        String strY = "Y";
        String strC = "C";
        String strD = "D";
        String roleName = "customer";
        	
        boolean status = false;
        
		OperatorContract operatorContract;
		Contract contract;
		Supplier supplier;
		Role role;
		String contractNumber;
		int supplierId;
		
		List<Contract> contracts;

        TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);

		try{
			
            txStatus = transactionManager.getTransaction(txDefine);
            
            intId = Integer.parseInt(operatorId);
            Operator operator = operatorManager.getOperator(intId);
            String comparePass = operator.getPassword();
            loginId = operator.getLoginId();
            
            if (pw != null && pw.equals(comparePass)) {
            	
            	password = pw;
            } else {
            	
            	password = instance.hashPassword(pw, loginId);
            }
            
            updateDate = TimeUtil.getCurrentTime();
	        contractIndex = Integer.parseInt(lastLine);
	        role = roleManager.getRoleByName(roleName);
	        intEmailYn = Integer.parseInt(emailYn);
	        intSmsYn = Integer.parseInt(smsYn);
	        
			arrayId = new String[contractIndex - 1];
	        arrayChecked = new String[contractIndex - 1];
	        arraySupplier = new String[contractIndex - 1];
	        arraySupplierId = new int[contractIndex - 1];
	        arrayContractNo = new String[contractIndex - 1];
	        arrayFriendlyName = new String[contractIndex - 1];
	        
			for (int i = 1; i < contractIndex; i++) {
				
				arrayId[i - 1] = request.getParameter("id" + i);
				arrayChecked[i - 1] = request.getParameter("checked" + i);
				arraySupplier[i - 1] = request.getParameter("supplier" + i);
				arraySupplierId[i - 1] = Integer.parseInt(arraySupplier[i - 1]);
				arrayContractNo[i - 1] = request.getParameter("contractNo" + i);
				arrayFriendlyName[i - 1] = request.getParameter("friendlyName" + i);
			}
			
			operator.setRole(role);
			operator.setPassword(password);
			operator.setEmail(email);
			operator.setEmailYn(intEmailYn);
			operator.setZipCode(zipCode);
			operator.setAddress1(address1);
			operator.setAddress2(address2);
			operator.setTelNo(telNo);
			operator.setMobileNumber(mobileNumber);
			operator.setSmsYn(intSmsYn);
			operator.setUpdateDate(updateDate);
			operator.setSupplier(new Supplier(arraySupplierId[0]));

			operatorManager.updateOperator(operator);
			
			for (int i = 0; i < contractIndex - 1; i++) {
				
				operatorContract = new OperatorContract();
				
				if ("".equals(arrayId[i])) { // 새로운 계약 정보 추가
					
					if (strY.equals(arrayChecked[i])) {
						
						operatorContract.setContractStatus(1);
					} else if (strC.equals(arrayChecked[i])) {
						
						operatorContract.setContractStatus(0);
					} else {
						
						continue;
					}
					
					operatorContract.setWriteDate(updateDate);
	
					contract = new Contract();
					supplier = new Supplier(arraySupplierId[i]);
					
					contract.setContractNumber(arrayContractNo[i]);
					contract.setSupplier(supplier);
					
					contracts = contractManager.getContractByContract(contract);
					
					for (Contract insertContract: contracts) {
						
						operatorContract.setContract(insertContract);
						operatorContract.setOperator(operator);
						operatorContract.setCustomerNumber(insertContract.getCustomer().getCustomerNo());
						operatorContract.setFriendlyName(arrayFriendlyName[i]);
						operatorContractManager.addOperatorContract(operatorContract);
					}
				} else { // 기존 계약정보 수정

					int id = Integer.parseInt(arrayId[i]);
					
					operatorContract.setId(id);
					
					List<OperatorContract> chageOperatorContracts 
						= operatorContractManager.getOperatorContract(operatorContract);
					
					for (OperatorContract chageoperatorContract: chageOperatorContracts) {
						
						if (strY.equals(arrayChecked[i])) {
							
							chageoperatorContract.setContractStatus(1);
						} else if (strC.equals(arrayChecked[i])) {
							
							chageoperatorContract.setContractStatus(0);
						}  else if (strD.equals(arrayChecked[i])) {
							// 에너지 절감 목표가 설정 되어있을 경우 , 삭제한다.
							energySavingGoalManager.deleteByOperatorContractId(chageoperatorContract.getId());

							// 고객계약 정보를 삭제한다.
							operatorContractManager.deleteOperatorContract(chageoperatorContract);

							continue;
						}
//						else {
//							continue;
//						}
						
						contract = new Contract();
						supplier = new Supplier();
						
						contract = chageoperatorContract.getContract();
						contractNumber = contract.getContractNumber();
						supplier = contract.getSupplier();
						supplierId = supplier.getId();
						
						if (arrayContractNo[i].equals(contractNumber)
							&& arraySupplierId[i] == supplierId) {
							
							chageoperatorContract.setUpdateDate(updateDate);
							chageoperatorContract.setFriendlyName(arrayFriendlyName[i]);
							operatorContractManager.updateOperatorContract(chageoperatorContract);
						} else {
							
							operatorContract.setWriteDate(updateDate);
							
							contract = new Contract();
							supplier = new Supplier(arraySupplierId[i]);
							
							contract.setContractNumber(arrayContractNo[i]);
							contract.setSupplier(supplier);
							
							contracts = contractManager.getContractByContract(contract);
							
							for (Contract insertContract: contracts) {
								
								operatorContract.setContract(insertContract);
								operatorContract.setOperator(operator);
								operatorContract.setCustomerNumber(insertContract.getCustomer().getCustomerNo());
								operatorContract.setFriendlyName(arrayFriendlyName[i]);
								operatorContractManager.addOperatorContract(operatorContract);
							}
						}
					}
				}
			}
			
			// 기본 설정 대시보드/가젯 정보 변경
			operatorContractManager.modifyDashboardGadgetInfo(operator.getId());

			// 디폴트 대시보드 
			transactionManager.commit(txStatus);
            
            status = true;
		} catch (Exception e) {
			
        	transactionManager.rollback(txStatus);
            
			status = false;
		}
		
		ModelAndView mav = new ModelAndView("jsonView");
			
		mav.addObject("status", status);
			
		return mav;
	}

	/**
	 * method name : getContractList
	 * method Desc : HEMS 회원 정보 조회 시 Contract 부분
	 *
	 * @param operatorId
	 * @return
	 */
	@RequestMapping(value="/gadget/system/membership/getContractList")
	public ModelAndView getContractList(
			@RequestParam("operatorId") int operatorId) {
		
		boolean status = false;
		
		List<OperatorContract> operatorContracts;
		
		try {
			
			Operator operator = new Operator();
			operator.setId(operatorId);
		
			operatorContracts = operatorContractManager.getOperatorContractByOperator(operator);
			status = true;
		} catch (Exception e) {
			
			operatorContracts = null;
			status = false;
			
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		mav.addObject("status", status);
		mav.addObject("operatorContracts", operatorContracts);
			
		return mav;
	}
	
	/**
	 * method name : findId
	 * method Desc : Id 조회 로직
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/gadget/system/membership/findId")
	public ModelAndView findId(
			HttpServletRequest request,
			HttpServletResponse response) {
	
		String loginId;
		boolean status = false;
		
		try {
			
			Operator operator = new Operator();
		
			String findIdName = request.getParameter("findIdName");
			String findIdNumber = request.getParameter("findIdNumber");
			int operatorStatus = 1;
			
			String pucNumber = ESAPI.encryptor().encrypt(findIdNumber);
			
			operator.setName(findIdName);
			operator.setPucNumber(pucNumber);
			operator.setOperatorStatus(operatorStatus);
			
			List<Operator> operators = operatorManager.getOperatorByOperator(operator);
			
			loginId = operators.get(0).getLoginId();
			
			status = true;
		} catch (Exception e) {
			
			loginId = null;
			status = false;
			
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		mav.addObject("status", status);
		mav.addObject("loginId", loginId);
			
		return mav;
	}

	/**
	 * method name : findPw
	 * method Desc : Password 조회 로직
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/gadget/system/membership/findPw")
	public ModelAndView findPw(
			HttpServletRequest request,
			HttpServletResponse response) {
	
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
		
		String password;
		boolean status = false;
		
		try {
			
			Operator operator = new Operator();
		
			String loginId = request.getParameter("findPwId");
			String findIdName = request.getParameter("findPwName");
			String findIdNumber = request.getParameter("findPwNumber");
			int operatorStatus = 1;
			
			String pucNumber = ESAPI.encryptor().encrypt(findIdNumber);
			
			operator.setLoginId(loginId);
			operator.setName(findIdName);
			operator.setPucNumber(pucNumber);
			operator.setOperatorStatus(operatorStatus);
			
			List<Operator> operators = operatorManager.getOperatorByOperator(operator);
			
			if (operators.size() == 1) {
				
				Double dbRandom = Math.random();
				String strRandom = dbRandom.toString();
				int lengthRandom = strRandom.length();
				StringBuffer sb = new StringBuffer(lengthRandom);
				String parseStr;
				int parseInt;
					
				for (int i = 2; i < lengthRandom; i++) {
					
					parseStr = "";

					if (1 == (i * i * i) % 7) {
						
						parseStr = strRandom.charAt(i) + "";
						parseInt = Integer.parseInt(parseStr);
						parseInt = ((parseInt*parseInt*parseInt)%25) + 97;
						parseStr = (char)parseInt + "";
					} else {
						
						parseStr = strRandom.charAt(i) + "";
					}
					
					sb = sb.append(parseStr);
				}
				
				password = sb.toString();
				
            	String hashPassword = instance.hashPassword(password, loginId);
            	
            	operator = operators.get(0);
            	
            	operator.setPassword(hashPassword);
            	
            	operatorManager.updateOperator(operator);
				
			} else {
				
				throw new Exception("Error Operator");
			}
				
			status = true;
		} catch (Exception e) {
			
			password = null;
			status = false;
			
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		mav.addObject("status", status);
		mav.addObject("password", password);
			
		return mav;
	}
	
}
