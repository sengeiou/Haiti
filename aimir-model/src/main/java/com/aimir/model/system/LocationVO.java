package com.aimir.model.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONSerializer;
import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.model.device.EndDevice;

public class LocationVO implements JSONString, Comparable<Object> {
	private Integer id;
	private String name;
	private int orderNo=0;
	private List<LocationVO> children = new ArrayList<LocationVO>();
	private List<EndDevice> endDeviceChild = new ArrayList<EndDevice>();
	

	public LocationVO(Location location) {
		this.id = location.getId();
		this.name = location.getName();
		this.orderNo = location.getOrderNo();

	}
	public LocationVO(Code code) {
		this.id = code.getId();
		this.name = code.getDescr();	

	}
	
	public LocationVO(Zone zone) {
		this.id = zone.getId();
		this.name = zone.getName();	
		this.orderNo = zone.getOrderNo();	
	}
	
	public void setLocationVO(List<LocationVO> children) {
		this.children = children;
	}

	public void setEndDevice(List<EndDevice> endDevice) {
		this.endDeviceChild = endDevice;
	}

	public int getOrderNo() {
		return this.orderNo;
	}

	@Override
	public String toString() {
		return "locationVO:" + toJSONString();
	}

	public String toJSONString() {
		JSONStringer js = null;

		try {
			js = new JSONStringer();
			js.object().key("id").value(this.id).key("level").value("location")
					.key("name").value(this.name).key("children").array();

			Collections.sort(this.children);
			Iterator<LocationVO> it = this.children.iterator();
			while (it.hasNext()) {
				LocationVO location = (LocationVO) it.next();
				js.value(JSONSerializer.toJSON(location.toJSONString()));
			}
			Iterator<EndDevice> endDeviceIt = this.endDeviceChild.iterator();
			setJsonEndDevice(js, endDeviceIt);

			js.endObject();
		} catch (Exception e) {
			System.out.println(e);
		}

		return js.toString();
	}

	public void setJsonEndDevice(JSONStringer js,
			Iterator<EndDevice> endDeviceIt) {

		if (endDeviceIt.hasNext()) {
			while (endDeviceIt.hasNext()) {
				EndDevice endDevice = endDeviceIt.next();
				js.value(JSONSerializer.toJSON(endDevice.toJSONString()));
			}
			js.endArray();
		} else {
			js.endArray();
		}
	}

	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		int result = 0;
		if (this.getOrderNo() > ((LocationVO) o).getOrderNo())
			result = 1;
		else if (this.getOrderNo() == ((LocationVO) o).getOrderNo())
			result = 0;
		return result;
	}

}
