package com.aimir.fep.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * java UDPEchoClient fd00:2:1:0:210:ff:fe00:5d 9091 7967
 * 7EA020030393FEC9818014050201000602010007040000000108040000000
 * 
 * @author simhanger
 *
 */
public class UDPEchoClient {
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

	public static byte[] readByteString(String byteString) {
		byte[] result = new byte[byteString.length() / 2];
		int index = 0;

		while (index < result.length) {
			result[index] = (byte) Short.parseShort(byteString.substring(index * 2, index * 2 + 2), 16);
			index++;
		}

		return result;
	}

	public static void main(String[] args) {
		try {
			if (args == null || args.length < 4) {
				System.out.println("[ERROR] main parameter is null.!!");
			} else {
				String serverIp = String.valueOf(args[0]);
				int serverPort = Integer.parseInt(String.valueOf(args[1]));

				int clinetPort = Integer.parseInt(String.valueOf(args[2]));
				String sendData = String.valueOf(args[3]);

				byte buffer[] = readByteString(sendData);
				DatagramPacket dp = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(serverIp), serverPort);

				DatagramSocket ds = null;
				
				if (args.length == 4
						|| args[4] == null 
						|| args[4].equals("")) {
					ds = new DatagramSocket(clinetPort);
				} else {
					String clientIp = String.valueOf(args[4]);
					ds = new DatagramSocket(clinetPort, InetAddress.getByName(clientIp));
					//ds.setSoTimeout(100);
					System.out.println("Set client ip = " + clientIp);
				}

				ds.send(dp);
				System.out.println("[SEND    ] " + ds.getLocalSocketAddress() + " ==> " + dp.getAddress() + ":" + dp.getPort() + ", Hex=" + decode(dp.getData()));

				buffer = new byte[1024];
				dp = new DatagramPacket(buffer, buffer.length);
				ds.receive(dp);
				System.out.println("[RECEIVED] " + ds.getLocalSocketAddress() + " <== " + dp.getAddress() + ":" + dp.getPort() + ", Hex=" + decode(dp.getData()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("UDP Client End");
		}

	}

}
