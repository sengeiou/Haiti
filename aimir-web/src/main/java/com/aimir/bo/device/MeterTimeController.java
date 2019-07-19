package com.aimir.bo.device;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Role;
import com.aimir.service.device.MCUManager;
import com.aimir.service.device.MeterTimeManager;
import com.aimir.service.system.RoleManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils;
import com.aimir.util.MeterTimeDiffMaxMakeExcel;
import com.aimir.util.MeterTimeSyncMaxMakeExcel;
import com.aimir.util.MeterTimeThresholdMaxMakeExcel;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

import flex.messaging.util.URLDecoder;

@Controller
public class MeterTimeController {
	
	@Autowired
    MeterTimeManager meterTimeManager;
	
	@Autowired
	MCUManager mcuManager;
	
    @Autowired
    RoleManager roleManager;

	@RequestMapping(value="/gadget/device/meterTimeMiniGadget")
	public ModelAndView meterTimeMiniGadget() {		
		return new ModelAndView("gadget/device/meterTimeMiniGadget");        
    }
	
	@RequestMapping(value="/gadget/device/meterTimeMaxGadget")
	public ModelAndView meterTimeMaxGadget() {	
		ModelAndView mav = new ModelAndView("gadget/device/meterTimeMaxGadget");
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("cmdAuth", authMap.get("command"));  // Command 권한(command = true)
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
		return mav;        
    }
	
	// MeterMini Gadget 조회 / Chart,Grid 동일 데이터 사용
	@RequestMapping(value="/gadget/device/getMeterTimeTimeDiffChart")
	public ModelAndView getMeterTimeTimeDiffChart(@RequestParam(value="mcuSysId" ,required=false)       String mcuSysId
			                                    , @RequestParam(value="customerName" ,required=false)   String customerName
			                                    , @RequestParam(value="meterMdsId" ,required=false)     String meterMdsId
			                                    , @RequestParam(value="contractNumber" ,required=false) String contractNumber
                                                 
			                                    , @RequestParam(value="timeDiff" ,required=false)       String timeDiff
			                                    , @RequestParam(value="time" ,required=false)           String time
			                                    , @RequestParam(value="timeType" ,required=false)       String timeType
                                                 
			                                    , @RequestParam(value="compliance" ,required=false) String compliance
                                                 
			                                    , @RequestParam(value="supplierId" ,required=false) String supplierId) {

		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("mcuSysId" 	    , mcuSysId);
		try {
			condition.put("customerName"    , URLDecoder.decode(customerName,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		condition.put("meterMdsId" 		, meterMdsId);
		condition.put("contractNumber"  , contractNumber);

		condition.put("timeDiff"		, timeDiff);
		condition.put("time" 			, time);
		condition.put("timeType" 		, timeType);

		condition.put("compliance" 	    , compliance);
		condition.put("supplierId" 	    , supplierId);

        
    	List<Object> miniChart = meterTimeManager.getMeterTimeTimeDiffChart(condition);
    	
        ModelAndView mav = new ModelAndView("jsonView");
        
        mav.addObject("chartData",miniChart.get(0));        
        
        return mav;        
    }

	// MeterMini Gadget 조회 / Chart,Grid 동일 데이터 사용
	@RequestMapping(value="/gadget/device/getMeterTimeTimeDiffComplianceChart")
	public ModelAndView getMeterTimeTimeDiffComplianceChart(@RequestParam(value="mcuSysId" ,required=false)       String mcuSysId
						                                  , @RequestParam(value="customerName" ,required=false)   String customerName
						                                  , @RequestParam(value="meterMdsId" ,required=false)     String meterMdsId
						                                  , @RequestParam(value="contractNumber" ,required=false) String contractNumber
			                                               
						                                  , @RequestParam(value="timeDiff" ,required=false)       String timeDiff
						                                  , @RequestParam(value="time" ,required=false)           String time
						                                  , @RequestParam(value="timeType" ,required=false)       String timeType
			                                               
						                                  , @RequestParam(value="compliance" ,required=false) String compliance
			                                               
						                                  , @RequestParam(value="supplierId" ,required=false) String supplierId) {

		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("mcuSysId" 	    , mcuSysId);
		try {
			condition.put("customerName"    , URLDecoder.decode(customerName,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		condition.put("meterMdsId" 		, meterMdsId);
		condition.put("contractNumber"  , contractNumber);

		condition.put("timeDiff"		, timeDiff);
		condition.put("time" 			, time);
		condition.put("timeType" 		, timeType);

		condition.put("compliance" 	    , compliance);
		condition.put("supplierId" 	    , supplierId);

        
    	List<Object> miniChart = meterTimeManager.getMeterTimeTimeDiffComplianceChart(condition);
    	
        ModelAndView mav = new ModelAndView("jsonView");
        
        mav.addObject("chartData",miniChart.get(0));        
        
        return mav;
    }
	
	
	// MeterMini Gadget 조회 / Chart,Grid 동일 데이터 사용
	@RequestMapping(value="/gadget/device/getMeterTimeTimeDiffGrid")
	public ModelAndView getMeterTimeTimeDiffGrid(@RequestParam(value="mcuSysId" ,required=false)        String mcuSysId
			                                   , @RequestParam(value="customerName" ,required=false)   String customerName
			                                   , @RequestParam(value="meterMdsId" ,required=false)     String meterMdsId
			                                   , @RequestParam(value="contractNumber" ,required=false) String contractNumber
                                                
			                                   , @RequestParam(value="timeDiff" ,required=false)       String timeDiff
			                                   , @RequestParam(value="time" ,required=false)           String time
			                                   , @RequestParam(value="timeType" ,required=false)       String timeType
                                                
			                                   , @RequestParam(value="compliance" ,required=false) 	String compliance 
	
			                                   , @RequestParam(value="supplierId" ,required=false) 	String supplierId
			                                   , @RequestParam(value="curPage" ,required=false)		Integer curPage
			                                   , @RequestParam(value="pageSize" ,required=false)		Integer pageSize) 	{
		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("mcuSysId" 	    , mcuSysId);
		try {
			condition.put("customerName"    , URLDecoder.decode(customerName,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		condition.put("meterMdsId" 		, meterMdsId);
		condition.put("contractNumber"  , contractNumber);

		condition.put("timeDiff"		, timeDiff);
		condition.put("time" 			, time);
		condition.put("timeType" 		, timeType);

		condition.put("compliance" 	    , compliance);
		condition.put("supplierId" 	    , supplierId);
		
		condition.put("curPage" 	    , curPage);
		condition.put("pageSize" 	    , pageSize);
        
    	List<Object> miniChart = meterTimeManager.getMeterTimeTimeDiffGrid(condition);
    	
        ModelAndView mav = new ModelAndView("jsonView");
        
        mav.addObject("totalCnt",miniChart.get(0));        
        mav.addObject("gridData",miniChart.get(1));
        
        return mav;        
    }	
	

	// MeterMini Gadget 조회 / Chart,Grid 동일 데이터 사용
	@RequestMapping(value="/gadget/device/getMeterTimeSyncLogChart")
	public ModelAndView getMeterTimeSyncLogChart(@RequestParam(value="mcuSysId" ,required=false) 	String mcuSysId
			                                   , @RequestParam(value="meterMdsId" ,required=false) 	String meterMdsId
			                                   , @RequestParam(value="operatorId" ,required=false) 	String operatorId
                                                
			                                   , @RequestParam(value="method" ,required=false) 		String method
			                                   , @RequestParam(value="status" ,required=false) 		String status
                                                
			                                   , @RequestParam(value="timeDiff" ,required=false)	String timeDiff
			                                   , @RequestParam(value="time" ,required=false) 		String time
			                                   , @RequestParam(value="timeType" ,required=false) 	String timeType
                                                
			                                   , @RequestParam(value="searchStartDate" ,required=false) String searchStartDate
			                                   , @RequestParam(value="searchEndDate" ,required=false) 	String searchEndDate
			                                   , @RequestParam(value="supplierId" ,required=false) 		String supplierId) {

		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("mcuSysId" 	, mcuSysId);
		condition.put("meterMdsId" 	, meterMdsId);
		condition.put("operatorId" 	, operatorId);

		condition.put("method" 		, method);
		condition.put("status" 		, status);

		condition.put("timeDiff" 	, timeDiff);
		condition.put("time" 		, time);
		condition.put("timeType" 	, timeType);
		
		if(searchStartDate.length() == 0 && searchEndDate.length() == 0) {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			Calendar ti = Calendar.getInstance();
			searchStartDate = searchEndDate = df.format(ti.getTime());
		} else if(searchStartDate.equals("0") && searchEndDate.equals("0")) {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			Calendar ti = Calendar.getInstance();
			searchEndDate =  df.format(ti.getTime());
			ti.add(Calendar.DAY_OF_YEAR, -6);
			searchStartDate = df.format(ti.getTime());
		}
		condition.put("searchStartDate" , searchStartDate);
		condition.put("searchEndDate" , searchEndDate);
		
		condition.put("supplierId" , supplierId);
        
    	List<Object> miniChart = meterTimeManager.getMeterTimeSyncLogChart(condition);
    	
        ModelAndView mav = new ModelAndView("jsonView");
        
        mav.addObject("chartData",miniChart.get(0));        
        
        return mav;        
    }

	@RequestMapping(value="/gadget/device/getMeterTimeSyncLogAutoChart")
	public ModelAndView getMeterTimeSyncLogAutoChart(@RequestParam(value="mcuSysId" ,required=false)	String mcuSysId
			                                       , @RequestParam(value="meterMdsId" ,required=false) 	String meterMdsId
			                                       , @RequestParam(value="operatorId" ,required=false) 	String operatorId
                                                    
			                                       , @RequestParam(value="method" ,required=false) 		String method
			                                       , @RequestParam(value="status" ,required=false) 		String status
                                                    
			                                       , @RequestParam(value="timeDiff" ,required=false) 	String timeDiff
			                                       , @RequestParam(value="time" ,required=false) 		String time
			                                       , @RequestParam(value="timeType" ,required=false) 	String timeType
                                                    
			                                       , @RequestParam(value="searchStartDate" ,required=false) String searchStartDate
			                                       , @RequestParam(value="searchEndDate" ,required=false) 	String searchEndDate
			                                       , @RequestParam(value="supplierId" ,required=false) 		String supplierId) {

		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("mcuSysId" 	, mcuSysId);
		condition.put("meterMdsId" 	, meterMdsId);
		condition.put("operatorId" 	, operatorId);

		condition.put("method" 		, method);
		condition.put("status" 		, status);

		condition.put("timeDiff" 	, timeDiff);
		condition.put("time" 		, time);
		condition.put("timeType" 	, timeType);

		if(searchStartDate.length() == 0 && searchEndDate.length() == 0) {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			Calendar ti = Calendar.getInstance();
			condition.put("searchStartDate" , df.format(ti.getTime()));
			condition.put("searchEndDate" , df.format(ti.getTime()));			
		} else {
			condition.put("searchStartDate" , searchStartDate);
			condition.put("searchEndDate" , searchEndDate);
		}
		condition.put("supplierId" , supplierId);
        
    	List<Object> miniChart = meterTimeManager.getMeterTimeSyncLogAutoChart(condition);
    	
        ModelAndView mav = new ModelAndView("jsonView");
        
        mav.addObject("chartData",miniChart.get(0));        
        
        return mav;        
    }
	
	
	@RequestMapping(value="/gadget/device/getMeterTimeSyncLogManualChart")
	public ModelAndView getMeterTimeSyncLogManualChart(@RequestParam(value="mcuSysId" ,required=false) 	String mcuSysId
			                                         , @RequestParam(value="meterMdsId" ,required=false)String meterMdsId
			                                         , @RequestParam(value="operatorId" ,required=false)String operatorId
                                                      
			                                         , @RequestParam(value="method" ,required=false) 	String method
			                                         , @RequestParam(value="status" ,required=false) 	String status
                                                      
			                                         , @RequestParam(value="timeDiff" ,required=false) 	String timeDiff
			                                         , @RequestParam(value="time" ,required=false) 		String time
			                                         , @RequestParam(value="timeType" ,required=false) 	String timeType
                                                      
			                                         , @RequestParam(value="searchStartDate" ,required=false) 	String searchStartDate
			                                         , @RequestParam(value="searchEndDate" ,required=false) 	String searchEndDate
			                                         , @RequestParam(value="supplierId" ,required=false) 		String supplierId) {

		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("mcuSysId" 	, mcuSysId);
		condition.put("meterMdsId" 	, meterMdsId);
		condition.put("operatorId" 	, operatorId);

		condition.put("method" 		, method);
		condition.put("status" 		, status);

		condition.put("timeDiff" 	, timeDiff);
		condition.put("time" 		, time);
		condition.put("timeType" 	, timeType);

		condition.put("searchStartDate" , searchStartDate);
		condition.put("searchEndDate" , searchEndDate);
		condition.put("supplierId" , supplierId);
        
    	List<Object> miniChart = meterTimeManager.getMeterTimeSyncLogManualChart(condition);
    	
        ModelAndView mav = new ModelAndView("jsonView");
        
        mav.addObject("chartData",miniChart.get(0));        
        
        return mav;        
    }
	
	@RequestMapping(value="/gadget/device/getMeterTimeSyncLogGrid")
	public ModelAndView getMeterTimeSyncLogGrid(@RequestParam(value="mcuSysId" ,required=false) 	String mcuSysId
			                                  , @RequestParam(value="meterMdsId" ,required=false) 	String meterMdsId
			                                  , @RequestParam(value="operatorId" ,required=false) 	String operatorId
                                               
			                                  , @RequestParam(value="method" ,required=false) 		String method
			                                  , @RequestParam(value="status" ,required=false) 		String status
                                               
			                                  , @RequestParam(value="timeDiff" ,required=false) 	String timeDiff
			                                  , @RequestParam(value="time" ,required=false) 		String time
			                                  , @RequestParam(value="timeType" ,required=false) 		String timeType
                                               
			                                  , @RequestParam(value="searchStartDate" ,required=false) 	String searchStartDate
			                                  , @RequestParam(value="searchEndDate" ,required=false) 	String searchEndDate
			                                  , @RequestParam(value="supplierId" ,required=false) 		String supplierId
			                                  
			                                  , @RequestParam(value="curPage" ,required=false)		String curPage
			                                  , @RequestParam(value="pageSize" ,required=false)		String pageSize) {

		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("mcuSysId" 	, mcuSysId);
		condition.put("meterMdsId" 	, meterMdsId);
		condition.put("operatorId" 	, operatorId);

		condition.put("method" 		, method);
		condition.put("status" 		, status);

		condition.put("timeDiff" 	, timeDiff);
		condition.put("time" 		, time);
		condition.put("timeType" 	, timeType);

		if(searchStartDate.length() == 0 && searchEndDate.length() == 0) {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			Calendar ti = Calendar.getInstance();
			searchStartDate = df.format(ti.getTime());
			searchEndDate =  df.format(ti.getTime());
		}
		searchStartDate += "000000";
		searchEndDate += "595959";
		
		condition.put("searchStartDate" , searchStartDate);
		condition.put("searchEndDate" , searchEndDate);
		
		
		condition.put("supplierId" 		, supplierId);
		
		condition.put("curPage"	, curPage);
		condition.put("pageSize", pageSize);
        
    	List<Object> miniChart = meterTimeManager.getMeterTimeSyncLogGrid(condition);
    	
        ModelAndView mav = new ModelAndView("jsonView");
        
        mav.addObject("totalCnt",miniChart.get(0));        
        mav.addObject("gridData",miniChart.get(1));
        
        return mav;        
    }

	
	@RequestMapping(value="/gadget/device/getMeterTimeThresholdGrid")
	public ModelAndView getMeterTimeThresholdGrid(@RequestParam(value="mcuSysType" ,required=false) 	String mcuSysType
												, @RequestParam(value="mcuSysId" ,required=false) 		String mcuSysId
												, @RequestParam(value="mcuCommState" ,required=false) 	String mcuCommState
										        
												, @RequestParam(value="locationId" ,required=false) 	String locationId
												, @RequestParam(value="time" ,required=false) 			String time
												, @RequestParam(value="timeDiffType" ,required=false) 	String timeDiffType
												
			                                    , @RequestParam(value="supplierId" ,required=false) 	String supplierId
			                                    , @RequestParam(value="curPage" ,required=false)		String curPage
			                                    , @RequestParam(value="pageSize" ,required=false)		String pageSize) {

		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("mcuSysType" 	 , mcuSysType);
		condition.put("mcuSysId" 	 , mcuSysId);
		condition.put("mcuCommState" , mcuCommState);

		condition.put("locationId" 	 , locationId);
		condition.put("time"	 	 , time);
		condition.put("timeDiffType" , timeDiffType);

		condition.put("supplierId" 	, supplierId);
		condition.put("curPage"		, curPage);
		condition.put("pageSize"	, pageSize);
    	List<Object> miniChart = meterTimeManager.getMeterTimeThresholdGrid(condition);
    	
        ModelAndView mav = new ModelAndView("jsonView");
        
        mav.addObject("totalCnt",miniChart.get(0));        
        mav.addObject("gridData",miniChart.get(1));
        
        return mav;        
    }
	
	 //meterTimeDiffMaxExcelMake 엑셀로 저장
  	@RequestMapping(value = "/gadget/device/meterTimeDiffMaxExcelMake")
  	public ModelAndView meterTimeDiffMaxExcelMake(
  			@RequestParam("condition[]") 	String[] condition,
  			@RequestParam("fmtMessage[]") 	String[] fmtMessage,
  			@RequestParam("filePath") 		String filePath) {

  		Map<String, String> msgMap = new HashMap<String, String>();
  		List<String> fileNameList = new ArrayList<String>();
  		List<Object> list = new ArrayList<Object>();

  		StringBuilder sbFileName = new StringBuilder();
  		StringBuilder sbSplFileName = new StringBuilder();

  		boolean isLast = false;
  		Long total = 0L; // 데이터 조회건수
  		Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

  		final String logPrefix = "meterTimeDiffList";//9

  		ModelAndView mav = new ModelAndView("jsonView");

  		List<Object> result = null;
  		try {
  			Map<String, Object> conditionMap = new HashMap<String, Object>();
  			
  				conditionMap.put("mcuSysId"   		, condition[0]);
  				try {
  					conditionMap.put("customerName"   	, URLDecoder.decode(condition[1],"UTF-8"));
  				} catch (UnsupportedEncodingException e) {
  					// TODO Auto-generated catch block
  					e.printStackTrace();
  				}

  				conditionMap.put("meterMdsId"   	, condition[2]);                          
  				conditionMap.put("contractNumber" 	, condition[3]);
  				conditionMap.put("timeDiff"			, condition[4]);
  				conditionMap.put("time"      		, condition[5]);                           
  				conditionMap.put("timeType"     	, condition[6]);
  				conditionMap.put("compliance"     	, condition[7]);
  				conditionMap.put("supplierId"       , condition[8]);
  				conditionMap.put("curPage"         	, 0);
  				conditionMap.put("pageSize"     	, 10000);
  			
  			List<Object> resultList = meterTimeManager.getMeterTimeTimeDiffGrid(conditionMap);
  			result = (List<Object>) resultList.get(1);

  			total = new Integer(result.size()).longValue();

  			mav.addObject("total", total);
  			if (total <= 0) {
  				return mav;
  			}

  			sbFileName.append(logPrefix);

  			sbFileName.append(TimeUtil.getCurrentTimeMilli());//14

  			// message 생성
  			msgMap.put("no"	 			, 	 fmtMessage[1]);
  			msgMap.put("mcuSysId"	 	, 	 fmtMessage[2]);
  			msgMap.put("meterMdsID"  	,    fmtMessage[3]);
  			msgMap.put("customerName"  	, 	 fmtMessage[4]);			
  			msgMap.put("contractNumber"	,    fmtMessage[5]);
  			msgMap.put("locName"		, 	 fmtMessage[6]);
  			msgMap.put("lastLinkTime"   ,    fmtMessage[7]);
  			msgMap.put("timeDiff"   	,    fmtMessage[8]);
  			msgMap.put("title"   		,    fmtMessage[9]);
  			
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

  						// 파일길이 : 26이상, 확장자 : xls|zip
  						if (filename.length() > 29
  								&& (filename.endsWith("xls") || filename
  										.endsWith("zip"))) {
  							// 10일 지난 파일들 삭제
  							if (filename.startsWith(logPrefix)
  									&& filename.substring(9, 17).compareTo(
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
  			MeterTimeDiffMaxMakeExcel wExcel = new MeterTimeDiffMaxMakeExcel();
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
  	
	
	 //meterTimeDiffMaxExcelMake 엑셀로 저장
 	@RequestMapping(value = "/gadget/device/meterTimeSyncMaxExcelMake")
 	public ModelAndView meterTimeSyncMaxExcelMake(
 			@RequestParam("condition[]") 	String[] condition,
 			@RequestParam("fmtMessage[]") 	String[] fmtMessage,
 			@RequestParam("filePath") 		String filePath) {

 		Map<String, String> msgMap = new HashMap<String, String>();
 		List<String> fileNameList = new ArrayList<String>();
 		List<Object> list = new ArrayList<Object>();

 		StringBuilder sbFileName = new StringBuilder();
 		StringBuilder sbSplFileName = new StringBuilder();

 		boolean isLast = false;
 		Long total = 0L; // 데이터 조회건수
 		Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

 		final String logPrefix = "meterTimeSyncList";//9

 		ModelAndView mav = new ModelAndView("jsonView");

 		List<Object> result = null;
 		try {
 			Map<String, Object> conditionMap = new HashMap<String, Object>();


 	         	conditionMap.put("mcuSysId"   		, condition[0]);                          
 				conditionMap.put("meterMdsId" 		, condition[1]);
 				conditionMap.put("operatorId"		, condition[2]);
 				conditionMap.put("method"      		, condition[3]);                           
 				conditionMap.put("status"     		, condition[4]);
 				conditionMap.put("timeDiff"     	, condition[5]);
 				conditionMap.put("time"         	, condition[6]);
 				conditionMap.put("timeType"     	, condition[7]);

 	 			if(condition[8].length() == 0 && condition[9].length() == 0) {
 	 				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
 	 				Calendar ti = Calendar.getInstance();
 	 				condition[8] = df.format(ti.getTime());
 	 				condition[9] =  df.format(ti.getTime());
 	 			}
 	 			condition[8] += "000000";
 	 			condition[9] += "595959";
 	 			
 				conditionMap.put("searchStartDate"  , condition[8]);
 				conditionMap.put("searchEndDate"  	, condition[9]);
 				conditionMap.put("supplierId"       , condition[10]);
 				conditionMap.put("curPage"         	, "0");
 				conditionMap.put("pageSize"     	, "10000");
 			
 			List<Object> resultList  = meterTimeManager.getMeterTimeSyncLogGrid(conditionMap);
 			result = (List<Object>) resultList.get(1);

 			total = new Integer(result.size()).longValue();

 			mav.addObject("total", total);
 			if (total <= 0) {
 				return mav;
 			}

 			sbFileName.append(logPrefix);

 			sbFileName.append(TimeUtil.getCurrentTimeMilli());//14

 			// message 생성
 			msgMap.put("no"	 			, 	 fmtMessage[0]);
 			msgMap.put("writeDate"	 	, 	 fmtMessage[1]);
 			msgMap.put("method"  		,    fmtMessage[2]);
 			msgMap.put("mcuSysID"  		, 	 fmtMessage[3]);			
 			msgMap.put("meterMdsId"		,    fmtMessage[4]);
 			msgMap.put("timeDiff"		, 	 fmtMessage[5]);
 			msgMap.put("previousDate"   ,    fmtMessage[6]);
 			msgMap.put("currentDate"    ,    fmtMessage[7]);
 			msgMap.put("status"   		,    fmtMessage[8]);
 			msgMap.put("operator"   	,    fmtMessage[9]);
 			msgMap.put("title"   		,    fmtMessage[10]);
 			
 			
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

 						// 파일길이 : 26이상, 확장자 : xls|zip
 						if (filename.length() > 29
 								&& (filename.endsWith("xls") || filename
 										.endsWith("zip"))) {
 							// 10일 지난 파일들 삭제
 							if (filename.startsWith(logPrefix)
 									&& filename.substring(9, 17).compareTo(
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
 			MeterTimeSyncMaxMakeExcel wExcel = new MeterTimeSyncMaxMakeExcel();
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
	
 	
	 //meterTimeDiffMaxExcelMake 엑셀로 저장
	@RequestMapping(value = "/gadget/device/meterTimeThresholdMaxExcelMake")
	public ModelAndView meterTimeThresholdMaxExcelMake(
			@RequestParam("condition[]") 	String[] condition,
			@RequestParam("fmtMessage[]") 	String[] fmtMessage,
			@RequestParam("filePath") 		String filePath) {

		Map<String, String> msgMap = new HashMap<String, String>();
		List<String> fileNameList = new ArrayList<String>();
		List<Object> list = new ArrayList<Object>();

		StringBuilder sbFileName = new StringBuilder();
		StringBuilder sbSplFileName = new StringBuilder();

		boolean isLast = false;
		Long total = 0L; // 데이터 조회건수
		Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

		final String logPrefix = "meterTimeThresholdList";//9

		ModelAndView mav = new ModelAndView("jsonView");

		List<Object> result = null;
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();


	         	conditionMap.put("mcuSysType"   	, condition[0]);                          
				conditionMap.put("mcuSysId" 		, condition[1]);
				conditionMap.put("mcuCommState"		, condition[2]);
				conditionMap.put("locationId"      	, condition[3]);                           
				conditionMap.put("time"     		, condition[4]);
				conditionMap.put("timeDiffType"     , condition[5]);
				conditionMap.put("supplierId"       , condition[6]);
				conditionMap.put("curPage"         	, "0");
				conditionMap.put("pageSize"     	, "10000");
			
			List<Object> resultList  = meterTimeManager.getMeterTimeThresholdGrid(conditionMap);
			result = (List<Object>) resultList.get(1);

			total = new Integer(result.size()).longValue();

			mav.addObject("total", total);
			if (total <= 0) {
				return mav;
			}

			sbFileName.append(logPrefix);

			sbFileName.append(TimeUtil.getCurrentTimeMilli());//14

			// message 생성
			msgMap.put("no"	 			, 	 fmtMessage[1]);
			msgMap.put("sysId"	 		, 	 fmtMessage[2]);
			msgMap.put("sysType"  		,    fmtMessage[3]);
			msgMap.put("sysName"  		, 	 fmtMessage[4]);			
			msgMap.put("phone"			,    fmtMessage[5]);
			msgMap.put("ipAddr"			, 	 fmtMessage[6]);
			msgMap.put("threshold"   	,    fmtMessage[7]);
			msgMap.put("lastComm"    	,    fmtMessage[8]);
			msgMap.put("commState"   	,    fmtMessage[9]);
			msgMap.put("title"   		,    fmtMessage[10]);
			msgMap.put("commState00"   	,    fmtMessage[11]);
			msgMap.put("commState24"   	,    fmtMessage[12]);
			msgMap.put("commState48"   	,    fmtMessage[13]);
			
			
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

						// 파일길이 : 26이상, 확장자 : xls|zip
						if (filename.length() > 29
								&& (filename.endsWith("xls") || filename
										.endsWith("zip"))) {
							// 10일 지난 파일들 삭제
							if (filename.startsWith(logPrefix)
									&& filename.substring(9, 17).compareTo(
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
			MeterTimeThresholdMaxMakeExcel wExcel = new MeterTimeThresholdMaxMakeExcel();
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
