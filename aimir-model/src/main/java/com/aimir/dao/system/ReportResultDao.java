/**
 * ReportResultDao.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.ReportResult;

/**
 * ReportResultDao.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2010. 11. 11.   v1.0       
 * </pre>
 */
public interface ReportResultDao extends GenericDao<ReportResult, Integer> {

    /**
     * method name : getReportResultList<b/>
     * method Desc : Report 관리 미니가젯에서 스케줄 결과를 조회한다.
     *
     * @param conditionMap
     * <ul>
	 * <li> roleId : Operator.role.id
	 * <li> operatorId : Operator.id
	 * <li> reportName : page number
	 * <li> startDate : yyyyMMddHHmmss
	 * <li> endDate : yyyyMMddHHmmss
	 * <li> page : page number
	 * <li> limit : data count 
	 * </ul>
     * 
     * @param isCount total count 여부
     * @return List of Map if isCount is true then return {total,count}
     *                     else return {
     *                     				ReportResult.id AS resultId,
     *                     				ReportResult.writeTime AS writeTime,
     *                     				ReportResult.resultLink AS resultLink,
     *                     				ReportResult.resultFileLink AS resultFileLink,
     *                     				ReportResult.reportSchedule.parameterData.reportParameter.report.name AS reportName,
     *                     				ReportResult.reportSchedule.parameterData.reportParameter.report.metaLink AS metaLink,
     *                     				ReportResult.operator.name AS operatorName
     *                     }
     */
    public List<Map<String, Object>> getReportResultList(Map<String, Object> conditionMap, boolean isCount);

    /**
     * method name : getReportScheduleResultList<b/>
     * method Desc : Report 관리 맥스가젯에서 스케줄 실행 결과를 조회한다.
     *
     * @param conditionMap
     * <ul>
	 * <li> scheduleId : ReportResult.reportSchedule.id
	 * <li> startDate : yyyyMMddHHmmss
	 * <li> endDate : yyyyMMddHHmmss
	 * <li> page : page number
	 * <li> limit : data count 
	 * </ul>
	 * 
     * @param isCount total count 여부
     * @return List of Map if isCount is true then return {total,count}
     *                     else return {
     *                     				ReportResult.id AS resultId,
     *                     				ReportResult.writeTime AS writeTime,
     *                     				ReportResult.result AS result,
     *                     				ReportResult.failReason AS failReason
     *                     }
     */
    public List<Map<String, Object>> getReportScheduleResultList(Map<String, Object> conditionMap, boolean isCount);
}
