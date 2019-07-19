package com.aimir.model.device;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * @author goodjob
 *
 */
@Entity
@DiscriminatorValue("FirmwareCodi")
public class FirmwareCodi extends Firmware{
	private static final long serialVersionUID = -8671912082669942032L;

	@Override
	public String toString() {
		return "FirmwareCodi [getId()=" + getId() + ", getHwVersion()="
				+ getHwVersion() + ", getFwVersion()=" + getFwVersion()
				+ ", getBuild()=" + getBuild() + ", getReleasedDate()="
				+ getReleasedDate() + ", getBinaryFileName()="
				+ getBinaryFileName() + "]";
	}
}
