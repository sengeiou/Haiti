// INSERT SP-193
package com.aimir.dao.device;


import com.aimir.dao.GenericDao;
import com.aimir.model.device.Threshold;

public interface ThresholdDao extends GenericDao<Threshold, Integer> {
	public Threshold getThresholdByname(String name);
	

}
