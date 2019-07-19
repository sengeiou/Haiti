/**
 * ReportParameterDataDaoImpl.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.ReportParameterDataDao;
import com.aimir.model.system.ReportParameterData;
import com.aimir.util.Condition;

/**
 * ReportParameterDataDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 10. 6.   v1.0       문동규   최초생성
 * </pre>
 */
@Repository(value = "reportParameterDataDao")
public class ReportParameterDataDaoImpl extends AbstractJpaDao<ReportParameterData, Integer> implements ReportParameterDataDao {

	public ReportParameterDataDaoImpl() {
		super(ReportParameterData.class);
	}

    @Override
    public List<Map<String, Object>> getReportParameterDataBySchedule(
            Integer scheduleId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<ReportParameterData> getPersistentClass() {
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
