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

public class OperationLogMaxMakeExcel {

	public OperationLogMaxMakeExcel() {

	}

	/**
	 * @param result
	 * @param msgMap
	 * @param isLast
	 * @param filePath
	 * @param fileName
	 */
	@SuppressWarnings("unchecked")
	public void writeReportExcel(List<Object> result,
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

			HSSFRow row = null;
			HSSFCell cell = null;
			HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
			
			Map<String, String> resultMap = new HashMap<String, String>();
			String fileFullPath = new StringBuilder().append(filePath).append(
					File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int operationlogListStartRow = 3;
            int totalColumnCnt = 11;
			int dataCount = 0;

			HSSFSheet sheet = workbook.createSheet(reportTitle);
			
            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 7);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 23);
 

            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));

            // Title
            row = sheet.createRow(operationlogListStartRow);

			int cellCnt = 0;
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("no"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("openTime"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("targetType"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("targetName"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("accomplishmentType"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("accomplisher"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("operation"));
            cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("operationStatus"));
            cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("description"));
            cell.setCellStyle(titleCellStyle);
		            
			// Title End

			// Data

			dataCount = result.size();

			for (int i = 0; i < dataCount; i++) {
				resultMap = (Map<String, String>) result.get(i);
				row = sheet.createRow(i + (operationlogListStartRow + 1));  
				int cellCnt2 = 0;
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("no")==null?"":resultMap.get("no").toString());			
            	cell.setCellStyle(dataCellStyle);
            	
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("openTime")==null?"":resultMap.get("openTime").toString());
            	cell.setCellStyle(dataCellStyle);
            	
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("targetType")==null?"":resultMap.get("targetType").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("targetName")==null?"":resultMap.get("targetName").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("accomplishmentType")==null?"":resultMap.get("accomplishmentType").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("accomplisher")==null?"":resultMap.get("accomplisher").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("operation")==null?"":resultMap.get("operation").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("operationStatus")==null?"":resultMap.get("operationStatus").toString());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("description")==null?"":resultMap.get("description").toString());
            	cell.setCellStyle(dataCellStyle);
				
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
