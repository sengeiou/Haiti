package com.aimir.cms.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.cms.dao.DebtEntDao;
import com.aimir.cms.dao.DebtLogDao;
import com.aimir.cms.model.DebtEnt;
import com.aimir.cms.model.DebtLog;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.prepayment.VendorCasherDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.DepositHistoryDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.Meter;
import com.aimir.model.prepayment.DepositHistory;
import com.aimir.model.prepayment.VendorCasher;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Service(value = "debtEntManager")
public class DebtEntManagerImpl implements DebtEntManager{
	
	protected static Log log = LogFactory.getLog(DebtEntManagerImpl.class);
	
	@Autowired
	DebtEntDao debtEntDao;
	
	@Autowired
	DebtLogDao debtLogDao;
	
	@Autowired
	CodeDao codeDao;
	
	@Autowired
	SupplierDao supplierDao;
	
	@Autowired
	MeterDao meterDao;
	
	@Autowired
	OperatorDao operatorDao;
	
    @Autowired
    ContractDao contractDao;
    
    @Autowired
    VendorCasherDao vendorCasherDao;
    
    @Autowired
    DepositHistoryDao depositHistoryDao;
    
    @Autowired
    PrepaymentLogDao prepaymentLogDao;
    
    @Autowired
    ContractChangeLogDao contractChangeLogDao;

    @Autowired
    HibernateTransactionManager transactionManager;

	
	public List<Map<String, Object>> getPrepaymentChargeList(Map<String, Object> condition) throws Exception {
        Integer supplierId = (Integer)condition.get("supplierId");
        List<Map<String, Object>> result = debtEntDao.getPrepaymentChargeContractList(condition, false);

        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());

        for (Map<String, Object> map : result) {
            map.put("LASTTOKENDATE", TimeLocaleUtil.getLocaleDate((String)map.get("LASTTOKENDATE"), lang, country));
            map.put("CURRENTCREDIT", ((map.get("CURRENTCREDIT") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("CURRENTCREDIT"))) : cdf.format(0d)));
            map.put("CURRENTARREARS", ((map.get("CURRENTARREARS") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("CURRENTARREARS"))) : cdf.format(0d)));
            map.put("DEBTAMOUNT", ((map.get("DEBTAMOUNT") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("DEBTAMOUNT"))) : cdf.format(0d)));
        }

        return result;
	}
	
	public Integer getPrepaymentChargeListTotalCount(Map<String, Object> condition) {
        List<Map<String, Object>> result = debtEntDao.getPrepaymentChargeContractList(condition, true);
        return (Integer)(result.get(0).get("TOTAL"));
	}
	
	public List<Map<String,Object>> getDebtInfoByCustomerNo(String customerNo, String debtType, String debtRef) {
		return debtEntDao.getDebtInfoByCustomerNo(customerNo, debtType, debtRef);
	}
	
	public void modifyDebtInfo(Map<String, Object> condition) {
		List<Map<String,Object>> debtInfoList = (List<Map<String, Object>>) condition.get("debtSaveInfo");
		int size = debtInfoList.size();
		try{
			for (int i = 0; i < size; i++) {
				Map<String,Object> tempData = debtInfoList.get(i);
				debtEntDao.modifyDebtInfo(tempData);
			}
		} catch(Exception e) {
			log.error(e,e);
		}
	}
	
    public Map<String, Object> vendorSavePrepaymentChargeECG(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String result = null;
        Double deposit = null;
        String supplierName = null;
        String dateTime = null;
        Map<String, Object> ret = new HashMap<String, Object>();
        
        try {
            dateTime = TimeUtil.getCurrentTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
        Supplier supplier = supplierDao.get(supplierId);
        supplierName = supplier.getName();
        conditionMap.put("dateTime", dateTime);
        conditionMap.put("supplierName", supplierName);

        Map<String, Object> resultMap = vendorBalanceChargingECG(conditionMap);
        result = (String) resultMap.get("result");
        
        if (result.equals("success")) {
        	Operator operator = operatorDao.get((Integer) conditionMap.get("operatorId"));
        	deposit = operator.getDeposit();
        	Long prepaymentLogId = (Long) resultMap.get("prepaymentLogId");
        	ret.put("credit", resultMap.get("credit"));
        	ret.put("isCutOff", resultMap.get("isCutOff"));
        	ret.put("deposit", deposit);
        	ret.put("prepaymentLogId", prepaymentLogId);
        	ret.put("smsInfo",resultMap.get("smsInfo"));
        }
        ret.put("result", result);
        return ret;
    }

	private Map<String, Object> vendorBalanceChargingECG(Map<String, Object> condition) {
    	Map<String, Object> result = new HashMap<String, Object>();
    	Boolean isVendor = (Boolean) condition.get("isVendor");
    	String supplierName = StringUtil.nullToBlank(condition.get("supplierName"));
    	String casherId = StringUtil.nullToBlank(condition.get("casherId"));
    	Integer contractId = (Integer) condition.get("contractId");
    	String contractNumber = StringUtil.nullToBlank(condition.get("contractNumber"));
    	String mdsId = StringUtil.nullToBlank(condition.get("mdsId"));
    	Double contractDemand = (Double) condition.get("contractDemand");
    	Integer operatorId = (Integer) condition.get("operatorId");
    	Double contractPrice = (Double) condition.get("contractPrice");
    	Boolean isPartpayment = (Boolean) condition.get("isPartpayment");
    	Boolean partpayReset = (Boolean) condition.get("partpayReset");
    	Integer payTypeId = (Integer) condition.get("payTypeId");
    	String customerNo = (String) condition.get("customerNo");
    	Double paidAmount = (Double) condition.get("paidAmount");
    	Double chargedCredit = (Double) condition.get("chargedCredit");
    	String checkNo = (String) condition.get("checkNo");
    	Integer bankCode = (Integer) condition.get("bankCode");
    	List<Map<String,Object>> currentDebtList = (List<Map<String,Object>>) condition.get("debtList");
    	
    	String rtnStr = "";
    	
    	TransactionStatus txStatus = null;
    	log.info(condition);
    	
    	try {
    		txStatus = transactionManager.getTransaction(null);

            Meter meter = meterDao.get(mdsId);
            
            Contract contract = null; 
            List<DebtEnt> debtEntList = null;
            Map<String, Object> conditionMap = new HashMap<String, Object>();
            
            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            conditionMap.put("mdsId", mdsId);
            
            // select Contract
            contract = contractDao.get(contractId);
            debtEntList = debtEntDao.getDebt(customerNo, null, null);

            if (contract == null) {
                transactionManager.commit(txStatus);
                result.put("result", "fail : Invalid contract Info");
                return result;
            }
            Operator updateOperator = operatorDao.get(operatorId);
    		Double currentDeposit = updateOperator.getDeposit();

    		if ( isVendor && (currentDeposit == null || currentDeposit < paidAmount) ) {
    			// 잔고 부족 
    			transactionManager.rollback(txStatus);
    			result.put("result", "fail : Insufficient Quota Balance");
    			return result;
    		}

    		String lastChargeDate = StringUtil.nullToBlank(contract.getLastTokenDate());
    		Double preCredit = StringUtil.nullToDoubleZero(contract.getCurrentCredit());
    		Double currentCredit = new BigDecimal(StringUtil.nullToZero(contract.getCurrentCredit())).
    				add(new BigDecimal(StringUtil.nullToZero(chargedCredit))).doubleValue();
    		
            Boolean isCutOff = false;    // 차단여부

            if(meter != null && meter.getModel() != null && currentCredit > 0d && meter.getMeterStatusCodeId() != null) {
	            // CommandOperationUtil 생성 후 개발 부분 Start
            	Code code = codeDao.get(meter.getMeterStatusCodeId());
            	 isCutOff = code.getCode().equals("1.3.3.4");
            }
            // CommandOperationUtil 생성 후 개발 부분 End
            
            Integer lastChargeCnt = new Integer(StringUtil.nullToZero(contract.getLastChargeCnt())) + 1;
            Code keyCode = null;
            Operator operator = operatorDao.get(operatorId);
            // insert ContractChangeLog
            addContractChangeLog(contract, operator, "lastTokenDate", contract.getLastTokenDate(), DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"),null);
            addContractChangeLog(contract, operator, "chargedCredit", contract.getChargedCredit(), paidAmount,null);
            addContractChangeLog(contract, operator, "currentCredit", contract.getCurrentCredit(), currentCredit.toString(),null);
            addContractChangeLog(contract, operator, "lastChargeCnt", contract.getLastChargeCnt(), lastChargeCnt.toString(),null);

            // chargeCredit 안에 포함 init Credit이 포함되어 결제 되므로 contract에서는 contract Price를 null로 바꾼다.
            if(contractPrice != null && contractPrice != 0d) {
            	contract.setContractPrice(null);
            }
            
            contract.setLastTokenDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            contract.setChargedCredit(paidAmount);
            contract.setCurrentCredit(currentCredit);
            contract.setLastChargeCnt(lastChargeCnt);
            contract.setCashPoint(operator.getCashPoint());
            
            PrepaymentLog prepaymentLog = new PrepaymentLog();
            List<DebtLog> debtLogList = new ArrayList<DebtLog>();
            
            if(isPartpayment) {
            	Map<String,Object> partpayResult = partpayManagement(contract, debtEntList, currentDebtList, operator, customerNo);
            	contract = (Contract)partpayResult.get("contract");
                debtEntList = (List<DebtEnt>)partpayResult.get("debtEntList");
                prepaymentLog = (PrepaymentLog)partpayResult.get("prepaymentLog");
                debtLogList = (List<DebtLog>)partpayResult.get("debtLogList");
            } else {
            	Double preArrears = contract.getCurrentArrears();
            	prepaymentLog.setArrears(preArrears);
            	prepaymentLog.setPreArrears(preArrears);
            	prepaymentLog.setChargedArrears(0d);
            	for (int i = 0; i < currentDebtList.size(); i++) {
                	Map<String, Object> map = currentDebtList.get(i);
        			Double payAmount = map.get("payAmount") != null ? Double.parseDouble(map.get("payAmount").toString()) : 0.0;
        			
        			if(((String)map.get("debtType")).equals("Arrears")) {
        				Double currentArrears = new BigDecimal(StringUtil.nullToDoubleZero(preArrears)).
        	    				subtract(new BigDecimal(payAmount)).doubleValue();
        				contract.setCurrentArrears(currentArrears);
                		
                		prepaymentLog.setArrears(currentArrears);
                		prepaymentLog.setChargedArrears(payAmount);
                		
                		addContractChangeLog(contract, operator, "currentArrears", preArrears, currentArrears, null);
        			} else {
        				Double tempCurrentDebt = 0.0;
        				for (int j = 0; j < debtEntList.size(); j++) {
        					DebtEnt tempDebtEnt = debtEntList.get(j);
        					if(tempDebtEnt.getDebtRef().equals(map.get("debtRef").toString())) {
								DebtLog debtLog = new DebtLog();
								Double preDebt = tempDebtEnt.getDebtAmount();
								tempCurrentDebt = new BigDecimal(StringUtil.nullToDoubleZero(preDebt)).
	    								subtract(new BigDecimal(payAmount)).doubleValue();
								debtLog.setPreDebt(preDebt);
				        		debtLog.setDebt(tempCurrentDebt);
				        		debtLog.setChargedDebt(payAmount);
								debtLog.setDebtRef(map.get("debtRef").toString());
								debtLog.setCustomerId(customerNo);
								debtLog.setDebtType(tempDebtEnt.getDebtType());
								
								debtLogList.add(debtLog);
								
								tempDebtEnt.setDebtAmount(tempCurrentDebt);
								
								addContractChangeLog(contract, operator, "currentDebt", preDebt, tempCurrentDebt, tempDebtEnt.getDebtType());
								
								debtEntList.set(j, tempDebtEnt);
        					}
        				}
        			}
            	}
            	
                if(debtLogList.size() < debtEntList.size()) {
                	List<DebtLog> tempLogList = new ArrayList<DebtLog>();
                	for (int j = 0; j < debtLogList.size(); j++) {
                		tempLogList.add(debtLogList.get(j));
					}
                	
                	for (int i = 0; i < debtEntList.size(); i++) {
                		boolean flag = false;
                		DebtEnt debtEnt = debtEntList.get(i);
                		for (int j = 0; j < tempLogList.size(); j++) {
                			DebtLog debtLog = tempLogList.get(j);
                			if(!debtLog.getDebtRef().equals(debtEnt.getDebtRef())) {
                				flag = true;
                			} else {
                				flag = false;
                				break;
                			}
						}
                		
                		if(tempLogList.size() == 0) {
                			flag = true;
                		}
                		
                		if(flag) {
                			DebtLog emptyDebtLog = new DebtLog();
	                		emptyDebtLog.setPreDebt(debtEnt.getDebtAmount());
	                		emptyDebtLog.setDebt(debtEnt.getDebtAmount());
	                		emptyDebtLog.setChargedDebt(0d);
	                		emptyDebtLog.setDebtRef(debtEnt.getDebtRef());
	                		emptyDebtLog.setCustomerId(customerNo);
	                		emptyDebtLog.setDebtType(debtEnt.getDebtType());
	                		if(debtEnt.getDebtContractCount() != null && debtEnt.getDebtContractCount() > 0) {
	                			emptyDebtLog.setPartpayInfo(StringUtil.nullToZero(debtEnt.getDebtPaymentCount())+"/"+debtEnt.getDebtPaymentCount());
	                		}
	            			debtLogList.add(emptyDebtLog);
                		}
                	}
                }
            	
            }

            // 미터가 교체되고 처음 결제 되는 경우 로그에 미터 교체 비용관련 항목이 추가된다.
            Integer daysFromCharge;
            if(lastChargeDate != null  && !lastChargeDate.equals("") ) {
            	daysFromCharge = TimeUtil.getDayDuration(lastChargeDate, DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            } else {
            	daysFromCharge = 0;
            }

            VendorCasher vendorCasher = vendorCasherDao.getByVendorCasherId(casherId, operator);
            Code keyType = codeDao.get(payTypeId);

            prepaymentLog.setVendorCasher(vendorCasher);
            prepaymentLog.setDaysFromCharge(daysFromCharge);
            prepaymentLog.setCustomer(contract.getCustomer());
            prepaymentLog.setContract(contract);
            prepaymentLog.setKeyType(keyCode);
            prepaymentLog.setChargedCredit(chargedCredit);
            prepaymentLog.setLastTokenDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            prepaymentLog.setOperator(operator);
            Integer emergencyYn = null;
            if (contract.getEmergencyCreditAvailable() != null) {
                emergencyYn = (contract.getEmergencyCreditAvailable()) ? 1 : 0;
            }
            prepaymentLog.setEmergencyCreditAvailable(emergencyYn);
            prepaymentLog.setPowerLimit(contractDemand);
            prepaymentLog.setPreBalance(preCredit);
            prepaymentLog.setBalance(currentCredit);
            prepaymentLog.setLocation(contract.getLocation());
            prepaymentLog.setTariffIndex(contract.getTariffIndex());
            prepaymentLog.setPayType(keyType);
            
            //지불타입이 Check면 BankNo와 CheckNo를 저장한다.
            if("18.2".equals(keyType.getCode())) {
            	prepaymentLog.setBankOfficeCode(bankCode);
            	prepaymentLog.setChequeNo(checkNo);
            }
            prepaymentLogDao.add(prepaymentLog);
            
            log.info("prepaymentLog has been added");
            
			DepositHistory dh = new DepositHistory();
			dh.setOperator(updateOperator);
			dh.setContract(contract);
			dh.setCustomer(contract.getCustomer());
			dh.setMeter(meter);
			dh.setChangeDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			dh.setChargeCredit(paidAmount);
			dh.setDeposit(updateOperator.getDeposit());
			dh.setPrepaymentLog(prepaymentLog);
			
			depositHistoryDao.add(dh);    
			
			for (int i = 0; i < debtLogList.size(); i++) {
				DebtLog debtLog = (DebtLog)debtLogList.get(i);
				debtLog.setPrepaymentLog(prepaymentLog);
				debtLogDao.add(debtLog);
			}

			// operator update
			if ( isVendor ) {
				updateOperator.setDeposit(currentDeposit - paidAmount);
			}
			operatorDao.update(updateOperator);
			log.info("operator update is completed");
			
			for (int i = 0; i < debtEntList.size(); i++) {
				debtEntDao.update((DebtEnt)debtEntList.get(i));
			}
			

            // update Contract
            contractDao.update(contract);
            
            log.info("contractInfo has been updated");

            log.info("depositHistory has been added");
            
            transactionManager.commit(txStatus);
            
        	Map<String,Object> smsInfo = new HashMap<String,Object>();
        	smsInfo.put("contract", contract);
        	smsInfo.put("chargedCredit", chargedCredit);
        	smsInfo.put("preCredit", preCredit);
        	smsInfo.put("isCutOff", isCutOff);
        	
            rtnStr = "success";
            result.put("result", rtnStr);
            result.put("prepaymentLogId", prepaymentLog.getId());
            result.put("isCutOff", isCutOff);
            result.put("credit", currentCredit);
            result.put("smsInfo", smsInfo);
            return result;
        } catch (Exception e) {
        	log.error(e, e); 
        	
        	if (txStatus != null && !txStatus.isCompleted()) {
                transactionManager.rollback(txStatus);
            }

            e.printStackTrace();
            rtnStr = "fail";
            
            result.put("result", rtnStr);
            return result;
        } 
    }
	
    private Map<String,Object> partpayManagement(Contract contract, List<DebtEnt> preDebtList, List<Map<String,Object>> debtList, Operator operator, String customerNo) throws Exception {
    	Map<String,Object> returnMap = new HashMap<String,Object>();
    	PrepaymentLog prepaymentLog = new PrepaymentLog();
    	List<DebtLog> debtLogList = new ArrayList<DebtLog>();
    	
    	Double preArrears = StringUtil.nullToDoubleZero(contract.getCurrentArrears());
    	prepaymentLog.setPreArrears(preArrears);
    	prepaymentLog.setArrears(preArrears);
    	prepaymentLog.setChargedArrears(0d);
    	
		Integer tempPaymentCount = null;
		Integer prePaymentCount = null;
		Integer preContractCount = null;
		for (int i = 0; i < debtList.size(); i++) {
			Map<String, Object> map = debtList.get(i);
			Double partCredit = map.get("partCredit") != null ? Double.parseDouble(map.get("partCredit").toString()) : 0.0;
			Double payAmount = map.get("payAmount") != null ? Double.parseDouble(map.get("payAmount").toString()) : 0.0;
			
			if(((String)map.get("debtType")).equals("Arrears")) {
				Double currentArrears = new BigDecimal(StringUtil.nullToDoubleZero(preArrears)).
	    				subtract(new BigDecimal(payAmount)).doubleValue();
				contract.setCurrentArrears(currentArrears);
				
				preArrears = contract.getCurrentArrears();
				prePaymentCount = contract.getArrearsPaymentCount();
	        	//charge하는 경우 타는 소스이므로 첫 charge시에는 arrearsPaymentCount가 1이되어야 한다.
	        	tempPaymentCount = prePaymentCount == null ? 1 : prePaymentCount + 1;
				contract.setArrearsPaymentCount(tempPaymentCount);
				
				//분할 납부금액 초기화
				if((partCredit < payAmount) && contract.getArrearsContractCount() != null) {
					Double preFirstArrears = contract.getFirstArrears();
					preContractCount = contract.getArrearsContractCount();
					
					contract.setFirstArrears(currentArrears);
					contract.setArrearsContractCount(contract.getArrearsContractCount()-contract.getArrearsPaymentCount());
					contract.setArrearsPaymentCount(0);
					
					addContractChangeLog(contract, operator, "firstArrears", preFirstArrears, contract.getFirstArrears(),null);
	        		addContractChangeLog(contract, operator, "arrearsContractCount", preContractCount, contract.getArrearsContractCount(),null);
				}
				
				//미수금을 모두 갚았을 경우 || 미수금이 없을 경우
				if((currentArrears <= 0 || currentArrears == null) || contract.getArrearsContractCount() == null) {
					Integer preACC = contract.getArrearsContractCount();
					Double preFA = contract.getFirstArrears();
					
        			contract.setArrearsPaymentCount(null);
        			contract.setArrearsContractCount(null);
        			contract.setFirstArrears(null);
        			
        			addContractChangeLog(contract, operator, "firstArrears", preFA, null,null);
        			addContractChangeLog(contract, operator, "arrearsContractCount", preACC, null,null);
        		}
				
				addContractChangeLog(contract, operator, "currentArrears", preArrears, contract.getCurrentArrears(),null);
        		addContractChangeLog(contract, operator, "arrearsPaymentCount", prePaymentCount, contract.getArrearsPaymentCount(),null);
        		if((preArrears > 0) && contract.getArrearsContractCount() != null && contract.getArrearsPaymentCount() != null) {
                	prepaymentLog.setPartpayInfo(contract.getArrearsPaymentCount()+"/"+contract.getArrearsContractCount());
                }
        		
        		//prepaymentLog.setPreArrears(preArrears);
        		prepaymentLog.setArrears(currentArrears);
        		prepaymentLog.setChargedArrears(payAmount);
        		
        		
			} else {
				Double tempCurrentDebt = 0.0;
				for (int j = 0; j < preDebtList.size(); j++) {
					DebtEnt tempDebtEnt = preDebtList.get(j);
					DebtLog debtLog = new DebtLog();
					if(tempDebtEnt.getDebtRef().equals(StringUtil.nullToBlank(map.get("debtRef").toString()))) {
						Double preDebt = tempDebtEnt.getDebtAmount();
						tempCurrentDebt = new BigDecimal(StringUtil.nullToDoubleZero(preDebt)).
								subtract(new BigDecimal(payAmount)).doubleValue();
						
						prePaymentCount = tempDebtEnt.getDebtPaymentCount();
						tempPaymentCount = prePaymentCount == null ? 1 : prePaymentCount + 1;
						tempDebtEnt.setDebtPaymentCount(tempPaymentCount);
						tempDebtEnt.setDebtAmount(tempCurrentDebt);
						
						//분할 납부금액 초기화
						if(partCredit < payAmount) {
							Double preFirstDebt = tempDebtEnt.getFirstDebt();
							preContractCount = tempDebtEnt.getDebtContractCount();
							
							tempDebtEnt.setFirstDebt(tempCurrentDebt);
							tempDebtEnt.setDebtContractCount(tempDebtEnt.getDebtContractCount()-tempDebtEnt.getDebtPaymentCount());
							tempDebtEnt.setDebtPaymentCount(0);
							
							addContractChangeLog(contract, operator, "firstDebt", preFirstDebt, tempDebtEnt.getFirstDebt(),tempDebtEnt.getDebtType());
			        		addContractChangeLog(contract, operator, "debtContractCount", preContractCount, tempDebtEnt.getDebtContractCount(),tempDebtEnt.getDebtType());
						}
						
						//미수금을 모두 갚았을 경우 || 미수금이 없을 경우
						if((tempCurrentDebt <= 0 || tempCurrentDebt == null) || tempDebtEnt.getDebtContractCount() == null) {
							Integer preDCC = tempDebtEnt.getDebtContractCount();
							Double preFD = tempDebtEnt.getFirstDebt();
							
							tempDebtEnt.setFirstDebt(null);
							tempDebtEnt.setDebtContractCount(null);
							tempDebtEnt.setDebtPaymentCount(null);
							
							addContractChangeLog(contract, operator, "firstDebt", preFD, null,tempDebtEnt.getDebtType());
			        		addContractChangeLog(contract, operator, "debtContractCount", preDCC, null,tempDebtEnt.getDebtType());
		        		}
		        		
						addContractChangeLog(contract, operator, "debtAmount", preDebt, tempDebtEnt.getDebtAmount(),tempDebtEnt.getDebtType());
		        		addContractChangeLog(contract, operator, "debtPaymentCount", prePaymentCount, tempDebtEnt.getDebtPaymentCount(),tempDebtEnt.getDebtType());
		        		
		        		if((preDebt > 0) && tempDebtEnt.getDebtContractCount() != null && tempDebtEnt.getDebtPaymentCount() != null) {
		        			debtLog.setPartpayInfo(tempDebtEnt.getDebtPaymentCount()+"/"+tempDebtEnt.getDebtContractCount());
		                }
		        		
		        		debtLog.setPreDebt(preDebt);
		        		debtLog.setDebt(tempCurrentDebt);
		        		debtLog.setChargedDebt(payAmount);
						debtLog.setDebtRef(map.get("debtRef").toString());
						debtLog.setCustomerId(customerNo);
						debtLog.setDebtType(map.get("debtType").toString());
						debtLogList.add(debtLog);
						preDebtList.set(j, tempDebtEnt);
					}
				}
			}
		}

        if(debtLogList.size() < preDebtList.size()) {
        	List<DebtLog> tempLogList = new ArrayList<DebtLog>();
        	for (int j = 0; j < debtLogList.size(); j++) {
        		tempLogList.add(debtLogList.get(j));
			}
        	
        	for (int i = 0; i < preDebtList.size(); i++) {
        		boolean flag = false;
        		DebtEnt debtEnt = preDebtList.get(i);
        		for (int j = 0; j < tempLogList.size(); j++) {
        			DebtLog debtLog = tempLogList.get(j);
        			if(!debtLog.getDebtRef().equals(debtEnt.getDebtRef())) {
        				flag = true;
        			} else {
        				flag = false;
        				break;
        			}
				}
        		
        		if(tempLogList.size() == 0) {
        			flag = true;
        		}
        		
        		if(flag) {
        			DebtLog emptyDebtLog = new DebtLog();
            		emptyDebtLog.setPreDebt(debtEnt.getDebtAmount());
            		emptyDebtLog.setDebt(debtEnt.getDebtAmount());
            		emptyDebtLog.setChargedDebt(0d);
            		emptyDebtLog.setDebtRef(debtEnt.getDebtRef());
            		emptyDebtLog.setCustomerId(customerNo);
            		emptyDebtLog.setDebtType(debtEnt.getDebtType());
            		if(debtEnt.getDebtContractCount() != null && debtEnt.getDebtContractCount() > 0) {
            			emptyDebtLog.setPartpayInfo(StringUtil.nullToZero(debtEnt.getDebtPaymentCount())+"/"+debtEnt.getDebtContractCount());
            		}
        			debtLogList.add(emptyDebtLog);
        		}
        	}
        }
        
        returnMap.put("contract", contract);
        returnMap.put("debtEntList", preDebtList);
        returnMap.put("prepaymentLog", prepaymentLog);
        returnMap.put("debtLogList", debtLogList);
        
        return returnMap;

    }
	

    /**
     * method name : addContractChangeLog
     * method Desc : ContractChangeLog 에 데이터 insert
     *
     * @param contract
     * @param field
     * @param beforeValue
     * @param afterValue
     */
    private void addContractChangeLog(Contract contract, Operator operator, String field, Object beforeValue, Object afterValue, String type) {

        // ContractChangeLog Insert
        ContractChangeLog contractChangeLog = new ContractChangeLog();

        contractChangeLog.setContract(contract);
        contractChangeLog.setCustomer(contract.getCustomer());
        contractChangeLog.setStartDatetime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        contractChangeLog.setChangeField(field);

        if (beforeValue == null) {
            contractChangeLog.setBeforeValue(null);
        } else {
        	if(type != null) {
        		contractChangeLog.setBeforeValue("["+type+"] "+StringUtil.nullToBlank(beforeValue));
        	} else {
        		contractChangeLog.setBeforeValue(StringUtil.nullToBlank(beforeValue));
        	}
        }

        if (afterValue == null) {
            contractChangeLog.setAfterValue(null);
        } else {
        	if(type != null) {
        		contractChangeLog.setAfterValue("["+type+"] "+StringUtil.nullToBlank(afterValue));
        	} else {
        		contractChangeLog.setAfterValue(StringUtil.nullToBlank(afterValue));
        	}
            
        }

        contractChangeLog.setOperator(operator);
        contractChangeLog.setWriteDatetime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

        contractChangeLogDao.add(contractChangeLog);
    }

	@Transactional(rollbackFor=Exception.class)
	public Map<String,Object> cancelTransaction(Long id, String operatorId, String reason) {
		Map<String,Object> returnData = new HashMap<String,Object>();
		String rtn = "success";
		Contract contract = null;
		Double totalAmount = 0d;
		Properties prop = new Properties();
		
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
			
			String isPartpaymentStr = prop.getProperty("partpayment.use");
			Boolean isPartpayment = (isPartpaymentStr == null || "".equals(isPartpaymentStr)) ? false : Boolean.parseBoolean(isPartpaymentStr);
			
			PrepaymentLog prepayLog = prepaymentLogDao.get(id);
			Boolean isCancelled = prepayLog.getIsCanceled() == null ? false : prepayLog.getIsCanceled();
			//Cancel된 내역인지 다시 한번 확인
			if(!isCancelled) {
				prepayLog.setIsCanceled(true);
				prepayLog.setCancelDate(TimeUtil.getCurrentTime());
				if(!reason.isEmpty()) {
					prepayLog.setCancelReason(reason);
				}
				prepaymentLogDao.update(prepayLog);
				
				Operator operator = operatorDao.getOperatorByLoginId(operatorId);
				Operator commitedVendor = prepayLog.getOperator();
	
				Integer contractId = prepayLog.getContractId();
				contract = contractDao.get(contractId);
	
				Double arrears = (Double) ObjectUtils.defaultIfNull(contract.getCurrentArrears() + prepayLog.getChargedArrears(), null);
				Double balance = (Double) ObjectUtils.defaultIfNull(contract.getCurrentCredit() - prepayLog.getChargedCredit(), null);
				List<Map<String,Object>> debtsList = new ArrayList<Map<String,Object>>();
				
				List<DebtEnt> debtEntList = debtEntDao.getDebt(prepayLog.getCustomer().getCustomerNo(), null, null);
				List<DebtLog> debtLogList = debtLogDao.getDebtLog(id);
				for (int i = 0; i < debtEntList.size(); i++) {
					DebtEnt debtEnt = debtEntList.get(i);
					for (int j = 0; j < debtLogList.size(); j++) {
						DebtLog debtLog = debtLogList.get(j);
						if(debtEnt.getDebtRef().equals(debtLog.getDebtRef())) {
							Map<String,Object> debts = new HashMap<String,Object>();
							Double preDebt = debtEnt.getDebtAmount();
							Double debtAmount = StringUtil.nullToDoubleZero(debtEnt.getDebtAmount()) + debtLog.getChargedDebt();
							totalAmount += StringUtil.nullToDoubleZero(debtLog.getChargedDebt());
							debts.put("preDebt", preDebt);
							debts.put("debtAmount", debtAmount);
							debts.put("debt", debtEnt);
							debts.put("debtLog", debtLog);
							debts.put("debtType",debtEnt.getDebtType());
							debtsList.add(debts);
						}
					}
				}
				
				
				totalAmount += StringUtil.nullToDoubleZero(prepayLog.getChargedArrears()) + 
						StringUtil.nullToDoubleZero(prepayLog.getChargedCredit()); 
						
				if ( commitedVendor.getRole().getName().equals("vendor")) {
					commitedVendor = prepayLog.getOperator();
					commitedVendor.setDeposit(commitedVendor.getDeposit() + totalAmount );		
					operatorDao.update(commitedVendor);
				}
	
				Double preArrears = contract.getCurrentArrears();
				Double preBalance = contract.getCurrentCredit();
				Double preChargedCredit = contract.getChargedCredit();
				
				addContractChangeLog(contract, operator, "currentCredit", preBalance, balance,null);
				addContractChangeLog(contract, operator, "currentArrears", preArrears, arrears,null);
				addContractChangeLog(contract, operator, "chargedCredit", preChargedCredit, -totalAmount,null);
				
				for (int i = 0; i < debtsList.size(); i++) {
					Map<String,Object> debts = debtsList.get(i);
					addContractChangeLog(contract, operator, "currentDebts", debts.get("preDebt"), debts.get("debtAmount"),StringUtil.nullToBlank(debts.get("debtType")));
					DebtEnt debt = (DebtEnt)debts.get("debt");
					debt.setDebtAmount((Double)debts.get("debtAmount"));
					debtEntDao.update(debt);
				}
				
				//분할납부사용중이면서 해당 로그에 arrears를 charge했던 로그를 취소하는 경우
				if(isPartpayment ) {
					if(prepayLog.getChargedArrears() > 0) {
						//reset Logic
						Double preFirstArrears = contract.getFirstArrears();
						Integer preContractCount = contract.getArrearsContractCount();
						Integer prePaymentCount = contract.getArrearsPaymentCount();
						
						Integer tempContractCount = preContractCount == null ? 0 : preContractCount;
						Integer tempPaymentCount = prePaymentCount == null ? 0 : prePaymentCount;
						Integer newContractCount = tempContractCount-tempPaymentCount+1;
						
						contract.setFirstArrears(arrears);
						contract.setArrearsContractCount(newContractCount);
						contract.setArrearsPaymentCount(0);
						
						addContractChangeLog(contract, operator, "firstArrears", preFirstArrears, arrears,null);
						addContractChangeLog(contract, operator, "arrearsContractCount", preContractCount, newContractCount,null);
						addContractChangeLog(contract, operator, "arrearsPaymentCount", prePaymentCount, 0,null);
					}
					
					for (int i = 0; i < debtsList.size(); i++) {
						Map<String,Object> debts = debtsList.get(i);
						DebtEnt debt = (DebtEnt)debts.get("debt");
						DebtLog debtLog = (DebtLog)debts.get("debtLog");
						Double debtAmount = (Double)debts.get("debtAmount");
						if(debtLog.getChargedDebt() > 0) {
							//reset Logic
							Double preFirstDebts = debt.getFirstDebt();
							Integer preContractCount = debt.getDebtContractCount();
							Integer prePaymentCount = debt.getDebtPaymentCount();
							
							Integer tempContractCount = preContractCount == null ? 0 : preContractCount;
							Integer tempPaymentCount = prePaymentCount == null ? 0 : prePaymentCount;
							Integer newContractCount = tempContractCount-tempPaymentCount+1;
							
							debt.setFirstDebt(debtAmount);
							debt.setDebtContractCount(newContractCount);
							debt.setDebtPaymentCount(0);
							
							addContractChangeLog(contract, operator, "firstDebts", preFirstDebts, debtAmount,debt.getDebtType());
							addContractChangeLog(contract, operator, "debtContractCount", preContractCount, newContractCount,debt.getDebtType());
							addContractChangeLog(contract, operator, "debtPaymentCount", prePaymentCount, 0,debt.getDebtType());
							
						}
					}
				}
				
				//분할납부가 끝난고객의 경우
				contract.setCurrentCredit(balance);
				contract.setCurrentArrears(arrears);
				//수정 전 소스의 프로세스대로 수정했으나 본래 모델에 정의된 본래 사용의도와 동일하지 않음.
				contract.setChargedCredit(-totalAmount);
				contractDao.update(contract);
				
				for (int i = 0; i < debtsList.size(); i++) {
					Map<String,Object> debts = debtsList.get(i);
					DebtEnt debt = (DebtEnt)debts.get("debt");
					Double debtAmount = (Double)debts.get("debtAmount");
					
					debt.setDebtAmount(debtAmount);
					debtEntDao.update(debt);
				}
				
				try {
					//충전 취소 후 SMS 전송
					
					Supplier supplier = supplierDao.get(contract.getSupplierId());
			        DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
			        
					Properties messageProp = new Properties();
			    		
			        String lang = supplier.getLang().getCode_2letter();
			        InputStream ip = getClass().getClassLoader().getResourceAsStream("message_"+ lang +".properties");
			        if(ip == null){
			        	ip = getClass().getClassLoader().getResourceAsStream("message_en.properties");	        	
			        }
			        messageProp.load(ip);
		    		
		    		log.info("prop load : "+prop.containsKey("smsClassPath"));
			        
					String text =  messageProp.getProperty("aimir.alert.cancelCharge").replace("$AMOUNT", cdf.format(totalAmount)) + messageProp.getProperty("aimir.price.unit");
					Map<String,Object> smsInfo = new HashMap<String,Object>();
					smsInfo.put("contract", contract);
					smsInfo.put("text", text);
					returnData.put("smsInfo", smsInfo);
				} catch (Exception e) {
					log.error(e,e);
				}
				
			} else {
				rtn = "cancelData";
			}
		} catch( Exception e ) {
			rtn = "failed";
		}
		
		returnData.put("result", rtn);
		return returnData;
	}
    
    public List<Map<String, Object>> getDebtArrearsLog(Long prepaymentLogId) {
    	return debtLogDao.getDebtArrearsLog(prepaymentLogId);
    }


	@Override
	public Map<String, Object> getVendorCustomerReceiptDataWithDebt(Map<String, Object> condition) {
    	Map<String, Object> result = new HashMap<String, Object>();
        Integer supplierId = (Integer)condition.get("supplierId");
        Integer contractId = (Integer)condition.get("contractId");
        Long prepaymentLogId = (Long)condition.get("prepaymentLogId");
        
        Map<String, Object> prepaymentLogWithDebt = debtLogDao.getPrepaymentLogWithDebt(prepaymentLogId);
        
        Integer operatorId = prepaymentLogWithDebt.get("OPERATORID") == null ? null : Integer.parseInt(prepaymentLogWithDebt.get("OPERATORID").toString());
        Operator operator = null;
        if (operatorId != null) {
        	operator = operatorDao.get(operatorId);	
        }        

        // 결제 관련 임의로 계정을 admin으로 지정한다.
        if (operatorId == null) {
        	operator = operatorDao.getOperatorByLoginId("admin");
        }
        Supplier supplier = supplierDao.get(supplierId);
        Contract contract = contractDao.get(contractId);
        
        Double chargedCredit = prepaymentLogWithDebt.get("CHARGEDCREDIT") == null ? 0.0 : Double.parseDouble(prepaymentLogWithDebt.get("CHARGEDCREDIT").toString());
        Double chargedArrears = prepaymentLogWithDebt.get("CHARGEDARREARS") == null ? 0.0 : Double.parseDouble(prepaymentLogWithDebt.get("CHARGEDARREARS").toString());
        Double chargedDebts = prepaymentLogWithDebt.get("CHARGEDDEBTSUM") == null ? 0.0 : Double.parseDouble(prepaymentLogWithDebt.get("CHARGEDDEBTSUM").toString());
        Integer daysFromCharge = prepaymentLogWithDebt.get("DAYSFROMCHARGE") == null ? null : Integer.parseInt(prepaymentLogWithDebt.get("DAYSFROMCHARGE").toString());
        BigDecimal bdChargedCredit = new BigDecimal(chargedCredit);
        BigDecimal bdChargedArrears = new BigDecimal(chargedArrears);
        BigDecimal bdChargedDebts = new BigDecimal(chargedDebts);
//        Double totalAmount = ((chargedCredit == null) ? 0D : chargedCredit) + ((chargedArrears == null) ? 0D : chargedArrears);
        Double totalAmount = bdChargedCredit.add(bdChargedArrears).add(bdChargedDebts).doubleValue();
        DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        
        String country = supplier.getCountry().getCode_2letter();
        String lang = supplier.getLang().getCode_2letter();
        String date = "";
        String dateByYyyymmdd = "";
        
        String vendorDistinct = "";
        String customerName = "";
        String customerNumber = "";
        String address = "";
        String customerAddr = "";
        String meterId = "";
        String lastMeterId = "";
        String district = "";
        String tarrif = "";
        String casherId = "";
        String casherName = "";        
        
        if (operatorId == null) {
        	vendorDistinct = operator.getLocation().getName();
        } else {
        	vendorDistinct = StringUtil.nullToBlank(prepaymentLogWithDebt.get("OPERATORL"));
        }
        
        if (prepaymentLogWithDebt.get("CUSTID") != null) {
        	customerName = StringUtil.nullToBlank(prepaymentLogWithDebt.get("CUSTOMERNAME"));
        	customerNumber = StringUtil.nullToBlank(prepaymentLogWithDebt.get("CUSTOMERNO"));
        	date = TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(prepaymentLogWithDebt.get("LASTTOKENDATE")), lang, country);
        	if(!date.isEmpty()) {
        		dateByYyyymmdd = TimeLocaleUtil.getLocaleDate(prepaymentLogWithDebt.get("LASTTOKENDATE").toString().substring(0,8), lang, country);
        	}
        }
        
        if (prepaymentLogWithDebt.get("METERID") != null) {
        	meterId = StringUtil.nullToBlank(prepaymentLogWithDebt.get("MDSID"));
        	lastMeterId = StringUtil.nullToBlank(prepaymentLogWithDebt.get("INSTALLPROPERTY"));
        	customerName = StringUtil.nullToBlank(prepaymentLogWithDebt.get("CUSTOMERNAME"));
        }
        
        if (prepaymentLogWithDebt.get("TARIFFID") != null) {
        	tarrif = StringUtil.nullToBlank(prepaymentLogWithDebt.get("TARIFFNAME"));
        }
        
        if (prepaymentLogWithDebt.get("VCID") != null) {
        	casherId = StringUtil.nullToBlank(prepaymentLogWithDebt.get("CASHERID"));
        	casherName = StringUtil.nullToBlank(prepaymentLogWithDebt.get("CASHERNAME"));
        }
        
        if (prepaymentLogWithDebt.get("CONTRACTL") != null) {
        	address = StringUtil.nullToBlank(prepaymentLogWithDebt.get("CONTRACTL"));

        	Location tempLoc = contract.getLocation();
        	Location districLoc = null;
        	List<Location> tempLocList = new ArrayList<Location>();
        	
        	for (int i = 0; i < 20; i++) {
        	    if (tempLoc.getParent() != null) {
        	        tempLocList.add(tempLoc.getParent());
        	        tempLoc = tempLoc.getParent();
        	    } else {
        	        break;
        	    }
        	}

        	if (tempLocList.size() > 2) {
        	    districLoc = tempLocList.get(tempLocList.size()-3);
        	} else {
        	    districLoc = contract.getLocation();
        	}
 
        	district = districLoc.getName();
        }
        
        if(prepaymentLogWithDebt.get("VCID") != null) {
        	customerAddr = StringUtil.nullToBlank(prepaymentLogWithDebt.get("ADDRESS"));
        }

        // 일반 영수증의 경우
        result.put("daysFromCharge", daysFromCharge);
        result.put("vendorName", StringUtil.nullToBlank(prepaymentLogWithDebt.get("OPERATORNAME")));
        result.put("vendorLocation", vendorDistinct);
        result.put("logId", prepaymentLogId);
        result.put("date", date);
        result.put("dateByYyyymmdd", dateByYyyymmdd);
        result.put("customer", customerName);
        result.put("customerNumber", customerNumber);
        result.put("meter", meterId);
        result.put("gCode", contract.getContractNumber());
        result.put("activity", tarrif);
        result.put("distinct", district);
        result.put("address", address);
        result.put("customerAddr", customerAddr);
        result.put("amount", cdf.format(chargedCredit));
        result.put("casherId", casherId);
        result.put("casherName", casherName);
        result.put("currentBalance", prepaymentLogWithDebt.get("BALANCE") == null ? "" : cdf.format(prepaymentLogWithDebt.get("BALANCE")));
        result.put("preBalance", prepaymentLogWithDebt.get("PREBALANCE") == null ? "" : cdf.format(prepaymentLogWithDebt.get("PREBALANCE")));
        result.put("payType", prepaymentLogWithDebt.get("PAYTYPE") != null ? prepaymentLogWithDebt.get("PAYTYPE") : "Cash"); // default : cash

        // 결제 영수증 미수금이 있는 경우 
        if (prepaymentLogWithDebt.get("PREARREARS") != null 
        		&& StringUtil.nullToDoubleZero(Double.parseDouble(prepaymentLogWithDebt.get("PREARREARS").toString())) != 0d) {
        	Double preArrears = Double.parseDouble(prepaymentLogWithDebt.get("PREARREARS").toString());
        	result.put("arrears", cdf.format(chargedArrears));
        	result.put("preArrears", cdf.format(preArrears * (-1)));
        	
        	Double tempArrears = prepaymentLogWithDebt.get("ARREARS") == null ? 0.0 : Double.parseDouble(prepaymentLogWithDebt.get("ARREARS").toString());
        	if (preArrears > 0d) {
        		result.put("currentArrears",  cdf.format(tempArrears * (-1)));
        	} else {
        		result.put("currentArrears", cdf.format(tempArrears));
        	}
        	
        	if(prepaymentLogWithDebt.get("INITCREDIT") != null && Double.parseDouble(prepaymentLogWithDebt.get("INITCREDIT").toString()) != 0d) {
        		Double initCredit = Double.parseDouble(prepaymentLogWithDebt.get("INITCREDIT").toString());
        		bdChargedCredit = bdChargedCredit.subtract(new BigDecimal(initCredit));
        		result.put("amount", cdf.format(bdChargedCredit.doubleValue()));
        		result.put("initCredit", cdf.format(initCredit));
        	}
        }
        
        if (prepaymentLogWithDebt.get("PREDEBTSUM") != null 
        		&& StringUtil.nullToDoubleZero(Double.parseDouble(prepaymentLogWithDebt.get("PREDEBTSUM").toString())) != 0d) {
        	Double preDebts = Double.parseDouble(prepaymentLogWithDebt.get("PREDEBTSUM").toString());
        	result.put("debts", cdf.format(chargedDebts));
        	result.put("preDebts", cdf.format(preDebts * (-1)));
        	Double tempDebts = prepaymentLogWithDebt.get("DEBTSUM") == null ? 0.0 : Double.parseDouble(prepaymentLogWithDebt.get("DEBTSUM").toString());
        	if (preDebts > 0d) {
        		result.put("currentDebts", cdf.format(tempDebts * (-1)));
        	} else {
        		result.put("currentDebts", cdf.format(tempDebts));
        	}
        }
        
        result.put("totalAmount", cdf.format(totalAmount));
    	result.put("lastMeter", lastMeterId);

        condition.put("lastTokenDate", prepaymentLogWithDebt.get("LASTTOKENDATE"));
        // 월 첫번째 영수증 여부 체크
        Boolean isFirst = prepaymentLogDao.checkMonthlyFirstReceipt(condition);
        result.put("isFirst", isFirst);

        // 월간 정산 영수증의 경우
        if (isFirst) {
            PrepaymentLog monthLog = prepaymentLogDao.getMonthlyPaidData(condition);

            if (monthLog != null) {
                Double govLevy = StringUtil.nullToDoubleZero(monthLog.getGovLevy());
                Double publicLevy = StringUtil.nullToDoubleZero(monthLog.getPublicLevy());
                Double monthlyServiceCharge = StringUtil.nullToDoubleZero(monthLog.getMonthlyServiceCharge());
                Double monthlyTotalAmount = StringUtil.nullToDoubleZero(monthLog.getMonthlyTotalAmount());
                Double monthlyPaidAmount = StringUtil.nullToDoubleZero(monthLog.getMonthlyPaidAmount());
                Double monthlyConsumption = StringUtil.nullToDoubleZero(monthLog.getUsedConsumption());
                Double utilityRelief = StringUtil.nullToDoubleZero(monthLog.getUtilityRelief());
                Double subSidy = StringUtil.nullToDoubleZero(monthLog.getSubsidy());
                Double lifeLineSubsidy = StringUtil.nullToDoubleZero(monthLog.getLifeLineSubsidy());
                Double additionalSubsidy = StringUtil.nullToDoubleZero(monthLog.getAdditionalSubsidy());
                Double vat = StringUtil.nullToDoubleZero(monthLog.getVat());

                BigDecimal bdTotalFees = new BigDecimal("0");
                BigDecimal bdGovLevy = new BigDecimal(govLevy);
                BigDecimal bdPublicLevy = new BigDecimal(publicLevy);
                BigDecimal bdMonthlyServiceCharge = new BigDecimal(monthlyServiceCharge);
                BigDecimal bdMonthlyPaidAmount = new BigDecimal(monthlyPaidAmount);
                BigDecimal bdSubSidy = new BigDecimal(subSidy);
                BigDecimal bdLifeLineSubsidy = new BigDecimal(lifeLineSubsidy);
                BigDecimal bdAdditionalSubsidy = new BigDecimal(additionalSubsidy);
                BigDecimal bdVat = new BigDecimal(vat);
                BigDecimal bdUR = new BigDecimal(utilityRelief);
                
                String tariffName = monthLog.getTariffIndex() == null ? tarrif : monthLog.getTariffIndex().getName();
                
                if ("Residential".equals(tariffName)) {
                    // totalFees : monthlyServiceCharge + govLevy + publicLevy - additionalSubsidy - subSidy - lifeLineSubsidy
                    //bdTotalFees = bdMonthlyServiceCharge.add(bdGovLevy).add(bdPublicLevy);
                	bdTotalFees = bdMonthlyServiceCharge;
                    bdTotalFees = bdTotalFees.subtract(bdAdditionalSubsidy).subtract(bdSubSidy).subtract(bdLifeLineSubsidy).subtract(bdUR);
                } else if ("Non Residential".equals(tariffName)) {
                    // totalFees : monthlyServiceCharge + vat + govLevy + publicLevy - additionalSubsidy
                    //bdTotalFees = bdMonthlyServiceCharge.add(bdVat).add(bdGovLevy).add(bdPublicLevy).subtract(bdAdditionalSubsidy);
                    bdTotalFees = bdMonthlyServiceCharge.add(bdVat).subtract(bdAdditionalSubsidy);
                }

                result.put("monthlyPaidAmount", cdf.format(monthlyPaidAmount));
                result.put("monthlyTotalAmount", cdf.format(monthlyTotalAmount));
                result.put("monthlyConsumption", mdf.format(monthlyConsumption));
                result.put("utilityRelief", cdf.format(utilityRelief));
                result.put("serviceCharge", cdf.format(monthlyServiceCharge));
                result.put("publicLevy", cdf.format(publicLevy));
                result.put("govLevy", cdf.format(govLevy));
                result.put("govSubsidy", feeValueformat(cdf, subSidy));
                result.put("lifeLineSubsidy", feeValueformat(cdf, lifeLineSubsidy));
                result.put("additionalSubsidy", feeValueformat(cdf, additionalSubsidy));
                result.put("vat", cdf.format(vat));
                result.put("additionalAmount", cdf.format(StringUtil.nullToDoubleZero(monthLog.getUsedCost())));
                result.put("totalFees", cdf.format(bdTotalFees.doubleValue()));
                result.put("chargeValue", cdf.format(bdMonthlyPaidAmount.add(bdTotalFees)));
            } else {
                result.put("isFirst", false);
            }
        }

    	return result; 
	}
	
    private String feeValueformat(DecimalFormat cdf, Double value) {
    	String returnValue = "";
    	Double valueD = StringUtil.nullToDoubleZero(value);
    	if(valueD > 0) {
    		returnValue = "("+cdf.format(valueD)+")";
    	} else {
//    		returnValue = String.valueOf(cdf.format(Math.abs(valueD)));
    		returnValue = String.valueOf(cdf.format(valueD));
    	}
    	return returnValue;
    }
    
    public Map<String, Object> getDepositHistoryList(Map<String,Object> condition) {
    	return debtLogDao.getDepositHistoryList(condition);
    }
}
