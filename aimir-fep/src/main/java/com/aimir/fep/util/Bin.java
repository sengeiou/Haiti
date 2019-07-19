package com.aimir.fep.util;

import java.util.ArrayList;

/**
 * bcd encode/decode
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class Bin
{

    public static void encodeLSBFirst(byte[] data,int[] bits)
    {
        int mod;
        int div;
        for(int i = 0 ; i < bits.length ; i++)
        {
            div = bits[i]/8;
            mod = bits[i]%8;
            if(div >= data.length)
                continue;
            data[div]|=((0x01)<<mod);
        }
    }

    public static void encodeMSBFirst(byte[] data,int[] bits)
    {
        int mod;
        int div;
        int len = data.length;
        for(int i = 0 ; i < bits.length ; i++)
        {
            div = bits[i]/8;
            mod = bits[i]%8;
            if(div >= data.length)
                continue;
            data[len-1-div]|=((0x01)<<mod);
        }
    }

    /**
     * decode
     *
     * @param b <code>byte</code> data
     * @param result <code>String</code> 
     */
    private static String decode(byte b)
    {
        StringBuffer sb = new StringBuffer();
        for(int i = 0 ; i < 8 ; i++)
        {
            sb.append((b>>(7-i))&0x01);
        }
        return sb.toString();
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
        for(int i = 0 ; i < data.length ; i++)
        {
            sb.append(decode(data[i]));
        }

        return sb.toString();
    }

    public static int[] getLSBFirst(byte[] data)
    {
        ArrayList<Integer> al = new ArrayList<Integer>();
        int ival = 0;
        for(int i = 0, j = 0 ; i < data.length ; i++)
        {
            byte b = data[i];
            for(int k = 0 ; k < 8 ; k++)
            {
                ival = ((b>>(7-k))&0x01);
                if(ival == 1)
                    al.add(new Integer(j));
                j++;
            }
        }
        Integer[] ivals = (Integer[])al.toArray(new Integer[0]);
        int[] res = new int[ivals.length];
        for(int i = 0 ; i < ivals.length ; i++)
        {
            res[i] = ivals[i].intValue();
        }

        return res;
    }

    public static int[] getMSBFirst(byte[] data)
    {
        ArrayList<Integer> al = new ArrayList<Integer>();
        int len = data.length;
        int ival = 0;
        for(int i = len -1, j = 0 ; i >=0 ; i--)
        {
            byte b = data[i];
            for(int k = 7 ; k >= 0 ; k--)
            {
                ival = ((b>>(7-k))&0x01);
                if(ival == 1)
                    al.add(new Integer(j));
                j++;
            }
        }
        Integer[] ivals = (Integer[])al.toArray(new Integer[0]);
        int[] res = new int[ivals.length];
        for(int i = 0 ; i < ivals.length ; i++)
        {
            res[i] = ivals[i].intValue();
        }

        return res;
    }

    /***
     * compress a string like "010110101010" into a byte[]
     *
     * @param str String encoding
     *
     * @return byte[] containing binary
     */
    public static byte[] binaryStringToBytes(String str) 
    { 
        if(str == null) 
        { 
            return null; 
        }
  
        int sz = str.length(); 
        if(sz % 4 != 0) 
        { 
            throw new IllegalArgumentException("String must be a factor of 4"); 
        } 
        byte[] ret = new byte[ sz/4 ]; 
        for( int i=0; i<sz; i+=4 ) 
        { 
            String b = str.substring(i, i+4); 
            ret[i/4] = binaryToByte(b); 
        } 
        return ret; 
    } 

    /***
     * show the binary for a byte array
     *
     * @param b byte[] to be encoded
     *
     * @return String encoding
     */
    public static String bytesToBinaryString(byte[] b) 
    { 
        if(b == null) 
        { 
            return null; 
        }
  
        StringBuffer buffer = new StringBuffer(); 
        for(int i=0; i<b.length; i++) 
        { 
            buffer.append(byteToBinary(b[i])); 
        } 
        return buffer.toString(); 
    }
  
    private static String byteToBinary(byte b) 
    { 
        switch(b) 
        { 
            case 0 : return  "0000"; 
            case 1 : return  "0001";
            case 2 : return  "0010";
            case 3 : return  "0011";
            case 4 : return  "0100";
            case 5 : return  "0101";
            case 6 : return  "0110";
            case 7 : return  "0111";
            case 8 : return  "1000";
            case 9 : return  "1001";
            case 10: return  "1010";
            case 11 : return "1011";
            case 12 : return "1100";
            case 13 : return "1101";
            case 14 : return "1110";
            case 15 : return "1111";
         }

         return "xxxx";
     }
 
     private static byte binaryToByte(String b) 
     { 
         if(b.equals("0000")) { 
             return 0; 
         } else if(b.equals("0001")) {
             return 1;
         } else if(b.equals("0010")) {
             return 2;
         } else if(b.equals("0011")) {
             return 3;
         } else if(b.equals("0100")) {
             return 4;
         } else if(b.equals("0101")) {
             return 5;
         } else if(b.equals("0110")) {
             return 6;
         } else if(b.equals("0111")) {
             return 7;
         } else if(b.equals("1000")) {
             return 8;
         } else if(b.equals("1001")) {
             return 9;
         } else if(b.equals("1010")) {
             return 10;
         } else if(b.equals("1011")) {
             return 11;
         } else if(b.equals("1100")) {
             return 12;
         } else if(b.equals("1101")) {
             return 13;
         } else if(b.equals("1110")) {
             return 14;
         } else if(b.equals("1111")) {
             return 15;
         } 

         // throw exception?
         return 0;
     }
}


