package com.aimir.fep.protocol.nip.frame.payload;

public class Ack extends PayloadFrame {
    private byte[] empty = new byte[0]; 
    
    @Override
    public void decode(byte[] bx) {
    }
    
    @Override
    public byte[] encode() throws Exception {
        return empty;
    }
    @Override
    public void setCommandFlow(byte code){ }
    @Override
    public void setCommandType(byte code){ }
    @Override
    public byte[] getFrameTid(){ return null;}
    @Override
    public void setFrameTid(byte[] code){}
}