package com.aimir.dao.device;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.MMIU;

import java.io.Serializable;
import java.util.List;

public interface MMIUDao extends GenericDao<MMIU, Integer> {
	public MMIU getModem(Integer id);  // MMIU Modem 정보 가져오기 : ID 기준
	public List<MMIU> getModem();  // MMIU Modem 정보 가져오기 : 전체
    public Serializable setModem(MMIU modem);   // MMIU Modem 정보 저장
}
