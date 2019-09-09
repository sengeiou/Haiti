package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * SupplierEMDataMakeExcel.java Description 
 *
 * 
 * Date           Version     Author   Description
 * 2012. 08. 07.   v1.0        jiae         
 *
 */

public class SupplierEMDataMakeExcel {
	@SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(SupplierEMDataMakeExcel.class);
    public SupplierEMDataMakeExcel() {}
    
    public void writeExportExcelEM( List<Map<String, Object>> result, Map<String, String> titleMap, boolean isLast, String filePath, String fileName) {

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

            Map<String, Object> resultMap = new HashMap<String, Object>();

            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            int SupplierStartRow = 0;
            int dataCount = 0;

            HSSFSheet sheet = workbook.createSheet(fileName);

            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 25);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 25);
            sheet.setColumnWidth(colIdx++, 256 * 18);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            
            row = sheet.createRow(SupplierStartRow);
            
            //Title
            cell = row.createCell(0);
            cell.setCellValue(titleMap.get("date"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(1);
            cell.setCellValue(titleMap.get("tariff"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(2);
            cell.setCellValue(titleMap.get("supplySize"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(3);
            cell.setCellValue(titleMap.get("serviceCharge"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(4);
            cell.setCellValue(titleMap.get("govLevy"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(5);
            cell.setCellValue(titleMap.get("publicLevy"));
            cell.setCellStyle(titleCellStyle);            
            
            cell = row.createCell(6);
            cell.setCellValue(titleMap.get("vat"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(7);
            cell.setCellValue(titleMap.get("activeEnergyCharge"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(8);
            cell.setCellValue(titleMap.get("lifeLineSubsidy"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(9);
            cell.setCellValue(titleMap.get("govSubsidy"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(10);
            cell.setCellValue(titleMap.get("additionalSubsidy"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(11);
            cell.setCellValue(titleMap.get("utilityRelief"));
            cell.setCellStyle(titleCellStyle);
            //Title end
            
  
            //Data
            dataCount = result.size();
            for(int i = 0 ; i < dataCount ; i++) {
            	resultMap = result.get(i);
            	row = sheet.createRow(i+ (SupplierStartRow + 1));     
            	
            	cell = row.createCell(0);
            	cell.setCellValue(resultMap.get("YYYYMMDD")==null?"":resultMap.get("YYYYMMDD").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(1);
            	cell.setCellValue((resultMap.get("TARIFFTYPE") == null)?"":resultMap.get("TARIFFTYPE").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(2);
            	cell.setCellValue(displayData(resultMap, titleMap));
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(3);
            	cell.setCellValue((resultMap.get("SERVICECHARGE") == null) ?"": resultMap.get("SERVICECHARGE").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(4);
            	cell.setCellValue((resultMap.get("TRANSMISSIONNETWORKCHARGE") == null) ?"": resultMap.get("TRANSMISSIONNETWORKCHARGE").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(5);
            	cell.setCellValue((resultMap.get("DISTRIBUTIONNETWORKCHARGE") == null) ?"": resultMap.get("DISTRIBUTIONNETWORKCHARGE").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(6);
            	cell.setCellValue((resultMap.get("ENERGYDEMANDCHARGE") == null) ?"": resultMap.get("ENERGYDEMANDCHARGE").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(7);
            	cell.setCellValue((resultMap.get("ACTIVEENERGYCHARGE") == null) ?"": resultMap.get("ACTIVEENERGYCHARGE").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(8);
            	cell.setCellValue((resultMap.get("REACTIVEENERGYCHARGE") == null) ?"": resultMap.get("REACTIVEENERGYCHARGE").toString());
            	cell.setCellStyle(dataCellStyle);
            	
                cell = row.createCell(9);
                cell.setCellValue((resultMap.get("ADMINCHARGE") == null) ?"": resultMap.get("ADMINCHARGE").toString());
                cell.setCellStyle(dataCellStyle);
                
            	cell = row.createCell(10);
            	cell.setCellValue((resultMap.get("RATEREBALANCINGLEVY") == null) ?"": resultMap.get("RATEREBALANCINGLEVY").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(11);
            	cell.setCellValue((resultMap.get("MAXDEMAND") == null) ?"": resultMap.get("MAXDEMAND").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            }
            
            //End Data
            
            //파일 생성
            FileOutputStream fs = null;
            try {
                fs = new FileOutputStream(fileFullPath);
                workbook.write(fs);
            } catch (Exception e) {
            	System.out.println("1:" + e);
                e.printStackTrace();
            } finally {
                if (fs != null) fs.close();
            }
            
		} catch (Exception e) {
			System.out.println("2:" + e);
			e.printStackTrace();
		} //End Try
	    
    } 	
    
    //display
    public String displayData (Map<String, Object> resultMap, Map<String, String> titleMap) {
    	String max = (resultMap.get("SUPPLYSIZEMAX") == null ? "" : resultMap.get("SUPPLYSIZEMAX")).toString();
    	String min = (resultMap.get("SUPPLYSIZEMIN") == null ? "" : resultMap.get("SUPPLYSIZEMIN")).toString();
    	
    	String cond1 = ">".equals(resultMap.get("CONDITION1")) ? "EXCESS" : ">=".equals(resultMap.get("CONDITION1"))  ? "MORE THAN" : "";
    	String cond2 = "<=".equals(resultMap.get("CONDITION2"))  ? "LESS" : "<".equals(resultMap.get("CONDITION2")) ? "BELOW" : "";
    	
//    	String cond1 = ">".equals(resultMap.get("CONDITION1")) ? titleMap.get("excess").toString() : ">=".equals(resultMap.get("CONDITION1"))  ? titleMap.get("morethan").toString() : "";
//    	String cond2 = "<=".equals(resultMap.get("CONDITION2"))  ? titleMap.get("less").toString() : "<".equals(resultMap.get("CONDITION2")) ? titleMap.get("below").toString() : "";
    	
    	if(max == null || "".equals(max)){
    		return min + " " + cond1;
    	}
    	else if(min == null){
            return max + " " + cond2;
    	}
    	else{
            return min + " " + cond1 + " " + max + " " + cond2;
        }
    }
    
}
