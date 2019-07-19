package com.aimir.dao.integration.impl;

import java.text.ParseException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.integration.WSMeterConfigResultDao;
import com.aimir.model.integration.WSMeterConfigResult;
import com.aimir.model.integration.WSMeterConfigResultPk;
import com.aimir.util.TimeUtil;

@Repository(value = "wsmeterconfigresultDao")
public class WSMeterConfigResultDaoImpl extends AbstractHibernateGenericDao<WSMeterConfigResult, WSMeterConfigResultPk> implements WSMeterConfigResultDao {

	@Autowired
	protected WSMeterConfigResultDaoImpl(SessionFactory sessionFactory) {
		super(WSMeterConfigResult.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public List<WSMeterConfigResult> getResultsByAsyncTrId(String deviceId, String asyncTrId, String command){
		String requestDate = asyncTrId.substring(0, 14);
		String trId = asyncTrId.substring(15);
		return getResults(requestDate, deviceId,  trId, command);
	}
	
	public List<WSMeterConfigResult> getResults(String requestDate, String meterId, String trId, String command) {
		Criteria criteria = getSession().createCriteria(WSMeterConfigResult.class);
		if ( requestDate != null && requestDate.length() > 0 ) {
			criteria.add(Restrictions.eq("id.requestDate", requestDate));
		}
		criteria.add(Restrictions.eq("deviceId", meterId));
		criteria.add(Restrictions.eq("id.trId", trId));
		if ( command != null && command.length() > 0) {
			criteria.add(Restrictions.eq("command", command));
		}
		criteria.addOrder(Order.asc("num"));
		return criteria.list();
	}
	
	public void addByAsyncTrId(String deviceId, String asyncTrId, String resultValue, String command)
	{
		String currentTime = null;
		try {
			currentTime = TimeUtil.getCurrentTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String requestTime = asyncTrId.substring(0, 14);
		String uuid = asyncTrId.substring(15);
		WSMeterConfigResult wsResult = new WSMeterConfigResult();
		wsResult.setTrId(uuid);
		wsResult.setRequestDate(requestTime);
		wsResult.setDeviceId(deviceId);
		wsResult.setNum(0);
		wsResult.setCommand(command);
		wsResult.setResultValue(resultValue);
		wsResult.setWriteDate(currentTime);
		add(wsResult);
	}

}
