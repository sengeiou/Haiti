package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.model.system.Location;
import com.aimir.util.SQLWrapper;
import com.aimir.util.StringUtil;

@Repository(value = "locationDao")
public class LocationDaoImpl extends
		AbstractHibernateGenericDao<Location, Integer> implements LocationDao {

	@Autowired
	protected LocationDaoImpl(SessionFactory sessionFactory) {
		super(Location.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Location> getLocations() {
		Criteria criteria = getSession().createCriteria(Location.class);
		return criteria.list();
		
		/*DetachedCriteria criteria = DetachedCriteria.forClass(Location.class);
		return (List<Location>)getHibernateTemplate().findByCriteria(criteria);*/
	}

	@SuppressWarnings("unchecked")
	public List<Location> getParents() {
		Query query = getSession().createQuery("from Location where parent is null");
		return query.list();
		
		/*return (List<Location>)getHibernateTemplate()
				.find("from Location where parent is null");*/
	}

	@SuppressWarnings("unchecked")
	public List<Location> getParentsBySupplierId(Integer supplierId) {
		Query query = getSession().createQuery("from Location where supplier_id = :supplierId AND parent is null order by orderno");
		query.setInteger("supplierId", supplierId);
		
		return query.list();
		/*return (List<Location>)getHibernateTemplate()
				.find("from Location where supplier_id = ? AND parent is null order by orderno",supplierId);*/
	}

	@SuppressWarnings("unchecked")
	public List<Location> getChildrenBySupplierId(Integer supplierId) {
		if (supplierId == null || supplierId == 0) {
//			return getHibernateTemplate()
//					.find(
//							"from Location where id NOT IN "
//									+ "(select DISTINCT parent from Location where parent is not null) order by orderno");
			
			Query query = getSession().createQuery("select pa from Location ch right outer join ch.parent pa where ch.id is null order by pa.orderNo");
			return query.list();
			
            // return (List<Location>)getHibernateTemplate().find("select pa from Location ch right outer join ch.parent pa where ch.id is null order by pa.orderNo");
		} else {
//			return getHibernateTemplate()
//					.find(
//							"from Location where supplier_id = ? AND id NOT IN "
//									+ "(select DISTINCT parent from Location where parent is not null) order by orderno",
//							supplierId);
			
			Query query = getSession().createQuery("select pa from Location ch right outer join ch.parent pa where pa.supplier.id = :supplierId and ch.id is null order by pa.orderNo");
			query.setInteger("supplierId", supplierId);
			return query.list();
			
            // return (List<Location>)getHibernateTemplate().find("select pa from Location ch right outer join ch.parent pa where pa.supplier.id = ? and ch.id is null order by pa.orderNo",supplierId);
		}
	}

	public int getParentsBykeyWord(Integer supplierId, String keyWord) {
		Query query = getSession().createQuery("select count(*) from Location where name like :keyWord AND supplier_id = :supplierId");
		query.setString("keyWord", "%" + keyWord + "%");
		query.setInteger("supplierId", supplierId);
		return DataAccessUtils.intResult(query.list());
		
		
		// return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from Location where name like ? AND supplier_id = ?","%" + keyWord + "%", supplierId));
	}

	@SuppressWarnings("unchecked")
	public List<Location> getChildren(Integer parentId) {
		Query query = getSession().createQuery("from Location where parent_id = :parentId order by orderno");
		query.setInteger("parentId", parentId);
		return query.list();
		
		// return (List<Location>)getHibernateTemplate().find("from Location where parent_id = ? order by orderno", parentId);
	}

	@SuppressWarnings("unchecked")
	public List<Location> getChildren(Integer parentId, Integer supplierId) {
		if (supplierId == null || supplierId == 0) {
			Query query = getSession().createQuery("from Location where parent_id = :parentId order by orderno");
			query.setInteger("parentId", parentId);
			return query.list();
			
			// return (List<Location>)getHibernateTemplate().find("from Location where parent_id = ? order by orderno", parentId);
		} else {
			Query query = getSession().createQuery("from Location where parent_id = :parentId and supplier_id = :supplierId order by orderno");
			query.setInteger("parentId", parentId);
			query.setInteger("supplierId", supplierId);
			return query.list();
			
			// return (List<Location>)getHibernateTemplate().find("from Location where parent_id = ? and supplier_id = ? order by orderno",parentId, supplierId);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Location> getParents(Integer supplierId) {
		if (supplierId == null || supplierId == 0) {
			Query query = getSession().createQuery("from Location where parent is null order by orderno");
			return query.list();
			
			// return (List<Location>)getHibernateTemplate().find("from Location where parent is null order by orderno");
		} else {
			Query query = getSession().createQuery("from Location where supplier_id = :supplierId AND parent is null order by orderno");
			query.setInteger("supplierId", supplierId);
			return query.list();
			
			// return (List<Location>)getHibernateTemplate().find("from Location where supplier_id = ? AND parent is null order by orderno", supplierId);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Location> getLocations(Integer supplierId) {
		if (supplierId == null || supplierId == 0) {
			return getAll();
		} else {
			Query query = getSession().createQuery("from Location where supplier_id = :supplierId order by orderno");
			query.setInteger("supplierId", supplierId);
			return query.list();
			
			// return (List<Location>)getHibernateTemplate().find("from Location where supplier_id = ? order by orderno", supplierId);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getParentId(Integer locationId) {
		Query query = getSession().createQuery("SELECT parent.id FROM Location WHERE id = :locationId");
		query.setInteger("locationId", locationId);
		return query.list();
		
		// return (List<Integer>)getHibernateTemplate().find("SELECT parent.id FROM Location WHERE id = ? ", locationId);
	}

	public List<Integer> getLeafLocationId(Integer locationId,
			Integer supplierId) {

		List<Integer> locationIdList = new ArrayList<Integer>();
		try {

			List<Location> locations = null;
			if (locationId == null || locationId.equals(0)) {
				locations = getParents(supplierId);
			} else {
				locations = getChildren(locationId, supplierId);
			}

			for (Location location : locations) {
				if (location.getChildren() != null
						&& location.getChildren().size() > 0) {
					locationIdList.addAll(getLeafLocation(location
							.getChildren()));
				} else {
					locationIdList.add(location.getId());
				}
			}

			if (locations == null || locations.size() < 1) {
				locationIdList.add(locationId);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return locationIdList;
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public List<Integer> getChildNodesInLocation(Integer locationId,			Integer supplierId)
	{
		
		/**
		 * 
		 * select * from location loc where loc.PARENT_ID=15 and loc.SUPPLIER_ID=22
		 */
    	
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT id FROM Location  ");
		sb.append(" WHERE parent_id=:locationId AND supplier_id=:supplierId");
	

		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		
		query.setInteger("supplierId", supplierId);
		query.setInteger("locationId", locationId);
		
		
		return query.list();
		
	}
	
	
	public String getChildNodesInLocationCnt(Integer locationId,Integer supplierId)
	{
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT count(*) FROM Location  ");
		sb.append(" WHERE parent_id=:locationId AND supplier_id=:supplierId");
	

		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		
		query.setInteger("supplierId", supplierId);
		query.setInteger("locationId", locationId);
		
		Object object = query.uniqueResult();
		
		return object.toString();
		
	}

	/**
	 * 최하위 지역 List 를 반환하는 재귀함수
	 * 
	 * @param locations
	 * @return
	 */
	private List<Integer> getLeafLocation(Set<Location> locations) {
		List<Integer> locationIdList = new ArrayList<Integer>();
		for (Location location : locations) {
			if (location.getChildren() != null
					&& location.getChildren().size() > 0) {
				locationIdList.addAll(getLeafLocation(location.getChildren()));
			} else {
				locationIdList.add(location.getId());
			}
		}
		return locationIdList;
	}
	
	
	public List<Integer> getChildLocationId(Integer locationId) {

		List<Integer> locationIdList = new ArrayList<Integer>();
		try {

			List<Location> locations = null;
			if (locationId == null || locationId.equals(0)) {
				locations = getParents();
			} else {
				locations = getChildren(locationId);
			}

			for (Location location : locations) {
				locationIdList.add(location.getId());
				if (location.getChildren() != null && location.getChildren().size() > 0) {
					locationIdList.addAll(getChildLocation(location.getChildren()));
				}
			}
			if (locations == null || locations.size() < 1) {
				locationIdList.add(locationId);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return locationIdList;
	}

	/**
	 * 하위 지역 List 를 반환하는 재귀함수
	 * 
	 * @param locations
	 * @return
	 */
	private List<Integer> getChildLocation(Set<Location> locations) {
		List<Integer> locationIdList = new ArrayList<Integer>();
		for (Location location : locations) {
			locationIdList.add(location.getId());
			if (location.getChildren() != null
					&& location.getChildren().size() > 0) {
				locationIdList.addAll(getChildLocation(location.getChildren()));
			} 
		}
		return locationIdList;
	}

	/**
	 * BEMS 에서 사용 location 명 중복체크를 위한 조회
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public List<Location> getLocationByName(String name) {
		Query query = getSession().createQuery("from Location where name = :name");
		query.setString("name", name);
		return query.list();
		
		// return (List<Location>)getHibernateTemplate().find("from Location where name = ?", name);
	}

	/**
	 * BEMS 에서 사용 OrderNo 업데이트
	 * 
	 * @param Integer
	 *            supplierId, Integer parentId, Integer orderNo,Integer
	 *            oriOrderNo
	 * @return
	 */
	public void updateOrderNo(Integer supplierId, Integer parentId,
			Integer orderNo, Integer oriOrderNo) {

		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("UPDATE Location ");
		if (orderNo < oriOrderNo) {
			hqlBuf.append("SET orderno= orderno+1 ");
		} else {
			hqlBuf.append("SET orderno= orderno-1 ");
		}
		hqlBuf.append("WHERE supplier_id= ? ");
		hqlBuf.append("AND parent_id= ? ");
		if (orderNo < oriOrderNo) {
			hqlBuf.append("AND orderno >= ? ");
			hqlBuf.append("AND orderno <= ? ");
		} else {
			hqlBuf.append("AND orderno <= ? ");
			hqlBuf.append("AND orderno >= ? ");
		}
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter(1, supplierId);
		query.setParameter(2, parentId);
		query.setParameter(3, orderNo);
		query.setParameter(4, oriOrderNo);
		query.executeUpdate();
		// this.getHibernateTemplate().bulkUpdate(hqlBuf.toString(), new Object[] { supplierId, parentId, orderNo, oriOrderNo });
	}
	/**
	 * BEMS 에서 사용 root 지역 가져오기
	 * 
	 * @param Integer
	 *            supplierId, Integer parentId, Integer orderNo,Integer
	 *            oriOrderNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getRoot() {
		Query query = getSession().createQuery("SELECT id FROM Location WHERE parent.id is NULL");
		return query.list();
		
		// return (List<Integer>)getHibernateTemplate().find("SELECT id FROM Location WHERE parent.id is NULL");
	}

	/**
	 * 그룹 관리 중 멤버 리스트 조회
	 * 
	 * @param condition
	 * @return
	 */
    @SuppressWarnings("unchecked")
    @Deprecated
	public List<Object> getGroupMember(Map<String, Object> condition){
    	String member = StringUtil.nullToBlank(condition.get("member"));
    	
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT t.id, t.name ")
		  .append("FROM LOCATION t LEFT JOIN GROUP_MEMBER g ON t.name = g.member ")
		  .append("WHERE t.supplier_id = :supplierId ");
		if(!"".equals(member)){
			sb.append("AND t.name like '%").append((String)condition.get("member")).append("%'");
		}
		sb.append("AND t.name NOT IN ( ");
			sb.append("SELECT t.name ");
			sb.append("FROM LOCATION t RIGHT JOIN GROUP_MEMBER g ON t.name = g.member ");
			sb.append("WHERE t.supplier_id = :supplierId ");
		sb.append(") ");
		sb.append(" ORDER BY t.name ASC");

		SQLQuery query = getSession().createSQLQuery(sb.toString());
		return query.setInteger("supplierId", Integer.parseInt((String)condition.get("supplierId")))
					.list();
    }

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : Group Management 가젯에서 Member 로 등록 가능한 Location 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {   	
    	List<Object> gridData 	= new ArrayList<Object>();		
    	List<Object> result		= new ArrayList<Object>();
    	
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer groupId = (Integer)conditionMap.get("groupId");
        String memberName = StringUtil.nullToBlank(conditionMap.get("memberName"));
        String locationId = StringUtil.nullToBlank(conditionMap.get("locationId"));
        int page = (Integer)conditionMap.get("page");
        int limit = (Integer)conditionMap.get("limit");
        
        StringBuilder sb = new StringBuilder();
        StringBuilder sbQuery = new StringBuilder();

        sbQuery.append("\nSELECT lo.id AS value, ");
        sbQuery.append("\n       lo.name AS text, ");
        sbQuery.append("\n       'Location' AS type ");
        
        sb.append("\nFROM Location lo ");
        sb.append("\nWHERE lo.supplier.id = :supplierId ");

        if (!memberName.isEmpty()) {
            sb.append("\nAND   lo.name LIKE :memberName ");
        }
        
        if(!locationId.equals("")){
        	sb.append("\nAND lo.id = '"+ locationId +"' ");
        }
        
        sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    SELECT 'X' ");
        sb.append("\n    FROM GroupMember gm ");
        sb.append("\n    WHERE gm.member = lo.name ");
        sb.append("\n    AND   gm.aimirGroup.id = :groupId ");
        sb.append("\n) ");
        sb.append("\nORDER BY lo.name ");
     // 전체 건수
        StringBuffer countQuery = new StringBuffer();
        countQuery.append("\n SELECT COUNT( * ) ");
        countQuery.append(sb);
        
        Query countQueryObj = getSession().createQuery(countQuery.toString());
        countQueryObj.setInteger("supplierId", supplierId);
        countQueryObj.setInteger("groupId", groupId);
        if (!memberName.isEmpty()) {
        	countQueryObj.setString("memberName", "%" + memberName + "%");
        }
        
        Number totalCount = (Number)countQueryObj.uniqueResult();
        result.add(totalCount.toString());

        sbQuery.append(sb);
        
        Query query = getSession().createQuery(sbQuery.toString());
        query.setInteger("supplierId", supplierId);
        query.setInteger("groupId", groupId);
       /*if(locationId != null)
        	query.setInteger("locationId", locationId);*/
        if (!memberName.isEmpty()) {
            query.setString("memberName", "%" + memberName + "%");
        }

        query.setFirstResult((page-1) * limit);      
        query.setMaxResults(limit);
        
        List dataList = null;
		dataList = query.list();
		
		// 실제 데이터
		int dataListLen = 0;
		if(dataList != null)
			dataListLen= dataList.size();
				
		for (int i = 0; i < dataListLen; i++) {
			HashMap chartDataMap = new HashMap();
			Object[] resultData = (Object[]) dataList.get(i);
			
			chartDataMap.put("value",           resultData[0] );
			chartDataMap.put("text",      resultData[1]);                 
			chartDataMap.put("type",    resultData[2]);
			gridData.add(chartDataMap);
		}
		
		result.add(gridData);
		
		return result;
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getParentIdImmediate(Integer locationId) {
		StringBuffer sbQuery = new StringBuffer();
		sbQuery.append("SELECT PARENT_ID,SUPPLIER_ID FROM LOCATION WHERE ID= ? ");
		//System.out.println(sbQuery.toString());
		SQLQuery query = getSession().createSQLQuery(sbQuery.toString());
		query.setInteger(0, locationId);
				
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		//return null;
	}

    /**
     * method name : getRootLocationList<b/>
     * method Desc : location tree 의 root location list 를 조회한다.
     *
     * @return List of {@link com.aimir.model.system.Location}
     */
    @SuppressWarnings("unchecked")
    public List<Location> getRootLocationList() {
        StringBuilder sb = new StringBuilder();

//        sb.append("\nSELECT p.id AS P_ID, ");
//        sb.append("\n       p.name AS P_NAME, ");
//        sb.append("\n       c.id AS C_ID, ");
//        sb.append("\n       c.name AS C_NAME ");
        sb.append("\nFROM Location ");
        sb.append("\nWHERE parent is null ");
//        sb.append("\n   OR id = 8 ");

        Query query = getSession().createQuery(sb.toString());
        return query.list();
    }

    /**
     * method name : getRootLocationListBySupplier<b/>
     * method Desc : 공급사에 따라 location tree 의 root location list 를 조회한다.
     *
     * @return List of {@link com.aimir.model.system.Location}
     */
    @SuppressWarnings("unchecked")
    public List<Location> getRootLocationListBySupplier(Integer supplierId) {
        StringBuilder sb = new StringBuilder();

        sb.append("\nFROM Location ");
        sb.append("\nWHERE parent is null ");
        if(supplierId != null)
        	sb.append("\nAND   supplierId = :supplierId");

        Query query = getSession().createQuery(sb.toString());
        query.setInteger("supplierId", supplierId);
        return query.list();
    }
    
    /**
     * method name : getLocationTreeForMeteringRate<b/>
     * method Desc :
     *
     * @param supplierId Supplier.id
     * @return List of Map {}
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getLocationTreeForMeteringRate(Integer supplierId) {
        StringBuilder sb = new StringBuilder();

//        sb.append("\nSELECT p.id AS P_ID, ");
//        sb.append("\n       p.name AS P_NAME, ");
//        sb.append("\n       c.id AS C_ID, ");
//        sb.append("\n       c.name AS C_NAME ");
//        sb.append("\nFROM location p ");
//        sb.append("\n     LEFT OUTER JOIN ");
//        sb.append("\n     location c ");
//        sb.append("\n     ON  p.id = c.parent_id ");
//        sb.append("\n     AND p.supplier_id = :supplierId ");
        
        sb.append("\nSELECT p.id AS P_ID, ");
        sb.append("\n       p.name AS P_NAME, ");
        sb.append("\n       c.id AS C_ID, ");
        sb.append("\n       c.name AS C_NAME ");
        sb.append("\nFROM location p, ");
        sb.append("\n     location c ");
        sb.append("\nWHERE p.id = c.parent_id ");
        sb.append("\nAND   p.supplier_id = :supplierId ");
        sb.append("\nAND   c.supplier_id = p.supplier_id ");

        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        query.setInteger("supplierId", supplierId);
        
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
    public Boolean isRoot(Integer locationId) {
    	Boolean isRoot = true;
    	if ( locationId != null ) {
    		Location location = get(locationId);
    		Location parent = location.getParent();
    		if ( parent != null) {
    			isRoot = false;
    		}
    	}
		return isRoot;
    }
    
    public String getNameByGeocode(String geocode, Integer supplierId) {
        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT name ");
        sb.append("\nFROM Location ");
        sb.append("\nWHERE 1=1 ");
        if(supplierId != null)
        	sb.append("\nAND   supplierId = :supplierId");
        if(geocode != null)
        	sb.append("\nAND   geocode = :geocode");

        Query query = getSession().createQuery(sb.toString());
        if(supplierId != null)
        	query.setInteger("supplierId", supplierId);
        if(geocode != null)
        	query.setString("geocode", geocode);
        
        String returnData = null;
        if(query.list() != null && query.list().size() > 0) {
        	returnData = (String)query.list().get(0);
        }
        
        return returnData;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public List<String> getLocationsName() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("\nSELECT name ");
        sb.append("\nFROM Location ");
        SQLQuery query = getSession().createSQLQuery(new SQLWrapper().getQuery(sb.toString()));
        return query.list();
    }
}