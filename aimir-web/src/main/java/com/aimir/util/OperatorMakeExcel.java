package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.aimir.constants.CommonConstants.LoginStatus;
import com.aimir.model.device.CommLog;

/**
 * OperatorMakeExcel.java Description
 * 
 * 
 * Date Version Author Kyungjoon.go
 * 
 */
public class OperatorMakeExcel
{
	
	

	public OperatorMakeExcel()
	{
		
	}

	/**
	 * @param result
	 * @param msgMap
	 * @param isLast
	 * @param filePath
	 * @param fileName
	 */
	@SuppressWarnings({ "rawtypes", "static-access" })
	public void writeReportExcel(List result, Map<String, String> msgMap, boolean isLast, String filePath,String fileName, String dateType, String searchTerm)
	{

		
		
		try
		{
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
			HSSFCellStyle noCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 1, 0);
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
					
			// MeteringListData resultMap = new MeteringListData();
			//Map<String, Object> resultMap = new HashMap<String, Object>();
			
			Map resultMap = new HashMap();

			String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
			final String reportTitle = "Operation Login Log History";
			int meteringDataStartRow = 3;
			// int totalColumnCnt = 10;
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
			sheet.setColumnWidth(colIdx++, 700 * 19);
			// sheet.setColumnWidth(colIdx++, 256 * 19);
			// sheet.setColumnWidth(colIdx++, 256 * 19);

			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue(reportTitle);
			cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0,				0, 0, 0, 0, 1, 0));
			sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0,		(short) (totalColumnCnt - 1)));

			// Title
			row = sheet.createRow(meteringDataStartRow);
			
			//2번쨰 로우 생성
			row = sheet.createRow(1);
			cell = row.createCell(0);
			
			//2rd row merge
			sheet.addMergedRegion(new CellRangeAddress(1, // first row (0-based)
					1, // last row (0-based)
					0, // first column (0-based)
					7 // last column (0-based)
			));
			
			
			//3번쨰 로우 생성
			row = sheet.createRow(2);
			cell = row.createCell(7);
			

			cell.setCellStyle(ExcelUtil.getStyle(workbook, fontHeader, 1, 1, 1,	1, 0, 0, 0, 1, 0));
			
			//스타일 인스턴스 생성
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			//우측 정렬 
			cellStyle.setAlignment(cellStyle.ALIGN_RIGHT);

			//셀 생성
			cell = row.createCell(0);
			//셀 스타일 적용
	        cell.setCellStyle(cellStyle);
	        //셀 value setting (기간 value)
			cell.setCellValue(dateType+ ":"+ searchTerm);
			//3rd row merge
			sheet.addMergedRegion(new CellRangeAddress(2, // first row (0-based)
					2, // last row (0-based)
					0, // first column (0-based)
					7 // last column (0-based)
			));
			
	
			
			/*
			 * #######################
			 * 엑셀 header 셋팅부분.
			 * ########################
			 * 
			 * 
			 */
			
			row = sheet.createRow(3);
			
			cell = row.createCell(0);
			cell.setCellValue(msgMap.get("msg_number"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(1);
			cell.setCellValue(msgMap.get("msg_userid"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(2);
			cell.setCellValue(msgMap.get("msg_username"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(3);
			cell.setCellValue(msgMap.get("msg_usergroup"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(4);
			cell.setCellValue(msgMap.get("msg_ipaddress"));
			cell.setCellStyle(titleCellStyle);
			
	

			cell = row.createCell(5);
			cell.setCellValue(msgMap.get("msg_loginhour"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(6);
			cell.setCellValue(msgMap.get("msg_logouthour"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(7);
			cell.setCellValue(msgMap.get("msg_status"));
			cell.setCellStyle(titleCellStyle);
			
		
			
			
			/**
			 * 
			 * 실제 데이타값 세팅 부분.
			 */
			dataCount = result.size();
			for (int i = 0; i < dataCount; i++)
			{
				
				
				
				
				resultMap = (Map) result.get(i);
				row = sheet.createRow(i + (meteringDataStartRow + 1));
				
				
				/*chartDataMap.put("no",          i );                       
				chartDataMap.put("userId",     	resultData[0]);                 
				chartDataMap.put("userName",    resultData[1]);
				chartDataMap.put("userGroup",   resultData[2]);
				chartDataMap.put("ipAddr",   	resultData[3]);
				chartDataMap.put("loginTime",   resultData[4]);
				chartDataMap.put("logoutTime",  resultData[5]);
				chartDataMap.put("status", _loginStatus.name());*/
				
				
				
				//0번째 컬럼 data
				cell = row.createCell(0);
				cell.setCellValue(i+1);
				cell.setCellStyle(noCellStyle);

				//1번째 컬럼
				cell = row.createCell(1);
				cell.setCellValue((String) resultMap.get("userId").toString());
				cell.setCellStyle(dataCellStyle);

				//2번째 컬럼
				cell = row.createCell(2);
				cell.setCellValue((String) resultMap.get("userName").toString());
				cell.setCellStyle(dataCellStyle);

				
				//3st
				cell = row.createCell(3);
				cell.setCellValue((String) resultMap.get("userGroup").toString());
				cell.setCellStyle(dataCellStyle);

				//4rd
				cell = row.createCell(4);
				cell.setCellValue((String) resultMap.get("ipAddr").toString());
				cell.setCellStyle(dataCellStyle);

				
				//5rd
				cell = row.createCell(5);
				cell.setCellValue((String) resultMap.get("loginTime").toString());
				//cell.setCellStyle(ExcelUtil.getStyle(workbook, fontBody, 1, 1,			1, 1, 0, 0, 0, 2, 0));
				cell.setCellStyle(dataCellStyle);

				
				//6rd
				cell = row.createCell(6);
				cell.setCellValue((String) resultMap.get("logoutTime").toString());
				cell.setCellStyle(dataCellStyle);
					
				
				

				//7st
				cell = row.createCell(7);
				cell.setCellValue((String) resultMap.get("status").toString());
				cell.setCellStyle(dataCellStyle);
				
				
			
			}
			// End Data

			// 파일 생성
			FileOutputStream fs = null;
			try
			{
				fs = new FileOutputStream(fileFullPath);
				workbook.write(fs);
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
				if (fs != null)
					fs.close();
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} // End Try
	}
}