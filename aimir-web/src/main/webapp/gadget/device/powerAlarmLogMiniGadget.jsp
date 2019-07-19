<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title>PowerAlarmLog Gadget</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
<%@ include file="/gadget/system/preLoading.jsp"%>
<script type="text/javascript" charset="utf-8">

    var currTabId = "PC";
    var supplierId = "${supplierId}";

    var fcChartDataXml;
    var fcChart;

    /**
     * 유저 세션 정보 가져오기
     */
    /* $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                if(json.supplierId != ""){
                    supplierId = json.supplierId;
                    updateFChart();
                }
            }
    ); */

    $(document).ready(function() {
        updateFChart();
    });

    $(function() {
        $(function() { $('#_powerCut').bind('click',function(event) { currTabId="PC"; requestSend(); } ); });
        $(function() { $('#_lineMissing').bind('click',function(event) { currTabId="LM"; requestSend(); } ); });

        $("#powerLogMini").tabs();
        $("#lineMissingType").selectbox();
    });

    function getFmtMessage() {
        var fmtMessage = new Array();;

        fmtMessage[0] = "<fmt:message key="aimir.type2"/>";
        fmtMessage[1] = "<fmt:message key="aimir.day"/>";

        return fmtMessage;
    }

    function getParams() {
        var conditionArray = new Array();

        conditionArray[0] = currTabId;
        conditionArray[1] = $("#lineMissingType").val();
        conditionArray[2] = supplierId;

        return conditionArray;
    }

    function requestSend() {
        updateFChart();
    }

    function updateFChart() {
        emergePre();
        $.getJSON('${ctx}/gadget/device/getPowerAlarmLogMiniGadgetChartData.do'
                    ,{supplierId:supplierId,
                        colFormat:"",
                        lineMissingType:$("#lineMissingType").val(),
                        currTabId:currTabId}
                ,function(json) {
                     var longOutageCodes = json.longOutageCodes;
                     var shortOutageCodes = json.shortOutageCodes;
                     var chartData = json.chartData;
                     fcChartDataXml = "<chart "
                            + "yAxisName='<fmt:message key="aimir.count"/>' "
                            + "chartLeftMargin='0' " 
                            + "showValues='0' " 
                            + "showLabels='1' "
                            + "showLegend='1' " 
                            + "labelStep='2' "
                            + "yaxismaxvalue='5' "
                            + "labelDisplay = 'NONE' "
                            + "legendPosition='RIGHT' "
                            + "reverseLegend='1' " 
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

                     for (var index = 0, ln = chartData.length; index < ln; index++) {
                         categories += "<category label='"+chartData[index].searchDate+"' />";

                         var temp = 0;
                         for (var index2 = 0, len = shortOutageCodes.length; index2 < len; index2++) {
                             if (eval("chartData["+index+"].type" + shortOutageCodes[index2].id) != null) {
                                 datasets[temp] += "<set value='"+eval("chartData["+index+"].type" + shortOutageCodes[index2].id)+"' />";
                             } else {
                                 datasets[temp] += "<set value='' />";
                             }
                             temp++;
                         }

                         for (var index2 = 0, len = longOutageCodes.length; index2 < len; index2++) {
                             if (eval("chartData["+index+"].type" + longOutageCodes[index2].id) != null) {
                                 datasets[temp] += "<set value='"+eval("chartData["+index+"].type" + longOutageCodes[index2].id)+"' />";
                             } else {
                                 datasets[temp] += "<set value='' />";
                             }
                             temp++;
                         }

                         datasets[temp] += "<set value='' />";
                         temp++;
                     }

                     categories += "</categories>";

                     for (var i = 0; i < seriesSize; i++) {
                         datasets[i] += "</dataset>";
                     }

                     fcChartDataXml += categories;

                     for (var i = 0; i < seriesSize; i++) {
                        fcChartDataXml += datasets[i];
                     }
                     fcChartDataXml += "</chart>";

                     fcChartRender();

                     hide();
                }
            );
    }

    window.onresize = fcChartRender;
    function fcChartRender() {
        if ($('#fcChartDiv').is(':visible')) {
        	fcChart = new FusionCharts({
        		id: 'fcChartId',
    			type: 'StackedColumn3D',
    			renderAt : 'fcChartDiv',
    			width : $('#fcChartDiv').width(),
    			height : '250',
    			dataSource : fcChartDataXml
    		}).render();
        	/* 
            fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3D.swf", "fcChartId", $('#fcChartDiv').width(), "250", "0", "0");
            fcChart.setDataXML(fcChartDataXml);
            fcChart.setTransparent("transparent");
            fcChart.render("fcChartDiv"); */
        }
    }
</script>
</head>
<body>
    <div id="powerLogMini">
        <!-- 상단 Tab 설정 -->
        <ul>
            <li><a href="#powerCut"  id="_powerCut" ><fmt:message key="aimir.powerAlarmLog" /></a></li>
            <li><a href="#lineMissing" id="_lineMissing"><fmt:message key="aimir.lineMissing" /></a></li>
        </ul>

        <div class="clear margin-t5px" style="margin-right:-10px">
            <!--  정전/복구 내역 Tab  -->
            <div id="powerCut"></div>

            <!--  Line Missing(결상) Tab  -->
            <div id="lineMissing">
                <ul class="floatright">
                    <li><select id="lineMissingType" style="width:115px;" onChange="javascript:requestSend();">
                            <option value=""><fmt:message key="aimir.phasetype2" /></option>
                            <c:forEach var="lineMissingType" items="${lineMissingTypes}">
                                <option value="${lineMissingType}">${lineMissingType.name}</option>
                            </c:forEach>
                        </select>
                    </li>
                </ul>
            </div>
            <!--  탭이하 내용(E)  -->
        </div>

        <div class="clear" >
        <div id="fcChartDiv">The chart will appear within this DIV. This text will be replaced by the chart.</div>
        </div>
    </div>
</body>
</html>