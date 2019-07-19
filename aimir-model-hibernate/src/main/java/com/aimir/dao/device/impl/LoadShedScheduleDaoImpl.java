package com.aimir.dao.device.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.ScheduleType;
import com.aimir.constants.CommonConstants.WeekDay;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.LoadShedScheduleDao;
import com.aimir.model.device.LoadShedGroup;
import com.aimir.model.device.LoadShedSchedule;
import com.aimir.model.device.LoadShedScheduleVO;
import com.aimir.model.system.GroupMember;
import com.aimir.util.Condition;

@Repository(value="loadshedscheduleDao")
public class LoadShedScheduleDaoImpl extends AbstractHibernateGenericDao<LoadShedSchedule, Integer>
		implements LoadShedScheduleDao {

	private static Log log = LogFactory.getLog(LoadShedScheduleDaoImpl.class);
	
	@Autowired
	LoadShedScheduleDaoImpl(SessionFactory sessionFactory){
		super(LoadShedSchedule.class);
		super.setSessionFactory(sessionFactory);
	}

	/**(non-Javadoc)
	 * @see com.aimir.dao.device.LoadShedScheduleDao#getLoadShedSchedule(String)
	 */
	@SuppressWarnings("unchecked")
	public List<LoadShedSchedule> getLoadShedSchedule(Integer groupId) {
		log.debug("== groupId["+groupId+"]");
		
		// Group Id 에 해당하는 LoadShedSchedule 반환
		StringBuffer hqlBuf = new StringBuffer();
		
		hqlBuf.append("SELECT s ");
		hqlBuf.append("FROM LoadShedSchedule s ");
		hqlBuf.append("WHERE s.target.id = :groupId" );
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter("groupId", groupId);
		
		List list = query.list();
		log.debug("===list size : " + list.size());
		
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<LoadShedSchedule> getLoadShedSchedule(String startDate, String endDate){
		log.debug("startDate["+startDate+"], endDate["+endDate+"]");
		
		StringBuffer hqlBuf = new StringBuffer();
		
		hqlBuf.append("SELECT s ");
		hqlBuf.append("FROM   LoadShedSchedule s ");
		hqlBuf.append("WHERE  s.startDate >= :startDate ");
		hqlBuf.append("AND    s.endDate <= :endDate ");
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		
		List<LoadShedSchedule> list = query.list();
		log.debug("====== List size of getLoadShedSchedule() is " + list.size() + "=======");
		
		return list;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.aimir.dao.device.LoadShedScheduleDao#getLoadShedSchedule(String, String, String, String, String)
	 * 그룹 타입과 날짜로 LoadShedSchedule 검색하여 해당하는 그룹명 반환
	 * @param groupType GroupType
	 * @param startDate 스케쥴의 시작일
	 * @param endDate  스케쥴의 종료일
	 */
	public List<LoadShedScheduleVO> getLoadShedSchedule(String groupType, String groupName, String scheduleType, String startDate, String endDate, String dayOfWeek) {
		log.debug("groupId["+groupType+"], groupName["+groupName+"], scheduleType["+scheduleType+"], startDate["+startDate+"], " + "endDate["+endDate+"]");
		
		StringBuffer hqlBuf = new StringBuffer();
		/*hqlBuf.append(" SELECT distinct g.id, g.writeDate, g.name, g.supplyCapacity, sc.scheduleType");
		hqlBuf.append(" FROM   LoadShedGroup g INNER JOIN g.loadShedSchedules sc ");
		hqlBuf.append(" WHERE sc.startTime >= :startTime ");
		hqlBuf.append(" AND   sc.endTime   <= :endTime ");
		hqlBuf.append(" AND   g.groupType  =  :groupType ");
		hqlBuf.append(" GROUP BY g.id, g.writeDate, g.name, g.supplyCapacity, sc.scheduleType");*/
		
		
		Query query = null;
		log.debug("========= schedule Type is " + scheduleType);
		if(scheduleType.equals("None")){	// 스케쥴이 없는 LoadShedGroup
			hqlBuf.append(" SELECT g.id, g.writeDate, g.name, g.supplyCapacity, g.supplyThreshold");
			hqlBuf.append(" FROM   LoadShedGroup g");
			hqlBuf.append(" WHERE  g.groupType = :groupType");
			if(groupName !=null && !"".equals(groupName)){
				hqlBuf.append(" AND    g.name like :groupName");
			}

			hqlBuf.append(" AND    g.id not in ( SELECT distinct sc.target.id as id");
			hqlBuf.append("                  	 FROM   LoadShedSchedule sc)");
			
			log.debug("query["+hqlBuf.toString()+"]");
			query = getSession().createQuery(hqlBuf.toString());
			query.setParameter("groupType", GroupType.valueOf(groupType));
			if(groupName !=null && !"".equals(groupName)){
				query.setParameter("groupName", "%"+groupName+"%");
			}
			log.debug("quesy set");
		}else if(scheduleType.equals("DayOfWeek")){	// 요일
			log.debug("DAY OF WEEK");
			if(!dayOfWeek.equals("")){
				int idx2 = Integer.parseInt(dayOfWeek);
				WeekDay[] weekDay2 = WeekDay.values();
				log.debug(weekDay2[idx2].name());
					
			}
			
			hqlBuf.append(" SELECT g.id, g.writeDate, g.name, g.supplyCapacity, g.supplyThreshold");
			hqlBuf.append(" FROM   LoadShedGroup g");
			hqlBuf.append(" WHERE  g.groupType = :groupType");
			hqlBuf.append(" AND    g.id in ( SELECT distinct sc.target.id as id");
			hqlBuf.append("                  FROM   LoadShedSchedule sc");
			hqlBuf.append("                  WHERE  sc.scheduleType = :scheduleType");
			if(dayOfWeek.equals("")){ 
				  hqlBuf.append(")");} // 전체 DayOfWeek 스케쥴 
			else{ hqlBuf.append("            AND    sc.weekDay = :weekDay)");}// 특정 요일 스케쥴
			
			query = getSession().createQuery(hqlBuf.toString());
			query.setParameter("groupType", GroupType.valueOf(groupType));
			query.setParameter("scheduleType", ScheduleType.valueOf(scheduleType));
			
			if(dayOfWeek.equals("")){}
			else{
				int idx = Integer.parseInt(dayOfWeek);
				WeekDay[] weekDay = WeekDay.values();
				log.debug(weekDay[idx].name());
				query.setParameter("weekDay", WeekDay.valueOf(weekDay[idx].name()));}
			
			log.debug("QUERY["+query.toString()+"]");
			
		}else if(scheduleType.equals("Date")){		// 일자
			hqlBuf.append(" SELECT g.id, g.writeDate, g.name, g.supplyCapacity, g.supplyThreshold");
			hqlBuf.append(" FROM   LoadShedGroup g");
			hqlBuf.append(" WHERE  g.groupType = :groupType");
			hqlBuf.append(" AND    g.id in ( SELECT distinct sc.target.id as id");
			hqlBuf.append("                  FROM   LoadShedSchedule sc");
			hqlBuf.append("                  WHERE  sc.startTime >= :startTime");
			hqlBuf.append("                  AND    sc.endTime   <= :endTime");
			hqlBuf.append("                  AND    sc.scheduleType = :scheduleType)");
			log.debug("query["+hqlBuf.toString()+"]");
			
			query = getSession().createQuery(hqlBuf.toString());
			query.setParameter("startTime", startDate);
			query.setParameter("endTime",   endDate);
			query.setParameter("groupType", GroupType.valueOf(groupType));
			query.setParameter("scheduleType", ScheduleType.valueOf(scheduleType));
			log.debug("quesy set");
		}
		
		List list = query.list();
		log.debug("=== list size["+list.size()+"], QUERY["+query.toString()+"]");
		
		List<LoadShedScheduleVO> loadShedScheduleVOList = new ArrayList <LoadShedScheduleVO>();
		LoadShedScheduleVO loadShedScheduleVO = null;
		
		if(list != null && list.size() > 0){
			
			for(int i=0; i< list.size(); i++){
				loadShedScheduleVO = new LoadShedScheduleVO();
				
				Object[] resultData = (Object[]) list.get(i);
				loadShedScheduleVO.setNum(new Integer(i+1));
				loadShedScheduleVO.setGroupId(Integer.parseInt(resultData[0].toString()));
				loadShedScheduleVO.setCreateDate(resultData[1].toString());
				loadShedScheduleVO.setGroupName(resultData[2].toString());
				loadShedScheduleVO.setSupplyCapacity(Double.parseDouble(resultData[3].toString()));
				loadShedScheduleVO.setSupplyThreshold(Double.parseDouble(resultData[4].toString()));
				//loadShedScheduleVO.setScheduleType(ScheduleType.valueOf(resultData[4].toString()));
				
				log.debug("=== num["+(i+1)+"]" +
						"loadShedScheduleVO createDate["+loadShedScheduleVO.getCreateDate()+"], " +
						"groupId["+loadShedScheduleVO.getGroupId()+"]" +
						"groupname["+loadShedScheduleVO.getGroupName()+"], " +
						"supplycapacity["+loadShedScheduleVO.getSupplyCapacity()+"], " +
						"scheduleType["+loadShedScheduleVO.getSupplyThreshold()+"]");
				
				loadShedScheduleVOList.add(loadShedScheduleVO);
			}	
		}

		return loadShedScheduleVOList;
	}
	
	public List<LoadShedSchedule> searchLoadShedSchedule(Integer operatorId,
			String groupType, String startDate, String endDate){
		
		// 날짜 로케일 적용 필요
		return null;
	}
	
	public List<LoadShedSchedule> getLoadShedSchedule(Integer targetId, ScheduleType scheduleType){
		log.debug("== targetId["+targetId+"], scheduleType["+scheduleType.name()+"]");
		
		StringBuffer hqlBuf = new StringBuffer();
		//hqlBuf.append("SELECT s ");
		hqlBuf.append("FROM   LoadShedSchedule s ");
		hqlBuf.append("WHERE  s.target.id    = :groupId " );
		hqlBuf.append("AND    s.scheduleType = :scheduleType ");
		
		if(scheduleType == ScheduleType.Date){
			hqlBuf.append("ORDER  BY startTime, endTime, onOff ");	
		}else if(scheduleType == ScheduleType.DayOfWeek){
			hqlBuf.append("ORDER  BY weekDay, startTime, endTime, onOff ");
		}
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter("groupId", targetId);
		query.setParameter("scheduleType", scheduleType);
		
		List<LoadShedSchedule> list = query.list();
		return list;
	}
}
