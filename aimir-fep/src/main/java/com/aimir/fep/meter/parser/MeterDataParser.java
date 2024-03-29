package com.aimir.fep.meter.parser;

import java.io.Serializable;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.model.device.Meter;

/**
 * parsing Meter Data Interface Class
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public abstract class MeterDataParser implements Serializable
{
	protected static Log log = LogFactory.getLog(MeterDataParser.class);
    protected Meter meter;
    protected String meteringTime;
    protected String meteringType;
    protected String meterTime;
    private boolean isOnDemand = false;
    // SP-687
    private String deviceId = null;

    /**
	 * @param isOnDemand the isOnDemand to set
	 */
	public void setOnDemand(boolean isOnDemand) {
		this.isOnDemand = isOnDemand;
	}

	/**
	 * @return the isOnDemand
	 */
	public boolean isOnDemand() {
		return isOnDemand;
	}

	/**
     * getRawData
     */
    public abstract byte[] getRawData();

    /**
     * get data length
     * @return length
     */
    public abstract int getLength();

    /**
     * parse meter mesurement data
     * @param data
     */
    public abstract void parse(byte[] data) throws Exception;

    /**
     * get metering value
     * @return meteringValue
     */
    public abstract Double getMeteringValue();

    /**
     * get String
     */
    public abstract String toString();

    /**
     * get Data
     */
    public abstract LinkedHashMap<?, ?> getData();


    /**
     * get flag
     * @return flag measurement flag
     */
    public abstract int getFlag();

    /**
     * set flag
     * @param flag measurement flag
     */
    public abstract void setFlag(int flag);

    /**
     * 미터 설정
     * @param meter
     */
    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    // SP-687
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    /**
     * 미터 가져오기 MBus의 경우 검침데이타에 미터 정보가 들어있다.
     * @return
     */
    public Meter getMeter() {
        return meter;
    }

    public void setMeteringTime(String meteringTime) {
        this.meteringTime = meteringTime;
    }
    
    public String getMeteringTime() {
        return this.meteringTime;
    }
    
    public void setMeterTime(String meterTime) {
        this.meterTime = meterTime;
    }
    
    public String getMeterTime() {
        return this.meterTime;
    }
    
    public String getMeteringType() {
		return meteringType;
	}

	public void setMeteringType(String meteringType) {
		this.meteringType = meteringType;
	}

	/**
     * 통신 장비 유형
     * @return
     */
    public DeviceType getDeviceType() {
        ModemType modemType = meter.getModem().getModemType();
        if (modemType == ModemType.IEIU || modemType == ModemType.MMIU || modemType == ModemType.Converter_Ethernet || modemType == ModemType.LTE)
            return DeviceType.Modem;
        else
            return DeviceType.MCU;
    }

    /**
     * 통신 장비 아이디
     * @return
     */
    public String getDeviceId() throws Exception{
        // SP-687
        String _deviceId = null;
        if (getDeviceType() == DeviceType.Modem) {
        	if (meter!=null){
        		if(meter.getModem()!=null) {
        			_deviceId = meter.getModem().getDeviceSerial();
        		}else {
        			log.warn("Please check - meter["+meter.getMdsId()+"] don't have a modem");
        		}
        	}else {
        		throw new Exception("Please check - meter is null!!");
        	}
        }
        else {
        	if(meter!=null) {
        		if(meter.getModem()!=null) {
        			if(meter.getModem().getMcu()!=null) {
        				_deviceId = meter.getModem().getMcu().getSysID();
        			}else {
        				log.warn("Please check - meter["+meter.getMdsId()+"], modem["+meter.getModem().getDeviceSerial()+"] don't have a dcu");
        			}
        		}else {
        			log.warn("Please check - meter["+meter+"] don't have a modem");
        		}
        	}else {
        		log.warn("Please check - meter is null!!");
        	}
        }
        if (_deviceId != null && !"".equals(_deviceId)) return _deviceId;
        else return this.deviceId;
    }

    /**
     * 검침장비유형
     * @return
     */
    public DeviceType getMDevType() {
        if (meter.getMdsId() == null || "".equals(meter.getMdsId()))
            return DeviceType.Modem;
        else
            return DeviceType.Meter;
    }

    /**
     * 검침장비아이디
     * @return
     */
    public String getMDevId() {
        if (getMDevType() == DeviceType.Modem) {
            return meter.getModem().getDeviceSerial();
        }
        else
            return meter.getMdsId();
    }

    public LinkedHashMap<String, Serializable> getDataNotFormatting() {
        return null;
    }

    public void setMcuRevision(String mcuRevision) {

    }

    /**
     * raw data 분석기용 parse
     * @param data
     * @return String
     */
    public String getParsingResult(byte[] data) throws Exception {
		return null;
	}

}
