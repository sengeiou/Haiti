package com.aimir.service.system.sla.impl;

import com.aimir.dao.system.GroupStrategyDao;
import com.aimir.service.system.sla.SLAOperationManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 16-08-17.
 */

@WebService(endpointInterface = "com.aimir.service.system.sla.SLAOperationManager")
@Service(value="SLAOperationManager")
@Transactional(readOnly=false)
public class SLAOperationManagerImpl implements SLAOperationManager {

    Log logger = LogFactory.getLog(SLAOperationManagerImpl.class);

    @Autowired
    GroupStrategyDao strategyDao;

    @Override
    public Map<String, Object> getGroupStragetyList(Integer supplierId) {

        Map<String,Object> result = new HashMap<String,Object>();
        List<Map<String, Object>> strategyList = strategyDao.getStrategyBySupplier(supplierId);

        result.put("list", strategyList);

        return result;
    }

    @Override
    public Map<String, Object> addNewGroupStragety() {
        return null;
    }

    @Override
    public Map<String, Object> updateGroupStragety() {
        return null;
    }
}
