/**
 * PrepaymentMgmtOperatorController.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.bo.system.prepaymentMgmt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.list.TreeList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncryptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.cms.service.DebtEntManager;
import com.aimir.dao.prepayment.VendorCasherDao;
import com.aimir.dao.system.DepositHistoryDao;
import com.aimir.dao.system.LanguageDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.prepayment.VendorCasher;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.depositMgmt.DepositMgmtManager;
import com.aimir.service.system.prepayment.PrepaymentChargeManager;
import com.aimir.util.ArrearsInfoMakeExcel;
import com.aimir.util.BillingBlockTariffExcelMake;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils;
import com.aimir.util.DepositHistoryDataMakeExcel;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.VendorPrepaymentContractMakeExcel;
import com.aimir.util.ZipUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * PrepaymentChargeController.java Description
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2013. 2. 15.  v1.0        문동규   선불 고객 충전 관리 Controller
 * </pre>
 */
@Controller
public class PrepaymentChargeController {
    Log log = LogFactory.getLog(PrepaymentChargeController.class);

    @Autowired
    PrepaymentChargeManager prepaymentChargeManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    ContractManager contractManager;

    @Autowired
    RoleManager roleManager;

    @Autowired
    DepositMgmtManager depositMgmtManager;

    @Autowired
    OperatorDao operatorDao;

    @Autowired
    VendorCasherDao vendorCasherDao;

    @Autowired
    SupplierDao supplierDao;

    @Autowired
    PrepaymentLogDao prepaymentLogDao;

    @Autowired
    LanguageDao languageDao;

    @Autowired
    DepositHistoryDao depositHistoryDao;

    @Autowired
    LocationDao locationDao;
    
    @Autowired
    SupplierManager supplierManager;
    
    @Autowired
    DebtEntManager debtEntManager;

    private static final String PASSWORD = "";
    /**
     * method name : loadPrepaymentChargeMaxGadget<b/>
     * method Desc : Max Gadget 페이지 로딩
     *
     * @return
     */
    @RequestMapping(value="/gadget/prepaymentMgmt/prepaymentChargeMaxGadget")
    public ModelAndView loadPrepaymentChargeMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/prepaymentChargeMaxGadget");
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();
        mav.addObject("operator", user.getLoginId());
        mav.addObject("supplierId", supplierId);

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);
        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        return mav;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value="/gadget/prepaymentMgmt/vendorPrepaymentChargeMaxGadget")
    public ModelAndView vendorPrepaymentChargeMax() {
        ModelAndView mav = new ModelAndView("gadget/prepaymentMgmt/vendorPrepaymentChargeMax");
        String FILE_PATH = "/tmp/monthly";
        File dir = new File(FILE_PATH);
        File[] files = dir.listFiles();
        List<String> fileNames = new TreeList();

        if ( !dir.exists() ) {
            dir.mkdirs();
        }

        if(files != null) {
            for ( File file: files ) {
                if ( file.isFile() ) {
                    fileNames.add(file.getName());
                }
            }
        }

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();
        Operator operator = operatorDao.getOperatorByLoginId(user.getLoginId());

        // true: Casher가 없는 경우이다.
        Boolean isFirst = false;
        List<VendorCasher> list = vendorCasherDao.getByVendorOperator(operator);

        if ( list == null || list.size() <= 0 ) {
            isFirst = true;
        }

        mav.addObject("vendor", operator.getLoginId());
        mav.addObject("deposit", operator.getDeposit());
        mav.addObject("supplierId", supplierId);
        mav.addObject("supplierName", user.getRoleData().getSupplier().getName());
        mav.addObject("isFirst", isFirst);
        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);
        String roleName = role.getName();
        Boolean isVendor = roleName.equals("vendor")||roleName.equals("edh_vendor");
        mav.addObject("role", roleName);
        mav.addObject("isVendor", isVendor);
        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("roleType", "vendor");
        params.put("supplierId", supplierId);
        mav.addObject("depositVendorList", operatorDao.getOperatorListByRoleType(params)); 
        
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); 
        Map<String, Object> vat = prepaymentChargeManager.getVatByFixedVariable("CHARGE_TAX", null, format.format(now));
        mav.addObject("vatAmount", vat.get("vatAmount"));
        mav.addObject("vatUnit", vat.get("vatUnit"));
        
        try {
        	Properties prop = new Properties();
            prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));            
            mav.addObject("logoImg", prop.getProperty("supplier.logo.filename") == null ? "/images/ECG_logo.gif" : prop.getProperty("supplier.logo.filename").trim());
            String isPartpayment = prop.getProperty("partpayment.use");
            String initArrears = prop.getProperty("prepay.init.arrears");
            mav.addObject("isPartpayment" , (isPartpayment == null || "".equals(isPartpayment)) ? false : isPartpayment);
            mav.addObject("initArrears" , (initArrears == null || "".equals(initArrears)) ? 0 : Integer.parseInt(initArrears));
		} catch (Exception e) {
			log.debug(e,e);	
		}
        
        return mav;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping(value="/gadget/prepaymentMgmt/vendorPrepaymentChargeECGMaxGadget")
    public ModelAndView vendorPrepaymentChargeECGMax() {
        ModelAndView mav = new ModelAndView("gadget/prepaymentMgmt/vendorPrepaymentChargeECGMax");
        String FILE_PATH = "/tmp/monthly";
        File dir = new File(FILE_PATH);
        File[] files = dir.listFiles();
        List<String> fileNames = new TreeList();

        if ( !dir.exists() ) {
            dir.mkdirs();
        }

        if(files != null) {
            for ( File file: files ) {
                if ( file.isFile() ) {
                    fileNames.add(file.getName());
                }
            }
        }

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();
        Operator operator = operatorDao.getOperatorByLoginId(user.getLoginId());

        // true: Casher가 없는 경우이다.
        Boolean isFirst = false;
        List<VendorCasher> list = vendorCasherDao.getByVendorOperator(operator);

        if ( list == null || list.size() <= 0 ) {
            isFirst = true;
        }

        mav.addObject("vendor", operator.getLoginId());
        mav.addObject("deposit", operator.getDeposit());
        mav.addObject("supplierId", supplierId);
        mav.addObject("supplierName", user.getRoleData().getSupplier().getName());
        mav.addObject("isFirst", isFirst);
        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);
        String roleName = role.getName();
        Boolean isVendor = roleName.equals("vendor");
        mav.addObject("role", roleName);
        mav.addObject("isVendor", isVendor);
        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("roleType", "vendor");
        params.put("supplierId", supplierId);
        mav.addObject("depositVendorList", operatorDao.getOperatorListByRoleType(params));        
        
        try {
        	Properties prop = new Properties();
            prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));            
            mav.addObject("logoImg", prop.getProperty("supplier.logo.filename") == null ? "/images/ECG_logo.gif" : prop.getProperty("supplier.logo.filename").trim());
            String isPartpayment = prop.getProperty("partpayment.use");
            String initArrears = prop.getProperty("prepay.init.arrears");
            mav.addObject("isPartpayment" , (isPartpayment == null || "".equals(isPartpayment)) ? false : isPartpayment);
            mav.addObject("initArrears" , (initArrears == null || "".equals(initArrears)) ? 0 : Integer.parseInt(initArrears));
		} catch (Exception e) {
			log.debug(e,e);	
		}
        
        return mav;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/prepaymentMgmt/vendorPrepaymentChargeSPASAMaxGadget")
    public ModelAndView vendorPrepaymentChargeSPASAMax() {
        ModelAndView mav = new ModelAndView("gadget/prepaymentMgmt/vendorPrepaymentChargeSPASAMax");
        String FILE_PATH = "/tmp/monthly";
        File dir = new File(FILE_PATH);
        File[] files = dir.listFiles();
        List<String> fileNames = new TreeList();

        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();
        Operator operator = operatorDao.getOperatorByLoginId(user.getLoginId());

        // true: Casher가 없는 경우이다.
        Boolean isFirst = false;
        List<VendorCasher> list = vendorCasherDao.getByVendorOperator(operator);

        if (list == null || list.size() <= 0) {
            isFirst = true;
        }

        mav.addObject("vendor", operator.getLoginId());
        mav.addObject("deposit", operator.getDeposit());
        mav.addObject("supplierId", supplierId);
        mav.addObject("isFirst", isFirst);
        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);
        String roleName = role.getName();
        Boolean isVendor = roleName.equals("vendor");
        mav.addObject("role", roleName);
        mav.addObject("isVendor", isVendor);
        mav.addObject("editAuth", authMap.get("cud")); // 수정권한(write/command = true)
        
        try {
        	Properties prop = new Properties();
            prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));            
            mav.addObject("logoImg", prop.getProperty("supplier.logo.filename") == null ? "/images/ECG_logo.gif" : prop.getProperty("supplier.logo.filename").trim());
		} catch (Exception e) {
			log.debug(e,e);	
		}
        
        return mav;
    }

    @RequestMapping(value="/gadget/prepaymentMgmt/vendorPrepaymentChargeMiniGadget")
    public ModelAndView vendorPrepaymentChargeMini() {
        ModelAndView mav = new ModelAndView("gadget/prepaymentMgmt/vendorPrepaymentChargeMini");
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();
        Operator operator = operatorDao.getOperatorByLoginId(user.getLoginId());
        mav.addObject("vendor", operator.getLoginId());
        mav.addObject("deposit", operator.getDeposit());
        mav.addObject("supplierId", supplierId);
        Role role = roleManager.getRole(user.getRoleData().getId());
        String roleName = role.getName();
        Boolean isVendor = roleName.equals("vendor");
        mav.addObject("role", roleName);
        mav.addObject("isVendor", isVendor);
        return mav;
    }

    /**
     * method name : loadPrepaymentChargeReceiptFramePopup<b/>
     * method Desc : Prepayment Charge 가젯에서 영수증 팝업창(외부 프레임)을 호출한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/prepaymentChargeReceiptFramePopup")
    public ModelAndView loadPrepaymentChargeReceiptFramePopup() {
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/prepaymentChargeReceiptFramePopup");
        return mav;
    }

    /**
     * method name : loadPrepaymentChargeReceiptPopup<b/>
     * method Desc : 영수증 팝업창에서 영수증 화면을 호출한다.
     *
     * @param supplierId
     * @param contractId
     * @param prepaymentLogId
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/prepaymentChargeReceiptPopup")
    public ModelAndView loadPrepaymentChargeReceiptPopup(
            @RequestParam("supplierId") Integer supplierId,
            @RequestParam("contractId") Integer contractId,
            @RequestParam("prepaymentLogId") Long prepaymentLogId) {
        ModelAndView mav = new ModelAndView();

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("contractId", contractId);
        conditionMap.put("prepaymentLogId", prepaymentLogId);

        Map<String, Object> result = prepaymentChargeManager.getVendorCustomerReceiptData(conditionMap);
        mav.addAllObjects(result);

        Supplier supplier = supplierDao.get(supplierId);
        String monthlyFilePath = ESAPI.httpUtilities().getCurrentRequest().getRealPath("/gadget/prepaymentMgmt/vendorMonthlyReceiptPopupFor"+supplier.getName()+".jsp");
        File monthlyFile = new File(monthlyFilePath);
        
        String withArrearsFilePath = ESAPI.httpUtilities().getCurrentRequest().getRealPath("/gadget/prepaymentMgmt/vendorCustomerWithArrearsReceiptPopupFor"+supplier.getName()+".jsp");
        File withArrearsFile = new File(withArrearsFilePath);
        
        String filePath = ESAPI.httpUtilities().getCurrentRequest().getRealPath("/gadget/prepaymentMgmt/vendorCustomerReceiptPopupFor"+supplier.getName()+".jsp");
        File file = new File(filePath);
        
        if ((Boolean)result.get("isFirst")) {
            // 월 정산 영수증 폼
        	if(monthlyFile.exists())
        		mav.setViewName("/gadget/prepaymentMgmt/vendorMonthlyReceiptPopupFor"+supplier.getName());
        	else
        		mav.setViewName("/gadget/prepaymentMgmt/vendorMonthlyReceiptPopup");
            mav.addObject("hasArrears", new Boolean(result.get("preArrears") != null).toString());
        } else {
            if (result.get("preArrears") != null || result.get("preArrears2") != null) {
                // 잔여 미수금이 있는 경우 영수증 폼
            	if(withArrearsFile.exists())
            		mav.setViewName("/gadget/prepaymentMgmt/vendorCustomerWithArrearsReceiptPopupFor"+supplier.getName());
            	else
            		mav.setViewName("/gadget/prepaymentMgmt/vendorCustomerWithArrearsReceiptPopup");
            } else {
            	if(file.exists())
            		mav.setViewName("/gadget/prepaymentMgmt/vendorCustomerReceiptPopupFor"+supplier.getName());
            	else
            		mav.setViewName("/gadget/prepaymentMgmt/vendorCustomerReceiptPopup");
            }
        }

        return mav;
    }

    @RequestMapping(value = "/gadget/prepaymentMgmt/prepaymentChargeReceiptPopupWithDebt")
    public ModelAndView loadPrepaymentChargeReceiptPopupWithDebt(
            @RequestParam("supplierId") Integer supplierId,
            @RequestParam("contractId") Integer contractId,
            @RequestParam("prepaymentLogId") Long prepaymentLogId) {
        ModelAndView mav = new ModelAndView();

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("contractId", contractId);
        conditionMap.put("prepaymentLogId", prepaymentLogId);

        Map<String, Object> result = debtEntManager.getVendorCustomerReceiptDataWithDebt(conditionMap);
        mav.addAllObjects(result);

        Supplier supplier = supplierDao.get(supplierId);
        String monthlyFilePath = ESAPI.httpUtilities().getCurrentRequest().getRealPath("/gadget/prepaymentMgmt/vendorMonthlyReceiptPopupFor"+supplier.getName()+".jsp");
        File monthlyFile = new File(monthlyFilePath);
        
        String withArrearsFilePath = ESAPI.httpUtilities().getCurrentRequest().getRealPath("/gadget/prepaymentMgmt/vendorCustomerWithArrearsReceiptPopupFor"+supplier.getName()+".jsp");
        File withArrearsFile = new File(withArrearsFilePath);
        
        String filePath = ESAPI.httpUtilities().getCurrentRequest().getRealPath("/gadget/prepaymentMgmt/vendorCustomerReceiptPopupFor"+supplier.getName()+".jsp");
        File file = new File(filePath);
        
        if ((Boolean)result.get("isFirst")) {
            // 월 정산 영수증 폼
        	if(monthlyFile.exists())
        		mav.setViewName("/gadget/prepaymentMgmt/vendorMonthlyReceiptPopupFor"+supplier.getName());
        	else
        		mav.setViewName("/gadget/prepaymentMgmt/vendorMonthlyReceiptPopup");
            mav.addObject("hasArrearsDebts", new Boolean(result.get("preArrears") != null || result.get("preDebts") != null).toString());
        } else {
            if (result.get("preArrears") != null || result.get("preDebts") != null) {
                // 잔여 미수금이 있는 경우 영수증 폼
            	if(withArrearsFile.exists())
            		mav.setViewName("/gadget/prepaymentMgmt/vendorCustomerWithArrearsReceiptPopupFor"+supplier.getName());
            	else
            		mav.setViewName("/gadget/prepaymentMgmt/vendorCustomerWithArrearsReceiptPopup");
            } else {
            	if(file.exists())
            		mav.setViewName("/gadget/prepaymentMgmt/vendorCustomerReceiptPopupFor"+supplier.getName());
            	else
            		mav.setViewName("/gadget/prepaymentMgmt/vendorCustomerReceiptPopup");
            }
        }

        return mav;
    }

    @RequestMapping(value = "/gadget/prepaymentMgmt/vendorChargeHistoryExcelDownloadPopup")
    public ModelAndView downloadDepositChargeExcel() {
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/vendorChargeHistoryExcelDownloadPopup");
        return mav;
    }
    
    @RequestMapping(value = "/gadget/prepaymentMgmt/vendorChargeAmountExcelDownloadPopup")
    public ModelAndView vendorChargeAmountExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/vendorChargeAmountExcelDownloadPopup");
        return mav;
    }

    /**
     * method name : getPrepaymentChargeList<b/>
     * method Desc : Prepayment Charge 가젯에서 Prepayment Charge List 를 조회한다.
     *
     * @param supplierId
     * @param contractNumber
     * @param customerNo
     * @param customerName
     * @param mdsId
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getPrepaymentChargeList")
    public ModelAndView getPrepaymentChargeList(@RequestParam("supplierId") Integer supplierId,
            @RequestParam int page,
            @RequestParam int limit,
            String barcode,
            String contractNumber,
            String phone,
            String customerNo,
            String customerName,
            String mdsId) {
        ModelAndView mav = new ModelAndView("jsonView");
        List<Map<String, Object>> result = null;
        Integer totalCount = null;
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("barcode", barcode);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("phone", phone);
        conditionMap.put("customerNo", customerNo);
        conditionMap.put("customerName", customerName);
        conditionMap.put("mdsId", mdsId);

        try{
        	result = prepaymentChargeManager.getPrepaymentChargeList(conditionMap);
        	totalCount = prepaymentChargeManager.getPrepaymentChargeListTotalCount(conditionMap);
        }catch(Exception e) {
        	log.error(e,e);
        	result = null;
        	totalCount = 0;
        }
        mav.addObject("result", result);
        mav.addObject("totalCount", totalCount);

        return mav;
    }
    
    /**
     * method name : getPrepaymentChargeList<b/>
     * method Desc : Prepayment Charge 가젯에서 Prepayment Charge List 를 조회한다.
     *
     * @param supplierId
     * @param contractNumber
     * @param customerNo
     * @param customerName
     * @param mdsId
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getPrepaymentChargeListWithDebt")
    public ModelAndView getPrepaymentChargeListWithDebt(@RequestParam("supplierId") Integer supplierId,
            @RequestParam int page,
            @RequestParam int limit,
            String barcode,
            String contractNumber,
            String customerNo,
            String customerName,
            String mdsId) {
        ModelAndView mav = new ModelAndView("jsonView");
        List<Map<String, Object>> result = null;
        Integer totalCount = null;
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("barcode", barcode);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("customerNo", customerNo);
        conditionMap.put("customerName", customerName);
        conditionMap.put("mdsId", mdsId);

        try{
        	result = debtEntManager.getPrepaymentChargeList(conditionMap);
        	totalCount = debtEntManager.getPrepaymentChargeListTotalCount(conditionMap);
        } catch(Exception e) {
        	log.error(e,e);
        	result = null;
        	totalCount = 0;
        }
        
        mav.addObject("result", result);
        mav.addObject("totalCount", totalCount);

        return mav;
    }

    @RequestMapping(value="/gadget/prepaymentMgmt/getDebtInfoByCustomerNo")
    public ModelAndView getDebtInfoByCustomerNo(
    		@RequestParam("customerNo") String customerNo,
    		@RequestParam(required=false, value="debtType") String debtType,
    		@RequestParam(required=false, value="debtRef") String debtRef) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	List<Map<String,Object>> debtInfo = debtEntManager.getDebtInfoByCustomerNo(customerNo, debtType, debtRef);
    	mav.addObject("debtInfo",debtInfo);
    	
    	return mav;
    }

    /**
     * method name : getChargeHistoryList
     * method Desc : Prepayment Charge 가젯에서 충전 이력 리스트를 조회한다.
     *
     * @param contractNumber
     * @param serviceType
     * @param searchStartMonth
     * @param searchEndMonth
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getChargeHistoryList")
    public ModelAndView getChargeHistoryList(@RequestParam("contractNumber") String contractNumber,
            @RequestParam("searchStartMonth") String searchStartMonth,
            @RequestParam("searchEndMonth") String searchEndMonth,
            @RequestParam("page") Integer page,
            @RequestParam("limit") Integer limit) {
        ModelAndView mav = new ModelAndView("jsonView");

        if (contractNumber.isEmpty()) {
            mav.addObject("result", new ArrayList<Map<String, Object>>());
            mav.addObject("totalCount", 0);
            return mav;
        }
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("searchStartMonth", searchStartMonth);
        conditionMap.put("searchEndMonth", searchEndMonth);
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        Contract contract = contractManager.getContractByContractNumber(contractNumber);
        Integer supplierId = contract.getSupplier().getId();
        Integer contractId = contract.getId();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("contractId", contractId);

        List<Map<String, Object>> result = prepaymentChargeManager.getChargeHistoryList(conditionMap);
        Long id = prepaymentLogDao.getRecentPrepaymentLogId(contractNumber);
        mav.addObject("result", result);
        mav.addObject("id", id);
        mav.addObject("totalCount", prepaymentChargeManager.getChargeHistoryListTotalCount(conditionMap));

        return mav;
    }

        /**
     * method name : getBalanceHistoryList
     * method Desc : 선불 충전 가젯에서 차감이력을 보기위함.
     *
     * @param contractNumber
     * @param serviceType
     * @param searchStartMonth
     * @param searchEndMonth
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getBalanceHistoryList")
    public ModelAndView getBalanceHistoryList(@RequestParam("contractNumber") String contractNumber,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("supplierId") String supplierId,
            @RequestParam("page") Integer page,
            @RequestParam("limit") Integer limit) {
        ModelAndView mav = new ModelAndView("jsonView");

        if (contractNumber.isEmpty()) {
            mav.addObject("result", new ArrayList<Map<String, Object>>());
            mav.addObject("totalCount", 0);
            return mav;
        }
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("startDate", startDate+"000000");
        conditionMap.put("endDate", endDate+"235959");
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("supplierId", Integer.parseInt(supplierId));
        
        Contract contract = contractManager.getContractByContractNumber(contractNumber);
        Integer contractId = contract.getId();
        conditionMap.put("contractId", contractId);

        List<Map<String, Object>> result = prepaymentChargeManager.getBalanceHistoryList(conditionMap);
        mav.addObject("result", result);
        
        Object totalCount = prepaymentChargeManager.getBalanceHistoryListTotalCount(conditionMap);
        if(totalCount instanceof Integer) {
        	mav.addObject("totalCount", (Integer)totalCount);
        } else if(totalCount instanceof Long) {
        	mav.addObject("totalCount", (Long)totalCount);
        }

        return mav;
    }

    /**
     * method name : savePrepaymentCharge<b/>
     * method Desc : Prepayment Charge 가젯에서 Prepayment Charge Price 를 저장한다.
     *
     * @param data
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/savePrepaymentCharge")
    public ModelAndView savePrepaymentCharge(@RequestParam("contractNumber") String contractNumber,
            @RequestParam("mdsId") String mdsId,
            @RequestParam("lastTokenId") String lastTokenId,
            @RequestParam("contractDemand") Double contractDemand,
            @RequestParam("tariffCode") Integer tariffCode,
            @RequestParam("amount") Double amount,
            @RequestParam("supplierId") Integer supplierId) {
        ModelAndView mav = new ModelAndView("jsonView");
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer operatorId = (int) user.getAccountId();

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("operatorId", operatorId);
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("mdsId", mdsId);
        conditionMap.put("lastTokenId", lastTokenId);
        conditionMap.put("contractDemand", contractDemand);
        conditionMap.put("tariffCode", tariffCode);
        conditionMap.put("amount", amount);

        String result = prepaymentChargeManager.savePrepaymentCharge(conditionMap);
        mav.addObject("result", result);

        return mav;
    }

    /**
     * method name : checkChargeAvailable<b/>
     * method Desc : 선불 충전 시 충전가능여부를 체크한다.
     *
     * @param contractNumber
     * @return
     */
    @RequestMapping(value="/gadget/prepaymentMgmt/checkChargeAvailable")
    public ModelAndView checkChargeAvailable(@RequestParam String contractNumber) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("contractNumber", contractNumber);

        Map<String, Object> result = prepaymentChargeManager.checkChargeAvailable(conditionMap);
        mav.addAllObjects(result);
        return mav;
    }
    
    @RequestMapping(value="/gadget/prepaymentMgmt/getDebtArrearsLog")
    public ModelAndView getDebtLog(
    		@RequestParam(required=false, value="prepaymentLogId") Long prepaymentLogId) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	List<Map<String,Object>> debtLogList = debtEntManager.getDebtArrearsLog(prepaymentLogId);
    	mav.addObject("debtLogList",debtLogList);
    	return mav;
    }
    
    /**
     * @MethodName vendorSavePrepaymentChargeECG
     * @Description 가나 ECG savePrepaymentCharge(vendor<->customer)
     */
    @RequestMapping(value="/gadget/prepaymentMgmt/vendorSavePrepaymentChargeECG")
    public ModelAndView vendorSavePrepaymentChargeECG(@RequestParam String contractNumber,
            @RequestParam String casherId,
            @RequestParam Integer contractId,
            @RequestParam Double paidAmount,
            @RequestParam Integer supplierId,
            @RequestParam String mdsId,
            @RequestParam Double contractDemand,
            @RequestParam Boolean isPartpayment,
            @RequestParam Boolean partpayReset,
            @RequestParam Integer payTypeId,
            @RequestParam String customerNo,
            @RequestParam Double chargedCredit,
            @RequestParam(value="checkNo", required=false) String checkNo,
            @RequestParam(value="bankCode", required=false) Integer bankCode,
            @RequestParam(value="chargedDebtArr") String chargedDebtArr,
            Double contractPrice) {
        ModelAndView mav = new ModelAndView("jsonView");
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer operatorId = (int) user.getAccountId();
        Operator vendor = operatorDao.get(operatorId);
        Boolean isVendor = vendor.getRole().getName().equals("vendor");
        
        JSONArray jsonArr = null;
        if(chargedDebtArr == null || chargedDebtArr.isEmpty()) {
        	jsonArr = new JSONArray();
        } else {
        	jsonArr = JSONArray.fromObject(chargedDebtArr);
        }
        
        List<Map<String,Object>> debtList = new ArrayList<Map<String,Object>>();
        int jsonArrSize = jsonArr.size();
        for (int i = 0; i < jsonArrSize; i++) {
        	Map<String, Object> map = new HashMap<String,Object>();
			JSONArray subJsonArr = jsonArr.getJSONArray(i);
			for (int j = 0; j < subJsonArr.size(); j++) {
				JSONObject jsonObj = subJsonArr.getJSONObject(j);
				Iterator it = jsonObj.keys();
				while(it.hasNext()) {
					log.info("start");
					String key = it.next().toString(); 
					map.put(key, jsonObj.get(key));
				}
				debtList.add(map);
			}
		}
        
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("isVendor", isVendor);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("casherId", casherId);
        conditionMap.put("operatorId", operatorId);
        conditionMap.put("contractId", contractId);
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("mdsId", mdsId);
        conditionMap.put("contractDemand", contractDemand);
        conditionMap.put("paidAmount", paidAmount);
        conditionMap.put("contractPrice", contractPrice);
        conditionMap.put("isPartpayment", isPartpayment);
        conditionMap.put("partpayReset", partpayReset);
        conditionMap.put("payTypeId", payTypeId);
        conditionMap.put("customerNo",customerNo);
        conditionMap.put("chargedCredit",chargedCredit);
        conditionMap.put("debtList",debtList);
        conditionMap.put("bankCode",bankCode);
        conditionMap.put("checkNo",checkNo);

        Map<String, Object> result = debtEntManager.vendorSavePrepaymentChargeECG(conditionMap);
        String resultStr = StringUtil.nullToBlank(result.get("result"));
        try{
        	//SMS 전송
        	if("success".equals(resultStr)) {
	        	Map<String, Object> smsInfo = (Map<String, Object>) result.get("smsInfo");
	        	Contract contract = (Contract) smsInfo.get("contract");
	        	Double amount = (Double) smsInfo.get("chargedCredit");
	        	Double preCurrentCredit = (Double) smsInfo.get("preCredit");
	        	Boolean isValid = (Boolean) smsInfo.get("isCutOff");
	        	prepaymentChargeManager.SMSNotificationForECG(contract, amount, preCurrentCredit, isValid);
        	}
        } catch(Exception e) {
        	log.error(e,e);
        }
        mav.addObject("deposit", result.get("deposit"));
        mav.addObject("prepaymentLogId", result.get("prepaymentLogId"));
        mav.addObject("result", result.get("result"));
        mav.addObject("credit", result.get("credit"));
        mav.addObject("isCutOff", result.get("isCutOff"));
        return mav;
    }

    /**
     * @MethodName vendorSavePrepaymentCharge
     * @Date 2013. 8. 5.
     * @param contractNumber 계약번호
     * @param casherId 종업원 접속 id
     * @param contractId 계약 id
     * @param amount 선불 납부 비용
     * @param arrears 미납금 납부 비용
     * @param supplierId 공급사 id
     * @param mdsId 미터 id
     * @param lastTokenId
     * @param contractDemand
     * @param tariffCode
     * @return
     * @Modified 2013. 10. 25
     */
    @RequestMapping(value="/gadget/prepaymentMgmt/vendorSavePrepaymentCharge")
    public ModelAndView vendorSavePrepaymentCharge(@RequestParam String contractNumber,
            @RequestParam String casherId,
            @RequestParam Integer contractId,
            @RequestParam Double amount,
            @RequestParam Double totalAmountPaid,
            @RequestParam Double arrears,
            @RequestParam Double arrears2,
            @RequestParam Double vat,
            @RequestParam Integer supplierId,
            @RequestParam String mdsId,
            @RequestParam String lastTokenId,
            @RequestParam Double contractDemand,
            @RequestParam Integer tariffCode,
            @RequestParam Boolean isPartpayment,
            @RequestParam Boolean partpayReset,
            @RequestParam Integer payTypeId,
            Double contractPrice) {
        ModelAndView mav = new ModelAndView("jsonView");
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer operatorId = (int) user.getAccountId();
        Operator vendor = operatorDao.get(operatorId);
        Boolean isVendor = vendor.getRole().getName().equals("vendor");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("isVendor", isVendor);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("casherId", casherId);
        conditionMap.put("operatorId", operatorId);
        conditionMap.put("contractId", contractId);
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("mdsId", mdsId);
        conditionMap.put("lastTokenId", lastTokenId);
        conditionMap.put("contractDemand", contractDemand);
        conditionMap.put("tariffCode", tariffCode);
        conditionMap.put("amount", amount);
        conditionMap.put("arrears", arrears);
        conditionMap.put("arrears2", arrears2);
        conditionMap.put("totalAmountPaid", totalAmountPaid);
        conditionMap.put("vat", vat);
        conditionMap.put("contractPrice", contractPrice);
        conditionMap.put("isPartpayment", isPartpayment);
        conditionMap.put("partpayReset", partpayReset);
        conditionMap.put("payTypeId", payTypeId);

        Map<String, Object> result = prepaymentChargeManager.vendorSavePrepaymentCharge(conditionMap);
        mav.addObject("deposit", result.get("deposit"));
        mav.addObject("prepaymentLogId", result.get("prepaymentLogId"));
        mav.addObject("result", result.get("result"));
        mav.addObject("credit", result.get("credit"));
        mav.addObject("isCutOff", result.get("isCutOff"));
        return mav;
    }

    /**
     * @MethodName vendorSavePrepaymentChargeSPASA
     * @Date 2014. 8. 26.
     * @param contractNumber 계약번호
     * @param casherId 종업원 접속 id
     * @param contractId 계약 id
     * @param amount 선불 납부 비용
     * @param arrears 미납금 납부 비용
     * @param supplierId 공급사 id
     * @param mdsId 미터 id
     * @param lastTokenId
     * @param contractDemand
     * @param tariffCode
     * @param authCode
     * @return
     */
    @RequestMapping(value="/gadget/prepaymentMgmt/vendorSavePrepaymentChargeSPASA")
    public ModelAndView vendorSavePrepaymentChargeSPASA(@RequestParam String contractNumber,
            @RequestParam String casherId,
            @RequestParam Integer contractId,
            @RequestParam Double amount,
            @RequestParam Double arrears,
            @RequestParam Integer supplierId,
            @RequestParam String mdsId,
            @RequestParam String lastTokenId,
            @RequestParam Double contractDemand,
            @RequestParam Integer tariffCode,
            @RequestParam String authCode,
            Double contractPrice) {
        ModelAndView mav = new ModelAndView("jsonView");
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer operatorId = (int) user.getAccountId();
        Operator vendor = operatorDao.get(operatorId);
        Boolean isVendor = vendor.getRole().getName().equals("vendor");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("isVendor", isVendor);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("casherId", casherId);
        conditionMap.put("operatorId", operatorId);
        conditionMap.put("contractId", contractId);
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("mdsId", mdsId);
        conditionMap.put("lastTokenId", lastTokenId);
        conditionMap.put("contractDemand", contractDemand);
        conditionMap.put("tariffCode", tariffCode);
        conditionMap.put("amount", amount);
        conditionMap.put("arrears", arrears);
        conditionMap.put("contractPrice", contractPrice);
        conditionMap.put("authCode", authCode);

        Map<String, Object> result = prepaymentChargeManager.vendorSavePrepaymentChargeSPASA(conditionMap);
        mav.addObject("deposit", result.get("deposit"));
        mav.addObject("prepaymentLogId", result.get("prepaymentLogId"));
        mav.addObject("result", result.get("result"));
        mav.addObject("credit", result.get("credit"));
        mav.addObject("isCutOff", result.get("isCutOff"));
        return mav;
    }

    /**
     * @MethodName vendorSetContractPrice
     * @Date 2013. 10. 11.
     * @param contractNumber
     * @param contractPrice
     * @return
     * @Modified
     * @Description 가나 선불 시스템에서 미터 교체시 미터 교체에 대한 기본 지불 비용 설정
     */
    @RequestMapping("/gadget/prepaymentMgmt/vendorSetContractPrice")
    public ModelAndView vendorSetContractPrice(@RequestParam String contractNumber,
            @RequestParam Double contractPrice) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("contractNumber", contractNumber);
        condition.put("contractPrice", contractPrice);

        String result = prepaymentChargeManager.vendorSetContractPrice(condition);
        mav.addObject("result", result);
        return mav;
    }

    /**
     * @MethodName updateBarcode
     * @Date 2013. 10. 11.
     * @param customerId
     * @param barcode
     * @return
     * @Modified
     * @Description 가나 선불 시스템에서 고객에게 부여할 선불 카드 바코드 update
     */
    @RequestMapping("/gadget/prepaymentMgmt/updateBarcode")
    public ModelAndView updateBarcode(@RequestParam Integer contractId,
            String barcode) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map <String, Object> condition = new HashMap<String, Object>();
        condition.put("contractId", contractId);
        condition.put("barcode", barcode);

        String result = prepaymentChargeManager.updateBarcode(condition);
        mav.addObject("result", result);
        return mav;
    }

    /**
     * @MethodName casherLogin
     * @Date 2013. 10. 15.
     * @param casherId
     * @param pw
     * @param mac
     * @return
     * @Modified
     * @Description vending station의 casher의 로그인 기능
     */
    @Transactional
    @RequestMapping("/gadget/prepaymentMgmt/casherLogin")
    public ModelAndView casherLogin(@RequestParam String casherId,
            @RequestParam String vendorId,
            @RequestParam String pw) {
        ModelAndView mav = new ModelAndView("jsonView");
        Properties messageProp = new Properties();
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        Operator vendor = operatorDao.getOperatorByLoginId(vendorId);
        String result = null;
        
        Supplier supplier = supplierManager.getSupplier(vendor.getSupplierId());
        InputStream ip = getClass().getClassLoader().getResourceAsStream("message_"+ supplier.getLang().getCode_2letter() +".properties");
        if(ip == null){
        	ip = getClass().getClassLoader().getResourceAsStream("message_en.properties");	        	
        }
        
        // casher가 처음 접속인지 확인하는 flag null값인 경우에도 참으로 한다.
        Boolean isFirst = true;
        try {
        	messageProp.load(ip);
        	
        	String success = messageProp.getProperty("aimir.success");
            String fail = messageProp.getProperty("aimir.failed");
            result = fail+":";
            
            String hashedPw = instance.hashPassword(pw, casherId);

            // id, pw 인증
            if (vendorCasherDao.isVaildVendorCasher(vendor, casherId, hashedPw)) {
                VendorCasher casher = vendorCasherDao.getByVendorCasherId(casherId, vendor);

                mav.addObject("isManager", casher.getIsManager());
                mav.addObject("isFirstLogIn", casher.getIsFirst());

                if ( casher.getIsFirst() !=  null ) {
                    isFirst = casher.getIsFirst();
                }

                // manager는 macaddress를 확인하지 않는다
                // casher가 처음 접속하는 경우 macaddress를 확인하지 않는다.
                //if (casher.getIsManager() || isFirst) {
                    result = success;

                //  macaddress 인증
                /*} else {
                	List<String> macList = vendorCasherDao.getMacAddressLIst(vendor);
                    if ( macList.contains(mac) ) {
                        result = success;
                    }

                    // macaddress 인증 실패
                    if( !result.equals(success) ) {
                        result += messageProp.getProperty("aimir.use.authPC");
                    }
                }*/

            // id, pw 인증 실패
            } else {
                result += messageProp.getProperty("aimir.invalid.namePass");
            }

        } catch (EncryptionException e) {
            e.printStackTrace();
        } catch (IOException e2) {
        	log.error(e2,e2);
        } finally {
            mav.addObject("result", result);
        }
        return mav;
    }

    /**
     * @MethodName getCasherList
     * @Date 2013. 10. 17.
     * @param vendorId
     * @param casherId
     * @param name
     * @return
     * @Modified
     * @Description 특정 vendor에 검색조건에 의한 casher 목록을 조회 (grid)
     */
    @RequestMapping("/gadget/prepaymentMgmt/casherManagerList")
    public ModelAndView getCasherList(@RequestParam String vendorId,
            @RequestParam Integer supplierId,
            String casherId,
            String name,
            Integer page,
            Integer limit) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> condition = new HashMap<String, Object>();

        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang = supplier.getLang().getCode_2letter();

        Operator vendor = operatorDao.getOperatorByLoginId(vendorId);
        condition.put("allManager", false);
        condition.put("vendor", vendor);
        condition.put("casherId", casherId);
        condition.put("name", name);
        condition.put("page", page);
        condition.put("limit", limit);
        condition.put("country", country);
        condition.put("lang", lang);

        mav.addAllObjects(vendorCasherDao.getCasherList(condition));
        return mav;
    }
    
    /**
     * @MethodName getCasherManagerList
     * @Date 2013. 10. 17.
     * @param vendorId
     * @param casherId
     * @param name
     * @return
     * @Modified
     * @Description 특정 vendor에 검색조건에 의한 casher 목록을 조회 (grid)
     */
    @RequestMapping("/gadget/prepaymentMgmt/managerList")
    public ModelAndView getManagerList(@RequestParam(value="vendorId", required=false) String vendorId,
            @RequestParam Integer supplierId,
            Boolean allManager,
            String casherId,
            String name,
            Integer page,
            Integer limit) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> condition = new HashMap<String, Object>();

        Supplier supplier = supplierDao.get(supplierId);
        String country = supplier.getCountry().getCode_2letter();
        String lang = supplier.getLang().getCode_2letter();

        Operator vendor = operatorDao.getOperatorByLoginId(vendorId);
        condition.put("allManager", allManager == null ? false : allManager);
        condition.put("vendor", vendor);
        condition.put("casherId", casherId);
        condition.put("name", name);
        condition.put("page", page);
        condition.put("limit", limit);
        condition.put("country", country);
        condition.put("lang", lang);

        mav.addAllObjects(vendorCasherDao.getCasherList(condition));
        return mav;
    }

    /**
     * @MethodName addCasher
     * @Date 2013. 10. 17.
     * @param casherId
     * @param name
     * @return
     * @Modified
     * @Description vendor에 대한 casher를 추가한다
     */
    @RequestMapping("/gadget/prepaymentMgmt/addCasher")
    public ModelAndView addCasher(@RequestParam String casherId,
            @RequestParam String name,
            @RequestParam String vendor,
            @RequestParam Boolean isManager,
            @RequestParam String lastUpdateDate) {
        ModelAndView mav = new ModelAndView("jsonView");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        Map <String, Object> condition = new HashMap <String, Object>();
        String result = "failed:";

        try {
            String password = instance.hashPassword(PASSWORD, casherId);

            condition.put("id", casherId);
            condition.put("name", name);
            condition.put("password", password);
            condition.put("vendor", vendor);
            condition.put("isManager", isManager);
            condition.put("lastUpdateDate", lastUpdateDate);

            result = prepaymentChargeManager.addCasher(condition);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mav.addObject("result", result);
        }

        return mav;
    }

    /**
     * @MethodName deleteCasher
     * @Date 2013. 10. 23.
     * @param id: vendorCasher 테이블 id
     * @param date: yyyyMMddhhmmss
     * @return
     * @Modified
     * @Description casher를 삭제한다
     */
    @RequestMapping("/gadget/prepaymentMgmt/deleteCasher")
    public ModelAndView deleteCasher(@RequestParam Integer id,
            @RequestParam String date) {
        ModelAndView mav = new ModelAndView("jsonView");
        String result = "failed:";
        Map<String, Object> condition = new HashMap<String, Object>();

        try {
            condition.put("id", id);
            condition.put("date", date);
            result = prepaymentChargeManager.deleteCasher(condition);
        } catch (Exception e) {
            e.printStackTrace();
            result += "delete casher is failed";
        } finally {
            mav.addObject("result", result);
        }

        return mav;
    }

    /**
     * @MethodName checkDuplicateId
     * @Date 2013. 10. 23.
     * @param casherId
     * @return
     * @Modified
     * @Description casher를 등록하기 이전에 중복을 확인한다.
     */
    @RequestMapping("/gadget/prepaymentMgmt/isDuplicate")
    public ModelAndView checkDuplicateId(@RequestParam String casherId,
            @RequestParam String vendor) {
        ModelAndView mav = new ModelAndView("jsonView");
        Operator ven = operatorDao.getOperatorByLoginId(vendor);
        VendorCasher idCasher = vendorCasherDao.getByVendorCasherId(casherId, ven);
        Boolean isDuplicateId = (idCasher != null);
        if ( isDuplicateId ) {
            mav.addObject("error", "There is a duplicate Id.");
        }
        mav.addObject("result", isDuplicateId);
        return mav;
    }



    /**
     * @MethodName changePwd
     * @Date 2013. 10. 23.
     * @param casherId
     * @param password
     * @return
     * @Modified
     * @Description casher의 password를 변경한다.
     */
    @RequestMapping("/gadget/prepaymentMgmt/changePwd")
    public ModelAndView changePwd(@RequestParam String casherId,
            @RequestParam String password,
            @RequestParam String vendor,
            String macList) {
        ModelAndView mav = new ModelAndView("jsonView");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        String result = "failed:";
        Operator vendorObj = operatorDao.getOperatorByLoginId(vendor);

        try {
            password  = instance.hashPassword(password, casherId);
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("casherId", casherId);
            condition.put("password", password);
            condition.put("vendor"  , vendorObj);
            result = vendorCasherDao.changePassword(condition);
            //Mac Address를 제거하는 대신 무조건 비밀번호를 변경하기 시작하면 처음 등록하지 않는 것으로 간주. isFirst값을 false로 바꾼다.
            // 처음 등록하는 경우
            /*
            if ( macList != null ) {
                condition.put("mac", macList);
                result = vendorCasherDao.updateMacAddress(condition);
            }
            */
        } catch (EncryptionException e) {
            result += "password encryption has been failed";
            e.printStackTrace();
        } finally {
            mav.addObject("result", result);
        }

        return mav;
    }

    /**
     * @MethodName cancel
     * @Date 2014. 2. 6.
     * @param id: preapymentLogId
     * @param vendor: Operator.loginId
     * @return
     * @Modified
     * @Description
     */
    @RequestMapping("/gadget/prepaymentMgmt/cancel")
    public ModelAndView cancel(@RequestParam Long id,
            @RequestParam String vendor,
            @RequestParam(value="reason", required=false) String reason) {
        ModelAndView mav = new ModelAndView("jsonView");
        reason = StringUtil.nullToBlank(reason);
        String result = prepaymentChargeManager.cancelTransaction(id, vendor, reason);
        mav.addObject("result", result);
        return mav;
    }
    
    /**
     * @MethodName cancel For Debt
     * @param id: preapymentLogId
     * @param vendor: Operator.loginId
     * @return
     * @Modified
     * @Description
     */
    @RequestMapping("/gadget/prepaymentMgmt/cancelWithDebt")
    public ModelAndView cancelWithDebt(@RequestParam Long id,
            @RequestParam String vendor,
            @RequestParam(value="reason", required=false) String reason) {
        ModelAndView mav = new ModelAndView("jsonView");
        reason = StringUtil.nullToBlank(reason);
        Map<String,Object> result = debtEntManager.cancelTransaction(id, vendor, reason);
        
        String resultStr = StringUtil.nullToBlank(result.get("result"));
        if("success".equals(resultStr)) {
        	Map<String,Object> smsInfo = (Map<String, Object>) result.get("smsInfo");
            prepaymentChargeManager.SMSNotificationWithText((Contract)smsInfo.get("contract"),(String)smsInfo.get("text"));
        }
        
        mav.addObject("result", resultStr);
        return mav;
    }

    /**
     * method name : vendorChargeHistoryList<b/>
     * method Desc :
     *
     * @param page
     * @param limit
     * @param supplierId
     * @param reportType
     * @param subType
     * @param casherId
     * @param vendor
     * @param contract
     * @param customerName
     * @param customerNo
     * @param meterId
     * @param startDate
     * @param endDate
     * @param locationId
     * @return
     */
    @RequestMapping("/gadget/prepaymentMgmt/vendorChargeHistoryList")
    public ModelAndView vendorChargeHistoryList(@RequestParam int page,
            @RequestParam int limit,
            int supplierId,
            String reportType,
            @RequestParam(value="subType", required=false) String subType,
            String casherId,
            String vendor,
            String contract,
            String customerName,
            String customerNo,
            String meterId,
            String gs1,
            String startDate,
            String endDate,
            Integer locationId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> params = new HashMap<String, Object>();

        if (!StringUtil.nullToBlank(vendor).isEmpty()) {
            params.put("vendor", vendor);
        }
        params.put("supplierId", supplierId);
        params.put("page", page);
        params.put("limit", limit);
        params.put("casherId", casherId);
        params.put("contract", contract);
        params.put("customerName", customerName);
        params.put("customerNo", customerNo);
        params.put("meterId", meterId);
        params.put("gs1", gs1);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("locationId", locationId);
        params.put("vendorOnly", false);
        params.put("reportType", reportType);
        params.put("subType", subType);
        params.put("loginIntId", null);
        params.put("onlyLoginData", false);

        Map<String, Object> result = prepaymentChargeManager.getDepositHistoryList(params);
        mav.addObject("count", result.get("count"));
        mav.addObject("list", result.get("list"));
        return mav;
    }

    /**
     * method name : vendorChargeContractListExcelMake
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/billingBlockTariffExcelMake")
    public ModelAndView billingBlockTariffExcelMake(
    		@RequestParam("condition[]") String[] condition,
			@RequestParam("fmtMessage[]") String[] fmtMessage,
			@RequestParam("filePath") String filePath) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
        Map<String, String> msgMap = new HashMap<String, String>();
        
		List<String> fileNameList = new ArrayList<String>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		StringBuilder sbSplFileName = new StringBuilder();
		
		Long total = 0L; // 데이터 조회건수
		Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수
		try{
			String contractNumber = StringUtil.nullToBlank(condition[1]);
			Contract contract = contractManager.getContractByContractNumber(contractNumber);
			Supplier supplier = supplierManager.getSupplier(Integer.parseInt(condition[4].toString()));
	    	
	        conditionMap.put("contractId", contract.getId());
	        conditionMap.put("startDate", StringUtil.nullToBlank(condition[2]));
	        conditionMap.put("endDate", StringUtil.nullToBlank(condition[3]));
	        conditionMap.put("supplierId", Integer.parseInt(condition[4].toString()));
	
	        List<Map<String, Object>> result = prepaymentChargeManager.getBalanceHistoryList(conditionMap);
			total = new Integer(result.size()).longValue();
			mav.addObject("total", total);
			if (total <= 0) {
				return mav; 
			}
			
	        StringBuilder sbFileName = new StringBuilder(fmtMessage[0]);
	        sbFileName.append("(").append(contractNumber).append(")").append("_");
	        sbFileName.append(TimeUtil.getCurrentTimeMilli());
			
	        msgMap.put("title", fmtMessage[0]);
	        msgMap.put("aimir.contractNo", fmtMessage[1]);
	        msgMap.put("contractNumber", contractNumber);
	        msgMap.put("startDate", StringUtil.nullToBlank(condition[2]));
	        msgMap.put("endDate", StringUtil.nullToBlank(condition[3]));
	        
			/**
			 * 파일 생성
			 */
	        BillingBlockTariffExcelMake wExcel = new BillingBlockTariffExcelMake();
			int cnt = 1;
			int idx = 0;
			int fnum = 0;
			int splCnt = 0;
	
			if (total <= maxRows) {
				sbSplFileName = new StringBuilder();
				sbSplFileName.append(sbFileName);
				sbSplFileName.append(".xls");
				wExcel.writeReportExcel(result, msgMap, filePath, sbSplFileName.toString(), supplier);
				fileNameList.add(sbSplFileName.toString());
			} else {
				for (int i = 0; i < total; i++) {
					if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
						sbSplFileName = new StringBuilder();
						sbSplFileName.append(sbFileName);
						sbSplFileName.append('(').append(++fnum).append(").xls");
						list = result.subList(idx, (i + 1));
						wExcel.writeReportExcel(list, msgMap, filePath,	sbSplFileName.toString(), supplier);
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
		}catch(Exception e) {
			log.error(e,e);
		}
    	return mav;
    }
    
    
    /**
     * method name : vendorChargeContractListExcelMake
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/vendorChargeContractListExcelMake")
    public ModelAndView vendorChargeContractListExcelMake(
    		@RequestParam("condition[]") String[] condition,
			@RequestParam("fmtMessage[]") String[] fmtMessage,
			@RequestParam("filePath") String filePath) {
    	
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        Map<String, String> msgMap = new HashMap<String, String>();


		List<String> fileNameList = new ArrayList<String>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		StringBuilder sbFileName = new StringBuilder();
		StringBuilder sbSplFileName = new StringBuilder();

		boolean isLast = false;
		Long total = 0L; // 데이터 조회건수
		Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수
		
		try {
			Integer supplierId = Integer.parseInt(condition[5]);
			
	        conditionMap.put("barcode", StringUtil.nullToBlank(condition[0]));	        
	        conditionMap.put("contractNumber", StringUtil.nullToBlank(condition[1]));
	        conditionMap.put("customerNo", StringUtil.nullToBlank(condition[2]));
	        conditionMap.put("customerName", StringUtil.nullToBlank(condition[3]));
	        conditionMap.put("mdsId", StringUtil.nullToBlank(condition[4]));
	        conditionMap.put("supplierId", supplierId);
	        conditionMap.put("phone", StringUtil.nullToBlank(condition[6]));
	        conditionMap.put("page", 1);
	        conditionMap.put("limit", 10000000);

	        List<Map<String, Object>> result = prepaymentChargeManager.getPrepaymentChargeList(conditionMap);
			total = new Integer(result.size()).longValue();
			mav.addObject("total", total);
			if (total <= 0) {
				return mav; 
			}
			
	        /**
	         *  Excel Title 생성
	         */
	        msgMap.put("contractNumber", fmtMessage[0]);
	        msgMap.put("customerNo", fmtMessage[1]);
	        msgMap.put("customerName", fmtMessage[2]);
	        msgMap.put("mdsId", fmtMessage[3]);
	        msgMap.put("address", fmtMessage[4]);
	        msgMap.put("statusName", fmtMessage[5]);
	        msgMap.put("lastTokenDate", fmtMessage[6]);
	        msgMap.put("currentCredit", fmtMessage[7]);
	        msgMap.put("currentArrears", fmtMessage[8]);
	        msgMap.put("barcode", fmtMessage[9]);
	        msgMap.put("title", fmtMessage[10]);
	        msgMap.put("currentArrears2", fmtMessage[11]);
	        msgMap.put("phone", fmtMessage[12]);
	        
	        
			Supplier supplier = supplierManager.getSupplier(supplierId);
			sbFileName.append(fmtMessage[10]+"_");
			sbFileName.append(TimeUtil.getCurrentTimeMilli());

			/**
			 * 파일 삭제
			 */
			File downDir = new File(filePath);
			if (downDir.exists()) {
				File[] files = downDir.listFiles();

				if (files != null) {
					String filename = null;
					String deleteDate;

					deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(),	Calendar.DAY_OF_MONTH, -10); // 10일 이전 일자
					boolean isDel = false;

					for (File file : files) {
						filename = file.getName();
						isDel = false;

						// 파일길이 : 30이상, 확장자 : xls|zip
						if (filename.length() > 30 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
							// 10일 지난 파일들 삭제
							if (filename.startsWith(fmtMessage[9]+"_") && filename.substring(17, 25).compareTo(deleteDate) < 0) {
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

			/**
			 * 파일 생성
			 */
			VendorPrepaymentContractMakeExcel wExcel = new VendorPrepaymentContractMakeExcel();
			int cnt = 1;
			int idx = 0;
			int fnum = 0;
			int splCnt = 0;

			if (total <= maxRows) {
				sbSplFileName = new StringBuilder();
				sbSplFileName.append(sbFileName);
				sbSplFileName.append(".xls");
				wExcel.writeReportExcel(result, msgMap, isLast, filePath, sbSplFileName.toString(), supplier);
				fileNameList.add(sbSplFileName.toString());
			} else {
				for (int i = 0; i < total; i++) {
					if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
						sbSplFileName = new StringBuilder();
						sbSplFileName.append(sbFileName);
						sbSplFileName.append('(').append(++fnum).append(").xls");
						list = result.subList(idx, (i + 1));
						wExcel.writeReportExcel(list, msgMap, isLast, filePath,	sbSplFileName.toString(), supplier);
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
		} catch (ParseException pe) {
			log.debug(pe,pe);
		} catch (Exception e) {
			log.debug(e,e);
		}

		return mav;
    }    

    /**
     * method name : vendorChargeHistoryExcelMake<b/>
     * method Desc :
     *
     * @param supplierId
     * @param vendor
     * @param vendorRole
     * @param reportType
     * @param subType
     * @param contract
     * @param customerName
     * @param customerNo
     * @param meterId
     * @param startDate
     * @param endDate
     * @param casherId
     * @param locationId
     * @param filePath
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/prepaymentMgmt/vendorChargeArrearsInfoExcelMake")
    public ModelAndView vendorChargeAmountInfoExcelMake(@RequestParam Integer supplierId,
            String startDate,
            String endDate,
            @RequestParam String filePath,
            String logoImg) {
        ModelAndView mav = new ModelAndView("jsonView");
        String prefix = "arrearsInfo";
        StringBuilder fileName = new StringBuilder(prefix);
        StringBuilder splFileName = new StringBuilder();
        Supplier supplier = supplierDao.get(supplierId);
        fileName.append(TimeUtil.getCurrentTimeMilli());
        Long total = 0L; // data count init
        Long maxRows = 5000L; // 엑셀 하나에 보여줄수 있는 최대 데이터 row 수

        List<String> fileNameList = new ArrayList<String>();
        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("supplierId", supplierId);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);

        File downDir = new File(filePath);

        if (downDir.exists()) {
            File[] files = downDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        } else {
            downDir.mkdir();
        }

        Map<String, Object> result = new HashMap<String, Object>();
        
        Map<String, Object> arrearsInfo = depositHistoryDao.getArrearsInfo(condition);
        List<Map<String, Object>> remainArrearsList = (List<Map<String, Object>>) arrearsInfo.get("list");
        
        Double arrearsSumData = (Double) arrearsInfo.get("sum");
        Map<String,Object> sumData = new HashMap<String,Object>();
        
        sumData.put("arrearsSumData", arrearsSumData);
        
        result.put("sumData", sumData);
        
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("withDebt", false);

        total = new Long(remainArrearsList.size());

        ArrearsInfoMakeExcel excel = new ArrearsInfoMakeExcel();
        int cnt = 1;
        int idx = 0;
        int fnum = 0;
        int splCnt = 0;

        if (total <= maxRows) {
        	Map<String,Object> dataList = new HashMap<String,Object>();
        	dataList.put("arrearsList", remainArrearsList);
            result.put("dataList", dataList);
            
            splFileName = new StringBuilder(fileName);
            splFileName.append(".xls");
            excel.writeReportExcel(result, supplier, filePath, splFileName.toString(), logoImg);
            fileNameList.add(splFileName.toString());
        } else {
            for (int i = 0; i < total; i++) {
                if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
                    splFileName = new StringBuilder(fileName);
                    splFileName.append('(').append(++fnum).append(").xls");
                    
                    Map<String,Object> dataList = new HashMap<String,Object>();
                	dataList.put("arrearsList", remainArrearsList.subList(idx, (i + 1)));
                    result.put("dataList", dataList);
                    
                    excel.writeReportExcel(result, supplier, filePath, splFileName.toString(), logoImg);
                    fileNameList.add(splFileName.toString());
                    splCnt = cnt;
                    cnt = 0;
                    idx = (i + 1);
                }
                cnt++;
            }
        }

        StringBuilder zipFile = new StringBuilder(fileName);
        zipFile.append(".zip");
        ZipUtils zutils = new ZipUtils();
        try {
            zutils.zipEntry(fileNameList, zipFile.toString(), filePath);
            mav.addObject("zipFileName", zipFile.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }

        mav.addObject("filePath", filePath);
        mav.addObject("fileName", fileNameList.get(0));
        mav.addObject("fileNames", fileNameList);
        return mav;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/prepaymentMgmt/vendorChargeArrearsInfoWithDebtExcelMake")
    public ModelAndView vendorChargeArrearsInfoWithDebtExcelMake(@RequestParam Integer supplierId,
            String startDate,
            String endDate,
            @RequestParam String filePath,
            String logoImg) {
        ModelAndView mav = new ModelAndView("jsonView");
        String prefix = "arrearsInfo";
        StringBuilder fileName = new StringBuilder(prefix);
        StringBuilder splFileName = new StringBuilder();
        Supplier supplier = supplierDao.get(supplierId);
        fileName.append(TimeUtil.getCurrentTimeMilli());
        Long total = 0L; // data count init
        Long maxRows = 5000L; // 엑셀 하나에 보여줄수 있는 최대 데이터 row 수

        List<String> fileNameList = new ArrayList<String>();
        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("supplierId", supplierId);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        condition.put("withDebt", true);
        File downDir = new File(filePath);

        if (downDir.exists()) {
            File[] files = downDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        } else {
            downDir.mkdir();
        }

        Map<String, Object> result = new HashMap<String, Object>();
        
        Map<String, Object> arrearsInfo = depositHistoryDao.getArrearsInfo(condition);
        List<Map<String, Object>> remainArrearsList = (List<Map<String, Object>>) arrearsInfo.get("list");
        Double arrearsSumData = arrearsInfo.get("sum") == null ? 0d : Double.parseDouble(arrearsInfo.get("sum").toString());
        Map<String,Object> sumData = new HashMap<String,Object>();
        
        sumData.put("arrearsSumData", arrearsSumData);
        
        Map<String, Object> debtsInfo = depositHistoryDao.getDebtInfo(condition);
        List<Map<String, Object>> remainDebtsList = (List<Map<String, Object>>) debtsInfo.get("list");
        Double debtsSumData = debtsInfo.get("sum") == null ? 0d : Double.parseDouble(debtsInfo.get("sum").toString());
        sumData.put("debtsSumData", debtsSumData);
        result.put("sumData", sumData);
        
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("withDebt", true);

        total = new Long(remainArrearsList.size());

        ArrearsInfoMakeExcel excel = new ArrearsInfoMakeExcel();
        int cnt = 1;
        int idx = 0;
        int fnum = 0;
        int splCnt = 0;

        if (total <= maxRows) {
        	Map<String,Object> dataList = new HashMap<String,Object>();
        	dataList.put("arrearsList", remainArrearsList);
        	dataList.put("debtsList", remainDebtsList);
        	
            result.put("dataList", dataList);
            splFileName = new StringBuilder(fileName);
            splFileName.append(".xls");
            excel.writeReportExcel(result, supplier, filePath, splFileName.toString(), logoImg);
            fileNameList.add(splFileName.toString());
        } else {
            for (int i = 0; i < total; i++) {
                if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
                    splFileName = new StringBuilder(fileName);
                    splFileName.append('(').append(++fnum).append(").xls");
                    
                    Map<String,Object> dataList = new HashMap<String,Object>();
                	dataList.put("arrearsList", remainArrearsList.subList(idx, (i + 1)));
                	dataList.put("debtsList",  remainDebtsList.subList(idx, (i + 1)));
                	
                    result.put("dataList", dataList);
                    
                    excel.writeReportExcel(result, supplier, filePath, splFileName.toString(), logoImg);
                    fileNameList.add(splFileName.toString());
                    splCnt = cnt;
                    cnt = 0;
                    idx = (i + 1);
                }
                cnt++;
            }
        }

        StringBuilder zipFile = new StringBuilder(fileName);
        zipFile.append(".zip");
        ZipUtils zutils = new ZipUtils();
        try {
            zutils.zipEntry(fileNameList, zipFile.toString(), filePath);
            mav.addObject("zipFileName", zipFile.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }

        mav.addObject("filePath", filePath);
        mav.addObject("fileName", fileNameList.get(0));
        mav.addObject("fileNames", fileNameList);
        return mav;
    }
    
    /**
     * method name : vendorChargeHistoryExcelMake<b/>
     * method Desc :
     *
     * @param supplierId
     * @param vendor
     * @param vendorRole
     * @param reportType
     * @param subType
     * @param contract
     * @param customerName
     * @param customerNo
     * @param meterId
     * @param startDate
     * @param endDate
     * @param casherId
     * @param locationId
     * @param filePath
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/prepaymentMgmt/vendorChargeHistoryExcelMake")
    public ModelAndView vendorChargeHistoryExcelMake(@RequestParam Integer supplierId,
            String vendor,
            String vendorRole,
            String reportType,
            @RequestParam(value="subType", required=false) String subType,
            String contract,
            String customerName,
            String customerNo,
            String meterId,
            String startDate,
            String endDate,
            String casherId,
            Integer locationId,
            @RequestParam String filePath,
            String logoImg) {
        ModelAndView mav = new ModelAndView("jsonView");
        String prefix = "depositHistoryData";
        StringBuilder fileName = new StringBuilder(prefix);
        StringBuilder splFileName = new StringBuilder();
        Supplier supplier = supplierDao.get(supplierId);
        fileName.append(TimeUtil.getCurrentTimeMilli());
        Boolean isLast = false;
        Long total = 0L; // data count init
        Long maxRows = 5000L; // 엑셀 하나에 보여줄수 있는 최대 데이터 row 수

        List<String> fileNameList = new ArrayList<String>();
        Map<String, Object> condition = new HashMap<String, Object>();

        if (!StringUtil.nullToBlank(vendor).isEmpty()) {
            condition.put("vendor", vendor);
        }
        condition.put("supplierId", supplierId);
        condition.put("reportType", reportType);
        condition.put("subType", subType);
        condition.put("contract", contract);
        condition.put("customerName", customerName);
        condition.put("customerNo", customerNo);
        condition.put("meterId", meterId);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        condition.put("casherId", casherId);
        condition.put("loginIntId", null);
        condition.put("onlyLoginData", false);

		List<Integer> rootLocList = locationDao.getRoot();
		int rootLocSize = rootLocList == null ? 0 : rootLocList.size();
		
        if (locationId != null && (!locationDao.isRoot(locationId) || rootLocSize > 1)) {
            List<Integer> locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            condition.put("locationIdList", locationIdList);
        }

        File downDir = new File(filePath);

        if (downDir.exists()) {
            File[] files = downDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        } else {
            downDir.mkdir();
        }

        Map<String, Object> result = new HashMap<String, Object>();

        List<Map<String, Object>> depositHistoryList = (List<Map<String, Object>>)depositHistoryDao.getDepositHistoryList(
                condition).get("list");

        result.put("startDate", startDate);
        result.put("endDate", endDate);
        List<Map<String, String>> dataList = getExcelData(depositHistoryList, supplier);

        total = new Long(depositHistoryList.size());

        DepositHistoryDataMakeExcel excel = new DepositHistoryDataMakeExcel();
        int cnt = 1;
        int idx = 0;
        int fnum = 0;
        int splCnt = 0;

        if (total <= maxRows) {
            result.put("dataList", dataList);
            splFileName = new StringBuilder(fileName);
            splFileName.append(".xls");
            excel.writeReportExcel(result, supplier, reportType, isLast, filePath, splFileName.toString(), StringUtil.nullToBlank(supplier.getDescr()), logoImg);
            fileNameList.add(splFileName.toString());
        } else {
            for (int i = 0; i < total; i++) {
                if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
                    splFileName = new StringBuilder(fileName);
                    splFileName.append('(').append(++fnum).append(").xls");
                    result.put("dataList", dataList.subList(idx, (i + 1)));
                    excel.writeReportExcel(result, supplier, reportType, isLast, filePath, splFileName.toString(), StringUtil.nullToBlank(supplier.getDescr()), logoImg);
                    fileNameList.add(splFileName.toString());
                    splCnt = cnt;
                    cnt = 0;
                    idx = (i + 1);
                }
                cnt++;
            }
        }

        StringBuilder zipFile = new StringBuilder(fileName);
        zipFile.append(".zip");
        ZipUtils zutils = new ZipUtils();
        try {
            zutils.zipEntry(fileNameList, zipFile.toString(), filePath);
            mav.addObject("zipFileName", zipFile.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }

        mav.addObject("filePath", filePath);
        mav.addObject("fileName", fileNameList.get(0));
        mav.addObject("fileNames", fileNameList);
        return mav;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/prepaymentMgmt/vendorChargeHistoryWithDebtExcelMake")
    public ModelAndView vendorChargeHistoryWithDebtExcelMake(@RequestParam Integer supplierId,
            String vendor,
            String vendorRole,
            String reportType,
            @RequestParam(value="subType", required=false) String subType,
            String contract,
            String customerName,
            String customerNo,
            String meterId,
            String startDate,
            String endDate,
            String casherId,
            Integer locationId,
            @RequestParam String filePath,
            String logoImg) {
        ModelAndView mav = new ModelAndView("jsonView");
        String prefix = "depositHistoryData";
        StringBuilder fileName = new StringBuilder(prefix);
        StringBuilder splFileName = new StringBuilder();
        Supplier supplier = supplierDao.get(supplierId);
        fileName.append(TimeUtil.getCurrentTimeMilli());
        Boolean isLast = false;
        Long total = 0L; // data count init
        Long maxRows = 5000L; // 엑셀 하나에 보여줄수 있는 최대 데이터 row 수

        List<String> fileNameList = new ArrayList<String>();
        Map<String, Object> condition = new HashMap<String, Object>();

        if (!StringUtil.nullToBlank(vendor).isEmpty()) {
            condition.put("vendor", vendor);
        }
        condition.put("supplierId", supplierId);
        condition.put("reportType", reportType);
        condition.put("subType", subType);
        condition.put("contract", contract);
        condition.put("customerName", customerName);
        condition.put("customerNo", customerNo);
        condition.put("meterId", meterId);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        condition.put("casherId", casherId);
        condition.put("loginIntId", null);
        condition.put("onlyLoginData", false);

		List<Integer> rootLocList = locationDao.getRoot();
		int rootLocSize = rootLocList == null ? 0 : rootLocList.size();
		
        if (locationId != null && (!locationDao.isRoot(locationId) || rootLocSize > 1)) {
            List<Integer> locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            condition.put("locationIdList", locationIdList);
        }

        File downDir = new File(filePath);

        if (downDir.exists()) {
            File[] files = downDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        } else {
            downDir.mkdir();
        }

        Map<String, Object> result = new HashMap<String, Object>();

        List<Map<String, Object>> depositHistoryList = (List<Map<String, Object>>)debtEntManager.getDepositHistoryList(
                condition).get("list");

        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("withDebt", true);
        List<Map<String, String>> dataList = getExcelDataUpperCase(depositHistoryList, supplier);

        total = new Long(depositHistoryList.size());

        DepositHistoryDataMakeExcel excel = new DepositHistoryDataMakeExcel();
        int cnt = 1;
        int idx = 0;
        int fnum = 0;
        int splCnt = 0;

        if (total <= maxRows) {
            result.put("dataList", dataList);
            splFileName = new StringBuilder(fileName);
            splFileName.append(".xls");
            excel.writeReportExcel(result, supplier, reportType, isLast, filePath, splFileName.toString(), StringUtil.nullToBlank(supplier.getDescr()), logoImg);
            fileNameList.add(splFileName.toString());
        } else {
            for (int i = 0; i < total; i++) {
                if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
                    splFileName = new StringBuilder(fileName);
                    splFileName.append('(').append(++fnum).append(").xls");
                    result.put("dataList", dataList.subList(idx, (i + 1)));
                    excel.writeReportExcel(result, supplier, reportType, isLast, filePath, splFileName.toString(), StringUtil.nullToBlank(supplier.getDescr()), logoImg);
                    fileNameList.add(splFileName.toString());
                    splCnt = cnt;
                    cnt = 0;
                    idx = (i + 1);
                }
                cnt++;
            }
        }

        StringBuilder zipFile = new StringBuilder(fileName);
        zipFile.append(".zip");
        ZipUtils zutils = new ZipUtils();
        try {
            zutils.zipEntry(fileNameList, zipFile.toString(), filePath);
            mav.addObject("zipFileName", zipFile.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }

        mav.addObject("filePath", filePath);
        mav.addObject("fileName", fileNameList.get(0));
        mav.addObject("fileNames", fileNameList);
        return mav;
    }

    /**
     * method name : vendorChargeHistoryTotalExcelMake<b/>
     * method Desc :
     *
     * @param supplierId
     * @param vendor
     * @param vendorRole
     * @param reportType
     * @param subType
     * @param contract
     * @param customerName
     * @param customerNo
     * @param meterId
     * @param startDate
     * @param endDate
     * @param casherId
     * @param locationId
     * @param filePath
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/prepaymentMgmt/vendorChargeHistoryTotalExcelMake")
    public ModelAndView vendorChargeHistoryTotalExcelMake(@RequestParam Integer supplierId,
            String vendor,
            String vendorRole,
            String reportType,
            @RequestParam(value="subType", required=false) String subType,
            String contract,
            String customerName,
            String customerNo,
            String meterId,
            String startDate,
            String endDate,
            String casherId,
            Integer locationId,
            @RequestParam String filePath,
    		String logoImg) {
        ModelAndView mav = new ModelAndView("jsonView");
        String prefix = "depositHistoryTotalData";
        StringBuilder fileName = new StringBuilder(prefix);
        StringBuilder splFileName = new StringBuilder();
        Supplier supplier = supplierDao.get(supplierId);
        fileName.append(TimeUtil.getCurrentTimeMilli());
        Boolean isLast = true;

        List<String> fileNameList = new ArrayList<String>();

        Map<String, Object> condition = new HashMap<String, Object>();

        if (!StringUtil.nullToBlank(vendor).isEmpty()) {
            condition.put("vendor", vendor);
        }
        condition.put("supplierId", supplierId);
        condition.put("reportType", reportType);
        condition.put("subType", subType);
        condition.put("contract", contract);
        condition.put("customerName", customerName);
        condition.put("customerNo", customerNo);
        condition.put("meterId", meterId);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        condition.put("casherId", casherId);
        condition.put("loginIntId", null);
        condition.put("onlyLoginData", false);

        if (locationId != null && !locationDao.isRoot(locationId)) {
            List<Integer> locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            condition.put("locationIdList", locationIdList);
        }

        File downDir = new File(filePath);

        if (downDir.exists()) {
            File[] files = downDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        } else {
            downDir.mkdir();
        }

        Map<String, Object> result = new HashMap<String, Object>();
        DepositHistoryDataMakeExcel excel = new DepositHistoryDataMakeExcel();
        List<Map<String, Object>> depositHistoryList = (List<Map<String, Object>>)depositHistoryDao.getDepositHistoryList(condition).get("list");

        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("withDebt", false);
        List<Map<String, String>> dataList = getExcelData(depositHistoryList, supplier);

        result.put("dataList", dataList);
        splFileName = new StringBuilder(fileName);
        splFileName.append(".xls");
        excel.writeReportExcel(result, supplier, reportType, isLast, filePath, splFileName.toString(), StringUtil.nullToBlank(supplier.getDescr()), logoImg);
        fileNameList.add(splFileName.toString());

        StringBuilder zipFile = new StringBuilder(fileName);
        zipFile.append(".zip");
        ZipUtils zutils = new ZipUtils();
        try {
            zutils.zipEntry(fileNameList, zipFile.toString(), filePath);
            mav.addObject("zipFileName", zipFile.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }        

        mav.addObject("filePath", filePath);
        mav.addObject("fileName", fileNameList.get(0));
        mav.addObject("fileNames", fileNameList);
        return mav;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/prepaymentMgmt/vendorChargeHistoryWithDebtTotalExcelMake")
    public ModelAndView vendorChargeHistoryWithDebtTotalExcelMake(@RequestParam Integer supplierId,
            String vendor,
            String vendorRole,
            String reportType,
            @RequestParam(value="subType", required=false) String subType,
            String contract,
            String customerName,
            String customerNo,
            String meterId,
            String startDate,
            String endDate,
            String casherId,
            Integer locationId,
            @RequestParam String filePath,
    		String logoImg) {
        ModelAndView mav = new ModelAndView("jsonView");
        String prefix = "depositHistoryTotalData";
        StringBuilder fileName = new StringBuilder(prefix);
        StringBuilder splFileName = new StringBuilder();
        Supplier supplier = supplierDao.get(supplierId);
        fileName.append(TimeUtil.getCurrentTimeMilli());
        Boolean isLast = true;

        List<String> fileNameList = new ArrayList<String>();

        Map<String, Object> condition = new HashMap<String, Object>();

        if (!StringUtil.nullToBlank(vendor).isEmpty()) {
            condition.put("vendor", vendor);
        }
        condition.put("supplierId", supplierId);
        condition.put("reportType", reportType);
        condition.put("subType", subType);
        condition.put("contract", contract);
        condition.put("customerName", customerName);
        condition.put("customerNo", customerNo);
        condition.put("meterId", meterId);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        condition.put("casherId", casherId);
        condition.put("loginIntId", null);
        condition.put("onlyLoginData", false);

        if (locationId != null && !locationDao.isRoot(locationId)) {
            List<Integer> locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            condition.put("locationIdList", locationIdList);
        }

        File downDir = new File(filePath);

        if (downDir.exists()) {
            File[] files = downDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        } else {
            downDir.mkdir();
        }

        Map<String, Object> result = new HashMap<String, Object>();
        DepositHistoryDataMakeExcel excel = new DepositHistoryDataMakeExcel();
        List<Map<String, Object>> depositHistoryList = (List<Map<String, Object>>)debtEntManager.getDepositHistoryList(condition).get("list");

        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("withDebt", true);
        List<Map<String, String>> dataList = getExcelDataUpperCase(depositHistoryList, supplier);

        result.put("dataList", dataList);
        splFileName = new StringBuilder(fileName);
        splFileName.append(".xls");
        excel.writeReportExcel(result, supplier, reportType, isLast, filePath, splFileName.toString(), StringUtil.nullToBlank(supplier.getDescr()), logoImg);
        fileNameList.add(splFileName.toString());

        StringBuilder zipFile = new StringBuilder(fileName);
        zipFile.append(".zip");
        ZipUtils zutils = new ZipUtils();
        try {
            zutils.zipEntry(fileNameList, zipFile.toString(), filePath);
            mav.addObject("zipFileName", zipFile.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }        

        mav.addObject("filePath", filePath);
        mav.addObject("fileName", fileNameList.get(0));
        mav.addObject("fileNames", fileNameList);
        return mav;
    }
    
        /**
     * method name : getExcelData<b/>
     * method Desc :
     *
     * @param historyList
     * @param supplier
     * @return
     */
    private List<Map<String, String>> getExcelData(List<Map<String, Object>> historyList, Supplier supplier) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        String country = supplier.getCountry().getCode_2letter();
        String lang = supplier.getLang().getCode_2letter();
        Map<String, String> total = new HashMap<String, String>();
        Double totalChargedCredit = 0d;
        Double totalChargedArrears = 0d;
        Double totalChargedDeposit = 0d;
        Double totalChargedCommission = 0d;
        Double totalChargedTax = 0d;
        Double totalChargedNetValue = 0d;

        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        if (supplier != null) {
            df = new DecimalFormat(supplier.getCd().getPattern());
        }

        for (Map<String, Object> map : historyList) {
            Map<String, String> data = new HashMap<String, String>();
            Integer prepaymentLogId = (map.get("prepaymentLogId") == null) ? null : ((Long)map.get("prepaymentLogId"))
                    .intValue();
            String changeDate = (String)map.get("changeDate");
            Double chargedCredit = (Double)map.get("chargedCredit");
            Double chargedArrears = (Double)map.get("chargedArrears");
            Integer vendorCasherId = (Integer)map.get("vendorCasherId");
            Integer vendingStationId = (Integer)map.get("vendingStationId");
            Integer contractId = (Integer)map.get("contractId");
            Integer meterId = (Integer)map.get("meterId");
            Integer tariffId = (Integer)map.get("tariffId");
            Integer customerId = (Integer)map.get("customerId");
            String payType = map.get("payType") != null ? (String)map.get("payType") : "Cash";
            Boolean isCanceled = map.get("isCanceled") == null ? false : (Boolean)map.get("isCanceled");
            String cancelDate = (String)map.get("cancelDate");
            String cancelReason = (String)map.get("cancelReason");
            String lastTokenId = (String)map.get("lastTokenId");

            String dbDate = changeDate == null ? CalendarUtil.getCurrentDate() : changeDate;
            String dbCancelDate = cancelDate == null ? CalendarUtil.getCurrentDate() : cancelDate;
            String date = TimeLocaleUtil.getLocaleDate(dbDate, lang, country);
            String cancelLocaleDate = null;
            if(cancelDate != null) {
            	cancelLocaleDate = TimeLocaleUtil.getLocaleDate(dbCancelDate, lang, country);
            }
            data.put("date", date);

            if (prepaymentLogId != null) {
                data.put("prepaymentLogId", "SC--" + map.get("prepaymentLogId").toString());
                data.put("paymentType", payType);
                data.put("chargedCredit", df.format(StringUtil.nullToDoubleZero(chargedCredit)));
                data.put("chargedArrears", df.format(StringUtil.nullToDoubleZero(chargedArrears)));
                data.put("cancelReason", cancelReason);
                data.put("cancelDate", cancelLocaleDate);
                data.put("lastTokenId", lastTokenId);

                totalChargedCredit += StringUtil.nullToDoubleZero(chargedCredit);
                totalChargedArrears += StringUtil.nullToDoubleZero(chargedArrears);

                if (vendorCasherId != null) {
                    data.put("cashier", (String)map.get("vendorCasherName"));
                }
                if (vendingStationId != null) {
                    data.put("vendingStationName", (String)map.get("vendingStationName"));
                }

                if (contractId != null) {
                    data.put("geoCode", (String)map.get("geoCode"));
                    try {
                        if (meterId != null) {
                            data.put("meterId", (String)map.get("mdsId"));
                        }
                    } catch(Exception e) {
                        log.error("contractId: " + contractId);
                        log.error("meterId: " + meterId);
                        e.printStackTrace();
                    }

                    try {
                        if (tariffId != null) {
                            data.put("tariffName", (String)map.get("tariffName"));
                        }
                    } catch(Exception e) {
                        log.error("contractId: " + contractId);
                        log.error("tariffTypeId: " + tariffId);
                        e.printStackTrace();
                    }

                    try {
                        if (customerId != null) {
                            data.put("customerName", (String)map.get("customerName"));
                            data.put("accountNo", (String)map.get("customerNo"));
                            String address = StringUtil.nullToBlank((String)map.get("address")) + " " 
                            		+ StringUtil.nullToBlank((String)map.get("address1")) + " "
                                    + StringUtil.nullToBlank((String)map.get("address2"));
                            data.put("address", address);
                        }
                    } catch(Exception e) {
                        log.error("contractId: " + contractId);
                        log.error("customerId: " + customerId);
                        e.printStackTrace();
                    }
                }
            } else {
                Double chargeDeposit = StringUtil.nullToDoubleZero((Double)map.get("chargeDeposit"));
                Double commissionRate = StringUtil.nullToDoubleZero(((Float)map.get("commission") == null) ? null : ((Float)map
                        .get("commission")).doubleValue());
                Double commisstion = chargeDeposit * commissionRate * 0.01;
                Double tax = StringUtil.nullToDoubleZero((Double)map.get("tax"));
                Double netValue = StringUtil.nullToDoubleZero((Double)map.get("netValue"));
                Integer historyOpId = (Integer)map.get("historyOpId");
                data.put("depositHistoryId", "SC--" + map.get("depositHistoryId").toString());
                data.put("chargedDeposit", df.format(chargeDeposit));
                data.put("commission", df.format(commisstion));
                data.put("tax", df.format(tax));
                data.put("netValue", df.format(netValue));
                totalChargedDeposit += chargeDeposit;
                totalChargedCommission += StringUtil.nullToDoubleZero(commisstion);
                totalChargedTax += tax;
                totalChargedNetValue += netValue;

                if (historyOpId != null) {
                    data.put("vendingStationName", (String)map.get("historyOpName"));
                }
            }
            total.put("totalChargedCredit", df.format(totalChargedCredit));
            total.put("totalChargedArrears", df.format(totalChargedArrears));
            total.put("totalChargedDeposit", df.format(totalChargedDeposit));
            total.put("totalChargedCommission", df.format(totalChargedCommission));
            total.put("totalChargedTax", df.format(totalChargedTax));
            total.put("totalChargedNetValue", df.format(totalChargedNetValue));
            result.add(data);
        }
        result.add(total);
        return result;
    }

    /**
     * method name : getExcelDataUpperCase<b/>
     * method Desc :
     *
     * @param historyList
     * @param supplier
     * @return
     */
    private List<Map<String, String>> getExcelDataUpperCase(List<Map<String, Object>> historyList, Supplier supplier) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        String country = supplier.getCountry().getCode_2letter();
        String lang = supplier.getLang().getCode_2letter();
        Map<String, String> total = new HashMap<String, String>();
        BigDecimal totalChargedCredit = new BigDecimal(0);
        BigDecimal totalChargedArrears = new BigDecimal(0);
        BigDecimal totalChargedDebts = new BigDecimal(0);
        BigDecimal totalChargedDeposit = new BigDecimal(0);
        BigDecimal totalChargedCommission = new BigDecimal(0);
        BigDecimal totalChargedTax = new BigDecimal(0);
        BigDecimal totalChargedNetValue = new BigDecimal(0);

        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        if (supplier != null) {
            df = new DecimalFormat(supplier.getCd().getPattern());
        }

        for (Map<String, Object> map : historyList) {
            Map<String, String> data = new HashMap<String, String>();
            
            String changeDate = (String)map.get("CHANGEDATE");
            Double chargedCredit = map.get("CHARGEDCREDIT") != null ? Double.parseDouble(map.get("CHARGEDCREDIT").toString()) : 0d;
            Double chargedArrears = map.get("CHARGEDARREARS") != null ? Double.parseDouble(map.get("CHARGEDARREARS").toString()) : 0d;
            Double chargedDebts = map.get("CHARGEDDEBTSUM") != null ? Double.parseDouble(map.get("CHARGEDDEBTSUM").toString()) : 0d;
            Integer vendorCasherId = map.get("VENDORCASHERID") != null ? Integer.parseInt(map.get("VENDORCASHERID").toString()) : null;
            Integer vendingStationId = map.get("VENDINGSTATIONID") != null ? Integer.parseInt(map.get("VENDINGSTATIONID").toString()) : null;
            Integer contractId = map.get("CONTRACTID") != null ? Integer.parseInt(map.get("CONTRACTID").toString()) : null;
            Integer meterId = map.get("METERID") != null ? Integer.parseInt(map.get("METERID").toString()) : null;
            Integer tariffId = map.get("TARIFFID") != null ? Integer.parseInt(map.get("TARIFFID").toString()) : null;
            Integer customerId = map.get("CUSTOMERID") != null ? Integer.parseInt(map.get("CUSTOMERID").toString()) : null;
            String payType = map.get("PAYTYPE") != null ? (String)map.get("PAYTYPE") : "Cash";
            Boolean isCanceled = false;
            
            if(map.get("ISCANCELED") != null && map.get("ISCANCELED") instanceof Short) {
            	isCanceled = ((Short)map.get("ISCANCELED")) == 1 ?true:false;
            }
            
            if(map.get("ISCANCELED") != null && map.get("ISCANCELED") instanceof Boolean) {
            	isCanceled = (Boolean)map.get("ISCANCELED");
            }
            
            String cancelDate = (String)map.get("CANCELDATE");
            String cancelReason = (String)map.get("CANCELREASON");
            String lastTokenId = (String)map.get("LASTTOKENID");

            String dbDate = changeDate == null ? CalendarUtil.getCurrentDate() : changeDate;
            String dbCancelDate = cancelDate == null ? CalendarUtil.getCurrentDate() : cancelDate;
            String date = TimeLocaleUtil.getLocaleDate(dbDate, lang, country);
            String cancelLocaleDate = null;
            if(cancelDate != null) {
            	cancelLocaleDate = TimeLocaleUtil.getLocaleDate(dbCancelDate, lang, country);
            }
            data.put("date", date);

            if (map.get("PREPAYMENTLOGID") != null) {
                data.put("prepaymentLogId", "SC--" + map.get("PREPAYMENTLOGID").toString());
                data.put("paymentType", payType);
                data.put("chargedCredit", df.format(chargedCredit));
                data.put("chargedArrears", df.format(chargedArrears));
                data.put("chargedDebts", df.format(chargedDebts));
                data.put("cancelReason", cancelReason);
                data.put("cancelDate", cancelLocaleDate);
                data.put("lastTokenId", lastTokenId);

                totalChargedCredit = totalChargedCredit.add(new BigDecimal(chargedCredit));
                totalChargedArrears = totalChargedArrears.add(new BigDecimal(chargedArrears));
                totalChargedDebts = totalChargedDebts.add(new BigDecimal(chargedDebts));

                if (vendorCasherId != null) {
                    data.put("cashier", (String)map.get("VENDORCASHERNAME"));
                }
                if (vendingStationId != null) {
                    data.put("vendingStationName", (String)map.get("VENDINGSTATIONNAME"));
                }

                if (contractId != null) {
                    data.put("geoCode", (String)map.get("GEOCODE"));
                    try {
                        if (meterId != null) {
                            data.put("meterId", (String)map.get("MDSID"));
                        }
                    } catch(Exception e) {
                        log.error("contractId: " + contractId);
                        log.error("meterId: " + meterId);
                        e.printStackTrace();
                    }

                    try {
                        if (tariffId != null) {
                            data.put("tariffName", (String)map.get("TARIFFNAME"));
                        }
                    } catch(Exception e) {
                        log.error("contractId: " + contractId);
                        log.error("tariffTypeId: " + tariffId);
                        e.printStackTrace();
                    }

                    try {
                        if (customerId != null) {
                            data.put("customerName", (String)map.get("CUSTOMERNAME"));
                            data.put("accountNo", (String)map.get("CUSTOMERNO"));
                            String address = StringUtil.nullToBlank((String)map.get("ADDRESS")) + " " 
                            		+ StringUtil.nullToBlank((String)map.get("ADDRESS1")) + " "
                                    + StringUtil.nullToBlank((String)map.get("ADDRESS2"));
                            data.put("address", address);
                        }
                    } catch(Exception e) {
                        log.error("contractId: " + contractId);
                        log.error("customerId: " + customerId);
                        e.printStackTrace();
                    }
                }
            } else {
            	BigDecimal chargeDeposit = map.get("CHARGEDEPOSIT") == null ? new BigDecimal(0) : new BigDecimal(Double.parseDouble(map.get("CHARGEDEPOSIT").toString()));
                BigDecimal commissionRate = map.get("COMMISSION") == null ? new BigDecimal(0) : new BigDecimal(Double.parseDouble(map.get("COMMISSION").toString()));
                
                BigDecimal commisstion = chargeDeposit.multiply(commissionRate.multiply(new BigDecimal(0.01)));
                BigDecimal tax = map.get("TAX") == null ? new BigDecimal(0) : new BigDecimal(Double.parseDouble(map.get("TAX").toString()));
                BigDecimal netValue = map.get("NETVALUE") == null ? new BigDecimal(0) : new BigDecimal(Double.parseDouble(map.get("NETVALUE").toString()));
                Integer historyOpId = map.get("HISTORYOPID") == null ? null : Integer.parseInt(map.get("HISTORYOPID").toString());
                data.put("depositHistoryId", "SC--" + map.get("DEPOSITHISTORYID").toString());
                data.put("chargedDeposit", df.format(chargeDeposit));
                data.put("commission", df.format(commisstion));
                data.put("tax", df.format(tax));
                data.put("netValue", df.format(netValue));
                totalChargedDeposit = totalChargedDeposit.add(chargeDeposit);
                totalChargedCommission = totalChargedCommission.add(commisstion);
                totalChargedTax = totalChargedTax.add(tax);
                totalChargedNetValue = totalChargedNetValue.add(netValue);

                if (historyOpId != null) {
                    data.put("vendingStationName", (String)map.get("HISTORYOPNAME"));
                }
            }
            total.put("totalChargedCredit", df.format(totalChargedCredit));
            total.put("totalChargedArrears", df.format(totalChargedArrears));
            total.put("totalChargedDebts", df.format(totalChargedDebts));
            total.put("totalChargedDeposit", df.format(totalChargedDeposit));
            total.put("totalChargedCommission", df.format(totalChargedCommission));
            total.put("totalChargedTax", df.format(totalChargedTax));
            total.put("totalChargedNetValue", df.format(totalChargedNetValue));
            result.add(data);
        }
        result.add(total);
        return result;
    }

    
    @RequestMapping(value="/gadget/prepaymentMgmt/vendorPrepaymentPayType")
    public ModelAndView vendorPrepaymentPayType() {
        ModelAndView mav = new ModelAndView("jsonView");

        List<Map<String, Object>>result = prepaymentChargeManager.vendorPrepaymentPayType();
        mav.addObject("payTypeList", result);
        return mav;
    }
    
    
}