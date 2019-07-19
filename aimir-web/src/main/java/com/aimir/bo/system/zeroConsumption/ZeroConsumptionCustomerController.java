package com.aimir.bo.system.zeroConsumption;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.prepayment.PrepaymentMgmtOperatorManager;
import com.aimir.service.system.zeroConsumption.ZeroConsumptionCustomerManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZeroConsumptionCustomerMakeExcel;
import com.aimir.util.ZipUtils;

@Controller
public class ZeroConsumptionCustomerController {

	protected static Log logger = LogFactory.getLog(ZeroConsumptionCustomerController.class);

	@Autowired
	CodeManager codeManager;

	@Autowired
	RoleManager roleManager;

	@Autowired
	ZeroConsumptionCustomerManager zcManager;

	@Autowired
	PrepaymentMgmtOperatorManager prepaymentMgmtOperatorManager;

    @Autowired
    SupplierManager supplierManager;

	@RequestMapping(value = "/gadget/system/zeroConsumptionMinGadget")
	public ModelAndView getZeroConsumptionMinGadget() {
		ModelAndView mav = new ModelAndView("/gadget/system/zeroConsumptionMin");

		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();
		Supplier supplier = user.getRoleData().getSupplier();
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		Integer supplierId = user.getRoleData().getSupplier().getId();

		try {
			String currentDate = TimeUtil.getCurrentDay();
			String formatDate = TimeLocaleUtil.getLocaleDate(currentDate.substring(0, 8), lang, country);
			mav.addObject("currentDate", currentDate);
			mav.addObject("formatDate", formatDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		mav.addObject("supplierId", supplierId);
		return mav;
	}

    /**
     * method name : getZeroConsumChartData
     * method Desc : 검색 조건에 따른 계약 목록의 차트
     *
     * @return
     */
    @RequestMapping(value = "/gadget/system/getZeroConsumChartData")
    public ModelAndView getZeroConsumChartData(
    		@RequestParam("supplierId") Integer supplierId
			, @RequestParam("searchDateType") String searchDateType
			, @RequestParam("searchStartDate") String searchStartDate
			, @RequestParam("searchEndDate") String searchEndDate
			, @RequestParam("searchWeek") String searchWeek) {
        ModelAndView mav = new ModelAndView("jsonView");

		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("supplierId", supplierId);

		if (StringUtil.nullToBlank(searchDateType).isEmpty() || StringUtil.nullToBlank(searchStartDate).isEmpty() || StringUtil.nullToBlank(searchEndDate).isEmpty()) {
			try {
				searchDateType = DateType.PERIOD.getCode(); // default 일자조건탭
				searchStartDate = TimeUtil.getCurrentDay();
				searchEndDate = TimeUtil.getCurrentDay();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		conditionMap.put("searchDateType", searchDateType);
		conditionMap.put("searchStartDate", searchStartDate);
		conditionMap.put("searchEndDate", searchEndDate);
		conditionMap.put("searchWeek", searchWeek);

		DateType dateType = null;

		for (DateType obj : DateType.values()) {
			if (obj.getCode().equals(conditionMap.get("searchDateType"))) {
				dateType = obj;
				break;
			}
		}

		switch (dateType) {
		case PERIOD:

			break;
		case WEEKLY:
//			try {
//				// 조회시작일자 이전일자
//				String prevDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);
//
//				// 이전일자 주차 구하기
//				int weekNum = CalendarUtil.getWeekOfMonth(prevDate);
//
//				// 주차에 해당하는 from to
//				Map<String, String> prevWeek = CalendarUtil.getDateWeekOfMonth(prevDate.substring(0, 4), prevDate.substring(4, 6), weekNum + "");
//
//				conditionMap.put("searchStartDate", prevWeek.get("startDate"));
//				conditionMap.put("searchEndDate", prevWeek.get("endDate"));
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
			break;
		case MONTHLY:
			conditionMap.put("searchStartDate", searchStartDate.substring(0, 6) + "01");
			conditionMap.put("searchEndDate", searchEndDate.substring(0, 6) + "31");
			break;
		case SEASONAL:

			break;
		default:
			break;
		}

		logger.debug("### 시작일 = " + conditionMap.get("searchStartDate"));
		logger.debug("### 종료일 = " + conditionMap.get("searchEndDate"));




        Map<String, Object> result = zcManager.getZeroConsumChartData(conditionMap);
        mav.addObject("chartDatas", result);

        return mav;
    }

	@RequestMapping(value = "/gadget/system/zeroConsumptionMaxGadget")
	public ModelAndView getZeroConsumptionMaxGadget() {
		ModelAndView mav = new ModelAndView("/gadget/system/zeroConsumptionMax");

		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();
		Supplier supplier = user.getRoleData().getSupplier();
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		int operatorId = (int) user.getAccountId();
		Integer supplierId = user.getRoleData().getSupplier().getId();

		try {
			String currentDate = TimeUtil.getCurrentDay();
			String formatDate = TimeLocaleUtil.getLocaleDate(currentDate.substring(0, 8), lang, country);
			mav.addObject("currentDate", currentDate);
			mav.addObject("formatDate", formatDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		mav.addObject("operatorId", operatorId);
		mav.addObject("supplierId", supplierId);
		mav.addObject("loginId", user.getLoginId());
		return mav;
	}

	/**
	 * method name : getZeroConsumptionCustomerContracList method Desc : 검색조건에
	 * 따른 0(zero) Consumption 고객 리스트를 조회한다.
	 *
	 * @return
	 */
	@RequestMapping(value = "/gadget/system/getZeroConsumptionCustomerContracList")
	public ModelAndView getZeroConsumptionCustomerContracList(
			  @RequestParam("supplierId") Integer supplierId
			, @RequestParam("searchDateType") String searchDateType
			, @RequestParam("searchStartDate") String searchStartDate
			, @RequestParam("searchEndDate") String searchEndDate
			, @RequestParam("searchWeek") String searchWeek) {

		ModelAndView mav = new ModelAndView("jsonView");
		HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
		int page = Integer.parseInt(request.getParameter("page"));
		int limit = Integer.parseInt(request.getParameter("limit"));

		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("page", page);
		conditionMap.put("limit", limit);
		conditionMap.put("supplierId", supplierId);

		if (StringUtil.nullToBlank(searchDateType).isEmpty() || StringUtil.nullToBlank(searchStartDate).isEmpty() || StringUtil.nullToBlank(searchEndDate).isEmpty()) {
			try {
				searchDateType = DateType.PERIOD.getCode(); // default 일자조건탭
				searchStartDate = TimeUtil.getCurrentDay();
				searchEndDate = TimeUtil.getCurrentDay();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		conditionMap.put("searchDateType", searchDateType);
		conditionMap.put("searchStartDate", searchStartDate);
		conditionMap.put("searchEndDate", searchEndDate);
		conditionMap.put("searchWeek", searchWeek);

		List<String> contractList = getZeroConsumptionCustomerContractList(conditionMap);

		if (contractList != null && 0 < contractList.size()) {
			conditionMap.put("contractNumberList", contractList);

			List<Map<String, Object>> result1 = prepaymentMgmtOperatorManager.getPrepaymentContractList(conditionMap);
			mav.addObject("result", result1);
			mav.addObject("totalCount", prepaymentMgmtOperatorManager.getPrepaymentContractListTotalCount(conditionMap));
		} else {
			mav.addObject("result", new ArrayList<Map<String, Object>>());
			mav.addObject("totalCount", 0);
		}

		return mav;
	}



	@RequestMapping(value = "/gadget/system/getZeroConsumptionCustomerExcelDownloadPopup")
	public ModelAndView getZeroConsumptionCustomerExcelDownloadPopup() {
		ModelAndView mav = new ModelAndView("/gadget/system/zeroConsumptionExcelDownPopup");
		return mav;
	}

	@RequestMapping(value = "gadget/system/zeroConsumptionExcelMake")
	public ModelAndView zeroConsumptionExcelMake(
			  @RequestParam("condition[]") String[] condition
			, @RequestParam("fmtMessage[]") String[] fmtMessage
			, @RequestParam("filePath") String filePath) {

		ModelAndView mav = new ModelAndView("jsonView");

		Map<String, Object> conditionMap = new HashMap<String, Object>();
		Map<String, String> msgMap = new HashMap<String, String>();

		int total = 0; // 데이터 조회건수

		Supplier supplier = supplierManager.getSupplier(Integer.parseInt(condition[0]));
		conditionMap.put("supplierId", supplier.getId());
		conditionMap.put("searchDateType", condition[1]);
		conditionMap.put("searchStartDate", condition[2]);
		conditionMap.put("searchEndDate", condition[3]);
		conditionMap.put("weeklyWeekCombo", condition[4]);
		conditionMap.put("page", 1);
		conditionMap.put("limit", 50000);

		// message 생성
		msgMap.put("contractNo", fmtMessage[0]);
		msgMap.put("customerName", fmtMessage[1]);
		msgMap.put("lastChargeDate", fmtMessage[2]);
		msgMap.put("remainingCredit", fmtMessage[3]);
		msgMap.put("meterId", fmtMessage[4]);
		msgMap.put("supplyType", fmtMessage[5]);
		msgMap.put("tariffType", fmtMessage[6]);
		msgMap.put("supplyStatus", fmtMessage[7]);
		msgMap.put("validDate", fmtMessage[8]);
		msgMap.put("title", fmtMessage[9]);

		List<String> contractList = getZeroConsumptionCustomerContractList(conditionMap);
		List<Map<String, Object>> result = null;

		if (contractList != null && 0 < contractList.size()) {
			conditionMap.put("contractNumberList", contractList);
			result = prepaymentMgmtOperatorManager.getPrepaymentContractList(conditionMap);
		} else {
			result = new ArrayList<Map<String, Object>>();
		}

		total = result.size();

		if (0 < total) {
			try {
				mav.addObject("total", total);

				StringBuilder sbFileName = new StringBuilder();
				StringBuilder sbSplFileName = new StringBuilder();
				List<String> fileNameList = new ArrayList<String>();
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				int maxRows = 5000; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

				sbFileName.append(fmtMessage[9] + "_");
				sbFileName.append(TimeUtil.getCurrentTimeMilli());

				File downDir = new File(filePath);

				if (downDir.exists()) {
					File[] files = downDir.listFiles();

					if (files != null) {
						String deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10);

						for (File file : files) {
							String filename = file.getName();
							// 확장자 : xls|zip
							if (filename.endsWith("xls") || filename.endsWith("zip")) {
								// 10일 지난 파일들 삭제
								if (filename.startsWith(fmtMessage[9] + "_") && filename.substring(filename.length() - 18, filename.length() - 10).compareTo(deleteDate) < 0) {
									logger.debug("File Delete for last 10days ==> " + filename);
									file.delete();
								}
							}
						}
					}
				} else {
					downDir.mkdir();
				}

				// create excel file
				ZeroConsumptionCustomerMakeExcel wExcel = new ZeroConsumptionCustomerMakeExcel();
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
							wExcel.writeReportExcel(list, msgMap, filePath, sbSplFileName.toString(), supplier);
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
				logger.debug(e,e);
			} catch (Exception e) {
				logger.debug(e,e);
			}
		}

		return mav;
	}

	private List<String> getZeroConsumptionCustomerContractList(Map<String, Object> conditionMap) {
		List<String> list = new ArrayList<String>();
		String searchStartDate = (String) conditionMap.get("searchStartDate");
		String searchEndDate = (String) conditionMap.get("searchEndDate");

		DateType dateType = null;

		for (DateType obj : DateType.values()) {
			if (obj.getCode().equals(conditionMap.get("searchDateType"))) {
				dateType = obj;
				break;
			}
		}

		switch (dateType) {
		case PERIOD:

			break;
		case WEEKLY:
//			try {
//				// 조회시작일자 이전일자
//				String prevDate = TimeUtil.getPreDay(searchStartDate).substring(0, 8);
//
//				// 이전일자 주차 구하기
//				int weekNum = CalendarUtil.getWeekOfMonth(prevDate);
//
//				// 주차에 해당하는 from to
//				Map<String, String> prevWeek = CalendarUtil.getDateWeekOfMonth(prevDate.substring(0, 4), prevDate.substring(4, 6), weekNum + "");
//
//				conditionMap.put("searchStartDate", prevWeek.get("startDate"));
//				conditionMap.put("searchEndDate", prevWeek.get("endDate"));
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
			break;
		case MONTHLY:
			conditionMap.put("searchStartDate", searchStartDate.substring(0, 6) + "01");
			conditionMap.put("searchEndDate", searchEndDate.substring(0, 6) + "31");
			break;
		case SEASONAL:

			break;
		default:
			break;
		}

		logger.debug("### 시작일 = " + conditionMap.get("searchStartDate"));
		logger.debug("### 종료일 = " + conditionMap.get("searchEndDate"));

		List<Object> contractList = zcManager.getZeroConsumptionContractData(conditionMap);

		for (int i = 0; i < contractList.size(); i++) {
			list.add(((List<String>) contractList.get(i)).get(0));

			logger.debug("### 계약명 ==> " + list.get(i));
		}

		return list;
	}
}