package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import com.aimir.service.device.bean.OpCodeConvertMap;

public interface LogAnalysisManager {
    OpCodeConvertMap getOpCodeConvertMap(String filePath);

    List<Map<String, Object>> getTotalTreeGridData(Map<String, String> conditionMap);

    List<Map<String, Object>> getTotalTreeGridOper(Map<String, String> conditionMap);
}