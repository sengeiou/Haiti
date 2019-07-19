package com.aimir.schedule.task;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.ScheduleJobErrorMsg;

public abstract class ScheduleTask {
    public Map<String, Object> scheduleResult;
    public abstract void execute(JobExecutionContext context);

    /**
     * method name : executeTask<b/>
     * method Desc : JobExecutionContext 객체를 받아서 결과값을 저장하는 메소드
     *
     * @param context
     */
    public void executeTask(JobExecutionContext context) {
        scheduleResult = new HashMap<String, Object>();
        
        execute(context);
        
        if (!scheduleResult.containsKey("result")) {
            setFailResult();
        }
        
        context.put("scheduleResult", scheduleResult);
    }

    /**
     * method name : executeTask<b/>
     * method Desc : 결과값을 저장한 Map 객체를 리턴하는 메소드
     *
     * @return
     */
    public Map<String, Object> executeTask() {
        scheduleResult = new HashMap<String, Object>();
        
        execute(null);
        
        if (!scheduleResult.containsKey("result")) {
            setFailResult();
        }
        return scheduleResult;
    }

    /**
     * method name : setSuccessResult<b/>
     * method Desc : 실행 결과값을 성공으로 저장한다.
     *
     */
    public void setSuccessResult() {
        scheduleResult.put("result", ResultStatus.SUCCESS.name());
    }

    /**
     * method name : setFailResult<b/>
     * method Desc : 실행 결과값을 실패로 저장하고 Default 에러메세지를 저장한다.
     *
     */
    public void setFailResult() {
        setFailResult(ScheduleJobErrorMsg.TASK_EXECUTE_ERROR.getMessage());
    }

    /**
     * method name : setFailResult<b/>
     * method Desc : 실행 결과값을 실패로 저장하고 주어진 에러메세지를 저장한다.
     *
     * @param errorMessage
     */
    public void setFailResult(String errorMessage) {
        scheduleResult.put("result", ResultStatus.FAIL.name());
        scheduleResult.put("errorMessage", errorMessage);
    }

    //    /**
//     * method name : executeTask<b/>
//     * method Desc : Map 객체를 받아서 결과값을 저장 후 리턴하는 메소드
//     *
//     * @param resultMap
//     * @return
//     */
//    public Map<String, Object> executeTask(Map<String, Object> resultMap) {
//        scheduleResult = new HashMap<String, Object>();
//
//        execute();
//
//        if (scheduleResult.containsKey("result")) {
////            scheduleResult.put("result", ResultStatus.SUCCESS.name());
//            resultMap.put("result", scheduleResult.get("result"));
//        } else {
////            resultMap.put("result", ResultStatus.SUCCESS.name());
//            resultMap.put("result", scheduleResult.get("result"));
//        }
////        if (!scheduleResult.containsKey("result")) {
////            scheduleResult.put("result", ResultStatus.FAIL.name());
////            scheduleResult.put("errorMessage", ScheduleJobErrorMsg.TASK_EXECUTE_ERROR.getMessage());
////        }
//        return scheduleResult;
//    }

}