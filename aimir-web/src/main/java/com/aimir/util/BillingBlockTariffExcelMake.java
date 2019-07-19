package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;

import com.aimir.model.system.Language;
import com.aimir.model.system.Supplier;

public class BillingBlockTariffExcelMake {

	private static Log logger = LogFactory.getLog(BillingBlockTariffExcelMake.class);

	HSSFFont fontTitle;
	HSSFFont fontTitleDate; 
	HSSFFont fontsubTitle;
	HSSFFont fontBody;
	
	HSSFCellStyle headerStyle;
	HSSFCellStyle bodyStyle_L;
	HSSFCellStyle bodyStyle_R;

	int WIDTH = 9;

	/**
	 * @param result
	 * @param msgMap
	 * @param isLast
	 * @param filePath
	 * @param fileName
	 */
	public void writeReportExcel(List<Map<String, Object>> result, Map<String, String> msgMap, String filePath, String fileName, Supplier supplier) {
    	String country = supplier.getCountry().getCode_2letter();
    	String lang = supplier.getLang().getCode_2letter();
    	Language language = supplier.getLang();
    	
	    try {
	    	HSSFWorkbook workbook = new HSSFWorkbook();

            fontTitle = commonFontStyle(workbook, (short)9, HSSFFont.BOLDWEIGHT_BOLD, "Microsoft Sans Serif");
            fontTitleDate = commonFontStyle(workbook, (short)9, HSSFFont.BOLDWEIGHT_BOLD, "Microsoft Sans Serif");
            fontsubTitle = commonFontStyle(workbook, (short)14, HSSFFont.BOLDWEIGHT_NORMAL, "Microsoft Sans Serif");
            fontBody = commonFontStyle(workbook, (short)8, HSSFFont.BOLDWEIGHT_NORMAL, "Tahoma");
            
            headerStyle = commonStyle(workbook, fontBody, HSSFCellStyle.ALIGN_CENTER, IndexedColors.LIME.getIndex());
            bodyStyle_L = commonStyle(workbook, fontBody, HSSFCellStyle.ALIGN_LEFT, (short)-1);
            bodyStyle_R = commonStyle(workbook, fontBody, HSSFCellStyle.ALIGN_RIGHT, (short)-1);
            
            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            HSSFSheet sheet = workbook.createSheet();
            
            sheet.getPrintSetup().setLandscape(true);
            sheet.getPrintSetup().setFitWidth(HSSFPrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
            
			int colIdx = 0;
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 25);
			sheet.setColumnWidth(colIdx++, 256 * 25);

            String startDate = StringUtil.nullToBlank(msgMap.get("startDate")).substring(0,8);
            String endDate = StringUtil.nullToBlank(msgMap.get("endDate")).substring(0, 8);
            String contractNumber = StringUtil.nullToBlank(msgMap.get("contractNumber"));
            String contractNoMsg = StringUtil.nullToBlank(msgMap.get("aimir.contractNo"));
            
            if(startDate.length() > 8) {
            	startDate = startDate.substring(0,8);
            }
            if(endDate.length() > 8) {
            	endDate = endDate.substring(0,8);
            }
            
            String dateFrom = TimeLocaleUtil.getLocaleDate(startDate, lang, country);
            String dateTo = TimeLocaleUtil.getLocaleDate(endDate, lang, country);
			
			// Title
            String reportTitle = msgMap.get("title");
            printTitle(workbook, sheet, dateFrom, dateTo, reportTitle, contractNoMsg, contractNumber);
            
            printColumnHeader(sheet, headerStyle, language);
            
            //Data
            printData(sheet, result);

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
	
    interface ReportProperty {
    	public int getColumnIndex();
    	public String getColumnHeader();
    	public String getKey();
    	public String getAlign();
    }
    
    enum HeaderProperty implements ReportProperty {
    	billingDate("aimir.paydate", 0, "writeDate", "L"), 
    	meteringTime("aimir.meteringtime", 1, "lpTime", "L"),
    	accUsage("aimir.accu.usage", 2, "accUsage", "R"),
    	accBill("aimir.accu.bill", 3, "accBill", "R"),
    	usage("aimir.usage", 4, "usage", "R"),
    	bill("aimir.bill", 5, "bill", "R"),
    	balance("aimir.balance", 6, "balance", "R"), 
    	activeEnergyImport("aimir.meter.value", 7, "activeImport", "R"),
    	activeEnergyExport("aimir.meter.value", 8, "activeExport", "R");

    	
    	// column header의 msg property key
    	String columnHeader;
    	// column index
    	int columnIndex;
    	// Map<String, String> data에서 특정 column의 data key
    	String key;
    	// column의 좌우 정렬
    	String align;
    	
    	HeaderProperty(String header, int columnIndex, String key, String align) {
    		this.columnHeader = header;
    		this.columnIndex  = columnIndex;
    		this.key = key;
    		this.align = align;
    	}
    	
    	public int getColumnIndex() {
    		return this.columnIndex;
    	}
    	
    	public String getColumnHeader() {
    		return this.columnHeader;
    	}
    	
    	public String getKey() {
    		return this.key;
    	}
    	
    	public String getAlign() {
    		return this.align;
    	}
    	
    }
    
    private void printTitle(HSSFWorkbook workbook, 
    		HSSFSheet sheet, 
    		String dateFrom, 
    		String dateTo,
    		String reportTitle,
    		String contractNoMsg,
    		String contractNumber) {
    	
    	int start = 0;	// 우측의 타이틀 및 날짜 row merge시  좌측좌표 
    	int end = WIDTH;		// 우측의 타이틀 및 날짜 row merge시  우측좌표
    	 
        HSSFRow row = sheet.createRow(0);
        row.setHeight((short)5);
        
        row = sheet.createRow(3);
        HSSFCell cell = row.createCell(start);
        cell.setCellValue(reportTitle);
        cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, start, end -1));
		sheet.addMergedRegion(new CellRangeAddress(2, 2, start, end -1));
		sheet.addMergedRegion(new CellRangeAddress(3, 3, start, end -1));
		sheet.addMergedRegion(new CellRangeAddress(4, 4, start, end -1));
        
        
        row = sheet.createRow(5);
        cell = row.createCell(start);
        cell.setCellValue(contractNoMsg + " : " + contractNumber);
        cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitleDate, 0, 0, 0, 0, 0, 0, 0, 1, 0));
        sheet.addMergedRegion(new CellRangeAddress(5, 5, start, end -1)); 
        
        row = sheet.createRow(6);
        cell = row.createCell(start);
        cell.setCellValue("Date From : " + dateFrom + "   To : " + dateTo);
        cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitleDate, 0, 0, 0, 0, 0, 0, 0, 1, 0));
        sheet.addMergedRegion(new CellRangeAddress(6, 6, start, end -1));

    }
    
    private void printColumnHeader(HSSFSheet sheet, HSSFCellStyle style, Language lang) {
		Properties prop = new Properties();
        if(lang.getName().equalsIgnoreCase(Locale.KOREAN.toString())){
	        try {
	        	prop.load(getClass().getClassLoader().getResourceAsStream(
	           "message_ko.properties"));
	        } catch (IOException e) {
	         e.printStackTrace();
	        }
        }else{
        	try {
	        	prop.load(getClass().getClassLoader().getResourceAsStream(
	           "message_en.properties"));
	        } catch (IOException e) {
	         e.printStackTrace();
	        }
        }    	
    	
        HSSFRow row = sheet.createRow(7);

        int width = 0;
        ReportProperty[] columns = null;

		width = WIDTH;

        columns = HeaderProperty.values();
		
        for ( int i = 0; i < width ; i ++) {
        	HSSFCell cell = row.createCell(i);
        	cell.setCellStyle(style);
        }    		
        
		for ( ReportProperty pr : columns ) {
			HSSFCell cell = row.createCell(pr.getColumnIndex());
			cell.setCellValue(prop.getProperty(pr.getColumnHeader(), ""));
			cell.setCellStyle(style);    			
		}    	
    }
    
    private void printData(HSSFSheet sheet, List<Map<String, Object>> data) {
    	int rowNumber = 7;
    	ReportProperty[] column = null;
    	int width = 0;
    	
		column = HeaderProperty.values();
		width= WIDTH;
    	
		for ( Map<String, Object> rowData: data) {
            ++rowNumber;
            HSSFRow row = sheet.createRow(rowNumber);
            for ( int i = 0; i < width ; i ++) {
            	HSSFCell cell = row.createCell(i);
        		cell.setCellStyle(bodyStyle_L);
            }
            
    		for ( ReportProperty property :  column) {
                
    			HSSFCell cell = row.createCell(property.getColumnIndex());
                cell.setCellValue( rowData.get(property.getKey()).toString());
            	if (property.getAlign().equalsIgnoreCase("L")) {
            		cell.setCellStyle(bodyStyle_L);
            	} else {
            		cell.setCellStyle(bodyStyle_R);
            	}
    		}
    	}    	
    }
    
    private HSSFFont commonFontStyle(HSSFWorkbook workbook, Short fontSize, Short bold, String fontName) {
    	HSSFFont commonFontStyle = workbook.createFont();
    	commonFontStyle.setFontHeightInPoints(fontSize);
    	commonFontStyle.setBoldweight(bold);
    	commonFontStyle.setFontName(fontName);
        
        return commonFontStyle;
    }
    
    private HSSFCellStyle commonStyle(HSSFWorkbook workbook, HSSFFont fontStyle, short align, short foreGroundColor) {
    	HSSFCellStyle commonStyle = workbook.createCellStyle();
    	commonStyle.setFont(fontStyle);
    	commonStyle.setAlignment(align);
    	commonStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
    	commonStyle.setTopBorderColor(HSSFColor.GREY_40_PERCENT.index);
    	commonStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    	commonStyle.setBottomBorderColor(HSSFColor.GREY_40_PERCENT.index);
        commonStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        commonStyle.setLeftBorderColor(HSSFColor.GREY_40_PERCENT.index);
        commonStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        commonStyle.setRightBorderColor(HSSFColor.GREY_40_PERCENT.index);
        if(foreGroundColor != -1) {
        	commonStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            commonStyle.setFillForegroundColor(foreGroundColor);
        }
        return commonStyle;
    }

}
