package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class ModemTime extends AbstractCommand{
    
    private String time;
    private Status status;
    
    public ModemTime() {
        super(new byte[] {(byte)0x20, (byte)0x01});
    }
    
    public String getTime() {
        return time;
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
        String yyyymmddhhmmss = (String)info.get("yyyymmddhhmmss");
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        byte[] b = DataUtil.get2ByteToInt(Integer.parseInt(yyyymmddhhmmss.substring(0, 4)));
        datas[0].setValue(new byte[]{b[0], b[1],
                DataUtil.getByteToInt(Integer.parseInt(yyyymmddhhmmss.substring(4,6))),
                DataUtil.getByteToInt(Integer.parseInt(yyyymmddhhmmss.substring(6,8))),
                DataUtil.getByteToInt(Integer.parseInt(yyyymmddhhmmss.substring(8,10))),
                DataUtil.getByteToInt(Integer.parseInt(yyyymmddhhmmss.substring(10,12))),
                DataUtil.getByteToInt(Integer.parseInt(yyyymmddhhmmss.substring(12,14)))});
        
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }

    @Override
    public void decode(byte[] data) {
        // status result
        if (data.length == 2) {
            byte[] b = new byte[2];
            System.arraycopy(data, 0, b, 0, b.length);

            for (Status s : Status.values()) {
                if (s.getCode()[0] == b[0] && s.getCode()[1] == b[1]) {
                    status = s;
                    break;
                }
            }
        }
        else if (data.length == 7) {
            byte[] b = new byte[7];
            System.arraycopy(data, 0, b, 0, b.length);
            
            time = String.format("%4d%02d%02d%02d%02d%02d", 
                    DataUtil.getIntTo2Byte(new byte[]{b[0], b[1]}),
                    DataUtil.getIntToByte(b[2]),
                    DataUtil.getIntToByte(b[3]),
                    DataUtil.getIntToByte(b[4]),
                    DataUtil.getIntToByte(b[5]),
                    DataUtil.getIntToByte(b[6]));
        }
    }

    @Override
    public String toString() {
        return "[ModemTime]"+
                "[status:"+status.name()+"]"+
                "[time:"+time+"]";
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
