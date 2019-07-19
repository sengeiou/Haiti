package com.aimir.dao.system;

import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.PeakDemandSetting;
import com.aimir.util.Condition;

public interface PeakDemandSettingDao extends GenericDao<PeakDemandSetting, Integer> {

	public long totalByConditions(final Set<Condition>condition);

}
