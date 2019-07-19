package com.aimir.service.system.impl;

import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;

import com.aimir.dao.system.TagDao;
import com.aimir.model.system.Gadget;
import com.aimir.model.system.Tag;
import com.aimir.service.system.TagManager;

@WebService(endpointInterface = "com.aimir.service.system.TagManager")
@Service(value = "tagManager")
@RemotingDestination
public class TagManagerImpl implements TagManager {

	@Autowired
	TagDao dao;
	
	public List<Gadget> searchGadgetByTag(String tag, int roleId) {
		return dao.searchGadgetByTag(tag, roleId);
	}

	public List<Tag> getTags(int roleId) {
		return dao.getTags(roleId);
	}

}
