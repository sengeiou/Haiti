package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class CloneOnOff extends AbstractCommand {
	public CloneOnOff() {
		super(new byte[] {(byte)0x00, (byte)0x0B});
	}	
	
	private String cloneCode="empty";
	private int cloneCount=-1;
	private String cloneOperatingTime;
	private String crc;
	private boolean optionFlg=false;
	
	
	public int getCloneCount() {
		return this.cloneCount;
	}
	
	public String getCloneCode() {
		return this.cloneCode;
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

		
		// INSERT START SP-681
		cloneCode = "0314";
        Object obj = info.get("code");
        if ((obj != null) && (obj instanceof String)) {
        	cloneCode = (String) obj;
		}		
		// INSERT END SP-681

		// Clone Count
		cloneCount = -1;
        obj = info.get("count");
		if (obj instanceof Integer) {
			cloneCount = (int)obj;
		} else if (obj instanceof String) {
			cloneCount = Integer.parseInt((String) obj);
		}
			
        obj = info.get("cloneOperatingTime");
        if ((obj != null) && (obj instanceof String)) {
        	cloneOperatingTime = (String) obj;
		}
        
        obj = info.get("crc");
        if ((obj != null) && (obj instanceof String)) {
        	crc = (String) obj;
		}

		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			out.write(Hex.encode(cloneCode));			
			out.write(DataUtil.getByteToInt(cloneCount));

	        // SP-1100
			//Clone operating time
	        if(cloneOperatingTime != null && !cloneOperatingTime.equals("")) {
	        	out.write(DataUtil.readByteString(cloneOperatingTime));
	        }
			//CRC
	        if(crc != null && !crc.equals("")) {
	        	out.write(DataUtil.readByteString(crc));
	        }
	        
			log.debug("## CloneOn/Off params HEX = [" + Hex.decode(out.toByteArray()) + "]");
			// INSERT END SP-681
			
			datas[0].setValue(out.toByteArray());
			
			attr.setData(datas);
			command.setAttribute(attr);
			return command;
		} catch(Exception e) {
        	log.error(e);
        	throw e;
        }
		finally {
            if (out != null) out.close();
        }
	}
		
	@Override
	public void decode(byte[] data) throws Exception {
		//log.info("## Clone On/OFF bx :" + Hex.decode(data));
		
		// clone code(2byte==0x0314)
		byte[] b = new byte[2];
		System.arraycopy(data, 0, b, 0, b.length);
		cloneCode =  Hex.decode(b);
		
		// clone count(1byte)
		byte[] c = new byte[1];
		System.arraycopy(data, b.length, c, 0, c.length);
		cloneCount = DataUtil.getIntToByte(c[0]);
		
        // SP-575 add start
        int len = 0;
        int pos = 0;
        len = data.length;
        pos = 3;
        // The option exists.
        if(len > 3) {
        	optionFlg = true;
            
        	b = new byte[7];
            System.arraycopy(data, pos, b, 0, b.length);
            pos += b.length;
            cloneOperatingTime = DataUtil.getTimeStamp(b);
            
        	b = new byte[2];
            System.arraycopy(data, pos, b, 0, b.length);
            pos += b.length;
            crc = Hex.decode(b);
        }
        // SP-575 add end
        
        log.info("## Clone On/OFF cloneCode :" + cloneCode + ", cloneCount :" + cloneCount + ", cloneOperatingTime : " + cloneOperatingTime + ", crc : " + crc);
	}
	
	public String toString() {
		String toCode = cloneCode;
		String toCount = cloneCount<0? "Unknown":Integer.toString(cloneCount);
		return "[Clone OnOff][CloneCode: "+ toCode + "][CloneCount: "+toCount+"*15min]";
	}

	// SP-575 add start
	public String toString2() {
		StringBuffer rtn = new StringBuffer();
		String cloneCodeStr = "";
     	if(cloneCode.equals("0314")){
     		cloneCodeStr = "Use your own image when cloning(automatic propagation True)(0x0314)";
     	}
		else if(cloneCode.equals("0315")){
			cloneCodeStr = "Use your own image when cloning(automatic propagation False)(0x0315)";
     	}
		else if(cloneCode.equals("8798")){
			cloneCodeStr = "Use clone system image(auto radio True)(0x8798)";
     	}
		else if(cloneCode.equals("8799")){
			cloneCodeStr = "Use clone system image(auto propagation False)(0x8799)";
     	}
		else{
			cloneCodeStr = "0x" + cloneCode;
		}
		if(optionFlg) {
			rtn.append("Clone Code: "+ cloneCodeStr + ", " + "Clone Count: "+ cloneCount + ", \n");
			rtn.append("Clone Opearting time: "+ cloneOperatingTime + ", ");
			rtn.append("CRC: "+ crc + ", \n");
			rtn.append("]");
		}
		else {
			rtn.append("Clone Code: "+ cloneCodeStr + ", " + "Clone Count: "+ cloneCount);
		}
		return rtn.toString();
	}
	// SP-575 add end

	@Override
	public void decode(byte[] bx, CommandType commandType) throws Exception {
		log.info("bx :" + bx + ", " + "commandType : " + commandType);
	}

	@Override
    public Command get(HashMap info) throws Exception {
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
