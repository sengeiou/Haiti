package com.aimir.mars.integration.bulkreading.dao;

import com.aimir.dao.GenericDao;
import com.aimir.mars.integration.bulkreading.model.MDMBatchLog;

public interface MDMBatchDataDao extends GenericDao<MDMBatchLog, Integer> {
	
	public void procBatchLpEM() throws Exception;
	
	public void procBatchBillingDayEM() throws Exception;
	
	public void procBatchBillingMonthEM() throws Exception;
	
	public void procBatchMetereventLog() throws Exception;
	
	public void procBatchDeleteData() throws Exception;
}