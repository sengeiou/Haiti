/**
 * EnergySavingGoalNotification.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.mvm.BillingDayEMDao;
import com.aimir.dao.mvm.BillingDayGMDao;
import com.aimir.dao.mvm.BillingDayWMDao;
import com.aimir.dao.mvm.BillingMonthEMDao;
import com.aimir.dao.mvm.BillingMonthGMDao;
import com.aimir.dao.mvm.BillingMonthWMDao;
import com.aimir.dao.system.energySavingGoal.EnergySavingTargetDao;
import com.aimir.dao.system.membership.OperatorContractDao;
import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.mvm.BillingDayGM;
import com.aimir.model.mvm.BillingDayWM;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.mvm.BillingMonthGM;
import com.aimir.model.mvm.BillingMonthWM;
import com.aimir.model.system.Contract;
import com.aimir.model.system.EnergySavingTarget;
import com.aimir.model.system.OperatorContract;

/**
 * EnergySavingGoalNotification.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 6. 22.   v1.0       김상연         목표 설정 통보 로직
 *
 */
@Service
public class EnergySavingGoalNotification {
	

	private static OperatorContractDao operatorContractDao;

	private static EnergySavingTargetDao energySavingTargetDao;

	private static BillingMonthEMDao billingMonthEMDao;

	private static BillingMonthGMDao billingMonthGMDao;

	private static BillingMonthWMDao billingMonthWMDao;

	private static BillingDayEMDao billingDayEMDao;

	private static BillingDayGMDao billingDayGMDao;

	private static BillingDayWMDao billingDayWMDao;

	@Autowired
	public void setOperatorContractDao(
			OperatorContractDao operatorContractDao) {
		EnergySavingGoalNotification.operatorContractDao = operatorContractDao;
	}


	@Autowired
	public void setEnergySavingTargetDao(
			EnergySavingTargetDao energySavingTargetDao) {
		EnergySavingGoalNotification.energySavingTargetDao = energySavingTargetDao;
	}

	@Autowired
	public void setBillingMonthEMDao(BillingMonthEMDao billingMonthEMDao) {
		EnergySavingGoalNotification.billingMonthEMDao = billingMonthEMDao;
	}

	@Autowired
	public void setBillingMonthGMDao(BillingMonthGMDao billingMonthGMDao) {
		EnergySavingGoalNotification.billingMonthGMDao = billingMonthGMDao;
	}

	@Autowired
	public void setBillingMonthWMDao(BillingMonthWMDao billingMonthWMDao) {
		EnergySavingGoalNotification.billingMonthWMDao = billingMonthWMDao;
	}

	@Autowired
	public void setBillingDayEMDao(BillingDayEMDao billingDayEMDao) {
		EnergySavingGoalNotification.billingDayEMDao = billingDayEMDao;
	}

	@Autowired
	public void setBillingDayGMDao(BillingDayGMDao billingDayGMDao) {
		EnergySavingGoalNotification.billingDayGMDao = billingDayGMDao;
	}

	@Autowired
	public void setBillingDayWMDao(BillingDayWMDao billingDayWMDao) {
		EnergySavingGoalNotification.billingDayWMDao = billingDayWMDao;
	}

	/**
	 * method name : Notification
	 * method Desc : 목표 설정 통보 로직
	 *
	 * @param contract
	 * @param basicDay
	 * @param bill
	 */
	public static void Notification(Contract contract, String basicDay) {
		
		if (null == contract) {
			
			return;
		}
		
		if (null == basicDay) {
			
			return;
		}
		
		int billDate = contract.getBillDate() == null ? 1 : contract.getBillDate();
		int year = Integer.parseInt(basicDay.substring(0, 4));
		int month = Integer.parseInt(basicDay.substring(4, 6)) - 1;
		int date = Integer.parseInt(basicDay.substring(6, 8));
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		Calendar calendar =  Calendar.getInstance();
				
		calendar.set(year, billDate > date ? month - 1 : month, billDate);
		
        String billDay = formatter.format(calendar.getTime());

        List<Integer> dateList = new ArrayList<Integer>();
        
        List<Integer> date2List = new ArrayList<Integer>();
        
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        
        for (int i = 0; i < 4; i++) {
       
        	calendar.add(Calendar.DAY_OF_YEAR, 7);
        	
        	dateList.add(calendar.get(Calendar.DAY_OF_MONTH));
        	
        	if (1 == i % 2) {
        		
        		date2List.add(calendar.get(Calendar.DAY_OF_MONTH));
        	}
        }
        
		calendar.set(year, month, billDate - 1);
		
        String preDay = formatter.format(calendar.getTime());
		
		String serviceType = contract.getServiceTypeCode().getCode();
		
		OperatorContract operatorContract = new OperatorContract();
		
		operatorContract.setContract(contract);
		operatorContract.setContractStatus(1);
		
		List<OperatorContract> operatorContracts = operatorContractDao.getOperatorContract(operatorContract);
		
		for (OperatorContract forOperatorContract : operatorContracts) {
			
			EnergySavingTarget energySavingTarget = new EnergySavingTarget();
			
			energySavingTarget.setCreateDate(basicDay);
			energySavingTarget.setOperatorContract(forOperatorContract);
			
			List<EnergySavingTarget> energySavingTargets = 
				energySavingTargetDao.getEnergySavingTarget(energySavingTarget, null, null);
			
			if (0 < energySavingTargets.size()) {
				
				energySavingTarget = energySavingTargets.get(0);
			} else {
				
				continue;
			}
			
			com.aimir.model.system.Notification notification = energySavingTarget.getNotification();
			
			if (null == notification) {
				
				continue;
			}
			
			String name = forOperatorContract.getOperator().getName();
			String title = null;
			String body = null;
			
			// 통보 주기(전월 에너지 절감 목표 실적)
			if (true == notification.getPeriod_1()){
				
				if (billDate == date) {
					
					Double bill = null;
					
					if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
						
						BillingMonthEM billingMonthEM = new BillingMonthEM();
						
						billingMonthEM.setContract(contract);
						billingMonthEM.setYyyymmdd(basicDay);
						
						List<BillingMonthEM> billingMonthEMs = billingMonthEMDao.getBillingMonthEMs(billingMonthEM, null, null);
						
						if (0 < billingMonthEMs.size()) {
							
							bill = billingMonthEMs.get(0).getBill();
						}
					} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
						
						BillingMonthGM billingMonthGM = new BillingMonthGM();
						
						billingMonthGM.setContract(contract);
						billingMonthGM.setYyyymmdd(basicDay);
						
						List<BillingMonthGM> billingMonthGMs = billingMonthGMDao.getBillingMonthGMs(billingMonthGM, null, null);
						
						if (0 < billingMonthGMs.size()) {
							
							bill = billingMonthGMs.get(0).getBill();
						}
					} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
						
						BillingMonthWM billingMonthWM = new BillingMonthWM();
						
						billingMonthWM.setContract(contract);
						billingMonthWM.setYyyymmdd(basicDay);
						
						List<BillingMonthWM> billingMonthWMs = billingMonthWMDao.getBillingMonthWMs(billingMonthWM, null, null);
						
						if (0 < billingMonthWMs.size()) {
							
							bill = billingMonthWMs.get(0).getBill();
						}
					}
					
					if (null != bill) {
						
						/* 통보 프레임 웍 후 메일 보내기 완성 해야할 부분 시작 */
						title = name + "에게";
						body = "전월 사용 금액은 " + bill + "입니다.";
						
						testMail(title, body);
						/* 통보 프레임 웍 후 메일 보내기 완성 해야할 부분 끝 */
					}
				}
			}
			
			Double bill = 0.0;
			Double preBill = 0.0;
			Double savingTarget = energySavingTarget.getSavingTarget();
			
			if (MeterType.EnergyMeter.getServiceType().equals(serviceType)) {
				
				BillingDayEM billingDayEM = new BillingDayEM();
				
				billingDayEM.setContract(contract);
				
				List<BillingDayEM> billingDayEMs = billingDayEMDao.getBillingDayEMs(billingDayEM, billDay, basicDay);
				
				for (BillingDayEM forBillingDayEM : billingDayEMs) {
					
					bill += (null == forBillingDayEM.getBill() ? 0.0 : forBillingDayEM.getBill());
				};
				
				billingDayEMs = billingDayEMDao.getBillingDayEMs(billingDayEM, billDay, preDay);
				
				for (BillingDayEM forBillingDayEM : billingDayEMs) {
					
					preBill += (null == forBillingDayEM.getBill() ? 0.0 : forBillingDayEM.getBill());
				};
			} else if (MeterType.GasMeter.getServiceType().equals(serviceType)) {
				
				BillingDayGM billingDayGM = new BillingDayGM();
				
				billingDayGM.setContract(contract);
				
				List<BillingDayGM> billingDayGMs = billingDayGMDao.getBillingDayGMs(billingDayGM, billDay, basicDay);
				
				for (BillingDayGM forBillingDayGM : billingDayGMs) {
					
					bill += (null == forBillingDayGM.getBill() ? 0.0 : forBillingDayGM.getBill());
				};
				
				billingDayGMs = billingDayGMDao.getBillingDayGMs(billingDayGM, billDay, preDay);
				
				for (BillingDayGM forBillingDayGM : billingDayGMs) {
					
					preBill += (null == forBillingDayGM.getBill() ? 0.0 : forBillingDayGM.getBill());
				};
			} else if (MeterType.WaterMeter.getServiceType().equals(serviceType)) {
				
				BillingDayWM billingDayWM = new BillingDayWM();
				
				billingDayWM.setContract(contract);
				
				List<BillingDayWM> billingDayWMs = billingDayWMDao.getBillingDayWMs(billingDayWM, billDay, basicDay);
				
				for (BillingDayWM forBillingDayWM : billingDayWMs) {
					
					bill += (null == forBillingDayWM.getBill() ? 0.0 : forBillingDayWM.getBill());
				};
				
				billingDayWMs = billingDayWMDao.getBillingDayWMs(billingDayWM, billDay, preDay);
				
				for (BillingDayWM forBillingDayWM : billingDayWMs) {
					
					preBill += (null == forBillingDayWM.getBill() ? 0.0 : forBillingDayWM.getBill());
				};
			}
			
			// 통보 주기(설정 목표 ${conditionValue} 초과시 통보)
			if (true == notification.getPeriod_2()){
				
				if (null != savingTarget && 0 != savingTarget) {
					
					int conditionValue = notification.getConditionValue();
					
					if (conditionValue > (preBill / savingTarget * 100) && conditionValue <= (bill / savingTarget * 100)) {
						
						/* 통보 프레임 웍 후 메일 보내기 완성 해야할 부분 시작 */
						title = name + "에게";
						body = "현재 사용 금액은 " + bill + "입니다.";
						
						testMail(title, body);
						/* 통보 프레임 웍 후 메일 보내기 완성 해야할 부분 끝 */
					}
				}
			}
			
			// 통보 주기(설정 목표 초과시 통보)
			if (true == notification.getPeriod_3()){
				
				if (null != savingTarget && 0 != savingTarget) {
					
					if (preBill < savingTarget && bill >= savingTarget) {
						
						/* 통보 프레임 웍 후 메일 보내기 완성 해야할 부분 시작 */
						title = name + "에게";
						body = "현재 사용 금액은 " + bill + "입니다.";
						
						testMail(title, body);
						/* 통보 프레임 웍 후 메일 보내기 완성 해야할 부분 끝 */
					}
				}
			}
			
			// 통보 주기(주1회 에너지 절감 목표 실적 통보)
			if (true == notification.getPeriod_4()){
				
				if (dateList.contains(date)) {
					
					/* 통보 프레임 웍 후 메일 보내기 완성 해야할 부분 시작 */
					title = name + "에게";
					body = "현재 사용 금액은 " + bill + "입니다.";
					
					testMail(title, body);
					/* 통보 프레임 웍 후 메일 보내기 완성 해야할 부분 끝 */
				}
			}
			
			// 통보 주기(2주1회 에너지 절감 목표 실적 통보)
			if (true == notification.getPeriod_5()){
				
				if (date2List.contains(date)) {
					
					/* 통보 프레임 웍 후 메일 보내기 완성 해야할 부분 시작 */
					title = name + "에게";
					body = "현재 사용 금액은 " + bill + "입니다.";
					
					testMail(title, body);
					/* 통보 프레임 웍 후 메일 보내기 완성 해야할 부분 끝 */
				}
			}
		}
	}

	/**
	 * method name : testMail
	 * method Desc : 메일 보내기 더미 (추후 통보 프레임 워크 생성 후 삭제하고 사용된 부분을 다 변경해야 함)
	 *
	 */
	private static void testMail(String title, String body) {
		
	}
	
}
