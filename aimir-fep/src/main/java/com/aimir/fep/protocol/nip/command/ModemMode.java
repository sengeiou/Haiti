package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class ModemMode extends AbstractCommand{
	private static Log log = LogFactory.getLog(ModemMode.class);
    public enum Mode {
        Push ((byte)0x00),
        Polling ((byte)0x01);
        
        private byte code;
        
        Mode(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
    private Mode mode;
    /**
	 * @return the mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	private Status status;
    private byte modemModeCode;
    
    public ModemMode() {
        super(new byte[] {(byte)0x20, (byte)0x03});
    }
    
    public Status getStatus() {
        return status;
    }
 
    @Override
    public Command set(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        byte setMode;
        if ( info.get("mode") instanceof String ){
        	setMode = DataUtil.getByteToInt(Integer.parseInt((String)info.get("mode")));
        	datas[0].setValue(new byte[]{DataUtil.getByteToInt(Integer.parseInt((String)info.get("mode")))});
        }
        else {
        	setMode = DataUtil.getByteToInt((int)info.get("mode"));
        	datas[0].setValue(new byte[]{DataUtil.getByteToInt((int)info.get("mode"))});
        }
        if ( setMode == Mode.Push.getCode()) {
        	setMode(Mode.Push);
        }
        else if (setMode == Mode.Polling.getCode()){
        	setMode(Mode.Polling);
        }
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
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
    public void decode(byte[] data) {
        byte[] b = new byte[2];
        System.arraycopy(data, 0, b, 0, b.length);

        for (Status s : Status.values()) {
            if (s.getCode()[0] == b[0] && s.getCode()[1] == b[1]) {
                status = s;
                break;
            }
        }
    }
    
    // SP-575 add start
    public void decode2(byte[] data) {
        byte[] b = new byte[1];
        System.arraycopy(data, 0, b, 0, b.length);

        for (Mode s : Mode.values()) {
            if (s.getCode() == b[0]) {
                mode = s;
                break;
            }
        }
        //
        modemModeCode = b[0];
    }
    // SP-575 add end
    
    @Override
    public String toString() {
    	String modemModeStr =  mode != null ? mode.name()  : "" ;
    	String statusStr =  status != null ? status.name() : "" ;
    	return ("[ModemMode][modemMode:" + modemModeStr + "]"
    			+ "[status:" + statusStr + "]");
    }

    // SP-575 add start
    public String toString2() {
        String modemModeStr = "";
        modemModeStr = getModemModeStr(mode);
        return "Modem Mode: "+ modemModeStr;
    }
    
    private String getModemModeStr(Mode mm){
        String rtnStr = "";
        if(mm == null ){
        	rtnStr = "0x" +String.format("%02x", modemModeCode);
        }
        else{
        	switch(mm){
        	case Push:
        		rtnStr = "Push Mode (default setting value)(0x00)";
    			break;
        	case Polling:
        		rtnStr = "Poll (Bypass) Mode (default setting value for SORIA)(0x01)";
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
    public Command trap() throws Exception{return null;}
    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {

    	if(commandType == CommandType.Get){
    		int pos = 0;
    		byte[] b = new byte[1];

    		System.arraycopy(p1, pos, b, 0, b.length);
    		if ( b[0] == Mode.Push.getCode()) {
    			setMode(Mode.Push);
    			status = Status.Success;
    		}
    		else if ( b[0] == Mode.Polling.getCode()){
    			setMode(Mode.Polling);
    			status = Status.Success;
    		}
    		else {
    			status = Status.Unknown;
    		}
    	}
    	else{ // SET
    		byte[] b = new byte[2];
    		System.arraycopy(p1, 0, b, 0, b.length);
    		for (Status s : Status.values()) {
    			if (s.getCode()[0] == b[0] && s.getCode()[1] == b[1]) {
    				status = s;
    				break;
    			}
    		}
    	}

    }
}
