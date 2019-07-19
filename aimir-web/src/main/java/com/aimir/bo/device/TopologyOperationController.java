package com.aimir.bo.device;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Location;
import com.aimir.service.device.MCUManager;
import com.aimir.service.device.MeterManager;
import com.aimir.service.device.ModemManager;
import com.aimir.service.system.LocationManager;

@Controller
public class TopologyOperationController {
	@Autowired
	MCUManager mcuManger;
	
	@Autowired
	ModemManager modemManager;
	
	@Autowired
	MeterManager meterManager;
	
	@Autowired
	LocationManager locationManager;
	
	@RequestMapping(value="/gadget/device/topology/topologyPopup")
    public ModelAndView topologyPopup() {
    	ModelAndView mav = new ModelAndView("/gadget/device/topology/topologyPopup");
    	return mav;
    }
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/gadget/device/topology/getTopologyInfo")
    public ModelAndView getTopologyInfo(@RequestParam(value = "mcuId", required = false) String mcuId) throws Exception {
		ModelAndView mav = new ModelAndView("jsonView");
		MCU mcu = mcuManger.getMCU(Integer.parseInt(mcuId));
		JSONObject jsonObject = new JSONObject();
		JSONArray nodes = new JSONArray();
		JSONArray links = new JSONArray();
		JSONObject link = new JSONObject();
		JSONObject node = new JSONObject();
		
		// DCU node
		node.put("name", mcu.getSysID());
		node.put("group", 1);
		nodes.put(0,node);
		
		List<Object> modemList = modemManager.getModemListByMCUsysID(mcu.getSysID());
		ArrayList<Integer> modemArr = new ArrayList<Integer>();
		Set<Meter> meterList= null;
		Modem modem;
		Meter meter;
		Integer ModemId;
		Integer sourceIndex;
		Integer targetIndex;
		int j =0;
		for(int i =0; i < modemList.size(); i++){ 
			ModemId = Integer.parseInt(modemList.get(i).toString());
			modemArr.add(i, ModemId);
			modem = modemManager.getModem(ModemId);
			node = new JSONObject();
			link = new JSONObject();
			// Modem node
			node.put("name", modem.getDeviceSerial());
			node.put("group", 2);
			nodes.put(i+1,node);
			// Modem-DCU link
			if(modem.getParentModemId() != null){ // parents modem이 있을경우 기존에 있던 link는 hide 시킨다.
				link.put("source", i+1); // 자기 자신  -> 안 그려짐 
				link.put("target", i+1);
				link.put("value", 0);
				links.put(i,link);
			}else{
				link.put("source", 0); // DCU(0)를 향하게 
				link.put("target", i+1);
				link.put("value", 1);
				links.put(i,link);}
			meterList = modem.getMeter();
	        Iterator<Meter> iter = meterList.iterator(); 
	        if(meterList.isEmpty())
	        	continue;
	        while(iter.hasNext()){
	        	node = new JSONObject();
				link = new JSONObject();
				meter = iter.next();
				// Meter node
				node.put("name", meter.getMdsId());
				node.put("group", 3);
				nodes.put(modemList.size()+1+j,node);
				// Meter-Modem link
				link.put("source", i+1);
				link.put("target", modemList.size()+1+j);
				link.put("value", 1);
				links.put(modemList.size()+j,link);
				j++;
	        }
		}
		int k = modemList.size() + j; 
		for(int i =0; i < modemList.size(); i++){
				ModemId = Integer.parseInt(modemList.get(i).toString());
				modem = modemManager.getModem(ModemId);
				sourceIndex = modemArr.indexOf(modem.getParentModemId())+1;
				targetIndex = i+1;
				if(modem.getParentModemId() == null)
					continue;
				while(true){
					if(modem == null) 
						break;
					else{
						ModemId = modem.getParentModemId();
						if(ModemId == null)
							break;
						modem = modemManager.getModem(ModemId);
						// Modem-Modem link
						link = new JSONObject();
						link.put("source", sourceIndex);
						link.put("target", targetIndex);
						link.put("value", 1);
						links.put(k++,link);
						targetIndex = sourceIndex;
						sourceIndex = modemArr.indexOf(modem.getParentModemId())+1;
					}
				}
		}
		jsonObject.put("nodes", nodes);
		jsonObject.put("links", links);
		mav.addObject("json", jsonObject.toString());
		return mav;
    }
	
	@RequestMapping(value="/gadget/device/NMSMax.do")
    public ModelAndView MapMax(HttpServletRequest request, HttpServletResponse response) {
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        String mcuId = (String)request.getParameter("mcuId");
        MCU mcu = mcuManger.getMCU(Integer.parseInt(mcuId));
        String mcuName = mcu.getSysID();
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          
        int supplierId = user.getRoleData().getSupplier().getId();

        List<Location> rootLocation = locationManager.getParentsBySupplierId(supplierId);
        String locationName = rootLocation.size() > 0 ? rootLocation.get(0).getName() : "";

        ModelAndView mav = new ModelAndView();
        mav.addObject("mcuName", mcuName);
        mav.addObject("supplierID", supplierId);
        mav.addObject("locationName", locationName);
        mav.setViewName("gadget/device/googleMapMax");

        return mav;
    }
}