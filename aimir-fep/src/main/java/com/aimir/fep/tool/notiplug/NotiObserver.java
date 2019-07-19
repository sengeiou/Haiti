/**
 * 
 */
package com.aimir.fep.tool.notiplug;

import java.util.Map;

/**
 * @author simhanger
 *
 */
public interface NotiObserver {
	public abstract void observerNotify(String notiGeneratorName, Map<?, ?> params);

	public abstract String getNotiObserverName();
	
	public abstract Map<?,?> getNotiParams();
}
