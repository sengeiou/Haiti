package com.aimir.dao.device;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.MeterCtrl;

public interface MeterCtrlDao extends GenericDao<MeterCtrl, Integer> {

    /**
     * method name : getMeterCommandResultData
     * method Desc : Meter Command 실행 결과 값을 조회한다.
     *
     * @param conditionMap
     * <ul>  
     * <li> ctrlId : MeterCtrl.id.ctrlId
     * <li> meterId : Meter.id
     * <li> writeDate : MeterCtrl.id.writeDate
     * </ul>
     * @return List of com.aimir.model.device.MeterCtrl
     */
    public List<MeterCtrl> getMeterCommandResultData(Map<String, Object> conditionMap);

    /**
     * method name : getMeterCommandRunCheck
     * method Desc : Meter Command 실행 여부를 조회한다.
     *
     * @param conditionMap
     * <ul>  
     * <li> meterId : Meter.id
     * </ul>
     * @return List of com.aimir.model.device.MeterCtrl
     */
    public List<MeterCtrl> getMeterCommandRunCheck(Map<String, Object> conditionMap);

    /**
     * method name : getBulkMeterCommandResultData
     * method Desc : MDIS - Bulk Meter Command 실행 결과 값을 조회한다.
     *
     * @param conditionMap
     * <ul>  
     * <li> ctrlId : MeterCtrl.id.ctrlId
     * <li> meterIdList : List of Meter.id
     * <li> writeDateList : List of MeterCtrl.id.writeDate
     * </ul>
     * @return List of com.aimir.model.device.MeterCtrl
     */
    public List<MeterCtrl> getBulkMeterCommandResultData(Map<String, Object> conditionMap);

    /**
     * method name : getBulkMeterCommandRunCheck
     * method Desc : MDIS - Bulk Meter Command 실행 여부를 조회한다.
     *
     * @param conditionMap
     * <ul>  
     * <li> meterIdList : List of Meter.id
     * </ul>
     * @return Integer - command 실행 중인 Meter 개수
     */
    public Integer getBulkMeterCommandRunCheck(Map<String, Object> conditionMap);
}