/**
 * 
 */
package com.aimir.fep.tool.batch.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @file  	: BatchRejectedExecutionHandler.java
 * @author	: simhanger 
 * @date	: 2018. 8. 22.
 * @desc	:
 */
public class BatchRejectedExecutionHandler implements RejectedExecutionHandler {
	private static Logger logger = LoggerFactory.getLogger(BatchRejectedExecutionHandler.class);
	
	private List<String> rejectList;

	public BatchRejectedExecutionHandler() {
		rejectList = new ArrayList<String>();
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		String name = "";
		if(r instanceof SingleExecutor) {
			name = ((SingleExecutor) r).getExecutorName();
			logger.warn("[REJECT_ADD] name = " + name);
		}else if(r instanceof IBatchJob) {
			name = ((IBatchJob) r).getName();
			logger.warn("[REJECT_ADD] name = " + name);
		}
		
		rejectList.add(name);
	}

	public int getSize() {
		return rejectList.size();
	}

	public List<String> getList() {
		return rejectList;
	}
}
