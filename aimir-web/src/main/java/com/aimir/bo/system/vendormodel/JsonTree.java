package com.aimir.bo.system.vendormodel;

import java.util.ArrayList;
import java.util.List;

public class JsonTree {

	private Object data;
	
	//private Integer attribute;
	private List children = new ArrayList();
	private List children1 = new ArrayList();
	private List children2 = new ArrayList();
	private List children3 = new ArrayList();
	
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	
	public List getChildren() {
		return children;
	}
	public void setChildren(List children) {
		this.children = children;
	}
	public List getChildren1() {
		return children1;
	}
	public void setChildren1(List children1) {
		this.children1 = children1;
	}
	public List getChildren2() {
		return children2;
	}
	public void setChildren2(List children2) {
		this.children2 = children2;
	}
	public List getChildren3() {
		return children3;
	}
	public void setChildren3(List children3) {
		this.children3 = children3;
	}
	
	
	
}
