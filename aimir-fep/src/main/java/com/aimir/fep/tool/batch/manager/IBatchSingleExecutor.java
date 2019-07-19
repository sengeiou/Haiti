/**
 * 
 */
package com.aimir.fep.tool.batch.manager;

/**
 * @file  	: IBatchSingleExecutor.java
 * @author	: simhanger 
 * @date	: 2018. 8. 22.
 * @desc	:
 */
public interface IBatchSingleExecutor extends Runnable {
	public String getExecutorName();

	/*
	 * 중복 체크를 위해서 구현해야함.
	 */
	@Override
	boolean equals(Object obj);

	public void addJob(IBatchJob job);

	public void execute(IBatchJob job);

	public String toStringExecutorInfo();

}
