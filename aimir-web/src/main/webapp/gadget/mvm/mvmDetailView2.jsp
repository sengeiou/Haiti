<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <link href="${ctx}/js/extjs/resources/css/treegrid.css" rel="stylesheet" type="text/css"/>
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold !important;
        }

        /* no Icon */
        .no-icon {
            display: none;
            background-image:url(${ctx}/js/extjs/resources/images/default/s.gif) !important;
        }

        .x-treegrid-text {
            padding-right:4px !important;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <%-- Ext-JS 관련 --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <%-- TreeGrid 관련 js --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridSorter.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumnResizer.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridNodeUI.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridLoader.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumns.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGrid.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/util/commonUtil.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
        //공급사ID
        var supplierId="${supplierId}";
        var loginId="";
        var tabs = {};
        var tabNames = {};
        var reportChannel;

        var searchCondition;
        var fcChartDataXml;
        //var fcChartRateDataXml;
        var fcChartDataJson;
        var detailChartType;
        // Ondemand 권한
        var ondemandAuth = "${ondemandAuth}";

        /**
         * 유저 세션 정보 가져오기
         */
        $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                if(json.supplierId != ""){
                    loginId = json.loginId;
                    pageInit();
                }
            }
        );

        function pageInit() {
            //$(function() { $('#_rately')     .bind('click',function(event) { if(tabClickExec){changeViewAll(DateTabOther.RATE);} } ); });
            $(function() { $('#_lpintervals').bind('click',function(event) { if(tabClickExec){changeViewAll(DateTabOther.INTERVAL);} } ); });
            $(function() { $('#_hourly')     .bind('click',function(event) { if(tabClickExec){changeViewAll(DateType.HOURLY);} } ); });
            $(function() { $('#_daily')      .bind('click',function(event) { if(tabClickExec){changeViewAll(DateType.DAILY);} } ); });
            $(function() { $('#_weekly')     .bind('click',function(event) { if(tabClickExec){changeViewAll(DateType.WEEKLY);} } ); });
            $(function() { $('#_monthly')    .bind('click',function(event) { if(tabClickExec){changeViewAll(DateType.MONTHLY);} } ); });
            $(function() { $('#_weekdaily')  .bind('click',function(event) { if(tabClickExec){changeViewAll(DateType.WEEKDAILY);} } ); });
            $(function() { $('#_seasonal')   .bind('click',function(event) { if(tabClickExec){changeViewAll(DateType.SEASONAL);} } ); });
        }

        function dateFormatChangeHour(baseDate) {
            var tmpDate = new String(baseDate);
            var changeValue = tmpDate.substring(8, 10);
            return changeValue;
        }

        // YYYYMMDD ▶ LocalDateType
        function dateFormatChange(baseDate){

            var tmpDate = new String(baseDate);
            var dbDate = tmpDate.substring(0, 8);

            var rtnDate = "";

            $.getJSON("${ctx}/common/convertLocalDate.do"
               ,{dbDate:dbDate, supplierId:supplierId}
               ,function(json) {
                   rtnDate = new String(json.localDate);
                });

            return rtnDate;
        }

        // Chrome 최선버전에서 Ext-JS Grid 컬럼사이즈 오류 수정
        function extColumnResize() {
            Ext.override(Ext.grid.ColumnModel, {
                getColumnWidth : function(col) {
                    var width = this.config[col].width;
                    var stsize = 4;
                    var chsize = stsize/this.config.length;

                    if (typeof width != 'number') {
                        width = this.defaultWidth;
                    }

                    width = width - chsize;
                    return width;
                }
            });
        }

        Ext.onReady(function(){
            Ext.QuickTips.init();
            extColumnResize();

            if (ondemandAuth == "true") {
                $("#ondemandDiv").show();
            } else {
                $("#ondemandDiv").hide();
            }

            //$("#detailRateGridDiv").show();
            getSearchDate(DateTabOther.INTERVAL);
            updateFChart();
            getDetailDataList();
            $("#detailGridDiv").show();
            $("#detailAllGridDiv").show();
        });
		
        var beforeDateType = null;
        function changeViewAll(dateType) {
            if (beforeDateType != null && beforeDateType == dateType) {
                return;
            }

            /* if (dateType == DateTabOther.RATE) {
                $("#channelList").hide();
                $("#chartType").css("left", "");
                $("#detailRateGridDiv").show();
                $("#detailGridDiv").hide();
                $("#detailAllGridDiv").hide();

                $("#viewAll").attr("checked", "");
                $("#viewAllDiv").hide();

                if (detailRatelyDataGrid == null) {
                    makeDetailRatelyDataList();
                }
            } else */ if (dateType == DateTabOther.INTERVAL || dateType == DateType.HOURLY) {
                $("#channelList").show();
                $("#chartType").css("left", "220px");
                $("#viewAllDiv").show();
                //$("#detailRateGridDiv").hide();
                $("#detailGridDiv").hide();
                $("#detailAllGridDiv").show();

                if (detailHourlyDataGrid == null) {
                    makeDetailHourlyDataList();
                }
            } else {
                $("#channelList").show();
                $("#chartType").css("left", "220px");
               // $("#detailRateGridDiv").hide();
                $("#detailGridDiv").show();
                $("#detailAllGridDiv").hide();

                $("#viewAll").attr("checked", "");
                $("#viewAllDiv").hide();

                if (detailDataGrid == null) {
                    makeDetailDataList();
                }
            }

            if ((beforeDateType == null || beforeDateType != DateType.MONTHLY) && dateType == DateType.MONTHLY) {
                $("#weeklyComChart").show();
            } else if (beforeDateType == null || beforeDateType == DateType.MONTHLY) {
                $("#weeklyComChart").hide();
                $("#defaultChart").attr("checked", "checked");
                chanageChartControl(false);
            }

            beforeDateType = dateType;
        }

        // 공통조회화면 필수 function
        function send(){
            updateFChart();
            getDetailDataList();
        }

        function getFmtMessage(){
            var fmtMessage = new Array();
            fmtMessage[0] = "<fmt:message key="aimir.meteringtime"/>";//검침시각
            fmtMessage[1] = "<fmt:message key="aimir.co2formula"/>";//탄소배출량
            fmtMessage[2] = "<fmt:message key="aimir.usage.kwh"/>";//사용량
            fmtMessage[3] = "<fmt:message key="aimir.avg"/>";//평균
            fmtMessage[4] = "<fmt:message key="aimir.max"/>";//최대
            fmtMessage[5] = "<fmt:message key="aimir.min.upper"/>";//최소
            fmtMessage[6] = "<fmt:message key="aimir.sum"/>";//합계

            return fmtMessage;
        }

        //chart radio 선택
        function chanageChartControl(isRender) {
            //chart radio 보이기, calendar chart 숨기기
            $('#calendar_div').hide();
            var chartType = $("input:radio[name=chart_type]:checked").val();

            detailChartType = chartType;
            if (isRender != false) {
                fcChartRender();
            }
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

        // WeeklyComparisonChart 전용 update method
        function updateWeeklyFChart(dateOfMonth){
            var searchStartDate = $('#searchStartDate').val();
            var searchEndDate = $('#searchEndDate').val();
            var meterNo = $("input:hidden[name=meterNo]").val();

            var channel="";//"1,0";
            
            var searchType = $('#searchDateType').val();
            var channelArray = new Array();  

            for (var i = 0; i < $("input:checkbox[name=channelCode]").length; i++) {
                if ($("input:checkbox[name=channelCode]")[i].checked == true) {
                    obj = new Object();
                    obj.code = $("input:checkbox[name=channelCode]")[i].value;
                    obj.name = $("input:hidden[name=channelName]")[i].value;
                    channelArray.push(obj);
                    
                    if(channel.length > 0){
                        channel = channel + "," + $("input:checkbox[name=channelCode]")[i].value;
                    }
                    else {
                        channel = $("input:checkbox[name=channelCode]")[i].value;
                    }
                }
            }

            $.getJSON("${ctx}/gadget/mvm/getWeeklyComparisonChartData.do"
                    ,{searchStartDate:searchStartDate,
                        searchEndDate:searchEndDate,
                        meterNo:meterNo,
                        channel:channel,
                        type:$("#mvmMiniType").val(),
                        searchType:searchType,
                        supplierId:supplierId}
                    ,function(json) {
                        fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSLine.swf", "myChartId", $('#fcChartDiv').width(), "210", "0", "0");
                        fcChart.setJSONData(json.fcJson);
                        fcChartDataJson = json.fcJson;
                        fcChart.setTransparent("transparent");
                        fcChart.render("fcChartDiv");
                    });

        }

        function updateFChart() {
            if(detailChartType == 'week'){
                // WeeklyComparisonChart 는 별도 로직으로 분기한다.
                updateWeeklyFChart();
                return;
            }

            var searchStartHour;
            var searchEndHour;
            var meterNo;
            //var channel="0";
            var channel = "";
            var channelArray = new Array();     // 체크된 channel id
            var obj = new Object();
            var searchType = $('#searchDateType').val();

            if (DateTabOther.INTERVAL == searchType) {
                searchStartHour = $('#searchStartHour').val();
                searchEndHour = $('#searchEndHour').val();
            } else {
                searchStartHour = "";
                searchEndHour = "";
            }

            meterNo = $("input:hidden[name=meterNo]").val();            

            for (var i = 0; i < $("input:checkbox[name=channelCode]").length; i++) {
                if ($("input:checkbox[name=channelCode]")[i].checked == true)  {
                    obj = new Object();
                    obj.code = $("input:checkbox[name=channelCode]")[i].value;
                    obj.name = $("input:hidden[name=channelName]")[i].value;
                    channelArray.push(obj);

                    channel += $("input:checkbox[name=channelCode]")[i].value + ",";
                }
            }

            // 마지막 "," 제거
            if(0 < channel.length){
                channel = channel.substr(0, channel.length - 1);
            }

            // 채널 선택이 모두 해제 되었을때. (아무 채널도 선택하지 않았을 경우)
            if(channelArray.length == 0){
                obj = new Object();
                obj.code = -1;
                obj.name = "";
                channelArray.push(obj);

                channel = -1;
            }

            chartType = $("input:radio[name=chart_type]:checked").val();

            var checkedChannelCount = channelArray.length;
            reportChannel = channel;

            $.getJSON("${ctx}/gadget/mvm/getMvmDetailMeteringDataChart.do"
                    ,{searchType : searchType,
                      searchStartDate : $('#searchStartDate').val(),
                      searchEndDate : $('#searchEndDate').val(),
                      searchStartHour : searchStartHour,
                      searchEndHour : searchEndHour,
                      meterNo : meterNo,
                      channel : channel,
                      type : $("#mvmMiniType").val(),
                      supplierId : supplierId}
                    ,function(json) {
                        /* if (DateTabOther.RATE == searchType) {
                            updateFChartRate(json);
                            return;
                        } */
                        var searchAddData = json.searchAddData;
                        var searchData = json.searchData;
                        var dataCount = 0;

                        dataCount = searchData.length / (checkedChannelCount);                            

                        //var labelStep = (dataCount <= 5) ? 1 : dataCount/5;
                        var labelStep = 1;
                        var labelDisplay = null;

                        if (DateTabOther.INTERVAL == searchType) {
                            labelStep = (dataCount <= 16) ? 1 : Math.ceil(dataCount/16);
                            labelDisplay = "ROTATE";
                        } else if(DateType.HOURLY == searchType) {
                            labelStep = (dataCount <= 12) ? 1 : Math.ceil(dataCount/12);
                            labelDisplay = "NONE";
                        } else if(DateType.DAILY == searchType) {
                            labelStep = (dataCount <= 23) ? 1 : Math.ceil(dataCount/23);
                            labelDisplay = "ROTATE";
                        } else if(DateType.WEEKLY == searchType) {
                            labelStep = (dataCount <= 8) ? 1 : Math.ceil(dataCount/8);
                            labelDisplay = "ROTATE";
                        } else if(DateType.MONTHLY == searchType) {
                            labelStep = 1;
                            labelDisplay = "NONE";
                        } else if(DateType.WEEKDAILY == searchType) {
                            labelStep = 1;
                            labelDisplay = "ROTATE";
                        } else if(DateType.SEASONAL == searchType) {
                            labelStep = (dataCount <= 12) ? 1 : Math.ceil(dataCount/12);
                            labelDisplay = "ROTATE";
                        }

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
                               + "labelDisplay='" + labelDisplay + "' "
                               + "labelStep='" + labelStep + "' "
                               + "decimals='3' "
                               + "forceDecimals='1' "
                               + "toolTipSepChar='{br}' "
                               + fChartStyle_Common
                               + fChartStyle_Font;
                        if (detailChartType == "column") {
                            fcChartDataXml += fChartStyle_MSColumn3D_nobg;
                        } else if(detailChartType == "bar") {
                            fcChartDataXml += fChartStyle_Column2D_nobg;
                        } else if(detailChartType == "line") {
                            fcChartDataXml += fChartStyle_MSLine_nobg;
                        } else if(detailChartType == "plot") {
                            fcChartDataXml += fChartStyle_MSCol3DLine_nobg;
                        } else {
                            fcChartDataXml += fChartStyle_MSCol3DLine_nobg;
                        }
                        fcChartDataXml += ">";

                        var categories = "<categories>";
                        var trendlines = "<trendLines>";

                        var seriesSize = checkedChannelCount;                        
                        var datasets = new Array(seriesSize);

                        for (var i = 0; i < checkedChannelCount; i++) {
                            datasets[i] = "<dataset seriesName='"+ channelArray[i].name +"'>";
                        }

                        var categoryMap = new JqMap();

                        if(0 < searchData.length){ // 미터링 데이터 리스트 있는지 체크

                            for (var index = 0; index < searchData.length; index++) {
                                /* Category 만들기 */
                                categoryMap.put(searchData[index].localeDate, "<category label='"+searchData[index].localeDate +"' x='"+searchData[index].localeDate +"'/>")

                                /* Dataset 만들기 */
                                for (var i = 0; i < channelArray.length; i++) {
                                    if (searchData[index].channel == channelArray[i].code) {
                                        datasets[i] += "<set value='"+searchData[index].value+"' x='"+searchData[index].localeDate+"' y='"+searchData[index].value+"'/>";                                        
                                    }
                                }
                            }

                            for(var c=0; c<categoryMap.size(); c++){
                                categories += categoryMap.values()[c];
                            }


                            /*  TrendLines 만들기 */
                            if(0 < searchAddData.length){  // channel=1 번 데이터가 없으면 searchAddData 가 없어서 Trendlines 을 그리지 않는다.
                                if (searchAddData.length > 1) {
                                    trendlines += "<line startValue='"+searchAddData[1].maxValue+"' displayValue='<fmt:message key="aimir.max"/>{br}{br}' color='"+fChartColor_Step2[1]+"' valueOnRight='1' showOnTop='0' toolText='<fmt:message key="aimir.maximum"/>{br}"+searchAddData[1].maxDecimalValue+"' />";
                                    trendlines += "<line startValue='"+searchAddData[1].avgValue+"' displayValue='{br}{br}<fmt:message key="aimir.avg"/>' color='"+fChartColor_Step2[0]+"' valueOnRight='1' showOnTop='0' toolText='<fmt:message key="aimir.average"/>{br}"+searchAddData[1].avgDecimalValue+"' dashed='1' />";
                                } else {
                                    trendlines += "<line startValue='"+searchAddData[0].maxValue+"' displayValue='<fmt:message key="aimir.max"/>{br}{br}' color='"+fChartColor_Step2[1]+"' valueOnRight='1' showOnTop='0' toolText='<fmt:message key="aimir.maximum"/>{br}"+searchAddData[0].maxDecimalValue+"' />";
                                    trendlines += "<line startValue='"+searchAddData[0].avgValue+"' displayValue='{br}{br}<fmt:message key="aimir.avg"/>' color='"+fChartColor_Step2[0]+"' valueOnRight='1' showOnTop='0' toolText='<fmt:message key="aimir.average"/>{br}"+searchAddData[0].avgDecimalValue+"' dashed='1' />";
                                }
                            }

                        }else {
                            categories += "<category label='' />";
                            for (var i = 0; i < seriesSize; i++ ) {
                                datasets[i] += "<set value='' />";
                            }
                        }

                        categories += "</categories>";
                        trendlines += "</trendLines>";
                        fcChartDataXml += categories;

                        for (var i = 0; i < seriesSize; i++ ) {
                            fcChartDataXml += datasets[i] + "</dataset>";
                        }

                        fcChartDataXml += trendlines;
                        fcChartDataXml += "</chart>";
                        
                        fcChartRender();
                        hide();
                    }
            );
        }

        function fcChartRender() {
            if(detailChartType == "column") {
                fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "myChartId", $('#fcChartDiv').width(), "250", "0", "0");
            } else if(detailChartType == "bar") {
                fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSBar2D.swf", "myChartId", $('#fcChartDiv').width(), "250", "0", "0");
            } else if(detailChartType == "line") {
                fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSLine.swf", "myChartId", $('#fcChartDiv').width(), "250", "0", "0");
            } else if(detailChartType == "plot") {
                fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Scatter.swf", "myChartId", $('#fcChartDiv').width(), "250", "0", "0");
            } else if(detailChartType == "week") {
                fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSLine.swf", "myChartId", $('#fcChartDiv').width(), "250", "0", "0");
                fcChart.setJSONData(fcChartDataJson);
                fcChart.setTransparent("transparent");
                fcChart.render("fcChartDiv");
                return;
            } else {
                fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSLine.swf", "myChartId", $('#fcChartDiv').width(), "250", "0", "0");
            }
            fcChart.setDataXML(fcChartDataXml);
            fcChart.setTransparent("transparent");
            fcChart.render("fcChartDiv");
        }

        // Rate Tab 에서 Chart 그리기
        /* function updateFChartRate(json) {
            var searchData = json.searchData;
            var searchTotalData = json.searchTotalData;
            var labelStep = 1;

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
                   + "labelDisplay='ROTATE' "
                   + "labelStep='" + labelStep + "' "
                   + "decimals='3' "
                   + "forceDecimals='1' "
                   + "toolTipSepChar='{br}' "
                   + fChartStyle_Common
                   + fChartStyle_Font;
            if(detailChartType == "column") {
                fcChartDataXml += fChartStyle_MSColumn3D_nobg;
            } else if(detailChartType == "bar") {
                fcChartDataXml += fChartStyle_Column2D_nobg;
            } else if(detailChartType == "line") {
                fcChartDataXml += fChartStyle_MSLine_nobg;
            } else if(detailChartType == "plot") {
                fcChartDataXml += fChartStyle_MSCol3DLine_nobg;
            } else {
                fcChartDataXml += fChartStyle_MSCol3DLine_nobg;
            }
            fcChartDataXml += ">";

            var categories = "<categories>";
            var seriesSize = 3;       // Rate1/Rate2/Rate3

            var rateTotal1 = (searchTotalData == null || searchTotalData.total0 == null) ? "0" : searchTotalData.total0;
            var rateTotal2 = (searchTotalData == null || searchTotalData.total1 == null) ? "0" : searchTotalData.total1;
            var rateTotal3 = (searchTotalData == null || searchTotalData.total2 == null) ? "0" : searchTotalData.total2;

            var unit = "<fmt:message key='aimir.unit.kwh'/>";
            
            if($("#mvmMiniType").val() == "EM") {
                unit = "<fmt:message key='aimir.unit.kwh'/>";
            } else if ($("#mvmMiniType").val() == "GM") {
                unit = "<fmt:message key='aimir.unit.m3'/>";
            } else if ($("#mvmMiniType").val() == "WM") {
                unit = "<fmt:message key='aimir.unit.m3'/>";
            }
            
            var datasets = new Array(seriesSize);
            datasets[0] = "<dataset seriesName='<fmt:message key='aimir.customer.usage.rate1'/> = " + rateTotal1 + " " + unit + " '>";
            datasets[1] = "<dataset seriesName='<fmt:message key='aimir.customer.usage.rate2'/> = " + rateTotal2 + " " + unit + " '>";
            datasets[2] = "<dataset seriesName='<fmt:message key='aimir.customer.usage.rate3'/> = " + rateTotal3 + " " + unit + " '>";

            var tmpLabel = "";

            if (searchData.length > 0) {
                for (var index = 0; index < searchData.length ; index++) {
                    if (searchData[index].localeDate != null) {
                        if (searchData[index].localeDate != tmpLabel) {
                            categories += "<category label='"+searchData[index].localeDate +"' x='"+searchData[index].localeDate +"'/>";
                            tmpLabel = searchData[index].localeDate;
                        }

                        datasets[searchData[index].rateIndex-1] += "<set value='"+searchData[index].value+"' x='"+tmpLabel+"' y='"+searchData[index].value+"'/>";
                    }
                }
            } else {
                categories += "<category label='' />";

                for (var i=0 ; i < seriesSize ; i++ ) {
                    datasets[i] += "<set value='' />";
                }
            }

            categories += "</categories>";
            fcChartDataXml += categories;

            for (var i=0 ; i < seriesSize ; i++ ) {
                fcChartDataXml += datasets[i] + "</dataset>";
            }

            fcChartDataXml += "</chart>";
            
            fcChartRender();
            hide();
        } */

        // birt를 이용한 보고서 화면 출력
        var win;
        function showReport(format) {

            var selectedIndex = $("#datetab").tabs("option", "selected");
            var report;
            /* if(selectedIndex == 0) report = "meteringDataMgmtRate.rptdesign";
            else */ report = "meteringDataManagement.rptdesign";
            var opts="width=1010px, height=650px, left=150px, resizable=no, status=no";
            var params = "&supplierId=${supplierId}";
            params += "&meterNo=${customerInfo.meterNo}";
            params += "&mvmMiniType=${mvmMiniType}";            
            params += "&startDate=" + $('#searchStartDate').val();
            params += "&endDate=" + $('#searchEndDate').val();
            
            if ($("#searchDateType").val() == DateTabOther.INTERVAL) {
                params += "&startHour=" + ($('#searchStartHour').val() != "" ? $('#searchStartHour').val() : "00"); 
                params += "&endHour=" + ($('#searchEndHour').val() != "" ? $('#searchEndHour').val() : "23");
            } else {
                params += "&startHour=" + "00"; 
                params += "&endHour=" + "23";
            }
            params += "&periodType=" + getSearchType();
            params += "&channel=" + reportChannel;

            if (win)
                win.close();
            if (format =="html") {
                //window.showModalDialog("${ctx}/report/reportPopup.jsp", "/birt-viewer/run?__report=" + report + params,opts); 
                var url = "/birt-viewer/output?__report=" + report + params + "&localPort=<%= request.getLocalPort() %>";
                win = window.open(url, "", opts);
            } else {
                var url = "/birt-viewer/frameset?__report=" + report + params + "&localPort=<%= request.getLocalPort() %>";
                win = window.open(url, "mvmDetailPopup",opts);
            }
        }
               
        function getSearchType() {
            var periodType = DateType.HOURLY;           
            var selectedIndex = $("#datetab").tabs("option", "selected");
           /*  if (selectedIndex == 0)      periodType = DateTabOther.RATE;
            else */ if (selectedIndex == 0) periodType = DateTabOther.INTERVAL;
            else if (selectedIndex == 1) periodType = DateType.HOURLY;
            else if (selectedIndex == 2) periodType = DateType.DAILY;
            else if (selectedIndex == 3) periodType = DateType.WEEKLY;
            else if (selectedIndex == 4) periodType = DateType.MONTHLY;
            else if (selectedIndex == 5) periodType = DateType.WEEKDAILY;
            else if (selectedIndex == 6) periodType = DateType.SEASONAL;
            
            return periodType;
        }

        function cmdOnDemandRecollect() {
            $.getJSON('${ctx}/gadget/device/command/cmdOnDemand.do',
                    {fromDate: $("#searchStartDate").val(),
                     toDate: $("#searchEndDate").val(),
                     target: '${meterId}',
                     loginId: loginId});
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.remetering"/>');
        }

        // 체크된 채널리스트 생성
        function getCheckedChannelData() {
            var channelArray = new Array();
            for (var i = 0; i < $("input:checkbox[name=channelCode]").length; i++) {
                if ($("input:checkbox[name=channelCode]")[i].checked == true) {
                    var obj = new Object();
                    obj.code = $("input:checkbox[name=channelCode]")[i].value;
                    obj.name = $("input:hidden[name=channelName]")[i].value;
                    channelArray.push(obj);
                }
            }
            return channelArray;
        }
		
        var checkedChannelArr = new Array();    // checked channel
        function getDetailDataList() {
            var searchDateType = $('#searchDateType').val();
            var searchStartHour = null;
            var searchEndHour = null;

            if (searchDateType == DateTabOther.INTERVAL) {
                searchStartHour = $("#searchStartHour").val();
                searchEndHour = $("#searchEndHour").val();
            } else {
                searchStartHour = "";
                searchEndHour = "";
            }

            checkedChannelArr = getCheckedChannelData();

            var len = checkedChannelArr.length;
            var checkedChannel = new Array();
            if(len == 0){
                checkedChannel.push(-1);
            }else {
                for (var i = 0; i < len; i++) {
                    checkedChannel.push(checkedChannelArr[i].code);
                }
            }

            var params = {
                    searchDateType : searchDateType,
                    searchStartDate : $('#searchStartDate').val(),
                    searchEndDate : $('#searchEndDate').val(),
                    searchStartHour : searchStartHour,
                    searchEndHour : searchEndHour,
                    chartType : $("input:radio[name=chart_type]:checked").val(),
                    mvmMiniType : $("#mvmMiniType").val(),
                    meterNo : $("#meterNo").val(),
                    channel : checkedChannel.join(","),
                    msgAvg : "<fmt:message key="aimir.avg"/>",
                    msgMax : "<fmt:message key="aimir.max"/>",
                    msgMin : "<fmt:message key="aimir.min.upper"/>",
                    msgSum : "<fmt:message key="aimir.sum"/>",
                    viewAll : ($("#viewAll").is(":checked")) ? "yes" : "no",
                    supplierId : supplierId
            };

            $.post("${ctx}/gadget/mvm/getMvmDetailMeteringData.do",
                    params,
                    function(json) {
                        var result = json.result;
                        if (result == null) {
                            result = new Array();
                        }

                        /* if(searchDateType == DateTabOther.RATE) {
                            detailRateData = result;
                            makeDetailRatelyDataList();
                        } else */ if(searchDateType == DateTabOther.INTERVAL || searchDateType == DateType.HOURLY) {
                            detailHourData = result;
                            makeDetailHourlyDataList();
                        } else {
                            detailData = result;
                            makeDetailDataList();
                        }
                    }
            );
        }

        //var detailRateData = [];
        var detailData = [];
        var detailHourData = [];

        /* Metering Data Detail Hourly Data 리스트 START */
        var detailHourlyDataGridOn = false;
        var detailHourlyDataGrid;
        var detailHourlyDataRootNode;

        function makeDetailHourlyDataList() {
            var width = $("#detailAllGridDiv").width();
            var len = checkedChannelArr.length;
            var checkedChannel = new Array();

            if(len == 0){
                checkedChannel.push(-1);
            }else {
                for (var i = 0; i < len; i++) {
                    checkedChannel.push(checkedChannelArr[i].code);
                }
            }

            var columns = [];
            var meteringTime = "<fmt:message key='aimir.meteringtime'/>";
            
            var columnWidth = (width/(len+1)) - (20/(len+1));
            
            columns.push({header: meteringTime, dataIndex: 'meteringTime', align: 'left', menuDisabled: true, width: columnWidth, tooltip: meteringTime});

            for (var i = 0 ; i < len ; i++) {
                columns.push({header: checkedChannelArr[i].name, dataIndex: "channel_" + checkedChannelArr[i].code, align: 'right', menuDisabled: true, width: columnWidth, tooltip: checkedChannelArr[i].name});
            }
            
            var detailHourlyDataColModel = columns;

            detailHourlyDataRootNode = new Ext.tree.AsyncTreeNode({
                text: 'root',
                id: 'root',
                draggable:false,
                expended:true,
                children: detailHourData
            });

            if (!detailHourlyDataGridOn) {
                var treeLoader = new Ext.tree.TreeLoader({
                    url:'${ctx}/gadget/mvm/getMvmDetailMeteringDataLpData.do',
                    baseParams: {
                        mvmMiniType : $("#mvmMiniType").val(),
                        meterNo : $("#meterNo").val(),
                        channel : checkedChannel.join(","),
                        supplierId : supplierId
                    }
                });

                detailHourlyDataGrid = new Ext.ux.tree.TreeGrid({
                    width: width,
                    height: 517,
                    renderTo: "detailAllGridDiv",
                    enableDD: false,
                    enableHdMenu : false,
                    enableSort : false,
                    autoScroll : false,
                    columns: detailHourlyDataColModel,
                    loader: treeLoader,
                    root: detailHourlyDataRootNode,
                    rootVisible: false//,
                });

                detailHourlyDataGridOn = true;
                // TreeGrid header width 관련 오류 수정 함수.
                startTreeGridDivRefresh();
            } else {
                detailHourlyDataGrid.setWidth(width);
                detailHourlyDataGrid.setRootNode(detailHourlyDataRootNode);
                detailHourlyDataGrid.render();
            }
            treeCmpl = 1;
        }

        var treeCmpl = 0;
        var interval_id;
        var interval_cnt = 0;
        // TreeGrid Header 의 width 가 비정상적으로 보이는 문제 수정
        // TreeGrid div 를 한번 숨겼다가 다시 보여주면 정상적으로 나옴 
        function startTreeGridDivRefresh() {
            $("#detailAllGridDiv").hide();
            interval_id = window.setInterval(function(){treeGridDivRefresh();}, 100);
        }
        // TreeGrid div 를 다시 보여주는 function 
        function treeGridDivRefresh() {
            interval_cnt++;
            if (treeCmpl == 1) {
                $("#detailAllGridDiv").show();
                window.clearInterval(interval_id);
            } else {
                // 3초 이상 지연되면 자동해제
                if (interval_cnt >= 30) {
                    $("#detailAllGridDiv").show();
                    window.clearInterval(interval_id);
                }
            }
        }

        /* Metering Data Detail 리스트 START */
        var detailDataGridOn = false;
        var detailDataGrid;
        var detailDataColModel;
        var makeDetailDataList = function() {
            var width = $("#detailGridDiv").width();
            var fields = [];
            var len = checkedChannelArr.length;

            fields.push("meteringTime");

            for (var i = 0 ; i < len ; i++) {
                fields.push("channel_" + checkedChannelArr[i].code);
            }

            var detailDataStore = new Ext.data.JsonStore({
                autoLoad : true,
                data : detailData,
                fields : fields
            });

            var columns = [];
            var meteringTime = "<fmt:message key='aimir.meteringtime'/>";
            columns.push({header: meteringTime, dataIndex: 'meteringTime', align: 'left', menuDisabled: true, width: (width/(len+1)), tooltip: meteringTime});

            for (var i = 0 ; i < len ; i++) {
                if (i == (len-1)) {
                    columns.push({header: checkedChannelArr[i].name, dataIndex: "channel_" + checkedChannelArr[i].code, align: 'right', menuDisabled: true, width: (width/(len+1))-6, tooltip: checkedChannelArr[i].name});
                } else {
                    columns.push({header: checkedChannelArr[i].name, dataIndex: "channel_" + checkedChannelArr[i].code, align: 'right', menuDisabled: true, width: (width/(len+1)), tooltip: checkedChannelArr[i].name});
                }
            }

            detailDataColModel = new Ext.grid.ColumnModel(columns);

            if(detailDataGridOn == false) {
                detailDataGrid = new Ext.grid.GridPanel({
                    store: detailDataStore,
                    colModel : detailDataColModel,
                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll:false,
                    width: width,
                    height: 530,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'detailGridDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display',
                        forceFit:true
                    }
                });
                detailDataGridOn = true;
            } else {
                detailDataGrid.setWidth(width);
                detailDataGrid.reconfigure(detailDataStore, detailDataColModel);
            }

            hide();
        };

        /* Metering Data Detail Rate 리스트 START */
       /*  var detailRatelyDataGridOn = false;
        var detailRatelyDataGrid;
        var detailRatelyDataColModel;
        var makeDetailRatelyDataList = function() {
            var width = $("#detailRateGridDiv").width();
            var detailRatelyDataStore = new Ext.data.JsonStore({
                autoLoad : true,
                data : detailRateData,
                fields: ["meteringTime", "rate_1", "rate_2", "rate_3"],
            });

            var meteringTime = "<fmt:message key='aimir.meteringtime'/>";
            var rate1 = "<fmt:message key='aimir.customer.usage.rate1'/>";
            var rate2 = "<fmt:message key='aimir.customer.usage.rate2'/>";
            var rate3 = "<fmt:message key='aimir.customer.usage.rate3'/>";

            detailRatelyDataColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: "<font style='font-weight: bold;'>" + meteringTime + "</font>", dataIndex: 'meteringTime', width: width/4, tooltip: meteringTime}
                   ,{header: "<font style='font-weight: bold;'>" + rate1 + "</font>", dataIndex: 'rate_1', align: 'right', width: width/4, tooltip: rate1}
                   ,{header: "<font style='font-weight: bold;'>" + rate2 + "</font>", dataIndex: 'rate_2', align: 'right', width: width/4, tooltip: rate2}
                   ,{header: "<font style='font-weight: bold;'>" + rate3 + "</font>", dataIndex: 'rate_3', align: 'right', width: (width/4)-6, tooltip: rate3}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
               }
            });

            if(detailRatelyDataGridOn == false) {
                detailRatelyDataGrid = new Ext.grid.GridPanel({
                    store: detailRatelyDataStore,
                    colModel : detailRatelyDataColModel,
                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll:false,
                    width: width,
                    height: 530,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'detailRateGridDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display',
                        forceFit:true
                    }
                });
                detailRatelyDataGridOn = true;
            } else {
                detailRatelyDataGrid.setWidth(width);
                detailRatelyDataGrid.reconfigure(detailRatelyDataStore, detailRatelyDataColModel);
            }

            hide();
        };
 */
        // window resize event
        $(window).resize(function() {
            fcChartRender()
            /* if (!$("#detailRateGridDiv").is(":hidden")) {
                makeDetailRatelyDataList();
            } else  */if (!$("#detailGridDiv").is(":hidden")) {
                makeDetailDataList();
            } else if (!$("#detailAllGridDiv").is(":hidden")) {
                makeDetailHourlyDataList();
            }
        });
        /*]]>*/
    </script>
</head>
<body>
<div class="mvm-popwin-body" style="width:860px">
<input type="hidden" id="meterNo" name="meterNo" value="${customerInfo.meterNo}">
<input type="hidden" id="mvmMiniType" name="mvmMiniType" value="${mvmMiniType}">
<input type="hidden" id="contractId" name="contractId" value="${contractId}">
<div style="float:right;" id ="btn" class ="btn_right_top2"><a href="javascript:showReport('other');" class="btn_blue"><span><fmt:message key="aimir.report.others"/></span></a></div>
<div style="float:right;" id ="btn" class ="btn_right_top2"><a href="javascript:showReport('html');" class="btn_blue"><span><fmt:message key="aimir.report.html"/></span></a></div>
 <br></br>
<div class="mvm-custinfo radius6">
    <table class="wfree table_detail" style="width:99%;">
        <tr>
            <th style="width:13% !important;"><fmt:message key="aimir.customerview"/></th>
            <td style="width:20% !important;"><input type="text" value="${customerInfo.customerName}" class="border-trans bg-trans" style="width:100%;"></td>
            <th style="width:14% !important;"><fmt:message key="aimir.contractNumber"/></th>
            <td style="width:19% !important;"><input type="text" value="${customerInfo.contractNo}" class="border-trans bg-trans" style="width:100%;"></td>
            <th style="width:13% !important;"><fmt:message key="aimir.contract.tariff.type"/></th>
            <td style="width:20% !important;"><input type="text" value="${customerInfo.tariffType}" class="border-trans bg-trans" style="width:100%;"></td>
        </tr>
        <tr>
            <th style="width:13% !important;"><fmt:message key="aimir.address"/></th>
            <td colspan="3"><input type="text" readonly value="${customerInfo.adress}" style="width:100%;" class="border-trans bg-trans"></td>
            <th style="width:13% !important;"><fmt:message key="aimir.location"/></th>
            <td style="width:20% !important;"><input type="text" value="${customerInfo.location}" class="border-trans bg-trans" style="width:100%;"></td>
        </tr>
        <tr>
            <th style="width:13% !important;"><fmt:message key="aimir.telephoneno"/></th>
            <td style="width:20% !important;"><input type="text" readonly value="${customerInfo.telephoneNo}" class="border-trans bg-trans" style="width:100%;"></td>
            <td style="width:14%; !important" class="blue11pt withinput"><fmt:message key="aimir.cpno"/></td>
            <td colspan="3"><input type="text" readonly value="${customerInfo.mobileNo}" class="border-trans bg-trans" style="width:100%;"></td>
        </tr>
        <tr>
            <th style="width:13% !important;"><fmt:message key="aimir.metertype"/></th>
            <td style="width:20% !important;"><input type="text" readonly value="${customerInfo.meterType}" class="border-trans bg-trans" style="width:100%;"></td>
            <th style="width:14% !important;"><fmt:message key="aimir.meterid"/></th>
            <td style="width:19% !important;"><input type="text" readonly value="${customerInfo.meterNo}" class="border-trans bg-trans" style="width:100%;"></td>
            <th style="width:13% !important;"><fmt:message key="aimir.mcuid2"/></th>
            <td style="width:20% !important;"><input type="text" readonly value="${customerInfo.mcuNo}" class="border-trans bg-trans" style="width:100%;"></td>
        </tr>
        <tr>
            <th style="width:13%;"><fmt:message key="aimir.meteringtime"/></th>
                <td style="width:20%;"><input type="text" readonly value="${customerInfo.lastTime}" class="border-trans bg-trans" style="width:100%;"></td>
                <c:if test="${customerInfo.meterType == 'EnergyMeter'}">
                    <th style="width:14%;"><fmt:message key="aimir.meteringdata"/>[<fmt:message key="aimir.unit.kwh"/>]</th>
                </c:if>
                <c:if test="${customerInfo.meterType == 'GasMeter'}">
                    <th style="width:14%;"><fmt:message key="aimir.meteringdata"/>[<fmt:message key="aimir.unit.m3"/>]</th>
                </c:if>
                <c:if test="${customerInfo.meterType == 'WaterMeter'}">
                    <th style="width:14%;"><fmt:message key="aimir.meteringdata"/>[<fmt:message key="aimir.unit.m3"/>]</th>
                </c:if>
            <td colspan="3"><input type="text" readonly value="${customerInfo.lastMeteringData}" class="border-trans bg-trans" style="width:100%;"></td>
        </tr>
    </table>
</div>

<!-- search-background DIV (S) -->
<div id="conditionDiv" class="search-bg-withtabs" style="height:98px;">

    <div class="dayoptions" style="border-top:1px solid #d3e6f5;">
    <%@ include file="../commonDateTabDetailView2.jsp" %>
    </div>
    <div class="dashedline"></div>


    <div class="mvm-multiselect border-blue" id="channelList" style="position: absolute; z-index: 1; width: 195px;">
        <table class="wfree" onclick="controlCombo(1);" onmouseover="endCloseCombo(1);" onmouseout="startCloseCombo(1);" style="width:100%">
            <tr><td class="space10"></td>
                <td class="graybold11pt withinput"><fmt:message key="aimir.channelid"/></td>
            </tr>
        </table>

        <div id="multiCombo1" class="line" style="display:none;" onmouseover="endCloseCombo(1);" onmouseout="startCloseCombo(1);">
            <table class="wfree">
                <c:forEach items="${channelList}" var="channel" varStatus="idx">
                <tr>
                    <td><input class="checkbox_space2" id="channelCode" name="channelCode" type="checkbox" checked="checked" value="${channel.codeId}" ></td>
                    <td class="gray11pt withinput">${channel.codeName}
                        <input id="channelName${idx.count-1}" name="channelName"  type="hidden" value="${channel.codeName}"></input>
                        <input id="channel${idx.count-1}" name="channel"  type="hidden" value="${channel.codeId}"></input></td>
                </tr>
                </c:forEach>
            </table>
        </div>
    </div>


    <div class="mvm-multiselect border-blue" style="left:220px; position: absolute; z-index: 1;" id="chartType">
        <table class="wfree" onclick="controlCombo(3);" onmouseover="endCloseCombo(3);" onmouseout="startCloseCombo(3);" style="width:100%">
            <tr><td class="space10"></td>
                <td class="graybold11pt withinput"><fmt:message key="aimir.chartType"/></td></tr>
        </table>

        <div id="multiCombo3" class="line" style="display:none;" onmouseover="endCloseCombo(3);" onmouseout="startCloseCombo(3);">
            <table class="wfree">
                <tr><td><input name="chart_type" class="radio_space2" type="radio" value="column" onclick="chanageChartControl()"></td>
                    <td class="gray11pt withinput">ColumnChart</td></tr>
                <tr><td><input name="chart_type" class="radio_space2" type="radio" value="bar" onclick="chanageChartControl()"></td>
                    <td class="gray11pt withinput">BarChart</td></tr>
                <tr><td><input name="chart_type" id="defaultChart" class="radio_space2" type="radio" value="line" checked="checked" onclick="chanageChartControl()"></td>
                    <td class="gray11pt withinput">LineChart</td></tr>
                <tr id="weeklyComChart" style="display:none;"><td><input name="chart_type" class="radio_space2" type="radio" value="week" onclick="chanageChartControl()"></td>
                    <td class="gray11pt withinput">WeeklyComparisonChart</td></tr>
                <!--
                <tr><td><input name="chart_type" class="trans" type="radio" value="plot" onclick="chanageChartControl()"></td>
                    <td class="gray11pt withinput">PlotChart</td></tr>
                 -->
            </table>
        </div>
    </div>

    <div id="viewAllDiv" style="position:absolute; width:180px;left:455px;">
        <table>
            <tr>
                <td><input type="checkbox" id="viewAll" name="viewAll" checked></input>
                </td>
                <td class="gray11pt withinput"><fmt:message key="aimir.mvm.viewLoadProfileAll"/>
                </td>
            </tr>
        </table>
    </div>

    <!-- <div id="btn-right" class="margin-t5px">
        <ul><li><a href="javascript:showCalendarChart(${endYear}, ${endMonth})" class="on"><fmt:message key="aimir.calendarchart"/></a></li></ul>
    </div> -->

</div>
<!--상세검색 끝-->


<div id="chartGridDiv" class="gadget_body">
    <div id="fcChartDiv">
        The chart will appear within this DIV. This text will be replaced by the chart.
    </div>
    <!-- <div id="detailRateGridDiv" style="display:none;"></div> -->
    <div id="detailGridDiv" style="display:none;"></div>
    <div id="detailAllGridDiv" style="display:none;"></div>
</div>

<div id="ondemandDiv" class="margin-t10px">
    <em class="btn_org"><a href="javascript:cmdOnDemandRecollect();" id="bt_search" class="on"><fmt:message key="aimir.onDemand.Metering"/></a></em>
</div>

</div>

</body>
</html>