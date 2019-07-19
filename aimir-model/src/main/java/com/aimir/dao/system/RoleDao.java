/**
 * RoleDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Gadget;
import com.aimir.model.system.Role;

/**
 * RoleDao.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 12.   v1.0       김상연         Role Id 검색 (이름)
 *
 */
public interface RoleDao  extends GenericDao<Role, Integer> {
	
	/**
	 * method name : getRoleBySupplierId
	 * method Desc : 공급사 아이디에 해당하는 Role 리스트를 리턴한다.
	 * 
	 * @param supplierId Supplier.id
	 * @return List of Role  @see com.aimir.model.system.Role
	 */
	public List<Role> getRoleBySupplierId(Integer supplierId);
	
	/**
     * method name : getRoleBySupplierIdForCustomer
     * method Desc : 공급사 아이디에 해당하는 Customer Role 리스트를 리턴한다.
     * 
     * @param supplierId Supplier.id
     * @return List of Role  @see com.aimir.model.system.Role
     */
	public List<Role> getRoleBySupplierIdForCustomer(Integer supplierId);
	
	/**
	 * method name : getGadgetList
	 * method Desc : 가젯 전체 목록을 리턴한다.
	 * 
	 * @return List of Gadget @see com.aimir.model.system.Gadget
	 */
	public List<Gadget> getGadgetList();
	
	/**
	 * method name : updateGadget
	 * method Desc : Gadget의 roleid를 업데이트 한다.
	 * 
	 * @param roleId Gadget.role.id
	 * @param gadgetId Gadget.id
	 */
	public void updateGadget(Integer roleId, Integer gadgetId);
	
	/**
	 * method name : delGadget
	 * method Desc : Gadget id가 일치하는 Gadget을 삭제한다.
	 * 
	 * @param gadgetId Gadget.id
	 */
	public void delGadget(Integer gadgetId);
	
	/**
	 * method name : gadgetSearch
	 * method Desc : role id와 gadget 이름이 일치하는 가젯 목록을 리턴한다.
	 *  
	 * @param roleId Gadget.role.id
	 * @param gadgetName Gadget.name
	 * @return List of Gadget  @see com.aimir.model.system.Gadget
	 */
	public List<Gadget> gadgetSearch(Integer roleId, String gadgetName);
	
	/**
	 * method name : gadgetSearchByTag
	 * method Desc : role id와 tag에 속한 Gadget 목록을 리턴한다.
	 * 
	 * @param roleId Gadget.role.id
	 * @param tag Tag.tag
	 * @return List of Gadget  @see com.aimir.model.system.Gadget
	 */
	public List<Gadget> gadgetSearchByTag(Integer roleId, String tag);
	
	/**
	 * method name : getPermitedGadgets
	 * method Desc : role id 에 해당하는 가젯 목록을 리턴한다.
	 * 
	 * @param roleId
	 * @return List of Gadget  @see com.aimir.model.system.Gadget
	 */
	public List<Gadget> getPermitedGadgets(Integer roleId);
	
	/**
	 * method name : gadgetAllSearch
	 * method Desc : role id와 Gadget name이 like 조건에 부합되는  Gadget 목록을 리턴한다.
	 * 
	 * @param roleId Gadget.role.id
	 * @param name Gadget.name (like)
	 * @return List of Gadget  @see com.aimir.model.system.Gadget
	 */
	public List<Gadget> gadgetAllSearch(Integer roleId, String name);
	
	/**
	 * method name : gadgetAllSearchByTag
	 * method Desc : role id와 tag에 like 조건에 부합되는  Gadget 목록을 리턴한다.
	 * 
	 * @param roleId Gadget.role.id
	 * @param tag Tag.tag (like)
	 * @return List of Gadget  @see com.aimir.model.system.Gadget
	 */
	public List<Gadget> gadgetAllSearchByTag(Integer roleId, String tag);
	
	/**
	 * method name : nameOverlapCheck
	 * method Desc : gadget name이 중복되는게 있는지 카운트를 리턴한다.
	 * 
	 * @param name Gadget.name
	 * @return 중복되지 않으면 0을 리턴한다.
	 */
	public int nameOverlapCheck(String name);
	
	/**
	 * method name : search
	 * method Desc : gadget name이 like 조건에 해당하는 Gadget 목록을 리턴한다.
	 * 
	 * @param name Gadget.name (like)
	 * @return List of Gadget  @see com.aimir.model.system.Gadget
	 */
	public List<Gadget> search(String name);
	
	/**
	 * method name : getRoleByName
	 * method Desc : Role 정보를 Role name으로 검색하여 일치하는 Role을 리턴
	 *
	 * @param name Role.name
	 * @return Role  @see com.aimir.model.system.Role
	 */
	public Role getRoleByName(String name);
	
}
