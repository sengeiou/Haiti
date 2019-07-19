package com.aimir.fep.meter.parser.MX2Table;

import java.util.ArrayList;
import java.util.List;

/**
 * MX2 Meter, Summer Time Table Value Object
 * @author kskim
 */
public class SummerTime implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5454740482403262414L;
	private List<SummerTimeDateSet> dateSet1 = new ArrayList<SummerTimeDateSet>();
	private List<SummerTimeDateSet> dateSet2 = new ArrayList<SummerTimeDateSet>();
	private List<SummerTimeDateSet> dateSet3 = new ArrayList<SummerTimeDateSet>();
	
	public List<SummerTimeDateSet> getDateSet2() {
		return dateSet2;
	}

	public void setDateSet2(List<SummerTimeDateSet> dateSet2) {
		this.dateSet2 = dateSet2;
	}

	public List<SummerTimeDateSet> getDateSet3() {
		return dateSet3;
	}

	public void setDateSet3(List<SummerTimeDateSet> dateSet3) {
		this.dateSet3 = dateSet3;
	}

	public List<SummerTimeDateSet> getDateSet1() {
		return dateSet1;
	}

	public void setDateSet1(List<SummerTimeDateSet> dateSet) {
		this.dateSet1 = dateSet;
	}
}