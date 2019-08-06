/**
 * CheckBalanceSettingWSModify.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.model.prepayment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * CheckBalanceSettingWSModify.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 22.  v1.0        문동규   잔액 체크 주기 설정 모델
 *
 */

@Entity
@Table(name = "CHECK_BALANCE_SETTING_WS")
public class CheckBalanceSettingWSModify extends BaseObject {

    private static final long serialVersionUID = -4131324217688205430L;
    
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_CHECK_BALANCE_SETTING_WS")
	@SequenceGenerator(name="SEQ_CHECK_BALANCE_SETTING_WS", sequenceName="SEQ_CHECK_BALANCE_SETTING_WS", allocationSize=1)
	private Integer id;
	
	@Column(name = "supplier_name", nullable=false)
	@ColumnInfo(name="Utility ID", descr="공급사 아이디")
	private String supplierName;

	@Column(name = "date_time", nullable=false)
	@ColumnInfo(name="Date & Time of request", descr="현재 날짜")
	private String dateTime;
	
    @Column(name = "contract_number", nullable=false)
    @ColumnInfo(name="Contract ID", descr="고객의 계약번호")
    private String contractNumber;

    @Column(name = "mds_id", nullable=false)
    @ColumnInfo(name="Meter Serial Number", descr="미터 시리얼 번호")
    private String mdsId;

    @Column(name="NOTIFICATION_PERIOD")
    @ColumnInfo(name="Notification Period", descr="통보 주기, 1:Daily, 2:Weekly")
    private Integer notificationPeriod;

    @Column(name="NOTIFICATION_INTERVAL")
    @ColumnInfo(name="Notification Interval", descr="통보 간격")
    private Integer notificationInterval;

    @Column(name="NOTIFICATION_TIME")
    @ColumnInfo(name="Notification Time", descr="통보 시간 , 시간 : 0, 1, 2, ,,,,, , 23")
    private Integer notificationTime;

    @Column(name="NOTIFICATION_WEEKLY_MON")
    @ColumnInfo(name="Notification Weekly Monday", descr="통보 주기가 주별일 경우, true : 월요일 통보")
    private Boolean notificationWeeklyMon;

    @Column(name="NOTIFICATION_WEEKLY_TUE")
    @ColumnInfo(name="Notification Weekly Tuesday", descr="통보 주기가 주별일 경우, true : 화요일 통보")
    private Boolean notificationWeeklyTue;

    @Column(name="NOTIFICATION_WEEKLY_WED")
    @ColumnInfo(name="Notification Weekly Wednesday", descr="통보 주기가 주별일 경우, true : 수요일 통보")
    private Boolean notificationWeeklyWed;

    @Column(name="NOTIFICATION_WEEKLY_THU")
    @ColumnInfo(name="Notification Weekly Thursday", descr="통보 주기가 주별일 경우, true : 목요일 통보")
    private Boolean notificationWeeklyThu;

    @Column(name="NOTIFICATION_WEEKLY_FRI")
    @ColumnInfo(name="Notification Weekly Friday", descr="통보 주기가 주별일 경우, true : 금요일 통보")
    private Boolean notificationWeeklyFri;

    @Column(name="NOTIFICATION_WEEKLY_SAT")
    @ColumnInfo(name="Notification Weekly Saturday", descr="통보 주기가 주별일 경우, true : 토요일 통보")
    private Boolean notificationWeeklySat;

    @Column(name="NOTIFICATION_WEEKLY_SUN")
    @ColumnInfo(name="Notification Weekly Sunday", descr="통보 주기가 주별일 경우, true : 일요일 통보")
    private Boolean notificationWeeklySun;

	@Column(name = "threshold")
	@ColumnInfo(name="Threshold (XX%)", descr="XX% 이상 소진 시 통보")
	private Integer threshold;

	@Column(name = "mobile_device_id")
	@ColumnInfo(name="Mobile Device ID", descr="장비 아이디")
	private String mobileDeviceId;

	@Column(name = "encryption_key", nullable=false)
	@ColumnInfo(name="Encryption Key", descr="암호화 시 인증 키")
	private String encryptionKey;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getMdsId() {
		return mdsId;
	}

	public void setMdsId(String mdsId) {
		this.mdsId = mdsId;
	}

    public Integer getNotificationPeriod() {
        return notificationPeriod;
    }

    public void setNotificationPeriod(Integer notificationPeriod) {
        this.notificationPeriod = notificationPeriod;
    }

    public Integer getNotificationInterval() {
        return notificationInterval;
    }

    public void setNotificationInterval(Integer notificationInterval) {
        this.notificationInterval = notificationInterval;
    }

    public Integer getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Integer notificationTime) {
        this.notificationTime = notificationTime;
    }

    public Boolean getNotificationWeeklyMon() {
        return notificationWeeklyMon;
    }

    public void setNotificationWeeklyMon(Boolean notificationWeeklyMon) {
        this.notificationWeeklyMon = notificationWeeklyMon;
    }

    public Boolean getNotificationWeeklyTue() {
        return notificationWeeklyTue;
    }

    public void setNotificationWeeklyTue(Boolean notificationWeeklyTue) {
        this.notificationWeeklyTue = notificationWeeklyTue;
    }

    public Boolean getNotificationWeeklyWed() {
        return notificationWeeklyWed;
    }

    public void setNotificationWeeklyWed(Boolean notificationWeeklyWed) {
        this.notificationWeeklyWed = notificationWeeklyWed;
    }

    public Boolean getNotificationWeeklyThu() {
        return notificationWeeklyThu;
    }

    public void setNotificationWeeklyThu(Boolean notificationWeeklyThu) {
        this.notificationWeeklyThu = notificationWeeklyThu;
    }

    public Boolean getNotificationWeeklyFri() {
        return notificationWeeklyFri;
    }

    public void setNotificationWeeklyFri(Boolean notificationWeeklyFri) {
        this.notificationWeeklyFri = notificationWeeklyFri;
    }

    public Boolean getNotificationWeeklySat() {
        return notificationWeeklySat;
    }

    public void setNotificationWeeklySat(Boolean notificationWeeklySat) {
        this.notificationWeeklySat = notificationWeeklySat;
    }

    public Boolean getNotificationWeeklySun() {
        return notificationWeeklySun;
    }

    public void setNotificationWeeklySun(Boolean notificationWeeklySun) {
        this.notificationWeeklySun = notificationWeeklySun;
    }

	public Integer getThreshold() {
		return threshold;
	}

	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

	public String getMobileDeviceId() {
	    return mobileDeviceId;
	}

	public void setMobileDeviceId(String mobileDeviceId) {
	    this.mobileDeviceId = mobileDeviceId;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	@Override
	public boolean equals(Object o) {
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return null;
	}
}
