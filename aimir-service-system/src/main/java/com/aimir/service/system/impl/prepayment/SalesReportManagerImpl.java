package com.aimir.service.system.impl.prepayment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.prepayment.SalesReportManager;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@Service(value = "SalesReportManager")
public class SalesReportManagerImpl implements SalesReportManager {
	@Autowired
	PrepaymentLogDao prepaymentLogDao;
	
	@Autowired
	ContractDao contractDao;
	
	/* (non-Javadoc)
	 * @see com.aimir.service.system.prepayment.SalesReportManager#getAddBalanceList(com.aimir.model.system.Supplier, int, int, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAddBalanceList(Supplier supplier, Integer page, Integer limit, String searchDate, String vendorId)  {
		Map<String, Object> data = prepaymentLogDao.getAddBalanceList(page, limit, searchDate, vendorId);
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("data");
        DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
        
		for ( Map<String, Object> dat : list ) {
			Double chargedCredit =(Double)dat.get("chargedCredit");
			chargedCredit = StringUtil.nullToDoubleZero(chargedCredit);
			dat.put("chargedCredit",cdf.format(chargedCredit));
			
			Double chargedArrears =(Double)dat.get("chargedArrears");
			chargedArrears = StringUtil.nullToDoubleZero(chargedArrears);
			dat.put("chargedArrears",cdf.format(chargedArrears));
			
			Double balance =(Double)dat.get("balance");
			balance = StringUtil.nullToDoubleZero(balance);
			dat.put("balance",cdf.format(balance));
			
			Double arrears =(Double)dat.get("arrears");
			arrears = StringUtil.nullToDoubleZero(arrears);
			dat.put("arrears",cdf.format(arrears));
		}
		data.put("data", list);
		return data;
	}
	
	/* (non-Javadoc)
	 * @see com.aimir.service.system.prepayment.SalesReportManager#getMonthlyGridDataList(com.aimir.model.system.Supplier, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String)
	 */
	public Map<String, Object> getMonthlyGridDataList(Supplier supplier, Integer page, Integer limit, String startDate, String endDate, String vendorId) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Integer> contractIdList = contractDao.getPrepaymentContract("Electricity");
		contractIdList.addAll(contractDao.getPrepaymentContract("Water"));
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
		DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		List<Integer> subIdList;
		
		int size= contractIdList.size();
		
		if ( page != null && limit != null ) {
			Integer start = (page -1) * limit;
			Integer end = start + limit;
			
			// 그리드의 마지막 페이지인 경우
			if (end > size) {
				end = size;
			}
			subIdList = contractIdList.subList(start, end);
		} else {
			subIdList = contractIdList;
		}
		
		for (Integer contractId : subIdList) {
			List<Map<String, Object>> log = prepaymentLogDao.getPrepaymentLogList(contractId, startDate, endDate, vendorId);
			Map<String, Object> record = new HashMap<String, Object>();

			if ( !log.isEmpty() ) {
				Map<String, Object> firstMap = log.get(0);
				Map<String, Object> lastMap = log.get(log.size() -1);
				record.put("contractNumber", StringUtil.nullToBlank(firstMap.get("contractNumber")));
				record.put("customerName", StringUtil.nullToBlank(firstMap.get("customerName")));
				record.put("mdsId", StringUtil.nullToBlank(firstMap.get("mdsId")));
				record.put("municCode", StringUtil.nullToBlank(firstMap.get("municipalityCode")));
				
				Double startBalance = firstMap.get("balance") != null ? (Double) firstMap.get("balance") : 0d;
				Double endBalance = lastMap.get("balance") != null ? (Double) lastMap.get("balance") : 0d;
				record.put("startBalance", cdf.format(startBalance));
				record.put("endBalance", cdf.format(endBalance));
				
				String startTime = StringUtil.nullToBlank(firstMap.get("lastTokenDate"));
				if ( !startTime.isEmpty() ) {
					startTime = TimeLocaleUtil.getLocaleDate(startTime, lang, country);
				}
				
				String endTime = StringUtil.nullToBlank(lastMap.get("lastTokenDate"));
				if ( !endTime.isEmpty() ) {
					endTime = TimeLocaleUtil.getLocaleDate(endTime, lang, country);
				}
				
				
				record.put("startDate", startTime);
				record.put("endDate", endTime);
				
				Double sumUsedEnergy = 0d; 
				Double sumUsedCost = 0d;
				Double sumChargedCredit = 0d;
				Double sumChargedArrears = 0d;
				
				Boolean firstRow=true;
				for (Map<String, Object> row : log) {
					
					Double usedConsumption = 0d;
					Double usedCost = 0d;
					Double chargedCredit = 0d;
					Double chargedArrears = 0d;
					
					if(firstRow) {
						firstRow = false;
					} else {
						usedConsumption =row.get("usedConsumption") != null ? (Double) row.get("usedConsumption") : 0d;
						usedCost = row.get("usedCost") !=null ? (Double) row.get("usedCost") : 0d;
						chargedCredit = row.get("chargedCredit") != null ? (Double) row.get("chargedCredit") : 0d;
						chargedArrears = row.get("chargedArrears") != null ? (Double) row.get("chargedArrears") : 0d;
					}
					
					sumUsedEnergy += usedConsumption;
					sumUsedCost += usedCost;
					sumChargedCredit += chargedCredit;
					sumChargedArrears += chargedArrears;
				}
				record.put("usedEnergy", mdf.format(sumUsedEnergy));
				record.put("usedCost", cdf.format(sumUsedCost));
				record.put("chargedCredit", cdf.format(sumChargedCredit));
				record.put("chargedArrears", cdf.format(sumChargedArrears));				
			} else {
				Contract contract = contractDao.get(contractId);
				Customer customer = contract.getCustomer();
				if ( contract != null ) {
					record.put("contractNumber", contract.getContractNumber());
				}
				if ( customer != null) {
					record.put("customerName", customer.getName());
				}
			}
			data.add(record);
		}
		result.put("data", data);
		result.put("size", contractIdList.size());
		
		return result;
	}
}
