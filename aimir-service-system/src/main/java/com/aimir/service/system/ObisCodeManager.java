package com.aimir.service.system;

import java.util.List;
import java.util.Map;

import com.aimir.model.system.OBISCode;

public interface ObisCodeManager {

	public OBISCode getObisCode(Long id);
	
	public void updateDescr(Map<String,Object> condition) throws Exception;

	public List<Map<String,Object>> getObisCodeInfo(Map<String,Object> condition);

    public List<Map<String,Object>> getObisCodeInfoByName(Map<String,Object> condition);
	
	public List<Map<String,Object>> getObisCodeGroup(Map<String,Object> condition);
	
	public List<Map<String,Object>> getObisCodeWithEvent(Map<String,Object> condition);
	
	public Integer getCheckDuplidate(Map<String,Object> condition);
	
	public void add(List<OBISCode> obisSaveList);
	
	public void update(OBISCode updateObisCode) throws Exception;
	
	public void delete(long obisId);
	
}
