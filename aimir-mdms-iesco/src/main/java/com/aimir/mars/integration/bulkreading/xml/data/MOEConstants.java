package com.aimir.mars.integration.bulkreading.xml.data;

public class MOEConstants {
	
	public static String getOBISCode(String deviceModelName, int channel) {
		return OBIS_LoadProfile.getCode(channel);
	}
    
    public enum OBIS_LoadProfile{   	
    	
    	CumulativeActiveEnergyImport("3#1.0.1.8.0.255#2"),
    	CumulativeActiveEnergyExport("3#1.0.2.8.0.255#2"),
    	IntervalActiveEnergyImport("3#1.0.1.29.0.255#2"),
    	IntervalActiveEnergyExport("3#1.0.2.29.0.255#2"),
    	AverageDemandActivePowerImport("5#1.0.1.4.0.255#3"),
    	AverageDemandActivePowerExport("5#1.0.2.4.0.255#3");
    	
    	//CumulativeActiveEnergy("3#1.0.1.8.0.255#2"),
    	//ActiveEnergy("3#1.0.15.9.0.255#2"),
    	//ReactiveEnergy("3#1.0.130.9.0.255#2");
    	//17	Cumulative active energy -import Wh	1.0.1.8.0.255
    	//21	Delta of Current billing Absolute active energy Wh	1.0.15.9.0.255
    	//22	Delta of Current billing Absolute reactive energy varh	1.0.130.9.0.255	proposed for billing
    	
    	
    	/*
    	ActivePowerPositive("3#1.0.1.7.0.255#2"),
    	ActivePowerNegative("3#1.0.2.7.0.255#2"),
    	ReactivePowerPositive("3#1.0.3.7.0.255#2"),
    	ReactivePowerNegative("3#1.0.4.7.0.255#2"),
    	ApparentPowerPositive("3#1.0.9.7.0.255#2"),
    	ApparentPowerNegative("3#1.0.10.7.0.255#2"),
    	VoltagePhase_A("3#1.0.32.7.0.255#2"),
    	VoltagePhase_B("3#1.0.52.7.0.255#2"),
    	VoltagePhase_C("3#1.0.72.7.0.255#2"),
    	CurrentPhase_A("3#1.0.31.7.0.255#2"),
    	CurrentPhase_B("3#1.0.51.7.0.255#2"),
    	CurrentPhase_C("3#1.0.71.7.0.255#2"),
    	PowerFactorPhase_A("3#1.0.33.7.0.255#2"),
    	PowerFactorPhase_B("3#1.0.53.7.0.255#2"),
    	PowerFactorPhase_C("3#1.0.73.7.0.255#2"),
    	ActiveEnergy("1.0.15.9.0.255"),//	Delta of Current billing Absolute active energy
    	ReactiveEnergy("1.0.130.9.0.255");//	Delta of Current billing Absolute reactive energy
        */

        private String code;
        
        OBIS_LoadProfile(String code) {
            this.code = code;
        }
        
        public String getCode() {
            return this.code;
        }
        
        public static String getCode(int idx){
        	
        	OBIS_LoadProfile[] profiles = OBIS_LoadProfile.values();
        	
        	if(idx < profiles.length){
        		return profiles[idx].getCode();
        	}else{
            	return null;
        	}
        }
    }
    
    public enum OBIS_DayProfile{   	
    	
    	TotalActiveEnergyPositive("3#1.0.1.8.0.255#2"),
    	Tariff1ActiveEnergyPositive("3#1.0.1.8.1.255#2"),
    	Tariff2ActiveEnergyPositive("3#1.0.1.8.2.255#2"),
    	TotalReactiveEnergyPositive("3#1.0.3.8.0.255#2"),
    	Tariff1ReactiveEnergyPositive("3#1.0.3.8.1.255#2"),
    	Tariff2ReactiveEnergyPositive("3#1.0.3.8.2.255#2");

    	private String code;
        
    	OBIS_DayProfile(String code) {
            this.code = code;
        }
        
        public String getCode() {
            return this.code;
        }
    }
    
    public enum OBIS_MonthProfile{   	
    	
    	TotalActiveEnergyPositive("3#1.0.1.8.0.255#2"),
    	Tariff1ActiveEnergyPositive("3#1.0.1.8.1.255#2"),
    	Tariff2ActiveEnergyPositive("3#1.0.1.8.2.255#2"),
    	TotalReactiveEnergyPositive("3#1.0.3.8.0.255#2"),
    	Tariff1ReactiveEnergyPositive("3#1.0.3.8.1.255#2"),
    	Tariff2ReactiveEnergyPositive("3#1.0.3.8.2.255#2"),	
    	TotalActiveMaximumDemandPositive("4#1.0.1.6.0.255#2"),
    	TotalActiveMaximumDemandPositiveTime("4#1.0.1.6.0.255#5");

    	private String code;
        
    	OBIS_MonthProfile(String code) {
            this.code = code;
        }
        
        public String getCode() {
            return this.code;
        }
    }
    
    public enum LPStatus{   	

    	Regular("1001"),
    	DST("1002"),
    	LowVoltage("1003"),
    	ReverseEnergyFlow("1004"),
    	NoReadOutage("2001"),
    	NoReadDisconnected("2002"),
    	Missing("3001"),
    	ClockError("3002"),
    	TimeResetOccurred("3003"),
    	ChecksumError("3004"),
    	DeviceFailure("3005"),
    	BadAMIData("3006"),
    	SystemEstimate("4001"),
    	OfficeEstimate("4002");

        private String code;
        
        LPStatus(String code) {
            this.code = code;
        }
        
        public String getCode() {
            return this.code;
        }
    }
    
    public enum MDMType {

		LP,
		Day,
		Month,
		Event;

	}
}
