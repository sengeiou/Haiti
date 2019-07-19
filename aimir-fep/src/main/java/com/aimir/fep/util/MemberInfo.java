package com.aimir.fep.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.datatype.OID;

/**
 * MemberInfo
 * 
 * @author goodjob
 * <pre>
 * &lt;complexType name="memberInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cmd" type="{http://server.ws.command.fep.aimir.com/}oid" minOccurs="0"/>
 *         &lt;element name="member" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "memberInfo", propOrder = {
    "cmd",
    "member",
    "state"
})
public class MemberInfo implements java.io.Serializable
{
	private static final long serialVersionUID = -9166534457867987355L;
	private String member = null;
    private OID cmd = null;
    private boolean state;

    public MemberInfo() { }

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public OID getCmd() {
		return cmd;
	}

	public void setCmd(OID cmd) {
		this.cmd = cmd;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("member info[");
        sb.append("(member=").append(member).append("),");
        sb.append("(cmd=").append(cmd).append("),");
        sb.append("(state=").append(state).append(')');
        sb.append("]\n");

        return sb.toString();
    }
}
