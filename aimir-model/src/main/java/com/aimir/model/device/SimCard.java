package com.aimir.model.device;

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
 * <p>Copyright NuriTelecom Co.Ltd. since 2017</p>
 * 
 * <p>SIM카드 정보</p>
 * 
 * @author SungHan Lim
 */
@Entity
@Table(name = "SIM_CARD")
public class SimCard extends BaseObject {
	private static final long serialVersionUID = -4482006277550671703L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SIM_CARD_SEQ")
    @SequenceGenerator(name="SIM_CARD_SEQ", sequenceName="SIM_CARD_SEQ", allocationSize=1)
	private Long id;
	
	@ColumnInfo(name = "SIM 카드 번호")
	@Column(name = "ICC_ID", length=30)
	private String iccId;
	
	@ColumnInfo(name = "모바일 전화번호, MSISDN(Mobile Station International ISDN Number)")
	@Column(name="PHONE_NUMBER", length=20)
    private String phoneNumber;
	
	@ColumnInfo(name = "국제 이동국 식별 번호(International Mobile Station Identity)")
    @Column(name="IMSI",length=15)
    private String imsi;
	
	
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
	
	public String getIccId() {
        return iccId;
    }

    public void setIccId(String iccId) {
        this.iccId = iccId;
    }
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
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
        return "";
    }
    
}
