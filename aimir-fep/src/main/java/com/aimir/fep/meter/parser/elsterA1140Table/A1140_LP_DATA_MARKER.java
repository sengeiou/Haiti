package com.aimir.fep.meter.parser.elsterA1140Table;

import java.util.HashMap;

public class A1140_LP_DATA_MARKER {

	private static final String[] CH = { "Import mW",  "Export mW",  "Q1 (mvar)",  "Q2 (mvar)",
                                         "Q3 (mvar)",  "Q4 (mvar)",  "mVA",        "Daylight Saving",
                                         "Cust Def 1", "Cust Def 2", "Cust Def 3", "External 1",
                                         "External 2", "External 3", "External 4", "Now Used(0)" };
	
	private String type;
	private String dateTime; // yyyymmddhhmmss
	private int channel;
	private int period;
	
	private HashMap<String, String> periodTable = new HashMap<String, String>();

	public A1140_LP_DATA_MARKER() {
		setPeriodTable();
	}

	public void setPeriodTable() {
		periodTable.put("00", "1");
		periodTable.put("01", "2");
		periodTable.put("02", "3");
		periodTable.put("03", "4");
		periodTable.put("04", "5");
		periodTable.put("05", "6");
		periodTable.put("06", "10");
		periodTable.put("07", "15");
		periodTable.put("08", "20");
		periodTable.put("09", "30");
		periodTable.put("0A", "60");
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}
	
	public HashMap getLpPeriodTable() {
		return periodTable;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
        
        try {
            sb.append("("    )
              .append("TYPE="     ).append(getType()).append("/")
              .append("DATETIME=" ).append(getDateTime()).append("/")
              .append("CHANNEL="  ).append(getChannel()).append("/")
              .append("PERIOD="   ).append(getPeriod())
              .append(")");
        } catch (Exception e) { }
        
        return sb.toString();
	}
}
