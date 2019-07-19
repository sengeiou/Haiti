package com.aimir.schedule.task;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class SmallScaleSLAMeteringRateTask extends ScheduleTask {
	private static Logger logger = LoggerFactory.getLogger(SmallScaleMeteringRateTask.class);

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
	
	public List<Map<String, Object>> execute(String searchTime) {
		TransactionStatus txstatus = null;
		txstatus = txmanager.getTransaction(null);
		List<Map<String, Object>> result = null;
		
		try {
			result = meterDao.getSmallScaleSLAMeteringRate(searchTime);
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

	public List<Map<String, Object>> execute2(String searchTime) {
		TransactionStatus txstatus = null;
		txstatus = txmanager.getTransaction(null);
		List<Map<String, Object>> result = null;
		
		try {
			result = meterDao.getSmallScaleSLAMeteringRate_detail(searchTime);
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
		
		if (args.length < 2) {
			logger.info("Usage:");
			logger.info("SmallScaleMeteringRateTask -DsearchTime=SearchTime ");
			return;
		}

		for (int i = 0; i < args.length; i += 2) {
			String nextArg = args[i];
			if (nextArg.startsWith("-searchTime")) {
				searchTime = new String(args[i + 1]);
			}
		}

		logger.info("SmallScaleMeteringRateTask params. SearchTime={}", searchTime);

		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-MeteringRate.xml" });
			DataUtil.setApplicationContext(ctx);

			SmallScaleSLAMeteringRateTask task = (SmallScaleSLAMeteringRateTask) ctx.getBean(SmallScaleSLAMeteringRateTask.class);
			result = task.execute(searchTime);
			result_detail = task.execute2(searchTime);
			
			makeExcel(result, result_detail, searchTime);
			
		} catch (Exception e) {
			logger.error("SmallScaleMeteringRateTask excute error - " + e, e);
		} finally {
			logger.info("#### SmallScaleMeteringRateTask finished. ####");
			System.exit(0);
		}
	}

	@SuppressWarnings("deprecation")
	private static void makeExcel(List<Map<String, Object>> result, List<Map<String, Object>> result_detail, String searchTime) {
		logger.info("=== ### makeExcel section ### ===\n");
		logger.info("===> result\n" + result);
		logger.info("===> detail result\n" + result_detail);
		
		ArrayList<String> summaryCol = new ArrayList<String>(35);
		int idx = 0;
		summaryCol.add(idx++, "SYS_ID");
		summaryCol.add(idx++, "DSO");
		summaryCol.add(idx++, "RELATED_METER");
		summaryCol.add(idx++, "TOTAL_LP");
		summaryCol.add(idx++, searchTime + "01");
		summaryCol.add(idx++, searchTime + "02");
		summaryCol.add(idx++, searchTime + "03");
		summaryCol.add(idx++, searchTime + "04");
		summaryCol.add(idx++, searchTime + "05");
		summaryCol.add(idx++, searchTime + "06");
		summaryCol.add(idx++, searchTime + "07");
		summaryCol.add(idx++, searchTime + "08");
		summaryCol.add(idx++, searchTime + "09");
		summaryCol.add(idx++, searchTime + "10");
		summaryCol.add(idx++, searchTime + "11");
		summaryCol.add(idx++, searchTime + "12");
		summaryCol.add(idx++, searchTime + "13");
		summaryCol.add(idx++, searchTime + "14");
		summaryCol.add(idx++, searchTime + "15");
		summaryCol.add(idx++, searchTime + "16");
		summaryCol.add(idx++, searchTime + "17");
		summaryCol.add(idx++, searchTime + "18");
		summaryCol.add(idx++, searchTime + "19");
		summaryCol.add(idx++, searchTime + "20");
		summaryCol.add(idx++, searchTime + "21");
		summaryCol.add(idx++, searchTime + "22");
		summaryCol.add(idx++, searchTime + "23");
		summaryCol.add(idx++, searchTime + "24");
		summaryCol.add(idx++, searchTime + "25");
		summaryCol.add(idx++, searchTime + "26");
		summaryCol.add(idx++, searchTime + "27");
		summaryCol.add(idx++, searchTime + "28");
		summaryCol.add(idx++, searchTime + "29");
		summaryCol.add(idx++, searchTime + "30");
		summaryCol.add(idx++, searchTime + "31");
		logger.info("summaryCol["+summaryCol.size()+"] : " + summaryCol );
		String[][] summaryArr = new String[result.size()][summaryCol.size()];
		for(int i = 0; i < result.size(); i++){
			for(int j = 0; j < summaryCol.size(); j++){
				summaryArr[i][j] = result.get(i).get(summaryCol.get(j)).toString();
			}
		}
		ArrayList<String> detailCol = new ArrayList<String>(37);
		idx = 0;
		detailCol.add(idx++, "DSO");
		detailCol.add(idx++, "METER_ID");
		detailCol.add(idx++, "DEVICE_SERIAL");
		detailCol.add(idx++, "SYS_ID");
		detailCol.add(idx++, "FW_VER");
		detailCol.add(idx++, "FW_REVISION");
		detailCol.add(idx++, searchTime + "01");
		detailCol.add(idx++, searchTime + "02");
		detailCol.add(idx++, searchTime + "03");
		detailCol.add(idx++, searchTime + "04");
		detailCol.add(idx++, searchTime + "05");
		detailCol.add(idx++, searchTime + "06");
		detailCol.add(idx++, searchTime + "07");
		detailCol.add(idx++, searchTime + "08");
		detailCol.add(idx++, searchTime + "09");
		detailCol.add(idx++, searchTime + "10");
		detailCol.add(idx++, searchTime + "11");
		detailCol.add(idx++, searchTime + "12");
		detailCol.add(idx++, searchTime + "13");
		detailCol.add(idx++, searchTime + "14");
		detailCol.add(idx++, searchTime + "15");
		detailCol.add(idx++, searchTime + "16");
		detailCol.add(idx++, searchTime + "17");
		detailCol.add(idx++, searchTime + "18");
		detailCol.add(idx++, searchTime + "19");
		detailCol.add(idx++, searchTime + "20");
		detailCol.add(idx++, searchTime + "21");
		detailCol.add(idx++, searchTime + "22");
		detailCol.add(idx++, searchTime + "23");
		detailCol.add(idx++, searchTime + "24");
		detailCol.add(idx++, searchTime + "25");
		detailCol.add(idx++, searchTime + "26");
		detailCol.add(idx++, searchTime + "27");
		detailCol.add(idx++, searchTime + "28");
		detailCol.add(idx++, searchTime + "29");
		detailCol.add(idx++, searchTime + "30");
		detailCol.add(idx++, searchTime + "31");
		//logger.info("detailCol : " + detailCol );
		logger.info("summaryCol["+detailCol.size()+"] : " + detailCol );
		String[][] detailArr = new String[result_detail.size()][detailCol.size()];
		for(int i = 0; i < result_detail.size(); i++){
			for(int j = 0; j < detailCol.size(); j++){
				detailArr[i][j] = result_detail.get(i).get(detailCol.get(j)).toString();
			}
		}
		
		long time = System.currentTimeMillis();
		SimpleDateFormat dayTimeFormat = new SimpleDateFormat("yyyyMM");
		String dayTime = dayTimeFormat.format(new Date(time));
		String fileName = "./report/SmallScaleSLAMeteringReport.xls";
		
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
			final String reportTitle2 = "검침구간   " + searchTime + "01 ~ " + searchTime + "31";
			
			int startRow = 3;
			HSSFSheet sheet = workbook.createSheet(reportTitle);
			
			int colIdx = 0;
			sheet.setColumnWidth(colIdx++, 256 * 15);	// A 
			sheet.setColumnWidth(colIdx++, 256 * 20);	// B 
			sheet.setColumnWidth(colIdx++, 256 * 20);	// C 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// D 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// E 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// F 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// G 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// 
			
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
			logger.info("=== Make DSO List ===");
			for (Map<String, Object> map : result) {
				String DSO = map.get("DSO").toString();
				if (!dsoList.contains(DSO)) {
					dsoList.add(DSO);
					for(int i = 2; i < summaryCol.size(); i++){
						dsoCalcMap.put(DSO + "_" + summaryCol.get(i), 0);
					}
				}
			}
			logger.info("ArrayList<String> dsoList = "+ dsoList.toString());
			logger.info("Map<String, Object> dsoCalcMap = "+ dsoCalcMap);
			// DSO별 통계를 위한 DSO List 추출 (E)

			// DCU별 통계 Title 표시 영역  (S)
			startRow += dsoList.size() + 4;
			logger.info("startRow = "+ startRow);
			row = sheet.createRow(startRow);
			
			for(int i = 0; i < summaryCol.size(); i++){
				cell = row.createCell(i);
				cell.setCellValue(summaryCol.get(i));
				cell.setCellStyle(titleCellStyle);
				logger.info("cell["+i+"] = " + summaryCol.get(i));
			}
			// DCU별 통계 Title 표시 영역  (E)
			// Data 표시 영역 (S)
			for(int i = 0; i < result.size(); i++){
				String dsoName = result.get(i).get("DSO").toString();
				logger.info("dsoName = "+ dsoName);
	            row = sheet.createRow(i + (startRow + 1));
				logger.info("row = "+ i + (startRow + 1));
				for(int j = 0; j < summaryCol.size(); j++){
					cell = row.createCell(j);
                	String tmpVal = result.get(i).get(summaryCol.get(j)).toString();
	                if(j > 3 ){
						logger.info("J > 3");
						
	                	String tmpTLP = result.get(i).get(summaryCol.get(3)).toString();
	                	Double tmpMeteringRate = (Double.valueOf(tmpVal)/Double.valueOf(tmpTLP)) * 100;
	                	tmpMeteringRate = Double.parseDouble(String.format("%.2f",tmpMeteringRate));
	                	
		                cell.setCellValue(result.get(i).get(summaryCol.get(j)).toString()+" ("+tmpMeteringRate+"%)");
						logger.info("cell[" + j + "] = " + result.get(i).get(summaryCol.get(j)).toString()+" ("+tmpMeteringRate+"%)");

		                // DSO별 통계 계산 로직 (S) 
						int tmpInt = Integer.valueOf(dsoCalcMap.get(dsoName + "_" + summaryCol.get(j)).toString());;
						dsoCalcMap.put(dsoName + "_" + summaryCol.get(j), Integer.valueOf(tmpVal)+tmpInt);
						logger.info("dsoCalcMap.put("+dsoName + "_" + summaryCol.get(j)+","+Integer.valueOf(tmpVal)+tmpInt+")");
		                // DSO별 통계 계산 로직 (E)
						
	                	if(tmpMeteringRate < 98){
	                		cell.setCellStyle(highLightCellStyle);
	                	}else{
	                    	cell.setCellStyle(dataCellStyle);
	                	}
	                	
                		continue;
	                }
	                cell.setCellValue(tmpVal);
	                logger.info("cell[" + j + "] = " + result.get(i).get(summaryCol.get(j)).toString());
                	cell.setCellStyle(dataCellStyle);
	                // DSO별 통계 Total 계산 로직 (S) 
                	if(j == 2 || j == 3){ // total
						int tmpInt = Integer.valueOf(dsoCalcMap.get(dsoName + "_" + summaryCol.get(j)).toString());
						dsoCalcMap.put(dsoName + "_" + summaryCol.get(j), Integer.valueOf(tmpVal)+tmpInt);
						logger.info("dsoCalcMap.put("+dsoName + "_" + summaryCol.get(j)+","+Integer.valueOf(tmpVal)+tmpInt+")");
                	}
	                // DSO별 통계 Total 계산 로직 (E)
				}

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
			logger.info("cell[0] = DSO별 통계 : ");
			
			for(int i = 1; i < summaryCol.size(); i++){
				cell = row.createCell(i);
				cell.setCellValue(summaryCol.get(i));
				cell.setCellStyle(titleCellStyle);
				//logger.info("cell["+i+"] = " + summaryCol.get(i));
			}
			int[] totalArr = new int[summaryCol.size()];
			for(int i = 0; i < dsoList.size(); i++){
				row = sheet.createRow(row_count + (i + 1));
				String dsoName = dsoList.get(i);
				cell = row.createCell(0);
	            cell.setCellValue("-");
	            cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(1);
	            cell.setCellValue(dsoName);
	            cell.setCellStyle(dataCellStyle);
	            
				for(int j = 2; j < summaryCol.size() ; j++){
					int tmpInt = Integer.valueOf(dsoCalcMap.get(dsoName + "_" + summaryCol.get(j)).toString());
					int tmpTotal = Integer.valueOf(dsoCalcMap.get(dsoName + "_" + summaryCol.get(3)).toString());

                	Double tmpMeteringRate = (Double.valueOf(tmpInt)/Double.valueOf(tmpTotal)) * 100;
                	tmpMeteringRate = Double.parseDouble(String.format("%.2f",tmpMeteringRate));
					cell = row.createCell(j);

					if(j > 3){
						cell.setCellValue(tmpInt+" ("+tmpMeteringRate+"%)");
						if(tmpMeteringRate < 98){
	                		cell.setCellStyle(highLightCellStyle);
	                	}else{
	                    	cell.setCellStyle(dataCellStyle);
	                	}
					}
					else{
						cell.setCellValue(tmpInt);
			            cell.setCellStyle(dataCellStyle);
					}
		            totalArr[j] += tmpInt;
				}
			}
			row = sheet.createRow(row_count + (dsoList.size() + 1));
			cell = row.createCell(0);
            cell.setCellValue("-");
            cell.setCellStyle(dataCellStyle);
			
			cell = row.createCell(1);
            cell.setCellValue("Total");
            cell.setCellStyle(dataCellStyle);
            
			for(int j = 2; j < summaryCol.size() ; j++){
				int tmpInt = totalArr[j];
				int tmpTotal = totalArr[3];

            	Double tmpMeteringRate = (Double.valueOf(tmpInt)/Double.valueOf(tmpTotal)) * 100;
            	tmpMeteringRate = Double.parseDouble(String.format("%.2f",tmpMeteringRate));
				cell = row.createCell(j);

				if(j > 3){
					cell.setCellValue(tmpInt+" ("+tmpMeteringRate+"%)");
					if(tmpMeteringRate < 98){
                		cell.setCellStyle(highLightCellStyle);
                	}else{
                    	cell.setCellStyle(dataCellStyle);
                	}
				}
				else{
					cell.setCellValue(tmpInt);
		            cell.setCellStyle(dataCellStyle);
				}
			}

			
			int row_count2 = sheet.getPhysicalNumberOfRows() + dsoList.size();
			// 검침 상세 내역 (S)
			row = sheet.createRow(row_count2 + 1);
			cell = row.createCell(0);
			cell.setCellValue("검침 상세 내역");
			cell.setCellStyle(titleCellStyle);
			
			// Title 표시 영역  (S)
			row = sheet.createRow(row_count2 + 2);

			for(int i = 0; i < detailCol.size(); i++){
				cell = row.createCell(i);
				cell.setCellValue(detailCol.get(i));
				cell.setCellStyle(titleCellStyle);
				logger.info("cell["+i+"] = " + detailCol.get(i));
			}
			// Title 표시 영역  (E)
			
			for (int i = 0; i < result_detail.size(); i++) {
				row = sheet.createRow(row_count2 + 3 + i);
				for(int j = 0; j < detailCol.size(); j++){
					cell = row.createCell(j);
					cell.setCellValue(result_detail.get(i).get(detailCol.get(j)).toString());
					if(j > 5 && !result_detail.get(i).get(detailCol.get(j)).toString().equals("24")){
						cell.setCellStyle(detailHighLightCellStyle);
						continue;
					}
					cell.setCellStyle(dataCellStyle);
				}
				
			}
			// Filter 적용
			sheet.setAutoFilter(new CellRangeAddress(row_count2 + 2, row_count2 + 2 + result_detail.size(),	0, 36));
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
