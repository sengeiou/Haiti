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

public class ConsumptionRankingMakeExcel {
	public ConsumptionRankingMakeExcel() {

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
			HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int dataGapsStartRow = 3;
//            int totalColumnCnt = 6;
			int dataCount = 0;

			HSSFSheet sheet = workbook.createSheet(reportTitle);
			int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 10);
            sheet.setColumnWidth(colIdx++, 256 * 21);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            int totalColumnCnt = colIdx;
            
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));

			// Title
			row = sheet.createRow(dataGapsStartRow);

			cell = row.createCell(0);
			cell.setCellValue(msgMap.get("ranking"));
            cell.setCellStyle(titleCellStyle);

			cell = row.createCell(1);
			cell.setCellValue(msgMap.get("date"));
            cell.setCellStyle(titleCellStyle);

			cell = row.createCell(2);
			cell.setCellValue(msgMap.get("totalUsage"));
            cell.setCellStyle(titleCellStyle);

			cell = row.createCell(3);
			cell.setCellValue(msgMap.get("contractNo"));
            cell.setCellStyle(titleCellStyle);

			cell = row.createCell(4);
			cell.setCellValue(msgMap.get("customerName"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(5);
            cell.setCellValue(msgMap.get("mdsId"));
            cell.setCellStyle(titleCellStyle);

			cell = row.createCell(6);
			cell.setCellValue(msgMap.get("tariffType"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(7);
			cell.setCellValue(msgMap.get("location"));
            cell.setCellStyle(titleCellStyle);

			// Title End

			// Data
			dataCount = result.size();

			for (int i = 0; i < dataCount; i++) {
				resultMap = result.get(i);
				row = sheet.createRow(i+ (dataGapsStartRow + 1));
				
				cell = row.createCell(0);
				cell.setCellValue(resultMap.get("rankingCnt") == null ? "" : resultMap.get("rankingCnt").toString());
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(1);
				cell.setCellValue(resultMap.get("period") == null ? "" : resultMap.get("period").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(2);
				cell.setCellValue(resultMap.get("totalUsage") == null ? "" : resultMap.get("totalUsage").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(3);
				cell.setCellValue(resultMap.get("contractNo") == null ? "" : resultMap.get("contractNo").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(4);
				cell.setCellValue(resultMap.get("customerName") == null ? "" : resultMap.get("customerName").toString());
				cell.setCellStyle(dataCellStyle);
				
                cell = row.createCell(5);
                cell.setCellValue(resultMap.get("mdsId") == null ? "" : resultMap.get("mdsId").toString());
                cell.setCellStyle(dataCellStyle);
                
				cell = row.createCell(6);
				cell.setCellValue(resultMap.get("tariffName") == null ? "" : resultMap.get("tariffName").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(7);
				cell.setCellValue(resultMap.get("locationName") == null ? "" : resultMap.get("locationName").toString());
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
}
