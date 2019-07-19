package com.aimir.schedule.task;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.CodeDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.system.Code;

/**
 * ScheduleSampleTask.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2012. 2. 27.  v1.0        문동규   변경된 스케줄러 Task 샘플소스. 변경사항: 동적인 Job 등록가능 / Job 실행결과 Log 에 저장
 * </pre>
 */
@Component
@Transactional
public class ScheduleSampleTask extends ScheduleTask {

    protected static Log log = LogFactory.getLog(ScheduleSampleTask.class);

    CodeDao codeDao;

    /**
     * method name : excute<b/>
     * method Desc : 스케줄 실행결과를 ScheduleResultLog 에 저장하기 위해 실행결과 상태값을 리턴한다.
     *
     * @return task 실행 결과를 Map 형태로 리턴한다.<b/>
     *   - result : 결과상태. ex) ResultStatus.SUCCESS.name()<b/>
     *   - errorMessage : 실패일 경우 해당 에러메세지
     */
    @SuppressWarnings("unused")
    @Override
    public void execute(JobExecutionContext context) {
        log.info("++++++ ScheduleSampleTask Start ++++++");
        // Dao 호출 테스트
        Class<?> codeDaoClass = null;
        Object codeDaoObject = null;

        try {
            codeDaoClass = Class.forName("com.aimir.dao.system.CodeDao");
            codeDaoObject = DataUtil.getBean(codeDaoClass);
        } catch (ClassNotFoundException ce) {
            ce.printStackTrace();
        }
        codeDao = (CodeDao)codeDaoObject;
        List<Code> codeList = codeDao.getChildCodes("1.3.1");

        setSuccessResult();
        log.info("++++++ TestScheduleTask End ++++++");
    }
}