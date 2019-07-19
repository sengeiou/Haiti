package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.MeterEventKind;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <pre>
 * Meter Event 정의 클래스
 * 미터에서 저장하고 발생한 이벤트 로그에 대해 정의한 클래스
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="METEREVENT")
public class MeterEvent extends BaseObject implements JSONString {

	private static final long serialVersionUID = -6058752565126044939L;

	@Id 
	private String id;	//	ID(PK)	
	
	@Column(nullable=false)
    @ColumnInfo(name="이벤트 코드")
    private String value;//event code
    
    @Column(nullable=false)
    @ColumnInfo(name="미터 타입")
    @Enumerated(EnumType.STRING)
    private TargetClass meterType;
    
    @Column(nullable=false)
    @ColumnInfo(name="미터 제조사")
    private String vendor;
    
    @Column(nullable=false)
    @ColumnInfo(name="미터 모델")
    private String model;   
    
    @Column(nullable=false, name="KIND")
    @ColumnInfo(name="미터 이벤트 종류")
    @Enumerated(EnumType.STRING)
    private MeterEventKind kind;
    
    @Column(nullable=false)
    @ColumnInfo(name="이벤트 명(메시지)")
    private String name;
    
    @ColumnInfo(name="이벤트 설명")
    private String descr;
    
    @Column(name="TROUBLE_ADVICE", nullable=true)
    @ColumnInfo(name="대처방안")
    private String troubleAdvice;
    
    @Column(name="SUPPORT")
    @ColumnInfo(name="지원여부")
    private Boolean support;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TargetClass getMeterType() {
        return meterType;
    }

    public void setMeterType(String meterType) {
        this.meterType = TargetClass.valueOf(meterType);
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public MeterEventKind getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = MeterEventKind.valueOf(kind);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getTroubleAdvice() {
        return troubleAdvice;
    }

    public void setTroubleAdvice(String troubleAdvice) {
        this.troubleAdvice = troubleAdvice;
    }

    public Boolean getSupport() {
        return support;
    }

    public void setSupport(Boolean support) {
        this.support = support;
    }

    public String toJSONString() {
        JSONStringer js = null;
    
        js = new JSONStringer();
        js.object().key("id").value(this.id)
                   .key("name").value((this.name == null)? "null":this.name)
                   .key("value").value((this.value == null)? "null":this.value)
                   .key("meterType").value((this.meterType == null)? "null":this.meterType.name())
                   .key("vendor").value((this.vendor == null)? "null":this.vendor)
                   .key("model").value((this.model == null)? "null":this.model)
                   .key("kind").value((this.kind == null)? "null":this.kind.name())
                   .key("descr").value((this.descr == null)? "null":this.descr)
                   .key("troubleAdvice").value((this.troubleAdvice == null)? "null":this.troubleAdvice)
                   .key("support").value((this.support == null)? "null":this.support)
                   .endObject();

        return js.toString();
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