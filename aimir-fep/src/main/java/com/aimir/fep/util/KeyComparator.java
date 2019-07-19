package com.aimir.fep.util;

import java.util.Comparator;

/**
 * Key Comparator
 * 
 * @author Y.S Kim (sorimo@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class KeyComparator implements Comparator<Object> {

    public int compare(Object o1,Object o2) {
        String s1 = (String) ((MIBNode) o1).getOid().getValue();
        String s2 = (String) ((MIBNode) o2).getOid().getValue();

        //return s1.compareTo(s2);

        String[] ss1 = s1.split("[\\.]");
        String[] ss2 = s2.split("[\\.]");

        int i1 = Integer.parseInt(ss1[0]);
        int i2 = Integer.parseInt(ss2[0]);
        if(i1 != i2)
            return i1 - i2;

        i1 = Integer.parseInt(ss1[1]);
        i2 = Integer.parseInt(ss2[1]);
        if(i1 != i2)
            return i1 - i2;

        i1 = Integer.parseInt(ss1[1]);
        i2 = Integer.parseInt(ss2[1]);

        return i1 - i2;

    }

    /**
    *  interface implementation
    */
    public boolean equals(Object objs) {
        return this.equals(objs);
    }
}
