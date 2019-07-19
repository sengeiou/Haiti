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

public class MeterTimeDiffMaxMakeExcel {

	public MeterTimeDiffMaxMakeExcel() {

	}

	/**
	 * @param result
	 * @param msgMap
	 * @param isLast
	 * @param filePath
	 * @param fileName
	 */
	@SuppressWarnings("unchecked")
	public void writeReportExcel(List<Object> result,
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
			HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 0, 1, 0, 1, 0, 1, 0, 1, 0);
			HSSFCellStyle title2CellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			String fileFullPath = new StringBuilder().append(filePath).append(
					File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int timediffListStartRow = 2;
			int dataCount = 0;

			HSSFSheet sheet = workbook.createSheet(reportTitle);
			
            int colIdx = 0;
  
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
   
            //Title
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (8)));

            // header2
            row = sheet.createRow(timediffListStartRow);

			int cellCnt = 0;
	
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("no"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("mcuSysId"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("meterMdsID"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("customerName"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("contractNumber"));
            cell.setCellStyle(title2CellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("locName"));
            cell.setCellStyle(title2CellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("lastLinkTime"));
            cell.setCellStyle(title2CellStyle);

            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("timeDiff"));
            cell.setCellStyle(title2CellStyle);

			// Title End

			// Data

			dataCount = result.size();

			for (int i = 0; i < dataCount; i++) {
				resultMap = (Map<String, Object>) result.get(i);
				row = sheet.createRow(i + (timediffListStartRow + 1));  
				int cellCnt2 = 0;
			            	
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("no")==null?"":resultMap.get("no").toString());
            	cell.setCellStyle(dataCellStyle);
            	
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("mcuSysId")==null?"":String.valueOf(resultMap.get("mcuSysId")));
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("meterMdsID")==null?"":resultMap.get("meterMdsID").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("customerName")==null?"":resultMap.get("customerName").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("contractNumber")==null?"":resultMap.get("contractNumber").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("locName")==null?"":resultMap.get("locName").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("lastLinkTime")==null?"":resultMap.get("lastLinkTime").toString());
            	cell.setCellStyle(dataCellStyle);

				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("timeDiff")==null?"":resultMap.get("timeDiff").toString());
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
