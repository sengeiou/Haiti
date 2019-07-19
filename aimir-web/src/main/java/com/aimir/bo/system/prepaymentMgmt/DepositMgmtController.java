package com.aimir.bo.system.prepaymentMgmt;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections.list.TreeList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.common.CommandProperty;
import com.aimir.dao.system.DepositHistoryDao;
import com.aimir.dao.system.LanguageDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.prepayment.DepositHistory;
import com.aimir.model.system.Language;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.excel.MonthlyConsumeData;
import com.aimir.schedule.excel.MonthlyConsumeExcel;
import com.aimir.schedule.excel.RegionalConsumeData;
import com.aimir.schedule.excel.RegionalConsumeExcel;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.depositMgmt.DepositMgmtManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils;
import com.aimir.util.DepositHistoryDataMakeExcel;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

/**
 * @FileName DepositMgmtController.java
 * @Date 2013. 6. 25.
 * @author khk
 * @ModifiedDate
 * @Descr 관리자 계정의 예치금 관리 화면의 Controller
 */

@Controller
public class DepositMgmtController {
	Log logger = LogFactory.getLog(DepositMgmtController.class);
	@Autowired
	DepositMgmtManager depositMgmtManager;
	
	@Autowired
	SupplierDao supplierDao;
	
	@Autowired
	DepositHistoryDao depositHistoryDao;

	@Autowired
	OperatorDao operatorDao;

    @Autowired
    RoleManager roleManager;
    
    @Autowired
    LanguageDao languageDao;
    
    @Autowired
    PrepaymentLogDao prepaymentLogDao;
    
    @Autowired
    LocationDao locationDao;
    
	@RequestMapping("/gadget/prepaymentMgmt/depositMgmtMax")
    public ModelAndView depositMgmtMax() {
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/depositMgmtMax");
        String FILE_PATH = "/tmp/monthly";
        
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        //user에서 그냥 supplier를 가져오면 이전 supplier객체를 가지고 온다.
        Supplier supplier = supplierDao.get(user.getSupplier().getId());
        
        float taxRate = 0f;
        float commissionRate = 0f;
        
        if (supplier != null) {
        	taxRate =  supplier.getTaxRate() == null ? 0f : supplier.getTaxRate();
            commissionRate = supplier.getCommissionRate() == null ? 0f : supplier.getCommissionRate();
        } 
    	mav.addObject("filePath", FILE_PATH);        
        mav.addObject("taxRate", taxRate);
        mav.addObject("commissionRate", commissionRate);

        Role role = roleManager.getRole(user.getRoleData().getId());
        Boolean isAdmin = role.getName().equals("admin");
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("isAdmin", isAdmin); // taxRate, commissionRate 수정 권한
        mav.addObject("editAuth", authMap.get("cud")); // 수정권한 (write/command = true)
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("roleType", "vendor");
        params.put("supplierId", supplier.getId());
        
        mav.addObject("depositVendorList", operatorDao.getOperatorListByRoleType(params));
        mav.addObject("vendor", user.getLoginId());
        mav.addObject("role", role.getName());
        
        try {
        	Properties prop = new Properties();
            prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));            
            mav.addObject("logoImg", prop.getProperty("supplier.logo.filename") == null ? "/images/ECG_logo.gif" : prop.getProperty("supplier.logo.filename").trim());
		} catch (Exception e) {
			logger.debug(e,e);	
		}
        
        return mav;
    }

    @RequestMapping("/gadget/prepaymentMgmt/depositMgmtMini")
    public ModelAndView depositMgmtMini() {
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/depositMgmtMini");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("editAuth", authMap.get("cud")); // 수정권한 (write/command = true)
        return mav;
    }

	@RequestMapping("/gadget/prepaymentMgmt/operatorList")
	public ModelAndView getOperatorByLoginIdAndName(@RequestParam int page, @RequestParam int limit, 
			@RequestParam int supplierId, String loginId, String name) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> map = 
				depositMgmtManager.getOperatorByLoginIdAndName(page, limit, supplierId, loginId, name);
		mav.addObject("count", map.get("count"));
		mav.addObject("list", map.get("list"));
		return mav;
	}
	
	@RequestMapping("/gadget/prepaymentMgmt/chargeDeposit")
	public ModelAndView chargeDeposit(@RequestParam Integer supplierId, 
			@RequestParam String vendorId, @RequestParam Double amount, @RequestParam String date, @RequestParam String loginId) {
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("supplierId", supplierId);
		condition.put("vendorId", vendorId);
		condition.put("amount", amount);
		condition.put("date", date);
		condition.put("loginId", loginId);
		DepositHistory result = depositMgmtManager.chargeDeposit(condition);
		mav.addObject(result);
		return mav;
	}
	
    /**
     * @MethodName depositCancel
     * @Date 2015. 02. 10
     * @param id: preapymentLogId
     * @param vendor: Operator.loginId
     * @return
     * @Modified
     * @Description
     */
    @RequestMapping("/gadget/prepaymentMgmt/depositCancel")
    public ModelAndView depositCancel(@RequestParam Integer depositHistoryId,
            @RequestParam String vendor) {
        ModelAndView mav = new ModelAndView("jsonView");
        String result = depositMgmtManager.depositCancelTransaction(depositHistoryId, vendor);
        mav.addObject("result", result);
        return mav;
    }

    @RequestMapping("/gadget/prepaymentMgmt/historyList")
    public ModelAndView getHistory(@RequestParam int page,
            @RequestParam int limit,
            int supplierId,
            String reportType,
            @RequestParam(value="subType", required=false) String subType,
            String casherId,
            String vendor, 
            String vendorRole,
            Boolean fromDepositGadget,
            String contract,
            String customerName,
            String customerNo,
            String meterId,
            String startDate,
            String endDate,
            Integer loginIntId,
            boolean onlyLoginData,
            boolean vendorOnly) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> params = new HashMap<String, Object>();

        fromDepositGadget = fromDepositGadget == null ? false : true;

        // vending station의 operator role이 admin인 경우 조회범위를 전체 vending station으로 한다.  
        if ( !StringUtil.nullToBlank(vendorRole).equals("admin") || fromDepositGadget) {
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
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("vendorOnly",vendorOnly);
        params.put("reportType", reportType);
        params.put("subType", subType);
        params.put("loginIntId", loginIntId);
        params.put("onlyLoginData", onlyLoginData);

        Map<String, Object> result = depositMgmtManager.getDepositHistoryList(params);
        mav.addObject("count", result.get("count"));
        mav.addObject("list", result.get("list"));
        return mav;
    }

	@RequestMapping(value = "/gadget/prepaymentMgmt/depoistChargeReceiptPopup")
	public ModelAndView openDepositChargeReceiptPopup(@RequestParam Integer supplierId, 
			@RequestParam Integer depositHistoryId, @RequestParam String vendorId) {
		DepositHistory depositHistory = depositHistoryDao.get(depositHistoryId);
		Supplier supplier = supplierDao.get(supplierId);
		Operator operator = operatorDao.getOperatorByLoginId(vendorId);
		
		//공급사별로 영수증 포맷을 다르게 적용하기 위함
		//공급사이름으로 파일을 찾아 파일이 존재하면 해당 공급사의 파일을 찾아가고 그렇지 않으면 초기 버전의 영수증 포맷을 보여준다.
		String filePath = ESAPI.httpUtilities().getCurrentRequest().getRealPath("/gadget/prepaymentMgmt/depositChargeReceiptPopupFor"+supplier.getName()+".jsp");
		File file = new File(filePath);

		ModelAndView mav = new ModelAndView();
		
		if(file.exists())
			mav.setViewName("/gadget/prepaymentMgmt/depositChargeReceiptPopupFor"+supplier.getName());
		else 
			mav.setViewName("/gadget/prepaymentMgmt/depositChargeReceiptPopup");
		
		DecimalFormat df = new DecimalFormat("###,###,##0.00");
		if(supplier != null) {
			df = new DecimalFormat(supplier.getCd().getPattern());
		}
		
		Float commission = depositHistory.getCommission();
		Double amount = depositHistory.getChargeDeposit();
		Double commissionValue = 0d; 
		if ( commission != null && amount != null) {
			commissionValue = amount * commission/100; 
		}
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		String date = TimeLocaleUtil.getLocaleDate(depositHistory.getChangeDate(), lang, country);
		
		mav.addObject("name", operator.getName());
		mav.addObject("location", (operator.getLocation() != null) ? operator.getLocation().getName() : "");
		mav.addObject("date", date);
		mav.addObject("receiptNo", depositHistoryId);
		mav.addObject("amount", df.format(depositHistory.getChargeDeposit()));
		mav.addObject("commission", depositHistory.getCommission());
		mav.addObject("commissionValue", df.format(StringUtil.nullToDoubleZero(commissionValue)));
		mav.addObject("value", df.format(StringUtil.nullToDoubleZero(depositHistory.getValue())));
		mav.addObject("taxRate", depositHistory.getTaxRate());
		mav.addObject("tax", df.format(StringUtil.nullToDoubleZero(depositHistory.getTax())));
		mav.addObject("netValue", df.format(StringUtil.nullToDoubleZero(depositHistory.getNetValue())));
		
		return mav;
	}
	
	@RequestMapping(value = "/gadget/prepaymentMgmt/depositChargeExcelDownloadPopup")
	public ModelAndView downloadDepositChargeExcel() {
		ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/depositChargeExcelDownloadPopup");
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/gadget/prepaymentMgmt/depositChargeExcelMake")
	public ModelAndView depositChargeExcelMake(@RequestParam Integer supplierId,
			String vendor,
			String vendorRole,
			Boolean fromDepositGadget,
			String reportType,
			@RequestParam(value="subType", required=false) String subType,
			String contract,
			String customerName,
			String customerNo,
			String meterId,
			String startDate,
			String endDate,
			String casherId,
			Integer loginIntId,
			Boolean onlyLoginData,
			@RequestParam String filePath,
			String logoImg){
		ModelAndView mav = new ModelAndView("jsonView");
		String prefix = "depositHistoryData";
		StringBuilder fileName = new StringBuilder(prefix);
		StringBuilder splFileName = new StringBuilder();
		Supplier supplier = supplierDao.get(supplierId);
		fileName.append(TimeUtil.getCurrentTimeMilli());
		Language lang = languageDao.get(supplier.getLangId());
		Properties prop = new Properties();
		
		fromDepositGadget = fromDepositGadget == null ? false : true;
		
        if(lang.getName().equals(Locale.KOREAN.toString())){
	        try {
	        	prop.load(getClass().getClassLoader().getResourceAsStream(
	           "message_ko.properties"));
	        } catch (IOException e) {
	         e.printStackTrace();
	        }
        }else{
        	try {
	        	prop.load(getClass().getClassLoader().getResourceAsStream(
	           "message_en.properties"));
	        } catch (IOException e) {
	         e.printStackTrace();
	        }
        }
        
		Boolean isLast = false;
		Long total = 0L;				// data count init
		Long maxRows= 5000L; // 엑셀 하나에 보여줄수 있는 최대 데이터 row 수

		List<String> fileNameList = new ArrayList<String> ();
		
		Map<String, Object> condition = new HashMap<String, Object>();

		// vending station의 operator role이 admin인 경우 조회범위를 전체 vending station으로 한다.  
		// deposit Management 가젯에서 조회되는 경우 vendor정보가 포함될 경우 vendor에 따라 검색되도록 한다.
		if ( !StringUtil.nullToBlank(vendorRole).equals("admin") || fromDepositGadget) {
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
		condition.put("loginIntId", loginIntId);
		condition.put("onlyLoginData",onlyLoginData);
		
		File downDir = new File(filePath);
		
		if ( downDir.exists() ) {
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

		List<Map<String, Object>> depositHistoryList = (List<Map<String, Object>>) depositHistoryDao.getDepositHistoryList(condition).get("list");

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
			splFileName = new StringBuilder(fileName	);
			splFileName.append(".xls");
			excel.writeReportExcel(result, supplier, reportType, isLast, filePath, splFileName.toString(), StringUtil.nullToBlank(supplier.getDescr()), logoImg);
			fileNameList.add(splFileName.toString());
		} else { 
			for ( int i = 0 ; i < total ; i++ ) {
				if( (splCnt * fnum + cnt ) == total || cnt == maxRows ) {
					splFileName = new StringBuilder(fileName);
					splFileName.append('(').append(++fnum).append(").xls");
					result.put("dataList", dataList.subList(idx, (i+1)));
					excel.writeReportExcel(result, supplier, reportType, isLast, filePath, splFileName.toString(), StringUtil.nullToBlank(supplier.getDescr()), logoImg);
					fileNameList.add(splFileName.toString());
					splCnt = cnt;
					cnt = 0;
					idx= (i +1);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        mav.addObject("filePath", filePath);
        mav.addObject("fileName", fileNameList.get(0));
        mav.addObject("fileNames", fileNameList);
		return mav;
	}

    /**
     * method name : depositChargeTotalExcelMake<b/>
     * method Desc :
     *
     * @param supplierId
     * @param vendor
     * @param vendorRole
     * @param fromDepositGadget
     * @param reportType
     * @param contract
     * @param customerName
     * @param customerNo
     * @param meterId
     * @param startDate
     * @param endDate
     * @param filePath
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/prepaymentMgmt/depositChargeTotalExcelMake")
    public ModelAndView depositChargeTotalExcelMake(@RequestParam Integer supplierId,
            String vendor,
            String vendorRole,
            Boolean fromDepositGadget,
            String reportType,
            @RequestParam(value="subType", required=false) String subType,
            String contract,
            String customerName,
            String customerNo,
            String meterId,
            String startDate,
            String endDate,
            String casherId,
			Integer loginIntId,
			Boolean onlyLoginData,
            @RequestParam String filePath,
            String logoImg) {
        ModelAndView mav = new ModelAndView("jsonView");
        String prefix = "depositHistoryTotalData";
        StringBuilder fileName = new StringBuilder(prefix);
        StringBuilder splFileName = new StringBuilder();
        Supplier supplier = supplierDao.get(supplierId);
        fileName.append(TimeUtil.getCurrentTimeMilli());
        Language lang = languageDao.get(supplier.getLangId());
        Properties prop = new Properties();

        fromDepositGadget = fromDepositGadget == null ? false : true;

        if (lang.getName().equals(Locale.KOREAN.toString())) {
            try {
                prop.load(getClass().getClassLoader().getResourceAsStream("message_ko.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                prop.load(getClass().getClassLoader().getResourceAsStream("message_en.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Boolean isTotal = true;

        List<String> fileNameList = new ArrayList<String>();

        Map<String, Object> condition = new HashMap<String, Object>();

        // vending station의 operator role이 admin인 경우 조회범위를 전체 vending station으로 한다.
        // deposit Management 가젯에서 조회되는 경우 vendor정보가 포함될 경우 vendor에 따라 검색되도록 한다.
        if (!StringUtil.nullToBlank(vendorRole).equals("admin") || fromDepositGadget) {
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
		condition.put("loginIntId", loginIntId);
		condition.put("onlyLoginData",onlyLoginData);
        
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
        List<Map<String, Object>> depositHistoryList = (List<Map<String, Object>>) depositHistoryDao.getDepositHistoryList(condition)
                .get("list");

        result.put("startDate", startDate);
        result.put("endDate", endDate);
        List<Map<String, String>> dataList = getExcelData(depositHistoryList, supplier);

        result.put("dataList", dataList);
        splFileName = new StringBuilder(fileName);
        splFileName.append(".xls");
        excel.writeReportExcel(result, supplier, reportType, isTotal, filePath, splFileName.toString(), StringUtil.nullToBlank(supplier.getDescr()), logoImg);
        fileNameList.add(splFileName.toString());

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
        List<Map<String, String>> result = new ArrayList<Map<String,String>>();
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
        if(supplier != null) {
            df = new DecimalFormat(supplier.getCd().getPattern());
        }

        for (Map<String, Object> map: historyList ) {
            Map<String, String> data = new HashMap<String, String>();
            Integer prepaymentLogId = (map.get("prepaymentLogId") == null) ? null : ((Long)map.get("prepaymentLogId")).intValue();
            String changeDate = (String)map.get("changeDate");
            Double chargedCredit = (Double)map.get("chargedCredit");
            Double chargedArrears = (Double)map.get("chargedArrears");
            Integer vendorCasherId = (Integer)map.get("vendorCasherId");
            Integer vendingStationId = (Integer)map.get("vendingStationId");
            Integer contractId = (Integer)map.get("contractId");
            Integer meterId = (Integer)map.get("meterId");
            Integer tariffId = (Integer)map.get("tariffId");
            Integer customerId = (Integer)map.get("customerId");

            String dbDate = changeDate == null ? 
                    CalendarUtil.getCurrentDate() : changeDate.substring(0, 8);
            String date = TimeLocaleUtil.getLocaleDate(dbDate, lang, country);
            data.put("date", date);
 
            if (prepaymentLogId != null) {
                data.put("prepaymentLogId", "SC--" + map.get("prepaymentLogId").toString());
                data.put("paymentType", "cash");
                data.put("chargedCredit", df.format(StringUtil.nullToDoubleZero(chargedCredit)));
                data.put("chargedArrears", df.format(StringUtil.nullToDoubleZero(chargedArrears)));

                if (map.get("isCanceled") == null || !(Boolean)map.get("isCanceled")) {
                    totalChargedCredit += StringUtil.nullToDoubleZero(chargedCredit);
                    totalChargedArrears += StringUtil.nullToDoubleZero(chargedArrears);
                }

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
                        logger.error("contractId: " + contractId);
                        logger.error("meterId: " + meterId);
                        e.printStackTrace();
                    }

                    try {
                        if (tariffId != null) {
                            data.put("tariffName", (String)map.get("tariffName"));
                        }
                    } catch(Exception e) {
                        logger.error("contractId: " + contractId);
                        logger.error("tariffTypeId: " + tariffId);
                        e.printStackTrace();
                    }

                    try {
                        if (customerId != null) {
                            data.put("customerName", (String)map.get("customerName"));
                            data.put("accountNo", (String)map.get("customerNo"));
                            String address = StringUtil.nullToBlank((String)map.get("address")) + 
                            		" " + StringUtil.nullToBlank((String)map.get("address1")) +
                                    " " + StringUtil.nullToBlank((String)map.get("address2"));
                            data.put("address", address);
                        }
                    } catch (Exception e) {
                        logger.error("contractId: " + contractId);
                        logger.error("customerId: " + customerId);
                        e.printStackTrace();
                    }
                }
            } else {
                Double chargeDeposit = StringUtil.nullToDoubleZero((Double)map.get("chargeDeposit"));
                Double commissionRate = StringUtil.nullToDoubleZero(((Float)map.get("commission") == null) ? null : ((Float)map.get("commission")).doubleValue());
                Double commisstion = chargeDeposit * commissionRate * 0.01;
                Double tax = StringUtil.nullToDoubleZero((Double)map.get("tax"));
                Double netValue = StringUtil.nullToDoubleZero((Double)map.get("netValue"));
                Integer historyOpId = (Integer)map.get("historyOpId");
                data.put("depositHistoryId","SC--" + map.get("depositHistoryId").toString());
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
     * ECG 용으로 GEOGODE별로 월정산레포트를 검색한다.
     * 
     * @param searchYear
     * @param searchMonth
     * @param locationId
     * @param supplierId
     * @return
     */
	@SuppressWarnings("unchecked")
    @RequestMapping(value="/gadget/prepaymentMgmt/getMonthlyReportExcelByGeocode")
	public ModelAndView getMonthlyReportExcelByContractNo(@RequestParam("searchYear") String searchYear,
	        @RequestParam("searchMonth") String searchMonth,
	        String geocode,
	        @RequestParam("supplierId") Integer supplierId) {
	    ModelAndView mav = new ModelAndView("jsonView");
	    final String RESIDENTIAL = "Residential";
	    final String NONRESIDENTIAL = "Non Residential";
	    String REPORT_CON_TITLE = "Prepayment Consumption and Government Subsidy Statistics for ";
	    final String REPORT_SAL_TITLE = "Prepayment Sales Report By Tariff Class";
	    final String REGIONAL = "Regional ";
	    final String FILE_NAME = "MonthlyReport";
	    
	    List<String> fileNames = new TreeList();
	    searchMonth = String.format("%02d", Integer.parseInt(searchMonth));
	    String searchDate = searchYear + searchMonth;
	    
	    try {
	        
	        logger.info("supplierId: " + supplierId); 
            logger.info("geocode: " + geocode);

            String locationName = locationDao.getNameByGeocode(geocode,supplierId);
            
            if(locationName == null) {
            	locationName = geocode;
            }
            
            REPORT_CON_TITLE += locationName;
            
            String monthlyConsumptionDate = TimeUtil.getAddedMonth(searchDate+"01", 1).substring(0,6);
            
            //By Section
            String fileName = "(" + searchDate + "_" + locationName + ")" + RESIDENTIAL + FILE_NAME; 
            
            List<PrepaymentLog> residentialLogList = prepaymentLogDao.getMonthlyConsumptionLogByGeocode(monthlyConsumptionDate, RESIDENTIAL, geocode);
            LinkedHashMap<String, Map<String, Object>> data = null;
            if(residentialLogList != null && residentialLogList.size() > 0) {
	            data = MonthlyConsumeData.makeExcelData(residentialLogList, RESIDENTIAL);
	            
	            logger.info("residential log count: "+ residentialLogList.size());
	            MonthlyConsumeExcel.makeMonthlyReportExcel(REPORT_CON_TITLE, data, fileName, searchDate, RESIDENTIAL);
	            logger.info(fileName + "has created");
	            fileNames.add(fileName+".xls");
            }
            fileName = "(" + searchDate + "_" + locationName + ")" + NONRESIDENTIAL + FILE_NAME;
            List<PrepaymentLog> nonResidentialLogList = prepaymentLogDao.getMonthlyConsumptionLogByGeocode(monthlyConsumptionDate, NONRESIDENTIAL, geocode);
            
            if(nonResidentialLogList != null && nonResidentialLogList.size() > 0) {
	            data = MonthlyConsumeData.makeExcelData(nonResidentialLogList , NONRESIDENTIAL);
	            
	            logger.info("nonResidential log count: "+ nonResidentialLogList.size());
	            MonthlyConsumeExcel.makeMonthlyReportExcel(REPORT_CON_TITLE, data, fileName, searchDate, NONRESIDENTIAL);
	            logger.info(fileName + "has created");
	            fileNames.add(fileName+".xls");
            }
            //By Location
            
            if((residentialLogList == null || residentialLogList.size() < 1) &&
            		(nonResidentialLogList == null || nonResidentialLogList.size() < 1)) {
            	return mav.addObject("nonData", true);
            }
            data.clear();
            
            if(residentialLogList != null && residentialLogList.size() > 0) {
            	fileName = "(" + searchDate + "_" + locationName + ")" + REGIONAL + "_" + RESIDENTIAL + FILE_NAME;
	            logger.info("Location: " + locationName);
	            logger.info("Location residential log count: "+ residentialLogList.size());            
	            data.put(locationName, RegionalConsumeData.makeExcelData(residentialLogList, RESIDENTIAL));
	            RegionalConsumeExcel.makeMonthlyReportExcel(REPORT_SAL_TITLE, data, fileName, searchDate, RESIDENTIAL);
	            logger.info(fileName + "has created");
	            fileNames.add(fileName+".xls");
	            data.clear();
            }
            
            if(nonResidentialLogList != null && nonResidentialLogList.size() > 0) {
            	fileName = "(" + searchDate + "_" + locationName + ")" + REGIONAL + "_" + NONRESIDENTIAL + FILE_NAME;
	            logger.info("Location: " + locationName);
	            logger.info("Location non residential log count: "+ nonResidentialLogList.size());            
	            data.put(locationName, RegionalConsumeData.makeExcelData(nonResidentialLogList, NONRESIDENTIAL));
	            
	            RegionalConsumeExcel.makeMonthlyReportExcel(REPORT_SAL_TITLE, data, fileName, searchDate, NONRESIDENTIAL);
	            logger.info(fileName + "has created");
	            fileNames.add(fileName+".xls");
            }
        } catch (ParseException e) {
            logger.error(e,e);
        }
	    
	    mav.addObject("fileNames", StringUtil.joinList(fileNames, ","));
	    mav.addObject("nonData", false);
	    return mav;
	}

	@SuppressWarnings("unchecked")
    @RequestMapping(value="/gadget/prepaymentMgmt/getMonthlyReportExcel")
	public ModelAndView getMonthlyReportExcel(@RequestParam("searchYear") String searchYear,
	        @RequestParam("searchMonth") String searchMonth,
	        Integer locationId,
	        @RequestParam("supplierId") Integer supplierId) {
	    ModelAndView mav = new ModelAndView("jsonView");
	    final String RESIDENTIAL = "Residential";
	    final String NONRESIDENTIAL = "Non Residential";
	    String REPORT_CON_TITLE = "Prepayment Consumption and Government Subsidy Statistics for ";
	    final String REPORT_SAL_TITLE = "Prepayment Sales Report By Tariff Class";
	    final String REGIONAL = "Regional ";
	    final String FILE_NAME = "MonthlyReport";
	    
	    List<String> fileNames = new TreeList();
	    searchMonth = String.format("%02d", Integer.parseInt(searchMonth));
	    String searchDate = searchYear + searchMonth;
	    
	    try {
	        
	        logger.info("supplierId: " + supplierId); 
            logger.info("root location Id: " + locationId);
            
            Location selectedLocation = locationDao.get(locationId);
            List<Integer> subLocation = locationDao.getChildLocationId(locationId);
            String selectedLocationName = selectedLocation.getName();
            REPORT_CON_TITLE += selectedLocationName;
            
            String monthlyConsumptionDate = TimeUtil.getAddedMonth(searchDate+"01", 1).substring(0,6);
            
            //By Section
            String fileName = "(" + searchDate + "_" + selectedLocationName + ")" + RESIDENTIAL + FILE_NAME; 
            
            List<PrepaymentLog> residentialLogList = prepaymentLogDao.getMonthlyConsumptionLog(monthlyConsumptionDate, RESIDENTIAL, subLocation);
            LinkedHashMap<String, Map<String, Object>> data = null;
            if(residentialLogList != null && residentialLogList.size() > 0) {
	            data = MonthlyConsumeData.makeExcelData(residentialLogList, RESIDENTIAL);
	            
	            logger.info("residential log count: "+ residentialLogList.size());
	            MonthlyConsumeExcel.makeMonthlyReportExcel(REPORT_CON_TITLE, data, fileName, searchDate, RESIDENTIAL);
	            logger.info(fileName + "has created");
	            fileNames.add(fileName+".xls");
            }
            fileName = "(" + searchDate + "_" + selectedLocationName + ")" + NONRESIDENTIAL + FILE_NAME;
            List<PrepaymentLog> nonResidentialLogList = prepaymentLogDao.getMonthlyConsumptionLog(monthlyConsumptionDate, NONRESIDENTIAL, subLocation);
            if(nonResidentialLogList != null && nonResidentialLogList.size() > 0) {
	            data = MonthlyConsumeData.makeExcelData(nonResidentialLogList , NONRESIDENTIAL);
	            
	            logger.info("nonResidential log count: "+ nonResidentialLogList.size());
	            MonthlyConsumeExcel.makeMonthlyReportExcel(REPORT_CON_TITLE, data, fileName, searchDate, NONRESIDENTIAL);
	            logger.info(fileName + "has created");
	            fileNames.add(fileName+".xls");
            }
            //By Location
            fileName = "(" + searchDate + "_" + selectedLocationName + ")" + REGIONAL + "_" + RESIDENTIAL + FILE_NAME;
            if((residentialLogList == null || residentialLogList.size() < 1) &&
            		(nonResidentialLogList == null || nonResidentialLogList.size() < 1)) {
            	return mav.addObject("nonData", true);
            }
            data.clear();

			if(residentialLogList != null && residentialLogList.size() > 0) {            
	            logger.info("Location: " + selectedLocationName);
	            logger.info("Location residential log count: "+ residentialLogList.size());            
	            data.put(selectedLocationName, RegionalConsumeData.makeExcelData(residentialLogList, RESIDENTIAL));
	            RegionalConsumeExcel.makeMonthlyReportExcel(REPORT_SAL_TITLE, data, fileName, searchDate, RESIDENTIAL);
	            logger.info(fileName + "has created");
	            fileNames.add(fileName+".xls");
	            data.clear();
            }
            
            if(nonResidentialLogList != null && nonResidentialLogList.size() > 0) {
            	fileName = "(" + searchDate + "_" + selectedLocationName + ")" + REGIONAL + "_" + NONRESIDENTIAL + FILE_NAME;
	            logger.info("Location: " + selectedLocationName);
	            logger.info("Location non residential log count: "+ nonResidentialLogList.size());            
	            data.put(selectedLocationName, RegionalConsumeData.makeExcelData(nonResidentialLogList, NONRESIDENTIAL));
	            
	            RegionalConsumeExcel.makeMonthlyReportExcel(REPORT_SAL_TITLE, data, fileName, searchDate, NONRESIDENTIAL);
	            logger.info(fileName + "has created");
	            fileNames.add(fileName+".xls");
            }
        } catch (ParseException e) {
            logger.error(e,e);
        }
	    
	    mav.addObject("fileNames", StringUtil.joinList(fileNames, ","));
	    mav.addObject("nonData", false);
	    return mav;
	}
	
	@RequestMapping(value="/gadget/prepaymentMgmt/getDisplayLocation")
	public ModelAndView getDisplayLocation(@RequestParam("supplierId") Integer supplierId) {
	    ModelAndView mav = new ModelAndView("jsonView");
	    
	    List<Location> returnLocation = new ArrayList<Location>();
	    List<Location> rootList = locationDao.getParents(supplierId);
	    Set<Location> childrenLocations = new HashSet<Location>();
	    
	    try {
	    	//여러 위치를 지정할 경우 deposit.location.info는  "1,2,Region/0,1,Oda" 이런식으로 /(슬래시)로 구분된다.
    	    //startLevel,endLevel,startCode/startLevel,endLevel,startCode  이런 형태로 입력한다.
	        //startLevel : location을 가져올 Level Step을 의미(숫자), endLevel : location을 가져올 마지막 Level Step을 의미(숫자), 
	        //startCode : startLevel의 location 이름 또는 이름의 일부를 입력(startCode가 있는 레벨의 하위 location만 가지고 온다.)
	        //endLevel을 지정하지 않을 경우 마지막 하위 Location까지를 검색된다.
	        //root location의 레벨을 0으로 정한다.
            String locationInfo = CommandProperty.getProperty("deposit.location.info");
            
            if(locationInfo == null || "".equals(locationInfo))
                return mav.addObject("locations", rootList);
            
            String[] locationInfoArr = locationInfo.split("/");
            for (int i = 0; i < locationInfoArr.length; i++) {
                String[] locationInfoSubArr = locationInfoArr[i].split(",");
                
                Integer startLevel = locationInfoSubArr[0] == null || "".equals(locationInfoSubArr[0]) ? 0 : Integer.parseInt(locationInfoSubArr[0]);
                Integer endLevel = locationInfoSubArr[1] == null || "".equals(locationInfoSubArr[1]) ? null : Integer.parseInt(locationInfoSubArr[1]);
                String startLocationCode = locationInfoSubArr[2];
                
                for (Location location : rootList) {
                    childrenLocations = location.getChildren();
                    for (int j = 0; j <= startLevel; j++) {
                        if(j == startLevel) {
                            int currLevel = j;
                            if(childrenLocations == null) {
                                childrenLocations = new HashSet<Location>();
                                childrenLocations.add(location);
                                returnLocation.addAll(childrenLocations);
                            } else if(startLevel == 0) {
                                childrenLocations = new HashSet<Location>();
                                childrenLocations.add(location);
                                returnLocation.addAll(childrenLocation(childrenLocations, startLevel, endLevel, currLevel, startLocationCode));
                            } else {
                                returnLocation.addAll(childrenLocation(childrenLocations, startLevel, endLevel, currLevel, startLocationCode));
                            }
                        }
                    }
                }
                
            }
	    } catch(Exception e) {
	        logger.error(e,e);
            return mav.addObject("locations", rootList);
	    }

	    if(returnLocation.size() == 0) {
	        returnLocation = rootList;
	    }
	    mav.addObject("locations", returnLocation);
	    
	    return mav;
	}
	
	/**
     * 
     * method name : childrenLocation<b/>
     * method Desc : 원하는 레벨의 Location을 검색해온다.
     *
     * @param locationSet
     * @param startLevel
     * @param endLevel
     * @param currLevel
     * @param startLocationCode
     * @return
     */
    private Set<Location> childrenLocation(Set<Location> locationSet, Integer startLevel, Integer endLevel, int currLevel, String startLocationCode) {
        
        Set<Location> returnChildren = new HashSet<Location>();
         
         if(endLevel == null || endLevel != currLevel) {
             for (Location subLocation : locationSet) {
                 
                 if(startLevel == currLevel && !subLocation.getName().contains(startLocationCode)) {
                     continue;
                 }
                 
                 if(subLocation.getChildren() == null){
                     returnChildren.add(subLocation);
                     return returnChildren;
                 } else {
                     subLocation.setChildren(childrenLocation(subLocation.getChildren(), startLevel, endLevel, currLevel+1, startLocationCode));
                     returnChildren.add(subLocation);
                 }
             }
         } else {
             for (Location subLocation : locationSet) {
                 if(startLevel == currLevel && !subLocation.getName().contains(startLocationCode)) {
                     continue;
                 }
                 
                 subLocation.setChildren(null);
                 returnChildren.add(subLocation);
             }
             return returnChildren;
         }
         
         return returnChildren;
     }

}
