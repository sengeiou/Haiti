/**
 * RoleManagerImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.service.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.DashboardDao;
import com.aimir.dao.system.DashboardGadgetDao;
import com.aimir.dao.system.GadgetDao;
import com.aimir.dao.system.GadgetRoleDao;
import com.aimir.dao.system.RoleDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Dashboard;
import com.aimir.model.system.DashboardGadget;
import com.aimir.model.system.Gadget;
import com.aimir.model.system.GadgetRole;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.RoleManager;

/**
 * RoleManagerImpl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 12.   v1.0       김상연         Role Id 검색 (이름)
 *
 */

@WebService(endpointInterface = "com.aimir.service.system.RoleManager")
@Service(value = "roleManager")
@Transactional
public class RoleManagerImpl implements RoleManager {
    @Autowired
    RoleDao dao;

    @Autowired
    GadgetDao gadgetDao;

    @Autowired
    GadgetRoleDao gadgetRoleDao;

    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    DashboardDao dashboardDao;
    
    @Autowired
    DashboardGadgetDao dashGadgetDao;

    public Role addRole(Role role) {
        return dao.add(role);
    }

    public void deleteRole(Role role) {
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("roleId", role.getId());
        conditionMap.put("supplierId", role.getSupplier().getId());

        //명령실행 role_code table 에서 해당 role_id 데이터 삭제
        role.setCommands(null);     
        dao.update(role);

        // 삭제할 GadgetRole 리스트를 조회한다.
        List<GadgetRole> gadgetRoleList = gadgetRoleDao.getDelGadgetRoleList(conditionMap);

        // GadgetRole 삭제
        for (GadgetRole obj : gadgetRoleList) {
            gadgetRoleDao.deleteById(obj.getId());
        }
        
        // 삭제할 DashBoard 리스트를 조회한다.
        List<Dashboard> dashboardList = dashboardDao.getDashboardsByRole(role.getId());
        
        // DashBoard 삭제
        for (Dashboard dash : dashboardList) {
            // 생성된 DashBoard Gadget 리스트를 조회한다.
            Set<DashboardGadget> dashGadgetList = dash.getDashboardGargets();
            for (DashboardGadget dashGadget : dashGadgetList){
                // DashBoard Gadget 삭제
                dashGadgetDao.deleteById(dashGadget.getId());
            }
            // 자식노드 삭제 후 DashBoard 삭제
            dashboardDao.deleteById(dash.getId());
        }

        // Role 삭제
        dao.deleteById(role.getId());
    }

    public List<Role> getRoles() {
        return dao.getAll();
    }

    public Role getRole(Integer roleId) {
        return dao.get(roleId);
    }

    public Role updateRole(Role role) {
        return dao.update(role);
    }

    /*
    public List<Role> getName(Integer supplierId) {
        return dao.getName(supplierId);
    }
    */

    public List<Role> getRoleBySupplierId(Integer supplierId) {
        return dao.getRoleBySupplierId(supplierId);
    }
    
    public List<Gadget> getGadgetList() {
        return dao.getGadgetList();
    }

    public void updateGadget(Integer roleId, Integer gadgetId) {
        dao.updateGadget(roleId, gadgetId);     
    }

    public void delGadget(Integer gadgetId) {
        dao.delGadget(gadgetId);        
    }

    public List<Gadget> gadgetSearch(Integer roleId,String gadgetName) {
        return dao.gadgetSearch(roleId,gadgetName);
    }

    public List<Gadget> gadgetSearchByTag(Integer roleId, String tag) {
        return dao.gadgetSearchByTag(roleId, tag);
    }

    public List<Gadget> getPermitedGadgets(Integer roleId) {
        return dao.getPermitedGadgets(roleId);
    }

    public List<Gadget> gadgetAllSearch(Integer roleId, String name) {
        return dao.gadgetAllSearch(roleId,name);
    }

    public List<Gadget> gadgetAllSearchByTag(Integer roleId, String tag) {
        return dao.gadgetAllSearchByTag(roleId, tag);
    }

    public int nameOverlapCheck(String name) {      
        return dao.nameOverlapCheck(name);
    }

    public List<Gadget> search(String name) {
        return dao.search(name);
    }

    /* (non-Javadoc)
     * @see com.aimir.service.system.RoleManager#getRoleByName(java.lang.String)
     */
    public Role getRoleByName(String name) {
        return dao.getRoleByName(name);
    }

    /**
     * method name : getGadgetListByRole<b/>
     * method Desc : UserManagement 맥스가젯에서 허용된 가젯 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Set<Gadget> getGadgetListByRole(Map<String, Object> conditionMap) {
        Set<Gadget> set = new HashSet<Gadget>();

        List<Map<String, Object>> list = gadgetRoleDao.getGadgetListByRole(conditionMap);
        
        if (list != null) 
        {
            
            for (Map<String, Object> map : list)
            {
                set.add((Gadget)map.get("gadget"));
            }
        }

        return set;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List getGadgetListByRole2(Map<String, Object> conditionMap) 
    {
        Set<Gadget> set = new HashSet<Gadget>();
        
        HashMap map=new HashMap();
        Gadget gadgetbean=new Gadget();

        List<Map<String, Object>> list = gadgetRoleDao.getGadgetListByRole(conditionMap);
        
        List<Gadget> list2=new ArrayList();
        
        for ( int i=0; i< list.size();i++)
        {
            map= (HashMap) list.get(i);
            
            gadgetbean =(Gadget) map.get("gadget");
        
            list2.add(gadgetbean);
        }
        
      

        return list2;
    }

    /**
     * method name : getRemainGadgetList<b/>
     * method Desc : UserManagement 맥스가젯에서 전체가젯 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Set<Gadget> getRemainGadgetList(Map<String, Object> conditionMap) {
        Set<Gadget> set = new HashSet<Gadget>();

        List<Gadget> list = gadgetDao.getRemainGadgetList(conditionMap);

        set.addAll(list);

        return set;
    }



    /**
     * method name : addGadgetRole<b/>
     * method Desc : UserManagement 맥스가젯에서 허용된 가젯 리스트에 선택한 가젯들을 등록한다.
     *
     * @param conditionMap
     */
    public void addGadgetRole(Map<String, Object> conditionMap) {
        Integer roleId = (Integer)conditionMap.get("roleId");
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String[] gadgetIds = (String[])conditionMap.get("gadgetIds");
        GadgetRole gadgetRole = new GadgetRole();

        Gadget gadget = null;
        Role role = dao.get(roleId);
        Supplier supplier = supplierDao.get(supplierId);

        for (String id : gadgetIds) {
            gadget = new Gadget();
            // 등록할 gadget 조회
            gadget = gadgetDao.get(new Integer(id));
            gadgetRole = new GadgetRole();

            gadgetRole.setRole(role);
            gadgetRole.setSupplier(supplier);
            gadgetRole.setGadget(gadget);

            gadgetRoleDao.add(gadgetRole);
        }
    }
    
    /**
     * method name : delGadgetRole<b/>
     * method Desc : UserManagement 맥스가젯에서 허용된 가젯 리스트에서 선택한 가젯들을 삭제한다.
     *
     * @param conditionMap
     */
    public void delGadgetRole(Map<String, Object> conditionMap) {
        String[] gadgetIds = (String[])conditionMap.get("gadgetIds");
        List<Integer> gadgetList = new ArrayList<Integer>();

        for (String id : gadgetIds) {
            gadgetList.add(new Integer(id));
        }

        conditionMap.put("gadgetList", gadgetList);
        
        List<GadgetRole> list = gadgetRoleDao.getDelGadgetRoleList(conditionMap);
        
        for (GadgetRole gadgetRole : list) {
            gadgetRoleDao.delete(gadgetRole);
        }
    }
    
    public List<Role> getRoleBySupplierIdForCustomer(Integer supplierId) {
        return dao.getRoleBySupplierIdForCustomer(supplierId);
    }
}