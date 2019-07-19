<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<title>DeviceRegistration MiniGadget</title>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
<%@ include file="/gadget/system/preLoading.jsp"%>
<script type="text/javascript" charset="utf-8">

    var flex;
    //var miniTab = "MCU";
    var supplierId = "";
	var detailChartType = "pie";
    var fcChartDataXml;
    var fcChart;

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

    function changeChartType() {
        
        var chartType = $('#chartType').val();
        
        detailChartType = chartType;
        updateFChart();
    }

    $(document).ready(function() {	
        $('#chartType').selectbox();
    	updateFChart();
    });
    
    function updateFChart() {
    	emergePre();
    	
   	    $.getJSON('${ctx}/gadget/device/getAssetMiniChart.do'
   	    	    ,{supplierId:supplierId}
				,function(json) {
                     var resultData = json.chartData;
                     fcChartDataXml = "<chart "
                    	 + "showValues='1' "
    					 + "showPercentValues='1' "
    					 + "showPercentInToolTip='0' "
    				     + "showZeroPies='1' "
    				     + "showLabels='0' "
    				     + "showLegend='1' "
    				     + "legendPosition='BOTTOM' "
    				     + "manageLabelOverflow='1' "
    				     + "numberSuffix='  ' "
    				     + fChartStyle_Common
                    	 + fChartStyle_Font;
                         
                        if(detailChartType == "pie") {

                            fcChartDataXml += "pieRadius='70' "
                             + fChartStyle_Pie3D
                             + "toolTipSepChar=',<fmt:message key='aimir.count'/>,'"
                             + ">";

                            var labels = "";
                             if(resultData != null && (resultData.mcuCount != 0 || resultData.modemCount != 0 
                                     || resultData.meterCount != 0 || resultData.contractCount != 0 || resultData.customerCount != 0)) {
                                labels += "<set label='<fmt:message key='aimir.mcu'/>' value='"+resultData.mcuCount+"' />"
                                + "<set label='<fmt:message key='aimir.modem'/>' value='"+resultData.modemCount+"' />"
                                + "<set label='<fmt:message key='aimir.meter'/>' value='"+resultData.meterCount+"' />"
                                + "<set label='<fmt:message key='aimir.contract'/>' value='"+resultData.contractCount+"' />"
                                + "<set label='<fmt:message key='aimir.customer'/>' value='"+resultData.customerCount+"' />";
                            } else {
                                labels += "<set label='<fmt:message key='aimir.mcu'/>' value='0' />"
                                + "<set label='<fmt:message key='aimir.modem'/>' value='0' />"
                                + "<set label='<fmt:message key='aimir.meter'/>' value='0' />"
                                + "<set label='<fmt:message key='aimir.contract'/>' value='0' />"
                                + "<set label='<fmt:message key='aimir.customer'/>' value='0' />"
                                + "<set label='' value='1' color='E9E9E9' toolText='<fmt:message key='aimir.data.notexist'/>' />";
                            }
                            fcChartDataXml += labels;
                        } else if(detailChartType == "column") {
                            fcChartDataXml += fChartStyle_MSColumn3D_nobg
                            + "chartTopMargin='30'"
                            + ">";
                            
                            var categories = "<categories>";
                            var datasets = new Array(5);

                            categories += "<category label='<fmt:message key='aimir.count'/>' />";

                            if (resultData.mcuCount != null) {
                                datasets[0] = "<dataset seriesName='<fmt:message key='aimir.mcu'/>'>"
                                            + "<set y='0' value='"+resultData.mcuCount+"' x='0'/>"
                                            + "</dataset>";
                            }
                            if (resultData.modemCount != null) {
                                datasets[1] = "<dataset seriesName='<fmt:message key='aimir.modem'/>'>"
                                            + "<set y='0' value='"+resultData.modemCount+"' x='0'/>"
                                            + "</dataset>";
                            }
                            if (resultData.meterCount != null) {
                                datasets[2] = "<dataset seriesName='<fmt:message key='aimir.meter'/>'>"
                                            + "<set y='0' value='"+resultData.meterCount+"' x='0'/>"
                                            + "</dataset>";                                
                            }
                            if (resultData.contractCount != null) {
                                datasets[3] = "<dataset seriesName='<fmt:message key='aimir.contract'/>'>"
                                            + "<set y='0' value='"+resultData.contractCount+"' x='0'/>"
                                            + "</dataset>";
                            }
                            if (resultData.customerCount != null) {
                                datasets[4] = "<dataset seriesName='<fmt:message key='aimir.customer'/>'>"
                                            + "<set y='0' value='"+resultData.customerCount+"' x='0'/>"
                                            + "</dataset>";
                            }
                            categories += "</categories>";
                            fcChartDataXml += categories
                            + datasets[0]
                            + datasets[1]
                            + datasets[2]
                            + datasets[3]
                            + datasets[4]; 

                        }
                        
                     fcChartDataXml += "</chart>";
                     
                     fcChartRender();

                     hide();
                }
   	    );


	}

    window.onresize = fcChartRender;
    function fcChartRender() {
    	if($('#fcChartDiv').is(':visible')) {
            if ( FusionCharts( "myChartId" ) ) FusionCharts( "myChartId" ).dispose();
            if(detailChartType == "pie") {
        		typeFChart = new FusionCharts({
	        		id: 'myChartId',
    				type: 'Pie3D',
    				renderAt : 'fcChartDiv',
    				width : $('#fcChartDiv').width(),
    				height : '280',
    				dataSource : fcChartDataXml
    			}).render();
            } else if(detailChartType == "column") {
				typeFChart = new FusionCharts({
    				id: 'myChartId',
					type: 'MSColumn3D',
					renderAt : 'fcChartDiv',
					width : $('#fcChartDiv').width(),
					height : '280',
					dataSource : fcChartDataXml
				}).render();
            }
    	}
    }

    </script>

</head>
<body>
    <form name="deviceForm" id = "deviceForm">
        <div id="deviceRegMini" class="search-bg-basic">
            <ul class="basic-ul">
                <li class="basic-li gray11pt withinput"><fmt:message key="aimir.chartType"/></li>
                <li class="basic-li">
                    <select id="chartType" name="chartType" class='selectbox' style="width:130px;" onchange="javascript:changeChartType();">
                        <option value="pie" selected>Pie Chart</option>
                        <option value="column">Column Chart</option>
                    </select>
                </li>
            </ul>
        </div>
    </form>

    <div id="fcChartDiv">
	    The chart will appear within this DIV. This text will be replaced by the chart.
	</div>
<!-- 
    <div id="gadget_body">
		<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="265px" id="deviceRegMiniChartEx">
				<param name="movie" value="${ctx}/flexapp/swf/deviceRegistrationMiniChart.swf" />
				<param name='wmode' value='transparent' />

				<!--[if !IE]>-->
				<!--
				<object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/deviceRegistrationMiniChart.swf" width="100%" height="265px" id="deviceRegMiniChartOt">
				<param name='wmode' value='transparent' />
				<!--<![endif]-->
				<!--
				<p>Alternative content</p>
				<!--[if !IE]>-->
				<!--
				</object>
				<!--<![endif]-->
			<!--
		</object>
    </div>
 -->

    </body>
</html>