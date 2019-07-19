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
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * SupplierGMDataMakeExcel.java Description 
 *
 * 
 * Date           Version     Author   Description
 * 2012. 80. 07.   v1.0        jiae         
 *
 */

public class SupplierGMDataMakeExcel {
	private static Log log = LogFactory.getLog(SupplierGMDataMakeExcel.class);
    public SupplierGMDataMakeExcel() {}
    
    public void writeExportExcelGM(List<Map<String, Object>> result, Map<String, String> titleMap, boolean isLast, String filePath, String fileName) {
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

            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            int SupplierStartRow = 0;
            int totalColumnCnt = 9;
            int dataCount = 0;

            HSSFSheet sheet = workbook.createSheet(fileName);

            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(fileName);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));
            
            row = sheet.createRow(SupplierStartRow);

 
            // Title  
            cell = row.createCell(0);
            cell.setCellValue(titleMap.get("tariffType"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(1);
            cell.setCellValue(titleMap.get("season"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(2);
            cell.setCellValue(titleMap.get("basicRate"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(3);
            cell.setCellValue(titleMap.get("usageUnitPrice"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(4);
            cell.setCellValue(titleMap.get("salePrice"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(5);
            cell.setCellValue(titleMap.get("adjustmentFactor"));
            cell.setCellStyle(titleCellStyle);
            
            //Title End
            
            //Data
            dataCount = result.size();
            for(int i = 0 ; i < dataCount ; i++) {
            	resultMap = result.get(i);
            	row = sheet.createRow(i+ (SupplierStartRow + 1));      

            	cell = row.createCell(0);
            	cell.setCellValue((resultMap.get("tariffType") == null)? "" :resultMap.get("tariffType").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(1);
            	cell.setCellValue((resultMap.get("season") == null) ? "" : resultMap.get("season").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(2);
            	cell.setCellValue((resultMap.get("basicRate") == null) ? "" : resultMap.get("basicRate").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(3);
            	cell.setCellValue((resultMap.get("usageUnitPrice") == null) ? "" : resultMap.get("usageUnitPrice").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(4);
            	cell.setCellValue((resultMap.get("share") == null) ? "" : resultMap.get("share").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(5);
            	cell.setCellValue((resultMap.get("adjustmentFactor") == null) ? "" : resultMap.get("adjustmentFactor").toString());
            	cell.setCellStyle(dataCellStyle);
            }
            //End Data
            
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
			e.getStackTrace();
			// TODO: handle exception
		} //End Try
	    
    } 	
    
}
