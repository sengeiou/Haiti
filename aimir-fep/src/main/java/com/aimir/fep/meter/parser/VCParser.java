package com.aimir.fep.meter.parser;

import java.io.Serializable;

public abstract class VCParser extends MeterDataParser implements Serializable
{
	private static final long serialVersionUID = -5651023418402226249L;
	public static final int VM_KIND_VCINFO = 1;
    public static final int VM_KIND_EVENTLOG = 2;
    public static final int VM_KIND_PARAMETERLOG =3;
    public static final int VM_KIND_DAYSTAT = 4;
    public static final int VM_KIND_DAYMAX = 5;
    public static final int VM_KIND_MONTHSTAT = 6;
    public static final int VM_KIND_MONTHMAX = 7;
    public static final String[] VM_KIND_NAME = { "", 
                                                  "Volume Information",
                                                  "Event Log",
                                                  "Parameter Log",
                                                  "Day Statistics",
                                                  "Day Maximum",
                                                  "Month Stat",
                                                  "Month Maximum" };
    public static final int[] VC_RESOLUTION = {0,5,15,30,60,1440};
    
    public abstract String getVcid();
    public abstract int getPortno();
    public abstract int getPeriod();
    public abstract int getMeterStatusCode();
}
