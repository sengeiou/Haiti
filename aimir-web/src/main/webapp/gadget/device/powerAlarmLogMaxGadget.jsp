<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<%@ include file="/gadget/system/preLoading.jsp"%>
<title>PowerAlarmLog MaxGadget</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css"/>
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />

<script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
<%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        /* TABLE{border-collapse: collapse; width:auto;} */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold;
        }
        .remove {
            background-image:url(../../images/allOff.gif) !important;
        }

        .accept {
            background-image:url(../../images/allOn.png) !important;
        }
        .task-master {
            background-image:url(${ctx}/js/extjs/resources/images/default/tree/user.png) !important;
        }
    </style>

<script type="text/javascript" charset="utf-8" >/*<![CDATA[*/
    var currTabId = "PC";

    var tabs = {hourly:0,daily:1,period:0,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};
    var tabNames = {};
    var cnt = 0;

    var fcColumnChart;
    var fcColumnChartDataXml;
    var fcPieChart;
    var fcPieChartDataXml;

    var supplierId = "${supplierId}";
    var durationDays = "<fmt:message key="aimir.hems.prepayment.days"/>";

    // Ext-JS Grid 컬럼사이즈 오류 수정
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

    Ext.onReady(function() {
        Ext.QuickTips.init();
        extColumnResize();

        updateFChart();
        makePowerAlarmGrid();
        hide();
    });

    $(function() {
        $(function() { $('#_powerCut').bind('click',function(event) { currTabId="PC"; searchPowerAlarmLog();  } ); });
        $(function() { $('#_lineMissing').bind('click',function(event) { currTabId="LM"; searchPowerAlarmLog(); } ); });

        $("#powerLogMenu").tabs();

        locationTreeGoGo('treeDiv1', 'searchLocationA', 'locationA');
        locationTreeGoGo('treeDiv2', 'searchLocationB', 'locationB');

        $("#typeA").selectbox();
        $("#typeB").selectbox();
        $("#lineMissingType").selectbox();

        $("#startDate").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#endDate").datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
    });

    var fmtMessage = new Array();

    fmtMessage[0] = "<fmt:message key="aimir.type2"/>";
    fmtMessage[1] = "<fmt:message key="aimir.open"/>";
    fmtMessage[2] = "<fmt:message key="aimir.close"/>";
    fmtMessage[3] = "<fmt:message key="aimir.day"/>";
    fmtMessage[4] = "<fmt:message key="aimir.hour"/>";
    fmtMessage[5] = "<fmt:message key="aimir.min"/>";
    fmtMessage[6] = "<fmt:message key="aimir.sec"/>";
    fmtMessage[7] = "<fmt:message key='aimir.excel.powerAlramLog'/>";

    var fmtMessageMaxChart = new Array();

    fmtMessageMaxChart[0] = "<fmt:message key="aimir.orderNo"/>";
    fmtMessageMaxChart[1] = "<fmt:message key="aimir.opentime"/>";
    fmtMessageMaxChart[2] = "<fmt:message key="aimir.closetime"/>";
    fmtMessageMaxChart[3] = "<fmt:message key="aimir.location.supplier"/>";
    fmtMessageMaxChart[4] = "<fmt:message key="aimir.phasetype2"/>";

    fmtMessageMaxChart[5] = "<fmt:message key="aimir.customername"/>";
    fmtMessageMaxChart[6] = "<fmt:message key="aimir.meterid"/>";
    fmtMessageMaxChart[7] = "<fmt:message key="aimir.duration"/>";
    fmtMessageMaxChart[8] = "<fmt:message key="aimir.close"/>";
    fmtMessageMaxChart[9] = "<fmt:message key="aimir.message"/>";

    function getFmtMessage() {
        return fmtMessage;
    }

    function getFmtMessageMaxChart() {
        return fmtMessageMaxChart;
    }

    function getParams() {
        var conditionArray = new Array();

        if (currTabId == 'PC') {     // 정전
            conditionArray[0] = currTabId;
            conditionArray[1] = $('#locationA').val();
            conditionArray[2] = $('#customerNameA').val();
            conditionArray[3] = $('#meterA').val();
            conditionArray[4] = $('#typeA').val();
            conditionArray[5] = '';
            conditionArray[6] = $('#searchStartDate').val();
            conditionArray[7] = $('#searchEndDate').val();
            conditionArray[8] = $('#searchStartHour').val();
            conditionArray[9] = $('#searchEndHour').val();
            conditionArray[10] = $('#searchDateType').val();
            conditionArray[11] = supplierId;
        } else if(currTabId == 'LM') {  // 결상
            conditionArray[0] = currTabId;
            conditionArray[1] = $('#locationB').val();
            conditionArray[2] = $('#customerNameB').val();
            conditionArray[3] = $('#meterB').val();
            conditionArray[4] = $('#typeB').val();
            conditionArray[5] = $('#lineMissingType').val();
            conditionArray[6] = $('#searchStartDate').val();
            conditionArray[7] = $('#searchEndDate').val();
            conditionArray[8] = $('#searchStartHour').val();
            conditionArray[9] = $('#searchEndHour').val();
            conditionArray[10] = $('#searchDateType').val();
            conditionArray[11] = supplierId;
        }
        conditionArray[12] = durationDays;

        return conditionArray;
    }

    function searchPowerAlarmLog() {
        if (validInputMeter()) {
            updateFChart();
            makePowerAlarmGrid();
        }
    }

    function makePowerAlarmGrid() {
        var conArray = getParams();

        if (currTabId == 'PC') {
            powerAlarmLogGrid({
                currTabId    : conArray[0],
                location     : conArray[1],
                customerName : conArray[2],
                meter        : conArray[3],
                type         : conArray[4],
                lineMissingType  : conArray[5],
                searchStartDate  : conArray[6],
                searchEndDate    : conArray[7],
                searchStartHour  : conArray[8],
                searchEndHour    : conArray[9],
                searchDateType   : conArray[10],
                supplierId       : conArray[11],
                durationDays     : conArray[12],
                searchType       : 'search',
                colFormat        : (searchDateType == "3" || searchDateType == "4")?fmtMessage[3]:fmtMessage[4]
            });
        } else if (currTabId == 'LM') {  // 결상
            powerAlarmLogGrid({
                currTabId    : conArray[0],
                location     : conArray[1],
                customerName : conArray[2],
                meter        : conArray[3],
                type         : conArray[4],
                lineMissingType : conArray[5],
                searchStartDate : conArray[6],
                searchEndDate   : conArray[7],
                searchStartHour : conArray[8],
                searchEndHour   : conArray[9],
                searchDateType  : conArray[10],
                supplierId      : conArray[11],
                durationDays    : conArray[12],
                searchType      : 'search',
                colFormat       : (searchDateType == "3" || searchDateType == "4")?fmtMessage[3]:fmtMessage[4]
            });
        }
    }

    var powerAlarmGridStore;

    function powerAlarmLogGrid(params) {
        var pageSize = 10;

        powerAlarmGridStore = new Ext.data.JsonStore({
            autoLoad : {params:{start: 0, limit: pageSize}},
            url : "${ctx}/gadget/device/getPowerAlarmLogMaxGrid.do",
            baseParams : {
                currTabId    : params.currTabId,
                location     : params.location,
                customerName : params.customerName,
                meter        : params.meter,
                type         : params.type,
                lineMissingType : params.lineMissingType,
                searchStartDate : params.searchStartDate,
                searchEndDate   : params.searchEndDate,
                searchStartHour : params.searchStartHour,
                searchEndHour   : params.searchEndHour,
                searchDateType  : params.searchDateType,
                supplierId      : params.supplierId,
                durationDays    : params.durationDays,
                searchType      : params.searchType,
                colFormat       : params.colFormat
            },
            root : 'gridData',
            totalProperty : 'totalCount',
            fields : [
                {name: 'id', type: 'string'},
                {name: 'openTime', type: 'string'},
                {name: 'closeTime', type: 'string'},
                {name: 'supplier', type: 'string'},
                {name: 'lineType', type: 'string'},
                {name: 'custName', type: 'string'},
                {name: 'meter', type: 'string'},
                {name: 'duration', type: 'string'},
                {name: 'status', type: 'string'},
                {name: 'message', type: 'string'}
            ],
            listeners : {
                beforeload : function(store, options) {
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                        page : Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                    });
                },
                load : function(store, record, options) {
                    makeGridPanel();
                }
            }
        });
    }

    var grid = undefined;
    var gridOn = false;
    function makeGridPanel() {
        var width = $("#powerAlarmLogGridDiv").width();
        var pageSize = 10;

        var colWidth = (width-70)/9;
        var colModel = new Ext.grid.ColumnModel({
            defaults : {
                width : colWidth,
                height : 280,
                sortable : true
            },
            columns : [{
                header: fmtMessageMaxChart[0],
                align: 'center',
                width: 70,
                dataIndex: "id",
                tooltip: fmtMessageMaxChart[0]
            }, {
                header:fmtMessageMaxChart[1],
                align: 'center',
                dataIndex: "openTime",
                tooltip: fmtMessageMaxChart[1]
            },{
                header: fmtMessageMaxChart[2],
                align: 'center',
                dataIndex : "closeTime",
                tooltip: fmtMessageMaxChart[2]
            }, {
                header:fmtMessageMaxChart[3],
                align: 'center',
                dataIndex: "supplier",
                tooltip: fmtMessageMaxChart[3]
            },{
                header: fmtMessageMaxChart[4],
                align: 'left',
                dataIndex: "lineType",
                tooltip: fmtMessageMaxChart[4]
            },{
                header: fmtMessageMaxChart[5],
                align: 'left',
                dataIndex: "custName",
                tooltip: fmtMessageMaxChart[5]
            },{
                header: fmtMessageMaxChart[6],
                align: 'center',
                dataIndex: "meter",
                tooltip: fmtMessageMaxChart[6]
            },{
                header: fmtMessageMaxChart[7],
                align: 'center',
                dataIndex: "duration",
                tooltip: fmtMessageMaxChart[7]
            },{
                header: fmtMessageMaxChart[8],
                align: 'center',
                dataIndex: "status",
                tooltip: fmtMessageMaxChart[8]
             },{
                header: fmtMessageMaxChart[9],
                align: 'center',
                dataIndex: "message",
                tooltip: fmtMessageMaxChart[9]
            }]
        });

        //페이징 툴바 셋팅
        var pagingToolbar = new Ext.PagingToolbar({
            store : powerAlarmGridStore,
            displayInfo : true,
            pageSize : pageSize,
            displayMsg: ' {0} - {1} / {2}'
        });

        //그리드 설정
        if (!gridOn) {
            grid = new Ext.grid.GridPanel({
                height : 280,
                renderTo : 'powerAlarmLogGridDiv',
                store : powerAlarmGridStore,
                colModel : colModel,
                width : width,
                style : 'align:center;font-wegiht:bold',
                bbar : pagingToolbar,
                viewConfig : {
                    showPreview : true,
                    emptyText: 'No data to display'
                },
            });
            gridOn = true;
        } else {
            grid.setWidth(width);
            grid.reconfigure(powerAlarmGridStore, colModel);
            var bottomToolbar = grid.getBottomToolbar();
            bottomToolbar.bindStore(powerAlarmGridStore);
        }
    }

    function validInputMeter() {
        var objMeter;
        var strMeter = "";
        if (currTabId == "PC") {
            objMeter = $("#meterA");
            strMeter = $("#meterA").val();
        } else {
            objMeter = $("#meterB");
            strMeter = $("#meterB").val();
        }

        for (var i = 0; i < strMeter.length; i++) {
            var f = strMeter.charAt(i);
            if (f < '0' || f > '9') {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.alert.onlydigit'/>");
                objMeter.focus();
                return false;
            }
        }

        return true;
    }

    function updateFChart() {
        emergePre();
        var param_location;
        var param_customerName;
        var param_meter;
        var param_type;
        var param_lineMissingType;

        if (currTabId == 'PC') {        // 정전
            param_location = $('#locationA').val();
            param_customerName = $('#customerNameA').val();
            param_meter = $('#meterA').val();
            param_type = $('#typeA').val();
            param_lineMissingType = "";
        } else if (currTabId == 'LM') { // 결상
            param_location = $('#locationB').val();
            param_customerName = $('#customerNameB').val();
            param_meter = $('#meterB').val();
            param_type = $('#typeB').val();
            param_lineMissingType = $('#lineMissingType').val();
        } else {
            param_location = "";
            param_customerName = "";
            param_meter = "";
            param_type = "";
            param_lineMissingType = "";
        }

        $.getJSON('${ctx}/gadget/device/getPowerAlarmLogMaxGadgetChartData.do'
                    ,{currTabId:currTabId,
                        location:param_location,
                        customerName:param_customerName,
                        meter:param_meter,
                        type:param_type,
                        lineMissingType:param_lineMissingType,
                        searchStartDate:$('#searchStartDate').val(),
                        searchEndDate:$('#searchEndDate').val(),
                        searchStartHour:$('#searchStartHour').val(),
                        searchEndHour:$('#searchEndHour').val(),
                        searchDateType:$('#searchDateType').val(),
                        supplierId:supplierId}
                ,function(json) {
                     var longOutageCodes = json.longOutageCodes;
                     var shortOutageCodes = json.shortOutageCodes;
                     var columnChartData = json.columnChartData;
                     var pieChartData = json.pieChartData;
                     fcColumnChartDataXml = "<chart "
                            + "yAxisName='<fmt:message key="aimir.count"/>' "
                            + "showValues='0' "
                            + "showLabels='1' "
                            + "showLegend='1' "
                            + "yaxismaxvalue='5' "
                            + "labelStep='"+Math.round(columnChartData.length / 6)+"' "
                            + "labelDisplay='NONE' "
                            + "legendPosition='RIGHT' "
                            + "reverseLegend='1' "
                            + "numberSuffix='  ' "
                            + fChartStyle_Common
                            + fChartStyle_Font
                            + fChartStyle_MSColumn3D_nobg
                            + ">";
                     var categories = "<categories>";
                     var seriesSize = longOutageCodes.length + shortOutageCodes.length + 1;
                     var datasets = new Array(seriesSize);

                     var n = 0;

                     for (var index = 0, len = shortOutageCodes.length; index < len; index++) {
                         datasets[n] = "<dataset seriesName='"+"<fmt:message key='aimir.over'/> "+ shortOutageCodes[index].descr +" <fmt:message key='aimir.min'/>" + "' >";
                         n++;
                     }

                     for (var index = 0, len = longOutageCodes.length; index < len; index++) {
                         datasets[n] = "<dataset seriesName='"+"<fmt:message key='aimir.over'/> "+ longOutageCodes[index].descr +" <fmt:message key='aimir.hour'/>" + "' >";
                         n++;
                     }

                     datasets[n] = "<dataset seriesName='"+"<fmt:message key='aimir.notRecover'/>"+"'>";

                     for (var index = 0, ln = columnChartData.length; index < ln; index++) {
                         if ($('#searchDateType').val() == "3" || $('#searchDateType').val() == "4") {
                             categories += "<category label='"+columnChartData[index].searchDate+"' />";
                         } else {
                             categories += "<category label='"+columnChartData[index].searchDate+"' />";
                         }

                         var temp = 0;

                         for (var index2 = 0, len = shortOutageCodes.length; index2 < len; index2++) {
                             if (eval("columnChartData["+index+"].type" + shortOutageCodes[index2].id) != null) {
                                 datasets[temp] += "<set value='"+eval("columnChartData["+index+"].type" + shortOutageCodes[index2].id)+"' />";
                             } else {
                                 datasets[temp] += "<set value='' />";
                             }
                             temp++;
                         }

                         for (var index2 = 0, len = longOutageCodes.length; index2 < len; index2++) {
                             if (eval("columnChartData["+index+"].type" + longOutageCodes[index2].id) != null) {
                                 datasets[temp] += "<set value='"+eval("columnChartData["+index+"].type" + longOutageCodes[index2].id)+"' />";
                             } else {
                                 datasets[temp] += "<set value='' />";
                             }
                             temp++;
                         }

                         if (columnChartData[index].open != null) {
                             datasets[temp] += "<set value='" + columnChartData[index].open + "' />";
                         } else {
                             datasets[temp] += "<set value='' />";
                         }
                     }

                     categories += "</categories>";

                     for (var i = 0; i < seriesSize; i++) {
                         datasets[i] += "</dataset>";
                     }

                     fcColumnChartDataXml += categories;

                     for (var i = 0; i < seriesSize; i++) {
                         fcColumnChartDataXml += datasets[i];
                     }
                     fcColumnChartDataXml += "</chart>";

                     fcPieChartDataXml = "<chart "
                     	+ "showPercentValues='1' "
                        + "showPercentInToolTip='0' "
                        + "showZeroPies='1' "
                        + "showLabels='0' "
                        + "showBorder='0' "
                        + "showValues='1' "
                        + "showLegend='1' "
                        + "pieRadius='90' "
                        + "legendPosition='RIGHT' "
                        + "manageLabelOverflow='1' "
                        + "numberSuffix='  ' "
                        + fChartStyle_Common
                        + fChartStyle_Font
                        + fChartStyle_Pie3D
                        + ">";
                     var labels = "";

                     var isEmptyOpened = true;
                     var isEmptyClosed = true;

                     for (var index = 0, len = pieChartData.length; index < len; index++) {
                         if (pieChartData[index].type == "open") {
                             labels += "<set label='<fmt:message key='aimir.open'/>' value='"+pieChartData[index].count+"' color='"+fChartColor_Step4[1]+"' />";
                             isEmptyOpened = false;
                         } else if(pieChartData[index].type == "close") {
                             labels += "<set label='<fmt:message key='aimir.close'/>' value='"+pieChartData[index].count+"' color='"+fChartColor_Step4[3]+"' />";
                             isEmptyClosed = false;
                         }
                     }

                     if (isEmptyOpened && isEmptyClosed) {
                         labels = "<set label='' value='1' color='E9E9E9' toolText='<fmt:message key='aimir.data.notexist'/>' displayValue=''/>"
                                + "<set label='<fmt:message key='aimir.open'/>' value='0' color='"+fChartColor_Step4[1]+"' displayValue=''/>"
                                + "<set label='<fmt:message key='aimir.close'/>' value='0' color='"+fChartColor_Step4[3]+"' displayValue=''/>";
                     } else if (isEmptyOpened) {
                         labels += "<set label='<fmt:message key='aimir.open'/>' value='0' color='"+fChartColor_Step4[1]+"' />";
                     } else if (isEmptyClosed) {
                         labels += "<set label='<fmt:message key='aimir.close'/>' value='0' color='"+fChartColor_Step4[3]+"' />";
                     }

                     fcPieChartDataXml += labels + "</chart>";

                     fcChartRender();

                     hide();
                }
            );
    }

    window.onresize = fcChartRender;
    function fcChartRender() {
        if ($('#fcColumnChartDiv').is(':visible')) {
        	fcColumnChart = new FusionCharts({
        		id: 'fcColumnChartId',
    			type: 'StackedColumn3D',
    			renderAt : 'fcColumnChartDiv',
    			width : $('#fcColumnChartDiv').width(),
    			height : '300',
    			dataSource : fcColumnChartDataXml
    		}).render();
        	
            /* fcColumnChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3D.swf", "fcColumnChartId",  $('#fcColumnChartDiv').width(), "300", "0", "0");
            fcColumnChart.setDataXML(fcColumnChartDataXml);
            fcColumnChart.setTransparent("transparent");
            fcColumnChart.render("fcColumnChartDiv"); */
        }

        if ($('#fcPieChartDiv').is(':visible')) {
        	fcPieChart = new FusionCharts({
        		id: 'fcPieChartId',
    			type: 'Pie3D',
    			renderAt : 'fcPieChartDiv',
    			width : $('#fcPieChartDiv').width(),
    			height : '300',
    			dataSource : fcPieChartDataXml
    		}).render();
        	
/*         	fcPieChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "fcPieChartId", $('#fcPieChartDiv').width(), "300", "0", "0");
            fcPieChart.setDataXML(fcPieChartDataXml);
            fcPieChart.setTransparent("transparent");
            fcPieChart.render("fcPieChartDiv"); */
        }
    }

    var win;
    function openExcelReport() {
        var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
        var obj = new Object();

        obj.condition   = getParams();
        obj.fmtMessage  = getFmtMessageMaxChart();
        obj.fmt     = getFmtMessage();

        if (win)
            win.close();
        win = window.open("${ctx}/gadget/device/powerAlarmLogExcelDownloadPopup.do", "PowerAlarmLogExcel", opts);
        win.opener.obj = obj;
    }
/*]]>*/
</script>
</head>
<body>

    <div id="powerLogMenu">
        <!-- 상단 Tab 설정 -->
        <ul>
            <li><a href="#powerCut"  id="_powerCut" ><fmt:message key="aimir.powerAlarmLog" /></a></li>
            <li><a href="#lineMissing" id="_lineMissing"><fmt:message key="aimir.lineMissing" /></a></li>
        </ul>

        <!-- search-background DIV (S) -->
        <div class="search-bg-withouttabs with-dayoptions-bt padding-reset">

            <div class="dayoptions-bt">
               <%@ include file="/gadget/commonDateTabButtonType.jsp"%>
            </div>
            <div class="dashedline"><ul><li></li></ul></div>

            <!--  Power Failure/Restore Tab (S) -->
            <div id="powerCut" class="searchoption-container">
                <table class="searchoption wfree">
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.location"/></td>
                        <td class="padding-r20px">
                            <input name="searchLocationA" id='searchLocationA' style="width:120px" type="text" />
                            <input type="hidden" id="locationA" value=""></input>

                        </td>
                        <td class="withinput"><fmt:message key="aimir.customername" /></td>
                        <td class="padding-r20px"><input type="text" id="customerNameA" /></td>
                        <td class="withinput"><fmt:message key="aimir.meterid" /></td>
                        <td><input type="text" id="meterA" /></td>
                        <td>
                            <select id="typeA" style="width:150px;">
                                <option value=""><fmt:message key="aimir.poweroff.duration" /></option>
                                <option value="open"><fmt:message key="aimir.notRecover" /></option>
                                <c:forEach var="longOutageCode" items="${longOutageCodes}">
                                    <option value="${longOutageCode.id}">Over ${longOutageCode.descr} hour</option>
                                </c:forEach>
                                <c:forEach var="shortOutageCode" items="${shortOutageCodes}">
                                    <option value="${shortOutageCode.id}">Over ${shortOutageCode.descr} minute</option>
                                </c:forEach>
                            </select>
                        </td>
                        <td>
                            <em class="am_button"><a href="javascript:searchPowerAlarmLog();" class="on"><fmt:message key="aimir.button.search" /></a></em>
                            <em class="am_button"><a href="javascript:openExcelReport();" class="on"><fmt:message key="aimir.button.excel"/></a></em>
                        </td>
                    </tr>
                </table>
                <div class="clear">
                    <div id="treeDiv1Outer" class="tree-billing auto"  style="display:none;">
                        <div id="treeDiv1"></div>
                    </div>
                </div>
            </div>
            <!--  Power Failure/Restore Tab (E) -->


            <!--  Line Missing(결상) Tab (S) -->
            <div id="lineMissing" class="searchoption-container">
                <table class="searchoption wfree">
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.location"/></td>
                        <td class="padding-r20px">
                            <input name="searchLocationB" id='searchLocationB' style="width:120px" type="text" />
                            <input type="hidden" id="locationB" value=""></input>

                        </td>
                        <td class="withinput"><fmt:message key="aimir.customername" /> </td>
                        <td class="padding-r20px"><input type="text" id="customerNameB" /></td>
                        <td class="withinput"><fmt:message key="aimir.meterid" /> </td>
                        <td><input type="text" id="meterB" /></td>
                        <td>
                            <select id="typeB" style="width:150px;">
                                <option value=""><fmt:message key="aimir.poweroff.duration" /></option>
                                <option value="open"><fmt:message key="aimir.notRecover" /></option>
                                <c:forEach var="longOutageCode" items="${longOutageCodes}">
                                    <option value="${longOutageCode.id}">Over ${longOutageCode.descr} hour</option>
                                </c:forEach>
                                <c:forEach var="shortOutageCode" items="${shortOutageCodes}">
                                    <option value="${shortOutageCode.id}">Over ${shortOutageCode.descr} minute</option>
                                </c:forEach>
                            </select>
                        </td>
                        <td>
                            <select id="lineMissingType" style="width:120px;">
                            <option value=""><fmt:message key="aimir.phasetype" /></option>
                            <c:forEach var="lineMissingType" items="${lineMissingTypes}">
                                <option value="${lineMissingType}">${lineMissingType.name}</option>
                            </c:forEach>
                            </select>
                        </td>
                        <td>
                            <em class="am_button"><a href="javascript:searchPowerAlarmLog();" class="on"><fmt:message key="aimir.button.search" /></a></em>
                            <em class="am_button"><a href="javascript:openExcelReport();" class="on"><fmt:message key="aimir.button.excel"/></a></em>
                        </td>
                    </tr>
                </table>
                <div class="clear">
                    <div id="treeDiv2Outer" class="tree-billing auto"  style="display:none;">
                        <div id="treeDiv2"></div>
                    </div>
                </div>
            </div>
            <!--  Line Missing(결상) Tab (E) -->

        </div>
        <!-- search-background DIV (E) -->



    <div class="gadget_body">
        <div id="fcChartParentDiv">
            <div id="fcColumnChartDiv" class="floatleft" style="width:60%;height:300px;">
                The chart will appear within this DIV. This text will be replaced by the chart.
            </div>
            <div id="fcPieChartDiv"   class="floatleft" style="width:40%;height:300px;">
                The chart will appear within this DIV. This text will be replaced by the chart.
            </div>

        </div>

     </div>
    <div id = "powerAlarmLogGridDiv"  style="padding-left: 20px;"></div>

</html>