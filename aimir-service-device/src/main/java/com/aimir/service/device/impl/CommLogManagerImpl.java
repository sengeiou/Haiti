package com.aimir.service.device.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.device.CommLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.CommLog;
import com.aimir.model.device.CommLogChartVO;
import com.aimir.model.system.Code;
import com.aimir.model.system.CodeVO;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.CommLogManager;
import com.aimir.util.DecimalUtil;
import com.aimir.util.ReflectionUtils;
import com.aimir.util.TimeLocaleUtil;


@Service(value = "commLogManager")
public class CommLogManagerImpl implements CommLogManager {

    protected static Log logger = LogFactory.getLog(CommLogManagerImpl.class);

	@Autowired
	public CommLogDao commLogDao;
	
	@Autowired
	public SupplierDao supplierDao;
	
	public List<CommLogChartVO> getReceivePieChartData(Map<String, String> conditionMap) {
		return commLogDao.getReceivePieChartData(conditionMap);
	}

	public List<Map<String, Object>> getReceivePieChart(Map<String, String> conditionMap) {
        return commLogDao.getReceivePieChart(conditionMap);
    }

	public List<CommLogChartVO> getBarChartData(Map<String, String> conditionMap) {
		return commLogDao.getBarChartData(conditionMap);
	}

	/**
	 * fetch bar chart for comm grid
	 */
    @SuppressWarnings({ "static-access", "unchecked", "rawtypes" })
	public List<Map<String, Object>> getBarChart(Map<String, String> conditionMap)
    {
    	
    	
        List<Map<String, Object>> list = commLogDao.getBarChart(conditionMap);
        
        int supplierId= 0;
        
        
    	
        supplierId= this.checkSupplierId((conditionMap.get("supplierId")));
	    
        
        
	    Supplier supplier = supplierDao.get(supplierId);
        //Supplier supplier = supplierDao.get(Integer.parseInt(conditionMap.get("supplierId")));
        
        
        String searchDateType = conditionMap.get("period");

        
       	Map<String, Object> map=new HashMap();
       	
       	String pastYymmdd= "";
        	
        for ( int i=0 ; i< list.size(); i++)
        {
        	map= (HashMap) list.get(i);
        	String presentYymmdd = (String)map.get("date");
        	
        
            
            if(DateType.HOURLY.getCode().equals(searchDateType)) 
            {
            	
            	//TimeLocaleUtil.getLocaleDateHour((String)map.get("date") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter())
            	
                //map.put("formatDate", TimeLocaleUtil.getLocaleDateHour((String)map.get("date") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            	
            	
            	String tempDate =(String)map.get("date");
            	
            	String tempTime = (String)map.get("date");
            	
            	
            	tempTime =tempTime.substring(8, tempTime.length());
            	
            	
            	//과거날짜와 현재날짜를 비교.
            	if ( i==0 || !presentYymmdd.substring(0,8).toString().equals( pastYymmdd) )
            	{
            		map.put("formatDate", TimeLocaleUtil.getLocaleDate((String)tempDate , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            	}
            	else
            	{
            		map.put("formatDate", TimeLocaleUtil.getLocaleHour((String)tempTime , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            	}

         
            } 
            else if(DateType.MONTHLY.getCode().equals(searchDateType)) 
            {
                map.put("formatDate", TimeLocaleUtil.getLocaleYearMonth((String)map.get("date") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            } else {
                map.put("formatDate", TimeLocaleUtil.getLocaleDate((String)map.get("date") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
            }
            
            //현재날짜를 과거날짜로 저장.
            //pastYymmdd = presentYymmdd.substring(0,8);
            
            
        	//월별 검색인 경우.
        	if (presentYymmdd.length() ==6)
        	{
        		pastYymmdd = presentYymmdd.substring(0,6);
        	}
        	else
        		pastYymmdd = presentYymmdd.substring(0,8);
        }
        
        
        
        return list;
        
    }
    
    
    
    public static int checkSupplierId(String supplierId)
    {
    	int supplierId2=0;
    	
    	
    	 if ( supplierId =="" || supplierId.toString().length() <=0 || supplierId== null ) 
    	 {
    		 //supplierId2= Integer.parseInt("22");//누리텔레콤
 	    	supplierId2= Integer.parseInt("1");//
    	 }
 	    else
 	    	supplierId2=  Integer.parseInt(supplierId);
    	 
    	 return supplierId2;
 	    
    }

    
    @Deprecated
	public List<CommLog> getCommLogGridData(Map<String, String> conditionMap) 
	{
	    List<CommLog> list = commLogDao.getCommLogGridData(conditionMap);

	    int supplierId= 0;
	    
    	
        supplierId= this.checkSupplierId((conditionMap.get("supplierId")));

	    Supplier supplier = supplierDao.get(supplierId);
	    

	    for (CommLog comm : list) 
	    {
	        comm.setStartDate(TimeLocaleUtil.getLocaleDate(comm.getStartDate(), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
	        comm.setStartTime(TimeLocaleUtil.getLocaleDate(comm.getStartTime(), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
	    }
	    return list;
	}
	
	
	
	
	/**
	 * 
	 * for extjs 
	 * 
	 */
	@SuppressWarnings("static-access")
	public List<CommLog> getCommLogGridData2(Map<String, String> conditionMap) 
	{
		
		
		//페이징 처리를 위한 부분 추가.extjs 는 0부터 시작
		int temppage = Integer.parseInt(conditionMap.get("page"));
		
		temppage= temppage-1;
		
		conditionMap.put("page", Integer.toString(temppage));
		
		//extjs
	    List<CommLog> list = commLogDao.getCommLogGridData(conditionMap);
	    
	    
	    int supplierId= 0;
        
    	
        supplierId= this.checkSupplierId((conditionMap.get("supplierId")));
	    
	    

	    Supplier supplier = supplierDao.get(supplierId);
	    DecimalFormat dfMd = DecimalUtil.getIntegerDecimalFormat(supplier.getMd());
	    int idx1 = 1; 
	    for (CommLog comm : list) 
	    {
	    	
    		comm.setStartDate(TimeLocaleUtil.getLocaleDate(comm.getStartDate(), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
	        comm.setStartTime(TimeLocaleUtil.getLocaleDate(comm.getStartTime(), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));

	        String receiverTypeCodeName = comm.getReceiverTypeCode() != null ? comm.getReceiverTypeCode().getDescr() : "";
	        String receiverId = comm.getReceiverId();
	       // String sender = dfCd.format(Double.parseDouble(comm.getSenderId()));
	        String result ="";
	        
	        
	        //숫자 포멧팅 처리
	  		
	        String strSendBytes = dfMd.format(Double.parseDouble(comm.getSendBytes().toString()));
	        String strReceiverBytes = dfMd.format(Double.parseDouble(comm.getRcvBytes().toString()));
	        String strTotalCommTime = dfMd.format(Double.parseDouble(comm.getTotalCommTime().toString()));
	        
	        
	        
	        //result코드를 메세지로 변환.
	        if ( comm.getCommResult() ==1)
	        	result = "Success";
	        else
	        	result = "Fail";
	        
	        
	        //return item.startDate + " " + item.startTime;
	        //로그 시각
	        String time = comm.getStartDate() + " " + comm.getStartTime();
	        
	        
	        
	        // 프로퍼티 형태에 맞게 다시 셋팅.
	        comm.setReceiver(receiverTypeCodeName+ '['+receiverId + ']' );
	        comm.setSender(comm.getSenderId() );
	        comm.setResult(result);
	        
	        //포멧팅 처리
	        comm.setStrSendBytes(strSendBytes);
	        comm.setStrReceiverBytes(strReceiverBytes);
	        
	        
	        comm.setTime(time);
	        comm.setStrTotalCommTime(strTotalCommTime);
	        
	        
	        //데이터 타입
	        // String dataType= comm.getSvcTypeCode().getName();
	        
	        
	        
	         String curPage = conditionMap.get("page");
	         String pageSize = conditionMap.get("pageSize");
	         
	        //페이지에 따른 페이지 인덱스 설정.
	        comm.setIdx1(dfMd.format(this.makeIdxPerPage(curPage, pageSize, idx1)));
	        
	        
	        
	        idx1++;
	        
	        
	    }
	    return list;
	}
	
	
	
	
	@SuppressWarnings({ "static-access", "rawtypes", "unchecked", "unused" })
	public List<CodeVO> getPacketType() 
	{
	

		List<Code> list = commLogDao.getPacketType();
	    
	    List<CodeVO> list2=new ArrayList();
	    
	    for ( int i=0 ; i< list.size(); i++)
	    {
	    	CodeVO codevo =new CodeVO();
	    	
	    	Code code = list.get(i);
	    	
	    	String name = code.getDescr();
	    	
	    	//String shortName = name.substring(1, 2);
	    	String shortName = code.getName().substring(1, 2);
	    	
	    	codevo.setName(name);
	    	
	    	codevo.setShortName(shortName);
	    	
	    	
	    	list2.add(codevo);
	    	
	    	
	    	
	    }
	    
	  	 
	    return list2;
	}
	
	@SuppressWarnings({ "static-access", "rawtypes", "unchecked", "unused" })
	public List<CodeVO> getSenderType() 
	{
	

		List<Code> list = commLogDao.getSenderType();
	    
	    List<CodeVO> list2=new ArrayList();
	    
	    for ( int i=0 ; i< list.size(); i++)
	    {
	    	CodeVO codevo =new CodeVO();
	    	
	    	Code code = list.get(i);
	    	
	    	Integer codeid = code.getId();
	    	
	    	if(codeid.equals(376) || codeid.equals(377) || codeid.equals(364)){
	    	
		    	String name = code.getName();	    		    	
		    	codevo.setId(codeid);
		    	codevo.setName(name);	    		    		    	
		    	
		    	list2.add(codevo);
	    	}	    		    	
	    }
	    
	  	 
	    return list2;
	}
	
	
	/**
	 * @desc : 페이지 따른 idx 만들어 주는 메소드
	 * @param curPage
	 * @param pageSize
	 * @param idx1
	 * @return
	 */
	public int makeIdxPerPage(String curPage, String pageSize, int idx1)
	{
		
		return (Integer.parseInt(curPage) * Integer.parseInt(pageSize)) + idx1;
		 
	}
	

	public List<Map<String, String>> getLocationLineChartData(Map<String, String> conditionMap) {
		return commLogDao.getLocationLineChartData(conditionMap);
	}

	public List<Map<String, String>> getMcuLineChartData(Map<String, String> conditionMap) {
		return commLogDao.getMcuLineChartData(conditionMap);
	}

	public List<CommLogChartVO> getLocationPieChartData(Map<String, String> conditionMap) {
		return commLogDao.getLocationPieChartData(conditionMap);
	}

	public List<CommLogChartVO> getMcuPieChartData(Map<String, String> conditionMap) {
		return commLogDao.getMcuPieChartData(conditionMap);
	}

	public List<CommLogChartVO> getPieChartData(Map<String, String> conditionMap) {
		return commLogDao.getPieChartData(conditionMap);
	}

	/*
	public List<CommLogChartVO> getSendRevceiveChartData(String suppliedId) {
		return commLogDao.getSendRevceiveChartData(suppliedId);
	}
	*/

	/**
	 * method name : getSendRevceiveChartData<b/>
	 * method Desc : method 단어 오타 수정. 호환성을 위해 기존 method 명 남겨둠.
	 *
	 * @param supplierId
	 * @return
	 */
	@Deprecated
	public List<Map<String,Object>> getSendRevceiveChartData(String supplierId) {
	    return getSendReceiveChartData(supplierId);
	}

	public List<Map<String,Object>> getSendReceiveChartData(String supplierId) {
		List<Map<String,Object>> list = ReflectionUtils.getDefineListToMapList(commLogDao.getSendReceiveChartData(supplierId));
		
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		
		for(Map<String, Object> data: list) {
			data.put("date", TimeLocaleUtil.getLocaleDate((String)data.get("date") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
		}

		return list;
	}

	/*
	public List<CommLogChartVO> getSVCTypeChartData() {
		return commLogDao.getSVCTypeChartData();
	}
	*/

	public List<Map<String,Object>> getSVCTypeChartData(String supplierId) 
	{
        List<Map<String,Object>> list = ReflectionUtils.getDefineListToMapList(commLogDao.getSVCTypeChartData(supplierId));
		
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		
		for(Map<String, Object> data: list) 
		{
			data.put("date", TimeLocaleUtil.getLocaleDate((String)data.get("date") , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
		}

		return list;
	}

	public List<Map<String, String>> getLocationChartData() {
		return commLogDao.getLocationChartData();
	}

	@SuppressWarnings("static-access")
	public Map<String, String> getCommLogStatisticsData(Map<String, String> conditionMap)
	{
		
		
		
		 Map<String, String> commlogstatisticsdata = null;
		 
		 commlogstatisticsdata = commLogDao.getCommLogStatisticsData(conditionMap);
		 
		    
		 int supplierId= 0;
	    	
    	
        supplierId= this.checkSupplierId((conditionMap.get("supplierId")));
		    
		 /**
		  * 
		  * 서플라이어 id 에 따른 숫자 포멧팅 처리 추가.
		  * 
		  */
		 DecimalFormat dfMd = DecimalUtil.getDecimalFormat(supplierDao.get(supplierId).getMd());
  		 DecimalFormat dfCd = DecimalUtil.getDecimalFormat(supplierDao.get(supplierId).getCd());
  		 
  		 
  		 //{avgReceiver=919.439393939394, avgSender=29.0, totalReceiver=60683, totalSender=1914}
  		 
  		String avgReceiver= commlogstatisticsdata.get("avgReceiver");
  		String avgSender= commlogstatisticsdata.get("avgSender");
  		String totalReceiver= commlogstatisticsdata.get("totalReceiver");
  		String totalSender= commlogstatisticsdata.get("totalSender");
  		 
  		//data.put("total", dfMd.format(Double.parseDouble(total.toString())));
  		commlogstatisticsdata.put("avgReceiver", dfCd.format(Double.parseDouble(avgReceiver.toString())));
  		commlogstatisticsdata.put("avgSender", dfCd.format(Double.parseDouble(avgSender.toString())));
  		commlogstatisticsdata.put("totalReceiver", dfCd.format(Double.parseDouble(totalReceiver.toString())));
  		commlogstatisticsdata.put("totalSender", dfCd.format(Double.parseDouble(totalSender.toString())));
		 
		
		return commlogstatisticsdata;
	}

	public String getCommLogGridDataCount(Map<String, String> conditionMap) {
		return commLogDao.getCommLogGridDataCount(conditionMap);
	}	
	
	@SuppressWarnings("static-access")
	public List<CommLog> getCommLogGridDataForExcel(Map<String, String> conditionMap) 
	{
	    List<CommLog> list = commLogDao.getCommLogGridDataForExcel(conditionMap);

	    int supplierId= 0;
	    
    	
        supplierId= this.checkSupplierId((conditionMap.get("supplierId")));
	    
	    

	    Supplier supplier = supplierDao.get(supplierId);
	    

	    for (CommLog comm : list) 
	    {
	        comm.setStartDate(TimeLocaleUtil.getLocaleDate(comm.getStartDate(), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
	        comm.setStartTime(TimeLocaleUtil.getLocaleDate(comm.getStartTime(), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
	    }
	    return list;
	}
	
	
	
	//커뮤니케이션 로그 그리드를 위한.
    //리시버 디스플레이 해주는 메소드
   /* private String displayReceiver(List arrList)
    {

    	var nullYn : Number = item.receiverTypeCode;
    	
    	if(nullYn == 0)  
			return '[' + item.receiverId + ']';
		else 
			return item.receiverTypeCode.name + '[' + item.receiverId + ']';
		
    }	*/
	
}
