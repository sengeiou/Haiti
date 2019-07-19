package com.aimir.fep.meter.parser.a1830rlnTable;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

public class A1800_MT implements java.io.Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 5150872672944877679L;
	private Log log = LogFactory.getLog(A1800_MT.class);
	private byte[] rawData = null;

    private byte[] ED_MODEL = new byte[12];
    private byte[] METER_SERIAL = new byte[8];
    private byte[] CLOCK_CALENDAR = new byte[7];
    private byte[] INSTRUMENT_SCALE = new byte[1];    
    private byte[] METER_CONSTANT = new byte[6];
    private byte[] METER_CONSTANT_SCALE = new byte[1];
    private byte[] CT = new byte[6];
    private byte[] VT = new byte[6];
    private byte[] METER_ELEMENT = new byte[2];
    private byte[] METER_STATUS = new byte[15];

    private String modelName = null;
    private String meterSerial = null;
    private String timestamp = null;
    private Double constant = null;
    private double ke = 0.0;
    private int meter_constant_scale = 0;
    private String meterElement = null;
    private Double ct = null;
    private Double vt = null;
    private Double instrumentScale = null;
    private String meterLog = null;
    
    
	public A1800_MT(byte[] rawData) {
        this.rawData = rawData;
		parse();
	}
	
	
	public String getModelName(){
		return this.modelName;
	}
	
	
	public String getMeterSerial(){
		return this.meterSerial;		
	}
	
	public String getTimeStamp() { 
		return this.timestamp;
	}
	
	
	public Double getConstant() {
		return this.constant;
	}
	
	public double getKE(){
		return this.ke;
	}
	
	public int getMeterConstantScale(){
		return this.meter_constant_scale;
	}
	
	public String getMeterElement() { 
		return this.meterElement;
	}
	
	public Double getCT(){
		return this.ct;
	}
	
	public Double getVT(){
		return this.vt;
	}
	
	public Double getInstrumentScale(){
		return this.instrumentScale;
	}	
	
	public String getMeterLog(){
		return this.meterLog;
	}	
	
	public void parse() {
		
        int pos = 0;

        System.arraycopy(rawData, pos, ED_MODEL, 0, ED_MODEL.length);
        pos += ED_MODEL.length;
        this.modelName = new String(ED_MODEL).trim();
        log.debug("ED_MODEL[" + modelName + "]");
        
        System.arraycopy(rawData, pos, METER_SERIAL, 0, METER_SERIAL.length);
        pos += METER_SERIAL.length;
        this.meterSerial = new String(METER_SERIAL).trim();
        log.debug("METER_SERIAL[" + meterSerial + "]");
        
        System.arraycopy(rawData, pos, CLOCK_CALENDAR, 0, CLOCK_CALENDAR.length);
        byte[] yyyy = new byte[2];
        byte[] MM	= new byte[1];
        byte[] dd	= new byte[1];
        byte[] hh	= new byte[1];
        byte[] mm	= new byte[1];
        byte[] ss	= new byte[1];
        
        try {
        	ByteArrayInputStream bis = new ByteArrayInputStream(CLOCK_CALENDAR);
        	bis.read(yyyy);
        	bis.read(MM);
        	bis.read(dd);
        	bis.read(hh);
        	bis.read(mm);
        	bis.read(ss);
			bis.close();
			
			String y = DataFormat.getIntTo2Byte(yyyy)+"";
			String M = DataFormat.getIntToBytes(MM)+"";
			String d = DataFormat.getIntToBytes(dd)+"";
			String h = DataFormat.getIntToBytes(hh)+"";
			String m = DataFormat.getIntToBytes(mm)+"";
			String s = DataFormat.getIntToBytes(ss)+"";
			
			if(DataFormat.getIntToBytes(MM)<10) M = "0"+DataFormat.getIntToBytes(MM);
			if(DataFormat.getIntToBytes(dd)<10) d = "0"+DataFormat.getIntToBytes(dd);
			if(DataFormat.getIntToBytes(hh)<10) h = "0"+DataFormat.getIntToBytes(hh);
			if(DataFormat.getIntToBytes(mm)<10) m = "0"+DataFormat.getIntToBytes(mm);
			if(DataFormat.getIntToBytes(ss)<10) s = "0"+DataFormat.getIntToBytes(ss);
			
			this.timestamp = y + M + d + h + m + s;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        pos += CLOCK_CALENDAR.length;
//        this.timestamp = new ST55(CLOCK_CALENDAR).getDateTime();
        log.debug("CLOCK_CALENDAR[" + timestamp + "]");
        
        System.arraycopy(rawData, pos, INSTRUMENT_SCALE, 0, INSTRUMENT_SCALE.length);
        pos += INSTRUMENT_SCALE.length;
        
        this.instrumentScale = Math.pow(10,DataFormat.hex2signed8(INSTRUMENT_SCALE[0]));
//        this.instrumentScale = new MT16(INSTRUMENT_SCALE).getINSScale() ;
        log.debug("INSTRUMENT_SCALE[" + instrumentScale + "]");
        
        System.arraycopy(rawData, pos, METER_CONSTANT, 0, METER_CONSTANT.length);
        pos += METER_CONSTANT.length;
        
        try {
			this.constant = DataFormat.hex2long(DataFormat.LSB2MSB(METER_CONSTANT))*instrumentScale.doubleValue();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
//        this.constant = new ST15(instrumentScale.doubleValue()).getKE(METER_CONSTANT);
        try {
			this.ke = DataFormat.hex2long(DataFormat.LSB2MSB(METER_CONSTANT));
		} catch (Exception e) {
			e.printStackTrace();
		}
        log.debug("METER_CONSTANT[" + constant + "]");
        
        
        
        //MT15 Meter Constant Scale : Adjusted Ke Scale Factor        
        System.arraycopy(rawData, pos, METER_CONSTANT_SCALE, 0, METER_CONSTANT_SCALE.length);
        this.meter_constant_scale = DataFormat.hex2signed8(METER_CONSTANT_SCALE[0]);
        pos += METER_CONSTANT_SCALE.length;
        log.debug("METER_CONSTANT_SCALE[" + METER_CONSTANT_SCALE[0] + "]");

        //ke 변경
        this.ke = this.ke * Math.pow(10, METER_CONSTANT_SCALE[0]);
        
        log.debug("KE[" + ke + "]");
        
        System.arraycopy(rawData, pos, CT, 0, CT.length);
        pos += CT.length;
        try {
			this.ct = DataFormat.hex2long(DataFormat.LSB2MSB(CT))*instrumentScale.doubleValue();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//        this.ct = new ST15(instrumentScale.doubleValue()).getCT(CT);
        log.debug("CT[" + ct + "]");
        
        System.arraycopy(rawData, pos, VT, 0, VT.length);
        pos += VT.length;
        try {
			this.vt = DataFormat.hex2long(DataFormat.LSB2MSB(VT))*instrumentScale.doubleValue();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        this.vt = new ST15(instrumentScale.doubleValue()).getVT(VT);
        log.debug("VT[" + vt + "]");
        
        System.arraycopy(rawData, pos, METER_ELEMENT, 0, METER_ELEMENT.length);
        pos += METER_ELEMENT.length;
        this.meterElement = new MT51().getMeterElement(METER_ELEMENT);
        log.debug("METER_ELEMENT[" + this.meterElement + "]"); 
        
        System.arraycopy(rawData, pos, METER_STATUS, 0, METER_STATUS.length);
        pos += METER_STATUS.length;
        this.meterLog = new ST03().getMeterStatus(METER_STATUS);
        log.debug("METER_STATUS[" + meterLog + "]");
        
	}
}
