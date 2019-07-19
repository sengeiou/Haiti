package com.aimir.bo.mvm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
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
import com.aimir.service.mvm.HandheldUnitDataUploadManager_MOE;
import com.aimir.support.FileUploadHelper;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.ExcelUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

/**
 * 검침 데이터 수동 업로드 가젯을 위한 Controller
 * 전기
 * @author SEJIN HAN
 *
 */
@Controller
public class HandheldUnitDataUploadController_MOE {
	
	private Logger logger = Logger.getLogger(ManualMeteringController.class);
	
	/**
	 * Manager
	 */
	// @Autowired
	private HandheldUnitDataUploadManager_MOE mduManager;	
		
	/**
	 * Mini Gadget
	 */
	@RequestMapping(value="/gadget/mvm/handheldUnitDataUploadMiniGadget_MOE.do")
	public ModelAndView loadManualMeteringMiniGadget() {
		return new ModelAndView("gadget/mvm/handheldUnitDataUploadMiniGadget_MOE"); 
	}
	
	/**
	 * Max Gadget
	 */
	@RequestMapping(value="/gadget/mvm/handheldUnitDataUploadMaxGadget_MOE.do")
	public ModelAndView loadManualMeteringMaxGadget() {
		return new ModelAndView("gadget/mvm/handheldUnitDataUploadMaxGadget_MOE"); 
	}
	
	/**
	 * 기본 이력 조회
	 */
	@RequestMapping(value="/gadget/mvm/getDefaultUploadHistory.do")
	public ModelAndView getDefaultUploadHistory(
													@RequestParam(value="supplierId") String supplierId,
													@RequestParam(value="meterId") String meterId,
													@RequestParam(value="loginId") String loginId,
													@RequestParam(value="startDate") String startDate,
													@RequestParam(value="endDate") String endDate){
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("meterId", meterId.trim());
		condition.put("loginId", loginId.trim());
		condition.put("startDate", startDate.concat("000000"));
		condition.put("endDate", endDate.concat("235959"));
			
		//result = mduManager.getUploadHistory(condition);
		result = mduManager.getUploadHistoryWithLocTime(condition,supplierId);
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", result);
		return mav;
	}
	
	/**
	 * 실패 이력 조회
	 */
	@RequestMapping(value="/gadget/mvm/getFailedUploadHistory.do")
	public ModelAndView getFailedUploadHistory(@RequestParam(value="uploadId") String uploadId){
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("uploadId", uploadId);
	
		
		result = mduManager.getFailedUploadHistory(condition);
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", result);
		return mav;
	}
	
	/**
	 * 파일 업로드 모듈 (UploadPanel에서 Select버튼을 통해 파일을 선택했을 때)
	 */
	@RequestMapping(value="/gadget/mvm/getTempFileName")
    public ModelAndView getTempFileName(HttpServletRequest request, HttpServletResponse response)
                    throws ServletRequestBindingException, IOException {

        String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");

        MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multiReq.getFile("userfile");
        
        String mdsId = (String) multiReq.getParameter("mdsid");
        if(mdsId == null || "".equals(mdsId))
        	return null;
        
        String oldname = multipartFile.getOriginalFilename();
        String filename = "";
        if (oldname == null || "".equals(oldname)){
        	return null;
        }else{
        	// 파일 이름 변경 ("HHU"_original name.xls)        	
        	filename = "HHU".concat("_".concat(oldname));             	
        }
            
        String tempPath = contextRoot+"temp/manualdata";
        //String tempPath = "/tmp/manualdata";
        if (!FileUploadHelper.exists(tempPath)) {
            File savedir = new File(tempPath);
            savedir.mkdir();
        }
        
        // 90일이 지난 file 모두 삭제
        File savedir = new File(tempPath);
        if(savedir.length()>0){
        	File[] files = savedir.listFiles();
        	
        	if(files != null){
        		String savename = null;
                String deleteDate = null;
                
                try{
                	deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -90); 
                }catch (ParseException pe){
                	pe.printStackTrace();
                }
                boolean isDel = false;
                for (File file : files) {
                	savename = file.getName();
                    isDel = false;

                    // 파일길이 : 22이상, 확장자 : xls|zip
                    if (savename.length() > 22 && (savename.endsWith("xls") || savename.endsWith("xlsx"))) {
                        // 10일 지난 파일들 삭제
                    	int startPosition = savename.lastIndexOf(".")-12;
                    	int lastPosition = savename.lastIndexOf(".")-6;
                        if (savename.contains("HHU") && savename.substring(startPosition, lastPosition).compareTo(deleteDate) < 0) {
                            isDel = true;
                        }
                        if (isDel) {
                            file.delete();
                        }
                    }
                    savename = null;
                }
        	}
        }
        
        
        File uFile = new File(FileUploadHelper.makePath(tempPath, filename));

        if(FileUploadHelper.exists(FileUploadHelper.makePath(tempPath, filename))){

            if(FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(tempPath, filename))){

                multipartFile.transferTo(uFile);
            }
        }
        else multipartFile.transferTo(uFile);

        String filePath = tempPath+"/"+filename;

        String ext = filePath.substring(filePath.lastIndexOf(".")+1).trim();

        ModelAndView mav = new ModelAndView("gadget/device/deviceBulkFile");
        mav.addObject("tempFileName", filePath);
        mav.addObject("newFileName", filename);
        String[] titleAndMds = mduManager.getTitleName(filePath,ext);
        mav.addObject("titleName", titleAndMds[0]);
        mav.addObject("meterSerialNumber",titleAndMds[1]);

        return mav;
    }
	
	/**
	 * 엑셀 파일 읽기
	 */
	@RequestMapping(value="/gadget/mvm/getExcelResult.do")
	public ModelAndView getExcelResult(
													@RequestParam(value="loginId") String loginId,
													@RequestParam(value="mdsId") String mdsId,
													@RequestParam(value="dataType") String dataType,
													@RequestParam(value="filePath") String filePath,
													@RequestParam(value="supplierId") String supplierId,
													@RequestParam(value="vendorname") String vendorName,
													@RequestParam(value="modelname") String modelName,
													@RequestParam(value="uploadHistoryId") String uploadId){
		ModelAndView mav = new ModelAndView("jsonView");
		String resultMsg = "success";
		mdsId = mdsId.trim();
		dataType = dataType.toLowerCase();
		
		try {
			File file = new File(filePath.trim());
			if (!file.exists() || !file.isFile() || !file.canRead()) {
                throw new IOException(filePath);
            }
			
			List<Map<String, Object>> lineList = null;
			Map<String,Object> condition = new HashMap<String,Object>();
			
			
			//리스트 구조체 추출
			if (!"".equals(StringUtils.defaultIfEmpty(filePath, ""))) {
                String ext = filePath.substring(filePath.lastIndexOf(".") + 1).trim();
                if ("xls".equals(ext)) {
                	lineList = mduManager.readExcel_XLS(filePath, dataType, supplierId);
                } else if ("xlsx".equals(ext)) {
                	lineList = mduManager.readExcel_XLSX(filePath, dataType, supplierId);
                }
            }
			
			//검침 데이터 저장 처리
			if(lineList != null && !lineList.isEmpty()){
				Map<String,Object> saveResult = new HashMap<String,Object>();
				String uHistId = null;
				if(uploadId.length()>4){
					// 재업로드 이력 업데이트 (업로드 시간 갱신)
					uHistId = mduManager.updateUploadHistory_basicInfo(uploadId, loginId, mdsId);
				}else{
					// 업로드 이력  생성 (기본정보만입력)
					uHistId = mduManager.addUploadHistory_basicInfo(loginId, mdsId);
				}
				
				if(uHistId==null){
					resultMsg = "Server failed to record the upload history";
					mav.addObject("resultMsg", resultMsg);
					return mav;
				} else ; 				
								
				// 데이터 저장
				if(dataType.contains("energy") || dataType.contains("1")){
					saveResult = mduManager.saveLPFromList2(lineList, mdsId, uHistId, modelName);
					condition = new HashMap<String,Object>();
					if(saveResult!=null){												
						condition.put("meterRegistration", "1");
						condition.put("uploadId", uHistId);
						condition.put("loginId", loginId);
						condition.put("dataType", "1");						
						condition.put("startDate", saveResult.get("startDate").toString());
						condition.put("endDate", saveResult.get("endDate").toString());
						condition.put("totalCnt", lineList.size());						
						condition.put("failCnt", saveResult.get("failCnt"));
						condition.put("successCnt", saveResult.get("successCnt"));
						condition.put("filePath", filePath);						
						mduManager.updateUploadHistory_detailInfo(condition);						
					}else{
						//save 결과가 null인 경우는 미터가 등록되지 않았음을 의미
						condition.put("meterRegistration", "0");
						condition.put("loginId", loginId);
						condition.put("uploadId", uHistId);
						condition.put("dataType", "1");										
						condition.put("filePath", filePath);
						mduManager.updateUploadHistory_detailInfo(condition);						
					}
	            }else if(dataType.contains("daily") || dataType.contains("2")){
	            	saveResult = mduManager.saveDPFromList(lineList, mdsId, uHistId, modelName);
	            	condition = new HashMap<String,Object>();
	            	if(saveResult!=null){												
						condition.put("meterRegistration", "1");
						condition.put("uploadId", uHistId);
						condition.put("loginId", loginId);
						condition.put("dataType", "2");						
						condition.put("startDate", saveResult.get("startDate").toString());
						condition.put("endDate", saveResult.get("endDate").toString());
						condition.put("totalCnt", lineList.size());						
						condition.put("failCnt", saveResult.get("failCnt"));
						condition.put("successCnt", saveResult.get("successCnt"));
						condition.put("filePath", filePath);						
						mduManager.updateUploadHistory_detailInfo(condition);						
					}else{
						//save 결과가 null인 경우는 미터가 등록되지 않았음을 의미
						condition.put("meterRegistration", "0");
						condition.put("loginId", loginId);
						condition.put("uploadId", uHistId);
						condition.put("dataType", "2");										
						condition.put("filePath", filePath);
						mduManager.updateUploadHistory_detailInfo(condition);
					}
	            }else if(dataType.contains("month") || dataType.contains("3")){
	            	saveResult = mduManager.saveMPFromList(lineList, mdsId, uHistId, modelName);
	            	condition = new HashMap<String,Object>();
	            	if(saveResult!=null){												
						condition.put("meterRegistration", "1");
						condition.put("uploadId", uHistId);
						condition.put("loginId", loginId);
						condition.put("dataType", "3");						
						condition.put("startDate", saveResult.get("startDate").toString());
						condition.put("endDate", saveResult.get("endDate").toString());
						condition.put("totalCnt", lineList.size());						
						condition.put("failCnt", saveResult.get("failCnt"));
						condition.put("successCnt", saveResult.get("successCnt"));
						condition.put("filePath", filePath);						
						mduManager.updateUploadHistory_detailInfo(condition);						
					}else{
						//save 결과가 null인 경우는 미터가 등록되지 않았음을 의미
						condition.put("meterRegistration", "0");
						condition.put("loginId", loginId);
						condition.put("uploadId", uHistId);
						condition.put("dataType", "3");										
						condition.put("filePath", filePath);
						mduManager.updateUploadHistory_detailInfo(condition);
					}
	            }else if(dataType.contains("power") || dataType.contains("4")){
	            	saveResult = mduManager.savePQFromList(lineList, mdsId, uHistId, modelName);
	            	condition = new HashMap<String,Object>();
	            	if(saveResult!=null){												
						condition.put("meterRegistration", "1");
						condition.put("uploadId", uHistId);
						condition.put("loginId", loginId);
						condition.put("dataType", "4");						
						condition.put("startDate", saveResult.get("startDate").toString());
						condition.put("endDate", saveResult.get("endDate").toString());
						condition.put("totalCnt", lineList.size());						
						condition.put("failCnt", saveResult.get("failCnt"));
						condition.put("successCnt", saveResult.get("successCnt"));
						condition.put("filePath", filePath);						
						mduManager.updateUploadHistory_detailInfo(condition);						
					}else{
						//save 결과가 null인 경우는 미터가 등록되지 않았음을 의미
						condition.put("meterRegistration", "0");
						condition.put("loginId", loginId);
						condition.put("uploadId", uHistId);
						condition.put("dataType", "4");										
						condition.put("filePath", filePath);
						mduManager.updateUploadHistory_detailInfo(condition);
					}
	            }
				mav.addObject("result", saveResult);
			}
			
		}catch(FileNotFoundException e) {
            e.printStackTrace();
            logger.error(e.toString(), e);
        } catch(Exception e) {
            e.printStackTrace();
            logger.error(e.toString(), e);
        }
				
		return mav;
	}
	
	
	/**
	 * 파일 다운로드/엑셀 내보내기 팝업 호출
	 */
	@RequestMapping(value="/gadget/mvm/hhuFileDownloadPopup.do")
	public ModelAndView getFileDownload(){
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        ModelAndView mav = new ModelAndView("/gadget/mvm/handheldUnitDataUploadExcelDownloadPopup");
        mav.addObject("supplierId", supplierId);

        return mav;	
	}
	
	
	/**
	 * 이력 정보를 엑셀로 변환하여 출력
	 */
	@RequestMapping(value="/gadget/mvm/hhuMakeExcelExport.do")
	public ModelAndView makeExcelExportFile(
											@RequestParam(value="supplierId") String supplierId,
											@RequestParam(value="meterId") String meterId,
											@RequestParam(value="loginId") String loginId,
											@RequestParam(value="startDate") String startDate,
											@RequestParam(value="endDate") String endDate,
											@RequestParam(value="title") String title,
											@RequestParam(value="msgMap[]") String msgArr){
		ModelAndView mav = new ModelAndView("jsonView");
		// 검색 조건
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("meterId", meterId);
		condition.put("loginId", loginId);
		condition.put("startDate", startDate.concat("000000"));
		condition.put("endDate", endDate.concat("235959"));
		// 이력 조회
		result = mduManager.getUploadHistory(condition);
		
		// 파일 이름
		Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
        
        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();
        sbFileName.append(title);
        // File Name에 날짜 덧붙이기
        sbFileName.append(TimeUtil.getCurrentDateUsingFormat(""));
               
        boolean isLast = false;
        Long total = 0L;        // 데이터 조회건수
        Long maxRows = 5000L;   // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수
        //Length
        total = new Integer(result.size()).longValue();
        mav.addObject("total", total);
        if (total <= 0) {
            return mav;
        }
        //Message 생성
        String[] namesArr = msgArr.split(",");
        msgMap.put("msgLoginId", namesArr[0]);
        msgMap.put("msgUploadDate", namesArr[1]);
        msgMap.put("msgMeterId", namesArr[2]);
        msgMap.put("msgMeterReg", namesArr[3]);
        msgMap.put("msgDataType", namesArr[4]);
        msgMap.put("msgStartDate", namesArr[5]);
        msgMap.put("msgEndDate", namesArr[6]);
        msgMap.put("msgTotal", namesArr[7]);
        msgMap.put("msgFileName", namesArr[8]);       
        msgMap.put("msgNumber", namesArr[10]);
        msgMap.put("title", title);
        
        // Check download dir (서버에 저장된 오래된 파일 삭제)
        String filePath = namesArr[9];
        File downDir = new File(filePath);
        
        if (downDir.exists()) {
            File[] files = downDir.listFiles();

            if (files != null) {
                String filename = null;
                String deleteDate = null;

                try {
                    deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10);    // 10일 이전 일자
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                boolean isDel = false;

                for (File file : files) {
                    filename = file.getName();
                    isDel = false;

                    // 파일길이 : 22이상, 확장자 : xls|zip
                    if (filename.length() > 22 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
                        // 10일 지난 파일들 삭제
                        if (filename.startsWith(title) && filename.substring(22, 30).compareTo(deleteDate) < 0) {
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
        
        
        // Date Pattern
        String localeDatePattern = mduManager.getDatePatternFromLocale(supplierId);
        // 엑셀 파일 생성
        uploadHistoryMakeExcel wExcel = new uploadHistoryMakeExcel(localeDatePattern);
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
        	//데이터 행이 5000라인을 넘으면 분할 파일 생성
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
        try {
            zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // return object
        mav.addObject("filePath", filePath);
        mav.addObject("fileName", fileNameList.get(0));
        mav.addObject("zipFileName", sbZipFile.toString());
        mav.addObject("fileNames", fileNameList);
		return mav;
	}
	
	
	
}

/**
 * Excel 파일을 생성하고, 내용을 작성하는 클래스
 * @author SEJIN
 */
class uploadHistoryMakeExcel {
	private String datePattern = "";
	
	public uploadHistoryMakeExcel(){
		
	}
	
	public uploadHistoryMakeExcel(String _datePattern){
		// locale과 country code에 해당하는 date pattern에 hh:mm:ss가 연결되어있음.
		datePattern = _datePattern;
	}
	
	/**
	 * 업로드 이력을 기록한 엑셀 출력
	 * @param result : 이력 데이터
	 * @param msgMap : 각 column 제목
	 * @param isLast
	 * @param filePath
	 * @param fileName
	 */
	public void writeReportExcel(List<Map<String, Object>> result,Map<String, String> msgMap, 
			boolean isLast, String filePath, String fileName){
		
		try {
			HSSFWorkbook workbook = new HSSFWorkbook();

            HSSFFont fontTitle = workbook.createFont();
            fontTitle.setFontHeightInPoints((short)14);
            fontTitle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFFont fontHeader = workbook.createFont();
            fontHeader.setFontHeightInPoints((short)10);
            fontHeader.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            
            HSSFFont fontBody = workbook.createFont();
            fontBody.setFontHeightInPoints((short)10);
            fontBody.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
            
            HSSFRow row = null;
            HSSFCell cell = null;
            
            HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
            HSSFCellStyle noCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 1, 0);
            HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
            HSSFCellStyle data2CellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 2, 0);
            
            // Single Data
            Map<String, Object> resultMap = new HashMap<String, Object>();
            
            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int historyStartRow = 3;
            int totalColumnCnt = 9;            
            
            HSSFSheet sheet = workbook.createSheet(reportTitle);
            
            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 10);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 25);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 25);
            
            // 최상단 타이틀 (0부터 시작)
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));
            
            // 소제목
            row = sheet.createRow(historyStartRow);
            cell = row.createCell(0);
            cell.setCellValue("List of Upload History");
            cell.setCellStyle(titleCellStyle);
            sheet.addMergedRegion(new CellRangeAddress(historyStartRow, (short) historyStartRow, 0, (short) (totalColumnCnt-1)));
            
            // 업로드 이력의 Column Name (항목)
            historyStartRow = historyStartRow + 1; 
            row = sheet.createRow(historyStartRow);
            
            cell = row.createCell(0);
            cell.setCellValue(msgMap.get("msgNumber"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(1);
            cell.setCellValue(msgMap.get("msgLoginId"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(2);
            cell.setCellValue(msgMap.get("msgUploadDate"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(3);
            cell.setCellValue(msgMap.get("msgMeterId"));
            cell.setCellStyle(titleCellStyle);
			
            cell = row.createCell(4);
            cell.setCellValue(msgMap.get("msgMeterReg"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(5);
            cell.setCellValue(msgMap.get("msgDataType"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(6);
            cell.setCellValue(msgMap.get("msgStartDate"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(7);
            cell.setCellValue(msgMap.get("msgEndDate"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(8);
            cell.setCellValue(msgMap.get("msgTotal"));
            cell.setCellStyle(titleCellStyle);
            
            //Column Content (업로드 이력 데이터)
            int dataCount = 0;
            dataCount = result.size();
            for(int i=0; i<dataCount; i++){
            	int no = i+1;
                resultMap = result.get(i);
                row = sheet.createRow(i+ (historyStartRow + 1));    
                
                cell = row.createCell(0);
                cell.setCellValue(""+no);
                cell.setCellStyle(noCellStyle);
                
                cell = row.createCell(1);
                cell.setCellValue(resultMap.get("loginid").toString());
                cell.setCellStyle(dataCellStyle);
                
                cell = row.createCell(2);
                if(resultMap.containsKey("uploaddate")){
                	String fTime = timeLocFormat((resultMap.get("uploaddate").toString()));
                	cell.setCellValue(fTime);
                }else {
                	cell.setCellValue("-");
                }                
                cell.setCellStyle(dataCellStyle);
                
                cell = row.createCell(3);
                cell.setCellValue(resultMap.get("meterid").toString());
                cell.setCellStyle(dataCellStyle);
                
                cell = row.createCell(4);
                cell.setCellValue(messageTransform("reg",resultMap.get("meterreg").toString()));
                cell.setCellStyle(dataCellStyle);
                
                cell = row.createCell(5);
                cell.setCellValue(messageTransform("type",resultMap.get("datatype").toString()));
                cell.setCellStyle(dataCellStyle);
                
                cell = row.createCell(6);
                if(resultMap.containsKey("startdate")){
                	String fTime = timeLocFormat((resultMap.get("startdate").toString()));
                	cell.setCellValue(fTime);
                }else {
                	cell.setCellValue("-");
                }                
                cell.setCellStyle(dataCellStyle);
                
                cell = row.createCell(7);
                if(resultMap.containsKey("enddate")){
                	String fTime = timeLocFormat((resultMap.get("enddate").toString()));
                	cell.setCellValue(fTime);
                }else {
                	cell.setCellValue("-");
                }                
                cell.setCellStyle(dataCellStyle);
                
                cell = row.createCell(8);
                cell.setCellValue(resultMap.get("cnt").toString());
                cell.setCellStyle(dataCellStyle);
            }
            
            //파일 생성
            FileOutputStream fs = null;
            try {
                fs = new FileOutputStream(fileFullPath);
                workbook.write(fs);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fs != null) fs.close();
            }
            
            
		} catch (Exception e) {
		    e.printStackTrace();
		} //End Try
		
	}
	
	
	/**
     * String Time을 입력받아 구분자(-)를 삽입하여 반환
     * @param inTime String
     */
    public String timeFormat(String inTime){
    	String outTime = null;
    	if(inTime.length()==10){
    		//yyyy mm dd hh -> yyyy-mm-dd hh시
    		outTime = inTime.substring(0,4) + "-" + inTime.substring(4,6) + "-" + inTime.substring(6,8) + " " + inTime.substring(8,10) + "시";    		
    	}else if(inTime.length()==8){
    		//yyyy mm dd -> yyyy-mm-dd
    		outTime = inTime.substring(0,4) + "-" + inTime.substring(4,6) + "-" + inTime.substring(6,8);
    	}else
    		return inTime;    	
    	return outTime;
    }
    
    /**
     * 주어진 Date Pattern과 일치하도록 시간을 변환
     * @param inTime
     */
    public String timeLocFormat(String inTime){
    	if(inTime.equals("-")){
    		return "-";
    	}
    	String outTime = null;    	
    	SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
    	Date cellTime = null;
    	try {    		
			cellTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(inTime);			
			outTime = sdf.format(cellTime);
		} catch (ParseException e) {
			e.printStackTrace();
			return outTime;
		}
    	return outTime;
    }
    
    /**
     * 메시지 타입에 알맞은 문자열로 변환
     * @param inCol : 미터 등록 여부, 데이터 타입 여부
     * @param inValue : flag
     */
    public String messageTransform(String inCol, String inValue){
    	String outValue = null;
    	if(inCol.contains("reg")){
    		if(inValue.equals("1")) outValue = "1. YES";
    		if(inValue.equals("0")) outValue = "0. NO";
    			
    	}else if(inCol.contains("type")){
    		if(inValue.equals("1")) outValue = "1. Energy";
    		if(inValue.equals("2")) outValue = "2. Daily";
    		if(inValue.equals("3")) outValue = "3. Monthly";
    	}    	
    	
    	return outValue;
    }
    
    /**
     * 소수점이 포함된 String을 입력받아 소수점 이전 2자리까지만 끊어서 반환
     * @param inValue
     */
    public String fixedNumber(String inValue){
    	String outValue = null;
    	int pointPosition = inValue.indexOf('.');
    	if(pointPosition > 0){
    		outValue = inValue.substring(0, pointPosition+2);    		
    	}else 
    		return inValue;
    	
    	return outValue;
    }
        
}




