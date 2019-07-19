package com.aimir.schedule.task;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.search.IntegerComparisonTerm;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.dao.device.FirmwareDao;
import com.aimir.dao.device.FirmwareIssueDao;
import com.aimir.dao.device.FirmwareIssueHistoryDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.schedule.excel.ExcelUtil;

/**
 * @author sunghan
 *
 */
@Service
public class Pilot2MeteringRateTask extends ScheduleTask {
	private static Logger logger = LoggerFactory.getLogger(Pilot2MeteringRateTask.class);

	@Resource(name = "transactionManager")
	HibernateTransactionManager txmanager;

	@Autowired
	FirmwareDao firmwareDao;

	@Autowired
	LocationDao locationDao;

	@Autowired
	FirmwareIssueHistoryDao firmwareIssueHistoryDao;

	@Autowired
	FirmwareIssueDao firmwareIssueDao;

	@Autowired
	OperatorDao operatorDao;

	@Autowired
	MCUDao mcuDao;

	@Autowired
	MeterDao meterDao;

	@Autowired
	ModemDao modemDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	MMIUDao mmiuDao;

	@Autowired
	OperationLogDao operationLogDao;

	@Autowired
	CmdOperationUtil cmdOperationUtil;

	@Autowired
	AsyncCommandLogDao asyncCommandLogDao;

	@Autowired
	AsyncCommandResultDao resultDao;

	@Override
	public void execute(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}
	
	public List<Map<String, Object>> execute(String searchTime, String tableName) {
		TransactionStatus txstatus = null;
		txstatus = txmanager.getTransaction(null);
		List<Map<String, Object>> result = null;
		
		try {
			result = meterDao.getPilot2MeteringRate(searchTime, tableName);
		} catch (Exception e) {
			if (txstatus != null) {
				txmanager.rollback(txstatus);
			}

			logger.error("Task Excute transaction error - " + e, e);
		}
		
		if (txstatus != null) {
			txmanager.commit(txstatus);
		}
		
		return result;
		// return meterDao.getMeteringRate(searchTime, tableName);
	}

	public List<Map<String, Object>> execute2(String searchTime, String tableName, String tempTableName) {
		TransactionStatus txstatus = null;
		txstatus = txmanager.getTransaction(null);
		List<Map<String, Object>> result = null;
		
		try {
			result = meterDao.getPilot2MeteringRate_detail(searchTime, tableName, tempTableName);
		} catch (Exception e) {
			if (txstatus != null) {
				txmanager.rollback(txstatus);
			}

			logger.error("Task Excute transaction error - " + e, e);
		}

		if (txstatus != null) {
			txmanager.commit(txstatus);
		}
		
		return result;
		// return meterDao.getMeteringRate_detail(searchTime, tableName, tempTableName);
	}
	

	public static void main(String[] args) {
		List<Map<String, Object>> result = null;
		List<Map<String, Object>> result_detail = null;
		
		String searchTime = null;
		String tableName = null;
		String tempTableName = null;
		
		if (args.length < 2) {
			logger.info("Usage:");
			logger.info("Pilot2MeteringRateTask -DsearchTime=SearchTime -DtableName=TableName -DtempTableName=TempTableName");
			return;
		}

		for (int i = 0; i < args.length; i += 2) {
			String nextArg = args[i];
			if (nextArg.startsWith("-searchTime")) {
				searchTime = new String(args[i + 1]);
			} else if (nextArg.startsWith("-tableName")) {
				tableName = new String(args[i + 1]);
			} else if (nextArg.startsWith("-tempTableName")) {
				tempTableName = new String(args[i + 1]);
			}
		}

		logger.info("Pilot2MeteringRateTask params. SearchTime={}, TableName={}, TempTableName={}", searchTime, tableName, tempTableName);

		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-MeteringRate.xml" });
			DataUtil.setApplicationContext(ctx);

			Pilot2MeteringRateTask task = (Pilot2MeteringRateTask) ctx.getBean(Pilot2MeteringRateTask.class);
			result = task.execute(searchTime, tableName);
			result_detail = task.execute2(searchTime, tableName, tempTableName);
			
			makeExcel(result, result_detail, searchTime);
			
		} catch (Exception e) {
			logger.error("Pilot2MeteringRateTask excute error - " + e, e);
		} finally {
			logger.info("#### Pilot2MeteringRateTask finished. ####");
			System.exit(0);
		}
	}

	@SuppressWarnings("deprecation")
	private static void makeExcel(List<Map<String, Object>> result, List<Map<String, Object>> result_detail, String searchTime) {
		logger.info("=== ### makeExcel section ### ===\n");
		logger.info("===> result\n" + result);
		logger.info("===> detail result\n" + result_detail);
		
		long time = System.currentTimeMillis();
		SimpleDateFormat dayTimeFormat = new SimpleDateFormat("yyyyMMddHH");
		String dayTime = dayTimeFormat.format(new Date(time));
		String fileName = "./report/Pilot2MeteringReport_" + dayTime + ".xls";
		
		try {
			HSSFWorkbook workbook = new HSSFWorkbook();

			HSSFFont fontTitle = workbook.createFont();
			fontTitle.setFontHeightInPoints((short) 10);
			fontTitle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			HSSFFont fontHeader = workbook.createFont();
			fontHeader.setFontHeightInPoints((short) 10);
			fontHeader.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			HSSFFont fontBody = workbook.createFont();
			fontBody.setFontHeightInPoints((short) 10);
			fontBody.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			
			HSSFFont fontDetail = workbook.createFont();
			fontDetail.setFontHeightInPoints((short) 10);
			fontDetail.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			fontDetail.setColor(Font.COLOR_RED);
			
			HSSFRow row = null;
			HSSFCell cell = null;
			
			HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
			HSSFCellStyle highLightCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 0, 1, 1, 0);
			HSSFCellStyle detailHighLightCellStyle = ExcelUtil.getStyle(workbook, fontDetail, 1, 1, 1, 1, 0, 0, 0, 1, 0);
			
			titleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			titleCellStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
			titleCellStyle.setBorderBottom(CellStyle.BORDER_DOUBLE);
			
			dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			highLightCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			highLightCellStyle.setFillForegroundColor(IndexedColors.ROSE.index);
			
			
			long time2 = System.currentTimeMillis();
			SimpleDateFormat dayTimeFormat2 = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
			String dayTime2 = dayTimeFormat2.format(new Date(time2));
			
			final String reportTitle = dayTime2 + " 기준";
			final String reportTitle2 = "검침구간   " + searchTime + "00 ~ " + searchTime + "23";
			
			int startRow = 3;
			HSSFSheet sheet = workbook.createSheet(reportTitle);
			
			int colIdx = 0;
			sheet.setColumnWidth(colIdx++, 256 * 15);	// A : DCU, DSO
			sheet.setColumnWidth(colIdx++, 256 * 20);	// B : DSO, Meter
			sheet.setColumnWidth(colIdx++, 256 * 20);	// C : Related Meters, Modem
			sheet.setColumnWidth(colIdx++, 256 * 25);	// D : Success Meter (LP 100%), DCU
			sheet.setColumnWidth(colIdx++, 256 * 20);	// E : Metering Rate, FW Ver
			sheet.setColumnWidth(colIdx++, 256 * 15);	// F : FW Build
			sheet.setColumnWidth(colIdx++, 256 * 15);	// G : Status
			
			// Main Title (S) 
				// [ Excel Row no : 1 ] yyyy년 MM월 dd일 HH시 mm분
			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue(reportTitle);
			cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
			sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) 1));
			
				// [ Excel Row no : 2 ] 검침구간 yyyyMMddHH ~ yyyyMMddHH
            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle2);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(1, (short) 1, 0, (short) 1));
            // Main Title (E)

			// DSO별 통계를 위한 DSO List 추출 (S)
			ArrayList<String> dsoList = new ArrayList<String>();
			Map<String, Object> dsoCalcMap = new HashMap<String, Object>();
			
			for (Map<String, Object> map : result) {
				if (!dsoList.contains(map.get("DSO").toString())) {
					dsoList.add(map.get("DSO").toString());
					
					dsoCalcMap.put(map.get("DSO").toString() + "_relatedMeter", 0);
					dsoCalcMap.put(map.get("DSO").toString() + "_meteringCount", 0);
					dsoCalcMap.put(map.get("DSO").toString() + "_meteringRate", 0);
				}
			}
			// DSO별 통계를 위한 DSO List 추출 (E)

			// DCU별 통계 Title 표시 영역  (S)
			startRow += dsoList.size() + 4;
			row = sheet.createRow(startRow);

			cell = row.createCell(0);
			cell.setCellValue("DCU");
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(1);
			cell.setCellValue("DSO");
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(2);
			cell.setCellValue("Related Meters"); 
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(3);
			cell.setCellValue("Success Meter (LP 100%)");
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(4);
			cell.setCellValue("Fail Meter");
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(5);
			cell.setCellValue("Metering Rate");
			cell.setCellStyle(titleCellStyle);
			// DCU별 통계 Title 표시 영역  (E)
			
			
			
			
			// Data 표시 영역 (S)
			int cnt = 0;
            for (Map<String, Object> map : result) {
                HSSFCellStyle currentRowStyle;
                row = sheet.createRow(cnt + (startRow + 1));
                
                String sysId = (String.valueOf(map.get("SYS_ID")) != null) ? String.valueOf(map.get("SYS_ID")) : "";
                String dsoName = (String.valueOf(map.get("DSO")) != null) ? String.valueOf(map.get("DSO")) : "";
                String relatedMeter = (String.valueOf(map.get("RELATED_METER")) != null) ? String.valueOf(map.get("RELATED_METER")) : "0"; 
                String meteringCount =  (String.valueOf(map.get("METERING_COUNT")) != null) ? String.valueOf(map.get("METERING_COUNT")) : "0";
                Double meteringRate = (Double.valueOf(meteringCount)/Double.valueOf(relatedMeter)) * 100;
                meteringRate = Double.parseDouble(String.format("%.2f",meteringRate));
                
				if(meteringRate < 98)	
					currentRowStyle = highLightCellStyle;
				else
					currentRowStyle = dataCellStyle;
				
                cell = row.createCell(0);
                cell.setCellValue(sysId);
                cell.setCellStyle(currentRowStyle);
                
                cell = row.createCell(1);
                cell.setCellValue(dsoName);
                cell.setCellStyle(currentRowStyle);
                
                cell = row.createCell(2);
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                cell.setCellValue(Integer.valueOf(relatedMeter));
                cell.setCellStyle(currentRowStyle);
                
                cell = row.createCell(3);
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                cell.setCellValue(Integer.valueOf(meteringCount));
                cell.setCellStyle(currentRowStyle);
                
                cell = row.createCell(4);
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                cell.setCellValue(Integer.valueOf(relatedMeter) - Integer.valueOf(meteringCount));
                cell.setCellStyle(currentRowStyle);

                cell = row.createCell(5);
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                cell.setCellValue(meteringRate + "%");
                cell.setCellStyle(currentRowStyle);
                
                cnt++;
                
                // DSO별 통계 계산 로직 (S) 
                for (int i = 0; i < dsoList.size(); i++) {
                	int temp_relatedMeter = 0;
        			int temp_meteringCount = 0;
        			Double temp_meteringRate = (double) 0;
        			
					if (dsoName.equals(dsoList.get(i))) {
						temp_relatedMeter = Integer.valueOf(dsoCalcMap.get(dsoList.get(i) + "_relatedMeter").toString());
						temp_relatedMeter += Integer.valueOf(relatedMeter);
						
						temp_meteringCount = Integer.valueOf(dsoCalcMap.get(dsoList.get(i) + "_meteringCount").toString());
						temp_meteringCount += Integer.valueOf(meteringCount);

						temp_meteringRate = (Double.valueOf(temp_meteringCount)/Double.valueOf(temp_relatedMeter)) * 100;
						temp_meteringRate = Double.parseDouble(String.format("%.2f", temp_meteringRate));

						dsoCalcMap.put(map.get("DSO").toString() + "_relatedMeter", temp_relatedMeter);
						dsoCalcMap.put(map.get("DSO").toString() + "_meteringCount", temp_meteringCount);
						dsoCalcMap.put(map.get("DSO").toString() + "_meteringRate", temp_meteringRate);
                	}
                }
                // DSO별 통계 계산 로직 (E)
            }
			// Data 표시 영역 (E)
            
            logger.info("=== DSO List ===\n" + dsoList);
            logger.info("=== dsoCalcMap ===\n" + dsoCalcMap);
            
            // DSO별 통계 표시 (S)
            int row_count = 3;
            
			row = sheet.createRow(row_count);
			
			cell = row.createCell(0);
			cell.setCellValue("DSO별 통계 : ");
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(1);
			cell.setCellValue("DSO");
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(2);
			cell.setCellValue("Related Meters");
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(3);
			cell.setCellValue("Success Meter (LP 100%)");
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(4);
			cell.setCellValue("Fail Meter");
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(5);
			cell.setCellValue("Metering Rate");
			cell.setCellStyle(titleCellStyle);

			// Variable for total summary
        	int totalRelatedMeter = 0;
        	int totalMeteringCount = 0;
			
			for (int i = 0; i < dsoList.size(); i++) {
				HSSFCellStyle currentRowStyle;
				if(Double.parseDouble((dsoCalcMap.get(dsoList.get(i) + "_meteringRate").toString())) < 98)	
					currentRowStyle = highLightCellStyle;
				else
					currentRowStyle = dataCellStyle;
					
				row = sheet.createRow(row_count + (i + 1));
				
				cell = row.createCell(0);
	            cell.setCellValue("-");
	            cell.setCellStyle(currentRowStyle);
				
				cell = row.createCell(1);
	            cell.setCellValue(dsoList.get(i));
	            cell.setCellStyle(currentRowStyle);
	            
	            // Related Meters
	            cell = row.createCell(2);
	            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	            cell.setCellValue(Integer.valueOf(dsoCalcMap.get(dsoList.get(i) + "_relatedMeter").toString()));
	            cell.setCellStyle(currentRowStyle);
	            totalRelatedMeter += Integer.valueOf(dsoCalcMap.get(dsoList.get(i) + "_relatedMeter").toString());
	            
	            // Success Meter (LP 100%)
	            cell = row.createCell(3);
	            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	            cell.setCellValue(Integer.valueOf(dsoCalcMap.get(dsoList.get(i) + "_meteringCount").toString()));
	            cell.setCellStyle(currentRowStyle);
	            totalMeteringCount += Integer.valueOf(dsoCalcMap.get(dsoList.get(i) + "_meteringCount").toString());
	            
	            // Related Meter - Success Meter
	            cell = row.createCell(4);
	            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	            cell.setCellValue(Integer.valueOf(dsoCalcMap.get(dsoList.get(i) + "_relatedMeter").toString())
	            		- Integer.valueOf(dsoCalcMap.get(dsoList.get(i) + "_meteringCount").toString()));
	            cell.setCellStyle(currentRowStyle);
	            
	            // Metering Rate
	            cell = row.createCell(5);
	            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	            cell.setCellValue(dsoCalcMap.get(dsoList.get(i) + "_meteringRate").toString() + "%");
                cell.setCellStyle(currentRowStyle);
                
                // Total Rate
                if( i+1 == dsoList.size()){
                	Double totalMeteringRate = Double.parseDouble(String.format("%.2f", Double.valueOf(totalMeteringCount)/Double.valueOf(totalRelatedMeter) * 100));
                	if(totalMeteringRate < 98)	
    					currentRowStyle = highLightCellStyle;
    				else
    					currentRowStyle = dataCellStyle;
                	
                	row = sheet.createRow(row_count + (i + 2));
                	
                	cell = row.createCell(0);
    	            cell.setCellValue("-");
    	            cell.setCellStyle(currentRowStyle);
    				
    				cell = row.createCell(1);
    	            cell.setCellValue("Total");
    	            cell.setCellStyle(currentRowStyle);

    	            cell = row.createCell(2);
    	            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    	            cell.setCellValue(totalRelatedMeter);
    	            cell.setCellStyle(currentRowStyle);
    	            
    	            cell = row.createCell(3);
    	            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    	            cell.setCellValue(totalMeteringCount);
    	            cell.setCellStyle(currentRowStyle);
    	            
    	            cell = row.createCell(4);
    	            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    	            cell.setCellValue(totalRelatedMeter - totalMeteringCount);
    	            cell.setCellStyle(currentRowStyle);
    	            
    	            cell = row.createCell(5);
    	            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    	            cell.setCellValue(totalMeteringRate + "%");
                    cell.setCellStyle(currentRowStyle);
                    
                }
			}
			// DSO별 통계 표시 (E)
			
			int row_count2 = sheet.getPhysicalNumberOfRows() + dsoList.size();
			// 검침 상세 내역 (S)
			row = sheet.createRow(row_count2 + 1);
			cell = row.createCell(0);
			cell.setCellValue("검침 상세 내역");
			cell.setCellStyle(titleCellStyle);
			
			// Title 표시 영역  (S)
			row = sheet.createRow(row_count2 + 2);

			cell = row.createCell(0);
			cell.setCellValue("DSO");
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(1);
			cell.setCellValue("METER");
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(2);
			cell.setCellValue("MODEM"); 
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(3);
			cell.setCellValue("DCU");
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(4);
			cell.setCellValue("FW_Ver");
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(5);
			cell.setCellValue("FW_Build");
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(6);
			cell.setCellValue("Status");
			cell.setCellStyle(titleCellStyle);
			// Title 표시 영역  (E)
			
			for (int i = 0; i < result_detail.size(); i++) {
				HSSFCellStyle currentRowStyle;
				if(!result_detail.get(i).get("METERING_COUNT").toString().equals("24"))	
					currentRowStyle = detailHighLightCellStyle;
				else
					currentRowStyle = dataCellStyle;
				
				row = sheet.createRow(row_count2 + (i + 3));
				
				cell = row.createCell(0);
	            cell.setCellValue((result_detail.get(i).get("DSO") != null) ? result_detail.get(i).get("DSO").toString() : "");
	            cell.setCellStyle(currentRowStyle);
				
				cell = row.createCell(1);
				cell.setCellValue((result_detail.get(i).get("METER_ID") != null) ? result_detail.get(i).get("METER_ID").toString() : "");
	            cell.setCellStyle(currentRowStyle);
	            
	            cell = row.createCell(2);
	            cell.setCellValue((result_detail.get(i).get("DEVICE_SERIAL") != null) ? result_detail.get(i).get("DEVICE_SERIAL").toString() : "");
	            cell.setCellStyle(currentRowStyle);
	            
	            cell = row.createCell(3);
	            cell.setCellValue((result_detail.get(i).get("SYS_ID") != null) ? result_detail.get(i).get("SYS_ID").toString() : "");
	            cell.setCellStyle(currentRowStyle);
	            
	            cell = row.createCell(4);
	            cell.setCellValue((result_detail.get(i).get("FW_VER") != null) ? result_detail.get(i).get("FW_VER").toString() : "");
	            cell.setCellStyle(currentRowStyle);
	            
	            cell = row.createCell(5);
	            cell.setCellValue((result_detail.get(i).get("FW_REVISION") != null) ? result_detail.get(i).get("FW_REVISION").toString()+"("+Integer.toHexString(Integer.parseInt(result_detail.get(i).get("FW_REVISION").toString()))+")" : "");
	            cell.setCellStyle(currentRowStyle);
	            
	            cell = row.createCell(6);
	            cell.setCellValue((result_detail.get(i).get("METERING_COUNT") != null) ? result_detail.get(i).get("METERING_COUNT").toString() : "");
                cell.setCellStyle(currentRowStyle);
			}
				// Filter 적용
			sheet.setAutoFilter(new CellRangeAddress(row_count2 + 2, row_count2 + 2 + result_detail.size(),	0, 6));
			// 검침 상세 내역 (E)
			


			// 파일 생성 - excel
			FileOutputStream fs = null;
			try {
				fs = new FileOutputStream(fileName);
				workbook.write(fs);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fs != null)
					fs.close();
			}
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		
	}
}
