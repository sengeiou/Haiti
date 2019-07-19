package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;

public class ModemMaxMakeExcel {
	private static Log log = LogFactory.getLog(ModemMaxMakeExcel.class);

	public ModemMaxMakeExcel() {

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
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
			
			Map<String, String> resultMap = new HashMap<String, String>();
			String fileFullPath = new StringBuilder().append(filePath).append(
					File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int modemListStartRow = 3;
            int totalColumnCnt = 10;
			int dataCount = 0;

			if(msgMap.get("type").equals("LM")){			
				totalColumnCnt = 9;
			}

			HSSFSheet sheet = workbook.createSheet(reportTitle);
			
            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 10);	// 0 no
            sheet.setColumnWidth(colIdx++, 256 * 22);	// 1 id
            sheet.setColumnWidth(colIdx++, 256 * 19);	// 2 type
            sheet.setColumnWidth(colIdx++, 256 * 19);	// 3 protocol type
            sheet.setColumnWidth(colIdx++, 256 * 19);	// 4 dcu id
            sheet.setColumnWidth(colIdx++, 256 * 19);	// 5 phone number
            sheet.setColumnWidth(colIdx++, 256 * 19);	// 6 model
            sheet.setColumnWidth(colIdx++, 256 * 19);	// 7 ver
			sheet.setColumnWidth(colIdx++, 256 * 19);	// 8 macAddr
			sheet.setColumnWidth(colIdx++, 256 * 23);	// 9 last comm.time


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
			cell.setCellValue(msgMap.get("id"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("type"));
            cell.setCellStyle(titleCellStyle);
            
            cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("protocolType"));
            cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("mcuid"));
            cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("phone"));
            cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("model"));
            cell.setCellStyle(titleCellStyle);
						
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("ver"));
            cell.setCellStyle(titleCellStyle);

			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("macAddr"));
			cell.setCellStyle(titleCellStyle);
            
			cell = row.createCell(cellCnt++);
			cell.setCellValue(msgMap.get("lastcomm"));
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
				cell.setCellValue(resultMap.get("modemDeviceSerial")==null?"":resultMap.get("modemDeviceSerial").toString());
            	cell.setCellStyle(dataCellStyle);
            	
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("modemType")==null?"":resultMap.get("modemType").toString());
            	cell.setCellStyle(dataCellStyle);
            	
            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("protocolType")==null?"":resultMap.get("protocolType").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("mcuSysId")==null?"":resultMap.get("mcuSysId").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("phone")==null?"":resultMap.get("phone").toString());
            	cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("deviceName")==null?"":resultMap.get("deviceName").toString());
            	cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("ver")==null?"":resultMap.get("ver").toString());
            	cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("macAddr")==null?"":resultMap.get("macAddr").toString());
                cell.setCellStyle(dataCellStyle);

            	cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("lastCommDate")==null?"":resultMap.get("lastCommDate").toString());
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
			log.debug(e, e);
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
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
			
			Map<String, String> resultMap = new HashMap<String, String>();
			String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
			final String reportTitle = msgMap.get("title");
			int modemListStartRow = 3;
			int totalColumnCnt = 10;
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
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
			HSSFCellStyle dateDataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
			CreationHelper createHelper = workbook.getCreationHelper();
			dateDataCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd hh:mm:ss"));
			dateDataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			titleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			titleCellStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
			titleCellStyle.setBorderBottom(CellStyle.BORDER_DOUBLE);
			dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			Map<String, String> resultMap = new HashMap<String, String>();
			String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("msg_title");
            
            int modemListStartRow = 0;
			int dataCount = 0;

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

            // Title 표시 영역  (S)
            row = sheet.createRow(modemListStartRow);

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
 			// Title 표시 영역  (E)

            // Data 표시 영역 (S)
			dataCount = result.size();
			String protocolType = null;
			String modemType = null;

			for (int i = 0; i < dataCount; i++) {
				resultMap = (Map<String, String>) result.get(i);
				row = sheet.createRow(i + (modemListStartRow + 1));
				int cellCnt2 = 0;

				protocolType = (resultMap.get("protocolType") != null) ? (String) resultMap.get("protocolType") : "";
				modemType = resultMap.get("modemType") == null ? "" : resultMap.get("modemType").toString();

				if (!modemType.isEmpty()) {
					if (modemType.equals(ModemType.MMIU.name())) {
						if (protocolType.equals(Protocol.IP.name())) {
							modemType = CommonConstants.ShipmentTargetType.EthernetModem.getName();
						} else if (protocolType.equals(Protocol.SMS.name())) {
							modemType = CommonConstants.ShipmentTargetType.MBBModem.getName();
						}else if (protocolType.equals(Protocol.GPRS.name())) {
							modemType = CommonConstants.ShipmentTargetType.MBBModem.getName();
						}
					} else if (modemType.equals(ModemType.SubGiga.name())) {
						modemType = CommonConstants.ShipmentTargetType.RFModem.getName();
					} else if (modemType.equals(ModemType.Converter_Ethernet.name())) {
						modemType = CommonConstants.ShipmentTargetType.EthernetConverter.getName();
					}
				}
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue((resultMap.get("po") != null) ? (String) resultMap.get("po") : "");
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(cellCnt2++);
				cell.setCellValue(modemType);
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("modemDeviceSerial") == null ? "" : resultMap.get("modemDeviceSerial").toString());
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(cellCnt2++);
				cell.setCellValue((resultMap.get("gs1") != null) ? (String) resultMap.get("gs1") : "");
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(cellCnt2++);
				cell.setCellValue(resultMap.get("deviceName") == null ? "" : resultMap.get("deviceName").toString());
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(cellCnt2++);
				cell.setCellValue((resultMap.get("hwVer") != null) ? (String) resultMap.get("hwVer") : "");
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(cellCnt2++);
				cell.setCellValue((resultMap.get("swVer") != null) ? (String) resultMap.get("swVer") : "");
				cell.setCellStyle(dataCellStyle);
				
				cell = row.createCell(cellCnt2++);
				cell.setCellValue((resultMap.get("lot") != null) ? (String) resultMap.get("lot") : "-");
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(cellCnt2++);
				cell.setCellValue((resultMap.get("imei") != null) ? (String) resultMap.get("imei") : "");
				cell.setCellStyle(dataCellStyle);

				// IMSI
				cell = row.createCell(cellCnt2++);
				cell.setCellValue((resultMap.get("simNumber") != null) ? (String) resultMap.get("simNumber") : "");
				cell.setCellStyle(dataCellStyle);

				cell = row.createCell(cellCnt2++);
				cell.setCellValue((resultMap.get("iccId") != null) ? (String) resultMap.get("iccId") : "");
				cell.setCellStyle(dataCellStyle);

				// MSISDN
				cell = row.createCell(cellCnt2++);
				cell.setCellValue((resultMap.get("phone") != null) ? (String) resultMap.get("phone") : "");
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
			log.debug(e, e);
		}
	}
	
	private String getDurationFormat(String sec, Map<String, String> msgMap){
    	int duration = Integer.parseInt(sec);
    	int s;
    	int m;
    	int h;
    	int d;
    	
    	String dayStr 	= msgMap.get("day").toString();
		String hourStr 	= msgMap.get("hour").toString();
		String minStr 	= msgMap.get("min").toString();
		String secStr 	= msgMap.get("sec").toString();
		
    	s = duration % 60;		// 초
		duration = duration / 60;
		m = duration % 60;		// 분
		duration = duration / 60;		
		h = duration % 24;		// 시
		d = duration / 24;		// 일
		
		
		if(d > 0) return d + dayStr + " " + h + hourStr + " " + m + minStr + " " + s + secStr;
		else if(h > 0) return h + hourStr + " " + m + minStr + " " + s + secStr;
		else if(m > 0) return m + minStr + " " + s + secStr;
		else return s + secStr;
    }
	
    public void writeLogReportExcel(String result, String filePath,String fileName) {
    	
        try {
        	//String[] resultLog = new String[100];
        	ArrayList<String> resultLog= new ArrayList<String>();
        	int count=0;
			StringTokenizer str = new StringTokenizer(result, "\n");
        	try{
				// Try to divide into each log-item.
				String tempStr = result.replaceAll("\\[NO", "|\\[NO");
				str = new StringTokenizer(tempStr, "\\|");
			}catch(Exception e){
				str = new StringTokenizer(result, "\n");
			}
        	while (str.hasMoreTokens()) {
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
            HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody,    1, 1, 1, 1, 0, 0, 0, 0, 0);

            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = "Event Log";
            int ConcentratorStartRow = 1;
            int totalColumnCnt = 1;

            HSSFSheet sheet = workbook.createSheet(reportTitle);

            int colIdx = 0;
           // sheet.setColumnWidth(colIdx++, 256 * 10);	// 0    No.
            sheet.setColumnWidth(colIdx++, 256 * 110);	// 1    Event Time
            //sheet.setColumnWidth(colIdx++, 256 * 30);	// 2    Event Name
            //sheet.setColumnWidth(colIdx++, 256 * 90);	// 3    Event Descr.
            
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 1, 1, 1, 1, 0, 1, 0, 1, 1));
            //sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, (totalColumnCnt - 0)));

            // Title
            row = sheet.createRow(ConcentratorStartRow);
            
            /*cell = row.createCell(0);
            cell.setCellValue("No");
            cell.setCellStyle(titleCellStyle);*/
            
            /*cell = row.createCell(1);
            cell.setCellValue("Date");
            cell.setCellStyle(titleCellStyle);*/

            /*cell = row.createCell(0);
            cell.setCellValue("Event");
            cell.setCellStyle(titleCellStyle);*/

            /*cell = row.createCell(3);
            cell.setCellValue("DESCR");
            cell.setCellStyle(titleCellStyle);*/
            
            // Title End
            int cnt =0;
            // Data
            for(int i = 0 ; i < count ; i++){
            	row = sheet.createRow(cnt + (ConcentratorStartRow));
            	cell = row.createCell(0);
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

}
