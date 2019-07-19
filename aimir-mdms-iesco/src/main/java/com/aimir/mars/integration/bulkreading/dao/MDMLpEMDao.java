package com.aimir.mars.integration.bulkreading.dao;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.mars.integration.bulkreading.model.MDMLpEM;

public interface MDMLpEMDao extends GenericDao<MDMLpEM, Integer> {
	
	public void delete(Map<String, Object> condition);
	
	public void updateTransferDate(int batchId);
	
	public void updateInitTransferDate(List<MDMLpEM> mdmLpEMList);
	
	public List<MDMLpEM> select(Map<String, Object> condition);
}