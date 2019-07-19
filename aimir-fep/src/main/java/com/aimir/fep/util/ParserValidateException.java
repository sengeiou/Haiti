package com.aimir.fep.util;

public class ParserValidateException extends Exception{

	private static final long serialVersionUID = 7525129259475763156L;
	
	private String failureMessage;
	private Exception e;
	 public ParserValidateException(String failureMessage, Exception e){
		 this.failureMessage = failureMessage;
		 this.e = e;
	 }
	 public String toErrorMessage(){
		return this.failureMessage + e.getMessage();
	 }
}
