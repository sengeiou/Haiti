package com.aimir.dao.system;

import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.PeakDemandScenario;
import com.aimir.util.Condition;

/**
 * 
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
public interface PeakDemandScenarioDao extends GenericDao<PeakDemandScenario, Integer> {

	public long totalByConditions(final Set<Condition>condition);
	
}
