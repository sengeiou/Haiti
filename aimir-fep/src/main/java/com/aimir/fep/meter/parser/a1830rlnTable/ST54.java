/** 
 * @(#)ST54.java       1.0 06/09/25 *
 * 
 * Calendar Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
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


/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class ST54 implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1461833985850300047L;
	public int OFS_ANCHOR_DATE = 0;
	public int OFS_NON_RECURR_DATES = 2;	
	private int OFS_RECURR_DATES;	
	private int OFS_TIER_SWITCHES;
	private int OFS_FULL_DAILY_SCHEDULES;
	
	private int LEN_NON_RECURR_DATES;
	private int LEN_RECURR_DATES;
	private int LEN_TIER_SWITCHES;
	private int LEN_FULL_DAILY_SCHEDULES;
	
	public int LEN_ANCHOR_DATE = 2;
	public int LEN_NON_RECURR_DATE = 2;
	public int LEN_CALENDAR_ACTION = 1;
	
	public int LEN_RECURR_DATE = 2;
	
	public int LEN_TIER_SWITCH = 2;
	public int LEN_DAY_SCH_NUM = 1;

	public int LEN_SCHEDULE = 1;
	
	private int NBR_SEASONS;
	private int NBR_SPECIAL_SCHED;
	private int NBR_NONE_RECURR_DATES;
	private int NBR_RECURR_DATES;
	private int NBR_TIER_SWITCHES;
	
	private byte[] data;

    private Log logger = LogFactory.getLog(getClass());
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST54(byte[] data,
	            int nbr_seasons,
	            int nbr_special_schd,
				int nbr_none_recurr_dates, 
				int nbr_recurr_dates, 
				int nbr_tier_switches) {
		this.data = data;
		
		this.NBR_SEASONS              = nbr_seasons;
		this.NBR_SPECIAL_SCHED        = nbr_special_schd;
		this.NBR_NONE_RECURR_DATES    = nbr_none_recurr_dates;
		this.NBR_RECURR_DATES         = nbr_recurr_dates;
		this.NBR_TIER_SWITCHES        = nbr_tier_switches;
		
		this.LEN_NON_RECURR_DATES 
			= nbr_none_recurr_dates*(LEN_NON_RECURR_DATE+LEN_CALENDAR_ACTION);
		this.LEN_RECURR_DATES
			= nbr_recurr_dates*(LEN_RECURR_DATE+LEN_CALENDAR_ACTION);
		this.LEN_TIER_SWITCHES
			= nbr_tier_switches*(LEN_TIER_SWITCH+LEN_DAY_SCH_NUM);
		this.LEN_FULL_DAILY_SCHEDULES = NBR_SEASONS*(NBR_SPECIAL_SCHED+7);
			
		this.OFS_RECURR_DATES         = OFS_NON_RECURR_DATES+LEN_NON_RECURR_DATES;	
		this.OFS_TIER_SWITCHES        = OFS_RECURR_DATES+LEN_RECURR_DATES;
		this.OFS_FULL_DAILY_SCHEDULES = OFS_TIER_SWITCHES+LEN_TIER_SWITCHES;

	}	
	
	public byte[] getANCHOR_DATE() throws Exception {
		return DataFormat.select(data,OFS_ANCHOR_DATE,LEN_ANCHOR_DATE);
	}
	
	public byte[] getNON_RECURR_DATES() throws Exception {
		return DataFormat.select(data,OFS_NON_RECURR_DATES,LEN_NON_RECURR_DATES);
	}	
	
	public byte[] getRECURR_DATES() throws Exception {
		return DataFormat.select(data,OFS_RECURR_DATES,LEN_RECURR_DATES);
	}	
	
	public byte[] getTIER_SWITCHES() throws Exception {
		return DataFormat.select(data,OFS_TIER_SWITCHES,LEN_TIER_SWITCHES);
	}
	
	public byte[] getFULL_DAILY_SCHEDULES() throws Exception {
		return DataFormat.select(data,OFS_FULL_DAILY_SCHEDULES,LEN_FULL_DAILY_SCHEDULES);
	}

}
