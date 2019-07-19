/*
 * $Id: FaultMgrBean.java,v 1.11 2003/05/30 01:10:05 geryon Exp $
 *
 * 작성날짜 : 2003/05/23
 * 변경날짜 : 2003/10/13
 * 변 경 자 : 박종성
 * 변경내용 : id type이 Integer에서 String으로 바뀜.
 *
 * Copyright @ 2003 - 2004 Nuritelecom
 * All rights reserved.
 *
 */
package com.aimir.notification;

import java.io.Serializable;

/** Framework notification class.
 * Notification은 모두 JavaBean규약에 따라 정의된 클래스이다. Notification이 JMS 메시지로 변환될 때는 다음의 규칙을 따른다.
 *
 * <ul>
 * 	<li> Notification은 JMS ObjectMessage로 변환된다.
 * 	<li> Notification은 ObjectMessge의 body에 저장된다.
 *  <li> Notification의 property 중 primitive type(wrapper class 포함)은 메지시지의 property로도 저장되어 selector를 통한 필터링에 이용되도록 한다.
 * </ul>
 * <p>
 * Notification type은 Notification class를 root로 한 상속 트리에 따라
 * 클래스 이름을 "."으로 연결하여 구한다.
 *
 * @author <a href="mailto:jaehwang@nuritelecom.com">Jae-Hwang Kim</a>
 */
public abstract class Notification implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = -4985871031539083810L;

    /** Notification type
	 */
	final String getJavaClassName() {
		return this.getClass().getName();
	}

	/** Notification type을 리턴한다.
	 */
	public final String getType() {
		String type=null;

		Class<?> clazz = this.getClass();
		while(clazz!=null) {
			String className = clazz.getName();
			int idx = className.lastIndexOf(".");
			if(type!=null) {
				type = className.substring(idx+1) + "." + type;
			}
			else {
				type = className.substring(idx+1);
			}
			if(clazz==Notification.class) {
				break;
			}
			clazz=clazz.getSuperclass();
		}
		return type;
		//return "Notification";
	}

	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id=id;
	}

	private Long time;
	public Long getTime() {
		return time;
	}
	public void setTime(Long t) {
		time = t;
	}

	private String message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String m) {
		message = m;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Notification [id=");
		builder.append(id);
		builder.append(", \\n message=");
		builder.append(message);
		builder.append(", \\n time=");
		builder.append(time);
		builder.append("]");
		return builder.toString();
	}

}
