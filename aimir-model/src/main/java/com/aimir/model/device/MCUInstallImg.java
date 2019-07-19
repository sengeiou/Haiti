package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.model.BaseObject;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>DCU 현장 설치 이미지 경로를 저장하는 클래스</p>
 * 
 * @author goodjob
 *
 */
@Entity
public class MCUInstallImg extends BaseObject {

	private static final long serialVersionUID = 6049407631921994613L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MCU_INSTALLIMAGE_SEQ")
    @SequenceGenerator(name="MCU_INSTALLIMAGE_SEQ", sequenceName="MCU_INSTALLIMAGE_SEQ", allocationSize=1)
	private Long id;	
    private String orginalName;
    private String currentTimeMillisName;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MCU_ID")
    private MCU mcu;
	
	@Column(name="MCU_ID", nullable=true, updatable=false, insertable=false)
	private Integer mcuId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrginalName() {
		return orginalName;
	}

	public void setOrginalName(String orginalName) {
		this.orginalName = orginalName;
	}

	public String getCurrentTimeMillisName() {
		return currentTimeMillisName;
	}

	public void setCurrentTimeMillisName(String currentTimeMillisName) {
		this.currentTimeMillisName = currentTimeMillisName;
	}

	@XmlTransient
	public MCU getMcu() {
		return mcu;
	}

	public void setMcuId(MCU mcu) {
		this.mcu = mcu;
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
