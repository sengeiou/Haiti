/*
 * Created on 2004. 12. 27.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.aimir.fep.meter.parser.zmdTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * LandisGyr+ ZMD4 meter Class. <p> 
 * Include LP Data . <p>
 */
public class Class12 {
	
	//public final static int OFS_LPCOUNT = 15;	// offset lp count.
	//public final static int LEN_LPCOUNT = 1;	// length lp count.

	private byte[] starttime;
	private byte[] endtime;
	private byte[] data;
	private final int LP_LEN_DATA = 31;
	
	private final int LP_LEN_TIME  = 6;
	private final int LP_LEN_CHAN  = 4;
	private final int LP_LEN_PFAIL = 1;
	private final int LP_LEN_EVENT = 1;
	
	private final int LP_CHAN_SIZE  = 4;
	private final int LP_CHAN_COUNT = 2;
    private static Log logger = LogFactory.getLog(Class12.class);


	/**
	 * Constructor 
	 * 
	 * @param data - read data
	 */
	public Class12(byte[] data) {
		this.data = data;
	}

	
	public int parseTotalSize(){
		int pcount = 0;
		int tot = 0;
		
		try{
			pcount = data.length/LP_LEN_DATA;
			tot = pcount*(LP_LEN_TIME+LP_CHAN_SIZE*LP_CHAN_COUNT+LP_LEN_PFAIL+LP_LEN_EVENT);
		}catch(Exception e){
			logger.debug(e);
		}
		return tot;
	}
	
	/**
	 * Get LP Count. <p>
	 * @return
	 */
	public byte[] parseLPCount(){
		
		byte[] pcount = new byte[2];
		int c = 0;
		
		try{
			c = data.length/LP_LEN_DATA;
			pcount[0] = (byte)((c >> 8) & 0xff);
			pcount[1] = (byte)(c & 0xff);

		}catch(Exception e){
			logger.debug(e);
		}

		return pcount;
	}

	public int getLPCount(){
		return data.length/LP_LEN_DATA;
	}
	
	public byte[] getStartTime(){
		return this.starttime;
	}
	
	public byte[] getEndTime(){
		return this.endtime;
	}

	public byte[] parseLPData(){

		int pcount = 0;
		pcount = data.length/LP_LEN_DATA;

		byte[] lpdata 
			= new byte[pcount*(LP_LEN_TIME+LP_CHAN_SIZE*LP_CHAN_COUNT+LP_LEN_PFAIL+LP_LEN_EVENT)];
		int idx = LP_LEN_DATA;
		
		int ofs = 0;
		for(int i = 0; i < pcount; i++){
			byte[] time  = getLPTime(idx*i+4,8);
			byte[] chan  = getChannelData(idx*i+22,LP_CHAN_SIZE*LP_CHAN_COUNT+1);
			//byte   pfail = getPfailFlag(idx*i+14);
			//byte   event = getEventFlag(idx*i+14);
			byte[] event = getEventFlag(idx*i+14);

			System.arraycopy(time,0,lpdata,ofs,time.length);
			ofs += time.length;
			System.arraycopy(chan,0,lpdata,ofs,chan.length);
			ofs += chan.length;
			lpdata[ofs++] = event[0];
			lpdata[ofs++] = event[1];
			
			if(i == 0){
				starttime = time;
			}else if(i == pcount-1){
				endtime = time;
			}
		}
	
		return lpdata;
	}

	
	private byte[] getLPTime(int start, int len){
		
		byte[] lptime = new byte[6];
		
		try {
			byte[] lp_time = DataFormat.select(data,start,len);
			
			lptime[0] = lp_time[0];
			lptime[1] = lp_time[1];
			lptime[2] = lp_time[2];
			lptime[3] = lp_time[3];
			
			if(lp_time[5] == 0 && lp_time[6] == 0){
				
				String temp = DataFormat.hexDateToStr(lptime).substring(0,8)+"0015";
				lptime = DataFormat.strDate2Hex(Util.addMinYymmdd(temp,-60*24));
				lptime[4] = 24;
				lptime[5] = 0;
			}else {
				lptime[4] = lp_time[5];
				lptime[5] = lp_time[6];
			}

			
		}catch(Exception e){
			logger.debug(e);
		}

		return lptime;
	}

	
	private byte[] getChannelData(int start, int len){
		
		byte[] chan = new byte[LP_CHAN_COUNT*LP_CHAN_SIZE];
		
		try {

			byte[] lp_chan = DataFormat.select(data,start,len);

			chan[0] = lp_chan[0];
			chan[1] = lp_chan[1];
			chan[2] = lp_chan[2];
			chan[3] = lp_chan[3];
			// except fourth idx
			chan[4] = lp_chan[5];
			chan[5] = lp_chan[6];
			chan[6] = lp_chan[7];
			chan[7] = lp_chan[8];

		}catch(Exception e){
			logger.debug(e);
		}

		return chan;
	}

	/*
	private byte getPfailFlag(int idx){
		
		byte[] temp = new byte[2];
		byte[] event = new byte[2];
		
		try {
			temp[0] = data[idx+1];
			temp[0] = (byte) ((temp[0] >> 5) & 0xff);
			temp[1] = (byte) ((data[idx] | temp[0]) & 0xff);
		
			event[0] = temp[1];
			event[1] = data[idx+2];
			
		}catch(Exception e){
			logger.debug(e);
		}
		
		if((event[1] & 0x80) > 0){
			return 'P';
		}

		return 'N';
	}
	*/
	
	
	private byte[] getEventFlag(int idx){
		
		byte[] temp = new byte[2];
		byte[] event = new byte[2];
		
		try {
			
			temp[0] = data[idx+1];
			temp[0] = (byte) ((temp[0] >> 5) & 0xff);
			temp[1] = (byte) ((data[idx] | temp[0]) & 0xff);
		
			event[0] = temp[1];
			event[1] = data[idx+2];
			
		}catch(Exception e){
			logger.debug(e);
		}
		
		return event; //origin event code return  
	}
	
	/*
	private byte getEventFlag(int idx){
		
		byte[] temp = new byte[2];
		byte[] event = new byte[2];
		
		try {
			
			temp[0] = data[idx+1];
			temp[0] = (byte) ((temp[0] >> 5) & 0xff);
			temp[1] = (byte) ((data[idx] | temp[0]) & 0xff);
		
			event[0] = temp[1];
			event[1] = data[idx+2];
			
		}catch(Exception e){
			logger.debug(e);
		}
		
		return event[1]; //origin event code return  
	}
	*/

}
