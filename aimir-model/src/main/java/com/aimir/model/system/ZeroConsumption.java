/**
 * (@)# ZeroConsumption.java
 *
 * 2014. 9. 18.
 *
 * Copyright (c) 2012 NURITelecom, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * NURITelecom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITelecom, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.model.system;

import com.aimir.model.BaseObject;

/**
 * @author nuri
 *
 */
public class ZeroConsumption extends BaseObject{
	private static final long serialVersionUID = 1L;

	private String contractId;

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

}
