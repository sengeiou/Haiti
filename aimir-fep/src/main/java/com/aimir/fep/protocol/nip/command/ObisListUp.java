package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class ObisListUp extends AbstractCommand{
    private int obisCnt;
    private ObisData[] obisData;
    
    public ObisListUp() {
        super(new byte[] {(byte)0x40, (byte)0x01});
    }
    
    public int getObisCnt() {
        return obisCnt;
    }

    public void setObisCnt(int obisCnt) {
        this.obisCnt = obisCnt;
    }

    public ObisData[] getObisData() {
        return obisData;
    }

    public void setObisData(ObisData[] obisData) {
        this.obisData = obisData;
    }

    @Override
    public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        obisCnt = DataUtil.getIntToByte(b[0]);
        pos += b.length;

        obisData = new ObisData[obisCnt];
        for(int i=0; i < obisData.length-1 ;i++){
        	obisData[i] = new ObisData();
        	b = new byte[14];
        	System.arraycopy(bx, pos, b, 0, b.length);
        	obisData[i].obisIndex =  DataUtil.getIntToByte(b[0]);
        	obisData[i].obisCodes.serviceTypes = String.valueOf(DataUtil.getIntTo3Byte((new byte[]{b[3],b[2],b[1]}))); 
        	obisData[i].obisCodes.classId = String.valueOf(DataUtil.getIntTo2Byte((new byte[]{b[5],b[4]})));
        	obisData[i].obisCodes.obis = String.valueOf(DataUtil.getLongTo6Byte((new byte[]{b[11],b[10],b[9],b[8],b[7],b[6]})));
        	obisData[i].obisCodes.attribute = DataUtil.getIntToByte(b[12]);
        	obisData[i].selectiveAccessLength =  DataUtil.getIntToByte(b[13]);
        	// SP-575 add start
        	b = new byte[obisData[i].selectiveAccessLength];
            obisData[i].selectiveAccessData = Hex.decode(b);
        	// SP-575 add end
        	pos += b.length;
        }
    }
    
    // SP-575 add start
    public void decode2(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        byte[] bTmp = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        obisCnt = DataUtil.getIntToByte(b[0]);
        pos += b.length;

        obisData = new ObisData[obisCnt];
        for(int i=0; i < obisData.length ;i++){
        	obisData[i] = new ObisData();
        	b = new byte[14];
        	System.arraycopy(bx, pos, b, 0, b.length);
        	bTmp = new byte[1];
            System.arraycopy(b, 0, bTmp, 0, bTmp.length);
        	obisData[i].obisIndex =  DataUtil.getIntToByte(b[0]);
        	bTmp = new byte[3];
            System.arraycopy(b, 1, bTmp, 0, bTmp.length);
        	obisData[i].obisCodes = obisData[i].new ObisCodes();
        	obisData[i].obisCodes.serviceTypes = String.valueOf(DataUtil.getIntTo3Byte(bTmp));
        	bTmp = new byte[2];
            System.arraycopy(b, 4, bTmp, 0, bTmp.length);
        	obisData[i].obisCodes.classId = String.valueOf(DataUtil.getIntTo2Byte(bTmp));
        	bTmp = new byte[6];
            System.arraycopy(b, 6, bTmp, 0, bTmp.length);
            StringBuffer obisStr = new StringBuffer();
            for(int j = 0 ; j< bTmp.length;j++){
            	obisStr.append(String.format("%d", bTmp[j]));
            	if(j != (bTmp.length-1)){
            	obisStr.append(".");
            	}
            }
        	obisData[i].obisCodes.obis = obisStr.toString();
        	bTmp = new byte[1];
            System.arraycopy(b, 12, bTmp, 0, bTmp.length);
        	obisData[i].obisCodes.attribute = DataUtil.getIntToByte(bTmp[0]);        	
        	bTmp = new byte[1];
            System.arraycopy(b, 13, bTmp, 0, bTmp.length);
        	pos += b.length;
        	obisData[i].selectiveAccessLength =  DataUtil.getIntToByte(bTmp[0]);
        	bTmp = new byte[obisData[i].selectiveAccessLength];
        	System.arraycopy(bx, pos, bTmp, 0, bTmp.length);
            obisData[i].selectiveAccessData = Hex.decode(bTmp);
        	pos += bTmp.length;
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
	
    @Override
    public String toString() {
        StringBuffer rtn= new StringBuffer();
        for(int i=0; i< obisData.length-1 ;i++){
            rtn.append("[i:"+i+"]");
            rtn.append("[ObisIndex:"+((ObisData)obisData[i]).getObisIndex()+"]");
            rtn.append("[ServiceTypes:"+((ObisData)obisData[i]).obisCodes.getServiceTypes()+"]");
            rtn.append("[ClassId:"+((ObisData)obisData[i]).obisCodes.getClassId()+"]");
            rtn.append("[Obis:"+((ObisData)obisData[i]).obisCodes.getObis()+"]");
            rtn.append("[Attribute:"+((ObisData)obisData[i]).obisCodes.getAttribute()+"]");
            rtn.append("[SelectiveAccessLength:"+((ObisData)obisData[i]).getSelectiveAccessLength()+"]");
        	
        }
        return "[ObisListUp]"+
        "[obisCnt:"+obisCnt+"]"+
        rtn.toString();	   
    }

    // SP-575 add start
    public String toString2() {
        StringBuffer rtn= new StringBuffer();
        rtn.append("OBIS Data: \n");
        for(int i = 0; i< obisData.length;i++){
            rtn.append("[");
            rtn.append("OBIS index: "+((ObisData)obisData[i]).getObisIndex()+", ");
            rtn.append("Service types: "+((ObisData)obisData[i]).obisCodes.getServiceTypes()+", ");
            rtn.append("Class ID: "+((ObisData)obisData[i]).obisCodes.getClassId()+", ");
            rtn.append("OBIS: "+((ObisData)obisData[i]).obisCodes.getObis()+", ");
            rtn.append("Attribute: "+((ObisData)obisData[i]).obisCodes.getAttribute()+", ");
            rtn.append("Selective Access Length: "+((ObisData)obisData[i]).getSelectiveAccessLength()+", ");
            rtn.append("Selective Access Data: "+((ObisData)obisData[i]).getSelectiveAccessData()+"]");
            if(i != (obisData.length-1)) {
                rtn.append(", \n");
            }
        }
        return "OBIS Cnt: "+obisCnt+", \n"+
        rtn.toString();	   
    }
    // SP-575 add end

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
