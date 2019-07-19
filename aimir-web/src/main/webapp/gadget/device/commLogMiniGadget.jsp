<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>통신이력관리</title>
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
	
	<%@ include file="/gadget/system/preLoading.jsp"%>
	
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
	<script type="text/javascript" charset="utf-8">

    var supplierId = "";

    var fcChartDataXml;
    var fcChart;
    var fcChartType = "";


	var labelMetering = "<fmt:message key='aimir.button.metering'/>";
	var labelCommand ="<fmt:message key='aimir.instrumentation'/>";
	var labelEvent ="<fmt:message key='aimir.event'/>";

    /**
     * 유저 세션 정보 가져오기
     */
    $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                if(json.supplierId != ""){
                    supplierId = json.supplierId;
                }

                getCommLogSendReceiveChartData();
            }
    );

	$(function() {

		$('#chartType').change(function () {
			searchList(this.value);
    	});

		$("#chartType").selectbox();
	});

	var searchList = function(_type) {
		/*
    	if(document["commLogChartFlexOt"] == null){
			window["commLogChartFlex"].searchList(_type);
		}else{
			document["commLogChartFlexOt"].searchList(_type);
		}*/

		if(_type == "SendReceive") {
			getCommLogSendReceiveChartData();
		} else if(_type == "SVCType") {
			getCommLogSVCTypeChartData();
		} else if(_type == "Location") {
			getCommLogLocationChartData();
		}

//    	getFlexObject('commLogChartFlex').searchList(_type);
	};
	
	

	function getCommLogSendReceiveChartData() {		
		emergePre();
   	    $.getJSON('${ctx}/gadget/device/commLog/commLogSendReceiveChartData.do'
   	    	    ,{supplierId:supplierId}
	   	    	,function(json) {
	                var list = json.chartMapDatas;	                
	                fcChartDataXml = "<chart "
	                	+ "chartLeftMargin='0' "
						+ "chartRightMargin='0' "
						+ "chartTopMargin='10' "
						+ "chartBottomMargin='0' "
	                    + "showValues='0' "
	                    + "showLabels='1' "
	                    + "showLegend='1' "
	                    + " yAxisName='byte'"
	                    //+ "labelDisplay='AUTO' "
	                    //+ "numberSuffix=' byte' "
	                    + fChartStyle_Common
	                    + fChartStyle_Font
	                    + fChartStyle_MSColumn3D_nobg 
	                    + ">";
		            var categories = "<categories>";
		            var dataset1 = "<dataset seriesName='<fmt:message key='aimir.header.receivebytes'/>'>";
		            var dataset2 = "<dataset seriesName='<fmt:message key='aimir.header.sendbytes'/>'>";
	                for( index in list){
	                    if(index != "indexOf") {
	                  	 categories += "<category label='"+list[index].date+"' />";
	                   	 dataset1 += "<set value='"+list[index].rcvCnt+"' />";
	                   	 dataset2 += "<set value='"+list[index].sendCnt+"' />";
	                    }
	                }
	                categories += "</categories>";
	                dataset1 += "</dataset>";
	                dataset2 += "</dataset>";

	                if(list.length == 0) {
						categories = "<categories><category label=' ' /></categories>";
		                dataset1 = "<dataset seriesName='<fmt:message key='aimir.header.receivebytes'/>'>0</dataset>";
		                dataset2 = "<dataset seriesName='<fmt:message key='aimir.header.sendbytes'/>'>0</dataset>";
	                }
	               
	                fcChartDataXml += categories + dataset1 + dataset2 + "</chart>";

	                fcChartType = "MSColumn3D";
	                fcChartRender();

	                hide();
	            }
   	    );
	}

	function getCommLogSVCTypeChartData() {
		emergePre();
   	
   	    $.getJSON('${ctx}/gadget/device/commLog/commLogSVCTypeChartData.do'
   	    	    ,{supplierId:supplierId}
   	    	 	,function(json) {
	                var list = json.chartMapDatas;
	                fcChartDataXml = "<chart "
	                	+ "chartLeftMargin='0' "
						+ "chartRightMargin='0' "
						+ "chartTopMargin='10' "
						+ "chartBottomMargin='0' "
	                    + "showValues='0' "
	                    + "showLabels='1' "
	                    + "showLegend='1' "
	                    //y축 name 설정
	                    + " yAxisName='byte'"
	                    //+ "labelDisplay='AUTO' "
	                    + fChartStyle_Common
	                    + fChartStyle_Font
	                    + fChartStyle_MSColumn3D_nobg
	                    + ">";
		            var categories = "<categories>";
		            var dataset1 = "<dataset seriesName='<fmt:message key='aimir.instrumentation'/>'>";
		            var dataset2 = "<dataset seriesName='<fmt:message key='aimir.button.metering'/>'>";		          
		            var dataset3 = "<dataset seriesName='<fmt:message key='aimir.event'/>'>";
	                for( index in list){
	                    if(index != "indexOf") {
	                  	 categories += "<category label='"+list[index].date+"' />";
	                   	 dataset1 += "<set value='"+list[index].meteringCnt+"' />";
	                   	 dataset2 += "<set value='"+list[index].ondemandCnt+"' />";
	                   	 dataset3 += "<set value='"+list[index].eventCnt+"' />";
	                    }
	                }
	                categories += "</categories>";
	                dataset1 += "</dataset>";
	                dataset2 += "</dataset>";
	                dataset3 += "</dataset>";

					if(list.length == 0) {
						categories = "<categories><category label=' ' /></categories>";
						dataset1 = "<dataset seriesName='<fmt:message key='aimir.instrumentation'/>'>0</dataset>";
		                dataset2 = "<dataset seriesName='<fmt:message key='aimir.button.metering'/>'>0</dataset>";
		                dataset3 = "<dataset seriesName='<fmt:message key='aimir.event'/>'>0</dataset>";
	                }
						               
	                fcChartDataXml += categories + dataset1 + dataset2 + dataset3 + "</chart>";

	                fcChartType = "MSColumn3D";
	                fcChartRender();

	                hide();
	            }
   	    );
	}

	function getCommLogLocationChartData() {
		emergePre();
    	
   	    $.getJSON('${ctx}/gadget/device/commLog/commLogLocationChartData.do'
   	    	    ,{}
   	    	 	,function(json) {
	                var list = json.chartDatas;
	                fcChartDataXml = "<chart "
	                	+ "chartLeftMargin='0' "
						+ "chartRightMargin='0' "
						+ "chartTopMargin='10' "
						+ "chartBottomMargin='0' "
	                    + "showValues='0' "
	                    + "showLabels='1' "
	                    + "showLegend='1' "
	                    + "labelDisplay='AUTO' "
	                    + fChartStyle_Common
	                    + fChartStyle_Font
	                    + fChartStyle_MSLine_nobg
	                    + ">";
		            var categories = "<categories>";
		            var dataset = new Array(list.length);

		            // commLogMiniGadgetChart.mxml 참고
		            // 차트 데이터가 없고 동적 생성되어 차후 로직 구현 필요
//		            categories = "<categories><category label=' ' /></categories>";
//	                dataset = "<dataset seriesName=' '>0</dataset>";

	                categories += "<categories><category label='1' /></categories>";
	                dataset += "<dataset seriesName=' '>0</dataset>";					

	                if(list.length == 0) {
						categories = "<categories><category label=' ' /></categories>";
		                dataset = "<dataset seriesName=' '>0</dataset>";
	                }
	               
	                fcChartDataXml += categories + dataset + "</chart>";

                	fcChartType = "MSLine";
	                fcChartRender();

	                hide();
	            }
   	    );
	}
	
	window.onresize = fcChartRender;
    function fcChartRender() {
    	if($('#fcChartDiv').is(':visible')) {
    		fcChart = new FusionCharts({
        		id: 'myChartId',
    			type: fcChartType,
    			renderAt : 'fcChartDiv',
    			width : $('#fcChartDiv').width(),
    			height : '280',
    			dataSource : fcChartDataXml
    		}).render();
    		
/*     		fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/"+fcChartType+".swf", "myChartId", $('#fcChartDiv').width(), "280", "0", "0");
	        fcChart.setDataXML(fcChartDataXml);
	        fcChart.setTransparent("transparent");
	        fcChart.render("fcChartDiv"); */
    	}
    }

	</script>
</head>

<body>
	<div class="commlogMiniLayer">
		<select	id="chartType" style="width:120px;">
			<option value="SendReceive"><fmt:message key="aimir.send"/>/<fmt:message key="aimir.operator.receive"/></option>
			<option value="SVCType"><fmt:message key="aimir.datatype"/></option>
			<!--
			<option value="Location"><fmt:message key="aimir.location"/></option>
 			-->
		</select>
	</div>

	<div class="div-commlog gadget_body2 margin-t5px">
		<div id="fcChartDiv" align="left" >
		    The chart will appear within this DIV. This text will be replaced by the chart.
		</div>
		<!-- 
			<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="290px" id="commLogChartFlexEx">
				<param name="movie" value="${ctx}/flexapp/swf/commLogMiniGadgetChart.swf" />
				<param name='wmode' value='transparent' />
				<!--[if !IE]>-->
				<!--
				<object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/commLogMiniGadgetChart.swf" width="100%" height="290px" id="commLogChartFlexOt">
				<param name='wmode' value='transparent' />
				<!--<![endif]-->
				<!--[if !IE]>-->
				<!--
				</object>
				<!--<![endif]-->
			<!--
			</object>
		 -->
	</div>


</body>

</html>