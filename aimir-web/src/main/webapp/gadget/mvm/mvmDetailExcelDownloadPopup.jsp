<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/gadget/system/preLoading.jsp"%>
<title><fmt:message key="aimir.report.fileDownload"/></title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8">

	$(document).ready(function() {
		var obj = window.opener.obj;
		var supplierId      = obj.supplierId;
		var searchType		= obj.searchType;
		var searchStartDate	= obj.searchStartDate;
		var searchEndDate	= obj.searchEndDate;
		var searchStartHour	= obj.searchStartHour;
		var searchEndHour	= obj.searchEndHour;
		var meterNo			= obj.meterNo;
		var channel			= obj.channel;
		var type			= obj.type;
		var viewAll         = obj.viewAll;
		
		// houly이고 check박스 선택되어 있을 때 interval로 엑셀 출력
		if(searchType == DateType.HOURLY && viewAll =="yes") {
			searchType = DateTabOther.INTERVAL;
		} 
		
		emergePre();
		$.ajaxSetup({ async: true });
		
		$.post('${ctx}/gadget/mvm/mvmDetailGadgetExcelMake.do', {
			'supplierId'        : supplierId,
			'searchType'        : searchType,
			'searchStartDate'   : searchStartDate,
			'searchEndDate'     : searchEndDate,
			'searchStartHour'	: searchStartHour,
			'searchEndHour'  	: searchEndHour,
			'meterNo'        	: meterNo,
			'channel'       	: channel,
			'type'        		: type
			}, function(json) {
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
	});

	function fileDown(fname) {
		$("#fileName").val($("#"+fname).val());
		var url = "${ctx}/common/fileDownload.do";
		var downform = document.getElementsByName("reportDownloadForm")[0];

		downform.action = url;
		downform.submit();
	}

	function addTableRows(fileNames) {
		var tbl = $("table");

		var htmltxt = '<colgroup><col width="45" /><col width="" /><col width="" /></colgroup>';
		for(var i = 0 ; i < fileNames.length ; i++) {
			htmltxt += "<tr><th>"+(i+1)+"</th><td>"+fileNames[i]+"</td><td class='button'>" +
					   "<input type='hidden' name='fileName"+i+"' id='fileName"+i+"' value='"+fileNames[i]+"'/>" +
					   "<button type='button' class='input_button' style='cursor:hand;' onclick='javascript:fileDown(\"fileName"+i+"\");'>" +
					   '<fmt:message key="aimir.report.fileDownload"/>' + "</button></td></tr>";
		}
		
		tbl.html(htmltxt);
	}

	function winClose() {
		window.close();
	}
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
                <div class="divbutton" onclick="winClose();" style="cursor:hand;">
					<fmt:message key="aimir.board.close"/>
				</div>
			</em></li>
		</ul>
	</div>

</div>

</body>
</html>