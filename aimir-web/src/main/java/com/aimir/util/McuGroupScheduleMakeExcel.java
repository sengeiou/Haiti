package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
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
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * McuGroupScheduleMakeExcel.java Description 
 *
 * 
 * Date           Version     Author   Description
 * 2017. 08. 03.   v1.0        TEN        
 * 	
 */
public class McuGroupScheduleMakeExcel {
    private static Log log = LogFactory.getLog(McuGroupScheduleMakeExcel.class);
    public McuGroupScheduleMakeExcel() {
    }

    /**
     * @param resultList
     * @param filePath
     * @param fileName
     */
    
    public void writeScheduleReportExcel(List<Map<String, Object>> resultList, String filePath, String fileName) {
    	
    		List<Integer> cntList = new ArrayList<Integer>();
    		
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();

            HSSFFont fontTitle = workbook.createFont();
            fontTitle.setFontHeightInPoints((short) 14);
            fontTitle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFFont fontHeader = workbook.createFont();
            fontHeader.setFontHeightInPoints((short) 10);
            fontHeader.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFFont fontBody = workbook.createFont();
            fontBody.setFontHeightInPoints((short) 10);
            fontBody.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
            
            HSSFFont fontFail = workbook.createFont();
            fontBody.setFontHeightInPoints((short) 10);
            fontBody.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
            fontFail.setColor(Font.COLOR_RED);

            HSSFRow row = null;
            HSSFRow row2 = null;
            HSSFCell cell = null;
            HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 0, 2, 1, 0);
            HSSFCellStyle DCUCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 0, 3, 1, 0);
            HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
            HSSFCellStyle centerCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 1, 0);
            HSSFCellStyle blankCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 4, 1, 0);
            HSSFCellStyle failCellStyle = ExcelUtil.getStyle(workbook, fontFail, 1, 1, 1, 1, 0, 0, 0, 1, 0);

            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = "Concentrator Schedule";
            int TemplateStartRow = 3;
            int totalColumnCnt = 6;

            HSSFSheet sheet = workbook.createSheet(reportTitle);
            
            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 4);	//No
            sheet.setColumnWidth(colIdx++, 256 * 10);	//DCU
            sheet.setColumnWidth(colIdx++, 256 * 20);	//DCU
            
            /*sheet.setColumnWidth(colIdx++, 256 * 15);	//NAME
            sheet.setColumnWidth(colIdx++, 256 * 20);	//Condition
            sheet.setColumnWidth(colIdx++, 256 * 19);	//Task
            sheet.setColumnWidth(colIdx++, 256 * 15);	//Suspend
*/  

            row = sheet.createRow(0);
            cell = row.createCell(0);
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt)));
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 1, 1, 1, 1, 0, 0, 2, 1, 0));

            // Title Row
            row = sheet.createRow(TemplateStartRow);

            // font.setFontHeightInPoints((short)10);
            cell = row.createCell(0);
            cell.setCellValue("No");
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("DCU");
            cell.setCellStyle(DCUCellStyle);
            
            cell = row.createCell(2);
            cell.setCellValue("Connection");
            cell.setCellStyle(DCUCellStyle);
            // Title End

            // Schedule Data
            //Data Row
            row2 = sheet.createRow(TemplateStartRow+1);
            int cnt=0;
            int max=0;
            String name;
            String condition;
            String task;
            String suspend;
            String status;
            for(int i = 0; i< resultList.size(); i++) {
            	row2 = sheet.createRow(TemplateStartRow+1+i);
            	
	            cell = row2.createCell(0);
	            cell.setCellValue(i+1);
	            cell.setCellStyle(dataCellStyle);
	
	            cell = row2.createCell(1);
	            cell.setCellValue(resultList.get(i).get("sysId").toString());
	            cell.setCellStyle(centerCellStyle);

	            cell = row2.createCell(2);
	            status = resultList.get(i).get("status").toString();
	            if(status.equals("FAIL")) {
	            	cell.setCellValue(status);
	            	cell.setCellStyle(failCellStyle);
	            }else {
	            	cell.setCellValue(status);
	            	cell.setCellStyle(centerCellStyle);
	            }
	
	            cnt = 0;
            	for(int j=0; j<resultList.get(i).size()+cnt-2; j+=5 ) {
            		name = null;      
            		condition = null; 
            		task = null;      
            		suspend = null;
            		
            		cell = row2.createCell(4+j);
		            if(resultList.get(i).get("name"+cnt) != null) {
		            	name = resultList.get(i).get("name"+cnt).toString();
			            cell.setCellValue(name);
			            cell.setCellStyle(dataCellStyle);
		            }
		            
		            cell = row2.createCell(5+j);
		            if(resultList.get(i).get("condition"+cnt) != null) {
		            	condition = resultList.get(i).get("condition"+cnt).toString();
			            cell.setCellValue(condition);
			            cell.setCellStyle(dataCellStyle);
		            }
		            
		            cell = row2.createCell(6+j);
		            if(resultList.get(i).get("task"+cnt) != null) {
		            	task = resultList.get(i).get("task"+cnt).toString();
			            cell.setCellValue(task);
			            cell.setCellStyle(dataCellStyle);
		            }
		            
		            cell = row2.createCell(7+j);
		            if(resultList.get(i).get("suspend"+cnt) != null) {
		            	suspend = resultList.get(i).get("suspend"+cnt).toString();
			            cell.setCellValue(suspend);
			            cell.setCellStyle(dataCellStyle);
		            }
		            cnt++;
	            }
	            cntList.add(cnt);
            }
            // Schedule Data End

            // Title Row
            max = Collections.max(cntList);
            int k = 0;
            for(int l=0; l<max; l++) {
            	
            	cell = row.createCell(3+k);
                cell.setCellValue("");
                cell.setCellStyle(blankCellStyle);
                sheet.setColumnWidth(colIdx++, 256 * 2);
            	
                cell = row.createCell(4+k);
                cell.setCellValue("Name");
                cell.setCellStyle(titleCellStyle);
                sheet.setColumnWidth(colIdx++, 256 * 15);
                
                cell = row.createCell(5+k);
                cell.setCellValue("Condition");
                cell.setCellStyle(titleCellStyle);
                sheet.setColumnWidth(colIdx++, 256 * 20);
                
                cell = row.createCell(6+k);
                cell.setCellValue("Task");
                cell.setCellStyle(titleCellStyle);
                sheet.setColumnWidth(colIdx++, 256 * 19);
                
                cell = row.createCell(7+k);
                cell.setCellValue("Suspend");
                cell.setCellStyle(titleCellStyle);
                sheet.setColumnWidth(colIdx++, 256 * 15);
                k+=5;
            }
            // Title End

            // File Create
            FileOutputStream fs = null;
            try {
                fs = new FileOutputStream(fileFullPath);
                workbook.write(fs);
                log.info("Excel Create Success ~ !");
            } catch (Exception e) {
                log.error(e,e);
            } finally {
                if (fs != null)
                    fs.close();
            }

        } catch (Exception e) {
        	log.error(e,e);
        } // End Try

    }

}