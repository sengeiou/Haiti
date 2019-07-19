package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.ReportDao;
import com.aimir.model.system.Report;
import com.aimir.util.Condition;

@Repository(value = "reportDao")
public class ReportDaoImpl extends AbstractJpaDao<Report, Integer> implements ReportDao {

	public ReportDaoImpl() {
		super(Report.class);
	}

    @Override
    public List<Map<String, Object>> getReportTreeRootList(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getReportTreeChildList(Integer roleId,
            Integer parentId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Report> getPersistentClass() {
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
