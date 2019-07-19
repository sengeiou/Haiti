package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.HomeGroupDao;
import com.aimir.model.device.MCU;
import com.aimir.model.system.AimirGroup;
import com.aimir.model.system.GroupMember;
import com.aimir.model.system.HomeGroup;
import com.aimir.util.StringUtil;

@Repository(value="homegroupDao")
public class HomeGroupDaoImpl extends AbstractHibernateGenericDao<HomeGroup, Integer> implements HomeGroupDao{

	@Autowired
	protected HomeGroupDaoImpl(SessionFactory sessionFactory) {
		super(HomeGroup.class);
		super.setSessionFactory(sessionFactory);
	}


	@SuppressWarnings("unchecked")
	public List<HomeGroup> getGroupList(Map<String, Object> condition) {
		Query query = getSession().createQuery("FROM AimirGroup");
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getHomeGroupList(Map<String, Object> conditionMap) {
        
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        String groupType = StringUtil.nullToBlank(conditionMap.get("groupType"));
        String groupName = StringUtil.nullToBlank(conditionMap.get("groupName"));
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        
		StringBuilder sb = new StringBuilder();
        
        sb.append("\nSELECT gr.id AS groupId, ");
        sb.append("\n       gr.name AS groupName, ");
        sb.append("\n       gr.groupKey AS groupKey, ");
        sb.append("\n       gr.homeGroupMcu.sysID AS sysId, ");
        sb.append("\n       gr.homeGroupMcuId AS mcuId, ");
        sb.append("\n       gr.groupType AS groupType, ");
        sb.append("\n       count(me.id) AS memCount ");
        sb.append("\nFROM GroupMember me ");
        sb.append("\n     RIGHT OUTER JOIN ");
        sb.append("\n     me.aimirGroup gr ");
        sb.append("\nWHERE gr.supplierId = :supplierId ");
        sb.append("\nAND gr.operatorId = :operatorId ");
        
        if (!groupType.isEmpty()) {
            sb.append("\nAND   hr.groupType = :groupType ");
        }
        if (!groupName.isEmpty()) {
            sb.append("\nAND   hr.name LIKE :groupName ");
        }
        

        sb.append("\nGROUP BY gr.name, ");
        sb.append("\n         gr.id, ");
        sb.append("\n         gr.groupType, ");
        sb.append("\n         gr.groupKey, ");
        sb.append("\n         gr.homeGroupMcu.sysID, ");
        sb.append("\n         gr.homeGroupMcuId ");
        
        sb.append("\nORDER BY lower(gr.name) ");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", supplierId);
        query.setInteger("operatorId", operatorId);

        if (!groupType.isEmpty()) {
            query.setString("groupType", groupType);
        }
        if (!groupName.isEmpty()) {
            query.setString("groupName", "%" + groupName + "%");
        }

        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getGroupListWithChild(Integer operatorId) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT id, name, groupType, allUserAccess FROM AimirGroup WHERE operator.id = :operatorId ");

		List<Object> groupList = getSession().createQuery(sb.toString())
											 .setInteger("operatorId", operatorId)
											 .list();
		List<Object> returnList = new ArrayList<Object>();

		if (groupList.size() > 0) {

			List<Object> children = null;
			List<GroupMember> members = null;

			Map<String,Object> group = null;
			Map<String,Object> member = null;

			Object[] objs = null;
			for(Object obj : groupList){
				objs = (Object[])obj;
				group = new HashMap<String,Object>();
				group.put("id",objs[0]);
				group.put("name",objs[1]);
				group.put("groupType",((GroupType)objs[2]).name());
				group.put("oldGroupType",((GroupType)objs[2]).name());
				group.put("allUserAccess",(Boolean)objs[3] ? "Y" : "N" );
				group.put("oldAllUserAccess",(Boolean)objs[3] ? "Y" : "N" );

				children = new ArrayList<Object>();
				members = getChildren(((Number)objs[0]).intValue());

				for (GroupMember m : members) {
					if (members != null && members.size() > 0) {
						member = new HashMap<String,Object>();
						member.put("id", m.getId());
						member.put("name", m.getMember());
						member.put("groupId", objs[0]);
						children.add(member);
					}
				}
				group.put("children",children);

				returnList.add(group);
			}
		}

		return returnList;
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

	public HomeGroup getHomeGroup(String name) {
	    return findByCondition("name", name);
	}

    /**
     * method name : getHomeGroupMcuByGroupId
     * method Desc : Group ID 로 MomeGroup 의 MCU 클래스를 조회한다.
     *
     * @param groupId
     * @return
     */
    @SuppressWarnings("unchecked")
    public MCU getHomeGroupMcuByGroupId(Integer groupId) {
        MCU mcu = null;
        StringBuilder sb = new StringBuilder();
        sb.append("\nSELECT g.homeGroupMcu ");
        sb.append("\nFROM AimirGroup g ");
        sb.append("\nWHERE g.id = :groupId ");

        List<Object> list = getSession().createQuery(sb.toString()).setInteger("groupId", groupId).list();

        if (list.size() > 0) {
            mcu = (MCU)list.get(0);
        }

        return mcu;
    }
    
    /**
     * method name : getMemberSelectData<b/>
     * method Desc : HomeGroup Management 가젯에서 Member 로 등록 가능한 HomeGroup 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {
    	
    	Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer groupId = (Integer)conditionMap.get("groupId");
        Integer mcuId = (Integer)conditionMap.get("mcuId");
        String memberName = StringUtil.nullToBlank(conditionMap.get("memberName"));

        StringBuilder sb = new StringBuilder();
        sb.append("\nSELECT * ");
        sb.append("\nFROM ( ");
        sb.append("\nSELECT mo.id AS value, ");
        sb.append("\n       mo.device_Serial AS text, ");
        sb.append("\n       'Modem' AS type ");
        sb.append("\nFROM Modem mo ");
        sb.append("\nWHERE mo.supplier_Id = :supplierId ");
        sb.append("\nAND   mo.mcu_Id = :mcuId ");
        
        if (!memberName.isEmpty()) {
            sb.append("\nAND   mo.device_Serial LIKE :memberName ");
        }

        sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    SELECT 'X' ");
        sb.append("\n    FROM  Group_Member gm, AimirGroup g ");
        sb.append("\n    WHERE gm.group_Id = g.id ");
        sb.append("\n    AND   g.groupName = :groupName ");
        sb.append("\n    AND   gm.member = mo.device_Serial ");
        sb.append("\n) ");
        
        sb.append("\n UNION");
        
        sb.append("\nSELECT me.id AS value, ");
        sb.append("\n       me.mds_Id AS text, ");
        sb.append("\n       'Meter' AS type ");
        sb.append("\nFROM Meter me ");
        sb.append("\nWHERE me.supplier_Id = :supplierId ");

        if (!memberName.isEmpty()) {
            sb.append("\nAND   me.mds_Id LIKE :memberName ");
        }

        sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    SELECT 'X' ");
        sb.append("\n    FROM  Group_Member gm, AimirGroup g ");
        sb.append("\n    WHERE gm.group_Id = g.id ");
        sb.append("\n    AND   g.groupName = :groupName ");
        sb.append("\n    AND gm.member = me.mds_Id ");
        sb.append("\n) ");
        
        sb.append("\n UNION");
        
        sb.append("\nSELECT co.id AS value, ");
        sb.append("\n       co.contract_Number AS text, ");
        sb.append("\n       'Contract' AS type ");
        sb.append("\nFROM Contract co ");
        sb.append("\nWHERE co.supplier_Id = :supplierId ");

        if (!memberName.isEmpty()) {
            sb.append("\nAND   co.contract_Number LIKE :memberName ");
        }

        sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    SELECT 'X' ");
        sb.append("\n    FROM  Group_Member gm, AimirGroup g ");
        sb.append("\n    WHERE gm.group_Id = g.id ");
        sb.append("\n    AND   g.groupName = :groupName ");
        sb.append("\n    AND gm.member = co.contract_Number ");
        sb.append("\n) ");
        
        sb.append("\n UNION");
        
        sb.append("\nSELECT lo.id AS value, ");
        sb.append("\n       lo.name AS text, ");
        sb.append("\n       'Location' AS type ");
        sb.append("\nFROM Location lo ");
        sb.append("\nWHERE lo.supplier_Id = :supplierId ");

        if (!memberName.isEmpty()) {
            sb.append("\nAND   lo.name LIKE :memberName ");
        }

        sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    SELECT 'X' ");
        sb.append("\n    FROM  Group_Member gm, AimirGroup g ");
        sb.append("\n    WHERE gm.group_Id = g.id ");
        sb.append("\n    AND   g.groupName = :groupName ");
        sb.append("\n    AND gm.member = lo.name ");
        sb.append("\n) ");
        
        sb.append("\n UNION");
        
        sb.append("\nSELECT op.id AS value, ");
        sb.append("\n       op.name AS text, ");
        sb.append("\n       'Operator' AS type ");
        sb.append("\nFROM Operator op ");
        sb.append("\nWHERE op.supplier_Id = :supplierId ");

        if (!memberName.isEmpty()) {
            sb.append("\nAND   op.name LIKE :memberName ");
        }

        sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    SELECT 'X' ");
        sb.append("\n    FROM  Group_Member gm, AimirGroup g ");
        sb.append("\n    WHERE gm.group_Id = g.id ");
        sb.append("\n    AND   g.groupName = :groupName ");
        sb.append("\n    AND gm.member = op.name ");
        sb.append("\n) ");
        
        sb.append("\n UNION");
        
        sb.append("\nSELECT mc.id AS value, ");
        sb.append("\n       mc.sys_ID AS text, ");
        sb.append("\n       'MCU' AS type ");
        sb.append("\nFROM MCU mc ");
        sb.append("\nWHERE mc.supplier_Id = :supplierId ");

        if (!memberName.isEmpty()) {
            sb.append("\nAND   mc.sys_ID LIKE :memberName ");
        }

        sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    SELECT 'X' ");
        sb.append("\n    FROM  Group_Member gm, AimirGroup g ");
        sb.append("\n    WHERE gm.group_Id = g.id ");
        sb.append("\n    AND   g.groupName = :groupName ");
        sb.append("\n    AND   gm.member = mc.sys_ID ");
        sb.append("\n) ");
        
        sb.append("\n UNION");
        
        sb.append("\nSELECT de.id AS value, ");
        sb.append("\n       de.friendly_Name AS text, ");
        sb.append("\n       'EndDevice' AS type ");
        sb.append("\nFROM EndDevice de ");
        sb.append("\nWHERE de.supplier_Id = :supplierId ");

        if (!memberName.isEmpty()) {
            sb.append("\nAND   de.friendly_Name LIKE :memberName ");
        }

        sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    SELECT 'X' ");
        sb.append("\n    FROM  Group_Member gm, AimirGroup g ");
        sb.append("\n    WHERE gm.group_Id = g.id ");
        sb.append("\n    AND   g.groupName = :groupName ");
        sb.append("\n    AND   gm.member = de.friendly_Name ");
        sb.append("\n) ");
        sb.append("\n) AS homeGroup");
        sb.append("\nORDER BY homeGroup.text");

        Query query = getSession().createSQLQuery(sb.toString());
        query.setInteger("supplierId", supplierId);
        query.setString("groupName", GroupType.HomeGroup.name());
        
        if(mcuId != null) {
        	query.setInteger("mcuId", mcuId);
        }
        
        if (!memberName.isEmpty()) {
            query.setString("memberName", "%" + memberName + "%");
        }

        return query.list();
    }

}
