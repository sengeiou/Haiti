package com.aimir.bo.system.customer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.cms.model.DebtEnt;
import com.aimir.cms.service.DebtEntManager;
import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.RegType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.model.system.Customer;
import com.aimir.model.system.GroupMember;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.SupplyType;
import com.aimir.model.system.TariffType;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.device.DeviceRegistrationManager;
import com.aimir.service.device.MCUManager;
import com.aimir.service.device.MeterManager;
import com.aimir.service.device.ModemManager;
import com.aimir.service.mvm.BillingManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ContractChangeLogManager;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.CustomerManager;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.PrepaymentLogManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.SupplyTypeManager;
import com.aimir.service.system.TariffTypeManager;
import com.aimir.service.system.prepayment.PrepaymentChargeManager;
import com.aimir.service.system.prepayment.PrepaymentMgmtCustomerManager;
import com.aimir.support.FileUploadHelper;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CodeTypeEditor;
import com.aimir.util.CommonUtils;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.CustomerMaxMakeExcel;
import com.aimir.util.CustomerTypeEditor;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.ExcelUtil;
import com.aimir.util.LocationTypeEditor;
import com.aimir.util.StringUtil;
import com.aimir.util.SupplierTypeEditor;
import com.aimir.util.TariffTypeEditor;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@SuppressWarnings("unused")
@Controller
public class CustomerController {

    Log log = LogFactory.getLog(CustomerController.class);

    @Autowired
    CustomerManager customerManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    SupplierManager supplierManager;

    @Autowired
    SupplyTypeManager supplyTypeManager;

    @Autowired
    LocationManager locationManager;

    @Autowired
    TariffTypeManager tarifftypeManager;

    @Autowired
    ContractManager contractManager;

    @Autowired
    ContractChangeLogManager contractChangeLogManager;

    @Autowired
    OperatorManager operatorManager;

    @Autowired
    LocationDao locationDao;

    @Autowired
    PrepaymentLogManager prepaymentLogManager;

    @Autowired
    BillingManager billingManager;

    @Autowired
    MeterManager meterManager;

    @Autowired
    ModemManager modemManager;

    @Autowired
    MCUManager mcuManager;

    @Autowired
    MeterDao meterDao;

    @Autowired
    DeviceRegistrationManager deviceRegistrationManager;

    @Autowired
    CmdOperationUtil cmdOperationUtil;

    @Autowired
    SupplierDao supplierDao;

    @Autowired
    GroupMemberDao groupMemberDao;

    @Autowired
    PrepaymentMgmtCustomerManager prepaymentMgmtCustomerManager;
    
    @Autowired
    PrepaymentChargeManager prepaymentChargeManager;
    
    @Autowired
    OperatorDao operatorDao;

    @Autowired
    RoleManager roleManager;
    
    @Autowired
    DebtEntManager debtEntManager;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Code.class, new CodeTypeEditor());
        binder.registerCustomEditor(Supplier.class, new SupplierTypeEditor());
        binder.registerCustomEditor(Location.class, new LocationTypeEditor());
        binder.registerCustomEditor(TariffType.class, new TariffTypeEditor());
        binder.registerCustomEditor(Customer.class, new CustomerTypeEditor());
    }

    /**
     * @desc 맥스 가젯 초기 진입 액션.
     * @return
     */
    @RequestMapping(value="/gadget/system/customerMax")
    public ModelAndView customerMax() {
        ModelAndView mav = new ModelAndView("gadget/system/customerMax");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);
        
        Properties prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
        } catch (IOException e) {
            log.error(e,e);
        }
        
        //분할납부기능을 사용할 경우 true로 표시한다. 분할납부기능은 ECG요구사항을 기반으로 만들어졌다.(2015년 02월 03일 요구사항 기준)
        String isPartpayment = prop.getProperty("partpayment.use");
        String initArrears = prop.getProperty("prepay.init.arrears");

        int supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        mav.addObject("supplierId", supplierId);
        mav.addObject("isPartpayment" , (isPartpayment == null || "".equals(isPartpayment)) ? false : isPartpayment);
        mav.addObject("initArrears" , (initArrears == null || "".equals(initArrears)) ? 0 : Integer.parseInt(initArrears));
        mav.addObject("role" , role.getName());
        
        return mav;
    }

    @RequestMapping(value="/gadget/system/customerMax2")
    public ModelAndView customerMax2() {
        return new ModelAndView("gadget/system/customerMax2");
    }

    @RequestMapping(value="/gadget/system/customerMax3")
    public ModelAndView customerMax3() {
        return new ModelAndView("gadget/system/customerMax3");
    }

    @RequestMapping(value="/gadget/system/customerMax4")
    public ModelAndView customerMax4() {

        return new ModelAndView("gadget/system/customerMax4");
    }

    /* *//**
     * @desc 1.트리 데이터를 json 형태로 가져온다.
     *       2.text-output stream 형태로 출력.
     * @param request
     * @param response
     *
     *//*
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    @RequestMapping(value = "/gadget/system/getJsonTree")
    public void getJsonTree(
                HttpServletRequest request, HttpServletResponse response
    )
    {
        response.setContentType("text/html");

        // session is retrieved before getting the writer

        PrintWriter out =null;

        try
        {
            out = response.getWriter();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        ModelAndView mav = new ModelAndView();
        // conditionMap.put("supplierId",
        // user.getSupplier().getId().toString());

        Map<String, Object> conditionMap = new HashMap<String, Object>();

        StringBuffer jsonStr = new StringBuffer();

        List<Code> parentList = codeManager.getChildren(9363);

        //초기 jsontree 형태 값 설정.
        jsonStr.append(" {'text' : '.', 'children' : [ ");

        for ( int i=0; i< parentList.size() ; i++)
        {
            List<Code> childrenList=new ArrayList();

            Code cd=new Code();
            cd= (Code) parentList.get(i);

            String parentName =cd.getName();
            int parentId = cd.getId().intValue();

            //엘리먼트 시작
            jsonStr.append("{task : '"+ parentName + "',");
            jsonStr.append("iconCls : 'task-folder',    expanded : true, ");

            //list형태로 자식 노드를 가지고 온다.
            childrenList= codeManager.getChildren(parentId);

            //하위 노드가 있을 경우.
            if ( childrenList.size()>0 )
            {
                jsonStr.append("children : [ ");
                for ( int j=0 ; j< childrenList.size(); j++)
                {
                    Code childCd=new Code();
                    childCd = (Code) childrenList.get(j);

                    String childrenName = childCd.getName();

                    jsonStr.append("{");
                    jsonStr.append("    task : '"+ childrenName+ "',");
                    jsonStr.append("    iconCls : 'task-folder',");
                    jsonStr.append("        children : []");

                    //마지막 노드가 아닌경우.
                    if (j == (childrenList.size()-1) )
                        jsonStr.append("} ");
                    else
                        jsonStr.append("}, ");

                }
                jsonStr.append("]");
            }
            //하위 노드가 없을 경우.
            else
            {
                jsonStr.append("children : []");
            }

            //마지막 노드인 경우.
            if (i == parentList.size() )
                jsonStr.append("} ");
            else
                jsonStr.append("}, ");

        }

        jsonStr.append("        ]} ");

        StringBuffer strbuf = new StringBuffer();

        // json type tree
        strbuf.append("{'text' : '.',   'children' : [{ task : 'Project: pjt1', duration : 13.25,user : 'Tommy Maintz', iconCls : 'task-folder',    expanded : true,    children : [{       task : 'children1',     duration : 1.25,    user : 'Tommy Maintz',iconCls : 'task-folder',      children : []       },      {   task : 'children2', duration : 12,  user : 'Tommy Maintz',  iconCls : 'task-folder',    expanded : true,    children : []   }   ]}  ]}");

        out.println(jsonStr.toString());
        out.close();
    }
    */

    /**
     *
     *
     * @param request
     * @param response
     * @return json tree type String
     *//*
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(value = "/gadget/system/getJsonTree2")
    public String getJsonTree2()
    {
        // session is retrieved before getting the writer

        PrintWriter out = null;

        ModelAndView mav = new ModelAndView();
        // conditionMap.put("supplierId",
        // user.getSupplier().getId().toString());

        Map<String, Object> conditionMap = new HashMap<String, Object>();

        StringBuffer jsonStr = new StringBuffer();

        List<Code> parentList = codeManager.getParents();

        // 초기 jsontree 형태 값 설정.
        jsonStr.append(" {'text' : '.', 'children' : [ ");

        for (int i = 0; i < parentList.size(); i++)
        {
            List<Code> childrenList = new ArrayList();

            Code cd = new Code();
            cd = (Code) parentList.get(i);

            String parentName = cd.getName();
            int parentId = cd.getId().intValue();

            // 엘리먼트 시작
            jsonStr.append("{task : '" + parentName + "',");
            jsonStr.append("iconCls : 'task-folder',    expanded : true, ");

            // list형태로 자식 노드를 가지고 온다.
            childrenList = codeManager.getChildren(parentId);

            // 하위 노드가 있을 경우.
            if (childrenList.size() > 0)
            {
                jsonStr.append("children : [ ");
                for (int j = 0; j < childrenList.size(); j++)
                {
                    Code childCd = new Code();
                    childCd = (Code) childrenList.get(j);

                    String childrenName = childCd.getName();

                    jsonStr.append("{");
                    jsonStr.append("    task : '" + childrenName + "',");
                    jsonStr.append("    iconCls : 'task-folder',");
                    jsonStr.append("        children : []");

                    // 마지막 노드가 아닌경우.
                    if (j == (childrenList.size() - 1))
                        jsonStr.append("} ");
                    else
                        jsonStr.append("}, ");

                }
                jsonStr.append("]");
            }
            // 하위 노드가 없을 경우.
            else
            {
                jsonStr.append("children : []");
            }

            // 마지막 노드인 경우.
            if (i == parentList.size())
                jsonStr.append("} ");
            else
                jsonStr.append("}, ");
        }

        jsonStr.append("        ]} ");

         * StringBuffer strbuf = new StringBuffer();
         *
         * // json type tree strbuf.append(
         * "{'text' : '.',  'children' : [{ task : 'Project: pjt1', duration : 13.25,user : 'Tommy Maintz', iconCls : 'task-folder',    expanded : true,    children : [{       task : 'children1',     duration : 1.25,    user : 'Tommy Maintz',iconCls : 'task-folder',      children : []       },      {   task : 'children2', duration : 12,  user : 'Tommy Maintz',  iconCls : 'task-folder',    expanded : true,    children : []   }   ]}  ]}"
         * );

        out.println(jsonStr.toString());
        out.close();

        return jsonStr.toString();
    }
*/

    @RequestMapping(value="/gadget/system/customerMax", params="param=customerMaxSelectBox")
    public ModelAndView customerSelectBox() {

        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        int supplierId = user.getRoleData().getSupplier().getId();
//        int emServiceTypeId = codeManager.getCodeByName("Electricity").getId();
//        int gmServiceTypeId = codeManager.getCodeByName("Gas").getId();
//        int wmServiceTypeId = codeManager.getCodeByName("Water").getId();

        int emServiceTypeId = codeManager.getCodeIdByCode(MeterType.EnergyMeter.getServiceType());
        int gmServiceTypeId = codeManager.getCodeIdByCode(MeterType.GasMeter.getServiceType());
        int wmServiceTypeId = codeManager.getCodeIdByCode(MeterType.WaterMeter.getServiceType());

        List<Location> locations = locationManager.getLocationsBySupplierId(supplierId);
        List<Code> serviceType = codeManager.getChildCodes(Code.ENERGY);
//        List<Code> customerType = codeManager.getChildCodes(Code.CUSTOMER_TYPE);
        List<Code> sicList = codeManager.getChildCodes(Code.SIC);
        List<TariffType> tariffTypeEM = tarifftypeManager.getTariffTypeList(supplierId, emServiceTypeId);
        List<TariffType> tariffTypeGM = tarifftypeManager.getTariffTypeList(supplierId, gmServiceTypeId);
        List<TariffType> tariffTypeWM = tarifftypeManager.getTariffTypeList(supplierId, wmServiceTypeId);
        List<Code> creditType = codeManager.getChildCodes(Code.PAYMENT);
        List<Code> status = codeManager.getChildCodes(Code.STATUS);
        List<Map<String,Object>> operator = operatorManager.getLoginId();

        List<Map<String, String>> drSelBox = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", "0");
        map.put("name", "Y");
        drSelBox.add(map);
        map = new HashMap<String, String>();
        map.put("id", "1");
        map.put("name", "N");
        drSelBox.add(map);

        mav.addObject("location", locations);
        mav.addObject("serviceType", serviceType);
//        mav.addObject("customerType", customerType);
        mav.addObject("sicList", sicList);
        mav.addObject("tariffTypeEM", tariffTypeEM);
        mav.addObject("tariffTypeGM", tariffTypeGM);
        mav.addObject("tariffTypeWM", tariffTypeWM);
        mav.addObject("creditType", creditType);
        mav.addObject("status", status);
        mav.addObject("dr", drSelBox);
        mav.addObject("operator", operator);

        return mav;
    }

    /**
     * method name : customerAddSelectBox<b/>
     * method Desc :
     *
     * @return
     * @deprecated
     */
    @Deprecated
	@RequestMapping(value="/gadget/system/customerMax", params="param=customerAddMaxSelectBox")
    public ModelAndView customerAddSelectBox() {
        ModelAndView mav = new ModelAndView("jsonView");

        List<Code> customerType = codeManager.getChildCodes(Code.CUSTOMER_TYPE);

        mav.addObject("customerType", customerType);

        return mav;
    }

    @RequestMapping(value="/gadget/system/customerMini")
    public ModelAndView customerMini() {
        ModelAndView mav = new ModelAndView("gadget/system/customerEmMiniGadget");
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        int supplierId = user.getRoleData().getSupplier().getId();
        mav.addObject("supplierId", supplierId);
        return mav;
    }

    @RequestMapping(value="/gadget/system/customerGmMini")
    public ModelAndView customerGmMini() {
        return new ModelAndView("gadget/system/customerGmMiniGadget");
    }

    @RequestMapping(value="/gadget/system/customerWmMini")
    public ModelAndView customerWmMini() {
        return new ModelAndView("gadget/system/customerWmMiniGadget");
    }

    @RequestMapping(value="/gadget/system/customerHmMini")
    public ModelAndView customerHmMini() {
        return new ModelAndView("gadget/system/customerHmMiniGadget");
    }

    @RequestMapping(value="/gadget/system/customerVcMini")
    public ModelAndView customerVcMini() {
        return new ModelAndView("gadget/system/customerVcMiniGadget");
    }

    //고객상세보기
    @RequestMapping(value="/gadget/system/customerMax", params="param=myCustomerView")
    public ModelAndView getMyCustomerView(@RequestParam("customerId") int customerId,
            HttpServletRequest request , HttpServletResponse response ) {
        ModelAndView model = new ModelAndView("jsonView");

        Customer customer = customerManager.getCustomer(customerId);

        //세션에서 받을 로그인id
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        int supplierId = 0;
        if (user != null && !user.isAnonymous()) {
            int operatorId = (int) user.getAccountId();
            //세션에서 받을 operator id
            Operator operator = operatorManager.getOperator(operatorId);
            supplierId = user.getRoleData().getSupplier().getId();
        }

        //에너지
        List<Code> energyList = codeManager.getEnergyList(customerId);
        //List<SupplyType> energyList = supplyTypeManager.getSupplyTypeBySupplierId(supplierId);

        if (customer != null) {
            if (customer.getAddress2() != null) {
                customer.setAddress2(customer.getAddress2().replaceAll("'", "_"));
            }

            if (customer.getEmail() == null) {
                customer.setEmail("");
            }
            if (customer.getTelephoneNo() == null) {
                customer.setTelephoneNo("");
            }
            if (customer.getMobileNo() == null) {
                customer.setMobileNo("");
            }
        }

        model.addObject("customer", customer);
        model.addObject("energyList", energyList);
        return model;
    }

    //계약정보수정
    @RequestMapping(value="/gadget/system/customerMax", params="param=contractUpdateMax")
    public ModelAndView contractUpdateMax(
            @RequestParam("contractId") int contractId, @RequestParam("customerId") int customerId,
            @RequestParam("serviceType") int serviceType, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("gadget/system/contractUpdateMax");

        //세션에서 받을 로그인id
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        int supplierId = 0;
        if (user != null && !user.isAnonymous()) {
            int operatorId = (int) user.getAccountId();
            //세션에서 받을 operator id
            supplierId = user.getRoleData().getSupplier().getId();
        }
        mav.addObject("supplier" , supplierId);
        mav.addObject("contractId" , contractId);
        mav.addObject("customerId" , customerId);
        mav.addObject("serviceType" , serviceType);
        return mav;
    }

    //계약상세정보
    @RequestMapping(value="/gadget/system/customerMax", params="param=contractDetailMax")
    public ModelAndView contractDetailMax(@RequestParam("contractId") int contractId, @RequestParam("customerId") int customerId ,
            @RequestParam("serviceType") int serviceType) {
        ModelAndView mav = new ModelAndView("gadget/system/contractDetailMax");
        mav.addObject("contractId" , contractId);
        mav.addObject("customerId" , customerId);
        mav.addObject("serviceType" , serviceType);
        return mav;
    }

    //고객상세정보
    @RequestMapping(value="/gadget/system/customerMax", params="param=customerDetailMax")
    public ModelAndView customerDetailMax(@RequestParam("customerId") int customerId) {
        ModelAndView mav = new ModelAndView("gadget/system/customerDetailMax");

        mav.addObject("customerId" , customerId);

        return mav;
    }

    /**
     * @desc 하단 계약정보탭 load action
     *       ( treeGrid에서 사용자및 계약을 클릭했을데 상태를 보여주는 하단 탭을 로드하는 action)
     * @param customerId
     * @return
     */
    @RequestMapping(value="/gadget/system/loadContractBottomTab")
    public ModelAndView loadContractBottomTab(@RequestParam("customerId") int customerId,
            @RequestParam("contractId") int contractId) {
        ModelAndView mav = new ModelAndView("gadget/system/customerBottomInfoTab");

        mav.addObject("customerId" , customerId);
        mav.addObject("contractId" , contractId);

        return mav;
    }

    //고객정보 수정 화면
    @RequestMapping(value="/gadget/system/customerMax", params="param=customerUpdateMax")
    public ModelAndView customerUpdateMax(@RequestParam("customerId") int customerId) {
        ModelAndView mav = new ModelAndView("gadget/system/customerUpdateMax");
        mav.addObject("customerId" , customerId);
        return mav;
    }

    /**
     * @desc 계약추가..
     * @param customerId
     * @param customerNo
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value="/gadget/system/customerMax", params="param=contractAddMax")
    public ModelAndView contractAddMax(@RequestParam("customerId") int customerId , @RequestParam("customerNo") String customerNo,
            HttpServletResponse response, HttpServletRequest request) {
        //세션에서 받을 로그인id
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        int supplierId = 0;
        if (user != null && !user.isAnonymous()) {
            int operatorId = (int) user.getAccountId();
            //세션에서 받을 operator id
            Operator operator = operatorManager.getOperator(operatorId);
            supplierId = user.getRoleData().getSupplier().getId();
        }

        Properties prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
        } catch (IOException e) {
            log.error(e,e);
        }
        
        //arrears와 credit의 초기 Default 값을 화면에 표시해 준다.
        String arrears = prop.getProperty("prepay.init.arrears");
        String currentBalance = prop.getProperty("prepay.init.credit");
        String alertBalance = prop.getProperty("prepay.init.alertBalance");
        //분할납부기능을 사용할 경우 true로 표시한다. 분할납부기능은 ECG요구사항을 기반으로 만들어졌다.(2015년 02월 03일 요구사항 기준)
        String isPartpayment = prop.getProperty("partpayment.use");

        ModelAndView mav = new ModelAndView();

        mav.addObject("customerId" , customerId);
        mav.addObject("customerNo" , customerNo);
        mav.addObject("supplierId" , supplierId);
        mav.addObject("arrears" , arrears);
        mav.addObject("currentBalance" , currentBalance);
        mav.addObject("alertBalance" , alertBalance);
        mav.addObject("isPartpayment" , (isPartpayment == null || "".equals(isPartpayment)) ? false : isPartpayment);

        mav.setViewName("gadget/system/contractAddMax");

        return mav;
    }

    @RequestMapping(value="/gadget/system/customerMax", params="param=getCreditType")
    public ModelAndView getCreditType(@RequestParam("codeId") int codeId) {
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("creditType", codeManager.getCode(codeId) );
        return mav;
    }

    //ID 중복체크
    @RequestMapping(value="/gadget/system/customerMax", params="param=overlapcheck")
    public ModelAndView overLapCheck(@RequestParam("customerNo") String customerNo) {
        ModelAndView mav = new ModelAndView("jsonView");
        boolean checkYN = false;
        int count = customerManager.idOverlapCheck(customerNo);

        if (count == 0) {
            checkYN = true;
        }
        mav.addObject("checkYN", checkYN);
        mav.addObject("count", count);
        return mav;
    }

    //loginId 존재 여부 체크
    @RequestMapping(value="/gadget/system/customerMax", params="param=loginIdCheck")
    public ModelAndView loginIdCheck(@RequestParam("loginId") String loginId, @RequestParam(value="customerNo", required=false) String customerNo) {
        ModelAndView mav = new ModelAndView("jsonView");
        boolean loginIdCheckYN = false;
        int count = customerManager.loginIdOverlapCheck(loginId, customerNo);

        if (count == 0) {
            Boolean loginIdByOperator = operatorManager.checkDuplicateLoginId(loginId);
            if(loginIdByOperator)
                loginIdCheckYN = true;
            else
                count = 1;
        }
        mav.addObject("loginIdCheckYN", loginIdCheckYN);
        mav.addObject("count", count);
        return mav;
    }

    @RequestMapping(value="/gadget/system/customerMax", params="param=createNew")
    public ModelAndView createNew() {
    	ModelAndView mav = new ModelAndView("jsonView");
    	String customerNumber = customerManager.createNewCustomerNumber();
    	mav.addObject("customerNumber", customerNumber);
    	return mav;
    }

    //계약번호 중복체크
    @RequestMapping(value="/gadget/system/customerMax", params="param=numberOverlapcheck")
    public ModelAndView overLapCheck1(@RequestParam("contractNumber") String contractNumber) {
        ModelAndView mav = new ModelAndView("jsonView");
        boolean checkYN = false;
        int count = contractManager.numberOverlapCheck(contractNumber);
        if (count == 0) {
            checkYN = true;
        }
        mav.addObject("checkYN", checkYN);
        mav.addObject("count", count);
        return mav;
    }

    //계약정보리스트
    @RequestMapping(value="/gadget/system/customerMax", params="param=contractEnergy")
    public ModelAndView getMyContractEnergy(@RequestParam("customerId") int customerId ,
            @RequestParam(value="serviceTypeId") int serviceTypeId) {
        ModelMap model = new ModelMap();
        //에너지
        List<Object> myEnergyList = contractManager.getMyEnergy(customerId, serviceTypeId);
        model.addAttribute("myEnergy", myEnergyList);
        return new ModelAndView("jsonView", model);
    }

    //계약정보
    @RequestMapping(value="/gadget/system/customerMax", params="param=contractInfo")
    public ModelAndView getMyContractInfo(@RequestParam("id") int id) {
        ModelMap model = new ModelMap();

        Contract contract = contractManager.getContract(id);

        //계약종별
        TariffType tariff  = contract.getTariffIndex();
        //공급지역
        Location location = contract.getLocation();

        Code status = contract.getStatus();
        Code creditType = contract.getCreditType();
        Code creditStatus = contract.getCreditStatus();

        model.addAttribute("contract", contract);
        model.addAttribute("tariff", tariff);
        model.addAttribute("location", location);
        model.addAttribute("status", status);
        model.addAttribute("creditType", creditType);
        model.addAttribute("creditStatus", creditStatus);
        return new ModelAndView("jsonView", model);
    }

    //서비스타입 가져오기 & 공급사 가져오기
    @RequestMapping(value="/gadget/system/customerMax", params="param=serviceType")
    public ModelAndView getServeceType(
    		@RequestParam(required=false, value="supplier") int supplierId,
    		@RequestParam(required=false, value="customerNo") String customerNo) {

        ModelAndView mav = new ModelAndView("jsonView");
        
        //에너지
        //List<SupplyType> energyList = supplyTypeManager.getSupplyTypeList(supplierId);
        List<Code> energyList = codeManager.getChildCodes(Code.ENERGY);
        //공급지역
        List<Location> locationList = locationManager.getChildrenBySupplierId(supplierId);

        //계약종별
        int emServiceTypeId = codeManager.getCodeIdByCode(MeterType.EnergyMeter.getServiceType());
        List<TariffType> tariffTypeList = tarifftypeManager.getTariffTypeList(supplierId, emServiceTypeId);

        //공급상태
        List<Code> statusList = codeManager.getChildCodes(Code.STATUS);
        //지불타입
        List<Code> creditType = codeManager.getChildCodes(Code.PAYMENT);
        //지불상태
        List<Code> creditStatus = codeManager.getChildCodes(Code.CREDITSTATUS);

        // SIC Code = 14
        List<Code> sicList = codeManager.getChildCodes(Code.SIC);

        List<Object> serviceType2List = contractManager.getServiceType2();
        
        List<Map<String,Object>> debtInfoList = debtEntManager.getDebtInfoByCustomerNo(customerNo, "", "");

        mav.addObject("energyList", energyList);
        mav.addObject("location", locationList);
        mav.addObject("tariffIndex", tariffTypeList);
        mav.addObject("status", statusList);
        mav.addObject("creditType", creditType);
        mav.addObject("creditStatus", creditStatus);
        mav.addObject("serviceType2", serviceType2List);
        mav.addObject("sicList", sicList);
        mav.addObject("supplier", supplierId);
        mav.addObject("debtInfoList",debtInfoList);
        return mav;
    }

    //서비스타입, supplierId 로 tarifftype List 가져오기
    @RequestMapping(value="/gadget/system/customerMax", params="param=getTariffList")
    public ModelAndView getServeceType(@RequestParam("serviceType") int serviceType , @RequestParam("supplier") int supplierId) {
        ModelAndView mav = new ModelAndView("jsonView");
        //계약종별
        List<TariffType> tariffTypeList = tarifftypeManager.getTariffTypeList(supplierId, serviceType);
        mav.addObject("tariff", tariffTypeList);
        return mav;
    }

    //서비스타입 가져오기 & 공급사 가져오기 & 계약정보 수정
    @RequestMapping(value="/gadget/system/customerMax", params="param=contractInfoUpdate")
    public ModelAndView getContractInfo( @RequestParam("contractId") int contractId, @RequestParam("serviceType") int serviceType,
            HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("jsonView");

        //세션에서 받을 로그인id
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        int supplierId = 0;
        if (user != null && !user.isAnonymous()) {
            int operatorId = (int) user.getAccountId();
            //세션에서 받을 operator id
            Operator operator = operatorManager.getOperator(operatorId);
            supplierId = user.getRoleData().getSupplier().getId();
        }

        //에너지
        List<SupplyType> servcieList = supplyTypeManager.getSupplyTypeList(supplierId);
        //공급지역
        List<Location> locationList = locationManager.getChildrenBySupplierId(supplierId);
        //계약종별
        List<TariffType> tariffTypeList = tarifftypeManager.getTariffTypeList(supplierId , serviceType);
        //계약상태
        List<Code> statusList = codeManager.getChildCodes(Code.STATUS);
        //지불타입
        List<Code> creditTypeList = codeManager.getChildCodes(Code.PAYMENT);
        //지불타입
        List<Code> creditStatusList = codeManager.getChildCodes(Code.CREDITSTATUS);
        // SIC Code
        List<Code> sicList = codeManager.getChildCodes(Code.SIC);

        //나의 계약정보 가져오기
        Contract contract = contractManager.getContract(contractId);
        Code service = contract.getServiceTypeCode();
        Supplier supplier_ = contract.getSupplier();
        TariffType tariff  = contract.getTariffIndex();
        Location location = contract.getLocation();
        Code status = contract.getStatus();
        Code creditType = contract.getCreditType();
        Code creditStatus = contract.getCreditStatus();

        mav.addObject("serviceList", servcieList);
        mav.addObject("locationList", locationList);
        mav.addObject("tariffTypeList", tariffTypeList);
        mav.addObject("statusList", statusList);
        mav.addObject("creditTypeList", creditTypeList);
        mav.addObject("creditStatusList", creditStatusList);
        mav.addObject("sicList", sicList);

        mav.addObject("contract", contract);
        mav.addObject("service", service);
        mav.addObject("supplier", supplier_);
        mav.addObject("tariff", tariff);
        mav.addObject("location", location);
        mav.addObject("status", status);
        mav.addObject("creditType", creditType);
        mav.addObject("creditStatus", creditStatus);
        return mav;
    }

    //개별고객 등록
    @RequestMapping(value="/gadget/system/customerMax", params="param=add")
    public ModelAndView addCustomer(HttpServletRequest request,
            @ModelAttribute("customerForm") Customer customer,
            @RequestParam("supplierId") String supplierId,
            SessionStatus status) throws Exception {
        ModelAndView mav = new ModelAndView("jsonView");
        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId.toString()));
        ResultStatus insertResult = ResultStatus.SUCCESS;
        try {        	
        	customer.setCustomerNo(customer.getCustomerNo().trim());
        	
            //demandResponse NULL 처리.
            if(customer.getDemandResponse() == null) customer.setDemandResponse(false);
            customer.setSupplier(supplier);
            String loginId = customer.getLoginId().trim();
            if(loginId != null && !"".equals(loginId)) {
                AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

                customer.setRole(roleManager.getRole(customer.getRoleId()));
                customer.setShowDefaultDashboard(true);
                customer.setPassword(instance.hashPassword(customer.getPassword(), customer.getLoginId()));
                customer.setLastPasswordChangeTime(formatter.format(Calendar.getInstance().getTime()));
                customer.setLoginDenied(false);
            } else {
                customer.setPassword(null);
                customer.setLoginId(null);
                customer.setRoleId(null);
                customer.setRole(null);
                customer.setLastPasswordChangeTime(null);
            }
            customerManager.addCustomer(customer);
            status.setComplete();
            mav.addObject("result", "success");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            insertResult = ResultStatus.FAIL;
//            e.toString();
            mav.addObject("result", "error");
            throw new Exception("저장에 실패 하였습니다.");
        } finally {
            Map<String, Object> logData = new HashMap<String, Object>();
            //로그 저장
            logData.put("deviceType",   TargetClass.Customer);
            logData.put("deviceName",   customer.getCustomerNo());
            logData.put("deviceModel",  null);
            logData.put("resultStatus", insertResult);
            logData.put("regType",      RegType.Manual);
            logData.put("supplier", supplier);
            deviceRegistrationManager.insertDeviceRegLog(logData);
        }

        return mav;
    }

    /**
     *
     * @desc: 새 계약 추가
     * @param request
     * @param contract
     * @param sicId
     * @param meterId
     * @param contractNumber
     * @param curPage
     * @param status
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/gadget/system/customerMax", params = "param=addContract")
    @Deprecated
    public ModelAndView addContract(HttpServletRequest request,
            @ModelAttribute("contractForm") Contract contract,
            @RequestParam("meterId") String meterId,
            // :contractNumber
            @RequestParam("contractNumber") String contractNumber,
            @RequestParam("curPage") String curPage,
            // sicId
            @RequestParam("sicId") String sicId,
            SessionStatus status,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("jsonView");
        String result = "";

        try {
            // 계약일자에 오늘날짜를 설정한다.
            contract.setContractDate(TimeUtil.getCurrentTime());
            ResultStatus insertResult = ResultStatus.SUCCESS;

            if (!StringUtil.nullToBlank(sicId).isEmpty()) {
                Code sicCode = codeManager.getCode(Integer.valueOf(sicId));
                contract.setSic(sicCode);
            }

            Supplier supplier = supplierDao.get(contract.getSupplier().getId());
            try {

                Meter meter = meterManager.getMeter(Integer.parseInt(meterId));

                contract.setMeter(meter);

                // contract num set
                contract.setContractNumber(contractNumber);

                // Contract 의 중복 여부를 체크한다..
                int meterCnt = contractManager.checkContractedMeterYn(meterId);

                // 이미 계약된 미터일 경우.
                if (meterCnt > 0) {

                    // 기존 계약을 가지고 온다.
                    Contract prevContract = contractManager.getContractByMeterId(Integer.parseInt(meterId));

                    // null처리, 기존 메터는
                    prevContract.setMeter(null);

                    // 기존 계약 meter_id를 널로 처리
                    contractManager.updateContract(prevContract);
                }

                // 새로운 계약을 추가.
                contractManager.addContract(contract);

                result = "success";

                status.setComplete();

                /**
                 * @desc : 로그 insert
                 */
                Map<String, Object> logData = new HashMap<String, Object>();
                // 로그 저장
                logData.put("deviceType", TargetClass.Contract);
                logData.put("deviceName", contract.getContractNumber());
                logData.put("deviceModel", null);
                logData.put("resultStatus", insertResult);
                logData.put("regType", RegType.Manual);
                logData.put("supplier", supplier);
                deviceRegistrationManager.insertDeviceRegLog(logData);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                e.printStackTrace();
                insertResult = ResultStatus.FAIL;

                result = "contract insert failed";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
            throw new Exception("저장에 실패 하였습니다.");
        }
        mav.addObject("result", result);

        // 페이지값 세팅
        mav.addObject("curPage", curPage);
        return mav;
    }

    /**
     * method name : createContract<b/>
     * method Desc : Contract 를 생성한다.
     *
     * @param request
     * @param contract
     * @param mdsId
     * @param contractNumber
     * @param sicId
     * @param customerId
     * @param prevContractId
     * @param status
     * @param response
     * @return
     * @throws Exception
     */
/*    @RequestMapping(value = "/gadget/system/customerMax", params = "param=createContract")
    public ModelAndView createContract(HttpServletRequest request,
            @ModelAttribute("contractForm") Contract contract,
            @RequestParam("mdsId") String mdsId,
            @RequestParam("contractNumber") String contractNumber,
            @RequestParam("sicId") Integer sicId,
            @RequestParam(value="customerId", required=false) Integer customerId,
            @RequestParam(value="prevContractId", required=false) Integer prevContractId,
            @RequestParam(value="serviceType2", required=false) String serviceType2,
            SessionStatus status,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("jsonView");

        //세션에서 받을 로그인id
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        if (user == null || user.isAnonymous()) {
            mav.addObject("result", "Session Error");
            return mav;
        }

        Integer operatorId = (int) user.getAccountId();
        String result = null;
        ResultStatus insertResult = ResultStatus.SUCCESS;
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        Supplier supplier = supplierDao.get(contract.getSupplier().getId());

        try {
            conditionMap.put("contract", contract);
            conditionMap.put("mdsId", mdsId);
            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("sicId", sicId);
            conditionMap.put("customerId", customerId);
            conditionMap.put("prevContractId", prevContractId);
            conditionMap.put("startDatetime",  TimeUtil.getCurrentTime());
            conditionMap.put("operatorId", operatorId);
            conditionMap.put("serviceType2", serviceType2);

            contractManager.insertContract(conditionMap);
            result = "success";
            status.setComplete();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            insertResult = ResultStatus.FAIL;
            result = "contract insert failed";
        }

        // 로그 insert
        Map<String, Object> logData = new HashMap<String, Object>();
        // 로그 저장
        logData.put("deviceType", TargetClass.Contract);
        logData.put("deviceName", contract.getContractNumber());
        logData.put("deviceModel", null);
        logData.put("resultStatus", insertResult);
        logData.put("regType", RegType.Manual);
        logData.put("supplier", supplier);
        deviceRegistrationManager.insertDeviceRegLog(logData);   

        mav.addObject("result", result);
        return mav;
    }
*/
    
    /**
     * method name : createContract<b/>
     * method Desc : Contract 를 생성한뒤 Current Balance를 저장하기위한 정보 리턴
     *
     * @param request
     * @param contract
     * @param mdsId
     * @param contractNumber
     * @param sicId
     * @param customerId
     * @param prevContractId
     * @param status
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/gadget/system/customerMax", params = "param=createContract")
    public ModelAndView createContract(HttpServletRequest request,
            @ModelAttribute("contractForm") Contract contract,
            @RequestParam("mdsId") String mdsId,
            @RequestParam("contractNumber") String contractNumber,
            @RequestParam("sicId") Integer sicId,
            @RequestParam(value="customerId", required=false) Integer customerId,
            @RequestParam(value="prevContractId", required=false) Integer prevContractId,
            @RequestParam(value="serviceType2", required=false) String serviceType2,
            @RequestParam(value="currentbalanceValue", required=false) String currentbalanceValue,
            @RequestParam(value="debtSaveInfo", required=false) String debtSaveInfo,
            @RequestParam(value="isPartpayment") Boolean isPartpayment,
            SessionStatus status,
            HttpServletResponse response) throws Exception {
    	
        ModelAndView mav = new ModelAndView("jsonView");

        //세션에서 받을 로그인id
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        if (user == null || user.isAnonymous()) {
            mav.addObject("result", "Session Error");
            return mav;
        }

        Operator operator =  operatorManager.getOperatorByLoginId(user.getLoginId());
        contract.setOperator(operator);
        
        if(contract.getContractNumber() != null){
        	contract.setContractNumber(contract.getContractNumber().trim());
        }
        
        Integer operatorId = (int) user.getAccountId();
        String result = null;
        ResultStatus insertResult = ResultStatus.SUCCESS;
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        Supplier supplier = supplierDao.get(contract.getSupplier().getId());
        Contract newContract = null;
        
        Code creditType = codeManager.getCode(contract.getCreditType().getId());

        //처음 미수금 임력시 그 값이 FirstArrears(초기 미수금 ) 값에 저장되도록 한다.
        //이는 ECG의 경우 해당사항으로 미수금을 분할납부 할경우 한회에 얼마씩 납부해야하는지 계산하기 위함이다. 
        if(isPartpayment && Code.PREPAYMENT.equals(creditType.getCode())) {
        	contract.setFirstArrears(contract.getCurrentArrears());
        	contract.setArrearsPaymentCount(0);
        } else {
        	contract.setArrearsContractCount(null);
        }
        
        try {
            /**
			*화면에서 입력받아온 값이 빈값이 경우 command.properties에 등록된 기본 값으로 저장한다.
			*해당 값 : currentCredit, currentArrears
			*/
            Properties prop = new Properties();
    		try {
    			prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
    		} catch (IOException e) {
    			log.error(e,e);
    		}
            
            if(currentbalanceValue == null || "".equals(currentbalanceValue)){
            	String initCredit = prop.getProperty("prepay.init.credit");
                contract.setCurrentCredit(initCredit == null || initCredit.isEmpty() ? null : Double.parseDouble(initCredit));
            } else {
            	contract.setCurrentCredit(Double.parseDouble(currentbalanceValue));
            }
            
            String initArrears = prop.getProperty("prepay.init.arrears");
            initArrears = (initArrears == null || initArrears.isEmpty()) ? null : initArrears;
            if(contract.getCurrentArrears() == null || "".equals(contract.getCurrentArrears())) {
            	contract.setCurrentArrears((initArrears == null) ? null : Double.parseDouble(initArrears));
            }
            
            if(contract.getPrepaymentThreshold() == null || "".equals(contract.getPrepaymentThreshold())) {
            	String initAlertBalance = prop.getProperty("prepay.init.alertBalance");
            	contract.setPrepaymentThreshold(initAlertBalance == null || initAlertBalance.isEmpty() ? null : Integer.parseInt(initAlertBalance));
            }
            
            List<Map<String,Object>> debtInfoList = new ArrayList<Map<String,Object>>();
            
            if(debtSaveInfo != null) {
	            JSONArray jsonArr = JSONArray.fromObject(debtSaveInfo);
	            
	            int jsonArrSize = jsonArr.size();
	            for (int i = 0; i < jsonArrSize; i++) {
	            	Map<String, Object> map = new HashMap<String,Object>();
	    			JSONObject jsonObj = jsonArr.getJSONObject(i);
	    			Iterator it = jsonObj.keys();
	    			while(it.hasNext()) {
	    				String key = it.next().toString(); 
	    				map.put(key, jsonObj.get(key));
	    			}
	    			debtInfoList.add(map);
	    		}
            }
            conditionMap.put("contract", contract);
            conditionMap.put("mdsId", mdsId);
            conditionMap.put("contractNumber", contractNumber.trim());
            conditionMap.put("sicId", sicId);
            conditionMap.put("customerId", customerId);
            conditionMap.put("prevContractId", prevContractId);
            conditionMap.put("startDatetime",  TimeUtil.getCurrentTime());
            conditionMap.put("operatorId", operatorId);
            conditionMap.put("serviceType2", serviceType2);
            conditionMap.put("isPartpayment", isPartpayment);
            conditionMap.put("initArrears", initArrears);

            if(debtInfoList.size() > 0) {
            	conditionMap.put("debtSaveInfo", debtInfoList);
                debtEntManager.modifyDebtInfo(conditionMap);
            }

            newContract = contractManager.insertContract(conditionMap);
            
            result = "success";
            status.setComplete();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            insertResult = ResultStatus.FAIL;
            result = "contract insert failed";
        }

        // 로그 insert
        Map<String, Object> logData = new HashMap<String, Object>();
        // 로그 저장
        logData.put("deviceType", TargetClass.Contract);
        logData.put("deviceName", contract.getContractNumber());
        logData.put("deviceModel", null);
        logData.put("resultStatus", insertResult);
        logData.put("regType", RegType.Manual);
        logData.put("supplier", supplier);
        deviceRegistrationManager.insertDeviceRegLog(logData);   

        mav.addObject("result", result);
        
        return mav;
    }
    
    
    //Customer 업데이트
    @RequestMapping(value="/gadget/system/customerMax", params="param=update")
    public ModelAndView updateCustomer(@ModelAttribute("customer") Customer customer,
             SessionStatus status) throws Exception {
        ModelAndView mav = new ModelAndView("jsonView");

        try {
            AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
            if(customer.getPassword() != "")
                customer.setPassword(instance.hashPassword(customer.getPassword(), customer.getLoginId()));
            if(customer.getLoginId() == null || "".equals(customer.getLoginId())) {
                customer.setPassword(null);
                customer.setRole(null);
                customer.setShowDefaultDashboard(null);
                customer.setLoginDenied(null);
                customer.setLastPasswordChangeTime(null);
                customer.setLoginId(null);
            } else {
                Role role = roleManager.getRole(customer.getRoleId());
                customer.setRole(role);
                customer.setShowDefaultDashboard(true);
                customer.setLoginDenied(false);
            }

            customerManager.updateCustomer(customer);
            status.setComplete();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception("변경에 실패 하였습니다.");
        }
        mav.addObject("result", "success");
        return mav;
    }

    /**
     * @Desc: Contract 업데이트
     * @param contract
     * @param meterId
     * @param sicId
     * @param pPrepaymentThreshold
     * @param response
     * @param request
     * @param status
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/gadget/system/customerMax", params="param=contractUpdate")
    @Deprecated
    public ModelAndView updateContract(@ModelAttribute("contract") Contract contract,
             //  @RequestParam("startDatetime") String applyDate,
             @RequestParam("meterId") String meterId,
             // @RequestParam("threshold") String threshold,
             @RequestParam("sicId") Integer sicId,
             @RequestParam("locationId2") Integer locationId2,
             // @RequestParam("sicId2") Integer sicId2,
             HttpServletResponse response,
             HttpServletRequest request,
             SessionStatus status) throws Exception {

        ModelAndView mav = new ModelAndView("jsonView");

        //세션에서 받을 로그인id
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        if (user == null || user.isAnonymous()) {
            mav.addObject("result", "Session Error");
            return mav;
        }

        int operatorId = (int) user.getAccountId();
        String startDatetime = TimeUtil.getCurrentDay() + "000000";
        String writeDatetime = DateUtil.formatDate(new Date(), "yyyyMMdd") + "000000";

        //세션에서 받을 operator id
        Operator operator = operatorManager.getOperator(operatorId);
        //id 로 저장되어 있는 값 db에서 가져오기 : beforeValue
        Contract beforeContract = contractManager.getContract(contract.getId());

        ContractChangeLog changeLog = new ContractChangeLog();

        //적용일자 변경시 ContractChangeLog 저장
        /*if (!StringUtil.nullToBlank(applyDate).equals(beforeContract.getApplyDate())) {
            changeLog.setAfterValue(applyDate);
            changeLog.setBeforeValue(beforeContract.getApplyDate());
            changeLog.setChangeField("applyDate");
            changeLog.setStartDatetime(startDatetime);

            changeLog.setWriteDatetime(DateUtil.formatDate(new Date(), "yyyyMMdd") + "000000");

            changeLog.setCustomer(beforeContract.getCustomer());
            changeLog.setOperator(operator);
            changeLog.setContract(beforeContract);

            try {
                contractChangeLogManager.addContractChangeLog(changeLog);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("로그 저장에 실패 하였습니다.");
            }
        }*/

        //서비스타입 변경시 ContractChangeLog 저장
        Integer beforeServiceTypeId = 0;
        if (beforeContract.getServiceTypeCode() != null) beforeServiceTypeId = beforeContract.getServiceTypeCode().getId();

        if (!beforeServiceTypeId.equals(contract.getServiceTypeCode().getId())) {
            changeLog.setAfterValue(contract.getServiceTypeCode().getId().toString());
            changeLog.setBeforeValue(beforeServiceTypeId.toString());
            changeLog.setChangeField("serviceType");
            changeLog.setStartDatetime(startDatetime);
            changeLog.setWriteDatetime(writeDatetime);
            changeLog.setCustomer(beforeContract.getCustomer());
            changeLog.setOperator(operator);
            changeLog.setContract(beforeContract);
            try {
                contractChangeLogManager.addContractChangeLog(changeLog);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new Exception("로그 저장에 실패 하였습니다.");
            }
        }

        // 공급지역 변경시
        Integer beforeLocationId = 0;
        if (beforeContract.getLocation() != null) {
            beforeLocationId = beforeContract.getLocation().getId();
        }

        if (contract.getLocation() != null) {
            if (!beforeLocationId.equals(contract.getLocation().getId())) {
                changeLog = new ContractChangeLog();
                changeLog.setAfterValue(contract.getLocation().getId().toString());
                changeLog.setBeforeValue(beforeLocationId.toString());
                changeLog.setChangeField("location");
                changeLog.setStartDatetime(startDatetime);
                changeLog.setWriteDatetime(writeDatetime);
                changeLog.setCustomer(beforeContract.getCustomer());
                changeLog.setOperator(operator);
                changeLog.setContract(beforeContract);

                contractChangeLogManager.addContractChangeLog(changeLog);
            }
        }

        // 계약종별 변경시
        Integer beforeTariffIndex = 0;

        if (beforeContract.getTariffIndex() != null) {
            beforeTariffIndex = beforeContract.getTariffIndex().getId();
        }

        if (contract.getTariffIndex() != null) {
            if (!beforeTariffIndex.equals(contract.getTariffIndex().getId())) {
                changeLog = new ContractChangeLog();
                changeLog.setAfterValue(contract.getTariffIndex().getId().toString());
                changeLog.setBeforeValue(beforeTariffIndex.toString());
                changeLog.setChangeField("tariffIndex");
                changeLog.setStartDatetime(startDatetime);
                changeLog.setWriteDatetime(writeDatetime);
                changeLog.setCustomer(beforeContract.getCustomer());
                changeLog.setOperator(operator);
                changeLog.setContract(beforeContract);

                try {
                    contractChangeLogManager.addContractChangeLog(changeLog);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new Exception("로그 저장에 실패 하였습니다.");
                }
            }
        }

        // 계약용량 변경시
        Double beforeContractDemand = 0d;
        if (beforeContract.getContractDemand() != null) {
            beforeContractDemand = beforeContract.getContractDemand();
        }

        if (contract.getContractDemand() != null) {
            if (!beforeContractDemand.equals(contract.getContractDemand())) {
                changeLog = new ContractChangeLog();
                changeLog.setAfterValue(String.valueOf(contract.getContractDemand()));
                changeLog.setBeforeValue(beforeContractDemand.toString());
                changeLog.setChangeField("contractDemand");
                changeLog.setStartDatetime(startDatetime);
                changeLog.setWriteDatetime(writeDatetime);
                changeLog.setCustomer(beforeContract.getCustomer());
                changeLog.setOperator(operator);
                changeLog.setContract(beforeContract);
                try {
                    contractChangeLogManager.addContractChangeLog(changeLog);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new Exception("로그 저장에 실패 하였습니다.");
                }
            }
        }

        // 공급상태 변경시
        Integer beforeStatus = 0;
        if (beforeContract.getStatus() != null) {
            beforeStatus = beforeContract.getStatus().getId();
        }

        if (contract.getStatus() != null) {
            if (!beforeStatus.equals(contract.getStatus())) {
                changeLog = new ContractChangeLog();
                changeLog.setAfterValue(contract.getStatus().toString());
                changeLog.setBeforeValue(beforeStatus.toString());
                changeLog.setChangeField("status");
                changeLog.setStartDatetime(startDatetime);
                changeLog.setWriteDatetime(writeDatetime);
                changeLog.setCustomer(beforeContract.getCustomer());
                changeLog.setOperator(operator);
                changeLog.setContract(beforeContract);
                try {
                    contractChangeLogManager.addContractChangeLog(changeLog);
                    status.setComplete();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new Exception("로그 저장에 실패 하였습니다.");
                }
            }
        }

        // 지불타입 변경시
        Integer beforeCreditType = 0;
        if (beforeContract.getCreditType() != null) {
            beforeCreditType = beforeContract.getCreditType().getId();
        }

        if (contract.getCreditType() != null) {
            if (!beforeCreditType.equals(contract.getCreditType())) {
                changeLog = new ContractChangeLog();
                changeLog.setAfterValue(contract.getCreditType().toString());
                changeLog.setBeforeValue(beforeCreditType.toString());
                changeLog.setChangeField("creditType");
                changeLog.setStartDatetime(startDatetime);
                changeLog.setWriteDatetime(writeDatetime);
                changeLog.setCustomer(beforeContract.getCustomer());
                changeLog.setOperator(operator);
                changeLog.setContract(beforeContract);
                try {
                    contractChangeLogManager.addContractChangeLog(changeLog);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    e.printStackTrace();
                    throw new Exception("로그 저장에 실패 하였습니다.");
                }
            }
        }

        // 지불상태 변경시
        Integer beforeCreditStatus = 0;
        if (beforeContract.getCreditStatus() != null) {
            beforeCreditStatus = beforeContract.getCreditStatus().getId();
        }

        if (contract.getCreditStatus() != null) {
            if (!beforeCreditStatus.equals(contract.getCreditStatus())) {
                changeLog = new ContractChangeLog();
                changeLog.setAfterValue(contract.getCreditStatus().toString());
                changeLog.setBeforeValue(beforeCreditStatus.toString());
                changeLog.setChangeField("creditStatus");
                changeLog.setStartDatetime(startDatetime);
                changeLog.setWriteDatetime(writeDatetime);
                changeLog.setCustomer(beforeContract.getCustomer());
                changeLog.setOperator(operator);
                changeLog.setContract(beforeContract);

                contractChangeLogManager.addContractChangeLog(changeLog);
            }
        }

        // Contract Number변경시
        String beforeContractNo = null;
        if (beforeContract.getContractNumber() != null) {
            beforeContractNo = beforeContract.getContractNumber();
        }

        if (contract.getContractNumber() != null) {
            if (!beforeContractNo.equals(contract.getContractNumber())) {
                changeLog = new ContractChangeLog();
                changeLog.setAfterValue(contract.getContractNumber());
                changeLog.setBeforeValue(beforeContractNo);
                changeLog.setChangeField("contractNumber");
                changeLog.setStartDatetime(startDatetime);
                changeLog.setWriteDatetime(writeDatetime);
                changeLog.setCustomer(beforeContract.getCustomer());
                changeLog.setOperator(operator);
                changeLog.setContract(beforeContract);

                contractChangeLogManager.addContractChangeLog(changeLog);
            }
        }

        // 잔액통보 변경시
        // Double beforeCurrentCredit = 0d;

        Integer prevPrepaymentThreshold = 0;
        if (beforeContract.getPrepaymentThreshold() != null) {
            prevPrepaymentThreshold = beforeContract.getPrepaymentThreshold();
        }

        /*
         * if (pPrepaymentThreshold=="null" || pPrepaymentThreshold.equals(null) || pPrepaymentThreshold.length() <0)
         * pPrepaymentThreshold= "";
         */

        // if (pPrepaymentThreshold !="")

        if (contract.getPrepaymentThreshold() != null) {

            // int prepaymentThreshold= Integer.parseInt(pPrepaymentThreshold);

            if (prevPrepaymentThreshold != contract.getPrepaymentThreshold()) {
                changeLog = new ContractChangeLog();
                changeLog.setAfterValue(contract.getPrepaymentThreshold().toString());
                changeLog.setBeforeValue(prevPrepaymentThreshold.toString());
                changeLog.setChangeField("prepaymentThreshold");
                changeLog.setStartDatetime(startDatetime);
                changeLog.setWriteDatetime(writeDatetime);
                changeLog.setCustomer(beforeContract.getCustomer());
                changeLog.setOperator(operator);
                changeLog.setContract(beforeContract);

                // 로그 저장.
                contractChangeLogManager.addContractChangeLog(changeLog);
            }
        }

        // Contract 의 중복 여부를 체크한다..
        int meterCnt = contractManager.checkContractedMeterYn(meterId);

        /**
         *
         * @desc: 이미 계약된 미터일 경우.기존 계약의 미터 id를 null로 처리
         *
         *        기존 매터 update 처리
         */
        if (meterCnt > 0) {
            // 기존 계약을 가지고 온다.
            Contract prevContract = contractManager.getContractByMeterId(Integer.parseInt(meterId));

            // 기존 메터는 null처리
            prevContract.setMeter(null);

            // 기존 계약 meter_id를 널로 처리
            contractManager.updateContract(prevContract);
        }

        /**
         * @desc: 현재 미터 처리 로직.
         */
        // 현재 미터값이 null로 온경우..
        if (meterId.equals("-1")) {
            contract.setMeter(null);
        } else {
            contract.setMeter(meterManager.getMeter(Integer.parseInt(meterId)));
        }

        Code sicCode = null;

        // SIC 추가
        if (sicId != null) {
            sicCode = codeManager.getCode(sicId);
            contract.setSic(sicCode);
        } else {
            contract.setSic(null);
        }

        // SIC 변경시
        Integer beforeSicId = null;
        // if (beforeContract.getSic() != null) beforeSicId = beforeContract.getSic().getId();
        if (beforeContract.getSic() != null) {
            beforeSicId = beforeContract.getSicCodeId();
        }

        if (!((sicId == null && beforeSicId == null) || (sicId != null && sicId.equals(beforeSicId)))) {
            changeLog = new ContractChangeLog();
            changeLog.setAfterValue((sicId != null) ? sicId.toString() : null);
            changeLog.setBeforeValue((beforeSicId != null) ? beforeSicId.toString() : null);
            changeLog.setChangeField("sic");
            changeLog.setStartDatetime(startDatetime);
            changeLog.setWriteDatetime(writeDatetime);
            changeLog.setCustomer(beforeContract.getCustomer());
            changeLog.setOperator(operator);
            changeLog.setContract(beforeContract);
            try {
                contractChangeLogManager.addContractChangeLog(changeLog);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new Exception("로그 저장에 실패 하였습니다.");
            }
        }

        try {
            Contract contract1 = contractManager.getContract(contract.getId());

            // contract1.setApplyDate(applyDate);
            contract1.setTariffIndex(contract.getTariffIndex());
            contract1.setServiceTypeCode(contract.getServiceTypeCode());

            // location
            Location tempLocation = locationManager.getLocation(locationId2);

            contract1.setLocation(tempLocation);

            // sic
            contract1.setSic(sicCode);

            // contract1.setLocation(contract.getLocation());
            contract1.setContractDemand(contract.getContractDemand());
            contract1.setStatus(contract.getStatus());
            contract1.setCreditType(contract.getCreditType());
            contract1.setCreditStatus(contract.getCreditStatus());
            contract1.setContractNumber(contract.getContractNumber());
            contract1.setPrepaymentThreshold(contract.getPrepaymentThreshold());
            // contract1.setCurrentCredit(contract.getCurrentCredit());

            if (contract.getPrepaymentThreshold() != null) {
                int prepaymentthreshold = contract.getPrepaymentThreshold();

                contract1.setPrepaymentThreshold(prepaymentthreshold);
            }

            contract1.setMeter(contract.getMeter());

            contractManager.updateContract(contract1);

            status.setComplete();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
            throw new Exception("변경에 실패 하였습니다.");
        }

        mav.addObject("result", "Modification Success");
        return mav;
    }

    /**
     * method name : modifyContract<b/>
     * method Desc : Contract 업데이트
     *
     * @param contract
     * @param meterId Meter.mdsId - 미터 아이디
     * @param sicId
     * @param locationId2
     * @param fromInsert Contract 생성화면에서 호출여부
     * @param barcode ecg 선불 관련 barcode
     * @param response
     * @param request
     * @param status
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/gadget/system/customerMax", params="param=updateContract")
    public ModelAndView modifyContract(@ModelAttribute("contract") Contract contract,
             @RequestParam("mdsId") String mdsId,
             @RequestParam("sicId") Integer sicId,
             @RequestParam("locationId2") Integer locationId2,
             @RequestParam("customerId") Integer customerId,
             @RequestParam(value="prevContractId", required=false) Integer prevContractId,
             @RequestParam(value="fromInsert", required=false) Boolean fromInsert,
             @RequestParam(value="serviceType2", required=false) String serviceType2,
             @RequestParam(value="isPartpayment", required=false) Boolean isPartpayment,
             @RequestParam(value="initArrears", required=false) String initArrears,
             @RequestParam(value="debtSaveInfo", required=false) String debtSaveInfo,
             String barcode,
             HttpServletResponse response,
             HttpServletRequest request,
             SessionStatus status) throws Exception {

        ModelAndView mav = new ModelAndView("jsonView");
        //세션에서 받을 로그인id
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        List<Map<String,Object>> debtInfoList = new ArrayList<Map<String,Object>>();

        if(debtSaveInfo != null) {
	        JSONArray jsonArr = JSONArray.fromObject(debtSaveInfo);
	        
	        int jsonArrSize = jsonArr.size();
	        for (int i = 0; i < jsonArrSize; i++) {
	        	Map<String, Object> map = new HashMap<String,Object>();
				JSONObject jsonObj = jsonArr.getJSONObject(i);
				Iterator it = jsonObj.keys();
				while(it.hasNext()) {
					String key = it.next().toString(); 
					map.put(key, jsonObj.get(key));
				}
				debtInfoList.add(map);
			}
        }
        if (user == null || user.isAnonymous()) {
            mav.addObject("result", "Session Error");
            return mav;
        }
        Integer operatorId = (int) user.getAccountId();
        String startDatetime = TimeUtil.getCurrentTime();
        String writeDatetime = startDatetime;
        initArrears = StringUtil.nullToBlank(initArrears);

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("contract", contract);
        conditionMap.put("mdsId", mdsId);
        conditionMap.put("sicId", sicId);
        conditionMap.put("locationId2", locationId2);
        conditionMap.put("customerId", customerId);
        conditionMap.put("prevContractId", ObjectUtils.defaultIfNull(prevContractId, contract.getId()));
        conditionMap.put("fromInsert", fromInsert);
        conditionMap.put("operatorId", operatorId);
        conditionMap.put("startDatetime", startDatetime);
        conditionMap.put("writeDatetime", writeDatetime);
        conditionMap.put("barcode", barcode);
        conditionMap.put("serviceType2", serviceType2);
        conditionMap.put("isPartpayment", isPartpayment);
        conditionMap.put("initArrears", "".equals(initArrears) ? 0.0 : Double.parseDouble(initArrears));
        contractManager.modifyContract(conditionMap);
        
        Map<String, Object> condition = new HashMap<String, Object>();
        if(debtInfoList.size() > 0) {
        	condition.put("debtSaveInfo", debtInfoList);
            debtEntManager.modifyDebtInfo(condition);
        }

        status.setComplete();
        mav.addObject("result", "Modification Success");
        return mav;
    }

    @RequestMapping(value="/gadget/system/customerMax", params="param=excel")
    public ModelAndView insertCustomer(@RequestParam("filename") String filename) throws Exception {
        ModelAndView mav = new ModelAndView("jsonView");
        List<?> list = ExcelUtil.readExcel("C:/test.xls");
        mav.addObject("result", list);
        return mav;
    }

    //Contract 삭제
    @RequestMapping(value="/gadget/system/customerMax", params="param=contractDelete")
    @Deprecated
    public ModelAndView contractDelete(@RequestParam("contractId") int contractId) {
        ModelAndView mav = new ModelAndView("jsonView");

//        contractChangeLogManager.contractLogDelete(contractId);
        contractManager.contractDelete(contractId);
        mav.addObject("result", "success");
        return mav;
    }

    /**
     * method name : deleteContract<b/>
     * method Desc : Contract 삭제
     *
     * @param contractId
     * @return
     */
    @RequestMapping(value = "/gadget/system/customerMax", params = "param=deleteContract")
    public ModelAndView deleteContract(@RequestParam("contractId") int contractId) {
        ModelAndView mav = new ModelAndView("jsonView");

        //세션에서 받을 로그인id
//        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        int operatorId = (int)user.getAccountId();

        // contractChangeLogManager.contractLogDelete(contractId);
        contractManager.deleteContract(contractId, operatorId);
        mav.addObject("result", "success");
        return mav;
    }

    //Customer 삭제
    @RequestMapping(value="/gadget/system/customerMax", params="param=customerDelete")
    public ModelAndView customerDelete(@RequestParam("customerId") int customerId) {
        ModelAndView mav = new ModelAndView("jsonView");

//        contractChangeLogManager.contractLogAllDelete(customerId);
//        contractManager.contractAllDelete(customerId);
        customerManager.customerDelete(customerId);

        mav.addObject("result", "success");
        return mav;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value="/gadget/system/customerMax", params="param=customerList")
    @Deprecated
    public ModelAndView getCustomerList(
            @RequestParam("page") String page,
            @RequestParam("pageSize") String pageSize,
            @RequestParam("customerNo") String customerNo,
            @RequestParam("customerName") String customerName,
            @RequestParam("location") String location,
            @RequestParam("tariffIndex") String tariffIndex,
            @RequestParam("contractDemand") String contractDemand,
            @RequestParam("creditType") String creditType,
            @RequestParam("mdsId") String mdsId,
            @RequestParam("status") String status,
            @RequestParam("dr") String dr,
            @RequestParam("customerType") String customerType,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("address") String address,
            @RequestParam("serviceType") String serviceType,
            @RequestParam("serviceTypeTab") String serviceTypeTab) {
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("page", page);
        conditionMap.put("pageSize", pageSize);
        conditionMap.put("customerNo", customerNo);
        conditionMap.put("customerName", customerName);
        conditionMap.put("location", location);
        conditionMap.put("tariffIndex", tariffIndex);
        conditionMap.put("contractDemand", contractDemand);
        conditionMap.put("creditType", creditType);
        conditionMap.put("mdsId", mdsId);
        conditionMap.put("status", status);
        conditionMap.put("dr", dr);
        conditionMap.put("customerType", customerType);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("address", address);
        conditionMap.put("serviceType", serviceType);
        conditionMap.put("serviceTypeTab", serviceTypeTab);
        conditionMap.put("supplierId", user.getSupplier().getId().toString());
        log.debug("startDate["+startDate+"], endDate["+endDate+"]");
        ModelMap modelMap = new ModelMap();

        List<?> rstList = contractManager.getContracts(conditionMap);

        /*전체 고객 탭 목록 조회시 첫번째 Row의 자식인 데이터를 자동으로 보여주기 위해...*/
        if (rstList.size() > 0 && "".equals(serviceTypeTab)) {
            Map<String, Object> oneDepth = (Map<String, Object>)rstList.get(0);
            if (oneDepth.size() > 0) {
                /*oneDepth에 담은 twoDepth의 리스트()의 첫번째 데이터를 가져온다.*/
            	if (((List<Map<String, Object>>)oneDepth.get("children")).size() > 0) {
                    Map<String, Object> twoDepth = ((List<Map<String, Object>>)oneDepth.get("children")).get(0);

                    modelMap.addAttribute("customerId", twoDepth.get("customerId"));
                    modelMap.addAttribute("serviceType", twoDepth.get("serviceType"));
                    modelMap.addAttribute("contractId", twoDepth.get("contractId"));
                    modelMap.addAttribute("customerNo", twoDepth.get("customerNo"));
                    modelMap.addAttribute("serviceTypeName", twoDepth.get("serviceTypeName"));
                } else {
                    /*twoDepth의 자료가 없다면 oneDepth의 자료를 가져와 리턴한다.*/
                    modelMap.addAttribute("customerName", oneDepth.get("customerName"));
                    modelMap.addAttribute("customerId", oneDepth.get("customerId"));
                    modelMap.addAttribute("customerNo", oneDepth.get("customerNo"));
                }
            } else {
            }
        } else { /*EM,GM,WM 탭 고객목록 조회시 첫번째 Row의 자식인 데이터를 자동으로 보여주기 위해...*/
            if (rstList.size() > 0) {
                Map<String, Object>  firstData = (Map<String, Object>)rstList.get(0);

                modelMap.addAttribute("customerId", firstData.get("CUSTOMERID"));
                modelMap.addAttribute("serviceType", firstData.get("SERVICETYPE"));
                modelMap.addAttribute("contractId", firstData.get("CONTRACTID"));
                modelMap.addAttribute("customerNo", firstData.get("CUSTOMERNO"));
                modelMap.addAttribute("serviceTypeName", firstData.get("SERVICETYPENAME"));
            }
        }

        modelMap.addAttribute("gridDatas", rstList);
        modelMap.addAttribute("total", contractManager.getContractCount(conditionMap));

        if ("".equals(serviceTypeTab)) {
            modelMap.addAttribute("totalCustomer", customerManager.getTotalCustomer(conditionMap));
        }

        log.info(modelMap);
        return new ModelAndView("jsonView", modelMap);
    }

    /**
     * method name : getCustomerListByType<b/>
     * method Desc : Customer Contract Management 맥스 가젯에서 Service Type 별 고객리스트를 조회한다.
     *
     * @param customerNo
     * @param customerName
     * @param location
     * @param tariffIndex
     * @param contractDemand
     * @param creditType
     * @param mdsId
     * @param status
     * @param dr
     * @param sicIds
     * @param startDate
     * @param endDate
     * @param address
     * @param serviceType
     * @param serviceTypeTab
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    @RequestMapping(value="/gadget/system/customerMax", params="param=customerListByType")
    public ModelAndView getCustomerListByType(@RequestParam("customerNo") String customerNo,
            @RequestParam("contractNumber") String contractNumber,
    		@RequestParam("customerName") String customerName,
            @RequestParam("location") Integer location,
            @RequestParam("tariffIndex") Integer tariffIndex,
            @RequestParam("contractDemand") String contractDemand,
            @RequestParam("creditType") Integer creditType,
            @RequestParam("mdsId") String mdsId,
            @RequestParam("status") Integer status,
            @RequestParam("dr") String dr,
            @RequestParam("sicIds") String sicIds,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("address") String address,
            @RequestParam("serviceType") String serviceType,
            @RequestParam("serviceTypeTab") String serviceTypeTab) {
        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        Integer page = Integer.valueOf(request.getParameter("page"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("customerNo", customerNo);
        conditionMap.put("customerName", customerName);
        conditionMap.put("location", location);
        conditionMap.put("tariffIndex", tariffIndex);
        conditionMap.put("contractDemand", contractDemand);
        conditionMap.put("creditType", creditType);
        conditionMap.put("mdsId", mdsId);
        conditionMap.put("status", status);
        conditionMap.put("dr", dr);
        conditionMap.put("sicIds", sicIds);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("address", address);
        conditionMap.put("serviceType", serviceType);
        conditionMap.put("serviceTypeTab", serviceTypeTab);
        conditionMap.put("supplierId", user.getSupplier().getId());

        List<Map<String, Object>> result = contractManager.getCustomerListByType(conditionMap);
        mav.addObject("result", result);
        mav.addObject("totalCount", contractManager.getCustomerListByTypeTotalCount(conditionMap));
        return mav;
    }

    /*  *//**
     * @desc 전체 고객리스트를 fetch
     * @param page
     * @param pageSize
     * @return
     *//*
    @SuppressWarnings("rawtypes")
    @RequestMapping(value="/gadget/system/getAllCustomerList")
    public ModelAndView getCustomerAllList(
            @RequestParam("page") String page,
            @RequestParam("pageSize") String pageSize
            )
    {
        ModelAndView mav=new ModelAndView();

        ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("page", page);
        conditionMap.put("pageSize", pageSize);

        List contracts = contractManager.getContracts(conditionMap);
        mav.addObject("contracts", contracts);

        List customers = customerManager.getCustomers();
        mav.addObject("root", customers);

        int customersCnt = customerManager.getTotalCustomer(conditionMap);
        mav.addObject("customersCnt", customersCnt);
        mav.setViewName("jsonView");

        return mav;
    }
*/

    /**
     *
     * @desc Entire customer /Contract all List fetch action
     * @param customerNo
     * @param customerName
     * @param location
     * @param tariffIndex
     * @param contractDemand
     * @param creditType
     * @param mdsId
     * @param status
     * @param dr
     * @param customerType
     * @param startDate
     * @param endDate
     * @param address
     * @param serviceType
     * @param serviceTypeTab
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value="/gadget/system/customerMax", params="param=customerExtList")
    public ModelAndView getCustomerExtTreeList(
    		@RequestParam("contractNumber") String contractNumber,
            @RequestParam("customerNo") String customerNo,
            @RequestParam("customerName") String customerName,
            @RequestParam("location") String location,
            @RequestParam("tariffIndex") String tariffIndex,
            @RequestParam("contractDemand") String contractDemand,
            @RequestParam("creditType") String creditType,
            @RequestParam("mdsId") String mdsId,
            @RequestParam("status") String status,
            @RequestParam("dr") String dr,
            @RequestParam("customerType") String customerType,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("address") String address,
            @RequestParam("serviceType") String serviceType,
            @RequestParam("serviceTypeTab") String serviceTypeTab,
            @RequestParam(value="operatorId", required=false) String operatorId) {

        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        int page = Integer.parseInt(request.getParameter("page"));

        int limit = Integer.parseInt(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        Supplier supplier = user.getSupplier();
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        if(startDate != null && !"".equals(startDate)) {
        	startDate = TimeLocaleUtil.getDBDate(startDate, 8, lang, country);
        }
        if(endDate != null && !"".equals(endDate)) {
        	endDate = TimeLocaleUtil.getDBDate(endDate, 8, lang, country);
        }

        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", limit);
        conditionMap.put("customerNo", customerNo);
        conditionMap.put("customerName", customerName);
        conditionMap.put("location", location);
        conditionMap.put("tariffIndex", tariffIndex);
        conditionMap.put("contractDemand", contractDemand);
        conditionMap.put("creditType", creditType);
        conditionMap.put("mdsId", mdsId);
        conditionMap.put("status", status);
        conditionMap.put("dr", dr);
        conditionMap.put("customerType", customerType);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("address", address);
        conditionMap.put("serviceType", serviceType);
        conditionMap.put("serviceTypeTab", serviceTypeTab);
        conditionMap.put("supplierId", supplier.getId().toString());
        conditionMap.put("operatorId", operatorId);
        log.info("startDate["+startDate+"], endDate["+endDate+"]");

        List contractsTreeList = contractManager.getContractsTree(conditionMap);

        // 고객 리스트
        mav.addObject("root", contractsTreeList);
        String totalCount = contractManager.getContractCount(conditionMap);
        // 고객수
        mav.addObject("total", totalCount);

        Integer totalContractCount = contractManager.getTotalContractCount(conditionMap);
        mav.addObject("totalContractCount", totalContractCount);
        return mav;
    }

    @RequestMapping(value="/gadget/system/customerExcelDownloadPopup")
    public ModelAndView customerMaxExcelDownloadPopup() {
        ModelAndView mav 
        = new ModelAndView("/gadget/ExcelDownloadPopup");
        return mav;
    }

    @RequestMapping(value="/gadget/system/customerMaxExcelMake")
    public ModelAndView getCustomerMaxExcel(
            @RequestParam("condition[]")    String[] condition,
            @RequestParam("fmtMessage[]")   String[] fmtMessage,
            @RequestParam("filePath")       String filePath) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        boolean isLast = false;
        Long total = 0L; // 데이터 조회건수
        Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

        final String logPrefix = "customerList";//9

        List<Map<String,Object>> result = null;
        try {
            Map<String, Object> conditionMap = new HashMap<String, Object>();

            conditionMap.put("page", 1);
            conditionMap.put("pageSize", null);
            conditionMap.put("contractNumber", condition[0]);
            conditionMap.put("customerNo", condition[1]);
            conditionMap.put("customerName", condition[2]);
            conditionMap.put("location", condition[3]);

            conditionMap.put("mdsId", condition[7]);
            conditionMap.put("customerType", condition[10]);
            conditionMap.put("address", condition[13]);
            conditionMap.put("serviceType", condition[14]);
            conditionMap.put("serviceTypeTab", condition[15]);
            conditionMap.put("supplierId", condition[16]);

            result = contractManager.getContractsTree(conditionMap);
            total = new Integer(result.size()).longValue();

            mav.addObject("total", total);
            if (total <= 0) {
                return mav;
            }

            sbFileName.append(logPrefix);

            sbFileName.append(TimeUtil.getCurrentTimeMilli());//14

            // message 생성
            msgMap.put("title",            "Customer List");
            msgMap.put("no",               fmtMessage[0]);
            msgMap.put("customerNo",       fmtMessage[1]);
            msgMap.put("customerName",     fmtMessage[2]);
            msgMap.put("address",          fmtMessage[3]);

            // check download dir
            File downDir = new File(filePath);

            if (downDir.exists()) {
                File[] files = downDir.listFiles();

                if (files != null) {
                    String filename = null;
                    String deleteDate;

                    deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(),
                            Calendar.DAY_OF_MONTH, -10); // 10일 이전 일자
                    boolean isDel = false;

                    for (File file : files) {

                        filename = file.getName();
                        isDel = false;

                        // 파일길이 : 26이상, 확장자 : xls|zip
                        if (filename.length() > 29
                                && (filename.endsWith("xls") || filename
                                        .endsWith("zip"))) {
                            // 10일 지난 파일들 삭제
                            if (filename.startsWith(logPrefix)
                                    && filename.substring(9, 17).compareTo(
                                            deleteDate) < 0) {
                                isDel = true;
                            }

                            if (isDel) {
                                file.delete();
                            }
                        }
                        filename = null;
                    }
                }
            } else {
                // directory 가 없으면 생성
                downDir.mkdir();
            }

            // create excel file
            CustomerMaxMakeExcel wExcel = new CustomerMaxMakeExcel();
            int cnt = 1;
            int idx = 0;
            int fnum = 0;
            int splCnt = 0;
			if (total <= maxRows) {
	            sbSplFileName = new StringBuilder();
	            sbSplFileName.append(sbFileName);
	            sbSplFileName.append(".xls");
	            wExcel.writeReportExcel(result, msgMap, isLast, filePath,
	                    sbSplFileName.toString(),maxRows);
	            fileNameList.add(sbSplFileName.toString());
			} else {
				for (int i = 0; i < total; i++) {
					if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
						sbSplFileName = new StringBuilder();
						sbSplFileName.append(sbFileName);
						sbSplFileName.append('(').append(++fnum)
								.append(").xls");

						list = result.subList(idx, (i + 1));

						wExcel.writeReportExcel(list, msgMap, isLast, filePath,
								sbSplFileName.toString(),maxRows);
						fileNameList.add(sbSplFileName.toString());
						list = null;
						splCnt = cnt;
						cnt = 0;
						idx = (i + 1);
					}
					cnt++;
				}
			}
			
            // create zip file
            StringBuilder sbZipFile = new StringBuilder();
            sbZipFile.append(sbFileName).append(".zip");

            ZipUtils zutils = new ZipUtils();
            zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);

            // return object
            mav.addObject("filePath", filePath);
            mav.addObject("fileName", fileNameList.get(0));
            mav.addObject("zipFileName", sbZipFile.toString());
            mav.addObject("fileNames", fileNameList);

        } catch (ParseException e) {
            e.printStackTrace();
            log.error(e.toString(), e);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString(), e);
        }

        return mav;
    }

    @RequestMapping(value="/gadget/system/customerMax", params="param=customerTreeChildList")
    public ModelAndView getCustomerTreeChildList(
            @RequestParam("contractNumber") String contractNumber,
            @RequestParam("location") String location,
            @RequestParam("tariffIndex") String tariffIndex,
            @RequestParam("contractDemand") String contractDemand,
            @RequestParam("creditType") String creditType,
            @RequestParam("mdsId") String mdsId,
            @RequestParam("status") String status,
            @RequestParam("dr") String dr,
            @RequestParam("customerType") String customerType,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("serviceType") String serviceType,
            @RequestParam("serviceTypeTab") String serviceTypeTab,
            @RequestParam(value="operatorId", required=false) String operatorId,
            @RequestParam("node") String node) {
        ModelAndView mav = new ModelAndView("treeJsonView");
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        Supplier supplier = user.getSupplier();
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        if(startDate != null && !"".equals(startDate)) {
        	startDate = TimeLocaleUtil.getDBDate(startDate, 8, lang, country);
        }
        if(endDate != null && !"".equals(endDate)) {
        	endDate = TimeLocaleUtil.getDBDate(endDate, 8, lang, country);
        }
        
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("location", location);
        conditionMap.put("tariffIndex", tariffIndex);
        conditionMap.put("contractDemand", contractDemand);
        conditionMap.put("creditType", creditType);
        conditionMap.put("mdsId", mdsId);
        conditionMap.put("status", status);
        conditionMap.put("dr", dr);
        conditionMap.put("customerType", customerType);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("serviceType", serviceType);
        conditionMap.put("serviceTypeTab", serviceTypeTab);
        conditionMap.put("customerId", Integer.valueOf(node));
        conditionMap.put("supplierId", supplier.getId().toString());
        conditionMap.put("operatorId", operatorId);
        log.info("startDate["+startDate+"], endDate["+endDate+"]");

        List<Map<String, Object>> contractsChildTreeList = contractManager.getContractsChildTree(conditionMap);

        //계약 리스트
        mav.addObject("result", contractsChildTreeList);
        return mav;
    }

    /**
     * @desc 하단 계약정보를 가지고 온다.
     * @param contractId
     * @param supplierId
     * @return
     */
    @RequestMapping(value="/gadget/system/customerMax", params="param=getContract")
    public ModelAndView getContract(
            @RequestParam("contractId") int contractId ,
            @RequestParam("supplierId") String supplierId,
            @RequestParam("customerNo") String customerNo) {
        ModelMap modelMap = new ModelMap();

        Map<String, Object> contractinfo= contractManager.getContractInfo(contractId, Integer.parseInt(supplierId));
        List<Map<String,Object>> debtInfo=debtEntManager.getDebtInfoByCustomerNo(customerNo,"","");
        modelMap.addAttribute("contract",contractinfo );
        modelMap.addAttribute("debtInfo",debtInfo);

        return new ModelAndView("jsonView", modelMap);
    }
    
    /**
     * @desc 하단 계약정보를 가지고 온다.
     * @param contractNumber
     * @param supplierId
     * @return
     */
    @RequestMapping(value="/gadget/system/getContractByContractNumber")
    public ModelAndView getContractByContractNumber(
            @RequestParam("contractNumber") String contractNumber,
            @RequestParam("supplierId") Integer supplierId) {
        ModelMap modelMap = new ModelMap();

        List<Contract> contract= contractManager.getContractByContractNumber2(contractNumber, supplierId);

        modelMap.addAttribute("contract",contract);

        return new ModelAndView("jsonView", modelMap);
    }
    
    /**
     * @desc 계약의 분할납부 정보를 가지고 온다.
     * @param contractNumber
     * @param supplierId
     * @return
     */
    @RequestMapping(value="/gadget/system/getPartpayInfoByContractNumber")
    public ModelAndView getPartpayInfoByContractNumber(
            @RequestParam("contractNumber") String contractNumber,
            @RequestParam("supplierId") Integer supplierId) {
        ModelMap modelMap = new ModelMap();

        Map<String,Object> contract= contractManager.getPartpayInfoByContractNumber(contractNumber, supplierId);

        modelMap.addAttribute("contract",contract);

        return new ModelAndView("jsonView", modelMap);
    }

    @RequestMapping(value="/gadget/system/customerMax", params="param=contractInfoUpdateForm")
    public ModelAndView getContractInfoUpdateForm(
            @RequestParam("contractId") String contractId,
            @RequestParam("serviceType") String serviceType,
            @RequestParam("customerNo") String customerNo) {

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();

        //서비스
        List<Code> serviceList = codeManager.getChildCodes(Code.ENERGY);
        //공급지역
        List<Location> locationList = locationManager.getChildrenBySupplierId(supplierId);
        //계약종별
        List<TariffType> tariffTypeList = tarifftypeManager.getTariffTypeList(supplierId, Integer.parseInt(serviceType));

        //전체 계약 종별 가지고 오기
        List<TariffType> allTariffTypeList = tarifftypeManager.getTariffTypeList(supplierId, -1);

        //계약상태
        List<Code> statusList = codeManager.getChildCodes(Code.STATUS);
        //지불타입
        List<Code> creditTypeList = codeManager.getChildCodes(Code.PAYMENT);
        //지불타입
        List<Code> creditStatusList = codeManager.getChildCodes(Code.CREDITSTATUS);

        List<Object> serviceType2List = contractManager.getServiceType2();

        // SIC Code = 14

        List<Code> sicList = codeManager.getChildCodes(Code.SIC);
        
        List<Map<String,Object>> debtTypeList = debtEntManager.getDebtInfoByCustomerNo(customerNo,"","");

        //나의 계약정보 가져오기
        Contract contract = contractManager.getContract(Integer.parseInt(contractId));
        Code service = contract.getServiceTypeCode();

        Supplier supplier_ = contract.getSupplier();
        TariffType tariff  = contract.getTariffIndex();
        Location location = contract.getLocation();
        Code status = contract.getStatus();
        Code creditType = contract.getCreditType();
        Code creditStatus = contract.getCreditStatus();
        String applyDate = contract.getApplyDate();
        Meter meter = contract.getMeter();
        Code sic = contract.getSic();
//        String receiptNumber = contract.getReceiptNumber();
//        Double amountPaid = contract.getAmountPaid();


        Double usageThreshold = (meter == null ? 0d : (meter.getUsageThreshold() == null ? 0d :meter.getUsageThreshold()));

        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("serviceList", serviceList);
        modelMap.addAttribute("locationList", locationList);
        modelMap.addAttribute("tariffTypeList", tariffTypeList);

        //전체 계약 종별 by  supplierId
        modelMap.addAttribute("allTariffTypeList", allTariffTypeList);

        modelMap.addAttribute("statusList", statusList);
        modelMap.addAttribute("creditTypeList", creditTypeList);
        modelMap.addAttribute("creditStatusList", creditStatusList);
        modelMap.addAttribute("sicList", sicList);
        modelMap.addAttribute("serviceType2List", serviceType2List);
        modelMap.addAttribute("debtTypeList", debtTypeList);
        
        modelMap.addAttribute("contract", contract);
        modelMap.addAttribute("applyDate", applyDate);
        modelMap.addAttribute("service", service);
        modelMap.addAttribute("supplier", supplier_);
        modelMap.addAttribute("tariff", tariff);
        modelMap.addAttribute("location", location);
        modelMap.addAttribute("status", status);
        modelMap.addAttribute("creditType", creditType);
        modelMap.addAttribute("creditStatus", creditStatus);
        modelMap.addAttribute("usageThreshold", usageThreshold);
        modelMap.addAttribute("meter", meter);

        return new ModelAndView("jsonView", modelMap);
    }

    @RequestMapping(value="/gadget/system/customerMax", params="param=contractChangeLog")
    @Deprecated
    public ModelAndView getContractChangeLog(
        @RequestParam("startDate") String startDate,
        @RequestParam("endDate") String endDate,
        @RequestParam("contractId") String contractId,
        @RequestParam("supplierId") String supplierId,
        @RequestParam("page") String page,
        @RequestParam("pageSize") String pageSize) {

        int firstResult = Integer.parseInt(page) * Integer.parseInt(pageSize);

        Set<Condition> set = new HashSet<Condition>();
        set.add(new Condition("contract.id", new Object[] { Integer.parseInt(contractId) }, null, Restriction.EQ));
        set.add(new Condition("writeDatetime", new Object[] { startDate, endDate }, null, Restriction.BETWEEN));
        set.add(new Condition("", new Object[] { firstResult }, null, Restriction.FIRST));
        set.add(new Condition("", new Object[] { Integer.parseInt(pageSize) }, null, Restriction.MAX));

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("total", contractChangeLogManager.getContractChangeLogCountByListCondition(set));
        mav.addObject("gridDatas", contractChangeLogManager.getContractChangeLogByListCondition(set));

        return mav;
    }

    /**
     * method name : getContractChangeLogList<b/>
     * method Desc : Customer Contract Management 맥스 가젯에서 Contract ChangeLog 를 조회한다.
     *
     * @param supplierId
     * @param contractId
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value="/gadget/system/getContractChangeLogList")
    public ModelAndView getContractChangeLogList(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("contractId") Integer contractId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        ModelAndView mav = new ModelAndView("jsonView");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        Integer page = Integer.valueOf(request.getParameter("page"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("contractId", contractId);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);

        List<Map<String, Object>> result = contractChangeLogManager.getContractChangeLogList(conditionMap);
        mav.addObject("result", result);
        mav.addObject("totalCount", contractChangeLogManager.getContractChangeLogListTotalCount(conditionMap));
        return mav;
    }

//    @RequestMapping(value="/gadget/system/getPrepaymentLog", params="param=prepaymentLog")
//    public ModelAndView getPrepaymentLog(
//        @RequestParam("startDate") String startDate,
//        @RequestParam("endDate") String endDate,
//        @RequestParam("contractId") String contractId,
//        @RequestParam("supplierId") String supplierId,
//        @RequestParam("page") String page,
//        @RequestParam("pageSize") String pageSize ) {
//        int firstResult = Integer.parseInt(page) * Integer.parseInt(pageSize);
//
//        Set<Condition> set = new HashSet<Condition>();
//        set.add(new Condition("contract.id", new Object[] { Integer.parseInt(contractId) }, null, Restriction.EQ));
//        set.add(new Condition("lastTokenDate", new Object[] { startDate, endDate }, null, Restriction.BETWEEN));
//        set.add(new Condition("", new Object[] { firstResult }, null, Restriction.FIRST));
//        set.add(new Condition("", new Object[] { Integer.parseInt(pageSize) }, null, Restriction.MAX));
//
//        ModelAndView mav = new ModelAndView("jsonView");
//        mav.addObject("total", prepaymentLogManager.getPrepaymentLogCountByListCondition(set));
//        mav.addObject("gridDatas", prepaymentLogManager.getPrepaymentLogByListCondition(set, supplierId));
//
//        return mav;
//    }

    /**
     * method name : getPrepaymentLog<b/>
     * method Desc : Customer Contract Management 맥스 가젯에서 Prepayment Log 를 조회한다.
     *
     * @param startDate
     * @param endDate
     * @param contractId
     * @param supplierId
     * @return
     */
    /** 2014.12.29 simhanger
    더이상 사용하지 않는 기능으로 주석처리함. 향후 필요 없을시 삭제 필요함.
    
    @RequestMapping(value="/gadget/system/getPrepaymentLog")
    public ModelAndView getPrepaymentLog(@RequestParam("startDate") String startDate,
          @RequestParam("endDate") String endDate,
          @RequestParam("contractId") String contractId,
          @RequestParam("supplierId") String supplierId) {
        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        Contract contract = contractManager.getContract(Integer.parseInt(contractId));
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("contractNumber", contract.getContractNumber());
        conditionMap.put("searchStartMonth", startDate);
        conditionMap.put("searchEndMonth", endDate);
        conditionMap.put("allFlag", true);

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        conditionMap.put("supplierId", Integer.parseInt(supplierId));
        conditionMap.put("contractId", contractId);

        List<Map<String, Object>> result = prepaymentMgmtCustomerManager.getChargeHistoryForCustomer(conditionMap);
        mav.addObject("result", result);
        mav.addObject("totalCount", prepaymentMgmtCustomerManager.getChargeHistoryTotalCount(conditionMap));

        return mav;
    }
    */

    @RequestMapping(value="/gadget/system/customerMax", params="param=contractBilling")
    public ModelAndView getContractBilling(
        @RequestParam("startDate") String startDate,
        @RequestParam("endDate") String endDate,
        @RequestParam("contractId") String contractId,
        @RequestParam("serviceTypeTab") String serviceTypeTab) {
        Map<String, String> conditionMap = new HashMap<String, String>();
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("contractId", contractId);
        conditionMap.put("serviceTypeTab", serviceTypeTab);

        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("chartDatas", billingManager.getContractBillingChartData(conditionMap));

        return new ModelAndView("jsonView", modelMap);
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/gadget/system/getCustomerContractInfo")
    public ModelAndView getCustomerContractInfo(@RequestParam("supplierId") String supplierId,
            @RequestParam("serviceType") String serviceType,
            @RequestParam(value="type", required=false) String type) {
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("serviceType", serviceType);
        conditionMap.put("type", StringUtil.nullToBlank(type));

        ModelAndView mav = new ModelAndView("jsonView");
        List customerContractInfoList = new ArrayList();

        Map<String, Object> customerContractInfoListMap = customerManager.getCustomerContractInfo(conditionMap);

        mav.addObject("result", customerContractInfoListMap);

        customerContractInfoList = (ArrayList) customerContractInfoListMap.get("grid");

        int totalCnt = customerContractInfoList.size();

        mav.addObject("customerContractInfoList", customerContractInfoList);
        mav.addObject("totalCnt", totalCnt);

        return mav;
    }

    @RequestMapping(value="/gadget/contract/getContractListByMeter")
    public ModelAndView getContractListByMeter(
              @RequestParam("customerNo") String customerNo
            , @RequestParam("customerName") String customerName
            , @RequestParam("location") String location
            , @RequestParam("mdsId") String mdsId
            , @RequestParam("customerType") String customerType
            , @RequestParam("address") String address
            , @RequestParam("serviceType") String serviceType) {

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        Map<String, String> conditionMap = new HashMap<String, String>();

        conditionMap.put("customerNo"   , customerNo);
        conditionMap.put("customerName" , customerName);
        conditionMap.put("location"     , location);
        conditionMap.put("mdsId"        , mdsId);
        conditionMap.put("customerType" , customerType);
        conditionMap.put("address"      , address);
        conditionMap.put("serviceType"  , serviceType);
        conditionMap.put("supplierId"   , user.getSupplier().getId().toString());

        ModelMap modelMap = new ModelMap();

        List<?> rstList = contractManager.getContractListByMeter(conditionMap);

        modelMap.addAttribute("gridDatas", rstList);
        return new ModelAndView("jsonView", modelMap);
    }

    /**
     * method name : updateMeterIdByContract<b/>
     * method Desc :
     *
     * @param meterId
     * @param contractId
     * @return
     */
    @RequestMapping(value="/gadget/contract/updateMeterIdByContract")
    public ModelAndView updateMeterIdByContract(@RequestParam("meterId") int meterId,
            @RequestParam("contractId") int contractId) {

        // 과거 Update
        Contract contractOld = contractManager.getContractByMeterId(meterId);
        contractOld.setMeter(null);
        contractManager.updateContract(contractOld);

        // 신규 Update
        Contract contract = contractManager.getContract(contractId);
        Meter meter = meterManager.getMeter(meterId);

        contract.setMeter(meter);

        contractManager.updateContract(contract);

        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("result", "Y");
        return new ModelAndView("jsonView", modelMap);
    }

    /**
     * @desc 전체 메터 리스트를 가지고온다
     * @param meterId
     * @param contractId
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(value="/gadget/contract/getMeterList")
    @Deprecated
    public ModelAndView getMeterList(
      //        @RequestParam("meterId")    int meterId
        //    , @RequestParam("contractId") int contractId
            @RequestParam("page") String page
            ,@RequestParam("pageSize") String pageSize
            ,@RequestParam("mdsId") String mdsId
            ) {

        Map<String, String> conditionMap = new HashMap<String, String>();

        conditionMap.put("page", page);
        conditionMap.put("pageSize", pageSize);

        if (mdsId != "") {
            conditionMap.put("mdsId", mdsId);
        }

        List<Meter> meterList = new ArrayList();

        // 메터리스트를 가지고 온다.
        meterList = contractManager.getMeterList(conditionMap);

        // 메터리스트 카운트를 가지고 온다.
        String meterListDataCount = contractManager.getMeterListDataCount(conditionMap);

        ModelMap modelMap = new ModelMap();

        modelMap.addAttribute("meterList", meterList);
        modelMap.addAttribute("meterListDataCount", meterListDataCount);

        return new ModelAndView("jsonView", modelMap);
    }

    /**
     * method name : getMeterGridList<b/>
     * method Desc : 전체 Meter List 를 가져온다.
     *
     * @param mdsId
     * @return
     */
    @RequestMapping(value = "/gadget/contract/getMeterGridList")
    public ModelAndView getMeterGridList (@RequestParam("mdsId") String mdsId) {
        ModelAndView mav = new ModelAndView("jsonView");
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("mdsId", mdsId);

        List<Map<String, Object>> result = contractManager.getMeterGridList(conditionMap);

        Integer totalCount = contractManager.getMeterGridListDataCount(conditionMap);

        mav.addObject("result", result);
        mav.addObject("totalCount", totalCount);

        return mav;
    }

    @RequestMapping(value="/gadget/system/customerAddMax")
    public ModelAndView customerAddMax() {
        return new ModelAndView("gadget/system/customerAddMax");
    }

    @RequestMapping(value="/gadget/system/customerAddMaxBulk")
    public ModelAndView customerAddMaxBulk() {
        return new ModelAndView("gadget/system/customerAddMaxBulk");
    }

    @RequestMapping(value="/gadget/system/getTempFileName")
    public ModelAndView getTempFileName(HttpServletRequest request, HttpServletResponse response)
                    throws ServletRequestBindingException, IOException {

        String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");

        MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multiReq.getFile("userfile");

        String filename = multipartFile.getOriginalFilename();
        if (filename == null || "".equals(filename)) {
            return null;
        }

        String tempPath = contextRoot+"temp";

        if (!FileUploadHelper.exists(tempPath)) {
            File savedir = new File(tempPath);
            savedir.mkdir();
        }
        File uFile = new File(FileUploadHelper.makePath(tempPath, filename));

        if (FileUploadHelper.exists(FileUploadHelper.makePath(tempPath, filename))) {
            if (FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(tempPath, filename))) {
                multipartFile.transferTo(uFile);
            }
        } else {
            multipartFile.transferTo(uFile);
        }

        String filePath = tempPath+"/"+filename;

        String ext = filePath.substring(filePath.lastIndexOf(".")+1).trim();

        ModelAndView mav = new ModelAndView("gadget/system/customerBulkFile");
        mav.addObject("tempFileName", filePath);
        mav.addObject("titleName", customerManager.getTitleName(filePath, ext));

        return mav;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value="/gadget/system/getCustomerBulkFile")
    public ModelAndView getCustomerBulkFile( @RequestParam("filePath") String filePath) {

        List<Object> resultList = null;

        try {
            File file = new File(filePath.trim());

            if (!file.exists() || !file.isFile() || !file.canRead()) {
                throw new IOException(filePath);
            }

            Map<String, Object> result = null;

            if (!"".equals(StringUtils.defaultIfEmpty(filePath, ""))) {
                String ext = filePath.substring(filePath.lastIndexOf(".")+1).trim();
                if ("xls".equals(ext)) {
                    result = customerManager.readExcelXLS(filePath);
                } else if ("xlsx".equals(ext)) {
                    result = customerManager.readExcelXLSX(filePath);
                }
            }

            if (result != null) {
                resultList = (List<Object>)result.get("file");
                // File Copy & Temp File Delete
                FileUploadHelper.removeExistingFile(filePath);
            }
        } catch(FileNotFoundException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("resultList", resultList);

        return mav;
    }

    @RequestMapping(value="/gadget/system/insertCustomerBulkFile")
    public ModelAndView setCustomerBulkFile(@RequestParam("filePath") String filePath,
            @RequestParam("supplierId") int supplierId) {
        String resultMsg = null;
        try {
            File file = new File(filePath.trim());

            if (!file.exists() || !file.isFile() || !file.canRead()) {
                throw new IOException(filePath);
            }

            Map<String, Object> result = null;

            if (!"".equals(StringUtils.defaultIfEmpty(filePath, ""))) {
                String ext = filePath.substring(filePath.lastIndexOf(".")+1).trim();
                if("xls".equals(ext)){
                    result = customerManager.saveExcelXLS(filePath, supplierId);
                }
                else if("xlsx".equals(ext)){
                    result = customerManager.saveExcelXLSX(filePath, supplierId);
                }
            }

            if( result != null ){
                FileUploadHelper.removeExistingFile(filePath);
            }
            resultMsg = result.get("resultMsg").toString();
        } catch(FileNotFoundException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
            resultMsg=e.getMessage();
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("resultMsg", resultMsg.toString());

        return mav;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value="/gadget/system/getContractBulkFile")
    public ModelAndView getContractBulkFile(@RequestParam("filePath") String filePath,
            @RequestParam("supplierId") int supplierId) {

        List<Object> resultList = null;

        try {
            File file = new File(filePath.trim());

            if (!file.exists() || !file.isFile() || !file.canRead()) {
                throw new IOException(filePath);
            }

            Map<String, Object> result = null;

            if (!"".equals(StringUtils.defaultIfEmpty(filePath, ""))) {
                String ext = filePath.substring(filePath.lastIndexOf(".")+1).trim();
                if ("xls".equals(ext)) {
                    result = contractManager.readExcelXLS(filePath, supplierId);
                } else if ("xlsx".equals(ext)) {
                    result = contractManager.readExcelXLSX(filePath, supplierId);
                }
            }

            if (result != null) {
                resultList = (List<Object>)result.get("file");
                // File Copy & Temp File Delete
                FileUploadHelper.removeExistingFile(filePath);
            }

        } catch(FileNotFoundException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("resultList", resultList);

        return mav;
    }

    @RequestMapping(value="/gadget/system/insertContractBulkFile")
    public ModelAndView setContractBulkFile(@RequestParam("filePath") String filePath,
            @RequestParam("supplierId") int supplierId) {

        String resultMsg = null;
        try {
            File file = new File(filePath.trim());

            if (!file.exists() || !file.isFile() || !file.canRead()) {
                throw new IOException(filePath);
            }

            Map<String, Object> result = null;

            if (!"".equals(StringUtils.defaultIfEmpty(filePath, ""))) {
                String ext = filePath.substring(filePath.lastIndexOf(".")+1).trim();
                if ("xls".equals(ext)) {
                    result = contractManager.saveExcelXLS(filePath, supplierId);
                } else if ("xlsx".equals(ext)) {
                    result = contractManager.saveExcelXLSX(filePath, supplierId);
                }
            }

            if (result != null) {
                // File Copy & Temp File Delete
                FileUploadHelper.removeExistingFile(filePath);
            }
            resultMsg = result.get("resultMsg").toString();
        } catch(FileNotFoundException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
//            e.getMessage();
            resultMsg=e.getMessage();
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("resultMsg", resultMsg.toString());

        return mav;
    }

    /**
     * 공급 제한 용량 변경시 IHD에 보내는 업데이트 정보
     * @param mdsId
     * @return
     */
    @RequestMapping(value="/gadget/system/getCustomerUpdateInfosMessage")
    public ModelAndView getCustomerUpdateInfosMessage(@RequestParam("mdsId") String mdsId,
            @RequestParam("groupId") String groupId) {

        Set<GroupMember> members = groupMemberDao.getGroupMemberById(Integer.parseInt(groupId));
        Modem modem = null;
        MCU mcu = null;
        for (GroupMember groupMember : members) {
        	if(modem == null) {
        		modem = modemManager.getModem(groupMember.getMember());
        	}
            if (modem != null) {
                mcu = mcuManager.getMCU(modem.getMcuId());
            }
        }

        ResultStatus status = ResultStatus.FAIL;

        String rtnStr = "";
        String rtnStrMax = "";

        Meter meter_EM = meterDao.get(mdsId);

//      createDateTime                              0x01
//      customerUpdateInfo_supplyCapacityLimit_E    0x02

        Date date = new Date();
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        rtnStr += getTypeFrame("01", formatter.format(date));

        if (meter_EM != null && meter_EM.getContract() != null &&
                meter_EM.getContract().getContractDemand() != null &&
                !meter_EM.getContract().getContractDemand().equals("")) {
            rtnStr += getTypeFrame("02", meter_EM.getContract().getContractDemand().toString());
            log.debug("\\\\\\\\\\\\\\\34X02. meter_EM.getContract().getContractDemand() : " + meter_EM.getContract().getContractDemand().toString());
        }

        ////데이터를 집중기로 보내기위해 보낼 데이터 변환 - Start
        byte sendTarget = Hex.encode("53")[0];
        byte receiveTarget = Hex.encode("49")[0];
        byte CMD = Hex.encode("34")[0];
        byte[] dataFrame = Hex.encode(rtnStr);
        byte[] data = new byte[dataFrame.length+1];
        data = DataUtil.append(Hex.encode("34"), dataFrame);
        byte[] requestBytes = new byte[6+data.length];
        byte[] head = new byte[3];
        head[0] = Hex.encode("02")[0];
        head[1] = sendTarget;
        head[2] = receiveTarget;
        byte[] dataLength=null;
        dataLength = DataUtil.get2ByteToInt(data.length);
        DataUtil.convertEndian(dataLength);

        // + DATALENGTH
        requestBytes = DataUtil.append(head, dataLength);
        // + DATA
        requestBytes = DataUtil.append(requestBytes, data);
        // + ETX
        requestBytes = DataUtil.append(requestBytes, Hex.encode("03"));
        ////데이터를 집중기로 보내기위해 보낼 데이터 변환 - End

        try {
            cmdOperationUtil.cmdSendIHDData(mcu.getSysID(), modem.getDeviceSerial(), requestBytes);
            status = ResultStatus.SUCCESS;
            rtnStrMax = status.name();
        } catch (Exception e) {
            rtnStrMax = e.getMessage();
            e.printStackTrace();
        }
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("status", status);
        mav.addObject("rtnStr", rtnStrMax);

        return mav;
    }

    /**
     * method name : getCheckContractByMeterId<b/>
     * method Desc : Contract 등록/수정 시 선택한 Meter 가 현재 다른 Contract 에 연결되어 있는지 여부를 체크한다.
     *
     * @param meterId
     * @param contractId
     * @return
     */
    @RequestMapping(value="/gadget/system/customerMax", params="param=getCheckContractByMeterId")
    @Deprecated
    public ModelAndView getCheckContractByMeterId(@RequestParam("meterId") Integer meterId,
            @RequestParam(value="contractId", required=false) Integer contractId) {
        ModelAndView mav = new ModelAndView("jsonView");

        //기존 계약을 가지고 온다.
        Map<String, Object> result = contractManager.getCheckContractByMeterId(meterId, contractId);
        mav.addObject("result", result);
        return mav;
    }

    /**
     * method name : getCheckContractByMeterNo<b/>
     * method Desc : Contract 등록/수정 시 선택한 Meter 가 현재 다른 Contract 에 연결되어 있는지 여부를 체크한다.
     *
     * @param meterId
     * @param contractId
     * @return
     */
    @RequestMapping(value="/gadget/system/customerMax", params="param=getCheckContractByMeterNo")
    public ModelAndView getCheckContractByMeterId(@RequestParam("meterNo") String meterNo,
            @RequestParam(value="contractId", required=false) Integer contractId) {
        ModelAndView mav = new ModelAndView("jsonView");

        //기존 계약을 가지고 온다.
        Map<String, Object> result = contractManager.getCheckContractByMeterNo(meterNo, contractId);
        mav.addObject("result", result);
        return mav;
    }

    /**
     * method name : checkCustomerNoLoginMapping<b/>
     * method Desc : 선택한 Customer에 이미 부여된 LoginId가 있는지 체크
     *
     * @param meterId
     * @param contractId
     * @return
     */
    @RequestMapping(value="/gadget/system/customerMax", params="param=checkCustomerNoLoginMapping")
    public ModelAndView checkCustomerNoLoginMapping(@RequestParam("customerNo") String customerNo) {
        ModelAndView mav = new ModelAndView("jsonView");


        Map<String, String> returnData = customerManager.checkCustomerNoLoginMapping(customerNo);

        mav.addObject("loginId", returnData.get("LOGINID"));
        mav.addObject("name", returnData.get("NAME"));
        mav.addObject("email", returnData.get("EMAIL"));
        mav.addObject("telNo", returnData.get("TELNO"));
        return mav;
    }

    /**
     * method name : getCheckContractByMeterId<b/>
     * method Desc : Contract 등록/수정 시 입력한 Contract Number 가 현재 다른 Customer 에 연결되어 있는지 여부를 체크한다.
     *
     * @param meterId
     * @param contractId
     * @return
     */
    @RequestMapping(value="/gadget/system/customerMax", params="param=getCheckContractNumber")
    public ModelAndView getCheckContractNumber(@RequestParam("contractNumber") String contractNumber) {
        ModelAndView mav = new ModelAndView("jsonView");

        //기존 계약관련 정보를 가지고 온다.
        Map<String, Object> result = contractManager.getCheckContractNumber(contractNumber);
        mav.addObject("result", result);
        return mav;
    }

    @RequestMapping(value="/gadget/system/customerMax", params="param=getCustomerRole")
    public ModelAndView getCustomerRole() {
        ModelAndView mav = new ModelAndView("jsonView");

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        int supplierId = user.getRoleData().getSupplier().getId();
        //기존 계약관련 정보를 가지고 온다.
        List<Role> result = roleManager.getRoleBySupplierIdForCustomer(supplierId);
        mav.addObject("result", result);
        return mav;
    }

    /**
     * getTypeFrame
     *
     * @param type
     * @param data
     * @return DATA필드의 Type 프레임 리턴(Type(1), TypeLength(1), Data(가변))
     */
    public String getTypeFrame(String type, String data){

        if(data.length()<1){
            return "";
        }
        String returnStr    = "";
        byte[] dataBytes    = data.getBytes();
        String dataSize     = String.format("%02X", DataUtil.getByteToInt(dataBytes.length));


        returnStr += type;
        returnStr += dataSize;
        returnStr += Hex.decode(dataBytes);

        Date date = new Date();
        log.debug("/////////////////////////////////////////////////////////");
        log.debug("date : " + date);
        log.debug("type : " + type);
        log.debug("dataSize : " + dataSize);
        log.debug("Hex.decode(dataBytes) : " + Hex.decode(dataBytes));

        return returnStr.replaceAll(" ", "");
    }
}
