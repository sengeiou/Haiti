/**
 * ReportContactsGroupDao.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.ReportContactsGroup;

/**
 * ReportContactsGroupDao.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 10. 13.   v1.0       문동규   최초생성
 * </pre>
 */
public interface ReportContactsGroupDao extends GenericDao<ReportContactsGroup, Integer> {

    /**
     * method name : getReportContactsGroupComboData<b/>
     * method Desc : Report 관리에서 Email Contacts Group Combo Data 를 조회한다.
     *
     * @param conditionMap
	 * <ul>
	 * <li> operatorId : ReportContactsGroup.operator.id
	 * </ul>
     * @return List of Map {
     * 						ReportContactsGroup.id AS id,
     * 						ReportContactsGroup.name AS name }
     * 
     */
    public List<Map<String, Object>> getReportContactsGroupComboData(Map<String, Object> conditionMap);

}
