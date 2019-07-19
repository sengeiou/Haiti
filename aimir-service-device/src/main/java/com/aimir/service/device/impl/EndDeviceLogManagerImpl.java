package com.aimir.service.device.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.device.EndDeviceLogDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.ZoneDao;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.EndDeviceLog;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.Zone;
import com.aimir.service.device.EndDeviceLogManager;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@WebService(endpointInterface = "com.aimir.service.device.EndDeviceLogManager")
@Service(value = "endDeviceLogManager")
@Transactional(readOnly = false)
public class EndDeviceLogManagerImpl implements EndDeviceLogManager {

	private Logger logger = Logger.getLogger(EndDeviceManagerImpl.class);
	
	@Autowired
	SupplierDao supplierDao;
	
	@Autowired
	EndDeviceLogDao dao;
	
	@Autowired
	EndDeviceDao endDeviceDao;

	@Autowired
	LocationDao locationDao;
	
	@Autowired
	ZoneDao zoneDao;

	@Transactional(readOnly = true)
	public List<EndDeviceLog> getEndDeviceLogsExt(int locationId, int endDeviceId, int start, int limit, int supplierId) {
		List<EndDeviceLog> retList = new ArrayList<EndDeviceLog>();
		List<EndDeviceLog> f = null;
		Supplier supplier = supplierDao.get(supplierId);
		if (locationId != -1) {
			List<Integer> list = locationDao.getChildLocationId(locationId);
			list.add(locationId);
			f = dao.getEndDeviceLogByLocationId(list, start, limit);
		}
		else {
			f = dao.getEndDeviceLogs(start, limit);
		}
		
		if(f!=null){
			if (supplier != null) {
				String lang = supplier.getLang().getCode_2letter();
				String country = supplier.getCountry().getCode_2letter();
				
				for(EndDeviceLog endDeviceLog : f) {
					String date = TimeLocaleUtil.getLocaleDate(endDeviceLog.getWriteDatetime(), lang, country);
					endDeviceLog.setWriteDatetime(date);
				}
			} else {
				SimpleDateFormat inFormat = new SimpleDateFormat("yyyyMMddhhmmss");
				SimpleDateFormat outFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");	
				
				for(EndDeviceLog endDeviceLog : f) {
					try {
						endDeviceLog.setWriteDatetime
							(outFormat.format(inFormat.parse(endDeviceLog.getWriteDatetime())));
					} 
					catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			retList.addAll(f);
		}
		
		logger.info(retList);
		return retList;
	}
	
	@Transactional(readOnly = true)
	public long getTotalSize(int locationId, int endDeviceId) {
		long total = 0;
		if (locationId != -1) {
			List<Integer> list = locationDao.getChildLocationId(locationId);
			list.add(locationId);
			total = dao.getTotalSize(list);
		}
		else {
			total = dao.getTotalSize(null);
		}
		return total;
	}
	
	@Transactional(readOnly = true)
	public List<EndDeviceLog> getEndDeviceLogs(int locationId, int endDeviceId) {

		Set<Condition> conditionList = new HashSet<Condition>();
		List<EndDeviceLog> retList = new ArrayList<EndDeviceLog>();
		if (locationId != -1) {
			Location location = locationDao.get(locationId);
			conditionList.add(new Condition("location.id",
					new Object[] { locationId }, null, Restriction.EQ));
			conditionList.add(new Condition("location.id", new Object[] {},
					null, Restriction.ORDERBY));
			conditionList.add(new Condition("writeDatetime", new Object[] {},
					null, Restriction.ORDERBY));

			List<EndDeviceLog> rootList = dao.findByConditions(conditionList);

			for (EndDeviceLog root : rootList) {
				retList.add(root);
			}

			Set<Location> children = location.getChildren();
			Iterator<Location> iterator = children.iterator();
			while (iterator.hasNext()) {

				int locId = iterator.next().getId();
				Set<Condition> childConditionList = new HashSet<Condition>();
				childConditionList.add(new Condition("location.id",
						new Object[] { locId }, null, Restriction.EQ));
				childConditionList.add(new Condition("location.id",
						new Object[] {}, null, Restriction.ORDERBY));
				childConditionList.add(new Condition("writeDatetime",
						new Object[] {}, null, Restriction.ORDERBY));
				List<EndDeviceLog> childList = dao
						.findByConditions(childConditionList);
				for (EndDeviceLog child : childList) {					
					retList.add(child);
				}
			}
		} else {

			if (endDeviceId != -1) {
				conditionList.add(new Condition("enddevice.id",
						new Object[] { endDeviceId }, null, Restriction.EQ));
				conditionList.add(new Condition("enddevice.id",
						new Object[] {}, null, Restriction.ORDERBY));
				conditionList.add(new Condition("writeDatetime",
						new Object[] {}, null, Restriction.ORDERBY));
				retList = dao.findByConditions(conditionList);
			}
		}
	
		for(EndDeviceLog ret:retList) {
			Supplier supplier = ret.getEnddevice().getSupplier();			
			ret.setWriteDatetime(TimeLocaleUtil.getLocaleDate(
				ret.getWriteDatetime(),
				supplier.getLang().getCode_2letter(),
				supplier.getCountry().getCode_2letter()
			));
		}

		return retList;
	}
	
	@Transactional(readOnly = true)
	public List<EndDeviceLog> getEndDeviceLogsByZone(Map<String,Object> params) {
		
		String zoneId = StringUtil.nullToBlank(params.get("zoneId"));
		String supplierId = StringUtil.nullToZero(params.get("supplierId"));
		
		List<Integer> zones = new ArrayList<Integer>();
		
		//zoneId 가 입력되지 않았을경우 모든 zone 대상으로 조회
		if("".equals(zoneId)){
			List<Zone> zoneList = zoneDao.getParents();
			for(Zone zone:zoneList){
				zones.addAll(zoneDao.getLeafZoneId(zone.getId()));
			}
		}else{
			zones = zoneDao.getLeafZoneId(Integer.parseInt(zoneId));
		}
		
		// 최하위 EndDevice 분류에 해당하는 EndDevice 목록조회
		List<EndDevice> endDeviceList = null;

		if (zones != null && zones.size() > 0) {
		    endDeviceList = endDeviceDao.getEndDevicesByzones(zones);
		} else {
		    endDeviceList = new ArrayList<EndDevice>();
		}

		List<Integer> endDeviceIdList = new ArrayList<Integer>();
		for(EndDevice endDevice:endDeviceList){
			endDeviceIdList.add(endDevice.getId());
		}
		
		List<EndDeviceLog> endDeviceLogList = dao.getEndDeviceLogByEndDeviceId(endDeviceIdList);
		
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId)); 
		
		//변경일자를 표준 날짜 포맷으로 수정한다.
		if (supplier != null) {
			String lang = supplier.getLang().getCode_2letter();
			String country = supplier.getCountry().getCode_2letter();
			
			for(EndDeviceLog endDeviceLog : endDeviceLogList) {
				String date = TimeLocaleUtil.getLocaleDate(endDeviceLog.getWriteDatetime(), lang, country);
				endDeviceLog.setWriteDatetime(date);
			}
		} else {
			SimpleDateFormat inFormat = new SimpleDateFormat("yyyyMMddhhmmss");
			SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");	
			
			for(EndDeviceLog endDeviceLog : endDeviceLogList) {
				try {
					endDeviceLog.setWriteDatetime
						(outFormat.format(inFormat.parse(endDeviceLog.getWriteDatetime())));
				} 
				catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		
		return endDeviceLogList;
	}

	public void addEndDeviceLogs(EndDeviceLog endDeviceLog) {
		dao.add(endDeviceLog);
	}
	
	public List<EndDeviceLog> getEndDeviceLogsByZoneByParam(String zoneId) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("zoneId", zoneId);
	    return getEndDeviceLogsByZone(condition);
	}
}
