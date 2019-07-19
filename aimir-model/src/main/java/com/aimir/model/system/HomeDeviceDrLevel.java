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
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * HomeDeviceDrLevel.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 6. 13.   v1.0       eunmiae         
 *
 */
@Entity
@Table(name = "HOME_DEVICE_DRLEVEL")
public class HomeDeviceDrLevel extends BaseObject {

	static final long serialVersionUID = 592930089815132155L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="HOME_DEVICE_DRLEVEL_SEQ")
    @SequenceGenerator(name="HOME_DEVICE_DRLEVEL_SEQ", sequenceName="HOME_DEVICE_DRLEVEL_SEQ", allocationSize=1)
	private Integer id;
	
    @Column(name = "DRLEVEL", nullable=false)
    @ColumnInfo(name="DR Level", descr="DemandResponse Level(1,2,3,6)")    
    private Integer drLevel;
    
    @Column(name = "DRNAME", nullable=false)
    @ColumnInfo(name="DR Name", descr="NORMAL(On), MODERATE, HIGH, SPECIAL(Off)")    
    private String drName;  
    
    @ColumnInfo(name="categoryCode", descr="EndDevice 유형(스마트 콘센트, 일반 가전, 스마트 가전등)")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="category_id")
    @ReferencedBy(name="code")
    private Code categoryCode;

    @Column(name="category_id", nullable=true, updatable=false, insertable=false)
    private Integer categoryCodeId;
    
    @Column(name = "DRLEVEL_IMG_FILENAME")
    @ColumnInfo(name="DR Level Image Filename", descr="DR Level의 이미지 파일명")
    private String drLevelImgFileName;
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDrLevel() {
		return drLevel;
	}

	public void setDrLevel(Integer drLevel) {
		this.drLevel = drLevel;
	}

	public String getDrName() {
		return drName;
	}

	public void setDrName(String drName) {
		this.drName = drName;
	}

	@XmlTransient
	public Code getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(Code categoryCode) {
		this.categoryCode = categoryCode;
	}	

	public String getDrLevelImgFileName() {
		return drLevelImgFileName;
	}

	public void setDrLevelImgFileName(String drLevelImgFileName) {
		this.drLevelImgFileName = drLevelImgFileName;
	}

    public Integer getCategoryCodeId() {
        return categoryCodeId;
    }

    public void setCategoryCodeId(Integer categoryCodeId) {
        this.categoryCodeId = categoryCodeId;
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
