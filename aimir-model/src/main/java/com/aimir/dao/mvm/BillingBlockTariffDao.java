/**
 * BillingDayEMDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.BillingBlockTariff;
import com.aimir.model.mvm.BillingDayEM;
import com.aimir.model.system.Contract;

/**
 * BillingBlockTariffDao.java Description 
 *
 */
public interface BillingBlockTariffDao extends GenericDao<BillingBlockTariff, Integer>{

	/**
	 * method name : getMaxBill
	 * method Desc : 지정한 일자의 MAX요금을 취득한다.
	 *
	 * @param contract
	 * @param yyyymmdd
	 * @return
	 */
	public Double getMaxBill(Contract contract, String yyyymmdd);

    /**
     * method name : getPrepaymentBillingDayList
     * method Desc : 잔액모니터링 스케줄러에서 조회하는 선불계약별 일별빌링 리스트
     *
     * @param contractId
     * @return
     */
    public List<BillingDayEM> getPrepaymentBillingDayList(Integer contractId);
    
    public Double getAverageBill(Contract contract, String yyyymmdd);

    /**
     * method name : getChargeHistoryBillingList
     * method Desc : 고객 선불관리 화면의 충전 이력 사용전력량을 조회한다.(계산용)
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getChargeHistoryBillingList(Map<String, Object> conditionMap);
    
    /**
     * method name : getBalanceHistoryList
     * method Desc : BalanceHistory 내역을 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getBalanceHistoryList(Map<String, Object> conditionMap, boolean isCount);
    
    
    /**
     * method name : getLastAccumulateBill
     * method Desc : 선불로직 구현시 마지막 누적액을 구하는데 쓰인다.
     * 
     * @param mdevId
     * @return
     */
    public List<Map<String, Object>> getLastAccumulateBill(String mdevId);
    
    public BillingBlockTariff getBillingBlockTariff(Integer contractId, String mdevId, String yyyymmdd, String hhmmss);
    
    public List<Map<String, Object>> getRevertBillingList();
}
