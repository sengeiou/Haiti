package com.aimir.fep.util;

import com.aimir.util.TimeUtil;

/**
 * bcd encode/decode
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class Bcd
{
    /**
     * encode
     *
     * @param data <code>String</code> data
     * @return result <code>byte[]</code>
     */
    public static byte[] encode(String data)
    {
        byte[] out = new byte[data.length() >> 1];
        int f = 0;
        for(int i = 0 ; i < out.length ; i++)
        {
            f = Integer.parseInt(data.substring(i*2,(i*2)+2));
            out[i]=(byte)(f & 0xff);
        }
        return out;
    }

    /**
     * decode
     *
     * @param data <code>byte[]</code> data
     * @param result <code>String</code> 
     */
    public static String decode(byte[] data)
    {
        StringBuffer sb = new StringBuffer();
        int f = 0;
        for(int i = 0 ; i < data.length ; i++)
        {
            f = (data[i] & 0xff);
            sb.append(TimeUtil.to2Digit(f));
        }

        return sb.toString();
    }

    /**
     * encode
     *
     * @param data <code>String</code> data
     * @return result <code>byte[]</code>
     */
    public static byte[] encodeTime(String data)
    {
        byte[] out = new byte[data.length() >> 1];
        int f = 0;
        for(int i = 0 ; i < out.length ; i++)
        {
            if(i == 0)
            {
                f = Integer.parseInt(data.substring(i*2,(i*2)+4));
                byte[] bx = DataUtil.get2ByteToInt(f);
                DataUtil.convertEndian(bx);
                out[i++]=bx[0];
                out[i] = bx[1];
            }
            else
            {
                f = Integer.parseInt(data.substring(i*2,(i*2)+2));
                out[i]=(byte)(f & 0xff);
            }
        }
        return out;
    }

    /**
     * encode
     *
     * @param data <code>String</code> data
     * @return result <code>byte[]</code>
     */
    public static byte[] encodeTimeNonOrdering(String data)
    {
        byte[] out = new byte[data.length() >> 1];
        int f = 0;
        for(int i = 0 ; i < out.length ; i++)
        {
            if(i == 0)
            {
                f = Integer.parseInt(data.substring(i*2,(i*2)+4));
                byte[] bx = DataUtil.get2ByteToInt(f);
                out[i++]=bx[0];
                out[i] = bx[1];
            }
            else
            {
                f = Integer.parseInt(data.substring(i*2,(i*2)+2));
                out[i]=(byte)(f & 0xff);
            }
        }
        return out;
    }

    /**
     * decode
     *
     * @param data <code>byte[]</code> data
     * @param result <code>String</code> 
     */
    public static String decodeTime(byte[] data)
    {
        StringBuffer sb = new StringBuffer();
        int f = 0;
        for(int i = 0 ; i < data.length ; i++)
        {
            if(i == 0)
            {
                byte[] bx = new byte[2]; 
                bx[0]=data[i++]; 
                bx[1]=data[i];
                DataUtil.convertEndian(bx);
                f = DataUtil.getIntTo2Byte(bx);
                sb.append(TimeUtil.to4Digit(f));
            }
            else
            {
                f = (data[i] & 0xff);
                sb.append(TimeUtil.to2Digit(f));
            }
        }

        return sb.toString();
    }

    /**
     * decode
     *
     * @param data <code>byte[]</code> data
     * @param result <code>String</code> 
     */
    public static String decodeTimeNonOrdering(byte[] data)
    {
        StringBuffer sb = new StringBuffer();
        int f = 0;
        for(int i = 0 ; i < data.length && i < 7; i++)
        {
            if(i == 0)
            {
                byte[] bx = new byte[2]; 
                bx[0]=data[i++]; 
                bx[1]=data[i];
                f = DataUtil.getIntTo2Byte(bx);
                sb.append(TimeUtil.to4Digit(f));
            }
            else
            {
                f = (data[i] & 0xff);
                sb.append(TimeUtil.to2Digit(f));
            }
        }

        return sb.toString();
    }

    /**
     * decode
     *
     * @param data <code>byte[]</code> data
     * @param result <code>String</code> 
     */
    public static String decodeTimeNonOrdering(byte[] data,int pos)
    {
        StringBuffer sb = new StringBuffer();
        int f = 0;
        for(int i = pos ; i < data.length && i < (7+pos); i++)
        {
            if(i == pos)
            {
                byte[] bx = new byte[2]; 
                bx[0]=data[i++]; 
                bx[1]=data[i];
                f = DataUtil.getIntTo2Byte(bx);
                sb.append(TimeUtil.to4Digit(f));
            }
            else
            {
                f = (data[i] & 0xff);
                sb.append(TimeUtil.to2Digit(f));
            }
        }

        return sb.toString();
    }
}
