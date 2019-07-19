package com.aimir.bo.device;

import java.io.File;
import java.text.ParseException;
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
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.MeterEventLogManager;
import com.aimir.service.system.RoleManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils;
import com.aimir.util.MeterEventLogMakeExcel;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

@Controller
public class MeterEventLogController {

    @Autowired
    MeterEventLogManager meterEventLogManager;

    @Autowired
    RoleManager roleManager;

    @RequestMapping(value="/gadget/device/meterEventLogMini")
    public ModelAndView meterEventLogMini() {
        ModelAndView mav = new ModelAndView("/gadget/device/meterEventLogMini");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        return mav;
    }

    @RequestMapping(value="/gadget/device/meterEventLogMax")
    public ModelAndView meterEventLogMax() {
        ModelAndView mav = new ModelAndView("/gadget/device/meterEventLogMax");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        Supplier supplier = user.getRoleData().getSupplier();
        String mdPattern = supplier.getMd().getPattern();
        Integer dot = mdPattern.indexOf(".");

        if(dot != -1)
        	mdPattern = mdPattern.substring(0,dot);
        
        
        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        mav.addObject("numberFormat", mdPattern.replace("#","0"));  
        return mav;
    }

    @RequestMapping(value="/gadget/device/getMeterEventLogMiniChartData")
    public ModelAndView getMeterEventLogMiniChartData(
        @RequestParam("searchStartDate") String searchStartDate,
        @RequestParam("searchEndDate") String searchEndDate,
        @RequestParam("searchDateType") String searchDateType,
        @RequestParam("locationId") String locationId,
        @RequestParam("supplierId") String supplierId) {

        ModelAndView mav = null;
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        long userId = 0L;

        try {
//          ESAPI.httpUtilities().setCurrentHTTP(request, response);
            // ESAPI.setAuthenticator(new AimirAuthenticator());
            AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();

            AimirUser user = (AimirUser)instance.getUserFromSession();

            if(user!=null && !user.isAnonymous()) {
                userId = user.getAccountId();
            }

            conditionMap.put("searchStartDate", searchStartDate);
            conditionMap.put("searchEndDate", searchEndDate);
            conditionMap.put("searchDateType", searchDateType);
            conditionMap.put("locationId", locationId);
            conditionMap.put("supplierId", supplierId);
            conditionMap.put("userId", userId);

            if(searchStartDate.length() == 0 && searchEndDate.length() == 0) {
                conditionMap.put("searchStartDate", TimeUtil.getCurrentDay());
                conditionMap.put("searchEndDate", TimeUtil.getCurrentDay());
                conditionMap.put("searchDateType", DateType.DAILY.getCode());
            }

            mav = new ModelAndView("jsonView");

            List<Map<String, Object>> result = meterEventLogManager.getMeterEventLogMiniChartData(conditionMap);
            mav.addObject("chartData", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;
    }

    @RequestMapping(value="/gadget/device/getMeterEventLogProfileData")
    public ModelAndView getMeterEventLogProfileData() {

        ModelAndView mav = null;
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        long userId = 0L;

        try {
            // ESAPI.setAuthenticator(new AimirAuthenticator());
            AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();

            AimirUser user = (AimirUser)instance.getUserFromSession();

            if(user!=null && !user.isAnonymous()) {
                userId = user.getAccountId();
            }

            conditionMap.put("userId", userId);

            mav = new ModelAndView("jsonView");

            List<Map<String, Object>> result = meterEventLogManager.getMeterEventLogProfileData(conditionMap);
            mav.addObject("gridData", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;
    }

    @RequestMapping(value="/gadget/device/updateMeterEventLogProfileData")
    public ModelAndView updateMeterEventLogProfileData(
        @RequestParam("meterEventNames") String meterEventNames,
        @RequestParam("allRemove") String allRemove) {

        ModelAndView mav = null;
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        long userId = 0L;

        try {
            // ESAPI.setAuthenticator(new AimirAuthenticator());
            AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();

            AimirUser user = (AimirUser)instance.getUserFromSession();

            if(user!=null && !user.isAnonymous()) {
                userId = user.getAccountId();
            }
            
            conditionMap.put("meterEventNames", meterEventNames.split(","));
            conditionMap.put("userId", userId);
            conditionMap.put("allRemove", allRemove);

            mav = new ModelAndView("jsonView");

            meterEventLogManager.updateMeterEventLogProfileData(conditionMap);
            mav.addObject("status", "success");
        } catch (Exception e) {
            e.printStackTrace();
            mav.addObject("status", "fail");
        }

        return mav;
    }

    @RequestMapping(value="/gadget/device/getMeterEventLogComboData")
    public ModelAndView getMeterEventLogComboData() {

        ModelAndView mav = null;

        try {
            mav = new ModelAndView("jsonView");
            List<Map<String, Object>> result = meterEventLogManager.getEventNames();
            mav.addObject("result", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;
    }

    @RequestMapping(value="/gadget/device/getMeterEventLogMaxChartData")
    public ModelAndView getMeterEventLogMaxChartData(
        @RequestParam("searchStartDate") String searchStartDate,
        @RequestParam("searchEndDate") String searchEndDate,
        @RequestParam("searchDateType") String searchDateType,
        @RequestParam("locationId") String locationId,
        @RequestParam("eventName") String eventName,
        @RequestParam("meterType") String meterType,
        @RequestParam("meterId") String meterId,
        @RequestParam("occurFreq") String occurFreq,
        @RequestParam("supplierId") String supplierId) {

        ModelAndView mav = null;
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        long userId = 0L;

        try {
            // ESAPI.setAuthenticator(new AimirAuthenticator());
            AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();

            AimirUser user = (AimirUser)instance.getUserFromSession();

            if(user!=null && !user.isAnonymous()) {
                userId = user.getAccountId();
            }

            conditionMap.put("searchStartDate", searchStartDate);
            conditionMap.put("searchEndDate", searchEndDate);
            conditionMap.put("searchDateType", searchDateType);
            conditionMap.put("locationId", locationId);

            conditionMap.put("eventName", eventName);
            conditionMap.put("meterType", meterType);
            conditionMap.put("meterId", meterId);
            conditionMap.put("occurFreq", occurFreq==null||occurFreq.length() == 0?"0":occurFreq);

            conditionMap.put("supplierId", supplierId);
            conditionMap.put("userId", userId);

            if(searchStartDate.length() == 0 && searchEndDate.length() == 0) {
                conditionMap.put("searchStartDate", TimeUtil.getCurrentDay());
                conditionMap.put("searchEndDate", TimeUtil.getCurrentDay());
                conditionMap.put("searchDateType", DateType.DAILY.getCode());
            }

            mav = new ModelAndView("jsonView");

            List<Map<String, Object>> result = meterEventLogManager.getMeterEventLogMaxChartData(conditionMap);
            mav.addObject("chartData", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;
    }

    @RequestMapping(value="/gadget/device/getMeterEventLogMeterByEventGridData")
    public ModelAndView getMeterEventLogMeterByEventGridData(
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("locationId") String locationId,
            @RequestParam("eventName") String eventName,
            @RequestParam("meterType") String meterType,
            @RequestParam("meterId") String meterId,
            @RequestParam("occurFreq") String occurFreq,
            @RequestParam("page") String page,
            @RequestParam("pageSize") String pageSize,
            @RequestParam("supplierId") String supplierId) {

        ModelAndView mav = null;
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        long userId = 0L;

        try {
            // ESAPI.setAuthenticator(new AimirAuthenticator());
            AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();

            AimirUser user = (AimirUser)instance.getUserFromSession();

            if (user!=null && !user.isAnonymous()) {
                userId = user.getAccountId();
            }

            conditionMap.put("searchStartDate", searchStartDate);
            conditionMap.put("searchEndDate", searchEndDate);
            conditionMap.put("searchDateType", searchDateType);
            conditionMap.put("locationId", locationId);
            conditionMap.put("eventName", eventName);
            conditionMap.put("meterType", meterType);
            conditionMap.put("meterId", meterId);
            conditionMap.put("occurFreq", (occurFreq == null || occurFreq.isEmpty()) ? "0" : occurFreq);
            conditionMap.put("page", page);
            conditionMap.put("pageSize", pageSize);
            conditionMap.put("supplierId", supplierId);
            conditionMap.put("userId", userId);

            if (searchStartDate.length() == 0 && searchEndDate.length() == 0) {
                conditionMap.put("searchStartDate", TimeUtil.getCurrentDay());
                conditionMap.put("searchEndDate", TimeUtil.getCurrentDay());
                conditionMap.put("searchDateType", DateType.DAILY.getCode());
            }

            mav = new ModelAndView("jsonView");

            mav.addObject("gridDatas", meterEventLogManager.getMeterEventLogMeterByEventGridData(conditionMap));
            mav.addObject("total", meterEventLogManager.getMeterEventLogMeterByEventGridDataCount(conditionMap));


        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;
    }

    @RequestMapping(value="/gadget/device/getMeterEventLogEventByMeterGridData")
    public ModelAndView getMeterEventLogEventByMeterGridData(
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("locationId") String locationId,
            @RequestParam("eventName") String eventName,
            @RequestParam("meterType") String meterType,
            @RequestParam("meterId") String meterId,
            @RequestParam("activatorId") String activatorId,
            @RequestParam("occurFreq") String occurFreq,
            @RequestParam("page") String page,
            @RequestParam("pageSize") String pageSize,
            @RequestParam("supplierId") String supplierId) {

        ModelAndView mav = null;
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        long userId = 0L;

        try {
            // ESAPI.setAuthenticator(new AimirAuthenticator());
            AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();

            AimirUser user = (AimirUser)instance.getUserFromSession();

            if (user!=null && !user.isAnonymous()) {
                userId = user.getAccountId();
            }

            conditionMap.put("searchStartDate", searchStartDate);
            conditionMap.put("searchEndDate", searchEndDate);
            conditionMap.put("searchDateType", searchDateType);
            conditionMap.put("locationId", locationId);
            conditionMap.put("eventName", eventName);
            conditionMap.put("meterType", meterType);
            conditionMap.put("meterId", meterId);
            conditionMap.put("activatorId", activatorId);
            conditionMap.put("occurFreq", occurFreq==null||occurFreq.length() == 0?"0":occurFreq);
            conditionMap.put("page", page);
            conditionMap.put("pageSize", pageSize);
            conditionMap.put("supplierId", supplierId);
            conditionMap.put("userId", userId);

            if (searchStartDate.length() == 0 && searchEndDate.length() == 0) {
                conditionMap.put("searchStartDate", TimeUtil.getCurrentDay());
                conditionMap.put("searchEndDate", TimeUtil.getCurrentDay());
                conditionMap.put("searchDateType", DateType.DAILY.getCode());
            }

            mav = new ModelAndView("jsonView");
            
            List<Map<String, Object>> list = meterEventLogManager.getMeterEventLogEventByMeterGridData(conditionMap);
            List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();

            String obisCode;
            for(int i=0; i<list.size();i++){
            	Map<String, Object> map = list.get(i);
            	obisCode = map.get("EVENTMODEL").toString() + "." + map.get("EVENTVALUE");
            	map.put("OBISCODE", obisCode);
            	list2.add(i, map);
            }
            
            mav.addObject("gridDatas", list2);
            mav.addObject("total", meterEventLogManager.getMeterEventLogEventByMeterGridDataCount(conditionMap));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;
    }
    
    @RequestMapping(value = "/gadget/device/meterEventLogExcelDownloadPopup")
    public ModelAndView meterEventLogExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView(
                "/gadget/device/meterEventLogExcelDownloadPopup");
        return mav;
    }

    @RequestMapping(value = "/gadget/device/meterEventLogExcelMake")
    public ModelAndView meterEventLogExcelMake(
            @RequestParam("condition[]")    String[] condition,
            @RequestParam("fmtMessage[]")   String[] fmtMessage,
            @RequestParam("type")           String type,
            @RequestParam("eventName")      String eventName,
            @RequestParam("meterId")        String meterId,
            @RequestParam("filePath")       String filePath) {

        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        boolean isLast = false;
        Long total = 0L; // 데이터 조회건수
        Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

        final String logPrefix = "meterEventLog("+type+")";//13+7+14

        ModelAndView mav = new ModelAndView("jsonView");

        List<Map<String, Object>> result = null;
        try {
            Map<String, Object> conditionMap = new HashMap<String, Object>();

            long userId = 0L;
            // ESAPI.setAuthenticator(new AimirAuthenticator());
            AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();

            AimirUser user = (AimirUser)instance.getUserFromSession();

            if(user!=null && !user.isAnonymous()) {
                userId = user.getAccountId();
            }

            conditionMap.put("searchStartDate", condition[0]);
            conditionMap.put("searchEndDate",   condition[1]);
            conditionMap.put("searchDateType",  condition[2]);
            conditionMap.put("locationId",      condition[3]);
            if(eventName==null || eventName.length() == 0){
                conditionMap.put("eventName",   condition[4]);
            }else {
                conditionMap.put("eventName",   eventName);
            }
            conditionMap.put("meterType",       condition[5]);
            if(meterId==null || meterId.length() == 0){
                conditionMap.put("meterId",     condition[6]);
            }else{
                conditionMap.put("meterId",     meterId);
            }
            conditionMap.put("occurFreq",       condition[7]==null||condition[7].length() == 0?"0":condition[7] );
            conditionMap.put("supplierId",      condition[8]);
            conditionMap.put("userId",          userId);

             if(condition[0].length() == 0 && condition[1].length() == 0) {
                    conditionMap.put("searchStartDate", TimeUtil.getCurrentDay());
                    conditionMap.put("searchEndDate", TimeUtil.getCurrentDay());
                    conditionMap.put("searchDateType", DateType.DAILY.getCode());
              }
             if(type.equals("meter")){
                result = meterEventLogManager.getMeterEventLogMeterByEventGridData(conditionMap);
             }else{
                result = meterEventLogManager.getMeterEventLogEventByMeterGridData(conditionMap);
             }

            total = new Integer(result.size()).longValue();

            mav.addObject("total", total);
            if (total <= 0) {
                return mav;
            }

            sbFileName.append(logPrefix);

            sbFileName.append(TimeUtil.getCurrentTimeMilli());

            // message 생성
            msgMap.put("number",        fmtMessage[0]);
            msgMap.put("opentime",      fmtMessage[1]);
            msgMap.put("writetime",     fmtMessage[2]);
            msgMap.put("eventName",     fmtMessage[3]);
            msgMap.put("location",      fmtMessage[4]);
            msgMap.put("meterid",       fmtMessage[5]);
            msgMap.put("metertype",     fmtMessage[6]);
            msgMap.put("message",       fmtMessage[7]);
            msgMap.put("troubleAdvice", fmtMessage[8]);
            msgMap.put("excel",         fmtMessage[9]);
            msgMap.put("occurFreq",     fmtMessage[10]);
            msgMap.put("title", fmtMessage[19]);

            msgMap.put("type",  type);


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

                        // 파일길이 : 34이상, 확장자 : xls|zip
                        if (filename.length() > 33
                                && (filename.endsWith("xls") || filename
                                        .endsWith("zip"))) {
                            // 10일 지난 파일들 삭제
                            if (filename.startsWith(logPrefix)
                                    && filename.substring(20, 28).compareTo(
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
            MeterEventLogMakeExcel wExcel = new MeterEventLogMakeExcel();
            int cnt = 1;
            int idx = 0;
            int fnum = 0;
            int splCnt = 0;

            if (total <= maxRows) {
                sbSplFileName = new StringBuilder();
                sbSplFileName.append(sbFileName);
                sbSplFileName.append(".xls");
                wExcel.writeReportExcel(result, msgMap, isLast, filePath,
                        sbSplFileName.toString());
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
                                sbSplFileName.toString());
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mav;
    }
}