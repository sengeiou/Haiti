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

import com.aimir.model.system.Supplier;

public class ZigResultDetailMakeExcel {

	public ZigResultDetailMakeExcel() {

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
			String fileName, Supplier supplier) {

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

			XSSFCellStyle dataCellStyleRed = workbook.createCellStyle();
			dataCellStyleRed.setFont(fontBody);
			dataCellStyleRed.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			dataCellStyleRed.setFillForegroundColor(XSSFFont.COLOR_RED);
			dataCellStyleRed.setBorderColor(BorderSide.BOTTOM, color_white);
			dataCellStyleRed.setBorderColor(BorderSide.TOP, color_white);
			dataCellStyleRed.setBorderColor(BorderSide.RIGHT, color_white);
			dataCellStyleRed.setBorderColor(BorderSide.LEFT, color_white);
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			String fileFullPath = new StringBuilder().append(filePath).append(
					File.separator).append(fileName).toString();
            final String reportTitle = "ZigResultDetail";
			long dataCount = 0;

			XSSFSheet sheet = workbook.createSheet(reportTitle);
			
            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 10);
            sheet.setColumnWidth(colIdx++, 256 * 22);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 22);
            sheet.setColumnWidth(colIdx++, 256 * 22);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 19);

            // Title
            row = sheet.createRow(0);

			int cellCnt = 0;
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("no"));
            cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("completeDate"));
            cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("testResult"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("meterSerial"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("modemSerail"));
            cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("hwVer"));
            cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("swVer"));
            cell.setCellStyle(titleCellStyle);
						
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("swBuild"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("failReason"));
            cell.setCellStyle(titleCellStyle);
            

			// Title End

            String code2letterByLang = supplier.getLang().getCode_2letter();
            String code2letterByCountry = supplier.getCountry().getCode_2letter();
			// Data
            List<Map<String,Object>> gridData = (List<Map<String, Object>>) result.get(1);
			dataCount = Long.parseLong(String.valueOf(result.get(0)));

			for (int i = 0; i < dataCount; i++) {
				resultMap = (Map<String, Object>) gridData.get(i);
				row = sheet.createRow(i + 1);  
				int cellCnt2 = 0;
				
				String testResult = resultMap.get("testResult")==null?"":resultMap.get("testResult").toString();

				if(testResult == "false" || testResult == "0") {
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(i + 1);				
	            	cell.setCellStyle(dataCellStyleRed);
	            	
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("completeDate")==null?"":TimeLocaleUtil.getLocaleDate(String.valueOf(resultMap.get("completeDate").toString()) , code2letterByLang, code2letterByCountry));
	            	cell.setCellStyle(dataCellStyleRed);

	            	cell = row.createCell(cellCnt2++);
            		testResult = msgMap.get("fail");
            		cell.setCellStyle(dataCellStyleRed);
					cell.setCellValue(testResult);
	            	
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("meterSerial")==null?"":resultMap.get("meterSerial").toString());
	            	cell.setCellStyle(dataCellStyleRed);
					
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("modemSerial")==null?"":resultMap.get("modemSerial").toString());
	            	cell.setCellStyle(dataCellStyleRed);
					
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("hwVer")==null?"":resultMap.get("hwVer").toString());
	            	cell.setCellStyle(dataCellStyleRed);

	            	cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("swVer")==null?"":resultMap.get("swVer").toString());
	            	cell.setCellStyle(dataCellStyleRed);
					
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("swBuild")==null?"":resultMap.get("swBuild").toString());
	            	cell.setCellStyle(dataCellStyleRed);

					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("failReason")==null?"":resultMap.get("failReason").toString());
	            	cell.setCellStyle(dataCellStyleRed);
				} else if((i%2) == 0) {
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(i + 1);				
	            	cell.setCellStyle(data1CellStyle);
	            	
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("completeDate")==null?"":TimeLocaleUtil.getLocaleDate(String.valueOf(resultMap.get("completeDate").toString()) , code2letterByLang, code2letterByCountry));
	            	cell.setCellStyle(data1CellStyle);

	            	cell = row.createCell(cellCnt2++);
	            	if(testResult == "true") {
	            		testResult = msgMap.get("success");
	            		cell.setCellStyle(data1CellStyle);
	            	} else if(testResult == "false") {
	            		testResult = msgMap.get("fail");
	            		cell.setCellStyle(dataCellStyleRed);
	            	} else {
	            		cell.setCellStyle(data1CellStyle);
	            	}
					cell.setCellValue(testResult);

					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("meterSerial")==null?"":resultMap.get("meterSerial").toString());
	            	cell.setCellStyle(data1CellStyle);
					
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("modemSerial")==null?"":resultMap.get("modemSerial").toString());
	            	cell.setCellStyle(data1CellStyle);
					
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("hwVer")==null?"":resultMap.get("hwVer").toString());
	            	cell.setCellStyle(data1CellStyle);

	            	cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("swVer")==null?"":resultMap.get("swVer").toString());
	            	cell.setCellStyle(data1CellStyle);
					
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("swBuild")==null?"":resultMap.get("swBuild").toString());
	            	cell.setCellStyle(data1CellStyle);

					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("failReason")==null?"":resultMap.get("failReason").toString());
	            	cell.setCellStyle(data1CellStyle);
	            	
				} else {
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(i + 1);				
	            	cell.setCellStyle(data2CellStyle);

					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("completeDate")==null?"":TimeLocaleUtil.getLocaleDate(String.valueOf(resultMap.get("completeDate").toString()) , code2letterByLang, code2letterByCountry));
	            	cell.setCellStyle(data2CellStyle);

	            	cell = row.createCell(cellCnt2++);
	            	if(testResult == "true") {
	            		cell.setCellStyle(data2CellStyle);
	            		testResult = msgMap.get("success");
	            	} else if(testResult == "false") {
	            		cell.setCellStyle(dataCellStyleRed);
	            		testResult = msgMap.get("fail");
	            	} else {
	            		cell.setCellStyle(data2CellStyle);
	            	}
					cell.setCellValue(testResult);
	            	
	            	
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("meterSerial")==null?"":resultMap.get("meterSerial").toString());
	            	cell.setCellStyle(data2CellStyle);
					
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("modemSerial")==null?"":resultMap.get("modemSerial").toString());
	            	cell.setCellStyle(data2CellStyle);
					
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("hwVer")==null?"":resultMap.get("hwVer").toString());
	            	cell.setCellStyle(data2CellStyle);

	            	cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("swVer")==null?"":resultMap.get("swVer").toString());
	            	cell.setCellStyle(data2CellStyle);
					
					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("swBuild")==null?"":resultMap.get("swBuild").toString());
	            	cell.setCellStyle(data2CellStyle);

					cell = row.createCell(cellCnt2++);
					cell.setCellValue(resultMap.get("failReason")==null?"":resultMap.get("failReason").toString());
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
