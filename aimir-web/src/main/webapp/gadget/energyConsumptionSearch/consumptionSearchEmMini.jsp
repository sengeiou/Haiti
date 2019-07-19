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
        //dailyArrow(maxDay, 0, true);
        
        
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

        searchDivInit();

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
                    $("#title").html("<span class='icon_title_blue'></span>" + result.fommatDay + " <fmt:message key='aimir.hems.label.elecUseInfo'/>");

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
                "color" : fChartColor_CompareElec,
                "chartType" : fChartMulti,
                "formatNumberScale" : 0,
                "trandlineText" : "<fmt:message key='aimir.hems.savings.goal'/>", 
                "label" : ["<fmt:message key='aimir.hems.label.averageUsageFee'/>", "<fmt:message key='aimir.hems.label.lastUsageFee'/>", "<fmt:message key='aimir.hems.label.basicUsageFee'/>" ]
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
        // 컬럼 챠트 공통 파라미터
        var chartCommonParams = {
                "chartLeftMargin" : 0,
                "chartRightMargin" : 0,
                "chartTopMargin" : 10,
                "chartBottomMargin" : 0,
                "showValues" : 0,
                "labelDisplay" : "WRAP",
                "labelStep" : 2,
                //"useRoundEdges" : 1,
                "moneyText" : "<fmt:message key='aimir.price.unit'/>",
                "contractId" : $("#contractNumber").val(),
                "formatNumberScale" : 0,
                "color" : fChartColor_hemsElec
        };

        var selectedDateType = $("#searchDateType").val();

        if( "0" == selectedDateType ){ // 주기 : 시간별 

        	// 텍스트 박스 속성 정의
            textParams = {
                    "basicDay" : $("#searchStartDate").val(),
                    "contractId" : $("#contractNumber").val()
            };
            textUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodTimeText.do";

            // 컬럼 챠트 속성 정의
            chartParams = {
                    "PYAxisName": "<fmt:message key='aimir.usage'/>(kWh)",   
                    "SYAxisName": "<fmt:message key='aimir.temperature'/>(℃)", 
                    "basicDay" : $("#searchStartDate").val(),
                    "toolText" : ["<fmt:message key='aimir.date'/>", "<fmt:message key='aimir.locationUsage.usage'/>", "kWh", "<fmt:message key='aimir.averageusage'/>"]
            };

            $.extend(chartParams, chartCommonParams, json_fChartStyle_Column2D_nobg);

            chartUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodTimeChart.do";
            chartName = "MSCombiDY2D.swf";
        }else if( "1" == selectedDateType ){

            // 텍스트 박스 속성 정의
            textParams = {
                    "basicDay" : $("#searchStartDate").val().substr(0,6),
                    "contractId" : $("#contractNumber").val()
            };

            textUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodDayText.do";

            // 컬럼 챠트 속성 정의
            chartParams = {
                    "PYAxisName": "<fmt:message key='aimir.hems.label.usageFeeSymbol'/>",   
                    "SYAxisName": "<fmt:message key='aimir.temperature'/>(℃)",               
                    "basicDay" : $("#searchStartDate").val().substr(0,6),
                    "toolText" : ["<fmt:message key='aimir.date'/>", "<fmt:message key='aimir.locationUsage.usage'/>", "kWh", "<fmt:message key='aimir.usageFee2'/>", "<fmt:message key='aimir.hems.showDetailInfo'/>", "<fmt:message key='aimir.hems.label.averageCharge'/>"]
            };

            $.extend(chartParams, chartCommonParams, json_fChartStyle_Column2D_nobg);
            chartUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodDayChart.do";
            chartName = "MSCombiDY2D.swf";
        }else if( "4" == selectedDateType ){

            // 텍스트 박스 속성 정의
            textParams = {
                    "basicDay" : $("#searchStartDate").val().substr(0,4),
                    "contractId" : $("#contractNumber").val()
            };

            textUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodMonthText.do";

            // 컬럼 챠트 속성 정의
            chartParams = {
                    "PYAxisName": "<fmt:message key='aimir.hems.label.usageFeeSymbol'/>",   
                    "SYAxisName": "<fmt:message key='aimir.temperature'/>(℃)",                
                    "basicDay" : $("#searchStartDate").val().substr(0,4),
                    "toolText" : ["<fmt:message key='aimir.date'/>", "<fmt:message key='aimir.locationUsage.usage'/>", "kWh", "<fmt:message key='aimir.usageFee2'/>", "<fmt:message key='aimir.hems.showDetailInfo'/>", "<fmt:message key='aimir.hems.label.averageCharge'/>"]
            };

            $.extend(chartParams, chartCommonParams, json_fChartStyle_Column2D_nobg);

            chartUrl = "${ctx}/gadget/energyConsumptionSearch/getPeriodMonthChart.do";
            chartName = "MSCombiDY2D.swf";
        }

        getPeriodText(textParams, textUrl);
        getPeriodChart(chartParams, chartUrl, chartName);
    };

    // 텍스트 박스에 표시값 설정
    var getPeriodText = function(textParams, textUrl) {

        $.getJSON(textUrl,
                textParams,
                function(result) {

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

    var chartName1;
    var chartName2;
    var linkedChartName = "Line.swf";
        
    var getDeviceSpecificDivData = function() {

        var chartParams1;
        var chartUrl1;
        var chartParams2;
        var chartUrl2;
        var selectedDateType = $("#searchDateType").val();

        // 파이챠트 공통 파라미터
        var chartCommonParams1 = {
                "color" : fChartColor_houseElec,
                "showLabels" : 0, 
                "showValues" : 0,
                "showLegend" : 1, 
                "legendPosition" : "RIGHT",
                "animation" : 1,
                "contractId" : $("#contractNumber").val()
        };

        // 누적컬럼챠트 공통 파라미터
        var chartCommonParams2 = {
                "chartLeftMargin" : 0,
                "chartRightMargin" : 5,
                "chartTopMargin" : 10,
                "chartBottomMargin" : 0,
                "showValues" : 0,
                "labelDisplay" : "WRAP",
                "labelStep" : 2,
                "toolText" : ["<fmt:message key='aimir.etc'/>"],
                "yaxisname": "<fmt:message key='aimir.usage'/>(kWh)",
                "formatNumberScale" : 0,
                "contractId" : $("#contractNumber").val()
        };

        if( "0" == selectedDateType ){ // 주기 : 시간별
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
        }else if( "1" == selectedDateType ){ // 주기 : 일별
            // Pie3D 그래프 속성 정의 Start
            chartParams1 = {
                    "toolText" : ["<fmt:message key='aimir.etc'/>", "<fmt:message key='aimir.hems.showDetailInfo'/>", "<fmt:message key='aimir.hems.label.equipName'/>", "<fmt:message key='aimir.usage'/>", "(kWh)"],
                    "basicDay" : $("#searchStartDate").val().substr(0,6)
            };

            $.extend(chartParams1, chartCommonParams1, json_fChartStyle_Pie2D, json_fChartStyle_legendScroll);
            chartUrl1 = "${ctx}/gadget/energyConsumptionSearch/getDeviceSpecificDayChart1.do";
            chartName1 = "Pie3D.swf";
            // Pie3D 그래프 속성 정의 End

            // StackedColumn2D 그래프 속성 정의 start
            chartParams2 = {                   
                    "basicDay" : $("#searchStartDate").val().substr(0,6)
            };

            $.extend(chartParams2, chartCommonParams2, json_fChartStyle_Column2D_nobg);
            chartUrl2 = "${ctx}/gadget/energyConsumptionSearch/getDeviceSpecificDayChart2.do";
            chartName2 = "StackedColumn2D.swf";
            // StackedColumn2D 그래프 속성 정의 end
        }else if( "4" == selectedDateType ){ // 주기 : 월별
            // Pie3D 그래프 속성 정의 Start
            chartParams1 = {
                    "toolText" : ["<fmt:message key='aimir.etc'/>", "<fmt:message key='aimir.hems.showDetailInfo'/>", "<fmt:message key='aimir.hems.label.equipName'/>", "<fmt:message key='aimir.usage'/>", "(kWh)"],
                    "basicDay" : $("#searchStartDate").val().substr(0,4)
            };

            $.extend(chartParams1, chartCommonParams1, json_fChartStyle_Pie2D, json_fChartStyle_legendScroll);
            chartUrl1 = "${ctx}/gadget/energyConsumptionSearch/getDeviceSpecificMonthChart1.do";
            chartName1 = "Pie3D.swf";
            // Pie3D 그래프 속성 정의 End
            
            // StackedColumn2D 그래프 속성 정의 start
            chartParams2 = {                   
                    "basicDay" : $("#searchStartDate").val().substr(0,4)
            };

            $.extend(chartParams2, chartCommonParams2, json_fChartStyle_Column2D_nobg);
            chartUrl2 = "${ctx}/gadget/energyConsumptionSearch/getDeviceSpecificMonthChart2.do";
            chartName2 = "StackedColumn2D.swf";
            // StackedColumn2D 그래프 속성 정의 End
        }

        // 파이 챠트 생성
        getDeviceSpecificChart1(chartParams1, chartUrl1);
        // 누적 컬럼 챠트 생ㅇ성
        getDeviceSpecificChart2(chartParams2, chartUrl2);
    };

    var deviceSpecificChartData1;

    var getDeviceSpecificChart1 = function(chartParams, chartUrl) {
        $.post(chartUrl,
                chartParams,
                function(result) {

                    var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartName1, "myChartId", $("#deviceSpecificChart1").width(), "150", "0", "0" );
                    myChart.setJSONData(result);
                    myChart.setTransparent("transparent");
                    myChart.render("deviceSpecificChart1");

                    myChart.configureLink ( {                           
                        swfUrl : "${ctx}/flexapp/swf/fcChart/" + linkedChartName,    
                        overlayButton: {
                            message: 'close',
                            fontColor : 'ffffff',
                            bgColor:'0F7CD0',
                            borderColor: '0D70B9' } }, 0);
                    
                    deviceSpecificChartData1 = result;

                    //$("#deviceSpecificTotal").html("&nbsp;&nbsp;&nbsp;&nbsp;<img src='${ctx}/themes/images/customer/icon_money.png'> " + "<fmt:message key='aimir.usageFee2'/> :<font color='#ff4400'><b> " + result.total + "</b></font> dollars");
                    $("#deviceSpecificTotal").html("&nbsp;&nbsp;&nbsp;&nbsp;<img src='${ctx}/themes/images/customer/icon_money.png'> " + "<fmt:message key='aimir.hems.label.usageFeeSymbol'/> :<font color='#ff4400'><b> " + result.total + "</b></font>");
                    
                    
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

                    var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartName2, "myChartId", $("#deviceSpecificChart2").width(), "240", "0", "0" );
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

        if ($('#deviceSpecificChart1').is(':visible')) {

            var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartName1, "myChartId", $("#deviceSpecificChart1").width(), "200", "0", "0" );
            myChart.setJSONData(deviceSpecificChartData1);
            myChart.setTransparent("transparent");
            myChart.render("deviceSpecificChart1");

            myChart.configureLink ( {                   
                swfUrl : "${ctx}/flexapp/swf/fcChart/" + linkedChartName,    
                overlayButton: {
                    message: 'close',
                    fontColor : 'ffffff',
                    bgColor:'0F7CD0',
                    borderColor: '0D70B9' } }, 0);
        }

        if ($('#deviceSpecificChart2').is(':visible')) {

            var myChart = new FusionCharts( "${ctx}/flexapp/swf/fcChart/" + chartName2, "myChartId", $("#deviceSpecificChart2").width(), "240", "0", "0" );
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


    Ext.namespace('Ext.ux');

    /**
     * @class Ext.ux.MonthPicker
     * @extends Ext.Component
     * A picker that allows you to select a month and year
     * @constructor
     * @param {Object} config Configuration options
     * @author Joseph Kralicky
     * @version 0.1
     */




    Ext.ux.MonthPicker = Ext.extend(Ext.Component, {

        format : "M, Y",

        okText : "&#160;OK&#160;", 
        
        cancelText : "Cancel",

        constrainToViewport : true,
        
        monthNames : Date.monthNames,
        
        startDay : 0,

        value : 0,

        noPastYears: true, // only use the current year and future years

        initComponent: function(){
            Ext.ux.MonthPicker.superclass.initComponent.call(this);

            this.value = this.value ?
                     this.value.clearTime() : new Date().clearTime();

            this.addEvents(
                
                'select'
            );

            if(this.handler){
                this.on("select", this.handler,  this.scope || this);
            }
        },

        focus : function(){
            if(this.el){
                this.update(this.activeDate);
            }
        },

        onRender : function(container, position){
            var m = [ '<div style="width: 175px; height:175px;"></div>' ]
            m[m.length] = '<div class="x-date-mp"></div>';

            var el = document.createElement("div");
            el.className = "x-date-picker";
            el.innerHTML = m.join("");

            container.dom.insertBefore(el, position);

            this.el = Ext.get(el);
            this.monthPicker = this.el.down('div.x-date-mp');
            this.monthPicker.enableDisplayMode('block');

            this.el.unselectable();

            this.showMonthPicker();

            if(Ext.isIE){
                this.el.repaint();
            }

            this.update(this.value);

        },

        createMonthPicker : function(){
            if(!this.monthPicker.dom.firstChild){
                var buf = ['<table border="0" cellspacing="0">'];
                for(var i = 0; i < 6; i++){
                    buf.push(
                        '<tr><td class="x-date-mp-month"><a href="#">', this.monthNames[i].substr(0, 3), '</a></td>',
                        '<td class="x-date-mp-month x-date-mp-sep"><a href="#">', this.monthNames[i+6].substr(0, 3), '</a></td>',
                        i == 0 ?
                        '<td class="x-date-mp-ybtn" align="center"><a class="x-date-mp-prev"></a></td><td class="x-date-mp-ybtn" align="center"><a class="x-date-mp-next"></a></td></tr>' :
                        '<td class="x-date-mp-year"><a href="#"></a></td><td class="x-date-mp-year"><a href="#"></a></td></tr>'
                    );
                }
                buf.push(
                    '<tr class="x-date-mp-btns"><td colspan="4"><button type="button" class="x-date-mp-ok">',
                        this.okText,
                        '</button><button type="button" class="x-date-mp-cancel">',
                        this.cancelText,
                        '</button></td></tr>',
                    '</table>'
                );
                this.monthPicker.update(buf.join(''));
                this.monthPicker.on('click', this.onMonthClick, this);
                this.monthPicker.on('dblclick', this.onMonthDblClick, this);

                this.mpMonths = this.monthPicker.select('td.x-date-mp-month');
                this.mpYears = this.monthPicker.select('td.x-date-mp-year');

                this.mpMonths.each(function(m, a, i){
                    i += 1;
                    if((i%2) == 0){
                        m.dom.xmonth = 5 + Math.round(i * .5);
                    }else{
                        m.dom.xmonth = Math.round((i-1) * .5);
                    }
                });
            }
        },

        showMonthPicker : function(){
            this.createMonthPicker();
            var size = this.el.getSize();
            this.monthPicker.setSize(size);
            this.monthPicker.child('table').setSize(size);

            this.mpSelMonth = (this.activeDate || this.value).getMonth();
            this.updateMPMonth(this.mpSelMonth);
            this.mpSelYear = (this.activeDate || this.value).getFullYear();
            this.updateMPYear(this.mpSelYear);

            this.monthPicker.show();
            //this.monthPicker.slideIn('t', {duration:.2});
        },

        updateMPYear : function(y){

            if ( this.noPastYears ) {
                var minYear = new Date().getFullYear();
                if ( y < (minYear+4) ) {
                    y = minYear+4;
                }
            }

            this.mpyear = y;
            var ys = this.mpYears.elements;
            for(var i = 1; i <= 10; i++){
                var td = ys[i-1], y2;
                if((i%2) == 0){
                    y2 = y + Math.round(i * .5);
                    td.firstChild.innerHTML = y2;
                    td.xyear = y2;
                }else{
                    y2 = y - (5-Math.round(i * .5));
                    td.firstChild.innerHTML = y2;
                    td.xyear = y2;
                }
                this.mpYears.item(i-1)[y2 == this.mpSelYear ? 'addClass' : 'removeClass']('x-date-mp-sel');
            }
        },

        updateMPMonth : function(sm){
            this.mpMonths.each(function(m, a, i){
                m[m.dom.xmonth == sm ? 'addClass' : 'removeClass']('x-date-mp-sel');
            });
        },

        selectMPMonth: function(m){
            
        },

        onMonthClick : function(e, t){
            e.stopEvent();
            var el = new Ext.Element(t), pn;
            if(el.is('button.x-date-mp-cancel')){
                this.hideMonthPicker();
                //this.fireEvent("select", this, this.value);
            }
            else if(el.is('button.x-date-mp-ok')){
                this.update(new Date(this.mpSelYear, this.mpSelMonth, (this.activeDate || this.value).getDate()));
                //this.hideMonthPicker();
                this.fireEvent("select", this, this.value);
            }
            else if(pn = el.up('td.x-date-mp-month', 2)){
                this.mpMonths.removeClass('x-date-mp-sel');
                pn.addClass('x-date-mp-sel');
                this.mpSelMonth = pn.dom.xmonth;
            }
            else if(pn = el.up('td.x-date-mp-year', 2)){
                this.mpYears.removeClass('x-date-mp-sel');
                pn.addClass('x-date-mp-sel');
                this.mpSelYear = pn.dom.xyear;
            }
            else if(el.is('a.x-date-mp-prev')){
                this.updateMPYear(this.mpyear-10);
            }
            else if(el.is('a.x-date-mp-next')){
                this.updateMPYear(this.mpyear+10);
            }
        },

        onMonthDblClick : function(e, t){
            e.stopEvent();
            var el = new Ext.Element(t), pn;
            if(pn = el.up('td.x-date-mp-month', 2)){
                this.update(new Date(this.mpSelYear, pn.dom.xmonth, (this.activeDate || this.value).getDate()));
                //this.hideMonthPicker();
                this.fireEvent("select", this, this.value);
            }
            else if(pn = el.up('td.x-date-mp-year', 2)){
                this.update(new Date(pn.dom.xyear, this.mpSelMonth, (this.activeDate || this.value).getDate()));
                //this.hideMonthPicker();
                this.fireEvent("select", this, this.value);
            }
        },

        hideMonthPicker : function(disableAnim){
            Ext.menu.MenuMgr.hideAll();
        },

        
        showPrevMonth : function(e){
           this.update(this.activeDate.add("mo", -1));
        },

        
        showNextMonth : function(e){
            this.update(this.activeDate.add("mo", 1));
        },

        
        showPrevYear : function(){
            this.update(this.activeDate.add("y", -1));
        },

        
        showNextYear : function(){
            this.update(this.activeDate.add("y", 1));
        },

        update : function( date ) {
            this.activeDate = date;
            this.value = date;

            if(!this.internalRender){
                var main = this.el.dom.firstChild;
                var w = main.offsetWidth;
                this.el.setWidth(w + this.el.getBorderWidth("lr"));
                Ext.fly(main).setWidth(w);
                this.internalRender = true;
                
                if(Ext.isOpera && !this.secondPass){
                    main.rows[0].cells[1].style.width = (w - (main.rows[0].cells[0].offsetWidth+main.rows[0].cells[2].offsetWidth)) + "px";
                    this.secondPass = true;
                    this.update.defer(10, this, [date]);
                }
            }
        }

    });

    Ext.reg('monthpicker', Ext.ux.MonthPicker);

    Ext.ux.MonthItem = function(config){
        Ext.ux.MonthItem.superclass.constructor.call(this, new Ext.ux.MonthPicker(config), config);
        
        this.picker = this.component;
        this.addEvents('select');
        
        this.picker.on("render", function(picker){
            picker.getEl().swallowEvent("click");
            picker.container.addClass("x-menu-date-item");
        });

        this.picker.on("select", this.onSelect, this);
    };

    Ext.extend(Ext.ux.MonthItem, Ext.menu.Adapter, {
            onSelect : function(picker, date){
            this.fireEvent("select", this, date, picker);
            Ext.ux.MonthItem.superclass.handleClick.call(this);
        }
    });

    Ext.ux.MonthMenu = function(config){
        Ext.ux.MonthMenu.superclass.constructor.call(this, config);
        this.plain = true;
        var mi = new Ext.ux.MonthItem(config);
        this.add(mi);

        this.picker = mi.picker;
        
        this.relayEvents(mi, ["select"]);
    };

    Ext.extend(Ext.ux.MonthMenu, Ext.menu.Menu, {
        cls:'x-date-menu'
    });
        
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
        
        <!--excel,print button  
        <ul>
            <li class="hm_button margin_r3"><span class="icon_excel"></span><a ><fmt:message key='aimir.button.excel'/></a></li>
            <li class="hm_button"><span class="icon_print"></span><a ><fmt:message key='aimir.report.print'/></a></li> 
        </ul>-->
    
        <!-- tab -->
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
                    <li><input id="dailyStartDate" type="text" readonly="readonly" class="daily_input"></li>
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
                    <li class="hm_button margin_l3"><a href="javascript:searchTab();"><fmt:message key='aimir.button.search'/></a></li>
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
        <div class="title_basic" id="title" ></div>
        <div class="margin_t30">
            <div class="today_left"><span class="img_elec"></span></div>
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
    
    <!-- tab 3: equip -->
    <div id="deviceSpecificDivTab">
    
        <div class="clear margin_10">

            <div class="title_basic">
                <span class="icon_title_blue"></span><fmt:message key='aimir.hems.label.deviceSpecificCompare'/>
            </div>
           <!--  <div id="deviceSpecificTotal"></div>-->
            <div class="divbox" id="deviceSpecificChart1" >
                The chart will appear within this DIV. This text will be replaced by the chart.
            </div>
        </div>
        
        <div class="margin_10" >
            <div class="title_basic"><span class="icon_title_blue"></span><fmt:message key='aimir.locationUsage.usage'/></div>
            <div class="margin_t5" id="deviceSpecificChart2" >
                The chart will appear within this DIV. This text will be replaced by the chart.
            </div>
        </div>
        
    </div>
    <!--// tab 3: equipment -->
</div>
</body>
</html>