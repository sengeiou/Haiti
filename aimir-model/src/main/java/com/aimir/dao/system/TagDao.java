package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Gadget;
import com.aimir.model.system.Tag;

public interface TagDao extends GenericDao<Tag, Integer> {
	
	/**
     * method name : searchGadgetByTag
     * method Desc : 공급타입의 중복을 체크한다
     * 
	 * @param tag Tag.tag
	 * @param roleId GadgetRole.role.id
	 * @return List of Gadget @see com.aimir.model.system.Gadget
	 */
	public List<Gadget> searchGadgetByTag(String tag, int roleId);
	
	/**
     * method name : getTags
     * method Desc : 공급타입의 중복을 체크한다
     * 
	 * @param roleId GadgetRole.role.id
	 * @return List of Tag @see com.aimir.model.system.Tag
	 */
	public List<Tag> getTags(int roleId);
}