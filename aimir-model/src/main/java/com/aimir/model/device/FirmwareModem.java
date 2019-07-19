package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.Scope;
import com.aimir.constants.CommonConstants.ModemType;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * @author goodjob
 *
 */

@Entity
@DiscriminatorValue("FirmwareModem")
public class FirmwareModem extends Firmware {

	private static final long serialVersionUID = -2595463346187722744L;

	@ColumnInfo(name = "MODEM TYPE", view = @Scope(create = true, read = true, update = false))
	@Column(name = "MODEM_TYPE")
	@Enumerated(EnumType.STRING)
	private ModemType modemType;

/*    @ColumnInfo(name="미터 모델", view=@Scope(create=true, read=true, update=false), descr="미터 제조사 모델의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="devicemodel_id")
    @ReferencedBy(name="name")
    private DeviceModel model;
*/
    @ColumnInfo(name="Stack Name", view=@Scope(create=true, read=true, update=false), descr="Stack Name")
    @Column(name="STACK_NAME")
    private String stackName;

	public ModemType getModemType() {
		return modemType;
	}

/*	public DeviceModel getModel() {
		return model;
	}
*/
	public String getStackName() {
		return stackName;
	}

	public void setModemType(String modemType) {
		this.modemType = ModemType.valueOf(modemType);
	}

/*	public void setModel(DeviceModel model) {
		this.model = model;
	}*/

	public void setStackName(String stackName) {
		this.stackName = stackName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
//		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result
				+ ((modemType == null) ? 0 : modemType.hashCode());
		result = prime * result
				+ ((stackName == null) ? 0 : stackName.hashCode());
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
		FirmwareModem other = (FirmwareModem) obj;
		/*if (model == null) {
			if (other.model != null)
				return false;
		} else if (!model.equals(other.model))
			return false;*/
		if (modemType == null) {
			if (other.modemType != null)
				return false;
		} else if (!modemType.equals(other.modemType))
			return false;
		if (stackName == null) {
			if (other.stackName != null)
				return false;
		} else if (!stackName.equals(other.stackName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FirmwareModem [modemType=" + modemType.name()
//				+ ", model=" + model + ", stackName=" + stackName
				+ ", stackName=" + stackName
				+ ", getId()=" + getId() + ", getHwVersion()="
				+ getHwVersion() + ", getFwVersion()=" + getFwVersion()
				+ ", getBuild()=" + getBuild() + ", getReleasedDate()="
				+ getReleasedDate() + ", getBinaryFileName()="
				+ getBinaryFileName() + "]";
	}
}
