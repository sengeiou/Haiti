package com.aimir.dao.device;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.SubGiga;

import java.io.Serializable;
import java.util.List;

public interface SubGigaDao extends GenericDao<SubGiga, Integer> {
	public SubGiga getModem(Integer id);  // SubGiga Modem 정보 가져오기 : ID 기준
    public SubGiga get(String deviceSerial);
    public SubGiga get(Integer id);
	public List<SubGiga> getModem();  // SubGiga 정보 가져오기 : 전체
    public Serializable setModem(SubGiga modem);   // Modem 정보 저장
}
