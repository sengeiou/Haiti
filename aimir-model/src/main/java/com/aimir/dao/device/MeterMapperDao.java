package com.aimir.dao.device;

import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.MeterMapper;

public interface MeterMapperDao extends GenericDao<MeterMapper, Integer>{

	/*
	 * 명판에 인쇄된 meterId을 기반으로 obis 기반의 meterId(dcu/modem이 올리는 meterId)을 가져오는 함수 
	 */
	public String getObisMeterIdByPrintedMeterId(String modemDeviceSerial, String printedMeterId);
	
	/*
	 * obis 기반의 meterId(dcu/modem이 올리는 meterId)을 기반으로 명판에 인쇄된 meterId을 인쇄
	 * 
	 */
	public String getPrintedMeterIdByObisMeterId(String modemDeviceSerial, String obisMeterId);
}
