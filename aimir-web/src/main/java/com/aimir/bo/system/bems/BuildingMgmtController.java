package com.aimir.bo.system.bems;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springmodules.validation.util.lang.ReflectionUtils;

import com.aimir.bo.common.CommonController;
import com.aimir.constants.CommonConstants.WeekDay;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.EndDeviceChartVO;
import com.aimir.model.device.EndDeviceLog;
import com.aimir.model.device.EndDeviceVO;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.BuildingMgmtEnergyContractVO;
import com.aimir.model.system.Code;
import com.aimir.model.system.ContractCapacity;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.LocationVO;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.SupplyCapacityLog;
import com.aimir.model.system.SupplyType;
import com.aimir.model.system.SupplyTypeLocation;
import com.aimir.model.system.TariffType;
import com.aimir.model.system.Zone;
import com.aimir.service.device.EndDeviceLogManager;
import com.aimir.service.device.EndDeviceManager;
import com.aimir.service.device.MeterManager;
import com.aimir.service.device.ModemManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ContractCapacityManager;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.CustomerManager;
import com.aimir.service.system.DeviceModelManager;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.SupplyCapacityLogManager;
import com.aimir.service.system.SupplyTypeLocationManager;
import com.aimir.service.system.SupplyTypeManager;
import com.aimir.service.system.TariffTypeManager;
import com.aimir.service.system.ZoneManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.ExcelUtil;
import com.aimir.util.FacilityHistoryDataMakeExcel;
import com.aimir.util.FacilitySituationDataMakeExcel;
import com.aimir.util.FacilityStatusDataMakeExcel;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

@Controller
public class BuildingMgmtController {

	Log logger = LogFactory.getLog(BuildingMgmtController.class);

	@Autowired
	SupplierManager supplierManager;

	@Autowired
	SupplyTypeManager supplyTypeManager;

	@Autowired
	LocationManager locationManager;

	@Autowired
	CodeManager codeManager;

	@Autowired
	SupplyTypeLocationManager locationServiceManager;

	@Autowired
	ContractManager contractManager;

	@Autowired
	ContractCapacityManager contractCapacityManager;

	@Autowired
	CustomerManager customerManager;

	@Autowired
	OperatorManager operatorManager;

	@Autowired
	DeviceModelManager deviceModelManager;

	@Autowired
	EndDeviceManager endDeviceManager;

	@Autowired
	EndDeviceLogManager endDeviceLogManager;

	@Autowired
	SupplyCapacityLogManager supplyCapacityLogManager;

	@Autowired
	ZoneManager zoneManager;

	@Autowired
	TariffTypeManager tariffTypeManager;
	
	@Autowired
	ModemManager modemManager;
	
	@Autowired
	MeterManager meterManager;

	private static final String FILE_PATH = "/tmp"; // 파일명이 전달되지 않았을 때 사용
	private static final Integer ONCE_PACKAGE_ROWS = 5000; // 한번에 쓰는 엑셀 행 수
	
	// Welcome page
	@RequestMapping(value = "/gadget/bems/buildingMgmtMax.do")
	public ModelAndView buildingMgmtMaxView() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("gadget/bems/buildingMgmtMax");
		return mav;
	}

	@RequestMapping(value = "/gadget/bems/facilityMgmtMini.do")
	public ModelAndView facilityMgmtMiniView() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("gadget/bems/facilityMgmtMini");
		return mav;
	}
	
	@RequestMapping(value = "/gadget/bems/facilityMgmtMax.do")
	public ModelAndView facilityMgmtMaxView() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("gadget/bems/facilityMgmtMax");
		return mav;
	}
	
	@RequestMapping(value = "/gadget/bems/facilityMgmtMini2.do")
	public ModelAndView facilityMgmtMiniView2() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("gadget/bems/facilityMgmtMini2");
		return mav;
	}
	
	@RequestMapping(value = "/gadget/bems/facilityMgmtMax2.do")
	public ModelAndView facilityMgmtMaxView2() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("gadget/bems/facilityMgmtMax2");
		return mav;
	}

	@RequestMapping(value = "/gadget/bems/buildingMgmtMini.do")
	public ModelAndView buildingMgmtMiniView() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("gadget/bems/buildingMgmtMini");
		return mav;
	}

	@RequestMapping(value = "/gadget/bems/zoneMgmtMiniGadget.do")
	public ModelAndView zoneMgmtMiniGadgetView(HttpServletResponse response ,HttpServletRequest request) {
		AimirUser user = CommonController.getAimirUser(response,request);   

		int supplierId = 0;
	
		if(user!=null && !user.isAnonymous()){                 
			supplierId = user.getRoleData().getSupplier().getId();
		}
		ModelAndView mav = new ModelAndView();
		mav.setViewName("gadget/bems/zoneMgmtMiniGadget");
		mav.addObject("supplierId", supplierId);
		return mav;
	}

	@RequestMapping(value = "/gadget/bems/zoneMgmtMaxGadget.do")
	public ModelAndView zoneMgmtMaxGadgetView() {
		ModelAndView mav = new ModelAndView();
	
		mav.setViewName("gadget/bems/zoneMgmtMaxGadget");
		List<Zone> zoneList = zoneManager.getParentZone();

		if (zoneList != null && zoneList.size() > 0) {
	        Zone rootZone = zoneList.get(0);
	        mav.addObject("zoneId", rootZone.getId());
		} else {
		    mav.addObject("zoneId", -1);
		}
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/addLocation.do")
	public ModelAndView addLocation(@RequestParam("supplierId") int supplierId,
			@RequestParam("parentId") int parentId,
			@RequestParam("newNode") String newNode,
			@RequestParam("orderNo") int orderNo) {

		Location location = new Location();
		Location parent = null;

		if (parentId != -1) {
			parent = locationManager.getLocation(parentId);
		}

		int idx = 0;

		String queryName = newNode;
		while (true) {
			if (idx != 0) {
				queryName = newNode + idx;
			}
			List<Location> existLocation = locationManager
					.getLocationByName(queryName);
			if (existLocation.size() > 0) {
				idx++;
			} 
			else {
				break;
			}
		}

		location.setName(queryName);
		location.setParent(parent);
		location.setOrderNo(orderNo);
		location.setSupplier(supplierManager.getSupplier(supplierId));

		locationManager.add(location);
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("location", locationManager.getLocation(location.getId()));

		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/updateLocation.do")
	public ModelAndView updateLocation(
			@RequestParam("locationId") int locationId,
			@RequestParam("newNode") String newNode) {

		ModelAndView mav = new ModelAndView("jsonView");
		Location location = locationManager.getLocation(locationId);
		List<Location> existLocation = locationManager.getLocationByName(newNode);

		if (existLocation.size() > 0 || newNode.length() <= 0) {
			if (existLocation.size() == 1) {
				if (location.getId() == existLocation.get(0).getId()) {
					mav.addObject("updateResult", "success");
				} 
				else {
					mav.addObject("updateResult", "fail");
				}
			} 
			else {
				mav.addObject("updateResult", "fail");
			}
		} 
		else {
			location.setName(newNode);
			locationManager.update(location);
			mav.addObject("updateResult", "success");
		}

		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/getLocations.do")
	public ModelAndView getLocations(@RequestParam("supplierId") int supplierId) {
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("locationlist", locationManager.getParentsBySupplierId(supplierId));
		return mav;
	}
	
	/**
	 * zone별로 설비현황, 설비운영상태, 설비 운영상태 변경 이력, 분류별 설비 대수 차트 조회
	 * 
	 * @param String zoneId
	 * @param String supplierId
	 * 
	 * @return jsonView
	 */
	@RequestMapping(value = "/gadget/system/bems/getZoneInfo.do")
	public ModelAndView getZoneInfo(@RequestParam("zoneId") String zoneId, 
			@RequestParam("supplierId") String supplierId ){
		ModelAndView mav = new ModelAndView("jsonView");
		
		try{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("supplierId", supplierId);
		params.put("zoneId", zoneId);
		
		List<Object> statusChart = zoneManager.getEndDeviceTypeAndStatusCountByZones(params);
		List<Object> catalogue = endDeviceManager.getEndDeviceByZone(params);
		List<Object> log = getEndDeviceLogList(endDeviceLogManager.getEndDeviceLogsByZone(params));
		
		mav.addObject("statusChart", statusChart);
		mav.addObject("catalogue", catalogue);
		mav.addObject("log", log);
		}catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mav;
	}
	
	/**
	 * 분류별 설비 대수 차트 조회
	 * 
	 * @param List<EndDeviceLog>
	 * 
	 * @return List<Object>
	 */
	private List<Object> getEndDeviceLogList(List<EndDeviceLog> logs) {
		List<Object> resultList = new ArrayList<Object>();
		
		for (EndDeviceLog log : logs) {
			Map<String, Object> result = new HashMap<String, Object>();
			
			result.put("categoryCode", log.getCategoryCode());
			result.put("friendlyName", log.getFriendlyName());
			result.put("statusCode", log.getStatusCode());
			result.put("writeDatetime", log.getWriteDatetime());
			
			resultList.add(result);
		}
		return resultList;
	}

	public LocationVO getLocationEndDeviceVO(Location location) {

		List<LocationVO> childVO = new ArrayList<LocationVO>();
		LocationVO locationVO = new LocationVO(location);
		locationVO.setEndDevice(endDeviceManager
				.getEndDevicesByLocationId(location.getId()));
		Set<Location> child = location.getChildren();
		if (child.size() > 0) {
			Iterator<Location> childIterator = child.iterator();
			while (childIterator.hasNext()) {
				Location childLocation = childIterator.next();
				LocationVO childLocationVO = getLocationEndDeviceVO(childLocation);
				childVO.add(childLocationVO);
			}
		}
		if (childVO.size() > 0) {
			locationVO.setLocationVO(childVO);
		}

		return locationVO;

	}

	@RequestMapping(value = "/gadget/system/bems/getLocationWithEndDevice.do")
	public ModelAndView getLocationWithEndDevice(
			@RequestParam("supplierId") int supplierId) {
		List<Location> location = locationManager.getParentsBySupplierId(supplierId);

		ModelAndView mav = new ModelAndView("jsonView");
		List<LocationVO> locationVOList = new ArrayList<LocationVO>();
		if(location.size() > 0) {
			LocationVO locationVO = getLocationEndDeviceVO(location.get(0));
			locationVOList.add(locationVO);
		}
		mav.addObject("locationlist", locationVOList);

		return mav;
	}

	public LocationVO getLocationCodeVO(Location location) {

		LocationVO locationVO = new LocationVO(location);
		List<LocationVO> childVO = new ArrayList<LocationVO>();
		List<Code> codeList = this.codeManager.getChildCodes("1.9.1");

		for (Code code : codeList) {
			childVO.add(convertCodeToLocationVO(code));
		}
		locationVO.setLocationVO(childVO);
		return locationVO;

	}

	public LocationVO getLocationZoneVO(Location location) {
		LocationVO locationVO = new LocationVO(location);
		List<LocationVO> childVO = new ArrayList<LocationVO>();
		List<Zone> zoneList = this.zoneManager.getZonesByLocation(location);

		for (Zone zone : zoneList) {
			childVO.add(convertZoneToLocationVO(zone));
		}
		locationVO.setLocationVO(childVO);
		return locationVO;

	}

	public LocationVO convertCodeToLocationVO(Code code) {

		List<LocationVO> childVO = new ArrayList<LocationVO>();
		LocationVO locationVO = new LocationVO(code);
		Set<Code> child = code.getChildren();
		if (child.size() > 0) {
			Iterator<Code> childIterator = child.iterator();
			while (childIterator.hasNext()) {
				Code childLocation = childIterator.next();
				LocationVO childLocationVO = convertCodeToLocationVO(childLocation);
				childVO.add(childLocationVO);
			}
		}
		if (childVO.size() > 0) {
			locationVO.setLocationVO(childVO);
		}
		return locationVO;
	}

	public LocationVO convertZoneToLocationVO(Zone zone) {
		List<LocationVO> childVO = new ArrayList<LocationVO>();
		LocationVO locationVO = new LocationVO(zone);
		Set<Zone> child = zone.getChildren();
		if (child.size() > 0) {
			Iterator<Zone> childIterator = child.iterator();
			while (childIterator.hasNext()) {
				Zone childZone = childIterator.next();
				LocationVO childLocationVO = convertZoneToLocationVO(childZone);
				childVO.add(childLocationVO);
			}
		}
		if (childVO.size() > 0) {
			locationVO.setLocationVO(childVO);
		}
		return locationVO;
	}

	@RequestMapping(value = "/gadget/system/bems/getLocationWithCode.do")
	public ModelAndView getLocationWithCode(
			@RequestParam("supplierId") int supplierId) {
		List<Location> location = locationManager.getParentsBySupplierId(supplierId);

		ModelAndView mav = new ModelAndView("jsonView");
		List<LocationVO> locationVOList = new ArrayList<LocationVO>();
		if (location.size() > 0) {
    		LocationVO locationVO = getLocationCodeVO(location.get(0));
    		locationVOList.add(locationVO);
		}

		mav.addObject("locationlist", locationVOList);

		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/getLocationWithZone.do")
	public ModelAndView getLocationWithZone(
			@RequestParam("supplierId") int supplierId) {
		List<Location> location = locationManager.getParentsBySupplierId(supplierId);

		ModelAndView mav = new ModelAndView("jsonView");
		List<LocationVO> locationVOList = new ArrayList<LocationVO>();
		LocationVO locationVO = getLocationZoneVO(location.get(0));
		locationVOList.add(locationVO);

		mav.addObject("locationlist", locationVOList);

		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/getSupplierLocation.do")
	public ModelAndView getSupplierLocation(
			@RequestParam("supplierId") int supplierId) {
		List<Location> locationArray = new ArrayList<Location>();
		ModelAndView mav = new ModelAndView("jsonView");

		List<Location> root = locationManager.getParentsBySupplierId(supplierId);

		if (root.size() > 0) {
			locationArray.add(root.get(0));
			Set<Location> child = root.get(0).getChildren();
			Iterator<Location> childIterator = child.iterator();

			while (childIterator.hasNext()) {
				locationArray.add(childIterator.next());
			}

		}
		mav.addObject("supplierLocation", locationArray);
		logger.debug("supplierLocation:" + locationArray);
		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/deleteLocation.do")
	public ModelAndView deleteLocation(
			@RequestParam("locationId") int locationId) {
		ModelAndView mav = new ModelAndView("jsonView");
		Location parent = locationManager.getLocation(locationId).getParent();

		locationManager.delete(locationId);
		mav.addObject("parent", parent);
		mav.addObject("deleteResult", "success");
		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/updateOrderNo.do")
	public ModelAndView updateOrderNo(
			@RequestParam("locationId") int locationId,
			@RequestParam("orderNo") int orderNo,
			@RequestParam("oriOrderNo") int oriOrderNo) {

		ModelAndView mav = new ModelAndView("jsonView");

		Location location = locationManager.getLocation(locationId);

		if (orderNo > oriOrderNo) {
			orderNo--;
		}
		locationManager.updateOrderNo(location.getSupplier().getId(), location
				.getParent().getId(), orderNo, oriOrderNo);

		location.setOrderNo(orderNo);
		locationManager.update(location);
		mav.addObject("updateResult", "success");

		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/getEnergyContract.do")
	public ModelAndView getEnergyContract(@RequestParam("page") int page,
			@RequestParam("count") int count) {

		List<BuildingMgmtEnergyContractVO> energyContract = new ArrayList<BuildingMgmtEnergyContractVO>();

		List<ContractCapacity> contractCapacityList = contractCapacityManager
				.getContractCapacityList(page, count);

		for (ContractCapacity contractCapacity : contractCapacityList) {
			energyContract.add(new BuildingMgmtEnergyContractVO(
					contractCapacity));
		}

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("energyContractlist", energyContract);

		return mav;
	}

	/**
	 * XXX: 오류 메시지 수정이 필요해 보인다.
	 * 
	 * @param supplierId
	 * @param contractNum
	 * @param codeId
	 * @param contractCapacity
	 * @param contractDay
	 * @param supplyLocation
	 * @return
	 */
	@RequestMapping(value = "/gadget/system/bems/addEnergyContract.do")
	public ModelAndView addEnergyContract(
			@RequestParam("supplierId") int supplierId,
			@RequestParam("contractNum") String contractNum,
			@RequestParam("codeId") int codeId,
			@RequestParam("contractCapacity") int contractCapacity,
			@RequestParam("contractDay") String contractDay,
			@RequestParam("supplyLocation") String supplyLocation) {
		ModelAndView mav = new ModelAndView("jsonView");
		Supplier supplier = supplierManager.getSupplier(supplierId);
		SupplyType supplyType = null;
		ContractCapacity contractCapa = new ContractCapacity();
		TariffType tf = tariffTypeManager.getTariffType(codeId);
		String[] locations = supplyLocation.split(",");
		boolean exist = false;
		for (int i = 0; i < locations.length; i++) {
			exist = this.contractCapacityManager.contractEnergyExistCheck(tf
					.getServiceTypeCode().getId(), Integer
					.parseInt(locations[i]));
			if (exist) {
				break;
			}
		}
		if (exist) {
			mav.addObject("contractCapacityId", -1);
			mav.addObject("addResult", "위치가 중복되었습니다.");
			return mav;
		}
		Set<SupplyType> supplyTypeList = supplier.getSupplyTypes();
		for (SupplyType supplyTypes : supplyTypeList) {
			if (supplyTypes.getTypeCode().getId() == tf.getServiceTypeCode()
					.getId()) {
				supplyType = supplyTypes;
			}
		}

		contractCapa.setCapacity(Integer.valueOf(contractCapacity)
				.doubleValue());
		contractCapa.setContractNumber(contractNum + "");
		contractCapa.setContractDate(contractDay);
		contractCapa.setContractTypeCode(tf);
		Set<SupplyTypeLocation> supplyTypeLocations = new HashSet<SupplyTypeLocation>(
				0);
		logger.debug("Integer.valueOf(contractCapacity).doubleValue():"
				+ Integer.valueOf(contractCapacity).doubleValue());
		logger.debug("contractNum:" + contractNum);
		logger.debug("tf:" + tf.getId());
		logger.debug("contractCapa:" + contractCapa);
		contractCapacityManager.add(contractCapa);
		Location loc = null;
		String logLoc = "";
		for (int i = 0; i < locations.length; i++) {
			loc = locationManager.getLocation(Integer.parseInt(locations[i]));
			SupplyTypeLocation stl = new SupplyTypeLocation();
			stl.setSupplyType(supplyType);
			stl.setLocation(loc);
			stl.setContractCapacity(contractCapa);
			supplyTypeLocations.add(stl);
			locationServiceManager.add(stl);
			if (i != locations.length - 1) {
				logLoc = logLoc + loc.getName() + ",";
			} else {
				logLoc = logLoc + loc.getName();
			}

		}

		contractCapa.setSupplyTypeLocations(supplyTypeLocations);

		SupplyCapacityLog supplyCapacityLog = new SupplyCapacityLog();
		supplyCapacityLog.setContractNumber(contractNum);
		supplyCapacityLog.setSupplier(supplier);
		supplyCapacityLog.setContractCapacity(contractCapa.getCapacity()
				.toString());
		supplyCapacityLog.setSupplyType(tf.getName());
		supplyCapacityLog.setLocation(loc);
		supplyCapacityLog.setSupplyTypeLocation(logLoc);
		supplyCapacityLog.setWriteDatetime(DateTimeUtil
				.getCurrentDateTimeByFormat(""));
		addSupplyCapacityLog(supplyCapacityLog);

		mav.addObject("contractCapacityId", contractCapa.getId());
		mav.addObject("addResult", "success");

		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/deleteEnergyContract.do")
	public ModelAndView deleteEnergyContract(
			@RequestParam("contractCapacityId") int contractCapacityId) {

		ContractCapacity contractCapa = contractCapacityManager
				.getContractCapacity(contractCapacityId);

		deleteSupplyLocation(contractCapa.getSupplyTypeLocations());
		contractCapacityManager.delete(contractCapacityId);

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("deleteResult", "success");

		return mav;
	}

	private SupplyType deleteSupplyLocation(
			Set<SupplyTypeLocation> supplyTypeLocations) {
		SupplyType supplyType = null;
		Iterator<SupplyTypeLocation> sIterator = supplyTypeLocations.iterator();

		while (sIterator.hasNext()) {
			SupplyTypeLocation supplyTypeLocation = sIterator.next();
			supplyType = supplyTypeLocation.getSupplyType();
			locationServiceManager.delete(supplyTypeLocation.getId());
		}
		return supplyType;
	}

	private void addLocation(String supplyLocation, SupplyType supplyType,
			ContractCapacity contractCapa,
			Set<SupplyTypeLocation> supplyTypeLocations) {
		String[] locations = supplyLocation.split(",");
		Location loc = null;
		for (int i = 0; i < locations.length; i++) {
			loc = locationManager.getLocation(Integer.parseInt(locations[i]));
			SupplyTypeLocation stl = new SupplyTypeLocation();
			stl.setSupplyType(supplyType);
			stl.setLocation(loc);
			stl.setContractCapacity(contractCapa);
			supplyTypeLocations.add(stl);
			locationServiceManager.add(stl);
		}
		contractCapa.setSupplyTypeLocations(supplyTypeLocations);
		contractCapacityManager.update(contractCapa);
	}

	private SupplyType updateSupplyLocation(
			Set<SupplyTypeLocation> supplyTypeLocations, String supplyLocation,
			String[] locations) {
		SupplyType supplyType = null;
		Iterator<SupplyTypeLocation> sIterator = supplyTypeLocations.iterator();

		int i = 0;
		while (sIterator.hasNext()) {
			SupplyTypeLocation supplyTypeLocation = sIterator.next();
			if (supplyLocation != null && supplyLocation.length() > 0) {
				supplyTypeLocation.setLocation(locationManager
						.getLocation(Integer.parseInt(locations[i])));
				supplyType = supplyTypeLocation.getSupplyType();
				locationServiceManager.update(supplyTypeLocation);
			}
			i++;
		}
		return supplyType;
	}

	private SupplyType updateSupplyLocation(
			Set<SupplyTypeLocation> supplyTypeLocations, SupplyType supplyType) {
		Iterator<SupplyTypeLocation> sIterator = supplyTypeLocations.iterator();
		while (sIterator.hasNext()) {
			SupplyTypeLocation supplyTypeLocation = sIterator.next();
			supplyTypeLocation.setSupplyType(supplyType);
			locationServiceManager.update(supplyTypeLocation);
		}
		return supplyType;
	}

	private SupplyType getSupplyType(Set<SupplyTypeLocation> supplyTypeLocations) {

		Iterator<SupplyTypeLocation> sIterator = supplyTypeLocations.iterator();

		SupplyType supplyType = null;

		while (sIterator.hasNext()) {
			SupplyTypeLocation supplyTypeLocation = sIterator.next();
			supplyType = supplyTypeLocation.getSupplyType();
			break;
		}
		return supplyType;
	}

	@RequestMapping(value="/gadget/system/bems/editEnergyContract.do",method=RequestMethod.POST)
	public ModelAndView editEnergyContract(
			@RequestParam("contractCapacityId") int contractCapacityId,
			@RequestParam("supplierId") int supplierId,
			@RequestParam("contractNum") String contractNum,
			@RequestParam("codeId") int codeId,
			@RequestParam("contractCapacity") int contractCapacity,
			@RequestParam("contractDay") String contractDay,
			@RequestParam("supplyLocation") String supplyLocation) {

		ContractCapacity contractCapa = contractCapacityManager
				.getContractCapacity(contractCapacityId);

		SupplyCapacityLog supplyCapacityLog = new SupplyCapacityLog();
		Set<SupplyTypeLocation> supplyTypeLocations = contractCapa
				.getSupplyTypeLocations();

		TariffType tariffType = contractCapa.getContractTypeCode();
		SupplyType supplyType = null;

		String[] locations = null;
		if (supplyLocation != null && supplyLocation.length() > 0) {
			locations = supplyLocation.split(",");
			if (supplyTypeLocations.size() != locations.length) {
				supplyType = deleteSupplyLocation(supplyTypeLocations);
				addLocation(supplyLocation, supplyType, contractCapa,
						new HashSet<SupplyTypeLocation>(0));
			} else {
				supplyType = updateSupplyLocation(supplyTypeLocations,
						supplyLocation, locations);
			}
		}

		if (supplierId != -1) {
			supplyType = getSupplyType(contractCapa.getSupplyTypeLocations());
			if (supplierId != supplyType.getSupplier().getId()) {
				Supplier supplier = supplierManager.getSupplier(supplierId);
				Code code = codeManager.getCode(codeId);
				Set<SupplyType> supplyTypeList = supplier.getSupplyTypes();
				for (SupplyType supplyTypes : supplyTypeList) {
					if (supplyTypes.getTypeCode().getId() == code.getParent()
							.getId()) {
						updateSupplyLocation(contractCapa
								.getSupplyTypeLocations(), supplyTypes);
						break;
					}
				}
			}
		}

		if (supplyType == null) {
			supplyType = getSupplyType(contractCapa.getSupplyTypeLocations());
		}

		supplyCapacityLog.setSupplier(supplyType.getSupplier());

		if (codeId != -1) {
			if (codeId != tariffType.getId()) {
			    TariffType newTariffType = tariffTypeManager.getTariffType(codeId);
				contractCapa.setContractTypeCode(newTariffType);
				contractCapacityManager.update(contractCapa);
			}
		}

		if (contractCapacity != -1) {
			if (contractCapacity != contractCapa.getCapacity()) {
				contractCapa.setCapacity(Integer.valueOf(contractCapacity)
						.doubleValue());
				contractCapacityManager.update(contractCapa);
			}
		}

		if (!contractNum.equals("-1")) {
			if (!contractNum.equals(contractCapa.getContractNumber())) {
				contractCapa.setContractNumber(contractNum + "");
				contractCapacityManager.update(contractCapa);
			}
		}

		if (contractDay != null && contractDay.length() > 0) {
			contractCapa.setContractDate(contractDay);
			contractCapacityManager.update(contractCapa);
		}

		String logLoc = "";
		Set<SupplyTypeLocation> supplyTypeLocationList = contractCapa
				.getSupplyTypeLocations();
		int i = 0;
		for (SupplyTypeLocation supplyTypeLocation : supplyTypeLocationList) {
			if (i != supplyTypeLocationList.size() - 1)
				logLoc = logLoc + supplyTypeLocation.getLocation().getName()
						+ ",";
			else
				logLoc = logLoc + supplyTypeLocation.getLocation().getName();
			i++;
		}
		supplyCapacityLog.setContractNumber(contractNum);
		supplyCapacityLog.setContractCapacity(contractCapa.getCapacity()
				.toString());
		supplyCapacityLog.setSupplyType(contractCapa.getContractTypeCode()
				.getName());

		supplyCapacityLog.setSupplyTypeLocation(logLoc);
		supplyCapacityLog.setWriteDatetime(DateTimeUtil
				.getCurrentDateTimeByFormat(""));
		addSupplyCapacityLog(supplyCapacityLog);

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("editResult", "success");

		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/getLocationChild.do")
	public ModelAndView getLocationChild(
			@RequestParam("locationId") int locationId) {

		List<Location> locationList = new ArrayList<Location>();
		Location location = locationManager.getLocation(locationId);
		locationList.add(location);

		Set<Location> child = location.getChildren();
		Iterator<Location> iterator = child.iterator();
		while (iterator.hasNext()) {
			locationList.add(iterator.next());
		}
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("locationChild", locationList);

		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/getDeviceModel.do")
	public ModelAndView getDeviceModel(
			@RequestParam("supplierId") int supplierId) {
		List<DeviceModel> deviceModelList = new ArrayList<DeviceModel>();

		List<Code> children = codeManager.getChildCodes("1.10");
		for (Code childCode : children) {

			List<DeviceModel> deviceModel = deviceModelManager
					.getDeviceModelByTypeId(supplierId, childCode.getId());
			for (DeviceModel device : deviceModel) {
				deviceModelList.add(device);
			}
		}
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("locationChild", deviceModelList);

		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/deleteEndDevice.do")
	public ModelAndView deleteEndDevice(
			@RequestParam("endDeviceId") int endDeviceId) {
		endDeviceManager.delete(endDeviceId);
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("deleteResult", "success");
		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/getSupplyCapacityLog.do")
	public ModelAndView getSupplyCapacityLog(@RequestParam("page") int page,
			@RequestParam("count") int count) {

		ModelAndView mav = new ModelAndView("jsonView");
		List<SupplyCapacityLog> log = supplyCapacityLogManager
				.getSupplyCapacityLogs();
		System.out.println("supplyCapacityLogManager");
		for(SupplyCapacityLog scLog:log){
			Supplier supplier =scLog.getSupplier();
			scLog.setWriteDatetime(TimeLocaleUtil.getLocaleDate(scLog.getWriteDatetime(),supplier.getLang().getCode_2letter(),supplier.getCountry().getCode_2letter()));
		}
		
		mav.addObject("supplyCapacityLog", log);

		return mav;
	}

	private void addSupplyCapacityLog(SupplyCapacityLog supplyCapacityLog) {

		supplyCapacityLogManager.add(supplyCapacityLog);

	}

	@RequestMapping(value = "/gadget/system/bems/editEndDevice.do")
	public ModelAndView editEndDevice(
			@RequestParam("endDeviceId") int endDeviceId,
			@RequestParam("locationId") int locationId,
			@RequestParam("codeId") int codeId,
			@RequestParam("manufacturerer") String manufacturerer,
			@RequestParam("model") String model,
			@RequestParam("friendlyName") String friendlyName,
			@RequestParam("installDate") String installDate,
			@RequestParam("manufactureDate") String manufactureDate,
			@RequestParam("powerConsumption") String numpowerConsumption,
			@RequestParam("modemType") int modemType,
			@RequestParam("modemSerial") String modemSerial) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		EndDevice endDevice = endDeviceManager.getEndDevice(endDeviceId);
		Location oriLocation = endDevice.getLocation();
		
		// ▼ 사용자가 입력하지 않은 조정하는 코드같다. START. ▼
		if (locationId != -1 && locationId != oriLocation.getId()) {
			endDevice.setLocation(locationManager.getLocation(locationId));
		}
		Code oriCode = endDevice.getCategoryCode();
		if (codeId != -1 && codeId != oriCode.getId()) {
			endDevice.setCategoryCode(codeManager.getCode(codeId));
		}
		if (manufacturerer != null && manufacturerer.length() > 0) {
			endDevice.setManufacturer(manufacturerer);
		}
		if (model != null && model.length() > 0) {
			endDevice.setModelName(model);
		}
		if (friendlyName != null && friendlyName.length() > 0) {
			endDevice.setFriendlyName(friendlyName);
		}
		if (installDate != null && installDate.length() > 0) {
			endDevice.setInstallDate(installDate);
		}
		if (manufactureDate != null && manufactureDate.length() > 0) {
			endDevice.setManufactureDate(manufactureDate);
		}
		Double powerConsumption = Double.parseDouble(numpowerConsumption);
		if (powerConsumption != -1
				&& powerConsumption != endDevice.getPowerConsumption()) {
			endDevice.setPowerConsumption(powerConsumption);
		}
		if (modemType != -1
				&& modemType != endDevice.getControllerCode().getId()) {
			endDevice.setControllerCode(codeManager.getCode(modemType));
		}
		if (modemSerial != null && modemSerial.length() > 0) {
			endDevice.setSerialNumber(modemSerial);
		}
		// ▲ 사용자가 입력하지 않은 조정하는 코드같다. END. ▲
		
		endDeviceManager.updateEndDevice(endDevice);
		
		// XXX: 만일 모뎀 시리얼이 같은 장비가 추가되면 기존 미터정보가 덮어써진다.
		updateMeterEndDeviceId(endDevice, modemSerial);
		
		mav.addObject("editResult", "success");
		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/getEndDeviceChart.do")
	public ModelAndView getEndDeviceChart(
			@RequestParam("locationId") int locationId,
			@RequestParam("endDeviceId") int endDeviceId) {
		List<Code> facilityCodeList = getFacility();

		HashMap<Integer, EndDeviceVO> facilityMap = new HashMap<Integer, EndDeviceVO>();

		for (Code facilityCode : facilityCodeList) {
			EndDeviceVO endDeviceVO = new EndDeviceVO();
			endDeviceVO.setFacilityType(facilityCode.getDescr());
			facilityMap.put(facilityCode.getId(), endDeviceVO);
		}

		List<EndDevice> endDeviceList = new ArrayList<EndDevice>();
		Location location = locationManager.getLocation(locationId);
		if (locationId != -1) {
			endDeviceList = endDeviceManager
					.getEndDevicesByLocationId(locationId);
			Set<Location> child = location.getChildren();
			Iterator<Location> iterator = child.iterator();
			while (iterator.hasNext()) {
				List<EndDevice> childEndDeviceList = endDeviceManager
						.getEndDevicesByLocationId(iterator.next().getId());
				for (EndDevice childEndDevice : childEndDeviceList) {
					endDeviceList.add(childEndDevice);
				}
			}
		} else {
			if (endDeviceId != -1) {
				EndDevice ed = endDeviceManager.getEndDevice(endDeviceId);
				endDeviceList.add(ed);
			} else {
				endDeviceList = endDeviceManager.getEndDevicesList();
			}
		}

		for (EndDevice endDevice : endDeviceList) {
			if (endDevice.getCategoryCode() != null) {
				EndDeviceVO endDeviceVO = facilityMap.get(endDevice
						.getCategoryCode().getId());
				if ("1.9.2.1".equalsIgnoreCase(endDevice.getStatusCode()
						.getCode())) {
					endDeviceVO.addRunning(1);
				} else if ("1.9.2.2".equalsIgnoreCase(endDevice.getStatusCode()
						.getCode())) {
					endDeviceVO.addStop(1);
				} else if ("1.9.2.3".equalsIgnoreCase(endDevice.getStatusCode()
						.getCode())) {
					endDeviceVO.addUnknown(1);
				}
			}
		}
		List<EndDeviceVO> endDeviceVOList = new ArrayList<EndDeviceVO>();
		for (Code facilityCode : facilityCodeList) {
			endDeviceVOList.add(facilityMap.get(facilityCode.getId()));
		}
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("endDeviceList", endDeviceVOList);
		return mav;
	}

	private List<Code> getFacility() {
		List<Code> children = codeManager.getChildCodes("1.9.1");
		List<Code> retCodeList = new ArrayList<Code>();
		for (Code code : children) {
			Set<Code> child = code.getChildren();
			for (Code childCode : child) {
				retCodeList.add(childCode);
			}
		}
		return retCodeList;
	}

	@RequestMapping(value = "/gadget/system/bems/getFacility.do")
	public ModelAndView getFacilityList() {
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("facility", getFacility());
		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/updateSupplier.do")
	public ModelAndView updateSupplier(
			@RequestParam("supplierId") int supplierId,
			@RequestParam("address") String address,
			@RequestParam("telno") String telno,
			@RequestParam("area") String area,
			@RequestParam("descr") String descr) {

		String sarea;
		Double darea;
		if((area.substring(0,area.length() -1)).indexOf(",")!= -1){
			sarea = (area.substring(0,area.length() -1)).replace(",", "");
		}else {
			sarea = area.substring(0,area.length() -1);
		}
		
		darea = Double.parseDouble(sarea);
		logger.debug("SAREA TYPE : " + sarea.getClass().getName());
		logger.debug("DAREA : "+ darea+ " , DAREA : "+ darea.getClass().getName());
		
		Supplier supplier = supplierManager.getSupplier(supplierId);
		supplier.setAddress(address);
		supplier.setTelno(telno);

		supplier.setArea(darea);
		supplier.setDescr(descr);
		supplierManager.update(supplier);
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", "sucess");
		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/getEnergySupplier.do")
	public ModelAndView getEnergySupplier() {

		List<SupplyType> supplyTypeList = supplyTypeManager.getSupplyTypeList();
		List<Supplier> supplierList = new ArrayList<Supplier>();

		HashMap<Integer, Supplier> supplierHash = new HashMap<Integer, Supplier>();
		for (SupplyType supplyType : supplyTypeList) {
			if (!supplierHash.containsKey(supplyType.getSupplier().getId())) {
				supplierHash.put(supplyType.getSupplier().getId(), supplyType
						.getSupplier());
				supplierList.add(supplyType.getSupplier());
			}
		}

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("energySupplierlist", supplierList);
		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/getContractType.do")
	public ModelAndView getContractType(
			@RequestParam("supplierId") int supplierId) {

		List<SupplyType> supplyTypeList = supplyTypeManager
				.getSupplyTypeBySupplierId(supplierId);
		List<Code> contractTypelist = new ArrayList<Code>();

		for (SupplyType supplyType : supplyTypeList) {
			List<Code> codeList = codeManager.getChildCodes(supplyType
					.getTypeCode().getCode());
			for (Code code : codeList) {
				contractTypelist.add(code);
			}
		}

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("contractTypelist", contractTypelist);

		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/editFacilityStatus.do")
	public ModelAndView editFacilityStatus(
			@RequestParam("endDeviceId") int endDeviceId,
			@RequestParam("codeId") int codeId) {

		EndDevice endDevice = endDeviceManager.getEndDevice(endDeviceId);

		String preStatusCode = endDevice.getStatusCode().getDescr();

		endDevice.setStatusCode(codeManager.getCode(codeId));
		endDeviceManager.updateEndDevice(endDevice);

		EndDeviceLog endDeviceLog = new EndDeviceLog();

		endDeviceLog.setLocation(endDevice.getLocation());
		endDeviceLog.setEnddevice(endDevice);
		endDeviceLog.setCategoryCode(endDevice.getCategoryCode().getDescr());
		endDeviceLog.setFriendlyName(endDevice.getFriendlyName());
		endDeviceLog.setLocationName(endDevice.getLocation().getName());
		endDeviceLog.setPreStatusCode(preStatusCode);
		endDeviceLog.setStatusCode(endDevice.getStatusCode().getDescr());
		endDeviceLog.setWriteDatetime(DateTimeUtil
				.getCurrentDateTimeByFormat(""));
		endDeviceLogManager.addEndDeviceLogs(endDeviceLog);
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("editResult", "success");

		return mav;
	}

	private Map<String, String> lastWeekDate(String lang, String today) {
		Map<String, String> map = new HashMap<String, String>();
		int year = Integer.parseInt(today.substring(0, 4));
		int month = Integer.parseInt(today.substring(4, 6));
		int day = Integer.parseInt(today.substring(6, 8));
		String weekday = CalendarUtil.getWeekDay(lang, year, month, day);
		String startDate = "";
		String endDate = "";
		if (WeekDay.Monday.getKorName().equals(weekday) || WeekDay.Monday.getEngName().equals(weekday)) {
			startDate = CalendarUtil.getDateWithoutFormat(today, Calendar.DATE,
					-8);
		} else if (WeekDay.Tuesday.getKorName().equals(weekday) || WeekDay.Tuesday.getEngName().equals(weekday)) {
			startDate = CalendarUtil.getDateWithoutFormat(today, Calendar.DATE,
					-9);
		} else if (WeekDay.Wednesday.getKorName().equals(weekday) || WeekDay.Wednesday.getEngName().equals(weekday)) {
			startDate = CalendarUtil.getDateWithoutFormat(today, Calendar.DATE,
					-10);
		} else if (WeekDay.Thursday.getKorName().equals(weekday) || WeekDay.Thursday.getEngName().equals(weekday)) {
			startDate = CalendarUtil.getDateWithoutFormat(today, Calendar.DATE,
					-11);
		} else if (WeekDay.Friday.getKorName().equals(weekday) || WeekDay.Friday.getEngName().equals(weekday)) {
			startDate = CalendarUtil.getDateWithoutFormat(today, Calendar.DATE,
					-12);
		} else if (WeekDay.Saturday.getKorName().equals(weekday) || WeekDay.Saturday.getEngName().equals(weekday)) {
			startDate = CalendarUtil.getDateWithoutFormat(today, Calendar.DATE,
					-13);
		} else if (WeekDay.Sunday.getKorName().equals(weekday) || WeekDay.Sunday.getEngName().equals(weekday)) {
			startDate = CalendarUtil.getDateWithoutFormat(today, Calendar.DATE,
					-7);
		}

		endDate = CalendarUtil
				.getDateWithoutFormat(startDate, Calendar.DATE, 6);
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		
		logger.debug("XXXX startDate["+startDate+"] endDate["+endDate+"]");
		return map;
	}

	@RequestMapping(value = "/gadget/system/bems/endDeviceCompareChart.do")
	public ModelAndView endDeviceCompareChart(
			@RequestParam("endDeviceId") int endDeviceId) {
		
		ModelAndView mav = new ModelAndView("jsonView");
     
		EndDevice endDevice = endDeviceManager.getEndDevice(endDeviceId);
		Integer modemId = -1;
		if(endDevice != null) {
			modemId = (endDevice.getModem() == null) ? -1 : endDevice.getModem().getId();
		}
		else {
			mav.addObject("result", "fail");
			mav.addObject("cause", "endDevice [" + endDeviceId + "] is not exists");
			return mav;
		}
		
		String today = CalendarUtil.getCurrentDate();
		String yesterday = CalendarUtil.getDateWithoutFormat(today, Calendar.DATE, -1);
		String lastMonth = CalendarUtil.getDateWithoutFormat(today, Calendar.MONTH, -1);
		String lastYear = CalendarUtil.getDateWithoutFormat(today, Calendar.YEAR, -1);
		int inWeek = CalendarUtil.getWeekOfMonth(today);
		String year = today.substring(0, 4);
		int month = Integer.parseInt(today.substring(4, 6));
		int intLastYear = Integer.parseInt(lastYear.substring(0, 4));

		Map<String, String> week = 
			CalendarUtil.getDateWeekOfMonth(year, month	+ "", inWeek + "");

		String lo = endDevice.getSupplier().getLang().getCode_2letter();
		Map<String, String> lastWeek = lastWeekDate(lo, today);
		
		HashMap<String, Object> dayCondition = new HashMap<String, Object>();

		dayCondition.put("yesterday", yesterday);
		dayCondition.put("today", today);
		dayCondition.put("weekStartDate", week.get("startDate"));
		dayCondition.put("weekEndDate", week.get("endDate"));
		dayCondition.put("lastWeekStartDate", lastWeek.get("startDate"));
		dayCondition.put("lastWeekEndDate", lastWeek.get("endDate"));
		dayCondition.put("endDeviceId", endDeviceId );
		dayCondition.put("modemId", modemId );
		
		HashMap<String, Object> monthCondition = new HashMap<String, Object>();

		monthCondition.put("lastMonth", lastMonth.substring(0, 6));
		monthCondition.put("month", today.substring(0, 6));
		monthCondition.put("lastYearStartMonth", intLastYear + "01");
		monthCondition.put("lastYearEndMonth", intLastYear + "12");
		monthCondition.put("yearStartMonth", year + "01");
		monthCondition.put("yearEndMonth", today.substring(0, 6));
		monthCondition.put("modemId", modemId );
		monthCondition.put("endDeviceId", endDeviceId );
		
		HashMap<String, Object> dayResult = 
			endDeviceManager.getCompareFacilityDayData(dayCondition);
		HashMap<String, Object> monthResult = 
			endDeviceManager.getCompareFacilityMonthData(monthCondition);

		List<EndDeviceChartVO> dailyChart = getChartData(dayResult, "daily");
		List<EndDeviceChartVO> weeklyChart = getChartData(dayResult, "weekly");
		List<EndDeviceChartVO> monthlyChart = getChartData(monthResult, "monthly");
		List<EndDeviceChartVO> yearlyChart = getChartData(monthResult, "yearly");

		mav.addObject("dailyChart", dailyChart);
		mav.addObject("weeklyChart", weeklyChart);
		mav.addObject("monthlyChart", monthlyChart);
		mav.addObject("yearlyChart", yearlyChart);

		return mav;
	}

	/**
	 * XXX: 국제화가 되어있지 않은것 같다.
	 * 
	 * @param data
	 * @param kind
	 * @return
	 */
	private List<EndDeviceChartVO> getChartData(Map<String, Object> data,
			String kind) {
		List<EndDeviceChartVO> chart = new ArrayList<EndDeviceChartVO>();
		EndDeviceChartVO electric = new EndDeviceChartVO("전기");
		EndDeviceChartVO gas = new EndDeviceChartVO("가스");
		EndDeviceChartVO water = new EndDeviceChartVO("수도");
		EndDeviceChartVO heat = new EndDeviceChartVO("열량");

		if ("daily".equals(kind)) {
			electric.setCurrent(chkData(data.get("EM_TODAY")));
			gas.setCurrent(chkData(data.get("GM_TODAY")));
			water.setCurrent(chkData(data.get("WM_TODAY")));
			heat.setCurrent(chkData(data.get("HM_TODAY")));
			electric.setOld(chkData(data.get("EM_YESTERDAY")));
			gas.setOld(chkData(data.get("GM_YESTERDAY")));
			water.setOld(chkData(data.get("WM_YESTERDAY")));
			heat.setOld(chkData(data.get("HM_YESTERDAY")));
		} else if ("weekly".equals(kind)) {

			electric.setCurrent(chkData(data.get("EM_WEEK")));
			gas.setCurrent(chkData(data.get("GM_WEEK")));
			water.setCurrent(chkData(data.get("WM_WEEK")));
			heat.setCurrent(chkData(data.get("HM_WEEK")));

			electric.setOld(chkData(data.get("EM_LASTWEEK")));
			gas.setOld(chkData(data.get("GM_LASTWEEK")));
			water.setOld(chkData(data.get("WM_LASTWEEK")));
			heat.setOld(chkData(data.get("HM_LASTWEEK")));
		} else if ("monthly".equals(kind)) {
			electric.setCurrent(chkData(data.get("EM_MONTH")));
			gas.setCurrent(chkData(data.get("GM_MONTH")));
			water.setCurrent(chkData(data.get("WM_MONTH")));
			heat.setCurrent(chkData(data.get("HM_MONTH")));
			electric.setOld(chkData(data.get("EM_LASTMONTH")));
			gas.setOld(chkData(data.get("GM_LASTMONTH")));
			water.setOld(chkData(data.get("WM_LASTMONTH")));
			heat.setOld(chkData(data.get("HM_LASTMONTH")));
		} else if ("yearly".equals(kind)) {
			electric.setCurrent(chkData(data.get("EM_YEAR")));
			gas.setCurrent(chkData(data.get("GM_YEAR")));
			water.setCurrent(chkData(data.get("WM_YEAR")));
			heat.setCurrent(chkData(data.get("HM_YEAR")));
			electric.setOld(chkData(data.get("EM_LASTYEAR")));
			gas.setOld(chkData(data.get("GM_LASTYEAR")));
			water.setOld(chkData(data.get("WM_LASTYEAR")));
			heat.setOld(chkData(data.get("HM_LASTYEAR")));
		}

		chart.add(electric);
		chart.add(gas);
		chart.add(water);
		chart.add(heat);

		return chart;

	}

	private String chkData(Object data) {

		if (data != null) {
			return new BigDecimal(data == null ? 0:((Number)data).doubleValue())+"";
			
		} else {
			return "";
		}
	}
	
	@RequestMapping(value = "/gadget/system/bems/addZone.do")
	public ModelAndView addZone(@RequestParam("parentId") int parentId) {
		Zone zone = zoneManager.addNewChildZone(parentId);
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("zone", zone);

		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/updateZoneName.do")
	public ModelAndView updateZoneName(@RequestParam("zoneId") int zoneId,
			@RequestParam("name") String name) {

		Zone zone = zoneManager.getZone(zoneId);

		Boolean success = zoneManager.updateZoneName(zoneId, name);

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("success", success);
		mav.addObject("zone", zone);
		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/updateZoneOrderNo.do")
	public ModelAndView updateZoneOrderNo(@RequestParam("zoneId") int zoneId,
			@RequestParam("orderNo") int orderNo,
			@RequestParam("oriOrderNo") int oriOrderNo) {

		ModelAndView mav = new ModelAndView("jsonView");

		Zone zone = zoneManager.getZone(zoneId);

		if (orderNo > oriOrderNo) {
			orderNo--;
		}
		zoneManager.updateOrderNo(zone == null || zone.getParent() == null ? -1
				: zone.getParent().getId(), orderNo, oriOrderNo);

		zone.setOrderNo(orderNo);
		zoneManager.update(zone);
		mav.addObject("updateResult", "success");

		return mav;
	}

	@RequestMapping(value = "/gadget/system/bems/deleteZone.do")
	public ModelAndView deleteZone(@RequestParam("zoneId") int zoneId) {
		ModelAndView mav = new ModelAndView("jsonView");
		Zone parent = zoneManager.getZone(zoneId).getParent();

		zoneManager.delete(zoneId);
		mav.addObject("parent", parent);
		mav.addObject("deleteResult", "success");
		return mav;
	}

	/**
	 * 메시지 프로퍼티 로드
	 * @return
	 */
	@RequestMapping(value = "/js/framework/Config/bems/fmtMessage.do")
	public ModelAndView fmtMessage() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/js/framework/Config/bems/fmtMessage");
		return mav;
	}
	
	/**
	 * 새로운 EndDevice 데이터 정보를 요청받아 ENDDEVICE 테이블에 넣는다.
	 * 모뎀 시리얼이 있다면, 모뎀을 검색하여 미터셋을 얻고, 미터에 EndDevice 정보를 업데이트한다.
	 * 없다면 무시한다.
	 * 
	 * @param supplierId 공급자 아이디
	 * @param locationId 위치(층) 아이디
	 * @param codeId 코드 아이디
	 * @param manufacturerer 제조사
	 * @param model 모델 코드
	 * @param friendlyName 표시 이름
	 * @param installDate 설치일
	 * @param manufactureDate 제조일
	 * @param powerConsumption 전력량
	 * @param modemType 모뎀 타입
	 * @param modemSerial 모뎀 시리얼
	 * @return JSON View 및 모델 맵 (ModelAndView)
	 * 
	 * @author javarouka(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
	 */
	@RequestMapping(value="/gadget/system/bems/addEndDevice.do", method=RequestMethod.POST)
	public ModelAndView addEndDevice(
			@RequestParam("supplierId") int supplierId,
			@RequestParam("locationId") int locationId,
			@RequestParam("codeId") int codeId,
			@RequestParam("manufacturerer") String manufacturerer,
			@RequestParam("model") String model,
			@RequestParam("friendlyName") String friendlyName,
			@RequestParam("installDate") String installDate,
			@RequestParam("manufactureDate") String manufactureDate,
			@RequestParam("powerConsumption") int powerConsumption,
			@RequestParam(value="modemType", required=false) int modemType,
			@RequestParam(value="modemSerial", required=false) String modemSerial) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		Location location = locationManager.getLocation(locationId);
		Code code = this.codeManager.getCode(codeId);
		
		EndDevice endDevice = new EndDevice();
		
		Supplier supplier = supplierManager.getSupplier(supplierId);
		if(supplier == null) {
			mav.addObject("addResult", "fail");
			mav.addObject("error", "supplier ["+supplierId+"] is not exists");
			return mav;
		}
		endDevice.setSupplier(supplier);
		
		endDevice.setLocation(location);
		endDevice.setCategoryCode(code);
		endDevice.setManufacturer(manufacturerer);
		endDevice.setModelName(model);
		endDevice.setFriendlyName(friendlyName);
		endDevice.setInstallDate(installDate);
		endDevice.setManufactureDate(manufactureDate);
		endDevice.setPowerConsumption(Double.parseDouble(powerConsumption + ""));
		endDevice.setSerialNumber(modemSerial + "");
		endDevice.setStatusCode(codeManager.getCode(
			codeManager.getCodeIdByCode("1.9.2.1"))
		);
		endDevice.setUuid(UUID.randomUUID().toString());
		endDevice.setControllerCode(codeManager.getCode(modemType));

		EndDevice retEndDevice = endDeviceManager.addEndDevice(endDevice);
		
		// XXX: 만일 모뎀 시리얼이 같은 장비가 추가되면 기존 미터정보가 덮어써진다.
		updateMeterEndDeviceId(endDevice, modemSerial);

		mav.addObject("addResult", "success");
		mav.addObject("endDevice", retEndDevice);

		return mav;
	}
	
	/**
	 * 설비 관리 그리드 부분 데이터
	 * 
	 * XXX: 원래라면 jsonView를 적용하기 위해 toJSONString을 구현해야 하지만,
	 * Model 레이어 변경을 피하기 위해 Reflection 을 사용하여 처리했다.
	 * 추후 JSONString 방식으로 바꾸는게 나을 것 같다.
	 * 
	 * @param locationId 위치(층) 아이디
	 * @param endDeviceId EndDevice 아이디
	 * @param start 테이블에서 시작 행 위치
	 * @param limit 가져올 행 수
	 * @return JSON View 및 모델 맵 (ModelAndView)
	 * 
	 * @author javarouka(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
	 */
	@RequestMapping(value = "/gadget/system/bems/getEndDevicesVOByLocationIdMetering.do")
	public ModelAndView getEndDevicesVOByLocationId(
		@RequestParam("locationId") int locationId,
		@RequestParam("endDeviceId") int endDeviceId,
		@RequestParam("start") int start,
		@RequestParam("limit") int limit) {

		ModelAndView mav = new ModelAndView("jsonView");
		
		List<EndDeviceVO> endDeviceList = 
			endDeviceManager.getEndDevicesVOByLocationIdExt(
				locationId, endDeviceId, start, limit, true
			);		
		
		// Reflection 사용
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();	
		Map<String,Object> m = null;
		for (EndDeviceVO e : endDeviceList) {
			Method methods [] = ReflectionUtils.getAllMethods(EndDeviceVO.class);
			m = new HashMap<String, Object>();
			for (Method method : methods) {
				if(method.getName().startsWith("get")) {
					try {
						m.put(
							method.getName().substring(3, 4).toLowerCase()
							+ method.getName().substring(4), 
							method.invoke(e, (Object[])null)
						);
					}
					catch (Exception ignore) { }
				}
			}
    		list.add(m);
		}
		
		mav.addObject("endDeviceList", list);
		
		// 총 행수를 얻는다. 페이징에 필요하다.
		mav.addObject("listCount", endDeviceManager.getTotalSize(locationId));
		
		return mav;
	}
	
	/**
	 * EndDevice 메타정보 얻기
	 * View에 필요한 콤보 박스나 수정등에 필요한 코드값 등의 모델을 만든다.
	 * 
	 * @param supplierId 공급자 아이디
	 * @return JSON View 및 모델 맵 (ModelAndView)
	 * 
	 * @author javarouka(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
	 */
	@RequestMapping(value = "/gadget/system/bems/getMetaData.do")
	public ModelAndView getMetaData(@RequestParam("supplierId") int supplierId) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> meta = endDeviceManager.getMetaData(supplierId);
		for (String key : meta.keySet()) {
			mav.addObject(key, meta.get(key));
		}
		
		return mav;
	}
	
	/**
	 * EndDeviceLog 조회 컨트롤러 메서드
	 * 
	 * @param locationId 위치(층) 아이디
	 * @param endDeviceId EndDevice 아이디
	 * @param start 테이블에서 시작 행 위치
	 * @param limit 가져올 행 수
	 * @return JSON View 및 모델 맵 (ModelAndView)
	 * 
	 * @author javarouka(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
	 */
	@RequestMapping(value = "/gadget/system/bems/getEndDeviceLog.do")
	public ModelAndView getEndDeviceLog(
		@RequestParam("locationId") int locationId,
		@RequestParam("endDeviceId") int endDeviceId,
		@RequestParam("start") int start,
		@RequestParam("limit") int limit,
		@RequestParam("supplierId") int supplierId) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		List<EndDeviceLog> list = 
			endDeviceLogManager.getEndDeviceLogsExt(
				locationId, endDeviceId, start, limit, supplierId
			);
		mav.addObject("endDeviceLogList", list);
		
		// 총 행수를 얻어야 페이징을 할 수 있다.
		long total = endDeviceLogManager.getTotalSize(locationId, endDeviceId);
		
		mav.addObject("listCount", total);		
		return mav;
	}
	
	/**
	 * 모뎀에 관련된 미터의 엔드디바이스 아이디를 갱신한다.
	 * 만일 모뎀 시리얼이 없다면 모뎀 시리얼을 MdsId로 간주하여 미터를 업데이트한다
	 * 
	 * XXX: 만일 모뎀 시리얼이 같은 장비가 추가되면 기존 미터정보가 덮어써진다.
	 * 
	 * @param endDevice 해당 EndDevice
	 * @param modemSerial 문자형식의 모뎀 시리얼 번호
	 * 
	 * @author javarouka(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
	 */
	private void updateMeterEndDeviceId(EndDevice endDevice, String modemSerial) {
		if(modemSerial != null) {
			Modem modem = modemManager.getModem(modemSerial);
			if(modem == null || modem.getId() == null) {
				updateMeterEndDeviceIdByMdsId(endDevice, modemSerial);
				return;
			}
			endDevice.setModem(modem);
			Set<Meter> meters = modem.getMeter();
			if(meters == null || meters.isEmpty()) {
				return;
			}
			for(Meter m : meters) {
				if(m.getId() == null) {
					continue;
				}				
				m.setEndDevice(endDevice);
				meterManager.updateMeter(m);
			}
		}
	}
	
	/**
	 * 미터의 EndDeviceId 를 갱신한다.
	 * 기존 EndDeviceId 는 덮어쓴다.
	 * 
	 * @param endDevice
	 * @param mdsId
	 */
	private void updateMeterEndDeviceIdByMdsId(EndDevice endDevice, String mdsId) {
		if(mdsId != null) {
			Meter meter = meterManager.getMeter(mdsId);
			if(meter == null || meter.getId() == null) {
				return;
			}
			meter.setEndDevice(endDevice);
			meterManager.updateMeter(meter);
		}
	}
	
	/** 
	 * 엑셀 다운로드 팝업 오픈
	 * @return jsp(javascript)
	 */
	@RequestMapping(value = "/gadget/bems/facilityDownloadPopup.do")
	public ModelAndView downloadPopup() {
		ModelAndView mav = new ModelAndView("/gadget/bems/facilityDownloadPopup");
		return mav;
	}
	
	
	@RequestMapping(value="/gadget/system/bems/excelFacilitySituation.do", method={ RequestMethod.POST })
	public ModelAndView excelFacilitySituation(
		@RequestParam int locationId,
		@RequestParam int endDeviceId,
		@RequestParam int start,
		@RequestParam Integer limit,
		@RequestParam Integer supplierId,
		@RequestParam String location,
		@RequestParam String type,
		@RequestParam String friendlyName, 
		@RequestParam String status,
		@RequestParam String dayEM,
		@RequestParam String dayWM, 
		@RequestParam String dayGM,
		@RequestParam String dayHM,
		@RequestParam(required=false) Integer onceCount,	
		@RequestParam(required=false) String filePath) {

		ModelAndView mav = new ModelAndView("/gadget/bems/facilityDownloadPopup");
		Map<String, String> msgMap = new HashMap<String, String>();
		List<String> fileNameList = new ArrayList<String>();
		
		msgMap.put("location", location);
        msgMap.put("type", type);
        msgMap.put("friendlyName", friendlyName);
        msgMap.put("status", status);
        msgMap.put("dayEM", dayEM);
        msgMap.put("dayWM", dayWM);
        msgMap.put("dayGM", dayGM);
        msgMap.put("dayHM", dayHM);
        
        int totalCount = 0;
		int once = (onceCount == null) ? ONCE_PACKAGE_ROWS : onceCount;
		
		List<EndDeviceVO> endDeviceList = 
				endDeviceManager.getEndDevicesVOByLocationIdExt(
					locationId, endDeviceId, start, limit, true
				);	
		totalCount = endDeviceList.size();
		
		StringBuilder sbFileName = new StringBuilder();
		StringBuilder sbSplFileName = new StringBuilder();
		if(filePath == null) filePath = FILE_PATH;
		
		sbFileName.append(TimeUtil.getCurrentTimeMilli());
		
		File file = ExcelUtil.initDirectory(filePath);
		if(file == null || !file.exists() || !file.isDirectory()) {
			mav.addObject("result", "fail");
			mav.addObject("msg", "file io error [" + file.getName() + "]");
			return mav;
		}
		List<EndDeviceVO> list = null;
		FacilitySituationDataMakeExcel wExcel = new FacilitySituationDataMakeExcel();
		
		int cnt = 1;
		int idx = 0;
		int fnum = 0;
		int splCnt = 0;
		
		if (totalCount <= once) {
			sbSplFileName = new StringBuilder();
			sbSplFileName.append(sbFileName);
			sbSplFileName.append(".xls");
			wExcel.writeReportExcel(endDeviceList, msgMap, false, filePath, sbSplFileName.toString());
			fileNameList.add(sbSplFileName.toString());
		} 
		else {
			for (int i = 0; i < totalCount; i++) {
				if ((splCnt * fnum + cnt) == totalCount || cnt == once) {
					sbSplFileName = new StringBuilder();
					sbSplFileName.append(sbFileName);
					sbSplFileName.append('(').append(++fnum).append(").xls");

					list = endDeviceList.subList(idx, (i + 1));

					wExcel.writeReportExcel(list, msgMap, false, filePath, sbSplFileName.toString());
					fileNameList.add(sbSplFileName.toString());
					list = null;
					splCnt = cnt;
					cnt = 0;
					idx = (i + 1);
				}
				cnt++;
			}
		}

		StringBuilder sbZipFile = new StringBuilder();
		sbZipFile.append(sbFileName).append(".zip");

		ZipUtils zutils = new ZipUtils();
		try {
			zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);
		}
		catch (Exception e) {
			mav.addObject("result", "fail");
			mav.addObject("msg", "file to zip proccess failed. " + e.getMessage());
			return mav;
		}
		
		mav.addObject("result", "success");
		mav.addObject("filePath", filePath);
		mav.addObject("fileName", fileNameList.get(0));
		mav.addObject("zipFileName", sbZipFile.toString());
		mav.addObject("fileNames", fileNameList);
		return mav;
	}
	
	@RequestMapping(value="/gadget/system/bems/excelFacilityStatus.do", method={ RequestMethod.POST })
	public ModelAndView excelFacilityStatus(
		@RequestParam int locationId,
		@RequestParam int endDeviceId,
		@RequestParam int start,
		@RequestParam Integer limit,
		@RequestParam(required=false) Integer supplierId,
		@RequestParam String location,
		@RequestParam String type,
		@RequestParam String manufacturerer,
		@RequestParam String model,
		@RequestParam String friendlyName, 
		@RequestParam String installDate,
		@RequestParam String powerConsumption,
		@RequestParam String modemType, 
		@RequestParam String modemSerial,
		@RequestParam(required=false) Integer onceCount,	
		@RequestParam(required=false) String filePath) {

		ModelAndView mav = new ModelAndView("/gadget/bems/facilityDownloadPopup");
		Map<String, String> msgMap = new HashMap<String, String>();
		List<String> fileNameList = new ArrayList<String>();
		
		msgMap.put("location", location);
        msgMap.put("type", type);
		msgMap.put("manufacturerer", manufacturerer);
        msgMap.put("model", model);
        msgMap.put("friendlyName", friendlyName);
        msgMap.put("installDate", installDate);
        msgMap.put("powerConsumption", powerConsumption);
        msgMap.put("modemType", modemType);
        msgMap.put("modemSerial", modemSerial);
          
        int totalCount = 0;
		int once = (onceCount == null) ? ONCE_PACKAGE_ROWS : onceCount;
		
		List<EndDeviceVO> endDeviceList = 
				endDeviceManager.getEndDevicesVOByLocationIdExt(
					locationId, endDeviceId, start, limit, true
				);	
		totalCount = endDeviceList.size();
		
		StringBuilder sbFileName = new StringBuilder();
		StringBuilder sbSplFileName = new StringBuilder();
		if(filePath == null) filePath = FILE_PATH;  
		
		
		sbFileName.append(TimeUtil.getCurrentTimeMilli());
		
		File file = ExcelUtil.initDirectory(filePath);
		if(file == null || !file.exists() || !file.isDirectory()) {
			mav.addObject("result", "fail");
			mav.addObject("msg", "file io error [" + file.getName() + "]");
			return mav;
		}
		
		List<EndDeviceVO> list = null;
		FacilityStatusDataMakeExcel wExcel = new FacilityStatusDataMakeExcel();
		
		int cnt = 1;
		int idx = 0;
		int fnum = 0;
		int splCnt = 0;
		
		if (totalCount <= once) {
			sbSplFileName = new StringBuilder();
			sbSplFileName.append(sbFileName);
			sbSplFileName.append(".xls");
			wExcel.writeReportExcel(endDeviceList, msgMap, false, filePath, sbSplFileName.toString());
			fileNameList.add(sbSplFileName.toString());
		} 
		else {
			for(int i = 0; i < totalCount; i++) {
				if ((splCnt * fnum + cnt) == totalCount || cnt == once) {
					sbSplFileName = new StringBuilder();
					sbSplFileName.append(sbFileName);
					sbSplFileName.append('(').append(++fnum).append(").xls");

					list = endDeviceList.subList(idx, (i + 1));

					wExcel.writeReportExcel(list, msgMap, false, filePath, sbSplFileName.toString());
					fileNameList.add(sbSplFileName.toString());
					list = null;
					splCnt = cnt;
					cnt = 0;
					idx = (i + 1);
				}
				cnt++;
			}
		}

		StringBuilder sbZipFile = new StringBuilder();
		sbZipFile.append(sbFileName).append(".zip");

		ZipUtils zutils = new ZipUtils();
		try {
			zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);
		}
		catch (Exception e) {
			mav.addObject("result", "fail");
			mav.addObject("msg", "file to zip proccess failed. " + e.getMessage());
			return mav;
		}
		
		mav.addObject("result", "success");
		mav.addObject("filePath", filePath);
		mav.addObject("fileName", fileNameList.get(0));
		mav.addObject("zipFileName", sbZipFile.toString());
		mav.addObject("fileNames", fileNameList);
		return mav;
	}
	
	@RequestMapping(value="/gadget/system/bems/excelFacilityHistory.do", method={ RequestMethod.POST })
	public ModelAndView excelFacilityHistory(
		@RequestParam int locationId,
		@RequestParam int endDeviceId,
		@RequestParam int start,
		@RequestParam Integer limit,
		@RequestParam Integer supplierId,
		@RequestParam String location,
		@RequestParam String categoryCode,
		@RequestParam String friendlyName,
		@RequestParam String preStatusCode,
		@RequestParam String statusCode, 
		@RequestParam String writeDatetime,
		@RequestParam(required=false) Integer onceCount,	
		@RequestParam(required=false) String filePath) {

		ModelAndView mav = new ModelAndView("/gadget/bems/facilityDownloadPopup");
		Map<String, String> msgMap = new HashMap<String, String>();
		List<String> fileNameList = new ArrayList<String>();
		
		msgMap.put("location", location);
        msgMap.put("categoryCode", categoryCode);
		msgMap.put("friendlyName", friendlyName);
        msgMap.put("preStatusCode", preStatusCode);
        msgMap.put("statusCode", statusCode);
        msgMap.put("writeDatetime", writeDatetime);
                 
        int totalCount = 0;
		int once = (onceCount == null) ? ONCE_PACKAGE_ROWS : onceCount;
		
		List<EndDeviceLog> endDeviceList = 
				endDeviceLogManager.getEndDeviceLogsExt(
					locationId, endDeviceId, start, limit, supplierId
				);
		totalCount = endDeviceList.size();
		
		StringBuilder sbFileName = new StringBuilder();
		StringBuilder sbSplFileName = new StringBuilder();
		if(filePath == null) filePath = FILE_PATH;  
		
		
		sbFileName.append(TimeUtil.getCurrentTimeMilli());
		
		File file = ExcelUtil.initDirectory(filePath);
		if(file == null || !file.exists() || !file.isDirectory()) {
			mav.addObject("result", "fail");
			mav.addObject("msg", "file io error [" + file.getName() + "]");
			return mav;
		}
		
		List<EndDeviceLog> list = null;
		FacilityHistoryDataMakeExcel wExcel = new FacilityHistoryDataMakeExcel();
		
		int cnt = 1;
		int idx = 0;
		int fnum = 0;
		int splCnt = 0;
		
		if (totalCount <= once) {
			sbSplFileName = new StringBuilder();
			sbSplFileName.append(sbFileName);
			sbSplFileName.append(".xls");
			wExcel.writeReportExcel(endDeviceList, msgMap, false, filePath, sbSplFileName.toString());
			fileNameList.add(sbSplFileName.toString());
		} 
		else {
			for (int i = 0; i < totalCount; i++) {
				if ((splCnt * fnum + cnt) == totalCount || cnt == once) {
					sbSplFileName = new StringBuilder();
					sbSplFileName.append(sbFileName);
					sbSplFileName.append('(').append(++fnum).append(").xls");

					list = endDeviceList.subList(idx, (i + 1));

					wExcel.writeReportExcel(list, msgMap, false, filePath, sbSplFileName.toString());
					fileNameList.add(sbSplFileName.toString());
					list = null;
					splCnt = cnt;
					cnt = 0;
					idx = (i + 1);
				}
				cnt++;
			}
		}

		StringBuilder sbZipFile = new StringBuilder();
		sbZipFile.append(sbFileName).append(".zip");

		ZipUtils zutils = new ZipUtils();
		try {
			zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);
		}
		catch (Exception e) {
			mav.addObject("result", "fail");
			mav.addObject("msg", "file to zip proccess failed. " + e.getMessage());
			return mav;
		}
		
		mav.addObject("result", "success");
		mav.addObject("filePath", filePath);
		mav.addObject("fileName", fileNameList.get(0));
		mav.addObject("zipFileName", sbZipFile.toString());
		mav.addObject("fileNames", fileNameList);
		return mav;
	}
}