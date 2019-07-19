<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="/gadget/system/preLoading.jsp"%>
<title><fmt:message key="aimir.report.fileDownload" /></title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8">
    $(document).ready(function() {
    	var obj = window.opener.obj;
    	var supplierId    = obj.supplierId;
        var filePath      = obj.filePath;
        
        $.post('${ctx}/gadget/device/updateSimCardFile.do', 
        		{   'supplierId'			: supplierId,
		        	'filePath'				: filePath
				}, function(result) {
					hide();
					
					$("#importResult").text(result.resultMsg);
					addTableRows(result.deviceList, result.excelLineList);
				}
           );
    });
    
    function addTableRows(fileNames, excelLineList) {
        var tbl = $("table");
        var htmltxt = '<colgroup><col width="45" /><col width="" /><col width="" /></colgroup>';	
        htmltxt = '<tr><th>No</th><th>Excel Line</th><th>IMSI</th></tr>';
        
        for(var i = 0 ; i < fileNames.length ; i++) {
            htmltxt += "<tr><td style = 'text-align : center;'>" + (i+1) + "</td>" + 
            		   "<td style = 'text-align : center;'>" + excelLineList[i] + "</td>" + 
            		   "<td style = 'text-align : center;'>" + fileNames[i] + "</td></tr>";
        }
        
        tbl.html(htmltxt);
    }

    function winClose() {
        window.close();
    }                                                    
</script>
</head>
<body>
	<div id="wrapper">
		<div class="search-bg-basic">
			<ul class="basic-ul">
				<li class="basic-li bluebold11pt withinput">Shipment File</li>
			</ul>
		</div>

		<div id="datalist" class="overflow_hidden" style="display: block">
			<div class="width-100" style="padding-top: 20px; padding-left: 20px; padding-right: 20px;">
				<label class="check">Import Result</label>
			</div>
		
			<div class="download_zip">
				<ul>
					<li class="file" id="importResult"></li>
				</ul>
			</div>
			
			<div class="width-100" style="padding-top: 20px; padding-left: 20px; padding-right: 20px;">
				<label class="check">Fail List</label>
			</div>
		
			<div class="downloadbox" style="height: 200px;">
				<table class="download" id="filelist"></table>
			</div>
			<div class="overflow_hidden textalign-center">
				<em class="am_button">
					<div class="divbutton" onclick="winClose();" style="cursor: hand;">Cancel</div>
				</em>
			</div>
		</div>

		<div id="nodata" class="nodata" style="display: none">
			<ul>
				<li class="text"><fmt:message key="aimir.data.notexist" /></li>
				<li class="close"><em class="am_button">
					<div class="divbutton" onclick="winClose();" style="cursor: hand;">Cancel</div>
				</li>
			</ul>
		</div>
	</div>
</body>
</html>