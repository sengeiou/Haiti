/**
 * OperatorDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.model.system.Operator;
import com.aimir.util.Condition;

/**
 * OperatorDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 6.   v1.0       김상연         주민등록번호 정보 추출
 * 2011. 4. 14.  v1.1       김상연         Operator 조회 (조건 : Operator)
 *
 */
@Repository(value = "operatorDao")
public class OperatorDaoImpl extends AbstractJpaDao<Operator, Integer> implements OperatorDao {

    Log logger = LogFactory.getLog(OperatorDaoImpl.class);
    
	public OperatorDaoImpl() {
		super(Operator.class);
	}

    @Override
    public boolean checkOperator(int userId, String pw) {
        boolean result = false;

        Query query = getEntityManager().createQuery("from Operator o where o.loginId = :userId");
        query.setParameter("userId", userId);

        Operator operator = (Operator) query.getResultList();

        if (operator != null) {
            if (operator.getPassword().equals(pw))
                result = true;
        }
        return false;
    }

    @Override
    public Operator getOperatorByLoginId(String loginId) {
        logger.debug("loginId : " + loginId);
        Query query = getEntityManager().createQuery("FROM Operator o WHERE o.loginId = :loginId");
        query.setParameter("loginId", loginId);

        List<Operator> list = query.getResultList();

        if (list != null && list.size() > 0)
            return (Operator) list.get(0);
        else
            return null;
    }

    @Override
    @Deprecated
    public List<Operator> getOperatorsByRole(Integer roleId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getOperatorListByRole(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Operator> getOperatorsHaveNoSupplier() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Operator> getOperatorsHaveNoRole(Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean checkDuplicateLoginId(String loginId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Operator> getOperators(int page, int count, int roleId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer count(int roleId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getLoginLogGrid(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<List<HashMap<String, Object>>> getLoginLogGrid2(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Operator getOperatorById(Integer operatorId) {
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
    public Operator getOperatorByPucNumber(String pucNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Operator> getOperatorByOperator(Operator operator) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getOperatorListByRoleType(
            Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int chargeDeposit(String vendorId, Double amount) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Operator> getOperatorByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Operator> getVendorByLoginIdAndName(int page, int limit,
            String loginId, String name, Integer supplierId,
            String supplierName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getVendorCountByLoginIdAndName(String loginId, String name,
            Integer supplierId, String supplierName) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Map<String, Object>> getLoginId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Operator> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }
}