package com.aimir.dao.integration;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.integration.WSMeterConfigOBIS;

public interface WSMeterConfigOBISDao extends GenericDao< WSMeterConfigOBIS, Long> {
    public WSMeterConfigOBIS get(String userId, String obisCode, String classId, String attributeNo);
    public List<WSMeterConfigOBIS> getMeterConfigOBISList(String userId);

}
