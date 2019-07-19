package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.aimir.bo.system.prepaymentMgmt.CreatingCustomerMgmtController;

public class CreatingCustomerMgmtMakeExcel {

    private static Log logger = LogFactory.getLog(CreatingCustomerMgmtController.class);

	public CreatingCustomerMgmtMakeExcel() {

	}

	/**
	 * @param result
	 * @param headerMap
	 * @param isLast
	 * @param filePath
	 * @param fileName
	 */
    public void writeReportExcel(List<Map<String, Object>> result, Map<String, String> headerMap, boolean isLast, String filePath,
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

			Map<String, Object> resultMap = new HashMap<String, Object>();
			String fileFullPath = new StringBuilder().append(filePath).append(
					File.separator).append(fileName).toString();

            int errorListStartRow = 0;
			int dataCount = 0;

			HSSFSheet sheet = workbook.createSheet();
			
            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 40);

            row = sheet.createRow(errorListStartRow);

			int cellCnt = 0;
			cell = row.createCell(cellCnt++);
			cell.setCellValue(headerMap.get("customerNo"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(headerMap.get("customerName"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(headerMap.get("contractNumber"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(headerMap.get("mobileNo"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(headerMap.get("errMsg"));
            cell.setCellStyle(titleCellStyle);

			// Title End

			// Data

			dataCount = result.size();

			for (int i = 0; i < dataCount; i++) {
				resultMap = result.get(i);
				row = sheet.createRow(i + (errorListStartRow + 1));  
				int cellCnt2 = 0;

				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("customerNo") == null ? "" : resultMap.get("customerNo").toString());
            	cell.setCellStyle(dataCellStyle);
            	
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("customerName") == null ? "" : resultMap.get("customerName").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("contractNumber") == null ? "" : resultMap.get("contractNumber").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("mobileNo") == null ? "" : resultMap.get("mobileNo").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("errMsg") == null ? "" : resultMap.get("errMsg").toString());
            	cell.setCellStyle(dataCellStyle);
			}
			// End Data

			// 파일 생성
			FileOutputStream fs = null;
			try {
				fs = new FileOutputStream(fileFullPath);
				workbook.write(fs);
			} catch (Exception e) {
				logger.error(e, e);
			} finally {
				if (fs != null)
					fs.close();
			}

		} catch (Exception e) {
		    logger.error(e, e);
		} // End Try
	}
}
