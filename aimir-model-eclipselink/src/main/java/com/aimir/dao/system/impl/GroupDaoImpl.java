package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.GroupDao;
import com.aimir.model.system.AimirGroup;
import com.aimir.model.system.GroupMember;
import com.aimir.util.Condition;

@Repository(value="groupDao")
public class GroupDaoImpl extends AbstractJpaDao<AimirGroup, Integer> implements GroupDao{
    
	public GroupDaoImpl() {
		super(AimirGroup.class);
	}

    @Override
    public List<AimirGroup> getGroupList(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getGroupListWithChild(Integer operatorId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List<Object> getGroupListWithChildNotinHomeGroupIHD(
            Integer operatorId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getGroupListNotHomeGroupIHD(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<AimirGroup> getGroupListMeter(Integer operatorId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<AimirGroup> getContractGroup(Integer operatorId) {
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
    public List<Map<String, Object>> getGroupComboDataByType(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getGroupTypeByGroup(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> dupCheckGroupName(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<AimirGroup> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public List<Map<String, Object>> getGroupListMcu(Integer operatorId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getSelectedListMcu(Map<String, Object> conditionMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSelectedCountMcu(Integer groupId) {
		// TODO Auto-generated method stub
		return 0;
	}
}