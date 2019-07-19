package com.aimir.fep.meter.parser.MX2Table;

public class NegotiateTable {
	private byte status;
	private byte[] packet_size;
	private byte nbr_packets;
	private byte baud_rate;
	
	public byte getStatus() {
		return status;
	}
	
	public static int length(){
		return 13;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public byte[] getPacket_size() {
		return packet_size;
	}

	public void setPacket_size(byte[] packetSize) {
		packet_size = packetSize;
	}

	public byte getNbr_packets() {
		return nbr_packets;
	}

	public void setNbr_packets(byte nbrPackets) {
		nbr_packets = nbrPackets;
	}

	public byte getBaud_rate() {
		return baud_rate;
	}

	public void setBaud_rate(byte baudRate) {
		baud_rate = baudRate;
	}

	public NegotiateTable(){
	}
}
