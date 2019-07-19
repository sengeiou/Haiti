package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
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

import com.aimir.model.system.Supplier;

public class PrepaymentBalanceHistoryMakeExcel {

	private static Log logger = LogFactory.getLog(PrepaymentBalanceHistoryMakeExcel.class);

	public PrepaymentBalanceHistoryMakeExcel() {

	}

	/**
	 * @param result
	 * @param msgMap
	 * @param isLast
	 * @param filePath
	 * @param fileName
	 */
	public void writeReportExcel(List<Map<String, Object>> result, Map<String, String> msgMap, boolean isLast, String filePath, String fileName, Supplier supplier) {

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

			HSSFRow row = null;
			HSSFCell cell = null;
			HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
			final String reportTitle = msgMap.get("title");
			int dataGapsStartRow = 3;
			int totalColumnCnt = 9;
			int dataCount = 0;

			String lang = supplier.getLang().getCode_2letter();
			String country = supplier.getCountry().getCode_2letter();
			DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
			DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

			HSSFSheet sheet = workbook.createSheet(reportTitle);
			int colIdx = 0;
			sheet.setColumnWidth(colIdx++, 256 * 19);
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 35);
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 23);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);

			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue(reportTitle);
			cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
			sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt - 1)));

			// Title
			row = sheet.createRow(dataGapsStartRow);

			cell = row.createCell(0);
			cell.setCellValue(msgMap.get("date"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(1);
			cell.setCellValue(msgMap.get("cost"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(2);
			cell.setCellValue(msgMap.get("consumption"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(3);
			cell.setCellValue(msgMap.get("amount"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(4);
			cell.setCellValue(msgMap.get("balanceTot"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(5);
			cell.setCellValue(msgMap.get("transactionNo"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(6);
			cell.setCellValue(msgMap.get("authorizationCode"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(7);
			cell.setCellValue(msgMap.get("municipalityCode"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(8);
			cell.setCellValue(msgMap.get("meterValue") + "(" + msgMap.get("activeImport") + ")");
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(9);
			cell.setCellValue(msgMap.get("meterValue") + "(" + msgMap.get("activeExport") + ")");
			cell.setCellStyle(titleCellStyle);

			// Title End

			// Data
			dataCount = result.size();

			for (int i = 0; i < dataCount; i++) {
				resultMap = result.get(i);
				row = sheet.createRow(i + (dataGapsStartRow + 1));

				cell = row.createCell(0);
				cell.setCellValue(resultMap.get("lastTokenDate") == null ? "" : TimeLocaleUtil.getLocaleDateByMediumFormat(((String) resultMap.get("lastTokenDate")), lang, country));
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(1);
				cell.setCellValue(resultMap.get("usedCost") == null ? "" : cdf.format(resultMap.get("usedCost")));
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(2);
				cell.setCellValue(resultMap.get("usedConsumption") == null ? "" : mdf.format(resultMap.get("usedConsumption")));
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(3);
				cell.setCellValue(resultMap.get("chargedCredit") == null ? "" : cdf.format(resultMap.get("chargedCredit")));
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(4);
				cell.setCellValue(resultMap.get("balance") == null ? "" : cdf.format(resultMap.get("balance")));
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(5);
				cell.setCellValue(resultMap.get("lastTokenId") == null ? "" : resultMap.get("lastTokenId").toString());
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(6);
				cell.setCellValue(resultMap.get("authCode") == null ? "" : resultMap.get("authCode").toString());
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(7);
				cell.setCellValue(resultMap.get("municipalityCode") == null ? "" : resultMap.get("municipalityCode").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(8);
				cell.setCellValue(resultMap.get("activeImport") == null ? "" : resultMap.get("activeImport").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(9);
				cell.setCellValue(resultMap.get("activeExport") == null ? "" : resultMap.get("activeExport").toString());
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
			logger.error(e, e);
		} // End Try
	}

}
