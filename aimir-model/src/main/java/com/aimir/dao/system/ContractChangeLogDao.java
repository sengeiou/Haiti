package com.aimir.dao.system;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.util.Condition;

public interface ContractChangeLogDao extends GenericDao<ContractChangeLog, Long> {

    /**
     * method name : getContractChangeLogCountByListCondition
     * method Desc : 조회조건에 해당하는 계약변경로그 리스트의 카운트를 리턴한다.
     *
     * @param set
     * @return List of Object (total Count)
     */
    public List<Object> getContractChangeLogCountByListCondition(Set<Condition> set);

    /**
     * method name : getContractChangeLogByListCondition
     * method Desc : 조회조건에 해당하는 계약변경로그 리스트를 리턴한다.
     *
     * @param set
     * @return List of ContractChangeLog @see com.aimir.model.system.ContractChangeLog
     */
    public List<ContractChangeLog> getContractChangeLogByListCondition(Set<Condition> set);

    /**
     * method name : contractLogDelete
     * method Desc : 계약정보 아이디에 해당하는 계약정보 변경 로그 를 삭제한다.
     *
     * @param contractId Contract.id
     */
    public void contractLogDelete(int contractId);

    /**
     * method name : contractLogAllDelete
     * method Desc : 고객 아이디에 해당하는 계약정보 변경 로그 전체를 삭제한다.
     *
     * @param customerId Customer.id
     */
    public void contractLogAllDelete(int customerId);

    /**
     * method name : getContractChangeLogList<b/>
     * method Desc : Customer Contract Management 맥스 가젯에서 Contract ChangeLog 를 조회한다.
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    public List<Map<String, Object>> getContractChangeLogList(Map<String, Object> conditionMap, boolean isCount);
}