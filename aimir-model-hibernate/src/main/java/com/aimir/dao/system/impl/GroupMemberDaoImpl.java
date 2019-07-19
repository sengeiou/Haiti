package com.aimir.dao.system.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.model.system.GroupMember;
import com.aimir.util.TimeUtil;

@Repository(value="groupmemberDao")
public class GroupMemberDaoImpl extends AbstractHibernateGenericDao<GroupMember, Integer> implements GroupMemberDao{
    
	private static Log log = LogFactory.getLog(GroupMemberDaoImpl.class); 
	
	@Autowired
	protected GroupMemberDaoImpl(SessionFactory sessionFactory) {
		super(GroupMember.class);
		super.setSessionFactory(sessionFactory);
	}
	
	public int updateData(Integer id, Integer groupId) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE GroupMember g ");
		sb.append("SET g.aimirGroup.id = ? ");		
		sb.append("WHERE g.id = ? ");

		//HQL문을 이용한 CUD를 할 경우에는 getHibernateTemplate().bulkUpdate() 메소드를 사용한다.
		Query query = getSession().createQuery(sb.toString());
		query.setParameter(1,  groupId);
		query.setParameter(2, id);
		return query.executeUpdate();
		// bulkUpdate 때문에 주석처리
		/*return this.getHibernateTemplate().bulkUpdate(sb.toString(), new Object[] { groupId, id } );*/
	}
	
    @SuppressWarnings("unchecked")
    @Deprecated
	public List<Object> getGroupMember(Map<String, Object> condition){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT m.id as data, m.member as label ")
		  .append("FROM GroupMember m LEFT OUTER JOIN m.group g ")
		  .append("WHERE m.group.id IS NULL ")
		  .append(" ORDER BY m.member ASC");
		
		return getSession().createQuery(sb.toString())
						   .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						   .list();
    }

    /**
     * method name : getMemberSelectedData<b/>
     * method Desc : Group Management 가젯에서 Member 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object> getMemberSelectedData(Map<String, Object> conditionMap) {
    	List<Object> gridData 	= new ArrayList<Object>();		
    	List<Object> result		= new ArrayList<Object>();
    	
    	Integer groupId = (Integer)conditionMap.get("groupId");
    	
    	int page = conditionMap.containsKey("page") ? (Integer) conditionMap.get("page") : 0;
        int limit = conditionMap.containsKey("limit") ? (Integer) conditionMap.get("limit") : 0;
         
    	StringBuilder sbQuery = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        StringBuilder countQuery = new StringBuilder();

        sbQuery.append("\nSELECT gm.id AS id, ");
        sbQuery.append("\n       gm.member AS name ");        
        sb.append("\nFROM GroupMember gm ");
        sb.append("\nWHERE gm.aimirGroup.id = :groupId ");
        sb.append("\nORDER BY gm.member ");
        // 전체 건수
        countQuery.append("\n SELECT COUNT( * ) ");
        countQuery.append(sb);
        
        Query countQueryObj = getSession().createQuery(countQuery.toString());
        countQueryObj.setInteger("groupId", groupId);
        
        Number totalCount = (Number)countQueryObj.uniqueResult();

        sbQuery.append(sb);
        
        Query query = getSession().createQuery(sbQuery.toString());
        query.setInteger("groupId", groupId);
        
        if(page > 0 && limit > 0) { // if page == 0 % limit == 0 -> Not Paging
        	query.setFirstResult((page-1) * limit);      
        	query.setMaxResults(limit);
        }
        List dataList = query.list();
	        
		// 실제 데이터
		int dataListLen = 0;
		if(dataList != null)
			dataListLen = dataList.size();
				
		for (int i = 0; i < dataListLen; i++) {
			HashMap chartDataMap = new HashMap();
			Object[] resultData = (Object[]) dataList.get(i);
			chartDataMap.put("value", resultData[0]);
			chartDataMap.put("text",  resultData[1]);                 
			gridData.add(chartDataMap);
		}
		
		result.add(totalCount.toString());
		result.add(gridData);
		
		if(page > 0 && limit > 0)
			return result;
		else // if page == 0 % limit == 0 -> Return only Grid Data
			return dataList;
    }
    
    /**
     * method name : getHomeGroupMemberSelectedData<b/>
     * method Desc : HomeGroup Management 가젯에서 Member 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getHomeGroupMemberSelectedData(Map<String, Object> conditionMap) {
        Integer groupId = (Integer)conditionMap.get("groupId");
        Integer supplierId = (Integer)conditionMap.get("supplierId");

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT gm.id as ID, gm.member as NAME, gm.lastSyncDate as LASTSYNCDATE, gm.type as TYPE,");
        sb.append("\n		CASE WHEN gm.isRegistration =:isRegistration THEN 'Registration' ELSE 'Close' END AS ISREGISTRATION");
        sb.append("\nFROM	(	SELECT	mo.device_serial as member, 'Modem' as type, subGM.isRegistration as isRegistration, ");
        sb.append("\n					subGM.id as id, subGM.last_sync_date as lastSyncDate");
        sb.append("\n			FROM	Modem mo, Group_Member subGm");
        sb.append("\n			WHERE	mo.supplier_id=:supplierId");
        sb.append("\n			AND		mo.device_serial =subGm.member");
        sb.append("\n			AND		subGM.group_Id =:groupId");
        sb.append("\n			UNION");
        sb.append("\n			SELECT	m.mds_id as member, 'Meter' as type, subGM.isRegistration as isRegistration, ");
        sb.append("\n					subGM.id as id, subGM.last_sync_date as lastSyncDate");
        sb.append("\n			FROM	Meter m, Group_Member subGm");
        sb.append("\n			WHERE	m.supplier_id=:supplierId");
        sb.append("\n			AND		m.mds_id =subGm.member");
        sb.append("\n			AND		subGM.group_Id =:groupId");
        sb.append("\n			UNION");
        sb.append("\n			SELECT	co.contract_number as member, 'Contract' as type, subGM.isRegistration as isRegistration, ");
        sb.append("\n					subGM.id as id, subGM.last_sync_date as lastSyncDate");
        sb.append("\n			FROM	Contract co, Group_Member subGm");
        sb.append("\n			WHERE	co.supplier_id=:supplierId");
        sb.append("\n			AND		co.contract_number =subGm.member");
        sb.append("\n			AND		subGM.group_Id =:groupId");
        sb.append("\n			UNION");
        sb.append("\n			SELECT	lo.name as member, 'Location' as type, subGM.isRegistration as isRegistration, ");
        sb.append("\n					subGM.id as id, subGM.last_sync_date as lastSyncDate");
        sb.append("\n			FROM	Location lo, Group_Member subGm");
        sb.append("\n			WHERE	lo.supplier_id=:supplierId");
        sb.append("\n			AND		lo.name =subGm.member");
        sb.append("\n			AND		subGM.group_Id =:groupId");
        sb.append("\n			UNION");
        sb.append("\n			SELECT	mc.sys_id as member, 'MCU' as type, subGM.isRegistration as isRegistration, ");
        sb.append("\n					subGM.id as id, subGM.last_sync_date as lastSyncDate");
        sb.append("\n			FROM	MCU mc, Group_Member subGm");
        sb.append("\n			WHERE	mc.supplier_id=:supplierId");
        sb.append("\n			AND		mc.sys_id =subGm.member");
        sb.append("\n			AND		subGM.group_Id =:groupId");
        sb.append("\n			UNION");
        sb.append("\n			SELECT	op.NAME as member, 'Operator' as type, subGM.isRegistration as isRegistration, ");
        sb.append("\n					subGM.id as id, subGM.last_sync_date as lastSyncDate");
        sb.append("\n			FROM	Operator op, Group_Member subGm");
        sb.append("\n			WHERE	op.supplier_id=:supplierId");
        sb.append("\n			AND		op.name =subGm.member");
        sb.append("\n			AND		subGM.group_Id =:groupId");
        sb.append("\n			UNION");
        sb.append("\n			SELECT	en.friendly_name as member, 'EndDevice' as type, subGM.isRegistration as isRegistration, ");
        sb.append("\n					subGM.id as id, subGM.last_sync_date as lastSyncDate");
        sb.append("\n			FROM	EndDevice en, Group_Member subGm");
        sb.append("\n			WHERE	en.supplier_id=:supplierId");
        sb.append("\n			AND		en.friendly_name =subGm.member");
        sb.append("\n			AND		subGM.group_Id =:groupId");
        sb.append("\n			) AS gm");
        sb.append("\nORDER BY gm.member, gm.type ");

        Query query = getSession().createSQLQuery(sb.toString());
        query.setBoolean("isRegistration", true);
        query.setInteger("supplierId", supplierId);
        query.setInteger("groupId", groupId);

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

	@SuppressWarnings("unchecked")
	public Set<GroupMember> getGroupMemberById(Integer groupId) {
		Query query = getSession().createQuery("FROM GroupMember WHERE group_id = " + groupId + " ");
    	List<GroupMember> list = query.list();
    	
    	Set<GroupMember> members = new HashSet<GroupMember>();
    	for(GroupMember g : list){
    		members.add(g);
    	}
    	return members; 
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getLoadShedGroupMembers(String targetType, String targetName){
		log.debug("targetType["+targetType+"], targetName["+targetName+"]");
		
		StringBuffer hqlBuf = new StringBuffer();
		/*hqlBuf.append(" SELECT gm.id, gm.member");
		hqlBuf.append(" FROM   GroupMember gm join gm.aimirGroup ag");
		hqlBuf.append(" WHERE  gm.id 	    = :targetId");
		hqlBuf.append(" AND    ag.groupType = :targetType");*/

		hqlBuf.append(" SELECT members.id as targetId, members.member as targetName, g.id as groupId");
		hqlBuf.append(" FROM   LoadShedGroup g ");
		hqlBuf.append(" LEFT OUTER JOIN g.members members ");
		hqlBuf.append(" WHERE g.groupType       = :targetType ");
		hqlBuf.append(" AND   members.member like :targetName");
		//hqlBuf.append(" ORDER BY targetName");
		
		Query query = getSession() .createQuery(hqlBuf.toString());
		query.setParameter("targetType", GroupType.valueOf(targetType));
		query.setParameter("targetName", "%"+targetName+"%");
		
		List<Object> list = query.list();
		return list;
	}
	
	public int updateGroupMember(Integer id, String member) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE GroupMember g ")
		.append("SET g.member = ? ")
		.append("WHERE g.id = ? ");
		
		//HQL문을 이용한 CUD를 할 경우에는 getHibernateTemplate().bulkUpdate() 메소드를 사용한다.		
		Query query = getSession().createQuery(sb.toString());
		query.setParameter(1, member);
		query.setParameter(2, id);
		return query.executeUpdate();
		// bulkUpdate 때문에 주석처리
		/*return this.getHibernateTemplate().bulkUpdate(sb.toString(), new Object[] { member, id } );*/
	}

	
	/**
	 * method name : updateMCURegirationList
	 * method Desc : mcu에 등록된 Memeber들의 isRegistration필드에 등록이 완료되었음을 표시한다.
	 */
	public int updateMCURegirationList(Boolean isRegistration, List<String> member, Integer groupId) {
		StringBuffer sb = new StringBuffer();
		sb.append("\nUPDATE GroupMember g ");
		
		if(isRegistration != null) {
			sb.append("\nSET g.isRegistration = :isRegistration ");
		} else {
			sb.append("\nSET g.lastSyncDate = :lastSyncDate");
		}
		
		sb.append("\nWHERE g.groupId = :groupId ");
		if(member != null || member.size() > 0) {
			sb.append("\nAND g.member IN (:memberList) ");
			
		}

		Query query = getSession().createQuery(sb.toString());
		if(isRegistration != null) {
			query.setBoolean("isRegistration", isRegistration);
		} else {
			try {
				query.setString("lastSyncDate", TimeUtil.getCurrentTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		query.setInteger("groupId", groupId);
		
		if(member != null || member.size() > 0) {
			query.setParameterList("memberList", member);
		}
		return query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
    @Override
	public List<String> getMeterGroupMemberIds(Integer groupId) {
		final String hql = "SELECT gm.member FROM GroupMember gm LEFT JOIN gm.aimirGroup ag WHERE ag.id=:ID AND ag.groupType=:GTYPE";
		
		Query query = getSession().createQuery(hql);
		query.setInteger("ID", groupId);
		query.setParameter("GTYPE", GroupType.Meter);
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Integer getGroupIdbyMember(String memberId) {
		final String hql = "FROM GroupMember gm WHERE gm.member=:memberId";
		Query query = getSession().createQuery(hql);
		query.setString("memberId", memberId);
		
		List<GroupMember> gmList = (List<GroupMember>)query.list();
		
		if(gmList == null || gmList.size() <= 0) return -1;
		
		return gmList.get(0).getGroupId();
	}

	@SuppressWarnings("unchecked")
    @Override
	public Set<GroupMember> getMeterSerialsByLocation(Integer groupId) {
		final String hql = "SELECT id FROM Location where name=(SELECT gm.member as member FROM GroupMember gm LEFT JOIN gm.aimirGroup ag WHERE ag.id=:ID AND ag.groupType=:GTYPE)";
		Set<GroupMember> resultMembers = new HashSet<GroupMember>();
		
		Query query = getSession().createQuery(hql);
		query.setInteger("ID", groupId);
		query.setParameter("GTYPE", GroupType.Location);
		List<Integer> locationId = query.list();
		
		List<Integer> allGroupChildLocations = new ArrayList<Integer>();
		
		//검색해야할 LOCATION 과 하위 location 의 id를 모두 조회한다.
		for (Integer parentId : locationId) {
			allGroupChildLocations.add(parentId);
			List<Integer> childLocations = getChildLocations(parentId);
			if(childLocations!=null){
				allGroupChildLocations.addAll(	childLocations);
			}
		}
		
		
		final String getChildsHQL = "SELECT mdsId FROM Meter WHERE location.id in (:LOCATION)";
		query = getSession().createQuery(getChildsHQL);
		query.setParameterList("LOCATION", allGroupChildLocations);
		List<String> memberList = query.list();
		
		for (String member : memberList) {
			//인스턴스 그룹을 만든다.(실제 디비에는 없는 맴버)
			GroupMember _member = new GroupMember();
			_member.setMember(member);
			resultMembers.add(_member);
		}
		
		return resultMembers;
	}

	/**
	 * method name : getChildLocations
	 * method Desc : location 모든 자식 노드들의 id를 구한다.(재귀함수)
	 * @param parentName 부모 노드 이름(no id)
	 * @return
	 */
	@SuppressWarnings("unchecked")
    private List<Integer> getChildLocations(Integer parentId) {
		
		final String getChildsHQL = "SELECT id FROM Location WHERE parent=:ID";
		Query query = getSession().createQuery(getChildsHQL);
		query.setInteger("ID", parentId);
		List<Integer> childs = query.list();
		List<Integer> grandChild = new ArrayList<Integer>();
		
		if(childs==null || childs.size()==0){
			return null;
		} else {
			for (Integer child : childs) {
				List<Integer> _childs=getChildLocations(child);
				if(_childs!=null){
					grandChild.addAll(_childs);
				}
			}
		}
		
		if(grandChild.size()>0){
			childs.addAll(grandChild);
		}
		return childs;
	}

}
