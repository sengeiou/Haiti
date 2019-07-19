package com.aimir.service.device.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.OperationListDao;
import com.aimir.model.device.OperationList;
import com.aimir.service.device.OperationListManager;

@Service(value = "operationListManager")
@Transactional(readOnly=false)
public class OperationListManagerImpl implements OperationListManager{
	private static Log logger = LogFactory.getLog(OperationListManagerImpl.class);

	@Autowired
	public OperationListDao operationListDao;

    /**
     * method name : getOperationListByModelId<b/>
     * method Desc : MDIS - Meter Management 맥스가젯에서 선택한 Meter 의 Model ID 에 해당하는 OperationList 를 조회한다.
     *
     * @param modelId aimir.model.system.DeviceModel.id
     * @param operationCodeList List of aimir.model.device.OperationList.operationCode.code
     * @return List of aimir.model.device.OperationList
     */
    public List<OperationList> getOperationListByModelId (Integer modelId, List<String> operationCodeList) {
        return operationListDao.getOperationListByModelId(modelId, operationCodeList);
    }

	@Override
	public List<String> getAvailableOperationList(Integer modelId, Integer roleID) {
		 return operationListDao.getAvailableOperationList(modelId, roleID);
	}
}