package com.aimir.service.device.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.McuStatus;
import com.aimir.dao.device.EventAlertLogDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.system.Code;
import com.aimir.service.device.MapManager;
import com.aimir.util.ReflectionUtils;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Icon;
import de.micromata.opengis.kml.v_2_2_0.IconStyle;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LabelStyle;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.LookAt;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;
import de.micromata.opengis.kml.v_2_2_0.Style;
/*
 * 본 서비스는 JAK Java API for KML 을 기반으로 동작하는 서비스 입니다.
 * site : http://labs.micromata.de/display/jak/Home
 */
@Service(value="MapManager")
public class MapManagerImpl implements MapManager {

    Log log = LogFactory.getLog(MapManagerImpl.class);
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    EventAlertLogDao eventAlertLogDao;
    
    @Autowired
    MCUDao mcuDao;

    @Autowired
    ModemDao modemDao;

    @Autowired
    MeterDao meterDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Double getDistance(Double gpiox,Double gpioy,Double gpiox1,Double gpioy1){
    	
   	   Double radianLat1 = gpiox * ( Math.PI  / 180 );
 	   Double radianLng1 = gpioy * ( Math.PI  / 180 );
 	   Double radianLat2 = gpiox1 * ( Math.PI  / 180 );
 	   Double radianLng2 = gpioy1 * ( Math.PI  / 180 );
 	   // sort out the radius, MILES or KM?
 	   Double earth_radius = 6378.1*1000; // (km = 6378.1) OR (miles = 3959) - radius of the earth
 	 
 	   // sort our the differences
 	   Double diffLat =  ( radianLat1 - radianLat2 );
 	   Double diffLng =  ( radianLng1 - radianLng2 );
 	   // put on a wave (hey the earth is round after all)
 	   Double sinLat = Math.sin( diffLat / 2  );
 	   Double sinLng = Math.sin( diffLng / 2  ); 
 	 
 	   // maths - borrowed from http://www.opensourceconnections.com/wp-content/uploads/2009/02/clientsidehaversinecalculation.html
 	   Double a = Math.pow(sinLat, 2.0) + Math.cos(radianLat1) * Math.cos(radianLat2) * Math.pow(sinLng, 2.0);
 	 
 	   // work out the distance
 	   Double distance = earth_radius * 2 * Math.asin(Math.min(1, Math.sqrt(a)));
 	   
 	   //System.out.println("gpiox:"+gpiox+":gpioy:"+gpioy+":gpiox1:"+gpiox1+":gpioy1:"+gpioy1+":distance:"+distance);
 	   // return the distance
 	   return distance;
    }
    

    // MCU 전체를 KML로 반환하는 서비스
    public Kml getMCU(Integer supplierID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        Code deleteCode = codeDao.getCodeIdByCodeObject(McuStatus.Delete.getCode());
        int deleteCodeId = deleteCode.getId();
        
        condition.put("deleteCodeId", deleteCodeId);

        List<MCU> mcus = (List<MCU>) mcuDao.getMCUWithoutGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        /*
         * 카메라 설정 : 현재 설정이 장비위치에 따라 자동 이동이라 불필요
         * LookAt lookat = setLookAt();
         * document.setAbstractView(lookat);
         */
        /*
         * 스타일 추가 : 기본 스타일만 사용중임 : 필요시 추가
         * document.getStyleSelector().add(setStyle()); 
         */

        for (int i=0; i<mcus.size(); i++) {
            MCU mcu = mcus.get(i);
            String mcuSysID = mcu.getSysID()+"(DCU)";           
            String mcuAddress = mcu.getSysLocation();
            String mcuCoordinate;
            if ((mcu.getGpioX()!=null)&&(mcu.getGpioY()!=null)&&(mcu.getGpioZ()!=null)) {
                mcuCoordinate = mcu.getGpioX().toString() + "," + mcu.getGpioY().toString() + "," + mcu.getGpioZ().toString();
            } else {
                mcuCoordinate = "0.0, 0.0, 0.0";
            }
            if(mcu.getMcuCodi() != null){
            	
            	document.getFeature().add(addPoint(mcu.getMcuCodi().getCodiID()+"(CODI)",mcuSysID, mcuAddress, "codiIcon", mcuCoordinate));
            }else{
            	document.getFeature().add(addPoint(mcuSysID, mcuAddress, "codiIcon", mcuCoordinate));
            }
        }

        return kml;
    }
    
    // MCU 전체를 KML로 반환하는 서비스
    public Kml getMCU(Integer supplierID,Integer locationID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        Code deleteCode = codeDao.getCodeIdByCodeObject(McuStatus.Delete.getCode());
        int deleteCodeId = deleteCode.getId();

        condition.put("deleteCodeId", deleteCodeId);
        condition.put("supplier.id", supplierID);
        condition.put("location.id", locationID);

        List<MCU> mcus = (List<MCU>) mcuDao.getMCUWithoutGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        /*
         * 카메라 설정 : 현재 설정이 장비위치에 따라 자동 이동이라 불필요
         * LookAt lookat = setLookAt();
         * document.setAbstractView(lookat);
         */
        /*
         * 스타일 추가 : 기본 스타일만 사용중임 : 필요시 추가
         * document.getStyleSelector().add(setStyle()); 
         */

        for (int i=0; i<mcus.size(); i++) {
            MCU mcu = mcus.get(i);
            String mcuSysID = mcu.getSysID()+"(DCU)";           
            String mcuAddress = mcu.getSysLocation();
            String mcuCoordinate;
            if ((mcu.getGpioX()!=null)&&(mcu.getGpioY()!=null)&&(mcu.getGpioZ()!=null)) {
                mcuCoordinate = mcu.getGpioX().toString() + "," + mcu.getGpioY().toString() + "," + mcu.getGpioZ().toString();
            } else {
                mcuCoordinate = "0.0, 0.0, 0.0";
            }
            if(mcu.getMcuCodi() != null){
            	
            	document.getFeature().add(addPoint(mcu.getMcuCodi().getCodiID()+"(CODI)",mcuSysID, mcuAddress, "codiIcon", mcuCoordinate));
            }else{
            	document.getFeature().add(addPoint(mcuSysID, mcuAddress, "codiIcon", mcuCoordinate));
            }
        }

        return kml;
    }

    // MCU 개별을 KML로 반환하는 서비스
    public Kml getMCU(Integer supplierID, String sysID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        Code deleteCode = codeDao.getCodeIdByCodeObject(McuStatus.Delete.getCode());
        int deleteCodeId = deleteCode.getId();

        condition.put("deleteCodeId", deleteCodeId);
        condition.put("supplier.id", supplierID);
        condition.put("sysID", sysID);

        List<MCU> mcus = (List<MCU>) mcuDao.getMCUWithoutGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        /*
         * 카메라 설정 : 현재 설정이 장비위치에 따라 자동 이동이라 불필요
         * LookAt lookat = setLookAt();
         * document.setAbstractView(lookat);
         */
        /*
         * 스타일 추가 : 기본 스타일만 사용중임 : 필요시 추가
         * document.getStyleSelector().add(setStyle()); 
         */

        for (int i=0; i<mcus.size(); i++) {
            MCU mcu = mcus.get(i);
            String mcuSysID = mcu.getSysID()+"(DCU)";
            String mcuAddress = mcu.getSysLocation();
            String mcuCoordinate;

            if ((mcu.getGpioX()!=null)&&(mcu.getGpioY()!=null)&&(mcu.getGpioZ()!=null)) {
                mcuCoordinate = mcu.getGpioX().toString() + "," + mcu.getGpioY().toString() + "," + mcu.getGpioZ().toString();
            } else {
                mcuCoordinate = "0.0, 0.0, 0.0";
            }
            if(mcu.getMcuCodi() != null){
            	
            	document.getFeature().add(addPoint(mcu.getMcuCodi().getCodiID()+"(CODI)",mcuSysID, mcuAddress, "codiIcon", mcuCoordinate));
            }else{
            	document.getFeature().add(addPoint(mcuSysID, mcuAddress, "codiIcon", mcuCoordinate));
            }
           
        }

        return kml;
    }
    
 // MCU 개별을 KML로 반환하는 서비스
    public Kml getMCU(Integer supplierID, String sysID,Integer locationID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        Code deleteCode = codeDao.getCodeIdByCodeObject(McuStatus.Delete.getCode());
        int deleteCodeId = deleteCode.getId();

        condition.put("deleteCodeId", deleteCodeId);
        
        condition.put("supplier.id", supplierID);
        condition.put("sysID", sysID);
        condition.put("location.id", locationID);

        List<MCU> mcus = (List<MCU>) mcuDao.getMCUWithoutGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        /*
         * 카메라 설정 : 현재 설정이 장비위치에 따라 자동 이동이라 불필요
         * LookAt lookat = setLookAt();
         * document.setAbstractView(lookat);
         */
        /*
         * 스타일 추가 : 기본 스타일만 사용중임 : 필요시 추가
         * document.getStyleSelector().add(setStyle()); 
         */

        for (int i=0; i<mcus.size(); i++) {
            MCU mcu = mcus.get(i);
            String mcuSysID = mcu.getSysID()+"(DCU)";
           
            String mcuAddress = mcu.getSysLocation();
            String mcuCoordinate;
            if ((mcu.getGpioX()!=null)&&(mcu.getGpioY()!=null)&&(mcu.getGpioZ()!=null)) {
                mcuCoordinate = mcu.getGpioX().toString() + "," + mcu.getGpioY().toString() + "," + mcu.getGpioZ().toString();
            } else {
                mcuCoordinate = "0.0, 0.0, 0.0";
            }
            if(mcu.getMcuCodi() != null){
            	
            	document.getFeature().add(addPoint(mcu.getMcuCodi().getCodiID()+"(CODI)",mcuSysID, mcuAddress, "codiIcon", mcuCoordinate));
            }else{
            	document.getFeature().add(addPoint(mcuSysID, mcuAddress, "codiIcon", mcuCoordinate));
            }
           
        }

        return kml;
    }

    /**
     * method name : getMCUMapData<b/>
     * method Desc : Concentrator Management 맥스가젯의 위치정보 탭에서 맵정보를 조회한다.
     *
     * @param supplierID
     * @param sysID
     * @return
     */
    // MCU명을 클릭했을 때 실행되는 로직
    public Kml getMCUMapData(Integer supplierID, String sysID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);
        condition.put("sysID", sysID);

        List<MCU> mcus = (List<MCU>) mcuDao.getMCUMapDataWithoutGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);
        
        if (mcus != null && mcus.size() > 0) {
            MCU mcu = mcus.get(0);
            String mcuSysID = mcu.getSysID()+"(DCU)";
            Integer mcuId = mcu.getId();
            String mcuAddress = null;
            
            // 개행문자 변환
            if (mcu.getSysLocation() != null) {
                mcuAddress = mcu.getSysLocation().replaceAll("\r\n", "\n").replaceAll("\r", "\n").replaceAll("\n", "<br>");
            }

            String mcuCoordinate;
            if ((mcu.getGpioX()!=null)&&(mcu.getGpioY()!=null)&&(mcu.getGpioZ()!=null)) {
                mcuCoordinate = mcu.getGpioX().toString() + "," + mcu.getGpioY().toString() + "," + mcu.getGpioZ().toString();
            } else {
                mcuCoordinate = "0.0, 0.0, 0.0";
            }
            if(mcu.getMcuCodi() != null){
            	document.getFeature().add(addPoint(mcu.getMcuCodi().getCodiID()+"(CODI)",mcuSysID, mcuAddress, "codiIcon", mcuCoordinate, mcuId));
            }else{
            	document.getFeature().add(addPoint(mcuSysID, mcuAddress, "codiIcon", mcuCoordinate, mcuId));
            }
        }

        return kml;
    }

    public List<MCU> getMCUbyCodi(String codiID){
    	return mcuDao.getMCUbyCodi(codiID);
    }
    
 // MCU 개별을 KML로 반환하는 서비스
    public Kml getMCUWithCodi(Integer supplierID, String sysID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);
        
        if(sysID != null && sysID.length()>0)
        condition.put("sysID", sysID);

        List<MCU> mcus = (List<MCU>) mcuDao.getMCUWithGpioCodi(condition);
       
        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        /*
         * 카메라 설정 : 현재 설정이 장비위치에 따라 자동 이동이라 불필요
         * LookAt lookat = setLookAt();
         * document.setAbstractView(lookat);
         */
        /*
         * 스타일 추가 : 기본 스타일만 사용중임 : 필요시 추가
         * document.getStyleSelector().add(setStyle()); 
         */

        for (int i=0; i<mcus.size(); i++) {
            MCU mcu = mcus.get(i);
            String mcuSysID = mcu.getMcuCodi().getCodiID()+"(CODI)";
             
            String mcuAddress = mcu.getSysLocation();
            String mcuCoordinate;
            if ((mcu.getGpioX()!=null)&&(mcu.getGpioY()!=null)&&(mcu.getGpioZ()!=null)) {
                mcuCoordinate = mcu.getGpioX().toString() + "," + mcu.getGpioY().toString() + "," + mcu.getGpioZ().toString();
            } else {
                mcuCoordinate = "0.0, 0.0, 0.0";
            }
            document.getFeature().add(addPoint(mcu.getSysID()+"(DCU)",mcuSysID, mcuAddress, "codiIcon", mcuCoordinate));
        }

        return kml;
    }
    
   
    
 // MCU 개별을 KML로 반환하는 서비스
    public Kml getMCUWithCodi(Integer supplierID, String sysID,Integer locationID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);
        
        if(locationID !=-1)
        condition.put("location.id", locationID);
        
        if(sysID != null && sysID.length()>0)
        condition.put("sysID", sysID);

        List<MCU> mcus = (List<MCU>) mcuDao.getMCUWithGpioCodi(condition);
       
        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        /*
         * 카메라 설정 : 현재 설정이 장비위치에 따라 자동 이동이라 불필요
         * LookAt lookat = setLookAt();
         * document.setAbstractView(lookat);
         */
        /*
         * 스타일 추가 : 기본 스타일만 사용중임 : 필요시 추가
         * document.getStyleSelector().add(setStyle()); 
         */

        for (int i=0; i<mcus.size(); i++) {
            MCU mcu = mcus.get(i);
            String mcuSysID = mcu.getMcuCodi().getCodiID()+"(CODI)";
             
            String mcuAddress = mcu.getSysLocation();
            String mcuCoordinate;
            if ((mcu.getGpioX()!=null)&&(mcu.getGpioY()!=null)&&(mcu.getGpioZ()!=null)) {
                mcuCoordinate = mcu.getGpioX().toString() + "," + mcu.getGpioY().toString() + "," + mcu.getGpioZ().toString();
            } else {
                mcuCoordinate = "0.0, 0.0, 0.0";
            }
            document.getFeature().add(addPoint(mcu.getSysID()+"(DCU)",mcuSysID, mcuAddress, "codiIcon", mcuCoordinate));
        }

        return kml;
    }


    // MCU 개별과 연계된 정보 출력
    public Kml getMCUWithRelativeDevice(Integer supplierID, String sysID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);
        condition.put("sysID", sysID);

        List<MCU> mcus = (List<MCU>) mcuDao.getMCUWithGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        /*
         * 카메라 설정 : 현재 설정이 장비위치에 따라 자동 이동이라 불필요
         * LookAt lookat = setLookAt();
         * document.setAbstractView(lookat);
         */
        /*
         * 스타일 추가 : 기본 스타일만 사용중임 : 필요시 추가
         * document.getStyleSelector().add(setStyle()); 
         */

        for (int i=0; i<mcus.size(); i++) {
            MCU mcu = mcus.get(i);
            String mcuSysID = mcu.getSysID()+"(DCU)";
            String mcuAddress = mcu.getSysLocation();
            String mcuCoordinate= mcu.getGpioX().toString() + "," + mcu.getGpioY().toString() + "," + mcu.getGpioZ().toString();
            document.getFeature().add(addPoint(mcuSysID, mcuAddress, "codiIcon", mcuCoordinate));
            
            List<Modem> modems = (List<Modem>) modemDao.getModemHavingMCU(mcu.getId());
            for (int j=0; j<modems.size(); j++) {
                Modem modem = modems.get(j);
                String deviceSerial = modem.getDeviceSerial();
                String modemAddress = modem.getAddress();
                String modemCoordinate= modem.getGpioX().toString() + "," + modem.getGpioY().toString() + "," + modem.getGpioZ().toString();
                Integer modemId = modem.getId();
                
                Double distance = this.getDistance(modem.getGpioY(), modem.getGpioX(), mcu.getGpioY(), mcu.getGpioX());
                document.getFeature().add(addPoint(deviceSerial+"(Modem)", modemAddress, "modemIcon", modemCoordinate,distance, modemId));
                document.getFeature().add(addLineString(deviceSerial+"(Modem)", distance+"",mcuCoordinate, modemCoordinate));
                
                
                List<Meter> meters = (List<Meter>) meterDao.getMeterHavingModem(modem.getId());
                
                for (int k=0; k<meters.size(); k++) {
                    Meter meter = meters.get(k);
                    String mdsID = meter.getMdsId();
                    String meterAddress = meter.getAddress();
                    String meterCoordinate= meter.getGpioX().toString() + "," + meter.getGpioY().toString() + "," + meter.getGpioZ().toString();
                    Double distance1 = this.getDistance(meter.getGpioY(), meter.getGpioX(), modem.getGpioY(), modem.getGpioX());
                    document.getFeature().add(addPoint(mdsID+"(Meter)", meterAddress, "meterIcon", meterCoordinate,distance1));
                    document.getFeature().add(addLineString(mdsID+"(Meter)", distance1+"",modemCoordinate, meterCoordinate));
                }
            }
        }

        return kml;
    }
    
    // Relative Information 버튼 클릭했을 때 실행되는 메소드
    // MCU 개별과 연계된 정보 출력
	public Kml getMCUWithRelativeModem(Integer supplierID, String sysID) {
		Kml kml = new Kml();

		try {
			HashMap<String, Object> condition = new HashMap<String, Object>();
			condition.put("supplier.id", supplierID);
			condition.put("sysID", sysID);

			List<MCU> mcus = (List<MCU>) mcuDao.getMCUWithGpio(condition);
			Document document = new Document();
			kml.setFeature(document);

			/*
			 * 카메라 설정 : 현재 설정이 장비위치에 따라 자동 이동이라 불필요 LookAt lookat = setLookAt();
			 * document.setAbstractView(lookat);
			 *
			 * 스타일 추가 : 기본 스타일만 사용중임 : 필요시 추가
			 * document.getStyleSelector().add(setStyle());
			 */

			for (int i = 0; i < mcus.size(); i++) {
				MCU mcu = mcus.get(i);
				String mcuSysID = mcu.getSysID() + "(DCU)";
				Integer mcuId = mcu.getId();

				String mcuAddress = mcu.getSysLocation();
				String mcuCoordinate = mcu.getGpioX().toString() + "," + mcu.getGpioY().toString() + "," + mcu.getGpioZ().toString();

				if (mcu.getMcuCodi() != null) {
					document.getFeature().add(addPoint(mcu.getMcuCodi().getCodiID() + "(CODI)", mcuSysID, mcuAddress, "codiIcon", mcuCoordinate, mcuId));
				} else {
					document.getFeature().add(addPoint(mcuSysID, mcuAddress, "codiIcon", mcuCoordinate, mcuId));
				}

				List<Modem> modems = (List<Modem>) modemDao.getModemHavingMCU(mcu.getId());

				for (int j = 0; j < modems.size(); j++) {
					Modem modem = modems.get(j);
					String deviceSerial = modem.getDeviceSerial();
					String modemAddress = modem.getAddress();
					String modemCoordinate = null;

					if ((modem.getGpioX() != null) && (modem.getGpioY() != null) && (modem.getGpioZ() != null)) {
						modemCoordinate = modem.getGpioX().toString() + "," + modem.getGpioY().toString() + "," + modem.getGpioZ().toString();
					}

					Integer modemId = modem.getId();
					Double distance = this.getDistance(modem.getGpioY(), modem.getGpioX(), mcu.getGpioY(), mcu.getGpioX());

					// [ 토폴로지 정보 표출시 좌표 활용 ]
					// 1. Modem 토폴로지 정보 표현시, 미터 좌표값이 있으면 미터 좌표값을 우선순위로 적용
					// 2. 미터 좌표값이 없으면 모뎀 좌표값으로 표현
					List<Meter> meters = meterDao.getMeterHavingModem(modem.getId());
					if (meters.size() != 0) { // modem에 물려있는 meter가 있을 경우
						Meter meter = meters.get(0);
						String meterCoordinate = null;

						if ((meter.getGpioX() != null) && (meter.getGpioY() != null) && (meter.getGpioZ() != null)) {
							meterCoordinate = meter.getGpioX().toString() + "," + meter.getGpioY().toString() + "," + meter.getGpioZ().toString();
							modemCoordinate = meterCoordinate;
						}
					}

					document.getFeature().add(addPoint(deviceSerial + "(Modem)", modemAddress, "modemIcon", modemCoordinate, distance, modem, modemId));
					Modem parentModem = modem.getModem();

					if (parentModem != null && parentModem.getGpioX() != null && parentModem.getGpioY() != null) {
						List<String> coordiList = new ArrayList<String>();
						coordiList.add(modemCoordinate);

						while (parentModem != null) {
							// modemId == parentModem.getId() 			 : 자기자신을 부모 노드로 재귀시키는 경우
							// modemId == parentModem.getParentModemId() : 부모 노드 호출이 반복되는 Cycle이 형성되는 경우
							if (modemId == parentModem.getId() || modemId == parentModem.getParentModemId()) {
								parentModem = null;
								continue;
							}

							String parentModemCoordinate = null;
							if ((parentModem.getGpioX() != null) && (parentModem.getGpioY() != null) && (parentModem.getGpioZ() != null)) {
								parentModemCoordinate = parentModem.getGpioX().toString() + "," + parentModem.getGpioY().toString() + "," + parentModem.getGpioZ().toString();
							}
							
							String parentModemDeviceSerial = parentModem.getDeviceSerial() + "(Modem)";
							String parentModemAddress = parentModem.getAddress();
							Integer parentModemId = parentModem.getId();

							// [ 토폴로지 정보 표출시 좌표 활용 ]
							// 1. Modem 토폴로지 정보 표현시, 미터 좌표값이 있으면 미터 좌표값을 우선순위로 적용
							// 2. 미터 좌표값이 없으면 모뎀 좌표값으로 표현
							List<Meter> parentMeters = meterDao.getMeterHavingModem(parentModem.getId());
							if (parentMeters.size() != 0) { // modem에 물려있는 meter가 있을 경우
								Meter meter = parentMeters.get(0);
								String meterCoordinate = null;

								if ((meter.getGpioX() != null) && (meter.getGpioY() != null) && (meter.getGpioZ() != null)) {
									meterCoordinate = meter.getGpioX().toString() + "," + meter.getGpioY().toString() + "," + meter.getGpioZ().toString();
									parentModemCoordinate = meterCoordinate;
								}
							}

							document.getFeature().add(addPoint(parentModemDeviceSerial, parentModemAddress, "parent_modemIcon", parentModemCoordinate, parentModemId));
							coordiList.add(parentModemCoordinate);
							parentModem = parentModem.getModem();
						}

						coordiList.add(mcuCoordinate);
						document.getFeature().add(addLineString(deviceSerial + "(Modem)", distance + "", coordiList));
					} else {
						document.getFeature().add(addLineString(deviceSerial + "(Modem)", distance + "", mcuCoordinate, modemCoordinate));
					}
				}
			}
		} catch (Exception e) {
			log.error(e, e);
		}

		return kml;
	}
    
    // MCU 개별과 연계된 정보 출력
    public Kml getMCUCodiWithRelativeModem(Integer supplierID, String sysID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);
        condition.put("sysID", sysID);

        List<MCU> mcus = (List<MCU>) mcuDao.getMCUWithGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        /*
         * 카메라 설정 : 현재 설정이 장비위치에 따라 자동 이동이라 불필요
         * LookAt lookat = setLookAt();
         * document.setAbstractView(lookat);
         */
        /*
         * 스타일 추가 : 기본 스타일만 사용중임 : 필요시 추가
         * document.getStyleSelector().add(setStyle()); 
         */

        for (int i=0; i<mcus.size(); i++) {
            MCU mcu = mcus.get(i);
            
            
            String mcuSysID = mcu.getMcuCodi().getCodiID()+"(CODI)";
                     
            String mcuAddress = mcu.getSysLocation();
            String mcuCoordinate= mcu.getGpioX().toString() + "," + mcu.getGpioY().toString() + "," + mcu.getGpioZ().toString();
            document.getFeature().add(addPoint(mcu.getSysID()+"(DCU)",mcuSysID, mcuAddress, "codiIcon", mcuCoordinate));
           
            List<Modem> modems = (List<Modem>) modemDao.getModemHavingMCU(mcu.getId());
         
            for (int j=0; j<modems.size(); j++) {
                Modem modem = modems.get(j);
                String deviceSerial = modem.getDeviceSerial();
                String modemAddress = modem.getAddress();
                String modemCoordinate= modem.getGpioX().toString() + "," + modem.getGpioY().toString() + "," + modem.getGpioZ().toString();
                Integer modemId = modem.getId();
                Double distance = this.getDistance(modem.getGpioY(), modem.getGpioX(), mcu.getGpioY(), mcu.getGpioX());
                document.getFeature().add(addPoint(deviceSerial+"(Modem)", modemAddress, "modemIcon", modemCoordinate,distance,modem, modemId));
                document.getFeature().add(addLineString(deviceSerial+"(Modem)", distance+"",mcuCoordinate, modemCoordinate));
                
                
//                List<Meter> meters = (List<Meter>) meterDao.getMeterHavingModem(modem.getId());
//                
//                for (int k=0; k<meters.size(); k++) {
//                    Meter meter = meters.get(k);
//                    String mdsID = meter.getMdsId();
//                    String meterAddress = meter.getAddress();
//                    String meterCoordinate= meter.getGpioX().toString() + "," + meter.getGpioY().toString() + "," + meter.getGpioZ().toString();
//                    Double distance1 = this.getDistance(meter.getGpioY(), meter.getGpioX(), modem.getGpioY(), modem.getGpioX());
//                    document.getFeature().add(addPoint(mdsID+"(Meter)", meterAddress, null, meterCoordinate,distance1));
//                    document.getFeature().add(addLineString(mdsID+"(Meter)", distance1+"",modemCoordinate, meterCoordinate));
//                }
            }
        }

        return kml;
    }


    // Meter 전체를 KML로 반환하는 서비스
    public Kml getMeter(Integer supplierID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);

        List<Meter> meters = (List<Meter>) meterDao.getMeterWithoutGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        /*
         * 카메라 설정 : 현재 설정이 장비위치에 따라 자동 이동이라 불필요
         * LookAt lookat = setLookAt();
         * document.setAbstractView(lookat);
         */
        /*
         * 스타일 추가 : 기본 스타일만 사용중임 : 필요시 추가
         * document.getStyleSelector().add(setStyle()); 
         */

        for (int i=0; i<meters.size(); i++) {
            Meter meter = meters.get(i);
            String mdsID = meter.getMdsId()+"(Meter)";
            String address = meter.getAddress();
            String coordinate;
            if ((meter.getGpioX()!=null)&&(meter.getGpioY()!=null)&&(meter.getGpioZ()!=null)) {
                coordinate = meter.getGpioX().toString() + "," + meter.getGpioY().toString() + "," + meter.getGpioZ().toString();
            } else {
                coordinate = "0.0, 0.0, 0.0";
            }
            document.getFeature().add(addPoint(mdsID, address, "meterIcon", coordinate));
        }

        return kml;
    }

    // Meter 개별을 KML로 반환하는 서비스
    public Kml getMeter(Integer supplierID, String mdsID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);
        condition.put("mdsId", mdsID);

        List<Meter> meters = (List<Meter>) meterDao.getMeterWithoutGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        /*
         * 카메라 설정 : 현재 설정이 장비위치에 따라 자동 이동이라 불필요
         * LookAt lookat = setLookAt();
         * document.setAbstractView(lookat);
         */
        /*
         * 스타일 추가 : 기본 스타일만 사용중임 : 필요시 추가
         * document.getStyleSelector().add(setStyle()); 
         */

        for (int i=0; i<meters.size(); i++) {
            Meter meter = meters.get(i);
            String meterMdsID = meter.getMdsId()+"(Meter)";
            String meterAddress = meter.getAddress();
            String meterCoordinate;
            if ((meter.getGpioX()!=null)&&(meter.getGpioY()!=null)&&(meter.getGpioZ()!=null)) {
                meterCoordinate = meter.getGpioX().toString() + "," + meter.getGpioY().toString() + "," + meter.getGpioZ().toString();
            } else {
                meterCoordinate = "0.0, 0.0, 0.0";
            }
            document.getFeature().add(addPoint(meterMdsID, meterAddress, "meterIcon", meterCoordinate));
        }

        return kml;
    }
    
 // Meter 개별을 KML로 반환하는 서비스
    public Kml getMeter(Integer supplierID, Integer locationID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);
        condition.put("location.id", locationID);

        List<Meter> meters = (List<Meter>) meterDao.getMeterWithoutGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        /*
         * 카메라 설정 : 현재 설정이 장비위치에 따라 자동 이동이라 불필요
         * LookAt lookat = setLookAt();
         * document.setAbstractView(lookat);
         */
        /*
         * 스타일 추가 : 기본 스타일만 사용중임 : 필요시 추가
         * document.getStyleSelector().add(setStyle()); 
         */

        for (int i=0; i<meters.size(); i++) {
            Meter meter = meters.get(i);
            String meterMdsID = meter.getMdsId()+"(Meter)";
            String meterAddress = meter.getAddress();
            String meterCoordinate;
            if ((meter.getGpioX()!=null)&&(meter.getGpioY()!=null)&&(meter.getGpioZ()!=null)) {
                meterCoordinate = meter.getGpioX().toString() + "," + meter.getGpioY().toString() + "," + meter.getGpioZ().toString();
            } else {
                meterCoordinate = "0.0, 0.0, 0.0";
            }
            document.getFeature().add(addPoint(meterMdsID, meterAddress, "meterIcon", meterCoordinate));
        }

        return kml;
    }
    
 // Meter 개별을 KML로 반환하는 서비스
    public Kml getMeter(Integer supplierID, String mdsID,Integer locationID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);
        condition.put("mdsId", mdsID);
        condition.put("location.id", locationID);

        List<Meter> meters = (List<Meter>) meterDao.getMeterWithoutGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        /*
         * 카메라 설정 : 현재 설정이 장비위치에 따라 자동 이동이라 불필요
         * LookAt lookat = setLookAt();
         * document.setAbstractView(lookat);
         */
        /*
         * 스타일 추가 : 기본 스타일만 사용중임 : 필요시 추가
         * document.getStyleSelector().add(setStyle()); 
         */

        for (int i=0; i<meters.size(); i++) {
            Meter meter = meters.get(i);
            String meterMdsID = meter.getMdsId()+"(Meter)";
            String meterAddress = meter.getAddress();
            String meterCoordinate;
            if ((meter.getGpioX()!=null)&&(meter.getGpioY()!=null)&&(meter.getGpioZ()!=null)) {
                meterCoordinate = meter.getGpioX().toString() + "," + meter.getGpioY().toString() + "," + meter.getGpioZ().toString();
            } else {
                meterCoordinate = "0.0, 0.0, 0.0";
            }
            document.getFeature().add(addPoint(meterMdsID, meterAddress, "meterIcon", meterCoordinate));
        }

        return kml;
    }

    /**
     * method name : getMeterMapData<b/>
     * method Desc : Meter Management 맥스가젯의 위치정보 탭에서 맵정보를 조회한다.
     *
     * @param supplierID
     * @param mdsID
     * @return
     */
    public Kml getMeterMapData(Integer supplierID, String mdsID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);
        condition.put("mdsId", mdsID);

        List<Meter> meters = (List<Meter>) meterDao.getMeterMapDataWithoutGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        for (int i = 0 ; i < meters.size() ; i++) {
            Meter meter = meters.get(i);
            Integer meterId = meter.getId();
            String meterMdsID = meter.getMdsId()+"(Meter)";
            String meterAddress = null;

            // 개행문자 변환
            if (meter.getAddress() != null) {
                meterAddress = meter.getAddress().replaceAll("\r\n", "\n").replaceAll("\r", "\n").replaceAll("\n", "<br>");
            }

            String meterCoordinate;
            if ((meter.getGpioX()!=null)&&(meter.getGpioY()!=null)&&(meter.getGpioZ()!=null)) {
                meterCoordinate = meter.getGpioX().toString() + "," + meter.getGpioY().toString() + "," + meter.getGpioZ().toString();
            } else {
                meterCoordinate = "0.0, 0.0, 0.0";
            }
            // document.getFeature().add(addPoint(meterMdsID, meterAddress, "meterIcon", meterCoordinate));
            document.getFeature().add(addPointWithTargetId(meterMdsID, meterAddress, "meterIcon", meterCoordinate, meterId));
        }

        return kml;
    }

    // meter - relative Info
    // Meter 개별과 연계된 정보 출력
	public Kml getMeterWithRelativeDevice(Integer supplierID, String mdsID) {
		Kml kml = new Kml();

		try {
			HashMap<String, Object> condition = new HashMap<String, Object>();
			condition.put("supplier.id", supplierID);
			condition.put("mdsId", mdsID);

			List<Meter> meters = (List<Meter>) meterDao.getMeterWithGpio(condition);

			Document document = new Document();
			kml.setFeature(document);

			/*
			 * 카메라 설정 : 현재 설정이 장비위치에 따라 자동 이동이라 불필요 LookAt lookat = setLookAt();
			 * document.setAbstractView(lookat);
			 */
			/*
			 * 스타일 추가 : 기본 스타일만 사용중임 : 필요시 추가
			 * document.getStyleSelector().add(setStyle());
			 */

			for (int i = 0; i < meters.size(); i++) {
				Meter meter = meters.get(i);
				Integer meterId = meter.getId();
				String meterMdsID = meter.getMdsId() + "(Meter)";
				String meterAddress = meter.getAddress();
				String meterCoordinate = meter.getGpioX().toString() + "," + meter.getGpioY().toString() + "," + meter.getGpioZ().toString();

				Modem modem = meter.getModem();
				String modemDeviceSerial = modem.getDeviceSerial() + "(Modem)";
				String modemAddress = modem.getAddress();
				Integer modemId = modem.getId();

				Modem parentModem = modem.getModem();
				String lineOption = "OnlyLine";
				String modemCoordinate = null;
				String mcuCoordinate = null;

				MCU mcu = modem.getMcu();
				Integer mcuId = mcu.getId();
				String mcuSysID = mcu.getSysID() + "(DCU)";
				String mcuAddress = mcu.getSysLocation();

				if ((modem.getGpioX() != null) && (modem.getGpioY() != null) && (modem.getGpioZ() != null)) {
					// Polygon, PolyLine 설정 : OnlyLine = PolyLine
					// Google Maps에서 제공하는 Polygon 기능을 해제한다.
					// String lineOption = "OnlyLine";

					// [ 토폴로지 정보 표출시 좌표 활용 ]
					// Modem 토폴로지 정보 표현시, 미터 좌표값이 있으면 미터 좌표값을 우선순위로 적용
					modemCoordinate = meterCoordinate;
					document.getFeature().add(addPoint(modemDeviceSerial, modemAddress, "modemIcon", modemCoordinate, modemId));
					document.getFeature().add(addLineString(modemDeviceSerial, modemAddress, meterCoordinate, modemCoordinate, lineOption));

					if ((mcu.getGpioX() != null) && (mcu.getGpioY() != null) && (mcu.getGpioZ() != null)) {
						mcuCoordinate = mcu.getGpioX().toString() + "," + mcu.getGpioY().toString() + "," + mcu.getGpioZ().toString();
						document.getFeature().add(addPoint(mcuSysID, mcuAddress, "codiIcon", mcuCoordinate, mcuId));
					}
				}

				// 모뎀이 (모)모뎀을 갖고 있을 경우
				if (parentModem != null && parentModem.getGpioX() != null && parentModem.getGpioY() != null) {
					String parentModemCoordinate = parentModem.getGpioX().toString() + "," + parentModem.getGpioY().toString() + "," + parentModem.getGpioZ().toString();
					String parentModemDeviceSerial = parentModem.getDeviceSerial() + "(Modem)";
					String parentModemAddress = parentModem.getAddress();
					Integer parentModemId = parentModem.getId();

					while (parentModem != null) {
						// modemId == parentModem.getId() 			 : 자기자신을 부모 노드로 재귀시키는 경우
						// modemId == parentModem.getParentModemId() : 부모 노드 호출이 반복되는 Cycle이 형성되는 경우
						if (modemId == parentModem.getId() || modemId == parentModem.getParentModemId()) {
							parentModem = null;
							continue;
						}

						document.getFeature().add(addPoint(parentModemDeviceSerial, parentModemAddress, "parent_modemIcon", parentModemCoordinate, parentModemId));
						document.getFeature().add(addLineString(modemDeviceSerial, modemAddress, modemCoordinate, parentModemCoordinate, lineOption)); // Print Line: (자)모뎀->(모)모뎀
						document.getFeature().add(addLineString(parentModemDeviceSerial, parentModemAddress, parentModemCoordinate, mcuCoordinate, lineOption)); // Print Line: (모)모뎀->집중기 

						parentModem = parentModem.getModem();
					}
				} else {
					document.getFeature().add(addLineString(mcuSysID, mcuAddress, mcuCoordinate, modemCoordinate, lineOption));
				}

				document.getFeature().add(addPointWithTargetId(meterMdsID, meterAddress, "meterIcon", meterCoordinate, meterId));
			}
		} catch (Exception e) {
			log.error(e, e);
		}

		return kml;
	}

    // Modem 전체를 KML로 반환하는 서비스
    public Kml getModem(Integer supplierID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);

        List<Modem> modems = (List<Modem>) modemDao.getModemWithoutGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        //LookAt lookat = setLookAt();
        //document.setAbstractView(lookat);
        //document.getStyleSelector().add(setStyle());

        for (int i=0; i<modems.size(); i++) {
            Modem modem = modems.get(i);
            String modemDeviceSerial = modem.getDeviceSerial()+"(Modem)";
            String modemAddress = modem.getAddress();
            String modemCoordinate;
            Integer modemId = modem.getId();
            
            if ((modem.getGpioX()!=null)&&(modem.getGpioY()!=null)&&(modem.getGpioZ()!=null)) {
                modemCoordinate = modem.getGpioX().toString() + "," + modem.getGpioY().toString() + "," + modem.getGpioZ().toString();
            } else {
                modemCoordinate = "0.0, 0.0, 0.0";
            }
            document.getFeature().add(addPoint(modemDeviceSerial, modemAddress, "modemIcon", modemCoordinate, modemId));
        }

        return kml;
    }

    // Modem 개별을 KML로 반환하는 서비스
    public Kml getModem(Integer supplierID, String deviceSerial) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);
        condition.put("deviceSerial", deviceSerial);

        List<Modem> modems = (List<Modem>) modemDao.getModemWithoutGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        //LookAt lookat = setLookAt();
        //document.setAbstractView(lookat);
        //document.getStyleSelector().add(setStyle());

        for (int i=0; i<modems.size(); i++) {
            Modem modem = modems.get(i);
            String modemDeviceSerial = modem.getDeviceSerial()+"(Modem)";
            String modemAddress = modem.getAddress();
            String modemCoordinate;
            Integer modemId = modem.getId();
            
            if ((modem.getGpioX()!=null)&&(modem.getGpioY()!=null)&&(modem.getGpioZ()!=null)) {
                modemCoordinate = modem.getGpioX().toString() + "," + modem.getGpioY().toString() + "," + modem.getGpioZ().toString();
            } else {
                modemCoordinate = "0.0, 0.0, 0.0";
            }
            document.getFeature().add(addPoint(modemDeviceSerial, modemAddress, "modemIcon", modemCoordinate, modemId));
        }

        return kml;
    }
    
 // Modem 개별을 KML로 반환하는 서비스
    public Kml getModem(Integer supplierID, Integer locationID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);
        condition.put("location.id", locationID);

        List<Modem> modems = (List<Modem>) modemDao.getModemWithoutGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        //LookAt lookat = setLookAt();
        //document.setAbstractView(lookat);
        //document.getStyleSelector().add(setStyle());

        for (int i=0; i<modems.size(); i++) {
            Modem modem = modems.get(i);
            String modemDeviceSerial = modem.getDeviceSerial()+"(Modem)";
            String modemAddress = modem.getAddress();
            String modemCoordinate;
            Integer modemId = modem.getId();
            
            if ((modem.getGpioX()!=null)&&(modem.getGpioY()!=null)&&(modem.getGpioZ()!=null)) {
                modemCoordinate = modem.getGpioX().toString() + "," + modem.getGpioY().toString() + "," + modem.getGpioZ().toString();
            } else {
                modemCoordinate = "0.0, 0.0, 0.0";
            }
            document.getFeature().add(addPoint(modemDeviceSerial, modemAddress, "modemIcon", modemCoordinate, modemId));
        }

        return kml;
    }
    
    // Modem 개별을 KML로 반환하는 서비스
    public Kml getModem(Integer supplierID, String deviceSerial,Integer locationID) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);
        condition.put("deviceSerial", deviceSerial);
        condition.put("loaction.id", locationID);

        List<Modem> modems = (List<Modem>) modemDao.getModemWithoutGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);

        //LookAt lookat = setLookAt();
        //document.setAbstractView(lookat);
        //document.getStyleSelector().add(setStyle());

        for (int i=0; i<modems.size(); i++) {
            Modem modem = modems.get(i);
            String modemDeviceSerial = modem.getDeviceSerial()+"(Modem)";
            String modemAddress = modem.getAddress();
            String modemCoordinate;
            Integer modemId = modem.getId();
            
            if ((modem.getGpioX()!=null)&&(modem.getGpioY()!=null)&&(modem.getGpioZ()!=null)) {
                modemCoordinate = modem.getGpioX().toString() + "," + modem.getGpioY().toString() + "," + modem.getGpioZ().toString();
            } else {
                modemCoordinate = "0.0, 0.0, 0.0";
            }
            document.getFeature().add(addPoint(modemDeviceSerial, modemAddress, "modemIcon", modemCoordinate, modemId));
        }

        return kml;
    }

    /**
     * method name : getModemMapData<b/>
     * method Desc : Modem Management 맥스가젯의 위치정보 탭에서 맵정보를 조회한다.
     *
     * @param supplierID
     * @param deviceSerial
     * @return
     */
    public Kml getModemMapData(Integer supplierID, String deviceSerial) {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);
        condition.put("deviceSerial", deviceSerial);

        List<Modem> modems = (List<Modem>) modemDao.getModemMapDataWithoutGpio(condition);

        Kml kml = new Kml();
        Document document = new Document();
        kml.setFeature(document);
        
        for (int i = 0 ; i < modems.size() ; i++) {
            Modem modem = modems.get(i);
            String modemDeviceSerial = modem.getDeviceSerial()+"(Modem)";
            String modemAddress = null;
            Integer modemId = modem.getId();
            
            // 개행문자 변환
            if (modem.getAddress() != null) {
                modemAddress = modem.getAddress().replaceAll("\r\n", "\n").replaceAll("\r", "\n").replaceAll("\n", "<br>");
            }
          
       	 	// [ 토폴로지 정보 표출시 좌표 활용 ] 
            // 1. Modem 토폴로지 정보 표현시, 미터 좌표값이 있으면 미터 좌표값을 우선순위로 적용
			// 2. 미터 좌표값이 없으면 모뎀 좌표값으로 표현
            String modemCoordinate = null;
            if ((modem.getGpioX() != null) && (modem.getGpioY() != null) && (modem.getGpioZ() != null)) {
            	modemCoordinate = modem.getGpioX().toString() + "," + modem.getGpioY().toString() + "," + modem.getGpioZ().toString();
            	
            	List<Meter> meters = meterDao.getMeterHavingModem(modem.getId());
            	
            	if (meters.size() != 0) { // modem에 물려있는 meter가 있을 경우
	            	Meter meter = meters.get(0);
	                String meterCoordinate = null;
	                
	                if ((meter.getGpioX()!=null)&&(meter.getGpioY()!=null)&&(meter.getGpioZ()!=null)) {
	                	meterCoordinate = meter.getGpioX().toString() + "," + meter.getGpioY().toString() + "," + meter.getGpioZ().toString();
	                	modemCoordinate = meterCoordinate;
	                }
            	}
            } else {
                modemCoordinate = "0.0, 0.0, 0.0";
            }
            
            document.getFeature().add(addPoint(modemDeviceSerial, modemAddress, "modemIcon", modemCoordinate, modemId));
        }
        
        return kml;
    }
    
    // Modem 개별과 연계된 정보 출력
	public Kml getModemWithRelativeDevice(Integer supplierID, String deviceSerial) {
		Kml kml = new Kml();

		try {
			HashMap<String, Object> condition = new HashMap<String, Object>();
			condition.put("supplier.id", supplierID);
			condition.put("deviceSerial", deviceSerial);

			List<Modem> modems = (List<Modem>) modemDao.getModemWithGpio(condition);
			Document document = new Document();
			kml.setFeature(document);

			document.getStyleSelector().add(setStyle());

			for (int i = 0; i < modems.size(); i++) {
				// Polygon, PolyLine 설정 : OnlyLine = PolyLine
				// Google Maps에서 제공하는 Polygon 기능을 해제한다.
				String lineOption = "OnlyLine";

				// MODEM (S)
				Modem modem = modems.get(i);
				String modemDeviceSerial = modem.getDeviceSerial() + "(Modem)";
				String modemAddress = modem.getAddress();
				String modemCoordinate = null;
				Integer modemId = modem.getId();

				// [ 토폴로지 정보 표출시 좌표 활용 ]
				// 1. Modem 토폴로지 정보 표현시, 미터 좌표값이 있으면 미터 좌표값을 우선순위로 적용
				// 2. 미터 좌표값이 없으면 모뎀 좌표값으로 표현
				if ((modem.getGpioX() != null) && (modem.getGpioY() != null) && (modem.getGpioZ() != null)) {
					List<Meter> meters = meterDao.getMeterHavingModem(modem.getId());
					
					if (meters.size() != 0) { // modem에 물려있는 meter가 있을 경우
						Meter meter = meters.get(0);
						String meterCoordinate = null;

						if ((meter.getGpioX() != null) && (meter.getGpioY() != null) && (meter.getGpioZ() != null)) {
							meterCoordinate = meter.getGpioX().toString() + "," + meter.getGpioY().toString() + "," + meter.getGpioZ().toString();

							modemCoordinate = meterCoordinate;
							document.getFeature().add(addPoint(modemDeviceSerial, modemAddress, "modemIcon", modemCoordinate, modemId));
						}
					} else {
						modemCoordinate = modem.getGpioX().toString() + "," + modem.getGpioY().toString() + "," + modem.getGpioZ().toString();
						document.getFeature().add(addPoint(modemDeviceSerial, modemAddress, "modemIcon", modemCoordinate, modemId));
					}
				}
				// MODEM (E)

				// MCU (S)
				MCU mcu = modem.getMcu();
				Integer mcuId = mcu.getId();
				String mcuSysID = mcu.getSysID() + "(DCU)";
				String mcuAddress = mcu.getSysLocation();
				String mcuCoordinate = null;
				List<String> coordiList = new ArrayList<String>();

				if ((mcu.getGpioX() != null) && (mcu.getGpioY() != null) && (mcu.getGpioZ() != null)) {
					mcuCoordinate = mcu.getGpioX().toString() + "," + mcu.getGpioY().toString() + "," + mcu.getGpioZ().toString();
					document.getFeature().add(addPoint(mcuSysID, mcuAddress, "codiIcon", mcuCoordinate, mcuId));
				}
				// MCU (E)

				// METER (S)
				// 선택된 모뎀에 미터가 물려있는 갯수를 구한다.
				List<Meter> meters = meterDao.getMeterHavingModem(modem.getId());

				for (int j = 0; j < meters.size(); j++) {
					Meter meter = meters.get(j);
					Integer meterId = meter.getId();
					String meterMdsID = meter.getMdsId() + "(Meter)";
					String meterAddress = meter.getAddress();
					String meterCoordinate = null;

					if ((meter.getGpioX() != null) && (meter.getGpioY() != null) && (meter.getGpioZ() != null)) {
						meterCoordinate = meter.getGpioX().toString() + "," + meter.getGpioY().toString() + "," + meter.getGpioZ().toString();

						document.getFeature().add(addPoint(meterMdsID, meterAddress, "meterIcon", meterCoordinate, meterId));
						// Print Line : Meter -> MCU
						// document.getFeature().add(addLineString(meterMdsID, meterAddress, meterCoordinate, modemCoordinate, lineOption));
					}
				}
				// METER (E)

				// Parent-MODEM (S)
				Modem parentModem = modem.getModem();

				// 모뎀이 (모)모뎀을 갖고 있을 경우
				if (parentModem != null && parentModem.getGpioX() != null && parentModem.getGpioY() != null) {
					coordiList.add(modemCoordinate);
					Double distance = this.getDistance(modem.getGpioY(), modem.getGpioX(), mcu.getGpioY(), mcu.getGpioX());

					while (parentModem != null) {
						// modemId == parentModem.getId() 			 : 자기자신을 부모 노드로 재귀시키는 경우
						// modemId == parentModem.getParentModemId() : 부모 노드 호출이 반복되는 Cycle이 형성되는 경우
						if (modemId == parentModem.getId() || modemId == parentModem.getParentModemId()) {
							parentModem = null;
							continue;
						}

						String parentModemCoordinate = parentModem.getGpioX().toString() + "," + parentModem.getGpioY().toString() + "," + parentModem.getGpioZ().toString();
						String parentModemDeviceSerial = parentModem.getDeviceSerial() + "(Modem)";
						String parentModemAddress = parentModem.getAddress();
						Integer parentModemId = parentModem.getId();

						document.getFeature().add(addPoint(parentModemDeviceSerial, parentModemAddress, "parent_modemIcon", parentModemCoordinate, parentModemId));
						// document.getFeature().add(addLineString(modemDeviceSerial, modemAddress, modemCoordinate, parentModemCoordinate, lineOption)); // Print Line : (자)모뎀 -> (모)모뎀
						// document.getFeature().add(addLineString(parentModemDeviceSerial, parentModemAddress, parentModemCoordinate, mcuCoordinate, lineOption)); // Print Line : (모)모뎀 -> 집중기

						coordiList.add(parentModemCoordinate);
						parentModem = parentModem.getModem();
					}

					coordiList.add(mcuCoordinate);
					document.getFeature().add(addLineString(deviceSerial + "(Modem)", distance + "", coordiList));
				} else { // 모뎀이 (모)모뎀을 갖고 있지 않은 경우
					document.getFeature().add(addLineString(mcuSysID, mcuAddress, mcuCoordinate, modemCoordinate, lineOption));
				}
				// Parent-MODEM (E)
			}
		} catch (Exception e) {
			log.error(e, e);
		}

		return kml;
	}
    
    public Boolean setMCUPoint(String sysID, Double gpioX, Double gpioY, Double gpioZ) {
        MCU mcu = mcuDao.get(sysID);
        mcu.setGpioX(gpioX);
        mcu.setGpioY(gpioY);
        mcu.setGpioZ(gpioZ);

        try{
            mcuDao.codeUpdate(mcu);
        } catch (Exception e) {
            log.info(e);
            return false;
        }
        return true;
    }
    
    public Boolean setMCUAddress(String sysID, String address) {
        MCU mcu = mcuDao.get(sysID);
        mcu.setSysLocation(address);

        try{
            mcuDao.codeUpdate(mcu);
        } catch (Exception e) {
            log.info(e);
            return false;
        }
        return true;
    }

    public Boolean setModemPoint(String deviceSerial, Double gpioX, Double gpioY, Double gpioZ) {
        Modem modem = modemDao.get(deviceSerial);
        modem.setGpioX(gpioX);
        modem.setGpioY(gpioY);
        modem.setGpioZ(gpioZ);
        
        try{
            modemDao.codeUpdate(modem);
        } catch (Exception e) {
            log.info(e);
            return false;
        }
        return true;
    }

    public Boolean setModemAddress(String deviceSerial, String address) {
        Modem modem = modemDao.get(deviceSerial);
        modem.setAddress(address);
        
        try{
            modemDao.codeUpdate(modem);
        } catch (Exception e) {
            log.info(e);
            return false;
        }
        return true;
    }

    public Boolean setMeterPoint(String mdsID, Double gpioX, Double gpioY, Double gpioZ) {
        Meter meter = meterDao.get(mdsID);
        
        meter.setGpioX(gpioX);
        meter.setGpioY(gpioY);
        meter.setGpioZ(gpioZ);
        
        try {
            meterDao.codeUpdate(meter);
        }catch (Exception e) {
            log.info(e);
            return false;
        }
        return true;
    }

    public Boolean setMeterAddress(String mdsID, String address) {
        Meter meter = meterDao.get(mdsID);
        
        meter.setAddress(address);
        
        try {
            meterDao.codeUpdate(meter);
        }catch (Exception e) {
            log.info(e);
            return false;
        }
        return true;
    }
    
    // Kml : 장비의 포인트(좌표)를 설정하기 위한 함수
    // addPoint(Placemark의 이름, 설명(주소) StyleID(Style이 지정되어있어야함), 좌표)
    public Placemark addPoint(String id,String name, String discription, String styleURL, String coord) {
        List<Coordinate> coordinate = new ArrayList<Coordinate>();
        coordinate.add(new Coordinate(coord));

        Placemark placemark = new Placemark();
        placemark.setId(id);
        placemark.setName(name);
        placemark.setStyleUrl(styleURL);
        placemark.setDescription(discription);
        Point point = new Point();
       
        placemark.setGeometry(point);
        point.setCoordinates(coordinate);

        return placemark;
    }
    
    // Kml : 장비의 포인트(좌표)를 설정하기 위한 함수
    // addPoint(Placemark의 이름, 설명(주소) StyleID(Style이 지정되어있어야함), 좌표)
    public Placemark addPoint(String id,String name, String discription, String styleURL, String coord, Integer targetId) {
        List<Coordinate> coordinate = new ArrayList<Coordinate>();
        coordinate.add(new Coordinate(coord));

        Placemark placemark = new Placemark();
        placemark.setId(id);
        placemark.setName(name);
        placemark.setStyleUrl(styleURL);
        placemark.setDescription(discription);
        placemark.setTargetId(targetId.toString());
        
        Point point = new Point();
       
        placemark.setGeometry(point);
        point.setCoordinates(coordinate);

        return placemark;
    }

    // Kml : 장비의 포인트(좌표)를 설정하기 위한 함수
    // addPoint(Placemark의 이름, 설명(주소) StyleID(Style이 지정되어있어야함), 좌표)
    public Placemark addPoint(String name, String discription, String styleURL, String coord) {
        List<Coordinate> coordinate = new ArrayList<Coordinate>();
        coordinate.add(new Coordinate(coord));

        Placemark placemark = new Placemark();
        placemark.setName(name);
        placemark.setStyleUrl(styleURL);
        placemark.setDescription(discription);
        Point point = new Point();
       
        placemark.setGeometry(point);
        point.setCoordinates(coordinate);

        return placemark;
    }
    
    public Placemark addPointWithTargetId(String name, String discription, String styleURL, String coord, Integer targetId) {
        List<Coordinate> coordinate = new ArrayList<Coordinate>();
        coordinate.add(new Coordinate(coord));

        Placemark placemark = new Placemark();
        placemark.setName(name);
        placemark.setStyleUrl(styleURL);
        placemark.setDescription(discription);
        placemark.setTargetId(targetId.toString());
        Point point = new Point();
       
        placemark.setGeometry(point);
        point.setCoordinates(coordinate);

        return placemark;
    }
    
 // Kml : 장비의 포인트(좌표)를 설정하기 위한 함수
    // addPoint(Placemark의 이름, 설명(주소) StyleID(Style이 지정되어있어야함), 좌표)
    public Placemark addPoint(String name, String discription, String styleURL, String coord,Double distance) {
        List<Coordinate> coordinate = new ArrayList<Coordinate>();
        coordinate.add(new Coordinate(coord));

        Placemark placemark = new Placemark();
        placemark.setName(name);
        placemark.setStyleUrl(styleURL);
        placemark.setDescription(discription+"<br />"+distance);
        Point point = new Point();
       
        placemark.setGeometry(point);
        point.setCoordinates(coordinate);

        return placemark;
    }
    
    // Kml : 장비의 포인트(좌표)를 설정하기 위한 함수
    // addPoint(Placemark의 이름, 설명(주소) StyleID(Style이 지정되어있어야함), 좌표)
    public Placemark addPoint(String name, String discription, String styleURL, String coord,Double distance,Modem modem) {
        List<Coordinate> coordinate = new ArrayList<Coordinate>();
        coordinate.add(new Coordinate(coord));

        Placemark placemark = new Placemark();
        placemark.setName(name);
        placemark.setStyleUrl(styleURL);
        if(modem instanceof ZEUPLS){
        	ZEUPLS zeupls = (ZEUPLS)modem;
			placemark.setDescription(discription + "<br/><b>Distance : </b>" + distance + "<br/><b>LQI : </b>" + zeupls.getLQI() + "<br/><b>RSSI:</b>" + zeupls.getRssi());
        }else{
			placemark.setDescription(discription + "<br/><b>Distance : </b>" + distance + " m");
        }
        Point point = new Point();
       
        placemark.setGeometry(point);
        point.setCoordinates(coordinate);

        return placemark;
    }

    public Placemark addPoint(String name, String discription, String styleURL, String coord, Integer targetId) {
        List<Coordinate> coordinate = new ArrayList<Coordinate>();
        coordinate.add(new Coordinate(coord));

        Placemark placemark = new Placemark();
        placemark.setName(name);
        placemark.setStyleUrl(styleURL);
        placemark.setDescription(discription);
        placemark.setTargetId(targetId.toString());
        
        Point point = new Point();
        placemark.setGeometry(point);
        point.setCoordinates(coordinate);

        return placemark;
    }
    
    public Placemark addPoint(String name, String discription, String styleURL, String coord,Double distance,Modem modem, Integer targetId) {
        List<Coordinate> coordinate = new ArrayList<Coordinate>();
        coordinate.add(new Coordinate(coord));

        Placemark placemark = new Placemark();
        placemark.setName(name);
        placemark.setStyleUrl(styleURL);
        placemark.setTargetId(targetId.toString());
        if(modem instanceof ZEUPLS){
        	ZEUPLS zeupls = (ZEUPLS)modem;
			placemark.setDescription(discription + "<br/><b>Distance : </b>" + distance + "<br/><b>LQI : </b>" + zeupls.getLQI() + "<br/><b>RSSI : </b>" + zeupls.getRssi());
		} else {
			placemark.setDescription(discription + "<br/><b>Distance : </b>" + distance + " m");
		}
        Point point = new Point();
       
        placemark.setGeometry(point);
        point.setCoordinates(coordinate);

        return placemark;
    }
    
    public Placemark addPoint(String name, String discription, String styleURL, String coord,Double distance, Integer targetId) {
        List<Coordinate> coordinate = new ArrayList<Coordinate>();
        coordinate.add(new Coordinate(coord));

        Placemark placemark = new Placemark();
        placemark.setName(name);
        placemark.setStyleUrl(styleURL);
		placemark.setDescription(discription + "<br/>" + distance);
        placemark.setTargetId(targetId.toString());
        Point point = new Point();
       
        placemark.setGeometry(point);
        point.setCoordinates(coordinate);

        return placemark;
    }

    // Kml : 초기 카메라 위치 설정 (고정적으로 어떤 위치를 바라보게 할 때 사용)
    public LookAt setLookAt() {
        LookAt lookat = new LookAt();
        lookat.setLongitude(126.88);    // 경도 (동-서)
        lookat.setLatitude(37.48);  // 위도(남-북)
        lookat.setRange(150.0d);    // 범위
        lookat.setTilt(50.0d);  // 경사
        lookat.setHeading(0.0d);    // 방향

        return lookat;
    }

    // Kml : 아이콘이나 글자 색을 바꾸기 위한 함수
    public Style setStyle() {
        Style style = new Style();
        style.setId("randomColorIcon"); // 스타일의 ID 지정

        IconStyle iconstyle = new IconStyle();
        style.setIconStyle(iconstyle);
        iconstyle.setColor("ff00ff00");
        iconstyle.setScale(0.9d);

        Icon icon = new Icon();
        iconstyle.setIcon(icon);
        icon.setHref("http://maps.google.com/mapfiles/kml/pal3/icon21.png");

        LabelStyle labelstyle = new LabelStyle();
        style.setLabelStyle(labelstyle);
        labelstyle.setColor("ff0000cc");
        labelstyle.setScale(1.0d);

        return style;
    }

    public Placemark addLineString(String name, String discription, List<String>coordList) {
        List<Coordinate> coordinate = new ArrayList<Coordinate>();
        for (int i = coordList.size() - 1; i >= 0; i--) {
        	coordinate.add(new Coordinate(coordList.get(i)));
		}
        
        Placemark placemark = new Placemark();
        placemark.setName(name);
        placemark.setDescription(discription);
        LineString linestring = new LineString();

        placemark.setGeometry(linestring);
        linestring.setExtrude(false);
        linestring.setTessellate(true);
        linestring.setCoordinates(coordinate);

        return placemark;
    }
    
    public Placemark addLineStringWithCount(String name, String discription,String coord1, String coord2, int countMeterWithRelativeMcu) {
        List<Coordinate> coordinate = new ArrayList<Coordinate>();
        coordinate.add(new Coordinate(coord1));
        coordinate.add(new Coordinate(coord2));

        Placemark placemark = new Placemark();
        placemark.setName(name);
        placemark.setDescription(discription);
        // setPhoneNumber - countMeterWithRelativeMcu 넘겨주는 메소드로 사용
        placemark.setPhoneNumber(Integer.toString(countMeterWithRelativeMcu));
        LineString linestring = new LineString();

        placemark.setGeometry(linestring);
        linestring.setExtrude(false);
        linestring.setTessellate(true);
        linestring.setCoordinates(coordinate);

        return placemark;
    }
    
    public Placemark addLineString(String name, String discription,String coord1, String coord2) {
        List<Coordinate> coordinate = new ArrayList<Coordinate>();
        coordinate.add(new Coordinate(coord1));
        coordinate.add(new Coordinate(coord2));

        Placemark placemark = new Placemark();
        placemark.setName(name);
        placemark.setDescription(discription);
        LineString linestring = new LineString();

        placemark.setGeometry(linestring);
        linestring.setExtrude(false);
        linestring.setTessellate(true);
        linestring.setCoordinates(coordinate);

        return placemark;
    }
    
    public Placemark addLineString(String name, String discription,String coord1, String coord2, String lineOption) {
        List<Coordinate> coordinate = new ArrayList<Coordinate>();
        
        coordinate.add(new Coordinate(coord1));
        coordinate.add(new Coordinate(coord2));

        Placemark placemark = new Placemark();
        placemark.setName(name);
        placemark.setDescription(discription);
        LineString linestring = new LineString();

        placemark.setGeometry(linestring);
        linestring.setExtrude(false);
        linestring.setTessellate(true);
        linestring.setId(lineOption);	
        linestring.setCoordinates(coordinate);

        return placemark;
    }
    
    public Placemark addLineString(String coord1, String coord2) {
        List<Coordinate> coordinate = new ArrayList<Coordinate>();
        coordinate.add(new Coordinate(coord1));
        coordinate.add(new Coordinate(coord2));

        Placemark placemark = new Placemark();
        LineString linestring = new LineString();

        placemark.setGeometry(linestring);
        linestring.setExtrude(false);
        linestring.setTessellate(true);
        linestring.setCoordinates(coordinate);

        return placemark;
    }
    
    /**
     * 
     * @param supplierID
     * @param sysID
     * @param locationID
     * @return
     */
    public  List<Object> getMCUList(Integer supplierID, String sysID, Integer locationID) {
    	List<Object> ret = new ArrayList<Object>();
        HashMap<String, Object> condition = new HashMap<String, Object>();
        Code deleteCode = codeDao.getCodeIdByCodeObject(McuStatus.Delete.getCode());
        int deleteCodeId = deleteCode.getId();

        condition.put("deleteCodeId", deleteCodeId);
        if ( supplierID != null )
        	condition.put("supplier.id", supplierID);
        if ( sysID != null && sysID.length() > 0  ){
        		condition.put("sysID", sysID);
        }
        else {
        	if ( locationID != null )
        		condition.put("location.id", locationID);
        }
        	
        List<MCU> mcus = (List<MCU>) mcuDao.getMCUWithoutGpio(condition);

        for (int i=0; i<mcus.size(); i++) {
        	HashMap<String, Object> mcuHash = new HashMap<String,Object>();
        	 MCU mcu = mcus.get(i);
        	 String mcuSysID = mcu.getSysID()+"(DCU)"; 
        	mcuHash.put("id", mcu.getId());
        	mcuHash.put("sysID", mcu.getSysID());
        	if ( mcu.getGpioX() == null || mcu.getGpioY() == null ){
        		continue;
        	}
        	
        	if (mcu.getGpioY() != null)
        		mcuHash.put("latitude", mcu.getGpioY().toString());
        	else 
        		mcuHash.put("latitude", "0.0");
        	
        	if (mcu.getGpioX() != null )
        		mcuHash.put("longitude", mcu.getGpioX().toString());
        	else 
        		mcuHash.put("longitude", "0.0");
        	
        	if ( mcu.getGpioZ() != null )
        		mcuHash.put("altitude", mcu.getGpioZ().toString());
        	else
        		mcuHash.put("altitude", "0.0");
        	
            if ( mcu.getSysLocation() != null)
            	mcuHash.put("address", mcu.getSysLocation());
            else 
            	mcuHash.put("address", "");
            
            if(mcu.getMcuCodi() != null){
            	mcuHash.put("name", mcu.getMcuCodi().getCodiID()+"(CODI)");
            }else{
            	mcuHash.put("name", mcuSysID);
            }
            ret.add(mcuHash);
        }

        return ret;
    }
    
    public List<Object> getMeterList(Integer supplierID, String mdsID,Integer locationID, String msa) { // UPDATE SP-1050

    	HashMap<String, String> condition = new HashMap<String, String>();
    	List<Object> ret = new ArrayList<Object>();
    	condition.put("supplierId", supplierID.toString());

    	if ( mdsID != null && mdsID.length() > 0 ){
    		condition.put("mdsId", mdsID);
    	}
    	else {
    		if ( locationID != null){ 
    			condition.put("locationId", locationID.toString());
    		}
    		if ( msa != null) {
    			condition.put("msa", msa);
    		}
    	}

    	List<Map<String, Object>> meters = meterDao.getMeterWithMCU(condition);
//    	List<Map<String, Object>> meters2 = meterDao.getMeterMMIU(condition);
//    	meters.addAll(meters2);

    	for (int i=0; i<meters.size(); i++) {
    		HashMap<String, Object> meterHash = new HashMap<String,Object>();

    		Map<String, Object> meter = meters.get(i);
    		meterHash.put("id", meter.get("ID"));
    		meterHash.put("name", meter.get("MDS_ID") + "(Meter)");
    		meterHash.put("mdsId", meter.get("MDS_ID"));
    		meterHash.put("modem_type", meter.get("MODEM_TYPE"));
    		meterHash.put("modem_protocol", meter.get("MODEM_PROTOCOL"));
    		if ( meter.get("LOCATION_NAME") != null)
    			meterHash.put("location", meter.get("LOCATION_NAME").toString());
    		
    		if ( meter.get("GPIOY") != null )
    			meterHash.put("latitude", meter.get("GPIOY").toString());
    		else 
    			meterHash.put("latitude", "0.0");
    		
    		if ( meter.get("GPIOX") != null )
    			meterHash.put("longitude", meter.get("GPIOX").toString());
    		else 
    			meterHash.put("longitude", "0.0");
        	if ( meter.get("GPIOZ") != null )
        		meterHash.put("altitude", meter.get("GPIOZ").toString());
        	else 
        		meterHash.put("altitude", "0.0");
        	
    		if ( meter.get("ADDRESS") != null){
    			meterHash.put("address", meter.get("ADDRESS"));
    		} else {
    			meterHash.put("address", "");
    		}
    		if( meter.get("DEVICE_SERIAL") != null){
    			meterHash.put("deviceSerial",meter.get("DEVICE_SERIAL") );
    		}
    		else {
    			meterHash.put("deviceSerial","");
    		}
    		if( meter.get("SYS_ID") != null){
    			meterHash.put("sysID",meter.get("SYS_ID") );
    		}
    		else {
    			meterHash.put("sysID","");
    		}
    		if ( meter.get("MSA") != null ) {
    			meterHash.put("msa", meter.get("MSA"));
    		}
    		else {
    			meterHash.put("msa", "");
    		}
    		
    		ret.add(meterHash);
    	}
    	return ret;
    }
    
    public List<Object> getModemList(Integer supplierID, String deviceSerial,Integer locationID,String msa) { // UPDATE SP-1050
    	HashMap<String, Object> condition = new HashMap<String, Object>();
    	List<Object> ret = new ArrayList<Object>();


    	condition.put("supplierId", supplierID.toString());
    	if ( deviceSerial != null && deviceSerial.length() > 0 ){
    		condition.put("deviceSerial", deviceSerial);
    	}
    	else {
    		if ( locationID != null){ 
    			condition.put("locationId", locationID.toString());
    		}
    		if ( msa != null) {
    			condition.put("msa", msa);
    		}
    	}

    	List<Map<String, Object>> targetList = modemDao.getModemWithMCU(condition);

    	//m.id AS id, m.mds_id AS mdsId, ");
    	//query.append("\n     m.address AS address, m.gpiox AS GPIOX, m.gpioy ASGPIOY, m.gpioz AS GPIOZ ,");
    	//query.append("\n     md.device_serial AS deviceSerial, mcu.sys_id AS sysID");

    	for (int i=0; i<targetList.size(); i++) {
    		HashMap<String, Object> modemHash = new HashMap<String,Object>();

    		Map<String, Object> target = targetList.get(i);
    		modemHash.put("id", target.get("ID"));
    		modemHash.put("name", target.get("DEVICE_SERIAL") + "(Modem)");
    		modemHash.put("modem_type", target.get("MODEM_TYPE"));
    		modemHash.put("modem_protocol", target.get("MODEM_PROTOCOL"));
    		if ( target.get("LOCATION_NAME") != null)
    			modemHash.put("location", target.get("LOCATION_NAME").toString());
    		
    		if ( target.get("GPIOY") != null )
    			modemHash.put("latitude", target.get("GPIOY").toString());
    		else 
    			modemHash.put("latitude", "0.0");
    		
    		if ( target.get("GPIOX") != null )
    			modemHash.put("longitude", target.get("GPIOX").toString());
    		else 
    			modemHash.put("longitude", "0.0");
    		
        	if ( target.get("GPIOZ") != null )
        		modemHash.put("altitude", target.get("GPIOZ").toString());
        	else 
        		modemHash.put("altitude", "0.0");
        	
    		if ( target.get("ADDRESS") != null)
    			modemHash.put("address", target.get("ADDRESS"));
    		else 
    			modemHash.put("address", "");
    		if( target.get("deviceSerial") != null){
    			modemHash.put("deviceSerial",target.get("DEVICE_SERIAL") );
    		}
    		else {
    			modemHash.put("deviceSerial","");
    		}
    		if( target.get("SYS_ID") != null){
    			modemHash.put("sysID",target.get("SYS_ID") );
    		}
    		else {
    			modemHash.put("sysID","");
    		}
    		if ( target.get("MSA") != null ) {
    			modemHash.put("msa", target.get("MSA"));
    		}
    		else {
    			modemHash.put("msa", "");
    		}
    		ret.add(modemHash);
    	}
    	return ret;
    }
    
    public List<Object>  getMCUWithCodiList(Integer supplierID, String sysID,Integer locationID) {
    	List<Object> ret = new ArrayList<Object>();
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplier.id", supplierID);

        if(locationID != null)
        	condition.put("location.id", locationID);
        
        if(sysID != null && sysID.length()>0)
        	condition.put("sysID", sysID);

        List<MCU> mcus = (List<MCU>) mcuDao.getMCUWithGpioCodi(condition);
       
        for (int i=0; i<mcus.size(); i++) {
        	HashMap<String, Object> mcuHash = new HashMap<String,Object>();
        	 MCU mcu = mcus.get(i);

        	mcuHash.put("id", mcu.getId());
        	mcuHash.put("sysID", mcu.getSysID());
        	String gpiox = "0.0";
        	String gpioy = "0.0";
        	String gpioz = "0.0";
        	if ( mcu.getGpioX()!=null) gpiox =  mcu.getGpioX().toString();
        	if ( mcu.getGpioY()!=null) gpioy =  mcu.getGpioY().toString();
        	if ( mcu.getGpioZ()!=null) gpioz =  mcu.getGpioZ().toString();
        	mcuHash.put("latitude", gpioy);
        	mcuHash.put("longitude", gpiox);
        	mcuHash.put("altitude", gpioz);
        	
            if ( mcu.getSysLocation() != null)
            	mcuHash.put("address", mcu.getSysLocation());
            else 
            	mcuHash.put("address", "");
            
            mcuHash.put("name", mcu.getMcuCodi().getCodiID()+"(CODI)");

            ret.add(mcuHash);
        }

        return ret;
    }
	
    /* 
     * SP-1038
     */
    @Transactional(readOnly = true)
    public String getMcuMap(String sysId)
    {
    	McuKmlMap mcukml = new McuKmlMap();
    	return mcukml.getMcuMap(sysId);
    }
    
    /**
     * SP-1038
     *
     */
    public class McuKmlMap {
        Session session = null;
        int _map_table = 0;
        boolean useLpEm = false;
        static final int   _maxHop = 10;
        static final String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" + 
                "<Document>\n" + 
                "<name>RF Mesh - Topology</name>";
        static final  String tail = "    <Style id=\"dcuIconBlue\">\n" + 
                "      <IconStyle>\n" + 
                "        <Icon>\n" + 
                "          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAB9ElEQVR42q1WSy8DURS+f8KGeMTG/7CVWIlHE7QzDTozpMW9Q1jYiBASUgsiKPGKIjQVbSOIingGqUdKqJBYEVtWR2+Tlo72zi3zJWdzc+b77r3nnO8OQgyYbPZCUSEdokw2i0tn37OK3oBGSfnoq1VRA9H1dnODvQBlCkosKGQpTqgXVgUvmMSWfC5yQcI1vMTaEG2tJja5TNS/kidOI+EWw3euDYuEq37duVHk8aiua8pLCFgl4mYl9w2OZH4KWZ3n2v3kjBsorm5uk9YHhl0wveBlisRamPY5K+nj4xPi+Cmy4t2BEZdbr+BtiA5RuoSpuWXQInz3AFt753xtK2E/+jmh2tgIbEMq3EeeuARM5u4XxEpY9vggHS6vw1wiTIGuHiewwCOCqHGxEkJXYabI7OJq2m9r6zufEXVFlkC52QORx6eU5CdnIR0TVH2ItpLeMefdHnCOuiC4fwQXoRvYCR7AgHNM93oEGWNEbZaVtOYLgn/7GIKHIdjcPQVvYB88/j2uAldamnNj00zHOl1S79A49DsnkqIvuqY7AzKZTngRNSajza6mwZGT5KjUYo2za1KW8k2Ijrbjv+TRwjYyXzWLDVcYvnMtaPVFicxwv8UycVUKSnbGfxcxIUVttcp4XZDUyPcAkQdRVr30Sn8VU4MvhOBkkp+tIFMAAAAASUVORK5CYII=</href>\n" + 
                "        </Icon>\n" + 
                "      </IconStyle>\n" + 
                "    </Style>\n" + 
                "    <Style id=\"dcuIconPurple\"><!--UnKnown-->\n" + 
                "      <IconStyle>\n" + 
                "        <Icon>\n" + 
                "          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAABuElEQVR42mNgwAOCEjOVErNKqpOySvc4+i55385Q/B+EvUJnvEnKLtuVmFlSmZBRoMBAKgAZnJBdugZmICGcnFW8IigqW54ow+PTi+KINRgdJ6QVRuE1PCGzpJxcw2E4MaO4mOouR8dx6cWRGGFOLcNhODQ2TQ5uQVJGyWp8irsmzCA9PjJLlxPl+uU9k/6DwPUly1DEe6cs+L8nIR2/JaAkDErn+BT9+PHzPwygWzJjwWq8FiRlFFcwgDIRLgVb84r/o4Obt+8RHUxAC3YyIOdQbBgbuHv/EVEWRMS3PGMgpAgXuN43gShL8FrQ0D75Pz5AjCUMoIILn4JrQEPwAbwZLq3qMQOoVMSnKCxh0//7Dx6RbDg4krPKdjCAkhIxYTl55gIwfSMlDZIPJs8mnNkyiksZQMUstYsJGPaPyJKFlqKly3Ep6pw493/P5HkouAsoRrBUzSxZBC+LQAUT1Qu7uAxplBIVVMRSsbgOwVonACuLQkoNT8gsysVbq8WlF4VT3eXoABT7SRmlS4iuJjNLFvhFJEmR3LoAWQTUXJKUWbw1MbP0PrwVkV16D1gKbwEFKUZkogEAE7gLwt7TTJMAAAAASUVORK5CYII=</href>\n" + 
                "        </Icon>\n" + 
                "      </IconStyle>\n" + 
                "    </Style>\n" + 
                "    <Style id=\"dcuIconGray\"><!--Power Down -->\n" + 
                "      <IconStyle>\n" + 
                "        <Icon>\n" + 
                "          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAB9klEQVR42q1WW0sCQRSe31F0oX/Sa1BBdLEg291Ad1aCcne7vvQQRFFQ2IMRlYVlZEWJkUpWZERXKuyCRRkFPQW91tNpB1J003Gs/eC8DGe/b3bOnO8MQhTU8LiMl5Q+QVK3yyvdH+FwGEhU1DnfBVtniMdKDye2l6J8QYg5m7qSIMwVrZK8VNNkK2Eib7HazazE+uAsHU1Ucg4rXX8lTwQvyrLhO9eH2So3/jpzo8gTUddsKU4KCKLipSUPjznzrwdWPUy7n3V7geDm7j5tfXTCBfNLfroIucLkntOSPj+/IIFUkTX/HjhdXqqAIMrdiDRRtoS5xVXQI/bwBDsHl0zHpAkEUWqH6mMrtAuZ8Bh/YRIwtQy8IVrCqi8A2XB9G2MSoQr0DzqABhYRRIyLlhC9iVFFFpbXszecpfcVEVekCdRzPog/v2QkP7uI0ossdQYQuUq5ftPj9YFj0gWRwxO4it7BXuQIRh1TuZtNlFVEbJaWtBGIQHD3FCLHUdjePwd/6BB8wQOmAlebpKIfF1U92ZKGxqdhxDGTFsPaWk5Xxcp80ouIMRludmaxMM1RicUaaNe1GWeCNiw6/kvOYXsbdaqZrfYGw3euB6m+IKpu5jGJFVeVSSjI+3VBhLSPFQHLmzxW48lXhE190lzYT470VzF1+AatC88x/x7eeAAAAABJRU5ErkJggg==</href>\n" + 
                "        </Icon>\n" + 
                "      </IconStyle>\n" + 
                "    </Style>\n" + 
                "    <Style id=\"dcuIconOrange\"><!-- Comm.Error -->\n" + 
                "      <IconStyle>\n" + 
                "        <Icon>\n" + 
                "          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAABuElEQVR42mNgwAOCEjOVErNKqpOySvc4+i55/zia4T8Ie4XOeJOUXbYrMbOkMiGjQIGBVAAyOCG7dA3MQEI4Oat4RVBUtjxRhsenF8URazA6TkgrjMJreEJmSTm5hsNwYkZxMdVdjo7j0osjMcKcWobDcGhsmhzcgqSMktX4FHdNmEF6fGSWLifK9ct7Jv0HgetLlqGI905Z8H9PQjp+S0BJGJTO8Sn68ePnfxhAt2TGgtV4LUjKKK5gAGUiXAq25hX/Rwc3b98jOpiAFuxkQM6h2DA2cPf+I6IsiIhvecZASBEucL1vAlGW4LWgoX3yf3yAGEsYQAUXPgXXgIbgA3gzXFrVYwZQqYhPUVjCpv/3Hzwi2XBwJGeV7WAAJSViwnLyzAVg+kZKGiQfTJ5NOLNlFJcygIpZahcTMOwfkSULLUVLl+NS1Dlx7v+eyfNQcBdQjGCpmlmyCF4WgQomqhd2cRnSKCUqqIilYnEdgrVOAFYWhZQanpBZlIu3VotLLwqnusvRASj2kzJKlxBdTWaWLPCLSJIiuXUBsgiouSQps3hrYmbpfXgrIrv0HrAU3gIKUozIRAMAyKZrprXoaLMAAAAASUVORK5CYII=</href>\n" + 
                "        </Icon>\n" + 
                "      </IconStyle>\n" + 
                "    </Style>\n" + 
                "    <Style id=\"dcuIconRed\"><!-- Security.Error -->\n" + 
                "      <IconStyle>\n" + 
                "        <Icon>\n" + 
                "          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAABuElEQVR42mNgwAOCEjOVErNKqpOySvc4+i55f4+B4T8Ie4XOeJOUXbYrMbOkMiGjQIGBVAAyOCG7dA3MQEI4Oat4RVBUtjxRhsenF8URazA6TkgrjMJreEJmSTm5hsNwYkZxMdVdjo7j0osjMcKcWobDcGhsmhzcgqSMktX4FHdNmEF6fGSWLifK9ct7Jv0HgetLlqGI905Z8H9PQjp+S0BJGJTO8Sn68ePnfxhAt2TGgtV4LUjKKK5gAGUiXAq25hX/Rwc3b98jOpiAFuxkQM6h2DA2cPf+I6IsiIhvecZASBEucL1vAlGW4LWgoX3yf3yAGEsYQAUXPgXXgIbgA3gzXFrVYwZQqYhPUVjCpv/3Hzwi2XBwJGeV7WAAJSViwnLyzAVg+kZKGiQfTJ5NOLNlFJcygIpZahcTMOwfkSULLUVLl+NS1Dlx7v+eyfNQcBdQjGCpmlmyCF4WgQomqhd2cRnSKCUqqIilYnEdgrVOAFYWhZQanpBZlIu3VotLLwqnusvRASj2kzJKlxBdTWaWLPCLSJIiuXUBsgiouSQps3hrYmbpfXgrIrv0HrAU3gIKUozIRAMAbWHkNyqx+voAAAAASUVORK5CYII=</href>\n" + 
                "        </Icon>\n" + 
                "      </IconStyle>\n" + 
                "    </Style>\n" + 
                "    <Style id=\"meterIconBlue\">\n" + 
                "      <IconStyle>\n" + 
                "        <Icon>\n" + 
                "          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAkAAAAJCAYAAADgkQYQAAAAiklEQVR42mNgQIAoIF4NxGegdCCSHAMzEC81M4v6n56++n9V1RkwbWgY+B8oPhOmKM3WNu3/zJn/MbCFRSxIYSxI0YHi4gNYFRUW7gUp2gtS9LC9/SFWRc3Nt0GKbhNtUizIbmyKjIxCQIpCYI6fD/JdVtZGsO8yMtbBfDeNAQ2AwmkjNJzWIYcTAMk+i9OhipcQAAAAAElFTkSuQmCC</href>\n" + 
                "        </Icon>\n" + 
                "      </IconStyle>\n" + 
                "    </Style>\n" + 
                "    <Style id=\"meterIconGreen\">\n" + 
                "      <IconStyle>\n" + 
                "        <Icon>\n" + 
                "          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAkAAAAJCAYAAADgkQYQAAAAiElEQVR42mNgQIAoIF4NxGegdCCSHAMzEC81izL7n746/X/VmSowbRho+B8oPhOmKM02zfb/TCzQItYCpDAWpOhA8YFirIoK9xaCFO0FKXrY/rAdq6Lm280gRbeJNikWZDc2RUYhRiBFITDHzwf5LmtjFth3GesyYL6bxoAGQOG0ERpO65DDCQDX7ovT++K9KQAAAABJRU5ErkJggg==</href>\n" + 
                "        </Icon>\n" + 
                "      </IconStyle>\n" + 
                "    </Style>\n" + 
                "    <Style id=\"meterIconPurple\">\n" + 
                "      <IconStyle>\n" + 
                "        <Icon>\n" + 
                "          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAkAAAAJCAYAAADgkQYQAAAAi0lEQVR42mNgQIAoIF4NxGegdCCSHAMzEC+NMov6vzp99f8zVWfAdKBh4H+g+EyYorQ027T//2f+x8CxFrEghbEgRQcOFB/Aqmhv4V6Qor0gRQ8ftj/Equh2822QottEmxQLshubohCjEJCiEJjj54N8tzFrI9h36zLWwXw3jQENgMJpIzSc1iGHEwBt95qDejjnKAAAAABJRU5ErkJggg==</href>\n" + 
                "        </Icon>\n" + 
                "      </IconStyle>\n" + 
                "    </Style>\n" + 
                "    <Style id=\"meterIconYellow\">\n" + 
                "      <IconStyle>\n" + 
                "        <Icon>\n" + 
                "          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAkAAAAJCAYAAADgkQYQAAAAi0lEQVR42mNgQIAoIF4NxGegdCCSHAMzEC+NijL7v3p1+v8zZ6rAdGCg4X+g+EyYorS0NNv////PxMCxsRYghbEgRQcOHCjGqmjv3kKQor0gRQ8fPmzHquj27WaQottEmxQLshubopAQI5CiEJjj54N8t3FjFth369ZlwHw3jQENgMJpIzSc1iGHEwB8p5qDBbsHtAAAAABJRU5ErkJggg==</href>\n" + 
                "        </Icon>\n" + 
                "      </IconStyle>\n" + 
                "    </Style>\n" + 
                "    <Style id=\"meterIconRed\">\n" + 
                "      <IconStyle>\n" + 
                "        <Icon>\n" + 
                "          <href>data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAkAAAAJCAYAAADgkQYQAAAAiklEQVR42mNgQIAoIF4NxGegdCCSHAMzEC+NUlH5v9rF5f+ZoCAwHaig8B8oPhOmKC1NU/P//7Q0DByrqgpSGAtSdOCAry9WRXt9fECK9oIUPXwYFYVV0e2ICJCi20SbFAuyG5uiECUlkKIQmOPng3y30d0d7Lt1bm4w301jQAOgcNoIDad1yOEEAFm9fSv/VqtJAAAAAElFTkSuQmCC</href>\n" + 
                "        </Icon>\n" + 
                "      </IconStyle>\n" + 
                "    </Style>\n" + 
                "    <Style id=\"line-0288D1-1-nodesc-normal\">\n" + 
                "      <LineStyle>\n" + 
                "        <color>ffD18802</color>\n" + 
                "        <width>1</width>\n" + 
                "      </LineStyle>\n" + 
                "      <BalloonStyle>\n" + 
                "        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
                "      </BalloonStyle>\n" + 
                "    </Style>\n" + 
                "    <Style id=\"line-0288D1-1-nodesc-highlight\">\n" + 
                "      <LineStyle>\n" + 
                "        <color>ffD18802</color>\n" + 
                "        <width>2.0</width>\n" + 
                "      </LineStyle>\n" + 
                "      <BalloonStyle>\n" + 
                "        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
                "      </BalloonStyle>\n" + 
                "    </Style>\n" + 
                "    <StyleMap id=\"line-0288D1-1-nodesc\"><!-- Connect Line -->\n" +  
                "      <Pair>\n" + 
                "        <key>normal</key>\n" + 
                "        <styleUrl>#line-0288D1-1-nodesc-normal</styleUrl>\n" + 
                "      </Pair>\n" + 
                "      <Pair>\n" + 
                "        <key>highlight</key>\n" + 
                "        <styleUrl>#line-0288D1-1-nodesc-highlight</styleUrl>\n" + 
                "      </Pair>\n" +  
                "    </StyleMap>\n" + 
                "    <Style id=\"line-FFA500-1-nodesc-normal\">\n" + 
                "      <LineStyle>\n" + 
                "        <color>ff00A5FF</color>\n" + 
                "        <width>1</width>\n" + 
                "      </LineStyle>\n" + 
                "      <BalloonStyle>\n" + 
                "        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
                "      </BalloonStyle>\n" + 
                "    </Style>\n" + 
                "    <Style id=\"line-FFA500-1-nodesc-highlight\">\n" + 
                "      <LineStyle>\n" + 
                "        <color>ff00A5FF</color>\n" + 
                "        <width>2</width>\n" + 
                "      </LineStyle>\n" + 
                "      <BalloonStyle>\n" + 
                "        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
                "      </BalloonStyle>\n" + 
                "    </Style>\n" + 
                "    <StyleMap id=\"line-FFA500-1-nodesc\"><!-- Abnormal Line -->\n" +  
                "      <Pair>\n" + 
                "        <key>normal</key>\n" + 
                "        <styleUrl>#line-FFA500-1-nodesc-normal</styleUrl>\n" + 
                "      </Pair>\n" + 
                "      <Pair>\n" + 
                "        <key>highlight</key>\n" + 
                "        <styleUrl>#line-FFA500-1-nodesc-highlight</styleUrl>\n" + 
                "      </Pair>\n" + 
                "    </StyleMap>\n" + 
                "    <Style id=\"line-FF5252-1-nodesc-normal\">\n" +  
                "      <LineStyle>\n" + 
                "        <color>ff5252FF</color>\n" + 
                "        <width>1</width>\n" + 
                "      </LineStyle>\n" + 
                "      <BalloonStyle>\n" + 
                "        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
                "      </BalloonStyle>\n" + 
                "    </Style>\n" + 
                "    <Style id=\"line-FF5252-1-nodesc-highlight\">\n" + 
                "      <LineStyle>\n" + 
                "        <color>ff5252FF</color>\n" + 
                "        <width>1</width>\n" + 
                "      </LineStyle>\n" + 
                "      <BalloonStyle>\n" + 
                "        <text><![CDATA[<h3>$[name]</h3>]]></text>\n" + 
                "      </BalloonStyle>\n" + 
                "    </Style>\n" + 
                "    <StyleMap id=\"line-FF5252-1-nodesc\"><!-- Disconnect Line -->\n" + 
                "      <Pair>\n" + 
                "        <key>normal</key>\n" + 
                "        <styleUrl>#line-FF5252-1-nodesc-normal</styleUrl>\n" + 
                "      </Pair>\n" + 
                "      <Pair>\n" + 
                "        <key>highlight</key>\n" + 
                "        <styleUrl>#line-FF5252-1-nodesc-highlight</styleUrl>\n" + 
                "      </Pair>\n" + 
                "    </StyleMap>\n" + 
                "  </Document>\n" + 
                "</kml>";
        

        private HashMap<Integer, HashMap<String, Object>> modemGpio = new HashMap<Integer, HashMap<String, Object>>();
        private ArrayList<Integer> connect = new  ArrayList<Integer>();
        private ArrayList<Integer> disConnectList = new ArrayList<Integer>();
        private ArrayList<Integer> abnomalConnectList = new  ArrayList<Integer>();
        
        public String getMcuMap(String sysId){
            StringBuffer out = new StringBuffer();
            out.append(header);
            out.append("\n");
            try {
                Properties prop = new Properties();

                try {
                    prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
                    useLpEm = Boolean.parseBoolean(prop.getProperty("nms.create.dcukmlmap.uselpem", "false"));
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
                
                session = sessionFactory.openSession();
                Query query = null;
                // check MUC
                MCU mcu = mcuDao.get(sysId);
                if ( mcu == null ) {
                    throw new Exception();
                }
                Double mcuGpioX =  mcu.getGpioX();
                Double mcuGpioY =  mcu.getGpioY();
                // get MCU 
                String mcuQueryStr = "select '<Placemark><name>DCU: '||sys_id||'</name><description><![CDATA[Device ID: '||sys_id||'<br/>IP Address: '||nvl(ip_addr,'')||'</br>IPV6 Address: '||nvl(ipv6_addr,'')||\n" + 
                        "       '</br>Status: '||nvl((select name from code where id = mcu_status),'UnKnown')||\n" + 
                        "       '</br>GpioX: '||gpiox||'</br>GpioY: '||gpioy||'</br>GpioZ: '||nvl(gpioz,0.0)||']]></description>'||\n" + 
                        "       '<Point><coordinates>'|| gpiox || ',' || gpioy || ',0.0</coordinates></Point><styleUrl>'||\n" + 
                        "      (case  when code.code='1.1.4.1' then '#dcuIconBlue' when code.code='1.1.4.3' then '#dcuIconGray' when code.code='1.1.4.4' then '#dcuIconRed' when code.code='1.1.4.5' then '#dcuIconOrange' when code.code is null then '#dcuIconPurple' end ) ||\n" + 
                        "      '</styleUrl>'||\n" + 
                        "       '<ExtendedData><Data name=\"mcu\"><value>{\"id\":'||mcu.id||',\"sysId\":\"'||sys_id||'\",\"location\":\"'||location.name||'\"}</value></Data></ExtendedData>'||\n" + 
                        "       '</Placemark>'\n" + 
                        "from mcu mcu left outer join code code on mcu.mcu_status = code.id  left outer join location on mcu.location_id = location.id where mcu.sys_id=:sysId";


                query = session.createSQLQuery(mcuQueryStr);
                query.setString("sysId", sysId);

                List<Object>  resultList  = query.list();
                out.append(resultList.get(0).toString());
                out.append("\n");
                // check meter_map table
                try {
                    query = session.createSQLQuery("select count(*) from user_tables where table_name = 'METER_MAP'");
                    _map_table = ((Number)query.uniqueResult()).intValue();
                }catch (Exception e) {
                	_map_table = 0;
                }
                String str1 = "        '<br/>GPS X: '||c.cust_gpiox||'<br/>GPS Y: '||c.cust_gpioy||'<br/>RSSI: '||a.rssi||";
                String str2 = "        '<Point><coordinates>'|| c.cust_gpiox || ',' || c.cust_gpioy || ',0.0</coordinates></Point><styleUrl>'||";
                String str3 = "        join meter_map c on b.mds_id=c.mds_id \n";
                String str4 = "left outer join (\n" + 
                        "  select meter_id,sum(current_mv_count) cnt, max(yyyymmdd) maxreaddate from sla_rawdata\n" + 
                        "  where meter_id in (select mds_id from meter where modem_id in (select id from modem where mcu_id=(select id from mcu where sys_id=:sysId)))\n" + 
                        "    and yyyymmdd between to_char(sysdate-3,'yyyymmdd') and to_char(sysdate-1,'yyyymmdd')\n" + 
                        "  group by meter_id \n" + 
                        ") l on b.mds_id=l.meter_id\n";
                String str5 ="             '<br/>Last LP date(last 3 days) : '|| to_char(to_date(l.maxreaddate,'yyyymmdd'),'yyyy/mm/dd')||\n";
                if ( _map_table == 0 ) {
                    str1 = "        '<br/>GPS X: '||b.gpiox||'<br/>GPS Y: '||b.gpioy||'<br/>RSSI: '||a.rssi||";
                    str2 = "        '<Point><coordinates>'|| b.gpiox || ',' || b.gpioy || ',0.0</coordinates></Point><styleUrl>'||";
                    str3 = "";
                }
                if ( useLpEm ) {
                    str4 = "left outer join (\n" + 
                            "    select mdev_id,sum(value_cnt) cnt,max(yyyymmddhh) maxreaddate from lp_em\n" + 
                            "    where mdev_id in (select mds_id from meter where modem_id in (select id from modem where mcu_id=(select id from mcu where sys_id=:sysId)))\n" + 
                            "        and channel=1\n" + 
                            "        and yyyymmdd between to_char(sysdate-3,'yyyymmdd') and to_char(sysdate,'yyyymmdd')\n" + 
                            "        and yyyymmddhh between to_char(sysdate-3,'yyyymmdd') and to_char(sysdate,'yyyymmdd')\n" + 
                            "    group by mdev_id\n" + 
                            ") l on b.mds_id=l.mdev_id\n";
                    str5 =  "             '<br/>Last LP date(last 3 days) : '|| to_char(to_date(l.maxreaddate,'yyyymmddhh24'),'yyyy/mm/dd hh24')||\n"; 
                }
                // get Meters
                StringBuffer meterSql = new StringBuffer();
                meterSql.append(
                        "select '<Placemark><name>Meter: '||b.mds_id||'</name>'||\n" + 
                        "       '<description><![CDATA[Device ID: '||b.mds_id||'<br/>Modem: '||a.device_serial||'<br/>GS1: '||b.gs1||\n" + 
                        "       '<br/>SW Ver: '||b.sw_version||\n" + 
                        "       '<br/>FW Ver: '||a.fw_ver||'('||trim(to_char(a.fw_revision,'XXXX'))||')'||"
                        );
                meterSql.append(str1);
                meterSql.append(str5);
                meterSql.append(
                        "       '<br/>LP Count(last 3 days) : '||nvl(l.cnt,0)||\n" + 
                        "       '<br/>Parent : '|| p.mds_id||']]></description>'||");
                meterSql.append(str2);
                meterSql.append(
                        "       (case when 3*24*60/b.LP_INTERVAL/cnt*100 = 100  then '#meterIconBlue' when cnt is null then '#meterIconRed' else '#meterIconYellow' end)||'</styleUrl>'||\n" + 
                        "    '<ExtendedData><Data name=\"meter\"><value>{\"id\":'||b.id||',\"mdsId\":\"'||b.mds_id||'\",\"modemId\":'||a.id||'}</value></Data>'||\n" + 
                        "        '<Data name=\"modem\"><value>{\"id\":'||a.id||',\"deviceSerial\":\"'||a.device_serial||'\",\"status\":\"'||(nvl((select name from code where id = a.modem_status), 'UnKown'))||'\",\"protocol\":\"'||a.protocol_type||'\",\"type\":\"'||a.modem_type||\n" + 
                        "        '\",\"fwver\":\"'||a.fw_ver||'('||trim(to_char(a.fw_revision,'XXXX'))||')\",\"rssi\":\"'||a.rssi||\n" + 
                        "        '\",\"parent\":\"'||(case when a.modem_id is not null then (select device_serial from modem where id = a.modem_id) else '' end )||\n" + 
                        "        '\",\"x\":'||nvl(a.gpiox,0)||',\"y\":'||nvl(a.gpioy,0)||',\"z\":'||nvl(a.gpioz,0)||'}</value></Data>'||\n" + 
                        "       '</ExtendedData></Placemark>'\n" + 
                        "from modem a\n" + 
                        "join mcu on mcu.id=a.MCU_ID\n" + 
                        "join meter b on a.id = b.modem_id\n");
                meterSql.append(str3);
                meterSql.append("left outer join meter p on a.modem_id = p.modem_id\n");
                meterSql.append(str4);
                meterSql.append(
                        "where mcu.sys_id=:sysId \n" + 
                        "and b.gpiox is not null and (b.meter_status!=(select id from code where code='1.3.3.9') or b.meter_status is null)");
                
                query = session.createSQLQuery(meterSql.toString());
                query.setString("sysId", sysId);

                resultList  = query.list();
                for( Object element : resultList ) {
                    out.append((String)element);
                    out.append("\n");
                }
                out.append(getLines(sysId, mcuGpioX, mcuGpioY));
            }
            catch ( Exception e) {
                log.error(e,e);
            }
            if ( session != null ) session.close();
            out.append(tail);
            return out.toString();
        }
        
        public void getChildren( ArrayList<Integer>  list, String sysId, Integer parentModemId , int maxHop) {
            
            if (maxHop > _maxHop) {
                return;
            }
            String QueryStr = "";
            if ( _map_table > 0 ) {
                QueryStr = "select modem.id , d.cust_gpiox, d.cust_gpioy, c.mds_id  from MODEM \n" + 
                    "join mcu on mcu.id=modem.MCU_ID \n" + 
                    "join meter c on modem.id = c.modem_id \n" + 
                    "join meter_map d on c.mds_id=d.mds_id \n" + 
                    "where mcu.sys_id=:sysId \n" +
                    "and modem.modem_id=:modemId \n" +
                    "and d.cust_gpiox is not null  and  d.cust_gpioy is not null and \n" +
                    "(c.meter_status!= (select id from code where code='1.3.3.9') or c.meter_status is null)"; 
            }
            else {
                QueryStr = "select modem.id , c.gpiox, c.gpioy, c.mds_id  from MODEM \n" + 
                        "join mcu on mcu.id=modem.MCU_ID \n" + 
                        "join meter c on modem.id = c.modem_id \n" + 
                        "where mcu.sys_id=:sysId \n" +
                        "and modem.modem_id=:modemId \n" +
                        "and c.gpiox is not null  and  c.gpioy is not null and \n" +
                        "(c.meter_status!= (select id from code where code='1.3.3.9') or c.meter_status is null)";     
            }
            
            
            Query query = null;
            query = session.createSQLQuery(QueryStr);
            query.setString("sysId", sysId);
            query.setLong("modemId", parentModemId);

            List<Object[]> resultList  = query.list();
            
            for ( Object[] result : resultList ) {
                Integer modemId = Integer.parseInt(result[0].toString());
                if ( modemGpio.get(modemId) != null ) 
                	continue;
                String gpiox = result[1].toString();
                String gpioy = result[2].toString();
                String mdsId = result[3].toString();
                HashMap<String, Object> elm = new  HashMap<String, Object>();
                elm.put("gpiox", gpiox);
                elm.put("gpioy", gpioy);
                elm.put("parentId",parentModemId);
                elm.put("mdsId",mdsId);
                modemGpio.put(modemId, elm);
                list.add(modemId);
                getChildren(list, sysId, modemId , maxHop + 1);
            }
        }
        
        public void searchConnectModem(String sysId) {
            connect = new ArrayList<Integer>();
            String QueryStr = "";
            if ( _map_table > 0 ) {
                QueryStr = "select modem.id, d.cust_gpiox, d.cust_gpioy , c.mds_id  from MODEM \n" + 
                    "join mcu on mcu.id=modem.MCU_ID \n" + 
                    "join meter c on modem.id = c.modem_id \n" + 
                    "join meter_map d on c.mds_id=d.mds_id \n" + 
                    "where mcu.sys_id=:sysId \n" +
                    "and modem.HOPS_TO_BASESTATION = 1 and modem.modem_id is NULL \n" +
                    "and d.cust_gpioy is not null  and  d.cust_gpioy is not null and \n" +
                    "(c.meter_status!= (select id from code where code='1.3.3.9') or c.meter_status is null)"; 
            }
            else {
                QueryStr = "select modem.id, c.gpiox, c.gpioy , c.mds_id  from MODEM \n" + 
                        "join mcu on mcu.id=modem.MCU_ID \n" + 
                        "join meter c on modem.id = c.modem_id \n" + 
                        "where mcu.sys_id=:sysId \n" +
                        "and modem.HOPS_TO_BASESTATION = 1 and modem.modem_id is NULL \n" +
                        "and c.gpioy is not null  and  c.gpioy is not null and \n" +
                        "(c.meter_status!= (select id from code where code='1.3.3.9') or c.meter_status is null)";     
            }
            Query query = null;
            query = session.createSQLQuery(QueryStr);
            query.setString("sysId", sysId);
            
            List<Object[]>  resultList  = query.list();
            
            for ( Object[] result : resultList ) {
                Integer modemId = Integer.parseInt(result[0].toString());
                String gpiox = result[1].toString();
                String gpioy = result[2].toString();
                String mdsId = result[3].toString();
                HashMap<String, Object> elm = new  HashMap<String, Object>();
                elm.put("gpiox", gpiox);
                elm.put("gpioy", gpioy);
                elm.put("mdsId", mdsId);
                modemGpio.put(modemId, elm);
                connect.add(modemId);
                getChildren(connect,  sysId, modemId, 0 );
            }
            return;
        }
        
        public void searchDisConnectModem(String sysId) {
            disConnectList = new ArrayList<Integer>();
            String QueryStr = "";
            if ( _map_table > 0 ) {
                QueryStr = "select modem.id , d.cust_gpiox, d.cust_gpioy ,c.mds_id from MODEM \n" + 
                    "join mcu on mcu.id=modem.MCU_ID \n" + 
                    "join meter c on modem.id = c.modem_id \n" + 
                    "join meter_map d on c.mds_id=d.mds_id \n" + 
                    "where mcu.sys_id=:sysId \n" +
                    "and ( modem.HOPS_TO_BASESTATION <> 1 or modem.HOPS_TO_BASESTATION is NULL ) and modem.modem_id is NULL \n" +
                    "and d.cust_gpioy is not null  and  d.cust_gpioy is not null and \n" +
                    "(c.meter_status!= (select id from code where code='1.3.3.9') or c.meter_status is null)"; 
            }
            else {
                QueryStr = "select modem.id , c.gpiox, c.gpioy ,c.mds_id from MODEM \n" + 
                        "join mcu on mcu.id=modem.MCU_ID \n" + 
                        "join meter c on modem.id = c.modem_id \n" + 
                        "where mcu.sys_id=:sysId \n" +
                        "and ( modem.HOPS_TO_BASESTATION <> 1 or modem.HOPS_TO_BASESTATION is NULL ) and modem.modem_id is NULL \n" +
                        "and c.gpioy is not null  and  c.gpioy is not null and \n" +
                        "(c.meter_status!= (select id from code where code='1.3.3.9') or c.meter_status is null)"; 
            }
            Query query = null;
            query = session.createSQLQuery(QueryStr);
            query.setString("sysId", sysId);

            List<Object[]>  resultList  = query.list();
            for ( Object[] result : resultList ) {
                Integer modemId = Integer.parseInt(result[0].toString());
                if ( modemGpio.get(modemId) != null )
                    continue;
                String gpiox = result[1].toString();
                String gpioy = result[2].toString();
                String mdsId = result[3].toString();
                HashMap<String, Object> elm = new  HashMap<String, Object>();
                elm.put("gpiox", gpiox);
                elm.put("gpioy", gpioy);
                elm.put("mdsId", mdsId);
                modemGpio.put(modemId, elm);
                disConnectList.add(modemId);
                getChildren(disConnectList,  sysId, modemId, 1 );
            }
            return;
        }
        
        public void searchAbnormalConnectModem(String sysId) {
            abnomalConnectList = new ArrayList<Integer>();

            String QueryStr = "";
            if ( _map_table > 0 ) {
                QueryStr = "select modem.id , d.cust_gpiox,d.cust_gpioy , modem.modem_id,  p2.cust_gpiox AS P_GPIOX, p2.cust_gpioy AS P_GPIOY , d.mds_id AS MDS_ID, p2.mds_id AS P_MDS_ID from MODEM \n" + 
                    "join mcu on mcu.id=modem.MCU_ID \n" + 
                    "join meter c on modem.id = c.modem_id \n" + 
                    "join meter_map d on c.mds_id=d.mds_id \n" + 
                    "join meter p on modem.modem_id = p.modem_id \n" + 
                    "join meter_map p2 on p.mds_id = p2.mds_id \n" +
                    "where mcu.sys_id=:sysId \n" +
                    "and modem.modem_id is NOT NULL \n" +
                    "and d.cust_gpioy is not null  and  d.cust_gpioy is not null and p2.cust_gpiox is not null and p2.cust_gpioy is not null and \n" +
                    "(c.meter_status!= (select id from code where code='1.3.3.9') or c.meter_status is null)"; 
            }
            else {
                QueryStr = "select modem.id , c.gpiox, c.gpioy , modem.modem_id,  p.gpiox AS P_GPIOX, p.gpioy AS P_GPIOY , c.mds_id AS MDS_ID, p.mds_id AS P_MDS_ID from MODEM \n" + 
                        "join mcu on mcu.id=modem.MCU_ID \n" + 
                        "join meter c on modem.id = c.modem_id \n" + 
                        "join meter p on modem.modem_id = p.modem_id \n" + 
                        "where mcu.sys_id=:sysId \n" +
                        "and modem.modem_id is NOT NULL \n" +
                        "and c.gpioy is not null  and  c.gpioy is not null and p.gpiox is not null and p.gpioy is not null and \n" +
                        "(c.meter_status!= (select id from code where code='1.3.3.9') or c.meter_status is null)"; 
               
            }
            Query query = null;
            query = session.createSQLQuery(QueryStr);
            query.setString("sysId", sysId);

            List<Object[]>  resultList  = query.list();
            List<Map<String,Object>> list = ReflectionUtils.getDefineListToMapList(resultList);
            for ( Object[] result : resultList ) {
                Integer modemId = Integer.parseInt(result[0].toString());
                if ( modemGpio.get(modemId) != null )
                    continue;
                String gpiox = result[1].toString();
                String gpioy = result[2].toString();
                HashMap<String, Object> elm = new  HashMap<String, Object>();
                elm.put("gpiox", gpiox);
                elm.put("gpioy", gpioy);
                Integer parentModemId = Integer.parseInt(result[3].toString());
                String pgpiox = result[4] == null ? null : result[4].toString();
                String pgpioy = result[5] == null ? null : result[5].toString();
                String pmdsId = result[7] == null ? null : result[7].toString();
                elm.put("parentId",parentModemId);
                elm.put("pgpiox", pgpiox );
                elm.put("pgpioy", pgpioy);
                elm.put("mdsId", result[6].toString());
                elm.put("pmdsId", pmdsId);
                modemGpio.put(modemId, elm);
                abnomalConnectList.add(modemId);
            }
            return;
        }
        
        public ArrayList<Integer> getDisconnectModemList()
        {
            return disConnectList;
        }
        
        public HashMap<Integer, HashMap<String, Object>> getModemGpioMap()
        {
            return modemGpio;
        }
        
        public String getLines(String sysId, Double mcuGpioX, Double mcuGpioY)
        {
            StringBuffer disConnectLines = new StringBuffer();
            StringBuffer connectLines = new StringBuffer();
            StringBuffer abnormalConnectLines = new StringBuffer();
            // Get Connected Lines
            searchConnectModem(sysId);
            for ( Integer modemId: connect) {
                String pGpioX = "";
                String pGpioY = "";
                String eGpioX = "";
                String eGpioY = "";
                HashMap<String,Object> entry = modemGpio.get(modemId);

                String parentId = "";
                String entryId = "";
                if ( entry.get("parentId") == null ) {
                    pGpioX = BigDecimal.valueOf(mcuGpioX).toPlainString();
                    pGpioY = BigDecimal.valueOf(mcuGpioY).toPlainString();
                    parentId = "MCU:" + sysId;
                } else {
                    HashMap<String,Object> parent = modemGpio.get((Integer)entry.get("parentId"));
                    pGpioX = (String)parent.get("gpiox");
                    pGpioY = (String)parent.get("gpioy");
                    parentId =  (String)parent.get("mdsId");
                }
                eGpioX = (String)entry.get("gpiox");
                eGpioY = (String)entry.get("gpioy");
                connectLines.append("    <Placemark>\n" + 
//                        "<description>Connect</description>\n" + 
                        "      <name>" + (String)entry.get("mdsId") + " - " + parentId +"</name>\n" + 
                        "      <styleUrl>#line-0288D1-1-nodesc</styleUrl> \n" + 
                        "      <LineString>\n" + 
                        "        <tessellate>1</tessellate>\n" + 
                        "        <coordinates>");
                connectLines.append(eGpioX + "," + eGpioY + ",0.0 " + pGpioX + "," + pGpioY + ",0.0" );
                connectLines.append("</coordinates>\n" + 
                    "      </LineString>\n" + 
                    "    </Placemark>\n");
            }
            
            // Get Disconnected Line
            searchDisConnectModem(sysId);
            
            for ( Integer modemId: disConnectList) {
                HashMap<String,Object> entry = modemGpio.get(modemId);
                if ( entry.get("parentId") != null ) {
                    String pGpioX = "";
                    String pGpioY = "";
                    String eGpioX = "";
                    String eGpioY = "";
                    
                    HashMap<String,Object> parent = modemGpio.get((Integer)entry.get("parentId"));
                    pGpioX = (String)parent.get("gpiox");
                    pGpioY = (String)parent.get("gpioy");
                    eGpioX = (String)entry.get("gpiox");
                    eGpioY = (String)entry.get("gpioy");
                    
                    disConnectLines.append("    <Placemark>\n" + 
//                            "<description>Disconnect</description>\n" + 
                            "      <name>" + (String)entry.get("mdsId") + " - " + (String)parent.get("mdsId") +"</name>\n" + 
                            "      <styleUrl>#line-FF5252-1-nodesc</styleUrl> \n" + 
                            "      <LineString>\n" + 
                            "        <tessellate>1</tessellate>\n" + 
                            "        <coordinates>");
                    disConnectLines.append(eGpioX + "," + eGpioY + ",0.0 " + pGpioX + "," + pGpioY + ",0.0" );
                    disConnectLines.append("</coordinates>\n" + 
                        "      </LineString>\n" + 
                        "    </Placemark>\n");
                }
            }
            
            // Get Abnormal Connected Line
            searchAbnormalConnectModem(sysId);

            for ( Integer modemId: abnomalConnectList) {
                HashMap<String,Object> entry = modemGpio.get(modemId);
                String pGpioX = (String)entry.get("pgpiox");
                String pGpioY = (String)entry.get("pgpioy");
                String eGpioX = (String)entry.get("gpiox");
                String eGpioY = (String)entry.get("gpioy");

                if ( pGpioX != null && pGpioY != null ) {
                    abnormalConnectLines.append("    <Placemark>\n" + 
//                            "<description>Abnormaly Connect</description>\n" + 
                            "      <name>" + (String)entry.get("mdsId") + " - " + (String)entry.get("pmdsId") +"</name>\n" + 
//                            "      <styleUrl>#line-FFA500-1-nodesc</styleUrl> \n" + 
                            "      <styleUrl>#line-FF5252-1-nodesc</styleUrl> \n" + 
                            "      <LineString>\n" + 
                            "        <tessellate>1</tessellate>\n" + 
                            "        <coordinates>");
                    abnormalConnectLines.append(eGpioX + "," + eGpioY + ",0.0 " + pGpioX + "," + pGpioY + ",0.0" );
                    abnormalConnectLines.append("</coordinates>\n" + 
                            "      </LineString>\n" + 
                            "    </Placemark>\n");
                }
            }

            return (abnormalConnectLines.toString() + disConnectLines.toString() +connectLines.toString());
        
        }
    }
}