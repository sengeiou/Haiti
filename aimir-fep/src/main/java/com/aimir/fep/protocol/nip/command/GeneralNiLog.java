package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.CertificateStatus;
import com.aimir.fep.protocol.nip.command.ResponseResult.NetworkSpeedType;
import com.aimir.fep.protocol.nip.command.ResponseResult.ObisStatus;
import com.aimir.fep.protocol.nip.command.ResponseResult.PowerSourceType;
import com.aimir.fep.protocol.nip.command.ResponseResult.SnmpTrapStatus;
import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.command.ResponseResult.TypeMain;
import com.aimir.fep.protocol.nip.command.ResponseResult.TypeType;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class GeneralNiLog extends AbstractCommand{
    private byte[] bx;
    public void setBx(byte[] bx) {
        this.bx = bx;
    }
    public byte[] getBx() {
        return this.bx;
    }

    public GeneralNiLog() {
        super(new byte[] {(byte)0x10, (byte)0x06});
    }

    @Override
    public Command get(HashMap info) throws Exception {
        Command command = new Command();
        return command;
    }
    
    @Override
    public void decode(byte[] bx) {
    	this.bx = bx;
    }
    
    @Override
    public String toString() {
        return "[GeneralNiLog]";	   
    }
 
    @Override
    public Command get() throws Exception{return null;}
    @Override
    public Command set() throws Exception{return null;}
    @Override
    public Command set(HashMap p) throws Exception{return null;}
    @Override
    public Command trap() throws Exception{return null;}
    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
    }

    public String createResponseStr(String attrID, String requestType){
    	NIAttributeId aid = NIAttributeId.getItem(Hex.encode(attrID));
    	byte[] bx = this.bx;
    	String Response = "";
        byte[] b = new byte[2];

        switch (aid) {
        case ResetModem:
     		if(requestType.equals("SET")){
     			Response = this.getNoResponseStr();
     		}
            break;
        case UploadMeteringData:
     		if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case FactorySetting:
     		if(requestType.equals("SET")){
     			Response = this.getNoResponseStr();
     		}
            break;
        case ReAuthenticate:
     		if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case WatchdogTest:
     		if(requestType.equals("SET")){
     			Response = this.getNoResponseStr();
     		}
            break;
        case RealTimeMetering:
     		if(requestType.equals("SET")){
     			RealTimeMetering rtm = new RealTimeMetering();
     			rtm.decode(bx);
     			Response = rtm.toString();
     		}
            break;
        case ModemInformation:
        	if(requestType.equals("GET")){
     			ModemInformation mi = new ModemInformation();
     			mi.decode(bx);
     			Response = mi.toString2();
     		}
            break;
        case ModemStatus:
        	if(requestType.equals("GET")){
     			ModemStatus ms = new ModemStatus();
     			ms.decode(bx);
     			Response = ms.toString2();
     		}
            break;
        case MeterInformation:
        	if(requestType.equals("GET")){
        		MeterInformation mi = new MeterInformation();
        		mi.decode(bx);
        		Response = mi.toString2();
        	}
            break;
        case ModemEventLog:
        	if(requestType.equals("GET")){
        		ModemEventLog mel = new ModemEventLog();
        		mel.decode2(bx);
        		Response = mel.toString2();
        	}
            break;
        case CloneOnOff:
        	if(requestType.equals("GET") || requestType.equals("SET")){
     			CloneOnOff coo = new CloneOnOff();
     			try {
					coo.decode(bx);
	     			Response = coo.toString2();
				} catch (Exception e) {
				}
     		}
            break;
        case ModemTime:
     		if(requestType.equals("GET")){ 	
     			Response = this.getDateFormatToString(bx, "DateFormat: ");
     		}
     		else if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case ModemResetTime:
     		if(requestType.equals("GET")){
     		} 
     		else if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case ModemMode:
     		if(requestType.equals("GET")){
        		ModemMode mm = new ModemMode();
        		mm.decode2(bx);
        		Response = mm.toString2();
     		}
     		else if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case MeteringInterval:
     		if(requestType.equals("GET")){
     			Response = this.getIntTo2ByteDataToString(bx, 2, "Metering Interval: ");
     		} 
     		else if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case ModemTXPower:
     		if(requestType.equals("GET")){
     			Response = this.getHexaDataToString(bx, "TX Power: ");
     		} 
     		else if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case Form_JoinNetwork:
     		if(requestType.equals("SET")){
     			Response = this.getNoResponseStr();
     		}
            break;
        case NetworkSpeed:
     		if(requestType.equals("GET")){
     			Response = this.getNetworkSpeedTypeStr(bx, "Network Speed: ");
     		} 
     		else if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case ModemIpInformation:
        	if(requestType.equals("GET") || requestType.equals("SET")){
     			ModemIpInformation mii = new ModemIpInformation();
     			mii.decode2(bx);
     			Response = mii.toString2();
     		}
            break;
        case ModemPortInformation:
        	if(requestType.equals("GET") || requestType.equals("SET")){
     			ModemPortInformation mpi = new ModemPortInformation();
     			mpi.decode2(bx);
     			Response = mpi.toString2();
     		}
            break;
        case Alarm_EventCommandON_OFF:
        	if(requestType.equals("GET") || requestType.equals("SET")){
     			AlarmEventCommandOnOff aec = new AlarmEventCommandOnOff();
     			aec.decode(bx);
     			Response = aec.toString2();
     		}
            break;
        case MeterBaud:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			Response = this.getIntTo4ByteDataToString(bx, 4, "Baudrate: ");
     		}
            break;
        case TransmitFrequency:
     		if(requestType.equals("GET")){
     			Response = this.getIntTo2ByteDataToString(bx, 2, "Transmit Frequency: ");
     		} 
     		else if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case RetryCount:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			Response = this.getIntToByteDataToString(bx, 1, "Retry Count: ");
     		}
            break;
        case SnmpTrapOnOff:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			Response = this.getSnmpTrapResultStr(bx, "Status: ");
     		}
            break;
        case RawROMAccess:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			RawRomAccess rra = new RawRomAccess();
     			rra.decode2(bx);
     			Response = rra.toString2();
     		}
            break;
        case OBISListup:
     		if(requestType.equals("GET")){
     			ObisListUp ol = new ObisListUp();
     			ol.decode2(bx);
     			Response = ol.toString2();
     		}
            break;
        case OBISAdd:
     		if(requestType.equals("SET")){
     			Response = this.getObisResultStr(bx, "Result: ");
     		}
            break;
        case OBISRemove:
     		if(requestType.equals("SET")){
     			Response = this.getObisResultStr(bx, "Result: ");
     		}
            break;
        case OBISListChange:
     		if(requestType.equals("SET")){
     			Response = this.getObisResultStr(bx, "Result: ");
     		}
            break;
        case TestConfiguration:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			TestConfiguration tc = new TestConfiguration();
     			tc.decode2(bx);
     			Response = tc.toString2();
     		}
            break;
        case TestDataUpload:
     		if(requestType.equals("GET")){
     			TestDataUpload tdu = new TestDataUpload();
     			tdu.decode2(bx);
     			Response = tdu.toString2();
     		}
            break;
        case CoordinatorInformation:
     		if(requestType.equals("GET")){
     			CoordinatorInformation ci = new CoordinatorInformation();
     			ci.decode2(bx);
     			Response = ci.toString2();
     		}
            break;
        case BootloaderJump:
     		if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case NetworkIPv6Prefix:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			Response = this.getHexaDataToString(bx, 6, "Network Ipv6 Prefix: ");
     		}
            break;
        case JoinBackoffTimer:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			Response = this.getIntTo4ByteDataToString(bx, 4, "Join Timer: ");
     		}
            break;
        case AuthBackoffTimer:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			Response = this.getIntTo4ByteDataToString(bx, 4, "Auth Timer: ");
     		}
            break;
        case MeterSharedKey:
     		if(requestType.equals("GET")){
     			MeterSharedKey shk = new MeterSharedKey();
     			shk.decode(bx);
     			Response = shk.toString2();
     		}
     		else if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case NullBypassOpen:
        	if(requestType.equals("GET")){
     			NullBypassOpen nbo = new NullBypassOpen();
     			nbo.decode(bx);
     			Response = nbo.toString2("GET");
     		}
        	else if(requestType.equals("SET")){
     			NullBypassOpen nbo = new NullBypassOpen();
     			nbo.decode(bx);
     			Response = nbo.toString2("SET");
     		}
            break;
        case NullBypassClose:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			NullBypassClose nbc = new NullBypassClose();
     			nbc.decode(bx);
     			Response = nbc.toString2();
     		}
            break;
        case ROMRead:
        	if(requestType.equals("GET")){
     			RomRead rr = new RomRead();
     			rr.decode2(bx);
     			Response = rr.toString2();
     		}
            break;
        case GatheringDataAction:
        	if(requestType.equals("SET")){
        		GatheringDataAction gda = new GatheringDataAction();
     			gda.decode(bx);
     			Response = gda.toString2();
     		}
            break;
        case GatheringDataPoll:
        	if(requestType.equals("GET")){
        		GatheringDataPoll gdp = new GatheringDataPoll();
     			gdp.decode2(bx);
     			Response = gdp.toString2();
     		}
            break;
        case MeteringDataRequest:
     		if(requestType.equals("GET")){
     			MeteringDataRequest mdr = new MeteringDataRequest();
     			mdr.decode(bx);
     			Response = mdr.toString();
     		}
            break;
        case ParentNodeInfo:
     		if(requestType.equals("GET")){
     			ParentNodeInfo pni = new ParentNodeInfo();
     			pni.decode2(bx);
     			Response = pni.toString2();
     		}
            break;
        case HopCount:
     		if(requestType.equals("GET")){
     			Response = this.getHopCountStr(bx, b, "Hop Count: ");
     		}
            break;
        case HopNeighborList:
     		if(requestType.equals("GET")){
     			HopNeighborList hnl = new HopNeighborList();
     			hnl.decode2(bx);
     			Response = hnl.toString2();
     		}
            break;
        case ChildNodeList:
        	if(requestType.equals("GET")){
        		ChildNodeList cnl = new ChildNodeList();
        		cnl.decode2(bx);
        		Response = cnl.toString2();
        	}
            break;
        case NodeAuthorization:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			NodeAuthorization na = new NodeAuthorization();
     			na.decode2(bx);
     			Response = na.toString2();
     		}
            break;
        case RollbackImage:
     		if(requestType.equals("GET")){
     			Response = this.getMultiTwoDataToString(bx, 2, 2, "Current Version: ", "Previous Version: ", "hexa", "hexa");
     		}
     		else if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case UploadMeteringData_UploadOnly:
     		if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case ModemDTLSHandshakeStatus:
     		if(requestType.equals("GET")){
     			Response = this.getModemDTLSHandshakeStatusToString(bx, 1, "GET");
     		}
     		else if(requestType.equals("SET")){
     			Response = this.getModemDTLSHandshakeStatusToString(bx, 1, "SET");
     		}
            break;
        case FWImageInformation:
     		if(requestType.equals("GET")){
     			Response = this.getFwImageInformationToString(bx, 44);
     		}
            break;
        case BootloaderInformation:
     		if(requestType.equals("GET")){
     			Response = this.getHexaDataToString(bx, 2, "Version: ");
     		}
            break;
        case ModemScheduleRun:
     		if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case CoordinatorEUI:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			Response = this.getMultiTwoDataToString(bx, 1, 8, "OTP index: ", "Coordinator EUI: ", "hexa", "hexa");
     		}
            break;
        case CoordinatorBroadcastConfiguration:
     		if(requestType.equals("GET")){
     			Response = this.getCoordinatorBroadcastConfigurationToString(bx, 7);
     		} 
     		else if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case NetworkKey:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			Response = this.getNetworkKeyToString(bx, 3);
     		}
            break;
        case CoordinatorOne_TimeBroadcast:
     		if(requestType.equals("SET")){
     			Response = this.getStatusStr(bx, b, "Status: ");
     		}
            break;
        case Networkfilterrssivalue:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			Response = this.getNetworkfilterRssiToString(bx, 1, "RSSI: ");
     		}
            break;
        case CertificateUpdate:
     		if(requestType.equals("GET")){
     			Response = this.getMultiTwoDataToString(bx, 2, bx.length-2, "Cert Payload Length: ", "Cert Payload: ", "decimal", "hexa");
     		}
     		else if(requestType.equals("SET")){
     			Response = this.getCertificateStatusStr(bx, "RSSI: ");
     		}
            break;
        case FactorySetting_Common:
     		if(requestType.equals("GET")){
     			Response = this.getIntToByteDataToString(bx, 1, "Status: ");
     		}
     		else if(requestType.equals("SET")){
     			Response = this.getNoResponseStr();
     		}
            break;
        case Reset:
     		if(requestType.equals("GET")){
     			Response = this.getIntToByteDataToString(bx, 1, "Status: ");
     		}
     		else if(requestType.equals("SET")){
     			Response = this.getNoResponseStr();
     		}
            break;
        case UtcTime:
     		if(requestType.equals("GET")){ 	
     			Response = this.getDateFormatToString(bx, "DateFormat: ");
     		}
            break;
        case TimeZone:
     		if(requestType.equals("GET")){
     			Response = this.getIntToByteDataToString(bx, 1, "Offset: ");
     		}
            break;
        case MeterSerialNumber:
     		if(requestType.equals("GET") ){
     			Response = this.getTextDataToString(bx, "Number: ");
     		}
            break;
        case MeterManufactureNumber:
     		if(requestType.equals("GET") ){
     			Response = this.getTextDataToString(bx, "Number: ");
     		}
            break;
        case CustomerNumber:
     		if(requestType.equals("GET") ){
     			Response = this.getTextDataToString(bx, "Number: ");
     		}
            break;
        case ModelName:
     		if(requestType.equals("GET") ){
     			Response = this.getTextDataToString(bx, "Name: ");
     		}
            break;
        case HWVersion:
     		if(requestType.equals("GET") ){
     			Response = this.getTextDataToString(bx, "Version: ");
     		}
            break;
        case SWVersion:
     		if(requestType.equals("GET") ){
     			Response = this.getTextDataToString(bx, "Version: ");
     		}
            break;
        case MeterStatus:
     		if(requestType.equals("GET")){
     			Response = this.getMeterStatusToString(bx);
     		}
            break;
        case LastUpdateTime:
     		if(requestType.equals("GET")){ 	
     			Response = this.getDateFormatToString(bx, "DateFormat: ");
     		}
            break;
        case LastCommTime:
     		if(requestType.equals("GET")){ 	
     			Response = this.getDateFormatToString(bx, "DateFormat: ");
     		}
            break;
        case LPChannelCount:
     		if(requestType.equals("GET")){
     			Response = this.getIntToByteDataToString(bx, 1, "Count: ");
     		} 
            break;
        case LPInterval:
     		if(requestType.equals("GET")){
     			Response = this.getMultiTwoDataToString(bx, 1, 4, "Tag: ", "Value: ", "decimal", "decimal");
     		}
            break;
        case CumulativeActivePower:
     		if(requestType.equals("GET")){
     			Response = this.getMultiTwoDataToString(bx, 1, 4, "Tag: ", "Value: ", "decimal", "decimal");
     		}
            break;
        case CumulativeActivePowerTime:
     		if(requestType.equals("GET")){
     			Response = this.getDateFormatToString(bx, "DateFormat: ");
     		}
            break;
        case CT:
     		if(requestType.equals("GET")){
     			Response = this.getMultiTwoDataToString(bx, 1, 4, "Tag: ", "Value: ", "decimal", "decimal");
     		}
            break;
        case PT:
     		if(requestType.equals("GET")){
     			Response = this.getMultiTwoDataToString(bx, 1, 4, "Tag: ", "Value: ", "decimal", "decimal");
     		}
            break;
        case TransformerRatio:
     		if(requestType.equals("GET")){
     			Response = this.getMultiTwoDataToString(bx, 1, 4, "Tag: ", "Value: ", "decimal", "decimal");
     		}
            break;
        case PhaseConfiguration:
     		if(requestType.equals("GET")){
     			Response = this.getTextDataToString(bx, "Phase: ");
     		}
            break;
        case SwitchStatus:
     		if(requestType.equals("GET")){
     			Response = this.getSwitchStatusToString(bx, 2);
     		}
            break;
        case Frequency_Electric:
     		if(requestType.equals("GET")){
     			Response = this.getMultiTwoDataToString(bx, 1, 4, "Tag: ", "Value: ", "decimal", "decimal");
     		}
            break;
        case VA_SF:
     		if(requestType.equals("GET")){
     			Response = this.getMultiTwoDataToString(bx, 1, 4, "Tag: ", "Value: ", "decimal", "decimal");
     		}
            break;
        case VAH_SF:
     		if(requestType.equals("GET")){
     			Response = this.getMultiTwoDataToString(bx, 1, 4, "Tag: ", "Value: ", "decimal", "decimal");
     		}
            break;
        case DISP_SCALAR:
     		if(requestType.equals("GET")){
     			Response = this.getIntHexaDataToString(bx, 1, "Value: ");
     		}
            break;
        case DISP_MULTIPLIER:
     		if(requestType.equals("GET")){
     			Response = this.getIntToByteDataToString(bx, 1, "Value: ");
     		}
            break;
        case PrimaryPowerSourceType:
     		if(requestType.equals("GET")){
     			Response = this.getPowerSourceTypeStr(bx, "Type: ", "primary");
     		}
            break;
        case SecondaryPowerSourceType:
     		if(requestType.equals("GET")){
     			Response = this.getPowerSourceTypeStr(bx, "Type: ", "secondary");
     		}
            break;
        case ResetCount:
     		if(requestType.equals("GET")){
     			Response = this.getIntTo2ByteDataToString(bx, 2, "Count: ");
     		}
            break;
        case ResetReason:
     		if(requestType.equals("GET")){
     			Response = this.getResetReasonToString(bx, 1, "Reason: ");
     		}
            break;
        case OperationTime:
     		if(requestType.equals("GET")){
     			Response = this.getIntTo4ByteDataToString(bx, 4, "Count: ");
     		}
            break;
        case ResetSchedule:
     		if(requestType.equals("GET")){
     			Response = this.getResetScheduleToString(bx, 1, "Reset Schedule: ");
     		}
            break;
        case Type_Main:
     		if(requestType.equals("GET")){
     			Response = this.getTypeMainStr(bx, "Type: ");
     		}
            break;
        case Type_Type:
     		if(requestType.equals("GET")){
     			Response = this.getTypeTypeStr(bx, "Type: ");
     		}
            break;
        case InterfaceIPv6Address:
     		if(requestType.equals("GET")){
     			Response = this.getHexaDataToString(bx, 16, "Address: ");
     		}
            break;
        case IPv6Address:
     		if(requestType.equals("GET")){
     			Response = this.getHexaDataToString(bx, 16, "Address: ");
     		}
            break;
        case InterfaceListenPort:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			Response = this.getIntHexaDataToString(bx, 2, "Port: ");
     		}
            break;
        case NetworkListenPort:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			Response = this.getIntHexaDataToString(bx, 2, "Port: ");
     		}
            break;
        case Frequency_6LoWPAN:
     		if(requestType.equals("GET")){
     			Response = this.getFrequencyToString(bx, 6);
     		}
            break;
        case Bandwidth:
     		if(requestType.equals("GET")){
     			Response = this.getBandWidhToString(bx, 2, "Bandwidth: ");
     		}
            break;
        case BaseStationAddress:
     		if(requestType.equals("GET")){
     			Response = this.getHexaDataToString(bx, 16, "Address: ");
     		}
            break;
        case APPKey:
     		if(requestType.equals("GET")){
     			Response = this.getHexaDataToString(bx, 16, "Key: ");
     		}
            break;
        case HopstoBaseStation:
     		if(requestType.equals("GET")){
     			Response = this.getHopsStr(bx, b, "Hops: ");
     		}
            break;
        case EUI64:
     		if(requestType.equals("GET")){
     			Response = this.getHexaDataToString(bx, 8, "EUI 64: ");
     		}
            break;
        case ListenPort:
     		if(requestType.equals("GET") || requestType.equals("SET")){
     			Response = this.getIntHexaDataToString(bx, 2, "Port: ");
     		}
            break;
        case MaxHop:
     		if(requestType.equals("GET")){
     			Response = this.getIntHexaDataToString(bx, 1, "Hop: ");
     		}
            break;
        case Meteringschedule:
     		if(requestType.equals("GET")){
     			Response = this.getIntTo2ByteDataToString(bx, 2, "Second: ");
     		}
     		else if(requestType.equals("SET")){
     			Response = this.getNoResponseStr();
     		}
            break;
        case LPUploadschedule:
     		if(requestType.equals("GET")){
     			Response = this.getIntHexaDataToString(bx, 2, "Schedule: ");
     		}
     		else if(requestType.equals("SET")){
     			Response = this.getNoResponseStr();
     		}
            break;
        case EventType:
     		if(requestType.equals("GET")){
     			Response = this.getEventTypeToString(bx);
     		}
            break;
        case NetworkChangeInformation:
     		if(requestType.equals("GET")){
     			Response = this.getNetworkChangeInformationToString(bx);
     		}
            break;            
        case MBBModuleInformation:
     		if(requestType.equals("GET")){
     			Response = this.getModuleInfo(bx);
     		}
            break;
        case InitiateModuleUpgrade:
     		if(requestType.equals("SET")){
     			Response = this.getNoResponseStr();
     		}
            break;
        default:
            Response = Hex.decode(this.bx);
            break;
        }

        b = new byte[bx.length];
        System.arraycopy(bx, 0, b, 0, bx.length);
        String binStr = "Binary Data: "+ Hex.decode(b) +"\n";
        return binStr +Response;
    }
    
    private String getModuleInfo(byte[] bx) {

        MBBModuleInformation frameInfo = new MBBModuleInformation();
        frameInfo.decode(bx);
        return frameInfo.toString();
    }

    private String getStatusStr(byte[] bx, byte[] b, String field){
        StringBuffer status = new StringBuffer();
        status.append(field);
        Status st = this.getStatus(bx, b);
        byte[] bTmp = new byte[bx.length];
        System.arraycopy(bx, 0, bTmp, 0, bTmp.length);

        if(st == null){
			int code = DataUtil.getIntTo2Byte(bTmp);
			status.append("0x" + String.format("%04x", code));
        }
        else {
        	switch(st){
        	case Success:
        		status.append("Success(0x0000)");
    			break;
        	case FormatError:
        		status.append("Format Error(0x1001)");
    			break;
        	case ParameterError:
        		status.append("Parameter Error(0x1002)");
    			break;
        	case ValueOverflow:
        		status.append("Value Overflow Error(0x1003)");
    			break;
        	case InvalidAttrId:
        		status.append("Invalid Attribute Id(0x1004)");
    			break;
        	case AuthorizationError:
        		status.append("Authorization Error(0x10005)");
    			break;
        	case NoDataError:
        		status.append("No Data Error(0x1006)");
    			break;
        	case MeteringBusy:
        		status.append("Metering Busy(0x2000)");
    			break;
        	case Unknown:
        		status.append("Unknown(0xFF00)");
    			break;
        	}
        }
    	return status.toString();
    }

    private Status getStatus(byte[] bx, byte[] b){
    	Status status = null;

    	System.arraycopy(bx, 0, b, 0, b.length);
        for (Status s : Status.values()) {
            if (s.getCode()[0] == b[0] && s.getCode()[1] == b[1]) {
            	status = s;
                break;
            }
        }
    	return status;
    }

    private ObisStatus getObisStatus(byte[] bx){
    	ObisStatus status = null;
    	byte[] b = new byte[1];
    	System.arraycopy(bx, 0, b, 0, b.length);
        for (ObisStatus s : ObisStatus.values()) {
            if (s.getCode() == b[0]) {
            	status = s;
                break;
            }
        }
    	return status;
    }

    private SnmpTrapStatus getSnmpTrapStatus(byte[] bx){
    	SnmpTrapStatus status = null;
    	byte[] b = new byte[1];
    	System.arraycopy(bx, 0, b, 0, b.length);
        for (SnmpTrapStatus s : SnmpTrapStatus.values()) {
            if (s.getCode()[0] == b[0]) {
            	status = s;
                break;
            }
        }
    	return status;
    }
 
    private PowerSourceType getPowerSourceType(byte[] bx){
    	PowerSourceType type = null;
    	byte[] b = new byte[1];
    	System.arraycopy(bx, 0, b, 0, b.length);
        for (PowerSourceType s : PowerSourceType.values()) {
            if (s.getCode()[0] == b[0]) {
            	type = s;
                break;
            }
        }
    	return type;
    }

    private TypeMain getTypeMain(byte[] bx){
    	TypeMain type = null;
    	byte[] b = new byte[1];
    	System.arraycopy(bx, 0, b, 0, b.length);
        for (TypeMain s : TypeMain.values()) {
            if (s.getCode()[0] == b[0]) {
            	type = s;
                break;
            }
        }
    	return type;
    }

    private TypeType getTypeType(byte[] bx){
    	TypeType type = null;
    	byte[] b = new byte[1];
    	System.arraycopy(bx, 0, b, 0, b.length);
        for (TypeType s : TypeType.values()) {
            if (s.getCode()[0] == b[0]) {
            	type = s;
                break;
            }
        }
    	return type;
    }

    private NetworkSpeedType getNetworkSpeedType(byte[] bx){
    	NetworkSpeedType ns = null;
    	byte[] b = new byte[1];
    	System.arraycopy(bx, 0, b, 0, b.length);
        for (NetworkSpeedType s : NetworkSpeedType.values()) {
            if (s.getCode()[0] == b[0]) {
            	ns = s;
                break;
            }
        }
    	return ns;
    }

    private CertificateStatus getCertificateStatus(byte[] bx){
    	CertificateStatus cs = null;
    	byte[] b = new byte[1];
    	System.arraycopy(bx, 0, b, 0, b.length);
        for (CertificateStatus s : CertificateStatus.values()) {
            if (s.getCode()[0] == b[0]) {
            	cs = s;
                break;
            }
        }
    	return cs;
    }
 
    private String getObisResultStr(byte[] bx, String field){
        StringBuffer status = new StringBuffer();
    	byte[] b = new byte[1];
    	System.arraycopy(bx, 0, b, 0, b.length);
        status.append(field);
        ObisStatus st = this.getObisStatus(bx);
        if(st == null){
        	status.append(String.format("%d", DataUtil.getIntToBytes(b)));
        }
        else {
        	switch(st){
        	case Success:
        		status.append("Success(0)");
    			break;
        	case Data_Error:
        		status.append("Data Error(1)");
    			break;
        	case Data_Overlap:
        		status.append("Data Overlap(2)");
    			break;
        	}
        }
    	return status.toString();
    }
 
    private String getSnmpTrapResultStr(byte[] bx, String field){
        StringBuffer status = new StringBuffer();
    	byte[] b = new byte[1];
    	System.arraycopy(bx, 0, b, 0, b.length);
    	
        status.append(field);
        SnmpTrapStatus st = this.getSnmpTrapStatus(bx);
        if(st == null){
        	status.append(String.format("%d", DataUtil.getIntToBytes(b)));
        }
        else{
        	switch(st){
       		case On:
       			status.append("SNMP Trap ON(1)");
			break;
       		case Off:
        		status.append("SNMP Trap OFF(0)");
    			break;
       		}
        }
    	return status.toString();
    }
 
    private String getPowerSourceTypeStr(byte[] bx, String field, String kind){
        StringBuffer type = new StringBuffer();
    	byte[] b = new byte[1];
    	System.arraycopy(bx, 0, b, 0, b.length);
        type.append(field);
        PowerSourceType pst = this.getPowerSourceType(bx);
        if(pst == null){
        	type.append(String.format("%d", DataUtil.getIntToBytes(b)));
        }
        else{
        	switch(pst){
        	case Unknown:
        		type.append("Unknown(0)");
    			break;
        	case Electric:
        		type.append("Electric(1)");
    			break;
            case Battery:
                type.append("Battery(2)");
                break;
            case Soloar:
                type.append("Soloar(3)");
                break;
            case Super_Cap: 
            	if(kind.equals("primary")){
            		type.append(String.format("%d", DataUtil.getIntToBytes(b)));
            	}
            	else{
            		 type.append("Super Cap(4)");
            	}
                break;
            }
        }
    	return type.toString();
    }


    private String getTypeMainStr(byte[] bx, String field){
        StringBuffer type = new StringBuffer();
        byte [] b = new byte[1];
        System.arraycopy(bx, 0, b, 0, b.length);
        type.append(field);
        TypeMain tm = this.getTypeMain(bx);
        if(tm == null){
        	type.append(String.format("%d", DataUtil.getIntToBytes(b)));
        }
        else{
        	switch(tm){
        	case Ethernet:
        		type.append("Ethernet(0)");
    			break;
        	case Mobile:
        		type.append("Mobile(1)");
    			break;
            }
        }
    	return type.toString();
    }

    private String getTypeTypeStr(byte[] bx, String field){
        StringBuffer type = new StringBuffer();
    	byte[] b = new byte[1];
    	System.arraycopy(bx, 0, b, 0, b.length);
        type.append(field);
        TypeType tt = this.getTypeType(bx);
        if(tt == null) {
        	type.append(String.format("%d", DataUtil.getIntToBytes(b)));
        }
        else {
        	switch(tt){
        	case ZigBee:
        		type.append("ZigBee(0)");
    			break;
        	case PLC:
        		type.append("PLC(1)");
    			break;
            case Subgiga:
                type.append("Subgiga(2)");
                break;
            }
        }
    	return type.toString();
    }

    private String getNetworkSpeedTypeStr(byte[] bx, String field){
        StringBuffer speed = new StringBuffer();
    	byte[] b = new byte[1];
    	System.arraycopy(bx, 0, b, 0, b.length);
        speed.append(field);
        NetworkSpeedType ns = this.getNetworkSpeedType(bx);
        if(ns == null) {
        	speed.append(String.format("%d", DataUtil.getIntToBytes(b)));
        }
        else {
        	switch(ns){
        	case S48:
        		speed.append("4.8 Kbps(1)");
    			break;
        	case S384:
        		speed.append("38.4 Kbps(2)");
    			break;
        	case S500:
        		speed.append("50 Kbps(3)");
    			break;
        	case S1000:
        		speed.append("100 Kbps(4)");
    			break;
        	case S1500:
        		speed.append("150 Kbps(5)");
    			break;
            }
        }
    	return speed.toString();
    }

    private String getCertificateStatusStr(byte[] bx, String field){
        StringBuffer status = new StringBuffer();
    	byte[] b = new byte[1];
    	System.arraycopy(bx, 0, b, 0, b.length);
        status.append(field);
        CertificateStatus cs = this.getCertificateStatus(bx);
        if(cs == null){
        	status.append(String.format("%d", DataUtil.getIntToBytes(b)));
        }
        else {
        	switch(cs){
        	case Success:
        		status.append("Success(0)");
    			break;
        	case Fail:
        		status.append("Fail(1)");
    			break;
            }
        }
    	return status.toString();
    }

    private String getHopCountStr(byte[] bx, byte[] b, String field){
        StringBuffer retBuf = new StringBuffer();
        retBuf.append(field);

        // distance
        int hop= DataUtil.getIntToByte(bx[0]);
        int coordinator= DataUtil.getIntToByte(bx[1]);
    	ParentNodeInfo pni = new ParentNodeInfo();
    	retBuf.append(pni.getDistanceStr(coordinator, hop));

    	return retBuf.toString();
    }

    private String getHopsStr(byte[] bx, byte[] b, String field){
        StringBuffer retBuf = new StringBuffer();
        retBuf.append(field);

        // distance
        int hop= DataUtil.getIntToByte(bx[0]);
        int coordinator= DataUtil.getIntToByte(bx[1]);
    	ParentNodeInfo pni = new ParentNodeInfo();
    	retBuf.append(pni.getDistanceStr(coordinator, hop));

        return retBuf.toString();
    }

    private String getNoResponseStr(){
    	String rtnStr = "";
    	return rtnStr;
    }
	
    private String getIntToByteDataToString(byte[] b, int length, String field){
        StringBuffer buf = new StringBuffer();
        int val = 0;
        byte bval = 0;
        
        buf.append(field);
        if(b.length >= 1){
            bval = b[0];
            val = DataUtil.getIntToByte(bval);
            buf.append(val);
		}
        return buf.toString();
    }

    private String getIntTo2ByteDataToString(byte[] b, int length, String field){
        StringBuffer buf = new StringBuffer();
        int val = 0;
    	byte[] bTmp = new byte[2];
    	System.arraycopy(b, 0, bTmp, 0, bTmp.length);
        
        buf.append(field);
        if(bTmp.length == 2){
            val = DataUtil.getIntTo2Byte(bTmp);
            buf.append(val);
        }
        return buf.toString();
    }
    
	private int getIntTo4Byte(byte[] b){
    	byte[] bTmp = new byte[4];
    	System.arraycopy(b, 0, bTmp, 0, bTmp.length);
        int val = 0;
        val = ((bTmp[0] & 0x7f) << 24)
                    + ((bTmp[1] & 0xff) << 16)
                    + ((bTmp[2] & 0xff) << 8)
                    + (bTmp[3] & 0xff);
        if((bTmp[0] & 0x80) == 0x80){
            val = -val;
        }
		return val;
    }

    private String getIntTo4ByteDataToString(byte[] b, int length, String field){
        StringBuffer buf = new StringBuffer();
    	byte[] bTmp = new byte[4];
    	System.arraycopy(b, 0, bTmp, 0, bTmp.length);
        
        buf.append(field);
        if(bTmp.length == 4){
        	int val = getIntTo4Byte(bTmp);
            buf.append(val);
        }
        return buf.toString();
    }

    private String getHexaDataToString(byte[] b, int length, String field){
        StringBuffer buf = new StringBuffer();
        byte[] tmpBuf = new byte[length];
        
        buf.append(field);
        System.arraycopy(b, 0, tmpBuf, 0, tmpBuf.length);
        buf.append(Hex.decode(tmpBuf));
        return buf.toString();
    }

    private String getHexaDataToString(byte[] b, String field){
        StringBuffer buf = new StringBuffer();
        
        buf.append(field);
        buf.append(String.format("%d", (short)b[0]));
        return buf.toString();
    }

    private String getIntHexaDataToString(byte[] b, int length, String field){
        StringBuffer buf = new StringBuffer();
        byte[] tmpBuf = new byte[length];
        
        buf.append(field);
        System.arraycopy(b, 0, tmpBuf, 0, tmpBuf.length);
        buf.append(String.format("%d", Integer.parseInt(Hex.decode(tmpBuf), 16)));
        return buf.toString();
    }

    private String getTextDataToString(byte[] b, String field){
    	StringBuffer buf = new StringBuffer();
        byte[] tmpBuf = new byte[b.length];
        
        System.arraycopy(b, 0, tmpBuf, 0, tmpBuf.length);
        String textStr = new String(tmpBuf).trim();
        buf.append(field);
        buf.append(textStr);
        return buf.toString();
    }

	// description(inplementation: kind:decimal(from byte to 4 bytes)/hexa)
	private String getMultiTwoDataToString(byte[] b, int len1, int len2, String field1, String field2, String kind1, String kind2){
        StringBuffer buf = new StringBuffer();
        byte[] tmpBuf = new byte[len1];
        
        buf.append(field1);
        System.arraycopy(b, 0, tmpBuf, 0, tmpBuf.length);
        
		if(kind1.equals("decimal")){
			if(len1 == 1){
				buf.append(DataUtil.getIntToByte(tmpBuf[0]));
			}
			else if(len1 == 2){
				buf.append(DataUtil.getIntTo2Byte(tmpBuf));
			}
			else if(len1 == 3){
				buf.append(DataUtil.getIntTo3Byte(tmpBuf));
			}
			else if(len1 == 4){
				buf.append(getIntTo4Byte(tmpBuf));
			}
		}
		else{
        	buf.append(Hex.decode(tmpBuf));
		}
        buf.append(", ");
        buf.append(field2);
        tmpBuf = new byte[len2];
        System.arraycopy(b, len1, tmpBuf, 0, tmpBuf.length);
		if(kind2.equals("decimal")){
			if(len2 == 1){
				buf.append(DataUtil.getIntToByte(tmpBuf[0]));
			}
			else if(len2 == 2){
				buf.append(DataUtil.getIntTo2Byte(tmpBuf));
			}
			else if(len2 == 3){
				buf.append(DataUtil.getIntTo3Byte(tmpBuf));
			}
			else if(len2 == 4){
				buf.append(getIntTo4Byte(tmpBuf));
			}
		}
		else{
        	buf.append(Hex.decode(tmpBuf));
		}
        return buf.toString();
    }

    private String getNetworkfilterRssiToString(byte[] b, int length, String field){
        StringBuffer buf = new StringBuffer();
        byte[] tmpBuf = new byte[length];
        
        buf.append(field);
        System.arraycopy(b, 0, tmpBuf, 0, tmpBuf.length);
        int val = tmpBuf[0];
        // rssi is negative value.
        if((val & 0x80) == 0x80){
        	val = -(val & 0x7F);
        }
        buf.append(val);
        return buf.toString();
    }

    private String getEventTypeToString(byte[] b){
        StringBuffer buf = new StringBuffer();
        StringBuffer bufDataFormat = new StringBuffer();
        int count = 0;
        int pos = 0;
        buf.append("Event Log Count: ");
        byte[] btmp = new byte[2];
        System.arraycopy(b, pos, btmp, 0, btmp.length);
        count = DataUtil.getIntTo2Byte(btmp);
        buf.append(count);
        pos += btmp.length;
       
        for(int i = 0; i < count;i++){
            buf.append("\nEvent Log Data: [Index: ");
            btmp = new byte[2];
            System.arraycopy(b, pos, btmp, 0, btmp.length);
            buf.append(Hex.decode(btmp));
            pos += btmp.length;
            buf.append(", Time: ");
            btmp = new byte[7];
            System.arraycopy(b, pos, btmp, 0, btmp.length);
            bufDataFormat = CreateDateFormat(btmp);
            buf.append(bufDataFormat.toString());
            pos += btmp.length;
            buf.append(", Log Code: ");
            btmp = new byte[2];
            System.arraycopy(b, pos, btmp, 0, btmp.length);
            buf.append(Hex.decode(btmp));
            pos += btmp.length;
            buf.append(", Log Value: ");
            btmp = new byte[4];
            System.arraycopy(b, pos, btmp, 0, btmp.length);
            buf.append(Hex.decode(btmp));
            pos += btmp.length;
            buf.append("]");
            if(i != (count-1)){
                buf.append(", ");
            }
        }
        return buf.toString();
    }

    private String getFwImageInformationToString(byte[] b, int length){
        StringBuffer buf = new StringBuffer();
        byte[] tmpBuf = new byte[length];
        int pos = 0;
        
        if(b.length >= length) 
        {
            buf.append("My Image Size: ");
            tmpBuf = new byte[4];
            System.arraycopy(b, pos, tmpBuf, 0, tmpBuf.length);
            buf.append(getIntTo4Byte(tmpBuf));
            pos += tmpBuf.length;
            buf.append(",\nMy Image CRC: ");
            tmpBuf = new byte[2];
            System.arraycopy(b, pos, tmpBuf, 0, tmpBuf.length);
            buf.append(Hex.decode(tmpBuf));
            pos += tmpBuf.length;
            buf.append(",\nMy Image Received Size: ");
            tmpBuf = new byte[4];
            System.arraycopy(b, pos, tmpBuf, 0, tmpBuf.length);
            buf.append(getIntTo4Byte(tmpBuf));
            pos += tmpBuf.length;
            buf.append(",\nMy Image Sequence: ");
            tmpBuf = new byte[2];
            System.arraycopy(b, pos, tmpBuf, 0, tmpBuf.length);
            buf.append(Hex.decode(tmpBuf));
            pos += tmpBuf.length;
            buf.append(",\nOther Device Model Name: ");
            tmpBuf = new byte[20];
            System.arraycopy(b, pos, tmpBuf, 0, tmpBuf.length);
            String textStr = new String(tmpBuf).trim();
            buf.append(textStr);
            pos += tmpBuf.length;
            buf.append(",\nOhter Device Image Size: ");
            tmpBuf = new byte[4];
            System.arraycopy(b, pos, tmpBuf, 0, tmpBuf.length);
            buf.append(getIntTo4Byte(tmpBuf));
            pos += tmpBuf.length;
            buf.append(",\nOhter Device Image CRC: ");
            tmpBuf = new byte[2];
            System.arraycopy(b, pos, tmpBuf, 0, tmpBuf.length);
            buf.append(Hex.decode(tmpBuf));
            pos += tmpBuf.length;
            buf.append(",\nOhter Device Image Received Size: ");
            tmpBuf = new byte[4];
            System.arraycopy(b, pos, tmpBuf, 0, tmpBuf.length);
            buf.append(getIntTo4Byte(tmpBuf));
            pos += tmpBuf.length;
            buf.append(",\nOhter Device Image Sequence: ");
            tmpBuf = new byte[2];
            System.arraycopy(b, pos, tmpBuf, 0, tmpBuf.length);
            buf.append(Hex.decode(tmpBuf));
            pos += tmpBuf.length;
        }
        return buf.toString();
    }

    private String getNetworkKeyToString(byte[] b, int length){
        StringBuffer buf = new StringBuffer();
        byte[] tmpBuf = new byte[length];
        int pos = 0;
        int keyLen = 0;
        
        if(b.length >= length) 
        {
            buf.append("Key ID: ");
            tmpBuf = new byte[1];
            System.arraycopy(b, pos, tmpBuf, 0, tmpBuf.length);
            buf.append(Hex.decode(tmpBuf));
            pos += tmpBuf.length;
            buf.append(",\nKey Info: [Key Length: ");
            tmpBuf = new byte[2];
            System.arraycopy(b, pos, tmpBuf, 0, tmpBuf.length);
            keyLen = DataUtil.getIntTo2Byte(tmpBuf);
            buf.append(keyLen);
            pos += tmpBuf.length;
            buf.append(", \n           Key: ");
            tmpBuf = new byte[keyLen];
            System.arraycopy(b, pos, tmpBuf, 0, tmpBuf.length);
            buf.append(Hex.decode(tmpBuf));
            buf.append("]");
            pos += tmpBuf.length;
        }
        return buf.toString();
    }

    private String getSwitchStatusToString(byte[] b, int length){
        StringBuffer buf = new StringBuffer();
        byte[] tmpBuf = new byte[2];
        
        if(b.length >= length){
            buf.append("Tag: ");
        	System.arraycopy(b, 0, tmpBuf, 0, tmpBuf.length);
            buf.append(DataUtil.getIntToByte(tmpBuf[0]));
            buf.append(", Status: ");
            if(tmpBuf[1] == 0){
                buf.append("Switch Off(0)");
            }
            else if (tmpBuf[1] == 1){
                buf.append("Switch On(1)");
            }
            else{
            	buf.append(DataUtil.getIntToByte(tmpBuf[1]));
        	}
        }
        return buf.toString();
    }

    private String getNetworkChangeInformationToString(byte[] b){ 
        StringBuffer buf = new StringBuffer();
        StringBuffer bufDataFormat = new StringBuffer();
        int count = 0;
        int pos = 0;
        buf.append("Network info count: ");
        byte[] btmp = new byte[1];
        System.arraycopy(b, pos, btmp, 0, btmp.length);
        count = DataUtil.getIntToByte(btmp[0]);
        buf.append(count);
        pos += btmp.length;
       
        buf.append(", \nNetwork change information: ");
        for(int i = 0; i < count;i++){
        	buf.append("\n[");
            buf.append("Network Prefix: ");
            btmp = new byte[8];
            System.arraycopy(b, pos, btmp, 0, btmp.length);
            buf.append(Hex.decode(btmp));
            pos += btmp.length;
            buf.append(", \nJoin Counter: ");
            btmp = new byte[1];
            System.arraycopy(b, pos, btmp, 0, btmp.length);
        	buf.append(DataUtil.getIntToByte(btmp[0]));
            pos += btmp.length;
            buf.append(", Join Time: ");
            btmp = new byte[7];
            System.arraycopy(b, pos, btmp, 0, btmp.length);
            bufDataFormat = CreateDateFormat(btmp);
            buf.append(bufDataFormat.toString());
            pos += btmp.length;
            buf.append(", \nInfo read time: ");
            btmp = new byte[7];
            System.arraycopy(b, pos, btmp, 0, btmp.length);
            bufDataFormat = CreateDateFormat(btmp);
            buf.append(bufDataFormat.toString());
            pos += btmp.length;
            buf.append(", Metering data upload time: ");
            btmp = new byte[7];
            System.arraycopy(b, pos, btmp, 0, btmp.length);
            bufDataFormat = CreateDateFormat(btmp);
            buf.append(bufDataFormat.toString());
            pos += btmp.length;
            buf.append("]");
            if(i != (count-1)){
                buf.append(", ");
            }
        }
        return buf.toString();
    }

    private String getFrequencyToString(byte[] b, int length){
        StringBuffer buf = new StringBuffer();
        int val = 0;
        byte[] integer = new byte[2];
        byte[] decimal = new byte[1];
        int pos = 0;
        
        if(b.length >= length){
            buf.append("Start Freq: ");
            System.arraycopy(b, pos, integer, 0, integer.length);
            val = DataUtil.getIntTo2Byte(integer);
            buf.append(val);
            buf.append(".");
        	pos += integer.length;
            System.arraycopy(b, pos, decimal, 0, decimal.length);
            val = DataUtil.getIntToByte(decimal[0]);
            buf.append(val);
            buf.append("MHz");
            buf.append(", End Freq: ");
        	pos += decimal.length;
        	integer = new byte[2];
            System.arraycopy(b, pos, integer, 0, integer.length);
            val = DataUtil.getIntTo2Byte(integer);
            buf.append(val);
            buf.append(".");
        	pos += integer.length;
        	decimal = new byte[1];
            System.arraycopy(b, pos, decimal, 0, decimal.length);
            val = DataUtil.getIntToByte(decimal[0]);
            buf.append(val);
            buf.append("MHz");
        }
        return buf.toString();
    }

    private String getModemDTLSHandshakeStatusToString(byte[] b, int length, String requestType){
        StringBuffer buf = new StringBuffer();
        byte val = 0;
        
    	if(b.length >= length) {
    		val = b[0];
            buf.append("DTLS Handshake Status: ");
        	if(requestType.equals("GET")) {
        		if(val == 0) {
                    buf.append("No handshaking(0)");
        		}
        		else if(val == 1) {
                    buf.append("Completed handshake(1)");
        		}
        		else {
                    buf.append(String.format("%d",val & 0xff));
        		}
        	}
        	else if(requestType.equals("SET")) {
        		if(val == 1) {
                    buf.append("command to take handshake again(1)");
        		}
        		else {
                    buf.append(String.format("%d",val & 0xff));
        		}
        	}
    	}
        return buf.toString();
    }
 
    private String getCoordinatorBroadcastConfigurationToString(byte[] b, int length){
        StringBuffer buf = new StringBuffer();
        int val = 0;
        byte[] bTmp = new byte[1];
    	int pos = 0;

        if(b.length >= length){
            buf.append("Enable Configuration: [");
        	System.arraycopy(b, pos, bTmp, 0, bTmp.length);
        	buf.append(getEnableConfigStr(bTmp));
            buf.append("]");
        	pos += bTmp.length;
            buf.append(",\nModem Mode: ");
        	bTmp = new byte[1];
        	System.arraycopy(b, pos, bTmp, 0, bTmp.length);
            if(bTmp[0] == 0x00){
                buf.append("Push Mode(0x00)");
            }
            else if(bTmp[0] == 0x01){
            	buf.append("Poll(Bypass) Mode(0x01)");
            }
        	else{
            	buf.append(String.valueOf(Hex.decode(bTmp)));
        	}
        	pos += bTmp.length;
        	bTmp = new byte[1];
        	System.arraycopy(b, pos, bTmp, 0, bTmp.length);
            buf.append(",\nETC Configuration: ");
            String etcStr = getEtcConfigStr(bTmp);
            buf.append(etcStr);
            buf.append(",\nMetering interval: ");
        	pos += bTmp.length;
        	bTmp = new byte[2];
        	System.arraycopy(b, pos, bTmp, 0, bTmp.length);
            val = DataUtil.getIntTo2Byte(bTmp);
            buf.append(val);
            buf.append(",\nTransmit frequency: ");
        	pos += bTmp.length;
        	bTmp = new byte[2];
        	System.arraycopy(b, pos, bTmp, 0, bTmp.length);
            val = DataUtil.getIntTo2Byte(bTmp);
            buf.append(val);
        }
        return buf.toString();
    }

    private String getEtcConfigStr(byte[] b){
    	StringBuffer buf = new StringBuffer();

        buf.append("[");
        if((b[0] & 0x20) == 0x20){
        	buf.append("Use DTLS with DCU.(Bit5:ON), ");
        }
        else{
        	buf.append("Do not use DTLS with DCU.(Bit5:OFF), ");
        }
        if((b[0] & 0x10) == 0x10){
        	buf.append("Auto upgrade 3rd party device on.(Bit4:ON), ");
        }
        else{
        	buf.append("Auto upgrade 3rd party device off.(Bit4:OFF), ");
        }
        if((b[0] & 0x08) == 0x08){
        	buf.append("Auto upgrade self on.(Bit3:ON), ");
        }
        else{
        	buf.append("Auto upgrade self off.(Bit3:OFF), ");
        }
        if((b[0] & 0x04) == 0x04){
        	buf.append("APC on.(Bit2:ON), ");
        }
        else{
        	buf.append("APC off.(Bit2:OFF), ");
        }
        if((b[0] & 0x02) == 0x02){
        	buf.append("MAC Push metering on.(Bit1:ON)");
        }
        else{
        	buf.append("MAC Push metering.(Bit1:OFF)");
        }
        if((b[0] & 0x01) == 0x01){
        	buf.append(", The current Clone is stopped.(Bit0:ON)");
        }
        buf.append("]");
    	return buf.toString();
    }

    private String getEnableConfigStr(byte[] b){
    	StringBuffer buf = new StringBuffer();
        if((b[0] & 0x08) == 0x08){
        	buf.append("Transmit frequency ON(Bit3:ON), ");
        }
    	else{
        	buf.append("Transmit frequency OFF(Bit3:OFF), ");
    	}
        if((b[0] & 0x04) == 0x04){
        	buf.append("Metering interval ON(Bit2:ON), ");
        }
    	else{
        	buf.append("Metering interval OFF(Bit2:OFF), ");
    	}
        if((b[0] & 0x02) == 0x02){
        	buf.append("ETC Configuration ON(Bit1:ON), ");
        }
    	else{
        	buf.append("ETC Configuration OFF(Bit1:OFF), ");
    	}
        if((b[0] & 0x01) == 0x01){
        	buf.append("Modem mode ON(Bit0:ON)");
        }
    	else{
        	buf.append("Modem mode OFF(Bit0:OFF)");
    	}
    	return buf.toString();
    }


    private String getResetReasonToString(byte[] b, int length, String field){
        StringBuffer buf = new StringBuffer();
        String hexaStr = "";
        if(b.length >= length){
            buf.append(field);
            int val = b[0];
            int bitCnt = 0;
            val = val & 0xff;
            hexaStr = String.format("%02x",val);
            buf.append(hexaStr);
            if(val > 0){
                buf.append("[");
                if((val & 0x80) == 0x80){
                	buf.append("Reserved(Bit7:ON)");
                	bitCnt++;
                }
                if((val & 0x40) == 0x40){
                	if(bitCnt > 0){
                		buf.append(", ");
                	}
                	buf.append("Low Power(Bit6:ON)");
                	bitCnt++;
                }
                if((val & 0x20) == 0x20){
                	if(bitCnt > 0){
                		buf.append(", ");
                	}
                	buf.append("WWDOG(Bit5:ON)");
                	bitCnt++;
                }
                if((val & 0x10) == 0x10){
                	if(bitCnt > 0){
                		buf.append(", ");
                	}
                	buf.append("IWDOG(Bit4:ON)");
                	bitCnt++;
                }
                if((val & 0x08) == 0x08){
                	if(bitCnt > 0){
                		buf.append(", ");
                	}
                	buf.append("Software(Bit3:ON)");
                	bitCnt++;
                }
                if((val & 0x04) == 0x04){
                	if(bitCnt > 0){
                		buf.append(", ");
                	}
                	buf.append("POR/PDR(Bit2:ON)");
                	bitCnt++;
                }
                if((val & 0x02) == 0x02){
                	if(bitCnt > 0){
                		buf.append(", ");
                	}
                	buf.append("Pin(Bit1:ON)");
                	bitCnt++;
                }
                if((val & 0x01) == 0x01){
                	if(bitCnt > 0){
                		buf.append(", ");
                	}
                	buf.append("POR/PDR or BOR(Bit0:ON)");
                	bitCnt++;
                }
                buf.append("]");
            }
        }
        return buf.toString();
    }

    private String getMeterStatusToString(byte[] b){
        StringBuffer buf = new StringBuffer();
        
    	buf.append(String.format("Tag: %d,\n", Integer.parseInt(String.format("%02x", b[0]), 16)));
        buf.append(String.format("Value: 0x%02x%02x%02x%02x", b[1], b[2], b[3], b[4]));
        
        String strb0 = this.getByte0Value(b[1]);
        String strb1 = this.getByte1Value(b[2]);
        String strb2 = this.getByte2Value(b[3]);
        if ((strb0.length() > 0) || (strb1.length() > 0) || (strb2.length() > 0)){
        	buf.append("[\n");
        }
        if (strb0.length() > 0){
        	buf.append(strb0);
        }
        if (strb1.length() > 0){
        	if (strb0.length() > 0){
            	buf.append(", \n");
        	}
        	buf.append(strb1);
        }
        if (strb2.length() > 0){
        	if ((strb0.length() > 0) || (strb1.length() > 0)){
            	buf.append(", \n");
        	}
        	buf.append(strb2);
        }
        if ((strb0.length() > 0) || (strb1.length() > 0) || (strb2.length() > 0)){
        	buf.append("]");
        }
        return buf.toString();
    }
    
    private String getByte0Value(byte b){
    	String retStr = "";
    	
    	if (((b & 0x80) >>> 7) == 1){
    		retStr = "Clock invalid(Bit7:ON)";
    	}else if (((b & 0x40) >>> 6) == 1){
    		retStr = "Replace battery(Bit6:ON)";
    	}else if (((b & 0x20) >>> 5) == 1){
    		retStr = "Power up(Bit5:ON)";
    	}else if (((b & 0x10) >>> 4) == 1){
    		retStr = "L1 error(Bit4:ON)";
    	}else if (((b & 0x08) >>> 3) == 1){
    		retStr = "L2 error(Bit3:ON)";
    	}else if (((b & 0x04) >>> 2) == 1){
    		retStr = "L3 error(Bit2:ON)";
    	}
    	
    	if (retStr.length() > 0){
    		retStr = "[Byte0: " + retStr + "]";
    	}
    	
    	return retStr;
    }

    private String getByte1Value(byte b){
    	String retStr = "";
    	
    	if (((b & 0x80) >>> 7) == 1){
    		retStr = "Program memory error(Bit7:ON)";
    	}else if (((b & 0x40) >>> 6) == 1){
    		retStr = "RAM error(Bit6:ON)";
    	}else if (((b & 0x20) >>> 5) == 1){
    		retStr = "NV memory error(Bit5:ON)";
    	}else if (((b & 0x10) >>> 4) == 1){
    		retStr = "Watchdog error(Bit4:ON)";
    	}else if (((b & 0x08) >>> 3) == 1){
    		retStr = "Fraud attempt(Bit3:ON)";
    	}
    	
    	if (retStr.length() > 0){
    		retStr = "[Byte1: " + retStr + "]";
    	}

    	return retStr;
    }

    private String getByte2Value(byte b){
    	String retStr = "";
    	
    	if (((b & 0x80) >>> 7) == 1){
    		retStr = "Communication error M-BUS(Bit7:ON)";
    	}else if (((b & 0x40) >>> 6) == 1){
    		retStr = "New M-BUS device discovered(Bit6:ON)";
    	}
    	
    	if (retStr.length() > 0){
    		retStr = "[Byte2: " + retStr + "]";
    	}

    	return retStr;
    }

    private String getDateFormatToString(byte[] b, String field){
        StringBuffer buf = new StringBuffer();
        StringBuffer dateFormat = new StringBuffer();
        buf.append(field);
        dateFormat = CreateDateFormat(b);
        buf.append(dateFormat.toString());
        return buf.toString();
    }
    
    private StringBuffer CreateDateFormat(byte[] b){
        StringBuffer buf = new StringBuffer();

        String convertStr = String.format("%04d-%02d-%02d %02d:%02d:%02d"
							,Integer.parseInt(String.format("%02x%02x", b[0], b[1]), 16)
							,b[2]
							,b[3]
							,b[4]
							,b[5]
							,b[6]
							);

    	buf.append(convertStr);
        return buf;
    }

    private String getResetScheduleToString(byte[] b, int length, String field){
        StringBuffer buf = new StringBuffer();
		int val = 0;
		String convertStr = "";
        
        buf.append(field);
        for(int i = 0; i < b.length; i++){
            val = b[i];
            val = val & 0xff;
            if(val == 0xff){
                buf.append("do not reset the modem.(0xFF)");
            }
            else{
                convertStr = String.format("%d", val);
                buf.append(convertStr);	
            }
        }
        return buf.toString();
    }

    private String getBandWidhToString(byte[] b, int length, String field){
        StringBuffer buf = new StringBuffer();
        int val = 0;
        byte[] tmpBuf = new byte[length];
        System.arraycopy(b, 0, tmpBuf, 0, tmpBuf.length);
        buf.append(field);
        if(length >= 2){
            val = DataUtil.getIntTo2Byte(tmpBuf);
            buf.append(String.format("%d",val));
            buf.append("kHz");
        }
        return buf.toString();
    }
}
