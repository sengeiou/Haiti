/**
 * OperatorContract.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * EnergySavingTarget.java Description 
 * 고객의 에너지원별(전기, 가스, 수도) 절감 목표를 설정한다.
 * 
 * Date          Version    Author   Description
 * 2011. 6. 7.   v1.0       eunmiae  초판 생성      
 *
 */
@Entity
@Table(name = "ENERGY_SAVING_TARGET")
public class EnergySavingTarget extends BaseObject {

	static final long serialVersionUID = -1741775424720596367L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ENERGY_SAVING_TARGET_SEQ")
    @SequenceGenerator(name="ENERGY_SAVING_TARGET_SEQ", sequenceName="ENERGY_SAVING_TARGET_SEQ", allocationSize=1)
	private Integer id;

    @ColumnInfo(name="계약정보", descr="계약 번호")    
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="operatorContract_id")
    private OperatorContract operatorContract;

    @Column(name="operatorContract_id", nullable=true, updatable=false, insertable=false)
    private Integer operatorContractId;

	@Column(name="SAVING_GOAL", length=20)
    @ColumnInfo(name="절감목표", descr="절감 목표를 금액으로 설정한다.")
    private Double savingTarget;

	@Column(name="CREATE_DATE", length=8, nullable=false)
    @ColumnInfo(name="에너지 절감 목표 생성일", descr="에너지 절감 목표 생성일")  
	private String createDate;

    @ColumnInfo(name="통보아이디", descr="통보 테이블의 ID")
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="notification_id")
    private Notification notification;
    
    @Column(name="notification_id", nullable=true, updatable=false, insertable=false)
    private Integer notificationId;

    @XmlTransient
    public OperatorContract getOperatorContract() {
		return operatorContract;
	}

	public void setOperatorContract(OperatorContract operatorContract) {
		this.operatorContract = operatorContract;
	}

	@XmlTransient
	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getSavingTarget() {
		return savingTarget;
	}

	public void setSavingTarget(Double savingTarget) {
		this.savingTarget = savingTarget;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public Integer getOperatorContractId() {
        return operatorContractId;
    }

    public void setOperatorContractId(Integer operatorContractId) {
        this.operatorContractId = operatorContractId;
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
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
