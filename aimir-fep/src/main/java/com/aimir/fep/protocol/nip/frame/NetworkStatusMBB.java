/**
 * 
 */
package com.aimir.fep.protocol.nip.frame;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.ByteArrayOutputStream;

public class NetworkStatusMBB extends NetworkStatus {
	private byte cpu;
	private byte memory;
	private byte[] totalTx = new byte[4];
	private byte network;

	public byte getCpu() {
		return cpu;
	}

	public void setCpu(byte cpu) {
		this.cpu = cpu;
	}

	public byte getMemory() {
		return memory;
	}

	public void setMemory(byte memory) {
		this.memory = memory;
	}

	public byte[] getTotalTx() {
		return totalTx;
	}

	public void setTotalTx(byte[] totalTx) {
		this.totalTx = totalTx;
	}

	public byte getNetwork() {
		return network;
	}

	public void setNetwork(byte network) {
		this.network = network;
	}

	public void decode(byte[] bx) {
		int pos = 0;

		byte[] data = new byte[1];
		System.arraycopy(bx, pos, data, 0, data.length);
		cpu = data[0];
		pos += data.length;

		data = new byte[1];
		System.arraycopy(bx, pos, data, 0, data.length);
		memory = data[0];
		pos += data.length;

		System.arraycopy(bx, pos, totalTx, 0, totalTx.length);
        pos += totalTx.length;

        data = new byte[1];
        System.arraycopy(bx, pos, data, 0, data.length);
        network = data[0];


	}

	public byte[] encode() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(cpu);
		out.write(memory);
		out.write(totalTx);
        out.write(network);

		byte[] b = out.toByteArray();
		out.close();

		return b;
	}

	@Override
	public void setRssi(byte rssi) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParentNode(String parentNode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEtx(byte[] etx) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
