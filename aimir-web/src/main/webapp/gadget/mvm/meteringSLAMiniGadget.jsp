<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<title>MeteringSLA MiniGadget</title>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">

<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<%@ include file="/gadget/system/preLoading.jsp"%>

<script type="text/javascript" charset="utf-8">

    var flexSummaryGrid;
//    var flexMiniChart;

    var fcChartDataXml;
    var fcChart;

    var supplierId = "";

    //탭초기화
    // 값 0 - 숨김처리
    // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
    var tabs     = {hourly:0,daily:0,period:1,weekly:0,monthly:0,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};

    // 탭명칭 변경시 값입력
    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

    /**
     * 유저 세션 정보 가져오기
     */
    $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                if(json.supplierId != ""){
                    supplierId = json.supplierId;

                    updateFChart();
                }
            }
    );

    $(function(){
    	//flexSummaryGrid    = getFlexObject('summaryGrid');
//    	flexMiniChart      = getFlexObject('miniChart');
    });


    function getSLACondition(){
        var condArray = new Array();

        condArray[0] = $('#searchStartDate').val();
        condArray[1] = $('#searchEndDate').val();
        condArray[2] = supplierId;

        return condArray;
    }

    function getMsgSLASummaryGrid(){
        var fmtMessage = new Array();
        fmtMessage[0] = "";
        fmtMessage[1] = "<fmt:message key="aimir.count"/>";
        fmtMessage[2] = "<fmt:message key="aimir.percent"/>";

        var dataFild = new Array();
        dataFild[0] = "label";
        dataFild[1] = "slaCount";
        dataFild[2] = "slaPercent";

        var gridAlign = new Array();
        gridAlign[0] = "center";
        gridAlign[1] = "right";
        gridAlign[2] = "right";

        var gridWidth = new Array();
        gridWidth[0] = "1000";
        gridWidth[1] = "1000";
        gridWidth[2] = "1000";

        var gridWidth = new Array();
        gridWidth[0] = "2000";
        gridWidth[1] = "1000";
        gridWidth[2] = "1000";

        var rowName = new Array();
        rowName[0] = "<fmt:message key="aimir.totalInstalledMeters"/>";
        rowName[1] = "<fmt:message key="aimir.commPermitted"/>";
        rowName[2] = "<fmt:message key="aimir.permitted"/>";

        var dataGrid = new Array();
        dataGrid[0] = fmtMessage;
        dataGrid[1] = dataFild;
        dataGrid[2] = gridAlign;
        dataGrid[3] = gridWidth;
        dataGrid[4] = rowName;

        return dataGrid;
    }

    function send(){
    	updateFChart();

    	//flexSummaryGrid.requestSend();
//    	flexMiniChart.requestSend();
	}

    function updateFChart() {
    	emergePre();
    	
   	    $.getJSON('${ctx}/gadget/mvm/getMeteringSLAMiniChart.do'
   	    	    ,{searchStartDate:$('#searchStartDate').val(), 
   	    	    	searchEndDate:$('#searchEndDate').val(),
   	    	    	supplierId:supplierId}
				,function(json) {
                     var list = json.chartData;
                     fcChartDataXml = "<chart "
	                    + "yAxisName='SLA(%)' "
	                    + "yAxisMaxValue='100' "
	                    + "yAxisMinValue='0' "
                   	 	+ "chartLeftMargin='5' "
						+ "chartRightMargin='5' "
						+ "chartTopMargin='10' "
						+ "chartBottomMargin='0' "
                        + "showValues='0' "
                        + "showLabels='1' "
                        + "showLegend='1' "
                        + "labelDisplay = 'AUTO' "                         
	                    + fChartStyle_Common
	                    + fChartStyle_Font
	                    + fChartStyle_MSLine_nobg
                        + ">";
                	 var categories = "<categories>";
                	 var dataset = "<dataset seriesName='<fmt:message key='aimir.success.ratio.percent'/>'>";

                     if(list == null || list.length == 0) {
                    	 categories += "<category label=' ' />";
                    	 dataset += "<set value='' />";
                     } else {
                    	 for( index in list){
                             if(index != "indexOf") {
                            	 categories += "<category label='"+list[index].xTag+"' />";
                            	 dataset += "<set value='"+list[index].successRate+"' />";
                             }
                         }
                     }

                     
                     categories += "</categories>";
                     dataset += "</dataset>";
                     
                     fcChartDataXml += categories + dataset + "</chart>";
                     
                     fcChartRender();
                     hide();
                }
   	    );   		
	}

    window.onresize = fcChartRender;
    function fcChartRender() {
    	if($('#fcChartDiv').is(':visible')) {
	    	fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSLine.swf", "myChartId", $('#fcChartDiv').width(), "240", "0", "0");
	        fcChart.setDataXML(fcChartDataXml);
	        fcChart.setTransparent("transparent");
	        fcChart.render("fcChartDiv");
    	}
    }

    </script>

</head>
<body>

<!-- search-background DIV (S) -->
<div class="search-bg-withtabs">
	<div class="dayoptions">
		<%@ include file="/gadget/commonDateTab.jsp"%>
	</div>
</div>
<!-- search-background DIV (E) -->


<div class="gadget_body2">
	<div id="fcChartDiv" class="margin-t5px">
		The chart will appear within this DIV. This text will be replaced by the chart.
	</div>
	<!-- 
	<object id="miniChartEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="158px">
		<param name="movie" value="${ctx}/flexapp/swf/meteringSLAMiniChart.swf" />
		<param name="wmode" value="opaque">
		<!--[if !IE]>-->
		<!-- 
		<object id="miniChartOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/meteringSLAMiniChart.swf" width="100%" height="158px">
		<param name="wmode" value="opaque">
		<!--<![endif]-->
		<!-- 
		<div>
			<h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
			<p><a href="http://www.adobe.com/go/getflashplayer"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash player" width="112" height="33" /></a></p>
		</div>
		<!--[if !IE]>-->
		<!-- 
		</object>
		<!--<![endif]-->
	<!-- 
	</object>
	 -->
</div>
<!--  
<div class="gadget_body3">
	<object id="summaryGridEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="86">
		<param name="movie" value="${ctx}/flexapp/swf/meteringSLASummaryGrid.swf" />
		<param name="wmode" value="opaque">
		<object id="summaryGridOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/meteringSLASummaryGrid.swf" width="100%" height="86">
		<param name="wmode" value="opaque">
		<div>
			<h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
			<p><a href="http://www.adobe.com/go/getflashplayer"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash player" width="112" height="33" /></a></p>
		</div>
		</object>
	</object>
</div>
-->

</body>
</html>