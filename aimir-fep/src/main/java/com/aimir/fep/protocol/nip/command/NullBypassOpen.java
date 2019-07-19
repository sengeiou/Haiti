package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class NullBypassOpen extends AbstractCommand {
	private int status = -1;
    private String statusStr;

	public NullBypassOpen() {
		super(new byte[] { (byte) 0xC1, (byte) 0x01 });
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
		byte[] b = new byte[2];
		System.arraycopy(bx, pos, b, 0, b.length);

		status = DataUtil.getIntTo2Byte(b);
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

		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			out.write(DataUtil.get2ByteToInt((int) info.get("port")));
			out.write(DataUtil.get2ByteToInt((int) info.get("timeout")));
			datas[0].setValue(out.toByteArray());
			attr.setData(datas);
			command.setAttribute(attr);
			return command;
		} finally {
			if (out != null)
				out.close();
		}
	}

	@Override
	public String toString() {
		return "[NullBypassOpen]" + "[status:" + status + "]";
	}

	// SP-575 add start
	public String toString2(String req) {
		statusStr(req);
		return "Status: " + statusStr;
	}

    private void statusStr(String req){
    	int val = 0;
		String hexaStr = "";
		byte[] b = new byte[2];
		StringBuffer buf = new StringBuffer();
		if(status == 0x0000) {
			if(req.equals("GET")){
				statusStr = "A null-bypassable state.(0x0000)";
			}
			else{
				statusStr = "Bypass open Success.(0x0000)";
			}
		}
		else {
			buf.append("This means that the system is proceeding null bypassing to "
					+ "\nthe corresponding port from another place. In this case,"
					+ "\nit is impossible to bypass null bypass.");

            buf.append("(0x");
            for(int i = 0; i < 2; i++) {
                val = b[i];
                val = val & 0xff;
                hexaStr = String.format("%02x",val);
                buf.append(hexaStr);
            }
            buf.append(")");
            statusStr = buf.toString();
		}
    }
	// SP-575 add end

	@Override
	public void decode(byte[] p1, CommandType p2) throws Exception {
	}

	@Override
	public Command get(HashMap p) throws Exception {
		return null;
	}

	@Override
	public Command set() throws Exception {
		return null;
	}

	@Override
	public Command trap() throws Exception {
		return null;
	}
}
