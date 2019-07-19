package com.aimir.dao.device;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.ZBRepeater;

import java.io.Serializable;
import java.util.List;

public interface ZBRepeaterDao extends GenericDao<ZBRepeater, Integer> {
    public ZBRepeater get(String deviceSerial);
	public ZBRepeater getModem(Integer id);  // ZBRepeater Modem 정보 가져오기 : ID 기준
	public List<ZBRepeater> getModem();  // ZBRepeater Modem 정보 가져오기 : 전체
    public Serializable setModem(ZBRepeater modem);   // ZBRepeater Modem 정보 저장
}
