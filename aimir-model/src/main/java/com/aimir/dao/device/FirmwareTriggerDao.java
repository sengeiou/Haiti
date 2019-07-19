package com.aimir.dao.device;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.FirmwareTrigger;

public interface FirmwareTriggerDao extends GenericDao<FirmwareTrigger, Long> {
	public void createTrigger(FirmwareTrigger firmwaretrigger)throws Exception;
	public FirmwareTrigger getFirmwareTrigger(String tr_id)throws Exception;
	public void updateTrigger(FirmwareTrigger firmwaretrigger)throws Exception;
}
