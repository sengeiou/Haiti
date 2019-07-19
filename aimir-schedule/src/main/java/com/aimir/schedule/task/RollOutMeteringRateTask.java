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
 * @author jiwoong
 *
 */
@Service
public class RollOutMeteringRateTask extends ScheduleTask {
	private static Logger logger = LoggerFactory.getLogger(RollOutMeteringRateTask.class);

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
			result = meterDao.getRollOutMeteringRate(searchTime);
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

	public static void main(String[] args) {
		List<Map<String, Object>> result = null;
		
		String searchTime = null;
		
		if (args.length < 2) {
			logger.info("Usage:");
			logger.info("RollOutMeteringRateTask -DsearchTime=SearchTime ");
			return;
		}

		for (int i = 0; i < args.length; i += 2) {
			String nextArg = args[i];
			if (nextArg.startsWith("-searchTime")) {
				searchTime = new String(args[i + 1]);
			}
		}

		logger.info("RollOutMeteringRateTask params. SearchTime={}", searchTime);

		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-MeteringRate.xml" });
			DataUtil.setApplicationContext(ctx);

			RollOutMeteringRateTask task = (RollOutMeteringRateTask) ctx.getBean(RollOutMeteringRateTask.class);
			result = task.execute(searchTime);
			
			makeExcel(result, searchTime);
			
		} catch (Exception e) {
			logger.error("RollOutMeteringRateTask excute error - " + e, e);
		} finally {
			logger.info("#### RollOutMeteringRateTask finished. ####");
			System.exit(0);
		}
	}

	@SuppressWarnings("deprecation")
	private static void makeExcel(List<Map<String, Object>> result, String searchTime) {
		logger.info("=== ### makeExcel section ### ===\n");
		logger.info("===> result\n" + result);
		
		Set<String> keys = result.get(0).keySet();
		
		ArrayList<String> summaryCol = new ArrayList<String>(keys.size());
		/*for(int i = 0; i < summaryCol.size(); i++) {
			summaryCol.set(i, keys);
		}*/
		
		// Set Columns of Query Result
		int idx = 0;
		summaryCol.add(idx++, "DCU");
		summaryCol.add(idx++, "DSO");
		summaryCol.add(idx++, "MSA");
		summaryCol.add(idx++, "RELATED_METER");
		summaryCol.add(idx++, "SUCCESS_METER");
		summaryCol.add(idx++, "FAIL_METER");
		summaryCol.add(idx++, "TOTAL_LP");
		summaryCol.add(idx++, "COLLECTED_LP");
		summaryCol.add(idx++, "REMAIN_LP");
		logger.info("summaryCol["+summaryCol.size()+"] : " + summaryCol );
		
		// Create Dynamic Summary Array
		String[][] summaryArr = new String[result.size()][summaryCol.size()];
		for(int i = 0; i < result.size(); i++){
			for(int j = 0; j < summaryCol.size(); j++){
				summaryArr[i][j] = result.get(i).get(summaryCol.get(j)).toString();
			}
		}
		
		// Get Time
		long time = System.currentTimeMillis();
		SimpleDateFormat dayTimeFormat = new SimpleDateFormat("yyyyMMddHH");
		String dayTime = dayTimeFormat.format(new Date(time));
		String fileName = "./report/RollOutMeteringReport_" + dayTime + ".xls";
		
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
			sheet.setColumnWidth(colIdx++, 256 * 20);	// A 
			sheet.setColumnWidth(colIdx++, 256 * 15);	// B 
			sheet.setColumnWidth(colIdx++, 256 * 20);	// C 
			sheet.setColumnWidth(colIdx++, 256 * 20);	// D 
			sheet.setColumnWidth(colIdx++, 256 * 20);	// E 
			sheet.setColumnWidth(colIdx++, 256 * 13);	// F 
			sheet.setColumnWidth(colIdx++, 256 * 13);	// G 
			sheet.setColumnWidth(colIdx++, 256 * 18);	// H
			sheet.setColumnWidth(colIdx++, 256 * 13);	// I 
			sheet.setColumnWidth(colIdx++, 256 * 20);	// J 
			
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

			// DSO별 통계를 위한 MSA List 추출 (S)
			ArrayList<String> msaList = new ArrayList<String>();
			Map<String, Object> msaCalcMap = new HashMap<String, Object>();
			logger.info("=== Make MSA-DSO List ===");
			for (Map<String, Object> map : result) {
				String MSA = map.get("MSA").toString();
				String DSO = map.get("DSO").toString();
				if (!msaList.contains(MSA + "_" + DSO)) {
					msaList.add(MSA + "_" + DSO);
					for(int i = 3; i < summaryCol.size(); i++){
						msaCalcMap.put(MSA + "_" + DSO + "_" + summaryCol.get(i), 0);
					}
				}
			}
			logger.info("ArrayList<String> msaList = "+ msaList.toString());
			logger.info("Map<String, Object> msaCalcMap = "+ msaCalcMap);
			// DSO별 통계를 위한 DSO List 추출 (E)

			// DCU별 통계 Title 표시 영역  (S)
			startRow += msaList.size() + 4;
			row = sheet.createRow(startRow);
			
			for(int i = 0; i < summaryCol.size(); i++){
				cell = row.createCell(i);
				cell.setCellValue(summaryCol.get(i));
				cell.setCellStyle(titleCellStyle);
				logger.info("cell["+i+"] = " + summaryCol.get(i));
			}
			cell = row.createCell(summaryCol.size());
			cell.setCellValue("METERING_RATE");
			cell.setCellStyle(titleCellStyle);
			// DCU별 통계 Title 표시 영역  (E)
			// Data 표시 영역 (S)
			for(int i = 0; i < result.size(); i++){
                row = sheet.createRow(i + (startRow + 1));
				String DSO = result.get(i).get("DSO").toString();
				String MSA = result.get(i).get("MSA").toString();
				

				int tmpTotal = Integer.valueOf(summaryArr[i][6]);
				int tmpCollectedLP = Integer.valueOf(summaryArr[i][7]);
				Double meteringRateDouble = (Double.valueOf(tmpCollectedLP)/Double.valueOf(tmpTotal)) * 100;
				meteringRateDouble = Double.parseDouble(String.format("%.2f",meteringRateDouble));

				HSSFCellStyle currentCellStyle = null;
	        	if(meteringRateDouble < 98)	currentCellStyle = highLightCellStyle;
				else	currentCellStyle = dataCellStyle;
	        	
				cell = row.createCell(summaryCol.size());
				cell.setCellStyle(currentCellStyle);
                cell.setCellValue(meteringRateDouble + "%");
                
				for(int j = 0; j < summaryCol.size(); j++){
					cell = row.createCell(j);
                	String tmpVal = summaryArr[i][j];

    				cell.setCellStyle(currentCellStyle);
	                cell.setCellValue(tmpVal);
                	if(j > 2 ) {
                		int tmpInt = 0;
                		tmpInt = Integer.valueOf(msaCalcMap.get(MSA + "_" + DSO + "_" + summaryCol.get(j)).toString());
                		msaCalcMap.put(MSA + "_" + DSO + "_" + summaryCol.get(j), Integer.valueOf(tmpVal)+tmpInt);
                	}
				}
			}
			// Data 표시 영역 (E)
            
            logger.info("=== msaCalcMap ===\n" + msaCalcMap);
            
            // MSA-DSO별 통계 표시 (S)
            int row_count = 3;
            
			row = sheet.createRow(row_count);

			cell = row.createCell(0);
			cell.setCellValue("MSA-DSO별 통계 : ");
			cell.setCellStyle(titleCellStyle);
			logger.info("cell[0] = MSA별 통계 : ");
			
			for(int i = 1; i < summaryCol.size(); i++){
				cell = row.createCell(i);
				cell.setCellValue(summaryCol.get(i));
				cell.setCellStyle(titleCellStyle);
				//logger.info("cell["+i+"] = " + summaryCol.get(i));
			}

			cell = row.createCell(summaryCol.size());
			cell.setCellValue("METERING_RATE");
			cell.setCellStyle(titleCellStyle);
			
			int[] totalArr = new int[summaryCol.size()];
			for(int i = 0; i < msaList.size(); i++){
				row = sheet.createRow(row_count + (i + 2));
				String MSA_DSO = msaList.get(i);
				String MSA = MSA_DSO.split("_")[0];
				String DSO = MSA_DSO.split("_")[1];
				
				int tmpTotal = Integer.valueOf(msaCalcMap.get(MSA_DSO + "_" + summaryCol.get(6)).toString());
				int tmpCollectedLP = Integer.valueOf(msaCalcMap.get(MSA_DSO + "_" + summaryCol.get(7)).toString());
            	Double tmpMeteringRate = (Double.valueOf(tmpCollectedLP)/Double.valueOf(tmpTotal)) * 100;
            	tmpMeteringRate = Double.parseDouble(String.format("%.2f",tmpMeteringRate));
            	
            	HSSFCellStyle currentCellStyle = null;
            	if(tmpMeteringRate < 98)	currentCellStyle = highLightCellStyle;
				else	currentCellStyle = dataCellStyle;
            	
				cell = row.createCell(0);
	            cell.setCellValue("-");
	            cell.setCellStyle(currentCellStyle);
				
				cell = row.createCell(1);
	            cell.setCellValue(DSO);
	            cell.setCellStyle(currentCellStyle);

				cell = row.createCell(2);
	            cell.setCellValue(MSA);
	            cell.setCellStyle(currentCellStyle);

				
				for(int j = 3; j < summaryCol.size() ; j++){
					cell = row.createCell(j);
					int tmpVal = Integer.valueOf(msaCalcMap.get(MSA_DSO + "_" + summaryCol.get(j)).toString());
					
		            cell.setCellStyle(currentCellStyle);
					cell.setCellValue(tmpVal);
		            totalArr[j] += tmpVal;
				}
				cell = row.createCell(summaryCol.size());
	            cell.setCellStyle(currentCellStyle);
				cell.setCellValue(tmpMeteringRate + "%");
			}
			row = sheet.createRow(row_count + 1);
			
			int tmpInt = totalArr[7];
			int tmpTotal = totalArr[6];
        	Double tmpMeteringRate = (Double.valueOf(tmpInt)/Double.valueOf(tmpTotal)) * 100;
        	tmpMeteringRate = Double.parseDouble(String.format("%.2f",tmpMeteringRate));

        	HSSFCellStyle currentCellStyle = null;
        	if(tmpMeteringRate < 98)	currentCellStyle = highLightCellStyle;
			else	currentCellStyle = dataCellStyle;
        	
			cell = row.createCell(0);
            cell.setCellValue("-");
            cell.setCellStyle(currentCellStyle);
			
			cell = row.createCell(1);
            cell.setCellValue("TOTAL");
            cell.setCellStyle(currentCellStyle);
            
            cell = row.createCell(2);
            cell.setCellValue("-");
            cell.setCellStyle(currentCellStyle);
            

			for(int i = 3; i < summaryCol.size() ; i++){
				cell = row.createCell(i);
				cell.setCellValue(totalArr[i]);
	            cell.setCellStyle(currentCellStyle);
			}
			cell = row.createCell(summaryCol.size());
			cell.setCellValue(tmpMeteringRate + "%");
            cell.setCellStyle(currentCellStyle);

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
