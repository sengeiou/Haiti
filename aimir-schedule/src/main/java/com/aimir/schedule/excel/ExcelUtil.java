package com.aimir.schedule.excel;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import com.aimir.schedule.excel.MonthlyConsumeData.ConsumeLevel;

public class ExcelUtil {
	public enum TariffType {
		Residential("Residential"),
		NonResidental("Non Residential");
		
		TariffType(String name) {
			this.name = name;
		}
		
		private String name;
		
		public String getName() {
			return this.name;
		}
		
		public static TariffType getTarrifTypeByName(String name) {
			TariffType[] types = TariffType.values();
			for ( TariffType type : types ) {
				if ( type.getName().equals(name) ) {
					return type;
				}
			}
			return null;
		}

		public boolean equals(TariffType tariff) {
			return this.getName().equals(tariff.getName());
		}
	}

	public enum MonthEnum {
		Jan("01", "JAN"),
		Feb("02", "FEB"),
		Mar("03", "MAR"),
		Apr("04", "APR"),
		May("05", "MAY"),
		Jun("06", "JUN"),
		Jul("07", "JUL"),
		Aug("08", "AUG"),
		Sep("09", "SEP"),
		Oct("10", "OCT"),
		Nov("11", "NOV"),
		Dec("12", "DEC");
		
		MonthEnum(String code, String name) {
			this.code = code;
			this.name = name;
		}
		
		private String code;
		private String name;
		
		public String getCode() {
			return this.code;
		}
		
		public String getName() {
			return this.name;
		}
		
		public static String  getMonthName(String code) {
			MonthEnum[] months =  MonthEnum.values();
			for ( MonthEnum month : months ) {
				if ( month.getCode().equals(code) ) {
					return month.getName();
				}
			}
			return "";
		}
	}

	public enum PropertyMap {
		cons("level", "CONS\nBreakdown", "", CellStyle.ALIGN_CENTER),
		district("district", "District", "", CellStyle.ALIGN_CENTER),
		numOfCust("numOfCust", "NO OF\nCUST", new Integer(0), CellStyle.ALIGN_LEFT),
		unitsConsumed("unitsConsumed", "UNITS\nCONSUMED", new Double(0d), CellStyle.ALIGN_RIGHT),
		unitsCharge("unitsCharge", "UNITS\nCHARGE", new Double(0d), CellStyle.ALIGN_RIGHT),
		serviceCharge("serviceCharge", "SERVICE\nCHARGE", new Double(0d), CellStyle.ALIGN_RIGHT),
		chargesWithoutLevies("chargesWithoutLevies", "CHARGES\nWITHOUT LEVIES", new Double(0d), CellStyle.ALIGN_RIGHT),
		vat("vat", "VAT", new Double(0d), CellStyle.ALIGN_RIGHT),
		vatOnSubsidy("vatOnSubsidy", "VAT ON\nSUBSIDY",new Double(0d), CellStyle.ALIGN_RIGHT),
		pubLevy("pubLevy", "PUBL\nLIGHT", new Double(0d), CellStyle.ALIGN_RIGHT),
		govLevy("govLevy", "GOVT\nLEVY", new Double(0d), CellStyle.ALIGN_RIGHT),
		totalLevies("totalLevies", "TOTAL\nLEVIES", new Double(0d), CellStyle.ALIGN_RIGHT),
		totalCharge("totalCharge", "TOTAL\nCHARGE", new Double(0d), CellStyle.ALIGN_RIGHT),	
		subsity("subsidy", "GOV'T\nSUBSIDY", new Double(0d), CellStyle.ALIGN_RIGHT),
		lifeLineSubsidy("lifeSubsidy", "LIFELINE\nSUBSIDY", new Double(0d), CellStyle.ALIGN_RIGHT),
		addtionalSubsidy("additionalSubsidy", "ADDITIONAL\nSUBSIDY", new Double(0d), CellStyle.ALIGN_RIGHT);
		
		// data에 대한 HashMap<String, Object>에 대한 key
		private String prop;
		// excel에서 각 column의 caption label
		private String message;
		// data에서 값이 null인 경우 엑셀의 값
		private Object zero;
		// 엑셀에서 특정 column에 대한 정렬 방식
		private short align;
		
		PropertyMap (String prop, String message, Object zero, short align) {
			this.prop = prop;
			this.message = message;
			this.zero = zero;
			this.align = align;
		}
		
		public String getProp() {
			return this.prop;
		}
		
		public String getMessage() {
			return this.message;
		}
		
		public Object getZero() {
			return this.zero;
		}
		
		public short getAlign() {
			return this.align;
		}
	}	

	public enum ReportType {
		Sales(1),
		Region(2);
		private int type;
		
		ReportType(int type) {
			this.type = type;
		}
		
		public int getType() {
			return this.type;
		}
	}
	
	public enum ReportColumn {
		SalesResidential(ReportType.Sales, TariffType.Residential, 
				PropertyMap.cons,
				PropertyMap.numOfCust,
				PropertyMap.unitsConsumed,
				PropertyMap.unitsCharge,
				PropertyMap.serviceCharge,
				PropertyMap.chargesWithoutLevies,
				PropertyMap.govLevy,
				PropertyMap.pubLevy,
				PropertyMap.totalLevies,
				PropertyMap.totalCharge,
				PropertyMap.lifeLineSubsidy,
				PropertyMap.subsity,
				PropertyMap.addtionalSubsidy),
		SalesNonResidential(ReportType.Sales, TariffType.NonResidental, 
				PropertyMap.cons,
				PropertyMap.numOfCust,
				PropertyMap.unitsConsumed,
				PropertyMap.unitsCharge,
				PropertyMap.serviceCharge,
				PropertyMap.chargesWithoutLevies,
				PropertyMap.vat,				
				PropertyMap.vatOnSubsidy,
				PropertyMap.govLevy,
				PropertyMap.pubLevy,
				PropertyMap.totalLevies,
				PropertyMap.totalCharge,
				PropertyMap.addtionalSubsidy),
		RegionalResidential(ReportType.Region, TariffType.Residential,
				PropertyMap.district,
				PropertyMap.numOfCust,
				PropertyMap.unitsConsumed,
				PropertyMap.unitsCharge,
				PropertyMap.serviceCharge,
				PropertyMap.chargesWithoutLevies,
				PropertyMap.govLevy,
				PropertyMap.pubLevy,
				PropertyMap.totalLevies,
				PropertyMap.totalCharge,
				PropertyMap.lifeLineSubsidy,
				PropertyMap.subsity,
				PropertyMap.addtionalSubsidy),
		RegionalNonResidential(ReportType.Region, TariffType.NonResidental,
				PropertyMap.district,
				PropertyMap.numOfCust,
				PropertyMap.unitsConsumed,
				PropertyMap.unitsCharge,
				PropertyMap.serviceCharge,
				PropertyMap.chargesWithoutLevies,
				PropertyMap.vat,
				PropertyMap.vatOnSubsidy,
				PropertyMap.govLevy,
				PropertyMap.pubLevy,
				PropertyMap.totalLevies,
				PropertyMap.totalCharge,
				PropertyMap.addtionalSubsidy);
		
		private ReportType type;
		private TariffType tariff;
		private PropertyMap[] property;
		
		ReportColumn(ReportType type,TariffType tariff, PropertyMap... property) {
			this.type = type;
			this.tariff = tariff;
			this.property = property;
		}
		
		public ReportType getReportType() {
			return this.type;
		}
		
		public TariffType getTariffType() {
			return this.tariff;
		}
		
		public PropertyMap[] getProperties() {
			return this.property;
		}
		
		public static PropertyMap[] getProperties(ReportType type, TariffType tariff) {
			ReportColumn[] value =  ReportColumn.values();
			for ( ReportColumn report : value) {
				if ( report.getTariffType().equals(tariff) && 
						report.getReportType().equals(type) ) {
					return report.getProperties();
				}
			}
			return null;
		}
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
    public static HSSFCellStyle getStyle(HSSFWorkbook workbook, HSSFFont font, int top, int bottom, int left, int right, int grey,
            int green, int orange, int align, int border) {

        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        //style.setWrapText(true);
        short borderStyle = HSSFCellStyle.BORDER_MEDIUM;
        
        if(border == 0) {
        	borderStyle = HSSFCellStyle.BORDER_THIN;
        }

        if (top == 1) {
    	   style.setBorderTop(borderStyle);
           style.setTopBorderColor(HSSFColor.BLACK.index);
        }

        if (bottom == 1) {
            style.setBorderBottom(borderStyle);
            style.setBottomBorderColor(HSSFColor.BLACK.index);
        }

        if (left == 1) {
            style.setBorderLeft(borderStyle);
            style.setLeftBorderColor(HSSFColor.BLACK.index);
        }

        if (right == 1) {
            style.setBorderRight(borderStyle);
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
        }

        if (align == 1) {
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        } else if (align == 2) {
            style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        }

        return style;
    }

	public static Map<String, Object> createZeroRecord(ConsumeLevel level) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		for ( PropertyMap property : PropertyMap.values() ) {
			result.put( property.getProp(), property.getZero() );
		}
		result.put( PropertyMap.cons.getProp(), level.getName() );
		return result;
	}

	public static void createTitleRow(HSSFCellStyle style, HSSFRow row, int index, PropertyMap... columns) {
		
		for ( PropertyMap column : columns )	 {
	        HSSFCell cell = row.createCell( index++ );
	        cell.setCellValue( new HSSFRichTextString( column.getMessage() ));
	        cell.setCellStyle( style );			
		}
	}

	public static void createDataRow(Map<String, Object> record, 
		HSSFWorkbook workbook, HSSFRow row, int index, PropertyMap... columns) {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		
		DecimalFormat df = new DecimalFormat("###,###,##0.####");
		for ( PropertyMap column : columns ) {
			Object value = record.get(column.getProp());
			HSSFCell cell = row.createCell( index++ );
			
			if ( column.getAlign() == CellStyle.ALIGN_RIGHT ) {
				cell.setCellValue(df.format(value));
			} else if ( value != null ){
				cell.setCellValue(value.toString());
			}
			style.setAlignment(column.getAlign());
			cell.setCellStyle(style);
		}
	}		
}


