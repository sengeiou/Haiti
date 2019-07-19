package com.aimir.schedule.task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.dao.mvm.BillingMonthWMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.mvm.BillingMonthWM;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.schedule.util.SAPProperty;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * 
 * @author jiae
 * 
 * IHD에서 BillingInfosMessage 를 보낼 때 사용되는 한달 bill 값을 저장하는 task
 * ftp 서버에 있는 Month billing(한달 사용량과 금액을 저장한 파일) 파일(yyyymmdd.xls)을 읽어와서 저장
 * 
 * 파일은 6일후에 삭제하도록 한다. (임의로 정한 보관기간)
 * 
 *
 */
public class IHDMonthBillSaveTask {
	private static Log log = LogFactory.getLog(IHDMonthBillSaveTask.class);
	
	@Autowired
	HibernateTransactionManager tx;
	
	@Autowired
	BillingMonthEMDao billingMonthEMDao;
	
	@Autowired
	BillingMonthWMDao billingMonthWMDao;
	
	@Autowired
	ContractDao contractDao;
	
	@Autowired
	MeterDao meterDao;
	
	@Autowired
	ModemDao modemDao;
	
	@Autowired
	CodeDao codeDao;
	
	List<HashMap<String,String>> wrongContractList = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> wrongContractInfo = new HashMap<String, String>();
	
	public void excute() {
		log.debug("\n##### IHD Month Bill Save Task - Start #####\n");
		
		//MonthBill이 저장된 데이터가 올라오는 파일Path
		String readFilePath = SAPProperty.getProperty("ihd.ftp.filePath", "") + "//";
		//backup 파일을 저장할 Path
		String wrongContractFilePath = SAPProperty.getProperty("ihd.ftp.wrongContractFilePath", "");
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n ## Property Information ##");
        sb.append("\n readFilePath : [" + readFilePath + "]");
        sb.append("\n backupFilePath : [" + wrongContractFilePath + "]");
        log.info(sb.toString());
        
     // check the properties value
        if(readFilePath.length() == 0) {
        	log.error("Please check the property file.");
        	return;
        }


		//컨넥션
		try {
			
			File readFileFolder = new File(readFilePath);
			File[] fileList = readFileFolder.listFiles();

			File backFileFolder = new File(wrongContractFilePath);
			File[] wrongContractFileList = backFileFolder.listFiles();
			
			String fileName = null;
			
			String today = TimeUtil.getCurrentDay();
			
			log.debug("************ 1. Delete backup File Start ************");
			for(File bFile : wrongContractFileList) {
				if( bFile.isFile() && checkFileName(bFile.getName())) {
				    log.debug("Backup fileName : ("+ bFile.getName()+")\n");
				    fileName = bFile.getName().substring(0,bFile.getName().lastIndexOf("."));
				    SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
					String toDay = sd.format(new Date());
					
					// 6일이 지난 백업파일은 삭제함
	                if(Integer.parseInt(fileName) < Integer.parseInt(CalendarUtil.getDateWithoutFormat(toDay,Calendar.DATE, -6))) {
	                	if(bFile.getName().toLowerCase().substring(bFile.getName().lastIndexOf(".")).equals(".txt")) {
	                		bFile.delete();
		                	log.debug("Delete backup file  [" + bFile.getName() + "]");
	                	}
	                }
	                
				}
			}
			log.debug("************ 1. Delete backup File End  ************");
			log.debug("************ 2. Handle read file Start************");
			for( File file : fileList ) {
				if( file.isFile() && checkFileName(file.getName())) {
				    log.debug("fileName : ("+ file.getName()+")\n");
				    if(file.getName().toLowerCase().substring(file.getName().lastIndexOf(".")).equals(".xls")) {
				    	readFileXLS(readFilePath, file.getName(), today);
				    }
	                
	                fileName = file.getName().substring(0,file.getName().lastIndexOf("."));
	                fileBackup(file, wrongContractFilePath);
	                
				}
			}
			log.debug("************ 2. Handle read file End************");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.debug("\n##### IHD Month Bill Save Task - END #####\n");
	}
	
	private boolean checkFileName(String fileName) {
        // ex) file format : yyyyMMdd.xls(MonthBill 저장 파일) or yyyyMMdd.txt(Backup 파일)
        //file name check
        
        // 0. 전체 파일 길이 검사.
        String[] fileNameTmp    = fileName.split("\\.");
        if(fileNameTmp[0].length()!=8){    //파일이름 총길이 8자 검사
            log.error("\nFile Name Format Error(" + fileName + ")");
            return false;
        }
        
        if(fileNameTmp[1].length() != 3 
        		|| !(("xls".equals(fileNameTmp[1].toLowerCase()))  || ("txt".equals(fileNameTmp[1].toLowerCase())))) {
        	log.error("\nFile Format Error(" + fileName + ")");
        	return false;
        }

        // 1-1. 파일명 포멧 검사
        //today date  *get 'yyyyMMdd' 
        // String today = TimeUtil.getCurrentDay();                        
            
        //날짜 포멧 검사
        DateFormat DF = new SimpleDateFormat("yyyyMMdd");
        DF.setLenient(false);
        
        try
		{
        	DF.parse(fileNameTmp[0]);
		} catch (ParseException ex) {
			log.error("\nFile Name Format Error(" + fileName + ")");
			return false;
		}
	    return true;
	}

	
	private void readFileXLS(String readFilePath, String fileName, String today){
		log.debug("\n--- 2.1 Read a Month Bill File Start---\n");
		HSSFWorkbook wb = null;
		TransactionStatus txStatus = null;
		try { 
			
			//엑셀 읽어오기
			wb = new HSSFWorkbook(new FileInputStream(readFilePath + fileName));
			// Text Extraction
			ExcelExtractor extractor = new ExcelExtractor(wb);

			extractor.setFormulasNotResults(true);
			extractor.setIncludeSheetNames(false);

			HSSFSheet sheet = wb.getSheetAt(0);
			
			String colValue = null;
			String[] readData = new String[3];
			List<String[]> readList = new ArrayList<String[]>();
			
			for (Row row : sheet) {
				if (row.getRowNum() != 0) {
					for (Cell cell : row) {
						if(cell.getColumnIndex() == 1 || cell.getColumnIndex() == 2 || cell.getColumnIndex() == 4) {
							switch( cell.getCellType() ) {
								case Cell.CELL_TYPE_STRING :
									colValue = cell.getRichStringCellValue().getString();
									break;
				
								case Cell.CELL_TYPE_NUMERIC :
									if(DateUtil.isCellDateFormatted(cell)) {
										colValue = cell.getDateCellValue().toString();
									} 
									else {
										Long roundVal = Math.round(cell.getNumericCellValue());
										Double doubleVal = cell.getNumericCellValue();
										if(doubleVal.equals(roundVal.doubleValue())){
											colValue = String.valueOf(roundVal);
										}else{
											colValue = String.valueOf(doubleVal);
										}
									}
									break;
				
								case Cell.CELL_TYPE_BOOLEAN :
									colValue = String.valueOf(cell.getBooleanCellValue());
									break;
				
								case Cell.CELL_TYPE_FORMULA :
									colValue = cell.getCellFormula();
									break;
				
								default:
									colValue = "";
							}	
						
							if(cell.getColumnIndex() == 1) {
								//ContractNumber
								readData[0] = colValue.trim();
							} else if(cell.getColumnIndex() == 2) {
								//Amount
								readData[1] = colValue.trim();
							} else if(cell.getColumnIndex() == 4) {
								//Usage
								readData[2] = colValue.trim();
							}
						}
					}
					if( readData[0] != null && readData[1] != null && readData[2] != null
							&& !(readData[0].isEmpty() || readData[1].isEmpty() || readData[2].isEmpty())) {
								readList.add(readData);
								readData = new String[3];
						} 
				}
			}
			log.debug("\n--- 2.1 Read a Month Bill File End---\n");
			
			txStatus = tx.getTransaction(null);
			insertDB(fileName.substring(0, fileName.lastIndexOf(".")), today, readList);
			 tx.commit(txStatus);
			 
		} catch (Exception e) {
			log.error(e,e);
			if (txStatus != null) tx.rollback(txStatus);
		}
		
	}
	

	//#######################################################################업데이트가 되는지 확인 필요
	 /**
	  * @date : 2013.01.10
	  * 11월의 사용량은 11월의 DB에 저장되도록 되어있음
	  * 현재 정책 : 사용량 - 11월 01일 ~ 11월 30일 까지의 사용량
	  *         Billing Date - 12월 20일에 나옴
	  *         납입기간 - 01월 12일전까지 
	  *         
	  *         BillingMonthEM에 저장시 11월의 사용량에 해당하는 Bill갑은 yyyymmdd값이 12월01인 레코드의 bill을 저장시킨다. 
	  */
	private void insertDB(String fileName, String today, List<String[]> readList) {
		log.debug("\n--- 2.2 Insert MonthBilling Data into DataBase Start---\n");
		String[] readData = null;
		Contract contract = null;
		Meter meter = null;
		Code code = null;
//		String preYesarMonth = CalendarUtil.getDate(fileName,2,-1).substring(0,6);
//		Map<String,String> usagePeriod = CalendarUtil.getDateMonth(preYesarMonth.substring(0,4), preYesarMonth.substring(4,6));
//		String usageStartDate =  usagePeriod.get("startDate")+"000000";
//		String usageEndDate = usagePeriod.get("endDate")+"000000";
		
		try {
			for (int i = 0; i < readList.size(); i++) {
				readData = readList.get(i);
				contract = (Contract) contractDao.getContractIdByContractNo(readData[0].toString()).get(0);
				if(contract != null) {
					meter = meterDao.get(contract.getMeterId());
					
					if(meter != null && meter.getModemId() != null) {
						
						code = codeDao.get(meter.getMeterTypeCodeId());
						if("1.3.1.1".equals(code.getCode())) {
							BillingMonthEM billingMonthEM = new BillingMonthEM();
							// Month To Date취득
					        billingMonthEM.setYyyymmdd(fileName.substring(0,6)+"01");
					        billingMonthEM.setHhmmss("000000");
							billingMonthEM.setMDevType(DeviceType.Meter.name());
					        billingMonthEM.setMDevId(meter.getMdsId());
							billingMonthEM.setContract(contract);
							billingMonthEM.setSupplier(contract.getSupplier());
							billingMonthEM.setLocation(contract.getLocation());
					        billingMonthEM.setBill(Double.parseDouble(readData[1]));
//					        billingMonthEM.setActiveEnergyRateTotal(Double.parseDouble(readData[2]));
//					        billingMonthEM.setUsageReadFromDate(usageStartDate);
//					        billingMonthEM.setUsageReadToDate(usageEndDate);
					        billingMonthEM.setMeterId(meter.getId());
					        billingMonthEM.setModemId(meter.getModemId());
					        billingMonthEM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
					       
					        billingMonthEMDao.saveOrUpdate(billingMonthEM);
			
						} else if("1.3.1.2".equals(code.getCode())){
							BillingMonthWM billingMonthWM = new BillingMonthWM();
							// Month To Date취득
							billingMonthWM.setYyyymmdd(fileName.substring(0,6)+"01");
							billingMonthWM.setHhmmss("000000");
							billingMonthWM.setMDevType(DeviceType.Meter.name());
							billingMonthWM.setMDevId(meter.getMdsId());
							billingMonthWM.setContract(contract);
							billingMonthWM.setSupplier(contract.getSupplier());
							billingMonthWM.setLocation(contract.getLocation());
							billingMonthWM.setBill(Double.parseDouble(readData[1]));
//					        billingMonthWM.setUsage(Double.parseDouble(readData[2]));
//					        billingMonthWM.setUsageReadFromDate(usageStartDate);
//					        billingMonthWM.setUsageReadToDate(usageEndDate);
					        billingMonthWM.setMeterId(meter.getId());
					        billingMonthWM.setModemId(meter.getModemId());
					        billingMonthWM.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
					        
					        billingMonthWMDao.saveOrUpdate(billingMonthWM);
						}
						log.info("bill Save contractNo [" + contract.getContractNumber()+"]");

					} else {
						log.info("meter is not exist [" + contract.getContractNumber()+"]");
						wrongContractInfo.put("contractNo", readData[0]);
						wrongContractInfo.put("bill", readData[1]);
						wrongContractInfo.put("usage", readData[2]);
						wrongContractList.add(wrongContractInfo);
					}
				} else {
					log.error("contractNumber is not exist [" + readData[0].toString()+"]");
					wrongContractInfo.put("contractNo", readData[0]);
					wrongContractInfo.put("bill", readData[1]);
					wrongContractInfo.put("usage", readData[2]);
					wrongContractList.add(wrongContractInfo);
				}
				
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			
		}
		log.debug("\n--- 2.2 Insert MonthBilling Data into DataBase End---\n");
	}
	
	/**
	 * 
	 * 존재하지 않는 contractNumber일 경우 backup파일을 생성한다.
	 * 
	 * @param file
	 * @param wrongContractFilePath
	 */
	private void fileBackup(File file, String wrongContractFilePath) {
		log.debug("\n---- 1.2 Create wrongContractList file Start----\n");
		try {
			if(wrongContractList.size() > 0) {
		    	File backupFile = new File(wrongContractFilePath+file.getName().substring(0, file.getName().lastIndexOf("."))+".txt");
		    	BufferedWriter out = new BufferedWriter(new FileWriter(backupFile));
		    	
		    	out.write("No		contractNumber				bill				usage");
		    	out.newLine();
		    	
		    	for (int i = 0; i < wrongContractList.size(); i++) {
		    		out.write(i+1 +"		"+ wrongContractList.get(i).get("contractNo")+"				" + wrongContractList.get(i).get("bill") + "				" + wrongContractList.get(i).get("usage"));
		    		out.newLine();
				}
		    	out.close();
		    	
		    	log.debug("create wrongContractList file [" + file.getName().substring(0, file.getName().lastIndexOf("."))+".txt" + "]");
		    	
		    	log.debug("\n---- 1.2 Create wrongContractList file End----\n");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("fileBackup error");
			log.error(e.getMessage());
		}
	}
	
}
	
