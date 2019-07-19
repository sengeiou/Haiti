package com.aimir.fep.meter.parser.MX2Table;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.aimir.fep.util.DataUtil;

/**
 * @author kskim
 */
public class TOUFRHoliday implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6530203856394523894L;

	class Data implements java.io.Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2198402846516961465L;
		String MMdd;
		int dayType;

		public String getMMdd() {
			return MMdd;
		}

		public void setMMdd(String mMdd) {
			MMdd = mMdd;
		}

		public int getDayType() {
			return dayType;
		}

		public void setDayType(int dayType) {
			this.dayType = dayType;
		}
	}
	
	List<Data> data = new ArrayList<Data>();

	public List<Data> getData() {
		return data;
	}

	public void addData(int dayType, String MMdd){
		Data data = new Data();
		data.setDayType(dayType);
		data.setMMdd(MMdd);
		this.data.add(data);
	}
	
	public int getSize(){
		return this.data.size();
	}

	public byte[] toByteArray() throws Exception {
//		데이터 길이가 0으로 모두 ff 값이 들어가는 경우 때문에 밑에 if 문 주석처리
//		if(data.size()==0)
//			return null;
			//throw new Exception("Can not found Holiday data");
		final int LEN = 60;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (Data d : data) {
			if(d.getMMdd()==null || d.getDayType() < 1 || d.getDayType() > 3)
				continue;
			byte[] b = new byte[3];
			byte[] bcd = DataUtil.getBCD(d.getMMdd());
			System.arraycopy(bcd, 0, b, 0, 2);
			b[2] = (byte) d.getDayType();
			bos.write(b);
		}
		byte[] ba = bos.toByteArray();
		//빈 공간을 0xff로 채운다.
		byte[] fba = DataUtil.fillCopy(ba, (byte)0xff, LEN);
		bos.close();
		return fba;
	}
}
