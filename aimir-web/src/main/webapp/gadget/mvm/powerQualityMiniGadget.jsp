<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="aimir.powerQuality"/></title>

	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
    <%@ include file="/gadget/system/preLoading.jsp"%>    
    <script type="text/javascript" >/*<![CDATA[*/

        var tabs = {period:0,hourly:0,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};
        var tabNames = {};

	    var flex;
	    var supplierId = ${supplierId};
	    var fcChartDataXml;
        var fcChart;

       // supplierId는 controller에서 설정하여 UI에 넘기므로
       // 아래와 supplierId값이 있는지 확인 한후 취득하도록 변경함
       if(supplierId == ""){
    	   $.getJSON('${ctx}/common/getUserInfo.do',
                   function(json) {
                       if(json.supplierId != ""){
                           supplierId = json.supplierId;
                       }
                   }
           );
       }
      

        $(document).ready(function(){
        	send();
        });

        function send(){
        	updateFChart();
        };
        
        function updateFChart() {
        	emergePre();
        	
        	//alert($('#searchStartDate').val());
        	//alert(    	$('#searchEndDate').val());
        	//alert($('#searchDateType').val());
//        	alert($('#deviation').val());
        	
        	
        	//날짜 초기값 설정
        	if ( $('#searchStartDate').val() =='' && $('#searchEndDate').val()=='' )
       		{
       			//alert("date is  null");
       			
        		var today= getToday();
        		
        		$('#searchStartDate').val(today);
    	    	$('#searchEndDate').val(today);
        		
       		}
        		
        	
        	
       	    $.getJSON('${ctx}/gadget/mvm/getPowerQuality.do'
       	    	    ,{supplierId:supplierId,
       	    	    	deviation:$('#deviation').val(),
       	    	    	fromDate:$('#searchStartDate').val(),
       	    	    	toDate:$('#searchEndDate').val(),
       	    	    	dateType:$('#searchDateType').val()}
    				,function(json) {
                         var list = json.result;
                         fcChartDataXml = "<chart "
                        	 + "showValues='1' "
        					 + "showPercentValues='1' "
        					 + "showPercentInToolTip='0' "
        				     + "showZeroPies='1' "
        					 + "pieRadius='90' "
        				     + "showLabels='0' "
        				     + "showLegend='1' "
        				     + "legendPosition='BOTTOM' "
        				     + "manageLabelOverflow='1' "
        				     + fChartStyle_Common
                        	 + fChartStyle_Font
                             + fChartStyle_Pie3D
                             + ">";
                    	 var labels = "";

                  	 	 labels += "<set label='<fmt:message key='aimir.voltageUnbalance'/>' value='"+list.abnormal1+"' color='"+fChartColor_Step4[1]+"' />"
                      	 	+ "<set label='<fmt:message key='aimir.reverseAngleUnbalance'/>' value='"+list.abnormal2+"' color='"+fChartColor_Step4[2]+"' />"
                      	 	+ "<set label='<fmt:message key='aimir.normal'/>' value='"+list.normal+"' color='"+fChartColor_Step4[0]+"' />";

                      	 if(list.abnormal1 == 0 && list.abnormal2 == 0 && list.normal == 0) {
                      		labels = "<set label='' value='1' color='E9E9E9' toolText='<fmt:message key='aimir.data.notexist'/>' />"
									+ "<set label='<fmt:message key='aimir.voltageUnbalance'/>' value='0' color='"+fChartColor_Step4[1]+"' />"
		                      	 	+ "<set label='<fmt:message key='aimir.reverseAngleUnbalance'/>' value='0' color='"+fChartColor_Step4[2]+"' />"
		                      	 	+ "<set label='<fmt:message key='aimir.normal'/>' value='0' color='"+fChartColor_Step4[0]+"' />"; 
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
        		fcChart = new FusionCharts({
            		id: 'myChartId',
        			type: 'Pie3D',
        			renderAt : 'fcChartDiv',
        			width : $('#fcChartDiv').width(),
        			height : '200',
        			dataSource : fcChartDataXml
        		}).render();
		    	/* fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "myChartId", $('#fcChartDiv').width() , "200", "0", "0");
	            fcChart.setDataXML(fcChartDataXml);
	            fcChart.setTransparent("transparent");
	            fcChart.render("fcChartDiv"); */
        	}
        }
        
        //오늘 날짜를 구한다.,
        function getToday()
        {
        	
        	var currentTime = new Date();
        	var month = (currentTime.getMonth() + 1);
        	var day = currentTime.getDate();//-8
        	var year = currentTime.getFullYear();
        	
        	//alert( day.toString().length );
        	if ( day.toString().length == 1)
			{
        		day = "0" + day.toString();
			}
        	
        	if ( month.toString().length == 1)
			{
        		month = "0" + month.toString();
			}
        	var today = year.toString() + month.toString() + day.toString();
        	
        	return today;
        }

	/*]]>*/
	</script>
</head>
<body>

<!-- search-background DIV (S) -->
<div class="search-bg-withtabs">

	<div class="dayoptions">
	<%@ include file="/gadget/commonDateTab.jsp"%>
	</div> 
	<div class="dashedline"><ul><li></li></ul></div>

	<div class="searchoption-container">
		<table class="searchoption wfree">
			<tr>
				<td class="gray11pt withinput"><fmt:message key="aimir.powerQuality.deviation"/></td>
				<td class="space5"></td>
				<td><input id="deviation" type="text" class="day textalign-center blubold" style="width:30px" value="5"></input></td>
				<td class="space5"></td>
				<td class="gray11pt withinput"><fmt:message key="aimir.powerQuality.over"/></td>
			</tr>
		</table>
	</div>

</div>

<div id="fcChartDiv">
    The chart will appear within this DIV. This text will be replaced by the chart.
</div>
<!-- search-background DIV (E) -->

</body>
</html>