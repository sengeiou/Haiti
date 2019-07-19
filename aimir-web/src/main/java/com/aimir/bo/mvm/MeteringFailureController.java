package com.aimir.bo.mvm;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.MeteringFailureManager;
import com.aimir.service.mvm.bean.FailureMeterData;
import com.aimir.service.mvm.bean.MeteringFailureData;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils;
import com.aimir.util.DecimalUtil;
import com.aimir.util.MeteringFailureMakeExcel;
import com.aimir.util.ReflectionUtils;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

@Controller
public class MeteringFailureController {

    protected static Log logger = LogFactory.getLog(MeteringFailureController.class);

    @Autowired
    LocationManager locationManager;

    @Autowired
    MeteringFailureManager meteringFailureManager;

    @Autowired
    RoleManager roleManager;
    
    @Autowired
    SupplierManager supplierManager;

    @RequestMapping(value="/gadget/mvm/meteringFailureEmMiniGadget")
    public ModelAndView meteringFailureEmMiniGadget() {
        ModelAndView mav = new ModelAndView();

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        mav.setViewName("gadget/mvm/meteringFailureEmMiniGadget");
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/meteringFailureGmMiniGadget")
    public ModelAndView meteringFailureGmMiniGadget() {
        ModelAndView mav = new ModelAndView();

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        mav.setViewName("gadget/mvm/meteringFailureGmMiniGadget");
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/meteringFailureWmMiniGadget")
    public ModelAndView meteringFailureWmMiniGadget() {
        ModelAndView mav = new ModelAndView();

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        mav.setViewName("gadget/mvm/meteringFailureWmMiniGadget");
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/meteringFailureHmMiniGadget")
    public ModelAndView meteringFailureHmMiniGadget() {
        ModelAndView mav = new ModelAndView();

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        mav.setViewName("gadget/mvm/meteringFailureHmMiniGadget");
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/meteringFailureEmMaxGadget")
    public ModelAndView meteringFailureEmMaxGadget() {
        ModelAndView mav = new ModelAndView("gadget/mvm/meteringFailureEmMaxGadget");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("ondemandAuth", authMap.get("ondemand"));  // Ondemand 권한(command = true)
        return mav;
    }
    
    @RequestMapping(value="/gadget/mvm/meteringFailureEmNotChartMaxGadget")
    public ModelAndView meteringFailureEmNotChartMaxGadget() {
        ModelAndView mav = new ModelAndView("gadget/mvm/meteringFailureEmNotChartMaxGadget");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("ondemandAuth", authMap.get("ondemand"));  // Ondemand 권한(command = true)
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/meteringFailureGmMaxGadget")
    public ModelAndView meteringFailureGmMaxGadget() {
//        return new ModelAndView("gadget/mvm/meteringFailureGmMaxGadget");
        ModelAndView mav = new ModelAndView("gadget/mvm/meteringFailureGmMaxGadget");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("ondemandAuth", authMap.get("ondemand"));  // Ondemand 권한(command = true)
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/meteringFailureWmMaxGadget")
    public ModelAndView meteringFailureWmMaxGadget() {
//        return new ModelAndView("gadget/mvm/meteringFailureWmMaxGadget");
        ModelAndView mav = new ModelAndView("gadget/mvm/meteringFailureWmMaxGadget");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("ondemandAuth", authMap.get("ondemand"));  // Ondemand 권한(command = true)
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/meteringFailureHmMaxGadget")
    public ModelAndView meteringFailureHmMaxGadget() {
//        return new ModelAndView("gadget/mvm/meteringFailureHmMaxGadget");
        ModelAndView mav = new ModelAndView("gadget/mvm/meteringFailureHmMaxGadget");
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("ondemandAuth", authMap.get("ondemand"));  // Ondemand 권한(command = true)
        return mav;
    }

    @RequestMapping(value="/gadget/mvm/getMeteringFailureRateListByLocation")
    public ModelAndView getMeteringFailureRateListByLocation(@RequestParam("searchStartDate") String searchStartDate
                                                         , @RequestParam("searchEndDate") String searchEndDate
                                                         , @RequestParam("meterType") String meterType
                                                         , @RequestParam("locationId") String locationId
                                                         , @RequestParam("locationType") String locationType
                                                         , @RequestParam("supplierId") String supplierId)
    {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("searchStartDate", searchStartDate);
        params.put("searchEndDate", searchEndDate);
        params.put("meterType", meterType);
        params.put("locationId", locationId);
        params.put("locationType", locationType);
        params.put("supplierId", supplierId);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("result", meteringFailureManager.getMeteringFailureRateListByLocation(params));

        return mav;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/mvm/getMeteringFailureMeter")
	public ModelAndView getMeteringFailureMeter(@RequestParam("searchStartDate") String searchStartDate
										     , @RequestParam("searchEndDate") String searchEndDate
										     , @RequestParam("searchDateType") String searchDateType
										     , @RequestParam("meterType") String meterType
										     , @RequestParam("locationId") String locationId
										     , @RequestParam("customerId") String customerId
										     , @RequestParam("meterId") String meterId
										     , @RequestParam("mcuId") String mcuId
										     , @RequestParam("supplierId") String supplierId
										     , @RequestParam("curPage") String curPage) {

	
		ModelAndView mav = new ModelAndView("jsonView");

		List<FailureMeterData> result = null;
		if("".equals(locationId))
			locationId = null;
		
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("searchStartDate", searchStartDate);
		conditionMap.put("searchEndDate", 	searchEndDate);
		conditionMap.put("searchDateType", 	searchDateType);
		conditionMap.put("meterType", 		meterType);
		conditionMap.put("locationId", 		locationId);
		conditionMap.put("customerId", 		customerId);
		conditionMap.put("meterId", 		meterId);
		conditionMap.put("mcuId", 			mcuId);
		conditionMap.put("supplierId", 		supplierId);      
		conditionMap.put("currPage",		curPage);
		Map<String, Object> resultMap = meteringFailureManager.getMeteringFailureMeter(conditionMap);
		result = (List<FailureMeterData>)resultMap.get("list");
		int total = Integer.parseInt((String)resultMap.get("totalCount"));

		mav.addObject("total", total);
		mav.addObject("result", result);
		return mav;
		
	}
    
    @RequestMapping(value = "/gadget/mvm/meteringFailureExcelDownloadPopup")
    public ModelAndView meteringFailureExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView(
                "/gadget/mvm/meteringFailureExcelDownloadPopup");
        return mav;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/mvm/meteringFailureExcelMake")
    public ModelAndView meteringFailureExcelMake(
            @RequestParam("condition[]") String[] condition,
            @RequestParam("fmtMessage[]") String[] fmtMessage,
            @RequestParam("filePath") String filePath) {

        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
        List<FailureMeterData> list = new ArrayList<FailureMeterData>();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        boolean isLast = false;
        Long total = 0L; // 데이터 조회건수
        Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

        final String logPrefix = "meteringFailure";//15+14

        ModelAndView mav = new ModelAndView("jsonView");
        
        String locationId = (String)condition[4]; 
        if("".equals(locationId)) {
        	locationId = null;
        }

        List<FailureMeterData> result = null;
        try {
            Map<String, Object> conditionMap = new HashMap<String, Object>();
            conditionMap.put("searchStartDate", condition[0]);
            conditionMap.put("searchEndDate",   condition[1]);
            conditionMap.put("searchDateType",  condition[2]);
            conditionMap.put("meterType",       condition[3]);
            conditionMap.put("locationId",      locationId);
            conditionMap.put("customerId",      condition[5]);
            conditionMap.put("meterId",         condition[6]);
            conditionMap.put("mcuId",           condition[7]);
            conditionMap.put("supplierId",      condition[8]);
            conditionMap.put("currPage",        "");

            result = (List<FailureMeterData>) meteringFailureManager.getMeteringFailureMeter(conditionMap).get("list");
            total = new Integer(result.size()).longValue();

            mav.addObject("total", total);
            if (total <= 0) {
                return mav;
            }

            sbFileName.append(logPrefix);

            sbFileName.append(TimeUtil.getCurrentTimeMilli());

            // message 생성
            msgMap.put("contractNo",        fmtMessage[11]);
            msgMap.put("mcuId",             fmtMessage[13]);
            msgMap.put("mdsId",             fmtMessage[12]);
            msgMap.put("customerName",      fmtMessage[14]);
            msgMap.put("address",           fmtMessage[15]);
            msgMap.put("lastlastReadDate",  fmtMessage[17]);
            msgMap.put("failureCause",      fmtMessage[5]);
            msgMap.put("modemId",           fmtMessage[23]);

            msgMap.put("NotComm",               fmtMessage[25]);    //통신 이력 없음
            msgMap.put("CommstateYellow",       fmtMessage[26]);    //장기간 통신 장애
            msgMap.put("MeteringFormatError",   fmtMessage[27]);    //검침포맷 이상
            msgMap.put("MeterChange",           fmtMessage[28]);    //미터 교체 및 공급 중단
            msgMap.put("MeterStatusError",      fmtMessage[29]);    //미터 상태 이상
            msgMap.put("MeterTimeError",        fmtMessage[30]);    //미터 시간 이상
            msgMap.put("Success",               fmtMessage[31]);    //성공

            msgMap.put("title", fmtMessage[32]);
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

                        // 파일길이 : 30이상, 확장자 : xls|zip
                        if (filename.length() > 30
                                && (filename.endsWith("xls") || filename
                                        .endsWith("zip"))) {
                            // 10일 지난 파일들 삭제
                            if (filename.startsWith(logPrefix)
                                    && filename.substring(17, 25).compareTo(
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
            MeteringFailureMakeExcel wExcel = new MeteringFailureMakeExcel();
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
            logger.error(e, e);
        } catch (Exception e) {
            logger.error(e, e);
        }
        return mav;
    }

    /**
     * method name : getMeteringCountListPerLocation<b/>
     * method Desc : location 별 검침 성공/실패 개수를 조회한다.
     *
     * @param searchStartDate
     * @param searchEndDate
     * @param meterType
     * @param locationId
     * @param isParent 상위지역ID 인지 여부
     * @param supplierId
     * @return org.springframework.web.servlet.ModelAndView
     */
    @RequestMapping(value="/gadget/mvm/getMeteringCountListPerLocation")
    public ModelAndView getMeteringCountListPerLocation(@RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("meterType") String meterType,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("isParent") Boolean isParent,
            @RequestParam("supplierId") Integer supplierId,
            @RequestParam(value="permitLocationId", required=false) Integer permitLocationId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        try {
            if (searchStartDate.isEmpty()) {
                searchStartDate = TimeUtil.getCurrentDay();
                searchEndDate = TimeUtil.getCurrentDay();
            }
        } catch (ParseException e) {
            logger.error(e, e);
        }

        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("meterType", meterType);
        conditionMap.put("locationId", locationId);
        conditionMap.put("isParent", isParent);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("permitLocationId", permitLocationId);

        mav.addObject("result", ReflectionUtils.getDefineListToMapList(meteringFailureManager.getMeteringCountListPerLocation(conditionMap)));

        return mav;
    }

    /**
     * method name : getMeteringFailureRateListWithChildren<b/>
     * method Desc : location 별 검침 실패율을 조회한다.
     *
     * @param searchStartDate
     * @param searchEndDate
     * @param meterType
     * @param supplierId
     * @return org.springframework.web.servlet.ModelAndView
     */
    @RequestMapping(value="/gadget/mvm/getMeteringFailureRateListWithChildren")
    public ModelAndView getMeteringFailureRateListWithChildren(@RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("meterType") String meterType,
            @RequestParam("supplierId") Integer supplierId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();

        try {
            if (searchStartDate.isEmpty()) {
                searchStartDate = TimeUtil.getCurrentDay();
                searchEndDate = TimeUtil.getCurrentDay();
            }
        } catch (ParseException e) {
            logger.error(e, e);
        }
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("meterType", meterType);
        conditionMap.put("supplierId", supplierId);
        
        List<MeteringFailureData> resultList = meteringFailureManager.getMeteringFailureRateListWithChildren(conditionMap);
        int listlength = resultList.size();
        mav.addObject("result", ReflectionUtils.getDefineListToMapList(resultList.subList(0, listlength-1)));
        mav.addObject("total",ReflectionUtils.getDefineListToMapList(resultList.subList(listlength-1, listlength)));
        return mav;
    }
}