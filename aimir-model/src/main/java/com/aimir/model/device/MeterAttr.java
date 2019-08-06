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

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

/**
 * SP-898
 * @author
 *
 */
@Entity
@Table(name="METER_ATTR")
public class MeterAttr extends BaseObject implements JSONString  {
	private static final long serialVersionUID = -6221794493727935393L;


    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_METER_ATTR")
    @SequenceGenerator(name="SEQ_METER_ATTR", sequenceName="SEQ_METER_ATTR", allocationSize=1)
	@ColumnInfo(name="PK", descr="PK")
	private Integer id;


	@ColumnInfo(name="METER_ID")
	@Column(name="METER_ID", unique=true, nullable=false)
	private Integer meterId;

	@ColumnInfo(name="TEXT_ATTR_00", descr="Attribute or Meter Text 00. used by CheckMeterSerialLocation")
	@Column(name="TEXT_ATTR_00")
	private String textAttr00;

	@ColumnInfo(name="TEXT_ATTR_01", descr="Attribute or Meter Text 01. used by CheckMeterSerialLocation")
	@Column(name="TEXT_ATTR_01")
	private String textAttr01;

	@ColumnInfo(name="TEXT_ATTR_02", descr="Attribute or Meter Text 02. used by CheckMeterSerialLocation")
	@Column(name="TEXT_ATTR_02")
	private String textAttr02;

	@ColumnInfo(name="TEXT_ATTR_03", descr="Attribute or Meter Text 03. used by CheckMeterSerialLocation")
	@Column(name="TEXT_ATTR_03")
	private String textAttr03;

	@ColumnInfo(name="TEXT_ATTR_04", descr="Attribute or Meter Text 04. used by CheckMeterSerialLocation")
	@Column(name="TEXT_ATTR_04")
	private String textAttr04;

	@ColumnInfo(name="TEXT_ATTR_05", descr="Attribute or Meter Text 05. used by CheckMeterSerialLocation")
	@Column(name="TEXT_ATTR_05")
	private String textAttr05;

	@ColumnInfo(name="TEXT_ATTR_06", descr="Attribute or Meter Text 06")
	@Column(name="TEXT_ATTR_06")
	private String textAttr06;

	@ColumnInfo(name="TEXT_ATTR_07", descr="Attribute or Meter Text 07")
	@Column(name="TEXT_ATTR_07")
	private String textAttr07;

	@ColumnInfo(name="TEXT_ATTR_08", descr="Attribute or Meter Text 08")
	@Column(name="TEXT_ATTR_08")
	private String textAttr08;

	@ColumnInfo(name="TEXT_ATTR_09", descr="Attribute or Meter Text 09")
	@Column(name="TEXT_ATTR_09")
	private String textAttr09;

	@ColumnInfo(name="INT_ATTR_00", descr="Attribute or Meter Integer 00")
	@Column(name="INT_ATTR_00")
	private Integer intAttr00;

	@ColumnInfo(name="INT_ATTR_01", descr="Attribute or Meter Integer 01")
	@Column(name="INT_ATTR_01")
	private Integer intAttr01;

	@ColumnInfo(name="INT_ATTR_02", descr="Attribute or Meter Integer 02")
	@Column(name="INT_ATTR_02")
	private Integer intAttr02;


	@ColumnInfo(name="ALARM_VALUE", descr="Value of MeterAlarmObject")
	@Column(name="ALARM_VALUE",length=16)
	private String alarmValue;
	
	@ColumnInfo(name="ALARM_DATE", descr="Date of MeterAlarmObject Notified or Cleared")
	@Column(name="ALARM_DATE",length=14)
	private String alarmDate;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getMeterId() {
		return meterId;
	}
	public void setMeterId(Integer meterId) {
		this.meterId = meterId;
	}
	public String getTextAttr00() {
		return textAttr00;
	}
	public void setTextAttr00(String textAttr00) {
		this.textAttr00 = textAttr00;
	}
	public String getTextAttr01() {
		return textAttr01;
	}
	public void setTextAttr01(String textAttr01) {
		this.textAttr01 = textAttr01;
	}
	public String getTextAttr02() {
		return textAttr02;
	}
	public void setTextAttr02(String textAttr02) {
		this.textAttr02 = textAttr02;
	}
	public String getTextAttr03() {
		return textAttr03;
	}
	public void setTextAttr03(String textAttr03) {
		this.textAttr03 = textAttr03;
	}
	public String getTextAttr04() {
		return textAttr04;
	}
	public void setTextAttr04(String textAttr04) {
		this.textAttr04 = textAttr04;
	}
	public String getTextAttr05() {
		return textAttr05;
	}
	public void setTextAttr05(String textAttr05) {
		this.textAttr05 = textAttr05;
	}
	public String getTextAttr06() {
		return textAttr06;
	}
	public void setTextAttr06(String textAttr06) {
		this.textAttr06 = textAttr06;
	}
	public String getTextAttr07() {
		return textAttr07;
	}
	public void setTextAttr07(String textAttr07) {
		this.textAttr07 = textAttr07;
	}
	public String getTextAttr08() {
		return textAttr08;
	}
	public void setTextAttr08(String textAttr08) {
		this.textAttr08 = textAttr08;
	}
	public String getTextAttr09() {
		return textAttr09;
	}
	public void setTextAttr09(String textAttr09) {
		this.textAttr09 = textAttr09;
	}

	public Integer getIntAttr00() {
		return intAttr00;
	}
	public void setIntAttr00(Integer intAttr00) {
		this.intAttr00 = intAttr00;
	}
	public Integer getIntAttr01() {
		return intAttr01;
	}
	public void setIntAttr01(Integer intAttr01) {
		this.intAttr01 = intAttr01;
	}
	public Integer getIntAttr02() {
		return intAttr02;
	}
	public void setIntAttr02(Integer intAttr02) {
		this.intAttr02 = intAttr02;
	}
	
	public String getAlarmValue() {
		return alarmValue;
	}
	public void setAlarmValue(String alarmValue) {
		this.alarmValue = alarmValue;
	}
	public String getAlarmDate() {
		return alarmDate;
	}
	public void setAlarmDate(String alarmDate) {
		this.alarmDate = alarmDate;
	}
	public String toJSONString() {
		JSONStringer js = null;

		js = new JSONStringer();
		js.object().key("id").value(this.id)
				   .key("textAttr00").value((this.textAttr00 == null)? "null":this.textAttr00)
				   .key("textAttr01").value((this.textAttr01 == null)? "null":this.textAttr01)
				   .key("textAttr02").value((this.textAttr02 == null)? "null":this.textAttr02)
				   .key("textAttr03").value((this.textAttr03 == null)? "null":this.textAttr03)
				   .key("textAttr04").value((this.textAttr04 == null)? "null":this.textAttr04)
				   .key("textAttr05").value((this.textAttr05 == null)? "null":this.textAttr05)
				   .key("textAttr06").value((this.textAttr06 == null)? "null":this.textAttr06)
				   .key("textAttr07").value((this.textAttr07 == null)? "null":this.textAttr07)
				   .key("textAttr08").value((this.textAttr08 == null)? "null":this.textAttr08)
				   .key("textAttr09").value((this.textAttr09 == null)? "null":this.textAttr09)
				   .key("intAttr00").value((this.intAttr00 == null)? "null":this.intAttr00)
				   .key("intAttr01").value((this.intAttr01 == null)? "null":this.intAttr01)
				   .key("intAttr02").value((this.intAttr02 == null)? "null":this.intAttr02)
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
