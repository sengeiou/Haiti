package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.model.system.Customer;
import com.aimir.util.Condition;

@Repository(value = "customerDao")
public class CustomerDaoImpl extends AbstractJpaDao<Customer, Integer> implements CustomerDao {
		
    Log logger = LogFactory.getLog(CustomerDaoImpl.class);
    
    public CustomerDaoImpl() {
        super(Customer.class);
    }

    @Override
    public List<Customer> getCustomersByName(String[] name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Customer> getCustomersByCustomerNo(String[] customerNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Customer getCustomersByLoginId(String loginId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int idOverlapCheck(String customerNo) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int loginIdOverlapCheck(String loginId, String customerNo) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Map<String, String>> checkCustomerNoLoginMapping(
            String customerNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Customer> customerSearchList(Set<Condition> set) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int customerSearchListCount(String customerNo, String name) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Integer getTotalCustomer(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Customer> getDemandResponseCustomerList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getCustomerCount(Map<String, String> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getNextId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getCustomerListByRole(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Customer> getPersistentClass() {
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
