/* 
 * @(#)SCE8711_LP.java       1.0 12/05/17 *
 * 
 * Load Profile.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.actarisSCE8711Table;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.device.Meter;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class SCE8711_LP2 {
    
    private int energyscale;
	private byte[] rawData = null;
    private int[] chanScale= null;
    private int[] chanUnit= null;
    private int cntCH=0;
    private int resolution=5;
    private String meterId="";
    private double transFactor=1.0;
    private double ke= 0.0001;
    private String calType ="0";
    
    public static final int OFS_CH_CNT = 0;
    public static final int OFS_CH_INFO = 1;
    
    public static final int OFS_LP_CH_HOURMIN = 0;
    public static final int OFS_LP_CH_DATA = 2;
    
    public static final int LEN_CH_CNT = 1;
    public static final int LEN_CH_INFO = 2;
    public static final int LEN_DATETIME_LP_DATA = 12;
    public static final int LEN_DATE_LP_DATA = 5;
    public static final int LEN_TIME_LP_DATA = 7;
    
    public static final int LEN_ERRORSTAT_LP_DATA = 4;
    public static final int LEN_LP_CH_HOURMIN = 2;
    public static final int LEN_LP_CH_DATA = 2;
    public static final int LEN_NBR_LP_DATA = 2;
    
    public static final int OFS_LP_BLOCK = 3;
       
    DecimalFormat dformat = new DecimalFormat("#0.000000");
        
    private Log log = LogFactory.getLog(SCE8711_LP2.class);
    
	/**
	 * Constructor .<p> 
	 * @param data - read data (header,crch,crcl)
	 */
	public SCE8711_LP2(byte[] rawData, int resolution, Meter meter) {
		this.rawData = rawData;
		this.resolution = resolution;
	//	this.ke =ke;
		if(meter!=null) {
	        try{	        	
	        	this.ke = meter.getPulseConstant();	        	
	        }catch(Exception e){
	        	log.error(e,e);
	        }
		}
	}
    
    /**
     * The time in minutes LP interval duration ST61
     * LP Interval Time
     * Default 15
     * (0,1,2,3,4,5,6,10,12,15,20,30,60)
     * @return
     */

	public int getChannelCnt() throws Exception {
		//cntCH = DataFormat.hex2unsigned16(DataFormat.select(rawData,OFS_CH_CNT,LEN_CH_CNT ));
		cntCH =  DataFormat.hex2unsigned8(rawData[OFS_CH_CNT]);
		return cntCH;
    }
    	
	public void getChannelInfo(byte[] data, int channelCnt) throws Exception {
		
		int ofs =0;
		chanScale = new int[channelCnt];
		chanUnit = new int[channelCnt];
		for(int i=0; i<channelCnt; i++){
			chanScale[i] = DataFormat.hex2signed8(data[ofs]);
			chanUnit[i]  = DataFormat.hex2unsigned8(data[ofs+1]);
			ofs+=2;
		}
		DataFormat.hex2unsigned8(rawData[OFS_CH_CNT]);
    //    return DataFormat.hex2unsigned8(rawData[OFS_CH_CNT]);
    }
	
    public int getNBR_DATE(int ofs) throws Exception {
        return DataFormat.hex2unsigned8(rawData[ofs]);
    }
    
    public int getNBR_LP_DATA(byte[] data) throws Exception {
        return DataFormat.hex2unsigned16(data);
    }
    
    public int getNBR_LP_CHAN(byte cnt) throws Exception {
        return cntCH;
    }
    
    public LPData[] parse() throws Exception {
        
        ArrayList<LPData> list = new ArrayList<LPData>();
        getChannelCnt();
        getChannelInfo(DataFormat.select(rawData,OFS_CH_INFO, cntCH*LEN_CH_INFO), cntCH);
        int offset = OFS_CH_INFO+cntCH*LEN_CH_INFO;
        int nbrDATE = getNBR_DATE(offset++);
        
        log.debug("transFactor : "+transFactor);
        
        for(int j=0; j<nbrDATE; j++){
        	byte[] date = DataFormat.select(rawData, offset, LEN_DATETIME_LP_DATA);
        	offset+=LEN_DATETIME_LP_DATA;
        	int nbrLP = getNBR_LP_DATA(DataFormat.select(rawData, offset,LEN_NBR_LP_DATA));
        	offset+=LEN_NBR_LP_DATA;
        	
	        if(nbrLP>0){
		        
		        int LEN_LP_DATA_SET = LEN_LP_CH_HOURMIN+ cntCH*LEN_LP_CH_DATA;
		        LPData[] interval = new LPData[nbrLP];
		        
		        String entryDateTime = new DLMSDateTime(date).getDateTime();
		        if(entryDateTime.length()==8)
		        	entryDateTime+="000000";
		        String beforeDateTime ="";
		        String lpdatetime = "";
		        LPData accum_lp =  null;
		        for(int i = 0; i < interval.length; i++){
		            //String lpDate=Util.addMinYymmdd(entryDateTime.substring(0,12), resolution*i);
		            LPData lp_blk =null;		            
		            lp_blk = parseChannel(DataFormat.select(rawData,offset,LEN_LP_DATA_SET), cntCH, entryDateTime);
		            offset +=LEN_LP_DATA_SET;
		            //
		            if(lp_blk!=null){
		            	
		            	lpdatetime = lp_blk.getDatetime();
		            	int min = Integer.parseInt(lpdatetime.substring(10,12));
		            	int sec = Integer.parseInt(lpdatetime.substring(12,14));
		            	int modMin = min%resolution;
		            	//5분단위가 아니거나,이전 블럭과 시간이 같을때.
		            	if((modMin !=0 || sec !=0) || (beforeDateTime.length()>0 && lpdatetime.equals(beforeDateTime))){
		            		log.debug("beforeDateTime ="+beforeDateTime+",  lpdatetime="+lpdatetime);
		            		String newdateTime = Util.addMinYymmdd(lpdatetime.substring(0,12), resolution-modMin);
		            		newdateTime = newdateTime.substring(0,12)+"00";
		            		
		            		if(accum_lp ==null){
		            			accum_lp = lp_blk;
		            			accum_lp.setDatetime(newdateTime);
		            		} else{
		            			if(accum_lp.getDatetime().equals(newdateTime)){
		            				accum_lp = sumLPData(accum_lp, lp_blk);
		            				
		            			} else{
		            				//list.add(accum_lp);
		            				listAdd(list,accum_lp);
		            				accum_lp = lp_blk;
		            				accum_lp.setDatetime(newdateTime);
		            				beforeDateTime =lpdatetime;
		            			}
		            		}
		            	} else{
		            		if(accum_lp ==null){
		            			//list.add(lp_blk);
		            			listAdd(list,lp_blk);
		            		}else{
		            			if(accum_lp.getDatetime().equals(lpdatetime)){
		            				accum_lp = sumLPData(accum_lp, lp_blk);
		            			//	list.add(accum_lp);
		            				listAdd(list,accum_lp);
		            				accum_lp = null;
		            			}else{
		            			//	list.add(accum_lp);
		            			//	list.add(lp_blk);
		            				listAdd(list,accum_lp);
		            				listAdd(list,lp_blk);
		            				accum_lp = null;
		            			}
		            		}
		            		
		            		beforeDateTime =lpdatetime;
		            	}
		            	
		         //   	list.add(lp_blk);
		            }
		            
		        }
		  //      Collections.sort(list,LPComparator.TIMESTAMP_ORDER);
	        }
        }
      //  return checkEmptyLP(list);
        if(list != null && list.size() > 0){
            LPData[] data = null;
            Object[] obj = list.toArray();            
            data = new LPData[obj.length];
            for(int i = 0; i < obj.length; i++){
                data[i] = (LPData)obj[i];
            }
            return data;
        }
        else
        {
            return null;
        }
    }
    
    private void listAdd(ArrayList list, LPData lpdata){    	        
        list.add(lpdata);
    }
    
    private LPData sumLPData(LPData accumLP, LPData newLP) throws Exception {
    	
    	LPData sumdata = new LPData();
    	
    	Double[] accch = accumLP.getCh();
    	Double[] newch = newLP.getCh();
    	Double[] sumch = new Double[newch.length];
    	
    	for(int i=0; i<newch.length; i++){
    		sumch[i] = new Double(accch[i].doubleValue() +newch[i].doubleValue());
    	}
    	
    	sumdata.setCh(sumch);    	
    	sumdata.setPF(getPF(sumch[0].doubleValue(), sumch[1].doubleValue()));
    	sumdata.setDatetime(accumLP.getDatetime());
    	sumdata.setFlag(accumLP.getFlag());    	
    	
    	return sumdata;
    }

    private LPData parseChannel(byte[] block, int cntCH, String datetime) throws Exception{

        LPData lpdata = new LPData();
        
        Double[] ch  = new Double[cntCH];

        int flag =0;
        int dst =0;
        
        log.debug("Date Raw: "+datetime+" : "+Hex.decode(DataFormat.select(block, OFS_LP_CH_HOURMIN, LEN_LP_CH_HOURMIN)));
        String lpdatetime = getLPDateTime(datetime,  DataFormat.select(block, OFS_LP_CH_HOURMIN, LEN_LP_CH_HOURMIN));
        
        lpdatetime = Util.addMinYymmdd(lpdatetime.substring(0,12), -resolution);
        log.debug("lpdatetime"+lpdatetime);
        for(int i = 0; i < cntCH; i++){
            ch[i] =  new Double(dformat.format((double)DataFormat.hex2unsigned16( DataFormat.select(
                       block, OFS_LP_CH_DATA + LEN_LP_CH_DATA*i, LEN_LP_CH_DATA))*Math.pow(10, chanScale[i]-3)));//올라오는 데이터는 wh ui는 kwh 이므로 1000으로 나눠 줌(펄스 컨스탄트를 써야겠지만 시간상 그냥 밗음) 
            log.debug("ch["+i+"]"+ch[i].doubleValue());
        }
        
        lpdata.setCh(ch);
        
        lpdata.setFlag(flag);
        
        lpdata.setPF(getPF(ch[0].doubleValue(), ch[1].doubleValue()));
        lpdata.setDatetime(lpdatetime);
        
        return lpdata;
    }
    
    private double getPF(double ch1, double ch2) throws Exception {

        double pf   = (float)(ch1/(Math.sqrt(Math.pow(ch1,2)+Math.pow(ch2,2))));

        if(ch1 == 0.0 && ch2 == 0.0)
            pf = (double) 1.0;
        /* Parsing Transform Results put Data Class */
        if(pf < 0.0 || pf > 1.0)
            throw new Exception("BILL PF DATA FORMAT ERROR : "+pf);
        return pf;
	}
    
    private String getLPDateTime(String datetime, byte[] hourmin)  throws Exception{
    	int hh = DataFormat.hex2unsigned8(hourmin[0]);
		int MM = DataFormat.hex2unsigned8(hourmin[1]);
		int ss = 0;
		
		StringBuffer ret = new StringBuffer();
		ret.append(datetime.substring(0,8));
		ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
		
		return ret.toString();
    }
    
    private String getDateTime(byte[] date)  throws Exception{
    	int blen = date.length;
		if(blen != 4)
			throw new Exception("YYYYMMDDHHMMSS LEN ERROR : "+blen);
		
		int MASK_YEAR 	= 0x00000FFF;
		int MASK_MONTH 	= 0x0000F000;
		int MASK_DAY 	= 0x001F0000;
		int MASK_HOUR 	= 0x03e00000;
		int MASK_MIN 	= 0xFC000000;
		
		int dateInt =(int)DataFormat.hex2unsigned32(date);

		int yy = dateInt & MASK_YEAR;
		int mm = (dateInt & MASK_MONTH) >> 12;
		int dd = (dateInt & MASK_DAY)   >> 16;
		int hh = (dateInt & MASK_HOUR)  >> 21;
		int MM = (dateInt & MASK_MIN)   >> 26;
		int ss = 0;

		StringBuffer ret = new StringBuffer();
				
		ret.append(Util.frontAppendNStr('0',Integer.toString(yy),4));
		ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
		
	//	log.debug("getDateTime :"+ret.toString());
		return ret.toString();
    } 
    
}