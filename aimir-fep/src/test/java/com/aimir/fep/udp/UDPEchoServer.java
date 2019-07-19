package com.aimir.fep.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;

/**
 * java UDPEchoServer 9091
 * 
 * @author simhanger
 *
 */
public class UDPEchoServer {
	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String decode(byte[] data) {
		if (data == null)
			return "";

		int l = data.length;
		char[] out = new char[l << 1];
		// two characters form the hex value.  
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}
		return new String(out);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if (args == null) {
				System.out.println("[ERROR] main parameter is null.!!");
			} else {
				String serverIp = String.valueOf(args[0]);
				int serverPort = Integer.parseInt(String.valueOf(args[1]));

				System.out.println("[" + serverIp + ":" + serverPort + "] Server ready.");

				//Inet6Address serverIp6 = (Inet6Address) Inet6Address.getByName(serverIp);
				//DatagramSocket ds = new DatagramSocket(serverPort, serverIp6);
				
				InetAddress serverIp4 = InetAddress.getByName(serverIp);
				//DatagramSocket ds = new DatagramSocket(serverPort, InetAddress.getLocalHost());
				DatagramSocket ds = new DatagramSocket(serverPort, serverIp4);
				while (true) {
					System.out.println("Listening ... : " + ds.getInetAddress());

					byte[] buffer = new byte[1024];
					DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
					ds.receive(dp);
					System.out.println("[RECEIVED] " + ds.getLocalSocketAddress() + " <== " + dp.getAddress() + ":" + dp.getPort() + ", Hex=" + decode(dp.getData()));

					dp = new DatagramPacket(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 }, 5, dp.getAddress(), dp.getPort());
					ds.send(dp);
					System.out.println("[SEND    ] " + ds.getLocalSocketAddress() + " ==> " + dp.getAddress() + ":" + dp.getPort() + ", Hex=" + decode(dp.getData()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("UDP Server End");
		}
	}
}
