package com.aimir.fep.meter.entry;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ChannelCalcMethod;
import com.aimir.constants.CommonConstants.DataSVC;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.MeterVendor;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.MeterMapperDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.DeviceVendorDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.fep.util.threshold.CheckThreshold;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.MeterMapper;
import com.aimir.model.device.Modem;
import com.aimir.model.mvm.ChannelConfig;
import com.aimir.model.mvm.DisplayChannel;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.Location;
import com.aimir.model.system.MeterConfig;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.IPUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

public class MeasurementDataEntry implements IMeasurementDataEntry
{
    private static Log log = LogFactory.getLog(MeasurementDataEntry.class);
    public byte[] modemId = new byte[8];
    public byte[] meterId = new byte[20];
    public byte modemType = (byte)0x01;
    public byte svcType = (byte)0x01;
    public byte vendor = (byte)0x01;
    public int model = 0;
    static List<String> EUI64_PREFIX = null;
    static List<Integer> EUI64_EXCEPT = null;

    private static CodeDao codeDao;
    private static LocationDao locationDao;
    private static ModemDao modemDao;
    private static MeterDao meterDao;
    private static MCUDao mcuDao;
    private static SupplierDao supplierDao;
    private static DeviceVendorDao deviceVendorDao;
    private static DeviceModelDao deviceModelDao;
    private Meter meter;
    private static MeterMapperDao meterMapperDao;

    //ondemand 여부
    private boolean isOnDemand = false;
    
    static {
        EUI64_PREFIX = new ArrayList<String>();
        EUI64_EXCEPT = new ArrayList<Integer>();
        String eui64candi = FMPProperty.getProperty("eui64.prefix.candidate");
        StringTokenizer st = new StringTokenizer(eui64candi, ",");
        while (st.hasMoreTokens()) {
            EUI64_PREFIX.add(st.nextToken().trim());
        }

        String eui64except = FMPProperty.getProperty("eui64.except.modem");
        st = new StringTokenizer(eui64except, ",");
        while (st.hasMoreTokens()) {
            EUI64_EXCEPT.add(Integer.parseInt(st.nextToken().trim()));
        }
        
        codeDao = DataUtil.getBean(CodeDao.class);
        locationDao = DataUtil.getBean(LocationDao.class);
        modemDao = DataUtil.getBean(ModemDao.class);
        meterDao = DataUtil.getBean(MeterDao.class);
        mcuDao = DataUtil.getBean(MCUDao.class);
        supplierDao = DataUtil.getBean(SupplierDao.class);
        deviceVendorDao = DataUtil.getBean(DeviceVendorDao.class);
        deviceModelDao = DataUtil.getBean(DeviceModelDao.class);
        meterMapperDao = DataUtil.getBean(MeterMapperDao.class);
    }

    public byte[] dataCnt = new byte[2];
    private ArrayList<MeasurementData> mds = new ArrayList<MeasurementData>();

    public MeasurementDataEntry()
    {
    }

    /**
	 * @param isOnDemand the isOnDemand to set
	 */
	public void setOnDemand(boolean isOnDemand) {
		this.isOnDemand = isOnDemand;
	}

	/**
	 * @return the isOnDemand
	 */
	public boolean isOnDemand() {
		return isOnDemand;
	}

	public String getModemId()
    {
		return Hex.decode(this.modemId);
    }
	
    public void setModemId(String data)
    {
        this.modemId = Hex.encode(data);
    }

    public String getMeterId()
    {
        return (new String(this.meterId)).trim();
    }

    public void setMeterId(String data)
    {
        byte[] bx = data.getBytes();
        int len = bx.length;
        if(len > 20) {
            len = 20;
        }
        System.arraycopy(bx,0,this.meterId,0,len);
    }

    public ModemType getModemType()
    {
        return CommonConstants.getModemType(DataUtil.getIntToByte(this.modemType));
    }

    public void setModemType(int data)
    {
        this.modemType = (byte)data;
    }

    public DataSVC getSvcType()
    {
        DataSVC svc = CommonConstants.getDataSVC(this.svcType);
    	// log.debug("svctypecode["+ svc.name() + "]");
        return svc;
    }

    public void setSvcType(int data)
    {
        this.svcType = (byte)data;
    }

    public MeterVendor getVendor()
    {
        return CommonConstants.getMeterVendor(DataUtil.getIntToByte(this.vendor));
    }

    public void setVendor(int data)
    {
        this.vendor = (byte)data;
    }

    public int getModel()
    {
        return this.model;
    }

    public void setModel(int data)
    {
        this.model = data;
    }

    public int getDataCnt()
    {
        return DataUtil.getIntTo2Byte(this.dataCnt);
    }

    public void setDataCnt(int data)
    {
        this.dataCnt = DataUtil.get2ByteToInt(data);
    }

    public void append(MeasurementData data)
    {
        mds.add(data);
    }

    public MeasurementData[] getMeasurementData()
    {
        return mds.toArray(new MeasurementData[0]);
    }

    public IMeasurementData[] getSortedMeasurementData()
    {
        Collections.sort(mds,MDComparator.TIMESTAMP_ORDER);
        return mds.toArray(new MeasurementData[0]);
    }

    public byte[] encode()
    {
        setDataCnt(mds.size());
        DataUtil.convertEndian(this.dataCnt);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bao.write(modemId,0,modemId.length);
        bao.write(meterId,0,meterId.length);
        bao.write(modemType);
        bao.write(svcType);
        bao.write(vendor);
        bao.write(dataCnt,0,dataCnt.length);
        MeasurementData[] mds = getMeasurementData();
        byte[] bx = null;
        for(int i = 0 ; i < mds.length ; i++)
        {
            bx = mds[i].encode();
            bao.write(bx,0,bx.length);
        }

        return bao.toByteArray();
    }

    public int decode(byte[] data, int position, String mcuId, String ns, String ipAddr,
            String protocolType, String meteringType) throws Exception
    {
        log.debug("MeasurementDataEntry decode:: position["+position+"]"); // raw["+Hex.decode(data)+"]");
        int pos = position;

        System.arraycopy(data,pos,modemId,0,modemId.length);
        log.debug("MeasurementDataEntry decode:: modemId["+getModemId()+"]");

        pos+=modemId.length;
        System.arraycopy(data,pos,meterId,0,meterId.length);

        pos+=meterId.length;
        modemType = data[pos++];
        svcType = data[pos++];
        vendor = data[pos++];

        
        log.debug("decode:: namespace[" + ns +"]");
        log.debug("decode:: dcuId[" + mcuId +"]");
        log.debug("decode:: McuIpAddress[" + ipAddr+"]");
        log.debug("decode:: meterId[" + getMeterId() + "]");
        log.debug("decode:: modemType[" + getModemType() + "]");
        log.debug("decode:: svcType[" + getSvcType() + "]");
        log.debug("decode:: vendor[" +getVendor()+"]");
        
        
		System.arraycopy(data, pos, dataCnt, 0, dataCnt.length);
		log.debug("decode:: dataCnt[" + Hex.decode(dataCnt) + "]");
		DataUtil.convertEndian(this.dataCnt);
		log.debug("decode:: dataCnt[" + Hex.decode(dataCnt) + "]");
        pos+=dataCnt.length;
        int cnt = getDataCnt();
        log.debug("decode:: cnt["+cnt+"]");
        MeasurementData md = null;

        /*
         * 파키스탄에서는 이 명명규칙을 사용하지 않으므로 주석처리
         * 
         */
        // meterId, modemId pattern check. if both is wrong, data is broken.
//        if (!Pattern.matches("(000)([A-Z]{1})([0-9A-Z]{2})([0-9a-zA-Z]{10})", getModemId()) || 
//                !Pattern.matches("([0-9a-zA-Z]{"+getMeterId().length()+"})", getMeterId())) {
//        	// UPDATE START SP-285
////        	// INSERT START SP-193
////            CheckThreshold.updateCount(ipAddr, ThresholdName.INVALID_PACKET);
////            // INSERT END SP-193 
//            CheckThreshold.updateCount(ipAddr, ThresholdName.INVALID_PACKET, false);
//        	// UPDATE END SP-285
//            throw new Exception("Invalid Packet");
//        }
            
        Meter meter = validate(mcuId, ns, ipAddr, protocolType);

        if (meter != null) {
            for(int i = 0 ; i < cnt ; i++)
            {
                md = new MeasurementData(meter, mcuId);
                md.setOnDemand(this.isOnDemand());
                log.debug("pos["+pos+"]");
    
                // 2017.01.16
                // DCU ondemand has bug, data cnt is little endian
                if (pos == data.length) break;
                
                pos+=md.decode(data, pos, meteringType);
    
                log.debug("pos["+pos+"] md["+md.toString()+"]");
                append(md);
            }
            return (pos - position);
        }
        return 0;

        // TODO 검침 장비와 아이디, 통신 장비와 아이디를 식별한다.
        /*
        if (getModemType() == ModemType.MMIU || getModemType() == ModemType.IEIU) {
            deviceType = DeviceType.Modem;
            deviceId = meter.getModem().getDeviceSerial();
            mdevType = DeviceType.Meter;
            mdevId = meter.getMdsId();
        }
        else {
            deviceType = DeviceType.MCU;
            deviceId = meter.getModem().getMcu().getSysID();
            if (getModemType() == ModemType.ACD) {
                mdevType = DeviceType.Modem;
                mdevId = meter.getModem().getDeviceSerial();
            }
            else {
                mdevType = DeviceType.Meter;
                mdevId = meter.getMdsId();
            }
        }
        */
    }

    /**
     * 집중기, 모뎀, 미터 등록 여부와 관계 등록 여부 등 이 함수에서 검증한다.
     * 자동 등록 또는 이벤트를 발송한다.
     */
    private Meter validate(String mcuId, String ns, String ipAddr, String protocolType) throws Exception
    {
        /*
         * EUI64 체크를 삭제한다. 2015.01.05
        if (!EUI64_EXCEPT.contains(imodemType) && !EUI64_PREFIX.contains(getModemId().substring(0, 6))) {
            throw new Exception("MODEM EUI64 PATTERN[" + getModemId() + "] IS WRONG, PLEASE CHECK");
            // return -;
        }
        */

        if (getSvcType() == DataSVC.Electricity) {
            if (!DataFormat.checkMeterIdFormat(getMeterId())) {
                throw new Exception("Format of meter ID[" + getMeterId() + "] is wrong");
                // return data.length - pos + 1;
            }
        }

        /*
         * 미터 등록 여부, 미터와 모뎀 관계 생성 여부 확인 등 체크 로직 넣어야 함.
         * 집중기 리비전 번호 체크
         */
        MCU mcu = null;
        if(getModemType() != ModemType.MMIU){
	        mcu = validateMCU(mcuId, ns, ipAddr);	        		
        }
        
        Modem modem = null;
        if (getModemType() == ModemType.MMIU || getModemType() == ModemType.IEIU || getModemType() == ModemType.Converter_Ethernet) {
        	modem = validateModem(mcu, ns, ipAddr, protocolType); //MMIU,IEIU타입인 경우 IF45정보에 모뎀아이디는 널이고 집중기 필드에 실제 모뎀아이디값이 올라온다.
		} else {
			modem = validateModem(mcu, ns, null, null);
		}

        Meter meter = validateMeter(modem);
        
        validateRelation(meter, modem);

        String mcuRevision = null;
        McuType mcuType = McuType.UNKNOWN;

        if(mcu !=null && mcu.getSysID()!=null && mcu.getMcuType() != null) {
            mcuType = McuType.valueOf(mcu.getMcuType().getName());
        }
        
        log.debug("mcuId : " + mcuId + ", mcuType : " + mcuType + ", modemType : " + getModemType());
        
        /*
         * 집중기 리비전에 따라 처리하는 로직이 달랐으나 표준화됨.
        if (getModemType() != ModemType.MMIU && getModemType() != ModemType.IEIU && getModemType() != ModemType.Converter) {
            mcuRevision = mcu.getSysSwRevision();
            if (mcuRevision == null || "".equals(mcuRevision)) {
                throw new Exception("MCU[" + mcuId + "] REVISION IS INVALID CHECK!");
            }else{
                log.debug("mcuRevision["+mcuRevision+"]");
            }
        }
        */
        
        // check Kamstrup(Built in GPRS MMIU) PropertNo -> Meter ID Conversion
        if (getModemType() == ModemType.MMIU
            && getSvcType() == DataSVC.Electricity
            && getVendor() == MeterVendor.KAMSTRUP)
        {
            if(getMeterId().startsWith("00") && getMeterId().length() == 8)
            {
                String propertyNo = Integer.toString(Integer.parseInt(getMeterId()));

                // 스웨덴 레럼과 GE 캄스트럽 설치 방법이 다름.
                String tempMeterId = meterDao.findByCondition("installProperty", propertyNo).getMdsId();

                if(tempMeterId!=null && !tempMeterId.equals(""))
                {
                    log.debug("MMIU(Kamstrup GPRS) propertyNo["+getMeterId()+"] real meterId["+tempMeterId+"]");
                    setMeterId(tempMeterId);
                }else{
                    log.error("MMIU(Kamstrup GPRS) propertyNo["+getMeterId()+"] real meterId is not existed");
                }
            }else{
                log.debug("MMIU(Kamstrup GPRS) normal meterId["+getMeterId()+"]");
            }
        }

        return meter;
    }

    /**
     * 집중기 등록 여부 검증, 고압 또는 집단에너지 모뎀은 집중기가 없다.
     * 자동 등록이 안된다.
     * @param mcuId
     * @return
     * @throws Exception
     */
    private MCU validateMCU(String mcuId, String ns, String mcuIpAddress) throws Exception
    {
        String defaultSysHwVersion = "0.0"; 
        String defaultSysSwVersion = "0.0";
        String defaultProtocolVersion = "0102";
        String currentTime = TimeUtil.getCurrentTime();
        
    	if(getModemType() == ModemType.MMIU || getModemType() == ModemType.IEIU ||
    	        getModemType() == ModemType.Converter_Ethernet || getModemType() == ModemType.SubGiga) {
    		return null;//고압 타입일 경우 집중기 정보가 없기 때문에 그냥 널로 리턴하여 validation check를 하지 않는다.
    	}
    	
    	log.debug("### DCU[" + mcuId + "] DCU_IP_ADDRESS[" + ((mcuIpAddress == null || mcuIpAddress.equals("")) ? "null~!!" : mcuIpAddress) + "] validation check....");
    	
        MCU mcu = mcuDao.get(mcuId);
		Code mcuStatusNormal = codeDao.findByCondition("code", "1.1.4.1");
        if(getModemType() != null){
            log.debug("ModemType="+getModemType().name());
        }
        
        // DCU Install 이벤트를 받지 못하고 검침정보를 이용하여 저장하는경우(단, 멀티프레임으로 오는경우)에 필요함.
        if(ns == null || ns.equals("")){
        	ns = FMPProperty.getProperty("default.namespace.dcu", null);
        	log.debug("Mcu= " + mcuId + ", Using Default namespace = " + ns);
        	
        	if(ns == null){
        		throw new Exception("Can't found Namespace Information. please check default Namespace.");
        	}
        }
        
        if(mcu == null){
            mcu = new MCU();
            mcu.setSysID(mcuId);
            mcu.setMcuType(CommonConstants.getMcuTypeByName(McuType.DCU.name()));
            DeviceModel model = deviceModelDao.findByCondition("name", "NZC I211");
			if (model != null) {
				mcu.setDeviceModel(model);
	            log.debug("DCU["+mcuId+"] SET MODEL["+model.getName()+"]");
			}else {
				log.debug("DeviceModel is null");
			}
			mcu.setSysModel(model.getName());
			
			if(mcuIpAddress != null) {
				if(mcuIpAddress.contains(":")) {
					mcu.setIpv6Addr(IPUtil.format(mcuIpAddress.toUpperCase()));
					log.debug("Set IPv6 Address = " + mcu.getIpv6Addr());
				}else {
			        mcu.setIpAddr(IPUtil.format(mcuIpAddress.toUpperCase()));
			        log.debug("Set IPv4 Address = " + mcu.getIpAddr());
				}				
			}

            mcu.setNetworkStatus(1);
            mcu.setInstallDate(currentTime);
            if(mcuStatusNormal != null) {
                mcu.setMcuStatus(mcuStatusNormal);
            }

            mcu.setLastCommDate(currentTime);
            mcu.setSysLocalPort(new Integer(8000));
            //mcu.setSysPhoneNumber(sysPhoneNumber);
            if(CommonConstants.getProtocolByName(Protocol.GPRS.name())!=null) {
            	mcu.setProtocolType(CommonConstants.getProtocolByName(Protocol.GPRS.name()));
            }
            
            if(ns != null && !"".equals(ns)){
            	mcu.setNameSpace(ns);
            }
            mcu.setSysHwVersion(defaultSysHwVersion);
            mcu.setSysSwVersion(defaultSysSwVersion);
        	mcu.setProtocolVersion(defaultProtocolVersion);            
            
            List<Location> locations = null;
        	String defaultLocName = FMPProperty.getProperty("loc.default.id");
        	if(defaultLocName != null && !"".equals(defaultLocName)){
        		locations = locationDao.getLocationByName(defaultLocName);

        	}else{
        		locations = locationDao.getParents();
        	}
            if(locations != null && locations.size() > 0){
                mcu.setLocation(locations.get(0));
            }
            //Set Default Supplier
            Integer defaultSupplierId = FMPProperty.getProperty("supplier.default.id")!=null ? Integer.parseInt(FMPProperty.getProperty("supplier.default.id")):null;
            if(defaultSupplierId!=null && mcu.getSupplier()==null) {
            	if(supplierDao.get(defaultSupplierId)!=null) {
	            	log.info("MCU["+mcu.getSysID()+"] Set Default Supplier["+supplierDao.get(defaultSupplierId).getId()+"]");
	            	mcu.setSupplier(supplierDao.get(defaultSupplierId));
            	}else {
            		log.info("MCU["+mcu.getSysID()+"] Default Supplier["+defaultSupplierId+"] is not Exist In DB, Set First Supplier["+supplierDao.getAll().get(0).getId()+"]");
                	mcu.setSupplier(supplierDao.getAll().get(0));
            	}
            }else {
            	log.info("MCU["+mcu.getSysID()+"] Default Supplier is Not Exist In Properties, Set First Supplier["+supplierDao.getAll().get(0).getId()+"]");
            	mcu.setSupplier(supplierDao.getAll().get(0));
            }
            mcu = mcuDao.add_requires_new(mcu);
            //throw new Exception("Invalid MCU[" + mcuId + "]");
        } else {
        	if(mcu.getDeviceModel() == null) {
            	DeviceModel model = deviceModelDao.findByCondition("name", "NZC I211");
    			if (model != null) {
    				mcu.setDeviceModel(model);
    			}
//    			mcu.setSysModel("DCU-DUMMY");
                log.debug("DCU["+mcu.getSysID()+"] SET MODEL["+model.getName()+"]");
        	}
        	
            if(mcu.getSysHwVersion() == null || mcu.getSysHwVersion().equals("")){
                mcu.setSysHwVersion(defaultSysHwVersion);
            }
            if(mcu.getSysSwVersion() == null || mcu.getSysSwVersion().equals("")){
                mcu.setSysSwVersion(defaultSysSwVersion);
            }
            if(mcu.getProtocolVersion() == null || mcu.getProtocolVersion().equals("")){
            	mcu.setProtocolVersion(defaultProtocolVersion);
            }
            
            
			if(mcuIpAddress != null) {
				if(mcuIpAddress.contains(":")) {
					log.debug("Update IPv6 Address [" + mcu.getIpv6Addr() + "] to  [" + mcuIpAddress + "]");
					mcu.setIpv6Addr(IPUtil.format(mcuIpAddress.toUpperCase()));
				}else {
					log.debug("Update IPv4 Address [" + mcu.getIpAddr() + "] to [" + mcuIpAddress + "]");
			        mcu.setIpAddr(IPUtil.format(mcuIpAddress.toUpperCase()));
				}				
			}
            
        }

        return mcu;
    }

    /**
     * 모뎀 등록 여부 검증. 없으면 자동등록을 시도한다.
     * @param mcu
     * @param modemType
     * @param modemId
     * @return
     * @throws Exception
     */
    private Modem validateModem(MCU mcu, String ns, String ipAddr, String protocolType) throws Exception
    {
    	log.debug("### Modem[" + getModemId() + "] namespace [" + ns + "] validation check....");
    	
		if (mcu == null) {
			log.debug("Mcu SysId = NULL~!, Namespce = " + ns);
		} else {
			log.debug("Mcu SysId = " + mcu.getSysID() + ", Namespce = " + ns);
		}

        Modem modem = modemDao.get(getModemId());

        if (modem == null) {
        	
        	ModemType modemType = null;
        	
            switch (getModemType()) {
            	case ZRU : modemType = ModemType.ZRU;
            	break;
            	case ZEUPLS : modemType = ModemType.ZEUPLS;
            	break;
            	case Repeater : modemType = ModemType.Repeater;
            	break;
            	case MMIU : modemType = ModemType.MMIU;
            	break;
            	case IEIU : modemType = ModemType.IEIU;
            	break;
            	case ZEU_MBus : modemType = ModemType.ZEU_MBus;
            	break;
            	case Converter_Ethernet : modemType = ModemType.Converter_Ethernet;
            	break;
            	case HMU : modemType = ModemType.HMU;
            	break;
            	case SubGiga : modemType = ModemType.SubGiga;
            	break;
            	case PLC_HD : 
            	case PLC_G3 : 
            	case PLC_PRIME : 
            		modemType = ModemType.PLCIU;
            	break;
            	case LTE : modemType = ModemType.LTE;
            	break;
            	case ACD : modemType = ModemType.ACD;
            	break;
            	case IHD : modemType = ModemType.IHD;
            	break;
            	default : modemType = ModemType.ZRU;
            }

            Class clazz = Class.forName("com.aimir.model.device." + modemType.name());
            Object obj = clazz.newInstance();
            switch (modemType) {
            case ZRU : modem = (com.aimir.model.device.ZRU)obj;
            break;
            case ZEUPLS : modem = (com.aimir.model.device.ZEUPLS)obj;
            break;
            case Repeater : modem = (com.aimir.model.device.ZBRepeater)obj;
            break;
            case MMIU : modem = (com.aimir.model.device.MMIU)obj;
            break;
            case IEIU : modem = (com.aimir.model.device.IEIU)obj;
            break;
            case ZEU_MBus : modem = (com.aimir.model.device.ZEUMBus)obj;
            break;
            case Converter_Ethernet : modem = (com.aimir.model.device.Converter)obj;
            break;
            case HMU : modem = (com.aimir.model.device.HMU)obj;
            break;
            case SubGiga : modem = (com.aimir.model.device.SubGiga)obj;
            break;
            case PLC_G3 : 
            case PLC_PRIME :
            case PLC_HD :
            case PLCIU :
            	modem = (com.aimir.model.device.PLCIU)obj;
            break;
            case LTE : modem = (com.aimir.model.device.LTE)obj;
            break;
            case ACD : modem = (com.aimir.model.device.ACD)obj;
            break;
            case IHD : modem = (com.aimir.model.device.IHD)obj;
            break;
            default : modem = (com.aimir.model.device.ZRU)obj;
            }
            modem = (Modem)clazz.newInstance();
            modem.setDeviceSerial(getModemId());
            modem.setModemType(modemType.name());
            if (mcu != null)
                modem.setMcu(mcu);
            modem.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            modem.setCommState(1);
            modem.setLastLinkTime(modem.getInstallDate());
            if (ns == null || "".equals(ns))
                modem.setNameSpace("");
            else
                modem.setNameSpace(ns);
            modem.setProtocolVersion("0102");
            
			if (modem instanceof com.aimir.model.device.SubGiga && mcu != null && mcu.getIpv6Addr() != null) {
				((com.aimir.model.device.SubGiga) modem).setIpv6Address(Util.getIPv6(mcu.getIpv6Addr(), getModemId()));
				modem.setNameSpace(mcu.getNameSpace());
			} else if (modem instanceof com.aimir.model.device.PLCIU && mcu != null && mcu.getIpv6Addr() != null) {
				((com.aimir.model.device.PLCIU) modem).setIpv6Address(Util.getIPv6(mcu.getIpv6Addr(), getModemId()));
				modem.setNameSpace(mcu.getNameSpace());
			}
            
            if(mcu != null){
                modem.setSupplier(mcu.getSupplier());
            }else{
            	// GPRS모뎀인 경우 집중기 정보가 없기때문에 디폴트 공급사 정보를 설정한다.
            	modem.setSupplier(supplierDao.getAll().get(0));
            }

            if(mcu !=null && mcu.getLocation()!=null && modem.getLocation()==null) {
                modem.setLocation(mcu.getLocation());
            }
            else {
                String defaultLocName = FMPProperty.getProperty("loc.default.name");
                
                if(defaultLocName != null && !"".equals(defaultLocName)){               
                    if(locationDao.getLocationByName(StringUtil.toDB(defaultLocName))!=null 
                            && locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).size()>0) {
                        modem.setLocation(locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0));
                    }else {
                        modem.setLocation(locationDao.getAll().get(0));   
                    }
                }
            }
            
            if (modem.getModemType() == ModemType.ACD) {
                List<DeviceModel> models = deviceModelDao.getDeviceModelByName(modem.getSupplier().getId(), "NACD-E15");
                if (models.size() == 1)
                    modem.setModel(models.get(0));
            }
            else if (modem.getModemType() == ModemType.HMU) {
                List<DeviceModel> models = deviceModelDao.getDeviceModelByName(modem.getSupplier().getId(), "NHMU-Z1240");
                if (models.size() == 1)
                    modem.setModel(models.get(0));
            }
            else if (modem.getModemType() == ModemType.ZEUPLS) {
                if (ns == null || "".equals(ns)) {
                    switch (getSvcType()) {
                    case Gas :
                        List<DeviceModel> models = deviceModelDao.getDeviceModelByName(modem.getSupplier().getId(), "NAMR-W115SR");
                        if (models.size() == 1)
                            modem.setModel(models.get(0));
                        break;
                    case Water :
                        models = deviceModelDao.getDeviceModelByName(modem.getSupplier().getId(), "NAMR-G115SR");
                        if (models.size() == 1)
                            modem.setModel(models.get(0));
                        break;
                    }
                }
                else if (ns.equals("TS")) {
                    switch (getSvcType()) {
                    case Gas :
                        List<DeviceModel> models = deviceModelDao.getDeviceModelByName(modem.getSupplier().getId(), "NAMR-W106SR");
                        if (models.size() == 1)
                            modem.setModel(models.get(0));
                        break;
                    case Water :
                        models = deviceModelDao.getDeviceModelByName(modem.getSupplier().getId(), "NAMR-G106SR");
                        if (models.size() == 1)
                            modem.setModel(models.get(0));
                        break;
                    }
                }
            }else {
            	List<DeviceModel> models = deviceModelDao.getDeviceModelByName(modem.getSupplier().getId(), "MODEM-DUMMY");  // SriLanka CEB Dummy Modem
            	if (models.size() == 1) {
                    modem.setModel(models.get(0));
                    log.debug("MODEM["+modem.getDeviceSerial()+"] SET MODEL["+models.get(0).getName()+"]");
            	}  
            }
            
			if (protocolType == null || "".equals(protocolType)) {
				if (modem instanceof com.aimir.model.device.SubGiga) {
					protocolType = Protocol.IP.name();
				} else if (modem instanceof com.aimir.model.device.PLCIU) {
					protocolType = Protocol.IP.name();
				} else {
					protocolType = Protocol.ZigBee.name();
				}
			}
            
            modem.setProtocolType(protocolType);
            // IP 패턴 검사 후 MBB인지, Ethernet인지 구분해야 함.
            if (modem instanceof com.aimir.model.device.MMIU && ipAddr != null && !"".equals(ipAddr)) {
                ((com.aimir.model.device.MMIU)modem).setIpAddr(ipAddr);
            }
            // TODO Vendor, Model
            modemDao.add_requires_new(modem);
            // TODO 등록 이벤트
            EventUtil.sendEvent("Equipment Registration",
                    TargetClass.valueOf(modem.getModemType().name()),
                    modem.getDeviceSerial(),
                    new String[][] {{"message", "ModemType[" + modem.getModemType().name()+
                        "] MODEM[" + getModemId() + "] on saving metering value"}}
                    );
        }
        else {
            if (mcu!=null) {
                if ((modem.getMcu()!=null && !mcu.getSysID().equals(modem.getMcu().getSysID())) || modem.getMcu() == null) {
                    modem.setMcu(mcu);
                    // SP-687
                    modemDao.update_requires_new(modem);
                }

                if(modem.getSupplier() == null) modem.setSupplier(mcu.getSupplier());
            }
            if(modem.getModel() == null || modem.getModel().getName().equals("")) {
            	List<DeviceModel> models = deviceModelDao.getDeviceModelByName(modem.getSupplier().getId(), "MODEM-DUMMY");  // SriLanka CEB Dummy Modem
            	if (models.size() == 1) {
                    modem.setModel(models.get(0));
                    log.debug("MODEM["+modem.getDeviceSerial()+"] SET MODEL["+models.get(0).getName()+"]");
            	}
            }
        }

        // TODO 모뎀의 집중기와 인자로 받은 집중기를 비교하여 다르면 변경한다.
        return modem;
    }

    /**
     * 미터 등록 여부 검증. 없으면 자동등록을 시도한다.
     * @param modem
     * @return
     * @throws Exception
     */
	private Meter validateMeter(Modem modem) throws Exception {
    	String ns = modem.getNameSpace();
    	log.debug("### Meter[" + getMeterId() + "] namespace [" + ns + "] validation check....");
    	
		try {
			Supplier supplier = modem.getSupplier();
	 		int limitedCount = supplier.getLicenceMeterCount() == null? 0: supplier.getLicenceMeterCount();
			log.debug("Supplier = "+supplier.getDescr());
			log.debug("LimitedCount = "+limitedCount);
			log.debug("LicenceUse = "+supplier.getLicenceUse());
	 		
	 		String eventAlertName = "Excessive Number of Device Registration";
	 		String activatorType = "EnergyMeter"; //modem.getModemType().name();
	 		String activatorId = getMeterId();
	 		
			if (supplier.getLicenceUse() == 1) {
				int currentMeterCount = meterDao.getTotalMeterCount();
				log.debug("TotalMeterCount = "+currentMeterCount);
				
				if (!(currentMeterCount < limitedCount)) {
					//EventUtil.sendEvent(eventAlertName, activatorType, activatorId, supplier);
					
		            EventUtil.sendEvent(eventAlertName, TargetClass.EnergyMeter, activatorId, null);
					log.info("Excessive Number of Device Registration. (limited quantity : " + limitedCount + ", Current quantity : " + currentMeterCount +")");

					return null; 
				}
			}
		} catch (Exception e) {
			log.error("VailidateMeter Error - " + e.getMessage(), e);
		}
    	
        // HMU와 리피터는 계량기가 없기 때문에 예외로 처리한다.
        // HMU와 ACD에 가상 계량기를 넣을 수 있어서 조건에서 제외한다.
		log.debug("ModemType = "+modem.getModemType().name());
        if (modem.getModemType() != ModemType.Repeater) {
        	//수도 미터의 경우 검침데이터에 미터 정보가 올라오지 않으므로 DB에서 미터 정보를 가져오도록 함
            if(getMeterId() == null || "".equals(getMeterId())){
                Set<Meter> meters = modem.getMeter();
                if (meters.size() == 1) {
                    meter = meters.iterator().next();
                }
                else {
                    if (modem.getModemType() == ModemType.HMU) {
                        setMeterId(modem.getDeviceSerial());
                    }
                    // 관련된 미터가 없거나 2개 이상이면 예외, 단 MBus는 제외.
                    else if (modem.getModemType() != ModemType.ZEU_MBus)
                        log.warn("Modem[" + modem.getDeviceSerial() +
                                "] is related to " + meters.size() + " meter(s).");
                    // 미터가 없으면 경고만 하고 가상 미터 정보를 생성하여 처리할 수 있도록 한다.
                }
                /*
                for (Meter mm : meters) {
                    if (mm.getMdsId().equals(getMeterId())){
                        meter = mm;
                    }
                }
                */
			} else {
				meter = meterDao.get(getMeterId());
			}
		}

        if (meter == null) {
            log.debug("Meter Instance[" + getMeterId() + "] is NULL!!");

            if (getSvcType() == DataSVC.Unknown) {
                throw new Exception("SVCType is Unknown");
            }

            MeterType meterType = null;
            switch (getSvcType()) {
            case Electricity :
                meterType = MeterType.EnergyMeter;
                break;
            case Gas :
                meterType = MeterType.GasMeter;
                break;
            case Cooling :
            case Water :
                meterType = MeterType.WaterMeter;
                break;
            case Heating :
                meterType = MeterType.HeatMeter;
                break;
            case Volume :
                meterType = MeterType.VolumeCorrector;
                break;
            default :
                meterType = MeterType.EnergyMeter;
            }

            Class<?> clazz = Class.forName("com.aimir.model.device." + meterType.name());
            Object obj = clazz.newInstance();
            switch (meterType) {
            case EnergyMeter : meter = (com.aimir.model.device.EnergyMeter)obj;
            break;
            case GasMeter : meter = (com.aimir.model.device.GasMeter)obj;
            break;
            case WaterMeter : meter = (com.aimir.model.device.WaterMeter)obj;
            break;
            case HeatMeter : meter = (com.aimir.model.device.HeatMeter)obj;
            break;
            case VolumeCorrector : meter = (com.aimir.model.device.VolumeCorrector)obj;
            break;
            }
            meter.setMdsId(getMeterId());
            meter.setInstallDate(modem.getInstallDate());
            // meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));
            meter.setMeterType(CommonConstants.getMeterTypeByName(meterType.name()));
            meter.setSupplier(modem.getSupplier());
            if(modem.getMcu()!=null && modem.getMcu().getLocation()!=null && meter.getLocation()==null) {
            	meter.setLocation(modem.getMcu().getLocation());
            }
            else {
                String defaultLocName = FMPProperty.getProperty("loc.default.name");
                
                if(defaultLocName != null && !"".equals(defaultLocName)){               
                    if(locationDao.getLocationByName(StringUtil.toDB(defaultLocName))!=null 
                            && locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).size()>0) {
                        meter.setLocation(locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0));
                    }else {
                        meter.setLocation(locationDao.getAll().get(0));   
                    }
                }
            }
            meter.setPulseConstant(Double.parseDouble(FMPProperty.getProperty("meter.pulse.constant.hmu")));
            log.debug("Vendor[" + getVendor().getName() + "]");
            List<DeviceVendor> vendorList = deviceVendorDao.getDeviceVendorByName(meter.getSupplier().getId(), getVendor().getName());
            if (vendorList.size() == 1) {
                DeviceVendor vendor = vendorList.get(0);
                if (vendor.getDeviceModels().size() > 0) {
                    if (getVendor() == MeterVendor.KAMSTRUP && this.vendor == 0x29) {
                        for (DeviceModel dm : vendor.getDeviceModels()) {
                            if (dm.getName().contains("OmniPower")) {
                                meter.setModel(dm);
                                break;
                            }
                        }
                    } else {
                    	DeviceModel defaultModel = getDefaultModel(modem.getSupplier(), meter.getMdsId(), "meter", getVendor().getName());
                    	if(defaultModel != null) {
                    		meter.setModel(defaultModel);
                    	}
                    }
                    
                    if (meter.getModel() != null 
                    		&& meter.getModel().getMdsIdPattern() != null
                            && !Pattern.matches(meter.getModel().getMdsIdPattern(), meter.getMdsId()))
                        throw new Exception("Meter serial["+meter.getMdsId()+" pattern["+meter.getModel().getMdsIdPattern()+"] not matched");
                    
                    if (getVendor() == MeterVendor.KAMSTRUP && getMeterId().startsWith("192")) {
                        throw new Exception("Meter serial[" + meter.getMdsId() + "] is wrong");
                    }
                }
            } 
            
            /*meter.setModel(deviceModelDao.getDeviceModelByCode(
                    modem.getSupplier().getId(),
                    getModel(),
                    meter.getMeterType().getId()));
            meter.setPulseConstant(((MeterConfig)meter.getModel().getDeviceConfig()).getPulseConst());
            meter.setLpInterval(((MeterConfig)meter.getModel().getDeviceConfig()).getLpInterval());*/
            // default channel 설정 by elevas. 2012.06.27 HMU의 경우 계량기가 없기 때문에
            // 가상으로 생성하는데 아래 모델 정보 체크 중 에러가 발생하여 진행이 안됨.
            meter.setModem(modem);
            // MBus의 경우 모뎀 포트 설정을 해야 한다.
            // MBus파서에서 포트 설정을 하도록 한다.
            // MBus의 경우 헤더에 있는 미터아이디를 사용할 수 없다. 따라서, 파서를 탄 후 미터아이디가 설정된 것으로 생성한다.
            // meterDao.add(meter);
            
            // MBus의 경우 미터아이디를 헤더가 아닌 검침데이타 프레임에 있는 것으로 설정해야 한다.
            // 그래서, validateMeter() 에서 생성하지 못하고 파서를 통과한 후 생성한다.
            // ACD도 검침데이타가 있지만 미터와 관계가 없다. 생성된 미터는 벌크
            if (modem.getModemType() != ModemType.ACD
                    && modem.getModemType() != ModemType.Repeater
                    && meter.getMdsId() != null && !"".equals(meter.getMdsId())) {

            	Integer updateCnt = meterMapperDao.updateMappingMeterId(modem.getDeviceSerial(), meter.getMdsId());
            	log.info("updateCnt : " + updateCnt +", meterId : " + meter.getMdsId() +", modem seiral : " + modem.getDeviceSerial());
            	if(updateCnt != null && updateCnt > 0) {
                	MeterMapper mapper = meterMapperDao.getPrintedMeterIdByObisMeterId(modem.getDeviceSerial(), meter.getMdsId());
                	if(mapper != null) {                    	
                		log.info("mapper modem serial : " + modem.getDeviceSerial() +", obis meterId : " + meter.getMdsId() +", printed meterId : " + mapper.getMeterPrintedMdsId());
                		meter.setGs1(mapper.getMeterPrintedMdsId());
                	}
                }
            	            	
                meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));
                meterDao.add_requires_new(meter);
                
                try {
	                EventUtil.sendEvent("Equipment Registration",
	                        TargetClass.valueOf(meter.getMeterType().getName()),
	                        meter.getMdsId(),
	                        new String[][] {{"message",
	                            "MeterType[" + meter.getMeterType().getName() +
	                            "] MCU[" + modem.getMcuId()+
	                            "] MODEM[" + meter.getModem().getDeviceSerial()+ "] on saving metering value"}}
	                        );
                } catch (Exception e) {
                	log.debug("Meter Equipment Registration event save error - " + e.getMessage(), e);
                	
                }
                // 모델 정보가 없기 때문에 진행이 안된다. 따라서 널로 반환하고 미터가 정상적으로 등록될 수 있도록 한다. 2012.07.09 by elevas
                // return null;
            }
        } else {
        	Integer updateCnt = meterMapperDao.updateMappingMeterId(modem.getDeviceSerial(), meter.getMdsId());
        	log.info("updateCnt : " + updateCnt +", meterId : " + meter.getMdsId() +", modem seiral : " + modem.getDeviceSerial());
        	if(updateCnt != null && updateCnt > 0) {
            	MeterMapper mapper = meterMapperDao.getPrintedMeterIdByObisMeterId(modem.getDeviceSerial(), meter.getMdsId());
            	if(mapper != null) {                    	
            		log.info("mapper modem serial : " + modem.getDeviceSerial() +", obis meterId : " + meter.getMdsId() +", printed meterId : " + mapper.getMeterPrintedMdsId());
            		meter.setGs1(mapper.getMeterPrintedMdsId());
            	}
            }
        	 
        	if(meter.getModel() == null || meter.getModel().getName().equals("")) {
        		DeviceModel defaultModel = getDefaultModel(modem.getSupplier(), meter.getMdsId(), "meter", getVendor().getName());
            	if(defaultModel != null) {
            		log.info("meter : " + meter.getMdsId() +", default model : " + defaultModel.toString());
            		meter.setModel(defaultModel);
            	}
        	}
        }

        return meter;
    }
	
	/*
	 * deviceType value : dcu, modem, meter
	 */
	private DeviceModel getDefaultModel(Supplier supplier, String meterId, String deviceType, String vendor) {
		String propertyVendor = FMPProperty.getProperty("vendor");
		
		String propStr = "install." + deviceType.toLowerCase() +"." + vendor.toLowerCase() + ".model.name";
		String defaultModel = FMPProperty.getProperty(propStr);
		log.debug("propStr : " +propStr+", defaultModel : " + defaultModel+", supplierId : "+supplier.getId());
		
		if(vendor != null && !vendor.isEmpty() && propertyVendor.contains(vendor)) {
			List<DeviceModel> models = deviceModelDao.getDeviceModelByName(supplier.getId(), defaultModel);
			if (models.size() > 0) {
				log.debug("METER["+meterId+"] SET MODEL["+models.get(0).getName()+"]");
				return models.get(0);
			}
		} else {
			List<DeviceModel> dummyModels = deviceModelDao.getDeviceModelByName(supplier.getId(), "METER-DUMMY");
        	if (dummyModels.size() > 0) { 
        		log.debug("METER["+meterId+"] SET MODEL["+dummyModels.get(0).getName()+"]");
        		return dummyModels.get(0);
        	}
		}
		
		log.debug("METER["+meterId+"] SET MODEL NULL");
		return null;
	}
    
    private DeviceModel makeDefaultModel() {
        DeviceModel dm = new DeviceModel();
        MeterConfig mc = new MeterConfig();
        ChannelConfig cc = new ChannelConfig();
        DisplayChannel dc = new DisplayChannel();
        dc.setChMethod(ChannelCalcMethod.SUM.name());
        cc.setChannel(dc);
        Set<ChannelConfig> chset = new HashSet<ChannelConfig>();
        chset.add(cc);
        mc.setChannel(chset);
        dm.setDeviceConfig(mc);
        return dm;
    }

    private boolean validateRelation(Meter meter, Modem modem) throws Exception
    {
        if (meter != null) {
            Modem orgModem = meter.getModem();
    
            // 미터의 모뎀이 널이거나 다른 모뎀이면
            // 다른 모뎀이면 
            if (orgModem == null) {
            	log.debug("orgModem is null");

            	// 모뎀 타입과 상관없이 모든 미터는 MBUS가 될 수가 있다고 본다. 
                // 모뎀은 반드시 마스터 미터 한개와 매핑한다. 마스터 미터 조건은 모뎀포트가 널이거나 0인 경우,
                // 슬레이브 미터의 모뎀 포트는 반드시 1이상의 값을 가져야 한다.
                Set<Meter> meters = new HashSet<Meter>();
                Set<Meter> _modemMeters = modem.getMeter();
                Meter orgMeter = null;
                
                boolean isExist = false;
                for (Meter m : _modemMeters) {
                    if (!m.getMdsId().equals(meter.getMdsId())) {
                        if (m.getModemPort() == null || m.getModemPort() == 0) {
                        	log.debug("Old meter update modem_id to null. Meter[" + m.getMdsId() + "]");
                            orgMeter = m;
                            m.setModem(null);
                            // SP-687
                            meterDao.update_requires_new(m);
                            continue;
                        }
                    }
                    else
                        isExist = true;
                    
                    meters.add(m);
                }
                if (!isExist) meters.add(meter);
                
                modem.setMeter(meters);
                // UPDATE START SP-917
//              // SP-687
//              modemDao.update_requires_new(modem);

                for (Meter m : modem.getMeter()) {
                	m.setModem(modem);
                	meterDao.update_requires_new(m);
                }
                // UPDATE END SP-917
              
                // Meter Replacement
                if (orgMeter != null) {
                    EventUtil.sendEvent("Equipment Replacement",
                            TargetClass.valueOf(meter.getMeterType().getName()),
                            modem.getDeviceSerial(),
                            new String[][] {{"equipType", meter.getMeterType().getName()},
                                            {"oldEquipID", orgMeter.getMdsId()},
                                            {"newEquipID", meter.getMdsId()}
                            });
                }
            }
            else if (!orgModem.getDeviceSerial().equals(modem.getDeviceSerial())) {
                // SP-825
            	log.debug("Different modem. orgModem[" + orgModem.getDeviceSerial() + "] newModem[" + modem.getDeviceSerial() + "]");
            	ArrayList<Integer> meterIdList = new ArrayList<Integer>();
                for (Meter m : modem.getMeter()) {
                	meterIdList.add(m.getId());
                	log.debug("Update modem_id to null. Meter[" + m.getMdsId() + "]");
                }

                modem.setMeter(orgModem.getMeter());
                // DELETE START SP-917
//                // SP-687
//                modemDao.update_requires_new(modem);
                // DELETE END SP-917
/*
                // 모뎀 교체로 처리한다.
                orgModem.setMeter(null);
                // SP-687
                modemDao.update_requires_new(orgModem);
*/               
                for (Meter m : modem.getMeter()) {
                    m.setModem(modem);
                    meterDao.update_requires_new(m);
                }

                // SP-825
                for (Integer m : meterIdList) {
                	// UPDATE START SP-917  
//                  meterDao.updateModemIdNull(m);
                	Meter updMeter = meterDao.get(m);
                	updMeter.setModem(null);
                	meterDao.update_requires_new(updMeter);
                	// UPDATE END SP-917  
                }
                
                String tempModemType = null;
                if(modem.getModemType() == ModemType.PLC_G3 
                		|| modem.getModemType() == ModemType.PLC_HD 
                		|| modem.getModemType() == ModemType.PLC_PRIME 
                		|| modem.getModemType() == ModemType.PLCIU) {
                	tempModemType = TargetClass.PLCIU.name();	
                }else {
                	tempModemType = modem.getModemType().name();
                }
                
                EventUtil.sendEvent("Equipment Replacement",
                        TargetClass.valueOf(tempModemType),
                        modem.getDeviceSerial(),
                        new String[][] {{"equipType", tempModemType},
                                        {"oldEquipID", orgModem.getDeviceSerial()},
                                        {"newEquipID", modem.getDeviceSerial()}
                        });
            }
            // INSERT START SP-825
            else if (orgModem.getDeviceSerial().equals(modem.getDeviceSerial())) {
            	// modem move to new meter
            	// already duplicate 
            	
                Set<Meter> updateMeters = new HashSet<Meter>();
                Set<Meter> _modemMeters = modem.getMeter();
                boolean isUpdate = false;
                for (Meter m : _modemMeters) {
                    if (!m.getMdsId().equals(meter.getMdsId())) {
                        if (m.getModemPort() == null || m.getModemPort() == 0) {
                        	isUpdate = true;
                        }
                        updateMeters.add(m);
                    }
                }
                
                if (isUpdate) {
                    for (Meter m : updateMeters) {
                    	log.debug("Update modem_id to null. Meter[" + m.getMdsId() + "]");
                    	// UPDATE START SP-917 
//                    	meterDao.updateModemIdNull(m.getId());
                    	m.setModem(null);
                    	meterDao.update_requires_new(m);
                    	// UPDATE END SP-917              
                    }                	
                }
            	
                return true;
            }            
            // INSERT END SP-825
            
            // DELETE START SP-917
//            meter.setModem(modem);
//            // SP-687
//            meterDao.update_requires_new(meter);
            // DELETE END SP-917
            // TODO 관계 검증
        }
        return true;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("MeasurementDataEntry[")
        .append("(modemId=").append(getModemId()).append("),")
        .append("(modemType=").append(getModemType()).append("),")
        .append("(svcType=").append(getSvcType()).append("),")
        .append("(vendor=").append(getVendor()).append(')')
        .append("(dataCnt=").append(getDataCnt()).append(")\n");

        MeasurementData[] mds = getMeasurementData();
        for(int i = 0 ; i < mds.length ; i++) {
            sb.append(mds[i].toString());
        }

        return sb.toString();
    }
}
