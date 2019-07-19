package com.aimir.dao.device;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.GenericDao;
import com.aimir.model.device.PLCIU;

import java.io.Serializable;
import java.util.List;

public interface PLCIUDao extends GenericDao<PLCIU, Integer> {
	public PLCIU getModem(Integer id);  // PLCIU Modem 정보 가져오기 : ID 기준
	public List<PLCIU> getModem();  // Modem 정보 가져오기 : 전체
    public Serializable setModem(PLCIU modem, ModemType modemType);   // Modem 정보 저장
}
