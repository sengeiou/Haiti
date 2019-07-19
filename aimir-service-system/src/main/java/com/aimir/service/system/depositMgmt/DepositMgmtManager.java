package com.aimir.service.system.depositMgmt;

import java.util.Map;

import com.aimir.model.prepayment.DepositHistory;


public interface DepositMgmtManager {

	/**
	 * @MethodName getOperatorByLoginIdAndName
	 * @Date 2013. 6. 27.
	 * @param page grid에 출력시 페이지
	 * @param limit grid에 출력시 페이지 한도 건수
	 * @param supplierId 공급사 id
	 * @param loginId vendor 계정
	 * @param name 사용자명 
	 * @return 
	 * @Modified
	 * @Description loginId, name의 조건에 맞는 Operator 목록을 조회한다 
	 */
	public Map<String, Object> getOperatorByLoginIdAndName(int page, int limit, int supplierId, String loginId, String name);
	
	/**
	 * @MethodName chargeDeposit
	 * @Date 2013. 6. 27.
	 * @param condition {supplierId, vendor, amount, date} 
	 * @return
	 * @Modified
	 * @Description 특정 vendor 게정에 amount만큼 depoisit을 추가한다. 
	 */
	public DepositHistory chargeDeposit(Map<String, Object> condition);
	
	/**
	 * @MethodName getHistoryList
	 * @Date 2013. 7. 23.
	 * @param params
	 * @return
	 * @Modified
	 * @Description 특정 조건 폼에 맞게 deposit이력을 조회한다.
	 */
	@Deprecated
	public Map<String, Object> getHistoryList(Map<String, Object> params);

    /**
     * method name : getDepositHistoryList<b/>
     * method Desc :
     *
     * @param params
     * @return
     */
    public Map<String, Object> getDepositHistoryList(Map<String, Object> params);
    
    /**
     * 
     * method name : depositCancelTransaction<b/>
     * method Desc :
     * 
     * @param depositHistoryId
     * @param loginId
     * @Description 예치금내역 취소 기능
     *
     */
    public String depositCancelTransaction(Integer depositHistoryId, String loginId);
    
}