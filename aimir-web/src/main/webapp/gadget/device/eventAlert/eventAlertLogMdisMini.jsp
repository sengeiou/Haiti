<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%@ include file="/gadget/system/preLoading.jsp"%>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title></title>
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
	<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
	<script type="text/javascript" charset="utf-8">

	var supplierId = ${supplierId};
    //탭초기화
    var tabs = {hourly:0,period:0,monthlyPeriod:0,yearly:0};
    var tabNames = {};

    var fcChartLogTypeDataXml;
    var fcChartLogType;

    var fcChartLogMessageDataXml;
    var fcChartLogMessage;
    var messageId='';
    var flex;

	/*
    function AjaxCall(){

        var myId = "Trap";
        var myDestination = "topic://AiMiR.Event"; // or "channel://MY.NAME"

    	   var myHandler =
    		   {
    			    rcvMessage: function(message)
	               {
    		            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"received "+message);
    		            }
    	    };

    	   amq.addListener(myId,myDestination,myHandler.rcvMessage);
    	   amq.removeHandler(myId);

    	    }
	*/

		$(function(){
			$('#pane-eventAlertLog-tab').tabs();
			
			// setInterval("realTimeEvent()", 1000);
		});

		/*
		function getLogByType() {

			var conditions = getCondition();
			if(document["EventAlertLogTypeOt"] == null){
				window["EventAlertLogTypeEx"].getLogByType(conditions);
			} else {
				document["EventAlertLogTypeOt"].getLogByType(conditions);
			}
		}

		function getLogByMessage() {
			var conditions = getCondition();
			if(document["EventAlertLogMessageOt"] == null){
				window["EventAlertLogMessageEx"].getLogByMessage(conditions);
			} else {
				document["EventAlertLogMessageOt"].getLogByMessage(conditions);
			}
		}
		*/


		function getFmtMessage(){
	        var fmtMessage = new Array();;

	        fmtMessage[0] = "<fmt:message key='aimir.opentime'/>";
	        fmtMessage[1] = "<fmt:message key='aimir.message'/>";
	        fmtMessage[2] = "<fmt:message key='aimir.location'/>";

	        return fmtMessage;
	    }

	    function getCondition() {

		    var array = [];
		    array[0] = '${supplierId}';
		    array[1] = $('#searchStartDate').val();
		    array[2] = $('#searchEndDate').val();

		    return array;
	    }

		function send() {
			updateFChart();
			//getLogByType();
			//getLogByMessage();
			//getLogRealTime();
		};



		$(document).ready(function() {
			updateFChart();
            interval = ${interval} * 1000;
			startRealTimeEventAlertLog();
	    });

	    function updateFChart() {
	    	emergePre();
	    	
	   	    $.getJSON('${ctx}/gadget/device/eventAlert/getEventAlertLogForMini.do'
	   	    	    ,{supplierId:'${supplierId}', 
	   	    	    	searchStartDate:$('#searchStartDate').val(),
	   	    	    	searchEndDate:$('#searchEndDate').val()}
					,function(json) {
                         var logTypeList = json.LogType;

                         /* 11-05-13 kskim
                         chartTopMargin 5=>10 여백 설정 
                         "numberSuffix=' ' " 추가
                         */
                         fcChartLogTypeDataXml = "<chart "
                        	+ "chartLeftMargin='0' "
							+ "chartRightMargin='0' "
							+ "chartTopMargin='10' "		
							+ "chartBottomMargin='0' "
                       	   	+ "showValues='0' "
                       	   	+ "numberSuffix=' ' "
                            + fChartStyle_Common
                            + fChartStyle_Font
                            + fChartStyle_Column3D_nobg
                            + " YAxisName='Count' "
                            + ">";
                    	 var logTypeLabels = "";
                    	
                    	 if(logTypeList == null || logTypeList.length == 0) {
                        	
                    		 logTypeLabels += "<set value='0' color='E48701'/>";
                         } else {
                        	 for( index in logTypeList){
    	                         if(index != "indexOf") {
    	                        	 logTypeLabels	+= "<set label='"+logTypeList[index].type+"' value='"+logTypeList[index].value+"' color='E48701'/>"
    	                         }
                             }
                         }
                    	 
                         
                         fcChartLogTypeDataXml += logTypeLabels + "</chart>";

                         var logMessageList = json.LogMessage;
                         fcChartLogMessageDataXml = "<chart "
                        	 + "showValues='0' "
                             + "useRoundEdges='0' "
                             + "numberSuffix=' ' "
                             + fChartStyle_Common
                             + fChartStyle_Font
                             + fChartStyle_Column2D_nobg
                             + ">";
                    	 var logMessageLabels = "";
                    	 if(logMessageList == null || logMessageList.length == 0) {
                    		 logMessageLabels += "<set value='0' color='E48701'/>";
                         } else {
	                         for( index in logMessageList){
	   	                         if(index != "indexOf") {
	   	                        	logMessageLabels	+= "<set label='"+logMessageList[index].type+"' value='"+logMessageList[index].value+"' color='E48701'/>"
	   	                         }
	                         }
                         }
                         fcChartLogMessageDataXml += logMessageLabels + "</chart>";

                         fcChartRender();
	                }
	   	    );
	   	
	   		hide();
		}

	    window.onresize = fcChartRender;
	    function fcChartRender() {
			var width = 0;
			if($('#fcChartLogTypeDiv').width() > 0 ) width = $('#fcChartLogTypeDiv').width();
			else width = $('#fcChartLogMessageDiv').width();

	    	fcChartLogType = new FusionCharts("${ctx}/flexapp/swf/fcChart/Column3D.swf", "fcChartLogTypeId", width, "150", "0", "0");
	    	fcChartLogType.setDataXML(fcChartLogTypeDataXml);
	    	fcChartLogType.setTransparent("transparent");
	    	fcChartLogType.render("fcChartLogTypeDiv");

	    	fcChartLogMessage = new FusionCharts("${ctx}/flexapp/swf/fcChart/Bar2D.swf", "fcChartLogMessageId", width, "150", "0", "0");
	    	fcChartLogMessage.setDataXML(fcChartLogMessageDataXml);
	    	fcChartLogMessage.setTransparent("transparent");
	    	fcChartLogMessage.render("fcChartLogMessageDiv");

	    }
	    function realTimeEvent(){
	        $.getJSON('${ctx}/gadget/device/eventAlert/getRealTimeEventAlertLog.do' , {supplierId:'${supplierId}',messageId:messageId} ,
	        	function( json ){
	        	    var m = json.message;
	        	    messageId =json.messageId;
	        	    if(m && m !=""){
	        	    	flex = getFlexObject('EventAlertLogRealTime');
		        	    flex.setLogRealTime(json.message.msgOpenTime,json.message.eventMessage,json.message.location);
	        	    }
	        		//addRow(json.message.msgOpenTime,json.message.eventMessage,json.message.location);
	            });
	    }
	    function addRow(openTime,message,location){
			   var rows =$('#eventTable tr');
			   var index=rows.length;
			   
			   $('#eventTable').append("<tr id='evt"+index+"'>"+"<td>"+openTime+"</td><td>"+message+"</td><td>"+location+"</td></tr>");
		}

        var timerId;
        var interval;
        function startRealTimeEventAlertLog() {
            window.setTimeout(getRealTimeEventAlertLog, 1000);  // flex loading 이후 실행.
            timerId = window.setInterval(getRealTimeEventAlertLog, interval);
        }

        //var startDateTime = "";
        function getRealTimeEventAlertLog() {
            $.getJSON('${ctx}/gadget/device/eventAlert/getRealTimeEventAlertLogFromDB.do', 
                {supplierId : supplierId,
                 interval : interval},
                function(json) {
                    var result = json.logList;
                    var len = result.length;

                    if (len > 0) {
                    	//startDateTime = result[0].openTimeValue;

                        if (getFlexObject('EventAlertLogRealTime').appendRealTimeData) {
                            for (var i = (len-1) ; i >= 0 ; i--) {
                                getFlexObject('EventAlertLogRealTime').appendRealTimeData(result[i]);
                            }
                        }
                    }
                });
        }

	</script>
</head>
<body>

	<!-- Tab Div(S) -->
	<div id="pane-eventAlertLog-tab">
		<ul>
			<li><a href="#pane-eventAlertLog-type"    id="_type"><fmt:message key='aimir.by.equip'/></a></li>
			<li><a href="#pane-eventAlertLog-message" id="_message"><fmt:message key='aimir.by.event.message'/></a></li>
		</ul>


		<!-- search-background DIV (S) -->
<!--		<div class="search-bg-withouttabs with-dayoptions-bt padding-reset">-->
			<div class="dayoptions-bt margin-b5px">
			<%@ include file="/gadget/commonDateTabButtonType.jsp"%>
			</div>
 
			<div>
				<table class="searchoption wfree">
					<tr>
						<td><div id="btn"><ul><li><a href="javascript:send();" class="on"><fmt:message key="aimir.button.search" /></a></li></ul></div></td>
					</tr>
				</table>
			</div>
			
			<div class="dashedline"></div>
<!--		</div>-->
		<!-- search-background DIV (E) -->


		<!--<div class="clear-width100 floatleft height7px"></div>-->
        <div id="pane-eventAlertLog-type" class="padding3px">
        	<div id="fcChartLogTypeDiv">
			    The chart will appear within this DIV. This text will be replaced by the chart.
			</div>
			<!--
			<div class="gadget_body">				 
				<object id="EventAlertLogTypeEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="140px">
					<param name="movie" value="${ctx}/flexapp/swf/EventAlertLogMiniType.swf">
					<param name="wmode" value="opaque" />
					<object id="EventAlertLogTypeOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/EventAlertLogMiniType.swf" width="100%" height="140px">
						<p>Alternative content</p>
					</object>
				</object>				 
			</div>
			-->
        </div>
        <div id="pane-eventAlertLog-message" class="padding3px">
        	<div id="fcChartLogMessageDiv">
			    The chart will appear within this DIV. This text will be replaced by the chart.
			</div>
			<!-- 
			<div class="gadget_body">
				<object id="EventAlertLogMessageEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="140px">
					<param name="movie" value="${ctx}/flexapp/swf/EventAlertLogMiniMessage.swf">
					<param name="wmode" value="opaque" />
					<object id="EventAlertLogMessageOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/EventAlertLogMiniMessage.swf" width="100%" height="140px">
						<p>Alternative content</p>
					</object>
				</object>
				 
			</div>
			-->
        </div>

	</div>
	<!-- Tab Div(E) -->


    <div id="pane-eventAlertLog-realtime" style="padding-top:245px;">
        <div class="gadget_body">
            <label class="check"><fmt:message key="aimir.real.time"/><!-- Suspected Substation List --></label>
       <!--  
        <table id="eventTable" width="100%" class="table_grid">
			<tr>
			<th><fmt:message key='aimir.opentime'/></th>
			<th><fmt:message key='aimir.message'/></th>
			<th><fmt:message key='aimir.location'/></th>
			</tr>
	   </table>
	  

  <input type="hidden" id="evtValue" value="1"/>
        <iframe id="realEventFrame" src="http://187.1.20.28:8161/event/eventAlert.html" frameborder="0" width="100%" height="300" marginwidth="0" marginheight="0" scrolling="yes"></iframe>
            
             --> 
            <object id="EventAlertLogRealTimeEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="157px">
                <param name="movie" value="${ctx}/flexapp/swf/EventAlertLogMiniRealTimeFromDBMdis.swf">
                <param name="wmode" value="transparent" />
                <object id="EventAlertLogRealTimeOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/EventAlertLogMiniRealTimeFromDBMdis.swf" width="100%" height="157px">
                    <param name="wmode" value="transparent" />
                    <p>Alternative content</p>
                </object>
            </object>
            
        </div>
    </div>

</body>
</html>




<!-- <script type="text/javascript" charset="utf-8" src="${ctx}/js/amq/amq.js"></script>
    <script type="text/javascript">amq.uri='${ctx}/amq';</script> -->
