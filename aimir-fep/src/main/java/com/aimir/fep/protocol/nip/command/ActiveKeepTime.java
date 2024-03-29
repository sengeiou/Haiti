package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class ActiveKeepTime extends AbstractCommand{
    private int keepTime;
    private Status status;

    public ActiveKeepTime() {
        super(new byte[] {(byte)0x20, (byte)0x0A});
    }
    
    public int getKeepTime() {
        return keepTime;
    }

    public Status getStatus() {
        return status;
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
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        datas[0].setValue(DataUtil.get2ByteToInt((int)info.get("keepTime")));
        
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }

    @Override
    public void decode(byte[] data) {
        // status result
        byte[] b = new byte[2];
        System.arraycopy(data, 0, b, 0, b.length);

        for (Status s : Status.values()) {
            if (s.getCode()[0] == b[0] && s.getCode()[1] == b[1]) {
                status = s;
                break;
            }
        }
        keepTime = DataUtil.getIntTo2Byte(b);
    }
    
	@Override
    public String toString() {
        return "[ActiveKeepTime]"+
        	   "[status:"+status.name()+"]"+
        	   "[keepTime:"+keepTime+"]";
    }

	@Override
	public Command set() throws Exception{return null;}

	@Override
	public Command get(HashMap p) throws Exception{return null;}

	@Override
	public Command trap() throws Exception{return null;}
 
	@Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
        
    }
}
