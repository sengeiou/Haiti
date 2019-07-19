/**
 * RoleDaoImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.RoleDao;
import com.aimir.model.system.Gadget;
import com.aimir.model.system.Role;
import com.aimir.util.Condition;


/**
 * RoleDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 12.   v1.0       김상연         Role Id 검색 (이름)
 *
 */
@Repository(value = "roleDao")
public class RoleDaoImpl extends AbstractJpaDao<Role, Integer> implements RoleDao {

    Log logger = LogFactory.getLog(RoleDaoImpl.class);
    
	public RoleDaoImpl() {
		super(Role.class);
	}

    @Override
    public List<Role> getRoleBySupplierId(Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Role> getRoleBySupplierIdForCustomer(Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Gadget> getGadgetList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateGadget(Integer roleId, Integer gadgetId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delGadget(Integer gadgetId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Gadget> gadgetSearch(Integer roleId, String gadgetName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Gadget> gadgetSearchByTag(Integer roleId, String tag) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Gadget> getPermitedGadgets(Integer roleId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Gadget> gadgetAllSearch(Integer roleId, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Gadget> gadgetAllSearchByTag(Integer roleId, String tag) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int nameOverlapCheck(String name) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Gadget> search(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Role getRoleByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Role> getPersistentClass() {
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