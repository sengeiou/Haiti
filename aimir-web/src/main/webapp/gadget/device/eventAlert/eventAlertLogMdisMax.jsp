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
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
	<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
	<!--
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/amq/amq.js"></script>
    <script type="text/javascript">amq.uri='${ctx}/amq';</script>
    <script type="text/javascript" src="${ctx}/js/amq/amq_jquery_adapter.js"></script>
    <script type="text/javascript" src="${ctx}/js/amq/amq.js"></script>
    -->
	<script type="text/javascript" charset="utf-8">

	var supplierId = ${supplierId};

	var fcChartLogTypeDataXml;
    var fcChartLogType;

    var fcChartLogMessageDataXml;
    var fcChartLogMessage;
    var messageId='';
    var flex;
    
	/**
	 * 유저 세션 정보 가져오기
	 */
	/*$.getJSON('${ctx}/common/getUserInfo.do',
	        function(json) {
	            if(json.supplierId != ""){
	                supplierId = json.supplierId;
	            }
	        }
	);*/
	
	    //탭초기화
	    var tabs = {hourly:0,period:0,monthlyPeriod:0,yearly:0};
	    var tabNames = {};

		var currentTab;

		function changeTab(type) {
			var realTimeTab = document.getElementsByName('realTimeTab');
			var historyTab = document.getElementsByName('historyTab');
			//var settingTab = document.getElementsByName('settingTab');

			if (type == "realTime") {
				realTimeTab[0].id = "current";
				historyTab[0].id = "";
				//settingTab[0].id = "";
				getRealTime();
			} else if (type == "history") {
				realTimeTab[0].id = "";
				historyTab[0].id = "current";
				//settingTab[0].id = "";
				getHistory();
				changeHistoryTab('log');
			} 

			/*else if (type == "setting") {
				realTimeTab[0].id = "";
				historyTab[0].id = "";
				settingTab[0].id = "current";
				getSetting();
			}
			*/
		}

		function changeHistoryTab(type) {
//			var logTab = document.getElementsByName('logTab');
//			var typeTab = document.getElementsByName('typeTab');
//			var msgTab = document.getElementsByName('msgTab');

			if (type == "log") {
//				logTab[0].id = "current";
//				typeTab[0].id = "";
//				msgTab[0].id = "";

				document.getElementById("pane-history-log").style.display = "block";
				document.getElementById("pane-history-type").style.display = "none";
				document.getElementById("pane-history-msg").style.display = "none";
			} else if (type == "type") {
//				logTab[0].id = "";
//				typeTab[0].id = "current";
//				msgTab[0].id = "";

				document.getElementById("pane-history-log").style.display = "none";
				document.getElementById("pane-history-type").style.display = "block";
				document.getElementById("pane-history-msg").style.display = "none";
				updateFChart();
			} else if (type == "msg") {
//				logTab[0].id = "";
//				typeTab[0].id = "";
//				msgTab[0].id = "current";

				document.getElementById("pane-history-log").style.display = "none";
				document.getElementById("pane-history-type").style.display = "none";
				document.getElementById("pane-history-msg").style.display = "block";
				updateFChart();
			}
		}

		function init() {
			$.getJSON('${ctx}/gadget/device/eventAlert/eventAlertLogInit.do',
				function(json) {
					var target;
					var profile = null;

					if (json.profile != null) {
						$.each(json.profile, function(index, data){
							profile = data;
						});
					}

					target = document.getElementById("eventAlertType");
					target.options.length = 0;
					target.options.add(new Option("<fmt:message key='aimir.all'/>", "all"), 0);
					target.selectedIndex = 0;
					$.each(json.eventAlertType, function(index, type) {
						if (type['name'] == "Event") {
							target.options.add(new Option("<fmt:message key='aimir.event'/>", type['name']), index+1);
						} else if (type['name'] == "Alert") {
							target.options.add(new Option("<fmt:message key='aimir.alert'/>", type['name']), index+1);
						}

						if (profile != null && profile['type'] == type['name']) {
							target.selectedIndex = index + 1;
						}
					});

					target = document.getElementById("severity");
					target.options.length = 0;
					target.options.add(new Option("<fmt:message key='aimir.all'/>", "all"), 0);
					$.each(json.severity, function(index, severity) {
						target.options.add(new Option(severity['name'], severity['name']), index+1);

						if (profile != null && profile['severity'] == severity['name']) {
							target.selectedIndex = index + 1;
						}
					});

					target = document.getElementById("eventAlertClass");
					target.options.length = 0;
					target.options.add(new Option("<fmt:message key='aimir.all'/>", "all"), 0);
					$.each(json.eventAlertClass, function(index, eventAlert) {
						target.options.add(new Option(eventAlert['name'], eventAlert['id']), index+1);

						if (profile != null && profile['eventAlert'] == eventAlert['name']) {
							target.selectedIndex = index + 1;
						}
					});
					target = document.getElementById("status");
					target.options.length = 0;
					target.options.add(new Option("<fmt:message key='aimir.all'/>", "all"), 0);
					$.each(json.status, function(index, status) {
						target.options.add(new Option(status['name'], status['name']), index+1);

						if (profile != null && profile['status'] == status['name']) {
							target.selectedIndex = index + 1;
						}
					});

					target = document.getElementById("activatorType");
					target.options.length = 0;
					target.options.add(new Option("<fmt:message key='aimir.all'/>", "all"), 0);
					$.each(json.activatorType, function(index, activatorType) {
						target.options.add(new Option(activatorType['name'], activatorType['name']), index+1);

						if (profile != null && profile['activatorType'] == activatorType['name']) {
							target.selectedIndex = index + 1;
						}
					});

					target = document.getElementById("activatorId");
					if (profile != null && profile['activatorId'] != null) {
						target.value = profile['activatorId'];
					}

					/*
					target = document.getElementsByName("sound");
					var radioLength = target.length;
						if (profile != null && profile['sound'] == true) {
							for (var i = 0; i < radioLength; i++) {
								if (target[i].value == 'true') {
									target[i].checked = true;
								}
							}
						} else if (profile['sound'] == false) {
							for (var i = 0; i < radioLength; i++) {
								if (target[i].value == 'false') {
									target[i].checked = true;
								}
							}
					}
						*/

						/*
					target = document.getElementsByName("popup");
					var radioLength = target.length;
						if (profile != null && profile['popup'] == true) {
							for (var i = 0; i < radioLength; i++) {
								if (target[i].value == 'true') {
									target[i].checked = true;
								}
							}
						} else if (profile['popup'] == false) {
							for (var i = 0; i < radioLength; i++) {
								if (target[i].value == 'false') {
									target[i].checked = true;
								}
							}
					}

					target = document.getElementsByName("popupCnt");
					if (profile != null && profile['popupCnt'] != null) {
						target[0].value = profile['popupCnt'];
					}
					*/

					$("#eventAlertType").selectbox();
					$("#severity").selectbox();
					$("#eventAlertClass").selectbox();
					$("#status").selectbox();
					$("#activatorType").selectbox();
                    //$("#openTime").selectbox();
				});
			locationTreeGoGo('treeeDiv', 'searchWord', 'location');
		}

		function getEventAlert() {
			var target = document.getElementById("eventAlertType");
			var eventAlertType = target.options[target.selectedIndex].value;

			target = document.getElementById("eventAlertClass");
			target.options.length = 0;

			$.getJSON('${ctx}/gadget/device/eventAlert/getEventAlertClass.do', {eventAlertType: eventAlertType},
				function(json) {
					target.options.add(new Option("<fmt:message key='aimir.all'/>", "all"), 0);
					$.each(json.eventAlertClass, function(index, eventAlert) {
						target.options.add(new Option(eventAlert['name'], eventAlert['id']), index+1);
					});
				});
		}

		function resetData() {
			document.getElementById("eventAlertType").selectedIndex = 0;
			document.getElementById("severity").selectedIndex = 0;
			document.getElementById("eventAlertClass").selectedIndex = 0;
			document.getElementById("status").selectedIndex = 0;
			document.getElementById("activatorType").selectedIndex = 0;
			document.getElementById("activatorId").value = "";
//			document.getElementById("location").selectedIndex = 0;
			document.getElementById("message").value = "";
		}

		function searchRealTime() {
			getFlexObject('EventAlertLogRealTime').searchData("${userId}", "${supplierId}");
		}

		function searchHistory() {
			var condition = getCondition();

			updateFChart();
			
			getFlexObject('EventAlertLogHistory').searchData(condition);			
		}

		function saveExcel(){
			getFlexObject('EventAlertLogHistory').saveExcel();
		}

		/*
		function getLogByMessage() {
			if(document["EventAlertLogHistoryMessageOt"] == null){
				window["EventAlertLogHistoryMessageEx"].getLogByMessage(getCondition1());
			} else {
				document["EventAlertLogHistoryMessageOt"].getLogByMessage(getCondition1());
			}
		}
		

		function getLogByType() {
			if(document["EventAlertLogHistoryTypeOt"] == null){
				//window["EventAlertLogHistoryTypeEx"].getLogByType("${supplierId}");
				window["EventAlertLogHistoryTypeEx"].getLogByType(getCondition1());
			} else {
				//document["EventAlertLogHistoryTypeOt"].getLogByType("${supplierId}");
				document["EventAlertLogHistoryTypeOt"].getLogByType(getCondition1());
			}
		}
		*/

		function getCondition() {
			var condition = new Array();

			var conditionCnt = 0;
			var supplierId = "${supplierId}";
			
			if (supplierId != null) {
				condition[conditionCnt] = "supplier:" + supplierId;
				conditionCnt++;
			}

			condition[conditionCnt++] = "location:" + $('#location').val();
			condition[conditionCnt++] = "startDate:" + $('#searchStartDate').val();
			condition[conditionCnt++] = "endDate:" + $('#searchEndDate').val();
			
			if (document.getElementById("eventAlertType").value != 'all') {
				condition[conditionCnt] = "eventAlertType:" + document.getElementById("eventAlertType").value;
				conditionCnt++;
			}
			
			if (document.getElementById("severity").value != 'all') {
				condition[conditionCnt] = "severity:" + document.getElementById("severity").value;
				conditionCnt++;
			}

			if (document.getElementById("eventAlertClass").value != 'all') {
				condition[conditionCnt] = "eventAlertClass:" + document.getElementById("eventAlertClass").value;
				conditionCnt++;
			}

			if (document.getElementById("status").value != 'all') {
				condition[conditionCnt] = "status:" + document.getElementById("status").value;
				conditionCnt++;
			}

			if (document.getElementById("activatorType").value != 'all') {
				condition[conditionCnt] = "activatorType:" + document.getElementById("activatorType").value;
				conditionCnt++;
			}

			if (document.getElementById("activatorId").value != '') {
				condition[conditionCnt++] = "activatorId:" + document.getElementById("activatorId").value;
			}

			if (document.getElementById("message").value != '') {
				condition[conditionCnt++] = "message:" + document.getElementById("message").value;
			}

			

			return condition;
		}

		function saveProfile(tab) {
			currentTab = tab;

			var options = {
	          	success : saveProfileResult,
	           	url : '${ctx}/gadget/system/profile/addProfile.do',
	           	type : 'post',
	           	datatype : 'json'
	        };
		    $('#profileAddForm').ajaxSubmit(options);
		}

		function saveProfileResult(responseText, status) {
		    if (responseText.result == "success") {
				Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.success'/>");
				if (currentTab == "realTime") {
					searchRealTime();
				}
			} else {
				Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.fail'/>");
			}
		    if (responseText.errors && responseText.errors.errorCount > 0) {
			    var i, fieldErrors = responseText.errors.fieldErrors;
			    for (i=0 ; i < fieldErrors.length; i++) {
				    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
			    	$(temp).val(''+fieldErrors[i].defaultMessage);
			    }
		    }
		}

		function getRealTime() {
			//document.getElementById("pane-setting").style.display = "none";
			document.getElementById("searchoption-container").style.display = "none";
			document.getElementById("pane-condition").style.display = "block";
			//document.getElementById("pane-condition-realtime").style.display = "block";
			//document.getElementById("pane-condition-history").style.display = "none";
            document.getElementById("pane-datecondition-history").style.display = "none";

			document.getElementById("pane-data-realtime").style.display = "block";
			document.getElementById("pane-data-history").style.display = "none";

			init();
		}

		function getHistory() {
			//document.getElementById("pane-setting").style.display = "none";
            document.getElementById("searchoption-container").style.display = "block";
			document.getElementById("pane-condition").style.display = "block";
			//document.getElementById("pane-condition-realtime").style.display = "none";
			//document.getElementById("pane-condition-history").style.display = "block";
            document.getElementById("pane-datecondition-history").style.display = "block";

			document.getElementById("pane-data-realtime").style.display = "none";
			document.getElementById("pane-data-history").style.display = "block";

			resetData();
			document.getElementById("logTab").checked = true;
			changeHistoryTab("log");
		}


		/*
		function getSetting() {
			document.getElementById("pane-setting").style.display = "block";
			document.getElementById("pane-condition").style.display = "none";

			document.getElementById("pane-data-realtime").style.display = "none";
			document.getElementById("pane-data-history").style.display = "none";
		}
		*/

		function getFmtMessage(){
	        var fmtMessage = new Array();;

	        fmtMessage[0] = "<fmt:message key='aimir.message'/>";
	        fmtMessage[1] = "<fmt:message key='aimir.location'/>";
	        fmtMessage[2] = "<fmt:message key='aimir.activatorId'/>";
	        fmtMessage[3] = "<fmt:message key='aimir.activatorType'/>";
	        fmtMessage[4] = "<fmt:message key='aimir.equipip'/>";
	        fmtMessage[5] = "<fmt:message key='aimir.status'/>";
			fmtMessage[6] = "<fmt:message key='aimir.writetime'/>";
	        fmtMessage[7] = "<fmt:message key='aimir.opentime'/>";
	        fmtMessage[8] = "<fmt:message key='aimir.closetime'/>";
	        fmtMessage[9] = "<fmt:message key='aimir.duration'/>";
			fmtMessage[10] = "<fmt:message key='aimir.history'/>";
			fmtMessage[11] = "<fmt:message key='aimir.severity'/>";
			fmtMessage[12] = "<fmt:message key="aimir.firmware.msg09"/>";
			
	        return fmtMessage;
	    }

	    function getCondition1() {

		    var array = [];
		    array[0] = '${supplierId}';
		    array[1] = $('#searchStartDate').val();
		    array[2] = $('#searchEndDate').val();

		    return array;
	    }

	    $(document).ready(function() {
	    	getRealTime();
			updateFChart();			
			//setInterval("realTimeEvent()", 1000);
			interval = ${interval} * 1000;
			startRealTimeEventAlertLog();
            hide();
	    });

	    function updateFChart() {
	    	emergePre();
	    	
	   	    $.getJSON('${ctx}/gadget/device/eventAlert/getEventAlertLog.do'
	   	    	    ,{supplierId:'${supplierId}', 
	   	    	    	searchStartDate:$('#searchStartDate').val(),
	   	    	    	searchEndDate:$('#searchEndDate').val()}
					,function(json) {
                         var logTypeList = json.LogType;
                         fcChartLogTypeDataXml = "<chart "
                        	+ "chartLeftMargin='10' "
							+ "chartRightMargin='10' "
							+ "chartTopMargin='10' "
							+ "chartBottomMargin='10' "
                       	 	+ "showValues='0' "
                       	 	+ "numberSuffix=' ' "
                            + fChartStyle_Common
                            + fChartStyle_Font
                            + fChartStyle_Column3D_nobg
                            + ">";
                    	 var logTypeLabels = "";
                         for( index in logTypeList){
	                         if(index != "indexOf") {
	                        	 logTypeLabels	+= "<set label='"+logTypeList[index].type+"' value='"+logTypeList[index].value+"' color='E48701'/>"
	                         }
                         }
						 if(logTypeList == null || logTypeList.length == 0) {
							 logTypeLabels	+= "<set label='' value='0' color='E48701'/>"
						 }
                         
                         fcChartLogTypeDataXml += logTypeLabels + "</chart>";

                         var logMessageList = json.LogMessage;
                         fcChartLogMessageDataXml = "<chart "
                        	+ "chartLeftMargin='10' "
 							+ "chartRightMargin='10' "
 							+ "chartTopMargin='10' "
 							+ "chartBottomMargin='10' "
                       	 	+ "showValues='0' "
                            + "useRoundEdges='0' "
                            + "numberSuffix=' ' "
                            + fChartStyle_Common
                            + fChartStyle_Font
                            + fChartStyle_Column2D_nobg
                            + ">";
                    	 var logMessageLabels = "";
                         for( index in logMessageList){
   	                         if(index != "indexOf") {
   	                        	logMessageLabels	+= "<set label='"+logMessageList[index].type+"' value='"+logMessageList[index].value+"' color='E48701'/>"
   	                         }
                         }
                         if(logMessageList == null || logMessageList == 0) {
                        	 logMessageLabels	+= "<set label='' value='0' color='E48701'/>"
						 }
                         fcChartLogMessageDataXml += logMessageLabels + "</chart>";

                         fcChartRender();

                         hide();
	                }
	   	    );	
		}

	    window.onresize = fcChartRender;
	    function fcChartRender() {
			var width = 0;
			if($('#fcChartLogTypeDiv').width() > 0 ) width = $('#fcChartLogTypeDiv').width();
			else width = $('#fcChartLogMessageDiv').width();

			if($('#fcChartLogTypeDiv').is(':visible')) {
		    	fcChartLogType = new FusionCharts("${ctx}/flexapp/swf/fcChart/Column3D.swf", "fcChartLogTypeId", width, "300", "0", "0");
		    	fcChartLogType.setDataXML(fcChartLogTypeDataXml);
		    	fcChartLogType.setTransparent("transparent");
		    	fcChartLogType.render("fcChartLogTypeDiv");
			}

	    	if($('#fcChartLogMessageDiv').is(':visible')) {
		    	fcChartLogMessage = new FusionCharts("${ctx}/flexapp/swf/fcChart/Bar2D.swf", "fcChartLogMessageId", width, "300", "0", "0");
		    	fcChartLogMessage.setDataXML(fcChartLogMessageDataXml);
		    	fcChartLogMessage.setTransparent("transparent");
		    	fcChartLogMessage.render("fcChartLogMessageDiv");
	    	}

	    }

        // 사용 안함. EventAlertLogRealTimeFromActiveMq 플렉스에서 주기적으로 데이타를 가져오도록 되어 잇음.
//	    function realTimeEvent(){
//
//	        $.getJSON('${ctx}/gadget/device/eventAlert/getRealTimeEventAlertLog.do' , {supplierId:'${supplierId}',messageId:messageId} ,
//	        	function( json ){
//	        	    var m = json.message;
//	        	    messageId =json.messageId;
//	        	    if(m && m !="")
//	        		addRow(json.message);
//	            });
//	    }
//	    function addRow(message){
//			   var rows =$('#eventTable tr');
//			   var index=rows.length;
//			   flex = getFlexObject('EventAlertLogRealTime');
//
//
//			   flex.setLogRealTime(message.eventMessage,message.location,message.activatorId,message.activatorType,message.activatorIp,message.status,message.msgWriteTime,message.msgOpenTime,message.msgCloseTime,message.duration);
//			   //$('#eventTable').append("<tr id='evt"+index+"'>"+"<td>"+message.eventMessage+"</td><td>"+message.location+"</td><td>"+message.activatorId+"</td><td>"+message.activatorType+"</td><td>"+message.activatorIp+"</td><td>"+message.status+"</td><td>"+message.msgWriteTime+"</td><td>"+message.msgOpenTime+"</td><td>"+message.msgCloseTime+"</td><td>"+message.duration+"</td></tr>");
//		}

/*
        var eventDestination = "topic://AiMiR.Event";
        //var myId = (new Date()).getTime().toString();
        var clientId = 'realtimeEventAlert';
        var amq = org.activemq.Amq;

        amq.init({
            //uri: 'amq',
            uri: '${ctx}/amq',
            logging: true,
            timeout: 40
            //clientId: (new Date()).getTime().toString()
        });

        // get message
        var eventHandler =
        {
            rcvMessage: function(message)
            {
                //getFlexObject('EventAlertLogRealTime').appendRealTimeData(message.textContent);
                if (getFlexObject('EventAlertLogRealTime').appendRealTimeData) {
                    // IE : message.text, Chrome,FF : message.textContent
                    getFlexObject('EventAlertLogRealTime').appendRealTimeData((message.text) ? message.text : message.textContent);
                }
            }
        };

        // start amq listener
        amq.addListener(clientId, eventDestination, eventHandler.rcvMessage); 
*/
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

        //report window(Excel)
        var winEventAlertLog;
        function openExcelReport() {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();

            obj.condition 	= getCondition();
            obj.fmtMessage 	= getFmtMessage();
            
            if(winEventAlertLog)
                winEventAlertLog.close();
            winEventAlertLog = window.open("${ctx}/gadget/device/eventAlert/eventAlertLogExcelDownloadPopup.do", "EventAlertLogExcel", opts);
            winEventAlertLog.opener.obj = obj;
        }
	</script>
</head>
<!-- <body onLoad="getRealTime();">  -->
<body>

<!--상단탭-->
<div id="gad_sub_tab">
	<ul>
		<li><a href="javascript:changeTab('realTime')" name="realTimeTab" id="current"><fmt:message key='aimir.real.time'/></a></li>
		<li><a href="javascript:changeTab('history')" name="historyTab"><fmt:message key='aimir.history'/></a></li>
		<!--  <li><a href="javascript:changeTab('setting')" name="settingTab"><fmt:message key='aimir.setting'/></a></li>-->
	</ul>
</div>
<!--상단탭 끝-->



	<!-- 3rd Tab - 설정 (S) -->
	<!-- 
	<div id="pane-setting" class="search-bg-withouttabs" style="display:none">
		<form id="profileAddForm" modelAttribute="profile">
			<div class="searchoption-container">
				<table class="searchoption wfree">
					<tr><td class="bluebold12pt withinput"><fmt:message key='aimir.popup.setting'/></td>
						<td class="space20"></td>
						<td class="gray11pt withinput"><fmt:message key='aimir.popup.window'/></td>
						<td class="space20"></td>
						<td><input type="radio" name="popup" value="true" class="trans"></td>
						<td class="gray11pt withinput" style="width:20px;"><fmt:message key='aimir.yes'/></td>
						<td><input type="radio" name="popup" value="false" class="trans"></td>
						<td class="gray11pt withinput"><fmt:message key='aimir.no'/></td>
						<td></td>
					</tr>
					<tr><td></td>
						<td class="space20"></td>
						<td class="gray11pt withinput"><fmt:message key='aimir.popup.window.count'/></td>
						<td class="space20"></td>
						<td colspan="4"><input type="text" name="popupCnt" style="width:120px;"></td>
						<td>
							<div id="btn">
								<ul style="padding:0 1px !important;"><li><a href="javascript:saveProfile('setting')" class="on"><fmt:message key="aimir.button.apply"/></a></li></ul>
							</div>
						</td>
					</tr>
				</table>
			</div>
	</div>
	 -->
	<!-- 3rd Tab - 설정 (E) -->




	<!-- 1st, 2nd Tab - 실시간, 이력 (S) -->
	<div id="pane-condition" class="search-bg-withouttabs">

		<!-- <div id="pane-datecondition-history" style="display:none;"> -->
        <div id="pane-datecondition-history">
			<div class="dayoptions-bt">
				<%@ include file="/gadget/commonDateTabButtonType.jsp"%>
			</div>
			<div class="dashedline"></div>
		</div>

		<!-- searchoption -->
		
        <div id="searchoption-container" class="searchoption-container">
				<table class="searchoption wfree">
					<tr>
						<td class="withinput padding-r20px"><fmt:message key='aimir.alert'/>/<fmt:message key='aimir.event'/></td>
						<td class="padding-r20px"><select name="eventAlertType" id="eventAlertType" onchange="javascript:getEventAlert();"></select></td>
						<td class="withinput padding-r20px">[<fmt:message key='aimir.alert'/>]&nbsp;<fmt:message key='aimir.severity'/></td>
						<td class="padding-r20px"><select id="severity" name="severity"></select></td>
						<td class="withinput padding-r20px"><fmt:message key='aimir.alert'/>/<fmt:message key='aimir.eventclassname'/></td>
						<td colspan="3"><select id="eventAlertClass" name="eventAlert.id" style="width:370px;"></select></td>
					</tr>
					<tr>
						<td class="withinput padding-r20px"><fmt:message key='aimir.state'/></td>
						<td class="padding-r20px"><select id="status" name="status"></select></td>
						<td class="withinput padding-r20px"><fmt:message key='aimir.activatorType'/></td>
						<td class="padding-r20px"><select id="activatorType" name="activatorType"></select></td>
						<td class="withinput padding-r20px"><fmt:message key='aimir.activatorId'/></td>
						<td class="padding-r20px"><input type="text" id="activatorId" name="activatorId"></td>
						<td class="withinput padding-r20px"><fmt:message key='aimir.location'/></td>
						<td class="padding-r10px">
							<input name="searchWord" id='searchWord' style="width:140px" type="text" />
							<input type="hidden" id="location" value=""></input>
							
						</td>
						<%-- <td id="pane-condition-realtime">
							<em class="am_button"><a href="javascript:saveProfile('realTime')"  class="on"><!--<a href="javascript:searchRealTime()">--><fmt:message key='aimir.button.apply'/></a></em>
						</td> --%>
					</tr>
					<!-- <tr id="pane-condition-history"> -->
                    <tr>
						<td class="withinput padding-r20px"><fmt:message key='aimir.message'/></td>
						<td class="padding-r20px"><input type="text" id="message" name="message"/></td>
						<!--
						<td class="withinput"><fmt:message key='aimir.opentime'/></td>
						<td><select id="openTime" name="openTime"></select></td>
						<td class="space20"></td>
						  -->
						<!-- <td id="pane-condition-realtime" colspan="7"> -->
                        <td colspan="6">
							<em class="am_button"><a href="javascript:searchHistory()" class="on"><fmt:message key='aimir.button.search'/></a></em>
						</td>
					</tr>
				</table>
			<div>
			<div id="treeeDivOuter" class="tree-billing auto" style="display:none;">
					<div id="treeeDiv"></div>
			</div>
		</div>
	</div>
	<!-- searchoption -->



	</div>
	<!-- 1st, 2nd Tab - 실시간, 이력 (E) -->




	<!-- data grid -->
	<div id="pane-data-realtime" class="gadget_body">
		<div class="alert-group">
			<div class="alert">
				<ul>
					<li><button type="button" class="alert5"></button></li>
					<li class="margin-t3px"><fmt:message key='aimir.critical'/></li>
					<li><button type="button" class="alert4"></button></li>
					<li class="margin-t3px"><fmt:message key='aimir.major'/></li>
					<li><button type="button" class="alert3"></button></li>
					<li class="margin-t3px"><fmt:message key='aimir.minor'/></li>
					<li><button type="button" class="alert2"></button></li>
					<li class="margin-t3px"><fmt:message key='aimir.warning'/></li>
					<li><button type="button" class="alert1"></button></li>
					<li class="margin-t3px"><fmt:message key='aimir.info'/></li>
					<li><button type="button" class="alert0"></button></li>
					<li class="margin-t3px"><fmt:message key='aimir.normal'/></li>
				</ul>
			</div>
		</div>
		<div class="flexlist">
		<!--  
		<table id="eventTable" width="100%" class="table_grid">
		
			<tr>
				<th><fmt:message key='aimir.message'/></th>
				<th><fmt:message key='aimir.location'/></th>
				<th><fmt:message key='aimir.activatorId'/></th>
				<th><fmt:message key='aimir.activatorType'/></th>
				<th><fmt:message key='aimir.equipip'/></th>
				<th><fmt:message key='aimir.status'/></th>
				<th><fmt:message key='aimir.writetime'/></th>
				<th><fmt:message key='aimir.opentime'/></th>
				<th><fmt:message key='aimir.closetime'/></th>
				<th><fmt:message key='aimir.duration'/></th>
			</tr>
	   </table>
	   -->

			<object id="EventAlertLogRealTimeEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="364px">
				<param name="movie" value="${ctx}/flexapp/swf/EventAlertLogRealTimeFromDBMdis.swf">
				<param name='wmode' value='transparent' />

				
				<object id="EventAlertLogRealTimeOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/EventAlertLogRealTimeFromDBMdis.swf" width="100%" height="364px">
				<param name='wmode' value='transparent' />
				
				<p>Alternative content</p>
				
				</object>
				
			</object>
		
		</div>
	</div>

	<div id="pane-data-history" style="display:none" class="gadget_body">
		<div class="alert-group">
			<table>
				<tr>
				<td>
					<div>
						<span><input type="radio" name="historyType" id="logTab" onClick='changeHistoryTab("log");' class="trans" style="margin:-1px 0 0 0;"></span>
						<span><fmt:message key='aimir.history'/></span>
						<span><input type="radio" name="historyType" id="typeTab" onClick='changeHistoryTab("type");' class="trans" style="margin:-1px 0 0 0;"></span>
						<span><fmt:message key='aimir.by.equip'/></span>
						<span><input type="radio" name="historyType" id="msgTab" onClick='changeHistoryTab("msg");' class="trans" style="margin:-1px 0 0 0;"></span>
						<span><fmt:message key='aimir.by.event.message'/></span>
					</div>
				</td>
				<td>
					<div class="alert">
						
						<ul>
							<li><button type="button" class="alert5"></button></li>
							<li class="margin-t3px"><fmt:message key='aimir.critical'/></li>
							<li><button type="button" class="alert4"></button></li>
							<li class="margin-t3px"><fmt:message key='aimir.major'/></li>
							<li><button type="button" class="alert3"></button></li>
							<li class="margin-t3px"><fmt:message key='aimir.minor'/></li>
							<li><button type="button" class="alert2"></button></li>
							<li class="margin-t3px"><fmt:message key='aimir.warning'/></li>
							<li><button type="button" class="alert1"></button></li>
							<li class="margin-t3px"><fmt:message key='aimir.info'/></li>
							<li><button type="button" class="alert0"></button></li>
							<li class="margin-t3px"><fmt:message key='aimir.normal'/></li>
								
						</ul>
							
					</div>
				</td>
				<td>
					<em class="am_button" >
			   			<a href="javascript:openExcelReport();" id="btnExcel"><fmt:message key="aimir.button.excel" /></a>
					</em>
				</td>
				</tr>
			</table>
		</div>

		<div id="pane-history-log" style="display:none" class="flexlist">
			<object id="EventAlertLogHistoryEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="324px">
				<param name="movie" value="${ctx}/flexapp/swf/EventAlertLogHistory.swf">
				<param name='wmode' value='transparent' />
					<!--[if !IE]>-->
				<object id="EventAlertLogHistoryOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/EventAlertLogHistory.swf" width="100%" height="324px">
				<param name='wmode' value='transparent' />
				<!--<![endif]-->
				<p>Alternative content</p>
				<!--[if !IE]>-->
				</object>
				<!--<![endif]-->
			</object>
		</div>
		<div id="pane-history-type" style="display:none" class="flexlist">
			<div id="fcChartLogTypeDiv" >
			    The chart will appear within this DIV. This text will be replaced by the chart.
			</div>
			<!-- 
			<object id="EventAlertLogHistoryTypeEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="280">
				<param name="movie" value="${ctx}/flexapp/swf/EventAlertLogHistoryType.swf">
				<object id="EventAlertLogHistoryTypeOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/EventAlertLogHistoryType.swf" width="100%" height="280">
					<p>Alternative content</p>
				</object>
			</object>
			 -->
		</div>
		<div id="pane-history-msg" style="display:none" class="flexlist">
			<div id="fcChartLogMessageDiv">
			    The chart will appear within this DIV. This text will be replaced by the chart.
			</div>
			<!-- 
			<object id="EventAlertLogHistoryMessageEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="280">
				<param name="movie" value="${ctx}/flexapp/swf/EventAlertLogHistoryMessage.swf">
				<object id="EventAlertLogHistoryMessageOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/EventAlertLogHistoryMessage.swf" width="100%" height="280">
					<p>Alternative content</p>
				</object>
			</object>
			 -->
		</div>
	</div>

</body>
</html>
