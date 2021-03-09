package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.MeterMapperDao;
import com.aimir.model.device.MeterMapper;
import com.aimir.util.Condition;

@SuppressWarnings("unused")
@Repository(value = "meterMapperDao")
public class MeterMapperDaoimpl extends AbstractJpaDao<MeterMapper, Integer> implements MeterMapperDao {

	private static Log log = LogFactory.getLog(MeterMapperDaoimpl.class);
	
	public MeterMapperDaoimpl() {
		super(MeterMapper.class);
	}

	@Override
	public MeterMapper getObisMeterIdByPrintedMeterId(String modemDeviceSerial, String printedMeterId) {
		StringBuilder sbQuery = new StringBuilder();
		
		sbQuery.append("select * from meter_mapper where 1=1");
		
		if(modemDeviceSerial != null && !modemDeviceSerial.isEmpty())
			sbQuery.append("\n modem_device_serial = '").append(modemDeviceSerial).append("'");
		
		if(printedMeterId != null && !printedMeterId.isEmpty())
			sbQuery.append("\n meter_printed_mdsId = '").append(printedMeterId).append("'");
				
		Query query = em.createNativeQuery(sbQuery.toString(), MeterMapper.class);
		return (MeterMapper)query.getSingleResult();
	}

	@Override
	public MeterMapper getPrintedMeterIdByObisMeterId(String modemDeviceSerial, String obisMeterId) {
		StringBuilder sbQuery = new StringBuilder();
		
		sbQuery.append("select * from meter_mapper where 1=1");
		
		if(modemDeviceSerial != null && !modemDeviceSerial.isEmpty())
			sbQuery.append("\n modem_device_serial = '").append(modemDeviceSerial).append("'");
		
		if(obisMeterId != null && !obisMeterId.isEmpty())
			sbQuery.append("\n meter_obis_mdsId = '").append(obisMeterId).append("'");
		
		Query query = em.createNativeQuery(sbQuery.toString(), MeterMapper.class);
		return (MeterMapper)query.getSingleResult();
	}
	
	@Override
	public Integer updateMappingMeterId(String modemDeviceSerial, String obisMeterId) {
		StringBuilder sbQuery = new StringBuilder();
		
		if(modemDeviceSerial == null || obisMeterId == null) 
			return null;
		
		sbQuery.append("UPDATE METER_MAPPER SET meter_obis_mdsId = '").append(obisMeterId).append("'");
		sbQuery.append("\n WHERE modem_device_serial = '").append(modemDeviceSerial).append("'");
		
		Query query = em.createNativeQuery(sbQuery.toString());
				
		return query.executeUpdate();
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
