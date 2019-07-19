package com.aimir.service.device;

import java.util.List;
import java.util.Map;

import com.aimir.model.device.MdisMeter;

/**
 * MdisMeterManager.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2012. 05. 03  v1.0        문동규   MDIS Meter Service
 * 2012. 05. 10  v1.1        문동규   package 위치변경(mvm -> device)
 * </pre>
 */
public interface MdisMeterManager {

    /**
     * method name : getMdisMeterByMeterId<b/>
     * method Desc : MdisMeter 모델 정보를 meterId 로 조회한다.
     *
     * @param meterId Meter.id
     * @return com.aimir.model.mvm.MdisMeter
     */
    public MdisMeter getMdisMeterByMeterId(Integer meterId);

    /**
     * method name : getMdisMeterByMeterIdBulkCommand<b/>
     * method Desc : Bulk Meter Command MdisMeter 모델 정보를 meterId 로 조회한다.
     *
     * @param meterIdList List of Meter.id
     * @return Map {mdisMeter : List of Map {meterKind : MdisMeter.meterKind
     *                                       prepaidDeposit : MdisMeter.prepaidDeposit
     *                                       lp1Timing : MdisMeter.lp1Timing
     *                                       lp2Pattern : MdisMeter.lp2Pattern
     *                                       lp2Timing : MdisMeter.lp2Timing
     *                                       meterDirection : MdisMeter.meterDirection
     *                                       prepaidAlertLevel1 : MdisMeter.prepaidAlertLevel1
     *                                       prepaidAlertLevel2 : MdisMeter.prepaidAlertLevel2
     *                                       prepaidAlertLevel3 : MdisMeter.prepaidAlertLevel3
     *                                       prepaidAlertStart : MdisMeter.prepaidAlertStart
     *                                       prepaidAlertOff : MdisMeter.prepaidAlertOff
     *                                       lcdDispScroll : MdisMeter.lcdDispScroll
     *                                       lcdDispCyclePost : MdisMeter.lcdDispCyclePost
     *                                       lcdDispContentPost : MdisMeter.lcdDispContentPost
     *                                       lcdDispCyclePre : MdisMeter.lcdDispCyclePre
     *                                       lcdDispContentPre : MdisMeter.lcdDispContentPre
     *                                       threshold : Contract.prepaymentThreshold
     *                                       conditions : Meter.conditions
     *                                       switchStatus : EnergyMeter.switchStatus.code
     *                                      }
     *              disablePostpaid : boolean - Postpaid 관련 command button disable 여부
     *              disableActivation : boolean - Activation 관련 command  button disable 여부
     *              disableDeactivation : boolean - Deactivation 관련 command  button disable 여부
     *              disableTampering : boolean - Tampering Issued 관련 command  button disable 여부
     *              isPostpaid : boolean - Postpaid 여부
     *              isActivation : boolean - Activation 여부
     *              isDeactivation : boolean - Deactivation 여부
     *              isTampering : boolean - Tampering Issued 여부
     *             }
     */
    public Map<String, Object> getMdisMeterByMeterIdBulkCommand(List<Integer> meterIdList);
}