package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import com.aimir.constants.CommonConstants.DateType;
import com.ibm.icu.util.Calendar;

/**
 * MakeExcel.java Description
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2012. 6. 1.   v1.0       문동규
 * </pre>
 */
public class EnergyBalanceMonitoringReportExcel {

    private List<String> header = null;
    private List<List<Object>> data = null;
    private String title = null;
    private boolean isRowColor = false;
    private String filePath = null;
    private String fileName = null;
    private String searchStartDate = null;
    private String searchEndDate = null;
    private String searchDateType = null;

    public static void main(String[] args) {
        EnergyBalanceMonitoringReportExcel excel = new EnergyBalanceMonitoringReportExcel();

        Calendar calendar = Calendar.getInstance();

        excel.setFileName("ebsreport" + calendar.getTimeInMillis());
        excel.setFilePath("d:\\tmp");
//        excel.setHeader(headerList);
//        excel.setData(list);
        excel.setTitle("Energy Balance Monitoring Report");
        excel.setSearchStartDate("20120605");
        excel.setSearchEndDate("20120605");
        excel.setSearchDateType("1");

        excel.writeReportExcel();
    }

    /**
     *
     */
    public EnergyBalanceMonitoringReportExcel() {
    }

    /**
     * @param header
     * @param data
     * @param title
     * @param isRowColor
     * @param fontName
     * @param fontSize
     * @param filePath
     * @param fileName
     */
    public EnergyBalanceMonitoringReportExcel(List<String> header, List<List<Object>> data, String title, Boolean isRowColor,
            String filePath, String fileName, String searchStartDate, String searchEndDate, String searchDateType) {
        this.header = header;
        this.data = data;
        this.title = title;
        if (isRowColor != null) {
            this.isRowColor = isRowColor;
        }
        this.filePath = filePath;
        this.fileName = fileName;
        this.searchStartDate = searchStartDate;
        this.searchEndDate = searchEndDate;
        this.searchDateType = searchDateType;
    }

    /**
     * method name : validate<b/>
     * method Desc :
     *
     * @return
     */
    private boolean validate() {
        if (header != null && header.size() > 0 && data != null && data.size() > 0 && data.get(0) != null
                && data.get(0).size() > 0 && header.size() == data.get(0).size() && filePath != null && !filePath.isEmpty()
                && fileName != null && !fileName.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * method name : writeReportExcel<b/>
     * method Desc :
     *
     */
    public void writeReportExcel() {

        final String sheetname = "EbsReport";

        if (!validate()) {
            return;
        }

        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFFont titleFont = null;

            if (title != null) {
                titleFont = workbook.createFont();
                titleFont.setFontHeightInPoints((short)14);
            }

            HSSFFont headerFont = workbook.createFont();
            headerFont.setFontHeightInPoints((short)10);
            headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFFont dataFont = workbook.createFont();
            dataFont.setFontHeightInPoints((short)10);

            HSSFRow row = null;
            HSSFCell cell = null;

            HSSFSheet sheet = workbook.createSheet(sheetname);

            HSSFCellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

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

            HSSFCellStyle dateOptStyle = workbook.createCellStyle();
            dateOptStyle.setFont(dataFont);
            dateOptStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            dateOptStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

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

            HSSFCellStyle dataNormalRightStyle = workbook.createCellStyle();
            dataNormalRightStyle.setFont(dataFont);
            dataNormalRightStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            dataNormalRightStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            dataNormalRightStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            dataNormalRightStyle.setTopBorderColor(HSSFColor.BLACK.index);
            dataNormalRightStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            dataNormalRightStyle.setBottomBorderColor(HSSFColor.BLACK.index);
            dataNormalRightStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            dataNormalRightStyle.setLeftBorderColor(HSSFColor.BLACK.index);
            dataNormalRightStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            dataNormalRightStyle.setRightBorderColor(HSSFColor.BLACK.index);

            HSSFCellStyle dataFstColorStyle = workbook.createCellStyle();
            dataFstColorStyle.setFont(dataFont);
            dataFstColorStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataFstColorStyle.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
            dataFstColorStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            dataFstColorStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            dataFstColorStyle.setTopBorderColor(HSSFColor.BLACK.index);
            dataFstColorStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            dataFstColorStyle.setBottomBorderColor(HSSFColor.BLACK.index);
            dataFstColorStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            dataFstColorStyle.setLeftBorderColor(HSSFColor.BLACK.index);
            dataFstColorStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            dataFstColorStyle.setRightBorderColor(HSSFColor.BLACK.index);

            HSSFCellStyle dataSndColorStyle = workbook.createCellStyle();
            dataSndColorStyle.setFont(dataFont);
            dataSndColorStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataSndColorStyle.setFillForegroundColor(HSSFColor.CORNFLOWER_BLUE.index);
            dataSndColorStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            dataSndColorStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            dataSndColorStyle.setTopBorderColor(HSSFColor.BLACK.index);
            dataSndColorStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            dataSndColorStyle.setBottomBorderColor(HSSFColor.BLACK.index);
            dataSndColorStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            dataSndColorStyle.setLeftBorderColor(HSSFColor.BLACK.index);
            dataSndColorStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            dataSndColorStyle.setRightBorderColor(HSSFColor.BLACK.index);

            int columnLen = header.size();
            int rowIdx = 0;
            int colIdx = 0;

            if (title != null) {
                // create Title
                row = sheet.createRow(rowIdx);

                for (int i = 0; i < columnLen; i++) {
                    cell = row.createCell(i);
                    cell.setCellStyle(titleStyle);

                    if (i == 0) {
                        cell.setCellValue(title);
                    }
                }

                sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, (columnLen - 1)));
                rowIdx++;
            }

            // 빈줄
            row = sheet.createRow(rowIdx++);

            // 조회조건
            row = sheet.createRow(rowIdx++);
            colIdx = 0;

            for (int i = 0; i < columnLen; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(dateOptStyle);
            }

            if ((DateType.DAILY.getCode()).equals(searchDateType) || (DateType.WEEKLY.getCode()).equals(searchDateType)
                    || (DateType.MONTHLY.getCode()).equals(searchDateType)) { // 일간/주간/월간
                cell.setCellValue(searchStartDate);
            } else if ((DateType.PERIOD.getCode()).equals(searchDateType)) { // 기간
                cell.setCellValue(searchStartDate + " ~ " + searchEndDate);
            }

            // create Header
            row = sheet.createRow(rowIdx++);
            colIdx = 0;

            for (String obj : header) {
                cell = row.createCell(colIdx++);
                cell.setCellStyle(headerStyle);
                cell.setCellValue(obj);
            }

            boolean isOddNum = false;
            int idx = 0;

            for (List<Object> list : data) {
                if (idx%2 == 0) {
                    isOddNum = false;
                } else {
                    isOddNum = true;
                }
                row = sheet.createRow(rowIdx++);
                colIdx = 0;
                for (Object obj : list) {
                    cell = row.createCell(colIdx);
                    if (isRowColor) {
                        if (isOddNum) {
                            cell.setCellStyle(dataFstColorStyle);
                        } else {
                            cell.setCellStyle(dataSndColorStyle);
                        }
                    } else {
                        if (colIdx == 1 || colIdx == 2) {
                            cell.setCellStyle(dataNormalLeftStyle);
                        } else {
                            cell.setCellStyle(dataNormalRightStyle);
                        }
                    }
                    cell.setCellValue(obj.toString());
                    colIdx++;
                }
                idx++;
            }

            int cnt = 0;
            sheet.setColumnWidth(cnt++, 256 * 8);
            sheet.autoSizeColumn(cnt++);
            sheet.autoSizeColumn(cnt++);
            sheet.setColumnWidth(cnt++, 256 * 14);
            sheet.setColumnWidth(cnt++, 256 * 25);
            sheet.setColumnWidth(cnt++, 256 * 35);
            sheet.setColumnWidth(cnt++, 256 * 26);

            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
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
            e.printStackTrace();
        }
    }

    /**
     * @return the header
     */
    public List<String> getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(List<String> header) {
        this.header = header;
    }

    /**
     * @return the data
     */
    public List<List<Object>> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<List<Object>> data) {
        this.data = data;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the isRowColor
     */
    public boolean isRowColor() {
        return isRowColor;
    }

    /**
     * @param isRowColor the isRowColor to set
     */
    public void setRowColor(boolean isRowColor) {
        this.isRowColor = isRowColor;
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the searchStartDate
     */
    public String getSearchStartDate() {
        return searchStartDate;
    }

    /**
     * @param searchStartDate the searchStartDate to set
     */
    public void setSearchStartDate(String searchStartDate) {
        this.searchStartDate = searchStartDate;
    }

    /**
     * @return the searchEndDate
     */
    public String getSearchEndDate() {
        return searchEndDate;
    }

    /**
     * @param searchEndDate the searchEndDate to set
     */
    public void setSearchEndDate(String searchEndDate) {
        this.searchEndDate = searchEndDate;
    }

    /**
     * @return the searchDateType
     */
    public String getSearchDateType() {
        return searchDateType;
    }

    /**
     * @param searchDateType the searchDateType to set
     */
    public void setSearchDateType(String searchDateType) {
        this.searchDateType = searchDateType;
    }

}
