package com.aimir.dao.device;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.NetworkInfoLog;
import com.aimir.model.device.NetworkInfoLogPk;

public interface NetworkInfoLogDao extends GenericDao<NetworkInfoLog, NetworkInfoLogPk>{
    public NetworkInfoLog[] list(String command, String startDate, String endDate, int pageNo, int rowCnt)
    throws Exception;
}
