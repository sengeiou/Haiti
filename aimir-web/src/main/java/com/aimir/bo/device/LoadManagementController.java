package com.aimir.bo.device;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.ScheduleType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.LoadControlSchedule;
import com.aimir.model.device.LoadLimitSchedule;
import com.aimir.model.device.LoadShedGroup;
import com.aimir.model.device.LoadShedSchedule;
import com.aimir.model.device.LoadShedScheduleVO;
import com.aimir.model.system.Role;
import com.aimir.service.device.LoadMgmtManager;
import com.aimir.service.system.RoleManager;
import com.aimir.util.CommonUtils;
import com.aimir.util.TimeUtil;

@Controller
public class LoadManagementController {

    private static Log log = LogFactory.getLog(LoadManagementController.class);

    @Autowired
    LoadMgmtManager loadMgmtManager;

    @Autowired
    RoleManager roleManager;

    @RequestMapping(value="/gadget/device/loadManagementMiniGadget")
    public ModelAndView getLoadManagementMini(){
        log.info("==== MAV getLoadManagementMini ready");
        ModelAndView mav = new ModelAndView("/gadget/device/loadManagementMiniGadget");

        List<Object> groupType  = loadMgmtManager.getGroupTypeCombo();
        mav.addObject("groupType", groupType);

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        return mav;
    }

    @RequestMapping(value="/gadget/device/loadManagementMaxGadget")
    public ModelAndView getLoadManagementMax(){
        log.info("==== MAV getLoadManagementMax ready");
        ModelAndView mav = new ModelAndView("/gadget/device/loadManagementMaxGadget");

        // SELECT BOX
        List<Object> groupType  = loadMgmtManager.getGroupTypeCombo();
        List<Object> loadType   = loadMgmtManager.getLoadTypeCombo();
        List<Object> weekDay    = loadMgmtManager.getWeekDayCombo();
        List<Object> hourCombo  = loadMgmtManager.getHourCombo();
        List<Object> minuteCombo= loadMgmtManager.getMinuteCombo();
        List<Object> onOffCombo = loadMgmtManager.getOnOffCombo();
        List<Object> limitTypeCombo = loadMgmtManager.getLimitTypeCombo();
        List<Object> peakTypeCombo  = loadMgmtManager.getPeakTypeCombo();

        mav.addObject("groupType", groupType);
        mav.addObject("loadType", loadType);
        mav.addObject("weekDayCombo", weekDay);
        mav.addObject("hourCombo", hourCombo);
        mav.addObject("minuteCombo", minuteCombo);
        mav.addObject("onOffCombo", onOffCombo);
        mav.addObject("limitTypeCombo", limitTypeCombo);
        mav.addObject("peakTypeCombo", peakTypeCombo);

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        return mav;
    }

    /**
     * 위치 : LoadShed 탭, 상단의 Load Shed List 목록 조회 결과 출력
     * @param groupType
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value="/gadget/device/getLoadShedListBySchedule.do", params="param=searchList")
    public ModelAndView getLoadShedList(@RequestParam("supplierId") String supplierId,
                                        @RequestParam("operatorId") String operatorId,
                                        @RequestParam("groupType")  String groupType,
                                        @RequestParam("startDate")  String startDate,
                                        @RequestParam("endDate")    String endDate){
        ModelAndView mav = new ModelAndView("jsonView");

        log.debug("=================Controller : getLoadShedList called!! =====");

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplierId", supplierId);
        condition.put("operatorId", operatorId);
        condition.put("groupType", groupType);
        condition.put("startDate", startDate);
        condition.put("endDate",   endDate);

        log.debug("supplierId["+supplierId+"], operatorId["+operatorId+"], groupType["+groupType+"], " +
                "startDate["+startDate+"], endDate["+endDate+"]");

        //Map<String, Object> result = loadMgmtManager.getLoadShedGroupBySchedule(condition);
        List<LoadShedScheduleVO> result = loadMgmtManager.getLoadShedGroupBySchedule(condition);

        mav.addObject("grid", result);

        return mav;
    }

    /**
     * LoadShedGroup 중 스케쥴이 없는 목록 반환. LoadShed탭 하단에서 사용
     * @param groupType 그룹 타입(Operator, Location, etc..)
     * @param groupName 검색하려는 그룹 명
     * @return
     */
    @RequestMapping(value="/gadget/device/getLoadShedGroupsWithoutSchedule.do")
    public ModelAndView getLoadShedGroupsWithoutSchedule(@RequestParam String groupType,
                                                         @RequestParam String groupName){
        ModelAndView mav = new ModelAndView("jsonView");
        log.debug("============== getLoadShedGruopWithoutSchedule called ==============");
        log.debug("groupType["+groupType+"], groupName["+groupName+"]");

        List<LoadShedGroup> list = loadMgmtManager.getLoadShedGroupWithoutSchedule(groupType, groupName);
        Map<String, Object> map = null;
        List<Object> returnList = new ArrayList<Object>();

        for(LoadShedGroup g : list){
            map = new HashMap<String, Object>();
            log.debug("groupId["+g.getId()+"], groupName["+g.getName()+"]");
            map.put("groupId", g.getId());
            map.put("groupName", g.getName());
            returnList.add(map);
        }
        mav.addObject("scheduleList", returnList);
        return mav;
    }

    /**
     *
     * @param groupType 그룹 종류(Operation, Location, etc..)
     * @param groupName 검색할 그룹 명
     * @return 검색 조건에 해당되는 그룹명, 그룹ID 리스트를 json 형식으로 반환한다.
     */
    @RequestMapping(value="/gadget/device/getLoadShedGroupMembers.do")
    public ModelAndView getLoadShedGroupMembers(@RequestParam("groupType") String targetType,
                                                @RequestParam("groupName") String targetName){
        log.debug("======= getLoadShedGroupMembers. targetType["+targetType+"], targetName["+targetName+"]");
        ModelAndView mav = new ModelAndView("jsonView");

        List<Object> list = loadMgmtManager.getLoadShedGroupMembers(targetType, targetName);
        log.debug("list size : " + list.size());

        List<Object> returnList = new ArrayList<Object>();
        Map<String, Object> map = null;
        Object[] obj = null;

        for(int i=0; i<list.size(); i++){
            obj = (Object[])list.get(i);
            log.debug(i+") targetType["+obj[0].toString()+"], targetName["+obj[1].toString()+"]");

            map = new HashMap<String, Object>();
            map.put("targetId", obj[0].toString());
            map.put("targetName", obj[1].toString());
            //obj[2] 는 groupId
            returnList.add(map);
        }
        mav.addObject("scheduleList", returnList);

        return mav;
    }

    /**
     * Load Control Schedule 목록을 조회하는 메서드.
     * Load Control 탭에서 왼쪽의 그룹 검색 결과에서 하나의 그룹을 선택할 경우 해당 그룹의 스케쥴 목록을 오른쪽 화면에 보여준다.
     * @param groupId 왼쪽 리스트박스에서 선택한 그룹의 ID
     * @return 오른쪽에 표시할 JSON 형식의 스케쥴 목록
     */
    @RequestMapping(value="/gadget/device/getLoadControlScheduleList.do")
    public ModelAndView getLoadControlScheduleList(@RequestParam("targetId") String targetId){
        log.debug("=== getLoadControlScheduleList called and targetId["+targetId+"]");

        ModelAndView mav = new ModelAndView("jsonView");
        List<LoadControlSchedule> list = loadMgmtManager.getLoadControlSchedule(targetId);
        mav.addObject("scheduleList", list);
        return mav;
    }

    @RequestMapping(value="/gadget/device/getLoadControlScheduleListByDate.do")
    public ModelAndView getLoadControlScheduleListByDate(@RequestParam("targetId") String targetId){
        log.debug("=== getLoadControlScheduleListByDate called and targetId["+targetId+"]");

        ModelAndView mav = new ModelAndView("jsonView");
        List<LoadControlSchedule> list = loadMgmtManager.getLoadControlScheduleByDate(targetId);
        mav.addObject("scheduleList", list);
        return mav;
    }

    @RequestMapping(value="/gadget/device/getLoadControlScheduleListByWeekday.do")
    public ModelAndView getLoadControlScheduleListByWeekday(@RequestParam("targetId") String targetId){
        log.debug("=== getLoadControlScheduleListByWeekday called and targetId["+targetId+"]");

        ModelAndView mav = new ModelAndView("jsonView");
        List<LoadControlSchedule> list = loadMgmtManager.getLoadControlScheduleByWeekday(targetId);
        mav.addObject("scheduleList", list);
        return mav;
    }


    /**
     * Load Limit 탭에서 왼쪽의 그룹 검색 결과에서 하나의 그룹을 선택할 경우 해당 그룹의
     * 스케쥴 목록을 오른쪽 화면에 보여준다.
     * @param groupId 왼쪽 리스트박스에서 선택한 그룹의 ID
     * @return 오른쪽에 표시할 JSON 형식의 스케쥴 목록
     */
    @RequestMapping(value="/gadget/device/getLoadLimitScheduleList.do")
    public ModelAndView getLoadLimitScheduleList(@RequestParam("targetId") String targetId){
        log.debug("=== getLoadControlScheduleList called and targetId["+targetId+"]");

        ModelAndView mav = new ModelAndView("jsonView");
        List<LoadLimitSchedule> list = loadMgmtManager.getLoadLimitSchedule(targetId);
        mav.addObject("scheduleList", list);
        return mav;
    }

    @RequestMapping(value="/gadget/device/getLoadLimitScheduleListByDate.do")
    public ModelAndView getLoadLimitScheduleListByDate(@RequestParam("targetId") String targetId){
        log.debug("=== getLoadControlScheduleList called and targetId["+targetId+"]");

        ModelAndView mav = new ModelAndView("jsonView");
        List<LoadLimitSchedule> list = loadMgmtManager.getLoadLimitScheduleByDate(targetId);
        mav.addObject("scheduleList", list);
        return mav;
    }

    @RequestMapping(value="/gadget/device/getLoadLimitScheduleListByWeekday.do")
    public ModelAndView getLoadLimitScheduleListByWeekday(@RequestParam("targetId") String targetId){
        log.debug("=== getLoadControlScheduleList called and targetId["+targetId+"]");

        ModelAndView mav = new ModelAndView("jsonView");
        List<LoadLimitSchedule> list = loadMgmtManager.getLoadLimitScheduleByWeekday(targetId);
        mav.addObject("scheduleList", list);
        return mav;
    }

    /**
     * Load Shed 탭에서 목록을 선택하였을 경우 호출.
     * 선택한 그룹에 해당하는 스케쥴 목록을 가져와서 json 형식으로 리턴
     * @param groupId 사용자가 플렉스의 Datagrid 에서 선택한 그룹의 ID
     * @return 선택한 그룹에 속한 모든 스케쥴 목록
     */
    @RequestMapping(value="/gadget/device/getLoadShedScheduleList.do")
    public ModelAndView getLoadShedScheduleList(@RequestParam("groupId") Integer targetId){
        log.debug("=== getLoadShedScheduleList called and targetId["+targetId+"]");

        ModelAndView mav = new ModelAndView("jsonView");
        List<LoadShedSchedule> list = loadMgmtManager.getLoadShedSchedule(targetId);
        mav.addObject("scheduleList", list);
        return mav;
    }

    @RequestMapping(value="/gadget/device/getLoadShedScheduleListByDate.do")
    public ModelAndView getLoadShedScheduleByDate(@RequestParam("groupId") Integer targetId){
        log.debug("=== getLoadShedScheduleList called and targetId["+targetId+"]");

        ModelAndView mav = new ModelAndView("jsonView");
        List<LoadShedSchedule> list = loadMgmtManager.getLoadShedScheduleByDate(targetId);
        mav.addObject("scheduleList", list);
        return mav;
    }

    @RequestMapping(value="/gadget/device/getLoadShedScheduleListByWeekday.do")
    public ModelAndView getLoadShedScheduleByWeek(@RequestParam("groupId") Integer targetId){
        log.debug("=== getLoadShedScheduleList called and targetId["+targetId+"]");

        ModelAndView mav = new ModelAndView("jsonView");
        List<LoadShedSchedule> list = loadMgmtManager.getLoadShedScheduleByWeekday(targetId);
        mav.addObject("scheduleList", list);
        return mav;
    }



    /**
     * Load Control 탭에서 스케쥴을 추가하면 해당 내용을 DB에 추가. 변경된 스케쥴 목록을 반환
     * @param condition
     * @return 스케쥴 목록
     * @throws ParseException
     */
    @RequestMapping(value="/gadget/device/addLoadControlSchedule.do")
    public ModelAndView addLoadControlSchedule(@RequestParam("targetType") String targetType,
                                            @RequestParam("targetId")      String targetId,
                                            @RequestParam("scheduleType")  String scheduleType,
                                            @RequestParam("onOff")         String onOff,
                                            @RequestParam(value="startTime", required=false)    String startTime,
                                            @RequestParam(value="endTime", required=false) String endTime,
                                            @RequestParam(value="weekDay", required=false) String weekDay,
                                            @RequestParam(value="runTime", required=false) Integer runTime,
                                            @RequestParam(value="delay",   required=false) Integer delay) throws ParseException{

        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> condition = new HashMap<String, Object>();

        log.debug("targetId["+targetId+"], targetType["+targetType+"], scheduleType["+scheduleType+"], ofOff["+onOff+"], startTime["+startTime+"], endTime["+endTime+"]");

        condition.put("targetType", targetType);
        condition.put("targetId", targetId);
        condition.put("scheduleType", scheduleType);
        condition.put("onOff", onOff);

        if(scheduleType.equals(ScheduleType.Immediately.name())){
            log.debug("=======scheduleType : Immedately========");
            log.debug("delay["+runTime+"], runTime["+runTime+"]");
            log.debug("starTime["+TimeUtil.getAddedHour(TimeUtil.getCurrentTime(), delay)+"], endTime[]");
            condition.put("runTime", runTime);
            condition.put("delay", delay);

            // 현재 시간 + delay
            startTime = TimeUtil.getAddedHour(TimeUtil.getCurrentTime(), delay);
            condition.put("startTime", startTime);

            // startTime + runTime
            condition.put("endTime", TimeUtil.getAddedHour(startTime, runTime));
            log.debug("==== runTime["+runTime+"], startTime["+startTime+"], endTime["+TimeUtil.getAddedHour(startTime, runTime)+"]");

        }else if(scheduleType.equals(ScheduleType.Date.name())){
            log.debug("=======scheduleType : Date========");
            condition.put("delay", 0);
            condition.put("startTime", startTime);
            condition.put("endTime", endTime);

        }else if(scheduleType.equals(ScheduleType.DayOfWeek.name())){
            log.debug("=======scheduleType : DayOfWeek========");
            log.debug("==weekday["+weekDay+"]");
            condition.put("delay", 0);
            condition.put("startTime", "00000000" + startTime); // YYYYMMDD 없음
            condition.put("endTime", "00000000" + endTime);
            condition.put("weekDay", weekDay);
        }

        // 해당 스케쥴을 추가
        loadMgmtManager.addLoadControlSchedule(condition);

        // 목록을 다시 불러옴
        //List<LoadLimitSchedule> list = loadMgmtManager.getLoadLimitSchedule(condition.get("target").toString());
        //mav.addObject("scheduleList", list);
        return mav;
    }

    @RequestMapping(value="/gadget/device/addLoadLimitSchedule.do")
    public ModelAndView addLoadLimitSchedule(@RequestParam("targetId")      String targetId,
                                            @RequestParam("targetType")     String targetType,
                                            @RequestParam("scheduleType")   String scheduleType,
                                            @RequestParam("limitType")      String limitType,
                                            @RequestParam("limit")          Double limit,
                                            @RequestParam(value="peakType",  required=false) String peakType,
                                            @RequestParam(value="startTime", required=false) String startTime,
                                            @RequestParam(value="endTime",   required=false) String endTime,
                                            @RequestParam(value="openPeriod",required=false) Integer openPeriod,
                                            @RequestParam(value="weekDay",   required=false) String weekDay) throws ParseException{
        log.debug("targetId["+targetId+"], targetType["+targetType+"], scheduleType["+scheduleType+"], limitType["+limitType+"], limit["+limit+"], peakType["+peakType+"]");

        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("targetId", targetId);
        condition.put("targetType", targetType);
        condition.put("scheduleType", scheduleType);
        String msg = "";
        if(limitType != null && limitType.length() != 0){
            condition.put("limitType", limitType);
            msg += " limitType["+limitType+"]";
        }
        if(limit != null){
            condition.put("limit", limit);
            msg += " limit["+limit+"]";
        }
        if(peakType != null && peakType.length() != 0){
            condition.put("peakType", peakType);
            msg += " peakType["+peakType+"]";
        }
        if(openPeriod != null){
            condition.put("openPeriod", openPeriod);
            msg += " openPeriod["+openPeriod+"]";
        }

        // 스케쥴 타입별 변수
        if(scheduleType.equals(ScheduleType.Immediately.name())){
            condition.put("startTime", TimeUtil.getCurrentTime());
            condition.put("endTime", TimeUtil.getCurrentTime());    // End Time 어떻게 할건지 결정 필요

        }else if(scheduleType.equals(ScheduleType.Date.name())){
            condition.put("startTime", startTime);
            condition.put("endTime", endTime);

        }else if(scheduleType.equals(ScheduleType.DayOfWeek.name())){
            condition.put("startTime", "00000000" + startTime);
            condition.put("endTime", "00000000" +endTime);
            condition.put("weekDay", weekDay);
            msg += " weekDay["+weekDay+"]";
        }
        msg += " startTime["+condition.get("startTime").toString()+"], endTime["+condition.get("endTime").toString()+"]";
        log.debug(msg);
        loadMgmtManager.addLoadLimitSchedule(condition);
        return mav;
    }

    /**
     * Load Control 탭에서 스케쥴을 추가하면 해당 내용을 DB에 추가. 변경된 스케쥴 목록을 반환
     * @param condition
     * @return 스케쥴 목록
     * @throws ParseException
     */
    @RequestMapping(value="/gadget/device/addLoadShedSchedule.do")
    public ModelAndView addLoadShedSchedule(@RequestParam("targetId")     String targetId,
                                            @RequestParam("scheduleType") String scheduleType,
                                            @RequestParam("onOff")        String onOff,
                                            @RequestParam(value="startTime", required=false) String startTime,
                                            @RequestParam(value="endTime",   required=false) String endTime,
                                            @RequestParam(value="weekDay",   required=false) String weekDay) throws ParseException{
        log.debug("targetId["+targetId+"], scheduleType["+scheduleType+"], onOff["+onOff+"], " +
                "startTime["+startTime+"], endTime["+endTime+"] ");

        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> condition = new HashMap<String, Object>();

        condition.put("targetId", targetId);
        condition.put("scheduleType", scheduleType);
        condition.put("onOffType", onOff);

        if(scheduleType.equals(ScheduleType.Immediately.name())){// ondemand
            condition.put("startTime", TimeUtil.getCurrentTime());
            condition.put("endTime", TimeUtil.getCurrentTime());

        }else if(scheduleType.equals(ScheduleType.Date.name())){
            condition.put("startTime", startTime);
            condition.put("endTime", endTime);
        }else if(scheduleType.equals(ScheduleType.DayOfWeek.name())){
            condition.put("startTime", "00000000"+startTime);   // YYYYMMDD 없음
            condition.put("endTime", "00000000"+endTime);

            condition.put("weekDay", weekDay);
            log.debug("weekDay["+weekDay+"]");
        }

        //condition.put("supplyCapacity", supplyCapacity);
        //condition.put("supplyThreshold", supplyThreshold);

        // 해당 스케쥴을 추가
        loadMgmtManager.addLoadShedSchedule(condition);

        // 목록을 다시 불러옴
        return mav;
    }

    @RequestMapping(value="/gadget/device/deleteLoadControlSchedule.do")
    public ModelAndView deleteLoadControlSchedule(@RequestParam("scheduleList") String targetIds){
        log.debug("====== targetIds ["+targetIds+"] ========");
        ModelAndView mav = new ModelAndView("jsonView");

        String[] Ids = targetIds.split(",");
        for(String str:Ids){
            log.debug("==== ID["+str+"] ====");
        }

        // 삭제할 아이디 loadMgmtManager 에 전달
        loadMgmtManager.deleteLoadControlSchedule(Ids);

        return mav;
    }

    @RequestMapping(value="/gadget/device/deleteLoadLimitSchedule.do")
    public ModelAndView deleteLoadLimitSchedule(@RequestParam("scheduleList") String targetIds){
        log.debug("====== targetIds ["+targetIds+"] ========");
        ModelAndView mav = new ModelAndView("jsonView");

        String[] Ids = targetIds.split(",");
        for(String str:Ids){
            log.debug("==== ID["+str+"] ====");
        }

        // 삭제할 아이디 loadMgmtManager 에 전달
        loadMgmtManager.deleteLoadLimitSchedule(Ids);

        return mav;
    }

    @RequestMapping(value="/gadget/device/deleteLoadShedSchedule.do")
    public ModelAndView deleteLoadShedSchedule(@RequestParam("scheduleList") String targetIds){
        log.debug("====== targetIds ["+targetIds+"] ========");
        ModelAndView mav = new ModelAndView("jsonView");

        String[] Ids = targetIds.split(",");
        for(String str:Ids){
            log.debug("==== ID["+str+"] ====");
        }

        // 삭제할 아이디 loadMgmtManager 에 전달
        loadMgmtManager.deleteLoadShedSchedule(Ids);

        return mav;
    }
}
