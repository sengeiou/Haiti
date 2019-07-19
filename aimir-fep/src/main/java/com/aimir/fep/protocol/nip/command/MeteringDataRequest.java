package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.NiProtocolEventCode.NIPEventCode;
import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class MeteringDataRequest extends AbstractCommand{

    public MeteringDataRequest() {
        super(new byte[] {(byte)0xC2, (byte)0x04});
    }
    
    private int paramCount;
    public int getParamCount() {
        return paramCount;
    }
    public void setParamCount(int paramCount) {
        this.paramCount = paramCount;
    }
        
    private String[] parameters;   
    public String[] getParameters() {
        return parameters;
    }
    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }
    
    private byte[] data; 
    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }    

    private Status status;
    private int statusCode;
    
    public Status getStatus() {
        return status;
    }
    
    @Override
    public Command get(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Get);
        datas[0].setId(getAttributeID());
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
    	out.write(DataUtil.getByteToInt(1)); // Header Type 0x01 (fixed for SORIA)
    	out.write(DataUtil.getByteToInt(1)); // Metering Data Type 0x01 (fixed for SORIA)       
        
        Object obj = info.get("count");
        if (obj instanceof Integer){
        	out.write(DataUtil.getByteToInt((int)obj));
        }else if (obj instanceof String){
        	out.write(DataUtil.getByteToInt(Integer.parseInt((String)obj)));
        }
        
        obj = info.get("parameters");
        String[] parameters = (String[])obj;
        for(int i=0; i < parameters.length; i++) {
        	out.write(DataUtil.get2ByteToInt(Integer.parseInt(parameters[i].substring(0,4))));	// YYYY
        	out.write(DataUtil.getByteToInt(Integer.parseInt(parameters[i].substring(4,6))));	// MM
        	out.write(DataUtil.getByteToInt(Integer.parseInt(parameters[i].substring(6,8))));	// DD
        	out.write(DataUtil.getByteToInt(Integer.parseInt(parameters[i].substring(8,10))));	// hh
        	out.write(DataUtil.getByteToInt(Integer.parseInt(parameters[i].substring(10,12))));	// mm
        }
        
        
        datas[0].setValue(out.toByteArray());
        
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
    
    @Override
    public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[2];
        System.arraycopy(bx, 0, b, 0, b.length);
        // SP-575 add start
        statusCode = DataUtil.getIntTo2Byte(b);
        // SP-575 add end
        for (Status s : Status.values()) {
            if (s.getCode()[0] == b[0] && s.getCode()[1] == b[1]) {
                status = s;
                break;
            }
        }
        
        pos += b.length;
        
        b = new byte[bx.length - pos];
        System.arraycopy(bx, pos, b, 0, b.length);
        data = b;   
        
    }
    
    @Override
    public String toString() {
        StringBuffer buf= new StringBuffer();

        // SP-575 add start
        String statusStr = "";
        String resStr = "";
        statusStr = getStatusStr();
        resStr = Hex.decode(data);
        buf.append("Status: ");
        buf.append(statusStr);
        if(status != null){
            if(status.name().equals("Success")){
                buf.append(", "); 
                buf.append("Response Data: ");
                buf.append(resStr);
            }
        }
        // SP-575 add end
        return buf.toString();
    }

    // SP-575 add start    
    private String getStatusStr(){
    	String rtnStr = "";
    	if(status == null){
			rtnStr = "0x" + String.format("%04x", statusCode);
			return rtnStr;
    	}
    	switch(status){
    	case Success:
    		rtnStr = "Success(0x0000)";
			break;
    	case FormatError:
    		rtnStr = "Format Error(0x1001)";
			break;
    	case ParameterError:
    		rtnStr = "Parameter Error(0x1002)";
			break;
    	case ValueOverflow:
    		rtnStr = "Value Overflow Error(0x1003)";
			break;
    	case InvalidAttrId:
    		rtnStr = "Invalid Attribute Id(0x1004)";
			break;
    	case AuthorizationError:
    		rtnStr = "Authorization Error(0x10005)";
			break;
    	case NoDataError:
    		rtnStr = "No Data Error(0x1006)";
			break;
    	case MeteringBusy:
    		rtnStr = "Metering Busy(0x2000)";
			break;
    	case Unknown:
    		rtnStr = "Unknown(0xFF00)";
			break;
    	}
    	return rtnStr;
    }
    // SP-575 add end
 
    @Override
    public Command get() throws Exception{return null;}
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
