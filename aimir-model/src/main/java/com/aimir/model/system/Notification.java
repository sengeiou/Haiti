/**
 * Notification.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * Notification.java Description 
 * 통보 프레임 워크 정보 관리 엔티티
 * (HEMS용 통보를 위한 엔티티 작성, 차후 통보 프레임 워크 설계시 재 고려 되어야 함)
 * 
 * Date          Version     Author   Description
 * 2011. 6. 13.   v1.0       eunmiae  초판 생성
 *
 */
@Entity
@Table(name = "NOTIFICATION")
public class Notification extends BaseObject{

	static final long serialVersionUID = 6056177359684341051L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="NOTIFICATION_SEQ")
    @SequenceGenerator(name="NOTIFICATION_SEQ", sequenceName="NOTIFICATION_SEQ", allocationSize=1)
	private Integer id;

	@Column(name="SMSYN", columnDefinition="INTEGER default 1")
	@ColumnInfo(name="SMS 통보 여부", descr="true: SMS 통보") 
	private Boolean smsYn;

	@Column(name="EMAILYN", columnDefinition="INTEGER default 1")
    @ColumnInfo(name="E-Mail 통보 여부", descr="true: E-Mail 통보") 
	private Boolean eMailYn;

	@Column(name="SMS_ADDRESS")
    @ColumnInfo(name="통보 주소", descr="통보 주소(핸드폰 번호)") 
	private String smsAddress;

	@Column(name="EMAIL_ADDRESS")
    @ColumnInfo(name="통보 주소", descr="통보 주소(Email주소)") 
	private String eMailAddress;

	@Column(name="PERIOD_1",  columnDefinition="INTEGER default 1")
    @ColumnInfo(name="통보 주기", descr="통보 주기(전월 에너지 절감 목표 실적") 
	private Boolean period_1;
	
	@Column(name="PERIOD_2",  columnDefinition="INTEGER default 1")
    @ColumnInfo(name="통보 주기", descr="통보 주기(설정 목표 ${conditionValue} 근접시 통보") 	
	private Boolean period_2;
	
	@Column(name="PERIOD_3",  columnDefinition="INTEGER default 1")
    @ColumnInfo(name="통보 주기", descr="통보 주기(설정 목표 초과시 통보") 	
	private Boolean period_3;
	
	@Column(name="PERIOD_4",  columnDefinition="INTEGER default 1")
    @ColumnInfo(name="통보 주기", descr="통보 주기(주1회 에너지 절감 목표 실적 통보") 	
	private Boolean period_4;
	
	@Column(name="PERIOD_5",  columnDefinition="INTEGER default 1")
    @ColumnInfo(name="통보 주기", descr="통보 주기(2주 1회 에너지 절감 목표 실적 통보") 	
	private Boolean period_5;

	@Column(name="CONDITION_VALUE")
    @ColumnInfo(name="통보 조건", descr="통보 조건") 	
	private Integer conditionValue;
	
//	@OneToOne
//	@JoinColumn(name="notificationTemplate_id")
//	private NotificationTemplate template;

	@Column(name="NOTIFICATION_NAME")
    @ColumnInfo(name="통보 명", descr="통보 명 또는 null") 
	private String name;
	// 통보 대상(to) 정보:NotificationGroup(통보대상 고정일 경우 지정,그룹일경우 그룹 아이디)
	// 통보자(from) 정보 :NotifiactionGroup

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getSmsYn() {
		return smsYn;
	}

	public void setSmsYn(Boolean smsYn) {
		this.smsYn = smsYn;
	}

	public Boolean geteMailYn() {
		return eMailYn;
	}

	public void seteMailYn(Boolean eMailYn) {
		this.eMailYn = eMailYn;
	}

	public String getSmsAddress() {
		return smsAddress;
	}

	public void setSmsAddress(String smsAddress) {
		this.smsAddress = smsAddress;
	}

	public String geteMailAddress() {
		return eMailAddress;
	}

	public void seteMailAddress(String eMailAddress) {
		this.eMailAddress = eMailAddress;
	}

//	public NotificationTemplate getTemplate() {
//		return template;
//	}
//
//	public void setTemplate(NotificationTemplate template) {
//		this.template = template;
//	}

	public Boolean getPeriod_1() {
		return period_1;
	}

	public void setPeriod_1(Boolean period_1) {
		this.period_1 = period_1;
	}

	public Boolean getPeriod_2() {
		return period_2;
	}

	public void setPeriod_2(Boolean period_2) {
		this.period_2 = period_2;
	}

	public Boolean getPeriod_3() {
		return period_3;
	}

	public void setPeriod_3(Boolean period_3) {
		this.period_3 = period_3;
	}

	public Boolean getPeriod_4() {
		return period_4;
	}

	public void setPeriod_4(Boolean period_4) {
		this.period_4 = period_4;
	}

	public Boolean getPeriod_5() {
		return period_5;
	}

	public void setPeriod_5(Boolean period_5) {
		this.period_5 = period_5;
	}

	public Integer getConditionValue() {
		return conditionValue;
	}

	public void setConditionValue(Integer conditionValue) {
		this.conditionValue = conditionValue;
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

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
}
