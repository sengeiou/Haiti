<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title></title>

<%@ include file="/gadget/system/preLoading.jsp"%>
    
<link href="${ctx}/css/style_hems.css" rel="stylesheet" type="text/css">
<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>

<script type="text/javascript">

	var supplierId = "${supplierId}";

	$(document).ready(function(){

		if("${isNotService}" == "true") {  // 해당 가젯에 대한 권한이 없을때
            $("#wrapper").hide();
            hide();
            return;
        } else { // 해당 가젯에 대한 권한이 있을때
            $("#isNotService").hide();
        }

		$("#contractNumber").selectbox();

		$("#summaryDivTabId").click(function() { displayDivTab("summaryDivTab"); });
		$("#periodDivTabId").click(function() { displayDivTab("periodDivTab"); });

		getContract();

		searchDivInit();

        displayDivTab("summaryDivTab");
	});

	// 달력 부분 시작
	var maxDay;
		
	var searchDivInit = function() {

		$.ajaxSetup({
		    async: false
		});

		var contractId = $("#contractNumber").val();
		var maxDate;
		
        $.getJSON("${ctx}/gadget/energyConsumptionSearch/getMaxDay.do"
                ,{"contractId" : contractId}
                ,function(result) {
                    
                	maxDay = result.maxDay;
					//maxDate = result.maxDate.split(" ")[0];
					maxDate = result.maxDate;
                });

		var now = new Date();
		var last = new Date(Number(maxDay.substring(0,4)), Number(maxDay.substring(4,6)) - 1, Number(maxDay.substring(6,8)));
		
		var diff = Math.ceil(last.valueOf()/(24*60*60*1000) - now.valueOf()/(24*60*60*1000));

		$(function() { $("#dailyLeft")   .bind("click",  function(event) { dailyArrow($("#dailyStartDate").val(),-1); } ); });
        $(function() { $("#dailyRight")  .bind("click",  function(event) { dailyArrow($("#dailyStartDate").val(),1); } ); });
        $(function() { $("#monthlyLeft") .bind("click",  function(event) { monthlyArrow(-1); } ); });
        $(function() { $("#monthlyRight").bind("click",  function(event) { monthlyArrow(1 ); } ); });
        $(function() { $("#yearlyLeft")  .bind("click",  function(event) { yearlyArrow(-1); } ); });
        $(function() { $("#yearlyRight") .bind("click",  function(event) { yearlyArrow(1 ); } ); });

        $(function() { $("#dailyStartDate")   .bind("change", function(event) { getSearchDate(DateType.HOURLY); } ); });
        $(function() { $("#monthlyYearCombo") .bind("change", function(event) { getMonthlyMonthCombo(""); } ); });
        $(function() { $("#monthlyMonthCombo").bind("change", function(event) { getSearchDate(DateType.DAILY); } ); });
        $(function() { $("#yearlyYearCombo")  .bind("change", function(event) { getSearchDate(DateType.MONTHLY); } ); });

        var locDateFormat = "yymmdd";

        $("#dailyStartDate").datepicker({maxDate:diff,showOn: "button", buttonImage: "${ctx}/themes/images/default/setting/calendar.gif", buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );

        $.getJSON("${ctx}/gadget/energyConsumptionSearch/getYear.do"
                ,{"maxDay" : maxDay}
                ,function(json) {
                    
                	var startYear = json.year;
                    var endYear = json.currYear;
                     
                    $("#yearlyYearCombo").numericOptions({from:startYear,to:endYear,selectedIndex:9});
                    $("#yearlyYearCombo").selectbox();
                     
                    $("#monthlyYearCombo").numericOptions({from:startYear,to:endYear,selectedIndex:9});
                    $("#monthlyYearCombo").selectbox();
                });

        getMonthlyMonthCombo("", true); // 월 selectBox 내용을 채운다.

		dailyArrow(maxDate, 0, true);
		
        $("#searchDateType").selectbox();
        
        $.ajaxSetup({
            async: true
        });
	};
	
    var modifyDate = function(setDate, inst) {
        
        var dateId = "#" + inst.id;

        $.getJSON("${ctx}/common/convertLocalDateByMediumFormat.do"
                ,{"dbDate" : setDate, "supplierId" : supplierId}
                ,function(json) {
                    
                    $(dateId).val(json.localDate);
                    $(dateId).trigger("change");
                });
    };

    var dailyArrow = function(bfDate, val, flag) {

        $.getJSON("${ctx}/gadget/energyConsumptionSearch/getDate.do"
                ,{"searchDate" : bfDate, "addVal" : val, "supplierId" : supplierId, "maxDay" : maxDay}
                ,function(json) {
                    
                    $("#dailyStartDate").val(json.searchDate);

                   	getSearchDate(DateType.HOURLY, flag);
                });
    };

    var monthlyArrow = function(val, flag) {
        
        $.getJSON("${ctx}/gadget/energyConsumptionSearch/getYearMonth.do"
                ,{"year" : $("#monthlyYearCombo").val(), "month" : $("#monthlyMonthCombo").val(), "addVal" : val, "maxDay" : maxDay}
                ,function(json) {
                    
                    $("#monthlyYearCombo").val(json.year);
                    $("#monthlyYearCombo").selectbox();
                    
                    getMonthlyMonthCombo(json.month, flag);
                });
    };

    var yearlyArrow = function(val, flag) {
        
        $.getJSON("${ctx}/gadget/energyConsumptionSearch/getYearAddVal.do"
                ,{"year" : $("#yearlyYearCombo").val(), "addVal" : val, "maxDay" : maxDay}
                ,function(json) {

                    var targetYear = json.targetYear;
                    var startYear = json.year;
                    var endYear = json.currYear;

                    $("#yearlyYearCombo").numericOptions({from:startYear,to:endYear,selectedIndex:9});
                    $("#yearlyYearCombo").val(targetYear);
                    $("#yearlyYearCombo").selectbox();

                    getSearchDate(DateType.MONTHLY, flag);
                });
    };

    var getMonthlyMonthCombo = function(monthVal, flag) {
        
        $.getJSON("${ctx}/gadget/energyConsumptionSearch/getMonth.do"
                ,{"year" : $("#monthlyYearCombo").val(), "maxDay" : maxDay}
                ,function(json) {
                    
                    var prevMonth = $("#monthlyMonthCombo").val();
                    
                    $("#monthlyMonthCombo").emptySelect();
                    
                    if( prevMonth == ""
                        || prevMonth == null
                        || Number(prevMonth) > Number(json.monthCount) ) {
                        
                        prevMonth = json.monthCount;
                    }
                    
                    var idx = Number(prevMonth) - 1;
                    
                    $("#monthlyMonthCombo").numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    if( monthVal != null && monthVal != "" ){
                        
                        $("#monthlyMonthCombo").val(monthVal);
                    }
                    
                    $("#monthlyMonthCombo").selectbox();

                   	getSearchDate(DateType.DAILY, flag);
                });
    };

    var getSearchDate = function(_dateType, flag) {

		if(DateType.HOURLY == _dateType) {

        	$("#searchStartDate").val($("#dailyStartDate").val());
            $("#searchEndDate").val($("#dailyStartDate").val());
            convertSearchDate(flag);

            $("#daily").show();
            $("#monthly").hide();
            $("#yearly").hide();
		} else if(DateType.DAILY == _dateType) {
            $.getJSON("${ctx}/common/getMonthPeriodByMediumFormat.do"
                    ,{"year" : $("#monthlyYearCombo").val(), "month":$("#monthlyMonthCombo").val(), "supplierId" : supplierId}
                    ,function(json) {
                        $("#searchStartDate").val(json.startDate);
                        $("#searchEndDate").val(json.endDate);

                        convertSearchDate(flag);
                    });


            $("#daily").hide();
            $("#monthly").show();
            $("#yearly").hide();
        } else if(DateType.MONTHLY == _dateType) {
            
            $.getJSON("${ctx}/common/getYearPeriodByMediumFormat.do"
                    ,{"year" : $("#yearlyYearCombo").val(), "supplierId" : supplierId}
                    ,function(json) {
                        $("#searchStartDate").val(json.startDate);
                        $("#searchEndDate").val(json.endDate);

                        convertSearchDate(flag);
                    });

            $("#daily").hide();
            $("#monthly").hide();
            $("#yearly").show();
        }

    };

    var convertSearchDate = function(flag) {
        $.getJSON("${ctx}/common/convertSearchDateByMediumFormat.do"
                ,{"searchStartDate" : $("#searchStartDate").val(), "searchEndDate" : $("#searchEndDate").val(), "supplierId" : supplierId}
                ,function(json) {
                    
                    $("#searchStartDate").val(json.searchStartDate);
                    $("#searchEndDate").val(json.searchEndDate);

                    if (flag == null) {

      	   				searchTab();
                	}
                });
    };

    var changeDateType = function() {
        
        var selectedDateType = $("#searchDateType").val();

        if( "0" == selectedDateType ){
            
            getSearchDate(DateType.HOURLY);
        }else if( "1" == selectedDateType ){
            
            getSearchDate(DateType.DAILY);
            
        }else if( "4" == selectedDateType ){

            getSearchDate(DateType.MONTHLY);
        }
    };
    // 달력 부분 끝
    
    var divTabArray = [ "summaryDivTab", "periodDivTab" ];
    var divTabArrayLength = divTabArray.length;
    
    var displayDivTab = function(_currentDivTab) {
        
        for ( var i = 0; i < divTabArrayLength; i++) {

            if (_currentDivTab == divTabArray[i]) {
                
                $("#" + divTabArray[i]).show();
                $("#" + divTabArray[i] + "Id").addClass("tabcurrent");

                if ( i == 0 ) {

                	$("#searchDiv").hide();
                } else {

                    $("#searchDiv").show();
                }

                searchTab(i);
            } else {
                
                $("#" + divTabArray[i]).hide();
                $("#" + divTabArray[i] + "Id").removeClass("tabcurrent");
            }
        }
    };

    var changeContract = function() {

    	getContract();

  	   	searchTab();
    };

    var searchTab = function(tabSeq) {
		
		emergePre();

		if (tabSeq == null) {

	        for (var i = 0; i < divTabArrayLength; i++) {

	   			if ($("#" + divTabArray[i]).css( "display" ) != "none") {

	   				tabSeq = i;
	   			}
	        }
		}

    	switch (tabSeq) {

    		case 0 :
    			getSummaryDivData();
        		break;

    		case 1 :
				getPeriodDivData();
        		break;

        	default :
            	break;
    	}
    };
    
    var getContract = function() {

    	var params = {
				"contractId" : $("#contractNumber").val()
		};

		$.getJSON("${ctx}/gadget/energyConsumptionSearch/getContract.do",
                params,
            	function(result) {

            		//$("#locationTd").text(result.location);
            		//$("#tariffTd").text(result.tariffType);
            		//$("#statusTd").text(result.status);
            		//$("#dateTd").text(result.date);

            		supplierId = result.contract.supplier;
            	}
            );
    };

    var getSummaryDivData = function() {

		var params = {
				"contractId" : $("#contractNumber").val()
		};

		$.getJSON("${ctx}/gadget/energyConsumptionSearch/getSummaryDivData.do",
                params,
            	function(result) {

                    $("#usage").val(result.usage);
                    $("#usageFee").val(result.usageFee);
                    $("#co2formula").val(result.co2formula);
                    $("#incentive").val(result.incentive);
					$("#title").html("<span class='icon_title_blue'></span>" + result.fommatDay + " <fmt:message key='aimir.hems.label.gasUseInfo'/>");

            		getMonthChart(result.maxDay);
            	}
            );
    };
    
	var monthChartData;
	
    var getMonthChart = function(basicDay) {
		
    	var params = {
    			"yAxisValuesStep" : 2,
    			"chartLeftMargin" : 0,
    			"chartRightMargin" : 0,
    			"chartTopMargin" : 0,
    			"chartBottomMargin" : 0,
                "yaxisname": "<fmt:message key='aimir.hems.label.usageFeeSymbol'/>",  
    	    	"basicDay" : basicDay,
				"contractId" : $("#contractNumber").val(),
				"color" : fChartColor_CompareGas,
				"chartType" : fChartMulti,
				"formatNumberScale" : 0,
				"trandlineText" : "<fmt:message key='aimir.hems.savings.goal'/>",
				"label" : ["<fmt:message key='aimir.hems.label.averageUsageFee'/>", "<fmt:message key='aimir.hems.label.lastUsageFee'/>", "<fmt:message key='aimir.hems.label.basicUsageFee'/>"]
		};
		
		$.extend(params, json_fChartStyle_StColumn3D_nobg);

        $.post("${ctx}/gadget/energyConsumptionSearch/getMonthChart.do",
                params,
            	function(result) {

               		//var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "myChartId", $("#monthChart").width(), "300", "0", "0" );
               		var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "myChartId", $("#monthChart").width(), "240", "0", "0" );
               		myChart.setJSONData(result);
               		myChart.setTransparent("transparent");
               		myChart.render("monthChart");

               		monthChartData = result;

                    hide();
               		                	
                    return;
            	},
            	"json"
            );
    };

	var chartName;
	
    var getPeriodDivData = function() {

		var textParams;
		var textUrl;
		var chartParams;
		var chartUrl;
		var chartCommonParams = {
                "chartLeftMargin" : 0,
                "chartRightMargin" : 0,
                "chartTopMargin" : 10,
                "chartBottomMargin" : 0,
                "showValues" : 0,
                "labelDisplay" : "WRAP",
                "labelStep" : 2,
                "moneyText" : "<fmt:message key='aimir.price.unit'/>",
                "contractId" : $("#contractNumber").val(),
                "formatNumberScale" : 0,
                "color" : fChartColor_hemsGas
		};

        var selectedDateType = $("#searchDateType").val();

        if( "0" == selectedDateType ){

            // 텍스트 박스 관련 파라메타 설정
        	textParams = {
        	    	"basicDay" : $("#searchStartDate").val(),
    				"contractId" : $("#contractNumber").val()
    		};
        	textUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodTimeText.do";
        	
            // 챠트 생성 파라메타 설정
        	chartParams = {
                    "PYAxisName": "<fmt:message key='aimir.usage'/>(m3)",   
                    "SYAxisName": "<fmt:message key='aimir.temperature'/>(℃)",                     
                    //"useRoundEdges" : 1,               	
        	    	"basicDay" : $("#searchStartDate").val(),
        	    	"toolText" : ["<fmt:message key='aimir.date'/>", "<fmt:message key='aimir.locationUsage.usage'/>", "㎥", "<fmt:message key='aimir.averageusage'/>"]
    		};
    		$.extend(chartParams, chartCommonParams, json_fChartStyle_Column2D_nobg);
        	chartUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodTimeChart.do";
            chartName = "MSCombiDY2D.swf";
        }else if( "1" == selectedDateType ){

            // 텍스트 박스 관련 파라메타 설정
        	textParams = {
        	    	"basicDay" : $("#searchStartDate").val().substr(0,6),
    				"contractId" : $("#contractNumber").val()
    		};
        	textUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodDayText.do";

        	// 챠트 생성 파라메타 설정
        	chartParams = {
                    "PYAxisName": "<fmt:message key='aimir.hems.label.usageFeeSymbol'/>",   
                    "SYAxisName": "<fmt:message key='aimir.temperature'/>(℃)",                                       
                    //"useRoundEdges" : 1,                	
        	    	"basicDay" : $("#searchStartDate").val().substr(0,6),
        	    	"toolText" : ["<fmt:message key='aimir.date'/>", "<fmt:message key='aimir.locationUsage.usage'/>", "㎥", "<fmt:message key='aimir.usageFee2'/>", "<fmt:message key='aimir.hems.showDetailInfo'/>", "<fmt:message key='aimir.hems.label.averageCharge'/>"]
    		};
    		$.extend(chartParams, chartCommonParams, json_fChartStyle_Column2D_nobg);
        	chartUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodDayChart.do";
            chartName = "MSCombiDY2D.swf";
        }else if( "4" == selectedDateType ){

            // 텍스트 박스 관련 파라메타 설정
        	textParams = {
        	    	"basicDay" : $("#searchStartDate").val().substr(0,4),
    				"contractId" : $("#contractNumber").val()
    		};
        	textUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodMonthText.do";

            // 챠트 생성 파라메타 설정
        	chartParams = {
 
                    "PYAxisName": "<fmt:message key='aimir.hems.label.usageFeeSymbol'/>",   
                    "SYAxisName": "<fmt:message key='aimir.temperature'/>(℃)",                    
                    //"useRoundEdges" : 1,               	
        	    	"basicDay" : $("#searchStartDate").val().substr(0,4),
        	    	"toolText" : ["<fmt:message key='aimir.date'/>", "<fmt:message key='aimir.locationUsage.usage'/>", "㎥", "<fmt:message key='aimir.usageFee2'/>", "<fmt:message key='aimir.hems.showDetailInfo'/>", "<fmt:message key='aimir.hems.label.averageCharge'/>"]
    		};
    		$.extend(chartParams, chartCommonParams, json_fChartStyle_Column2D_nobg);
        	chartUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodMonthChart.do";
            chartName = "MSCombiDY2D.swf";
        }
			
   		getPeriodText(textParams, textUrl);
   		getPeriodChart(chartParams, chartUrl, chartName);
    };
    
    var getPeriodText = function(textParams, textUrl) {

		$.getJSON(textUrl,
				textParams,
            	function(result) {

                    $("#lastItem").text(result.lastItem);  
                    $("#lastUsage").text(result.lastUsage + " ㎥"); 
                    $("#lastFee").text(result.lastFee + " <fmt:message key='aimir.price.unit'/>");   
                    $("#lastCo2").text(result.lastCo2 + " kg");   
                    $("#lastIncentive").text(result.lastIncentive +" point");    
                    $("#currentItem").text(result.currentItem);      
                    $("#currentUsage").text(result.currentUsage + " ㎥");     
                    $("#currentFee").text(result.currentFee + " <fmt:message key='aimir.price.unit'/>");       
                    $("#currentCo2").text(result.currentCo2 + " kg");       
                    $("#currentIncentive").text(result.currentIncentive + " point"); 
            	}
            );
    };
    
	var periodChartData;
	
    var getPeriodChart = function(chartParams, chartUrl) {
		
        $.post(chartUrl,
        		chartParams,
            	function(result) {

               		var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartName, "myChartId", $("#periodChart").width(), "240", "0", "0" );
               		myChart.setJSONData(result);
               		myChart.setTransparent("transparent");
               		myChart.render("periodChart");

               		periodChartData = result;

                    hide();
               		                	
                    return;
            	},
            	"json"
            );
    };

    $(window).resize(function() {
        
        renderChart();
    });

    var renderChart = function() {
        
    	if($('#monthChart').is(':visible')) {
        	
       		var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "myChartId", $("#monthChart").width(), "240", "0", "0" );
       		myChart.setJSONData(monthChartData);
       		myChart.setTransparent("transparent");
       		myChart.render("monthChart");
    	} 

		if ($('#periodChart').is(':visible')) {

       		var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartName, "myChartId", $("#periodChart").width(), "200", "0", "0" );
       		myChart.setJSONData(periodChartData);
       		myChart.setTransparent("transparent");
       		myChart.render("periodChart");
    	}
    };
    
    var showDetailInfo = function(detailDate, showDateType) {
        $.ajaxSetup({
            async: false
        });
        if (DateType.HOURLY == showDateType) {

            $("#searchDateType").val(DateType.HOURLY);
            $("#dailyStartDate").val(detailDate);            
        } else if (DateType.DAILY == showDateType) {

            $("#searchDateType").val(DateType.DAILY);
            $("#monthlyYearCombo").val(detailDate.substr(0,4));
            $("#monthlyYearCombo").selectbox(); 

            getMonthlyMonthCombo(Number(detailDate.substr(4,2)), true);
        }
        $("#searchDateType").selectbox();
        changeDateType();
        $.ajaxSetup({
            async: true
        });
    };

</script>      

</head>

<body>
<div id="isNotService">
        <div class="margin_t10">
            <div class="isNotService_today_left"><span class="img_isNotService_gas_house"></span></div>
            <div class="isNotService_today_right">
                <table height='160'>
                <tr>    
                    <td><fmt:message key='aimir.hems.label.isNotService'/></td>
                </tr>
                </table>
            </div>
        </div>
</div>

<div id="wrapper">

	<!--contract no.-->
	<div class="topsearch">
		<div class="contract">
			<table>
				<tr>
					<td class="tit_name"><fmt:message key='aimir.hems.label.contractFriendlyName'/></td>
					<td>
						<select name="contractNumber" id="contractNumber" style="width:280px" onchange="javascript:changeContract();" >
		                	<c:forEach var="contract" items="${contracts}">
	                            <option value="${contract.id}">${contract.keyNum}</option>
	                        </c:forEach>
		                </select>
					</td>
				</tr>
			</table>
		</div>
		<div class="top_line"></div>
	
		<!-- tab : J query를 이용한 Tab메뉴로 해주세요!-->
		<div class="hems_tab">
			<ul>
				<li><a id="summaryDivTabId"><fmt:message key='aimir.hems.label.summary'/></a></li>
				<li><a id="periodDivTabId"><fmt:message key='aimir.hems.label.periodSearch'/></a></li>
			</ul>
		</div>
		<!--// tab -->
	
	</div>
	<!--//contract no.-->
	
		
	<!-- search : 기간 별/기기별만 보임 -->
	<div id="searchDiv" class="term_search">
		<div class="float_left">
	  		<ul class="usage">
				<li class="tit_term"><fmt:message key='aimir.locationUsage.term'/></li>
				<li>	    
					<select id="searchDateType" style="width:80px" onchange="javascript:changeDateType();" >
		        		<option value="0" selected="selected"><fmt:message key="aimir.hourly"/></option>
		        		<option value="1"><fmt:message key="aimir.daily"/></option>
		        		<option value="4"><fmt:message key="aimir.monthly"/></option>
		    		</select>
				</li>
			</ul>
		</div>
    	<div class="float_left">
		    <div id="daily">
		        <ul class="usage">
		            <li class="icon_prev"><button id="dailyLeft" type="button"></button></li>
		            <li><input id="dailyStartDate" type="text" class="daily_input" readonly="readonly"></li>
		            <li class="icon_next"><button id="dailyRight" type="button"></button></li>
		            <li class="hm_button"><a href="javascript:searchTab();"><fmt:message key='aimir.button.search'/></a></li>
		        </ul>
	        </div>
		    <div id="monthly">
		        <ul class="usage">
		            <li class="icon_prev"><button id="monthlyLeft" type="button"></button></li>
		            <li><select id="monthlyYearCombo"></select></li>
		            <li><label class="datetxt"><fmt:message key="aimir.hems.label.minYear" /></label></li>
		            <li><select id="monthlyMonthCombo" style="width:40px"></select></li>
		            <li><label class="datetxt"><fmt:message key="aimir.hems.label.minMonth" /></label></li>
		            <li class="icon_next"><button id="monthlyRight" type="button"></button></li>
		            <li class="hm_button"><a href="javascript:searchTab();"><fmt:message key='aimir.button.search'/></a></li>
		        </ul>
		    </div>
		    <div id="yearly">
		        <ul class="usage">
	            	<li class="icon_prev margin_r1"><button id="yearlyLeft" type="button"></button></li>
		            <li class="yearspace"><select id="yearlyYearCombo"></select></li>
	            	<li class="icon_next"><button id="yearlyRight" type="button"></button></li>
		            <li class="hm_button"><a href="javascript:searchTab();"><fmt:message key='aimir.button.search'/></a></li>
		        </ul>
		    </div>
    	</div>
	    <input id="searchStartDate" type="hidden"/>
	    <input id="searchEndDate" type="hidden" />
	</div>
	<!--// search -->
	
	<!-- tab 1: summary -->
	<div id="summaryDivTab" class="today">
	
		<!-- today info -->
		<div class="title_basic" id="title"></div>
		<div class="margin_t30">
			<div class="today_left"><span class="img_gas"></span></div>
			<div class="today_right">
				<table>
				<tr>	
					<th><fmt:message key='aimir.usageFee2'/></th>
					<td><span class="datavalue"><input type="text" id="usageFee" style="width:80px"/></span>
						<span class="datavalue_rt"></span>
					</td>
					<td><fmt:message key='aimir.hems.label.moneySymbol'/></td>
				</tr>
				<tr>
					<th><fmt:message key='aimir.locationUsage.usage'/></th>
					<td><span class="datavalue"><input type="text" id="usage" style="width:80px"/></span>
						<span class="datavalue_rt"></span>
					</td>
					<td>㎥</td>
				</tr>
				<tr>
					<th><fmt:message key='aimir.hems.label.incentive'/></th>
					<td><span class="datavalue"><input type="text" id="incentive" style="width:80px"/></span>
						<span class="datavalue_rt"></span>
					</td>
					<td>point</td>
				</tr>
				<tr>
					<th><fmt:message key='aimir.co2formula2'/></th>
					<td><span class="datavalue"><input type="text" id="co2formula" style="width:80px"/></span>
						<span class="datavalue_rt"></span>
					</td>
					<td>kg</td>
				</tr>
				</table>
			</div>
		</div>
		<!--// today info -->
		
		<div class="h20"></div>
		
		<!-- compare usage -->
		<div class="divbox margin_t40">
			<div class="title_basic"><span class="icon_title_blue"></span><span><fmt:message key='aimir.hems.label.compareMonthCharge'/></span></div>
			<div class="margin_t20" id="monthChart" >
				The chart will appear within this DIV. This text will be replaced by the chart.
			</div>
		</div>
		<!--// compare usage -->
		
	</div>
	<!--// tab 1: summary -->
	
	<!-- tab 2: term -->
	<div id="periodDivTab">
		<div class="divbox margin_10" id="periodChart" >
			The chart will appear within this DIV. This text will be replaced by the chart.
		</div>
		<div class="term_table_div">
		
			<!-- term data table -->
			<table class="term_table">
				<colgroup>
				<col width="35%"/>
				<col width="30%"/>
				<col width=""/>
				</colgroup>
				<tr>
					<th class="left" id="lastItem" ></th>
					<th class="center" ><fmt:message key='aimir.item'/></th>
					<th class="right" id="currentItem" ></th>
				</tr>
                <tr>
                    <td class="left" id="lastFee" ></td>
                    <td class="center"><fmt:message key='aimir.usageFee2'/></td>
                    <td class="right" id="currentFee" ></td>
                </tr>				
				<tr>
					<td class="left" id="lastUsage" ></td>
					<td class="center" ><fmt:message key='aimir.locationUsage.usage'/></td>
					<td class="right" id="currentUsage" ></td>
				</tr>

                <tr>
                    <td class="left" id="lastIncentive" ></td>
                    <td class="center"><fmt:message key='aimir.hems.label.incentive'/></td>
                    <td class="right" id="currentIncentive" ></td>
                </tr>				
				<tr>
					<td class="left" id="lastCo2" ></td>
					<td class="center"><fmt:message key='aimir.co2formula2'/></td>
					<td class="rightlast" id="currentCo2" ></td>
				</tr>

			</table>
			<!--// term data table -->
			
		</div>
	</div>
	<!--// tab 2: term -->

</div>
</body>
</html>