package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing I210 Pulse Meter Data
 * implemented in Pillipin
 *
 * @author Yeon Kyoung Park (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2007-07-07 12:00:15 +0900 $,
 */
public class SM110P extends MeterDataParser implements java.io.Serializable
{
	private static final long serialVersionUID = 7503986198693601423L;

	private static Log log = LogFactory.getLog(SM110P.class);
    
    private byte[] rawData = null;
    private String meterId = null;
    private Double meteringValue = null;
    private int flag = 0;

    private ZEUPLS_Alarm alarm = null;
    private ZEUPLS_Status status = null;

    private int memberId = 0;
    private String serialNumber = null;
    private long register = 0;
    private int[] preDayLPValue = new int[24];

    private long basePulse = -1;

    private byte[] OPERATINGDAY = new byte[2];
    private byte[] ACTIVEMIN = new byte[2];
    private byte[] BATTERYVOLT = new byte[2];
    private byte[] LPPERIOD = new byte[1];
    private byte[] CURPULSE = new byte[4];
    private byte[] BASEPULSE = new byte[4];
    private byte[] LP = new byte[2];

    /**
     * constructor
     */
    public SM110P()
    {
    }

    /**
     * getRawData
     */
    public byte[] getRawData()
    {
        return rawData;
    }

    /**
     * get data length
     * @return length
     */
    public int getLength()
    {
        if(rawData == null)
            return 0;

        return rawData.length;
    }

    /**
     * parse meter mesurement data
     * @param data 
     */
    public void parse(byte[] data) throws Exception
    {
        if(data.length == 67 || data.length == 70)
            parseNAM_V_1_6(data);
        else if(data.length == 73 || data.length == 76)
            parseNAM_V_1_7(data);
        else if(data.length == 63 || data.length >= 101)
            parse_V_3_0(data);
        else {
            
            int pos = 4;

            byte[] bx = null;
            // Get Hourly Pulse Value at Previous Day
            bx = new byte[30];
            System.arraycopy(data,pos,bx,0,bx.length);
            pos+=bx.length;

            byte[] bxx = new byte[2];
            int nextPos = 0;
            int ival = 0;
            int mval = 0;
            int day_sum = 0;
            for(int i = 0 ; i < 24 ; i++)
            {
                nextPos = 24 + (i/4);
                bxx[1] = bx[i];
                bxx[0] = bx[nextPos];

                mval = i%4;
                if(mval == 0)
                {
                    bxx[0] = (byte)((bxx[0] & 0xC0) >> 6);
                }
                else if(mval == 1)
                {
                    bxx[0] = (byte)((bxx[0] & 0x30) >> 4);
                } else if(mval == 2)
                {
                    bxx[0] = (byte)((bxx[0] & 0x0C) >> 2);
                } else if(mval == 3)
                {
                    bxx[0] = (byte)(bxx[0] & 0x03);
                }

                ival = DataUtil.getIntTo2Byte(bxx);

                preDayLPValue[i] = ival;
                day_sum += ival;
            }

            // Get Base Pulse(Pulse Value in Today - 0 Hour) 
            bx = new byte[4];
            System.arraycopy(data,pos,bx,0,bx.length);
            pos+=bx.length;
            this.basePulse = DataUtil.getLongToBytes(bx);
            this.meteringValue = (this.basePulse)*1.0d;
            log.debug("METERINGVALUE[" + meteringValue + "]");
        }
    }

    public void parse_V_3_0(byte[] data) throws Exception
    {
        int pos = 0;
        
        System.arraycopy(data, pos, OPERATINGDAY, 0, OPERATINGDAY.length);
        pos += OPERATINGDAY.length;
        int operatingDay = DataUtil.getIntTo2Byte(OPERATINGDAY);
        log.debug("OPERATIOGDAY[" + operatingDay + "]");
        
        System.arraycopy(data, pos, ACTIVEMIN,0, ACTIVEMIN.length);
        pos += ACTIVEMIN.length;
        int activeMin = DataUtil.getIntTo2Byte(ACTIVEMIN);
        log.debug("ACTIVEMIN[" + activeMin + "]");
        
        System.arraycopy(data, pos, BATTERYVOLT, 0, BATTERYVOLT.length);
        pos += BATTERYVOLT.length;
        int batteryVolt = DataUtil.getIntTo2Byte(BATTERYVOLT);
        log.debug("BATTERYVOLT[" + batteryVolt + "]");
        
        // from here, lp
        System.arraycopy(data, pos, LPPERIOD, 0, LPPERIOD.length);
        pos += LPPERIOD.length;
        int period = DataUtil.getIntToBytes(LPPERIOD);
        log.debug("LPPERIOD[" + period + "]");
        
        System.arraycopy(data, pos, CURPULSE, 0, CURPULSE.length);
        pos += CURPULSE.length;
        meteringValue = new Double(DataUtil.getLongToBytes(CURPULSE));
        log.debug("CURPULSE[" + meteringValue + "]");

        // get real pulse
        /* lucky
         * initpulse 0 maxpulse 0 ignore
        lp = IUtil.getRealPulseValue(this.meterId, lp);
        log.debug("REALPULSE[" + lp + "]");
         */
        
        System.arraycopy(data, pos, BASEPULSE, 0, BASEPULSE.length);
        pos += BASEPULSE.length;
        basePulse = DataUtil.getLongToBytes(BASEPULSE);
        log.debug("BASEPULSE[" + basePulse + "]");
        
        preDayLPValue = new int[period * 24];
        
        if (data.length - pos < (period * 24 * LP.length)) {
            log.warn("LP Length[" + (data.length - pos) + "] is WRONG");
            return;
        }
            
        for (int i = 0; i < preDayLPValue.length; i++) {
            System.arraycopy(data, pos, LP, 0, LP.length);
            pos += LP.length;
            preDayLPValue[i] = DataUtil.getIntTo2Byte(LP);
            log.debug("PREDAYLPIDX[" + i + "] VALUE["+ preDayLPValue[i] + "]");
        }
    }
    
    /**
     * parse meter mesurement data
     * @param data 
     */
    public void parseNAM_V_1_6(byte[] data) throws Exception
    {
        int pos = 0;

        if(data.length != 67)
        {
            if(data.length == 70)
                pos+=3;
            else
                throw new Exception("parseNAM_V_1_7 I210P data length "
                        +" invalid [" + data.length +"]  (valid[67]");
        }

        this.rawData = data;

        byte[] bx = new byte[2];
        System.arraycopy(data,pos,bx,0,bx.length);
        pos+=bx.length;
        this.alarm.parse(bx);

        bx = new byte[12];
        System.arraycopy(data,pos,bx,0,bx.length);
        pos+=bx.length;
        this.status = new ZEUPLS_Status();
        this.status.parse(bx);

        this.memberId = DataUtil.getIntToByte(data[pos]);
        pos++;

        bx = new byte[18];
        System.arraycopy(data,pos,bx,0,bx.length);
        pos+=bx.length;
        this.serialNumber = new String(bx).trim();
        this.meterId = serialNumber;

        bx = new byte[4];
        System.arraycopy(data,pos,bx,0,bx.length);
        pos+=bx.length;
        this.register = DataUtil.getLongToBytes(bx);
        this.meteringValue = new Double(this.register);
        
        // Calculate init Pulse
        /* lucky
         * initpulse 0 maxpulse 0 ignore
        lp = IUtil.getRealPulseValue(this.meterId, lp);
        log.debug("REALPULSE[" + lp + "]");
         */

        bx = new byte[30];
        System.arraycopy(data,pos,bx,0,bx.length);

        byte[] bxx = new byte[2];
        int nextPos = 0;
        int ival = 0;
        int mval = 0;
        for(int i = 0 ; i < 24 ; i++)
        {
            nextPos = 24 + (i/4);
            bxx[1] = bx[i];
            bxx[0] = bx[nextPos];

            mval = i%4;
            if(mval == 0)
            {
                bxx[0] = (byte)((bxx[0] & 0xC0) >> 6);
            }
            else if(mval == 1)
            {
                bxx[0] = (byte)((bxx[0] & 0x30) >> 4);
            } else if(mval == 2)
            {
                bxx[0] = (byte)((bxx[0] & 0x0C) >> 2);
            } else if(mval == 3)
            {
                bxx[0] = (byte)(bxx[0] & 0x03);
            }

            ival = DataUtil.getIntTo2Byte(bxx);

            preDayLPValue[i] = ival;
        }
    }

    /**
     * parse meter mesurement data
     * @param data 
     */
    public void parseNAM_V_1_7(byte[] data) throws Exception
    {
        int pos = 0;

        if(data.length != 73)
        {
            if(data.length == 76)
                pos+=3;
            else
                throw new Exception("parseNAM_V_1_7 I210P data length "
                        +" invalid [" + data.length +"]  (valid[71]");
        }

        this.rawData = data;

        byte[] bx = new byte[2];
        System.arraycopy(data,pos,bx,0,bx.length);
        pos+=bx.length;
        this.alarm = new ZEUPLS_Alarm();
        this.alarm.parse(bx);

        bx = new byte[12];
        System.arraycopy(data,pos,bx,0,bx.length);
        pos+=bx.length;
        this.status = new ZEUPLS_Status();
        this.status.parse(bx);

        // NZC(MCU) ID
        pos+=2;

        this.memberId = DataUtil.getIntToByte(data[pos]);
        pos++;

        bx = new byte[18];
        System.arraycopy(data,pos,bx,0,bx.length);
        pos+=bx.length;
        this.serialNumber = new String(bx).trim();
        this.meterId = serialNumber;

        bx = new byte[4];
        System.arraycopy(data,pos,bx,0,bx.length);
        pos+=bx.length;
        this.register = DataUtil.getLongToBytes(bx);
        this.meteringValue = new Double(this.register);

        // Get Hourly Pulse Value at Previous Day
        bx = new byte[30];
        System.arraycopy(data,pos,bx,0,bx.length);
        pos+=bx.length;

        byte[] bxx = new byte[2];
        int nextPos = 0;
        int ival = 0;
        int mval = 0;
        for(int i = 0 ; i < 24 ; i++)
        {
            nextPos = 24 + (i/4);
            bxx[1] = bx[i];
            bxx[0] = bx[nextPos];

            mval = i%4;
            if(mval == 0)
            {
                bxx[0] = (byte)((bxx[0] & 0xC0) >> 6);
            }
            else if(mval == 1)
            {
                bxx[0] = (byte)((bxx[0] & 0x30) >> 4);
            } else if(mval == 2)
            {
                bxx[0] = (byte)((bxx[0] & 0x0C) >> 2);
            } else if(mval == 3)
            {
                bxx[0] = (byte)(bxx[0] & 0x03);
            }

            ival = DataUtil.getIntTo2Byte(bxx);

            preDayLPValue[i] = ival;
        }

        // Get Base Pulse(Pulse Value in Today - 0 Hour) 
        bx = new byte[4];
        System.arraycopy(data,pos,bx,0,bx.length);
        pos+=bx.length;
        this.basePulse = DataUtil.getLongToBytes(bx);
    }

    /**
     * get meter id
     * @return meter id
     */
    public String getMeterId()
    {
        return this.meterId;
    }

    /**
     * get Metering Value
     * @return Metering Value
     */
    public Double getMeteringValue()
    {
        return this.meteringValue;
    }

    /**
     * get ZEUPLS Alarm Data
     * @return  ZEUPLS Alarm 
     */
    public ZEUPLS_Alarm getAlarm()
    {
        return this.alarm;
    }

    /**
     * get ZEUPLS Status Data
     * @return ZEUPLS Status 
     */
    public ZEUPLS_Status getStatus()
    {
        return this.status;
    }

    /**
     * get member ID
     * @return  member ID
     */
    public int getMemberId()
    {
        return this.memberId;
    }

    /**
     * get Previous Day LP Values 
     * @return previous day lps 
     */
    public int[] getPreDayLPValues()
    {
        return this.preDayLPValue;
    }

    /**
     * get flag
     * @return flag measurement flag
     */
    public int getFlag()
    {
        return this.flag;
    }

    /**
     * set flag
     * @param flag measurement flag
     */
    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    /**
     * get base pulse(current day 0 hour pulse)
     * @return basePulse 
     */
    public long getBasePulse()
    {
        return this.basePulse;
    }

    /**
     * set basePulse
     * @param basePulse Base Pulse
     */
    public void setBasePulse(long basePulse)
    {
        this.basePulse = basePulse;
    }

    /**
     * get String
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append("I210 DATA[");
        sb.append("(meteringValue=").append(meteringValue).append("),");
        sb.append("(meterId=").append(meterId).append(')');
        sb.append("(alarm=").append(alarm).append(')');
        sb.append("(status=").append(status).append(')');
        sb.append("(basePulse=").append(basePulse).append(')');
        sb.append("(memberId=").append(memberId).append(')');
        sb.append("(lp=(");
        for(int i = 0 ; i < 24 ; i++)
        {
            sb.append("Hour["+i+"]=["+preDayLPValue[i]+"], ");
        }
        sb.append(")\n");
        sb.append("]\n");

        return sb.toString();
    }

    /**
     * get Data
     */
    @Override
    public LinkedHashMap getData()
    {
        LinkedHashMap res = new LinkedHashMap(16,0.75f,false);
        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
        
        StringBuffer sb = new StringBuffer();
        for(int i = 0 ; i < 24 ; i++)
        {
            sb.append("Hour["+i+"]=["+preDayLPValue[i]+"], ");
        }
        res.put("Metering Value",""+df3.format(meteringValue));
        //res.put("Meter Number",""+meterId);
        res.put("Previous Day LP",sb.toString());

        return res;
    }
}
