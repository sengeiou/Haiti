/**
 * BillingMonthGMDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.BillingMonthGM;
import com.aimir.model.mvm.BillingMonthWM;
import com.aimir.model.system.Contract;

/**
 * BillingMonthGMDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 17.   v1.0       김상연         BillingMonthGM 조회 - 조건 (BillingMonthGM)
 * 2011. 5. 17.   v1.1       김상연         계약 회사 평균 조회
 * 2011. 6. 09.   v1.2       김상연         해당 계약 최고 사용 금액 조회
 * 2011. 6. 27.   v1.3       김상연        사용 평균 비용 조회
 *
 */
public interface BillingMonthGMDao extends GenericDao<BillingMonthGM, Integer>{

	/**
	 * method name : getBillingMonthGMs
	 * method Desc : BillingMonthGM 조회 - 조건 (BillingMonthGM)
	 *
	 * @param billingMonthGM
	 * @param startDay
	 * @param finishDay
	 * @return
	 */
	List<BillingMonthGM> getBillingMonthGMs(BillingMonthGM billingMonthGM, String startDay, String finishDay);

	/**
	 * method name : getAverageUsage
	 * method Desc : 계약 회사 평균 조회
	 *
	 * @param billingMonthGM
	 * @return
	 */
	Double getAverageUsage(BillingMonthGM billingMonthGM);

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
	Double getAverageBill(Contract contract, String yyyymmdd);

	/**
	 * method name : getBillingMonthGMsAvg
	 * method Desc : 같은 지역의 평균 월 요금 취득
	 *
	 * @param billingMonthGM
	 * @param fromDay
	 * @param toDay
	 * @return
	 */
	public List<Object> getBillingMonthGMsAvg(BillingMonthGM billingMonthGM, String fromDay, String toDay);
    
}
