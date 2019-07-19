package com.aimir.service.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import com.aimir.model.device.Headend;

@WebService(name="headendService", targetNamespace="http://aimir.com/services")
public interface HeadendManager {
	
	public List<Headend> getLastData();
	
}
