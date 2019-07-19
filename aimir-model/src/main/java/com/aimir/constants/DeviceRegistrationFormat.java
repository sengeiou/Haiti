package com.aimir.constants;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.device.MCUCodiDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MCUVarDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.ACD;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.GasMeter;
import com.aimir.model.device.HMU;
import com.aimir.model.device.HeatMeter;
import com.aimir.model.device.IEIU;
import com.aimir.model.device.IHD;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;
import com.aimir.model.device.MCUCodiBinding;
import com.aimir.model.device.MCUCodiDevice;
import com.aimir.model.device.MCUCodiMemory;
import com.aimir.model.device.MCUCodiNeighbor;
import com.aimir.model.device.MCUVar;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.PLCIU;
import com.aimir.model.device.SubGiga;
import com.aimir.model.device.VolumeCorrector;
import com.aimir.model.device.WaterMeter;
import com.aimir.model.device.ZBRepeater;
import com.aimir.model.device.ZEUMBus;
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.device.ZMU;
import com.aimir.model.device.ZRU;
import com.aimir.model.system.Contract;
import com.aimir.model.system.DeviceModel;

@Component
public class DeviceRegistrationFormat {

	private static SupplierDao supplierDao;
	private static DeviceModelDao deviceModelDao;
    private static LocationDao locationDao;
    private static CodeDao codeDao;
	private static MCUVarDao mcuVarDao;
	private static MCUCodiDao mcuCodiDao;
	private static ContractDao contractDao;
	private static ModemDao modemDao;
	private static EndDeviceDao endDeviceDao;
	private static MCUDao mcuDao;

    @Autowired
    public void setSupplierDao(SupplierDao _supplierDao) {
    	supplierDao = _supplierDao;
    }
    
    @Autowired
    public void setDeviceModelDao(DeviceModelDao _deviceModelDao) {
    	deviceModelDao = _deviceModelDao;
    }
    
    @Autowired
    public void setLocationDao(LocationDao _locationDao) {
    	locationDao = _locationDao;
    }
    
    @Autowired
    public void setCodeDao(CodeDao _codeDao) {
        codeDao = _codeDao;
    }
	
	@Autowired
    public void setMCUVarDao(MCUVarDao _mcuVarDao) {
		mcuVarDao = _mcuVarDao;
    }
	
	@Autowired	
    public void setMCUCodiDao(MCUCodiDao _mcuCodiDao) {
		mcuCodiDao = _mcuCodiDao;
    }
	
	@Autowired	
    public void setContractDao(ContractDao _contractDao) {
		contractDao = _contractDao;
    }
	
	@Autowired	
    public void setModemDao(ModemDao _modemDao) {
		modemDao = _modemDao;
    }
    
	/**
	 * MCU
	 *
	 */
	public enum McuEnum {
    	 deviceModel {
    		 @Override
    		 public MCU getMCU(MCU mcu, String colValue){
				 List<DeviceModel> devices = deviceModelDao.getDeviceModelByName(mcu.getSupplier().getId(), colValue);
    			 mcu.setDeviceModel(devices.get(0));
				 return mcu;
    		 }
    	 }
    	,installDate {
   		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
   		 		 mcu.setInstallDate(colValue);
	   			 return mcu;
	   		 }
    	}
    	,location {
   		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
   		 		 mcu.setLocation(locationDao.getLocationByName(colValue).get(0));
	   			 return mcu;
	   		 }
    	}
    	,mcuType {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setMcuType(codeDao.get(codeDao.getCodeIdByCode((colValue))));
	   			 return mcu;
	   		 }
    	}
    	,protocolType {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setProtocolType(codeDao.get(codeDao.getCodeIdByCode((colValue))));
	   			 return mcu;
	   		 }
    	}
    	,supplier {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 if(mcu.getSupplier() == null) 
  		 			 mcu.setSupplier(supplierDao.getSupplierByName(colValue));
	   			 return mcu;
	   		 }
    	}
    	,sysHwVersion {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysHwVersion(colValue);
	   			 return mcu;
	   		 }
    	}
    	,sysID {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysID(colValue);
	   			 return mcu;
	   		 }
    	}
    	,sysLocalPort {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysLocalPort(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
    	}
    	,sysSwRevision {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysSwRevision(colValue);
	   			 return mcu;
	   		 }
    	}
    	,sysSwVersion {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysSwVersion(colValue);
	   			 return mcu;
	   		 }
    	}
    	,batteryCapacity {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setBatteryCapacity(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
    	}
    	,fwState {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setFwState(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
    	}
    	,ipAddr {
	   		 public MCU getMCU(MCU mcu, String colValue){
	   			 mcu.setIpAddr(colValue);
	   			 return mcu;
	   		 }
    	}
    	,lastCommDate {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setLastCommDate(colValue);
	   			 return mcu;
	   		 }
    	}
    	,lastModifiedDate {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setLastModifiedDate(colValue);
	   			 return mcu;
	   		 }
    	}
    	,lastswUpdateDate {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setLastswUpdateDate(colValue);
	   			 return mcu;
	   		 }
  		}
    	,lastTimeSyncDate {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setLastTimeSyncDate(colValue);
	   			 return mcu;
	   		 }
  		}
    	,locDetail {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setLocDetail(colValue);
	   			 return mcu;
	   		 }
  		}
    	,lowBatteryFlag {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setLowBatteryFlag(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,mcuCodi {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
				 MCUCodi codi = new MCUCodi();
				 codi.setMcuCodiBinding(new MCUCodiBinding());
				 codi.setMcuCodiDevice(new MCUCodiDevice());
				 codi.setMcuCodiMemory(new MCUCodiMemory());
				 codi.setMcuCodiNeighbor(new MCUCodiNeighbor());
				
				 mcuCodiDao.add(codi);
				 mcu.setMcuCodi(codi);
	   			 return mcu;
	   		 }
  		}
    	,mcuVar {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
				 MCUVar mcuVar = new MCUVar();
				 mcuVarDao.add(mcuVar);
				 mcu.setMcuVar(mcuVar);
	   			 return mcu;
	   		 }
  		}
    	,mobileUsageFlag {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
				 mcu.setMobileUsageFlag(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,networkStatus {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setNetworkStatus(Integer.parseInt(colValue));
  		 		 return mcu;
	   		 }
  		}
    	,powerState {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setPowerState(Integer.parseInt(colValue));
  		 		 return mcu;
	   		 }
  		}
    	,serviceAtm {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setServiceAtm(Integer.parseInt(colValue));
  		 		 return mcu;
	   		 }
  		}
    	,sysContanct {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysContact(colValue);
	   			 return mcu;
	   		 }
  		}
    	,sysCurTemp {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysCurTemp(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,sysDescr {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysDescr(colValue);
  		 		 return mcu;
	   		 }
  		}
    	,sysEtherType {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysEtherType(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,sysJoinNodeCount {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysJoinNodeCount(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,sysLocation {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysLocation(colValue);
  		 		 return mcu;
	   		 }
  		}
    	,sysMaxTemp {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysMaxTemp(Integer.parseInt(colValue));
  		 		 return mcu;
	   		 }
  		}
    	,sysMinTemp {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysMinTemp(Integer.parseInt(colValue));
  		 		 return mcu;
	   		 }
  		}
    	,sysMobileAccessPoinstName {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysMobileAccessPointName(colValue);
	   			 return mcu;
	   		 }
  		}
    	,sysMobileMode {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysMobileMode(Integer.parseInt(colValue));
  		 		 return mcu;
	   		 }
  		}
    	,sysMobileType {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysMobileType(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,sysMobileVendor {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysMobileVendor(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,sysModel {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysModel(colValue);
	   			 return mcu;
	   		 }
  		}
    	,sysName {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysName(colValue);
	   			 return mcu;
	   		 }
  		}
    	,sysOpMode {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysOpMode(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,sysPhoneNumber {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysPhoneNumber(colValue);
	   			 return mcu;
	   		 }
  		}
    	,sysPowerType {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysPowerType(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,sysResetReason {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysResetReason(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,sysServer {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysServer(colValue);
	   			 return mcu;
	   		 }
  		}
    	,sysServerAlarmPort {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysServerAlarmPort(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,sysServerPort {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysServerPort(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,sysState {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysState(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,sysStateMask {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysStateMask(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,sysTime {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysTime(colValue);
	   			 return mcu;
	   		 }
  		}
    	,sysTimeZone {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysTimeZone(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,sysType {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysType(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		}
    	,sysUpTime {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysUpTime(colValue);
	   			 return mcu;
	   		 }
  		}
    	,sysVendor {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setSysVendor(colValue);
	   			 return mcu;
	   		 }
  		}
    	,updateServerPort {
  		 	 @Override
	   		 public MCU getMCU(MCU mcu, String colValue){
  		 		 mcu.setUpdateServerPort(Integer.parseInt(colValue));
	   			 return mcu;
	   		 }
  		};

  		McuEnum(){}
    	
    	abstract public MCU getMCU(MCU mcu, String colValue);
    }


	public enum MeterEnum {
	
		 contract {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				List<Object> contractIds = contractDao.getContractIdByContractNo(colValue);
				meter.setContract((Contract)contractIds.get(0));
				return meter;
			}
		}
		,installDate {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setInstallDate(colValue);
				return meter;
			}
		}
		,Location {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setLocation(locationDao.getLocationByName(colValue).get(0));
				return meter;
			}
		}
		,mdsId {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setMdsId(colValue);
				return meter;
			}
		}
		,meterType {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setMeterType(codeDao.get(codeDao.getCodeIdByCode((colValue))));
				return meter;
			}
		}
		,model {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				List<DeviceModel> devices = deviceModelDao.getDeviceModelByName(meter.getSupplier().getId(), colValue);
				meter.setModel(devices.get(0));
				return meter;
			}
		}
		,modemPort {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setModemPort(Integer.parseInt(colValue));
				return meter;
			}
		}
		,prepaymentMeter {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setPrepaymentMeter( Integer.parseInt(colValue) != 0 );
				return meter;
			}
		}
		,supplierId {
			@Override
			public Meter getMeter(Meter meter, String colValue){
  		 		if(meter.getSupplier() == null) 
  		 			meter.setSupplier(supplierDao.getSupplierByName(colValue));
				return meter;
			}
		}
		,Address {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setAddress(colValue);
				return meter;
			}
		}
		,endDevice {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setEndDevice(endDeviceDao.get(Integer.parseInt(colValue)));
				return meter;
			}
		}
		,expirarionDate {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setExpirationDate(colValue);
				return meter;
			}
		}
		,gpioX {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setGpioX(Double.parseDouble(colValue));
				return meter;
			}
		}
		,gpioY {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setGpioY(Double.parseDouble(colValue));
				return meter;
			}
		}
		,gpioZ {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setGpioZ(Double.parseDouble(colValue));
				return meter;
			}
		}
		,hwVersion {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setHwVersion(colValue);
				return meter;
			}
		}
		,ihdId {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setIhdId(colValue);
				return meter;
			}
		}
		,installedSiteImg {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setInstalledSiteImg(colValue);
				return meter;
			}
		}
		,installProperty {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setInstallProperty(colValue);
				return meter;
			}
		}
		,lastMeteringValue {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setLastMeteringValue(Double.parseDouble(colValue));
				return meter;
			}
		}
		,lastReadDate {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setLastReadDate(colValue);
				return meter;
			}
		}
		,lastTimesyncDate {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setLastTimesyncDate(colValue);
				return meter;
			}
		}
		,lpInterval {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setLpInterval(Integer.parseInt(colValue));
				return meter;
			}
		}
		,meterCaution {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setMeterCaution(colValue);
				return meter;
			}
		}
		,meterError {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setMeterError(colValue);
				return meter;
			}
		}
		,meterStatus {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setMeterStatus(codeDao.get(codeDao.getCodeIdByCode((colValue))));
				return meter;
			}
		}
		,modem {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setModem(modemDao.get(colValue));
				return meter;
			}
		}
		,pulseConstant {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setPulseConstant(Double.parseDouble(colValue));
				return meter;
			}
		}
		,qualifiedDate {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setQualifiedDate(colValue);
				return meter;
			}
		}
		,swName {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setSwName(colValue);
				return meter;
			}
		}
		,swUpdateDate {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setSwUpdateDate(colValue);
				return meter;
			}
		}
		,swVersion {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setSwVersion(colValue);
				return meter;
			}
		}

		,usageThreshold {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setUsageThreshold(Double.parseDouble(colValue));
				return meter;
			}
		}
		,writeDate {
			@Override
			public Meter getMeter(Meter meter, String colValue){
				meter.setWriteDate(colValue);
				return meter;
			}
		};

		MeterEnum(){}
    	
    	abstract public Meter getMeter(Meter meter, String colValue);
		
	}

	public enum EnergyMeterEnum {
		
		 ct {
			@Override
			public EnergyMeter getMeter(EnergyMeter meter, String colValue){
				meter.setCt(Double.parseDouble(colValue));
				return meter;
			}
		}
		,dstApplyOn {
			@Override
			public EnergyMeter getMeter(EnergyMeter meter, String colValue){
				meter.setDstApplyOn( Integer.parseInt(colValue) != 0 );
				return meter;
			}
		}
		,dstSeasonOn {
			@Override
			public EnergyMeter getMeter(EnergyMeter meter, String colValue){
				meter.setDstSeasonOn( Integer.parseInt(colValue) != 0 );
				return meter;
			}
		}
		,meterElement {
			@Override
			public EnergyMeter getMeter(EnergyMeter meter, String colValue){
				meter.setMeterElement(codeDao.get(codeDao.getCodeIdByCode((colValue))));
				return meter;
			}
		}
		,switchActivateStatus {
			@Override
			public EnergyMeter getMeter(EnergyMeter meter, String colValue){
				meter.setSwitchActivateStatus(Integer.parseInt(colValue));
				return meter;
			}
		}
		,switchStatus {
			@Override
			public EnergyMeter getMeter(EnergyMeter meter, String colValue){
				meter.setSwitchStatus(Integer.parseInt(colValue));
				return meter;
			}
		}
		,transformerRatio {
			@Override
			public EnergyMeter getMeter(EnergyMeter meter, String colValue){
				meter.setTransformerRatio(Double.parseDouble(colValue));
				return meter;
			}
		}
		,vt {
			@Override
			public EnergyMeter getMeter(EnergyMeter meter, String colValue){
				meter.setVt(Double.parseDouble(colValue));
				return meter;
			}
		};

		EnergyMeterEnum(){}
    	
    	abstract public Meter getMeter(EnergyMeter meter, String colValue);
	}

	public enum GasMeterEnum {
		
		correctPulse {
			@Override
			public GasMeter getMeter(GasMeter meter, String colValue){
				meter.setCorrectPulse(Double.parseDouble(colValue));
				return meter;
			}
		}
		,currentPulse {
			@Override
			public GasMeter getMeter(GasMeter meter, String colValue){
				meter.setCurrentPulse(Double.parseDouble(colValue));
				return meter;
			}
		}
		,initPulse {
			@Override
			public GasMeter getMeter(GasMeter meter, String colValue){
				meter.setInitPulse(Double.parseDouble(colValue));
				return meter;
			}
		}
		,meterStatus {
			@Override
			public GasMeter getMeter(GasMeter meter, String colValue){
				meter.setMeterStatus(CommonConstants.getGasMeterStatus(colValue));
				return meter;
			}
		};

		GasMeterEnum(){}

		abstract public GasMeter getMeter(GasMeter meter, String colValue);
	}
	
	public enum HeatMeterEnum {
		
		apparatusRoomNumber {
			@Override
			public HeatMeter getMeter(HeatMeter meter, String colValue){
				meter.setApparatusRoomNumber(Integer.parseInt(colValue));
				return meter;
			}
		}
		,flowPerUnitPulse {
			@Override
			public HeatMeter getMeter(HeatMeter meter, String colValue){
				meter.setFlowPerUnitPulse(Integer.parseInt(colValue));
				return meter;
			}
		}
		,heatingArea {
			@Override
			public HeatMeter getMeter(HeatMeter meter, String colValue){
				meter.setHeatingArea(Double.parseDouble(colValue));
				return meter;
			}
		}
		,heatType {
			@Override
			public HeatMeter getMeter(HeatMeter meter, String colValue){
				meter.setHeatType(codeDao.get(codeDao.getCodeIdByCode((colValue))));
				return meter;
			}
		}
		,installedPressSensor {
			@Override
			public HeatMeter getMeter(HeatMeter meter, String colValue){
				meter.setInstalledPressSensor( Integer.parseInt(colValue) != 0 );
				return meter;
			}
		}
		,meteringUnit {
			@Override
			public HeatMeter getMeter(HeatMeter meter, String colValue){
				meter.setMeteringUnit(colValue);
				return meter;
			}
		}
		,numOfRoom {
			@Override
			public HeatMeter getMeter(HeatMeter meter, String colValue){
				meter.setNumOfRoom(Integer.parseInt(colValue));
				return meter;
			}
		}
		,standard {
			@Override
			public HeatMeter getMeter(HeatMeter meter, String colValue){
				meter.setStandard(colValue);
				return meter;
			}
		};

		HeatMeterEnum(){}

		abstract public HeatMeter getMeter(HeatMeter meter, String colValue);
	}

	public enum WaterMeterEnum {

		correctPulse {
			@Override
			public WaterMeter getMeter(WaterMeter meter, String colValue){
				meter.setCorrectPulse(Double.parseDouble(colValue));
				return meter;
			}
		}
		,currentPulse {
			@Override
			public WaterMeter getMeter(WaterMeter meter, String colValue){
				meter.setCurrentPulse(Integer.parseInt(colValue));
				return meter;
			}
		}
		,initPulse {
			@Override
			public WaterMeter getMeter(WaterMeter meter, String colValue){
				meter.setInitPulse(Double.parseDouble(colValue));
				return meter;
			}
		}
		,meterSize {
			@Override
			public WaterMeter getMeter(WaterMeter meter, String colValue){
				meter.setMeterSize(Double.parseDouble(colValue));
				return meter;
			}
		}
		,Qmax {
			@Override
			public WaterMeter getMeter(WaterMeter meter, String colValue){
				meter.setQMax(Double.parseDouble(colValue));
				return meter;
			}
		}
		,underGround {
			@Override
			public WaterMeter getMeter(WaterMeter meter, String colValue){
				meter.setUnderGround( Integer.parseInt(colValue) != 0 );
				return meter;
			}
		};

		WaterMeterEnum(){}
    	
    	abstract public WaterMeter getMeter(WaterMeter meter, String colValue);
		
	}

	public enum VCMeterEnum {

		 atmospherePressure {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setAtmospherePressure(Double.parseDouble(colValue));
				return meter;
			}
		}
		,basePressure {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setBasePressure(Double.parseDouble(colValue));
				return meter;
			}
		}
		,baseTemperature {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setBaseTemperature(Double.parseDouble(colValue));
				return meter;
			}
		}
		,batteryVoltage {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setBatteryVoltage(Double.parseDouble(colValue));
				return meter;
			}
		}
		,co2 {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setCo2(Double.parseDouble(colValue));
				return meter;
			}
		}
		,compressFactor {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setCompressFactor(Double.parseDouble(colValue));
				return meter;
			}
		}
		,convertType {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setConverterType(colValue);
				return meter;
			}
		}
		,correctedUsageIndex {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setCorretedUsageIndex(Double.parseDouble(colValue));
				return meter;
			}
		}
		,correctUsageCount {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setCorretedUsageCount(Double.parseDouble(colValue));
				return meter;
			}
		}
		,currentPressure {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setCurrentPressure(Double.parseDouble(colValue));
				return meter;
			}
		}
		,currentTemperature {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setCurrentTemperature(Double.parseDouble(colValue));
				return meter;
			}
		}
		,fixedFpv {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setFixedFpv(Double.parseDouble(colValue));
				return meter;
			}
		}
		,fixedPressure {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setFixedPressure(Double.parseDouble(colValue));
				return meter;
			}
		}
		,fixedTemperature {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setFixedTemperature(Double.parseDouble(colValue));
				return meter;
			}
		}
		,gasHour {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setGasHour(Integer.parseInt(colValue));
				return meter;
			}
		}
		,gasRelativeDensity {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setGasRelativeDensity(Double.parseDouble(colValue));
				return meter;
			}
		}
		,lowestLimitPressure {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setLowestLimitPressure(Double.parseDouble(colValue));
				return meter;
			}
		}
		,lowestLimptTemperature {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setLowestLimitTemperature(Double.parseDouble(colValue));
				return meter;
			}
		}
		,meterFactor {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setMeterFactor(Double.parseDouble(colValue));
				return meter;
			}
		}
		,n2 {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setN2(Double.parseDouble(colValue));
				return meter;
			}
		}
		,pipeLine {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setPipeLine(Integer.parseInt(colValue));
				return meter;
			}
		}
		,powerSupply {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setPowerSupply(Integer.parseInt(colValue));
				return meter;
			}
		}
		,pressureUnit {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setPressureUnit(colValue);
				return meter;
			}
		}
		,pulseWeight {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setPulseWeight(Double.parseDouble(colValue));
				return meter;
			}
		}
		,siteName {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setSiteName(colValue);
				return meter;
			}
		}
		,specificGravity {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setSpecificGravity(Double.parseDouble(colValue));
				return meter;
			}
		}
		,tag {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setTag(Integer.parseInt(colValue));
				return meter;
			}
		}
		,temperatureUnit {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setTemperatureUnit(colValue);
				return meter;
			}
		}
		,uncorrectedusageCount {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setUncorrectedUsageCount(Double.parseDouble(colValue));
				return meter;
			}
		}
		,uncorrectedusageIndex {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setUncorretedUsageIndex(Double.parseDouble(colValue));
				return meter;
			}
		}
		,upperLimitPressure {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setUpperLimitPressure(Double.parseDouble(colValue));
				return meter;
			}
		}
		,upperLimitTemperature {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setUpperLimitTemperature(Double.parseDouble(colValue));
				return meter;
			}
		}
		,volumeUnit {
			@Override
			public VolumeCorrector getMeter(VolumeCorrector meter, String colValue){
				meter.setVolumeUnit(colValue);
				return meter;
			}
		};

		VCMeterEnum(){}
    	
    	abstract public VolumeCorrector getMeter(VolumeCorrector meter, String colValue);
		
	}
	

	/**
	 * MODEM 
	 * @author subin.lee
	 *
	 */
	public enum ModemEnum {
		deviceSerial {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setDeviceSerial(colValue);
				return modem;
			}
		}
		,installDate {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setInstallDate(colValue);
				return modem;
			}
		}
		,lpPeriod {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setLpPeriod(Integer.parseInt(colValue));
				return modem;
			}
		}
		,modemType {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setModemType(colValue);
				return modem;
			}
		}
		,nodeType {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setNodeType(Integer.parseInt(colValue));
				return modem;
			}
		}
		,supplier {
			@Override
			public Modem getModem(Modem modem, String colValue){
  		 		if(modem.getSupplier() == null) 
  		 			modem.setSupplier(supplierDao.getSupplierByName(colValue));
				return modem;
			}
		}
		,address {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setAddress(colValue);
				return modem;
			}
		}
		,commState {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setCommState(Integer.parseInt(colValue));
				return modem;
			}
		}
		,currentThreshold {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setCurrentThreshold(Double.parseDouble(colValue));
				return modem;
			}
		}
		,fwRevision {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setFwRevision(colValue);
				return modem;
			}
		}
		,fwVer {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setFwVer(colValue);
				return modem;
			}
		}
		,gpioX {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setGpioX(Double.parseDouble(colValue));
				return modem;
			}
		}
		,gpioY {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setGpioY(Double.parseDouble(colValue));
				return modem;
			}
		}
		,gpioZ {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setGpioZ(Double.parseDouble(colValue));
				return modem;
			}
		}
		,hwVer {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setHwVer(colValue);
				return modem;
			}
		}
		,ipAddr {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setIpAddr(colValue);
				return modem;
			}
		}
		,lastLinkTime {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setLastLinkTime(colValue);
				return modem;
			}
		}
		,lastResetCode {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setLastResetCode(Integer.parseInt(colValue));
				return modem;
			}
		}
		,macAddr {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setMacAddr(colValue);
				return modem;
			}
		}
		,mcu {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setMcu(mcuDao.get(colValue));
				return modem;
			}
		}
		,model {
			@Override
			public Modem getModem(Modem modem, String colValue){
				List<DeviceModel> devices = deviceModelDao.getDeviceModelByName(modem.getSupplier().getId(), colValue);
				modem.setModel(devices.get(0));
				return modem;
			}
		}
		,nodeKind {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setNodeKind(colValue);
				return modem;
			}
		}
		,powerThreshold {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setPowerThreshold(Double.parseDouble(colValue));
				return modem;
			}
		}
		,protocolType {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setProtocolType(colValue);
				return modem;
			}
		}
		,protocolVersion {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setProtocolVersion(colValue);
				return modem;
			}
		}
		,resetCount {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setResetCount(Integer.parseInt(colValue));
				return modem;
			}
		}
		,rfPower {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setRfPower(Long.parseLong(colValue));
				return modem;
			}
		}
		,swVer {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setSwVer(colValue);
				return modem;
			}
		}
		,zdzdIfVersion {
			@Override
			public Modem getModem(Modem modem, String colValue){
				modem.setZdzdIfVersion(colValue);
				return modem;
			}
		};

		ModemEnum(){}
    	
    	abstract public Modem getModem(Modem modem, String colValue);
	}
	
	public enum ZRUModemEnum {

		channelId {
			@Override
			public Modem getModem(ZRU modem, String colValue){
				modem.setChannelId(Integer.parseInt(colValue));
				return modem;
			}
		}
		,extPanId {
			@Override
			public Modem getModem(ZRU modem, String colValue){
				modem.setExtPanId(colValue);
				return modem;
			}
		}
		,fixedReset {
			@Override
			public Modem getModem(ZRU modem, String colValue){
				modem.setFixedReset(colValue);
				return modem;
			}
		}
		,linkKey {
			@Override
			public Modem getModem(ZRU modem, String colValue){
				modem.setLinkKey(colValue);
				return modem;
			}
		}
		,lpChoice {
			@Override
			public Modem getModem(ZRU modem, String colValue){
				modem.setLpChoice(Integer.parseInt(colValue));
				return modem;
			}
		}
		,manualEnable {
			@Override
			public Modem getModem(ZRU modem, String colValue){
				modem.setManualEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,meteringDay {
			@Override
			public Modem getModem(ZRU modem, String colValue){
				modem.setMeteringDay(colValue);
				return modem;
			}
		}
		,meteringHour {
			@Override
			public Modem getModem(ZRU modem, String colValue){
				modem.setMeteringHour(colValue);
				return modem;
			}
		}
		,needJoinSet {
			@Override
			public Modem getModem(ZRU modem, String colValue){
				modem.setNeedJoinSet( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,networkKey {
			@Override
			public Modem getModem(ZRU modem, String colValue){
				modem.setNetworkKey(colValue);
				return modem;
			}
		}
		,panId {
			@Override
			public Modem getModem(ZRU modem, String colValue){
				modem.setPanId(Integer.parseInt(colValue));
				return modem;
			}
		}
		,securityEnable {
			@Override
			public Modem getModem(ZRU modem, String colValue){
				modem.setSecurityEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,testFlag {
			@Override
			public Modem getModem(ZRU modem, String colValue){
				modem.setTestFlag( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		};

		ZRUModemEnum(){}
    	
    	abstract public Modem getModem(ZRU modem, String colValue);
	}

	public enum ZMUModemEnum {
		
		 channelId {
			@Override
			public Modem getModem(ZMU modem, String colValue){
				modem.setChannelId(Integer.parseInt(colValue));
				return modem;
			}
		}
		,extPanId {
			@Override
			public Modem getModem(ZMU modem, String colValue){
				modem.setExtPanId(colValue);
				return modem;
			}
		}
		,linkKey {
			@Override
			public Modem getModem(ZMU modem, String colValue){
				modem.setLinkKey(colValue);
				return modem;
			}
		}
		,manualEnable {
			@Override
			public Modem getModem(ZMU modem, String colValue){
				modem.setManualEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,needJoinSet {
			@Override
			public Modem getModem(ZMU modem, String colValue){
				modem.setNeedJoinSet( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,networkKey {
			@Override
			public Modem getModem(ZMU modem, String colValue){
				modem.setNetworkKey(colValue);
				return modem;
			}
		}
		,panId {
			@Override
			public Modem getModem(ZMU modem, String colValue){
				modem.setPanId(Integer.parseInt(colValue));
				return modem;
			}
		}
		,securityEnable {
			@Override
			public Modem getModem(ZMU modem, String colValue){
				modem.setSecurityEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		};

		ZMUModemEnum(){}
    	
    	abstract public Modem getModem(ZMU modem, String colValue);
	}

	public enum ZEUPLSModemEnum {
		
		 activeTime {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setActiveTime(Integer.parseInt(colValue));
				return modem;
			}
		}
		,alarmFlag {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setAlarmFlag(Integer.parseInt(colValue));
				return modem;
			}
		}
		,alarmMask {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setAlarmMask(Integer.parseInt(colValue));
				return modem;
			}
		}
		,autoTrapFlag {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setAutoTrapFlag( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,batteryCapacity {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setBatteryCapacity(Double.parseDouble(colValue));
				return modem;
			}
		}
		,batteryStatus {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setBatteryStatus(colValue);
				return modem;
			}
		}
		,batteryVolt {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setBatteryVolt(Double.parseDouble(colValue));
				return modem;
			}
		}
		,channelId {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setChannelId(Integer.parseInt(colValue));
				return modem;
			}
		}
		,extPanId {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setExtPanId(colValue);
				return modem;
			}
		}
		,fixedReset {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setFixedReset(colValue);
				return modem;
			}
		}
		,linkKey {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setLinkKey(colValue);
				return modem;
			}
		}
		,lpChoice {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setLpChoice(Integer.parseInt(colValue));
				return modem;
			}
		}
		,LQI {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setLQI(Integer.parseInt(colValue));
				return modem;
			}
		}
		,manualEnable {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setManualEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,meteringDay {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setMeteringDay(colValue);
				return modem;
			}
		}
		,meteringHour {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setMeteringHour(colValue);
				return modem;
			}
		}
		,needJoinSet {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setNeedJoinSet( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,networkKey {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setNetworkKey(colValue);
				return modem;
			}
		}
		,networkType {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setNetworkType(colValue);
				return modem;
			}
		}
		,operationDay {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setOperatingDay(Integer.parseInt(colValue));
				return modem;
			}
		}
		,panId {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setPanId(Integer.parseInt(colValue));
				return modem;
			}
		}
		,permitMode {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setPermitMode(Integer.parseInt(colValue));
				return modem;
			}
		}
		,permitState {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setPermitState(Integer.parseInt(colValue));
				return modem;
			}
		}
		,powerType {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setPowerType(colValue);
				return modem;
			}
		}
		,resetReason {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setResetReason(Integer.parseInt(colValue));
				return modem;
			}
		}
		,rssi {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setRssi(Integer.parseInt(colValue));
				return modem;
			}
		}
		,securityEnable {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setSecurityEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,solarADV {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setSolarADV(Double.parseDouble(colValue));
				return modem;
			}
		}
		,solarBDCV {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setSolarBDCV(Double.parseDouble(colValue));
				return modem;
			}
		}
		,solarChgBV {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setSolarChgBV(Double.parseDouble(colValue));
				return modem;
			}
		}
		,testFlag {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setTestFlag( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,trapDate {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setTrapDate(Integer.parseInt(colValue));
				return modem;
			}
		}
		,trapHour {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setTrapHour(Integer.parseInt(colValue));
				return modem;
			}
		}
		,trapMinute {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setTrapMinute(Integer.parseInt(colValue));
				return modem;
			}
		}
		,trapSecond {
			@Override
			public Modem getModem(ZEUPLS modem, String colValue){
				modem.setTrapSecond(Integer.parseInt(colValue));
				return modem;
			}
		};

		ZEUPLSModemEnum(){}
    	
    	abstract public Modem getModem(ZEUPLS modem, String colValue);
	}

	public enum ZEUMBusModemEnum {
		
		 armFwBuild {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setArmFwBuild(colValue);
				return modem;
			}
		}
		,armFwVer {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setArmFwVer(colValue);
				return modem;
			}
		}
		,armHwVer {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setArmHwVer(colValue);
				return modem;
			}
		}
		,armModel {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setArmModel(colValue);
				return modem;
			}
		}
		,channelId {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setChannelId(Integer.parseInt(colValue));
				return modem;
			}
		}
		,extPanId {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setExtPanId(colValue);
				return modem;
			}
		}
		,fixedReset {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setFixedReset(colValue);
				return modem;
			}
		}
		,linkKey {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setLinkKey(colValue);
				return modem;
			}
		}
		,manualEnable {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setManualEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,meteringDay {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setMeteringDay(colValue);
				return modem;
			}
		}
		,meteringHour {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setMeteringHour(colValue);
				return modem;
			}
		}
		,needJoinSet {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setNeedJoinSet( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,networkKey {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setNetworkKey(colValue);
				return modem;
			}
		}
		,panId {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setPanId(Integer.parseInt(colValue));
				return modem;
			}
		}
		,securityEnable {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setSecurityEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,testFlag {
			@Override
			public Modem getModem(ZEUMBus modem, String colValue){
				modem.setTestFlag( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		};

		ZEUMBusModemEnum(){}
    	
    	abstract public Modem getModem(ZEUMBus modem, String colValue);
	}

	public enum ZBRepeaterModemEnum {
		 activeTime {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setActiveTime(Integer.parseInt(colValue));
				return modem;
			}
		}
		,batteryCapacity {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setBatteryCapacity(Integer.parseInt(colValue));
				return modem;
			}
		}
		,batteryStatus {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setBatteryStatus(colValue);
				return modem;
			}
		}
		,batteryVolt {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setBatteryVolt(Double.parseDouble(colValue));
				return modem;
			}
		}
		,channelId {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setChannelId(Integer.parseInt(colValue));
				return modem;
			}
		}
		,extPanId {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setExtPanId(colValue);
				return modem;
			}
		}
		,fixedReset {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setFixedReset(colValue);
				return modem;
			}
		}
		,linkKey {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setLinkKey(colValue);
				return modem;
			}
		}
		,lpChoice {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setLpChoice(Integer.parseInt(colValue));
				return modem;
			}
		}
		,manualEnable {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setManualEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,meteringDay {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setMeteringDay(colValue);
				return modem;
			}
		}
		,meteringHour {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setMeteringHour(colValue);
				return modem;
			}
		}
		,networkKey {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setNetworkKey(colValue);
				return modem;
			}
		}
		,networkType {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setNetworkType(colValue);
				return modem;
			}
		}
		,operationDay {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setOperatingDay(Integer.parseInt(colValue));
				return modem;
			}
		}
		,panId {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setPanId(Integer.parseInt(colValue));
				return modem;
			}
		}
		,powerType {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setPowerType(colValue);
				return modem;
			}
		}
		,repeatingDay {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setRepeatingDay(colValue);
				return modem;
			}
		}
		,repeatingHour {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setRepeatingHour(colValue);
				return modem;
			}
		}
		,repeatingSetupSec {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setRepeatingSetupSec(Integer.parseInt(colValue));
				return modem;
			}
		}
		,securityEnable {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setSecurityEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,solarADV {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setSolarADV(Double.parseDouble(colValue));
				return modem;
			}
		}
		,solarBDCV {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setSolarBDCV(Double.parseDouble(colValue));
				return modem;
			}
		}
		,testFlag {
			@Override
			public Modem getModem(ZBRepeater modem, String colValue){
				modem.setTestFlag( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		};

		ZBRepeaterModemEnum(){}
    	
    	abstract public Modem getModem(ZBRepeater modem, String colValue);
	}

	public enum PLCIUModemEnum {
		 sysContact {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysContact(colValue);
				return modem;
			}
		}
		,sysDescr {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysDescr(colValue);
				return modem;
			}
		}
		,sysFactoryReset {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysFactoryReset(Integer.parseInt(colValue));
				return modem;
			}
		}
		,sysFwVersion {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysFwVersion(colValue);
				return modem;
			}
		}
		,sysIpAddr {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysIpAddr(colValue);
				return modem;
			}
		}
		,sysLocation {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysLocation(colValue);
				return modem;
			}
		}
		,sysName {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysName(colValue);
				return modem;
			}
		}
		,sysNodeType {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysNodeType(Integer.parseInt(colValue));
				return modem;
			}
		}
		,sysObjectId {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysObjectId(colValue);
				return modem;
			}
		}
		,sysPort {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysPort(colValue);
				return modem;
			}
		}
		,sysReset {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysReset(Integer.parseInt(colValue));
				return modem;
			}
		}
		,sysRtsCtsEnable {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysRtsCtsEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,sysSerialParityType {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysSerialParityType(Integer.parseInt(colValue));
				return modem;
			}
		}
		,sysSerialRate {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysSerialRate(Integer.parseInt(colValue));
				return modem;
			}
		}
		,sysSerialStopBit {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysSerialStopBit(Integer.parseInt(colValue));
				return modem;
			}
		}
		,sysSerialWordBit {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysSerialWordBit(Integer.parseInt(colValue));
				return modem;
			}
		}
		,sysService {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysService(Integer.parseInt(colValue));
				return modem;
			}
		}
		,sysStatus {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysStatus(colValue);
				return modem;
			}
		}
		,sysUseDhcp {
			@Override
			public Modem getModem(PLCIU modem, String colValue){
				modem.setSysUseDhcp(Integer.parseInt(colValue));
				return modem;
			}
		};

		PLCIUModemEnum(){}
    	
    	abstract public Modem getModem(PLCIU modem, String colValue);
	}
	
	public enum MMIUModemEnum {
		 phoneNumber {
			@Override
			public Modem getModem(MMIU modem, String colValue){
				modem.setPhoneNumber(colValue);
				return modem;
			}
		}
		,errorStatus {
			@Override
			public Modem getModem(MMIU modem, String colValue){
				modem.setErrorStatus(Integer.parseInt(colValue));
				return modem;
			}
		}
		,simNumber {
			@Override
			public Modem getModem(MMIU modem, String colValue){
				modem.setSimNumber(colValue);
				return modem;
			}
		};

		MMIUModemEnum(){}
    	
    	abstract public Modem getModem(MMIU modem, String colValue);
	}
	
	public enum IHDModemEnum {
		 billDate {
			@Override
			public Modem getModem(IHD modem, String colValue){
				modem.setBillDate(Integer.parseInt(colValue));
				return modem;
			}
		}
		,channelId {
			@Override
			public Modem getModem(IHD modem, String colValue){
				modem.setChannelId(Integer.parseInt(colValue));
				return modem;
			}
		}
//		,demandResponse {
//			@Override
//			public Modem getModem(IHD modem, String colValue){
//				modem.set(colValue);
//				return modem;
//			}
//		}
		,extPanId {
			@Override
			public Modem getModem(IHD modem, String colValue){
				modem.setExtPanId(colValue);
				return modem;
			}
		}
		,fixedReset {
			@Override
			public Modem getModem(IHD modem, String colValue){
				modem.setFixedReset(colValue);
				return modem;
			}
		}
		,gasThreshold {
			@Override
			public Modem getModem(IHD modem, String colValue){
				modem.setGasThreshold(Integer.parseInt(colValue));
				return modem;
			}
		}
		,linkKey {
			@Override
			public Modem getModem(IHD modem, String colValue){
				modem.setLinkKey(colValue);
				return modem;
			}
		}
		,manualEnable {
			@Override
			public Modem getModem(IHD modem, String colValue){
				modem.setManualEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,needJoinSet {
			@Override
			public Modem getModem(IHD modem, String colValue){
				modem.setNeedJoinSet( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,networkKey {
			@Override
			public Modem getModem(IHD modem, String colValue){
				modem.setNetworkKey(colValue);
				return modem;
			}
		}
		,panId {
			@Override
			public Modem getModem(IHD modem, String colValue){
				modem.setPanId(Integer.parseInt(colValue));
				return modem;
			}
		}
		,peakDemandThreshold {
			@Override
			public Modem getModem(IHD modem, String colValue){
				modem.setPeakDemandThreshold(Integer.parseInt(colValue));
				return modem;
			}
		}
		,securityEnable {
			@Override
			public Modem getModem(IHD modem, String colValue){
				modem.setSecurityEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,testFlag {
			@Override
			public Modem getModem(IHD modem, String colValue){
				modem.setTestFlag( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,waterThreshold {
			@Override
			public Modem getModem(IHD modem, String colValue){
				modem.setWaterThreshold(Integer.parseInt(colValue));
				return modem;
			}
		};

		IHDModemEnum(){}
    	
    	abstract public Modem getModem(IHD modem, String colValue);
	}
	
	public enum IEIUModemEnum {
		 phoneNumber {
			@Override
			public Modem getModem(IEIU modem, String colValue){
				modem.setPhoneNumber(colValue);
				return modem;
			}
		}
		,errorStatus {
			@Override
			public Modem getModem(IEIU modem, String colValue){
				modem.setErrorStatus(Integer.parseInt(colValue));
				return modem;
			}
		}
		,groupNumber {
			@Override
			public Modem getModem(IEIU modem, String colValue){
				modem.setGroupNumber(Integer.parseInt(colValue));
				return modem;
			}
		}
		,memberNumber {
			@Override
			public Modem getModem(IEIU modem, String colValue){
				modem.setMemberNumber(Integer.parseInt(colValue));
				return modem;
			}
		}
		,simNumber {
			@Override
			public Modem getModem(IEIU modem, String colValue){
				modem.setSimNumber(colValue);
				return modem;
			}
		};

		IEIUModemEnum(){}
    	
    	abstract public Modem getModem(IEIU modem, String colValue);
	}
	
	public enum HMUModemEnum {
		 channelId {
			@Override
			public Modem getModem(HMU modem, String colValue){
				modem.setChannelId(Integer.parseInt(colValue));
				return modem;
			}
		}
		,extPanId {
			@Override
			public Modem getModem(HMU modem, String colValue){
				modem.setExtPanId(colValue);
				return modem;
			}
		}
		,fixedReset {
			@Override
			public Modem getModem(HMU modem, String colValue){
				modem.setFixedReset(colValue);
				return modem;
			}
		}
		,linkKey {
			@Override
			public Modem getModem(HMU modem, String colValue){
				modem.setLinkKey(colValue);
				return modem;
			}
		}
		,lpChoice {
			@Override
			public Modem getModem(HMU modem, String colValue){
				modem.setLpChoice(Integer.parseInt(colValue));
				return modem;
			}
		}
		,manualEnable {
			@Override
			public Modem getModem(HMU modem, String colValue){
				modem.setManualEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,meteringDay {
			@Override
			public Modem getModem(HMU modem, String colValue){
				modem.setMeteringDay(colValue);
				return modem;
			}
		}
		,meteringHour {
			@Override
			public Modem getModem(HMU modem, String colValue){
				modem.setMeteringHour(colValue);
				return modem;
			}
		}
		,needJoinSet {
			@Override
			public Modem getModem(HMU modem, String colValue){
				modem.setNeedJoinSet( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,networkKey {
			@Override
			public Modem getModem(HMU modem, String colValue){
				modem.setNetworkKey(colValue);
				return modem;
			}
		}
		,panId {
			@Override
			public Modem getModem(HMU modem, String colValue){
				modem.setPanId(Integer.parseInt(colValue));
				return modem;
			}
		}
		,securityEnable {
			@Override
			public Modem getModem(HMU modem, String colValue){
				modem.setSecurityEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,testFlag {
			@Override
			public Modem getModem(HMU modem, String colValue){
				modem.setTestFlag( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		};

		HMUModemEnum(){}
    	
    	abstract public Modem getModem(HMU modem, String colValue);
	}
	
	public enum ACDModemEnum {
		 channelId {
			@Override
			public Modem getModem(ACD modem, String colValue){
				modem.setChannelId(Integer.parseInt(colValue));
				return modem;
			}
		}
		,extPanId {
			@Override
			public Modem getModem(ACD modem, String colValue){
				modem.setExtPanId(colValue);
				return modem;
			}
		}
		,fixedReset {
			@Override
			public Modem getModem(ACD modem, String colValue){
				modem.setFixedReset(colValue);
				return modem;
			}
		}
		,linkKey {
			@Override
			public Modem getModem(ACD modem, String colValue){
				modem.setLinkKey(colValue);
				return modem;
			}
		}
		,lpChoice {
			@Override
			public Modem getModem(ACD modem, String colValue){
				modem.setLpChoice(Integer.parseInt(colValue));
				return modem;
			}
		}
		,manualEnable {
			@Override
			public Modem getModem(ACD modem, String colValue){
				modem.setManualEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,meteringDay {
			@Override
			public Modem getModem(ACD modem, String colValue){
				modem.setMeteringDay(colValue);
				return modem;
			}
		}
		,meteringHour {
			@Override
			public Modem getModem(ACD modem, String colValue){
				modem.setMeteringHour(colValue);
				return modem;
			}
		}
		,needJoinSet {
			@Override
			public Modem getModem(ACD modem, String colValue){
				modem.setNeedJoinSet( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,networkKey {
			@Override
			public Modem getModem(ACD modem, String colValue){
				modem.setNetworkKey(colValue);
				return modem;
			}
		}
		,panId {
			@Override
			public Modem getModem(ACD modem, String colValue){
				modem.setPanId(Integer.parseInt(colValue));
				return modem;
			}
		}
		,securityEnable {
			@Override
			public Modem getModem(ACD modem, String colValue){
				modem.setSecurityEnable( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		}
		,testFlag {
			@Override
			public Modem getModem(ACD modem, String colValue){
				modem.setTestFlag( Integer.parseInt(colValue) != 0 );
				return modem;
			}
		};

		ACDModemEnum(){}
    	
    	abstract public Modem getModem(ACD modem, String colValue);
	}
	
	public enum SubGigaModemEnum {

		baseStationAddress {
			@Override
			public Modem getModem(SubGiga modem, String colValue){
				modem.setBaseStationAddress(colValue);
				return modem;
			}
		}
		,ipv6Address {
			@Override
			public Modem getModem(SubGiga modem, String colValue){
				modem.setIpv6Address(colValue);
				return modem;
			}
		}	
		,securityKey {
			@Override
			public Modem getModem(SubGiga modem, String colValue){
				modem.setSecurityKey(colValue);
				return modem;
			}
		}
		,hopsToBaseStation {
			@Override
			public Modem getModem(SubGiga modem, String colValue){
				modem.setHopsToBaseStation(Integer.parseInt(colValue));
				return modem;
			}
		}
		,frequency {
			@Override
			public Modem getModem(SubGiga modem, String colValue){
				modem.setFrequency(Integer.parseInt(colValue));
				return modem;
			}
		}
		,bandWidth {
			@Override
			public Modem getModem(SubGiga modem, String colValue){
				modem.setBandWidth(Integer.parseInt(colValue));
				return modem;
			}
		};


		SubGigaModemEnum(){}
    	
    	abstract public Modem getModem(SubGiga modem, String colValue);
	}
}
