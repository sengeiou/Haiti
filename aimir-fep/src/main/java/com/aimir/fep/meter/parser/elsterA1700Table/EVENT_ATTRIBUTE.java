package com.aimir.fep.meter.parser.elsterA1700Table;

public class EVENT_ATTRIBUTE {

	public enum EVENTATTRIBUTE {
	    NOT_USED             ( 0, "Not Used"),
		POWER_UP             ( 1, "Power Up"),
		POWER_DOWN           ( 2, "Power Down"),
		CONFIG_CHANGE        ( 3, "Configuration Change"),
		TIME_CHANGE          ( 4, "Time Change"),
		DAYLIGHT_SAVING      ( 5, "Daylight Saving"),
		LP_CLEARED           ( 6, "Load Profile Cleared"),
	    FORCED_END_OF_DEMAND ( 7, "Forced End Of Demand"),
	    METER_RESET          ( 8, "Meter Reset"),
	    TIME_SYNC            ( 9, "Time Sync"),
	    DATA_CHANGE          (10, "Data Change"),
	    BATTERY_FAIL         (11, "Battery Fail"),
		PHASE_FAILURE        (12, "Phase Failure"),
		REVERSE_RUNNING      (13, "Reverse Running"),
		POWER_FAIL           (14, "Power Fail");

        private int code;
        private String name;

        EVENTATTRIBUTE(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }   
}
