/**
 * ContractDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.util.Condition;

/**
 * ContractDaoImpl.java Description
 *
 *
 * Date          Version     Author   Description
 * 2011. 4. 11.   v1.0       김상연         Contract List 조회 (조건별)
 *
 */
@Repository(value="contractDao")
public class ContractDaoImpl extends AbstractJpaDao<Contract, Integer> implements ContractDao{

    Log logger = LogFactory.getLog(ContractDaoImpl.class);

    public ContractDaoImpl() {
        super(Contract.class);
    }

    @Override
	@SuppressWarnings("unchecked")
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<Object> getMyEnergy(int customerId, int serviceTypeId) {
        String sql = "SELECT contract.id, contract.contractNumber from Contract contract " +
                "left outer join contract.serviceTypeCode service " +
                "WHERE customer.id = :customerId and serviceType.id = :serviceTypeId ";
        Query query = em.createQuery(sql, Contract.class);
        query.setParameter("customerId", customerId);
        query.setParameter("serviceTypeId", serviceTypeId);
        return query.getResultList();
    }

    @Override
    public List<Object> getContractIdByCustomerNo(String[] customerNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getContractIdByContractNo(String contractNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getContractIdByContractNoLike(String contractNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getContractIdByCustomerName(String customerName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getContractIdByGroup(String group) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getContractIdByTariffIndex(int tariffIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getContractIdWithCustomerName(String[] contractNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Contract> getContractByListCondition(Set<Condition> set) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getContractCountByStatusCode(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getContractCountForToday(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getContractCountByTariffType(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int numberOverlapCheck(String contractNumber) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int meterOverlapCheck(Integer meterId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void contractAllDelete(Integer id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    @Deprecated
    public List<Object[]> getAllCustomerTabData(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getTotalContractCount(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getAllCustomerTabDataTree(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getContractListByCustomer(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public String getAllCustomerTabDataCount(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getContractInfo(int contractId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEMCustomerTabData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List getMeterList(Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public String getMeterListDataCount(Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMeterGridList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEMCustomerTabDataCount(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Contract> getContractByCustomerId(int customerId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Contract> getContractByMeterId(int meterId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object[]> getContractListByMeter(
            Map<String, String> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, String>> getSupplyCapacity(
            Map<String, String> paramMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List<Object> getGroupMember(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Contract> getContractList(Contract contract) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Contract> getPrepaymentContract(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getPrepaymentContractBalanceInfo(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Contract> getBalanceMonitorContract() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Contract> getEmergencyCreditMonitorContract() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getEmergencyCreditContractList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getPrepaymentContractStatusChartData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getPrepaymentContractList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getContractBySicId(Integer codeId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public void updateSendResult(int contractId, String delayDay) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Map<String, Object>> getDeliveryDelayContratInfo(
            int serviceTypeId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Contract> getContractIdByCustomerNo(String customerNo,
            String supplierName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getPrepaymentContract() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getPrepaymentContract(String serviceType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int idOverlapCheck(String contractNo) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    @Deprecated
    public int checkContractedMeterYn(String meterId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int checkContractMeterYn(String meterId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    @Deprecated
    public void updateContractByMeterId(String meterId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Map<String, Object>> getSicContractCountList(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getCustomerListByType(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getPrepaymentChargeContractList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getContractSMSYN(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getContractSMSYNWithGroup(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getContractSMSYNNOTGroup(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getMdsIdFromContractNumber(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Contract> getECGContract() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Contract> getECGContract(List<Integer> locationId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getContractCount(Map<String, String> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateStatus(int contractId, Code code) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateCreditType(int contractId, Code code) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateSmsNumber(int contractId, String msg) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Map<String, Object>> getContractByloginId(String loginId,
            String meterType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Contract> getContractForSAWSPOS(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getPartpayInfoByContractNumber(
            String contractNumber, Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getContractUsageSMSNOTGroup(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getContractUsageSMSGroup(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Contract> getECGContractByNotCalculation(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateCurrentCredit(int contractId, double currentCredit) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateSMSPriod(int contractId, String msgId,
            String lastNotificationDate) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Map<String, Object>> getPrepaidCustomerListForSMS(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getRequestDataForUSSDPOS(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Contract> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public List<Contract> getContract(String payType, String serviceType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Contract> getReqSendSMSList() {
		// TODO Auto-generated method stub
		return null;
	}

	
    
}