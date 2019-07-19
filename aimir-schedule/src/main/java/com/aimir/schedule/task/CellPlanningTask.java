package com.aimir.schedule.task;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.model.device.MCU;
import com.aimir.model.device.ZEUPLS;

@Transactional
public class CellPlanningTask {
	private static Log log = LogFactory.getLog(CellPlanningTask.class);

	@Autowired
	SqliteDao sqliteDao;

	@Autowired
	MCUDao mcuDao;

	@Autowired
	ModemDao modemDao;

	public void excute() {
		List<Map<String, Object>> codiList = sqliteDao.getAtcCodi();

		for (Map<String, Object> codi : codiList) {

			String codiID = (String) codi.get("_id");

			Double gpioX = (Double) codi.get("longitude");
			Double gpioY = (Double) codi.get("latitude");

			gpioX = getTransfer(gpioX, 3);
			gpioY = getTransfer(gpioY, 2);

			List<MCU> mcuList = mcuDao.getMCUbyCodi(codiID);
			//log.debug("mcuList:" + mcuList);
			for (MCU mcu : mcuList) {
				mcu.setGpioX(gpioX);
				mcu.setGpioY(gpioY);
				mcu.setGpioZ(0d);
				mcu.setSysLocation(getAddress(gpioX,gpioY));
			
				mcuDao.update(mcu);
				List<Map<String, Object>> modemList = sqliteDao
						.getAtcCell(codiID);
				//log.debug("modemList:" + modemList);
				for (Map<String, Object> md : modemList) {
					String deviceSerial = (String) md.get("_id");
					if (deviceSerial != null && deviceSerial.length() > 0) {
						
						Double mgpioX = (Double) md.get("longitude");
						Double mgpioY = (Double) md.get("latitude");
						Integer lqi = (Integer) md.get("lqi");
						Integer rssi = (Integer) md.get("rssi");
						ZEUPLS modem = (ZEUPLS)modemDao.get(deviceSerial);
						
						if (modem != null) {
							modem.setMcu(mcu);
							mgpioX = getTransfer(mgpioX, 3);
							mgpioY = getTransfer(mgpioY, 2);
							//log.debug("mgpioX:" + mgpioX);
							//log.debug("mgpioY:" + mgpioY);
							modem.setGpioX(mgpioX);
							modem.setGpioY(mgpioY);
							modem.setGpioZ(0d);
							modem.setLQI(lqi);
							//log.debug("lqi:" + lqi);
							modem.setRssi(rssi);
							modem.setAddress(getAddress(mgpioX,mgpioY));
					
							modemDao.update(modem);
						}
					}
				}

			}
		}
	}

	private Double getTransfer(Double gpio, int delim) {
		String strGpioX = gpio.toString();
		BigDecimal multi = new BigDecimal(0);
		String prefix = strGpioX.substring(0, delim);

		BigDecimal sub = new BigDecimal(60);
		BigDecimal allValue = new BigDecimal(strGpioX);
		BigDecimal prefixValue = new BigDecimal(prefix);
		
		BigDecimal pfValue = new BigDecimal(0);

		if (gpio > 10000d) {
			if (delim == 3)
				multi = new BigDecimal(100);
			else if (delim == 2)
				multi = new BigDecimal(1000);
			pfValue = prefixValue.multiply(multi);

		} else if (gpio < 10000d && gpio > 1000d) {
			if (delim == 3)
				multi = new BigDecimal(10);
			else if (delim == 2)
				multi = new BigDecimal(100);
			pfValue = prefixValue.multiply(multi);
		} else if (gpio < 1000d && gpio > 100d) {
			if (delim == 2){
				multi = new BigDecimal(10);
			    pfValue = prefixValue.multiply(multi);
			}else{
				return gpio;
			}
		} else{
			return gpio;
		}

		BigDecimal subfixValue = allValue.subtract(pfValue);

		
		BigDecimal value = subfixValue
				.divide(sub, 10, BigDecimal.ROUND_CEILING).setScale(10,
						BigDecimal.ROUND_CEILING);

		
		return prefixValue.add(value).doubleValue();
	}
	
	public String getAddress(Double mgpioX,Double mgpioY){
		URL url;
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			url = new URL("http://maps.google.co.kr/maps/api/geocode/xml?latlng="+mgpioY+","+mgpioX+"&sensor=false");
			doc = builder.build(new InputStreamReader(url.openConnection()
					.getInputStream(), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
       
		Element root = doc.getRootElement();	
		Element result = root.getChild("result");	
		Element address = result.getChild("formatted_address");
		
		return address.getValue();
	}
	
	
}