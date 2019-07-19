package com.aimir.fep;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * @author guyrune
 * 
 * jira - OPF-680
 *  - 기존 6byte, 8byte의 처리를 int로 처리하여 최대 4byte 표현 범위가 넘어감으로 long으로 변경 한 내용의 테스트
 */
public class DataUtilTest {
	
	protected final Log log = LogFactory.getLog(DataUtilTest.class);
	
	@Test
	public void getLongToByte6Test() {
		int size = 6;
		
		byte[] val = new byte[] { (byte)0xff, (byte)0xC1, (byte)0x12, (byte)0x79, (byte)0x0, (byte)0xFF };
		System.out.println("--> 6byte");
		System.out.println("6byte Random value: " + Hex.decode(val));
		System.out.println("int type: " + Hex.decode(DataUtil.get6ByteToInt(getLegacyIntToByte(size, val))));
		System.out.println("long type: " + Hex.decode(DataUtil.get6ByteToInt(DataUtil.getLongTo6Byte(val))));
		System.out.println();

		val = getMaxByte(size);
		System.out.println("6byte Max value: " + Hex.decode(val));
		System.out.println("int type: " + Hex.decode(DataUtil.get6ByteToInt(getLegacyIntToByte(size, val))));
		System.out.println("long type: " + Hex.decode(DataUtil.get6ByteToInt(DataUtil.getLongTo6Byte(val))));
		System.out.println();
	}
	
	@Test
	public void getLongToByte8Test() {
		int size = 8;
		
		byte[] val = new byte[] { (byte)0xFF, (byte)0x21, (byte)0xff, (byte)0xC1, (byte)0x12, (byte)0x79, (byte)0x0, (byte)0xFF };
		System.out.println("--> 8byte");
		System.out.println("8byte Random value: " + Hex.decode(val));
		System.out.println("int type: " + Hex.decode(DataUtil.get8ByteToInt(getLegacyIntToByte(size, val))));
		System.out.println("long type: " + Hex.decode(DataUtil.get8ByteToInt(DataUtil.getLongTo8Byte(val))));
		System.out.println();

		val = getMaxByte(size);
		System.out.println("8byte Max value: " + Hex.decode(val));
		System.out.println("int type: " + Hex.decode(DataUtil.get8ByteToInt(getLegacyIntToByte(size, val))));
		System.out.println("long type: " + Hex.decode(DataUtil.get8ByteToInt(DataUtil.getLongTo8Byte(val))));
		System.out.println();
	}
	
	public int getLegacyIntToByte(int size, byte[] val) {
		int res = 0;
		int shift = 8 * (size - 1);
		
		for(int i = 0 ; i < size ; i++) {
			res += ((val[i] & 0xFF) << shift);
			shift -= 8;
		}
		return res;
	}
	
	public byte[] getMaxByte(int size) {
		byte[] arr = new byte[size];
		
		for(int i = 0 ; i < size ; i++) {
			arr[i] = (byte) 0xFF;
		}
		return arr;
	}
	
	public long getMaxHex(int size) {
		long res = 0;
		for(int i = 0 ; i < (size * 2); i++) {
			res += 15 * Math.pow(16, i);
		}
		return res;
	}
}
