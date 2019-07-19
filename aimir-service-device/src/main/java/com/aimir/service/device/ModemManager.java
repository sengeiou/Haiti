package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.aimir.model.device.ACD;
import com.aimir.model.device.Converter;
import com.aimir.model.device.HMU;
import com.aimir.model.device.IEIU;
import com.aimir.model.device.IHD;
import com.aimir.model.device.LTE;
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

@WebService(name="ModemService", targetNamespace="http://aimir.com/services")
public interface ModemManager {
	
	@WebMethod
	@WebResult(name="Modem")
	public Modem getModem(@WebParam(name ="modemId")Integer modemId);	
	@WebMethod(operationName="getModemByDeviceSerial")
	@WebResult(name="Modem")
	public Modem getModem(@WebParam(name ="deviceSerial")String deviceSerial);
	@WebMethod
	public int deleteModem(@WebParam(name ="modemId")Integer modemId);
	
	/**
	 * @method deleteModemStatus
	 * @param modemId
	 * @return
	 * 
	 * Modem의 상태를 바꾼다.
	 */
	@WebMethod
	public int deleteModemStatus(@WebParam(name ="modemId")int modemId, Code code);
	
	@WebMethod
	@WebResult(name="ModemByType")
	public Object getModemByType(@WebParam(name ="condition")Map<String, Object> condition) throws Exception;
	@WebMethod(operationName="listModemByType")
	@WebResult(name="ModemByTypeList")
	public List<Object> getModemByType(@WebParam(name ="condition")Map<String, Object> condition, 
			@WebParam(name ="supplierId")String supplierId);

	// MiniGadget
	@WebMethod
	@WebResult(name="MiniChart")
	public List<Object> getMiniChart(@WebParam(name ="condition")Map<String, Object> condition);
	
	public List<Object> getMiniChart2(@WebParam(name ="condition")Map<String, Object> condition);
	
	// MaxGadget
	@WebMethod
	@WebResult(name="ModemSearchChart")
	public List<Object> getModemSearchChart(@WebParam(name ="condition")Map<String, Object> condition);	
	@WebMethod
	@WebResult(name="ModemSearchGrid")
	public List<Object> getModemSearchGrid(@WebParam(name ="condition")Map<String, Object> condition);
	
	
	public List<Object> getModemSearchGrid2(@WebParam(name ="condition")Map<String, Object> condition, String gridType);
	
	
	@WebMethod
	@WebResult(name="ModemLogChart")
	public List<Object> getModemLogChart(@WebParam(name ="condition")Map<String, Object> condition);
	/*  모뎀가젯의 history탭 : 구현이 안되어 있는 부분이고 필요없는 기능이라 삭제
	 * @WebMethod
	@WebResult(name="ModemLogGrid")
	public List<Object> getModemLogGrid(@WebParam(name ="condition")Map<String, Object> condition);*/
	
	
	@WebMethod
	@WebResult(name="ModemSearchCondition")
	public Map<String, Object> getModemSearchCondition();
	
	// modemSerial에 대한 자동완성 
	@WebMethod
	@WebResult(name="ModemSerialList")
	public List<Object> getModemSerialList(@WebParam(name ="condition")Map<String, Object> condition);
	
	// modem 등록
	@WebMethod
	@WebResult(name="ModemMap")
	public Map<String, Object> insertModem(@WebParam(name ="modem")Modem modem);
	
	@WebMethod
	@WebResult(name="ModemZRU")
	public Map<String, Object> insertModemZRU(@WebParam(name ="zru")ZRU zru);
	@WebMethod
	@WebResult(name="ModemZEUPLS")
	public Map<String, Object> insertModemZEUPLS(@WebParam(name ="zeupls")ZEUPLS zeupls);
	@WebMethod
	@WebResult(name="ModemMMIU")
	public Map<String, Object> insertModemMMIU(@WebParam(name ="mmiu")MMIU mmiu);	
	@WebMethod
	@WebResult(name="ModemIEIU")
	public Map<String, Object> insertModemIEIU(@WebParam(name ="ieiu")IEIU ieiu);
	
	
	@WebMethod
	@WebResult(name="ModemZMU")
	public Map<String, Object> insertModemZMU(@WebParam(name ="zmu")ZMU zmu);
	@WebMethod
	@WebResult(name="ModemIHD")
	public Map<String, Object> insertModemIHD(@WebParam(name ="ihd")IHD ihd);
	@WebMethod
	@WebResult(name="ModemACD")
	public Map<String, Object> insertModemACD(@WebParam(name ="acd")ACD acd);
	@WebMethod
	@WebResult(name="ModemHMU")
	public Map<String, Object> insertModemHMU(@WebParam(name ="hmu")HMU hmu);
	@WebMethod
	@WebResult(name="ModemPLCIU")
	public Map<String, Object> insertModemPLCIU(@WebParam(name ="plciu")PLCIU plciu);
	
	
	@WebMethod
	@WebResult(name="ModemZEUMBus")
	public Map<String, Object> insertModemZEUMBus(@WebParam(name ="zeumBus")ZEUMBus zeumBus);
	@WebMethod
	@WebResult(name="ModemZBRepeater")
	public Map<String, Object> insertModemZBRepeater(@WebParam(name ="zbRepeater")ZBRepeater zbRepeater);
	@WebMethod
	@WebResult(name="ModemConverter")
	public Map<String, Object> insertModemConverter(@WebParam(name ="converter")Converter converter);
	@WebMethod
	@WebResult(name="ModemSubGiga")
	public Map<String, Object> insertModemSubGiga(@WebParam(name ="subGiga")SubGiga subGiga);
	@WebMethod
	@WebResult(name="ModemLTE")
	public Map<String, Object> insertModemLTE(@WebParam(name ="lte")LTE lte);
	
	//modem 수정
	@WebMethod
	@WebResult(name="ModemMap")
	public Map<String, Object> updateModem(@WebParam(name ="modem")Modem modem);
	@WebMethod
	@WebResult(name="ModemZRU")
	public Map<String, Object> updateModemZRU(@WebParam(name ="zru")ZRU zru);
	@WebMethod
	@WebResult(name="ModemZEUPLS")
	public Map<String, Object> updateModemZEUPLS(@WebParam(name ="zeupls")ZEUPLS zeupls);
	@WebMethod
	@WebResult(name="ModemMMIU")
	public Map<String, Object> updateModemMMIU(@WebParam(name ="mmiu")MMIU mmiu);	
	@WebMethod
	@WebResult(name="ModemIEIU")
	public Map<String, Object> updateModemIEIU(@WebParam(name ="ieiu")IEIU ieiu);
	
	
	@WebMethod
	@WebResult(name="ModemZMU")
	public Map<String, Object> updateModemZMU(@WebParam(name ="zmu")ZMU zmu);
	@WebMethod
	@WebResult(name="ModemIHD")
	public Map<String, Object> updateModemIHD(@WebParam(name ="ihd")IHD ihd);
	@WebMethod
	@WebResult(name="ModemACD")
	public Map<String, Object> updateModemACD(@WebParam(name ="acd")ACD acd);
	@WebMethod
	@WebResult(name="ModemHMU")
	public Map<String, Object> updateModemHMU(@WebParam(name ="hmu")HMU hmu);
	@WebMethod
	@WebResult(name="ModemPLCIU")
	public Map<String, Object> updateModemPLCIU(@WebParam(name ="plciu")PLCIU plciu);
	@WebMethod
	@WebResult(name="ModemZEUMBus")
	public Map<String, Object> updateModemZEUMBus(@WebParam(name ="zeumBus")ZEUMBus zeumBus);
	@WebMethod
	@WebResult(name="ModemZBRepeater")
	public Map<String, Object> updateModemZBRepeater(@WebParam(name ="zbRepeater")ZBRepeater zbRepeater);
	@WebMethod
	@WebResult(name="ModemConverter")
	public Map<String, Object> updateModemConverter(@WebParam(name ="converter")Converter converter);
	@WebMethod
	@WebResult(name="ModemSubGiga")
	public Map<String, Object> updateModemSubGiga(@WebParam(name ="subGiga")SubGiga subGiga);
	@WebMethod
	@WebResult(name="ModemLTE")
	public Map<String, Object> updateModemLTE(@WebParam(name ="lte")LTE lte);
	
	
	//modem 수정
	@WebMethod
	@WebResult(name="ModemScheduleZEUPLS")
	public Map<String, Object> updateModemScheduleZEUPLS(@WebParam(name ="zeupls")ZEUPLS zeupls);
	@WebMethod
	@WebResult(name="ModemScheduleZEUMBus")
	public Map<String, Object> updateModemScheduleZEUMBus(@WebParam(name ="zeumbus")ZEUMBus zeumbus);
	@WebMethod
	@WebResult(name="ModemScheduleZBRepeater")
	public Map<String, Object> updateModemScheduleZBRepeater(@WebParam(name ="zbRepeater")ZBRepeater zbRepeater);
	
	@WebMethod
	@WebResult(name="ModemListByMCUsysID")
	public List<Object> getModemListByMCUsysID(@WebParam(name ="sys_id")String sys_id);
	
	@WebMethod
	@WebResult(name="ModemIdListByDevice_serial")
	public List<Object> getModemIdListByDevice_serial(@WebParam(name ="device_serial")String device_serial);
	
	@WebMethod
	@WebResult(name="ConverterModem")
	public List<Converter> getConverterModem();
	
	@WebMethod
	@WebResult(name="update")
	public void update(Modem modem);
	
	/**
	 * 페이징처리 안된 모뎀 리스트를 구하는 메소드
	 * @param conditionMap
	 * @return Modem List
	 */
	public List<Object> getModemListExcel(Map<String, Object> conditionMap);
	
	public List<Object> getModemCommInfoListExcel(Map<String, Object> conditionMap);
	
	@WebMethod(operationName="setLocation")
    public @WebResult(name=("result")) int setLocation(@WebParam(name="ModemSerial") String deviceSerial,
            @WebParam(name="Address") String address, 
            @WebParam(name="GPS_X") double x, 
            @WebParam(name="GPS_Y") double y,
            @WebParam(name="GPS_Z") double z);
	
	public List<String> getFirmwareVersionList(Map<String, Object> condition);
	
	public List<String> getDeviceList(Map<String, Object> condition);

	public List<String> getDeviceListModem(Map<String, Object> condition);

	public List<String> getTargetList(Map<String, Object> condition);

	public List<String> getTargetListModem(Map<String, Object> condition);

	public List<Object> getModemList(Map<String, Object> condition);
	
	public List<Map<String, Object>> getParentDevice(Map<String, Object> condition);	//sp-1004

}




