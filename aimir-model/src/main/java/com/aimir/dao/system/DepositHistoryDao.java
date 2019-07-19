package com.aimir.dao.system;

import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.prepayment.DepositHistory;

public interface DepositHistoryDao extends GenericDao<DepositHistory, Integer>{
    /**
     * @MethodName getHistoryList
     * @Date 2013. 10. 25.
     * @param params
     * @return
     * @Modified
     * @Description
     */
    @Deprecated
    public Map<String, Object> getHistoryList(Map<String, Object> params);

    /**
     * method name : getDepositHistoryList<b/>
     * method Desc : Deposit History List 를 조회한다.
     *
     * @param params
     * @return
     */
    public Map<String, Object> getDepositHistoryList(Map<String, Object> params);
    
    /**
     * method name : getRecentDepositId<b/>
     * method Desc : Deposit History List 를 조회한다.
     *
     * @param params
     * @return
     */
    public Integer getRecentDepositId(String vendorId);
    
    /**
     * method name : deleteByPrepaymentLogId<b/>
     * method Desc : prepaymentLogId를 가지로 삭제한다.
     * 
     * @param pId
     * @return
     */
    public void deleteByPrepaymentLogId(long pId);
    public Map<String, Object> getArrearsInfo(Map<String, Object> params);
    
    public Map<String, Object> getDebtInfo(Map<String, Object> params);
    
}
