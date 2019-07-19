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

import com.aimir.model.device.CommLog;

/**
 * MeteringDataMakeExcel.java Description
 * 
 * 
 * Date Version Author Description 2012. 4. 26. v1.0 enj
 * 
 */
public class CommLogMakeExcel
{
	
	public static void main(String[] args)
	{
		System.out.println("sdlfksdlkfsdlkf");
		
		CommLogMakeExcel commlogmakeexcel=new CommLogMakeExcel();
		
		//commlogmakeexcel.writeReportExcel(result, msgMap, isLast, filePath, fileName);
		
		
	}

	public CommLogMakeExcel()
	{
		
	}

	/**
	 * @param result
	 * @param msgMap
	 * @param isLast
	 * @param filePath
	 * @param fileName
	 */
	public void writeReportExcel(List<CommLog> result, Map<String, String> msgMap, boolean isLast, String filePath,String fileName)
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
			HSSFCellStyle dataCellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 1, 0);
			HSSFCellStyle data2CellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 0, 0);
			HSSFCellStyle data3CellStyle = ExcelUtil.getStyle(workbook, fontBody, 1, 1, 1, 1, 0, 0, 0, 2, 0);
			
			// MeteringListData resultMap = new MeteringListData();
			//Map<String, Object> resultMap = new HashMap<String, Object>();
			
			CommLog resultMap = new CommLog();

			String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
			final String reportTitle = "Comm Log Grid data";
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
			sheet.setColumnWidth(colIdx++, 300 * 19);
			sheet.setColumnWidth(colIdx++, 256 * 19);
			
			
			// sheet.setColumnWidth(colIdx++, 256 * 19);
			// sheet.setColumnWidth(colIdx++, 256 * 19);

			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue(reportTitle);
			cell.setCellStyle(ExcelUtil.getStyle(workbook, fontTitle, 0, 0, 0,
					0, 0, 0, 0, 1, 0));
			sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0,
					(short) (totalColumnCnt - 1)));

			// Title
			row = sheet.createRow(meteringDataStartRow);

			// font.setFontHeightInPoints((short)10);
			
			/*
			 * #######################
			 * 엑셀 header 셋팅부분.
			 * ########################
			 * 
			 * 
			 */
			cell = row.createCell(0);
			cell.setCellValue("NO");
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(1);
			cell.setCellValue(msgMap.get("msg_time"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(2);
			cell.setCellValue(msgMap.get("msg_datatype"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(3);
			cell.setCellValue(msgMap.get("msg_protocol"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(4);
			cell.setCellValue(msgMap.get("msg_sender"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(5);
			cell.setCellValue(msgMap.get("msg_receiver"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(6);
			cell.setCellValue(msgMap.get("msg_sendbytes"));
			cell.setCellStyle(titleCellStyle);

			cell = row.createCell(7);
			cell.setCellValue(msgMap.get("msg_receivebytes"));
			cell.setCellStyle(titleCellStyle);
			
			
			cell = row.createCell(8);
			cell.setCellValue(msgMap.get("msg_result"));
			cell.setCellStyle(titleCellStyle);
			
			cell = row.createCell(9);
			cell.setCellValue(msgMap.get("msg_totalcommtime"));
			cell.setCellStyle(titleCellStyle);
			
			
			cell = row.createCell(10);
			cell.setCellValue(msgMap.get("msg_operationcode"));
			cell.setCellStyle(titleCellStyle);
			// Title End

			
			
			/**
			 * 
			 * 실제 데이타값 세팅 부분.
			 */
			dataCount = result.size();
			for (int i = 0; i < dataCount; i++)
			{
				
				
				
				
				resultMap = result.get(i);
				row = sheet.createRow(i + (meteringDataStartRow + 1));
				
				
				//0번째 컬럼data
				cell = row.createCell(0);
				cell.setCellValue(i+1);
				cell.setCellStyle(dataCellStyle);

				
				//String time = comm.getStartDate() + " " + comm.getStartTime();
				//1번째 컬럼data
				cell = row.createCell(1);
				cell.setCellValue((String) resultMap.getStartDate().toString() + " "+ resultMap.getStartTime().toString());
				cell.setCellStyle(data2CellStyle);

				cell = row.createCell(2);
				cell.setCellValue((resultMap.getSvcTypeCode() == null) ? "" : (String)resultMap.getSvcTypeCode().getDescr());
				cell.setCellStyle(data2CellStyle);

				
				//프로토콜 네임
				cell = row.createCell(3);
				cell.setCellValue(resultMap.getProtocolCode() == null ? "" : (String) resultMap.getProtocolCode().getDescr());
				cell.setCellStyle(data2CellStyle);

				//sender
				cell = row.createCell(4);
				cell.setCellValue((String) resultMap.getSenderId());
				cell.setCellStyle(data3CellStyle);

				//receiver
				
				String receiverTypeCodeName = resultMap.getReceiverTypeCode().getDescr();
		        String receiverId = resultMap.getReceiverId();
		        String receiver = receiverTypeCodeName+ '['+receiverId + ']' ;
				
				cell = row.createCell(5);
				cell.setCellValue((String) receiver);
				cell.setCellStyle(data3CellStyle);

				
				//send bytes
				
				cell = row.createCell(6);
				cell.setCellValue((String) resultMap.getSendBytes().toString());
				cell.setCellStyle(data2CellStyle);
				
				

				//receive bytes.
				cell = row.createCell(7);
				cell.setCellValue(Integer.toString(resultMap.getRcvBytes()));
				cell.setCellStyle(data2CellStyle);
				

				//result
				String commResult="";
				
			    if ( resultMap.getCommResult() ==1)
				  	commResult = "Success";
			    else
			       	commResult = "Fail";
				
				cell = row.createCell(8);
				cell.setCellValue((String) commResult);
				cell.setCellStyle(data2CellStyle);
				
				//totalcommtime
				cell = row.createCell(9);
				cell.setCellValue((String) resultMap.getTotalCommTime().toString());
				cell.setCellStyle(data2CellStyle);
				
				
				
				//operationCode
				cell = row.createCell(10);
				cell.setCellValue((String) resultMap.getOperationCode().toString());
				cell.setCellStyle(data2CellStyle);
				
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