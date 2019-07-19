package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.ReportResultDao;
import com.aimir.model.system.ReportResult;
import com.aimir.util.Condition;

/**
 * ReportResultDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 9. 20.   v1.0       문동규   주석생성
 * </pre>
 */
@Repository(value = "reportResultDao")
public class ReportResultDaoImpl extends AbstractJpaDao<ReportResult, Integer> implements ReportResultDao {

	public ReportResultDaoImpl() {
		super(ReportResult.class);
	}

    @Override
    public List<Map<String, Object>> getReportResultList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getReportScheduleResultList(
            Map<String, Object> conditionMap, boolean isCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<ReportResult> getPersistentClass() {
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