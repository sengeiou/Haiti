package com.aimir.fep.meter.parser.MX2Table;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.aimir.fep.util.DataUtil;

/**
 * @author kskim
 */
public class TOUDayPattern implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5787775977329373758L;

	class Data implements java.io.Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1903821541086206850L;
		String HHmm;
		int rate;

		public String getHHmm() {
			return HHmm;
		}

		public void setHHmm(String hHmm) {
			HHmm = hHmm;
		}

		public int getRate() {
			return rate;
		}

		public void setRate(int rate) {
			this.rate = rate;
		}
	}

	List<Data> dayType1 = new ArrayList<Data>();
	List<Data> dayType2 = new ArrayList<Data>();
	List<Data> dayType3 = new ArrayList<Data>();

	public List<Data> getDayType(int dayType) {
		switch (dayType) {
		case 1:
			return this.dayType1;
		case 2:
			return this.dayType2;
		case 3:
			return this.dayType3;
		default:
			break;
		}
		return null;
	}

	public void addDayType(String hhmm, int rate, int dayType) {
		
		Data d = new Data();
		
		d.setHHmm(hhmm);
		d.setRate(rate);
		
		switch (dayType) {
		case 1:
			this.dayType1.add(d);
			break;
		case 2:
			this.dayType2.add(d);
			break;
		case 3:
			this.dayType3.add(d);
			break;
		default:
			break;
		}
	}

	public byte[] toByteArray() throws Exception {
		final int LEN = 30;
		
		Object[] list = new Object[]{dayType1,dayType2,dayType3};
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (Object object : list) {
			ByteArrayOutputStream bbos = new ByteArrayOutputStream();
			List<Data> data = (List<Data>)object;
			for (Data d : data) {

				if (d.getHHmm() == null || d.getHHmm().length() != 4
						|| d.getRate() < 0 || d.getRate() > 8)
					continue;

				byte[] b = new byte[3];

				byte[] bcd = DataUtil.getBCD(d.getHHmm());

				System.arraycopy(bcd, 0, b, 0, 2);

				b[2] = (byte) d.getRate();
				
				bbos.write(b);
			}
			bos.write(DataUtil.fillCopy(bbos.toByteArray(), (byte)0xff, LEN));
			bbos.close();
		}
		
		byte[] result = bos.toByteArray();
		bos.close();
		return result;
	}

}
