package com.aimir.fep.meter.parser.elsterA1140Table;

public class EVENT_ATTRIBUTE {

	public enum EVENTATTRIBUTE {  
	    NOT_USED             		( 0, "Not Used"),
		POWER_UP             		( 1, "Power Up"),
		POWER_DOWN           		( 2, "Power Down"),
		CONFIG_CHANGE        		( 3, "Configuration Change"),
		TIME_CHANGE          		( 4, "Time Change"),
		DAYLIGHT_SAVING      		( 5, "Daylight Saving"),
		LP_CLEARED           		( 6, "Load Profile Cleared"),
	    METER_RESET          		( 7, "Meter Reset"),
	    TIME_SYNC            		( 8, "Time Sync"),
	    DATA_CHANGE          		( 9, "Data Change"),
	    BATTERY_FAIL         		(10, "Battery Fail"),
		PHASE_FAILURE        		(11, "Phase Failure"),
		PHASE_FAILURE2       		(12, "Phase Failure"),
		REVERSE_RUNNING      		(13, "Reverse Running"),
		POWER_FAIL           		(14, "Power Fail"),
		CT_RATIO_CHANGE      		(15, "CT ratio chage"),
		TERMINAL_COVER_OPEN  		(16, "Terminal Cover Open"),
		MAIN_COVER_OPEN      		(17, "Main Cover Open"),		
		TERMINAL_MAIN_COVER_OPEN	(18, "Terminal and Main Cover Open");

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
