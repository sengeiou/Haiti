<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<title>Meter Time Management MiniGadget</title>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">

<script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
<%@ include file="/gadget/system/preLoading.jsp"%>
<script type="text/javascript" >/*<![CDATA[*/
    var supplierId = "";

    var fcChartDataXml;
    var fcChart;

    var currTab = "";

    /**
     * 유저 세션 정보 가져오기
     */
    $.getJSON('${ctx}/common/getUserInfo.do',
            function(json) {
                if(json.supplierId != ""){
                    supplierId = json.supplierId;

                    updateMeterTimeDiff();
                }
            }
    );

    $(function(){

        $(function() { $('#_timeDiff')      .bind('click',function(event) {changeTab("timeDiff"); } ); });
        $(function() { $('#_timeSyncLog')   .bind('click',function(event) {changeTab("timeSyncLog"); } ); });

        $("#meterTime").tabs();

        // Select
        $("#method").selectbox();

    });


    function getMsgTimeDiff(){
        var fmtMessage = new Array();;

        fmtMessage[0] = "<fmt:message key="aimir.normal"/>";
        fmtMessage[1] = "1~12 <fmt:message key="aimir.hour"/>";
        fmtMessage[2] = "12~24 <fmt:message key="aimir.hour"/>";
        fmtMessage[3] = "24 <fmt:message key="aimir.hour"/>" + "<fmt:message key="aimir.over"/>";

        return fmtMessage;
    }


    function getConditionTimeDiff(){
        var condArray = new Array();

        condArray[0] = "";
        condArray[1] = "";
        condArray[2] = "";
        condArray[3] = "";

        condArray[4] = "";
        condArray[5] = "";
        condArray[6] = "";

        condArray[7] = "";
        condArray[8] = supplierId;

        return condArray;
    }

    function getMsgTimeSyncLogAuto(){
        var fmtMessage = new Array();;

        fmtMessage[0] = "<fmt:message key="aimir.normal"/>";
        fmtMessage[1] = "1~12 <fmt:message key="aimir.hour"/>";
        fmtMessage[2] = "12~24 <fmt:message key="aimir.hour"/>";
        fmtMessage[3] = "24<fmt:message key="aimir.hour"/>" + "<fmt:message key="aimir.over"/>";

        return fmtMessage;
    }

    function getMsgTimeSyncLogManual(){
        var fmtMessage = new Array();;

        fmtMessage[0] = "<fmt:message key="aimir.success"/>";
        fmtMessage[1] = "<fmt:message key="aimir.fail"/>";
        fmtMessage[2] = "<fmt:message key="aimir.invalidArgument"/>";
        fmtMessage[3] = "<fmt:message key="aimir.commFailure"/>";

        return fmtMessage;
    }


    function getConditionTimeSyncLog(){
        var condArray = new Array();

        condArray[0] = "";
        condArray[1] = "";
        condArray[2] = "";

        condArray[3] = $('#method').val();
        condArray[4] = "";

        condArray[5] = "";
        condArray[6] = "";
        condArray[7] = "";


        var now = new Date();
        var dt = now - 6*24*60*60*1000;
        var pre1Week = new Date(dt);

        // 오늘 날짜 -7일
        condArray[8] = pre1Week.getYear() + fncLPAD((pre1Week.getMonth()+1)) + fncLPAD(pre1Week.getDate());

        // 오늘 날짜
        condArray[9] = now.getYear() + fncLPAD((now.getMonth()+1)) + fncLPAD(now.getDate());
        condArray[10] = supplierId;

        return condArray;
    }

    function fncLPAD(num)
    {
        if(num<10)
            return '0'+num;
        else return ''+num;

    }
    
   // Tab / Combo 의 변경으로 조회대상 데이터 변경
    function changeData(selMiniTab){
    	miniTab = selMiniTab;

//    	flex.requestSend();
    }

    function changeTab(tabName) {
    	currTab = tabName;
        if(tabName == "timeDiff") {
        	updateMeterTimeDiff();
        } else if(tabName == "timeSyncLog") {
        	updateMeterTimeSyncLog();
        }
    }
	
	function updateMeterTimeDiff() {
		emergePre();
		$.getJSON('${ctx}/gadget/device/getMeterTimeTimeDiffChart.do'
		    	    ,{supplierId:supplierId}
				,function(json) {
	                 var chartData = json.chartData;
	                 fcChartDataXml = "<chart "
    				     + "showZeroPies='1' "
    				     + "showLabels='0' "
    				     + "showValues='1' "
    				     + "showLegend='1' "
    				     + "legendNumColumns='2' "
    				     + "showPercentInToolTip='0' "
    				     + "legendPosition='Bottom' "
    				     + "manageLabelOverflow='1' "
     				     + fChartStyle_Common
                    	 + fChartStyle_Font
                         + fChartStyle_Pie3D_nobg 
                         + ">";

				 var labels = "<set label='<fmt:message key='aimir.normal'/>' value='"+chartData.NORMAL+"' color='"+fChartColor_Step4[0]+"' />"
                	 	    + "<set label='<fmt:message key='aimir.bems.facilityMgmt.unknown'/>' value='"+chartData.DIFF_UNKNOWN+"' color='8833CB'/>"
						 	+ "<set label='1~12 <fmt:message key='aimir.hour'/>' value='"+chartData.DIFF_1+"' color='"+fChartColor_Step4[1]+"' />"
						 	+ "<set label='12~24 <fmt:message key='aimir.hour'/>' value='"+chartData.DIFF_12+"' color='"+fChartColor_Step4[2]+"' />"
						 	+ "<set label='24 <fmt:message key='aimir.hour'/> <fmt:message key='aimir.over'/>' value='"+chartData.DIFF_24+"' color='"+fChartColor_Step4[3]+"' />";
						 	
				 	 if(chartData.NORMAL == null && chartData.DIFF_1 == null && chartData.DIFF_12 == null && chartData.DIFF_24 == null) {
						 labels = "<set label='' value='1' color='E9E9E9' toolText='' />"
					 }
                	                 	 
                     fcChartDataXml += labels + "</chart>";
	  	             
	                 fcChartRender();
	
	                 hide();
	            }
		    );
	}

	function updateMeterTimeSyncLog() {
		emergePre();
		$.getJSON('${ctx}/gadget/device/getMeterTimeSyncLogChart.do'
		    	    ,{supplierId:supplierId,
		    	    	method:$('#method').val(),
		    	    	searchStartDate:"0",
		    	    	searchEndDate:"0"}
				,function(json) {
	                 var chartData = json.chartData;
	                 fcChartDataXml = "<chart "
	                    	+ "yAxisName='<fmt:message key="aimir.count"/>' "
							+ "chartLeftMargin='0' "
							+ "chartRightMargin='0' "
							+ "chartTopMargin='13' "
							+ "chartBottomMargin='0' "
	  	                    + "showValues='0' "
	 	                    + "showLabels='1' "
	 	                    + "showLegend='1' "
	 	                    + "legendNumColumns='2' "
	 	                    + "labelDisplay = 'WRAP' "
	 	                    + "labelStep='2' "
 	 	                    + fChartStyle_Common
	 	                    + fChartStyle_Font
	 	                    + fChartStyle_MSColumn3D_nobg 
	 	                    + ">";
	                 var categories = "<categories>";
	 				 var dataset1 = "";
	 				 var dataset2 = "";
	 				 var dataset3 = "";
	 				 var dataset4 = "";

 					 dataset1 = "<dataset seriesName='<fmt:message key='aimir.success'/>'>";
	 				 dataset2 = "<dataset seriesName='<fmt:message key='aimir.fail'/>'>";
	 				 dataset3 = "<dataset seriesName='<fmt:message key='aimir.invalidArgument'/>'>";
	 				 dataset4 = "<dataset seriesName='<fmt:message key='aimir.commFailure'/>'>";
	 				 

	 				 for(index in chartData) {
	 					categories += "<category label='"+chartData[index].xTag +"' />";

	 					dataset1 += "<set value='"+chartData[index].result1+"' />";
	 					dataset2 += "<set value='"+chartData[index].result2+"' />";
	 					dataset3 += "<set value='"+chartData[index].result3+"' />";
	 					dataset4 += "<set value='"+chartData[index].result4+"' />";
	 				 }

	 				categories += "</categories>";
					dataset1 += "</dataset>";
					dataset2 += "</dataset>";
					dataset3 += "</dataset>";
					dataset4 += "</dataset>";	 				                 	
                	                 	 
                     fcChartDataXml += categories + dataset1 + dataset2 + dataset3 + dataset4 + "</chart>";
	  	             
	                 fcChartRender();
	
	                 hide();
	            }
		    );
	}
	
	window.onresize = fcChartRender;
	function fcChartRender() {
		if($('#fcChartDiv').is(':visible')) {
			if(currTab == "" || currTab == "timeDiff") {
				fcChart = new FusionCharts({
        			type: 'pie3d',
        			renderAt : 'fcChartDiv',
        			width : $('#fcChartDiv').width(),
        			height : '240',
        			dataSource : fcChartDataXml
        		}).render();
				//fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "fcChartId", $('#fcChartDiv').width(), 240, "0", "0");
	        } else if(currTab == "timeSyncLog") {
	        	fcChart = new FusionCharts({
        			type: 'stackedcolumn3d',
        			renderAt : 'fcChartDiv',
        			width : $('#fcChartDiv').width(),
        			height : '240',
        			dataSource : fcChartDataXml
        		}).render();
	        	//fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3D.swf", "fcChartId", $('#fcChartDiv').width(), 240, "0", "0");    		
	        }
			//fcChart.setDataXML(fcChartDataXml);
			//fcChart.setTransparent("transparent");
			//fcChart.render("fcChartDiv");
		}
	}
    /*]]>*/
    </script>

</head>
<body>

    <div id="meterTime">
        <!-- 상단 Tab 설정 -->
        <ul>
            <li><a href="#timeDiff"     id="_timeDiff" >Meter Time Diff </a></li>
            <li><a href="#timeSyncLog"  id="_timeSyncLog">Meter Time Sync Log</a></li>
        </ul>

		<!--  MeterTimeDiff Tab  -->
        <div id="timeDiff">
			<div class="div-metertime gadget_body">
			</div>
        </div>

        <!--  MeterTimeSyncLog Tab  -->
        <div id="timeSyncLog">
			<div class="metertimeMiniLayer">
				<select id="method" style="width:130px" onChange="javascript:updateMeterTimeSyncLog();">
					<option value="Auto"  selected="selected">Auto</option>
					<option value="Manual">Manual</option>
				</select>
			</div>
        </div>
        
        <div id="fcChartDiv" class="floatnone side3px">
			The chart will appear within this DIV. This text will be replaced by the chart.
		</div>


    </div>


</body>
</html>