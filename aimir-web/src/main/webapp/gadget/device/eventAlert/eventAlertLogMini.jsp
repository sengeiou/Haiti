<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title></title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <!-- 스타일 추가 extjs css -->
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        /* Ext-Js Grid Header style 정의. */
        .x-grid3-hd-inner {
            text-align: center;
            font-weight: bold;
        }
        /* context menu 의 style 이 어긋나는 부분 수정 */
        .x-menu-list-item span, .x-menu-list
        -item a {
            float:none !important;
        }
        /* context menu 의 icon 공간을 삭제 */
        div.no-icon-menu a.x-menu-item {
            padding-left: 0 !important;
        }
        .x-menu-item-text {
            padding-left: 10px !important;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/stomp.js"></script>
    <script type="text/javascript" charset="utf-8">
	
    /* severity color 설정 변수*/
    var red = '#F31523'; //critical
	var orange = '#E76F09'; //major
	var yellow = '#E0A504'; //minor
	var green = '#0E9C0C'; //warning
	var blue = '#12ABBA'; //information
	var purple = '#5E32BB'; //normal
	
    var supplierId = ${sesSupplierId};
    //탭초기화
    var tabs = {hourly:0,period:0,monthlyPeriod:0,yearly:0};
    var tabNames = {};

    var fcChartLogTypeDataXml;
    var fcChartLogType;

    var fcChartLogMessageDataXml;
    var fcChartLogMessage;
    //var messageId = '';
    //var flex;
    //var queueLength = "";
    //var browserWidth = "";

    //인터벌 설정 (60초)
    //var interval = ${interval};
    var interval = 60000;
    // grid 에서 삭제할 데이터 인터벌
    var gridRemoveInterval = interval + 10000;
    // queue 에서 삭제 인터벌
    var removeInterval = 60000;

    // 수정권한
    var editAuth = "${editAuth}";

    var chromeColAdd = 0;
    // Chrome 최선버전에서 Ext-JS Grid 컬럼사이즈 오류 수정
    Ext.onReady(function() {
        Ext.QuickTips.init();
        var isIE9 = (navigator.userAgent.indexOf("Trident/5")>-1);

        if (!Ext.isIE && !isIE9 && !Ext.isGecko) {
            Ext.chromeVersion = Ext.isChrome ? parseInt(( /chrome\/(\d{2})/ ).exec(navigator.userAgent.toLowerCase())[1],10) : NaN;
            Ext.override(Ext.grid.ColumnModel, {
                getTotalWidth : function(includeHidden) {
                    if (!this.totalWidth) {
                        var boxsizeadj = (Ext.isChrome && Ext.chromeVersion > 18 ? 2 : 0);
                        this.totalWidth = 0;
                        for (var i = 0, len = this.config.length; i < len; i++) {
                            if (includeHidden || !this.isHidden(i)) {
                                this.totalWidth += (this.getColumnWidth(i) + boxsizeadj);
                            }
                        }
                    }
                    return this.totalWidth;
                }
            });
            chromeColAdd = 2;
        }
    });

    $(document).ready(function() {
        updateFChart();

        //EventAlertLogRealTimeMin  grid fetch func
        getEventAlertLogRealTimeMin();
		
        //인터벌 함수 설정.
        // TODO - TEST
        //setInterval("insertDataToStore()", interval);
        //setInterval("deleteQueueData()", removeInterval);
        
        //최초 실행 한번후에 주기적 동작
        insertStompDataToStore();
        runInsertStompDataToStore();
        keepalive();
    });

    //윈도우 리싸이즈시 gridchart size 재설정.
    $(window).resize(function() {
        //get grid chart  AlertLogRealTime
        getEventAlertLogRealTimeMin();
    });
    
  //KeepAlive
    var keepalive = function() {
        setInterval(function() {            	
        	for(j=0; j<client.length; j++){
        		if(client[j].ws.readyState == 1)
        			client[j].send('/topic/keepalive', {}, 'keep alive');        			
        		else
        			console.log("There is no connection to send the keepalive.");
        	}  
        } , 60000);
           
    }
    
    // stomp topic
    function runInsertStompDataToStore() {
    	setTimeout(function(){insertStompDataToStore();}, interval);
    }
    
    //stomp topic 
    /*
    * last message time : 같은 서버에 대한 websocket 연결이 2개 이상 중복되면서,
    * 					   하나의  메시지가 여러번 출력되는 상황을 방지하기 위함.
    * message_callback : message가 발생하면 이를 그리드 패널에 삽입함.
    */		        
    var last_time;
    var last_msg="";
    var lang="";
    var country="";
    var message_callback = function(message) {
    	if(last_time==undefined){
    		last_time= new Date().getTime()-200;
    	}        		
    	var current_time = new Date().getTime();        	
    	// call by message
    	if(message.body == last_msg && current_time-last_time<100) {
			// 동일한 메시지가 빠른 시간안에 다시 오면 패스			
    	}
    	else {
    		// message validation : 개행 제거 및 json object로 변환
    		var msgValue = message.body.replaceAll("\n","");        		        		
    		var jsonvalue = msgValue.replaceAll("\r","");    		
    		var	JSONdata = Ext.util.JSON.decode(jsonvalue);
    		if(JSONdata.openTime.length==14){
    			yyyy = JSONdata.openTime.substr(0,4);
        		mm = JSONdata.openTime.substr(4,2);
        		dd = JSONdata.openTime.substr(6,2);
        		hh = JSONdata.openTime.substr(8,2);
        		nn = JSONdata.openTime.substr(10,2);
        		ss = JSONdata.openTime.substr(12,2);        		
        		dTime = new Date(yyyy,mm,dd,hh,nn,ss);
        		dTime.setMonth(dTime.getMonth()-1);
        		JSONdata.openTime = dTime.toLocaleString(lang);
    		}
    		if(JSONdata.closeTime.length==14){
    			yyyy = JSONdata.closeTime.substr(0,4);
        		mm = JSONdata.closeTime.substr(4,2);
        		dd = JSONdata.closeTime.substr(6,2);
        		hh = JSONdata.closeTime.substr(8,2);
        		nn = JSONdata.closeTime.substr(10,2);
        		ss = JSONdata.closeTime.substr(12,2);        		
        		dcTime = new Date(yyyy,mm,dd,hh,nn,ss);
        		dcTime.setMonth(dcTime.getMonth()-1);
        		JSONdata.closeTime = dcTime.toLocaleString(lang);
    		}
    		var	jsonRecord = new Ext.data.Record(JSONdata);
     			        		        		            		
    		// 패널에서 스토어 추출하여insert
    		EventAlertLogRealTimeMinPanel.getStore().insert(0,jsonRecord);
    		
    		// 시간 검증
    		last_time = new Date().getTime(); 
    		last_msg = message.body;
    	}
    }
    
    //stomp topic
    var connect_callback = function() {
    	for(j=0; j<client.length; j++){
    		if(client[j].ws.readyState == 1)
    			client[j].subscribe("/topic/AiMiR.Event", message_callback, {id:"client-stomp-mini-"+j});
    			//client[j].subscribe("/topic/AiMiR.Event", message_callback, {id:(new Date()).getTime().toString()});
    		else
    			console.log("  - - - - - - - - - - >> > "+client[j].ws.url + ":::::" + client[j].ws.readyState);
    	}        	
    	
    }
    //stomp topic
    var error_callback = function(error) {
    	//alert ("Event Alert Log For Real Time : Error");        	
    	//console.log(error);
    	for(k=0; k<client.length; k++){
    		if(client[k].connected)
    			;
    		else
    			client[k].disconnect();
    	}        	
    }
    
    
    //stomp topic       
    var client=[];
    //var wsUrl[0] = "ws://187.1.10.58:61614/stomp";
    var wsUrl=[];
    function insertStompDataToStore() {
    	$.ajax({
    		type : "POST", 
    		data : {
                supplierId : supplierId,                
            },
    		dataType : "json",
    		url : "${ctx}/gadget/device/eventAlert/getEventAlertLogRealTimeForMax.do",
    		success : function(data, status) {
    			//console.log('insertStompDataToStore MINI');
    			if (data.lang >0){
    				lang = data.lang;
    				country = data.country;
    			}else{
    				lang = window.navigator.userLanguage || window.navigator.language;        				
    			}
    			
    			if (data.wslen > 0){
    				//wsUrl = data.wsurl;
    				//wsUrl = ["ws://187.1.30.62:61614", "ws://187.1.10.58:61614"];
    				for(i=0; i<data.wslen; i++){
    					var propUrl = data.wsurl[i];
    					if(propUrl.includes('localhost')){
    						wsUrl[i] = "ws://"+Ext.global.document.domain+":61614/";    						
    					}else
    						wsUrl[i] = propUrl;
    				}
    			}else{
    				wsUrl[0] = "ws://"+Ext.global.document.domain+":61614/";
    				data.wslen =1;
    			} 
    			for(i=0;i<data.wslen; i++){
    				if ( client[i]==null || !client[i].connected){
        				client[i] = Stomp.client(wsUrl[i]);
            			client[i].heartbeat.outgoing = 0;
            			client[i].heartbeat.incoming = 0;
        				client[i].connect('guest','guest', connect_callback, error_callback);
        			}
    			}        		
    		},
    		 error : function(request, status) {                     
                 console.log("event alarm log - ajax error");                     
             }
    	})
    	        	
    	//runInsertStompDataToStore();
    	
    }

    function runInsertDataToStore() {
        //setInterval(function(){insertDataToStore();}, interval);
      //setTimeout(function(){insertDataToStore();}, interval);
    }

    var startTime = "";
    // 큐에 데이타가 있는지를 검사해서 있으면 gridStore에 추가시켜주는 js func
    function insertDataToStore() {
        $.ajax({
            type : "POST",
            data : {
                supplierId : supplierId,
                interval : interval,
                startTime : startTime
            },
            dataType : "json",
            url : "${ctx}/gadget/device/eventAlert/getRealTimeEventAlertLogFromActiveMq.do",
            success : function(data, status) {
                if (data.startTime != null && data.startTime != "") {
                    startTime = data.startTime;
                }
                // 메시지 queue 에 데이타가 존재하면. grid store에 queue log adding
                if (data.queueLength > 0 ) {
                    var arrayList = data.logList;
                    var recordFrame = Ext.data.Record.create([
                        {name: 'openTime'},
                        {name: 'eventMessage'},
                        {name: 'location'},
                        {name: 'status'},
                        {name: 'eventLogId'},
                        {name: 'closeTimenf'},
                    ]);

                    var len = 0;
                    var dataMap = null;
                    var delRecord = null;
                    var eventLogId;
                    $.each(arrayList, function(i) {
                        dataMap = arrayList[i];
                        eventLogId = dataMap.eventLogId;

                        //추가 데이터 정의
                        var record = new recordFrame({
                                openTime : dataMap.openTime,
                                eventMessage : dataMap.eventMessage ,
                                location : dataMap.location,
                                status : dataMap.status,
                                eventLogId : eventLogId,
                                closeTimenf : dataMap.closeTimenf
                        });

                        len = EventAlertLogRealTimeMinStore.getCount();

                        if (len > 0) {
                            for (var i = 0; i < len; i++) {
                                delRecord = EventAlertLogRealTimeMinStore.getAt(i);
                                if (eventLogId == delRecord.data.eventLogId) {
                                    EventAlertLogRealTimeMinStore.remove(delRecord);
                                    break;
                                }
                            }
                        }

                        //store에 로그 데이타 insert
                        EventAlertLogRealTimeMinStore.insert(0, record);
                    });
                    getEventAlertLogRealTimeMin();
                }

                // Cleared/Cleared Manually 데이터 삭제:주석처리
                //removeClearedData();
                runInsertDataToStore();
            },
            error:function(request, status) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',status);
                runInsertDataToStore();
            }
        });
    }

    /* function deleteQueueData() {
        if (startTime == null || startTime == "") {
            console.log("startTime is null");    
            return;
        }
        $.ajax({
            type : "POST",
            data : {
                supplierId : supplierId,
                interval : interval,
                startTime : startTime
            },
            dataType : "json",
            url : "${ctx}/gadget/device/eventAlert/deleteRealTimeEventAlertLogFromActiveMq.do",
            success : function(data, status) {
                if (data != null) {
                    console.log("result mini:", data.result);
                }
            },
            error : function(request, status) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',status);
            }
        });
    } */

    /* function getGridCount() {
        Ext.Msg.alert('<fmt:message key='aimir.message'/>',EventAlertLogRealTimeMinStore.getCount());
    } */

    var gridWidth = 0;
    //########### real time log grid min gadget fetch js func start #######
    var dummyArray = new Array();
    var EventAlertLogRealTimeMinModel;
    var EventAlertLogRealTimeMinPanel;
    var EventAlertLogRealTimeMinStore = null;
    var EventAlertLogRealTimeMinInstanceOn = false;

    //EventAlertLogRealTimeMin  grid fetch func
    function getEventAlertLogRealTimeMin() {
        var panelHeight = 170;
        var width = $("#pane-eventAlertLog-realtime").width();

        if (width <= 0) {
            width = gridWidth;
        } else {
            gridWidth = width;
        }

        if (EventAlertLogRealTimeMinInstanceOn == false) {
            EventAlertLogRealTimeMinStore = new Ext.data.JsonStore({
                autoLoad : true,
                data : dummyArray,
                fields : [
                          "openTime"
                         ,"eventMessage"
                         ,"location"
                         ,"status"
                         ,"eventLogId"
                         ,"closeTimenf"
                ],
                listeners : {
                	add : function(store, rec, ind){
                		var count = store.getCount();
                		if(count > 10){
                			store.removeAt(count-1);
                		}
                	}
                }
            });
        }

        var fmtMessage = getFmtMessage();
        var colWidth = (width/3) - chromeColAdd;        

        // EventAlertLogRealTimeMin Model define.
        EventAlertLogRealTimeMinModel = new Ext.grid.ColumnModel({
            columns: [
                 {header: fmtMessage[0], dataIndex: 'openTime',     tooltip: fmtMessage[0]}
                ,{header: fmtMessage[1], dataIndex: 'eventMessage', tooltip: fmtMessage[1]}
                ,{header: fmtMessage[2], dataIndex: 'location',     tooltip: fmtMessage[2]}
            ],
            defaults: {
                 sortable : true
                ,menuDisabled : true
                ,width : colWidth
                ,align : "left"
                ,renderer : addTooltip
            }
        });

        if (EventAlertLogRealTimeMinInstanceOn == false) {
            //grid panel 인스턴스 생성및 정의.
            EventAlertLogRealTimeMinPanel = new Ext.grid.GridPanel({
                store : EventAlertLogRealTimeMinStore,
                colModel : EventAlertLogRealTimeMinModel,
                sm : new Ext.grid.RowSelectionModel({singleSelect:true}),
                autoScroll : true,
                width : width,
                height : panelHeight,
                stripeRows : true,
                columnLines : true,
                renderTo : 'pane-eventAlertLog-realtime',
                viewConfig : {
                    forceFit : true,
                    enableRowBody : true,
                    showPreview : true,
                    //emptyText: 'No data to display'
                },
                listeners : {
                    rowcontextmenu : function(grid, index, event) {
                        if (editAuth == "true") {
                            if (index != null) {
                                this.getSelectionModel().selectRow(index);
                            }

                            showRealtimeGridMenu(grid, index, event);
                        }
                    },                
                }
            });
            EventAlertLogRealTimeMinInstanceOn = true;
        } else {
            EventAlertLogRealTimeMinPanel.setWidth(width);
            EventAlertLogRealTimeMinPanel.reconfigure(EventAlertLogRealTimeMinStore, EventAlertLogRealTimeMinModel);
        }

        hide();
    };//func EventAlertLogRealTimeMinList End

    // Event Alert Log Realtime Grid Context Menu
    function showRealtimeGridMenu(grid, index, event) {
        event.stopEvent();
        var record = grid.getStore().getAt(index);

        var menu = new Ext.menu.Menu({
            showSeparator: false,
            cls: "no-icon-menu",
            items: [
                <c:forEach var="eventStatus" items="${eventStatusList}">
                    {
                        text: "${eventStatus}",
                        handler: function() {
                            Ext.Msg.confirm("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.event.message.wouldchangeeventstatus"/>", function(btn) {
                                if (btn == "yes") {
                                    var params = {
                                        eventLogId : record.data.eventLogId,
                                        eventStatusName : "${eventStatus}"
                                    };

                                    $.post("${ctx}/gadget/device/eventAlert/changeEventAlertStatus.do",
                                           params,
                                           function(json) {
                                               if (json != null && json.result == "success") {
                                                   Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save"/>",
                                                           function() {
                                                       EventAlertLogRealTimeMinStore.remove(record);
                                                   });
                                               } else {
                                                   Ext.Msg.alert("<fmt:message key="aimir.error"/>", "<fmt:message key="aimir.save.error"/>");
                                               }

                                               return;
                                           }
                                    );
                                }
                            });

                        }
                    }<c:if test="${not status.last}">,</c:if>
                </c:forEach>
            ]
        });

        menu.showAt(event.xy);
    }

    // 실시간 Grid 에서 Cleared/Cleared Manually 인 데이터 삭제
    function removeClearedData() {
        var len = EventAlertLogRealTimeMinStore.getCount();

        if (len > 0) {
            var delRecord = null;
            var closeTime = null;
            var date = null;
            var rdate = null;
            var cdate = null;

            for (var i = 0; i < len; i++) {
                delRecord = EventAlertLogRealTimeMinStore.getAt(i);
                status = delRecord.data.status;
                
                if (status == "Cleared" || status == "ClearedManually") {
                    closeTime = delRecord.data.closeTimenf;
                    date = new Date(closeTime.substring(0, 4), (closeTime.substring(4, 6)-1), closeTime.substring(6, 8),
                                    closeTime.substring(8, 10), closeTime.substring(10, 12), closeTime.substring(12, 14));

                    rdate = new Date();
                    rdate.setTime(date.getTime() + gridRemoveInterval);

                    cdate = new Date();

                    if (cdate > rdate) {
                        EventAlertLogRealTimeMinStore.remove(delRecord);
                        i--;
                        len--;
                    }
                }
            }
        }
    }

    $(function(){
        $('#pane-eventAlertLog-tab').tabs();
    });

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
    };

    function updateFChart() {
        emergePre();

        $.getJSON('${ctx}/gadget/device/eventAlert/getEventAlertLogForMini.do'
                ,{supplierId:'${supplierId}',
                    searchStartDate:$('#searchStartDate').val(),
                    searchEndDate:$('#searchEndDate').val()}
                ,function(json) {
                    var minlogTypeList = 0;
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
                        + "yaxismaxvalue='5' "
                        + fChartStyle_Common
                        + fChartStyle_Font
                        + fChartStyle_Column3D_nobg
                        + " YAxisName='<fmt:message key='aimir.count'/>' ";

                    var logTypeLabels = "";

                    if (logTypeList == null || logTypeList.length == 0) {
                        logTypeLabels += "<set value='0' color='E48701'/>";
                    } else {
                        for (index in logTypeList) {
                            if (index != "remove") {
                                logTypeLabels  += "<set label='"+logTypeList[index].type+"' value='"+logTypeList[index].value+"' color='E48701'/>";
                                if (logTypeList[index].value > minlogTypeList) {
                                    minlogTypeList = logTypeList[index].value;
                                }
                            }
                        }
                    }
                    if (minlogTypeList > 0) {
                        fcChartLogTypeDataXml += ">" +logTypeLabels + "</chart>";
                    } else {
                        fcChartLogTypeDataXml = "<chart "
                            + "chartLeftMargin='0' "
                            + "chartRightMargin='0' "
                            + "chartTopMargin='10' "
                            + "chartBottomMargin='0' "
                            + "showValues='1' "
                            + "numberSuffix=' ' "
                            + " YAxisName='<fmt:message key='aimir.count'/>' ";
                        fcChartLogTypeDataXml += " showZeroPlaneValue='1' setAdaptiveYMin='0' showDivLineValues='0'  yAxisMinValue='0' yAxisMaxValue='1' >" +logTypeLabels + "</chart>";
                    }
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
                    if (logMessageList == null || logMessageList.length == 0) {
                        logMessageLabels += "<set value='0' color='E48701'/>";
                    } else {
                    	var color= 'E48701'; //defualt
                        for (index in logMessageList) {
                            if (index != "remove") {
                                //severity에 따라 chart label색상 변경 
                                switch(logMessageList[index].severity){
                                	case "Critical" : color = red; break;
                                	case "Major" : color = orange; break;
                                	case "Minor" : color = yellow; break;
                                	case "Warning" : color = green; break;
                                	case "Information" : color = blue; break;
                                	case "Normal" : color = purple; break;
                                }  
                                logMessageLabels    += "<set label='"+logMessageList[index].type+"' value='"+logMessageList[index].value+ "' color='"+color+"'/>";
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
        if ($('#fcChartLogTypeDiv').width() > 0) width = $('#fcChartLogTypeDiv').width();
        else width = $('#fcChartLogMessageDiv').width();

        fcChartLogType = new FusionCharts({
    		id: 'fcChartLogTypeId',
			type: 'Column3D',
			renderAt : 'fcChartLogTypeDiv',
			width : width,
			height : '150',
			dataSource : fcChartLogTypeDataXml
		}).render();
        
        fcChartLogMessage = new FusionCharts({
    		id: 'fcChartLogMessageId',
			type: 'Bar2D',
			renderAt : 'fcChartLogMessageDiv',
			width : width,
			height : '150',
			dataSource : fcChartLogMessageDataXml
		}).render();
        
        /* fcChartLogType = new FusionCharts("${ctx}/flexapp/swf/fcChart/Column3D.swf", "fcChartLogTypeId", width, "150", "0", "0");
        fcChartLogType.setDataXML(fcChartLogTypeDataXml);
        fcChartLogType.setTransparent("transparent");
        fcChartLogType.render("fcChartLogTypeDiv");

        fcChartLogMessage = new FusionCharts("${ctx}/flexapp/swf/fcChart/Bar2D.swf", "fcChartLogMessageId", width, "150", "0", "0");
        fcChartLogMessage.setDataXML(fcChartLogMessageDataXml);
        fcChartLogMessage.setTransparent("transparent");
        fcChartLogMessage.render("fcChartLogMessageDiv"); */

    }

    // grid column tooltip
    function addTooltip(value, metadata) {
        if (value != null && value != "" && metadata != null) {
            metadata.attr = 'ext:qtip="' + value + '"';
        }
        return value;
    }
    </script>
</head>
<body>
<input type="hidden" id="queueLength" value="0">

    <!-- Tab Div(S) -->
    <div id="pane-eventAlertLog-tab">
        <ul>
            <li><a href="#pane-eventAlertLog-type"    id="_type"><fmt:message key='aimir.by.equip'/></a></li>
            <li><a href="#pane-eventAlertLog-message" id="_message"><fmt:message key='aimir.by.event.message'/></a></li>
        </ul>

        <!-- search-background DIV (S) -->
            <div class="search-bg-withtabs">
                <div class="dayoptions">
                    <%@ include file="/gadget/commonDateTab.jsp" %>
                </div>
            </div>
            <div class="dashedline"></div>
        <!-- search-background DIV (E) -->


        <!--<div class="clear-width100 floatleft height7px"></div>-->
        <div id="pane-eventAlertLog-type" class="padding3px">
            <div id="fcChartLogTypeDiv">
                The chart will appear within this DIV. This text will be replaced by the chart.
            </div>
        </div>
        <div id="pane-eventAlertLog-message" class="padding3px">
            <div id="fcChartLogMessageDiv">
                The chart will appear within this DIV. This text will be replaced by the chart.
            </div>
        </div>

    </div>
    <!-- Tab Div(E) -->


    <!-- EventAlertLogMiniRealTimeFromActiveMq.swf삭제  -->
    <!-- eXtjs grid div -->
    <div class="gadget_body" id="gadget_body">

        <div id="pane-eventAlertLog-realtime" >
            <label class="check"><fmt:message key="aimir.real.time"/></label>
        </div>
    </div>

</body>
</html>