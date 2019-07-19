/**
 * ReportParameterDataDao.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.ReportParameterData;

/**
 * ReportParameterDataDao.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 10. 6.   v1.0       문동규   최초생성
 * </pre>
 */
public interface ReportParameterDataDao extends GenericDao<ReportParameterData, Integer> {

    /**
     * method name : getReportParameterDataBySchedule<b/>
     * method Desc : ReportSchedule Id 로 ReportParameterData 를 조회한다.
     *
     * @param scheduleId ReportParameterData.reportSchedule.id
     * @return List of Map {
     * 						ReportParameterData.reportParameter.parameterType AS parameterType,
     * 						ReportParameterData.value AS parameterData
     * 						}
     */
    public List<Map<String, Object>> getReportParameterDataBySchedule(Integer scheduleId);

}
