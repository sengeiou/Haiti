package com.aimir.fep.protocol.fmp.frame.service.entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.datatype.*;
import com.aimir.fep.protocol.fmp.frame.service.Entry;


/**
 * @author kaze
 *
 * <pre>
 * &lt;complexType name="idrEntry">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}entry">
 *       &lt;sequence>
 *         &lt;element name="duration" type="{http://server.ws.command.fep.aimir.com/}int" minOccurs="0"/>
 *         &lt;element name="eventId" type="{http://server.ws.command.fep.aimir.com/}octet" minOccurs="0"/>
 *         &lt;element name="expectedUsage" type="{http://server.ws.command.fep.aimir.com/}uint" minOccurs="0"/>
 *         &lt;element name="groupName" type="{http://server.ws.command.fep.aimir.com/}octet" minOccurs="0"/>
 *         &lt;element name="incentive" type="{http://server.ws.command.fep.aimir.com/}octet" minOccurs="0"/>
 *         &lt;element name="reductionGoal" type="{http://server.ws.command.fep.aimir.com/}uint" minOccurs="0"/>
 *         &lt;element name="startTime" type="{http://server.ws.command.fep.aimir.com/}timestamp" minOccurs="0"/>
 *         &lt;element name="status" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "idrEntry", propOrder = {
    "duration",
    "eventId",
    "expectedUsage",
    "groupName",
    "incentive",
    "reductionGoal",
    "startTime",
    "status"
})
public class idrEntry extends Entry {
	private OCTET eventId = new OCTET();
	private OCTET groupName = new OCTET();
	private TIMESTAMP startTime = new TIMESTAMP();
	private INT duration = new INT();
	private UINT expectedUsage = new UINT();
	private UINT reductionGoal = new UINT();
	private OCTET incentive = new OCTET();
	private BYTE status = new BYTE();
	
	/**
	 * @return the eventId
	 */
	public OCTET getEventId() {
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(OCTET eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return the groupName
	 */
	public OCTET getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(OCTET groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the startTime
	 */
	public TIMESTAMP getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(TIMESTAMP startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the duration
	 */
	public INT getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(INT duration) {
		this.duration = duration;
	}

	/**
	 * @return the expectedUsage
	 */
	public UINT getExpectedUsage() {
		return expectedUsage;
	}

	/**
	 * @param expectedUsage the expectedUsage to set
	 */
	public void setExpectedUsage(UINT expectedUsage) {
		this.expectedUsage = expectedUsage;
	}

	/**
	 * @return the reductionGoal
	 */
	public UINT getReductionGoal() {
		return reductionGoal;
	}

	/**
	 * @param reductionGoal the reductionGoal to set
	 */
	public void setReductionGoal(UINT reductionGoal) {
		this.reductionGoal = reductionGoal;
	}

	/**
	 * @return the incentive
	 */
	public OCTET getIncentive() {
		return incentive;
	}

	/**
	 * @param incentive the incentive to set
	 */
	public void setIncentive(OCTET incentive) {
		this.incentive = incentive;
	}

	/**
	 * @return the status
	 */
	public BYTE getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(BYTE status) {
		this.status = status;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "idrEntry [duration=" + duration + ", eventId=" + eventId
				+ ", expectedUsage=" + expectedUsage + ", groupName="
				+ groupName + ", incentive=" + incentive + ", reductionGoal="
				+ reductionGoal + ", startTime=" + startTime + ", status="
				+ status + "]";
	}
	
}
