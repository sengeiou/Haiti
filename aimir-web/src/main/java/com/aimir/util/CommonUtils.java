package com.aimir.util;

import com.aimir.model.system.Role;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

public class CommonUtils
{
	
	
	 /**
     * @desc 
     * @param supplierId
     * @return
     */
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
	
	/**
	 * @desc extjs 
	 * @param conditionMap
	 * @return
	 */
	public static Map<String, String> getFirstPageForExtjsGrid(Map<String, String> conditionMap)
	{
		// 페이징 처리를 위한 부분 추가.extjs 는 0부터 시작
		int temppage = Integer.parseInt(conditionMap.get("page"));

		temppage = temppage - 1;

		conditionMap.put("page", Integer.toString(temppage));
		
		return conditionMap;
	}
	
	
	
	public static void main (String[] args)
	{
		//String testDate= "12.9.11";
		String testDate= "2012/09/11";
		
		boolean boolContainString1= containString(testDate, "\\.");
		
		boolean boolContainString2= containString(testDate, "/");
		
		System.out.println(boolContainString1);
		
		System.out.println(boolContainString2);
		
		String dbDate ="";
		
		if ( boolContainString1 == true )
			dbDate= changeDbFormatDate(testDate);
		else if ( boolContainString2 == true)
			dbDate = changeDbFormatDate2(testDate);
		
		
		
	}

	static int supplierId2 = 0;

	public static int getSupplierId(int supplierId)
	{

		Map<String, Object> conditionMap = new HashMap<String, Object>();

		if (conditionMap.get("supplierId") == ""
				|| conditionMap.get("supplierId").toString().length() <= 0)
			supplierId2 = Integer.parseInt("22");// 누리텔레콤
		else
			supplierId2 = supplierId;

		return supplierId2;
	}

	/**
	 * @desc db query형식으로 날짜 변환
	 * 
	 * @param strDate
	 * @return
	 */
	public static String changeDbFormatDate(String strDate)
	{

		String[] dates= strDate.split("\\. ");
		
		String year = dates[0];
		String month =dates[1];
		String day = dates[2];
		

		// alert( day.toString().length );
		if (day.length() == 1)
		{
			day = "0" + day.toString();
		}

		if (month.length() == 1)
		{
			month = "0" + month.toString();
		}

		String strFormattedDate = "20"+ year.toString() + month.toString() + day.toString();

		return strFormattedDate;

	}
	
	public static String changeDbFormatDate2(String strDate)
	{

		String[] dates= strDate.split("/");
		
		String day = dates[0];
		String month =dates[1];
		String year = dates[2];
		

	

		String strFormattedDate = "20"+ year.toString() + month.toString() + day.toString();

		return strFormattedDate;

	}
	
	
	/**
	 * @desc Returns true if the string contains of "."
	 * @param str1
	 * @return
	 */
	public static boolean containString(String str1, String matchString)
	{
		boolean result =false;
		
		
		try
		{
			
			
			if (str1.matches(".*"+matchString +".*"))
			{
				System.out.format("매치되었습니다.%n");
				result= true;
			}
				
			else
			{
				System.out.format("그런 문자열이 없습니다.%n");
				result= false;
			}
	
		} catch (PatternSyntaxException e)
		{ // 정규식에 에러가 있다면
			System.err.println(e);
			System.exit(1);
		}
		
		return result;
	}
	
	/**
	 * @desc : datepicker ( View ) {01/01/12혹은 12.01.01형태} 에서 전달되온 날짜형태를 
	 *  	   db date query 형태로 변환 (20121010 형태) 
	 * @param date
	 * @return
	 */
	public static String convertToDbDate (String date)
	{
		boolean boolContainString1= containString(date, "\\.");
		boolean boolContainString2= containString(date, "/");
		
		
		
		String dbDate ="";
		
		//.을 포함하고 있는 날짜 형식이면
		if ( boolContainString1 == true )
		{
			dbDate= changeDbFormatDate(date);
		}
		// slash를 포함하고 있는 날짜 형식일 경우.
		else if ( boolContainString2 == true)
		{
			dbDate = changeDbFormatDate2(date);
		}
		
		return dbDate;
	}

	public static Map<String, Object> getAllAuthorityByRole(Role role) {
	    if (role == null) {
	        return null;
	    }

	    Map<String, Object> result = new HashMap<String, Object>();
	    String mtrAuthority = role.getMtrAuthority();
	    String systemAuthority = role.getSystemAuthority();
		Integer commands = role.getCommands().size();
	    result.put("vee", (mtrAuthority == null || mtrAuthority.equals("r")) ? false : true);              // VEE Edition
//	    result.put("ondemand", (mtrAuthority != null && mtrAuthority.equals("c")) ? true : false);         // OnDemand
	    result.put("cud", (systemAuthority == null || systemAuthority.equals("r")) ? false : true);        // All CUD
//	    result.put("command", (systemAuthority != null && systemAuthority.equals("c")) ? true : false);    // All Command
		// ondemand, command option was removed, a number of command will consider as permission
		result.put("command", (commands != null && commands.intValue() > 0 )? true : false);
		result.put("ondemand", (commands != null && commands.intValue() > 0 )? true : false);

	    return result;
	}
}
