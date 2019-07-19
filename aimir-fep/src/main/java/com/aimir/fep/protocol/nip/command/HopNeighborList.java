package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class HopNeighborList extends AbstractCommand{
    private int neighborCount;
    private NodeInformationTable[] nodeInformationTable;
    
    public HopNeighborList() {
        super(new byte[] {(byte)0xC3, (byte)0x03});
    }

    public int getNeighborCount() {
        return neighborCount;
    }

    public void setNeighborCount(int neighborCount) {
        this.neighborCount = neighborCount;
    }
    
    @Override
    public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        neighborCount = DataUtil.getIntTo2Byte(b);
        pos += b.length;
        
        nodeInformationTable = new NodeInformationTable[neighborCount];
        for(int i=0;i<nodeInformationTable.length-1;i++){
            nodeInformationTable[i] = new NodeInformationTable();
            b = new byte[8];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setEui(String.valueOf(DataUtil.getIntToBytes(b)));
            pos += b.length;
             
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setType(DataUtil.getIntToByte(b[0]));
            pos += b.length;
             
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setState(DataUtil.getIntToByte(b[0]));
            pos += b.length;
             
            b = new byte[2];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setDistance(DataUtil.getIntTo2Byte(b));
            pos += b.length;
             
            b = new byte[2];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setEtx(DataUtil.getIntTo2Byte(b));
            pos += b.length;
             
            b = new byte[4];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setLifeTime(String.valueOf(DataUtil.getIntTo4Byte(b)));
            pos += b.length;
             
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setVer(DataUtil.getIntToByte(b[0]));
            pos += b.length;
             
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setDtsn(DataUtil.getIntToByte(b[0]));
        }
    }
    
    // SP-575 add start
    public void decode2(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        neighborCount = DataUtil.getIntTo2Byte(b);
        pos += b.length;
        
        nodeInformationTable = new NodeInformationTable[neighborCount];
        for(int i=0;i<nodeInformationTable.length;i++){
            nodeInformationTable[i] = new NodeInformationTable();
            b = new byte[2];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setIndex(DataUtil.getIntTo2Byte(b));
            pos += b.length;
            
            b = new byte[8];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setEui(Hex.decode(b));
            pos += b.length;
             
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setType(DataUtil.getIntToByte(b[0]));
            pos += b.length;
             
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setState(DataUtil.getIntToByte(b[0]));
            pos += b.length;
             
            // distance
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setHop(DataUtil.getIntToByte(b[0]));
            pos += b.length;
            
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setCoordinator(DataUtil.getIntToByte(b[0]));
            pos += b.length;

            b = new byte[2];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setEtx(DataUtil.getIntTo2Byte(b));
            pos += b.length;
             
            b = new byte[4];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setLifeTime(String.valueOf(getIntTo4Byte(b)));
            pos += b.length;
             
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setVer(DataUtil.getIntToByte(b[0]));
            pos += b.length;
             
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setDtsn(DataUtil.getIntToByte(b[0]));
            pos += b.length;
            
            b = new byte[4];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setTxCnt(String.valueOf(getIntTo4Byte(b)));
            pos += b.length;
            
            b = new byte[4];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setTxFail(String.valueOf(getIntTo4Byte(b)));
            pos += b.length;
            
            b = new byte[4];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setRx(String.valueOf(getIntTo4Byte(b)));
            pos += b.length;
            
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);       
            int tmpRssi = 0;
            if((b[0] & 0x80) == 0x80){
            	tmpRssi = -(b[0] & 0x7F);
            }
            else{
            	tmpRssi = b[0] & 0x7F;
            }
            nodeInformationTable[i].setRssi(tmpRssi);
            pos += b.length;
            
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            nodeInformationTable[i].setLqi(DataUtil.getIntToByte(b[0]));
        }
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
    public String toString() {
		 StringBuffer rtn= new StringBuffer();
		 for(int i=0; i< nodeInformationTable.length-1 ;i++){
		     rtn.append("[i:"+i+"]");
		     rtn.append("[eui:"+((NodeInformationTable)nodeInformationTable[i]).getEui()+"]");
		     rtn.append("[type:"+((NodeInformationTable)nodeInformationTable[i]).getType()+"]");
		     rtn.append("[state:"+((NodeInformationTable)nodeInformationTable[i]).getState()+"]");
		     rtn.append("[distance:"+((NodeInformationTable)nodeInformationTable[i]).getDistance()+"]");
		     rtn.append("[etx:"+((NodeInformationTable)nodeInformationTable[i]).getEtx()+"]");
		     rtn.append("[lifeTime:"+((NodeInformationTable)nodeInformationTable[i]).getLifeTime()+"]");
		     rtn.append("[ver:"+((NodeInformationTable)nodeInformationTable[i]).getVer()+"]");
		     rtn.append("[dtsn:"+((NodeInformationTable)nodeInformationTable[i]).getDtsn()+"]");
		 }
		 return "[HopNeighborList]"+
		 "[neighborCount:"+neighborCount+"]"+
		 rtn.toString();	   
    }

	// SP-575 add start
    public String toString2() {
        StringBuffer rtn= new StringBuffer();
    	int type = 0;
    	int state = 0;
		String typeStr = "";
		String stateStr = "";
		String distanceStr = "";
    	rtn.append("\nNode Information Table: \n");
		for(int i=0; i< nodeInformationTable.length ;i++){
		    rtn.append("[Index: "+((NodeInformationTable)nodeInformationTable[i]).getIndex()+", ");
		    rtn.append("EUI: "+((NodeInformationTable)nodeInformationTable[i]).getEui()+", ");
		    type = ((NodeInformationTable)nodeInformationTable[i]).getType();
			typeStr = getTypeStr(type);
		    rtn.append(typeStr);
		    state = ((NodeInformationTable)nodeInformationTable[i]).getState();
			stateStr = getStateStr(state);
		    rtn.append(stateStr);
			distanceStr = getDistanceStr(((NodeInformationTable)nodeInformationTable[i]).getCoordinator(),
						((NodeInformationTable)nodeInformationTable[i]).getHop());
		    rtn.append(distanceStr);
		    rtn.append("EtX: "+((NodeInformationTable)nodeInformationTable[i]).getEtx()+", ");
		    rtn.append("Lifetime: "+((NodeInformationTable)nodeInformationTable[i]).getLifeTime()+", ");
		    rtn.append("Dodag_ver: "+((NodeInformationTable)nodeInformationTable[i]).getVer()+", ");
		    rtn.append("Dtsn: "+((NodeInformationTable)nodeInformationTable[i]).getDtsn()+", ");
		    rtn.append("Tx_cnt: "+((NodeInformationTable)nodeInformationTable[i]).getTxCnt()+", ");
		    rtn.append("Tx_fail: "+((NodeInformationTable)nodeInformationTable[i]).getTxFail()+", ");
		    rtn.append("Rx: "+((NodeInformationTable)nodeInformationTable[i]).getRx()+", ");
		    rtn.append("Rssi: "+((NodeInformationTable)nodeInformationTable[i]).getRssi()+", ");
		    rtn.append("Lqi: "+((NodeInformationTable)nodeInformationTable[i]).getLqi()+"]");
			if(i != (nodeInformationTable.length-1)) {
				rtn.append(", \n");
			}
		}
		return "Neighbor Count: "+neighborCount+", "+
		rtn.toString();
    }
	// SP-575 add end

	// SP-575 add start
	private String getTypeStr(int type) {
        String rtnStr = "";
        switch(type){
            case 0:
                rtnStr = "Type: Coordinator(0), ";
                break;
            case 1:
                rtnStr = "Type: Router(1), ";
                break;
            case 2:
                rtnStr = "Type: Host(2), ";
                break;
            case 3:
                rtnStr = "Type: Sleepy Host(3), ";
                break;
            default:
                rtnStr = "Type: "+ type +", ";
                break;
        }
        return rtnStr;
	}

	private String getStateStr(int state) {
        String rtnStr = "";
        switch(state){
            case 0:
                rtnStr = "State: None (communication impossible)(0), ";
                break;
            case 1:
                rtnStr = "State: Neighbor Discovery Incomplete(1), ";
                break;
            case 2:
                rtnStr = "State: Neighbor Discovery Probe (communication impossible)(2), ";
                break;
            case 3:
                rtnStr = "State: Neighbor Discovery Delay (communication impossible)(3), ";
                break;
            case 4:
                rtnStr = "State: Stale (Tx only for that node)(4), ";
                break;
            case 5:
                rtnStr = "State: Reachable (Tx Rx all possible)(5), ";
                break;
            default:
                rtnStr = "State: "+ state +", ";
                break;
        }
        return rtnStr;
	}

	private String getDistanceStr(int coordinator, int hop) {
    	ParentNodeInfo pni = new ParentNodeInfo();
        return "Distance:" + pni.getDistanceStr(coordinator, hop) + ", ";
	}
	// SP-575 add end

    public class NodeInformationTable{
        private int index;
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

        // SP-575 add start
        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    	// SP-575 add end

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
        // SP-575 add end
    }

    @Override
    public Command get(HashMap p) throws Exception{return null;}
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
