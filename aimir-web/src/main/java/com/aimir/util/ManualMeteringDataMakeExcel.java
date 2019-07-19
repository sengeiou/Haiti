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
import org.apache.poi.ss.util.CellRangeAddress;

import com.aimir.model.mvm.MeteringDay;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.bean.MeteringListData;

/**
 * MeteringDataMakeExcel.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2012. 4. 26.   v1.0       enj         
 *
 */
public class ManualMeteringDataMakeExcel {

    public ManualMeteringDataMakeExcel() {

    }
    
    /**
     * @param result
     * @param msgMap
     * @param isLast
     * @param filePath
     * @param fileName
     */
    public void writeManualMeteringDayExcel(
    	List<MeteringDay> result, 
    	Map<String, String> msgMap,    	
    	Supplier supplier,
    	boolean isLast, String filePath, String fileName) {

    	MeteringDay m = null;
    	String lang = supplier.getLang().getCode_2letter();
    	String country = supplier.getCountry().getCode_2letter();
    	
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
            HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
            HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);

            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = "Metering Data Report";
            int meteringDataStartRow = 3;
            int totalColumnCnt = 10;
            int dataCount = 0;

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

            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));

            /**
             * {
	    	    	header: I18N["aimir.meteringdate"],
	    	    	header: I18N["aimir.meterid"],
	    	    	header: I18N["aimir.name"],
	    	    	header: I18N["aimir.usage"] + params.unit,
	    	    	header: I18N["aimir.thisdaydata"],
             */
            // Title
            row = sheet.createRow(meteringDataStartRow);
   
            cell = row.createCell(0);
            cell.setCellValue(msgMap.get("meteringDate"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(1);
            cell.setCellValue(msgMap.get("mdsId"));
            cell.setCellStyle(titleCellStyle);
			
            cell = row.createCell(2);
            cell.setCellValue(msgMap.get("friendlyName"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(3);
            cell.setCellValue(msgMap.get("usage"));
            cell.setCellStyle(titleCellStyle);
            
         	cell = row.createCell(4);
            cell.setCellValue(msgMap.get("thisDayData"));
            cell.setCellStyle(titleCellStyle);
            
            dataCount = result.size();
            for(int i = 0 ; i < dataCount ; i++) {
            	m = result.get(i);
            	row = sheet.createRow(i+ (meteringDataStartRow + 1));      

            	cell = row.createCell(0);
            	cell.setCellValue(TimeLocaleUtil.getLocaleDate(m.getYyyymmdd(), lang, country));
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(1);
            	cell.setCellValue(m.getMeterId().toString());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(2);
            	String fName = (m.getMeter() != null) ? m.getMeter().getFriendlyName() : ""; 
            	cell.setCellValue(fName);
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(3);
            	cell.setCellValue(m.getTotal().toString());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(4);
            	cell.setCellValue(m.getBaseValue().toString());
            	cell.setCellStyle(dataCellStyle);
            }
            //End Data
            
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
			// TODO: handle exception
		} //End Try
    }  

    /**
     * @param result
     * @param msgMap
     * @param isLast
     * @param filePath
     * @param fileName
     */
    public void writeReportExcel(List<MeteringListData> result, Map<String, String> msgMap, boolean isLast, String filePath, String fileName) {

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
            HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
            HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);

            MeteringListData resultMap = new MeteringListData();

            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = "Metering Data Report";
            int meteringDataStartRow = 3;
            int totalColumnCnt = 10;
            int dataCount = 0;

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

            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));

            // Title
            row = sheet.createRow(meteringDataStartRow);
   
            //font.setFontHeightInPoints((short)10);
            cell = row.createCell(0);
            cell.setCellValue(msgMap.get("number"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(1);
            cell.setCellValue(msgMap.get("contractNumber"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(2);
            cell.setCellValue(msgMap.get("customername"));
            cell.setCellStyle(titleCellStyle);
			
            cell = row.createCell(3);
            cell.setCellValue(msgMap.get("meteringtime"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(4);
            cell.setCellValue(msgMap.get("usage"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(5);
            if(msgMap.containsKey("thisDayData")) {
                cell.setCellValue(msgMap.get("thisDayData"));
            }
            else {
                cell.setCellValue(msgMap.get("previous"));
            }
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(6);
            cell.setCellValue(msgMap.get("co2formula"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(7);
            cell.setCellValue(msgMap.get("mcuid2"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(8);
            cell.setCellValue(msgMap.get("meterid2"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(9);
            cell.setCellValue(msgMap.get("location"));    
            cell.setCellStyle(titleCellStyle);
            //Title End
            
            //Data
            

            dataCount = result.size();
            for(int i = 0 ; i < dataCount ; i++) {
            	resultMap = result.get(i);
            	row = sheet.createRow(i+ (meteringDataStartRow + 1));      

            	cell = row.createCell(0);
            	cell.setCellValue(resultMap.getNo());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(1);
            	cell.setCellValue(resultMap.getContractNo().toString());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(2);
            	cell.setCellValue(resultMap.getCustomerName().toString());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(3);
            	cell.setCellValue(resultMap.getMeteringTime().toString());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(4);
            	cell.setCellValue(resultMap.getMeteringData());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(5);
            	if(msgMap.containsKey("thisDayData")) {
            		cell.setCellValue(resultMap.getBaseValue());
            	}
            	else {
	            	cell.setCellValue(resultMap.getBeforData());
            	}
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(6);
            	cell.setCellValue(resultMap.getCo2());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(7);
            	cell.setCellValue(resultMap.getMcuNo().toString());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(8);
            	cell.setCellValue(resultMap.getMeterNo().toString());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(9);
            	cell.setCellValue(resultMap.getLocationName().toString());
            	cell.setCellStyle(dataCellStyle);
            }
            //End Data
            
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
			// TODO: handle exception
		} //End Try
    }    	

}
