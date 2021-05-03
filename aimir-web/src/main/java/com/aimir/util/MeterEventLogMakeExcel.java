package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class MeterEventLogMakeExcel {

	public MeterEventLogMakeExcel() {

	}

	/**
	 * @param result
	 * @param msgMap
	 * @param isLast
	 * @param filePath
	 * @param fileName
	 */
	@SuppressWarnings("unchecked")
	public void writeReportExcel(List<Map<String, Object>> result,
			Map<String, String> msgMap, boolean isLast, String filePath,
			String fileName) {

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

			HSSFRow row 	= null;
			HSSFCell cell 	= null;
			HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
			String type 	= msgMap.get("type");
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			String fileFullPath = new StringBuilder().append(filePath).append(
					File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int meterEventLogStartRow = 3;
            int totalColumnCnt = 5;
			int dataCount = 0;

            HSSFSheet sheet = workbook.createSheet(reportTitle);

            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 10);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);

            // 각 타입에 따라서 컬럼수가 달라진다.
            if(type.equals("event")) {
            	totalColumnCnt = 9;
            }else if(type.equals("meter")) {
            	totalColumnCnt = 6;
            }
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
			sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));

            // Title
            row = sheet.createRow(meterEventLogStartRow);
			int cellCnt = 0;
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("number"));
            cell.setCellStyle(titleCellStyle);
            
			if(type.equals("event")){
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(msgMap.get("opentime"));
	            cell.setCellStyle(titleCellStyle);
	            
				cell = row.createCell(cellCnt++);
				cell.setCellValue(msgMap.get("writetime"));
	            cell.setCellStyle(titleCellStyle);
			}
						
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("eventName"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("location"));
            cell.setCellStyle(titleCellStyle);
            
			if(type.equals("meter")){
				cell = row.createCell(cellCnt++);
				cell.setCellValue(msgMap.get("occurFreq"));
	            cell.setCellStyle(titleCellStyle);
			}
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("meterid"));
            cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("gs1"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("metertype"));
            cell.setCellStyle(titleCellStyle);
            
			if(type.equals("event")){
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(msgMap.get("message"));
	            cell.setCellStyle(titleCellStyle);
	            
				cell = row.createCell(cellCnt++);
				cell.setCellValue(msgMap.get("troubleAdvice"));
	            cell.setCellStyle(titleCellStyle);
			}
			
			// Title End

			// Data

			dataCount = result.size();

			for (int i = 0; i < dataCount; i++) {
				resultMap = result.get(i);
				row = sheet.createRow(i + (meterEventLogStartRow + 1));    
				int cellCnt2 = 0;
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(i+1);
            	cell.setCellStyle(dataCellStyle);
            	
				if(type.equals("event")){
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("OPENTIME")==null?"":resultMap.get("OPENTIME").toString());
	            	cell.setCellStyle(dataCellStyle);
	            	
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("WRITETIME")==null?"":resultMap.get("WRITETIME").toString());
	            	cell.setCellStyle(dataCellStyle);
				}

				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("EVENTNAME")==null?"":resultMap.get("EVENTNAME").toString());
            	cell.setCellStyle(dataCellStyle);
            	
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("LOCATIONNAME")==null?"":resultMap.get("LOCATIONNAME").toString());
            	cell.setCellStyle(dataCellStyle);
            	
				if(type.equals("meter")){
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("METERCOUNT")==null?"":resultMap.get("METERCOUNT").toString());
	            	cell.setCellStyle(dataCellStyle);
				}
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("METERID")==null?"":resultMap.get("METERID").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("GS1")==null?"":resultMap.get("GS1").toString());
            	cell.setCellStyle(dataCellStyle);
            	
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("METERTYPE")==null?"":resultMap.get("METERTYPE").toString());
            	cell.setCellStyle(dataCellStyle);
            	
				if(type.equals("event")){
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("MESSAGE")==null?"":resultMap.get("MESSAGE").toString());
	            	cell.setCellStyle(dataCellStyle);
	            	
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("TROUBLEADVICE")==null?"":resultMap.get("TROUBLEADVICE").toString());
	            	cell.setCellStyle(dataCellStyle);
				}
			}
			// End Data

			// 파일 생성
			FileOutputStream fs = null;
			try {
				fs = new FileOutputStream(fileFullPath);
				workbook.write(fs);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fs != null)
					fs.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
		} // End Try
	}
	
}
