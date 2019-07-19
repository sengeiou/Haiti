package com.aimir.dao.device.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.LoadType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.LoadShedGroupDao;
import com.aimir.model.device.LoadShedGroup;
import com.aimir.model.system.GroupMember;

@Repository(value = "loadshedgroupDao")
public class LoadShedGroupDaoImpl extends
		AbstractHibernateGenericDao<LoadShedGroup, Integer> implements
		LoadShedGroupDao {

	private static Log log = LogFactory.getLog(LoadShedGroupDaoImpl.class);

	@Autowired
	protected LoadShedGroupDaoImpl(SessionFactory sessionFactory) {
		super(LoadShedGroup.class);
		super.setSessionFactory(sessionFactory);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.aimir.dao.device.LoadShedGroupDao#getLoadShedGroupList(Map)
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getLoadShedGroupList() {
		// return
		// getSession().find("FROM AimirGroup WHERE GroupName = \'LoadShedGroup\'");
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT id, name, groupType, allUserAccess, supplyCapacity, supplyThreshold, loadType "
				+ " FROM AimirGroup WHERE operator.id = :operatorId ");

		List<Object> groupList = getSession().createQuery(sb.toString()).list();
		return groupList;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.aimir.dao.device.LoadShedGroupDao#getLoadShedGroupList(Map)
	 */
	@SuppressWarnings("unchecked")
	public List<LoadShedGroup> getLoadShedGroupList(Integer operatorId) {
		// return
		// getSession().find("FROM AimirGroup WHERE GroupName = \'LoadShedGroup\'");
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT id, name, groupType, allUserAccess, supplyCapacity, supplyThreshold, loadType "
				+ " FROM AimirGroup WHERE operator.id = :operatorId");

		List<LoadShedGroup> groupList = getSession().createQuery(sb.toString())
				.setInteger("operatorId", operatorId).list();
		return groupList;
	}

	public List<LoadShedGroup> getLoadShedGroupListWithoutSchedule(
			String groupType, String groupName) {

		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" FROM   LoadShedGroup g");
		hqlBuf.append(" WHERE  g.groupType = :groupType");
		if (!groupName.equals("") && groupName != null) {
			hqlBuf.append(" AND    g.name LIKE %:groupName%");
		}
		hqlBuf.append(" AND    g.id not in (SELECT distinct sc.target.id as id");
		hqlBuf.append("                     FROM   LoadShedSchedule sc)");

		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter("groupType", GroupType.valueOf(groupType));
		if (!groupName.equals("") && groupName != null) {
			query.setParameter("groupName", groupName);
		}

		List<LoadShedGroup> list = query.list();
		log.debug(" LIST Size[" + list.size() + "]");
		log.debug(" List[" + list.toString() + "]");

		return list;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.aimir.dao.device.LoadShedGroupDao#getGroupListWithChild(Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getGroupListWithChild(Integer operatorId) {
		log.debug("===========Execute GroupListWithChild function");
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT id, name, groupType, allUserAccess, supplyCapacity, supplyThreshold, loadType "
				+ " FROM AimirGroup WHERE operator.id = :operatorId ");

		List<Object> groupList = getSession().createQuery(sb.toString())
				.setInteger("operatorId", operatorId).list();
		List<Object> returnList = new ArrayList<Object>();

		if (groupList.size() > 0) {
			log.debug("===groupList.size > 0, size is : " + groupList.size()
					+ " ===");
			List<Object> children = null;
			List<GroupMember> members = null;

			Map<String, Object> group = null;
			Map<String, Object> member = null;

			Object[] objs = null;
			for (Object obj : groupList) {
				objs = (Object[]) obj;
				group = new HashMap<String, Object>();
				group.put("id", (Integer) objs[0]);
				group.put("name", (String) objs[1]);
				group.put("groupType", ((GroupType) objs[2]).name());
				group.put("allUserAccess", (Boolean) objs[3] ? "Y" : "N");
				group.put("supplyCapacity", ((Number) objs[4]).doubleValue());
				group.put("supplyThreshold", ((Number) objs[5]).doubleValue());
				group.put("loadType", ((LoadType) objs[6]).name());
				// group.put("oldAllUserAccess",(Boolean)objs[3] ? "Y" : "N" );

				log.debug("group ::: " + group.toString());
				children = new ArrayList<Object>();
				members = getChildren((Integer) objs[0]); // 해당 그룹의 구성원을 저장

				for (GroupMember m : members) {
					if (members != null && members.size() > 0) {
						member = new HashMap<String, Object>();
						log.debug("m.getId() : " + m.getId());
						log.debug("m.getId() : " + m.getId());
						log.debug("(Integer)objs[0] : " + (Integer) objs[0]);
						member.put("id", m.getId());
						member.put("name", m.getMember());
						member.put("groupId", (Integer) objs[0]);
						children.add(member);
					}
				}
				group.put("children", children); // AdvancedDataGrid의 Hierachy를
													// 위해 chilren 추가

				returnList.add(group);
			}
		}
		log.info("============= LoadShedGroupDaoImpl Ends");
		return returnList;
	}

	public List<Object> getGroupListWithChild2(Integer operatorId) {
		log.debug("===========Execute GroupListWithChild function");

		Query query = getSession().createQuery("from LoadShedGroup where ( (all_Users_Access  = 1 AND (operator_id IS NOT NULL)) OR "
                + "(all_Users_Access <> 1 AND (operator_id =? )))");
		query.setInteger(1, operatorId);
		
		List<LoadShedGroup> loadShedGroups = (List<LoadShedGroup>)query.list();
		
		Map<String, Object> loadShedGroup = null;

		List<GroupMember> members = null;
		Map<String, Object> member = null;

		List<Object> children = null;

		// 반환할 그룹 정보 목록
		List<Object> returnList = new ArrayList<Object>();

		for (LoadShedGroup g : loadShedGroups) {
			// 그룹정보가 있을 경우
			if (loadShedGroups != null && loadShedGroups.size() > 0) {
				loadShedGroup = new HashMap<String, Object>();
				log.debug("group member : " + g.toString());

				loadShedGroup.put("id", g.getId());
				loadShedGroup.put("name", g.getName());
				loadShedGroup.put("groupType", g.getGroupType().name());
				loadShedGroup.put("oldGroupType", g.getGroupType().name());
				loadShedGroup.put("allUserAccess", g.getAllUserAccess() ? "Y"
						: "N");
				loadShedGroup.put("oldAllUserAccess",
						g.getAllUserAccess() ? "Y" : "N");
				loadShedGroup.put("supplyCapacity", g.getSupplyCapacity());
				loadShedGroup.put("supplyThreshold", g.getSupplyThreshold());
				loadShedGroup.put("loadType", g.getLoadType());
				loadShedGroup.put("descr", g.getDescr());

				children = new ArrayList<Object>();
				members = getChildren(g.getId()); // 해당 그룹의 구성원을 저장
				for (GroupMember m : members) {
					if (members != null && members.size() > 0) {
						member = new HashMap<String, Object>();
						member.put("id", m.getId());
						member.put("name", m.getMember());
						member.put("groupId", m.getId());
						children.add(member);
						log.debug("member toString : " + member.toString());
					}
				}
			}
			loadShedGroup.put("children", children);
			returnList.add(loadShedGroup);
		}
		return returnList;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.aimir.dao.device.LoadShedGroupDao#getChildren(Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<GroupMember> getChildren(Integer groupId) {
	    Query query = getSession().createQuery("from GroupMember where group_id = ?");
	    query.setInteger(1, groupId);
	    return query.list();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.aimir.dao.device.LoadShedGroupDao#count()
	 */
	public Integer count() {
		Criteria criteria = getSession().createCriteria(LoadShedGroup.class);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}

}
