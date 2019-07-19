package com.aimir.bo.test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.common.CommandProperty;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.PlcQualityTestManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.support.AimirFilePath;
import com.aimir.support.FileUploadHelper;
import com.aimir.util.CalendarUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZigResultDetailMakeExcel;
import com.aimir.util.ZigResultMakeExcel;
import com.aimir.util.ZipUtils;

@Controller
public class PlcQualityTestController {

	private static Log log = LogFactory.getLog(PlcQualityTestController.class);
	
	private static final String FOLDERNAME	= "ZigTestFile";
	
	@Autowired
	AimirFilePath aimirFilePath;
	
	@Autowired
	PlcQualityTestManager plcQualityTestManager;
	
	@Autowired
	SupplierManager supplierManager;

	@RequestMapping(value="/gadget/test/plcQualityTestMax")
    public ModelAndView pocTestMiniGadget(HttpSession session) 
	{
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();

        ModelAndView mav = new ModelAndView("gadget/test/plcQualityTestMax");
        mav.addObject("supplierId", supplierId);

        return mav;
    }
	
	@RequestMapping(value="/gadget/test/zigList")
    public ModelAndView getZigList(
    		@RequestParam(value="zigName") String zigName,
    		@RequestParam(value="supplierId") String supplierId) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
        String osName = System.getProperty("os.name");
        String homePath = null;
        if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
        	homePath = CommandProperty.getProperty("firmware.window.dir");
        }else{
        	homePath = CommandProperty.getProperty("firmware.dir");
        }
		int lastPathIndex = homePath.lastIndexOf("/");
		homePath = homePath.substring(0, lastPathIndex < 0 ? 0 : lastPathIndex);
		
		String savePath = homePath+"/"+FOLDERNAME;
		File dir = new File(savePath);
		File[] files = dir.listFiles();

        List<Map<String,String>> fileNames = new ArrayList<Map<String,String>>();

        if(files != null) {
            for ( File file: files ) {
                if ( file.isFile() && !file.isHidden()) {
                	String fileName = file.getName();
                	fileName = fileName.substring(0, fileName.lastIndexOf("."));
                	Map<String,String> map = new HashMap<String,String>();
                	
                	if(fileName.endsWith("_complete")) {
                		fileName = fileName.substring(0,fileName.lastIndexOf("_complete"));
                		map.put("zigName",fileName);
                		map.put("testYN","테스트 완료");
                	} else if(fileName.endsWith("_ing")) {
                		fileName = fileName.substring(0,fileName.lastIndexOf("_ing"));
                		map.put("zigName",fileName);
                		map.put("testYN","테스트 중");
                	} else {
                		map.put("zigName",fileName);
                		map.put("testYN","테스트 준비");
                	}

                	if(zigName != null && !"".equals(zigName)) {
                		if(zigName != null && zigName.equals(fileName)) {
                			fileNames.add(map);
                		}
                	} else {
                		fileNames.add(map);
                	}
                }
            }
        }
		
		if (FileUploadHelper.exists(savePath)) {
	        mav.addObject("result", fileNames);
	        mav.addObject("totalCnt", fileNames.size());
		}
		
        return mav;
    }
	
	@RequestMapping(value="/gadget/test/assetList")
    public ModelAndView getAssetList(
    		@RequestParam(value="zigName") String zigName,
    		@RequestParam(value="supplierId") String supplierId) {

		ModelAndView mav = new ModelAndView("jsonView");
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = new HashMap<String, Object>();

		if (zigName == null || "".equals(zigName))
			return null;
		
        String osName = System.getProperty("os.name");
        String homePath = null;
        if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
        	homePath = CommandProperty.getProperty("firmware.window.dir");
        }else{
        	homePath = CommandProperty.getProperty("firmware.dir");
        }
		int lastPathIndex = homePath.lastIndexOf("/");
		homePath = homePath.substring(0, lastPathIndex < 0 ? 0 : lastPathIndex);
		
		String savePath = homePath+"/"+FOLDERNAME;
		list = plcQualityTestManager.getReadExcelAsset(savePath,zigName);

        mav.addObject("result", list);
        mav.addObject("totalCnt", list.size());
		
        return mav;
    }
	
	@RequestMapping(value="/gadget/test/zigResult")
    public ModelAndView getZigResult(
    		@RequestParam(value="zigName", required=false) String zigName,
    		@RequestParam(value="startDate") String startDate,
    		@RequestParam(value="endDate") String endDate,
    		@RequestParam(value="supplierId") String supplierId,
    		@RequestParam(value="searchType") String searchType,
			@RequestParam(value="testResult") String testResult) {
		
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        String curPage = request.getParameter("page");
        String limit = request.getParameter("limit");

		ModelAndView mav = new ModelAndView("jsonView");
		List<Object> list = new ArrayList<Object>();
		try{
			Map<String,Object> condition = new HashMap<String, Object>();
			condition.put("zigName", zigName);
			condition.put("startDate", startDate);
			condition.put("endDate", endDate);
			condition.put("supplierId", supplierId);
			condition.put("searchType", searchType);
			condition.put("testResult", testResult);
			condition.put("curPage", curPage);
			condition.put("limit", limit);
			condition.put("isExcel",false);
			list = plcQualityTestManager.getPlcQualityResult(condition);
		} catch(Exception e) {
			log.error(e,e);
		}
        mav.addObject("result", list.get(0));
        mav.addObject("totalCount",list.get(1));

        return mav;
    }
	
	@RequestMapping(value="/gadget/test/detailResult")
    public ModelAndView getDetailResult(
    		@RequestParam(value="zigId") Integer zigId,
    		@RequestParam(value="startDate") String startDate,
    		@RequestParam(value="endDate") String endDate,
    		@RequestParam(value="supplierId") String supplierId,
    		@RequestParam(value="searchType") String searchType,
			@RequestParam(value="testResult") String testResult) {
		
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        String curPage = request.getParameter("page");
        String limit = request.getParameter("limit");
		
		ModelAndView mav = new ModelAndView("jsonView");
		List<Object> list = new ArrayList<Object>();
		try {
			Supplier supplier = supplierManager.getSupplier(Integer.parseInt(supplierId));
			Map<String,Object> condition = new HashMap<String, Object>();
			condition.put("zigId", zigId);
			condition.put("curPage", curPage);
			condition.put("limit", limit);
			condition.put("startDate", startDate);
			condition.put("endDate", endDate);
			condition.put("supplier", supplier);
			condition.put("searchType", searchType);
			condition.put("testResult", testResult);
			condition.put("isExcel",false);
			list = plcQualityTestManager.getPlcQualityDetailResult(condition);
		}catch(Exception e) {
			log.error(e,e);
		}

		mav.addObject("totalCount", list.get(0));
        mav.addObject("result", list.get(1));
        return mav;
    }
	
	@RequestMapping(value="/gadget/test/getSummaryInfo")
	public ModelAndView getSummaryInfo(
			@RequestParam(value="zigId") Integer zigId,
    		@RequestParam(value="startDate") String startDate,
    		@RequestParam(value="endDate") String endDate,
    		@RequestParam(value="searchType") String searchType,
			@RequestParam(value="testResult") String testResult) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		 Map<String, Object> map = new  HashMap<String, Object>();
		try {
			Map<String,Object> condition = new HashMap<String, Object>();
			condition.put("zigId", zigId);
			condition.put("startDate", startDate);
			condition.put("endDate", endDate);
			condition.put("searchType", searchType);
			condition.put("testResult", testResult);
			map = plcQualityTestManager.getSummaryInfo(condition);
		}catch(Exception e) {
			log.error(e,e);
		}

		mav.addObject("summary", map.get("summary"));
        return mav;
		
		
	}
	
	@RequestMapping(value="/gadget/test/saveZigUploadFile", method=RequestMethod.POST)
    public ModelAndView saveZigUploadFile(
    		@RequestParam(value="ext") String extension,
    		HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, IOException{
		ModelAndView mav = new ModelAndView("jsonView");
		String filename = null;
		String saveFileName = null;
		String reason = "";
		try {
			String verifyStr = StringUtil.nullToBlank(request.getParameter("verify"));
			Boolean verify = false;
			if(!verifyStr.isEmpty()) {
				verify = Boolean.parseBoolean(verifyStr);
			}
			
			String zigName = request.getParameter("zigName");
			filename = request.getParameter("fileName");

	        String osName = System.getProperty("os.name");
	        String homePath = null;
	        if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
	        	homePath = CommandProperty.getProperty("firmware.window.dir");
	        }else{
	        	homePath = CommandProperty.getProperty("firmware.dir");
	        }
			int lastPathIndex = homePath.lastIndexOf("/");
			homePath = homePath.substring(0, lastPathIndex < 0 ? 0 : lastPathIndex);
			saveFileName = zigName + "." + extension;
			
			String savePath = homePath+"/"+FOLDERNAME;
			String tempPath = homePath+"/temp/"+FOLDERNAME;
			
			if (!FileUploadHelper.exists(savePath)) {
				File savedir = new File(savePath);
				savedir.mkdir();
			}
			
			if (!FileUploadHelper.exists(tempPath)) {
				File savedir = new File(tempPath);
				savedir.mkdir();
			}

			if(verify) {
				//동일 이름이 존재하는지 검증필요.
				File verifyFile_xlsx = new File(FileUploadHelper.makePath(savePath, zigName+".xlsx"));
				File verifyFile_xls = new File(FileUploadHelper.makePath(savePath, zigName+".xls"));
				File verifyFile_ing_xlsx = new File(FileUploadHelper.makePath(savePath, zigName+"_ing.xlsx"));
				File verifyFile_ing_xls = new File(FileUploadHelper.makePath(savePath, zigName+"_ing.xls"));
				File verifyFile_complete_xlsx = new File(FileUploadHelper.makePath(savePath, zigName+"_complete.xlsx"));
				File verifyFile_complete_xls = new File(FileUploadHelper.makePath(savePath, zigName+"_complete.xls"));
				
				if(verifyFile_xlsx.exists() || verifyFile_ing_xlsx.exists() || verifyFile_complete_xlsx.exists()
						|| verifyFile_xls.exists() ||verifyFile_ing_xls.exists() || verifyFile_complete_xls.exists()) {
					reason = "duplicate";
					File delFile =new File(FileUploadHelper.makePath(tempPath, saveFileName));
					if(delFile.exists()) {
						delFile.delete();
					}
					
					throw new Exception();
				}

//				File Copy & Temp File Delete
				FileUploadHelper.copy(FileUploadHelper.makePath(tempPath, filename), FileUploadHelper.makePath(savePath, filename));	
				FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(tempPath, filename));
			} else {
				MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest)request;
				MultipartFile multipartFile = multiReq.getFile("userfile");
				filename = multipartFile.getOriginalFilename();
				
				if (filename == null || "".equals(filename))
					return null;
				
				File uFile =new File(FileUploadHelper.makePath(tempPath, saveFileName));
				multipartFile.transferTo(uFile);
			}

			mav.addObject("result","success");

		} catch(Exception e) {
			mav.addObject("result","fail");
			mav.addObject("reason", reason);
			log.error(e,e);
		}
		
    	mav.addObject("fileName",filename);
    	mav.addObject("saveFileName",saveFileName);
    	mav.addObject("ext",extension);
    	
		return mav;
    }
	
	@RequestMapping(value="/gadget/test/delZigUploadFile", method=RequestMethod.POST)
    public ModelAndView delZigUploadFile(
    		HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, IOException{
		ModelAndView mav = new ModelAndView("jsonView");
		String failReason = "<fmt:message key='aimir.bems.facilityMgmt.unknown'/>";
		try {
			
			String zigName = request.getParameter("zigName");

			if (zigName == null || "".equals(zigName))
				return null;
			
	        String osName = System.getProperty("os.name");
	        String homePath = null;
	        if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
	        	homePath = CommandProperty.getProperty("firmware.window.dir");
	        }else{
	        	homePath = CommandProperty.getProperty("firmware.dir");
	        }
			int lastPathIndex = homePath.lastIndexOf("/");
			homePath = homePath.substring(0, lastPathIndex < 0 ? 0 : lastPathIndex);
			
			String savePath = homePath+"/"+FOLDERNAME;
			
			String[] endFileName = {".xlsx", "_complete.xlsx", "_ing.xlsx", ".xls", "_complete.xls", "_ing.xls"};
			Boolean result = false;
			
			if (FileUploadHelper.exists(savePath)) {
				for (int i = 0; i < endFileName.length; i++) {
					String fileName = zigName+endFileName[i];
					File uFile =new File(FileUploadHelper.makePath(savePath, fileName));
					if(uFile.exists()) {
						result=FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(savePath, fileName));
						if(!result) {
							failReason = "<fmt:message key='aimir.cannot.use.file'/>";
						}
						break;
					}
				}
			}
				
			mav.addObject("isSuccess",result);
			mav.addObject("reason",failReason);
		} catch(Exception e) {
			mav.addObject("isSuccess",false);
			mav.addObject("reason",failReason);
			log.error(e,e);
		}
		
		return mav;
    }
	
	@RequestMapping(value = "/gadget/test/exportExcelPopup")
	public ModelAndView meterMaxExcelDownloadPopup() {
		ModelAndView mav = new ModelAndView(
				"/gadget/ExcelDownloadPopup");
		return mav;
	}
	
	@RequestMapping(value="/gadget/test/exportResult")
    public ModelAndView exportResult(
		@RequestParam("condition[]") 	String[] condition,
		@RequestParam("fmtMessage[]") 	String[] fmtMessage,
		@RequestParam("filePath") 		String filePath) {
	
		Map<String, String> msgMap = new HashMap<String, String>();
		List<Object> list = new ArrayList<Object>();
		List<String> fileNameList = new ArrayList<String>();
		StringBuilder sbFileName = new StringBuilder();
		StringBuilder sbSplFileName = new StringBuilder();
	
		boolean isLast = false;
		Integer total = 0; // 데이터 조회건수
		Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수
	
		final String logPrefix = "zigTestResult";//9
	
		ModelAndView mav = new ModelAndView("jsonView");
	
		List<Object> result = null;
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("zigName", 	condition[0]);
	        conditionMap.put("startDate", 	condition[1]);
	        conditionMap.put("endDate", 	condition[2]);
	        conditionMap.put("supplierId", 	condition[3]);
	        conditionMap.put("searchType",  condition[4]);
	        conditionMap.put("testResult",  condition[5]);
	        conditionMap.put("isExcel",		true);
			result = plcQualityTestManager.getPlcQualityResult(conditionMap);
	
			total = (Integer) result.get(1);
	
			mav.addObject("total", total);
			if (total <= 0) {
				return mav;
			}
	
			sbFileName.append(logPrefix);
	
			sbFileName.append(TimeUtil.getCurrentTimeMilli());//14
	
			// message 생성
			msgMap.put("no",		   	   fmtMessage[0]);
			msgMap.put("zigName",		   fmtMessage[1]);
			msgMap.put("resultCnt", 	   fmtMessage[2]);
			msgMap.put("completeDate",     fmtMessage[3]);

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
	
						// 파일길이 : 26이상, 확장자 : xlsx|zip
						if (filename.length() > 29
								&& (filename.endsWith("xlsx") || filename
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
			
			ZigResultMakeExcel wExcel = new ZigResultMakeExcel();
			int cnt = 1;
			int idx = 0;
			int fnum = 0;
			int splCnt = 0;
	
			if (total <= maxRows) {
				sbSplFileName = new StringBuilder();
				sbSplFileName.append(sbFileName);
				sbSplFileName.append(".xlsx");
				wExcel.writeReportExcel(result, msgMap, isLast, filePath,
						sbSplFileName.toString());
				fileNameList.add(sbSplFileName.toString());
			} else {
				for (int i = 0; i < total; i++) {
					if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
						sbSplFileName = new StringBuilder();
						sbSplFileName.append(sbFileName);
						sbSplFileName.append('(').append(++fnum)
								.append(").xlsx");
	
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
			e.printStackTrace();
			log.error(e.toString(), e);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.toString(), e);
		}
		return mav;
	}
	
	@RequestMapping(value="/gadget/test/exportResultDetail")
    public ModelAndView exportResultDetail(
		@RequestParam("condition[]") 	String[] condition,
		@RequestParam("fmtMessage[]") 	String[] fmtMessage,
		@RequestParam("filePath") 		String filePath) {

		Map<String, String> msgMap = new HashMap<String, String>();
		List<String> fileNameList = new ArrayList<String>();
		List<Object> list = new ArrayList<Object>();
	
		StringBuilder sbFileName = new StringBuilder();
		StringBuilder sbSplFileName = new StringBuilder();
	
		String supplierId = condition[1];
		Supplier supplier = supplierManager.getSupplier(Integer.parseInt(supplierId));

		boolean isLast = false;
		Long total = 0L; // 데이터 조회건수
		Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수
	
		final String logPrefix = "zigTestDetailResult";//9
	
		ModelAndView mav = new ModelAndView("jsonView");
	
		List<Object> result = null;
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
	        conditionMap.put("zigId", Integer.parseInt(condition[0]));
	        conditionMap.put("supplier",	supplier);
	        conditionMap.put("startDate", 	condition[2]);
	        conditionMap.put("endDate", 	condition[3]);
	        conditionMap.put("searchType",  condition[4]);
	        conditionMap.put("testResult",  condition[5]);
	        conditionMap.put("isExcel",		true);
	
			result = plcQualityTestManager.getPlcQualityDetailResult(conditionMap);
	
			total = new Integer(result.size()).longValue();
	
			mav.addObject("total", total);
			if (total <= 0) {
				return mav;
			}
	
			sbFileName.append(logPrefix);
	
			sbFileName.append(TimeUtil.getCurrentTimeMilli());//14
	
			// message 생성
			msgMap.put("no", 			fmtMessage[0]);
			msgMap.put("testResult", 	fmtMessage[1]);
			msgMap.put("meterSerial", 	fmtMessage[2]);
			msgMap.put("modemSerial", 	fmtMessage[3]);
			msgMap.put("hwVer", 		fmtMessage[4]);
	        msgMap.put("swVer", 		fmtMessage[5]);
	        msgMap.put("swBuild", 		fmtMessage[6]);
	        msgMap.put("failReason",	fmtMessage[7]);
	        msgMap.put("completeDate",  fmtMessage[8]);
	        msgMap.put("success", 		fmtMessage[9]);
	        msgMap.put("fail",	 		fmtMessage[10]);

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
	
						// 파일길이 : 26이상, 확장자 : xlsx|zip
						if (filename.length() > 29
								&& (filename.endsWith("xlsx") || filename
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
			ZigResultDetailMakeExcel wExcel = new ZigResultDetailMakeExcel();
			int cnt = 1;
			int idx = 0;
			int fnum = 0;
			int splCnt = 0;
	
			if (total <= maxRows) {
				sbSplFileName = new StringBuilder();
				sbSplFileName.append(sbFileName);
				sbSplFileName.append(".xlsx");
				wExcel.writeReportExcel(result, msgMap, isLast, filePath,
						sbSplFileName.toString(), supplier);
				fileNameList.add(sbSplFileName.toString());
			} else {
				for (int i = 0; i < total; i++) {
					if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
						sbSplFileName = new StringBuilder();
						sbSplFileName.append(sbFileName);
						sbSplFileName.append('(').append(++fnum)
								.append(").xlsx");
	
						list = result.subList(idx, (i + 1));
	
						wExcel.writeReportExcel(list, msgMap, isLast, filePath,
								sbSplFileName.toString(), supplier);
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
			e.printStackTrace();
			log.error(e.toString(), e);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.toString(), e);
		}
		return mav;
	}
	
	@RequestMapping(value="/gadget/test/testStart")
    public ModelAndView testStart(
    		@RequestParam("zigList[]") 	String[] zigList) {
		ModelAndView mav = new ModelAndView("jsonView");

        String osName = System.getProperty("os.name");
        String homePath = null;
        if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
        	homePath = CommandProperty.getProperty("firmware.window.dir");
        }else{
        	homePath = CommandProperty.getProperty("firmware.dir");
        }
		int lastPathIndex = homePath.lastIndexOf("/");
		homePath = homePath.substring(0, lastPathIndex < 0 ? 0 : lastPathIndex);
		String savePath = homePath+"/"+"ZigTestFile";
		
		plcQualityTestManager.testStart(savePath, zigList);
        mav.addObject("result", "success");
        return mav;
    }
	
	@RequestMapping(value="/gadget/test/testEnd")
    public ModelAndView testEnd(
    		@RequestParam("zigList[]") 	String[] zigList) {
		ModelAndView mav = new ModelAndView("jsonView");

        String osName = System.getProperty("os.name");
        String homePath = null;
        if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
        	homePath = CommandProperty.getProperty("firmware.window.dir");
        }else{
        	homePath = CommandProperty.getProperty("firmware.dir");
        }
		int lastPathIndex = homePath.lastIndexOf("/");
		homePath = homePath.substring(0, lastPathIndex < 0 ? 0 : lastPathIndex);
		String savePath = homePath+"/"+"ZigTestFile";

		plcQualityTestManager.testEnd(savePath, zigList);
        mav.addObject("result", "success");
        return mav;
    }
	
	@RequestMapping(value="/gadget/test/checkResult")
    public ModelAndView checkResult(
    		@RequestParam("zigName") 	String zigName) {
		ModelAndView mav = new ModelAndView("jsonView");

		List<Map<String, Object>> checkData = plcQualityTestManager.checkResult(zigName);
        mav.addObject("checkData", checkData);
        return mav;
    }
	
	@RequestMapping(value="/gadget/test/changeNullResult")
    public ModelAndView changeNullResult(
    		@RequestParam("zigId") 	Integer zigId,
    		@RequestParam("testStartDate") 	String testStartDate) {
		ModelAndView mav = new ModelAndView("jsonView");

		plcQualityTestManager.changeNullResult(zigId, testStartDate);
        mav.addObject("result", "success");
        return mav;
    }
	
}
