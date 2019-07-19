package com.aimir.mars.integration.bulkreading.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.mars.integration.bulkreading.dao.MDMSDao;


@Service
@Transactional
public class MDMSService {
	
	private static final Logger log = LoggerFactory.getLogger(MDMSService.class);
		
	public MDMSHelper mdmsHelper;
	
	@Autowired
	MDMSDao mdmsDao;
	
	@Transactional
    public List<Map<String, Object>> getMDMSStatistics(Map<String, Object> params) throws Exception
    {	
		return mdmsDao.getMDMSStatistics(params);
    }
	
	@Transactional
    public Map<String, Object> getMDMSBatchList(Map<String, Object> params) throws Exception
    {	
		Map<String, Object> result = new HashMap<String, Object>();
		
		int total = mdmsDao.getMDMSBatchCount(params);
		List<Map<String, Object>> list = mdmsDao.getMDMSBatchList(params);
		
		result.put("totalCount", total);
		result.put("resultGrid", list);
		
		return result;
    }
	
	@Transactional
    public Map<String, Object> getMDMSLPList(Map<String, Object> params) throws Exception
    {	
		Map<String, Object> result = new HashMap<String, Object>();
		
		int total = mdmsDao.getMDMSLPCount(params);
		List<Map<String, Object>> list = mdmsDao.getMDMSLPList(params);
		
		result.put("totalCount", total);
		result.put("resultGrid", list);
		
		return result;
    }
	
	@Transactional
    public Map<String, Object> getMDMSDailyList(Map<String, Object> params) throws Exception
    {	
		Map<String, Object> result = new HashMap<String, Object>();
		
		int total = mdmsDao.getMDMSDailyCount(params);
		List<Map<String, Object>> list = mdmsDao.getMDMSDailyList(params);
		
		result.put("totalCount", total);
		result.put("resultGrid", list);
		
		return result;
    }
	
	@Transactional
    public Map<String, Object> getMDMSMonthlyList(Map<String, Object> params) throws Exception
    {	
		Map<String, Object> result = new HashMap<String, Object>();
		
		int total = mdmsDao.getMDMSMonthlyCount(params);
		List<Map<String, Object>> list = mdmsDao.getMDMSMonthlyList(params);
		
		result.put("totalCount", total);
		result.put("resultGrid", list);
		
		return result;
    }
	
	@Transactional
    public Map<String, Object> getMDMSEventList(Map<String, Object> params) throws Exception
    {	
		Map<String, Object> result = new HashMap<String, Object>();
		
		int total = mdmsDao.getMDMSEventCount(params);
		List<Map<String, Object>> list = mdmsDao.getMDMSEventList(params);
		
		result.put("totalCount", total);
		result.put("resultGrid", list);
		
		return result;
    }
		
}