package com.aimir.fep.protocol.nip.frame;

import java.io.ByteArrayOutputStream;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.Hex;

public class NetworkStatusSub1Ghz extends NetworkStatus {
    private static Log log = LogFactory.getLog(NetworkStatusSub1Ghz.class);
    
	private byte[] parentNode = new byte[8];
	private byte rssi;

	public String getParentNode() {
		return Hex.decode(parentNode);
	}

	@Override
	public void setParentNode(String parentNode) {
		this.parentNode = Hex.encode(parentNode);
	}

	public byte getRssi() {
		return rssi;
	}

	@Override
	public void setRssi(byte rssi) {
		this.rssi = rssi;
	}

	@Override
	public void decode(byte[] bx) {
	    log.debug(Hex.decode(bx));
	    
		int pos = 0;
		byte[] b = new byte[8];
		
		System.arraycopy(bx, pos, b, 0, b.length);
		pos += b.length;
		parentNode = b;
		
		b = new byte[1];
		System.arraycopy(bx, pos, b, 0, b.length);
		pos += b.length;
		
		rssi = b[0];
	}

	@Override
	public byte[] encode() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(parentNode);
		out.write(rssi);

		byte[] b = out.toByteArray();
		out.close();

		return b;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public void setEtx(byte[] etx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCpu(byte cpu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMemory(byte memory) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTotalTx(byte[] totalTx) {
		// TODO Auto-generated method stub
		
	}
}
