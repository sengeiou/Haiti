<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<title>오퍼레이터 excel download popup</title>
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
        	var arr = getFmtMessage2();
        	
        	//alert("operatorExcelDownloadPopup.jsp");
        	
            // emergePre();
            $.ajaxSetup({ async: true }); 

            $.ajax({
	            type:"POST",
	            data:{
	            	'supplierId'        : obj.supplierId,
	            	//'tabName'        : obj.tabName,
	            	'tabType'       	: obj.tabType,
	            	'search_from'       : obj.search_from,
	            	'dateType'        	: obj.tabName,
	            	'roleId'        	: obj.roleId,
                    'dailyStartDate' 	: obj.dailyStartDate,
                    /* 'hourlyEndDate' : obj.hourlyEndDate,
                    'hourlyStartHourCombo_input' : obj.hourlyStartHourCombo_input,
                    'hourlyEndHourCombo_input' : obj.hourlyEndHourCombo_input, */
                    'periodType_input' 	: obj.periodType_input,
                    'periodStartDate' 	: obj.periodStartDate,
                    'periodEndDate' 	: obj.periodEndDate,
                    'weeklyYearCombo_input' 	: obj.weeklyYearCombo_input,
                    'weeklyMonthCombo_input'	:obj.weeklyMonthCombo_input,
                    'weeklyWeekCombo_input' 	: obj.weeklyWeekCombo_input,
                    'monthlyYearCombo_input'	:obj.monthlyYearCombo_input,
                    'monthlyMonthCombo_input' 	: obj.monthlyMonthCombo_input,
                    'loginStatusCheckedValue'	: obj.loginStatusCheckedValue,
                    'loginLogLoginId'	: obj.loginLogLoginId,
                    'loginLogIpAddr'	: obj.loginLogIpAddr,
                    'msg_number'        : arr[0],
                    'msg_userid' 		: arr[1],
                    'msg_username'   	: arr[2], 
                    'msg_usergroup'   	: arr[3],
                    'msg_ipaddress'     : arr[4],
                    'msg_loginhour'     : arr[5],
                    'msg_logouthour'    : arr[6],
                    'msg_status'        : arr[7],
            		'filePath'        	: arr[8]
            },
                    
	            dataType:"json",
	            ///gadget/system/operator/operatorExcelDownloadPopup
	            url:'${ctx}/gadget/system/operator/operatorExcelMake.do',
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
						//alert("데이타 있슴");                    	
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
	            	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"excel make async comm fail");
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

       /*  function getFmtMessage2()
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
   	        fmtMessage[9] = "<fmt:message key="aimir.report.fileDownloadDir"/>";        // File Path        

            
    

            return fmtMessage;
        } 
     */
	     function getFmtMessage2()
	     {
	     	
	         var fmtMessage = new Array();
	         fmtMessage[0] = "<fmt:message key="aimir.number"/>";
	         fmtMessage[1] = "<fmt:message key="aimir.user.id"/>";
	         fmtMessage[2] = "<fmt:message key="aimir.name.user"/>";
	         fmtMessage[3] = "<fmt:message key="aimir.user.group"/>";
	         fmtMessage[4] = "<fmt:message key="aimir.ipaddress"/>";
	
	         fmtMessage[5] = "<fmt:message key="aimir.login.login"/>" + " <fmt:message key="aimir.hour"/>";
	         fmtMessage[6] = "<fmt:message key="aimir.login.logout"/>"+ " <fmt:message key="aimir.hour"/>";
	         fmtMessage[7] = "<fmt:message key="aimir.status"/>";
	         fmtMessage[8] = "<fmt:message key="aimir.report.fileDownloadDir"/>";        // File Path        
	
	        
	         
	         /* var roleManageObj = document.getElementById("roleManage");
	         var selectedIdx = roleManageObj.roleName.selectedIndex;
	         this.roleId = roleManageObj.roleName[selectedIdx].value; */
	         
	
	         //alert("this.roleId : " + this.roleId);
	
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