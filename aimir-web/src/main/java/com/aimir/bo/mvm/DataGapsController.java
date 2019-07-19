package com.aimir.bo.mvm;

import java.io.File;
import java.text.DecimalFormat;
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

import com.aimir.dao.system.SupplierDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.Role;
import com.aimir.service.mvm.DataGapsManager;
import com.aimir.util.DecimalUtil;
import com.aimir.service.system.RoleManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils;
import com.aimir.util.DataGapsMakeExcel;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

@Controller
public class DataGapsController {

    @Autowired
    DataGapsManager dataGapsManager;

    @Autowired
    RoleManager roleManager;
    
    @Autowired
    SupplierDao supplierDao;

    @RequestMapping(value="/gadget/mvm/dataGapsEmMiniGadget")
    public ModelAndView dataGapsEmMiniGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/dataGapsMiniGadget");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        mav.addObject("meterTypeCode2", "EM");
        mav.addObject("MeterType", "MeterType.EM");
        mav.addObject("chartColor", "fChartColor_Elec[0]");

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("ondemandAuth", authMap.get("ondemand")); // Ondemand 권한(command = true)

        return mav;
    }

    @RequestMapping(value="/gadget/mvm/dataGapsEmMaxGadget")
    public ModelAndView dataGapsEmMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/dataGapsMaxGadget");
        mav.addObject("meterTypeCode2", "EM");
        mav.addObject("MeterType", "MeterType.EM");
        mav.addObject("chartColor", "fChartColor_Elec[0]");
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/dataGapsGmMiniGadget")
    public ModelAndView dataGapsGmMiniGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/dataGapsMiniGadget");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        mav.addObject("meterTypeCode2", "GM");
        mav.addObject("MeterType", "MeterType.GM");
        mav.addObject("chartColor", "fChartColor_Gas[0]");

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("ondemandAuth", authMap.get("ondemand")); // Ondemand 권한(command = true)

        return mav;
    }

    @RequestMapping(value="/gadget/mvm/dataGapsGmMaxGadget")
    public ModelAndView dataGapsGmMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/dataGapsMaxGadget");
        mav.addObject("meterTypeCode2", "GM");
        mav.addObject("MeterType", "MeterType.GM");
        mav.addObject("chartColor", "fChartColor_Gas[0]");
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/dataGapsWmMiniGadget")
    public ModelAndView dataGapsWmMiniGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/dataGapsMiniGadget");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        mav.addObject("meterTypeCode2", "WM");
        mav.addObject("MeterType", "MeterType.WM");
        mav.addObject("chartColor", "fChartColor_Water[0]");

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("ondemandAuth", authMap.get("ondemand")); // Ondemand 권한(command = true)

        return mav;
    }

    @RequestMapping(value="/gadget/mvm/dataGapsWmMaxGadget")
    public ModelAndView dataGapsWmMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/dataGapsMaxGadget");
        mav.addObject("meterTypeCode2", "WM");
        mav.addObject("MeterType", "MeterType.WM");
        mav.addObject("chartColor", "fChartColor_Water[0]");
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/dataGapsHmMiniGadget")
    public ModelAndView dataGapsHmMiniGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/dataGapsMiniGadget");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        mav.addObject("meterTypeCode2", "HM");
        mav.addObject("MeterType", "MeterType.HM");
        mav.addObject("chartColor", "fChartColor_Heat[0]");

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("ondemandAuth", authMap.get("ondemand")); // Ondemand 권한(command = true)

        return mav;
    }

    @RequestMapping(value="/gadget/mvm/dataGapsHmMaxGadget")
    public ModelAndView dataGapsHmMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/dataGapsMaxGadget");
        mav.addObject("meterTypeCode2", "HM");
        mav.addObject("MeterType", "MeterType.HM");
        mav.addObject("chartColor", "fChartColor_Heat[0]");
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/getDataGaps")
    public ModelAndView getDataGaps(@RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("meterType") String meterType,
            @RequestParam("supplierId") String supplierId) {

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("searchDateType", searchDateType);
        conditionMap.put("meterType", meterType);
        conditionMap.put("supplierId", supplierId);

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> datagaps = dataGapsManager.getDataGaps(conditionMap);

        mav.addObject("result", datagaps);

        return mav;
    }

    /**
     * @desc
     * @param searchStartDate
     * @param searchEndDate
     * @param searchDateType
     * @param meterType
     * @param supplierId
     * @return
     */
    @SuppressWarnings({ "rawtypes" })
    @RequestMapping(value="/gadget/mvm/getDataGaps2")
    public ModelAndView getDataGaps2(@RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("meterType") String meterType,
            @RequestParam("supplierId") String supplierId,
            @RequestParam("page") String page) {

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("searchDateType", searchDateType);
        conditionMap.put("meterType", meterType);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("page", page);

        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> datagaps = dataGapsManager.getDataGaps2(conditionMap);

        List dataGapsList = new ArrayList();

        // meter Counts
        int allMissingMeterCount = (Integer) datagaps.get("allMissingMeterCount");
        int patialMissingMeterCount = (Integer) datagaps.get("patialMissingMeterCount");
        int totalMeterCount = (Integer) datagaps.get("totalMeterCount");

        // dataGapsList fetch
        dataGapsList = (List) datagaps.get("dataGapsList");
        
        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
        
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
        
        mav.addObject("allMissingMeterCount", dfMd.format(allMissingMeterCount));
        mav.addObject("patialMissingMeterCount", dfMd.format(patialMissingMeterCount));
        mav.addObject("totalMeterCount", dfMd.format(totalMeterCount));

        mav.addObject("dataGapsList", dataGapsList);

        return mav;
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value="/gadget/mvm/getLpMissingMeters")
    public ModelAndView getLpMissingMeters(@RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("mdsId") String mdsId,
            @RequestParam("deviceId") String deviceId,
            @RequestParam("deviceType") String deviceType,
            @RequestParam("meterType") String meterType,
            @RequestParam("supplierId") String supplierId) {

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("searchDateType", searchDateType);
        conditionMap.put("mdsId", mdsId);
        conditionMap.put("deviceId", deviceId);
        conditionMap.put("deviceType", deviceType);
        conditionMap.put("meterType", meterType);
        conditionMap.put("supplierId", supplierId);

        ModelAndView mav = new ModelAndView("jsonView");
        List lpmissingmeterslist = new ArrayList();

        lpmissingmeterslist = dataGapsManager.getLpMissingMeters(conditionMap);

        String totalCnt = lpmissingmeterslist.get(0).toString();

        // Missing meter List
        List arrList = (List) lpmissingmeterslist.get(1);

        // int totalCnt = dataGapsManager.getLpMissingMetersListCnt(conditionMap);

        mav.addObject("totalCnt", totalCnt);
        mav.addObject("result", arrList);

        return mav;
    }

    /**
     * @DEcs : For extjs grid action
     * @param searchStartDate
     * @param searchEndDate
     * @param searchDateType
     * @param mdsId
     * @param deviceId
     * @param deviceType
     * @param meterType
     * @param supplierId
     * @return
     */
    @SuppressWarnings({ "rawtypes" })
    @RequestMapping(value="/gadget/mvm/getLpMissingMeters2")
    public ModelAndView getLpMissingMeters2(@RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("mdsId") String mdsId,
            @RequestParam("deviceId") String deviceId,
            @RequestParam("deviceType") String deviceType,
            @RequestParam("meterType") String meterType,
            @RequestParam("supplierId") String supplierId,
            @RequestParam("pageSize") String pageSize,
            @RequestParam("page") String page) {

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        Map<String, Object> conditionMap2 = new HashMap<String, Object>();

        // param for totalCnt
        conditionMap2.put("searchStartDate", searchStartDate);
        conditionMap2.put("searchEndDate", searchEndDate);
        conditionMap2.put("searchDateType", searchDateType);
        conditionMap2.put("mdsId", mdsId);
        conditionMap2.put("deviceId", deviceId);
        conditionMap2.put("deviceType", deviceType);
        conditionMap2.put("meterType", meterType);
        conditionMap2.put("supplierId", supplierId);

        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("searchDateType", searchDateType);
        conditionMap.put("mdsId", mdsId);
        conditionMap.put("deviceId", deviceId);
        conditionMap.put("deviceType", deviceType);
        conditionMap.put("meterType", meterType);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("pageSize", pageSize);
        conditionMap.put("page", page);

        ModelAndView mav = new ModelAndView("jsonView");

        /**
         * @Desc: Missing meter Count fetch
         */
        List cntList = new ArrayList();
        cntList = dataGapsManager.getLpMissingMeters(conditionMap2);
        String totalCnt = cntList.get(0).toString();
        mav.addObject("totalCnt", totalCnt);

        /**
         * @Desc: Missing meter List fetch
         */
        List lpmissingmeterslist2 = new ArrayList();

        lpmissingmeterslist2 = dataGapsManager.getLpMissingMeters2(conditionMap);

        mav.addObject("lpmissingmeterslist", lpmissingmeterslist2);

        return mav;
    }

    @RequestMapping(value="/gadget/mvm/getLpMissingCount")
    public ModelAndView getLpMissingCount(
            @RequestParam("mdsId") String mdsId,
            @RequestParam("meterId") String meterId,
            @RequestParam("meterType") String meterType,
            @RequestParam("deviceId") String deviceId,
            @RequestParam("deviceType") String deviceType,
            @RequestParam("lpInterval") String lpInterval,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("searchDateType") String searchDateType,
            @RequestParam("yyyymmdd") String yyyymmdd,
            @RequestParam("supplierId") String supplierId) {

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("mdsId", mdsId);
        conditionMap.put("meterId", meterId);
        conditionMap.put("meterType", meterType);
        conditionMap.put("deviceId", deviceId);
        conditionMap.put("deviceType", deviceType);
        conditionMap.put("lpInterval", lpInterval);
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("searchDateType", searchDateType);
        conditionMap.put("yyyymmdd", yyyymmdd);
        conditionMap.put("supplierId", supplierId);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("result", dataGapsManager.getLpMissingCount(conditionMap) );
        return mav;
    }

    @RequestMapping(value = "/gadget/mvm/dataGapsExcelDownloadPopup")
    public ModelAndView dataGapsExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/mvm/dataGapsExcelDownloadPopup");
        return mav;
    }

    @RequestMapping(value = "/gadget/mvm/dataGapsExcelMake")
    public ModelAndView dataGapsExcelMake(@RequestParam("condition[]") String[] condition,
            @RequestParam("fmtMessage[]") String[] fmtMessage,
            @RequestParam("filePath") String filePath) {

        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
        List<Object> list = new ArrayList<Object>();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        boolean isLast = false;
        Long total = 0L; // 데이터 조회건수
        Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

        final String logPrefix = "dataGapsLog(" + condition[3] + ")";// 17+14

        ModelAndView mav = new ModelAndView("jsonView");

        List<Object> result = null;
        try {
            Map<String, Object> conditionMap = new HashMap<String, Object>();
            conditionMap.put("searchStartDate", condition[0]);
            conditionMap.put("searchEndDate", condition[1]);
            conditionMap.put("searchDateType", condition[2]);
            conditionMap.put("meterType", condition[3]);
            conditionMap.put("supplierId", condition[4]);
            conditionMap.put("mdsId", condition[5]);
            conditionMap.put("deviceType", condition[6]);
            conditionMap.put("deviceId", condition[7]);

            result = (List<Object>) dataGapsManager.getLpMissingMetersExcel(conditionMap);

            total = new Integer(result.size()).longValue();

            mav.addObject("total", total);
            if (total <= 0) {
                return mav;
            }

            sbFileName.append(logPrefix);

            sbFileName.append(TimeUtil.getCurrentTimeMilli());

            // message 생성
            msgMap.put("no", fmtMessage[0]);
            msgMap.put("customerName", fmtMessage[1]);
            msgMap.put("deviceNo", fmtMessage[2]);
            msgMap.put("mdsId", fmtMessage[3]);
            msgMap.put("missingCnt", fmtMessage[4]);
            msgMap.put("lastReadDate", fmtMessage[5]);
            msgMap.put("title", fmtMessage[6]);

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

                        // 파일길이 : 30이상, 확장자 : xls|zip
                        if (filename.length() > 30 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
                            // 10일 지난 파일들 삭제
                            if (filename.startsWith(logPrefix) && filename.substring(17, 25).compareTo(deleteDate) < 0) {
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
            DataGapsMakeExcel wExcel = new DataGapsMakeExcel();
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
