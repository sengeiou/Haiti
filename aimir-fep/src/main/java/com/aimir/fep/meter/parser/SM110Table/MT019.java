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

import com.aimir.fep.util.DataFormat;

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

	public MT019() {}
	
	public MT019(byte[] data) {
        parse(data);
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
		System.arraycopy(data, pos, PF_CTR, 0, PF_CTR.length); pos += PF_CTR.length;
		System.arraycopy(data, pos, SAG_CTR, 0, SAG_CTR.length); pos += SAG_CTR.length;
		System.arraycopy(data, pos, SWELL_CTR, 0, SWELL_CTR.length); pos += SWELL_CTR.length;
		System.arraycopy(data, pos, INVERSION_CTR, 0, INVERSION_CTR.length); pos += INVERSION_CTR.length;
		System.arraycopy(data, pos, TEMPERATURE, 0, TEMPERATURE.length); pos += TEMPERATURE.length;
		System.arraycopy(data, pos, FILLER_3, 0, FILLER_3.length); pos += FILLER_3.length;
		System.arraycopy(data, pos, CRC, 0, CRC.length); pos += CRC.length;
		
		log.debug("pos = "+pos+", data.length = "+data.length);
    }
    
	public Double getTOTAL_DEL_KWH() throws Exception {
		return Double.valueOf(DataFormat.hex2dec(TOTAL_DEL_KWH));
	}

	public Double getTOTAL_DEL_PLUS_RCVD_KWH() throws Exception {
		return Double.valueOf(DataFormat.hex2dec(TOTAL_DEL_PLUS_RCVD_KWH));
	}

	public Double getTOTAL_DEL_MINUS_RCVD_KWH() throws Exception {
		return Double.valueOf(DataFormat.hex2dec(TOTAL_DEL_MINUS_RCVD_KWH));
	}

	public Double getTOTAL_REC_KWH() throws Exception {
		return Double.valueOf(DataFormat.hex2dec(TOTAL_REC_KWH));
	}


	public String printAll() {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("  START_CHR="+new String(START_CHR).trim()).append(", \n")
			
			  .append("  RESET_TAMPER="+booleanArr2Str(RESET_TAMPER).trim()).append(", \n")
			  .append("  METERING_PROBLEM="+booleanArr2Str(METERING_PROBLEM).trim()).append(", \n")
			  .append("  REVERSE_ENERGY_FLOW="+booleanArr2Str(REVERSE_ENERGY_FLOW).trim()).append(", \n")
			  .append("  FILLER_1="+booleanArr2Str(FILLER_1).trim()).append(", \n")
			  .append("  DISPLAY_MULTIPLIER="+booleanArr2Str(DISPLAY_MULTIPLIER).trim()).append(", \n")
			  .append("  DISPLAY_DIGITS="+booleanArr2Str(DISPLAY_DIGITS).trim()).append(", \n")
			  .append("  SPI_MESSAGE_TYPE="+booleanArr2Str(SPI_MESSAGE_TYPE).trim()).append(", \n")
			  
			  .append("  SERVICE_ERROR="+booleanArr2Str(SERVICE_ERROR).trim()).append(", \n")
			  .append("  DC_DETECTION="+booleanArr2Str(DC_DETECTION).trim()).append(", \n")
			  .append("  METER_INVERSION_REVERSED="+booleanArr2Str(METER_INVERSION_REVERSED).trim()).append(", \n")
			  .append("  TEMPERATURE_CAUTION_1="+booleanArr2Str(TEMPERATURE_CAUTION_1).trim()).append(", \n")
			  .append("  FILLER_2="+booleanArr2Str(FILLER_2).trim()).append(", \n")
			  .append("  DETENT="+booleanArr2Str(DETENT).trim()).append(", \n")
			  
			  .append("  RECEIVED_KWH="+booleanArr2Str(RECEIVED_KWH).trim()).append(", \n")
			  .append("  METER_INVERSION_FLAG="+booleanArr2Str(METER_INVERSION_FLAG).trim()).append(", \n")
			  .append("  DC_CAUTION="+booleanArr2Str(DC_CAUTION).trim()).append(", \n")
			  .append("  SERVICE_CAUTION="+booleanArr2Str(SERVICE_CAUTION).trim()).append(", \n")
			  .append("  TEMPERATURE_CAUTION_2="+booleanArr2Str(TEMPERATURE_CAUTION_2).trim()).append(", \n")
			  .append("  METER_ERROR="+booleanArr2Str(METER_ERROR).trim()).append(", \n")
			  .append("  NVRAM_CRC="+booleanArr2Str(NVRAM_CRC).trim()).append(", \n")
			  .append("  NVRAM_I2C="+booleanArr2Str(NVRAM_I2C).trim()).append(", \n")
			  
			  .append("  TOTAL_DEL_KWH="+DataFormat.hex2dec(TOTAL_DEL_KWH)).append(", \n")
			  .append("  TOTAL_DEL_PLUS_RCVD_KWH="+DataFormat.hex2dec(TOTAL_DEL_PLUS_RCVD_KWH)).append(", \n")
			  .append("  TOTAL_DEL_MINUS_RCVD_KWH="+DataFormat.hex2dec(TOTAL_DEL_MINUS_RCVD_KWH)).append(", \n")
			  .append("  TOTAL_REC_KWH="+DataFormat.hex2dec(TOTAL_REC_KWH)).append(", \n")
			  .append("  INSTANTANEOUS_KW="+DataFormat.hex2dec(INSTANTANEOUS_KW)).append(", \n")
			  .append("  TF="+DataFormat.hex2dec(TF)).append(", \n")
			  .append("  RMS_VOLT="+DataFormat.hex2dec(RMS_VOLT)).append(", \n")
			  .append("  PF_CTR="+DataFormat.hex2dec(PF_CTR)).append(", \n")
			  .append("  SAG_CTR="+DataFormat.hex2dec(SAG_CTR)).append(", \n")
			  .append("  SWELL_CTR="+DataFormat.hex2dec(SWELL_CTR)).append(", \n")
			  .append("  INVERSION_CTR="+DataFormat.hex2dec(INVERSION_CTR)).append(", \n")
			  .append("  TEMPERATURE="+DataFormat.hex2dec(TEMPERATURE)).append(", \n")
			  .append("  CRC="+DataFormat.hex2dec(CRC));
		} catch (Exception e) {
			log.error(e,e);
		}
		
		return "MT019[\n"+sb.toString()+"\n]\n";
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
		sb.append("[ ");
		for(boolean bool : arr) {
			sb.append((bool == true) ? "1" : "0").append(" ");
		}
		sb.append("]");
		return sb.toString();
	}
}
