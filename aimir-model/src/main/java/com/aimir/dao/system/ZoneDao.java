package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Zone;

public interface ZoneDao extends GenericDao<Zone, Integer> {

	/**
	 * method name : getParents
	 * method Desc : Zone 중에서 parent에 해당하는 Zone를 리턴한다.
	 * 
	 * @return List of Zone @see com.aimir.model.system.Zone
	 */
	public List<Zone> getParents();
	
	/**
	 * method name : getChildren
	 * method Desc : parent id에 해당하는 Zone의 Child 목록을 리턴한다.
	 * 
	 * @param parentId Zone.parent.id
	 * @return List of Zone @see com.aimir.model.system.Zone
	 */
	public List<Zone> getChildren(Integer parentId);

	
	/**
	 * method name : getLeafZoneId
	 * method Desc : 입력한 지역id의 하위 지역중에서 최하위지역id 목록을 조회한다.
	 * 
	 * 
	 * @param zoneId Zone.id
	 * @return 아이디 목록을 리턴
	 */
	public List<Integer> getLeafZoneId(Integer zoneId);

	
	/**
	 * method name : getZonesByLocation
	 * method Desc : BEMS - 빌딩의 location 에 해당하는 Zone 목록을 조회한다.
	 * 
	 * @param locationId Zone.location.id
	 * @return List of Zone @see com.aimir.model.system.Zone
	 */
	public List<Zone> getZonesByLocation(Integer locationId);
	
	/**
	 * method name : getZoneByName
	 * method Desc : BEMS 에서 사용 zone 명 중복체크를 위한 조회
	 * 
	 * @param name Zone.name
	 * @return List of Zone @see com.aimir.model.system.Zone
	 */
    public List<Zone> getZoneByName(String name);
    
    /**
	 * method name : updateOrderNo
	 * method Desc : BEMS 에서 사용 zone OrderNo 업데이트
	 * 
     * @param parentId Zone.parent.id
     * @param orderNo Zone.orderNo (New)
     * @param oriOrderNo Zone.orderNo (Old)
     */ 
	public void updateOrderNo(Integer parentId,Integer orderNo, Integer oriOrderNo);
}
