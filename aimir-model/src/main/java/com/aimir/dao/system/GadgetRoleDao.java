package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.GadgetRole;

public interface GadgetRoleDao extends GenericDao<GadgetRole, Integer> {
	public List<Map<String, Object>> getGadgetRolesList(Map<String, Object> params);

    /**
     * method name : getGadgetListByRole<b/>
     * method Desc : UserManagement 맥스가젯에서 허용된 가젯 리스트를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         Integer roleId = (Integer)conditionMap.get("roleId");
     *         Integer supplierId = (Integer)conditionMap.get("supplierId");
     *         String gadgetName = StringUtil.nullToBlank(conditionMap.get("gadgetName"));
     *         String tagName = StringUtil.nullToBlank(conditionMap.get("tagName"));
     * @return
     */
    public List<Map<String, Object>> getGadgetListByRole(Map<String, Object> conditionMap);

    /**
     * method name : getDelGadgetRoleList<b/>
     * method Desc : UserManagement 맥스가젯에서 삭제할 가젯 리스트를 조회한다.
     *
     * @param conditionMap
     * {@code}
     * 		String roleId = StringUtil.nullToBlank(params.get("roleId"));
     * 		String gadgetId = StringUtil.nullToBlank(params.get("gadgetId"));
     * 		String supplierId = StringUtil.nullToBlank(params.get("supplierId"));
     * 
     * @return @see com.aimir.model.system.GadgetRole
     */
    public List<GadgetRole> getDelGadgetRoleList(Map<String, Object> conditionMap);
}