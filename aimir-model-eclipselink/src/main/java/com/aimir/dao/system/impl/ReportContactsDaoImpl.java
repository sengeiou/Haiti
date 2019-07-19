/**
 * ReportContactsDaoImpl.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.ReportContactsDao;
import com.aimir.model.system.ReportContacts;
import com.aimir.util.Condition;

/**
 * ReportContactsDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 9. 27.   v1.0       문동규   최초생성
 * </pre>
 */
@Repository(value = "reportContactsDao")
public class ReportContactsDaoImpl extends AbstractJpaDao<ReportContacts, Integer> implements ReportContactsDao {

	public ReportContactsDaoImpl() {
		super(ReportContacts.class);
	}

    @Override
    public List<Map<String, Object>> getReportContactsList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<ReportContacts> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

}
