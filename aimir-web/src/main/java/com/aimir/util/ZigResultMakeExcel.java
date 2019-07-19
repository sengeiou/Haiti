package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;

public class ZigResultMakeExcel {

	public ZigResultMakeExcel() {

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
			XSSFWorkbook workbook = new XSSFWorkbook();
			
			XSSFColor color_white = getXSSFColor("FFFFFF");
			XSSFColor color_black = getXSSFColor("000000");
			XSSFColor color_title = getXSSFColor("4E81BD");
			XSSFColor color_data1 = getXSSFColor("B8CCE4");
			XSSFColor color_data2 = getXSSFColor("DCE6F1");

            XSSFFont fontHeader = workbook.createFont();
            fontHeader.setFontHeightInPoints((short)11);
            fontHeader.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
            fontHeader.setFontName("맑은 고딕");
            fontHeader.setColor(IndexedColors.WHITE.getIndex());
            
            XSSFFont fontBody = workbook.createFont();
            fontBody.setFontHeightInPoints((short)11);
            fontBody.setFontName("맑은 고딕");
            fontBody.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
            fontBody.setColor(color_black);

			XSSFRow row = null;
			XSSFCell cell = null;
			XSSFCellStyle titleCellStyle = workbook.createCellStyle();
			titleCellStyle.setFont(fontHeader);
			titleCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			titleCellStyle.setFillForegroundColor(color_title);

			XSSFCellStyle data1CellStyle = workbook.createCellStyle();
			data1CellStyle.setFont(fontBody);
			data1CellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			data1CellStyle.setFillForegroundColor(color_data1);
			data1CellStyle.setBorderColor(BorderSide.BOTTOM, color_white);
			data1CellStyle.setBorderColor(BorderSide.TOP, color_white);
			data1CellStyle.setBorderColor(BorderSide.RIGHT, color_white);
			data1CellStyle.setBorderColor(BorderSide.LEFT, color_white);

			XSSFCellStyle data2CellStyle = workbook.createCellStyle();
			data2CellStyle.setFont(fontBody);
			data2CellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			data2CellStyle.setFillForegroundColor(color_data2);
			data2CellStyle.setBorderColor(BorderSide.BOTTOM, color_white);
			data2CellStyle.setBorderColor(BorderSide.TOP, color_white);
			data2CellStyle.setBorderColor(BorderSide.RIGHT, color_white);
			data2CellStyle.setBorderColor(BorderSide.LEFT, color_white);
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			String fileFullPath = new StringBuilder().append(filePath).append(
					File.separator).append(fileName).toString();
            final String reportTitle = "ZigResult";
			Integer dataCount = 0;

			XSSFSheet sheet = workbook.createSheet(reportTitle);
			
            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 22);
            sheet.setColumnWidth(colIdx++, 256 * 22);
            sheet.setColumnWidth(colIdx++, 256 * 22);          

            // Title
            row = sheet.createRow(0);

			int cellCnt = 0;

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("zigName"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("resultCnt"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("completeDate"));
            cell.setCellStyle(titleCellStyle);
			// Title End

			// Data

            List<Map<String, Object>> gridData = (List<Map<String, Object>>) result.get(0); 
			dataCount = (Integer) result.get(1);

			for (int i = 0; i < dataCount; i++) {
				resultMap = (Map<String, Object>) gridData.get(i);
				row = sheet.createRow(i + 1);  
				int cellCnt2 = 0;

				if((i%2) == 0) {
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("zigName")==null?"":resultMap.get("zigName").toString());
	            	cell.setCellStyle(data1CellStyle);
	            	
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("resultCnt")==null?"":resultMap.get("resultCnt").toString());
	            	cell.setCellStyle(data1CellStyle);
					
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("completeDate")==null?"":resultMap.get("completeDate").toString());
	            	cell.setCellStyle(data1CellStyle);
				} else {
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("zigName")==null?"":resultMap.get("zigName").toString());
	            	cell.setCellStyle(data2CellStyle);
	            	
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("resultCnt")==null?"":resultMap.get("resultCnt").toString());
	            	cell.setCellStyle(data2CellStyle);
					
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("completeDate")==null?"":resultMap.get("completeDate").toString());
	            	cell.setCellStyle(data2CellStyle);
				}

				


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
			e.printStackTrace();
		} // End Try
	}
	
	private static XSSFColor getXSSFColor(String RGB) {

		int red = Integer.parseInt(RGB.substring(0,2), 16);
		int green = Integer.parseInt(RGB.substring(2,4), 16);
		int blue = Integer.parseInt(RGB.substring(4,6), 16);

		 //add alpha to avoid bug 51236
		byte[] rgb = new byte[] { (byte) -1, (byte) red, (byte) green, (byte) blue };
		   
		return new XSSFColor(rgb);
	}

}
