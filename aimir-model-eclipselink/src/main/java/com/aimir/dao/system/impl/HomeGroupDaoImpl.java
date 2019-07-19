package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.HomeGroupDao;
import com.aimir.model.device.MCU;
import com.aimir.model.system.GroupMember;
import com.aimir.model.system.HomeGroup;
import com.aimir.util.Condition;

@Repository(value="homegroupDao")
public class HomeGroupDaoImpl extends AbstractJpaDao<HomeGroup, Integer> implements HomeGroupDao{

	public HomeGroupDaoImpl() {
		super(HomeGroup.class);
	}

    @Override
    public List<HomeGroup> getGroupList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getHomeGroupList(
            Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getGroupListWithChild(Integer operatorId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<GroupMember> getChildren(Integer groupId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer count() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HomeGroup getHomeGroup(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MCU getHomeGroupMcuByGroupId(Integer groupId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<HomeGroup> getPersistentClass() {
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
