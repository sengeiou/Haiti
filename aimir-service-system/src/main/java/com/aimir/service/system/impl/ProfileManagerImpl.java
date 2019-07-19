package com.aimir.service.system.impl;

import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.ProfileDao;
import com.aimir.model.system.Profile;
import com.aimir.service.system.ProfileManager;

@WebService(endpointInterface = "com.aimir.service.system.ProfileManager")
@Service(value="profileManager")
@Transactional
public class ProfileManagerImpl implements ProfileManager {

    @Autowired
    ProfileDao dao;

	public List<Profile> getProfileByUser(Integer userId) {
		return dao.getProfileByUser(userId);
	}

	public void addProfile(Profile profile) {
		Integer userId = profile.getOperator().getId();
		
		if (dao.checkProfileByUser(userId) == false) {
			dao.deleteByUser(userId);
		}
		dao.add(profile);
	}
}
