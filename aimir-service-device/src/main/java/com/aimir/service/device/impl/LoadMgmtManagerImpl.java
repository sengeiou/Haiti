package com.aimir.service.device.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.FW_STATE;
import com.aimir.constants.CommonConstants.LimitType;
import com.aimir.constants.CommonConstants.LoadType;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.OnOffType;
import com.aimir.constants.CommonConstants.PeakType;
import com.aimir.constants.CommonConstants.ScheduleType;
import com.aimir.constants.CommonConstants.WeekDay;
import com.aimir.dao.device.LoadControlScheduleDao;
import com.aimir.dao.device.LoadLimitScheduleDao;
import com.aimir.dao.device.LoadShedGroupDao;
import com.aimir.dao.device.LoadShedScheduleDao;
import com.aimir.dao.system.GroupDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.model.device.LoadControlSchedule;
import com.aimir.model.device.LoadLimitSchedule;
import com.aimir.model.device.LoadShedGroup;
import com.aimir.model.device.LoadShedSchedule;
import com.aimir.model.device.LoadShedScheduleVO;
import com.aimir.model.system.AimirGroup;
import com.aimir.service.device.LoadMgmtManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Service(value="loadMgmtManager")
@Transactional(readOnly=false)
public class LoadMgmtManagerImpl implements LoadMgmtManager {

	private static Log log = LogFactory.getLog(LoadMgmtManagerImpl.class);
	
	@Autowired
	LoadShedGroupDao groupDao;
	//GroupDao groupDao;
	
	@Autowired
	GroupMemberDao groupMemberDao; 
	
	@Autowired
	LoadShedGroupDao loadShedGroupDao;
	
	@Autowired
	LoadControlScheduleDao loadControlScheduleDao;
	
	@Autowired
	LoadLimitScheduleDao loadLimitScheduleDao;
	
	@Autowired
	LoadShedScheduleDao loadShedScheduleDao;
	
	@Autowired                                                         
	OperatorDao operatorDao;    
	
	/**(non-Javadoc)
	 * @see com.aimir.service.device.LoadMgmtManager#getGroupTypeCombo()
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getGroupTypeCombo() {
		List<Object> resultList = new ArrayList<Object>();
		Map<String, Object> resultMap = null;
		
		for(GroupType t: GroupType.values()){
			resultMap = new HashMap();
			resultMap.put("id", t.ordinal());
			resultMap.put("name", t.name());
			resultList.add(resultMap);
		}
		return resultList;
	}
	
	/**(non-Javadoc)
	 * @see com.aimir.service.device.LoadMgmtManager#getLoadTypeCombo()
	 */
	public List<Object> getLoadTypeCombo(){
		List<Object> typeList = new ArrayList<Object>();
		Map<String, Object> type = null;
		
		for(LoadType t: LoadType.values()){
			type = new HashMap();
			type.put("id", t.ordinal());
			type.put("name", t.name());
			typeList.add(type);
		}
		return typeList;
	}
	
	/**(non-Javadoc)
	 * @see com.aimir.service.device.LoadMgmtManager#getWeekDayCombo()
	 */
	public List<Object> getWeekDayCombo() {
		List<Object> weekList 	 = new ArrayList<Object>();
		Map<String, Object> week = null;
		
		for(WeekDay d: WeekDay.values()){
			week = new HashMap();
			
			week.put("id", d.ordinal());
			//  Locale 에 따라 name 변수에 넣을 언어를 설정하는 로직 필요(supplierId 필요)
			week.put("code", d.getCode());
			week.put("name", d.getEngName());
			
			weekList.add(week);
		}
		return weekList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.aimir.service.device.LoadMgmtManager#getHourCombo()
	 */
	public List<Object> getHourCombo(){
		List<Object> hourList = new ArrayList<Object>();
		Map<String, Object> hour = null;
		
		DecimalFormat df = new DecimalFormat("00");
		for(int i = 0; i < 24; i++){
			hour = new HashMap();
			hour.put("hour", df.format(i));	// 자리수 2자리 유지(00, 01)
			hourList.add(hour);
		}
		return hourList;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.aimir.service.device.LoadMgmtManager#getMinuteCombo()
	 */
	public List<Object> getMinuteCombo(){
		List<Object> minuteList = new ArrayList<Object>();
		Map<String, Object> minute = null;
		
		DecimalFormat df = new DecimalFormat("00");
		for(int i = 0; i < 60; i++){
			minute = new HashMap();
			minute.put("minute", df.format(i));	// 자리수 2자리 유지(00, 01)
			minuteList.add(minute);
		}
		return minuteList;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.aimir.service.device.LoadMgmtManager#getGroupNameCombo()
	 */
	public List<Object> getOnOffCombo(){
		List<Object> onOffList = new ArrayList<Object>();
		Map<String, Object> onOff = null;
		
		onOff = new HashMap();
		onOff.put("name", "OFF");
		onOff.put("onOff", "off");
		onOffList.add(onOff);
		
		onOff = new HashMap();
		onOff.put("name", "ON");
		onOff.put("onOff", "on");
		onOffList.add(onOff);
		
		return onOffList;
	}
	
	/**
	 * Limit Type 목록 반환. Demand, Current
	 */
	public List<Object> getLimitTypeCombo(){
		List<Object> limitTypeList = new ArrayList<Object>();
		Map<String, Object>	limitType = null;
		LimitType types[] = LimitType.values();
		
		for(LimitType t : types){
			limitType = new HashMap();
			limitType.put("name", t.name());
			limitTypeList.add(limitType);	
		}
		return limitTypeList;
	}
	
	/**
	 * Peak Type 목록 반환. PEAK, OFF_PEAK, CRITICAL_PEAK
	 */
	public List<Object> getPeakTypeCombo(){
		List<Object> peakTypeList = new ArrayList<Object>();
		Map<String, Object> peakType = null;
		
		PeakType[] peak = PeakType.values();
		for(PeakType p : peak){
			peakType = new HashMap();
			peakType.put("name", p.name());
			peakTypeList.add(peakType);
		}
		
		return peakTypeList;
		
	}
	/**
	 * (non-Javadoc)
	 * @see com.aimir.service.device.LoadMgmtManager#getGroupNameCombo()
	 */
	@SuppressWarnings("unchecked")
	public List<LoadShedGroup> getGroupListCombo(String operatorId){
		log.debug("=== parameter operator id : " + operatorId);
		
		Integer id = 0;
		if(!"".equals(StringUtil.nullToBlank(operatorId))){
			id = Integer.parseInt(operatorId);
		}
		
		List<LoadShedGroup> list = loadShedGroupDao.getLoadShedGroupList(id);
		return list;
	}
	
	/**(non-Javadoc)
	 * @see com.aimir.service.device.LoadMgmtManager#getDRGroupList(String)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getDRGroupList(String operatorId){
		log.debug("=== parameter operator id : " + operatorId);
		Integer id = 0;
		if(!"".equals(StringUtil.nullToBlank(operatorId))){
			id = Integer.parseInt(operatorId);
		}
		
		List<Object> list = loadShedGroupDao.getGroupListWithChild2(id);
		//List<Object> list = loadShedGroupDao.getGroupListWithChild(id);
		log.debug("=== get grouplist result ===	");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("grid", list);
		
		return result;
	}
	
	/**
	 * 새로운 그룹을 추가
	 */
	@SuppressWarnings("unchecked")
	public int saveGroups(List<Object> groups) throws Exception {
		log.debug("================ saveGroups ================");
		int returnCnt = 0;
		//AimirGroup newGroup = null;
		LoadShedGroup newGroup = null;
		Map<String, Object> g = null;
		log.debug("SIZE : " + groups.size());
		for(Object obj:groups){
			log.debug("===== for loop =======");
			g = (Map<String, Object>)obj;
			
			//newGroup = new AimirGroup();
			newGroup = new LoadShedGroup();
			log.debug("=== 1");
			log.debug("OP : "     + g.get("operatorId").toString());
			log.debug(" id : "    + (Integer)g.get("id"));
			log.debug("name : "   + g.get("name").toString());
			log.debug("groupType : " + g.get("groupType").toString());
			log.debug("capa : "   + Double.parseDouble(g.get("supplyCapacity").toString()));
			log.debug("thresh : " + Double.parseDouble(g.get("supplyThreshold").toString()));
			log.debug("desc ; "   + g.get("descr").toString());
			log.debug("allUA : "  + g.get("allUserAccess").toString());
			
			newGroup.setOperator(operatorDao.getOperatorById(Integer.parseInt((String)g.get("operatorId"))));
			newGroup.setId((Integer)g.get("id"));
			newGroup.setName((String)g.get("name"));
			newGroup.setGroupType((String)g.get("groupType"));
			newGroup.setSupplyCapacity(Double.parseDouble(g.get("supplyCapacity").toString()));
			newGroup.setSupplyThreshold(Double.parseDouble(g.get("supplyThreshold").toString()));
			newGroup.setDescr((String)g.get("descr"));
			newGroup.setAllUserAccess(("Y".equals((String)g.get("allUserAccess"))) ? true : false);
			log.debug("=== 2");
			newGroup.setWriteDate(CalendarUtil.getCurrentDate());
			newGroup.setMembers(groupMemberDao.getGroupMemberById((Integer)g.get("id")));

			/*String debugMsg = "opId["+operatorDao.getOperatorById(Integer.parseInt((String)g.get("operatorId")))+"], " +
			"id["+(Integer)g.get("id")+"], name["+(String)g.get("name")+"], groupType["+(String)g.get("groupType")+"], " +
			"supCap["+(String)g.get("supplyCapacity")+"], supThld["+(String)g.get("supplyThreshold")+"], desc["+(String)g.get("descr")+"]";*/
			log.debug("==========================debug==========================");
			//log.debug(debugMsg);
			
			log.debug("---- stats : " + (String)g.get("state"));
			if("U".equals((String)g.get("state"))){
				log.debug("======= STATE is U =====");
				//newGroup = groupDao.groupUpdate(newGroup);	// Generic
				newGroup = loadShedGroupDao.groupUpdate(newGroup);
			}
			else{
				log.debug("====== STATE is not U ======");
				//newGroup = groupDao.groupAdd(newGroup);
				newGroup = loadShedGroupDao.groupAdd(newGroup);
				//newGroup = loadShedGroupDao.add(newGroup);
			}
			log.debug("==newgruop : " + newGroup.toString());
			if(newGroup != null) returnCnt++;
		}
		return returnCnt;
		
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getLoadShedGroupWithSchedule(String operatorId) {
		
		// 사용자 ID
		Integer id = 0;
		if(!"".equals(StringUtil.nullToBlank(operatorId))){
			id = Integer.parseInt(operatorId);
		}
		
		Integer 				groupId;
		List<LoadShedSchedule>  scheduleList 	= null;
		Set<LoadShedSchedule> 	schedules 		= null;
		
		//반환할 그룹 정보 목록
		List<Object> returnList = new ArrayList<Object>();
		
		// LoadShedGroup 을 구한다.
		List<LoadShedGroup> groupList = loadShedGroupDao.getLoadShedGroupList(id);
		
		// 각 그룹에 대한 LoadShedSchedule 을 구하여  add
		for(LoadShedGroup g : groupList){
			groupId = g.getId();
			// 해당 그룹의 스케쥴 목록
			scheduleList = loadShedScheduleDao.getLoadShedSchedule(groupId);
			
			for(LoadShedSchedule s: scheduleList){
				schedules.add(s);
			}
			// 스케쥴 목록 추가
			g.setLoadShedSchedules(schedules);
			
			returnList.add(g);
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("grid", returnList);
		
		return result;
	}
	
	public List<LoadShedGroup> getLoadShedGroupWithoutSchedule(String groupType, String groupName){
		log.debug("======== LoadShedGroupWithoutSchedule called ==========");

		return loadShedGroupDao.getLoadShedGroupListWithoutSchedule(groupType, groupName);
		
	}
	/**
	 * 
	 */
	//public Map<String, Object> getLoadShedGroupBySchedule(Map<String, Object> condition){
	public List<LoadShedScheduleVO> getLoadShedGroupBySchedule(Map<String, Object> condition){
		log.debug("===============getLoadShedGroupWithSchedule called");
		log.debug("Map : " + condition.toString());
		String supplierId = (String)condition.get("supplierId");
		//String operatorId = (String)condition.get("operatorId");	log.debug("operator  : " + condition.get("operatorId"));
		String groupName  = (String)condition.get("groupName");
		String groupType  = (String)condition.get("groupType");
		String scheduleType	  = (String)condition.get("scheduleType");
		String startDate  = (String)condition.get("startDate");
		String endDate	  = (String)condition.get("endDate");
		String dayOfWeek  = (String)condition.get("dayOfWeek");
		
		log.debug("supplierId["+supplierId+"], groupType["+groupType+"], " +
				"startDate["+startDate+"], endDate["+endDate+"]");
		
		startDate = startDate + "000000";
		endDate   = endDate + "000000";
		
		// LoadShedSchedule에서 groupType, startDate, endDate 를 만족시키는 스케쥴이 있는지 검색
		/*List<LoadShedGroup> list = loadShedScheduleDao.getLoadShedSchedule(groupType, startDate, endDate);
				
		log.debug("======== Returned LoadShedGroup list size : " + list.size());
		log.debug("======== LIST : " + list.toString());
		
		Map<String, Object> 	scheduleList = null;
		LoadShedSchedule	 	schedule = null;
		List<Object> 			returnList = null;
		
		if(list.size() > 0){
			log.debug("===== Step in 1 =====");
			for(LoadShedGroup g: list){
				log.debug("===== Step in 2 ===== schdules size : " + g.getLoadShedSchedules().size());
				schedule = getLatestSchedule( g.getLoadShedSchedules());
				
				// 스케쥴이 여러개일 경우 createTime 이 가장 최근인 날짜로 표시
				if(schedule != null){
					scheduleList.put("createTime", 		schedule.getCreateTime());	log.debug("createTime : " + schedule.getCreateTime());
					scheduleList.put("groupName",  		g.getName());				log.debug("groupName  : " + g.getName());
					scheduleList.put("supplyCapacity",  g.getSupplyCapacity());		log.debug("supplyCapacity : " + g.getSupplyCapacity());
					scheduleList.put("periodType", schedule.getScheduleType().name()); log.debug("periodType : " + schedule.getScheduleType().name());
					
					if(ScheduleType.DayOfWeek == schedule.getScheduleType()){
						scheduleList.put("period", schedule.getWeekDay());
					}else{
						scheduleList.put("period", "");
					}	
				}else{
					
					scheduleList.put("createTime", "");			
					scheduleList.put("groupName", g.getName());					log.debug("groupName  : " + g.getName());
					scheduleList.put("supplyCapacity", g.getSupplyCapacity());	log.debug("supplyCapacity : " + g.getSupplyCapacity());
					scheduleList.put("periodType", "");
					scheduleList.put("period", "");
				}
				log.debug("==== Step in 2 end ====");
				returnList.add(scheduleList);
			}
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", returnList);
		
		log.debug("========end of getLoadShedGroupBySchedule====");
		return result;*/
		
		List<LoadShedScheduleVO> list = loadShedScheduleDao.getLoadShedSchedule(groupType, groupName, scheduleType, startDate, endDate, dayOfWeek);
		log.debug("======== list size : " + list.size());
		
		//Map<String, Object> result = new HashMap<String, Object>();
		//result.put("shedList", list);
		
		return list;
	}
	
	private LoadShedSchedule getLatestSchedule(Set<LoadShedSchedule> schedules){
		log.debug("======= getLatestSchedule ==== schedule size : " + schedules.size());
		int i = 0;
		LoadShedSchedule schedule = null;
		String createTime = "";
		
		if(schedules.size() > 0){
			log.debug("== getLatestSchedule 2==");
			
			for(LoadShedSchedule g: schedules){
				log.debug("== getLatestSchedule 3 ==");
				if(createTime.compareTo(g.getCreateTime()) < 0){
					createTime = g.getCreateTime();
					schedule = g;
				}
			}
		}else{
			log.debug("no SET");
			return null;
		}
		
		
		log.debug("======= end of getLatestSchedule ======");
		return schedule;
	}
	
	/////////////////// LOADSHEDGROUP /////////////////
	public LoadShedGroup getLoadShedGroup(String groupId){
		return loadShedGroupDao.findById(Integer.parseInt(groupId), false);
	}

	/////////////////// LOAD CONTROL ////////////////////////////
	@SuppressWarnings("unchecked")
	public List<LoadControlSchedule> getLoadControlSchedule(String targetId) {
		log.debug("======= getLoadShedSchedule called =======");
		List<LoadControlSchedule> list = loadControlScheduleDao.getLoadControlSchedule(targetId);
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<LoadControlSchedule> getLoadControlScheduleByDate(String targetId) {
		log.debug("======= getLoadControlScheduleByDate called =======");
		List<LoadControlSchedule> list = loadControlScheduleDao.getLoadControlSchedule(targetId, ScheduleType.Date);
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<LoadControlSchedule> getLoadControlScheduleByWeekday(String targetId) {
		log.debug("======= getLoadControlScheduleByWeekday called =======");
		List<LoadControlSchedule> list = loadControlScheduleDao.getLoadControlSchedule(targetId, ScheduleType.DayOfWeek);
		
		return list;
	}
	
	///////////////////////// LOAD LIMIT ////////////////////////////////
	@SuppressWarnings("unchecked")
	public List<LoadLimitSchedule> getLoadLimitSchedule(String targetId) {
		log.debug("======= getLoadLimitSchedule called =======");
		List<LoadLimitSchedule> list = loadLimitScheduleDao.getLoadLimitSchedule(targetId);
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<LoadLimitSchedule> getLoadLimitScheduleByDate(String targetId){
		log.debug("======= getLoadLimitScheduleByDate called =======");
		return loadLimitScheduleDao.getLoadLimitSchedule(targetId, ScheduleType.Date);
	}
	
	@SuppressWarnings("unchecked")
	public List<LoadLimitSchedule> getLoadLimitScheduleByWeekday(String targetId){
		log.debug("======= getLoadLimitScheduleByWeekday called =======");
		List<LoadLimitSchedule> list = loadLimitScheduleDao.getLoadLimitSchedule(targetId, ScheduleType.DayOfWeek);
		// DayOfWeek 의 경우 startTime, endTime 앞의 00000000(년월일) 을 제거해야함
		
		/*LoadLimitSchedule schedule = null;
		
		for(int i=0; i < list.size(); i++){
			schedule = list.get(i);
			log.debug("pre startTime["+schedule.getStartTime()+"], endTime["+schedule.getEndTime()+"]");
			schedule.setStartTime(schedule.getStartTime().substring(8));
			schedule.setEndTime(schedule.getEndTime().substring(8));
			log.debug("post startTime["+schedule.getStartTime()+"], endTime["+schedule.getEndTime()+"]");
			list.set(i, schedule);
		}*/
		return list;
	}
	
	/////////////////////// LOAD SHED SCHEDULE ///////////////////////////
	@SuppressWarnings("unchecked")
	public List<LoadShedSchedule> getLoadShedSchedule(Integer targetId) {
		log.debug("======= getLoadShedSchedule called =======");
		return loadShedScheduleDao.getLoadShedSchedule(targetId);
	}
	
	@SuppressWarnings("unchecked")
	public List<LoadShedSchedule> getLoadShedScheduleByDate(Integer targetId) {
		log.debug("======= getLoadShedSchedule called =======");
		return loadShedScheduleDao.getLoadShedSchedule(targetId, ScheduleType.Date);
	}
	
	@SuppressWarnings("unchecked")
	public List<LoadShedSchedule> getLoadShedScheduleByWeekday(Integer targetId) {
		log.debug("======= getLoadShedSchedule called =======");
		return loadShedScheduleDao.getLoadShedSchedule(targetId, ScheduleType.DayOfWeek);
	}
	
	
	@SuppressWarnings("unchecked")
	public List getLoadShedGroupMembers(String targetType, String targetName){
		return groupMemberDao.getLoadShedGroupMembers(targetType, targetName);
	}
	
	@SuppressWarnings("unchecked")
	public LoadControlSchedule addLoadControlSchedule(Map<String, Object> condition){

		log.debug("==========addLoadControlSchedule==============");
		log.debug("scheduleType["+condition.get("scheduleType").toString()+"], " +
				"groupType["+condition.get("targetType").toString()+"], " +
				"groupId["+condition.get("targetId").toString()+"], " +
				"OnOff["+condition.get("onOff").toString()+"], " +
				"startTime["+condition.get("startTime").toString()+"], " +
				"endTime["+condition.get("endTime").toString()+"]");
		
		LoadControlSchedule lcs = new LoadControlSchedule();
		
		try {
			lcs.setTargetType(condition.get("targetType").toString());
			lcs.setTarget(condition.get("targetId").toString());
			lcs.setOnOff(condition.get("onOff").toString());
			lcs.setCreateTime(TimeUtil.getCurrentTime());
			lcs.setStartTime(condition.get("startTime").toString());
			lcs.setEndTime(condition.get("endTime").toString());
			lcs.setDelay(Integer.parseInt(condition.get("delay").toString()));
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// DayOfWeek 일 경우 HHmmss 만 표시
		lcs.setScheduleType(condition.get("scheduleType").toString());
		ScheduleType type = ScheduleType.valueOf(condition.get("scheduleType").toString());
		if(type.equals(ScheduleType.Immediately)){		// On-Demand
			lcs.setRunTime(Integer.parseInt(condition.get("runTime").toString())); // 1~24
			
		}else if(type.equals(ScheduleType.Date)){		// Date
			
		}else if(type.equals(ScheduleType.DayOfWeek)){	// DayOfWeek
			int index = Integer.parseInt(condition.get("weekDay").toString());
			WeekDay[] 	weekDays = WeekDay.values();
			WeekDay 	day 	 = weekDays[index];
			
			lcs.setWeekDay(day.name());						// 선택한 요일
			log.debug("====day.getName["+day.name()+"]");
		}
		
		// 새로운 스케쥴 추가
		return loadControlScheduleDao.add(lcs);
	}
	
	public LoadLimitSchedule addLoadLimitSchedule(Map<String, Object> condition){
		log.debug("==========addLoadLimitSchedule==============");
		
		LoadLimitSchedule lls = new LoadLimitSchedule();
		ScheduleType type = ScheduleType.valueOf(condition.get("scheduleType").toString());
		Set<String> keys = condition.keySet();
		
		for(String k:keys){
			log.debug("======Key["+k+"], value["+condition.get(k).toString()+"]");
		}
		
		try {
			lls.setTarget(condition.get("targetId").toString());
			lls.setTargetType(condition.get("targetType").toString());
			lls.setScheduleType(condition.get("scheduleType").toString());
			
			lls.setLimitType(condition.get("limitType").toString());
			lls.setLimit(Double.parseDouble(condition.get("limit").toString()));
			
			lls.setPeakType(condition.get("peakType").toString());
			lls.setCreateTime(TimeUtil.getCurrentTime());
			
			lls.setStartTime(condition.get("startTime").toString());
			lls.setEndTime(condition.get("endTime").toString());
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ScheduleType scheduleType = ScheduleType.valueOf(condition.get("scheduleType").toString());
		if(scheduleType.equals(ScheduleType.Immediately)){
			
		}else if(scheduleType.equals(ScheduleType.Date)){
			
		}else if(scheduleType.equals(ScheduleType.DayOfWeek)){
			int index = Integer.parseInt(condition.get("weekDay").toString());
			WeekDay[] 	weekDays = WeekDay.values();
			WeekDay 	day 	 = weekDays[index];
			lls.setWeekDay(day.name());
		}
		
		return loadLimitScheduleDao.add(lls);
	}
	
	@SuppressWarnings("unchecked")
	public LoadShedSchedule addLoadShedSchedule(Map<String, Object> condition){
		log.debug("==========addLoadShedSchedule==============");
		log.debug("targetId["+condition.get("targetId").toString()+"], targetType["+condition.get("targetType").toString()+"], " +
				"scheduleType["+condition.get("scheduleType").toString()+"], onOffType["+condition.get("onOffType").toString()+"], " +
				"startTime["+condition.get("startTime").toString()+"], endTime["+condition.get("endTime")+"], " +
				"supplyCapacity["+condition.get("supplyCapacity").toString()+"], supplyThreshold["+condition.get("supplyThreshold").toString()+"]");

		LoadShedSchedule lss = new LoadShedSchedule();
		LoadShedGroup group = this.getLoadShedGroup(condition.get("targetId").toString());
		
		try {
			lss.setTarget(group);// getLoadShedGroup() 같은 id로 그룹을 찾는 메소드 필요.
			lss.setScheduleType(condition.get("scheduleType").toString());
			lss.setOnOff(condition.get("onOffType").toString());
			lss.setCreateTime(TimeUtil.getCurrentTime());
			lss.setStartTime(condition.get("startTime").toString());
			lss.setEndTime(condition.get("endTime").toString());

			group.setSupplyCapacity(Double.parseDouble(condition.get("supplyCapacity").toString()));
			group.setSupplyThreshold(Double.parseDouble(condition.get("supplyThreshold").toString()));
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return loadShedScheduleDao.add(lss);
	}

	/**
	 * Delete Load Control Schedules
	 */
	@SuppressWarnings("unchecked")
	public void deleteLoadControlSchedule(String[] targetIds){
		log.debug("====== deleteLoadControlSchedule ======");
		for(String s:targetIds){
			log.debug("deleted ID[" + loadControlScheduleDao.deleteById(Integer.parseInt(s)) + "]");
		}
	}
	
	/**
	 * Delete Load Limit Schedules
	 */
	@SuppressWarnings("unchecked")
	public void deleteLoadLimitSchedule(String[] targetIds){
		log.debug("====== deleteLoadLimitSchedule ======");
		for(String s:targetIds){
			log.debug("delete ID[" + loadLimitScheduleDao.deleteById(Integer.parseInt(s)) + "]");
		}
	}
	
	/**
	 * Delete Load Shed Schedules
	 */
	@SuppressWarnings("unchecked")
	public void deleteLoadShedSchedule(String[] targetIds){
		log.debug("====== deleteLoadShedSchedule ======");
		for(String s:targetIds){
			log.debug("delete ID[" + loadShedScheduleDao.deleteById(Integer.parseInt(s)) + "]");
		}
	}
	
	/**
	 * Delete Load Shed Groups
	 */
	@SuppressWarnings("unchecked")
	public int deleteGroups(List<Object> groups){
		log.debug("==== deleteGroups called====");
		int returnCnt = 0;
		Map<String, Object> g = null;

		for(Object obj:groups){
			g = (Map<String, Object>)obj;
			if(g.get("groupId") != null) {
				log.debug("delete groupMemeber id["+(Integer)g.get("id")+"]");
				returnCnt += groupMemberDao.deleteById((Integer)g.get("id"));
			}
			else {
				log.debug("delete loadShedGroup id["+(Integer)g.get("id")+"]");
				returnCnt += loadShedGroupDao.deleteById((Integer)g.get("id"));
			}
		}
		return returnCnt;
	}
}
