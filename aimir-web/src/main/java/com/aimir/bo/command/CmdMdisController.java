package com.aimir.bo.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.CommandType;
import com.aimir.constants.CommonConstants.MeterCommand;
import com.aimir.model.device.Meter;
import com.aimir.model.device.MeterCtrl;
import com.aimir.model.system.Code;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.MeterCtrlManager;
import com.aimir.service.device.MeterManager;
import com.aimir.service.device.OperationLogManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.util.StringUtil;

@Service(value="cmdMdisController")
@Controller
public class CmdMdisController<V> {

	private static Log log = LogFactory.getLog(CmdMdisController.class);

    @Autowired
    MeterManager meterManager;

    @Autowired
    OperationLogManager operationLogManager;

    @Autowired
    OperatorManager operatorManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    MeterCtrlManager meterCtrlManager;

	protected boolean commandAuthCheck(String loginId, CommandType cmdType, String command) {

		Operator operator = operatorManager.getOperatorByLoginId(loginId);

		Role role = operator.getRole();
		Set<Code> commands = role.getCommands();
		Code codeCommand = null;
		if (role.getCustomerRole() != null && role.getCustomerRole()) {
			return false; //고객 권한이면 
		}
		
		if(role.getMtrAuthority().equals("c")){
			return true; //관리자면
		}
		
		for (Iterator<Code> i = commands.iterator(); i.hasNext();) {
			codeCommand = (Code) i.next();
			if (codeCommand.getCode().equals(command))
				return true; //관리자가 아니라도 명령에 대한 권한이 있으면
		}
 
		return false;
	}

    /**
     * MDIS 커맨드 실행 - MeterCtrl 에 해당 데이터를 저장한다.
     * 
     * @param ctrlId
     * @param meterId
     * @param loginId
     * @param value
     * @return
     */
    @RequestMapping(value = "/gadget/device/command/saveMeterCommand")
    public ModelAndView saveMeterCommand(@RequestParam("ctrlId") String ctrlId,
            @RequestParam("meterId") Integer meterId,
            @RequestParam("loginId") String loginId,
            @RequestParam(value="prepaidRate", required=false) Integer prepaidRate,
            @RequestParam(value="addPrepaidDeposit", required=false) Double addPrepaidDeposit,
            @RequestParam(value="lp1Timing", required=false) String lp1Timing,
            @RequestParam(value="lp2Pattern", required=false) String lp2Pattern,
            @RequestParam(value="lp2Timing", required=false) String lp2Timing,
            @RequestParam(value="meterDirection", required=false) String meterDirection,
            @RequestParam(value="meterKind", required=false) String meterKind,
            @RequestParam(value="prepaidAlertLevel1", required=false) Integer prepaidAlertLevel1,
            @RequestParam(value="prepaidAlertLevel2", required=false) Integer prepaidAlertLevel2,
            @RequestParam(value="prepaidAlertLevel3", required=false) Integer prepaidAlertLevel3,
            @RequestParam(value="prepaidAlertStart", required=false) Integer prepaidAlertStart,
            @RequestParam(value="prepaidAlertOff", required=false) Integer prepaidAlertOff,
            @RequestParam(value="lcdDispScroll", required=false) String lcdDispScroll,
            @RequestParam(value="lcdDispCycle", required=false) Integer lcdDispCycle,
            @RequestParam(value="lcdDispContent", required=false) String lcdDispContent) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String cmdCode = null;

        // control ID check
        if (StringUtil.nullToBlank(ctrlId).isEmpty()) {
            resultMap.put("status", "failure");
            resultMap.put("msg", "Meter Control ID is null!");
            mav.addObject("result", resultMap);
            return mav;
        }

        cmdCode = cmdCtrlIdToCode(ctrlId);

        // Permission Check
        if (!commandAuthCheck(loginId, CommandType.DeviceWrite, cmdCode)) {
            resultMap.put("status", "failure");
            resultMap.put("msg", "Error : No permission");
            mav.addObject("result", resultMap);
            return mav;
        }

        // Meter ID Check
        if (StringUtil.nullToBlank(meterId).isEmpty()) {
            resultMap.put("status", "failure");
            resultMap.put("msg", "Error : Meter ID is null!");
            mav.addObject("result", resultMap);
            return mav;
        }

        MeterCommand cmd = null;
        for (MeterCommand obj : MeterCommand.values()) {
            if (obj.getId().equals(ctrlId)) {
                cmd = obj;
            }
        }

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("ctrlId", ctrlId);
        conditionMap.put("meterId", meterId);

        // parameter check
        switch(cmd) {
            case SET_PREPAID_RATE:
                if (prepaidRate == null) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("prepaidRate", prepaidRate);
                break;

            case ADD_PREPAID_DEPOSIT:
                if (addPrepaidDeposit == null) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("addPrepaidDeposit", addPrepaidDeposit);
                break;

            case SET_LP1_TIMING:
                if (StringUtil.nullToBlank(lp1Timing).isEmpty()) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("lp1Timing", lp1Timing);
                break;

            case SET_LP2_TIMING:
                if (StringUtil.nullToBlank(lp2Pattern).isEmpty() || StringUtil.nullToBlank(lp2Timing).isEmpty()) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("lp2Pattern", lp2Pattern);
                conditionMap.put("lp2Timing", lp2Timing);
                break;

            case SET_METER_DIRECTION:
                if (StringUtil.nullToBlank(meterDirection).isEmpty()) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("meterDirection", meterDirection);
                break;

            case SET_METER_KIND:
                if (StringUtil.nullToBlank(meterKind).isEmpty()) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("meterKind", meterKind);
                break;
                
            case SET_PREPAID_ALERT:
                if (prepaidAlertLevel1 == null || prepaidAlertLevel2 == null || prepaidAlertLevel3 == null
                        || prepaidAlertStart == null || prepaidAlertOff == null) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("prepaidAlertLevel1", prepaidAlertLevel1);
                conditionMap.put("prepaidAlertLevel2", prepaidAlertLevel2);
                conditionMap.put("prepaidAlertLevel3", prepaidAlertLevel3);
                conditionMap.put("prepaidAlertStart", prepaidAlertStart);
                conditionMap.put("prepaidAlertOff", prepaidAlertOff);
                break;

            case SET_METER_DISPLAY_ITEMS:
                if (StringUtil.nullToBlank(meterKind).isEmpty() || StringUtil.nullToBlank(lcdDispScroll).isEmpty()
                        || lcdDispCycle == null || StringUtil.nullToBlank(lcdDispContent).isEmpty()) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("meterKind", meterKind);
                conditionMap.put("lcdDispScroll", lcdDispScroll);
                conditionMap.put("lcdDispCycle", lcdDispCycle);
                conditionMap.put("lcdDispContent", lcdDispContent);
                break;
        }

        conditionMap.put("meterCommand", cmd);
        
        // Add MeterCtrl
        MeterCtrl result = meterCtrlManager.saveMeterCommand(conditionMap);

        if (result == null || result.getCtrlId() == null) {
            resultMap.put("status", "failure");
            resultMap.put("msg", "Error : Initialize Error");
            mav.addObject("result", resultMap);
            return mav;
        }

        Meter meter = meterManager.getMeter(meterId);
        Supplier supplier = meter.getSupplier();
        if(supplier == null){
            Operator operator = operatorManager.getOperatorByLoginId(loginId);
            supplier = operator.getSupplier();
        }

        Code operationCode = codeManager.getCodeByCode(cmdCode);

        if (operationCode != null) {
            String contractNumber = (meter.getContract() != null) ? meter.getContract().getContractNumber() : null;
            String description = operationCode.getName();
            // insert OperationLog 
            operationLogManager.saveOperationLogByMeterCmd(supplier, meter.getMeterType(), meter.getMdsId(), result.getWriteDate(),
                    loginId, operationCode, description, contractNumber);
        }

        resultMap.put("status", "success");
        resultMap.put("msg", "");
        resultMap.put("ctrlId", result.getCtrlId());
        resultMap.put("meterId", result.getMeter().getId());
        resultMap.put("writeDate", result.getWriteDate());

        mav.addObject("result", resultMap);
        return mav;
    }

    /**
     * method name : getMeterCommandResultData<b/>
     * method Desc : MDIS 커맨드 결과 정보를 조회한다.
     *
     * @param ctrlId
     * @param meterId
     * @param writeDate
     * @return
     */
    @RequestMapping(value = "/gadget/device/command/getMeterCommandResultData")
    public ModelAndView getMeterCommandResultData(@RequestParam("ctrlId") String ctrlId,
            @RequestParam("meterId") Integer meterId,
            @RequestParam("writeDate") String writeDate) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("ctrlId", ctrlId);
        conditionMap.put("meterId", meterId);
        conditionMap.put("writeDate", writeDate);

        Map<String, Object> result = meterCtrlManager.getMeterCommandResultData(conditionMap);

        mav.addObject("result", result);
        return mav;
    }

    /**
     * method name : getMeterCommandRunCheck<b/>
     * method Desc : MDIS 커맨드 실행 여부를 조회한다.
     *
     * @param meterId
     * @param loginId
     * @return
     */
    @RequestMapping(value = "/gadget/device/command/getMeterCommandRunCheck")
    public ModelAndView getMeterCommandRunCheck(@RequestParam("meterId") Integer meterId,
            @RequestParam("loginId") String loginId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> resultMap = new HashMap<String, Object>();

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("meterId", meterId);
//        conditionMap.put("loginId", loginId);

        MeterCtrl meterCtrl = meterCtrlManager.getMeterCommandRunCheck(conditionMap);

        if (meterCtrl == null) {
            resultMap.put("status", "n/a");
            mav.addObject("result", resultMap);
            return mav;
        }

        String ctrlId = meterCtrl.getCtrlId();
        String cmdCode = cmdCtrlIdToCode(ctrlId);

        // Permission Check
        if (!commandAuthCheck(loginId, CommandType.DeviceWrite, cmdCode)) {
            resultMap.put("status", "n/a");
            mav.addObject("result", resultMap);
            return mav;
        }
//        
//        // control ID check
//        if (StringUtil.nullToBlank(ctrlId).isEmpty()) {
//            resultMap.put("status", "failure");
//            resultMap.put("msg", "Meter Control ID is null!");
//            mav.addObject("result", resultMap);
//            return mav;
//        }

        resultMap.put("status", "success");
        resultMap.put("ctrlId", ctrlId);
        resultMap.put("meterId", meterCtrl.getMeter().getId());
        resultMap.put("writeDate", meterCtrl.getWriteDate());
        mav.addObject("result", resultMap);
        return mav;
    }

    /**
     * method name : cmdCtrlIdToCode<b/>
     * method Desc : Meter Control ID 에 해당하는 Code 를 리턴한다.
     *
     * @param ctrlId
     * @return
     */
    private String cmdCtrlIdToCode(String ctrlId) {
        for (MeterCommand cmd : MeterCommand.values()) {
            if (cmd.getId().equals(ctrlId)) {
                return cmd.getCode();
            }
        }
        return "";
    }

    /**
     * method name : numberChk<b/>
     * method Desc : 숫자값 인지 체크한다.
     *
     * @param str
     * @return
     */
//    private boolean numberChk(String str) {
//        char c;
//     
//        if(str.isEmpty()) return false;
//        
//        int len = str.length();
//     
//        for (int i = 0 ; i < len ; i++) {
//            c = str.charAt(i);
//            if (c < 48 || c > 59) {
//                return false;
//            }
//        }
//        return true;
//    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * method name : saveBulkMeterCommand<b/>
     * method Desc : MDIS Meter Command 실행 - MeterCtrl 에 해당 데이터를 저장한다.
     *
     * @param ctrlId
     * @param meterIds
     * @param loginId
     * @param prepaidRate
     * @param addPrepaidDeposit
     * @param lp1Timing
     * @param lp2Pattern
     * @param lp2Timing
     * @param meterDirection
     * @param meterKind
     * @param prepaidAlertLevel1
     * @param prepaidAlertLevel2
     * @param prepaidAlertLevel3
     * @param prepaidAlertStart
     * @param prepaidAlertOff
     * @param lcdDispScroll
     * @param lcdDispCycle
     * @param lcdDispContent
     * @return
     */
    @RequestMapping(value = "/gadget/device/command/saveBulkMeterCommand")
    public ModelAndView saveBulkMeterCommand(@RequestParam("ctrlId") String ctrlId,
            @RequestParam("meterIds[]") String[] meterIds,
            @RequestParam("loginId") String loginId,
            @RequestParam(value="prepaidRate", required=false) Integer prepaidRate,
            @RequestParam(value="addPrepaidDeposit", required=false) Double addPrepaidDeposit,
            @RequestParam(value="lp1Timing", required=false) String lp1Timing,
            @RequestParam(value="lp2Pattern", required=false) String lp2Pattern,
            @RequestParam(value="lp2Timing", required=false) String lp2Timing,
            @RequestParam(value="meterDirection", required=false) String meterDirection,
            @RequestParam(value="meterKind", required=false) String meterKind,
            @RequestParam(value="prepaidAlertLevel1", required=false) Integer prepaidAlertLevel1,
            @RequestParam(value="prepaidAlertLevel2", required=false) Integer prepaidAlertLevel2,
            @RequestParam(value="prepaidAlertLevel3", required=false) Integer prepaidAlertLevel3,
            @RequestParam(value="prepaidAlertStart", required=false) Integer prepaidAlertStart,
            @RequestParam(value="prepaidAlertOff", required=false) Integer prepaidAlertOff,
            @RequestParam(value="lcdDispScroll", required=false) String lcdDispScroll,
            @RequestParam(value="lcdDispCycle", required=false) Integer lcdDispCycle,
            @RequestParam(value="lcdDispContent", required=false) String lcdDispContent) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String cmdCode = null;

        // control ID check
        if (StringUtil.nullToBlank(ctrlId).isEmpty()) {
            resultMap.put("status", "failure");
            resultMap.put("msg", "Meter Control ID is null!");
            mav.addObject("result", resultMap);
            return mav;
        }

        cmdCode = cmdCtrlIdToCode(ctrlId);

        // Permission Check
        if (!commandAuthCheck(loginId, CommandType.DeviceWrite, cmdCode)) {
            resultMap.put("status", "failure");
            resultMap.put("msg", "Error : No permission");
            mav.addObject("result", resultMap);
            return mav;
        }

        // Meter ID Check
        if (meterIds == null || meterIds.length <= 0) {
            resultMap.put("status", "failure");
            resultMap.put("msg", "Error : Meter ID is null!");
            mav.addObject("result", resultMap);
            return mav;
        }

        MeterCommand cmd = null;
        for (MeterCommand obj : MeterCommand.values()) {
            if (obj.getId().equals(ctrlId)) {
                cmd = obj;
                break;
            }
        }

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("ctrlId", ctrlId);
        int len = meterIds.length;
        List<Integer> meterIdList = new ArrayList<Integer>();

        for (int i = 0; i < len; i++) {
            meterIdList.add(Integer.parseInt(meterIds[i]));
        }
        conditionMap.put("meterIdList", meterIdList);

        // parameter check
        switch(cmd) {
            case SET_PREPAID_RATE:
                if (prepaidRate == null) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("prepaidRate", prepaidRate);
                break;

            case ADD_PREPAID_DEPOSIT:
                if (addPrepaidDeposit == null) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("addPrepaidDeposit", addPrepaidDeposit);
                break;

            case SET_LP1_TIMING:
                if (StringUtil.nullToBlank(lp1Timing).isEmpty()) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("lp1Timing", lp1Timing);
                break;

            case SET_LP2_TIMING:
                if (StringUtil.nullToBlank(lp2Pattern).isEmpty() || StringUtil.nullToBlank(lp2Timing).isEmpty()) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("lp2Pattern", lp2Pattern);
                conditionMap.put("lp2Timing", lp2Timing);
                break;

            case SET_METER_DIRECTION:
                if (StringUtil.nullToBlank(meterDirection).isEmpty()) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("meterDirection", meterDirection);
                break;

            case SET_METER_KIND:
                if (StringUtil.nullToBlank(meterKind).isEmpty()) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("meterKind", meterKind);
                break;
                
            case SET_PREPAID_ALERT:
                if (prepaidAlertLevel1 == null || prepaidAlertLevel2 == null || prepaidAlertLevel3 == null
                        || prepaidAlertStart == null || prepaidAlertOff == null) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("prepaidAlertLevel1", prepaidAlertLevel1);
                conditionMap.put("prepaidAlertLevel2", prepaidAlertLevel2);
                conditionMap.put("prepaidAlertLevel3", prepaidAlertLevel3);
                conditionMap.put("prepaidAlertStart", prepaidAlertStart);
                conditionMap.put("prepaidAlertOff", prepaidAlertOff);
                break;

            case SET_METER_DISPLAY_ITEMS:
                if (StringUtil.nullToBlank(meterKind).isEmpty() || StringUtil.nullToBlank(lcdDispScroll).isEmpty()
                        || lcdDispCycle == null || StringUtil.nullToBlank(lcdDispContent).isEmpty()) {
                    resultMap.put("status", "failure");
                    resultMap.put("msg", "Error : Input Parameter is null!");
                    mav.addObject("result", resultMap);
                    return mav;
                }
                conditionMap.put("meterKind", meterKind);
                conditionMap.put("lcdDispScroll", lcdDispScroll);
                conditionMap.put("lcdDispCycle", lcdDispCycle);
                conditionMap.put("lcdDispContent", lcdDispContent);
                break;
        }

        conditionMap.put("meterCommand", cmd);
        
        // Add MeterCtrl
        List<MeterCtrl> result = meterCtrlManager.saveBulkMeterCommand(conditionMap);

        if (result == null || result.size() <= 0) {
            resultMap.put("status", "failure");
            resultMap.put("msg", "Error : Initialize Error");
            mav.addObject("result", resultMap);
            return mav;
        }

//        String writeDate = null;
        List<String> writeDateList = new ArrayList<String>();
//        for (MeterCtrl obj : result) {
//            writeDateList.add(obj.getWriteDate());
//        }

        Meter meter = null;
        Supplier supplier = null;
        Code operationCode = null;
        String contractNumber = null;
        String description = null;

        for (MeterCtrl meterCtrl : result) {
            meter = meterCtrl.getMeter();
            supplier = meter.getSupplier();

            if(supplier == null){
                Operator operator = operatorManager.getOperatorByLoginId(loginId);
                supplier = operator.getSupplier();
            }

            operationCode = codeManager.getCodeByCode(cmdCode);

            if (operationCode != null) {
                contractNumber = (meter.getContract() != null) ? meter.getContract().getContractNumber() : null;
                description = operationCode.getName();
                // insert OperationLog 
                operationLogManager.saveOperationLogByMeterCmd(supplier, meter.getMeterType(), meter.getMdsId(), meterCtrl.getWriteDate(),
                        loginId, operationCode, description, contractNumber);
            }
            writeDateList.add(meterCtrl.getWriteDate());
        }

        resultMap.put("status", "success");
        resultMap.put("msg", "");
        resultMap.put("ctrlId", ctrlId);
        resultMap.put("meterIds", meterIds);
        resultMap.put("writeDates", writeDateList);

        mav.addObject("result", resultMap);
        return mav;
    }

    /**
     * method name : getBulkMeterCommandResultData<b/>
     * method Desc : MDIS Bulk Meter Command 결과 정보를 조회한다.
     *
     * @param ctrlId
     * @param meterIds
     * @param writeDate
     * @return
     */
    @RequestMapping(value = "/gadget/device/command/getBulkMeterCommandResultData")
    public ModelAndView getBulkMeterCommandResultData(@RequestParam("ctrlId") String ctrlId,
            @RequestParam("meterIds[]") String[] meterIds,
            @RequestParam("writeDates[]") String[] writeDates) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("ctrlId", ctrlId);

        int meterlen = meterIds.length;
        List<Integer> meterIdList = new ArrayList<Integer>();

        for (int i = 0; i < meterlen; i++) {
            meterIdList.add(Integer.parseInt(meterIds[i]));
        }
        conditionMap.put("meterIdList", meterIdList);

        int datelen = writeDates.length;
        List<String> writeDateList = new ArrayList<String>();

        for (int i = 0; i < datelen; i++) {
            writeDateList.add(writeDates[i]);
        }

        conditionMap.put("writeDateList", writeDateList);

        Map<String, Object> result = meterCtrlManager.getBulkMeterCommandResultData(conditionMap);

        mav.addObject("result", result);
        return mav;
    }

    /**
     * method name : getBulkMeterCommandRunCheck<b/>
     * method Desc : MDIS Bulk Meter Command 실행 여부를 조회한다.
     *
     * @param meterId
     * @param loginId
     * @return
     */
    @RequestMapping(value = "/gadget/device/command/getBulkMeterCommandRunCheck")
    public ModelAndView getBulkMeterCommandRunCheck(@RequestParam("meterIds[]") String[] meterIds,
            @RequestParam("loginId") String loginId) {
        ModelAndView mav = new ModelAndView("jsonView");
//        Map<String, Object> resultMap = new HashMap<String, Object>();
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        List<Integer> meterIdList = new ArrayList<Integer>();
        boolean isProgress = false;
        int len = meterIds.length;

        for (int i = 0; i < len; i++) {
            meterIdList.add(Integer.parseInt(meterIds[i]));
        }
        conditionMap.put("meterIdList", meterIdList);
//        conditionMap.put("loginId", loginId);

        Integer count = meterCtrlManager.getBulkMeterCommandRunCheck(conditionMap);

        if (count == null || count <= 0) {
            isProgress = false;
        } else {
            isProgress = true;
        }

        mav.addObject("isProgress", isProgress);
        return mav;
    }
}