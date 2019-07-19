package com.aimir.dao.system.impl;

/**
 * Created on 16-08-17.
 */

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.GroupStrategyDao;
import com.aimir.model.system.GroupStrategy;
import com.aimir.util.Condition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository(value="groupStrategyDao")
public class GroupStrategyDaoImpl extends AbstractJpaDao<GroupStrategy, Integer> implements GroupStrategyDao {

    private static Log log = LogFactory.getLog(GroupStrategyDaoImpl.class);

    public GroupStrategyDaoImpl() {
        super(GroupStrategy.class);
    }

    @Override
    public List<Map<String, Object>> getStrategyBySupplier(Integer supplierId) {

        return null;
    }

    @Override
    public List<Object> getStrategyByGroup(Integer groupId) {
        return null;
    }

    @Override
    public List<Object> getStrategyByConfig(String configName) {
        return null;
    }

    @Override
    public Class<GroupStrategy> getPersistentClass() {
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String... groupBy) {
        return null;
    }
}
