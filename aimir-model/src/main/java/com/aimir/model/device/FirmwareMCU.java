package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.Scope;
import com.aimir.constants.CommonConstants.McuType;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * @author goodjob
 *
 */
@Entity
@DiscriminatorValue("FirmwareMCU")
public class FirmwareMCU extends Firmware {
	private static final long serialVersionUID = -8313642538680764663L;

	@ColumnInfo(name = "MCU TYPE", view = @Scope(create = true, read = true, update = false), descr = "Code 1.1.1")
	@Column(name = "MCU_TYPE")
	@Enumerated(EnumType.STRING)
    private McuType mcuType;
	
	

	public McuType getMcuType() {
		return mcuType;
	}
	
	public void setMcuType(String mcuType) {
		//this.mcuType = McuType.valueOf(mcuType);
		this.mcuType = McuType.valueOf(mcuType);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((mcuType == null) ? 0 : mcuType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FirmwareMCU other = (FirmwareMCU) obj;
		if (mcuType == null) {
			if (other.mcuType != null)
				return false;
		} else if (!mcuType.equals(other.mcuType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FirmwareMCU [mcuType=" + mcuType.name() + ", getId()=" + getId()
				+ ", getHwVersion()=" + getHwVersion() + ", getFwVersion()="
				+ getFwVersion() + ", getBuild()=" + getBuild()
				+ ", getReleasedDate()=" + getReleasedDate()
				+ ", getBinaryFileName()=" + getBinaryFileName() + "]";
	}
}
