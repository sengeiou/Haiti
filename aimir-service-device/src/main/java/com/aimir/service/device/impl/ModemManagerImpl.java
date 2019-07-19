package com.aimir.service.device.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.McuStatus;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.RegType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.ACDDao;
import com.aimir.dao.device.ConverterDao;
import com.aimir.dao.device.HMUDao;
import com.aimir.dao.device.IEIUDao;
import com.aimir.dao.device.IHDDao;
import com.aimir.dao.device.LTEDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.PLCIUDao;
import com.aimir.dao.device.SubGigaDao;
import com.aimir.dao.device.ZBRepeaterDao;
import com.aimir.dao.device.ZEUMBusDao;
import com.aimir.dao.device.ZEUPLSDao;
import com.aimir.dao.device.ZMUDao;
import com.aimir.dao.device.ZRUDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.DeviceVendorDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.ACD;
import com.aimir.model.device.Converter;
import com.aimir.model.device.HMU;
import com.aimir.model.device.IEIU;
import com.aimir.model.device.IHD;
import com.aimir.model.device.LTE;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Modem;
import com.aimir.model.device.PLCIU;
import com.aimir.model.device.SubGiga;
import com.aimir.model.device.ZBRepeater;
import com.aimir.model.device.ZEUMBus;
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.device.ZMU;
import com.aimir.model.device.ZRU;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.DeviceRegistrationManager;
import com.aimir.service.device.ModemManager;
import com.aimir.util.CommonUtils2;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;

@WebService(endpointInterface = "com.aimir.service.device.ModemManager")
@Service(value = "modemManager")
@Transactional(readOnly = false)
public class ModemManagerImpl implements ModemManager {
    private static Log log = LogFactory.getLog(ModemManagerImpl.class);

	@Autowired
	ModemDao modemDao;
	
	@Autowired
	MeterDao meterDao;

	@Autowired
	LocationDao locDao;

	@Autowired
	MCUDao mcuDao;

	@Autowired
	ZRUDao zruDao;

	@Autowired
	ZEUPLSDao zeuplsDao;

	@Autowired
	MMIUDao mmiuDao;

	@Autowired
	IEIUDao ieiuDao;

	@Autowired
	ZMUDao zmuDao;

	@Autowired
	IHDDao ihdDao;

	@Autowired
	ACDDao acdDao;

	@Autowired
	HMUDao hmuDao;

	@Autowired
	PLCIUDao plciuDao;

	@Autowired
	ZEUMBusDao zeumbusDao;

	@Autowired
	ZBRepeaterDao zbrepeaterDao;

	@Autowired
	ConverterDao converterDao;
	
	@Autowired
	SubGigaDao subGigaDao;
	
	@Autowired
	LTEDao lteDao;

	@Autowired
	DeviceRegistrationManager deviceRegistrationManager;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	LocationDao locationDao;

	@Autowired
	DeviceVendorDao deviceVendorDao;

	@Autowired
	DeviceModelDao deviceModelDao;

	public Modem getModem(Integer modemId) {
		Modem resultModem = new Modem();

		resultModem = modemDao.get(modemId);

		return resultModem;
	}

	public Modem getModem(String deviceSerial) {
		return modemDao.findByCondition("deviceSerial", deviceSerial);
	}

	public int deleteModem(Integer modemId) {
		return modemDao.deleteById(modemId);
	}
	
	public int deleteModemStatus(int modemId, Code code) {
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("modemId", modemId);
		ArrayList meter = (ArrayList) meterDao.getMeterListByModem(condition).get(0);
		int returnData = 0;
		if(meter.size() > 0) {
			returnData = 0;
		} else {
			returnData = modemDao.deleteModemStatus(modemId, code);
		}
		return returnData;
	}

	public List<Converter> getConverterModem() {
		return converterDao.getAll();
	}

	public Object getModemByType(Map<String, Object> condition) throws Exception {

		Integer modemId = Integer.parseInt(condition.get("modemId").toString());
		String modemType = StringUtil.nullToBlank(condition.get("modemType"));

		Modem rtnModem = null;

		if (modemType.equals(ModemType.ZRU.toString()))
			rtnModem = zruDao.get(modemId);
		else if (modemType.equals(ModemType.ZEU_PLS.toString()))
			rtnModem = zeuplsDao.get(modemId);
		else if (modemType.equals(ModemType.MMIU.toString()))
			rtnModem = mmiuDao.get(modemId);
		else if (modemType.equals(ModemType.IEIU.toString()))
			rtnModem = ieiuDao.get(modemId);
		else if (modemType.equals(ModemType.ZMU.toString()))
			rtnModem = zmuDao.get(modemId);
		else if (modemType.equals(ModemType.IHD.toString()))
			rtnModem = ihdDao.get(modemId);
		else if (modemType.equals(ModemType.ACD.toString()))
			rtnModem = acdDao.get(modemId);
		else if (modemType.equals(ModemType.HMU.toString()))
			rtnModem = hmuDao.get(modemId);
		else if (modemType.equals(ModemType.PLC_G3.toString()))
			rtnModem = plciuDao.get(modemId);
		else if (modemType.equals(ModemType.PLC_PRIME.toString()))
			rtnModem = plciuDao.get(modemId);
		else if (modemType.equals(ModemType.PLC_HD.toString()))
			rtnModem = plciuDao.get(modemId);
		else if (modemType.equals(ModemType.PLCIU.toString()))
			rtnModem = plciuDao.get(modemId);
		else if (modemType.equals(ModemType.PLC_G3.toString()))
			rtnModem = plciuDao.get(modemId);
		else if (modemType.equals(ModemType.ZEU_MBus.toString()))
			rtnModem = zeumbusDao.get(modemId);
		else if (modemType.equals(ModemType.Repeater.toString()))
			rtnModem = zbrepeaterDao.get(modemId);
		else if (modemType.equals(ModemType.Converter_Ethernet.toString()))
			rtnModem = converterDao.get(modemId);
		else if (modemType.equals(ModemType.SubGiga.toString()))
			rtnModem = subGigaDao.get(modemId);
		else if (modemType.equals(ModemType.LTE.toString()))
			rtnModem = lteDao.get(modemId);
		
		return rtnModem;

	}

	public List<Object> getModemByType(Map<String, Object> condition,
			String supplierId) {

		Integer modemId = Integer.parseInt(condition.get("modemId").toString());
		String modemType = StringUtil.nullToBlank(condition.get("modemType"));

		Modem rtnModem = null;

		if (modemType.equals(ModemType.ZRU.toString()))
			rtnModem = zruDao.get(modemId);
		else if (modemType.equals(ModemType.ZEU_PLS.toString()))
			rtnModem = zeuplsDao.get(modemId);
		else if (modemType.equals(ModemType.MMIU.toString()))
			rtnModem = mmiuDao.get(modemId);
		else if (modemType.equals(ModemType.IEIU.toString()))
			rtnModem = ieiuDao.get(modemId);
		else if (modemType.equals(ModemType.ZMU.toString()))
			rtnModem = zmuDao.get(modemId);
		else if (modemType.equals(ModemType.IHD.toString()))
			rtnModem = ihdDao.get(modemId);
		else if (modemType.equals(ModemType.ACD.toString()))
			rtnModem = acdDao.get(modemId);
		else if (modemType.equals(ModemType.HMU.toString()))
			rtnModem = hmuDao.get(modemId);
		else if (modemType.equals(ModemType.PLC_G3.toString()))
			rtnModem = plciuDao.get(modemId);
		else if (modemType.equals(ModemType.PLC_PRIME.toString()))
			rtnModem = plciuDao.get(modemId);
		else if (modemType.equals(ModemType.PLC_HD.toString()))
			rtnModem = plciuDao.get(modemId);
		else if (modemType.equals(ModemType.PLCIU.toString()))
			rtnModem = plciuDao.get(modemId);
		else if (modemType.equals(ModemType.ZEU_MBus.toString()))
			rtnModem = zeumbusDao.get(modemId);
		else if (modemType.equals(ModemType.Repeater.toString()))
			rtnModem = zbrepeaterDao.get(modemId);
		else if (modemType.equals(ModemType.Converter_Ethernet.toString()))
			rtnModem = converterDao.get(modemId);
		else if (modemType.equals(ModemType.SubGiga.toString()))
			rtnModem = subGigaDao.get(modemId);
		else if (modemType.equals(ModemType.LTE.toString()))
			rtnModem = lteDao.get(modemId);	

		List<Object> result = new ArrayList<Object>();
		String installDate = "";
		String lastLinkTime = "";

		if (supplierId.length() > 0) {
			Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));

			installDate = TimeLocaleUtil
					.getLocaleDate(StringUtil.nullToBlank(rtnModem
							.getInstallDate()), supplier.getLang()
							.getCode_2letter(), supplier.getCountry()
							.getCode_2letter());

			if (rtnModem.getLastLinkTime() != null
					&& StringUtil.nullToBlank(rtnModem.getLastLinkTime()).length() > 0) {
				lastLinkTime = TimeLocaleUtil.getLocaleDate(StringUtil
						.nullToBlank(rtnModem.getLastLinkTime()), supplier
						.getLang().getCode_2letter(), supplier.getCountry()
						.getCode_2letter());
			}
		}

		result.add(rtnModem);
		result.add(installDate);
		result.add(lastLinkTime);

		return result;

	}

	public List<Object> getMiniChart(Map<String, Object> condition) {

		// modemType / loc / commStatus

		// ml / mc
		// lm / lc
		// cm / cl

		String modemChart = condition.get("modemChart").toString();

		List<Object> result = new ArrayList<Object>();

		// modemType / loc
		if (modemChart.equals("ml")) {
			result = modemDao.getMiniChartModemTypeByLocation(condition);
		}

		// modemType / commStatus
		if (modemChart.equals("mc")) {
			result = modemDao.getMiniChartModemTypeByCommStatus(condition);
		}

		// loc / modemType
		if (modemChart.equals("lm")) {
			result = modemDao.getMiniChartLocationByModemType(condition);
		}

		// loc / commStatus
		if (modemChart.equals("lc")) {
			result = modemDao.getMiniChartLocationByCommStatus(condition);
		}

		// commStatus / modemType
		if (modemChart.equals("cm")) {
			result = modemDao.getMiniChartCommStatusByModemType(condition);
		}

		// commStatus / loc
		if (modemChart.equals("cl")) {
			result = modemDao.getMiniChartCommStatusByLocation(condition);
		}

		return result;

	}

    public List<Object> getMiniChart2(Map<String, Object> condition) {
        String modemChart = condition.get("modemChart").toString();

        String fmtmessagecommalert = StringUtil.nullToBlank(condition.get("fmtmessagecommalert"));

        String chartType = StringUtil.nullToBlank(condition.get("chartType"));

        String[] arrFmtmessagecommalert = {};

        if (fmtmessagecommalert != "")
            arrFmtmessagecommalert = fmtmessagecommalert.split(",");

        List<Object> result = new ArrayList<Object>();

        // modemType / commStatus
        if (modemChart.equals("mc")) {
            result = modemDao.getMiniChartModemTypeByCommStatus(condition);
        }
        // commStatus / modemType
        else if (modemChart.equals("cm")) {
            if (chartType.equals("grid"))
                result = modemDao.getMiniChartCommStatusByModemType(condition, arrFmtmessagecommalert);
            else
                result = modemDao.getMiniChartCommStatusByModemType(condition);
        	
        }

        return result;
    }

	public List<Object> getModemSearchChart(Map<String, Object> condition) {

		// 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
		if (condition.get("sLocationId") != null
				&& !((String) condition.get("sLocationId")).trim().equals("")) {
			List<Integer> locations = locationDao.getLeafLocationId(Integer
					.parseInt((String) condition.get("sLocationId")), Integer
					.parseInt((String) condition.get("supplierId")));
			String sLocations = "";
			for (int i = 0; i < locations.size(); i++) {
				if (i == 0) {
					sLocations += locations.get(i);
				} else {
					sLocations += ", " + locations.get(i);
				}
			}

			condition.put("sLocationId", sLocations);
		}

		List<Object> result = new ArrayList<Object>();
		result = modemDao.getModemSearchChart(condition);
		return result;

	}
	public List<Object> getModemSearchGrid(Map<String, Object> condition) {
		// 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
		if (condition.get("sLocationId") != null
				&& !((String) condition.get("sLocationId")).trim().equals("")) {
			List<Integer> locations = locationDao.getLeafLocationId(Integer
					.parseInt((String) condition.get("sLocationId")), Integer
					.parseInt((String) condition.get("supplierId")));
			String sLocations = "";
			for (int i = 0; i < locations.size(); i++) {
				if (i == 0) {
					sLocations += locations.get(i);
				} else {
					sLocations += ", " + locations.get(i);
				}
			}

			condition.put("sLocationId", sLocations);
		}

		List<Object> result = new ArrayList<Object>();
		result = modemDao.getModemSearchGrid(condition);

		List<Object> gridList = (List<Object>) result.get(1);

		String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
		if (supplierId.length() > 0) {
			Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));

			for (Object data : gridList) {
				Map<String, Object> mapData = (Map<String, Object>) data;

				mapData.put("lastCommDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mapData.get("lastCommDate")), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
				mapData.put("installDate", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mapData.get("installDate")), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			}
		}

		return result;
	}
	
	public List<Object> getModemSearchGrid2(Map<String, Object> condition, String gridType) {

		// 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
		if (condition.get("sLocationId") != null
				&& !((String) condition.get("sLocationId")).trim().equals("")) {
			List<Integer> locations = locationDao.getLeafLocationId(Integer
					.parseInt((String) condition.get("sLocationId")), Integer
					.parseInt((String) condition.get("supplierId")));
			String sLocations = "";
			for (int i = 0; i < locations.size(); i++) {
				if (i == 0) {
					sLocations += locations.get(i);
				} else {
					sLocations += ", " + locations.get(i);
				}
			}

			condition.put("sLocationId", sLocations);
		}

		List<Object> result = new ArrayList<Object>();
		
		//페이징 0부터 처리
		if ( gridType.equals("extjs"))
			condition =CommonUtils2.getFirstPageForExtjsGrid3(condition);
		
		
		
		result = modemDao.getModemSearchGrid(condition);
		
		
		
		

		List<Object> gridList = (List<Object>) result.get(1);

		String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
		if (supplierId.length() > 0) {
			Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
			DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());
			
			int idx=1;
			for (Object data : gridList) 
			{
				Map<String, Object> mapData = (Map<String, Object>) data;

				mapData.put("lastCommDate", TimeLocaleUtil.getLocaleDate(
						StringUtil.nullToBlank(mapData.get("lastCommDate")),
						supplier.getLang().getCode_2letter(), supplier
								.getCountry().getCode_2letter()));
				mapData.put("installDate", TimeLocaleUtil.getLocaleDate(
						StringUtil.nullToBlank(mapData.get("installDate")),
						supplier.getLang().getCode_2letter(), supplier
								.getCountry().getCode_2letter()));
				
				
				//페이징 인덱스 처리
				if ( gridType.equals("extjs"))
				{
					String curPage = String.valueOf( condition.get("page"));
			        String pageSize =  String.valueOf( condition.get("pageSize"));
					
					
					mapData.put("idx", dfMd.format(CommonUtils2.makeIdxPerPage(curPage, pageSize, idx)));
				}
		        
		        
				
				idx++;
			}
		}

		return result;

	}
	
	
	@SuppressWarnings("unchecked")
	public List<Object> getModemSearchGrid2(Map<String, Object> condition) {

		// 공급지역 검색 조건 시 - 최하위 노드 값 조회 및 설정
		if (condition.get("sLocationId") != null
				&& !((String) condition.get("sLocationId")).trim().equals("")) {
			List<Integer> locations = locationDao.getLeafLocationId(Integer
					.parseInt((String) condition.get("sLocationId")), Integer
					.parseInt((String) condition.get("supplierId")));
			String sLocations = "";
			for (int i = 0; i < locations.size(); i++) {
				if (i == 0) {
					sLocations += locations.get(i);
				} else {
					sLocations += ", " + locations.get(i);
				}
			}

			condition.put("sLocationId", sLocations);
		}

		List<Object> result = new ArrayList<Object>();
		
		
		result = modemDao.getModemSearchGrid2(condition);
		
		
		
		
		

		List<Object> gridList = (List<Object>) result.get(1);

		String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
		if (supplierId.length() > 0) {
			Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));

			for (Object data : gridList) {
				Map<String, Object> mapData = (Map<String, Object>) data;

				mapData.put("lastCommDate", TimeLocaleUtil.getLocaleDate(
						StringUtil.nullToBlank(mapData.get("lastCommDate")),
						supplier.getLang().getCode_2letter(), supplier
								.getCountry().getCode_2letter()));
				mapData.put("installDate", TimeLocaleUtil.getLocaleDate(
						StringUtil.nullToBlank(mapData.get("installDate")),
						supplier.getLang().getCode_2letter(), supplier
								.getCountry().getCode_2letter()));
			}
		}

		return result;

	}
	
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Object> getModemLogChart(Map<String, Object> condition) {

		List<Object> result = new ArrayList<Object>();
		result = modemDao.getModemLogChart(condition);

		Supplier supplier = supplierDao.get(Integer.parseInt(String.valueOf(condition.get("supplierId"))));
		
		
		List<Object> dataList = (List<Object>) result.get(0);
		
		
		for (Object obj : dataList) 
		{
			HashMap chartDataMap = (HashMap) obj;
			String yyyyMMdd = String.valueOf(chartDataMap.get("xTag"));
			chartDataMap.put("xTag", TimeLocaleUtil.getLocaleDate(yyyyMMdd,
					supplier.getLang().getCode_2letter(), supplier.getCountry()
							.getCode_2letter()));
		}

		return result;

	}
/*  모뎀가젯의 history탭 : 구현이 안되어 있는 부분이고 필요없는 기능이라 삭제
	public List<Object> getModemLogGrid(Map<String, Object> condition) {

		List<Object> result = new ArrayList<Object>();
		String logType = StringUtil.nullToBlank(condition.get("logType"));

		if (logType.equals("commLog") || logType.equals(""))
		{
			result = modemDao.getModemCommLog(condition);
			
			
		} else if (logType.equals("updateLog"))
		{
			// 없음
			result = modemDao.getModemLogGrid(condition);
		} else if (logType.equals("brokenLog"))
		{
			// 없음
			result = modemDao.getModemLogGrid(condition);
		} else if (logType.equals("operationLog"))
		{
			result = modemDao.getModemOperationLog(condition);
		}

		return result;

	}
*/
	public Map<String, Object> getModemSearchCondition() {
		Map<String, Object> result = new HashMap<String, Object>();
		result = modemDao.getModemSearchCondition();
		return result;
	}

	public List<Object> getModemSerialList(Map<String, Object> condition) {
		List<Object> result = new ArrayList<Object>();

		result = modemDao.getModemSerialList(condition);

		return result;

	}

	// Modem등록
	public Map<String, Object> insertModem(Modem modem) {
		Map<String, Object> result = new HashMap<String, Object>();

		modemDao.add(modem);
		result.put("id", modemDao.get(modemDao.get(modem.getDeviceSerial())
				.getId()));

		return result;
	}

	// ZRU 등록
	public Map<String, Object> insertModemZRU(ZRU zru) {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultStatus insertResult = ResultStatus.SUCCESS;

		try {
			if (zru.getMcu() == null || zru.getMcu().getSysID() == null) {
				zru.setMcu(null);
			} else {
				MCU mcu = mcuDao.get(zru.getMcu().getSysID());
				if(mcu == null || (mcu.getMcuStatus() != null && McuStatus.Delete.getCode().equals(mcu.getMcuStatus().getCode()))) {
					zru.setMcu(null);
				} else {
					zru.setMcu(mcu);
				}
			}

			zru.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));

			zruDao.add(zru);

			result.put("id", zruDao.get(
					modemDao.get(zru.getDeviceSerial()).getId()).getId());
		} catch (Exception e) {
			insertResult = ResultStatus.FAIL;
		} finally {
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", TargetClass.ZRU);
			logData.put("deviceName", zru.getDeviceSerial());
			logData.put("deviceModel", zru.getModel());
			logData.put("resultStatus", insertResult);
			logData.put("regType", RegType.Manual);
			logData.put("supplier", zru.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}
		return result;
	}

	// PLC 등록
	public Map<String, Object> insertModemPLCIU(PLCIU plciu) {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultStatus insertResult = ResultStatus.SUCCESS;

		try {
			if (plciu.getMcu() == null || plciu.getMcu().getSysID() == null) {
				plciu.setMcu(null);
			} else {
				MCU mcu = mcuDao.get(plciu.getMcu().getSysID());
				if(mcu == null || (mcu.getMcuStatus() != null && McuStatus.Delete.getCode().equals(mcu.getMcuStatus().getCode()))) {
					plciu.setMcu(null);
				} else {
					plciu.setMcu(mcu);
				}
			}

			plciu.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));

			plciuDao.add(plciu);

			result.put("id", plciuDao.get(
					modemDao.get(plciu.getDeviceSerial()).getId()).getId());
		} catch (Exception e) {
			insertResult = ResultStatus.FAIL;
		} finally {
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", TargetClass.PLC);
			logData.put("deviceName", plciu.getDeviceSerial());
			logData.put("deviceModel", plciu.getModel());
			logData.put("resultStatus", insertResult);
			logData.put("regType", RegType.Manual);
			logData.put("supplier", plciu.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}
		return result;
	}

	// ZEPUS 등록
	public Map<String, Object> insertModemZEUPLS(ZEUPLS zeupls) {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultStatus insertResult = ResultStatus.SUCCESS;

		try {
			if (zeupls.getMcu() == null || zeupls.getMcu().getSysID() == null) {
				zeupls.setMcu(null);
			} else {
				MCU mcu = mcuDao.get(zeupls.getMcu().getSysID());
				if(mcu == null || (mcu.getMcuStatus() != null && McuStatus.Delete.getCode().equals(mcu.getMcuStatus().getCode()))) {
					zeupls.setMcu(null);
				} else {
					zeupls.setMcu(mcu);
				}
			}

			zeupls.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));

			zeuplsDao.add(zeupls);
			result.put("id", zeuplsDao.get(
					modemDao.get(zeupls.getDeviceSerial()).getId()).getId());
		} catch (Exception e) {
			insertResult = ResultStatus.FAIL;
		} finally {
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", TargetClass.ZEUPLS);
			logData.put("deviceName", zeupls.getDeviceSerial());
			logData.put("deviceModel", zeupls.getModel());
			logData.put("resultStatus", insertResult);
			logData.put("regType", RegType.Manual);
			logData.put("supplier", zeupls.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}
		return result;
	}

	// MMIU 등록
	public Map<String, Object> insertModemMMIU(MMIU mmiu) {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultStatus insertResult = ResultStatus.SUCCESS;

		try {
			if (mmiu.getMcu() == null || mmiu.getMcu().getSysID() == null) {
				mmiu.setMcu(null);
			} else {
				MCU mcu = mcuDao.get(mmiu.getMcu().getSysID());
				if(mcu == null || (mcu.getMcuStatus() != null && McuStatus.Delete.getCode().equals(mcu.getMcuStatus().getCode()))) {
					mmiu.setMcu(null);
				} else {
					mmiu.setMcu(mcu);
				}
			}

			mmiu.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));

			mmiuDao.add(mmiu);
			result.put("id", mmiuDao.get(
					modemDao.get(mmiu.getDeviceSerial()).getId()).getId());
		} catch (Exception e) {
			insertResult = ResultStatus.FAIL;
		} finally {
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", TargetClass.MMIU);
			logData.put("deviceName", mmiu.getDeviceSerial());
			logData.put("deviceModel", mmiu.getModel());
			logData.put("resultStatus", insertResult);
			logData.put("regType", RegType.Manual);
			logData.put("supplier", mmiu.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}

		return result;
	}

	// IEIU 등록
	public Map<String, Object> insertModemIEIU(IEIU ieiu) {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultStatus insertResult = ResultStatus.SUCCESS;
		try {
			if (ieiu.getMcu() == null || ieiu.getMcu().getSysID() == null) {
				ieiu.setMcu(null);
			} else {
				MCU mcu = mcuDao.get(ieiu.getMcu().getSysID());
				if(mcu == null || (mcu.getMcuStatus() != null && McuStatus.Delete.getCode().equals(mcu.getMcuStatus().getCode()))) {
					ieiu.setMcu(null);
				} else {
					ieiu.setMcu(mcu);
				}
			}

			ieiu.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));

			ieiuDao.add(ieiu);
			result.put("id", ieiu.getId());
		} catch (Exception e) {
			insertResult = ResultStatus.FAIL;
		} finally {
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", TargetClass.IEIU);
			logData.put("deviceName", ieiu.getDeviceSerial());
			logData.put("deviceModel", ieiu.getModel());
			logData.put("resultStatus", insertResult);
			logData.put("regType", RegType.Manual);
			logData.put("supplier", ieiu.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}

		return result;
	}

	// ZMU 등록
	public Map<String, Object> insertModemZMU(ZMU zmu) {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultStatus insertResult = ResultStatus.SUCCESS;

		try {
			if (zmu.getMcu() == null || zmu.getMcu().getSysID() == null) {
				zmu.setMcu(null);
			} else {
				MCU mcu = mcuDao.get(zmu.getMcu().getSysID());
				if(mcu == null || (mcu.getMcuStatus() != null && McuStatus.Delete.getCode().equals(mcu.getMcuStatus().getCode()))) {
					zmu.setMcu(null);
				} else {
					zmu.setMcu(mcu);
				}
			}

			zmu.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));

			zmuDao.add(zmu);
			result.put("id", zmuDao.get(
					modemDao.get(zmu.getDeviceSerial()).getId()).getId());
		} catch (Exception e) {
			insertResult = ResultStatus.FAIL;
		} finally {
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", TargetClass.ZMU);
			logData.put("deviceName", zmu.getDeviceSerial());
			logData.put("deviceModel", zmu.getModel());
			logData.put("resultStatus", insertResult);
			logData.put("regType", RegType.Manual);
			logData.put("supplier", zmu.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}

		return result;
	}

	// IHD 등록
	public Map<String, Object> insertModemIHD(IHD ihd) {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultStatus insertResult = ResultStatus.SUCCESS;

		try {
			if (ihd.getMcu() == null || ihd.getMcu().getSysID() == null) {
				ihd.setMcu(null);
			} else {
				MCU mcu = mcuDao.get(ihd.getMcu().getSysID());
				if(mcu == null || (mcu.getMcuStatus() != null && McuStatus.Delete.getCode().equals(mcu.getMcuStatus().getCode()))) {
					ihd.setMcu(null);
				} else {
					ihd.setMcu(mcu);
				}
			}

			ihd.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));

			ihdDao.add(ihd);
			result.put("id", ihdDao.get(
					modemDao.get(ihd.getDeviceSerial()).getId()).getId());
		} catch (Exception e) {
			insertResult = ResultStatus.FAIL;
		} finally {
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", TargetClass.IHD);
			logData.put("deviceName", ihd.getDeviceSerial());
			logData.put("deviceModel", ihd.getModel());
			logData.put("resultStatus", insertResult);
			logData.put("regType", RegType.Manual);
			// logData.put("supplier", ihd.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}

		return result;
	};

	// ACD 등록
	public Map<String, Object> insertModemACD(ACD acd) {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultStatus insertResult = ResultStatus.SUCCESS;

		try {
			if (acd.getMcu() == null || acd.getMcu().getSysID() == null) {
				acd.setMcu(null);
			} else {
				MCU mcu = mcuDao.get(acd.getMcu().getSysID());
				if(mcu == null || (mcu.getMcuStatus() != null && McuStatus.Delete.getCode().equals(mcu.getMcuStatus().getCode()))) {
					acd.setMcu(null);
				} else {
					acd.setMcu(mcu);
				}
			}

			acd.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));

			acdDao.add(acd);
			result.put("id", acdDao.get(
					modemDao.get(acd.getDeviceSerial()).getId()).getId());
		} catch (Exception e) {
			insertResult = ResultStatus.FAIL;
		} finally {
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", TargetClass.ACD);
			logData.put("deviceName", acd.getDeviceSerial());
			logData.put("deviceModel", acd.getModel());
			logData.put("resultStatus", insertResult);
			logData.put("regType", RegType.Manual);
			logData.put("supplier", acd.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}

		return result;
	}

	// hmu등록
	public Map<String, Object> insertModemHMU(HMU hmu) {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultStatus insertResult = ResultStatus.SUCCESS;

		try {
			if (hmu.getMcu() == null || hmu.getMcu().getSysID() == null) {
				hmu.setMcu(null);
			} else {
				MCU mcu = mcuDao.get(hmu.getMcu().getSysID());
				if(mcu == null || (mcu.getMcuStatus() != null && McuStatus.Delete.getCode().equals(mcu.getMcuStatus().getCode()))) {
					hmu.setMcu(null);
				} else {
					hmu.setMcu(mcu);
				}
			}

			hmu.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));

			hmuDao.add(hmu);
			result.put("id", hmuDao.get(
					modemDao.get(hmu.getDeviceSerial()).getId()).getId());
		} catch (Exception e) {
			insertResult = ResultStatus.FAIL;
		} finally {
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", TargetClass.HMU);
			logData.put("deviceName", hmu.getDeviceSerial());
			logData.put("resultStatus", insertResult);
			logData.put("deviceModel", hmu.getModel());
			logData.put("regType", RegType.Manual);
			logData.put("supplier", hmu.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}
		return result;
	}

	// ZEUMBus 등록
	public Map<String, Object> insertModemZEUMBus(ZEUMBus zeumBus) {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultStatus insertResult = ResultStatus.SUCCESS;

		try {
			if (zeumBus.getMcu() == null || zeumBus.getMcu().getSysID() == null) {
				zeumBus.setMcu(null);
			} else {
				MCU mcu = mcuDao.get(zeumBus.getMcu().getSysID());
				if(mcu == null || (mcu.getMcuStatus() != null && McuStatus.Delete.getCode().equals(mcu.getMcuStatus().getCode()))) {
					zeumBus.setMcu(null);
				} else {
					zeumBus.setMcu(mcu);
				}
			}

			zeumBus.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));

			zeumbusDao.add(zeumBus);
			result.put("id", zeumbusDao.get(
					zeumbusDao.get(zeumBus.getDeviceSerial()).getId()).getId());
		} catch (Exception e) {
			insertResult = ResultStatus.FAIL;
		} finally {
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", TargetClass.ZEUMBus);
			logData.put("deviceName", zeumBus.getDeviceSerial());
			logData.put("resultStatus", insertResult);
			logData.put("deviceModel", zeumBus.getModel());
			logData.put("regType", RegType.Manual);
			logData.put("supplier", zeumBus.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}
		return result;
	}

	// ZBRepeater 등록
	public Map<String, Object> insertModemZBRepeater(ZBRepeater zbRepeater) {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultStatus insertResult = ResultStatus.SUCCESS;

		try {
			if (zbRepeater.getMcu() == null || zbRepeater.getMcu().getSysID() == null) {
				zbRepeater.setMcu(null);
			} else {
				MCU mcu = mcuDao.get(zbRepeater.getMcu().getSysID());
				if(mcu == null || (mcu.getMcuStatus() != null && McuStatus.Delete.getCode().equals(mcu.getMcuStatus().getCode()))) {
					zbRepeater.setMcu(null);
				} else {
					zbRepeater.setMcu(mcu);
				}
			}

			zbRepeater.setInstallDate(DateTimeUtil
					.getCurrentDateTimeByFormat(""));

			zbrepeaterDao.add(zbRepeater);
			result.put("id", zbrepeaterDao.get(
					zbrepeaterDao.get(zbRepeater.getDeviceSerial()).getId())
					.getId());
		} catch (Exception e) {
			insertResult = ResultStatus.FAIL;
		} finally {
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", TargetClass.ZBRepeater);
			logData.put("deviceName", zbRepeater.getDeviceSerial());
			logData.put("resultStatus", insertResult);
			logData.put("deviceModel", zbRepeater.getModel());
			logData.put("regType", RegType.Manual);
			logData.put("supplier", zbRepeater.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}
		return result;
	}

	// Converter 등록
	public Map<String, Object> insertModemConverter(Converter converter) {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultStatus insertResult = ResultStatus.SUCCESS;

		try {
			/*
			 * if(converter.getMcu().getSysID() == null) converter.setMcu(null);
			 * else
			 * converter.setMcu(mcuDao.get(zbRepeater.getMcu().getSysID()));
			 */// 집중기 설정하지 않음 집중기와 연결 없음
			converter.setMcu(null);
			converter.setInstallDate(DateTimeUtil
					.getCurrentDateTimeByFormat(""));

			converterDao.add(converter);
			result.put("id", converterDao.get(
					converterDao.get(converter.getDeviceSerial()).getId())
					.getId());
		} catch (Exception e) {
			insertResult = ResultStatus.FAIL;
		} finally {
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", TargetClass.Converter);
			logData.put("deviceName", converter.getDeviceSerial());
			logData.put("resultStatus", insertResult);
			logData.put("deviceModel", converter.getModel());
			logData.put("regType", RegType.Manual);
			logData.put("supplier", converter.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}
		return result;
	}
	
	// Converter 등록
	public Map<String, Object> insertModemSubGiga(SubGiga subGiga) {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultStatus insertResult = ResultStatus.SUCCESS;

		try {
			if (subGiga.getMcu() == null || subGiga.getMcu().getSysID() == null) {
				subGiga.setMcu(null);
			} else {
				MCU mcu = mcuDao.get(subGiga.getMcu().getSysID());
				if(mcu == null || (mcu.getMcuStatus() != null && McuStatus.Delete.getCode().equals(mcu.getMcuStatus().getCode()))) {
					subGiga.setMcu(null);
				} else {
					subGiga.setMcu(mcu);
				}
			}

			subGiga.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));

			subGigaDao.add(subGiga);
			result.put("id", subGigaDao.get(
					subGigaDao.get(subGiga.getDeviceSerial()).getId())
					.getId());
		} catch (Exception e) {
			insertResult = ResultStatus.FAIL;
		} finally {
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", TargetClass.SubGiga);
			logData.put("deviceName", subGiga.getDeviceSerial());
			logData.put("resultStatus", insertResult);
			logData.put("deviceModel", subGiga.getModel());
			logData.put("regType", RegType.Manual);
			logData.put("supplier", subGiga.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}
		return result;
	}

	public Map<String, Object> insertModemLTE(LTE lte) {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultStatus insertResult = ResultStatus.SUCCESS;

		try {
			if (lte.getMcu() == null || lte.getMcu().getSysID() == null) {
				lte.setMcu(null);
			} else {
				MCU mcu = mcuDao.get(lte.getMcu().getSysID());
				if(mcu.getMcuStatus() != null && McuStatus.Delete.getCode().equals(mcu.getMcuStatus().getCode())) {
					lte.setMcu(null);
				} else {
					lte.setMcu(mcu);
				}
			}

			lte.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(""));

			lteDao.add(lte);
			result.put("id", lteDao.get(lteDao.get(lte.getDeviceSerial()).getId()).getId());
		} catch (Exception e) {
			insertResult = ResultStatus.FAIL;
		} finally {
			Map<String, Object> logData = new HashMap<String, Object>();
			logData.put("deviceType", TargetClass.LTE);
			logData.put("deviceName", lte.getDeviceSerial());
			logData.put("resultStatus", insertResult);
			logData.put("deviceModel", lte.getModel());
			logData.put("regType", RegType.Manual);
			logData.put("supplier", lte.getSupplier());
			deviceRegistrationManager.insertDeviceRegLog(logData);
		}
		return result;
	}	
	// UPDATE ---------------------------------------------------------

	// MeterUpdate
	public Map<String, Object> updateModem(Modem modem) {
	    Map<String, Object> result = new HashMap<String, Object>();

        Modem oriModem = modemDao.get(modem.getDeviceSerial());
        
        // RF 단말과 좌표 연동하기 위해 추가됨. 2016.05.18
        if (oriModem != null && modem.getId() == null) {
            log.info("ModemID[" + modem.getDeviceSerial() + 
                    "] X[" + modem.getGpioX() + 
                    "] Y[" + modem.getGpioY() + 
                    "] Z[" + modem.getGpioZ() + "]");
            
            if (modem.getGpioX() != null) oriModem.setGpioX(modem.getGpioX());
            if (modem.getGpioY() != null) oriModem.setGpioY(modem.getGpioY());
            if (modem.getGpioZ() != null) oriModem.setGpioZ(modem.getGpioZ());
            
            modemDao.update(oriModem);
            
            result.put("id", modem.getId());

            return result;
        }

        // Update내역
        oriModem.setMcu(modem.getMcu());
        oriModem.setHwVer(modem.getHwVer());
        oriModem.setSwVer(modem.getSwVer());
        oriModem.setProtocolVersion(modem.getProtocolVersion());
        oriModem.setInstallDate(modem.getInstallDate());
        oriModem.setModel(modem.getModel());

        modemDao.update(oriModem);
        modemDao.flushAndClear();

        result.put("id", modem.getId());

        return result;
	}

	// PLC Update
	public Map<String, Object> updateModemPLCIU(PLCIU plciu) {
		Map<String, Object> result = new HashMap<String, Object>();

		PLCIU oriPlciu = plciuDao.get(plciu.getId());

		copyModemField(plciu, oriPlciu);

		oriPlciu.setIpAddr(plciu.getIpAddr());
		oriPlciu.setMacAddr(plciu.getMacAddr());

		plciuDao.update(oriPlciu);
		plciuDao.flushAndClear();

		result.put("id", plciu.getId());

		return result;
	}
	
	/**
	 * modem info update 시 공통 항목 설정.
	 * @param src
	 * @param dest
	 */
	public void copyModemField(Modem src, Modem dest){
		
		// Update 항목
		dest.setHwVer(src.getHwVer());
		dest.setSwVer(src.getSwVer());
		dest.setFwVer(src.getFwVer());
		dest.setFwRevision(src.getFwRevision());
	   
		//MCU
		MCU mcu = mcuDao.get(src.getMcu().getSysID());
		if(mcu != null && mcu.getMcuStatus() != null && mcu.getMcuStatus().getCode().equals(McuStatus.Delete.getCode())) {
			dest.setMcu(null);
		} else {
			dest.setMcu(mcu);
		}

		dest.setRfPower(src.getRfPower());
		dest.setNodeKind(src.getNodeKind());
		
		if (src.getIpAddr() != null)
			dest.setIpAddr(src.getIpAddr());
		
		if (src.getLocation().getId() != null)
			dest.setLocation(locDao.get(src.getLocation().getId()));

		if (src.getModel().getId() != null)
			dest.setModel(deviceModelDao.get(src.getModel().getId()));

		if (src.getProtocolType() != null)
			dest.setProtocolType(src.getProtocolType().toString());

		if (src.getModemType() != null)
			dest.setModemType(src.getModemType().toString());
		
		if (src.getDeviceSerial() != null)
			dest.setDeviceSerial(src.getDeviceSerial().toString());

		if (src.getInstallDate() != null)
		    dest.setInstallDate(src.getInstallDate());
		
		if (src.getModemStatus() != null)
			dest.setModemStatus(src.getModemStatus());
	}

	// ZRU Update
	public Map<String, Object> updateModemZRU(ZRU zru) {
		Map<String, Object> result = new HashMap<String, Object>();

		ZRU updateZru = zruDao.get(zru.getId());
		
		// Modem 공통항목 값 설정.
		copyModemField(zru,updateZru);
		
		updateZru.setChannelId(zru.getChannelId());
		updateZru.setPanId(zru.getPanId());

		zruDao.update(updateZru);
		zruDao.flushAndClear();

		result.put("id", zru.getId());

		return result;
	}

	// ZEUPLS Update
	public Map<String, Object> updateModemZEUPLS(ZEUPLS zeupls) {
		Map<String, Object> result = new HashMap<String, Object>();

		ZEUPLS oriZeupls = zeuplsDao.get(zeupls.getId());

		// Modem 공통항목 값 설정.
		copyModemField(zeupls, oriZeupls);

		oriZeupls.setChannelId(zeupls.getChannelId());
		oriZeupls.setPanId(zeupls.getPanId());

		zeuplsDao.update(oriZeupls);
		zeuplsDao.flushAndClear();

		result.put("id", zeupls.getId());

		return result;
	}

	// MMIUS Update
	public Map<String, Object> updateModemMMIU(MMIU mmiu) {
		Map<String, Object> result = new HashMap<String, Object>();

		MMIU oriMmiu = mmiuDao.get(mmiu.getId());
		
		copyModemField(mmiu, oriMmiu);
		oriMmiu.setIpv6Address(mmiu.getIpv6Address());
		if (mmiu.getPhoneNumber() != null)
			oriMmiu.setPhoneNumber(mmiu.getPhoneNumber());

		mmiuDao.update(oriMmiu);
		mmiuDao.flushAndClear();

		result.put("id", mmiu.getId());

		return result;
	}

	// IEIU Update
	public Map<String, Object> updateModemIEIU(IEIU ieiu) {
		Map<String, Object> result = new HashMap<String, Object>();

		IEIU oriIeiu = ieiuDao.get(ieiu.getId());

		copyModemField(ieiu, oriIeiu);

		ieiuDao.update(oriIeiu);
		ieiuDao.flushAndClear();

		result.put("id", ieiu.getId());

		return result;
	}

	// ZMU Update
	public Map<String, Object> updateModemZMU(ZMU zmu) {
		Map<String, Object> result = new HashMap<String, Object>();

		ZMU oriZmu = zmuDao.get(zmu.getId());

		copyModemField(zmu, oriZmu);

		zmuDao.update(oriZmu);
		zmuDao.flushAndClear();

		result.put("id", zmu.getId());

		return result;
	}

	// IHD Update
	public Map<String, Object> updateModemIHD(IHD ihd) {
		Map<String, Object> result = new HashMap<String, Object>();

		IHD oriIhd = ihdDao.get(ihd.getId());

		copyModemField(ihd, oriIhd);

		ihdDao.update(oriIhd);
		ihdDao.flushAndClear();

		result.put("id", ihd.getId());

		return result;
	}

	// ACD Update
	public Map<String, Object> updateModemACD(ACD acd) {
		Map<String, Object> result = new HashMap<String, Object>();

		ACD oriAcd = acdDao.get(acd.getId());

		copyModemField(acd, oriAcd);

		acdDao.update(oriAcd);
		acdDao.flushAndClear();

		result.put("id", acd.getId());

		return result;
	}

	// HMU Update
	public Map<String, Object> updateModemHMU(HMU hmu) {
		Map<String, Object> result = new HashMap<String, Object>();

		HMU oriHmu = hmuDao.get(hmu.getId());

		copyModemField(hmu, oriHmu);

		hmuDao.update(oriHmu);
		hmuDao.flushAndClear();

		result.put("id", hmu.getId());

		return result;
	}

	// ZEUMBus Update
	public Map<String, Object> updateModemZEUMBus(ZEUMBus zeumBus) {
		Map<String, Object> result = new HashMap<String, Object>();

		ZEUMBus oriZEUMBus = zeumbusDao.get(zeumBus.getId());

		copyModemField(zeumBus, oriZEUMBus);
		
		oriZEUMBus.setChannelId(zeumBus.getChannelId());
		oriZEUMBus.setPanId(zeumBus.getPanId());

		zeumbusDao.update(oriZEUMBus);
		zeumbusDao.flushAndClear();

		result.put("id", zeumBus.getId());

		return result;
	}

	// ZBRepeter Update
	public Map<String, Object> updateModemZBRepeater(ZBRepeater zbRepeter) {
		Map<String, Object> result = new HashMap<String, Object>();

		ZBRepeater oriZBRepeater = zbrepeaterDao.get(zbRepeter.getId());

		copyModemField(zbRepeter, oriZBRepeater);

		zbrepeaterDao.update(oriZBRepeater);
		zbrepeaterDao.flushAndClear();

		result.put("id", zbRepeter.getId());

		return result;
	}

	// Converter Update
	public Map<String, Object> updateModemConverter(Converter converter) {
		Map<String, Object> result = new HashMap<String, Object>();

		Converter oriConverter = converterDao.get(converter.getId());

		copyModemField(converter, oriConverter);
		
		if(converter.getIpAddr() != null)
			oriConverter.setIpAddr(converter.getIpAddr());
		
		if(converter.getSysPort() != null)
			oriConverter.setSysPort(converter.getSysPort());

		converterDao.update(oriConverter);
		converterDao.flushAndClear();

		result.put("id", converter.getId());

		return result;
	}
	
	// SubGiga Update
	public Map<String, Object> updateModemSubGiga(SubGiga subGiga) {
		Map<String, Object> result = new HashMap<String, Object>();

		SubGiga updateSubGiga = subGigaDao.get(subGiga.getId());

		// Modem 공통항목 값 설정.
		copyModemField(subGiga,updateSubGiga);
		updateSubGiga.setIpv6Address(subGiga.getIpv6Address());
		subGigaDao.update(updateSubGiga);
		subGigaDao.flushAndClear();

		result.put("id", subGiga.getId());

		return result;
	}
	
	public Map<String, Object> updateModemLTE(LTE lte) {
		Map<String, Object> result = new HashMap<String, Object>();

		LTE updateLTE = lteDao.get(lte.getId());

		// Modem 공통항목 값 설정.
		copyModemField(lte, updateLTE);

		lteDao.update(updateLTE);
		lteDao.flushAndClear();

		result.put("id", lte.getId());

		return result;
	}	

	public Map<String, Object> updateModemScheduleZEUPLS(ZEUPLS zeupls) {
		Map<String, Object> result = new HashMap<String, Object>();

		ZEUPLS oriZeupls = zeuplsDao.get(zeupls.getId());

		oriZeupls.setHwVer(zeupls.getHwVer());
		oriZeupls.setSwVer(zeupls.getSwVer());

		oriZeupls.setLpPeriod(zeupls.getLpPeriod());
		oriZeupls.setAlarmFlag(zeupls.getAlarmFlag());
		oriZeupls.setLpChoice(zeupls.getLpChoice());
		oriZeupls.setMeteringDay(zeupls.getMeteringDay());
		oriZeupls.setMeteringHour(zeupls.getMeteringHour());

		if (zeupls.getLocation().getId() != null)
			oriZeupls.setLocation(locDao.get(zeupls.getLocation().getId()));

		if (zeupls.getModel().getId() != null)
			oriZeupls.setModel(deviceModelDao.get(zeupls.getModel().getId()));

		zeuplsDao.update(oriZeupls);
		zeuplsDao.flushAndClear();

		result.put("id", zeupls.getId());

		return result;
	}

	public Map<String, Object> updateModemScheduleZEUMBus(ZEUMBus zeumbus) {
		Map<String, Object> result = new HashMap<String, Object>();

		ZEUMBus oriZeumbus = zeumbusDao.get(zeumbus.getId());

		oriZeumbus.setLpPeriod(zeumbus.getLpPeriod());
		// oriZeumbus.setAlarmFlag(zeumbus.getAlarmFlag());
		oriZeumbus.setLpChoice(zeumbus.getLpChoice());
		oriZeumbus.setMeteringDay(zeumbus.getMeteringDay());
		oriZeumbus.setMeteringHour(zeumbus.getMeteringHour());

		if (zeumbus.getLocation().getId() != null)
			oriZeumbus.setLocation(locDao.get(zeumbus.getLocation().getId()));

		if (zeumbus.getModel().getId() != null)
			oriZeumbus.setModel(deviceModelDao.get(zeumbus.getModel().getId()));

		zeumbusDao.update(oriZeumbus);
		zeumbusDao.flushAndClear();

		result.put("id", zeumbus.getId());

		return result;
	}

	public Map<String, Object> updateModemScheduleZBRepeater(
			ZBRepeater zbRepeater) {
		Map<String, Object> result = new HashMap<String, Object>();

		ZBRepeater oriZbRepeater = zbrepeaterDao.get(zbRepeater.getId());

		oriZbRepeater.setLpPeriod(zbRepeater.getLpPeriod());
		// oriZbRepeater.setAlarmFlag(zbRepeater.getAlarmFlag());
		oriZbRepeater.setLpChoice(zbRepeater.getLpChoice());
		oriZbRepeater.setMeteringDay(zbRepeater.getMeteringDay());
		oriZbRepeater.setMeteringHour(zbRepeater.getMeteringHour());

		if (zbRepeater.getLocation().getId() != null)
			oriZbRepeater.setLocation(locDao.get(zbRepeater.getLocation()
					.getId()));

		if (zbRepeater.getModel().getId() != null)
			oriZbRepeater.setModel(deviceModelDao.get(zbRepeater.getModel()
					.getId()));

		zbrepeaterDao.update(oriZbRepeater);
		zbrepeaterDao.flushAndClear();

		result.put("id", zbRepeater.getId());

		return result;
	}

	public List<Object> getModemListByMCUsysID(String sys_id) {
		return modemDao.getModemListByMCUsysID(sys_id);
	}

	public List<Object> getModemIdListByDevice_serial(String device_serial) {
		return modemDao.getModemIdListByDevice_serial(device_serial);
	}
	
	public List<Object> getModemListExcel(Map<String, Object> condition) {
		condition.put("excelList", "list");	

		List<Object> result = new ArrayList<Object>();
		result = (List<Object>) this.getModemSearchGrid(condition).get(1);
	
		List<Object> resultList = new ArrayList<Object>();
		HashMap<String,Object> resultMap = null;
		if(result.size() > 0) {
			Map<String,Object> tmp = null;
			for(Object obj:result) {
				tmp = new HashMap<String,Object>();
	    		tmp = (Map<String,Object>)obj;
	    		
	    		resultMap = new HashMap<String,Object>();
	    		resultMap.put("no", StringUtil.nullToBlank(tmp.get("no")));
	    		resultMap.put("modemDeviceSerial", StringUtil.nullToBlank(tmp.get("modemDeviceSerial")));
	    		resultMap.put("modemType", StringUtil.nullToBlank(tmp.get("modemType")));
	    		resultMap.put("mcuSysId", StringUtil.nullToBlank(tmp.get("mcuSysId")));
	    		resultMap.put("vendorName", StringUtil.nullToBlank(tmp.get("vendorName")));
	    		resultMap.put("deviceName", StringUtil.nullToBlank(tmp.get("deviceName")));
	    		resultMap.put("ver", StringUtil.nullToBlank(tmp.get("ver")));
	    		resultMap.put("lastCommDate", StringUtil.nullToBlank(tmp.get("lastCommDate")));
	    		resultMap.put("protocolType", StringUtil.nullToBlank(tmp.get("protocolType")));
				resultMap.put("macAddr", StringUtil.nullToBlank(tmp.get("macAddr")));
				resultMap.put("phone", StringUtil.nullToBlank(tmp.get("phone")));
				resultMap.put("gs1", StringUtil.nullToBlank(tmp.get("gs1")));
				resultMap.put("po", StringUtil.nullToBlank(tmp.get("po")));
				resultMap.put("simNumber", StringUtil.nullToBlank(tmp.get("simNumber")));
				resultMap.put("iccId", StringUtil.nullToBlank(tmp.get("iccId")));
				resultMap.put("manufacturedDate", StringUtil.nullToBlank(tmp.get("manufacturedDate")));
				resultMap.put("imei", StringUtil.nullToBlank(tmp.get("imei")));
				resultMap.put("hwVer", StringUtil.nullToBlank(tmp.get("hwVer")));
				resultMap.put("swVer", StringUtil.nullToBlank(tmp.get("swVer")));
				resultMap.put("lot", "-");
	    		
	    		resultList.add(resultMap);
			}
		}
			
		return resultList;	
	}

	public List<Object> getModemCommInfoListExcel(Map<String, Object> condition) {
		condition.put("isExcel", true);	
		
		List<Object> result = new ArrayList<Object>();
		result = (List<Object>) this.getModemSearchChart(condition).get(0);

		List<Object> resultList = new ArrayList<Object>();
		HashMap<String, Object> resultMap = null;
		if (result.size() > 0) {
			Map<String, Object> tmp = null;
			for (Object obj : result) {
				tmp = new HashMap<String, Object>();
				tmp = (Map<String, Object>) obj;

				resultMap = new HashMap<String, Object>();
				resultMap.put("no", StringUtil.nullToBlank(tmp.get("no")));
				resultMap.put("mcuSysId", StringUtil.nullToBlank(tmp.get("mcuSysId")));
				resultMap.put("activity24", StringUtil.nullToBlank(tmp.get("value0")));
				resultMap.put("noActivity24", StringUtil.nullToBlank(tmp.get("value1")));
				resultMap.put("noActivity48", StringUtil.nullToBlank(tmp.get("value2")));
				resultMap.put("unknown", StringUtil.nullToBlank(tmp.get("value3")));
				resultMap.put("commError", StringUtil.nullToBlank(tmp.get("value4")));
				resultMap.put("securityError", StringUtil.nullToBlank(tmp.get("value5")));
	    		
	    		resultList.add(resultMap);
			}
		}
			
		return resultList;	
	}
	
	public void update(Modem modem) {
	    Modem _modem = modemDao.get(modem.getDeviceSerial());
        
        if (_modem != null && modem.getId() == null) {
            log.info("ModemID[" + modem.getDeviceSerial() + 
                    "] X[" + modem.getGpioX() + 
                    "] Y[" + modem.getGpioY() + 
                    "] Z[" + modem.getGpioZ() + "]");
            
            if (modem.getGpioX() != null) _modem.setGpioX(modem.getGpioX());
            if (modem.getGpioY() != null) _modem.setGpioY(modem.getGpioY());
            if (modem.getGpioZ() != null) _modem.setGpioZ(modem.getGpioZ());
            
            modemDao.update(_modem);
            return;
        }
        
		modemDao.update(modem);
	}
	
	/**
	 * 현제 날짜 (YYYYMMDD)
	 * @return
	 */
	public String getCurrentDate() {
		Calendar ti = Calendar.getInstance();
		
		String month = "";
		String day = "";
		
		if((ti.get(Calendar.MONTH) + 1) < 10) month = "0" +  (ti.get(Calendar.MONTH) + 1);
		else month = "" +  (ti.get(Calendar.MONTH) + 1);
		
		if((ti.get(Calendar.DAY_OF_MONTH) + 1) < 10) day = "0" +  ti.get(Calendar.DAY_OF_MONTH);
		else day = "" +  ti.get(Calendar.DAY_OF_MONTH);


		return "" + ti.get(Calendar.YEAR) + month + day;
	}

    @Override
    public int setLocation(String deviceSerial, String address, double x,
            double y, double z) {
        try {
            Modem modem = modemDao.get(deviceSerial);
            if (modem == null)
                return 1;
            
            if (address != null && !"".equals(address))
                modem.setAddress(address);
            
            modem.setGpioX(x);
            modem.setGpioY(y);
            modem.setGpioZ(z);
            
            modemDao.update(modem);
            
            return 0;
        }
        catch (Exception e) {
            log.error(e, e);
            return 2;
        }
    }
    
    public List<String> getFirmwareVersionList(Map<String, Object> condition){
    	List<String> versionList = modemDao.getFirmwareVersionList(condition);
    	return versionList;
    }

    public List<String> getDeviceList(Map<String, Object> condition){
    	List<String> deviceList = modemDao.getDeviceList(condition);
    	return deviceList;
    }
    
    public List<String> getDeviceListModem(Map<String, Object> condition){
    	List<String> deviceList = modemDao.getDeviceListModem(condition);
    	return deviceList;
    }
    
    public List<String> getTargetList(Map<String, Object> condition){
    	List<String> deviceList = modemDao.getTargetList(condition);
    	return deviceList;
    }
    
    public List<String> getTargetListModem(Map<String, Object> condition){
    	List<String> deviceList = modemDao.getTargetListModem(condition);
    	return deviceList;
    }
    
    public List<Object> getModemList(Map<String, Object> condition) {
    	List<Object> deviceList = modemDao.getModemList(condition);
    	
    	String supplierId = StringUtil.nullToBlank(condition.get("supplierId"));
    	Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
    	for (Object data : deviceList) 
		{
	    	Map<String, Object> mapData = (Map<String, Object>) data;
	    	mapData.put("LASTCOMMDATE", TimeLocaleUtil.getLocaleDate(
					StringUtil.nullToBlank(mapData.get("LASTCOMMDATE")),
					supplier.getLang().getCode_2letter(), supplier
							.getCountry().getCode_2letter()));
	    	
		}
    	return deviceList;
    }

    //SP-1004
	@Override
	public List<Map<String, Object>> getParentDevice(Map<String, Object> condition) {
		List<Map<String, Object>> deviceList = modemDao.getParentDevice(condition);
    	return deviceList;
	}
}
