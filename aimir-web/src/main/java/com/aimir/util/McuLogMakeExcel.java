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
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import com.ibm.icu.util.Calendar;

/**
 * McuLogMakeExcel.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2012. 11. 09. v1.0        문동규   Grid 데이터를 Excel 파일로 생성해주는 프로그램.
 * </pre>
 */
public class McuLogMakeExcel {

    private List<String> headerList = null;     // header list
    private List<Integer> widthList = null;     // cell width list
    private List<String> alignList = null;      // cell align list
    private List<String> dataFieldList = null;  // cell value field name list

    //    private List<List<Object>> dataList = null;
    private List<Map<String, Object>> dataList = null;
    private String title = null;
    private boolean isRowColor = false;
    private String filePath = null;
    private String fileName = null;
    private String sheetName = null;
//    private String searchStartDate = null;
//    private String searchEndDate = null;
//    private String searchDateType = null;

    public static void main(String[] args) {
        McuLogMakeExcel excel = new McuLogMakeExcel();
        Calendar calendar = Calendar.getInstance();

        excel.setFileName("ebsreport" + calendar.getTimeInMillis());
        excel.setFilePath("d:\\tmp");
//        excel.setHeader(headerList);
//        excel.setData(list);
        excel.setTitle("Energy Balance Monitoring Report");
//        excel.setSearchStartDate("20120605");
//        excel.setSearchEndDate("20120605");
//        excel.setSearchDateType("1");
        excel.writeReportExcel();
    }

    /**
     *
     */
    public McuLogMakeExcel() {
    }

    /**
     * @param headerList
     * @param widthList
     * @param dataFieldList
     * @param dataList
     * @param alignList
     * @param title
     * @param isRowColor
     * @param filePath
     * @param fileName
     * @param sheetName
     */
    public McuLogMakeExcel(List<String> headerList, List<Integer> widthList, List<String> dataFieldList, List<Map<String, Object>> dataList,
            List<String> alignList, String title, Boolean isRowColor, String filePath, String fileName, String sheetName) {
        this.headerList = headerList;
        this.widthList = widthList;
        this.alignList = alignList;
        this.dataFieldList = dataFieldList;
        this.dataList = dataList;
        this.title = title;
        if (isRowColor != null) {
            this.isRowColor = isRowColor;
        } 
        this.filePath = filePath;
        this.fileName = fileName;
        this.sheetName = sheetName;
    }

    /**
     * method name : validate<b/>
     * method Desc : parameter 유효성 체크
     *
     * @return
     */
    private boolean validate() {
        if (headerList != null && headerList.size() > 0 && dataFieldList != null && dataFieldList.size() > 0 && dataList != null && dataList.size() > 0
                && headerList.size() == dataList.get(0).size() && filePath != null && !filePath.isEmpty() && fileName != null && !fileName.isEmpty()) {
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
        if (!validate()) {
            return;
        }

        if (StringUtil.nullToBlank(sheetName).isEmpty()) {
            sheetName = "ExcelReport";
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

            HSSFSheet sheet = workbook.createSheet(sheetName);

            HSSFCellStyle titleStyle = workbook.createCellStyle();
            if (title != null) {
                titleStyle.setFont(titleFont);
                titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            }

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

            HSSFCellStyle dataNormalLeftStyle = workbook.createCellStyle(); // 좌측정렬 data style
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

            HSSFCellStyle dataNormalCenterStyle = workbook.createCellStyle();   // 중앙정렬 data style
            dataNormalCenterStyle.setFont(dataFont);
            dataNormalCenterStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            dataNormalCenterStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataNormalCenterStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            dataNormalCenterStyle.setTopBorderColor(HSSFColor.BLACK.index);
            dataNormalCenterStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            dataNormalCenterStyle.setBottomBorderColor(HSSFColor.BLACK.index);
            dataNormalCenterStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            dataNormalCenterStyle.setLeftBorderColor(HSSFColor.BLACK.index);
            dataNormalCenterStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            dataNormalCenterStyle.setRightBorderColor(HSSFColor.BLACK.index);

            HSSFCellStyle dataNormalRightStyle = workbook.createCellStyle();    // 우측정렬 data style
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

            int columnLen = headerList.size();
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
                // 빈줄
                row = sheet.createRow(rowIdx++);
            }

            // create Header
            row = sheet.createRow(rowIdx++);
            colIdx = 0;

            for (String obj : headerList) {
                cell = row.createCell(colIdx++);
                cell.setCellStyle(headerStyle);
                cell.setCellValue(obj);
            }

            boolean isOddNum = false;
            int idx = 0;

            for (Map<String, Object> map : dataList) {
                if (idx%2 == 0) {
                    isOddNum = false;
                } else {
                    isOddNum = true;
                }
                row = sheet.createRow(rowIdx++);
                colIdx = 0;
                for (String field : dataFieldList) {
                    cell = row.createCell(colIdx);
                    if (isRowColor) {   // TODO - 정렬 추가해야 함
                        if (isOddNum) {
                            cell.setCellStyle(dataFstColorStyle);
                        } else {
                            cell.setCellStyle(dataSndColorStyle);
                        }
                    } else {
                        if (StringUtil.nullToBlank(alignList.get(colIdx)).isEmpty() || alignList.get(colIdx).equals("l")) {
                            cell.setCellStyle(dataNormalLeftStyle);
                        } else if (alignList.get(colIdx).equals("c")) {
                            cell.setCellStyle(dataNormalCenterStyle);
                        } else if (alignList.get(colIdx).equals("r")) {
                            cell.setCellStyle(dataNormalRightStyle);
                        }
                    }
                    cell.setCellValue((map.get(field) == null) ? "" : map.get(field).toString());
                    colIdx++;
                }
                idx++;
            }

            // setting column width
            if (widthList != null) {
//                int cnt = 0;
                int len = widthList.size();
                
                for (int j = 0; j < columnLen; j++) {
                    if (j >= len) {     // 컬럼개수보다 적은 경우 width 개수 만큼 설정
                        break;
                    }

                    if (widthList.get(j) == null || widthList.get(j) < 0) {   // null or 음수 인 경우 width 설정하지 않음
                        continue;
                    } else if (widthList.get(j) == 0) {      // 0 인 경우 width 자동설정
                        sheet.autoSizeColumn(j);
                    } else {
//                        sheet.setColumnWidth(j, 256 * 8);
                        sheet.setColumnWidth(j, widthList.get(j));
                    }
                }
//                sheet.setColumnWidth(cnt++, 256 * 8);
//                sheet.autoSizeColumn(cnt++);
//                sheet.autoSizeColumn(cnt++);
//                sheet.setColumnWidth(cnt++, 256 * 14);
//                sheet.setColumnWidth(cnt++, 256 * 25);
//                sheet.setColumnWidth(cnt++, 256 * 35);
//                sheet.setColumnWidth(cnt++, 256 * 26);
                
            }

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
     * @return the headerList
     */
    public List<String> getHeaderList() {
        return headerList;
    }

    /**
     * @param headerList the headerList to set
     */
    public void setHeaderList(List<String> headerList) {
        this.headerList = headerList;
    }

    /**
     * @return the widthList
     */
    public List<Integer> getWidthList() {
        return widthList;
    }

    /**
     * @param widthList the widthList to set
     */
    public void setWidthList(List<Integer> widthList) {
        this.widthList = widthList;
    }

    /**
     * @return the alignList
     */
    public List<String> getAlignList() {
        return alignList;
    }

    /**
     * @param alignList the alignList to set
     */
    public void setAlignList(List<String> alignList) {
        this.alignList = alignList;
    }

    /**
     * @return the dataList
     */
    public List<Map<String, Object>> getDataList() {
        return dataList;
    }

    /**
     * @param dataList the dataList to set
     */
    public void setDataList(List<Map<String, Object>> dataList) {
        this.dataList = dataList;
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
     * @return the sheetName
     */
    public String getSheetName() {
        return sheetName;
    }

    /**
     * @param sheetName the sheetName to set
     */
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    /**
     * @return the dataFieldList
     */
    public List<String> getDataFieldList() {
        return dataFieldList;
    }

    /**
     * @param dataFieldList the dataFieldList to set
     */
    public void setDataFieldList(List<String> dataFieldList) {
        this.dataFieldList = dataFieldList;
    }

}
