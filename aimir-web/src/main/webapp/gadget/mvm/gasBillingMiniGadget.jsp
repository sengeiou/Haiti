<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Billing Mini Gadget</title>
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
	<script type="text/javascript">
		var supplierId = "";

		$(document).ready(function(){
			 /**
		     * 유저 세션 정보 가져오기
		     */
		    $.getJSON('${ctx}/common/getUserInfo.do',
		            function(json) {
		                if(json.supplierId != ""){
		                    supplierId = json.supplierId;
		                }
		                
		                updateFChart();
		            }
		    );
		});
		
	   

	    var tabs     = {hourly:0,daily:1,period:0,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};
	    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

	    var fcChartDataXml;
	    var fcChart;

		$(function() {

			$("div div div[id='btn']").hide();

			locationTreeGoGo('treeDivA', 'searchWord', 'locationId');
		});

	    var chartType = 'columnChart';

	    var getConditionArray = function() {

            var arrayObj = Array();

            arrayObj[0] = $('#searchStartDate').val();
            arrayObj[1] = $('#searchEndDate').val();
			arrayObj[2] =  $('#searchDateType').val();
            arrayObj[3] = chartType;
            arrayObj[4] = $('#locationId').val();//getLocationIds($('#treeDiv'));
            arrayObj[5] = "GM";

            return arrayObj;
	    };

	    function updateFChart() {
	    	emergePre();
	    	
	   	    $.getJSON('${ctx}/gadget/mvm/getBillingFusionChartData.do'
	   	    	    ,{startDate:$('#searchStartDate').val(), 
	   	    	    	endDate:$('#searchEndDate').val(),
	   	    	    	chartType:chartType, 
	   	    	    	searchDateType:$('#searchDateType').val(),
	   	    	    	locationIds:$('#locationId').val(), 
	   	    	    	serviceType:"GM"}
					,function(json) {
                         var list = json.gridDatas;
                         fcChartDataXml = "<chart "
                        	 + "chartLeftMargin='0' "
						 	 + "chartRightMargin='0' "
						 	 + "chartTopMargin='10' "
						 	 + "chartBottomMargin='0' "
                        	 + "showValues='0' "
                        	 + "numberSuffix=' kWh' "
                             + fChartStyle_Common
                             + fChartStyle_Font
                             + fChartStyle_Column3D_nobg
                             + ">";
                    	 var labels = "";
                         for( index in list){
	                         if(index != "indexOf") {
	                         	labels	+= "<set label='"+list[index].locationName+"' value='"+list[index].kwh+"' color='"+fChartColor_Gas[0]+"' />";
	                         }
                         }

                         if(list.length == 0) {
                        	 labels	= "<set label=' ' value='0' color='"+fChartColor_Gas[0]+"' />";
                         }

                         fcChartDataXml += labels + "</chart>";                         
                         fcChartRender();
                         hide();
	                }
	   	    );	   		
		}

	    window.onresize = fcChartRender;
	    function fcChartRender() {
	    	if($('#fcChartDiv').is(':visible')) {
		    	fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Column3D.swf", "myChartId", $('#fcChartDiv').width(), "210", "0", "0");
		        fcChart.setDataXML(fcChartDataXml);
		        fcChart.setTransparent("transparent");
		        fcChart.render("fcChartDiv");
	    	}
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
	
		<div class="dashedline"></div>
	
		<div class="searchoption-container">
			<table class="searchoption wfree">
			<tr><td><input name="searchWord" id='searchWord' class="billing-searchword" type="text" value='<fmt:message key="aimir.board.location"/>' /></td>
				<td><a href="javascript:updateFChart();" id="btnSearch" class="btn_blue"><span><fmt:message key="aimir.button.search" /></span></a></td>
			</tr>
			</table>
		</div>
		<div id="treeDivAOuter" class="tree-billing auto" style="display:none;">
			<div id="treeDivA"></div>
		</div>
	</div>
	<!-- search-background DIV (E) -->
	
	<div id="fcChartDiv" class="margin10px">
	    The chart will appear within this DIV. This text will be replaced by the chart.
	</div>
</body>
</html>