package com.aimir.bo.device.eventAlert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.common.CommandProperty;
import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.EventAlertLogVO;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.device.EventAlertLogManager;
import com.aimir.service.device.EventAlertManager;
import com.aimir.service.device.MeterManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ProfileManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils;
import com.aimir.util.EventAlertLogMakeExcel;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;


@Controller
public class EventAlertLogController {
    private static Log log = LogFactory.getLog(EventAlertLogController.class);

    @Autowired
    HibernateTransactionManager transactionManager;

    @Autowired
    EventAlertLogManager eventAlertLogManager;

    @Autowired
    EventAlertManager eventAlertManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    ProfileManager profileManager;

    @Autowired
    SupplierManager supplierManager;

    @Autowired
    RoleManager roleManager;

    @Autowired
    CmdOperationUtil cmdOperationUtil;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    MeterManager meterManager;
    
    @Autowired
    EventAlertDao eventAlertDao;
    
//    @Autowired
//    JmsTemplate activeJmsTemplate;

//    @Autowired
//    Topic eventTopic;

//    protected MessageSender messageSender = null;
//    protected MessageReceiver messageReceiver = null;

    /**
     *
     *@desc: 이벤트 알람 로그 history fetch action
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/gadget/device/eventAlert/getEventAlramLogHistory.do")
    public ModelAndView getEventAlramLogHistory(@RequestParam("page") String page,
            @RequestParam("supplierId") String supplierId,
            @RequestParam("pageSize") String pageSize,
            // 검색 조건 values
            @RequestParam("values") String[] values) {
        Map<String, String> conditionMap = new HashMap<String, String>();

        // 검색 조건 셋팅 부분.
        conditionMap.put("page", page);
        conditionMap.put("pageSize", pageSize);
        conditionMap.put("supplierId", supplierId);

        ModelAndView mav = new ModelAndView("jsonView");

        // ## EventAlertLogHistory list fetch
        List eventAlertLogList = eventAlertLogManager.getEventAlertLogHistory2(values, conditionMap);

        // 이벤트 알람 내역 List TOTAL count
        String totalCnt = eventAlertLogManager.getEventAlertLogHistoryTotalCnt(values);

        mav.addObject("eventAlertLogList", eventAlertLogList);

        mav.addObject("eventalertloghistorytotal", Integer.parseInt(totalCnt));

        return mav;
    }

    @RequestMapping(value = "/gadget/device/eventAlert/eventAlertLogMax")
    public ModelAndView getEventAlertLogMax(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("/gadget/device/eventAlert/eventAlertLogMax");

        String interval = CommandProperty.getProperty("event.alert.interval");

        if (StringUtil.nullToBlank(interval).isEmpty()) {
            interval = "3000";
        }
        mav.addObject("interval", interval);

        ESAPI.httpUtilities().setCurrentHTTP(request, response);

        // ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();

        AimirUser user = (AimirUser) instance.getUserFromSession();

        if (user != null && !user.isAnonymous()) {
            try {
                mav.addObject("userId", user.getOperator(new Operator()).getId());
                mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<String> eventStatusList = new ArrayList<String>();
        for (EventStatus status : EventStatus.values()) {
            if (status.name().equals("Open") || status.name().equals("Cleared")) {
                continue;
            }
            eventStatusList.add(status.name());
        }
        mav.addObject("eventStatusList", eventStatusList);

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)

        return mav;
    }

    @RequestMapping(value = "/gadget/device/eventAlert/eventAlertLogMini")
    public ModelAndView getEventAlertLogMini(HttpServletRequest request, HttpServletResponse response) {
    	//WEB Stop현상으로인해 eventAlertLogMini가젯의 RealTime기능을 제거
    	//ModelAndView mav = new ModelAndView("/gadget/device/eventAlert/eventAlertLogMiniEmpty");
    	//Mini가젯의 RealTime기능 사용시 eventAlertLogMini 로 소스 교체
        ModelAndView mav = new ModelAndView("/gadget/device/eventAlert/eventAlertLogMini");

        String interval = CommandProperty.getProperty("event.alert.interval");

        if (StringUtil.nullToBlank(interval).isEmpty()) {
            interval = "3000";
        }
        mav.addObject("interval", interval);

        DataUtil.setApplicationContext(WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext()));
        ESAPI.httpUtilities().setCurrentHTTP(request, response);

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        if (user != null && !user.isAnonymous()) {
            try {
                mav.addObject("userId", user.getOperator(new Operator()).getId());
                mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<String> eventStatusList = new ArrayList<String>();
        for (EventStatus status : EventStatus.values()) {
            if (status.name().equals("Open") || status.name().equals("Cleared")) {
                continue;
            }
            eventStatusList.add(status.name());
        }
        mav.addObject("eventStatusList", eventStatusList);

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)

        return mav;
    }

    @RequestMapping(value = "/gadget/device/eventAlert/eventAlertLogInit.*")
    public ModelAndView getEventAlertLogInit(HttpServletRequest request,
            HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("eventAlertType", CommonConstants.getEventAlertTypes());
        mav.addObject("severity", CommonConstants.getSeverityTypes());
        mav.addObject("status", CommonConstants.getEventStatuses());
        mav.addObject("activatorType", codeManager.getChildCodes(codeManager
                .getCodeByName("SystemClass").getCode()));
        mav.addObject("eventAlertClass", eventAlertManager.getEventAlerts());

        ESAPI.httpUtilities().setCurrentHTTP(request, response);

        // ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI
                .authenticator();

        AimirUser user = (AimirUser) instance.getUserFromSession();

        if (user != null && !user.isAnonymous()) {
            try {
                int userId = user.getOperator(new Operator()).getId();

                mav.addObject("profile", profileManager
                        .getProfileByUser(userId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mav;
    }

    @RequestMapping(value = "/gadget/device/eventAlert/getEventAlertClass.*")
    public ModelAndView getEventAlertClass(@RequestParam("eventAlertType") String eventAlertType) {
        ModelAndView mav = new ModelAndView("jsonView");

        if (eventAlertType.equals("all")) {
            mav.addObject("eventAlertClass", eventAlertManager.getEventAlerts());
        } else {
            mav.addObject("eventAlertClass", eventAlertManager.getEventAlertsByType(eventAlertType));
        }

        return mav;
    }
    
    @RequestMapping(value = "/gadget/device/eventAlert/eventAlertConfInit.*")
    public ModelAndView getEventAlertConfInit(HttpServletRequest request,
            HttpServletResponse response) {
    	// 이벤트 설정에 필요한 정보를 조회 (모니터타입, 우선순위 등)
        ModelAndView mav = new ModelAndView("jsonView");
        
        mav.addObject("severity", CommonConstants.getSeverityTypes());
        mav.addObject("monitor", CommonConstants.MonitorType.values());
        mav.addObject("category", CommonConstants.getEventAlertTypes());

        ESAPI.httpUtilities().setCurrentHTTP(request, response);

        // ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI
                .authenticator();

        AimirUser user = (AimirUser) instance.getUserFromSession();

        if (user != null && !user.isAnonymous()) {
            try {
                int userId = user.getOperator(new Operator()).getId();

                mav.addObject("profile", profileManager
                        .getProfileByUser(userId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mav;
    }
    
    @RequestMapping(value = "/gadget/device/eventAlert/getEventAlertList")
    public ModelAndView getEventAlertList(@RequestParam("page") String page,            
            							  @RequestParam("pageSize") String pageSize) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, String> conditionMap = new HashMap<String, String>();
        
        conditionMap.put("page", page);
        conditionMap.put("pageSize", pageSize);
        
        //mav.addObject("eventAlertRoot", eventAlertManager.getEventAlerts());
        mav.addObject("eventAlertRoot", eventAlertManager.getEventAlertListWithPaging(conditionMap));
        Integer totalCnt = eventAlertManager.getEventAlertListCount(conditionMap);
        mav.addObject("eventAlertListCount", totalCnt);
        
        return mav;
    }
    
    @RequestMapping(value = "/gadget/device/eventAlert/updateEventConfiguration")
    public ModelAndView updateEventConfiguration(@RequestParam("eMonitorType") String eMonitorType,            
    											 @RequestParam("eDescription") String eDescription,
    											 @RequestParam("eSeverity") String eSeverity,
    											 @RequestParam("eAdvice") String eAdvice,
    											 @RequestParam("eCategory") String eCategory,
    											 @RequestParam("eId") String eId) {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, String> conditionMap = new HashMap<String, String>();
        
        conditionMap.put("eMonitorType", eMonitorType);
        conditionMap.put("eDescription", eDescription);
        conditionMap.put("eSeverity", eSeverity);
        conditionMap.put("eAdvice", eAdvice);
        conditionMap.put("eCategory", eCategory);
        conditionMap.put("eId", eId);
        
        Map<String, String> result = eventAlertManager.updateEventAlertConfig(conditionMap);
        mav.addObject("result", result);        
        return mav;
    }

    @RequestMapping(value = "/gadget/device/eventAlert/getEventAlertLogForMini")
    public ModelAndView getEventAlertLogForMini(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);

        List<Map<String, Object>> result1 = eventAlertLogManager.getEventAlertLogByActivatorTypeForMini(conditionMap);
        mav.addObject("LogType", result1);

        List<Map<String, Object>> result2 = eventAlertLogManager.getEventAlertLogByMessageForMini(conditionMap);
        mav.addObject("LogMessage", result2);

        return mav;
    }

    @RequestMapping(value = "/gadget/device/eventAlert/getEventAlertLog")
    public ModelAndView getEventAlertLog(
            @RequestParam("supplierId") Integer supplierId,
            @RequestParam("searchStartDate") String searchStartDate,
            @RequestParam("searchEndDate") String searchEndDate,
            @RequestParam("eventAlertType") String eventAlertType,
            @RequestParam("severity") String severity,
            @RequestParam("eventAlertClass") Integer eventAlertClass,
            @RequestParam("status") String status,
            @RequestParam("activatorType") String activatorType,
            @RequestParam("activatorId") String activatorId,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("message") String message) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("searchStartDate", searchStartDate);
        conditionMap.put("searchEndDate", searchEndDate);
        conditionMap.put("eventAlertType", eventAlertType);
        conditionMap.put("severity", severity);
        conditionMap.put("eventAlertClass", eventAlertClass);
        conditionMap.put("status", status);
        conditionMap.put("activatorType", activatorType);
        conditionMap.put("activatorId", activatorId);
        conditionMap.put("locationId", locationId);
        conditionMap.put("message", message);

        List<Map<String, Object>> result1 = eventAlertLogManager.getEventAlertLogByActivatorType(conditionMap);
        mav.addObject("LogType", result1);

        List<Map<String, Object>> result2 = eventAlertLogManager.getEventAlertLogByMessage(conditionMap);
        mav.addObject("LogMessage", result2);

        return mav;
    }

    /**
     * @desc : fetch real time log fetch action.
     * @param page
     * @param supplierId
     * @param pageSize
     * @param values
     * @return
     */
    @RequestMapping(value = "/gadget/device/eventAlert/getEventAlertLogRealTimeForMax.do")
    @Deprecated
    //public ModelAndView getEventAlertLogRealTimeForMax(@RequestParam("userId") String userId,
    //        @RequestParam("supplierId") String supplierId, @RequestParam("pageSize") String pageSize)
    public ModelAndView getEventAlertLogRealTimeForMax(@RequestParam("supplierId") Integer supplierId){
        Map<String, String> conditionMap = new HashMap<String, String>();

        // 검색 조건 셋팅 부분.
        // conditionMap.put("page", page);
//        conditionMap.put("pageSize", pageSize);
//        conditionMap.put("supplierId", supplierId);

        ModelAndView mav = new ModelAndView("jsonView");
                        
        Supplier supplier = supplierManager.getSupplier(supplierId);

        String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        
        mav.addObject("country", country);
        mav.addObject("lang", lang);
        /*
         * TODO - command.properties 파일의 'activemq.jmxrmi' 값을 ',' 로 구분해서 여러 개 넣으면 각각의 ActiveMQ 에서 데이터를 가져온다.
         * ex) 187.1.10.58 서버와 187.1.10.71 서버에 각각 ActiveMQ 가 있는 경우:
         *     activemq.jmxrmi=service:jmx:rmi:///jndi/rmi://187.1.10.58:1616/jmxrmi,service:jmx:rmi:///jndi/rmi://187.1.10.71:1616/jmxrmi
         *     위 내용을 응용하여 설정파일에 한줄이 추가되었다.
         *     activemq.stomp=ws://localhost:61614,ws://187.1.10.58:61614 이렇게 여러 경로를 추가하여 각각의 ActiveMQ에서 메시지를 받아온다.
         */
        String activemqProp = CommandProperty.getProperty("activemq.stomp");
        if(activemqProp == null){
        	activemqProp="ws:localhost:61614";
        }
        String[] activemqList = activemqProp.split(",");
        int ListLength = activemqList.length;
        //String wsurl = "ws://187.1.10.58:61616/stomp";
        
        mav.addObject("wsurl", activemqList);
        mav.addObject("wslen", ListLength);
        
        

        // ## eventAlertLogRealTimeList fetch p.
        // List eventAlertLogRealTimeList = eventAlertLogManager.getEventAlertLogRealTimeForMax(userId, supplierId, 0, 15);

        // List TOTAL count
        // String totalCnt = eventAlertLogManager.getEventAlertLogRealTimeTotal(userId, supplierId);

        // mav.addObject("eventAlertLogRealTimeList", eventAlertLogRealTimeList);       

        // mav.addObject("totalCnt", totalCnt);

        return mav;
    }

    @RequestMapping(value = "/gadget/device/eventAlert/getRealTimeEventAlertLog")
    public ModelAndView getRealTimeEventAlertLog(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("messageId") String messageId) {

        ModelAndView mav = new ModelAndView("jsonView");
        Supplier supplier = supplierManager.getSupplier(supplierId);        

        if (EventMessage.getInstnce().getMessageId() != "") {

            if (messageId.equals(EventMessage.getInstnce().getMessageId())) {
                mav.addObject("message", "");
            } else {

                Map<String, Object> mapMessage = EventMessage.getInstnce().getMapMessage();
                // System.out.println("mapMessage start:"+mapMessage);
                if (mapMessage.get("openTime") != null) {
                    mapMessage.put("msgOpenTime", TimeLocaleUtil.getLocaleDate((String) mapMessage.get("openTime"), supplier
                            .getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
                }
                if (mapMessage.get("writeTime") != null) {
                    mapMessage.put("msgWriteTime", TimeLocaleUtil.getLocaleDate((String) mapMessage.get("writeTime"), supplier
                            .getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
                }

                if (mapMessage.get("closeTime") != null) {
                    mapMessage.put("msgCloseTime", TimeLocaleUtil.getLocaleDate((String) mapMessage.get("closeTime"), supplier
                            .getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
                }
                // System.out.println("mapMessage end:"+mapMessage);
                mav.addObject("message", mapMessage);
            }

            mav.addObject("messageId", EventMessage.getInstnce().getMessageId());
        } else {
            mav.addObject("message", "");
            mav.addObject("messageId", "");
        }
        return mav;
    }

    /**
     * method name : getRealTimeEventAlertLogFromActiveMq<b/>
     * method Desc : Active MQ 에서 실시간 이벤트 정보를 가져온다.
     *
     * @param supplierId
     * @param interval
     * @param startTime
     * @return
     */
    @RequestMapping(value = "/gadget/device/eventAlert/getRealTimeEventAlertLogFromActiveMq")
    public ModelAndView getRealTimeEventAlertLogFromActiveMq(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("interval") long interval,
            @RequestParam(value="startTime", required=false) Long startTime) {
        ModelAndView mav = new ModelAndView("jsonView");

        List<Properties> retList = new ArrayList<Properties>();
        Long currentTime = null;

        try {
            ObjectName notiObj = new ObjectName("org.apache.activemq:BrokerName=localhost,Type=Queue,Destination=AiMiR.Event");

            currentTime = TimeUtil.getCurrentLongTime();
            mav.addObject("startTime", currentTime);

            if (startTime == null) {
                startTime = currentTime - interval;
            } else {
                startTime = startTime + 1;
            }

            StringBuilder selector = new StringBuilder();
            selector.append("JMSTimestamp between ").append(startTime).append(" and ").append(currentTime).append(" and supplierId=").append(supplierId);

            /*
             * TODO - command.properties 파일의 'activemq.jmxrmi' 값을 ',' 로 구분해서 여러 개 넣으면 각각의 ActiveMQ 에서 데이터를 가져온다.
             * ex) 187.1.10.58 서버와 187.1.10.71 서버에 각각 ActiveMQ 가 있는 경우:
             *     activemq.jmxrmi=service:jmx:rmi:///jndi/rmi://187.1.10.58:1616/jmxrmi,service:jmx:rmi:///jndi/rmi://187.1.10.71:1616/jmxrmi
             */
            String activemqProp = CommandProperty.getProperty("activemq.jmxrmi");
            String[] activemqList = activemqProp.split(",");

            List<CompositeData> compositeDataList = new ArrayList<CompositeData>();

            JMXServiceURL url = null;
            JMXConnector jmxc = null;
            MBeanServerConnection mbsc = null;
            QueueViewMBean queueMBean = null;

            for (String activemq : activemqList) {
                if (activemq.isEmpty()) {
                    continue;
                }
                url = new JMXServiceURL(activemq);
                jmxc = JMXConnectorFactory.connect(url);
                mbsc = jmxc.getMBeanServerConnection();

                queueMBean = (QueueViewMBean)MBeanServerInvocationHandler.newProxyInstance(mbsc, notiObj, QueueViewMBean.class, true);

                CompositeData[] datalistSub = queueMBean.browse(selector.toString());

                if (datalistSub != null && datalistSub.length > 0) {
                    compositeDataList.addAll((List<CompositeData>)Arrays.asList(datalistSub));
                }

                // 삭제
                selector = new StringBuilder();
                selector.append("JMSTimestamp < ").append(startTime).append(" and supplierId=").append(supplierId);
                int removeCount = queueMBean.removeMatchingMessages(selector.toString());
                
                if (removeCount > 0) {
                    log.debug("Remove Message MQ_URL[" + activemq + "] COUNT[" + removeCount + "] Left Message[" + queueMBean.getQueueSize() + "]");
                }
                
                jmxc.close();
            }

            mav.addObject("queueLength", compositeDataList.size());

            Supplier supplier = supplierManager.getSupplier(supplierId);
            String lang = supplier.getLang().getCode_2letter();
            String country = supplier.getCountry().getCode_2letter();
            String contentMap = null;

            // 날짜순 정렬
            Collections.sort(compositeDataList, new Comparator<CompositeData>() {

                @Override
                public int compare(CompositeData o1, CompositeData o2) {
                    // TODO Auto-generated method stub
                    return ((Date)o1.get("JMSTimestamp")).compareTo((Date)o2.get("JMSTimestamp"));
                }
                
            });
            
            for (CompositeData cd : compositeDataList) {
                contentMap = (String)cd.get("ContentMap");
                getRealTimeEventLogFromQueueData(retList, contentMap, 0, lang, country);
            }
            
        } catch (Exception e) {
            log.error(e, e);
        }

        mav.addObject("logList", retList);
        return mav;
    }
    
//    /**
//     * method name : deleteRealTimeEventAlertLogFromActiveMq<b/>
//     * method Desc : 오래된 MQ 데이터를 삭제한다.
//     *
//     * @param supplierId
//     * @param interval
//     * @param startTime
//     * @return
//     */
//    @RequestMapping(value = "/gadget/device/eventAlert/deleteRealTimeEventAlertLogFromActiveMq")
//    public ModelAndView deleteRealTimeEventAlertLogFromActiveMq(@RequestParam("supplierId") Integer supplierId,
//            @RequestParam("interval") long interval,
//            @RequestParam("startTime") Long startTime) {
//        ModelAndView mav = new ModelAndView("jsonView");
//
//        List<Properties> retList = new ArrayList<Properties>();
//        Long currentTime = null;
//
//        try {
//            ObjectName notiObj = new ObjectName("org.apache.activemq:BrokerName=localhost,Type=Queue,Destination=AiMiR.Event");
//            String activemqProp = CommandProperty.getProperty("activemq.jmxrmi");
//            String[] activemqList = activemqProp.split(",");
//
//            List<CompositeData> compositeDataList = new ArrayList<CompositeData>();
//
//            // 삭제범위를 조회간격 x 3 정도 시간차를 둔다
//            Long removeInterval = interval * 5;
//
//            StringBuilder removeSelector = new StringBuilder();
//            removeSelector.append("JMSTimestamp < ").append(startTime - removeInterval).append(" and supplierId=").append(supplierId);
//            log.debug("====================> removeSelector : " + removeSelector);
//            int removeCount = 0;
////            log.debug("====================> 01 ");
//
//            JMXServiceURL url = null;
//            JMXConnector jmxc = null;
//            MBeanServerConnection mbsc = null;
//            QueueViewMBean queueMBean = null;
//
//            for (String activemq : activemqList) {
////                log.debug("====================> 02-1 ");
//                if (activemq.isEmpty()) {
//                    continue;
//                }
//                url = new JMXServiceURL(activemq);
//                jmxc = JMXConnectorFactory.connect(url);
//                mbsc = jmxc.getMBeanServerConnection();
//
//                queueMBean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(mbsc, notiObj, QueueViewMBean.class, true);
//
////                CompositeData[] datalistSub = queueMBean.browse(selector);
////                log.debug("Enqueue count[" + queueMBean.getEnqueueCount() + "] message size[" + queueMBean.browseAsTable().size() + "]");
//
////                log.debug("====================> 02-2 ");
////                log.debug("queueMBean.isCursorFull():" + queueMBean.isCursorFull() + ", queueMBean.cursorSize():" + queueMBean.cursorSize() + ", queueMBean.getQueueSize():" + queueMBean.getQueueSize());
//                removeCount += queueMBean.removeMatchingMessages(removeSelector.toString());
//                jmxc.close();
////                log.debug("====================> 02-4 ");
//            }
//
////            if (removeCount > 0) {
////                log.debug("Remove Message COUNT[" + removeCount + "]");
////            }
//            log.debug("====================> Remove Message COUNT[" + removeCount + "]");
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error(e, e);
//        }
//
//        mav.addObject("result", "success");
////        log.debug("====================> 05 ");
//        return mav;
//    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
//    /**
//     * method name : getRealTimeEventAlertLogFromActiveMq<b/>
//     * method Desc : Active MQ 에서 실시간 이벤트 정보를 가져온다.
//     *
//     * @param supplierId
//     * @param interval
//     * @param startTime
//     * @return
//     */
//    @RequestMapping(value = "/gadget/device/eventAlert/getRealTimeTopicData")
//    public ModelAndView getRealTimeTopicData(HttpServletResponse response,
//            HttpServletRequest request,
//            @RequestParam(value="clientId", required=false) String clientId) {
//        ModelAndView mav = new ModelAndView("jsonView");
//
//        ServletContext servletContext = request.getSession().getServletContext();
//        messageReceiver = (MessageReceiver)servletContext.getAttribute("messageReceiver");
//        
//        if (messageReceiver == null) {
////            mav.addObject("status", Boolean.FALSE);
////            mav.addObject("result", "Can not receive topic.");
////            return mav;
//            messageReceiver = new MessageReceiver();
//            Thread messageReceiverThread = new Thread(messageReceiver, "MessageReceiver");
//            messageReceiverThread.setDaemon(true);
//            messageReceiverThread.start();
//            servletContext.setAttribute("messageReceiver", messageReceiver);
//        }
//        
//        messageSender = (MessageSender)servletContext.getAttribute("messageSender");
//
//        if (messageSender == null) {
//            messageSender = new MessageSender(activeJmsTemplate, eventTopic, messageReceiver);
//            Thread messageSenderThread = new Thread(messageSender, "MessageSender");
//            messageSenderThread.setDaemon(true);
//            messageSenderThread.start();
//            servletContext.setAttribute("messageSender", messageSender);
//        }
//
//        mav.addObject("status", Boolean.TRUE);
//
//        return mav;
//    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<Properties> getRealTimeEventLogFromQueueData(List<Properties> retList, String content, int index, String lang, String country) {
        Properties prop = null;
        InputStreamReader isr = null;
        ByteArrayInputStream bis = null;

        try {
            bis = new ByteArrayInputStream(content.substring(1, content.length() - 1).replace(", ", "\n").getBytes("UTF-8"));
            isr = new InputStreamReader(bis, "UTF-8");
            prop = new Properties();
            prop.load(isr);

            prop.put("eventMessage", convertMessage(prop, prop.get("eventMessage") + ""));
            prop.put("openTime", TimeLocaleUtil.getLocaleDate((String) prop.get("openTime"), lang, country));
            prop.put("closeTimenf", (String) prop.get("closeTime"));
            prop.put("closeTime", TimeLocaleUtil.getLocaleDate((String) prop.get("closeTime"), lang, country));
            prop.put("writeTime", TimeLocaleUtil.getLocaleDate((String) prop.get("writeTime"), lang, country));

            retList.add(index, prop);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e, e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (Exception e) {
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (Exception e) {
                }
            }
        }
        return retList;
    }

    @RequestMapping(value = "/gadget/device/eventAlert/eventAlertLogExcelDownloadPopup")
    public ModelAndView eventAlertLogExcelDownloadPopup() {
        ModelAndView mav = new ModelAndView("/gadget/device/eventAlert/eventAlertLogExcelDownloadPopup");
        return mav;
    }

    @RequestMapping(value = "/gadget/device/eventAlert/eventAlertLogGadgetExcelMake")
    public ModelAndView eventAlertLogGadgetExcelMake(@RequestParam("condition[]") String[] condition,
            @RequestParam("fmtMessage[]") String[] fmtMessage,
            @RequestParam("filePath") String filePath) {

        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
        List<EventAlertLogVO> list = new ArrayList<EventAlertLogVO>();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        boolean isLast = false;
        Long total = 0L; // 데이터 조회건수
        Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

        final String logPrefix = "eventAlertLog";

        ModelAndView mav = new ModelAndView("jsonView");

        List<EventAlertLogVO> result = null;
        try {

            result = (List<EventAlertLogVO>) eventAlertLogManager.getEventAlertLogHistoryExcel(condition);

            total = new Integer(result.size()).longValue();

            mav.addObject("total", total);
            if (total <= 0) {
                return mav;
            }

            sbFileName.append(logPrefix);

            sbFileName.append(TimeUtil.getCurrentTimeMilli());

            // message 생성
            msgMap.put("No", "No");
            msgMap.put("severity", fmtMessage[11]);
            msgMap.put("Type", "Type");
            msgMap.put("message", fmtMessage[0]);
            msgMap.put("location", fmtMessage[1]);
            msgMap.put("activatorId", fmtMessage[2]);
            msgMap.put("activatorType", fmtMessage[3]);
            msgMap.put("equipip", fmtMessage[4]);
            msgMap.put("status", fmtMessage[5]);
            msgMap.put("writetime", fmtMessage[6]);
            msgMap.put("opentime", fmtMessage[7]);
            msgMap.put("closetime", fmtMessage[8]);
            msgMap.put("duration", fmtMessage[9]);
            msgMap.put("title", fmtMessage[13]);

            // check download dir
            File downDir = new File(filePath);

            if (downDir.exists()) {
                File[] files = downDir.listFiles();

                if (files != null) {
                    String filename = null;
                    String deleteDate;

                    deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10); // 10일 이전 일자
                    boolean isDel = false;

                    for (File file : files) {

                        filename = file.getName();
                        isDel = false;

                        // 파일길이 : 20이상, 확장자 : xls|zip
                        if (filename.length() > 20 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
                            // 10일 지난 파일들 삭제
                            if (filename.startsWith(logPrefix) && filename.substring(13, 21).compareTo(deleteDate) < 0) {
                                isDel = true;
                            }

                            if (isDel) {
                                file.delete();
                            }
                        }
                        filename = null;
                    }
                }
            } else {
                // directory 가 없으면 생성
                downDir.mkdir();
            }

            // create excel file
            EventAlertLogMakeExcel wExcel = new EventAlertLogMakeExcel();
            int cnt = 1;
            int idx = 0;
            int fnum = 0;
            int splCnt = 0;

            if (total <= maxRows) {
                sbSplFileName = new StringBuilder();
                sbSplFileName.append(sbFileName);
                sbSplFileName.append(".xls");
                wExcel.writeReportExcel(result, msgMap, isLast, filePath, sbSplFileName.toString());
                fileNameList.add(sbSplFileName.toString());
            } else {
                for (int i = 0; i < total; i++) {
                    if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
                        sbSplFileName = new StringBuilder();
                        sbSplFileName.append(sbFileName);
                        sbSplFileName.append('(').append(++fnum).append(").xls");

                        list = result.subList(idx, (i + 1));

                        wExcel.writeReportExcel(list, msgMap, isLast, filePath, sbSplFileName.toString());
                        fileNameList.add(sbSplFileName.toString());
                        list = null;
                        splCnt = cnt;
                        cnt = 0;
                        idx = (i + 1);
                    }
                    cnt++;
                }
            }

            // create zip file
            StringBuilder sbZipFile = new StringBuilder();
            sbZipFile.append(sbFileName).append(".zip");

            ZipUtils zutils = new ZipUtils();
            zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);

            // return object
            mav.addObject("filePath", filePath);
            mav.addObject("fileName", fileNameList.get(0));
            mav.addObject("zipFileName", sbZipFile.toString());
            mav.addObject("fileNames", fileNameList);

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "/gadget/device/eventAlert/changeEventAlertStatus")
    public ModelAndView changeEventAlertStatus(@RequestParam("eventLogId") Long eventLogId,
            @RequestParam("eventStatusName") String eventStatusName) {
        ModelAndView mav = new ModelAndView("jsonView");
		//TransactionStatus txStatus = null;
        //DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        try {
            if (eventLogId == null) {
                log.error("Event Alert Log ID is null!!!");
                mav.addObject("result", "failure");
            } else {
                //txStatus = transactionManager.getTransaction(txDefine);
                long logId = eventLogId.longValue();
                //EventStatus eventStatus = EventStatus.valueOf(eventStatusName);
                //EventUtil.sendEvent(logId, eventStatus);
                String obj = cmdOperationUtil.cmdSendEventByFep(logId, eventStatusName);
                mav.addObject("result", obj);
                //transactionManager.commit(txStatus);
            }
        } catch (Exception e) {
            //if (txStatus != null) transactionManager.rollback(txStatus);
            log.error(e, e);
            mav.addObject("result", "failure");
        }

        return mav;
    }

    private String convertMessage(Properties prop, String msg) {
        String pattern = "([$][(][a-zA-Z0-9.]+[)])";
        
        try {
            RE re = new RE(pattern);
    
            int pos = 0;
    
            String res = "";
    
            if (!re.match(msg)) {
                return msg;
            }
            while (re.match(msg)) {
                String var = re.getParen(1);
                int idx = msg.indexOf(var);
                String before = msg.substring(0, idx);
    
                res = res + before;
                int var_len = var.length();
    
                String convertVar = convertParam(prop, var.substring(2, var_len - 1));
    
                res = res + convertVar;
    
                pos = idx + var_len;
                msg = msg.substring(pos);
    
            }
            if (msg.equals("]")) {
                res = res + msg;
            }
            return res;
        }
        catch(RESyntaxException e) {}
        return msg;
    }

    public String convertParam(Properties prop, String msg) {
        int paramIdx = msg.lastIndexOf(".");

        String param = msg.substring(paramIdx + 1, msg.length());
        String inMsg = prop.get(param) + "";
        try {
            if (inMsg != null && inMsg.length() > 0) {
                return inMsg;
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
    
    // INSERT START SP-193
    @RequestMapping(value = "/gadget/device/eventAlert/getThresholdList")
    public ModelAndView getAllThreshold() {
        ModelAndView mav = new ModelAndView("jsonView");
        List<Map<String,Object>> nameList = new ArrayList<Map<String,Object>>();
        
        mav.addObject("thresholdList", eventAlertManager.getAllThreshold());

		for (ThresholdName name : ThresholdName.values()) {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("name", name.name());
			map.put("value", name.getThresholdNameValue());
			nameList.add(map);
		}
        mav.addObject("thresholdNameList", nameList);
        
        return mav;
    }
    
    @RequestMapping(value = "/gadget/device/eventAlert/updateAllThreshold")
    public ModelAndView updateAllThreshold(	@RequestParam("count") Integer count,
    										@RequestParam("name[]") String[] nameArray,            
    										@RequestParam("threshold[]") String[] thresholdArray,
    										@RequestParam("schedule[]") String[] scheduleArray
    									) {
        ModelAndView mav = new ModelAndView("jsonView");
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        
        for(int i=0; i < count; i++ ) {
            Map<String, String> conditionMap = new HashMap<String, String>();
	        conditionMap.put("name", ThresholdName.getThresholdName(nameArray[i]).name());
	        conditionMap.put("threshold", thresholdArray[i]);
	        conditionMap.put("schedule", scheduleArray[i]);
	        list.add(conditionMap);
        }
		TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        try {        
        txStatus = transactionManager.getTransaction(txDefine);
            Map<String, String> result = eventAlertManager.updateAllThreshold(list);
            mav.addObject("result", result);
      	
        transactionManager.commit(txStatus);
	    } catch (Exception e) {
	        if (txStatus != null) transactionManager.rollback(txStatus);
	        log.error(e, e);
	        mav.addObject("result", "failure");
	    }        
        
        return mav;
    }    
    // INSERT END SP-193
    
    @RequestMapping(value = "/gadget/device/eventAlert/checkDeviceLicence")
	public ModelAndView checkDeviceLicence(
			@RequestParam("supplierId") Integer supplierId,
			@RequestParam("activatorTypeId") Integer activatorTypeId) throws Exception {
    	
     	ModelAndView mav = new ModelAndView("jsonView");
 		
 		String eventAlertName = "Excessive Number of Device Registration";
 		String activatorId = " ";
 		int limitedCount = 0;

 		try {
 			Supplier supplier = supplierDao.get(supplierId);
 			String activatorType = codeManager.getCode(activatorTypeId).getName();
 			
 			int licenceUse = supplier.getLicenceUse();
 			
 			if(licenceUse == 1) {
 				int currentMeterCount = meterManager.getTotalMeterCount();
 				limitedCount = supplier.getLicenceMeterCount();
 				log.info("LimitedCount = " + limitedCount + ", CurrentMeterCount = " + currentMeterCount);
 				
 				// Meter 등록 가능시, limitedCount에 0을 넣어 WEB단으로 반환한다.
 				if (currentMeterCount < limitedCount) {
 					limitedCount = 0;
 				} else {
					cmdOperationUtil.cmdSendEvent2(eventAlertName, activatorType, activatorId, supplier.getId());
 				}
 			}
 		} catch (Exception e) {
 			log.error(e, e);
 		}

 		mav.addObject("checkResult", limitedCount);
 		return mav;
    }
}