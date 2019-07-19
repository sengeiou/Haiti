<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<meta content='IE=EmulateIE8' http-equiv='X-UA-Compatible'/> 
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">

<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>

<%@ include file="/gadget/system/preLoading.jsp"%>

<style type="text/css" media="screen">

	.clear {
		width: 0px;
		height: 0px;
		clear: both;
		display: none;
	}

	#emsSelect li {
		height: 15px;
	}
	/* commonDateTabButtonType3.jsp의 css 수정 */
	#usageStaticReport div#datetab {
		padding: 0px;
	}
	
	#usageStaticReport div#datetab div {
		padding-right: 0px;
	}
	/* commonDateTabButtonType3.jsp의 inline css 수정 */
	#usageStaticReport input#weeklyYearCombo_input.selectbox { 
		width: 42px !important;
	}
	#usageStaticReport input#weeklyMonthCombo_input.selectbox { 
		width: 27px !important;
	}
	#usageStaticReport input#weeklyWeekCombo_input.selectbox { 
		width: 27px !important;
	}
	#usageStaticReport input#monthlyYearCombo_input.selectbox { 
		width: 42px !important;
	}
	#usageStaticReport input#monthlyMonthCombo_input.selectbox { 
		width: 27px !important;
	}		
</style>
<script type="text/javascript">

	var tabs = {
		hourly : 0,
		daily : 1,
		period : 1,
		weekly : 1,
		monthly : 1,
		monthlyPeriod : 0,
		weekDaily : 0,
		seasonal : 1,
		yearly : 1
	};
    var tabNames = {};

	var periodType = 1;
	var reportType = 1; // 0:에너지목표관리, 1:사용량통계
    var supplierId = "";
	var locationId;
	var energyType;
	
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
        fmtMessage[19] = '<fmt:message key="aimir.goal.amount"/>';                 	// 목표량

        fmtMessage[20] = '<fmt:message key="aimir.locationUsage.electricity"/>';    // 전기
        fmtMessage[21] = '<fmt:message key="aimir.locationUsage.gas"/>';            // 가스
        fmtMessage[22] = '<fmt:message key="aimir.locationUsage.water"/>';          // 수도
				
        fmtMessage[23] = '<fmt:message key="aimir.report.energySaving"/>';          // 에너지 목표관리
        fmtMessage[24] = '<fmt:message key="aimir.report.daily"/>';          		// 일간
        fmtMessage[25] = '<fmt:message key="aimir.report.weekly"/>';         		// 주간
        fmtMessage[26] = '<fmt:message key="aimir.report.monthly"/>';          		// 월간
        fmtMessage[27] = '<fmt:message key="aimir.report.yearly"/>';          		// 연간
        fmtMessage[28] = '<fmt:message key="aimir.quarterly"/>';          			// 분기
        
        fmtMessage[29] = '<fmt:message key="aimir.bems.report.usageResult"/>';		// 사용실적
        fmtMessage[30] = '<fmt:message key="aimir.bems.report.billList"/>';			// 요금청구 금액내역
        fmtMessage[31] = '<fmt:message key="aimir.bems.locationUsage.oil"/>';		// 등유
        fmtMessage[32] = '<fmt:message key="aimir.bems.report.zone"/>';				// zone별 사용량
        fmtMessage[33] = '<fmt:message key="aimir.bems.report.location"/>';				// 위치별 사용량

        return fmtMessage;
    }

	function setCanvas(type) {

		console.log(type);
		// 에너지 목표 관리 보고서
		if (type == "1") {
			$('#_yearly').click();

			// 사용량 통계 보고서	
		} else if (type == "2" || type == "3") {
			$('#_daily').click();
			periodType = DateType.DAILY;
		} else if (type == "4") {
			$('#_weekly').click();
			periodType = DateType.WEEKLY;
		} else if (type == "5") {
			$('#_monthly').click();
			periodType = DateType.MONTHLY;
		} else if (type == "6") {
			$('#_seasonal').click();
			periodType = DateType.QUARTERLY;
		} else if (type == "7") {
			$('#_yearly').click();
			periodType = DateType.YEARLY;

			// 요금청구서	
		} else if (type == "8" || type == "9" || type == "10" || type == "11"
				|| type == "12") {
			$('#_monthly').click();
			periodType = DateType.MONTHLY;
			if (type == "8" || type == "9") {
				energyType = "EM";
			} else if (type == "10") {
				energyType = "GM";
			} else if (type == "11") {
				energyType = "WM";
			} else if (type == "12") {
				energyType = "HM";
			}

			// 전기 사용 실적			
		} else if (type == "13") {
			$('#_yearly').click();
			periodType = DateType.YEARLY;
			// zone 별 사용량 통계
		} else if (type == "14") {
			$('#_period').click();
			periodType = DateType.PERIOD;
			// 위치별 사용량 통계
		} else if (type == "15") {
			$('#_period').click();
			periodType = DateType.PERIOD;
		}

		//에너지 목표관리 보고서
		if (type == "1") {
			$('#emsSelect').hide();
			$('#reportTitle')
					.text(
							'<fmt:message key="aimir.report.energySaving"/> <fmt:message key="aimir.report"/>');
			$('#gridSpace').hide();
			$('#energyChartSpace').show();
			reportType = 0;

			//사용량 통계 보고서			
		} else if (type == "2" || type == "3" || type == "4" || type == "5"
				|| type == "6" || type == "7") {
			$('#emsSelect').show();
			$('#reportTitle')
					.text(
							'<fmt:message key="aimir.report.usage"/> <fmt:message key="aimir.report"/>');
			$('#energyChartSpace').hide();
			$('#gridSpace').show();
			reportType = 1;

			//요금청구 금액내역	
		} else if (type == "8" || type == "9" || type == "10" || type == "11"
				|| type == "12") {
			$('#emsSelect').hide();
			$('#reportTitle').text(
					'<fmt:message key="aimir.bems.report.billList"/>');
			$('#energyChartSpace').hide();
			$('#gridSpace').hide();
			reportType = 2;
			//전기 사용실적 보고서
		} else if (type == "13") {
			$('#emsSelect').hide();
			$('#reportTitle')
					.text(
							'<fmt:message key="aimir.electricity"/> '
									+ '<fmt:message key="aimir.bems.report.usageResult"/> '
									+ '<fmt:message key="aimir.report"/>');
			$('#energyChartSpace').hide();
			$('#gridSpace').hide();
			reportType = 3;
			//zone별 사용량 보고서
		} else if (type == "14") {
			$('#emsSelect').hide();
			$('#reportTitle').text(
					'<fmt:message key="aimir.bems.report.zone"/>'
							+ '<fmt:message key="aimir.report"/>');
			$('#energyChartSpace').hide();
			$('#gridSpace').hide();
			reportType = 4;
			//위치별 사용량 보고서
		} else if (type == "15") {
			$('#emsSelect').hide();
			$('#reportTitle').text(
					'<fmt:message key="aimir.bems.report.location"/>'
							+ '<fmt:message key="aimir.report"/>');
			$('#energyChartSpace').hide();
			$('#gridSpace').hide();
			reportType = 5;
		}

	}

	function getParams() {

		var condArray = new Array();
		condArray[0] = periodType;
		condArray[1] = $('#searchEndDate').val();
		condArray[2] = $('#seasonalSeasonCombo').val();
		condArray[3] = supplierId;
		condArray[4] = $('#searchEndDate').val().substr(0, 4);

		return condArray;

	}

	function report() {
		if (reportType == 0) {
			openEnergySavingReport();
		} else if (reportType == 1) {
			openEmsReport();
		} else if (reportType == 2) {
			openBillReport();
		} else if (reportType == 3) {
			openEelecUsageReport();
		} else if (reportType == 4) {
			openZoneUsageReport();
		} else if (reportType == 5) {
			openLocationUsageReport();
		}
	}

	function searchInfo() {
		if (reportType == 0) {
			getFlexObject('energySavingReportMiniGadget').requestSendToFlex();
		} else if (reportType == 1) {
			getFlexObject('emsReportMiniGadget').requestSendToFlex();
		}
	}

	// 에너지별 사용량 통계 보고서
	var winEmsReport;
	function openEmsReport() {
		var report = "emsReport.rptdesign";
		var opts = "width=1010px, height=650px, left=150px, resizable=no, status=no";
		var params = '';
		params = params + '&periodType=' + periodType; // 1,3,4,8,9 - 일,주,월,년,분기
		params = params + '&searchDate=' + $('#searchEndDate').val();
		//        params = params + '&year=' + $('#searchYear').val(); //주별,월별,분기별,년도별일경우 사용
		//        params = params + '&weekOfYear=' + $('#searchWeekOfYear').val(); //주별일경우 사용 해당년도의 1주부터 5x주
		//        params = params + '&month=' + $('#searchMonth').val(); // 월별일경우 사용
		params = params + '&quarter=' + $('#seasonalSeasonCombo').val(); // 주기별일경우 사용
		params = params + '&report1='
				+ ($('#report1').is(':checked') ? "1" : "0");// 에너지별 사용량 보고서 , 값이 1일경우 visible else hidden
		params = params + '&report2='
				+ ($('#report2').is(':checked') ? "1" : "0");// 공조에너지 사용량 보고서 ,  값이 1일경우 visible else hidden
		params = params + '&report3='
				+ ($('#report3').is(':checked') ? "1" : "0");// 전기에너지 사용량 보고서 ,  값이 1일경우 visible else hidden
		params = params + '&report4='
				+ ($('#report4').is(':checked') ? "1" : "0");// 기타에너지 사용량 보고서 ,  값이 1일경우 visible else hidden
		//        params = params + '&report5=1';// 이상발생통계보고서 ,  값이 1일경우 visible else hidden

		if(winEmsReport)
			winEmsReport.close(); 
        var localport = "<%= request.getLocalPort() %>";
        var birtURL = "/birt-viewer/frameset?__report="+report+params + "&localPort=" + localport;
		winEmsReport = window.open(birtURL, "emsReportExcel", opts);
	}

	// 에너지 목표관리 보고서
	var winEnergySavingReport;
	function openEnergySavingReport() {
		var report = "energySaving.rptdesign";
		var opts = "width=1010px, height=650px, left=150px, resizable=no, status=no";
		var params = '';

		params = params + '&supplierId=' + supplierId;
		params = params + '&searchYear='
				+ $('#searchEndDate').val().substr(0, 4);
		params = params + '&temp='
				+ '<fmt:message key="aimir.locationUsage.gasPay"/>';

		if(winEnergySavingReport)
			winEnergySavingReport.close();
        var localport = "<%= request.getLocalPort() %>";
        var birtURL = "/birt-viewer/frameset?__report="+report+params + "&localPort=" + localport;
		winEnergySavingReport = window.open(birtURL,"EnergySavingReportExcel", opts);
	}

	// 요구청구 금액내역 보고서
	var winBillingReport;
	function openBillReport() {
		var searchDate = $('#searchEndDate').val().substring(0, 6);
		var report = "emsEnergyCharge.rptdesign";
		var opts = "width=1010px, height=650px, left=150px, resizable=no, status=no";
		var params = "&searchDate=" + searchDate + "&periodType=" + periodType
				+ "&energyType=" + energyType;

		if(winBillingReport)
			winBillingReport.close();
        var localport = "<%= request.getLocalPort() %>";
        var birtURL = "/birt-viewer/frameset?__report="+report+params + "&localPort=" + localport;
		winBillingReport = window.open(birtURL, "BillingReportExcel", opts);
	}

	// 전기 사용실적 보고서
	var winEleUsageReport;
	function openEelecUsageReport() {
		var report = "emsUsageReport.rptdesign";
		var opts = "width=1010px, height=650px, left=150px, resizable=no, status=no";
		var params = "&searchYear=" + $('#searchEndDate').val().substr(0, 4)
				+ "&energyType=EM" + "&supplierId=" + supplierId;

		if (locationId) {
			params += "&locationId=" + locationId;
		}

		if(winEleUsageReport)
			winEleUsageReport.close();
        var localport = "<%= request.getLocalPort() %>";
        var birtURL = "/birt-viewer/frameset?__report="+report+params + "&localPort=" + localport;
		winEleUsageReport = window.open(birtURL, "ElecUsageReportExcel", opts);
	}

	// Zone 별 사용량 보고서
	var winZoneUsageReport;
	function openZoneUsageReport() {
		var report = "zoneUsageReport.rptdesign";
		var opts = "width=1010px, height=650px, left=150px, resizable=no, status=no";
		var params = "&searchStartDate=" + $('#searchStartDate').val()
				+ "&searchEndDate=" + $('#searchEndDate').val()
				+ "&supplierId=" + supplierId;

		if(winZoneUsageReport)
			winZoneUsageReport.close();
        var localport = "<%= request.getLocalPort() %>";
        var birtURL = "/birt-viewer/frameset?__report="+report+params + "&localPort=" + localport;
		winZoneUsageReport = window.open(birtURL, "ZoneUsageReportExcel", opts);
	}

	// 위치별 사용량 보고서
	var winLocationUsageReport;
	function openLocationUsageReport() {
		var report = "locationUsageReport.rptdesign";
		var opts = "width=1010px, height=650px, left=150px, resizable=no, status=no";
		var params = "&searchStartDate=" + $('#searchStartDate').val()
				+ "&searchEndDate=" + $('#searchEndDate').val()
				+ "&supplierId=" + supplierId;

		if(winLocationUsageReport)
			winLocationUsageReport.close();
        var localport = "<%= request.getLocalPort() %>";
        var birtURL = "/birt-viewer/frameset?__report="+report+params + "&localPort=" + localport;
		winLocationUsageReport = window.open(birtURL, "LocationUsageReportExcel", opts);
	}
</script>
</head>
<body>

<div id="wrapper">
 <div id="bldgNavi">
		<!-- 검색 및 정보 (S) -->	
    <div class="topAll">
		<div class="search h180" style="width: 170px; float:left; overflow: visible;">
			<object
				classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%"
				height="180" id="emsReportTypeTreeEx">
				<param name='wmode' value='transparent' />
				<param name="movie"
					value="${ctx}/flexapp/swf/bems/emsReportTypeTree.swf" />
				<!--[if !IE]>--> <object type="application/x-shockwave-flash"
					data="${ctx}/flexapp/swf/bems/emsReportTypeTree.swf" width="100%"
					height="150" id="emsReportTypeTreeOt"> <!--<![endif]--> <!--[if !IE]>-->
				</object> <!--<![endif]--> </object>
        </div>
			
		<div class="info ml170" id="usageStaticReport" style="float:left; width:205px;margin-left:5px;">
			<div style="height:60px;">
				<ul >
					<li class="Tbu_bold" id="reportTitle"><fmt:message key="aimir.report.usage"/>&nbsp;<fmt:message key="aimir.report"/></li>
					<li ><%@ include file="/gadget/commonDateTabButtonType10.jsp" %></li>
				</ul>
			</div>
			<div style="height: 90px;margin-bottom: 6px;">
				<ul id="emsSelect" >	
					<li class="Tbk_bold11 pt10"><fmt:message key="aimir.report.type.select"/></li>
					<li class="mt2"><input type="checkbox" class="checkbox" id="report1" name="report1"><fmt:message key="aimir.report.usage.energy"/></li>
					<li class="mt2"><input type="checkbox" class="checkbox" id="report2" name="report2"><fmt:message key="aimir.report.usage.machinery"/></li>
					<li class="mt2"><input type="checkbox" class="checkbox" id="report3" name="report3"><fmt:message key="aimir.report.usage.electricity"/></li>
					<li class="mt2"><input type="checkbox" class="checkbox" id="report4" name="report4"><fmt:message key="aimir.report.usage.etc"/></li>
					<!-- <li class="mt2"><input type="checkbox" class="checkbox"> 이상 발생 통계</li> -->
				</ul>
			</div>
			<div>
				<ul>
					<li class="btn_right"><em class="bems_button"><a href="javascript:report();"><fmt:message key="aimir.report.print"/></a></em></li>					
				</ul>
			</div>
   		</div>
   		<div class="clear"></div>
   	</div>
  	<!-- 검색 및 정보 (E) -->	
      
	<div class="Bchart" id="gridSpace" >
      	<object
			classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%"
			height="155" id="emsReportMiniGadgetEx">
			<param name='wmode' value='transparent' />
			<param name="movie"
				value="${ctx}/flexapp/swf/bems/emsReportMiniGadget.swf" />
			<!--[if !IE]>--> <object type="application/x-shockwave-flash"
				data="${ctx}/flexapp/swf/bems/emsReportMiniGadget.swf" width="100%"
				height="155" id="emsReportMiniGadgetOt"> <!--<![endif]--> <!--[if !IE]>-->
			</object> <!--<![endif]--> </object>
	</div>
	<div class="Bchart" id="energyChartSpace" style="display:none;">
      	<object
			classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%"
			height="155" id="energySavingReportMiniGadgetEx">
			<param name='wmode' value='transparent' />
			<param name="movie"
				value="${ctx}/flexapp/swf/bems/energySavingReportMiniGadget.swf" />
			<!--[if !IE]>--> <object type="application/x-shockwave-flash"
				data="${ctx}/flexapp/swf/bems/energySavingReportMiniGadget.swf" width="100%"
				height="155" id="energySavingReportMiniGadgetOt"> <!--<![endif]--> <!--[if !IE]>-->
			</object> <!--<![endif]--> </object>
	</div>
 </div>
</div>
</body>
</html>