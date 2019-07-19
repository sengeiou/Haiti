package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class ModemInformation extends AbstractCommand {
    public enum ModemType {
        Coordinator ((byte)0x00),
        Standard ((byte)0x01),
        Etype ((byte)0x02),
        Gtype ((byte)0x03),
        Water ((byte)0x04),
        Gas ((byte)0x05),
        IraqNBPlc ((byte)0x10),
        SProjRFCoordinator((byte)0x20),
        SProjRFRouter ((byte)0x21),
        SProjMBB ((byte)0x22),
        SProjEthernet ((byte)0x23),
        // SP-575 change start
        //SProjDongle ((byte)0x24);
        SProjDongle ((byte)0x24),
        SProjRFRouterPANA ((byte)0x25);
        // SP-575 change end
        
        
        private byte code;
        
        ModemType(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
    
    public enum ModemStatus {
        Idle ((byte)0x00),
        MeterReading ((byte)0x01),
        FirmwareUpgrade ((byte)0x02);
        
        private byte code;
        
        ModemStatus(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
    
    public enum ModemMode {
        Push((byte)0x00),
        Poll((byte)0x01);
        private byte code;
        ModemMode(byte code) {
            this.code = code;
        }
        public byte getCode() {
            return this.code;
        }
    }
    
    private String euiId;
    private ModemType modemType;
    private int resetTime;
    private String nodeKind;
    
    private String fwVersion;
    private String bulidNumber;
    private String hwVersion;
    
    private ModemStatus modemStatus;
    private ModemMode modemMode;
    private byte modemModeCode;
    private byte modemStatusCode;
    private byte modemTypeCode;
    
    public ModemInformation() {
        super(new byte[] {(byte)0x10, (byte)0x01});
    }
    
    public String getEuiId() {
        return euiId;
    }

    public ModemType getModemType() {
        return modemType;
    }

    public int getResetTime() {
        return resetTime;
    }

    public String getNodeKind() {
        return nodeKind;
    }

    public String getFwVersion() {
        return fwVersion;
    }
    
    public String getBulidNumber() {
        return bulidNumber;
    }

    public String getHwVersion() {
        return hwVersion;
    }
    
    public ModemStatus getModemStatus() {
        return modemStatus;
    }
    
    public ModemMode getModemMode() {
        return modemMode;
    }

    @Override
    public Command get() throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Get);
        datas[0].setId(getAttributeID());
        
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
 
    @Override
    public Command trap() throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Get);
        datas[0].setId(getAttributeID());
        
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
  
    @Override
    public void decode(byte[] bx) {
        int pos = 0;
        
        byte[] b = new byte[8];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        euiId = Hex.decode(b);

        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        for (ModemType m : ModemType.values()) {
            if (m.getCode() == b[0]) {
                modemType = m;
                break;
            }
        }
        // SP-575 add start
        modemTypeCode = b[0];
        // SP-575 add end
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        resetTime = DataUtil.getIntToByte(b[0]);
        
        b = new byte[20];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        nodeKind = new String(b).trim();
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        fwVersion = String.format("%02x%02x", b[0], b[1], 16);

        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        bulidNumber = String.format("%02x%02x", b[0], b[1], 16);
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        hwVersion = String.format("%02x%02x", b[0], b[1], 16);
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        for (ModemStatus m : ModemStatus.values()) {
            if (m.getCode() == b[0]) {
            	modemStatus = m;
                break;
            }
        }
        // SP-575 add start
        modemStatusCode = b[0];
        // SP-575 add end
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        for (ModemMode m : ModemMode.values()) {
            if (m.getCode() == b[0]) {
            	modemMode = m;
                break;
            }
        }
        // SP-575 add start
        modemModeCode = b[0];
        // SP-575 add end
    }

	@Override
	public String toString() {
	    return "[ModemInformation]"+
	    	   "[euiId:"+euiId+"]"+
	    	   "[modemType:"+modemType.name()+"]"+
	    	   "[resetTime:"+resetTime+"]"+
	    	   "[nodeKind:"+nodeKind+"]"+
	    	   "[fwVersion:"+fwVersion+"]"+
	    	   "[bulidNumber:"+bulidNumber+"]"+
	    	   "[hwVersion:"+hwVersion+"]"+
	    	   "[modemStatus:"+modemStatus.name()+"]"+
	    	   "[modemMode:"+modemMode.name()+"]";
	}

	// SP-575 add start
	public String toString2() {
        String modemTypeStr = "";
        String modemStatusStr = "";
        String modemModeStr = "";
        modemTypeStr = getModemTypeStr(modemType);
        modemStatusStr = getModemStatusStr(modemStatus);
        modemModeStr = getModemModeStr(modemMode);
	    return "EUI Id: "+euiId+", \n"+
	    	   "Modem Type: "+modemTypeStr+", \n"+
	    	   "Modem Reset Time: "+resetTime+", \n"+
	    	   "Node Kind: "+nodeKind+", \n"+
	    	   "F/W Version: "+fwVersion+", \n"+
	    	   "Bulid Number: "+bulidNumber+", \n"+
	    	   "H/W Version: "+hwVersion+", \n"+
	    	   "Modem Status: "+modemStatusStr+", \n"+
	    	   "Modem Mode: "+modemModeStr;
	}

	private String getModemTypeStr(ModemType mt){
        String rtnStr = "";
        if(mt == null){
            rtnStr = "0x" +String.format("%x", modemTypeCode);
        	return rtnStr;
        }
    	switch(mt){
    	case Coordinator:
    		rtnStr = "Coordinator modem(0x00)";
			break;
    	case Standard:
    		rtnStr = "Internal modem (standard type)(0x01)";
			break;
    	case Etype:
    		rtnStr = "External E-type modem(0x02)";
			break;
    	case Gtype:
    		rtnStr = "G-type(0x03)";
			break;
    	case Water:
    		rtnStr = "Water Modem(0x04)";
			break;
    	case Gas:
    		rtnStr = "Gas Modem(0x05)";
			break;
    	case IraqNBPlc:
    		rtnStr = "Iraq NB-PLC Modem(0x10)";
			break;
    	case SProjRFCoordinator:
    		rtnStr = "S-Project RF Coordinator Modem(0x20)";
			break;
    	case SProjRFRouter:
    		rtnStr = "S-Project RF Router Modem(0x21)";
			break;
    	case SProjMBB:
    		rtnStr = "S-Project MBB Modem(0x22)";
			break;
    	case SProjEthernet:
    		rtnStr = "S-Project Ethernet Modem(0x23)";
			break;
    	case SProjDongle:
    		rtnStr = "S-Project Dongle Modem(0x24)";
			break;
    	case SProjRFRouterPANA:
    		rtnStr = "S-Project RF Router PANA Modem(0x25)";
    		break;
        }
    	return rtnStr;
    }
	
    private String getModemStatusStr(ModemStatus ms){
        String rtnStr = "";
        if(ms == null){
            rtnStr = "0x" +String.format("%x", modemStatusCode);
        	return rtnStr;
        }
    	switch(ms){
    	case Idle:
    		rtnStr = "Idle(0x00)";
			break;
    	case MeterReading:
    		rtnStr = "Meter Reading(0x01)";
			break;
    	case FirmwareUpgrade:
    		rtnStr = "Firmware Upgrade(0x02)";
			break;
        }
    	return rtnStr;
    }

    private String getModemModeStr(ModemMode mm){
        String rtnStr = "";
        if(mm == null){
            rtnStr = "0x" +String.format("%x", modemModeCode);
        	return rtnStr;
        }
    	switch(mm){
    	case Push:
    		rtnStr = "Push Mode(0x00)";
			break;
    	case Poll:
    		rtnStr = "Poll (Bypass) Mode(0x01)";
			break;
        }
    	return rtnStr;
    }
	// SP-575 add end

	@Override
	public Command get(HashMap p) throws Exception{return null;}
	@Override
	public Command set() throws Exception{return null;}
	@Override
	public Command set(HashMap p) throws Exception{return null;}
    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
        
    }
}
