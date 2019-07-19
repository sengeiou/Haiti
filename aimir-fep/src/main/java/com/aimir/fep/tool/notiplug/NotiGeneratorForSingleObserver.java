/**
 * 
 */
package com.aimir.fep.tool.notiplug;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simhanger
 *
 */
public abstract class NotiGeneratorForSingleObserver {
	private static Logger logger = LoggerFactory.getLogger(NotiGeneratorForSingleObserver.class);

	private NotiObserver observer;
	private String observerName;

	public abstract String getNotiGeneratorName();

	public void addObserver(NotiObserver observer) {
		String obName = observer.getNotiObserverName();
		if (obName == null || obName.equals("")) {
			obName = String.valueOf(observer.hashCode());
		}
		
		logger.debug("### [NOTI] ### Add~! Observer = {}", obName);
		
		this.observerName = obName;
		this.observer = observer;
	}

	public void notifyToObserver(Map<?, ?> params) {
		String generatorName = getNotiGeneratorName();
		if (generatorName == null || generatorName.equals("")) {
			generatorName = String.valueOf(hashCode());
		}
		logger.debug("### [NOTI] ### Notify~! Generator={} ==> Observer={}, Params={}", generatorName, observerName, (params == null ? "Parameter is null" : params.toString()));
		observer.observerNotify(generatorName, params);
	}
}
