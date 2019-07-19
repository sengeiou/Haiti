/**
 * NotificationTemplate.java Copyright NuriTelecom Limited 2011
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
 * NotificationTemplate.java Description 
 * 통보 템플릿 정보를 관리하는 엔티티
 * (HEMS용 통보를 위한 엔티티 작성, 차후 통보 프레임 워크 설계시 재 고려 되어야 함)
 * 
 * Date          Version     Author   Description
 * 2011. 6. 13.   v1.0       eunmiae  초판 생성       
 *
 */
@Entity
@Table(name = "NOTIFICATION_TEMPLATE")
public class NotificationTemplate extends BaseObject{

	static final long serialVersionUID = -3823458337330749780L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="NOTIFICATION_TEMPLATE_SEQ")
    @SequenceGenerator(name="NOTIFICATION_TEMPLATE_SEQ", sequenceName="NOTIFICATION_TEMPLATE_SEQ", allocationSize=1)
	private Integer id;

	@Column(name="NAME", nullable=false, unique=true)
    @ColumnInfo(name="템플릿 명", descr="예)TPL_EnergySavingTarget") 
	private String name;

	@Column(name="TITLE")
    @ColumnInfo(name="통보 타이틀", descr="통보 타이틀([AiMiR Notification] ${title}") 	
	private String title;
	
	@Column(name="BODY")
    @ColumnInfo(name="통보 내용", descr="통보 내용(${body})") 	
	private String body;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
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
