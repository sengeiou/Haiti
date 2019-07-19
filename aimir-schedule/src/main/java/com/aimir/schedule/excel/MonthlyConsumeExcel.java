package com.aimir.schedule.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.aimir.schedule.excel.ExcelUtil.MonthEnum;
import com.aimir.schedule.excel.ExcelUtil.PropertyMap;
import com.aimir.schedule.excel.ExcelUtil.ReportColumn;
import com.aimir.schedule.excel.ExcelUtil.ReportType;
import com.aimir.schedule.excel.ExcelUtil.TariffType;
import com.aimir.schedule.excel.MonthlyConsumeData.ConsumeLevel;

public class MonthlyConsumeExcel {
	private static final String FILE_PATH = "/tmp/monthly";
	
	public static void makeMonthlyReportExcel(String title,
			Map<String, Map<String, Object>> result,
			String fileName,
			String yyyymm,
			String tariffName) {
		String file = fileName + ".xls";
		String year = yyyymm.substring(0, 4);
		String month = yyyymm.substring(4);
		TariffType tariff = TariffType.getTarrifTypeByName(tariffName);
		
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

            //style 
            HSSFCellStyle titleStyle = ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0);
            titleStyle.setWrapText(false);
            HSSFCellStyle captionStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);            
            captionStyle.setWrapText(true);
            
            HSSFRow row = null;
            HSSFCell cell = null;

            String fileFullPath = new StringBuilder().append(FILE_PATH).append(File.separator).append(file).toString();
            String reportTitle = title;
            int dataStartRow = 3;
            PropertyMap[] columnProp = ReportColumn.getProperties(ReportType.Sales, tariff);
            int totalColumnCnt = columnProp.length;
            
            HSSFSheet sheet = workbook.createSheet();
            //workbook.setPrintArea(0, 0, totalColumnCnt, 0, 20);
            
            sheet.getPrintSetup().setLandscape(true);
            sheet.getPrintSetup().setFitWidth(HSSFPrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setScale((short) 48);
            
            int colIdx = 0;
            for ( int i = 0 ; i < totalColumnCnt ; i++ ) {
            	sheet.setColumnWidth(colIdx++, 256 * 19);
            }  
            
            // title
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));
            
            // Description
            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue("Tariff Class: " + tariffName);
            
            cell = row.createCell(4);
            cell.setCellValue("YEAR:  " + year);
            
            cell = row.createCell(7);
            cell.setCellValue("MONTH: " + MonthEnum.getMonthName(month));
            

            
            // Caption row
            row = sheet.createRow(dataStartRow);
            int columnIndex = 0;            
            
            ExcelUtil.createTitleRow(captionStyle, row, columnIndex, columnProp);
            
            //Caption End
            
            int rowNumber = 0;
            
            //Data
            int i = 0;
            for( ConsumeLevel level : ConsumeLevel.values() ) {
                Map<String, Object> record = result.get( level.getName() );
                
                if ( record == null ) {
                	record = ExcelUtil.createZeroRecord(level);
                }
                
                rowNumber = i+ (dataStartRow + 1);
                row = sheet.createRow(rowNumber);      
                columnIndex = 0;                
                record.put(PropertyMap.cons.getProp(), level.getName());
                ExcelUtil.createDataRow(record, workbook, row, columnIndex, columnProp);
                i++;
            }
            
            // total
            Map<String, Object> record = result.get("total");
            record.put(PropertyMap.cons.getProp(), "Total");
            row = sheet.createRow(rowNumber + 1);
            columnIndex = 0;
            ExcelUtil.createDataRow(record, workbook, row, columnIndex, columnProp);
            //End Data
            
            //파일 생성
            FileOutputStream fs = null;
            File desti = new File(FILE_PATH);
            try {
                File excelFile = new File(fileFullPath);
                if ( excelFile.exists() ) {
                	excelFile.delete();
                } else if ( !desti.exists() ) {
                	desti.mkdirs();
                }
                fs = new FileOutputStream(fileFullPath);

                workbook.write(fs);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fs != null) fs.close();
            }
            
		} catch (Exception e) {
		    e.printStackTrace();
		} //End Try		
	}
}
