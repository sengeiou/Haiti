package com.aimir.service.mvm.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants.ElectricityChannel;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.UploadHistoryEMDao;
import com.aimir.dao.mvm.UploadHistoryEM_FailListDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.mvm.LpEM;
import com.aimir.model.mvm.UploadHistoryEM;
import com.aimir.model.mvm.UploadHistoryEM_FailList;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.HandheldUnitDataUploadManager_MOE;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.Condition.Restriction;

/**
 * 검침 데이터 수동 업로드 가젯을 위한 Manager(전기)
 * 함수 기능 설명은 Interface 참조바랍니다.
 * @author SEJIN HAN
 *
 */
@Service(value = "MeteringManualUploadManager")
@Transactional(readOnly=false)
public class HandheldUnitDataUploadManagerImpl_MOE implements HandheldUnitDataUploadManager_MOE {

	private static Log logger = LogFactory.getLog(HandheldUnitDataUploadManagerImpl_MOE.class);
	
	// 엑셀 파일 저장 위치
    private String ctxRoot;    
    
    @Resource(name="transactionManager")
    protected HibernateTransactionManager txmanager;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    UploadHistoryEMDao uploadEmDao;
    
    @Autowired
    UploadHistoryEM_FailListDao uploadEmFailDao;
    
    @Autowired
    LpEMDao lpEMDao;
    
    // @Autowired
    // HandheldUnitDataSaver hhuXMLsaver;

    protected boolean save(IMeasurementData md) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }
    

    @Override
    public String addUploadHistory_basicInfo(String loginId, String mdsId) {
        String timeStamp = DateTimeUtil.getCurrentDateTimeByFormat("");
        try{
            UploadHistoryEM uHist = new UploadHistoryEM();          
            uHist.setId(timeStamp);
            uHist.setLoginId(loginId);
            uHist.setMdsId(mdsId);
            uHist.setUploadDate(timeStamp);
            uploadEmDao.add(uHist);
        } catch (Exception e){
            logger.error(e, e);
            return null;
        }
        
        return timeStamp;
    }
    
    public String updateUploadHistory_basicInfo(String uploadId, String loginId, String mdsId){
        String timeStamp = DateTimeUtil.getCurrentDateTimeByFormat("");
        String uid = "";
        try{
            UploadHistoryEM uHist = uploadEmDao.getUploadHistory(uploadId);
            //uHist.setId(timeStamp);
            uHist.setLoginId(loginId);
            uHist.setMdsId(mdsId);
            uHist.setUploadDate(timeStamp);
            uploadEmDao.update(uHist);
            uid = uHist.getId();
        } catch (Exception e){
            logger.error(e, e);
            return null;
        }       
        return uid;     
    }
    
    @Override
    public String updateUploadHistory_detailInfo(Map<String, Object> conditionMap) {
        String timeStamp = DateTimeUtil.getCurrentDateTimeByFormat("");
        UploadHistoryEM uHist = uploadEmDao.getUploadHistory(conditionMap.get("uploadId").toString());      
        // 공통
        uHist.setMeterRegistration(conditionMap.get("meterRegistration").toString());
        uHist.setLoginId(conditionMap.get("loginId").toString());
        uHist.setUploadDate(timeStamp);
        uHist.setFilePath(conditionMap.get("filePath").toString());
        uHist.setDataType(Integer.parseInt(conditionMap.get("dataType").toString()));
                
        // 미터 등록 여부에 따라 바뀌는 항목
        if(conditionMap.containsKey("startDate")){
            uHist.setStartDate(conditionMap.get("startDate").toString());
        }
        if(conditionMap.containsKey("endDate")){
            uHist.setEndDate(conditionMap.get("endDate").toString());
        }
        if(conditionMap.containsKey("totalCnt")){
            uHist.setTotalCnt(Integer.parseInt(conditionMap.get("totalCnt").toString()));
        }
        if(conditionMap.containsKey("failCnt")){
            uHist.setFailCnt(Integer.parseInt(conditionMap.get("failCnt").toString()));
        }
        if(conditionMap.containsKey("successCnt")){
            uHist.setSuccessCnt(Integer.parseInt(conditionMap.get("successCnt").toString()));
        }
        uploadEmDao.update(uHist);
        return null;
    }
    
    public String addUploadFailHistory(Map<String,Object> conditionMap){
        UploadHistoryEM_FailList uFList = new UploadHistoryEM_FailList();
        UploadHistoryEM uHist = uploadEmDao.getUploadHistory(conditionMap.get("uploadId").toString());
        uFList.setUploadId(uHist);
        uFList.setRowLine(Integer.parseInt(conditionMap.get("rowLine").toString()));
        uFList.setFailReason(conditionMap.get("failReason").toString());
        uFList.setDataType(Integer.parseInt(conditionMap.get("dataType").toString()));
        uFList.setMeteringTime(conditionMap.get("meteringTime").toString());
        uFList.setMdValue(conditionMap.get("mdValue").toString());
        uploadEmFailDao.add(uFList);
        return null;
    }

    
    @Override
    public List<Map<String,Object>>getUploadHistory(Map<String, Object> condition) {        
        // 주어진 검색 조건에 해당하는 이력 리스트 반환        
        List<UploadHistoryEM> list = uploadEmDao.getUploadHistory_List(condition);
        
        // 결과 분류
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
        if(list == null) return result;
        Map<String,Object> output = new HashMap<String,Object>();
        int listSize = list.size();
        for(int a=0; a<listSize; a++){
            output = new HashMap<String,Object>();
            UploadHistoryEM uh = list.get(a);
            output.put("uploadid", uh.getId());
            output.put("loginid", (String)ObjectUtils.defaultIfNull(uh.getLoginId(),"-"));
            output.put("uploaddate", (String)ObjectUtils.defaultIfNull(uh.getUploadDate(),"-"));
            output.put("meterid", (String)ObjectUtils.defaultIfNull(uh.getMdsId(),"-"));
            output.put("meterreg", (String)ObjectUtils.defaultIfNull(uh.getMeterRegistration(),"-"));
            int dtInt = (Integer)ObjectUtils.defaultIfNull(uh.getDataType(),-1);
            output.put("datatype","-");
            if(dtInt >= 0){
                output.put("datatype", (String)ObjectUtils.defaultIfNull(uh.getDataType().toString(),"-"));
            }           
            output.put("startdate", (String)ObjectUtils.defaultIfNull(uh.getStartDate(),"-"));
            output.put("enddate", (String)ObjectUtils.defaultIfNull(uh.getEndDate(),"-"));
            int cntInt = (Integer)ObjectUtils.defaultIfNull(uh.getTocalCnt(),-1);
            String cntStr = "-";
            if(cntInt >= 0){
                cntStr = uh.getTocalCnt().toString().concat("(");
                cntStr = cntStr.concat(uh.getSuccessCnt().toString()).concat("/");
                cntStr = cntStr.concat(uh.getFailCnt().toString()).concat(")");
            }           
            output.put("cnt", cntStr);
            output.put("filepath", uh.getFilePath());
            result.add(output);
        }
        
        return result;
    }
    
    //공급사 국가 코드에 맞는 시간 포맷으로 "date" 데이터를 변환한 후 이력 반환
    public List<Map<String,Object>>getUploadHistoryWithLocTime(Map<String, Object> condition,String supplierId) {       
        // 주어진 검색 조건에 해당하는 이력 리스트 반환        
        List<UploadHistoryEM> list = uploadEmDao.getUploadHistory_List(condition);
        
        // 공급사를 통해 locale,country code찾아서 date 패턴 탐색
        String datePattern = getDatePatternFromLocale(supplierId);
        if(datePattern == null){
            datePattern = "dd/mm/yyyy hh:mm:ss";
        }
        
        // 결과 분류
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
        if(list == null) return result;
        Map<String,Object> output = new HashMap<String,Object>();
        int listSize = list.size();
        for(int a=0; a<listSize; a++){
            output = new HashMap<String,Object>();
            UploadHistoryEM uh = list.get(a);
            output.put("uploadid", uh.getId());
            output.put("loginid", (String)ObjectUtils.defaultIfNull(uh.getLoginId(),"-"));          
            output.put("meterid", (String)ObjectUtils.defaultIfNull(uh.getMdsId(),"-"));
            output.put("meterreg", (String)ObjectUtils.defaultIfNull(uh.getMeterRegistration(),"-"));
            
            String tempDate = (String)ObjectUtils.defaultIfNull(uh.getUploadDate(),"-");
            output.put("uploaddate", timeLocFormat(tempDate, datePattern));
            tempDate = (String)ObjectUtils.defaultIfNull(uh.getStartDate(),"-");
            output.put("startdate", timeLocFormat(tempDate, datePattern));
            tempDate = (String)ObjectUtils.defaultIfNull(uh.getEndDate(),"-");
            output.put("enddate", timeLocFormat(tempDate, datePattern));
            
            int dtInt = (Integer)ObjectUtils.defaultIfNull(uh.getDataType(),-1);
            output.put("datatype","-");
            if(dtInt >= 0){
                output.put("datatype", (String)ObjectUtils.defaultIfNull(uh.getDataType().toString(),"-"));
            }           
            
            int cntInt = (Integer)ObjectUtils.defaultIfNull(uh.getTocalCnt(),-1);
            String cntStr = "-";
            if(cntInt >= 0){
                cntStr = uh.getTocalCnt().toString().concat("(");
                cntStr = cntStr.concat(uh.getSuccessCnt().toString()).concat("/");
                cntStr = cntStr.concat(uh.getFailCnt().toString()).concat(")");
            }           
            output.put("cnt", cntStr);
            output.put("filepath", uh.getFilePath());
            result.add(output);
        }
        
        return result;
    }

    public List<Map<String,Object>> getFailedUploadHistory(Map<String, Object> condition) {
        // 주어진 업로드 아이디에 해당하는 실패리스트 반환
        String uId = condition.get("uploadId").toString();
        List<UploadHistoryEM_FailList> list = uploadEmFailDao.getUploadFailHistory(uId);
        if(list == null) return null;
        // 결과 분류
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
        Map<String,Object> output = new HashMap<String,Object>();
        int listSize = list.size();
        for(int i=0; i<listSize; i++){
            output = new HashMap<String,Object>();
            UploadHistoryEM_FailList fl = list.get(i);
            output.put("rowline", fl.getRowLine().toString());
            output.put("uploadid", fl.getUploadId().getId());
            output.put("failreason", (String)ObjectUtils.defaultIfNull(fl.getFailReason(),"-"));
            output.put("meteringtime", (String)ObjectUtils.defaultIfNull(fl.getMeteringTime(),"-"));
            String cellNames = null;
            if(fl.getDataType()>1){
                cellNames = getDpMpCellNames(fl.getMdValue());
            }else{
                cellNames = getLpCellNames(fl.getMdValue());
            }
            output.put("mdvalue", cellNames);
            result.add(output);
        }
        
        return result;
    }
    
    // 컬럼 이름을 붙여서 반환 (load profile)
    public String getLpCellNames(String _mdVal){
        String[] mdValues = _mdVal.split("\\|");
        String resultMd = "";
        resultMd = resultMd.concat(" Consumption active energy -import for LP1 (kWh): " + mdValues[1].trim());
        resultMd = resultMd.concat(", Consumption active energy -export for LP1 (kWh): " + mdValues[2].trim());
        resultMd = resultMd.concat(", Consumption reactive energy -import for LP1 (kvarh): " + mdValues[3].trim());
        resultMd = resultMd.concat(", AMR profile status: " + mdValues[4].trim());
//      resultMd = resultMd.concat(" Active power -export (kW): " + mdValues[1].trim());
//      resultMd = resultMd.concat(", Active power -import (kW): " + mdValues[2].trim());
//      resultMd = resultMd.concat(", Reactive power -export (kvar): " + mdValues[3].trim());
//      resultMd = resultMd.concat(", Reactive power -import (kvar): " + mdValues[4].trim());
//      resultMd = resultMd.concat(", Total export apparent power (QII+QIII) (kVA): " + mdValues[5].trim());
//      resultMd = resultMd.concat(", Total import apparent power (QI+QIV) (kVA): " + mdValues[6].trim());
//      resultMd = resultMd.concat(", L1 voltage (V): " + mdValues[7].trim());
//      resultMd = resultMd.concat(", L2 voltage (V): " + mdValues[8].trim());
//      resultMd = resultMd.concat(", L3 voltage (V): " + mdValues[9].trim());
//      resultMd = resultMd.concat(", L1 current (A): " + mdValues[10].trim());
//      resultMd = resultMd.concat(", L2 current (A): " + mdValues[11].trim());
//      resultMd = resultMd.concat(", L3 current (A): " + mdValues[12].trim());
//      resultMd = resultMd.concat(", L1 power factor : " + mdValues[13].trim());
//      resultMd = resultMd.concat(", L2 power factor : " + mdValues[14].trim());
//      resultMd = resultMd.concat(", L3 power factor : " + mdValues[15].trim());
        
        return resultMd;
    }
    
    // 컬럼 이름을 붙여서 반환 (Daily, Month)
    public String getDpMpCellNames(String _mdVal){
        String[] mdValues = _mdVal.split("\\|");
        String resultMd = "";
        resultMd = resultMd.concat(" Cumulative active energy -import (kWh): " + mdValues[1].trim());
        resultMd = resultMd.concat(", Cumulative active energy -import rate 1 (kWh): " + mdValues[2].trim());
        resultMd = resultMd.concat(", Cumulative active energy -import rate 2 (kWh): " + mdValues[3].trim());
        resultMd = resultMd.concat(", Cumulative reactive energy -import (kvarh): " + mdValues[4].trim());
        resultMd = resultMd.concat(", Cumulative reactive energy -import rate 1 (kvarh): " + mdValues[5].trim());
        resultMd = resultMd.concat(", Cumulative reactive energy -import rate 2 (kvarh): " + mdValues[6].trim());
        if(mdValues.length > 8){
            resultMd = resultMd.concat(", Total maximum demand +A (kW): " + mdValues[7].trim());
            resultMd = resultMd.concat(", Total maximum demand +A(Capture Time): " + mdValues[8].trim());
        }
        return resultMd;
    }

    @Override
    public String[] getTitleName(String excel, String ext) {

        StringBuffer sb = new StringBuffer();
        String meterNo = null;
        
        try {
            // check file
            File file = new File(excel.trim()); // jhkim
            Row titles = null;
            

            if ("xls".equals(ext)) {
                // Workbook
                HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
                titles = (wb.getSheetAt(0)).getRow(0);
                meterNo = checkMeterNo_xls((wb.getSheetAt(0)).getRow(1));
            } else if ("xlsx".equals(ext)) {
                // Workbook
                XSSFWorkbook wb = new XSSFWorkbook(excel.trim());
                titles = (wb.getSheetAt(0)).getRow(0);
                meterNo = checkMeterNo_xlsx((wb.getSheetAt(0)).getRow(1));
            }

            for (Cell cell : titles) {

                if (cell.getColumnIndex() > 0)
                    sb.append(',');

                sb.append(cell.getRichStringCellValue().getString());
            }                        

        } catch(IOException ie) {
            ie.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        String sbt = sb.toString();
        sbt = sbt.substring(0, sbt.indexOf(",")).trim();
        String[] resultArr = new String[2];
        resultArr[0] = sbt;
        resultArr[1] = meterNo;
        return resultArr;
        
    }   

    @Override
    public List<Map<String, Object>> readExcel_XLS(String excel, String dataType, String supplierId) {
        ctxRoot = excel.substring(0,excel.lastIndexOf("/"));
        
        List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
        Map<String,Object> line = new HashMap<String,Object>();
        
        // 공급사를 통해 locale,country code찾아서 date 패턴 탐색
        Supplier supplier = supplierDao.getSupplierById(Integer.parseInt(supplierId));
        String datePattern = getDatePatternFromLocale(supplierId);
        if(datePattern == null){
            datePattern = "dd/mm/yyyy hh:mm:ss";
        }
        
        // Decimal Pattern
        DecimalFormat mdp = DecimalUtil.getDecimalFormat(supplier.getMd());     
        
        try {
            File file = new File(excel.trim());     
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
                
            // Text Extraction
            ExcelExtractor extractor = new ExcelExtractor(wb);
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);
            
            // 첫번째 시트
            HSSFSheet sheet = wb.getSheetAt(0);
            int rowCnt = sheet.getPhysicalNumberOfRows(); 
            
            // 컬럼 이름 확인 (데이터 타입이 유효한지 파일 편집이 있었는지 체크)
            HSSFRow testRow = sheet.getRow(2);
            String[] cellNames = null;
            // 데이터 타입에 따라 다른 컬럼 테스트 (daily, month는 같은 함수)
            dataType = dataType.toLowerCase();
            if(dataType.contains("energy") || dataType.contains("1")){
                cellNames = readLoadProfile_XLS_cellNameTest2(testRow);
            }else if(dataType.contains("daily") || dataType.contains("2")){
                cellNames = readDailyMonth_XLS_cellNameTest(testRow);
            }else if(dataType.contains("month") || dataType.contains("3")){
                cellNames = readDailyMonth_XLS_cellNameTest(testRow);
            }else if(dataType.contains("power") || dataType.contains("4")){
                cellNames = readPowerProfile_XLS_cellNameTest(testRow);
            }
            
            if(cellNames==null){
                logger.info("[MeteringDataManualUpload] Cell Name Error");
                return null;
            }
            
            // 컬럼 이름에 포함된 단위 확인 (kWh or Wh)
            double kRate = 1.0;
            kRate = check_XLS_Unit(testRow);
            
            
            // row 3~last : exclude title and model name at the top of document
            for(int r=3; r<rowCnt; r++){
                 HSSFRow row = sheet.getRow(r);
                 
                 // row가 공백이면 에러 발생
                 if(row==null) continue;
                 
                 int cellCnt = row.getPhysicalNumberOfCells();
                 line = new HashMap<String,Object>();
                 
                 // entire cell
                 for(int c=0; c<cellCnt; c++){
                     HSSFCell cell = row.getCell(c);
                     // cell이 공백이면 에러 발생
                     if(cell==null) continue;        
                     
                     // 패턴 검색을 통해 숫자만 남기고, 단위 표기를 제거
                     String value = cell.getStringCellValue();
                     Matcher matcher = Pattern.compile("\\d+").matcher(value);                   
                     int strIdx = 0;
                     while(matcher.find()){
                         strIdx = matcher.end();
                     }                                       
                     value = value.substring(0, strIdx);
                     
                     // clock 컬럼은 시간값 변환 (-> yyyyMMddhhmmss)
                     Date cellTime = null;
                     if(cellNames[c].contains("clock")){
                         String timeValue = cell.getStringCellValue();
                         timeValue = timeValue.replaceAll("-", "/");
                         SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                         cellTime = sdf.parse(timeValue);       
                         value = DateTimeUtil.getDateString(cellTime);
                     }
                     
                     // capture time 컬럼은 시간값 변환 (-> yyyyMMddhhmmss)                  
                     if(cellNames[c].contains("capture")){
                         String timeValue = cell.getStringCellValue();
                         timeValue = timeValue.replaceAll("-", "/");
                         SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                         cellTime = sdf.parse(timeValue);       
                         value = DateTimeUtil.getDateString(cellTime);
                     }
                     
                     // 컬럼 이름이 "-"이면 값을 0으로 입력 (단상, 3상등 미터 종류에 따라 안들어오는 데이터가 있음)
                     if(cellNames[c].equals("-")){
                         value = "0";
                     }
                     
                     if(value!=null){
                         if(!cellNames[c].contains("index") && !cellNames[c].contains("capture") && !cellNames[c].contains("clock")){
                             // 시간값이 아닌 경우에 한하여 kRate를 곱해서 단위변환 처리
                             double dValue = Double.parseDouble(value);
                             dValue = dValue * kRate;
                             // 공급사에 해당하는 measurement decimal format으로 변환
                             value = mdp.format(dValue);
                             //value = Double.toString(dValue);
                         }
                         line.put(""+c, value);
                     }
                 }
                 result.add(line);
            }
            
        }catch(IOException ie) {
            ie.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return result;
    }


    @Override
    public List<Map<String, Object>> readExcel_XLSX(String excel, String dataType, String supplierId) {
        ctxRoot = excel.substring(0,excel.lastIndexOf("/"));
        
        List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
        Map<String,Object> line = new HashMap<String,Object>();
        
        // 공급사를 통해 locale,country code찾아서 date 패턴 탐색
        // 부득이하게 언어 설정이 달라서 날짜 표기방법이 해당 국가와 일치하지 않을 경우 적용
        Supplier supplier = supplierDao.getSupplierById(Integer.parseInt(supplierId));
        Locale[] locs = Locale.getAvailableLocales();
        String datePattern = null;
        for(Locale lc : locs){
            if(lc.getCountry().equals(supplier.getCountry().getCode_2letter())){
                String langCode = lc.getLanguage();
                DateFormat dateInstance = DateFormat.getDateInstance(DateFormat.MEDIUM,lc);
                String formats = dateInstance.format(new Date());               
                String pattern = ((SimpleDateFormat) dateInstance).toPattern();
                datePattern = pattern.concat(" hh:mm:ss"); //공백을 한칸 두었음
                break;
            }
        }
        if(datePattern == null){
            datePattern = "dd/mm/yyyy hh:mm:ss";
        }
        
        // Decimal Pattern
        DecimalFormat mdp = DecimalUtil.getDecimalFormat(supplier.getMd());     
        
        try {
            File file = new File(excel.trim());     
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
                
            // Text Extraction
            XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);          
            
            // 첫번째 시트를 읽음
            XSSFSheet sheet = wb.getSheetAt(0);
            int rowCnt = sheet.getPhysicalNumberOfRows();
            
            // Phase 확인 (단상, 3상 구별)
            XSSFRow testRow = sheet.getRow(1);
            XSSFCell testCell = testRow.getCell(0); 
            //String inputPhase = checkPhase(testCell);
            
            // 컬럼 이름 확인 (데이터 타입 유효성, 파일 편집 가능성 체크)
            testRow = sheet.getRow(2);
            String[] cellNames = null;
            // 데이터 타입에 따라 다른 컬럼 테스트 (daily, month는 같은 함수)
            dataType = dataType.toLowerCase();
            if(dataType.contains("energy") || dataType.contains("1")){
                cellNames = readLoadProfile_XLSX_cellNameTest2(testRow);
            }else if(dataType.contains("daily") || dataType.contains("2")){
                cellNames = readDailyMonth_XLSX_cellNameTest(testRow);
            }else if(dataType.contains("month") || dataType.contains("3")){
                cellNames = readDailyMonth_XLSX_cellNameTest(testRow);
            }else if(dataType.contains("power") || dataType.contains("4")){
                cellNames = readPowerProfile_XLSX_cellNameTest(testRow);
            }
            if(cellNames==null){
                logger.info("[MeteringDataManualUpload] Cell Name Error");
                return null;
            }
            
            // 컬럼 이름에 포함된 단위 확인 (kWh or Wh)
            double kRate = 1.0;
            kRate = check_XLSX_Unit(testRow);
            
            
            // (행)row 3~last : exclude title and model name at the top of document
            for(int r=3; r<rowCnt; r++){
                
                 XSSFRow row = sheet.getRow(r);
                 // row가 공백인 경우 에러 발생
                 if(row==null) continue;
                 
                 int cellCnt = row.getPhysicalNumberOfCells();
                 line = new HashMap<String,Object>();
                 
                 // (전체 열) entire cell
                 for(int c=0; c<cellCnt; c++){
                     
                     XSSFCell cell = row.getCell(c);
                     // cell이 공백인 경우 에러 발생
                     if(cell==null) continue;
                     
                    //패턴 검색을 통해 가장 마지막 숫자 위치를 조회하고, 이를 통해 단위 표기 제거
                     String value = cell.getStringCellValue();
                     Matcher matcher = Pattern.compile("\\d+").matcher(value);                   
                     int strIdx = 0;
                     while(matcher.find()){
                         strIdx = matcher.end();
                     }                                       
                     value = value.substring(0, strIdx);
                     
                     // clock 컬럼은 시간값 변환 (-> yyyyMMddhhmmss)
                     Date cellTime = null;
                     if(cellNames[c].contains("clock")){
                         String timeValue = cell.getStringCellValue();
                         timeValue = timeValue.replaceAll("-", "/");
                         SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                         cellTime = sdf.parse(timeValue);       
                         value = DateTimeUtil.getDateString(cellTime);
                     }
                     
                     // capture time 컬럼은 시간값 변환 (-> yyyyMMddhhmmss)                  
                     if(cellNames[c].contains("capture")){
                         String timeValue = cell.getStringCellValue();
                         timeValue = timeValue.replaceAll("-", "/");
                         SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                         cellTime = sdf.parse(timeValue);       
                         value = DateTimeUtil.getDateString(cellTime);
                     }
                     
                     // 컬럼 이름이 "-"이면 값을 0으로 입력 (단상, 3상등 미터 종류에 따라 안들어오는 데이터가 있음)
                     if(cellNames[c].equals("-")){
                         value = "0";
                     }
                     
                     if(value!=null){
                         if(!cellNames[c].contains("index") && !cellNames[c].contains("capture") && !cellNames[c].contains("clock")){
                             // 시간값이 아닌 경우에 한하여 kRate를 곱해서 단위변환 처리
                             double dValue = Double.parseDouble(value);
                             dValue = dValue * kRate;
                             // 공급사에 해당하는 measurement decimal format으로 변환
                             value = mdp.format(dValue);
                             //value = Double.toString(dValue);
                         }
                         
                         line.put(""+c, value);
                     }
                     
                 }
                 result.add(line);           
            }
            
        }catch(IOException ie) {
            ie.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return result;
    }       


    // 기존과 기능은 같지만, 파일의 Column이 전체 변경되었음
    // Power Quality 데이터는 들어오지 않고, LP만 고려함
    public String[] readLoadProfile_XLSX_cellNameTest2(Object _testRow){
        String[] cellName = new String[6];
        XSSFRow testRow = (XSSFRow)_testRow;
        XSSFCell testCell = testRow.getCell(0); 
        
        String testVal = "";
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("index")) return null;
            cellName[0] = testVal;
        }else return null;
        testCell = testRow.getCell(1);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("clock")) return null;
            cellName[1] = testVal;
        }else return null;
        testCell = testRow.getCell(2);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("import for lp1")) return null;
            cellName[2] = testVal;
        }else return null;
        testCell = testRow.getCell(3);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("export for lp1")) return null;
            cellName[3] = testVal;
        }else return null;
        testCell = testRow.getCell(4);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("consumption reactive energy")) return null;
            cellName[4] = testVal;
        }else return null;
        testCell = testRow.getCell(5);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("amr profile status")) return null;
            cellName[5] = testVal;
        }else return null;
        
        return cellName;
    }
    
    // LP채널 변경에 따라 Energy에 붙어있던 Power항목이 별도 파일로 분리되었음.
    public String[] readPowerProfile_XLSX_cellNameTest(Object _testRow) {
        String[] cellName = new String[12];     
        XSSFRow testRow = (XSSFRow)_testRow;
        XSSFCell testCell = testRow.getCell(0);     
        
        String testVal = "";
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("index")) return null;
            cellName[0] = testVal;
        }else return null;
        testCell = testRow.getCell(1);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("clock")) return null;
            cellName[1] = testVal;
        }else return null;
        testCell = testRow.getCell(2);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("active power -import")) return null;
            cellName[2] = testVal;
        }else return null;
        testCell = testRow.getCell(3);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("active power -export")) return null;
            cellName[3] = testVal;
        }else return null;
        testCell = testRow.getCell(4);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive power -import")) return null;
            cellName[4] = testVal;
        }else return null;
        testCell = testRow.getCell(5);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive power -export")) return null;
            cellName[5] = testVal;
        }else return null;
        testCell = testRow.getCell(6);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("total import apparent power")) return null;
            cellName[6] = testVal;
        }else return null;
        testCell = testRow.getCell(7);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("total export apparent power")) return null;
            cellName[7] = testVal;
        }else return null;
        // Power Quality
        testCell = testRow.getCell(8);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l1 voltage")){
                cellName[8] = testVal;
            }else{
                cellName[8] = "-";
            }           
        }else return null;
        testCell = testRow.getCell(9);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l1 current")){
                cellName[9] = testVal;
            }else{
                cellName[9] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(10);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l1 power factor")){
                cellName[10] = testVal;
            }else{
                cellName[10] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(11);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("amr profile status")){
                cellName[11] = testVal;
            }else{
                cellName[11] = "-";
            }       
        }else return null;
        
        //전부 통과하면 true, 하나라도 잘못되면 작업 취소
        return cellName;
    }
    
    // 기존과 기능은 같지만, 파일의 Column이 전체 변경되었음
    // Power Quality 데이터는 들어오지 않고, LP만 고려함
    public String[] readLoadProfile_XLS_cellNameTest2(Object _testRow) {
        String[] cellName = new String[6];
        HSSFRow testRow = (HSSFRow)_testRow;
        HSSFCell testCell = testRow.getCell(0); 
        
        String testVal = "";
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("index")) return null;
            cellName[0] = testVal;
        }else return null;
        testCell = testRow.getCell(1);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("clock")) return null;
            cellName[1] = testVal;
        }else return null;
        testCell = testRow.getCell(2);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("import for lp1")) return null;
            cellName[2] = testVal;
        }else return null;
        testCell = testRow.getCell(3);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("export for lp1")) return null;
            cellName[3] = testVal;
        }else return null;
        testCell = testRow.getCell(4);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("consumption reactive energy")) return null;
            cellName[4] = testVal;
        }else return null;
        testCell = testRow.getCell(5);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("amr frofile status")) return null;
            cellName[5] = testVal;
        }else return null;
        
        //전부 통과하면 true, 하나라도 잘못되면 작업 취소
        return cellName;
    }
    
    // LP채널 변경에 따라 Energy에 붙어있던 Power항목이 별도 파일로 분리되었음.
    public String[] readPowerProfile_XLS_cellNameTest(Object _testRow) {
        String[] cellName = new String[12];     
        HSSFRow testRow = (HSSFRow)_testRow;
        HSSFCell testCell = testRow.getCell(0);     
        
        String testVal = "";
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("index")) return null;
            cellName[0] = testVal;
        }else return null;
        testCell = testRow.getCell(1);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("clock")) return null;
            cellName[1] = testVal;
        }else return null;
        testCell = testRow.getCell(2);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("active power -import")) return null;
            cellName[2] = testVal;
        }else return null;
        testCell = testRow.getCell(3);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("active power -export")) return null;
            cellName[3] = testVal;
        }else return null;
        testCell = testRow.getCell(4);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive power -import")) return null;
            cellName[4] = testVal;
        }else return null;
        testCell = testRow.getCell(5);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive power -export")) return null;
            cellName[5] = testVal;
        }else return null;
        testCell = testRow.getCell(6);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("total import apparent power")) return null;
            cellName[6] = testVal;
        }else return null;
        testCell = testRow.getCell(7);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("total export apparent power")) return null;
            cellName[7] = testVal;
        }else return null;
        // Power Quality
        testCell = testRow.getCell(8);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l1 voltage")){
                cellName[8] = testVal;
            }else{
                cellName[8] = "-";
            }           
        }else return null;
        testCell = testRow.getCell(9);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l1 current")){
                cellName[9] = testVal;
            }else{
                cellName[9] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(10);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l1 power factor")){
                cellName[10] = testVal;
            }else{
                cellName[10] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(11);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("amr profile status")){
                cellName[11] = testVal;
            }else{
                cellName[11] = "-";
            }       
        }else return null;
        
        //전부 통과하면 true, 하나라도 잘못되면 작업 취소
        return cellName;
    }

    
    // LP는 전부다 있어야 하고, Power Quality 부분은 단상,3상에 따라 달라질수 있음을 고려
    public String[] readLoadProfile_XLSX_cellNameTest(Object _testRow){
        String[] cellName = new String[17];     
        XSSFRow testRow = (XSSFRow)_testRow;
        XSSFCell testCell = testRow.getCell(0);     
        
        String testVal = "";
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("index")) return null;
            cellName[0] = testVal;
        }else return null;
        testCell = testRow.getCell(1);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("clock")) return null;
            cellName[1] = testVal;
        }else return null;
        testCell = testRow.getCell(2);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("active power -export")) return null;
            cellName[2] = testVal;
        }else return null;
        testCell = testRow.getCell(3);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("active power -import")) return null;
            cellName[3] = testVal;
        }else return null;
        testCell = testRow.getCell(4);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive power -export")) return null;
            cellName[4] = testVal;
        }else return null;
        testCell = testRow.getCell(5);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive power -import")) return null;
            cellName[5] = testVal;
        }else return null;
        testCell = testRow.getCell(6);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("total export apparent power")) return null;
            cellName[6] = testVal;
        }else return null;
        testCell = testRow.getCell(7);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("total import apparent power")) return null;
            cellName[7] = testVal;
        }else return null;
        // Power Quality
        testCell = testRow.getCell(8);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l1 voltage")){
                cellName[8] = testVal;
            }else{
                cellName[8] = "-";
            }           
        }else return null;
        testCell = testRow.getCell(9);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l2 voltage")){
                cellName[9] = testVal;
            }else{
                cellName[9] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(10);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l3 voltage")){
                cellName[10] = testVal;
            }else{
                cellName[10] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(11);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l1 current")){
                cellName[11] = testVal;
            }else{
                cellName[11] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(12);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l2 current")){
                cellName[12] = testVal;
            }else{
                cellName[12] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(13);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l3 current")){
                cellName[13] = testVal;
            }else{
                cellName[13] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(14);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l1 power factor")){
                cellName[14] = testVal;
            }else{
                cellName[14] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(15);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l2 power factor")){
                cellName[15] = testVal;
            }else{
                cellName[15] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(16);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l3 power factor")){
                cellName[16] = testVal;
            }else{
                cellName[16] = "-";
            }       
        }else return null;
        
        //전부 통과하면 true, 하나라도 잘못되면 작업 취소
        return cellName;
    }

    // LP는 전부다 있어야 하고, Power Quality 부분은 단상,3상에 따라 달라질수 있음을 고려
    public String[] readLoadProfile_XLS_cellNameTest(Object _testRow) {
        String[] cellName = new String[17];     
        HSSFRow testRow = (HSSFRow)_testRow;
        HSSFCell testCell = testRow.getCell(0);     
        
        String testVal = "";
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("index")) return null;
            cellName[0] = testVal;
        }else return null;
        testCell = testRow.getCell(1);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("clock")) return null;
            cellName[1] = testVal;
        }else return null;
        testCell = testRow.getCell(2);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("active power -export")) return null;
            cellName[2] = testVal;
        }else return null;
        testCell = testRow.getCell(3);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("active power -import")) return null;
            cellName[3] = testVal;
        }else return null;
        testCell = testRow.getCell(4);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive power -export")) return null;
            cellName[4] = testVal;
        }else return null;
        testCell = testRow.getCell(5);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive power -import")) return null;
            cellName[5] = testVal;
        }else return null;
        testCell = testRow.getCell(6);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("total export apparent power")) return null;
            cellName[6] = testVal;
        }else return null;
        testCell = testRow.getCell(7);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("total import apparent power")) return null;
            cellName[7] = testVal;
        }else return null;
        // Power Quality
        testCell = testRow.getCell(8);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l1 voltage")){
                cellName[8] = testVal;
            }else{
                cellName[8] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(9);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l2 voltage")){
                cellName[9] = testVal;
            }else{
                cellName[9] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(10);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l3 voltage")){
                cellName[10] = testVal;
            }else{
                cellName[10] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(11);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l1 current")){
                cellName[11] = testVal;
            }else{
                cellName[11] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(12);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l2 current")){
                cellName[12] = testVal;
            }else{
                cellName[12] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(13);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l3 current")){
                cellName[13] = testVal;
            }else{
                cellName[13] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(14);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l1 power factor")){
                cellName[14] = testVal;
            }else{
                cellName[14] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(15);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l2 power factor")){
                cellName[15] = testVal;
            }else{
                cellName[15] = "-";
            }       
        }else return null;
        testCell = testRow.getCell(16);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("l3 power factor")){
                cellName[16] = testVal;
            }else{
                cellName[16] = "-";
            }       
        }else return null;
        
        //전부 통과하면 true, 하나라도 잘못되면 작업 취소
        return cellName;
    }

    //채널 변경: Daily 20(0~19), Monthly 22(0~21)
    public String[] readDailyMonth_XLSX_cellNameTest(Object _testRow) {
        String[] cellName = new String[22];     
        XSSFRow testRow = (XSSFRow)_testRow;
        int testRowIdx = testRow.getPhysicalNumberOfCells();
        String testVal = "";
                        
        XSSFCell testCell = testRow.getCell(0);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("index")) return null; 
            cellName[0] = testVal;
        }else return null;
        testCell = testRow.getCell(1);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("clock")) return null;
            cellName[1] = testVal;
        }else return null;
        testCell = testRow.getCell(2);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import")) return null;
            cellName[2] = testVal;
        }else return null;
        testCell = testRow.getCell(3);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 1")) return null;
            cellName[3] = testVal;
        }else return null;
        testCell = testRow.getCell(4);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 2")) return null;
            cellName[4] = testVal;
        }else return null;
        testCell = testRow.getCell(5);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 3")) return null;
            cellName[5] = testVal;
        }else return null;
        testCell = testRow.getCell(6);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 4")) return null;
            cellName[6] = testVal;
        }else return null;      
        testCell = testRow.getCell(7);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 5")) return null;
            cellName[7] = testVal;
        }else return null;
        testCell = testRow.getCell(8);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 6")) return null;
            cellName[8] = testVal;
        }else return null;
        testCell = testRow.getCell(9);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 7")) return null;
            cellName[9] = testVal;
        }else return null;
        testCell = testRow.getCell(10);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 8")) return null;
            cellName[10] = testVal;
        }else return null;
        testCell = testRow.getCell(11);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import")) return null;
            cellName[11] = testVal;
        }else return null;
        testCell = testRow.getCell(12);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 1")) return null;
            cellName[12] = testVal;
        }else return null;
        testCell = testRow.getCell(13);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 2")) return null;
            cellName[13] = testVal;
        }else return null;
        testCell = testRow.getCell(14);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 3")) return null;
            cellName[14] = testVal;
        }else return null;
        testCell = testRow.getCell(15);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 4")) return null;
            cellName[15] = testVal;
        }else return null;
        testCell = testRow.getCell(16);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 5")) return null;
            cellName[16] = testVal;
        }else return null;
        testCell = testRow.getCell(17);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 6")) return null;
            cellName[17] = testVal;
        }else return null;
        testCell = testRow.getCell(18);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 7")) return null;
            cellName[18] = testVal;
        }else return null;
        testCell = testRow.getCell(19);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 8")) return null;
            cellName[19] = testVal;
        }else return null;
        
        // DataType : Month Only (20~21)
        if(testRowIdx > 20){
            testCell = testRow.getCell(20);
            if(testCell != null){
                testVal = testCell.getStringCellValue().toLowerCase();
                if(!testVal.contains("total maximum demand")) return null;
                cellName[20] = testVal;
            }else return null;
            testCell = testRow.getCell(21);
            if(testCell != null){
                testVal = testCell.getStringCellValue().toLowerCase();
                if(!testVal.contains("capture time")) return null;
                cellName[21] = testVal;
            }else return null;
        }else ;
        
        return cellName;
    }

    //채널 변경: Daily 20(0~19), Monthly 22(0~21)
    public String[] readDailyMonth_XLS_cellNameTest(Object _testRow) {
        String[] cellName = new String[22];     
        HSSFRow testRow = (HSSFRow)_testRow;
        int testRowIdx = testRow.getPhysicalNumberOfCells();
        String testVal = "";
                        
        HSSFCell testCell = testRow.getCell(0);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("index")) return null; 
            cellName[0] = testVal;
        }else return null;
        testCell = testRow.getCell(1);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("clock")) return null;
            cellName[1] = testVal;
        }else return null;
        testCell = testRow.getCell(2);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import")) return null;
            cellName[2] = testVal;
        }else return null;
        testCell = testRow.getCell(3);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 1")) return null;
            cellName[3] = testVal;
        }else return null;
        testCell = testRow.getCell(4);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 2")) return null;
            cellName[4] = testVal;
        }else return null;
        testCell = testRow.getCell(5);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 3")) return null;
            cellName[5] = testVal;
        }else return null;
        testCell = testRow.getCell(6);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 4")) return null;
            cellName[6] = testVal;
        }else return null;      
        testCell = testRow.getCell(7);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 5")) return null;
            cellName[7] = testVal;
        }else return null;
        testCell = testRow.getCell(8);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 6")) return null;
            cellName[8] = testVal;
        }else return null;
        testCell = testRow.getCell(9);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 7")) return null;
            cellName[9] = testVal;
        }else return null;
        testCell = testRow.getCell(10);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("cumulative active energy -import rate 8")) return null;
            cellName[10] = testVal;
        }else return null;
        testCell = testRow.getCell(11);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import")) return null;
            cellName[11] = testVal;
        }else return null;
        testCell = testRow.getCell(12);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 1")) return null;
            cellName[12] = testVal;
        }else return null;
        testCell = testRow.getCell(13);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 2")) return null;
            cellName[13] = testVal;
        }else return null;
        testCell = testRow.getCell(14);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 3")) return null;
            cellName[14] = testVal;
        }else return null;
        testCell = testRow.getCell(15);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 4")) return null;
            cellName[15] = testVal;
        }else return null;
        testCell = testRow.getCell(16);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 5")) return null;
            cellName[16] = testVal;
        }else return null;
        testCell = testRow.getCell(17);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 6")) return null;
            cellName[17] = testVal;
        }else return null;
        testCell = testRow.getCell(18);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 7")) return null;
            cellName[18] = testVal;
        }else return null;
        testCell = testRow.getCell(19);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(!testVal.contains("reactive energy -import rate 8")) return null;
            cellName[19] = testVal;
        }else return null;
        
        // DataType : Month Only (20~21)
        if(testRowIdx > 20){
            testCell = testRow.getCell(20);
            if(testCell != null){
                testVal = testCell.getStringCellValue().toLowerCase();
                if(!testVal.contains("total maximum demand")) return null;
                cellName[20] = testVal;
            }else return null;
            testCell = testRow.getCell(21);
            if(testCell != null){
                testVal = testCell.getStringCellValue().toLowerCase();
                if(!testVal.contains("capture time")) return null;
                cellName[21] = testVal;
            }else return null;
        }else ;
        
        return cellName;
    }
    
    // XLSX 파일, 3번째 컬럼에 포함된 단위를 읽고 배율 반환
    public double check_XLSX_Unit(Object _testRow){
        String[] cellNameUnit = new String[3];      
        XSSFRow testRow = (XSSFRow)_testRow;
        
        String testVal = "";
                        
        XSSFCell testCell = testRow.getCell(2);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("(wh)")) return 0.001; 
            cellNameUnit[0] = testVal;
        }else return 1.0;
        return 1.0;
    }
    
    // XLS 파일, 3번째 컬럼에 포함된 단위를 읽고 배율 반환
    public double check_XLS_Unit(Object _testRow){
        String[] cellNameUnit = new String[3];      
        HSSFRow testRow = (HSSFRow)_testRow;
        
        String testVal = "";
                        
        HSSFCell testCell = testRow.getCell(2);
        if(testCell != null){
            testVal = testCell.getStringCellValue().toLowerCase();
            if(testVal.contains("(wh)")) return 0.001; 
            cellNameUnit[0] = testVal;
        }else return 1.0;
        return 1.0;
    }

    //기존 함수와 기능은 같지만, 저장하는 항목이 달라짐 (PQ제거, LP변경)
    //DB에 저장하는 함수   
    public Map<String,Object> saveLPFromList2(List<Map<String, Object>> _lineList, String _mdsId, String _uHistId, String _modelId) {
        Map<String,Object> result = new HashMap<String,Object>();
        
        if(_lineList.isEmpty()) return null;
        
        // 미터 조회
        Meter meter = meterDao.get(_mdsId);             
        
        // 미터가 등록되지 않았으면 업데이트 중지, 이력 생성
        if(meter == null){
            // Null반환시 Controller에서 이력 업데이트
            return null;
        }else {
            // 미터 모델 아이디 비교
            DeviceModel model = meter.getModel();
            String modelId = model.getId().toString();
            if(!modelId.equals(_modelId)){
                // 조회한 미터의 모델 아이디와 입력된 모델 아이디가 다르면 Null 반환
                return null;
            }
        }
    
        // LP SAVE 데이터
        int totalCnt = _lineList.size();        
        double[][] lpValues = null;
        int[] flaglist = null;
        Modem modem = meter.getModem();     
        // 중복 검사 
        LinkedHashSet<Condition> condition = null;
        String str_mm = "";
        // PQ SAVE 데이터
        Instrument ins = new Instrument();
        Instrument[] inss = new Instrument[1];
        // 반환 정보
        int failCnt = 0;
        String startDate = null;
        String endDate = null;
        
        for(int r=0; r<totalCnt; r++){
            TransactionStatus txstatus = null;
            Map<String,Object> row = _lineList.get(r);
            try {
                //txstatus = txmanager.getTransaction(null);
                txstatus = txmanager.getTransaction(
                        new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
                            
                String yyyymmddhh = row.get("1").toString().substring(0,10); //converting
                String yyyymmdd = row.get("1").toString().substring(0,8); //converting
                String hhmm = row.get("1").toString().substring(8,12); //converting
                if(r==0){
                    startDate = row.get("1").toString();
                    endDate = row.get("1").toString();                  
                }else if(r==totalCnt-1){
                    startDate = row.get("1").toString();
                }
                
                int lpc = 3;
                lpValues = new double[lpc][1];
                flaglist = new int[lpc];
                for(int i=0; i<lpc; i++){
                    // row의 0,1셀에는 인덱스와 시간값이 있음(2부터4셀까지LP)
                    lpValues[i][0] = Double.parseDouble(row.get(2+i+"").toString());
                    // 각각 채널 1,2,3,...,i+1
                    flaglist[i] = i+1;
                }
                
                // LP 중복 검사
                Boolean isDuplicate = false;
                condition = new LinkedHashSet<Condition>();
                condition.add(new Condition("id.yyyymmddhh",new Object[] { yyyymmddhh }, null, Restriction.EQ));
                condition.add(new Condition("id.channel",
                        new Object[] { ElectricityChannel.Usage.getChannel() },null, Restriction.EQ));
                condition.add(new Condition("id.dst", new Object[] {0}, null, Restriction.EQ)); // TODO DST
                condition.add(new Condition("id.mdevId", new Object[] { meter.getMdsId() }, null, Restriction.EQ));
                
                
                // 지금 작업중인 LP의 정보와 일치하는 LP가 있는지 DB를 조회
                List<LpEM> lpEMs = lpEMDao.findByConditions(condition);
                if(lpEMs != null && lpEMs.size() > 0){
                    str_mm = "value_"+hhmm.substring(2,4);
                    
                    for(LpEM _lp : lpEMs){
                        if(_lp.getYyyymmddhh().equals(yyyymmddhh)){
                            String _lpVal = null;
                            _lpVal = BeanUtils.getProperty(_lp, str_mm);
                            if(_lpVal != null){
                                //break;
                                if (txstatus != null) txmanager.rollback(txstatus);
                                // 실패 카운트 증가, 에러 메시지 작성하여 failList 업데이트
                                failCnt++;              
                                Map<String,Object> fCondition = new HashMap<String,Object>();
                                fCondition.put("uploadId", _uHistId);
                                fCondition.put("rowLine", row.get("0").toString());
                                fCondition.put("failReason", "Duplicated LP");
                                fCondition.put("dataType", "1");
                                fCondition.put("meteringTime", row.get("1").toString());
                                String mdValue = "|";
                                for(int w=2; w<6; w++){
                                    //Index(0), Clock(1)을 제외한 MD(2~5)를 묶음 
                                    mdValue = mdValue.concat(row.get(""+w).toString()).concat("|");                 
                                }
                                fCondition.put("mdValue", mdValue);
                                addUploadFailHistory(fCondition);
                                isDuplicate = true;
                                break;
                            }
                        }
                    }
                }
                // 중복 LP가 없으면 계속 진행.
                if(isDuplicate) continue;
                
                // save LP (Transaction)
                //saveLPData(MeteringType.Manual, yyyymmdd, hhmm, lpValues, flaglist,
                //        0.0, meter, DeviceType.Modem,
                //        modem.getId().toString(), DeviceType.Meter, meter.getMdsId());
                
                // MDMS연동을 위한 XML 구조체,파일 생성        
                // TODO
                // hhuXMLsaver.makeLpIntegrationData(lpValues, row.get("1").toString(), meter);
                
                // 에러 없이 마무리 되었다면 commit
                txmanager.commit(txstatus);
            } catch (Exception e){
                logger.error(e,e);
                if (txstatus != null) txmanager.rollback(txstatus);
                // 실패 카운트 증가, 에러 메시지 작성하여 failList 업데이트
                failCnt++;              
                Map<String,Object> fCondition = new HashMap<String,Object>();
                fCondition.put("uploadId", _uHistId);
                fCondition.put("rowLine", row.get("0").toString());
                fCondition.put("failReason", e.toString());
                fCondition.put("dataType", "1");
                fCondition.put("meteringTime", row.get("1").toString());
                String mdValue = "|";
                for(int w=2; w<6; w++){
                    //Index(0), Clock(1)을 제외한 MD(2~5)를 묶음 
                    mdValue = mdValue.concat(row.get(""+w).toString()).concat("|");                 
                }
                fCondition.put("mdValue", mdValue);
                addUploadFailHistory(fCondition);
            }
        } //for         
        result.put("resultMsg", "success");
        result.put("failCnt", failCnt);
        result.put("totalCnt", totalCnt);
        result.put("successCnt", totalCnt-failCnt);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        
        return result;
    }
    
    //기존 LP저장함수에 포함되던 PQ항목이 분리되었음
    //DB에 저장하는 함수
    @Override
    public Map<String,Object> savePQFromList(List<Map<String, Object>> _lineList, String _mdsId, String _uHistId, String _modelId) {
        Map<String,Object> result = new HashMap<String,Object>();
        
        if(_lineList.isEmpty()) return null;
        
        // 미터 조회
        Meter meter = meterDao.get(_mdsId);             
        
        // 미터가 등록되지 않았으면 업데이트 중지, 이력 생성
        if(meter == null){
            // Null반환시 Controller에서 이력 업데이트
            return null;
        }else {
            // 미터 모델 아이디 비교
            DeviceModel model = meter.getModel();
            String modelId = model.getId().toString();
            if(!modelId.equals(_modelId)){
                // 조회한 미터의 모델 아이디와 입력된 모델 아이디가 다르면 Null 반환
                return null;
            }
        }
    
        // SAVE 데이터
        int totalCnt = _lineList.size();        
        Modem modem = meter.getModem();     
        // PQ SAVE 데이터
        Instrument ins = new Instrument();
        Instrument[] inss = new Instrument[1];
        // 반환 정보
        int failCnt = 0;
        String startDate = null;
        String endDate = null;
        
        for(int r=0; r<totalCnt; r++){
            TransactionStatus txstatus = null;
            Map<String,Object> row = _lineList.get(r);
            try {
                //txstatus = txmanager.getTransaction(null);
                txstatus = txmanager.getTransaction(
                        new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
                            
                String yyyymmdd = row.get("1").toString().substring(0,8); //converting
                String hhmm = row.get("1").toString().substring(8,12); //converting
                if(r==0){
                    startDate = row.get("1").toString();
                    endDate = row.get("1").toString();                  
                }else if(r==totalCnt-1){
                    startDate = row.get("1").toString();
                }               

                //save to PQ (단상 기준)
                ins = new Instrument();
                ins.setVOL_A(Double.parseDouble(row.get("8").toString()));
                //ins.setVOL_B(Double.parseDouble(row.get("9").toString()));
                //ins.setVOL_C(Double.parseDouble(row.get("10").toString()));
                ins.setCURR_A(Double.parseDouble(row.get("9").toString()));
                //ins.setCURR_B(Double.parseDouble(row.get("12").toString()));
                //ins.setCURR_C(Double.parseDouble(row.get("13").toString()));
                ins.setPF_A(Double.parseDouble(row.get("10").toString()));
                //ins.setPF_B(Double.parseDouble(row.get("15").toString()));
                //ins.setPF_C(Double.parseDouble(row.get("16").toString()));
                inss[0] = ins;
                
                // save PQ (Transaction)
                //savePowerQuality(meter, row.get("1").toString(), inss, DeviceType.Modem, modem.getId().toString(), 
                //        DeviceType.Meter, meter.getMdsId());
                
                // MDMS 연동을 위한 XML 구조체, 파일 생성
                
                
                
                // 에러 없이 마무리 되었다면 commit
                txmanager.commit(txstatus);
            } catch (Exception e){
                logger.error(e,e);
                if (txstatus != null) txmanager.rollback(txstatus);
                // 실패 카운트 증가, 에러 메시지 작성하여 failList 업데이트
                failCnt++;              
                Map<String,Object> fCondition = new HashMap<String,Object>();
                fCondition.put("uploadId", _uHistId);
                fCondition.put("rowLine", row.get("0").toString());
                fCondition.put("failReason", e.toString());
                fCondition.put("dataType", "4");
                fCondition.put("meteringTime", row.get("1").toString());
                String mdValue = "|";
                for(int w=2; w<11; w++){
                    mdValue = mdValue.concat(row.get(""+w).toString()).concat("|");                 
                }
                fCondition.put("mdValue", mdValue);
                addUploadFailHistory(fCondition);
            }
        } //for         
        result.put("resultMsg", "success");
        result.put("failCnt", failCnt);
        result.put("totalCnt", totalCnt);
        result.put("successCnt", totalCnt-failCnt);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        
        return result;
    }
    
    //DB에 저장하는 함수
    @Override
    public Map<String,Object> saveLPFromList(List<Map<String, Object>> _lineList, String _mdsId, String _uHistId, String _modelId) {
        Map<String,Object> result = new HashMap<String,Object>();
        
        if(_lineList.isEmpty()) return null;
        
        // 미터 조회
        Meter meter = meterDao.get(_mdsId);             
        
        // 미터가 등록되지 않았으면 업데이트 중지, 이력 생성
        if(meter == null){
            // Null반환시 Controller에서 이력 업데이트
            return null;
        }else {
            // 미터 모델 아이디 비교
            DeviceModel model = meter.getModel();
            String modelId = model.getId().toString();
            if(!modelId.equals(_modelId)){
                // 조회한 미터의 모델 아이디와 입력된 모델 아이디가 다르면 Null 반환
                return null;
            }
        }
    
        // LP SAVE 데이터
        int totalCnt = _lineList.size();        
        double[][] lpValues = null;
        int[] flaglist = null;
        Modem modem = meter.getModem();     
        // PQ SAVE 데이터
        Instrument ins = new Instrument();
        Instrument[] inss = new Instrument[1];
        // 반환 정보
        int failCnt = 0;
        String startDate = null;
        String endDate = null;
        
        for(int r=0; r<totalCnt; r++){
            TransactionStatus txstatus = null;
            Map<String,Object> row = _lineList.get(r);
            try {
                //txstatus = txmanager.getTransaction(null);
                txstatus = txmanager.getTransaction(
                        new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
                            
                String yyyymmdd = row.get("1").toString().substring(0,8); //converting
                String hhmm = row.get("1").toString().substring(8,12); //converting
                if(r==0){
                    startDate = row.get("1").toString();
                    endDate = row.get("1").toString();                  
                }else if(r==totalCnt-1){
                    startDate = row.get("1").toString();
                }
                
                int lpc = 6;
                lpValues = new double[lpc][1];
                flaglist = new int[lpc];
                for(int i=0; i<lpc; i++){
                    // row의 0,1셀에는 인덱스와 시간값이 있음(2부터7셀까지LP)
                    lpValues[i][0] = Double.parseDouble(row.get(2+i+"").toString());
                    flaglist[i] = i+1;
                }
                
                // save LP (Transaction)
                //saveLPData(MeteringType.Manual, yyyymmdd, hhmm, lpValues, flaglist,
                //        0.0, meter, DeviceType.Modem,
                //        modem.getId().toString(), DeviceType.Meter, meter.getMdsId());
                
                //save to PQ
                ins = new Instrument();
                ins.setVOL_A(Double.parseDouble(row.get("8").toString()));
                ins.setVOL_B(Double.parseDouble(row.get("9").toString()));
                ins.setVOL_C(Double.parseDouble(row.get("10").toString()));
                ins.setCURR_A(Double.parseDouble(row.get("11").toString()));
                ins.setCURR_B(Double.parseDouble(row.get("12").toString()));
                ins.setCURR_C(Double.parseDouble(row.get("13").toString()));
                ins.setPF_A(Double.parseDouble(row.get("14").toString()));
                ins.setPF_B(Double.parseDouble(row.get("15").toString()));
                ins.setPF_C(Double.parseDouble(row.get("16").toString()));
                inss[0] = ins;
                
                // save PQ (Transaction)
                //savePowerQuality(meter, row.get("1").toString(), inss, DeviceType.Modem, modem.getId().toString(), 
                //        DeviceType.Meter, meter.getMdsId());
                
                // 에러 없이 마무리 되었다면 commit
                txmanager.commit(txstatus);
            } catch (Exception e){
                logger.error(e,e);
                if (txstatus != null) txmanager.rollback(txstatus);
                // 실패 카운트 증가, 에러 메시지 작성하여 failList 업데이트
                failCnt++;              
                Map<String,Object> fCondition = new HashMap<String,Object>();
                fCondition.put("uploadId", _uHistId);
                fCondition.put("rowLine", row.get("0").toString());
                fCondition.put("failReason", e.toString());
                fCondition.put("dataType", "1");
                fCondition.put("meteringTime", row.get("1").toString());
                String mdValue = "|";
                for(int w=2; w<17; w++){
                    mdValue = mdValue.concat(row.get(""+w).toString()).concat("|");                 
                }
                fCondition.put("mdValue", mdValue);
                addUploadFailHistory(fCondition);
            }
        } //for         
        result.put("resultMsg", "success");
        result.put("failCnt", failCnt);
        result.put("totalCnt", totalCnt);
        result.put("successCnt", totalCnt-failCnt);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        
        return result;
    }

    //DB에 저장
    @Override
    public Map<String,Object> saveDPFromList(List<Map<String, Object>> _lineList, String _mdsId, String _uHistId, String _modelId) {
        Map<String,Object> result = new HashMap<String,Object>(); 
        if(_lineList.isEmpty()) return null;
        //미터 조회
        Meter meter = meterDao.get(_mdsId);
        
        // 미터가 등록되지 않았으면 업데이트 중지, 이력 생성
        if(meter == null){
            // Null반환시 Controller에서 이력 업데이트
            return null;
        }else {
            // 미터 모델 아이디 비교
            DeviceModel model = meter.getModel();
            String modelId = model.getId().toString();
            if(!modelId.equals(_modelId)){
                // 조회한 미터의 모델 아이디와 입력된 모델 아이디가 다르면 Null 반환
                return null;
            }
        }
        
        // Daily Billing 데이터
        int totalCnt = _lineList.size();
        Modem modem = meter.getModem();
        // 반환 정보
        int failCnt = 0;
        String startDate = null;
        String endDate = null;
        
        for(int r=0; r<totalCnt; r++){
            TransactionStatus txstatus = null;
            Map<String,Object> row = _lineList.get(r);
            try {
                txstatus = txmanager.getTransaction(
                        new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
                
                String yyyymmdd = row.get("1").toString().substring(0,8); //converting
                String hhmm = row.get("1").toString().substring(8,12); //converting
                if(r==0){
                    startDate = row.get("1").toString();
                    endDate = row.get("1").toString();                  
                }else if(r==totalCnt-1){
                    startDate = row.get("1").toString();
                }
                
                // save to billingdata
                // 2015-12-18기준 LS미터에서 업로드한 채널은 유효,무효 각각 8채널씩이지만,
                // 시스템에서는 Rate4까지만 세팅되므로 가능한것만 저장하고 나머지는 버림.
                BillingData day = new BillingData();
                day.setBillingTimestamp(row.get("1").toString());               
                day.setActiveEnergyImportRateTotal(Double.parseDouble(row.get("2").toString()));
                day.setActiveEnergyImportRate1(Double.parseDouble(row.get("3").toString()));
                day.setActiveEnergyImportRate2(Double.parseDouble(row.get("4").toString()));
                day.setActiveEnergyImportRate3(Double.parseDouble(row.get("5").toString()));
                day.setActiveEnergyImportRate4(Double.parseDouble(row.get("6").toString()));            
                // row-7,8,9,10 버림
                day.setReactiveEnergyRateTotal(Double.parseDouble(row.get("11").toString()));
                day.setReactiveEnergyRate1(Double.parseDouble(row.get("12").toString()));
                day.setReactiveEnergyRate2(Double.parseDouble(row.get("13").toString()));
                day.setReactiveEnergyRate3(Double.parseDouble(row.get("14").toString()));
                day.setReactiveEnergyRate4(Double.parseDouble(row.get("15").toString()));
                // row-16,17,18,19 버림
                
                // save Daily Billing
                //saveDailyBilling(day, meter, DeviceType.Modem, modem.getId().toString(), 
                //        DeviceType.Meter, meter.getMdsId());
                
                // 에러 없이 마무리 되었다면 commit
                txmanager.commit(txstatus);
            } catch (Exception e){
                logger.error(e,e);
                if (txstatus != null) txmanager.rollback(txstatus);
                // 실패 카운트 증가, 에러 메시지 작성하여 failList 업데이트
                failCnt++;      
                Map<String,Object> fCondition = new HashMap<String,Object>();
                fCondition.put("uploadId", _uHistId);
                fCondition.put("rowLine", row.get("0").toString());
                fCondition.put("failReason", e.toString());
                fCondition.put("dataType", "1");
                fCondition.put("meteringTime", row.get("1").toString());
                String mdValue = "|";
                for(int w=2; w<20; w++){
                    //Index(0), Clock(1)을 제외한 MD(2~19)를 묶음 
                    mdValue = mdValue.concat(row.get(""+w).toString()).concat("|");                 
                }
                fCondition.put("mdValue", mdValue);
                addUploadFailHistory(fCondition);
            }
        } //for 
        
        result.put("resultMsg", "success");
        result.put("failCnt", failCnt);
        result.put("totalCnt", totalCnt);
        result.put("successCnt", totalCnt-failCnt);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        return result;
    }

    @Override
    public Map<String,Object> saveMPFromList(List<Map<String, Object>> _lineList, String _mdsId, String _uHistId, String _modelId) {
        Map<String,Object> result = new HashMap<String,Object>(); 
        if(_lineList.isEmpty()) return null;
        //미터 조회
        Meter meter = meterDao.get(_mdsId);
        
        // 미터가 등록되지 않았으면 업데이트 중지, 이력 생성
        if(meter == null){
            // Null반환시 Controller에서 이력 업데이트
            return null;
        }else {
            // 미터 모델 아이디 비교
            DeviceModel model = meter.getModel();
            String modelId = model.getId().toString();
            if(!modelId.equals(_modelId)){
                // 조회한 미터의 모델 아이디와 입력된 모델 아이디가 다르면 Null 반환
                return null;
            }
        } 
        
        // Monthly Billing 데이터
        int totalCnt = _lineList.size();
        Modem modem = meter.getModem();
        // 반환 정보
        int failCnt = 0;
        String startDate = null;
        String endDate = null;
        
        for(int r=0; r<totalCnt; r++){
            TransactionStatus txstatus = null;
            Map<String,Object> row = _lineList.get(r);
            try {
                txstatus = txmanager.getTransaction(
                        new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
                //Clock
                String yyyymmdd = row.get("1").toString().substring(0,8); //converting
                String hhmm = row.get("1").toString().substring(8,12); //converting
                if(r==0){
                    startDate = row.get("1").toString();
                    endDate = row.get("1").toString();                  
                }else if(r==totalCnt-1){
                    startDate = row.get("1").toString();
                }
                
                // save to billingdata
                BillingData month = new BillingData();
                month.setBillingTimestamp(row.get("1").toString());
                month.setActiveEnergyImportRateTotal(Double.parseDouble(row.get("2").toString()));
                month.setActiveEnergyImportRate1(Double.parseDouble(row.get("3").toString()));
                month.setActiveEnergyImportRate2(Double.parseDouble(row.get("4").toString()));
                month.setActiveEnergyImportRate3(Double.parseDouble(row.get("5").toString()));
                month.setActiveEnergyImportRate4(Double.parseDouble(row.get("6").toString()));          
                // row-7,8,9,10 버림
                month.setReactiveEnergyRateTotal(Double.parseDouble(row.get("11").toString()));
                month.setReactiveEnergyRate1(Double.parseDouble(row.get("12").toString()));
                month.setReactiveEnergyRate2(Double.parseDouble(row.get("13").toString()));
                month.setReactiveEnergyRate3(Double.parseDouble(row.get("14").toString()));
                month.setReactiveEnergyRate4(Double.parseDouble(row.get("15").toString()));
                // row-16,17,18,19 버림               
                month.setActivePowerMaxDemandRateTotal(Double.parseDouble(row.get("20").toString()));
                month.setActivePowerDemandMaxTimeRateTotal(row.get("21").toString().substring(0,12));
                
                // save Monthly Billing
                //saveMonthlyBilling(month, meter, DeviceType.Modem, modem.getId().toString(), 
                //        DeviceType.Meter, meter.getMdsId());
                
                // 에러 없이 마무리 되었다면 commit
                txmanager.commit(txstatus);
            } catch (Exception e){
                logger.error(e,e);
                if (txstatus != null) txmanager.rollback(txstatus);
                // 실패 카운트 증가, 에러 메시지 작성하여 failList 업데이트
                failCnt++;      
                Map<String,Object> fCondition = new HashMap<String,Object>();
                fCondition.put("uploadId", _uHistId);
                fCondition.put("rowLine", row.get("0").toString());
                fCondition.put("failReason", e.toString());
                fCondition.put("dataType", "1");
                fCondition.put("meteringTime", row.get("1").toString());
                String mdValue = "|";
                for(int w=2; w<22; w++){
                    //Index(0), Clock(1)을 제외한 MD(2~21)를 묶음 
                    mdValue = mdValue.concat(row.get(""+w).toString()).concat("|");                 
                }
                fCondition.put("mdValue", mdValue);
                addUploadFailHistory(fCondition);
            }
        } //for 
        
        result.put("resultMsg", "success");
        result.put("failCnt", failCnt);
        result.put("totalCnt", totalCnt);
        result.put("successCnt", totalCnt-failCnt);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        return result;
    }


    public String getDatePatternFromLocale(String supplierId){
        // 공급사를 통해 locale,country code찾아서 date 패턴 탐색
        // 언어 설정 등으로 인해 기존 함수로 조회한 날짜 표기방식이 해당 국가의 날짜 표기방식과 다를 경우 사용함.
        Supplier supplier = supplierDao.getSupplierById(Integer.parseInt(supplierId));
        Locale[] locs = Locale.getAvailableLocales();
        String datePattern = null;
        for(Locale lc : locs){
            if(lc.getCountry().equals(supplier.getCountry().getCode_2letter())){
                String langCode = lc.getLanguage();
                DateFormat dateInstance = DateFormat.getDateInstance(DateFormat.MEDIUM,lc);
                String formats = dateInstance.format(new Date());               
                String pattern = ((SimpleDateFormat) dateInstance).toPattern();
                // 연.월.일을 찾고 시간표기를 덧붙임
                datePattern = pattern.concat(" hh:mm:ss");
                break;
            }
        }
        
        return datePattern;
    }
    
    // 주어진 시간포맷과 일치하도록 입력 시간을 변환
    public String timeLocFormat(String inTime, String _datePattern){
        if(inTime.equals("-")){
            return "-";
        }
        String outTime = null;      
        SimpleDateFormat sdf = new SimpleDateFormat(_datePattern);
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
    
    // 엑셀의 두번째 라인에 포함된 미터 시리얼을 추출하여 반환
    public String checkMeterNo_xlsx(Object _testRow){
        XSSFRow nameRow = (XSSFRow)_testRow;
        XSSFCell nameCell = nameRow.getCell(0);
        
        String cellValue = nameCell.getStringCellValue();
        cellValue = cellValue.toLowerCase().trim();
        int startNo = cellValue.indexOf("no:");
        int endNo = cellValue.indexOf("read", startNo);
        
        // 패턴 검색을 통해 숫자만 남기고, 단위 표기를 제거
        String result = cellValue.substring(startNo,endNo).trim();
        Matcher matcher = Pattern.compile("\\d+").matcher(result);
        if(matcher.find())
            result = result.substring(matcher.start(), matcher.end());
        else
            result = "";
        
        return result;
    }
    // 엑셀의 두번째 라인에 포함된 미터 시리얼을 추출하여 반환
    public String checkMeterNo_xls(Object _testRow){
        HSSFRow nameRow = (HSSFRow)_testRow;
        HSSFCell nameCell = nameRow.getCell(0);
        
        String cellValue = nameCell.getStringCellValue();
        cellValue = cellValue.toLowerCase().trim();
        int startNo = cellValue.indexOf("no:");
        int endNo = cellValue.indexOf("read", startNo);
        
        // 패턴 검색을 통해 숫자만 남기고, 단위 표기를 제거
        String result = cellValue.substring(startNo,endNo).trim();
        Matcher matcher = Pattern.compile("\\d+").matcher(result);
        if(matcher.find())
            result = result.substring(matcher.start(), matcher.end());
        else
            result = "";
        
        return result;
    }
    
    // 2016-02-22 : 사용하지 않음 (모델명이 들어오지 않음)
    // 엑셀의 미터 타입을 읽어서 단상, 3상 구분 
    public String checkPhase(Object _testCell) {
        XSSFCell nameCell = (XSSFCell)_testCell;
        String phase = nameCell.getStringCellValue();
        if(phase.contains("LS12Meter")){
            return "1";
        }else if(phase.contains("LS34Meter")){
            return "3";
        }
        // 기본적으로 단상으로 가정함
        return "1";
    }
}
