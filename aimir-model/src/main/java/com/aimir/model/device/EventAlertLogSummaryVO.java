package com.aimir.model.device;

/**
 * EventAlertLog의 통계(미니가젯)를 flex에 전달하는 객체
 * @author 최은정(ej8486)
 *
 */
public class EventAlertLogSummaryVO {
	private String type;
	private int value;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
}
