/**
 * (@)# Inverter.java
 *
 * 2015. 6. 9.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.Scope;

/**
 * @author simhanger
 * 
 */

@Entity
@DiscriminatorValue("Inverter")
public class Inverter extends Meter {
	private static final long serialVersionUID = 1L;

	@ColumnInfo(name = "출력 전류", view = @Scope(create = true, read = true, update = true), descr = "Output Current")
	@Column(name = "OUTPUT_CURRENT")
	private Double outputCurrent;

	@ColumnInfo(name = "출력 주파수", view = @Scope(create = true, read = true, update = true), descr = "Output Frequency")
	@Column(name = "OUTPUT_FREQUENCY")
	private Double outputFrequency;

	@ColumnInfo(name = "출력 전압", view = @Scope(create = true, read = true, update = true), descr = "Output Voltage")
	@Column(name = "OUTPUT_VOLTAGE")
	private Double outputVoltage;

	public Double getOutputCurrent() {
		return outputCurrent;
	}

	public void setOutputCurrent(Double outputCurrent) {
		this.outputCurrent = outputCurrent;
	}

	public Double getOutputFrequency() {
		return outputFrequency;
	}

	public void setOutputFrequency(Double outputFrequency) {
		this.outputFrequency = outputFrequency;
	}

	public Double getOutputVoltage() {
		return outputVoltage;
	}

	public void setOutputVoltage(Double outputVoltage) {
		this.outputVoltage = outputVoltage;
	}

}
