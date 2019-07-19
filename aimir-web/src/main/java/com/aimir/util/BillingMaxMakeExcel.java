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

public class BillingMaxMakeExcel {

	public BillingMaxMakeExcel() {

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
			HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 0, 1, 0, 1, 0, 1, 0, 1, 0);
			HSSFCellStyle title2CellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);

			Map<String, String> resultMap = new HashMap<String, String>();
			String fileFullPath = new StringBuilder().append(filePath).append(
					File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int voltagelevelListStartRow = 2;
            int totalColumnCnt = result.size();
			int dataCount = 0;

			HSSFSheet sheet = workbook.createSheet(reportTitle);
			
            int colIdx = 0;
  
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
   
            //Title
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (9)));

            // header2
            row = sheet.createRow(voltagelevelListStartRow);

			int cellCnt = 0;
	
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("rownum"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("yyyymmdd"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("customerName"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("contractNo"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("meterName"));
            cell.setCellStyle(title2CellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("total"));
            cell.setCellStyle(title2CellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("usage"));
            cell.setCellStyle(title2CellStyle);
            
    		cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("max"));
            cell.setCellStyle(title2CellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("usageCharge"));
            cell.setCellStyle(title2CellStyle);

			// Title End

			// Data

			dataCount = result.size();

			for (int i = 0; i < dataCount; i++) {
				resultMap = (Map<String, String>) result.get(i);
				row = sheet.createRow(i + (voltagelevelListStartRow + 1));  
				int cellCnt2 = 0;
			            	
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("rownum")==null?"":resultMap.get("rownum").toString());
            	cell.setCellStyle(dataCellStyle);
            	
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("yyyymmdd")==null?"":String.valueOf(resultMap.get("yyyymmdd")));
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("customerName")==null?"":resultMap.get("customerName").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("contractNo")==null?"":resultMap.get("contractNo").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("meterName")==null?"":resultMap.get("meterName").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("total")==null?"":resultMap.get("total").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("usage")==null?"":resultMap.get("usage").toString());
            	cell.setCellStyle(dataCellStyle);
            	
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("max")==null?"":resultMap.get("max").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("usageCharge")==null?"":resultMap.get("usageCharge").toString());
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
