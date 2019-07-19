package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class ChildNodeList extends AbstractCommand{
    private static Log log = LogFactory.getLog(ChildNodeList.class);
    
    private int childCount;
    private ChildInformation[] childInformation;
    private int childIndex;
    
    public ChildNodeList() {
        super(new byte[] {(byte)0xC3, (byte)0x04});
    }
    
    public int getChildCount() {
		return childCount;
	}

	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}

	// SP-575 add start
    public int getChildIndex() {
		return childIndex;
	}

	public void setChildIndex(int childIndex) {
		this.childIndex = childIndex;
	}
	// SP-575 add end

	@Override
	public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        childCount = DataUtil.getIntTo2Byte(b);
        pos += b.length;
        log.debug("CHILD_COUNT[" + childCount + "]");
        
        childInformation = new ChildInformation[childCount];
        for(int i=0; i < childInformation.length; i++){
            childInformation[i] = new ChildInformation();
            b = new byte[8];
            System.arraycopy(bx, pos, b, 0, b.length);
            childInformation[i].setDestination(String.valueOf(DataUtil.getLongTo8Byte(b)));
            pos += b.length;
             
            b = new byte[8];
            System.arraycopy(bx, pos, b, 0, b.length);
            childInformation[i].setNextNode(String.valueOf(DataUtil.getLongTo8Byte(b)));
            pos += b.length;
             
            b = new byte[4];
            System.arraycopy(bx, pos, b, 0, b.length);
            childInformation[i].setLifeTime(String.valueOf(DataUtil.getIntTo4Byte(b)));
            pos += b.length;
             
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            childInformation[i].setUpdated(DataUtil.getIntToByte(b[0]));
        }
	}

	// SP-575 add start
	public void decode2(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        childCount = DataUtil.getIntTo2Byte(b);
        pos += b.length;
        log.debug("CHILD_COUNT[" + childCount + "]");
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        childIndex = DataUtil.getIntTo2Byte(b);
        pos += b.length;

        childInformation = new ChildInformation[childCount];
        for(int i=0; i < childInformation.length; i++){
            childInformation[i] = new ChildInformation();
            b = new byte[8];
            System.arraycopy(bx, pos, b, 0, b.length);
            childInformation[i].setDestination(Hex.decode(b));
            pos += b.length;
             
            b = new byte[8];
            System.arraycopy(bx, pos, b, 0, b.length);
            childInformation[i].setNextNode(Hex.decode(b));
            pos += b.length;
             
            b = new byte[4];
            System.arraycopy(bx, pos, b, 0, b.length);
            childInformation[i].setLifeTime(String.valueOf(getIntTo4Byte(b)));
            pos += b.length;
            
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            childInformation[i].setPathSequence(DataUtil.getIntToByte(b[0]));
            pos += b.length;
             
            b = new byte[1];
            System.arraycopy(bx, pos, b, 0, b.length);
            childInformation[i].setUpdated(DataUtil.getIntToByte(b[0]));
            pos += b.length;
        }
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
	
	public class ChildInformation{
		public String destination;
	    public String nextNode;
	    public String lifeTime;
	    public int updated;
	    public int pathSequence;
	    
		public String getDestination() {
			return destination;
		}

		public void setDestination(String destination) {
			this.destination = destination;
		}

		public String getLifeTime() {
			return lifeTime;
		}

		public void setLifeTime(String lifeTime) {
			this.lifeTime = lifeTime;
		}

		public String getNextNode() {
			return nextNode;
		}

		public void setNextNode(String nextNode) {
			this.nextNode = nextNode;
		}

		public int getUpdated() {
			return updated;
		}

		public void setUpdated(int updated) {
			this.updated = updated;
		}

		// SP-575 add start
		public int getPathSequence() {
			return pathSequence;
		}

		public void setPathSequence(int pathSequence) {
			this.pathSequence = pathSequence;
		}
		// SP-575 add end
	}
		
	@Override
	public String toString() {
		StringBuffer rtn= new StringBuffer();
	    for(int i=0; i< childInformation.length-1 ;i++){
	    	rtn.append("[i:"+i+"]");
        	rtn.append("[Destination:"+((ChildInformation)childInformation[i]).getDestination()+"]");
        	rtn.append("[NextNode:"+((ChildInformation)childInformation[i]).getNextNode()+"]");
        	rtn.append("[LifeTime:"+((ChildInformation)childInformation[i]).getLifeTime()+"]");
        	rtn.append("[Updated:"+((ChildInformation)childInformation[i]).getUpdated()+"]");
	    }
	    return "[ChildNodeList]"+
	 		   "[childCount:"+childCount+"]"+
	 		   rtn.toString();	   
	}

	// SP-575 add start
	public String toString2() {
		StringBuffer rtn= new StringBuffer();
		rtn.append("Child Information: \n");
	    for(int i=0; i< childInformation.length ;i++){
        	rtn.append("[Destination: "+((ChildInformation)childInformation[i]).getDestination()+", ");
        	rtn.append("NextNode: "+((ChildInformation)childInformation[i]).getNextNode()+", ");
        	rtn.append("LifeTime: "+((ChildInformation)childInformation[i]).getLifeTime()+", ");
        	rtn.append("PathSequence: "+((ChildInformation)childInformation[i]).getPathSequence()+", ");
        	int updateVal = ((ChildInformation)childInformation[i]).getUpdated();
        	if(updateVal == 0) {
            	rtn.append("Updated: update(0)"+"]");
        	}
        	else if(updateVal == 1) {
            	rtn.append("Updated: no update(1)"+"]");
        	}
        	else {
            	rtn.append("Updated: "+((ChildInformation)childInformation[i]).getUpdated()+"]");
        	}
        	if(i != (childInformation.length-1)){
            	rtn.append(", \n");
        	}
	    }
	    return "Child Count: "+childCount+", \n"+
		       "Child Index: "+childIndex+", \n"+
	 		   rtn.toString();
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
