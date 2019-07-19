/** 
 * @(#)ST026.java       1.0 2008.06.24 *
 * 
 * Self Read Data Class.(TOU Version)
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
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

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.fep.meter.parser.SM110Table.GE_ANSI_CONSTANT.MeterMode;
import com.aimir.fep.util.DataFormat;

/**
 * @author YK.Park
 */
public class ST026 implements java.io.Serializable {

	private static final long serialVersionUID = -874472760368322996L;

	private byte[] data;

    private static Log log = LogFactory.getLog(ST026.class);

    private ST025[] st025 = null;
    
    protected final int OFS_LIST_STATUS = 0;
    protected final int OFS_NBR_VALID_ENTRIES = 1;
    protected final int OFS_LAST_ENTRY_ELEMENT = 2;
    protected final int OFS_LAST_ENTRY_SEQ_NUMBER = 3;
    protected final int OFS_NBR_UNREAD_ENTRIES = 5;
    protected final int OFS_SELF_READ_ENTRIES = 6;

	private int NBR_TIERS;
	private int NBR_SUM;
	private int NBR_DMD;
	private int NBR_COIN;
    private int energyscale;
    private int powerscale;
    private int displayscale;
    private int dispmult;
    private MeterMode meterMode;
    
    public ST026() {}
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST026(byte[] data, int nbr_tiers, int nbr_sum, int nbr_dmd, int nbr_coin,
                              int energyscale,int powerscale, int displayscale, int dispmult) 
    {
		this.data = data;
		this.NBR_TIERS = nbr_tiers;
		this.NBR_SUM   = nbr_sum;
		this.NBR_DMD   = nbr_dmd;
		this.NBR_COIN  = nbr_coin;
        this.energyscale = energyscale;
        this.powerscale = powerscale;
        this.displayscale = displayscale;
        this.dispmult = dispmult;

	}

	public MeterMode getMeterMode() {
		return meterMode;
	}
	
	public void setMeterMode(MeterMode meterMode) {
		this.meterMode = meterMode;
	}
	
	public void setMeterMode(int meterMode){
    	this.meterMode = GE_ANSI_CONSTANT.getMeterMode(meterMode);
	}
	
	public void parseData() throws Exception 
    {
        int offset = OFS_SELF_READ_ENTRIES;
        int len = 732;//st025 block size
        if(meterMode == null || meterMode.getCode() == MeterMode.TOU.getCode()){
            len = 732;//st025 block size(tou)
        }else if(meterMode.getCode() == MeterMode.DemandLp.getCode()){
        	len = 241;//st025 block size(demand)
        }
        
        log.info("size="+len);

        //int nbr_valid_entries = getNBR_VALID_ENTRIES();
        int nbr_valid_entries = (data.length - OFS_SELF_READ_ENTRIES)/len;

        log.info("nbr_valid_entries="+nbr_valid_entries);
        if( nbr_valid_entries  > 0)
        {
            st025 = new ST025[nbr_valid_entries];
            for(int i = 0; i < nbr_valid_entries; i++)
            {
                //log.debug("selfread="+Util.getHexString(DataFormat.select(data,offset,len)));
                st025[i] = new ST025(DataFormat.select(data,offset,len),
                                     NBR_TIERS, NBR_SUM, NBR_DMD, NBR_COIN,
                                     energyscale, powerscale, displayscale, dispmult);
                offset += (i+1)*len;  

                //log.debug("resettime="+st025[i].getResetTime());
            }
        }
	}
    
    public ST025[] getSelfReads()
    {
        return this.st025;
    }
    
    public int getNBR_VALID_ENTRIES()
    {
        //NBR_VALID_ENTRIES
        return DataFormat.hex2unsigned8(data[OFS_NBR_VALID_ENTRIES]);
    }
    
    public int getLAST_ENTRY_ELEMENT()
    {
        //LAST_ENTRY_ELEMENT
        return DataFormat.hex2unsigned8(data[OFS_LAST_ENTRY_ELEMENT]);
    }
    
    public int getNBR_UNREAD_ENTRIES()
    {
        //LAST_ENTRY_ELEMENT
        return DataFormat.hex2unsigned8(data[OFS_NBR_UNREAD_ENTRIES]);
    }
}