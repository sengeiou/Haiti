/**
 * EnergyConsumptionSearchManagerImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.service.system.impl.energyConsumptionSearch;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.HomeDeviceCategoryType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.dao.mvm.BillingDayGMDao;
import com.aimir.dao.mvm.BillingDayWMDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.dao.mvm.BillingMonthGMDao;
import com.aimir.dao.mvm.BillingMonthWMDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.energySavingGoal.EnergySavingTargetDao;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.Modem;
import com.aimir.model.mvm.Billing;
import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.mvm.BillingDayGM;
import com.aimir.model.mvm.BillingDayWM;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.mvm.BillingMonthGM;
import com.aimir.model.mvm.BillingMonthWM;
import com.aimir.model.mvm.BillingPk;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.mvm.DayGM;
import com.aimir.model.mvm.DayWM;
import com.aimir.model.mvm.MeteringDay;
import com.aimir.model.mvm.MonthEM;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.EnergySavingTarget;
import com.aimir.model.system.OperatorContract;
import com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager;
import com.aimir.util.BillDateUtil;
import com.aimir.util.DecimalUtil;

/**
 * EnergyConsumptionSearchManagerImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 26.   v1.0       김상연         전기 사용량 조회
 * 2011. 5. 17.   v1.1       김상연         가스,수도 사용량 조회
 * 2011. 6. 24.   v1.2       김상연         사용량 -> 비용, 목표 비용 설정
 *
 */

@Service(value="energyConsumptionSearchManager")
@Transactional(readOnly=false)
public class EnergyConsumptionSearchManagerImpl implements EnergyConsumptionSearchManager {
	
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
    DayEMDao dayEMDao;
    
    @Autowired
    DayGMDao dayGMDao;
    
    @Autowired
    DayWMDao dayWMDao;
    
    @Autowired
    MonthEMDao monthEMDao;
    
    @Autowired
    EndDeviceDao endDeviceDao;
    
    @Autowired
    ContractDao contractDao;
    
    @Autowired
    EnergySavingTargetDao energySavingTargetDao;
    
    @Autowired
    CodeDao codeDao;
    
	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getMaxDay(com.aimir.model.system.Contract)
	 */
	public String getMaxDay(Contract contract) {
		
		String serviceType = contract.getServiceTypeCode().getCode();
		
		String maxDay = null;
		
		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			
			maxDay = billingDayEMDao.getMaxDay(contract);
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
			
			maxDay = billingDayGMDao.getMaxDay(contract);
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			
			maxDay = billingDayWMDao.getMaxDay(contract);
		} 

		return maxDay;
	}
	
	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getBillingDayEm(com.aimir.model.system.Contract, java.lang.String)
	 */
	public BillingDayEM getBillingDayEm(Contract contract, String someDay) {
		
		BillingDayEM billingDayEM = new BillingDayEM();
		BillingPk billingPk = new BillingPk();
		
		billingPk.setYyyymmdd(someDay);
		billingDayEM.setId(billingPk);
		billingDayEM.setContract(contract);
		
		List<BillingDayEM> billingDayEMs = billingDayEMDao.getBillingDayEMs(billingDayEM, null, null);
		
		if (1 == billingDayEMs.size()) {
			billingDayEM = billingDayEMs.get(0);
		}
		
		return billingDayEM;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getMonthUsage(int, java.lang.String)
	 */
	public BillingMonthEM getMonthUsage(Contract contract, String someDay) {
		
		BillingMonthEM billingMonthEM = new BillingMonthEM();
		BillingPk billingPk = new BillingPk();
		
		billingPk.setYyyymmdd(someDay);
		billingMonthEM.setId(billingPk);
		billingMonthEM.setContract(contract);
		
		List<BillingMonthEM> billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, null, null);
		
		return billingMonthEMs.get(0);
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getAverageUsage(com.aimir.model.system.Contract, java.lang.String)
	 */
	public Double getAverageUsage(Contract contract, String basicDay) {
		
		String startDay = basicDay.substring(0, 6) + "01";
		
		return billingDayEMDao.getAverageUsage(contract, startDay, basicDay);
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getPeriodUsage(com.aimir.model.system.Contract, java.lang.String, java.lang.String)
	 */
	public Double getPeriodUsage(Contract contract, String lastDay,
			String basicDay) {

		String startDay = basicDay.substring(0, 6) + "01";

		return billingDayEMDao.getPeriodUsage(contract, startDay, basicDay);
	}

	/******************************/
	/*       시간별 사용량 취득             */
	/******************************/
	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getPeriodTimeUsage(int, java.lang.String)
	 */
	public List<MeteringDay> getPeriodTimeUsage(Contract contract, String basicDay, int channel, String mdevType) {
		
		MeteringDay meteringDay;
		List<MeteringDay> meteringDays = new ArrayList<MeteringDay>();
		
		String serviceType = contract.getServiceTypeCode().getCode();
		
		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			
			meteringDay = new DayEM();
			
			meteringDay.setContract(contract);
			meteringDay.setYyyymmdd(basicDay);
			meteringDay.setChannel(channel);
			meteringDay.setMDevType(mdevType);
			
			meteringDays.addAll(dayEMDao.getDayEMs((DayEM) meteringDay));
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
			
			meteringDay = new DayGM();
			
			meteringDay.setContract(contract);
			meteringDay.setYyyymmdd(basicDay);
			meteringDay.setChannel(channel);
			meteringDay.setMDevType(mdevType);
			
			meteringDays.addAll(dayGMDao.getDayGMs((DayGM) meteringDay));
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			
			meteringDay = new DayWM();
			
			meteringDay.setContract(contract);
			meteringDay.setYyyymmdd(basicDay);
			meteringDay.setChannel(channel);
			meteringDay.setMDevType(mdevType);
			
			meteringDays.addAll(dayWMDao.getDayWMs((DayWM) meteringDay));
		}
				
		return meteringDays;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getPeriodTimeUsage(int, java.lang.String)
	 */
//	public List<Object> getPeriodTimeAvgUsage(Contract contract, String basicDay, int channel, String mdevType) {
//
//		MeteringDay meteringDay;
//		List<Object> meteringDaysAvg = null;
//
//		String serviceType = contract.getServiceTypeCode().getCode();
//
//		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
//
//			meteringDay = new DayEM();
//			//meteringDay.setContract(contract);
//			meteringDay.setYyyymmdd(basicDay);
//			meteringDay.setChannel(channel);
//			meteringDay.setMDevType(mdevType);
//			meteringDay.setLocation(contract.getLocation());
//			
//			meteringDaysAvg = dayEMDao.getDayEMsAvg((DayEM) meteringDay);
//		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
//			
//			meteringDay = new DayGM();
//			//meteringDay.setContract(contract);
//			meteringDay.setYyyymmdd(basicDay);
//			meteringDay.setChannel(channel);
//			meteringDay.setMDevType(mdevType);
//			meteringDay.setLocation(contract.getLocation());
//			
//			meteringDaysAvg = dayGMDao.getDayGMsAvg((DayGM) meteringDay);
//		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
//			
//			meteringDay = new DayWM();
//			
//			//meteringDay.setContract(contract);
//			meteringDay.setYyyymmdd(basicDay);
//			meteringDay.setChannel(channel);
//			meteringDay.setMDevType(mdevType);
//			meteringDay.setLocation(contract.getLocation());
//			
//			meteringDaysAvg = dayWMDao.getDayWMsAvg((DayWM) meteringDay);
//		}
//
//		return meteringDaysAvg;
//	}

	/******************************/
	/*     시간별 평균 사용량 취득          */
	/******************************/
	public Double getPeriodTimeAvgUsage(Contract contract, String basicDay, int channel, String mdevType) {

		MeteringDay meteringDay;
		
		//List<Object> meteringDaysAvg = null;
		Double usageAve = 0d;
		String serviceType = contract.getServiceTypeCode().getCode();

		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			meteringDay = new DayEM();
			//meteringDay.setContract(contract);
			meteringDay.setYyyymmdd(basicDay);
			meteringDay.setChannel(channel);
			meteringDay.setMDevType(mdevType);
			meteringDay.setLocation(contract.getLocation());

			//meteringDaysAvg = dayEMDao.getDayEMsAvg((DayEM) meteringDay);
			usageAve = dayEMDao.getDayEMsUsageAvg((DayEM) meteringDay);

		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {

			meteringDay = new DayGM();
			//meteringDay.setContract(contract);
			meteringDay.setYyyymmdd(basicDay);
			meteringDay.setChannel(channel);
			meteringDay.setMDevType(mdevType);
			meteringDay.setLocation(contract.getLocation());

			//meteringDaysAvg = dayGMDao.getDayGMsAvg((DayGM) meteringDay);
			usageAve =  dayGMDao.getDayGMsUsageAvg((DayGM) meteringDay);

		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			
			meteringDay = new DayWM();

			//meteringDay.setContract(contract);
			meteringDay.setYyyymmdd(basicDay);
			meteringDay.setChannel(channel);
			meteringDay.setMDevType(mdevType);
			meteringDay.setLocation(contract.getLocation());

			//meteringDaysAvg = dayWMDao.getDayWMsAvg((DayWM) meteringDay);
			usageAve = dayWMDao.getDayWMsUsageAvg((DayWM) meteringDay);
		}

		return usageAve;
	}

	/******************************/
	/*      일 평균 사용요금 취득            */
	/******************************/
	public Double getPeriodDayAvgBillEm(Contract contract, String someMonth) {

		return billingDayEMDao.getAverageBill(contract, someMonth);
	}

	public Double getPeriodDayAvgBillGm(Contract contract, String someMonth) {

		return billingDayGMDao.getAverageBill(contract, someMonth);
	}

	public Double getPeriodDayAvgBillWm(Contract contract, String someMonth) {

		return billingDayWMDao.getAverageBill(contract, someMonth);
	}

	/******************************/
	/*      일 평균 사용량 취득               */
	/******************************/
	public List<Object> getPeriodDayAvgUsageEm(Contract contract, String someMonth) {

		BillingDayEM billingDayEM = new BillingDayEM();
		BillingPk billingPk = new BillingPk();

		billingPk.setYyyymmdd(someMonth);
		billingDayEM.setId(billingPk);
		//billingDayEM.setContract(contract);
		billingDayEM.setLocation(contract.getLocation());

		List<Object> billingDayEMs = billingDayEMDao.getBillingDayEMsAvg(billingDayEM);

		return billingDayEMs;
	}

	public List<Object> getPeriodDayAvgUsageGm(Contract contract, String someMonth) {
		
		BillingDayGM billingDayGM = new BillingDayGM();
		BillingPk billingPk = new BillingPk();

		billingPk.setYyyymmdd(someMonth);
		billingDayGM.setId(billingPk);
//		billingDayGM.setContract(contract);
		billingDayGM.setLocation(contract.getLocation());

		List<Object> billingDayGMs = billingDayGMDao.getBillingDayGMsAvg(billingDayGM);

		return billingDayGMs;
	}

	public List<Object> getPeriodDayAvgUsageWm(Contract contract, String someMonth) {
		
		BillingDayWM billingDayWM = new BillingDayWM();
		BillingPk billingPk = new BillingPk();

		billingPk.setYyyymmdd(someMonth);
		billingDayWM.setId(billingPk);
//		billingDayWM.setContract(contract);
		billingDayWM.setLocation(contract.getLocation());

		List<Object> billingDayWMs = billingDayWMDao.getBillingDayWMsAvg(billingDayWM);

		return billingDayWMs;
	}

	/******************************/
	/*         일  사용량 취득                */
	/******************************/
	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getPeriodDayUsageEm(com.aimir.model.system.Contract, java.lang.String)
	 */
	public List<BillingDayEM> getPeriodDayUsageEm(Contract contract, String someMonth) {

		BillingDayEM billingDayEM = new BillingDayEM();
		BillingPk billingPk = new BillingPk();

		billingPk.setYyyymmdd(someMonth);
		billingDayEM.setId(billingPk);
		billingDayEM.setContract(contract);

		List<BillingDayEM> billingDayEMs = billingDayEMDao.getBillingDayEMs(billingDayEM, null, null);

		return billingDayEMs;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getPeriodDayUsageGm(com.aimir.model.system.Contract, java.lang.String)
	 */
	public List<BillingDayGM> getPeriodDayUsageGm(Contract contract, String someMonth) {
		
		BillingDayGM billingDayGM = new BillingDayGM();
		BillingPk billingPk = new BillingPk();

		billingPk.setYyyymmdd(someMonth);
		billingDayGM.setId(billingPk);
		billingDayGM.setContract(contract);
		
		List<BillingDayGM> billingDayGMs = billingDayGMDao.getBillingDayGMs(billingDayGM, null, null);

		return billingDayGMs;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getPeriodDayUsageWm(com.aimir.model.system.Contract, java.lang.String)
	 */
	public List<BillingDayWM> getPeriodDayUsageWm(Contract contract, String someMonth) {
		
		BillingDayWM billingDayWM = new BillingDayWM();
		BillingPk billingPk = new BillingPk();

		billingPk.setYyyymmdd(someMonth);
		billingDayWM.setId(billingPk);
		billingDayWM.setContract(contract);
		
		List<BillingDayWM> billingDayWMs = billingDayWMDao.getBillingDayWMs(billingDayWM, null, null);

		return billingDayWMs;
	}
	
	
	/******************************/
	/*         월  사용량 취득               */
	/******************************/
	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getPeriodMonthUsageEm(com.aimir.model.system.Contract, java.lang.String)
	 */
	public List<BillingMonthEM> getPeriodMonthUsageEm(Contract contract, String someYear) {

		BillingMonthEM billingMonthEM = new BillingMonthEM();
		BillingPk billingPk = new BillingPk();

		billingPk.setYyyymmdd(someYear);
		billingMonthEM.setId(billingPk);
		billingMonthEM.setContract(contract);
		
		List<BillingMonthEM> billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, null, null);

		return billingMonthEMs;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getPeriodMonthUsageGm(com.aimir.model.system.Contract, java.lang.String)
	 */
	public List<BillingMonthGM> getPeriodMonthUsageGm(Contract contract, String someYear) {
		
		BillingMonthGM billingMonthGM = new BillingMonthGM();
		BillingPk billingPk = new BillingPk();

		billingPk.setYyyymmdd(someYear);
		billingMonthGM.setId(billingPk);
		billingMonthGM.setContract(contract);
		
		List<BillingMonthGM> billingMonthGMs = billingMonthGMDao.getBillingMonthGMs(billingMonthGM, null, null);

		return billingMonthGMs;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getPeriodMonthUsageWm(com.aimir.model.system.Contract, java.lang.String)
	 */
	public List<BillingMonthWM> getPeriodMonthUsageWm(Contract contract, String someYear) {
		
		BillingMonthWM billingMonthWM = new BillingMonthWM();
		BillingPk billingPk = new BillingPk();

		billingPk.setYyyymmdd(someYear);
		billingMonthWM.setId(billingPk);
		billingMonthWM.setContract(contract);
		
		List<BillingMonthWM> billingMonthWMs = billingMonthWMDao.getBillingMonthWMs(billingMonthWM, null, null);

		return billingMonthWMs;
	}


	/******************************/
	/*      월 평균 사용량 취득               */
	/******************************/
	public List<Object> getPeriodMonthAvgUsageEm(Contract contract, String someYear) {

		BillingMonthEM billingMonthEM = new BillingMonthEM();
		BillingPk billingPk = new BillingPk();

		billingPk.setYyyymmdd(someYear);
		billingMonthEM.setId(billingPk);
		//billingMonthEM.setContract(contract);
		billingMonthEM.setLocation(contract.getLocation());

        // 전월 과금일 취득
		List<Object> billingMonthEMs = billingMonthEMDao.getBillingMonthEMsAvg(billingMonthEM, null, null);

		return billingMonthEMs;
	}

	public List<Object> getPeriodMonthAvgUsageGm(Contract contract, String someYear) {
		
		BillingMonthGM billingMonthGM = new BillingMonthGM();
		BillingPk billingPk = new BillingPk();

		billingPk.setYyyymmdd(someYear);
		billingMonthGM.setId(billingPk);
		//billingMonthGM.setContract(contract);
		billingMonthGM.setLocation(contract.getLocation());

		List<Object> billingMonthGMs = billingMonthGMDao.getBillingMonthGMsAvg(billingMonthGM, null, null);

		return billingMonthGMs;
	}

	public List<Object> getPeriodMonthAvgUsageWm(Contract contract, String someYear) {
		
		BillingMonthWM billingMonthWM = new BillingMonthWM();
		BillingPk billingPk = new BillingPk();

		billingPk.setYyyymmdd(someYear);
		billingMonthWM.setId(billingPk);
//		billingMonthWM.setContract(contract);
		billingMonthWM.setLocation(contract.getLocation());

		List<Object> billingMonthWMs = billingMonthWMDao.getBillingMonthWMsAvg(billingMonthWM, null, null);

		return billingMonthWMs;
	}

	/******************************/
	/*      월 평균 사용요금 취득            */
	/******************************/
	public Double getPeriodMonthAvgBillEm(Contract contract, String someMonth) {

		return billingMonthEMDao.getAverageBill(contract, someMonth);
	}

	public Double getPeriodMonthAvgBillGm(Contract contract, String someMonth) {

		return billingMonthGMDao.getAverageBill(contract, someMonth);
	}

	public Double getPeriodMonthAvgBillWm(Contract contract, String someMonth) {

		return billingMonthWMDao.getAverageBill(contract, someMonth);
	}
	
	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getBillingMonthEm(com.aimir.model.system.Contract, java.lang.String)
	 */
	public BillingMonthEM getBillingMonthEm(Contract contract, String someMonth) {

		BillingMonthEM billingMonthEM = new BillingMonthEM();
		BillingPk billingPk = new BillingPk();

		billingPk.setYyyymmdd(someMonth);
		billingMonthEM.setId(billingPk);
		billingMonthEM.setContract(contract);

		List<BillingMonthEM> billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, null, null);

		if (1 == billingMonthEMs.size()) {
			billingMonthEM = billingMonthEMs.get(0);
		}

		return billingMonthEM;
	}

	
	
	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getBillingYearEm(com.aimir.model.system.Contract, java.lang.String)
	 */
	public Map<String, Object> getBillingYear(Contract contract, String someYear) {

		String serviceType = contract.getServiceTypeCode().getCode();
		
		Double activeEnergyRateTotal = 0.0;
		Double bill = 0.0;
		Double co2Emissions = 0.0;
		Double newMiles = 0.0;
		
    	if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
		
			BillingMonthEM billingMonthEM = new BillingMonthEM();
			BillingPk billingPk = new BillingPk();
	
			billingPk.setYyyymmdd(someYear);
			billingMonthEM.setId(billingPk);
			billingMonthEM.setContract(contract);
			
			List<BillingMonthEM> billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, null, null);
			
			for (BillingMonthEM result: billingMonthEMs) {
				
				activeEnergyRateTotal += result.getActiveEnergyRateTotal() == null ? 0 : result.getActiveEnergyRateTotal();
				bill += result.getBill() == null ? 0 : result.getBill();
				co2Emissions += result.getCo2Emissions() == null ? 0 : result.getCo2Emissions();
				newMiles += result.getNewMiles() == null ? 0 : result.getNewMiles();
			}
    	} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
    		
			BillingMonthGM billingMonthGM = new BillingMonthGM();
			BillingPk billingPk = new BillingPk();
	
			billingPk.setYyyymmdd(someYear);
			billingMonthGM.setId(billingPk);
			billingMonthGM.setContract(contract);
			
			List<BillingMonthGM> billingMonthGMs = billingMonthGMDao.getBillingMonthGMs(billingMonthGM, null, null);
			
			for (BillingMonthGM result: billingMonthGMs) {
				
				activeEnergyRateTotal += result.getUsage() == null ? 0 : result.getUsage();
				bill += result.getBill() == null ? 0 : result.getBill();
				co2Emissions += result.getCo2Emissions() == null ? 0 : result.getCo2Emissions();
				newMiles += result.getNewMiles() == null ? 0 : result.getNewMiles();
			}
    	} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
    		
			BillingMonthWM billingMonthWM = new BillingMonthWM();
			BillingPk billingPk = new BillingPk();
	
			billingPk.setYyyymmdd(someYear);
			billingMonthWM.setId(billingPk);
			billingMonthWM.setContract(contract);
			
			List<BillingMonthWM> billingMonthWMs = billingMonthWMDao.getBillingMonthWMs(billingMonthWM, null, null);
			
			for (BillingMonthWM result: billingMonthWMs) {
				
				activeEnergyRateTotal += result.getUsage() == null ? 0 : result.getUsage();
				bill += result.getBill() == null ? 0 : result.getBill();
				co2Emissions += result.getCo2Emissions() == null ? 0 : result.getCo2Emissions();
				newMiles += result.getNewMiles() == null ? 0 : result.getNewMiles();
			}
    	}
    	
		Map<String, Object> billingYear = new HashMap<String, Object>();
		
		billingYear.put("activeEnergyRateTotal", activeEnergyRateTotal);
		billingYear.put("bill", bill);
		billingYear.put("co2Emissions", co2Emissions);
		billingYear.put("newMiles", newMiles);
		
		return billingYear;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getDayEMs(com.aimir.model.mvm.DayEM)
	 */
	public List<DayEM> getDayEMs(DayEM dayEM) {
		
		Code code = codeDao.getCodeIdByCodeObject(HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode());
		
		List<DayEM> dayEMs = new ArrayList<DayEM>();
		
		if (dayEM.getMDevType() != null) {
			
			switch (dayEM.getMDevType().getCode()) {
			
				case 0 :
					
					dayEMs = dayEMDao.getDayEMs(dayEM);
					
					break;
					
				case 1 :
					
					dayEMs = dayEMDao.getDayEMs(dayEM);
					
					for (int i = 0; i < dayEMs.size(); i++) {
						
						EndDevice endDevice = new EndDevice();
						
						endDevice.setModem(dayEMs.get(i).getModem());
						endDevice.setCategoryCode(code);
						
						List<EndDevice> endDevices = endDeviceDao.getEndDevices(endDevice);
						
						if (1 == endDevices.size()) {
							
							endDevice = endDevices.get(0);
							dayEMs.get(i).setEnddevice(endDevice);
						} else {

							dayEMs.remove(i--);
						}
					}
					
					break;
					
				case 2 :
					
					dayEMs = dayEMDao.getDayEMs(dayEM);
					
					break;
					
				case 3 :
					
					dayEMs = dayEMDao.getDayEMs(dayEM);
					
					break;
					
				default :
					break;
			}
		}

		return dayEMs;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getMonthEMs(com.aimir.model.mvm.MonthEM)
	 */
	public List<MonthEM> getMonthEMs(MonthEM monthEM) {

		Code code = codeDao.getCodeIdByCodeObject(HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode());
		
		List<MonthEM> monthEMs = new ArrayList<MonthEM>();
		
		if (monthEM.getMDevType() != null) {
			
			switch (monthEM.getMDevType().getCode()) {
			
				case 0 :
					
					monthEMs = monthEMDao.getMonthEMs(monthEM);
					
					break;
					
				case 1 :
					
					monthEMs = monthEMDao.getMonthEMs(monthEM);
					
					for (int i = 0; i < monthEMs.size(); i++) {
						
						EndDevice endDevice = new EndDevice();
						
						endDevice.setModem(monthEMs.get(i).getModem());
						endDevice.setCategoryCode(code);
						
						List<EndDevice> endDevices = endDeviceDao.getEndDevices(endDevice);
						
						if (1 == endDevices.size()) {
							
							endDevice = endDevices.get(0);
							monthEMs.get(i).setEnddevice(endDevice);
						} else {

							monthEMs.remove(i--);
						}
					}
					
					break;
					
				case 2 :
					
					monthEMs = monthEMDao.getMonthEMs(monthEM);
					
					break;
					
				case 3 :
					
					monthEMs = monthEMDao.getMonthEMs(monthEM);
					
					break;
					
				default :
					
					break;
			}
		} else {
			
			monthEMs = monthEMDao.getMonthEMs(monthEM);
		}

		return monthEMs;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getSumMonthEMs(com.aimir.model.mvm.MonthEM)
	 */
	public List<Map<String, Object>> getSumMonthEMs(MonthEM monthEM) {

		Code code = codeDao.getCodeIdByCodeObject(HomeDeviceCategoryType.GENERAL_APPLIANCE.getCode());

		List<Map<String, Object>> sumMonthEMs = new ArrayList<Map<String, Object>>();
		
		if (monthEM.getMDevType() != null) {
			
			switch (monthEM.getMDevType().getCode()) {
			
				case 0 :
					
					sumMonthEMs = monthEMDao.getSumMonthEMs(monthEM);
					
					break;
					
				case 1 :
					
					sumMonthEMs = monthEMDao.getSumMonthEMs(monthEM);
					
					for (int i = 0; i < sumMonthEMs.size(); i++) {
						
						EndDevice endDevice = new EndDevice();
						
						Modem modem = (Modem) sumMonthEMs.get(i).get("modem");
						
						endDevice.setModem(modem);
						endDevice.setCategoryCode(code);
						
						List<EndDevice> endDevices = endDeviceDao.getEndDevices(endDevice);
						
						if (1 == endDevices.size()) {
							
							endDevice = endDevices.get(0);
							sumMonthEMs.get(i).put("enddevice", endDevice);
						} else {
							
							sumMonthEMs.remove(i--);
						}
					}
					break;
					
				case 2 :
					
					sumMonthEMs = monthEMDao.getSumMonthEMs(monthEM);
					
					break;
					
				case 3 :
					
					sumMonthEMs = monthEMDao.getSumMonthEMs(monthEM);
					
					break;
					
				default :
					break;
			}
		}

		return sumMonthEMs;
	}


	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getCompareBill(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	public List<Object> getCompareBill(int contractId, String someDay, String lastDay, String lastYearDay) {
		
		List<Object> result = new ArrayList<Object>();

		Contract contract = contractDao.get(contractId);
		
		DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());

		String serviceType = contract.getServiceTypeCode().getCode();
		
		BillingDayEM billingDayEM;
		List<BillingDayEM> billingDayEMs;
		
		Billing billing;
		List<Billing> billings;
		
		BillingMonthEM billingMonthEM;
		List<BillingMonthEM> billingMonthEMs;

		Double lastYear = 0.0;
		Double basicYear = 0.0;
		
		switch (someDay.length()) {
		
			case 8 :
				
				if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {

					billingDayEMs = new ArrayList<BillingDayEM>();

					billingDayEM = new BillingDayEM();
					billingDayEM.setContract(contract);
					billingDayEM.setLocation(contract.getLocation());
					billingDayEM.setYyyymmdd(lastYearDay);
					// 집단 평균 요금 취득
					//result.add(billingDayEMDao.getAverageUsage(billingDayEM));
					// 전년도 동월 요금 취득
					billingDayEMs = billingDayEMDao.getBillingDayEMs(billingDayEM, null, null);
					result.add(1 != billingDayEMs.size() 
							? 0 : (billingDayEMs.get(0).getBill() == null 
									? 0 : dfCd.format(billingDayEMs.get(0).getBill())));

					// 전일 요금 취득
					billingDayEMs.clear();
					billingDayEM.setYyyymmdd(lastDay);
					billingDayEMs = billingDayEMDao.getBillingDayEMs(billingDayEM, null, null);
					result.add(1 != billingDayEMs.size() 
							? 0 : (billingDayEMs.get(0).getBill() == null 
									? 0 : billingDayEMs.get(0).getBill().intValue()));
					
					// 선택한일의 요금 취득
					billingDayEM.setYyyymmdd(someDay);
					billingDayEMs.clear();
					billingDayEMs = billingDayEMDao.getBillingDayEMs(billingDayEM, null, null);
					result.add(1 != billingDayEMs.size() 
							? 0 : (billingDayEMs.get(0).getBill() == null 
									? 0 : billingDayEMs.get(0).getBill().intValue()));
				} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {

					billings = new ArrayList<Billing>();

					billing = new BillingDayGM();
					billing.setContract(contract);
					billing.setYyyymmdd(lastYearDay);
					billing.setLocation(contract.getLocation());
					// 집단 평균 요금 취득
					//result.add(billingDayGMDao.getAverageUsage((BillingDayGM) billing));
					// 전년도 동일 요금 취득
					billings.addAll(billingDayGMDao.getBillingDayGMs((BillingDayGM) billing, null, null));
					result.add(1 != billings.size() 
							? 0 : (billings.get(0).getBill() == null 
									? 0 : billings.get(0).getBill().intValue()));

					// 전일 요금 취득
					billings.clear();
					billing.setYyyymmdd(lastDay);
					billings.addAll(billingDayGMDao.getBillingDayGMs((BillingDayGM) billing, null, null));
					result.add(1 != billings.size() 
							? 0 : (billings.get(0).getBill() == null 
									? 0 : billings.get(0).getBill().intValue()));

					// 선택한 일의 요금 취득
					billings.clear();
					billing.setYyyymmdd(someDay);
					billings.addAll(billingDayGMDao.getBillingDayGMs((BillingDayGM) billing, null, null));
					result.add(1 != billings.size() 
							? 0 : (billings.get(0).getBill() == null 
									? 0 : billings.get(0).getBill().intValue()));

				} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {

					billings = new ArrayList<Billing>();

					billing = new BillingDayWM();
					billing.setContract(contract);
					billing.setYyyymmdd(lastYearDay);
					billing.setLocation(contract.getLocation());
					// 집단 평균 요금 취득
					//result.add(billingDayWMDao.getAverageUsage((BillingDayWM) billing));

					// 전년도 동일 요금 취득
					billings.addAll(billingDayWMDao.getBillingDayWMs((BillingDayWM) billing, null, null));
					result.add(1 != billings.size() 
							? 0 : (billings.get(0).getBill() == null 
									? 0 : billings.get(0).getBill().intValue()));

					// 전일 요금 취득
					billings.clear();
					billing.setYyyymmdd(lastDay);
					billings.addAll(billingDayWMDao.getBillingDayWMs((BillingDayWM) billing, null, null));
					result.add(1 != billings.size() 
							? 0 : (billings.get(0).getBill() == null 
									? 0 : billings.get(0).getBill().intValue()));
					
					// 선택한 일의 요금 취득
					billings.clear();
					billing.setYyyymmdd(someDay);
					billings.addAll(billingDayWMDao.getBillingDayWMs((BillingDayWM) billing, null, null));

					result.add(1 != billings.size() 
							? 0 : (billings.get(0).getBill() == null 
									? 0 : billings.get(0).getBill().intValue()));					
				}

				break;
				
			case 6 :

				if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {

					billingMonthEMs = new ArrayList<BillingMonthEM>();

					billingMonthEM = new BillingMonthEM();

					billingMonthEM.setContract(contract);
					billingMonthEM.setYyyymmdd(lastYearDay);
					billingMonthEM.setLocation(contract.getLocation());
					
					//result.add(billingMonthEMDao.getAverageUsage(billingMonthEM));
					// 전년도 동월 요금 취득
					billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, null, null);
					result.add(1 != billingMonthEMs.size() 
							? 0 : (billingMonthEMs.get(0).getBill() == null 
									? 0 : billingMonthEMs.get(0).getBill().intValue()));
									
					// 전월 요금 취득
					billingMonthEMs.clear();
					billingMonthEM.setYyyymmdd(lastDay);
					billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, null, null);
					result.add(1 != billingMonthEMs.size() 
							? 0 : (billingMonthEMs.get(0).getBill() == null 
									? 0 : billingMonthEMs.get(0).getBill().intValue()));
					
					// 선택한 월의 요금 취득
					billingMonthEMs.clear();
					billingMonthEM.setYyyymmdd(someDay);
					billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, null, null);
					result.add(1 != billingMonthEMs.size() 
							? 0 : (billingMonthEMs.get(0).getBill() == null 
									? 0 : billingMonthEMs.get(0).getBill()));					
				} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {

					billings = new ArrayList<Billing>();

					billing = new BillingMonthGM();
					billing.setContract(contract);
					billing.setYyyymmdd(lastYearDay);
					billing.setLocation(contract.getLocation());
					
					//result.add(billingMonthGMDao.getAverageUsage((BillingMonthGM) billing));

					// 전년도 동월 요금 취득
					billings.addAll(billingMonthGMDao.getBillingMonthGMs((BillingMonthGM) billing, null, null));
					result.add(1 != billings.size() 
							? 0 : (billings.get(0).getBill() == null 
									? 0 : billings.get(0).getBill().intValue()));

					// 전월 요금 취득
					billings.clear();
					billing.setYyyymmdd(lastDay);
					billings.addAll(billingMonthGMDao.getBillingMonthGMs((BillingMonthGM) billing, null, null));
					result.add(1 != billings.size() 
							? 0 : (billings.get(0).getBill() == null 
									? 0 : billings.get(0).getBill().intValue()));

					// 선택한 월의 요금 취득
					billings.clear();
					billing.setYyyymmdd(someDay);
					billings.addAll(billingMonthGMDao.getBillingMonthGMs((BillingMonthGM) billing, null, null));
					result.add(1 != billings.size() 
							? 0 : (billings.get(0).getBill() == null 
									? 0 : billings.get(0).getBill().intValue()));					
				} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {

					billings = new ArrayList<Billing>();

					billing = new BillingMonthWM();
					billing.setContract(contract);
					billing.setYyyymmdd(lastYearDay);
					billing.setLocation(contract.getLocation());
					
					//result.add(billingMonthWMDao.getAverageUsage((BillingMonthWM) billing));
					// 전년도 동월 요금 취득
					billings.addAll(billingMonthWMDao.getBillingMonthWMs((BillingMonthWM) billing, null, null));
					result.add(1 != billings.size() 
							? 0 : (billings.get(0).getBill() == null 
									? 0 : billings.get(0).getBill().intValue()));	
					
					// 전월 요금 취득
					billings.clear();
					billing.setYyyymmdd(lastDay);
					billings.addAll(billingMonthWMDao.getBillingMonthWMs((BillingMonthWM) billing, null, null));
					result.add(1 != billings.size() 
							? 0 : (billings.get(0).getBill() == null 
									? 0 : billings.get(0).getBill().intValue()));

					// 선택한 월의 요금 취득
					billings.clear();
					billing.setYyyymmdd(someDay);
					billings.addAll(billingMonthWMDao.getBillingMonthWMs((BillingMonthWM) billing, null, null));
					result.add(1 != billings.size() 
							? 0 : (billings.get(0).getBill() == null 
									? 0 : billings.get(0).getBill().intValue()));					
				}
				
				break;
				
			case 4 :
				
				if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {

					billingMonthEMs = new ArrayList<BillingMonthEM>();

					billingMonthEM = new BillingMonthEM();
					billingMonthEM.setContract(contract);
					billingMonthEM.setYyyymmdd(lastYearDay);
					billingMonthEM.setLocation(contract.getLocation());
					//result.add(billingMonthEMDao.getAverageUsage(billingMonthEM));
					// 선택한 해의 2년전 요금 취득
					billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, null, null);
					for (BillingMonthEM monthEM : billingMonthEMs) {

						//lastYear += (monthEM.getActiveEnergyRateTotal() == null ? 0 : monthEM.getActiveEnergyRateTotal());
						lastYear += (monthEM.getBill() == null ? 0 : monthEM.getBill().intValue());
					}					
					result.add(lastYear);
					
					// 전년도 요금 취득
					billingMonthEMs.clear();
					billingMonthEM.setYyyymmdd(lastDay);
					billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, null, null);
					for (BillingMonthEM monthEM : billingMonthEMs) {

						//lastYear += (monthEM.getActiveEnergyRateTotal() == null ? 0 : monthEM.getActiveEnergyRateTotal());
						lastYear += (monthEM.getBill() == null ? 0 : monthEM.getBill().intValue());
					}					
					result.add(lastYear);
					
					// 선택한 해의 요금 취득
					billingMonthEMs.clear();
					billingMonthEM.setYyyymmdd(someDay);
					billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, null, null);
					for (BillingMonthEM monthEM : billingMonthEMs) {

						//basicYear += (monthEM.getActiveEnergyRateTotal() == null ? 0 : monthEM.getActiveEnergyRateTotal());
						basicYear += (monthEM.getBill() == null ? 0 : monthEM.getBill());
					}
					result.add(basicYear);
				} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {

					billing = new BillingMonthGM();
					
					billing.setContract(contract);
					billing.setYyyymmdd(lastYearDay);
					billing.setLocation(contract.getLocation());
					
					//result.add(billingMonthGMDao.getAverageUsage((BillingMonthGM) billing));
					billings = new ArrayList<Billing>();
					
					billings.addAll(billingMonthGMDao.getBillingMonthGMs((BillingMonthGM) billing, null, null));
					
					for (Billing monthGM : billings) {
						//lastYear += (monthGM.getUsage() == null ? 0 : monthGM.getUsage());
						lastYear += (monthGM.getBill() == null ? 0 : monthGM.getBill().intValue());
					}
					
					result.add(lastYear);

					billings.clear();
					billing.setYyyymmdd(lastDay);
					
					//billings = new ArrayList<Billing>();
					
					billings.addAll(billingMonthGMDao.getBillingMonthGMs((BillingMonthGM) billing, null, null));
					
					for (Billing monthGM : billings) {
						//lastYear += (monthGM.getUsage() == null ? 0 : monthGM.getUsage());
						lastYear += (monthGM.getBill() == null ? 0 : monthGM.getBill().intValue());
					}
					
					result.add(lastYear);
					
					billings.clear();
					billing.setYyyymmdd(someDay);
					
					//billings = new ArrayList<Billing>();
					
					billings.addAll(billingMonthGMDao.getBillingMonthGMs((BillingMonthGM) billing, null, null));
					
					for (Billing monthGM : billings) {
						
						//basicYear += (monthGM.getUsage() == null ? 0 : monthGM.getUsage());
						basicYear += (monthGM.getBill() == null ? 0 : monthGM.getBill().intValue());
					}
					
					result.add(basicYear);
				} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {

					billing = new BillingMonthWM();

					billing.setContract(contract);
					billing.setYyyymmdd(lastYearDay);
					billing.setLocation(contract.getLocation());
					
					//result.add(billingMonthWMDao.getAverageUsage((BillingMonthWM) billing));
					billings = new ArrayList<Billing>();
					
					billings.addAll(billingMonthWMDao.getBillingMonthWMs((BillingMonthWM) billing, null, null));
					for (Billing monthWM : billings) {

						//lastYear += (monthWM.getUsage() == null ? 0 : monthWM.getUsage());
						lastYear += (monthWM.getBill() == null ? 0 : monthWM.getBill().intValue());
					}
					result.add(lastYear);

					billings.clear();
					billing.setYyyymmdd(lastDay);
					//billings = new ArrayList<Billing>();
					billings.addAll(billingMonthWMDao.getBillingMonthWMs((BillingMonthWM) billing, null, null));
					
					for (Billing monthWM : billings) {

						//lastYear += (monthWM.getUsage() == null ? 0 : monthWM.getUsage());
						lastYear += (monthWM.getBill() == null ? 0 : monthWM.getBill().intValue());
					}
					result.add(lastYear);
					
					billings.clear();
					billing.setYyyymmdd(someDay);
					//billings = new ArrayList<Billing>();
					billings.addAll(billingMonthWMDao.getBillingMonthWMs((BillingMonthWM) billing, null, null));

					for (Billing monthWM : billings) {

						//basicYear += (monthWM.getUsage() == null ? 0 : monthWM.getUsage());
						basicYear += (monthWM.getBill() == null ? 0 : monthWM.getBill().intValue());
					}

					result.add(basicYear);
				}

				break;
				
			default:
				break;
 		}

		return result;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getDeviceSpecificGrid(java.lang.String, int)
	 */
	public List<Map<String, Object>> getDeviceSpecificGrid(String basicDay, int contractId) {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		switch (basicDay.length()) {
		
			case 8 :
				
				result = dayEMDao.getDeviceSpecificGrid(basicDay, contractId);
				
				break;
				
			case 6 :

				result = monthEMDao.getDeviceSpecificGrid(basicDay, contractId);

				break;
				
			case 4 :

				result = monthEMDao.getDeviceSpecificGrid(basicDay, contractId);

				break;
				
			default:
				break;
 		}
		
		for (int i = 0; i < result.size(); i++) {
			result.get(i).put("no", i + 1);
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getBillingDayGm(com.aimir.model.system.Contract, java.lang.String)
	 */
	public BillingDayGM getBillingDayGm(Contract contract, String someDay) {
		
		BillingDayGM billingDayGM = new BillingDayGM();
		BillingPk billingPk = new BillingPk();
		
		billingPk.setYyyymmdd(someDay);
		billingDayGM.setId(billingPk);
		billingDayGM.setContract(contract);
		
		List<BillingDayGM> billingDayGMs = billingDayGMDao.getBillingDayGMs(billingDayGM, null, null);
		
		if (1 == billingDayGMs.size()) {
			billingDayGM = billingDayGMs.get(0);
		}
		
		return billingDayGM;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getBillingDayWm(com.aimir.model.system.Contract, java.lang.String)
	 */
	public BillingDayWM getBillingDayWm(Contract contract, String someDay) {
		
		BillingDayWM billingDayWM = new BillingDayWM();
		BillingPk billingPk = new BillingPk();
		
		billingPk.setYyyymmdd(someDay);
		billingDayWM.setId(billingPk);
		billingDayWM.setContract(contract);
		
		List<BillingDayWM> billingDayWMs = billingDayWMDao.getBillingDayWMs(billingDayWM, null, null);
		
		if (1 == billingDayWMs.size()) {
			billingDayWM = billingDayWMs.get(0);
		}
		
		return billingDayWM;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getBillingMonthGm(com.aimir.model.system.Contract, java.lang.String)
	 */
	public BillingMonthGM getBillingMonthGm(Contract contract, String someMonth) {
		
		BillingMonthGM billingMonthGM = new BillingMonthGM();
		BillingPk billingPk = new BillingPk();

		billingPk.setYyyymmdd(someMonth);
		billingMonthGM.setId(billingPk);
		billingMonthGM.setContract(contract);
		
		List<BillingMonthGM> billingMonthGMs = billingMonthGMDao.getBillingMonthGMs(billingMonthGM, null, null);
		
		if (1 == billingMonthGMs.size()) {
			billingMonthGM = billingMonthGMs.get(0);
		}
		
		return billingMonthGM;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getBillingMonthWm(com.aimir.model.system.Contract, java.lang.String)
	 */
	public BillingMonthWM getBillingMonthWm(Contract contract, String someMonth) {
		
		BillingMonthWM billingMonthWM = new BillingMonthWM();
		BillingPk billingPk = new BillingPk();

		billingPk.setYyyymmdd(someMonth);
		billingMonthWM.setId(billingPk);
		billingMonthWM.setContract(contract);
		
		List<BillingMonthWM> billingMonthWMs = billingMonthWMDao.getBillingMonthWMs(billingMonthWM, null, null);
		
		if (1 == billingMonthWMs.size()) {
			billingMonthWM = billingMonthWMs.get(0);
		}
		
		return billingMonthWM;
	}




	

	








	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getMonthBill(com.aimir.model.system.Contract, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getMonthBill(Contract contract, String basicDay) {
		
		String serviceType = contract.getServiceTypeCode().getCode();
		
		Double averageBill = 0.0;
		Double lastBill = 0.0;
		Double standardBill = 0.0;
		
//        String billDate = BillDateUtil.getLastBillDay(contract, basicDay, 0);
        String billDate = BillDateUtil.getBillDate(contract, basicDay, -1);
        String monthToDate = BillDateUtil.getMonthToDate(contract, basicDay, 1);

//        String lastBillDate = BillDateUtil.getLastBillDay(contract, basicDay, -1);
        String lastBillDate = BillDateUtil.getBillDate(contract, basicDay, -2);
        String lastMonthToDate = BillDateUtil.getMonthToDate(contract, basicDay, 0);

    	if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {

    		//averageBill = billingMonthEMDao.getAverageBill(contract);

    		BillingMonthEM billingMonthEM = new BillingMonthEM();
    		
    		billingMonthEM.setContract(contract);
			billingMonthEM.setLocation(contract.getLocation());

			// 전월 요금 취득
			List<BillingMonthEM> billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, lastBillDate, lastMonthToDate);
			for (BillingMonthEM result: billingMonthEMs) {
				
				lastBill += result.getBill() == null ? 0 : result.getBill();
			}
			
//			BillingPk billingPk = new BillingPk();
//			billingPk.setYyyymmdd(startDay);
//			billingMonthEM.setId(billingPk);

			// 같은 지역 평균값
			List<Object> list = billingMonthEMDao.getBillingMonthEMsAvg(billingMonthEM, billDate, monthToDate);
			if(list.size() != 0) {			
				averageBill = (Double)((Map)list.get(0)).get("BILL");
			}

			// 현월 요금 취득
//			BillingDayEM billingDayEM = new BillingDayEM();
//			billingDayEM.setContract(contract);
//			List<BillingDayEM> billingDayEMs = billingDayEMDao.getBillingDayEMs(billingDayEM, billDate, monthToDate);
//			for (BillingDayEM result: billingDayEMs) {
//				
//				standardBill += result.getBill() == null ? 0 : result.getBill();
//			}

			// billing_month에도 매일 과금 정보를 등록한다는 전제
			billingMonthEMs.clear();
			billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, billDate, monthToDate);
			for (BillingMonthEM result: billingMonthEMs) {
				
				standardBill += result.getBill() == null ? 0 : result.getBill();
			}
    	} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
    		
    		//averageBill = billingMonthGMDao.getAverageBill(contract);

			BillingMonthGM billingMonthGM = new BillingMonthGM();
			billingMonthGM.setContract(contract);
			billingMonthGM.setLocation(contract.getLocation());
			
			// 전월 요금 취득
			List<BillingMonthGM> billingMonthGMs = billingMonthGMDao.getBillingMonthGMs(billingMonthGM, lastBillDate, lastMonthToDate);
			
			for (BillingMonthGM result: billingMonthGMs) {
				
				lastBill += result.getBill() == null ? 0 : result.getBill();
			}			
//
//			BillingPk billingPk = new BillingPk();
//			billingPk.setYyyymmdd(startDay);
//			billingMonthGM.setId(billingPk);
			
			// 같은 지역 평균값
			List<Object> list = billingMonthGMDao.getBillingMonthGMsAvg(billingMonthGM, billDate, monthToDate);
			if(list.size() != 0) {
				averageBill = (Double)((Map)list.get(0)).get("BILL");
			}

			// 현월 요금 취득
//			BillingDayGM billingDayGM = new BillingDayGM();
//			billingDayGM.setContract(contract);
//
//			List<BillingDayGM> billingDayGMs = billingDayGMDao.getBillingDayGMs(billingDayGM, billDate, monthToDate);
//			for (BillingDayGM result: billingDayGMs) {
//				
//				standardBill += result.getBill() == null ? 0 : result.getBill();
//			}
			billingMonthGMs.clear();
			billingMonthGMs = billingMonthGMDao.getBillingMonthGMs(billingMonthGM, billDate, monthToDate);
			for (BillingMonthGM result: billingMonthGMs) {
				
				standardBill += result.getBill() == null ? 0 : result.getBill();
			}
    	} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
    		
    		//averageBill = billingMonthWMDao.getAverageBill(contract);
			// 전월 요금 취득
			BillingMonthWM billingMonthWM = new BillingMonthWM();
			billingMonthWM.setContract(contract);
			billingMonthWM.setLocation(contract.getLocation());

			List<BillingMonthWM> billingMonthWMs = billingMonthWMDao.getBillingMonthWMs(billingMonthWM, lastBillDate, lastMonthToDate);
			for (BillingMonthWM result: billingMonthWMs) {
				
				lastBill += result.getBill() == null ? 0 : result.getBill();
			}

//			BillingPk billingPk = new BillingPk();
//			billingPk.setYyyymmdd(startDay);
//			billingMonthWM.setId(billingPk);
			// 같은 지역 평균값
			List<Object> list = billingMonthWMDao.getBillingMonthWMsAvg(billingMonthWM, billDate, monthToDate);
			if(list.size() != 0) {
				averageBill = (Double)((Map)list.get(0)).get("BILL");
			}
			// 현월 요금 취득
//			BillingDayWM billingDayWM = new BillingDayWM();
//			billingDayWM.setContract(contract);
//			List<BillingDayWM> billingDayWMs = billingDayWMDao.getBillingDayWMs(billingDayWM, billDate, monthToDate);
//			
//			for (BillingDayWM result: billingDayWMs) {
//				
//				standardBill += result.getBill() == null ? 0 : result.getBill();
//			}
			billingMonthWMs.clear();
			billingMonthWMs = billingMonthWMDao.getBillingMonthWMs(billingMonthWM, billDate, monthToDate);
			
			for (BillingMonthWM result: billingMonthWMs) {
				
				standardBill += result.getBill() == null ? 0 : result.getBill();
			}
    	}
    	
    	List<Object> returnList = new ArrayList<Object>();
    	returnList.add(averageBill.intValue());
//    	returnList.add(lastBill);
//    	returnList.add(standardBill);
    	returnList.add(lastBill.intValue());
    	returnList.add(standardBill.intValue());
    	
		return returnList;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energyConsumptionSearch.EnergyConsumptionSearchManager#getSavingTarget(com.aimir.model.system.OperatorContract, java.lang.String)
	 */
	public Double getSavingTarget(OperatorContract operatorContract, String fromDay, String toDay) {
		
		EnergySavingTarget energySavingTarget = new EnergySavingTarget();
		
		energySavingTarget.setOperatorContract(operatorContract);
		//energySavingTarget.setCreateDate(basicDay);
		
		Double savingTarget = 0.0;
		
		//List<EnergySavingTarget> energySavingTargets = energySavingTargetDao.getEnergySavingTarget(energySavingTarget, null, null);
		List<EnergySavingTarget> energySavingTargets = energySavingTargetDao.getEnergySavingTarget(energySavingTarget, fromDay, toDay);
		if (0 < energySavingTargets.size()) {
			
			savingTarget = (null == energySavingTargets.get(0).getSavingTarget() ? 0.0 : energySavingTargets.get(0).getSavingTarget());
		}
		
		return savingTarget;
	}
	
}
