/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmWareMakeInnerHtml.java
 * 작성일자/작성자 : 2010.12.09 최창희
 * @see 
 * 
 * 펌웨어 관리자 페이지 에서 json을 이용한 InnerHtml을 많이 사용하는 관계로 
 * html에 들어갈 String을 따로 분리 하였음.
 * 
 * ============================================================================
 * 수정 내역
 * NO  수정일자   수정자   수정내역
 * 
 * ============================================================================
 */
package com.aimir.bo.device.firmware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.service.device.FirmWareManager;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;


public class FirmWareMakeInnerHtml {

	private static Log log = LogFactory.getLog(FirmWareMakeInnerHtml.class);
	
    @Autowired
    FirmWareManager firmWareManager;
    
    
    public static final String triggerListStep1Div =    "" +
													    " <div class=\"mgn_t20\"><em class=\"blu12_bold\">Total.</em>0 / 0 Page </div>     "+
														 " <table class=\"fw_table\">                                                       "+
														 " 	<tr>                                                                            "+
														 " 		<th rowspan=\"2\">No</th>                                                     "+
														 " 		<th rowspan=\"2\">Trigger ID</th>                                             "+
														 " 		<th rowspan=\"2\">Equip Kind</th>                                             "+
														 " 		<th rowspan=\"2\">Equip Type</th>                                             "+
														 " 		<th colspan=\"3\">Source Version</th>                                         "+
														 " 		<th colspan=\"3\">Target Version</th>                                         "+
														 " 		<th  colspan=\"5\">State</th>                                                 "+
														 " 	</tr>                                                                           "+
														 " 	<tr>                                                                            "+
														 " 		<td class=\"subtit\">H/W Version</td>                                         "+
														 " 		<td class=\"subtit\">F/W Version</td>                                         "+
														 " 		<td class=\"subtit\">F/W Build</td>                                           "+
														 " 		<td class=\"subtit\">H/W Version</td>                                         "+
														 " 		<td class=\"subtit\">F/W Version</td>                                         "+
														 " 		<td class=\"subtit\">F/W Build</td>                                           "+
														 " 		<td class=\"subtit\">Total</td>                                               "+
														 " 		<td class=\"subtit\">Success</td>                                             "+
														 " 		<td class=\"subtit\">Executing</td>                                          "+
														 " 		<td class=\"subtit\">Cancel</td>                                              "+
														 " 		<td class=\"subtit\">Error</td>                                               "+
														 " 	</tr>                                                                           "+
														 " 	<tr>                                                                            "+
														 " 		<td colspan=\"15\">Data Does Not Exist</td> 			                            "+
														 " 	</tr>                                                                           "+
														 " </table>                                                                         ";
    
    public static final String triggerListStep2Div =    "" +
													    " <table class=\"fw_basic_gry\">            "+
													    " <colgroup>                              "+
													    "     <col width=\"15%\"/>                  "+
													    "     <col width=\"42%\"/>                  "+
													    "     <col width=\"15%\"/>                  "+
													    "     <col width=\"\"/>                     "+
													    "     </colgroup>                         "+
													    " <tr>                                    "+
													    
													    " <tr>"+
													    " <th>Trigger ID / MCU ID</th>"+
													    " <td></td>"+
													    " <th>State / Operation</th>"+
													    " <td>        "+
													    " <select name=\"select5\" id=\"select5\" >"+
													    " <option selected=\"selected\">All</option>"+
													    " </select>"+
													    " / "+
													    " <em class=\"fw_btn_org\"><a href=\"#\">Retry</a></em>"+
													    " </td>"+
													    " </tr>"+
													    " <tr>"+
													    " <th>Start Time / End Time</th>"+
													    " <td></td>"+
													    " <th>MCU Type / Location</th>"+
													    " <td></td>"+
													    " </tr>"+
													    " <tr>"+
													    " <th>Source Firmware</th>"+
													    " <td></td>"+
													    " <th>Target Firmware</th>"+
													    " <td></td>"+
													    " </tr>"+
													    " <tr>"+
													    " <th>Source HW/FW/Build Ver</th>"+
													    " <td></td>"+
													    " <th>Target HW/FW/Build Ver</th>"+
													    " <td></td>"+
													    " </tr>                                   "+
													    " </table>								  ";
    
    public static final String triggerListStep3Div =    "" +
														" <table   class=\"fw_table\">	"+
														" 	<colgroup>                  "+
														" 		<col width=\"50\" />      "+
														" 		<col width=\"25%\" />     "+
														" 		<col width=\"\" />        "+
														" 		<col width=\"\" />        "+
														" 		<col width=\"\" />        "+
														" 		<col width=\"\" />        "+
														" 		<col width=\"\" />        "+
														" 	</colgroup>                 "+
														" 	<tr>                        "+
														" 		<th>No</th>               "+
														" 		<th>ID</th>               "+
														" 		<th>State</th>            "+
														" 		<th>Trigger Step</th>     "+
														" 		<th>Trigger Count</th>    "+
														" 		<th>Error</th>            "+
														" 		<th>Retry</th>            "+
														" 	</tr>                       "+
														" 	<tr>                        "+
														" 		<td colspan=\"7\">Data Does Not Exist</td>   "+
														" 	</tr> "+
														" </table> ";   

    public String cmdAuth = null;

    public FirmWareMakeInnerHtml() {
        super();
    }

    public FirmWareMakeInnerHtml(String cmdAuth) {
        super();
        this.cmdAuth = cmdAuth;
    }

    /**
    *
    * @param       : List
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : FirmWareMgmMaxController에서 호출 (배포 >>펌웨어리스트)
    * @return : DivinnerHtml StringBuffer
    */
	@SuppressWarnings("unused")
    public StringBuffer getFirmwareList(List<Object> firmwareList,String equip_type){
		
		StringBuffer firmwareListDiv = new StringBuffer();
		firmwareListDiv.append("<table class=\"fw_table\" id=\"frmlistpagingTable\">  "); 
	    firmwareListDiv.append(" <colgroup>  ");
	    firmwareListDiv.append(" <col width=\"22\"/> ");
	    firmwareListDiv.append(" <col width=\"5%\"/> ");
	    firmwareListDiv.append(" <col width=\"6%\"/> ");
	    firmwareListDiv.append(" <col width=\"6%\"/>  ");
	    firmwareListDiv.append(" <col width=\"12%\"/>  ");
	    firmwareListDiv.append(" <col width=\"12%\"/>  ");
	    //firmwareListDiv.append(" <col width=\"10%\"/>  ");
	    firmwareListDiv.append(" <col width=\"8%\"/> ");
	    firmwareListDiv.append(" <col width=\"8%\"/> ");
	    firmwareListDiv.append(" <col width=\"8%\"/> ");
	    firmwareListDiv.append(" <col width=\"8%\"/> ");
	    firmwareListDiv.append(" <col width=\"8%\"/> ");
	    firmwareListDiv.append(" <col width=\"8%\"/> ");
	    firmwareListDiv.append(" <col width=\"8%\"/> ");
	    firmwareListDiv.append(" <col width=\"\"/> ");
	    firmwareListDiv.append(" </colgroup> ");
	    firmwareListDiv.append(" <tr>  ");
	    firmwareListDiv.append("  <th></th>  ");
	    firmwareListDiv.append("  <th>H/W</th> ");
	    firmwareListDiv.append("  <th>FW Ver</th>  ");
	    firmwareListDiv.append("  <th>FW Rev</th>  ");
	    firmwareListDiv.append("  <th>Write Date</th>  ");
	    firmwareListDiv.append("  <th>Release</th> ");
	    //firmwareListDiv.append("  <th>Status</th>  ");
	    firmwareListDiv.append("  <th>Equip Total</th>  ");
	    firmwareListDiv.append("  <th>Total</th> ");
	    firmwareListDiv.append("  <th>Success</th> ");
	    firmwareListDiv.append("  <th>Executing</th> ");
	    firmwareListDiv.append("  <th>Cancel</th> ");
	    firmwareListDiv.append("  <th>Error</th> ");
	    firmwareListDiv.append("  <th>Writer</th>  ");
	    firmwareListDiv.append("  <th>Descr</th>  ");
	    firmwareListDiv.append(" </tr> ");
	    int checkId = 0;
	    for (Object obj : firmwareList) {
	        Object[] objs = (Object[])obj;
	        int len = objs.length;
//		    sys_hw_version, sys_sw_version, sys_sw_revision, ar,  binaryfilename, firmware_id,writedate,released_date , supplier_id
//		    total,succ,pexec,cancel,error 
	        String hwersion = String.valueOf(objs[0]).length() == 0?"UnKown":String.valueOf(objs[0]);
	        String swersion = String.valueOf(objs[1]).length() == 0?"UnKown":String.valueOf(objs[1]);
	        String revision = String.valueOf(objs[2]).length() == 0?"UnKown":String.valueOf(objs[2]);
	        String arm = String.valueOf(objs[3]);
	        String frimware_id = String.valueOf(objs[5]);
	        
	        String tmpTimeA =  String.valueOf(objs[6]);
	        String timeA = "";
	        String timeB = "";
	        if(tmpTimeA == null || tmpTimeA.length() == 0){
	        	tmpTimeA = "00000000";
	        }
	        String tmpTimeB =  String.valueOf(objs[7]);
	        if(tmpTimeB == null || tmpTimeB.length() == 0){
	        	tmpTimeB = "00000000";
	        }
	        if(tmpTimeA.length()>6){
	        	timeA = TimeLocaleUtil.getLocaleDate(tmpTimeA.substring(0, 8));	
	        }else{
	        	timeA = TimeLocaleUtil.getLocaleDate(tmpTimeA);
	        }
	        
	        if(tmpTimeB.length()>6){
	        	timeB = TimeLocaleUtil.getLocaleDate(tmpTimeB.substring(0, 8));	
	        }else{
	        	timeB = TimeLocaleUtil.getLocaleDate(tmpTimeB);
	        }
	        
			String fileName =  String.valueOf(objs[4]);

			String total = String.valueOf(objs[10]);
			String success = String.valueOf(objs[11]);
			String executing = String.valueOf(objs[12]);
			String status = "Undistributed";
			String descr = "";
			
			if(Integer.parseInt(success) > 0 || Integer.parseInt(total) >0 ){
				if(Integer.parseInt(total) == Integer.parseInt(success) ){
					 status = "Distributed";					
				}
			}else if(Integer.parseInt(executing) > 0){
				 status = "Distributing";
			}
			String disable = "";
			String checkboxvlaue = fileName+"|"+hwersion+"|"+swersion+"|"+revision+"|"+frimware_id+"|"+arm;
			if(fileName.length() == 0){
				//disable = "disabled";
				descr = "Firmware file does not exist.";
			}
			
			
			firmwareListDiv.append(" <tr>  ");
		    firmwareListDiv.append("  <td><input name=\"frmlistcheckbox\" id=\"frmlistckbox_"+checkId+"\" type=\"checkbox\" class=\"checkbox\" value = \""+checkboxvlaue+"\" onclick=\"frmlistpagingCheck('frmlistckbox_"+checkId+"','"+equip_type+"');\" "+disable+"></td>  ");
	        firmwareListDiv.append("  <td><a href=\"javascript:viewDistributeStatus('"+frimware_id+"','"+hwersion+"','"+swersion+"','"+revision+"','A');\">"+hwersion+"</a></td> ");//HW
	        firmwareListDiv.append("  <td>"+swersion+"</td> ");//SW
	        firmwareListDiv.append("  <td>"+String.valueOf(objs[2])+"</td> ");//FW Rev(build)
	        firmwareListDiv.append("  <td>"+timeA+"</td> ");//WriteDate
	        firmwareListDiv.append("  <td>"+timeB+"</td> ");//ReleasedDate
	        //firmwareListDiv.append("  <td>"+status+"</td> ");//Status
	        firmwareListDiv.append("  <td>"+ String.valueOf(objs[9])+"</td> ");//equip Tot(펌웨어 버젼을가지고 있는 장비의 갯수)
	        firmwareListDiv.append("  <td>"+total+"</td> ");//Tot
	        firmwareListDiv.append("  <td>"+success+"</td> ");//Success
	        firmwareListDiv.append("  <td>"+executing+"</td> ");//Exec
	        firmwareListDiv.append("  <td>"+String.valueOf(objs[13])+"</td> ");//Cancel
	        firmwareListDiv.append("  <td>"+String.valueOf(objs[14])+"</td> ");//Error
	        firmwareListDiv.append("  <td>"+String.valueOf(objs[8])+"</td> ");//wirter
	        firmwareListDiv.append("  <td>"+descr+"</td> ");//wirter
		    firmwareListDiv.append(" </tr> ");	 
		    
		    checkId++;
	    }
	    
	    
	    if(firmwareList == null || firmwareList.size() < 1){
		    firmwareListDiv.append("<tr><td colspan=\"14\">Data Does Not Exist</td></tr>");
	    }
	    firmwareListDiv.append("</table> ");	
	    
		return firmwareListDiv;
	}
	
	  /**
    *
    * @param       : List
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : FirmWareMgmMaxController에서 호출 (배포관리 >>펌웨어리스트)
    * @return : DivinnerHtml StringBuffer
    */
	@SuppressWarnings("unused")
    public StringBuffer getFirmwareMngList(List<Object> firmwareList,String equip_type,int curPage, int totalRowCnt){
		
		int rowNum = 0;
		
		if(curPage == 1){
			rowNum = totalRowCnt;
		}
		if(curPage > 1){
			rowNum = totalRowCnt-((curPage-1)*5);
		}
		
		StringBuffer firmwareMngListDiv = new StringBuffer();
		firmwareMngListDiv.append("<table class=\"fw_table\" style=\"table-layout:fixed;word-break:break-all;\" height=\"auto\">   ");
		firmwareMngListDiv.append("  <colgroup>                 ");
		firmwareMngListDiv.append("  <col width=\"50\"/>        ");
		firmwareMngListDiv.append("  <col width=\"8%\"/>        ");
		firmwareMngListDiv.append("  <col width=\"10%\"/>       ");
		firmwareMngListDiv.append("  <col width=\"8%\"/>        ");
		firmwareMngListDiv.append("  <col width=\"8%\"/>       ");
		firmwareMngListDiv.append("  <col width=\"8%\"/>       ");
		firmwareMngListDiv.append("  <col width=\"\"/>       ");
		firmwareMngListDiv.append("  <col width=\"10%\"/>       ");
		firmwareMngListDiv.append("  <col width=\"10%\"/>       ");
		firmwareMngListDiv.append("  <col width=\"10%\"/>       ");
		firmwareMngListDiv.append("  </colgroup>                ");
		firmwareMngListDiv.append("  <tr>                       ");
		firmwareMngListDiv.append("    <th>No</th>             ");
		firmwareMngListDiv.append("    <th>Equip Kind</th>          ");
		firmwareMngListDiv.append("    <th>Equip Type</th>          ");
		firmwareMngListDiv.append("    <th>H/W</th>             ");
		firmwareMngListDiv.append("    <th>F/W</th>             ");
		firmwareMngListDiv.append("    <th>FW Rev</th>          ");
		firmwareMngListDiv.append("    <th>Title</th>           ");
		firmwareMngListDiv.append("    <th>Release Date</th>    ");
		firmwareMngListDiv.append("    <th>Write Date</th>      ");
		firmwareMngListDiv.append("    <th>Writer</th>           ");
		firmwareMngListDiv.append("  </tr>                      ");
	    for (Object obj : firmwareList) {
	        Object[] objs = (Object[])obj;
	        int len = objs.length;
//		     frm.MCU_TYPE, frm.MODEM_TYPE, frm.equip_kind, frm.hw_version , frm.fw_version , brd.TITLE, 
//		     brd.writedate ,frm.released_date , brd.OPERATOR_ID , frm.build ,frm.firmware_id, frm.supplier_id  
	        String equip_kind = String.valueOf(objs[2]);
	        String hwersion = String.valueOf(objs[3]).length() == 0?"UnKown":String.valueOf(objs[3]);
	        String swersion = String.valueOf(objs[4]).length() == 0?"UnKown":String.valueOf(objs[4]);
	        String revision = String.valueOf(objs[9]);
	        String brdTitle = String.valueOf(objs[5]);
	        String brdContent = String.valueOf(objs[15]);
	        String releasedDate = String.valueOf(objs[7]);
	        String writedate = String.valueOf(objs[6]);
	        String write = String.valueOf(objs[8]);
	        String frimware_id = String.valueOf(objs[10]);
	        String vendor = String.valueOf(objs[12]);
	        String model = String.valueOf(objs[13]);
	        String arm = String.valueOf(objs[14]);
	        
	        //String tmpTimeA =  String.valueOf(objs[6]);
	        if(releasedDate == null || releasedDate.length() == 0){
	        	releasedDate = "00000000";
	        }
	        if(writedate == null || writedate.length() == 0){
	        	writedate = "00000000";
	        }
	        if(releasedDate.length()>6){
	        	releasedDate = TimeLocaleUtil.getLocaleDate(releasedDate.substring(0, 8));	
	        }else{
	        	releasedDate = TimeLocaleUtil.getLocaleDate(releasedDate);
	        }
	        
	        if(writedate.length()>6){
	        	writedate = TimeLocaleUtil.getLocaleDate(writedate.substring(0, 8));	
	        }else{
	        	writedate = TimeLocaleUtil.getLocaleDate(writedate);
	        }
	        
			String fileName =  String.valueOf(objs[4]);

			String total = String.valueOf(objs[11]);
			String success = String.valueOf(objs[11]);
			String executing = String.valueOf(objs[11]);
			String status = "미배포";
			
			if(Integer.parseInt(success) > 0 || Integer.parseInt(total) >0 ){
				if(Integer.parseInt(total) == Integer.parseInt(success) ){
					 status = "배포완료";					
				}
			}else if(Integer.parseInt(executing) > 0){
				 status = "배포중";
			}
			String disable = "";
			String checkboxvlaue = fileName+"|"+hwersion+"|"+swersion+"|"+revision;
			if(fileName.length() == 0){
				disable = "disabled";
			}		
			firmwareMngListDiv.append("  <tr>                       ");
			firmwareMngListDiv.append("    <td>"+rowNum+"</td>      ");
			firmwareMngListDiv.append("    <td>"+equip_kind+"</td>  ");
			firmwareMngListDiv.append("    <td>"+equip_type+"</td>  ");
			firmwareMngListDiv.append("    <td><a href=\"javascript:setAddFirmWareForm('"+frimware_id+"','"+equip_type+"','"+vendor+"','"+model+"','"+hwersion+"','"+swersion+"','"+revision+"','"+arm+"');\">"+hwersion+"</a></td>    ");
			firmwareMngListDiv.append("    <td>"+swersion+"</td>    ");
			firmwareMngListDiv.append("    <td>"+revision+"</td>    ");
			firmwareMngListDiv.append("    <td><a href=\"javascript:viewFrmContent('"+brdContent+"');\">"+brdTitle+"</a></td>    ");
			firmwareMngListDiv.append("    <td>"+releasedDate+"</td> ");
			firmwareMngListDiv.append("    <td>"+writedate+"</td>    ");
			firmwareMngListDiv.append("    <td>"+write+"</td>        ");
			firmwareMngListDiv.append("  </tr>                       ");
		rowNum--;
	    }		
		firmwareMngListDiv.append("</table>                      ");
		
		return firmwareMngListDiv;
	}
	
	 /**
    *
    * @param       : List
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : FirmWareMgmMaxController에서 호출 (배포>>배포상태정보)
    * @return : DivinnerHtml StringBuffer
    */
	public StringBuffer getMakeHtmlLocationTriger(List<Object> locatonDetailList,HashMap<String, Object> param ){
		StringBuffer makeHtmlLocationTriger = new StringBuffer();
		String gubun =  String.valueOf(param.get("gubun"));
    	String firmware_Id =  String.valueOf(param.get("firmware_Id"));
    	String build =  String.valueOf(param.get("build"));
    	String equip_kind =  String.valueOf(param.get("equip_kind"));
    	String equip_type =  String.valueOf(param.get("equip_type"));
    	String mcuStr = "";
    	if(equip_kind.equals("Modem")&&gubun.equals("A")){
    		if(equip_type.equals("MMIU")|| equip_type.equals("IEIU")){
        		mcuStr = "MCU ID";
    		}else{
        		mcuStr = "Device Serial";    			
    		}
    	}else{
    		mcuStr = "MCU ID";
    	}
    		
		String gubunStr = gubun.equals("A") ? mcuStr : "Location";
		String checkedStr = "";
		if(locatonDetailList.size()>0){
	        makeHtmlLocationTriger.append("<ul class=\"fw_inline h30\" >  "); 
	        checkedStr = gubun.equals("A") ? "checked" : "";
	        makeHtmlLocationTriger.append("<li class=\"mgn_r20\"><input type=\"radio\" name=\"a1\" "+checkedStr+" class=\"radio\" id=\"distributeStatusA\" onclick=\"javascript:clickViewDistributeStatus('A');\"> By MCU</li>  ");
	        checkedStr = gubun.equals("B") ? "checked" : "";
	        makeHtmlLocationTriger.append("<li class=\"mgn_r20\"><input type=\"radio\" name=\"a1\" "+checkedStr+" class=\"radio\" id=\"distributeStatusB\" onclick=\"javascript:clickViewDistributeStatus('B');\"> By Location</li> ");
	        makeHtmlLocationTriger.append("</ul>  ");
	        makeHtmlLocationTriger.append("<div class=\"float_none\"></div>  ");
	        makeHtmlLocationTriger.append("<div>");
	        makeHtmlLocationTriger.append("<table class=\"fw_table\">  ");
	        makeHtmlLocationTriger.append("<colgroup>  ");
	        makeHtmlLocationTriger.append("<col width=\"150px\"/>  ");
	        makeHtmlLocationTriger.append("<col width=\"120px\"/>  ");
	        makeHtmlLocationTriger.append("<col width=\"120px\"/>  ");
	        makeHtmlLocationTriger.append("<col width=\"120px\"/>  ");
	        makeHtmlLocationTriger.append("<col width=\"120px\"/>  ");
	        makeHtmlLocationTriger.append("<col width=\"100px\"/>  ");
	        makeHtmlLocationTriger.append("<col width=\"100px\"/>  ");
	        makeHtmlLocationTriger.append("<col width=\"\"/>  ");
	        makeHtmlLocationTriger.append("</colgroup>  ");
	        makeHtmlLocationTriger.append("<tr>  ");
	        makeHtmlLocationTriger.append(" <th>"+gubunStr+"</th>  ");
	        makeHtmlLocationTriger.append(" <th>Trigger ID</th>  ");
	        makeHtmlLocationTriger.append(" <th>Total</th>  ");
	        makeHtmlLocationTriger.append(" <th>Sucess</th>  ");
	        makeHtmlLocationTriger.append(" <th>Executing</th>  ");
	        makeHtmlLocationTriger.append(" <th>Cancel</th>  ");
	        makeHtmlLocationTriger.append(" <th>Error</th>  ");
	        makeHtmlLocationTriger.append(" <th>Retry</th>  ");
	        makeHtmlLocationTriger.append("</tr>  ");
	        makeHtmlLocationTriger.append("</table>  ");
	        makeHtmlLocationTriger.append("</div>");
	        makeHtmlLocationTriger.append("<div class=\"border_table\" style=\"overflow-y:auto;overflow-x:none;height:168px\">  ");
	        makeHtmlLocationTriger.append("<table class=\"fw_table\">  ");
			for (Object objl : locatonDetailList) {
		        Object[] objsl = (Object[])objl;
		        
		        String tr_id  = String.valueOf(objsl[1]);
		        String trigger_step  = String.valueOf(objsl[7]);
		        String trigger_state = String.valueOf(objsl[8]);
		        String ota_step  = String.valueOf(objsl[9]);
		        String ota_state = String.valueOf(objsl[10]);
		        String mcu_id = "";
		        
		        if(equip_kind.equals("Modem")){
		        	mcu_id = String.valueOf(objsl[11]);
		        }else{
		        	if(gubun.equals("B")){
		        		mcu_id = String.valueOf(objsl[11]);
		        	}else if(gubun.equals("A")){
		        		mcu_id = String.valueOf(objsl[0]);
		        	}
		        }

		        
		        
		        makeHtmlLocationTriger.append("<colgroup>  ");
		        makeHtmlLocationTriger.append("<col width=\"150px\"/>  ");
		        makeHtmlLocationTriger.append("<col width=\"120px\"/>  ");
		        makeHtmlLocationTriger.append("<col width=\"120px\"/>  ");
		        makeHtmlLocationTriger.append("<col width=\"120px\"/>  ");
		        makeHtmlLocationTriger.append("<col width=\"120px\"/>  ");
		        makeHtmlLocationTriger.append("<col width=\"100px\"/>  ");
		        makeHtmlLocationTriger.append("<col width=\"100px\"/>  ");
		        makeHtmlLocationTriger.append("<col width=\"\"/>  ");
		        makeHtmlLocationTriger.append("</colgroup>  ");
		        makeHtmlLocationTriger.append("<tr>  ");
		        makeHtmlLocationTriger.append(" <td>"+objsl[0]+"</td>  ");
		        makeHtmlLocationTriger.append(" <td>"+objsl[1]+"</td>  ");
		        makeHtmlLocationTriger.append(" <td>"+objsl[2]+"</td>  ");
		        makeHtmlLocationTriger.append(" <td>"+objsl[3]+"</td>  ");
		        makeHtmlLocationTriger.append(" <td>"+objsl[4]+"</td>  ");
		        makeHtmlLocationTriger.append(" <td>"+objsl[5]+"</td>  ");
		        makeHtmlLocationTriger.append(" <td>"+objsl[6]+"</td>  ");

		        if (StringUtil.nullToBlank(cmdAuth).equals("true")) {
	                makeHtmlLocationTriger.append(" <td><em><A href=\"javascript:goRedistStep2('step3','"+firmware_Id+"','"+tr_id+"','"+build+"','"+equip_kind+"','"+trigger_step+"','"+trigger_state+"','"+ota_step+"','"+ota_state+"','"+mcu_id+"','"+equip_type+"');\">Retry</A></em></td>  ");
		        } else {
	                makeHtmlLocationTriger.append(" <td></td>  ");
		        }

		        makeHtmlLocationTriger.append("</tr>  ");
			}
			makeHtmlLocationTriger.append("</table>  ");
	        makeHtmlLocationTriger.append("</div>  ");
	        //makeHtmlLocationTriger.append("</table>  ");
		}else{
			makeHtmlLocationTriger.append("<ul class=\"fw_inline h30\" >  "); 
			checkedStr = gubun.equals("A") ? "checked" : "";
	        makeHtmlLocationTriger.append("<li class=\"mgn_r20\"><input type=\"radio\" name=\"a1\" "+checkedStr+" class=\"radio\" id=\"distributeStatusA\"  onclick=\"javascript:clickViewDistributeStatus('A');\"> By MCU</li>  ");
	        checkedStr = gubun.equals("B") ? "checked" : "";
	        makeHtmlLocationTriger.append("<li class=\"mgn_r20\"><input type=\"radio\" name=\"a1\" "+checkedStr+" class=\"radio\" id=\"distributeStatusB\"  onclick=\"javascript:clickViewDistributeStatus('B');\"> By Location</li> ");
	        makeHtmlLocationTriger.append("</ul>  ");
	        makeHtmlLocationTriger.append("<div class=\"float_none\"></div>  ");
	        makeHtmlLocationTriger.append("<table class=\"fw_table\">  ");
	        makeHtmlLocationTriger.append("<colgroup>  ");
	        makeHtmlLocationTriger.append("<col width=\"10%\"/>  ");
	        makeHtmlLocationTriger.append("<col width=\"\"/>  ");
	        makeHtmlLocationTriger.append("<col width=\"15%\"/>  ");
	        makeHtmlLocationTriger.append("<col width=\"10%\"/>  ");
	        makeHtmlLocationTriger.append("<col width=\"15%\"/>  ");
	        makeHtmlLocationTriger.append("<col width=\"10%\"/>  ");
	        makeHtmlLocationTriger.append("<col width=\"10%\"/>  ");
	        makeHtmlLocationTriger.append("<col width=\"10%\"/>  ");
	        makeHtmlLocationTriger.append("</colgroup>  ");
	        makeHtmlLocationTriger.append("<tr>  ");
	        makeHtmlLocationTriger.append(" <th>"+gubunStr+"</th>  ");
	        makeHtmlLocationTriger.append(" <th>Trigger ID</th>  ");
	        makeHtmlLocationTriger.append(" <th>Total</th>  ");
	        makeHtmlLocationTriger.append(" <th>Sucess</th>  ");
	        makeHtmlLocationTriger.append(" <th>Executing</th>  ");
	        makeHtmlLocationTriger.append(" <th>Cancel</th>  ");
	        makeHtmlLocationTriger.append(" <th>Error</th>  ");
	        makeHtmlLocationTriger.append(" <th>Retry</th>  ");
	        makeHtmlLocationTriger.append("</tr>  ");
	        makeHtmlLocationTriger.append("<tr>  ");
	        makeHtmlLocationTriger.append(" <td colspan=8> Data Does Not Exist </td>  ");
	        makeHtmlLocationTriger.append("</tr>  ");
	        makeHtmlLocationTriger.append("</table>  ");	
		}
		return makeHtmlLocationTriger;
	}
	
	/**
    *
    * @param       : List
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : FirmWareMgmMaxController에서 호출 (배포내역>>TriggerListSetp1)
    * @return : DivinnerHtml StringBuffer
    */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public StringBuffer getTriggerListStep1(List<Object> datalist,HashMap<String, Object> param,FirmWarePageTreat pp, int curPage ){
		StringBuffer makeHtml = new StringBuffer();
		int totPage = pp.getTotalPCnt();
		int totalRowCnt = pp.getRowCnt();
		int rowNum = 0;
		
		log.debug("totalPage="+totPage+", totalRowCnt="+totalRowCnt+", curPage="+curPage);
		
		if(curPage == 1){
			rowNum = totalRowCnt;
		}
		if(curPage > 1){
			rowNum = totalRowCnt-((curPage-1)*5);
		}
		
		if(datalist.size()>0){
			makeHtml.append(" <div class=\"mgn_t20\"><em class=\"blu12_bold\">Total.</em>"+curPage+" / "+totPage+" Page </div> "); 
			makeHtml.append(" <table class=\"fw_table\"> ");
			makeHtml.append(" 	<tr> ");
			makeHtml.append(" 		<th rowspan=\"2\">No</th> ");
			makeHtml.append(" 		<th rowspan=\"2\">Trigger ID</th> ");
			makeHtml.append(" 		<th rowspan=\"2\">Equip Kind</th> ");
			makeHtml.append(" 		<th rowspan=\"2\">Equip Type</th> ");
			makeHtml.append(" 		<th colspan=\"3\">Source Version</th> ");
			makeHtml.append(" 		<th colspan=\"3\">Target Version</th> ");
			makeHtml.append(" 		<th  colspan=\"5\">State</th> ");
			makeHtml.append(" 	</tr> ");
			makeHtml.append(" 	<tr> ");
			makeHtml.append(" 		<td class=\"subtit\">H/W Version</td> ");
			makeHtml.append(" 		<td class=\"subtit\">F/W Version</td> ");
			makeHtml.append(" 		<td class=\"subtit\">F/W Build</td> ");
			makeHtml.append(" 		<td class=\"subtit\">H/W Version</td> ");
			makeHtml.append(" 		<td class=\"subtit\">F/W Version</td> ");
			makeHtml.append(" 		<td class=\"subtit\">F/W Build</td> ");
			makeHtml.append(" 		<td class=\"subtit\">Total</td> ");
			makeHtml.append(" 		<td class=\"subtit\">Success</td> ");
			makeHtml.append(" 		<td class=\"subtit\">Executing</td> ");
			makeHtml.append(" 		<td class=\"subtit\">Cancel</td> ");
			makeHtml.append(" 		<td class=\"subtit\">Error</td> ");
			makeHtml.append(" 	</tr> ");	
			for (Object objl : datalist) {
		        Object[] objsl = (Object[])objl;
		        String equip_type  =  String.valueOf(objsl[2]);
		        String src_firmware  =  String.valueOf(objsl[14]);//[0_4_NURITelecom_NZC I211_1.0_3.0_2809_false]		        
				String src_hw_version = String.valueOf(objsl[17]);
				String src_sw_version = String.valueOf(objsl[18]);
				String src_sw_revision = "";
				if(equip_type.equals("MMIU")||equip_type.equals("IEIU")){
					src_sw_revision = "";
				}else{
					src_sw_revision = String.valueOf(objsl[19]);	
				}
				
				makeHtml.append(" 	<tr> ");
				makeHtml.append(" 		<td>"+rowNum+"</td> ");
				makeHtml.append(" 		<td><a href=\"javascript:TriggerInfoSetp2('"+objsl[0]+"','"+objsl[16]+"','"+objsl[1]+"','"+objsl[11]+"','"+equip_type+"');\">"+objsl[0]+"</a></td> ");
				makeHtml.append(" 		<td>"+objsl[1]+"</td> ");
				makeHtml.append(" 		<td>"+objsl[2]+"</td> ");
				makeHtml.append(" 		<td>"+src_hw_version+"</td> ");
				makeHtml.append(" 		<td>"+src_sw_version+"</td> ");
				makeHtml.append(" 		<td>"+src_sw_revision+"</td> ");
				makeHtml.append(" 		<td>"+objsl[6]+"</td> ");
				makeHtml.append(" 		<td>"+objsl[7]+"</td> ");
				makeHtml.append(" 		<td>"+objsl[8]+"</td> ");
				makeHtml.append(" 		<td>"+objsl[9]+"</td> ");
				makeHtml.append(" 		<td>"+objsl[10]+"</td> ");
				makeHtml.append(" 		<td>"+objsl[11]+"</td> ");
				makeHtml.append(" 		<td>"+objsl[12]+"</td> ");
				makeHtml.append(" 		<td>"+objsl[13]+"</td> ");
				makeHtml.append(" 	</tr> ");
				rowNum--;
			}
			makeHtml.append(" </table> ");
		}else{
			makeHtml.append(triggerListStep1Div);
		
		}
		
		return makeHtml ;
	}
	
	/**
    *
    * @param       : List
    * @exception   : 
    * @Date        : 2011/02/11
    * @Description : FirmWareMgmMaxController에서 호출 (배포내역 TriggerInfo>>TriggerListSetp2)
    * @return : DivinnerHtml StringBuffer
    */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public ArrayList getTriggerListStep2(List<Object> datalist,HashMap<String, Object> param ){
		StringBuffer makeHtml = new StringBuffer();
		ArrayList returnList = new ArrayList();
		ArrayList statusStoreList = new ArrayList();
		String equip_kind = String.valueOf(param.get("equip_kind"));
		String equip_type = String.valueOf(param.get("equip_type"));
		int trExec = Integer.parseInt(String.valueOf(param.get("trExec")));
		if(datalist.size()>0){
			int i = 0;
	        String targetFirmWare = "";
	        String tr_id = "";
	        String build = "";
	        String trigger_step = "";
	        String trigger_state = "";
	        String ota_step = "";
	        String ota_state = "";
	        String mcu_id = "";
	        String sys_id = "";
			for (Object objl : datalist) {
		        Object[] objsl = (Object[])objl;
		        if(i == 0){
			        String tmpStrA = String.valueOf(objsl[7]) ;
			        String tmpStrB = String.valueOf(objsl[8]) ;
			        String starttime = tmpStrA.equals("null") ? "000000":tmpStrA;
			        String entime = tmpStrB.equals("null") ? "000000":tmpStrB;
			        String src_firmware  =  String.valueOf(objsl[9]);//src_firmware 에서 hw,sw, build 버젼을 유추할 수 있다.[0_4_NURITelecom_NZC I211_1.0_3.0_2809_false]

			        if(equip_kind.equals("Modem")&&!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
			        	mcu_id =String.valueOf(objsl[24]);		        	
			        }else{
			        	mcu_id =String.valueOf(objsl[22]);	
			        }

					String src_hw_version = String.valueOf(objsl[10]);
					String src_sw_version = String.valueOf(objsl[11]);
					String src_sw_revision = String.valueOf(objsl[12]);
			        targetFirmWare =  String.valueOf(objsl[10]);			        
			        tr_id =  String.valueOf(objsl[5]);
			        build =  String.valueOf(objsl[16]);
			        sys_id =  String.valueOf(objsl[22]);
			        trigger_step =  String.valueOf(objsl[0]);
			        trigger_state =  String.valueOf(objsl[1]);
			        ota_step =  String.valueOf(objsl[2]);
			        ota_state =  String.valueOf(objsl[3]);
			        
			        if(starttime.length()>6){
			        	starttime = TimeLocaleUtil.getLocaleDate(starttime.substring(0, 8));	
			        }else{
			        	starttime = TimeLocaleUtil.getLocaleDate(starttime);
			        }
			        
			        if(entime.length()>6) entime = TimeLocaleUtil.getLocaleDate(entime.substring(0, 8));	
			        else entime = TimeLocaleUtil.getLocaleDate(entime);
			        
			        if(entime.length()>6) entime = TimeLocaleUtil.getLocaleDate(entime.substring(0, 8));	
			        else entime = TimeLocaleUtil.getLocaleDate(entime);
			        
					makeHtml.append(" <table class=\"fw_basic_gry\">   ");
					makeHtml.append(" 	<colgroup>                     ");
					makeHtml.append("     <col width=\"15%\"/>         ");
					makeHtml.append("     <col width=\"42%\"/>         ");
					makeHtml.append("     <col width=\"15%\"/>         ");
					makeHtml.append("     <col width=\"\"/>            ");
					makeHtml.append("   </colgroup>                    ");
					makeHtml.append(" 	<tr>                           ");
					makeHtml.append(" 	<th>Trigger ID / MCU ID</th>   ");
					makeHtml.append(" 	<td>"+objsl[5]+" / "+objsl[22]+"</td>  ");
					makeHtml.append(" 	<th>State / Operation</th>     ");
					makeHtml.append("   <td>                     ");
					
					
					makeHtml.append("    <select name=\"step3Select\" id=\"step3Select\" onchange=\"javascript:step3SelectChenge();\" > ");
					makeHtml.append("    <option selected=\"selected\">All</option> ");
					makeHtml.append("    <OPTION >Succ</OPTION>   ");
					makeHtml.append("    <OPTION >Exec</OPTION>   ");
					makeHtml.append("    <OPTION >Cancel</OPTION> ");
					makeHtml.append("    <OPTION >Error</OPTION>  ");
					makeHtml.append("    </select>                ");
					makeHtml.append("    / ");
					if (StringUtil.nullToBlank(cmdAuth).equals("true")) {
//	                    if (trExec > 0) {
//	                        makeHtml.append("    <td><em class=\"fw_button\"><a href=\"javascript:goRedistStep2('step2','"+targetFirmWare+"','"+tr_id+"','"+build+"','"+equip_kind+"','"+trigger_step+"','"+trigger_state+"','"+ota_step+"','"+ota_state+"','"+mcu_id+"','"+equip_type+"');\">Retry</a></em><em class=\"fw_button\"><a href=\"javascript:goCanceldistStep2('"+mcu_id+"','"+tr_id+"','"+sys_id+"');\">Cancel</a></em><em class=\"fw_button\"><a href=\"javascript:goStatedistStep2('"+mcu_id+"','"+tr_id+"','"+sys_id+"');\">State</a></em></td> ");
//	                    } else {
//	                        makeHtml.append("    <td><em class=\"fw_button\"><a href=\"javascript:goRedistStep2('step2','"+targetFirmWare+"','"+tr_id+"','"+build+"','"+equip_kind+"','"+trigger_step+"','"+trigger_state+"','"+ota_step+"','"+ota_state+"','"+mcu_id+"','"+equip_type+"');\">Retry</a></em></td> ");   
//	                    }
	                    makeHtml.append("    <em class=\"fw_button\"><a href=\"javascript:goRedistStep2('step2','").append(targetFirmWare).append("','").append(tr_id).append("','");
	                    makeHtml.append(build).append("','").append(equip_kind).append("','").append(trigger_step).append("','").append(trigger_state).append("','").append(ota_step).append("','");
	                    makeHtml.append(ota_state).append("','").append(mcu_id).append("','").append(equip_type).append("');\">Retry</a></em>");

	                    if (trExec > 0) {
                            makeHtml.append("<em class=\"fw_button\"><a href=\"javascript:goCanceldistStep2('").append(mcu_id).append("','").append(tr_id).append("','").append(sys_id).append("');\">Cancel</a></em>");
                            makeHtml.append("<em class=\"fw_button\"><a href=\"javascript:goStatedistStep2('").append(mcu_id).append("','").append(tr_id).append("','").append(sys_id).append("');\">State</a></em>");
                        }

					} 
					makeHtml.append("    </td>                    ");
					makeHtml.append(" 	</tr>                          ");
					makeHtml.append("   <tr>                           ");
					makeHtml.append("    <th>Start Time / End Time</th>           ");
					makeHtml.append("    <td>"+starttime+" / "+entime+"</td>  ");
					makeHtml.append("    <th>MCU Type / Location</th>             ");
					makeHtml.append("    <td>"+objsl[6]+" / "+objsl[20]+"</td>         ");
					makeHtml.append("   </tr>                          ");
					makeHtml.append("   <tr>                           ");
					makeHtml.append("    <th>Source Firmware</th>      ");
					makeHtml.append("    <td>"+objsl[9]+"</td>         ");
					makeHtml.append("    <th>Target Firmware</th>      ");
					makeHtml.append("    <td>"+objsl[13]+"</td> ");
					makeHtml.append("   </tr>                          ");
					makeHtml.append("   <tr>                           ");
					makeHtml.append("    <th>Source HW/FW/Build Ver</th>       ");
					makeHtml.append("    <td>"+src_hw_version+"/"+src_sw_version+"/"+src_sw_revision+"</td>   ");
					makeHtml.append("    <th>Target HW/FW/Build Ver</th>       ");
					makeHtml.append("    <td>"+objsl[15]+"/"+objsl[14]+"/"+objsl[16]+"</td>        ");
					makeHtml.append("   </tr>                          ");
					makeHtml.append(" </table>                    ");
					i++;
		        }
		        
		    	//-----------------------------------------------------------------------------
				//Trigger Step(0:Init, 1:Download, 2:Start, 3:End, 4:Success
			    //Trigger State(0:Success, 1:Fail, 2:Cancel, 3:Unknown)
			    //OTA Step(01:Check, 02:Data Send, 04:Verify, 08:Install, 16:Scan, 31:All)
			    //OTA Step state(0:Success, 1:Fail, 2:Cancel, 3:Unknown)
			    //-----------------------------------------------------------------------------
		        
		  
		        
		        int inttrigger_step = Integer.parseInt(trigger_step) ;
		        int inttrigger_State = Integer.parseInt(trigger_state);
		        int intota_state = Integer.parseInt(ota_state);
		        int triggerCnt = Integer.parseInt(String.valueOf(objsl[21]));
		        String setState = "";
		        int errorCnt = 0;
	
		        String setTriggerStep = inttrigger_step==0?"Init":(inttrigger_step==1?"Download":(inttrigger_step==2?"Start":(inttrigger_step==3?"End":(inttrigger_step==4?"Sucess":""))));
//		        String settriggerState = trigger_State==0?"Success":(trigger_State==1?"Fail":(trigger_State==2?"Start":(trigger_State==3?"Cancel":(trigger_State==4?"Unknown":""))));
				//Succ
				if(inttrigger_step==4 && inttrigger_State ==0){
					setState = "Succ";
				}
				//Cancel
				else if(intota_state==2){
					setState = "Cancel";
				}
				//Error
				else if((inttrigger_State==1 && intota_state!=2) || intota_state==1){
					errorCnt++;
					setState = "Error";
				}
				//Exec
				else{
					setState = "Exec";
				}
				
				HashMap<String, Object> statusListMap = new HashMap<String, Object>();
				
		        if(equip_kind.equals("Modem")&&!equip_type.equals("MMIU")&&!equip_type.equals("IEIU")){
		        	mcu_id =String.valueOf(objsl[24]);		        	
		        }else{
		        	mcu_id =String.valueOf(objsl[22]);	
		        }

				statusListMap.put("mcu_id",mcu_id);
				statusListMap.put("equip_kind",equip_kind);
				statusListMap.put("triggerStep",setTriggerStep);
				statusListMap.put("trigger_step",String.valueOf(trigger_step));
				statusListMap.put("trigger_state",trigger_state);
				statusListMap.put("triggerCnt",String.valueOf(triggerCnt));
				statusListMap.put("setState",setState);
				statusListMap.put("ota_state",ota_state);
				statusListMap.put("ota_step",ota_step);
				statusListMap.put("errorCnt",errorCnt);
				statusListMap.put("targetFirmWare",targetFirmWare);
				statusListMap.put("tr_id",tr_id);
				statusListMap.put("build",build);
				
				statusStoreList.add(statusListMap);
		        
		    }
		}else{
			makeHtml.append(triggerListStep2Div);
		}
		
		returnList.add(makeHtml);
		returnList.add(statusStoreList);
		
		return returnList ;
	}	
	
	/**
    *
    * @param       : List
    * @exception   : 
    * @Date        : 2011/02/11
    * @Description : FirmWareMgmMaxController에서 호출 (배포내역 TriggerInfo>>TriggerListSetp3)
    * @return : DivinnerHtml StringBuffer
    */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public ArrayList getTriggerListStep3(ArrayList datalist,String trequip_type ){
		ArrayList returnArr = new ArrayList();
		StringBuffer makeHtml = new StringBuffer();
		ArrayList arrSucc = new ArrayList();
		ArrayList arrCancel = new ArrayList();
		ArrayList arrError = new ArrayList();
		ArrayList arrExec = new ArrayList();
		
		int datalistCnt = datalist.size();
		if(datalistCnt>0){
			String top_equip_kind = "";
			String mcuStr = "";
			for (int i=0 ; i < datalist.size() ; i ++) {
				HashMap<String, Object> statusListMap = (HashMap<String, Object>) datalist.get(i);
				top_equip_kind= (String) statusListMap.get("equip_kind");
				if(top_equip_kind.equals("Modem")){
					mcuStr = "Device Serial";
				}else{
					mcuStr = "MCU ID";
				}
			}
			
			makeHtml.append(" <div>	");
	        makeHtml.append(" <table class=\"fw_table\">	");
	        makeHtml.append(" 	<colgroup>                  ");
	        makeHtml.append(" 		<col width=\"50\" />      ");
	        makeHtml.append(" 		<col width=\"240\" />     ");
	        makeHtml.append(" 		<col width=\"160\" />        ");
	        makeHtml.append(" 		<col width=\"160\" />        ");
	        makeHtml.append(" 		<col width=\"160\" />        ");
	        makeHtml.append(" 		<col width=\"160\" />        ");
	        makeHtml.append(" 		<col width=\"\" />        ");
	        makeHtml.append(" 	</colgroup>                 ");
	        makeHtml.append(" 	<tr>                        ");
	        makeHtml.append(" 		<th>No</th>               ");
	        makeHtml.append(" 		<th>"+mcuStr+"</th>               ");
	        makeHtml.append(" 		<th>State</th>            ");
	        makeHtml.append(" 		<th>Trigger Step</th>     ");
	        makeHtml.append(" 		<th>Trigger Count</th>    ");
	        makeHtml.append(" 		<th>Error</th>            ");
	        makeHtml.append(" 		<th>Retry</th>            ");
	        makeHtml.append(" 	</tr>                       ");
	        makeHtml.append(" </table>	");
	        makeHtml.append(" </div>	");
	        makeHtml.append(" <div class=\"border_table\" style=\"overflow-y:auto;overflow-x:none;height:168px\">	");
	        makeHtml.append(" <table class=\"fw_table\">	");
			for (int i=0 ; i < datalist.size() ; i ++) {
				HashMap<String, Object> statusListMap = (HashMap<String, Object>) datalist.get(i);
				String mcu_id = (String) statusListMap.get("mcu_id");
				String equip_kind= (String) statusListMap.get("equip_kind");
//				String equip_type= (String) statusListMap.get("equip_type");
				String setTriggerStep= (String) statusListMap.get("triggerStep");
				String trigger_step= String.valueOf(statusListMap.get("trigger_step"));
				String trigger_state= String.valueOf(statusListMap.get("trigger_state"));
				String triggerCnt= String.valueOf(statusListMap.get("triggerCnt"));
				String setState= String.valueOf(statusListMap.get("setState"));
				String ota_state= String.valueOf(statusListMap.get("ota_state"));
				String ota_step= String.valueOf(statusListMap.get("ota_step"));
				String targetFirmWare= String.valueOf(statusListMap.get("targetFirmWare"));
				String errorCnt= String.valueOf(statusListMap.get("errorCnt"));
				String tr_id= String.valueOf(statusListMap.get("tr_id"));
				String build= String.valueOf(statusListMap.get("build"));

				String retryHtml = null;

				if (StringUtil.nullToBlank(cmdAuth).equals("true")) {
	                retryHtml = "        <td><em><a href=\"javascript:goRedistStep2('step3','"+targetFirmWare+"','"+tr_id+"','"+build+"','"+equip_kind+"','"+trigger_step+"','"+trigger_state+"','"+ota_step+"','"+ota_state+"','"+mcu_id+"','"+trequip_type+"');\">Retry</a></em></td> ";
				} else {
	                retryHtml = "        <td></td> ";
				}

				makeHtml.append(" 	<colgroup>                  ");
		        makeHtml.append(" 		<col width=\"50\" />      ");
		        makeHtml.append(" 		<col width=\"240\" />     ");
		        makeHtml.append(" 		<col width=\"160\" />        ");
		        makeHtml.append(" 		<col width=\"160\" />        ");
		        makeHtml.append(" 		<col width=\"160\" />        ");
		        makeHtml.append(" 		<col width=\"160\" />        ");
		        makeHtml.append(" 		<col width=\"\" />        ");
		        makeHtml.append(" 	</colgroup>                 ");
		        makeHtml.append(" 	<tr>                        ");
		        makeHtml.append(" 		<td>"+datalistCnt--+"</td>                ");
		        makeHtml.append(" 		<td>"+mcu_id+"</td>    ");		        	
		        makeHtml.append(" 		<td>"+setState+"</td>                ");
		        makeHtml.append(" 		<td>"+setTriggerStep+"</td>                ");
		        makeHtml.append(" 		<td>"+triggerCnt+"</td>                ");
		        makeHtml.append(" 		<td>"+errorCnt+"</td>                ");
		        makeHtml.append(retryHtml);
		        makeHtml.append(" 	</tr> ");
		        
		        if(setState.equals("Succ")){
		        	HashMap<String, Object> statusListMap2 = new HashMap<String, Object>();
		        	statusListMap2.put("mcu_id", mcu_id);
		        	statusListMap2.put("setState", setState);
		        	statusListMap2.put("setTriggerStep", setTriggerStep);
		        	statusListMap2.put("triggerCnt", triggerCnt);
		        	statusListMap2.put("errorCnt", errorCnt);
		        	statusListMap2.put("retryHtml", retryHtml);
		        	arrSucc.add(statusListMap2);
		        }else if(setState.equals("Cancel")){
		        	HashMap<String, Object> statusListMap2 = new HashMap<String, Object>();
		        	statusListMap2.put("mcu_id", mcu_id);
		        	statusListMap2.put("setState", setState);
		        	statusListMap2.put("setTriggerStep", setTriggerStep);
		        	statusListMap2.put("triggerCnt", triggerCnt);
		        	statusListMap2.put("errorCnt", errorCnt);
		        	statusListMap2.put("retryHtml", retryHtml);
		        	arrCancel.add(statusListMap2);		        	
		        }else if(setState.equals("Error")){
		        	HashMap<String, Object> statusListMap2 = new HashMap<String, Object>();
		        	statusListMap2.put("mcu_id", mcu_id);
		        	statusListMap2.put("setState", setState);
		        	statusListMap2.put("setTriggerStep", setTriggerStep);
		        	statusListMap2.put("triggerCnt", triggerCnt);
		        	statusListMap2.put("errorCnt", errorCnt);
		        	statusListMap2.put("retryHtml", retryHtml);
		        	arrError.add(statusListMap2);
		        }else if(setState.equals("Exec")){
		        	HashMap<String, Object> statusListMap2 = new HashMap<String, Object>();
		        	statusListMap2.put("mcu_id", mcu_id);
		        	statusListMap2.put("setState", setState);
		        	statusListMap2.put("setTriggerStep", setTriggerStep);
		        	statusListMap2.put("triggerCnt", triggerCnt);
		        	statusListMap2.put("errorCnt", errorCnt);
		        	statusListMap2.put("retryHtml", retryHtml);
		        	arrExec.add(statusListMap2);
		        }
		    }
	        makeHtml.append(" </table> ");
	        makeHtml.append(" </div> ");
		}else{
			makeHtml.append(triggerListStep3Div);
/*			makeHtml.append(" <table   class=\"fw_table\">	");
			makeHtml.append(" 	<colgroup>                  ");
			makeHtml.append(" 		<col width=\"50\" />      ");
			makeHtml.append(" 		<col width=\"25%\" />     ");
			makeHtml.append(" 		<col width=\"\" />        ");
			makeHtml.append(" 		<col width=\"\" />        ");
			makeHtml.append(" 		<col width=\"\" />        ");
			makeHtml.append(" 		<col width=\"\" />        ");
			makeHtml.append(" 		<col width=\"\" />        ");
			makeHtml.append(" 	</colgroup>                 ");
			makeHtml.append(" 	<tr>                        ");
			makeHtml.append(" 		<th>No</th>               ");
			makeHtml.append(" 		<th>ID</th>               ");
			makeHtml.append(" 		<th>State</th>            ");
			makeHtml.append(" 		<th>Trigger Step</th>     ");
			makeHtml.append(" 		<th>Trigger Count</th>    ");
			makeHtml.append(" 		<th>Error</th>            ");
			makeHtml.append(" 		<th>Retry</th>            ");
			makeHtml.append(" 	</tr>                       ");
			makeHtml.append(" 	<tr>                        ");
			makeHtml.append(" 		<td colspan=\"7\">Data Does Not Exist</td>   ");
			makeHtml.append(" 	</tr> ");
			makeHtml.append(" </table> ");*/
		}
		
		returnArr.add(makeHtml.toString());
		returnArr.add(getTriggerListStep3setState(arrSucc).toString());
		returnArr.add(getTriggerListStep3setState(arrCancel).toString());
		returnArr.add(getTriggerListStep3setState(arrError).toString());
		returnArr.add(getTriggerListStep3setState(arrExec).toString());
		
		return returnArr ;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public StringBuffer getTriggerListStep3setState(ArrayList datalist ){
		StringBuffer makeHtml = new StringBuffer();
		int datalistCnt = datalist.size();
		int datalistCnt2 = datalist.size();
		
		if(datalistCnt>0){
	        makeHtml.append(" <table   class=\"fw_table\">	");
	        makeHtml.append(" 	<colgroup>                  ");
	        makeHtml.append(" 		<col width=\"50\" />      ");
	        makeHtml.append(" 		<col width=\"25%\" />     ");
	        makeHtml.append(" 		<col width=\"\" />        ");
	        makeHtml.append(" 		<col width=\"\" />        ");
	        makeHtml.append(" 		<col width=\"\" />        ");
	        makeHtml.append(" 		<col width=\"\" />        ");
	        makeHtml.append(" 		<col width=\"\" />        ");
	        makeHtml.append(" 	</colgroup>                 ");
			makeHtml.append(" 	<tr>                        ");
	        makeHtml.append(" 		<th>No</th>               ");
	        makeHtml.append(" 		<th>ID</th>               ");
	        makeHtml.append(" 		<th>State</th>            ");
	        makeHtml.append(" 		<th>Trigger Step</th>     ");
	        makeHtml.append(" 		<th>Trigger Count</th>    ");
	        makeHtml.append(" 		<th>Error</th>            ");
	        makeHtml.append(" 		<th>Retry</th>            ");
	        makeHtml.append(" 	</tr>                       ");
			for(int i =0 ; i< datalistCnt2 ; i++){
				HashMap<String, Object> statusListMap = (HashMap<String, Object>) datalist.get(i);
		        makeHtml.append(" 	<tr>                        ");
		        makeHtml.append(" 		<td>"+datalistCnt--+"</td>                ");
		        makeHtml.append(" 		<td>"+statusListMap.get("mcu_id")+"</td>    ");		        	
		        makeHtml.append(" 		<td>"+statusListMap.get("setState")+"</td>                ");
		        makeHtml.append(" 		<td>"+statusListMap.get("setTriggerStep")+"</td>                ");
		        makeHtml.append(" 		<td>"+statusListMap.get("triggerCnt")+"</td>                ");
		        makeHtml.append(" 		<td>"+statusListMap.get("errorCnt")+"</td>                ");
		        makeHtml.append(statusListMap.get("retryHtml"));
		        makeHtml.append(" 	</tr> ");
			}
			 makeHtml.append(" </table> ");
		}else{
			makeHtml.append(triggerListStep3Div);
/*			makeHtml.append(" <table   class=\"fw_table\">	");
			makeHtml.append(" 	<colgroup>                  ");
			makeHtml.append(" 		<col width=\"50\" />      ");
			makeHtml.append(" 		<col width=\"25%\" />     ");
			makeHtml.append(" 		<col width=\"\" />        ");
			makeHtml.append(" 		<col width=\"\" />        ");
			makeHtml.append(" 		<col width=\"\" />        ");
			makeHtml.append(" 		<col width=\"\" />        ");
			makeHtml.append(" 		<col width=\"\" />        ");
			makeHtml.append(" 	</colgroup>                 ");
			makeHtml.append(" 	<tr>                        ");
			makeHtml.append(" 		<th>No</th>               ");
			makeHtml.append(" 		<th>ID</th>               ");
			makeHtml.append(" 		<th>State</th>            ");
			makeHtml.append(" 		<th>Trigger Step</th>     ");
			makeHtml.append(" 		<th>Trigger Count</th>    ");
			makeHtml.append(" 		<th>Error</th>            ");
			makeHtml.append(" 		<th>Retry</th>            ");
			makeHtml.append(" 	</tr>                       ");
			makeHtml.append(" 	<tr>                        ");
			makeHtml.append(" 		<td colspan=\"7\">Data Does Not Exist</td>   ");
			makeHtml.append(" 	</tr> ");
			makeHtml.append(" </table> ");*/
		}
		
		return makeHtml ;
	}
	
}
