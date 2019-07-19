package com.aimir.service.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.ContractChangeLogManager;
import com.aimir.util.Condition;
import com.aimir.util.ReflectionUtils;
import com.aimir.util.TimeLocaleUtil;

@WebService(endpointInterface = "com.aimir.service.system.ContractChangeLogManager")
@Service(value = "contractChangeLogManager")
@Transactional
@RemotingDestination
public class ContractChangeLogManagerImpl implements ContractChangeLogManager{


    @Autowired
    ContractChangeLogDao dao;

    @Autowired
    SupplierDao supplierDao;

    public void updateContractChangeLog(ContractChangeLog contractChangeLog) {
        dao.update(contractChangeLog);
    }

    public void addContractChangeLog(ContractChangeLog contractChangeLog) {
        dao.add(contractChangeLog);
    }

    public ContractChangeLog getContractChangeLog(Long id) {
        return dao.get(id);
    }

    public List<ContractChangeLog> getContractChangeLogByListCondition(
            Set<Condition> set) {
        return dao.getContractChangeLogByListCondition(set);
    }

    public List<Map<String,Object>> getContractChangeLogByListCondition(
            Set<Condition> set, String supplierId) {

        List<Map<String, Object>> list = ReflectionUtils.getDefineListToMapList(dao.getContractChangeLogByListCondition(set));

        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));

        for(Map<String, Object> data: list) {
            data.put("startDatetime", TimeLocaleUtil.getLocaleDate((String)data.get("startDatetime"), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
        }

        return list;
    }

    public List<Object> getContractChangeLogCountByListCondition(
            Set<Condition> set) {
        return dao.getContractChangeLogCountByListCondition(set);
    }

    public void contractLogDelete(int contractId) {
        dao.contractLogDelete(contractId);
    }

    public void contractLogAllDelete(int customerId) {
        dao.contractLogAllDelete(customerId);
    }

    /**
     * method name : getContractChangeLogList<b/>
     * method Desc : Customer Contract Management 맥스 가젯에서 Contract ChangeLog 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    public List<Map<String, Object>> getContractChangeLogList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = null;
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();

        result = dao.getContractChangeLogList(conditionMap, false);

        for (Map<String, Object> map : result) {
            map.put("startDatetime", TimeLocaleUtil.getLocaleDate((String)map.get("startDatetime"), lang, country));
            map.put("writeDatetime", TimeLocaleUtil.getLocaleDate((String)map.get("writeDatetime"), lang, country));
        }
        return result;
    }

    /**
     * method name : getContractChangeLogListTotalCount<b/>
     * method Desc : Customer Contract Management 맥스 가젯에서 Contract ChangeLog Total Count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    public Integer getContractChangeLogListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = dao.getContractChangeLogList(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }
}