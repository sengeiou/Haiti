package ch.iec.tc57._2011.schema.message;

public enum ReplyError {

	E00("0.0",		"2",Category.C0,	"No errors"),
	E01("0.1",		"2",Category.C0,	"Partial result (additional results conveyed in separate messages)"),
	E02("0.2",		"2",Category.C0,	"Partial result (no further results to follow)"),
	E03("0.3",		"2",Category.C0,	"Simple acknowledgment"),
	E15("1.5",		"3",Category.C1,	"Mandatory Header elements missing"),
	E16("1.6",		"3",Category.C1,	"Mandatory Request elements missing"),
	E17("1.7",		"3",Category.C1,	"Mandatory Payload elements missing"),
	E18("1.8",		"3",Category.C1,	"Format of request does not validate against schem"),
	E19("1.9",		"3",Category.C1,	"Unsupported message revision in Header"),
	E24("2.4",		"3",Category.C2,	"Invalid Meter(s)"),
	E25("2.5",		"3",Category.C2,	"Invalid Noun"),
	E26("2.6",		"3",Category.C2,	"Invalid ReadingType(s)"),
	E29("2.9",		"3",Category.C2,	"Invalid Verb"),
	E210("2.10",	"3",Category.C2,	"Unsupported ReadingType(s)"),
	E212("2.12",	"3",Category.C2,	"Invalid UsagePoint(s)"),
	E213("2.13",	"3",Category.C2,	"Meter / UsagePoint mismatch"),
	E214("2.14",	"3",Category.C2,	"Invalid Source"),
	E215("2.15",	"3",Category.C2,	"Invalid Request ID(s)"),
	E216("2.16",	"3",Category.C2,	"Invalid ServiceLocation(s)"),
	E217("2.17",	"3",Category.C2,	"Meter / ServiceLocation mismatch*"),
	E218("2.18",	"3",Category.C2,	"ComModule / Meter mismatch*"),
	E219("2.19",	"3",Category.C2,	"Invalid CustomerAccount(s)"),
	E220("2.20",	"3",Category.C2,	"Invalid ServiceSupplier(s)"),
	E221("2.21",	"3",Category.C2,	"CustomerAccount / ServiceSupplier mismatch"),
	E222("2.22",	"3",Category.C2,	"Invalid Customer(s)"),
	E223("2.23",	"3",Category.C2,	"Customer / CustomerAccount mismatch"),
	E224("2.24",	"3",Category.C2,	"Invalid CustomerAgreement(s)"),
	E225("2.25",	"3",Category.C2,	"CustomerAccount / CustomerAgreement mismatch"),
	E226("2.26",	"3",Category.C2,	"CustomerAgreement / UsagePoint mismatch"),
	E227("2.27",	"3",Category.C2,	"CustomerAccount / UsagePoint mismatch"),
	E228("2.28",	"3",Category.C2,	"ServiceSupplier / UsagePoint mismatch"),
	E229("2.29",	"3",Category.C2,	"Object relationship mismatch"),
	E230("2.30",	"3",Category.C2,	"Invalid ComModule(s)"),
	E231("2.31",	"3",Category.C2,	"Invalid ServiceCategory(ies)"),
	E232("2.32",	"3",Category.C2,	"Invalid UsagePointLocation(s)"),
	E233("2.33",	"3",Category.C2,	"Invalid PricingStructure(s)"),
	E31("3.1",		"3",Category.C3,	"Too many items in request"),
	E32("3.2",		"3",Category.C3,	"Too many pending requests"),
	E41("4.1",		"3",Category.C4,	"Request timed out"),
	E51("5.1",		"3",Category.C5,	"Unable to process the request - high system activity level"),
	E52("5.2",		"3",Category.C5,	"Unable to process request -transaction not attempted"),
	E53("5.3",		"3",Category.C5,	"Unable to process the request - transaction attempted and failed"),
	E54("5.4",		"3",Category.C5,	"Unable to process the request - multiple error types encountered"),
	E55("5.5",		"3",Category.C5,	"Some or all of the requested ReadingTypes are unavailable in MDMS"),
	E56("5.6",		"3",Category.C5,	"Some or all of the requested ReadingTypes are unavailable in AMI"),
	E57("5.7",		"3",Category.C5,	"Some or all of the requested data is unavailable"),
	E58("5.8",		"3",Category.C5,	"Unable to process the request ��mandatory field(s) missing"),
	E59("5.9",		"3",Category.C5,	"Transaction aborted to maintain transactional integrity"),
	E61("6.1",		"3",Category.C6,	"Request canceled per business rule"),
	E62("6.2",		"3",Category.C6,	"Request placed on hold per business rule"),
	E63("6.3",		"3",Category.C6,	"Request released from business rule hold"),
	E64("6.4",		"3",Category.C6,	"Request rescheduled per business rule"),
	E65("6.5",		"3",Category.C6,	"Request canceled by user"),
	E71("7.1",		"3",Category.C7,	"Temporary authentication failure"),
	E72("7.2",		"3",Category.C7,	"Authentication required"),
	E73("7.3",		"3",Category.C7,	"Authentication mechanism insufficient"),
	E74("7.4",		"3",Category.C7,	"Authentication failure"),
	E75("7.5",		"3",Category.C7,	"Action not authorized for user"),
	E76("7.6",		"3",Category.C7,	"Authentication mechanism requires encryption"),
	E77("7.7",		"3",Category.C7,	"Policy violation");
	
	private String code;
	private String group;
	private Category category;
	private String description;
	 
	ReplyError(String code,String group,Category category, String description) {
		this.code = code;
		this.group = group;
		this.category = category;
		this.description = description;
	}
    
	public String getDescription() {
    	return this.description;
    }
    
	public String getGroup() {
		return group;
	}
	
	public String getCode() {
		return code;
	}
	
	public Category getCategory() {
		return category;
	}
	
	public static Enum<ReplyError> findEnum(String str){
		ReplyError.valueOf(str);
    	return ReplyError.valueOf(str);
    }
	
	public static ReplyError findERR(String str){
		ReplyError val = null ;
		for(ReplyError re :  values()){
			if(re.getCode().equals(str)){
				val = re;
			}
		}
		return val;
	}
	
	public enum Category {                   
		C0("0",	"성공",				"OK" ),
		C1("1",	"잘못된메세지",		"Bad or Missing element" ),
		C2("2",	"인식불가",			"Parameter invalid" ),
		C3("3",	"너무 많은 결과값",	"Too many values" ),
		C4("4",	"응답없음",			"Request aborted" ),
		C5("5",	"프로세스에러",		"Application Error" ),
		C6("6",	"업무처리규칙 위반",	"Business rule violation" ),
		C7("7",	"보안문제발생",		"Security issue" );
	        
	   	private String category;
	   	private String error_kor;
	   	private String error;
	   	
	   	Category(String category, String error_kor, String error) {
	   		this.category = category;
	   		this.error_kor = error_kor;
	   		this.error = error;
	    }
		
	   	public String getCategory() {
			return category;
		}
	   	
	   	public String getError_kor() {
	   		return error_kor;
	   	}
		
	   	public String getError() {
			return error;
		}

	}
	
}