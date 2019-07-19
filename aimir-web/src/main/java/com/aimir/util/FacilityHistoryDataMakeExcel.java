package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.aimir.model.device.EndDeviceLog;
/**
 * MeteringDataMakeExcel.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2012. 8.10   v1.0       prrain         
 *
 */
public class FacilityHistoryDataMakeExcel {

    public FacilityHistoryDataMakeExcel() {

    }

    /**
     * @param result
     * @param msgMap
     * @param isLast
     * @param filePath
     * @param fileName
     */
    public void writeReportExcel(List<EndDeviceLog> result, Map<String, String> msgMap, boolean isLast, String filePath, String fileName) {

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

            EndDeviceLog resultMap = new EndDeviceLog();

            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = "설비 운영 상태 변경 이력 보고서 ";
            int meteringDataStartRow = 3;
            int totalColumnCnt = 10;
            int dataCount = 0;

            HSSFSheet sheet = workbook.createSheet(reportTitle);

            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 30);
  
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));

            // Title
            row = sheet.createRow(meteringDataStartRow);
   
            //font.setFontHeightInPoints((short)10);
            cell = row.createCell(0);
            cell.setCellValue(msgMap.get("location"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(1);
            cell.setCellValue(msgMap.get("categoryCode"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(2);
            cell.setCellValue(msgMap.get("friendlyName"));
            cell.setCellStyle(titleCellStyle);
			
            cell = row.createCell(3);
            cell.setCellValue(msgMap.get("preStatusCode"));
            cell.setCellStyle(titleCellStyle);
                        
            cell = row.createCell(4);
            cell.setCellValue(msgMap.get("statusCode"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(5);
            cell.setCellValue(msgMap.get("writeDatetime"));
            cell.setCellStyle(titleCellStyle);
            
  
            //Title End
            
            //Data
            

            dataCount = result.size();
            for(int i = 0 ; i < dataCount ; i++) {
            	resultMap = result.get(i);
            	row = sheet.createRow(i+ (meteringDataStartRow + 1));      

            	cell = row.createCell(0);
            	cell.setCellValue(resultMap.getLocationName());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(1);
            	cell.setCellValue(resultMap.getCategoryCode());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(2);
            	cell.setCellValue(resultMap.getFriendlyName());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(3);
            	cell.setCellValue(resultMap.getPreStatusCode());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(4);
            	cell.setCellValue(resultMap.getStatusCode());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(5);
            	cell.setCellValue(resultMap.getWriteDatetime());
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
			// TODO: handle exception
		} //End Try
    }    	

}
