package com.aimir.model.system;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.AuditAction;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 *
 * 변경 이력 정보 클래스
 * Entity, Table 타입의 클래스 선언에서 IAuditable 을 선언하게 되면 해당 클래스 정보가 변경 시
 * AuditLog에 변경된 정보가 추적되어 이력으로 남겨진다.
 *
 * @author 박종성(elevas)
 *
 */
@Entity
@Table(name="AuditLog")
public class AuditLog extends BaseObject implements JSONString {

	private static final long serialVersionUID = 1509560962134552502L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AUDITLOG_SEQ")
	@SequenceGenerator(name="AUDITLOG_SEQ", sequenceName="AUDITLOG_SEQ", allocationSize=1)
    @ColumnInfo(name="PK", descr="PK")
    private Long id;     //id

    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @Temporal(TemporalType.TIMESTAMP)
    @ColumnInfo(name="로그생성일")
    private Date createdDate;

    @ColumnInfo(name="엔티티 아이디")
    private long entityId;

    @ColumnInfo(name="엔티티 클래스 명")
    private String entityName;

    @ColumnInfo(name="엔티티 속성 명")
    private String propertyName;

    @ColumnInfo(name="이전 값")
    @Column(length=1024)
    private String previousState;

    @ColumnInfo(name="현재 값")
    @Column(length=1024)
    private String currentState;

    @ColumnInfo(name="인스턴스 명, 미터는 계량기번호. 모뎀은 시리얼번호 등")
    @Column(length=1024)
    private String instanceName;

    @ColumnInfo(name="로그인 아이디, FEP, SCHEDULER")
    @Column(length=1024)
    private String loginId;

    public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPreviousState() {
        return previousState;
    }

    public void setPreviousState(String previousState) {
        this.previousState = previousState;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    @Override
    public String toJSONString() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
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
