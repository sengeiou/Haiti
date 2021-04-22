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

public class ChargeAndBalanceHistoryMakeExcel {

	private static Log logger = LogFactory.getLog(ChargeAndBalanceHistoryMakeExcel.class);

	public ChargeAndBalanceHistoryMakeExcel() {

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
			HSSFCellStyle dataCellStyle2 = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 2, 0);

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
			sheet.setColumnWidth(colIdx++, 256 * 15);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 15);
			sheet.setColumnWidth(colIdx++, 256 * 15);
			sheet.setColumnWidth(colIdx++, 256 * 15);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 30);

			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue(reportTitle);
			cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
			
//			row = sheet.createRow(1);
//			cell = row.createCell(1);
//			cell.setCellValue("Debt Amount : "+msgMap.get("amountDebt"));
//			cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
//			cell = row.createCell(3);
//			cell.setCellValue("Debt Count : "+msgMap.get("countDebt"));
//			cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
			
			sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt - 1)));

			// Title
			row = sheet.createRow(dataGapsStartRow);

			Integer cellCnt = 0;
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("type"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("contractNumber"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("date"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("beforebalance"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("balance"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("cost"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("usage"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("chargeAmount"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("canceledDate"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("paymenttype"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("monthlyUsage"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("monthlyCost"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("vat"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("serviceCharge"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("description"));
			cell.setCellStyle(titleCellStyle);

			// Title End

			// Data
			dataCount = result.size();

			for (int i = 0; i < dataCount; i++) {
				resultMap = result.get(i);
				row = sheet.createRow(i + (dataGapsStartRow + 1));
				
				cellCnt = 0;
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("TYPE") == null ? "" : resultMap.get("TYPE").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("CONTRACTID") == null ? "" : resultMap.get("CONTRACTID").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("DATETIME") == null ? "" : resultMap.get("DATETIME").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("BEFOREBALANCE") == null ? "" : resultMap.get("BEFOREBALANCE").toString());
				cell.setCellStyle(dataCellStyle2);
				
				cell = row.createCell(cellCnt++);								
				cell.setCellValue(resultMap.get("BALANCE") == null ? "" : resultMap.get("BALANCE").toString());
				cell.setCellStyle(dataCellStyle2);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("USAGETOTAL") == null ? "" : resultMap.get("USAGETOTAL").toString());
				cell.setCellStyle(dataCellStyle2);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("USAGECOST") == null ? "" : resultMap.get("USAGECOST").toString());
				cell.setCellStyle(dataCellStyle2);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("CHARGEDAMOUNT") == null ? "" : resultMap.get("CHARGEDAMOUNT").toString());
				cell.setCellStyle(dataCellStyle2);
				
//				cell = row.createCell(9);
//				cell.setCellValue(resultMap.get("CHARGEDTOKEN") == null ? "" : resultMap.get("CHARGEDTOKEN").toString());
//				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("CANCELDATE") == null ? "" : resultMap.get("CANCELDATE").toString());
				cell.setCellStyle(dataCellStyle);
				
//				cell = row.createCell(11);
//				cell.setCellValue(resultMap.get("CANCELTOKEN") == null ? "" : resultMap.get("CANCELTOKEN").toString());
//				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("PAYTYPE") == null ? "" : resultMap.get("PAYTYPE").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("MONTHLYUSAGE") == null ? "" : resultMap.get("MONTHLYUSAGE").toString());
				cell.setCellStyle(dataCellStyle2);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("MONTHLYCOST") == null ? "" : resultMap.get("MONTHLYCOST").toString());
				cell.setCellStyle(dataCellStyle2);
				
				cell = row.createCell(cellCnt++);								
				cell.setCellValue(resultMap.get("VAT") == null ? "" : resultMap.get("VAT").toString());
				cell.setCellStyle(dataCellStyle2);

				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("SERVICECHARGE") == null ? "" : resultMap.get("SERVICECHARGE").toString());
				cell.setCellStyle(dataCellStyle2);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("DESCR") == null ? "" : resultMap.get("DESCR").toString());
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
