package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class NetworkJoinTimeout extends AbstractCommand{
    private String ss;
    private Status status;
    
    public NetworkJoinTimeout() {
        super(new byte[] {(byte)0x20, (byte)0x0E});
    }
    
    public String getSs() {
        return ss;
    }

    public void setSs(String ss) {
        this.ss = ss;
    }
	 
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "[NetworkJoinTimeout]"+
                "[status:"+status.name()+"]"+
                "[ss:"+ss+"]";
    }
	
    @Override
    public void decode(byte[] bx, CommandType commandType)
                    throws Exception {
        if(commandType == CommandType.Get){
            int pos = 0;
            byte[] b = new byte[2];
            System.arraycopy(bx, pos, b, 0, b.length);
            ss = String.valueOf(DataUtil.getIntToByte(b[0]));           
        }
        else{
            byte[] b = new byte[2];
            System.arraycopy(bx, 0, b, 0, b.length);
            for (Status s : Status.values()) {
                if (s.getCode()[0] == b[0] && s.getCode()[1] == b[1]) {
                    status = s;
                    break;
                }
            }
        }
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
        String ss = (String)info.get("ss");
        
        //ss
        datas[0].setValue(new byte[]{DataUtil.getByteToInt(Integer.parseInt(ss.substring(0, 1)))
        		,DataUtil.getByteToInt(Integer.parseInt(ss.substring(1, 2)))});
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
    @Override
    public void decode(byte[] p1) throws Exception{}
    @Override
    public Command get(HashMap p) throws Exception{return null;}
    @Override
    public Command set() throws Exception{return null;}
    @Override
    public Command trap() throws Exception{return null;}

}
