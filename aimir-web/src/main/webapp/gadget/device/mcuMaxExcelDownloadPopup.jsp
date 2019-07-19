<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
 	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
 	<%@ include file="/gadget/system/preLoading.jsp"%>
	<title><fmt:message key="aimir.report.fileDownload"/></title>
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    //var flex;
    //var supplierId = "";

    /**
     * 유저 세션 정보 가져오기
     */
    //$.getJSON('${ctx}/common/getUserInfo.do',
    //        function(json) {
    //            if(json.supplierId != ""){
    //                supplierId = json.supplierId;
    //            }
    //        }
    //);

    $(document).ready(function(){
    	var obj = window.opener.obj;
    	var arr = getFmtMessage();

        emergePre();
        //$.ajaxSetup({ async: true });
        $.post('${ctx}/gadget/device/mcuMaxGadgetGridExcelMake.do'
                , {'supplierId' 		: obj.supplierId, //mcuMaxGadget에서 그대로 넘어온 값
                	'mcuId' 			: obj.mcuId,
        			'mcuType' 			: obj.mcuType, 
                	'locationId' 		: obj.locationId,
                	'swVersion' 		: obj.swVersion,
                	'hwVersion' 		: obj.hwVersion,
                	'installDateStart' 	: obj.installDateStart,
                	'installDateEnd' 	: obj.installDateEnd,
                	'protocol' 			: obj.protocol,
                    'filter'			: obj.filter,
                    'order'				: obj.order,
                    'mcuStatus'			: obj.mcuStatus,
                    //fmt:head Text
                    'number'			: arr[0],
                    'mcuId2'			: arr[1],
                    'mcuName'			: arr[2],
                    'mcuMobile'			: arr[3],
                    'ipAddress'			: arr[4],
                    'swVer'				: arr[5],
                    'installation'		: arr[6],
                    'lastCommDate'		: arr[7],
                    'CommStatus'		: arr[8],
                    'msg09'				: arr[9],
                    'filePath'  		: arr[10],
                    'HH48over'			: arr[11],
                    'HH24over'			: arr[12],
                    'normal'			: arr[13],
                    'title'				: obj.title,
                    //MDIS 추가
                    'mcuTypeFmt'		: arr[14],
        			'vendor'			: arr[15],
        			'model'				: arr[16],
        			'hwVer'				: arr[17],
        			'protocolType'		: arr[18],
        			'location'			: arr[19],
                    'mcuSerial'         : arr[20],
                    // sys_location 추가
                    'sysLocation'		: arr[21]
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
        //$.ajaxSetup({ async: false });
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
        
        fmtMessage[0] = "<fmt:message key="aimir.number"/>";
        fmtMessage[1] = "<fmt:message key="aimir.mcuid"/>";
        fmtMessage[2] = "<fmt:message key="aimir.mcu.name"/>";
        fmtMessage[3] = "<fmt:message key="aimir.mcumobile"/>";
        fmtMessage[4] = "<fmt:message key="aimir.ipaddress"/>";
        fmtMessage[5] = "<fmt:message key="aimir.sw.version"/>";
        fmtMessage[6] = "<fmt:message key="aimir.installationdate"/>";
        fmtMessage[7] = "<fmt:message key="aimir.lastcomm"/>";
        fmtMessage[8] = "<fmt:message key="aimir.commstatus"/>";
        fmtMessage[9] = "<fmt:message key="aimir.firmware.msg09"/>";
        fmtMessage[10] = "<fmt:message key="aimir.report.fileDownloadDir"/>";        // File Path
        fmtMessage[11] = "<fmt:message key="aimir.commstateRed"/>"; // 48시간 통신장애
        fmtMessage[12] = "<fmt:message key="aimir.commstateYellow"/>"; // 24시간 통신장애
        fmtMessage[13] = "<fmt:message key="aimir.normal"/>"; // 정상 
        fmtMessage[14] = "<fmt:message key="aimir.mcutype"/>"; 
        fmtMessage[15] = "<fmt:message key="aimir.vendor"/>"; 
        fmtMessage[16] = "<fmt:message key="aimir.model"/>";  
        fmtMessage[17] = "<fmt:message key="aimir.fw.hwversion"/>";  
        fmtMessage[18] = "<fmt:message key="aimir.view.mcu39"/>";  
        fmtMessage[19] = "<fmt:message key="aimir.location"/>";
        fmtMessage[20] = "<fmt:message key="aimir.dcuSerial"/>";
        fmtMessage[21] = "<fmt:message key="aimir.installed.location"/>"; // 설치 주소 sys_location

        return fmtMessage;
    }

    //function getParams() {
    //    var condArray = new Array();
    //    condArray[0] = periodType;
    //    condArray[1] = $('#searchEndDate').val();
    //    condArray[2] = $('#seasonalSeasonCombo').val();
    //    condArray[3] = supplierId;
    //    condArray[4] = $('#searchEndDate').val().substr(0,4);
    //    
    //    return condArray;
    //}

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
//        top.close();
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