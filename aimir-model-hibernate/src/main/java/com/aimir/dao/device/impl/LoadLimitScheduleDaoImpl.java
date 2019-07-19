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
import com.aimir.dao.device.LoadLimitScheduleDao;
import com.aimir.dao.device.LoadShedScheduleDao;
import com.aimir.model.device.LoadLimitSchedule;

@Repository(value = "loadlimitscheduleDao")
public class LoadLimitScheduleDaoImpl extends AbstractHibernateGenericDao<LoadLimitSchedule, Integer> implements LoadLimitScheduleDao {

	private static Log log = LogFactory.getLog(LoadLimitScheduleDaoImpl.class);
	
	@Autowired
	protected LoadLimitScheduleDaoImpl(SessionFactory sessionFactory) {
		super(LoadLimitSchedule.class);
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public List<LoadLimitSchedule> getLoadLimitSchedule(String targetType,
			String targetId) {
		log.debug("targetType["+targetType+"], targetId["+targetId+"]");
		
		StringBuffer hqlBuf = new StringBuffer();
		
		hqlBuf.append("SELECT s ");
		hqlBuf.append("FROM   LoadLimitSchedule s ");
		hqlBuf.append("WHERE  s.targetType = :targetType ");
		hqlBuf.append("AND    s.target.id  = :targetId ");
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter("targetType", targetType);
		query.setParameter("targetId", targetId);
		
		List list = query.list();
		log.debug("==== List size is " + list.size());
		
		return list;
	}
	
	public List<LoadLimitSchedule> getLoadLimitSchedule(String targetId){
		log.debug("groupId["+targetId+"]");
		
		StringBuffer hqlBuf = new StringBuffer();
		
		hqlBuf.append("SELECT s ");
		hqlBuf.append("FROM   LoadLimitSchedule s ");
		hqlBuf.append("WHERE  s.target = :targetId ");
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter("targetId", targetId);
		
		List<LoadLimitSchedule> list = query.list();
		return list;
	}
	
	public List<LoadLimitSchedule> getLoadLimitSchedule(String targetId, ScheduleType scheduleType){
		log.debug("targetId["+targetId+"], scheduleType["+scheduleType.name()+"]");
		
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("SELECT s ");
		hqlBuf.append("FROM   LoadLimitSchedule s ");
		hqlBuf.append("WHERE  s.target       = :targetId ");
		hqlBuf.append("AND    s.scheduleType = :scheduleType ");
		
		if(scheduleType == ScheduleType.Date){
			hqlBuf.append("ORDER  BY startTime, endTime ");	
		}else if(scheduleType == ScheduleType.DayOfWeek){
			hqlBuf.append("ORDER  BY weekDay, startTime, endTime ");
		}
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter("targetId", targetId);
		query.setParameter("scheduleType", scheduleType);
		
		List<LoadLimitSchedule> list = query.list();
		return list;
	}
}
