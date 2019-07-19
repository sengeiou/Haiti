package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

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
import org.owasp.esapi.ESAPI;

import com.aimir.constants.CommonConstants;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.MCU;

/**
 * McuDataMakeExcel.java Description 
 *
 * 
 * Date           Version     Author   Description
 * 2012. 06. 27.   v1.0        jiae         
 * 	
 */
public class McuDataMakeExcel {
	@SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(McuDataMakeExcel.class);
    public McuDataMakeExcel() {
    }

    /**
     * @param result
     * @param msgMap
     * @param isLast
     * @param filePath
     * @param fileName
     */
    @Deprecated
    public void writeReportExcelTemp(List<MCU> result, Map<String, String> msgMap, boolean isLast, String filePath,
            String fileName) {

        try {
            HSSFWorkbook workbook = new HSSFWorkbook();

            HSSFFont fontTitle = workbook.createFont();
            fontTitle.setFontHeightInPoints((short) 14);
            fontTitle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFFont fontHeader = workbook.createFont();
            fontHeader.setFontHeightInPoints((short) 10);
            fontHeader.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFFont fontBody = workbook.createFont();
            fontBody.setFontHeightInPoints((short) 10);
            fontBody.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);

            HSSFRow row = null;
            HSSFCell cell = null;
            HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 1, 0, 0, 0, 0);
            HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);

            MCU resultMap = new MCU();

            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int ConcentratorStartRow = 3;
            int totalColumnCnt = 15;
            int dataCount = 0;

            HSSFSheet sheet = workbook.createSheet(reportTitle);
            
            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 4);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 20);
            sheet.setColumnWidth(colIdx++, 256 * 17);
            sheet.setColumnWidth(colIdx++, 256 * 17);
            sheet.setColumnWidth(colIdx++, 256 * 22);
            sheet.setColumnWidth(colIdx++, 256 * 22);
            sheet.setColumnWidth(colIdx++, 256 * 15);
            sheet.setColumnWidth(colIdx++, 256 * 25);

            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 1, 1, 1, 1, 1, 0, 0, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt - 1)));

            // Title
            row = sheet.createRow(ConcentratorStartRow);

            // font.setFontHeightInPoints((short)10);
            cell = row.createCell(0);
            cell.setCellValue(msgMap.get("number"));
            cell.setCellStyle(titleCellStyle);

            // MDIS_집중기타입
            cell = row.createCell(1);
            cell.setCellValue(msgMap.get("mcuTypeFmt"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(2);
            cell.setCellValue(msgMap.get("mcuId2"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(3);
            cell.setCellValue(msgMap.get("mcuName"));
            cell.setCellStyle(titleCellStyle);

            // MDIS_제조사
            cell = row.createCell(4);
            cell.setCellValue(msgMap.get("vendor"));
            cell.setCellStyle(titleCellStyle);

            // MDIS_모델
            cell = row.createCell(5);
            cell.setCellValue(msgMap.get("model"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(6);
            cell.setCellValue(msgMap.get("mcuMobile"));
            cell.setCellStyle(titleCellStyle);

            // MDIS_지역
            cell = row.createCell(7);
            cell.setCellValue(msgMap.get("location"));
            cell.setCellStyle(titleCellStyle);
            
            // SYS_LOCATION
            

            cell = row.createCell(8);
            cell.setCellValue(msgMap.get("ipAddress"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(9);
            cell.setCellValue(msgMap.get("swVer"));
            cell.setCellStyle(titleCellStyle);

            // MDIS_하드웨어Ver
            cell = row.createCell(10);
            cell.setCellValue(msgMap.get("hwVer"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(11);
            cell.setCellValue(msgMap.get("installation"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(12);
            cell.setCellValue(msgMap.get("lastCommDate"));
            cell.setCellStyle(titleCellStyle);

            // MDIS_통신타입
            cell = row.createCell(13);
            cell.setCellValue(msgMap.get("protocolType"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(14);
            cell.setCellValue(msgMap.get("CommStatus"));
            cell.setCellStyle(titleCellStyle);
            
            // Title End

            // Data

            dataCount = result.size();
            for (int i = 0; i < dataCount; i++) {
                resultMap = (MCU) result.get(i);
                row = sheet.createRow(i + (ConcentratorStartRow + 1));

                cell = row.createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(dataCellStyle);

                // MDIS_집중기타입
                cell = row.createCell(1);
                cell.setCellValue((resultMap.getMcuType() != null) ? resultMap.getMcuType().getDescr() : "");
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(2);
                cell.setCellValue(resultMap.getSysID());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(3);
                cell.setCellValue(resultMap.getSysName());
                cell.setCellStyle(dataCellStyle);

                // MDIS_제조사
                cell = row.createCell(4);
                cell.setCellValue(resultMap.getDeviceModel() == null ? "" : resultMap.getDeviceModel().getDeviceVendor()
                        .getName());
                cell.setCellStyle(dataCellStyle);

                // MDIS_모델
                cell = row.createCell(5);
                cell.setCellValue(resultMap.getDeviceModel() == null ? "" : resultMap.getDeviceModel().getName());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(6);
                cell.setCellValue(resultMap.getSysPhoneNumber());
                cell.setCellStyle(dataCellStyle);

                // MDIS_지역 - problem 최종통신일시로 title이 찍힘
                cell = row.createCell(7);
                cell.setCellValue(resultMap.getLocation() == null ? "" : resultMap.getLocation().getName());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(8);
                cell.setCellValue(resultMap.getIpAddr());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(9);
                cell.setCellValue(resultMap.getSysSwVersion());
                cell.setCellStyle(dataCellStyle);

                // MDIS_하드웨어Ver
                cell = row.createCell(10);
                cell.setCellValue(resultMap.getSysHwVersion());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(11);
                cell.setCellValue(CountryDateType(resultMap.getInstallDate()));
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(12);
                cell.setCellValue(CountryDateType(resultMap.getLastCommDate()));
                cell.setCellStyle(dataCellStyle);

                // MDIS_통신타입
                cell = row.createCell(13);
                cell.setCellValue((resultMap.getProtocolType() != null) ? resultMap.getProtocolType().getDescr() : "");
                cell.setCellStyle(dataCellStyle);

                // 상태정보 표시
                String CommStatus = FormatDate(resultMap.getLastCommDate());
                if (CommStatus.equals("normal")) {
                    CommStatus = msgMap.get("normal");
                } else if (CommStatus.equals("HH24over")) {
                    CommStatus = msgMap.get("HH24over");
                } else if (CommStatus.equals("HH48over")) {
                    CommStatus = msgMap.get("HH48over");
                } else if (CommStatus.length() == 0) {
                    CommStatus = "";
                }

                cell = row.createCell(14);
                cell.setCellValue(CommStatus);
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
            e.getStackTrace();
        } // End Try

    }

    /**
     * @param result
     * @param msgMap
     * @param isLast
     * @param filePath
     * @param fileName
     */
    public void writeReportExcel(List<Map<String, Object>> result, Map<String, String> msgMap, boolean isLast, String filePath,
            String fileName) {

        try {
            HSSFWorkbook workbook = new HSSFWorkbook();

            HSSFFont fontTitle = workbook.createFont();
            fontTitle.setFontHeightInPoints((short) 14);
            fontTitle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFFont fontHeader = workbook.createFont();
            fontHeader.setFontHeightInPoints((short) 10);
            fontHeader.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFFont fontBody = workbook.createFont();
            fontBody.setFontHeightInPoints((short) 10);
            fontBody.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);

            HSSFRow row = null;
            HSSFCell cell = null;
            HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
            HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);

            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int ConcentratorStartRow = 3;
            int totalColumnCnt = 17;

            HSSFSheet sheet = workbook.createSheet(reportTitle);

            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 4);	// 0    No
            sheet.setColumnWidth(colIdx++, 256 * 15);	// 1    DCU Type
            sheet.setColumnWidth(colIdx++, 256 * 19);	// 2    DCU ID
            sheet.setColumnWidth(colIdx++, 256 * 19);	// 3    DCU Serial
            sheet.setColumnWidth(colIdx++, 256 * 19);	// 4    DCU Name
            sheet.setColumnWidth(colIdx++, 256 * 20);	// 5    Vendor
            sheet.setColumnWidth(colIdx++, 256 * 20);	// 6    Model
            sheet.setColumnWidth(colIdx++, 256 * 20);	// 7    Location
            sheet.setColumnWidth(colIdx++, 256 * 30);	// 8    Installed Location
            sheet.setColumnWidth(colIdx++, 256 * 30);	// 9    IP
            sheet.setColumnWidth(colIdx++, 256 * 30);	// 10   Ipv6
            sheet.setColumnWidth(colIdx++, 256 * 17);	// 11   SW Ver
            sheet.setColumnWidth(colIdx++, 256 * 17);	// 12   HW Ver
            sheet.setColumnWidth(colIdx++, 256 * 22);	// 13   Installation Date
            sheet.setColumnWidth(colIdx++, 256 * 22);	// 14   Last Comm. Date
            sheet.setColumnWidth(colIdx++, 256 * 15);	// 15   Protocol Type
            sheet.setColumnWidth(colIdx++, 256 * 25);	// 16   Comm. Status
            sheet.setColumnWidth(colIdx++, 256 * 20);	// 17   Mac Address
            
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt - 1)));

            // Title
            row = sheet.createRow(ConcentratorStartRow);

            cell = row.createCell(0);
            cell.setCellValue(msgMap.get("number"));
            cell.setCellStyle(titleCellStyle);

            // MDIS_집중기타입
            cell = row.createCell(1);
            cell.setCellValue(msgMap.get("mcuTypeFmt"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(2);
            cell.setCellValue(msgMap.get("mcuId2"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(3);
            cell.setCellValue(msgMap.get("mcuSerial"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(4);
            cell.setCellValue(msgMap.get("mcuName"));
            cell.setCellStyle(titleCellStyle);

            // MDIS_제조사
            cell = row.createCell(5);
            cell.setCellValue(msgMap.get("vendor"));
            cell.setCellStyle(titleCellStyle);

            // MDIS_모델
            cell = row.createCell(6);
            cell.setCellValue(msgMap.get("model"));
            cell.setCellStyle(titleCellStyle);
            
            // MCU Mobile
            /*cell = row.createCell(6);
            cell.setCellValue(msgMap.get("mcuMobile"));
            cell.setCellStyle(titleCellStyle);*/

            // MDIS_지역
            cell = row.createCell(7);
            cell.setCellValue(msgMap.get("location"));
            cell.setCellStyle(titleCellStyle);
            
            // Installed Location
            cell = row.createCell(8);
            cell.setCellValue(msgMap.get("sysLocation"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(9);
            cell.setCellValue(msgMap.get("ipAddress"));
            cell.setCellStyle(titleCellStyle);
            
            // IPv6 Addr
            cell = row.createCell(10);
            cell.setCellValue(msgMap.get("ipv6Addr"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(11);
            cell.setCellValue(msgMap.get("swVer"));
            cell.setCellStyle(titleCellStyle);

            // MDIS_하드웨어Ver
            cell = row.createCell(12);
            cell.setCellValue(msgMap.get("hwVer"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(13);
            cell.setCellValue(msgMap.get("installation"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(14);
            cell.setCellValue(msgMap.get("lastCommDate"));
            cell.setCellStyle(titleCellStyle);

            // MDIS_통신타입
            cell = row.createCell(15);
            cell.setCellValue(msgMap.get("protocolType"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(16);
            cell.setCellValue(msgMap.get("CommStatus"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(17);
            cell.setCellValue(msgMap.get("macAddr"));
            cell.setCellStyle(titleCellStyle);
            // Title End

            // Data
            int cnt = 0;
            for (Map<String, Object> map : result) {
                row = sheet.createRow(cnt + (ConcentratorStartRow + 1));

                cell = row.createCell(0);
                cell.setCellValue(cnt + 1);
                cell.setCellStyle(dataCellStyle);

                // MDIS_집중기타입
                cell = row.createCell(1);
                cell.setCellValue((map.get("dcuType") != null) ? (String) map.get("dcuType") : "");
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(2);
                cell.setCellValue((String) map.get("sysID"));
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(3);
                cell.setCellValue((String) map.get("mcuSerial"));
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(4);
                cell.setCellValue((String) map.get("sysName"));
                cell.setCellStyle(dataCellStyle);

                // MDIS_제조사
                cell = row.createCell(5);
                cell.setCellValue((String) map.get("vendor"));
                cell.setCellStyle(dataCellStyle);

                // MDIS_모델
                cell = row.createCell(6);
                cell.setCellValue((String) map.get("model"));
                cell.setCellStyle(dataCellStyle);

                // MCU Mobile
                /*cell = row.createCell(6);
                cell.setCellValue((String) map.get("sysPhoneNumber"));
                cell.setCellStyle(dataCellStyle);*/

                // MDIS_지역 - problem 최종통신일시로 title이 찍힘
                cell = row.createCell(7);
                cell.setCellValue((String) map.get("location"));
                cell.setCellStyle(dataCellStyle);
                
                // Installed Location Address
                cell = row.createCell(8);
                cell.setCellValue((String) map.get("sysLocation"));
                cell.setCellStyle(dataCellStyle);              

                cell = row.createCell(9);
                cell.setCellValue((String) map.get("ipAddr"));
                cell.setCellStyle(dataCellStyle);

                String ipv6Addr = (String) map.get("ipv6Addr");
                cell = row.createCell(10);
                cell.setCellValue(ipv6Addr);
                cell.setCellStyle(dataCellStyle);
                
                cell = row.createCell(11);
                cell.setCellValue((String) map.get("sysSwVersion"));
                cell.setCellStyle(dataCellStyle);

                // MDIS_하드웨어Ver
                cell = row.createCell(12);
                cell.setCellValue((String) map.get("sysHwVersion"));
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(13);
                cell.setCellValue((String) map.get("installDate"));
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(14);
                cell.setCellValue((String) map.get("lastCommDate"));
                cell.setCellStyle(dataCellStyle);

                // MDIS_통신타입
                cell = row.createCell(15);
                cell.setCellValue((String) map.get("protocolType"));
                cell.setCellStyle(dataCellStyle);

                // 상태정보 표시
                String CommStatus = (String) map.get("commState");
                if (CommStatus.equals("fmtMessage00")) {
                    CommStatus = msgMap.get("normal");
                } else if (CommStatus.equals("fmtMessage24")) {
                    CommStatus = msgMap.get("HH24over");
                } else if (CommStatus.equals("fmtMessage48")) {
                    CommStatus = msgMap.get("HH48over");
                } else if (CommStatus.equals("1.1.4.5")) {
                    // Code Table 혹은 CommonConstants의 McuStatus 참조
                    CommStatus = CommonConstants.McuStatus.CommError.name();
                } else if (CommStatus.equals("1.1.4.3")) {
                    CommStatus = CommonConstants.McuStatus.PowerDown.name();
                } else if (CommStatus.equals("1.1.4.4")) {
                    CommStatus = CommonConstants.McuStatus.SecurityError.name();
                } else {
                    CommStatus = "Unknown";
                }

                cell = row.createCell(16);
                cell.setCellValue(CommStatus);
                cell.setCellStyle(dataCellStyle);
                
                String macAddr = (String) map.get("macAddr");
                cell = row.createCell(17);
                cell.setCellValue(macAddr);
                cell.setCellStyle(dataCellStyle);
                
                cnt++;
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
            e.getStackTrace();
        } // End Try
    }
    
	public void writeShipmentReportExcel(List<Map<String, Object>> result, Map<String, String> msgMap, boolean isLast, String filePath, String fileName) {
		try {
			HSSFWorkbook workbook = new HSSFWorkbook();

			HSSFFont fontTitle = workbook.createFont();
			fontTitle.setFontHeightInPoints((short) 14);
			fontTitle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			HSSFFont fontHeader = workbook.createFont();
			fontHeader.setFontHeightInPoints((short) 10);
			fontHeader.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			HSSFFont fontBody = workbook.createFont();
			fontBody.setFontHeightInPoints((short) 10);
			fontBody.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);

			HSSFRow row = null;
			HSSFCell cell = null;
			HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 0);
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
			HSSFCellStyle dateDataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
			CreationHelper createHelper = workbook.getCreationHelper();
			dateDataCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd hh:mm:ss"));
			dateDataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			titleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			titleCellStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
			titleCellStyle.setBorderBottom(CellStyle.BORDER_DOUBLE);
			dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
			final String reportTitle = msgMap.get("msg_title");

			int ConcentratorStartRow = 0;
			HSSFSheet sheet = workbook.createSheet(reportTitle);

			int colIdx = 0;
			sheet.setColumnWidth(colIdx++, 256 * 20);	// PO
			sheet.setColumnWidth(colIdx++, 256 * 20);	// Type
			sheet.setColumnWidth(colIdx++, 256 * 20);	// EUI ID
			sheet.setColumnWidth(colIdx++, 256 * 20);	// GS1 Code
			sheet.setColumnWidth(colIdx++, 256 * 20);	// Model
			sheet.setColumnWidth(colIdx++, 256 * 15);	// HW Version
			sheet.setColumnWidth(colIdx++, 256 * 15);	// SW Version
			sheet.setColumnWidth(colIdx++, 256 * 15);	// LOT
			sheet.setColumnWidth(colIdx++, 256 * 20);	// IMEI
			sheet.setColumnWidth(colIdx++, 256 * 20);	// IMSI
			sheet.setColumnWidth(colIdx++, 256 * 20);	// ICC ID
			sheet.setColumnWidth(colIdx++, 256 * 20);	// MSISDN
			sheet.setColumnWidth(colIdx++, 256 * 30);	// Production Date
			sheet.setColumnWidth(colIdx++, 256 * 20);	// DCU ID
			
			// Title 표시 영역  (S)
			row = sheet.createRow(ConcentratorStartRow);

			cell = row.createCell(0);
			cell.setCellValue(msgMap.get("msg_po"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(1);
			cell.setCellValue(msgMap.get("msg_type"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(2);
			cell.setCellValue(msgMap.get("msg_euiId"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(3);
			cell.setCellValue(msgMap.get("msg_gs1"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(4);
			cell.setCellValue(msgMap.get("msg_model"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(5);
			cell.setCellValue(msgMap.get("msg_hwVer"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(6);
			cell.setCellValue(msgMap.get("msg_swVer"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(7);
			cell.setCellValue("LOT");
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(8);
			cell.setCellValue(msgMap.get("msg_imei"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(9);
			cell.setCellValue(msgMap.get("msg_imsi"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(10);
			cell.setCellValue(msgMap.get("msg_iccId"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(11);
			cell.setCellValue(msgMap.get("msg_msisdn"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(12);
			cell.setCellValue(msgMap.get("msg_productionDate"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(13);
			cell.setCellValue(msgMap.get("msg_dcuId"));
			cell.setCellStyle(titleCellStyle);
			// Title 표시 영역  (E)

			// Data 표시 영역 (S)
			int cnt = 0;
			for (Map<String, Object> map : result) {
				row = sheet.createRow(cnt + (ConcentratorStartRow + 1));

				cell = row.createCell(0);
				cell.setCellValue((map.get("po") != null) ? (String) map.get("po") : "");
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(1);
				cell.setCellValue((map.get("dcuType") != null) ? (String) map.get("dcuType") : "");
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(2);
				cell.setCellValue((String) map.get("mcuSerial"));
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(3);
				cell.setCellValue((map.get("gs1") != null) ? (String) map.get("gs1") : "");
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(4);
				cell.setCellValue((String) map.get("model"));
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(5);
				cell.setCellValue((String) map.get("sysHwVersion"));
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(6);
				cell.setCellValue((String) map.get("sysSwVersion"));
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(7);
				cell.setCellValue((String) map.get("lot"));
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(8);
				cell.setCellValue((map.get("imei") != null) ? (String) map.get("imei") : "");
				cell.setCellStyle(dataCellStyle);
				
				// IMSI
				cell = row.createCell(9);
				cell.setCellValue((map.get("simNumber") != null) ? (String) map.get("simNumber") : "");
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(10);
				cell.setCellValue((map.get("iccId") != null) ? (String) map.get("iccId") : "");
				cell.setCellStyle(dataCellStyle);
				
				// MSISDN
				cell = row.createCell(11);
				cell.setCellValue((map.get("sysPhoneNumber") != null) ? (String) map.get("sysPhoneNumber") : "");
				cell.setCellStyle(dataCellStyle);

				/** Convert 'yyyyMMdd' to 'yyyy-MM-dd HH:mm:ss' (S) */
				// DB에 yyyyMMdd Format으로 저장되어있는 데이터, yyyy-MM-dd HH:mm:ss Format으로 변환하여 Excel에 기재
				cell = row.createCell(12);
				String manufacturedDate = (map.get("manufacturedDate") != null) ? (String) map.get("manufacturedDate") : "";
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
				
				cell = row.createCell(13);
				cell.setCellValue((map.get("sysID") != null) ? (String) map.get("sysID") : "");
				cell.setCellStyle(dataCellStyle);
				
				cnt++;
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
			e.getStackTrace();
		}
	}

	public void writeLogReportExcel(String result, String filePath, String fileName) {
		try {
			// String[] resultLog = new String[100];
			ArrayList<String> resultLog = new ArrayList<String>();
			int count = 0;
			StringTokenizer str = new StringTokenizer(result, "\n");
			while (str.hasMoreTokens()) {
				// resultLog[count++]=str.nextToken();
				resultLog.add(str.nextToken());
				count++;
			}

        	HSSFWorkbook workbook = new HSSFWorkbook();
            
        	HSSFFont fontTitle = workbook.createFont();
            fontTitle.setFontHeightInPoints((short) 14);
            fontTitle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFFont fontHeader = workbook.createFont();
            fontHeader.setFontHeightInPoints((short) 10);
            fontHeader.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFFont fontBody = workbook.createFont();
            fontBody.setFontHeightInPoints((short) 10);
            fontBody.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);

            HSSFRow row = null;
            HSSFCell cell = null;
            HSSFCellStyle titleCellStyle = ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1, 1, 0, 1, 0, 1, 1);
            HSSFCellStyle headCellStyle = ExcelUtil.getStyle(workbook, fontBody,    1, 1, 1, 1, 0, 0, 0, 1, 0);
            HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody,    1, 1, 1, 1, 0, 0, 0, 0, 0);

            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = "Event Log";
            int ConcentratorStartRow = 1;
            int totalColumnCnt = 2;

            HSSFSheet sheet = workbook.createSheet(reportTitle);

            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 10);	// 0    
            sheet.setColumnWidth(colIdx++, 256 * 120);	// 1    
            
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 1, 1, 1, 1, 0, 1, 0, 1, 1));
            sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (short) (totalColumnCnt - 1)));

            // Title
            row = sheet.createRow(ConcentratorStartRow);
            
            cell = row.createCell(0);
            cell.setCellValue("No");
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(1);
            cell.setCellValue("");
            cell.setCellStyle(titleCellStyle);

            /*cell = row.createCell(0);
            cell.setCellValue("Event");
            cell.setCellStyle(titleCellStyle);*/

            /*cell = row.createCell(3);
            cell.setCellValue("DESCR");
            cell.setCellStyle(titleCellStyle);*/
            
            // Title End
            int cnt =0;
            int index =0;
            // Data
            for(int i = 0 ; i < count ; i++){
            	row = sheet.createRow(cnt + (ConcentratorStartRow)+1);
            	if(resultLog.get(i).contains("#")){
            		cell = row.createCell(0);
                    cell.setCellValue(++index);
                    cell.setCellStyle(headCellStyle);
            	}else{
            		cell = row.createCell(0);
                    cell.setCellValue("");
                    cell.setCellStyle(headCellStyle);
            	}
            	cell = row.createCell(1);
                cell.setCellValue(resultLog.get(i));
                cell.setCellStyle(dataCellStyle);
            	cnt++;
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
            e.getStackTrace();
        } // End Try

    }

    //상태정보 
    @Deprecated
    private String FormatDate(String lastCommDate) {
        String CommStatus = "";

        if (lastCommDate != null && lastCommDate.length() > 0 && !lastCommDate.equals("null")) {

            Calendar nowTime = Calendar.getInstance();
            int lastDateYear = Integer.parseInt(lastCommDate.substring(0, 4));
            int lastDateMonth = Integer.parseInt(lastCommDate.substring(4, 6));
            int lastDateDate = Integer.parseInt(lastCommDate.substring(6, 8));
            int lastDateHrs = Integer.parseInt(lastCommDate.substring(8, 10));
            int lastDateMin = Integer.parseInt(lastCommDate.substring(10, 12));
            int lastDateSec = Integer.parseInt(lastCommDate.substring(12, 14));

            Calendar lastCommDateFmt = Calendar.getInstance();
            lastCommDateFmt.set(lastDateYear, lastDateMonth - 1, lastDateDate, lastDateHrs, lastDateMin, lastDateSec);

            Date lastCommDateMS = lastCommDateFmt.getTime();
            nowTime.add(Calendar.DATE, -1);
            Date oneDayMS = nowTime.getTime();
            nowTime.add(Calendar.DATE, -1);
            Date twoDayMS = nowTime.getTime();

            if (lastCommDateMS.after(oneDayMS)) {
                CommStatus = "normal";
            } else if (lastCommDateMS.before(oneDayMS) && lastCommDateMS.after(twoDayMS)) {
                CommStatus = "HH24over";
            } else if (lastCommDateMS.before(twoDayMS)) {
                CommStatus = "HH48over";
            }
        }

        return CommStatus;

    }

    // KYHGH Add code because dateType Country Issue No=0000305
    public String CountryDateType(String inDate) {
        // ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        String lang = user.getSupplier().getLang().getCode_2letter();
        String countryName = user.getSupplier().getCountry().getCode_2letter();
        String strDate = TimeLocaleUtil.getLocaleDate(inDate, lang, countryName);

        return strDate;
    }
}