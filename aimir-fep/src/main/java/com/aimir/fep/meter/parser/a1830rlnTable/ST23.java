/** 
 * @(#)ST23.java       1.0 06/06/23 *
 * 
 * Current Billing Data Class.
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a1830rlnTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;


/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class ST23 implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4778759778194665882L;

	private final int OFS_DATA_FIELD_START = 1;

	/* Total Block */
	private int OFS_A_WH   = -1;//changeble |
	private int OFS_R_VARH = -1;
	private int OFS_PF     = -1;
	
	private int OFS_A_MAX_W_TIME = -1;
	private int OFS_A_CUM_W      = -1;
	private int OFS_A_MAX_W      = -1;
	
	private int OFS_R_MAX_W_TIME = -1;
	private int OFS_R_CUM_W      = -1;
	private int OFS_R_MAX_W      = -1;

	private final int LEN_WH_TOTAL = 6;
	private final int LEN_MAX_W_TOTAL      = 5;
	private final int LEN_MAX_W_TOTAL_TIME = 5;

	private final int LEN_RATE_WH         = 6;
	private final int LEN_RATE_CUM_W      = 6;
	private final int LEN_RATE_MAX_W      = 5;
	private final int LEN_RATE_MAX_W_TIME = 5;	

	private int NBR_TIERS;
	private int NBR_SUM;
	private int NBR_DMD;
	private int NBR_COIN;
	
	private byte[] data;
	
	private byte[] total_tou_blk;
	private byte[] rate1_tou_blk;
	private byte[] rate2_tou_blk;
	private byte[] rate3_tou_blk;
	
//	MT15
//	energyscale	= EXTERNAL_MULTIPLIER_SCALE_FACTOR INT8
//	dispmult	= EXTERNAL_MULTIPLIER UINT32
//	displayscale = ADJ_KE_SCALE_FACTOR INT8
	private double energyscaleXdispmult ;
    private int energyscale;
    private int powerscale;
    private int displayscale;
    private int dispmult;

    private Log logger = LogFactory.getLog(getClass());
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST23(byte[] data, int nbr_tiers, int nbr_sum, int nbr_dmd, int nbr_coin, double ke, int meterConstantScale) {
		this.data = data;
		this.NBR_TIERS = nbr_tiers;
		this.NBR_SUM   = nbr_sum;
		this.NBR_DMD   = nbr_dmd;
		this.NBR_COIN  = nbr_coin;
		this.energyscaleXdispmult = ke;
		this.displayscale = meterConstantScale;
		try {
			getOffset();
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}

	private void getOffset() throws Exception {
		
		int offset = OFS_DATA_FIELD_START;
		
		int tou_blk_size 
			= this.NBR_SUM*6 + this.NBR_DMD*(5+6+5) + this.NBR_COIN*5;
			
		total_tou_blk = new byte[tou_blk_size];
		rate1_tou_blk = new byte[tou_blk_size];
		rate2_tou_blk = new byte[tou_blk_size];
		rate3_tou_blk = new byte[tou_blk_size];
		
		if(this.NBR_TIERS <= 4){
			System.arraycopy(data,offset,total_tou_blk,0,tou_blk_size);
			offset += tou_blk_size;
			System.arraycopy(data,offset,rate1_tou_blk,0,tou_blk_size);
			offset += tou_blk_size;
			System.arraycopy(data,offset,rate2_tou_blk,0,tou_blk_size);
			offset += tou_blk_size;
			System.arraycopy(data,offset,rate3_tou_blk,0,tou_blk_size);
			offset += tou_blk_size;
		}
		//logger.debug("total : \n"+Util.getHexString(total_tou_blk));
		//logger.debug("rate1 : \n"+Util.getHexString(rate1_tou_blk));
		//logger.debug("rate2 : \n"+Util.getHexString(rate2_tou_blk));
		//logger.debug("rate3 : \n"+Util.getHexString(rate3_tou_blk));

		int i = this.NBR_SUM;
		OFS_A_WH   = (this.NBR_SUM-i)*6;
		i--;
		OFS_R_VARH = (this.NBR_SUM-i)*6;
		i--;
		OFS_PF     = (this.NBR_SUM-i)*6;
		i--;
	
		OFS_A_MAX_W_TIME = this.NBR_SUM*6;		
		OFS_A_CUM_W      = this.NBR_SUM*6+5;
		OFS_A_MAX_W      = this.NBR_SUM*6+5+6;
	
		OFS_R_MAX_W_TIME = OFS_A_MAX_W+5;
		OFS_R_CUM_W      = OFS_R_MAX_W_TIME+5;
		OFS_R_MAX_W      = OFS_R_MAX_W_TIME+5+6;

	}
	
	public byte[] parseTOUBlk() throws Exception {
		return this.total_tou_blk;
	}
	
	public byte[] parseAWHTOT() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(total_tou_blk,OFS_A_WH,LEN_WH_TOTAL));
	}
	
	public byte[] parseRWHTOT() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(total_tou_blk,OFS_R_VARH,LEN_WH_TOTAL));
	}
	
	public byte[] parseARATE1WH() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate1_tou_blk,OFS_A_WH,LEN_RATE_WH));
	}

	public byte[] parseARATE2WH() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate2_tou_blk,OFS_A_WH,LEN_RATE_WH));
	}
	
	public byte[] parseARATE3WH() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate3_tou_blk,OFS_A_WH,LEN_RATE_WH));
	}

	public byte[] parseRRATE1WH() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate1_tou_blk,OFS_R_VARH,LEN_RATE_WH));
	}

	public byte[] parseRRATE2WH() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate2_tou_blk,OFS_R_VARH,LEN_RATE_WH));
	}
	
	public byte[] parseRRATE3WH() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate3_tou_blk,OFS_R_VARH,LEN_RATE_WH));
	}

	public byte[] parseARATE1MAXW() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate1_tou_blk,OFS_A_MAX_W,LEN_RATE_MAX_W));
	}
	
	public byte[] parseARATE2MAXW() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate2_tou_blk,OFS_A_MAX_W,LEN_RATE_MAX_W));
	}
	
	public byte[] parseARATE3MAXW() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate3_tou_blk,OFS_A_MAX_W,LEN_RATE_MAX_W));
	}

	public byte[] parseARATE1MAXWTIME() throws Exception {
		return parseYyyymmddhhmm(
			rate1_tou_blk,OFS_A_MAX_W_TIME,LEN_RATE_MAX_W_TIME);
	}
	
	public byte[] parseARATE2MAXWTIME() throws Exception {
		return parseYyyymmddhhmm(
			rate2_tou_blk,OFS_A_MAX_W_TIME,LEN_RATE_MAX_W_TIME);
	}
	
	public byte[] parseARATE3MAXWTIME() throws Exception {
		return parseYyyymmddhhmm(
			rate3_tou_blk,OFS_A_MAX_W_TIME,LEN_RATE_MAX_W_TIME);
	}
	
	public byte[] parseRRATE1MAXW() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate1_tou_blk,OFS_R_MAX_W,LEN_RATE_MAX_W));
	}
	
	public byte[] parseRRATE2MAXW() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate2_tou_blk,OFS_R_MAX_W,LEN_RATE_MAX_W));
	}
	
	public byte[] parseRRATE3MAXW() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate3_tou_blk,OFS_R_MAX_W,LEN_RATE_MAX_W));
	}

	public byte[] parseRRATE1MAXWTIME() throws Exception {
		return parseYyyymmddhhmm(
			rate1_tou_blk,OFS_R_MAX_W_TIME,LEN_RATE_MAX_W_TIME);
	}
	
	public byte[] parseRRATE2MAXWTIME() throws Exception {
		return parseYyyymmddhhmm(
			rate2_tou_blk,OFS_R_MAX_W_TIME,LEN_RATE_MAX_W_TIME);
	}
	
	public byte[] parseRRATE3MAXWTIME() throws Exception {
		return parseYyyymmddhhmm(
			rate3_tou_blk,OFS_R_MAX_W_TIME,LEN_RATE_MAX_W_TIME);
	}
	
	public byte[] parseARATE1CUMW() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate1_tou_blk,OFS_A_CUM_W,LEN_RATE_CUM_W)); 
	}
	
	public byte[] parseARATE2CUMW() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate2_tou_blk,OFS_A_CUM_W,LEN_RATE_CUM_W)); 
	}
	
	public byte[] parseARATE3CUMW() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate3_tou_blk,OFS_A_CUM_W,LEN_RATE_CUM_W)); 
	}
	
	public byte[] parseRRATE1CUMW() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate1_tou_blk,OFS_R_CUM_W,LEN_RATE_CUM_W)); 
	}
	
	public byte[] parseRRATE2CUMW() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate2_tou_blk,OFS_R_CUM_W,LEN_RATE_CUM_W)); 
	}
	
	public byte[] parseRRATE3CUMW() throws Exception {
		return DataFormat.LSB2MSB(
				DataFormat.select(rate3_tou_blk,OFS_R_CUM_W,LEN_RATE_CUM_W)); 
	}

	
	public Double getAWHTOT() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							total_tou_blk,OFS_A_WH,LEN_WH_TOTAL)));
		return new Double(val/Math.pow(10,(9+displayscale))); // energyscaleXdispmult -> energyscaleXdispmult
	}
	
	public Double getRWHTOT() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							total_tou_blk,OFS_R_VARH,LEN_WH_TOTAL)));
		return new Double(val/Math.pow(10,(9+displayscale)));
	}
	
	public Double getARATE1WH() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate1_tou_blk,OFS_A_WH,LEN_RATE_WH)));
		return new Double(val/Math.pow(10,(9+displayscale)));
	}

	public Double getARATE2WH() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate2_tou_blk,OFS_A_WH,LEN_RATE_WH)));
		return new Double(val/Math.pow(10,(9+displayscale)));
	}
	
	public Double getARATE3WH() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate3_tou_blk,OFS_A_WH,LEN_RATE_WH)));
		return new Double(val/Math.pow(10,(9+displayscale)));
	}

	public Double getRRATE1WH() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate1_tou_blk,OFS_R_VARH,LEN_RATE_WH)));
		return new Double(val/Math.pow(10,(9+displayscale)));
	}

	public Double getRRATE2WH() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate2_tou_blk,OFS_R_VARH,LEN_RATE_WH)));
		return new Double(val/Math.pow(10,(9+displayscale)));
	}
	
	public Double getRRATE3WH() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate3_tou_blk,OFS_R_VARH,LEN_RATE_WH)));
		return new Double(val/Math.pow(10,(9+displayscale)));
	}

	public Double getARATE1MAXW() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate1_tou_blk,OFS_A_MAX_W,LEN_RATE_MAX_W)));
		return new Double(val/Math.pow(10,(9+displayscale)));
	}
	
	public Double getARATE2MAXW() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate2_tou_blk,OFS_A_MAX_W,LEN_RATE_MAX_W)));
		return new Double(val/Math.pow(10,(9+displayscale)));
	}
	
	public Double getARATE3MAXW() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate3_tou_blk,OFS_A_MAX_W,LEN_RATE_MAX_W)));
		return new Double(val/Math.pow(10,(9+displayscale)));
	}

	public String getARATE1MAXWTIME() throws Exception {
		return getYyyymmddhhmm(
				rate1_tou_blk,OFS_A_MAX_W_TIME,LEN_RATE_MAX_W_TIME);
	}
	
	public String getARATE2MAXWTIME() throws Exception {
		return getYyyymmddhhmm(
				rate2_tou_blk,OFS_A_MAX_W_TIME,LEN_RATE_MAX_W_TIME);
	}
	
	public String getARATE3MAXWTIME() throws Exception {
		return getYyyymmddhhmm(
				rate3_tou_blk,OFS_A_MAX_W_TIME,LEN_RATE_MAX_W_TIME);
	}

	public Double getRRATE1MAXW() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate1_tou_blk,OFS_R_MAX_W,LEN_RATE_MAX_W)));
		return new Double(val/Math.pow(10,(9+displayscale)));
	}
	
	public Double getRRATE2MAXW() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate2_tou_blk,OFS_R_MAX_W,LEN_RATE_MAX_W)));
		return new Double(val/Math.pow(10,(9+displayscale)));
	}
	
	public Double getRRATE3MAXW() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate3_tou_blk,OFS_R_MAX_W,LEN_RATE_MAX_W)));
		return new Double(val/Math.pow(10,(9+displayscale)));
	}

	public String getRRATE1MAXWTIME() throws Exception {
		return getYyyymmddhhmm(
				rate1_tou_blk,OFS_R_MAX_W_TIME,LEN_RATE_MAX_W_TIME);
	}
	
	public String getRRATE2MAXWTIME() throws Exception {
		return getYyyymmddhhmm(
				rate2_tou_blk,OFS_R_MAX_W_TIME,LEN_RATE_MAX_W_TIME);
	}
	
	public String getRRATE3MAXWTIME() throws Exception {
		return getYyyymmddhhmm(
				rate3_tou_blk,OFS_R_MAX_W_TIME,LEN_RATE_MAX_W_TIME);
	}

	public Double getARATE1CUMW() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate1_tou_blk,OFS_A_CUM_W,LEN_RATE_CUM_W))); 
		return new Double(val/Math.pow(10,(9+displayscale)));
	}
	
	public Double getARATE2CUMW() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate2_tou_blk,OFS_A_CUM_W,LEN_RATE_CUM_W)));  
		return new Double(val/Math.pow(10,(9+displayscale)));
	}
	
	public Double getARATE3CUMW() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate3_tou_blk,OFS_A_CUM_W,LEN_RATE_CUM_W))); 
		return new Double(val/Math.pow(10,(9+displayscale)));
	}
	
	public Double getRRATE1CUMW() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate1_tou_blk,OFS_R_CUM_W,LEN_RATE_CUM_W))); 
		return new Double(val/Math.pow(10,(9+displayscale)));
	}
	
	public Double getRRATE2CUMW() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate2_tou_blk,OFS_R_CUM_W,LEN_RATE_CUM_W)));  
		return new Double(val/Math.pow(10,(9+displayscale)));
	}
	
	public Double getRRATE3CUMW() throws Exception {
		long val = DataFormat.hex2signeddec(
					DataFormat.LSB2MSB(
						DataFormat.select(
							rate3_tou_blk,OFS_R_CUM_W,LEN_RATE_CUM_W)));  
		return new Double(val/Math.pow(10,(9+displayscale)));
	}

	
	public float getPF() throws Exception {
		
		double ch1 = getAWHTOT();
		double ch2 = getAWHTOT();
		float  pf   = (float)(ch1/(Math.sqrt(Math.pow(ch1,2)+Math.pow(ch2,2))));

		if(ch1 == 0 && ch2 == 0)
			pf = (float) 1.0;
		/* Parsing Transform Results put Data Class */
		if(pf < 0 || pf > 1.0)
			throw new Exception("BILL PF DATA FORMAT ERROR : "+pf);

		return pf;
	}

	
	private byte[] parseYyyymmddhhmm(byte[] b, int offset, int len)
						throws Exception {

		byte[] datetime = new byte[6];
		
		int blen = b.length;
		if(blen-offset < 5)
			throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
		if(len != 5)
			throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);
		
		int idx = offset;
		int yy = DataFormat.hex2unsigned8(b[idx++]);
		int mm = DataFormat.hex2unsigned8(b[idx++]);
		int dd = DataFormat.hex2unsigned8(b[idx++]);
		int hh = DataFormat.hex2unsigned8(b[idx++]);
		int MM = DataFormat.hex2unsigned8(b[idx++]);
		
		int currcen = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
	
		int year   = yy;
		if(year != 0){
			year = yy + currcen;
		}

		datetime[0] = (byte)((year >> 8) & 0xff);
		datetime[1] = (byte)(year & 0xff);
		datetime[2] = (byte) mm;
		datetime[3] = (byte) dd;
		datetime[4] = (byte) hh;
		datetime[5] = (byte) MM;
		
		return datetime;
		
	}
	
	private String getYyyymmddhhmm(byte[] b, int offset, int len)
							throws Exception {
		
		int blen = b.length;
		if(blen-offset < 5)
			throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
		if(len != 5)
			throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);
		
		int idx = offset;
		
		int yy = DataFormat.hex2unsigned8(b[idx++]);
		int mm = DataFormat.hex2unsigned8(b[idx++]);
		int dd = DataFormat.hex2unsigned8(b[idx++]);
		int hh = DataFormat.hex2unsigned8(b[idx++]);
		int MM = DataFormat.hex2unsigned8(b[idx++]);

		StringBuffer ret = new StringBuffer();
		
		int currcen = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
	
		int year   = yy;
		if(year != 0){
			year = yy + currcen;
		}
		
		ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
		
		return ret.toString();
			
	}
	

}
