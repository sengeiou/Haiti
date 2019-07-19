/**
 * EnergyConsumptionMonitoringManagerImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.service.system.impl.energyConsumptionMonitoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.dao.mvm.BillingDayGMDao;
import com.aimir.dao.mvm.BillingDayWMDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.dao.mvm.BillingMonthGMDao;
import com.aimir.dao.mvm.BillingMonthWMDao;
import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.mvm.BillingDayGM;
import com.aimir.model.mvm.BillingDayWM;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.mvm.BillingMonthGM;
import com.aimir.model.mvm.BillingMonthWM;
import com.aimir.model.mvm.BillingPk;
import com.aimir.model.system.Contract;
import com.aimir.service.system.energyConsumptionMonitoring.EnergyConsumptionMonitoringManager;
import com.aimir.util.BillDateUtil;

/**
 * EnergyConsumptionMonitoringManagerImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 30.   v1.0       김상연         
 *
 */

@Service(value="energyConsumptionMonitoringManager")
@Transactional(readOnly=true)
public class EnergyConsumptionMonitoringManagerImpl implements EnergyConsumptionMonitoringManager {

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
    
	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionMonitoring.EnergyConsumptionMonitoringManager#getLast(com.aimir.model.system.Contract)
	 */
	public Map<String, Object> getLast(Contract contract) {
		
		String serviceType = contract.getServiceTypeCode().getCode();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			
			resultMap = billingDayEMDao.getLast(contract.getId());
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
			
			resultMap = billingDayGMDao.getLast(contract.getId());
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			
			resultMap = billingDayWMDao.getLast(contract.getId());
		}
		
		return resultMap;
	}
	
	public Map<String, Object> getFirst(Contract contract) {
		
		String serviceType = contract.getServiceTypeCode().getCode();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			
			resultMap = billingDayEMDao.getFirst(contract.getId());
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
			
			resultMap = billingDayGMDao.getFirst(contract.getId());
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			
			resultMap = billingDayWMDao.getFirst(contract.getId());
		}
		
		return resultMap;
	}

	public Map<String, Object> getSelDate(Contract contract, String selDate) {
		
		String serviceType = contract.getServiceTypeCode().getCode();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			
			resultMap = billingDayEMDao.getSelDate(contract.getId(), selDate);
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
			
			resultMap = billingDayGMDao.getSelDate(contract.getId(), selDate);
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			
			resultMap = billingDayWMDao.getSelDate(contract.getId(), selDate);
		}
		
		return resultMap;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionMonitoring.EnergyConsumptionMonitoringManager#getTotal(com.aimir.model.system.Contract, java.lang.String, java.lang.String)
	 */
	public Map<String, Object> getTotal(Contract contract, String lastBillDay, String periodDay) {

		String serviceType = contract.getServiceTypeCode().getCode();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		/*
		// billing_month에 정보가 매일 저장되지 않을 경우는 하기 로직 사용
		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			
			resultMap = billingDayEMDao.getTotal(contract.getId(), lastBillDay, periodDay);
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
			
			resultMap = billingDayGMDao.getTotal(contract.getId(), lastBillDay, periodDay);
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			
			resultMap = billingDayWMDao.getTotal(contract.getId(), lastBillDay, periodDay);
		}
		*/

		// billing_month에 매일 빌링정보를 저장할 경우는 하기 로직 사용
		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			
			BillingMonthEM billingMonthEM = new BillingMonthEM();
			billingMonthEM.setContract(contract);

			List<BillingMonthEM> result =  billingMonthEMDao.getBillingMonthEMs(billingMonthEM, lastBillDay, periodDay);

			if(result.size() != 0) {
				resultMap.put("totalUsage", result.get(0).getActiveEnergyRateTotal());
				resultMap.put("totalBill", result.get(0).getBill());
			}
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
			BillingMonthGM billingMonthGM = new BillingMonthGM();
			billingMonthGM.setContract(contract);

			List<BillingMonthGM> result =  billingMonthGMDao.getBillingMonthGMs(billingMonthGM, lastBillDay, periodDay);

			if(result.size() != 0) {
				resultMap.put("totalUsage", result.get(0).getUsage());
				resultMap.put("totalBill", result.get(0).getBill());
			}
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			BillingMonthWM billingMonthWM = new BillingMonthWM();
			billingMonthWM.setContract(contract);
			
			List<BillingMonthWM> result =  billingMonthWMDao.getBillingMonthWMs(billingMonthWM, lastBillDay, periodDay);

			if(result.size() != 0) {
				resultMap.put("totalUsage", result.get(0).getUsage());
				resultMap.put("totalBill", result.get(0).getBill());
			}
		}

		return resultMap;
	}
	
	public  Map<String, Object>  getBeforeDayUsageInfo(Contract contract, String date) {
		String serviceType = contract.getServiceTypeCode().getCode();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			
			BillingDayEM billingDayEM = new BillingDayEM();
			BillingPk billingPk = new BillingPk();
			
			billingPk.setYyyymmdd(date);
			billingDayEM.setId(billingPk);
			billingDayEM.setContract(contract);
			
			List<BillingDayEM> result =  billingDayEMDao.getBillingDayEMs(billingDayEM, null, null);

			if(result.size() != 0) {
				resultMap.put("beforeUsage", result.get(0).getActiveEnergyRateTotal());
				resultMap.put("beforeBill", result.get(0).getBill());
			}
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
			BillingDayGM billingDayGM = new BillingDayGM();
			BillingPk billingPk = new BillingPk();

			billingPk.setYyyymmdd(date);
			billingDayGM.setId(billingPk);
			billingDayGM.setContract(contract);
			
			List<BillingDayGM> result =  billingDayGMDao.getBillingDayGMs(billingDayGM, null, null);

			if(result.size() != 0) {
				resultMap.put("beforeUsage", result.get(0).getUsage());
				resultMap.put("beforeBill", result.get(0).getBill());
			}
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			BillingDayWM billingDayWM = new BillingDayWM();
			BillingPk billingPk = new BillingPk();

			billingPk.setYyyymmdd(date);
			billingDayWM.setId(billingPk);
			billingDayWM.setContract(contract);
			
			List<BillingDayWM> result =  billingDayWMDao.getBillingDayWMs(billingDayWM, null, null);

			if(result.size() != 0) {
				resultMap.put("beforeUsage", result.get(0).getUsage());
				resultMap.put("beforeBill", result.get(0).getBill());
			}
		}
		return resultMap;
	}
    
	public  Map<String, Object>  getBeforeMonthUsageInfo(Contract contract, String date) {
		String serviceType = contract.getServiceTypeCode().getCode();
//        String lastBillDate = BillDateUtil.getLastBillDay(contract, date, -1);
        String lastBillDate = BillDateUtil.getBillDate(contract, date, -2);        
        String lastMonthToDate = BillDateUtil.getMonthToDate(contract, date, 0);
        
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			
			BillingMonthEM billingMonthEM = new BillingMonthEM();
			billingMonthEM.setContract(contract);
			//BillingPk billingPk = new BillingPk();
			//billingPk.setYyyymmdd(date);
			//billingMonthEM.setId(billingPk);

			List<BillingMonthEM> result =  billingMonthEMDao.getBillingMonthEMs(billingMonthEM, lastBillDate, lastMonthToDate);

			if(result.size() != 0) {
				resultMap.put("beforeUsage", result.get(0).getActiveEnergyRateTotal());
				resultMap.put("beforeBill", result.get(0).getBill());
			}
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
			BillingMonthGM billingMonthGM = new BillingMonthGM();
			billingMonthGM.setContract(contract);
			
//			BillingPk billingPk = new BillingPk();
//			billingPk.setYyyymmdd(date);
//			billingMonthGM.setId(billingPk);

			List<BillingMonthGM> result =  billingMonthGMDao.getBillingMonthGMs(billingMonthGM, lastBillDate, lastMonthToDate);

			if(result.size() != 0) {
				resultMap.put("beforeUsage", result.get(0).getUsage());
				resultMap.put("beforeBill", result.get(0).getBill());
			}
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			BillingMonthWM billingMonthWM = new BillingMonthWM();
			billingMonthWM.setContract(contract);
			
//			BillingPk billingPk = new BillingPk();
//			billingPk.setYyyymmdd(date);
//			billingMonthWM.setId(billingPk);
			
			List<BillingMonthWM> result =  billingMonthWMDao.getBillingMonthWMs(billingMonthWM, lastBillDate, lastMonthToDate);

			if(result.size() != 0) {
				resultMap.put("beforeUsage", result.get(0).getUsage());
				resultMap.put("beforeBill", result.get(0).getBill());
			}
		}

		return resultMap;
	}
	
	public Double getMaxBill(Contract contract, String yyyymmdd) {
		String serviceType = contract.getServiceTypeCode().getCode();
		
		Double maxBill = 0.0;
		
		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			
			maxBill = billingDayEMDao.getMaxBill(contract, yyyymmdd);
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
			
			maxBill = billingDayGMDao.getMaxBill(contract, yyyymmdd);
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			
			maxBill = billingDayWMDao.getMaxBill(contract, yyyymmdd);
		}

		return maxBill;		
	}
}
