package com.aimir.mars.integration.multispeak.util;

public class MultiSpeakConstants {
 
    
    public enum GridLocation {
    	Indoor("Indoor"),
        Outdoor("Outdoor"),
        Substation("Substation"),
        Transformer("Transformer"),
        Pole("Pole");
    	
		private String value;

		GridLocation(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
		
        public static GridLocation getGridLocation(String gl) {
            for (GridLocation type : GridLocation.values()) {
                if (type.getValue().equals(gl))
                    return type;
            }

            return null;
        }
    }
	
	
	public enum ErrorType {
		NoError(0),
		Error(-1);
		
		private Integer value;
		
		ErrorType(int value){
			this.value = value;
		}
		
		public Integer getValue() {
			return value;
		}
        
        public int getIntValue() {
            return this.value.intValue();
        }
        
        public static ErrorType getErrorType(int code) {
            for (ErrorType type : ErrorType.values()) {
                if (type.getIntValue() == code)
                    return type;
            }

            return ErrorType.NoError;
        }
	}


	public enum AntennaType {
		DEFAULT_RF_ANTENNA("Default RF Antenna"),
		EXTENSION_RF_ANTENNA("Extension RF Antenna"),
		DEFAULT_MBB_ANTENNA("Default MBB Antenna"),
		EXTENSION_MBB_ANTENNA("Extension MBB Antenna");
		
		private String value;

		AntennaType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
		
        public static AntennaType getAntennaType(String searchType) {
            for (AntennaType type : AntennaType.values()) {
                if (type.getValue().equals(searchType))
                    return type;
            }
			return null;
        }
	}
	
	
	public enum Category {
		Add("Add"),
		Remove("Remove"),
		Deactivation("Deactivation");
		
		private String value;

		Category(String value) {
			this.value = value;
		}

		public String getName() {
			return value;
		}
		
        public static Category getCategory(String searchType) {
            for (Category type : Category.values()) {
                if (type.getName().equals(searchType))
                    return type;
            }
			return null;
        }
	}
	
	
	public enum ValidationError{
		
		INVALID_PARAMETER("Invalid Parameter"),
		DCU_ALREADY_EXIST("The DCU already exist"),
		METER_ALREADY_EXIST("The Meter already exists"),
		ALREADY_REMOVED("Already removed"),
		ALREADY_DEACTIVATED("Already deactivated"),
        UNREGISTERED_METER("The requested meter does not exist"),
        NO_DATA("Meter does not have requested data"),
        NOT_SUPPORT("Not support"),
        COMMUNICATION_FAILURE("The meter could not be reached"),
		SYSTEM_ERROR("System Error");
		
		private String value;
		
		public String getName() {
			return value;
		}
		
		ValidationError(String value) {
			this.value = value;
		}
		
        public static ValidationError getValidationError(String searchType) {
            for (ValidationError type: ValidationError.values()) {
                if (type.getName().equals(searchType))
                    return type;
            }
			return null;
        }
		

	}
}
