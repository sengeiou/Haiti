<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="aimir.batteryvoltage"/></title>

	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <script type="text/javascript" >/*<![CDATA[*/

	    var flex;
        var supplierId = "";

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
                    updateFChart();
                }
        );

	    $(document).ready(function(){
//            flex = getFlexObject('batteryVoltageFlex');
            $('#modemType').selectbox(); 
	    });


        function send(){
//            flex.requestSend();
            updateFChart();
        };

        /**
         * fmt message 전달
         */
	    function getFmtMessage(){
	        var fmtMessage = new Array();

	        fmtMessage[0] = "<fmt:message key="aimir.normal"/>";
	        fmtMessage[1] = "<fmt:message key="aimir.abnormal"/>";
	        fmtMessage[2] = "<fmt:message key="aimir.replacement"/>";
            fmtMessage[3] = "<fmt:message key="aimir.unknown"/>";
	        fmtMessage[9] = "<fmt:message key="aimir.alert"/>";

	        return fmtMessage;
	    }

        /**
         * Condition 전달
         */
        function getCondition(){
            var condArray = new Array();
            condArray[0] = supplierId;
            condArray[1] = $('#modemType').val();
            condArray[2] = $('#modemType option:selected').text();

            return condArray;
        }


        function updateFChart() {
        	emergePre();
        	
       	    $.getJSON('${ctx}/gadget/device/getBatteryLog.do'
       	    	    ,{supplierId:supplierId
           	    	    , modemType:$('#modemType').val()
       	    	    	, modemTypeName:$('#modemType option:selected').text()}
    				,function(json) {
                         var result = json.result;
                         fcChartDataXml = "<chart "
                        	 + "showValues='1' "
        					 + "showPercentValues='1' "
        					 + "showPercentInToolTip='1' "
        					 + "pieRadius='75' "
        				     + "showZeroPies='1' "
        				     + "showLabels='1' "
        				     + "showLegend='1' "
        				     + "legendPosition='BOTTOM' "
        				     + "manageLabelOverflow='1' "
        				     + "legendNumColumns='1' "
        				     + "chartTopMargin='20' "
        				     + "chartLeftMargin='20' "
        				     + fChartStyle_Common
                        	 + fChartStyle_Font
                             + fChartStyle_Pie3D
                             + ">";
                    	 var labels = "";

                    	 if(result.total > 0) {
    	              	 	 labels += "<set label='<fmt:message key='aimir.normal'/>' value='"+result.normal+"' />"
    	                  	 	+ "<set label='<fmt:message key='aimir.abnormal'/>' value='"+result.abnormal+"' />"
    	                  	 	+ "<set label='<fmt:message key='aimir.replacement'/>' value='"+result.replacement+"' />"
    	                  	 	+ "<set label='<fmt:message key='aimir.unknown'/>' value='"+result.unknown+"' />";
                    	 } else {
                      		labels = "<set label='' value='1' color='E9E9E9' toolText='' />";
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
    	    	fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "myChartId", $('#fcChartDiv').width() , "250", "0", "0");
                fcChart.setDataXML(fcChartDataXml);
                fcChart.setTransparent("transparent");
                fcChart.render("fcChartDiv");
        	}
        }

	/*]]>*/
	</script>
</head>
<body>



    <div class="search-bg-basic">
		<ul class="basic-ul">
			<li class="basic-li gray11pt withinput"><fmt:message key="aimir.modem.type"/></li>
			<li class="basic-li space5"></li>
			<li class="basic-li">
				<select id="modemType" style="width:140px;" onChange="javascript:send();">
					<c:forEach var="combo" items="${combo}">
					<option value="${combo.id}">${combo.name}</option>
					</c:forEach>
				</select>
			</li>
		</ul>
	</div>

    <div id="gadget_body blue">
    	<div id="fcChartDiv">
		    The chart will appear within this DIV. This text will be replaced by the chart.
		</div>
	</div>


</body>
</html>