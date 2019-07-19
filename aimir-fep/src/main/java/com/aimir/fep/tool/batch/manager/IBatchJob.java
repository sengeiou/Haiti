/**
 * 
 */
package com.aimir.fep.tool.batch.manager;

import com.aimir.constants.CommonConstants.ResultStatus;

/**
 * @author simhanger
 *
 */
public interface IBatchJob extends Runnable {
	public String getExecutorName();
	public String getName();

	public void printResult(String title, ResultStatus status, String desc);
	
	/*
	 * 중복 체크를 위해서 구현해야함.
	 */
	@Override
	boolean equals(Object obj);
}
