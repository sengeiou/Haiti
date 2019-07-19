/**
 * 
 */
package com.aimir.fep.tool.notiplug;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simhanger
 *
 */
public abstract class NotiGeneratorForMultiObserver {
	private static Logger logger = LoggerFactory.getLogger(NotiGeneratorForMultiObserver.class);

	private Map<String, NotiObserver> observers = new HashMap<String, NotiObserver>();

	public abstract String getNotiGeneratorName();

	public void addObserver(NotiObserver observer) {
		String observerName = observer.getNotiObserverName();
		if (observerName == null || observerName.equals("")) {
			observerName = String.valueOf(observer.hashCode());
		}
		addObserver(observerName, observer);
	}

	public void addObserver(String observerName, NotiObserver observer) {
		logger.debug("[Noti] Add~! Observer = {}", observerName);
		observers.put(observerName, observer);
	}

	public void deleteObserver(NotiObserver observer) {
		String observerName = observer.getNotiObserverName();
		if (observerName == null || observerName.equals("")) {
			observerName = String.valueOf(observer.hashCode());
		}
		deleteObserver(observerName);
	}

	public void deleteObserver(String observerName) {
		logger.debug("[Noti] Delete~! Observer = {}", observerName);
		observers.remove(observerName);
	}

	public void notifyToObserver(NotiGeneratorForMultiObserver notiGenerator, String observerName, Map<?, ?> params) {
		if (observers.containsKey(observerName)) {
			logger.debug("[Noti] Notify~! Generator={} ==> ObserverName={}, Params={}"
					, notiGenerator.getNotiGeneratorName(), observerName, params.toString());
			observers.get(observerName).observerNotify(notiGenerator.getNotiGeneratorName(), params);
		} else {
			logger.error("Can't find target Observer. Generator={} ==> ObserverName={}, Params={}"
					, notiGenerator.getNotiGeneratorName(), observerName, params.toString());
		}
	}

	public void notifyToObserverAll(NotiGeneratorForMultiObserver notiGenerator, Map<?, ?> params) {
		Iterator<String> it = observers.keySet().iterator();
		while (it.hasNext()) {
			notifyToObserver(notiGenerator, it.next(), params);
		}
	}
}
