package com.aimir.dao.device.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.LoadShedGroupDao;
import com.aimir.model.device.LoadShedGroup;
import com.aimir.model.system.GroupMember;
import com.aimir.util.Condition;

@Repository(value = "loadshedgroupDao")
public class LoadShedGroupDaoImpl extends
		AbstractJpaDao<LoadShedGroup, Integer> implements
		LoadShedGroupDao {

	private static Log log = LogFactory.getLog(LoadShedGroupDaoImpl.class);

	public LoadShedGroupDaoImpl() {
		super(LoadShedGroup.class);
	}

    @Override
    public List<Object> getLoadShedGroupList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LoadShedGroup> getLoadShedGroupList(Integer operatorId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LoadShedGroup> getLoadShedGroupListWithoutSchedule(
            String groupType, String groupName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getGroupListWithChild(Integer operatorId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getGroupListWithChild2(Integer operatorId) {
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
    public Class<LoadShedGroup> getPersistentClass() {
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
