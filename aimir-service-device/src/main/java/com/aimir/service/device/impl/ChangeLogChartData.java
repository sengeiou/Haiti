package com.aimir.service.device.impl;

public class ChangeLogChartData {

	private int rank;
	private String property;
	private int count;

	public ChangeLogChartData(int rank, String property, int count) {
		this.rank = rank;
		this.property = property;
		this.count = count;
	}
	
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
