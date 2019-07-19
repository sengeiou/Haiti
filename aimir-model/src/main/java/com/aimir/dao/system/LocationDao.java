package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Location;

public interface LocationDao extends GenericDao<Location, Integer> {

	/**
     * method name : getParents
     * method Desc : parent인  목록 전체를 찾아서 리턴한다.
	 * 
	 * @return List of Location @see com.aimir.model.system.Location
	 */
	public List<Location> getParents();
	
	/**
     * method name : getLocations
     * method Desc : 전체 지역 목록을 리턴한다.
     * 
	 * @return List of Location @see com.aimir.model.system.Location
	 */
	public List<Location> getLocations();
	
	
	/**
     * method name : getChildren
     * method Desc : parent 에 해당하는 children 목록 전체를 찾아서 리턴한다.
     * 
	 * @param parentId parent id Location.id 
	 * @return List of Location @see com.aimir.model.system.Location
	 */
	public List<Location> getChildren(Integer parentId);
	
	/**
     * method name : getChildren
     * method Desc : parent, 공급사 아이디에 충족하는 children 목록 전체를 찾아서 리턴한다.
	 * 
	 * @param parentId Location.id
	 * @param supplierId Supplier.id
	 * @return List of Location @see com.aimir.model.system.Location
	 */
	public List<Location> getChildren(Integer parentId,Integer supplierId);
	
	/**
     * method name : getParents
     * method Desc : 공급사 아이디에 충족하는 parent인  목록 전체를 찾아서 리턴한다.
	 * 
	 * @param supplierId Supplier.id
	 * @return List of Location @see com.aimir.model.system.Location
	 */
	public List<Location> getParents(Integer supplierId);
	
	/**
     * method name : getParentsBySupplierId
     * method Desc : 공급사 아이디에 충족하는 parent인  목록 전체를 찾아서 리턴한다.
	 * 
	 * @param supplierId Supplier.id
	 * @return List of Location @see com.aimir.model.system.Location
	 */
	public List<Location> getParentsBySupplierId(Integer supplierId);

	/**
     * method name : getParentsBySupplierId
     * method Desc : 공급사 아이디에 충족하는,  keyWord가 location name과 일치하는  parent인  목록 전체의 카운트를 구한다.
	 * 
	 * @param supplierId Supplier.id
	 * @param keyWord Location.name
	 * @return
	 */
	public int getParentsBykeyWord(Integer supplierId, String keyWord);
	
	/**
     * method name : getChildrenBySupplierId
     * method Desc :  공급사 아이디에 충족하는 children 목록 전체를 찾아서 리턴한다.
	 * 
	 * 
	 * @param supplierId Supplier.id
	 * @return List of Location @see com.aimir.model.system.Location
	 */
    public List<Location> getChildrenBySupplierId(Integer supplierId);
    
    
    /**
     * method name : getParentId
     * method Desc :  locationId에 충족하는 parent의 id를 리턴한다.
     * 
     * @param locationId Location.id
     * @return parent id
     */
	public List<Integer> getParentId(Integer locationId);
	
	/**
     * method name : getLocations
     * method Desc :  공급사 아이디에 충족하는 location 목록 전체를 찾아서 리턴한다.
     * 
	 * @param supplierId Supplier.id
	 * @return List of Location @see com.aimir.model.system.Location
	 */
	public List<Location> getLocations(Integer supplierId);
	
	/**
     * method name : getLeafLocationId
     * method Desc : 입력한 지역id의 하위 지역중에서 최하위지역id 목록을 조회한다.
	 * 
	 * @param locationId Location.id
	 * @param supplierId Supplier.id
	 * @return 최하위 지역의 Location.id
	 */
	public List<Integer> getLeafLocationId(Integer locationId,Integer supplierId);
	
	
	
	/**
	 * @desc 로케이션의 하위노드를 가지고 온다.
	 * @param locationId
	 * @param supplierId
	 * @return 현재노드의 children
	 */
	public List<Integer> getChildNodesInLocation(Integer locationId,Integer supplierId);
	
	
	/**
	 * @DESC 하위노드의  갯수를 fetch
	 * @param locationId
	 * @param supplierId
	 * @return 하위노드의 갯수
	 */
	public String getChildNodesInLocationCnt(Integer locationId,Integer supplierId);
	
	/**
     * method name : getChildLocationId
     * method Desc : 입력한 지역id의 모든 하위지역id 목록을 조회한다.
	 * 
	 * @param locationId Location.id
	 * @return 하위지역 location 아이디 목록
	 */
	public List<Integer> getChildLocationId(Integer locationId);
	
	/**
     * method name : getLocationByName
     * method Desc : BEMS 에서 사용 location 명 중복체크를 위한 조회 
	 * 
	 * @param name Location.name
	 * @return @see com.aimir.model.system.Location
	 */
	public List<Location> getLocationByName(String name);
	
	/**
     * method name : updateOrderNo
     * method Desc : BEMS 에서 사용 OrderNo 업데이트  
	 * 
	 * @param Integer supplierId, Integer parentId,
			Integer orderNo,Integer oriOrderNo
	 * @return void
	 */
	public void updateOrderNo(Integer supplierId, Integer parentId,
			Integer orderNo,Integer oriOrderNo);
	
	/**
     * method name : getRoot
     * method Desc : BEMS 에서 사용 root 지역 가져오기
	 * 
	 * @param Integer
	 *            supplierId, Integer parentId, Integer orderNo,Integer
	 *            oriOrderNo
	 * @return
	 */
	public List<Integer> getRoot();
	
	/**
     * method name : getGroupMember
     * method Desc : 그룹 관리 중 멤버 리스트 조회
	 * 
	 * @param condition Supplier.id, Location.name
	 * 
	 * {@code} String member = StringUtil.nullToBlank(condition.get("member")); //member is location's name
	 * Integer.parseInt((String)condition.get("supplierId")
	 * @return List of Object {Location.id, Location.name}
	 */
	@Deprecated
	public List<Object> getGroupMember(Map<String, Object> condition);

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : Group Management 가젯에서 Member 로 등록 가능한 Location 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap);

	/**
     * method name : getParentIdImmediate
     * method Desc : locationid로 parentid와 supplierid를 구한다.
	 * 
	 * @param locationId Location.id
	 * @return List of Object {parentId, supplierId}
	 */
	public List<Object> getParentIdImmediate(Integer locationId);

    /**
     * method name : getRootLocationList<b/>
     * method Desc : location tree 의 root location list 를 조회한다.
     *
     * @return List of {@link com.aimir.model.system.Location}
     */
    public List<Location> getRootLocationList();
    
    /**
     * method name : getRootLocationListBySupplier<b/>
     * method Desc : 공급사에 따른 location tree 의 root location list 를 조회한다.
     *
     * @return List of {@link com.aimir.model.system.Location}
     */
    public List<Location> getRootLocationListBySupplier(Integer supplierId);

    /**
     * method name : getLocationTreeForMeteringRate<b/>
     * method Desc :
     *
     * @param supplierId Supplier.id
     * @return List of Map {P_ID : Location.id - parent location id
     *                      P_NAME : Location.name - parent location name
     *                      C_ID : Location.id - child location id
     *                      C_NAME : Location.name - child location name
     *                     }
     */
    public List<Map<String, Object>> getLocationTreeForMeteringRate(Integer supplierId);
    
    /**
     * @MethodName isRoot
     * @Date 2014. 1. 23.
     * @param locationId
     * @return
     * @Modified 특정 locationId가  root인지 반환
     * @Description
     */
    public Boolean isRoot(Integer locationId);
    
    public String getNameByGeocode(String geocode, Integer supplierId);
    
    public List<String> getLocationsName();
}