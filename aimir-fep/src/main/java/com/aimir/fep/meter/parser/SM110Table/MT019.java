/** 
 * @(#)MT019.java       1.0 2019-11-07 *
 * 
 * AMR SPI Frame2 Table.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.SM110Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AMR SPI Frame2 Table
 * @author Park Jiwoong wll27471297@nuritelecom.com
 */
public class MT019 implements java.io.Serializable {

	private static final long serialVersionUID = 8231316621429350884L;
	
	private static Log log = LogFactory.getLog(MT019.class);
	
	private byte[] TMP_BYTE = new byte[1];
	
	private byte[] START_CHR = new byte[1];
	// FLAGS : 1 Byte
	private boolean[] RESET_TAMPER  = new boolean[1]; 			// 7
	private boolean[] METERING_PROBLEM  = new boolean[1];		// 6
	private boolean[] REVERSE_ENERGY_FLOW  = new boolean[1];	// 5
	private boolean[] FILLER_1  = new boolean[1];				// 4
	private boolean[] DISPLAY_MULTIPLIER  = new boolean[1];		// 3
	private boolean[] DISPLAY_DIGITS  = new boolean[1];			// 2
	private boolean[] SPI_MESSAGE_TYPE  = new boolean[2];		// 1-0
	// FLAGS_EXT : 1 Byte
	private boolean[] SERVICE_ERROR  = new boolean[1];				// 7
	private boolean[] DC_DETECTION  = new boolean[1];				// 6
	private boolean[] METER_INVERSION_REVERSED  = new boolean[1]; 	// 5 FIXME Check usage
	private boolean[] TEMPERATURE_CAUTION_1  = new boolean[1]; 		// 4 FIXME Check usage
	private boolean[] FILLER_2  = new boolean[2];					// 3-2
	private boolean[] DETENT  = new boolean[2];						// 1-0
	// HISTORY : 1 Byte
	private boolean[] RECEIVED_KWH  = new boolean[1];			// 7
	private boolean[] METER_INVERSION_FLAG  = new boolean[1];	// 6 FIXME Check usage
	private boolean[] DC_CAUTION  = new boolean[1];				// 5
	private boolean[] SERVICE_CAUTION  = new boolean[1];		// 4
	private boolean[] TEMPERATURE_CAUTION_2  = new boolean[1];	// 3 FIXME Check usage
	private boolean[] METER_ERROR  = new boolean[1];			// 2
	private boolean[] NVRAM_CRC  = new boolean[1];				// 1
	private boolean[] NVRAM_I2C  = new boolean[1];				// 0
	
	private byte[] TOTAL_DEL_KWH  = new byte[4];
	private byte[] TOTAL_DEL_PLUS_RCVD_KWH  = new byte[4];
	private byte[] TOTAL_DEL_MINUS_RCVD_KWH  = new byte[4];
	private byte[] TOTAL_REC_KWH  = new byte[4];
	private byte[] INSTANTANEOUS_KW  = new byte[4];
	private byte[] TF  = new byte[2];
	private byte[] RMS_VOLT  = new byte[2];
	private byte[] PF_CTR  = new byte[1];
	private byte[] SAG_CTR  = new byte[1];
	private byte[] SWELL_CTR  = new byte[1];
	private byte[] INVERSION_CTR  = new byte[1];
	private byte[] TEMPERATURE  = new byte[1];
	private byte[] FILLER_3  = new byte[5];
	private byte[] CRC  = new byte[2];

	
//	private byte[] data;
    

	public MT019() {}
	
	public MT019(byte[] data) {
//		this.data = data;
        parse(data);
        printAll();
	}
	public static boolean[] booleanArrayFromByte(byte x) {
	    boolean bs[] = new boolean[8];
	    bs[0] = ((x & 0x01) != 0);
	    bs[1] = ((x & 0x02) != 0);
	    bs[2] = ((x & 0x04) != 0);
	    bs[3] = ((x & 0x08) != 0);
	    bs[4] = ((x & 0x10) != 0);
	    bs[5] = ((x & 0x20) != 0);
	    bs[6] = ((x & 0x40) != 0);
	    bs[7] = ((x & 0x80) != 0);
	    return bs;
	}

	public String booleanArr2Str(boolean[] arr) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(boolean bool : arr) {
			sb.append((bool == true) ? "1" : "0").append(" ");
		}
		sb.append("]");
		return sb.toString();
	}

    public void parse(byte[] data) {
    	int pos = 0;
		int boolPos = 0;
    	boolean[] tmpBoolArr = new boolean[8];
    	
		System.arraycopy(data, pos, START_CHR, 0, START_CHR.length); pos += START_CHR.length;			
		
		System.arraycopy(data, pos, TMP_BYTE, 0, TMP_BYTE.length); pos += TMP_BYTE.length;
		tmpBoolArr = booleanArrayFromByte(TMP_BYTE[0]);
		System.arraycopy(tmpBoolArr, boolPos, RESET_TAMPER, 0, RESET_TAMPER.length); boolPos += RESET_TAMPER.length;
		System.arraycopy(tmpBoolArr, boolPos, METERING_PROBLEM, 0, METERING_PROBLEM.length); boolPos += METERING_PROBLEM.length;
		System.arraycopy(tmpBoolArr, boolPos, REVERSE_ENERGY_FLOW, 0, REVERSE_ENERGY_FLOW.length); boolPos += REVERSE_ENERGY_FLOW.length;
		System.arraycopy(tmpBoolArr, boolPos, FILLER_1, 0, FILLER_1.length); boolPos += FILLER_1.length;
		System.arraycopy(tmpBoolArr, boolPos, DISPLAY_MULTIPLIER, 0, DISPLAY_MULTIPLIER.length); boolPos += DISPLAY_MULTIPLIER.length;
		System.arraycopy(tmpBoolArr, boolPos, DISPLAY_DIGITS, 0, DISPLAY_DIGITS.length); boolPos += DISPLAY_DIGITS.length;
		System.arraycopy(tmpBoolArr, boolPos, SPI_MESSAGE_TYPE, 0, SPI_MESSAGE_TYPE.length); boolPos = 0;

		System.arraycopy(data, pos, TMP_BYTE, 0, TMP_BYTE.length); pos += TMP_BYTE.length;
		tmpBoolArr = booleanArrayFromByte(TMP_BYTE[0]);
		System.arraycopy(tmpBoolArr, boolPos, SERVICE_ERROR, 0, SERVICE_ERROR.length); boolPos += SERVICE_ERROR.length;
		System.arraycopy(tmpBoolArr, boolPos, DC_DETECTION, 0, DC_DETECTION.length); boolPos += DC_DETECTION.length;
		System.arraycopy(tmpBoolArr, boolPos, METER_INVERSION_REVERSED, 0, METER_INVERSION_REVERSED.length); boolPos += METER_INVERSION_REVERSED.length;
		System.arraycopy(tmpBoolArr, boolPos, TEMPERATURE_CAUTION_1, 0, TEMPERATURE_CAUTION_1.length); boolPos += TEMPERATURE_CAUTION_1.length;
		System.arraycopy(tmpBoolArr, boolPos, FILLER_2, 0, FILLER_2.length); boolPos += FILLER_2.length;
		System.arraycopy(tmpBoolArr, boolPos, DETENT, 0, DETENT.length); boolPos = 0;
		
		System.arraycopy(data, pos, TMP_BYTE, 0, TMP_BYTE.length); pos += TMP_BYTE.length;
		tmpBoolArr = booleanArrayFromByte(TMP_BYTE[0]);
		System.arraycopy(tmpBoolArr, boolPos, RECEIVED_KWH, 0, RECEIVED_KWH.length); boolPos += RECEIVED_KWH.length;
		System.arraycopy(tmpBoolArr, boolPos, METER_INVERSION_FLAG, 0, METER_INVERSION_FLAG.length); boolPos += METER_INVERSION_FLAG.length;
		System.arraycopy(tmpBoolArr, boolPos, DC_CAUTION, 0, DC_CAUTION.length); boolPos += DC_CAUTION.length;
		System.arraycopy(tmpBoolArr, boolPos, SERVICE_CAUTION, 0, SERVICE_CAUTION.length); boolPos += SERVICE_CAUTION.length;
		System.arraycopy(tmpBoolArr, boolPos, TEMPERATURE_CAUTION_2, 0, TEMPERATURE_CAUTION_2.length); boolPos += TEMPERATURE_CAUTION_2.length;
		System.arraycopy(tmpBoolArr, boolPos, METER_ERROR, 0, METER_ERROR.length); boolPos += METER_ERROR.length;
		System.arraycopy(tmpBoolArr, boolPos, NVRAM_CRC, 0, NVRAM_CRC.length); boolPos += NVRAM_CRC.length;
		System.arraycopy(tmpBoolArr, boolPos, NVRAM_I2C, 0, NVRAM_I2C.length); boolPos = 0;
		
		System.arraycopy(data, pos, TOTAL_DEL_KWH, 0, TOTAL_DEL_KWH.length); pos += TOTAL_DEL_KWH.length;
		System.arraycopy(data, pos, TOTAL_DEL_PLUS_RCVD_KWH, 0, TOTAL_DEL_PLUS_RCVD_KWH.length); pos += TOTAL_DEL_PLUS_RCVD_KWH.length;
		System.arraycopy(data, pos, TOTAL_DEL_MINUS_RCVD_KWH, 0, TOTAL_DEL_MINUS_RCVD_KWH.length); pos += TOTAL_DEL_MINUS_RCVD_KWH.length;
		System.arraycopy(data, pos, TOTAL_REC_KWH, 0, TOTAL_REC_KWH.length); pos += TOTAL_REC_KWH.length;
		
		System.arraycopy(data, pos, INSTANTANEOUS_KW, 0, INSTANTANEOUS_KW.length); pos += INSTANTANEOUS_KW.length;
		System.arraycopy(data, pos, TF, 0, TF.length); pos += TF.length;
		System.arraycopy(data, pos, RMS_VOLT, 0, RMS_VOLT.length); pos += RMS_VOLT.length;
		System.arraycopy(data, pos, PF_CTR, 0, PF_CTR.length); pos += SPI_MESSAGE_TYPE.length;
		System.arraycopy(data, pos, SAG_CTR, 0, SAG_CTR.length); pos += SAG_CTR.length;
		System.arraycopy(data, pos, SWELL_CTR, 0, SWELL_CTR.length); pos += SWELL_CTR.length;
		System.arraycopy(data, pos, INVERSION_CTR, 0, INVERSION_CTR.length); pos += INVERSION_CTR.length;
		System.arraycopy(data, pos, TEMPERATURE, 0, TEMPERATURE.length); pos += TEMPERATURE.length;
		System.arraycopy(data, pos, FILLER_3, 0, FILLER_3.length); pos += FILLER_3.length;
		System.arraycopy(data, pos, CRC, 0, CRC.length); pos += CRC.length;
		
		log.debug("pos = "+pos+", data.length = "+data.length);
    }
    
	public void printAll() {
		StringBuilder sb = new StringBuilder();
		sb.append("START_CHR="+new String(START_CHR).trim()).append(", ")
		
		  .append("RESET_TAMPER="+booleanArr2Str(RESET_TAMPER).trim()).append(", ")
		  .append("METERING_PROBLEM="+booleanArr2Str(METERING_PROBLEM).trim()).append(", ")
		  .append("REVERSE_ENERGY_FLOW="+booleanArr2Str(REVERSE_ENERGY_FLOW).trim()).append(", ")
		  .append("FILLER_1="+booleanArr2Str(FILLER_1).trim()).append(", ")
		  .append("DISPLAY_MULTIPLIER="+booleanArr2Str(DISPLAY_MULTIPLIER).trim()).append(", ")
		  .append("DISPLAY_DIGITS="+booleanArr2Str(DISPLAY_DIGITS).trim()).append(", ")
		  .append("SPI_MESSAGE_TYPE="+booleanArr2Str(SPI_MESSAGE_TYPE).trim()).append(", ")
		  
		  .append("SERVICE_ERROR="+booleanArr2Str(SERVICE_ERROR).trim()).append(", ")
		  .append("DC_DETECTION="+booleanArr2Str(DC_DETECTION).trim()).append(", ")
		  .append("METER_INVERSION_REVERSED="+booleanArr2Str(METER_INVERSION_REVERSED).trim()).append(", ")
		  .append("TEMPERATURE_CAUTION_1="+booleanArr2Str(TEMPERATURE_CAUTION_1).trim()).append(", ")
		  .append("FILLER_2="+booleanArr2Str(FILLER_2).trim()).append(", ")
		  .append("DETENT="+booleanArr2Str(DETENT).trim()).append(", ")
		  
		  .append("RECEIVED_KWH="+booleanArr2Str(RECEIVED_KWH).trim()).append(", ")
		  .append("METER_INVERSION_FLAG="+booleanArr2Str(METER_INVERSION_FLAG).trim()).append(", ")
		  .append("DC_CAUTION="+booleanArr2Str(DC_CAUTION).trim()).append(", ")
		  .append("SERVICE_CAUTION="+booleanArr2Str(SERVICE_CAUTION).trim()).append(", ")
		  .append("TEMPERATURE_CAUTION_2="+booleanArr2Str(TEMPERATURE_CAUTION_2).trim()).append(", ")
		  .append("METER_ERROR="+booleanArr2Str(METER_ERROR).trim()).append(", ")
		  .append("NVRAM_CRC="+booleanArr2Str(NVRAM_CRC).trim()).append(", ")
		  .append("NVRAM_I2C="+booleanArr2Str(NVRAM_I2C).trim()).append(", ")
		  
		  .append("TOTAL_DEL_KWH="+new String(TOTAL_DEL_KWH).trim()).append(", ")
		  .append("TOTAL_DEL_PLUS_RCVD_KWH="+new String(TOTAL_DEL_PLUS_RCVD_KWH).trim()).append(", ")
		  .append("TOTAL_DEL_MINUS_RCVD_KWH="+new String(TOTAL_DEL_MINUS_RCVD_KWH).trim()).append(", ")
		  .append("TOTAL_REC_KWH="+new String(TOTAL_REC_KWH).trim()).append(", ")
		  .append("INSTANTANEOUS_KW="+new String(INSTANTANEOUS_KW).trim()).append(", ")
		  .append("TF="+new String(TF).trim()).append(", ")
		  .append("RMS_VOLT"+new String(RMS_VOLT).trim()).append(", ")
		  .append("PF_CTR="+new String(PF_CTR).trim()).append(", ")
		  .append("SAG_CTR="+new String(SAG_CTR).trim()).append(", ")
		  .append("SWELL_CTR="+new String(SWELL_CTR).trim()).append(", ")
		  .append("INVERSION_CTR="+new String(INVERSION_CTR).trim()).append(", ")
		  .append("TEMPERATURE="+new String(TEMPERATURE).trim()).append(", ")
		  .append("FILLER_3="+new String(FILLER_3).trim()).append(", ")
		  .append("CRC="+new String(CRC).trim());
		
		log.info("MT019["+sb.toString()+"]");
	}
}
