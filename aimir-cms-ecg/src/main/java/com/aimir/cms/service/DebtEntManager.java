package com.aimir.cms.service;

import java.util.List;
import java.util.Map;

public interface DebtEntManager {
	public List<Map<String, Object>> getPrepaymentChargeList(Map<String, Object> condition) throws Exception;
	public Integer getPrepaymentChargeListTotalCount(Map<String, Object> condition);
	public List<Map<String,Object>> getDebtInfoByCustomerNo(String customerNo, String debtType, String debtRef);
	public void modifyDebtInfo(Map<String, Object> condition);
	public Map<String, Object> vendorSavePrepaymentChargeECG(Map<String, Object> condition);
	public List<Map<String, Object>> getDebtArrearsLog(Long prepaymentLogId);
	public Map<String,Object> cancelTransaction(Long id, String operatorId, String reason);
		
	public Map<String,Object> getVendorCustomerReceiptDataWithDebt(Map<String,Object> condition);
	public Map<String, Object> getDepositHistoryList(Map<String,Object> condition);
	
}
