package com.aimir.schedule.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.dao.mvm.BillingDayGMDao;
import com.aimir.dao.mvm.BillingDayWMDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.dao.mvm.BillingMonthGMDao;
import com.aimir.dao.mvm.BillingMonthWMDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Supplier;

import com.aimir.schedule.util.BillingScheduleProperty;
import com.aimir.util.MakeExcelUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Transactional
public class BillingInfoCreateTask extends ScheduleTask {
    protected static Log log = LogFactory.getLog(BalanceMonitorTask.class);
    private final String[] headerLabel = {"id","previousMeterReading","currentMeterReading","consumption","meterNumber","writeDate"};

    private static final String fileDirectory = BillingScheduleProperty.getProperty("delivery.directory");
    private static final String backupFileDirectory = BillingScheduleProperty.getProperty("delivery.backup.directory");
    private static final String deliveryDelayDay = BillingScheduleProperty.getProperty("delivery.delay.day");

    @Autowired
    ContractDao contractDao;
 
    @Autowired
    BillingDayEMDao billingDayEMDao;
 
    @Autowired
    BillingDayGMDao billingDayGMDao;

    @Autowired
    BillingDayWMDao billingDayWMDao;

    @Autowired
    BillingMonthEMDao billingMonthEMDao;

    @Autowired
    BillingMonthGMDao billingMonthGMDao;

    @Autowired
    BillingMonthWMDao billingMonthWMDao;

    @Autowired
    DayEMDao dayEmDao;

    @Autowired
    DayWMDao dayWmDao;

    @Autowired
    CodeDao codeDao;
    
    @Autowired
    SupplierDao supplierDao;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("\n ########### Create Billing Information once a month Start ###########");

        // 설정파일 값 체크
        this.checkProperty();

        // 공급사의 과금일 기준, 빌링 정보 생성 함수
        this.createBillingInfoByBillDateOfSupplier();

        // setSuccessResult();
        
        log.info("\n ########### Create Billing Information once a month End ###########");
    }

    // Supply BillDate Base
    private void createBillingInfoByBillDateOfSupplier() {
    	/******* 전기 고객 *******/
    	// 과금일이 오늘인 고객의 빌링 정보를 생성한다.
    	this.createEmBillingDayInfoOfSupplierBillDate();

        // 과거 빌링값 누락된 고객을 상대로 재검침 되었는지 판단하여 빌링 정보를 전송한다.
        this.createReDeliveryEmBillingInfo();
        
        // 과금일에 해당하는 빌링값 누락으로 정보 전송하지 못한 고객의 상태를 갱신한다.
        this.createNotDeliveryEmBillingInfoOfSupplierBillDate();

        /******* 수도 고객 *******/
    	// 과금일이 오늘인 고객의 빌링 정보를 생성한다.
    	this.createWmBillingDayInfoOfSupplierBillDate();

        // 과거 빌링값 누락된 고객을 상대로 재검침 되었는지 판단하여 빌링 정보를 전송한다.
        this.createReDeliveryWmBillingInfo();

        // 과금일에 해당하는 빌링값 누락으로 정보 전송하지 못한 고객의 상태를 갱신한다.
        this.createNotDeliveryWmBillingInfoOfSupplierBillDate();
    }

    /************************************************/
    // 공급사의 과금일 기준, 전기 고객의 빌링 정보 생성 함수       //
    // 공급사가 같은 고객들의 빌링 정보를 취득한다.          //
    /************************************************/
    /************** Energy Customer Start ***************/
    public void createEmBillingDayInfoOfSupplierBillDate() {
    	log.info("\n ***** Normal EM Billing Information Start *****");

    	String today = TimeUtil.getCurrentTimeMilli();
    	int serviceType = codeDao.getCodeIdByCode(CommonConstants.MeterType.EnergyMeter.getServiceType());
    	int creditType = codeDao.getCodeIdByCode(Code.POSTPAY);
    	List<Map<String, Object>> list = billingDayEMDao.getDayBillingInfoDataBySupplierBillDate(serviceType, creditType);

    	log.info("\n **Create EM Billing Customer size : " + list.size());
 
    	// 해당 정보가 없을 때는 처리를 중단 한다.
    	if(list.size()==0) return;

    	// 파일명 생성을 위한 공급사 명 취득 
    	Contract contract = contractDao.get((Integer)list.get(0).get("ID"));
    	Supplier supplier = supplierDao.get(contract.getSupplierId());
    	String supplierName = supplier.getName();

    	// 빌링정보 생성 플라그 갱신
    	this.updateEMBillingSendResultFlag(true, list);
    	log.info("\n *** Success update Send Result Flag");

    	// 빌링 정보 xls파일 생성
    	// 현재는 에너지 별로 한 공급사만 있지만 앞으로 여러 공급사를 같이 관리 할때는 공급사별로 하기 로직을 분기 해야 한다.
    	this.writeBillingInformationExcel(list, fileDirectory, "EM_BillingInfo_Supplier("+ supplierName + ")_billDate(" + today.substring(6,8) +  ")_" + today +".xls");
    	this.writeBillingInformationExcel(list, backupFileDirectory, "EM_BillingInfo_Supplier("+ supplierName + ")_billDate(" + today.substring(6,8) +  ")_" + today +".xls");
    	
    	log.info("\n ***** Normal EM Billing Information End *****");
    }

    private void updateEMBillingSendResultFlag(boolean sendResultFlag, List<Map<String, Object>> list) {
    	for(Map<String, Object> map : list) {
    		billingDayEMDao.updateBillingSendResultFlag(sendResultFlag, (Integer)map.get("ID"), (String)map.get("YYYYMMDD"));
    	}
    }

    private void updateWMBillingSendResultFlag(boolean sendResultFlag, List<Map<String, Object>> list) {
    	for(Map<String, Object> map : list) {
    		billingDayWMDao.updateBillingSendResultFlag(sendResultFlag, (Integer)map.get("ID"), (String)map.get("YYYYMMDD"));
    	}
    }

    private void createNotDeliveryEmBillingInfoOfSupplierBillDate() {
    	log.info("\n ***** Not Delivery EM Billing Information Start *****");
    	String today = TimeUtil.getCurrentTimeMilli().substring(0, 8);

    	// 전기 고객 대상으로 검색
    	int serviceType = codeDao.getCodeIdByCode(CommonConstants.MeterType.EnergyMeter.getServiceType());
        // 후불 고객 대상으로 검색
    	int creditType = codeDao.getCodeIdByCode(Code.POSTPAY);
  
    	List<Map<String, Object>> list = billingDayEMDao.getNotDeliveryDayBillingInfoDataBySupplierBillDate(serviceType, creditType);   	
    	log.info("\n *** Not Delivery Customer size : " + list.size());
 
		this.updateNotDeliveryDelayDay(list, today);
    	// delivery Delay day 0일 경우는 빌링정보가 누락된 고객도 즉시 전송한다.
    	if(Integer.parseInt(deliveryDelayDay) == 0) {
    		this.createReDeliveryEmBillingInfo();
    	}
    	log.info("\n ***** Not Delivery EM Billing Information End *****");
    }

    private void createReDeliveryEmBillingInfo() {
    	log.info("\n ***** Re-Delivery EM Billing Information Start *****");
    	// 과거 누락된 고객 리스트 취득

    	int serviceType = codeDao.getCodeIdByCode(CommonConstants.MeterType.EnergyMeter.getServiceType());
    	List<Map<String, Object>> list = contractDao.getDeliveryDelayContratInfo(serviceType);
    	List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
    	String today = TimeUtil.getCurrentTimeMilli();
		String beforeDelayDay = "";
    	try{
    		log.info("\n *** Re-Delivery Customer size : " + list.size());
	    	// 빌링이 재검침 되었는지 판단
    		Map<String, Object> map = null;
    		String delayDay = "";
    		int contractId = 0;
    		boolean hasActiveTotalEnergy = false;
    		Contract c = null;
    		String supplierName = "Spasa";
    		List<Map<String, Object>> newMap = null;
    		
    		for(int i=0; i<list.size(); i++) {
    			map = list.get(i);
	    		delayDay = (String)map.get("DELAYDAY");
	    		contractId = (Integer)map.get("ID");
	    		hasActiveTotalEnergy = billingDayEMDao.hasActiveTotalEnergyByBillDate(contractId, delayDay);
	    		c = contractDao.get(contractId);
	    		// supplierName = c.getSupplier().getName();
	    		log.info("Contract[" + c.getContractNumber() + "] idx["+i+
	    		        "] hasActiveTotalEnergy[" + hasActiveTotalEnergy+"] billDate[" + delayDay+ "]");
	    		
	    		if(hasActiveTotalEnergy) {
	    			// 해당 고객의 빌링 정보를 취득한다.
	    			newMap = billingDayEMDao.getReDeliveryDayBillingInfoData(contractId, delayDay);
	  
	    			if(newMap.size() != 0) {
			        	// 빌링정보 생성 플라그 갱신
	    				newList.add(newMap.get(0));
	    				billingDayEMDao.updateBillingSendResultFlag(true, contractId, delayDay);
	    				c.setDelayDay(delayDay);
	    				contractDao.update(c);
		    			// contractDao.updateSendResult(contractId, null);
	    			}
	    		} else {
	    			// Delay날짜를 갱신한다.
	    			String dalay_yyyymmdd = TimeUtil.getAddedDay(delayDay, Integer.parseInt(deliveryDelayDay)).substring(0, 8);
	    			if(TimeUtil.getDayDuration(dalay_yyyymmdd, today.substring(0, 8)) >= 0 ) {
	    				// 지정된 delay날짜까지 재검침 되지 않을 경우 과금일에 해당하는 빌링정보까지 작성하여 전송한다.
	        			// 해당 고객의 빌링 정보를 취득한다.
	        			newMap = billingDayEMDao.getReDeliveryDayBillingInfoData(contractId, delayDay);
	        			
	        			if (newMap.size() > 0) {
    	        			log.info("Contract[" + c.getContractNumber() + "] idx["+i+"] billDate[" + (String)newMap.get(0).get("YYYYMMDD") +"]");
    	        			hasActiveTotalEnergy = billingDayEMDao.hasActiveTotalEnergyByBillDate(contractId, (String)newMap.get(0).get("YYYYMMDD"));
    	        			
    	        			if(newMap.size() !=0 && hasActiveTotalEnergy) {
            					newList.add(newMap.get(0));
        			        	// 빌링정보 생성 플라그 갱신
        		    			billingDayEMDao.updateBillingSendResultFlag(true, contractId, (String)newMap.get(0).get("YYYYMMDD"));
        	    				// contractDao.updateSendResult(contractId, null);
                                c.setDelayDay(delayDay);
                                contractDao.update(c);
    	        			}
	        			}
	        			else log.warn("Contract[" + c.getContractNumber() + "] idx["+i+"] billDate[" + delayDay+ "] does not exist");
	    			}
	    		}

		    	if(i==list.size()-1 || (!delayDay.equals(beforeDelayDay) && i!=0)) {
		    	    // 빌링 정보 xls파일 생성
		    		this.writeBillingInformationExcel(newList, fileDirectory, "EM_BillingInfo_Supplier(" + supplierName +  ")_billDate_(" + today.substring(6, 8) + ")" + today +".xls");
		    		this.writeBillingInformationExcel(newList, backupFileDirectory, "EM_BillingInfo_Supplier(" + supplierName +  ")_billDate_(" + today.substring(6, 8) + ")" + today +".xls");
		    		newList.clear();
		    	}
		    	beforeDelayDay = delayDay;
	    	}

//        	String supplierName = contractDao.get((Integer)list.get(0).get("ID")).getSupplier().getName();
//    		if(newList.size() !=0 ) {
//	    	    // 빌링 정보 xls파일 생성
//	    		this.writeBillingInformationExcel(newList, fileDirectory, "EM_BillingInfo_Supplier_(" + supplierName + ")_billDate(" + currentDay +  ")_" + today +".xls");
//	    		this.writeBillingInformationExcel(newList, backupFileDirectory, "EM_BillingInfo_Supplier_(" + supplierName +  ")_" + today +".xls");
//   			
//    		}
    	}catch(Exception e){
    		log.error(e, e);
    	}
    	log.info("\n ***** Re-Delivery EM Billing Information End *****");
    }
    /************** Energy Customer End ***************/

    /************** Water Customer Start ***************/
    public void createWmBillingDayInfoOfSupplierBillDate() {
    	log.info("\n ***** Normal WM Billing Information Start *****");

    	String today = TimeUtil.getCurrentTimeMilli();
    	int serviceType = codeDao.getCodeIdByCode(CommonConstants.MeterType.WaterMeter.getServiceType());
    	int creditType = codeDao.getCodeIdByCode(Code.POSTPAY);
    	List<Map<String, Object>> list = billingDayWMDao.getDayBillingInfoDataBySupplierBillDate(serviceType, creditType);

    	log.info("\n **Create WM Billing Customer size : " + list.size());
 
    	// 해당 정보가 없을 때는 처리를 중단 한다.
    	if(list.size()==0) return;

    	// 파일명 생성을 위한 공급사 명 취득 
    	Contract contract = contractDao.get((Integer)list.get(0).get("ID"));
    	Supplier supplier = supplierDao.get(contract.getSupplierId());
    	String supplierName = supplier.getName();

    	// 빌링정보 생성 플라그 갱신
    	this.updateWMBillingSendResultFlag(true, list);
    	log.info("\n *** Success update Send Result Flag");

    	// 빌링 정보 xls파일 생성
    	// 현재는 에너지 별로 한 공급사만 있지만 앞으로 여러 공급사를 같이 관리 할때는 공급사별로 하기 로직을 분기 해야 한다.
    	this.writeBillingInformationExcel(list, fileDirectory, "WM_BillingInfo_Supplier("+ supplierName + ")_billDate(" + today.substring(6,8) +  ")_" + today +".xls");
    	this.writeBillingInformationExcel(list, backupFileDirectory, "WM_BillingInfo_Supplier("+ supplierName + ")_billDate(" + today.substring(6,8) +  ")_" + today +".xls");   	

    	log.info("\n ***** Normal WM Billing Information End *****");
    }

    private void createReDeliveryWmBillingInfo() {
    	log.info("\n ***** Re-Delivery WM Billing Information Start *****");
    	// 과거 누락된 고객 리스트 취득
    	int serviceType = codeDao.getCodeIdByCode(CommonConstants.MeterType.WaterMeter.getServiceType());
    	List<Map<String, Object>> list = contractDao.getDeliveryDelayContratInfo(serviceType);
    	List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
    	String today = TimeUtil.getCurrentTimeMilli();
		String beforeDelayDay = "";
    	try{
    		log.info("\n *** Re-Delivery Customer size : " + list.size());
	    	// 빌링이 재검침 되었는지 판단
    		Map<String, Object> map = null;
    		String delayDay = null;
    		int contractId = 0;
    		Contract c = null;
    		boolean hasActiveTotalEnergy = false;
    		String supplierName = "Spasa";
    		List<Map<String, Object>> newMap = null;
    		
    		for(int i=0; i<list.size(); i++) {
    			map = list.get(i);
	    		delayDay = (String)map.get("DELAYDAY");
	    		contractId = (Integer)map.get("ID");
	    		c = contractDao.get(contractId);
	    		// supplierName = c.getSupplier().getName();
	    		hasActiveTotalEnergy = billingDayWMDao.hasActiveTotalEnergyByBillDate(contractId, delayDay);
	    		log.info("Contract[" + c.getContractNumber() + "] idx["+i+
                        "] hasActiveTotalEnergy[" + hasActiveTotalEnergy+"] billDate[" + delayDay+ "]");
	    		
	    		if(hasActiveTotalEnergy) {
	    			// 해당 고객의 빌링 정보를 취득한다.
	    			newMap = billingDayWMDao.getReDeliveryDayBillingInfoData(contractId, delayDay);
	    			if(newMap.size() != 0) {
	    				newList.add(newMap.get(0));
			        	// 빌링정보 생성 플라그 갱신
		    			billingDayWMDao.updateBillingSendResultFlag(true, contractId, delayDay);
		    			// contractDao.updateSendResult(contractId, null);
		    			c = contractDao.get(contractId);
                        c.setDelayDay(delayDay);
                        contractDao.update(c);
	    			}
	    		} else {
	    			String dalay_yyyymmdd = TimeUtil.getAddedDay(delayDay, Integer.parseInt(deliveryDelayDay)).substring(0, 8);
	    			if(TimeUtil.getDayDuration(dalay_yyyymmdd, today.substring(0, 8)) >= 0 ) {
	    				// 지정된 delay날짜까지 재검침 되지 않을 경우 과금일에 해당하는 빌링정보까지 작성하여 전송한다.
	        			// 해당 고객의 빌링 정보를 취득한다.
	        			newMap = billingDayWMDao.getReDeliveryDayBillingInfoData(contractId, delayDay);
	        			
	        			if (newMap.size() > 0) {
    	        			log.info("Contract[" + c.getContractNumber() + "] idx["+i+"] billDate[" + (String)newMap.get(0).get("YYYYMMDD") +"]");
                            hasActiveTotalEnergy = billingDayWMDao.hasActiveTotalEnergyByBillDate(contractId, (String)newMap.get(0).get("YYYYMMDD"));
                            
    	        			if(newMap.size() != 0 && hasActiveTotalEnergy) {
    		        			newList.add(newMap.get(0));
    				        	// 빌링정보 생성 플라그 갱신
    			    			billingDayWMDao.updateBillingSendResultFlag(true, contractId, (String)newMap.get(0).get("YYYYMMDD"));
    		    				// contractDao.updateSendResult(contractId, null);
    			    			c = contractDao.get(contractId);
    	                        c.setDelayDay(delayDay);
    	                        contractDao.update(c);
    	        			}
	        			}
	        			else log.warn("Contract[" + c.getContractNumber() + "] idx["+i+"] billDate[" + delayDay+ "] does not exist");
	    			}
	    		}

		    	if(i==list.size()-1 || (!delayDay.equals(beforeDelayDay) && i!=0)) {
		    	    // 빌링 정보 xls파일 생성
		    		this.writeBillingInformationExcel(newList, fileDirectory, "WM_BillingInfo_Supplier(" + supplierName + ")_billDate_(" + today.substring(6, 8) +  ")_" + today +".xls");
		    		this.writeBillingInformationExcel(newList, backupFileDirectory, "WM_BillingInfo_Supplier(" + supplierName + ")_billDate_(" + today.substring(6, 8) +  ")_" + today +".xls");
		    		newList.clear();
		    	}
		    	beforeDelayDay = delayDay;
	    	}

//    		if(newList.size() !=0 ) {
//    	    	// 파일명 생성을 위한 공급사 명 취득 
//    	    	String supplierName = contractDao.get((Integer)list.get(0).get("ID")).getSupplier().getName();
//	    		this.writeBillingInformationExcel(newList, fileDirectory, "WM_BillingInfo_Supplier_(" + supplierName +  ")_" + today +".xls");
//	    		this.writeBillingInformationExcel(newList, backupFileDirectory, "WM_BillingInfo_Supplier_(" + supplierName +  ")_" + today +".xls");   			
//    		}
    	}catch(Exception e){
    		log.error(e, e);
    	}
    	log.info("\n ***** Re-Delivery WM Billing Information End *****");
    }

    private void createNotDeliveryWmBillingInfoOfSupplierBillDate() {
    	log.info("\n ***** Not Delivery WM Billing Information Start *****");
    	String today = TimeUtil.getCurrentTimeMilli().substring(0, 8);

    	// 수도 고객 대상으로 검색
    	int serviceType = codeDao.getCodeIdByCode(CommonConstants.MeterType.WaterMeter.getServiceType());
        // 후불 고객 대상으로 검색
    	int creditType = codeDao.getCodeIdByCode(Code.POSTPAY);
  
    	List<Map<String, Object>> list = billingDayWMDao.getNotDeliveryDayBillingInfoDataBySupplierBillDate(serviceType, creditType);   	
    	log.info("\n *** Not Delivery Customer size : " + list.size());
 
		this.updateNotDeliveryDelayDay(list, today);
    	// delivery Delay day 0일 경우는 빌링정보가 누락된 고객도 즉시 전송한다.
    	if(Integer.parseInt(deliveryDelayDay) == 0) {
    		this.createReDeliveryWmBillingInfo();
    	}
    	log.info("\n ***** Not Delivery WM Billing Information End *****");
    }
    /************** Water Customer End ***************/

    // 빌링 정보 누락으로 전송하지 못한 고객의 상태를 갱신한다.
    private void updateNotDeliveryDelayDay(List<Map<String, Object>> list, String delayDay) {    	
    	for(Map<String, Object> map : list) {
    		int contractId = (Integer)map.get("ID");
    		// contractDao.updateSendResult(contractId, delayDay.substring(0, 8));
    		Contract c = contractDao.get(contractId);
            c.setDelayDay(delayDay.substring(0, 8));
            contractDao.update(c);
    	}
    }

    private boolean checkProperty() {
    	log.info("\n ***** Check Properties Start *****");
    	boolean res = true;
    	log.info("\n ***FTP Directory : [" + fileDirectory + "]");
    	if(fileDirectory.isEmpty()) {
    		log.info("\n **FTP Diresctory is null");
    		res = false;
    	}

    	log.info("\n ***FTP Backup Directory : [" + backupFileDirectory + "]");
    	if(backupFileDirectory.isEmpty()) {
    		log.info("\n **FTP Backup Directory is null");
    		res = false;
    	}

    	log.info("\n ***deilveryDelayDay : [" + deliveryDelayDay + "]");
    	if(!StringUtil.isDigit(String.valueOf(deliveryDelayDay))) {
    		log.info("\n **You must enter only numbers(0-9) in the deilveryDelayDay field. ");
    		res = false;
    	}
    	log.info("\n ***** Check Properties Start *****");
    	return res;
    }

    /**
     * method name : writeReportExcel
     * method Desc :
     *
     * @param result
     * @param filePath
     * @param fileName
     */
    public void writeBillingInformationExcel(List<Map<String, Object>> result, String filePath, String fileName) {
    	log.info("\n ***** Write Billing Information Excel Start *****");
    	MakeExcelUtil billingInfoMakeExcel = null;
        try {
        	// 디렉토리가 없으면 생성 한다.
        	this.makeDirectory(filePath);
        	// 데이터가 없으면 작성하지 않는다.
        	if(result.size() ==0){
        		log.info("\nNo Data");
        		return;
        	}

        	String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
        	billingInfoMakeExcel = new MakeExcelUtil(fileFullPath, "BillingInformation");
        	billingInfoMakeExcel.setFontHeightInPoints((short)9);

            Map<String, Object> resultMap = null;

            int colcnt = 0;
            billingInfoMakeExcel.setColumnWidth(0, 256 * 22);
            billingInfoMakeExcel.setColumnWidth(1, 256 * 22);
            billingInfoMakeExcel.setColumnWidth(2, 256 * 22);
            billingInfoMakeExcel.setColumnWidth(3, 256 * 22);
            billingInfoMakeExcel.setColumnWidth(4, 256 * 22);
            billingInfoMakeExcel.setColumnWidth(5, 256 * 22);

            billingInfoMakeExcel.createRow(0);

            // Header Label 작성
            for(int i=0; i<headerLabel.length; i++) {
            	billingInfoMakeExcel.createCell(i);
            	billingInfoMakeExcel.setCellValue(headerLabel[i]);
            }

            for(int i=0; i<result.size(); i++) {
                colcnt = 0;
                resultMap = result.get(i);
                // 열 생성
                billingInfoMakeExcel.createRow(i+1);
                billingInfoMakeExcel.createCell(colcnt);
                billingInfoMakeExcel.setCellValue((String)resultMap.get("NUMBER"));
                billingInfoMakeExcel.createCell(++colcnt);

                Double previousMeterReading = resultMap.get("PREVIOUSMETERREADING") == null ? 0d : (Double)resultMap.get("PREVIOUSMETERREADING");
                billingInfoMakeExcel.setCellValue(String.valueOf(previousMeterReading));
                billingInfoMakeExcel.createCell(++colcnt);

                Double currentMeterReading = resultMap.get("CURRENTMETERREADING") == null ? 0d : (Double)resultMap.get("CURRENTMETERREADING");
                billingInfoMakeExcel.setCellValue(String.valueOf(currentMeterReading));
                
                billingInfoMakeExcel.createCell(++colcnt);
                billingInfoMakeExcel.setCellValue(String.valueOf(currentMeterReading - previousMeterReading)); // 현재 누적 사용량 - 전달 누적 사용량

                billingInfoMakeExcel.createCell(++colcnt);
                billingInfoMakeExcel.setCellValue((String)resultMap.get("MDEVID"));                

                billingInfoMakeExcel.createCell(++colcnt);
                billingInfoMakeExcel.setCellValue(TimeUtil.getCurrentTimeMilli());
            }

            billingInfoMakeExcel.writeExcel();
        }
        catch (Exception e) {
            log.error(e, e);
        }
        finally {
            try {
                if (billingInfoMakeExcel != null)
                    billingInfoMakeExcel.close();
            }
            catch (Exception e) {}
        }
        log.info("\n ***** Write Billing Information Excel End *****");
    }
    
    private void makeDirectory(String directoryName) {
    	File file = new File(directoryName);
    	if(!file.exists()) {
    		file.mkdirs();
    	}
    }
    
//    private Map<String, Object> makeZeroConsumptionInfo(int contractId) {
//    	Map<String, Object> map = new HashMap<String, Object>();
//    	Contract contract = contractDao.get(contractId);
//    	map.put("NUMBER", contract.getContractNumber());
//    	map.put("PREVIOUSMETERREADING", 0d);
//    	map.put("CURRENTMETERREADING", 0d);
//    	map.put("MDEVID", contract.getMeter() == null ? "" : contract.getMeter().getMdsId());
//    	return map;
//    }
   
    // 60일 이전의 Backup 파일을 삭제한다.
    private void deleteValidFile(String backupDirectory) {
    	long deadline = 60*24*60*60;
    	long today = TimeUtil.getCurrentLongTime();
    	File file = new File(backupDirectory);
    	if(file.exists()) {
    		File[] files = file.listFiles();
    		for(File f : files) {
    			long lastModifyMSC = f.lastModified();
    			if( today - lastModifyMSC > deadline) {
//    				 60일 이전의 파일은 전부 삭제
        			f.delete();
    			}
    		}
    	}
    }
}
