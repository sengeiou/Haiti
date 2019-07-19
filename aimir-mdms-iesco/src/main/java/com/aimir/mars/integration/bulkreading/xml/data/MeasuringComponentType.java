//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.22 at 06:41:41 PM KST 
//


package com.aimir.mars.integration.bulkreading.xml.data;

public enum MeasuringComponentType {
	
	LoadProfile("LOADPROFILE"),
	DailyDataLoadProfile("DAILYLOADPROFILE"),
	BillingDataLoadProfile("BILLINGLOADPROFILE"),
	MeterEvent("EVENT");

    private final String value;

    MeasuringComponentType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MeasuringComponentType fromValue(String v) {
        for (MeasuringComponentType c: MeasuringComponentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
