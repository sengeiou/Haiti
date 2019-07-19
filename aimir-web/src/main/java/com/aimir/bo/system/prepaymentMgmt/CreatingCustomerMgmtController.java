/**
 * CreatingCustomerMgmtController.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.bo.system.prepaymentMgmt;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Role;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.prepayment.CreatingCustomerMgmtManager;
import com.aimir.support.FileUploadHelper;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils;
import com.aimir.util.CreatingCustomerMgmtMakeExcel;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

/**
 * PrepaymentChargeController.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2013. 10. 28. v1.0        문동규   선불 개통 고객 정보 입력 Controller
 * </pre>
 */
@Controller
public class CreatingCustomerMgmtController {

    private static Log logger = LogFactory.getLog(CreatingCustomerMgmtController.class);

    @Autowired
    CreatingCustomerMgmtManager creatingCustomerMgmtManager;

    @Autowired
    ContractManager contractManager;

    @Autowired
    RoleManager roleManager;

    /**
     * method name : loadCreatingCustomerMgmtMaxGadget<b/>
     * method Desc : Max Gadget 페이지 로딩
     *
     * @return
     */
    @RequestMapping(value="/gadget/prepaymentMgmt/creatingCustomerMgmtMaxGadget")
    public ModelAndView loadCreatingCustomerMgmtMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/creatingCustomerMgmtMaxGadget");
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();
        mav.addObject("supplierId", supplierId);

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        return mav;
    }

    /**
     * method name : saveCreatingCustomer<b/>
     * method Desc : Creating Customer Manager 가젯에서 고객정보를 저장한다.
     *
     * @param data
     * @param supplierId
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/saveCreatingCustomer")
    public ModelAndView saveCreatingCustomer(@RequestParam("customerNo") String customerNo,
            @RequestParam("customerName") String customerName,
            @RequestParam("contractNumber") String contractNumber,
            @RequestParam("telephoneNo") String telephoneNo,
            @RequestParam("mobileNo") String mobileNo,
            @RequestParam("email") String email,
            @RequestParam("barcode") String barcode,
            @RequestParam("supplierId") Integer supplierId) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("customerNo", customerNo);
        conditionMap.put("customerName", customerName);
        conditionMap.put("contractNumber", contractNumber);
        conditionMap.put("telephoneNo", telephoneNo);
        conditionMap.put("mobileNo", mobileNo);
        conditionMap.put("email", email);
        conditionMap.put("barcode", barcode);

        creatingCustomerMgmtManager.saveCreatingCustomer(conditionMap);
        mav.addObject("result", "success");

        return mav;
    }

    /**
     * method name : saveBulkCreatingCustomer<b/>
     * method Desc : Creating Customer Manager 가젯에서 고객정보를 엑셀파일로 받아서 저장한다.
     *
     * @param request
     * @param response
     * @return
     * @throws ServletRequestBindingException
     * @throws IOException
     */
    @RequestMapping(value="/gadget/prepaymentMgmt/saveBulkCreatingCustomer")
    public ModelAndView saveBulkCreatingCustomer(HttpServletRequest request, HttpServletResponse response)
                    throws ServletRequestBindingException, IOException {

        String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");

        MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multiReq.getFile("userfile");

        logger.debug("==== supplierId:" + multiReq.getParameter("supplierId"));

        Integer supplierId = Integer.valueOf(multiReq.getParameter("supplierId"));
        String filename = multipartFile.getOriginalFilename();
        if (filename == null || "".equals(filename))
            return null;

        String tempPath = contextRoot+"temp";

        if (!FileUploadHelper.exists(tempPath)) {
            File savedir = new File(tempPath);
            savedir.mkdir();
        }
        String fullpath = FileUploadHelper.makePath(tempPath, filename);
        File uFile = new File(fullpath);

        if (FileUploadHelper.exists(fullpath)) {
            logger.debug("file path:" + fullpath);
            if (FileUploadHelper.removeExistingFile(fullpath)) {
                multipartFile.transferTo(uFile);
            } else {
                Long longTime = TimeUtil.getCurrentLongTime();
                StringBuilder sbFilename = new StringBuilder();
                sbFilename.append(filename.substring(0, filename.lastIndexOf(".")));
                sbFilename.append("_");
                sbFilename.append(longTime.toString());
                sbFilename.append(".");
                sbFilename.append(filename.substring(filename.lastIndexOf(".")+1));
                fullpath = FileUploadHelper.makePath(tempPath, sbFilename.toString());
                uFile = new File(fullpath);
                multipartFile.transferTo(uFile);
            }
        } else {
            multipartFile.transferTo(uFile);
        }

        String ext = fullpath.substring(fullpath.lastIndexOf(".")+1).trim();

        Map<String, Object> result = null;
        if ("xls".equals(ext.toLowerCase())) {
            result = creatingCustomerMgmtManager.saveBulkCreatingCustomerByExcelXLS(fullpath, supplierId);
        } else if ("xlsx".equals(ext.toLowerCase())) {
            result = creatingCustomerMgmtManager.saveBulkCreatingCustomerByExcelXLSX(fullpath, supplierId);
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("status", result.get("status"));
        mav.addObject("errorList", result.get("errorList"));
        mav.addObject("errorListSize", result.get("errorListSize"));

        return mav;
    }

    /**
     * method name : sendCertificationSMS<b/>
     * method Desc : Creating Customer Manager 가젯에서 휴대폰번호를 인증한다.
     *
     * @param mobileNo
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/sendCertificationSMS")
    public ModelAndView sendCertificationSMS(@RequestParam("mobileNo") String mobileNo) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("mobileNo", mobileNo);

        Boolean result = creatingCustomerMgmtManager.sendCertificationSMS(conditionMap);
        mav.addObject("result", result.toString());

        return mav;
    }

    /**
     * method name : creatingCustomerExcelDownloadPopup<b/>
     * method Desc : Creating Customer Manager 가젯에서 오류리스트를 엑셀로 다운로드 받는 팝업창을 연다.
     *
     * @return
     */
    @RequestMapping(value="/gadget/prepaymentMgmt/creatingCustomerExcelDownloadPopup")
    public ModelAndView creatingCustomerExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/creatingCustomerExcelDownloadPopup");
        return mav;
    }

    /**
     * method name : getErrorListExportExcel<b/>
     * method Desc : Creating Customer Manager 가젯에서 오류리스트를 엑셀로 생성한다.
     *
     * @param errorList
     * @param headerMsg
     * @param filePath
     * @return
     */
    @RequestMapping(value = "/gadget/prepaymentMgmt/getErrorListExportExcel")
    public ModelAndView getErrorListExportExcel(@RequestParam("errorList[]") String[] errorList,
            @RequestParam("headerMsg[]") String[] headerMsg,
            @RequestParam("filePath") String filePath) {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<String> fileNameList = new ArrayList<String>();
        Map<String, String> headerMap = new HashMap<String, String>();
        Map<String, Object> map = null;

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        boolean isLast = false;
        Long total = 0L; // 데이터 조회건수
        Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

        final String logPrefix = "CreatingCustomerErrorList";   //25

        ModelAndView mav = new ModelAndView("jsonView");

        try {
            for (String record : errorList) {
                if (record != null) {
                    String[] colList = record.split("\\|");

                    if (colList != null && colList.length >= 5) {
                        map = new HashMap<String, Object>();
                        map.put("customerNo",     colList[0]);
                        map.put("customerName",   colList[1]);
                        map.put("contractNumber", colList[2]);
                        map.put("mobileNo",       colList[3]);
                        map.put("errMsg",         colList[4]);
                        result.add(map);
                    }
                }
            }

            sbFileName.append(logPrefix);
            sbFileName.append(TimeUtil.getCurrentTimeMilli());//14

            // message 생성
            headerMap.put("customerNo",     headerMsg[0]);
            headerMap.put("customerName",   headerMsg[1]);
            headerMap.put("contractNumber", headerMsg[2]);
            headerMap.put("mobileNo",       headerMsg[3]);
            headerMap.put("errMsg",         headerMsg[4]);

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

                        // 파일길이 : 39이상, 확장자 : xls|zip
                        if (filename.length() >= 39 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
                            // 10일 지난 파일들 삭제 : 실제 파일명에 맞춰서 수정
                            if (filename.startsWith(logPrefix) && filename.substring(25, 33).compareTo(deleteDate) < 0) {
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
            CreatingCustomerMgmtMakeExcel wExcel = new CreatingCustomerMgmtMakeExcel();
            int cnt = 1;
            int idx = 0;
            int fnum = 0;
            int splCnt = 0;

            if (total <= maxRows) {
                sbSplFileName = new StringBuilder();
                sbSplFileName.append(sbFileName);
                sbSplFileName.append(".xls");
                wExcel.writeReportExcel(result, headerMap, isLast, filePath, sbSplFileName.toString());
                fileNameList.add(sbSplFileName.toString());
            } else {
                for (int i = 0; i < total; i++) {
                    if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
                        sbSplFileName = new StringBuilder();
                        sbSplFileName.append(sbFileName);
                        sbSplFileName.append('(').append(++fnum).append(").xls");

                        list = result.subList(idx, (i + 1));

                        wExcel.writeReportExcel(list, headerMap, isLast, filePath, sbSplFileName.toString());
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
            logger.error(e, e);
        } catch (Exception e) {
            logger.error(e, e);
        }
        return mav;
    }
}