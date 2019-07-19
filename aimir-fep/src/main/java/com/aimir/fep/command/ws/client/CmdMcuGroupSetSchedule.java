package com.aimir.fep.command.ws.client;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for CmdMcuSetSchedule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdMcuGroupSetSchedule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GroupId" type="{http://www.w3.org/2001/XMLSchema}Integer" minOccurs="0"/>
 *         &lt;element name="ScheduleList" type="{http://www.w3.org/2001/XMLSchema}List" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdMcuGroupSetSchedule", propOrder = {
    "groupId",
    "scheduleList"
})
public class CmdMcuGroupSetSchedule {

	@XmlElement(name = "GroupId")
	protected Integer groupId;
    @XmlElement(name = "ScheduleList")
    protected List<StringArray> scheduleList;
    
    
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	
	public List<StringArray> getScheduleList() {
        if (scheduleList == null) {
        	scheduleList = new ArrayList<StringArray>();
        }
        return this.scheduleList;
    }
	

}
