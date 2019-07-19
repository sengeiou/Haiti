/*
 * @(#)Mk6N_BTB.java       1.0 2008/08/19 *
 *
 * Billing Total.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.meter.parser.Mk6NTable;

import com.aimir.fep.meter.data.TOU_BLOCK;

/**
 * @author kaze kaze@nuritelecom.com
 */
public class Mk6N_BTB extends BillingData implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7823447075785094346L;

	/**
	 * Constructor .<p>
	 *
	 * @param data - read data (header,crch,crcl)
	 */
    public Mk6N_BTB(String billingType, String meterId,TOU_BLOCK[] prev_block, byte[] rawData, String resetTime, int currentSeason, int resetCnt, int channelCnt, int activeChannelCnt, int reActiveChannelCnt) {
        super(billingType, meterId, prev_block, rawData, resetTime, currentSeason, resetCnt, channelCnt, activeChannelCnt, reActiveChannelCnt);
    }
}