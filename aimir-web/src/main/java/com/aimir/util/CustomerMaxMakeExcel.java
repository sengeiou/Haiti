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

public class CustomerMaxMakeExcel {

    private static Log log = LogFactory.getLog(CustomerMaxMakeExcel.class);

    public CustomerMaxMakeExcel() {

    }

    /**
     * @param result
     * @param msgMap
     * @param isLast
     * @param filePath
     * @param fileName
     */
    @SuppressWarnings("unchecked")
    public void writeReportExcel(List<Map<String,Object>> result,
            Map<String, String> msgMap, boolean isLast, String filePath,
            String fileName, Long maxRows) {

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
            String reportTitle = msgMap.get("title");
            int startRow = 3;
            int dataCount = 0;
            	
			HSSFSheet sheet = workbook.createSheet(reportTitle);
            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 7);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 25);
            sheet.setColumnWidth(colIdx++, 256 * 65);
            
            int totalColumnCnt = colIdx;

            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));
            
            // Title
            row = sheet.createRow(startRow);

            int cellCnt = 0;
            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("no"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("customerNo"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("customerName"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("address"));
            cell.setCellStyle(titleCellStyle);
            // Title End
	            
            // Data
            int temp = fileName.indexOf("(");
            Long fileNumber = 0L;
            Long no = 1L;
            if(temp > -1) {
            	fileNumber = Long.parseLong(fileName.substring(temp+1,temp+2));
            	no = ((fileNumber-1)*maxRows)+1;
            }
            
            dataCount = result.size();
            
            for (int i = 0; i < dataCount; i++) {
                resultMap = (Map<String, Object>) result.get(i);
                row = sheet.createRow(i + (startRow + 1));
                int cellCnt2 = 0;
	
                cell = row.createCell(cellCnt2++);
                cell.setCellValue(no++);
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("customerNo")==null?"":resultMap.get("customerNo").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("customerName")==null?"":resultMap.get("customerName").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("address")==null?"":resultMap.get("address").toString());
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
            log.error(e.toString(), e);
        } // End Try
    }

}
