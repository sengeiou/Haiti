<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
        // 공급사ID
        var supplierId = "${supplierId}";
        var flex;
        var initStdDate = new String();
        var initEndDate = new String();
        var tabs = {hourly:1,daily:0,period:1,weekly:1,monthly:1,monthlyPeriod:1,weekDaily:0,seasonal:0,yearly:1};
        var searchCondition;
        var chartType = null;
        var beforeChartType = null;
        var selectType = null;

        var fcChartDataXml = null;
        var fcOverChartDataXml = new Array();

        // 탭명칭 변경시 값입력
        var tabNames = {hourly:'',daily:'',period:'<fmt:message key="aimir.daily"/>',weekly:'<fmt:message key="aimir.weekdaily"/>',
                        monthly:'<fmt:message key="aimir.weekly"/>',monthlyPeriod:'<fmt:message key="aimir.monthly"/>',
                        weekDaily:'',seasonal:'',yearly:'<fmt:message key="aimir.seasonal"/>'};

        // YYYYMMDD ▶ LocalDateType
        function dateFormatChange(baseDate){

            $.getJSON("${ctx}/common/convertLocalDate.do"
               ,{dbDate:baseDate, supplierId:supplierId}
               ,function(json) {
                   return json.localDate;
                });
        }

        function dateFormatChangeHour(baseDate) {
            var tmpDate = new String(baseDate);
            var changeValue = tmpDate.substring(8, 10);
            return changeValue;
        }

        var tabType = ${tabType};
        $(document).ready(function(){
            flex = getFlexObject('meteringChartFlex');

            //tabType = ${tabType};

            var dateType = new Array(DateType.HOURLY,'',DateType.PERIOD,DateType.WEEKLY,DateType.MONTHLY,DateType.MONTHLYPERIOD,'','',DateType.YEARLY);
            var stdDate = ${startDate};
            var endDate = ${endDate};

            var inputDate = new Object();
            inputDate._dateType = dateType[tabType];
            var comboVal = ${comboValue};

            if(tabType==0) {
                inputDate.hourlyStartDate       = dateFormatChange(stdDate);
                inputDate.hourlyEndDate         = dateFormatChange(endDate);
                inputDate.hourlyStartHourCombo  = dateFormatChangeHour(stdDate);
                inputDate.hourlyEndHourCombo    = dateFormatChangeHour(endDate);
            } else if(tabType==2) {
                inputDate.periodType= ${comboValue};
                inputDate.periodStartDate       = dateFormatChange(stdDate);
                inputDate.periodEndDate         = dateFormatChange(endDate);
            } else if(tabType==3) {
                inputDate.weeklyYearCombo       = ${startYear};
                inputDate.weeklyMonthCombo      = ${startMonth};
                inputDate.weeklyWeekCombo       = comboVal;
            } else if(tabType==4) {
                inputDate.monthlyYearCombo      = ${startYear};
                inputDate.monthlyMonthCombo     = ${startMonth};
            } else if(tabType==5) {
                inputDate.monthlyPeriodStartYearCombo   = ${startYear};
                inputDate.monthlyPeriodStartMonthCombo  = ${startMonth};

                inputDate.monthlyPeriodEndYearCombo     = ${endYear};
                inputDate.monthlyPeriodEndMonthCombo    = ${endMonth};
            } else if(tabType==8) {
                 inputDate.yearlyYearCombo              = ${startYear};
            } else {
                return;
            }

            inputDate.searchStartDate = stdDate;
            inputDate.searchEndDate   = endDate;

            tabs.InputDate = inputDate;

            initStdDate=stdDate+"";
            initEndDate= endDate+"";

            //setSearchCondition();
            //updateFChart(searchCondition, tabType);
            //initAfterTabFinished2();

            //$(function() { $('#_hourly')        .bind('click', function(event) { removeOverChartSelect(); } ); });
            ////$(function() { $('#_daily')         .bind('click', function(event) { removeOverChartSelect(); } ); });
            //$(function() { $('#_period')        .bind('click', function(event) { addOverChartSelect(); } ); });
            //$(function() { $('#_weekly')        .bind('click', function(event) { addOverChartSelect(); } ); });
            //$(function() { $('#_monthly')       .bind('click', function(event) { addOverChartSelect(); } ); });
            //$(function() { $('#_monthlyPeriod') .bind('click', function(event) { addOverChartSelect(); } ); });
            ////$(function() { $('#_weekdaily')     .bind('click', function(event) { removeOverChartSelect(); } ); });
            ////$(function() { $('#_seasonal')      .bind('click', function(event) { removeOverChartSelect(); } ); });
            //$(function() { $('#_yearly')        .bind('click', function(event) { removeOverChartSelect(); } ); });
                
        });

        var isOverChartControl = false;
        function controlOverChartSelect() {
            if (isOverChartControl) {
                return;
            }

            $(function() { $('#_hourly')        .bind('click', function(event) { removeOverChartSelect(); } ); });
            //$(function() { $('#_daily')         .bind('click', function(event) { removeOverChartSelect(); } ); });
            $(function() { $('#_period')        .bind('click', function(event) { addOverChartSelect(); } ); });
            $(function() { $('#_weekly')        .bind('click', function(event) { addOverChartSelect(); } ); });
            $(function() { $('#_monthly')       .bind('click', function(event) { addOverChartSelect(); } ); });
            $(function() { $('#_monthlyPeriod') .bind('click', function(event) { addOverChartSelect(); } ); });
            //$(function() { $('#_weekdaily')     .bind('click', function(event) { removeOverChartSelect(); } ); });
            //$(function() { $('#_seasonal')      .bind('click', function(event) { removeOverChartSelect(); } ); });
            $(function() { $('#_yearly')        .bind('click', function(event) { removeOverChartSelect(); } ); });

            var tabTypeVal = $('#searchDateType').val();
            if (tabTypeVal == 0 || tabTypeVal == 8) {
                removeOverChartSelect();
            }

            isOverChartControl = true;

            
            //var tabTypeVal = $('#searchDateType').val();
            //alert("tabTypeVal:"+tabTypeVal);
            //switch(tabTypeVal) {
            //    case "0":
            //        removeOverChartSelect();
            //        break;
            //    case "8":
            //        removeOverChartSelect();
            //        break;
            //    default:
            //        addOverChartSelect();
            //        break;
            //}
        }            


        function addOverChartSelect() {
            //alert("addOverChartSelect");
            $("#overChartRow").show();
            //$("input:radio[name='chart_type'][value='over']").show();
        }

        function removeOverChartSelect() {
            //alert(">> "+$("input:radio[name='chart_type'][value='over']").is(":checked"));
            var tabTypeVal = $('#searchDateType').val();
            
            if ((tabTypeVal == 0 || tabTypeVal == 8) && $("input:radio[name='chart_type'][value='over']").is(":checked")) {
                $("input:radio[name='chart_type'][value='column']").attr("checked","checked");
                $("#chart").show();
                $("#overlay").hide();
            }
            $("#overChartRow").hide();
        }

        
        var contractCount = 0;
        var customerNames = new Array();
        var contractNumbers = new Array();
    
        //function initFlexSet() {
        //    contractCount = 0;
        //    customerNames = new Array();
        //    contractNumbers = new Array();
        //
        //    for (var i = 0; i < $("input:checkbox").length; i++) {
        //        if ($("input:checkbox")[i].checked) {
        //            customerNames.push($("#customerName"+i).val());
        //            contractNumbers.push($("input:checkbox")[i].value);
        //        }
        //    }
        //    contractCount = contractNumbers.length;
        //    //custName = new Array();
        //
        //    //for(var i=0;i<contractCount;i++) {
        //    //    custName[i]=$("#customerName"+i).val();
        //    //}
        //    flex.flexParamSet(customerNames,contractCount);
        //
        //    var searchCondition = new Array();
        //    var tmpTab = ${tabType};
        //    var dateType = new Array("hour","","day","dayWeek","week","month","","","season");
        //    tabType = dateType[tmpTab];
        //
        //    searchCondition[0] = initStdDate;
        //    searchCondition[1] = initEndDate;
        //
        //    //searchCondition[2] = $("input:radio[name='chart_type']:checked").val();
        //    //searchCondition[3] = $("input:radio[name='value_type']:checked").val();
        //    chartType = $("input:radio[name='chart_type']:checked").val();
        //    selectType = $("input:radio[name='value_type']:checked").val();
        //    
        //    searchCondition[2] = chartType;
        //    searchCondition[3] = selectType;
        //    //searchCondition[4] = "";
        //    //var jcount=0;
        //    //for (var i = 0; i < $("input:checkbox").length; i++) {
        //    //    if ($("input:checkbox")[i].checked == true) {
        //    //        if(jcount!= 0) {
        //    //             searchCondition[4] = searchCondition[4] + "," + $("input:checkbox")[i].value;
        //    //        }
        //    //        else {
        //    //            searchCondition[4] = $("input:checkbox")[i].value;
        //    //        }
        //    //        jcount=jcount+1;
        //    //    }
        //    //}
        //    searchCondition[4] = contractNumbers.join(",");
        //    // 채널값에 default로 Usage(1)세팅
        //    $("input:radio[name=channelCode]").filter('[value=1]').attr("checked", "checked");
        //    searchCondition[5] = $("input:radio[name=channelCode]:checked").val();//기본 채널값
        //    searchCondition[6] = supplierId;
        //
        //    flex.searchMeteringData(searchCondition,tabType);
        //    //updateFChart(initStdDate, initEndDate, tmpTab);
        //    //updateFChart(searchCondition, tmpTab);
        //}

        function initFlexSet() {
            //contractCount = 0;
            //customerNames = new Array();
            //contractNumbers = new Array();
            //
            //for (var i = 0; i < $("input:checkbox").length; i++) {
            //    if ($("input:checkbox")[i].checked) {
            //        customerNames.push($("#customerName"+i).val());
            //        contractNumbers.push($("input:checkbox")[i].value);
            //    }
            //}
            //contractCount = contractNumbers.length;
            //custName = new Array();
        
            //for(var i=0;i<contractCount;i++) {
            //    custName[i]=$("#customerName"+i).val();
            //}
            flex.flexParamSet(customerNames,contractCount);
            flex.searchMeteringData(searchCondition, tabType);
        }

        function initAfterTabFinished() {
            setSearchCondition();
            $("#chart").show();
            //updateFChart(searchCondition, tabType);
            updateFChart();


        }
        
        // 공통조회화면 필수 function
        function send(){
            setSearchCondition();
            //var chart_type = $("input:radio[name='chart_type']:checked").val();
            //if(chart_type == "over" || chart_type == "load" || chart_type == "inout") {
            if(chartType == "over" || chartType == "load" || chartType == "inout") {
                updateOverChart();
            } else {
                searchMeteringData();
                updateFChart();
            }
        }

        /*
         * Flex로 넘어가는 value 설정
         * [0] : 시작일자
         * [1] : 종료일자
         * [2] : 차트종류
         * [3] : 선택항목
         * [4] : 고객계약번호
         * [5] : 채널값
         */
        function setSearchCondition() {
            searchCondition = new Array();
            contractCount = 0;
            contractNumbers = new Array();
            customerNames = new Array();
            var meterNos = new Array();

            for (var i = 0; i < $("input:checkbox").length; i++) {
                if ($("input:checkbox")[i].checked) {
                    customerNames.push($("#customerName"+i).val());
                    contractNumbers.push($("input:checkbox")[i].value);
                    meterNos.push($("#meterNo"+i).val());
                }
            }
            contractCount = contractNumbers.length;

            //var dateType = new Array("hour","","day","dayWeek","week","month","","","season");
            //tabType = dateType[$('#searchDateType').val()];
            tabType = $('#searchDateType').val();

            var startDate = $('#searchStartDate').val();
            var endDate = $('#searchEndDate').val();
            var reg = /^\d+$/;

            if (startDate.length != 8 || !reg.test(startDate) || endDate.length != 8 || !reg.test(endDate)) {
                startDate = "${currentDate}";
                endDate = "${currentDate}";
            }

            if ($('#searchDateType').val() == 0) {
                searchCondition[0] = startDate + $('#searchStartHour').val();
                searchCondition[1] = endDate + $('#searchEndHour').val();
            } else {
                searchCondition[0] = startDate;
                searchCondition[1] = endDate;
            }

            //searchCondition[2] = $("input:radio[name='chart_type']:checked").val();
            //searchCondition[3] = $("input:radio[name='value_type']:checked").val();
            chartType = $("input:radio[name='chart_type']:checked").val();
            selectType = $("input:radio[name='value_type']:checked").val();
            searchCondition[2] = chartType;
            searchCondition[3] = selectType;
            //searchCondition[4] = "";
            //var jcount=0;
            //var contractNum = new Array();
            //for (var i = 0; i < $("input:checkbox").length; i++) {
            //    if ($("input:checkbox")[i].checked) {
            //        contractNum.push($("input:checkbox")[i].value);
            //        //if(jcount!= 0) {
            //            //searchCondition[4] = searchCondition[4] + "," + $("input:checkbox")[i].value;
            //        //} else {
            //        //    searchCondition[4] = $("input:checkbox")[i].value;
            //        //}
            //        jcount=jcount+1;
            //    }
            //}
            searchCondition[4] = contractNumbers.join(",");
            searchCondition[5] = $("input:radio[name=channelCode]:checked").val();
            searchCondition[6] = supplierId;
            searchCondition[7] = meterNos.join(",");
        }

        //function searchComparisonChartData(){
        //    setSearchCondition();
        //    flex.searchComparisonChartData(searchCondition);
        //}

        function searchMeteringData(){
            flex.searchMeteringData(searchCondition, tabType);
        }

        function getFmtMessage(){
            var fmtMessage = new Array();

            fmtMessage[0] = "<fmt:message key="aimir.number"/>";
            fmtMessage[1] = "<fmt:message key="aimir.customerid"/>";
            fmtMessage[2] = "<fmt:message key="aimir.customername"/>";
            fmtMessage[3] = "<fmt:message key="aimir.meteringtime"/>";//검침시각
            //fmtMessage[4] = "<fmt:message key="aimir.usage"/>";//사용량

            for (var i = 0; i < $("input:radio[name=channelCode]").length; i++) {
                if ($("input:radio[name=channelCode]")[i].checked == true) {
                    messageLabel = $("input:hidden[name=channelName]")[i].value;
                }
            }
            
            fmtMessage[4] = messageLabel;//사용량
            return fmtMessage;
        }

        function changeChartView(chart_type) {
            beforeChartType = chartType;
            chartType = chart_type;

            if(chart_type == "over") {
                $("#chart").hide();
                $("#overlay").show();
                setSearchCondition();
                updateOverChart();
            } else if(chart_type == "load") {

            } else if(chart_type == "inout") {
                
            } else {
                if(beforeChartType == "over" || beforeChartType == "load" || beforeChartType == "inout") {
                    setSearchCondition();
                    $("#chart").show();
                    $("#overlay").hide();
                    //updateFChart(searchCondition, tabType);
                    updateFChart();
                } else {
                    fcChartRender();
                }
            }
        }

        function changeSelectType(select_type) {
            //var chart_type = $("input:radio[name='chart_type']:checked").val();

            /*
            if(document["meteringChartFlexOt"]==null){
                window["meteringChartFlexEx"].changeSelectType(chart_type, select_type);
            }else{
                document["meteringChartFlexOt"].changeSelectType(chart_type, select_type);
            }
            */

            //flex = getFlexObject('meteringChartFlex');
            //flex.changeSelectType(chartType, select_type);
            selectType = select_type;
            updateFChartXml();
        }

        function controlCombo(num) {
            if ($("#multiCombo"+num).is(":hidden")) {
                $("#multiCombo"+num).show();
            } else {
                $("#multiCombo"+num).hide();
            }
        }

        var closeTimer1;
        var closeTimer2;
        var closeTimer3;

        function startCloseCombo(num) {
            var intval = 500;
            switch(num) {
                case 1:
                    closeTimer1 = window.setTimeout("closeCombo(1)", intval);
                    break;
                case 2:
                    closeTimer2 = window.setTimeout("closeCombo(2)", intval);
                    break;
                case 3:
                    closeTimer3 = window.setTimeout("closeCombo(3)", intval);
                    break;
            }
        }

        function endCloseCombo(num) {
            if (eval("closeTimer"+num) != null) {
                window.clearTimeout(eval("closeTimer"+num));
            }
        }

        function closeCombo(num) {
            if ($("#multiCombo"+num).is(":visible")) {
                $("#multiCombo"+num).hide();
            }
        }

        var searchChartData = new Array();

        //function updateFChart(_searchCondition, _tabType) {
        function updateFChart() {
            //var searchCondition = null;

            //if (_searchCondition != null) {
            //    searchCondition = _searchCondition;
            //} else {
            //    //setSearchCondition();
            //    //searchCondition = this.searchCondition;
            //}

            var searchStartDate;
            var searchEndDate;
            var searchDateType;
            var obj = new Object();

            searchStartDate = searchCondition[0];
            searchEndDate = searchCondition[1];

            //if(_tabType != null) {
            //    searchDateType = _tabType;
            //} else {
            //    searchDateType = $('#searchDateType').val();
            //}
            searchDateType = $('#searchDateType').val();
            //alert("searchDateType:"+searchDateType);
            //chartType = $("input:radio[name=chart_type]:checked").val();

            $.ajax({
                type:"POST",
                url :"${ctx}/gadget/mvm/getMvmChartMeteringData.do",
                data : {searchStartDate : searchStartDate,
                    searchEndDate : searchEndDate,
                    searchDateType : searchDateType,
                    contractNumbers : searchCondition[4],
                    channel : searchCondition[5],
                    type : $("#mvmMiniType").val(),
                    meterNos : searchCondition[7],
                    supplierId : supplierId},
                dataType:"json",
                success : function(json) {
                    searchData = new Array();
                    searchData = json.searchData;
                    updateFChartXml();
                }
            
            });
            
        }

        function updateFChartXml() {
            //var searchData = flex.getChartDataList();
            //var selectType = $("input:radio[name='value_type']:checked").val();

            //alert(searchData);
            var sdataLen =  (searchData != null) ? searchData.length : 0;
            //var dataCount = sdataLen / contractCount;
            var labelStep = (sdataLen <= 6) ? 1 : sdataLen/6;

            fcChartDataXml = "<chart "
                   + "chartLeftMargin='5' "
                   + "chartRightMargin='10' "
                   + "chartTopMargin='10' "
                   + "chartBottomMargin='10' "
                   + "showValues='0' "
                   + "showLabels='1' "
                   + "showLegend='1' "
                   + "useRoundEdges='0' "
                   + "legendPosition='RIGHT' "
                   + "labelDisplay='STAGGER' "
                   + "labelStep='" + labelStep + "' "
                   + "decimals='3' "
                   + "forceDecimals='1' "
                   + "toolTipSepChar='{br}' "
                   + fChartStyle_Common
                   + fChartStyle_Font;
            if(chartType == "column") {
                fcChartDataXml += fChartStyle_MSColumn3D_nobg;
            } else if(chartType == "bar") {
                fcChartDataXml += fChartStyle_Column2D_nobg;
            } else if(chartType == "line") {
                fcChartDataXml += fChartStyle_MSLine_nobg;
            } else if(chartType == "plot") {
                fcChartDataXml += fChartStyle_MSCol3DLine_nobg;
            } else {
                fcChartDataXml += fChartStyle_MSCol3DLine_nobg;
            }
            fcChartDataXml += ">";

            var categories = "<categories>";
            //var trendlines = "<trendLines>";
            var datasets = new Array();
            // usage 삭제
            //datasets[1] = "<dataset seriesName='<fmt:message key='aimir.usage.kwh'/>'>";
            //var tempImdex = 2;
            //datasets[0] = "<dataset seriesName='<fmt:message key='aimir.co2formula'/>'>";
            //for (var i = 0; i < $("input:checkbox[name=channelCode]").length; i++) {
            //    if ($("input:checkbox[name=channelCode]")[i].checked == true) {
            //        datasets[tempIndex++] = "<dataset seriesName='"+ $("input:hidden[name=channelName]")[i].value +"'>";
            //        //tempImdex++;
            //    }
            //}
            for (var i = 0 ; i < contractCount ; i++) {
                datasets.push("<dataset seriesName='"+ customerNames[i] +"'>");
            }

            var tmpLabel = "";
            //var categoriesCount = 0;
            //var maxVal = 0;
            //var dsIndex = 0;

            // test
            var tempStr = "";
            var valueProp = null;
            //tempImdex = 1;
            var setValue = null;

            if(sdataLen > 0) {

                for(var index = 0; index < sdataLen ; index++) {

                    if(searchData[index].firstCol != null) {

                        if(searchData[index].firstCol != tmpLabel) {
                            categories += "<category label='"+searchData[index].firstCol +"' x='"+searchData[index].firstCol +"'/>";
                            tmpLabel = searchData[index].firstCol;
                        }

                        for (var i = 0 ; i < contractCount ; i++) {
                            switch(selectType) {
                                case "1":
                                    valueProp = "user"+i+"Value";
                                    break;

                                case "2":
                                    valueProp = "user"+i+"Avg";
                                    break;

                                case "3":
                                    valueProp = "user"+i+"Max";
                                    break;

                                case "4":
                                    valueProp = "user"+i+"Co2";
                                    break;
                            }
                            //datasets[i] += "<set value='"+(searchData[index][valueProp]).replace(/,/g, "")+"' x='"+tmpLabel+"' y='"+(searchData[index][valueProp]).replace(/,/g, "")+"'/>";
                            setValue = searchData[index][valueProp]+"";
                            
                            datasets[i] += "<set value='"+setValue.replace(/,/g, "")+"' x='"+tmpLabel+"' y='"+setValue.replace(/,/g, "")+"'/>";
                        }
                    }
                }

            } else {
                categories += "<category label='' />";

                for(var i=0 ; i < contractCount ; i++ ) {
                    datasets[i] += "<set value='' />";
                }
            }

            categories += "</categories>";
            //trendlines += "</trendLines>";
            fcChartDataXml += categories;

            for(var i=0 ; i < contractCount ; i++ ) {
                fcChartDataXml += datasets[i] + "</dataset>";
            }

            //fcChartDataXml += trendlines;
            fcChartDataXml += "</chart>";
            fcChartRender();
        }

        //window.onresize = fcChartRender;
        function fcChartRender() {
            var chartSwf = null;
            switch(chartType){
                case "column":
                    chartSwf = "${ctx}/flexapp/swf/fcChart/MSColumn3D.swf";
                    break;

                case "bar":
                    chartSwf = "${ctx}/flexapp/swf/fcChart/MSBar2D.swf";
                    break;

                case "line":
                    chartSwf = "${ctx}/flexapp/swf/fcChart/MSLine.swf";
                    break;

                case "plot":
                    chartSwf = "${ctx}/flexapp/swf/fcChart/Scatter.swf";
                    break;

                default:
                    chartSwf = "${ctx}/flexapp/swf/fcChart/MSColumn3D.swf";
                    break;
            }

            fcChart = new FusionCharts(chartSwf, "myChartId", $('#fcChartDiv').width(), "210", "0", "0");
            //alert("fcChartDataXml:"+fcChartDataXml);
            fcChart.setDataXML(fcChartDataXml);
            fcChart.setTransparent("transparent");
            fcChart.render("fcChartDiv");
        }

        // Overlay Chart
        function updateOverChart() {
            //var searchCondition = null;

            //setSearchCondition();
            //searchCondition = this.searchCondition;

            var searchStartDate = searchCondition[0];
            var searchEndDate = searchCondition[1];
            var searchDateType = $('#searchDateType').val();
            //var obj = new Object();

            //searchStartDate 
            //searchEndDate = searchCondition[1];
            //searchDateType = $('#searchDateType').val();

            $.getJSON("${ctx}/gadget/mvm/getMvmChartMeteringDataOverChart.do"
                    ,{searchStartDate:searchStartDate,
                        searchEndDate:searchEndDate,
                        searchDateType:searchDateType,
                        contractNumbers:searchCondition[4],
                        channel:searchCondition[5],
                        type:$("#mvmMiniType").val(),
                        supplierId:supplierId}
                    ,function(json) {
                        var overlayData = json.overlayData;
                        var categoriesData = json.categoriesData;
                        updateOverChartXml(overlayData, categoriesData);
                    }
            );
        }

        function updateOverChartXml(overlayData, categoriesData) {
            fcOverChartDataXml = new Array();
            //var datalen = overlayData.length;
            var categorylen = categoriesData.length;
            var categoryStart = (tabType == 4 || tabType == 5) ? 1 : 0;
            var labelStep = categorylen/6;
            var caption = "";
            for (var i = 0 ; i < contractCount ; i++) {
                fcOverChartDataXml[i] = "<chart "
                    + "caption='" + customerNames[i] + "(" + contractNumbers[i] + ")" + "' "
                    + "chartLeftMargin='5' "
                    + "chartRightMargin='10' "
                    + "chartTopMargin='10' "
                    + "chartBottomMargin='10' "
                    + "showValues='0' "
                    + "showLabels='1' "
                    + "showLegend='1' "
                    + "useRoundEdges='0' "
                    + "legendPosition='RIGHT' "
                    + "labelDisplay='STAGGER' "
                    + "labelStep='" + labelStep + "' "
                    + "decimals='3' "
                    + "forceDecimals='1' "
                    + "toolTipSepChar='{br}' "
                    + fChartStyle_Common
                    + fChartStyle_Font;
                fcOverChartDataXml[i] += fChartStyle_MSLine_nobg;
                fcOverChartDataXml[i] += ">";

                var categories = "<categories>";
                var datasets = new Array();
                var nodata = false;

                for (var k = 0 ; k < categorylen ; k++) {
                    categories += "<category label='" + categoriesData[k] + "' x='" + categoriesData[k] + "'/>";
                }
                //alert("categorylen:"+categorylen);
                if (overlayData[contractNumbers[i]] != null) {
                    var overlayChartData = overlayData[contractNumbers[i]];
                
                    var sdataLen =  overlayChartData.length;
                    //for (var i = 0 ; i < contractCount ; i++) {
                    //    datasets.push("<dataset seriesName='"+ custName[i] +"'>");
                    //}

                    //var tmpLabel = "";
                    //var categoriesCount = 0;
                    //var maxVal = 0;
                    //var dsIndex = 0;
        
                    // test
                    var tempStr = "";
                    var valueProp = null;
                    //tempImdex = 1;
                    var setValue = null;
        
                    for(var index = 0; index < sdataLen ; index++) {
                        datasets[index] = "<dataset seriesName='" + overlayChartData[index].yyyymmdd + "'>";
                        
                        for (var k = categoryStart ; k < (categorylen + categoryStart) ; k++) {
                            //if (index == 0) {
                            //    categories += "<category label='" + categoriesData[k] + "' x='" + categoriesData[k] + "'/>";
                            //}
                            datasets[index] += "<set value='" + overlayChartData[index]["value_" + ((k < 10)?"0":"") + k] + "' />";
                            //datasets[index] += "<set value='" + overlayChartData[index]["value_00"] + "' />";
                        }
                    }

                    if (sdataLen == 0) {
                        nodata = true;
                    }
                } else {
                    nodata = true;
                }

                if (nodata) {
                    //categories += "<category label='' />";
                    datasets[0] = "<dataset seriesName=''><set value='' />";
                }

                categories += "</categories>";
                //trendlines += "</trendLines>";
                fcOverChartDataXml[i] += categories;
    
                for(var l = 0 ; l < datasets.length ; l++ ) {
                    fcOverChartDataXml[i] += datasets[l] + "</dataset>";
                }

                fcOverChartDataXml[i] += "</chart>";
            }

            fcOverChartRender();
        }
        

        function fcOverChartRender() {
            var fcOverChart = new Array();
            var xmlLen = fcOverChartDataXml.length;
            var chartDiv = $("#overlay");
            var htmlText = "";
            for (var i = 0 ; i < xmlLen ; i++) {
                htmlText += "<div id='overDiv"+i+"'></div>";
            }
            chartDiv.html(htmlText);

            for (var i = 0 ; i < xmlLen ; i++) {
                fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSLine.swf", "myChartId"+i, $("#overDiv"+i).width(), "210", "0", "0");
                fcChart.setDataXML(fcOverChartDataXml[i]);
                //alert(fcOverChartDataXml[i]);
                fcChart.setTransparent("transparent");
                fcChart.render("overDiv"+i);
                //fcOverChart[i] = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSLine.swf", "myChartId"+i, $("#overDiv"+i).width(), "210", "0", "0");
                //fcOverChart[i].setDataXML(fcOverChartDataXml[i]);
                //alert(fcOverChartDataXml[i]);
                //fcOverChart[i].setTransparent("transparent");
                //fcOverChart[i].render("overDiv"+i);
            }
        }
        

        /*]]>*/
    </script>

</head>
<body>
<div class="mvm-popwin-body">
<input type="hidden" id="mvmMiniType" name="mvmMiniType" value="${mvmMiniType}"/>

    <div class="mvm-custlist">
    <ul>
      <!--li id="custlist"><label><fmt:message key="aimir.customerlist"/></label></li-->
        <c:forEach items="${customerInfo}" var="customer" varStatus="idx">
        <li>
            <span class="mvm-custlist-cust"><!--button class="checkbox-customer-on"></button--><input id="customerInfo" name="customerInfo" type="checkbox" value="${customer.contractNo}"  checked="checked" class="checkbox_space2"></span>
            <span class="mvm-custlist-name blue11pt">${customer.customerName}(${customer.contractNo})</span>
            <span>
                <input id="customerName${idx.count-1}" name="customerName"  type="hidden" value="${customer.customerName}"/>
                <input id="meterNo${idx.count-1}" name="meterNo"  type="hidden" value="${customer.meterNo}"/>
            </span>
            
        </li>
        </c:forEach>
     </ul>
    </div>

    <!-- search-background DIV (S) -->
    <div class="search-bg-withtabs" style="height:98px;">

        <div class="dayoptions" style="border-top:1px solid #d3e6f5;">
            <%@ include file="../commonDateTab.jsp" %>
        </div>
        <div class="dashedline"><ul><li></li></ul></div>

        <div class="mvm-multiselect border-blue" id="channelList">
            <table class="wfree" onclick="controlCombo(1);" onmouseover="endCloseCombo(1);" onmouseout="startCloseCombo(1);" style="width:100%;">
                <tr><td class="space10"></td>
                    <td class="graybold11pt withinput"><fmt:message key="aimir.channelid"/></td>
                </tr>
            </table>

            <div id="multiCombo1" class="line" style="display:none;" onmouseover="endCloseCombo(1);" onmouseout="startCloseCombo(1);">
                <table class="wfree">
                    <c:set var="cnt" value="${fn:length(channelList)}"/>
                    <c:forEach items="${channelList}" var="channel" varStatus="idx">
                    <tr>
                        <td><input class="radio_space2" id="channelCode" name="channelCode" type="radio" value="${channel.codeId}" ${((idx.count-1) == 0)?"checked='checked'":""}"></td>
                        <td class="gray11pt withinput">${channel.codeName}<input id="channelName${idx.count-1}" name="channelName"  type="hidden" value="${channel.codeName}"></input></td>
                    </tr>
                    </c:forEach>

                    <c:if test="${cnt == 0}">
                    <tr>
                        <td><input class="radio_space2" id="channelCode" name="channelCode" type="radio" value="" checked="checked"/></td>
                        <td class="gray11pt withinput"><fmt:message key="aimir.none"/><input id="channelName0" name="channelName"  type="hidden" value=""></input></td>
                    </tr>
                    </c:if>
                </table>
            </div>
        </div>

        <div class="mvm-multiselect border-blue" style="left:220px;">
            <table class="wfree" onclick="controlCombo(2);" onmouseover="endCloseCombo(2);" onmouseout="startCloseCombo(2);" style="width:100%;">
                <tr><td class="space10"></td>
                    <td class="graybold11pt withinput"><fmt:message key="aimir.select"/> <fmt:message key="aimir.item"/></td></tr>
            </table>

            <div id="multiCombo2" class="line" style="display:none;" onmouseover="endCloseCombo(2);" onmouseout="startCloseCombo(2);">
                <table class="wfree">
                    <tr><td><input name="value_type" type="radio" value="1" checked="checked" onclick="changeSelectType(value)" class="radio_space2"></td>
                        <td class="gray11pt withinput"><fmt:message key="aimir.usage"/></td></tr>
                    <tr><td><input name="value_type" type="radio" value="2" onclick="changeSelectType(value)" class="radio_space2"></td>
                        <td class="gray11pt withinput"><fmt:message key="aimir.averageusage"/></td></tr>
                    <tr><td><input name="value_type" type="radio" value="3" onclick="changeSelectType(value)" class="radio_space2"></td>
                        <td class="gray11pt withinput"><fmt:message key="aimir.maximum"/><fmt:message key="aimir.usage"/></td></tr>
                    <tr><td><input name="value_type" type="radio" value="4" onclick="changeSelectType(value)" class="radio_space2"></td>
                        <td class="gray11pt withinput"><fmt:message key="aimir.co2formula"/></td></tr>
                </table>
            </div>
        </div>

        <div class="mvm-multiselect border-blue" style="left:440px;">
            <table class="wfree" onclick="controlOverChartSelect();controlCombo(3);" onmouseover="endCloseCombo(3);" onmouseout="startCloseCombo(3);" style="width:100%;">
                <tr><td class="space10"></td>
                    <td class="graybold11pt withinput"><fmt:message key="aimir.view.chart"/><!--fmt:message key="aimir.type2"/--></td></tr>
            </table>

            <div id="multiCombo3" class="line" style="display:none;" onmouseover="endCloseCombo(3);" onmouseout="startCloseCombo(3);">
                <table class="wfree">
                    <tr><td><input name="chart_type" class="radio_space2" type="radio" value="column" checked="checked" onclick="changeChartView(value)"></td>
                        <td class="gray11pt withinput">ColumnChart</td></tr>
                    <tr><td><input name="chart_type" class="radio_space2" type="radio" value="bar" onclick="changeChartView(value)"></td>
                        <td class="gray11pt withinput">BarChart</td></tr>
                    <tr><td><input name="chart_type" class="radio_space2" type="radio" value="line"  onclick="changeChartView(value)"></td>
                        <td class="gray11pt withinput">LineChart</td></tr>
                    <tr><td><input name="chart_type" class="radio_space2" type="radio" value="plot" onclick="changeChartView(value)"></td>
                        <td class="gray11pt withinput">PlotChart</td></tr>
                    <tr id="overChartRow"><td><input name="chart_type" class="radio_space2" type="radio" value="over" onclick="changeChartView(value)"></td>
                        <td class="gray11pt withinput">OverChart</td></tr>
                    <tr><td><input name="chart_type" class="radio_space2" type="radio" value="load" onclick="changeChartView(value)"></td>
                        <td class="gray11pt withinput">LoadChart</td></tr>
                    <tr><td><input name="chart_type" class="radio_space2" type="radio" value="inout" onclick="changeChartView(value)"></td>
                        <td class="gray11pt withinput">In/OutTimeChart</td></tr>
                </table>
            </div>
        </div>

    </div>
    <!-- search-background DIV (E) -->

    <div id="chart" class="gadget_body" style="display:none;">
        <div id="fcChartDiv">
            The chart will appear within this DIV. This text will be replaced by the chart.
        </div>

        <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="470px" id="meteringChartFlexEx">
            <param name="movie" value="${ctx}/flexapp/swf/MeteringDataChart.swf" />
            <param name="wmode" value="transparent" />
            <!--[if !IE]>-->
            <object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/MeteringDataChart.swf" width="100%" height="470px" id="meteringChartFlexOt">
            <param name="wmode" value="transparent" />
            <!--<![endif]-->
            <div>
                <h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
                <p><a href="http://www.adobe.com/go/getflashplayer"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash player" width="112" height="33" /></a></p>
            </div>
            <!--[if !IE]>-->
            </object>
            <!--<![endif]-->
        </object>
    </div>

    <div id="overlay" class="gadget_body" style="display:none;"></div>

</div>

</body>
</html>
