/**
 * ReportDao.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Report;

public interface ReportDao extends GenericDao<Report, Integer> {
    
    /**
     * method name : getReportResultList<b/>
     * method Desc : Report 관리 화면에서 Report Tree Root 데이터를 조회한다.
     *
     * @param conditionMap
	 * <ul>
	 * <li> roleId : ReportRole.role.id
	 * </ul>
     * @return List of Map {
     * 						ReportRole.report.id AS reportId,
     * 						ReportRole.report.name AS reportName,
     * 						ReportRole.report.description AS description,
     * 						ReportRole.report.parent.id AS parentId,
     * 						ReportRole.report.metaLink AS metaLink,
     * 						ReportRole.report.categoryItem AS categoryItem
     * 					}
     */
    public List<Map<String, Object>> getReportTreeRootList(Map<String, Object> conditionMap);

    /**
     * method name : getReportTreeChildList<b/>
     * method Desc : Report 관리 화면에서 Report Tree Child 데이터를 조회한다.
     *
     * @param roleId ReportRole.role.id
     * @param parentId ReportRole.report.parent.id
     * @return List Of Map { 
     * 						ReportRole.report.id AS reportId,
     * 						ReportRole.report.name AS reportName,
     * 						ReportRole.report.description AS description,
     * 						ReportRole.report.parent.id AS parentId,
     * 						ReportRole.report.metaLink AS metaLink,
     * 						ReportRole.report.categoryItem AS categoryItem
     * 						}
     */
    public List<Map<String, Object>> getReportTreeChildList(Integer roleId, Integer parentId);

}
