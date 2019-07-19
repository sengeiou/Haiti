package com.aimir.service.device.impl;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.HeadendDao;
import com.aimir.model.device.Headend;
import com.aimir.service.device.HeadendManager;

@WebService(endpointInterface = "com.aimir.service.device.HeadendManager")
@Service(value = "headendManager")
public class HeadendManagerImpl implements HeadendManager {

	@Autowired
	HeadendDao headendDao;
	
	public List<Headend> getLastData() {
		return headendDao.getLastData();
	}

}
