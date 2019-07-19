package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.device.MCU;
/**
 * <p>홈 그룹 정보 </p>
 * 댁내의 가전기기, IHD, Appliance Control 장치들을 맵핑 하기위한 그룹 클래스 <br>
 * 구분은 계약번호(contractNumber)가 그룹의 중요 키가 된다.  <br>
 * 멤버에는 가전기기(EndDevice), IHD, Appliance Control Device 가 될수 있다.  <br>
 * 
 * @author 김재식(kaze)
 *
 */

@Entity
@DiscriminatorValue("HomeGroup")
public class HomeGroup extends AimirGroup{
	private static final long serialVersionUID = -1539840084122347436L;

	@ColumnInfo(name="MCU 아이디", descr="MCU 테이블의 ID 혹은  NULL")
	@ManyToOne(fetch=FetchType.LAZY)    
    @JoinColumn(name="HomeGroup_MCU_ID")
    @ReferencedBy(name="sysID")
	private MCU homeGroupMcu;
	
	@Column(name="HomeGroup_MCU_ID", nullable=true, updatable=false, insertable=false)
	private Integer homeGroupMcuId;
	
	@Column(name="GROUP_KEY")
	@ColumnInfo(descr="MCU에 home group 추가 후 리턴해 주는 그룹키")
	private int groupKey ;

	/**
	 * @return the homeGroupMCU
	 */
	@XmlTransient
	public MCU getHomeGroupMcu() {
		return homeGroupMcu;
	}

	/**
	 * @param homeGroupMcu the homeGroupMCU to set
	 */
	public void setHomeGroupMcu(MCU homeGroupMcu) {
		this.homeGroupMcu = homeGroupMcu;
	}

	/**
	 * @return the groupKey
	 */
	public int getGroupKey() {
		return groupKey;
	}

	/**
	 * @param groupKey the groupKey to set
	 */
	public void setGroupKey(int groupKey) {
		this.groupKey = groupKey;
	}

    public Integer getHomeGroupMcuId() {
        return homeGroupMcuId;
    }

    public void setHomeGroupMcuId(Integer homeGroupMcuId) {
        this.homeGroupMcuId = homeGroupMcuId;
    }
}
	