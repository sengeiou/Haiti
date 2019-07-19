package com.aimir.service.system;

import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.cxf.annotations.WSDLDocumentation;

import com.aimir.model.system.Notice;

@WSDLDocumentation("Notice Information Management Service")
@WebService(name="NoticeService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface NoticeManager {
	
	@WebMethod(operationName ="noticeList")
	@WebResult(name="noticeList")
	public List<Notice> getNotice();
	
	@WebMethod
	@WebResult(name="noticeList")
    public List<Notice> getNotices(
    		@WebParam(name ="page")int page, 
    		@WebParam(name ="count")int count);
	
	@WebMethod
	@WebResult(name="noticeList")
    public List<Notice> sortList(
    		@WebParam(name ="name")String name, 
    		@WebParam(name ="page")int page, 
    		@WebParam(name ="count")int count, 
    		@WebParam(name ="sortCheck")int sortCheck);
	
	@WebMethod
	@WebResult(name="count")
    public Long getCount();
	
	@WebMethod
	@WebResult(name="count1Map")
    public Map<String,String> getCount1();
	
	@WebMethod
	@WebResult(name="countMap")
	public Map<String,String> getSearchCount(
			@WebParam(name ="searchWord")String searchWord, 
			@WebParam(name ="searchDetail")String searchDetail, 
			@WebParam(name ="searchCategory")String searchCategory, 
			@WebParam(name ="startDate")String startDate, 
			@WebParam(name ="endDate")String endDate);
	
	@WebMethod(operationName ="noticeListById")
	@WebResult(name="noticeList")
    public Notice getNotice(
    		@WebParam(name ="id")Integer id);
	
	@WebMethod
    public void add(
    		@WebParam(name ="notice")Notice notice);
	
	@WebMethod
    public void update(
    		@WebParam(name ="notice")Notice notice);
	
	@WebMethod
    public void delete(
    		@WebParam(name ="supplierId")Integer supplierId);
	
	@WebMethod
	@WebResult(name="noticeList")
	public List<Notice> searchNotice(
			@WebParam(name ="searchWord")String searchWord, 
			@WebParam(name ="searchDetail")String searchDetail, 
			@WebParam(name ="searchCategory")String searchCategory, 
			@WebParam(name ="startDate")String startDate, 
			@WebParam(name ="endDate")String endDate, 
			@WebParam(name ="page")int page, 
			@WebParam(name ="count")int count) ;
	
	@WebMethod
	public void hitsPlus(
			@WebParam(name ="noticeId")Integer noticeId);
}
