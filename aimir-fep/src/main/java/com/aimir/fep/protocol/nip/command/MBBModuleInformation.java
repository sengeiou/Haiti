package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;

public class MBBModuleInformation extends AbstractCommand{
    private String moduleVersion;
    private String moduleRevision;
    
    public MBBModuleInformation() {
        super(new byte[] {(byte)0x10, (byte)0x0B});
    }
    
    public String getModuleVersion() {
        return this.moduleVersion;
    }

    public String getModuleRevision() {
        return this.moduleRevision;
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
    public Command set(HashMap info) throws Exception {
        throw new Exception("Set MBB Module information is not support.");
    }
  
    @Override
    public void decode(byte[] data) {
        // status result
        if (data.length >= 2) {

        	if(data[0] == 0x01) {
        		this.moduleVersion = "Telit LE910 EUG";
        	}else if(data[0] == 0x02) {
        		this.moduleVersion = "Telit LE910 V2";
        	}else {
        		this.moduleVersion = "unknown type["+data[0]+"]";
        	}
        	
        	int len = data[1] & 0xFF;
        	
        	if(len > 0 && (data.length-2) >= len) {
                byte[] b = new byte[len];
                System.arraycopy(data, 2, b, 0, len);
                
                this.moduleRevision = new String(b).trim();
                
                if(this.moduleRevision.length() < 11) {
                	this.moduleRevision = "";                	
                }else if(this.moduleRevision.charAt(0) == '-') {
                	this.moduleRevision = "";
                }
        	}
        }
    }
 
    @Override
	public String toString() {
	    return "[MBBModuleInformation]"+
	    	   "[Version:"+moduleVersion+"]"+
	    	   "[Revision:"+moduleRevision+"]";
	}
 
    @Override
    public Command get(HashMap p) throws Exception{return null;}
    @Override
    public Command set() throws Exception{return null;}
    @Override
    public Command trap() throws Exception{return null;}

    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
        
    }
}
