package com.aimir.dao.device;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.ZMU;

import java.io.Serializable;
import java.util.List;

public interface ZMUDao extends GenericDao<ZMU, Integer> {
	public ZMU getModem(Integer id);  // ZMU Modem 정보 가져오기 : ID 기준
    public ZMU get(String deviceSerial);
	public List<ZMU> getModem();  // ZMU Modem 정보 가져오기 : 전체
    public Serializable setModem(ZMU modem);   // ZMU Modem 정보 저장
}
