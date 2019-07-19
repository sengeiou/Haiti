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
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/
  
    var $$ = $(opener.document);
    var url;

    $(document).ready(function() {
        var excelType = opener.excelType;
        var params;

        if(excelType == 1 || excelType == 8){
            params =  {
                condition : opener.contactListObj.condition,
                fmtMessage : opener.contactListObj.fmtMessage,
                filePath :'<fmt:message key="aimir.report.fileDownloadDir"/>'     
            };            
        } else if(excelType == 4) {
            params =  {
            		supplierId: opener.supplierId,
                    startDate: $$.find("#arrearsInfo input[name=startDate]").val(),
                    endDate: $$.find("#arrearsInfo input[name=endDate]").val(),
            		logoImg: opener.logoImg,
                    filePath :'<fmt:message key="aimir.report.fileDownloadDir"/>'     
                };    
        } else {
            params =  {
                vendor: $$.find("#vendor").val(),
                supplierId: opener.supplierId,
                vendorRole: opener.vendorRole,
                fromDepositGadget : opener.fromDepositGadget,
                reportType: $$.find("#depositHistory select[name=reportType]").val(),
                subType: $$.find("#depositHistory select[name=subType]").val(),
                contract: $$.find("#depositHistory input[name=contract]").val(),
                customerName: $$.find("#depositHistory input[name=customerName]").val(),
                customerNo: $$.find("#depositHistory input[name=customerId]").val(),
                meterId: $$.find("#depositHistory input[name=meterId]").val(),
                startDate: $$.find("#depositHistory input[name=startDate]").val(),
                endDate: $$.find("#depositHistory input[name=endDate]").val(),
                casherId: $$.find("#depositHistory input[name=casherId]").val(),
                locationId: $$.find("#locationId").val(),
                filePath: "<fmt:message key='aimir.report.fileDownloadDir'/>",
                logoImg: opener.logoImg
            };                        
        }

        if (excelType == 1) {
            url = "${ctx}/gadget/prepaymentMgmt/vendorChargeContractListExcelMake.do";
        } else if(excelType == 2){
            url = "${ctx}/gadget/prepaymentMgmt/vendorChargeHistoryExcelMake.do";
        } else if(excelType == 3) {
            url = "${ctx}/gadget/prepaymentMgmt/vendorChargeHistoryTotalExcelMake.do";
        } else if(excelType == 4) {
            url = "${ctx}/gadget/prepaymentMgmt/vendorChargeArrearsInfoExcelMake.do";
        } else if(excelType == 5) {
            url = "${ctx}/gadget/prepaymentMgmt/vendorChargeHistoryWithDebtExcelMake.do";
        } else if(excelType == 6) {
            url = "${ctx}/gadget/prepaymentMgmt/vendorChargeHistoryWithDebtTotalExcelMake.do";
        } else if(excelType == 7) {
            url = "${ctx}/gadget/prepaymentMgmt/vendorChargeArrearsInfoWithDebtExcelMake.do";
        } else if(excelType == 8) {
        	url = "${ctx}/gadget/prepaymentMgmt/billingBlockTariffExcelMake.do";
        } else {
            Ext.MessageBox.alert('<fmt:message key="aimir.message"/>' + ' - PopUp', '<fmt:message key="aimir.firmware.msg20"/>');
            return; 
        }         


        emergePre();

        $.post(url, params, function(json) {
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
        });
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
        for (var i = 0; i < fileNames.length; i++) {
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
        <div class="divbutton" onclick="winClose();" style="cursor:hand;">
					<fmt:message key="aimir.board.close"/>
				</div>
			</em></li>
		</ul>
	</div>

</div>

</body>
</html>