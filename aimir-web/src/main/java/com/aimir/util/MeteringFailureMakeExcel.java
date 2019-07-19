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

import com.aimir.service.mvm.bean.FailureMeterData;

public class MeteringFailureMakeExcel {

    public MeteringFailureMakeExcel() {

    }

    /**
     * @param result
     * @param msgMap
     * @param isLast
     * @param filePath
     * @param fileName
     */
    public void writeReportExcel(List<FailureMeterData> result,
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
            HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
            HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);

            FailureMeterData resultMap = new FailureMeterData();
            String fileFullPath = new StringBuilder().append(filePath).append(
                    File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int meteringFailureStartRow = 3;
            int dataCount = 0;

            HSSFSheet sheet = workbook.createSheet(reportTitle);

            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 25);

            int totalColumnCnt = colIdx;
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));

            // Title
            row = sheet.createRow(meteringFailureStartRow);

            cell = row.createCell(0);
            cell.setCellValue(msgMap.get("contractNo"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(1);
            cell.setCellValue(msgMap.get("mcuId"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(2);
            cell.setCellValue(msgMap.get("mdsId"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(3);
            cell.setCellValue(msgMap.get("modemId"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(4);
            cell.setCellValue(msgMap.get("customerName"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(5);
            cell.setCellValue(msgMap.get("address"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(6);
            cell.setCellValue(msgMap.get("lastlastReadDate"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(7);
            cell.setCellValue(msgMap.get("failureCause"));
            cell.setCellStyle(titleCellStyle);
            // Title End

            // Data

            dataCount = result.size();

            for (int i = 0; i < dataCount; i++) {
                resultMap = (FailureMeterData) result.get(i);
                row = sheet.createRow(i + (meteringFailureStartRow + 1));
                cell = row.createCell(0);
                cell.setCellValue(resultMap.getCustomerId()==null?"":resultMap.getCustomerId().toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(1);
                cell.setCellValue(resultMap.getMcuId()==null?"":resultMap.getMcuId().toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(2);
                cell.setCellValue(resultMap.getMdsId()==null?"":resultMap.getMdsId().toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(3);
                cell.setCellValue(resultMap.getModemId()==null?"":resultMap.getModemId().toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(4);
                cell.setCellValue(resultMap.getCustomerName()==null?"":resultMap.getCustomerName().toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(5);
                cell.setCellValue(resultMap.getAddress()==null?"":resultMap.getAddress().toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(6);
                cell.setCellValue(resultMap.getLastlastReadDate()==null?"":resultMap.getLastlastReadDate().toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(7);
                cell.setCellValue(failureCauseMessageFunction(resultMap.getFailureCause()==null?"":resultMap.getFailureCause().toString(),msgMap));
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

    private String failureCauseMessageFunction(String faulureCause, Map<String, String> msgMap) {
        String strTemp = "";

        switch (Integer.parseInt(faulureCause)) {
            case 0:
                strTemp = msgMap.get("NotComm");
                break;
            case 1:
                strTemp = msgMap.get("CommstateYellow");
                break;
            case 2:
                strTemp = msgMap.get("MeteringFormatError");
                break;
            case 3:
                strTemp = msgMap.get("MeterChange");
                break;
            case 4:
                strTemp = msgMap.get("MeterStatusError");
                break;
            case 5:
                strTemp = msgMap.get("MeterTimeError");
                break;
            case 6:
                strTemp = msgMap.get("Success");
                break;
        }

        return strTemp;
    }
}