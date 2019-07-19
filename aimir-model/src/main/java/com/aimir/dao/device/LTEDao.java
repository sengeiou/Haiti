package com.aimir.dao.device;

import java.io.Serializable;
import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.LTE;

public interface LTEDao extends GenericDao<LTE, Integer> {
	public LTE getModem(Integer id); // LTE Modem 정보 가져오기 : ID 기준

	public LTE get(String deviceSerial);

	public LTE get(Integer id);

	public List<LTE> getModem(); // LTE 정보 가져오기 : 전체

	public Serializable setModem(LTE modem); // Modem 정보 저장
}
