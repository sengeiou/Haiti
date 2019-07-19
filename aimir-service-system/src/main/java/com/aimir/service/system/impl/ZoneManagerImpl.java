package com.aimir.service.system.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.EndDeviceStatus;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.system.ZoneDao;
import com.aimir.model.system.Location;
import com.aimir.model.system.Zone;
import com.aimir.service.system.ZoneManager;
import com.aimir.util.StringUtil;

@WebService(endpointInterface = "com.aimir.service.system.ZoneManager")
@Service(value="zoneManager")
@Transactional
public class ZoneManagerImpl implements ZoneManager{

    @Autowired
    ZoneDao zonedao;
    
    @Autowired
    EndDeviceDao endDeviceDao;
    
    public List<Zone> getZonesByLocation(Location location){
    	return zonedao.getZonesByLocation(location.getId());
    }
    
    @SuppressWarnings("unchecked")
	public List<Object> getEndDeviceTypeAndStatusCountByZones(Map<String,Object> params){
    	String zoneId = StringUtil.nullToBlank(params.get("zoneId"));
    	    	
    	List<Integer> zoneIdList = null;
    	if(!"".equals(zoneId)){
    		zoneIdList = zonedao.getLeafZoneId(Integer.parseInt(zoneId));
    	}else{
    		zoneIdList = zonedao.getLeafZoneId(null);	
    	}
    	
    	List<Object> endDeviceCntList = endDeviceDao.getEndDeviceTypeAndStatusCountByZones(zoneIdList);
    	
    	List<Object> resultList = new ArrayList<Object>();
    	Map<String,Object> resultMap = null;
    	
    	Map<String,Object> tmp = null;
    	for(Object obj:endDeviceCntList){
    		tmp = new HashMap<String,Object>();
    		tmp = (Map<String,Object>)obj;
    		
    		if(resultList.size()<1){
    			resultMap = new HashMap<String,Object>();
    			resultMap.put("category", tmp.get("category"));
    			
    			if(EndDeviceStatus.Run.getCode().equals((String)tmp.get("status"))){
    				resultMap.put("run", tmp.get("cnt"));
    			}else if(EndDeviceStatus.Stop.getCode().equals((String)tmp.get("status"))){
    				resultMap.put("stop", tmp.get("cnt"));
    			}else if(EndDeviceStatus.Unknown.getCode().equals((String)tmp.get("status"))){
    				resultMap.put("unknown", tmp.get("cnt"));
    			}
    			
    			resultList.add(resultMap);
    		}else{
    			resultMap = (Map<String,Object>)resultList.get(resultList.size()-1);
    			
    			if(resultMap.get("category").equals(tmp.get("category"))){
        			if(EndDeviceStatus.Run.getCode().equals((String)tmp.get("status"))){
        				resultMap.put("run", tmp.get("cnt"));
        			}else if(EndDeviceStatus.Stop.getCode().equals((String)tmp.get("status"))){
        				resultMap.put("stop", tmp.get("cnt"));
        			}else if(EndDeviceStatus.Unknown.getCode().equals((String)tmp.get("status"))){
        				resultMap.put("unknown", tmp.get("cnt"));
        			}
    			}else{
    				resultMap = new HashMap<String,Object>();
        			resultMap.put("category", tmp.get("category"));
        			
        			if(EndDeviceStatus.Run.getCode().equals((String)tmp.get("status"))){
        				resultMap.put("run", tmp.get("cnt"));
        			}else if(EndDeviceStatus.Stop.getCode().equals((String)tmp.get("status"))){
        				resultMap.put("stop", tmp.get("cnt"));
        			}else if(EndDeviceStatus.Unknown.getCode().equals((String)tmp.get("status"))){
        				resultMap.put("unknown", tmp.get("cnt"));
        			}
        			
        			resultList.add(resultMap);
    			}
    		}
    	}
    	return resultList;
    }
    
    public void add(Zone zone) {
    	zonedao.add(zone);
    }
    
    public Zone addNewChildZone(Integer parentId) {
    	
    	Zone parent = zonedao.get(parentId);
    	
    	List<Zone> children = zonedao.getChildren(parentId);
    	
    	String newNamePre = parent.getName()+"_";
    	String newName = "";
    	int newOrder=1;
    	if(children!=null&&children.size()>0){
    		Zone lastChild = children.get(children.size()-1);
    		newOrder = lastChild.getOrderNo()+1;
    	}
    	
    	int idx = children.size()+1;
		while (true) {
			if (idx != 0) {
				newName = newNamePre + idx;
			}
			List<Zone> existZone = zonedao.getZoneByName(newName);
			if (existZone.size() > 0) {
				idx++;
			} else {
				break;
			}
		}
    	
    	Zone newZone = new Zone();
    	newZone.setName(newName);
    	newZone.setOrderNo(newOrder);
    	newZone.setLocation(parent.getLocation());
    	newZone.setParent(parent);
    	
    	zonedao.add(newZone);
    	
    	return zonedao.getChildren(parentId).get(children.size());
    }
    
    public Boolean updateZoneName(Integer zoneId,String newName) {
    	
    	Zone zone = zonedao.get(zoneId);
    	
    	List<Zone> newNameZoneList = zonedao.getZoneByName(newName);
 
    	boolean success = true;
    	if(newNameZoneList!=null&&newNameZoneList.size()>0){
    		if(newNameZoneList.size()>1){
    			success = false;
    		}else{
    			if(!zoneId.equals(newNameZoneList.get(0).getId())){
    				success = false;
    			}
    		}
    	} 
    	
    	if(success){
    		zone.setName(newName);
    		zonedao.update(zone);
    	}    	
    	
    	return success;
    }
    
    public Zone getZone(Integer id){
        Zone zone = zonedao.get(id);
        return zone;
    }
    
    public void update(Zone zone) {
    	zonedao.update(zone);
    }
    
    public void delete(Integer zoneId) {
        Zone zone = zonedao.get(zoneId);
        zonedao.delete(zone);
    }
    
    public List<Zone> getZoneByName(String name) {
		return zonedao.getZoneByName(name);
	}
    
	public void updateOrderNo(Integer parentId,Integer orderNo,Integer oriOrderNo) {
		zonedao.updateOrderNo(parentId, orderNo,oriOrderNo);
	}
	
    public List<Zone> getParentZone(){
    	List<Zone> zoneList = zonedao.getParents();
    	return zoneList;
    }
}
