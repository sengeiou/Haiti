/**
 * BillingMonthWMDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.BillingMonthWM;
import com.aimir.model.system.Contract;

/**
 * BillingMonthWMDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 17.   v1.0       김상연         BillingMonthWM 조회 - 조건 (BillingMonthWM)
 * 2011. 5. 17.   v1.1       김상연         계약 회사 평균 조회
 * 2011. 6. 09.   v1.2       김상연         해당 계약 최고 사용 금액 조회
 * 2011. 6. 27.   v1.3       김상연        사용 평균 비용 조회
 *
 */
public interface BillingMonthWMDao extends GenericDao<BillingMonthWM, Integer>{

	/**
	 * method name : getAverageUsage
	 * method Desc : 계약 회사 평균 조회
	 *
	 * @param billing
	 * @return
	 */
	Double getAverageUsage(BillingMonthWM billingMonthWM);

	/**
	 * method name : getBillingMonthWMs
	 * method Desc : BillingMonthWM 조회 - 조건 (BillingMonthWM)
	 *
	 * @param billingMonthWM
	 * @param startDay
	 * @param finishDay
	 * @return
	 */
	List<BillingMonthWM> getBillingMonthWMs(BillingMonthWM billingMonthWM, String startDay, String finishDay);

	/**
	 * method name : getMaxBill
	 * method Desc : 해당 계약 최고 사용 금액 조회
	 *
	 * @param contract
	 * @param yyyymmdd
	 * @return
	 */
	Double getMaxBill(Contract contract, String yyyymmdd);

	/**
	 * method name : getAverageBill
	 * method Desc : 사용 평균 비용 조회
	 *
	 * @param contract
	 * @param yyyymmdd
	 * @return
	 */
	public Double getAverageBill(Contract contract, String yyyymmdd);

	/**
	 * method name : getBillingMonthWMsAvg
	 * method Desc : 같은 지역의 평균 월 요금 취득
	 *
	 * @param billingMonthWM
	 * @param fromDay
	 * @param toDay
	 * @return
	 */
	public List<Object> getBillingMonthWMsAvg(BillingMonthWM billingMonthWM, String fromDay, String toDay);
    
    
}
