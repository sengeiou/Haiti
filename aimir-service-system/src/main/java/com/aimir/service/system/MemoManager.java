package com.aimir.service.system;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import com.aimir.model.system.Memo;
@WebService(name="MemoService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface MemoManager {
	
	@WebMethod(operationName ="momosList")
	@WebResult(name="memosList")
	public List<Memo> getMemos(
			@WebParam(name ="userId")long userId);
	
	@WebMethod
	@WebResult(name="Count")
	public int getCount(
			@WebParam(name ="userId")long userId);
	
	@WebMethod
	public void add(
			@WebParam(name ="memo")Memo memo);
	
	@WebMethod
	public void delete(
			@WebParam(name ="Id")Integer Id);
	
	@WebMethod
	public void update(
			@WebParam(name ="memo")Memo memo);
	
	@WebMethod
	public void deleteAll(
			@WebParam(name ="userId")long userId);
	
	@WebMethod
	@WebResult(name="memosList")
	public List<Memo> searchMemos(
			@WebParam(name ="word")String word)throws UnsupportedEncodingException;
	
	@WebMethod(operationName ="memosListByIndex")
	@WebResult(name="memosList")
	public List<Memo> getMemos(
			@WebParam(name ="userId")long userId, 
			@WebParam(name ="startIndex")Integer startIndex, 
			@WebParam(name ="maxIndex")Integer maxIndex);
}