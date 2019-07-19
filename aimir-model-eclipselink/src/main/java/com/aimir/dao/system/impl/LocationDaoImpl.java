package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Location;
import com.aimir.util.Condition;

@Repository(value = "locationDao")
public class LocationDaoImpl extends
    AbstractJpaDao<Location, Integer> implements LocationDao {

	public LocationDaoImpl() {
		super(Location.class);
	}

    @Override
    public List<Location> getParents() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Location> getLocations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Location> getChildren(Integer parentId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Location> getChildren(Integer parentId, Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Location> getParents(Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Location> getParentsBySupplierId(Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getParentsBykeyWord(Integer supplierId, String keyWord) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Location> getChildrenBySupplierId(Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getParentId(Integer locationId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Location> getLocations(Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getLeafLocationId(Integer locationId,
            Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getChildNodesInLocation(Integer locationId,
            Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getChildNodesInLocationCnt(Integer locationId,
            Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getChildLocationId(Integer locationId) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Location> getLocationByName(String name) {
        String sql = "select loc from Location loc where loc.name like :locName";
        Query query = em.createQuery(sql, Location.class);
        query.setParameter("locName", "%"+name+"%");
        return (List<Location>)query.getResultList();
    }

    @Override
    public void updateOrderNo(Integer supplierId, Integer parentId,
            Integer orderNo, Integer oriOrderNo) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Integer> getRoot() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public List<Object> getGroupMember(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getParentIdImmediate(Integer locationId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Location> getRootLocationList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Map<String, Object>> getLocationTreeForMeteringRate(
            Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean isRoot(Integer locationId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Location> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Location> getRootLocationListBySupplier(Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getNameByGeocode(String geocode, Integer supplierId) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<String> getLocationsName() {
        // TODO Auto-generated method stub
        return null;
    }
}