/**
 * ReportContactsDao.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.ReportContacts;

/**
 * ReportContactsDao.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 9. 27.   v1.0       문동규   최초생성
 * </pre>
 */
public interface ReportContactsDao extends GenericDao<ReportContacts, Integer> {

    /**
     * method name : getReportContactsList<b/>
     * method Desc : Report 관리에서 Email Contacts 리스트를 조회한다.
     *
     * @param conditionMap
	 * <ul>
	 * <li> operatorId : Operator.id
	 * <li> searchType : group "group" or name means ""
	 * <li> searchValue : ReportContacts.name
	 * <li> page : page number
	 * <li> limit : data count
	 * </ul>
     * 
     * @param isCount total count 여부
     * @return List of Map if isCount is true then return {total, count}
     *                     else return {
								ReportContact.id AS contactsId,
								ReportContact.name AS name,
								ReportContact.email AS email,
								ReportContact.group.id AS groupId,
								ReportContact.group.name AS groupName
     *                     }    
     */
    public List<Map<String, Object>> getReportContactsList(Map<String, Object> conditionMap, boolean isCount);

}
