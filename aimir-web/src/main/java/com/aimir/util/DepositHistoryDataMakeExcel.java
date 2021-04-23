package com.aimir.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Language;
import com.aimir.model.system.Supplier;
import com.ibm.icu.util.Calendar;

public class DepositHistoryDataMakeExcel {
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
	
	int SALES_WIDTH = 18;
	int DEPOSIT_WIDTH = 10;
	String supplierName = null;
	public DepositHistoryDataMakeExcel() {
		
	}
	
	@Autowired
	SupplierDao supplierDao;
	
	@SuppressWarnings({ "unchecked", "deprecation" })
    public void writeReportExcel(Map<String, Object> result, 
    		Supplier supplier,
    		String reportType, 
    		boolean onlyTotal, 
    		String filePath, 
    		String fileName, String supplierDesc, String logoImg) {
    	
    	String country = supplier.getCountry().getCode_2letter();
    	String lang = supplier.getLang().getCode_2letter();
    	Language language = supplier.getLang();
    	supplierName = supplier.getName();
    	
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
            //HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, (short)2, 4, (short)3, 0); // 이미지 크기조절
            //HSSFClientAnchor(0, 0, 0, 0, 좌측상단컬럼, 좌측상단로우, 우측하단컬럼, 우측하단로우)
            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, (short)2, 1, (short)3, 4); // 이미지 크기조절
            
            // anchor.setAnchorType(2);
            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
            
            HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
            //parch.createPicture(anchor, loadPicture(request.getSession().getServletContext().getRealPath("/images/ECG_logo.gif"), workbook));
            parch.createPicture(anchor, loadPicture(request.getSession().getServletContext().getRealPath(logoImg), workbook));
            
            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, (256*10)/7); //vending Station
            sheet.setColumnWidth(colIdx++, (256*10)/7); //vending Station
            sheet.setColumnWidth(colIdx++, (256*65)/7); //vending Station
            sheet.setColumnWidth(colIdx++, (256*65)/7); //cashier
            sheet.setColumnWidth(colIdx++, (256*20)/7); //cashier
            sheet.setColumnWidth(colIdx++, (256*100)/7); //customer
            sheet.setColumnWidth(colIdx++, (256*75)/7); //accoutNo
            sheet.setColumnWidth(colIdx++, (256*75)/7); //meterId
            sheet.setColumnWidth(colIdx++, (256*75)/7); //prepaymentType
            sheet.setColumnWidth(colIdx++, (256*80)/7); //chargeValue
            sheet.setColumnWidth(colIdx++, (256*75)/7); //ChargedArrearsA
            sheet.setColumnWidth(colIdx++, (256*75)/7); //ChargedArrearsB
            sheet.setColumnWidth(colIdx++, (256*75)/7); //activity
            sheet.setColumnWidth(colIdx++, (256*75)/7); //activity
            sheet.setColumnWidth(colIdx++, (256*75)/7); //NIC
            sheet.setColumnWidth(colIdx++, (256*75)/7); //billId
            sheet.setColumnWidth(colIdx++, (256*90)/7); //date
            sheet.setColumnWidth(colIdx++, (256*150)/7); //address
            sheet.setColumnWidth(colIdx++, (256*120)/7); //cancelDate
            sheet.setColumnWidth(colIdx++, (256*150)/7); //cancelReason
            
            SimpleDateFormat sb = new SimpleDateFormat("yyyyMMddhhmmss");
            String currTime = sb.format(Calendar.getInstance().getTime()).substring(0,8);
            String startDate = StringUtil.nullToBlank(result.get("startDate"));
            String endDate = StringUtil.nullToBlank(result.get("endDate"));
            
            if(startDate.length() > 8) {
            	startDate = startDate.substring(0,8);
            }
            if(endDate.length() > 8) {
            	endDate = endDate.substring(0,8);
            }
            
            String dateFrom = TimeLocaleUtil.getLocaleDate(startDate, lang, country);
            String dateTo = TimeLocaleUtil.getLocaleDate(endDate, lang, country);
            String today = TimeLocaleUtil.getLocaleDate(currTime, lang, country);
            
            // Title
            printTitle(workbook, sheet, reportType, dateFrom, dateTo, today, onlyTotal, StringUtil.nullToBlank(supplierDesc));

            // Header
            printColumnHeader(sheet, headerStyle, reportType, language, onlyTotal, withDebt);
            
            int rowNumber = 8;
            
            //Data
            List<Map<String, String>> dataList = (List<Map<String, String>>) result.get("dataList");
            
            if ( !onlyTotal ) {
            	rowNumber = printData(sheet, dataList, reportType, withDebt);
            }
            
            // total : dataList에서 마지막 데이터가 합계 데이터이다.
            Map<String, String> totalData = dataList.get(dataList.size() -1); 
           
            printTotal(sheet, reportType, totalData, ++rowNumber, onlyTotal, withDebt);
            
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
    
    interface ReportProperty {
    	public int getColumnIndex();
    	public String getColumnHeader();
    	public String getKey();
    	public String getAlign();
    	public String getTotalKey();
    }
    
    enum SalesSPASAProperty implements ReportProperty {
    	vendingStation("aimir.vendingStation", 0, "vendingStationName", "L", null), 
    	customer("aimir.customer", 3, "customerName", "L", null),
    	accountNo("aimir.accountNo", 5, "accountNo", "L", null),
    	meterId("aimir.meterid", 6, "meterId", "L", null),
    	paymentType("aimir.paymenttype", 7, "paymentType", "L", null), 
    	chargedCredit("aimir.chargeAmount", 8, "chargedCredit", "R", "totalChargedCredit"),
    	chargedArrears("aimir.prepayment.chargearrears", 9, "chargedArrears", "R", "totalChargedArrears"),
    	tariff("aimir.residental.activity", 10, "tariffName", "L", null),
    	geoCode("aimir.contractNumber", 11, "geoCode", "L", null),
    	prepaymentLogId("aimir.contract.receioptNo", 13, "prepaymentLogId", "L", null),
    	lastTokenId("aimir.hems.prepayment.transactionNum", 14, "lastTokenId", "L", null),
    	date("aimir.date", 15, "date", "L", null), 
    	address("aimir.address", 16, "address", "L", null),
    	cashier("aimir.operator", 17, "cashier", "L", null),
    	cancelDate("aimir.cancelDate", 18, "cancelDate", "L", null),
    	cancelReason("aimir.cancelReason", 19, "cancelReason", "L", null);
    	
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
    	
    	SalesSPASAProperty(String header, int columnIndex, String key, String align, String totalKey) {
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
   
    enum SalesProperty implements ReportProperty {
    	vendingStation("aimir.id", 0, "cashierId", "L", null), 
    	cashier("aimir.prepayment.casher" , 3, "cashierName", "L", null),
    	customer("aimir.customer", 5, "customerName", "L", null),
    	accountNo("aimir.accountNo", 6, "accountNo", "L", null),
    	meterId("aimir.meterid", 7, "meterId", "L", null),
    	paymentType("aimir.paymenttype", 8, "paymentType", "L", null), 
    	chargedCredit("aimir.amount.paid", 9, "totalAmountPaid", "R", "totalAmountPaid"),
    	chargedArrearsA("aimir.prepayment.chargearrearsA", 10, "chargedArrears", "R", "totalChargedArrears"),
    	chargedArrearsB("aimir.prepayment.chargearrearsB", 11, "chargedArrears2", "R", "totalChargedArrears2"),
    	tariff("aimir.residental.activity", 13, "tariffName", "L", null),
    	geoCode("aimir.contractNumber", 14, "geoCode", "L", null),
    	prepaymentLogId("aimir.billId", 15, "prepaymentLogId", "L", null),
    	date("aimir.date", 16, "date", "L", null), 
    	address("aimir.address", 17, "address", "L", null),
    	cancelDate("aimir.cancelDate", 18, "cancelDate", "L", null),
    	cancelReason("aimir.cancelReason", 19, "cancelReason", "L", null);
    	
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
    	
    	SalesProperty(String header, int columnIndex, String key, String align, String totalKey) {
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
    
    enum SalesDebtsProperty implements ReportProperty {
    	vendingStation("aimir.vendingStation", 0, "vendingStationName", "L", null), 
    	customer("aimir.customer", 3, "customerName", "L", null),
    	accountNo("aimir.accountNo", 5, "accountNo", "L", null),
    	meterId("aimir.meterid", 6, "meterId", "L", null),
    	paymentType("aimir.paymenttype", 7, "paymentType", "L", null), 
    	chargedCredit("aimir.chargeAmount", 8, "chargedCredit", "R", "totalChargedCredit"),
    	chargedArrears("aimir.prepayment.chargedarrears", 9, "chargedArrears", "R", "totalChargedArrears"),
    	chargedDebts("aimir.chargedDebt", 10, "chargedDebts", "R", "totalChargedDebts"),
    	tariff("aimir.residental.activity", 11, "tariffName", "L", null),
    	geoCode("aimir.contractNumber", 12, "geoCode", "L", null),
    	prepaymentLogId("aimir.billId", 14, "prepaymentLogId", "L", null),
    	date("aimir.date", 15, "date", "L", null), 
    	address("aimir.address", 16, "address", "L", null),
    	cashier("aimir.operator", 17, "cashier", "L", null),
    	cancelDate("aimir.cancelDate", 18, "cancelDate", "L", null),
    	cancelReason("aimir.cancelReason", 19, "cancelReason", "L", null);
    	
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
    	
    	SalesDebtsProperty(String header, int columnIndex, String key, String align, String totalKey) {
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
    
    enum SalesTotalProperty implements ReportProperty {
    	vendingStation("aimir.vendingStation", 0, "vendingStationName", "L", null), 
    	customer("aimir.customer", 3, "customerName", "L", null),
    	accountNo("aimir.accountNo", 5, "accountNo", "L", null),
    	meterId("aimir.meterid", 6, "meterId", "L", null),
    	paymentType("aimir.paymenttype", 7, "paymentType", "L", null), 
    	chargedCredit("aimir.chargeAmount", 8, "chargedCredit", "R", "totalChargedCredit"),
    	chargedArrears("aimir.prepayment.chargearrears", 9, "chargedArrears", "R", "totalChargedArrears");

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

    	SalesTotalProperty(String header, int columnIndex, String key, String align, String totalKey) {
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
    
    enum SalesDebtTotalProperty implements ReportProperty {
    	vendingStation("aimir.vendingStation", 0, "vendingStationName", "L", null), 
    	customer("aimir.customer", 3, "customerName", "L", null),
    	accountNo("aimir.accountNo", 5, "accountNo", "L", null),
    	meterId("aimir.meterid", 6, "meterId", "L", null),
    	paymentType("aimir.paymenttype", 7, "paymentType", "L", null), 
    	chargedCredit("aimir.chargeAmount", 8, "chargedCredit", "R", "totalChargedCredit"),
    	chargedArrears("aimir.prepayment.chargearrears", 9, "chargedArrears", "R", "totalChargedArrears"),
    	chargedDebts("aimir.chargedDebt", 10, "chargedDebts", "R", "totalChargedDebts");

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

    	SalesDebtTotalProperty(String header, int columnIndex, String key, String align, String totalKey) {
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
    
    enum DepositProperty implements ReportProperty {
    	id("aimir.billId", 0, "depositHistoryId", "L", null),
    	vendingStation("aimir.vendingStation", 3, "vendingStationName", "L", null), 
    	date("aimir.date", 5, "date", "L", null), 
    	chagedDeposit("aimir.prepayment.chargevalue", 6, "chargedDeposit", "R", "totalChargedDeposit"),
    	tax("aimir.tax", 7, "tax", "R", "totalChargedTax"),
    	commission("aimir.prepayment.commission", 8, "commission", "R", "totalChargedCommission"),
    	netValue("aimir.netvalue", 9, "netValue", "R", "totalChargedNetValue");

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
    	
    	DepositProperty(String header, int columnIndex, String key, String align, String totalKey) {
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
    
    private void printTitle(HSSFWorkbook workbook, 
    		HSSFSheet sheet, 
    		String reportType,
    		String dateFrom, 
    		String dateTo,
    		String today,
    		Boolean onlyTotal, String supplierDesc) {
    	String reportTitle = "";
    	
    	int start = 0;	// 우측의 타이틀 및 날짜 row merge시  좌측좌표 
    	int end = 0;		// 우측의 타이틀 및 날짜 row merge시  우측좌표
    	
    	if ( reportType.equalsIgnoreCase("sales") || reportType.equalsIgnoreCase("cancelled")) {
    		if(reportType.equalsIgnoreCase("sales"))
    			reportTitle = "Vendor/Cashier Report";
    		else if(reportType.equalsIgnoreCase("cancelled"))
    			reportTitle = "Operator Cancelled Report";
    		end = SALES_WIDTH;
    		start = 11; 
    		if ( onlyTotal ) {
    			end= DEPOSIT_WIDTH;
    			start =6;
    		}
    	} else if ( reportType.equalsIgnoreCase("deposit")) {
    		reportTitle = "Deposit Sales Report";
    		end = DEPOSIT_WIDTH;
    		start = 6;
    	}
    	
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
        sheet.addMergedRegion(new CellRangeAddress(4, 5, 1, 5));
        
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
    }
    
    private void printColumnHeader(HSSFSheet sheet, HSSFCellStyle style, String reportType, Language lang, Boolean onlyTotal, Boolean withDebt) {
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
    	if (reportType.equalsIgnoreCase("sales") || reportType.equalsIgnoreCase("cancelled")) {
    		width = SALES_WIDTH;
    		if("Spasa".equals(supplierName)) {
    			columns =SalesSPASAProperty.values();
    		} else if(withDebt) {
    			columns = SalesDebtsProperty.values();
    		} else {
    			columns =SalesProperty.values();
    		}
    		
    		if ( onlyTotal ) {
    			width = DEPOSIT_WIDTH;
    			
    			if(withDebt) {
    				columns = SalesDebtTotalProperty.values();
    			} else {
    				columns = SalesTotalProperty.values();
    			}
    			
    		}
    		//vending station cell merge
            sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 2));
            //customer cell merge
            sheet.addMergedRegion(new CellRangeAddress(7, 7, 3, 4));
            
            if ( !onlyTotal) {
	            //contractNumber(geoCode) cell merge
            	if(withDebt) {
            		sheet.addMergedRegion(new CellRangeAddress(7, 7, 12, 13));
            	} else {
            		sheet.addMergedRegion(new CellRangeAddress(7, 7, 11, 12));
            	}
    			
	            
            }
    	} else if ( reportType.equalsIgnoreCase("deposit")) {
    		width = DEPOSIT_WIDTH;

             columns = DepositProperty.values();
    		
    		//id cell merge
            sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 2));
            //vendingStation cell merge
            sheet.addMergedRegion(new CellRangeAddress(7, 7, 3, 4));    		
    	}
    	
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
    
    private int printData(HSSFSheet sheet, List<Map<String, String>> data, String reportType, Boolean withDebt) {
    	int rowNumber = 7;
    	ReportProperty[] column = null;
    	int width = 0;
    	
    	if ( reportType.equalsIgnoreCase("sales") || reportType.equalsIgnoreCase("cancelled")) {
    		if("Spasa".equals(supplierName)) {
    			column = SalesSPASAProperty.values();
    		} else if(withDebt) {
    			column = SalesDebtsProperty.values();
    		} else {
    			column = SalesProperty.values();
    		}
    		 width = SALES_WIDTH;
    	} else if ( reportType.equalsIgnoreCase("deposit") ) {
    		column = DepositProperty.values();
    		width= DEPOSIT_WIDTH;
    	}
    	
		for ( Map<String, String> rowData: data ) {
            ++rowNumber;
            HSSFRow row = sheet.createRow(rowNumber);
            for ( int i = 0; i < width ; i ++) {
            	HSSFCell cell = row.createCell(i);
            	if(rowNumber%2 == 0) {
            		cell.setCellStyle(bodyStyle_W_L);
            	} else {
            		cell.setCellStyle(bodyStyle_S_L);
            	}
            }
            
    		for ( ReportProperty property :  column) {
                
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
            
            if ( reportType.equalsIgnoreCase("sales") ||reportType.equalsIgnoreCase("cancelled")) {
	            //contractNumber(geoCode) cell merge
            	if(withDebt) {
            		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 12, 13));
            	} else {
            		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 11, 12));
            	}
	            
            }
    	}    	
    	return rowNumber;
    }
    
    private void printTotal(HSSFSheet sheet, String reportType, Map<String, String> data, int rowNumber, Boolean onlyTotal, Boolean withDebt) {
    	ReportProperty[] column = null;
    	int width = 0;
    	
    	if ( reportType.equalsIgnoreCase("sales") | reportType.equalsIgnoreCase("cancelled")) {
    		if("Spasa".equals(supplierName)) {
    			column = SalesSPASAProperty.values();
    		} else if (withDebt) {
    			column = SalesDebtsProperty.values();
    		} else {
    			column = SalesProperty.values();
    		}
    		width = SALES_WIDTH; 
    		
    		if ( onlyTotal ) {
    			if(withDebt) {
    				column = SalesDebtTotalProperty.values();
    			} else {
    				column = SalesTotalProperty.values();
    			}
    			
    			width = DEPOSIT_WIDTH;
    		}
    		
    	} else if ( reportType.equalsIgnoreCase("deposit")) {
    		column = DepositProperty.values();
    		width = DEPOSIT_WIDTH;
    	}
       	HSSFRow row = sheet.createRow(rowNumber);
        
       	for ( int i = 0; i < width ; i ++) {
        	HSSFCell cell = row.createCell(i);
        	cell.setCellStyle(bottomStyle);
        }

        HSSFCell cell = row.createCell(0);
        cell.setCellValue("total");
        cell.setCellStyle(headerStyle);
       	
        for ( ReportProperty property: column ) {
       		if ( property.getTotalKey() != null ) {
       			String value = StringUtil.nullToBlank(data.get(property.getTotalKey()));
       			cell = row.createCell(property.getColumnIndex());
       			cell.setCellValue(value);
       			cell.setCellStyle(bottomStyle);
       		}
       	}
    	
        if ( reportType.equalsIgnoreCase("sales") || reportType.equalsIgnoreCase("cancelled")) {
        	sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 7));
        	if ( !onlyTotal ) {
        		if("Spasa".equals(supplierName)) {
        			sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 10, 19));
        		} else if(withDebt) {
        			sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 11, 19));
        		} else {
        			sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 10, 18));
        		}
        	}
        } else if ( reportType.equalsIgnoreCase("deposit") ) {
        	sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 5));        	
        }
    }
    

}
