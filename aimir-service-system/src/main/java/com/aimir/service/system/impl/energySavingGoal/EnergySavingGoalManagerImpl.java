/**
 * EnergySavingGoalManagerImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.service.system.impl.energySavingGoal;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.aimir.dao.system.EnergySavingGoalDao;
import com.aimir.dao.system.energySavingGoal.EnergySavingTargetDao;
import com.aimir.dao.system.energySavingGoal.NotificationDao;
import com.aimir.dao.system.energySavingGoal.NotificationTemplateDao;
import com.aimir.dao.system.membership.OperatorContractDao;
import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.mvm.BillingDayGM;
import com.aimir.model.mvm.BillingDayWM;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.mvm.BillingMonthGM;
import com.aimir.model.mvm.BillingMonthWM;
import com.aimir.model.mvm.BillingPk;
import com.aimir.model.system.Contract;
import com.aimir.model.system.EnergySavingTarget;
import com.aimir.model.system.Notification;
import com.aimir.model.system.NotificationTemplate;
import com.aimir.model.system.OperatorContract;
import com.aimir.service.system.energySavingGoal.EnergySavingGoalManager;
import com.aimir.util.BillDateUtil;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * EnergySavingGoalManagerImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 6. 3.   v1.0       김상연         에너지 절감 목표
 *
 */

@Service(value="savingGoalManager")
public class EnergySavingGoalManagerImpl implements EnergySavingGoalManager{

	@Autowired
	OperatorContractDao operatorContractDao;
	
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
    EnergySavingTargetDao energySavingTargetDao;
    
    @Autowired
    NotificationDao notificationDao;
    
    @Autowired
    NotificationTemplateDao notificationTemplateDao;
    
	/* (non-Javadoc)
	 * @see com.aimir.service.system.energySavingGoal.EnergySavingGoalManager#getMaxDay(com.aimir.model.system.Contract)
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
	 * @see com.aimir.service.system.energySavingGoal.EnergySavingGoalManager#getSavingTarget(java.lang.String)
	 */
	public Double getSavingTarget(int operatorContractId, String basicDay) {

		EnergySavingTarget energySavingTarget = new EnergySavingTarget();
		
		OperatorContract operatorContract = operatorContractDao.get(operatorContractId);
		
		energySavingTarget.setOperatorContract(operatorContract);
		energySavingTarget.setCreateDate(basicDay);
		
		Double savingTarget = 0.0;
		
		List<EnergySavingTarget> energySavingTargets = energySavingTargetDao.getEnergySavingTarget(energySavingTarget, null, null);
		
		if (0 < energySavingTargets.size()) {
			
			savingTarget = (null == energySavingTargets.get(0).getSavingTarget() ? 0.0 : energySavingTargets.get(0).getSavingTarget());
		}
		
		return savingTarget;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energySavingGoal.EnergySavingGoalManager#getMaxBill(com.aimir.model.system.Contract)
	 */
	public Double getMaxBill(Contract contract, String yyyymmdd) {
		
		String serviceType = contract.getServiceTypeCode().getCode();
		
		Double maxBill = 0.0;
		
		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			
			maxBill = billingMonthEMDao.getMaxBill(contract, yyyymmdd);
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
			
			maxBill = billingMonthGMDao.getMaxBill(contract, yyyymmdd);
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			
			maxBill = billingMonthWMDao.getMaxBill(contract, yyyymmdd);
		} 

		return maxBill;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energySavingGoal.EnergySavingGoalManager#getForecastBill(com.aimir.model.system.Contract, java.lang.String)
	 */
	public Double getForecastBill(Contract contract, String lastBillDay, String toDay) {

		String serviceType = contract.getServiceTypeCode().getCode();
		
		//String billDay = getBillDay(contract, basicDay);
		String nextBillDay = getNextBillDay(lastBillDay);
		        
        int dayCount = getCompareDay(lastBillDay, nextBillDay);
		int size = 0;
		
		Double forecastBill = 0.0;
		
		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			
			BillingDayEM billingDayEM = new BillingDayEM();
			
			billingDayEM.setContract(contract);
			
			List<BillingDayEM> billingDayEMs = billingDayEMDao.getBillingDayEMs(billingDayEM, lastBillDay, toDay);
			
			size = billingDayEMs.size();
			
			for (BillingDayEM result : billingDayEMs) {
				
				forecastBill += (null == result.getBill() ? 0.0 : result.getBill());
			}
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
			
			BillingDayGM billingDayGM = new BillingDayGM();
			
			billingDayGM.setContract(contract);
			
			List<BillingDayGM> billingDayGMs = billingDayGMDao.getBillingDayGMs(billingDayGM, lastBillDay, toDay);
			
			size = billingDayGMs.size();
			
			for (BillingDayGM result : billingDayGMs) {
				
				forecastBill += (null == result.getBill() ? 0.0 : result.getBill());
			}
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			
			BillingDayWM billingDayWM = new BillingDayWM();
			
			billingDayWM.setContract(contract);
			
			List<BillingDayWM> billingDayWMs = billingDayWMDao.getBillingDayWMs(billingDayWM, lastBillDay, toDay);
			
			size = billingDayWMs.size();
			
			for (BillingDayWM result : billingDayWMs) {
				
				forecastBill += (null == result.getBill() ? 0.0 : result.getBill());
			}
		} 

		if (0 == size) {
			
			forecastBill = 0.0;
		} else {
			
			forecastBill = forecastBill / size * dayCount;
		}

		return forecastBill;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energySavingGoal.EnergySavingGoalManager#getLastMonthBill(com.aimir.model.system.Contract, java.lang.String)
	 */
	public Double getLastMonthBill(Contract contract, String lastBillDay, String basicDay) {

		String serviceType = contract.getServiceTypeCode().getCode();
		
		//String billDay = getBillDay(contract, basicDay);
		String formDay = CalendarUtil.getDate(lastBillDay, Calendar.MONTH, -1);
		String toDay = CalendarUtil.getDate(basicDay, Calendar.MONTH, -1);
		Double lastMonthBill = 0.0;
		
		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			
			BillingMonthEM billingMonthEM = new BillingMonthEM();
			
			//billingMonthEM.setYyyymmdd(billDay);
			billingMonthEM.setContract(contract);
			
			List<BillingMonthEM> billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, formDay, toDay);
			
			for (BillingMonthEM result : billingMonthEMs) {
				
				lastMonthBill += (null == result.getBill() ? 0.0 : result.getBill());
			}
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
			
			BillingMonthGM billingMonthGM = new BillingMonthGM();
			
			//billingMonthGM.setYyyymmdd(billDay);
			billingMonthGM.setContract(contract);
			
			List<BillingMonthGM> billingMonthGMs = billingMonthGMDao.getBillingMonthGMs(billingMonthGM, formDay, toDay);
			
			for (BillingMonthGM result : billingMonthGMs) {
				
				lastMonthBill += (null == result.getBill() ? 0.0 : result.getBill());
			}
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			
			BillingMonthWM billingMonthWM = new BillingMonthWM();
			
			//billingMonthWM.setYyyymmdd(billDay);
			billingMonthWM.setContract(contract);
			
			List<BillingMonthWM> billingMonthWMs = billingMonthWMDao.getBillingMonthWMs(billingMonthWM, formDay, toDay);
			
			for (BillingMonthWM result : billingMonthWMs) {
				
				lastMonthBill += (null == result.getBill() ? 0.0 : result.getBill());
			}
		} 

		return lastMonthBill;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energySavingGoal.EnergySavingGoalManager#getLastYearSameMonthBill(com.aimir.model.system.Contract, java.lang.String)
	 */
	public Double getLastYearSameMonthBill(Contract contract, String lastBillDay, String basicDay) {

		String serviceType = contract.getServiceTypeCode().getCode();
		
//		int billDate = contract.getBillDate() == null ? 1 : contract.getBillDate();
//		int year = Integer.parseInt(basicDay.substring(0, 4)) - 1;
//		int month = Integer.parseInt(basicDay.substring(4, 6));
//		int date = Integer.parseInt(basicDay.substring(6, 8));
//		
//		Calendar calendar =  Calendar.getInstance();
//				
//		calendar.set(year, billDate > date ? month - 1 : month, billDate);
//		
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
//		
//		String billDay = formatter.format(calendar.getTime());
		String fromDay = CalendarUtil.getDate(lastBillDay, Calendar.YEAR, -1);
		String toDay = CalendarUtil.getDate(basicDay, Calendar.YEAR, -1);

		Double lastYearSameMonthBill = 0.0;
		
		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
			
			BillingMonthEM billingMonthEM = new BillingMonthEM();
			
			//billingMonthEM.setYyyymmdd(billDay);
			billingMonthEM.setContract(contract);
			
			List<BillingMonthEM> billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, fromDay, toDay);
			
			for (BillingMonthEM result : billingMonthEMs) {
				
				lastYearSameMonthBill += (null == result.getBill() ? 0.0 : result.getBill());
			}
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
			
			BillingMonthGM billingMonthGM = new BillingMonthGM();
			
			//billingMonthGM.setYyyymmdd(billDay);
			billingMonthGM.setContract(contract);

			List<BillingMonthGM> billingMonthGMs = billingMonthGMDao.getBillingMonthGMs(billingMonthGM, fromDay, toDay);

			for (BillingMonthGM result : billingMonthGMs) {

				lastYearSameMonthBill += (null == result.getBill() ? 0.0 : result.getBill());
			}
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
			
			BillingMonthWM billingMonthWM = new BillingMonthWM();
			
			//billingMonthWM.setYyyymmdd(billDay);
			billingMonthWM.setContract(contract);
			
			List<BillingMonthWM> billingMonthWMs = billingMonthWMDao.getBillingMonthWMs(billingMonthWM, fromDay, toDay);
			
			for (BillingMonthWM result : billingMonthWMs) {
				
				lastYearSameMonthBill += (null == result.getBill() ? 0.0 : result.getBill());
			}
		} 

		return lastYearSameMonthBill;
	}
    
	/**
	 * method name : getBillDay
	 * method Desc : 직전 결제일 조회
	 *
	 * @param contract
	 * @param basicDay
	 * @return
	 */
	private String getBillDay(Contract contract, String basicDay) {
		
		int billDate = contract.getBillDate() == null ? 1 : contract.getBillDate();
		int year = Integer.parseInt(basicDay.substring(0, 4));
		int month = Integer.parseInt(basicDay.substring(4, 6)) - 1;
		int date = Integer.parseInt(basicDay.substring(6, 8));

		Calendar calendar =  Calendar.getInstance();
				
		calendar.set(year, billDate > date ? month - 1 : month, billDate);
		
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		
		return formatter.format(calendar.getTime());
	}
	
	/**
	 * method name : getNextBillDay
	 * method Desc : 다음 결제일 조회
	 *
	 * @param basicDay
	 * @return
	 */
	private String getNextBillDay(String basicDay) {
		
		int year = Integer.parseInt(basicDay.substring(0, 4));
		int month = Integer.parseInt(basicDay.substring(4, 6));
		int date = Integer.parseInt(basicDay.substring(6, 8));
		
		Calendar calendar =  Calendar.getInstance();
		
		calendar.set(year, month, date);
		
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		
		return formatter.format(calendar.getTime());
	}
	
	/**
	 * method name : getCompareDay
	 * method Desc : 날짜 간 일 수 차이 계산
	 *
	 * @param fromDay
	 * @param toDay
	 * @return
	 */
	private int getCompareDay(String fromDay, String toDay) {
		
		Calendar calendar =  Calendar.getInstance();
		
		int fromYear = Integer.parseInt(fromDay.substring(0, 4));
		int fromMonth = Integer.parseInt(fromDay.substring(4, 6));
		int fromDate = Integer.parseInt(fromDay.substring(6, 8));
		
		calendar.set(fromYear, fromMonth, fromDate);
		
		long from = calendar.getTimeInMillis();
		
		int toYear = Integer.parseInt(toDay.substring(0, 4));
		int toMonth = Integer.parseInt(toDay.substring(4, 6));
		int toDate = Integer.parseInt(toDay.substring(6, 8));
		
		calendar.set(toYear, toMonth, toDate);
		
		long to = calendar.getTimeInMillis();

		return (int)((to - from) / 1000 / 60 / 60 / 24);
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energySavingGoal.EnergySavingGoalManager#saveSavingTarget(int, java.lang.Double, java.lang.String)
	 */
	@Transactional
	public boolean saveSavingTarget(int operatorContractId, Double savingTarget, String basicDay) {

		OperatorContract operatorContract = operatorContractDao.get(operatorContractId);
		
		Contract contract = operatorContract.getContract();
		
		EnergySavingTarget energySavingTarget = new EnergySavingTarget();
		
		energySavingTarget.setOperatorContract(operatorContract);

		boolean isFirst = false;

		List<EnergySavingTarget> energySavingTargets = energySavingTargetDao.getEnergySavingTarget(energySavingTarget, getBillDay(contract, basicDay), basicDay);
		
		int size = energySavingTargets.size();
		
		if (0 == size) {
			
			energySavingTarget.setCreateDate(basicDay);
			
			energySavingTargets = energySavingTargetDao.getEnergySavingTarget(energySavingTarget, null, null);
			
			if (0 < energySavingTargets.size()) {
				
				energySavingTarget.setNotification(energySavingTargets.get(0).getNotification());
			} else {
				// 처음 목표 설정 시에만, 디폴트 통보 정보 등록을 위해 결과값을 true로 설정한다.
				isFirst =  true;
			}

			energySavingTarget.setSavingTarget(savingTarget);
			
			energySavingTargetDao.add(energySavingTarget);
		} else if (0 < size) {
			
			energySavingTarget = energySavingTargets.get(0);
			
			energySavingTarget.setCreateDate(basicDay);
			energySavingTarget.setSavingTarget(savingTarget);
			
			energySavingTargetDao.update(energySavingTarget);
		}

		return isFirst;
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energySavingGoal.EnergySavingGoalManager#getContractGrid(int)
	 */
	public List<Map<String, Object>> getContractGrid(int operatorContractId, String basicDay) {

		OperatorContract operatorContract = operatorContractDao.get(operatorContractId);
		
		Contract contract = operatorContract.getContract();
		
		String lang = contract.getSupplier().getLang().getCode_2letter();
		String Country = contract.getSupplier().getCountry().getCode_2letter();
		String serviceType = contract.getServiceTypeCode().getCode();
    	DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());
		
		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> returnMap;

		Double maxBill = getMaxBill(contract, null);
		Double savingTarget = 0.0;
		Double lastMonthBill = 0.0;
		Double lastYearSameMonthBill = 0.0;
		Double bill = 0.0;
		Double rate = 0.0;
		
		String fromDay;
		String toDay;

		try{
			if(basicDay.length() == 0) {
				basicDay = BillDateUtil.getMonthToDate(contract, TimeUtil.getCurrentDay(), 1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		String startDate = energySavingTargetDao.getEnergySavingStartDate(operatorContractId);
		if(startDate.length() == 0) {
			return this.setDefaultGrid(contract, basicDay, operatorContractId);
		}

		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {

			BillingMonthEM billingMonthEM = new BillingMonthEM();
			billingMonthEM.setContract(contract);
			startDate = getBillDay(contract, startDate);
			List<BillingMonthEM> billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, startDate, null);

			if(billingMonthEMs.size() == 0) {
				return this.setDefaultGrid(contract, basicDay, operatorContractId);
			}
            for (BillingMonthEM result : billingMonthEMs) {

				returnMap = new HashMap<String, Object>();
				fromDay = getBillDay(contract, result.getYyyymmdd());
				toDay = getToDay(getNextBillDay(fromDay));
				
				savingTarget = getSavingTarget(operatorContractId, toDay);
				lastMonthBill = getLastMonthBill(contract, fromDay, toDay);
				lastYearSameMonthBill = getLastYearSameMonthBill(contract, fromDay, toDay);

				bill = result.getBill();
				rate = this.getSavingPercentage(bill, savingTarget);
//				rate = (0 == savingTarget ? 0 : (savingTarget - bill) / savingTarget * 100);

//				if (0 < rate) {
//					
//					rate = Math.floor(rate);
//				} else {
//					
//					rate = Math.ceil(rate);
//				}

				returnMap.put("day", TimeLocaleUtil.getLocaleDateByMediumFormat(fromDay, lang, Country) + " ~ " + TimeLocaleUtil.getLocaleDateByMediumFormat(toDay, lang, Country));
				returnMap.put("savingTarget", dfCd.format(savingTarget));
				returnMap.put("maxBill", dfCd.format(maxBill));
				returnMap.put("lastMonthBill", dfCd.format(lastMonthBill));
				returnMap.put("lastYearSameMonthBill", dfCd.format(lastYearSameMonthBill));
				returnMap.put("bill", dfCd.format(bill));
				returnMap.put("rate", rate);

				returnList.add(returnMap);
			}
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {

			BillingMonthGM billingMonthGM = new BillingMonthGM();
			billingMonthGM.setContract(contract);
			List<BillingMonthGM> billingMonthGMs = billingMonthGMDao.getBillingMonthGMs(billingMonthGM, startDate, null);
			if(billingMonthGMs.size() == 0) {
				return this.setDefaultGrid(contract, basicDay, operatorContractId);

			}
			for (BillingMonthGM result : billingMonthGMs) {
				
				returnMap = new HashMap<String, Object>();

				fromDay = getBillDay(contract, result.getYyyymmdd());
				toDay = getToDay(getNextBillDay(fromDay));

				savingTarget = getSavingTarget(operatorContractId, toDay);
				lastMonthBill = getLastMonthBill(contract, fromDay, toDay);
				lastYearSameMonthBill = getLastYearSameMonthBill(contract, fromDay, toDay);
				bill = result.getBill();
				rate = this.getSavingPercentage(bill, savingTarget);
				//rate = (0 == savingTarget ? 0 : (savingTarget - bill) / savingTarget * 100);
//				if (0 < rate) {
//					
//					rate = Math.floor(rate);
//				} else {
//					
//					rate = Math.ceil(rate);
//				}

				returnMap.put("day", TimeLocaleUtil.getLocaleDateByMediumFormat(fromDay, lang, Country) + " ~ " + TimeLocaleUtil.getLocaleDateByMediumFormat(toDay, lang, Country));
				returnMap.put("savingTarget", dfCd.format(savingTarget));
				returnMap.put("maxBill", dfCd.format(maxBill));
				returnMap.put("lastMonthBill", dfCd.format(lastMonthBill));
				returnMap.put("lastYearSameMonthBill", dfCd.format(lastYearSameMonthBill));
				returnMap.put("bill", dfCd.format(bill));
				returnMap.put("rate", rate);
				
				returnList.add(returnMap);
			}
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {

			BillingMonthWM billingMonthWM = new BillingMonthWM();
			billingMonthWM.setContract(contract);
			List<BillingMonthWM> billingMonthWMs = billingMonthWMDao.getBillingMonthWMs(billingMonthWM, startDate, null);
			if(billingMonthWMs.size() == 0) {
				return this.setDefaultGrid(contract, basicDay, operatorContractId);
			}
			for (BillingMonthWM result : billingMonthWMs) {
				
				returnMap = new HashMap<String, Object>();

				fromDay = getBillDay(contract, result.getYyyymmdd());
				toDay = getToDay(getNextBillDay(fromDay));

				savingTarget = getSavingTarget(operatorContractId, toDay);
				lastMonthBill = getLastMonthBill(contract, fromDay, toDay);
				lastYearSameMonthBill = getLastYearSameMonthBill(contract, fromDay, toDay);

				bill = result.getBill();
				rate = this.getSavingPercentage(bill, savingTarget);

				returnMap.put("day", TimeLocaleUtil.getLocaleDateByMediumFormat(fromDay, lang, Country) + " ~ " + TimeLocaleUtil.getLocaleDateByMediumFormat(toDay, lang, Country));
				returnMap.put("savingTarget", dfCd.format(savingTarget));
				returnMap.put("maxBill", dfCd.format(maxBill));
				returnMap.put("lastMonthBill", dfCd.format(lastMonthBill));
				returnMap.put("lastYearSameMonthBill", dfCd.format(lastYearSameMonthBill));
				returnMap.put("bill", dfCd.format(bill));
				returnMap.put("rate", rate);
				
				returnList.add(returnMap);
			}
		}
		return returnList;
	}

	public List<Map<String, Object>> getEnergySavingResultsYearComboBox(int operatorContractId, String basicDay) {

		OperatorContract operatorContract = operatorContractDao.get(operatorContractId);

		Contract contract = operatorContract.getContract();
		String serviceType = contract.getServiceTypeCode().getCode();

		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();

		Map<String, Object> returnMap;

		String fromDay;
		String toDay;
		String compareYYYY = "";

		String startDate = energySavingTargetDao.getEnergySavingStartDate(operatorContractId);
		if(startDate.length() == 0) {
			returnMap = new HashMap<String, Object>();
			//fromDay = BillDateUtil.getBillDate(contract, basicDay, -1);
			toDay = basicDay;
			returnMap.put("id", toDay.substring(0, 4));
			returnMap.put("name", toDay.substring(0, 4));
			returnList.add(returnMap);

			return returnList;
		}

		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {

			BillingMonthEM billingMonthEM = new BillingMonthEM();
			billingMonthEM.setContract(contract);
			startDate = getBillDay(contract, startDate);
			//List<BillingMonthEM> billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, startDate, null);
			List<Object> list = billingMonthEMDao.getBillingMonthEMsComboBox(billingMonthEM, startDate, null);
			
			if(list.size() == 0) {
				returnMap = new HashMap<String, Object>();
				//fromDay = BillDateUtil.getBillDate(contract, basicDay, -1);
				toDay = basicDay;
				returnMap.put("id", toDay.substring(0, 4));
				returnMap.put("name", toDay.substring(0, 4));
				return returnList;
			}
            for (Object object : list) {
				returnMap = new HashMap<String, Object>();
            	String yyyymmdd = (String)((Map<String, Object>)object).get("YYYYMMDD");
        
				fromDay = BillDateUtil.getBillDate(contract, yyyymmdd, -1);
				toDay = BillDateUtil.getMonthToDate(contract, fromDay, 0);

				if(!toDay.substring(0, 4).equals(compareYYYY)) {
					returnMap.put("id", toDay.substring(0, 4));
					returnMap.put("name", toDay.substring(0, 4));
					returnList.add(returnMap);
				}
				compareYYYY = toDay.substring(0, 4);
			}
		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {

			BillingMonthGM billingMonthGM = new BillingMonthGM();
			billingMonthGM.setContract(contract);
			List<BillingMonthGM> billingMonthGMs = billingMonthGMDao.getBillingMonthGMs(billingMonthGM, startDate, null);
			if(billingMonthGMs.size() == 0) {
				returnMap = new HashMap<String, Object>();
				//fromDay = BillDateUtil.getBillDate(contract, basicDay, -1);
				toDay = basicDay;
				returnMap.put("id", toDay.substring(0, 4));
				returnMap.put("name", toDay.substring(0, 4));
				return returnList;

			}
			for (BillingMonthGM result : billingMonthGMs) {

				returnMap = new HashMap<String, Object>();
				fromDay = BillDateUtil.getBillDate(contract, result.getYyyymmdd(), -1);
				toDay = BillDateUtil.getMonthToDate(contract, fromDay, 0);

				returnMap.put("id", toDay.substring(0, 4));
				returnMap.put("name", toDay.substring(0, 4));
				returnList.add(returnMap);
			}
		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {

			BillingMonthWM billingMonthWM = new BillingMonthWM();
			billingMonthWM.setContract(contract);
			List<BillingMonthWM> billingMonthWMs = billingMonthWMDao.getBillingMonthWMs(billingMonthWM, startDate, null);
			if(billingMonthWMs.size() == 0) {
				returnMap = new HashMap<String, Object>();
				//fromDay = BillDateUtil.getBillDate(contract, basicDay, -1);
				toDay = basicDay;
				returnMap.put("id", toDay.substring(0, 4));
				returnMap.put("name", toDay.substring(0, 4));
				return returnList;
			}
			for (BillingMonthWM result : billingMonthWMs) {
				returnMap = new HashMap<String, Object>();
				fromDay = BillDateUtil.getBillDate(contract, result.getYyyymmdd(), -1);
				toDay = BillDateUtil.getMonthToDate(contract, fromDay, 0);

				returnMap.put("id", toDay.substring(0, 4));
				returnMap.put("name", toDay.substring(0, 4));
				returnList.add(returnMap);
			}
		} 
		return returnList;
	}

	public List<Map<String, Object>> getEnergySavingResultsMonthComboBox(int operatorContractId, String selYear) {

		OperatorContract operatorContract = operatorContractDao.get(operatorContractId);

		Contract contract = operatorContract.getContract();
		String serviceType = contract.getServiceTypeCode().getCode();

		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();

		Map<String, Object> returnMap;

		String fromDay;
		String toDay;

		try{
			String startDate = energySavingTargetDao.getEnergySavingStartDate(operatorContractId);
			if(startDate.length() == 0) {
				returnMap = new HashMap<String, Object>();
				fromDay = BillDateUtil.getBillDate(contract, TimeUtil.getCurrentDay(), -1);
				toDay = TimeUtil.getCurrentDay();
				returnMap.put("id", toDay.substring(4, 6));
				returnMap.put("name", toDay.substring(4, 6));
				returnList.add(returnMap);

				return returnList;
			}
	
			if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {

				BillingMonthEM billingMonthEM = new BillingMonthEM();
				billingMonthEM.setContract(contract);
				BillingPk billingPk = new BillingPk();
				billingPk.setYyyymmdd(selYear);
				billingMonthEM.setId(billingPk);
				
				List<Object> list = billingMonthEMDao.getBillingMonthEMsComboBox(billingMonthEM, null, null);
				
				if(list.size() == 0) {
					returnMap = new HashMap<String, Object>();
					//fromDay = BillDateUtil.getBillDate(contract, basicDay, -1);
					toDay = TimeUtil.getCurrentDay();
					returnMap.put("id", toDay.substring(4, 6));
					returnMap.put("name", toDay.substring(4, 6));
					return returnList;
				}
	            for (Object object : list) {
	            	
	            	returnMap = new HashMap<String, Object>();
	            	String yyyymmdd = (String)((Map<String, Object>)object).get("YYYYMMDD");
	            	
					fromDay = BillDateUtil.getBillDate(contract, yyyymmdd, -1);
					//toDay = BillDateUtil.getMonthToDate(contract, fromDay, 0);

					if(selYear.equals(fromDay.substring(0, 4))) {
						returnMap.put("id", fromDay.substring(4, 6));
						returnMap.put("name", fromDay.substring(4, 6));
						returnList.add(returnMap);
					}					
				}
			} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
	
				BillingMonthGM billingMonthGM = new BillingMonthGM();
				BillingPk billingPk = new BillingPk();
		
				billingPk.setYyyymmdd(selYear);
				billingMonthGM.setId(billingPk);
				billingMonthGM.setContract(contract);
				
				List<BillingMonthGM> billingMonthGMs = billingMonthGMDao.getBillingMonthGMs(billingMonthGM, null, null);
				if(billingMonthGMs.size() == 0) {
					returnMap = new HashMap<String, Object>();
					//fromDay = BillDateUtil.getBillDate(contract, basicDay, -1);
					toDay = TimeUtil.getCurrentDay();
					returnMap.put("id", toDay.substring(0, 4));
					returnMap.put("name", toDay.substring(0, 4));
					return returnList;
	
				}
				for (BillingMonthGM result : billingMonthGMs) {
	
					returnMap = new HashMap<String, Object>();
					fromDay = BillDateUtil.getBillDate(contract, result.getYyyymmdd(), -1);
					toDay = BillDateUtil.getMonthToDate(contract, fromDay, 0);
	
					returnMap.put("id", toDay.substring(0, 4));
					returnMap.put("name", toDay.substring(0, 4));
					returnList.add(returnMap);
				}
			} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
	
				BillingMonthWM billingMonthWM = new BillingMonthWM();
				BillingPk billingPk = new BillingPk();
		
				billingPk.setYyyymmdd(selYear);
				billingMonthWM.setId(billingPk);
				billingMonthWM.setContract(contract);

				List<BillingMonthWM> billingMonthWMs = billingMonthWMDao.getBillingMonthWMs(billingMonthWM, null, null);
				if(billingMonthWMs.size() == 0) {
					returnMap = new HashMap<String, Object>();
					//fromDay = BillDateUtil.getBillDate(contract, basicDay, -1);
					toDay = TimeUtil.getCurrentDay();
					returnMap.put("id", toDay.substring(0, 4));
					returnMap.put("name", toDay.substring(0, 4));
					return returnList;
				}
				for (BillingMonthWM result : billingMonthWMs) {
					returnMap = new HashMap<String, Object>();
					fromDay = BillDateUtil.getBillDate(contract, result.getYyyymmdd(), -1);
					toDay = BillDateUtil.getMonthToDate(contract, fromDay, 0);
	
					returnMap.put("id", toDay.substring(0, 4));
					returnMap.put("name", toDay.substring(0, 4));
					returnList.add(returnMap);
				}
			} 
		}catch(Exception e){
			e.printStackTrace();
		}
		return returnList;
	}

	private List<Map<String, Object>> setDefaultGrid(Contract contract, String basicDay, int operatorContractId) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> returnMap;
		String lang = contract.getSupplier().getLang().getCode_2letter();
		String Country = contract.getSupplier().getCountry().getCode_2letter();
    	DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());
    	
		String fromDay = getBillDay(contract, basicDay);
		String toDay = basicDay;

		Double bill = 0d;
		Double savingTarget = getSavingTarget(operatorContractId, toDay);
		returnMap = new HashMap<String, Object>();
		returnMap.put("day", TimeLocaleUtil.getLocaleDateByMediumFormat(fromDay, lang, Country) + " ~ " + TimeLocaleUtil.getLocaleDateByMediumFormat(toDay, lang, Country));
		returnMap.put("savingTarget", dfCd.format(savingTarget));
		returnMap.put("maxBill", dfCd.format(this.getMaxBill(contract, null)));
		returnMap.put("lastMonthBill", dfCd.format(getLastMonthBill(contract, fromDay, toDay)));
		returnMap.put("lastYearSameMonthBill", dfCd.format(getLastYearSameMonthBill(contract, fromDay, toDay)));
		returnMap.put("bill", dfCd.format(bill));
		returnMap.put("rate", this.getSavingPercentage(bill, savingTarget));

		returnList.add(returnMap);
		return returnList;	
	}

//	public List<Map<String, Object>> getContractGrid(int operatorContractId, String basicDay) {
//
//		OperatorContract operatorContract = operatorContractDao.get(operatorContractId);
//		
//		Contract contract = operatorContract.getContract();
//		
//		String lang = contract.getSupplier().getLang().getCode_2letter();
//		String Country = contract.getSupplier().getCountry().getCode_2letter();
//		String serviceType = contract.getServiceTypeCode().getCode();
//    	DecimalFormat dfCd = DecimalUtil.getDecimalFormat(contract.getSupplier().getCd());
//		
//		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
//		
//		Map<String, Object> returnMap;
//		String fromDay;
//		String toDay;
//		Double maxBill = getMaxBill(contract, null);
//		Double savingTarget;
//		Double lastMonthBill;
//		Double lastYearSameMonthBill;
//		Double bill;
//		Double rate;
//		
//		if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
//
//			returnMap = new HashMap<String, Object>();
//			
//			fromDay = getBillDay(contract, basicDay);
//			toDay = getToDay(getNextBillDay(fromDay));
//			savingTarget = getSavingTarget(operatorContractId, toDay);
//			lastMonthBill = getLastMonthBill(contract, toDay);
//			lastYearSameMonthBill = getLastYearSameMonthBill(contract, toDay);
//			//lastMonthBill = getLastMonthBill(contract, fromDay);
//			//lastYearSameMonthBill = getLastYearSameMonthBill(contract, fromDay);
//
//			bill = getForecastBill(contract, basicDay);
//			rate = (0 == savingTarget ? 0 : (savingTarget - bill) / savingTarget * 100);
//			
//			if (0 < rate) {
//				
//				rate = Math.floor(rate);
//			} else {
//				
//				rate = Math.ceil(rate);
//			}
//				
//			returnMap.put("day", TimeLocaleUtil.getLocaleDateByLongFormat(fromDay, lang, Country) + " ~ " + TimeLocaleUtil.getLocaleDateByLongFormat(toDay, lang, Country));
//			returnMap.put("savingTarget", dfCd.format(savingTarget));
//			returnMap.put("maxBill", dfCd.format(maxBill));
//			returnMap.put("lastMonthBill", dfCd.format(lastMonthBill));
//			returnMap.put("lastYearSameMonthBill", dfCd.format(lastYearSameMonthBill));
//			returnMap.put("bill", dfCd.format(bill));
//			returnMap.put("rate", rate);
//			
//			returnList.add(returnMap);
//			
//			BillingMonthEM billingMonthEM = new BillingMonthEM();
//			
//			billingMonthEM.setContract(contract);
//			
//			List<BillingMonthEM> billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM);
//			
//            for (BillingMonthEM result : billingMonthEMs) {
//				
//				returnMap = new HashMap<String, Object>();
//				
//				fromDay = getFromDay(result.getYyyymmdd());
//				toDay = getToDay(result.getYyyymmdd());
//				savingTarget = getSavingTarget(operatorContractId, toDay);
//				lastMonthBill = getLastMonthBill(contract, toDay);
//				lastYearSameMonthBill = getLastYearSameMonthBill(contract, toDay);
////				lastMonthBill = getLastMonthBill(contract, fromDay);
////				lastYearSameMonthBill = getLastYearSameMonthBill(contract, fromDay);
//				bill = result.getBill();
//				rate = (0 == savingTarget ? 0 : (savingTarget - bill) / savingTarget * 100);
//					
//				if (0 < rate) {
//					
//					rate = Math.floor(rate);
//				} else {
//					
//					rate = Math.ceil(rate);
//				}
//					
//				returnMap.put("day", TimeLocaleUtil.getLocaleDateByLongFormat(fromDay, lang, Country) + " ~ " + TimeLocaleUtil.getLocaleDateByLongFormat(toDay, lang, Country));
//				returnMap.put("savingTarget", dfCd.format(savingTarget));
//				returnMap.put("maxBill", dfCd.format(maxBill));
//				returnMap.put("lastMonthBill", dfCd.format(lastMonthBill));
//				returnMap.put("lastYearSameMonthBill", dfCd.format(lastYearSameMonthBill));
//				returnMap.put("bill", dfCd.format(bill));
//				returnMap.put("rate", rate);
//				
//				returnList.add(returnMap);
//			}
//		} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
//			
//			returnMap = new HashMap<String, Object>();
//			
//			fromDay = getBillDay(contract, basicDay);
//			toDay = getToDay(getNextBillDay(fromDay));
//			savingTarget = getSavingTarget(operatorContractId, toDay);
//			lastMonthBill = getLastMonthBill(contract, fromDay);
//			lastYearSameMonthBill = getLastYearSameMonthBill(contract, fromDay);
////			lastMonthBill = getLastMonthBill(contract, toDay);
////			lastYearSameMonthBill = getLastYearSameMonthBill(contract, toDay);
//			bill = getForecastBill(contract, basicDay);
//			rate = (0 == savingTarget ? 0 : (savingTarget - bill) / savingTarget * 100);
//				
//			if (0 < rate) {
//				
//				rate = Math.floor(rate);
//			} else {
//				
//				rate = Math.ceil(rate);
//			}
//				
//			returnMap.put("day", TimeLocaleUtil.getLocaleDateByLongFormat(fromDay, lang, Country) + " ~ " + TimeLocaleUtil.getLocaleDateByLongFormat(toDay, lang, Country));
//			returnMap.put("savingTarget", dfCd.format(savingTarget));
//			returnMap.put("maxBill", dfCd.format(maxBill));
//			returnMap.put("lastMonthBill", dfCd.format(lastMonthBill));
//			returnMap.put("lastYearSameMonthBill", dfCd.format(lastYearSameMonthBill));
//			returnMap.put("bill", dfCd.format(bill));
//			returnMap.put("rate", rate);
//			
//			returnList.add(returnMap);
//			
//			BillingMonthGM billingMonthGM = new BillingMonthGM();
//			
//			billingMonthGM.setContract(contract);
//			
//			List<BillingMonthGM> billingMonthGMs = billingMonthGMDao.getBillingMonthGMs(billingMonthGM);
//			
//			for (BillingMonthGM result : billingMonthGMs) {
//				
//				returnMap = new HashMap<String, Object>();
//				
//				fromDay = getFromDay(result.getYyyymmdd());
//				toDay = getToDay(result.getYyyymmdd());
//				savingTarget = getSavingTarget(operatorContractId, toDay);
//				lastMonthBill = getLastMonthBill(contract, toDay);
//				lastYearSameMonthBill = getLastYearSameMonthBill(contract, toDay);
////				lastMonthBill = getLastMonthBill(contract, fromDay);
////				lastYearSameMonthBill = getLastYearSameMonthBill(contract, fromDay);
//				bill = result.getBill();
//				rate = (0 == savingTarget ? 0 : (savingTarget - bill) / savingTarget * 100);
//					
//				if (0 < rate) {
//					
//					rate = Math.floor(rate);
//				} else {
//					
//					rate = Math.ceil(rate);
//				}
//					
//				returnMap.put("day", TimeLocaleUtil.getLocaleDateByLongFormat(fromDay, lang, Country) + " ~ " + TimeLocaleUtil.getLocaleDateByLongFormat(toDay, lang, Country));
//				returnMap.put("savingTarget", dfCd.format(savingTarget));
//				returnMap.put("maxBill", dfCd.format(maxBill));
//				returnMap.put("lastMonthBill", dfCd.format(lastMonthBill));
//				returnMap.put("lastYearSameMonthBill", dfCd.format(lastYearSameMonthBill));
//				returnMap.put("bill", dfCd.format(bill));
//				returnMap.put("rate", rate);
//				
//				returnList.add(returnMap);
//			}
//		} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
//			
//			returnMap = new HashMap<String, Object>();
//			
//			fromDay = getBillDay(contract, basicDay);
//			toDay = getToDay(getNextBillDay(fromDay));
//			savingTarget = getSavingTarget(operatorContractId, toDay);
//			lastMonthBill = getLastMonthBill(contract, toDay);
//			lastYearSameMonthBill = getLastYearSameMonthBill(contract, toDay);
////			lastMonthBill = getLastMonthBill(contract, fromDay);
////			lastYearSameMonthBill = getLastYearSameMonthBill(contract, fromDay);
//			bill = getForecastBill(contract, basicDay);
//			rate = (0 == savingTarget ? 0 : (savingTarget - bill) / savingTarget * 100);
//				
//			if (0 < rate) {
//				
//				rate = Math.floor(rate);
//			} else {
//				
//				rate = Math.ceil(rate);
//			}
//				
//			returnMap.put("day", TimeLocaleUtil.getLocaleDateByLongFormat(fromDay, lang, Country) + " ~ " + TimeLocaleUtil.getLocaleDateByLongFormat(toDay, lang, Country));
//			returnMap.put("savingTarget", dfCd.format(savingTarget));
//			returnMap.put("maxBill", dfCd.format(maxBill));
//			returnMap.put("lastMonthBill", dfCd.format(lastMonthBill));
//			returnMap.put("lastYearSameMonthBill", dfCd.format(lastYearSameMonthBill));
//			returnMap.put("bill", dfCd.format(bill));
//			returnMap.put("rate", rate);
//			
//			returnList.add(returnMap);
//			
//			BillingMonthWM billingMonthWM = new BillingMonthWM();
//			
//			billingMonthWM.setContract(contract);
//			
//			List<BillingMonthWM> billingMonthWMs = billingMonthWMDao.getBillingMonthWMs(billingMonthWM);
//			
//			for (BillingMonthWM result : billingMonthWMs) {
//				
//				returnMap = new HashMap<String, Object>();
//				
//				fromDay = getFromDay(result.getYyyymmdd());
//				toDay = getToDay(result.getYyyymmdd());
//				savingTarget = getSavingTarget(operatorContractId, toDay);
//				lastMonthBill = getLastMonthBill(contract, toDay);
//				lastYearSameMonthBill = getLastYearSameMonthBill(contract, toDay);
////				lastMonthBill = getLastMonthBill(contract, fromDay);
////				lastYearSameMonthBill = getLastYearSameMonthBill(contract, fromDay);
//				bill = result.getBill();
//				rate = (0 == savingTarget ? 0 : (savingTarget - bill) / savingTarget * 100);
//					
//				if (0 < rate) {
//					
//					rate = Math.floor(rate);
//				} else {
//					
//					rate = Math.ceil(rate);
//				}
//					
//				returnMap.put("day", TimeLocaleUtil.getLocaleDateByLongFormat(fromDay, lang, Country) + " ~ " + TimeLocaleUtil.getLocaleDateByLongFormat(toDay, lang, Country));
//				returnMap.put("savingTarget", dfCd.format(savingTarget));
//				returnMap.put("maxBill", dfCd.format(maxBill));
//				returnMap.put("lastMonthBill", dfCd.format(lastMonthBill));
//				returnMap.put("lastYearSameMonthBill", dfCd.format(lastYearSameMonthBill));
//				returnMap.put("bill", dfCd.format(bill));
//				returnMap.put("rate", rate);
//				
//				returnList.add(returnMap);
//			}
//		} 
//		
//		return returnList;
//	}
	
	/**
	 * method name : getFromDay
	 * method Desc : 시작일자
	 *
	 * @param basicDay
	 * @return
	 */
	private String getFromDay(String basicDay) {
		
		int year = Integer.parseInt(basicDay.substring(0, 4));
		int month = Integer.parseInt(basicDay.substring(4, 6)) - 2;
		int date = Integer.parseInt(basicDay.substring(6, 8));
		
		Calendar calendar =  Calendar.getInstance();
		
		calendar.set(year, month, date);
		
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		
		return formatter.format(calendar.getTime());
	}
	
	/**
	 * method name : getToDay
	 * method Desc : 종료일자
	 *
	 * @param basicDay
	 * @return
	 */
	private String getToDay(String basicDay) {
		
		int year = Integer.parseInt(basicDay.substring(0, 4));
		int month = Integer.parseInt(basicDay.substring(4, 6)) - 1;
		int date = Integer.parseInt(basicDay.substring(6, 8)) - 1;
		
		Calendar calendar =  Calendar.getInstance();
		
		calendar.set(year, month, date);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		
		return formatter.format(calendar.getTime());
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energySavingGoal.EnergySavingGoalManager#saveNoticeTarget(int, java.lang.String, com.aimir.model.system.Notification)
	 */
	@Transactional
	public boolean saveNoticeTarget(int operatorContractId, String basicDay, Notification notification) {

		EnergySavingTarget energySavingTarget = new EnergySavingTarget();
		
		OperatorContract operatorContract = operatorContractDao.get(operatorContractId);
		
		energySavingTarget.setOperatorContract(operatorContract);
		energySavingTarget.setCreateDate(basicDay);
		
		List<EnergySavingTarget> energySavingTargets = energySavingTargetDao.getEnergySavingTarget(energySavingTarget, null, null);
		
		int size = energySavingTargets.size();
		
		if (0 == size) {
			
			//throw new Exception("No EnergySavingTarget Data");
			return false;
		} else if (0 < size) {
			
			energySavingTarget = energySavingTargets.get(0);
		}
		
		NotificationTemplate notificationTemplate = new NotificationTemplate();
		
		/* 추후 코드 화시 변경 될 부분 시작 */
		notificationTemplate.setName("TPL_EnergySavingTarget");
		/* 추후 코드 화시 변경 될 부분 끝 */
			
		List<NotificationTemplate> notificationTemplates = notificationTemplateDao.getNotificationTemplateList(notificationTemplate);
		
		if (0 < notificationTemplates.size()) {
		
			//notification.setTemplate(notificationTemplates.get(0));
		}
		
		if (null == energySavingTarget.getNotification()) {
			
			notification = notificationDao.add(notification);
			
			energySavingTarget.setNotification(notification);
			
			energySavingTargetDao.update(energySavingTarget);
		} else {
			
			Notification update = energySavingTarget.getNotification();
			
	    	update.setSmsYn(notification.getSmsYn());
	    	update.setSmsAddress(notification.getSmsAddress());
	    	update.seteMailYn(notification.geteMailYn());
	    	update.seteMailAddress(notification.geteMailAddress());
	    	update.setPeriod_1(notification.getPeriod_1());
	    	update.setPeriod_2(notification.getPeriod_2());
	    	update.setPeriod_3(notification.getPeriod_3());
	    	update.setPeriod_4(notification.getPeriod_4());
	    	update.setPeriod_5(notification.getPeriod_5());
	    	update.setConditionValue(notification.getConditionValue());
	    	//update.setTemplate(notification.getTemplate());
			
			notificationDao.update(update);
		}
		return true;
		
	}

	/* (non-Javadoc)
	 * @see com.aimir.service.system.energySavingGoal.EnergySavingGoalManager#getEnergySavingTarget(com.aimir.model.system.EnergySavingTarget)
	 */
	public List<EnergySavingTarget> getEnergySavingTarget(EnergySavingTarget energySavingTarget) {

		return energySavingTargetDao.getEnergySavingTarget(energySavingTarget, null, null);
	}
	
	/* (non-Javadoc)
	 * @see com.aimir.service.system.energySavingGoal.EnergySavingGoalManager#getEnergySavingTarget(com.aimir.model.system.EnergySavingTarget)
	 */
	public List<EnergySavingTarget> getEnergySavingTarget(EnergySavingTarget energySavingTarget, String toDay) {

		return energySavingTargetDao.getEnergySavingTarget(energySavingTarget, null, toDay);
	}
	
	public void deleteByOperatorContractId(int operatorContractId) {
		energySavingTargetDao.deleteByOperatorContractId(operatorContractId);
	}
	/**
	 * 증감율 구하기
	 * 
	 * @param pre
	 *            기준값
	 * @param now
	 *            비교값
	 * @return
	 */
	private Double getSavingPercentage(double pre, double now) {
		double dPre = 0d;
		double dNow = 0d;
		// 소숫점 첫째짜리 까지 출력
		DecimalFormat form = new DecimalFormat("0.0");
		
		if (pre == 0) {
			dPre = 1d;
		} else {
			dPre = pre;
		}
		if (now == 0) {
			dNow = 1d;
		} else {
			dNow = now;
		}

		double result = 0d;

		if (pre != 0 && now != 0) {
			result = Double.valueOf(form.format(((dNow - dPre) / dPre) * 100d));
		}

		return result;
	}
	
}
