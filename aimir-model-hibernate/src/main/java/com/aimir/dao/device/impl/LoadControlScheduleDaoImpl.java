package com.aimir.dao.device.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.ScheduleType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.LoadControlScheduleDao;
import com.aimir.model.device.LoadControlSchedule;

@Repository(value = "loadcontrolscheduleDao")
public class LoadControlScheduleDaoImpl extends AbstractHibernateGenericDao<LoadControlSchedule, Integer> implements LoadControlScheduleDao {

	private static Log log = LogFactory.getLog(LoadControlScheduleDaoImpl.class);
	
	@Autowired
	protected LoadControlScheduleDaoImpl(SessionFactory sessionFactory) {
		super(LoadControlSchedule.class);
		super.setSessionFactory(sessionFactory);
	}

	
	public List<LoadControlSchedule> getLoadControlSchedule(String targetType,
			String targetId) {

		log.debug("targetType["+targetType+"], targetId["+targetId+"]");
		
		StringBuffer hqlBuf = new StringBuffer();
		
		hqlBuf.append("SELECT s ");
		hqlBuf.append("FROM   LoadControlSchedule s ");
		hqlBuf.append("WHERE  s.targetType = :targetType ");
		hqlBuf.append("AND    s.target.id  = :targetId ");
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter("targetType", targetType);
		query.setParameter("targetId", targetId);
		
		List list = query.list();
		log.debug("==== List size is " + list.size());
		
		return list;
	}
	
	public List<LoadControlSchedule> getLoadControlSchedule(String targetId){
		log.debug("==== getLoadControlSchedule === targetId["+targetId+"]");
		
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("SELECT s ");
		hqlBuf.append("FROM   LoadControlSchedule s ");
		hqlBuf.append("WHERE  s.target = :targetId ");
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter("targetId", targetId);
		
		List<LoadControlSchedule> list = query.list();
		if(list != null)
			log.debug("=== LIST SIZE IS : " + list.size());
		else
			log.debug("=== LIST SIZE IS ZERO ===");
		
		return list;
	}
	
	public List<LoadControlSchedule> getLoadControlSchedule(String targetId, ScheduleType type){
		log.debug("==== getLoadControlSchedule === targetId["+targetId+"], ScheduleType["+type+"]");
		
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("SELECT s ");
		hqlBuf.append("FROM   LoadControlSchedule s ");
		hqlBuf.append("WHERE  s.target = :targetId ");
		hqlBuf.append("AND    s.scheduleType = :scheduleType ");
		
		if(type == ScheduleType.Date){
			hqlBuf.append("ORDER  BY startTime, endTime, onOff ");	
		}else if(type == ScheduleType.DayOfWeek){
			hqlBuf.append("ORDER  BY weekDay, startTime, endTime, onOff ");
		}
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter("targetId", targetId);
		query.setParameter("scheduleType", type);
		
		List<LoadControlSchedule> list = query.list();
		if(list != null)
			log.debug("=== LIST SIZE IS : " + list.size());
		else
			log.debug("=== LIST SIZE IS ZERO ===");
		
		return list;
	}
}
