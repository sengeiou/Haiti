package com.aimir.mars.integration.service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.aimir.util.StringUtil;

@WebService(serviceName = "ServiceConfigure")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@Service(value = "serviceConfigure")
public class ServiceConfigure {
    protected static Log log = LogFactory.getLog(ServiceConfigure.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Resource
    private WebServiceContext wsContext;

    @WebMethod(operationName = "getEventDeliveryFilter")
    public @WebResult(name = "getEventDeliveryFilterResult") Map<String, String> getEventDeliveryFilter() {
        Map<String, String> rtnMap = new HashMap<String, String>();

        List<Map<String, Object>> eventDeliveryFilter = jdbcTemplate
                .queryForList(
                        "select attributename, attributevalue from ip_ev_option where codetype=?", "EV");
        for (Map<String, Object> row : eventDeliveryFilter) {
            rtnMap.put(row.get("ATTRIBUTENAME").toString(),
                    row.get("ATTRIBUTEVALUE").toString().toUpperCase());
        }

        return rtnMap;
    }

    @WebMethod(operationName = "getEventDevliery")
    public @WebResult(name = "getEventDevlieryResult") Map<String, String> getEventDevliery() {
        Map<String, String> rtnMap = new HashMap<String, String>();

        List<Map<String, Object>> ipEvOption = jdbcTemplate
                .queryForList(
                        "select attributename, attributevalue from ip_ev_option where codetype=?", "OP");
        for (Map<String, Object> row : ipEvOption) {
            rtnMap.put(row.get("ATTRIBUTENAME").toString(),
                    row.get("ATTRIBUTEVALUE").toString().toUpperCase());
        }
        
        List<Map<String, Object>> triggerStatus = jdbcTemplate
                .queryForList(
                        "select trigger_name,status from user_triggers where trigger_name in ('IP_EVENTALERTLOG', 'IP_METEREVENT_LOG')");
        for (Map<String, Object> row : triggerStatus) {
            String tempTriggerName = row.get("TRIGGER_NAME").toString();
            String tempTriggerStatus = row.get("STATUS").toString();
            if(tempTriggerName.equals("IP_EVENTALERTLOG")) {
                if(tempTriggerStatus.equals("ENABLED")) {
                    rtnMap.put("CNF_TRIGGER_EVENTALERTLOG", "TRUE");
                } else {
                    rtnMap.put("CNF_TRIGGER_EVENTALERTLOG", "FALSE");
                }
            } else if(tempTriggerName.equals("IP_METEREVENT_LOG")) {
                if(tempTriggerStatus.equals("ENABLED")) {
                    rtnMap.put("CNF_TRIGGER_METEREVENTLOG", "TRUE");
                } else {
                    rtnMap.put("CNF_TRIGGER_METEREVENTLOG", "FALSE");
                }
            }
        }
        return rtnMap;
    }

    @WebMethod(operationName = "getMeterDataDevliery")
    public @WebResult(name = "getMeterDataDevlieryResult") Map<String, String> getMeterDataDevliery() {
        Map<String, String> rtnMap = new HashMap<String, String>();

        List<Map<String, Object>> ipEvOption = jdbcTemplate
                .queryForList(
                        "select attributename, attributevalue from ip_mv_option where codetype=?", "OP");
        for (Map<String, Object> row : ipEvOption) {
            rtnMap.put(row.get("ATTRIBUTENAME").toString(),
                    row.get("ATTRIBUTEVALUE").toString().toUpperCase());
        }

        List<Map<String, Object>> triggerStatus = jdbcTemplate
                .queryForList(
                        "select trigger_name,status from user_triggers where trigger_name in ('IP_LP_EM', 'IP_LP_WM', 'IP_LP_GM', 'IP_LP_HM', 'IP_POWER_QUALITY')");
        for (Map<String, Object> row : triggerStatus) {
            String tempTriggerName = row.get("TRIGGER_NAME").toString();
            String tempTriggerStatus = row.get("STATUS").toString();
            if(tempTriggerName.equals("IP_LP_EM")) {
                if(tempTriggerStatus.equals("ENABLED")) {
                    rtnMap.put("CNF_TRIGGER_LP_EM", "TRUE");
                } else {
                    rtnMap.put("CNF_TRIGGER_LP_EM", "FALSE");
                }
            } else if(tempTriggerName.equals("IP_LP_WM")) {
                if(tempTriggerStatus.equals("ENABLED")) {
                    rtnMap.put("CNF_TRIGGER_LP_MW", "TRUE");
                } else {
                    rtnMap.put("CNF_TRIGGER_LP_WM", "FALSE");
                }
            } else if(tempTriggerName.equals("IP_LP_GM")) {
                if(tempTriggerStatus.equals("ENABLED")) {
                    rtnMap.put("CNF_TRIGGER_LP_GM", "TRUE");
                } else {
                    rtnMap.put("CNF_TRIGGER_LP_GM", "FALSE");
                }
            } else if(tempTriggerName.equals("IP_LP_HM")) {
                if(tempTriggerStatus.equals("ENABLED")) {
                    rtnMap.put("CNF_TRIGGER_LP_HM", "TRUE");
                } else {
                    rtnMap.put("CNF_TRIGGER_LP_HM", "FALSE");
                }
            } else if(tempTriggerName.equals("IP_POWER_QUALITY")) {
                if(tempTriggerStatus.equals("ENABLED")) {
                    rtnMap.put("CNF_TRIGGER_POWER_QUALITY", "TRUE");
                } else {
                    rtnMap.put("CNF_TRIGGER_POWER_QUALITY", "FALSE");
                }
            }
        }
        return rtnMap;
    }

    @WebMethod(operationName = "setEventDeliveryFilter")
    public void setEventDeliveryFilter(
            @WebParam(name = "setEventDeliveryFilterRequst") Map<String, String> setEventDeliveryFilterRequst) {
        Iterator<Entry<String, String>> iterEntry = setEventDeliveryFilterRequst
                .entrySet().iterator();
        jdbcTemplate.batchUpdate(
                "update ip_ev_option set attributevalue=? where codetype=? and attributename=?",
                new BatchPreparedStatementSetter() {

                    @Override
                    public int getBatchSize() {
                        return setEventDeliveryFilterRequst.size();
                    }

                    @Override
                    public void setValues(PreparedStatement ps, int i)
                            throws SQLException {

                        Entry<String, String> entry = (Entry<String, String>) iterEntry
                                .next();
                        ps.setString(1, entry.getValue().toUpperCase());
                        ps.setString(2, "EV");
                        ps.setString(3, entry.getKey());
                    }
                });
    }

    @WebMethod(operationName = "setEventDevliery")
    public void setEventDevliery(
            @WebParam(name = "setEventDevlieryRequest") Map<String, String> setEventDevlieryRequest) {

        if (setEventDevlieryRequest != null
                && setEventDevlieryRequest.size() > 0) {
            for (Entry<String, String> row : setEventDevlieryRequest
                    .entrySet()) {
                String key = row.getKey();
                String value = row.getValue();
                if (key != null
                        && key.toUpperCase().equals("CNF_DELIVERY")) {
                    if(value != null && (value.toUpperCase().equals("TRUE") || value.toUpperCase().equals("FALSE"))) {
                        jdbcTemplate.update(
                                "update ip_ev_option set attributevalue=? where codetype=? and attributename=?",
                                value.toUpperCase(), "OP", "CNF_DELIVERY");
                    }
                } else if (row.getKey() != null && row.getKey().toUpperCase()
                        .equals("CNF_BATCH_MAXSIZE")) {
                    if(StringUtil.isNumeric(value)) {
                        jdbcTemplate.update(
                                "update ip_ev_option set attributevalue=? where codetype=? and attributename=?",
                                value, "OP", "CNF_BATCH_MAXSIZE");
                    }
                } else if (row.getKey() != null && row.getKey().toUpperCase()
                        .equals("CNF_TRIGGER_EVENTALERTLOG")) {
                    if(value != null && value.toUpperCase().equals("TRUE")) {
                        jdbcTemplate.execute("alter trigger ip_eventalertlog enable");
                    } else if(value != null && value.toUpperCase().equals("FALSE")) {
                        jdbcTemplate.execute("alter trigger ip_eventalertlog disable");
                    }
                } else if (row.getKey() != null && row.getKey().toUpperCase()
                        .equals("CNF_TRIGGER_METEREVENTLOG")) {
                    if(value != null && value.toUpperCase().equals("TRUE")) {
                        jdbcTemplate.execute("alter trigger ip_meterevent_log enable");
                    } else if(value != null && value.toUpperCase().equals("FALSE")) {
                        jdbcTemplate.execute("alter trigger ip_meterevent_log disable");
                    }
                }
            }
        }
    }

    @WebMethod(operationName = "setMeterDataDevliery")
    public void setMeterDataDevliery(
            @WebParam(name = "setMeterDataDevlieryRequest") Map<String, String> setMeterDataDevlieryRequest) {

        if (setMeterDataDevlieryRequest != null
                && setMeterDataDevlieryRequest.size() > 0) {
            for (Entry<String, String> row : setMeterDataDevlieryRequest
                    .entrySet()) {
                String key = row.getKey();
                String value = row.getValue();
                if (key != null && (key.toUpperCase().startsWith("CNF_DELIVERY_")
                        || key.toUpperCase()
                                .equals("CNF_VALIDATION_SUPPORT"))) {
                    if (value != null && (value.toUpperCase().equals("TRUE")
                            || value.toUpperCase().equals("FALSE"))) {
                        jdbcTemplate.update(
                                "update ip_mv_option set attributevalue=? where codetype=? and attributename=?",
                                value.toUpperCase(), "OP", key.toUpperCase());
                    }
                } else if (row.getKey() != null && (row.getKey().toUpperCase()
                        .equals("CNF_BATCH_MAXSIZE")
                        || row.getKey().toUpperCase().equals("CNF_OLDMV_VDAYS")
                        || row.getKey().toUpperCase()
                                .equals("CNF_VAL_MAXDIGIT_EM_VALUE")
                        || row.getKey().toUpperCase()
                                .equals("CNF_VAL_MAXDIGIT_WM_VALUE")
                        || row.getKey().toUpperCase()
                                .equals("CNF_VAL_MAXDIGIT_GM_VALUE")
                        || row.getKey().toUpperCase()
                                .equals("CNF_VAL_MAXDIGIT_HM_VALUE"))) {
                    if (StringUtil.isNumeric(value)) {
                        jdbcTemplate.update(
                                "update ip_mv_option set attributevalue=? where codetype=? and attributename=?",
                                value, "OP", row.getKey().toUpperCase());
                    }
                } else if (row.getKey() != null && row.getKey().toUpperCase()
                        .equals("CNF_TRIGGER_LP_EM")) {
                    if(value != null && value.toUpperCase().equals("TRUE")) {
                        jdbcTemplate.execute("alter trigger ip_lp_em enable");
                    } else if(value != null && value.toUpperCase().equals("FALSE")) {
                        jdbcTemplate.execute("alter trigger ip_lp_em disable");
                    }
                } else if (row.getKey() != null && row.getKey().toUpperCase()
                        .equals("CNF_TRIGGER_LP_WM")) {
                    if(value != null && value.toUpperCase().equals("TRUE")) {
                        jdbcTemplate.execute("alter trigger ip_lp_wm enable");
                    } else if(value != null && value.toUpperCase().equals("FALSE")) {
                        jdbcTemplate.execute("alter trigger ip_lp_wm disable");
                    }
                } else if (row.getKey() != null && row.getKey().toUpperCase()
                        .equals("CNF_TRIGGER_LP_GM")) {
                    if(value != null && value.toUpperCase().equals("TRUE")) {
                        jdbcTemplate.execute("alter trigger ip_lp_gm enable");
                    } else if(value != null && value.toUpperCase().equals("FALSE")) {
                        jdbcTemplate.execute("alter trigger ip_lp_gm disable");
                    }
                } else if (row.getKey() != null && row.getKey().toUpperCase()
                        .equals("CNF_TRIGGER_LP_HM")) {
                    if(value != null && value.toUpperCase().equals("TRUE")) {
                        jdbcTemplate.execute("alter trigger ip_lp_hm enable");
                    } else if(value != null && value.toUpperCase().equals("FALSE")) {
                        jdbcTemplate.execute("alter trigger ip_lp_hm disable");
                    }
                } else if (row.getKey() != null && row.getKey().toUpperCase()
                        .equals("CNF_TRIGGER_POWER_QUALITY")) {
                    if(value != null && value.toUpperCase().equals("TRUE")) {
                        jdbcTemplate.execute("alter trigger ip_power_quality enable");
                    } else if(value != null && value.toUpperCase().equals("FALSE")) {
                        jdbcTemplate.execute("alter trigger ip_power_quality disable");
                    }
                }
            }
        }
    }
}
