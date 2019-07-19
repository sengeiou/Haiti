package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.ScheduleResultLog;

public interface ScheduleResultLogDao extends GenericDao<ScheduleResultLog, Long>{

    /**
     * method name : getLatestScheduleResultLogByTrigger
     * method Desc : 트리거 이름으로 조회하여 마지막에 수행한 스케줄 결과 목록을 리턴
     *
     * @param triggerName
     * @return List Of Object {operator, result}
     */
    @Deprecated
    public List<Object> getLatestScheduleResultLogByTrigger(String triggerName);

    /**
     * method name : getLatestScheduleResultLogByJobTrigger
     * method Desc : 트리거 이름으로 조회하여 마지막에 수행한 스케줄 결과 목록을 리턴
     *
     * @param conditionMap
     * @return List Of Object {operator, result}
     */
    public List<Object> getLatestScheduleResultLogByJobTrigger(Map<String, Object> conditionMap);

    /**
     * method name : getScheduleResultLogByJobName<b/>
     * method Desc : TaskManagement 맥스가젯에서 스케줄러 실행 결과로그를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         String searchStartDate = (String)conditionMap.get("searchStartDate");
     *         String searchEndDate   = (String)conditionMap.get("searchEndDate");
     *         String jobName     = (String)conditionMap.get("jobName");
     *         String triggerName = (String)conditionMap.get("triggerName");
     *         Integer result     = (Integer)conditionMap.get("result");
     *         Integer page       = (Integer)conditionMap.get("page");
     *          Integer pageSize   = (Integer)conditionMap.get("pageSize");
     *
     * @param isTotal 카운트를 구하는지 여부 true이면 데이터 목록 전체를 구하는것이 아니라 카운트를 구한다.
     * @return List of Map if isTotal is true then return  {total, count}
     *                     else return
     *          {ScheduleResultLog.responseTime AS responseTime,
     *           ScheduleResultLog.createTime AS createTime,
     *           ScheduleResultLog.jobName AS jobName,
     *           ScheduleResultLog.triggerName AS triggerName,
     *           ScheduleResultLog.result AS result,
     *           ScheduleResultLog.errorMessage AS errorMessage}
     */
    public List<Map<String, Object>> getScheduleResultLogByJobName(Map<String, Object> conditionMap, boolean isTotal);
}