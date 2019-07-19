package ch.iec.tc57._2011.schema.message;

public enum Verb {
	
	create	("CREATED",""),
	change	("CHANGED",""),
	cancel	("CANCELED",""),
	close	("CLOSED",""),	
	delete	("DELETED",""),
	execute	("EXECUTED",""),
	get		("get",""),
	created	("",""),
	changed	("",""),
	canceled("",""),
	closed	("",""),
	deleted	("",""),
	executed("",""),
	reply	("REPLY","");
	
	private String description;
	private String returnType;
	 
	Verb(String returnType,String description) {
		this.returnType = returnType;
        this.description = description;
    }
	
    public String getDescription() {
        return this.description;
    }
    
    public String getReturnType() {
        return this.returnType;
    }
    
    public static Enum<Verb> findEnum(String str){
    	Verb.valueOf(str);
    	return Verb.valueOf(str);
    }

}
