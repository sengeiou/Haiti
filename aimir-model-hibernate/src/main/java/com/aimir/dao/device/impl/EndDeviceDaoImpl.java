/**
 * EndDeviceDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.device.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.model.device.EndDevice;
import com.aimir.util.StringUtil;

/**
 * EndDeviceDaoImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 5. 12.   v1.0       김상연         EndDevice 조회 - 조건(EndDevice)
 *
 */
@Repository(value = "enddeviceDao")
public class EndDeviceDaoImpl extends AbstractHibernateGenericDao<EndDevice, Integer> implements EndDeviceDao {

	@Autowired
	protected EndDeviceDaoImpl(SessionFactory sessionFactory) {
		super(EndDevice.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<EndDevice> getEndDevices(int page, int count) {
		int pageSize = count;
		Criteria criteria = getSession().createCriteria(EndDevice.class);
		criteria.addOrder(Order.asc("id")); // 나중에 입력된 최근 글부터 정렬
		criteria.setFirstResult((page - 1) * pageSize); // page는 1부터 2,3... 하지만
		// 이는 입력 되기를 0번 글부터 시작되고
		// pageSize단위로 곱해준다.
		criteria.setMaxResults(pageSize); // 한번에 불러올 리스트 크기를 정의
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
    public List<EndDevice> getEndDevicesByLocationId(int locationId, int page,
			int count) {
		Criteria criteria = getSession().createCriteria(EndDevice.class);
		criteria.add(Restrictions.eq("location.id", locationId));
		criteria.addOrder(Order.asc("id")); 
		criteria.setFirstResult((page - 1) * count); // page는 1부터 2,3... 하지만
		// 이는 입력 되기를 0번 글부터 시작되고
		// pageSize단위로 곱해준다.
		criteria.setMaxResults(count); // 한번에 불러올 리스트 크기를 정의
		List<EndDevice> result = (List<EndDevice>) criteria.list();
		return result;
	}

	@SuppressWarnings("unchecked")
    public List<EndDevice> getEndDevicesByLocationId(int locationId) {
		Criteria criteria = getSession().createCriteria(EndDevice.class);
		criteria.add(Restrictions.eq("location.id", locationId));
		criteria.addOrder(Order.asc("id")); 
		
        List<EndDevice> result = (List<EndDevice>) criteria.list();
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public List<EndDevice> getEndDevicesByCategories(List<Integer> categories) {
		Criteria criteria = getSession().createCriteria(EndDevice.class);
		criteria.add(Restrictions.in("categoryCode.id", categories));

		List<EndDevice> result = (List<EndDevice>) criteria.list();
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public List<EndDevice> getEndDevicesByzones(List<Integer> zones) {
		Criteria criteria = getSession().createCriteria(EndDevice.class);
		criteria.add(Restrictions.in("zone.id", zones));

        List<EndDevice> result = (List<EndDevice>) criteria.list();
		return result;
	}
	@SuppressWarnings("unchecked")
    public List<EndDevice> getEndDevicesByLocations(List<Integer> locationIds) {
		Criteria criteria = getSession().createCriteria(EndDevice.class);
		criteria.add(Restrictions.in("location.id", locationIds));

        List<EndDevice> result = (List<EndDevice>) criteria.list();
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public List<EndDevice> getEndDevicesByParentLocations(List<Integer> locationIds,List<Integer> categories) {
		Criteria criteria = getSession().createCriteria(EndDevice.class);
		criteria.add(Restrictions.in("location.id", locationIds));
		criteria.add(Restrictions.in("categoryCode.id", categories));
        List<EndDevice> result = (List<EndDevice>) criteria.list();
		return result;
	}
    
    @SuppressWarnings("unchecked")
	public List<Object> getEndDeviceTypeAndStatusCountByZones(List<Integer> zones){
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT   e.categoryCode.descr as category,e.statusCode.code as status,count(e.categoryCode.descr) as cnt ");
        sb.append(" FROM     EndDevice e INNER JOIN e.zone zone  ");
        sb.append(" WHERE    zone.id in(:zones) ");
        sb.append(" GROUP BY e.categoryCode.descr,e.statusCode.code ");

		Query query = getSession().createQuery(sb.toString());
		query.setParameterList("zones", zones);
		
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }
    
	// EndDevice 정보 저장
	public void updateZoneOfEndDevice(EndDevice endDevice) {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE EndDevice e ");
		sb.append("SET e.zone.id = :zoneId ");
		sb.append("WHERE e.id = :deviceId ");					
	
		//HQL문을 이용한 CUD를 할 경우에는 getSession().bulkUpdate() 메소드를 사용한다.		
		Query query = this.getSession().createQuery(sb.toString());
        query.setInteger("zoneId", endDevice.getZone().getId());
        query.setInteger("deviceId", endDevice.getId());
        query.executeUpdate();
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
		sb.append("SELECT t.id, t.friendly_name ")
		  .append("FROM ENDDEVICE t LEFT JOIN GROUP_MEMBER g ON t.friendly_name = g.member ")
		  .append("WHERE t.supplier_id = :supplierId ");
		if(!"".equals(member)){
			sb.append("AND t.friendly_name like '%").append((String)condition.get("member")).append("%'");
		}
		sb.append("AND t.friendly_name NOT IN ( ");
			sb.append("SELECT t.friendly_name ");
			sb.append("FROM ENDDEVICE t RIGHT JOIN GROUP_MEMBER g ON t.friendly_name = g.member ");
			sb.append("WHERE t.supplier_id = :supplierId ");
		sb.append(") ");
		sb.append(" ORDER BY t.friendly_name ASC");

		SQLQuery query = getSession().createSQLQuery(sb.toString());
		return query.setInteger("supplierId", Integer.parseInt((String)condition.get("supplierId")))
					.list();
    }

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : Group Management 가젯에서 Member 로 등록 가능한 End Device 리스트를 조회한다.
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
        String subType = StringUtil.nullToBlank(conditionMap.get("subType"));
        String memberName = StringUtil.nullToBlank(conditionMap.get("memberName"));
        int page = (Integer)conditionMap.get("page");
        int limit = (Integer)conditionMap.get("limit");
        
        StringBuilder sb = new StringBuilder();
        StringBuilder sbQuery = new StringBuilder();
        
        sbQuery.append("\nSELECT de.id AS value, ");
        sbQuery.append("\n       de.friendlyName AS text, ");
        sbQuery.append("\n       'EndDevice' AS type ");
        sb.append("\nFROM EndDevice de ");
        sb.append("\nWHERE de.supplier.id = :supplierId ");

        if (!memberName.isEmpty()) {
            sb.append("\nAND   de.friendlyName LIKE :memberName ");
        }

        sb.append("\nAND   NOT EXISTS ( ");
        sb.append("\n    SELECT 'X' ");
        sb.append("\n    FROM GroupMember gm, HomeGroup hg ");
        sb.append("\n    WHERE gm.groupId = hg.id ");
        sb.append("\n    AND gm.member = de.friendlyName ");
        if(subType.isEmpty()) {
        	sb.append("\n    AND   gm.aimirGroup.id = :groupId ");
        }
        sb.append("\n) ");
        sb.append("\nORDER BY de.friendlyName ");
        
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
        if(subType.isEmpty()) {
        	query.setInteger("groupId", groupId);
        }
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

	/* (non-Javadoc)
	 * @see com.aimir.dao.device.EndDeviceDao#getEndDevices(com.aimir.model.device.EndDevice)
	 */
	@SuppressWarnings("unchecked")
	public List<EndDevice> getEndDevices(EndDevice endDevice) {

		Criteria criteria = getSession().createCriteria(EndDevice.class);
		
		if (endDevice != null) {
			
			if (endDevice.getModem() != null) {
				
				if (endDevice.getModem().getId() != null) {
					
					criteria.add(Restrictions.eq("modem.id", endDevice.getModem().getId()));
				} 
			}
			
			if (endDevice.getCategoryCode() != null) {
				
				if (endDevice.getCategoryCode().getId() != null) {
					
					criteria.add(Restrictions.eq("categoryCode.id", endDevice.getCategoryCode().getId()));
				}
			}
			
			if (endDevice.getSerialNumber() != null) {
				
				criteria.add(Restrictions.ne("serialNumber", endDevice.getSerialNumber()));
			}
		}

		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getHomeDeviceInfo(String groupId, String homeDeviceGroupName, String homeDeviceCategory){

		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT e.id AS id ")
		.append(", e.homeDevice_Group_Name AS homeDeviceGroupName  ")
		.append(", e.friendly_Name AS friendlyName ")
		.append(", e.drlevel AS drlevel ")
		.append(", e.homeDevice_Img_Filename AS homeDeviceImgFilename ")
		.append(", e.modem_id AS modemId ")	
		.append(", e.category_id AS categoryId ")
		.append(", e.serial_Number AS serialNumber ")
		.append(", g.id AS groupMemberId ")
		.append(", c.name AS installStatus ")
		.append(", c.code AS installStatusCode ")
		.append(" FROM ENDDEVICE e left join GROUP_MEMBER g on e.serial_number = g.member, Code c ")
		.append(" WHERE g.group_id = :groupId ") 
		.append(" AND e.installStatus_id = c.id ");

		if (homeDeviceGroupName.length() > 0) {
			sbSql.append(" AND e.homeDevice_group_name = '").append(homeDeviceGroupName).append("' ");
		}
		
		if (homeDeviceCategory.length() > 0) {
			sbSql.append(" AND e.category_id = ").append(Integer.parseInt(homeDeviceCategory)).append(" ");
		}

		sbSql.append(" ORDER BY e.homeDevice_Group_name, e.friendly_Name ");

		SQLQuery query = getSession().createSQLQuery(sbSql.toString());
		query.setInteger("groupId", Integer.parseInt(groupId));		

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getHomeDeviceInfoForDrMgmt(String groupId, String homeDeviceGroupName, String homeDeviceCategory){

		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT e.id AS id ")
		.append(", e.homeDevice_Group_Name AS homeDeviceGroupName  ")
		.append(", e.friendly_Name AS friendlyName ")
		//.append(", e.drlevel AS drlevel ")
		.append(", e.homeDevice_Img_Filename AS homeDeviceImgFilename ")
		.append(", e.modem_id AS modemId ")	
		.append(", e.category_id AS categoryId ")
		.append(", e.serial_Number AS serialNumber ")
		.append(", c.name AS installStatus ")
		.append(" FROM ENDDEVICE e left join GROUP_MEMBER g on e.serial_number = g.member, Code c ")
		.append(" WHERE g.group_id = :groupId ") 
		.append(" AND e.installStatus_id = c.id ");

		
		if (homeDeviceGroupName.length() > 0) {
			sbSql.append(" AND e.homeDevice_group_name = '").append(homeDeviceGroupName).append("' ");
		}
		
		if (homeDeviceCategory.length() > 0) {
			sbSql.append(" AND e.category_id = ").append(Integer.parseInt(homeDeviceCategory)).append(" ");
		}

		sbSql.append(" ORDER BY e.homeDevice_Group_name, e.friendly_Name ");

		SQLQuery query = getSession().createSQLQuery(sbSql.toString());
		query.setInteger("groupId", Integer.parseInt(groupId));		

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<EndDevice> getMappingHomeDeviceForDrMgmt(int modeId, int codeId) {
		Criteria criteria = getSession().createCriteria(EndDevice.class);
		criteria.add(Restrictions.eq("categoryCode.id", codeId));		
		criteria.add(Restrictions.eq("modem.id", modeId));

		return criteria.list();

	}


	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getHomeDeviceGroupCnt(String groupId, int smartConcent, int generalAppliance){

		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT e.homeDevice_group_name AS homeDeviceGroupName ")
		.append(", count(*) AS cnt  ")
		.append(" FROM ENDDEVICE e left join GROUP_MEMBER g on e.serial_number = g.member ")
		.append(" WHERE g.group_id = :groupId ")
		.append(" AND (e.category_id = :smartConcent ")
		.append(" OR e.category_id = :generalAppliance) ")
		.append(" GROUP BY e.homeDevice_group_name,e.category_id ")
        .append(" ORDER BY e.homeDevice_Group_name, cnt ");

		SQLQuery query = getSession().createSQLQuery(sbSql.toString());
		query.setInteger("groupId", Integer.parseInt(groupId));		
		query.setInteger("smartConcent", smartConcent);	
		query.setInteger("generalAppliance", generalAppliance);	

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@SuppressWarnings("unchecked")
	public List<EndDevice> getMappingHomeDevice(int modeId, int codeId) {
		Criteria criteria = getSession().createCriteria(EndDevice.class);
		criteria.add(Restrictions.eq("categoryCode.id", codeId));		
		criteria.add(Restrictions.eq("modem.id", modeId));

//		Map<String, Object> result = new HashMap<String, Object>();
		return criteria.list();
		
//		if(list.size() > 0) {
//			for(EndDevice tmp : list){
//				result.put("mappingFriendlyName", tmp.getFriendlyName());
//				result.put("mappingImg", tmp.getHomeDeviceImgFilename());
//				result.put("mappingDrProgramMandatory", tmp.getDrProgramMandatory());
//				result.put("mappingDrLevel", tmp.getDrLevel());
//			}
//		} else {
//			result.put("mappingFriendlyName", "");
//			result.put("mappingImg", "");
//			result.put("mappingDrProgramMandatory", "");
//			result.put("mappingDrLevel", "");
//		}
//		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getHomeDeviceGroupSelected(String groupId){

		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT e.homeDevice_Group_Name AS name  ")
		.append(" FROM ENDDEVICE e left join GROUP_MEMBER g on e.serial_number = g.member ")
		.append(" WHERE g.group_id = :groupId ")
		.append(" GROUP BY e.homedevice_group_name ORDER BY e.homeDevice_Group_name ");

		SQLQuery query = getSession().createSQLQuery(sbSql.toString());
		query.setInteger("groupId", Integer.parseInt(groupId));

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getHomeDeviceCategorySelected(String groupId){

		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT c.name AS name  ")
		.append(", c.id AS id ")
		.append(", c.code AS code ")
		.append(" FROM ENDDEVICE e left join GROUP_MEMBER g on e.serial_number = g.member, Code c ")
		.append(" WHERE g.group_id = :groupId ")
		.append(" AND e.category_id = c.id ")
		.append(" GROUP BY c.name, c.id, c.code ORDER BY c.code ");

		SQLQuery query = getSession().createSQLQuery(sbSql.toString());
		query.setInteger("groupId", Integer.parseInt(groupId));

		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	// EndDevice 정보 저장
	public void updateMappingInfo(int id, int modemId) {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE EndDevice e ")
		.append("SET e.modem.id = :modemId ")
		.append("WHERE e.id = :deviceId ");

		//HQL문을 이용한 CUD를 할 경우에는 getSession().bulkUpdate() 메소드를 사용한다.		
		Query query = getSession().createQuery(sb.toString());
		query.setInteger("modemId", modemId);
		query.setInteger("deviceId", id);
		query.executeUpdate();
		// this.getSession().bulkUpdate(sb.toString(), new Object[] {modemId, id} );
	}
	
	// EndDevice 정보 저장
	public void updateEndDeviceInfo(int id, String homeDeviceGroupName, String friendlyName) {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE EndDevice e ")
		.append("SET e.homeDeviceGroupName = ? ")
		.append(",e.friendlyName = ? ")
		.append("WHERE e.id = ? ");

		//HQL문을 이용한 CUD를 할 경우에는 getSession().bulkUpdate() 메소드를 사용한다.		
		//this.getSession().bulkUpdate(sb.toString(), new Object[] {homeDeviceGroupName, friendlyName, id} );
	}
	
	public void resetMappingInfo(int modemId, int categoryId){
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE EndDevice e ")
		.append("SET e.modem.id = null ")
		.append("WHERE e.modem.id = ? ")
		.append("AND e.categoryCode.id = ? ");
		//HQL문을 이용한 CUD를 할 경우에는 getSession().bulkUpdate() 메소드를 사용한다.		
		//this.getSession().bulkUpdate(sb.toString(), new Object[] {modemId, categoryId} );
	}
	
	public void resetMappingInfo(int endDeviceId){
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE EndDevice e ")
		.append("SET e.modem.id = null ")
		.append("WHERE e.id = ? ");
		//HQL문을 이용한 CUD를 할 경우에는 getSession().bulkUpdate() 메소드를 사용한다.		
		//this.getSession().bulkUpdate(sb.toString(), new Object[] {endDeviceId} );
	}

	// Home Device 인스톨 상태 변경
	public void updateEndDeviceInstallStatus(int installStatusCode, String serialNumber, int categoryCode) {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE EndDevice e ")
		.append("SET e.installStatus_id = ? ")
		.append("WHERE e.serialNumber = ? ")
		.append("AND e.categoryCode.id = ? ");

		//HQL문을 이용한 CUD를 할 경우에는 getSession().bulkUpdate() 메소드를 사용한다.		
		//this.getSession().bulkUpdate(sb.toString(), new Object[] {installStatusCode, serialNumber, categoryCode} );
	}

	// EndDevice 정보 저장
	public void updateDrProgramMandatoryInfo(int id, String drProgramMandatory) {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE EndDevice e ")
		.append("SET e.drProgramMandatory = ? ")
		.append("WHERE e.id = ? ");

		//HQL문을 이용한 CUD를 할 경우에는 getSession().bulkUpdate() 메소드를 사용한다.		
		//this.getSession().bulkUpdate(sb.toString(), new Object[] {Boolean.parseBoolean(drProgramMandatory), id} );
	}
	
	// Home Device DR Level 변경
	public void updateEndDeviceDrLevel(int endDeviceId, int categoryCode, int drLevel) {
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE EndDevice e ")
		.append("SET e.drLevel = ? ")
		.append("WHERE e.id = ? ")
		.append("AND e.categoryCode.id = ? ");

		//HQL문을 이용한 CUD를 할 경우에는 getSession().bulkUpdate() 메소드를 사용한다.		
		//this.getSession().bulkUpdate(sb.toString(), new Object[] {drLevel, endDeviceId, categoryCode} );
	}
	
	// Extjs 페이징 및 로케이션 아이디 검색기능
    @SuppressWarnings("unchecked")
	public List<EndDevice> getEndDevicesByLocationIds(List<Integer> locationIds, int start, int limit) {
    	Criteria criteria = getSession().createCriteria(EndDevice.class);
		criteria.addOrder(Order.asc("id"));
		if(locationIds != null) {
			criteria.add(Restrictions.in("location.id", locationIds));
		}
		criteria.setFirstResult(start);
		criteria.setMaxResults(limit);
		List<EndDevice> result = (List<EndDevice>) criteria.list();
		return result;
	}

    /**
     * 모든 행 수 얻기
     */
	@Override
	public long getTotalSize(List<Integer> locationIds) {
		Criteria criteria = getSession().createCriteria(EndDevice.class);
		if(locationIds != null) {
			criteria.add(Restrictions.in("location.id", locationIds));
		}
		return (Long) criteria.setProjection(Projections.count("id")).uniqueResult();
	}
	
    /**
     * method name : getEndDeviceByFriendlyName<b/>
     * method Desc : HomeGroup Management 가젯에서 FriendlyName을 가진 EndDevice를 구한다.
     *
     * @param String friendlyName
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<EndDevice> getEndDeviceByFriendlyName(String friendlyName) {

        StringBuilder sb = new StringBuilder();

        sb.append("\nSELECT de.id AS id, ");
        sb.append("\n       de.serialNumber AS serialNumber, ");
        sb.append("\n       de.uuid AS uuid, ");
        sb.append("\n       de.upc AS upc ");
        sb.append("\nFROM EndDevice de ");
        sb.append("\nWHERE de.friendlyName = :friendlyName ");

        Query query = getSession().createQuery(sb.toString());
        query.setString("friendlyName", friendlyName);
        

        return query.list();
    }
}
