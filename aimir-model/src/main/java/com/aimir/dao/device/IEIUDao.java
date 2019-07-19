package com.aimir.dao.device;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.IEIU;

import java.io.Serializable;
import java.util.List;

public interface IEIUDao extends GenericDao<IEIU, Integer> {
	public IEIU getModem(Integer id);  // IEIU Modem 정보 가져오기 : ID 기준
	public List<IEIU> getModem();  // IEIU Modem 정보 가져오기 : 전체
    public Serializable setModem(IEIU modem);   // IEIU Modem 정보 저장
}
