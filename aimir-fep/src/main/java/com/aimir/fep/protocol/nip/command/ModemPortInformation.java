package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class ModemPortInformation extends AbstractCommand{
    public ModemPortInformation() {
        super(new byte[] {(byte)0x20, (byte)0x10});
    }
     
	public enum TargetType {
    	DCU_Server((byte)0x00),
    	DCU_Client((byte)0x01),
    	HES_Server((byte)0x02),
    	HES_Client((byte)0x03),
    	HES_Auth((byte)0x04),
    	SNMP((byte)0x05),
    	Coap((byte)0x06),
    	NI((byte)0x07),
    	NTP((byte)0x08),
    	Modem((byte)0x09);
		
        private byte code;
        
        private TargetType(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }

        public static TargetType valueOf(byte code) {
            for (TargetType type : values()) {
                if (type.getCode() == code) {
                	return type;
                }
            }
            throw new IllegalArgumentException("no such enum object for the code: " + code);
        }

    }
	private TargetType targetType;
	private String targetTypeStr;
	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}
    private String port;
    public void setPort(String port)
    {
    	this.port = port;
    }
    public String getPort ( )
    {
    	return port;
    }
	private Status status;
	
	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        targetType = TargetType.valueOf(b[0]);
        pos += b.length;
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        port = String.valueOf(DataUtil.getIntTo2Byte(b));
        status = Status.Success;
    }

	// SP-575 add start
	public void decode2(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        if(b[0] < 10){
        	targetType = TargetType.valueOf(b[0]);
        }
        else{
            targetTypeStr = String.format("%d", DataUtil.getIntToBytes(b));
        }
        pos += b.length;
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        port = String.valueOf(DataUtil.getIntTo2Byte(b));
        status = Status.Success;
    }
	// SP-575 add end

	@Override
	public String toString() {
		String targetTypeStr = "";
		String portStr = "";
		if ( targetType != null ){
			targetTypeStr = targetType.name()+"(" + targetType.getCode() +")";
		}
		if ( port != null){
			portStr = port;
		}
		return "[ModemPortInformation]"+
	    	   "[targetType:"+ targetTypeStr + "]" +
	    	   "[port:" + portStr + "]";
	}

    // SP-575 add start
    public String toString2() {
		String targetTypeStr = "";
        targetTypeStr = getTargetTypeStr(targetType);
        return "Target Type: "+ targetTypeStr + ", " + 
        	   "Port: " + port;
    }

    private String getTargetTypeStr(TargetType tt){
        String rtnStr = "";
        if(tt == null){
            rtnStr = targetTypeStr;
        }
        else{
        	switch(tt){
        	case DCU_Server:
        		rtnStr = "Security DCU Server (RF_Modem only)(0)";
    			break;
        	case DCU_Client:
        		rtnStr = "Security DTLS DCU Client (RF_Modem only)(1)";
    			break;
        	case HES_Server:
        		rtnStr = "Security HES Server(2)";
    			break;
        	case HES_Client:
        		rtnStr = "Security HES Client(3)";
    			break;
        	case HES_Auth:
        		rtnStr = "HES Auth(4)";
    			break;
        	case SNMP:
        		rtnStr = "SNMP(5)";
    			break;
        	case Coap:
        		rtnStr = "Coap(6)";
    			break;
        	case NI:
        		rtnStr = "NI(7)";
    			break;
        	case NTP:
        		rtnStr = "NTP(MBB Modem only)(8)";
    			break;
        	case Modem:
        		rtnStr = "Modem(Ethernet Modem only)(9)";
    			break;
            default:
                rtnStr = String.format("%02d", tt.getCode());
                break;
            }
        	
        }
    	return rtnStr;
    }
	// SP-575 add end
	
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
	public Command set(HashMap info) throws Exception {
		// UPDATE START SP-701
		int targetType;
		
		if ( info.get("targetType") instanceof String ){
			targetType  = Integer.parseInt((String)info.get("targetType"));
		}
		else {
			targetType = (int)info.get("targetType");
		}
		// UPDATE END SP-701

		String port = (String)info.get("port"); // Hex String
		
		byte datavalue[] = new byte[]{
				DataUtil.getByteToInt(targetType)
				,DataUtil.getByteToInt(Integer.parseInt(port.substring(0, 2), 16))
        		,DataUtil.getByteToInt(Integer.parseInt(port.substring(2, 4), 16))};
		
		
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        

        datas[0].setValue(datavalue);
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }

	@Override
	public void decode(byte[] p1, CommandType p2) throws Exception{}

	@Override
	public Command get(HashMap info) throws Exception
	{
		// UPDATE START SP-701
		int targetType;
		if ( info.get("targetType") instanceof String ){
			targetType  = Integer.parseInt((String)info.get("targetType"));
		}
		else {
			targetType = (int)info.get("targetType");
		}
		// UPDATE END SP-701

        byte[]  paramdata = new byte[1];
        
        paramdata[0] = DataUtil.getByteToInt(targetType);
        
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Get);
        datas[0].setId(getAttributeID());   

        datas[0].setValue(paramdata);
        attr.setData(datas);
        command.setAttribute(attr);

        return command;
	}

	@Override
	public Command set() throws Exception{return null;}

	@Override
	public Command trap() throws Exception{return null;}
}
