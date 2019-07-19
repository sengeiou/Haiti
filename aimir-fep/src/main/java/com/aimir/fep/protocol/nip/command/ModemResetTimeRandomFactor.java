package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class ModemResetTimeRandomFactor extends AbstractCommand{
    private int randomFactor;
    
    public ModemResetTimeRandomFactor() {
        super(new byte[] {(byte)0x20, (byte)0x17});
    }

    public int getRandomFactor() {
        return randomFactor;
    }

    public void setRandomFactor(int randomFactor) {
    	this.randomFactor = randomFactor;
    }

    @Override
    public void decode(byte[] bx) {

        byte[] b = new byte[2];
        System.arraycopy(bx, 0, b, 0, b.length);
        this.randomFactor = DataUtil.getIntTo2Byte(b);

    }

    @Override
    public Command set(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        
        this.randomFactor = (int)info.get("randomFactor"); 
        ByteArrayOutputStream out = null;
        
        try {
            out = new ByteArrayOutputStream();
            
            //randomFactor
            out.write(DataUtil.get2ByteToInt(randomFactor));

            
            datas[0].setValue(out.toByteArray());
            attr.setData(datas);
            command.setAttribute(attr);
            return command;
        }
        finally {
            if (out != null) out.close();
        }
    }
	
    @Override
    public String toString() {
        return "[randomFactor]"+
                "[val:"+this.randomFactor+"]";
    }
	
	@Override
	public Command get() throws Exception{return null;}
	@Override
	public Command get(HashMap p) throws Exception{return null;}
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
