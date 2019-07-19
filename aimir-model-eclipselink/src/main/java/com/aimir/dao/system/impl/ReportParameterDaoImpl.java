/**
 * ReportParameterDaoImpl.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.ReportParameterDao;
import com.aimir.model.system.ReportParameter;
import com.aimir.util.Condition;

/**
 * ReportParameterDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 10. 6.   v1.0       문동규   최초생성
 * </pre>
 */
@Repository(value = "reportParameterDao")
public class ReportParameterDaoImpl extends AbstractJpaDao<ReportParameter, Integer> implements ReportParameterDao {

	public ReportParameterDaoImpl() {
		super(ReportParameter.class);
	}

    @Override
    public Class<ReportParameter> getPersistentClass() {
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
