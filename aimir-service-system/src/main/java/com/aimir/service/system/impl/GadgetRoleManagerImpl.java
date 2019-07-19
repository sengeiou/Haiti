package com.aimir.service.system.impl;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.GadgetRoleDao;
import com.aimir.model.system.GadgetRole;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.GadgetRoleManager;

@WebService(endpointInterface = "com.aimir.service.system.GadgetRoleManager")
@Service(value = "gadgetRoleManager")
@RemotingDestination
@Transactional
public class GadgetRoleManagerImpl implements GadgetRoleManager {

	@Autowired
	GadgetRoleDao dao;
	
	public List<GadgetRole> getGadgetRoles() {
		return dao.getAll();
	}
	
	public List<Map<String, Object>> getGadgetRolesList(Map<String, Object> params) {
		return dao.getGadgetRolesList(params);
	}

    public void add(GadgetRole gadgetRole) {
        dao.add(gadgetRole);
    }

    /**
     * method name : addGadgetRoles<b/>
     * method Desc : User Management 에서 그룹 등록 시 허용된 가젯을 저장한다.
     *
     * @param gadgetRoles
     */
    public void addGadgetRoles(List<GadgetRole> gadgetRoles) {
        
        if (gadgetRoles != null) {
            for (GadgetRole entity : gadgetRoles) {
                dao.add(entity);
            }
        }
    }
}