package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MeterMapperDao;
import com.aimir.model.device.MeterMapper;
import com.aimir.util.Condition;

@SuppressWarnings("unused")
@Repository(value = "meterMapperDao")
public class MeterMapperDaoimpl extends AbstractJpaDao<MeterMapper, Integer> implements MeterMapperDao {

	public MeterMapperDaoimpl() {
		super(MeterMapper.class);
	}

	@Override
	public String getObisMeterIdByPrintedMeterId(String modemDeviceSerial, String printedMeterId) {
		StringBuilder query = new StringBuilder();
		
		query.append("select * from meter_mapper where 1=1");
		
		if(modemDeviceSerial != null && !modemDeviceSerial.isEmpty())
			query.append("\n modem_device_serial = ?");
		
		if(printedMeterId != null && !printedMeterId.isEmpty())
			query.append("\n printedMeterId = ?");
		
		
		
			
			
		
		return null;
	}

	@Override
	public String getPrintedMeterIdByObisMeterId(String modemDeviceSerial, String obisMeterId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Class<MeterMapper> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
		// TODO Auto-generated method stub
		return null;
	}
}
