package com.aimir.fep.protocol.nip.frame.payload;

import java.io.ByteArrayOutputStream;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class Bypass extends PayloadFrame {
    
    public enum TID_Type {
        Disable ((byte)0x00),
        Enable ((byte)0x80);
        
        private byte code;
        
        TID_Type(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
    
    private byte tid;
    private byte[] data;
    
    private TID_Type _tidType = TID_Type.Disable; // UPDATE SP-722
    private int tidLocation = 0;	// UPDATE SP-722
    private int _tid = 0;			// UPDATE SP-722
    
    public int get_tid() {
		return _tid;
	}

	public void set_tid(int _tid) {
		this._tid = _tid;
	}

	public void setTidLocation(int tidLocation) {
		this.tidLocation = tidLocation;
	}
	private byte[] payload;
    
    public void setTID_Type(byte code) {
    	for (TID_Type f : TID_Type.values()) {
            if (f.getCode() == code) {
            	_tidType = f;
                break;
            }
        }
    }
    
    public TID_Type getTidType() {
        return this._tidType;
    }
    
    public void newPayload(int cnt) {
		this.payload = new byte[cnt];
	}
    
    public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public int getTidLocation() {
//        if ((tid & 0x40) != (byte)0x00) {
//            tidLocation = 1;
//        }
//        else {
//            tidLocation = 0;
//        }
        return this.tidLocation;
    }
    
    public int getTid() {
        this._tid = DataUtil.getIntToByte((byte)(tid & (byte)0x3F));
        return _tid;
    }
    
    public void setTidType(TID_Type _tidType) {
        this._tidType = _tidType;
    }
    
    // UPDATE SP-722
    public void setTid(byte tid) {
        this.tid = (byte)tid;     	
    }
    
    public void setData(byte[] data) {
        this.data = data;
    }
    
    @Override
    public void decode(byte[] bx) {
        log.debug(Hex.decode(bx));
        byte[] b = new byte[1];
        int pos = 0;
        System.arraycopy(bx, pos, b, 0, 1);
        pos++;
        String tidTypeBit = DataUtil.getBit(b[0]);
        
       
        String tidType = DataUtil.getBitToInt(tidTypeBit.substring(0, 1), "%d0000000");
        setTID_Type(DataUtil.getByteToInt(tidType));
        String tidLocation = DataUtil.getBitToInt(tidTypeBit.substring(1, 2), "%d");
        setTidLocation(Integer.parseInt(tidLocation));
        String tid = DataUtil.getBitToInt(tidTypeBit.substring(2, 8), "%d");
        set_tid(Integer.parseInt(tid));
        setTid(b[0]); // INSERT SP-722
        
        b = new byte[bx.length-1];
        System.arraycopy(bx, pos, b, 0, bx.length-1);
        setPayload(b);
        log.debug("###########[decode]###############");
        log.debug("TID Field Type = " + getTidType().name());
        log.debug("TID Location = " + getTidLocation());
        log.debug("TID = " + tid);
        log.debug(Hex.decode(b));
        log.debug("###########[decode]###############");
        data = new byte[bx.length - 1];
        System.arraycopy(bx, pos, data, 0, data.length);
    }
    
    public byte[] encode() throws Exception {
        if (_tid > 0x3F) {
            _tid = 0x00;
            tidLocation = 0x40;
        }
     // UPDATE START SP-722
//        tid = (byte)(_tidType.getCode() | DataUtil.getByteToInt(tidLocation) | DataUtil.getByteToInt(tid));
        tid = (byte)(_tidType.getCode() | DataUtil.getByteToInt(tidLocation) | DataUtil.getByteToInt(_tid));
     // UPDATE END   SP-722
        
        log.debug("###########[encode]###############");
        log.debug("TID Field Type = " + _tidType.name());
        log.debug("TID Location = " + tidLocation);
        log.debug("TID Trans ID = " + _tid + ", Hex = " + Hex.decode(new byte[] {(byte)(_tid&0x3F)}));
        log.debug("Payload = " + Hex.decode(getPayload()));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(new byte[]{tid});
        //out.write(data);
        out.write(getPayload());
        byte[] bx = out.toByteArray();
        log.debug("TID + Payload = " + Hex.decode(bx));
        log.debug("###########[encode]###############");
        out.close();
        return bx;
    }
    
	@Override
	public String toString() {
	    return "[Bypass]"+
	    	   "[tidType:"+getTidType().name()+"]"+
	    	   "[tidLocation:"+getTidLocation()+"]"+
	    	   "[tid:"+get_tid()+"]"+
	    	   "[payload:"+Hex.decode(getPayload())+"]";
	}
    
    @Override
    public void setCommandFlow(byte code){ }
  
    @Override
    public void setCommandType(byte code){ }
  
    @Override
    public byte[] getFrameTid(){
    	// INSERT SP-722
    	byte[] ret = new byte[1];
    	ret[0] = tid;
    	return ret;
    }
    
    @Override
    public void setFrameTid(byte[] code){
    	// INSERT SP-722
    	tid = code[0];
    }
    
    
    /**
     * SP-722
     * @param tid
     * @return
     */
    public static TID_Type getTidType(byte tid[])
    {
    	int n =  (tid[0] & 0xFF);
    
    	if ( (n & 0x80) == 0x80 ){
    		return TID_Type.Enable;
    	}
    	else {
    		return TID_Type.Disable;
    	}
    }
    
    /**
     * SP-722
     * @param tid
     * @return
     */
    public static int  getTidLocation(byte tid[]){
    	int n = tid[0] & 0xFF;
        
    	if ( (n & 0x40) == 0x40 ){
    		return 1;
    	}
    	else {
    		return 0;
    	}
    }
    
    /**
     * SP-722
     * @param tid
     * @return
     */
    public static int  getTid(byte tid[])
    {
    	int n =  (tid[0] & 0x3F);
    	return n;
    }
    
    /**
     * SP-722
     * @param tid
     * @return
     */
    public static int getNextTid(int tid){
    	if (tid >= 0x3F )
    		return 0;
    	else  
    		return  (tid + 1);
    }
    
    /**
     * SP-722
     * @param type
     * @param location
     * @param tid
     * @return
     */
    public static byte[] makeTidByte(TID_Type type, int location, int tid)
    {
    	byte[] ret = new byte[1];
    	ret[0] = 0;
    	
    	ret[0] = (byte) ((byte)type.getCode() & (byte)location & (byte)tid);
    	return ret;
    }
    
    /**
     * SP-722
     * @param tid
     * @return
     */
    public static int getPrevTid(int tid){
    	if ( tid == 0 ){
    		return 0x3F;
    	}
    	else {
    		return (tid - 1);
    	}
    }
}
