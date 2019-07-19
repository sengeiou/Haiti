/**
 * ReportMgmtController.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.bo.system.report;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.ReportExportFormat;
import com.aimir.constants.CommonConstants.ReportFileDirectory;
import com.aimir.dao.system.SupplierDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Code;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.EmsReportManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ReportMgmtManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.CommonUtils;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * ReportMgmtController.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 9. 16.  v1.0        문동규   리포트 관리 View Controller
 *
 */
@Controller
public class ReportMgmtController {

    @Autowired
    ReportMgmtManager reportMgmtManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    SupplierManager supplierManager;

    /**** TEST START ****/
    @Autowired
    EmsReportManager emsReportManager;
    /**** TEST END ****/

    @Autowired
    SupplierDao supplierDao;

    @Autowired
    RoleManager roleManager;

     /**
     * method name : loadReportMgmtMini
     * method Desc : 리포트관리 미니가젯 화면을 로딩한다.
     *
     * @return
     */
    @RequestMapping(value="/gadget/system/reportMgmtMini")
    public ModelAndView loadReportMgmtMini() {
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        ModelAndView mav = new ModelAndView("/gadget/system/reportMgmtMini");

        mav.addObject("reportFileDir", ReportFileDirectory.ReportFile.getCode());
        String reportExportRealPath = request.getSession().getServletContext().getRealPath(ReportFileDirectory.ExportFile.getCode());

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());

        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);
        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)

        mav.addObject("reportExportDir", reportExportRealPath.replace('\\', '/'));

        return mav;
    }

    /**
     * method name : loadReportMgmtMax
     * method Desc : 리포트관리 맥스가젯 화면을 로딩한다.
     *
     * @return
     */
    @RequestMapping(value="/gadget/system/reportMgmtMax")
    public ModelAndView loadReportMgmtMax() {
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        ModelAndView mav = new ModelAndView("/gadget/system/reportMgmtMax");

        mav.addObject("reportFileDir", ReportFileDirectory.ReportFile.getCode());
        String reportExportRealPath = request.getSession().getServletContext().getRealPath(ReportFileDirectory.ExportFile.getCode());

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());

        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);
        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)

        mav.addObject("reportExportDir", reportExportRealPath.replace('\\', '/'));

        return mav;
    }

    /**
     * method name : getReportMiniSelectBoxData
     * method Desc : Report 관리 미니가젯 SelectBox 데이터를 가져온다.
     *
     * @return
     */
    @RequestMapping(value="/gadget/system/getReportMiniSelectBoxData")
    public ModelAndView getReportMiniSelectBoxData() {
        ModelAndView mav = new ModelAndView("jsonView");
        List<Code> meterType = codeManager.getChildCodes(Code.METER_TYPE);
        
        List<Map<String, Object>> expformat = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        ReportExportFormat[] datas = ReportExportFormat.values();

        for (ReportExportFormat ef : datas) {
            map = new HashMap<String, Object>();
            map.put("id", ef.toString());
            map.put("name", ef.toString());
            expformat.add(map);
        }
        
        mav.addObject("meterType", meterType);
        mav.addObject("exportFormat", expformat);
        
        return mav;
    }

    /**
     * method name : getReportResultList<b/>
     * method Desc : Report 관리 미니가젯에서 스케줄 실행 결과를 조회한다.
     *
     * @param reportName
     * @param searchStartDate
     * @param searchEndDate
     * @return
     */
    @RequestMapping(value = "/gadget/system/getReportResultList")
    public ModelAndView getReportResultList(@RequestParam("reportName") String reportName,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        conditionMap.put("reportName", reportName);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);

        try {
            if(startDate.length() == 0 && startDate.length() == 0) {
                conditionMap.put("startDate", TimeUtil.getCurrentDay());
                conditionMap.put("endDate", TimeUtil.getCurrentDay());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          

        conditionMap.put("supplierId", user.getRoleData().getSupplier().getId());
        conditionMap.put("operatorId", (int)user.getAccountId());
        conditionMap.put("roleId", user.getRoleData().getId());

        List<Map<String, Object>> result = reportMgmtManager.getReportResultList(conditionMap);

        mav.addObject("result", result);
        mav.addObject("totalCount", reportMgmtManager.getReportResultListTotalCount(conditionMap));

        return mav;
    }

    /**
     * method name : notifyEmergencyCredit
     * method Desc : 선택한 선불고객들에게 Emergency Credit Mode 정보를 통보한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/system/deleteReportResult")
    public ModelAndView deleteReportResult(@RequestParam("checkedData") String checkedData) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        String[] resultIds = checkedData.split(",");

        if (resultIds == null || resultIds.length == 0) {
            mav.addObject("result", "fail");
            return mav;
        }

        List<Integer> resultIdList = new ArrayList<Integer>();
        int count = resultIds.length;

        for (int i = 0 ; i < count ; i++) {
            resultIdList.add(new Integer(resultIds[i]));
        }
        
        conditionMap.put("resultIds", resultIdList);

        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        String dataFileDir = request.getSession().getServletContext().getRealPath(ReportFileDirectory.ReportData.getCode());
        String exportFileDir = request.getSession().getServletContext().getRealPath(ReportFileDirectory.ExportFile.getCode());

        conditionMap.put("dataFileDir", dataFileDir);
        conditionMap.put("exportFileDir", exportFileDir);

        // delete
        reportMgmtManager.deleteReportResult(conditionMap);

        mav.addObject("result", "success");

        return mav;
    }
    
    /**
     * method name : getReportTreeData<b/>
     * method Desc : Report 관리 미니가젯에서 Report 트리 데이터를 조회한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/system/getReportTreeData")
    public ModelAndView getReportTreeData() {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          

        conditionMap.put("supplierId", user.getRoleData().getSupplier().getId());
        conditionMap.put("operatorId", (int)user.getAccountId());
        conditionMap.put("roleId", user.getRoleData().getId());

//        List<Location> result = reportMgmtManager.getReportTreeData(conditionMap);
        List<Map<String, Object>> result = reportMgmtManager.getReportTreeData(conditionMap);

        mav.addObject("result", result);

        return mav;
    }

    /**
     * method name : getReportContactsGroupComboData
     * method Desc : Report 관리 Email Contacts Group SelectBox 데이터를 가져온다.
     *
     * @return
     */
    @RequestMapping(value="/gadget/system/getReportContactsGroupComboData")
    public ModelAndView getReportContactsGroupComboData() {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          

        conditionMap.put("operatorId", (int)user.getAccountId());

        List<Map<String, Object>> result = reportMgmtManager.getReportContactsGroupComboData(conditionMap);

        mav.addObject("result", result);
        
        return mav;
    }

    /**
     * method name : getReportResultList<b/>
     * method Desc : Report 관리 미니가젯에서 Email Contacts Group List 를 조회한다.
     *
     * @param reportName
     * @param searchStartDate
     * @param searchEndDate
     * @return
     */
    @RequestMapping(value = "/gadget/system/getReportContactsGroupList")
    public ModelAndView getReportContactsGroupList() {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          

//        conditionMap.put("supplierId", user.getRoleData().getSupplier().getId());
        conditionMap.put("operatorId", (int)user.getAccountId());
//        conditionMap.put("roleId", user.getRoleData().getId());

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        mav.addObject("result", result);
        return mav;
    }

    /**
     * method name : getReportContactsList<b/>
     * method Desc : Report 관리에서 Email Contacts 리스트를 조회한다.
     *
     * @param reportName
     * @param searchStartDate
     * @param searchEndDate
     * @return
     */
    @RequestMapping(value = "/gadget/system/getReportContactsList")
    public ModelAndView getReportContactsList(@RequestParam("searchType") String searchType,
            @RequestParam("searchValue") String searchValue) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        conditionMap.put("searchType", searchType);
        conditionMap.put("searchValue", searchValue);

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          

//        conditionMap.put("supplierId", user.getRoleData().getSupplier().getId());
        conditionMap.put("operatorId", (int)user.getAccountId());
//        conditionMap.put("roleId", user.getRoleData().getId());

        List<Map<String, Object>> result = reportMgmtManager.getReportContactsList(conditionMap);

        mav.addObject("result", result);
        mav.addObject("totalCount", reportMgmtManager.getReportContactsListTotalCount(conditionMap));

        return mav;
    }

    /**
     * method name : saveReportContactsGroup
     * method Desc : Report 관리 화면의 Email Contacts Group 정보를 저장한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/system/saveReportContactsGroup")
    public ModelAndView saveReportContactsGroup(@RequestParam("groupId") Integer groupId,
            @RequestParam("groupName") String groupName) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        conditionMap.put("groupId", groupId);
        conditionMap.put("groupName", groupName);

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          

        conditionMap.put("operatorId", (int)user.getAccountId());

        if (!StringUtil.nullToBlank(groupId).isEmpty()) {
            reportMgmtManager.updateReportContactsGroup(conditionMap);
        } else {
            reportMgmtManager.insertReportContactsGroup(conditionMap);
        }
        mav.addObject("result", "success");

        return mav;
    }

    /**
     * method name : deleteReportContactsGroup
     * method Desc : Report 관리 화면의 Email Contacts Group 정보를 저장한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/system/deleteReportContactsGroup")
    public ModelAndView deleteReportContactsGroup(@RequestParam("groupId") Integer groupId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        if (groupId == null || groupId == 0) {
            mav.addObject("result", "fail");
            return mav;
        }

        conditionMap.put("groupId", groupId);

        // delete
        reportMgmtManager.deleteReportContactsGroup(conditionMap);

        mav.addObject("result", "success");

        return mav;
    }

    /**
     * method name : insertReportContactsData
     * method Desc : Report 관리 화면의 Email Contacts 정보를 등록한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/system/insertReportContactsData")
    public ModelAndView insertReportContactsData(@RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("groupId") Integer groupId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        conditionMap.put("name", name);
        conditionMap.put("email", email);
        conditionMap.put("groupId", groupId);

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          

        conditionMap.put("operatorId", (int)user.getAccountId());

        // insert
        reportMgmtManager.insertReportContactsData(conditionMap);
        mav.addObject("result", "success");

        return mav;
    }

    /**
     * method name : updateReportContactsData
     * method Desc : Report 관리 화면의 Email Contacts 정보를 저장한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/system/updateReportContactsData")
    public ModelAndView updateReportContactsData(@RequestParam("contactsId") Integer contactsId,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("groupId") Integer groupId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        conditionMap.put("contactsId", contactsId);
        conditionMap.put("name", name);
        conditionMap.put("email", email);
        conditionMap.put("groupId", groupId);

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          

        conditionMap.put("operatorId", (int)user.getAccountId());

        // update
        reportMgmtManager.updateReportContactsData(conditionMap);
        mav.addObject("result", "success");

        return mav;
    }

    /**
     * method name : deleteReportContactsData
     * method Desc : Report 관리 화면의 Email Contacts 정보를 삭제한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/system/deleteReportContactsData")
    public ModelAndView deleteReportContactsData(@RequestParam("contactsId") Integer contactsId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        if (contactsId == null || contactsId == 0) {
            mav.addObject("result", "fail");
            return mav;
        }

        conditionMap.put("contactsId", contactsId);

        // delete
        int delCnt = reportMgmtManager.deleteReportContactsData(conditionMap);

        if (delCnt > 0) {
            mav.addObject("result", "success");
        } else {
            mav.addObject("result", "fail");
        }

        return mav;
    }

    /**
     * method name : insertReportSchedule
     * method Desc : Report 관리 화면의 Report Schedule 정보를 등록한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/system/insertReportSchedule")
    public ModelAndView insertReportSchedule(@RequestParam("reportId") Integer reportId,
            @RequestParam("scheduleName") String scheduleName,
            @RequestParam("cronFormat") String cronFormat,
            @RequestParam("exportFormat") String exportFormat,
            @RequestParam("useEmailYn") Boolean useEmailYn,
            @RequestParam("email") String email,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("meterType") String meterType) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        conditionMap.put("reportId", reportId);
        conditionMap.put("scheduleName", scheduleName);
        conditionMap.put("cronFormat", cronFormat);
        conditionMap.put("exportFormat", exportFormat);
        conditionMap.put("useEmailYn", useEmailYn);
        conditionMap.put("email", email);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("locationId", locationId);
        conditionMap.put("meterType", meterType);

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          

        conditionMap.put("operatorId", (int)user.getAccountId());

        // insert
        reportMgmtManager.insertReportSchedule(conditionMap);
        mav.addObject("result", "success");

        return mav;
    }

    /**
     * method name : updateReportSchedule
     * method Desc : Report 관리 화면의 Report Schedule 정보를 수정한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/system/updateReportSchedule")
    public ModelAndView updateReportSchedule(@RequestParam("scheduleId") Integer scheduleId,
            @RequestParam("scheduleName") String scheduleName,
            @RequestParam("cronFormat") String cronFormat,
            @RequestParam("exportFormat") String exportFormat,
            @RequestParam("useEmailYn") Boolean useEmailYn,
            @RequestParam("email") String email,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("meterType") String meterType) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        conditionMap.put("scheduleId", scheduleId);
        conditionMap.put("scheduleName", scheduleName);
        conditionMap.put("cronFormat", cronFormat);
        conditionMap.put("exportFormat", exportFormat);
        conditionMap.put("useEmailYn", useEmailYn);
        conditionMap.put("email", email);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("locationId", locationId);
        conditionMap.put("meterType", meterType);

        // update
        reportMgmtManager.updateReportSchedule(conditionMap);
        mav.addObject("result", "success");

        return mav;
    }

    /**
     * method name : deleteReportContactsData
     * method Desc : Report 관리 화면에서 Report Schedule 정보를 삭제한다.
     *
     * @return
     */
    @RequestMapping(value = "/gadget/system/deleteReportSchedule")
    public ModelAndView deleteReportSchedule(@RequestParam("scheduleId") Integer scheduleId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        if (scheduleId == null || scheduleId == 0) {
            mav.addObject("result", "fail");
            return mav;
        }

        conditionMap.put("scheduleId", scheduleId);

        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        // Report Data File Directory
        String dataFileDir = request.getSession().getServletContext().getRealPath(ReportFileDirectory.ReportData.getCode());
        // Report Export File Directory
        String exportFileDir = request.getSession().getServletContext().getRealPath(ReportFileDirectory.ExportFile.getCode());

        conditionMap.put("dataFileDir", dataFileDir);
        conditionMap.put("exportFileDir", exportFileDir);

        // delete
        reportMgmtManager.deleteReportSchedule(conditionMap);

        mav.addObject("result", "success");

        return mav;
    }

    /**
     * method name : getReportScheduleList<b/>
     * method Desc : Report 관리 미니가젯에서 스케줄 리스트를 조회한다.
     *
     * @param reportName
     * @param searchStartDate
     * @param searchEndDate
     * @return
     */
    @RequestMapping(value = "/gadget/system/getReportScheduleList")
    public ModelAndView getReportScheduleList(@RequestParam("reportId") Integer reportId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        conditionMap.put("reportId", reportId);
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          

        conditionMap.put("supplierId", user.getRoleData().getSupplier().getId());
        conditionMap.put("operatorId", (int)user.getAccountId());

        List<Map<String, Object>> result = reportMgmtManager.getReportScheduleList(conditionMap);

        mav.addObject("result", result);
        mav.addObject("totalCount", reportMgmtManager.getReportScheduleListTotalCount(conditionMap));

        return mav;
    }

    /**
     * method name : getReportScheduleResultList<b/>
     * method Desc : Report 관리 맥스가젯에서 스케줄 실행 결과를 조회한다.
     *
     * @param reportName
     * @param searchStartDate
     * @param searchEndDate
     * @return
     */
    @RequestMapping(value = "/gadget/system/getReportScheduleResultList")
    public ModelAndView getReportScheduleResultList(@RequestParam("scheduleId") Integer scheduleId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        conditionMap.put("scheduleId", scheduleId);
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        try {
            if(startDate.length() == 0 && startDate.length() == 0) {
                conditionMap.put("startDate", TimeUtil.getCurrentDay());
                conditionMap.put("endDate", TimeUtil.getCurrentDay());
            } else {
                conditionMap.put("startDate", startDate);
                conditionMap.put("endDate", endDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          

        conditionMap.put("supplierId", user.getRoleData().getSupplier().getId());
        conditionMap.put("operatorId", (int)user.getAccountId());

        List<Map<String, Object>> result = reportMgmtManager.getReportScheduleResultList(conditionMap);

        mav.addObject("result", result);
        mav.addObject("totalCount", reportMgmtManager.getReportScheduleResultListTotalCount(conditionMap));

        return mav;
    }

    /**
     * method name : getReportParameterFormatDate
     * method Desc : Report 관리 화면에서 Report Parameter 기간조건 데이터를 일자 formatting 된 데이터로 변환한다.
     *
     * @return
     */
    @RequestMapping(value="/gadget/system/getReportParameterFormatDate")
    public ModelAndView getReportParameterFormatDate(@RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        ModelAndView mav = new ModelAndView("jsonView");

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          

        Supplier supplier = supplierManager.getSupplier(user.getRoleData().getSupplier().getId());
        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();

        mav.addObject("startDate", TimeLocaleUtil.getLocaleDate(startDate, lang, country));
        mav.addObject("endDate", TimeLocaleUtil.getLocaleDate(endDate, lang, country));
        
        return mav;
    }

    /******************************************************************************************************************************************
     **** TEST REPORT CREATE START ************************************************************************************************************
     ******************************************************************************************************************************************/

    @RequestMapping(value = "/report/energySavingTemp")
    public ModelAndView getEnergySavingTempReport(HttpServletResponse response, HttpServletRequest request,
            @RequestParam("supplierId") String supplierId, @RequestParam("searchYear") String searchYear) {

        ModelAndView mav = new ModelAndView("report/reportData");
        String xmlString = "";

        String reportFileName;
        boolean hasLink = false;
        reportFileName = request.getParameter("link");

        if (!StringUtil.nullToBlank(reportFileName).isEmpty() && !StringUtil.nullToBlank(reportFileName).equals("null")) {
            hasLink = true;
        } else {
            reportFileName = "energyReport" + searchYear + "_" + supplierId + "." + new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()) + ".rpt";
        }

        if (isExistFile(request, reportFileName)) {
            File reportFile = getReportFile(request, reportFileName);
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(reportFile));
                String readLine = "";
                while ((readLine = br.readLine()) != null) {
                    xmlString += readLine + "\n";
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null)
                        br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (!hasLink) {
            File reportFile = getReportFile(request, reportFileName);
            BufferedWriter bw = null;

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("supplierId", supplierId);
            params.put("searchYear", searchYear);

            Map<String, Object> resultMap = emsReportManager.getEnergySavingReportInfo(params);

            Element monthlyEl = genElementMonthlyEnergySaving((Map<String, Object>) resultMap.get("Monthly"), "Monthly");
            Element yearlyEl = genElementYearlyEnergySaving((Map<String, Object>) resultMap.get("Yearly"), "Yearly");

            Element master = new Element("EnergySavingReport");

            master.addContent(monthlyEl);
            master.addContent(yearlyEl);

            xmlString = getXmlString(master);

            try {
                bw = new BufferedWriter(new FileWriter(reportFile));
                bw.write(xmlString);
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    if (bw != null)
                        bw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        mav.addObject("data", xmlString);

        return mav;
    }

    private String getXmlString(Element element){
        
        XMLOutputter xmlOut = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        xmlOut.setFormat(format);
        
        String xmlStr = xmlOut.outputString(element);
        
        return xmlStr;
    }

    public Element genElementMonthlyEnergySaving(Map<String,Object> data, String name){
        List<Object> emMonthlyReportList = (List<Object>)data.get("emMonthlyReport");
        List<Object> wmMonthlyReportList = (List<Object>)data.get("wmMonthlyReport");
        List<Object> gmMonthlyReportList = (List<Object>)data.get("gmMonthlyReport");
        
        
        Element el = new Element(name);
        
        // 전기
        Element emMonthlyReportEl = new Element("emMonthlyReport");
        Map<String,Object> emMonthlyReportData = null;
        for(Object obj:emMonthlyReportList){
            emMonthlyReportData = (Map<String,Object>)obj;
            Element row = new Element("Row");
            row.setAttribute("month",   (String)emMonthlyReportData.get("month"));
            row.setAttribute("usage",   (String)emMonthlyReportData.get("usage"));
            row.setAttribute("goalUsage",   (String)emMonthlyReportData.get("goalUsage"));
            row.setAttribute("toe",     (String)emMonthlyReportData.get("toe"));
            row.setAttribute("goalToe",     (String)emMonthlyReportData.get("goalToe"));
            row.setAttribute("reduceUsageRate",     (String)emMonthlyReportData.get("reduceUsageRate"));
            row.setAttribute("reduceCo2",   (String)emMonthlyReportData.get("reduceCo2"));
            row.setAttribute("effectedUsage",   (String)emMonthlyReportData.get("effectedUsage"));
            row.setAttribute("effectedToe",     (String)emMonthlyReportData.get("effectedToe"));
            row.setAttribute("goal",    (String)emMonthlyReportData.get("goal"));
            emMonthlyReportEl.addContent(row);
        }
        
        // 수도
        Element wmMonthlyReportEl = new Element("wmMonthlyReport");
        Map<String,Object> wmMonthlyReportData = null;
        for(Object obj:wmMonthlyReportList){
            wmMonthlyReportData = (Map<String,Object>)obj;
            Element row = new Element("Row");
            row.setAttribute("month",   (String)wmMonthlyReportData.get("month"));
            row.setAttribute("usage",   (String)wmMonthlyReportData.get("usage"));
            row.setAttribute("goalUsage",   (String)wmMonthlyReportData.get("goalUsage"));
            row.setAttribute("toe",     (String)wmMonthlyReportData.get("toe"));
            row.setAttribute("goalToe",     (String)wmMonthlyReportData.get("goalToe"));
            row.setAttribute("reduceUsageRate",     (String)wmMonthlyReportData.get("reduceUsageRate"));
            row.setAttribute("reduceCo2",   (String)wmMonthlyReportData.get("reduceCo2"));
            row.setAttribute("effectedUsage",   (String)wmMonthlyReportData.get("effectedUsage"));
            row.setAttribute("effectedToe",     (String)wmMonthlyReportData.get("effectedToe"));
            row.setAttribute("goal",    (String)wmMonthlyReportData.get("goal"));
            wmMonthlyReportEl.addContent(row);
        }
        
        // 가스
        Element gmMonthlyReportEl = new Element("gmMonthlyReport");
        Map<String,Object> gmMonthlyReportData = null;
        for(Object obj:gmMonthlyReportList){
            gmMonthlyReportData = (Map<String,Object>)obj;
            Element row = new Element("Row");
            row.setAttribute("month",   (String)gmMonthlyReportData.get("month"));
            row.setAttribute("usage",   (String)gmMonthlyReportData.get("usage"));
            row.setAttribute("goalUsage",   (String)gmMonthlyReportData.get("goalUsage"));
            row.setAttribute("toe",     (String)gmMonthlyReportData.get("toe"));
            row.setAttribute("goalToe",     (String)gmMonthlyReportData.get("goalToe"));
            row.setAttribute("reduceUsageRate",     (String)gmMonthlyReportData.get("reduceUsageRate"));
            row.setAttribute("reduceCo2",   (String)gmMonthlyReportData.get("reduceCo2"));
            row.setAttribute("effectedUsage",   (String)gmMonthlyReportData.get("effectedUsage"));
            row.setAttribute("effectedToe",     (String)gmMonthlyReportData.get("effectedToe"));
            row.setAttribute("goal",    (String)gmMonthlyReportData.get("goal"));
            gmMonthlyReportEl.addContent(row);
        }
        
        
        el.addContent(emMonthlyReportEl);
        el.addContent(wmMonthlyReportEl);
        el.addContent(gmMonthlyReportEl);
        
        return el;
    }

    public Element genElementYearlyEnergySaving(Map<String,Object> data, String name){
        List<Object> emYearlyReportList = (List<Object>)data.get("emYearlyReport");
        List<Object> wmYearlyReportList = (List<Object>)data.get("wmYearlyReport");
        List<Object> gmYearlyReportList = (List<Object>)data.get("gmYearlyReport");
        
        
        Element el = new Element(name);
        
        // 전기
        Element emYearlyReportEl = new Element("emYearlyReport");
        Map<String,Object> emYearlyReportData = null;
        for(Object obj:emYearlyReportList){
            emYearlyReportData = (Map<String,Object>)obj;
            Element row = new Element("Row");
            row.setAttribute("year",    (String)emYearlyReportData.get("year"));
            row.setAttribute("usage",   (String)emYearlyReportData.get("usage"));
            row.setAttribute("goalUsage",   (String)emYearlyReportData.get("goalUsage"));
            row.setAttribute("toe",     (String)emYearlyReportData.get("toe"));
            row.setAttribute("goalToe",     (String)emYearlyReportData.get("goalToe"));
            row.setAttribute("reduceUsageRate",     (String)emYearlyReportData.get("reduceUsageRate"));
            row.setAttribute("reduceCo2",   (String)emYearlyReportData.get("reduceCo2"));
            row.setAttribute("effectedUsage",   (String)emYearlyReportData.get("effectedUsage"));
            row.setAttribute("effectedToe",     (String)emYearlyReportData.get("effectedToe"));
            row.setAttribute("period",  (String)emYearlyReportData.get("period"));
            row.setAttribute("avgUsage",    (String)emYearlyReportData.get("avgUsage"));
            row.setAttribute("goal",    (String)emYearlyReportData.get("goal"));
            emYearlyReportEl.addContent(row);
        }
        
        Properties prop = new Properties();
        Supplier supplier = supplierDao.get(Integer.parseInt(data.get("supplierId").toString()));
		String lang = supplier.getLang().getCode_2letter();

        if(lang.equals(Locale.KOREAN.toString())){
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
        
        Element emYearlyChartEl = new Element("emYearlyChart");
        if(emYearlyReportList.size() > 0) {
            Map<String,Object> emYearlyChartData = (Map<String,Object>) emYearlyReportList.get(0);
            Element row = new Element("Row");
            row.setAttribute("xField",  (String)emYearlyChartData.get("period") + prop.get("aimir.avg.year"));
            row.setAttribute("value",   (String)emYearlyChartData.get("avgUsage"));
            row.setAttribute("order",   "1");
            emYearlyChartEl.addContent(row);
            
            row = new Element("Row");
            row.setAttribute("xField",  prop.get("aimir.goal").toString());
            row.setAttribute("value",   (String)emYearlyChartData.get("goalUsage"));
            row.setAttribute("order",   "2");
            emYearlyChartEl.addContent(row);
            
            row = new Element("Row");
            row.setAttribute("xField",  prop.get("aimir.prediction").toString());
            row.setAttribute("value",   "0");
            row.setAttribute("order",   "3");
            emYearlyChartEl.addContent(row);
            
            row = new Element("Row");
            row.setAttribute("xField",  (String)emYearlyChartData.get("year"));
            row.setAttribute("value",   (String)emYearlyChartData.get("usage"));
            row.setAttribute("order",   "4");
            emYearlyChartEl.addContent(row);
        }
        
        
        // 수도
        Element wmYearlyReportEl = new Element("wmYearlyReport");
        Map<String,Object> wmYearlyReportData = null;
        for(Object obj:wmYearlyReportList){
            wmYearlyReportData = (Map<String,Object>)obj;
            Element row = new Element("Row");
            row.setAttribute("year",    (String)wmYearlyReportData.get("year"));
            row.setAttribute("usage",   (String)wmYearlyReportData.get("usage"));
            row.setAttribute("goalUsage",   (String)wmYearlyReportData.get("goalUsage"));
            row.setAttribute("toe",     (String)wmYearlyReportData.get("toe"));
            row.setAttribute("goalToe",     (String)wmYearlyReportData.get("goalToe"));
            row.setAttribute("reduceUsageRate",     (String)wmYearlyReportData.get("reduceUsageRate"));
            row.setAttribute("reduceCo2",   (String)wmYearlyReportData.get("reduceCo2"));
            row.setAttribute("effectedUsage",   (String)wmYearlyReportData.get("effectedUsage"));
            row.setAttribute("effectedToe",     (String)wmYearlyReportData.get("effectedToe"));
            row.setAttribute("period",  (String)wmYearlyReportData.get("period"));
            row.setAttribute("avgUsage",    (String)wmYearlyReportData.get("avgUsage"));
            row.setAttribute("goal",    (String)wmYearlyReportData.get("goal"));
            wmYearlyReportEl.addContent(row);
        }
        
        Element wmYearlyChartEl = new Element("wmYearlyChart");
        if(wmYearlyReportList.size() > 0) {
            Map<String,Object> wmYearlyChartData = (Map<String,Object>) wmYearlyReportList.get(0);
            Element row = new Element("Row");
            row.setAttribute("xField",  (String)wmYearlyChartData.get("period") + prop.get("aimir.avg.year"));            row.setAttribute("value",   (String)wmYearlyChartData.get("avgUsage"));
            row.setAttribute("order",   "1");
            wmYearlyChartEl.addContent(row);
            
            row = new Element("Row");
            row.setAttribute("xField",  prop.get("aimir.goal").toString());
            row.setAttribute("value",   (String)wmYearlyChartData.get("goalUsage"));
            row.setAttribute("order",   "2");
            wmYearlyChartEl.addContent(row);
            
            row = new Element("Row");
            row.setAttribute("xField",  prop.get("aimir.prediction").toString());
            row.setAttribute("value",   "0");
            row.setAttribute("order",   "3");
            wmYearlyChartEl.addContent(row);            
            
            row = new Element("Row");
            row.setAttribute("xField",  (String)wmYearlyChartData.get("year"));
            row.setAttribute("value",   (String)wmYearlyChartData.get("usage"));
            row.setAttribute("order",   "4");
            wmYearlyChartEl.addContent(row);
        }
        
        // 가스
        Element gmYearlyReportEl = new Element("gmYearlyReport");
        Map<String,Object> gmYearlyReportData = null;
        for(Object obj:gmYearlyReportList){
            gmYearlyReportData = (Map<String,Object>)obj;
            Element row = new Element("Row");
            row.setAttribute("year",    (String)gmYearlyReportData.get("year"));
            row.setAttribute("usage",   (String)gmYearlyReportData.get("usage"));
            row.setAttribute("goalUsage",   (String)gmYearlyReportData.get("goalUsage"));
            row.setAttribute("toe",     (String)gmYearlyReportData.get("toe"));
            row.setAttribute("goalToe",     (String)gmYearlyReportData.get("goalToe"));
            row.setAttribute("reduceUsageRate",     (String)gmYearlyReportData.get("reduceUsageRate"));
            row.setAttribute("reduceCo2",   (String)gmYearlyReportData.get("reduceCo2"));
            row.setAttribute("effectedUsage",   (String)gmYearlyReportData.get("effectedUsage"));
            row.setAttribute("effectedToe",     (String)gmYearlyReportData.get("effectedToe"));
            row.setAttribute("period",  (String)gmYearlyReportData.get("period"));
            row.setAttribute("avgUsage",    (String)gmYearlyReportData.get("avgUsage"));
            row.setAttribute("goal",    (String)gmYearlyReportData.get("goal"));
            gmYearlyReportEl.addContent(row);
        }
        
        Element gmYearlyChartEl = new Element("gmYearlyChart");
        if(gmYearlyReportList.size() > 0) {
            Map<String,Object> gmYearlyChartData = (Map<String,Object>) gmYearlyReportList.get(0);
            Element row = new Element("Row");
            row.setAttribute("xField",  (String)gmYearlyChartData.get("period") + prop.get("aimir.avg.year"));
            row.setAttribute("value",   (String)gmYearlyChartData.get("avgUsage"));
            row.setAttribute("order",   "1");
            gmYearlyChartEl.addContent(row);
            
            row = new Element("Row");
            row.setAttribute("xField",  prop.get("aimir.goal").toString());
            row.setAttribute("value",   (String)gmYearlyChartData.get("goalUsage"));
            row.setAttribute("order",   "2");
            gmYearlyChartEl.addContent(row);
            
            row = new Element("Row");
            row.setAttribute("xField",  prop.get("aimir.prediction").toString());
            row.setAttribute("value",   "0");
            row.setAttribute("order",   "3");
            gmYearlyChartEl.addContent(row);
            
            row = new Element("Row");
            row.setAttribute("xField",  (String)gmYearlyChartData.get("year"));
            row.setAttribute("value",   (String)gmYearlyChartData.get("usage"));
            row.setAttribute("order",   "4");
            gmYearlyChartEl.addContent(row);
        }
        
        
        el.addContent(emYearlyReportEl);
        el.addContent(emYearlyChartEl);
        el.addContent(wmYearlyReportEl);
        el.addContent(wmYearlyChartEl);
        el.addContent(gmYearlyReportEl);
        el.addContent(gmYearlyChartEl);
        
        return el;
    }
    
    public boolean isExistFile(HttpServletRequest request, String filename) {
//        String reportFilePath = "/EmsReport/";
        String reportFilePath = ReportFileDirectory.ReportData.getCode();
        String destDir = request.getSession().getServletContext().getRealPath(reportFilePath);      
        try {
            File dirPath = new File(destDir);
            if (!dirPath.exists()) {
                boolean created = dirPath.mkdirs();
                if (!created) {
                    throw new Exception(
                            "Fail to create a directory for product image. ["
                                    + destDir + "]");
                }
            }
//            File file = new File(destDir, filename + ".rpt");
            File file = new File(destDir, filename);
            if(!file.exists()) {
                try{
                    file.createNewFile();
                    return false;
                }catch(IOException io){
                    io.printStackTrace();
                }
            } else {
                return true;
            }           
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public File getReportFile(HttpServletRequest request, String filename) {
        //String reportFilePath = "/EmsReport/";
        String reportFilePath = ReportFileDirectory.ReportData.getCode();
        String destDir = request.getSession().getServletContext().getRealPath(reportFilePath);
        
        File file = null;
        try {
//            file = new File(destDir, filename + ".rpt");
            file = new File(destDir, filename);
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return file;
    }

    /*******************************************************************************************************************************************
     **** TEST REPORT CREATE END ***************************************************************************************************************
     *******************************************************************************************************************************************/

    /*******************************************************************************************************************************************
     **** TEST REPORT_RESULT INSERT START ******************************************************************************************************
     *******************************************************************************************************************************************
    @RequestMapping(value = "/gadget/system/testInsertReportScheduleResult")
    public ModelAndView testInsertReportScheduleResult(@RequestParam("scheduleId") Integer scheduleId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        conditionMap.put("scheduleId", scheduleId);

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          

        conditionMap.put("operatorId", (int)user.getAccountId());

        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        String dataFileDir = request.getSession().getServletContext().getRealPath(ReportFileDirectory.ReportData.getCode());
        String exportFileDir = request.getSession().getServletContext().getRealPath(ReportFileDirectory.ExportFile.getCode());

        conditionMap.put("dataFileDir", dataFileDir);
        conditionMap.put("exportFileDir", exportFileDir);

        // insert
        reportMgmtManager.testInsertReportScheduleResult(conditionMap);
        mav.addObject("result", "success");

        return mav;
    }
    *****************************************************************************************************************************************
     **** TEST REPORT_RESULT INSERT END ******************************************************************************************************
     *****************************************************************************************************************************************/
}