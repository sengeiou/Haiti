package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class NullBypassClose extends AbstractCommand{
    private int status;
    private String statusStr;
    
    public NullBypassClose() {
        super(new byte[] {(byte)0xC1, (byte)0x02});
    }
    
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	// SP-575 add start
	public String getStatusStr() {
		return statusStr;
	}

	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}
	// SP-575 add end

    @Override
    public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);

		status = DataUtil.getIntToBytes(b);
		// SP-575 add start
		if(status == 0) {
			statusStr = "Terminated gracefully by command(0)";
		}
		else if(status == 1) {
			statusStr = "Termination of bypass due to timeout(1)";
		}
		else if(status == 2) {
			statusStr = "Port Mismatch(2)";
		}
		else {
			statusStr = String.format("%d",status);
		}
		// SP-575 add end
    }

    @Override
    public  Command get() throws Exception {
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
        datas[0].setValue(DataUtil.get2ByteToInt((int)info.get("port")));
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
    public String toString() {
        return "[NullBypassClose]"+
                "[status:"+status+"]";
    }

    // SP-575 add start
    public String toString2() {
        return "Status: " + statusStr;
    }
    // SP-575 add end

    @Override
    public void decode(byte[] p1, CommandType p2) throws Exception{}

    @Override
    public Command get(HashMap p) throws Exception{return null;}

    @Override
    public Command set() throws Exception{return null;}

}
