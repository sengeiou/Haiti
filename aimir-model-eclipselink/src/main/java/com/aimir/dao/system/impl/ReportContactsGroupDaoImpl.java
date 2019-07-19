/**
 * ReportContactsGroupDaoImpl.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.ReportContactsGroupDao;
import com.aimir.model.system.ReportContactsGroup;
import com.aimir.util.Condition;

/**
 * ReportContactsGroupDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 10. 13.   v1.0       문동규   최초생성
 * </pre>
 */
@Repository(value = "reportContactsGroupDao")
public class ReportContactsGroupDaoImpl extends AbstractJpaDao<ReportContactsGroup, Integer> implements ReportContactsGroupDao {

	public ReportContactsGroupDaoImpl() {
		super(ReportContactsGroup.class);
	}

    @Override
    public List<Map<String, Object>> getReportContactsGroupComboData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<ReportContactsGroup> getPersistentClass() {
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
