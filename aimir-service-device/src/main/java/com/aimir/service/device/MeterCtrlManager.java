package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import com.aimir.model.device.MeterCtrl;


/**
 * MeterCtrlManager.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 11. 11.   v1.0       문동규         
 * </pre>
 */
public interface MeterCtrlManager {

    /**
     * method name : saveMeterCommand<b/>
     * method Desc : Command 버튼 클릭 시 MeterCtrl 에 데이터를 insert 한다.
     *
     * @param conditionMap
     * @return
     */
    public MeterCtrl saveMeterCommand(Map<String, Object> conditionMap);

    /**
     * method name : getMeterCommandResultData<b/>
     * method Desc : Meter Command 실행 결과 값을 조회한다.
     *
     * @param conditionMap
     * <ul>  
     * <li> ctrlId : MeterCtrl.id.ctrlId
     * <li> meterId : Meter.id
     * <li> writeDate : MeterCtrl.id.writeDate
     * </ul>
     * @return Map {status : String - complete/error/progress
     *              msg : String - result message
     *             }
     */
    public Map<String, Object> getMeterCommandResultData(Map<String, Object> conditionMap);

    /**
     * method name : getMeterCommandRunCheck<b/>
     * method Desc : Meter Command 실행 여부를 조회한다. 실행 중인 command 가 있으면 해당 정보를 리턴한다.
     *
     * @param conditionMap
     * <ul>  
     * <li> meterId : Meter.id
     * </ul>
     * @return com.aimir.model.device.MeterCtrl
     */
    public MeterCtrl getMeterCommandRunCheck(Map<String, Object> conditionMap);

    /**
     * method name : saveBulkMeterCommand<b/>
     * method Desc : Bulk Meter Command mode 에서 Command 버튼 클릭 시 MeterCtrl 에 각 Meter 별 데이터를 insert 한다.
     *
     * @param conditionMap
     * <ul>
     * <li> meterCommand : com.aimir.constants.CommonConstants.MeterCommand
     * <li> ctrlId : com.aimir.model.device.MeterCtrl.id.ctrlId
     * <li> meterKind : MdisMeter.meterKind
     * <li> lcdDispScroll : MdisMeter.lcdDispScroll
     * <li> lcdDispContent : MdisMeter.lcdDispContentPost/MdisMeter.lcdDispContentPre
     * <li> meterId : Meter.id
     * <li> prepaidRate : 
     * <li> addPrepaidDeposit : MdisMeter.prepaidDeposit
     * <li> lp1Timing : MdisMeter.lp1Timing
     * <li> lp2Pattern : MdisMeter.lp2Pattern
     * <li> lp2Timing : MdisMeter.lp2Timing
     * <li> meterDirection : MdisMeter.meterDirection
     * <li> prepaidAlertLevel1 : MdisMeter.prepaidAlertLevel1
     * <li> prepaidAlertLevel2 : MdisMeter.prepaidAlertLevel2
     * <li> prepaidAlertLevel3 : MdisMeter.prepaidAlertLevel3
     * <li> prepaidAlertStart : MdisMeter.prepaidAlertStart
     * <li> prepaidAlertOff : MdisMeter.prepaidAlertOff
     * <li> lcdDispCycle : MdisMeter.lcdDispCyclePost/MdisMeter.lcdDispCyclePre
     * </ul>
     * @return List of com.aimir.model.device.MeterCtrl
     */
    public List<MeterCtrl> saveBulkMeterCommand(Map<String, Object> conditionMap);

    /**
     * method name : getBulkMeterCommandResultData<b/>
     * method Desc : MDIS - Bulk Meter Command 실행 결과 값을 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getBulkMeterCommandResultData(Map<String, Object> conditionMap);

    /**
     * method name : getBulkMeterCommandRunCheck<b/>
     * method Desc : MDIS Bulk Meter Command 실행 여부를 조회한다. 실행 중인 command 가 있으면 해당 정보를 리턴한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getBulkMeterCommandRunCheck(Map<String, Object> conditionMap);
}