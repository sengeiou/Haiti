package com.aimir.service.device.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.McuStatus;
import com.aimir.constants.CommonConstants.ModemSleepMode;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.RegType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.CommLogDao;
import com.aimir.dao.device.EventAlertLogDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MCUInstallImgDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.CommLog;
import com.aimir.model.device.CommStateByLocationVO;
import com.aimir.model.device.LocationByCommStateVO;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;
import com.aimir.model.device.MCUInstallImg;
import com.aimir.model.device.MCUTypeByCommStateVO;
import com.aimir.model.device.MCUTypeByLocationVO;
import com.aimir.model.device.Modem;
import com.aimir.model.device.OperationLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.DecimalPattern;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.DeviceRegistrationManager;
import com.aimir.service.device.MCUManager;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@WebService(endpointInterface = "com.aimir.service.device.MCUManager")
@Service(value = "mcuManager")
@Transactional(readOnly=false)
public class MCUManagerImpl implements MCUManager {
    private static Log log = LogFactory.getLog(MCUManagerImpl.class);
	
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    LocationDao locationDao;
	
    @Autowired
    MCUInstallImgDao mcuInstallImgDao;
    
    @Autowired
    CodeDao codeDao;    
    
    @Autowired
    CommLogDao commLogDao;    
    
    @Autowired
    OperationLogDao operationLogDao;
    
    @Autowired
    EventAlertLogDao eventAlertLogDao;
    
    @Autowired
    DeviceRegistrationManager deviceRegistrationManager;
    
	@Autowired
	SupplierDao supplierDao;

    @Autowired
    ModemDao modemDao;

    // 포맷 파일 저장 위치
 	private String ctxRoot;
    
    @Deprecated
	public List<Map<String, Object>> getGridData(Map<String, String> conditionMap) {
    	
		Supplier supplier = supplierDao.get(Integer.parseInt(conditionMap.get("supplierId")));

		if(conditionMap.get("locationId") != null && !(conditionMap.get("locationId")).trim().equals("")) {
			List<Integer> locations = locationDao.getLeafLocationId(Integer.parseInt(conditionMap.get("locationId")), Integer.parseInt(conditionMap.get("supplierId")));
			String sLocations = "";
			for(int i=0 ; i<locations.size() ; i++) {
				if(i == 0) {
					sLocations += locations.get(i);
				} else {
					sLocations += "," + locations.get(i);
				}
			}
			
			conditionMap.put("locationId", sLocations);
		}
		
    	List<MCU> list = mcuDao.getGridData(conditionMap);
    	List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
    	
    	for (MCU mcu : list) {

    		Map<String, Object> changeMap = new HashMap<String, Object>();
    		
    		changeMap.put("id", StringUtil.nullToZero(mcu.getId()));
    		changeMap.put("sysID", StringUtil.nullToBlank(mcu.getSysID()));
    		changeMap.put("sysName", StringUtil.nullToBlank(mcu.getSysName()));
    		changeMap.put("sysPhoneNumber", StringUtil.nullToBlank(mcu.getSysPhoneNumber()));
    		changeMap.put("ipAddr", StringUtil.nullToBlank(mcu.getIpAddr()));
    		changeMap.put("sysSwVersion", StringUtil.nullToBlank(mcu.getSysSwVersion()));
    		changeMap.put("lastCommDateBasic", StringUtil.nullToBlank(mcu.getLastCommDate()));
    		changeMap.put("installDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mcu.getInstallDate()), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
    		changeMap.put("lastCommDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mcu.getLastCommDate()), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
    		
    		if (StringUtil.nullToBlank(mcu.getMcuType()).length() == 0) {
    			changeMap.put("mcuTypeName", "");
    		} else {
    			changeMap.put("mcuTypeName", StringUtil.nullToBlank(mcu.getMcuType().getName()));
    		}
    		
    		returnList.add(changeMap);
    	}
    	
    	return returnList;
    }

	@SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDcuGridData(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer page = (Integer) conditionMap.get("page");
        Integer limit = (Integer) conditionMap.get("limit");
		Supplier supplier = supplierDao.get(supplierId);

		if (locationId != null) {
			List<Integer> locations = locationDao.getLeafLocationId(locationId, supplierId);
			conditionMap.put("locationIdList", locations);
		} /*else {
			// User 계정이 Admin이고, DSO 정보 입력란에 아무것도 입력하지 않고 검색한 경우 - default값으로 location table의 첫번째 인덱스 데이터를 반환
			List<Location> locationRoot = locationDao.getRootLocationList();
			List<Integer> locations = locationDao.getLeafLocationId(locationRoot.get(0).getId(), supplierId);
			conditionMap.put("locationIdList", locations);
		}*/
		
        Code deleteCode = codeDao.getCodeIdByCodeObject(McuStatus.Delete.getCode());
        conditionMap.put("deleteCode", deleteCode);
        Code NormalCode = codeDao.getCodeIdByCodeObject(McuStatus.Normal.getCode());
        conditionMap.put("normalCodeId", NormalCode);
        Code SecurityErrorCode = codeDao.getCodeIdByCodeObject(McuStatus.SecurityError.getCode());
        conditionMap.put("securityErrorCodeId", SecurityErrorCode);
        Code CommErrorCode = codeDao.getCodeIdByCodeObject(McuStatus.CommError.getCode());
        conditionMap.put("commErrorCodeId", CommErrorCode);
        Code PowerDownCode = codeDao.getCodeIdByCodeObject(McuStatus.PowerDown.getCode());
        conditionMap.put("powerDownCodeId", PowerDownCode);
        
		Map<String, Object> result = mcuDao.getDcuGridData(conditionMap, false);
		List<MCU> list = (List<MCU>)result.get("list");
    	List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
    	Map<String, Object> changeMap = null;
    	String lang = supplier.getLang().getCode_2letter();
    	String country = supplier.getCountry().getCode_2letter();
        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");
        int idx = 1;
        
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
        
        for (MCU mcu : list) {
            changeMap = new HashMap<String, Object>();

            if (page != null && limit != null) {
                changeMap.put("rowNo", dfMd.format((((page-1) * limit) + idx)));
                idx++;
            }

            changeMap.put("mcuId", StringUtil.nullToZero(mcu.getId()));
            changeMap.put("mcuSerial", StringUtil.nullToBlank(mcu.getSysSerialNumber())); 
            if (mcu.getMcuType() != null) {
                changeMap.put("dcuType", StringUtil.nullToBlank(mcu.getMcuType().getDescr()));
            } else {
                changeMap.put("dcuType", "");
            }
            changeMap.put("sysID", StringUtil.nullToBlank(mcu.getSysID()));
            changeMap.put("sysName", StringUtil.nullToBlank(mcu.getSysName()));

            if (mcu.getDeviceModel() != null) {
                changeMap.put("model", StringUtil.nullToBlank(mcu.getDeviceModel().getName()));

                if (mcu.getDeviceModel().getDeviceVendor() != null) {
                    changeMap.put("vendor", StringUtil.nullToBlank(mcu.getDeviceModel().getDeviceVendor().getName()));
                } else {
                    changeMap.put("vendor", "");
                }
            } else {
                changeMap.put("model", "");
                changeMap.put("vendor", "");
            }

            changeMap.put("sysPhoneNumber", StringUtil.nullToBlank(mcu.getSysPhoneNumber()));

            if (mcu.getLocation() != null) {
                changeMap.put("location", StringUtil.nullToBlank(mcu.getLocation().getName()));
            } else {
                changeMap.put("location", "");
            }
            changeMap.put("ipAddr", StringUtil.nullToBlank(mcu.getIpAddr()));
            changeMap.put("sysSwVersion", StringUtil.nullToBlank(mcu.getSysSwVersion()));
            changeMap.put("sysHwVersion", StringUtil.nullToBlank(mcu.getSysHwVersion()));
            changeMap.put("lastCommDateBasic", StringUtil.nullToBlank(mcu.getLastCommDate()));
            changeMap.put("installDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mcu.getInstallDate()), lang, country));

            if (mcu.getProtocolType() != null) {
                changeMap.put("protocolType", StringUtil.nullToBlank(mcu.getProtocolType().getDescr()));
            } else {
                changeMap.put("protocolType", "");
            }

            changeMap.put("lastCommDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mcu.getLastCommDate()), lang, country));
            if (StringUtil.nullToBlank(mcu.getLastCommDate()).isEmpty()) {
                changeMap.put("commState", "");
            } else if (mcu.getLastCommDate().compareTo(datePre24H) >= 0) {
                changeMap.put("commState", "fmtMessage00");
            } else if (mcu.getLastCommDate().compareTo(datePre24H) < 0 && mcu.getLastCommDate().compareTo(datePre48H) >= 0) {
                changeMap.put("commState", "fmtMessage24");
            } else if (mcu.getLastCommDate().compareTo(datePre48H) < 0) {
                changeMap.put("commState", "fmtMessage48");
			} else {
                changeMap.put("commState", "");}
            if(mcu.getMcuStatus()!= null){
				if ((mcu.getMcuStatusCodeId()).equals(SecurityErrorCode.getId())) {
					changeMap.put("commState", SecurityErrorCode.getCode());
				}
				if ((mcu.getMcuStatusCodeId()).equals(CommErrorCode.getId())) {
					changeMap.put("commState", CommErrorCode.getCode());
				}
				if (mcu.getMcuStatusCodeId().equals(PowerDownCode.getId())) {
					changeMap.put("commState", PowerDownCode.getCode());
				}
            }

            if (StringUtil.nullToBlank(mcu.getMcuType()).length() == 0) {
                changeMap.put("mcuTypeName", "");
            } else {
                changeMap.put("mcuTypeName", StringUtil.nullToBlank(mcu.getMcuType().getName()));
            }
            
            changeMap.put("ipv6Addr", StringUtil.nullToBlank(mcu.getIpv6Addr()));
            changeMap.put("amiNetworkAddress", StringUtil.nullToBlank(mcu.getAmiNetworkAddress()));
            changeMap.put("macAddr", StringUtil.nullToBlank(mcu.getMacAddr()));
            changeMap.put("swrev", StringUtil.nullToBlank(mcu.getSysSwRevision()));
            
            changeMap.put("sysLocation", StringUtil.nullToBlank(mcu.getSysLocation()));
            changeMap.put("locationId", StringUtil.nullToBlank(mcu.getLocationId()));
            changeMap.put("gs1", StringUtil.nullToBlank(mcu.getGs1()));
            changeMap.put("imei", StringUtil.nullToBlank(mcu.getImei()));
            changeMap.put("simNumber", StringUtil.nullToBlank(mcu.getSimNumber()));
            changeMap.put("iccId", StringUtil.nullToBlank(mcu.getIccId()));
            changeMap.put("manufacturedDate", StringUtil.nullToBlank(mcu.getManufacturedDate()));
            changeMap.put("po", StringUtil.nullToBlank(mcu.getPo()));
            changeMap.put("lot", "-");
            
            returnList.add(changeMap);
        }

    	return returnList;
    }

    @Deprecated
	public List<MCU> getGridDataExcel(Map<String, String> conditionMap) {
    	
//		Supplier supplier = supplierDao.get(Integer.parseInt(conditionMap.get("supplierId")));

		if(conditionMap.get("locationId") != null && !(conditionMap.get("locationId")).trim().equals("")) {
			List<Integer> locations = locationDao.getLeafLocationId(Integer.parseInt(conditionMap.get("locationId")), Integer.parseInt(conditionMap.get("supplierId")));
			String sLocations = "";
			for(int i=0 ; i<locations.size() ; i++) {
				if(i == 0) {
					sLocations += locations.get(i);
				} else {
					sLocations += "," + locations.get(i);
				}
			}
			
			conditionMap.put("locationId", sLocations);
		}
		
    	List<MCU> list = mcuDao.getGridData(conditionMap);
    	
    	return list;
    }

	@SuppressWarnings("unchecked")
    public List<MCU> getDcuGridDataExcel(Map<String, Object> conditionMap) {
	    Integer supplierId = (Integer)conditionMap.get("supplierId");
	    Integer locationId = (Integer)conditionMap.get("locationId");

	    if (locationId != null) {
	        List<Integer> locations = locationDao.getLeafLocationId(locationId, supplierId);
	        conditionMap.put("locationIdList", locations);
	    }

	    Map<String, Object> result = mcuDao.getDcuGridData(conditionMap, false);
	    List<MCU> list = (List<MCU>)result.get("list");

	    return list;
	}

    @Deprecated
    public Map<String, String> getMcuGridDataTotalCount(String[] conditionArray) {
    	
		if(conditionArray[2] != null && !(conditionArray[2].trim().equals(""))) {
			List<Integer> locations = locationDao.getLeafLocationId(Integer.parseInt(conditionArray[2]), Integer.parseInt(conditionArray[12]));
			String sLocations = "";
			for(int i=0 ; i<locations.size() ; i++) {
				if(i == 0) {
					sLocations += locations.get(i);
				} else {
					sLocations += "," + locations.get(i);
				}
			}
			
			conditionArray[2] = sLocations;
		}
		
    	Integer count = mcuDao.getMcuGridDataTotalCount(conditionArray);
    	
    	Map<String, String> rtnMap = new HashMap<String, String>();
    	rtnMap.put("total", Integer.toString(count));
    	
    	return rtnMap;
    }
    
    public Integer getDcuGridDataTotalCount(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer locationId = (Integer)conditionMap.get("locationId");

        if (locationId != null) {
            List<Integer> locations = locationDao.getLeafLocationId(locationId, supplierId);
            conditionMap.put("locationIdList", locations);
        } /*else {
        	// User 계정이 Admin이고, DSO 정보 입력란에 아무것도 입력하지 않고 검색한 경우 - default값으로 location table의 첫번째 인덱스 데이터를 반환
        	List<Location> locationRoot = locationDao.getRootLocationList();
        	List<Integer> locations = locationDao.getLeafLocationId(locationRoot.get(0).getId(), supplierId);
        	conditionMap.put("locationIdList", locations);
        }*/

        Map<String, Object> result = mcuDao.getDcuGridData(conditionMap, true);
        Integer count = (Integer)result.get("count");

        return count;
    }

	public List<MCUTypeByLocationVO> getMcusByCondition(Map<String, String> conditionMap) {

		List<MCUTypeByLocationVO> rtnValues = new ArrayList<MCUTypeByLocationVO>();
		MCUTypeByLocationVO rtnValue = null;
		
		List<MCU> mcus = mcuDao.getMcusByCondition(conditionMap);

		for(MCU mcu : mcus) {
			
			rtnValue = new MCUTypeByLocationVO();
			rtnValue.setId(mcu.getId());
			
			if(mcu.getMcuType() != null)
				rtnValue.setMcuType(mcu.getMcuType().getName());
			else
				rtnValue.setMcuType("");
			
			if(mcu.getLastCommDate() != null && !"".equals(mcu.getLastCommDate())) 
				rtnValue.setCommState(decideCommState(Long.parseLong(mcu.getLastCommDate())));
			else
				rtnValue.setCommState("");
			
			if(mcu.getLocation() != null) 				
				rtnValue.setLocationName1(mcu.getLocation().getName());
			else
				rtnValue.setLocationName1("");
			
			if(mcu.getSysID() != null)
				rtnValue.setSysID(mcu.getSysID());
			else
				rtnValue.setSysID("");
				
			rtnValues.add(rtnValue);
		}

		return rtnValues;
	}	
	
	private String decideCommState(long lastCommDate) {

		Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
		Map<String, String> FEBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -48);
	
		long TFDate = Long.parseLong(TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", ""));
		long FEDate = Long.parseLong(FEBeforeHour.get("date").replace("-", "") + FEBeforeHour.get("time").replace(".", ""));
		
		if(lastCommDate >= TFDate) 
			return "Normal";
		else if(lastCommDate < TFDate && lastCommDate >= FEDate)
			return "Nan24H";
		else if(lastCommDate < FEDate)
			return "Nan48H";	
		else
			return "";
	}
	
	public List<String> getHwVersions() {

		return mcuDao.getHwVersions();
	}

	
	public List<String> getSwVersions() {

		return mcuDao.getSwVersions();
	}

	// 로케이션 리스트 맵를 얻어 옵니다.
	public Map<String, List<String>> getLocationTreeToRows(int supplierId) {		
		
		Map<String, List<String>> locations = new HashMap<String, List<String>>();			
		List<String> keys = new ArrayList<String>();
		List<String> locationNames = new ArrayList<String>();
		
		List<Location> rootLocations = locationDao.getParentsBySupplierId(supplierId);
		
		for(Location rootLocation : rootLocations) {
			if(rootLocation != null && rootLocation.getChildren() != null) 
				makeLocationTreeToRows(keys, locationNames, rootLocation, "");
		}
			
		locations.put("keys", keys);
		locations.put("locationNames", locationNames);
		
		return locations;
	}
	
	// 자식로케이션들을 ROW 형태로 만들어 주찌뽕
	private void makeLocationTreeToRows(List<String> keys, List<String> locationNames, Location location, String parentLocationName)  {
		
		String locationName = parentLocationName + "-" + location.getName();
		// 루트 로케이션은 앞에 '-'를 붙이지 않는다.
		if(location.getParent() == null)
			locationName = location.getName();
		
		keys.add(Integer.toString(location.getId()));
		locationNames.add(locationName);				
		
		Set<Location> childLocationSet = location.getChildren();
		
		// 일단 오도 바이정렬
		List<Location> sortedChildLocations = makeSortedChildLocations(childLocationSet);
		
		for(Location currentLocation : sortedChildLocations) {
			
			if(currentLocation.getChildren() != null) 
				
				makeLocationTreeToRows(keys, locationNames, currentLocation, locationName);
		}
	}
	
	//set에 들어 있는 로케이션을 Order 순에 따른 List 형으로 만들어 낸다던데?
	private List<Location> makeSortedChildLocations(Set<Location> locationSet) {
		
		List<Location> locations = new ArrayList<Location>();
		
		SortedMap<Integer, Location> locationMap = new TreeMap<Integer, Location>();
		
		for(Location location : locationSet) {	
			
			locationMap.put(Integer.valueOf(location.getOrderNo()), location);		
		}

		for(Entry<Integer, Location> entry : locationMap.entrySet()) {		
			
			locations.add(entry.getValue());			
		}
		
		return locations;
	}

	public MCU getMCU(Integer mcuId) {
		
		MCU mcu = mcuDao.get(mcuId);
		
		return mcu;
	}
	
	public MCU getMCU(String name) {
        MCU mcu = mcuDao.get(name);
        return mcu;
    }

	public void updateMCU(Map<String, String> map) {
		MCU mcu = mcuDao.get(Integer.parseInt(map.get("mcuId")));
		mcu.setInstallDate(map.get("installDate"));
		mcu.setIpAddr(map.get("ipAddr"));
		mcu.setIpv6Addr(map.get("ipv6Address"));
		mcu.setAmiNetworkAddress(map.get("amiNetworkAddress"));
		mcu.setAmiNetworkAddressV6(map.get("amiNetworkAddressV6"));
		mcu.setSysPhoneNumber(map.get("sysPhoneNumber"));
		mcu.setLastswUpdateDate(map.get("lastswUpdateDate"));
		
		if(map.get("sysHwBuild") != null && !"".equals(map.get("sysHwBuild")))
			mcu.setSysHwBuild(map.get("sysHwBuild"));
		if(map.get("sysSerialNumber") != null && !"".equals(map.get("sysSerialNumber")))
			mcu.setSysSerialNumber(map.get("sysSerialNumber"));
		if(map.get("sysTlsPort") != null && !"".equals(map.get("sysTlsPort")))
			mcu.setSysTlsPort(Integer.parseInt(map.get("sysTlsPort")));
		if(map.get("sysTlsVersion") != null && !"".equals(map.get("sysTlsVersion")))
			mcu.setSysTlsVersion(map.get("sysTlsVersion"));
		if(map.get("macAddr") != null && !"".equals(map.get("macAddr")))
			mcu.setMacAddr(map.get("macAddr"));
		if(map.get("hwVersion") != null && !"".equals(map.get("hwVersion")))
			mcu.setSysHwVersion(map.get("hwVersion"));
		if(map.get("protocolType") != null && !"".equals(map.get("protocolType")))
			mcu.setProtocolType(codeDao.get(Integer.parseInt(map.get("protocolType"))));
		if(map.get("mcuStatus") != null && !"".equals(map.get("mcuStatus"))) {
			Code statusCode = codeDao.get(Integer.parseInt(map.get("mcuStatus")));
			if(McuStatus.Delete.getCode().equals(statusCode.getCode())) {
				Set<Modem> modemList = mcu.getModem();
				if(modemList.size() > 0) {
					for (Modem modem : modemList) {
						if(modem != null && (modem.getModemStatus() == null || !ModemSleepMode.Delete.getCode().equals(modem.getModemStatus().getCode()))) {
							break;
						} else {
							mcu.setMcuStatus(statusCode);
						}
					}
				} else {
					mcu.setMcuStatus(statusCode);
				}
				
			} else {
				mcu.setMcuStatus(statusCode);
			}
			
		}

		if(map.get("locationId") != null && !"".equals(map.get("locationId")))
			mcu.setLocation(locationDao.get(Integer.parseInt(map.get("locationId"))));

		if (!StringUtil.nullToBlank(mcu.getSysLocation()).equals(StringUtil.nullToBlank(map.get("sysLocation"))))
		    mcu.setSysLocation(map.get("sysLocation"));

        if (!StringUtil.nullToBlank(mcu.getSysLocalPort()).equals(StringUtil.nullToBlank(map.get("sysLocalPort")))) {
            if (StringUtil.nullToBlank(map.get("sysLocalPort")).isEmpty()) {
                mcu.setSysLocalPort(null);
            } else {
                mcu.setSysLocalPort(Integer.valueOf(map.get("sysLocalPort")));
            }
        }

		mcuDao.update(mcu);
	}

	public Integer getMCUCountByCondition(Map<String, String> condition) {		
		
		Integer totalCount = mcuDao.getMCUCountByCondition(condition); 
		
		return totalCount;
	}	

	public Map<String, String> getPagingInfo(int page, Integer totalRowCount, String pagingType) {

		Map<String, String> map = new HashMap<String, String>();
		
		int rowPerPage = -1;
		int pagePerBlock = -1;
		
		// MCU 장비 목록
		if("GRID".equals(pagingType)) {
			rowPerPage = MCUPagingInfo.rowPerPage;
			pagePerBlock = MCUPagingInfo.pagePerBlock;
		
		// 설치된 장비 이미지
		} else if("IMG".equals(pagingType)) {
			rowPerPage = MCUPagingInfo.istImgRowPerPage;
			pagePerBlock = MCUPagingInfo.istImgPagePerBlock;			
		}
				
    	int startRow = (page - 1) * rowPerPage + 1;
    	int endRow = (totalRowCount > (page * rowPerPage)) ? page * rowPerPage : totalRowCount;
    	
    	int totalPageCount = (int)Math.ceil((double)totalRowCount / (double)rowPerPage);
    	int startPage = page - ((page - 1) % pagePerBlock);
    	int endPage = (page < totalPageCount) ? startPage + (pagePerBlock - 1) : totalPageCount;
    	if(endPage > totalPageCount) endPage = totalPageCount;
    	
    	String prevPage = (page > 1) ? "true" : "false"; 
    	String nextPage = (page < totalPageCount) ? "true" : "false";
    	String prevBlock = (page > pagePerBlock)? "true" : "false";
    	String nextBlock = (totalPageCount > endPage) ? "true" : "false";     		    	    	
    	
    	map.put("totalRowCount", Integer.toString(totalRowCount));
    	map.put("totalPageCount", Integer.toString(totalPageCount));
    	map.put("startRow", Integer.toString(startRow));
    	map.put("endRow", Integer.toString(endRow));
    	map.put("startPage", Integer.toString(startPage));
    	map.put("endPage", Integer.toString(endPage));
    	map.put("prevPage", prevPage);
    	map.put("nextPage", nextPage);
    	map.put("prevBlock", prevBlock);
    	map.put("nextBlock", nextBlock);
    	map.put("page", Integer.toString(page));
    	
		return map;
	}

	public List<MCUTypeByLocationVO> getMCUTypeByLocationDataBack() {

		return mcuDao.getMCUTypeByLocationDataBack();		
	}

	public String getMCUTypeByLocationData(String supplierId) {

		return mcuDao.getMCUTypeByLocationData(supplierId);		
	}
	
	public Map<String, Object> getMCUTypeListByLocationData(String supplierId) {		
		List<Object> list = mcuDao.getMCUTypeListByLocationData(supplierId);
		
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> seriesList = new ArrayList<Map<String,Object>>();
		
		int currentDisplaySize = 0;          // 현재 로케이션 수
		String currentMcuTypeName = null;    // 현재 mcuType
		String pastMcuTypeName = "";         // 과거 mcuType
		
		Map<String, Object> map = null;
		for(Object obj: list) {
			Object[] data = (Object[]) obj;

			boolean isExist = false;
			
			for(int i=0 ; i < seriesList.size() ; i++) {
				Map<String, Object> s = seriesList.get(i);
				if(s.get("seriesName").equals(data[2])) {
					isExist = true;
					break;
				}
			}
			
			if(!isExist) {
				Map<String, Object> series = new HashMap<String, Object>();
				series.put("seriesName", String.valueOf(data[2]).trim());
				seriesList.add(series);
			}
			
			currentMcuTypeName = data[1].toString();
			
			// MCUTYPE 이름이 바뀌면..
			if(!pastMcuTypeName.equals(currentMcuTypeName)) {
								
				pastMcuTypeName = currentMcuTypeName;
				currentDisplaySize = 1;
				map = new HashMap<String, Object>();
				dataList.add(map);
				map.put("mcuType", currentMcuTypeName);				
			}
			
			map.put("locationName" + currentDisplaySize, data[2].toString());
			map.put("locationCnt" + currentDisplaySize, data[3].toString());
			map.put("locationId" + currentDisplaySize, data[4].toString());
			map.put("mcuType" + currentDisplaySize, data[0].toString());
			
			currentDisplaySize++;
			
		}
		
		result.put("dataList", dataList);
		result.put("seriesList", seriesList);
		
		return result;
	}
	
	public List<MCUTypeByCommStateVO> getMCUTypeByCommStateData(String supplierId) {
		
		List<MCUTypeByCommStateVO> result = mcuDao.getMCUTypeByCommStateData(supplierId);
		
		return result;
	}

	public String getLocationByMCUTypeData(String supplierId) {

		return mcuDao.getLocationByMCUTypeData(supplierId);
	}
	
	public List<Map<String, Object>> getLocationListByMCUTypeData(String supplierId) {

		return mcuDao.getLocationListByMCUTypeData(supplierId);
	}

	public List<LocationByCommStateVO> getLocationByCommStateData(String supplierId) {
		return mcuDao.getLocationByCommStateData(supplierId);
	}

	public String getCommStateByMCUTypeData(String supplierId) {

		return 	mcuDao.getCommStateByMCUTypeData(supplierId);
	}
	
	public List<Map<String, Object>> getCommStateListByMCUTypeData(String supplierId) {

		return 	mcuDao.getCommStateListByMCUTypeData(supplierId);
	}

	public List<CommStateByLocationVO> getCommStateByLocationDataBack() {

		return mcuDao.getCommStateByLocationDataBack();		
	}

	public String getCommStateByLocationData(String supplierId) {

		return mcuDao.getCommStateByLocationData(supplierId);		
	}
	
	public Map<String, Object> getCommStateListByLocationData(String supplierId) {
		
		List<Object> list = mcuDao.getCommStateListByLocationData(supplierId);

		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> seriesList = new ArrayList<Map<String,Object>>();
		
		int currentDisplaySize = 1;          // 현재 로케이션 수
		String currentCommState = null;    // 현재 mcuType
		String pastCommState = "";         // 과거 mcuType
		String cnt = null;
		String locationId = null;
		
		Map<String, Object> map = new HashMap<String, Object>();
		for(Object obj: list) {
			Object[] data = (Object[]) obj;

			boolean isExist = false;
			
			for(int i=0 ; i < seriesList.size() ; i++) {
				Map<String, Object> s = seriesList.get(i);
				if(s.get("seriesName").equals(data[1])) {
					isExist = true;
					break;
				}
			}
			
			if(!isExist) {
				Map<String, Object> series = new HashMap<String, Object>();
				series.put("seriesName", String.valueOf(data[1]).trim());
				seriesList.add(series);
			}
			
			currentCommState = data[0].toString();

			if(!pastCommState.equals(currentCommState)) {
				
				if(!"".equals(pastCommState)) {
					map.put("commState", pastCommStateToName(pastCommState));
					dataList.add(map);
					
					map = new HashMap<String, Object>();
					currentDisplaySize = 1;	
				}
				
				pastCommState = currentCommState;
			}

			if(data[3] != null)
				cnt = data[3].toString();
			else
				cnt = "0";
			
			if(data[2] != null)
				locationId = data[2].toString();
			else
				locationId = "";			
			
			map.put("locationName" + currentDisplaySize, data[1].toString());
			map.put("locationCnt" + currentDisplaySize, cnt);
			map.put("locationId" + currentDisplaySize, locationId);
			
			currentDisplaySize++;
			
		}
		
		map.put("commState", pastCommStateToName(pastCommState));
		dataList.add(map);
		
		result.put("dataList", dataList);
		result.put("seriesList", seriesList);
		
		return result;
	}

	@Deprecated
    public List<Map<String, String>> getBrokenLogs(String[] array) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("startDate", array[0] + "000000");
        map.put("endDate", array[1] + "235959");
        map.put("mcuId", array[2]);
        map.put("page", array[3]);
        map.put("pageSize", array[4]);

        return eventAlertLogDao.getEventAlertLogs(map);
    }

    @Deprecated
    public List<OperationLog> getCommandLogs(String[] array) {

        String startDate = array[0];
        String endDate = array[1];
        String targetName = array[2];
        String page = array[3];
        String pageSize = array[4];

        Map<String, String> conditionMap = new HashMap<String, String>();
        conditionMap.put("period", " ");
        conditionMap.put("date", "");
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("targetName", targetName);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", pageSize);

        return operationLogDao.getGridData(conditionMap);
    }

    @Deprecated
    public List<CommLog> getCommunicationLogs(String[] array) {

        String startDate = array[0];
        String endDate = array[1];
        String name = array[2];
        String page = array[3];
        String pageSize = array[4];

        Map<String, String> conditionMap = new HashMap<String, String>();
        conditionMap.put("period", " ");
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("senderId", name);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", pageSize);

        return commLogDao.getCommLogGridData(conditionMap);
    }

	public List<MockData> getUpdateLogs(String[] array) {

		int startPage = Integer.parseInt(array[2]);
		int endPage = startPage + Integer.parseInt(array[3]);
		
		return SingletonLogMockData.getInstance().getMockDatas(startPage, endPage);
	}

	@Deprecated
    public Map<String, String> getBrokenLogCount(String[] array) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("startDate", array[0].replace("/", "") + "000000");
        map.put("endDate", array[1].replace("/", "") + "235959");
        map.put("mcuId", array[2]);
        map.put("page", array[3]);
        map.put("pageSize", array[4]);

        String totalCount = eventAlertLogDao.getEventAlertLogCount(map);

        Map<String, String> result = new HashMap<String, String>();
        result.put("total", totalCount);

        return result;

    }

	@Deprecated
    public Map<String, String> getCommandLogCount(String[] array) {

        String startDate = array[0];
        String endDate = array[1];
        String targetName = array[2];

        Map<String, String> conditionMap = new HashMap<String, String>();
        conditionMap.put("period", " ");
        conditionMap.put("date", "");
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("targetName", targetName);

        Map<String, String> rtnMap = new HashMap<String, String>();
        rtnMap.put("total", operationLogDao.getOperationLogMaxGridDataCount(conditionMap));

        return rtnMap;
    }

	@Deprecated
    public Map<String, String> getCommunicationLogCount(String[] array) {

        Map<String, String> conditionMap = new HashMap<String, String>();
        conditionMap.put("period", " ");
        conditionMap.put("startDate", array[0]);
        conditionMap.put("endDate", array[1]);
        conditionMap.put("senderId", array[2]);

        Map<String, String> map = new HashMap<String, String>();
        Map<String, String> comLogDataMap = commLogDao.getCommLogData(conditionMap);
        map.put("sendSum", comLogDataMap.get("sendSum"));
        map.put("sendMax", comLogDataMap.get("sendMax"));
        map.put("sendMin", comLogDataMap.get("sendMin"));
        map.put("rcvSum", comLogDataMap.get("rcvSum"));
        map.put("rcvdMax", comLogDataMap.get("rcvdMax"));
        map.put("rcvMin", comLogDataMap.get("rcvMin"));
        map.put("total", commLogDao.getCommLogGridDataCount(conditionMap));

        return map;
    }

	public Map<String, String> getUpdateLogCount(String[] array) {

		Map<String, String> map = new HashMap<String, String>();
		map.put("totalRecordCount", SingletonLogMockData.getInstance().getTotalRecordSize() + "");
		
		return map;
	}

	@Deprecated
    public Set<Modem> getConnectedDevices(Integer mcuId) {

        MCU mcu = mcuDao.get(mcuId);

        return mcu.getModem();
    }

	public List<MCU> getChartMCUs(String[] array) {
		
		List<MCU> mcus = new ArrayList<MCU>();
		
		MCU mcu = null;
		
		for(int i = 0 ; i < 5 ; i++) {
			
			mcu = new MCU();
			mcu.setId(i);
		}
		
		return mcus;
	}

	public MCU insertMCU(MCU mcu) {
		
		MCU returnMcu = null;
		ResultStatus insertResult = ResultStatus.SUCCESS;
		
	    try{	
	    	returnMcu = mcuDao.add(mcu);
	    	mcuDao.flushAndClear();
  			
  		}catch(Exception e){
  			insertResult = ResultStatus.FAIL;
  		}finally{
  			Map<String, Object> logData = new HashMap<String, Object>();
  			
  			logData.put("deviceType", 	TargetClass.DCU);
  			logData.put("deviceName",   mcu.getSysID());
  			logData.put("deviceModel",  mcu.getDeviceModel());
  			logData.put("resultStatus", insertResult);
  			logData.put("regType", 		RegType.Manual);
  			logData.put("supplier", mcu.getSupplier());
  			
  			deviceRegistrationManager.insertDeviceRegLog(logData);
  		}
		
		return returnMcu;
	}

	public MCU updateMCU(MCU mcu) {
	    // RF 단말과 좌표 연동하기 위해 추가됨. 2016.05.18
	    MCU _mcu = mcuDao.get(mcu.getSysID());
	    if (_mcu != null && mcu.getId() == null) {
	        log.info("McuID[" + mcu.getSysID() + 
                    "] X[" + mcu.getGpioX() + 
                    "] Y[" + mcu.getGpioY() + 
                    "] Z[" + mcu.getGpioZ() + "]");
	        
	        if (mcu.getGpioX() != null)
	            _mcu.setGpioX(mcu.getGpioX());
	        if (mcu.getGpioY() != null)
                _mcu.setGpioY(mcu.getGpioY());
	        if (mcu.getGpioZ() != null)
                _mcu.setGpioZ(mcu.getGpioZ());
	        
	        return mcuDao.update(_mcu);
	    }
		return mcuDao.update(mcu);
	}

	public void deleteMCU(Integer mcuId) {

		MCU entity = mcuDao.get(mcuId);
		entity.setMcu(null);
		entity.setDeviceModel(null);
		
		List<MCU> childMcus = entity.getChildMcus();
		List<MCUInstallImg> imgs = entity.getMcuInstallImgs();
		
		for(MCU mcu : childMcus) {
			mcu.setMcu(null);
		}
				
		for(MCUInstallImg img : imgs) {
			mcuInstallImgDao.delete(img);
		}
		
		// list null 처리 안해 주면 에러 발생(근대 2번 처리 하믄 2번째에서는 실행 됨.) 근대 왜 모뎀은 list null 처리 안해도 될까? 
		entity.setChildMcus(null);
		entity.setMcuInstallImgs(null);

		mcuDao.delete(entity);
	}
	
	public Integer updateDcuStatus(Integer mcuId) {
		Map<String, Object> condition = new HashMap<String, Object>();
		int returnData = 0;
		MCU mcu = mcuDao.get(mcuId);
		Code deleteCode = codeDao.getCodeIdByCodeObject(McuStatus.Delete.getCode());
		returnData = mcuDao.updateDcuStatus(mcuId, deleteCode);
		return returnData;
	}
	
	public List<Object> getMCUNameList(Map<String, Object> condition){
		List<Object> result = new ArrayList<Object>();
		
		result = mcuDao.getMCUNameList(condition);		
		
		return result;
		
	}
	
	private String pastCommStateToName(String commState) {
		
		String rtnValue = null;
		
		if("0".equals(commState)) {
			rtnValue = "normal";	
		} else if("1".equals(commState)) {
			rtnValue = "24Hours";	
		} else if("2".equals(commState)) {
			rtnValue = "48Hours";
		} else if("3".equals(commState)) {
			rtnValue = "others";
		}
		return rtnValue;
	}
	
	public String getInstallDateChage(MCU mcu, Integer supplierId){
		
		Supplier supplier = supplierDao.get(supplierId);

		return TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mcu.getInstallDate()), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
	}
	
	public String getLastCommDateChage(MCU mcu, Integer supplierId){
		
		Supplier supplier = supplierDao.get(supplierId);
		
		return TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mcu.getLastCommDate()), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
	}

    /**
     * method name : getConnectedDeviceList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getConnectedDeviceList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer page = (Integer) conditionMap.get("page");
        Integer limit = (Integer) conditionMap.get("limit");
        int idx = 1;
        result = modemDao.getMcuConnectedDeviceList(conditionMap, false);

        for (Map<String, Object> map : result) {
            map.put("rowNo", (((page-1) * limit) + idx));
            idx++;
        }

        return result;
    }

    /**
     * method name : getConnectedDeviceListTotalCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public Integer getConnectedDeviceListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = modemDao.getMcuConnectedDeviceList(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

    /**
     * method name : getCommLogList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getCommLogList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer page = (Integer) conditionMap.get("page");
        Integer limit = (Integer) conditionMap.get("limit");
        Integer supplierId = (Integer) conditionMap.get("supplierId");

        int idx = 1;
        Integer rowNo = 0;
        result = commLogDao.getMcuCommLogList(conditionMap, false);
        
        //DecimalFormat dfMd = DecimalUtil.getMDStyle(supplierDao.get(supplierId).getMd());
        DecimalFormat df = DecimalUtil.getIntegerDecimalFormat((supplierDao.get(supplierId).getMd()));

        for (Map<String, Object> map : result) {
            if (page != null && limit != null) {
                rowNo = ((page-1) * limit) + idx;
            } else {
                rowNo = idx;
            }
            map.put("rowNo", df.format(rowNo));
            idx++;
            map.put("sendBytes", df.format(DecimalUtil.ConvertNumberToInteger(map.get("sendBytes"))) + " byte");
            map.put("rcvBytes", df.format(DecimalUtil.ConvertNumberToInteger(map.get("rcvBytes"))) + " byte");
            map.put("totalBytes", df.format(DecimalUtil.ConvertNumberToInteger(map.get("totalBytes"))) + " byte");
            map.put("totalCommTime", df.format(DecimalUtil.ConvertNumberToInteger(map.get("totalCommTime"))) + " ms");
        }

        return result;
    }

    /**
     * method name : getCommLogListTotalCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public Integer getCommLogListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = commLogDao.getMcuCommLogList(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

    /**
     * method name : getCommLogList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getCommLogData(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        DecimalFormat cdf = DecimalUtil.getDecimalFormat(new DecimalPattern("###,###,###,###","r"));
        result = commLogDao.getMcuCommLogData(conditionMap);

        for (Map<String, Object> map : result) {
            map.put("sendSum", cdf.format(DecimalUtil.ConvertNumberToInteger(map.get("sendSum"))));
            map.put("sendMax", cdf.format(DecimalUtil.ConvertNumberToInteger(map.get("sendMax"))));
            map.put("sendMin", cdf.format(DecimalUtil.ConvertNumberToInteger(map.get("sendMin"))));
            map.put("rcvSum", cdf.format(DecimalUtil.ConvertNumberToInteger(map.get("rcvSum"))));
            map.put("rcvMax", cdf.format(DecimalUtil.ConvertNumberToInteger(map.get("rcvMax"))));
            map.put("rcvMin", cdf.format(DecimalUtil.ConvertNumberToInteger(map.get("rcvMin"))));
        }

        return result;
    }

    /**
     * method name : getEventAlertLogList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEventAlertLogList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer page = (Integer) conditionMap.get("page");
        Integer limit = (Integer) conditionMap.get("limit");
        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        int idx = 1;
        Integer rowNo = 0;
        result = eventAlertLogDao.getMcuEventAlertLogList(conditionMap, false);
        
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

        for (Map<String, Object> map : result) {
            if (page != null && limit != null) {
                rowNo = ((page-1) * limit) + idx;
            } else {
                rowNo = idx;
            }
            map.put("rowNo", dfMd.format(rowNo));
            idx++;
            map.put("openTime", TimeLocaleUtil.getLocaleDate((String)map.get("openTime"), lang, country));
            map.put("closeTime", TimeLocaleUtil.getLocaleDate((String)map.get("closeTime"), lang, country));
            if(!"".equals((String)map.get("duration")) && (String)map.get("duration")!=null)
            	map.put("duration", dfMd.format(Double.parseDouble((String)map.get("duration"))));
            if(map.get("locationName")!=null)
            	map.put("locationName", locationDao.get(((Integer)map.get("locationName"))).getName());
        }

        return result;
    }

    /**
     * method name : getEventAlertLogListTotalCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public Integer getEventAlertLogListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = eventAlertLogDao.getMcuEventAlertLogList(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

    /**
     * method name : getOperationLogList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getOperationLogList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Integer page = (Integer) conditionMap.get("page");
        Integer limit = (Integer) conditionMap.get("limit");
        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        int idx = 1;
        Integer rowNo = 0;
        Integer operatorType = null;
        result = operationLogDao.getMcuOperationLogList(conditionMap, false);
        
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

        for (Map<String, Object> map : result) {
            if (page != null && limit != null) {
                rowNo = ((page-1) * limit) + idx;
            } else {
                rowNo = idx;
            }
            map.put("rowNo", dfMd.format(rowNo));
            idx++;
            map.put("yyyymmddhhmmss", TimeLocaleUtil.getLocaleDate((String)map.get("yyyymmddhhmmss"), lang, country));

            if (map.get("operatorType") != null) {
                operatorType = DecimalUtil.ConvertNumberToInteger(map.get("operatorType"));

                if (operatorType.equals(OperatorType.SYSTEM.getCode())) {
                    map.put("operatorType", OperatorType.SYSTEM.name().substring(0, 1) + OperatorType.SYSTEM.name().substring(1).toLowerCase());
                } else if (operatorType.equals(OperatorType.OPERATOR.getCode())) {
                    map.put("operatorType", OperatorType.OPERATOR.name().substring(0, 1) + OperatorType.OPERATOR.name().substring(1).toLowerCase());
                } else {
                    map.put("operatorType", "");
                }
            }
        }

        return result;
    }

    /**
     * method name : getOperationLogListTotalCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public Integer getOperationLogListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = operationLogDao.getMcuOperationLogList(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

    /**
     * method name : getMCUMiniChart<b/>
     * method Desc : 집중기관리 미니가젯에서 통신상태/집중기타입 정보를 조회한다.
     *
     * @param condition
     * @return
     */
    public List<List<Map<String, Object>>> getMCUMiniChart(Map<String, Object> condition) {
        String mcuChart = condition.get("mcuChart").toString();

        String message = StringUtil.nullToBlank(condition.get("message"));

        String chartType = StringUtil.nullToBlank(condition.get("chartType"));

        String[] arrMessage = null;

        if (!message.isEmpty()) {
            arrMessage = message.split(",");
        }

        List<List<Map<String, Object>>> result = new ArrayList<List<Map<String, Object>>>();

        // modemType / commStatus
        if (mcuChart.equals("mc")) {
            result = mcuDao.getMiniChartMCUTypeByCommStatus(condition);
        }
        // commStatus / modemType
        else if (mcuChart.equals("cm")) {
            if (chartType.equals("grid")) {
                result = mcuDao.getMiniChartCommStatusByMCUType(condition, arrMessage);
            } else {
                result = mcuDao.getMiniChartCommStatusByMCUType(condition);
            }
        }

        return result;
    }

    @Override
    public int setLocation(String mcuId, String address, double x, double y,
            double z) {
        try {
            MCU mcu = mcuDao.get(mcuId);
            if (mcu == null)
                return 1;
            
            if (address != null && !"".equals(address))
                mcu.setLocDetail(address);
            
            mcu.setGpioX(x);
            mcu.setGpioY(y);
            mcu.setGpioZ(z);
            
            mcuDao.update(mcu);
            
            return 0;
            
        }
        catch (Exception e) {
            log.error(e, e);
            return 2;
        }
    }
    
    public List<String> getFirmwareVersionList(Map<String, Object> condition){
    	List<String> versionList = mcuDao.getFirmwareVersionList(condition);
    	return versionList;
    }
    
    public List<String> getDeviceList(Map<String, Object> condition){
    	List<String> deviceList = mcuDao.getDeviceList(condition);
    	return deviceList;
    }
    
    public List<String> getTargetList(Map<String, Object> condition){
    	List<String> deviceList = mcuDao.getTargetList(condition);
    	return deviceList;
    }

	@Override
	public String getTitleName(String excel, String ext) {
		
		StringBuffer sb = new StringBuffer();
		
		try {
			File file = new File(excel.trim());
			Row titles = null;

			if ("xls".equals(ext)) {
				HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
				titles = (wb.getSheetAt(0)).getRow(2);
			} else if ("xlsx".equals(ext)) {
				XSSFWorkbook wb = new XSSFWorkbook(excel.trim()); 
				titles = (wb.getSheetAt(0)).getRow(2);
			}

			for (Cell cell : titles) {
				if (cell.getColumnIndex() > 0 && cell.getColumnIndex() < 5)
					sb.append(',');

				sb.append(cell.getRichStringCellValue().getString());
				
				if(cell.getColumnIndex()==4) {
					break;
				}
			}

		} catch (IOException ie) {
			log.error(ie,ie);
		} catch (Exception e) {
			log.error(e,e);
		}

		return sb.toString();
	}

	@Override
	public Map<String, Object> readOnlyExcelXLS(String excel, int supplierId) {
		Map<String, Object> result = new HashMap<String, Object>();
		Supplier supplier = supplierDao.getSupplierById(supplierId);
		
		try {
			ctxRoot = excel.substring(0, excel.lastIndexOf("/"));

			File file = new File(excel.trim());

			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));

			ExcelExtractor extractor = new ExcelExtractor(wb);
			extractor.setFormulasNotResults(true);
			extractor.setIncludeSheetNames(false);

			HSSFSheet sheet = wb.getSheetAt(0);				//첫번째 시트를 읽어옴
			int rowCnt = sheet.getPhysicalNumberOfRows();	//유효한 행의 갯수
			
			List<Object> resultList = new ArrayList<Object>();
			Row titles = null;
			Cell cell = null;
			Row row = null;
			int cnt = 0;
			
			String excelType = "xls";
			
			for (int rowIndex = 0; rowIndex<rowCnt; rowIndex++) {
				Map<String, Object> returnData = new HashMap<String, Object>();
				row = sheet.getRow(rowIndex);
				
				if((row!=null)) {
					
					if(row.getCell(0)==null || row.getCell(0).equals("")) {
						continue;
					}
					
					cell= row.getCell(0);
						
					if(cell.getRowIndex()==1 ||cell.getRowIndex()==0) {
						continue;
					}else if(cell.getRowIndex()==2) {
						titles = cell.getRow();
					}else {
						returnData = getFileMapRead(titles,row,excelType);
						if(returnData.get("Status").equals("Success")) {
							resultList.add(returnData);
							cnt++;
						}
					}
					
				}else{
					continue;
				}

			}
			result.put("resultList", resultList);
			
			List<Object> headerList = new ArrayList<Object>();
			headerList.add("Status");
			
			for (int i = 0; i < 5; i++) {
				headerList.add(titles.getCell(i).toString().trim());
			}
			
			result.put("headerList", headerList);
			result.put("index", cnt);

		} catch (IOException ie) {
			log.error(ie,ie);
		} catch (Exception e) {
			log.error(e,e);
		}
		return result;
	}

	@SuppressWarnings({ "unlikely-arg-type", "null" })
	@Override
	public Map<String, Object> readOnlyExcelXLSX(String excel, int supplierId) {
		Map<String, Object> result = new HashMap<String, Object>();
		Supplier supplier = supplierDao.getSupplierById(supplierId);
		
		try {
			ctxRoot = excel.substring(0, excel.lastIndexOf("/"));
	
			File file = new File(excel.trim());
			if (!file.exists() || !file.isFile() || !file.canRead()) {
				throw new IOException(excel.trim());
			}

			XSSFWorkbook wb = new XSSFWorkbook(excel.trim());

			XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);
			extractor.setFormulasNotResults(true);
			extractor.setIncludeSheetNames(true);
			
			XSSFSheet sheet = wb.getSheetAt(0);				//첫번째 시트를 읽어옴
			
			int rowCnt = sheet.getPhysicalNumberOfRows();	//유효한 행의 갯수
			
			List<Object> resultList = new ArrayList<Object>();
			Row titles = null;
			Cell cell = null;
			Row row = null;
			int cnt = 0;
			
			String excelType = "xlsx";
			
			for (int rowIndex = 0; rowIndex<rowCnt; rowIndex++) {
				Map<String, Object> returnData = new HashMap<String, Object>();
				row = sheet.getRow(rowIndex);
				
				if((row!=null)) {
					
					if(row.getCell(0)==null || row.getCell(0).equals("")) {
						continue;
					}
					
					cell= row.getCell(0);
						
					if(cell.getRowIndex()==1 ||cell.getRowIndex()==0) {
						continue;
					}else if(cell.getRowIndex()==2) {
						titles = cell.getRow();
					}else {
						returnData = getFileMapRead(titles,row,excelType);
						if(returnData.get("Status").equals("Success")) {
							resultList.add(returnData);
							cnt++;
						}
						
					}
				}else{
					continue;
				}
			}
			result.put("resultList", resultList);
			
			List<Object> headerList = new ArrayList<Object>();
			headerList.add("Status");
			
			for (int i = 0; i < 5; i++) {
				headerList.add(titles.getCell(i).toString().trim());
			}
			result.put("headerList", headerList);
			result.put("index", cnt);
			
		} catch (IOException ie) {
			log.error(ie, ie);
		} catch (Exception e) {
			log.error(e, e);
		}
		return result;
	}
    
	@SuppressWarnings("unlikely-arg-type")
	private Map<String, Object> getFileMapRead(Row titles, Row row, String excelType) throws IOException {
		String[] cellName = new String[5];
		if(excelType.equals("xlsx")) {
			XSSFRow fileRow = (XSSFRow) row;
			XSSFCell fileCell = fileRow.getCell(0);
		}else{
			HSSFRow fileRow = (HSSFRow) row;
			HSSFCell fileCell = fileRow.getCell(0);
		}
		
		Map<String, Object> returnData = new HashMap<String, Object>();
		
		String colName = null;
		String colValue = null;
		String tmpStatus = "Success"; // Status (Success or Failure)
		String status = "Success"; // Status (Success or Failure)
		
		for (Cell cell : row) {
			if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
				break;
			}
			colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();
			
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				colValue = cell.getRichStringCellValue().getString().trim();
				break;

			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					colValue = cell.getDateCellValue().toString().trim();
				} else {
					Long roundVal = Math.round(cell.getNumericCellValue());
					Double doubleVal = cell.getNumericCellValue();
					if (doubleVal.equals(roundVal.doubleValue())) {
						colValue = String.valueOf(roundVal).trim();
					} else {
						colValue = String.valueOf(doubleVal).trim();
					}
				}
				break;

			case Cell.CELL_TYPE_BOOLEAN:
				colValue = String.valueOf(cell.getBooleanCellValue()).trim();
				break;

			case Cell.CELL_TYPE_FORMULA:
				colValue = cell.getCellFormula().trim();
				break;

			default:
				colValue = "";
				break;
			}

			colValue = colValue.replaceAll("　", "").trim();
			try {
				if(colName.equals("No")) {
					if (colValue == null || colValue.length() == 0 || colValue.equals("")) {
						tmpStatus = "Failure";
					}
					if (tmpStatus.equals("Failure"))
						status = "Failure";
				} else if(colName.equals("Name")) {
					if (colValue == null || colValue.length() == 0 || colValue.equals("")) {
						tmpStatus = "Failure";
					}
					if (tmpStatus.equals("Failure"))
						status = "Failure";
				} else if(colName.equals("Condition")) {
					if (colValue == null || colValue.length() == 0 || colValue.equals("")) {
						tmpStatus = "Failure";
					}
					if (tmpStatus.equals("Failure"))
						status = "Failure";
				} else if(colName.equals("Task")) {
					if (colValue == null || colValue.length() == 0 || colValue.equals("")) {
						tmpStatus = "Failure";
					}
					if (tmpStatus.equals("Failure"))
						status = "Failure";
					
				} else if(colName.equals("Suspend")) {
					if (colValue == null || colValue.length() == 0 || colValue.equals("")) {
						tmpStatus = "Failure";
					}
					if (tmpStatus.equals("Failure"))
						status = "Failure";
				}else {
					break;
				}
				
			}catch (Exception e) {
				status = "Failure";
			}
			returnData.put(colName, colValue);
		}
		
		returnData.put("Status", status);
		tmpStatus = "";
		return returnData;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getMcuSearchedList(Map<String, Object> conditionMap) {
		Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer locationId = (Integer)conditionMap.get("locationId");
		Supplier supplier = supplierDao.get(supplierId);

		if (locationId != null) {
			List<Integer> locations = locationDao.getLeafLocationId(locationId, supplierId);
			conditionMap.put("locationIdList", locations);
		} /*else {
			// User 계정이 Admin이고, DSO 정보 입력란에 아무것도 입력하지 않고 검색한 경우 - default값으로 location table의 첫번째 인덱스 데이터를 반환
			List<Location> locationRoot = locationDao.getRootLocationList();
			List<Integer> locations = locationDao.getLeafLocationId(locationRoot.get(0).getId(), supplierId);
			conditionMap.put("locationIdList", locations);
		}*/
		
        Code deleteCode = codeDao.getCodeIdByCodeObject(McuStatus.Delete.getCode());
        conditionMap.put("deleteCode", deleteCode);
        Code NormalCode = codeDao.getCodeIdByCodeObject(McuStatus.Normal.getCode());
        conditionMap.put("normalCodeId", NormalCode);
        Code SecurityErrorCode = codeDao.getCodeIdByCodeObject(McuStatus.SecurityError.getCode());
        conditionMap.put("securityErrorCodeId", SecurityErrorCode);
        Code CommErrorCode = codeDao.getCodeIdByCodeObject(McuStatus.CommError.getCode());
        conditionMap.put("commErrorCodeId", CommErrorCode);
        Code PowerDownCode = codeDao.getCodeIdByCodeObject(McuStatus.PowerDown.getCode());
        conditionMap.put("powerDownCodeId", PowerDownCode);
        
		List<String> result = new ArrayList<String>();
		Map<String, Object> gridResult = mcuDao.getDcuGridData(conditionMap, false);
		List<MCU> list = (List<MCU>) gridResult.get("list");

		for (MCU mcu : list) {
			result.add(mcu.getSysID());
		}
		
		log.info("result =====>"+result);
		return result;
	}

    public List<String> getCodiFirmwareVersionList(Map<String, Object> condition){
    	List<String> versionList = mcuDao.getCodiFirmwareVersionList(condition);
    	return versionList;
    }
    
    public List<String> getCodiDeviceList(Map<String, Object> condition){
    	List<String> deviceList = mcuDao.getCodiDeviceList(condition);
    	return deviceList;
    }
    
    public List<String> getCodiTargetList(Map<String, Object> condition){
    	List<String> deviceList = mcuDao.getCodiTargetList(condition);
    	return deviceList;
    }
    
	@SuppressWarnings("unchecked")
    public List<Map<String, Object>> getCodiGridData(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer locationId = (Integer)conditionMap.get("locationId");
        Integer page = (Integer) conditionMap.get("page");
        Integer limit = (Integer) conditionMap.get("limit");
		Supplier supplier = supplierDao.get(supplierId);

		if (locationId != null) {
			List<Integer> locations = locationDao.getLeafLocationId(locationId, supplierId);
			conditionMap.put("locationIdList", locations);
		} /*else {
			// User 계정이 Admin이고, DSO 정보 입력란에 아무것도 입력하지 않고 검색한 경우 - default값으로 location table의 첫번째 인덱스 데이터를 반환
			List<Location> locationRoot = locationDao.getRootLocationList();
			List<Integer> locations = locationDao.getLeafLocationId(locationRoot.get(0).getId(), supplierId);
			conditionMap.put("locationIdList", locations);
		}*/
		
        Code deleteCode = codeDao.getCodeIdByCodeObject(McuStatus.Delete.getCode());
        conditionMap.put("deleteCode", deleteCode);
        Code NormalCode = codeDao.getCodeIdByCodeObject(McuStatus.Normal.getCode());
        conditionMap.put("normalCodeId", NormalCode);
        Code SecurityErrorCode = codeDao.getCodeIdByCodeObject(McuStatus.SecurityError.getCode());
        conditionMap.put("securityErrorCodeId", SecurityErrorCode);
        Code CommErrorCode = codeDao.getCodeIdByCodeObject(McuStatus.CommError.getCode());
        conditionMap.put("commErrorCodeId", CommErrorCode);
        Code PowerDownCode = codeDao.getCodeIdByCodeObject(McuStatus.PowerDown.getCode());
        conditionMap.put("powerDownCodeId", PowerDownCode);
        
		Map<String, Object> result = mcuDao.getCodiGridData(conditionMap, false);
		List<MCU> list = (List<MCU>)result.get("list");
    	List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
    	Map<String, Object> changeMap = null;
    	String lang = supplier.getLang().getCode_2letter();
    	String country = supplier.getCountry().getCode_2letter();
        String datePre24H = DateTimeUtil.calcDate(Calendar.HOUR, -24, "yyyyMMddHHmmss");
        String datePre48H = DateTimeUtil.calcDate(Calendar.HOUR, -48, "yyyyMMddHHmmss");
        int idx = 1;
        
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
        
        for (MCU mcu : list) {
            changeMap = new HashMap<String, Object>();

            if (page != null && limit != null) {
                changeMap.put("rowNo", dfMd.format((((page-1) * limit) + idx)));
                idx++;
            }

            changeMap.put("mcuId", StringUtil.nullToZero(mcu.getId()));
            changeMap.put("mcuSerial", StringUtil.nullToBlank(mcu.getSysSerialNumber())); 
            changeMap.put("codiFwVer", StringUtil.nullToZero(conditionMap.get("swVersion")));
            if (mcu.getMcuType() != null) {
                changeMap.put("dcuType", StringUtil.nullToBlank(mcu.getMcuType().getDescr()));
            } else {
                changeMap.put("dcuType", "");
            }
            changeMap.put("sysID", StringUtil.nullToBlank(mcu.getSysID()));
            changeMap.put("sysName", StringUtil.nullToBlank(mcu.getSysName()));

            if (mcu.getDeviceModel() != null) {
                changeMap.put("model", StringUtil.nullToBlank(mcu.getDeviceModel().getName()));

                if (mcu.getDeviceModel().getDeviceVendor() != null) {
                    changeMap.put("vendor", StringUtil.nullToBlank(mcu.getDeviceModel().getDeviceVendor().getName()));
                } else {
                    changeMap.put("vendor", "");
                }
            } else {
                changeMap.put("model", "");
                changeMap.put("vendor", "");
            }

            changeMap.put("sysPhoneNumber", StringUtil.nullToBlank(mcu.getSysPhoneNumber()));

            if (mcu.getLocation() != null) {
                changeMap.put("location", StringUtil.nullToBlank(mcu.getLocation().getName()));
            } else {
                changeMap.put("location", "");
            }
            changeMap.put("ipAddr", StringUtil.nullToBlank(mcu.getIpAddr()));
            changeMap.put("sysSwVersion", StringUtil.nullToBlank(mcu.getSysSwVersion()));
            changeMap.put("sysHwVersion", StringUtil.nullToBlank(mcu.getSysHwVersion()));
            changeMap.put("lastCommDateBasic", StringUtil.nullToBlank(mcu.getLastCommDate()));
            changeMap.put("installDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mcu.getInstallDate()), lang, country));

            if (mcu.getProtocolType() != null) {
                changeMap.put("protocolType", StringUtil.nullToBlank(mcu.getProtocolType().getDescr()));
            } else {
                changeMap.put("protocolType", "");
            }

            changeMap.put("lastCommDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mcu.getLastCommDate()), lang, country));
            if (StringUtil.nullToBlank(mcu.getLastCommDate()).isEmpty()) {
                changeMap.put("commState", "");
            } else if (mcu.getLastCommDate().compareTo(datePre24H) >= 0) {
                changeMap.put("commState", "fmtMessage00");
            } else if (mcu.getLastCommDate().compareTo(datePre24H) < 0 && mcu.getLastCommDate().compareTo(datePre48H) >= 0) {
                changeMap.put("commState", "fmtMessage24");
            } else if (mcu.getLastCommDate().compareTo(datePre48H) < 0) {
                changeMap.put("commState", "fmtMessage48");
			} else {
                changeMap.put("commState", "");}
            if(mcu.getMcuStatus()!= null){
				if ((mcu.getMcuStatusCodeId()).equals(SecurityErrorCode.getId())) {
					changeMap.put("commState", SecurityErrorCode.getCode());
				}
				if ((mcu.getMcuStatusCodeId()).equals(CommErrorCode.getId())) {
					changeMap.put("commState", CommErrorCode.getCode());
				}
				if (mcu.getMcuStatusCodeId().equals(PowerDownCode.getId())) {
					changeMap.put("commState", PowerDownCode.getCode());
				}
            }

            if (StringUtil.nullToBlank(mcu.getMcuType()).length() == 0) {
                changeMap.put("mcuTypeName", "");
            } else {
                changeMap.put("mcuTypeName", StringUtil.nullToBlank(mcu.getMcuType().getName()));
            }
            
            changeMap.put("ipv6Addr", StringUtil.nullToBlank(mcu.getIpv6Addr()));
            changeMap.put("amiNetworkAddress", StringUtil.nullToBlank(mcu.getAmiNetworkAddress()));
            changeMap.put("macAddr", StringUtil.nullToBlank(mcu.getMacAddr()));
            changeMap.put("swrev", StringUtil.nullToBlank(mcu.getSysSwRevision()));
            
            changeMap.put("sysLocation", StringUtil.nullToBlank(mcu.getSysLocation()));
            changeMap.put("locationId", StringUtil.nullToBlank(mcu.getLocationId()));
            changeMap.put("gs1", StringUtil.nullToBlank(mcu.getGs1()));
            changeMap.put("imei", StringUtil.nullToBlank(mcu.getImei()));
            changeMap.put("simNumber", StringUtil.nullToBlank(mcu.getSimNumber()));
            changeMap.put("iccId", StringUtil.nullToBlank(mcu.getIccId()));
            changeMap.put("manufacturedDate", StringUtil.nullToBlank(mcu.getManufacturedDate()));
            changeMap.put("po", StringUtil.nullToBlank(mcu.getPo()));
            changeMap.put("lot", "-");
            
            returnList.add(changeMap);
        }

    	return returnList;
    }
    
    public Integer getCodiGridDataTotalCount(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer locationId = (Integer)conditionMap.get("locationId");

        if (locationId != null) {
            List<Integer> locations = locationDao.getLeafLocationId(locationId, supplierId);
            conditionMap.put("locationIdList", locations);
        } /*else {
        	// User 계정이 Admin이고, DSO 정보 입력란에 아무것도 입력하지 않고 검색한 경우 - default값으로 location table의 첫번째 인덱스 데이터를 반환
        	List<Location> locationRoot = locationDao.getRootLocationList();
        	List<Integer> locations = locationDao.getLeafLocationId(locationRoot.get(0).getId(), supplierId);
        	conditionMap.put("locationIdList", locations);
        }*/

        Map<String, Object> result = mcuDao.getCodiGridData(conditionMap, true);
        Integer count = (Integer)result.get("count");

        return count;
    }
    
}