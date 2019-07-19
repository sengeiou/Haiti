package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;

public class ModemStatus extends AbstractCommand{
    public enum Status {
        Idle ((byte)0x00),
        MeterReading ((byte)0x01),
        FirmwareUpgrade ((byte)0x02);
        
        private byte code;
        
        Status(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }

    public ModemStatus() {
        super(new byte[] {(byte)0x10, (byte)0x04});
    }
    
    private Status modemStatus;
    private byte statusCode;
    
    public Status getModemStatus() {
        return modemStatus;
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
    public void decode(byte[] bx) {
        int pos = 0;
        
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        for (Status m : Status.values()) {
            if (m.getCode() == b[0]) {
                modemStatus = m;
                break;
            }
        }
        // SP-575 add start
        statusCode = b[0];
        // SP-575 add start
    }
    
    @Override
    public String toString() {
        return "[ModemStatus]"+
        	   "[status:"+modemStatus.name()+"]";
    }

    // SP-575 add start
    public String toString2() {
        String statusStr = "";
        statusStr = getStatusStr(modemStatus);
        return "Status: "+ statusStr;
    }

    private String getStatusStr(Status ms){
        String rtnStr = "";
        if(ms == null){
    		rtnStr = "Reserved(" + "0x" +String.format("%02x", statusCode)+ ")";
        }
        else{
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
    public Command trap() throws Exception{return null;}
    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
        
    }
}
