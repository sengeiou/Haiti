package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.OBISCode;

public interface ObisCodeDao extends GenericDao<OBISCode, Long> {

	public void updateDescr(Map<String,Object> condition) throws Exception;
    public List<Map<String,Object>> getObisCodeInfo(Map<String,Object> condition);

    /**
     * meter model과 service name에 해당하는 DLMS OBISCode를 조회
     * @param condition{modelId, ClassName}
     * @return List of Obiscode row
     */
    public List<Map<String,Object>> getObisCodeInfoByName(Map<String,Object> condition);
    public List<Map<String,Object>> getObisCodeGroup(Map<String,Object> condition);
    public List<Map<String,Object>> getObisCodeWithEvent(Map<String,Object> condition);
    public List<Map<String,Object>> getEventObisCode(String obisFormat);	
	
}
