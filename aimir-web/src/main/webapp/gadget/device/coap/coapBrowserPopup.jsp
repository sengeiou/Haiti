<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<!-- STYLE -->
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >

	<style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        .remove {
            background-image:url(../../images/allOff.gif) !important;
        }
        .accept {
            background-image:url(../../images/allOn.png) !important;
        }

        @media screen and (-webkit-min-device-pixel-ratio:0) {
            .x-grid3-row td.x-grid3-cell {
                padding-left: 0px;
                padding-right: 0px;
            }
        }
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold;
        }
	</style>
	
<!-- LIB -->
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>	
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
</head>
<body>
<script type="text/javascript" charset="utf-8">

var target="";
var loginId="";
var device="";

$(document).ready(function () {
	// data
    var obj = window.opener.obj;
	target  = obj.target;
	loginId = obj.loginId;
	if(obj.device != null)
		device = obj.device;
})
function coapBrowser(){
	//alert("device: " + device +"\nloginId: " + loginId + "\ntarget: " + target+"\nURI: "+ $("#uri").val()+"\nquery: " + $("#query").val() + "\ntype: " + $("input[name=config]:checked").val());
	if($("#uri").val().trim() == ""){
		Ext.Msg.alert('', "Please make sure to enter 'URI'");
		return;
	}
	
	var url=""
	if(device == "dcu")
		url = '${ctx}/gadget/device/command/coapBrowserForDCU.do';
	else
		url = '${ctx}/gadget/device/command/coapBrowser.do';
	Ext.Msg.wait('Waiting for response.', 'Wait !');	
	$('#commandResult').val("Request modem info ....");
	$.ajax({
        type : "POST",
        data : {
        	    'uri'  			: $("#uri").val()
        	,   'query'  		: $("#query").val()
            ,   'loginId' 		: loginId
            ,   'target' 		: target
            ,   'config'        : $("input[name=config]:checked").val()
            
		},
        dataType : "json",
        async : true,
        url : url,
        success : function (returnData){
            if(!returnData.status){
            	Ext.Msg.hide();
                $('#commandResult').val("[FAIL] " + returnData.rtnStr);
                   return;
            }
            if(returnData.status.length>0 && returnData.status=='SUCCESS'){
            	Ext.Msg.hide();
                $('#commandResult').val(returnData.rtnStr);
            }else{
            	Ext.Msg.hide();
            	$('#commandResult').val(returnData.rtnStr);
            }
        }});
}

var helpWin;
function help(){
	var opts = "width=700px, height=680px, left=" + 1000 + "px, top=200px, scrollbars = yes";
	if (helpWin){
		helpWin.close();
	}
	helpWin = window.open("${ctx}/gadget/device/coap/coapHelpPopup.do", 
							"help", opts);
	helpWin.opener.obj = obj;			
}
</script>
<div id="wrapper" class="max">
	
	<!-- title -->
	<div class="search-bg-basic">
		<ul class="basic-ul">   
			<li class="basic-li bluebold11pt withinput">CoAP Browser</li>                
        </ul>
	</div>
	
	<div class="margin10px padding-b10px border_blu">
		<table class="wfree margin10px">
			<tr>
				<td class="graybold11pt withinput" style="width:100px;">URI</td><td class="padding-r20px withinput"><input type="textbox" id="uri" style="width:290px"></label></td>
			</tr>
			<tr>
				<td class="graybold11pt withinput">Query</td><td class="padding-r20px withinput"><input type="textbox" id="query"  style="width:290px"></label></td>				
			</tr>
			
		</table>
		<table>
		<td><input type="radio"  name="config" value="GET" checked="checked" class="transonly margin-l10"/><em class="bluebold11pt">GET</em></td>
		<td><input type="radio"  name="config" value="PUT" class="transonly margin-l10"/><em class="bluebold11pt">PUT</em></td>
		
		<td class="padding-r20px"></td><td class="padding-r20px"></td><td class="padding-r20px"></td><td class="padding-r20px"></td><td class="padding-r20px"></td>
		<td class="padding-r20px"></td><td class="padding-r20px">
		<!-- <td class="padding-r20px"></td><td class="padding-r20px"></td> -->
		<td><em id="help" class="am_button"><a href="javascript:help();" class="on">Command List</a></em><td>
		<td class="padding-r10px"></td>
		<td><em id="Execute" class="am_button"><a href="javascript:coapBrowser();" class="on"><fmt:message key='aimir.execute'/></a></em><td>
		<!-- <td><em id="help" class="am_button"><a href="javascript:help();" class="on">HELP</a></em><td> -->
		
		</table>
	
	</div>
	<div class="margin10px"><label class="check">Operation Result</label></div>
	<!-- <textarea id = commandReseult class="margin10px padding-b10px border_blu" style="height: 100px;"> -->
	<textarea id="commandResult" name="commandResult" style="height: 120px; width: 410px; margin:10px; padding:10px" readonly="readonly">Operation Result</textarea>
	</div>

</div>
</body>
</html>