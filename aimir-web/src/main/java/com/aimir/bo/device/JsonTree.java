package com.aimir.bo.device;

import java.util.List;
import java.util.Map;

public class JsonTree {

	private String data;
	private String state;
	private Map<String, String> attributes;
	private List<JsonTree> children;
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	public List<JsonTree> getChildren() {
		return children;
	}
	public void setChildren(List<JsonTree> children) {
		this.children = children;
	}
	

}
