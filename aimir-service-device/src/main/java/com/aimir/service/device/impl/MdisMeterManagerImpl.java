package com.aimir.service.device.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.CircuitBreakerStatus;
import com.aimir.constants.CommonConstants.MdisMeterKind;
import com.aimir.constants.CommonConstants.MdisTamperingStatus;
import com.aimir.dao.device.EnergyMeterDao;
import com.aimir.dao.device.MdisMeterDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.model.device.MdisMeter;
import com.aimir.service.device.MdisMeterManager;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;

/**
 * MdisMeterManagerImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2012. 05. 03  v1.0        문동규   MDIS Meter Service Impl
 * 2012. 05. 10  v1.1        문동규   package 위치변경(mvm -> device)
 * </pre>
 */
@Service(value = "mdisMeterManager")
public class MdisMeterManagerImpl implements MdisMeterManager {

    protected static Log log = LogFactory.getLog(MdisMeterManagerImpl.class);

    @Autowired
    MdisMeterDao dao;

    @Autowired
    ContractDao contractDao;

    @Autowired
    MeterDao meterDao;

    @Autowired
    EnergyMeterDao energyMeterDao;

    /**
     * method name : getMdisMeterByMeterId<b/>
     * method Desc : MdisMeter 모델 정보를 meterId 로 조회한다.
     *
     * @param meterId Meter.id
     * @return com.aimir.model.mvm.MdisMeter
     */
    public MdisMeter getMdisMeterByMeterId(Integer meterId) {
        MdisMeter mdisMeter = dao.get(meterId);
        
        if (mdisMeter != null && !StringUtil.nullToBlank(mdisMeter.getLcdDispContent()).isEmpty()) {
            mdisMeter.setLcdDispContent(mdisMeter.getLcdDispContent().replaceAll("\r\n", "\n").replaceAll("\n", "\\n"));
        }
        return mdisMeter;
    }

    /**
     * method name : getMdisMeterByMeterIdBulkCommand<b/>
     * method Desc : Bulk Meter Command MdisMeter 모델 정보를 meterId 로 조회한다.
     *
     * @param meterIdList List of Meter.id
     * @return List of Map
     */
    public Map<String, Object> getMdisMeterByMeterIdBulkCommand(List<Integer> meterIdList) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> list = dao.getMdisMeterByMeterIdBulkCommand(meterIdList);

        boolean isPrevPostpaid = false;
        boolean isPrevMeterKindNull = false;
        boolean isPrevActivation = false;
        boolean isPrevDeactivation = false;
        boolean isPrevTampering = false;

        boolean isPostpaid = false;                 // postpaid 여부
        boolean isMeterKindNull = false;            // meterkind null 여부
        boolean isActivation = false;               // activation 여부
        boolean isDeactivation = false;             // deactivation 여부
        boolean isTampering = false;                // tampering issued 여부

        // 조회한 list 중 값이 다를 경우 해당 command button disable
        boolean isDisablePostpaid = false;          // disable postpaid
        boolean isDisableActivation = false;        // disable activation
        boolean isDisableDeactivation = false;      // disable deactivation
        boolean isDisableTampering = false;         // disable tampering issued
        int cnt = 0;

        if (list != null && meterIdList.size() == list.size()) {
            for (Map<String, Object> obj : list) {
                map = new HashMap<String, Object>();

                map.put("meterKind", obj.get("METER_KIND"));
                map.put("prepaidDeposit", obj.get("PREPAID_DEPOSIT"));
                map.put("lp1Timing", obj.get("LP1_TIMING"));
                map.put("lp2Pattern", obj.get("LP2_PATTERN"));
                map.put("lp2Timing", obj.get("LP2_TIMING"));
                map.put("meterDirection", obj.get("METER_DIRECTION"));
                map.put("prepaidAlertLevel1", obj.get("PREPAID_ALERT_LEVEL1"));
                map.put("prepaidAlertLevel2", obj.get("PREPAID_ALERT_LEVEL2"));
                map.put("prepaidAlertLevel3", obj.get("PREPAID_ALERT_LEVEL3"));
                map.put("prepaidAlertStart", obj.get("PREPAID_ALERT_START"));
                map.put("prepaidAlertOff", obj.get("PREPAID_ALERT_OFF"));
                map.put("lcdDispScroll", obj.get("LCD_DISP_SCROLL"));
                map.put("lcdDispCyclePost", obj.get("LCD_DISP_CYCLE_POST"));
                map.put("lcdDispContentPost", obj.get("LCD_DISP_CONTENT_POST"));
                map.put("lcdDispCyclePre", obj.get("LCD_DISP_CYCLE_PRE"));
                map.put("lcdDispContentPre", obj.get("LCD_DISP_CONTENT_PRE"));
                map.put("threshold", obj.get("PREPAYMENTTHRESHOLD"));
                map.put("conditions", obj.get("CONDITIONS"));
                map.put("switchStatus", obj.get("SWITCH_STATUS"));
                result.add(map);

                if (cnt > 0) {
                    if (!isDisablePostpaid) {
                        isPrevPostpaid = isPostpaid;
                        isPrevMeterKindNull = isMeterKindNull;
                    }
                    if (!isDisableActivation) {
                        isPrevActivation = isActivation;
                    }
                    if (!isDisableDeactivation) {
                        isPrevDeactivation = isDeactivation;
                    }
                    if (!isDisableTampering) {
                        isPrevTampering = isTampering;
                    }
                }

                if (!isDisablePostpaid) {
                    if (obj.get("METER_KIND") == null) {
                        isMeterKindNull = true;
                    } else {
                        isMeterKindNull = false;
                    }

                    if (obj.get("METER_KIND") == null || MdisMeterKind.POSTPAID.getCode().equals((String)obj.get("METER_KIND"))) {
                        isPostpaid = true;
                    } else {
                        isPostpaid = false;
                    }
                }

                if (!isDisableActivation) {
                    if (obj.get("SWITCH_STATUS") != null
                            && CircuitBreakerStatus.Activation.getCode() == DecimalUtil.ConvertNumberToInteger(obj
                                    .get("SWITCH_STATUS"))) {
                        isActivation = true;
                    } else {
                        isActivation = false;
                    }
                }

                if (!isDisableDeactivation) {
                    if (obj.get("SWITCH_STATUS") != null
                            && CircuitBreakerStatus.Deactivation.getCode() == DecimalUtil.ConvertNumberToInteger(obj
                                    .get("SWITCH_STATUS"))) {
                        isDeactivation = true;
                    } else {
                        isDeactivation = false;
                    }
                }

                if (!isDisableTampering) {
                    if (obj.get("CONDITIONS") != null && MdisTamperingStatus.TAMPERING_ISSUED.getMessage().equals((String)obj.get("CONDITIONS"))) {
                        isTampering = true;
                    } else {
                        isTampering = false;
                    }
                }

                if (cnt > 0) {
                    if (!isDisablePostpaid && (isMeterKindNull != isPrevMeterKindNull || isPostpaid != isPrevPostpaid)) {
                        isDisablePostpaid = true;
                    }
                    if (!isDisableActivation && isActivation != isPrevActivation) {
                        isDisableActivation = true;
                    }
                    if (!isDisableDeactivation && isDeactivation != isPrevDeactivation) {
                        isDisableDeactivation = true;
                    }
                    if (!isDisableTampering && isTampering != isPrevTampering) {
                        isDisableTampering = true;
                    }
                }

                cnt++;
            }

            resultMap.put("disablePostpaid", isDisablePostpaid);
            resultMap.put("disableActivation", isDisableActivation);
            resultMap.put("disableDeactivation", isDisableDeactivation);
            resultMap.put("disableTampering", isDisableTampering);

            resultMap.put("isPostpaid", isPostpaid);
            resultMap.put("isMeterKindNull", isMeterKindNull);
            resultMap.put("isActivation", isActivation);
            resultMap.put("isDeactivation", isDeactivation);
            resultMap.put("isTampering", isTampering);
        } else {
            resultMap.put("disablePostpaid", true);
            resultMap.put("disableActivation", true);
            resultMap.put("disableDeactivation", true);
            resultMap.put("disableTampering", true);
        }
        resultMap.put("mdisMeter", result);

        return resultMap;
    }
}