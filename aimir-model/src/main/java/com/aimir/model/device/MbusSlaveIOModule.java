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
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

import com.aimir.annotation.ColumnInfo;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 *
 * <pre>
 * MBUS_SALVE_IO_MODULE
 * </pre>
 *
 * @author
 *
 */
@Entity
//@Indexes({
//	@Index(name="IDX_MBUSSLAVEIOMODULE_01", columnNames={"MDS_ID"}),
//	@Index(name="IDX_MBUSSLAVEIOMODULE_02", columnNames={"METER_ID"}),
//})
@Table(name="MBUS_SLAVE_IO_MODULE")
public class MbusSlaveIOModule extends BaseObject implements JSONString, IAuditable {

	private static final long serialVersionUID = 2302164098551925453L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_MBUSSLAVEIOMODULE")
    @SequenceGenerator(name="SEQ_MBUSSLAVEIOMODULE", sequenceName="SEQ_MBUSSLAVEIOMODULE", allocationSize=1)
	private Integer id;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="meter_id")
    private Meter meter;
	
	@Column(name="meter_id", nullable=true, unique=true, updatable=false, insertable=false)
	private Integer meterId;
    

	@Column(name="mds_id",length=255, nullable=false, unique=true)
	@ColumnInfo(name="", descr="")
	private String mdsId;

	@Column(name="degital_1")
	@ColumnInfo(descr="")
	private Boolean degital1;

	@Column(name="degital_2")
	@ColumnInfo(descr="")
	private Boolean degital2;

	@Column(name="degital_3")
	@ColumnInfo(descr="")
	private Boolean degital3;

	@Column(name="degital_4")
	@ColumnInfo(descr="")
	private Boolean degital4;

	@Column(name="degital_5")
	@ColumnInfo(descr="")
	private Boolean degital5;

	@Column(name="degital_6")
	@ColumnInfo(descr="")
	private Boolean degital6;

	@Column(name="degital_7")
	@ColumnInfo(descr="")
	private Boolean degital7;

	@Column(name="degital_8")
	@ColumnInfo(descr="")
	private Boolean degital8;

	@Column(name="degital_current")
	@ColumnInfo(descr="")
	private Integer degitalCurrent;
	
	public Integer getDegitalCurrent() {
		return degitalCurrent;
	}

	public void setDegitalCurrent(Integer degitalCurrent) {
		
		this.degitalCurrent = degitalCurrent;
		if ( (((int)degitalCurrent >> 7) & 0x01 ) > 0 ) this.degital8 = true;
		if ( (((int)degitalCurrent >> 6) & 0x01 ) > 0 ) this.degital7 = true;
		if ( (((int)degitalCurrent >> 5) & 0x01 ) > 0 ) this.degital6 = true;
		if ( (((int)degitalCurrent >> 4) & 0x01 ) > 0 ) this.degital5 = true;
		if ( (((int)degitalCurrent >> 3) & 0x01 ) > 0 ) this.degital4 = true;
		if ( (((int)degitalCurrent >> 2) & 0x01 ) > 0 ) this.degital3 = true;
		if ( (((int)degitalCurrent >> 1) & 0x01 ) > 0 ) this.degital2 = true;
		if ( (((int)degitalCurrent ) & 0x01 ) > 0 ) this.degital1 = true;
		
	}

	@ColumnInfo(name="analog_current", descr="analog_current")
	@Column(name="analog_current")
	private Double analogCurrent;



	@Column(name="analog_current_cnv")
	@ColumnInfo(name="analog_current_cnv", descr="analog_current_cnv")
	private Double analogCurrentCnv;

	@Column(name="analog_voltage")
	@ColumnInfo(name="analog_voltage", descr="analog_voltage")
	private Double analogVoltage;

	@Column(name="analog_voltage_cnv")
	@ColumnInfo(name="analog_voltage_cnv", descr="analog_voltage_cnv")
	private Double analogVoltageCnv;

	@Column(name="scale_current")
	@ColumnInfo(name="scale_current", descr="scale_current")
	private Integer scaleCurrent;

	@Column(name="scale_voltage")
	@ColumnInfo(name="scale_voltage", descr="scale_voltage")
	private Integer scaleVoltage;

	@ColumnInfo(name = "", descr = "")
	@Column(name = "INSTALL_DATE", length = 14)
	private String installDate;

	@ColumnInfo(name = "", descr = "")
	@Column(name = "LAST_UPDATE_TIME", length = 14)
	private String lastUpdateTime;

	@Override
	public String getInstanceName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toJSONString() {
		JSONStringer js = null;

		try {
			js = new JSONStringer();
			js.object().key("meterId").value((this.meterId == null)? "":this.meterId)
			.key("mdsId").value((this.mdsId == null)? "":this.mdsId)
			.key("degital1").value((this.degital1 == null)? "":this.degital1)
			.key("degital2").value((this.degital1 == null)? "":this.degital2)
			.key("degital3").value((this.degital1 == null)? "":this.degital3)
			.key("degital4").value((this.degital1 == null)? "":this.degital4)
			.key("degital5").value((this.degital1 == null)? "":this.degital5)
			.key("degital6").value((this.degital1 == null)? "":this.degital6)
			.key("degital7").value((this.degital1 == null)? "":this.degital7)
			.key("degital8").value((this.degital1 == null)? "":this.degital8)
			.key("analogCurrent").value((this.degital1 == null)? "":this.analogCurrent)
			.key("analogCurrentCnv").value((this.degital1 == null)? "":this.analogCurrentCnv)
			.key("analogVoltage").value((this.degital1 == null)? "":this.analogVoltage)
			.key("analogVoltageCnv").value((this.degital1 == null)? "":this.analogVoltageCnv)
			.key("analogCurrentCnv").value((this.degital1 == null)? "":this.analogCurrentCnv)
			.key("scaleCurrent").value((this.degital1 == null)? "":this.scaleCurrent)
			.key("scaleVoltage").value((this.degital1 == null)? "":this.scaleVoltage)
			.key("installDate").value((this.degital1 == null)? "":this.scaleCurrent)
			.key("lastUpdateTime").value((this.degital1 == null)? "":this.scaleVoltage)
			.endObject();
		} catch (Exception e) {
			System.out.println(e);
		}
		return js.toString();
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
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

	public String getDegitalString(){
		if ( degital1 == null || degital2 == null  || degital3 == null || degital4 == null ||
				degital5 == null || degital6 == null  || degital7 == null || degital8 == null ){
			return "";
		}
		StringBuffer strBuff = new StringBuffer();

		if ( degital8 ) strBuff.append("1"); else strBuff.append("0");
		if ( degital7 ) strBuff.append("1"); else strBuff.append("0");
		if ( degital6 ) strBuff.append("1"); else strBuff.append("0");
		if ( degital5 ) strBuff.append("1"); else strBuff.append("0");
		if ( degital4 ) strBuff.append("1"); else strBuff.append("0");
		if ( degital3 ) strBuff.append("1"); else strBuff.append("0");
		if ( degital2 ) strBuff.append("1"); else strBuff.append("0");
		if ( degital1 ) strBuff.append("1"); else strBuff.append("0");
		return strBuff.toString();
	}

	public Integer getDegital()
	{
		int r = 0;
		if ( degital1 == null || degital2 == null  || degital3 == null || degital4 == null ||
				degital5 == null || degital6 == null  || degital7 == null || degital8 == null ){
			return null;
		}
		if ( degital8 ){
			r |= 0x01 << 7 ;
		}
		if ( degital7 ){
			r |= 0x01 << 6 ;
		}
		if ( degital6 ){
			r |= 0x01 << 5 ;
		}
		if ( degital5 ){
			r |= 0x01 << 4 ;
		}
		if ( degital4 ){
			r |= 0x01 << 3 ;
		}
		if ( degital3 ){
			r |= 0x01 << 2 ;
		}
		if ( degital2 ){
			r |= 0x01 << 1 ;
		}
		if ( degital1 ){
			r |= 0x01 ;
		}
		return new Integer(r & 0xFFFFFFFF);      
	}

	public Meter getMeter() {
		return meter;
	}

	public void setMeter(Meter meter) {
		this.meter = meter;
	}

	public Integer getMeterId() {
		return meterId;
	}

	public void setMeterId(Integer meterId) {
		this.meterId = meterId;
	}

	public String getMdsId() {
		return mdsId;
	}

	public void setMdsId(String mdsId) {
		this.mdsId = mdsId;
	}

	public Boolean getDegital1() {
		return degital1;
	}

	public void setDegital1(Boolean degital1) {
		this.degital1 = degital1;
	}

	public Boolean getDegital2() {
		return degital2;
	}

	public void setDegital2(Boolean degital2) {
		this.degital2 = degital2;
	}

	public Boolean getDegital3() {
		return degital3;
	}

	public void setDegital3(Boolean degital3) {
		this.degital3 = degital3;
	}

	public Boolean getDegital4() {
		return degital4;
	}

	public void setDegital4(Boolean degital4) {
		this.degital4 = degital4;
	}

	public Boolean getDegital5() {
		return degital5;
	}

	public void setDegital5(Boolean degital5) {
		this.degital5 = degital5;
	}

	public Boolean getDegital6() {
		return degital6;
	}

	public void setDegital6(Boolean degital6) {
		this.degital6 = degital6;
	}

	public Boolean getDegital7() {
		return degital7;
	}

	public void setDegital7(Boolean degital7) {
		this.degital7 = degital7;
	}

	public Boolean getDegital8() {
		return degital8;
	}

	public void setDegital8(Boolean degital8) {
		this.degital8 = degital8;
	}

	public Double getAnalogCurrent() {
		return analogCurrent;
	}

	public void setAnalogCurrent(Double analogCurrent) {
		this.analogCurrent = analogCurrent;
	}

	public Double getAnalogCurrentCnv() {
		return analogCurrentCnv;
	}

	public void setAnalogCurrentCnv(Double analogCurrentCnv) {
		this.analogCurrentCnv = analogCurrentCnv;
	}

	public Double getAnalogVoltage() {
		return analogVoltage;
	}

	public void setAnalogVoltage(Double analogVoltage) {
		this.analogVoltage = analogVoltage;
	}

	public Double getAnalogVoltageCnv() {
		return analogVoltageCnv;
	}

	public void setAnalogVoltageCnv(Double analogVoltageCnv) {
		this.analogVoltageCnv = analogVoltageCnv;
	}

	public Integer getScaleCurrent() {
		return scaleCurrent;
	}

	public void setScaleCurrent(Integer scaleCurrent) {
		this.scaleCurrent = scaleCurrent;
	}

	public Integer getScaleVoltage() {
		return scaleVoltage;
	}

	public void setScaleVoltage(Integer scaleVoltage) {
		this.scaleVoltage = scaleVoltage;
	}

	public String getInstallDate() {
		return installDate;
	}

	public void setInstallDate(String installDate) {
		this.installDate = installDate;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}