package com.aimir.fep.meter.parser.vcTable;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;

public class LP
{
    private static Log log = LogFactory.getLog(LP.class);

    private byte[] data = null;
    private int _cuvLen = 4;
    private int _cvLen = 4;
    private int _ctLen = 2;
    private int _cpLen = 3;
    private int _statusLen = 1;

    private int pw = 0;
    private int tu = 0;
    private int pu = 0;
    private int period = 0;
    private LPData[] lp = null;
    private String meterId = null;
    private int lpcnt = 0;
    String timestamp = null;

    public LP(String lt, int lpcnt, int pw, int tu, int pu, int period, String meterId, byte[] data) {
    	timestamp = lt;
    	this.lpcnt = lpcnt;
    	this.data = data;
        this.pw = pw;
        this.tu = tu;
        this.pu = pu;
        this.period = period;
        this.meterId = meterId;

        try{
            parseLP();
        }catch(Exception e){
            log.warn("CORUS[" +meterId + "] parse LP Failed=>"+e,e);
        }
    }

    public void parseLP() throws Exception
    {
        int pos = 0;
        lp = new LPData[lpcnt];

        String dateTime = timestamp;
        log.debug("LP DATETIME[" + dateTime + "]");

        double ch1 = 0;
        double ch2 = 0;
        double ch3 = 0;
        double ch4 = 0;
        double ch5 = 0;
        double status = 0;
        
        ArrayList dataArray = new ArrayList();
        LPData lpData = null;

        try{
            for (int i = 0; i < lpcnt; i++) {
            	lpData = new LPData();
            	lpData.setDatetime(Util.addMinYymmdd(dateTime, i * period));

            	if(!Hex.decode(DataUtil.select(data, pos, _cuvLen)).equals("FFFFFFFF")){
                    ch1 = DataFormat.hex2long(DataUtil.select(data, pos, _cuvLen)) * Math.pow(10, pw);
                    pos += _cuvLen;
                    ch2 = DataFormat.hex2long(DataUtil.select(data, pos, _cvLen)) * Math.pow(10, pw);
                    log.debug("raw"+Hex.decode(DataUtil.select(data, pos, _cvLen)));
                    log.debug("test0: "+DataFormat.hex2dec(DataUtil.select(data, pos, _cvLen)));
                    log.debug("test: "+Math.pow(10, pw));
                    pos += _cvLen;
                    ch3 = DataFormat.hex2signed16(DataUtil.select(data, pos, _ctLen)) * Math.pow(10, tu);
                    pos += _ctLen;
                    ch4 = DataFormat.hex2long(DataUtil.select(data, pos, _cpLen)) * Math.pow(10, pu);
                    pos += _cpLen;
                    ch5 = ch2*60/this.period;
                    status = DataFormat.hex2long(DataUtil.select(data, pos, _statusLen));
                    pos += _statusLen;
                    lpData.setCh(new Double[]{ch1,ch2,ch3,ch4,ch5});
                    lpData.setV(new Double[]{0.0, 0.0, 0.0, 0.0, 0.0});
                    lpData.setFlag(0);
                    lpData.setLPChannelCnt(5);
                    dataArray.add(lpData);
                    log.debug("lpData["+i+"] " + lpData);
            	}
            }
        }catch(Exception e){
        	log.error("parse LP Failed=>"+e,e);
        	lp = null;
        }
        
        Object[] obj = dataArray.toArray();        
        lp = new LPData[dataArray.size()];
        for(int i = 0; i < obj.length; i++){
        	lp[i] = (LPData)obj[i];
        }

    }

    public LPData[] getLPData()
    {
        return lp;
    }
}
