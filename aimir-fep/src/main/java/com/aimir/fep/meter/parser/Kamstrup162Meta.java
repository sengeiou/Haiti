package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.TimeLocaleUtil;

/**
 * Kamstrup 162 계량기의 검침데이타 파서
 * 모뎀의 검침데이타 포맷이 아니다.
 *
 * @author jspark (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2012-06-14 09:17:15 +0900 $,
 */
public class Kamstrup162Meta extends MeterDataParser implements java.io.Serializable
{
	private static final long serialVersionUID = -8775356910022692575L;
	private byte[] rawData = null;
    private Double meteringValue = null;
    private String meterType = null;
    private String meterNumber = null;
    private int power = 0;
    private int peakPower = 0;
    private int hour = 0;
    private Double resetCounter = null;
    private int pulseInput = 0;
    private int specialData = 0;
    private Double energy = null;
    private int tariff1 = 0;
    private int tariff2 = 0;
    private int flag = 0;

    private final int FIRSTDATA_LEN=96;
    private final int SECONDDATA_LEN=80;
    private final int THIRDDATA_LEN=34;

    /**
     * constructor
     */
    public Kamstrup162Meta()
    {
    }

    /**
     * check CRC
     */
    private boolean checkCRC(byte[] data) throws Exception
    {
        int dlen = data.length - 4;
        String emdata = new String(data,1,dlen);
        String crc = new String(data,1+dlen,2);

        byte[] hexdata = Hex.encode(emdata);
        int sum = 0;
        for(int i = 0 ; i < hexdata.length  ; i++)
        {
            sum += (hexdata[i]&0xff);
        }
        byte s = (byte)sum;
        byte c = (byte)(0xff - s);
        c = (byte)(c+1);
        byte[] hexcrc = Hex.encode(crc);
        if(hexcrc.length == 1 && hexcrc[0] == c)
            return true;
        return false;
    }

    /**
     * parse data data of @FE010001002CD4 command
     */
    private void parseFirst(byte[] data) throws Exception
    {
        // Meter Type
        int off = 5;
        int len = 20;
        byte[] mthex = Hex.encode(new String(data,5,20));
        meterType = new String(mthex);

        /*
        // Meter Number
        off = 71;
        len = 8;
        mthex = EMUtil.encodeHex(String(data,off,len));
        mthex = EMUtil.reverse(mthex);
        meterNumber= Integer.toString(EMUtil.getIntToBytes(mthex));
        */
    }

    /**
     * get raw Data
     */
    public byte[] getRawData()
    {
        return this.rawData;
    }

    /**
     * parse data data of @FE9072 command
     */
    private void parseSecond(byte[] data) throws Exception
    {
        String hexstr = null;
        byte[] hexbts = null;

        int off = 13;
        int len = 8;
        hexbts = Hex.encode(new String(data,off,len));
        power = DataUtil.getIntToBytes(hexbts);

        off = 21;
        len = 8;
        hexbts = Hex.encode(new String(data,off,len));
        hour = DataUtil.getIntToBytes(hexbts);

        off = 29;
        len = 8;
        hexbts = Hex.encode(new String(data,off,len));
        resetCounter = new Double(DataUtil.getIntToBytes(hexbts)/100.0);

        off = 37;
        len = 8;
        hexbts = Hex.encode(new String(data,off,len));
        peakPower = DataUtil.getIntToBytes(hexbts);

        off = 45;
        len = 8;
        hexbts = Hex.encode(new String(data,off,len));
        meterNumber = Integer.toString(DataUtil.getIntToBytes(hexbts));

        off = 53;
        len = 8;
        hexbts = Hex.encode(new String(data,off,len));
        pulseInput = DataUtil.getIntToBytes(hexbts);

        off = 61;
        len = 8;
        hexbts = Hex.encode(new String(data,off,len));
        specialData = DataUtil.getIntToBytes(hexbts);
    }

    /**
     * parse data data of @FE95006D command
     */
    private void parseThird(byte[] data) throws Exception
    {
        byte[] hexbts = null;

        int off = 7;
        int len = 8;
        hexbts = Hex.encode(new String(data,off,len));
        energy = new Double(DataUtil.getIntToBytes(hexbts));

        off = 15;
        len = 8;
        hexbts = Hex.encode(new String(data,off,len));
        tariff1 = DataUtil.getIntToBytes(hexbts);

        off = 23;
        len = 8;
        hexbts = Hex.encode(new String(data,off,len));
        tariff2 = DataUtil.getIntToBytes(hexbts);
    }

    /**
     * parseing Energy Meter Data of Kamstrup 162 Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception
    {
        this.rawData = data;
        int tlen = data.length;
        if(tlen < (FIRSTDATA_LEN+SECONDDATA_LEN+THIRDDATA_LEN))
        {
            throw new Exception("Kamstrup162 data length invalid "
                    + "valid len["+(FIRSTDATA_LEN+SECONDDATA_LEN
                        +THIRDDATA_LEN)+"] data len["+tlen+"]");
        }

        byte[] firstdata = new byte[FIRSTDATA_LEN];
        byte[] seconddata = new byte[SECONDDATA_LEN];
        byte[] thirddata = new byte[THIRDDATA_LEN];
        int pos=0;
        int len=0;
        for(int i = 0 ; i < data.length ; i++)
        {
            if(data[i]==(byte)'\r')
            {
                len=(i - pos)+1;
                if(len == firstdata.length)
                    System.arraycopy(data,pos,firstdata,0,len);
                else if(len == seconddata.length)
                    System.arraycopy(data,pos,seconddata,0,len);
                else if(len == thirddata.length)
                    System.arraycopy(data,pos,thirddata,0,len);
                pos=i+1;
            }
        }

        /*
        byte[] firstdata = new byte[FIRSTDATA_LEN];
        System.arraycopy(data,0,firstdata,0,firstdata.length);
        byte[] seconddata = new byte[SECONDDATA_LEN];
        System.arraycopy(data,firstdata.length,seconddata,0,
                seconddata.length);
        byte[] thirddata = new byte[THIRDDATA_LEN];
        System.arraycopy(data,firstdata.length+seconddata.length,
                thirddata,0,thirddata.length);
        */

        /*
        if(!checkCRC(firstdata) || !checkCRC(seconddata)
                || !checkCRC(thirddata))
            throw new Exception("Kamstrup162 invalid CRC");
        */
        parseFirst(firstdata);
        parseSecond(seconddata);
        parseThird(thirddata);
        if(resetCounter.compareTo(energy) >= 0)
            this.meteringValue = resetCounter;
        else
            this.meteringValue = energy;
    }

    public int getLength()
    {
        return FIRSTDATA_LEN+ SECONDDATA_LEN+ THIRDDATA_LEN;
    }

    /**
     * get Metering Value
     */
    public Double getMeteringValue()
    {
        return this.meteringValue;
    }

    /**
     * get meter id
     * @return meter id
     */
    public String getMeterId()
    {
        return meterNumber;
    }

    /**
     * get Meter Type
     */
    public String getMeterType(){ return this.meterType;}
    /**
     * get Meter Number
     */
    public String getMeterNumber(){return this.meterNumber;}
    /**
     * get Power
     */
    public int getPower(){ return this.power; }
    /**
     * get Peak Power
     */
    public int getPeakPower(){ return this.peakPower; }
    /**
     * get Hour
     */
    public int getHour(){ return this.hour; }
    /**
     * get Reset Counter
     */
    public Double getResetCounter(){ return this.resetCounter; }
    /**
     * get pulse input
     */
    public int getPulseInput(){ return this.pulseInput; }
    /**
     * get special data
     */
    public int getSpecialData(){ return this.specialData; }
    /**
     * get energy value
     */
    public Double getEnergy(){ return this.energy; }
    /**
     * get tariff1 energy value
     */
    public int getTariff1(){ return this.tariff1; }
    /**
     * get tariff2 energy value
     */
    public int getTariff2(){ return this.tariff2; }

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

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("Kamstrup162 DATA[");
        sb.append("(meteringValue=").append(meteringValue).append("),");
        sb.append("(meterType=").append(meterType).append("),");
        sb.append("(meterNumber=").append(meterNumber).append("),");
        sb.append("(power=").append(power).append("),");
        sb.append("(peakPower=").append(peakPower).append("),");
        sb.append("(hour=").append(hour).append("),");
        sb.append("(resetCounter=").append(resetCounter).append("),");
        sb.append("(pulseInput=").append(pulseInput).append("),");
        sb.append("(specialData=").append(specialData).append("),");
        sb.append("(energy=").append(energy).append("),");
        sb.append("(tariff1=").append(tariff1).append("),");
        sb.append("(tariff2=").append(tariff2).append(')');
        sb.append("]\n");

        return sb.toString();
    }

    /**
     * get Data
     */
    @Override
    public LinkedHashMap<String, String> getData()
    {
        LinkedHashMap<String, String> res = new LinkedHashMap<String, String>(16,0.75f,false);
        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
        
        /*
        res.put("name","Kamstrup162");
        res.put("lp",""+lp);
        res.put("lpValue",""+lpValue);
        res.put("meterType",""+meterType);
        */
        res.put("Energy(kWh)",""+df3.format(energy));
        res.put("Reset Counter(kWh)",""+resetCounter);
        res.put("Tariff 1(kWh/T1)",""+tariff1);
        res.put("Peak Power (Wp)",""+peakPower);
        res.put("Tariff 2(kWh/T1)",""+tariff2);
        res.put("Meter number (NUM)",""+meterNumber);
        res.put("Power (W)",""+power);
        res.put("Pulse input","");
        res.put("Hours (HRS)",""+hour);
        res.put("Special data (SPC)",""+specialData);

        return res;
    }
}
