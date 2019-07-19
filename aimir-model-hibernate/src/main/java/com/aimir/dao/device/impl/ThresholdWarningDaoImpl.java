// INSERT SP-193
package com.aimir.dao.device.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.ThresholdWarningDao;
import com.aimir.model.device.ThresholdWarning;

@Repository(value = "ThresholdWarningDao")
public class ThresholdWarningDaoImpl extends AbstractHibernateGenericDao<ThresholdWarning, Integer> implements ThresholdWarningDao {

	private static Log log = LogFactory.getLog(ThresholdWarning.class);
	
	@Autowired
	protected ThresholdWarningDaoImpl(SessionFactory sessionFactory) {
		super(ThresholdWarning.class);
		super.setSessionFactory(sessionFactory);
	}
	
	public ThresholdWarning getThresholdWarning(DeviceType type, Integer deviceId, Integer thresholdId) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT t.id, t.ipAddr, t.deviceType, t.deviceid, t.threshold_id, t.value ");
		hqlBuf.append(" FROM ThresholdWarning t");
		hqlBuf.append(" WHERE t.deviceType = :deviceType"); 
		hqlBuf.append(" AND t.deviceid = :deviceid"); 
		hqlBuf.append(" AND t.threshold_id = :threshold_id"); 
		
		Query query = getSession().createQuery(hqlBuf.toString());

		query.setParameter("deviceType", type.getCode());
		query.setParameter("deviceid", deviceId);
		query.setParameter("threshold_id", thresholdId);
		
		List result = query.list();;
		
		if (result.size() == 0) {
			return null;
		}
		else {

		    ThresholdWarning item = new ThresholdWarning();
		    Object[] resultData = (Object[])result.get(0);
	        		
			item.setId(Integer.parseInt(resultData[0].toString()));
			if (resultData[1] != null) item.setIpAddr(resultData[1].toString());
			if (resultData[2] != null) item.setDeviceType(Integer.parseInt(resultData[2].toString()));
			if (resultData[3] != null) item.setDeviceId(Integer.parseInt(resultData[3].toString()));
			if (resultData[4] != null) item.setThresholdId(Integer.parseInt(resultData[4].toString()));
			if (resultData[5] != null) item.setValue(Integer.parseInt(resultData[5].toString()));			
						
			return item;
		}	
	}

	public ThresholdWarning getThresholdWarning(String  ip, Integer thresholdId) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT t.id, t.ipAddr, t.deviceType, t.deviceid, t.threshold_id, t.value ");
		hqlBuf.append(" FROM ThresholdWarning t");
		hqlBuf.append(" WHERE t.ipAddr = :ipAddr"); 
		hqlBuf.append(" AND t.threshold_id = :threshold_id"); 
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter("ipAddr", ip);
		query.setParameter("threshold_id", thresholdId);
		
		List result = query.list();	
		
		if (result.size() == 0) {
			return null;
		}
		else {

		    ThresholdWarning item = new ThresholdWarning();
		    Object[] resultData = (Object[])result.get(0);
	        		
			item.setId(Integer.parseInt(resultData[0].toString()));
			if (resultData[1] != null) item.setIpAddr(resultData[1].toString());
			if (resultData[2] != null) item.setDeviceType(Integer.parseInt(resultData[2].toString()));
			if (resultData[3] != null) item.setDeviceId(Integer.parseInt(resultData[3].toString()));
			if (resultData[4] != null) item.setThresholdId(Integer.parseInt(resultData[4].toString()));
			if (resultData[5] != null) item.setValue(Integer.parseInt(resultData[5].toString()));			
						
			return item;
		}	
	}		
	
	@SuppressWarnings("unchecked")
	public List<ThresholdWarning>getOverThresholdDevices(Integer thresholdId, Integer limit)
	{
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT t.id, t.ipAddr, t.deviceType, t.deviceid, t.threshold_id, t.value ");
		hqlBuf.append(" FROM ThresholdWarning t");
		hqlBuf.append(" WHERE t.threshold_id = :threshold_id"); 
		hqlBuf.append(" AND t.value > :limit"); 
		
		Query query = getSession().createQuery(hqlBuf.toString());

		query.setParameter("threshold_id", thresholdId);
		query.setParameter("limit", limit);

	    List<Object[]> result = query.list();
	    if (result == null) return null;

	    List<ThresholdWarning> list = new ArrayList<ThresholdWarning>();
	    Object[] resultData = null;     

	    for(int i = 0 ; i < result.size() ; i++) {
		    ThresholdWarning item = new ThresholdWarning();
	        resultData = (Object[])result.get(i);
	        		
			item.setId(Integer.parseInt(resultData[0].toString()));
			if (resultData[1] != null) item.setIpAddr(resultData[1].toString());
			if (resultData[2] != null) item.setDeviceType(Integer.parseInt(resultData[2].toString()));
			if (resultData[3] != null) item.setDeviceId(Integer.parseInt(resultData[3].toString()));
			if (resultData[4] != null) item.setThresholdId(Integer.parseInt(resultData[4].toString()));
			if (resultData[5] != null) item.setValue(Integer.parseInt(resultData[5].toString()));
			
	        list.add(item);
	    }		
				
		return list;		
	}

	@SuppressWarnings("unchecked")
	public List<ThresholdWarning>getThresholdWarningList(Integer thresholdId)
	{
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT t.id, t.ipAddr, t.deviceType, t.deviceid, t.threshold_id, t.value ");
		hqlBuf.append(" FROM ThresholdWarning t");
		hqlBuf.append(" WHERE t.threshold_id = :threshold_id"); 
		
		Query query = getSession().createQuery(hqlBuf.toString());

		query.setParameter("threshold_id", thresholdId);
		
		List<Object[]> result = query.list();
	    if (result == null) return null;

	    List<ThresholdWarning> list = new ArrayList<ThresholdWarning>();
	    Object[] resultData = null;     

	    for(int i = 0 ; i < result.size() ; i++) {
		    ThresholdWarning item = new ThresholdWarning();
	        resultData = (Object[])result.get(i);
	        		
			item.setId(Integer.parseInt(resultData[0].toString()));
			if (resultData[1] != null) item.setIpAddr(resultData[1].toString());
			if (resultData[2] != null) item.setDeviceType(Integer.parseInt(resultData[2].toString()));
			if (resultData[3] != null) item.setDeviceId(Integer.parseInt(resultData[3].toString()));
			if (resultData[4] != null) item.setThresholdId(Integer.parseInt(resultData[4].toString()));
			if (resultData[5] != null) item.setValue(Integer.parseInt(resultData[5].toString()));
			
	        list.add(item);
	    }		
				
		return list;			
	}		
}
