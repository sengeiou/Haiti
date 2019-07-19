package com.aimir.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.owasp.esapi.ESAPI;

import com.aimir.model.system.Language;
import com.aimir.model.system.Supplier;
import com.ibm.icu.util.Calendar;

public class ArrearsInfoMakeExcel {

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
	
	int WIDTH=5;
	int DEBTWIDTH=10;
    public ArrearsInfoMakeExcel() {

    }

    /**
     * @param result
     * @param msgMap
     * @param isLast
     * @param filePath
     * @param fileName
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    public void writeReportExcel(Map<String,Object> result, Supplier supplier, String filePath,
            String fileName, String logoImg) {
    	
    	String country = supplier.getCountry().getCode_2letter();
    	String lang = supplier.getLang().getCode_2letter();
    	Language language = supplier.getLang();
    	String supplierDesc = StringUtil.nullToBlank(supplier.getDescr());
	    try {
	    	Boolean withDebt = result.get("withDebt") == null ? false : (Boolean)result.get("withDebt");
	    	
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
            
            sheet.getPrintSetup().setLandscape(true);
            sheet.getPrintSetup().setFitWidth(HSSFPrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
            
            HSSFPatriarch parch = sheet.createDrawingPatriarch();
            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, (short)2, 1, (short)3, 4); // 이미지 크기조절
            
            // anchor.setAnchorType(2);
            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
            
            HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
            parch.createPicture(anchor, loadPicture(request.getSession().getServletContext().getRealPath(logoImg), workbook));
            
            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, (256*10)/7); //
            sheet.setColumnWidth(colIdx++, (256*10)/7); //
            sheet.setColumnWidth(colIdx++, (256*65)/7); //
            sheet.setColumnWidth(colIdx++, (256*96)/7); //contract Number
            sheet.setColumnWidth(colIdx++, (256*96)/7); //remain arrears
            
            if(withDebt) {
            colIdx=colIdx+3;
            sheet.setColumnWidth(colIdx++, (256*96)/7); //customer number
            sheet.setColumnWidth(colIdx++, (256*96)/7); //remain debts
            }
            SimpleDateFormat sb = new SimpleDateFormat("yyyyMMddhhmmss");
            String currTime = sb.format(Calendar.getInstance().getTime()).substring(0,8);
            String startDate = StringUtil.nullToBlank(result.get("startDate"));
            String endDate = StringUtil.nullToBlank(result.get("endDate"));
            
            Map<String,Object> sumData = (Map<String, Object>) result.get("sumData");
            
            Double chargedArrears = sumData.get("arrearsSumData") != null ? Double.parseDouble(sumData.get("arrearsSumData").toString()) : 0d;
            Double chargedDebts = sumData.get("debtsSumData") != null ? Double.parseDouble(sumData.get("debtsSumData").toString()) : 0d;
            if(startDate.length() > 8) {
            	startDate = startDate.substring(0,8);
            }
            if(endDate.length() > 8) {
            	endDate = endDate.substring(0,8);
            }
            
            //Data
            Map<String,Object> dataList = (Map<String,Object>)result.get("dataList");
            List<Map<String, Object>> debtsList = (List<Map<String, Object>>) dataList.get("debtsList");
            List<Map<String, Object>> arrearsList = (List<Map<String, Object>>) dataList.get("arrearsList");
            
            
            Map<String,String> remainData = getRemainData((Map<String,Object>)result.get("dataList"),supplier , withDebt);
            
            String dateFrom = TimeLocaleUtil.getLocaleDate(startDate, lang, country);
            String dateTo = TimeLocaleUtil.getLocaleDate(endDate, lang, country);
            String today = TimeLocaleUtil.getLocaleDate(currTime, lang, country);
            
            // Title
            int debtSize = debtsList == null ? 0 : debtsList.size();
            int arrearsSize = arrearsList == null ? 0 : arrearsList.size();
            printTitle(workbook, sheet, dateFrom, dateTo, today, debtSize, arrearsSize, withDebt, chargedArrears, chargedDebts, remainData, StringUtil.nullToBlank(supplierDesc));

            // Header
            printColumnHeader(sheet, headerStyle, language, withDebt);
            
            printData(sheet, dataList, withDebt);

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
		} //End Try

    }
    
    private void printTitle(HSSFWorkbook workbook, 
    		HSSFSheet sheet, 
    		String dateFrom, 
    		String dateTo,
    		String today,
    		int customerSize,
    		int contractSize,
    		Boolean withDebt,
    		Double chargedArrears,
    		Double chargedDebts,
    		Map<String,String> remainData,
    		String supplierDesc) {
    	String reportTitle = "Arrears Information";
    	
    	int start = 0;	// 우측의 타이틀 및 날짜 row merge시  좌측좌표 
    	int end = 0;		// 우측의 타이틀 및 날짜 row merge시  우측좌표
    	
    	end = 12;
		start = 5;
		
		String remainArrears = remainData.get("remainArrears") == null ? "0" : remainData.get("remainArrears");
		String remainDebts = remainData.get("remainDebts") == null ? "0" : remainData.get("remainDebts");
		
        HSSFRow row = sheet.createRow(0);
        row.setHeight((short)5);
        
        row = sheet.createRow(3);
        HSSFCell cell = row.createCell(start);
        //cell.setCellValue("ELECTRICITY COMPANY OF GHANA");
        cell.setCellValue(supplierDesc);
        cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, start, end -1 ));
		sheet.addMergedRegion(new CellRangeAddress(2, 2, start, end -1));
		sheet.addMergedRegion(new CellRangeAddress(3, 3, start, end -1));
		sheet.addMergedRegion(new CellRangeAddress(4, 4, start, end -1));
        
        row = sheet.createRow(4);
        cell = row.createCell(1);
        cell.setCellValue(reportTitle);
        cell.setCellStyle(ExcelUtil.getStyle(workbook, fontsubTitle, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(0, 3, 1, 3));
        sheet.addMergedRegion(new CellRangeAddress(4, 5, 1, 4));
        
        row = sheet.createRow(5);
        cell = row.createCell(start);
        cell.setCellValue("Date From : " + dateFrom + "   To : " + dateTo);
        cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitleDate, 0, 0, 0, 0, 0, 0, 0, 1, 0));
        sheet.addMergedRegion(new CellRangeAddress(5, 5, start, end -1));
        
        row = sheet.createRow(6);
        cell = row.createCell(start);
        cell.setCellValue("Today: " + today);
        cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitleDate, 0, 0, 0, 0, 0, 0, 0, 1, 0));
        sheet.addMergedRegion(new CellRangeAddress(6, 6, start, end -1));    	
        
        row = sheet.createRow(8);
        cell = row.createCell(3);
        cell.setCellValue("Total Contract Count : " + contractSize);
        cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitleDate, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        
        if(withDebt) {
        	cell = row.createCell(8);
            cell.setCellValue("Total Customer Count : " + customerSize);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitleDate, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }
        
        row = sheet.createRow(9);
        cell = row.createCell(3);
        cell.setCellValue("Total Charged Arrears: " + chargedArrears);
        cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitleDate, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        
        if(withDebt) {
        	cell = row.createCell(8);
        	cell.setCellValue("Total Charged Debts: " + chargedDebts);
        	cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitleDate, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }
        
        row = sheet.createRow(10);
    	cell = row.createCell(3);
    	cell.setCellValue("Total Remain Arrears: " + remainArrears);
    	cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitleDate, 0, 0, 0, 0, 0, 0, 0, 0, 0));

        if(withDebt) {
        	cell = row.createCell(8);
        	cell.setCellValue("Total Remain Debts: " + remainDebts);
        	cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitleDate, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }
    }
    
    private void printColumnHeader(HSSFSheet sheet, HSSFCellStyle style, Language lang, Boolean withDebt) {
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
    	
        HSSFRow row = sheet.createRow(11);

        int width = WIDTH;
        int debtWidth = DEBTWIDTH;
        ReportProperty[] columns = ArrearsProperty.values();
        
		//blank
        sheet.addMergedRegion(new CellRangeAddress(11, 11, 0, 2));
        
        for ( int i = 3; i < width ; i ++) {
        	HSSFCell cell = row.createCell(i);
        	cell.setCellStyle(style);
        } 
        
		for ( ReportProperty pr : columns ) {
			HSSFCell cell = row.createCell(pr.getColumnIndex());
			cell.setCellValue(prop.getProperty(pr.getColumnHeader(), ""));
			cell.setCellStyle(style);    			
		}  
		
		if(withDebt) {
			ReportProperty[] columnsDebt = ArrearsWithDebtProperty.values();
			
			for (int i = 8; i < debtWidth; i++ ) {
	        	HSSFCell cell = row.createCell(i);
	        	cell.setCellStyle(style);
	        }
			
			for ( ReportProperty pr : columnsDebt ) {
				HSSFCell cell = row.createCell(pr.getColumnIndex());
				cell.setCellValue(prop.getProperty(pr.getColumnHeader(), ""));
				cell.setCellStyle(style);    			
			}
		}
		
		
    }
    
    private void printData(HSSFSheet sheet, Map<String, Object> data, Boolean withDebt) {
    	int rowNumber = 11;
    	int debtRowNumber = 11;

    	int width = WIDTH;
    	int debtWidth = DEBTWIDTH;
    	
    	List<Map<String, Object>> arrearsData = (List<Map<String, Object>>)data.get("arrearsList");
    	List<Map<String, Object>> debtsData = (List<Map<String, Object>>)data.get("debtsList");
    	
    	printDataArrears(arrearsData, rowNumber, width, sheet);
    	
    	if(withDebt) {
    		printDataDebt(debtsData, debtRowNumber, debtWidth, sheet, arrearsData.size());
    	}
		
    }
    
    private void printDataArrears(List<Map<String, Object>> arrearsData, int rowNumber, int width, HSSFSheet sheet) {
    	ReportProperty[] column = ArrearsProperty.values();

    	for ( Map<String, Object> rowData: arrearsData ) {
            ++rowNumber;
            
            HSSFRow row = sheet.createRow(rowNumber);
            for ( int i = 3; i < width ; i ++) {
            	HSSFCell cell = row.createCell(i);
            	if(rowNumber%2 == 0) {
            		cell.setCellStyle(bodyStyle_W_L);
            	} else {
            		cell.setCellStyle(bodyStyle_S_L);
            	}
            }
            
    		for ( ReportProperty property :  column) {
                
    			HSSFCell cell = row.createCell(property.getColumnIndex());
    			Object cellValue = rowData.get( property.getKey() );
    			if(cellValue instanceof Double) {
    				cell.setCellValue( (Double)rowData.get( property.getKey() ) );
    			} else if(cellValue instanceof String) {
    				cell.setCellValue( (String)rowData.get( property.getKey() ) );
    			}
                
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
    	}  
    }
    
    private void printDataDebt(List<Map<String, Object>> debtData, int rowNumber, int width, HSSFSheet sheet, int arrearsSize) {
    	ReportProperty[] column = ArrearsWithDebtProperty.values();

    	for ( Map<String, Object> rowData: debtData ) {
            ++rowNumber;
            
            HSSFRow row = null;
            if(arrearsSize >= debtData.size()) {
            	row = sheet.getRow(rowNumber);
            } else {
            	if(arrearsSize < rowNumber) {
            		row = sheet.createRow(rowNumber);
            	} else {
            		row = sheet.getRow(rowNumber);
            	}
            }
            
            for ( int i = 8; i < width ; i ++) {
            	HSSFCell cell = row.createCell(i);
            	if(rowNumber%2 == 0) {
            		cell.setCellStyle(bodyStyle_W_L);
            	} else {
            		cell.setCellStyle(bodyStyle_S_L);
            	}
            }
            
    		for ( ReportProperty property :  column) {
                
    			HSSFCell cell = row.createCell(property.getColumnIndex());
    			Object cellValue = rowData.get( property.getKey() );
    			if(cellValue instanceof Double) {
    				cell.setCellValue( (Double)rowData.get( property.getKey() ) );
    			} else if(cellValue instanceof String) {
    				cell.setCellValue( (String)rowData.get( property.getKey() ) );
    			}
                
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
    	}  
    }
    
    interface ReportProperty {
    	public int getColumnIndex();
    	public String getColumnHeader();
    	public String getKey();
    	public String getAlign();
    	public String getTotalKey();
    }

    enum ArrearsProperty implements ReportProperty {
    	contractNumber("aimir.contractNumber",3, "CONTRACTNUMBER", "L", null), 
    	remainArrears("aimir.remaining.arrears", 4, "ARREARS", "L", null);
    	
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
    	
    	ArrearsProperty(String header, int columnIndex, String key, String align, String totalKey) {
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

    enum ArrearsWithDebtProperty implements ReportProperty {
    	customerNumber("aimir.customerid",8, "CUSTOMERNUMBER", "L", null),
    	remainDebts("aimir.remaining.debt", 9, "DEBTSUM", "L", null);
    	
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
    	
    	ArrearsWithDebtProperty(String header, int columnIndex, String key, String align, String totalKey) {
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
    
    private Map<String,String> getRemainData(Map<String, Object> dataList, Supplier supplier, Boolean withDebt ) {
    	
    	DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
    	
    	List<Map<String, Object>> arrearsList = (List<Map<String, Object>>)dataList.get("arrearsList");
    	
    	Map<String,String> remainData = new HashMap<String,String>();
    	
    	int size = arrearsList.size();
    	BigDecimal remainArrears = new BigDecimal(0);
    	Map<String, Object> temp = new HashMap<String, Object>();
    	for (int i = 0; i < size; i++) {
    		temp = arrearsList.get(i);
    		remainArrears = remainArrears.add(temp != null && temp.get("ARREARS") != null ? new BigDecimal(Double.parseDouble(temp.get("ARREARS").toString())) : new BigDecimal(0));
		}
    	
    	remainData.put("remainArrears", df.format(remainArrears));
    	
    	if(withDebt) {
    		List<Map<String, Object>> debtsList = (List<Map<String, Object>>)dataList.get("debtsList");

    		int tempSize = debtsList.size();
    		BigDecimal remainDebts = new BigDecimal(0);
        	Map<String, Object> tempMap = new HashMap<String, Object>();
        	for (int i = 0; i < tempSize; i++) {
        		tempMap = debtsList.get(i);
        		remainDebts = remainDebts.add(tempMap != null && tempMap.get("DEBTSUM") != null ? new BigDecimal(Double.parseDouble(tempMap.get("DEBTSUM").toString())) : new BigDecimal(0));
    		}
        	remainData.put("remainDebts", df.format(remainDebts));
    	}
    	
    	
    	
    	return remainData;
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
    
    private int loadPicture( String path, HSSFWorkbook wb ) throws IOException {
        int pictureIndex;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;

        try {
            fis = new FileInputStream(path);
            bos = new ByteArrayOutputStream( );
            int c;
            while ( (c = fis.read()) != -1) {
                bos.write( c );
            }
            pictureIndex = wb.addPicture( bos.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG);
        } finally {
            if (fis != null) fis.close();
            if (bos != null) bos.close();
        }
        return pictureIndex;
    }

}
