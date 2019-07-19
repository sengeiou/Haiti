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

public class PowerInstrumentMaxMakeExcel {

	public PowerInstrumentMaxMakeExcel() {

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
			HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 0, 0, 0, 1, 0, 1, 0, 1, 0);
			HSSFCellStyle title2CellStyle = ExcelUtil.getStyle(workbook, fontHeader, 0, 0, 1, 1, 0, 1, 0, 1, 0);
			HSSFCellStyle title3CellStyle = ExcelUtil.getStyle(workbook, fontHeader, 0, 1, 0, 1, 0, 1, 0, 1, 0);
			HSSFCellStyle title4CellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 1, 0);
			HSSFCellStyle data2CellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);

			Map<String, String> resultMap = new HashMap<String, String>();
			String fileFullPath = new StringBuilder().append(filePath).append(
					File.separator).append(fileName).toString();
            final String reportTitle = "PowerQuality_PowerInstrumentExcel";
            int powerInstrumnetListStartRow = 4;
//            int totalColumnCnt = result.size();
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
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (15)));
            //header1
            row = sheet.createRow(3);
            cell = row.createCell(0); 
            cell.setCellValue("");
            cell.setCellStyle(titleCellStyle);
          
            cell = row.createCell(1); 
            cell.setCellValue("");
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(2); 
            cell.setCellValue("");
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(3); 
            cell.setCellValue("");
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(4); 
            cell.setCellValue("");
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(5); 
            cell.setCellValue("");
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(6); 
            cell.setCellValue(msgMap.get("voltage"));
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 6, 8));
            cell.setCellStyle(title2CellStyle);
            
            
            cell = row.createCell(9); 
            cell.setCellValue(msgMap.get("current"));
            cell.setCellStyle(title2CellStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 9, 11));
            
            cell = row.createCell(12); 
            cell.setCellValue(msgMap.get("linevoltage"));
            cell.setCellStyle(title2CellStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 12, 14));
            
        
            // header2
            row = sheet.createRow(powerInstrumnetListStartRow);

			int cellCnt = 0;
	
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("no"));
            cell.setCellStyle(title3CellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("deviceType"));
            cell.setCellStyle(title3CellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("deviceId"));
            cell.setCellStyle(title3CellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("lastReadDate"));
            cell.setCellStyle(title3CellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("contractId"));
            cell.setCellStyle(title3CellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("customerName"));
            cell.setCellStyle(title3CellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("A"));
            cell.setCellStyle(title4CellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("B"));
            cell.setCellStyle(title4CellStyle);
		    
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("C"));
            cell.setCellStyle(title4CellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("A"));
            cell.setCellStyle(title4CellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("B"));
            cell.setCellStyle(title4CellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("C"));
            cell.setCellStyle(title4CellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("AB"));
            cell.setCellStyle(title4CellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("CA"));
            cell.setCellStyle(title4CellStyle);
		    
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("BC"));
            cell.setCellStyle(title4CellStyle);

			// Title End

			// Data

			dataCount = result.size();

			for (int i = 0; i < dataCount; i++) {
				resultMap = (Map<String, String>) result.get(i);
				row = sheet.createRow(i + (powerInstrumnetListStartRow + 1));  
				int cellCnt2 = 0;
			            	
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(i+1);
            	cell.setCellStyle(dataCellStyle);
            	
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("deviceType")==null?"":String.valueOf(resultMap.get("deviceType")));
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("deviceId")==null?"":resultMap.get("deviceId").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("lastReadDate")==null?"":resultMap.get("lastReadDate").toString());
            	cell.setCellStyle(data2CellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("contractId")==null?"":resultMap.get("contractId").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("customerName")==null?"":resultMap.get("customerName").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("voltA")==null?"":resultMap.get("voltA").toString());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("voltB")==null?"":resultMap.get("voltB").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("voltC")==null?"":resultMap.get("voltC").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("currA")==null?"":resultMap.get("currA").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("currB")==null?"":resultMap.get("currB").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("currC")==null?"":resultMap.get("currC").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("line_AB")==null?"":resultMap.get("line_AB").toString());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("line_CA")==null?"":resultMap.get("line_CA").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("line_BC")==null?"":resultMap.get("line_BC").toString());
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
