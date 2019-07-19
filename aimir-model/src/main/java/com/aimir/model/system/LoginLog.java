package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.constants.CommonConstants.LoginStatus;
import com.aimir.model.BaseObject;

/**
 * LoginLog
 *  로그인 이력 정보 
 *  @author goodjob
 */
@Entity
@Table(name="LOGIN_LOG")
public class LoginLog extends BaseObject{

	private static final long serialVersionUID = -244292093243821790L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="LOGINLOG_SEQ")
	@SequenceGenerator(name="LOGINLOG_SEQ", sequenceName="LOGINLOG_SEQ", allocationSize=1)
	private Long id;
	
	@Column(name="SESSION_ID")
	private String sessionId;
		
	@Column(name="IP_ADDR", length=25) 
	private String ipAddr;
	
	@Column(name="LOGIN_DATE", length=14)
	private String loginDate;
	
	@Column(name="LOGOUT_DATE", length=14)
	private String logoutDate;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operator_id")//사용자에 없는 로그인 아이디로 등록했을 경우 기록을 위해 nullable true를 허용
	private Operator operator;
	
	@Column(name="operator_id", nullable=true, updatable=false, insertable=false)
	private Integer operatorId;
	
	@Column(name="login_id", nullable=false)
	private String loginId; //사용자에 없는 로그인 아이디로 등록했을 경우 기록을 위해

	
	@Column(name = "status")
	private LoginStatus status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlTransient
	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}	
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(String loginDate) {
		this.loginDate = loginDate;
	}

	public String getLogoutDate() {
		return logoutDate;
	}

	public void setLogoutDate(String logoutDate) {
		this.logoutDate = logoutDate;
	}
	
	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public LoginStatus getStatus() {
		return status;
	}

	public void setStatus(String statusName) {
		
		if(statusName != null && !"".equals(statusName)){
			this.status = LoginStatus.valueOf(statusName);
		}

	}

	public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String toString()
	{
	    return "LoginLog ";
	}

    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }
}
