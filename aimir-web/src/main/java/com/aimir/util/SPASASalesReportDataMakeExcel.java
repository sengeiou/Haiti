package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import com.aimir.model.system.Language;
import com.aimir.model.system.Supplier;
import com.ibm.icu.util.Calendar;

public class SPASASalesReportDataMakeExcel {
	HSSFFont fontTitle;
	HSSFFont fontTitleDate; 
	HSSFFont fontsubTitle;
	HSSFFont fontBody;
	
	HSSFCellStyle headerStyle;
	HSSFCellStyle bottomStyle;
	HSSFCellStyle bodyStyle_S_L;
	HSSFCellStyle bodyStyle_S_R;
	HSSFCellStyle bodyStyle_W_L;
	HSSFCellStyle bodyStyle_W_R;
	
	int DAILY_WIDTH = 14;
	int MONTHLY_WIDTH = 15;
	
	public SPASASalesReportDataMakeExcel() {
	}
	
	@SuppressWarnings("unchecked")
	public void writeReportExcel(Map<String, Object> data,
			String reportType,
			Supplier supplier,
    		String filePath, 
    		String fileName) {
		
    	String country = supplier.getCountry().getCode_2letter();
    	String lang = supplier.getLang().getCode_2letter();
    	Language language = supplier.getLang();
    	
	    try {
	    	HSSFWorkbook workbook = new HSSFWorkbook();

            fontTitle = commonFontStyle(workbook, (short)9, HSSFFont.BOLDWEIGHT_BOLD, "Microsoft Sans Serif");
            fontTitleDate = commonFontStyle(workbook, (short)9, HSSFFont.BOLDWEIGHT_BOLD, "Microsoft Sans Serif");
            fontsubTitle = commonFontStyle(workbook, (short)14, HSSFFont.BOLDWEIGHT_NORMAL, "Microsoft Sans Serif");
            fontBody = commonFontStyle(workbook, (short)8, HSSFFont.BOLDWEIGHT_NORMAL, "Tahoma");
            
            headerStyle = commonStyle(workbook, fontBody, HSSFCellStyle.ALIGN_CENTER, HSSFColor.GREY_25_PERCENT.index);
            bottomStyle = commonStyle(workbook, fontBody, HSSFCellStyle.ALIGN_RIGHT, HSSFColor.GREY_25_PERCENT.index);
            bodyStyle_S_L = commonStyle(workbook, fontBody, HSSFCellStyle.ALIGN_LEFT, HSSFColor.LIGHT_TURQUOISE.index);
            bodyStyle_S_R = commonStyle(workbook, fontBody, HSSFCellStyle.ALIGN_RIGHT, HSSFColor.LIGHT_TURQUOISE.index);
            bodyStyle_W_L = commonStyle(workbook, fontBody, HSSFCellStyle.ALIGN_LEFT, (short)-1);
            bodyStyle_W_R = commonStyle(workbook, fontBody, HSSFCellStyle.ALIGN_RIGHT, (short)-1);
            
            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            HSSFSheet sheet = workbook.createSheet();
            
            int colIdx = 0;
            																		   //daily						||monthly
            sheet.setColumnWidth(colIdx++, (256*10)/7); //charge date			||contractNumber
            sheet.setColumnWidth(colIdx++, (256*10)/7); //charge date			||contractNumber
            sheet.setColumnWidth(colIdx++, (256*65)/7); //charge date			||contractNumber
            sheet.setColumnWidth(colIdx++, (256*96)/7); //contract number	||customerName
            sheet.setColumnWidth(colIdx++, (256*75)/7); //contract number	||customerName
            sheet.setColumnWidth(colIdx++, (256*75)/7); //customer name 		||meterId
            sheet.setColumnWidth(colIdx++, (256*75)/7); //meterId					||municCode
            sheet.setColumnWidth(colIdx++, (256*75)/7); //vendorName					||vendorName
            sheet.setColumnWidth(colIdx++, (256*75)/7); //municipalityCode	||startDate
            sheet.setColumnWidth(colIdx++, (256*75)/7); //transactionNumber||startBalance
            sheet.setColumnWidth(colIdx++, (256*75)/7); //authCode				||endDate
            sheet.setColumnWidth(colIdx++, (256*75)/7); //chargedCredit		||endBalance
            sheet.setColumnWidth(colIdx++, (256*75)/7); //chargedArrears		||usedEnergy
            sheet.setColumnWidth(colIdx++, (256*75)/7); //balance					||usedCost
            sheet.setColumnWidth(colIdx++, (256*75)/7); //arrears					||chargedCredit
            
            if ( reportType.equals("monthly") ) {
            	sheet.setColumnWidth(colIdx++, (256*75)/7); //							||chargedArrears
            }
            SimpleDateFormat sb = new SimpleDateFormat("yyyyMMddhhmmss");
            String currTime = sb.format(Calendar.getInstance().getTime()).substring(0,8);
            String searchDate = StringUtil.nullToBlank(data.get("searchDate"));
            String startDate = StringUtil.nullToBlank(data.get("startDate"));
            String endDate = StringUtil.nullToBlank(data.get("endDate"));
            
            if(!searchDate.isEmpty() &&searchDate.length() >= 8) {
            	searchDate = searchDate.substring(0,8);
            	searchDate = TimeLocaleUtil.getLocaleDate(searchDate, lang, country);
            }
            
            if(!startDate.isEmpty() && !endDate.isEmpty() && (startDate.length() >= 8 || endDate.length() >= 8) ) {
            	startDate = startDate.substring(0, 8);
            	endDate = endDate.substring(0, 8);
            	startDate = TimeLocaleUtil.getLocaleDate(startDate, lang, country);
            	endDate = TimeLocaleUtil.getLocaleDate(endDate, lang, country);
            }
            
            String today = TimeLocaleUtil.getLocaleDate(currTime, lang, country);
            
            // Title
            printTitle(workbook, sheet, reportType, searchDate, startDate, endDate, today);           

            // Header
            printColumnHeader(sheet, headerStyle, reportType, language);            

            int rowNumber = 0;
            
            //Data
            List<Map<String, String>> dataList = (List<Map<String, String>>) data.get("data");
            
            rowNumber = printData(sheet, dataList, reportType);
            
            //파일 생성
            FileOutputStream fs = null;
            try {
                fs = new FileOutputStream(fileFullPath);
                workbook.write(fs);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fs != null) fs.close();
            }            
            
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
    private void printTitle(HSSFWorkbook workbook, 
    		HSSFSheet sheet, 
    		String reportType,
    		String searchDate, 
    		String startDate,
    		String endDate,
    		String today) {
    	String reportTitle = "";
    	int end = 0;
    	int start = 0;
    	if ( reportType.equalsIgnoreCase("daily")) {
    		reportTitle = "Daily Sales Report";
    		end = DAILY_WIDTH;
    		start = 11; 
    	} else if ( reportType.equalsIgnoreCase("monthly")) {
    		reportTitle = "Customer Usage Report";
    		end = MONTHLY_WIDTH;
    		start = 11;
    		searchDate = startDate + " - " + endDate;
    	}
    	
        HSSFRow row = sheet.createRow(0);
        row.setHeight((short)5);
        
        row = sheet.createRow(3);
        HSSFCell cell = row.createCell(start);
        cell.setCellValue("SPASA");
        cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, start, end -1 ));
		sheet.addMergedRegion(new CellRangeAddress(2, 2, start, end -1));
		sheet.addMergedRegion(new CellRangeAddress(3, 3, start, end -1));
		sheet.addMergedRegion(new CellRangeAddress(4, 4, start, end -1));
        
        row = sheet.createRow(4);
        cell = row.createCell(1);
        cell.setCellValue(reportTitle);
        cell.setCellStyle(ExcelUtil.getStyle(workbook, fontsubTitle, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(4, 5, 1, 5));
        
        row = sheet.createRow(5);
        cell = row.createCell(start);
        cell.setCellValue("Search Date : " + searchDate);
        cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitleDate, 0, 0, 0, 0, 0, 0, 0, 1, 0));
        sheet.addMergedRegion(new CellRangeAddress(5, 5, start, end -1));
        
        row = sheet.createRow(6);
        cell = row.createCell(start);
        cell.setCellValue("Today: " + today);
        cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitleDate, 0, 0, 0, 0, 0, 0, 0, 1, 0));
        sheet.addMergedRegion(new CellRangeAddress(6, 6, start, end -1));    	
    }
	
    private void printColumnHeader(HSSFSheet sheet, HSSFCellStyle style, String reportType, Language lang) {
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

        
    	if (reportType.equalsIgnoreCase("daily")) {
            for ( int i = 0; i < DAILY_WIDTH ; i ++) {
            	HSSFCell cell = row.createCell(i);
            	cell.setCellStyle(style);
            }
            
    		DailyProperty[] columns =DailyProperty.values();
    		for ( DailyProperty pr : columns ) {
    			HSSFCell cell = row.createCell(pr.getColumnIndex());
    			cell.setCellValue(getColumnHeader(pr.getColumnHeader(), prop));
    			cell.setCellStyle(style);
    		}
    	} else if (reportType.equalsIgnoreCase("monthly")) {
            for ( int i = 0; i < MONTHLY_WIDTH ; i ++) {
            	HSSFCell cell = row.createCell(i);
            	cell.setCellStyle(style);
            }
            
    		MonthlyProperty[] columns = MonthlyProperty.values();
    		for ( MonthlyProperty pr : columns ) {
    			HSSFCell cell = row.createCell(pr.getColumnIndex());
    			cell.setCellValue(getColumnHeader(pr.getColumnHeader(), prop));
    			cell.setCellStyle(style);
    		}    		
    	}
    	
		//date cell merge
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 2));
        //contractNumber cell merge
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 3, 4));
    }    
    
    private int printData(HSSFSheet sheet, List<Map<String, String>> data, String reportType) {
    	int rowNumber = 7;
   	
    	if ( reportType.equalsIgnoreCase("daily")) {
    		DailyProperty[] column = DailyProperty.values();
    		
    		for ( Map<String, String> rowData: data ) {
                ++rowNumber;
                HSSFRow row = sheet.createRow(rowNumber);
                for ( int i = 0; i < DAILY_WIDTH ; i ++) {
                	HSSFCell cell = row.createCell(i);
                	if(rowNumber%2 == 0) {
                		cell.setCellStyle(bodyStyle_W_L);
                	} else {
                		cell.setCellStyle(bodyStyle_S_L);
                	}
                }
                
        		for ( DailyProperty property :  column) {
                    
        			HSSFCell cell = row.createCell(property.getColumnIndex());
                    cell.setCellValue( rowData.get( property.getKey() ) );
                    if(rowNumber%2 == 0) {
                    	if (property.getAlign().equalsIgnoreCase("L")) {
                    		cell.setCellStyle(bodyStyle_W_L);
                    	} else {
                    		cell.setCellStyle(bodyStyle_W_R);
                    	}
                    } else {
                    	if (property.getAlign().equalsIgnoreCase("L")) {
                    		cell.setCellStyle(bodyStyle_S_L);
                    	} else {
                    		cell.setCellStyle(bodyStyle_S_R);
                    	}
                    }
        		}
        		//vending station cell merge
                sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 2));
        		//customer cell merge                
                sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 3, 4));
        	}

    	} else if ( reportType.equalsIgnoreCase("monthly") ) {
    		MonthlyProperty[] column = MonthlyProperty.values();
    		
    		for ( Map<String, String> rowData: data ) {
                ++rowNumber;
                HSSFRow row = sheet.createRow(rowNumber);
                for ( int i = 0; i < MONTHLY_WIDTH; i ++) {
                	HSSFCell cell = row.createCell(i);
                	if(rowNumber%2 == 0) {
                		cell.setCellStyle(bodyStyle_W_L);
                	} else {
                		cell.setCellStyle(bodyStyle_S_L);
                	}
                }
                
        		for ( MonthlyProperty property :  column) {
                    
        			HSSFCell cell = row.createCell(property.getColumnIndex());
                    cell.setCellValue( rowData.get( property.getKey() ) );
                    if(rowNumber%2 == 0) {
                    	if (property.getAlign().equalsIgnoreCase("L")) {
                    		cell.setCellStyle(bodyStyle_W_L);
                    	} else {
                    		cell.setCellStyle(bodyStyle_W_R);
                    	}
                    } else {
                    	if (property.getAlign().equalsIgnoreCase("L")) {
                    		cell.setCellStyle(bodyStyle_S_L);
                    	} else {
                    		cell.setCellStyle(bodyStyle_S_R);
                    	}
                    }
        		}
        		//vending station cell merge
                sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 2));
        		//customer cell merge                
                sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 3, 4));
        	}
    	}
    	return rowNumber;
    }    
    
    enum DailyProperty {
    	date("aimir.hems.prepayment.chargedate", 0, "lastTokenDate", "L", null), 
    	contractNumber("aimir.contractNumber", 3, "contractNumber", "L", null),
    	customerName("aimir.customername", 5, "customerName", "L", null),
    	meterId("aimir.meterid", 6, "mdsId", "L", null),
    	vendorName("aimir.vendor", 7, "vendorName", "L", null),
    	municipalityCode("aimir.prepayment.municipalityCode", 8, "municipalityCode", "L", null), 
    	transactionNumber("aimir.hems.prepayment.transactionNum", 9, "transactionNumber", "L", null),
    	authCode("aimir.prepayment.authCode", 10, "authCode", "L", null),
    	chargedCredit("aimir.deposit.chargecredit", 11, "chargedCredit", "R", null),
    	chargedArrears("aimir.prepayment.chargearrears", 12, "chargedArrears", "R", null),
    	balance("aimir.credit", 13, "balance", "R", null),
    	arrears("aimir.arrears", 14, "arrears", "R", null);
    	
    	// column header의 msg property key
    	String columnHeader;
    	// column index
    	int columnIndex;
    	// Map<String, String> data에서 특정 column의 data key
    	String key;
    	// column의 좌우 정렬
    	String align;
    	// 특정 column에 대한 total key 값
    	String totalKey;
    	
    	DailyProperty(String header, int columnIndex, String key, String align, String totalKey) {
    		this.columnHeader = header;
    		this.columnIndex  = columnIndex;
    		this.key = key;
    		this.align = align;
    		this.totalKey = totalKey;
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
    	
    	public String getTotalKey() {
    		return this.totalKey;
    	}
    }

    enum MonthlyProperty {
    	contractNumber(new String[] {"aimir.contractNumber"}, 0, "contractNumber", "L", null), 
    	customerName(new String[] {"aimir.customername"}, 3, "customerName", "L", null),
    	meterId(new String[]{"aimir.meterid"}, 5, "mdsId", "L", null),
    	vendorName(new String[]{"aimir.vendor"}, 6, "vendorName", "L", null),
    	municipalityCode(new String[]{"aimir.prepayment.municipalityCode"}, 7, "municCode", "L", null),
    	fromDate(new String[]{"aimir.time.from", "aimir.time.date"}, 8, "startDate", "L", null), 
    	openBalance(new String[]{"aimir.open", "aimir.balance"}, 9, "startBalance", "R", null),
    	toDate(new String[]{"aimir.time.to", "aimir.time.date"}, 10, "endDate", "L", null),
    	closeBalance(new String[] {"aimir.board.close", "aimir.balance"}, 11, "endBalance", "R", null),
    	usedEnergy(new String[] {"aimir.usage"}, 12, "usedEnergy", "R", null),
    	usedCost(new String[]{"aimir.used.cost"}, 13, "usedCost", "R", null),
    	chargedCredit(new String[]{"aimir.deposit.chargecredit"}, 14, "chargedCredit", "R", null),
    	chargedArrears(new String[]{"aimir.prepayment.chargearrears"}, 15, "chargedArrears", "R", null);
    	
    	// column header의 msg property key
    	String[] columnHeader;
    	// column index
    	int columnIndex;
    	// Map<String, String> data에서 특정 column의 data key
    	String key;
    	// column의 좌우 정렬
    	String align;
    	// 특정 column에 대한 total key 값
    	String totalKey;
    	
    	MonthlyProperty(String header[], int columnIndex, String key, String align, String totalKey) {
    		this.columnHeader = header;
    		this.columnIndex  = columnIndex;
    		this.key = key;
    		this.align = align;
    		this.totalKey = totalKey;
    	}
    	
    	public int getColumnIndex() {
    		return this.columnIndex;
    	}
    	
    	public String getColumnHeader() {
    		StringBuffer ret = new StringBuffer();
    		String[] header = this.columnHeader;
    		for (String word : header) {
    			ret.append(word).append(" ");
    		}
    		return ret.toString().trim();
    	}
    	
    	public String getKey() {
    		return this.key;
    	}
    	
    	public String getAlign() {
    		return this.align;
    	}
    	
    	public String getTotalKey() {
    		return this.totalKey;
    	}
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
    
    private HSSFFont commonFontStyle(HSSFWorkbook workbook, Short fontSize, Short bold, String fontName) {
    	HSSFFont commonFontStyle = workbook.createFont();
    	commonFontStyle.setFontHeightInPoints(fontSize);
    	commonFontStyle.setBoldweight(bold);
    	commonFontStyle.setFontName(fontName);
        
        return commonFontStyle;
    }    
    
    private String getColumnHeader(String key, Properties prop) {
    	StringBuffer ret = new StringBuffer();
    	String[] keys = key.split(" ");
    	
    	for ( String keyStr : keys ) {
    		ret.append(prop.getProperty(keyStr, "")).append(" ");
    	}
    	return ret.toString().trim();
    }
}
