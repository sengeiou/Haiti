/**
 * 
 */
package com.aimir.fep.protocol.nip.client.batch.excutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simhanger
 *
 */
@Deprecated
public class BatchRejectedExecutionHandler implements RejectedExecutionHandler {
	private static Logger logger = LoggerFactory.getLogger(BatchRejectedExecutionHandler.class);
	private List<String> rejectList;

	public BatchRejectedExecutionHandler() {
		rejectList = new ArrayList<String>();
	}

	@Override
	public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
		String r = ((IBatchRunnable) runnable).getName();
		logger.warn("Rejected Execution - JobName = {}", r);
		
		rejectList.add(r);
	}

	public int getSize() {
		return rejectList.size();
	}

	public List<String> getList() {
		return rejectList;
	}
}
