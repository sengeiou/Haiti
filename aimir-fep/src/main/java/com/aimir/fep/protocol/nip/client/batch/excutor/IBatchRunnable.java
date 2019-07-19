/**
 * 
 */
package com.aimir.fep.protocol.nip.client.batch.excutor;

import com.aimir.constants.CommonConstants.ResultStatus;

/**
 * @author simhanger
 *
 */
@Deprecated
public interface IBatchRunnable extends Runnable {
	public String getName();

	public void printResult(String title, ResultStatus status, String desc);

	/*
	 * 중복 체크를 위해서 구현해야함.
	 */
	@Override
	boolean equals(Object obj);
}
