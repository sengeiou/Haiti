package com.aimir.service.system.impl.depositMgmt;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.DepositHistoryDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.prepayment.DepositHistory;
import com.aimir.model.prepayment.VendorCasher;
import com.aimir.model.system.Operator;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.depositMgmt.DepositMgmtManager;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Service(value = "DepositMgmtManager")
public class DepositMgmtManagerImpl implements DepositMgmtManager {
	
    private static Log log = LogFactory.getLog(DepositMgmtManagerImpl.class);
    
	@Autowired
	SupplierDao supplierDao;
	
	@Autowired
	OperatorDao operatorDao;

	@Autowired
	DepositHistoryDao depositHistoryDao;
	
	public Map<String, Object> getOperatorByLoginIdAndName(int page, int limit,
			int supplierId, String loginId, String name) {
		Map<String, Object> map = new HashMap<String, Object>();
		Supplier supplier = supplierDao.get(supplierId);
		List<Operator> list = operatorDao.getVendorByLoginIdAndName(page,
				limit, loginId, name, supplierId,supplier.getName());
		int count = operatorDao.getVendorCountByLoginIdAndName(loginId, name, supplierId, supplier.getName());
		DecimalFormat df = new DecimalFormat("###,###,##0.00");
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

		if (supplier !=null) {
			df = new DecimalFormat(supplier.getCd().getPattern()); 
		}
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> operatorMap = new HashMap<String, Object>();
			Operator operator = list.get(i);
			operatorMap.put("id", operator.getLoginId());
			operatorMap.put("name", operator.getName());
			operatorMap.put("tel", operator.getTelNo());
			operatorMap.put("email", operator.getEmail());
			if (operator.getLocation() != null) {
				operatorMap.put("location", operator.getLocation().getName());
			}
			if(operator.getLastChargeDate() != null && !operator.getLastChargeDate().equals("")) {
				String localeDate = TimeLocaleUtil.getLocaleDate(operator.getLastChargeDate(), lang, country);
				operatorMap.put("lastChargeDate", localeDate);
			}
			if (operator.getDeposit() != null) {
				operatorMap.put("deposit", df.format(operator.getDeposit()));
			}
			mapList.add(operatorMap);
		}
		map.put("count", count);
		map.put("list", mapList);
		return map;
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public DepositHistory chargeDeposit(Map<String, Object> condition) {
		Integer supplierId = (Integer) condition.get("supplierId");
		String vendorId = (String) condition.get("vendorId");
		Double amount = (Double) condition.get("amount");
		String date = (String) condition.get("date");
		String loginId = (String) condition.get("loginId");
		Float commissionRate = 0f;
		Float taxRate =0f;
		Double deposit = 0d;
		
		Supplier supplier = supplierDao.get(supplierId);
		if (supplier != null) {
			commissionRate = supplier.getCommissionRate() == null ? 0f : supplier.getCommissionRate();
			taxRate = supplier.getTaxRate() == null ? 0f : supplier.getTaxRate();
		}
		Operator operator = operatorDao.getOperatorByLoginId(vendorId);
		Operator loginUser = operatorDao.getOperatorByLoginId(loginId);

		Date d = new Date(Long.parseLong(date));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = sdf.format(d);
		operator.setLastChargeDate(dateString);
		
		DepositHistory dh = new DepositHistory();
		dh.setOperator(operator);
		dh.setLoginUser(loginUser);
		dh.setChargeDeposit(amount);
		dh.setCommission(commissionRate);
		
		Double value = (1 + (new Double(commissionRate) * 0.01)) * amount;		
		dh.setValue(value);
		dh.setTaxRate(taxRate);
		
		Double commissionVal = new Double(commissionRate) * 0.01 * amount;
		Double tax = commissionVal * new Double(taxRate * 0.01);
		dh.setTax(tax);
		
		Double netValue = value - tax;
		dh.setNetValue(netValue);

		if (operator.getDeposit() != null) {
			deposit = operator.getDeposit() + netValue;
		} else {
			deposit = netValue;
		}
		
		operator.setDeposit(deposit);
		dh.setDeposit(deposit);
		dh.setChangeDate(dateString);
		
		operatorDao.update(operator);
		depositHistoryDao.add(dh);
		return dh;
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	public Map<String, Object> getHistoryList(Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> historyMap = depositHistoryDao.getHistoryList(params);
		List<DepositHistory> list = (List<DepositHistory>) historyMap.get("list");
		Integer count = (Integer) historyMap.get("count");
		int supplierId = (Integer) params.get("supplierId");
		Supplier supplier = supplierDao.get(supplierId);

		DecimalFormat df = new DecimalFormat("###,###,##0.00");
		if (supplier !=null) {
			df = new DecimalFormat(supplier.getCd().getPattern()); 
		}
		
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		
		for (int i = 0 ; i < list.size() ; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			DepositHistory history= list.get(i);
			PrepaymentLog prepaymentLog = history.getPrepaymentLog();
			map.put("id", history.getId());
			
			if (history.getOperator() != null) {
				map.put("vendor", history.getOperator().getLoginId());
			}
			if (history.getContract() != null) {
				map.put("contractNo", history.getContract().getContractNumber());
				map.put("contractId", history.getContractId());
			}
			if (history.getCustomer() != null) {
				map.put("customerId", history.getCustomer().getCustomerNo());
				map.put("customerName", history.getCustomer().getName());
				map.put("address", history.getCustomer().getAddress());
			}
			if (history.getMeter() != null) {
				map.put("meter", history.getMeter().getMdsId());
			}
			if (history.getChangeDate() != null && !history.getChangeDate().equals("")) {
				String localeDate = TimeLocaleUtil.getLocaleDate(history.getChangeDate(), lang, country);
				map.put("changeDate", localeDate);
			}
			
			Double chargeCredit = history.getChargeCredit();
			Double chargeDeposit = history.getNetValue();
			
			map.put("chargeCredit", chargeCredit != null ? df.format(chargeCredit) : null);
			map.put("chargeDeposit", chargeDeposit != null ? df.format(chargeDeposit): null);
			if ( prepaymentLog != null && prepaymentLog.getVendorCasher() != null ) {
				VendorCasher casher = prepaymentLog.getVendorCasher();
				map.put("casher", casher.getCasherId());
				map.put("isCanceled", prepaymentLog.getIsCanceled());
				map.put("cancelDate", prepaymentLog.getCancelDate());
			}
			
			map.put("prepaymentLogId", history.getPrepaymentLogId());
			map.put("deposit", df.format(StringUtil.nullToDoubleZero(history.getDeposit())));
			mapList.add(map);
		}
		result.put("count", count);
		result.put("list", mapList);
		return result;
	}

    /**
     * method name : getDepositHistoryList<b/>
     * method Desc :
     *
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getDepositHistoryList(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> historyMap = depositHistoryDao.getDepositHistoryList(params);
        List<Map<String, Object>> list = (List<Map<String, Object>>) historyMap.get("list");
        Integer count = (Integer) historyMap.get("count");
        int supplierId = (Integer) params.get("supplierId");
        Supplier supplier = supplierDao.get(supplierId);

        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        if (supplier != null) {
            df = new DecimalFormat(supplier.getCd().getPattern()); 
        }

        String country = supplier.getCountry().getCode_2letter();
        String lang = supplier.getLang().getCode_2letter();

        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        for (Map<String, Object> hmap : list) {
            map = new HashMap<String, Object>();
            map.put("id", hmap.get("depositHistoryId"));

            if (hmap.get("historyOpId") != null) {
                map.put("vendor", hmap.get("historyOpLoginId"));
            }
            if (hmap.get("historyContractId") != null) {
                map.put("contractNo", hmap.get("historyContractNumber"));
                map.put("contractId", hmap.get("historyContractId"));
            }
            if (hmap.get("historyCustomerId") != null) {
                map.put("customerId", hmap.get("historyCustomerNo"));
                map.put("customerName", hmap.get("historyCustomerName"));
                map.put("address", hmap.get("historyCustomerAddress"));
                map.put("address2", hmap.get("historyCustomerAddress2"));
            }
            if (hmap.get("historyMeterId") != null) {
                map.put("meter", hmap.get("historyMeterMdsId"));
            }
            if (hmap.get("changeDate") != null && !((String)hmap.get("changeDate")).equals("")) {
                map.put("changeDate", TimeLocaleUtil.getLocaleDate((String)hmap.get("changeDate"), lang, country));
            }

            Double chargeCredit = (Double)hmap.get("chargeCredit");
            Double chargeDeposit = (Double)hmap.get("netValue");

            map.put("chargeCredit", chargeCredit != null ? df.format(chargeCredit) : null);
            map.put("chargeDeposit", chargeDeposit != null ? df.format(chargeDeposit) : null);
            map.put("isCanceledByDeposit", hmap.get("isCanceledByDeposit"));

            if (hmap.get("prepaymentLogId") != null && hmap.get("vendorCasherId") != null) {
                map.put("casher", hmap.get("vcCasherId"));
                map.put("isCanceled", hmap.get("isCanceled"));
                map.put("cancelDate", hmap.get("cancelDate"));
            }

            map.put("prepaymentLogId", hmap.get("prepaymentLogId"));
            map.put("deposit", df.format(StringUtil.nullToDoubleZero((Double)hmap.get("deposit"))));
            mapList.add(map);
        }
        result.put("count", count);
        result.put("list", mapList);
        return result;
    }
    
    @Transactional(rollbackFor=Exception.class)
    public String depositCancelTransaction(Integer depositHistoryId, String loginId) {
		String rtn = "success";
		try {
			DepositHistory dh = depositHistoryDao.get(depositHistoryId);
			Boolean isCancelled = dh.getIsCanceled() == null ? false : dh.getIsCanceled();
			if(!isCancelled) {
				dh.setIsCanceled(true);
				dh.setCancelDate(TimeUtil.getCurrentTime());
				
				depositHistoryDao.update(dh);
				
				Operator operator = operatorDao.getOperatorByLoginId(loginId);
				Double deposit = 0d;
				
				deposit = operator.getDeposit() - dh.getNetValue();
				
				operator.setDeposit(deposit);
			} else {
				rtn = "cancelData";
			}
		} catch( Exception e ) {
			rtn = "failed";
			log.error(e,e);
		}
		return rtn;
    }
}