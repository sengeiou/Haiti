package com.aimir.dao.device;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.ZEUMBus;

import java.io.Serializable;
import java.util.List;

public interface ZEUMBusDao extends GenericDao<ZEUMBus, Integer> {
	public ZEUMBus getModem(Integer id);  // ZEUMBus Modem 정보 가져오기 : ID 기준
    public ZEUMBus get(String deviceSerial);
	public List<ZEUMBus> getModem();  // ZEUMBus Modem 정보 가져오기 : 전체
    public Serializable setModem(ZEUMBus modem);   // ZEUMBus Modem 정보 저장
}
