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
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;

import com.aimir.model.device.EventAlertLogVO;

public class EventAlertLogMakeExcel {

	
	public EventAlertLogMakeExcel() {

	}

	/**
	 * @param result
	 * @param msgMap
	 * @param isLast
	 * @param filePath
	 * @param fileName
	 */
	public void writeReportExcel(List<EventAlertLogVO> result,
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
            
            HSSFFont redFont = workbook.createFont();
            redFont.setColor(HSSFColor.RED.index);
            
            HSSFFont yellowFont = workbook.createFont();
            yellowFont.setColor(HSSFColor.YELLOW.index);

			HSSFRow row = null;
			HSSFCell cell = null;
			HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
			HSSFCellStyle redDataCellStyle = ExcelUtil.getStyle(workbook, redFont, 1, 1, 1, 1, 0, 0, 0, 0, 0);
			HSSFCellStyle yellowDataCellStyle = ExcelUtil.getStyle(workbook, yellowFont, 1, 1, 1, 1, 0, 0, 0, 0, 0);
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);

			EventAlertLogVO resultMap = new EventAlertLogVO();
			String fileFullPath = new StringBuilder().append(filePath).append(
					File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int eventAlertLogStartRow = 3;
            int totalColumnCnt = 13;
			int dataCount = 0;
			int colcnt = 0;

			HSSFSheet sheet = workbook.createSheet(reportTitle);

			int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 10);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);		
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);

            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));

			// Title
			row = sheet.createRow(eventAlertLogStartRow);

			cell = row.createCell(0);
			cell.setCellValue(msgMap.get("No"));
            cell.setCellStyle(titleCellStyle);	
			
			cell = row.createCell(1);
			cell.setCellValue(msgMap.get("severity"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(2);
			cell.setCellValue(msgMap.get("Type"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(3);
			cell.setCellValue(msgMap.get("message"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(4);
			cell.setCellValue(msgMap.get("location"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(5);
			cell.setCellValue(msgMap.get("activatorId"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(6);
			cell.setCellValue(msgMap.get("activatorType"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(7);
			cell.setCellValue(msgMap.get("equipip"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(8);
			cell.setCellValue(msgMap.get("status"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(9);
			cell.setCellValue(msgMap.get("writetime"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(10);
			cell.setCellValue(msgMap.get("opentime"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(11);
			cell.setCellValue(msgMap.get("closetime"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(12);
			cell.setCellValue(msgMap.get("duration"));
            cell.setCellStyle(titleCellStyle);
	
			// Title End

			// Data

			dataCount = result.size();

			for (int i = 0; i < dataCount; i++) {
				resultMap = result.get(i);
				row = sheet.createRow(i + (eventAlertLogStartRow + 1));

				cell = row.createCell(0);
				cell.setCellValue(i + 1);
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(1);
				cell.setCellValue(resultMap.getSeverity().toString());

				if(resultMap.getSeverity().toString().equals("Critical")){

					cell.setCellStyle(redDataCellStyle);
				} /*else if(resultMap.getSeverity().toString().equals("Major")){

					font.setColor(HSSFColor.ORANGE.index);					
				}else if(resultMap.getSeverity().toString().equals("Minor")){

					font.setColor(HSSFColor.YELLOW.index);				
				}*/else if(resultMap.getSeverity().toString().equals("Warning")){

					cell.setCellStyle(yellowDataCellStyle);
				}/*else if(resultMap.getSeverity().toString().equals("Information")){

					font.setColor(HSSFColor.SKY_BLUE.index);
				}else if(resultMap.getSeverity().toString().equals("Normal")){
					font.setColor(HSSFColor.BLUE.index);
				}*/
				
				cell = row.createCell(2);
				cell.setCellValue(resultMap.getType().toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(3);
				cell.setCellValue(resultMap.getMessage().toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(4);
				cell.setCellValue(resultMap.getLocation().toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(5);
				cell.setCellValue(resultMap.getActivatorId().toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(6);
				cell.setCellValue(resultMap.getActivatorType().toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(7);
				cell.setCellValue(resultMap.getActivatorIp().toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(8);
				cell.setCellValue(resultMap.getStatus().toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(9);
				cell.setCellValue(resultMap.getWriteTime().toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(10);
				cell.setCellValue(resultMap.getOpenTime().toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(11);
				cell.setCellValue(resultMap.getCloseTime().toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(12);
				cell.setCellValue(resultMap.getDuration().toString());
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
	
    /**
     * @param workbook
     * @param top
     * @param bottom
     * @param left
     * @param right
     * @param grey
     * @param green
     * @param orange
     * @param align
     * @return HSSFCellStyle
     */
    private HSSFCellStyle getCellStyle(HSSFWorkbook workbook, HSSFFont font, int top, int bottom, int left, int right, int grey,
            int green, int orange, int align) {

        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);

        if (top == 1) {
            style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
            style.setTopBorderColor(HSSFColor.BLACK.index);
        }

        if (bottom == 1) {
            style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
            style.setBottomBorderColor(HSSFColor.BLACK.index);
        }

        if (left == 1) {
            style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
            style.setLeftBorderColor(HSSFColor.BLACK.index);
        }

        if (right == 1) {
            style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
            style.setRightBorderColor(HSSFColor.BLACK.index);
        }

        if (grey == 1) {
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            
        } else if (green == 1) {
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            style.setFillForegroundColor(IndexedColors.LIME.getIndex());
        } else if (orange == 1) {
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
//            style.setFillForegroundColor(IndexedColors.TAN.getIndex());
        }

        if (align == 1) {
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        } else if (align == 2) {
            style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        }

        return style;
    }


}
