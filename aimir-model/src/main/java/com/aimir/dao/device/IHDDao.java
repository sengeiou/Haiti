package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.IHD;

public interface IHDDao extends GenericDao<IHD, Integer> {
    
    /**
     * method name : getMemberSelectData<b/>
     * method Desc : HomeGroup Management 가젯에서 Member 로 등록 가능한 IHD 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap);
}
