package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.ZoneDao;
import com.aimir.model.system.Zone;

@Repository(value = "zoneDao")
public class ZoneDaoImpl extends
		AbstractHibernateGenericDao<Zone, Integer> implements ZoneDao {

	@Autowired
	protected ZoneDaoImpl(SessionFactory sessionFactory) {
		super(Zone.class);
		super.setSessionFactory(sessionFactory);
	}

//	@SuppressWarnings("unchecked")
//	public List<Zone> getLocations() {
//		DetachedCriteria criteria = DetachedCriteria.forClass(Zone.class);
//
//		return getHibernateTemplate().findByCriteria(criteria);
//	}
//
	@SuppressWarnings("unchecked")
	public List<Zone> getParents() {
		Query query = getSession().createQuery("from Zone where parent is null");
		return query.list();

		/*return (List<Zone>)getHibernateTemplate()
				.find("from Zone where parent is null");*/
	}
//
//	@SuppressWarnings("unchecked")
//	public List<Zone> getParentsBySupplierId(Integer locationId) {
//		return getHibernateTemplate()
//				.find(
//						"from Zone where location_id = ? AND parent is null order by orderno",
//						locationId);
//	}
//
//	@SuppressWarnings("unchecked")
//	public List<Zone> getChildrenBySupplierId(Integer locationId) {
//		if (locationId == null || locationId == 0) {
//			return getHibernateTemplate()
//					.find(
//							"from Zone where id NOT IN "
//									+ "(select DISTINCT parent from Zone where parent is not null) order by orderno");
//		} else {
//			return getHibernateTemplate()
//					.find(
//							"from Zone where location_id = ? AND id NOT IN "
//									+ "(select DISTINCT parent from Zone where parent is not null) order by orderno",
//									locationId);
//		}
//	}
//
//	public int getParentsBykeyWord(Integer locationId, String keyWord) {
//		return DataAccessUtils
//				.intResult(getHibernateTemplate()
//						.find(
//								"select count(*) from Zone where name like ? AND location_id = ?",
//								"%" + keyWord + "%", locationId));
//	}
//
	@SuppressWarnings("unchecked")
	public List<Zone> getChildren(Integer parentId) {
		Query query = getSession().createQuery("from Zone where parent_id = :parentId order by orderno");
		query.setInteger("parentId", parentId);
		
		return query.list();
		
		/*return (List<Zone>)getHibernateTemplate().find(
				"from Zone where parent_id = ? order by orderno", parentId);*/
	}
//
//	@SuppressWarnings("unchecked")
//	public List<Zone> getChildren(Integer parentId, Integer locationId) {
//		if (locationId == null || locationId == 0) {
//			return getHibernateTemplate().find(
//					"from Zone where parent_id = ? order by orderno",
//					parentId);
//		} else {
//			return getHibernateTemplate()
//					.find(
//							"from Zone where parent_id = ? and location_id = ? order by orderno",
//							parentId, locationId);
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	public List<Zone> getParents(Integer locationId) {
//		if (locationId == null || locationId == 0) {
//			return getHibernateTemplate().find(
//					"from Zone where parent is null order by orderno");
//		} else {
//			return getHibernateTemplate()
//					.find(
//							"from Zone where supplier_id = ? AND parent is null order by orderno",
//							locationId);
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	public List<Zone> getLocations(Integer locationId) {
//		if (locationId == null || locationId == 0) {
//			return getAll();
//		} else {
//			return getHibernateTemplate().find(
//					"from Zone where location_id = ? order by orderno",
//					locationId);
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	public List<Integer> getParentId(Integer zoneId) {
//		return getHibernateTemplate().find(
//				"SELECT parent.id FROM Zone WHERE id = ? ", zoneId);
//	}
//
	/**
	 * 입력받은 zone의 하위 zone 중에서 최하위 zone id 목록을 조회한다.
	 * @param zoneId
	 * @return
	 */
	public List<Integer> getLeafZoneId(Integer zoneId) {

		List<Integer> zoneIdList = new ArrayList<Integer>();

		List<Zone> zones=null;
		if(zoneId!=null){
			zones = getChildren(zoneId);
		}else{
			zones = getParents();
		}
		
		for (Zone zone : zones) {
			if (zone.getChildren() != null && zone.getChildren().size() > 0) {
				zoneIdList.addAll(getLeafZone(zone.getChildren()));
			} else {
				zoneIdList.add(zone.getId());
			}
		}

		if (zoneIdList == null || zoneIdList.size() < 1) {
			zoneIdList.add(zoneId);
		}

		return zoneIdList;
	}

	/**
	 * 최하위 Code 를 반환하는 재귀함수
	 * 
	 * @param locations
	 * @return
	 */
	private List<Integer> getLeafZone(Set<Zone> zones) {
		List<Integer> zoneIdList = new ArrayList<Integer>();
		for (Zone zone: zones) {
			if (zone.getChildren() != null && zone.getChildren().size() > 0) {
				zoneIdList.addAll(getLeafZone(zone.getChildren()));
			} else {
				zoneIdList.add(zone.getId());
			}
		}
		return zoneIdList;
	}
//
//	/**
//	 * BEMS 에서 사용 location 명 중복체크를 위한 조회
//	 * 
//	 * @param name
//	 * @return
//	 */
//	public List<Zone> getLocationByName(String name) {
//		return getHibernateTemplate()
//				.find("from Zone where name = ?", name);
//	}
//
//	/**
//	 * BEMS 에서 사용 OrderNo 업데이트
//	 * 
//	 * @param Integer
//	 *            supplierId, Integer parentId, Integer orderNo,Integer
//	 *            oriOrderNo
//	 * @return
//	 */
//	public void updateOrderNo(Integer locationId, Integer parentId,
//			Integer orderNo, Integer oriOrderNo) {
//		StringBuffer hqlBuf = new StringBuffer();
//		hqlBuf.append("UPDATE Zone ");
//		if (orderNo < oriOrderNo) {
//			hqlBuf.append("SET orderno= orderno+1 ");
//		} else {
//			hqlBuf.append("SET orderno= orderno-1 ");
//		}
//		hqlBuf.append("WHERE location_id= ? ");
//		hqlBuf.append("AND parent_id= ? ");
//		if (orderNo < oriOrderNo) {
//			hqlBuf.append("AND orderno >= ? ");
//			hqlBuf.append("AND orderno <= ? ");
//		} else {
//			hqlBuf.append("AND orderno <= ? ");
//			hqlBuf.append("AND orderno >= ? ");
//		}
//		
//		this.getHibernateTemplate().bulkUpdate(hqlBuf.toString(),
//				new Object[] { locationId, parentId, orderNo, oriOrderNo });
//	}
	
	/**
	 * BEMS - 빌딩의 location 에 해당하는 Zone 목록을 조회한다.
	 * @param locationId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Zone> getZonesByLocation(Integer locationId) {
		Query query = getSession().createQuery("from Zone where parent_id is null and location_id = :locationId order by orderno");
		query.setInteger("locationId", locationId);
		
		return query.list();
	}
	
	/**
	 * BEMS 에서 사용 zone 명 중복체크를 위한 조회
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public List<Zone> getZoneByName(String name) {
		Query query = getSession().createQuery("from Zone where name = :name");
		query.setString("name", name);
		
		return query.list();
	}

	/**
	 * BEMS 에서 사용 zone OrderNo 업데이트
	 * 
	 * @param  Integer parentId, Integer orderNo,Integer oriOrderNo
	 * @return
	 */
	public void updateOrderNo(Integer parentId,Integer orderNo, Integer oriOrderNo) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("UPDATE Zone ");
		if (orderNo < oriOrderNo) {
			hqlBuf.append("SET orderno= orderno+1 ");
		} else {
			hqlBuf.append("SET orderno= orderno-1 ");
		}
		hqlBuf.append("WHERE 1=1 ");
		
		if(parentId<1){
			hqlBuf.append("AND parent_id is null  ");
		}else{
			hqlBuf.append("AND parent_id= ? ");
		}
		if (orderNo < oriOrderNo) {
			hqlBuf.append("AND orderno >= ? ");
			hqlBuf.append("AND orderno <= ? ");
		} else {
			hqlBuf.append("AND orderno <= ? ");
			hqlBuf.append("AND orderno >= ? ");
		}
		
		Query query = getSession().createQuery(hqlBuf.toString());
		if(parentId<1){
		    query.setParameter(1, orderNo);
		    query.setParameter(2, oriOrderNo);
		    query.executeUpdate();
			// this.getHibernateTemplate().bulkUpdate(hqlBuf.toString(),new Object[] { orderNo, oriOrderNo });
		}else{
		    query.setParameter(1, parentId);
		    query.setParameter(2, orderNo);
		    query.setParameter(3, oriOrderNo);
		    query.executeUpdate();
			// this.getHibernateTemplate().bulkUpdate(hqlBuf.toString(),new Object[] { parentId, orderNo, oriOrderNo });
		}
		
		
	}

}