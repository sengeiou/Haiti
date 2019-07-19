package com.aimir.fep.meter.parser.amuKepco_dlmsTable;

import java.util.Comparator;

import com.aimir.fep.meter.data.LPData;

/**
 * LP Comparator
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 4. 22. 오후 5:27:18$
 */
public class LPComparator {

	public static final Comparator TIMESTAMP_ORDER= new Comparator()
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


