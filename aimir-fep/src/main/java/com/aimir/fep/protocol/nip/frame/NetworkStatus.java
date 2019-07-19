package com.aimir.fep.protocol.nip.frame;

public abstract class NetworkStatus {
	public abstract void setRssi(byte rssi);

	public abstract void setParentNode(String parentNode);

	public abstract void setEtx(byte[] etx);

	public abstract void setCpu(byte cpu);

	public abstract void setMemory(byte memory);

	public abstract void setTotalTx(byte[] totalTx);

	public abstract void decode(byte[] data);

	public abstract byte[] encode() throws Exception;

}
