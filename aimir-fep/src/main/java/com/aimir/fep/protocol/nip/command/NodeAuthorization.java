package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class NodeAuthorization extends AbstractCommand{
    
    private int nodeCount;
    private AuthorizationInfo[] authorizationInfo;
    private int backoffTime;
    private int statusCode;
    
    public NodeAuthorization() {
        super(new byte[] {(byte)0xC3, (byte)0x05});
    }
    
    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    // SP-575 add start
    public int getBackoffTime() {
        return backoffTime;
    }

    public void setBackoffTime(int backoffTime) {
        this.backoffTime = backoffTime;
    }
    // SP-575 add end

    @Override
    public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        nodeCount = DataUtil.getIntTo2Byte(b);
        pos += b.length;
        
        authorizationInfo = new AuthorizationInfo[nodeCount];
        for(int i=0;i<authorizationInfo.length-1;i++){
        	 authorizationInfo[i] = new AuthorizationInfo();
        	 b = new byte[8];
             System.arraycopy(bx, pos, b, 0, b.length);
             authorizationInfo[i].setNodeEui(String.valueOf(DataUtil.getLongTo8Byte(b)));
             pos += b.length;
             
             b = new byte[1];
             System.arraycopy(bx, pos, b, 0, b.length);
             authorizationInfo[i].setAuthorizationStatus(b[0]);
             pos += b.length;
        }
    }

	// SP-575 add start
    public void decode2(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        nodeCount = DataUtil.getIntTo2Byte(b);
        pos += b.length;
        
        authorizationInfo = new AuthorizationInfo[nodeCount];
        for(int i=0;i<authorizationInfo.length;i++){
        	 authorizationInfo[i] = new AuthorizationInfo();
        	 b = new byte[8];
             System.arraycopy(bx, pos, b, 0, b.length);
             authorizationInfo[i].setNodeEui(Hex.decode(b));
             pos += b.length;
             
             b = new byte[1];
             System.arraycopy(bx, pos, b, 0, b.length);
             statusCode = b[0];
             authorizationInfo[i].setAuthorizationStatus(b[0]);
             pos += b.length;
             
             b = new byte[2];
             System.arraycopy(bx, pos, b, 0, b.length);
             backoffTime = DataUtil.getIntTo2Byte(b);
             pos += b.length;
        }
    }
	// SP-575 add end
	
    @Override
    public Command get(HashMap info) throws Exception {
       Command command = new Command();
       Command.Attribute attr = command.newAttribute();
       Command.Attribute.Data[] datas = attr.newData(1);
       
       command.setCommandFlow(CommandFlow.Request);
       command.setCommandType(CommandType.Get);
       datas[0].setId(getAttributeID());
       
       int nodeCount = (int)info.get("nodeCount");
       String[] euiList = (String[])info.get("euiList");
       
       datas[0].setValue(DataUtil.get2ByteToInt(nodeCount));
       for(int i=0;i<nodeCount;i++){
    	   datas[0].setValue(DataUtil.get8ByteToInt(Long.parseLong(euiList[i])));
       }
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
       command.setCommandType(CommandType.Get);
       datas[0].setId(getAttributeID());
       int nodeCount = (int)info.get("nodeCount");
       AuthorizationInfo[] authorizationInfo = (AuthorizationInfo[])info.get("authorizationInfo");
       
       datas[0].setValue(DataUtil.get2ByteToInt(nodeCount));
       for(int i=0;i<nodeCount;i++){
    	   datas[0].setValue(DataUtil.get8ByteToInt(Long.parseLong(authorizationInfo[i].getNodeEui())));
    	   datas[0].setValue((new byte[]{authorizationInfo[i]._authorizationStatus.getCode()}));
       }
       attr.setData(datas);
       command.setAttribute(attr);
       return command;
    }

    @Override
    public String toString() {
        StringBuffer rtn= new StringBuffer();
	    
        for(int i=0; i< authorizationInfo.length-1 ;i++){
            rtn.append("[i:"+i+"]");
            rtn.append("[NodeEui:"+((AuthorizationInfo)authorizationInfo[i]).getNodeEui()+"]");
            rtn.append("[AuthorizationStatus:"+((AuthorizationInfo)authorizationInfo[i])._authorizationStatus.name()+"]");
        }

        return "[NodeAuthorization]"+
        "[nodeCount:"+nodeCount+"]"+
        rtn.toString();	   
	}

    // SP-575 add start
    public String toString2() {
        StringBuffer rtn= new StringBuffer();
	    
    	rtn.append("Authorization Info: \n");
        for(int i=0; i< authorizationInfo.length ;i++){
            rtn.append("[Node Eui: "+((AuthorizationInfo)authorizationInfo[i]).getNodeEui()+", ");
            
            if(authorizationInfo[i]._authorizationStatus == null){
            	rtn.append("Authorization Status: (" + "0x" + String.format("%x", statusCode)+ "), ");
            }
            else{
                byte status = authorizationInfo[i]._authorizationStatus.getCode();
                switch(status){
                    case 0x00:
                        rtn.append("Authorization Status: HES not authorized, security unauthenticated status(0x00), ");
                        break;
                    case 0x01:
                        rtn.append("Authorization Status: HES permission, security unauthenticated status (3-PASS)(0x01), ");
                        break;
                    case 0x02:
                        rtn.append("Authorization Status: HES permission, security authentication status (3-PASS)(0x02), ");
                        break;
                    case 0x11:
                        rtn.append("Authorization Status: Security Authentication Status (PANA)(0x11), ");
                        break;
                    case 0x12:
                        rtn.append("Authorization Status: Security Failure Backoff(0x12), ");
                        break;
                    case (byte)0xFF:
                        rtn.append("Authorization Status: No node information(0xFF), ");
                        break;
                    default:
                        rtn.append("Authorization Status: (" + "0x" + String.format("%x", statusCode)+ "), ");
                        break;
                }
            	
            }
            rtn.append("Backoff Time: "+getBackoffTime()+"]");
            if(i != (authorizationInfo.length-1)){
            	rtn.append(", ");
            }
            rtn.append("\n");
        }

        return "Node Count: "+nodeCount+", \n"+
        rtn.toString();	   
    }
    // SP-575 add end

    @Override
    public void decode(byte[] p1, CommandType p2) throws Exception{}

    @Override
    public Command get() throws Exception{return null;}
    
    @Override
    public Command set() throws Exception{return null;}

    @Override
    public Command trap() throws Exception{return null;}
	
}
