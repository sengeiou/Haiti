package com.aimir.model.device;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.audit.IAuditable;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * @author goodjob
 *
 */
@MappedSuperclass
public abstract class Device implements IAuditable {
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_DEVICE")
    @SequenceGenerator(name="SEQ_DEVICE", sequenceName="SEQ_DEVICE", allocationSize=1)
	private Long id;

    @Enumerated(EnumType.STRING)
	private DeviceType deviceType;

//	@OneToMany(cascade=CascadeType.ALL)	
//	private List<Device> devices = new ArrayList<Device>();		
//
//	@ManyToOne(cascade=CascadeType.ALL)
//	private Device device;
    
	@OneToMany(cascade=CascadeType.ALL, mappedBy="device", fetch=FetchType.LAZY)	
	private List<Device> devices = new ArrayList<Device>(0);	

	@ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="PARENT_ID")
	private Device device;
	
	@Column(name="PARENT_ID", nullable=true, updatable=false, insertable=false)
	private Long parentId;

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="DEVICE_ID")
	@OrderBy("id")
	private List<MCUInstallImg> deviceInstallImgs;	

	public enum DeviceType {

        MCU(0), Modem(1), Meter(2);
        
        private Integer code;
        
        DeviceType(Integer code) {
            this.code = code;
        }
        
        public Integer getCode() {
            return this.code;
        }
        
		public static DeviceType getItem(int value) {
			for (DeviceType fc : DeviceType.values()) {
				if (fc.code == value) {
					return fc;
				}
			}
			return null;
		}
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	@XmlTransient
	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public void addDevice(Device device) {
		
		if(devices == null) 
			devices = new ArrayList<Device>();
		
		this.devices.add(device);
		device.setDevice(this);
	}
	
	@XmlTransient
	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}
	
	@XmlTransient
	public List<MCUInstallImg> getDeviceInstallImgs() {
		return deviceInstallImgs;
	}

	public void setDeviceInstallImgs(List<MCUInstallImg> deviceInstallImgs) {
		this.deviceInstallImgs = deviceInstallImgs;
	}
	
	public void addDeviceInstallImg(MCUInstallImg deviceInstallImg) {
		
		if(deviceInstallImgs == null) 
			deviceInstallImgs = new ArrayList<MCUInstallImg>();
		
		this.deviceInstallImgs.add(deviceInstallImg);
	}

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }	
	
}
