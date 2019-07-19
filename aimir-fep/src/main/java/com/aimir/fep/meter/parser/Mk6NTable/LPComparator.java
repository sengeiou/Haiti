package com.aimir.fep.meter.parser.Mk6NTable;

import java.util.Comparator;

import com.aimir.fep.meter.data.LPData;


//public class MDComparator implements Comparator 
public class LPComparator  implements java.io.Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1531565919135285514L;
	public static final Comparator<Object> TIMESTAMP_ORDER= new Comparator<Object>()
    {
        public int compare(Object o1,Object o2)
        {
            LPData lp1 = (LPData)o1;
            LPData lp2 = (LPData)o2;

            String ts1 = lp1.getDatetime();
            String ts2 = lp2.getDatetime();

            return ts1.compareTo(ts2);
        }
        public boolean equals(Object objs)
        {
            return this.equals(objs);
        }
    };
}
