/**
 * ReportScheduleDaoImpl.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.ReportScheduleDao;
import com.aimir.model.system.ReportSchedule;
import com.aimir.util.Condition;

/**
 * ReportScheduleDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 10. 6.   v1.0       문동규   최초생성
 * </pre>
 */
@Repository(value = "reportScheduleDao")
public class ReportScheduleDaoImpl extends AbstractJpaDao<ReportSchedule, Integer> implements ReportScheduleDao {

	public ReportScheduleDaoImpl() {
		super(ReportSchedule.class);
	}

    @Override
    public List<Map<String, Object>> getReportScheduleList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<ReportSchedule> getPersistentClass() {
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
