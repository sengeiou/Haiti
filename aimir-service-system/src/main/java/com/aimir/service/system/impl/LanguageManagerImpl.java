package com.aimir.service.system.impl;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.LanguageDao;
import com.aimir.model.system.Language;
import com.aimir.service.system.LanguageManager;


@WebService(endpointInterface = "com.aimir.service.system.LanguageManager")
@Service(value = "languageManager")
@Transactional
@RemotingDestination
public class LanguageManagerImpl implements LanguageManager {

	Log logger = LogFactory.getLog(CountryManagerImpl.class);
    
    @Autowired
    LanguageDao dao;
    
	public Language get(Integer languageId) {
		return dao.get(languageId);
	}

	public List<Language> getLanguaes() {
		return dao.getAll();
	}

}
