package com.aimir.dao.device.impl;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.MeterMapperDao;
import com.aimir.model.device.MeterMapper;

@Repository(value = "meterMapperDao")
public class MeterMapperDaoimpl extends AbstractHibernateGenericDao<MeterMapper, Integer> implements MeterMapperDao {

    @Autowired
    protected MeterMapperDaoimpl(SessionFactory sessionFactory) {
        super(MeterMapper.class);
        super.setSessionFactory(sessionFactory);
    }

	@Override
	public MeterMapper getObisMeterIdByPrintedMeterId(String modemDeviceSerial, String printedMeterId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MeterMapper getPrintedMeterIdByObisMeterId(String modemDeviceSerial, String obisMeterId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer updateMappingMeterId(String modemDeviceSerial, String obisMeterId) {
		// TODO Auto-generated method stub
		return null;
	}

}
