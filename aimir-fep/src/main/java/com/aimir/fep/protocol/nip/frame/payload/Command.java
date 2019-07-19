package com.aimir.fep.protocol.nip.frame.payload;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;

public class Command extends PayloadFrame {
    private static Log log = LogFactory.getLog(Command.class);
    
    private byte[] command =new byte[1];
    private byte[] tid=new byte[1];;
    private byte[] data;
    
    @Override
    public byte[] getFrameTid() {
		return tid;
	}
    @Override
	public void setFrameTid(byte[] tid) {
		this.tid = tid;
	}

	private CommandFlow _commandFlow = CommandFlow.Response_Trap;
    private CommandType _commandType;
    private Attribute attribute;
    
    @Override
    public void setCommandFlow(byte code) {
        for (CommandFlow c : CommandFlow.values()) {
            if (c.getCode() == code) {
            	_commandFlow = c;
                break;
            }
        }
    }
    @Override
    public void setCommandType(byte code) {
        for (CommandType c : CommandType.values()) {
            if (c.getCode() == code) {
                _commandType = c;
                break;
            }
        }
    }
    
    public CommandFlow getCommandFlow() {
        return this._commandFlow;
    }
    
    public CommandType getCommandType() {
        return this._commandType;
    }
    
    public void setCommandFlow(CommandFlow flow) {
        this._commandFlow = flow;
    }
    
    public void setCommandType(CommandType type) {
        this._commandType = type;
    }
    
    public void setAttribute(Attribute attr) {
        this.attribute = attr;
    }
    
    public Attribute getAttribute() {
        return this.attribute;
    }
    
    public byte[] getCommand() {
        return this.command;
    }
    
    public void setCommand(byte[] command) {
        this.command = command;
    }
    
    public Attribute newAttribute() {
        return new Attribute();
    }
    
    public void setTid(int tid) {
        this.tid = new byte[]{DataUtil.getByteToInt(tid)};
    }
    
    public void setTid(byte[] tid) {
        this.tid = tid;
    }
    
    @Override
    public void decode(byte[] bx) {
        log.debug("[CommandFRame] - " + Hex.decode(bx));
        
        int pos = 0;
        
        System.arraycopy(bx, pos, command, 0, 1);
        pos++;
        // String commandFrame = DataUtil.getBit(command[0]);
        // setCommandFlow(DataUtil.getByteToInt(DataUtil.getBitToInt(commandFrame.substring(0,2),"%02d")));
        // setCommandType(DataUtil.getByteToInt(DataUtil.getBitToInt(commandFrame.substring(4,8),"%02d")));
        setCommandFlow((byte)(command[0] & (byte)0xC0));
        setCommandType((byte)(command[0] & (byte)0x0F));
        log.debug("CommandFlow[" + _commandFlow + "] CommandType[" + _commandType + "]");
        
        System.arraycopy(bx, pos, tid, 0, 1);
        pos++;
        log.debug("TID[" + DataUtil.getIntToBytes(tid) + "]");
        
        data = new byte[bx.length - pos];
        System.arraycopy(bx, pos, data, 0, data.length);
        
        attribute = new Attribute();
        attribute.decode(data);
    }
    
    @Override
	public String toString() {
	    return "[Command]"+
	    	   "[CommandFlow:"+getCommandFlow().name()+"]"+
	    	   "[CommandType:"+getCommandType().name()+"]";
	}
    
    @Override
    public byte[] encode() throws Exception {
        command[0] = (byte)(_commandFlow.getCode() | _commandType.getCode());
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            out.write(command);
            out.write(getFrameTid()[0]);//TID : 명령 요청에 대한 고유 ID(0~255)
            out.write(attribute.encode());
            byte[] b = out.toByteArray();
            out.close();
            log.debug(Hex.decode(b));
            return b;
        }
        finally {
            if (out != null) out.close();
        }
    }
    
    private void setCommandFlow() {
        for (CommandFlow c : CommandFlow.values()) {
            if ((command[0] & c.getCode()) != 0x00) {
                _commandFlow = c;
                break;
            }
        }
    }
    
    private void setCommandType() {
        for (CommandType c : CommandType.values()) {
            if ((command[0] & c.getCode()) != 0x00) {
                _commandType = c;
                break;
            }
        }
    }
    
    public class Attribute {
        private byte[] count = new byte[1];
        private int dataPos = 0;
        private Data[] data;
        
        public void setData(Data[] data) {
            this.data = data;
        }
        
        public Data[] getData() {
            return this.data;
        }
        
        
        public Data[] newData(int count) {
        	dataPos = 0;
            data = new Data[count];
            for (int i = 0; i < count; i++)
                data[i] = new Data();
            return data;
        }
        
        public void decode(byte[] bx) {
            log.debug("[CommandFrame][Payload] - " + Hex.decode(bx));
            
            int pos = 0;
            
            System.arraycopy(bx, pos, count, 0, 1);
            pos++;
            int cnt = DataUtil.getIntToBytes(count);
            newData(cnt);
            
            byte[] b = new byte[bx.length - 1];
            System.arraycopy(bx, pos, b, 0, b.length);
            
            log.debug("AttributeId_Count[" + data.length + "]");
            
            for (int i = 0; i < cnt; i++) {
                data[i].decode(b, dataPos);
            }
        }
        
        public byte[] encode() throws Exception {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write((byte)data.length);
            for (Data d : data) {
                out.write(d.encode());
            }
            
            byte[] b = out.toByteArray();
            out.close();
            log.debug(Hex.decode(b));
            
            return b;
        }
        public class Data {
            private byte[] id = new byte[2];
            private byte[] len = new byte[2];
            private byte[] value;
            
            public void setId(byte[] id) {
                this.id = id;
            }
            
            public byte[] getId() {
                return this.id;
            }
            
            public void setValue(byte[] value) {
                this.value = value;
            }
            
            public byte[] getValue() {
                return this.value;
            }
            
            public void decode(byte[] bx, int pos) {
                System.arraycopy(bx, pos, id, 0, id.length);
                pos += id.length;
                log.debug("Attribute Id[" + Hex.decode(id) + "]");
                
                System.arraycopy(bx, pos, len, 0, len.length);
                pos += len.length;
                log.debug("Attribute Len[" + DataUtil.getIntTo2Byte(len) + "]");
                
                value = new byte[DataUtil.getIntTo2Byte(len)];
                System.arraycopy(bx, pos, value, 0, value.length);
                log.debug("Attribute Value[" + Hex.decode(value) + "]");
                pos += value.length;
                
                dataPos = pos;
            }
            
            public byte[] encode() throws Exception {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(id);
                if (value == null) value = new byte[0];
                out.write(DataUtil.get2ByteToInt(value.length));
                out.write(value);
                
                byte[] b = out.toByteArray();
                out.close();
                log.debug(Hex.decode(b));
                return b;
            }
        }
    }
}
