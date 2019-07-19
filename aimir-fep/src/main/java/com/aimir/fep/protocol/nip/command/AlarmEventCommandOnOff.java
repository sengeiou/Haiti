package com.aimir.fep.protocol.nip.command;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.google.gson.Gson;
import com.aimir.fep.protocol.nip.command.ResponseResult.Status;

public class AlarmEventCommandOnOff extends AbstractCommand{
    private int count=0;
    private AlarmEventCmd[] cmds; 
    private Status status;
    private byte[] statusCode;
    
    /**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	public AlarmEventCommandOnOff() {
        super(new byte[] {(byte)0x20, (byte)0x11});
    }
    
	public AlarmEventCmd[] getCmds() {
		return cmds;
	}

	public void setCmds(AlarmEventCmd[] cmds) {
		this.cmds = cmds;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public void decode(byte[] bx) {
         int pos = 0;
		 byte[] b = new byte[1];
		 System.arraycopy(bx, pos, b, 0, b.length);
		 count = DataUtil.getIntToByte(b[0]);
		 pos += b.length;
		 
		 cmds = new AlarmEventCmd[count];
		 // SP-575 add start
		 statusCode = new byte[count];
		 // SP-575 add end
		 for(int i=0 ; i<cmds.length;i++){
			cmds[i] = new AlarmEventCmd();
			b = new byte[3];
		    System.arraycopy(bx, pos, b, 0, b.length);
		    cmds[i].setAlarmEventTypeId(DataUtil.getIntTo2Byte(new byte[]{b[0],b[1]}));
		    cmds[i].setStatue(b[2]);
		    // SP-575 add start
		    statusCode[i] = b[2];
		    // SP-575 add end
		  	pos += b.length;
		 }
		 status = Status.Success;
    }

	@Override
	 public Command get(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Get);
        datas[0].setId(getAttributeID());
        // UPDATE START SP-701
        int cnt;
        if ( info.get("count") instanceof String ){
        	cnt = Integer.parseInt((String)info.get("count"));
        }
        else {
            cnt = (int)info.get("count");
        }
        // UPDATE END SP-701
        
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            out.write(new byte[]{DataUtil.getByteToInt(cnt)});
            for(int i=0; i<cnt ; i++){
            	// UPDATE START SP-701
            	if ( info.get("cmds") instanceof String){
                	Gson gson = new Gson();
                	out.write(DataUtil.get2ByteToInt(((AlarmEventCmd[])gson.fromJson((String)info.get("cmds"), 
                			AlarmEventCmd[].class))[i].alarmEventTypeId));
                }
                else {
                	out.write(DataUtil.get2ByteToInt(((AlarmEventCmd[])info.get("cmds"))[i].alarmEventTypeId));
                }
            	// UPDATE END SP-701
            }
            datas[0].setValue(out.toByteArray());
        }
        catch (Exception e){
        	log.debug(e,e);
        }
        finally{
        	if ( out != null )
        		 out.close();
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
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
           
        int cnt;
        if ( info.get("count") instanceof String ){
        	cnt = Integer.parseInt((String)info.get("count"));
        }
        else {
            cnt = (int)info.get("count");
        }
        AlarmEventCmd[] cmdarray;
        if ( info.get("cmds") instanceof String){
        	Gson gson = new Gson();
        	cmdarray = (AlarmEventCmd[]) gson.fromJson((String)info.get("cmds"), AlarmEventCmd[].class);
        }
        else {
        	cmdarray = (AlarmEventCmd[])info.get("cmds");
        }
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            out.write(new byte[]{DataUtil.getByteToInt(cnt)});
            for(int i=0; i<cnt ; i++){
            	out.write(DataUtil.get2ByteToInt(cmdarray[i].alarmEventTypeId));
            	out.write(cmdarray[i]._statue.getCode());
            }
            datas[0].setValue(out.toByteArray());
        }
        catch (Exception e){
        	log.debug(e,e);
        }
        finally{
        	if ( out != null )
        		 out.close();
        }

        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }

	@Override
    public String toString() {
		StringBuffer rtn= new StringBuffer();
        for(int i=0; i< cmds.length;i++){
        	rtn.append("\n["+i+":" + cmds[i].toString() + "]"); //UPDATE SP-701
        }
        return "[AlarmEventCommandOnOff]"+
 	   		   "[count:"+count+"]"+
 	   		   rtn.toString();	   
    }
	
	// SP-575 add start
    public String toString2() {
		StringBuffer rtn = new StringBuffer();
		String typeIdStr = "";
		String statusStr = "";
		String typeIdHexStr = "";
		
        for(int i=0; i< cmds.length;i++){
		    typeIdHexStr = String.format("%04x", cmds[i].alarmEventTypeId);
		    typeIdStr = cmds[i].getAlarmEventType()+ "(0x"+ typeIdHexStr +")";
			if(cmds[i]._statue == null){
				statusStr = "(0x"+ String.format("%02x", statusCode[i]) +")";
			}
			else{
				statusStr = cmds[i]._statue.name() + "(0x"+ String.format("%02x", cmds[i]._statue.getCode()) +")";
			}
        	rtn.append("\n[Alarm/Event Type ID: "+ typeIdStr +", ");
        	rtn.append("Status: "+ statusStr +"]");
        	if( i != (cmds.length-1)){
            	rtn.append(", ");
        	}
        }
        return "Count: "+count+", \n"+
               "Command: "+rtn.toString();	   
    }
    // SP-575 add end
    
	@Override
	public Command get() throws Exception{return null;}

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
