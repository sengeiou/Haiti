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

public class PrepaymentBalanceMakeExcel {

	private static Log logger = LogFactory.getLog(PrepaymentBalanceMakeExcel.class);

	public PrepaymentBalanceMakeExcel() {

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
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 30);
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 23);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 20);
			sheet.setColumnWidth(colIdx++, 256 * 100);

			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue(reportTitle);
			cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
			sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt - 1)));

			// Title
			row = sheet.createRow(dataGapsStartRow);

			Integer cellCnt = 0;
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("customerNumber"));
			cell.setCellStyle(titleCellStyle);
			
//			cell = row.createCell(cellCnt++);
//			cell.setCellValue(msgMap.get("accountNo"));
//			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("customername"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("celluarphone"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("lastchargedate"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("currentbalance"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("meterid"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("gs1"));
			cell.setCellStyle(titleCellStyle);
			
//			cell = row.createCell(7);
//			cell.setCellValue(msgMap.get("stsnumber"));
//			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("supplyType"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("tariffType"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("contractStatus"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("meterStatus"));
			cell.setCellStyle(titleCellStyle);
			
//			cell = row.createCell(cellCnt++);
//			cell.setCellValue(msgMap.get("lastreaddate"));
//			cell.setCellStyle(titleCellStyle);	
			
//			cell = row.createCell(12);
//			cell.setCellValue(msgMap.get("validperiod"));
//			cell.setCellStyle(titleCellStyle);	
			
//			cell = row.createCell(cellCnt++);
//			cell.setCellValue(msgMap.get("address"));
//			cell.setCellStyle(titleCellStyle);
			// Title End

			// Data
			dataCount = result.size();

			for (int i = 0; i < dataCount; i++) {
				resultMap = result.get(i);
				row = sheet.createRow(i + (dataGapsStartRow + 1));
				
				cellCnt = 0;
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("customerNumber") == null ? "" : resultMap.get("customerNumber").toString());
				cell.setCellStyle(dataCellStyle);
				
//				cell = row.createCell(cellCnt++);
//				cell.setCellValue(resultMap.get("SPN") == null ? "" : resultMap.get("SPN").toString());
//				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("customerName") == null ? "" : resultMap.get("customerName").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("mobileNo") == null ? "" : resultMap.get("mobileNo").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt++);								
				cell.setCellValue(resultMap.get("lastTokenDate") == null ? "" : resultMap.get("lastTokenDate").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("currentCredit") == null ? "" : resultMap.get("currentCredit").toString());
				cell.setCellStyle(dataCellStyle2);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("mdsId") == null ? "" : resultMap.get("mdsId").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("gs1") == null ? "" : resultMap.get("gs1").toString());
				cell.setCellStyle(dataCellStyle);
				
//				cell = row.createCell(7);
//				cell.setCellValue(resultMap.get("ihdId") == null ? "" : resultMap.get("ihdId").toString());
//				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("serviceTypeName") == null ? "" : resultMap.get("serviceTypeName").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("tariffTypeName") == null ? "" : resultMap.get("tariffTypeName").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("statusName") == null ? "" : resultMap.get("statusName").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue(resultMap.get("meterStatus") == null ? "" : resultMap.get("meterStatus").toString());
				cell.setCellStyle(dataCellStyle);
				
//				cell = row.createCell(cellCnt++);
//				cell.setCellValue(resultMap.get("lastReadDate") == null ? "" : resultMap.get("lastReadDate").toString());
//				cell.setCellStyle(dataCellStyle);
				
//				cell = row.createCell(12);
//				cell.setCellValue(resultMap.get("emergencyCreditMaxDate") == null ? "" : resultMap.get("emergencyCreditMaxDate").toString());
//				cell.setCellStyle(dataCellStyle);
				
//				cell = row.createCell(cellCnt++);
//				cell.setCellValue(resultMap.get("address") == null ? "" : resultMap.get("address").toString());
//				cell.setCellStyle(dataCellStyle);
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
