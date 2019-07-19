package com.aimir.notification;

/**
 * Alarm Attribute Class
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class AlarmAttr implements java.io.Serializable
{
	private static final long serialVersionUID = -7705492588132914090L;
	private String alarmId = null;
    private int    seq = 1;
    private String attrName = null;
    private String value = null;


    /**
     * constructor
     */
    public AlarmAttr()
    {
    }

    /**
     * constructor
     * @param seq - attribute sequence
     * @param attrName - attribute name
     * @param value - attribute value
     */
    public AlarmAttr(int seq,String attrName, String value)
    {
        this.seq = seq;
        this.attrName = attrName;
        this.value = value;
    }

    /**
     * constructor
     * @param alarmId - alarm Identifier
     * @param seq - attribute sequence
     * @param attrName - attribute name
     * @param value - attribute value
     */
    public AlarmAttr(String alarmId,int seq, String attrName, String value)
    {
        this.alarmId = alarmId;
        this.seq = seq;
        this.attrName = attrName;
        this.value = value;
    }

    /**
     * get Alarm ID
     * @return alarm ID
     */
    public String getAlarmId()
    {
        return this.alarmId;
    }
    /**
     * set Alarm ID
     * @param alarmId - alarm ID
     */
    public void setAlarmId(String alarmId)
    {
        this.alarmId = alarmId;
    }

    /**
     * get sequnece
     * @return sequence
     */
    public int getSeq()
    {
        return this.seq;
    }
    /**
     * set sequnece
     * @param seq - sequence
     */
    public void setSeq(int seq)
    {
        this.seq = seq;
    }

    /**
     * get attribute name
     * @return attirbute name
     */
    public String getAttrName()
    {
        return this.attrName;
    }
    /**
     * set attribute name
     * @param attrName - attirbute name
     */
    public void setAttrName(String attrName)
    {
        this.attrName = attrName;
    }

    /**
     * get attribute value
     * @return value
     */
    public String getValue()
    {
        return this.value;
    }
    /**
     * set attribute value
     * @param value - attribute value
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * get string
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("alarmId = ").append(alarmId).append(", ");
        sb.append("attrName = ").append(attrName).append(", ");
        sb.append("value = ").append(value).append("\n");

        return sb.toString();
    }
}
