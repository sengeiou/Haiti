package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class CoordinatorInformation extends AbstractCommand{
    private String panId;
    private String networkKey;
    private String rfPower;
    private Channel newworkChannel;
    private int nwChannelCode;
    
    public CoordinatorInformation() {
        super(new byte[] {(byte)0xA0, (byte)0x01});
    }
    
	public enum Channel {
        Channel1((new double[]{(double)1,(double)917.3})),
        Channel2((new double[]{(double)2,(double)917.9})),
        Channel3((new double[]{(double)3,(double)918.5})),
        Channel4((new double[]{(double)4,(double)919.1})),
        Channel5((new double[]{(double)5,(double)919.7})),
        Channel6((new double[]{(double)6,(double)920.3})),
        Channel7((new double[]{(double)7,(double)920.7})),
        Channel8((new double[]{(double)8,(double)920.9})),
        Channel9((new double[]{(double)9,(double)921.1})),
        Channel10((new double[]{(double)10,(double)921.3})),
        Channel11((new double[]{(double)11,(double)921.5})),
        Channel12((new double[]{(double)12,(double)921.7})),
        Channel13((new double[]{(double)13,(double)921.9})),
        Channel14((new double[]{(double)14,(double)922.1})),
        Channel15((new double[]{(double)15,(double)922.3})),
        Channel16((new double[]{(double)16,(double)922.5})),
        Channel17((new double[]{(double)17,(double)922.7})),
        Channel18((new double[]{(double)18,(double)922.9})),
        Channel19((new double[]{(double)19,(double)923.1})),
        Channel20((new double[]{(double)20,(double)923.3}));
        
        private double[] code;
        
        Channel(double[] code) {
            this.code = code;
        }
        
        public double[] getCode() {
            return this.code;
        }
    }

	public String getPanId() {
		return panId;
	}

	public void setPanId(String panId) {
		this.panId = panId;
	}

	public String getNetworkKey() {
		return networkKey;
	}

	public void setNetworkKey(String networkKey) {
		this.networkKey = networkKey;
	}

	public String getRfPower() {
		return rfPower;
	}

	public void setRfPower(String rfPower) {
		this.rfPower = rfPower;
	}

	@Override
	public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        for (Channel m : Channel.values()) {
            if (m.getCode()[0] == (double)DataUtil.getIntToByte(b[0])) {
            	newworkChannel = m;
                break;
            }
        }
        pos += b.length;
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        panId = String.valueOf(DataUtil.getIntTo2Byte(b));
        pos += b.length;
        
        b = new byte[16];
        System.arraycopy(bx, pos, b, 0, b.length);
        networkKey = String.valueOf(DataUtil.getIntToBytes(b));
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        rfPower = String.valueOf(DataUtil.getIntToByte(b[0]));
    }

	//SP-575 add start
	public void decode2(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        for (Channel m : Channel.values()) {
            if (m.getCode()[0] == (double)DataUtil.getIntToByte(b[0])) {
            	newworkChannel = m;
                break;
            }
        }
        nwChannelCode = DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        panId = String.valueOf(DataUtil.getIntTo2Byte(b));
        pos += b.length;
        
        b = new byte[16];
        System.arraycopy(bx, pos, b, 0, b.length);
        networkKey = Hex.decode(b);
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        rfPower = Hex.decode(b);
    }
	//SP-575 add end

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
	    return "[CoordinatorInformation]"+
	    	   "[newworkChannel:"+newworkChannel.name()+"]"+
	    	   "[panId:"+panId+"]"+
	    	   "[networkKey:"+networkKey+"]"+
	    	   "[rfPower:"+rfPower+"]";
	}
	
	//SP-575 add start
	public String toString2() {
		String newworkChannelStr = getTargetTypeStr(newworkChannel);
	    return "Network Channel: "+newworkChannelStr+", "+
 	   "Pan ID: "+panId+", "+
 	   "Network Key: "+networkKey+", "+
 	   "RF Power: "+"0x" +rfPower;
	}
	
    private String getTargetTypeStr(Channel cn){
        String rtnStr = "";
        if(cn == null){
        	rtnStr = String.format("%02x", nwChannelCode);
        }
        else{
        	String hexaCodeStr = "0x" + String.format("%02x", nwChannelCode);
        	switch(cn){
        	case Channel1:
        		rtnStr = "Channe 1 = 917.3(" + hexaCodeStr + ")";
    			break;
        	case Channel2:
        		rtnStr = "Channe 2 = 917.9(" + hexaCodeStr + ")";
    			break;
        	case Channel3:
        		rtnStr = "Channe 3 = 918.5(" + hexaCodeStr + ")";
    			break;
        	case Channel4:
        		rtnStr = "Channe 4 = 919.1(" + hexaCodeStr + ")";
    			break;
        	case Channel5:
        		rtnStr = "Channe 5 = 919.7(" + hexaCodeStr + ")";
    			break;
        	case Channel6:
        		rtnStr = "Channe 6 = 920.3(" + hexaCodeStr + ")";
    			break;
        	case Channel7:
        		rtnStr = "Channe 7 = 920.7(" + hexaCodeStr + ")";
    			break;
        	case Channel8:
        		rtnStr = "Channe 8 = 920.9(" + hexaCodeStr + ")";
    			break;
        	case Channel9:
        		rtnStr = "Channe 9 = 921.1(" + hexaCodeStr + ")";
    			break;
        	case Channel10:
        		rtnStr = "Channe 10 = 921.3(" + hexaCodeStr + ")";
    			break;
        	case Channel11:
        		rtnStr = "Channe 11 = 921.5(" + hexaCodeStr + ")";
    			break;
        	case Channel12:
        		rtnStr = "Channe 12 = 921.7(" + hexaCodeStr + ")";
    			break;
        	case Channel13:
        		rtnStr = "Channe 13 = 921.9(" + hexaCodeStr + ")";
    			break;
        	case Channel14:
        		rtnStr = "Channe 14 = 922.1(" + hexaCodeStr + ")";
    			break;
        	case Channel15:
        		rtnStr = "Channe 15 = 922.3(" + hexaCodeStr + ")";
    			break;
        	case Channel16:
        		rtnStr = "Channe 16 = 922.5(" + hexaCodeStr + ")";
    			break;
        	case Channel17:
        		rtnStr = "Channe 17 = 922.7(" + hexaCodeStr + ")";
    			break;
        	case Channel18:
        		rtnStr = "Channe 18 = 922.9(" + hexaCodeStr + ")";
    			break;
        	case Channel19:
        		rtnStr = "Channe 19 = 923.1(" + hexaCodeStr + ")";
    			break;
        	case Channel20:
        		rtnStr = "Channe 20 = 923.3(" + hexaCodeStr + ")";
    			break;
            }
        }
    	return rtnStr;
    }
    //SP-575 add end
	
	@Override
	public Command get(HashMap p) throws Exception{return null;}
	@Override
	public Command set() throws Exception{return null;}
	@Override
	public Command set(HashMap p) throws Exception{return null;}

    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
        
    }

}
