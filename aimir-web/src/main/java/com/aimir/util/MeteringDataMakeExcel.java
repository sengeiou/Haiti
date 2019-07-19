package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
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
import org.apache.poi.ss.util.CellRangeAddress;

import com.aimir.constants.CommonConstants.DateType;

/**
 * MeteringDataMakeExcel.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2012. 4. 26.   v1.0       enj         
 *
 */
public class MeteringDataMakeExcel {
	private static Log logger = LogFactory.getLog(MeteringDataMakeExcel.class);

    public MeteringDataMakeExcel() {

    }

    /**
     * @param result
     * @param msgMap
     * @param isLast
     * @param filePath
     * @param fileName
     */
    public void writeReportExcel(List<Map<String, Object>> result, Map<String, String> msgMap, boolean isLast, String filePath, String fileName, DateType dateType, String mvmMiniType, String supplierName) {

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
            HSSFCellStyle noCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 1, 0);
            HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
            HSSFCellStyle data2CellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 2, 0);
            
//            MeteringListData resultMap = new MeteringListData();
            Map<String, Object> resultMap = new HashMap<String, Object>();

            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int meteringDataStartRow = 3;
//            int totalColumnCnt = 10;
            
            int totalColumnCnt = 8;
			if("대성에너지".equals(supplierName)) {
            	totalColumnCnt = 9;
            }
            
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
            
            if(("대성에너지".equals(supplierName)) && (dateType == DateType.HOURLY || dateType == DateType.DAILY || dateType == DateType.WEEKLY || dateType == DateType.MONTHLY)) 
            	sheet.setColumnWidth(colIdx++, 256 * 19);
//            sheet.setColumnWidth(colIdx++, 256 * 19);
//            sheet.setColumnWidth(colIdx++, 256 * 19);

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
            cell.setCellValue(msgMap.get("customerName"));
            cell.setCellStyle(titleCellStyle);
			
            cell = row.createCell(3);
            cell.setCellValue(msgMap.get("meteringTime"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(4);
            cell.setCellValue(msgMap.get("usage"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(5);
            cell.setCellValue(msgMap.get("previous"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(6);
            cell.setCellValue(msgMap.get("meterId"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(7);
            cell.setCellValue(msgMap.get("modemId"));
            cell.setCellStyle(titleCellStyle);

            if(("대성에너지".equals(supplierName) && (dateType == DateType.HOURLY || dateType == DateType.DAILY || dateType == DateType.WEEKLY || dateType == DateType.MONTHLY))) {
	            cell = row.createCell(8);
	            cell.setCellValue(msgMap.get("accumulate"));
	            cell.setCellStyle(titleCellStyle);
	    	}
            //Title End
            
            //Data
            dataCount = result.size();
            for(int i = 0 ; i < dataCount ; i++) {
                resultMap = result.get(i);
                row = sheet.createRow(i+ (meteringDataStartRow + 1));      

                cell = row.createCell(0);
                cell.setCellValue(resultMap.get("num").toString());
                cell.setCellStyle(noCellStyle);

                cell = row.createCell(1);
                cell.setCellValue((String)resultMap.get("contractNumber"));
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(2);
                cell.setCellValue((String)resultMap.get("customerName"));
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(3);
                cell.setCellValue((String)resultMap.get("meteringTime"));
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(4);
                cell.setCellValue((String)resultMap.get("value"));
                cell.setCellStyle(data2CellStyle);

                cell = row.createCell(5);
                cell.setCellValue((String)resultMap.get("prevValue"));
                cell.setCellStyle(data2CellStyle);

                cell = row.createCell(6);
                cell.setCellValue((String)resultMap.get("meterNo"));
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(7);
                cell.setCellValue((String)resultMap.get("modemId"));
                cell.setCellStyle(dataCellStyle);
                
                if(("대성에너지".equals(supplierName) && (dateType == DateType.HOURLY || dateType == DateType.DAILY || dateType == DateType.WEEKLY || dateType == DateType.MONTHLY))) {
	                cell = row.createCell(8);
	                cell.setCellValue((String)resultMap.get("accumulateValue"));
	                cell.setCellStyle(dataCellStyle);
                }
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
		    e.printStackTrace();
		} //End Try
    }
    
    /**
     * @param result
     * @param msgMap
     * @param isLast
     * @param filePath
     * @param fileName
     */
    public void writeReportExcelForMeterValue(List<Map<String, Object>> result, Map<String, String> msgMap, boolean isLast, String filePath, String fileName, DateType dateType, String mvmMiniType, String supplierName) {
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
            HSSFCellStyle noCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 1, 0);
            HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
            HSSFCellStyle data2CellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 2, 0);

            Map<String, Object> resultMap = new HashMap<String, Object>();

            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int meteringDataStartRow = 3;
            
            int totalColumnCnt = 8;
			if(dateType == DateType.MONTHLY) {
            	totalColumnCnt = 10;
            }
            
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
            
            if(dateType == DateType.MONTHLY) {
               	sheet.setColumnWidth(colIdx++, 256 * 19);
               	sheet.setColumnWidth(colIdx++, 256 * 19);
            }

            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));

            // Title
            row = sheet.createRow(meteringDataStartRow);
   
            cell = row.createCell(0);
            cell.setCellValue(msgMap.get("number"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(1);
            cell.setCellValue(msgMap.get("contractNumber"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(2);
            cell.setCellValue(msgMap.get("customerName"));
            cell.setCellStyle(titleCellStyle);
			
            cell = row.createCell(3);
            cell.setCellValue(msgMap.get("meteringTime"));
            cell.setCellStyle(titleCellStyle);
            
            if(dateType == DateType.MONTHLY) {
            	cell = row.createCell(4);
                cell.setCellValue(msgMap.get("meterValue2"));
                cell.setCellStyle(titleCellStyle);
                
                cell = row.createCell(5);
                cell.setCellValue(msgMap.get("prevMeterValue"));
                cell.setCellStyle(titleCellStyle);
                
                cell = row.createCell(6);
                cell.setCellValue(msgMap.get("usage"));
                cell.setCellStyle(titleCellStyle);
                
                cell = row.createCell(7);
                cell.setCellValue(msgMap.get("prevUsage"));
                cell.setCellStyle(titleCellStyle);
                
                cell = row.createCell(8);
                cell.setCellValue(msgMap.get("meterId"));
                cell.setCellStyle(titleCellStyle);
                
                cell = row.createCell(9);
                cell.setCellValue(msgMap.get("modemId"));
                cell.setCellStyle(titleCellStyle);
            } else {
            	cell = row.createCell(4);
                cell.setCellValue(msgMap.get("meterValue2"));
                cell.setCellStyle(titleCellStyle);
                
                cell = row.createCell(5);
                cell.setCellValue(msgMap.get("usage"));
                cell.setCellStyle(titleCellStyle);
                
                cell = row.createCell(6);
                cell.setCellValue(msgMap.get("meterId"));
                cell.setCellStyle(titleCellStyle);
                
                cell = row.createCell(7);
                cell.setCellValue(msgMap.get("modemId"));
                cell.setCellStyle(titleCellStyle);
                
            }
            
            //Title End
            
            //Data
            dataCount = result.size();
            for(int i = 0 ; i < dataCount ; i++) {
                resultMap = result.get(i);
                row = sheet.createRow(i+ (meteringDataStartRow + 1));      

                cell = row.createCell(0);
                cell.setCellValue(resultMap.get("num").toString());
                cell.setCellStyle(noCellStyle);

                cell = row.createCell(1);
                cell.setCellValue((String)resultMap.get("contractNumber"));
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(2);
                cell.setCellValue((String)resultMap.get("customerName"));
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(3);
                cell.setCellValue((String)resultMap.get("meteringTime"));
                cell.setCellStyle(dataCellStyle);

                if(dateType == DateType.MONTHLY) {
                	cell = row.createCell(4);
                    cell.setCellValue((String)resultMap.get("value"));
                    cell.setCellStyle(data2CellStyle);

                    cell = row.createCell(5);
                    cell.setCellValue((String)resultMap.get("prevValue"));
                    cell.setCellStyle(data2CellStyle);
                    
                    cell = row.createCell(6);
                    cell.setCellValue((String)resultMap.get("usage"));
                    cell.setCellStyle(data2CellStyle);

                    cell = row.createCell(7);
                    cell.setCellValue((String)resultMap.get("prevUsage"));
                    cell.setCellStyle(data2CellStyle);

                    cell = row.createCell(8);
                    cell.setCellValue((String)resultMap.get("meterNo"));
                    cell.setCellStyle(dataCellStyle);

                    cell = row.createCell(9);
                    cell.setCellValue((String)resultMap.get("modemId"));
                    cell.setCellStyle(dataCellStyle);
                }else {
                    cell = row.createCell(4);
                    cell.setCellValue((String)resultMap.get("value"));
                    cell.setCellStyle(data2CellStyle);

                    cell = row.createCell(5);
                    cell.setCellValue((String)resultMap.get("usage"));
                    cell.setCellStyle(data2CellStyle);

                    cell = row.createCell(6);
                    cell.setCellValue((String)resultMap.get("meterNo"));
                    cell.setCellStyle(dataCellStyle);

                    cell = row.createCell(7);
                    cell.setCellValue((String)resultMap.get("modemId"));
                    cell.setCellStyle(dataCellStyle);
                }
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
		    e.printStackTrace();
		} //End Try
    }
    
    public void writeDetailReportExcel(List<Map<String, Object>> result, Map<String, String> msgMap, String filePath, String fileName, String searchType) {
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
    		HSSFCellStyle noCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 1, 0);
    		HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
    		HSSFCellStyle data2CellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 2, 0);
    		
			Map<String, Object> resultMap = new HashMap<String, Object>();
    		List<String> keyList = new ArrayList<String>();
    		
    		String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
    		
    		final String reportTitle = msgMap.get("msg_title");
    		final String msg_meterTime = msgMap.get("msg_meterTime");
    		final String msg_activeImp = msgMap.get("msg_01");
    		final String msg_activeExp = msgMap.get("msg_02");
    		final String msg_reactiveImp = msgMap.get("msg_03");
    		final String msg_reactiveExp = msgMap.get("msg_04");
    		int meteringDataStartRow = 3;
    		int totalColumnCnt = 5;
    		int colIdx = 0;
    		
    		HSSFSheet sheet = workbook.createSheet(reportTitle);
    		sheet.setColumnWidth(colIdx++, 256 * 25);
    		sheet.setColumnWidth(colIdx++, 256 * 25);
    		sheet.setColumnWidth(colIdx++, 256 * 25);
    		sheet.setColumnWidth(colIdx++, 256 * 25);
    		sheet.setColumnWidth(colIdx++, 256 * 25);

    		row = sheet.createRow(0);
    		cell = row.createCell(0);
    		cell.setCellValue(reportTitle);
    		cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
    		sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt-1)));

    		 
    		// Title Section (S)
    		row = sheet.createRow(meteringDataStartRow);

			if (searchType == "RATE") {
    			cell = row.createCell(0);
        		cell.setCellValue(msg_meterTime);
        		cell.setCellStyle(titleCellStyle);

        		cell = row.createCell(1);
        		cell.setCellValue(msg_activeImp);
        		cell.setCellStyle(titleCellStyle);
        		
        		cell = row.createCell(2);
        		cell.setCellValue(msg_activeExp);
        		cell.setCellStyle(titleCellStyle);
        		
        		cell = row.createCell(3);
        		cell.setCellValue(msg_reactiveImp);
        		cell.setCellStyle(titleCellStyle);
			} else {
    			cell = row.createCell(0);
        		cell.setCellValue(msg_meterTime);
        		cell.setCellStyle(titleCellStyle);

        		cell = row.createCell(1);
        		cell.setCellValue(msg_activeImp);
        		cell.setCellStyle(titleCellStyle);
        		
        		cell = row.createCell(2);
        		cell.setCellValue(msg_activeExp);
        		cell.setCellStyle(titleCellStyle);
        		
        		cell = row.createCell(3);
        		cell.setCellValue(msg_reactiveImp);
        		cell.setCellStyle(titleCellStyle);
        		
        		cell = row.createCell(4);
        		cell.setCellValue(msg_activeExp);
        		cell.setCellStyle(titleCellStyle);
    		}
    		// Title Section (E)
    		
    		// Data Set Logic (S)
    		String meteringTime = null;
    		
			if (searchType == "RATE") {
				for (int i = 0; i < result.size(); i++) {
	    			resultMap = result.get(i);
	    			
					if (!resultMap.get("localeDate").toString().equals(meteringTime)) {
	    				meteringTime = resultMap.get("localeDate").toString();
	    				keyList.add(meteringTime);
	    			}
	    		}
	    		
	    		for (int i = 0; i < keyList.size(); i++) {
					row = sheet.createRow(i + (meteringDataStartRow + 1)); 

					cell = row.createCell(0);
					cell.setCellValue(keyList.get(i));
					cell.setCellStyle(noCellStyle);
		    		
					for (int j = 0; j < result.size(); j++) {
						int index = j + 1;
						resultMap = result.get(j);
						
						if (resultMap.get("localeDate").toString().equals(keyList.get(i))) {
							if (resultMap.get("rateIndex").toString().equals("1")) {
								cell = row.createCell(Integer.parseInt(resultMap.get("rateIndex").toString()));
								cell.setCellValue(resultMap.get("decimalValue").toString());
								cell.setCellStyle(dataCellStyle);
							} else if (resultMap.get("rateIndex").toString().equals("2")) {
								cell = row.createCell(Integer.parseInt(resultMap.get("rateIndex").toString()));
								cell.setCellValue(resultMap.get("decimalValue").toString());
								cell.setCellStyle(dataCellStyle);
							} else if (resultMap.get("rateIndex").toString().equals("3")) {
								cell = row.createCell(Integer.parseInt(resultMap.get("rateIndex").toString()));
								cell.setCellValue(resultMap.get("decimalValue").toString());
								cell.setCellStyle(dataCellStyle);
							}
						}
					}
				}
    		} else {
    			for (int i = 0; i < result.size(); i++) {
        			resultMap = result.get(i);
        			
        			// ch1, ch2, ch3, ch4을 묶을 key값을 추출하여 keyList에 담는다
    				if (!resultMap.get("reportDate").toString().equals(meteringTime)) {
        				meteringTime = resultMap.get("reportDate").toString();
        				keyList.add(meteringTime);
        			}
        		}
        		
        		for (int i = 0; i < keyList.size(); i++) {
    				row = sheet.createRow(i + (meteringDataStartRow + 1)); 

    				cell = row.createCell(0);
    				cell.setCellValue(keyList.get(i));
    				cell.setCellStyle(noCellStyle);
    	    		
    				for (int j = 0; j < result.size(); j++) {
    					int index = j + 1;
    					resultMap = result.get(j);
    					
    					if (resultMap.get("reportDate").toString().equals(keyList.get(i))) {
    						if (resultMap.get("channel").toString().equals("1")) {
    							cell = row.createCell(Integer.parseInt(resultMap.get("channel").toString().toString()));
    							cell.setCellValue(resultMap.get("decimalValue").toString());
    							cell.setCellStyle(dataCellStyle);
    						} else if (resultMap.get("channel").toString().equals("2")) {
    							cell = row.createCell(Integer.parseInt(resultMap.get("channel").toString().toString()));
    							cell.setCellValue(resultMap.get("decimalValue").toString());
    							cell.setCellStyle(dataCellStyle);
    						} else if (resultMap.get("channel").toString().equals("3")) {
    							cell = row.createCell(Integer.parseInt(resultMap.get("channel").toString().toString()));
    							cell.setCellValue(resultMap.get("decimalValue").toString());
    							cell.setCellStyle(dataCellStyle);
    						} else if (resultMap.get("channel").toString().equals("4")) {
    							cell = row.createCell(Integer.parseInt(resultMap.get("channel").toString().toString()));
    							cell.setCellValue(resultMap.get("decimalValue").toString());
    							cell.setCellStyle(dataCellStyle);
    						}
    					}
    				}
    			}
    			
    		}
			
    		// 파일 생성
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
    		logger.error(e, e);
    	}
    }
}