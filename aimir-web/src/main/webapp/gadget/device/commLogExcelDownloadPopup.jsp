<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <%@ include file="/gadget/system/preLoading.jsp"%>
	<title><fmt:message key="aimir.report.fileDownload"/></title>
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
	<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

        var flex;
        var supplierId = ${supplierId};

        /**
         * 유저 세션 정보 가져오기
         */
    /*      
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    if(json.supplierId != ""){
                        supplierId = json.supplierId;
                    }
                }
        );
 */
        
        
        $(document).ready(function()
   		{
        	
        	
        	//이전 페이지에서 (jsp)에서 날라온 parameter
        	var obj = window.opener.obj;
        	
        	//메세지 프로퍼티를 배열로 가지고 온다.
        	var arr = getFmtMessage();
        	
             emergePre();
            $.ajaxSetup({ async: true }); 
            

            $.ajax({
	            type:"POST",
	            data:{
	            	'supplierId'        : obj.supplierId,
	            	//'tabName'        : obj.tabName,
	            	'tabType'        : obj.tabType,
	            	'search_from'        : obj.search_from,
	            	'dateType'        : obj.tabName,
                    'svcTypeCode' : obj.svcTypeCode,
                    'protocolCode' : obj.protocolCode,
                    'senderId' : obj.senderId,
                    'receiverId' : obj.receiverId,
                    'hourlyStartDate' : obj.hourlyStartDate,
                    'hourlyEndDate' : obj.hourlyEndDate,
                    'hourlyStartHourCombo_input' : obj.hourlyStartHourCombo_input,
                    'hourlyEndHourCombo_input' : obj.hourlyEndHourCombo_input,
                    'periodType_input' : obj.periodType_input,
                    'periodStartDate' : obj.periodStartDate,
                    'periodEndDate' : obj.periodEndDate,
                    'weeklyYearCombo_input' : obj.weeklyYearCombo_input,
                    'weeklyMonthCombo_input' :obj.weeklyMonthCombo_input,
                    'weeklyWeekCombo_input' : obj.weeklyWeekCombo_input,
                    'monthlyYearCombo_input' :obj.monthlyYearCombo_input,
                    'monthlyMonthCombo_input' : obj.monthlyMonthCombo_input,
                    'msg_time'         : arr[0],
                    'msg_datatype' : arr[1],
                    'msg_protocol'   : arr[2], 
                    'msg_sender'   : arr[3],
                    'msg_receiver'          : arr[4],
                    'msg_sendbytes'       : arr[5],
                    'msg_receivebytes'        : arr[6],
                    'msg_result'        : arr[7],
                    'msg_totalcommtime'        : arr[8],
                    'msg_operationcode'        : arr[9],
                    'filePath'       : arr[10] 
               
            },
                    
	            dataType:"json",
	            url:'${ctx}/gadget/device/commLog/commLogExcelMake.do',
	            success:function(data, status) 
	            {
	            	
	            	//alert("성공");
	            	
	            	//json을 data로 대체( serverside에서 날라오는 data)
                    hide();
                    if (data.total == 0) 
                    {
                    	$("#datalist").hide();
                    	$("#nodata").show();
                    	return;
                    }
                    else
                   	{
						;                 	
                   	}
                    
                    
                 /*    alert(data.filePath);
                    alert(data.fileName);
                    alert(data.zipFileName);
                    alert(data.zipFileName); */

                    
                    
                    $("#filePath").val(data.filePath);
                    $("#fileName").val(data.fileName);
                    $("#zipFileName").val(data.zipFileName);
                    $("#zipFile").text(data.zipFileName);
                    
                    addTableRows(data.fileNames);
                	
	            },
	            error:function(request, status)
	            {
	            	Ext.Msg.alert('<fmt:message key="aimir.message"/>','Failed to Ajax Communication');
	                Ext.Msg.alert('<fmt:message key='aimir.message'/>',status);
	            }
	        });// ajaxEnd
        	
         
        });//document Ready End

        function fileDown(fname) 
        {
        	$("#fileName").val($("#"+fname).val());
            var url = "${ctx}/common/fileDownload.do";
            var downform = document.getElementsByName("reportDownloadForm")[0];

            downform.action = url;
            downform.submit();
        }

        function getFmtMessage()
        {
            var fmtMessage = new Array();
            var cnt = 0;
            

            fmtMessage[0] =  "<fmt:message key="aimir.time"/>";
           	fmtMessage[1] = "<fmt:message key="aimir.datatype"/>";
       		fmtMessage[2] = "<fmt:message key="aimir.protocol"/>";
   			fmtMessage[3] = "<fmt:message key="aimir.sender"/>";
			fmtMessage[4] = "<fmt:message key="aimir.receiver"/>";
			fmtMessage[5] = "<fmt:message key="aimir.sendbytes"/>";
    		fmtMessage[6] = "<fmt:message key="aimir.receivebytes"/>";
    		fmtMessage[7] = "<fmt:message key="aimir.result"/>";
    		fmtMessage[8] = "<fmt:message key="aimir.totalcommtime"/>";
    		fmtMessage[9] = "<fmt:message key="aimir.operationcode"/>";  
   	        fmtMessage[10] = "<fmt:message key="aimir.report.fileDownloadDir"/>";        // File Path        

            
    

            return fmtMessage;
        } 
    
        function addTableRows(fileNames) 
        {
            var tbl = $("table");

            var htmltxt = '<colgroup><col width="45" /><col width="" /><col width="" /></colgroup>';
            for(var i = 0 ; i < fileNames.length ; i++) 
            {
                //var tbRow = table.insertRow();
                htmltxt += "<tr><th>"+(i+1)+"</th><td>"+fileNames[i]+"</td><td class='button'>" +
                           "<input type='hidden' name='fileName"+i+"' id='fileName"+i+"' value='"+fileNames[i]+"'/>" +
                           "<button type='button' class='input_button' style='cursor:hand;' onclick='javascript:fileDown(\"fileName"+i+"\");'>" +
                           '<fmt:message key="aimir.report.fileDownload"/>' + "</button></td></tr>";
            }
            tbl.html(htmltxt);
        }

        function winClose()
        {
            window.close();
//            top.close();
        }
    /*]]>*/
    </script>
    <base target="_self" ></base>
</head>
<body>
<form name="reportDownloadForm" id="reportDownloadForm" method="post" target="downFrame" style="display:none;">
<input type="hidden" id="filePath" name="filePath" />
<input type="hidden" id="fileName" name="fileName" />
<input type="hidden" id="zipFileName" name="zipFileName" />
<input type="hidden" id="realFileName" name="realFileName" />
</form>
<iframe name="downFrame" style="display:none;"></iframe>
<div id="wrapper">
	<div class="popup_title"><span class="icon_download"></span><fmt:message key="aimir.report.fileDownload"/></div>
	
	<div id="datalist" class="overflow_hidden"  style="display:block">
		<div class="download_zip">
			<ul>
			<!-- <li class="file">billingday20110418(1).zip</li> -->
			<li class="file" id="zipFile"></li>
			<li class="downbtn">
				<em class="am_button">
					<div class="divbutton" onclick="javascript:fileDown('zipFileName');">
						<fmt:message key="aimir.report.fileDownload"/> 
					</div>
				</em>
			</li>
			</ul>
		</div>
		
		<div class="downloadbox">
			<table class="download" id="filelist">

			</table>
		</div>
		
		<div class="overflow_hidden textalign-center">
			<em class="am_button">
				<div class="divbutton" onclick="winClose();" style="cursor:hand;">
					<fmt:message key="aimir.board.close"/>
				</div>
			</em>
		</div>
	</div>
	<div id="nodata" class="nodata"  style="display:none">
		<ul>
			<li class="text"><fmt:message key="aimir.data.notexist"/></li>
			<li class="close"><em class="am_button">
				<!-- <div class="divbutton" onclick="javascript:window.close();" > -->
                <div class="divbutton" onclick="winClose();" style="cursor:hand;">
					<fmt:message key="aimir.board.close"/>
				</div>
			</em></li>
		</ul>
	</div>

</div>

</body>
</html>