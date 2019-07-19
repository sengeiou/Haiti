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
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
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
	    $("#deviceSpecificDivTabId").click(function() { displayDivTab("deviceSpecificDivTab"); });
	
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
	
	var divTabArray = [ "summaryDivTab", "periodDivTab", "deviceSpecificDivTab" ];
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

	// 윈도우 사이즈 변경시, 그리드를 윈도우 사이즈에 맞게 재 설정한다.
    $(window).resize(function() {

       for (var i = 0; i < divTabArrayLength; i++) {

           if ($("#" + divTabArray[i]).css( "display" ) != "none") {

               tabSeq = i;
           }
       }

       switch (tabSeq) {
	       case 0 :
	    	   getSummaryGrid(rstMaxDay, $("#contractNumber").val());
	           break;
	       case 2 :
	    	   getDeviceSpecificGrid(chartParams1.basicDay, chartParams1.contractId);
	           break;
	
	       default :
	           break;
       }
    });


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
	
	        case 2 :
	            getDeviceSpecificDivData();
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
	
	               // $("#locationTd").text(result.location);
	               // $("#tariffTd").text(result.tariffType);
	               // $("#statusTd").text(result.status);
	               // $("#dateTd").text(result.date);
	
	                supplierId = result.contract.supplier;
	            }
	        );
	};

	var rstMaxDay;
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
					$("#title").html("<span class='icon_title_blue'></span>" + result.fommatDay + " <fmt:message key='aimir.hems.label.elecUseInfo'/>");
	
	                getMonthChart(result.maxDay);
	                rstMaxDay = result.maxDay;
	                getSummaryGrid(result.maxDay, $("#contractNumber").val());
				}
	        );
	};
	
	var monthChartData;
	var chartName0;
	
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
				"color" : fChartColor_CompareElec,
				"chartType" : fChartMulti,
				"formatNumberScale" : 0,
				"trandlineText" : "<fmt:message key='aimir.hems.savings.goal'/>",
				"label" : ["<fmt:message key='aimir.hems.label.averageUsageFee'/>", "<fmt:message key='aimir.hems.label.lastUsageFee'/>", "<fmt:message key='aimir.hems.label.basicUsageFee'/>"]
		};
		
		$.extend(params, json_fChartStyle_StColumn3D_nobg);

        $.post("${ctx}/gadget/energyConsumptionSearch/getMonthChart.do",
	            params,
	            function(result) {

	                var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "myChartId", $("#monthChart").width(), "200", "0", "0" );
	                myChart.setJSONData(result);
	                myChart.setTransparent("transparent");
	                myChart.render("monthChart");
	
	                monthChartData = result;
	
	                chartParams = {
	                        "showLabels" : 0, 
	                        "showValues" : 0,
	                        "showLegend" : 1, 
	                        "legendPosition" : "RIGHT",
	                        "animation" : 1,
	                        "toolText" : ["<fmt:message key='aimir.etc'/>", "<fmt:message key='aimir.hems.showDetailInfo'/>", "<fmt:message key='aimir.hems.label.equipName'/>", "<fmt:message key='aimir.usage'/>", "(kWh)"],
	                        "basicDay" : basicDay,
	                        "contractId" : $("#contractNumber").val()
	                };
	               $.extend(chartParams, json_fChartStyle_Pie2D, json_fChartStyle_legendScroll);

	                chartUrl = "${ctx}/gadget/energyConsumptionSearch/getDeviceSpecificTimeChart1.do";
	
	                chartName0 = "Pie3D.swf";
	
	                getDeviceSpecificChart(chartParams, chartUrl);
	                
	                return;
	            },
	            "json"
	        );
	};
	
	var deviceSpecificChartData;
	
	var getDeviceSpecificChart = function(chartParams, chartUrl) {
	    
	    $.post(chartUrl,
	            chartParams,
	            function(result) {

	                var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartName0, "myChartId", $("#deviceSpecificChart").width(), "216", "0", "0" );
	                myChart.setJSONData(result);
	                myChart.setTransparent("transparent");
	                myChart.render("deviceSpecificChart");

	                myChart.configureLink ( {                       
	                    swfUrl : "${ctx}/flexapp/swf/fcChart/" + linkedChartName,    
	                    overlayButton: {
	                        message: 'close',
	                        fontColor : '880000',
	                        bgColor:'FFEEEE',
	                        borderColor: '660000' } }, 0);
	                
	                deviceSpecificChartData = result;

	                hide();
	                                    
	                return;
	            },
	            "json"
	        );
	};
	
	var chartName;
	var chartCompareName;
	
	var getPeriodDivData = function() {
        $.ajaxSetup({
            async: false
        });
	    var textParams;
	    var textUrl;
	    var chartParams;
	    var chartUrl;

	    var selectedDateType = $("#searchDateType").val();

	    // 주기별 컬럼챠트 공통 파라미터
	    var chartCommonParams = {
                "chartLeftMargin" : 0,
                "chartRightMargin" : 0,
                "chartTopMargin" : 10,
                "chartBottomMargin" : 0,
                "showValues" : 0,
                "labelDisplay" : "WRAP",
                "moneyText" : "<fmt:message key='aimir.price.unit'/>",
                "contractId" : $("#contractNumber").val(),
                "formatNumberScale" : 0,
                "color" : fChartColor_hemsElec
	    };

	    // 주기별 비교 바챠트 공통 파라미터
	    var chartCompareCommonParams = {
                "yAxisValuesStep" : 2,
                "chartLeftMargin" : 0,
                "chartRightMargin" : 0,
                "chartTopMargin" : 0,
                "chartBottomMargin" : 0,
                "yaxisname": "<fmt:message key='aimir.hems.label.usageFeeSymbol'/>",
                "contractId" : $("#contractNumber").val(),
                "color" : fChartColor_CompareElec,
                "chartType" : fChartMulti,
                "formatNumberScale" : 0
	    };

	    if( "0" == selectedDateType ){ // 주기 : 시간별

	        // 텍스트 박스 속성 정의
	        textParams = {
	                "basicDay" : $("#searchStartDate").val(),
	                "contractId" : $("#contractNumber").val()
	        };
	        textUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodTimeText.do";
	        getPeriodText(textParams, textUrl);

            // 우리집과 이웃의 비교 차트  속성 정의
            $.getJSON("${ctx}/gadget/energyConsumptionSearch/getCompareTimeVLEDChart.do",
            		textParams,
                    function(result) {
                        $("#locationName").text(result.locationName + " " + "<fmt:message key='aimir.hems.label.averageUsageFee'/>"); // 지역명 설정
                        $("#compareGap").text(result.compareGap);
                        getVLEDChart(1, result.valueNumber, result.maxValue); // 우리집 챠트 생성
                        getVLEDChart(2, result.compareValue, result.maxValue); // 비교 대상 챠트 생성
                    }
            );

            // 컬럼 챠트  속성 정의
	        chartParams = {
                    "PYAxisName": "<fmt:message key='aimir.usage'/>(kWh)",   
                    "SYAxisName": "<fmt:message key='aimir.temperature'/>(℃)", 
	                "basicDay" : $("#searchStartDate").val(),
	                "toolText" : ["<fmt:message key='aimir.date'/>", "<fmt:message key='aimir.locationUsage.usage'/>", "kWh", "<fmt:message key='aimir.averageusage'/>"],
	                "label" : ["<fmt:message key='aimir.hems.label.averageUsageFee'/>"]
	        };
	        $.extend(chartParams, chartCommonParams, json_fChartStyle_Column2D_nobg);
	        chartUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodTimeChart.do";
            chartName = "MSCombiDY2D.swf";

            // 바 챠트  속성 정의
	        chartCompareParams = {              
	                "basicDay" : $("#searchStartDate").val(),
	                //"label" : ["<fmt:message key='aimir.hems.label.averageUsageFee'/>", barFCPastLabel, barFCLabel]
	                "label" : [barFCLastYearLabel, barFCPastLabel, barFCLabel]
	        };
	        $.extend(chartCompareParams, chartCompareCommonParams, json_fChartStyle_StColumn3D_nobg);
	        chartCompareUrl = "${ctx}/gadget/energyConsumptionSearch/getCompareTimeChart.do";
	        chartCompareName = "MSBar3D.swf";
	        
	    }else if( "1" == selectedDateType ){ // 주기 : 일별

            // 텍스트 박스 속성 정의
	        textParams = {
	                "basicDay" : $("#searchStartDate").val().substr(0,6),
	                "contractId" : $("#contractNumber").val()
	        };
	        textUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodDayText.do";
	        getPeriodText(textParams, textUrl);

            // 우리집과 이웃의 비교 차트  속성 정의
            $.getJSON("${ctx}/gadget/energyConsumptionSearch/getCompareDayVLEDChart.do",
                    textParams,
                    function(result) {
                        $("#locationName").text(result.locationName + " " + "<fmt:message key='aimir.hems.label.averageUsageFee'/>"); // 지역명 설정
                        $("#compareGap").text(result.compareGap);
		            	getVLEDChart(1, result.valueNumber, result.maxValue); // 우리집 그래프 생성
		            	getVLEDChart(2, result.compareValue, result.maxValue); // 비교 대상 그래프  생성
                    }
            );

            // 컬럼 챠트  속성 정의
	        chartParams = {
                    "PYAxisName": "<fmt:message key='aimir.hems.label.usageFeeSymbol'/>",   
                    "SYAxisName": "<fmt:message key='aimir.temperature'/>(℃)",     
	                "basicDay" : $("#searchStartDate").val().substr(0,6),
	                "toolText" : ["<fmt:message key='aimir.date'/>", "<fmt:message key='aimir.locationUsage.usage'/>", "kWh", "<fmt:message key='aimir.usageFee2'/>", "<fmt:message key='aimir.hems.showDetailInfo'/>", "<fmt:message key='aimir.hems.label.averageCharge'/>"]
	        };
	        $.extend(chartParams, chartCommonParams, json_fChartStyle_Column2D_nobg);
	        chartUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodDayChart.do";
            chartName = "MSCombiDY2D.swf";

            // 바 챠트  속성 정의
	        chartCompareParams = {
	                "basicDay" : $("#searchStartDate").val().substr(0,6),
                    //"label" : ["<fmt:message key='aimir.hems.label.averageUsageFee'/>", barFCPastLabel, barFCLabel]
                    "label" : [barFCLastYearLabel, barFCPastLabel, barFCLabel]
	        };
	        $.extend(chartCompareParams, chartCompareCommonParams, json_fChartStyle_StColumn3D_nobg);
	        chartCompareUrl = "${ctx}/gadget/energyConsumptionSearch/getCompareDayChart.do";
	        chartCompareName = "MSBar3D.swf";
	    }else if( "4" == selectedDateType ){ // 주기 : 월별

            // 텍스트 박스 속성 정의
	        textParams = {
	                "basicDay" : $("#searchStartDate").val().substr(0,4),
	                "contractId" : $("#contractNumber").val()
	        };

            // 우리집과 이웃의 비교 차트  속성 정의
            $.getJSON("${ctx}/gadget/energyConsumptionSearch/getCompareMonthVLEDChart.do",
                    textParams,
                    function(result) {
                        $("#locationName").text(result.locationName + " " + "<fmt:message key='aimir.hems.label.averageUsageFee'/>"); // 지역명 설정
                        $("#compareGap").text(result.compareGap);
		            	getVLEDChart(1, result.valueNumber, result.maxValue); // 우리집 그래프 생성
		            	getVLEDChart(2, result.compareValue, result.maxValue); // 비교대상 그래프 생성
                    }
            );
	        textUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodMonthText.do";
            getPeriodText(textParams, textUrl);

            // 컬럼 챠트  속성 정의
	        chartParams = {
                    "PYAxisName": "<fmt:message key='aimir.hems.label.usageFeeSymbol'/>",   
                    "SYAxisName": "<fmt:message key='aimir.temperature'/>(℃)",    
	                "basicDay" : $("#searchStartDate").val().substr(0,4),
	                "toolText" : ["<fmt:message key='aimir.date'/>", "<fmt:message key='aimir.locationUsage.usage'/>", "kWh", "<fmt:message key='aimir.usageFee2'/>", "<fmt:message key='aimir.hems.showDetailInfo'/>", "<fmt:message key='aimir.hems.label.averageCharge'/>"]
	        };
	        $.extend(chartParams, chartCommonParams, json_fChartStyle_Column2D_nobg);
	        chartUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodMonthChart.do";
            chartName = "MSCombiDY2D.swf";

            // 바 챠트  속성 정의
	        chartCompareParams = {
	                "basicDay" : $("#searchStartDate").val().substr(0,4),
                    //"label" : ["<fmt:message key='aimir.hems.label.averageUsageFee'/>", barFCPastLabel, barFCLabel]
                    "label" : [barFCLastYearLabel, barFCPastLabel, barFCLabel]
	        };
	        $.extend(chartCompareParams, chartCompareCommonParams, json_fChartStyle_StColumn3D_nobg);
	        chartCompareUrl = "${ctx}/gadget/energyConsumptionSearch/getCompareMonthChart.do";
	        chartCompareName = "MSBar3D.swf";
	    }

//	        getPeriodText(textParams, textUrl);
        getPeriodChart(chartParams, chartUrl, chartName);
        getCompareChart(chartCompareParams, chartCompareUrl, chartCompareName);

        $.ajaxSetup({
            async: true
        });
	};

	var barFCLabel;
	var barFCPastLabel;
	var barFCLastYearLabel;
	var getPeriodText = function(textParams, textUrl) {
	
	    $.getJSON(textUrl,
	            textParams,
	            function(result) {
			    	barFCLabel = result.currentItem;
			    	barFCPastLabel = result.lastItem;
			    	barFCLastYearLabel = result.lastYearItem;
	                $("#lastItem").text(result.lastItem);  
	                $("#lastUsage").text(result.lastUsage + " kWh"); 
	                $("#lastFee").text(result.lastFee + " <fmt:message key='aimir.price.unit'/>");   
	                $("#lastCo2").text(result.lastCo2 + " kg");   
	                $("#lastIncentive").text(result.lastIncentive +" point");    
	                $("#currentItem").text(result.currentItem);      
	                $("#currentUsage").text(result.currentUsage + " kWh");     
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
	
	                var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartName, "myChartId", $("#periodChart").width(), "200", "0", "0" );
	                myChart.setJSONData(result);
	                myChart.setTransparent("transparent");
	                myChart.render("periodChart");
	
	                periodChartData = result;
	
	                return;
	            },
	            "json"
	        );
	};
	
	var compareChartData;
	
	var getCompareChart = function(chartParams, chartUrl) {
	    
	    $.post(chartUrl,
	            chartParams,
	            function(result) {
	
	                var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartCompareName, "myChartId", $("#compareChart").width(), "265", "0", "0" );
	                myChart.setJSONData(result);
	                myChart.setTransparent("transparent");
	                myChart.render("compareChart");
	
	                compareChartData = result;
	
	                hide();
	                                    
	                return;
	            },
	            "json"
	        );
	};
	
	var chartName1;
	var chartName2;
	var linkedChartName = "Line.swf";
    var chartParams1;
    var chartParams2;
	var getDeviceSpecificDivData = function() {
	
	    var chartUrl1;
	    var chartUrl2;

	    var chartCommonParams1 = {
                "showLabels" : 0, 
                "showValues" : 0,
                "showLegend" : 1, 
                "legendPosition" : "RIGHT",
                "animation" : 1,
                "contractId" : $("#contractNumber").val()
	    };

	    var chartCommonParams2 = {
                "chartLeftMargin" : 0,
                "chartRightMargin" : 0,
                "chartTopMargin" : 10,
                "chartBottomMargin" : 0,
                "showValues" : 0,
                "labelDisplay" : "WRAP",
                "formatNumberScale" : 0,
                "contractId" : $("#contractNumber").val(),
                "yaxisname": "<fmt:message key='aimir.usage'/>(kWh)",
                "toolText" : ["<fmt:message key='aimir.etc'/>"]
	    };
	
	    var selectedDateType = $("#searchDateType").val();
	
	    if( "0" == selectedDateType ){  // 주기 : 시간별

	    	// Pie3D 그래프 속성 정의 Start
	        chartParams1 = {
                	"toolText" : ["<fmt:message key='aimir.etc'/>", "<fmt:message key='aimir.hems.showDetailInfo'/>", "<fmt:message key='aimir.hems.label.equipName'/>", "<fmt:message key='aimir.usage'/>", "(kWh)"],
	                "basicDay" : $("#searchStartDate").val()
	        };
	
	        $.extend(chartParams1, chartCommonParams1, json_fChartStyle_Pie2D, json_fChartStyle_legendScroll);
	        chartUrl1 = "${ctx}/gadget/energyConsumptionSearch/getDeviceSpecificTimeChart1.do";
	        chartName1 = "Pie3D.swf";
	        // Pie3D 그래프 속성 정의 End

	        // StackedColumn2D 그래프 속성 정의 start
	        chartParams2 = {
        	    	"basicDay" : $("#searchStartDate").val()
	        };

	        $.extend(chartParams2, chartCommonParams2, json_fChartStyle_Column2D_nobg);
	        chartUrl2 = "${ctx}/gadget/energyConsumptionSearch/getDeviceSpecificTimeChart2.do";
	        chartName2 = "StackedColumn2D.swf";
	       // StackedColumn2D 그래프 속성 정의 end
	    }else if( "1" == selectedDateType ){  // 주기 : 일별
	    	// Pie3D 그래프 속성 정의 Start
	        chartParams1 = {  
                	"toolText" : ["<fmt:message key='aimir.etc'/>", "<fmt:message key='aimir.hems.showDetailInfo'/>", "<fmt:message key='aimir.hems.label.equipName'/>", "<fmt:message key='aimir.usage'/>", "(kWh)"],
	                "basicDay" : $("#searchStartDate").val().substr(0,6)
	        };

	        $.extend(chartParams1, chartCommonParams1, json_fChartStyle_Pie2D, json_fChartStyle_legendScroll);
	        chartUrl1 = "${ctx}/gadget/energyConsumptionSearch/getDeviceSpecificDayChart1.do";
	        chartName1 = "Pie3D.swf";
	        // Pie3D 그래프 속성 정의 end

	        // StackedColumn2D 그래프 속성 정의 start
	        chartParams2 = {              	
                    "basicDay" : $("#searchStartDate").val().substr(0,6)
	        };

	        $.extend(chartParams2, chartCommonParams2, json_fChartStyle_Column2D_nobg);
	        chartUrl2 = "${ctx}/gadget/energyConsumptionSearch/getDeviceSpecificDayChart2.do";
	        chartName2 = "StackedColumn2D.swf";
	        // StackedColumn2D 그래프 속성 정의 end
	    }else if( "4" == selectedDateType ){ // 주기 : 월별
            // Pie3D 그래프 속성 정의 start
	        chartParams1 = {   
                	"toolText" : ["<fmt:message key='aimir.etc'/>", "<fmt:message key='aimir.hems.showDetailInfo'/>", "<fmt:message key='aimir.hems.label.equipName'/>", "<fmt:message key='aimir.usage'/>", "(kWh)"],
	                "basicDay" : $("#searchStartDate").val().substr(0,4)
	        };
	        $.extend(chartParams1, chartCommonParams1, json_fChartStyle_Pie2D, json_fChartStyle_legendScroll);
	        chartUrl1 = "${ctx}/gadget/energyConsumptionSearch/getDeviceSpecificMonthChart1.do";
	        chartName1 = "Pie3D.swf";
            // Pie3D 그래프 속성 정의 end
            
            // StackedColumn2D 그래프 속성 정의 start	        
	        chartParams2 = {                	
        	    	"basicDay" : $("#searchStartDate").val().substr(0,4)
	        };
	
	        $.extend(chartParams2, chartCommonParams2, json_fChartStyle_Column2D_nobg);
	        chartUrl2 = "${ctx}/gadget/energyConsumptionSearch/getDeviceSpecificMonthChart2.do";
	        chartName2 = "StackedColumn2D.swf";
	        // StackedColumn2D 그래프 속성 정의 end
	    }

	    getDeviceSpecificChart1(chartParams1, chartUrl1);
	    getDeviceSpecificChart2(chartParams2, chartUrl2);
	    getDeviceSpecificGrid(chartParams1.basicDay, chartParams1.contractId);
	};

	var deviceSpecificChartData1;
	
	var getDeviceSpecificChart1 = function(chartParams, chartUrl) {
	    
	    $.post(chartUrl,
	            chartParams,
	            function(result) {
	
	                var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartName1, "myChartId", $("#deviceSpecificChart1").width(), "216", "0", "0" );
	                myChart.setJSONData(result);
	                myChart.setTransparent("transparent");
	                myChart.render("deviceSpecificChart1");
	
	                myChart.configureLink ( {                       
	                    swfUrl : "${ctx}/flexapp/swf/fcChart/" + linkedChartName,    
	                    overlayButton: {
	                        message: 'close',
	                        fontColor : 'ffffff',
	                        bgColor:'0F7CD0',
	                        borderColor: '0F7CD0' } }, 0);
	                
	                deviceSpecificChartData1 = result;
	                
	                $("#deviceSpecificTotal").html("&nbsp;&nbsp;&nbsp;&nbsp;<img src='${ctx}/themes/images/customer/icon_money.png'> " + "<fmt:message key='aimir.usageFee2'/> :<font color='#ff4400'><b> " + result.total + "</b></font> dollars");
	            	
	                //$("#deviceSpecificTotal").text("<fmt:message key='aimir.usageFee2'/> : " + result.total + " kWh");
	            	
	                hide();
	                                    
	                return;
	            },
	            "json"
	        );
	};
	
	var deviceSpecificChartData2;
	
	var getDeviceSpecificChart2 = function(chartParams, chartUrl) {
	    
	    $.post(chartUrl,
	            chartParams,
	            function(result) {
	
	                var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartName2, "myChartId", $("#deviceSpecificChart2").width(), "220", "0", "0" );
	                myChart.setJSONData(result);
	                myChart.setTransparent("transparent");
	                myChart.render("deviceSpecificChart2");
	
	                deviceSpecificChartData2 = result;
	
	                hide();
	                                    
	                return;
	            },
	            "json"
	        );
	};
	
	var summaryGridOn = false;
	var summaryGrid;
	
    var program = function(val) {
        if (val == false) {
            return "<fmt:message key='aimir.hems.label.rejection'/>";
        } else if (val == true) {
            return "<fmt:message key='aimir.hems.label.permission'/>";
        }
        return val;
    };
    
	var getSummaryGrid = function(basicDay, contractId) {
		
	    var width = $("#summaryGrid").width();
	    
	    var store = new Ext.data.JsonStore({
	        autoLoad: true,
	        url: "${ctx}/gadget/energyConsumptionSearch/getDeviceSpecificGrid.do?basicDay=" + basicDay + "&contractId=" + contractId,
	        root: "result",
	        fields: ["no", "NAME", {name:"USAGE", type: "float"}, "LEVELDR", "PROGRAMDR", {name:"CO2", type: "float"}]
	    });
	    
	    var colModel = new Ext.grid.ColumnModel({
	        defaults: {
	            width: ((width-40) / 5) - 1, // 순위 컬럼을 제외한 나머지 컬럼은 디폴트로 설정, 수평 스크롤 자동 발생을 방지하기 위해서  -1을 추가한다.
	            sortable: true
	        },
	        columns: [
	  	            { id: "no", header: "<fmt:message key='aimir.ranking'/>", width: 40, dataIndex: "no"},
		            { header: "<fmt:message key='aimir.hems.label.equipName'/>", dataIndex: "NAME"},
		            { header: "<fmt:message key='aimir.usage'/>(kWh)", dataIndex: "USAGE"},
		            { header: "<fmt:message key='aimir.hems.label.levelDR'/>", dataIndex: "LEVELDR"},
		            { header: "<fmt:message key='aimir.hems.label.programDR'/>", renderer: program, dataIndex: "PROGRAMDR"},
		            { header: "<fmt:message key='aimir.co2formula'/>", dataIndex: "CO2"}
	        ]
	    });
	    
	    if (summaryGridOn == false) {
	        
	    	summaryGrid = new Ext.grid.GridPanel({
	            height: 220
	           ,store: store
	           ,colModel : colModel
	           ,width: width
               ,columnLines: true
	        });
	
	    	summaryGrid.render("summaryGrid");
	    	summaryGridOn = true;
	    } else {
	    	summaryGrid.setWidth(width);
	    	summaryGrid.reconfigure(store, colModel);
	    }
	};

	var deviceSpecificGridOn = false;
	var deviceSpecificGrid;
	
	var getDeviceSpecificGrid = function(basicDay, contractId) {
	
	    var width = $("#deviceSpecificGrid").width();
	    
	    var store = new Ext.data.JsonStore({
	        autoLoad: true,
	        url: "${ctx}/gadget/energyConsumptionSearch/getDeviceSpecificGrid.do?basicDay=" + basicDay + "&contractId=" + contractId,
	        root: "result",
	        fields: ["no", "NAME", {name:"USAGE", type: "float"}, "LEVELDR", "PROGRAMDR", {name:"CO2", type: "float"}]
	    });
	
	    var colModel = new Ext.grid.ColumnModel({
	        defaults: {
	            width: ((width-40) / 5) - 1, // 순위 컬럼을 제외한 나머지 컬럼은 디폴트로 설정, 수평 스크롤 자동 발생을 방지하기 위해서  -1을 추가한다.
	            sortable: true
	        },
	        columns: [
	  	            { id: "no", header: "<fmt:message key='aimir.ranking'/>", width: 40, dataIndex: "no"},
		            { header: "<fmt:message key='aimir.hems.label.equipName'/>", dataIndex: "NAME"},
		            { header: "<fmt:message key='aimir.usage'/>(kWh)", dataIndex: "USAGE"},
		            { header: "<fmt:message key='aimir.hems.label.levelDR'/>", dataIndex: "LEVELDR"},
		            { header: "<fmt:message key='aimir.hems.label.programDR'/>", renderer: program, dataIndex: "PROGRAMDR"},
		            { header: "<fmt:message key='aimir.co2formula'/>", dataIndex: "CO2"}
	        ]
	    });

	    if (deviceSpecificGridOn == false) {

	    	deviceSpecificGrid = new Ext.grid.GridPanel({
	            height: 220
	           ,store: store
	           ,colModel : colModel
	           ,width: width
               ,columnLines: true
	           // ,title: "<fmt:message key='aimir.hems.label.deviceSpecificRanking'/>"
	        });

	    	deviceSpecificGrid.render("deviceSpecificGrid");
	        deviceSpecificGridOn = true;
	    } else {
	    	deviceSpecificGrid.setWidth(width);
	    	deviceSpecificGrid.reconfigure(store, colModel);
	    }
	};

	$(window).resize(function() {

	    renderChart();
	});

	var renderChart = function() {

	    if($('#monthChart').is(':visible')) {
	        
	        var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/MSBar3D.swf", "myChartId", $("#monthChart").width(), "220", "0", "0" );
	        myChart.setJSONData(monthChartData);
	        myChart.setTransparent("transparent");
	        myChart.render("monthChart");
	    } 

	    if ($('#deviceSpecificChart').is(':visible')) {

	        var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartName0, "myChartId", $("#deviceSpecificChart").width(), "220", "0", "0" );
	        myChart.setJSONData(deviceSpecificChartData);
	        myChart.setTransparent("transparent");
	        myChart.render("deviceSpecificChart");
	
	        myChart.configureLink ( {               
	            swfUrl : "${ctx}/flexapp/swf/fcChart/" + linkedChartName,    
	            overlayButton: {
	                message: 'close',
	                fontColor : '880000',
	                bgColor:'FFEEEE',
	                borderColor: '660000' } }, 0);
	    }
	
	    if ($('#periodChart').is(':visible')) {
	
	        var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartName, "myChartId", $("#periodChart").width(), "220", "0", "0" );
	        myChart.setJSONData(periodChartData);
	        myChart.setTransparent("transparent");
	        myChart.render("periodChart");
	    }
	
	    if ($('#compareChart').is(':visible')) {
	
	        var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartCompareName, "myChartId", $("#compareChart").width(), "220", "0", "0" );
	        myChart.setJSONData(compareChartData);
	        myChart.setTransparent("transparent");
	        myChart.render("compareChart");
	    }
	
	    if ($('#deviceSpecificChart1').is(':visible')) {
	
	        var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartName1, "myChartId", $("#deviceSpecificChart1").width(), "220", "0", "0" );
	        myChart.setJSONData(deviceSpecificChartData1);
	        myChart.setTransparent("transparent");
	        myChart.render("deviceSpecificChart1");
	
	        myChart.configureLink ( {               
	            swfUrl : "${ctx}/flexapp/swf/fcChart/" + linkedChartName,    
	            overlayButton: {
	                message: 'close',
	                fontColor : '880000',
	                bgColor:'FFEEEE',
	                borderColor: '660000' } }, 0);
	    }
	
	    if ($('#deviceSpecificChart2').is(':visible')) {
	
	        var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartName2, "myChartId", $("#deviceSpecificChart2").width(), "220", "0", "0" );
	        myChart.setJSONData(deviceSpecificChartData2);
	        myChart.setTransparent("transparent");
	        myChart.render("deviceSpecificChart2");
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

   var getVLEDChart = function(index, value, maxValue) {

	   // 챠트 레이아웃 속성 설정
	   dataXml = ' <chart lowerLimit="0" upperLimit="' + maxValue + '" showTickMarks="0" showTickValues="0" ' 
	                +  'minorTMNumber="0" minorTMHeight="3" majorTMThickness="0" showBorder="0" '
	                +  'decimalPrecision="0" ledGap="0" ledSize="1" ledBorderThickness="0" ticksOnRight="0" '
	                + "bgSWF='../../themes/images/customer/bg_house.gif' "//(background)
	                + "chartLeftMargin='30' "
	                + "chartRightMargin='30' "
	                + "chartTopMargin='50' "
	                + "chartBottomMargin='0' " 
	               // + fChartStyle_Font
	                + fChartStyle_vLed_nobg
	                + '> '      
	                + ' <colorRange> '
	                +    '<color minValue="0" maxValue="' + value + '" code="'+fChartColor_CompareElec[2]+'" /> '
	                +    '<color minValue="' + value + '" maxValue="' + maxValue + '" code="ffffff" /> '
	                + ' </colorRange> '
	                +'  <value>' +value+'</value> '
	                + ' </chart> ';        

            var myChart = new FusionCharts( "${ctx}/flexapp/swf/fusionwidgets/VLED.swf", "myChartId", "100", "167", "0", "0");
            myChart.setDataXML(dataXml);
            myChart.render("chart" + index);
   };
 
</script>      

</head>
<body>
<div id="isNotService">
        <div class="margin_t10">
            <div class="isNotService_today_left"><span class="img_isNotService_elec_house"></span></div>
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
						<select name="contractNumber" id="contractNumber" onchange="javascript:changeContract();" style="width:550px" >
		                    <c:forEach var="contract" items="${contracts}">
		                        <option value="${contract.id}">${contract.keyNum}</option>
		                    </c:forEach>
	                	</select>
					</td>
				</tr>
			</table>
		</div>
		<div class="top_line"></div>
			
		<!--excel,print button  
		<ul>
		    <li class="hm_button margin_r3"><span class="icon_excel"></span><a ><fmt:message key='aimir.button.excel'/></a></li>
		    <li class="hm_button"><span class="icon_print"></span><a ><fmt:message key='aimir.report.print'/></a></li> 
		</ul>-->
  
		<!-- tab : J query를 이용한 Tab메뉴로 해주세요!-->
		<div class="hems_tab">
		    <ul>
		        <li><a id="summaryDivTabId"><fmt:message key='aimir.hems.label.summary'/></a></li>
		        <li><a id="periodDivTabId"><fmt:message key='aimir.hems.label.periodSearch'/></a></li>
		        <li><a id="deviceSpecificDivTabId"><fmt:message key='aimir.hems.label.deviceSpecificSearch'/></a></li>
		    </ul>
		</div>
		<!--// tab -->
	
	</div>
    <!--//contract no.-->
    
    <!-- search : 기간 별/기기별만 보임 -->
    <div id="searchDiv" class="term_search_max">
        <div class="float_left">
            <ul>
                <li class="tit_term"><fmt:message key='aimir.locationUsage.term'/></li>
                <li>        
                    <select id="searchDateType" style="width:100px" onchange="javascript:changeDateType();" >
                        <option value="0" selected="selected"><fmt:message key="aimir.hourly"/></option>
                        <option value="1"><fmt:message key="aimir.daily"/></option>
                        <option value="4"><fmt:message key="aimir.monthly"/></option>
                    </select>
                </li>
            </ul>
        </div>
        <div class="float_left">
            <div id="daily">
                <ul>
                    <li class="icon_prev"><button id="dailyLeft" type="button"></button></li>
                    <li><input id="dailyStartDate" type="text" class="daily_input" readonly="readonly"></li>
                    <li class="icon_next"><button id="dailyRight" type="button"></button></li>
                    <li class="hm_button"><a href="javascript:searchTab();"><fmt:message key='aimir.button.search'/></a></li>
                </ul>
            </div>
            <div id="monthly">
                <ul>
                    <li class="icon_prev"><button id="monthlyLeft" type="button"></button></li>
                    <li><select id="monthlyYearCombo"></select></li>
                    <li><label class="datetxt"><fmt:message key="aimir.hems.label.maxYear" /></label></li>
                    <li><select id="monthlyMonthCombo"></select></li>
                    <li><label class="datetxt"><fmt:message key="aimir.hems.label.maxMonth" /></label></li>
                    <li class="icon_next"><button id="monthlyRight" type="button"></button></li>
                    <li class="hm_button"><a href="javascript:searchTab();"><fmt:message key='aimir.button.search'/></a></li>
                </ul>
            </div>
            <div id="yearly">
                <ul>
                    <li class="icon_prev"><button id="yearlyLeft" type="button"></button></li>
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
        <div class="max_left bg_img">
        
            <!-- today info -->
			<div class="title_basic" id="title" ></div>
            <div class="margin_t30">
                <div class="today_left" ><span class="img_elec"></span></div>
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
                        <td>kWh</td>
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
            
        </div>
        
        <div class="max_right">
        
            <!-- compare usage max -->
            <div class="divbox">
                <div class="title_basic">
                    <span class="icon_title_blue"></span>
                    <span><fmt:message key='aimir.hems.label.compareMonthCharge'/></span>
                </div>
                <div  id="monthChart" >
                    The chart will appear within this DIV. This text will be replaced by the chart.
                </div>
            </div>
            <!--// compare usage max -->
            
        </div>
        
        <div class="clear h20"></div>
        
        <div class="max_left">
            <!-- Compare Used Equipment -->
            <div class="title_basic">
            	<span class="icon_title_blue"></span><fmt:message key='aimir.hems.label.deviceSpecificCompare'/>
            </div>
            <div class="divbox border_dotted_blue" id="deviceSpecificChart" >
            	The chart will appear within this DIV. This text will be replaced by the chart.
            </div>
            <!--//Compare Used Equipment  -->
        </div>
        
	    <div class="title_basic">
	      	<span class="icon_title_blue"></span><fmt:message key='aimir.hems.label.deviceSpecificRanking'/>
	    </div>
        <div class="max_right" id="summaryGrid">
        </div>
        
        <div class="clear h20"></div>
        
        <div class="areamargin">
            <textarea class="em_textarea"></textarea>
        </div>
    
    </div>
    <!--// tab 1: summary -->
    
    <!-- tab 2: term -->
    <div id="periodDivTab">
        <div class="clear"></div>
        
        <div class="max2_right">
            <div class="margin_term">
            
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
        <div class="max2_left">
            <div class="divbox margin_10" id="periodChart" >
                The chart will appear within this DIV. This text will be replaced by the chart.
            </div>
        </div>
        
        <div class="clear h20"></div>
        
        <!-- VLED Chart -->
        <div class="max2_right">
        	<div class="title_basic">
                <span class="icon_title_blue"></span>
                <span><fmt:message key='aimir.hems.label.compareToHomeElc'/></span>
            </div>
            <div class="comparebox">
            	<div class="compare_left">
            		<ul><li id='chart1'></li>
            			<li><fmt:message key='aimir.hems.label.myHome'/></li>
            		</ul>
            		<ul><li id='chart2'></li>
            			<li id='locationName'></li>
            		</ul>
			    </div>
		        
		        <div class="compare_right">
		        	<ul>
		        		<li><span class="icon_arrow_gray"></span><fmt:message key='aimir.hems.label.checkCompareTo'/></li>
		        		<li><input type="radio" id="" name="compareTo" class="radio" checked/> <fmt:message key='aimir.hems.label.sameLocation'/></li>
		        		<li><input type="radio" id="" name="compareTo" class="radio" disabled/> <fmt:message key='aimir.hems.label.CompareToSameFamily'/></li>
		        		<li> &nbsp;</li>

		        	</ul>
		        	<div class="copmare_result">
			        	<fmt:message key='aimir.hems.label.CompareToHomeElcMsg1'/> 
			        	<span class="text_orange2 bold" id='compareGap'></span><span class="text_orange2 bold"><fmt:message key='aimir.price.unit'/></span>
			        	<fmt:message key='aimir.hems.label.CompareToHomeMsg2'/> 
		        	</div>
		        </div>
	        </div>
        </div>
        <!--// VLED Chart -->
        
        <!-- compare usage -->
        <div class="max2_left padding_r35">
            <div class="title_basic">
                <span class="icon_title_blue"></span>
                <span><fmt:message key='aimir.hems.label.compareCharge'/></span>
            </div>
            <div  id="compareChart" >
                The chart will appear within this DIV. This text will be replaced by the chart.
            </div>
        </div>
        <!--// compare usage -->
 
    </div>
    <!--// tab 2: term -->
    
    <!-- tab 3: equip -->
    <div id="deviceSpecificDivTab" class="today">
 
       	<div class="title_basic">
       		<span class="icon_title_blue"></span><fmt:message key='aimir.hems.label.deviceSpecificCompare'/>
	    </div>
	    	
        <div class="max_left border_dotted_blue" >
        	 <!--<div id="deviceSpecificTotal" class="margin_t10"></div>-->
        	<div id="deviceSpecificChart1">
            	The chart will appear within this DIV. This text will be replaced by the chart.
        	</div>
        </div>
        
        <div class="max_right"  id="deviceSpecificGrid"></div>
        <div class="clear margin_20"><div id="deviceSpecificChart2" class="w_100"></div></div>
    </div>
    <!--// tab 3: equipment -->
    
</div>
</body>
</html>