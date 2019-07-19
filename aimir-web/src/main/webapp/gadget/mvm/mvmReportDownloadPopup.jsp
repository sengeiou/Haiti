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

        var flex;
        var supplierId = "";

        /**
         * 유저 세션 정보 가져오기
         */
        $.getJSON('${ctx}/common/getUserInfo.dbo',
                function(json) {
                    if(json.supplierId != ""){
                        supplierId = json.supplierId;
                    }
                }
        );

        $(document).ready(function(){

        	var obj = window.opener.obj;
        	
           	emergePre();
           	
           	if(obj.excelType == "details") {
           		obj.fmtMessage = getDetailsFmtMessage();
           	}
           	
       		$.post("${ctx}/gadget/mvm/getMeteringDataReport.do",
                    {"condition"	: obj.condition,
                	"fmtMessage"	: obj.fmtMessage,
                	"excelType"		: obj.excelType,
                	"filePath"		:'<fmt:message key="aimir.report.fileDownloadDir"/>' },
                     function(json) {
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

        function fileDown(fname) {
        	$("#fileName").val($("#"+fname).val());
            var url = "${ctx}/common/fileDownload.do";
            var downform = document.getElementsByName("reportDownloadForm")[0];

            downform.action = url;
            downform.submit();
        }

        function getDetailsFmtMessage(){
            var fmtMessage = new Array();
            var cnt = 0;
            
            fmtMessage[cnt++] = '<fmt:message key="aimir.date.yesterday" />' + '(' + '<fmt:message key="aimir.facilityMgmt.beforeMonth" />' + ')';      // 전일(전월)
            fmtMessage[cnt++] = '<fmt:message key="aimir.searchDate"/>';                    // 조회날짜
            fmtMessage[cnt++] = '<fmt:message key="aimir.readingDay"/>';                    // 검침일
            fmtMessage[cnt++] = '<fmt:message key="aimir.customername"/>';                  // 고객명
            fmtMessage[cnt++] = '<fmt:message key="aimir.buildingMgmt.contractNumber"/>';   // 계약번호
            fmtMessage[cnt++] = '<fmt:message key="aimir.meterid"/>';                       // 미터번호
            fmtMessage[cnt++] = '<fmt:message key="aimir.contract.wattage"/>';              // 계약전력
            fmtMessage[cnt++] = '<fmt:message key="aimir.contract.tariff.type"/>';          // 계약종별
            fmtMessage[cnt++] = '<fmt:message key="aimir.location"/>';                      // 지역
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.actImp"/>';                 // Active Import
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.actExp"/>';                 // Active Export
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.reactLagImp"/>';            // Reactive Lag Import
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.reactLeadImp"/>';           // Reactive Lead Import
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.reactLagExp"/>';            // Reactive Lag Export
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.reactLeadExp"/>';           // Reactive Lead Export
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.totEnergy"/>';              // Total Energy
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.energy"/>';                 // Energy
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.totDemandTime"/>';          // Total Demand and Time
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.maxDemandTime"/>';          // Max Demand and Time
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.totCummDemand"/>';          // Total Cummulative Demand
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.cummDemand"/>';             // Cummulative Demand
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.rate1"/>';                  // Rate 1
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.rate2"/>';                  // Rate 2
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.rate3"/>';                  // Rate 3
            fmtMessage[cnt++] = '<fmt:message key="aimir.number"/>';                        // 번호
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.fileDownloadDir"/>';        // File Path
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.kVAh1"/>';                  // kVAh 1

            return fmtMessage;
        }
        
    
        function getParams() {
    
            var condArray = new Array();
            condArray[0] = periodType;
            condArray[1] = $('#searchEndDate').val();
            condArray[2] = $('#seasonalSeasonCombo').val();
            condArray[3] = supplierId;
            condArray[4] = $('#searchEndDate').val().substr(0,4);
            
            return condArray;
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