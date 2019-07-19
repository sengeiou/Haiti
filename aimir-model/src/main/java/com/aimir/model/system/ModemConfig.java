package com.aimir.model.system;

import javax.persistence.Entity;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;

/**
 * 모뎀장비 설정정보 
 *모뎀에 연결되는 파서명 
 */
@Entity
public class ModemConfig extends DeviceConfig implements JSONString {

	private static final long serialVersionUID = -3944612148130901203L;
	
	@ColumnInfo(name="swVersion", descr="모뎀 SW Version")
	private String swVersion;
	
	@ColumnInfo(name="swRevision", descr="모뎀 SW Revision")
	private String swRevision;
	
	public String getSwVersion() {
		return swVersion;
	}
	public void setSwVersion(String swVersion) {
		this.swVersion = swVersion;
	}
	public String getSwRevision() {
		return swRevision;
	}
	public void setSwRevision(String swRevision) {
		this.swRevision = swRevision;
	}
	
    @Override
	public String toString()
	{
	    return "ModemConfig "+toJSONString();
	}
	public String toJSONString() {
	    
	    String retValue = "";
	    
	    retValue = "{"
	        + "id:'" + this.getId() 
	        + "',deviceModel:'" + ((getDeviceModel() == null)? "null":getDeviceModel().getId()) 
	        + "',parserName:'" + this.getParserName() 
	        + "',saverName:'" + this.getSaverName()
	        + "',ondemandParserName:'" + this.getOndemandParserName()
	        + "',ondemandSaverName:'" + this.getOndemandSaverName()
	        + "',swVersion:'" + this.getSwVersion() 
	        + "',swRevision:'" + this.getSwRevision() 
	        + "'}";
	    return retValue;
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
    public String getInstanceName() {
        return this.getName();
    }
}
