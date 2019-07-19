<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">

<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>

<%@ include file="/gadget/system/preLoading.jsp"%>

<script type="text/javascript">
	var tabs = {
		hourly : 0,
		daily : 1,
		period : 0,
		weekly : 1,
		monthly : 1,
		monthlyPeriod : 0,
		weekDaily : 0,
		seasonal : 1,
		yearly : 1
	};

	var periodType = 1;
	var reportType = 1; // 0:에너지목표관리, 1:사용량통계
	var subTabType = "";	

	//플렉스객체
    var flex;
    var flex2;

    var supplierId = "";

    /**
     * 유저 세션 정보 가져오기
     */
    $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                if(json.supplierId != ""){
                    supplierId = json.supplierId;
                }
            }
    );

    $(document).ready(function(){
    	hide();
    });

    $(function(){
        flex = getFlexObject('emsReportMaxGadget');
	});

    function getFmtMessage(){
        var fmtMessage = new Array();
        
        fmtMessage[0] = '<fmt:message key="aimir.report"/>';						// 보고서
        fmtMessage[1] = '<fmt:message key="aimir.report.usage"/>';					// 사용량 통계
        fmtMessage[2] = '<fmt:message key="aimir.report.usage.energy"/>';			// 에너지별 사용량 통계
        fmtMessage[3] = '<fmt:message key="aimir.report.usage.machinery"/>';		// 공조 에너지 사용량 통계
        fmtMessage[4] = '<fmt:message key="aimir.report.usage.electricity"/>';		// 전기 에너지 사용량 통계
        fmtMessage[5] = '<fmt:message key="aimir.report.usage.etc"/>';				// 기타 에너지 사용량 통계
        fmtMessage[6] = '<fmt:message key="aimir.report.label.lastday"/>';			// 전일대비
        fmtMessage[7] = '<fmt:message key="aimir.report.label.lastyear"/>';			// 전년 동일대비
        fmtMessage[8] = '<fmt:message key="aimir.report.label.usageRate"/>';		// 사용비중
        fmtMessage[9] = '<fmt:message key="aimir.report.label.peakUsage"/>';		// Peak 사용량
        fmtMessage[10] = '<fmt:message key="aimir.report.label.peakTime"/>';		// Peak 시간
        fmtMessage[11] = '<fmt:message key="aimir.report.label.co2Rate"/>';			// CO2 발생비중

        fmtMessage[12] = '<fmt:message key="aimir.locationUsage.day"/>';			// 일
        fmtMessage[13] = '<fmt:message key="aimir.locationUsage.week"/>';			// 주
        fmtMessage[14] = '<fmt:message key="aimir.locationUsage.month"/>';			// 월
        fmtMessage[15] = '<fmt:message key="aimir.quarter"/>';			// 분기
        fmtMessage[16] = '<fmt:message key="aimir.year1"/>';                 		// 년

        fmtMessage[17] = '<fmt:message key="aimir.item"/>';                 		// 항목
        fmtMessage[18] = '<fmt:message key="aimir.usage"/>';                 		// 사용량
        
        return fmtMessage;
    }

	function setCanvas(type) {
		if (type == "1" || type == "8") {
			$('#gridSpace').hide();
			$('#usageStaticReport').hide();			
		} else {
			$('#gridSpace').show();
			$('#usageStaticReport').show();
		}

		if (type == "3") {
			$('#_daily').click();
			periodType = 1;
			periodTypeName = "일별";
		} else if (type == "4") {
			$('#_weekly').click();
			periodType = 3;
			periodTypeName = "주별";
		} else if (type == "5") {
			$('#_monthly').click();
			periodType = 4;
			periodTypeName = "월별";
		} else if (type == "6") {
			$('#_seasonal').click();
			periodType = 9;
			periodTypeName = "분기별";
		} else if (type == "7") {
			$('#_yearly').click();
			periodType = 8;
			periodTypeName = "연별";
		}
		
		//flex.requestSendToFlex();
	}

	function changeSubTab(type) {
		subTabType = type;

		if(type == "energy") $('#reportTitle').text('<fmt:message key="aimir.report.usage.energy"/>' + ' ' + '<fmt:message key="aimir.report"/>');
		else if(type == "machinery") $('#reportTitle').text('<fmt:message key="aimir.report.usage.machinery"/>' + ' ' + '<fmt:message key="aimir.report"/>');
		else if(type == "electricity") $('#reportTitle').text('<fmt:message key="aimir.report.usage.electricity"/>' + ' ' + '<fmt:message key="aimir.report"/>');
		else if(type == "etc") $('#reportTitle').text('<fmt:message key="aimir.report.usage.etc"/>' + ' ' + '<fmt:message key="aimir.report"/>');

		flex.changeUsageChart();
	}

	function getSubTabType() {
		var condArray = new Array();
        condArray[0] = subTabType;

        return condArray;
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

	function report(){
		if(reportType == 0) {
			openEnergySavingReport();
		} else {
			openEmsReport();
		}
	}

	function searchInfo() {
		if(reportType == 0) {
			flex2.requestSendToFlex();
		} else {
			flex.requestSendToFlex();
		}
	}

	var winEmsReport;
	function openEmsReport() {
		var report = "emsReport.rptdesign"; 
        var opts="width=1010px, height=650px, left=150px, resizable=no, status=no";
        var params='';

        params = params + '&periodType=' + periodType; // 1,3,4,8,9 - 일,주,월,년,분기
        params = params + '&searchDate=' + $('#searchEndDate').val();
//        params = params + '&year=' + $('#searchYear').val(); //주별,월별,분기별,년도별일경우 사용
//        params = params + '&weekOfYear=' + $('#searchWeekOfYear').val(); //주별일경우 사용 해당년도의 1주부터 5x주
//        params = params + '&month=' + $('#searchMonth').val(); // 월별일경우 사용
        params = params + '&quarter=' + $('#seasonalSeasonCombo').val(); // 주기별일경우 사용
        params = params + '&report1=' + ($('#report1').is(':checked') ? "1" : "0");// 에너지별 사용량 보고서 , 값이 1일경우 visible else hidden
        params = params + '&report2=' + ($('#report2').is(':checked') ? "1" : "0");// 공조에너지 사용량 보고서 ,  값이 1일경우 visible else hidden
        params = params + '&report3=' + ($('#report3').is(':checked') ? "1" : "0");// 전기에너지 사용량 보고서 ,  값이 1일경우 visible else hidden
        params = params + '&report4=' + ($('#report4').is(':checked') ? "1" : "0");// 기타에너지 사용량 보고서 ,  값이 1일경우 visible else hidden
//        params = params + '&report5=1';// 이상발생통계보고서 ,  값이 1일경우 visible else hidden

		if(winEmsReport)
			winEmsReport.close();
        var localport = "<%= request.getLocalPort() %>";
        var birtURL = "/birt-viewer/frameset?__report="+report+params + "&localPort=" + localport;
		winEmsReport = window.open(birtURL, "EmsReportExcel", opts);
	}

	var winEnergySavingReport;
	function openEnergySavingReport() {
		var report = "energySaving.rptdesign"; 
        var opts="width=1010px, height=650px, left=150px, resizable=no, status=no";
        var params='';

        params = params + '&supplierId=' + supplierId;
        params = params + '&searchYear=' + $('#searchEndDate').val().substr(0,4);

        if(winEnergySavingReport)
        	winEnergySavingReport.close();
        var localport = "<%= request.getLocalPort() %>";
        var birtURL = "/birt-viewer/frameset?__report="+report+params + "&localPort=" + localport;
		winEnergySavingReport = window.open(birtURL, "EnergySavingReportExcel", opts);
	}
</script>
</head>
<body>
<div id="wrapper">
<div id="container2">
<div class="max_left ptrbl10 w280">

<div class="max_search h200 w260"><object
	classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%"
	height="198" id="emsReportTypeTreeEx">
	<param name='wmode' value='transparent' />
	<param name="movie"
		value="${ctx}/flexapp/swf/bems/emsReportTypeTree.swf" />
	<!--[if !IE]>--> <object type="application/x-shockwave-flash"
		data="${ctx}/flexapp/swf/bems/emsReportTypeTree.swf" width="100%"
		height="198" id="emsReportTypeTreeOt"> <!--<![endif]--> <!--[if !IE]>-->
	</object> <!--<![endif]--> </object></div>

<div class="clear"></div>

<div class="report ptrbl10" id="usageStaticReport">
<ul style="overflow: auto; height:50px">
	<li class="Tbu_bold"><fmt:message key="aimir.report.usage"/>&nbsp;<fmt:message key="aimir.report"/></li>
	<li style="float: left;"><%@ include
		file="/gadget/commonDateTabButtonType3.jsp"%></li>
</ul>

<ul>

	<li class="Tbk_bold11 mt20"><fmt:message key="aimir.report.type.select"/></li>
	<li class="mt2"><input type="checkbox" class="checkbox" id="report1" name="report1">
	<fmt:message key="aimir.report.usage.energy"/></li>
	<li class="mt2"><input type="checkbox" class="checkbox" id="report2" name="report2">
	<fmt:message key="aimir.report.usage.machinery"/></li>
	<li class="mt2"><input type="checkbox" class="checkbox" id="report3" name="report3">
	<fmt:message key="aimir.report.usage.electricity"/></li>
	<li class="mt2"><input type="checkbox" class="checkbox" id="report4" name="report4">
	<fmt:message key="aimir.report.usage.etc"/></li>
	<li class="mt20 ml30 w200"><em class="big_button"><a href="javascript:report();"><span
		class="big_printer printspace"></span><fmt:message key="aimir.report.print"/></a></em></li>
</ul>
</div>

</div>

<div class="Bchart m300" id="gridSpace">
<!-- tab (S) -->
<div class="bldg_sub_tab pt10">
<ul>
	<li><a href="javascript:changeSubTab('energy');" id="sub_tab1"><fmt:message key="aimir.report.usage.energy"/></a></li>
	<li><a href="javascript:changeSubTab('machinery');" id="sub_tab2"><fmt:message key="aimir.report.usage.machinery"/></a></li>
	<li><a href="javascript:changeSubTab('electricity');" id="sub_tab3"><fmt:message key="aimir.report.usage.electricity"/></a></li>
	<li><a href="javascript:changeSubTab('etc');" id="sub_tab2"><fmt:message key="aimir.report.usage.etc"/></a></li>	
</ul>
</div>
<!-- tab (E) -->
<div class="allM20">
<p class="borderBottom3 Tbk_bold14 t_center" id="reportTitle"><fmt:message key="aimir.report.usage.energy"/>&nbsp;<fmt:message key="aimir.report"/></p>
</div>
<div class="Bchart sideM20"
	style="border: 1px solid #B4D3F0; height: 500px">
	<object
		classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%"
		height="100%" id="emsReportMaxGadgetEx">
		<param name='wmode' value='transparent' />
		<param name="movie"
			value="${ctx}/flexapp/swf/bems/emsReportMaxGadget.swf" />
		<!--[if !IE]>--> <object type="application/x-shockwave-flash"
			data="${ctx}/flexapp/swf/bems/emsReportMaxGadget.swf" width="100%"
			height="150" id="SupplierLocationOt"> <!--<![endif]--> <!--[if !IE]>-->
		</object> <!--<![endif]--> </object>
</div>
</div>

</div>
</div>
</body>
</html>