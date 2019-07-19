package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;

public class MeterMaxMakeExcel {

    private static Log log = LogFactory.getLog(MeterMaxMakeExcel.class);

    public MeterMaxMakeExcel() {

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

            HSSFRow row = null;
            HSSFCell cell = null;
            HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
            HSSFCellStyle dataCellStyle =  ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
            HSSFCellStyle dataCellStyleSub1 =  ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 2, 0);
            Map<String, String> resultMap = new HashMap<String, String>();
            String fileFullPath = new StringBuilder().append(filePath).append(
                    File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int modemListStartRow = 3;
//            int totalColumnCnt = 15;
            int dataCount = 0;

            HSSFSheet sheet = workbook.createSheet(reportTitle);

            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 7);    // No.
            sheet.setColumnWidth(colIdx++, 256 * 19);   // Meter ID
            sheet.setColumnWidth(colIdx++, 256 * 19);   // Meter Type
            sheet.setColumnWidth(colIdx++, 256 * 15);   // Contract No.
            sheet.setColumnWidth(colIdx++, 256 * 19);   // Customer ID
            sheet.setColumnWidth(colIdx++, 256 * 19);   // Customer Name
            sheet.setColumnWidth(colIdx++, 256 * 35);   // Customer Address
            sheet.setColumnWidth(colIdx++, 256 * 19);   // Status
            sheet.setColumnWidth(colIdx++, 256 * 19);   // DCU ID
            sheet.setColumnWidth(colIdx++, 256 * 20);   // Vendor
            sheet.setColumnWidth(colIdx++, 256 * 20);   // Model
            sheet.setColumnWidth(colIdx++, 256 * 19);   // Modem ID
            sheet.setColumnWidth(colIdx++, 256 * 20);   // Modem Model
            sheet.setColumnWidth(colIdx++, 256 * 25);   // Last Comm. Date
            sheet.setColumnWidth(colIdx++, 256 * 20);   // Location
            sheet.setColumnWidth(colIdx++, 256 * 20);   // Install Property
            sheet.setColumnWidth(colIdx++, 256 * 15);   // Install ID
            sheet.setColumnWidth(colIdx++, 256 * 35);   // Meter Address
            sheet.setColumnWidth(colIdx++, 256 * 25);   // Transformer Ratio
            sheet.setColumnWidth(colIdx++, 256 * 15);   // CT
            sheet.setColumnWidth(colIdx++, 256 * 19);   // SW/HW(Ver)

            int totalColumnCnt = colIdx;

            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));

            // Title
            row = sheet.createRow(modemListStartRow);

            int cellCnt = 0;
            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("no"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("meterid"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("metertype"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("contractNumber"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("customerId"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("customerName"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("customerAddress"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("status"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("mcuid"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("vendor"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("model"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("modemid"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("modemModel"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("lastcomm"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("location"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("installProperty"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("installId"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("meterAddress"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("transformerRatio"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("ct"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("ver"));
            cell.setCellStyle(titleCellStyle);
            // Title End

            // Data

            dataCount = result.size();

            for (int i = 0; i < dataCount; i++) {
                resultMap = (Map<String, String>) result.get(i);
                row = sheet.createRow(i + (modemListStartRow + 1));
                int cellCnt2 = 0;

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(i + 1);
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("meterMds")==null?"":resultMap.get("meterMds").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("meterType")==null?"":resultMap.get("meterType").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("contractNumber")==null?"":resultMap.get("contractNumber").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("customerId")==null?"":resultMap.get("customerId").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("customerName")==null?"":resultMap.get("customerName").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("customerAddress")==null?"":resultMap.get("customerAddress").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("commStatus")==null?"":resultMap.get("commStatus").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("mcuSysID")==null?"":resultMap.get("mcuSysID").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("vendorName")==null?"":resultMap.get("vendorName").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("modelName")==null?"":resultMap.get("modelName").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("modemId")==null?"":resultMap.get("modemId").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("modemModelName")==null?"":resultMap.get("modemModelName").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("lastCommDate")==null?"":resultMap.get("lastCommDate").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("locName")==null?"":resultMap.get("locName").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("installProperty")==null?"":resultMap.get("installProperty").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("installId")==null?"":resultMap.get("installId").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("meterAddress")==null?"":resultMap.get("meterAddress").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("transformerRatio")==null?"":resultMap.get("transformerRatio").toString());
                cell.setCellStyle(dataCellStyleSub1);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("ct")==null?"":resultMap.get("ct").toString());
                cell.setCellStyle(dataCellStyleSub1);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("ver")==null?"":resultMap.get("ver").toString());
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
            log.error(e, e); 
        } // End Try
    }
    
    @SuppressWarnings("unchecked")
    public void writeCommInfoReportExcel(List<Object> result, Map<String, String> msgMap, boolean isLast, String filePath, String fileName) {
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
            HSSFCellStyle dataCellStyle =  ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
            HSSFCellStyle dataCellStyleSub1 =  ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 2, 0);
            
            Map<String, String> resultMap = new HashMap<String, String>();
            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int modemListStartRow = 3;
            int dataCount = 0;

            HSSFSheet sheet = workbook.createSheet(reportTitle);

            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 10);	// 0 No.
			sheet.setColumnWidth(colIdx++, 256 * 22);	// 1 DCU ID
			sheet.setColumnWidth(colIdx++, 256 * 19);	// 2 A24h
			sheet.setColumnWidth(colIdx++, 256 * 19);	// 3 NA24~48h
			sheet.setColumnWidth(colIdx++, 256 * 19);	// 4 NA48h
			sheet.setColumnWidth(colIdx++, 256 * 19);	// 5 Unknown
			sheet.setColumnWidth(colIdx++, 256 * 19);	// 6 CommError
			sheet.setColumnWidth(colIdx++, 256 * 19);	// 7 SecurityError
			sheet.setColumnWidth(colIdx++, 256 * 19);	// 8 PowerDown

            int totalColumnCnt = colIdx;

            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));

            // Title (S)
            row = sheet.createRow(modemListStartRow);

            int cellCnt = 0;
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("no"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("mcuid"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("activity24"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("noActivity24"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("noActivity48"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("unknown"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("commError"));
			cell.setCellStyle(titleCellStyle);
						
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("securityError"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("powerDown"));
			cell.setCellStyle(titleCellStyle);
			// Title (E)

			// Data (S)
			dataCount = result.size();

			for (int i = 0; i < dataCount; i++) {
				resultMap = (Map<String, String>) result.get(i);
				row = sheet.createRow(i + (modemListStartRow + 1));  
				int cellCnt2 = 0;
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(i + 1);				
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("mcuSysId")==null?"":resultMap.get("mcuSysId").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("activity24")==null?"":resultMap.get("activity24").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("noActivity24")==null?"":resultMap.get("noActivity24").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("noActivity48")==null?"":resultMap.get("noActivity48").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("unknown")==null?"":resultMap.get("unknown").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("commError")==null?"":resultMap.get("commError").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("securityError")==null?"":resultMap.get("securityError").toString());
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("powerDown")==null?"":resultMap.get("powerDown").toString());
				cell.setCellStyle(dataCellStyle);
			}
			// Data (E)

            // 파일 생성
            FileOutputStream fs = null;
            try {
                fs = new FileOutputStream(fileFullPath);
                workbook.write(fs);
            } catch (Exception e) {
            	log.error(e, e);
			} finally {
				if (fs != null)
					fs.close();
			}
		} catch (Exception e) {
			log.error(e, e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void writeShipmentReportExcel(List<Object> result, Map<String, String> msgMap, boolean isLast, String filePath, String fileName) {
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
    		HSSFCellStyle dataCellStyle =  ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
    		HSSFCellStyle dataCellStyleSub1 =  ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 2, 0);
			HSSFCellStyle dateDataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
			CreationHelper createHelper = workbook.getCreationHelper();
			dateDataCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd hh:mm:ss"));
			dateDataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    		
    		titleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    		titleCellStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
			titleCellStyle.setBorderBottom(CellStyle.BORDER_DOUBLE);
			dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			dataCellStyleSub1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
    		Map<String, String> resultMap = new HashMap<String, String>();
    		String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
    		final String reportTitle = msgMap.get("msg_title");
    		int modemListStartRow = 0;
    		int dataCount = 0;

    		HSSFSheet sheet = workbook.createSheet(reportTitle);

    		int colIdx = 0;
    		sheet.setColumnWidth(colIdx++, 256 * 20);	// Type
    		sheet.setColumnWidth(colIdx++, 256 * 20);	// EUI ID
			sheet.setColumnWidth(colIdx++, 256 * 20);	// GS1 Code
			sheet.setColumnWidth(colIdx++, 256 * 20);	// Model
			sheet.setColumnWidth(colIdx++, 256 * 15);	// HW Version
			sheet.setColumnWidth(colIdx++, 256 * 15);	// SW Version
			sheet.setColumnWidth(colIdx++, 256 * 30);	// Production Date
			
    		int totalColumnCnt = colIdx;

    		// Title 표시 영역  (S)
            row = sheet.createRow(modemListStartRow);

 			cell = row.createCell(0);
 			cell.setCellValue(msgMap.get("msg_type"));
 			cell.setCellStyle(titleCellStyle);

 			cell = row.createCell(1);
 			cell.setCellValue(msgMap.get("msg_euiId"));
 			cell.setCellStyle(titleCellStyle);
 			
 			cell = row.createCell(2);
 			cell.setCellValue(msgMap.get("msg_gs1"));
 			cell.setCellStyle(titleCellStyle);
 			
 			cell = row.createCell(3);
 			cell.setCellValue(msgMap.get("msg_model"));
 			cell.setCellStyle(titleCellStyle);
 			
 			cell = row.createCell(4);
 			cell.setCellValue(msgMap.get("msg_hwVer"));
 			cell.setCellStyle(titleCellStyle);
 			
 			cell = row.createCell(5);
 			cell.setCellValue(msgMap.get("msg_swVer"));
 			cell.setCellStyle(titleCellStyle);
 			
 			cell = row.createCell(6);
 			cell.setCellValue(msgMap.get("msg_productionDate"));
 			cell.setCellStyle(titleCellStyle);
 			// Title 표시 영역  (E)

    		// Data 표시 영역 (S)
    		dataCount = result.size();

    		for (int i = 0; i < dataCount; i++) {
    			resultMap = (Map<String, String>) result.get(i);
    			row = sheet.createRow(i + (modemListStartRow + 1));
    			int cellCnt2 = 0;

    			cell = row.createCell(cellCnt2++);
    			cell.setCellValue(resultMap.get("meterType")==null?"":resultMap.get("meterType").toString());
    			cell.setCellStyle(dataCellStyle);

    			cell = row.createCell(cellCnt2++);
    			cell.setCellValue(resultMap.get("meterMds")==null?"":resultMap.get("meterMds").toString());
    			cell.setCellStyle(dataCellStyle);
    			
    			cell = row.createCell(cellCnt2++);
            	cell.setCellValue((resultMap.get("gs1") != null) ? (String) resultMap.get("gs1") : "");
            	cell.setCellStyle(dataCellStyle);
            	
    			cell = row.createCell(cellCnt2++);
    			cell.setCellValue(resultMap.get("modelName")==null?"":resultMap.get("modelName").toString());
    			cell.setCellStyle(dataCellStyle);
    			
    			cell = row.createCell(cellCnt2++);
    			cell.setCellValue((resultMap.get("hwVer") != null) ? (String) resultMap.get("hwVer") : "");
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
            	cell.setCellValue((resultMap.get("swVer") != null) ? (String) resultMap.get("swVer") : "");
            	cell.setCellStyle(dataCellStyle);
            	
				/** Convert 'yyyyMMdd' to 'yyyy-MM-dd HH:mm:ss' (S) */
				// DB에 yyyyMMdd Format으로 저장되어있는 데이터, yyyy-MM-dd HH:mm:ss Format으로 변환하여 Excel에 기재
				cell = row.createCell(cellCnt2++);
				String manufacturedDate = (resultMap.get("manufacturedDate") != null) ? (String) resultMap.get("manufacturedDate") : "";
				SimpleDateFormat recvSimpleFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
				SimpleDateFormat tranSimpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

				if (!manufacturedDate.isEmpty()) {
					try {
						Date data = recvSimpleFormat.parse(manufacturedDate);
						manufacturedDate = tranSimpleFormat.format(data);
					} catch (ParseException e) {
						manufacturedDate = "";
						e.printStackTrace();
					}
				}
				
				cell.setCellValue(manufacturedDate);
				cell.setCellStyle(dateDataCellStyle);
				/** Convert 'yyyyMMdd' to 'yyyy-MM-dd HH:mm:ss' (E) */
    		}
    		// Data 표시 영역 (E)

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
    		log.error(e.toString(), e);
    	}
    	
    }

    @SuppressWarnings("unused")
    @Deprecated
    private String getCommStatus(Object objStr, Map<String, String> msgMap){
        String commStatus = "";

        if(objStr == null) return "";

        if(objStr.toString().equals("fmtMessage00")){           //fmtMessage00
            commStatus = msgMap.get("normal");
        } else if(objStr.toString().equals("fmtMessage24")){    //fmtMessage24
            commStatus = msgMap.get("commstateYellow");
        } else if(objStr.toString().equals("fmtMessage48")){    //fmtMessage48
            commStatus = msgMap.get("commstateRed");
        }

        return commStatus;
    }

    @SuppressWarnings("unused")
    private String getDurationFormat(String sec, Map<String, String> msgMap){
        int duration = Integer.parseInt(sec);

        int s;
        int m;
        int h;
        int d;

        String dayStr   = msgMap.get("day").toString();
        String hourStr  = msgMap.get("hour").toString();
        String minStr   = msgMap.get("min").toString();
        String secStr   = msgMap.get("sec").toString();

        s = duration % 60;      // 초
        duration = duration / 60;
        m = duration % 60;      // 분
        duration = duration / 60;
        h = duration % 24;      // 시
        d = duration / 24;      // 일


        if(d > 0) return d + dayStr + " " + h + hourStr + " " + m + minStr + " " + s + secStr;
        else if(h > 0) return h + hourStr + " " + m + minStr + " " + s + secStr;
        else if(m > 0) return m + minStr + " " + s + secStr;
        else return s + secStr;
    }

}
