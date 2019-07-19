package com.aimir.bo.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.service.mvm.BillingManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.MeteringDataReportExcel;
import com.aimir.util.MeteringDataReportListExcel;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

@Controller
public class MvmReportController {
	
	@Autowired
	BillingManager billingManager;
	
	@RequestMapping(value="/gadget/mvm/mvmReportMiniGadget")
	public ModelAndView mvmReportMiniGadget() {		
		ModelAndView mav = new ModelAndView("/gadget/mvm/mvmReportMiniGadget");
		return mav;
	}

	@RequestMapping(value="/gadget/mvm/mvmReportMaxGadget")
	public ModelAndView mvmReportMaxGadget() {	
		
		ModelAndView mav = new ModelAndView("/gadget/mvm/mvmReportMaxGadget");
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
		return mav;
	}
	
    @RequestMapping(value="/gadget/mvm/mvmReportDownloadPopup")
    public ModelAndView mvmReportDownloadPopup() {      
        ModelAndView mav = new ModelAndView("/gadget/mvm/mvmReportDownloadPopup");
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/getMeteringDataCount")
    public ModelAndView getMeteringDataCount(
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("locationId") String locationId,
            @RequestParam("tariffIndexId") String tariffIndexId,
            @RequestParam("reportType") String reportType,
            @RequestParam("supplierId") String supplierId) {
        
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("searchDateType", searchDateType);
        conditionMap.put("locationId", locationId);
        conditionMap.put("tariffIndexId", tariffIndexId);
        conditionMap.put("reportType", reportType);
        conditionMap.put("supplierId", supplierId);
        
        ModelAndView mav = new ModelAndView("jsonView");        
        mav.addObject("result", billingManager.getMeteringDataCount(conditionMap) );
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/getMeteringData")
    public ModelAndView getMeteringData(
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("locationId") String locationId,
            @RequestParam("tariffIndexId") String tariffIndexId,
            @RequestParam("customerName") String customerName,
            @RequestParam("contractNo") String contractNo,
            @RequestParam("meterId") String meterId,
            @RequestParam("reportType") String reportType,
            @RequestParam("page") String page,
            @RequestParam("pageSize") String pageSize,
            @RequestParam("supplierId") String supplierId) {
        
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("searchDateType", searchDateType);
        conditionMap.put("locationId", locationId);
        conditionMap.put("tariffIndexId", tariffIndexId);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("customerName", customerName);
        conditionMap.put("contractNo", contractNo);
        conditionMap.put("reportType", reportType);
        conditionMap.put("meterId", meterId);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", pageSize);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("gridDatas", billingManager.getMeteringData(conditionMap));
        mav.addObject("total", billingManager.getMeteringDataCount(conditionMap));
        return mav;
    }
 
    @RequestMapping(value="/gadget/mvm/getMeteringDataReport")
    public ModelAndView getMeteringDataReport(
    		@RequestParam("condition[]") String[] condition,
			@RequestParam("fmtMessage[]") String[] fmtMessage,
			@RequestParam("excelType") String excelType,
			@RequestParam("filePath") String filePath) {
        
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        Map<String, String> msgMap = new HashMap<String, String>();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        boolean isLast = false;
        Long total = 0L;        // 데이터 조회건수
        Long maxRows = 5000L;   // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수
        Long rangeRows = 0L;    // excel 파일을 분할해서 보여줄 데이터 건수

        String dayPrefix = null;
        String monthPrefix = null;
        String currentPrefix = null;
        
        if("details".equals(excelType)) {
        	dayPrefix = "billingday(Details)";
            monthPrefix = "billingmonth(Details)";
            currentPrefix = "billingcurrent(Details)";
        } else {
        	dayPrefix = "billingday";
            monthPrefix = "billingmonth";
            currentPrefix = "billingcurrent";
        }
        
        ModelAndView mav = new ModelAndView("jsonView");

        List<Map<String, Object>> result = null;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<String> fileNameList = new ArrayList<String>();
        
        String searchStartDate 	= condition[0];
        String searchDateType 	= condition[2];
        String reportType 	 	= condition[5];
        String outputRange		= condition[10];
        String lastData 		= condition[9];

        try {
        	if("details".equals(excelType)) {
        		conditionMap.put("searchStartDate", searchStartDate);
                conditionMap.put("searchEndDate", 	condition[1]);
                conditionMap.put("searchDateType", 	searchDateType);
                conditionMap.put("locationId", 		condition[3]);
                conditionMap.put("tariffIndexId",	condition[4]);
                conditionMap.put("reportType", 		reportType);
                conditionMap.put("customerName", 	condition[6]);
                conditionMap.put("contractNo", 		condition[7]);
                conditionMap.put("meterId", 		condition[8]);
                conditionMap.put("lastData", 		lastData);
                conditionMap.put("outputRange", 	outputRange);
                conditionMap.put("supplierId", 		condition[11]);
        	} else {
        		conditionMap.put("searchStartDate", searchStartDate);
                conditionMap.put("searchEndDate", 	condition[1]);
                conditionMap.put("searchDateType", 	searchDateType);
                conditionMap.put("locationId", 		condition[3]);
                conditionMap.put("tariffIndexId",	condition[4]);
                conditionMap.put("reportType", 		reportType);
                conditionMap.put("customerName", 	condition[6]);
                conditionMap.put("contractNo", 		condition[7]);
                conditionMap.put("meterId", 		condition[8]);
                conditionMap.put("page", 			condition[9]);
                conditionMap.put("pageSize", 		condition[10]);
                conditionMap.put("supplierId", 		condition[11]);
        	}
            

            if ((DateType.DAILY.getCode()).equals(searchDateType)) {
                // 전일
//                conditionMap.put("lastStartDate", TimeUtil.getPreDay(searchStartDate + "000000"));
                String preStartDateTime = TimeUtil.getPreDay(searchStartDate + "000000");
                String preStartDate = preStartDateTime.substring(0, 8);
                conditionMap.put("lastStartDate", preStartDate);
            } else if ((DateType.MONTHLY.getCode()).equals(searchDateType)) { // 월간
                // 전월
//                conditionMap.put("lastStartDate", TimeUtil.getPreMonth(searchStartDate + "000000"));
                String preStartDateTime = TimeUtil.getPreMonth(searchStartDate + "000000");
                String preStartDate = preStartDateTime.substring(0, 8);
                conditionMap.put("lastStartDate", preStartDate);

                String preYear = preStartDate.substring(0,4);
                String preMonth = preStartDate.substring(4,6);
                String preDay = CalendarUtil.getMonthLastDate(preYear, preMonth);
                conditionMap.put("lastEndDate", preYear + preMonth + preDay);
            }

            if("details".equals(excelType)) {
            	result = billingManager.getMeteringDataReport(conditionMap);
            } else {
            	result = billingManager.getMeteringData(conditionMap);
            }
   /////////////////////////////////////////////////////////////////////////////////////////////         
            total = new Integer(result.size()).longValue();

            mav.addObject("total", total);
            if (total <= 0) {
                return mav;
            }

            if (StringUtil.nullToBlank(reportType).equals("daily")) { // 일간
                sbFileName.append(dayPrefix);
                msgMap.put("title", fmtMessage[1] + " " + fmtMessage[2]);
            } else if (StringUtil.nullToBlank(reportType).equals("monthly")) { // 월간
                sbFileName.append(monthPrefix);
                msgMap.put("title", fmtMessage[1] + " " + fmtMessage[3]);
            } else if (StringUtil.nullToBlank(reportType).equals("current")) { // Current
                sbFileName.append(currentPrefix);
                msgMap.put("title", fmtMessage[1] + " " + fmtMessage[4]);
            }

            sbFileName.append(TimeUtil.getCurrentTimeMilli());

            if (StringUtil.nullToBlank(lastData).length() > 0) {
                isLast = true;
            }

            if (StringUtil.nullToBlank(outputRange).length() > 0) {
                rangeRows = Long.valueOf(outputRange);
            }

         //message 생성
            if("details".equals(excelType)) {
	            msgMap.put("msgLastDay", 			fmtMessage[0]);		
	            msgMap.put("msgSearchDate", 		fmtMessage[1]);
	            msgMap.put("msgDate", 				fmtMessage[2]);
	            msgMap.put("msgCustomerName", 		fmtMessage[3]);
	            msgMap.put("msgConstractNo", 		fmtMessage[4]);
	            msgMap.put("msgMeterId", 			fmtMessage[5]);
	            msgMap.put("msgContractDemand", 	fmtMessage[6]);
	            msgMap.put("msgTariffType", 		fmtMessage[7]);
	            msgMap.put("msgLocation", 			fmtMessage[8]);
	            msgMap.put("msgActImp", 			fmtMessage[9]);
	            msgMap.put("msgActExp", 			fmtMessage[10]);
	            msgMap.put("msgRactLagImp", 		fmtMessage[11]);
	            msgMap.put("msgRactLeadImp",		fmtMessage[12]);
	            msgMap.put("msgRactLagExp", 		fmtMessage[13]);
	            msgMap.put("msgRactLeadExp", 		fmtMessage[14]);
	            msgMap.put("msgTotEnergy", 			fmtMessage[15]);
	            msgMap.put("msgEnergy",				fmtMessage[16]);
	            msgMap.put("msgTotDemandTime", 		fmtMessage[17]);
	            msgMap.put("msgMaxDemandTime",		fmtMessage[18]);
	            msgMap.put("msgTotCummDemand",		fmtMessage[19]);
	            msgMap.put("msgCummDemand", 		fmtMessage[20]);
	            msgMap.put("msgRate1", 				fmtMessage[21]);
	            msgMap.put("msgRate2",				fmtMessage[22]);
	            msgMap.put("msgRate3", 				fmtMessage[23]);
	            msgMap.put("msgNo", 				fmtMessage[24]);
	            msgMap.put("msgkVAh1", 				fmtMessage[25]);
            } else {
	            msgMap.put("msgNo",			 		fmtMessage[5]);
	            msgMap.put("msgReadingDay",			fmtMessage[6]);
	            msgMap.put("msgCustomerName", 		fmtMessage[7]);
	            msgMap.put("msgContractNo", 		fmtMessage[8]);
	            msgMap.put("msgMeterId", 			fmtMessage[9]);
	            msgMap.put("msgTotEnergyUsage", 	fmtMessage[10]);
	            msgMap.put("msgContractDemand", 	fmtMessage[11]);
	            msgMap.put("msgPowerConsumption",	fmtMessage[12]);
	            msgMap.put("msgTotKvah", 			fmtMessage[13]);
                msgMap.put("msgMaxDmdKvahTimeRate1", fmtMessage[23]);
                msgMap.put("msgMaxDmdKvahTimeRate2", fmtMessage[24]);
                msgMap.put("msgMaxDmdKvahTimeRate3", fmtMessage[25]);
	            msgMap.put("msgMaxDmdKvahTime",     fmtMessage[14]);
                msgMap.put("msgMaxDmdKvahRate1",    fmtMessage[20]);
                msgMap.put("msgMaxDmdKvahRate2",    fmtMessage[21]);
                msgMap.put("msgMaxDmdKvahRate3",    fmtMessage[22]);
                msgMap.put("msgMaxDmdKvah",         fmtMessage[15]);
	            msgMap.put("msgPhaseA", 			fmtMessage[16]);
	            msgMap.put("msgPhaseB", 			fmtMessage[17]);
	            msgMap.put("msgPhaseC", 			fmtMessage[18]);
            }
            
            // check download dir
            File downDir = new File(filePath);

            if (downDir.exists()) {
                File[] files = downDir.listFiles();

                if (files != null) {
                    String filename = null;
                    String deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10); // 10일 이전 일자
                    boolean isDel = false;
//                    System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//                    System.out.println("deleteDate : " + deleteDate);
//                    System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

                    for (File file : files) {
//                        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//                        System.out.println("exist file : " + filename);
//                        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

                        filename = file.getName();
                        isDel = false;

                        // 파일길이 : 22이상, 확장자 : xls|zip
                        if (filename.length() > 22 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
                            // 10일 지난 파일들 삭제
                            if (filename.startsWith(dayPrefix) && filename.substring(10, 18).compareTo(deleteDate) < 0) {
                                isDel = true;
                            } else if (filename.startsWith(monthPrefix) && filename.substring(12, 20).compareTo(deleteDate) < 0) {
                                isDel = true;
                            } else if (filename.startsWith(currentPrefix) && filename.substring(14, 22).compareTo(deleteDate) < 0) {
                                isDel = true;
                            }

                            if (isDel) {
//                                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//                                System.out.println("deleted file : " + filename);
//                                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
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
            MeteringDataReportExcel wExcel = new MeteringDataReportExcel();
            MeteringDataReportListExcel wListExcel = new MeteringDataReportListExcel();
            int cnt = 1;
            int idx = 0;
            int fnum = 0;
            int splCnt = 0;

            if ((rangeRows == 0 || total <= rangeRows) && total <= maxRows) {
                sbSplFileName = new StringBuilder();
                sbSplFileName.append(sbFileName);
                sbSplFileName.append(".xls");
                if("details".equals(excelType)) {
                	wExcel.writeReportExcel(result, msgMap, isLast, filePath, sbSplFileName.toString());
                } else {
                	wListExcel.writeReportExcel(result, msgMap, isLast, filePath, sbSplFileName.toString());
                }
                
                fileNameList.add(sbSplFileName.toString());
            } else {
                for (int i = 0; i < total; i++) {
                    if (cnt == rangeRows || (splCnt * fnum + cnt) == total || cnt == maxRows) {
                        sbSplFileName = new StringBuilder();
                        sbSplFileName.append(sbFileName);
                        sbSplFileName.append('(').append(++fnum).append(").xls");

                        list = result.subList(idx, (i + 1));
                        
                        if("details".equals(excelType)) {
                        	wExcel.writeReportExcel(list, msgMap, isLast, filePath, sbSplFileName.toString());
                        } else {
                        	wListExcel.writeReportExcel(list, msgMap, isLast, filePath, sbSplFileName.toString());
                        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;
    }

    @RequestMapping(value="/gadget/mvm/getMeteringDetailData")
    public ModelAndView getMeteringDetailData(
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("reportType") String reportType,
            @RequestParam("lastData") String lastData,
            @RequestParam("mdevType") String mdevType,
            @RequestParam("mdevId") String mdevId,
            @RequestParam("detailContractId") String detailContractId,
            @RequestParam("detailMeterId") String detailMeterId,
            @RequestParam("hhmmss") String hhmmss,
            @RequestParam("supplierId")        String supplierId) {
    	
    	if(detailContractId.equals("null") || detailContractId.length() == 0)	detailContractId=null;
    	
        List<Map<String, Object>> result = null;
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        ModelAndView mav = new ModelAndView("jsonView");

        try {
            conditionMap.put("searchStartDate", searchStartDate);
            conditionMap.put("searchEndDate", searchEndDate);
            conditionMap.put("searchDateType", searchDateType);
            conditionMap.put("reportType", reportType);
            conditionMap.put("lastData", lastData);
            conditionMap.put("supplierId", supplierId);
            conditionMap.put("hhmmss", hhmmss);
            conditionMap.put("mdevType", mdevType);
            conditionMap.put("mdevId", mdevId);
            conditionMap.put("detailContractId", detailContractId);
            conditionMap.put("detailMeterId", detailMeterId);

            if ((DateType.DAILY.getCode()).equals(searchDateType)) {
                // 전일
                String preStartDateTime = TimeUtil.getPreDay(searchStartDate + "000000");
                String preStartDate = preStartDateTime.substring(0, 8);
                conditionMap.put("lastStartDate", preStartDate);
            } else if ((DateType.MONTHLY.getCode()).equals(searchDateType)) { // 월간
                // 전월
                String preStartDateTime = TimeUtil.getPreMonth(searchStartDate + "000000");
                String preStartDate = preStartDateTime.substring(0, 6) + "01";
                conditionMap.put("lastStartDate", preStartDate);

                String preYear = preStartDate.substring(0,4);
                String preMonth = preStartDate.substring(4,6);
                String preDay = CalendarUtil.getMonthLastDate(preYear, preMonth);
                conditionMap.put("lastEndDate", preYear + preMonth + preDay);
            }

            result = billingManager.getMeteringDetailData(conditionMap);

            mav.addObject("result", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;
    }

    @RequestMapping(value="/gadget/mvm/getMeteringDetailUsageData")
    public ModelAndView getMeteringDetailUsageData(
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("locationId") String locationId,
            @RequestParam("tariffIndexId") String tariffIndexId,
            @RequestParam("reportType") String reportType,
            @RequestParam("customerName") String customerName,
            @RequestParam("contractNo") String contractNo,
            @RequestParam("meterId") String meterId,
            @RequestParam("lastData") String lastData,
            @RequestParam("mdevType") String mdevType,
            @RequestParam("mdevId") String mdevId,
            @RequestParam("detailContractId") String detailContractId,
            @RequestParam("detailMeterId") String detailMeterId,
            @RequestParam("hhmmss") String hhmmss,
            @RequestParam("msgActImp")         String msgActImp,
            @RequestParam("msgActExp")         String msgActExp,
            @RequestParam("msgRactLagImp")     String msgRactLagImp,
            @RequestParam("msgRactLeadImp")    String msgRactLeadImp,
            @RequestParam("msgRactLagExp")     String msgRactLagExp,
            @RequestParam("msgRactLeadExp")    String msgRactLeadExp,
            @RequestParam("msgTotEnergy")      String msgTotEnergy,
            @RequestParam("msgEnergy")         String msgEnergy,
            @RequestParam("msgTotDemandTime")  String msgTotDemandTime,
            @RequestParam("msgMaxDemandTime")  String msgMaxDemandTime,
            @RequestParam("msgTotCummDemand")  String msgTotCummDemand,
            @RequestParam("msgCummDemand")     String msgCummDemand,
            @RequestParam("msgRate1")          String msgRate1,
            @RequestParam("msgRate2")          String msgRate2,
            @RequestParam("msgRate3")          String msgRate3,
            @RequestParam("msgkVah1")          String kVah1,
            @RequestParam("supplierId")        String supplierId) {
    	if(detailContractId.equals("null") || detailContractId.length() == 0)	detailContractId=null;
        List<Map<String, Object>> result = null;
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        Map<String, String> msgMap = new HashMap<String, String>();

        ModelAndView mav = new ModelAndView("jsonView");

        try {
            conditionMap.put("searchStartDate", searchStartDate);
            conditionMap.put("searchEndDate", searchEndDate);
            conditionMap.put("searchDateType", searchDateType);
            conditionMap.put("locationId", locationId);
            conditionMap.put("tariffIndexId", tariffIndexId);
            conditionMap.put("reportType", reportType);
            conditionMap.put("supplierId", supplierId);
            conditionMap.put("customerName", customerName);
            conditionMap.put("contractNo", contractNo);
            conditionMap.put("meterId", meterId);
            conditionMap.put("lastData", lastData);
            conditionMap.put("mdevType", mdevType);
            conditionMap.put("mdevId", mdevId);
            conditionMap.put("detailContractId", detailContractId);
            conditionMap.put("detailMeterId", detailMeterId);
            conditionMap.put("hhmmss", hhmmss);

            if ((DateType.DAILY.getCode()).equals(searchDateType)) {
                // 전일
                String preStartDateTime = TimeUtil.getPreDay(searchStartDate + "000000");
                String preStartDate = preStartDateTime.substring(0, 8);
                conditionMap.put("lastStartDate", preStartDate);
            } else if ((DateType.MONTHLY.getCode()).equals(searchDateType)) { // 월간
                // 전월
                String preStartDateTime = TimeUtil.getPreMonth(searchStartDate + "000000");
                String preStartDate = preStartDateTime.substring(0, 8);
                conditionMap.put("lastStartDate", preStartDate);

                String preYear = preStartDate.substring(0,4);
                String preMonth = preStartDate.substring(4,6);
                String preDay = CalendarUtil.getMonthLastDate(preYear, preMonth);
                conditionMap.put("lastEndDate", preYear + preMonth + preDay);
            }

            // message 생성
            msgMap.put("msgActImp", msgActImp);
            msgMap.put("msgActExp", msgActExp);
            msgMap.put("msgRactLagImp", msgRactLagImp);
            msgMap.put("msgRactLeadImp", msgRactLeadImp);
            msgMap.put("msgRactLagExp", msgRactLagExp);
            msgMap.put("msgRactLeadExp", msgRactLeadExp);
            msgMap.put("msgTotEnergy", msgTotEnergy);
            msgMap.put("msgEnergy", msgEnergy);
            msgMap.put("msgTotDemandTime", msgTotDemandTime);
            msgMap.put("msgMaxDemandTime", msgMaxDemandTime);
            msgMap.put("msgTotCummDemand", msgTotCummDemand);
            msgMap.put("msgCummDemand", msgCummDemand);
            msgMap.put("msgRate1", msgRate1);
            msgMap.put("msgRate2", msgRate2);
            msgMap.put("msgRate3", msgRate3);
            msgMap.put("msgkVah1", kVah1);
            
            conditionMap.put("msgMap", msgMap);

            result = billingManager.getMeteringDetailUsageData(conditionMap);

            mav.addObject("result", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;
    }
    
    @RequestMapping(value="/gadget/mvm/getMeteringDetailLastUsageData")
    public ModelAndView getMeteringDetailLastUsageData(
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("locationId") String locationId,
            @RequestParam("tariffIndexId") String tariffIndexId,
            @RequestParam("reportType") String reportType,
            @RequestParam("customerName") String customerName,
            @RequestParam("contractNo") String contractNo,
            @RequestParam("meterId") String meterId,
            @RequestParam("lastData") String lastData,
            @RequestParam("mdevType") String mdevType,
            @RequestParam("mdevId") String mdevId,
            @RequestParam("detailContractId") String detailContractId,
            @RequestParam("detailMeterId") String detailMeterId,
            @RequestParam("hhmmss") String hhmmss,
            @RequestParam("msgActImp")         String msgActImp,
            @RequestParam("msgActExp")         String msgActExp,
            @RequestParam("msgRactLagImp")     String msgRactLagImp,
            @RequestParam("msgRactLeadImp")    String msgRactLeadImp,
            @RequestParam("msgRactLagExp")     String msgRactLagExp,
            @RequestParam("msgRactLeadExp")    String msgRactLeadExp,
            @RequestParam("msgTotEnergy")      String msgTotEnergy,
            @RequestParam("msgEnergy")         String msgEnergy,
            @RequestParam("msgTotDemandTime")  String msgTotDemandTime,
            @RequestParam("msgMaxDemandTime")  String msgMaxDemandTime,
            @RequestParam("msgTotCummDemand")  String msgTotCummDemand,
            @RequestParam("msgCummDemand")     String msgCummDemand,
            @RequestParam("msgRate1")          String msgRate1,
            @RequestParam("msgRate2")          String msgRate2,
            @RequestParam("msgRate3")          String msgRate3,
            @RequestParam("msgkVah1")          String kVah1,
            @RequestParam("supplierId")        String supplierId) {
    	if(detailContractId.equals("null") || detailContractId.length() == 0)	detailContractId=null;
        List<Map<String, Object>> result = null;
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        Map<String, String> msgMap = new HashMap<String, String>();

        ModelAndView mav = new ModelAndView("jsonView");

        try {
            conditionMap.put("searchStartDate", searchStartDate);
            conditionMap.put("searchEndDate", searchEndDate);
            conditionMap.put("searchDateType", searchDateType);
            conditionMap.put("locationId", locationId);
            conditionMap.put("tariffIndexId", tariffIndexId);
            conditionMap.put("reportType", reportType);
            conditionMap.put("supplierId", supplierId);
            conditionMap.put("customerName", customerName);
            conditionMap.put("contractNo", contractNo);
            conditionMap.put("meterId", meterId);
            conditionMap.put("lastData", lastData);
            conditionMap.put("mdevType", mdevType);
            conditionMap.put("mdevId", mdevId);
            conditionMap.put("detailContractId", detailContractId);
            conditionMap.put("detailMeterId", detailMeterId);
            conditionMap.put("hhmmss", hhmmss);

            if ((DateType.DAILY.getCode()).equals(searchDateType)) {
                // 전일
                String preStartDateTime = TimeUtil.getPreDay(searchStartDate + "000000");
                String preStartDate = preStartDateTime.substring(0, 8);
                conditionMap.put("lastStartDate", preStartDate);
            } else if ((DateType.MONTHLY.getCode()).equals(searchDateType)) { // 월간
                // 전월
                String preStartDateTime = TimeUtil.getPreMonth(searchStartDate + "000000");
                String preStartDate = preStartDateTime.substring(0, 6) + "01";
                conditionMap.put("lastStartDate", preStartDate);

                String preYear = preStartDate.substring(0,4);
                String preMonth = preStartDate.substring(4,6);
                String preDay = CalendarUtil.getMonthLastDate(preYear, preMonth);
                conditionMap.put("lastEndDate", preYear + preMonth + preDay);
            }

            // message 생성
            msgMap.put("msgActImp", msgActImp);
            msgMap.put("msgActExp", msgActExp);
            msgMap.put("msgRactLagImp", msgRactLagImp);
            msgMap.put("msgRactLeadImp", msgRactLeadImp);
            msgMap.put("msgRactLagExp", msgRactLagExp);
            msgMap.put("msgRactLeadExp", msgRactLeadExp);
            msgMap.put("msgTotEnergy", msgTotEnergy);
            msgMap.put("msgEnergy", msgEnergy);
            msgMap.put("msgTotDemandTime", msgTotDemandTime);
            msgMap.put("msgMaxDemandTime", msgMaxDemandTime);
            msgMap.put("msgTotCummDemand", msgTotCummDemand);
            msgMap.put("msgCummDemand", msgCummDemand);
            msgMap.put("msgRate1", msgRate1);
            msgMap.put("msgRate2", msgRate2);
            msgMap.put("msgRate3", msgRate3);
            msgMap.put("msgkVah1", kVah1);

            conditionMap.put("msgMap", msgMap);

            result = billingManager.getMeteringDetailLastUsageData(conditionMap);

            mav.addObject("result", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;
    }
}