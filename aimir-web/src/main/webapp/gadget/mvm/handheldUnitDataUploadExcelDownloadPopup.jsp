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
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    
<!-- SCRIPT -->
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/    
var flex;
var supplierId = ${supplierId};

/**
 * 유저 세션 정보 가져오기
 */
/*$.getJSON('${ctx}/common/getUserInfo.do',
        function(json) {
            if(json.supplierId != ""){
                supplierId = json.supplierId;
            }
        }
);*/

$(document).ready(function(){

	var obj = window.opener.obj;
	var arr = getFmtMessage();

	if(obj.callType == "hhuFile"){
		
    }
    else if(obj.callType == "hhuExcel"){
    	emergePre();    	
        $.ajaxSetup({ async: true });
        $.post('${ctx}/gadget/mvm/hhuMakeExcelExport.do'
                , { 
        			'supplierId' : supplierId,
                    'meterId'    : obj.meterId,
                    'loginId'    : obj.loginId,
                    'startDate'  : obj.startDate,
                    'endDate'    : obj.endDate,
                    'title'    	 : obj.title,   
                    'msgMap[]'	 : arr,
                   	}
                , function(json) {
                    hide();
                    if (json.total == 0) {
                    	$("#datalist").hide();
                    	$("#nodata").show();
                    	return;
                    }

                    $("#filePath").val(json.filePath);
                    $("#fileName").val(json.fileName);
                    $("#zipFileName").val(json.zipFileName);
                    $("#zipFile").text(json.zipFileName);
                    
                    addTableRows(json.fileNames);
                }
        );
        $.ajaxSetup({ async: false });
    }
    else ;
    
});


function fileDown(fname) {
	$("#fileName").val($("#"+fname).val());
    var url = "${ctx}/common/fileDownload.do";
    var downform = document.getElementsByName("reportDownloadForm")[0];

    downform.action = url;
    downform.submit();
}

function getFmtMessage(){
    var fmtMessage = new Array();

    fmtMessage[0] = "<fmt:message key="aimir.loginId"/>";
    fmtMessage[1] = "<fmt:message key="aimir.view.mcu18"/><fmt:message key="aimir.date"/>";
    fmtMessage[2] = "<fmt:message key="aimir.meterid"/>";
    fmtMessage[3] = "<fmt:message key="aimir.system.meterreg"/>";
    fmtMessage[4] = "<fmt:message key="aimir.datatype"/>";
    fmtMessage[5] = "<fmt:message key="aimir.time.from"/>";
    fmtMessage[6] = "<fmt:message key="aimir.time.to"/>";
    fmtMessage[7] = "Total (Success/Failure)";
    fmtMessage[8] = "<fmt:message key="aimir.file.name"/>";
    fmtMessage[9] = "<fmt:message key="aimir.report.fileDownloadDir"/>";
    fmtMessage[10] = "<fmt:message key="aimir.number"/>";

    return fmtMessage;
}

function addTableRows(fileNames) {
    var tbl = $("table");

    var htmltxt = '<colgroup><col width="45" /><col width="" /><col width="" /></colgroup>';
    for(var i = 0 ; i < fileNames.length ; i++) {
        //var tbRow = table.insertRow();
        htmltxt += "<tr><th>"+(i+1)+"</th><td>"+fileNames[i]+"</td><td class='button'>" +
                   "<input type='hidden' name='fileName"+i+"' id='fileName"+i+"' value='"+fileNames[i]+"'/>" +
                   "<button type='button' class='input_button' style='cursor:hand;' onclick='javascript:fileDown(\"fileName"+i+"\");'>" +
                   '<fmt:message key="aimir.report.fileDownload"/>' + "</button></td></tr>";
    }
    tbl.html(htmltxt);
}


function winClose() {
    window.close();
//    top.close();
}
/*]]>*/
</script>

    
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