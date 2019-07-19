package ch.iec.tc57._2011.schema.message;

import java.util.ArrayList;
import java.util.List;


public enum ReadingType {
	
//	sag			(Group.A,	"",						"0.0.2.4.1.1.41.0.0.0.0.0.0.0.0.0.0.0"),	//
//	swell		(Group.A,	"",						"0.0.2.4.1.1.42.0.0.0.0.0.0.0.0.0.0.0"),	//
//	전압			(Group.A,	"voltage",				"0.0.2.4.1.1.54.0.0.0.0.0.0.0.0.0.29.0"),
//	전류			(Group.A,	"Ecurrent",				"0.0.2.4.1.1.4.0.0.0.0.0.0.0.0.0.5.0"),
//	위상각		(Group.A,	"",						"0.0.2.4.1.1.55.0.0.0.0.0.0.0.0.0.9.0"),	//
//	THD			(Group.A,	"",						"0.0.2.4.1.1.47.0.0.0.0.0.0.0.0.3.61.0"),	//
//	역률			(Group.A,	"factor",				"0.0.2.4.1.1.38.0.0.0.0.0.0.0.0.3.61.0"),	//
//	수전유효전력	(Group.A,	"RCeffectivePower",		"0.0.2.4.1.1.12.0.0.0.0.0.0.0.0.3.72.0"),
//	수전피상전력	(Group.A,	"RCapparentPower",		"0.0.2.4.1.1.12.0.0.0.0.0.0.0.0.3.61.0"),
//	수전진상무효	(Group.A,	"RCleadReactivePower",	"0.0.2.4.18.1.12.0.0.0.0.0.0.0.0.3.63.0"),
//	수전지상무효	(Group.A,	"RClaggReactivePower",	"0.0.2.4.15.1.12.0.0.0.0.0.0.0.0.3.63.0");	//
//	송전유효전력	(Group.A,	"TReffectivePower",		"0.0.2.4.19.1.12.0.0.0.0.0.0.0.0.3.72.0"),
//	송전피상전력	(Group.A,	"TRapparentPower",		"0.0.2.4.19.1.12.0.0.0.0.0.0.0.0.3.61.0"),
//	송전진상		(Group.A,	"TRleadReactivePower",	"0.0.2.4.16.1.12.0.0.0.0.0.0.0.0.3.63.0"),
//	송전지상		(Group.A,	"TRlaggReactivePower",	"0.0.2.4.17.1.12.0.0.0.0.0.0.0.0.3.63.0");	//
//  ----------------------------------------------------------------------------------------------
//	수전유효전력	(Group.A,	"RCeffectivePower",		"0.0.2.4.1.1.12.0.0.0.0.0.0.0.0.3.72.0"),
//	수전피상전력	(Group.A,	"RCapparentPower",		"0.0.2.4.1.1.12.0.0.0.0.0.0.0.0.3.61.0"),
//	수전진상무효	(Group.A,	"RCleadReactivePower",	"0.0.2.4.18.1.12.0.0.0.0.0.0.0.0.3.63.0"),
//	수전지상무효	(Group.A,	"RClaggReactivePower",	"0.0.2.4.15.1.12.0.0.0.0.0.0.0.0.3.63.0"),
	
//	최대부하전류	(Group.B,	"DATA_1",		"0.8.2.4.1.1.4.0.0.0.0.0.0.0.0.0.5.0"),
//	유효전력량	(Group.B,	"DATA_2",		"0.0.0.1.20.1.12.0.0.0.0.0.0.0.0.3.73.0"),
	
//	A상전압		(Group.C,	"DATA_1",		"0.0.0.6.0.1.54.0.0.0.0.0.0.0.128.0.29.0"),
//	B상전압		(Group.C,	"DATA_2",		"0.0.0.6.0.1.54.0.0.0.0.0.0.0.64.0.29.0"),
//	C상전압		(Group.C,	"DATA_3",		"0.0.0.6.0.1.54.0.0.0.0.0.0.0.32.0.29.0"),
//	A상전류		(Group.C,	"DATA_4",		"0.0.0.6.0.1.4.0.0.0.0.0.0.0.128.0.5.0"),
//	B상전류		(Group.C,	"DATA_5",		"0.0.0.6.0.1.4.0.0.0.0.0.0.0.64.0.5.0"),
//	C상전류		(Group.C,	"DATA_6",		"0.0.0.6.0.1.4.0.0.0.0.0.0.0.32.0.5.0"),
//	A상이용률	(Group.C,	"DATA_7",		"0.0.0.6.0.1.0.0.0.0.0.0.0.0.128.0.0.0"),
//	B상이용률	(Group.C,	"DATA_8",		"0.0.0.6.0.1.0.0.0.0.0.0.0.0.64.0.0.0"),
//	C상이용률	(Group.C,	"DATA_9",		"0.0.0.6.0.1.0.0.0.0.0.0.0.0.32.0.0.0");
	
	/**
	 * Group
	 * A:On-Demand, 구간계량
	 * B:계기부하측전압
	 * C:변압기 감시
	 */

	METR_KND						(Group.A,	"METR_KND",				"METR_KND"),
	MR_CYCL						(Group.A,	"MR_CYCL",				"MR_CYCL"),
	WHME_LLOAD_NDL		(Group.A,	"WHME_LLOAD_NDL",		"WHME_LLOAD_NDL"),
	WHME_MLOAD_NDL		(Group.A,	"WHME_MLOAD_NDL",		"WHME_MLOAD_NDL"),
	WHME_MAX_LOAD_NDL		(Group.A,	"WHME_MAX_LOAD_NDL",	"WHME_MAX_LOAD_NDL"),
	VAR_LLOAD_NDL		(Group.A,	"VAR_LLOAD_NDL",		"VAR_LLOAD_NDL"),
	VAR_MLOAD_NDL		(Group.A,	"VAR_MLOAD_NDL",		"VAR_MLOAD_NDL"),
	VAR_MAX_LOAD_NDL		(Group.A,	"VAR_MAX_LOAD_NDL",		"VAR_MAX_LOAD_NDL"),
	DM_MT_LLOAD_NDL	(Group.A,	"DM_MT_LLOAD_NDL",		"DM_MT_LLOAD_NDL"),
	DM_MT_MLOAD_NDL	(Group.A,	"DM_MT_MLOAD_NDL",		"DM_MT_MLOAD_NDL"),
	DM_MT_MAX_LOAD_NDL	(Group.A,	"DM_MT_MAX_LOAD_NDL",	"DM_MT_MAX_LOAD_NDL"),
	
	MAX_LOAD_CURR	(Group.B,	"MAX_LOAD_CURR",	"MAX_LOAD_CURR"),
	WHME	(Group.B,	"WHME",				"WHME"),
	
	PHA_GAUG_PTIME_AVG_VOLT		(Group.C,	"PHA_GAUG_PTIME_AVG_VOLT",		"PHA_GAUG_PTIME_AVG_VOLT"),
	PHB_GAUG_PTIME_AVG_VOLT		(Group.C,	"PHB_GAUG_PTIME_AVG_VOLT",		"PHB_GAUG_PTIME_AVG_VOLT"),
	PHC_GAUG_PTIME_AVG_VOLT		(Group.C,	"PHC_GAUG_PTIME_AVG_VOLT",		"PHC_GAUG_PTIME_AVG_VOLT"),
	PHA_GAUG_PTIME_AVG_CURR		(Group.C,	"PHA_GAUG_PTIME_AVG_CURR",		"PHA_GAUG_PTIME_AVG_CURR"),
	PHB_GAUG_PTIME_AVG_CURR		(Group.C,	"PHB_GAUG_PTIME_AVG_CURR",		"PHB_GAUG_PTIME_AVG_CURR"),
	PHC_GAUG_PTIME_AVG_CURR		(Group.C,	"PHC_GAUG_PTIME_AVG_CURR",		"PHC_GAUG_PTIME_AVG_CURR"),
	PHA_USE_RATIO	(Group.C,	"PHA_USE_RATIO",				"PHA_USE_RATIO"),
	PHB_USE_RATIO	(Group.C,	"PHB_USE_RATIO",				"PHB_USE_RATIO"),
	PHC_USE_RATIO	(Group.C,	"PHC_USE_RATIO",				"PHC_USE_RATIO"),
	
	WHME_LOAD_FAP		(Group.A,	"WHME_LOAD_FAP",		"WHME_LOAD_FAP"),
	VAR_LOAD_LARAP		(Group.A,	"VAR_LOAD_LARAP",		"VAR_LOAD_LARAP"),
	VAR_LOAD_LERAP		(Group.A,	"VAR_LOAD_LERAP",		"VAR_LOAD_LERAP"),
	LOAD_AP		(Group.A,	"LOAD_AP",		"LOAD_AP"),
	WHME_LOAD_BFAP		(Group.A,	"WHME_LOAD_BFAP",		"WHME_LOAD_BFAP"),
	VAR_LOAD_BLARAP		(Group.A,	"VAR_LOAD_BLARAP",		"VAR_LOAD_BLARAP"),
	VAR_LOAD_BLERAP		(Group.A,	"VAR_LOAD_BLERAP",		"VAR_LOAD_BLERAP"),
	LOAD_BAP		(Group.A,	"LOAD_BAP",		"LOAD_BAP");
	
	private String code;
	private String var; 
	private Group group;
	
	ReadingType(Group g,String var, String code) {
		this.group = g;
		this.var = var;
        this.code = code;
	}
	 
    public String getVar() {
		return var;
	}
    
	public String getCode() {
		return code;
	}
	
	public Group getGroup() {
		return group;
	}
	
	public static Enum<Verb> findEnum(String str){
    	Verb.valueOf(str);
    	return Verb.valueOf(str);
    }
	
	public static List<ReadingType> isInGroup(Group group) {
		List<ReadingType> list = new ArrayList<ReadingType>();
		
		for(ReadingType rt: ReadingType.values()){
			if(rt.getGroup().equals(group)) {
				list.add(rt);
			}
		}
        return list; 
    }
	
	public static ReadingType getReadingTypeByReadingTypeCode(String str){
		ReadingType val = null ;
		
		for(ReadingType re :  values()){
			if(re.getCode().equals(str)){
				val = re;
			}
		}
		return val;
	}
	
    public enum Group {
        A,
        B,
        C,
        D;
    }
}
