package com.aimir.util;

/**
 * NURI Telecom <br>
 * AIMIR Framwork - StopWatch <br>
 * JDF Framework Based(javaservice.net) <br>
 * 
 * @author Jeong Hun(lucky@nuritelecom.com)
 */
public class StopWatch {
	long start=0;
	long current=0;

	/**
	 * StopWatch constructor comment.
	 */
	public StopWatch() {
		reset();
	}

	/**
	 * @return long
	 */
	public long getElapsed() {
		long now=TimeUtil.getCurrentLongTime();
		long elapsed=(now-current);
		current=now;
		return elapsed;
	}

	/**
	 * @return long
	 */
	public long getTotalElapsed() {
		current=TimeUtil.getCurrentLongTime();
		return (current-start);
	}

	public void reset() {
		start=TimeUtil.getCurrentLongTime();
		current=start;
	}
}