package com.aimir.bo.device;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.MeterEventLogDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.MapManager;
import com.aimir.service.device.MeterManager;
import com.aimir.service.device.ModemManager;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.util.TimeLocaleUtil;

import de.micromata.opengis.kml.v_2_2_0.Kml;

@Controller
public class mapController {

    Log log = LogFactory.getLog(mapController.class);

    @Autowired
    MapManager mapManager;

    @Autowired
    LocationManager locationManager;
    
    @Autowired
    ModemManager modemManager;
    
    @Autowired
    MeterManager meterManager;
    
    @Autowired
    MeterEventLogDao meterEventLogDao;
    
    @Autowired
    OperatorManager operatorManager;

    // 지도 초기 화면
    @RequestMapping(value="/gadget/device/mapview.do")
    public ModelAndView Simple(HttpServletRequest request, HttpServletResponse response) {
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          
        int supplierId = user.getRoleData().getSupplier().getId();

        ModelAndView mav = new ModelAndView();
        mav.addObject("supplierID", supplierId);
        mav.setViewName("gadget/device/mapview");

        return mav;
    }

    // 장비 리스트 반환
    @RequestMapping(value="/gadget/device/maplist.do")
    public ModelAndView MCUList(@RequestParam("locationID") Integer locationID,@RequestParam("supplierID") Integer supplierID, @RequestParam("class") String className, @RequestParam("field") String field) {
        int supplierId = supplierID;

        ModelAndView mav = new ModelAndView();
        Kml kml;

        if (className.equals("mcu")) {
            if (field.length() == 0){
            	if(locationID != null && locationID !=-1)
            		kml = mapManager.getMCU(supplierId,locationID);
            	else
            	kml = mapManager.getMCU(supplierId);
            }
            else if(locationID != null && locationID !=-1) kml = mapManager.getMCU(supplierId, field,locationID);
            else kml = mapManager.getMCU(supplierId, field);
        } else if (className.equals("meter")) {
            if (field.length() == 0){
            	if(locationID != null && locationID !=-1)
            		kml = mapManager.getMeter(supplierId,locationID);
            	else
            	  	kml = mapManager.getMeter(supplierId);
            }
            else if(locationID != null && locationID !=-1) kml = mapManager.getMeter(supplierId, field,locationID);
            else kml = mapManager.getMeter(supplierId, field);
        } else if (className.equals("modem")) {
            if (field.length() == 0){
            	if(locationID != null && locationID !=-1)
            		kml = mapManager.getModem(supplierId,locationID);
            	else
            	  	kml = mapManager.getModem(supplierId);
            }
            else if(locationID != null && locationID !=-1) kml = mapManager.getModem(supplierId, field,locationID);
            else kml = mapManager.getModem(supplierId, field); 
        } else if (className.equals("mcucodi")) {
        	 kml = mapManager.getMCUWithCodi(supplierId, field,locationID);
        } else {
            kml = new Kml();
        }

        mav.addObject("result", kml);
        mav.addObject("className", className);
        mav.setViewName("jsonView");

        return mav;
    }
    
 

    // 좌표 업데이트
    @RequestMapping(value="/gadget/device/mapUpdate.do")
    public ModelAndView ListUpdate(@RequestParam("className") String className, @RequestParam("name") String name, @RequestParam("pointx") Double gpioX, @RequestParam("pointy") Double gpioY) {

        ModelAndView mav = new ModelAndView();

        if (className.equals("mcu")) {
            if (mapManager.setMCUPoint(name, gpioX, gpioY, 0.0)) {
                mav.addObject("result", "success");
            } else {
                mav.addObject("result", "false");
            }
        } else if (className.equals("meter")) {
            if (mapManager.setMeterPoint(name, gpioX, gpioY, 0.0)){
                mav.addObject("result", "success");
            } else {
                mav.addObject("result", "false");
            }
        } else if (className.equals("modem")) {
            if (mapManager.setModemPoint(name, gpioX, gpioY, 0.0)) {
                mav.addObject("result", "success");
            } else {
                mav.addObject("result", "false");
            }
        } else {
            mav.addObject("result", "false");
        }

        mav.setViewName("jsonView");

        return mav;
    }

    // 좌표 업데이트
    @RequestMapping(value="/gadget/device/mapUpdateAddress.do")
    public ModelAndView ListAddressUpdate(@RequestParam("className") String className, @RequestParam("name") String name, @RequestParam("address") String address) throws UnsupportedEncodingException {

        ModelAndView mav = new ModelAndView();
        address =  URLDecoder.decode(address,"UTF-8");  

        if (className.equals("mcu")||className.equals("mcucodi")) {
            if (mapManager.setMCUAddress(name, address)) {
                mav.addObject("result", "success");
            } else {
                mav.addObject("result", "false");
            }
        } else if (className.equals("meter")) {
            if (mapManager.setMeterAddress(name, address)) {
                mav.addObject("result", "success");
            } else {
                mav.addObject("result", "false");
            }
        } else if (className.equals("modem")) {
            if (mapManager.setModemAddress(name, address)) {
                mav.addObject("result", "success");
            } else {
                mav.addObject("result", "false");
            }
        } else {
            mav.addObject("result", "false");
        }

        mav.setViewName("jsonView");

        return mav;
    }

    // MCU 전체에 대한 KML 출력
    @RequestMapping(value="/{supplierID}/mcu.kml")
    public ModelAndView MCUAllKML(@PathVariable("supplierID") Integer supplierID) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getMCU(supplierID);

        mav.addObject(kml);
        mav.setViewName("kmlView");
        return mav;
    }

    // MCU 개별에 대한 KML 출력
    @RequestMapping(value="/{supplierID}/{sysID}/mcu.kml")
    public ModelAndView MCUtoKML(@PathVariable("supplierID") Integer supplierID, @PathVariable("sysID") String sysID) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getMCU(supplierID, sysID);

        mav.addObject(kml);
        mav.setViewName("kmlView");
        return mav;
    }

    // MCU 개별에 대한 연관 장비 KML 출력
    @RequestMapping(value="/{supplierID}/{sysID}/mcu+relative.kml")
    public ModelAndView MCUtoDevicetoKML(@PathVariable("supplierID") Integer supplierID, @PathVariable("sysID") String sysID) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getMCUWithRelativeModem(supplierID, sysID);

        mav.addObject(kml);
        mav.setViewName("kmlView");
        return mav;
    }

    // Modem 전체에 대한 KML 출력
    @RequestMapping(value="/{supplierID}/modem.kml")
    public ModelAndView ModemAllKML(@PathVariable("supplierID") Integer supplierID) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getModem(supplierID);

        mav.addObject(kml);
        mav.setViewName("kmlView");
        return mav;
    }

    // Modem 개별에 대한 연관 장비 KML 출력
    @RequestMapping(value="/{supplierID}/{deviceSerial}/modem+relative.kml")
    public ModelAndView ModemtoKML(@PathVariable("supplierID") Integer supplierID, @PathVariable("deviceSerial") String deviceSerial) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getModemWithRelativeDevice(supplierID, deviceSerial);

        mav.addObject(kml);
        mav.setViewName("kmlView");
        return mav;
    }

    // Modem 개별에 대한 KML 출력
    @RequestMapping(value="/{supplierID}/{deviceSerial}/modem.kml")
    public ModelAndView ModemtoDevicetoKML(@PathVariable("supplierID") Integer supplierID, @PathVariable("deviceSerial") String deviceSerial) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getModem(supplierID, deviceSerial);

        mav.addObject(kml);
        mav.setViewName("kmlView");
        return mav;
    }

    // Meter 전체에 대한 KML 출력
    @RequestMapping(value="/{supplierID}/meter.kml")
    public ModelAndView MeterAllKML(@PathVariable("supplierID") Integer supplierID) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getMeter(supplierID);

        mav.addObject(kml);
        mav.setViewName("kmlView");
        return mav;
    }

    // Meter 개별에 대한 KML 출력
    @RequestMapping(value="/{supplierID}/{mdsID}/meter.kml")
    public ModelAndView MetertoKML(@PathVariable("supplierID") Integer supplierID, @PathVariable("mdsID") String mdsID) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getMeter(supplierID, mdsID);

        mav.addObject(kml);
        mav.setViewName("kmlView");
        return mav;
    }

    // Meter 개별에 대한 연관 장비 KML 출력
    @RequestMapping(value="/{supplierID}/{mdsID}/meter+relative.kml")
    public ModelAndView MetertoDevicetoKML(@PathVariable("supplierID") Integer supplierID, @PathVariable("mdsID") String mdsID) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getMeterWithRelativeDevice(supplierID, mdsID);

        mav.addObject(kml);
        mav.setViewName("kmlView");
        return mav;
    }

    // 지도 초기 화면 (Mini)
    @RequestMapping(value="/gadget/device/GoogleMapMini.do")
    public ModelAndView MapMini(HttpServletRequest request, HttpServletResponse response) {
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          
        int supplierId = user.getRoleData().getSupplier().getId();

        List<Location> rootLocation = locationManager.getParentsBySupplierId(supplierId);
        String locationName = rootLocation.size() > 0 ? rootLocation.get(0).getName() : "";

        ModelAndView mav = new ModelAndView();
        mav.addObject("supplierID", supplierId);
        mav.addObject("locationName", locationName);
        mav.setViewName("gadget/device/googleMapMini");

        return mav;
    }

    // 지도 초기 화면 (Mini)
    @RequestMapping(value="/gadget/device/GoogleMapMax.do")
    public ModelAndView MapMax(HttpServletRequest request, HttpServletResponse response) {
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          
        int supplierId = user.getRoleData().getSupplier().getId();

        List<Location> rootLocation = locationManager.getParentsBySupplierId(supplierId);
        String locationName = rootLocation.size() > 0 ? rootLocation.get(0).getName() : "";

        ModelAndView mav = new ModelAndView();
        String mcuName="";
        mav.addObject("mcuName", mcuName);
        mav.addObject("supplierID", supplierId);
        mav.addObject("locationName", locationName);
        mav.setViewName("gadget/device/googleMapMax");

        return mav;
    }

    // MCU 전체에 대한 KML 출력
    @RequestMapping(value="/{supplierID}/mcu.do")
    public ModelAndView MCUAllJson(@PathVariable("supplierID") Integer supplierID) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getMCU(supplierID);

        mav.addObject(kml);
        mav.setViewName("jsonView");
        return mav;
    }

    // MCU 개별에 대한 KML 출력
    @RequestMapping(value="/{supplierID}/{sysID}/mcu.do")
    public ModelAndView MCUtoJson(@PathVariable("supplierID") Integer supplierID, @PathVariable("sysID") String sysID) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getMCUMapData(supplierID, sysID);

        mav.addObject(kml);
        mav.setViewName("jsonView");
        return mav;
    }
    
 // MCU 개별에 대한 KML 출력
    @RequestMapping(value="/{supplierID}/{sysID}/mcucodi.do")
    public ModelAndView MCUCoditoJson(@PathVariable("supplierID") Integer supplierID, @PathVariable("sysID") String sysID) {
        ModelAndView mav = new ModelAndView();
        List<MCU> mcuList = mapManager.getMCUbyCodi(sysID);
        
        String mcuId = ((MCU)mcuList.get(0)).getSysID();
        Kml kml = mapManager.getMCUWithCodi(supplierID, mcuId);

        mav.addObject(kml);
        mav.setViewName("jsonView");
        return mav;
    }

    // MCU 개별에 대한 연관 장비 KML 출력
    @RequestMapping(value="/{supplierID}/{sysID}/mcu+relative.do")
    public ModelAndView MCUtoDevicetoJson(@PathVariable("supplierID") Integer supplierID, @PathVariable("sysID") String sysID) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getMCUWithRelativeModem(supplierID, sysID);

        mav.addObject(kml);
        mav.setViewName("jsonView");
        return mav;
    }
    
 // MCU 개별에 대한 연관 장비 KML 출력
    @RequestMapping(value="/{supplierID}/{sysID}/mcucodi+relative.do")
    public ModelAndView MCUCoditoDevicetoJson(@PathVariable("supplierID") Integer supplierID, @PathVariable("sysID") String sysID) {
        ModelAndView mav = new ModelAndView();
        List<MCU> mcuList = mapManager.getMCUbyCodi(sysID);
        
        String mcuId = ((MCU)mcuList.get(0)).getSysID();
        Kml kml = mapManager.getMCUCodiWithRelativeModem(supplierID, mcuId);

        mav.addObject(kml);
        mav.setViewName("jsonView");
        return mav;
    }

    // Modem 전체에 대한 KML 출력
    @RequestMapping(value="/{supplierID}/modem.do")
    public ModelAndView ModemAllJson(@PathVariable("supplierID") Integer supplierID) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getModem(supplierID);

        mav.addObject(kml);
        mav.setViewName("jsonView");
        return mav;
    }

    // Modem 개별에 대한 연관 장비 KML 출력
    @RequestMapping(value="/{supplierID}/{deviceSerial}/modem+relative.do")
    public ModelAndView ModemtoJson(@PathVariable("supplierID") Integer supplierID, @PathVariable("deviceSerial") String deviceSerial) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getModemWithRelativeDevice(supplierID, deviceSerial);

        mav.addObject(kml);
        mav.setViewName("jsonView");
        return mav;
    }

    // Modem 개별에 대한 KML 출력
    @RequestMapping(value="/{supplierID}/{deviceSerial}/modem.do")
    public ModelAndView ModemtoDevicetoJson(@PathVariable("supplierID") Integer supplierID, @PathVariable("deviceSerial") String deviceSerial) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getModemMapData(supplierID, deviceSerial);

        mav.addObject(kml);
        mav.setViewName("jsonView");
        return mav;
    }

    // Meter 전체에 대한 KML 출력
    @RequestMapping(value="/{supplierID}/meter.do")
    public ModelAndView MeterAllJson(@PathVariable("supplierID") Integer supplierID) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getMeter(supplierID);

        mav.addObject(kml);
        mav.setViewName("jsonView");
        return mav;
    }

    // Meter 개별에 대한 KML 출력
    @RequestMapping(value="/{supplierID}/{mdsID}/meter.do")
    public ModelAndView MetertoJson(@PathVariable("supplierID") Integer supplierID, @PathVariable("mdsID") String mdsID) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getMeterMapData(supplierID, mdsID);

        mav.addObject(kml);
        mav.setViewName("jsonView");
        return mav;
    }

    // Meter 개별에 대한 연관 장비 KML 출력
    @RequestMapping(value="/{supplierID}/{mdsID}/meter+relative.do")
    public ModelAndView MetertoDevicetoJson(@PathVariable("supplierID") Integer supplierID, @PathVariable("mdsID") String mdsID) {
        ModelAndView mav = new ModelAndView();
        
        Kml kml = mapManager.getMeterWithRelativeDevice(supplierID, mdsID);

        mav.addObject(kml);
        mav.setViewName("jsonView");
        return mav;
    }
    
    //Map에서 모뎀 선택시 나타나는 미터정보 출력
    @RequestMapping(value="/gadget/device/meterInfo.do")
    public ModelAndView getMeterInfo(@RequestParam(value = "target", required = false) String target,
    		@RequestParam(value = "loginId", required = false) String loginId) {
    	ModelAndView mav = new ModelAndView("jsonView");
		String rtnStr1 = "<b>Meter Number : </b> -";
		String rtnStr2 = "<br/><b>Last Event Time : </b> -";
    	Modem modem = modemManager.getModem(Integer.parseInt(target)); 
    	
    	 Supplier supplier = modem.getSupplier();
         
         if(supplier == null){
             //Operator operator = operatorManager.getOperatorByLoginId(loginId);
        	 Operator operator = operatorManager.getOperatorByLoginId(loginId);
        	 supplier = operator.getSupplier();
         }
         // Date Format을 위한 설정
         String lang = supplier.getLang().getCode_2letter();
         String country = supplier.getCountry().getCode_2letter();
         
    	String mds_id="";

    	//SP-1019
        if (modem != null && !modem.getMeter().isEmpty()) {
            for (Meter m : modem.getMeter()) {
                if (m.getModemPort() == null || m.getModemPort() == 0) {
                    mds_id = m.getMdsId();
                    break;
                }
            }
        }
        
        List list =meterEventLogDao.getEventLogByMds_id(mds_id);
        String openTime="";
        if(list.size() != 0)
        	openTime = list.get(0).toString();
        if(mds_id != "" && mds_id != null)
        	rtnStr1 = "<b>Meter Number: </b>" + mds_id;
        if(openTime != "" && openTime != null)
        	rtnStr2 = "<br/><b>Last Event Time: </b>" + TimeLocaleUtil.getLocaleDate(openTime, lang, country);
        mav.addObject("rtnStr", rtnStr1 + rtnStr2);
        return mav;
    }
    
	@RequestMapping(value = "/gadget/device/meterEventTime.do")
	public ModelAndView getMeterEventTime(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) {
		
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Meter meter = meterManager.getMeter(Integer.parseInt(target));
		Supplier supplier = meter.getSupplier();
		
		String rtnStr = "";
		String mds_id = "";

		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		Integer supplierID = supplier.getId();
		
		// Date Format을 위한 설정
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		
		mds_id = meter.getMdsId();
		
		List list = meterEventLogDao.getEventLogByMds_id(mds_id);
		String openTime = "";
		if (list.size() != 0) {
			openTime = list.get(0).toString();
			
			if (openTime != "" && openTime != null) {
				rtnStr = "<b>Last Event Time : </b>" + TimeLocaleUtil.getLocaleDate(openTime, lang, country);
				status = ResultStatus.SUCCESS;
			} else {
				rtnStr = "<b>Last Event Time : </b>" + "-";
			}
		} else {
			rtnStr = "<b>Last Event Time : </b>" + "-";
		}

		mav.addObject("status", status);
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}
	

    /**
     * SP-572
     * @param supplierId
     * @return
     */
    @RequestMapping(value="/gadget/device/getLocationListForMap.do")
    public ModelAndView getLocationListForMap(@RequestParam("supplierId") int supplierId) {

        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
    	//String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");
        String contextRoot =  request.getSession().getServletContext().getRealPath("/");
    	String dsolistPath = contextRoot + "/kml/dsolist.txt";
        FileReader fr = null;
        BufferedReader br = null;
        HashMap<String, Object> dsomap = new HashMap<String, Object>();
        ArrayList<Location> retLoc = new ArrayList<Location>();
        
        try {
        	fr = new FileReader(dsolistPath);
        	br = new BufferedReader(fr);

        	String line, dsoName;
        	while ((line = br.readLine()) != null) {
        		dsoName = line.trim();
        		dsomap.put(dsoName,"exist");
        	}
        	
        	List<Location> locations = locationManager.getRootLocationListBySupplier(supplierId);
        	for (Location loc : locations) {
        		if ( dsomap.get(loc.getName()) != null){
        			retLoc.add(loc);
        		}
        	}
        } catch (Exception e) {
        	
        } finally {
        	try {
        		br.close();
        		fr.close();
        	} catch (Exception e) {
        	}
        }
		ModelMap model = new ModelMap("locations",retLoc);

		return new ModelAndView("jsonView", model);
    }
    
    
    /**
     * SP-1050
     * @param locationName
     * @return
     */
    @RequestMapping(value="/gadget/device/getMsaListByLocationName.do")
	public ModelAndView getMsaListByLocationName(@RequestParam("locationName") String locationName) {
		List<Object> msaList = meterManager.getMsaListByLocationName(locationName);
		ArrayList<HashMap<String,Object>> retList = new ArrayList<HashMap<String,Object>> ();

		for (int i = 0; i < msaList.size(); i++ ) {
			HashMap<String,Object> map = new HashMap<String,Object>();
			if ( (String)msaList.get(i) == null )
				continue;
			map.put("id", i);
			map.put("name",(String)msaList.get(i));
			map.put("order", i);
			retList.add(map);
		}
		ModelMap model = new ModelMap("msas",retList);

		return new ModelAndView("jsonView", model);
	}
    /**
     * SP-1038
     * @param supplierId
     * @return
     */
    @RequestMapping(value="/gadget/device/getRootLocationList.do")
    public ModelAndView getRootLocationList(@RequestParam("supplierId") int supplierId) {
    	List<Location> locations = locationManager.getRootLocationListBySupplier(supplierId);
		ModelMap model = new ModelMap("locations",locations);

		return new ModelAndView("jsonView", model);
    }

    /**
     * SP-572
     * @param locationID
     * @param supplierID
     * @param className
     * @param field
     * @return
     */
    @RequestMapping(value="/gadget/device/getMapList.do")
    public ModelAndView getMapList(@RequestParam("locationID") Integer locationID,
            @RequestParam("supplierID") Integer supplierID, 
            @RequestParam("class") String className, 
            @RequestParam("field") String field,
            @RequestParam("msa") String msa ) {

    	HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        String contextRoot =  request.getSession().getServletContext().getRealPath("/");
    	
    	int supplierId = supplierID;

        ModelAndView mav = new ModelAndView();
        List<Object> devicelist  = new ArrayList<Object>();
        //Map<String, Integer> mapDcuList = readDCUList(contextRoot + "/kml/dculist.txt");
        //Map<String, Integer> mapDsoList = readDSOList(contextRoot + "/kml/dsolist.txt");
        if (className.equals("mcu")) {
           	devicelist = mapManager.getMCUList(supplierID, field, locationID);

        } else if (className.equals("meter")) {
        	devicelist = mapManager.getMeterList(supplierID, field, locationID, msa);
        } else if (className.equals("modem")) {
        	devicelist = mapManager.getModemList(supplierID, field, locationID, msa);
         } else if (className.equals("mcucodi")) {
        	devicelist = mapManager.getMCUWithCodiList(supplierId, field,locationID);
        } 
        // Check mcu is exist in mculist.txt
        for( Object obj : devicelist){
        	HashMap<String, Object> target = (HashMap<String, Object>) obj;
        	if ( target.get("sysID") != null && !"".equals(target.get("sysID"))){
        				target.put("kmlFile", "DCU");
        	}
        	else {
        		target.put("kmlFile", "DSO");
        	}
        }
        mav.addObject("result", devicelist);
        mav.addObject("className", className);
        mav.setViewName("jsonView");
        return mav;
    }
    
    /**
     * SP-572
     * @param filePath
     * @return
     */
    private Map<String,Integer> readDCUList(String filePath){
    	FileReader fr = null;
    	BufferedReader br = null;
    	HashMap<String, Integer> dcumap = new HashMap<String, Integer>();

    	try {
    		fr = new FileReader(filePath);
    		br = new BufferedReader(fr);

    		String line;
    		while ((line = br.readLine()) != null) {
    			String ar[] = line.split("[\\s]+");
    			dcumap.put(ar[0], Integer.valueOf(ar[1]));
    		}
    	} catch (Exception e) {
    	} finally {
    		try {
    			br.close();
    			fr.close();
    		} catch (Exception e) {
    		}
    	}
    	return dcumap;
    }
    /**
     * SP-572
     * @param filePath
     * @return
     */
    private Map<String,Integer> readDSOList(String filePath){
    	FileReader fr = null;
    	BufferedReader br = null;
    	HashMap<String, Integer> dsomap = new HashMap<String, Integer>();

    	try {
    		fr = new FileReader(filePath);
    		br = new BufferedReader(fr);

    		String line;
    		while ((line = br.readLine()) != null) {
        		String dsoName = line.trim();
        		dsomap.put(dsoName, 1);
    		}
    	} catch (Exception e) {
    	} finally {
    		try {
    			br.close();
    			fr.close();
    		} catch (Exception e) {
    		}
    	}
    	return dsomap;
    }
    
    /**
     * SP-1038
     * @param sysId
     * @return
     */
    @RequestMapping(value="/gadget/device/makeMcuMap.do")
    public ModelAndView makeMcuMap(@RequestParam("sysId") String sysId ) {
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        String contextRoot =  request.getSession().getServletContext().getRealPath("/");
        
        // check MCU
        String kmlfile = "/kml/data/map_" + sysId + ".kml";
        
        String kmlpath = contextRoot + kmlfile;
        String result = mapManager.getMcuMap(sysId);
        PrintWriter pw  = null;
        FileWriter file = null;
        try {
           file = new FileWriter(kmlpath);
           pw = new PrintWriter(new BufferedWriter(file));
            pw.print(result);
        } catch (Exception e) {
            kmlfile = "";
        }
        finally {
            if ( pw != null)
                pw.close();
        }
        ModelAndView mav = new ModelAndView();

        mav.addObject("kml", kmlfile);
        mav.setViewName("jsonView");
        return mav;
    }
    
}