/*
 * @(#)ST064.java       1.0 06/12/14 *
 *
 * Load Profile.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. *
 * This software is the confidential and proprietary information of
 * Nuritelcom, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Nuritelecom.
 */

package com.aimir.fep.meter.parser.SM110Table;

import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class ST064Short extends ST064 implements java.io.Serializable {

	private static final long serialVersionUID = 558914077741912672L;

	private int totpulsecnt;

    private int NBR_BLKS_SET1;
    private int NBR_BLK_INTS_SET1;
    private int NBR_CHANS_SET1;
    private int INT_TIME_SET1;
    private int NBR_VALID_INT;

    private int energyscale;
    private int powerscale;
    private int displayscale;
    private int dispmult;
    private ST062 st062 = null;

    private final int LEN_BLK_END_TIME = 5;
    private int LEN_END_READINGS;
    private int LEN_NBR_INTERVAL_RECORD;
    private int LEN_EXT_INTERVAL_STATUS;
    private int LEN_INTERVAL_DATA;

    private byte[] data;
    private String lpstarttime;
    private String lpendtime;
    private String blockendtime = null;
    private int emptylpcount;
    private LPData[] lpdata;
    private String meterId;

    private static Log log = LogFactory.getLog(ST064Short.class);

    public ST064Short() {}

    /**
     * Constructor .<p>
     *
     * @param data - read data (header,crch,crcl)
     */
    public ST064Short(byte[] data,
                    String meterId,
                    int nbr_blks_set1,
                    int nbr_blk_ints_set1,
                    int nbr_chans_set1,
                    int int_time_set1,
                    int energyscale,
                    int powerscale,
                    int displayscale,
                    int dispmult,
                    ST062 st062,
                    int nbrValidInt) {
        this.data = data;
        this.NBR_BLKS_SET1     = nbr_blks_set1;
        this.NBR_BLK_INTS_SET1 = nbr_blk_ints_set1;
        this.NBR_CHANS_SET1    = nbr_chans_set1;
        this.INT_TIME_SET1     = int_time_set1;
        this.energyscale = energyscale;
        this.powerscale = powerscale;
        this.displayscale = displayscale;
        this.dispmult = dispmult;
        this.st062 = st062;
        this.meterId = meterId;
        this.NBR_VALID_INT=nbrValidInt;
        log.debug("nbrValidInt :"+nbrValidInt);
        try{
            log.debug("scalar0=>"+st062.getSCALAR(0)+" scalar1=>"+st062.getSCALAR(1));
            log.debug("divisor0=>"+st062.getDIVISOR(0)+" divisor1=>"+st062.getDIVISOR(1));
        }catch(Exception e){log.error(e,e);}

        this.LEN_END_READINGS = nbr_chans_set1*6;
        this.LEN_NBR_INTERVAL_RECORD = nbr_blk_ints_set1*(1+nbr_chans_set1*5/2);
        this.LEN_EXT_INTERVAL_STATUS = 1+(nbr_chans_set1/2);
        this.LEN_INTERVAL_DATA = nbr_chans_set1*2;

        try{
            parseLP();
        }catch(Exception e){
            log.warn("parse LP Failed=>"+e);
        }
    }

    public LPData[] getLPData()
    {
        return this.lpdata;
    }

    public int getTotpulseCount()
    {
        if(lpdata != null)
            return this.lpdata.length;
        else
            return 0;
    }

    public void parseLP()
    {
        int empty_blk_cnt = 0;

        int oneblocksize = LEN_BLK_END_TIME
                         + LEN_END_READINGS
                         + this.LEN_NBR_INTERVAL_RECORD;

        log.debug("datalen:"+data.length+ " oneblksize=>"+oneblocksize);
        int datalen = data.length;
        int block_count = data.length/oneblocksize;
        log.debug("blkcnt:"+block_count);
        int lpcount = (block_count)*this.NBR_BLK_INTS_SET1;

        ArrayList[] lists = new ArrayList[block_count];

        String endtime = new String();

        byte[] lp;
        int x = 0;

        try{
            int offset = 0;

            for(int i = 0; i < block_count; i++){

                byte[] blk = new byte[oneblocksize];
                blk = DataFormat.select(data,offset,oneblocksize);

                endtime = Util.getQuaterYymmddhhmm(
                            getYymmddhhmm(blk,0,this.LEN_BLK_END_TIME),this.INT_TIME_SET1);
                endtime = Util.addMinYymmdd(endtime, -this.INT_TIME_SET1);
                // 미터에서 LP시간이 00시00분부터 00시15분 사이의 데이터는 00시 15분으로 저장하기 때문에
                //서버에서는 해당 기간의 사용데이터는 00시 00분으로 계산하므로 주기만큼 빼야함.

                lp = DataFormat.select(blk,
                                        this.LEN_BLK_END_TIME+LEN_END_READINGS,
                                        this.NBR_BLK_INTS_SET1*(this.LEN_EXT_INTERVAL_STATUS+this.LEN_INTERVAL_DATA));
                log.debug("["+meterId+"] METER LP BLK=["+i+"] "+",ENDTIME=["+endtime+"]"+Util.getHexString(lp));
                lists[x] = new ArrayList();
                lists[x].add(0,new Integer(this.NBR_BLK_INTS_SET1));
                lists[x].add(1,endtime);
                lists[x].add(2,lp);
                x++;

                offset += oneblocksize;

            }

            ArrayList biglst = new ArrayList();
            int total_lp_cnt = 0;
            for(int i = 0; i < lists.length; i++){
                log.debug("parselpcn=>"+i);
                LPData[] lst = parseLP(lists[i]);
                    if(lst != null){
                        biglst.add(i,lst);
                        total_lp_cnt += lst.length;
                }
            }

            LPData[] lplist = new LPData[total_lp_cnt];
            int idx = 0;
            for(int i = 0; i < biglst.size(); i++){
                LPData[] temp = (LPData[])biglst.get(i);
                int j = 0;
                while(j < temp.length){
                    lplist[idx++] = temp[j++];
                }
            }

            this.lpdata = new LPData[total_lp_cnt];
            this.lpdata = lplist;
            //this.lpdata = checkEmptyLP(lplist);

        }catch(Exception e){
            log.warn(e);
        }
    }

    /*
    protected ArrayList checkEmptyLP(ArrayList[] lists) throws Exception {

        int len = lists.length;

        ArrayList newlist = new ArrayList();
        ArrayList emptytime = new ArrayList();
        String prevtime = new String();

        try{
            int idx = 0;
            for(int i = 0; i < len; i++){
                String nowtime = (String)lists[i].get(0);
                if(i == 0){
                    newlist.add(idx++,lists[i]);
                }else {

                    int emptycnt = getEmptyCount(prevtime,nowtime);
                    int chn = 0;
                    String time = prevtime;
                    for(int j = 0; j < emptycnt; j++){
                        time = Util.addMinYymmdd(time,this.INT_TIME_SET1);
                        ArrayList temp = new ArrayList();
                        temp.add(chn++,time);
                        for(int k = 0; k < this.NBR_CHANS_SET1; k++){
                            temp.add(chn++,new Double(0));
                            temp.add(chn++,new Double(0));
                        }

                        temp.add(chn++,new Integer(0));
                        temp.add(chn++,new Double(0));
                        newlist.add(idx++,temp);
                    }
                    newlist.add(idx++,lists[i]);
                }
                prevtime = nowtime;
            }

            return newlist;
        }catch(Exception e){
            throw new Exception("check empty lp : "+e);
        }

    }
    */


    protected LPData[] orderby(LPData[] lists)
    {

        int size = lists.length;
        LPData[] newlists = new LPData[size];

        for(int i = 0; i < size; i++){
            newlists[i] = lists[size-i-1];
        }
        return newlists;

    }

    protected LPData[] orderby(LPData[] lists, int emptycnt)
    {

        int size = lists.length-emptycnt;
        LPData[] newlists = new LPData[size];

        for(int i = 0; i < size; i++){
            newlists[i] = lists[size-i-1];
        }
        return newlists;

    }

    protected LPData[] parseLP(ArrayList list)
        throws Exception
    {
        LPData[] interval = null;
        LPData[] ordered = null;
        try{
            Integer lpcount = (Integer)list.get(0);
            String endtime  = (String) list.get(1);
            byte[] lps      = (byte[]) list.get(2);
            int emptycnt    = 0;

            interval = new LPData[lpcount.intValue()];
            ordered  = new LPData[lpcount.intValue()];

            int ofs = lps.length - (this.LEN_EXT_INTERVAL_STATUS+this.LEN_INTERVAL_DATA);
            int idx = 0;
            log.info("["+meterId+"] meter lpcount->"+lpcount.intValue()+" LP ENDTIME["+endtime+"]");
            int timecnt = 0;

            Double[] ch = new Double[this.NBR_CHANS_SET1];
            Double[] v = new Double[this.NBR_CHANS_SET1];

            for(int i = 0; i < lpcount.intValue(); i++){

                ch= new Double[this.NBR_CHANS_SET1];
                v = new Double[this.NBR_CHANS_SET1];

                byte[] lpstat = DataFormat.select(lps,ofs,this.LEN_EXT_INTERVAL_STATUS);
                ofs += this.LEN_EXT_INTERVAL_STATUS;

                for(int k = 0; k < this.NBR_CHANS_SET1;k++){
                    ch[k] = new Double(getCh(lps,ofs,this.LEN_INTERVAL_DATA/this.NBR_CHANS_SET1,0));
                    v[k] = new Double(getV(lps,ofs,this.LEN_INTERVAL_DATA/this.NBR_CHANS_SET1,0));
                    ofs += this.LEN_INTERVAL_DATA/this.NBR_CHANS_SET1;
                }

                double ch1 = 0;
                double v1 = 0;
                double ch2 = 0;
                double v2 = 0;

                if(this.NBR_CHANS_SET1 >=2 )
                {
                    ch1 = ch[0].doubleValue();
                    v1 = v[0].doubleValue();
                    ch2 = ch[1].doubleValue();
                    v2 = v[1].doubleValue();
                }
                else if(this.NBR_CHANS_SET1 == 1)
                {
                    ch1 = ch[0].doubleValue();
                    v1 = v[0].doubleValue();
                }

                boolean isPowerfail = false;
                for(int n = 0; n < lpstat.length;n++){
                    if(lpstat[n] == (byte)0xFF)
                    {
                        isPowerfail = true;
                    }
                    else
                    {
                        isPowerfail = false;
                    }
                }

                int chn = 0;
                if(isPowerfail)
                {
                    emptycnt++;
                }
                else
                {
                	//----------------------------------------------
                	// 제주용 검침 데이터는 s064 테이블에 BLK_END_TIME이
                	// 해당 블럭의 마지막을 의미 하지 않고,
                	// s063 테이블에 nbr_valid_int 번째 데이터를 의미함
                	// 또한 서버로는 최근 데이터가 먼저 들어와, 역순으로 계산함
                	//----------------------------------------------
                	/*
                	 * nbr_valid_int : 52
                    S064 : 593
                    + BLK_END_TIME 2011/06/13 06:00
                        115.037    0.000   88.892    0.000  109.808    0.000   52.290    0.000
                         38.504    0.000   53.716    0.000   43.733    0.000   42.782    0.000
                         82.713    0.000  224.845    0.000  194.422    0.000  212.961    0.000
                        237.205    0.000  195.848    0.000  158.770    0.000  210.584    0.000
                        190.144    0.000  164.950    0.000  221.993    0.000  178.735    0.000
                        169.228    0.000  165.901    0.000  147.362    0.000  160.672    0.000
                        191.095    0.000  154.017    0.000  159.246    0.000  133.576    0.000
                        110.284    0.000  144.034    0.000  119.315    0.000  113.611    0.000
                        146.886    0.000  111.234    0.000  119.315    0.000  122.643    0.000
                        105.055    0.000  137.854    0.000  116.463    0.000  108.382    0.000
                        127.396    0.000   92.695    0.000  113.136    0.000  146.411    0.000
                        116.463    0.000  142.133    0.000  121.217    0.000  112.185    0.000
                        125.495    0.000  160.196    0.000  115.988    0.000  44126.446    0.000 <--(여기가 52번째 블럭으로 2011/06/13 06:00이다)
                         -1.000   -1.000   -1.000   -1.000   -1.000   -1.000   40-1.000   -1.000
                         -1.000   -1.000   -1.000   -1.000   -1.000   -1.000   36-1.000   -1.000
                         -1.000   -1.000   -1.000   -1.000   -1.000   -1.000   32-1.000   -1.000
                         -1.000   -1.000   -1.000   -1.000   -1.000   -1.000   28-1.000   -1.000
                         -1.000   -1.000   -1.000   -1.000   -1.000   -1.000   24-1.000   -1.000
                         -1.000   -1.000   -1.000   -1.000   -1.000   -1.000   20-1.000   -1.000
                         -1.000   -1.000   -1.000   -1.000   -1.000   -1.000   16-1.000   -1.000
                         -1.000   -1.000   -1.000   -1.000   -1.000   -1.000   12-1.000   -1.000
                         -1.000   -1.000   -1.000   -1.000   -1.000   -1.000   8-1.000   -1.000
                         -1.000   -1.000   -1.000   -1.000   -1.000   -1.000   4-1.000   -1.000
                         -1.000   -1.000   -1.000   -1.000   -1.000   -1.000   0-1.000   -1.000
                    */
                	//if(i>=lpcount.intValue()-NBR_VALID_INT) {
	                    String lptime = Util.addMinYymmdd(endtime,-this.INT_TIME_SET1*timecnt);
	                    interval[idx] = new LPData();
	                    interval[idx].setDatetime(lptime);
	                    interval[idx].setCh(ch);
	                    interval[idx].setV(v);
	                    interval[idx].setFlag(0);
	                    interval[idx].setPF(new Double(getPF(ch1,ch2)));
	                    if(idx==0) {
	                    	log.info("["+meterId+"]["+i+"] Last lpdata=>"+interval[idx].toString());
	                    }
	                    else if(i+1==lpcount) {
	                    	log.info("["+meterId+"]["+i+"] First lpdata=>"+interval[idx].toString());
	                    }
	                    idx++;
	                    timecnt++;
                	//}
                }

                ofs = lps.length - (i+2)*(this.LEN_EXT_INTERVAL_STATUS+this.LEN_INTERVAL_DATA);
                ch = null;
                v= null;
            }

            log.debug("before orderby emptycnt=>"+emptycnt);
            ordered = orderby(interval,emptycnt);
        }catch(Exception e){
            log.warn("parselp err->", e);
        }
        //log.debug("after orderby");
        return ordered;

    }

    public String getEndTime()
    {
        try
        {
            return this.lpendtime;
        }
        catch (Exception e)
        {
            log.warn(e.getMessage());
        }
        return null;
    }

    public String getStartTime()
    {
        try
        {
            return this.lpstarttime;
        }
        catch (Exception e)
        {
            log.warn(e.getMessage());
        }
        return null;
    }


    /**
     * LP Channel data parsing.<p>
     * @param s
     * @param len
     * @return
     */
    private double getCh(byte[] b, int start, int len,int ch)
                                            throws Exception{

        int val = DataFormat.hex2dec(
                    DataFormat.LSB2MSB(
                        DataFormat.select(b,start,len)));

        return (val*(energyscale*Math.pow(10,-9))*(st062.getDIVISOR(ch)/st062.getSCALAR(ch)));

    }

    private double getV(byte[] b, int start, int len,int ch)
        throws Exception{

        int val = DataFormat.hex2dec(
                DataFormat.LSB2MSB(
                        DataFormat.select(b,start,len)));

        return (val*(energyscale*Math.pow(10,-9))*(st062.getDIVISOR(ch)/st062.getSCALAR(ch)))*60/this.INT_TIME_SET1;
    }

    public double getPF(double ch1, double ch2) throws Exception {

        double pf   = (float)(ch1/(Math.sqrt(Math.pow(ch1,2)+Math.pow(ch2,2))));

        if(ch1 == 0.0 && ch2 == 0.0)
            pf = 1.0;
        /* Parsing Transform Results put Data Class */
        if(pf < 0.0 || pf > 1.0)
            throw new Exception("BILL PF DATA FORMAT ERROR : "+pf);
        return pf;
    }

    private String getYymmddhhmm(byte[] b, int offset, int len)
                            throws Exception {

        int blen = b.length;
        if(blen-offset < 5)
            throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
        if(len != 5)
            throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);

        int idx = offset;

        int yy = DataFormat.hex2unsigned8(b[idx++]);
        int mm = DataFormat.hex2unsigned8(b[idx++]);
        int dd = DataFormat.hex2unsigned8(b[idx++]);
        int hh = DataFormat.hex2unsigned8(b[idx++]);
        int MM = DataFormat.hex2unsigned8(b[idx++]);

        StringBuffer ret = new StringBuffer();

        int currcen = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;

        int year   = yy;
        if(year != 0){
            year = yy + currcen;
        }

        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));

        return ret.toString();

    }


    private String getQuaterYymmddhhmm(String yymmddHHMM) throws Exception {

        String dateString = "";

        try{
            if(yymmddHHMM == null || yymmddHHMM.equals(""))
                throw new Exception("INPUT NULL");
            if(yymmddHHMM.length() < 12)
                throw new Exception("Length Error");

            int yy  = Integer.parseInt(yymmddHHMM.substring(0,4));
            int mm  = Integer.parseInt(yymmddHHMM.substring(4,6));
            int day = Integer.parseInt(yymmddHHMM.substring(6,8));
            int HH  = Integer.parseInt(yymmddHHMM.substring(8,10));
            int MM  = Integer.parseInt(yymmddHHMM.substring(10,12));

            if(((MM !=0) && (MM%this.INT_TIME_SET1!=0))){
                MM = (MM) - (MM)%this.INT_TIME_SET1;
            }

            dateString =  "" + yy +
            (mm < 10? "0" + mm:"" + mm) +
            (day < 10? "0" + day:"" + day) +
            (HH < 10? "0" + HH: "" + HH) +
            (MM < 10? "0" + MM: "" + MM);

        }catch(NumberFormatException e){
            throw new Exception("Util.getQuaterYymmdd() : "+e.getMessage());
        }
        return dateString;

    }

    private int getEmptyCount(String time1, String time2) {

        int ret = 0;
        long sec = 0;
        try {
            sec = Util.getDuration(time1,time2);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        ret = (int)(sec/this.INT_TIME_SET1);
        if(ret < 0) ret = 0;
        return  ret;
    }

}
