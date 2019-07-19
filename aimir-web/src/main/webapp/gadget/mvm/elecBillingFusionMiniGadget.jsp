<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>

<head>
	<%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Billing Mini Gadget</title>
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" src="${ctx}/js/prevention.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/FusionCharts.js"></script>
	<script type="text/javascript">

	    var supplierId = "";

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

	    var tabs     = {hourly:0,daily:1,period:0,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:1,yearly:0};
	    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};
	    
	    $(function() {

			$("div div div[id='btn']").hide();

			locationTreeGoGo('treeDivA', 'searchWord', 'locationId');
		});

	    $(document).ready(function() {
			hide();
			updateThreshold();
	    });

	    var chartType = 'columnChart';

	    var getConditionArray = function() {

            var arrayObj = Array();

            arrayObj[0] = $('#searchStartDate').val();
            arrayObj[1] = $('#searchEndDate').val();
			arrayObj[2] =  $('#searchDateType').val();
            arrayObj[3] = chartType;
            arrayObj[4] = $('#locationId').val();//getLocationIds($('#treeDiv'));
            arrayObj[5] = "EM";

            return arrayObj;
	    };

		function updateThreshold() {
			emergePre();
			
	   	    $.getJSON('${ctx}/gadget/mvm/getBillingFusionChartData.do'
	   	    	    ,{startDate:$('#searchStartDate').val(), 
	   	    	    	endDate:$('#searchEndDate').val(),
	   	    	    	chartType:chartType, 
	   	    	    	searchDateType:$('#searchDateType').val(),
	   	    	    	locationIds:$('#locationId').val(), 
	   	    	    	serviceType:"EM"}
					,function(json) {
                         var list = json.result;
                         var columnChartDataXml = "<chart yAxisName='kWh' showValues='0' >";
                    	 var labels = "";
                         for( index in list){
	                         if(index != "indexOf") {
	                         	labels	+= "<set label='"+list[index].locationName+"' value='"+list[index].kwh+"' />"
	                         }
                         }
                         columnChartDataXml += labels + "</chart>";

                         if($('#columnChartDiv').is(':visible')) {
	                         var columnChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Column2D.swf", "myChartId", $('#columnChartDiv').width(), "250", "0", "0");
	                         columnChart.setDataXML(columnChartDataXml);
	                         columnChart.setTransparent("transparent");
	                         columnChart.render("columnChartDiv");
                         }
	                }
	   	    );

	   	    hide();
		}

	</script>

</head>

<body>

<input type='hidden' id='locationId' value=''></input>

<!-- search-background DIV (S) -->
<div class="search-bg-withtabs">

	<div class="dayoptions">
	<%@ include file="../commonDateTab.jsp" %>
	</div>

	<div class="dashedline"><ul><li></li></ul></div>

		<div class="searchoption-container">
			<table class="searchoption wfree">
			<tr>
				<td><input name="searchWord" id='searchWord' class="billing-searchword" type="text" value='<fmt:message key="aimir.board.location"/>' /></td>
				<td>
					<div class="btn">
						<ul><li><a href="javascript:updateThreshold();" class="on" id="btnSearch"><fmt:message key="aimir.button.search" /></a></li></ul>						
					</div>
				</td>
			</tr>
			</table>
		</div>
		<div id="treeDivAOuter" class="tree-billing auto" style="display:none;">
			<div id="treeDivA"></div>
		</div>

</div>

<div id="columnChartDiv" align="left">
    The chart will appear within this DIV. This text will be replaced by the chart.
</div>

</body>
</html>