package com.aimir.service.system.impl;

import java.io.UnsupportedEncodingException;
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

import com.aimir.dao.system.LocationDao;
import com.aimir.model.system.Location;
import com.aimir.service.system.LocationManager;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

@WebService(endpointInterface = "com.aimir.service.system.LocationManager")
@Service(value="locationManager")
@Transactional
public class LocationManagerImpl implements LocationManager {

    @Autowired
    LocationDao dao;
    
    public void add(Location location) {
        dao.add(location);
    }

    public void delete(Integer locationId) {
        //dao.deleteById(locationId);
        Location location = dao.get(locationId);
        dao.delete(location);
    }

    public Location getLocation(Integer locationId) {
        Location location = dao.get(locationId);
        return location;
    }

    public List<Location> getParents() {
        return dao.getParents();
    }

    public void update(Location location) {
        dao.update(location);
    }

    public List<Location> getChildren(Integer parentId) {
        return dao.getChildren(parentId);
    }

    public List<Location> getParentsBySupplierId(Integer supplierId){
		return dao.getParentsBySupplierId(supplierId);
    }
    
    public int getParentsBykeyWord(Integer supplierId, String keyWord) throws UnsupportedEncodingException{
    	return dao.getParentsBykeyWord(supplierId,keyWord);
    }
    
    public List<Location> getChildrenBySupplierId(Integer supplierId){
		return dao.getChildrenBySupplierId(supplierId);
    }
    
    public List<Location> getLocationsBySupplierId(Integer supplierId){
		return dao.getLocations(supplierId);
    }
    public List<Location> getLocations(){
    	return dao.getAll();
    }
    
    public List<Location> getLocationByName(String name) {
		return dao.getLocationByName(name);
	}

	public void updateOrderNo(Integer supplierId, Integer parentId,
			Integer orderNo,Integer oriOrderNo) {
		dao.updateOrderNo(supplierId, parentId, orderNo,oriOrderNo);
		
	}

    /**
     * method name : getUserLocation<b/>
     * method Desc :
     *
     * @param locationId
     * @return
     */
    public List<Location> getUserLocation(Integer locationId) {
        Set<Condition> set = new HashSet<Condition>();
        set.add(new Condition("id", new Object[] { locationId }, null, Restriction.EQ));
        List<Location> list = dao.findByConditions(set);

        return list;
    }

    /**
     * method name : getUserLocationBySupplierId<b/>
     * method Desc :
     *
     * @param locationId
     * @param supplierId
     * @return
     */
    public List<Location> getUserLocationBySupplierId(Integer locationId, Integer supplierId) {
        Set<Condition> set = new HashSet<Condition>();
        set.add(new Condition("id", new Object[] { locationId }, null, Restriction.EQ));
        set.add(new Condition("supplier.id", new Object[] { supplierId }, null, Restriction.EQ));
        List<Location> list = dao.findByConditions(set);

        return list;
    }

    /**
     * method name : getAllLocationsForExcel<b/>
     * method Desc :
     *
     * @param supplierId
     * @return
     */
    public List<Object> getAllLocationsForExcel(Integer supplierId) {
        List<Object> result = new ArrayList<Object>();
        List<Location> list = dao.getParentsBySupplierId(supplierId);
        List<Object> childrenList = null;
        Integer maxCnt = 0;
        Integer childCnt = null;

        for (Location loc : list) {
            childrenList = getChildrenLocationList(loc, 1);
            childCnt = (Integer)childrenList.get(0);
            
            if (maxCnt < childCnt) {
                maxCnt = childCnt;
            }
            childrenList.remove(0);
            result.addAll(childrenList);
        }

        result.add(0, maxCnt);
        return result;
    }
    
    private List<Object> getChildrenLocationList(Location location, int level) {
        List<Object> list = new ArrayList<Object>();
        Map<String, Object> map = new HashMap<String, Object>();
        Set<Location> children = location.getChildren();
        List<Object> childrenList = null;
        Integer maxCnt = level;
        Integer childCnt = null;

        if (children != null && !children.isEmpty()) {
            for (Location loc : children) {
                childrenList = getChildrenLocationList(loc, level+1);
                childCnt = (Integer)childrenList.get(0);
                
                if (maxCnt < childCnt) {
                    maxCnt = childCnt;
                }
                childrenList.remove(0);
                list.addAll(childrenList);
            }
        }

        map.put("col" + level, location.getName());
        list.add(0, maxCnt);
        list.add(1, map);

        return list;
    }
    
    public List<Location> getRootLocationListBySupplier(Integer supplierId){
		return dao.getRootLocationListBySupplier(supplierId);
    }
    
    public List<String> getLocationsName(){
    	return dao.getLocationsName();
    }
}