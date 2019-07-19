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

import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;
import com.aimir.model.device.EndDevice;

/**
 * HomeGroupAssets.java Description 
 * 홈 그룹의 자산 정보 관리(자산 : 스마트 콘센트, 일반 가전등)
 * 
 * Date          Version     Author   Description
 * 2011. 5. 4.   v1.0       eunmiae   신규 작성      
 *
 */
@Entity
@Table(name = "HOME_GROUP_ASSETS")
public class HomeGroupAssets extends BaseObject {

	static final long serialVersionUID = -7750156238515804376L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HOME_GROUP_ASSETS_SEQ")
	@SequenceGenerator(name = "HOME_GROUP_ASSETS_SEQ", sequenceName = "HOME_GROUP_ASSETS_SEQ", allocationSize = 1)
	/* Primary Key */
    private Integer id;

	@Column(name = "CUSTOMER_NUMBER")
    @ColumnInfo(name="Home Group Key", descr="계약 번호") 
	private String homeGroupKey;

    @ColumnInfo(name="장비", descr="장비(스마트 콘센트, 가전)")
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ENDDEVICE_ID")
    @ReferencedBy(name="uuid")
    private EndDevice endDevice;
    
    @Column(name="ENDDEVICE_ID", nullable=true, updatable=false, insertable=false)
    private Integer endDeviceId;

	@Column(name = "WRITE_DATE", length=14)
    @ColumnInfo(name="회원정보 등록 날짜", descr="yyyymmddhhmmss")
	private String writeDate;

	@Column(name = "UPDATE_DATE", length=14)
    @ColumnInfo(name="회원정보 갱신 날짜", descr="yyyymmddhhmmss")
	private String updateDate;

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getHomeGroupKey() {
		return homeGroupKey;
	}

	public void setHomeGroupKey(String homeGroupKey) {
		this.homeGroupKey = homeGroupKey;
	}

	@XmlTransient
	public EndDevice getEndDevice() {
		return endDevice;
	}

	public void setEndDevice(EndDevice endDevice) {
		this.endDevice = endDevice;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getEndDeviceId() {
        return endDeviceId;
    }

    public void setEndDeviceId(Integer endDeviceId) {
        this.endDeviceId = endDeviceId;
    }

    /* 
	 * @see net.sf.json.JSONString#toJSONString()
	 */
	public String toJSONString() {
        JSONStringer jsonString = new JSONStringer();

        jsonString.object()
                          .key("id").value(this.id)
                          .key("homeGroupKey").value(this.homeGroupKey)
                          .key("endDevice").value(this.endDevice)
                          .key("writeDate").value(this.writeDate)
                          .key("updateDate").value(this.updateDate)
                  .endObject();

		return jsonString.toString();
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
