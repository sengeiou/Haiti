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
import org.apache.poi.hssf.util.HSSFColor;

public class SupplierMaxLocationMakeExcel {

    private static Log log = LogFactory.getLog(SupplierMaxLocationMakeExcel.class);

    public SupplierMaxLocationMakeExcel() {

    }

    /**
     * @param result
     * @param msgMap
     * @param isLast
     * @param filePath
     * @param fileName
     */
    @SuppressWarnings("unchecked")
    public void writeReportExcel(List<Object> result, Map<String, String> msgMap, String filePath, String fileName,
            Integer maxColCnt) {
        final String sheetname = "sheet";

        try {
            HSSFWorkbook workbook = new HSSFWorkbook();

            HSSFFont headerFont = workbook.createFont();
            headerFont.setFontHeightInPoints((short)10);
            headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFFont dataFont = workbook.createFont();
            dataFont.setFontHeightInPoints((short)10);

            HSSFRow row = null;
            HSSFCell cell = null;

            Map<String, String> resultMap = new HashMap<String, String>();
            String fileFullPath = new StringBuilder().append(filePath).append(
                    File.separator).append(fileName).toString();
            int dataCount = 0;

            HSSFSheet sheet = workbook.createSheet(sheetname);

            HSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            headerStyle.setFillForegroundColor(HSSFColor.LIME.index);
            headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            headerStyle.setTopBorderColor(HSSFColor.BLACK.index);
            headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            headerStyle.setBottomBorderColor(HSSFColor.BLACK.index);
            headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            headerStyle.setLeftBorderColor(HSSFColor.BLACK.index);
            headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            headerStyle.setRightBorderColor(HSSFColor.BLACK.index);

            HSSFCellStyle dataNormalLeftStyle = workbook.createCellStyle();
            dataNormalLeftStyle.setFont(dataFont);
            dataNormalLeftStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            dataNormalLeftStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataNormalLeftStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            dataNormalLeftStyle.setTopBorderColor(HSSFColor.BLACK.index);
            dataNormalLeftStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            dataNormalLeftStyle.setBottomBorderColor(HSSFColor.BLACK.index);
            dataNormalLeftStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            dataNormalLeftStyle.setLeftBorderColor(HSSFColor.BLACK.index);
            dataNormalLeftStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            dataNormalLeftStyle.setRightBorderColor(HSSFColor.BLACK.index);

            int colIdx = 0;
            for (int i = 0; i < maxColCnt; i++) {
                sheet.setColumnWidth(colIdx++, 256 * 15);
            }

            // Header Start
            row = sheet.createRow(0);
            int cellCnt = 0;

            for (int i = 0; i < maxColCnt; i++) {
                cell = row.createCell(cellCnt++);
                cell.setCellValue(msgMap.get("location") + (i + 1));
                cell.setCellStyle(headerStyle);
            }
            // Header End

            // Data Start
            dataCount = result.size();

            for (int i = 0; i < dataCount; i++) {
                resultMap = (Map<String, String>) result.get(i);
                row = sheet.createRow(i + 1);

                for (int j = 0; j < maxColCnt; j++) {
                    cell = row.createCell(j);
                    cell.setCellValue(resultMap.get("col" + (j + 1)) == null ? "" : resultMap.get("col" + (j + 1)).toString());
                    cell.setCellStyle(dataNormalLeftStyle);
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
            log.error(e.toString(), e);
        } // End Try
    }
}