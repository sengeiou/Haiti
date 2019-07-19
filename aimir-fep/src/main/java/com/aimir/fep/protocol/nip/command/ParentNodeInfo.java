package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class ParentNodeInfo extends AbstractCommand{
    private String eui;
    private int type;
    private int state;
    private int distance;
    private int coordinator;
    private int hop;
    private int etx;
    private String lifeTime;
    private int ver;
    private int dtsn;
    private String txCnt;
    private String txFail;
    private String rx;
    private int rssi;
    private int lqi;
    private int index;
    
    public ParentNodeInfo() {
        super(new byte[] {(byte)0xC3, (byte)0x01});
    }
    
	public String getEui() {
		return eui;
	}

	public void setEui(String eui) {
		this.eui = eui;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
	public int getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(int coordinator) {
		this.coordinator = coordinator;
	}

	public int getHop() {
		return hop;
	}

	public void setHop(int hop) {
		this.hop = hop;
	}

	public int getEtx() {
		return etx;
	}

	public void setEtx(int etx) {
		this.etx = etx;
	}

	public String getLifeTime() {
		return lifeTime;
	}

	public void setLifeTime(String lifeTime) {
		this.lifeTime = lifeTime;
	}

	public int getVer() {
		return ver;
	}

	public void setVer(int ver) {
		this.ver = ver;
	}

	public int getDtsn() {
		return dtsn;
	}

	public void setDtsn(int dtsn) {
		this.dtsn = dtsn;
	}

    // SP-575 add start
    public String getTxCnt() {
        return txCnt;
    }

    public void setTxCnt(String txCnt) {
        this.txCnt = txCnt;
    }

    public String getTxFail() {
        return txFail;
    }

    public void setTxFail(String txFail) {
        this.txFail = txFail;
    }

    public String getRx() {
        return rx;
    }

    public void setRx(String rx) {
        this.rx = rx;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getLqi() {
        return lqi;
    }

    public void setLqi(int lqi) {
        this.lqi = lqi;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    // SP-575 add end

	@Override
	public void decode(byte[] bx) {
		
        int pos = 0;
        byte[] b = new byte[8];
        System.arraycopy(bx, pos, b, 0, b.length);
        eui = String.valueOf(DataUtil.getIntToBytes(b));
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        type= DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        state= DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        distance= DataUtil.getIntTo2Byte(b);
        pos += b.length;
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        etx= DataUtil.getIntTo2Byte(b);
        pos += b.length;
        
        b = new byte[4];
        System.arraycopy(bx, pos, b, 0, b.length);
        lifeTime= String.valueOf(DataUtil.getIntTo4Byte(b));
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        ver= DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        dtsn= DataUtil.getIntToByte(b[0]);
        
    }
	
	// SP-575 add start
	public void decode2(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        index= DataUtil.getIntTo2Byte(b);
        pos += b.length;
 
        b = new byte[8];
        System.arraycopy(bx, pos, b, 0, b.length);
        eui = Hex.decode(b);
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        type= DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        state= DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        // distance
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        hop= DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        coordinator= DataUtil.getIntToByte(b[0]);
        pos += b.length;

        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        etx= DataUtil.getIntTo2Byte(b);
        pos += b.length;
        b = new byte[4];
        System.arraycopy(bx, pos, b, 0, b.length);
        lifeTime= String.valueOf(getIntTo4Byte(b));
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        ver= DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        dtsn= DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        b = new byte[4];
        System.arraycopy(bx, pos, b, 0, b.length);
        txCnt= String.valueOf(getIntTo4Byte(b));
        pos += b.length;
        
        b = new byte[4];
        System.arraycopy(bx, pos, b, 0, b.length);
        txFail= String.valueOf(getIntTo4Byte(b));
        pos += b.length;

        b = new byte[4];
        System.arraycopy(bx, pos, b, 0, b.length);
        rx= String.valueOf(getIntTo4Byte(b));
        pos += b.length;

        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        if((b[0] & 0x80) == 0x80){
        	rssi = -(b[0] & 0x7F);
        }
        else{
        	rssi = b[0] & 0x7F;
        }
        pos += b.length;
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        lqi= DataUtil.getIntToByte(b[0]);
    }
	
	private int getIntTo4Byte(byte[] b){
    	byte[] bTmp = new byte[4];
    	System.arraycopy(b, 0, bTmp, 0, bTmp.length);
        int val = 0;
        val = ((bTmp[0] & 0x7f) << 24)
                    + ((bTmp[1] & 0xff) << 16)
                    + ((bTmp[2] & 0xff) << 8)
                    + (bTmp[3] & 0xff);
        if((bTmp[0] & 0x80) == 0x80){
            val = -val;
        }
		return val;
    }
	// SP-575 add end

	@Override
	public String toString() {
	    return "[ParentNodeInfo]"+
	    	   "[eui:"+eui+"]"+
	    	   "[type:"+type+"]"+
	    	   "[state:"+state+"]"+
	    	   "[distance:"+distance+"]"+
	    	   "[etx:"+etx+"]"+
	    	   "[lifeTime:"+lifeTime+"]"+
	    	   "[ver:"+ver+"]"+
	    	   "[dtsn:"+dtsn+"]";
	}

	// SP-575 add start
	public String toString2() {
		String typeStr = getTypeStr(type);
		String stateStr = getStateStr(state);
		String distanceStr = getDistanceStr(coordinator, hop);
	    return "Index: "+index+", "+
	    	   "EUI: "+eui+", "+
	    	   "Type: "+typeStr+", \n"+
	    	   "State: "+stateStr+", "+
	    	   "Distance: "+distanceStr+", \n"+
	    	   "ETX: "+etx+", "+
	    	   "LifeTime: "+lifeTime+", "+
	    	   "Dodag_ver: "+ver+", \n"+
	    	   "Dtsn:"+dtsn+", "+
	    	   "Tx_cnt: "+txCnt+", \n"+
	    	   "Tx_fail: "+txFail+", "+
	    	   "Rx: "+rx+", \n"+
	    	   "Rssi: "+rssi+", "+
	    	   "Lqi: "+lqi;
	}
	// SP-575 add end

	// SP-575 add start
	private String getTypeStr(int type) {
        String rtnStr = "";
        switch(type){
            case 0:
                rtnStr = "Coordinator(0)";
                break;
            case 1:
                rtnStr = "Router(1)";
                break;
            case 2:
                rtnStr = "Host(2)";
                break;
            case 3:
                rtnStr = "Sleepy Host(3)";
                break;
            default:
                rtnStr = String.format("%d", type);
                break;
        }
        return rtnStr;
	}

	private String getStateStr(int state) {
        String rtnStr = "";
        switch(state){
            case 0:
                rtnStr = "None (communication impossible)(0)";
                break;
            case 1:
                rtnStr = "Neighbor Discovery Incomplete(1)";
                break;
            case 2:
                rtnStr = "Neighbor Discovery Probe (communication impossible)(2)";
                break;
            case 3:
                rtnStr = "Neighbor Discovery Delay (communication impossible)(3)";
                break;
            case 4:
                rtnStr = "Stale (Tx only for that node)(4)";
                break;
            case 5:
                rtnStr = "Reachable (Tx Rx all possible)(5)";
                break;
            default:
                rtnStr = String.format("%d", state);
                break;
        }
        return rtnStr;
	}

	public String getDistanceStr(int coordinator, int hop) {
		String rtnStr = "";
        if(coordinator == 1){
        	rtnStr = "Coordinator";
        }
        if(hop > 0){
        	if (rtnStr.length() > 0){
        		rtnStr = rtnStr + ",";
        	}
        	rtnStr = rtnStr + String.format("%d hop node", hop);
        }
        
        return String.format("%s(0x%s)", rtnStr, String.format("%02X%02X", hop, coordinator));
	}
	// SP-575 add end

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
	public void decode(byte[] p1, CommandType p2) throws Exception{}
	
	@Override
	public Command get(HashMap p) throws Exception{return null;}
	
	@Override
	public Command set() throws Exception{return null;}
	
	@Override
	public Command set(HashMap p) throws Exception{return null;}
	
	@Override
	public Command trap() throws Exception{return null;}
}
