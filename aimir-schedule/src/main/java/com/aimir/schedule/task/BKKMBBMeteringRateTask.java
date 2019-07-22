package com.aimir.schedule.task;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.mail.search.IntegerComparisonTerm;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.dao.device.FirmwareDao;
import com.aimir.dao.device.FirmwareIssueDao;
import com.aimir.dao.device.FirmwareIssueHistoryDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.schedule.excel.ExcelUtil;

/**
 * @author jiwoong
 *
 */
@Service
@Deprecated
public class BKKMBBMeteringRateTask extends ScheduleTask {
	private static Logger logger = LoggerFactory.getLogger(RollOutMeteringRateTask.class);

	@Resource(name = "transactionManager")
	HibernateTransactionManager txmanager;

	@Autowired
	FirmwareDao firmwareDao;

	@Autowired
	LocationDao locationDao;

	@Autowired
	FirmwareIssueHistoryDao firmwareIssueHistoryDao;

	@Autowired
	FirmwareIssueDao firmwareIssueDao;

	@Autowired
	OperatorDao operatorDao;

	@Autowired
	MCUDao mcuDao;

	@Autowired
	MeterDao meterDao;

	@Autowired
	ModemDao modemDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	MMIUDao mmiuDao;

	@Autowired
	OperationLogDao operationLogDao;

	@Autowired
	CmdOperationUtil cmdOperationUtil;

	@Autowired
	AsyncCommandLogDao asyncCommandLogDao;

	@Autowired
	AsyncCommandResultDao resultDao;

	@Override
	public void execute(JobExecutionContext context) {
		// TODO Auto-generated method stub

	}

	public List<Map<String, Object>> execute48Hour(String searchTime) {
		TransactionStatus txstatus = null;
		txstatus = txmanager.getTransaction(null);
		List<Map<String, Object>> result = null;
		try {
			result = meterDao.get48HourNoMeteringRate(searchTime);
		} catch (Exception e) {
			if (txstatus != null) {
				txmanager.rollback(txstatus);
			}
			logger.error("Task Excute transaction error - " + e, e);
		}

		if (txstatus != null) {
			txmanager.commit(txstatus);
		}
		return result;
	}

	public List<Map<String, Object>> executeHLS() {
		TransactionStatus txstatus = null;
		txstatus = txmanager.getTransaction(null);
		List<Map<String, Object>> result = null;
		try {
			result = meterDao.getHLSKeyErrorMeteringRate();
		} catch (Exception e) {
			if (txstatus != null) {
				txmanager.rollback(txstatus);
			}
			logger.error("Task Excute transaction error - " + e, e);
		}

		if (txstatus != null) {
			txmanager.commit(txstatus);
		}
		return result;
	}

	public List<Map<String, Object>> executeNoResponse() {
		TransactionStatus txstatus = null;
		txstatus = txmanager.getTransaction(null);
		List<Map<String, Object>> result = null;
		try {
			result = meterDao.getMeterNoResponseMeteringRate();
		} catch (Exception e) {
			if (txstatus != null) {
				txmanager.rollback(txstatus);
			}
			logger.error("Task Excute transaction error - " + e, e);
		}

		if (txstatus != null) {
			txmanager.commit(txstatus);
		}
		return result;
	}

	public List<Map<String, Object>> executeNoValue() {
		TransactionStatus txstatus = null;
		txstatus = txmanager.getTransaction(null);
		List<Map<String, Object>> result = null;
		try {
			result = meterDao.getNoValueMeteringRate();
		} catch (Exception e) {
			if (txstatus != null) {
				txmanager.rollback(txstatus);
			}
			logger.error("Task Excute transaction error - " + e, e);
		}

		if (txstatus != null) {
			txmanager.commit(txstatus);
		}
		return result;
	}

	public static void main(String[] args) {
		List<Map<String, Object>> result48Hour = null;
		List<Map<String, Object>> resultHLS = null;
		List<Map<String, Object>> resultNoResponse = null;
		List<Map<String, Object>> resultNoValue = null;

		String searchTime = null;

		if (args.length < 1) {
			logger.info("Usage:");
			logger.info("RollOutMeteringRateTask -DsearchTime=SearchTime ");
			return;
		}

		for (int i = 0; i < args.length; i += 2) {
			String nextArg = args[i];
			if (nextArg.startsWith("-searchTime")) {
				searchTime = new String(args[i + 1]);
			}
		}

		logger.info("BKKMBBMeteringRateTask params. SearchTime={}", searchTime);

		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(
					new String[] { "spring-BKKMBBMeteringRate.xml" });
			DataUtil.setApplicationContext(ctx);

			BKKMBBMeteringRateTask task = (BKKMBBMeteringRateTask) ctx.getBean(BKKMBBMeteringRateTask.class);
			result48Hour = task.execute48Hour(searchTime);
			resultHLS = task.executeHLS();
			resultNoResponse = task.executeNoResponse();
			resultNoValue = task.executeNoValue();
			makeExcel(result48Hour, resultHLS, resultNoResponse, resultNoValue, searchTime);

		} catch (Exception e) {
			logger.error("BKKMBBMeteringRateTask excute error - " + e, e);
		} finally {
			logger.info("#### BKKMBBMeteringRateTask finished. ####");
			System.exit(0);
		}
	}

	@SuppressWarnings("resource")
	private static void makeExcel(List<Map<String, Object>> result48Hour, List<Map<String, Object>> resultHLS,
			List<Map<String, Object>> resultNoResponse, List<Map<String, Object>> resultNoValue, String searchTime) {
		logger.info("=== ### makeExcel section ### ===");
		// logger.info("===> result\n" + result48Hour);

		long time = System.currentTimeMillis();
		SimpleDateFormat dayTimeFormat = new SimpleDateFormat("yyyyMMddHH");
		String dayTime = dayTimeFormat.format(new Date(time));
		String fileName = "./report/BKKMBBMeteringReport_" + dayTime + ".xls";

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = null;
		HSSFRow row = null;
		HSSFCell cell = null;

		HSSFFont fontTitle = workbook.createFont();
		fontTitle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		HSSFCellStyle titleCellStyle = workbook.createCellStyle();
		titleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		titleCellStyle.setBorderBottom(CellStyle.BORDER_DOUBLE);
		titleCellStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
		titleCellStyle.setFont(fontTitle);

		int resultSize = 0;
		int colSize = 0;
		int colWidthAdjust = 500;

		// 48 hour over no metering [ S ]
		sheet = workbook.createSheet("48Hour Over NO Metering");
		resultSize = result48Hour.size();
		colSize = result48Hour.get(0).keySet().size();
		logger.info("48Hour Over NO Metering : resultSize = " + resultSize);

		row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("DSO");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(1);
		cell.setCellValue("MSA");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(2);
		cell.setCellValue("METERSERIAL");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(3);
		cell.setCellValue("GS1");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(4);
		cell.setCellValue("EUI");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(5);
		cell.setCellValue("HW_VER");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(6);
		cell.setCellValue("FW_VER");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(7);
		cell.setCellValue("LAST_LINK_TIME");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(8);
		cell.setCellValue("LAST_METERING_TIME");
		cell.setCellStyle(titleCellStyle);

		for (int rowNum = 0; rowNum < resultSize; rowNum++) {
			row = sheet.createRow(rowNum + 1);
			cell = row.createCell(0);
			cell.setCellValue((String) result48Hour.get(rowNum).get("DSO"));
			cell = row.createCell(1);
			cell.setCellValue((String) result48Hour.get(rowNum).get("MSA"));
			cell = row.createCell(2);
			cell.setCellValue((String) result48Hour.get(rowNum).get("METERSERIAL"));
			cell = row.createCell(3);
			cell.setCellValue((String) result48Hour.get(rowNum).get("GS1"));
			cell = row.createCell(4);
			cell.setCellValue((String) result48Hour.get(rowNum).get("EUI"));
			cell = row.createCell(5);
			cell.setCellValue((String) result48Hour.get(rowNum).get("HW_VER"));
			cell = row.createCell(6);
			cell.setCellValue((String) result48Hour.get(rowNum).get("FW_VER"));
			cell = row.createCell(7);
			cell.setCellValue((String) result48Hour.get(rowNum).get("LAST_LINK_TIME"));
			cell = row.createCell(8);
			cell.setCellValue((String) result48Hour.get(rowNum).get("LAST_METERING_TIME"));
		}
		for(int colNum = 0; colNum < colSize; colNum++) {
			sheet.autoSizeColumn(colNum);
			sheet.setColumnWidth(colNum, sheet.getColumnWidth(colNum)+colWidthAdjust);
		}
		// 48 hour over no metering [ E ]

		// HLS Key Error [ S ]
		sheet = workbook.createSheet("HLS Key Error");
		resultSize = resultHLS.size();
		colSize = resultHLS.get(0).keySet().size();

		logger.info("HLS Key Error : resultSize = " + resultSize);

		row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("DSO");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(1);
		cell.setCellValue("MSA");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(2);
		cell.setCellValue("METERSERIAL");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(3);
		cell.setCellValue("GS1");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(4);
		cell.setCellValue("EUI");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(5);
		cell.setCellValue("LAST_LINK_TIME");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(6);
		cell.setCellValue("LAST_METERING_TIME");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(7);
		cell.setCellValue("EVENT_TIME");
		cell.setCellStyle(titleCellStyle);

		for (int rowNum = 0; rowNum < resultSize; rowNum++) {
			row = sheet.createRow(rowNum + 1);
			cell = row.createCell(0);
			cell.setCellValue((String) resultHLS.get(rowNum).get("DSO"));
			cell = row.createCell(1);
			cell.setCellValue((String) resultHLS.get(rowNum).get("MSA"));
			cell = row.createCell(2);
			cell.setCellValue((String) resultHLS.get(rowNum).get("METERSERIAL"));
			cell = row.createCell(3);
			cell.setCellValue((String) resultHLS.get(rowNum).get("GS1"));
			cell = row.createCell(4);
			cell.setCellValue((String) resultHLS.get(rowNum).get("EUI"));
			cell = row.createCell(5);
			cell.setCellValue((String) resultHLS.get(rowNum).get("LAST_LINK_TIME"));
			cell = row.createCell(6);
			cell.setCellValue((String) resultHLS.get(rowNum).get("LAST_METERING_TIME"));
			cell = row.createCell(7);
			cell.setCellValue((String) resultHLS.get(rowNum).get("EVENT_TIME"));
		}
		for(int colNum = 0; colNum < colSize; colNum++) {
			sheet.autoSizeColumn(colNum);
			sheet.setColumnWidth(colNum, sheet.getColumnWidth(colNum)+colWidthAdjust);
		}
		// HLS Key Error [ E ]

		// No Response [ S ]
		sheet = workbook.createSheet("No Response");
		resultSize = resultNoResponse.size();
		colSize = resultNoResponse.get(0).keySet().size();

		logger.info("No Response : resultSize = " + resultSize);

		row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("DSO");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(1);
		cell.setCellValue("MSA");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(2);
		cell.setCellValue("METERSERIAL");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(3);
		cell.setCellValue("GS1");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(4);
		cell.setCellValue("EUI");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(5);
		cell.setCellValue("LAST_LINK_TIME");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(6);
		cell.setCellValue("LAST_METERING_TIME");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(7);
		cell.setCellValue("EVENT_TIME");
		cell.setCellStyle(titleCellStyle);

		for (int rowNum = 0; rowNum < resultSize; rowNum++) {
			row = sheet.createRow(rowNum + 1);
			cell = row.createCell(0);
			cell.setCellValue((String) resultNoResponse.get(rowNum).get("DSO"));
			cell = row.createCell(1);
			cell.setCellValue((String) resultNoResponse.get(rowNum).get("MSA"));
			cell = row.createCell(2);
			cell.setCellValue((String) resultNoResponse.get(rowNum).get("METERSERIAL"));
			cell = row.createCell(3);
			cell.setCellValue((String) resultNoResponse.get(rowNum).get("GS1"));
			cell = row.createCell(4);
			cell.setCellValue((String) resultNoResponse.get(rowNum).get("EUI"));
			cell = row.createCell(5);
			cell.setCellValue((String) resultNoResponse.get(rowNum).get("LAST_LINK_TIME"));
			cell = row.createCell(6);
			cell.setCellValue((String) resultNoResponse.get(rowNum).get("LAST_METERING_TIME"));
			cell = row.createCell(7);
			cell.setCellValue((String) resultNoResponse.get(rowNum).get("EVENT_TIME"));
		}
		for(int colNum = 0; colNum < colSize; colNum++) {
			sheet.autoSizeColumn(colNum);
			sheet.setColumnWidth(colNum, sheet.getColumnWidth(colNum)+colWidthAdjust);
		}
		// No Response [ E ]

		// No Value [ S ]
		sheet = workbook.createSheet("No Value");
		resultSize = resultNoValue.size();
		colSize = resultNoValue.get(0).keySet().size();

		logger.info("No Value : resultSize = " + resultSize);

		row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("DSO");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(1);
		cell.setCellValue("MSA");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(2);
		cell.setCellValue("METERSERIAL");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(3);
		cell.setCellValue("GS1");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(4);
		cell.setCellValue("EUI");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(5);
		cell.setCellValue("HW_VER");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(6);
		cell.setCellValue("FW_VER");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(7);
		cell.setCellValue("LAST_LINK_TIME");
		cell.setCellStyle(titleCellStyle);
		cell = row.createCell(8);
		cell.setCellValue("LAST_METERING_TIME");
		cell.setCellStyle(titleCellStyle);

		for (int rowNum = 0; rowNum < resultSize; rowNum++) {
			row = sheet.createRow(rowNum + 1);
			cell = row.createCell(0);
			cell.setCellValue((String) resultNoValue.get(rowNum).get("DSO"));
			cell = row.createCell(1);
			cell.setCellValue((String) resultNoValue.get(rowNum).get("MSA"));
			cell = row.createCell(2);
			cell.setCellValue((String) resultNoValue.get(rowNum).get("METERSERIAL"));
			cell = row.createCell(3);
			cell.setCellValue((String) resultNoValue.get(rowNum).get("GS1"));
			cell = row.createCell(4);
			cell.setCellValue((String) resultNoValue.get(rowNum).get("EUI"));
			cell = row.createCell(5);
			cell.setCellValue((String) resultNoValue.get(rowNum).get("HW_VER"));
			cell = row.createCell(6);
			cell.setCellValue((String) resultNoValue.get(rowNum).get("FW_VER"));
			cell = row.createCell(7);
			cell.setCellValue((String) resultNoValue.get(rowNum).get("LAST_LINK_TIME"));
			cell = row.createCell(8);
			cell.setCellValue((String) resultNoValue.get(rowNum).get("LAST_METERING_TIME"));
		}
		for(int colNum = 0; colNum < colSize; colNum++) {
			sheet.autoSizeColumn(colNum);
			sheet.setColumnWidth(colNum, sheet.getColumnWidth(colNum)+colWidthAdjust);
		}
		// No Value [ E ]
		
		// 파일 생성 - excel
		FileOutputStream fs = null;
		try {
			fs = new FileOutputStream(fileName);
			workbook.write(fs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fs != null)
				try {
					fs.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
