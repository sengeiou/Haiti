package com.aimir.dao.integration;

import com.aimir.dao.GenericDao;
import com.aimir.model.integration.WSMeterConfigUser;

public interface WSMeterConfigUserDao extends GenericDao<WSMeterConfigUser, Long> {
    public WSMeterConfigUser get(String userId);
    public String getPassword(String userId);
    
}
