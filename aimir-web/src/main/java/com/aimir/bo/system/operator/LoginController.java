/**
 * LoginController.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.bo.system.operator;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EnterpriseSecurityException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Customer;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.User;
import com.aimir.service.system.CustomerManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.membership.OperatorContractManager;
import com.aimir.util.SessionContext;
import com.aimir.util.TimeUtil;
import com.aimir.web.SupplierLocale;

/**
 * LoginController.java Description
 *
 *
 * Date          Version     Author   Description
 * 2011. 4. 4.   v1.0       eBStorm
 * 2011. 4. 5.   v1.1       김상연          HEMS 관련 ContractNo 유효성 체크 로직 ADD
 * 2011. 4. 13.  v1.2       김상연          HEMS 관련 ContractNo 유효성 체크 로직 변경
 *
 */
@Controller
@RequestMapping({"/admin/login.*","/customer/login.*","/admin/edh/login.*"})
public class LoginController{
    private final Log log = LogFactory.getLog(LoginController.class);

    @Autowired
    OperatorManager operatorManager;

    @Autowired
    CustomerManager customerManager;

    @Autowired
    RoleManager userRoleManager;

    @Autowired
    OperatorContractManager operatorContractManager;
    
    @Autowired
    SupplierManager supplierManager;
    
	@Resource(name = "sessionContextFactory")
    ObjectFactory<?> sessionContextFactory;

    AimirUser aimirUser;
    Map<String, Object> condition;

    /**
     * method name : login
     * method Desc : Post 방식 로그인
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings( { "unchecked" })
    @RequestMapping(method = RequestMethod.POST)
    protected ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {

        log.info("login POST");

        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        ModelAndView mav = new ModelAndView("jsonView");

        // System.out.println(""+ESAPI.currentRequest().getParameter( "remember" ));
        // authentication for login
        // // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();

        String id = null;
        String pw = null;
        id = ESAPI.currentRequest().getParameter(ESAPI.securityConfiguration().getUsernameParameterName());
        pw = ESAPI.currentRequest().getParameter(ESAPI.securityConfiguration().getPasswordParameterName());

        HttpSession session = ESAPI.currentRequest().getSession();
        AimirUser aimirUser = (AimirUser) instance.getUserFromSession();

        // else if there's a remember token then use that
        if (aimirUser != null) {
            log.info("user from session." + aimirUser.getLoginId());
            aimirUser.logout();
            session = ESAPI.currentRequest().getSession();
            aimirUser = (AimirUser) instance.getUserFromSession();
            // mav.addObject(ESAPI.securityConfiguration().getUsernameParameterName(), ""+user.getLoginId());
            // mav.addObject(ESAPI.securityConfiguration().getPasswordParameterName(), "");
        }
        String browserLang = request.getParameter("lang");        
		Properties messageProp = new Properties();
		
        InputStream ip = getClass().getClassLoader().getResourceAsStream("message_"+ browserLang +".properties");
        if(ip == null){
        	ip = getClass().getClassLoader().getResourceAsStream("message_en.properties");	        	
        }
        messageProp.load(ip);

        /*
         * // if there's a user in the session then use that AimirUser user = (AimirUser)instance.getUserFromSession();
         *
         * String[] data = null; // else if there's a remember token then use that if ( user == null ) { data =
         * instance.getUserFromRememberToken(); }else { log.info("user from session."+ user.getAccountName()); if
         * (user.getUserType()!=1 || user.isAnonymous() || !id.equals(""+user.getAccountId())){ user.logout(); user = null;
         * }else{ mav.addObject("result", "login success!!"); return mav; } }
         */
        /*
         * // else try to verify credentials - throws exception if login fails if ( user == null ) { user =
         * (AimirUser)instance.loginWithUsernameAndPassword(request, response); }
         */

        User loginUser = null;

        loginUser = operatorManager.getOperatorByLoginId(id);
        if (loginUser == null)
            loginUser = customerManager.getCustomersByLoginId(id);
        
        Supplier supplier = null;
		if(loginUser != null && loginUser.getSupplier() != null && loginUser.getSupplierId() != 1) {
			supplier = loginUser.getSupplier();
        	ip = getClass().getClassLoader().getResourceAsStream("message_"+ supplier.getLang().getCode_2letter() +".properties");
        	if(ip == null){
        		ip = getClass().getClassLoader().getResourceAsStream("message_en.properties");	        	
        	}
        	messageProp.load(ip);
		}
        

        log.debug("============id/pw : " + id + ", " + pw + "=============");
        log.debug("SESSION ID : " + session.getId());
        log.debug("IP ADDR : " + request.getRemoteAddr());
        log.debug("REMOTE HOST : " + request.getRemoteHost());
        log.debug("operator ID : "+ id);

        condition = new HashMap();

        condition.put("sessionid", new String(session.getId())); // session id
        condition.put("ipaddr", new String(request.getRemoteAddr())); // ip addr
        condition.put("loginid", id + ""); // login id

        if (loginUser != null) {

            /*
             * 2011. 04. 13 v1.2 HEMS 관련 ContractNo 유효성 체크 로직 변경 DELETE START 김상연 Role role = null;
             *
             * Locale locale = new Locale( operator.getSupplier().getLang().getCode_2letter(),
             * operator.getSupplier().getCountry().getCode_2letter());
             *
             * DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
             *
             * TimeLocaleUtil tlu = new TimeLocaleUtil( locale, dfs.getGroupingSeparator()+"", dfs.getDecimalSeparator()+"");
             *
             * @SuppressWarnings("unused") String loginDate = tlu.getLocaleDate(TimeUtil.getCurrentLongTime(), 14,
             * locale.getLanguage(), locale.getCountry());
             *
             * // 접속시간 condition.put("logindate",TimeUtil.getCurrentTime());
             *
             * log.info("========================================="); log.info("접속 시간 : " + TimeUtil.getCurrentTime());
             *
             * //System.out.println("접속시간 ; " + tlu.getLocaleDate(TimeUtil.getCurrentLongTime(), 14, locale.getLanguage(),
             * locale.getCountry()));
             *
             * /* if(operator.getGroupId()!=null && operator.getGroupId().length()>0) role = (Role)
             * userRoleManager.getRole(Integer.parseInt(operator.getGroupId()));
             * System.out.println("role.getId() :"+role.getId()); System.out.println("role.getName() :"+role.getName());
             */
            /* 2011. 04. 13 v1.2 HEMS 관련 ContractNo 유효성 체크 로직 변경 DELETE END 김상연 */
            // userLoginManager
            try { // 로그인 성공

                /* 2011. 04. 13 v1.2 HEMS 관련 ContractNo 유효성 체크 로직 변경 ADD START 김상연 */
                if (0 == (loginUser.getOperatorStatus() == null ? 1 : loginUser.getOperatorStatus())) {
                    throw new Exception("INVALID_ID (OPERATOR_STATUS 0)");
                }

                Role role = null;

                Locale locale;
                String currentTime = TimeUtil.getCurrentTime();

                // 접속시간
                condition.put("logindate", currentTime);

                log.info("=========================================");
                log.info("접속 시간 : " + currentTime);
                log.info("loginRoleId : " + loginUser.getRoleId());

                //로케일값 가지고 오는 부분.
                locale = new Locale(loginUser.getSupplier().getLang().getCode_2letter(),
                        loginUser.getSupplier().getCountry().getCode_2letter());

                //String locale2= locale.getLanguage();
                //로케일값 세션으로 설정.
                String lang= loginUser.getSupplier().getLang().getCode_2letter();
                String supplierId = loginUser.getSupplier().getId().toString();

                session.setAttribute("sesSupplierId", supplierId);
                session.setAttribute("locale", locale);
                session.setAttribute("lang", lang.toUpperCase());

                /* 2011. 04. 13 v1.2 HEMS 관련 ContractNo 유효성 체크 로직 변경 ADD END 김상연 */

                aimirUser = new AimirUser(loginUser);
                if (role != null)
                    aimirUser.setRoleData(role);
                // user = new AimirUser(operator);
                aimirUser.enable();
                aimirUser.unlock();
                aimirUser.setLastHostAddress(ESAPI.currentRequest().getRemoteHost());
                
                SessionContext sessionContext = (SessionContext) sessionContextFactory.getObject();
                sessionContext.setUser(loginUser);
                
                // 아이디가 일치할 경우
                if(loginUser.getLoginDenied() == false) {

                    if (loginUser.getPassword().equals(instance.hashPassword(pw, id))) {
                        aimirUser.setLastLoginTime(Calendar.getInstance().getTime());
                        aimirUser.setFailedLoginCount(0);
                        aimirUser.setLoggedIn(true);
                        // instance.setCurrentUser(user);
                        // create new session for this User

                        aimirUser.addSession(session);
                        session.setAttribute("ESAPIUserSessionKey", aimirUser);
                        instance.setCurrentUser(aimirUser);

                        

                        
                        /*
                         * if ( ESAPI.currentRequest().getParameter( "remember" ) != null ) { int maxAge = ( 60 * 60 * 24 * 14 );
                         * String token = ESAPI.httpUtilities().setRememberToken( ESAPI.httpUtilities().getCurrentRequest(),
                         * ESAPI.httpUtilities().getCurrentResponse(), pw, maxAge, null, null );
                         * ESAPI.currentRequest().setAttribute("token", "New remember token:" + token ); }
                         */
                        request.setAttribute(aimirUser.getCSRFToken(), "authenticated");
                        if (loginUser instanceof Operator)
                            operatorManager.updateOperator((Operator)aimirUser.getOperator(loginUser));
                        else if (loginUser instanceof Customer)
                            customerManager.updateCustomer((Customer)aimirUser.getOperator(loginUser));

                        /*
                         * Current User: <%=user.getAccountName() %><br> Last Successful Login: <%=user.getLastLoginTime() %><br>
                         * Last Failed Login: <%=user.getLastFailedLoginTime() %><br> Failed Login Count:
                         * <%=user.getFailedLoginCount() %><br> Current Roles: <%=user.getRoles() %><br> Last Host Name:
                         * <%=user.getLastHostAddress() %><br>
                         */
                        aimirUser = (AimirUser) instance.getUserFromSession();

                        mav.addObject("result", "success");
                        condition.put("status", "LOGIN"); // 로그인 성공

                        log.debug("====================저장할 세션 값 :" + locale.getCountry() + ", " + locale.getLanguage());
                        // Set supplier's locale information to session.
                        SupplierLocale.setSessionLocale(request, locale);
                        log.debug("============== Session Stored Locale : " + SupplierLocale.getSessionLocale(request).getCountry()
                                + ", " + SupplierLocale.getSessionLocale(request).getLanguage()
                                + "==================================");
                        
                        

                        
                        
                    } else {
                        aimirUser.setLoggedIn(false);
                        aimirUser.setLastFailedLoginTime(Calendar.getInstance().getTime());
                        aimirUser.incrementFailedLoginCount();

                        if (aimirUser.getFailedLoginCount() >= ESAPI.securityConfiguration().getAllowedLoginAttempts()) {
                            aimirUser.lock();
                        }
                        if (loginUser instanceof Operator)
                            operatorManager.updateOperator((Operator)aimirUser.getOperator(loginUser));
                        else if (loginUser instanceof Customer)
                            customerManager.updateCustomer((Customer)aimirUser.getOperator(loginUser));
                        // throw new Exception ("password is wrong");

                        mav.addObject("result", messageProp.getProperty("aimir.invalid.namePass"));
                        condition.put("status", "INVALID_PASSWORD"); // 로그인 실패

                        // mav.setViewName("/admin/login");
                        // mav.setViewName("/customer/login");
                        //mav.setViewName("/admin/edh/login");
                    }
                } else {

                    aimirUser.setLoggedIn(false);
                    aimirUser.setLastFailedLoginTime(Calendar.getInstance().getTime());
                    aimirUser.incrementFailedLoginCount();

                    if (aimirUser.getFailedLoginCount() >= ESAPI.securityConfiguration().getAllowedLoginAttempts()) {
                        aimirUser.lock();
                    }
                    if (loginUser instanceof Operator)
                        operatorManager.updateOperator((Operator)aimirUser.getOperator(loginUser));
                    else if (loginUser instanceof Customer)
                        customerManager.updateCustomer((Customer)aimirUser.getOperator(loginUser));

                    mav.addObject("result", "login is denied");
                    condition.put("status", "LOGIN_IS_DENIED"); // 로그인 접속 제한

                }

            } catch (EnterpriseSecurityException e) {
                log.error(e, e);
                aimirUser.logout();
                mav.addObject("result", messageProp.getProperty("aimir.invalid.namePass"));
                condition.put("status", "INVALID_PASSWORD"); // 로그인 실패
            } catch (Exception e) {

                log.error(e, e);
            }


        }
        else
        {
            mav.addObject("result", messageProp.getProperty("aimir.invalid.namePass"));
            condition.put("status", "INVALID_ID"); // 잘못된 ID
        }
        log.debug("==========================================");
        log.debug("session id : " + condition.get("sessionid"));
        log.debug("get status : " + condition.get("status"));

        if (loginUser instanceof Operator ) {
            Long pkId = operatorManager.addLoginLog(condition, (Operator)loginUser); // 로그인 이력 저장
            if (!((String) condition.get("sessionid")).equals(pkId.toString())) {
                session.setAttribute("generatedId", pkId);
            }
        }else if(loginUser instanceof Customer) {
            Long pkId = operatorManager.loginLogCustomer(condition, (Customer)loginUser); // 로그인 이력 저장
            if (!((String) condition.get("sessionid")).equals(pkId.toString())) { 
                session.setAttribute("generatedId", pkId); 
            }
        }


        // WebUtils.setSessionAttribute(request, "generatedId", pkId); // 세션에 현재 로그인 이력이 저장된 row의 ID값 세션에 저장

        /* 2011. 04. 05 v1.1 HEMS 관련 ContractNo 유효성 체크 로직 ADD START 김상연 */
        /*
         * 2011. 04. 13 v1.2 HEMS 관련 ContractNo 유효성 체크 로직 변경 DELETE START 김상연 if ( request.getParameter("title") != null &&
         * request.getParameter("title").equals("HEMS") && condition.get("status").equals("LOGIN")) {
         *
         * if (!operatorContractManager.checkOperatorContract(operator)) {
         *
         * user.logout(); mav.addObject("result", "Contract No"); } } 2011. 04. 13 v1.2 HEMS 관련 ContractNo 유효성 체크 로직 변경 DELETE
         * END 김상연
         */
        /* 2011. 04. 05 v1.1 HEMS 관련 ContractNo 유효성 체크 로직 ADD END 김상연 */

        return mav;
    }

    /**
     * method name : openLogin
     * method Desc : Get 방식 로그인
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    @RequestMapping(method = RequestMethod.GET)
    protected ModelMap openLogin(HttpServletRequest request, HttpServletResponse response)
    throws ServletException {


        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        ModelMap mav = new ModelMap();

        // authentication for login
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        @SuppressWarnings("unused")
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();

        // if there's a user in the session then use that
   //     AimirUser user = (AimirUser)instance.getUserFromSession();

        // else if there's a remember token then use that
        if ( aimirUser == null ) {
            /*
            data = instance.getUserFromRememberToken();

            if(data!=null){
                log.info("user from getUserFromRememberToken()::"+ data[0]+", "+data[1]);
                mav.addObject(ESAPI.securityConfiguration().getUsernameParameterName(), ""+data[0]);
                mav.addObject(ESAPI.securityConfiguration().getPasswordParameterName(), ""+data[1]);
            }else{
                mav.addObject(ESAPI.securityConfiguration().getUsernameParameterName(), "");
            }
            */
        } else {

        //  mav.addObject(ESAPI.securityConfiguration().getUsernameParameterName(), ""+user.getLoginId());
        //      mav.addObject(ESAPI.securityConfiguration().getPasswordParameterName(), "");
        }
        mav.addAttribute("result", "");
        return mav;
    }

}