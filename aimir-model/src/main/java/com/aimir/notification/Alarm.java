package com.aimir.notification;

import java.util.Hashtable;

/**
 * Alarm Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class Alarm extends Trap
{
	private static final long serialVersionUID = 3108992183393869444L;
	private String instanceKey = null;
    private String auId = null;
    private String sourceType = null;
    private String eventClassName = null;
    private String faultClassName = null;
    private String alertId = null;
    private Integer status = null;
    private Integer times = null;
    private Long    lastTime = null;
    private String  systemKey = null;
    private String  systemName = null;
    private Boolean  cleared = new Boolean(true);
    private Hashtable<String, AlarmAttr> alarmAttrs = new Hashtable<String, AlarmAttr>();
    private Object alarmMO = null;

    /**
     * get instance key
     * @return isntanceKey - mi instance name related alarm source
     */
    public String getInstanceKey()
    {
        return this.instanceKey;
    }
    /**
     * set instance key
     * @param isntanceKey - mi instance name related alarm source
     */
    public void setInstanceKey(String instanceKey)
    {
        this.instanceKey = instanceKey;
    }

    /**
     * get au id
     * @return auId - mcu id
     */
    public String getAuId()
    {
        return this.auId;
    }
    /**
     * set mcu id
     * @param auId - au id
     */
    public void setAuId(String auId)
    {
        this.auId = auId;
    }

    /**
     * get source type
     * @return sourceType - source type
     */
    public String getSourceType()
    {
        return this.sourceType;
    }
    /**
     * set source type
     * @param sourceType - source type
     */
    public void setSourceType(String sourceType)
    {
        this.sourceType = sourceType;
    }

    /**
     * get event classification name
     * @return event classification name
     */
    public String getEventClassName()
    {
        return this.eventClassName;
    }
    /**
     * set event classification name
     * @param eventClassName - event classification name
     */
    public void setEventClassName(String eventClassName)
    {
        this.eventClassName = eventClassName;
    }
    
    /**
     * get fault classification name
     * @return fault classification name
     */
    public String getFaultClassName()
    {
        return this.faultClassName;
    }
    /**
     * set fault classification name
     * @param faultClassName - fault classification name
     */
    public void setFaultClassName(String faultClassName)
    {
        this.faultClassName = faultClassName;
    }
    
    /**
     * get alert ID
     * @return alert Id ocuured by alarm
     */
    public String getAlertId()
    {
        return this.alertId;
    }
    /**
     * set alert ID
     * @param alaertId - alert Id ocuured by alarm
     */
    public void setAlertId(String alertId)
    {
        this.alertId = alertId;
    }

    /**
     * get alarm status
     * @return alarm status
     */
    public Integer getStatus()
    {
        return this.status;
    }
    /**
     * set alarm status
     * @param status - alarm status
     */
    public void setStatus(Integer status)
    {
        this.status = status;
    }

    /**
     * get times
     * @return duplicated alarm times
     */
    public Integer getTimes()
    {
        return this.times;
    }
    /**
     * set times
     * @param times - duplicated alarm times
     */
    public void setTimes(Integer times)
    {
        this.times = times;
    }

    /**
     * get alarm occured time
     * @return alarm occurred time
     */
    public Long getLastTime()
    {
        return this.lastTime;
    }
    /**
     * set alarm occured time
     * @param lastTime - alarm occurred time
     */
    public void setLastTime(Long lastTime)
    {
        this.lastTime = lastTime;
    }

    /**
     * get system key
     * @return system instance key related alarm
     */
    public String getSystemKey()
    {
        return this.systemKey;
    }
    /**
     * set system key
     * @param systemKey - system instance key related alarm
     */
    public void setSystemKey(String systemKey)
    {
        this.systemKey = systemKey;
    }

    /**
     * get system name
     * @return system instance name related alarm
     */
    public String getSystemName()
    {
        return this.systemName;
    }
    /**
     * set system name
     * @param systemName - system instance name related alarm
     */
    public void setSystemName(String systemName)
    {
        this.systemName = systemName;
    }

    /**
     * get cleared
     * @return cleared status
     */
    public Boolean getCleared()
    {
        return this.cleared;
    }
    /**
     * set cleared
     * @param cleared - cleared status
     */
    public void setCleared(Boolean cleared)
    {
        this.cleared = cleared;
    }

    /**
     * append alarm attribute
     * @param alarmAttr -  alarm attribute(param meter)
     */
    public void append(AlarmAttr alarmAttr)
    {
        this.alarmAttrs.put(alarmAttr.getAttrName(),alarmAttr);
    }

    /**
     * get alarm attribute list
     * @return alarm attribute list
     */
    public AlarmAttr[] getAlarmAttrs()
    {
        return (AlarmAttr[])alarmAttrs.values().toArray(
                new AlarmAttr[0]);
    }

    /**
     * get alarm attribute value
     * @param attrName -  attribute name
     * @return attribute value
     */
    public String getAlarmAttrValue(String attrName)
    {
        if(alarmAttrs.containsKey(attrName))
            return ((AlarmAttr)alarmAttrs.get(attrName)).getValue();
        return "";
    }

    /**
     * check whether exist specified attribute or not
     * @param attrName - attribute name
     * @return 
     */
    public boolean isHasAttribute(String attrName)
    {
        return alarmAttrs.containsKey(attrName);
    }

    /**
     * get MOINSTANCE related alarm
     * @return instance
     */
    public Object getAlarmMO()
    {
        return this.alarmMO;
    }
    /**
     * set MOINSTANCE related alarm
     * @param alarmMO - MOINSTANCE related alarm
     */
    public void  setAlarmMO(Object alarmMO)
    {
        this.alarmMO = alarmMO;
    }

    /**
     * get string
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("id=[").append(getId()).append("],");
        sb.append("instanceKey=[").append(instanceKey).append("],");
        sb.append("auId=[").append(auId).append("],");
        sb.append("eventClassName=[").append(eventClassName).append("],");
        sb.append("faultClassName=[").append(faultClassName).append("],");
        sb.append("alertId=[").append(alertId).append("],");
        sb.append("status=[").append(status).append("],");
        sb.append("times=[").append(times).append("],");
        sb.append("time=[").append(getTime()).append("],");
        sb.append("systemKey=[").append(systemKey).append("],");
        sb.append("systemName=[").append(systemName).append("],");
        sb.append("message=[").append(getMessage()).append("],");
        sb.append("cleared=[").append(cleared).append("]\n");

        AlarmAttr[] attrs = getAlarmAttrs(); 
        for(int i = 0 ; i < attrs.length ; i++)
        {
            sb.append(attrs[i].toString());
        }

        return sb.toString();
    }
}
