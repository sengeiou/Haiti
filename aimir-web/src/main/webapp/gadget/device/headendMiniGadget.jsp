<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">
    
    var ctrlId;
    var timeout;
    var retry;
    var writeDate;
    var timeId;
    var intervalSec = 5000;
    // Command 권한
    var cmdAuth = "${cmdAuth}";
    
    $(document).ready(function() {
        // 권한체크
        if (cmdAuth == "true") {
            $("#updBtn").show();
        } else {
            $("#updBtn").hide();
        }

     	retry = document.getElementById("retryCount").value;
    	timeout = document.getElementById("timeout").value; 
   });
    
	function displayForm(_divName) {
		
		if(_divName == 'headendBasicDiv') {
			if(timeout == "" || timeout == null) {
				$('#timeout').val("-");
			} else {
				$('#timeout').val(timeout);
			}
			
			if(retry == "" || retry == null) {
				$('#retryCount').val("-");
			} else {
				$('#retryCount').val(retry);
			}
			
			$('#headendUpdateDiv').hide();
			$('#headendBasicDiv').show();
		} else if(_divName == 'headendUpdateDiv') {
			$('#updateTimeout').val(timeout);
			$('#updateRetryCount').val(retry);
			
			if($('#timeout').val() == "-") {
				$('#updateTimeout').val("");
			}
			if($('#retryCount').val() == "-") {
				$('#updateRetryCount').val("");
			}
			$('#timeout').val("");
			$('#retryCount').val("");
			
			
			$('#headendUpdateDiv').show();
			$('#headendBasicDiv').hide();
		}
	};
	
	// ok button action
     function saveCommand() {
		timeout = $('#updateTimeout').val();
 		retry = $('#updateRetryCount').val();
 		ctrlId="ST";
 		$.getJSON('${ctx}/gadget/device/saveHeadendCtrlCommand.do'
 					,{'ctrlId'    	 : ctrlId,
                       'timeout'   	 : timeout,
                       'retry'    	 : retry},
 					 function(json) {
                    	   result = json.result;
                    	   
                    	   $.getJSON('${ctx}/gadget/device/headendMiniGadget.do'
                    			   ,{}, function(json2) {
                    				   timeout = json2.headend.timeout;
                    				   retry = json2.headend.retry;
                    			   });
                    	   
 							displayForm('headendBasicDiv');
 							
 							Ext.Msg.wait('Waiting for response.', 'Wait !');
 							
 							saveHeadendCtrlCommandResult(result, json.headendCtrlwriteDate);
 							
 						});
            }
	
	// command 실행 결과
     function saveHeadendCtrlCommandResult(result, writeDate) {
		
		
		
	     if (result.status == "success") {
	    	 timeId = setInterval("checkCommandStatus("+writeDate+")", intervalSec);
	     } else {
	    	 Ext.Msg.hide(); 
	         document.getElementById('status').innerHTML = result.msg;
	     }
     }
	
 	 // 실행 중인 command 의 결과를 체크한다.
	function checkCommandStatus(writeDate) {
 		$.getJSON('${ctx}/gadget/device/getHeadendCtrlCommandResultData.do'
               ,{'ctrlId' : ctrlId,
                 'writeDate' : writeDate}
               ,function (json) {
                   var result = json.result;
                   if (result.status == "complete" || result.status == "error") {
                	   Ext.Msg.hide();
                	   document.getElementById('status').innerHTML = result.msg;
                       clearInterval(timeId);
                       
                   }
                });
    }
        
    
    </script>
</head>
<body>

<div class="w_auto">
	<div id="headendBasicDiv" style="margin: 30px;">
		<table>
			<tr>
				<th class="blue12pt" style="position: absolute; left: 30px;"><label class="check"><fmt:message key="aimir.timeout"/></label></th>
	                <c:choose>
	                	<c:when test="${not empty headend.timeout}">
	                  		<td style="padding-left: 110px"><input type="text" id="timeout" style="border: 0; text-align: center;" value="${headend.timeout}"  readonly/>&nbsp; <fmt:message key="aimir.sec"/></td>
	                	</c:when>
	                	<c:otherwise>
	    					<td style="padding-left: 110px"><input type="text" id="timeout" style="border: 0; text-align: center;" value="-"  readonly/>&nbsp; <fmt:message key="aimir.sec"/></td>
	                	</c:otherwise>
	                </c:choose>
				
			</tr>
			<tr>
				<th class="blue12pt" style="position: absolute; left: 30px;"><label class="check"><fmt:message key="aimir.retrycount"/></label></th>
				<c:choose>
	                	<c:when test="${not empty headend.retry}">
	                  		<td style="padding-left: 110px"><input type="text" id="retryCount" style="border: 0; text-align: center;" value="${headend.retry}" readonly/></td>
	                	</c:when>
	                	<c:otherwise>
	    					<td style="padding-left: 110px"><input type="text" id="retryCount" style="border: 0; text-align: center;" value="-" readonly/></td>
	                	</c:otherwise>
	                </c:choose>
				
			</tr>
		</table>
	<div class="margin10px" style="right: 30px; position: absolute;">
		<span id="updBtn" class="am_button">
			<a href="javaScript:displayForm('headendUpdateDiv');"><fmt:message key="aimir.update"/></a>
		</span>
	</div>
	</div>
</div>

<!-- Update -->
<div class="w_auto">
	<div id="headendUpdateDiv" style="display: none; margin: 30px">
		<table>
			<tr>
				<th class="blue12pt" style="position: absolute; left: 30px;"><label class="check"><fmt:message key="aimir.timeout"/></label></th>
				<td style="padding-left: 110px"><input type="text" id="updateTimeout" style="text-align: center;"/>&nbsp;<fmt:message key="aimir.sec"/></td>
			</tr>
			<tr>
				<th class="blue12pt" style="position: absolute; left: 30px;"><label class="check"><fmt:message key="aimir.retrycount"/></label></th>
				<td style="padding-left: 110px"><input type="text" id="updateRetryCount" style="text-align: center;"/></td>
			</tr>
			<tr class="margin10px" style="right: 30px; position: absolute;">
				<td style="padding-right: 5px">
					<span class="am_button">
						<a href="javaScript:saveCommand();"><fmt:message key="aimir.ok"/></a>
					</span>
				</td>
				<td>
					<span class="am_button" >
						<a href="javaScript:displayForm('headendBasicDiv');"><fmt:message key="aimir.cancel"/></a>
					</span>
				</td>
			</tr>
		</table>
	</div>
</div>
<!-- Status -->
<div style="margin-top: 55px">
	<div class="margin-l30" >
	<li class="blue12pt"><label class="check"><b><fmt:message key="aimir.status"/></b></label></li>
	<textarea id="status" style="height: 73px; width: 340px;" readonly>Result</textarea>
	</div>
</div>
</body>
</html>