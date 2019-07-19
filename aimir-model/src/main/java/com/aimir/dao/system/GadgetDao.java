package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Gadget;


public interface GadgetDao extends GenericDao<Gadget, Integer> {
		
	/**
	 * 
     * method name : searchGadgetList
     * method Desc : 가젯명과 롤로 가젯 목록을 리턴한다.
	 * 
	 * @param gadgetName Gadget.name
	 * @param roleId GadgetRole.id
	 * 
	 * @return @see com.aimir.model.system.Gadget
	 */
	public List<Gadget> searchGadgetList(String gadgetName, Integer roleId);

    /**
     * method name : getGadgetByGadgetCode
     * method Desc : gadgetCode에 해당하는 가젯 정보를 취득한다.
     *               
     * @param uniqueName 기초데이터 등록시 설정한 가젯명  Gadget.code
     * @return @see com.aimir.model.system.Gadget
     */
    public List<Gadget> getGadgetByGadgetCode(String gadgetCode);

    /**
     * method name : getRemainGadgetList<b/>
     * method Desc : UserManagement 맥스가젯에서 전체가젯 리스트를 조회한다.
     *
     * @param conditionMap
     * {@code}
     * Integer roleId = (Integer)conditionMap.get("roleId");
     * Integer supplierId = (Integer)conditionMap.get("supplierId");
     * String gadgetName = StringUtil.nullToBlank(conditionMap.get("gadgetName"));
     * String tagName = StringUtil.nullToBlank(conditionMap.get("tagName"));
     * @return @see com.aimir.model.system.Gadget
     */
    public List<Gadget> getRemainGadgetList(Map<String, Object> conditionMap);
    
    
    
    /**
     * @desc 전체 가젯 리스트를 가지고 온다.
     * @param conditionMap
     * @return
     */
    public List<Gadget> getAllGadgetList(Map<String, Object> conditionMap);
}