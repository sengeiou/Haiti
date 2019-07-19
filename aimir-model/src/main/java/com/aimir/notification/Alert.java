package com.aimir.notification;

import java.util.Vector;

/** Notifications are correlated to Alert.
 *
 * @author <a href="mailto:jaehwang@nuritelecom.com">Jae-Hwang Kim</a>
 */
public class Alert extends Notification {

	private static final long serialVersionUID = -415532776455234905L;

    private String faultClassName;
    public String getFaultClassName() {
        return faultClassName;
    }
    public void setFaultClassName(String faultClassName) {
        this.faultClassName = faultClassName;
    }

    private Integer status;
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }

    private Long closeTime;
    public Long getCloseTime() {
        return closeTime;
    }
    public void setCloseTime(Long closeTime) {
        this.closeTime = closeTime;
    }

    private Long duration;
    public Long getDuration() {
        return duration;
    }
    public void setDuration(Long d) {
        duration = d;
    }

    private Integer severity;
    public Integer getSeverity() {
        return severity;
    }
    public void setSeverity(Integer severity) {
        this.severity = severity;
    }
    
    private Integer otherSeverity;
    public Integer getOtherSeverity() {
        return otherSeverity;
    }
    public void setOtherSeverity(Integer otherSeverity) {
        this.otherSeverity = otherSeverity;
    }
    
    private Integer severityTrending;
    public Integer getSeverityTrending() {
        return severityTrending;
    }
    public void setSeverityTrending(Integer severityTrending) {
        this.severityTrending = severityTrending;
    }

    private Integer times;
    public Integer getTimes() {
        return times;
    }
    public void setTimes(Integer times) {
        this.times = times;
    }
    
    private String instanceKey;
    public String getInstanceKey() {
        return instanceKey;
    }
    public void setInstanceKey(String key) {
        instanceKey = key;
    }

    private String containment;
    public String getContainment() {
        return containment;
    }
    public void setContainment(String containment) {
        this.containment = containment;
    }
    
    private String locationCode=null;
    public void setLocationCode(String code) {
        locationCode = code;
    }
    public String getLocationCode() {
        return locationCode;
    }

    private String locationName=null;
    public void setLocationName(String name) {
        locationName = name;
    }
    public String getLocationName() {
        return locationName;
    }
    
    private String createOper=null;
    public void setCreateOper(String oper) {
        createOper = oper;
    }
    public String getCreateOper() {
        return createOper;
    }
    
    private String clearOper=null;
    public void setClearOper(String oper) {
        clearOper = oper;
    }
    public String getClearOper() {
        return clearOper;
    }
    
    private String alertingMONameAttribute;
    public String getAlertingMONameAttribute() {
        return alertingMONameAttribute;
    }
    public void setAlertingMONameAttribute(String alertingMONameAttribute) {
        this.alertingMONameAttribute = alertingMONameAttribute;
    }

    private Object alertingMO;
    public Object getAlertingMO() {
        return alertingMO;
    }
    public void setAlertingMO(Object mo) {
        alertingMO = mo;
    }

    private Vector<?> correlatedNotification;
    public Vector<?> getCorrelatedNotification() {
        return correlatedNotification;
    }
    public void setCorrelatedNotification(Vector<?> v) {
        correlatedNotification = v;
    }
    
    /**********************************************************************
        System information
    **********************************************************************/

    private String systemIpAddr;
    public String getSystemIpAddr() {
        return systemIpAddr;
    }
    public void setSystemIpAddr(String ip) {
        this.systemIpAddr = ip;
    }

    private String systemKey;
    public String getSystemKey() {
        return systemKey;
    }
    public void setSystemKey(String key) {
        this.systemKey = key;
    }

    private String systemName;
    public String getSystemName() {
        return systemName;
    }
    public void setSystemName(String name) {
        this.systemName = name;
    }
    
    private String trace;
    public String getTrace() {
        return this.trace;
    }
    public void setTrace(String trace) {
        this.trace = trace;
    }
}
