package com.aimir.bo.device;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.LineType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Code;
import com.aimir.model.system.Operator;
import com.aimir.service.device.PowerAlarmLogManager;
import com.aimir.service.system.CodeManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.PowerAlarmLogMakeExcel;
import com.aimir.util.ReflectionUtils;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

@Controller
public class PowerAlarmLogController {

    @Autowired
    CodeManager codeManager;

    @Autowired
    PowerAlarmLogManager powerAlarmLogManager;

    /**
     * Mini Gadget
     * @return
     */
    @RequestMapping(value="/gadget/device/powerAlarmLogMiniGadget")
    public ModelAndView getPowerAlarmLogMiniGadget() {
        ModelAndView mav = new ModelAndView("gadget/device/powerAlarmLogMiniGadget");
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();

        if (user != null && !user.isAnonymous()) {
            try {
                mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        mav.addObject("lineMissingTypes", LineType.values()); // 결상타입 Code
        return mav;
    }

    /**
     * Max Gadget
     * @return
     */
    @RequestMapping(value="/gadget/device/powerAlarmLogMaxGadget")
    public ModelAndView getPowerAlarmLogMaxGadget(HttpServletRequest request, HttpServletResponse response) {

        ESAPI.httpUtilities().setCurrentHTTP(request, response);

        // ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();

        AimirUser user = (AimirUser)instance.getUserFromSession();

        ModelAndView mav = new ModelAndView("gadget/device/powerAlarmLogMaxGadget");
        mav.addObject("longOutageCodes", codeManager.getChildCodes("12.1")); // 정전지속시간 Long Outage Code(More than 1 hour)
        mav.addObject("shortOutageCodes", codeManager.getChildCodes("12.2")); // 정전지속시간 Short Outage Code(Less than 1 hour)
        mav.addObject("lineMissingTypes", LineType.values()); // 결상타입 Code

        if (user != null && !user.isAnonymous()) {
            try {
                mav.addObject("userId", user.getOperator(new Operator()).getId());
                mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return mav;
    }

    /**
     * PowerAlarmLog Type Code
     * Using by ColumnChart Series
     * @return
     */
    @RequestMapping(value="/gadget/device/powerAlarmLogCode")
    public ModelAndView getLongOutageCode() {
        ModelMap model = new ModelMap("longOutageCodes", codeManager.getChildCodes("12.1")); // 정전지속시간 Long Outage Code(More than 1 hour)
        model.putAll(new ModelMap("shortOutageCodes", codeManager.getChildCodes("12.2"))); // 정전지속시간 Short Outage Code(Less than 1 hour)
        return new ModelAndView("jsonView", model);
    }

    @RequestMapping(value="/gadget/device/getPowerAlarmLogMiniGadgetChartData")
    public ModelAndView getPowerAlarmLogMiniGadgetChartData(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("colFormat") String colFormat,
            @RequestParam("lineMissingType") String lineMissingType,
            @RequestParam("currTabId") String currTabId) {
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplierId", supplierId);
        condition.put("colFormat", colFormat);
        condition.put("lineMissingType", lineMissingType);
        condition.put("currTabId", currTabId);

        ModelAndView mav = new ModelAndView("jsonView");

        List<Object> longOutageCodes = new ArrayList<Object>();
        List<Object> shortOutageCodes = new ArrayList<Object>();
 
        List<Code> r = codeManager.getChildCodes("12.1");
        for (Code obj : r) {
            longOutageCodes.add(0, obj);
        }

        r = codeManager.getChildCodes("12.2");
        for (Code obj: r) {
            shortOutageCodes.add(0, obj);
        }

        mav.addObject("longOutageCodes", ReflectionUtils.getDefineListToMapList(longOutageCodes));
        mav.addObject("shortOutageCodes", ReflectionUtils.getDefineListToMapList(shortOutageCodes));
        mav.addObject("chartData", powerAlarmLogManager.getPowerAlarmLogColumnMiniChart(condition));

        return mav;
    }

    @RequestMapping(value="/gadget/device/getPowerAlarmLogMaxGadgetChartData")
    public ModelAndView getPowerAlarmLogMaxGadgetChartData(@RequestParam("currTabId") String currTabId,
            @RequestParam("location") Integer location,
            @RequestParam("customerName") String customerName,
            @RequestParam("meter") String meter,
            @RequestParam("type") String type,
            @RequestParam("lineMissingType") String lineMissingType,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchStartHour") String searchStartHour,
            @RequestParam("searchEndHour") String searchEndHour,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("supplierId") Integer supplierId) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("currTabId", currTabId);
        condition.put("location", location);
        condition.put("customerName", customerName);
        condition.put("meter", meter);
        condition.put("type", type);
        condition.put("lineMissingType", lineMissingType);
        condition.put("searchStartDate",searchStartDate);
        condition.put("searchEndDate", searchEndDate);
        condition.put("searchStartHour", searchStartHour);
        condition.put("searchEndHour", searchEndHour);
        condition.put("searchDateType", searchDateType);
        condition.put("supplierId", supplierId);
        condition.put("colFormat", "");

        ModelAndView mav = new ModelAndView("jsonView");

        List<Object> longOutageCodes = new ArrayList<Object>();
        List<Object> shortOutageCodes = new ArrayList<Object>();

        List<Code> r = codeManager.getChildCodes("12.1");
        for (Code obj : r) {
            longOutageCodes.add(0, obj);
        }

        r = codeManager.getChildCodes("12.2");
        for (Code obj : r) {
            shortOutageCodes.add(0, obj);
        }

        mav.addObject("longOutageCodes", ReflectionUtils.getDefineListToMapList(longOutageCodes));
        mav.addObject("shortOutageCodes", ReflectionUtils.getDefineListToMapList(shortOutageCodes));
        mav.addObject("columnChartData", powerAlarmLogManager.getPowerAlarmLogColumnChart(condition));
        mav.addObject("pieChartData", powerAlarmLogManager.getPowerAlarmLogPieData(condition));

        return mav;
    }

    @RequestMapping(value="/gadget/device/getPowerAlarmLogMaxGrid")
    public ModelAndView getPowerAlarmLogMaxGrid(@RequestParam("currTabId") String currTabId,
            @RequestParam("location") Integer location,
            @RequestParam("customerName") String customerName,
            @RequestParam("meter") String meter,
            @RequestParam("type") String type,
            @RequestParam("lineMissingType") String lineMissingType,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchStartHour") String searchStartHour,
            @RequestParam("searchEndHour") String searchEndHour,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("supplierId") Integer supplierId,
            @RequestParam("durationDays") String durationDays,
            @RequestParam("colFormat") String colFormat,
            @RequestParam("page") Integer page,
            @RequestParam("limit") Integer limit) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("currTabId", currTabId);
        condition.put("location", location);
        condition.put("customerName", customerName);
        condition.put("meter", meter);
        condition.put("type", type);
        condition.put("lineMissingType", lineMissingType);
        condition.put("searchStartDate", searchStartDate);
        condition.put("searchEndDate", searchEndDate);
        condition.put("searchStartHour", searchStartHour);
        condition.put("searchEndHour", searchEndHour);
        condition.put("searchDateType", searchDateType);
        condition.put("supplierId", supplierId);
        condition.put("durationDays", durationDays);
        condition.put("colFormat", colFormat);
        condition.put("page", page);
        condition.put("limit", limit);

        ModelMap modelMap = new ModelMap();

        modelMap.addAttribute("gridData", powerAlarmLogManager.getPowerAlamLogMaxData(condition));
        modelMap.addAttribute("totalCount", powerAlarmLogManager.getPowerAlarmLogGridDataTotalCount(condition));

        return new ModelAndView("jsonView", modelMap);
    }

    @RequestMapping(value = "/gadget/device/powerAlarmLogExcelDownloadPopup")
    public ModelAndView dataGapsExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/device/powerAlarmLogExcelDownloadPopup");
        return mav;
    }

    @RequestMapping(value = "/gadget/device/powerAlarmLogExcelMake")
    public ModelAndView powerAlarmLogExcelMake(@RequestParam("condition[]") String[] condition,
            @RequestParam("fmtMessage[]") String[] fmtMessage,
            @RequestParam("fmt[]") String[] fmt,
            @RequestParam("filePath") String filePath) {

        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
        List<Object> list = new ArrayList<Object>();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        boolean isLast = false;
        Long total = 0L; // 데이터 조회건수
        Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

        final String logPrefix = "powerAlarmLog";// 13+14

        ModelAndView mav = new ModelAndView("jsonView");

        List<Object> result = null;
        try {
            Map<String, Object> conditionMap = new HashMap<String, Object>();

            if (condition[0].equals("PC")) { // 정전
                conditionMap.put("currTabId", condition[0]);
                String locationId = StringUtil.nullToBlank(condition[1]);
                conditionMap.put("location", (!locationId.isEmpty()) ? Integer.valueOf(locationId) : null);
                conditionMap.put("customerName", condition[2]);
                conditionMap.put("meter", condition[3]);
                conditionMap.put("type", condition[4]);
                conditionMap.put("lineMissingType", "");
                conditionMap.put("searchStartDate", condition[6]);
                conditionMap.put("searchEndDate", condition[7]);
                conditionMap.put("searchStartHour", condition[8]);
                conditionMap.put("searchEndHour", condition[9]);
                conditionMap.put("searchDateType", condition[10]);
                String supplierId = StringUtil.nullToBlank(condition[11]);
                conditionMap.put("supplierId", (!supplierId.isEmpty()) ? Integer.valueOf(supplierId) : null);
                conditionMap.put("durationDays", condition[12]);
            } else if (condition[0].equals("LM")) { // 결상
                conditionMap.put("currTabId", condition[0]);
                String locationId = StringUtil.nullToBlank(condition[1]);
                conditionMap.put("location", (!locationId.isEmpty()) ? Integer.valueOf(locationId) : null);
                conditionMap.put("customerName", condition[2]);
                conditionMap.put("meter", condition[3]);
                conditionMap.put("type", condition[4]);
                conditionMap.put("lineMissingType", condition[5]);
                conditionMap.put("searchStartDate", condition[6]);
                conditionMap.put("searchEndDate", condition[7]);
                conditionMap.put("searchStartHour", condition[8]);
                conditionMap.put("searchEndHour", condition[9]);
                conditionMap.put("searchDateType", condition[10]);
                String supplierId = StringUtil.nullToBlank(condition[11]);
                conditionMap.put("supplierId", (!supplierId.isEmpty()) ? Integer.valueOf(supplierId) : null);
                conditionMap.put("durationDays", condition[12]);
            }

            result = (List<Object>)powerAlarmLogManager.getPowerAlamLogMaxDataExcel(conditionMap);

            total = new Integer(result.size()).longValue();

            mav.addObject("total", total);
            if (total <= 0) {
                return mav;
            }

            sbFileName.append(logPrefix);

            sbFileName.append(TimeUtil.getCurrentTimeMilli());

            // message 생성
            msgMap.put("id", fmtMessage[0]);
            msgMap.put("openTime", fmtMessage[1]);
            msgMap.put("closeTime", fmtMessage[2]);
            msgMap.put("supplier", fmtMessage[3]);
            if (condition[0].equals("LM")) {
                msgMap.put("type", "LM");
                msgMap.put("lineType", fmtMessage[4]);
            } else {
                msgMap.put("type", "PC");
            }
            msgMap.put("custName", fmtMessage[5]);
            msgMap.put("meter", fmtMessage[6]);
            msgMap.put("duration", fmtMessage[7]);
            msgMap.put("status", fmtMessage[8]);
            msgMap.put("message", fmtMessage[9]);

            msgMap.put("type2", fmt[0]);
            msgMap.put("open", fmt[1]);
            msgMap.put("close", fmt[2]);
            msgMap.put("day", fmt[3]);
            msgMap.put("hour", fmt[4]);
            msgMap.put("min", fmt[5]);
            msgMap.put("sec", fmt[6]);
            msgMap.put("title", fmt[7]);

            // check download dir
            File downDir = new File(filePath);

            if (downDir.exists()) {
                File[] files = downDir.listFiles();

                if (files != null) {
                    String filename = null;
                    String deleteDate;

                    deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10); // 10일 이전 일자
                    boolean isDel = false;

                    for (File file : files) {

                        filename = file.getName();
                        isDel = false;

                        // 파일길이 : 26이상, 확장자 : xls|zip
                        if (filename.length() > 26 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
                            // 10일 지난 파일들 삭제
                            if (filename.startsWith(logPrefix) && filename.substring(13, 21).compareTo(deleteDate) < 0) {
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
            PowerAlarmLogMakeExcel wExcel = new PowerAlarmLogMakeExcel();
            int cnt = 1;
            int idx = 0;
            int fnum = 0;
            int splCnt = 0;

            if (total <= maxRows) {
                sbSplFileName = new StringBuilder();
                sbSplFileName.append(sbFileName);
                sbSplFileName.append(".xls");
                wExcel.writeReportExcel(result, msgMap, isLast, filePath, sbSplFileName.toString());
                fileNameList.add(sbSplFileName.toString());
            } else {
                for (int i = 0; i < total; i++) {
                    if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
                        sbSplFileName = new StringBuilder();
                        sbSplFileName.append(sbFileName);
                        sbSplFileName.append('(').append(++fnum).append(").xls");

                        list = result.subList(idx, (i + 1));

                        wExcel.writeReportExcel(list, msgMap, isLast, filePath, sbSplFileName.toString());
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

        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch(Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mav;
    }
}