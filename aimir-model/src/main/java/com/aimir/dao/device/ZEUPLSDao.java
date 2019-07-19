package com.aimir.dao.device;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.ZEUPLS;

import java.io.Serializable;
import java.util.List;

public interface ZEUPLSDao extends GenericDao<ZEUPLS, Integer> {
    public ZEUPLS get(String deviceSerial);
	public ZEUPLS getModem(Integer id);  // ZEUPLS Modem 정보 가져오기 : ID 기준
	public List<ZEUPLS> getModems();  // ZEUPLS Modem 정보 가져오기 : 전체
    public Serializable setModem(ZEUPLS modem);   // ZEUPLS Modem 정보 저장
}
