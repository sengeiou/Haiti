package com.aimir.service.system.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;

import com.aimir.dao.system.GadgetDao;
import com.aimir.model.system.Gadget;
import com.aimir.service.system.GadgetManager;
import org.hibernate.SessionFactory;

@WebService(endpointInterface = "com.aimir.service.system.GadgetManager")
@Service(value = "gadgetManager")
@RemotingDestination
public class GadgetManagerImpl implements GadgetManager {
	
    @Autowired
    GadgetDao dao;      
    
    @SuppressWarnings("unchecked")
	public List<Gadget> getGadgets()
    {
    	List<Gadget> gadgetlist=new ArrayList();
    	
    	gadgetlist = dao.getAll();
    	
    	
    	
    	
    	return gadgetlist;
    }
    
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Gadget> getGadgets2(Map<String, Object> conditionMap)
    {
    	List<Gadget> gadgetlist=new ArrayList();
    	
    	//gadgetlist = dao.getAll();
    	
    	gadgetlist= dao.getAllGadgetList(conditionMap);
    	
    	
    	return gadgetlist;
    }

    public Gadget getGadget(Integer gadgetId) {
        return dao.get(gadgetId);
    }

    public void add(Gadget gadget){
    	dao.add(gadget);
    }
    
    public void update(Gadget gadget){
    	Gadget tempGadget = dao.get(gadget.getId());
    	tempGadget.setName(gadget.getName());
    	tempGadget.setDescr(gadget.getDescr());
    	tempGadget.setMiniHeight(gadget.getMiniHeight());
    	tempGadget.setFullHeight(gadget.getFullHeight());
    	
    	dao.codeUpdate(tempGadget);
    }
    
    public void delete(Gadget gadget){
    	dao.delete(gadget);
    }

	public List<Gadget> searchGadgetList(String gadgetName, Integer roleId) {
		return dao.searchGadgetList("%"+gadgetName+"%", roleId);
	}
}
