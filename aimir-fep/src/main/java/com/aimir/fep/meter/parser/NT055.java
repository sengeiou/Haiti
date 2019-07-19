/** 
 * @(#)N055.java       1.0 07/10/12 *
 * 
 * MCU metering time Class.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser;

import com.aimir.fep.meter.parser.SM110Table.ST055;

public class NT055 extends ST055{

	private static final long serialVersionUID = 4696353226988884801L;

	public NT055() {}
    
    public NT055(byte[] data){
        super(data);
    }
}
