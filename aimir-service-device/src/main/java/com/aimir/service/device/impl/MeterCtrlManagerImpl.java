package com.aimir.service.device.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DefaultCmdResult;
import com.aimir.constants.CommonConstants.GetTamperingCmdResult;
import com.aimir.constants.CommonConstants.MdisMeterKind;
import com.aimir.constants.CommonConstants.MeterCommand;
import com.aimir.constants.CommonConstants.MeterCommandStatus;
import com.aimir.constants.CommonConstants.RelaySwitchCmdResult;
import com.aimir.constants.CommonConstants.RelaySwitchOnOffCmdResult;
import com.aimir.dao.device.MdisMeterDao;
import com.aimir.dao.device.MeterCtrlDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.model.device.Meter;
import com.aimir.model.device.MeterCtrl;
import com.aimir.service.device.MeterCtrlManager;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/**
 * MeterCtrlManagerImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 11. 11.   v1.0       문동규         
 * </pre>
 */
@Service(value="meterCtrlManager")
@Transactional(readOnly=false)
public class MeterCtrlManagerImpl implements MeterCtrlManager{

    @Autowired
    MeterCtrlDao meterCtrlDao;

    @Autowired
    MeterDao meterDao;

    @Autowired
    MdisMeterDao mdisMeterDao;

    /**
     * method name : saveMeterCommand<b/>
     * method Desc : Command 버튼 클릭 시 MeterCtrl 에 데이터를 insert 한다.
     *
     * @param conditionMap
     * @return
     */
    public MeterCtrl saveMeterCommand(Map<String, Object> conditionMap) {
        String writeDate = null;
        MeterCommand cmd = (MeterCommand)conditionMap.get("meterCommand");
        String ctrlId = (String)conditionMap.get("ctrlId");
        String meterKind = (String)conditionMap.get("meterKind");
        String lcdDispScroll = (String)conditionMap.get("lcdDispScroll");
        String lcdDispContent = (String)conditionMap.get("lcdDispContent");
        Integer meterId = (Integer)conditionMap.get("meterId");
        Integer prepaidRate = (Integer)conditionMap.get("prepaidRate");
        Double addPrepaidDeposit = (Double)conditionMap.get("addPrepaidDeposit");
        String lp1Timing = (String)conditionMap.get("lp1Timing");
        String lp2Pattern = (String)conditionMap.get("lp2Pattern");
        String lp2Timing = (String)conditionMap.get("lp2Timing");
        String meterDirection = (String)conditionMap.get("meterDirection");
        Integer prepaidAlertLevel1 = (Integer)conditionMap.get("prepaidAlertLevel1");
        Integer prepaidAlertLevel2 = (Integer)conditionMap.get("prepaidAlertLevel2");
        Integer prepaidAlertLevel3 = (Integer)conditionMap.get("prepaidAlertLevel3");
        Integer prepaidAlertStart = (Integer)conditionMap.get("prepaidAlertStart");
        Integer prepaidAlertOff = (Integer)conditionMap.get("prepaidAlertOff");
        Integer lcdDispCycle = (Integer)conditionMap.get("lcdDispCycle");
        
        try {
            writeDate = TimeUtil.getCurrentTime();
        } catch(ParseException pe) {
            pe.printStackTrace();
        }

        MeterCtrl meterCtrl = new MeterCtrl();
        Meter meter = meterDao.get(meterId);
        meterCtrl.setCtrlId(ctrlId);
        meterCtrl.setMeter(meter);
        meterCtrl.setWriteDate(writeDate);
        meterCtrl.setStatus(0);        // Initialize

        switch(cmd) {
            case TIME_SYNC:
                meterCtrl.setParam1(writeDate);
                break;

            case SET_PREPAID_RATE:
                meterCtrl.setParam1(prepaidRate.toString());
                break;

            case ADD_PREPAID_DEPOSIT:
                meterCtrl.setParam1(addPrepaidDeposit.toString());
                break;

            case SET_LP1_TIMING:
                meterCtrl.setParam1("0");
                meterCtrl.setParam2(lp1Timing);
                break;

            case SET_LP2_TIMING:
                meterCtrl.setParam1(lp2Pattern);
                meterCtrl.setParam2(lp2Timing);
                break;

            case SET_METER_DIRECTION:
                meterCtrl.setParam1(meterDirection);
                break;

            case SET_METER_KIND:
                meterCtrl.setParam1(meterKind);
                break;

            case SET_PREPAID_ALERT:
                meterCtrl.setParam1(prepaidAlertLevel1.toString());
                meterCtrl.setParam2(prepaidAlertLevel2.toString());
                meterCtrl.setParam3(prepaidAlertLevel3.toString());
                meterCtrl.setParam4(prepaidAlertStart.toString());
                meterCtrl.setParam5(prepaidAlertOff.toString());
                break;

            case SET_METER_DISPLAY_ITEMS:
                meterCtrl.setParam1(lcdDispScroll);

                if (MdisMeterKind.POSTPAID.getCode().equals(meterKind)) {
                    meterCtrl.setParam2(lcdDispCycle.toString());
                    meterCtrl.setParam3(lcdDispContent);
                } else {
                    meterCtrl.setParam4(lcdDispCycle.toString());
                    meterCtrl.setParam5(lcdDispContent);
                }
                break;
        }

        MeterCtrl result = meterCtrlDao.add(meterCtrl);
        return result;
    }

	
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
    public Map<String, Object> getMeterCommandResultData(Map<String, Object> conditionMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        List<MeterCtrl> list = meterCtrlDao.getMeterCommandResultData(conditionMap);

        if (list == null || list.size() <= 0) {
            return resultMap;
        }

        MeterCtrl meterCtrl = list.get(0);
        int status = meterCtrl.getStatus();
        String msg = null;

        switch(status) {
            case 2 :        // Return Value
                // make result message
                msg = makeMeterCommandResultMsg(meterCtrl);
                resultMap.put("status", "complete");
                resultMap.put("msg", msg);
                break;
            case -1 :       // Timeout Error
            case -2 :       // Error
                // make error message
                msg = makeMeterCommandErrorMsg(meterCtrl);
                resultMap.put("status", "error");
                resultMap.put("msg", msg);
                break;
            default :       // Progress
                // progress
                resultMap.put("status", "progress");
                break;
        }

        return resultMap;
    }

    /**
     * method name : getMeterCommandRunCheck<b/>
     * method Desc : Meter Command 실행 여부를 조회한다. 실행 중인 command 가 있으면 해당 정보를 리턴한다.
     *
     * @param conditionMap
     * @return
     */
    public MeterCtrl getMeterCommandRunCheck(Map<String, Object> conditionMap) {
        List<MeterCtrl> list = meterCtrlDao.getMeterCommandRunCheck(conditionMap);

        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * method name : makeMeterCommandResultMsg<b/>
     * method Desc : Meter Command 의 결과 메시지를 생성한다.
     *
     * @param meterCtrl
     * @return 결과메시지
     */
    private String makeMeterCommandResultMsg(MeterCtrl meterCtrl) {

        StringBuilder msg = new StringBuilder();
        String ctrlId = meterCtrl.getCtrlId();
        String result1 = StringUtil.nullToBlank(meterCtrl.getResult1());

        MeterCommand constant = null;

        for (MeterCommand obj : MeterCommand.values()) {
            if (obj.getId().equals(ctrlId)) {
                constant = obj;
            }
        }
        msg.append(makeMeterCommandMsgPrefix(ctrlId));

        switch(constant) {
            case ON_DEMAND_METERING:
                msg.append(result1).append(" ").append(meterCtrl.getResult2());
                break;
            case RELAY_STATUS:
                for (RelaySwitchCmdResult obj : RelaySwitchCmdResult.values()) {
                    if (result1.equals(obj.getCode())) {
                        msg.append(obj.getMessage());
                        break;
                    }
                }
                break;
            case RELAY_OFF:
            case RELAY_ON:
                for (RelaySwitchOnOffCmdResult obj : RelaySwitchOnOffCmdResult.values()) {
                    if (result1.equals(obj.getCode())) {
                        msg.append(obj.getMessage());
                        break;
                    }
                }
                break;
            case TIME_SYNC:
            case CLEAR_TAMPERING:
            case SET_PREPAID_RATE:
            case SET_LP1_TIMING:
            case SET_LP2_TIMING:
            case SET_METER_DIRECTION:
            case ADD_PREPAID_DEPOSIT:
            case SET_METER_KIND:
            case SET_PREPAID_ALERT:
            case SET_METER_DISPLAY_ITEMS:
            case SET_METER_RESET:
                // 00 : Success, 01 : Failure
                for (DefaultCmdResult obj : DefaultCmdResult.values()) {
                    if (result1.equals(obj.getCode())) {
                        msg.append(obj.getMessage());
                        break;
                    }
                }
                break;
            case GET_TAMPERING:
                String result = null;
                String result4 = null;
                String result5 = null;

                if (!result1.isEmpty()) {
                    result = result1;
                } else if (!StringUtil.nullToBlank(meterCtrl.getResult2()).isEmpty()) {
                    result = meterCtrl.getResult2();
                } else if (!StringUtil.nullToBlank(meterCtrl.getResult3()).isEmpty()) {
                    result = meterCtrl.getResult3();
                } else if (!StringUtil.nullToBlank(meterCtrl.getResult4()).isEmpty()) {
                    result4 = meterCtrl.getResult4();
                } else if (!StringUtil.nullToBlank(meterCtrl.getResult5()).isEmpty()) {
                    result5 = meterCtrl.getResult5();
                }

                if (result != null) {
                    for (GetTamperingCmdResult obj : GetTamperingCmdResult.values()) {
                        if (result.equals(obj.getCode())) {
                            msg.append(obj.getMessage());
                            break;
                        }
                    }
                } else if (result4 != null) {
                    for (GetTamperingCmdResult obj : GetTamperingCmdResult.values()) {
                        if (result4.equals(obj.getCode())) {
                            msg.append(obj.getMessage4());
                            break;
                        }
                    }
                } else if (result5 != null) {
                    for (GetTamperingCmdResult obj : GetTamperingCmdResult.values()) {
                        if (result5.equals(obj.getCode())) {
                            msg.append(obj.getMessage5());
                            break;
                        }
                    }
                }
                break;
            case GET_PREPAID_DEPOSIT:
            case GET_SW_VER:
                msg.append(result1);
                break;
        }

        return msg.toString();
    }

    /**
     * method name : makeMeterCommandErrorMsg<b/>
     * method Desc : Meter Command 의 Error Message 를 생성한다.
     *
     * @param meterCtrl
     * @return
     */
    private String makeMeterCommandErrorMsg(MeterCtrl meterCtrl) {

        StringBuilder msg = new StringBuilder();
        String ctrlId = meterCtrl.getCtrlId();

        msg.append(makeMeterCommandMsgPrefix(ctrlId));

        if (meterCtrl.getStatus() == -1) {
            msg.append(MeterCommandStatus.TIME_OUT.getMessage());
        } else if (meterCtrl.getStatus() == -2) {
            msg.append(MeterCommandStatus.ERROR.getMessage());
        }

        return msg.toString();
    }

    /**
     * method name : makeMeterCommandMsgPrefix<b/>
     * method Desc : Meter Command 의 Message 시작부분을 생성한다.
     *
     * @param ctrlId
     * @return
     */
    private String makeMeterCommandMsgPrefix(String ctrlId) {

        StringBuilder msg = new StringBuilder();

        for (MeterCommand constant : MeterCommand.values()) {
            if (ctrlId.equals(constant.getId())) {
                msg.append(constant.getResultMsg());
                break;
            }
        }        

        msg.append(" : ");
        return msg.toString();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * method name : saveBulkMeterCommand<b/>
     * method Desc : MDIS - Bulk Meter Command mode 에서 Command 버튼 클릭 시 MeterCtrl 에 각 Meter 별 데이터를 insert 한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<MeterCtrl> saveBulkMeterCommand(Map<String, Object> conditionMap) {
        List<MeterCtrl> result = new ArrayList<MeterCtrl>();
        String writeDate = null;
        MeterCommand cmd = (MeterCommand)conditionMap.get("meterCommand");
        String ctrlId = (String)conditionMap.get("ctrlId");
        String meterKind = (String)conditionMap.get("meterKind");
        String lcdDispScroll = (String)conditionMap.get("lcdDispScroll");
        String lcdDispContent = (String)conditionMap.get("lcdDispContent");
//        Integer meterId = (Integer)conditionMap.get("meterId");
        Integer prepaidRate = (Integer)conditionMap.get("prepaidRate");
//        Double addPrepaidDeposit = (Double)conditionMap.get("addPrepaidDeposit");
        String lp1Timing = (String)conditionMap.get("lp1Timing");
        String lp2Pattern = (String)conditionMap.get("lp2Pattern");
        String lp2Timing = (String)conditionMap.get("lp2Timing");
        String meterDirection = (String)conditionMap.get("meterDirection");
        Integer prepaidAlertLevel1 = (Integer)conditionMap.get("prepaidAlertLevel1");
        Integer prepaidAlertLevel2 = (Integer)conditionMap.get("prepaidAlertLevel2");
        Integer prepaidAlertLevel3 = (Integer)conditionMap.get("prepaidAlertLevel3");
        Integer prepaidAlertStart = (Integer)conditionMap.get("prepaidAlertStart");
        Integer prepaidAlertOff = (Integer)conditionMap.get("prepaidAlertOff");
        Integer lcdDispCycle = (Integer)conditionMap.get("lcdDispCycle");
        List<Integer> meterIdList = (List<Integer>)conditionMap.get("meterIdList");
        MeterCtrl meterCtrl = null;
        MeterCtrl addResult = null;
        Meter meter = null;

        try {
            writeDate = TimeUtil.getCurrentTime();
        } catch(ParseException pe) {
            pe.printStackTrace();
        }

        for (Integer meterId : meterIdList) {
            addResult = null;
            meterCtrl = new MeterCtrl();
            meter = meterDao.get(meterId);
            meterCtrl.setCtrlId(ctrlId);
            meterCtrl.setMeter(meter);
            meterCtrl.setWriteDate(writeDate);
            meterCtrl.setStatus(0);        // Initialize

            switch(cmd) {
                case TIME_SYNC:
                    meterCtrl.setParam1(writeDate);
                    break;

                case SET_PREPAID_RATE:
                    meterCtrl.setParam1(prepaidRate.toString());
                    break;

//                case ADD_PREPAID_DEPOSIT:
//                    meterCtrl.setParam1(addPrepaidDeposit.toString());
//                    break;

                case SET_LP1_TIMING:
                    meterCtrl.setParam1("0");
                    meterCtrl.setParam2(lp1Timing);
                    break;

                case SET_LP2_TIMING:
                    meterCtrl.setParam1(lp2Pattern);
                    meterCtrl.setParam2(lp2Timing);
                    break;

                case SET_METER_DIRECTION:
                    meterCtrl.setParam1(meterDirection);
                    break;

                case SET_METER_KIND:
                    meterCtrl.setParam1(meterKind);
                    break;

                case SET_PREPAID_ALERT:
                    meterCtrl.setParam1(prepaidAlertLevel1.toString());
                    meterCtrl.setParam2(prepaidAlertLevel2.toString());
                    meterCtrl.setParam3(prepaidAlertLevel3.toString());
                    meterCtrl.setParam4(prepaidAlertStart.toString());
                    meterCtrl.setParam5(prepaidAlertOff.toString());
                    break;

                case SET_METER_DISPLAY_ITEMS:
                    meterCtrl.setParam1(lcdDispScroll);

                    if (MdisMeterKind.POSTPAID.getCode().equals(meterKind)) {
                        meterCtrl.setParam2(lcdDispCycle.toString());
                        meterCtrl.setParam3(lcdDispContent);
                    } else {
                        meterCtrl.setParam4(lcdDispCycle.toString());
                        meterCtrl.setParam5(lcdDispContent);
                    }
                    break;
            }

            addResult = meterCtrlDao.add(meterCtrl); 
            result.add(addResult);
        }
        return result;
    }

    /**
     * method name : getBulkMeterCommandResultData<b/>
     * method Desc : MDIS - Bulk Meter Command 실행 결과 값을 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getBulkMeterCommandResultData(Map<String, Object> conditionMap) {
//        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<MeterCtrl> list = meterCtrlDao.getBulkMeterCommandResultData(conditionMap);

        if (list == null || list.size() <= 0) {
            return resultMap;
        }

        StringBuilder sbMsg = new StringBuilder();
        int status = 0;
        boolean hasProgress = false;

        for (MeterCtrl meterCtrl : list) {
            status = meterCtrl.getStatus();
            sbMsg.append("[").append(meterCtrl.getMeter().getMdsId()).append("] ");

            switch(status) {
                case 2 :        // Return Value
                    // make result message
//                    sbMsg.append("[").append(meterCtrl.getMeter().getMdsId()).append("] ").append(makeMeterCommandResultMsg(meterCtrl));
                    sbMsg.append(makeMeterCommandResultMsg(meterCtrl));
                    sbMsg.append("\n");
                    break;

                case -1 :       // Timeout Error
                case -2 :       // Error
                    // make error message
//                    sbMsg.append("[").append(meterCtrl.getMeter().getMdsId()).append("] ").append(makeMeterCommandErrorMsg(meterCtrl));
                    sbMsg.append(makeMeterCommandErrorMsg(meterCtrl));
                    sbMsg.append("\n");
                    break;

                default :       // Progress
                    // pregress
                    hasProgress = true;
                    break;
            }
        }

        if (hasProgress) {
            resultMap.put("status", "progress");
        } else {
            resultMap.put("status", "complete");
            resultMap.put("msg", sbMsg.toString());
        }
        
        return resultMap;
    }

    /**
     * method name : getBulkMeterCommandRunCheck<b/>
     * method Desc : MDIS Bulk Meter Command 실행 여부를 조회한다. 실행 중인 command 가 있으면 해당 정보를 리턴한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getBulkMeterCommandRunCheck(Map<String, Object> conditionMap) {
        Integer result = meterCtrlDao.getBulkMeterCommandRunCheck(conditionMap);
        return result;
    }
}