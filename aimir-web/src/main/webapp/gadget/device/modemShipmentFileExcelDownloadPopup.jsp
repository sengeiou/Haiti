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
    	var arr = getFmtMessage();
    	var supplierId = obj.supplierId;
        var filePath = obj.filePath;
        var detailType = obj.detailType;
        var model = obj.model;
        var purchaseOrder = obj.purchaseOrder; 
        
        $.post('${ctx}/gadget/device/modemShipmentExcelMake.do', 
        		{
		        	'supplierId'			: supplierId,
		        	'filePath'				: filePath,
		        	'detailType'			: detailType,
		            'model'					: model,
		            'purchaseOrder'			: purchaseOrder,
		            
		            // excel head title section
		            'msg_title'				: arr[0],
					'msg_number'			: arr[1],
					'msg_po'				: arr[2],
					'msg_type'				: arr[3],
					'msg_euiId'				: arr[4],
					'msg_gs1'				: arr[5],
					'msg_model'				: arr[6],
					'msg_hwVer'				: arr[7],
					'msg_swVer'				: arr[8],
					'msg_imei'				: arr[9],
					'msg_imsi'				: arr[10],
					'msg_iccId'				: arr[11],
					'msg_msisdn'			: arr[12],
					'msg_productionDate'	: arr[13]
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
    });
    
    function getFmtMessage() {
        var fmtMessage = new Array();
        
        fmtMessage[0] = "<fmt:message key="aimir.shipment"/>";
        fmtMessage[1] = "<fmt:message key="aimir.number"/>";
        fmtMessage[2] = "<fmt:message key="aimir.shipment.purchaseorder"/>";
        fmtMessage[3] = "Type";
        fmtMessage[4] = "EUI ID";
        fmtMessage[5] = "<fmt:message key="aimir.shipment.gs1"/>";
        fmtMessage[6] = "<fmt:message key="aimir.model"/>";
        fmtMessage[7] = "HW Version";
        fmtMessage[8] = "SW Version";
        fmtMessage[9] = "<fmt:message key="aimir.shipment.imei"/>";
        fmtMessage[10] = "<fmt:message key="aimir.shipment.imsi"/>";
        fmtMessage[11] = "<fmt:message key="aimir.shipment.iccid"/>";
        fmtMessage[12] = "MSISDN";
        fmtMessage[13] = "<fmt:message key="aimir.shipment.productiondate"/>";
        
        return fmtMessage;
    }

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
<base target="_self"></base>
</head>
<body>
	<form name="reportDownloadForm" id="reportDownloadForm" method="post" target="downFrame" style="display: none;">
		<input type="hidden" id="filePath" name="filePath" />
		<input type="hidden" id="fileName" name="fileName" />
		<input type="hidden" id="zipFileName" name="zipFileName" />
		<input type="hidden" id="realFileName" name="realFileName" />
	</form>
	<iframe name="downFrame" style="display: none;"></iframe>
	<div id="wrapper">
		<div class="search-bg-basic">
			<ul class="basic-ul">
				<li class="basic-li bluebold11pt withinput">Shipment File</li>
			</ul>
		</div>

		<div class="width-100" style="padding-top: 20px; padding-left: 20px; padding-right: 20px;">
			<label class="check">File Download</label>
		</div>

		<div id="datalist" class="overflow_hidden" style="display: block">
			<div class="download_zip">
				<ul>
					<!-- <li class="file">billingday20110418(1).zip</li> -->
					<li class="file" id="zipFile"></li>
					<li class="downbtn"><em class="am_button">
							<div class="divbutton" onclick="javascript:fileDown('zipFileName');">
								<fmt:message key="aimir.report.fileDownload" />
							</div>
					</em></li>
				</ul>
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
				<li class="close"><em class="am_button"> <!-- <div class="divbutton" onclick="javascript:window.close();" > -->
					<div class="divbutton" onclick="winClose();" style="cursor: hand;">Cancel</div>
				</li>
			</ul>
		</div>
		
	</div>
</body>
</html>