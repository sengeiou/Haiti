package com.aimir.fep.meter.parser.MX2Table;

import java.util.Date;

/**
 * Summer Time Data Struct
 * 
 * @author kskim
 */
public class SummerTimeDateSet implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9094981296871805170L;
	Date startDate;
	Date endDate;

	public SummerTimeDateSet() {
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
