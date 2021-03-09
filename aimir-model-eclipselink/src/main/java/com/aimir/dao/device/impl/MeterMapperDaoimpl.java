package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.Query;

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
	public MeterMapper getObisMeterIdByPrintedMeterId(String modemDeviceSerial, String printedMeterId) {
		StringBuilder sbQuery = new StringBuilder();
		
		sbQuery.append("select * from meter_mapper where 1=1");
		
		if(modemDeviceSerial != null && !modemDeviceSerial.isEmpty())
			sbQuery.append("\n modem_device_serial = :modemDeviceSerial");
		
		if(printedMeterId != null && !printedMeterId.isEmpty())
			sbQuery.append("\n meter_printed_mdsId = :printedMeterId");
				
		Query query = em.createNativeQuery(sbQuery.toString(), MeterMapper.class);
		if(modemDeviceSerial != null && !modemDeviceSerial.isEmpty())
			query.setParameter("modemDeviceSerial", modemDeviceSerial);
		
		if(printedMeterId != null && !printedMeterId.isEmpty())
			query.setParameter("modem_device_serial", printedMeterId);
		
		return (MeterMapper)query.getSingleResult();
	}

	@Override
	public MeterMapper getPrintedMeterIdByObisMeterId(String modemDeviceSerial, String obisMeterId) {
		StringBuilder sbQuery = new StringBuilder();
		
		sbQuery.append("select * from meter_mapper where 1=1");
		
		if(modemDeviceSerial != null && !modemDeviceSerial.isEmpty())
			sbQuery.append("\n modem_device_serial = :modemDeviceSerial");
		
		if(obisMeterId != null && !obisMeterId.isEmpty())
			sbQuery.append("\n meter_obis_mdsId = :obisMeterId");
		
		Query query = em.createNativeQuery(sbQuery.toString(), MeterMapper.class);
		if(modemDeviceSerial != null && !modemDeviceSerial.isEmpty())
			query.setParameter("modemDeviceSerial", modemDeviceSerial);
		
		if(obisMeterId != null && !obisMeterId.isEmpty())
			query.setParameter("obisMeterId", obisMeterId);
		
		return (MeterMapper)query.getSingleResult();
	}
	
	@Override
	public Integer updateMappingMeterId(String modemDeviceSerial, String obisMeterId) {
		StringBuilder sbQuery = new StringBuilder();
		
		if(modemDeviceSerial == null || obisMeterId == null) 
			return null;
		
		sbQuery.append("update meter_mapper set meter_obis_mdsId = :obisMeterId where modem_device_serial = :modemDeviceSerial");
		
		Query query = em.createNativeQuery(sbQuery.toString());
		query.setParameter("modemDeviceSerial", modemDeviceSerial);
		query.setParameter("obisMeterId", obisMeterId);
		
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
