package com.aimir.bo.device;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.system.SupplierDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.CommLog;
import com.aimir.model.device.EventAlertLogVO;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.CommLogManager;
import com.aimir.service.device.EventAlertLogManager;
import com.aimir.service.device.LogAnalysisManager;
import com.aimir.service.device.OperationLogManager;
import com.aimir.service.device.bean.OpCodeConvertMap;
import com.aimir.service.device.bean.OpCodeConvertMap.ConvOper;
import com.aimir.service.device.bean.OpCodeConvertMap.Saver;
import com.aimir.service.system.CodeManager;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Controller
public class LogAnalysisController {

    protected static Log logger = LogFactory.getLog(LogAnalysisController.class);

    @Autowired
    OperationLogManager operationLogManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    CommLogManager commLogManager;

    @Autowired
    EventAlertLogManager eventAlertLogManager;

    @Autowired
    LogAnalysisManager logAnalysisManager;

    @Autowired
    public SupplierDao supplierDao;

    @RequestMapping(value = "/gadget/device/logAnalysisMinGadget")
    public ModelAndView getLogAnalysisMiniGadget() {
        ModelAndView mav = new ModelAndView("/gadget/device/logAnalysisMinGadget");

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();
        mav.addObject("supplierId", supplierId);

        List<Integer> timeGap = new ArrayList<Integer>();
        for(int i=5; i<=60; i=i+5){
            timeGap.add(i);
        }
        mav.addObject("timeGap", timeGap);

        return mav;
    }

    @RequestMapping(value = "/gadget/device/logAnalysisMaxGadget")
    public ModelAndView getLogAnalysisMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/device/logAnalysisMaxGadget");

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        List<Code> codeList = codeManager.getChildCodes(Code.METER_COMMAND);
        mav.addObject("operations", codeList);

        List<Integer> timeGap = new ArrayList<Integer>();
        for(int i=5; i<=60; i=i+5){
            timeGap.add(i);
        }
        mav.addObject("timeGap", timeGap);

        return mav;
    }

    @RequestMapping(value="/gadget/device/logAnalysis/getTotalLogTreeGridOper")
    public ModelAndView getTotalLogTreeGridOper(
        @RequestParam("startDate") String startDate,
        @RequestParam("endDate") String endDate)
    {
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Map<String, String> conditionMap = new HashMap<String, String>();
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("supplierId", user.getRoleData().getSupplier().getId().toString());

        List<Map<String, Object>> gridList = logAnalysisManager.getTotalTreeGridOper(conditionMap);

        List<Map<String, Object>> mappedGridList = new ArrayList<Map<String,Object>>();
        Map<String, Object> mappedMap = null;

        int countId = 1;
        for(Map<String, Object> map : gridList){
            mappedMap = new HashMap<String, Object>();
            mappedMap.put("id", countId);
            mappedMap.put("iconCls", "task-folder");

            mappedMap.put("dateByView", map.get("DATE_BY_VIEW"));
            mappedMap.put("logType", map.get("LOG_TYPE"));
            mappedMap.put("senderId", map.get("SENDER_ID"));
            mappedMap.put("device", map.get("DEVICE"));
            mappedMap.put("userId", map.get("USER_ID"));
            mappedMap.put("operationCode", map.get("OPERATION_CODE"));

            if(map.get("RESULT").toString().equalsIgnoreCase("success")){
            	mappedMap.put("result", "Success");
            }else if(map.get("RESULT").toString().equalsIgnoreCase("fail")){
            	mappedMap.put("result", "Fail");
            }else {
            	mappedMap.put("result", map.get("RESULT"));
            }

            mappedMap.put("message", map.get("MESSAGE"));

            mappedGridList.add(mappedMap);
            countId++;
        }

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("treeList", mappedGridList);

        return mav;
    }

    @RequestMapping(value="/gadget/device/logAnalysis/getTotalLogTreeGridData")
    public ModelAndView getTotalLogTreeGridData(
        @RequestParam("startDate") String startDate,
        @RequestParam("endDate") String endDate,
        @RequestParam(value = "timeGap", defaultValue = "5") int timeGap,
        @RequestParam("node") String node,
        @RequestParam("svcTypeCode") String svcTypeCode,
        @RequestParam("eventAlertClass") String eventAlertClass,  //장애 /이벤트 타입  => EnergyLevelChanged 만 조회
        @RequestParam(value = "device", defaultValue = "") String device)
    {
        ModelAndView mav = new ModelAndView("treeJsonView");

        if(!startDate.equals("") && !device.equals("")){
            AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
            AimirUser user = (AimirUser) instance.getUserFromSession();

            Map<String, String> conditionMap = new HashMap<String, String>();
            try {
                Supplier supplier = user.getSupplier();
                String time_org = TimeLocaleUtil.getDBDate(
                        startDate
                      , 14
                      , supplier.getLang().getCode_2letter()
                      , supplier.getCountry().getCode_2letter());

                conditionMap.put("startDate", time_org);
                conditionMap.put("endDate", TimeUtil.getAddMinute(time_org, timeGap).toString());
                conditionMap.put("supplierId", supplier.getId().toString());
                conditionMap.put("device", device);
                conditionMap.put("svcTypeCode", svcTypeCode);
                conditionMap.put("eventAlertClass", eventAlertClass);
            } catch (ParseException e) {
                logger.error("Time Gap 변환 에러 = " + e.getMessage());
            }

            if(svcTypeCode.equals("C"))
            {
                conditionMap.put("svcTypeCode", "4.9.C");
            }


            List<Map<String, Object>> gridList = logAnalysisManager.getTotalTreeGridData(conditionMap);
            List<Map<String, Object>> mappedGridList = new ArrayList<Map<String,Object>>();
            Map<String, Object> mappedMap = null;

            int countId = 1;
            for(Map<String, Object> map : gridList){
                mappedMap = new HashMap<String, Object>();
                mappedMap.put("id", node + "_" + map.get("LOG_TYPE") + "_" +countId);
                mappedMap.put("iconCls", "task");
                mappedMap.put("leaf", true);

                mappedMap.put("dateByView", map.get("DATE_BY_VIEW"));
                mappedMap.put("logType", map.get("LOG_TYPE"));
                mappedMap.put("senderId", map.get("SENDER_ID"));
                mappedMap.put("device", map.get("DEVICE"));
                mappedMap.put("userId", map.get("USER_ID"));
                mappedMap.put("operationCode", map.get("OPERATION_CODE"));

                if(map.get("RESULT").toString().equalsIgnoreCase("success")){
                	mappedMap.put("result", "Success");
                }else if(map.get("RESULT").toString().equalsIgnoreCase("fail")){
                	mappedMap.put("result", "Fail");
                }else {
                	mappedMap.put("result", map.get("RESULT"));
                }

                mappedMap.put("message", map.get("MESSAGE"));

                mappedGridList.add(mappedMap);
                countId++;
            }

            mav.addObject("result", mappedGridList);
        }else{
            mav.addObject("result", new ArrayList<Map<String,Object>>());
        }

        return mav;
    }

    @RequestMapping(value="/gadget/device/logAnalysis/getOperationLogGridData")
    public ModelAndView getOperationLogGridData(
        @RequestParam(value = "start", defaultValue = "0") int start,
        @RequestParam(value = "limit", defaultValue = "10") int limit,

        @RequestParam("targetName") String targetName,
        @RequestParam(value = "period", defaultValue = "") String period,
        @RequestParam("startDate") String startDate,
        @RequestParam("endDate") String endDate,
        @RequestParam("operation") String operation,
        @RequestParam(value = "date", defaultValue = "") String date,
        @RequestParam("supplierId") String supplierId)
    {
        Map<String, String> conditionMap = new HashMap<String, String>();
        conditionMap.put("targetName", targetName);
        conditionMap.put("period", CommonConstants.DateType.HOURLY.getCode());
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("operation", operation);
        conditionMap.put("date", date);
        conditionMap.put("page", String.valueOf((int)Math.ceil((start + limit) / limit) - 1));
        conditionMap.put("pageSize", String.valueOf(limit));
        conditionMap.put("supplierId", supplierId);

        // 조회 버튼 클릭시
        List<Map<String, Object>> gridList = operationLogManager.getGridData(conditionMap, supplierId);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("gridDatas", gridList);
        mav.addObject("total", operationLogManager.getOperationLogMaxGridDataCount(conditionMap));

        return mav;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/device/logAnalysis/getCommLogGridData")
    public ModelAndView getCommLogGridData(
        @RequestParam(value = "start", defaultValue = "0") int start,
        @RequestParam(value = "limit", defaultValue = "10") int limit,
        @RequestParam("startDate") String startDate,
        @RequestParam("endDate") String endDate,
        @RequestParam(value = "group", defaultValue = "") String group,
        @RequestParam(value = "groupData", defaultValue = "") String groupData,
        @RequestParam("svcTypeCode") String svcTypeCode,
        @RequestParam(value = "receiverId", defaultValue = "") String receiverId,
        @RequestParam("supplierId") String supplierId,
        @RequestParam(value = "eventTime", defaultValue = "") String eventTime,
        @RequestParam(value = "operationCode", required = false) String operationCode,
        @RequestParam(value = "timeGap", defaultValue = "5") int timeGap)
    {
        Map<String, String> conditionMap = new HashMap<String, String>();
        conditionMap.put("period", CommonConstants.DateType.PERIOD.getCode());
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("group", group);
        conditionMap.put("groupData", groupData);
        conditionMap.put("svcTypeCode", svcTypeCode);
        conditionMap.put("page", String.valueOf((int)Math.ceil((start + limit) / limit)));
        conditionMap.put("pageSize", String.valueOf(limit));
        conditionMap.put("receiverId", receiverId);
        conditionMap.put("supplierId", supplierId);
        //conditionMap.put("timeGap", String.valueOf((timeGap * -1)));
        conditionMap.put("timeGap", String.valueOf(timeGap));

        // Operation 코드를 눌러서 검색하는지 여부 체크
        if(eventTime != null && !eventTime.equals("")){
            // Time Gap 설정
            try {
                conditionMap.put("startDate", eventTime);
                conditionMap.put("endDate", TimeUtil.getAddMinute(eventTime, timeGap).toString());
            } catch (ParseException e) {
                logger.error("Time Gap 변환 에러 = " + e.getMessage());
            }

            addMappingCondition(operationCode, conditionMap);
        }

        List<CommLog> listcommlog = commLogManager.getCommLogGridData2(conditionMap);

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> resultMap = null;
        Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));

        for (CommLog comm : listcommlog) {
            resultMap = new HashMap<String, Object>();

            String time_org = TimeLocaleUtil.getDBDate(
                      comm.getTime()
                    , 14
                    , supplier.getLang().getCode_2letter()
                    , supplier.getCountry().getCode_2letter());

            resultMap.put("idx1", comm.getIdx1());
            resultMap.put("time_org", time_org);
            resultMap.put("time", comm.getTime());
            resultMap.put("svcTypeCode", (comm.getSvcTypeCode() == null) ? "" : comm.getSvcTypeCode().getDescr());
            resultMap.put("protocolCode", (comm.getProtocolCode() == null) ? "" : comm.getProtocolCode().getDescr());
            resultMap.put("senderId", comm.getSenderId());
            resultMap.put("sender", comm.getSenderId());
            resultMap.put("receiver", comm.getReceiverId());
            resultMap.put("receiver_desc", ((comm.getReceiverTypeCode() == null) ? "" : comm.getReceiverTypeCode().getDescr()) + "[" + comm.getReceiverId() + "]");
            resultMap.put("result", comm.getResult());
            resultMap.put("strSendBytes", comm.getStrSendBytes());
            resultMap.put("strReceiverBytes", comm.getStrReceiverBytes());
            resultMap.put("strTotalCommTime", comm.getStrTotalCommTime());
            resultMap.put("operationCode", comm.getOperationCode());

            result.add(resultMap);
        }

        String commloggriddatacount = commLogManager.getCommLogGridDataCount(conditionMap);

        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("commloggriddatacount", commloggriddatacount);
        mav.addObject("listcommlog", result);

        return mav;
    }


   @RequestMapping(value = "/gadget/device/logAnalysis/getEventAlramLogHistory")
   public ModelAndView getEventAlertLogHistory(
       @RequestParam(value = "start", defaultValue = "0") int start,
       @RequestParam(value = "limit", defaultValue = "10") int limit,

       @RequestParam("startDate") String startDate,
       @RequestParam("endDate") String endDate,
       @RequestParam(value = "location", defaultValue = "") String location,   // 지역
       @RequestParam("eventAlertClass") String eventAlertClass,  //장애 /이벤트 타입  => EnergyLevelChanged 만 조회
       @RequestParam("supplierId") String supplierId,
       @RequestParam(value = "activatorId", defaultValue = "") String activatorId,
       @RequestParam(value = "eventTime", required = false) String eventTime,
       @RequestParam(value = "timeGap", defaultValue = "5") int timeGap)
   {
       Map<String, String> conditionMap = new HashMap<String, String>();
       conditionMap.put("page", String.valueOf((int)Math.ceil((start + limit) / limit)));
       conditionMap.put("pageSize", String.valueOf(limit));
       conditionMap.put("supplierId", supplierId);
       conditionMap.put("period", CommonConstants.DateType.PERIOD.getCode());

       List<String> valuesList = new ArrayList<String>();
       valuesList.add("supplierId:" + supplierId);
       valuesList.add("location:" + location);

       valuesList.add("eventAlertClass:" + eventAlertClass);
       valuesList.add("activatorId:" + activatorId);

       // CommLog 코드를 눌러서 검색하는지 여부 체크
       if(eventTime != null && !eventTime.equals("")){
           // Time Gap 설정
           try {
               valuesList.add("startDate:" + eventTime);
               valuesList.add("endDate:" + TimeUtil.getAddMinute(eventTime, timeGap).toString());

               logger.debug("############ timeGap = " + TimeUtil.getAddMinute(eventTime, timeGap).toString());

           } catch (ParseException e) {
               logger.error("Time Gap 변환 에러");
           }
       }else{
           valuesList.add("startDate:" + startDate);
           valuesList.add("endDate:" + endDate);
       }


       List<EventAlertLogVO> eventAlertLogList = eventAlertLogManager.getEventAlertLogHistory2(valuesList.toArray(new String[0]), conditionMap);

       String totalCnt = eventAlertLogManager.getEventAlertLogHistoryTotalCnt(valuesList.toArray(new String[0]));


       ModelAndView mav = new ModelAndView("jsonView");
       mav.addObject("eventAlertLogList", eventAlertLogList);
       mav.addObject("eventalertloghistorytotal", Integer.parseInt(totalCnt));

       return mav;
   }

   /**
    * 오퍼레이션 로그의 Operation code와 매칭되는 Comm Log 를 검색하기위한
    * 조건을 추가해주는 기능
    * @param operationCode
    * @param conditionMap
    */
   private void addMappingCondition(String operationCode, Map<String, String> conditionMap){
       OpCodeConvertMap map = logAnalysisManager.getOpCodeConvertMap("OperationCodeMapTable.xml");

       ConvOper oper = map.getOperation(operationCode);
       if(oper != null){
           List<Saver> savers = oper.getSavers();

           if(savers != null && 0 < savers.size()){
               StringBuilder builder = new StringBuilder();

               for(Saver saver : savers){
                   builder.append("'" + saver.getOperationMsg() + "',");
               }

               String text = builder.toString().trim();
               conditionMap.put("operationCode", text.substring(0, text.length()-1));
           }else{
               conditionMap.put("operationCode", "'HAVE_NOT_OPERATION_MAPPING'");
           }
       }else{
           conditionMap.put("operationCode", "'HAVE_NOT_OPERATION_MAPPING'");
       }

   }


}