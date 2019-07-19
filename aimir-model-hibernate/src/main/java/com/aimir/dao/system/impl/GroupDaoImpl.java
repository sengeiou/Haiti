package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.GroupDao;
import com.aimir.model.system.AimirGroup;
import com.aimir.model.system.GroupMember;
import com.aimir.util.StringUtil;

import net.sf.json.JSONArray;

@Repository(value="groupDao")
public class GroupDaoImpl extends AbstractHibernateGenericDao<AimirGroup, Integer> implements GroupDao{
    
	@Autowired
	protected GroupDaoImpl(SessionFactory sessionFactory) {
		super(AimirGroup.class);
		super.setSessionFactory(sessionFactory);
	}
	

	@SuppressWarnings("unchecked")
	public List<AimirGroup> getGroupList(Map<String, Object> condition) {
		Query query = getSession().createQuery("FROM AimirGroup");
		return query.list();	
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getGroupListWithChild(Integer operatorId) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT id, name, groupType, allUserAccess FROM AimirGroup WHERE operator.id = :operatorId ");

		List<Object> groupList = getSession().createQuery(sb.toString())
											 .setInteger("operatorId", operatorId)
											 .list();

		return groupList;
	}

    @SuppressWarnings("unchecked")
    @Deprecated
    public List<Object> getGroupListWithChildNotinHomeGroupIHD(Integer operatorId) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nSELECT id, name, groupType, allUserAccess ");
        sb.append("\nFROM AimirGroup ");
        sb.append("\nWHERE operator.id = :operatorId ");
        sb.append("\nAND   groupType NOT IN (:groupTypeList) ");

        List<GroupType> groupTypeList = new ArrayList<GroupType>();
        groupTypeList.add(GroupType.HomeGroup);
        groupTypeList.add(GroupType.IHD);

//        List<Object> groupList = getSession().createQuery(sb.toString()).setInteger("operatorId", operatorId).setParameterList("groupTypeList", groupTypeList).list();
        Query query = getSession().createQuery(sb.toString());
        query.setInteger("operatorId", operatorId);
        query.setParameterList("groupTypeList", groupTypeList);
        
        List<Object> groupList = query.list();

        return groupList;
    }

    /**
     * method name : getGroupListNotHomeGroupIHD<b/>
     * method Desc : Group Management 가젯에서 Group List 를 조회한다. HomeGroup/IHD 제외.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getGroupListNotHomeGroupIHD(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = null;
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        String groupType = StringUtil.nullToBlank(conditionMap.get("groupType"));
        String groupName = StringUtil.nullToBlank(conditionMap.get("groupName"));
        Integer supplierId = (Integer)conditionMap.get("supplierId");

        StringBuilder sb = new StringBuilder();
       
        sb.append("\nSELECT gr.id AS groupId, ");
        sb.append("\n       gr.name AS groupName, ");
        sb.append("\n       gr.groupType AS groupType, ");
        sb.append("\n       CASE WHEN gr.allUserAccess = true THEN 'Y' ELSE 'N' END AS allUserAccess, ");
        sb.append("\n       count(me.id) AS memCount, ");
        sb.append("\n       gr.mobileNo AS mobileNo ");
        sb.append("\nFROM GroupMember me ");
        sb.append("\n     RIGHT OUTER JOIN ");
        sb.append("\n     me.aimirGroup gr ");
        sb.append("\nWHERE( (gr.allUserAccess  = true AND (gr.operator.id IS NOT NULL)) OR");
        sb.append("\n(gr.allUserAccess <> true AND (gr.operator.id = :operatorId)))");
        sb.append("\nAND   gr.groupType NOT IN (:groupTypeList) ");
        if(supplierId != null)
        	sb.append("\nAND gr.supplierId = :supplierId");

        if (!groupType.isEmpty()) {
            sb.append("\nAND   gr.groupType = :groupType ");
        }
        if (!groupName.isEmpty()) {
            sb.append("\nAND   gr.name LIKE :groupName ");
        }

        sb.append("\nGROUP BY gr.name, ");
        sb.append("\n         gr.id, ");
        sb.append("\n         gr.groupType, ");
        sb.append("\n         gr.allUserAccess, ");
        sb.append("\n         gr.mobileNo ");
        sb.append("\nORDER BY lower(gr.name) ");

        List<GroupType> groupTypeList = new ArrayList<GroupType>();
        groupTypeList.add(GroupType.HomeGroup);
        groupTypeList.add(GroupType.IHD);

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("operatorId", operatorId);
        query.setParameterList("groupTypeList", groupTypeList);

        if(supplierId != null) {
        	query.setInteger("supplierId", supplierId);
        }
        if (!groupType.isEmpty()) {
            query.setString("groupType", groupType);
        }
        if (!groupName.isEmpty()) {
            query.setString("groupName", "%" + groupName + "%");
        }

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }

	@SuppressWarnings("unchecked")
	public List<AimirGroup> getGroupListMeter(Integer operatorId) {
		Query query = getSession().createQuery("from AimirGroup  where ( (all_Users_Access  = 1 AND (operator_id IS NOT NULL)) OR "+
				"(all_Users_Access <> 1 AND (operator_id = " + operatorId + " ))) and GROUP_TYPE ='Meter' order by upper(name) asc");
		
		return query.list();

	}
	
	@SuppressWarnings("unchecked")
	public List<AimirGroup> getContractGroup(Integer operatorId) {
		Query query = getSession().createQuery("from AimirGroup  where ( (all_Users_Access  = 1 AND (operator_id IS NOT NULL)) OR "+
				"(all_Users_Access <> 1 AND (operator_id = " + operatorId + " ))) and GROUP_TYPE ='Contract' order by upper(name) asc");
		return query.list();

	}
	
	@SuppressWarnings("unchecked")
	public List<GroupMember> getChildren(Integer groupId) {
		Query query = getSession().createQuery("from GroupMember where group_id = " + groupId + " ");
		return query.list();
	}
		
	public int updateData(AimirGroup aimirGroup) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE AimirGroup g ");
		sb.append("SET g.name = ?, ");
		sb.append("    g.allUserAccess = ?, ");	
		sb.append("    g.groupType = ?, ");		
		sb.append("WHERE g.id = ? ");

		//HQL문을 이용한 CUD를 할 경우에는 getHibernateTemplate().bulkUpdate() 메소드를 사용한다.
		Query query = getSession().createQuery(sb.toString());
		query.setParameter(1, aimirGroup.getName());
		query.setParameter(2, aimirGroup.getAllUserAccess());
		query.setParameter(3, aimirGroup.getGroupType());
		query.setParameter(4, aimirGroup.getId());
		return query.executeUpdate();
		// bulkUpdate 때문에 주석처리
		/*return this.getHibernateTemplate().bulkUpdate(sb.toString(), new Object[] {  aimirGroup.getName(), 
																					 aimirGroup.getAllUserAccess(), 
																					 aimirGroup.getGroupType(),
																					 aimirGroup.getId() } );*/
	}
	
	public Integer count() {
		Criteria criteria = getSession().createCriteria(AimirGroup.class);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}

    /**
     * method name : getGroupComboDataByType<b/>
     * method Desc : Task Management 맥스가젯에서 선택한 GroupType 의 Group Combo Data 를 조회한다. 
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getGroupComboDataByType(Map<String, Object> conditionMap) {
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        String groupType = (String)conditionMap.get("groupType");
        StringBuilder sb = new StringBuilder();
        
        sb.append("\nSELECT ag.id AS id,");
        sb.append("\n       ag.name AS name");
        sb.append("\nFROM AimirGroup ag");
        sb.append("\nWHERE  ( (ag.allUserAccess  = true AND (ag.operator.id IS NOT NULL)) OR");
        sb.append("\n (ag.allUserAccess <> true AND (ag.operator.id = :operatorId)))");
        sb.append("\nAND   ag.groupType = :groupType");
        sb.append("\nORDER BY ag.name");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("operatorId", operatorId);
        query.setString("groupType", groupType);
        
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * method name : getGroupTypeByGroup<b/>
     * method Desc : Task Management 맥스가젯에서 선택한 Job 의 Group Type 을 조회한다. 
     *
     * @param conditionMap
     * @return
     */
    public String getGroupTypeByGroup(Map<String, Object> conditionMap) {
        String result = null;
        String groupId = (String)conditionMap.get("groupId");
        StringBuilder sb = new StringBuilder();
        
        sb.append("\nSELECT ag.groupType AS groupType");
        sb.append("\nFROM AimirGroup ag");
        sb.append("\nWHERE ag.id = :groupId");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("groupId", new Integer(groupId));
        
        GroupType groupType = (GroupType)query.uniqueResult();
        
        if (groupType != null) {
            result = groupType.name();
        }
        return result;
    }
    
    /**
     * method name : dupCheckGroupName<b/>
     * method Desc : Group Management 가젯에서 그룹이름이 중복되었는지 체크
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> dupCheckGroupName(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = null;
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        String groupName = StringUtil.nullToBlank(conditionMap.get("groupName"));

        StringBuilder sb = new StringBuilder();
       
        sb.append("\nSELECT g.id AS groupId ");
        sb.append("\nFROM AimirGroup g ");
        sb.append("\nWHERE g.name = :groupName ");
        if(operatorId != null && operatorId != 0) {
        	sb.append("\nAND g.operatorId = :operatorId ");
        }

        Query query = getSession().createQuery(sb.toString());
        if(operatorId != null && operatorId != 0) {
        	query.setInteger("operatorId", operatorId);
        }
        query.setString("groupName", groupName);

        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }
    
    /**
     * method name : getGroupListMcu<b/>
     * method Desc : Group Schedule 팝업창에서 DCU 그룹 리스트 출력
     *
     * @param operatorId
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<Map<String, Object>> getGroupListMcu(Integer operatorId) {
    	
    	List<Map<String, Object>> result = null;
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("\nSELECT id AS id, name AS name ");
		sb.append("\nfrom AimirGroup  where ( (all_Users_Access  = 1 AND (operator_id IS NOT NULL)) OR ");
		sb.append(" (all_Users_Access <> 1 AND (operator_id = :operatorId ))) and GROUP_TYPE ='DCU' order by upper(name) asc");
		
		Query query = getSession().createQuery(sb.toString());
		query.setInteger("operatorId", operatorId);
		
        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		return result;

	}
    
    /**
     * method name : getSelectedListMcu<b/>
     * method Desc : Group Schedule 팝업창에서 Selected DCU 리스트 출력
     *
     * @param conditionMap
     * @return
     */
	@Override
	public List<Map<String, Object>> getSelectedListMcu(Map<String, Object> conditionMap) {
		Integer groupId = (Integer)conditionMap.get("groupId");
        Integer page = (Integer) conditionMap.get("page");
        Integer limit = (Integer) conditionMap.get("limit");
        String pageonoff = (String) conditionMap.get("pageonoff");
        int idx = 1;
        
		List<Map<String, Object>> result = null;
	
        StringBuilder sb = new StringBuilder();
        if(pageonoff.equals("on")) {
	        sb.append("\nSELECT m.ID AS MCUID, m.SYS_ID AS SYSID, l.NAME AS LOCATION ");
	        sb.append("\nFROM GROUP_MEMBER g, MCU m, LOCATION l ");
	        sb.append("\nWHERE g.MEMBER = m.SYS_ID ");
	        sb.append("\nAND m.LOCATION_ID = l.ID ");
	        sb.append("\nAND g.GROUP_ID = :groupId ");
	        
	        Query query = getSession().createSQLQuery(sb.toString());
	        query.setInteger("groupId", groupId);
	        
	        if (page != null && limit != null) {
				query.setFirstResult((page - 1) * limit);
				query.setMaxResults(limit);
			}
	        
	        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	        
        }else if(pageonoff.equals("off")) {
	        sb.append("\nSELECT m.SYS_ID AS SYSID");
	        sb.append("\nFROM GROUP_MEMBER g, MCU m, LOCATION l ");
	        sb.append("\nWHERE g.MEMBER = m.SYS_ID ");
	        sb.append("\nAND m.LOCATION_ID = l.ID ");
	        sb.append("\nAND g.GROUP_ID = :groupId ");
	        
	        Query query = getSession().createSQLQuery(sb.toString());
	        query.setInteger("groupId", groupId);
	        	        
	        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        }
	        
		return result;
	}

	/**
     * method name : getSelectedCountMcu<b/>
     * method Desc : Group Schedule 팝업창에서 Selected DCU Total 출력
     *
     * @param groupId
     * @return
     */
	@Override
	public int getSelectedCountMcu(Integer groupId) {
			int result;
			
	        StringBuilder sb = new StringBuilder();
	        sb.append("\nSELECT COUNT(*) AS TOTAL ");
	        sb.append("\nFROM GROUP_MEMBER g, MCU m, LOCATION l ");
	        sb.append("\nWHERE g.MEMBER = m.SYS_ID ");
	        sb.append("\nAND m.LOCATION_ID = l.ID ");
	        sb.append("\nAND g.GROUP_ID = :groupId ");
	        
	        Query query = getSession().createSQLQuery(sb.toString());
	        query.setInteger("groupId", groupId);
		
	        result = DataAccessUtils.intResult(query.list());

		return result;
	}
    
}