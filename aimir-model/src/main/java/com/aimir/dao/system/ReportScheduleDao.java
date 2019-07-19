/**
 * ReportScheduleDao.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.ReportSchedule;

/**
 * ReportScheduleDao.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 10. 6.   v1.0       문동규   최초생성
 * </pre>
 */
public interface ReportScheduleDao extends GenericDao<ReportSchedule, Integer> {

    /**
     * method name : getReportScheduleList<b/>
     * method Desc : Report 관리 맥스가젯에서 스케줄 리스트를 조회한다.
     *
     * @param conditionMap
     * <ul>
	 * <li> operatorId : Operator.id
	 * <li> reportId : Report.id
	 * <li> page : page number
	 * <li> limit : data count
	 * </ul>
	 * 
     * @param isCount total count 여부
     * @return List of Map if isCount is true then return {total, count}
     *                     else return {
     *                                  ReportSchedule.id AS scheduleId,
     *                                  ReportSchedule.name AS scheduleName,
     *                                  ReportSchedule.used AS used,
     *                                  ReportSchedule.cronFormat AS cronFormat,
     *                                  ReportSchedule.exportFormat AS exportFormat,
     *                                  ReportResult.writeTime AS writeTime,
     *                                  ReportSchedule.email AS email 
     *                     }
     */
    public List<Map<String, Object>> getReportScheduleList(Map<String, Object> conditionMap, boolean isCount);

}
