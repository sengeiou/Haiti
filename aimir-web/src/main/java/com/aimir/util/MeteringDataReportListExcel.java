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

public class MeteringDataReportListExcel {

    public MeteringDataReportListExcel() {

    }

    /**
     * @param result
     * @param msgMap
     * @param isLast
     * @param filePath
     * @param fileName
     */
    public void writeReportExcel(List<Map<String, Object>> result, Map<String, String> msgMap, boolean isLast, String filePath, String fileName) {

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

            Map<String, Object> resultMap = new HashMap<String, Object>();

            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int ConcentratorStartRow = 3;
            int dataCount = 0;

            HSSFSheet sheet = workbook.createSheet(reportTitle);

            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 4);
            sheet.setColumnWidth(colIdx++, 256 * 23);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 38);
            sheet.setColumnWidth(colIdx++, 256 * 28);
            sheet.setColumnWidth(colIdx++, 256 * 28);
            sheet.setColumnWidth(colIdx++, 256 * 12);
            sheet.setColumnWidth(colIdx++, 256 * 26);
            sheet.setColumnWidth(colIdx++, 256 * 26);
            sheet.setColumnWidth(colIdx++, 256 * 26);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 26);
            sheet.setColumnWidth(colIdx++, 256 * 26);
            sheet.setColumnWidth(colIdx++, 256 * 26);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);

            int totalColumnCnt = colIdx;
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));

            // Title
            row = sheet.createRow(ConcentratorStartRow);
            
            HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
            HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
            HSSFCellStyle dataCellStyle2 = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 2, 0);

            cell = row.createCell(0);
            cell.setCellValue(msgMap.get("msgNo"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(1);
            cell.setCellValue(msgMap.get("msgReadingDay"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(2);
            cell.setCellValue(msgMap.get("msgCustomerName"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(3);
            cell.setCellValue(msgMap.get("msgContractNo"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(4);
            cell.setCellValue(msgMap.get("msgMeterId"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(5);
            cell.setCellValue(msgMap.get("msgTotEnergyUsage")+"[kWh]");
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(6);
            cell.setCellValue(msgMap.get("msgContractDemand")+"[kW]");
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(7);
            cell.setCellValue(msgMap.get("msgPowerConsumption")+"[kW]");
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(8);
            cell.setCellValue(msgMap.get("msgTotKvah"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(9);
            cell.setCellValue(msgMap.get("msgMaxDmdKvahTimeRate1"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(10);
            cell.setCellValue(msgMap.get("msgMaxDmdKvahTimeRate2"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(11);
            cell.setCellValue(msgMap.get("msgMaxDmdKvahTimeRate3"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(12);
            cell.setCellValue(msgMap.get("msgMaxDmdKvahTime"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(13);
            cell.setCellValue(msgMap.get("msgMaxDmdKvahRate1"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(14);
            cell.setCellValue(msgMap.get("msgMaxDmdKvahRate2"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(15);
            cell.setCellValue(msgMap.get("msgMaxDmdKvahRate3"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(16);
            cell.setCellValue(msgMap.get("msgMaxDmdKvah"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(17);
            cell.setCellValue(msgMap.get("msgPhaseA")+"[kWh]");
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(18);
            cell.setCellValue(msgMap.get("msgPhaseB")+"[kWh]");
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(19);
            cell.setCellValue(msgMap.get("msgPhaseC")+"[kWh]");
            cell.setCellStyle(titleCellStyle);

            //Title End

            //Data
            dataCount = result.size();
            for(int i = 0 ; i < dataCount ; i++) {
                resultMap = result.get(i);
                row = sheet.createRow(i+ (ConcentratorStartRow + 1));

                cell = row.createCell(0);
                cell.setCellValue(i+1);
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(1);
                cell.setCellValue(resultMap.get("dateview") == null ? "" : resultMap.get("dateview").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(2);
                cell.setCellValue(resultMap.get("customerName") == null ? "" : resultMap.get("customerName").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(3);
                cell.setCellValue(resultMap.get("contractNo") == null ? "" : resultMap.get("contractNo").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(4);
                cell.setCellValue(resultMap.get("mdevId") == null ? "" : resultMap.get("mdevId").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(5);
                cell.setCellValue(resultMap.get("energyRateTot") == null ? "" : resultMap.get("energyRateTot").toString());
                cell.setCellStyle(dataCellStyle2);

                cell = row.createCell(6);
                cell.setCellValue(resultMap.get("contractDemand") == null ? "" : resultMap.get("contractDemand").toString());
                cell.setCellStyle(dataCellStyle2);

                cell = row.createCell(7);
                cell.setCellValue(resultMap.get("demandRateTot") == null ? "" : resultMap.get("demandRateTot").toString());
                cell.setCellStyle(dataCellStyle2);

                cell = row.createCell(8);
                cell.setCellValue(resultMap.get("kVah") == null ? "" :resultMap.get("kVah").toString());
                cell.setCellStyle(dataCellStyle2);

                cell = row.createCell(9);
                cell.setCellValue(resultMap.get("maxDmdkVahTimeRate1") == null ? "" :resultMap.get("maxDmdkVahTimeRate1").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(10);
                cell.setCellValue(resultMap.get("maxDmdkVahTimeRate2") == null ? "" :resultMap.get("maxDmdkVahTimeRate2").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(11);
                cell.setCellValue(resultMap.get("maxDmdkVahTimeRate3") == null ? "" :resultMap.get("maxDmdkVahTimeRate3").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(12);
                cell.setCellValue(resultMap.get("maxDmdkVahTime") == null ? "" :resultMap.get("maxDmdkVahTime").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(13);
                cell.setCellValue(resultMap.get("maxDmdkVahRate1") == null ? "" :resultMap.get("maxDmdkVahRate1").toString());
                cell.setCellStyle(dataCellStyle2);

                cell = row.createCell(14);
                cell.setCellValue(resultMap.get("maxDmdkVahRate2") == null ? "" :resultMap.get("maxDmdkVahRate2").toString());
                cell.setCellStyle(dataCellStyle2);

                cell = row.createCell(15);
                cell.setCellValue(resultMap.get("maxDmdkVahRate3") == null ? "" :resultMap.get("maxDmdkVahRate3").toString());
                cell.setCellStyle(dataCellStyle2);

                cell = row.createCell(16);
                cell.setCellValue(resultMap.get("maxDmdkVah") == null ? "" :resultMap.get("maxDmdkVah").toString());
                cell.setCellStyle(dataCellStyle2);
                
                cell = row.createCell(17);
                cell.setCellValue(resultMap.get("impkWhPhaseA") == null ? "" : resultMap.get("impkWhPhaseA").toString());
                cell.setCellStyle(dataCellStyle2);

                //MDIS_하드웨어Ver
                cell = row.createCell(18);
                cell.setCellValue(resultMap.get("impkWhPhaseB") == null ? "" : resultMap.get("impkWhPhaseB").toString());
                cell.setCellStyle(dataCellStyle2);

                cell = row.createCell(19);
                cell.setCellValue(resultMap.get("impkWhPhaseC") == null ? "" : resultMap.get("impkWhPhaseC").toString());
                cell.setCellStyle(dataCellStyle2);
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
        } //End Try

    }
}