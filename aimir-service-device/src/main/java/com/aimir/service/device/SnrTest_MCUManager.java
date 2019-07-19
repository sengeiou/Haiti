package com.aimir.service.device;

import java.util.List;
import java.util.Map;

public interface SnrTest_MCUManager {
	/**
     * method name : getMcuSnrList<b/>
     * method Desc : 집중기에 연결된 모뎀들의 SNR 리스트를 조회한다. (마지막값, 평균,최대,최소)
     * @param condition
     * @return
     */
    public Map<String,Map<String,Object>> getMcuSnrList (Map<String,Object> condition);
    
    /**
     * method name : getModemSnrList<b/>
     * method Desc : 선택된 모뎀의 시간별 SNR 데이터를 조회한다. (차트,그리드 생성)
     * @param condition
     * @return
     */
    public List<Map<String,Object>> getModemSnrList (Map<String,Object> condition);
}
