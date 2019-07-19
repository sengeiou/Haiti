package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.model.system.GroupMember;
import com.aimir.util.Condition;

@Repository(value="groupmemberDao")
public class GroupMemberDaoImpl extends AbstractJpaDao<GroupMember, Integer> implements GroupMemberDao{
    
	private static Log log = LogFactory.getLog(GroupMemberDaoImpl.class); 
	
	public GroupMemberDaoImpl() {
		super(GroupMember.class);
	}

    @Override
    public int updateData(Integer id, Integer groupId) throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int updateMCURegirationList(Boolean isRegistration,
            List<String> member, Integer groupId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    @Deprecated
    public List<Object> getGroupMember(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMemberSelectedData(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getHomeGroupMemberSelectedData(
            Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<GroupMember> getGroupMemberById(Integer groupId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getLoadShedGroupMembers(String targetType,
            String targetName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int updateGroupMember(Integer id, String member) throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<String> getMeterGroupMemberIds(Integer groupId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getGroupIdbyMember(String memberId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<GroupMember> getMeterSerialsByLocation(Integer groupId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<GroupMember> getPersistentClass() {
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
