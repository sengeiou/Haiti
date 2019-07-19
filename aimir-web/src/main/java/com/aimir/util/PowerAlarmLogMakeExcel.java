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
import org.apache.poi.ss.util.CellRangeAddress;

public class PowerAlarmLogMakeExcel {

    protected static Log log = LogFactory.getLog(PowerAlarmLogMakeExcel.class);

	public PowerAlarmLogMakeExcel() {

	}

	/**
	 * @param result
	 * @param msgMap
	 * @param isLast
	 * @param filePath
	 * @param fileName
	 */
	@SuppressWarnings("unchecked")
    public void writeReportExcel(List<Object> result, Map<String, String> msgMap, boolean isLast, String filePath,
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
            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            final String reportTitle = msgMap.get("title");
            int powerAlarmLogStartRow = 3;
            int totalColumnCnt = 9;
            int dataCount = 0;

            if (msgMap.get("type").equals("LM")) {
                totalColumnCnt = 10;
            }

            HSSFSheet sheet = workbook.createSheet(reportTitle);

            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 10);
            sheet.setColumnWidth(colIdx++, 256 * 22);
            sheet.setColumnWidth(colIdx++, 256 * 22);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 25);

            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0, 0, 0, 0, 0, 1, 0));
            sheet.addMergedRegion(new CellRangeAddress(0, (short)0, 0, (short)(totalColumnCnt - 1)));

            // Title
            row = sheet.createRow(powerAlarmLogStartRow);

            int cellCnt = 0;
            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("id"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("openTime"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("closeTime"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("supplier"));
            cell.setCellStyle(titleCellStyle);

            if (msgMap.get("type").equals("LM")) {
                cell = row.createCell(cellCnt++);
                cell.setCellValue(msgMap.get("lineType"));
                cell.setCellStyle(titleCellStyle);
            }

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("custName"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("meter"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("duration"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("status"));
            cell.setCellStyle(titleCellStyle);

            cell = row.createCell(cellCnt++);
            cell.setCellValue(msgMap.get("message"));
            cell.setCellStyle(titleCellStyle);
            // Title End

            // Data

            dataCount = result.size();

            for (int i = 0; i < dataCount; i++) {
                resultMap = (Map<String, String>)result.get(i);
                row = sheet.createRow(i + (powerAlarmLogStartRow + 1));
                int cellCnt2 = 0;
                cell = row.createCell(cellCnt2++);
                cell.setCellValue(i + 1);
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("openTime") == null ? "" : resultMap.get("openTime").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("closeTime") == null ? "" : resultMap.get("closeTime").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("supplier") == null ? "" : resultMap.get("supplier").toString());
                cell.setCellStyle(dataCellStyle);

                if (msgMap.get("type").equals("LM")) {
                    cell = row.createCell(cellCnt2++);
                    cell.setCellValue(resultMap.get("lineType") == null ? "" : resultMap.get("lineType").toString());
                    cell.setCellStyle(dataCellStyle);
                }

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("custName") == null ? "" : resultMap.get("custName").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("meter") == null ? "" : resultMap.get("meter").toString());
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                // cell.setCellValue((resultMap.get("duration").equals("null")||resultMap.get("duration")==null)?"":getDurationFormat(resultMap.get("duration").toString(),msgMap));
                cell.setCellValue((resultMap.get("duration") == null) ? "" : resultMap.get("duration"));
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                if (resultMap.get("status").equals("open")) {
                    cell.setCellValue(msgMap.get("open"));
                } else {
                    cell.setCellValue(msgMap.get("close"));
                }
                cell.setCellStyle(dataCellStyle);

                cell = row.createCell(cellCnt2++);
                cell.setCellValue(resultMap.get("message") == null ? "" : resultMap.get("message").toString());
                cell.setCellStyle(dataCellStyle);
            }
            // End Data

            // 파일 생성
            FileOutputStream fs = null;
            try {
                fs = new FileOutputStream(fileFullPath);
                workbook.write(fs);
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (fs != null)
                    fs.close();
            }

        } catch(Exception e) {
            log.error(e.toString(), e);
        } // End Try
    }

//	private String getDurationFormat(String sec, Map<String, String> msgMap){
//    	int duration = Integer.parseInt(sec);
//    	
//    	int s;
//    	int m;
//    	int h;
//    	int d;
//    	
//    	String dayStr 	= msgMap.get("day").toString();
//		String hourStr 	= msgMap.get("hour").toString();
//		String minStr 	= msgMap.get("min").toString();
//		String secStr 	= msgMap.get("sec").toString();
//		
//    	s = duration % 60;		// 초
//		duration = duration / 60;
//		m = duration % 60;		// 분
//		duration = duration / 60;		
//		h = duration % 24;		// 시
//		d = duration / 24;		// 일
//		
//		
//		if(d > 0) return d + dayStr + " " + h + hourStr + " " + m + minStr + " " + s + secStr;
//		else if(h > 0) return h + hourStr + " " + m + minStr + " " + s + secStr;
//		else if(m > 0) return m + minStr + " " + s + secStr;
//		else return s + secStr;
//    }
}
