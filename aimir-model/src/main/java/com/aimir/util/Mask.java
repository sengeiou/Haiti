package com.aimir.util;

import java.util.Vector;

/**
 * Mask
 *
 * @author 2.x버전에서 가져옴
 */
public class Mask
{
    private int attr = 0x00;

    public Mask()
    {
    }

    public Mask(int attr)
    {
        this.attr = attr;
    }

    public void setBit(int b)
    {
        attr = (int) (attr | getSBit(b));
    }  

    public void setBitAll()
    {
        attr = 0xffffffff;
    }

    public void setBit(int[] b)
    {
        if (b != null)
        {
            for (int i = 0; i < b.length; i++)
            {
                setBit(b[i]);
            }
        }
    }  

    public void unsetBit(int b)
    {
        attr = (int) (attr & ~(getSBit(b)));
    }  

    public void unsetBitAll()
    {
        attr = 0x00;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        int t = attr;
        sb.append("============[ Bit 0 - 31 ]============\n");
        for (int i = 0; i < 32; i++)
        {
            t = (int) ((attr >> (i)) & 0x01);
            //sb.append("BIT[").append(i).append("]=").append(t).append(",");
            sb.append(t);
        }
        return sb.toString();
    }

    public int getMask()
    {
        return attr;
    }

    public int[] getMaskBits()
    {
        Vector<Integer> v = new Vector<Integer>();
        int t = attr;
        for (int i = 0; i < 32; i++)
        {
            t = (int) ((attr >> (i)) & 0x01);
            if (t == 1)
            {
                v.add(Integer.valueOf(i));
            }
        }
        int[] res = new int[v.size()];
        for (int i = 0; i < v.size(); i++)
        {
            res[i] = ((Integer) v.elementAt(i)).intValue();
        }
        return res;
    }

    private int getSBit(int i)
    {
        if (i < 0)
        {
            i = 0;
        }
        else if (i > 32)
        {
            i = 32;
        }
        return (int) (0x01 << (i));
    }
}
