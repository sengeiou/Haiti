package com.aimir.dao.device;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.ZRU;

import java.io.Serializable;
import java.util.List;

public interface ZRUDao extends GenericDao<ZRU, Integer> {
	public ZRU getModem(Integer id);  // ZRU Modem 정보 가져오기 : ID 기준
    public ZRU get(String deviceSerial);
    public ZRU get(Integer id);
	public List<ZRU> getModem();  // ZRU Modem 정보 가져오기 : 전체
    public Serializable setModem(ZRU modem);   // ZRU Modem 정보 저장
}
