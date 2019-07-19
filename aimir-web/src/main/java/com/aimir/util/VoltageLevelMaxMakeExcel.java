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

public class VoltageLevelMaxMakeExcel {

	public VoltageLevelMaxMakeExcel() {

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
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);

			Map<String, String> resultMap = new HashMap<String, String>();
			String fileFullPath = new StringBuilder().append(filePath).append(
					File.separator).append(fileName).toString();
            final String reportTitle = "Voltage Levels List";
            int voltagelevelListStartRow = 4;
            int totalColumnCnt = result.size();
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
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (21)));
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
            cell.setCellValue(msgMap.get("headerA1"));
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 4, 6));
            cell.setCellStyle(title2CellStyle);
            
            
            cell = row.createCell(7); 
            cell.setCellValue(msgMap.get("headerA2"));
            cell.setCellStyle(title2CellStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 7, 9));
            
            cell = row.createCell(10); 
            cell.setCellValue(msgMap.get("headerB1"));
            cell.setCellStyle(title2CellStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 10, 12));
            
            cell = row.createCell(13); 
            cell.setCellValue(msgMap.get("headerB2"));
            cell.setCellStyle(title2CellStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 13, 15));
            
            cell = row.createCell(16); 
            cell.setCellValue(msgMap.get("headerC1"));
            cell.setCellStyle(title2CellStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 16, 18));
            
            cell = row.createCell(19); 
            cell.setCellValue(msgMap.get("headerC2"));
            cell.setCellStyle(title2CellStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 19, 21));
            // header2
            row = sheet.createRow(voltagelevelListStartRow);

			int cellCnt = 0;
	
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("yyyymmdd"));
            cell.setCellStyle(title3CellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("deviceType"));
            cell.setCellStyle(title3CellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("deviceId"));
            cell.setCellStyle(title3CellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("customerName"));
            cell.setCellStyle(title3CellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("volA_min"));
            cell.setCellStyle(title4CellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("volA_max"));
            cell.setCellStyle(title4CellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("volA_avg"));
            cell.setCellStyle(title4CellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("vol_angleA_min"));
            cell.setCellStyle(title4CellStyle);
		    
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("vol_angleA_max"));
            cell.setCellStyle(title4CellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("vol_angleA_avg"));
            cell.setCellStyle(title4CellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("volB_min"));
            cell.setCellStyle(title4CellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("volB_max"));
            cell.setCellStyle(title4CellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("volB_avg"));
            cell.setCellStyle(title4CellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("vol_angleB_min"));
            cell.setCellStyle(title4CellStyle);
		    
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("vol_angleB_max"));
            cell.setCellStyle(title4CellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("vol_angleB_avg"));
            cell.setCellStyle(title4CellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("volC_min"));
            cell.setCellStyle(title4CellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("volC_max"));
            cell.setCellStyle(title4CellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("volC_avg"));
            cell.setCellStyle(title4CellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("vol_angleC_min"));
            cell.setCellStyle(title4CellStyle);
		    
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("vol_angleC_max"));
            cell.setCellStyle(title4CellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("vol_angleC_avg"));
            cell.setCellStyle(title4CellStyle);
			// Title End

			// Data

			dataCount = result.size();

			for (int i = 0; i < dataCount; i++) {
				resultMap = (Map<String, String>) result.get(i);
				row = sheet.createRow(i + (voltagelevelListStartRow + 1));  
				int cellCnt2 = 0;
			            	
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("yyyymmdd")==null?"":resultMap.get("yyyymmdd").toString());
            	cell.setCellStyle(dataCellStyle);
            	
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("deviceType")==null?"":String.valueOf(resultMap.get("deviceType")));
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("deviceId")==null?"":resultMap.get("deviceId").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("customerName")==null?"":resultMap.get("customerName").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("volA_min")==null?"":resultMap.get("volA_min").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("volA_max")==null?"":resultMap.get("volA_max").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("volA_avg")==null?"":resultMap.get("volA_avg").toString());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("vol_angleA_min")==null?"":resultMap.get("vol_angleA_min").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("vol_angleA_max")==null?"":resultMap.get("vol_angleA_max").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("vol_angleA_avg")==null?"":resultMap.get("vol_angleA_avg").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("volB_min")==null?"":resultMap.get("volB_min").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("volB_max")==null?"":resultMap.get("volB_max").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("volB_avg")==null?"":resultMap.get("volB_avg").toString());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("vol_angleB_min")==null?"":resultMap.get("vol_angleB_min").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("vol_angleB_max")==null?"":resultMap.get("vol_angleB_max").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("vol_angleB_avg")==null?"":resultMap.get("vol_angleB_avg").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("volC_min")==null?"":resultMap.get("volC_min").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("volC_max")==null?"":resultMap.get("volC_max").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("volC_avg")==null?"":resultMap.get("volC_avg").toString());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("vol_angleC_min")==null?"":resultMap.get("vol_angleC_min").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("vol_angleC_max")==null?"":resultMap.get("vol_angleC_max").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("vol_angleC_avg")==null?"":resultMap.get("vol_angleC_avg").toString());
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
