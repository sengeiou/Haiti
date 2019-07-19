package com.aimir.dao.device;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.Converter;

import java.io.Serializable;
import java.util.List;

public interface ConverterDao extends GenericDao<Converter, Integer> {
    public Converter get(String deviceSerial);
	public Converter getModem(Integer id);  // Converter Modem 정보 가져오기 : ID 기준
	public List<Converter> getModem();  // Converter Modem 정보 가져오기 : 전체
    public Serializable setModem(Converter modem);   // Converter Modem 정보 저장
}
