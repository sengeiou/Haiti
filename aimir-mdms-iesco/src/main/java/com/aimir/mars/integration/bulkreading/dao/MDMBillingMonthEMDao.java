package com.aimir.mars.integration.bulkreading.dao;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.mars.integration.bulkreading.model.MDMBillingMonthEM;

public interface MDMBillingMonthEMDao extends GenericDao<MDMBillingMonthEM, Integer> {
	
	public void delete(Map<String, Object> condition);
	
	public void updateTransferDate(int batchId);
	
	public void updateInitTransferDate(List<MDMBillingMonthEM> mdmList);
}